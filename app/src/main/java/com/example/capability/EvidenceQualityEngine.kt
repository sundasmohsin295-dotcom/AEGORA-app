package com.example.capability

import com.example.data.db.CapabilityEvidenceEntity
import com.example.data.db.VerificationStatus
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Deterministic engine for analyzing evidence artifacts, verification authenticity,
 * independence penalties (hints/retries), and diversity diminishing returns.
 */
object EvidenceQualityEngine {

  data class EvaluatedEvidenceMetrics(
    val breakdown: EvidenceQualityBreakdown,
    val aggregateQualityScore: Int,
    val aggregateIndependenceScore: Int,
    val aggregateTransferScore: Int,
    val aggregateReasoningScore: Int,
    val aggregateOutcomeScore: Int,
    val aggregateUncertaintyScore: Int,
    val validEvidenceCount: Int,
    val verifiedCount: Int
  )

  fun evaluateEvidence(
    evidenceList: List<CapabilityEvidenceEntity>,
    policy: MasteryPolicy = MasteryPolicy.DEFAULT
  ): EvaluatedEvidenceMetrics {
    if (evidenceList.isEmpty()) {
      return EvaluatedEvidenceMetrics(
        breakdown = EvidenceQualityBreakdown(
          effectiveQualityScore = 0,
          authenticityScore = 0,
          independenceScore = 0,
          transferabilityScore = 0,
          diversityFactor = 1.0,
          verifiedCount = 0,
          pendingCount = 0,
          unverifiedCount = 0,
          revokedExcludedCount = 0,
          totalConsidered = 0
        ),
        aggregateQualityScore = 0,
        aggregateIndependenceScore = 0,
        aggregateTransferScore = 0,
        aggregateReasoningScore = 0,
        aggregateOutcomeScore = 0,
        aggregateUncertaintyScore = 0,
        validEvidenceCount = 0,
        verifiedCount = 0
      )
    }

    var revokedCount = 0
    var verifiedCount = 0
    var pendingCount = 0
    var unverifiedCount = 0

    val validList = mutableListOf<CapabilityEvidenceEntity>()

    for (ev in evidenceList) {
      when (ev.verificationStatus) {
        VerificationStatus.REVOKED.name, VerificationStatus.INVALID.name -> {
          revokedCount++
        }
        VerificationStatus.VERIFIED.name -> {
          verifiedCount++
          validList.add(ev)
        }
        VerificationStatus.UNVERIFIED.name -> {
          unverifiedCount++
          validList.add(ev)
        }
        else -> {
          unverifiedCount++
          validList.add(ev)
        }
      }
    }

    if (validList.isEmpty()) {
      return EvaluatedEvidenceMetrics(
        breakdown = EvidenceQualityBreakdown(
          effectiveQualityScore = 0,
          authenticityScore = 0,
          independenceScore = 0,
          transferabilityScore = 0,
          diversityFactor = 1.0,
          verifiedCount = verifiedCount,
          pendingCount = 0,
          unverifiedCount = unverifiedCount,
          revokedExcludedCount = revokedCount,
          totalConsidered = evidenceList.size
        ),
        aggregateQualityScore = 0,
        aggregateIndependenceScore = 0,
        aggregateTransferScore = 0,
        aggregateReasoningScore = 0,
        aggregateOutcomeScore = 0,
        aggregateUncertaintyScore = 0,
        validEvidenceCount = 0,
        verifiedCount = verifiedCount
      )
    }

    // Calculate Diversity Diminishing Returns per mission/task key
    val missionOccurrenceMap = mutableMapOf<String, Int>()
    var totalWeightedQuality = 0.0
    var totalWeightedIndependence = 0.0
    var totalWeightedTransfer = 0.0
    var totalWeightedReasoning = 0.0
    var totalWeightedOutcome = 0.0
    var totalWeightedUncertainty = 0.0
    var totalAuthenticity = 0.0
    var totalWeight = 0.0
    var totalDiversityFactor = 0.0

    for (ev in validList) {
      val key = "${ev.missionId ?: "generic"}_${ev.evidenceType}"
      val occurrence = missionOccurrenceMap.getOrDefault(key, 0) + 1
      missionOccurrenceMap[key] = occurrence

      // Diminishing returns: 1.0 for first attempt, 1/sqrt(2) for second, etc.
      val diversityWeight = 1.0 / sqrt(occurrence.toDouble())
      totalDiversityFactor += diversityWeight

      val statusMultiplier = when (ev.verificationStatus) {
        VerificationStatus.VERIFIED.name -> policy.verifiedMultiplier
        else -> policy.unverifiedMultiplier
      }

      val authFactor = (ev.authenticityScore / 100.0).coerceIn(0.1, 1.0)
      val effectiveWeight = statusMultiplier * diversityWeight * authFactor

      // Independence penalty calculation: deduct for hints and retries
      val hintDeduction = ev.hintsUsed * policy.hintPenaltyPerUnit
      val retryDeduction = ev.retries * policy.retryPenaltyPerUnit
      val adjustedIndependence = max(0.0, ev.independenceScore - hintDeduction - retryDeduction)

      // Epistemic calibration: difference between declared and calibrated confidence
      val calibrationGap = kotlin.math.abs(ev.confidenceDeclared - ev.confidenceCalibrated)
      val uncertaintyScore = max(0.0, 100.0 - (calibrationGap * 1.5))

      totalWeightedQuality += ev.evidenceQualityScore * effectiveWeight
      totalWeightedIndependence += adjustedIndependence * effectiveWeight
      totalWeightedTransfer += ev.transferabilityScore * effectiveWeight
      totalWeightedReasoning += ev.reasoningQualityScore * effectiveWeight
      totalWeightedOutcome += ev.outcomeScore * effectiveWeight
      totalWeightedUncertainty += uncertaintyScore * effectiveWeight
      totalAuthenticity += ev.authenticityScore * effectiveWeight
      totalWeight += effectiveWeight
    }

    val safeWeight = if (totalWeight > 0.0) totalWeight else 1.0
    val meanDiversity = totalDiversityFactor / validList.size

    val finalQuality = min(100, max(0, (totalWeightedQuality / safeWeight).roundToInt()))
    val finalIndependence = min(100, max(0, (totalWeightedIndependence / safeWeight).roundToInt()))
    val finalTransfer = min(100, max(0, (totalWeightedTransfer / safeWeight).roundToInt()))
    val finalReasoning = min(100, max(0, (totalWeightedReasoning / safeWeight).roundToInt()))
    val finalOutcome = min(100, max(0, (totalWeightedOutcome / safeWeight).roundToInt()))
    val finalUncertainty = min(100, max(0, (totalWeightedUncertainty / safeWeight).roundToInt()))
    val finalAuthenticity = min(100, max(0, (totalAuthenticity / safeWeight).roundToInt()))

    val breakdown = EvidenceQualityBreakdown(
      effectiveQualityScore = finalQuality,
      authenticityScore = finalAuthenticity,
      independenceScore = finalIndependence,
      transferabilityScore = finalTransfer,
      diversityFactor = (meanDiversity * 100.0).roundToInt() / 100.0,
      verifiedCount = verifiedCount,
      pendingCount = pendingCount,
      unverifiedCount = unverifiedCount,
      revokedExcludedCount = revokedCount,
      totalConsidered = evidenceList.size
    )

    return EvaluatedEvidenceMetrics(
      breakdown = breakdown,
      aggregateQualityScore = finalQuality,
      aggregateIndependenceScore = finalIndependence,
      aggregateTransferScore = finalTransfer,
      aggregateReasoningScore = finalReasoning,
      aggregateOutcomeScore = finalOutcome,
      aggregateUncertaintyScore = finalUncertainty,
      validEvidenceCount = validList.size,
      verifiedCount = verifiedCount
    )
  }
}
