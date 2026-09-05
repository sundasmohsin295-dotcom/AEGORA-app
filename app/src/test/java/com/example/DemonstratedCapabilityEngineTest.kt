package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.capability.*
import com.example.data.DemonstratedCapabilityRepository
import com.example.data.db.*
import com.example.model.TwinDimensionV12
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
class DemonstratedCapabilityEngineTest {

  private lateinit var database: AegoraDatabase
  private lateinit var repository: DemonstratedCapabilityRepository
  private lateinit var engine: DemonstratedCapabilityEngine
  private lateinit var cyberTwinAdapter: CyberTwinAdapter
  private lateinit var nextBestActionAdapter: NextBestActionAdapter

  @Before
  fun setUp() {
    database = Room.inMemoryDatabaseBuilder(
      ApplicationProvider.getApplicationContext(),
      AegoraDatabase::class.java
    ).allowMainThreadQueries().build()

    repository = DemonstratedCapabilityRepository(
      capabilityDao = database.capabilityDao(),
      evidenceDao = database.capabilityEvidenceDao(),
      assessmentDao = database.masteryAssessmentDao()
    )

    engine = DemonstratedCapabilityEngine(MasteryPolicy.DEFAULT)
    cyberTwinAdapter = DefaultCyberTwinAdapter()
    nextBestActionAdapter = DefaultNextBestActionAdapter()
  }

  @After
  fun tearDown() {
    database.close()
  }

  // ==========================================
  // 1. SEVEN MASTERY GATES & DEMONSTRATION TESTS
  // ==========================================

  @Test
  fun testAllSevenGatesPassProducesDemonstratedStatus() {
    val capability = CapabilityEntity(
      id = "cap_001",
      learnerId = "learner_001",
      skillKey = "linux_log_analysis",
      name = "Linux & Sysmon Telemetry Triage",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 90,
      recallScore = 88,
      applicationScore = 92,
      investigationScore = 95,
      transferScore = 85,
      explanationScore = 90,
      uncertaintyResilienceScore = 88,
      independenceScore = 95,
      evidenceQualityScore = 95,
      currentConfidence = 90,
      historicalCapabilityScore = 90,
      lastVerifiedAt = System.currentTimeMillis() - 86400000L
    )

    val evidence = listOf(
      CapabilityEvidenceEntity(
        id = "ev_001",
        learnerId = "learner_001",
        capabilityId = "cap_001",
        missionId = "mission_sysmon_01",
        attemptId = "att_01",
        evidenceType = EvidenceType.LAB_SUBMISSION.name,
        complexity = 80,
        independenceScore = 95,
        authenticityScore = 100,
        transferabilityScore = 90,
        evidenceQualityScore = 95,
        outcomeScore = 95,
        hintsUsed = 0,
        retries = 0,
        reasoningQualityScore = 92,
        confidenceDeclared = 90,
        confidenceCalibrated = 90,
        evidenceHash = "sha256:hash_001",
        verificationStatus = VerificationStatus.VERIFIED.name,
        createdAt = System.currentTimeMillis() - 86400000L
      )
    )

    val result = engine.evaluateCapability(
      capability = capability,
      evidence = evidence,
      assessments = emptyList()
    )

    assertTrue("Should be marked as demonstrated", result.isDemonstrated)
    assertEquals(MasteryStatus.DEMONSTRATED, result.overallMasteryStatus)
    assertEquals(7, result.gateResults.size)
    assertTrue("All 7 gates should pass", result.gateResults.values.all { it.isPassed })
    assertNull("No limiting bottleneck should exist", result.limitingGate)
    assertEquals(RetentionRisk.LOW, result.retentionRisk)
  }

  @Test
  fun testHighKnowledgeWithWeakInvestigationAndTransferIsBlockedFromDemonstrated() {
    // High theory/recall (95), but failing investigation (50) and transfer (40)
    val capability = CapabilityEntity(
      id = "cap_asym_001",
      learnerId = "learner_001",
      skillKey = "k8s_security",
      name = "Kubernetes Cluster Defense",
      category = CapabilityCategory.CLOUD_IAM.name,
      knowledgeScore = 95,
      recallScore = 95,
      applicationScore = 75,
      investigationScore = 50,
      transferScore = 40,
      explanationScore = 80,
      uncertaintyResilienceScore = 70,
      currentConfidence = 72,
      historicalCapabilityScore = 0
    )

    val result = engine.evaluateCapability(
      capability = capability,
      evidence = emptyList(),
      assessments = emptyList()
    )

    assertFalse("Must NOT be marked demonstrated due to failed investigation/transfer gates", result.isDemonstrated)
    assertNotEquals(MasteryStatus.DEMONSTRATED, result.overallMasteryStatus)
    assertNotNull("Limiting bottleneck must be identified", result.limitingGate)
    assertEquals(MasteryGateType.TRANSFER, result.limitingGate) // lowest score (40)
    assertFalse(result.gateResults[MasteryGateType.TRANSFER]?.isPassed == true)
    assertFalse(result.gateResults[MasteryGateType.INVESTIGATE]?.isPassed == true)
  }

