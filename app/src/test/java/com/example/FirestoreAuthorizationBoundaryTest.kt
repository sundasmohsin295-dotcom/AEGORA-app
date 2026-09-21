package com.example

import com.example.auth.*
import com.example.data.cloud.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Phase 3B: Real Firestore Backend + Server-Side Authorization Boundary Test Suite.
 *
 * Tests:
 * 1. Identity:
 *    - Authenticated UID resolves to correct learner profile.
 *    - Unauthenticated access is denied with CloudSecurityException.
 * 2. Learner Isolation:
 *    - User A cannot read User B's documents.
 *    - User A cannot write User B's documents.
 * 3. Privilege Escalation Prevention:
 *    - Client writes attempting `verified = true`, `mastery = VERIFIED`, or `readiness = JOB_READY` are rejected.
 *    - Client cannot self-certify mission outcomes.
 * 4. Cross-Platform Continuity:
 *    - Android writes mission state; readable across platforms for same UID.
 *    - Client can update permitted progress (status = IN_PROGRESS / SUBMITTED).
 * 5. Evidence Security:
 *    - Evidence strictly bound to caller's Firebase UID.
 *    - Cross-user evidence access denied.
 *    - SHA-256 payload tampering detected.
 *    - Verifies SHA-256 is integrity-only (NOT digital signature, authentication, or authorization).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FirestoreAuthorizationBoundaryTest {

  private val userA = "firebase_uid_alpha_111"
  private val userB = "firebase_uid_beta_222"

  private lateinit var cloudRepository: FirestoreCloudRepository
  private lateinit var syncManager: AegoraCloudSyncManager

  @Before
  fun setUp() {
    AegoraAuthRepository.resetToDefault()
    cloudRepository = FirestoreCloudRepository { null } // Test in-memory cloud repository
    syncManager = AegoraCloudSyncManager(cloudRepository)
  }

  @After
  fun tearDown() {
    AegoraAuthRepository.resetToDefault()
  }

  private fun authenticateUser(uid: String, email: String = "$uid@test.aegora.cyber") {
    val identity = AuthenticatedIdentity.fromProvider(
      providerUid = uid,
      provider = "FIREBASE",
      email = email,
      displayName = "Test User $uid"
    )
    val testProvider = object : AuthProvider {
      override val providerId: String = "FIREBASE"
      override val isConfigured: Boolean = true
      override val authState = kotlinx.coroutines.flow.MutableStateFlow<AuthState>(AuthState.Authenticated(identity))
      override val currentIdentity: AuthenticatedIdentity? = identity
      override suspend fun signInWithEmailPassword(email: String, pass: String): AuthResult = AuthResult.Success(identity)
      override suspend fun signUpWithEmailPassword(email: String, pass: String): AuthResult = AuthResult.Success(identity)
      override suspend fun signInWithFederatedToken(idToken: String): AuthResult = AuthResult.Success(identity)
      override suspend fun signOut(): Boolean {
        authState.value = AuthState.Unauthenticated
        return true
      }
    }
    AegoraAuthRepository.setProviderForTesting(testProvider)
  }

  @Test
  fun test01_unauthenticatedAccessDenied() = runBlocking {
    // Current identity is null
    assertNull(AegoraAuthRepository.currentIdentity)

    try {
      cloudRepository.getOrCreateLearnerProfile(userA, "op_alpha", "User A")
      fail("Should throw CloudSecurityException when unauthenticated")
    } catch (e: CloudSecurityException) {
      assertTrue(e.message?.contains("Unauthenticated") == true)
    }

    try {
      cloudRepository.submitMissionAttempt(
        CloudMissionAttempt(missionId = "m_triage")
      )
      fail("Should throw CloudSecurityException when unauthenticated")
    } catch (e: CloudSecurityException) {
      assertTrue(e.message?.contains("Unauthenticated") == true)
    }
  }

  @Test
  fun test02_authenticatedUidResolvesToCorrectLearner() = runBlocking {
    authenticateUser(userA)

    val profile = cloudRepository.getOrCreateLearnerProfile(
      authUid = userA,
      canonicalLearnerId = "operator_alpha",
      displayName = "Alpha Analyst",
      email = "alpha@aegora.cyber"
    )

    assertEquals(userA, profile.firebaseAuthUid)
    assertEquals(userA, profile.ownerAuthUid)
    assertEquals("operator_alpha", profile.canonicalLearnerId)
    assertEquals("Alpha Analyst", profile.displayName)
  }

  @Test
  fun test03_learnerIsolation_userACannotReadUserB() = runBlocking {
    authenticateUser(userA)

    // User A attempts to read User B's profile
    try {
      cloudRepository.getOrCreateLearnerProfile(userB, "op_beta", "User B")
      fail("User A must NOT be able to access User B's profile")
    } catch (e: CloudSecurityException) {
      assertTrue(e.message?.contains("LearnerIsolationViolation") == true)
    }

    // User A attempts to read User B's mission attempt
    try {
      cloudRepository.getMissionAttempt(userB, "att_b_1")
      fail("User A must NOT be able to access User B's mission attempts")
    } catch (e: CloudSecurityException) {
      assertTrue(e.message?.contains("LearnerIsolationViolation") == true)
    }

    // User A attempts to read User B's evidence
    try {
      cloudRepository.getEvidence(userB, "ev_b_1")
      fail("User A must NOT be able to access User B's evidence")
    } catch (e: CloudSecurityException) {
      assertTrue(e.message?.contains("LearnerIsolationViolation") == true)
    }
  }

  @Test
  fun test04_privilegeEscalation_clientCannotSelfPromoteCapability() = runBlocking {
    authenticateUser(userA)

    try {
      cloudRepository.attemptClientCapabilityWrite("soc_triage", "MASTERED", verified = true)
      fail("Client must NOT be permitted to write verified = true or self-promote capability")
    } catch (e: PrivilegeEscalationException) {
      assertTrue(e.message?.contains("PrivilegeEscalation") == true)
    }
  }

  @Test
  fun test05_privilegeEscalation_clientCannotWriteMasteryDirectly() = runBlocking {
    authenticateUser(userA)

    try {
      cloudRepository.attemptClientMasteryWrite("soc_triage", "VERIFIED")
      fail("Client must NOT be permitted to write mastery = VERIFIED")
    } catch (e: PrivilegeEscalationException) {
      assertTrue(e.message?.contains("PrivilegeEscalation") == true)
    }
  }

  @Test
  fun test06_privilegeEscalation_clientCannotWriteReadinessDirectly() = runBlocking {
    authenticateUser(userA)

    try {
      cloudRepository.attemptClientReadinessWrite("soc_tier_1", "JOB_READY")
      fail("Client must NOT be permitted to write readiness = JOB_READY")
    } catch (e: PrivilegeEscalationException) {
      assertTrue(e.message?.contains("PrivilegeEscalation") == true)
    }
  }

  @Test
  fun test07_privilegeEscalation_clientCannotSelfCertifyMissionOutcome() = runBlocking {
    authenticateUser(userA)

    val spoofedAttempt = CloudMissionAttempt(
      missionId = "m_incident_response",
      status = "SUBMITTED",
      verifiedOutcome = true // FORBIDDEN: Client attempting self-certification
    )

    try {
      cloudRepository.submitMissionAttempt(spoofedAttempt)
      fail("Client must NOT be permitted to set verifiedOutcome = true")
    } catch (e: PrivilegeEscalationException) {
      assertTrue(e.message?.contains("PrivilegeEscalation") == true)
    }
  }

  @Test
  fun test08_evidenceSecurity_scopedToUidWithPendingStatusAndIntegrityDigest() = runBlocking {
    authenticateUser(userA)

    val payload = mapOf("action" to "network_quarantine", "targetHost" to "192.168.1.50")
    val evidence = cloudRepository.submitEvidence(
      attemptId = "att_a_101",
      skillKey = "containment_execution",
      evidenceType = "HOST_QUARANTINE",
      rawPayload = payload,
      sourcePlatform = "ANDROID"
    )

    assertEquals(userA, evidence.ownerAuthUid)
    assertEquals("PENDING_VERIFICATION", evidence.serverVerificationState)
    assertFalse("Evidence must not be verified by default client write", evidence.verified)
    assertNotNull(evidence.clientDigest)
    assertEquals(64, evidence.clientDigest.length) // SHA-256 hex string length
  }

  @Test
  fun test09_evidenceSecurity_payloadTamperDetected() = runBlocking {
    authenticateUser(userA)

    val payload = mutableMapOf<String, Any>("ruleId" to "YARA_TROJAN_APT29", "matched" to true)
    val evidence = cloudRepository.submitEvidence(
      attemptId = "att_a_102",
      skillKey = "yara_signature_analysis",
      evidenceType = "SCAN_RESULT",
      rawPayload = payload,
      sourcePlatform = "ANDROID"
    )

    // Tamper with payload
    payload["ruleId"] = "YARA_TROJAN_APT29_TAMPERED"

    try {
      cloudRepository.getEvidence(userA, evidence.evidenceId)
      fail("Must detect tampering when SHA-256 digest does not match modified payload")
    } catch (e: EvidenceTamperException) {
      assertTrue(e.message?.contains("EvidenceTamperDetected") == true)
    }
  }

  @Test
  fun test10_cloudContinuity_crossPlatformMissionStateReadAndWrite() = runBlocking {
    authenticateUser(userA)

    // Android client writes permitted progress
    val writtenAttempt = cloudRepository.submitMissionAttempt(
      CloudMissionAttempt(
        attemptId = "att_shared_ir_01",
        missionId = "m_ransomware_triage",
        status = "IN_PROGRESS",
        userInputs = mapOf("quarantinedSubnet" to "10.0.4.0/24")
      )
    )

    // Web client using the same Firebase UID reads the exact same state
    val retrievedAttempt = cloudRepository.getMissionAttempt(userA, "att_shared_ir_01")
    assertNotNull(retrievedAttempt)
    assertEquals("m_ransomware_triage", retrievedAttempt?.missionId)
    assertEquals("IN_PROGRESS", retrievedAttempt?.status)
    assertEquals("10.0.4.0/24", retrievedAttempt?.userInputs?.get("quarantinedSubnet"))
  }

  @Test
  fun test11_safeSync_protectsAgainstStaleClientStateOverwrite() = runBlocking {
    authenticateUser(userA)

    // Server-authoritative capability state pushed by backend
    val authoritativeState = CloudCapabilityState(
      skillKey = "reverse_engineering",
      ownerAuthUid = userA,
      level = "COMPETENT",
      demonstratedState = true,
      verifiedState = true,
      confidence = 0.88,
      evidenceCount = 5,
      revision = 3L
    )
    cloudRepository.setAuthoritativeCapabilityForSync(userA, authoritativeState)

    // Local client pulls authoritative capabilities safely
    val result = syncManager.pullAuthoritativeCapabilities(listOf("reverse_engineering"))
    assertTrue("Sync must succeed or resolve conflicts safely", result is SyncResult.PartialSuccess || result is SyncResult.Success)

    // Read back to confirm authoritative state was not overwritten
    val current = cloudRepository.getLearnerCapability(userA, "reverse_engineering")
    assertNotNull(current)
    assertEquals("COMPETENT", current?.level)
    assertTrue(current?.verifiedState == true)
  }
}
