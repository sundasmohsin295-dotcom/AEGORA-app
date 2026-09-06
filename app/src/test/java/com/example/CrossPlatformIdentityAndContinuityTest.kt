package com.example

import com.example.auth.AegoraAuthRepository
import com.example.auth.AuthState
import com.example.auth.AuthenticatedIdentity
import com.example.model.CognitiveClusterType
import com.example.platform.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verification test suite for AEGORA Platform: Cross-Platform Identity, Continuity,
 * and Canonical Authority (Android + Web).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CrossPlatformIdentityAndContinuityTest {

  @Before
  fun setUp() {
    CrossPlatformContinuityManager.clearSessions()
    AegoraAuthRepository.resetToDefault()
  }

  @After
  fun tearDown() {
    CrossPlatformContinuityManager.clearSessions()
    AegoraAuthRepository.resetToDefault()
  }

  // =========================================================================
  // TEST 1: Identical Provider UID yields identical Canonical Identity across clients
  // =========================================================================
  @Test
  fun test01_sameProviderUid_yieldsIdenticalCanonicalIdentityAcrossAndroidAndWeb() {
    val providerUid = "google_sec_99182"
    val provider = "google.com"
    val email = "soc_operator@enterprise.org"

    val androidIdentity = CanonicalAegoraIdentity.resolve(
      providerUid = providerUid,
      provider = provider,
      email = email,
      clientType = PlatformClient.ANDROID
    )

    val webIdentity = CanonicalAegoraIdentity.resolve(
      providerUid = providerUid,
      provider = provider,
      email = email,
      clientType = PlatformClient.WEB
    )

    assertEquals("Canonical User ID must match across Android and Web", androidIdentity.canonicalUserId, webIdentity.canonicalUserId)
    assertEquals("Canonical Learner ID must match across Android and Web", androidIdentity.canonicalLearnerId, webIdentity.canonicalLearnerId)
    assertEquals("operator_google_sec_99182", androidIdentity.canonicalLearnerId)
    assertEquals("usr_google_sec_99182", androidIdentity.canonicalUserId)
  }

  // =========================================================================
  // TEST 2: Bridge from AuthenticatedIdentity retains canonical properties
  // =========================================================================
  @Test
  fun test02_authenticatedIdentity_bridgesAccuratelyToCanonicalIdentity() {
    val authenticated = AuthenticatedIdentity.fromProvider(
      providerUid = "firebase_usr_77",
      provider = "firebase"
    )

    val canonical = CanonicalAegoraIdentity.fromAuthenticatedIdentity(authenticated, PlatformClient.WEB)
    assertEquals(authenticated.mappedLearnerId, canonical.canonicalLearnerId)
    assertEquals(PlatformClient.WEB, canonical.clientType)
  }

  // =========================================================================
  // TEST 3: Cross-platform continuity session handover
  // =========================================================================
  @Test
  fun test03_crossPlatformSession_handlesClientHandoverCleanly() {
    val identity = CanonicalAegoraIdentity.resolve(
      providerUid = "analyst_alpha_01",
      provider = "firebase",
      clientType = PlatformClient.ANDROID
    )

    // 1. Session created on Android
    val androidSession = CrossPlatformContinuityManager.registerSession(identity, PlatformClient.ANDROID)
    assertEquals(PlatformClient.ANDROID, androidSession.activeClient)
    assertEquals(1, CrossPlatformContinuityManager.getActiveSessionCount())

    // 2. Same learner transitions to Web
    val webIdentity = identity.copy(clientType = PlatformClient.WEB)
    val webSession = CrossPlatformContinuityManager.registerSession(webIdentity, PlatformClient.WEB)

    assertEquals("Session count should not duplicate for same user", 1, CrossPlatformContinuityManager.getActiveSessionCount())
    assertEquals(PlatformClient.WEB, webSession.activeClient)
    assertEquals(androidSession.sessionId, webSession.sessionId)
  }

  // =========================================================================
  // TEST 4: Cross-platform authorization rejects client learner ID spoofing
  // =========================================================================
  @Test
  fun test04_crossPlatformAuthorization_rejectsClientLearnerSpoofing() {
    val identity = CanonicalAegoraIdentity.resolve(
      providerUid = "legit_user_55",
      provider = "firebase",
      clientType = PlatformClient.WEB
    )

    val session = CrossPlatformContinuityManager.registerSession(identity, PlatformClient.WEB)
    val legitLearnerId = identity.canonicalLearnerId
    val spoofedLearnerId = "operator_admin_overseer"

    // Valid check
    val validCheck = CrossPlatformContinuityManager.validateClientAccess(
      session.sessionId,
      legitLearnerId,
      PlatformClient.WEB
    )
    assertTrue("Legitimate learner access must be Authorized", validCheck is CrossPlatformValidationResult.Authorized)

    // Spoofed check
    val spoofCheck = CrossPlatformContinuityManager.validateClientAccess(
      session.sessionId,
      spoofedLearnerId,
      PlatformClient.WEB
    )
    assertTrue("Spoofed learner access must be Forbidden", spoofCheck is CrossPlatformValidationResult.Forbidden)
    assertTrue((spoofCheck as CrossPlatformValidationResult.Forbidden).reason.contains("Cross-platform isolation violation"))
  }

  // =========================================================================
  // TEST 5: Authoritative 4 Cyber Twin clusters are strictly preserved
  // =========================================================================
  @Test
  fun test05_authoritativeFourClusters_areStrictlyPreserved() {
    val clusters = CognitiveClusterType.values().map { it.displayName }
    assertEquals(4, clusters.size)
    assertTrue("Must contain Foundation", clusters.contains("Foundation"))
    assertTrue("Must contain Active Defense", clusters.contains("Active Defense"))
    assertTrue("Must contain Generalization & Stress", clusters.contains("Generalization & Stress"))
    assertTrue("Must contain Metacognitive & Strategic", clusters.contains("Metacognitive & Strategic"))
  }

  // =========================================================================
  // TEST 6: Canonical mission validation requires sufficient reasoning
  // =========================================================================
  @Test
  fun test06_canonicalMissionValidation_requiresSufficientReasoning() {
    val missionId = CrossPlatformMissionBridge.SUSPICIOUS_LOGIN_MISSION_ID
    val answers = mapOf(
      "q_login_1" to 0,
      "q_login_2" to 0,
      "q_login_3" to 0
    )

    // Insufficient reasoning
    val rejectedResult = CrossPlatformMissionBridge.validateMissionSubmission(
      missionId = missionId,
      learnerAnswers = answers,
      reasoningText = "too short"
    )
    assertTrue("Insufficient reasoning must be Rejected", rejectedResult is CanonicalMissionValidationResult.Rejected)

    // Adequate reasoning with correct answers
    val successResult = CrossPlatformMissionBridge.validateMissionSubmission(
      missionId = missionId,
      learnerAnswers = answers,
      reasoningText = "Observed 8 failed 4625 attempts from Moscow followed by a single 4624 success, then an impossible travel VPN session in Austin 7 min later."
    )
    assertTrue("Correct answers and solid reasoning must yield Success", successResult is CanonicalMissionValidationResult.Success)
    val success = successResult as CanonicalMissionValidationResult.Success
    assertEquals(100, success.scorePercent)
    assertTrue(success.evidenceHash.startsWith("sha256:aegora_lab_suspicious_login_"))
  }

  // =========================================================================
  // TEST 7: Default platform auth state remains unauthenticated
  // =========================================================================
  @Test
  fun test07_defaultPlatformAuthState_isStrictlyUnauthenticated() {
    assertEquals(AuthState.Unauthenticated, AegoraAuthRepository.authState.value)
    assertFalse(AegoraAuthRepository.isAuthenticated)
    assertNull(CrossPlatformContinuityManager.continuitySnapshot.value)
  }
}
