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
