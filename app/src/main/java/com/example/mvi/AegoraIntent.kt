package com.example.mvi

/**
 * ============================================================================
 * AEGORA ARCHITECTURE: UNIDIRECTIONAL DATA FLOW (MVI) — USER INTENTS
 * ============================================================================
 *
 * [AegoraIntent] defines every discreet, auditable action that can be initiated
 * by the user or an autonomous system event.
 *
 * ARCHITECTURAL DESIGN:
 * - SEALED INTENT HIERARCHY:
 *   Enforces exhaustive compile-time pattern matching in the ViewModel reducer.
 *   Unhandled user actions are structurally impossible, eliminating runtime bugs.
 *
 * - AUDITABILITY & REPLAYABILITY:
 *   Because intents are distinct immutable data structures, the SOC shift engine
 *   and forensics timeline can serialize and replay the exact sequence of analyst
 *   decisions to verify operational compliance.
 *
 * - SECURITY GATING:
 *   Privileged actions (such as [EngageAdversaryDuel] or [RequestRedTeamAI]) pass
 *   through the Zero-Trust RevenueCat check before mutating state.
 */
sealed interface AegoraIntent {

    /**
     * User initiates an AI adversarial duel to analyze raw telemetry.
     */
    data class EngageAdversaryDuel(val scenarioId: String) : AegoraIntent

    /**
     * User challenges an AI analyst claim as a hallucination.
     */
    data class ChallengeAiHallucination(
        val scenarioId: String,
        val userDetectedHallucination: Boolean
    ) : AegoraIntent

    /**
     * Submits an OSINT natural language reconnaissance query to the Threat Intel Agent.
     */
    data class QueryThreatIntel(val prompt: String) : AegoraIntent

    /**
     * Requests authorization of the RevenueCat PRO or Career pass upgrade.
     */
    data class AuthorizeProUpgrade(val packageIdentifier: String) : AegoraIntent

    /**
     * Dismisses or launches the Obsidian Paywall modal.
     */
    data class SetPaywallVisibility(val isVisible: Boolean) : AegoraIntent

    /**
     * Synchronizes authoritative subscription state from RevenueCat's getCustomerInfo.
     */
    data object RefreshAuthoritativeEntitlement : AegoraIntent

    /**
     * Triggers simulated or live SOC shift alert response.
     */
    data class TriageIncidentAlert(val alertId: String, val containmentAction: String) : AegoraIntent

    /**
     * Switches primary operations workspace tab (Learn, Operate, Proof, Intel, etc.).
     */
    data class SwitchWorkspaceTab(val tabIndex: Int) : AegoraIntent
}
