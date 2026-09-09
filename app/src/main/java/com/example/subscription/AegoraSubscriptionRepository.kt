package com.example.subscription

import com.example.model.AuthoritativeSubscriptionState
import com.example.model.SubscriptionFeatureCheck
import com.example.model.SubscriptionPackageInfo
import com.example.model.SubscriptionTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Interface defining the RevenueCat / In-App Purchases service.
 * Supports real Purchases SDK operations with mock/test and fallback support.
 */
interface SubscriptionPurchasesClient {
  val isConfigured: Boolean
  suspend fun logIn(appUserId: String): Result<AuthoritativeSubscriptionState>
  suspend fun logOut(): Result<Unit>
  suspend fun getCustomerInfo(): Result<AuthoritativeSubscriptionState>
  suspend fun purchasePackage(packageId: String): Result<AuthoritativeSubscriptionState>
  suspend fun restorePurchases(): Result<AuthoritativeSubscriptionState>
}

/**
 * Central Subscription & Entitlement Repository for AEGORA.
 * Ensures:
 * 1. Default subscription state is strictly [SubscriptionTier.FREE].
 * 2. User identity is bound deterministically to Firebase Auth UID.
 * 3. Switching accounts or signing out resets cached customer info.
 * 4. Client writes are forbidden; server authority / Firestore is source of truth.
 */
object AegoraSubscriptionRepository {

  private val _subscriptionState = MutableStateFlow(
    AuthoritativeSubscriptionState(
      ownerAuthUid = "",
      tier = SubscriptionTier.FREE,
      active = true,
      provider = "SYSTEM_DEFAULT"
    )
  )
  val subscriptionState: StateFlow<AuthoritativeSubscriptionState> = _subscriptionState.asStateFlow()

  private var activeClient: SubscriptionPurchasesClient? = null

  fun setPurchasesClient(client: SubscriptionPurchasesClient) {
    this.activeClient = client
  }

  fun getAvailablePackages(): List<SubscriptionPackageInfo> {
    return listOf(
      SubscriptionPackageInfo(
        identifier = "aegora_pro_monthly",
        packageType = "MONTHLY",
        tier = SubscriptionTier.PRO,
        title = "AEGORA Pro Monthly",
        description = "Live SOC shifts, deep binary disassembly, full 7-gate radar and hands-on incident triage.",
        priceString = "$19.99 / mo"
      ),
      SubscriptionPackageInfo(
        identifier = "aegora_career_annual",
        packageType = "ANNUAL",
        tier = SubscriptionTier.CAREER,
        title = "AEGORA Career Pass Annual",
        description = "Full Purple Team war room, 1-on-1 AI mentor synthesis, verified employer dossier export & priority radar.",
        priceString = "$149.99 / yr"
      )
    )
  }

  /**
   * Synchronizes subscription state for the authenticated Firebase UID.
   */
  suspend fun syncSubscriptionForUser(authUid: String): AuthoritativeSubscriptionState {
    if (authUid.isBlank()) {
      val defaultState = AuthoritativeSubscriptionState(
        ownerAuthUid = "",
        tier = SubscriptionTier.FREE,
        active = true,
        provider = "SYSTEM_DEFAULT"
      )
      _subscriptionState.value = defaultState
      return defaultState
    }

    val client = activeClient
    if (client != null && client.isConfigured) {
      val loginRes = client.logIn(authUid)
      if (loginRes.isSuccess) {
        val state = loginRes.getOrThrow()
        _subscriptionState.value = state
        return state
      }
    }

    // Default state for authenticated user without active purchases
    val fallback = AuthoritativeSubscriptionState(
      ownerAuthUid = authUid,
      tier = SubscriptionTier.FREE,
      active = true,
      customerId = authUid,
      provider = "SYSTEM_DEFAULT"
    )
    _subscriptionState.value = fallback
    return fallback
  }

  /**
   * Updates state from server authority / Firestore document snapshot.
   */
  fun updateFromServerAuthority(authoritative: AuthoritativeSubscriptionState) {
    _subscriptionState.value = authoritative
  }

  /**
   * Purchases a package for the currently active user.
   */
  suspend fun purchasePackage(packageId: String): Result<AuthoritativeSubscriptionState> {
    val client = activeClient
    if (client == null || !client.isConfigured) {
      return Result.failure(IllegalStateException("RevenueCat client is not configured or available."))
    }

    val res = client.purchasePackage(packageId)
    if (res.isSuccess) {
      val updated = res.getOrThrow()
      _subscriptionState.value = updated
    }
    return res
  }

  /**
   * Restores existing purchases for the active user.
   */
  suspend fun restorePurchases(): Result<AuthoritativeSubscriptionState> {
    val client = activeClient
    if (client == null || !client.isConfigured) {
      return Result.failure(IllegalStateException("RevenueCat client is not configured or available."))
    }

    val res = client.restorePurchases()
    if (res.isSuccess) {
      val updated = res.getOrThrow()
      _subscriptionState.value = updated
    }
    return res
  }

  /**
   * Evaluates if a given feature key is unlocked under the current subscription.
   */
  fun checkFeatureAccess(featureKey: String): SubscriptionFeatureCheck {
    val currentTier = _subscriptionState.value.tier
    return when (featureKey) {
      "LIVE_SOC_SHIFT",
      "BINARY_DISASSEMBLER",
      "FULL_SKILL_RADAR" -> {
        val granted = currentTier == SubscriptionTier.PRO || currentTier == SubscriptionTier.CAREER
        SubscriptionFeatureCheck(
          featureKey = featureKey,
          requiredTier = SubscriptionTier.PRO,
          granted = granted,
          reason = if (!granted) "Requires AEGORA Pro or Career Pass." else null
        )
      }

      "PURPLE_TEAM_ARENA",
      "CAREER_DOSSIER_EXPORT",
      "UNLIMITED_MENTOR_SYNTHESIS" -> {
        val granted = currentTier == SubscriptionTier.CAREER
        SubscriptionFeatureCheck(
          featureKey = featureKey,
          requiredTier = SubscriptionTier.CAREER,
          granted = granted,
          reason = if (!granted) "Requires AEGORA Career Pass." else null
        )
      }

      "FOUNDATIONAL_MISSIONS" -> {
        SubscriptionFeatureCheck(
          featureKey = featureKey,
          requiredTier = SubscriptionTier.FREE,
          granted = true
        )
      }

      else -> {
        SubscriptionFeatureCheck(
          featureKey = featureKey,
          requiredTier = SubscriptionTier.FREE,
          granted = true
        )
      }
    }
  }

  /**
   * Resets subscription identity when user signs out or switches accounts.
   */
  suspend fun onUserSignedOut() {
    activeClient?.logOut()
    _subscriptionState.value = AuthoritativeSubscriptionState(
      ownerAuthUid = "",
      tier = SubscriptionTier.FREE,
      active = true,
      provider = "SYSTEM_DEFAULT"
    )
  }

  internal fun resetForTesting() {
    activeClient = null
    _subscriptionState.value = AuthoritativeSubscriptionState(
      ownerAuthUid = "",
      tier = SubscriptionTier.FREE,
      active = true,
      provider = "SYSTEM_DEFAULT"
    )
  }
}
