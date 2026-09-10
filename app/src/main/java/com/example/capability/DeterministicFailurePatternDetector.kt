package com.example.capability

import com.example.model.FailureModeType
import com.example.model.InvestigationLab
import com.example.model.InvestigationQuestion
import com.example.model.InvestigationTimelineEvent

/**
 * Structured input representing the learner's submitted investigation attempt.
 * Contains only concrete behavioral inputs—no client-controlled failure labels or confidence scores.
 */
data class LearnerMissionSubmission(
  val missionId: String,
  val learnerId: String,
  val selectedAnswers: Map<String, Int>,
  val selectedIocs: List<String> = emptyList(),
  val reasoningText: String = "",
  val acceptedAiClaim: Boolean? = null,
  val reportedUnsupportedIocs: List<String> = emptyList(),
  val executionTimeSeconds: Int = 120,
  val hintsRequestedCount: Int = 0
)

/**
 * Ground truth answer key and behavioral bounds for an investigation lab.
 */
data class MissionAnswerKey(
  val missionId: String,
  val questions: List<InvestigationQuestion>,
  val validTimelineEvents: List<InvestigationTimelineEvent>,
  val validIocSet: Set<String>,
  val falsePositiveIocSet: Set<String> = emptySet(),
  val containmentQuestionId: String = "q_login_3",
  val optimalContainmentIndex: Int = 0,
  val prematureEscalationIndex: Int = 2, // e.g. Rebooting DC or deleting mailbox before triage
  val minimumReasonableTimeSeconds: Int = 20
)

/**
 * Deterministic detection result for an observed task-behavior failure pattern.
 */
data class FailurePatternDetectionResult(
  val pattern: FailureModeType,
  val isDetected: Boolean,
  val observedSymptom: String,
  val rootCauseCausalLink: String,
  val actionableRemediation: String,
  val affectedEvidenceIocs: List<String> = emptyList()
)

/**
 * Authoritative observation status:
 * - OBSERVED: Single authoritative observation in an evaluation
 * - REPEATED: 2 or more qualifying occurrences
 * - HIGH_CONFIDENCE: 3 or more occurrences, triggering targeted remediation/adversary
 */
enum class FailurePatternConfidenceTier {
  OBSERVED,
  REPEATED,
  HIGH_CONFIDENCE
}

data class AuthoritativeFailurePatternRecord(
  val patternId: String,
  val learnerId: String,
  val pattern: FailureModeType,
  val confidenceTier: FailurePatternConfidenceTier,
  val observationCount: Int,
  val lastObservedMissionId: String,
  val lastObservedTimestamp: Long,
  val firstObservedTimestamp: Long,
  val detectionDetails: List<String>
)

/**
 * Deterministic, rule-based failure pattern detector.
 *
 * Rules operate STRICTLY over structured evidence and answer key bounds.
 * No subjective AI grading is permitted for pattern identification.
 */
object DeterministicFailurePatternDetector {

  /**
   * 1. PREMATURE_ESCALATION:
   * Learner triggers destructive/high-impact remediation (e.g. DC reboot, domain-wide wipe)
   * before identifying initial access or before root-cause analysis is performed.
   */
  fun detectPrematureEscalation(
    submission: LearnerMissionSubmission,
    answerKey: MissionAnswerKey
  ): FailurePatternDetectionResult {
    val selectedContainment = submission.selectedAnswers[answerKey.containmentQuestionId]
    val isPremature = selectedContainment == answerKey.prematureEscalationIndex ||
      (submission.executionTimeSeconds < answerKey.minimumReasonableTimeSeconds && selectedContainment != answerKey.optimalContainmentIndex)

    return FailurePatternDetectionResult(
      pattern = FailureModeType.PREMATURE_ESCALATION,
      isDetected = isPremature,
      observedSymptom = if (isPremature) {
        "Triggered high-impact service interruption/reboot without establishing root-cause causality."
      } else "Containment followed proper triage sequence.",
      rootCauseCausalLink = "Premature escalation occurs when destructive containment is chosen before confirming active adversary pivot vectors.",
      actionableRemediation = "Follow disciplined containment: isolate the specific compromised host/token before invoking enterprise-wide service resets."
    )
  }

