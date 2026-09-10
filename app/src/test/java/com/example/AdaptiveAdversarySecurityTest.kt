package com.example

import com.example.capability.*
import com.example.model.FailureModeType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AdaptiveAdversarySecurityTest {

  private lateinit var failurePatternManager: AuthoritativeFailurePatternManager
  private lateinit var adversaryManager: AdaptiveAdversaryChallengeManager

  @Before
  fun setup() {
    failurePatternManager = AuthoritativeFailurePatternManager()
    adversaryManager = AdaptiveAdversaryChallengeManager(failurePatternManager)
  }

  // 1. Unauthenticated user cannot create an adaptive challenge
  @Test
  fun testUnauthenticatedUserCannotCreateAdaptiveChallenge() {
    try {
      adversaryManager.generateTargetedChallenge(authenticatedUid = "")
      fail("Expected IllegalArgumentException for unauthenticated user.")
    } catch (e: IllegalArgumentException) {
      assertTrue(e.message!!.contains("Authenticated UID is required"))
    }
  }

  // 2. User cannot request another learner's failure pattern
  @Test
  fun testUserCannotRequestAnotherLearnersFailurePattern() {
    val uidVictim = "learner_victim_99"
    val uidAttacker = "learner_attacker_01"

    // Seed victim with verified PREMATURE_ESCALATION
    failurePatternManager.recordObservation(
      authenticatedUid = uidVictim,
      missionId = "mission_01",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.PREMATURE_ESCALATION,
          isDetected = true,
          observedSymptom = "Premature escalation",
          rootCauseCausalLink = "Root cause",
          actionableRemediation = "Remediation"
        )
      )
    )

    // Attacker generates challenge; cannot access victim's pattern
    val attackerPatterns = failurePatternManager.getPatternsForLearner(uidAttacker)
    assertTrue("Attacker patterns must be isolated and empty", attackerPatterns.isEmpty())

    val attackerChallenge = adversaryManager.generateTargetedChallenge(authenticatedUid = uidAttacker)
    // Should fallback to default baseline because attacker has zero personal patterns
    assertEquals(FailureModeType.INSUFFICIENT_CORRELATION, attackerChallenge.targetFailureMode)
  }

  // 3. Client cannot select another failure mode as its authoritative target
  @Test
  fun testClientCannotSelectOwnTargetFailureMode() {
    val uid = "learner_real_77"

    // Server has authoritatively recorded CONFIRMATION_BIAS
    failurePatternManager.recordObservation(
      authenticatedUid = uid,
      missionId = "mission_ps_triage",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.CONFIRMATION_BIAS,
          isDetected = true,
          observedSymptom = "Dismissed alert due to maintenance window",
          rootCauseCausalLink = "Failed to examine payload",
          actionableRemediation = "Review script block logs"
        )
      )
    )

    // Client attempts to forge its target as PREMATURE_ESCALATION
    val challenge = adversaryManager.generateTargetedChallenge(
      authenticatedUid = uid,
      suggestedClientTarget = FailureModeType.PREMATURE_ESCALATION
    )

    // Server MUST use authoritative history (CONFIRMATION_BIAS), rejecting client suggestion
    assertEquals(FailureModeType.CONFIRMATION_BIAS, challenge.targetFailureMode)
    assertTrue(challenge.scenarioTitle.contains("Disconfirming", ignoreCase = true))
  }

  // 4. Client cannot set HIGH_CONFIDENCE
  @Test
  fun testClientCannotForgeHighConfidenceTier() {
    val uid = "learner_forge_tier"

    // Only 1 observation recorded (OBSERVED tier)
    failurePatternManager.recordObservation(
      authenticatedUid = uid,
      missionId = "mission_01",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.EVIDENCE_OVERWEIGHTING,
          isDetected = true,
          observedSymptom = "Anchored to IP reputation",
          rootCauseCausalLink = "Ignored curl host context",
          actionableRemediation = "Check process lineage"
        )
      )
    )

    // Client attempts to request HIGH_CONFIDENCE
    val challenge = adversaryManager.generateTargetedChallenge(
      authenticatedUid = uid,
      suggestedTier = AdaptiveChallengeTier.HIGH_CONFIDENCE
    )

    // Server MUST enforce OBSERVED tier because observationCount is 1
    assertEquals(AdaptiveChallengeTier.OBSERVED, challenge.challengeTier)
  }

  // 5. Client cannot modify hidden trap state or ground-truth answer
  @Test
  fun testHiddenAdversarialStateIsProtected() {
    val uid = "learner_sec_01"

    failurePatternManager.recordObservation(
      authenticatedUid = uid,
      missionId = "mission_01",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.PREMATURE_ESCALATION,
          isDetected = true,
          observedSymptom = "Premature DC reboot",
          rootCauseCausalLink = "Root cause",
          actionableRemediation = "Remediation"
        )
      )
    )

    val learnerChallenge = adversaryManager.generateTargetedChallenge(uid)
    val authoritativeState = adversaryManager.getAuthoritativeState(learnerChallenge.challengeId, uid)

    assertNotNull(authoritativeState)
    assertTrue("Authoritative state contains planted trap flag", authoritativeState!!.isUnsupportedPlantedTrap)
    assertEquals("act_investigate_baseline", authoritativeState.authoritativeCorrectActionId)

    // The learner-safe challenge payload does NOT contain any fields exposing whether it is a trap or what the correct answer is
    val payloadString = learnerChallenge.toString()
    assertFalse("Learner safe payload must NOT contain correct action", payloadString.contains("authoritativeCorrectActionId"))
    assertFalse("Learner safe payload must NOT contain isUnsupportedPlantedTrap", payloadString.contains("isUnsupportedPlantedTrap"))
  }

  // 6. Challenge selection is deterministic for the same authoritative state
  @Test
  fun testDeterministicChallengeSelectionForIdenticalState() {
    val uid = "learner_deterministic_01"

    failurePatternManager.recordObservation(
      authenticatedUid = uid,
      missionId = "mission_01",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.WEAK_UNCERTAINTY_HANDLING,
          isDetected = true,
          observedSymptom = "Overconfident guess",
          rootCauseCausalLink = "Missing packets",
          actionableRemediation = "Preserve uncertainty"
        )
      )
    )

    val challenge1 = adversaryManager.generateTargetedChallenge(uid)
    val challenge2 = adversaryManager.generateTargetedChallenge(uid)

    assertEquals(challenge1.targetFailureMode, challenge2.targetFailureMode)
    assertEquals(challenge1.challengeTier, challenge2.challengeTier)
    assertEquals(challenge1.targetedSkill, challenge2.targetedSkill)
  }

  // 7. Learner A cannot access Learner B's adaptive challenge state
  @Test
  fun testLearnerIsolationOnChallengeState() {
    val uidA = "learner_alice"
    val uidB = "learner_bob"

    failurePatternManager.recordObservation(
      authenticatedUid = uidA,
      missionId = "mission_alice",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.CONTEXT_IGNORANCE,
          isDetected = true,
          observedSymptom = "Ignored CI/CD role",
          rootCauseCausalLink = "Context ignored",
          actionableRemediation = "Check CMDB"
        )
      )
    )

    val aliceChallenge = adversaryManager.generateTargetedChallenge(uidA)

    try {
      adversaryManager.getAuthoritativeState(aliceChallenge.challengeId, authenticatedUid = uidB)
      fail("Expected SecurityException when Bob attempts to access Alice's challenge state")
    } catch (e: SecurityException) {
      assertTrue(e.message!!.contains("Learner isolation violation"))
    }
  }

  // 8. AI output cannot overwrite authoritative ground truth
  @Test
  fun testAiOutputCannotOverwriteAuthoritativeGroundTruth() {
    val uid = "learner_ai_test"

    failurePatternManager.recordObservation(
      authenticatedUid = uid,
      missionId = "m_seed",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.PREMATURE_ESCALATION,
          isDetected = true,
          observedSymptom = "Premature DC reboot",
          rootCauseCausalLink = "Root cause",
          actionableRemediation = "Remediation"
        )
      )
    )

    val challenge = adversaryManager.generateTargetedChallenge(uid)
    val authState = adversaryManager.getAuthoritativeState(challenge.challengeId, uid)!!

    // The AI Analyst claims "emergency reboot" with 94% confidence
    assertEquals("act_emergency_reboot_dc", challenge.aiAnalystClaim.recommendedAction)
    assertEquals(94, challenge.aiAnalystClaim.confidencePercentage)

    // BUT the authoritative ground truth remains strictly "act_investigate_baseline"
    assertEquals("act_investigate_baseline", authState.authoritativeCorrectActionId)
    assertTrue(authState.isUnsupportedPlantedTrap)
    assertNotEquals(challenge.aiAnalystClaim.recommendedAction, authState.authoritativeCorrectActionId)
  }

  // 9. All 7 Failure Modes produce the correct targeted policy
  @Test
  fun testAllSevenFailureModesProduceTargetedPolicy() {
    val allModes = listOf(
      FailureModeType.PREMATURE_ESCALATION,
      FailureModeType.EVIDENCE_OVERWEIGHTING,
      FailureModeType.CONFIRMATION_BIAS,
      FailureModeType.INSUFFICIENT_CORRELATION,
      FailureModeType.WEAK_UNCERTAINTY_HANDLING,
      FailureModeType.CONTEXT_IGNORANCE,
      FailureModeType.INCORRECT_PRIORITIZATION
    )

    for (mode in allModes) {
      val policy = adversaryManager.buildPolicy(mode, AdaptiveChallengeTier.OBSERVED)
      assertEquals(mode, policy.targetFailureMode)
      assertNotNull(policy.policyObjective)
      assertNotNull(policy.adversaryRole)
      assertNotNull(policy.recoveryCriteria)
      assertTrue(policy.evidenceRequirements.isNotEmpty())
    }
  }

  // 10. Progression: OBSERVED -> REPEATED -> HIGH_CONFIDENCE progression
  @Test
  fun testObservedRepeatedHighConfidenceProgression() {
    val uid = "learner_progression"

    // 1st observation -> OBSERVED
    failurePatternManager.recordObservation(
      authenticatedUid = uid,
      missionId = "m1",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.INCORRECT_PRIORITIZATION,
          isDetected = true,
          observedSymptom = "Toggled blog first",
          rootCauseCausalLink = "Noise distraction",
          actionableRemediation = "Prioritize exfil"
        )
      )
    )
    val c1 = adversaryManager.generateTargetedChallenge(uid)
    assertEquals(AdaptiveChallengeTier.OBSERVED, c1.challengeTier)

    // 2nd observation -> REPEATED
    failurePatternManager.recordObservation(
      authenticatedUid = uid,
      missionId = "m2",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.INCORRECT_PRIORITIZATION,
          isDetected = true,
          observedSymptom = "Toggled blog first",
          rootCauseCausalLink = "Noise distraction",
          actionableRemediation = "Prioritize exfil"
        )
      )
    )
    val c2 = adversaryManager.generateTargetedChallenge(uid)
    assertEquals(AdaptiveChallengeTier.REPEATED, c2.challengeTier)

    // 3rd observation -> HIGH_CONFIDENCE
    failurePatternManager.recordObservation(
      authenticatedUid = uid,
      missionId = "m3",
      detectedResults = listOf(
        FailurePatternDetectionResult(
          pattern = FailureModeType.INCORRECT_PRIORITIZATION,
          isDetected = true,
          observedSymptom = "Toggled blog first",
          rootCauseCausalLink = "Noise distraction",
          actionableRemediation = "Prioritize exfil"
        )
      )
    )
    val c3 = adversaryManager.generateTargetedChallenge(uid)
    assertEquals(AdaptiveChallengeTier.HIGH_CONFIDENCE, c3.challengeTier)
  }
}
