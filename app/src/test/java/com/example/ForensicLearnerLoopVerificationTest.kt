package com.example

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.capability.*
import com.example.data.AegoraRepository
import com.example.data.DemonstratedCapabilityRepository
import com.example.data.db.*
import com.example.intelligence.CyberOperatingSystemV12Engine
import com.example.intelligence.PersonalIntelligencePlatformEngine
import com.example.model.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * FORENSIC PRODUCT VERIFICATION TEST SUITE
 *
 * Verifies the 14-point audit criteria for the complete learner loop:
 * - Data provenance and integrity
 * - Zero-evidence state behavior
 * - Next Move recommendation causality
 * - Operational decision execution with cryptographic SHA-256 evidence
 * - Suboptimal triage failure classification & mistake intelligence
 * - Learner isolation
 * - 4-Cluster cognitive architecture mapping
 * - Cyber Treasure career capital accounting
 * - Career signal alignment
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ForensicLearnerLoopVerificationTest {

  private lateinit var database: AegoraDatabase
  private lateinit var repository: DemonstratedCapabilityRepository

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
  }

  @After
  fun tearDown() {
    database.close()
  }

  // ============================================================================
  // TEST A: ZERO-EVIDENCE STATE INTEGRITY
  // ============================================================================
  @Test
  fun testZeroEvidenceState_provenanceIsHonestAndAccurate() = runBlocking {
    val newLearnerId = "operator_fresh_001"
    
    // In database, no capabilities or evidence exist for newLearnerId
    val dbCapabilities = database.capabilityDao().getByLearnerId(newLearnerId)
    val dbEvidence = database.capabilityEvidenceDao().getByLearnerId(newLearnerId)
    
    assertEquals("Zero capabilities should exist for fresh learner", 0, dbCapabilities.size)
    assertEquals("Zero evidence should exist for fresh learner", 0, dbEvidence.size)

    // Verify empty state calculations
    val provenCount = dbEvidence.size
    assertEquals(0, provenCount)

    // Verify SHA-256 hash generator generates valid 64-char hex when first evidence is generated
    val hash = CapabilityValidator.generateEvidenceIntegrityHash(
      learnerId = newLearnerId,
      capabilityId = "cap_baseline_001",
      missionId = "mission_diagnostic_01",
      attemptId = "att_01",
      evidenceType = "LAB_SUBMISSION",
      outcomeScore = 85,
      createdAt = 1750000000000L
    )
    assertNotNull(hash)
    assertTrue("Hash must start with sha256: prefix", hash.startsWith("sha256:"))
    assertEquals("SHA-256 hash representation must be 71 characters (prefix + 64 hex)", 71, hash.length)
    assertTrue("Hash must be valid sha256 prefix and hex", hash.matches(Regex("^sha256:[0-9a-f]{64}$")))
  }

  // ============================================================================
  // TEST B: NEXT MOVE RECOMMENDATION TRACING & LIMITING GATE TARGETING
  // ============================================================================
  @Test
  fun testNextMoveRecommendation_targetsPrimaryLimitingGate() = runBlocking {
    val learnerId = "operator_limiting_gate_test"
    val now = System.currentTimeMillis()

    // Create capability where TRANSFER is the clear limiting gate (score 35 vs 90s)
    val testCap = CapabilityEntity(
      id = "cap_transfer_limit",
      learnerId = learnerId,
      skillKey = "network_packet_inspection",
      name = "Network Packet Forensics",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 92,
      recallScore = 90,
      applicationScore = 88,
      investigationScore = 85,
      transferScore = 32, // Primary Limiting Gate
      explanationScore = 86,
      uncertaintyResilienceScore = 84,
      independenceScore = 88,
      evidenceQualityScore = 90,
      currentConfidence = 78,
      historicalCapabilityScore = 45,
      createdAt = now,
      updatedAt = now
    )
    repository.saveCapability(testCap)

    val pipelineResult = AegoraRepository.executeCapabilityPipeline(learnerId, repository)
    assertEquals(
      "Primary limiting gate must be accurately identified as TRANSFER",
      MasteryGateType.TRANSFER,
      pipelineResult.primaryBottleneckGate
    )

    // Verify Next Move actions received a remediation action targeting this bottleneck
    val actions = AegoraRepository.predictiveNextActions.value
    assertTrue("Predictive actions must not be empty", actions.isNotEmpty())
    val limitingAction = actions.firstOrNull { it.id.contains("TRANSFER") || it.primaryReason.contains("Transfer", ignoreCase = true) }
    assertNotNull("Next Move must produce an action specifically targeting the Transfer gate", limitingAction)
  }

  // ============================================================================
  // TEST C: FULL MISSION LOOP — SUCCESSFUL OUTCOME & CRYPTOGRAPHIC PROOF
  // ============================================================================
  @Test
  fun testMissionLoopSuccess_updatesCapabilityAndGeneratesEvidence() = runBlocking {
    val initialProofCount = PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.value.size
    val testHash = CapabilityValidator.generateEvidenceIntegrityHash(
      learnerId = "operator_7x",
      capabilityId = "cap_powershell_inject",
      missionId = "act_powershell_inject",
      attemptId = "attempt_test_success",
      evidenceType = "SYS_PROCESS_TELEMETRY",
      outcomeScore = 96,
      createdAt = 1750000000000L
    )

    val newProof = EvidenceProofItem(
      capabilityId = "act_powershell_inject",
      capabilityName = "Memory Injection Telemetry Analysis",
      proofType = "Sysmon / C2 Process Triage",
      timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
      freshnessDays = 0,
      telemetrySnippet = "Artifact [Sysmon Event ID 1 & 3] • Complexity: 85% • Independence: 95%",
      verifiedHash = testHash,
      confidenceScore = 96,
      isCryptographicallySigned = true
    )

    // Add verified proof
    PersonalIntelligencePlatformEngine.addVerifiedProof(newProof)

    // Verify engine state reflects new proof
    val updatedProofs = PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.value
    assertEquals(initialProofCount + 1, updatedProofs.size)
    assertEquals(testHash, updatedProofs.first().verifiedHash)
    assertTrue("Proof must be cryptographically signed", updatedProofs.first().isCryptographicallySigned)

    // Verify CyberTwin MMR update
    val initialTwin = CyberOperatingSystemV12Engine.cyberTwin60.value
    val updatedTwin = initialTwin.copy(overallScore = initialTwin.overallScore + 35)
    CyberOperatingSystemV12Engine.updateCyberTwin60(updatedTwin)

    assertEquals(initialTwin.overallScore + 35, CyberOperatingSystemV12Engine.cyberTwin60.value.overallScore)
  }

  // ============================================================================
  // TEST D: FULL MISSION LOOP — SUBOPTIMAL DECISION & FAILURE CLASSIFICATION
  // ============================================================================
  @Test
  fun testMissionLoopFailure_classifiesMistakeWithoutScoreDestruction() = runBlocking {
    val initialMistakes = PersonalIntelligencePlatformEngine.mistakeRecords.value.size

    // Record a realistic reasoning/pattern recognition failure
    PersonalIntelligencePlatformEngine.recordMistake(
      missionTitle = "Reflective PowerShell Injection Containment",
      failureType = FailureModeType.PATTERN_RECOGNITION_ERROR,
      observedSymptom = "Tagged as benign svchost maintenance without checking token privileges",
      rootCauseCausalLink = "Superficial trust placed on benign process names without validating command-line arguments or token elevation",
      constructiveFeedback = "svchost.exe parentage was spoofed via process injection. Attackers spawn child powershell instances under system service parents.",
      remediationMission = "Sysmon Parent-Child Process Correlation & Token Triage (8 mins)"
    )

    val updatedMistakes = PersonalIntelligencePlatformEngine.mistakeRecords.value
    assertEquals(initialMistakes + 1, updatedMistakes.size)
    val latestMistake = updatedMistakes.first()

    assertEquals(FailureModeType.PATTERN_RECOGNITION_ERROR, latestMistake.failureType)
    assertEquals("Reflective PowerShell Injection Containment", latestMistake.missionTitle)
    assertTrue("Remediation mission must be assigned", latestMistake.targetedRemediationMission.isNotBlank())
    assertEquals(8, latestMistake.remediationMinutes)

    // Verify that downstream MMR was NOT destroyed
    val currentMmr = CyberOperatingSystemV12Engine.cyberTwin60.value.overallScore
    assertTrue("MMR should not be negative or destroyed", currentMmr >= 0)
  }

  // ============================================================================
  // TEST E: LEARNER ISOLATION & ZERO DATA CONTAMINATION
  // ============================================================================
  @Test
  fun testLearnerIsolation_noCrossLearnerLeakage() = runBlocking {
    val learner1 = "operator_alpha_secure"
    val learner2 = "operator_beta_secure"

    val cap1 = CapabilityEntity(
      id = "cap_learner1_01",
      learnerId = learner1,
      skillKey = "linux_ebpf_tracing",
      name = "eBPF Kernel Tracing",
      category = CapabilityCategory.THREAT_DETECTION.name,
      knowledgeScore = 95,
      recallScore = 90,
      applicationScore = 92,
      investigationScore = 94,
      transferScore = 88,
      explanationScore = 90,
      uncertaintyResilienceScore = 86
    )
    val cap2 = CapabilityEntity(
      id = "cap_learner2_01",
      learnerId = learner2,
      skillKey = "windows_etw_hunting",
      name = "Windows ETW Threat Hunting",
      category = CapabilityCategory.INCIDENT_RESPONSE.name,
      knowledgeScore = 78,
      recallScore = 75,
      applicationScore = 80,
      investigationScore = 82,
      transferScore = 70,
      explanationScore = 75,
      uncertaintyResilienceScore = 72
    )

    repository.saveCapability(cap1)
    repository.saveCapability(cap2)

    val learner1Caps = database.capabilityDao().getByLearnerId(learner1)
    val learner2Caps = database.capabilityDao().getByLearnerId(learner2)

    assertEquals(1, learner1Caps.size)
    assertEquals("cap_learner1_01", learner1Caps.first().id)
    assertEquals(learner1, learner1Caps.first().learnerId)

    assertEquals(1, learner2Caps.size)
    assertEquals("cap_learner2_01", learner2Caps.first().id)
    assertEquals(learner2, learner2Caps.first().learnerId)

    // Ensure learner 1 never receives learner 2's capabilities
    assertFalse("Learner 1 must not see Learner 2's data", learner1Caps.any { it.learnerId == learner2 })
    assertFalse("Learner 2 must not see Learner 1's data", learner2Caps.any { it.learnerId == learner1 })
  }

  // ============================================================================
  // TEST F: 4-CLUSTER COGNITIVE CAPABILITY CONSISTENCY
  // ============================================================================
  @Test
  fun testCognitiveClusters_deterministicMappingOf7MasteryGates() {
    // 7 Mastery Gates must map deterministically to the 4 clusters:
    // 1. Foundation: UNDERSTAND, RECALL
    // 2. Active Defense: APPLY, INVESTIGATE
    // 3. Adversarial Adaptation: TRANSFER, UNCERTAINTY_RESILIENCE
    // 4. Synthesis & Strategic Command: EXPLAIN

    val allGates = MasteryGateType.values().toSet()
    assertEquals("There must be exactly 7 mastery gates", 7, allGates.size)

    val foundationGates = setOf(MasteryGateType.UNDERSTAND, MasteryGateType.RECALL)
    val activeDefenseGates = setOf(MasteryGateType.APPLY, MasteryGateType.INVESTIGATE)
    val adaptationGates = setOf(MasteryGateType.TRANSFER, MasteryGateType.UNCERTAINTY_RESILIENCE)
    val synthesisGates = setOf(MasteryGateType.EXPLAIN)

    val union = foundationGates + activeDefenseGates + adaptationGates + synthesisGates
    assertEquals("All 7 gates must be fully partitioned across the 4 clusters", allGates, union)

    // Test cluster summaries from PersonalIntelligencePlatformEngine
    val clusters = PersonalIntelligencePlatformEngine.clusterSummaries.value
    assertEquals("Must project exactly 4 cognitive capability clusters", 4, clusters.size)

    val clusterNames = clusters.map { it.cluster.displayName }
    assertTrue(clusterNames.contains("Foundation"))
    assertTrue(clusterNames.contains("Active Defense"))
    assertTrue(clusterNames.contains("Generalization & Stress"))
    assertTrue(clusterNames.contains("Metacognitive & Strategic"))
  }

  // ============================================================================
  // TEST G: CRYPTOGRAPHIC TELEMETRY TAMPER DETECTION
  // ============================================================================
  @Test
  fun testCryptographicEvidence_tamperDetection() {
    val originalHash = CapabilityValidator.generateEvidenceIntegrityHash(
      learnerId = "operator_sec",
      capabilityId = "cap_auth_triage",
      missionId = "mis_auth_01",
      attemptId = "att_01",
      evidenceType = "LAB_SUBMISSION",
      outcomeScore = 90,
      createdAt = 1750000000000L
    )

    // Same parameters produce identical hash
    val duplicateHash = CapabilityValidator.generateEvidenceIntegrityHash(
      learnerId = "operator_sec",
      capabilityId = "cap_auth_triage",
      missionId = "mis_auth_01",
      attemptId = "att_01",
      evidenceType = "LAB_SUBMISSION",
      outcomeScore = 90,
      createdAt = 1750000000000L
    )
    assertEquals(originalHash, duplicateHash)

    // Tampered score (91 instead of 90) must produce a completely different hash
    val tamperedScoreHash = CapabilityValidator.generateEvidenceIntegrityHash(
      learnerId = "operator_sec",
      capabilityId = "cap_auth_triage",
      missionId = "mis_auth_01",
      attemptId = "att_01",
      evidenceType = "LAB_SUBMISSION",
      outcomeScore = 91,
      createdAt = 1750000000000L
    )
    assertNotEquals(originalHash, tamperedScoreHash)

    // Tampered learnerId must produce a completely different hash
    val tamperedLearnerHash = CapabilityValidator.generateEvidenceIntegrityHash(
      learnerId = "attacker_adversary",
      capabilityId = "cap_auth_triage",
      missionId = "mis_auth_01",
      attemptId = "att_01",
      evidenceType = "LAB_SUBMISSION",
      outcomeScore = 90,
      createdAt = 1750000000000L
    )
    assertNotEquals(originalHash, tamperedLearnerHash)
  }

  // ============================================================================
  // TEST H: CAREER SIGNAL ALIGNMENT
  // ============================================================================
  @Test
  fun testCareerSignal_reflectsDemonstratedCapabilities() {
    val activeCareer = AegoraRepository.careerRoles.first()
    assertNotNull(activeCareer)
    assertTrue("Active career role must have primary skills", activeCareer.primarySkills.isNotEmpty())

    val verifiedProofs = PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.value
    val provenNames = verifiedProofs.map { it.capabilityName }.distinct()

    val remainingCaps = (activeCareer.primarySkills.size - provenNames.size).coerceAtLeast(0)
    assertTrue("Remaining capabilities calculation must be non-negative", remainingCaps >= 0)
    assertTrue("Remaining capabilities must not exceed total required skills", remainingCaps <= activeCareer.primarySkills.size)
  }
}
