package com.example.model

/**
 * AEGORA Subscription & Entitlement Architecture Models
 * Governed strictly by Firebase Auth UID and RevenueCat server authority.
 */

enum class SubscriptionTier(val displayName: String, val badgeColor: Long) {
  FREE("Free Operative", 0xFF64748B),
  PRO("AEGORA Pro", 0xFF00F0FF),
  CAREER("Career Pass", 0xFFFFB800),
  UNKNOWN("Unverified", 0xFF475569)
}

data class AuthoritativeSubscriptionState(
  val ownerAuthUid: String,
  val tier: SubscriptionTier,
  val active: Boolean,
  val entitlementIdentifiers: List<String> = emptyList(),
  val productIdentifier: String? = null,
  val expiresAt: String? = null,
  val provider: String = "SYSTEM_DEFAULT",
  val customerId: String = ownerAuthUid,
  val checkedAt: String = "",
  val sourceEventId: String? = null
)

data class SubscriptionFeatureCheck(
  val featureKey: String,
  val requiredTier: SubscriptionTier,
  val granted: Boolean,
  val reason: String? = null
)

data class SubscriptionPackageInfo(
  val identifier: String,
  val packageType: String,
  val tier: SubscriptionTier,
  val title: String,
  val description: String,
  val priceString: String
)
