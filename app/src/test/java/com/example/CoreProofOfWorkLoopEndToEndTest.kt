package com.example

import com.example.capability.*
import com.example.data.db.*
import com.example.model.*
import com.example.platform.CrossPlatformMissionBridge
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * AEGORA — CORE PROOF-OF-WORK LOOP END-TO-END SUITE
 *
 * Validates the core product loop:
 * MISSION -> AI ANALYSIS -> EVIDENCE -> HUMAN DECISION -> AUTHORITATIVE VERIFICATION
 * -> AI FAILURE DETECTION -> FAILURE PATTERN -> ADAPTIVE CHALLENGE -> IMPROVEMENT -> PROOF
 */
class CoreProofOfWorkLoopEndToEndTest {

  private lateinit var failurePatternManager: AuthoritativeFailurePatternManager
  private lateinit var adaptiveManager: AdaptiveAdversaryChallengeManager

  @Before
  fun setup() {
    failurePatternManager = AuthoritativeFailurePatternManager()
    adaptiveManager = AdaptiveAdversaryChallengeManager(failurePatternManager)
  }

  // --------------------------------------------------------------------------
  // CASE A: Human Catches AI Failure (The Killer Moment)
  // --------------------------------------------------------------------------

  @Test
  fun testCaseA_HumanCatchesAiFailure_EndToEndProofGenerated() {
    val learnerUid = "learner_secops_01"
    val missionId = "lab_suspicious_login"
    val claimId = "claim_login_malicious_ip"
    val auditEvidence = listOf("tl_01", "tl_02", "tl_03")

    // Human operator checks telemetry and correctly challenges the unsupported claim
    val result = CrossPlatformMissionBridge.evaluateAiClaimDecision(
      attemptId = "att_test_01",
      missionId = missionId,
      claimId = claimId,
      learnerDecision = LearnerAiDecision.CHALLENGE_AI,
      selectedEvidenceIds = auditEvidence
    )

    // Verify Authoritative Outcome
    assertEquals(VerificationOutcomeStatus.AI_FAILURE_DETECTED, result.outcome)
    assertTrue("AI failure must be detected", result.isAiFailureDetected)
    assertTrue("Evidence must be verified", result.evidenceVerified)
    assertEquals("AI FAILURE DETECTED ✓", result.headline)
    assertTrue("Proof digest must be generated", result.evidenceDigest.startsWith("sha256:aegora_ai_claim_"))
    assertNull("No failure autopsy should be generated on success", result.failureAutopsy)

    // Verify CapabilityEvidenceEntity integration with cryptographic proof
    val verifiedCapability = CapabilityEvidenceEntity(
      id = "cap_ai_triage_01",
      learnerId = learnerUid,
      capabilityId = "cap_threat_detection",
      missionId = missionId,
      attemptId = "att_test_01",
      evidenceType = EvidenceType.SIMULATION_TELEMETRY.name,
      evidenceHash = result.evidenceDigest,
      verificationStatus = VerificationStatus.VERIFIED.name
    )
    assertNotNull(verifiedCapability.evidenceHash)
    assertTrue(verifiedCapability.evidenceHash.contains("sha256"))
  }

  // --------------------------------------------------------------------------
  // CASE B: Recovery Path (Reasoning Error -> Autopsy -> Adaptive Challenge -> Improvement)
  // --------------------------------------------------------------------------

