package com.example

import com.example.intelligence.CyberRealityIntelligenceV13Engine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class CyberRealityIntelligenceV13Test {

  @Test
  fun testCyberRealityEngine20EntitiesAndGraphRelationships() {
    val nodes = CyberRealityIntelligenceV13Engine.realityNodes.value
    assertTrue("Reality graph nodes must be populated", nodes.isNotEmpty())
    val entityTypes = nodes.map { it.type }.toSet()
    assertTrue("Should contain multiple entity categories", entityTypes.size >= 4)

    val edges = CyberRealityIntelligenceV13Engine.realityEdges.value
    assertTrue("Graph edges must be populated", edges.isNotEmpty())
    val edgeRelations = edges.map { it.relation }.toSet()
    assertTrue("Should contain diverse MITRE and system relations", edgeRelations.contains(RealityRelationTypeV13.USES))
  }

  @Test
  fun testRealTimeIntelligenceIngestionAndNormalization() {
    val feed = CyberRealityIntelligenceV13Engine.normalizedIntelFeed.value
    assertTrue("Normalized intel feed must not be empty", feed.isNotEmpty())

    feed.forEach { item ->
      assertTrue("Source url must not be blank", item.sourceUrl.isNotBlank())
      assertTrue("Publication date must not be blank", item.publicationDate.isNotBlank())
      assertTrue("Retrieval date must not be blank", item.retrievalDate.isNotBlank())
      assertTrue("Confidence must be in 0..100 range", item.confidencePercent in 0..100)
      assertTrue("SHA-256 hash must be valid format", item.sha256ContentHash.startsWith("sha256:"))
      assertTrue("Educational abstract must be provided", item.educationalAbstract.isNotBlank())
    }

    val liveStatus = CyberRealityIntelligenceV13Engine.liveIntelStatus.value
    assertNotNull("Live intel status must be tracked", liveStatus)
  }

  @Test
  fun testCyberTimelineAndMultiDomainFiltering() {
    val events = CyberRealityIntelligenceV13Engine.timelineEvents.value
    assertTrue("Timeline events must not be empty", events.isNotEmpty())

    val timeframes = events.map { it.timeframe }.toSet()
    assertTrue("Should include Today, This Week, This Month, or Historical", timeframes.contains(TimelineTimeframeV13.TODAY))

    CyberRealityIntelligenceV13Engine.setTimelineFilter("Identity")
    assertEquals("Timeline filter state should update", "Identity", CyberRealityIntelligenceV13Engine.selectedTimelineFilter.value)
  }

  @Test
  fun testPersonalImpactEngineAndPersonalThreatRadar() {
    val assessments = CyberRealityIntelligenceV13Engine.personalImpactAssessments.value
    assertTrue("Personal impact assessments must exist", assessments.isNotEmpty())

    val firstAssessment = assessments.first()
    assertTrue("Why it matters explanation must be visible", firstAssessment.whyItMattersToYou.isNotBlank())
    assertTrue("Matching career path must be specified", firstAssessment.matchingCareerPath.isNotBlank())
    assertTrue("Capability gap must be highlighted", firstAssessment.currentCapabilityGap.isNotBlank())
    assertTrue("Micro-drill duration must be positive", firstAssessment.recommendedMicroDrillMinutes > 0)

    val radarItems = CyberRealityIntelligenceV13Engine.personalThreatRadar.value
    assertTrue("Personal threat radar must rank items", radarItems.isNotEmpty())
    val firstRadar = radarItems.first()
    assertTrue("Rank must be positive", firstRadar.aggregateEducationalRank >= 1)
    assertTrue("Educational disclaimer must be present", firstRadar.disclaimer.contains("Educational relevance"))
  }

  @Test
  fun testEventToMissionGeneratorSafeEducationalAbstractions() {
    val missions = CyberRealityIntelligenceV13Engine.eventMissions.value
    assertTrue("Event missions must exist", missions.isNotEmpty())

    missions.forEach { mission ->
      assertTrue("Mission duration must be positive", mission.durationMinutes > 0)
      assertTrue("Objective must be clearly defined", mission.missionObjective.isNotBlank())
      assertTrue("Simulated telemetry snippet must be present", mission.simulatedTelemetrySnippet.isNotBlank())
      assertTrue("Required skill inputs must not be empty", mission.requiredSkillInputs.isNotEmpty())
      assertTrue("Harmful exploit code must be quarantined", mission.isHarmfulExploitQuarantined)
    }
  }

  @Test
  fun testCareerMarketIntelligenceAndSkillCompounding() {
    val marketSkills = CyberRealityIntelligenceV13Engine.marketSkillDemands.value
    assertTrue("Market skill demands must exist", marketSkills.isNotEmpty())

    val firstSkill = marketSkills.first()
    assertTrue("Verified mentions count must be positive", firstSkill.verifiedJobMentionsCount > 0)
    assertTrue("Data source must be transparently cited", firstSkill.dataSource.isNotBlank())
    assertTrue("Sample size must be stated", firstSkill.sampleSize.contains("Sample:"))

    val compoundingSkills = CyberRealityIntelligenceV13Engine.highLeverageSkills.value
    assertTrue("High-leverage skills must exist", compoundingSkills.isNotEmpty())
    val comp = compoundingSkills.first()
    assertTrue("Multiplier must be greater than 1.0", comp.leverageMultiplier > 1.0)
    assertTrue("Unlocked careers must span multiple paths", comp.unlockedCareers.size >= 3)
  }

  @Test
  fun testSkillDependenciesAndCareerWhatIfSimulator() {
    val dependencies = CyberRealityIntelligenceV13Engine.skillDependencies.value
    assertTrue("Skill dependency chains must exist", dependencies.isNotEmpty())
    val dep = dependencies.first()
    assertTrue("Prerequisite chain must have depth", dep.prerequisiteChain.size >= 3)
    assertTrue("Sequential justification must be clear", dep.whySequential.isNotBlank())

    val whatIfSoc = CyberRealityIntelligenceV13Engine.simulateCareerWhatIf("SOC Analyst (Tier 2)")
    assertNotNull("What-if result must not be null", whatIfSoc)
    assertTrue("Match percent must be in 0..100", whatIfSoc.currentMatchPercent in 0..100)
    assertTrue("Effort weeks must be positive", whatIfSoc.estimatedEffortWeeks > 0)
    assertTrue("Planning estimate disclaimer must be included", whatIfSoc.planningEstimateNotice.contains("not guaranteed"))
  }

  @Test
  fun testMasteryTransferGatesAndEvidenceQuality20() {
    val gates = CyberRealityIntelligenceV13Engine.masteryGatesV13.value
    assertTrue("Mastery gates must exist", gates.isNotEmpty())

    val initialUnlockedGate = gates.first { it.isDemonstratedCapabilityGranted }
    assertTrue("Completed gate must satisfy all 7 verification dimensions",
      initialUnlockedGate.understandsConcept &&
      initialUnlockedGate.recallAccuracy &&
      initialUnlockedGate.labApplicationVerified &&
      initialUnlockedGate.rawInvestigationPassed &&
      initialUnlockedGate.crossContextTransferred &&
      initialUnlockedGate.verbalExplanationClear &&
      initialUnlockedGate.uncertaintyResiliencePassed
    )

    val incompleteGate = gates.first { !it.isDemonstratedCapabilityGranted }
    CyberRealityIntelligenceV13Engine.completeMasteryGateCheck(incompleteGate.skillName)
    val updatedGate = CyberRealityIntelligenceV13Engine.masteryGatesV13.value.first { it.skillName == incompleteGate.skillName }
    assertTrue("Gate should now be marked as demonstrated", updatedGate.isDemonstratedCapabilityGranted)

    val evMetrics = CyberRealityIntelligenceV13Engine.evidenceQualityMetrics.value
    assertTrue("Evidence quality metrics must exist", evMetrics.isNotEmpty())
    val ev = evMetrics.first()
    assertTrue("Authenticity score must be valid", ev.authenticityScore in 0..100)
    assertTrue("Independence score must be valid", ev.independenceScore in 0..100)
    assertTrue("Complexity score must be valid", ev.complexityScore in 0..100)
  }

  @Test
  fun testPersonalLearningExperimentsAndSeasonSystem() {
    val experiments = CyberRealityIntelligenceV13Engine.learningExperiments.value
    assertTrue("Personal learning experiments must exist", experiments.isNotEmpty())
    val exp = experiments.first()
    assertTrue("Scientific disclaimer must be present", exp.scientificDisclaimer.contains("Empirical personal pattern"))
    assertTrue("Retention delta must be recorded", exp.retentionDelta.isNotBlank())

    val seasons = CyberRealityIntelligenceV13Engine.seasonCampaigns.value
    assertTrue("Cyber season campaigns must exist", seasons.isNotEmpty())
    val s1 = seasons.first { it.seasonNumber == 1 }
    assertTrue("Season 1 must be unlocked", s1.isSeasonUnlocked)
    assertTrue("Season must contain episodes", s1.episodes.isNotEmpty())
  }

  @Test
  fun testOrganizationalMemoryAndPersistentIncidentMemory() {
    val enterprise = CyberRealityIntelligenceV13Engine.enterpriseState.value
    assertEquals("AegoraBank Global Financial", enterprise.enterpriseName)
    assertTrue("Student decisions impact summary must be present", enterprise.studentDecisionsImpactSummary.isNotBlank())

    val initialMemories = CyberRealityIntelligenceV13Engine.incidentMemoryLogs.value
    assertTrue("Initial incident memory logs must not be empty", initialMemories.isNotEmpty())

    CyberRealityIntelligenceV13Engine.recordIncidentMemory(
      scenarioTitle = "Zero-Day Log4j Exploit Containment Drill",
      actionsExecuted = listOf("Applied WAF regex rule", "Disabled JNDI lookup via JVM flag"),
      successes = listOf("Blocked 1,200 remote code execution attempts"),
      mistakes = listOf("Briefly delayed LDAP egress egress filter"),
      takeaway = "JVM runtime flags provide immediate immutable defense during active 0-day waves."
    )

    val updatedMemories = CyberRealityIntelligenceV13Engine.incidentMemoryLogs.value
    assertEquals("Memory logs count should increment", initialMemories.size + 1, updatedMemories.size)
    assertEquals("Newest scenario title must match", "Zero-Day Log4j Exploit Containment Drill", updatedMemories.first().scenarioTitle)
  }

  @Test
  fun testPrinciplesMatrixAndToolAgnosticChallenges() {
    val principles = CyberRealityIntelligenceV13Engine.principleMatrix.value
    assertTrue("Principles matrix must exist", principles.isNotEmpty())
    val p = principles.first()
    assertTrue("Core invariant must be stated", p.coreInvariant.isNotBlank())
    assertTrue("Linux application must be specified", p.linuxApplication.isNotBlank())
    assertTrue("Cloud IAM application must be specified", p.cloudIamApplication.isNotBlank())

    val challenges = CyberRealityIntelligenceV13Engine.toolAgnosticChallenges.value
    assertTrue("Tool-agnostic transfer challenges must exist", challenges.isNotEmpty())
    val ch = challenges.first()
    assertTrue("Familiar and unknown tools must differ", ch.familiarToolName != ch.targetUnknownToolName)
    assertTrue("Evaluation rubric must not be blank", ch.evaluationRubric.isNotBlank())
  }

  @Test
  fun testRealWorldConstraintsAndEthicalDecisionLab() {
    val constraints = CyberRealityIntelligenceV13Engine.constraintScenarios.value
    assertTrue("Constraint scenarios must exist", constraints.isNotEmpty())
    val c = constraints.first()
    assertTrue("Budget constraint must be described", c.budgetConstraint.isNotBlank())
    assertTrue("Legacy tech hurdle must be specified", c.legacyTechHurdle.isNotBlank())

    val ethicalCases = CyberRealityIntelligenceV13Engine.ethicalCases.value
    assertTrue("Ethical cases must exist", ethicalCases.isNotEmpty())
    val eth = ethicalCases.first()
    assertTrue("No-dogma notice must be present", eth.noDogmaNotice.contains("balancing statutory obligations"))
    assertTrue("Option A and Option B must be distinct", eth.optionA != eth.optionB)
  }

  @Test
  fun testAiMentorStylesAndAiSecondOpinion() {
    CyberRealityIntelligenceV13Engine.setMentorStyle(AiMentorPersonaStyleV13.STRICT_EXAMINER)
    assertEquals("Mentor style should switch to Strict Examiner", AiMentorPersonaStyleV13.STRICT_EXAMINER, CyberRealityIntelligenceV13Engine.activeMentorStyle.value)

    val secondOpinions = CyberRealityIntelligenceV13Engine.secondOpinionCases.value
    assertTrue("Second opinion comparisons must exist", secondOpinions.isNotEmpty())
    val op = secondOpinions.first()
    assertTrue("AI Tutor view must not be blank", op.aiTutorConclusion.isNotBlank())
    assertTrue("AI Research Analyst view must not be blank", op.aiResearchAnalystConclusion.isNotBlank())
    assertTrue("Points of divergence must be analyzed", op.pointsOfDivergence.isNotEmpty())
  }

  @Test
  fun testTimeAndEnergyAwareNextBestAction30() {
    CyberRealityIntelligenceV13Engine.setLearnerEnergy(LearnerEnergyStateV13.LOW_ENERGY)
    CyberRealityIntelligenceV13Engine.setAvailableTimeMinutes(10)

    val lowAction = CyberRealityIntelligenceV13Engine.nextBestAction.value
    assertEquals(LearnerEnergyStateV13.LOW_ENERGY, lowAction.energyRequirement)
    assertTrue("Should recommend micro-recall/review for low energy", lowAction.actionTitle.contains("Review") || lowAction.actionTitle.contains("Flashcard"))

    CyberRealityIntelligenceV13Engine.setLearnerEnergy(LearnerEnergyStateV13.HIGH_FOCUS)
    CyberRealityIntelligenceV13Engine.setAvailableTimeMinutes(30)

    val highAction = CyberRealityIntelligenceV13Engine.nextBestAction.value
    assertEquals(LearnerEnergyStateV13.HIGH_FOCUS, highAction.energyRequirement)
    assertTrue("Should recommend deep scenario for high focus", highAction.actionTitle.contains("Attack Journey") || highAction.actionTitle.contains("Lab"))
  }

  @Test
  fun testProfessionalDebriefReportAndGrowthIndexAndReleaseCenter() {
    val debrief = CyberRealityIntelligenceV13Engine.simulationDebriefReport.value
    assertNotNull("Debrief report must not be null", debrief)
    assertTrue("Overall score must be positive", debrief.learnerScoreOverall > 0)
    assertTrue("Demonstrated strengths must be listed", debrief.demonstratedStrengths.isNotEmpty())
    assertTrue("Mistake analysis must be transparent", debrief.mistakeAnalysis.isNotBlank())

    val growth = CyberRealityIntelligenceV13Engine.growthIndex.value
    assertTrue("Capability index must be in 0..100", growth.capabilityIndex in 0..100)
    assertTrue("Evidence quality index must be in 0..100", growth.evidenceQualityIndex in 0..100)
    assertTrue("Transferability index must be in 0..100", growth.transferabilityIndex in 0..100)

    val release = CyberRealityIntelligenceV13Engine.releaseCenter.value
    assertEquals("13.0.0", release.version)
    assertEquals("CYBER REALITY INTELLIGENCE LAYER", release.releaseCodename)
    assertTrue("All 13 launch gates must be cleared", release.allGatesClear)
    assertTrue("Should contain all 13 gates", release.gates.size >= 13)
  }
}
