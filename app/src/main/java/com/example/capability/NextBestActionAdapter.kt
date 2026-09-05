package com.example.capability

import com.example.model.PredictiveNextAction
import java.util.UUID

/**
 * Adapter interface and implementation for synthesizing Next Best Action recommendations
 * directly grounded in Demonstrated Capability Engine telemetry and limiting gate bottlenecks.
 */
interface NextBestActionAdapter {
  fun generateNextActions(
    results: List<CapabilityAssessmentResult>,
    maxActions: Int = 4
  ): List<PredictiveNextAction>
}

class DefaultNextBestActionAdapter : NextBestActionAdapter {

  override fun generateNextActions(
    results: List<CapabilityAssessmentResult>,
    maxActions: Int
  ): List<PredictiveNextAction> {
    if (results.isEmpty()) {
      return listOf(
        PredictiveNextAction(
          id = "act_baseline_001",
          title = "Initial SOC Diagnostic Challenge",
          category = "Roadmap Milestone",
          destinationTag = "missions",
          urgencyScore = 95,
          primaryReason = "No capability baseline established. Complete diagnostic to initialize Cyber Twin.",
          reasoningTags = listOf("Initial Baseline", "7-Gate Calibration"),
          estimatedMins = 15,
          xpReward = 150,
          telemetryMetric = "Capabilities Measured: 0/5"
        )
      )
    }

    val actions = mutableListOf<PredictiveNextAction>()

    // 1. Prioritize Critical and High Retention Decay capabilities
    val decayingCapabilities = results
      .filter { it.retentionRisk == RetentionRisk.CRITICAL || it.retentionRisk == RetentionRisk.HIGH }
      .sortedByDescending { it.retentionRisk.level }

    for (cap in decayingCapabilities) {
      actions.add(
        PredictiveNextAction(
          id = "act_decay_${cap.skillKey}",
          title = "Decay Refresher: ${cap.name}",
          category = "Decay Prevention",
          destinationTag = "missions",
          urgencyScore = if (cap.retentionRisk == RetentionRisk.CRITICAL) 98 else 85,
          primaryReason = "Last verified ${cap.daysSinceLastVerified} days ago. Current confidence decayed to ${cap.currentConfidence}%.",
          reasoningTags = listOf("Decay Risk: ${cap.retentionRisk.name}", "Baseline: ${cap.historicalCapabilityScore}%"),
          estimatedMins = 15,
          xpReward = 120,
          telemetryMetric = "Confidence: ${cap.currentConfidence}% (Threshold: 75%)"
        )
      )
    }

    // 2. Prioritize Limiting Gates on In-Progress capabilities
    val inProgressCapabilities = results
      .filter { !it.isDemonstrated && it.limitingGate != null }
      .sortedBy { it.gateResults[it.limitingGate]?.score ?: 0 }

    for (cap in inProgressCapabilities) {
      val gate = cap.limitingGate ?: continue
      val gateScore = cap.gateResults[gate]?.score ?: 0
      actions.add(
        PredictiveNextAction(
          id = "act_gate_${cap.skillKey}_${gate.name}",
          title = "${gate.displayName} Drill: ${cap.name}",
          category = "Mistake Remediation",
          destinationTag = "labs",
          urgencyScore = 80,
          primaryReason = "Bottleneck detected in ${gate.displayName} Gate ($gateScore% score).",
          reasoningTags = listOf("Limiting Gate: ${gate.displayName}", "Verified Artifacts: ${cap.evidenceQualityBreakdown.verifiedCount}"),
          estimatedMins = 20,
          xpReward = 180,
          telemetryMetric = "${gate.displayName} Gate: $gateScore% (Required: 70%)"
        )
      )
    }

    // 3. Spaced Practice for Demonstrated capabilities needing maintenance
    val demonstratedNeedingMaintenance = results
      .filter { it.isDemonstrated && it.daysSinceLastVerified > 14 }
      .sortedByDescending { it.daysSinceLastVerified }

    for (cap in demonstratedNeedingMaintenance) {
      actions.add(
        PredictiveNextAction(
          id = "act_spaced_${cap.skillKey}",
          title = "Spaced Challenge: ${cap.name}",
          category = "Spaced Review",
          destinationTag = "decision_lab",
          urgencyScore = 65,
          primaryReason = "Maintain peak historical capability (${cap.historicalCapabilityScore}%) across new context variations.",
          reasoningTags = listOf("Demonstrated Mastery", "Spaced Practice"),
          estimatedMins = 10,
          xpReward = 90,
          telemetryMetric = "Historical Peak: ${cap.historicalCapabilityScore}%"
        )
      )
    }

    return actions.distinctBy { it.id }.take(maxActions)
  }
}