  @Test
  fun testCaseB_RecoveryPath_AutopsyToAdaptiveImprovementProof() {
    val learnerUid = "learner_analyst_42"
    val missionId = "lab_suspicious_login"
    val claimId = "claim_login_malicious_ip"

    // Step 1: Human incorrectly accepts unsupported AI claim
    val initialResult = CrossPlatformMissionBridge.evaluateAiClaimDecision(
      attemptId = "att_test_02",
      missionId = missionId,
      claimId = claimId,
      learnerDecision = LearnerAiDecision.ACCEPT_AI,
      selectedEvidenceIds = emptyList()
    )

    assertEquals(VerificationOutcomeStatus.AI_CLAIM_NOT_VERIFIED, initialResult.outcome)
    assertFalse(initialResult.isAiFailureDetected)
    assertEquals("AI CLAIM NOT VERIFIED", initialResult.headline)
    assertEquals(FailureModeType.EVIDENCE_OVERWEIGHTING, initialResult.detectedFailurePattern)

    // Step 2: Verify Failure Autopsy has all 7 mandatory elements
    val autopsy = initialResult.failureAutopsy
    assertNotNull("Failure autopsy must be populated on reasoning error", autopsy)
    assertTrue("1. Your decision must be populated", autopsy!!.yourDecision.isNotBlank())
    assertTrue("2. AI claim must be populated", autopsy.aiClaim.isNotBlank())
    assertNotNull("3. Evidence you used must be present", autopsy.evidenceYouUsed)
    assertTrue("4. Evidence that mattered must be present", autopsy.evidenceThatMattered.contains("tl_01"))
    assertTrue("5. What went wrong must be present", autopsy.whatWentWrong.contains("Evidence Overweighting"))
    assertEquals(FailureModeType.EVIDENCE_OVERWEIGHTING, autopsy.canonicalFailureMode)
    assertTrue("6. Better reasoning must be present", autopsy.betterReasoning.isNotBlank())
    assertTrue("7. Next challenge title must be present", autopsy.nextChallengeTitle.isNotBlank())

    // Step 3: Record observation in authoritative pattern manager
    failurePatternManager.recordObservation(
      authenticatedUid = learnerUid,
      missionId = missionId,
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.EVIDENCE_OVERWEIGHTING,
          isDetected = true,
          observedSymptom = autopsy.whatWentWrong,
          rootCauseCausalLink = autopsy.betterReasoning,
          actionableRemediation = autopsy.nextChallengeTitle
        )
      )
    )

    // Step 4: System generates targeted adaptive challenge locking to EVIDENCE_OVERWEIGHTING
    val challenge = adaptiveManager.generateTargetedChallenge(authenticatedUid = learnerUid)
    assertEquals(FailureModeType.EVIDENCE_OVERWEIGHTING, challenge.targetFailureMode)
    assertTrue(challenge.evidencePool.isNotEmpty())
    assertTrue(challenge.actionOptions.isNotEmpty())

    // Step 5: Test submission with insufficient reasoning (< 15 chars)
    val failResReasoning = adaptiveManager.evaluateChallengeSubmission(
      challengeId = challenge.challengeId,
      learnerUid = learnerUid,
      selectedActionId = "act_analyze_host_execution",
      selectedEvidenceIds = listOf("evi_intel_01", "evi_process_01"),
      reasoning = "too short"
    )
    assertFalse("Must fail when reasoning < 15 characters", failResReasoning.isPassed)

    // Step 6: Test submission succumbing to biased action
    val failResBiasedAction = adaptiveManager.evaluateChallengeSubmission(
      challengeId = challenge.challengeId,
      learnerUid = learnerUid,
      selectedActionId = "act_nuke_workstation",
      selectedEvidenceIds = listOf("evi_intel_01", "evi_process_01"),
      reasoning = "The AI told me to nuke the workstation so I did."
    )
    assertFalse("Must fail when biased action chosen", failResBiasedAction.isPassed)

    // Step 7: Successful submission with correct action, required evidence, and sound reasoning
    val successRes = adaptiveManager.evaluateChallengeSubmission(
      challengeId = challenge.challengeId,
      learnerUid = learnerUid,
      selectedActionId = "act_analyze_host_execution",
      selectedEvidenceIds = listOf("evi_intel_01", "evi_process_01"),
      reasoning = "Sysmon Event ID 1 process lineage reveals curl.exe executed by authorized security auditor, refuting co-pilot."
    )

    assertTrue("Adaptive challenge must pass", successRes.isPassed)
    assertTrue("Improvement must be verified", successRes.isImprovementVerified)
    assertEquals("IMPROVEMENT VERIFIED ✓", successRes.headline)
    assertEquals(FailureModeType.EVIDENCE_OVERWEIGHTING, successRes.previousFailureMode)
    assertNotNull("Verified proof artifact ID must be generated", successRes.verifiedProofArtifactId)
    assertTrue(successRes.verifiedProofArtifactId!!.startsWith("proof_adaptive_"))
    assertNotNull(successRes.demonstratedImprovementSummary)
    assertTrue(successRes.demonstratedImprovementSummary!!.contains("Targeted reasoning error not reproduced"))
  }

  // --------------------------------------------------------------------------
  // CASE C: Security Boundaries & Learner Isolation
  // --------------------------------------------------------------------------

  @Test
  fun testCaseC_SecurityBoundaries_LearnerIsolationAndForeignEvidence() {
    val learnerA = "learner_alpha"
    val learnerB = "learner_beta"

    // Seed pattern and generate challenge for learner A
    failurePatternManager.recordObservation(
      authenticatedUid = learnerA,
      missionId = "m_01",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.PREMATURE_ESCALATION,
          isDetected = true,
          observedSymptom = "Hasty escalation",
          rootCauseCausalLink = "Lack of baseline triage",
          actionableRemediation = "Verify health before reboot"
        )
      )
    )

    val challengeA = adaptiveManager.generateTargetedChallenge(authenticatedUid = learnerA)

    // 1. Learner B cannot submit answers for Learner A's challenge
    try {
      adaptiveManager.evaluateChallengeSubmission(
        challengeId = challengeA.challengeId,
        learnerUid = learnerB,
        selectedActionId = "act_investigate_baseline",
        selectedEvidenceIds = listOf("evi_auth_01", "evi_health_01"),
        reasoning = "Learner B attempting to forge submission."
      )
      fail("Expected SecurityException when submitting another user's challenge.")
    } catch (e: SecurityException) {
      assertTrue(e.message!!.contains("belongs to a different learner"))
    }

    // 2. Foreign evidence outside the challenge evidence pool must be rejected
    try {
      adaptiveManager.evaluateChallengeSubmission(
        challengeId = challengeA.challengeId,
        learnerUid = learnerA,
        selectedActionId = "act_investigate_baseline",
        selectedEvidenceIds = listOf("foreign_evidence_injected_01"),
        reasoning = "Injecting foreign telemetry."
      )
      fail("Expected IllegalArgumentException for foreign evidence.")
    } catch (e: IllegalArgumentException) {
      assertTrue(e.message!!.contains("Foreign evidence"))
    }
  }
}
