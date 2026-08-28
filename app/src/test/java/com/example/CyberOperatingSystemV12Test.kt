package com.example

import com.example.intelligence.CyberOperatingSystemV12Engine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class CyberOperatingSystemV12Test {

  @Test
  fun testCyberTwin60All27DimensionsCompletenessAndExplainability() {
    val twin = CyberOperatingSystemV12Engine.cyberTwin60.value
    assertNotNull("Cyber Twin 6.0 snapshot must not be null", twin)
    assertTrue("Overall capability score must be positive", twin.overallScore > 0)
    assertEquals("Should track all 27 distinct dynamic dimensions", 27, twin.dimensions.size)

    twin.dimensions.forEach { (dim, explainability) ->
      assertEquals("Dimension key and inner dimension enum must match", dim, explainability.dimension)
      assertTrue("Score must be in valid 0..100 range", explainability.currentState in 0..100)
      assertTrue("Evidence count must be non-negative", explainability.evidenceCount >= 0)
      assertTrue("Evidence quality must be in 0..100", explainability.evidenceQuality in 0..100)
      assertTrue("Confidence must be in 0..100", explainability.confidence in 0..100)
      assertTrue("Algorithmic estimate label must be true", explainability.isAlgorithmicEstimate)
      assertTrue("Recommended action must not be blank", explainability.recommendedAction.isNotBlank())
    }
  }

  @Test
  fun testCapabilityVsKnowledgeEngineDivergence() {
    val profiles = CyberOperatingSystemV12Engine.conceptCapabilityProfiles.value
    assertTrue("Capability profiles list must not be empty", profiles.isNotEmpty())

    val sqli = profiles.first { it.conceptName.contains("SQL Injection") }
    assertTrue("Knowledge score should reflect high theoretical understanding", sqli.knowledgeScore >= 90)
    assertTrue("Capability gap should show difference between theory and application", sqli.capabilityGap > 0)
    assertTrue("Prescriptive fix must be provided", sqli.prescriptiveFix.isNotBlank())
  }

  @Test
  fun testTransferabilityEngineCrossContextEvaluation() {
    val transferList = CyberOperatingSystemV12Engine.transferTests.value
    assertTrue("Transfer tests must exist", transferList.isNotEmpty())

    val testItem = transferList.first()
    assertTrue("Contexts tested map must not be empty", testItem.contextsTested.isNotEmpty())
    assertTrue("Recognition score must be valid", testItem.recognitionScore in 0..100)
    assertTrue("Transfer score must be valid", testItem.transferScore in 0..100)
    assertTrue("Evaluation note must not be blank", testItem.evaluationNote.isNotBlank())
  }

  @Test
  fun testUnknownUnknownEngineMetacognitiveCalibration() {
    val unknowns = CyberOperatingSystemV12Engine.unknownUnknowns.value
    assertTrue("Unknown-unknown gaps must be populated", unknowns.isNotEmpty())

    val item = unknowns.first()
    assertTrue("Self confidence should exceed actual performance for gap detection", item.selfConfidence > item.actualPerformance)
    assertTrue("Gap detected flag must be true", item.gapDetected)
    assertTrue("Respectful feedback must be present and constructive", item.respectfulFeedback.contains("confidence is currently ahead"))
    assertTrue("Calibration exercise must not be blank", item.calibrationExercisePrompt.isNotBlank())
  }

  @Test
  fun testCyberDecisionLabAndUncertaintyAction() {
    val scenarios = CyberOperatingSystemV12Engine.decisionLabScenarios.value
    assertTrue("Decision lab scenarios must exist", scenarios.isNotEmpty())

    val firstScenario = scenarios.first()
    assertTrue("Must have conflicting alerts", firstScenario.conflictingAlerts.isNotEmpty())
    assertTrue("Time limit must be positive", firstScenario.timeLimitSeconds > 0)

    CyberOperatingSystemV12Engine.executeDecisionLabAction(firstScenario.id, DecisionActionType.CONTAIN)
    val updated = CyberOperatingSystemV12Engine.decisionLabScenarios.value.first { it.id == firstScenario.id }
    assertEquals("Action should be updated to CONTAIN", DecisionActionType.CONTAIN, updated.selectedAction)
    assertTrue("Debrief should explain containment impact", updated.postDecisionDebrief.contains("Host Isolated"))
  }

  @Test
  fun testAttackerJourneyAndDigitalCrimeSceneMode() {
    val journeys = CyberOperatingSystemV12Engine.attackerJourneys.value
    assertTrue("Attacker journeys must exist", journeys.isNotEmpty())

    val journey = journeys.first()
    assertTrue("Journey must span MITRE stages", journey.stages.size >= 8)

    val crimeScene = CyberOperatingSystemV12Engine.digitalCrimeScene.value
    assertTrue("Crime scene artifacts must exist", crimeScene.isNotEmpty())
    val firstArtifact = crimeScene.first()
    assertTrue("Artifact must have cryptographic chain-of-custody hash", firstArtifact.chainOfCustodyHash.startsWith("sha256:"))

    val originalPreserved = firstArtifact.isPreserved
    CyberOperatingSystemV12Engine.togglePreserveArtifact(firstArtifact.id)
    val toggled = CyberOperatingSystemV12Engine.digitalCrimeScene.value.first { it.id == firstArtifact.id }
    assertEquals("Preservation state should toggle", !originalPreserved, toggled.isPreserved)
  }

  @Test
  fun testSocShiftAndCyberFatigueSimulation() {
    val session = CyberOperatingSystemV12Engine.socShiftSession.value
    assertTrue("SOC Shift must have alerts", session.alerts.isNotEmpty())
    assertTrue("Triage accuracy must be positive", session.triageAccuracyPercent > 0)
    assertTrue("Simulation must be explicitly labeled", session.isSimulationExplicitlyLabeled)

    val alert = session.alerts.first()
    CyberOperatingSystemV12Engine.triageSocAlert(alert.alertId, "Isolated host WS-ACCT-04")
    val updatedSession = CyberOperatingSystemV12Engine.socShiftSession.value
    val updatedAlert = updatedSession.alerts.first { it.alertId == alert.alertId }
    assertEquals("Alert action should be recorded", "Isolated host WS-ACCT-04", updatedAlert.triagedAction)

    val fatigue = CyberOperatingSystemV12Engine.cyberFatigueScenario.value
    assertTrue("Noise ratio should be high to simulate real shift", fatigue.noiseEventsCount > fatigue.criticalSignalsCount)
    assertTrue("Fatigue root-cause analysis must be present", fatigue.whyMissedAnalysis.isNotBlank())
  }

  @Test
  fun testMultiPersonSimulationAndBoardroomBriefing() {
    val multiPerson = CyberOperatingSystemV12Engine.multiPersonSimulations.value
    assertTrue("Multi-person scenarios must exist", multiPerson.isNotEmpty())

    val scenario = multiPerson.first()
    assertTrue("Conversation history must contain diverse roles", scenario.conversationHistory.size >= 3)
    val roles = scenario.conversationHistory.map { it.senderRole }.toSet()
    assertTrue("Should include multiple distinct organizational roles", roles.size >= 3)

    val boardroom = CyberOperatingSystemV12Engine.boardroomBriefings.value
    assertTrue("Boardroom briefings must exist", boardroom.isNotEmpty())
    val briefing = boardroom.first()
    assertTrue("Clarity score must be positive", briefing.clarityScore in 0..100)
    assertTrue("Board feedback must not be blank", briefing.boardFeedback.isNotBlank())
  }

  @Test
  fun testCyberCommunicationLabEvaluation() {
    val evaluations = CyberOperatingSystemV12Engine.communicationEvaluations.value
    assertTrue("Communication evaluations must exist", evaluations.isNotEmpty())

    val eval = evaluations.first()
    assertTrue("Accuracy score must be valid", eval.accuracyScore in 0..100)
    assertTrue("Clarity score must be valid", eval.clarityScore in 0..100)
    assertTrue("AI critique must not be blank", eval.aiCritique.isNotBlank())
  }

  @Test
  fun testResearchIntelligenceDeskAndClaimVerification() {
    val researchItems = CyberOperatingSystemV12Engine.researchDesk.value
    assertTrue("Research desk must have curated intel items", researchItems.isNotEmpty())
    researchItems.forEach { item ->
      assertTrue("Source url must not be blank", item.sourceUrl.isNotBlank())
      assertTrue("Untrusted content quarantine must be active by default", item.isUntrustedContentQuarantined)
    }

    val contradictedResult = CyberOperatingSystemV12Engine.verifySecurityClaim("This software is 100% unhackable and completely secure forever.")
    assertEquals("Should contradict absolute 100% security claim", ClaimVerificationVerdict.CONTRADICTED, contradictedResult.verdict)
    assertTrue("Evidence basis must cite standards", contradictedResult.evidenceBasis.contains("NIST"))

    val supportedResult = CyberOperatingSystemV12Engine.verifySecurityClaim("Hardware FIDO2 MFA keys protect against automated credential stuffing.")
    assertEquals("Should support evidence-backed MFA claim", ClaimVerificationVerdict.SUPPORTED, supportedResult.verdict)
  }

  @Test
  fun testCareerPathSimulatorAndJobReadinessDeconstruction() {
    val blueprints = CyberOperatingSystemV12Engine.careerPathBlueprints.value
    assertTrue("Career path blueprints must exist", blueprints.isNotEmpty())

    val socPath = blueprints.first { it.role == CyberCareerRoleV12.SOC_ANALYST }
    assertTrue("Match percentage must be valid", socPath.currentCapabilityMatchPercent in 0..100)
    assertTrue("Must include non-placement disclaimer", socPath.noPlacementGuaranteeDisclaimer.contains("employment not guaranteed"))

    val readinessList = CyberOperatingSystemV12Engine.jobReadinessDeconstruction.value
    assertTrue("Job readiness deconstruction must exist", readinessList.isNotEmpty())
    val readiness = readinessList.first()
    assertTrue("Ready areas must not be empty", readiness.readyAreas.isNotEmpty())
    assertTrue("Gap areas must not be empty", readiness.gapAreas.isNotEmpty())
  }

  @Test
  fun testPortfolioAuditorAndProjectGenerator() {
    val auditReport = CyberOperatingSystemV12Engine.auditPortfolio(
      repoUrl = "https://github.com/aegora/detection-engine",
      markdown = "# Sigma Rules Repository\nAutomated detection rules targeting MITRE ATT&CK T1059 and T1558 with Sysmon logs."
    )
    assertNotNull("Audit report must not be null", auditReport)
    assertTrue("Technical depth score must be valid", auditReport.technicalDepthScore in 0..100)
    assertTrue("Actionable improvements should be provided", auditReport.actionableImprovements.isNotEmpty())

    val blueprints = CyberOperatingSystemV12Engine.projectBlueprints30.value
    assertTrue("Project blueprints must exist", blueprints.isNotEmpty())
    val proj = blueprints.first()
    assertTrue("Milestones must exist", proj.milestones.isNotEmpty())
    assertTrue("Learning outcomes must exist", proj.learningOutcomes.isNotEmpty())
  }

  @Test
  fun testSkillEvidenceGraphMasteryGatesAndBossIncidents() {
    val evidenceNodes = CyberOperatingSystemV12Engine.skillEvidenceNodes.value
    assertTrue("Evidence nodes must exist", evidenceNodes.isNotEmpty())
    val node = evidenceNodes.first()
    assertTrue("Evidence hash must be valid SHA-256", node.evidenceHash.startsWith("sha256:"))
    assertTrue("Evidence freshness must be positive", node.freshnessDays >= 0)

    val masteryGates = CyberOperatingSystemV12Engine.masteryGates.value
    assertTrue("Mastery gates must exist", masteryGates.isNotEmpty())
    val l1Gate = masteryGates.first { it.gateTitle.contains("SOC L1") }
    assertTrue("L1 Gate should be unlocked with verified proofs", l1Gate.isUnlocked)

    val bossList = CyberOperatingSystemV12Engine.bossIncidents.value
    assertTrue("Boss incidents must exist", bossList.isNotEmpty())
    val boss = bossList.first()
    assertTrue("Boss incident must have multi-stage phases", boss.multiStagePhases.size >= 4)
  }

  @Test
  fun testKnowledgeVaultAiMemoryAndPrivacyAudit() {
    val initialVault = CyberOperatingSystemV12Engine.personalKnowledgeVault.value
    assertTrue("Initial knowledge vault must not be empty", initialVault.isNotEmpty())

    CyberOperatingSystemV12Engine.addVaultEntry("Wireshark Filter Tip", "CheatSheet", "tcp.flags.syn==1 and tcp.flags.ack==0")
    val updatedVault = CyberOperatingSystemV12Engine.personalKnowledgeVault.value
    assertEquals("Vault should have 1 additional entry", initialVault.size + 1, updatedVault.size)

    val memoryState = CyberOperatingSystemV12Engine.aiMemoryConsent.value
    assertTrue("AI memory items should be listed", memoryState.rememberedItems.isNotEmpty())

    val origConsent = memoryState.isPersonalizationEnabled
    CyberOperatingSystemV12Engine.togglePersonalizationConsent()
    assertEquals("Consent state should toggle", !origConsent, CyberOperatingSystemV12Engine.aiMemoryConsent.value.isPersonalizationEnabled)

    val privacy = CyberOperatingSystemV12Engine.privacyAudit.value
    assertTrue("Privacy audit data categories must not be empty", privacy.dataCategoriesStored.isNotEmpty())
    assertTrue("Export must be available", privacy.exportAvailable)
  }

  @Test
  fun testAiEvaluationCenterAndRedTeamShield() {
    val evaluations = CyberOperatingSystemV12Engine.aiEvaluationScores.value
    assertTrue("AI evaluations must exist", evaluations.isNotEmpty())
    evaluations.forEach { eval ->
      assertTrue("Accuracy score must be high", eval.accuracyScore >= 90)
      assertEquals("Prompt injection defense rate should be 100%", "100%", eval.promptInjectionDefenseRate)
    }

    val redTeam = CyberOperatingSystemV12Engine.aiRedTeamTests.value
    assertTrue("Red team test cases must exist", redTeam.isNotEmpty())
    val testCase = redTeam.first()
    assertTrue("Shield verification flag must be true", testCase.isShieldVerified)
    assertTrue("Actual defense result must confirm quarantine", testCase.actualDefenseResult.contains("QUARANTINED"))
  }

  @Test
  fun testBriefingsDebriefsAndProficiencyProgression() {
    val daily = CyberOperatingSystemV12Engine.dailyBrief.value
    assertTrue("Daily mission must not be blank", daily.todaysMission.isNotBlank())
    assertTrue("Skill at risk must not be blank", daily.skillAtRisk.isNotBlank())

    val weekly = CyberOperatingSystemV12Engine.weeklyDebrief.value
    assertTrue("Weekly debrief must list improvements", weekly.whatImproved.isNotEmpty())
    assertTrue("Evidence created count must be non-negative", weekly.evidenceCreatedCount >= 0)

    val monthly = CyberOperatingSystemV12Engine.monthlyCapabilityReview.value
    assertTrue("Monthly capability deltas must exist", monthly.capabilityDeltas.isNotEmpty())

    CyberOperatingSystemV12Engine.switchProficiencyTier(LearnerProficiencyTier.ADVANCED)
    assertEquals("Proficiency tier should switch to Advanced", LearnerProficiencyTier.ADVANCED, CyberOperatingSystemV12Engine.proficiencyTier.value)
  }
}
