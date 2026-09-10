package com.example

import com.example.capability.*
import com.example.model.FailureModeType
import com.example.model.InvestigationQuestion
import com.example.model.InvestigationTimelineEvent
import com.example.model.ProgressiveHints
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DeterministicFailurePatternDetectorTest {

  private lateinit var canonicalAnswerKey: MissionAnswerKey

  @Before
  fun setup() {
    canonicalAnswerKey = MissionAnswerKey(
      missionId = "lab_suspicious_login",
      questions = listOf(
        InvestigationQuestion(
          id = "q_login_1",
          stepNumber = 1,
          questionText = "Initial access type?",
          options = listOf("Adversary Brute-force", "Routine password change", "DDoS", "Kerberos renewal"),
          correctOptionIndex = 0,
          hints = ProgressiveHints("c1", "e1", "d1", "exp1")
        ),
        InvestigationQuestion(
          id = "q_login_2",
          stepNumber = 2,
          questionText = "Why impossible travel?",
          options = listOf("7 minutes between Moscow and Austin", "Austin blacklisted", "VPN morning forbidden", "Browser obsolete"),
          correctOptionIndex = 0,
          hints = ProgressiveHints("c2", "e2", "d2", "exp2")
        ),
        InvestigationQuestion(
          id = "q_login_3",
          stepNumber = 3,
          questionText = "Immediate containment action?",
          options = listOf("Revoke sessions & reset pass", "Ignore until end of day", "Reboot DC", "Delete mailbox"),
          correctOptionIndex = 0,
          hints = ProgressiveHints("c3", "e3", "d3", "exp3")
        )
      ),
      validTimelineEvents = listOf(
        InvestigationTimelineEvent("tl_01", "2026-08-25 08:14:02 UTC", "FAILED_LOGIN", "DC-01", "j.smith", "Failure", "raw1", true),
        InvestigationTimelineEvent("tl_02", "2026-08-25 08:14:15 UTC", "FAILED_LOGIN", "DC-01", "j.smith", "Failure", "raw2", true),
        InvestigationTimelineEvent("tl_03", "2026-08-25 08:15:40 UTC", "SUCCESS_LOGIN", "DC-01", "j.smith", "Success", "raw3", true),
        InvestigationTimelineEvent("tl_04", "2026-08-25 08:22:10 UTC", "SUCCESS_LOGIN", "VPN-US", "j.smith", "Success", "raw4", false)
      ),
      validIocSet = setOf("198.51.100.12", "WIN-WORK-99", "j.smith"),
      falsePositiveIocSet = setOf("decoy_clean_backup_server.internal"),
      containmentQuestionId = "q_login_3",
      optimalContainmentIndex = 0,
      prematureEscalationIndex = 2
    )
  }

  // -------------------------------------------------------------
  // 1. PREMATURE_ESCALATION
  // -------------------------------------------------------------
  @Test
  fun testPrematureEscalation_TruePositive() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf(
        "q_login_1" to 0,
        "q_login_2" to 0,
        "q_login_3" to 2 // Reboot Domain Controller prematurely!
      ),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "Domain controller looks infected, rebooting right now."
    )

    val result = DeterministicFailurePatternDetector.detectPrematureEscalation(submission, canonicalAnswerKey)
    assertTrue("Should detect PREMATURE_ESCALATION", result.isDetected)
    assertEquals(FailureModeType.PREMATURE_ESCALATION, result.pattern)
    assertTrue(result.observedSymptom.contains("service interruption/reboot"))
  }

  @Test
  fun testPrematureEscalation_TrueNegative() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf(
        "q_login_1" to 0,
        "q_login_2" to 0,
        "q_login_3" to 0 // Revoke sessions & reset pass
      ),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "Isolating compromised user session and credentials first.",
      executionTimeSeconds = 60
    )

    val result = DeterministicFailurePatternDetector.detectPrematureEscalation(submission, canonicalAnswerKey)
    assertFalse("Should NOT detect PREMATURE_ESCALATION for proper containment", result.isDetected)
  }

  // -------------------------------------------------------------
  // 2. EVIDENCE_OVERWEIGHTING
  // -------------------------------------------------------------
  @Test
  fun testEvidenceOverweighting_TruePositive() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 0, "q_login_2" to 0, "q_login_3" to 0),
      selectedIocs = listOf("198.51.100.12", "phantom_trojan_hash_99999", "10.0.99.1"), // Ungrounded IOCs
      reasoningText = "The trojan hash explains the attack."
    )

    val result = DeterministicFailurePatternDetector.detectEvidenceOverweighting(submission, canonicalAnswerKey)
    assertTrue("Should detect EVIDENCE_OVERWEIGHTING when citing ungrounded IOCs", result.isDetected)
    assertEquals(FailureModeType.EVIDENCE_OVERWEIGHTING, result.pattern)
    assertTrue(result.affectedEvidenceIocs.contains("phantom_trojan_hash_99999"))
  }

  @Test
  fun testEvidenceOverweighting_TrueNegative() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 0, "q_login_2" to 0, "q_login_3" to 0),
      selectedIocs = listOf("198.51.100.12", "WIN-WORK-99"),
      reasoningText = "Confirmed strictly from Event ID 4625 logs."
    )

    val result = DeterministicFailurePatternDetector.detectEvidenceOverweighting(submission, canonicalAnswerKey)
    assertFalse("Should NOT detect EVIDENCE_OVERWEIGHTING when IOCs are grounded in logs", result.isDetected)
  }

  // -------------------------------------------------------------
  // 3. CONFIRMATION_BIAS
  // -------------------------------------------------------------
  @Test
  fun testConfirmationBias_TruePositive() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf(
        "q_login_1" to 1, // Routine password change! (Decoy benign hypothesis)
        "q_login_2" to 0,
        "q_login_3" to 0
      ),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "Looks like standard routine password update behavior."
    )

    val result = DeterministicFailurePatternDetector.detectConfirmationBias(submission, canonicalAnswerKey)
    assertTrue("Should detect CONFIRMATION_BIAS when fixating on benign decoy", result.isDetected)
    assertEquals(FailureModeType.CONFIRMATION_BIAS, result.pattern)
  }

  @Test
  fun testConfirmationBias_TrueNegative() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 0, "q_login_2" to 0, "q_login_3" to 0),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "Attacker attempted brute force with rapid bad passwords followed by single success."
    )

    val result = DeterministicFailurePatternDetector.detectConfirmationBias(submission, canonicalAnswerKey)
    assertFalse("Should NOT detect CONFIRMATION_BIAS for objective analysis", result.isDetected)
  }

  // -------------------------------------------------------------
  // 4. INSUFFICIENT_CORRELATION
  // -------------------------------------------------------------
  @Test
  fun testInsufficientCorrelation_TruePositive() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf(
        "q_login_1" to 0,
        "q_login_2" to 3, // Browser obsolete (failed to correlate Moscow and Austin logins across 7 minutes)
        "q_login_3" to 0
      ),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "The browser version looks old."
    )

    val result = DeterministicFailurePatternDetector.detectInsufficientCorrelation(submission, canonicalAnswerKey)
    assertTrue("Should detect INSUFFICIENT_CORRELATION when missing cross-event delta", result.isDetected)
    assertEquals(FailureModeType.INSUFFICIENT_CORRELATION, result.pattern)
  }

  @Test
  fun testInsufficientCorrelation_TrueNegative() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 0, "q_login_2" to 0, "q_login_3" to 0),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "Correlating 08:15 Moscow to 08:22 Austin proves impossible travel in 7 minutes."
    )

    val result = DeterministicFailurePatternDetector.detectInsufficientCorrelation(submission, canonicalAnswerKey)
    assertFalse("Should NOT detect INSUFFICIENT_CORRELATION when correlation is accurate", result.isDetected)
  }

  // -------------------------------------------------------------
  // 5. WEAK_UNCERTAINTY_HANDLING
  // -------------------------------------------------------------
  @Test
  fun testWeakUncertaintyHandling_TruePositive() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 2, "q_login_2" to 1, "q_login_3" to 1), // Incorrect answers
      selectedIocs = emptyList(),
      reasoningText = "IDK", // Superficial reasoning with zero uncertainty bounds
      hintsRequestedCount = 0
    )

    val result = DeterministicFailurePatternDetector.detectWeakUncertaintyHandling(submission, canonicalAnswerKey)
    assertTrue("Should detect WEAK_UNCERTAINTY_HANDLING for unreasoned failed guess", result.isDetected)
    assertEquals(FailureModeType.WEAK_UNCERTAINTY_HANDLING, result.pattern)
  }

  @Test
  fun testWeakUncertaintyHandling_TrueNegative() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 0, "q_login_2" to 0, "q_login_3" to 0),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "Confidence is high for initial compromise based on rapid Event 4625 events; impossible travel confirmed by delta calculation."
    )

    val result = DeterministicFailurePatternDetector.detectWeakUncertaintyHandling(submission, canonicalAnswerKey)
    assertFalse("Should NOT detect WEAK_UNCERTAINTY_HANDLING when reasoning and bounds are detailed", result.isDetected)
  }

  // -------------------------------------------------------------
  // 6. CONTEXT_IGNORANCE
  // -------------------------------------------------------------
  @Test
  fun testContextIgnorance_TruePositive() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 0, "q_login_2" to 0, "q_login_3" to 0),
      selectedIocs = listOf("73.189.44.10"), // Flagging legitimate corporate VPN from Austin!
      reasoningText = "73.189.44.10 is malicious attacker gateway."
    )

    val result = DeterministicFailurePatternDetector.detectContextIgnorance(submission, canonicalAnswerKey)
    assertTrue("Should detect CONTEXT_IGNORANCE when flagging assigned employee VPN", result.isDetected)
    assertEquals(FailureModeType.CONTEXT_IGNORANCE, result.pattern)
  }

  @Test
  fun testContextIgnorance_TrueNegative() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 0, "q_login_2" to 0, "q_login_3" to 0),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "Austin IP 73.189.44.10 matches legitimate home VPN, while Moscow IP 198.51.100.12 is adversary."
    )

    val result = DeterministicFailurePatternDetector.detectContextIgnorance(submission, canonicalAnswerKey)
    assertFalse("Should NOT detect CONTEXT_IGNORANCE when context is respected", result.isDetected)
  }

  // -------------------------------------------------------------
  // 7. INCORRECT_PRIORITIZATION
  // -------------------------------------------------------------
  @Test
  fun testIncorrectPrioritization_TruePositive() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf(
        "q_login_1" to 0,
        "q_login_2" to 0,
        "q_login_3" to 1 // Option 1: Ignore alert until the end of the business day
      ),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "We have other tickets, we can look at this tomorrow."
    )

    val result = DeterministicFailurePatternDetector.detectIncorrectPrioritization(submission, canonicalAnswerKey)
    assertTrue("Should detect INCORRECT_PRIORITIZATION when deprioritizing active compromise", result.isDetected)
    assertEquals(FailureModeType.INCORRECT_PRIORITIZATION, result.pattern)
  }

  @Test
  fun testIncorrectPrioritization_TrueNegative() {
    val submission = LearnerMissionSubmission(
      missionId = "lab_suspicious_login",
      learnerId = "learner_test_01",
      selectedAnswers = mapOf("q_login_1" to 0, "q_login_2" to 0, "q_login_3" to 0),
      selectedIocs = listOf("198.51.100.12"),
      reasoningText = "Immediate containment: active session revoke and credential reset required as SEV-1."
    )

    val result = DeterministicFailurePatternDetector.detectIncorrectPrioritization(submission, canonicalAnswerKey)
    assertFalse("Should NOT detect INCORRECT_PRIORITIZATION for proper containment priority", result.isDetected)
  }
}
