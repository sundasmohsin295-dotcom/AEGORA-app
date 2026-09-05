package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.DemonstratedCapabilityRepository
import com.example.data.db.*
import com.example.data.db.dao.CapabilityDao
import com.example.data.db.dao.CapabilityEvidenceDao
import com.example.data.db.dao.MasteryAssessmentDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DemonstratedCapabilityRoomTest {

  private lateinit var database: AegoraDatabase
  private lateinit var capabilityDao: CapabilityDao
  private lateinit var evidenceDao: CapabilityEvidenceDao
  private lateinit var assessmentDao: MasteryAssessmentDao
  private lateinit var repository: DemonstratedCapabilityRepository

  @Before
  fun createDb() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      AegoraDatabase::class.java
    ).allowMainThreadQueries().build()

    capabilityDao = database.capabilityDao()
    evidenceDao = database.capabilityEvidenceDao()
    assessmentDao = database.masteryAssessmentDao()

    repository = DemonstratedCapabilityRepository(
      capabilityDao = capabilityDao,
      evidenceDao = evidenceDao,
      assessmentDao = assessmentDao
    )
  }

  @After
  fun closeDb() {
    database.close()
  }

  @Test
  fun testCapabilityInsertAndRetrieve() = runBlocking {
    val cap = CapabilityEntity(
      id = "cap_l1_001",
      learnerId = "learner_001",
      skillKey = "linux_log_analysis",
      name = "Linux Sysmon Telemetry Triage",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 90,
      recallScore = 85,
      applicationScore = 95,
      investigationScore = 92,
      transferScore = 88,
      explanationScore = 90,
      uncertaintyResilienceScore = 86,
      independenceScore = 94,
      evidenceQualityScore = 95,
      currentConfidence = 91,
      historicalCapabilityScore = 91,
      lastVerifiedAt = 1700000000000L
    )

    val validation = repository.saveCapability(cap)
    assertTrue("Capability validation must succeed", validation.isValid)

    val fetched = repository.getCapabilityById("cap_l1_001")
    assertNotNull("Capability must be found", fetched)
    assertEquals("learner_001", fetched?.learnerId)
    assertEquals("linux_log_analysis", fetched?.skillKey)
    assertEquals(95, fetched?.applicationScore)
    assertEquals(91, fetched?.currentConfidence)
    assertEquals(91, fetched?.historicalCapabilityScore)
    assertTrue(fetched?.isDemonstrated == true)
  }

  @Test
  fun testCapabilityCompositeKeyUniqueness() = runBlocking {
    val cap1 = CapabilityEntity(
      id = "cap_l1_uniq_1",
      learnerId = "learner_001",
      skillKey = "aws_iam_privesc",
      name = "AWS IAM Audit Initial",
      category = CapabilityCategory.CLOUD_IAM.name,
      currentConfidence = 70
    )
    repository.saveCapability(cap1)

    // Second capability with identical learnerId + skillKey replaces/updates
    val cap2 = CapabilityEntity(
      id = "cap_l1_uniq_2",
      learnerId = "learner_001",
      skillKey = "aws_iam_privesc",
      name = "AWS IAM Audit Updated",
      category = CapabilityCategory.CLOUD_IAM.name,
      currentConfidence = 85
    )
    repository.saveCapability(cap2)

    val list = capabilityDao.getByLearnerId("learner_001")
    assertEquals("Should only have 1 entry for learner_001 + aws_iam_privesc", 1, list.size)
    assertEquals("AWS IAM Audit Updated", list.first().name)
  }

  @Test
  fun testLearnerIsolationForCapabilities() = runBlocking {
    val cap1 = CapabilityEntity(
      id = "cap_l1_iso",
      learnerId = "learner_alpha",
      skillKey = "memory_forensics",
      name = "Memory Forensics Volatility",
      category = CapabilityCategory.INCIDENT_RESPONSE.name,
      currentConfidence = 90
    )
    val cap2 = CapabilityEntity(
      id = "cap_l2_iso",
      learnerId = "learner_bravo",
      skillKey = "memory_forensics",
      name = "Memory Forensics Volatility",
      category = CapabilityCategory.INCIDENT_RESPONSE.name,
      currentConfidence = 60
    )

    repository.saveCapability(cap1)
    repository.saveCapability(cap2)

    val alphaList = capabilityDao.getByLearnerId("learner_alpha")
    val bravoList = capabilityDao.getByLearnerId("learner_bravo")

    assertEquals(1, alphaList.size)
    assertEquals("learner_alpha", alphaList.first().learnerId)
    assertEquals(90, alphaList.first().currentConfidence)

    assertEquals(1, bravoList.size)
    assertEquals("learner_bravo", bravoList.first().learnerId)
    assertEquals(60, bravoList.first().currentConfidence)
  }

  @Test
  fun testEvidenceInsertAndIntegrityVerification() = runBlocking {
    val cap = CapabilityEntity(
      id = "cap_soc_ev",
      learnerId = "learner_001",
      skillKey = "powershell_triage",
      name = "PowerShell Script Triage",
      category = CapabilityCategory.THREAT_DETECTION.name
    )
    repository.saveCapability(cap)

    val timestamp = System.currentTimeMillis()
    val hash = CapabilityValidator.generateEvidenceIntegrityHash(
      learnerId = "learner_001",
      capabilityId = "cap_soc_ev",
      missionId = "mission_001",
      attemptId = "attempt_001",
      evidenceType = EvidenceType.LAB_SUBMISSION.name,
      outcomeScore = 100,
      createdAt = timestamp
    )

    val evidence = CapabilityEvidenceEntity(
      id = "ev_001",
      learnerId = "learner_001",
      capabilityId = "cap_soc_ev",
      missionId = "mission_001",
      attemptId = "attempt_001",
      evidenceType = EvidenceType.LAB_SUBMISSION.name,
      complexity = 80,
      independenceScore = 95,
      authenticityScore = 100,
      transferabilityScore = 90,
      evidenceQualityScore = 95,
      outcomeScore = 100,
      hintsUsed = 0,
      retries = 0,
      timeSpentSeconds = 300,
      reasoningQualityScore = 92,
      confidenceDeclared = 90,
      confidenceCalibrated = 92,
      evidenceHash = hash,
      verificationStatus = VerificationStatus.VERIFIED.name,
      createdAt = timestamp
    )

    val validation = repository.recordEvidence(evidence)
    assertTrue("Evidence validation must succeed", validation.isValid)

    val fetchedEvidence = repository.getEvidenceByHash(hash)
    assertNotNull("Evidence should be retrievable by deterministic hash", fetchedEvidence)
    assertEquals("ev_001", fetchedEvidence?.id)
    assertEquals(VerificationStatus.VERIFIED.name, fetchedEvidence?.verificationStatus)

    val count = evidenceDao.countVerifiedEvidenceForCapability("cap_soc_ev")
    assertEquals(1, count)
  }

  @Test
  fun testMasteryAssessmentDeterministicLatestQuery() = runBlocking {
    val cap = CapabilityEntity(
      id = "cap_assess_test",
      learnerId = "learner_001",
      skillKey = "k8s_security",
      name = "Kubernetes RBAC Hardening",
      category = CapabilityCategory.CLOUD_IAM.name
    )
    repository.saveCapability(cap)

    val assess1 = MasteryAssessmentEntity(
      id = "assess_001",
      learnerId = "learner_001",
      capabilityId = "cap_assess_test",
      understandScore = 70,
      recallScore = 65,
      applyScore = 60,
      overallScore = 65,
      masteryStatus = MasteryStatus.IN_PROGRESS.name,
      assessmentReason = "Initial attempt shows partial RBAC policy understanding",
      createdAt = 1000L
    )

    val assess2 = MasteryAssessmentEntity(
      id = "assess_002",
      learnerId = "learner_001",
      capabilityId = "cap_assess_test",
      understandScore = 95,
      recallScore = 92,
      applyScore = 90,
      overallScore = 92,
      masteryStatus = MasteryStatus.DEMONSTRATED.name,
      assessmentReason = "Advanced attempt passed all 7-stage verification criteria",
      createdAt = 2000L
    )

    repository.recordAssessment(assess1)
    repository.recordAssessment(assess2)

    val latest = repository.getLatestAssessment("cap_assess_test")
    assertNotNull("Latest assessment must not be null", latest)
    assertEquals("assess_002", latest?.id)
    assertEquals(MasteryStatus.DEMONSTRATED.name, latest?.masteryStatus)
    assertEquals(92, latest?.overallScore)
  }

  @Test
  fun testFullCompositeRelationCapabilityWithEvidenceAndAssessments() = runBlocking {
    val cap = CapabilityEntity(
      id = "cap_rel_001",
      learnerId = "learner_001",
      skillKey = "reverse_engineering",
      name = "Binary Reverse Engineering",
      category = CapabilityCategory.VULNERABILITY_RESEARCH.name
    )
    repository.saveCapability(cap)

    val ev = CapabilityEvidenceEntity(
      id = "ev_rel_001",
      learnerId = "learner_001",
      capabilityId = "cap_rel_001",
      evidenceHash = "sha256:rel_evidence_hash_12345"
    )
    repository.recordEvidence(ev)

    val assess = MasteryAssessmentEntity(
      id = "assess_rel_001",
      learnerId = "learner_001",
      capabilityId = "cap_rel_001",
      evidenceId = "ev_rel_001",
      assessmentReason = "Disassembly and control flow reconstruction verified"
    )
    repository.recordAssessment(assess)

    val fullRel = repository.getCapabilityWithFullMastery("cap_rel_001").first()
    assertNotNull(fullRel)
    assertEquals("Binary Reverse Engineering", fullRel?.capability?.name)
    assertEquals(1, fullRel?.evidenceList?.size)
    assertEquals("ev_rel_001", fullRel?.evidenceList?.first()?.id)
    assertEquals(1, fullRel?.assessments?.size)
    assertEquals("assess_rel_001", fullRel?.assessments?.first()?.id)
  }

  @Test
  fun testScoreBoundsValidation() {
    val invalidCap = CapabilityEntity(
      id = "cap_invalid",
      learnerId = "learner_001",
      skillKey = "invalid_skill",
      name = "Invalid Capability",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 150 // Invalid > 100
    )

    val validation = CapabilityValidator.validateCapability(invalidCap)
    assertFalse("Validation should reject score > 100", validation.isValid)
    assertTrue("Error should mention knowledgeScore", validation.errors.any { it.contains("knowledgeScore") })

    val invalidEvidence = CapabilityEvidenceEntity(
      id = "ev_invalid",
      learnerId = "learner_001",
      capabilityId = "cap_001",
      evidenceHash = "hash",
      hintsUsed = -1 // Invalid negative hints
    )
    val evValidation = CapabilityValidator.validateEvidence(invalidEvidence)
    assertFalse("Validation should reject negative hintsUsed", evValidation.isValid)
  }

  @Test
  fun testSeedCapabilitiesPopulatesLearner() = runBlocking {
    repository.seedInitialCapabilitiesIfEmpty("learner_001")

    val list = capabilityDao.getByLearnerId("learner_001")
    assertTrue("Should seed initial capabilities", list.size >= 5)

    val demonstrated = capabilityDao.getDemonstratedForLearner("learner_001").first()
    assertTrue("Should have demonstrated capabilities", demonstrated.isNotEmpty())

    val verifiedEv = evidenceDao.getVerifiedEvidence("learner_001")
    assertTrue("Should have verified evidence", verifiedEv.isNotEmpty())
  }
}