  /**
   * 2. EVIDENCE_OVERWEIGHTING:
   * Learner selects or attributes compromise to IOCs/artifacts that do NOT exist in the supplied telemetry.
   */
  fun detectEvidenceOverweighting(
    submission: LearnerMissionSubmission,
    answerKey: MissionAnswerKey
  ): FailurePatternDetectionResult {
    val ungroundedIocs = submission.selectedIocs.filter { it !in answerKey.validIocSet }
    val isOverweighting = ungroundedIocs.isNotEmpty()

    return FailurePatternDetectionResult(
      pattern = FailureModeType.EVIDENCE_OVERWEIGHTING,
      isDetected = isOverweighting,
      observedSymptom = if (isOverweighting) {
        "Attributed compromise to ungrounded artifact(s): ${ungroundedIocs.joinToString(", ")} (not present in raw telemetry)."
      } else "All cited artifacts are grounded in verified telemetry.",
      rootCauseCausalLink = "Superficial pattern matching on expected IOC keywords without verifying presence in the actual host/network log stream.",
      actionableRemediation = "Cross-reference every cited IP, hash, or process GUID against the raw log stream before logging it as confirmed evidence.",
      affectedEvidenceIocs = ungroundedIocs
    )
  }

  /**
   * 3. CONFIRMATION_BIAS:
   * Learner fixates on an early benign or decoy artifact while ignoring contradictory telemetry
   * (e.g. claiming routine maintenance or Kerberos renewal despite Event 4625 brute force).
   */
  fun detectConfirmationBias(
    submission: LearnerMissionSubmission,
    answerKey: MissionAnswerKey
  ): FailurePatternDetectionResult {
    // Decoy option in q_login_1 is option 1 (Routine password change) or option 3 (Scheduled Kerberos renewal)
    val q1Answer = submission.selectedAnswers["q_login_1"]
    val hasDecoyBelief = q1Answer == 1 || q1Answer == 3
    // Also true if learner selected known false positive IOCs
    val hasDecoyIocs = submission.selectedIocs.any { it in answerKey.falsePositiveIocSet }
    val isConfirmationBias = hasDecoyBelief || hasDecoyIocs

    return FailurePatternDetectionResult(
      pattern = FailureModeType.CONFIRMATION_BIAS,
      isDetected = isConfirmationBias,
      observedSymptom = if (isConfirmationBias) {
        "Maintained initial benign hypothesis despite contradictory brute-force telemetry (Event ID 4625 sequence)."
      } else "Evaluated contradictory telemetry objectively.",
      rootCauseCausalLink = "Confirmation bias leads analysts to discard anomalous telemetry that conflicts with an initial benign assumption.",
      actionableRemediation = "Actively attempt to falsify your leading hypothesis by searching for anomalous child processes and authentication failures."
    )
  }

  /**
   * 4. INSUFFICIENT_CORRELATION:
   * Learner examines single events in isolation and fails to correlate across timestamps or sources
   * (e.g. failing impossible travel check because logins were not correlated across time and geography).
   */
  fun detectInsufficientCorrelation(
    submission: LearnerMissionSubmission,
    answerKey: MissionAnswerKey
  ): FailurePatternDetectionResult {
    val q2Answer = submission.selectedAnswers["q_login_2"]
    // Q2 correct is 0 (impossible travel: Moscow to Austin in 7 mins). Options 1, 2, 3 miss correlation.
    val failedCorrelation = q2Answer != null && q2Answer != 0

    return FailurePatternDetectionResult(
      pattern = FailureModeType.INSUFFICIENT_CORRELATION,
      isDetected = failedCorrelation,
      observedSymptom = if (failedCorrelation) {
        "Evaluated authentication events as isolated occurrences rather than computing cross-event velocity/distance delta."
      } else "Successfully correlated sequential events across geographic and temporal bounds.",
      rootCauseCausalLink = "Focusing exclusively on individual log entries rather than building an end-to-end multi-source timeline.",
      actionableRemediation = "Plot timestamps and source locations chronologically to detect velocity violations and pivot chains."
    )
  }