  // ==========================================
  // 2. EVIDENCE QUALITY & DIVERSITY ENGINE TESTS
  // ==========================================

  @Test
  fun testVerifiedEvidenceCarriesStrongerWeightThanUnverified() {
    val baseCap = CapabilityEntity(
      id = "cap_weight_001",
      learnerId = "learner_001",
      skillKey = "pcap_analysis",
      name = "Network PCAP Forensics",
      category = CapabilityCategory.INCIDENT_RESPONSE.name,
      applicationScore = 50,
      investigationScore = 50
    )

    val verifiedEvidence = listOf(
      CapabilityEvidenceEntity(
        id = "ev_v1",
        learnerId = "learner_001",
        capabilityId = "cap_weight_001",
        evidenceQualityScore = 90,
        outcomeScore = 90,
        independenceScore = 90,
        authenticityScore = 100,
        evidenceHash = "sha256:v1",
        verificationStatus = VerificationStatus.VERIFIED.name
      )
    )

    val unverifiedEvidence = listOf(
      CapabilityEvidenceEntity(
        id = "ev_u1",
        learnerId = "learner_001",
        capabilityId = "cap_weight_001",
        evidenceQualityScore = 90,
        outcomeScore = 90,
        independenceScore = 90,
        authenticityScore = 100,
        evidenceHash = "sha256:u1",
        verificationStatus = VerificationStatus.UNVERIFIED.name
      )
    )

    val metricsVerified = EvidenceQualityEngine.evaluateEvidence(verifiedEvidence)
    val metricsUnverified = EvidenceQualityEngine.evaluateEvidence(unverifiedEvidence)

    assertEquals(1, metricsVerified.verifiedCount)
    assertEquals(0, metricsUnverified.verifiedCount)
    assertEquals(1, metricsUnverified.breakdown.unverifiedCount)
  }

  @Test
  fun testRevokedAndInvalidEvidenceAreExcludedFromScoring() {
    val evidenceList = listOf(
      CapabilityEvidenceEntity(
        id = "ev_revoked",
        learnerId = "learner_001",
        capabilityId = "cap_rev_001",
        evidenceQualityScore = 100,
        outcomeScore = 100,
        evidenceHash = "sha256:rev",
        verificationStatus = VerificationStatus.REVOKED.name
      ),
      CapabilityEvidenceEntity(
        id = "ev_invalid",
        learnerId = "learner_001",
        capabilityId = "cap_rev_001",
        evidenceQualityScore = 100,
        outcomeScore = 100,
        evidenceHash = "sha256:inv",
        verificationStatus = VerificationStatus.INVALID.name
      ),
      CapabilityEvidenceEntity(
        id = "ev_valid",
        learnerId = "learner_001",
        capabilityId = "cap_rev_001",
        evidenceQualityScore = 75,
        outcomeScore = 75,
        independenceScore = 80,
        authenticityScore = 90,
        evidenceHash = "sha256:val",
        verificationStatus = VerificationStatus.VERIFIED.name
      )
    )

    val metrics = EvidenceQualityEngine.evaluateEvidence(evidenceList)

    assertEquals(1, metrics.validEvidenceCount)
    assertEquals(2, metrics.breakdown.revokedExcludedCount)
    assertEquals(75, metrics.aggregateQualityScore)
  }

  @Test
  fun testDiminishingReturnsForRepeatedIdenticalMissions() {
    // 4 repetitions of the exact same mission
    val repeatedEvidence = (1..4).map { idx ->
      CapabilityEvidenceEntity(
        id = "ev_rep_$idx",
        learnerId = "learner_001",
        capabilityId = "cap_rep_001",
        missionId = "identical_mission_01",
        evidenceType = EvidenceType.LAB_SUBMISSION.name,
        evidenceQualityScore = 90,
        outcomeScore = 90,
        independenceScore = 90,
        authenticityScore = 100,
        evidenceHash = "sha256:rep_$idx",
        verificationStatus = VerificationStatus.VERIFIED.name
      )
    }

    val metrics = EvidenceQualityEngine.evaluateEvidence(repeatedEvidence)
    assertTrue("Diversity factor should be less than 1.0 due to diminishing returns", metrics.breakdown.diversityFactor < 1.0)
    assertTrue("Diversity factor should be positive", metrics.breakdown.diversityFactor > 0.0)
  }

