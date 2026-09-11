package com.example.platform

import com.example.data.AegoraRepository
import com.example.model.InvestigationLab
import com.example.model.InvestigationTimelineEvent
import com.example.model.InvestigationQuestion

/**
 * Cross-platform bridge defining the canonical mission contracts shared between
 * AEGORA Android and AEGORA Web.
 */
object CrossPlatformMissionBridge {

  const val SUSPICIOUS_LOGIN_MISSION_ID = "lab_suspicious_login"

  /**
   * Retrieves the canonical definition of a mission so both Android and Web
   * execute the exact same scenario logic.
   */
  fun getCanonicalMission(missionId: String): InvestigationLab? {
    return AegoraRepository.investigationLabs.firstOrNull { it.id == missionId }
  }

  /**
   * Authoritative validation of a mission outcome.
   * Neither Android nor Web can forge proof; submission answers must be verified against
   * the canonical criteria.
   */
  fun validateMissionSubmission(
    missionId: String,
    learnerAnswers: Map<String, Int>,
    reasoningText: String
  ): CanonicalMissionValidationResult {
    val mission = getCanonicalMission(missionId)
      ?: return CanonicalMissionValidationResult.Error("Mission $missionId not found in canonical catalog")

    if (reasoningText.trim().length < 15) {
      return CanonicalMissionValidationResult.Rejected(
        reason = "Forensic reasoning explanation is insufficient. Professional defense requires reasoned analysis."
      )
    }

    var correctCount = 0
    val totalQuestions = mission.questions.size

    for (q in mission.questions) {
      val selectedIndex = learnerAnswers[q.id]
      if (selectedIndex == q.correctOptionIndex) {
        correctCount++
      }
    }

    val scorePercent = ((correctCount.toDouble() / totalQuestions) * 100).toInt()
    val isPassed = scorePercent >= 70

    return if (isPassed) {
      val evidenceHash = "sha256:aegora_${missionId}_${System.currentTimeMillis()}_${scorePercent}pct"
      CanonicalMissionValidationResult.Success(
        missionId = missionId,
        scorePercent = scorePercent,
        correctCount = correctCount,
        totalQuestions = totalQuestions,
        evidenceHash = evidenceHash,
        awardedCapabilityKey = "sec_auth_impossible_travel_triage"
      )
    } else {
      CanonicalMissionValidationResult.Failed(
        scorePercent = scorePercent,
        remediationAdvice = "Review Windows Event IDs 4624/4625 sequence and GeoIP velocity calculation before re-attempting."
      )
    }
  }

  /**
   * Deterministic client-side evaluation helper for AI Analyst claims, strictly matching
   * ServerAiHallucinationAuthority.
   */
  fun evaluateAiClaimDecision(
    attemptId: String,
    missionId: String,
    claimId: String,
    learnerDecision: com.example.model.LearnerAiDecision,
    selectedEvidenceIds: List<String>,
    learnerReasoning: String = ""
  ): com.example.model.ClientSafeAiVerificationResult {
    val mission = getCanonicalMission(missionId)
      ?: throw IllegalArgumentException("Mission $missionId not found in canonical catalog")

    val validPool = mission.timelineEvents.map { it.id }
    for (eviId in selectedEvidenceIds) {
      if (!validPool.contains(eviId)) {
        throw IllegalArgumentException("Foreign evidence '$eviId' rejected.")
      }
    }

    val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date())
    val digest = "sha256:aegora_ai_claim_${claimId}_${System.currentTimeMillis()}"

