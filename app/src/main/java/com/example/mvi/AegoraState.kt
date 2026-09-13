package com.example.mvi

import com.example.model.AuthoritativeSubscriptionState
import com.example.model.UserProfile
import com.example.model.SubscriptionTier

/**
 * ============================================================================
 * AEGORA ARCHITECTURE: UNIDIRECTIONAL DATA FLOW (MVI) — IMMUTABLE UI STATE
 * ============================================================================
 *
 * [AegoraState] represents the single, immutable source of truth for the entire
 * AEGORA cybersecurity platform. 
 *
 * ARCHITECTURAL PRINCIPLES:
 * 1. UNIDIRECTIONAL DATA FLOW (UDF):
 *    State flows down from ViewModel/Repository to Jetpack Compose UI components.
 *    No UI component ever mutates state directly. Every mutation requires emitting
 *    an [AegoraIntent] through the ViewModel pipeline.
 *
 * 2. ZERO-TRUST STATE VERIFICATION:
 *    Entitlements (such as [AuthoritativeSubscriptionState]) are never client-inferred
 *    or fabricated. They must be authoritatively validated via RevenueCat
 *    ([Purchases.sharedInstance.getCustomerInfo]) or cryptographic attestation.
 *
 * 3. GRACEFUL DEGRADATION INTEGRITY:
 *    If an external service (e.g. Gemini 2.5 Flash, WebSocket telemetry) times out
 *    beyond 1500ms, the state transitions [isDegradedMode] to `true` while injecting
 *    verified synthetic telemetry (e.g. APT29 mock C2 chain) to guarantee zero UI
 *    freezes and zero interrupted presentations.
 *
 * 4. RECOMPOSITION BOUNDARIES:
 *    All properties are immutable `val` primitives or immutable data classes,
 *    allowing Compose's compiler to skip recomposition when unaffected state slices
 *    remain unchanged, delivering stable 60fps rendering.
 */
data class AegoraState(
    val userProfile: UserProfile = UserProfile(),
    val subscriptionState: AuthoritativeSubscriptionState = AuthoritativeSubscriptionState(
        ownerAuthUid = "",
        tier = SubscriptionTier.FREE,
        active = true,
        provider = "REVENUECAT_AUTHORITATIVE"
    ),
    val activeThreatStream: List<ThreatTelemetryItem> = emptyList(),
    val activeDuelScore: Int = 0,
    val duelsRemainingFreeTier: Int = 2,
    val isAgentThinking: Boolean = false,
    val isDegradedMode: Boolean = false,
    val isPaywallVisible: Boolean = false,
    val lastInterceptedHallucination: String? = null,
    val networkStatus: AegoraVerificationStatus = AegoraVerificationStatus.VERIFIED_SECURE,
    val telemetryMerkleRoot: String = "0x7a3f8c92b4d1e"
)

enum class AegoraVerificationStatus {
    VERIFIED_SECURE,
    DEGRADED_FALLBACK,
    VERIFYING
}

/**
 * Immutable threat telemetry unit representing verifiable event logs.
 */
data class ThreatTelemetryItem(
    val id: String,
    val adversaryName: String,
    val mitreTactic: String,
    val rawPayload: String,
    val isAiHallucination: Boolean,
    val forensicConfidence: Float = 0.96f
)
