package com.example

import com.example.model.AuthoritativeSubscriptionState
import com.example.model.SubscriptionTier
import com.example.subscription.AegoraSubscriptionRepository
import com.example.subscription.SubscriptionPurchasesClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SubscriptionArchitectureTest {

  @Before
  fun setUp() {
    AegoraSubscriptionRepository.resetForTesting()
  }

  @Test
  fun testInitialStateIsStrictlyFreeTier() {
    val state = AegoraSubscriptionRepository.subscriptionState.value
    assertEquals(SubscriptionTier.FREE, state.tier)
    assertTrue(state.active)
    assertEquals("SYSTEM_DEFAULT", state.provider)
  }

  @Test
  fun testFeatureAccessControlAcrossTiers() = runBlocking {
    // 1. Initially FREE tier
    val freeSocCheck = AegoraSubscriptionRepository.checkFeatureAccess("LIVE_SOC_SHIFT")
    assertFalse(freeSocCheck.granted)
    assertEquals(SubscriptionTier.PRO, freeSocCheck.requiredTier)

    val freeFoundational = AegoraSubscriptionRepository.checkFeatureAccess("FOUNDATIONAL_MISSIONS")
    assertTrue(freeFoundational.granted)

    val freePurpleArena = AegoraSubscriptionRepository.checkFeatureAccess("PURPLE_TEAM_ARENA")
    assertFalse(freePurpleArena.granted)
    assertEquals(SubscriptionTier.CAREER, freePurpleArena.requiredTier)

    // 2. Transition to PRO tier via server authority update
    AegoraSubscriptionRepository.updateFromServerAuthority(
      AuthoritativeSubscriptionState(
        ownerAuthUid = "usr_test_pro",
        tier = SubscriptionTier.PRO,
        active = true,
        entitlementIdentifiers = listOf("pro"),
        provider = "REVENUECAT"
      )
    )

    val proSocCheck = AegoraSubscriptionRepository.checkFeatureAccess("LIVE_SOC_SHIFT")
    assertTrue(proSocCheck.granted)

    val proDisassemblerCheck = AegoraSubscriptionRepository.checkFeatureAccess("BINARY_DISASSEMBLER")
    assertTrue(proDisassemblerCheck.granted)

    val proPurpleArena = AegoraSubscriptionRepository.checkFeatureAccess("PURPLE_TEAM_ARENA")
    assertFalse(proPurpleArena.granted)

    // 3. Transition to CAREER tier
    AegoraSubscriptionRepository.updateFromServerAuthority(
      AuthoritativeSubscriptionState(
        ownerAuthUid = "usr_test_career",
        tier = SubscriptionTier.CAREER,
        active = true,
        entitlementIdentifiers = listOf("career"),
        provider = "REVENUECAT"
      )
    )

    val careerSocCheck = AegoraSubscriptionRepository.checkFeatureAccess("LIVE_SOC_SHIFT")
    assertTrue(careerSocCheck.granted)

    val careerPurpleArena = AegoraSubscriptionRepository.checkFeatureAccess("PURPLE_TEAM_ARENA")
    assertTrue(careerPurpleArena.granted)

    val careerDossier = AegoraSubscriptionRepository.checkFeatureAccess("CAREER_DOSSIER_EXPORT")
    assertTrue(careerDossier.granted)
  }

  @Test
  fun testAccountSignOutClearsSubscriptionCache() = runBlocking {
    AegoraSubscriptionRepository.updateFromServerAuthority(
      AuthoritativeSubscriptionState(
        ownerAuthUid = "usr_authenticated_123",
        tier = SubscriptionTier.PRO,
        active = true,
        provider = "REVENUECAT"
      )
    )

    assertEquals(SubscriptionTier.PRO, AegoraSubscriptionRepository.subscriptionState.value.tier)

    // Simulate user sign out
    AegoraSubscriptionRepository.onUserSignedOut()

    val stateAfterSignOut = AegoraSubscriptionRepository.subscriptionState.value
    assertEquals(SubscriptionTier.FREE, stateAfterSignOut.tier)
    assertEquals("", stateAfterSignOut.ownerAuthUid)
    assertEquals("SYSTEM_DEFAULT", stateAfterSignOut.provider)
  }

  @Test
  fun testPurchasesClientPurchaseFlow() = runBlocking {
    val mockClient = object : SubscriptionPurchasesClient {
      override val isConfigured: Boolean = true
      var lastLoggedInUser: String? = null

      override suspend fun logIn(appUserId: String): Result<AuthoritativeSubscriptionState> {
        lastLoggedInUser = appUserId
        return Result.success(
          AuthoritativeSubscriptionState(
            ownerAuthUid = appUserId,
            tier = SubscriptionTier.FREE,
            active = true
          )
        )
      }

      override suspend fun logOut(): Result<Unit> = Result.success(Unit)

      override suspend fun getCustomerInfo(): Result<AuthoritativeSubscriptionState> {
        return Result.success(
          AuthoritativeSubscriptionState(
            ownerAuthUid = "usr_test",
            tier = SubscriptionTier.FREE,
            active = true
          )
        )
      }

      override suspend fun purchasePackage(packageId: String): Result<AuthoritativeSubscriptionState> {
        val tier = if (packageId.contains("career")) SubscriptionTier.CAREER else SubscriptionTier.PRO
        return Result.success(
          AuthoritativeSubscriptionState(
            ownerAuthUid = "usr_test",
            tier = tier,
            active = true,
            entitlementIdentifiers = listOf(tier.name.lowercase()),
            productIdentifier = packageId,
            provider = "REVENUECAT"
          )
        )
      }

      override suspend fun restorePurchases(): Result<AuthoritativeSubscriptionState> {
        return Result.success(
          AuthoritativeSubscriptionState(
            ownerAuthUid = "usr_test",
            tier = SubscriptionTier.PRO,
            active = true,
            entitlementIdentifiers = listOf("pro"),
            provider = "REVENUECAT"
          )
        )
      }
    }

    AegoraSubscriptionRepository.setPurchasesClient(mockClient)
    AegoraSubscriptionRepository.syncSubscriptionForUser("firebase_user_777")
    assertEquals("firebase_user_777", mockClient.lastLoggedInUser)

    val purchaseResult = AegoraSubscriptionRepository.purchasePackage("aegora_career_annual")
    assertTrue(purchaseResult.isSuccess)
    assertEquals(SubscriptionTier.CAREER, AegoraSubscriptionRepository.subscriptionState.value.tier)
  }
}