  @Test
  fun testIndependencePenaltiesForHintsAndRetries() {
    val assistedEvidence = listOf(
      CapabilityEvidenceEntity(
        id = "ev_assisted",
        learnerId = "learner_001",
        capabilityId = "cap_ind_001",
        independenceScore = 90,
        hintsUsed = 3, // 3 * 5 = 15 penalty
        retries = 2,   // 2 * 4 = 8 penalty
        evidenceQualityScore = 80,
        authenticityScore = 100,
        evidenceHash = "sha256:assist",
        verificationStatus = VerificationStatus.VERIFIED.name
      )
    )

    val metrics = EvidenceQualityEngine.evaluateEvidence(assistedEvidence)
    // 90 - 15 - 8 = 67
    assertEquals(67, metrics.aggregateIndependenceScore)
  }

  // ==========================================
  // 3. RECENCY, DECAY & RETENTION RISK TESTS
  // ==========================================

  @Test
  fun testHistoricalCapabilityDoesNotDecreaseWithAgeWhileConfidenceDecays() {
    val currentTime = 1700000000000L
    val sixtyDaysAgo = currentTime - (60L * 24 * 60 * 60 * 1000)

    val capability = CapabilityEntity(
      id = "cap_decay_001",
      learnerId = "learner_001",
      skillKey = "ghidra_reversing",
      name = "Ghidra Reverse Engineering",
      category = CapabilityCategory.VULNERABILITY_RESEARCH.name,
      knowledgeScore = 90,
      recallScore = 90,
      applicationScore = 90,
      investigationScore = 90,
      transferScore = 90,
      explanationScore = 90,
      uncertaintyResilienceScore = 90,
      historicalCapabilityScore = 92,
      lastVerifiedAt = sixtyDaysAgo
    )

    val result = engine.evaluateCapability(
      capability = capability,
      evidence = emptyList(),
      assessments = emptyList(),
      currentTime = currentTime
    )

    assertEquals("Historical capability score baseline must remain exactly 92%", 92, result.historicalCapabilityScore)
    assertTrue("Current confidence should decay due to 60 days of idle time", result.currentConfidence < 92)
    assertEquals(RetentionRisk.HIGH, result.retentionRisk)
    assertEquals(60, result.daysSinceLastVerified)
  }

  // ==========================================
  // 4. STRICT LEARNER ISOLATION TESTS
  // ==========================================

  @Test(expected = IllegalArgumentException::class)
  fun testCrossLearnerEvidenceThrowsException() {
    val capability = CapabilityEntity(
      id = "cap_iso_001",
      learnerId = "learner_alice",
      skillKey = "aws_audit",
      name = "AWS Audit",
      category = CapabilityCategory.CLOUD_IAM.name
    )

    val crossEvidence = listOf(
      CapabilityEvidenceEntity(
        id = "ev_bob",
        learnerId = "learner_bob", // Mismatched learner
        capabilityId = "cap_iso_001",
        evidenceHash = "sha256:bob"
      )
    )

    engine.evaluateCapability(capability, crossEvidence, emptyList())
  }

  @Test(expected = IllegalArgumentException::class)
  fun testBlankLearnerIdThrowsException() {
    val capability = CapabilityEntity(
      id = "cap_blank",
      learnerId = "", // Blank
      skillKey = "aws_audit",
      name = "AWS Audit",
      category = CapabilityCategory.CLOUD_IAM.name
    )

    engine.evaluateCapability(capability, emptyList(), emptyList())
  }

  // ==========================================
  // 5. DETERMINISM TESTS
  // ==========================================

