package com.example.platform

import com.example.auth.AegoraAuthRepository
import com.example.auth.AuthorizationBoundary
import com.example.auth.AuthorizationCheckResult
import com.example.model.PredictiveNextAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Cross-platform session model allowing session handover between Android and Web clients.
 */
data class CrossPlatformSession(
  val sessionId: String,
  val identity: CanonicalAegoraIdentity,
  val activeClient: PlatformClient,
  val createdAt: Long,
  val lastHeartbeatAt: Long,
  val continuityToken: String
)

/**
 * Synchronized snapshot shared across Android and Web clients for the same canonical learner.
 */
data class PlatformContinuitySnapshot(
  val canonicalLearnerId: String,
  val targetRole: String,
  val overallReadinessPercent: Int,
  val primaryBottleneck: String?,
  val nextMove: PredictiveNextAction?,
  val activeMissionId: String?,
  val verifiedEvidenceCount: Int,
  val lastSynchronizedAt: Long
)

/**
 * Platform Continuity Manager:
 * Ensures cross-platform consistency between Android and Web clients.
 *
 * Enforces:
 * 1. Android Learner ID == Web Learner ID for identical provider UID.
 * 2. Neither client can submit requests with mismatched learner IDs.
 * 3. Client state can transition between Android and Web seamlessly.
 */
object CrossPlatformContinuityManager {

  private val activeSessions = mutableMapOf<String, CrossPlatformSession>()

  private val _continuitySnapshot = MutableStateFlow<PlatformContinuitySnapshot?>(null)
  val continuitySnapshot: StateFlow<PlatformContinuitySnapshot?> = _continuitySnapshot.asStateFlow()

  /**
   * Registers or updates an active session from either client type.
   */
  fun registerSession(
    canonicalIdentity: CanonicalAegoraIdentity,
    client: PlatformClient
  ): CrossPlatformSession {
    val existing = activeSessions.values.firstOrNull {
      it.identity.canonicalUserId == canonicalIdentity.canonicalUserId
    }

    val session = if (existing != null) {
      existing.copy(
        activeClient = client,
        lastHeartbeatAt = System.currentTimeMillis()
      )
    } else {
      CrossPlatformSession(
        sessionId = "sess_${UUID.randomUUID().toString().take(12)}",
        identity = canonicalIdentity,
        activeClient = client,
        createdAt = System.currentTimeMillis(),
        lastHeartbeatAt = System.currentTimeMillis(),
        continuityToken = "cont_${UUID.randomUUID().toString().replace("-", "").take(24)}"
      )
    }

    activeSessions[session.sessionId] = session
    return session
  }

  /**
   * Authoritatively validates that a request from [client] for [targetLearnerId] matches
   * the active session's canonical learner ID.
   */
  fun validateClientAccess(
    sessionId: String,
    targetLearnerId: String,
    client: PlatformClient
  ): CrossPlatformValidationResult {
    val session = activeSessions[sessionId]
      ?: return CrossPlatformValidationResult.SessionNotFound

    if (session.identity.canonicalLearnerId != targetLearnerId) {
      return CrossPlatformValidationResult.Forbidden(
        "Cross-platform isolation violation: Client $client attempted to access learner $targetLearnerId while bound to ${session.identity.canonicalLearnerId}"
      )
    }

    return CrossPlatformValidationResult.Authorized(session)
  }

  /**
   * Updates the synchronized snapshot for the current learner.
   */
  fun updateSnapshot(snapshot: PlatformContinuitySnapshot) {
    _continuitySnapshot.value = snapshot
  }

  /**
   * Clears sessions and snapshots on logout.
   */
  fun clearSessions() {
    activeSessions.clear()
    _continuitySnapshot.value = null
  }

  internal fun getActiveSessionCount(): Int = activeSessions.size
}

sealed class CrossPlatformValidationResult {
  data class Authorized(val session: CrossPlatformSession) : CrossPlatformValidationResult()
  object SessionNotFound : CrossPlatformValidationResult()
  data class Forbidden(val reason: String) : CrossPlatformValidationResult()
}
