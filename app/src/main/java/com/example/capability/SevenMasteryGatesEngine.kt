package com.example.capability

import com.example.data.db.CapabilityEntity
import com.example.data.db.MasteryAssessmentEntity
import com.example.data.db.MasteryStatus
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Deterministic engine for evaluating the Seven Mastery Gates and isolating limiting bottlenecks.
 */
object SevenMasteryGatesEngine {

  data class GatesEvaluationOutput(
    val gateResults: Map<MasteryGateType, GateResult>,
    val limitingGate: MasteryGateType?,
    val overallGatePassingRatio: Double,
    val allCriticalGatesPassed: Boolean,
    val limitingFactors: List<String>,
    val positiveFactors: List<String>
  )

  fun evaluateGates(
    capability: CapabilityEntity,
    latestAssessment: MasteryAssessmentEntity?,
    evidenceMetrics: EvidenceQualityEngine.EvaluatedEvidenceMetrics,
    policy: MasteryPolicy = MasteryPolicy.DEFAULT
  ): GatesEvaluationOutput {
    val results = mutableMapOf<MasteryGateType, GateResult>()
    val limitingFactors = mutableListOf<String>()
    val positiveFactors = mutableListOf<String>()

    // 1. UNDERSTAND GATE
    val understandScore = computeGateScore(
      capability.knowledgeScore,
      latestAssessment?.understandScore,
      evidenceMetrics.aggregateReasoningScore
    )
    val understandPassed = understandScore >= policy.minGatePassingScore
    results[MasteryGateType.UNDERSTAND] = GateResult(
      gate = MasteryGateType.UNDERSTAND,
      score = understandScore,
      isPassed = understandPassed,
      masteryStatus = if (understandPassed) MasteryStatus.DEMONSTRATED else MasteryStatus.IN_PROGRESS,
      evidenceCount = evidenceMetrics.validEvidenceCount,
      confidence = understandScore,
      limitingFactor = if (!understandPassed) "Theoretical foundation insufficient ($understandScore < ${policy.minGatePassingScore})" else null,
      positiveFactor = if (understandPassed) "Solid theoretical and structural knowledge demonstrated ($understandScore%)" else null
    )
    if (!understandPassed) limitingFactors.add("Understand Gate: Core theoretical principles need reinforcement.")
    else positiveFactors.add("Understand Gate: Strong structural grasp of security mechanisms.")

    // 2. RECALL GATE
    val recallScore = computeGateScore(
      capability.recallScore,
      latestAssessment?.recallScore,
      evidenceMetrics.aggregateOutcomeScore
    )
    val recallPassed = recallScore >= policy.minGatePassingScore
    results[MasteryGateType.RECALL] = GateResult(
      gate = MasteryGateType.RECALL,
      score = recallScore,
      isPassed = recallPassed,
      masteryStatus = if (recallPassed) MasteryStatus.DEMONSTRATED else MasteryStatus.IN_PROGRESS,
      evidenceCount = evidenceMetrics.validEvidenceCount,
      confidence = recallScore,
      limitingFactor = if (!recallPassed) "Active recall speed/accuracy below threshold ($recallScore < ${policy.minGatePassingScore})" else null,
      positiveFactor = if (recallPassed) "High-fidelity rapid recall verified ($recallScore%)" else null
    )
    if (!recallPassed) limitingFactors.add("Recall Gate: Fluency and rapid lookup-free recall below benchmark.")
    else positiveFactors.add("Recall Gate: Instant, accurate conceptual retrieval verified.")

    // 3. APPLY GATE (Practical execution)
    val applyScore = computeGateScore(
      capability.applicationScore,
      latestAssessment?.applyScore,
      evidenceMetrics.aggregateOutcomeScore,
      weightEvidence = 0.50
    )
    val applyPassed = applyScore >= policy.minGatePassingScore
    results[MasteryGateType.APPLY] = GateResult(
      gate = MasteryGateType.APPLY,
      score = applyScore,
      isPassed = applyPassed,
      masteryStatus = if (applyPassed) MasteryStatus.DEMONSTRATED else MasteryStatus.IN_PROGRESS,
      evidenceCount = evidenceMetrics.validEvidenceCount,
      confidence = applyScore,
      limitingFactor = if (!applyPassed) "Hands-on lab execution score insufficient ($applyScore < ${policy.minGatePassingScore})" else null,
      positiveFactor = if (applyPassed) "Practical lab execution benchmark reached ($applyScore%)" else null
    )
    if (!applyPassed) limitingFactors.add("Apply Gate: Hands-on lab execution requires additional verified completions.")
    else positiveFactors.add("Apply Gate: Practical application and tool execution proven in realistic labs.")

    // 4. INVESTIGATE GATE (Root cause & triage)
    val investigateScore = computeGateScore(
      capability.investigationScore,
      latestAssessment?.investigateScore,
      evidenceMetrics.aggregateQualityScore,
      weightEvidence = 0.45
    )
    val investigatePassed = investigateScore >= policy.minGatePassingScore
    results[MasteryGateType.INVESTIGATE] = GateResult(
      gate = MasteryGateType.INVESTIGATE,
      score = investigateScore,
      isPassed = investigatePassed,
      masteryStatus = if (investigatePassed) MasteryStatus.DEMONSTRATED else MasteryStatus.IN_PROGRESS,
      evidenceCount = evidenceMetrics.validEvidenceCount,
      confidence = investigateScore,
      limitingFactor = if (!investigatePassed) "Investigation and telemetry hunting score is below standard ($investigateScore < ${policy.minGatePassingScore})" else null,
      positiveFactor = if (investigatePassed) "Deep investigative anomaly deduction confirmed ($investigateScore%)" else null
    )
    if (!investigatePassed) limitingFactors.add("Investigate Gate: Telemetry triage and artifact root cause deduction needs practice.")
    else positiveFactors.add("Investigate Gate: High-fidelity log analysis and threat hunting demonstrated.")

    // 5. TRANSFER GATE (Cross-context generalization)
    val transferScore = computeGateScore(
      capability.transferScore,
      latestAssessment?.transferScore,
      evidenceMetrics.aggregateTransferScore,
      weightEvidence = 0.50
    )
    val transferPassed = transferScore >= policy.minTransferScore
    results[MasteryGateType.TRANSFER] = GateResult(
      gate = MasteryGateType.TRANSFER,
      score = transferScore,
      isPassed = transferPassed,
      masteryStatus = if (transferPassed) MasteryStatus.DEMONSTRATED else MasteryStatus.IN_PROGRESS,
      evidenceCount = evidenceMetrics.validEvidenceCount,
      confidence = transferScore,
      limitingFactor = if (!transferPassed) "Transfer across novel domains/architectures unproven ($transferScore < ${policy.minTransferScore})" else null,
      positiveFactor = if (transferPassed) "Proven capability transfer across multiple mission contexts ($transferScore%)" else null
    )
    if (!transferPassed) limitingFactors.add("Transfer Gate: Skill not yet demonstrated across varied architecture contexts.")
    else positiveFactors.add("Transfer Gate: Robust multi-scenario transferability proven.")

    // 6. EXPLAIN GATE (Reporting & communication)
    val explainScore = computeGateScore(
      capability.explanationScore,
      latestAssessment?.explainScore,
      evidenceMetrics.aggregateReasoningScore
    )
    val explainPassed = explainScore >= policy.minGatePassingScore
    results[MasteryGateType.EXPLAIN] = GateResult(
      gate = MasteryGateType.EXPLAIN,
      score = explainScore,
      isPassed = explainPassed,
      masteryStatus = if (explainPassed) MasteryStatus.DEMONSTRATED else MasteryStatus.IN_PROGRESS,
      evidenceCount = evidenceMetrics.validEvidenceCount,
      confidence = explainScore,
      limitingFactor = if (!explainPassed) "Executive/technical explanation score below threshold ($explainScore < ${policy.minGatePassingScore})" else null,
      positiveFactor = if (explainPassed) "Clear, structured technical explanation verified ($explainScore%)" else null
    )
    if (!explainPassed) limitingFactors.add("Explain Gate: Technical communication and business impact reporting need refinement.")
    else positiveFactors.add("Explain Gate: Articulate technical reasoning and impact synthesis confirmed.")

    // 7. UNCERTAINTY RESILIENCE GATE
    val uncertaintyScore = computeGateScore(
      capability.uncertaintyResilienceScore,
      latestAssessment?.uncertaintyResilienceScore,
      evidenceMetrics.aggregateUncertaintyScore,
      weightEvidence = 0.40
    )
    val uncertaintyPassed = uncertaintyScore >= policy.minUncertaintyScore
    results[MasteryGateType.UNCERTAINTY_RESILIENCE] = GateResult(
      gate = MasteryGateType.UNCERTAINTY_RESILIENCE,
      score = uncertaintyScore,
      isPassed = uncertaintyPassed,
      masteryStatus = if (uncertaintyPassed) MasteryStatus.DEMONSTRATED else MasteryStatus.IN_PROGRESS,
      evidenceCount = evidenceMetrics.validEvidenceCount,
      confidence = uncertaintyScore,
      limitingFactor = if (!uncertaintyPassed) "Decision confidence calibration amidst ambiguity below threshold ($uncertaintyScore < ${policy.minUncertaintyScore})" else null,
      positiveFactor = if (uncertaintyPassed) "Resilient reasoning and honest uncertainty calibration verified ($uncertaintyScore%)" else null
    )
    if (!uncertaintyPassed) limitingFactors.add("Uncertainty Resilience Gate: Decision calibration under conflicting data requires training.")
    else positiveFactors.add("Uncertainty Resilience Gate: Calibrated decision-making under ambiguity proven.")

    // Identify primary bottleneck / limiting gate (lowest score among failed gates, or lowest overall)
    val failedGates = results.values.filter { !it.isPassed }
    val limitingGate = if (failedGates.isNotEmpty()) {
      failedGates.minByOrNull { it.score }?.gate
    } else null

    val passedCount = results.values.count { it.isPassed }
    val passingRatio = passedCount.toDouble() / results.size
    val allCriticalPassed = passedCount == results.size &&
      evidenceMetrics.verifiedCount >= policy.minVerifiedEvidenceCount &&
      evidenceMetrics.breakdown.independenceScore >= policy.minIndependenceScore &&
      evidenceMetrics.breakdown.effectiveQualityScore >= policy.minEvidenceQuality

    return GatesEvaluationOutput(
      gateResults = results,
      limitingGate = limitingGate,
      overallGatePassingRatio = passingRatio,
      allCriticalGatesPassed = allCriticalPassed,
      limitingFactors = limitingFactors,
      positiveFactors = positiveFactors
    )
  }

  private fun computeGateScore(
    baseScore: Int,
    assessmentScore: Int?,
    evidenceMetricScore: Int,
    weightEvidence: Double = 0.30
  ): Int {
    return if (assessmentScore != null && assessmentScore > 0) {
      val directWeight = 1.0 - weightEvidence
      val combined = (assessmentScore * 0.60 + baseScore * 0.40) * directWeight + (evidenceMetricScore * weightEvidence)
      min(100, max(0, combined.roundToInt()))
    } else if (evidenceMetricScore > 0 && baseScore > 0) {
      val combined = (baseScore * (1.0 - weightEvidence)) + (evidenceMetricScore * weightEvidence)
      min(100, max(0, combined.roundToInt()))
    } else {
      min(100, max(0, max(baseScore, evidenceMetricScore)))
    }
  }
}
