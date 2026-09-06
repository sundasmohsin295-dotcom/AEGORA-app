package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.auth.*
import com.example.capability.*
import com.example.data.DemonstratedCapabilityRepository
import com.example.data.ZeroTrustSecurityRepository
import com.example.data.db.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

/**
 * PHASE 1C: AUTHENTICATION ARCHITECTURE HARDENING TEST SUITE
 *
 * Verifies that:
 * 1. Default authentication state is strictly Unauthenticated upon startup.
 * 2. UI cannot directly forge or mutate authenticated state.
 * 3. Unauthenticated users are rejected by protected domain operations.
 * 4. Authenticated identity authoritatively establishes learner ownership.
 * 5. Learner A cannot access Learner B data (isolation enforcement).
 * 6. Changing a UI learner ID parameter cannot override authenticated ownership.
 * 7. Sign-out cleanly purges authenticated identity and blocks operations.
 * 8. Security Center and repositories honestly report provider unconfigured status.
 * 9. No production authentication path starts with MutableStateFlow(true).
 * 10. Existing learner isolation and capability pipelines remain fully compatible.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthenticationArchitectureHardeningTest {

  private lateinit var database: AegoraDatabase
  private lateinit var repository: DemonstratedCapabilityRepository
  private lateinit var engine: DemonstratedCapabilityEngine

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    database = Room.inMemoryDatabaseBuilder(context, AegoraDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    repository = DemonstratedCapabilityRepository(
      database.capabilityDao(),
      database.capabilityEvidenceDao(),
      database.masteryAssessmentDao()
    )

    engine = DemonstratedCapabilityEngine(MasteryPolicy.DEFAULT)

    AegoraAuthRepository.resetToDefault()
  }

  @After
  fun tearDown() {
    AegoraAuthRepository.resetToDefault()
    database.close()
  }

  /**
   * Helper test-only AuthProvider to simulate verified provider identity in unit tests
   * without fabricating fake production Firebase credentials.
   */
  private class TestAuthProvider(
    private val testUserUid: String = "test_uid_441",
    private val testEmail: String = "operator@aegora.net"
  ) : AuthProvider {
    override val providerId: String = "TEST_ISOLATED_PROVIDER"
    override val isConfigured: Boolean = true

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override val currentIdentity: AuthenticatedIdentity?
      get() = (_authState.value as? AuthState.Authenticated)?.identity

    override suspend fun signInWithEmailPassword(email: String, pass: String): AuthResult {
      if (email == testEmail && pass == "CorrectPass123!") {
        val identity = AuthenticatedIdentity.fromProvider(
          providerUid = testUserUid,
          provider = providerId,
          email = testEmail,
          displayName = "Verified Test Operator"
        )
        _authState.value = AuthState.Authenticated(identity)
        return AuthResult.Success(identity)
      } else {
        _authState.value = AuthState.AuthenticationFailed("Invalid test credentials")
        return AuthResult.Failure("Invalid credentials")
      }
    }

    override suspend fun signInWithFederatedToken(idToken: String): AuthResult {
      val identity = AuthenticatedIdentity.fromProvider(
        providerUid = testUserUid,
        provider = providerId,
        email = testEmail
      )
      _authState.value = AuthState.Authenticated(identity)
      return AuthResult.Success(identity)
    }

    override suspend fun signOut(): Boolean {
      _authState.value = AuthState.Unauthenticated
      return true
    }
  }

  // =========================================================================
  // TEST 1: Default authentication state is unauthenticated
  // =========================================================================
  @Test
  fun test01_defaultAuthenticationState_isUnauthenticated() {
    assertEquals(AuthState.Unauthenticated, AegoraAuthRepository.authState.value)
    assertFalse("AegoraAuthRepository must not be authenticated by default", AegoraAuthRepository.isAuthenticated)
    assertNull("No authenticated identity should exist on cold start", AegoraAuthRepository.currentIdentity)
    assertNull("No current learner ID should exist on cold start", AegoraAuthRepository.currentLearnerId)
  }

  // =========================================================================
  // TEST 2: UI cannot directly set authenticated state
  // =========================================================================
  @Test
  fun test02_uiCannotDirectlySetAuthenticatedState() = runBlocking {
    // Attempting to sign in against production BlockedAuthProvider must fail honestly
    val result = AegoraAuthRepository.signInWithEmailPassword("attacker@evil.com", "random_password")

    assertTrue("Signing in with unconfigured provider must be Blocked", result is AuthResult.Blocked)
    assertFalse("AegoraAuthRepository must remain unauthenticated", AegoraAuthRepository.isAuthenticated)
    assertTrue(
      "Auth state must reflect failed/blocked state",
      AegoraAuthRepository.authState.value is AuthState.AuthenticationFailed
    )
  }

  // =========================================================================
  // TEST 3: Unauthenticated user cannot execute protected learner operations
  // =========================================================================
  @Test
  fun test03_unauthenticatedUser_cannotExecuteProtectedLearnerOperations() {
    // Ensure repository is in default unauthenticated state
    AegoraAuthRepository.resetToDefault()

    val protectedResult = AuthorizationBoundary.executeProtected("operator_target") {
      "Sensitive Capability Record"
    }

    assertTrue(
      "Unauthenticated caller must receive AuthorizationResult.Unauthenticated",
      protectedResult is AuthorizationResult.Unauthenticated
    )

    val gatewayResult = ProtectedCapabilityGateway.getCapabilitiesForLearner(repository, "operator_target")
    assertTrue(
      "Protected capability gateway must reject unauthenticated callers",
      gatewayResult is AuthorizationResult.Unauthenticated
    )
  }

  // =========================================================================
  // TEST 4: Authenticated identity determines learner ownership
  // =========================================================================
  @Test
  fun test04_authenticatedIdentity_determinesLearnerOwnership() = runBlocking {
    val testProvider = TestAuthProvider(testUserUid = "vance_sec_99", testEmail = "operator@aegora.net")
    AegoraAuthRepository.setProviderForTesting(testProvider)

    val authResult = AegoraAuthRepository.signInWithEmailPassword("operator@aegora.net", "CorrectPass123!")
    assertTrue(authResult is AuthResult.Success)

    val expectedLearnerId = "operator_vance_sec_99"
    assertEquals(expectedLearnerId, AegoraAuthRepository.currentLearnerId)

    val check = AuthorizationBoundary.validateLearnerAccess(expectedLearnerId)
    assertTrue("Authorized identity must own its mapped learner ID", check is AuthorizationCheckResult.Authorized)
  }

  // =========================================================================
  // TEST 5: Learner A cannot access Learner B
  // =========================================================================
  @Test
  fun test05_learnerACannotAccessLearnerB() = runBlocking {
    val testProvider = TestAuthProvider(testUserUid = "learner_alpha")
    AegoraAuthRepository.setProviderForTesting(testProvider)
    AegoraAuthRepository.signInWithFederatedToken("token_alpha")

    val learnerAId = "operator_learner_alpha"
    val learnerBId = "operator_learner_bravo"

    assertEquals(learnerAId, AegoraAuthRepository.currentLearnerId)

    // Verify Learner A can access their own domain operations
    val ownResult = AuthorizationBoundary.executeProtected(learnerAId) { "Learner A Data" }
    assertTrue(ownResult is AuthorizationResult.Authorized)
    assertEquals("Learner A Data", (ownResult as AuthorizationResult.Authorized).data)

    // Verify Learner A CANNOT access Learner B
    val crossResult = AuthorizationBoundary.executeProtected(learnerBId) { "Learner B Data" }
    assertTrue("Cross-learner access must be Forbidden", crossResult is AuthorizationResult.Forbidden)
    assertTrue(
      (crossResult as AuthorizationResult.Forbidden).message.contains("Learner isolation violation")
    )
  }

  // =========================================================================
  // TEST 6: Changing a UI learner ID cannot change authenticated ownership
  // =========================================================================
  @Test
  fun test06_changingUiLearnerId_cannotChangeAuthenticatedOwnership() = runBlocking {
    val testProvider = TestAuthProvider(testUserUid = "legit_user_77")
    AegoraAuthRepository.setProviderForTesting(testProvider)
    AegoraAuthRepository.signInWithFederatedToken("token")

    val authenticatedLearnerId = "operator_legit_user_77"
    val spoofedUiLearnerId = "operator_admin_super"

    // Even if UI supplies 'spoofedUiLearnerId', the domain boundary enforces identity mapping
    val spoofCheck = AuthorizationBoundary.validateLearnerAccess(spoofedUiLearnerId)
    assertTrue("Spoofed learner ID must be rejected with Forbidden", spoofCheck is AuthorizationCheckResult.Forbidden)

    val protectedOp = AuthorizationBoundary.executeProtected(spoofedUiLearnerId) {
      "Unauthorized Access"
    }
    assertTrue("Domain boundary must reject spoofed UI parameter", protectedOp is AuthorizationResult.Forbidden)
  }

  // =========================================================================
  // TEST 7: Sign-out clears authenticated application context
  // =========================================================================
  @Test
  fun test07_signOut_clearsAuthenticatedApplicationContext() = runBlocking {
    val testProvider = TestAuthProvider(testUserUid = "operator_session_test")
    AegoraAuthRepository.setProviderForTesting(testProvider)
    AegoraAuthRepository.signInWithFederatedToken("token")

    assertTrue(AegoraAuthRepository.isAuthenticated)
    assertNotNull(AegoraAuthRepository.currentIdentity)

    // Execute sign out
    AegoraAuthRepository.signOut()

    assertFalse("Must be unauthenticated after sign out", AegoraAuthRepository.isAuthenticated)
    assertEquals(AuthState.Unauthenticated, AegoraAuthRepository.authState.value)
    assertNull("Current identity must be purged", AegoraAuthRepository.currentIdentity)
    assertNull("Learner ID must be purged", AegoraAuthRepository.currentLearnerId)

    // Verify protected access is immediately blocked
    val postSignOutResult = AuthorizationBoundary.executeProtected("operator_session_test") { "Data" }
    assertTrue(postSignOutResult is AuthorizationResult.Unauthenticated)
  }

  // =========================================================================
  // TEST 8: Security Center correctly reports provider authentication unavailable
  // =========================================================================
  @Test
  fun test08_securityCenter_reportsProviderAuthenticationUnavailableWhenFirebaseAbsent() {
    AegoraAuthRepository.resetToDefault()

    val providerStatus = AegoraAuthRepository.getProviderStatus()
    assertTrue("Status must be BLOCKED when google-services.json is missing", providerStatus is ProviderStatus.BLOCKED)

    val blockedStatus = providerStatus as ProviderStatus.BLOCKED
    assertTrue(blockedStatus.reason.contains("google-services.json"))

    assertFalse("ZeroTrustSecurityRepository live provider must be false", ZeroTrustSecurityRepository.isLiveProviderConnected)
    assertTrue(ZeroTrustSecurityRepository.providerInfrastructureStatus.contains("AUTH BACKEND BLOCKED"))
  }

  // =========================================================================
  // TEST 9: No production authentication path depends on MutableStateFlow(true)
  // =========================================================================
  @Test
  fun test09_noProductionAuthenticationPathDependsOnMutableStateFlowTrue() {
    // Assert that ZeroTrustSecurityRepository defaults to false
    assertFalse(
      "ZeroTrustSecurityRepository.isAuthenticated must default to false",
      ZeroTrustSecurityRepository.isAuthenticated.value
    )

    // Assert that AegoraAuthRepository defaults to unauthenticated
    assertEquals(
      "AegoraAuthRepository.authState must default to Unauthenticated",
      AuthState.Unauthenticated,
      AegoraAuthRepository.authState.value
    )
    assertFalse("AegoraAuthRepository.isAuthenticated must default to false", AegoraAuthRepository.isAuthenticated)
  }

  // =========================================================================
  // TEST 10: Existing capability and learner-isolation tests remain passing
  // =========================================================================
  @Test
  fun test10_existingCapabilityAndLearnerIsolationTests_remainPassing() = runBlocking {
    val learnerA = "operator_alpha"
    val learnerB = "operator_beta"

    // Seed capability for Learner A
    val capabilityA = CapabilityEntity(
      id = "cap_a_1",
      learnerId = learnerA,
      skillKey = "wireshark_pcap_analysis",
      name = "Network PCAP Triage",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 80,
      recallScore = 80,
      applicationScore = 80,
      investigationScore = 80,
      transferScore = 80,
      explanationScore = 80,
      uncertaintyResilienceScore = 80,
      independenceScore = 80,
      evidenceQualityScore = 80,
      currentConfidence = 80,
      historicalCapabilityScore = 80,
      lastVerifiedAt = System.currentTimeMillis()
    )
    database.capabilityDao().insert(capabilityA)

    // Seed capability for Learner B
    val capabilityB = CapabilityEntity(
      id = "cap_b_1",
      learnerId = learnerB,
      skillKey = "memory_forensics",
      name = "Volatility Memory Analysis",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 70,
      recallScore = 70,
      applicationScore = 70,
      investigationScore = 70,
      transferScore = 70,
      explanationScore = 70,
      uncertaintyResilienceScore = 70,
      independenceScore = 70,
      evidenceQualityScore = 70,
      currentConfidence = 70,
      historicalCapabilityScore = 70,
      lastVerifiedAt = System.currentTimeMillis()
    )
    database.capabilityDao().insert(capabilityB)

    // Verify database separation
    val capsA = database.capabilityDao().getByLearnerId(learnerA)
    val capsB = database.capabilityDao().getByLearnerId(learnerB)

    assertEquals(1, capsA.size)
    assertEquals("cap_a_1", capsA[0].id)
    assertEquals(1, capsB.size)
    assertEquals("cap_b_1", capsB[0].id)

    // Verify engine validation rejects cross-learner evidence
    val crossEvidence = CapabilityEvidenceEntity(
      id = UUID.randomUUID().toString(),
      capabilityId = "cap_a_1",
      learnerId = learnerB, // Mismatched learner
      missionId = "mis_01",
      attemptId = "att_01",
      evidenceType = EvidenceType.LAB_SUBMISSION.name,
      complexity = 80,
      independenceScore = 80,
      authenticityScore = 80,
      transferabilityScore = 80,
      evidenceQualityScore = 80,
      outcomeScore = 85,
      hintsUsed = 0,
      retries = 0,
      reasoningQualityScore = 80,
      confidenceDeclared = 80,
      confidenceCalibrated = 80,
      evidenceHash = "sha256:dummy_test_hash_value",
      verificationStatus = VerificationStatus.VERIFIED.name,
      createdAt = System.currentTimeMillis()
    )

    try {
      engine.evaluateCapability(capabilityA, listOf(crossEvidence), emptyList())
      fail("DemonstratedCapabilityEngine must throw IllegalArgumentException on cross-learner evidence")
    } catch (expected: IllegalArgumentException) {
      assertTrue(expected.message!!.contains("Cross-learner evidence isolation violation"))
    }
  }
}
