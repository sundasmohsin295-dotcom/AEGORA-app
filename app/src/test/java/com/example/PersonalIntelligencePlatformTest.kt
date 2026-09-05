package com.example

import com.example.capability.*
import com.example.data.db.CapabilityEvidenceEntity
import com.example.data.db.MasteryStatus
import com.example.data.db.VerificationStatus
import com.example.intelligence.PersonalIntelligencePlatformEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PersonalIntelligencePlatformTest {

  @Before
  fun setUp() {
    PersonalIntelligencePlatformEngine.resetToDefaultDemonstrationState()
  }

  @Test
  fun testLearnerIsolationViolationThrowsException() {
    val results = listOf(
      createMockCapabilityResult(
        learnerId = "operator_alice",
        capabilityId = "cap_alice_1",
        name = "Alice SIEM"
      )
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      PersonalIntelligencePlatformEngine.evaluateAndLoadProfile(
        learnerId = "operator_bob",
        capabilityResults = results
      )
    }

    assertTrue(
      "Should contain isolation violation message",
      exception.message?.contains("Learner isolation violation") == true
    )
  }

  @Test
  fun testLearnerIsolationEvidenceMismatchThrowsException() {
    val results = listOf(
      createMockCapabilityResult(
        learnerId = "operator_alice",
        capabilityId = "cap_alice_1",
        name = "Alice SIEM"
      )
    )
    val evidence = listOf(
      CapabilityEvidenceEntity(
        id = "ev_001",
        capabilityId = "cap_alice_1",
        learnerId = "operator_charlie",
        evidenceType = "SOC_ALERT_TRIAGE_LOG",
        evidenceHash = "0xABCDEF1234567890",
        evidenceQualityScore = 90,
        independenceScore = 85,
        verificationStatus = VerificationStatus.VERIFIED.name,
        createdAt = System.currentTimeMillis()
      )
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      PersonalIntelligencePlatformEngine.evaluateAndLoadProfile(
        learnerId = "operator_alice",
        capabilityResults = results,
        allEvidence = evidence
      )
    }

    assertTrue(
      "Should prevent cross-learner evidence leakage",
      exception.message?.contains("Learner isolation violation") == true
    )
  }

  @Test
  fun testHonestEmptyStateWhenNoEvidenceExists() {
    PersonalIntelligencePlatformEngine.evaluateAndLoadProfile(
      learnerId = "novice_operator_99",
      capabilityResults = emptyList(),
      allEvidence = emptyList()
    )

    val uiState = PersonalIntelligencePlatformEngine.profileUiState.value
    assertTrue("UI State should be Empty", uiState is PersonalIntelligenceUiState.Empty)

    val emptyState = uiState as PersonalIntelligenceUiState.Empty
    assertEquals("novice_operator_99", emptyState.learnerId)
    assertTrue(emptyState.message.contains("No verified capability evidence"))

    val summaries = PersonalIntelligencePlatformEngine.clusterSummaries.value
    assertEquals(4, summaries.size)
    summaries.forEach { summary ->
      assertEquals(0, summary.score)
      assertTrue(summary.whatLearnerKnows.isEmpty())
      assertTrue(summary.whatLearnerCanPerform.isEmpty())
    }

    assertTrue(PersonalIntelligencePlatformEngine.capabilityNodes.value.isEmpty())
    assertTrue(PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.value.isEmpty())
  }

  @Test
  fun testDeterministicClusterScoringAndGateMapping() {
    val targetLearner = "operator_tactical"

    val results = listOf(
      createMockCapabilityResult(
        learnerId = targetLearner,
        capabilityId = "cap_net_01",
        name = "Packet Extraction",
        category = "FOUNDATION_NETWORK",
        understandScore = 90,
        recallScore = 80,
        applyScore = 85,
        investigateScore = 85,
        transferScore = 70,
        uncertaintyScore = 75,
        explainScore = 80,
        independenceScore = 85
      ),
      createMockCapabilityResult(
        learnerId = targetLearner,
        capabilityId = "cap_siem_02",
        name = "SIEM Detection",
        category = "ACTIVE_DEFENSE_SIEM",
        understandScore = 80,
        recallScore = 70,
        applyScore = 75,
        investigateScore = 85,
        transferScore = 60,
        uncertaintyScore = 65,
        explainScore = 75,
        independenceScore = 80
      )
    )

    val evidence = listOf(
      CapabilityEvidenceEntity(
        id = "ev_tactical_1",
        capabilityId = "cap_net_01",
        learnerId = targetLearner,
        evidenceType = "PACKET_CAPTURE_PCAP",
        evidenceHash = "0x9876543210FEDCBA",
        evidenceQualityScore = 95,
        independenceScore = 90,
        verificationStatus = VerificationStatus.VERIFIED.name,
        createdAt = System.currentTimeMillis()
      )
    )

    PersonalIntelligencePlatformEngine.evaluateAndLoadProfile(
      learnerId = targetLearner,
      capabilityResults = results,
      allEvidence = evidence
    )

    val uiState = PersonalIntelligencePlatformEngine.profileUiState.value
    assertTrue("UI state should be Ready", uiState is PersonalIntelligenceUiState.Ready)

    val readyState = uiState as PersonalIntelligenceUiState.Ready
    assertEquals(targetLearner, readyState.learnerId)

    val summaries = PersonalIntelligencePlatformEngine.clusterSummaries.value
    val foundation = summaries.first { it.cluster == CognitiveClusterType.FOUNDATION }
    val activeDefense = summaries.first { it.cluster == CognitiveClusterType.ACTIVE_DEFENSE }
    val generalization = summaries.first { it.cluster == CognitiveClusterType.GENERALIZATION_STRESS }
    val metacognitive = summaries.first { it.cluster == CognitiveClusterType.METACOGNITIVE_STRATEGIC }

    // Foundation average of (90, 80, 80, 70) = 80
    assertEquals(80, foundation.score)
    // Active Defense average of (85, 85, 75, 85) = 82
    assertEquals(82, activeDefense.score)
    // Generalization average of (70, 75, 60, 65) = 67
    assertEquals(67, generalization.score)
    // Metacognitive average of (80, 85, 75, 80) = 80
    assertEquals(80, metacognitive.score)

    // Primary bottleneck should be the lowest cluster: GENERALIZATION_STRESS (67)
    assertEquals(CognitiveClusterType.GENERALIZATION_STRESS, readyState.primaryBottleneckCluster)

    // Evidence mapping check
    val proofs = PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.value
    assertEquals(1, proofs.size)
    assertEquals("cap_net_01", proofs[0].capabilityId)
    assertTrue(proofs[0].isCryptographicallySigned)
  }

  @Test
  fun testProveItChallengeExecutionAndCryptographicProof() {
    val initialProofCount = PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.value.size

    val success = PersonalIntelligencePlatformEngine.completeProveItChallenge(
      challengeId = "prove_siem_beacon",
      selectedOptionId = "opt_c2_isolate" // optimal action
    )

    assertTrue("Optimal option should succeed challenge", success)
    val updatedProofs = PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.value
    assertEquals(initialProofCount + 1, updatedProofs.size)

    val latestProof = updatedProofs.first()
    assertEquals("prove_siem_beacon", latestProof.capabilityId)
    assertTrue(latestProof.isCryptographicallySigned)
  }

  @Test
  fun testFailureIntelligenceRecording() {
    val initialCount = PersonalIntelligencePlatformEngine.mistakeRecords.value.size

    PersonalIntelligencePlatformEngine.recordMistake(
      missionTitle = "Ransomware Triage Delta",
      failureType = FailureModeType.PREMATURE_CONCLUSION,
      observedSymptom = "Attributed LockBit IOC to DarkSide affiliate",
      rootCauseCausalLink = "Premature IOC matching before mutex confirmation",
      constructiveFeedback = "Verify mutex and compilation timestamp before cluster attribution",
      remediationMission = "Adversary Infrastructure Dissection"
    )

    val updatedCount = PersonalIntelligencePlatformEngine.mistakeRecords.value.size
    assertEquals(initialCount + 1, updatedCount)

    val latest = PersonalIntelligencePlatformEngine.mistakeRecords.value.first()
    assertEquals("Ransomware Triage Delta", latest.missionTitle)
    assertEquals(FailureModeType.PREMATURE_CONCLUSION, latest.failureType)
  }

  // ==========================================
  // HELPER FACTORIES
  // ==========================================

  private fun createMockCapabilityResult(
    learnerId: String,
    capabilityId: String,
    name: String,
    category: String = "ACTIVE_DEFENSE",
    understandScore: Int = 85,
    recallScore: Int = 80,
    applyScore: Int = 85,
    investigateScore: Int = 80,
    transferScore: Int = 75,
    uncertaintyScore: Int = 70,
    explainScore: Int = 80,
    independenceScore: Int = 85
  ): CapabilityAssessmentResult {
    val gateResults = mapOf(
      MasteryGateType.UNDERSTAND to GateResult(MasteryGateType.UNDERSTAND, understandScore, understandScore >= 70, MasteryStatus.DEMONSTRATED, 2, understandScore),
      MasteryGateType.RECALL to GateResult(MasteryGateType.RECALL, recallScore, recallScore >= 70, MasteryStatus.DEMONSTRATED, 2, recallScore),
      MasteryGateType.APPLY to GateResult(MasteryGateType.APPLY, applyScore, applyScore >= 70, MasteryStatus.DEMONSTRATED, 2, applyScore),
      MasteryGateType.INVESTIGATE to GateResult(MasteryGateType.INVESTIGATE, investigateScore, investigateScore >= 70, MasteryStatus.DEMONSTRATED, 2, investigateScore),
      MasteryGateType.TRANSFER to GateResult(MasteryGateType.TRANSFER, transferScore, transferScore >= 70, MasteryStatus.DEMONSTRATED, 2, transferScore),
      MasteryGateType.UNCERTAINTY_RESILIENCE to GateResult(MasteryGateType.UNCERTAINTY_RESILIENCE, uncertaintyScore, uncertaintyScore >= 70, MasteryStatus.DEMONSTRATED, 2, uncertaintyScore),
      MasteryGateType.EXPLAIN to GateResult(MasteryGateType.EXPLAIN, explainScore, explainScore >= 70, MasteryStatus.DEMONSTRATED, 2, explainScore)
    )

    val isAllPassed = gateResults.values.all { it.isPassed }

    return CapabilityAssessmentResult(
      capabilityId = capabilityId,
      learnerId = learnerId,
      skillKey = "skill_$capabilityId",
      name = name,
      category = category,
      historicalCapabilityScore = 80,
      currentConfidence = 80,
      isDemonstrated = isAllPassed,
      overallMasteryStatus = if (isAllPassed) MasteryStatus.DEMONSTRATED else MasteryStatus.PROVISIONAL,
      gateResults = gateResults,
      limitingGate = if (isAllPassed) null else MasteryGateType.TRANSFER,
      evidenceQualityBreakdown = EvidenceQualityBreakdown(
        effectiveQualityScore = 90,
        authenticityScore = 90,
        independenceScore = independenceScore,
        transferabilityScore = 85,
        diversityFactor = 1.0,
        verifiedCount = 3,
        pendingCount = 0,
        unverifiedCount = 0,
        revokedExcludedCount = 0,
        totalConsidered = 3
      ),
      retentionRisk = RetentionRisk.LOW,
      daysSinceLastVerified = 1,
      limitingFactors = if (isAllPassed) emptyList() else listOf("Transfer gate below baseline"),
      positiveFactors = listOf("Consistent high independence across lab executions"),
      recommendedAction = "Cross-Environment Transfer Mission",
      whyThisScore = "Demonstrated across multiple telemetry verification gates."
    )
  }
}
