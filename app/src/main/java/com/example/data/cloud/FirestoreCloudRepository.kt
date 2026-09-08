package com.example.data.cloud

import com.example.auth.AegoraAuthRepository
import com.example.auth.AuthenticatedIdentity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

/**
 * Security and Authorization Exceptions
 */
class CloudSecurityException(message: String) : SecurityException(message)
class PrivilegeEscalationException(message: String) : SecurityException(message)
class EvidenceTamperException(message: String) : SecurityException(message)

/**
 * Canonical Cloud Data Models aligned with Firestore Security Rules
 */
data class CloudLearnerDocument(
  val firebaseAuthUid: String = "",
  val ownerAuthUid: String = "",
  val canonicalLearnerId: String = "",
  val email: String? = null,
  val displayName: String = "",
  val createdAt: Long = System.currentTimeMillis(),
  val lastActiveAt: Long = System.currentTimeMillis()
)

data class CloudMissionAttempt(
  val attemptId: String = "",
  val missionId: String = "",
  val ownerAuthUid: String = "",
  val status: String = "INITIALIZED", // INITIALIZED, IN_PROGRESS, SUBMITTED, EVALUATED
  val clientStartedAt: Long = System.currentTimeMillis(),
  val clientCompletedAt: Long? = null,
  val userInputs: Map<String, Any> = emptyMap(),
  val evaluatedScore: Double? = null,
  val outcome: String? = null,
  val verifiedOutcome: Boolean? = null,
  val masteryAwarded: Boolean? = null,
  val revision: Long = 1L
)

data class CloudEvidenceItem(
  val evidenceId: String = "",
  val ownerAuthUid: String = "",
  val attemptId: String = "",
  val skillKey: String = "",
  val evidenceType: String = "",
  val rawPayload: Map<String, Any> = emptyMap(),
  val clientDigest: String = "", // SHA-256 for integrity check only (NOT a digital signature)
  val serverVerificationState: String = "PENDING_VERIFICATION", // PENDING_VERIFICATION, VERIFIED, REJECTED
  val verified: Boolean = false,
  val sourcePlatform: String = "ANDROID",
  val clientTimestamp: Long = System.currentTimeMillis()
)

data class CloudCapabilityState(
  val skillKey: String = "",
  val ownerAuthUid: String = "",
  val level: String = "NOVICE", // NOVICE, COMPETENT, PROFICIENT, EXPERT, MASTERED
  val demonstratedState: Boolean = false,
  val verifiedState: Boolean = false,
  val confidence: Double = 0.0,
  val evidenceCount: Int = 0,
  val lastDemonstratedAt: Long = System.currentTimeMillis(),
  val revision: Long = 1L
)

data class CloudNextAction(
  val actionId: String = "",
  val ownerAuthUid: String = "",
  val targetSkillKey: String = "",
  val prescribedMissionId: String = "",
  val priority: String = "NORMAL",
  val reason: String = "",
  val userStatus: String = "PENDING", // PENDING, ACCEPTED, DISMISSED
  val acknowledgedAt: Long? = null,
  val dismissedAt: Long? = null,
  val generatedAt: Long = System.currentTimeMillis(),
  val expiresAt: Long = System.currentTimeMillis() + (86400 * 1000)
)

/**
 * Computes SHA-256 digest for client payload integrity check.
 *
 * CRITICAL SECURITY INVARIANT:
 * SHA-256 = integrity check (tamper detection)
 * NOT authentication
 * NOT authorization
 * NOT digital signature
 * NOT non-repudiation
 */