  @Test
  fun testStrictDeterminismAcrossMultipleCalculations() {
    val capability = CapabilityEntity(
      id = "cap_det_001",
      learnerId = "learner_001",
      skillKey = "threat_intel",
      name = "Threat Intelligence Modeling",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 84,
      recallScore = 80,
      applicationScore = 88,
      investigationScore = 90,
      transferScore = 75,
      explanationScore = 85,
      uncertaintyResilienceScore = 80,
      currentConfidence = 82,
      historicalCapabilityScore = 85,
      lastVerifiedAt = 1690000000000L
    )

    val fixedTime = 1695000000000L
    val baselineResult = engine.evaluateCapability(capability, emptyList(), emptyList(), fixedTime)

    for (i in 1..50) {
      val repeatResult = engine.evaluateCapability(capability, emptyList(), emptyList(), fixedTime)
      assertEquals(baselineResult.historicalCapabilityScore, repeatResult.historicalCapabilityScore)
      assertEquals(baselineResult.currentConfidence, repeatResult.currentConfidence)
      assertEquals(baselineResult.isDemonstrated, repeatResult.isDemonstrated)
      assertEquals(baselineResult.overallMasteryStatus, repeatResult.overallMasteryStatus)
      assertEquals(baselineResult.whyThisScore, repeatResult.whyThisScore)
      assertEquals(baselineResult.recommendedAction, repeatResult.recommendedAction)
    }
  }

  // ==========================================
  // 6. CYBER TWIN & NEXT BEST ACTION ADAPTERS
  // ==========================================

  @Test
  fun testCyberTwinAdapterMapsCorrectly() {
    val capability = CapabilityEntity(
      id = "cap_tw_001",
      learnerId = "learner_001",
      skillKey = "linux_forensics",
      name = "Linux Forensics",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 90,
      recallScore = 90,
      applicationScore = 90,
      investigationScore = 90,
      transferScore = 90,
      explanationScore = 90,
      uncertaintyResilienceScore = 90,
      historicalCapabilityScore = 90,
      currentConfidence = 90
    )

    val result = engine.evaluateCapability(capability, emptyList(), emptyList())
    val snapshot = cyberTwinAdapter.mapToCyberTwinSnapshot("learner_001", listOf(result))

    assertEquals("learner_001", snapshot.learnerId)
    assertTrue("Overall score should be > 1500 ELO", snapshot.overallScore > 1500)
    assertTrue(snapshot.dimensions.containsKey(TwinDimensionV12.INVESTIGATION))
    val investDim = snapshot.dimensions[TwinDimensionV12.INVESTIGATION]
    assertNotNull(investDim)
    assertEquals(90, investDim?.currentState)
  }

  @Test
  fun testNextBestActionAdapterGeneratesPrioritizedActions() {
    val decayingCap = CapabilityAssessmentResult(
      capabilityId = "cap_d1",
      learnerId = "learner_001",
      skillKey = "decaying_skill",
      name = "Decaying IAM Skill",
      category = CapabilityCategory.CLOUD_IAM.name,
      historicalCapabilityScore = 90,
      currentConfidence = 45,
      isDemonstrated = false,
      overallMasteryStatus = MasteryStatus.REQUIRES_REASSESSMENT,
      gateResults = emptyMap(),
      limitingGate = MasteryGateType.INVESTIGATE,
      evidenceQualityBreakdown = EvidenceQualityBreakdown(0, 0, 0, 0, 1.0, 0, 0, 0, 0, 0),
      retentionRisk = RetentionRisk.CRITICAL,
      daysSinceLastVerified = 75,
      limitingFactors = listOf("Critical retention decay"),
      positiveFactors = emptyList(),
      recommendedAction = "Complete 20-minute refresher",
      whyThisScore = "Decayed"
    )

    val actions = nextBestActionAdapter.generateNextActions(listOf(decayingCap))
    assertFalse(actions.isEmpty())
    assertEquals("Decay Prevention", actions.first().category)
    assertEquals(98, actions.first().urgencyScore)
  }

  // ==========================================
  // 7. END-TO-END ROOM PERSISTENCE & EVALUATE
  // ==========================================

  @Test
  fun testEvaluateAndPersistUpdatesDatabase() = runBlocking {
    repository.seedInitialCapabilitiesIfEmpty("learner_001")
    val initialCap = repository.getCapabilityByLearnerAndSkill("learner_001", "linux_log_analysis")
    assertNotNull(initialCap)

    val result = engine.evaluateAndPersist(initialCap!!.id, repository)
    assertNotNull(result)
    assertTrue(result!!.isDemonstrated)

    val updatedCap = repository.getCapabilityById(initialCap.id)
    assertNotNull(updatedCap)
    assertEquals(result.historicalCapabilityScore, updatedCap?.historicalCapabilityScore)
    assertEquals(result.currentConfidence, updatedCap?.currentConfidence)
  }
}
