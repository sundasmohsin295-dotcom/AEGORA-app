package com.example.capability

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Deterministic engine for recency-based decay analysis and retention risk computation.
 *
 * Fundamental invariant:
 * Historical capability baseline is NEVER reduced by the passage of time alone.
 * Current confidence reflects active operational readiness adjusted for time elapsed since last verification.
 */
object DecayRecencyEngine {

  data class DecayAnalysisOutput(
    val currentConfidence: Int,
    val retentionRisk: RetentionRisk,
    val daysSinceLastVerified: Int,
    val decayFactorApplied: Double,
    val recommendedAction: String
  )

  fun computeDecay(
    historicalCapabilityScore: Int,
    rawGateConfidence: Int,
    lastVerifiedAt: Long,
    limitingGate: MasteryGateType?,
    currentTime: Long = System.currentTimeMillis(),
    policy: MasteryPolicy = MasteryPolicy.DEFAULT
  ): DecayAnalysisOutput {
    val daysElapsed = if (lastVerifiedAt > 0L && currentTime >= lastVerifiedAt) {
      ((currentTime - lastVerifiedAt) / (1000.0 * 60 * 60 * 24)).toInt()
    } else if (lastVerifiedAt == 0L) {
      90 // Unverified / long ago
    } else {
      0
    }

    val halfLife = if (policy.decayHalfLifeDays > 0.0) policy.decayHalfLifeDays else 30.0
    val rawDecay = 0.5.pow(daysElapsed / halfLife)

    // Floor decay factor so baseline foundational competence is preserved (e.g. at 60% of peak)
    val minDecayFloor = max(0.40, (100.0 - policy.maxDecayPenaltyPercent) / 100.0)
    val boundedDecay = max(minDecayFloor, min(1.0, rawDecay))

    // Current confidence calculation
    val baseline = if (historicalCapabilityScore > 0) historicalCapabilityScore else rawGateConfidence
    val decayedBase = baseline * boundedDecay

    // If active gate evaluation yields fresh evidence, balance fresh execution with decayed baseline
    val finalConfidence = if (rawGateConfidence > 0 && historicalCapabilityScore > 0) {
      val blended = (decayedBase * 0.40) + (rawGateConfidence * 0.60)
      min(100, max(0, blended.roundToInt()))
    } else if (historicalCapabilityScore > 0) {
      min(100, max(0, decayedBase.roundToInt()))
    } else {
      min(100, max(0, rawGateConfidence))
    }

    // Determine retention risk
    val retentionRisk = when {
      daysElapsed <= 14 && finalConfidence >= 80 -> RetentionRisk.LOW
      daysElapsed <= 30 && finalConfidence >= 65 -> RetentionRisk.MEDIUM
      daysElapsed <= 60 || finalConfidence >= 45 -> RetentionRisk.HIGH
      else -> RetentionRisk.CRITICAL
    }

    // Prescriptive recommended action formulation
    val recommendedAction = generatePrescriptiveAction(retentionRisk, limitingGate, daysElapsed)

    return DecayAnalysisOutput(
      currentConfidence = finalConfidence,
      retentionRisk = retentionRisk,
      daysSinceLastVerified = daysElapsed,
      decayFactorApplied = (boundedDecay * 100.0).roundToInt() / 100.0,
      recommendedAction = recommendedAction
    )
  }

  private fun generatePrescriptiveAction(
    risk: RetentionRisk,
    limitingGate: MasteryGateType?,
    daysElapsed: Int
  ): String {
    return when {
      limitingGate == MasteryGateType.INVESTIGATE ->
        "Complete a 15-minute raw SIEM/Sysmon forensic triage drill to satisfy the Investigation Gate."

      limitingGate == MasteryGateType.TRANSFER ->
        "Execute a cross-platform transfer scenario (e.g. AWS -> GCP or Linux -> Windows) to prove generalizability."

      limitingGate == MasteryGateType.APPLY ->
        "Complete a hands-on terminal defense lab without lookup aids to elevate practical execution."

      limitingGate == MasteryGateType.UNCERTAINTY_RESILIENCE ->
        "Engage in a Cyber Decision Lab with conflicting IOCs to train uncertainty calibration."

      limitingGate == MasteryGateType.EXPLAIN ->
        "Draft a structured incident executive briefing or technical post-mortem."

      limitingGate == MasteryGateType.RECALL ->
        "Run an active flash-recall sprint on command syntaxes and attack primitives."

      limitingGate == MasteryGateType.UNDERSTAND ->
        "Review foundational threat modeling and architectural mechanics."

      risk == RetentionRisk.CRITICAL ->
        "Last verified $daysElapsed days ago. Complete an urgent 20-minute refresher mission to restore operational readiness."

      risk == RetentionRisk.HIGH ->
        "Decay risk elevated ($daysElapsed days idle). Complete a spaced retrieval micro-drill."

      risk == RetentionRisk.MEDIUM ->
        "Schedule a quick diagnostic challenge within the next 7 days to maintain peak mastery."

      else ->
        "Capability verified and fresh ($daysElapsed days ago). Maintain state with periodic ambient challenges."
    }
  }
}