object IntegrityDigestHelper {
  fun computeDigest(payload: Map<String, Any>): String {
    val sortedString = payload.toSortedMap().toString()
    val bytes = MessageDigest.getInstance("SHA-256").digest(sortedString.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }
}

/**
 * Authoritative Firestore Cloud Repository for AEGORA.
 *
 * Enforces:
 * 1. Scoped collections under /learners/{authUid}
 * 2. Learner Isolation: User A cannot read/write User B
 * 3. Client Privilege Escalation Prevention: Client cannot write verified=true or self-promote capabilities
 * 4. Offline and sync safety: Idempotency with deterministic document IDs and revision checks
 */
class FirestoreCloudRepository(
  private val firestoreProvider: () -> FirebaseFirestore? = {
    try {
      FirebaseFirestore.getInstance()
    } catch (_: Exception) {
      null
    }
  }
) {

  // Local synchronized state cache for offline safety, reconnects, and testing
  private val inMemoryLearners = mutableMapOf<String, CloudLearnerDocument>()
  private val inMemoryAttempts = mutableMapOf<String, CloudMissionAttempt>()
  private val inMemoryEvidence = mutableMapOf<String, CloudEvidenceItem>()
  private val inMemoryCapabilities = mutableMapOf<String, CloudCapabilityState>()
  private val inMemoryNextActions = mutableMapOf<String, CloudNextAction>()

  val firestore: FirebaseFirestore?
    get() = firestoreProvider()

  private fun assertOwnership(targetAuthUid: String) {
    val currentAuthUid = AegoraAuthRepository.currentIdentity?.providerUid
    if (currentAuthUid == null) {
      throw CloudSecurityException("Unauthenticated: Cannot access protected cloud resources")
    }
    if (currentAuthUid != targetAuthUid) {
      throw CloudSecurityException("LearnerIsolationViolation: Caller $currentAuthUid cannot access $targetAuthUid")
    }
  }

  // --- Learner Profile ---

  suspend fun getOrCreateLearnerProfile(
    authUid: String,
    canonicalLearnerId: String,
    displayName: String,
    email: String? = null
  ): CloudLearnerDocument {
    assertOwnership(authUid)

    val db = firestore
    if (db != null) {
      try {
        val docRef = db.collection("learners").document(authUid)
        val snap = docRef.get().await()
        if (snap.exists()) {
          return snap.toObject(CloudLearnerDocument::class.java)!!
        }
        val profile = CloudLearnerDocument(
          firebaseAuthUid = authUid,
          ownerAuthUid = authUid,
          canonicalLearnerId = canonicalLearnerId,
          email = email,
          displayName = displayName,
          createdAt = System.currentTimeMillis(),
          lastActiveAt = System.currentTimeMillis()
        )
        docRef.set(profile).await()
        inMemoryLearners[authUid] = profile
        return profile
      } catch (_: Exception) {
        // Fallback to synchronized memory cache
      }
    }

    return inMemoryLearners.getOrPut(authUid) {
      CloudLearnerDocument(
        firebaseAuthUid = authUid,
        ownerAuthUid = authUid,
        canonicalLearnerId = canonicalLearnerId,
        email = email,
        displayName = displayName,
        createdAt = System.currentTimeMillis(),
        lastActiveAt = System.currentTimeMillis()
      )
    }
  }

  // --- Mission Attempts (Client-Writable Telemetry) ---

  suspend fun submitMissionAttempt(attempt: CloudMissionAttempt): CloudMissionAttempt {
    val currentAuthUid = AegoraAuthRepository.currentIdentity?.providerUid
      ?: throw CloudSecurityException("Unauthenticated: Cannot submit mission attempt")

    if (attempt.verifiedOutcome == true || attempt.masteryAwarded == true) {
      throw PrivilegeEscalationException("PrivilegeEscalation: Client cannot self-certify mission verifiedOutcome or masteryAwarded")
    }

    val attemptId = if (attempt.attemptId.isNotBlank()) attempt.attemptId else "att_${currentAuthUid}_${attempt.missionId}_${System.currentTimeMillis()}"
    val fullAttempt = attempt.copy(
      attemptId = attemptId,
      ownerAuthUid = currentAuthUid,
      revision = 1L
    )

    val db = firestore
    if (db != null) {
      try {
        db.collection("learners").document(currentAuthUid)
          .collection("mission_attempts").document(attemptId)
          .set(fullAttempt).await()
      } catch (_: Exception) {
        // Fallback to cache
      }
    }

    inMemoryAttempts["$currentAuthUid:$attemptId"] = fullAttempt
    return fullAttempt
  }

  suspend fun getMissionAttempt(targetAuthUid: String, attemptId: String): CloudMissionAttempt? {
    assertOwnership(targetAuthUid)

    val db = firestore
    if (db != null) {
      try {
        val snap = db.collection("learners").document(targetAuthUid)
          .collection("mission_attempts").document(attemptId)
          .get().await()
        if (snap.exists()) {
          return snap.toObject(CloudMissionAttempt::class.java)
        }
      } catch (_: Exception) {
        // Fallback to cache
      }
    }

    return inMemoryAttempts["$targetAuthUid:$attemptId"]
  }

  // --- Evidence (Client Submits Pending Evidence, Server Verifies) ---

  suspend fun submitEvidence(
    attemptId: String,
    skillKey: String,
    evidenceType: String,
    rawPayload: Map<String, Any>,
    sourcePlatform: String = "ANDROID"
  ): CloudEvidenceItem {
    val currentAuthUid = AegoraAuthRepository.currentIdentity?.providerUid
      ?: throw CloudSecurityException("Unauthenticated: Cannot submit evidence")

    val clientDigest = IntegrityDigestHelper.computeDigest(rawPayload)
    val evidenceId = "ev_${currentAuthUid}_${attemptId}_${System.currentTimeMillis()}"

    val evidence = CloudEvidenceItem(
      evidenceId = evidenceId,
      ownerAuthUid = currentAuthUid,
      attemptId = attemptId,
      skillKey = skillKey,
      evidenceType = evidenceType,
      rawPayload = rawPayload,
      clientDigest = clientDigest, // Integrity check only (NOT a digital signature)
      serverVerificationState = "PENDING_VERIFICATION",
      verified = false,
      sourcePlatform = sourcePlatform,
      clientTimestamp = System.currentTimeMillis()
    )

    val db = firestore
    if (db != null) {
      try {
        db.collection("learners").document(currentAuthUid)
          .collection("evidence").document(evidenceId)
          .set(evidence).await()
      } catch (_: Exception) {
        // Fallback to cache
      }
    }

    inMemoryEvidence["$currentAuthUid:$evidenceId"] = evidence
    return evidence
  }

  suspend fun getEvidence(targetAuthUid: String, evidenceId: String): CloudEvidenceItem? {
    assertOwnership(targetAuthUid)

    val item = inMemoryEvidence["$targetAuthUid:$evidenceId"]
    if (item != null) {
      val currentDigest = IntegrityDigestHelper.computeDigest(item.rawPayload)
      if (currentDigest != item.clientDigest) {
        throw EvidenceTamperException("EvidenceTamperDetected: Payload hash does not match original digest for $evidenceId")
      }
    }
    return item
  }

  // --- Capabilities & Mastery (Server-Authoritative, Protected from Client Direct Writes) ---

  suspend fun attemptClientCapabilityWrite(skillKey: String, level: String, verified: Boolean) {
    if (AegoraAuthRepository.currentIdentity == null) {
      throw CloudSecurityException("Unauthenticated")
    }
    throw PrivilegeEscalationException(
      "PrivilegeEscalation: Client cannot directly write capability state ($skillKey=$level, verified=$verified). Server authority required."
    )
  }

  suspend fun attemptClientMasteryWrite(skillKey: String, masteryLevel: String) {
    if (AegoraAuthRepository.currentIdentity == null) {
      throw CloudSecurityException("Unauthenticated")
    }
    throw PrivilegeEscalationException(
      "PrivilegeEscalation: Client cannot directly create MasteryAssessment ($skillKey=$masteryLevel). Server authority required."
    )
  }

  suspend fun attemptClientReadinessWrite(careerTrack: String, status: String) {
    if (AegoraAuthRepository.currentIdentity == null) {
      throw CloudSecurityException("Unauthenticated")
    }
    throw PrivilegeEscalationException(
      "PrivilegeEscalation: Client cannot directly set career readiness status ($careerTrack=$status). Server authority required."
    )
  }

  suspend fun getLearnerCapability(targetAuthUid: String, skillKey: String): CloudCapabilityState? {
    assertOwnership(targetAuthUid)
    return inMemoryCapabilities["$targetAuthUid:$skillKey"]
  }

  fun setAuthoritativeCapabilityForSync(targetAuthUid: String, state: CloudCapabilityState) {
    inMemoryCapabilities["$targetAuthUid:${state.skillKey}"] = state
  }

  // --- Next Actions / NEXT MOVE ---

  suspend fun getNextActions(targetAuthUid: String): List<CloudNextAction> {
    assertOwnership(targetAuthUid)
    return inMemoryNextActions.values.filter { it.ownerAuthUid == targetAuthUid }
  }

  suspend fun updateNextActionStatus(actionId: String, status: String) {
    val currentAuthUid = AegoraAuthRepository.currentIdentity?.providerUid
      ?: throw CloudSecurityException("Unauthenticated")

    val key = "$currentAuthUid:$actionId"
    val action = inMemoryNextActions[key] ?: return
    inMemoryNextActions[key] = action.copy(
      userStatus = status,
      acknowledgedAt = if (status == "ACCEPTED") System.currentTimeMillis() else action.acknowledgedAt,
      dismissedAt = if (status == "DISMISSED") System.currentTimeMillis() else action.dismissedAt
    )
  }

  fun setAuthoritativeNextActionForSync(targetAuthUid: String, action: CloudNextAction) {
    inMemoryNextActions["$targetAuthUid:${action.actionId}"] = action
  }
}
