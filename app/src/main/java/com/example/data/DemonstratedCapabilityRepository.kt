package com.example.data

import com.example.data.db.*
import com.example.data.db.dao.CapabilityDao
import com.example.data.db.dao.CapabilityEvidenceDao
import com.example.data.db.dao.MasteryAssessmentDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository coordinating database operations for the Demonstrated Capability Engine.
 * Follows the architecture: UI -> ViewModel -> Engine -> Repository -> DAO -> Room.
 */
class DemonstratedCapabilityRepository(
  private val capabilityDao: CapabilityDao,
  private val evidenceDao: CapabilityEvidenceDao,
  private val assessmentDao: MasteryAssessmentDao
) {

  fun observeCapabilitiesForLearner(learnerId: String): Flow<List<CapabilityEntity>> =
    capabilityDao.observeByLearner(learnerId)

  fun observeDemonstratedForLearner(learnerId: String): Flow<List<CapabilityEntity>> =
    capabilityDao.getDemonstratedForLearner(learnerId)

  fun countCapabilitiesForLearner(learnerId: String): Flow<Int> =
    capabilityDao.countForLearner(learnerId)

  fun countDemonstratedForLearner(learnerId: String): Flow<Int> =
    capabilityDao.countDemonstratedForLearner(learnerId)

  fun getAverageConfidenceForLearner(learnerId: String): Flow<Float?> =
    capabilityDao.getAverageConfidenceForLearner(learnerId)

  suspend fun getCapabilityById(id: String): CapabilityEntity? =
    capabilityDao.getById(id)

  suspend fun getCapabilityByLearnerAndSkill(learnerId: String, skillKey: String): CapabilityEntity? =
    capabilityDao.getByLearnerAndSkill(learnerId, skillKey)

  fun getCapabilitiesByCategory(learnerId: String, category: String): Flow<List<CapabilityEntity>> =
    capabilityDao.getCapabilitiesByCategory(learnerId, category)

  suspend fun getCapabilitiesNeedingReassessment(
    learnerId: String,
    thresholdTimestamp: Long = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000), // 30 days
    confidenceThreshold: Int = 70
  ): List<CapabilityEntity> =
    capabilityDao.getCapabilitiesNeedingReassessment(learnerId, thresholdTimestamp, confidenceThreshold)

  suspend fun getRecentlyVerified(learnerId: String, limit: Int = 10): List<CapabilityEntity> =
    capabilityDao.getRecentlyVerified(learnerId, limit)

  fun observeEvidenceForCapability(capabilityId: String): Flow<List<CapabilityEvidenceEntity>> =
    evidenceDao.observeEvidenceForCapability(capabilityId)

  fun observeEvidenceForLearner(learnerId: String): Flow<List<CapabilityEvidenceEntity>> =
    evidenceDao.observeEvidenceForLearner(learnerId)

  suspend fun getEvidenceByCapabilityId(capabilityId: String): List<CapabilityEvidenceEntity> =
    evidenceDao.getByCapabilityId(capabilityId)

  suspend fun getEvidenceById(id: String): CapabilityEvidenceEntity? =
    evidenceDao.getById(id)

  suspend fun getEvidenceByHash(hash: String): CapabilityEvidenceEntity? =
    evidenceDao.getByHash(hash)

  suspend fun getVerifiedEvidence(learnerId: String): List<CapabilityEvidenceEntity> =
    evidenceDao.getVerifiedEvidence(learnerId)

  fun observeAssessmentsForCapability(capabilityId: String): Flow<List<MasteryAssessmentEntity>> =
    assessmentDao.observeAssessmentsForCapability(capabilityId)

  suspend fun getAssessmentsByCapabilityId(capabilityId: String): List<MasteryAssessmentEntity> =
    assessmentDao.getByCapabilityId(capabilityId)

  suspend fun getLatestAssessment(capabilityId: String): MasteryAssessmentEntity? =
    assessmentDao.getLatestAssessment(capabilityId)

  fun observeLatestAssessment(capabilityId: String): Flow<MasteryAssessmentEntity?> =
    assessmentDao.observeLatestAssessment(capabilityId)

  fun getCapabilityWithEvidence(id: String): Flow<CapabilityWithEvidence?> =
    capabilityDao.getCapabilityWithEvidence(id)

  fun getCapabilityWithFullMastery(id: String): Flow<CapabilityWithFullMastery?> =
    capabilityDao.getCapabilityWithFullMastery(id)

  fun getAllDemonstratedWithEvidence(learnerId: String): Flow<List<CapabilityWithEvidence>> =
    capabilityDao.getAllDemonstratedWithEvidence(learnerId)

  suspend fun saveCapability(capability: CapabilityEntity): CapabilityValidator.ValidationResult {
    val validation = CapabilityValidator.validateCapability(capability)
    if (!validation.isValid) return validation
    capabilityDao.insert(capability)
    return validation
  }

  suspend fun recordEvidence(evidence: CapabilityEvidenceEntity): CapabilityValidator.ValidationResult {
    val validation = CapabilityValidator.validateEvidence(evidence)
    if (!validation.isValid) return validation
    evidenceDao.insert(evidence)
    return validation
  }

  suspend fun recordAssessment(assessment: MasteryAssessmentEntity): CapabilityValidator.ValidationResult {
    val validation = CapabilityValidator.validateAssessment(assessment)
    if (!validation.isValid) return validation
    assessmentDao.insert(assessment)
    return validation
  }

  suspend fun deleteCapability(capability: CapabilityEntity) {
    capabilityDao.delete(capability)
  }

  suspend fun deleteEvidence(evidence: CapabilityEvidenceEntity) {
    evidenceDao.delete(evidence)
  }

  suspend fun deleteAssessment(assessment: MasteryAssessmentEntity) {
    assessmentDao.delete(assessment)
  }

  suspend fun seedInitialCapabilitiesIfEmpty(learnerId: String = "learner_001") {
    val existing = capabilityDao.getByLearnerId(learnerId)
    if (existing.isNotEmpty()) return

    val sampleCapabilities = listOf(
      CapabilityEntity(
        id = "cap_${learnerId}_soc_001",
        learnerId = learnerId,
        skillKey = "linux_log_analysis",
        name = "Linux & Sysmon Telemetry Triage",
        category = CapabilityCategory.THREAT_DETECTION.name,
        knowledgeScore = 95,
        recallScore = 92,
        applicationScore = 90,
        investigationScore = 96,
        transferScore = 88,
        explanationScore = 94,
        uncertaintyResilienceScore = 90,
        independenceScore = 95,
        evidenceQualityScore = 96,
        currentConfidence = 92,
        historicalCapabilityScore = 92,
        lastVerifiedAt = System.currentTimeMillis() - 86400000L
      ),
      CapabilityEntity(
        id = "cap_${learnerId}_iam_002",
        learnerId = learnerId,
        skillKey = "aws_iam_privesc",
        name = "AWS IAM Role Assumption & PrivEsc Auditing",
        category = CapabilityCategory.CLOUD_IAM.name,
        knowledgeScore = 92,
        recallScore = 88,
        applicationScore = 95,
        investigationScore = 90,
        transferScore = 85,
        explanationScore = 92,
        uncertaintyResilienceScore = 88,
        independenceScore = 90,
        evidenceQualityScore = 92,
        currentConfidence = 90,
        historicalCapabilityScore = 90,
        lastVerifiedAt = System.currentTimeMillis() - 172800000L
      ),
      CapabilityEntity(
        id = "cap_${learnerId}_ir_003",
        learnerId = learnerId,
        skillKey = "ransomware_memory_forensics",
        name = "Ransomware Volatility & Memory Invariant Analysis",
        category = CapabilityCategory.INCIDENT_RESPONSE.name,
        knowledgeScore = 78,
        recallScore = 75,
        applicationScore = 70,
        investigationScore = 72,
        transferScore = 65,
        explanationScore = 80,
        uncertaintyResilienceScore = 68,
        independenceScore = 85,
        evidenceQualityScore = 80,
        currentConfidence = 68,
        historicalCapabilityScore = 0,
        lastVerifiedAt = System.currentTimeMillis() - 259200000L
      ),
      CapabilityEntity(
        id = "cap_${learnerId}_vr_004",
        learnerId = learnerId,
        skillKey = "heap_overflow_triage",
        name = "Heap Memory Corruption & Use-After-Free Detection",
        category = CapabilityCategory.VULNERABILITY_RESEARCH.name,
        knowledgeScore = 60,
        recallScore = 55,
        applicationScore = 45,
        investigationScore = 40,
        transferScore = 30,
        explanationScore = 50,
        uncertaintyResilienceScore = 40,
        independenceScore = 70,
        evidenceQualityScore = 60,
        currentConfidence = 45,
        historicalCapabilityScore = 0,
        lastVerifiedAt = 0L
      ),
      CapabilityEntity(
        id = "cap_${learnerId}_aisec_005",
        learnerId = learnerId,
        skillKey = "llm_prompt_injection",
        name = "LLM Indirect Prompt Injection & Guardrail Testing",
        category = CapabilityCategory.AI_SECURITY.name,
        knowledgeScore = 85,
        recallScore = 80,
        applicationScore = 75,
        investigationScore = 70,
        transferScore = 72,
        explanationScore = 82,
        uncertaintyResilienceScore = 75,
        independenceScore = 80,
        evidenceQualityScore = 85,
        currentConfidence = 76,
        historicalCapabilityScore = 0,
        lastVerifiedAt = System.currentTimeMillis() - 400000000L
      )
    )

    capabilityDao.insertAll(sampleCapabilities)

    val sampleEvidence = listOf(
      CapabilityEvidenceEntity(
        id = "ev_${learnerId}_001",
        learnerId = learnerId,
        capabilityId = "cap_${learnerId}_soc_001",
        missionId = "mission_powershell_deobfuscation",
        attemptId = "att_001",
        evidenceType = EvidenceType.LAB_SUBMISSION.name,
        complexity = 75,
        independenceScore = 95,
        authenticityScore = 98,
        transferabilityScore = 90,
        evidenceQualityScore = 96,
        outcomeScore = 100,
        hintsUsed = 0,
        retries = 0,
        timeSpentSeconds = 480,
        reasoningQualityScore = 94,
        confidenceDeclared = 90,
        confidenceCalibrated = 92,
        evidenceHash = CapabilityValidator.generateEvidenceIntegrityHash(
          learnerId, "cap_${learnerId}_soc_001", "mission_powershell_deobfuscation", "att_001",
          EvidenceType.LAB_SUBMISSION.name, 100, System.currentTimeMillis() - 86400000L
        ),
        verificationStatus = VerificationStatus.VERIFIED.name,
        createdAt = System.currentTimeMillis() - 86400000L
      ),
      CapabilityEvidenceEntity(
        id = "ev_${learnerId}_002",
        learnerId = learnerId,
        capabilityId = "cap_${learnerId}_iam_002",
        missionId = "mission_iam_audit",
        attemptId = "att_002",
        evidenceType = EvidenceType.CODE_ARTIFACT.name,
        complexity = 80,
        independenceScore = 90,
        authenticityScore = 96,
        transferabilityScore = 92,
        evidenceQualityScore = 92,
        outcomeScore = 95,
        hintsUsed = 1,
        retries = 0,
        timeSpentSeconds = 720,
        reasoningQualityScore = 90,
        confidenceDeclared = 85,
        confidenceCalibrated = 88,
        evidenceHash = CapabilityValidator.generateEvidenceIntegrityHash(
          learnerId, "cap_${learnerId}_iam_002", "mission_iam_audit", "att_002",
          EvidenceType.CODE_ARTIFACT.name, 95, System.currentTimeMillis() - 172800000L
        ),
        verificationStatus = VerificationStatus.VERIFIED.name,
        createdAt = System.currentTimeMillis() - 172800000L
      )
    )

    evidenceDao.insertAll(sampleEvidence)
  }
}
