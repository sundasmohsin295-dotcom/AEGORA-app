package com.example

import com.example.capability.*
import com.example.model.FailureModeType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthoritativeFailurePatternSecurityTest {

  private lateinit var manager: AuthoritativeFailurePatternManager

  @Before
  fun setup() {
    manager = AuthoritativeFailurePatternManager()
  }

  // 1. Unauthenticated caller cannot create authoritative failure patterns
  @Test
  fun testUnauthenticatedAccessDenied() {
    val detection = FailurePatternDetectionResult(
      pattern = FailureModeType.PREMATURE_ESCALATION,
      isDetected = true,
      observedSymptom = "Premature reboot triggered.",
      rootCauseCausalLink = "Root cause",
      actionableRemediation = "Remediation"
    )

    try {
      manager.recordObservation(
        authenticatedUid = "",
        missionId = "lab_suspicious_login",
        detectedResults = listOf(detection)
      )
      fail("Expected IllegalArgumentException for unauthenticated UID")
    } catch (e: IllegalArgumentException) {
      assertTrue(e.message!!.contains("Authenticated learner UID is required"))
    }
  }

  // 2. User A cannot access User B's failure patterns (Learner isolation)
  @Test
  fun testCrossUserAccessDenied() {
    val userA = "uid_user_alpha"
    val userB = "uid_user_bravo"

    val detection = FailurePatternDetectionResult(
      pattern = FailureModeType.CONFIRMATION_BIAS,
      isDetected = true,
      observedSymptom = "Biased triage on user A.",
      rootCauseCausalLink = "Root cause",
      actionableRemediation = "Remediation"
    )

    manager.recordObservation(userA, "mission_1", listOf(detection))

    val userAPatterns = manager.getPatternsForLearner(userA)
    val userBPatterns = manager.getPatternsForLearner(userB)

    assertEquals(1, userAPatterns.size)
    assertEquals(0, userBPatterns.size)
    assertNotEquals(userAPatterns, userBPatterns)
  }

  // 3. Client-provided forged failure labels are ignored/rejected
  @Test
  fun testForgedClientFailureLabelsRejected() {
    val user = "uid_user_test"
    val rejected = manager.sanitizeAndRejectClientInjectedPattern(
      authenticatedUid = user,
      clientClaimedPattern = FailureModeType.CONFIRMATION_BIAS,
      clientClaimedConfidence = "HIGH_CONFIDENCE"
    )
    assertFalse("Direct client injection of failure patterns must be rejected", rejected)
  }

  // 4. Client cannot directly set HIGH confidence
  @Test
  fun testClientCannotDirectlySetHighConfidence() {
    val user = "uid_user_test"
    val detection = FailurePatternDetectionResult(
      pattern = FailureModeType.INSUFFICIENT_CORRELATION,
      isDetected = true,
      observedSymptom = "Symptom",
      rootCauseCausalLink = "Link",
      actionableRemediation = "Remediation"
    )

    // First observation must be OBSERVED, never HIGH_CONFIDENCE regardless of client desire
    val recorded = manager.recordObservation(user, "mission_1", listOf(detection))
    assertEquals(1, recorded.size)
    assertEquals(FailurePatternConfidenceTier.OBSERVED, recorded[0].confidenceTier)
    assertEquals(1, recorded[0].observationCount)
  }

  // 5. Repeated observations are deterministic
  @Test
  fun testRepeatedObservationsAreDeterministic() {
    val user = "uid_user_test"
    val detection = FailurePatternDetectionResult(
      pattern = FailureModeType.EVIDENCE_OVERWEIGHTING,
      isDetected = true,
      observedSymptom = "Overweighted IOC",
      rootCauseCausalLink = "Link",
      actionableRemediation = "Remediation"
    )

    // Observation 1 -> OBSERVED
    val obs1 = manager.recordObservation(user, "mission_1", listOf(detection))
    assertEquals(FailurePatternConfidenceTier.OBSERVED, obs1[0].confidenceTier)
    assertEquals(1, obs1[0].observationCount)

    // Observation 2 -> REPEATED
    val obs2 = manager.recordObservation(user, "mission_2", listOf(detection))
    assertEquals(FailurePatternConfidenceTier.REPEATED, obs2[0].confidenceTier)
    assertEquals(2, obs2[0].observationCount)

    // Observation 3 -> HIGH_CONFIDENCE
    val obs3 = manager.recordObservation(user, "mission_3", listOf(detection))
    assertEquals(FailurePatternConfidenceTier.HIGH_CONFIDENCE, obs3[0].confidenceTier)
    assertEquals(3, obs3[0].observationCount)

    val highConfList = manager.getHighConfidencePatterns(user)
    assertEquals(1, highConfList.size)
    assertEquals(FailureModeType.EVIDENCE_OVERWEIGHTING, highConfList[0].pattern)
  }

  // 6. Failure records cannot be written outside the authenticated learner scope
  @Test
  fun testFailureRecordsScopedToAuthenticatedLearner() {
    val user = "uid_user_isolated"
    val detection = FailurePatternDetectionResult(
      pattern = FailureModeType.CONTEXT_IGNORANCE,
      isDetected = true,
      observedSymptom = "Ignored VPN context",
      rootCauseCausalLink = "Link",
      actionableRemediation = "Remediation"
    )

    val records = manager.recordObservation(user, "mission_1", listOf(detection))
    for (r in records) {
      assertEquals(user, r.learnerId)
    }
  }
}
