package com.example.capability

import com.example.data.DemonstratedCapabilityRepository
import com.example.data.db.CapabilityEntity
import com.example.data.db.CapabilityEvidenceEntity
import com.example.data.db.MasteryAssessmentEntity
import com.example.data.db.MasteryStatus
import kotlinx.coroutines.flow.first
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * AEGORA Demonstrated Capability Engine 1.0
 *
 * Deterministic calculation engine consuming Evidence artifacts and Mastery Assessments
 * to produce explainable, non-decaying peak capability states and recency-calibrated confidence.
 *
 * Architecture Pipeline:
 * Evidence -> Evidence Quality -> Mastery Assessment -> Seven Gates -> Independence -> Transfer -> Uncertainty -> Recency -> Current Confidence
 */
class DemonstratedCapabilityEngine(
  private val policy: MasteryPolicy = MasteryPolicy.DEFAULT
) {

  /**
   * Pure deterministic calculation of capability assessment result.
   * Throws [IllegalArgumentException] if learnerId boundaries are violated or inputs are invalid.
   */
  fun evaluateCapability(
    capability: CapabilityEntity,
    evidence: List<CapabilityEvidenceEntity>,
    assessments: List<MasteryAssessmentEntity>,
    currentTime: Long = System.currentTimeMillis(),
    customPolicy: MasteryPolicy = policy
  ): CapabilityAssessmentResult {
    require(capability.learnerId.isNotBlank()) { "Learner ID cannot be blank" }
    require(capability.skillKey.isNotBlank()) { "Skill Key cannot be blank" }

    // Strict learner isolation check
    for (ev in evidence) {
      require(ev.learnerId == capability.learnerId) {
        "Cross-learner evidence isolation violation: expected ${capability.learnerId}, found ${ev.learnerId}"
      }
    }
    for (asmt in assessments) {
      require(asmt.learnerId == capability.learnerId) {
        "Cross-learner assessment isolation violation: expected ${capability.learnerId}, found ${asmt.learnerId}"
      }
    }

    // 1. Evidence Quality & Independence Evaluation
    val evidenceMetrics = EvidenceQualityEngine.evaluateEvidence(evidence, customPolicy)

    // 2. Latest Mastery Assessment
    val latestAssessment = assessments.maxByOrNull { it.createdAt }

    // 3. Seven Mastery Gates Evaluation
    val gatesOutput = SevenMasteryGatesEngine.evaluateGates(
      capability = capability,
      latestAssessment = latestAssessment,
      evidenceMetrics = evidenceMetrics,
      policy = customPolicy
    )

    // 4. Weighted Aggregate Mastery Score Calculation
    val understandScore = gatesOutput.gateResults[MasteryGateType.UNDERSTAND]?.score ?: 0
    val recallScore = gatesOutput.gateResults[MasteryGateType.RECALL]?.score ?: 0
    val applyScore = gatesOutput.gateResults[MasteryGateType.APPLY]?.score ?: 0
    val investigateScore = gatesOutput.gateResults[MasteryGateType.INVESTIGATE]?.score ?: 0
    val transferScore = gatesOutput.gateResults[MasteryGateType.TRANSFER]?.score ?: 0
    val explainScore = gatesOutput.gateResults[MasteryGateType.EXPLAIN]?.score ?: 0
    val uncertaintyScore = gatesOutput.gateResults[MasteryGateType.UNCERTAINTY_RESILIENCE]?.score ?: 0

    val rawWeightedScore = (
      understandScore * customPolicy.understandWeight +
        recallScore * customPolicy.recallWeight +
        applyScore * customPolicy.applyWeight +
        investigateScore * customPolicy.investigateWeight +
        transferScore * customPolicy.transferWeight +
        explainScore * customPolicy.explainWeight +
        uncertaintyScore * customPolicy.uncertaintyResilienceWeight
      ).roundToInt()

    // 5. Recency and Decay Analysis
    val decayOutput = DecayRecencyEngine.computeDecay(
      historicalCapabilityScore = capability.historicalCapabilityScore,
      rawGateConfidence = rawWeightedScore,
      lastVerifiedAt = capability.lastVerifiedAt,
      limitingGate = gatesOutput.limitingGate,
      currentTime = currentTime,
      policy = customPolicy
    )

    // 6. Historical Peak Capability Update (Non-decaying baseline)
    val shouldPromoteHistorical = gatesOutput.allCriticalGatesPassed &&
      rawWeightedScore >= customPolicy.minDemonstrationScore &&
      evidenceMetrics.breakdown.effectiveQualityScore >= customPolicy.minEvidenceQuality

    val calculatedHistoricalScore = if (shouldPromoteHistorical) {
      max(capability.historicalCapabilityScore, rawWeightedScore)
    } else {
      capability.historicalCapabilityScore
    }

    val isDemonstrated = calculatedHistoricalScore >= customPolicy.minDemonstrationScore &&
      gatesOutput.allCriticalGatesPassed

    // 7. Overall Mastery Status
    val overallStatus = when {
      isDemonstrated -> MasteryStatus.DEMONSTRATED
      gatesOutput.overallGatePassingRatio >= 0.50 -> MasteryStatus.IN_PROGRESS
      decayOutput.retentionRisk == RetentionRisk.CRITICAL -> MasteryStatus.REQUIRES_REASSESSMENT
      else -> MasteryStatus.NOT_STARTED
    }

    // 8. Deterministic "Why This Score?" Explainability Summary
    val whyThisScore = buildWhyThisScoreExplanation(
      capabilityName = capability.name,
      historicalScore = calculatedHistoricalScore,
      currentConfidence = decayOutput.currentConfidence,
      status = overallStatus,
      passedGatesCount = gatesOutput.gateResults.values.count { it.isPassed },
      totalGatesCount = gatesOutput.gateResults.size,
      limitingGate = gatesOutput.limitingGate,
      evidenceBreakdown = evidenceMetrics.breakdown,
      retentionRisk = decayOutput.retentionRisk,
      daysSinceVerified = decayOutput.daysSinceLastVerified
    )

    return CapabilityAssessmentResult(
      capabilityId = capability.id,
      learnerId = capability.learnerId,
      skillKey = capability.skillKey,
      name = capability.name,
      category = capability.category,
      historicalCapabilityScore = calculatedHistoricalScore,
      currentConfidence = decayOutput.currentConfidence,
      isDemonstrated = isDemonstrated,
      overallMasteryStatus = overallStatus,
      gateResults = gatesOutput.gateResults,
      limitingGate = gatesOutput.limitingGate,
      evidenceQualityBreakdown = evidenceMetrics.breakdown,
      retentionRisk = decayOutput.retentionRisk,
      daysSinceLastVerified = decayOutput.daysSinceLastVerified,
      limitingFactors = gatesOutput.limitingFactors,
      positiveFactors = gatesOutput.positiveFactors,
      recommendedAction = decayOutput.recommendedAction,
      whyThisScore = whyThisScore,
      calculatedAt = currentTime
    )
  }

  /**
   * Evaluates and updates a single capability in the Room persistence repository.
   */
  suspend fun evaluateAndPersist(
    capabilityId: String,
    repository: DemonstratedCapabilityRepository,
    currentTime: Long = System.currentTimeMillis(),
    customPolicy: MasteryPolicy = policy
  ): CapabilityAssessmentResult? {
    val fullMastery = repository.getCapabilityWithFullMastery(capabilityId).first() ?: return null
    val cap = fullMastery.capability
    val evidence = fullMastery.evidenceList
    val assessments = fullMastery.assessments

    val result = evaluateCapability(
      capability = cap,
      evidence = evidence,
      assessments = assessments,
      currentTime = currentTime,
      customPolicy = customPolicy
    )

    // Persist updated scores back to CapabilityEntity
    val updatedEntity = cap.copy(
      knowledgeScore = result.gateResults[MasteryGateType.UNDERSTAND]?.score ?: cap.knowledgeScore,
      recallScore = result.gateResults[MasteryGateType.RECALL]?.score ?: cap.recallScore,
      applicationScore = result.gateResults[MasteryGateType.APPLY]?.score ?: cap.applicationScore,
      investigationScore = result.gateResults[MasteryGateType.INVESTIGATE]?.score ?: cap.investigationScore,
      transferScore = result.gateResults[MasteryGateType.TRANSFER]?.score ?: cap.transferScore,
      explanationScore = result.gateResults[MasteryGateType.EXPLAIN]?.score ?: cap.explanationScore,
      uncertaintyResilienceScore = result.gateResults[MasteryGateType.UNCERTAINTY_RESILIENCE]?.score ?: cap.uncertaintyResilienceScore,
      independenceScore = result.evidenceQualityBreakdown.independenceScore,
      evidenceQualityScore = result.evidenceQualityBreakdown.effectiveQualityScore,
      currentConfidence = result.currentConfidence,
      historicalCapabilityScore = result.historicalCapabilityScore,
      lastVerifiedAt = if (result.isDemonstrated && result.evidenceQualityBreakdown.verifiedCount > 0) currentTime else cap.lastVerifiedAt,
      updatedAt = currentTime
    )

    repository.saveCapability(updatedEntity)
    return result
  }

  /**
   * Evaluates all capabilities for a learner and returns their structured assessment results.
   */
  suspend fun evaluateAllForLearner(
    learnerId: String,
    repository: DemonstratedCapabilityRepository,
    currentTime: Long = System.currentTimeMillis(),
    customPolicy: MasteryPolicy = policy
  ): List<CapabilityAssessmentResult> {
    require(learnerId.isNotBlank()) { "Learner ID cannot be blank" }

    val capabilities = repository.observeCapabilitiesForLearner(learnerId).first()
    return capabilities.mapNotNull { cap ->
      val evidence = repository.getEvidenceByCapabilityId(cap.id)
      val assessments = repository.getAssessmentsByCapabilityId(cap.id)
      evaluateCapability(
        capability = cap,
        evidence = evidence,
        assessments = assessments,
        currentTime = currentTime,
        customPolicy = customPolicy
      )
    }
  }

  private fun buildWhyThisScoreExplanation(
    capabilityName: String,
    historicalScore: Int,
    currentConfidence: Int,
    status: MasteryStatus,
    passedGatesCount: Int,
    totalGatesCount: Int,
    limitingGate: MasteryGateType?,
    evidenceBreakdown: EvidenceQualityBreakdown,
    retentionRisk: RetentionRisk,
    daysSinceVerified: Int
  ): String {
    val statusLabel = when (status) {
      MasteryStatus.DEMONSTRATED -> "DEMONSTRATED (All 7 gates satisfied)"
      MasteryStatus.PROVISIONAL -> "PROVISIONAL (Requires additional verified trials)"
      MasteryStatus.IN_PROGRESS -> "IN PROGRESS ($passedGatesCount/$totalGatesCount gates passed)"
      MasteryStatus.REQUIRES_REASSESSMENT -> "REQUIRES REASSESSMENT (High retention decay)"
      MasteryStatus.NOT_STARTED -> "NOT STARTED (Insufficient verified evidence)"
    }

    val bottleneck = if (limitingGate != null) {
      "Primary Bottleneck: ${limitingGate.displayName} Gate."
    } else {
      "No limiting bottleneck detected."
    }

    return "Capability '$capabilityName' evaluated at $currentConfidence% current confidence with $historicalScore% peak historical baseline. " +
      "Status: $statusLabel. Verified Artifacts: ${evidenceBreakdown.verifiedCount} (Quality: ${evidenceBreakdown.effectiveQualityScore}%, Independence: ${evidenceBreakdown.independenceScore}%). " +
      "$bottleneck Retention Risk: ${retentionRisk.label} ($daysSinceVerified days elapsed)."
  }
}