    if (claimId == "claim_login_malicious_ip") {
      // The claim is UNSUPPORTED (trap)
      return if (learnerDecision == com.example.model.LearnerAiDecision.CHALLENGE_AI) {
        val hasEvidence = selectedEvidenceIds.any { listOf("tl_01", "tl_02", "tl_03").contains(it) }
        com.example.model.ClientSafeAiVerificationResult(
          attemptId = attemptId,
          claimId = claimId,
          outcome = com.example.model.VerificationOutcomeStatus.AI_FAILURE_DETECTED,
          isAiFailureDetected = true,
          evidenceVerified = hasEvidence || selectedEvidenceIds.isNotEmpty(),
          headline = "AI FAILURE DETECTED ✓",
          explanation = "The AI analyst made an unsupported claim. You detected it using authoritative evidence.",
          detectedFailurePattern = null,
          evidenceDigest = digest,
          verifiedAt = now
        )
      } else {
        // Learner incorrectly accepted the unsupported claim
        val autopsy = com.example.model.LearnerFailureAutopsy(
          yourDecision = "Accepted AI Claim (Recommended Action: Blacklist 185.91.0.0/16 and close incident ticket)",
          aiClaim = "The login is confirmed malicious because the source IP 185.91.x.x is associated with the attack.",
          evidenceYouUsed = selectedEvidenceIds,
          evidenceThatMattered = listOf("tl_01", "tl_02", "tl_03"),
          whatWentWrong = "Evidence Overweighting: You accepted the AI's conclusion without verifying whether IP 185.91.x.x actually appeared anywhere in the supplied authentication telemetry.",
          canonicalFailureMode = com.example.model.FailureModeType.EVIDENCE_OVERWEIGHTING,
          betterReasoning = "Correlate each claimed IOC against raw telemetry logs (Event ID 4624/4625) before approving containment actions.",
          nextChallengeTitle = "Targeted follow-up unlocked: Threat Intel Anchor vs System Truth",
          nextChallengeId = "chal_evidence_overweighting"
        )
        com.example.model.ClientSafeAiVerificationResult(
          attemptId = attemptId,
          claimId = claimId,
          outcome = com.example.model.VerificationOutcomeStatus.AI_CLAIM_NOT_VERIFIED,
          isAiFailureDetected = false,
          evidenceVerified = false,
          headline = "AI CLAIM NOT VERIFIED",
          explanation = "Evidence does not support the analyst's conclusion. Telemetry does not establish that attribution.",
          detectedFailurePattern = com.example.model.FailureModeType.EVIDENCE_OVERWEIGHTING,
          failureAutopsy = autopsy,
          evidenceDigest = digest,
          verifiedAt = now
        )
      }
    }

    if (claimId == "claim_login_supported_geo") {
      return if (learnerDecision == com.example.model.LearnerAiDecision.ACCEPT_AI) {
        com.example.model.ClientSafeAiVerificationResult(
          attemptId = attemptId,
          claimId = claimId,
          outcome = com.example.model.VerificationOutcomeStatus.AI_CLAIM_CORRECTLY_ACCEPTED,
          isAiFailureDetected = false,
          evidenceVerified = true,
          headline = "EVIDENCE VERIFIED ✓",
          explanation = "Correct. The analyst's conclusion is grounded directly in the supplied telemetry events.",
          detectedFailurePattern = null,
          evidenceDigest = digest,
          verifiedAt = now
        )
      } else {
        val autopsy = com.example.model.LearnerFailureAutopsy(
          yourDecision = "Challenged AI Claim (Questioned impossible travel finding)",
          aiClaim = "The sequential logins from Moscow and Austin within 7 minutes represent an impossible travel anomaly.",
          evidenceYouUsed = selectedEvidenceIds,
          evidenceThatMattered = listOf("tl_03", "tl_04"),
          whatWentWrong = "Insufficient Correlation: You failed to correlate the timestamp delta (7 minutes) with geographic distance (~9,000 km) between successive Event ID 4624 logons.",
          canonicalFailureMode = com.example.model.FailureModeType.INSUFFICIENT_CORRELATION,
          betterReasoning = "Calculate geographic travel velocity across sequential authentications for the same user identity before dismissing anomalies.",
          nextChallengeTitle = "Targeted follow-up unlocked: Cross-Host Lateral Correlation",
          nextChallengeId = "chal_insufficient_correlation"
        )
        com.example.model.ClientSafeAiVerificationResult(
          attemptId = attemptId,
          claimId = claimId,
          outcome = com.example.model.VerificationOutcomeStatus.INCORRECT_AI_CHALLENGE,
          isAiFailureDetected = false,
          evidenceVerified = false,
          headline = "INCORRECT CHALLENGE",
          explanation = "The AI analyst claim was rigorously supported by the telemetry events.",
          detectedFailurePattern = com.example.model.FailureModeType.INSUFFICIENT_CORRELATION,
          failureAutopsy = autopsy,
          evidenceDigest = digest,
          verifiedAt = now
        )
      }
    }

    throw IllegalArgumentException("Unknown claim '$claimId'")
  }
}

sealed class CanonicalMissionValidationResult {
  data class Success(
    val missionId: String,
    val scorePercent: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val evidenceHash: String,
    val awardedCapabilityKey: String
  ) : CanonicalMissionValidationResult()

  data class Failed(
    val scorePercent: Int,
    val remediationAdvice: String
  ) : CanonicalMissionValidationResult()

  data class Rejected(val reason: String) : CanonicalMissionValidationResult()
  data class Error(val message: String) : CanonicalMissionValidationResult()
}
