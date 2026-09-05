package com.example.capability

import com.example.model.CyberTwin60Snapshot
import com.example.model.DecayRiskLevel
import com.example.model.DimensionExplainabilityV12
import com.example.model.PredictiveNextAction
import com.example.model.SkillPassportLevel
import com.example.model.TwinDimensionV12

/**
 * Result data class packaging the entire end-to-end execution of the Capability Intelligence Pipeline:
 * Real Evidence -> DemonstratedCapabilityEngine -> CapabilityAssessmentResult -> CyberTwinAdapter
 * -> Cyber Twin 6.0 State -> Capability Bottleneck -> NextBestActionAdapter -> Next Best Action
 */
data class CapabilityPipelineExecutionResult(
  val learnerId: String,
  val capabilityResults: List<CapabilityAssessmentResult>,
  val cyberTwinSnapshot: CyberTwin60Snapshot,
  val primaryBottleneckGate: MasteryGateType?,
  val nextBestActions: List<PredictiveNextAction>
)

/**
 * Adapter interface and implementation connecting Demonstrated Capability Engine outputs
 * to the AEGORA Cyber Twin 6.0 capability model.
 */
interface CyberTwinAdapter {
  fun mapToCyberTwinSnapshot(
    learnerId: String,
    results: List<CapabilityAssessmentResult>,
    targetRole: String = "Senior SOC Analyst / Detection Engineer"
  ): CyberTwin60Snapshot

  fun mapDimensionExplainability(
    dimension: TwinDimensionV12,
    results: List<CapabilityAssessmentResult>
  ): DimensionExplainabilityV12
}

class DefaultCyberTwinAdapter : CyberTwinAdapter {

  override fun mapToCyberTwinSnapshot(
    learnerId: String,
    results: List<CapabilityAssessmentResult>,
    targetRole: String
  ): CyberTwin60Snapshot {
    val dimensionsMap = mutableMapOf<TwinDimensionV12, DimensionExplainabilityV12>()

    for (dim in TwinDimensionV12.values()) {
      dimensionsMap[dim] = mapDimensionExplainability(dim, results)
    }

    val overallScore = if (results.isNotEmpty()) {
      val avg = results.map { it.currentConfidence }.average().toInt()
      // Scale 0..100 to 0..2500 Cyber Twin ELO points
      (avg * 25).coerceIn(0, 2500)
    } else {
      1200
    }

    val passportLevel = when {
      overallScore >= 2200 -> SkillPassportLevel.L7_EXPERT
      overallScore >= 1800 -> SkillPassportLevel.L5_ADVANCED
      overallScore >= 1400 -> SkillPassportLevel.L4_SKILLED
      overallScore >= 1000 -> SkillPassportLevel.L3_PRACTITIONER
      else -> SkillPassportLevel.L1_BEGINNER
    }

    return CyberTwin60Snapshot(
      learnerId = learnerId,
      overallScore = overallScore,
      skillPassportLevel = passportLevel,
      dimensions = dimensionsMap,
      targetRole = targetRole,
      daysToTargetReadiness = computeDaysToReadiness(results),
      cognitiveLoadScore = 35,
      lastCalculatedLabel = "Demonstrated Capability Engine 1.0 verified (${results.size} capabilities evaluated)"
    )
  }

  override fun mapDimensionExplainability(
    dimension: TwinDimensionV12,
    results: List<CapabilityAssessmentResult>
  ): DimensionExplainabilityV12 {
    if (results.isEmpty()) {
      return DimensionExplainabilityV12(
        dimension = dimension,
        currentState = 50,
        trend = "Stable",
        evidenceCount = 0,
        evidenceQuality = 50,
        evidenceAgeDays = 0,
        confidence = 50,
        recentPerformance = "No active evidence recorded",
        weaknesses = listOf("No verified capability assessments"),
        strengths = emptyList(),
        decayRisk = DecayRiskLevel.LOW,
        transferability = "Unmeasured",
        recommendedAction = "Complete diagnostic baseline",
        isAlgorithmicEstimate = true
      )
    }

    // Map specific capability categories/gates to TwinDimensionV12
    val relevantScores = results.mapNotNull { res ->
      when (dimension) {
        TwinDimensionV12.KNOWLEDGE -> res.gateResults[MasteryGateType.UNDERSTAND]?.score
        TwinDimensionV12.RETENTION -> res.gateResults[MasteryGateType.RECALL]?.score
        TwinDimensionV12.PRACTICAL_ABILITY -> res.gateResults[MasteryGateType.APPLY]?.score
        TwinDimensionV12.INVESTIGATION -> res.gateResults[MasteryGateType.INVESTIGATE]?.score
        TwinDimensionV12.TRANSFERABILITY -> res.gateResults[MasteryGateType.TRANSFER]?.score
        TwinDimensionV12.COMMUNICATION -> res.gateResults[MasteryGateType.EXPLAIN]?.score
        TwinDimensionV12.CONFIDENCE_CALIBRATION -> res.gateResults[MasteryGateType.UNCERTAINTY_RESILIENCE]?.score
        TwinDimensionV12.INDEPENDENCE -> res.evidenceQualityBreakdown.independenceScore
        TwinDimensionV12.EVIDENCE_QUALITY -> res.evidenceQualityBreakdown.effectiveQualityScore
        else -> res.currentConfidence
      }
    }

    val state = if (relevantScores.isNotEmpty()) relevantScores.average().toInt() else 60
    val totalEvidence = results.sumOf { it.evidenceQualityBreakdown.totalConsidered }
    val avgQuality = results.map { it.evidenceQualityBreakdown.effectiveQualityScore }.average().toInt()
    val avgAge = results.map { it.daysSinceLastVerified }.average().toInt()

    val highestRisk = results.maxByOrNull { it.retentionRisk.level }?.retentionRisk ?: RetentionRisk.LOW
    val mappedDecayRisk = when (highestRisk) {
      RetentionRisk.CRITICAL -> DecayRiskLevel.CRITICAL
      RetentionRisk.HIGH -> DecayRiskLevel.HIGH
      RetentionRisk.MEDIUM -> DecayRiskLevel.MEDIUM
      RetentionRisk.LOW -> DecayRiskLevel.LOW
    }

    val weaknesses = results.flatMap { it.limitingFactors }.distinct().take(3)
    val strengths = results.flatMap { it.positiveFactors }.distinct().take(3)

    return DimensionExplainabilityV12(
      dimension = dimension,
      currentState = state.coerceIn(0, 100),
      trend = if (state >= 80) "+8% Demonstrated" else if (state < 50) "-5% Decay Alert" else "Stable",
      evidenceCount = totalEvidence,
      evidenceQuality = avgQuality.coerceIn(0, 100),
      evidenceAgeDays = avgAge,
      confidence = state.coerceIn(0, 100),
      recentPerformance = if (state >= 75) "Consistently passing 7-gate rubric" else "Bottlenecks detected in active gates",
      weaknesses = weaknesses.ifEmpty { listOf("Continue active evidence generation") },
      strengths = strengths.ifEmpty { listOf("Foundation established") },
      decayRisk = mappedDecayRisk,
      transferability = if (state >= 70) "High generalization" else "Local context only",
      recommendedAction = results.firstOrNull { it.limitingGate != null }?.recommendedAction ?: "Maintain verified challenge cadence",
      isAlgorithmicEstimate = false
    )
  }

  private fun computeDaysToReadiness(results: List<CapabilityAssessmentResult>): Int {
    val undemonstratedCount = results.count { !it.isDemonstrated }
    return (undemonstratedCount * 7).coerceIn(7, 90)
  }
}
