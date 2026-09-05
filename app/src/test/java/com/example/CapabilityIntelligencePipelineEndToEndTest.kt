package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.capability.*
import com.example.data.AegoraRepository
import com.example.data.DemonstratedCapabilityRepository
import com.example.data.db.*
import com.example.intelligence.AegoraIntelligenceOrchestrator
import com.example.intelligence.CyberOperatingSystemV12Engine
import com.example.model.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.MessageDigest

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CapabilityIntelligencePipelineEndToEndTest {

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

  private fun sha256(input: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
  }

  // ============================================================================
  // 1. REQUIRED END-TO-END TEST & SCENARIO: STRONG KNOWLEDGE, WEAK TRANSFER
  // ============================================================================

  @Test
  fun testEndToEndCapabilityPipeline_WeakTransferScenario() = runBlocking {
    val learnerId = "learner_strong_knowledge_weak_transfer"
    val skillKey = "cloud_siem_correlation"
    val capabilityId = "cap_siem_001"
    val now = 1750000000000L

    // Step 1: Persist Capability with strong knowledge/recall/apply/investigate/explain/uncertainty, but LOW transfer
    val initialCapability = CapabilityEntity(
      id = capabilityId,
      learnerId = learnerId,
      skillKey = skillKey,
      name = "Cloud SIEM & Telemetry Correlation",
      category = CapabilityCategory.CLOUD_IAM.name,
      knowledgeScore = 92,               // Understand HIGH
      recallScore = 90,                  // Recall HIGH
      applicationScore = 88,             // Apply HIGH
      investigationScore = 86,           // Investigate HIGH
      transferScore = 35,                // Transfer LOW (< 70 benchmark)
      explanationScore = 88,             // Explain HIGH
      uncertaintyResilienceScore = 85,   // Uncertainty HIGH
      independenceScore = 90,
      evidenceQualityScore = 88,
      currentConfidence = 75,
      historicalCapabilityScore = 40,
      lastVerifiedAt = now - (2L * 24 * 60 * 60 * 1000),
      createdAt = now - (10L * 24 * 60 * 60 * 1000),
      updatedAt = now - (2L * 24 * 60 * 60 * 1000)
    )
    val saveResult = repository.saveCapability(initialCapability)
    assertTrue("Initial capability must pass validation", saveResult.isValid)

    // Step 2: Persist Real Evidence supporting high domain performance but confirming weak transferability
    val rawPayload = "SOC Telemetry correlation artifact showing local VPC success but failed cross-cloud Azure transfer"
    val evidenceEntity = CapabilityEvidenceEntity(
      id = "ev_siem_001",
      capabilityId = capabilityId,
      learnerId = learnerId,
      missionId = "lab_cloud_transfer_101",
      evidenceType = EvidenceType.LAB_SUBMISSION.name,
      verificationStatus = VerificationStatus.VERIFIED.name,
      evidenceQualityScore = 90,
      independenceScore = 92,
      transferabilityScore = 35,          // Low transfer evidence score
      reasoningQualityScore = 90,
      evidenceHash = sha256(rawPayload),
      confidenceDeclared = 85,
      confidenceCalibrated = 85,
      hintsUsed = 0,
      retries = 0,
      createdAt = now - (1L * 24 * 60 * 60 * 1000)
    )
    val recordResult = repository.recordEvidence(evidenceEntity)
    assertTrue("Real evidence must pass cryptographic validation", recordResult.isValid)

    // Step 3: Execute the complete production Capability Intelligence Pipeline
    // REAL EVIDENCE -> DemonstratedCapabilityEngine -> CapabilityAssessmentResult
    // -> CyberTwinAdapter -> EXISTING Cyber Twin 6.0 -> Capability Bottleneck
    // -> NextBestActionAdapter -> EXISTING Next Best Action -> REAL Mission / Action
    val pipelineResult = AegoraRepository.executeCapabilityPipeline(
      learnerId = learnerId,
      repository = repository
    )

    // ==========================================
    // VERIFY LINK 1: DemonstratedCapabilityEngine & Rubric Evaluation
    // ==========================================
    assertEquals(1, pipelineResult.capabilityResults.size)
    val capResult = pipelineResult.capabilityResults.first()

    assertEquals(learnerId, capResult.learnerId)
    assertEquals(skillKey, capResult.skillKey)

    // Verify 7-Gate Rubric: Understand HIGH, Recall HIGH, Apply HIGH, Investigate HIGH, Transfer LOW, Explain HIGH, Uncertainty HIGH
    val gates = capResult.gateResults
    assertTrue("Understand gate must be HIGH (>= 85)", (gates[MasteryGateType.UNDERSTAND]?.score ?: 0) >= 85)
    assertTrue("Understand gate must pass", gates[MasteryGateType.UNDERSTAND]?.isPassed == true)

    assertTrue("Recall gate must be HIGH (>= 85)", (gates[MasteryGateType.RECALL]?.score ?: 0) >= 85)
    assertTrue("Recall gate must pass", gates[MasteryGateType.RECALL]?.isPassed == true)

    assertTrue("Apply gate must be HIGH (>= 80)", (gates[MasteryGateType.APPLY]?.score ?: 0) >= 80)
    assertTrue("Apply gate must pass", gates[MasteryGateType.APPLY]?.isPassed == true)

    assertTrue("Investigate gate must be HIGH (>= 80)", (gates[MasteryGateType.INVESTIGATE]?.score ?: 0) >= 80)
    assertTrue("Investigate gate must pass", gates[MasteryGateType.INVESTIGATE]?.isPassed == true)

    assertTrue("Transfer gate must be LOW (< 70)", (gates[MasteryGateType.TRANSFER]?.score ?: 100) < 70)
    assertFalse("Transfer gate must fail", gates[MasteryGateType.TRANSFER]?.isPassed == true)

    assertTrue("Explain gate must be HIGH (>= 80)", (gates[MasteryGateType.EXPLAIN]?.score ?: 0) >= 80)
    assertTrue("Explain gate must pass", gates[MasteryGateType.EXPLAIN]?.isPassed == true)

    assertTrue("Uncertainty gate must be HIGH (>= 80)", (gates[MasteryGateType.UNCERTAINTY_RESILIENCE]?.score ?: 0) >= 80)
    assertTrue("Uncertainty gate must pass", gates[MasteryGateType.UNCERTAINTY_RESILIENCE]?.isPassed == true)

    // Expected: Demonstrated = false
    assertFalse("Capability must NOT be marked demonstrated because Transfer gate failed", capResult.isDemonstrated)

    // Primary bottleneck = Transfer
    assertEquals("Primary limiting gate must be TRANSFER", MasteryGateType.TRANSFER, capResult.limitingGate)
    assertEquals("Pipeline bottleneck must be TRANSFER", MasteryGateType.TRANSFER, pipelineResult.primaryBottleneckGate)

    // ==========================================
    // VERIFY LINK 2: CyberTwinAdapter & Cyber Twin 6.0 State
    // ==========================================
    val twinState = CyberOperatingSystemV12Engine.cyberTwin60.value
    assertEquals(learnerId, twinState.learnerId)

    // Cyber Twin receives appropriate capability signal
    val transferDimension = twinState.dimensions[TwinDimensionV12.TRANSFERABILITY]
    assertNotNull("Cyber Twin must have TRANSFERABILITY dimension", transferDimension)
    assertTrue("Transferability dimension must reflect weak transfer (score < 70)", transferDimension!!.currentState < 70)
    assertEquals("Local context only", transferDimension.transferability)
    assertEquals("-5% Decay Alert", transferDimension.trend)

    val knowledgeDimension = twinState.dimensions[TwinDimensionV12.KNOWLEDGE]
    assertNotNull("Cyber Twin must have KNOWLEDGE dimension", knowledgeDimension)
    assertTrue("Knowledge dimension must reflect strong knowledge (score >= 85)", knowledgeDimension!!.currentState >= 85)

    // ==========================================
    // VERIFY LINK 3: NextBestActionAdapter & Next Best Action Flow
    // ==========================================
    val currentActions = AegoraRepository.predictiveNextActions.value
    assertFalse("Next Best Actions must not be empty", currentActions.isEmpty())

    // NBA receives bottleneck and produces a transfer-focused action/mission
    val transferAction = currentActions.firstOrNull { it.id.contains("TRANSFER") || it.title.contains("Transfer", ignoreCase = true) }
    assertNotNull("NBA must produce a transfer-focused action", transferAction)

    assertEquals("Transfer Drill: Cloud SIEM & Telemetry Correlation", transferAction!!.title)
    assertEquals("Mistake Remediation", transferAction.category)
    assertEquals("labs", transferAction.destinationTag)
    assertTrue(transferAction.primaryReason.contains("Transfer Gate", ignoreCase = true))
    assertTrue(transferAction.reasoningTags.any { it.contains("Limiting Gate: Transfer", ignoreCase = true) })
  }

  // ============================================================================
  // 2. SECURITY TEST: TWO LEARNERS & RIGID DATA ISOLATION
  // ============================================================================

  @Test
  fun testSecurity_LearnerIsolationAndTamperPrevention() = runBlocking {
    val learnerA = "learner_alpha"
    val learnerB = "learner_beta"
    val now = 1750000000000L

    // Learner A: Linux Forensics
    val capA = CapabilityEntity(
      id = "cap_alpha_01",
      learnerId = learnerA,
      skillKey = "linux_forensics",
      name = "Linux Incident Forensics",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 95,
      recallScore = 92,
      applicationScore = 90,
      investigationScore = 94,
      transferScore = 88,
      explanationScore = 90,
      uncertaintyResilienceScore = 88,
      independenceScore = 90,
      evidenceQualityScore = 92,
      currentConfidence = 90,
      historicalCapabilityScore = 90
    )
    repository.saveCapability(capA)

    val evA = CapabilityEvidenceEntity(
      id = "ev_alpha_01",
      capabilityId = capA.id,
      learnerId = learnerA,
      evidenceType = EvidenceType.LAB_SUBMISSION.name,
      verificationStatus = VerificationStatus.VERIFIED.name,
      evidenceQualityScore = 95,
      independenceScore = 95,
      transferabilityScore = 90,
      reasoningQualityScore = 92,
      evidenceHash = sha256("Volatile memory triage artifact for Alpha"),
      confidenceDeclared = 90,
      confidenceCalibrated = 90,
      hintsUsed = 0,
      retries = 0,
      createdAt = now
    )
    repository.recordEvidence(evA)

    // Learner B: Cryptographic Protocols with Weak Recall
    val capB = CapabilityEntity(
      id = "cap_beta_01",
      learnerId = learnerB,
      skillKey = "tls_crypto_handshake",
      name = "TLS 1.3 Handshake Protocols",
      category = CapabilityCategory.CRYPTOGRAPHY.name,
      knowledgeScore = 80,
      recallScore = 30, // Low recall
      applicationScore = 75,
      investigationScore = 75,
      transferScore = 70,
      explanationScore = 75,
      uncertaintyResilienceScore = 70,
      independenceScore = 80,
      evidenceQualityScore = 80,
      currentConfidence = 60,
      historicalCapabilityScore = 50
    )
    repository.saveCapability(capB)

    val evB = CapabilityEvidenceEntity(
      id = "ev_beta_01",
      capabilityId = capB.id,
      learnerId = learnerB,
      evidenceType = EvidenceType.CODE_ARTIFACT.name,
      verificationStatus = VerificationStatus.VERIFIED.name,
      evidenceQualityScore = 80,
      independenceScore = 80,
      transferabilityScore = 70,
      reasoningQualityScore = 75,
      evidenceHash = sha256("Diffie-Hellman RFC 8446 verify for Beta"),
      confidenceDeclared = 60,
      confidenceCalibrated = 60,
      hintsUsed = 1,
      retries = 1,
      createdAt = now
    )
    repository.recordEvidence(evB)

    // 1. Execute pipeline for Learner A
    val resA = AegoraRepository.executeCapabilityPipeline(learnerA, repository)
    assertEquals(learnerA, resA.learnerId)
    assertEquals(1, resA.capabilityResults.size)
    assertEquals("linux_forensics", resA.capabilityResults.first().skillKey)
    assertTrue("Learner A must demonstrate mastery", resA.capabilityResults.first().isDemonstrated)
    assertEquals(learnerA, CyberOperatingSystemV12Engine.cyberTwin60.value.learnerId)

    // 2. Execute pipeline for Learner B
    val resB = AegoraRepository.executeCapabilityPipeline(learnerB, repository)
    assertEquals(learnerB, resB.learnerId)
    assertEquals(1, resB.capabilityResults.size)
    assertEquals("tls_crypto_handshake", resB.capabilityResults.first().skillKey)
    assertFalse("Learner B must not demonstrate mastery due to recall", resB.capabilityResults.first().isDemonstrated)
    assertEquals(MasteryGateType.RECALL, resB.primaryBottleneckGate)
    assertEquals(learnerB, CyberOperatingSystemV12Engine.cyberTwin60.value.learnerId)

    // 3. Prove that A's evidence cannot affect B's result
    val actionsForB = AegoraRepository.predictiveNextActions.value
    assertTrue("Learner B actions must target TLS Handshake or Recall Drill",
      actionsForB.any { it.title.contains("TLS", ignoreCase = true) || it.title.contains("Recall", ignoreCase = true) }
    )
    assertFalse("Learner B actions must NOT contain Learner A's forensics tasks",
      actionsForB.any { it.title.contains("Linux Incident Forensics", ignoreCase = true) }
    )

    // 4. Verify boundary security: injecting A's evidence into B's evaluation throws IllegalArgumentException
    try {
      engine.evaluateCapability(
        capability = capB,
        evidence = listOf(evA), // Illegal cross-learner tampering!
        assessments = emptyList()
      )
      fail("Expected IllegalArgumentException when evaluating cross-learner evidence")
    } catch (e: IllegalArgumentException) {
      assertTrue(e.message!!.contains("Cross-learner evidence isolation violation"))
    }
  }

  // ============================================================================
  // 3. DETERMINISM TEST: IDENTICAL INPUTS YIELD IDENTICAL RESULTS
  // ============================================================================

  @Test
  fun testDeterminism_PipelineExecutesWithZeroVariance() = runBlocking {
    val learnerId = "learner_determinism_test"
    val skillKey = "buffer_overflow_triage"
    val capId = "cap_det_001"
    val fixedTimestamp = 1750000000000L

    val capability = CapabilityEntity(
      id = capId,
      learnerId = learnerId,
      skillKey = skillKey,
      name = "Buffer Overflow & ASLR Bypass",
      category = CapabilityCategory.VULNERABILITY_RESEARCH.name,
      knowledgeScore = 88,
      recallScore = 85,
      applicationScore = 82,
      investigationScore = 80,
      transferScore = 78,
      explanationScore = 80,
      uncertaintyResilienceScore = 82,
      independenceScore = 85,
      evidenceQualityScore = 85,
      currentConfidence = 82,
      historicalCapabilityScore = 80,
      lastVerifiedAt = fixedTimestamp - 86400000L
    )
    repository.saveCapability(capability)

    val evidence = CapabilityEvidenceEntity(
      id = "ev_det_001",
      capabilityId = capId,
      learnerId = learnerId,
      evidenceType = EvidenceType.LAB_SUBMISSION.name,
      verificationStatus = VerificationStatus.VERIFIED.name,
      evidenceQualityScore = 85,
      independenceScore = 88,
      transferabilityScore = 80,
      reasoningQualityScore = 85,
      evidenceHash = sha256("Deterministic payload artifact 42"),
      confidenceDeclared = 80,
      confidenceCalibrated = 80,
      hintsUsed = 0,
      retries = 0,
      createdAt = fixedTimestamp - 43200000L
    )
    repository.recordEvidence(evidence)

    // Run 1
    val run1 = AegoraIntelligenceOrchestrator.executeCapabilityIntelligencePipeline(
      learnerId = learnerId,
      repository = repository,
      engine = engine,
      cyberTwinAdapter = cyberTwinAdapter,
      nextBestActionAdapter = nextBestActionAdapter,
      currentTime = fixedTimestamp
    )

    // Run 2 (identical parameters & state)
    val run2 = AegoraIntelligenceOrchestrator.executeCapabilityIntelligencePipeline(
      learnerId = learnerId,
      repository = repository,
      engine = engine,
      cyberTwinAdapter = cyberTwinAdapter,
      nextBestActionAdapter = nextBestActionAdapter,
      currentTime = fixedTimestamp
    )

    // 1. Verify identical capability result
    val res1 = run1.capabilityResults.first()
    val res2 = run2.capabilityResults.first()
    assertEquals(res1.historicalCapabilityScore, res2.historicalCapabilityScore)
    assertEquals(res1.currentConfidence, res2.currentConfidence)
    assertEquals(res1.isDemonstrated, res2.isDemonstrated)
    assertEquals(res1.overallMasteryStatus, res2.overallMasteryStatus)
    assertEquals(res1.whyThisScore, res2.whyThisScore)
    assertEquals(res1.recommendedAction, res2.recommendedAction)

    // 2. Verify identical gate results
    assertEquals(res1.gateResults.size, res2.gateResults.size)
    for (gate in MasteryGateType.values()) {
      val g1 = res1.gateResults[gate]
      val g2 = res2.gateResults[gate]
      assertEquals(g1?.score, g2?.score)
      assertEquals(g1?.isPassed, g2?.isPassed)
    }

    // 3. Verify identical bottleneck
    assertEquals(run1.primaryBottleneckGate, run2.primaryBottleneckGate)
    assertEquals(res1.limitingGate, res2.limitingGate)

    // 4. Verify identical Cyber Twin impact
    assertEquals(run1.cyberTwinSnapshot.overallScore, run2.cyberTwinSnapshot.overallScore)
    assertEquals(run1.cyberTwinSnapshot.skillPassportLevel, run2.cyberTwinSnapshot.skillPassportLevel)
    for (dim in TwinDimensionV12.values()) {
      assertEquals(
        run1.cyberTwinSnapshot.dimensions[dim]?.currentState,
        run2.cyberTwinSnapshot.dimensions[dim]?.currentState
      )
      assertEquals(
        run1.cyberTwinSnapshot.dimensions[dim]?.trend,
        run2.cyberTwinSnapshot.dimensions[dim]?.trend
      )
    }

    // 5. Verify identical recommendation / Next Best Actions
    assertEquals(run1.nextBestActions.size, run2.nextBestActions.size)
    for (i in run1.nextBestActions.indices) {
      val a1 = run1.nextBestActions[i]
      val a2 = run2.nextBestActions[i]
      assertEquals(a1.id, a2.id)
      assertEquals(a1.title, a2.title)
      assertEquals(a1.category, a2.category)
      assertEquals(a1.urgencyScore, a2.urgencyScore)
      assertEquals(a1.primaryReason, a2.primaryReason)
      assertEquals(a1.destinationTag, a2.destinationTag)
    }
  }
}