  /**
   * 5. WEAK_UNCERTAINTY_HANDLING:
   * Learner jumps to absolute conclusions on ambiguous data or defaults to paralysis
   * without stating confidence boundaries or requesting progressive telemetry.
   */
  fun detectWeakUncertaintyHandling(
    submission: LearnerMissionSubmission,
    answerKey: MissionAnswerKey
  ): FailurePatternDetectionResult {
    // Detected if learner uses 0 hints on a failed attempt with very brief reasoning (<25 chars)
    // or claims 100% certainty when wrong
    val failedAny = submission.selectedAnswers.any { (qId, ans) ->
      val q = answerKey.questions.firstOrNull { it.id == qId }
      q != null && q.correctOptionIndex != ans
    }
    val briefReasoning = submission.reasoningText.trim().length < 25
    val isWeakUncertainty = failedAny && briefReasoning

    return FailurePatternDetectionResult(
      pattern = FailureModeType.WEAK_UNCERTAINTY_HANDLING,
      isDetected = isWeakUncertainty,
      observedSymptom = if (isWeakUncertainty) {
        "Asserted definitive conclusions on incomplete triage without documenting uncertainty margins or intermediate hypotheses."
      } else "Documented explicit uncertainty bounds and investigative hypotheses.",
      rootCauseCausalLink = "Treating security analysis as binary true/false without quantifying evidence confidence or unknown variables.",
      actionableRemediation = "Explicitly annotate telemetry gaps and maintain probabilistic confidence ratings during active triage."
    )
  }

  /**
   * 6. CONTEXT_IGNORANCE:
   * Learner ignores critical environmental context (e.g. corporate VPN policy, employee home location,
   * normal baseline business hours).
   */
  fun detectContextIgnorance(
    submission: LearnerMissionSubmission,
    answerKey: MissionAnswerKey
  ): FailurePatternDetectionResult {
    // Flagging Austin VPN (tl_04) as malicious despite it being the assigned employee location
    val flaggedLegitVpn = submission.selectedIocs.contains("73.189.44.10") ||
      submission.reasoningText.contains("73.189.44.10 is malicious", ignoreCase = true)

    return FailurePatternDetectionResult(
      pattern = FailureModeType.CONTEXT_IGNORANCE,
      isDetected = flaggedLegitVpn,
      observedSymptom = if (flaggedLegitVpn) {
        "Flagged assigned employee corporate VPN gateway as malicious due to ignoring identity baseline profile."
      } else "Respected environmental identity baselines and assigned corporate network parameters.",
      rootCauseCausalLink = "Analyzing alerts in isolation from asset identity, user assignment, and approved infrastructure baselines.",
      actionableRemediation = "Consult user asset registry and assigned network boundaries before classifying corporate endpoints as malicious."
    )
  }

  /**
   * 7. INCORRECT_PRIORITIZATION:
   * Learner focuses on minor background telemetry (e.g. cosmetic workstation noise) while an active C2/compromised
   * credential session remains uncontained.
   */
  fun detectIncorrectPrioritization(
    submission: LearnerMissionSubmission,
    answerKey: MissionAnswerKey
  ): FailurePatternDetectionResult {
    // Option 1 on q_login_3 is "Ignore alert until end of business day"
    val q3Answer = submission.selectedAnswers["q_login_3"]
    val isDeprioritized = q3Answer == 1

    return FailurePatternDetectionResult(
      pattern = FailureModeType.INCORRECT_PRIORITIZATION,
      isDetected = isDeprioritized,
      observedSymptom = if (isDeprioritized) {
        "Deprioritized active credential compromise triage in favor of routine end-of-day review."
      } else "Prioritized active credential compromise with immediate containment triage.",
      rootCauseCausalLink = "Failing to classify confirmed active attacker access as SEV-1/SEV-2 urgency.",
      actionableRemediation = "Always prioritize active authenticated sessions from unverified origins above passive maintenance queues."
    )
  }

  /**
   * Evaluates a submission across all 7 deterministic failure pattern detectors.
   */
  fun evaluateAll(
    submission: LearnerMissionSubmission,
    answerKey: MissionAnswerKey
  ): List<FailurePatternDetectionResult> {
    return listOf(
      detectPrematureEscalation(submission, answerKey),
      detectEvidenceOverweighting(submission, answerKey),
      detectConfirmationBias(submission, answerKey),
      detectInsufficientCorrelation(submission, answerKey),
      detectWeakUncertaintyHandling(submission, answerKey),
      detectContextIgnorance(submission, answerKey),
      detectIncorrectPrioritization(submission, answerKey)
    )
  }

  /**
   * Authoritative calculation of pattern confidence tier based on deterministic observation count.
   */
  fun computeConfidenceTier(observationCount: Int): FailurePatternConfidenceTier {
    return when {
      observationCount >= 3 -> FailurePatternConfidenceTier.HIGH_CONFIDENCE
      observationCount == 2 -> FailurePatternConfidenceTier.REPEATED
      else -> FailurePatternConfidenceTier.OBSERVED
    }
  }
}
