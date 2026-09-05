package com.example.data.db

import java.security.MessageDigest

/**
 * Deterministic validation logic and integrity hash generation for Capability Engine entities.
 */
object CapabilityValidator {

  data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
  )

  fun validateCapability(capability: CapabilityEntity): ValidationResult {
    val errors = mutableListOf<String>()

    if (capability.id.isBlank()) errors.add("Capability ID must not be blank")
    if (capability.learnerId.isBlank()) errors.add("Learner ID must not be blank")
    if (capability.skillKey.isBlank()) errors.add("Skill Key must not be blank")
    if (capability.name.isBlank()) errors.add("Capability Name must not be blank")

    validateScore("knowledgeScore", capability.knowledgeScore, errors)
    validateScore("recallScore", capability.recallScore, errors)
    validateScore("applicationScore", capability.applicationScore, errors)
    validateScore("investigationScore", capability.investigationScore, errors)
    validateScore("transferScore", capability.transferScore, errors)
    validateScore("explanationScore", capability.explanationScore, errors)
    validateScore("uncertaintyResilienceScore", capability.uncertaintyResilienceScore, errors)
    validateScore("independenceScore", capability.independenceScore, errors)
    validateScore("evidenceQualityScore", capability.evidenceQualityScore, errors)
    validateScore("currentConfidence", capability.currentConfidence, errors)
    validateScore("historicalCapabilityScore", capability.historicalCapabilityScore, errors)

    if (capability.lastVerifiedAt < 0L) errors.add("lastVerifiedAt must not be negative")
    if (capability.createdAt < 0L) errors.add("createdAt must not be negative")
    if (capability.updatedAt < 0L) errors.add("updatedAt must not be negative")

    return ValidationResult(errors.isEmpty(), errors)
  }

  fun validateEvidence(evidence: CapabilityEvidenceEntity): ValidationResult {
    val errors = mutableListOf<String>()

    if (evidence.id.isBlank()) errors.add("Evidence ID must not be blank")
    if (evidence.learnerId.isBlank()) errors.add("Learner ID must not be blank")
    if (evidence.capabilityId.isBlank()) errors.add("Capability ID must not be blank")
    if (evidence.evidenceHash.isBlank()) errors.add("Evidence Hash must not be blank")

    validateScore("complexity", evidence.complexity, errors)
    validateScore("independenceScore", evidence.independenceScore, errors)
    validateScore("authenticityScore", evidence.authenticityScore, errors)
    validateScore("transferabilityScore", evidence.transferabilityScore, errors)
    validateScore("evidenceQualityScore", evidence.evidenceQualityScore, errors)
    validateScore("outcomeScore", evidence.outcomeScore, errors)
    validateScore("reasoningQualityScore", evidence.reasoningQualityScore, errors)
    validateScore("confidenceDeclared", evidence.confidenceDeclared, errors)
    validateScore("confidenceCalibrated", evidence.confidenceCalibrated, errors)

    if (evidence.hintsUsed < 0) errors.add("hintsUsed must be non-negative")
    if (evidence.retries < 0) errors.add("retries must be non-negative")
    if (evidence.timeSpentSeconds < 0) errors.add("timeSpentSeconds must be non-negative")
    if (evidence.createdAt < 0L) errors.add("createdAt must not be negative")

    return ValidationResult(errors.isEmpty(), errors)
  }

  fun validateAssessment(assessment: MasteryAssessmentEntity): ValidationResult {
    val errors = mutableListOf<String>()

    if (assessment.id.isBlank()) errors.add("Assessment ID must not be blank")
    if (assessment.learnerId.isBlank()) errors.add("Learner ID must not be blank")
    if (assessment.capabilityId.isBlank()) errors.add("Capability ID must not be blank")
    if (assessment.assessmentReason.isBlank()) errors.add("Assessment Reason must not be blank")

    validateScore("understandScore", assessment.understandScore, errors)
    validateScore("recallScore", assessment.recallScore, errors)
    validateScore("applyScore", assessment.applyScore, errors)
    validateScore("investigateScore", assessment.investigateScore, errors)
    validateScore("transferScore", assessment.transferScore, errors)
    validateScore("explainScore", assessment.explainScore, errors)
    validateScore("uncertaintyResilienceScore", assessment.uncertaintyResilienceScore, errors)
    validateScore("overallScore", assessment.overallScore, errors)
    validateScore("confidence", assessment.confidence, errors)

    if (assessment.createdAt < 0L) errors.add("createdAt must not be negative")
    if (assessment.updatedAt < 0L) errors.add("updatedAt must not be negative")

    return ValidationResult(errors.isEmpty(), errors)
  }

  private fun validateScore(name: String, score: Int, errors: MutableList<String>) {
    if (score < 0 || score > 100) {
      errors.add("$name must be within [0, 100], but was $score")
    }
  }

  /**
   * Generates a deterministic SHA-256 integrity hash from evidence parameters.
   */
  fun generateEvidenceIntegrityHash(
    learnerId: String,
    capabilityId: String,
    missionId: String?,
    attemptId: String?,
    evidenceType: String,
    outcomeScore: Int,
    createdAt: Long
  ): String {
    val rawPayload = "$learnerId|$capabilityId|${missionId ?: "none"}|${attemptId ?: "none"}|$evidenceType|$outcomeScore|$createdAt"
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(rawPayload.toByteArray(Charsets.UTF_8))
    val hex = hashBytes.joinToString("") { "%02x".format(it) }
    return "sha256:$hex"
  }
}
