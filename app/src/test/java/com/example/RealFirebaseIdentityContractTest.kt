package com.example

import com.example.auth.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Phase 3A: Real Firebase Identity & Cross-Platform Contract Verification Test Suite.
 *
 * Verifies:
 * 1. Initial authentication state is strictly Unauthenticated.
 * 2. RealFirebaseAuthProvider detects absence of google-services.json honestly without fake auth.
 * 3. Firebase UID deterministically maps to canonical learner identity.
 * 4. Cross-platform invariant: Android and Web produce the identical canonical learner ID for the same Firebase UID.
 * 5. Deterministic collision resistance & sanitization of provider UIDs.
 * 6. Sign-out cleanly invalidates active identity.
 * 7. Arbitrary client learnerId cannot override authenticated identity.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class RealFirebaseIdentityContractTest {

  @Before
  fun setUp() {
    AegoraAuthRepository.resetToDefault()
  }

  @After
  fun tearDown() {
    AegoraAuthRepository.resetToDefault()
  }

  @Test
  fun test01_initialStateIsStrictlyUnauthenticated() {
    val state = AegoraAuthRepository.authState.value
    assertTrue("Initial state must be Unauthenticated", state is AuthState.Unauthenticated)
    assertNull("Initial identity must be null", AegoraAuthRepository.currentIdentity)
    assertNull("Initial learnerId must be null", AegoraAuthRepository.currentLearnerId)
    assertFalse("Must not be authenticated by default", AegoraAuthRepository.isAuthenticated)
  }

  @Test
  fun test02_realFirebaseAuthProvider_unconfiguredWithoutGoogleServices() {
    val provider = RealFirebaseAuthProvider()
    assertFalse("Without google-services.json, RealFirebaseAuthProvider must not claim to be configured", provider.isConfigured)
    assertEquals("FIREBASE", provider.providerId)

    runBlocking {
      val result = provider.signInWithEmailPassword("user@example.com", "secret123")
      assertTrue("Sign-in without google-services.json must return Blocked", result is AuthResult.Blocked)
      val blocked = result as AuthResult.Blocked
      assertTrue("Must explicitly mention missing app/google-services.json", blocked.reason.contains("google-services.json"))
    }
  }

  @Test
  fun test03_firebaseUidToCanonicalLearnerMappingIsDeterministic() {
    val firebaseUid = "d7K3mP9xYz42AbCd"
    val identity = AuthenticatedIdentity.fromProvider(
      providerUid = firebaseUid,
      provider = "FIREBASE",
      email = "operator@aegora.cyber"
    )

    assertEquals("d7K3mP9xYz42AbCd", identity.providerUid)
    assertEquals("FIREBASE", identity.provider)
    assertEquals("operator@aegora.cyber", identity.email)
    assertEquals("operator_d7k3mp9xyz42abcd", identity.mappedLearnerId)
  }

  @Test
  fun test04_crossPlatformMatchingInvariant() {
    // Exact same test vector used in Web platform.test.ts
    val testUid = "firebase_usr_7721"
    val identity = AuthenticatedIdentity.fromProvider(
      providerUid = testUid,
      provider = "FIREBASE",
      email = "analyst@cyberops.internal"
    )

    // Web produces: canonicalUserId="usr_firebase_usr_772", canonicalLearnerId="operator_firebase_usr_772"
    assertEquals("operator_firebase_usr_772", identity.mappedLearnerId)
  }

  @Test
  fun test05_collisionResistanceAndSanitization() {
    val uidA = "FIREBASE_UID_ALPHA_123"
    val uidB = "FIREBASE_UID_BETA_123"

    val identityA = AuthenticatedIdentity.fromProvider(uidA, "FIREBASE")
    val identityB = AuthenticatedIdentity.fromProvider(uidB, "FIREBASE")

    assertNotEquals(identityA.mappedLearnerId, identityB.mappedLearnerId)
    assertTrue("Learner ID must start with operator_ prefix", identityA.mappedLearnerId.startsWith("operator_"))
    assertTrue("Learner ID must start with operator_ prefix", identityB.mappedLearnerId.startsWith("operator_"))
  }

  @Test
  fun test06_signOut_invalidatesSession() {
    val testIdentity = AuthenticatedIdentity.fromProvider("uid_signout_test", "FIREBASE")

    val mockProvider = object : AuthProvider {
      override val providerId: String = "FIREBASE"
      override val isConfigured: Boolean = true
      override val authState = kotlinx.coroutines.flow.MutableStateFlow<AuthState>(AuthState.Authenticated(testIdentity))
      override val currentIdentity: AuthenticatedIdentity? = testIdentity
      override suspend fun signInWithEmailPassword(email: String, pass: String): AuthResult = AuthResult.Success(testIdentity)
      override suspend fun signInWithFederatedToken(idToken: String): AuthResult = AuthResult.Success(testIdentity)
      override suspend fun signOut(): Boolean {
        authState.value = AuthState.Unauthenticated
        return true
      }
    }

    AegoraAuthRepository.setProviderForTesting(mockProvider)
    assertTrue("Must be authenticated with test provider", AegoraAuthRepository.isAuthenticated)
    assertEquals("uid_signout_test", AegoraAuthRepository.currentIdentity?.providerUid)

    runBlocking {
      AegoraAuthRepository.signOut()
    }

    assertFalse("Must be unauthenticated after sign out", AegoraAuthRepository.isAuthenticated)
    assertNull("Identity must be null after sign out", AegoraAuthRepository.currentIdentity)
    assertNull("LearnerId must be null after sign out", AegoraAuthRepository.currentLearnerId)
  }

  @Test
  fun test07_clientSuppliedLearnerIdCannotOverrideAuthenticatedOwnership() {
    val realIdentity = AuthenticatedIdentity.fromProvider("real_uid_88", "FIREBASE")

    val mockProvider = object : AuthProvider {
      override val providerId: String = "FIREBASE"
      override val isConfigured: Boolean = true
      override val authState = kotlinx.coroutines.flow.MutableStateFlow<AuthState>(AuthState.Authenticated(realIdentity))
      override val currentIdentity: AuthenticatedIdentity? = realIdentity
      override suspend fun signInWithEmailPassword(email: String, pass: String): AuthResult = AuthResult.Success(realIdentity)
      override suspend fun signInWithFederatedToken(idToken: String): AuthResult = AuthResult.Success(realIdentity)
      override suspend fun signOut(): Boolean = true
    }
    AegoraAuthRepository.setProviderForTesting(mockProvider)

    val validResult = AuthorizationBoundary.validateLearnerAccess(realIdentity.mappedLearnerId)
    assertTrue("Access to own identity must be authorized", validResult is AuthorizationCheckResult.Authorized)

    val spoofResult = AuthorizationBoundary.validateLearnerAccess("operator_attacker_spoofed")
    assertTrue("Access to foreign identity must be forbidden", spoofResult is AuthorizationCheckResult.Forbidden)
  }
}
