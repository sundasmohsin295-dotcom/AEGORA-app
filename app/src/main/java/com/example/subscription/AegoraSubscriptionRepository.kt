package com.example.subscription

import com.example.model.AuthoritativeSubscriptionState
import com.example.model.SubscriptionFeatureCheck
import com.example.model.SubscriptionPackageInfo
import com.example.model.SubscriptionTier
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getCustomerInfoWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

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

  const val ENTITLEMENT_PRO = "pro_tier"
  const val ENTITLEMENT_CAREER = "career_tier"
  const val FREE_TIER_MAX_AI_REALITY_MISSIONS = 3
  const val FREE_TIER_MAX_DUELS = 2

  private val _subscriptionState = MutableStateFlow(
    AuthoritativeSubscriptionState(
      ownerAuthUid = "",
      tier = SubscriptionTier.FREE,
      active = true,
      provider = "SYSTEM_DEFAULT"
    )
  )
  val subscriptionState: StateFlow<AuthoritativeSubscriptionState> = _subscriptionState.asStateFlow()

  private val _aiRealityMissionsCompleted = MutableStateFlow(0)
  val aiRealityMissionsCompleted: StateFlow<Int> = _aiRealityMissionsCompleted.asStateFlow()

  private val _adversaryDuelsEngaged = MutableStateFlow(0)
  val adversaryDuelsEngaged: StateFlow<Int> = _adversaryDuelsEngaged.asStateFlow()

  private var activeClient: SubscriptionPurchasesClient? = null

  fun setPurchasesClient(client: SubscriptionPurchasesClient) {
    this.activeClient = client
  }

  fun recordAiRealityMissionCompleted() {
    _aiRealityMissionsCompleted.value += 1
  }

  fun recordDuelEngaged() {
    _adversaryDuelsEngaged.value += 1
  }

  fun canAccessAdversaryDuel(): Boolean {
    val state = _subscriptionState.value
    if (state.tier == SubscriptionTier.PRO || state.tier == SubscriptionTier.CAREER) return true
    if (state.entitlementIdentifiers.contains(ENTITLEMENT_PRO) || state.entitlementIdentifiers.contains(ENTITLEMENT_CAREER)) return true
    return _adversaryDuelsEngaged.value < FREE_TIER_MAX_DUELS
  }

  fun canAccessAiRealityCheck(): Boolean {
    val state = _subscriptionState.value
    if (state.tier == SubscriptionTier.PRO || state.tier == SubscriptionTier.CAREER) return true
    if (state.entitlementIdentifiers.contains(ENTITLEMENT_PRO) || state.entitlementIdentifiers.contains(ENTITLEMENT_CAREER)) return true
    return _aiRealityMissionsCompleted.value < FREE_TIER_MAX_AI_REALITY_MISSIONS
  }

  fun canAccessBiometricExport(): Boolean {
    val state = _subscriptionState.value
    return state.tier == SubscriptionTier.PRO || state.tier == SubscriptionTier.CAREER ||
      state.entitlementIdentifiers.contains(ENTITLEMENT_PRO) || state.entitlementIdentifiers.contains(ENTITLEMENT_CAREER)
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
  suspend fun syncSubscriptionForUser(authUid: String): AuthoritativeSubscriptionState = withContext(Dispatchers.IO) {
    if (authUid.isBlank()) {
      val defaultState = AuthoritativeSubscriptionState(
        ownerAuthUid = "",
        tier = SubscriptionTier.FREE,
        active = true,
        provider = "SYSTEM_DEFAULT"
      )
      _subscriptionState.value = defaultState
      return@withContext defaultState
    }

    val client = activeClient
    if (client != null && client.isConfigured) {
      val loginRes = client.logIn(authUid)
      if (loginRes.isSuccess) {
        val state = loginRes.getOrThrow()
        _subscriptionState.value = state
        return@withContext state
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
    fallback
  }

  /**
   * Updates state from server authority / Firestore document snapshot.
   */
  fun updateFromServerAuthority(authoritative: AuthoritativeSubscriptionState) {
    _subscriptionState.value = authoritative
  }

  /**
   * Authoritative RevenueCat CustomerInfo updater.
   * `Purchases.sharedInstance.getCustomerInfo` is the sole source of truth for PRO tier entitlements.
   */
  fun updateFromCustomerInfo(customerInfo: CustomerInfo) {
    val proActive = customerInfo.entitlements[ENTITLEMENT_PRO]?.isActive == true
    val careerActive = customerInfo.entitlements[ENTITLEMENT_CAREER]?.isActive == true
    val entitlements = mutableListOf<String>()
    if (proActive) entitlements.add(ENTITLEMENT_PRO)
    if (careerActive) entitlements.add(ENTITLEMENT_CAREER)

    val currentUid = _subscriptionState.value.ownerAuthUid.ifBlank { customerInfo.originalAppUserId }
    val tier = when {
      careerActive -> SubscriptionTier.CAREER
      proActive -> SubscriptionTier.PRO
      else -> SubscriptionTier.FREE
    }

    _subscriptionState.value = AuthoritativeSubscriptionState(
      ownerAuthUid = currentUid,
      tier = tier,
      active = proActive || careerActive,
      customerId = customerInfo.originalAppUserId,
      entitlementIdentifiers = entitlements,
      provider = "REVENUECAT_AUTHORITATIVE"
    )
  }

  /**
   * Asynchronously queries Purchases.sharedInstance.getCustomerInfo to refresh authoritative entitlement status.
   */
  fun refreshFromCustomerInfo(onResult: ((Boolean) -> Unit)? = null) {
    if (!Purchases.isConfigured) {
      onResult?.invoke(false)
      return
    }
    try {
      Purchases.sharedInstance.getCustomerInfoWith(
        onError = {
          onResult?.invoke(false)
        },
        onSuccess = { customerInfo ->
          updateFromCustomerInfo(customerInfo)
          val hasPro = customerInfo.entitlements[ENTITLEMENT_PRO]?.isActive == true
          onResult?.invoke(hasPro)
        }
      )
    } catch (e: Throwable) {
      onResult?.invoke(false)
    }
  }

  /**
   * Updates state directly when confirmed via RevenueCat purchase callback.
   */
  fun recordDirectPurchase(tier: SubscriptionTier, ownerAuthUid: String) {
    _subscriptionState.value = AuthoritativeSubscriptionState(
      ownerAuthUid = ownerAuthUid,
      tier = tier,
      active = tier != SubscriptionTier.FREE,
      entitlementIdentifiers = if (tier == SubscriptionTier.PRO) listOf(ENTITLEMENT_PRO) else if (tier == SubscriptionTier.CAREER) listOf(ENTITLEMENT_PRO, ENTITLEMENT_CAREER) else emptyList(),
      provider = "REVENUECAT_GOOGLE_PLAY"
    )
  }

  /**
   * Purchases a package for the currently active user.
   */
  suspend fun purchasePackage(packageId: String): Result<AuthoritativeSubscriptionState> = withContext(Dispatchers.IO) {
    val client = activeClient
    if (client == null || !client.isConfigured) {
      return@withContext Result.failure(IllegalStateException("RevenueCat client is not configured or available."))
    }

    val res = client.purchasePackage(packageId)
    if (res.isSuccess) {
      val updated = res.getOrThrow()
      _subscriptionState.value = updated
    }
    res
  }

  /**
   * Restores existing purchases for the active user.
   */
  suspend fun restorePurchases(): Result<AuthoritativeSubscriptionState> = withContext(Dispatchers.IO) {
    val client = activeClient
    if (client == null || !client.isConfigured) {
      return@withContext Result.failure(IllegalStateException("RevenueCat client is not configured or available."))
    }

    val res = client.restorePurchases()
    if (res.isSuccess) {
      val updated = res.getOrThrow()
      _subscriptionState.value = updated
    }
    res
  }

  /**
   * Evaluates if a given feature key is unlocked under the current subscription.
   */
  fun checkFeatureAccess(featureKey: String): SubscriptionFeatureCheck {
    val currentTier = _subscriptionState.value.tier
    return when (featureKey) {
      "LIVE_SOC_SHIFT",
      "BINARY_DISASSEMBLER",
      "FULL_SKILL_RADAR",
      "RED_TEAM_ADVERSARY",
      "AI_COUNCIL_ARBITRATION",
      "ADVANCED_FAILURE_AUTOPSY",
      "LIVE_THREAT_INTEL",
      "FORENSIC_ARBITRATOR",
      "ADAPTIVE_SKILL_PASSPORT" -> {
        val hasEntitlement = _subscriptionState.value.entitlementIdentifiers.contains(ENTITLEMENT_PRO) ||
          _subscriptionState.value.entitlementIdentifiers.contains(ENTITLEMENT_CAREER)
        val granted = hasEntitlement || currentTier == SubscriptionTier.PRO || currentTier == SubscriptionTier.CAREER
        SubscriptionFeatureCheck(
          featureKey = featureKey,
          requiredTier = SubscriptionTier.PRO,
          granted = granted,
          reason = if (!granted) "Requires AEGORA Pro ($ENTITLEMENT_PRO) or Career Pass." else null
        )
      }

      "PURPLE_TEAM_ARENA",
      "CAREER_DOSSIER_EXPORT",
      "RECRUITER_PROOF_LINK_EXPORT",
      "UNLIMITED_MENTOR_SYNTHESIS",
      "VULNERABILITY_TRIAGE_ARENA" -> {
        val hasEntitlement = _subscriptionState.value.entitlementIdentifiers.contains(ENTITLEMENT_CAREER)
        val granted = hasEntitlement || currentTier == SubscriptionTier.CAREER
        SubscriptionFeatureCheck(
          featureKey = featureKey,
          requiredTier = SubscriptionTier.CAREER,
          granted = granted,
          reason = if (!granted) "Requires AEGORA Career Pass ($ENTITLEMENT_CAREER)." else null
        )
      }

      "AI_REALITY_CHECK" -> {
        val granted = canAccessAiRealityCheck()
        SubscriptionFeatureCheck(
          featureKey = featureKey,
          requiredTier = SubscriptionTier.FREE,
          granted = granted,
          reason = if (!granted) "Free tier limit reached ($FREE_TIER_MAX_AI_REALITY_MISSIONS missions). Upgrade to Pro for unlimited AI reality checks." else null
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
