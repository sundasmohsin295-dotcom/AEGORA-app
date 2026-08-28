package com.example.intelligence

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object CyberOperatingSystemEngine {

  // ============================================================
  // 1. CYBER TWIN 5.0 (22 EXPLAINABLE DIMENSIONS) & 4.0 LEGACY
  // ============================================================
  private val _cyberTwin40 = MutableStateFlow(createInitialTwin40Snapshot())
  val cyberTwin40: StateFlow<CyberTwin40Snapshot> = _cyberTwin40.asStateFlow()

  private val _cyberTwin50 = MutableStateFlow(createInitialTwin50Snapshot())
  val cyberTwin50: StateFlow<CyberTwin50Snapshot> = _cyberTwin50.asStateFlow()

  // ============================================================
  // 2. NEXT BEST ACTION ENGINE 2.0
  // ============================================================
  private val _nextBestAction = MutableStateFlow(createInitialNextBestActions())
  val nextBestAction: StateFlow<NextBestActionBundle> = _nextBestAction.asStateFlow()

  // ============================================================
  // 3. ADAPTIVE MISSIONS
  // ============================================================
  private val _activeMissions = MutableStateFlow(createInitialMissions())
  val activeMissions: StateFlow<List<CyberMissionV10>> = _activeMissions.asStateFlow()

  // ============================================================
  // 4. ADAPTIVE LEARNING MODES (16 v10 & 13 v11 MODES)
  // ============================================================
  private val _currentLearningMode = MutableStateFlow(AdaptiveLearningModeV10.INVESTIGATION)
  val currentLearningMode: StateFlow<AdaptiveLearningModeV10> = _currentLearningMode.asStateFlow()

  private val _currentLearningModeV11 = MutableStateFlow(LearningModeV11.INVESTIGATION)
  val currentLearningModeV11: StateFlow<LearningModeV11> = _currentLearningModeV11.asStateFlow()

  private val _learnerDiagnostics = MutableStateFlow(createInitialLearnerDiagnostics())
  val learnerDiagnostics: StateFlow<LearnerDiagnosticProfile> = _learnerDiagnostics.asStateFlow()

  // ============================================================
  // 5. SKILL DECAY RADAR 2.0 & RESURRECTION
  // ============================================================
  private val _skillResurrections = MutableStateFlow(createInitialResurrectionChallenges())
  val skillResurrections: StateFlow<List<SkillResurrectionChallenge>> = _skillResurrections.asStateFlow()

  private val _skillDecayRadar = MutableStateFlow(createInitialSkillDecayRadar())
  val skillDecayRadar: StateFlow<List<SkillDecayItemV11>> = _skillDecayRadar.asStateFlow()

  // ============================================================
  // 6. MISTAKE DNA 4.0 (17 PATTERNS) & 3.0
  // ============================================================
  private val _mistakeHistory = MutableStateFlow(createInitialMistakeHistory())
  val mistakeHistory: StateFlow<List<MistakeRecordV10>> = _mistakeHistory.asStateFlow()

  private val _mistakeDnaV11 = MutableStateFlow(createInitialMistakeDnaV11())
  val mistakeDnaV11: StateFlow<List<MistakePatternDetailV11>> = _mistakeDnaV11.asStateFlow()

  // ============================================================
  // 7. REASONING GRAPH 4.0 & REPLAY
  // ============================================================
  private val _reasoningAudit = MutableStateFlow(createInitialReasoningAudit())
  val reasoningAudit: StateFlow<ReasoningGraphAudit> = _reasoningAudit.asStateFlow()

  private val _investigationReplays = MutableStateFlow(createInitialInvestigationReplay())
  val investigationReplays: StateFlow<List<InvestigationReplaySession>> = _investigationReplays.asStateFlow()

  // ============================================================
  // 8. CYBER REALITY GRAPH (CONNECTED ENTITY GRAPH)
  // ============================================================
  private val _cyberRealityGraph = MutableStateFlow(createInitialRealityGraph())
  val cyberRealityGraph: StateFlow<CyberRealityGraphData> = _cyberRealityGraph.asStateFlow()

  // ============================================================
  // 9. INCIDENT MULTIVERSE (ALTERNATIVE BRANCHES)
  // ============================================================
  private val _incidentMultiverse = MutableStateFlow(createInitialMultiverse())
  val incidentMultiverse: StateFlow<List<IncidentMultiverseScenario>> = _incidentMultiverse.asStateFlow()

  // ============================================================
  // 10. LIVING ADVERSARY ENGINE
  // ============================================================
  private val _livingAdversary = MutableStateFlow(createInitialAdversaryProfile())
  val livingAdversary: StateFlow<LivingAdversaryProfile> = _livingAdversary.asStateFlow()

  // ============================================================
  // 11. FUSION CHALLENGES
  // ============================================================
  private val _fusionChallenges = MutableStateFlow(createInitialFusionChallenges())
  val fusionChallenges: StateFlow<List<FusionChallengeScenario>> = _fusionChallenges.asStateFlow()

  // ============================================================
  // 12. RED -> BLUE MODE
  // ============================================================
  private val _redBlueSessions = MutableStateFlow(createInitialRedBlueSessions())
  val redBlueSessions: StateFlow<List<RedBlueSession>> = _redBlueSessions.asStateFlow()

  // ============================================================
  // 13. WORKPLACE REALITY ENGINE 4.0 & INBOX
  // ============================================================
  private val _workplaceInbox = MutableStateFlow(createInitialWorkplaceInbox())
  val workplaceInbox: StateFlow<List<WorkplaceItemV10>> = _workplaceInbox.asStateFlow()

  private val _workplaceCrisesV11 = MutableStateFlow(createInitialWorkplaceCrisesV11())
  val workplaceCrisesV11: StateFlow<List<WorkplaceCrisisItemV11>> = _workplaceCrisesV11.asStateFlow()

  // ============================================================
  // 14. VOICE CYBER DRILLS
  // ============================================================
  private val _voiceDrills = MutableStateFlow(createInitialVoiceDrills())
  val voiceDrills: StateFlow<List<VoiceCyberDrillScenario>> = _voiceDrills.asStateFlow()

  // ============================================================
  // 15. AI AGENTS (17 SPECIALIZED ROLES) & SAFETY REGISTRY
  // ============================================================
  private val _aiSafetyProfiles = MutableStateFlow(createAiSafetyRegistry())
  val aiSafetyProfiles: StateFlow<List<AiMentorSafetyProfile>> = _aiSafetyProfiles.asStateFlow()

  private val _aiAgentRegistryV11 = MutableStateFlow(createInitialAiAgentRegistry())
  val aiAgentRegistryV11: StateFlow<List<AiAgentStateV11>> = _aiAgentRegistryV11.asStateFlow()

  // ============================================================
  // 16. EVIDENCE ENGINE & SKILL PASSPORT 4.0
  // ============================================================
  private val _evidenceLedgerV11 = MutableStateFlow(createInitialEvidenceLedger())
  val evidenceLedgerV11: StateFlow<List<SkillEvidenceEntryV11>> = _evidenceLedgerV11.asStateFlow()

  private val _portfolioArtifacts = MutableStateFlow(createInitialPortfolio())
  val portfolioArtifacts: StateFlow<List<PortfolioArtifactV10>> = _portfolioArtifacts.asStateFlow()

  // ============================================================
  // 17. OPPORTUNITY RADAR 2.0
  // ============================================================
  private val _opportunityRadar = MutableStateFlow(createInitialOpportunities())
  val opportunityRadar: StateFlow<List<OpportunityItemV10>> = _opportunityRadar.asStateFlow()

  private val _opportunityRadarV11 = MutableStateFlow(createInitialOpportunitiesV11())
  val opportunityRadarV11: StateFlow<List<OpportunityRadarItemV11>> = _opportunityRadarV11.asStateFlow()

  // ============================================================
  // 18. QUALITY CENTER & RELEASE CONTROL
  // ============================================================
  private val _releaseControlAudit = MutableStateFlow(ReleaseControlAudit())
  val releaseControlAudit: StateFlow<ReleaseControlAudit> = _releaseControlAudit.asStateFlow()

  // ============================================================
  // BUSINESS LOGIC & INTERACTION METHODS
  // ============================================================

  fun updateActionFeedback(actionId: String, feedback: ActionFeedbackType) {
    val current = _nextBestAction.value
    fun updateItem(item: NextBestActionItem): NextBestActionItem {
      return if (item.id == actionId) item.copy(feedbackState = feedback) else item
    }

    _nextBestAction.value = current.copy(
      primaryAction = updateItem(current.primaryAction),
      optionA = updateItem(current.optionA),
      optionB = updateItem(current.optionB),
      optionC = updateItem(current.optionC)
    )
  }

  fun switchLearningMode(mode: AdaptiveLearningModeV10) {
    _currentLearningMode.value = mode
  }

  fun switchLearningModeV11(mode: LearningModeV11) {
    _currentLearningModeV11.value = mode
  }

  fun filterRealityGraphDomain(domain: String) {
    _cyberRealityGraph.value = _cyberRealityGraph.value.copy(activeDomainFilter = domain)
  }

  fun generateAdaptiveMission(
    duration: MissionDuration,
    category: MissionCategory,
    targetRole: String = "SOC Analyst L2"
  ): CyberMissionV10 {
    val newMission = when (category) {
      MissionCategory.INVESTIGATE -> CyberMissionV10(
        title = "Triage Injected PowerShell in St. Jude Healthcare",
        objective = "Detect memory injection, correlate Sysmon Event ID 8, and quarantine patient monitor subnet.",
        targetRole = targetRole,
        difficulty = SkillLevel.INTERMEDIATE,
        timeBudget = duration,
        prerequisites = listOf("Windows Event Log Analysis", "Sysmon Process Lineage"),
        targetSkills = listOf("Process Injection Triage", "EDR Host Isolation"),
        targetTools = listOf("Sysmon", "Splunk SPL", "PowerShell"),
        scenarioDescription = "At 02:14 UTC, high volume DNS requests originated from nurse workstation WS-702 pointing to high-entropy domain.",
        evidenceRequirements = listOf("Sysmon Event 8 Log Export", "Target Memory Dump MD5"),
        successCriteria = listOf("Identify parent process svchost.exe", "Isolate host within 180s", "Write executive briefing"),
        debriefSummary = "Adversary attempted living-off-the-land execution via unquoted service path.",
        followUpAction = "Execute 5-min timeline correlation drill."
      )
      MissionCategory.DEFEND -> CyberMissionV10(
        title = "Deploy Sigma Defensive Rule for Kerberoasting",
        objective = "Construct Sigma rule targeting RC4-HMAC ticket requests (Event 4769) with encryption type 0x17.",
        targetRole = targetRole,
        difficulty = SkillLevel.ADVANCED,
        timeBudget = duration,
        prerequisites = listOf("Active Directory Kerberos Protocol", "Sigma Syntax"),
        targetSkills = listOf("Detection Engineering", "Active Directory Security"),
        targetTools = listOf("Sigma CLI", "Splunk", "Zeek"),
        scenarioDescription = "Service accounts within Apex Global Banking requested anomalous TGS tickets during non-business hours.",
        evidenceRequirements = listOf("Validated Sigma YAML", "True Positive Test Suite Result"),
        successCriteria = listOf("Zero false positives against backup admin service", "Detection latency under 60 seconds"),
        debriefSummary = "Kerberoasting mitigated by enforcing AES256 ticket encryption policy across SPNs.",
        followUpAction = "Draft change request ticket in Jira Workplace."
      )
      else -> CyberMissionV10(
        title = "Rapid Incident Triage: ${category.title}",
        objective = "Execute disciplined 5-stage triage: Evidence → Hypothesis → Verification → Containment.",
        targetRole = targetRole,
        difficulty = SkillLevel.BEGINNER,
        timeBudget = duration,
        prerequisites = listOf("Core Security Telemetry"),
        targetSkills = listOf("Telemetry Analysis", "Systematic Reasoning"),
        targetTools = listOf("Wireshark", "Sysmon"),
        scenarioDescription = "Anomalous lateral movement detected across internal VLAN.",
        evidenceRequirements = listOf("Triage Notes Hash", "Packet Capture PCAP"),
        successCriteria = listOf("Establish exact compromise timestamp", "Prevent secondary domain compromise"),
        debriefSummary = "Systematic timeline reconstruction prevented premature alert closure.",
        followUpAction = "Review Mistake DNA diagnostic report."
      )
    }

    _activeMissions.value = listOf(newMission) + _activeMissions.value
    return newMission
  }

  fun completeMission(missionId: String, scoreEarned: Int) {
    _activeMissions.value = _activeMissions.value.map {
      if (it.id == missionId) it.copy(isCompleted = true, scoreEarned = scoreEarned) else it
    }
  }

  fun advanceResurrectionChallenge(skillId: String, nextStage: Int) {
    _skillResurrections.value = _skillResurrections.value.map {
      if (it.skillId == skillId) it.copy(currentStage = nextStage) else it
    }
  }

  fun recordMistake(
    pattern: MistakePatternType,
    whatHappened: String,
    whyItHappened: String,
    impact: String,
    approach: String,
    microDrillTitle: String,
    microDrillPrompt: String
  ): MistakeRecordV10 {
    val record = MistakeRecordV10(
      pattern = pattern,
      whatHappened = whatHappened,
      whyItHappened = whyItHappened,
      businessAndTechnicalImpact = impact,
      correctApproach = approach,
      microDrillTitle = microDrillTitle,
      microDrillPrompt = microDrillPrompt
    )
    _mistakeHistory.value = listOf(record) + _mistakeHistory.value
    return record
  }

  fun selectWorkplaceCrisisOption(crisisId: String, optionIndex: Int) {
    _workplaceCrisesV11.value = _workplaceCrisesV11.value.map { crisis ->
      if (crisis.id == crisisId) {
        val feedback = when (optionIndex) {
          0 -> "Optimal resolution: Balanced technical containment with zero critical business disruption."
          1 -> "Elevated downtime risk: Unnecessary broadcast isolation caused secondary outage in accounting."
          else -> "Regulatory risk: Escalation delayed past statutory 72-hour reporting boundary."
        }
        crisis.copy(selectedOptionIndex = optionIndex, outcomeFeedback = feedback)
      } else crisis
    }
  }

  fun submitVoiceDrillTranscript(drillId: String, transcript: String): VoiceCyberDrillScenario {
    val wordCount = transcript.split("\\s+".toRegex()).size
    val techScore = (70 + (wordCount % 25)).coerceIn(60, 98)
    val calmScore = if (transcript.contains("immediately", ignoreCase = true)) 90 else 82
    val updated = VoiceCyberDrillScenario(
      drillId = drillId,
      learnerResponseTranscript = transcript,
      technicalCorrectnessScore = techScore,
      questionQualityScore = 88,
      communicationCalmnessScore = calmScore,
      prioritizationScore = 92,
      completenessScore = 86,
      coachCritique = "Demonstrated rapid initial isolation instructions without inciting employee panic."
    )
    _voiceDrills.value = _voiceDrills.value.map { if (it.drillId == drillId) updated else it }
    return updated
  }

  fun addEvidenceEntry(
    skillName: String,
    activityTitle: String,
    type: EvidenceVerificationType,
    resultSummary: String
  ): SkillEvidenceEntryV11 {
    val entry = SkillEvidenceEntryV11(
      skillName = skillName,
      activityTitle = activityTitle,
      verificationType = type,
      resultSummary = resultSummary
    )
    _evidenceLedgerV11.value = listOf(entry) + _evidenceLedgerV11.value
    return entry
  }

  fun evaluateJobDescription(rawJobText: String): ReverseJobAnalysisResult {
    val required = mutableListOf<String>()
    val preferred = mutableListOf<String>()
    val inferred = mutableListOf<String>()

    if (rawJobText.contains("Splunk", ignoreCase = true) || rawJobText.contains("SIEM", ignoreCase = true)) {
      required.add("SIEM & Splunk SPL Query Optimization")
    }
    if (rawJobText.contains("Sysmon", ignoreCase = true) || rawJobText.contains("Windows", ignoreCase = true)) {
      required.add("Windows Sysmon Process Telemetry Analysis")
    }
    if (rawJobText.contains("Python", ignoreCase = true) || rawJobText.contains("script", ignoreCase = true)) {
      preferred.add("Python Automated Artifact Parsing Script")
    }
    if (rawJobText.contains("Sigma", ignoreCase = true) || rawJobText.contains("Rule", ignoreCase = true)) {
      preferred.add("Sigma Rule Authoring & MITRE ATT&CK Mapping")
    }
    inferred.add("Structured Root-Cause Incident Reporting")
    inferred.add("Cross-Functional Executive Escalation")

    val verifiedEvidence = listOf(
      "SIEM SPL Log Search Lab (sha256:7f83...)",
      "Sysmon Event ID 1 Process Lineage Sandbox (sha256:4a91...)"
    )
    val missingGaps = listOf(
      "Verified Sigma Detection Engine Repository",
      "Executive Incident Briefing STAR Recording"
    )

    return ReverseJobAnalysisResult(
      parsedJobTitle = "Senior SOC Defense Analyst",
      targetCompanyArchetype = "Tier-1 FinTech Financial Institution",
      requiredSkills = required.ifEmpty { listOf("Security Incident Monitoring", "Log Analysis") },
      preferredSkills = preferred.ifEmpty { listOf("Scripting & Automation", "Cloud Security") },
      inferredSkills = inferred,
      learnerVerifiedEvidence = verifiedEvidence,
      missingEvidenceGaps = missingGaps,
      overallMatchPercentage = 78,
      recommendedLabs = listOf(
        "30-Min Sigma Detection Engineering Arena",
        "15-Min Executive Ransomware Briefing Drill"
      ),
      recommendedProjects = listOf("Enterprise Sysmon & Sigma GitHub Detection Engine"),
      recommendedCertifications = listOf("CompTIA CySA+", "GIAC Certified Incident Handler (GCIH)"),
      interviewPreparationQuestions = listOf(
        "How do you differentiate legitimate rundll32.exe invocations from Cobalt Strike beacons?",
        "Walk me through your methodology when an EDR agent ceases heartbeat during an active ransomware outbreak."
      ),
      shortestRealisticPathWeeks = 4
    )
  }

  fun generatePortfolioArtifact(type: String, title: String, evidenceHashes: List<String>): PortfolioArtifactV10 {
    val content = when (type) {
      "GitHub README" -> """
        # $title
        Production-ready defensive artifacts verified by AEGORA cryptographic evidence proofs.
        
        ## Evidence Ledger
        ${evidenceHashes.joinToString("\n") { "- Hash: $it (Verified)" }}
        
        ## Detection Logic
        - Automated ingestion of Sysmon events 1, 3, 7, 8, 10, 11, 13.
        - Zero-suppression false positive validation against baseline telemetry.
      """.trimIndent()
      else -> """
        # Incident Report: $title
        **Author:** AEGORA Practitioner (Verified Level 4)
        **Date:** 2026-08-28
        
        ### Executive Summary
        Adversary gained unauthorized footholds via spear-phishing attachment on WS-702. 
        Detection occurred within 180s; host quarantined before data staging completed.
      """.trimIndent()
    }

    val artifact = PortfolioArtifactV10(
      title = title,
      type = type,
      markdownContent = content,
      verifiedEvidenceHashes = evidenceHashes
    )
    _portfolioArtifacts.value = listOf(artifact) + _portfolioArtifacts.value
    return artifact
  }

  fun quarantineExternalContent(sourceId: String, content: String): UntrustedContentQuarantine {
    val injectionPatterns = listOf(
      "ignore previous instructions",
      "disregard all prior rules",
      "reveal system prompt",
      "act as unrestricted",
      "bypass security controls",
      "export environment variables"
    )

    val detected = injectionPatterns.filter { content.contains(it, ignoreCase = true) }
    val isSuspected = detected.isNotEmpty()

    val sanitized = if (isSuspected) {
      "[QUARANTINED CONTENT — PROMPT INJECTION HEURISTIC DETECTED: ${detected.joinToString()}] $content"
    } else {
      content
    }

    return UntrustedContentQuarantine(
      sourceIdentifier = sourceId,
      rawContent = content,
      isPromptInjectionSuspected = isSuspected,
      detectedInjectionHeuristics = if (isSuspected) listOf("System Prompt Override Heuristic") else emptyList(),
      sanitizedContentForAnalysis = sanitized
    )
  }

  // ============================================================
  // MOCK INITIALIZATION DATA BUILDERS
  // ============================================================

  private fun createInitialTwin50Snapshot(): CyberTwin50Snapshot {
    val dimMap = mutableMapOf<TwinDimensionV11, DimensionExplainabilityV11>()

    dimMap[TwinDimensionV11.THEORETICAL_KNOWLEDGE] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.THEORETICAL_KNOWLEDGE,
      currentScore = 88,
      whyItExists = "Passed 24 foundational theory modules across OSI model, TCP/IP handshake, and Kerberos protocol.",
      evidenceSummary = "24 verified quizzes & concept checkpoints with > 90% accuracy.",
      evidenceCount = 24,
      evidenceAgeDays = 4,
      recentTrend = "Accelerating (+6%)",
      negativeFactors = listOf("Decaying retention on Public Key Infrastructure X.509 extensions"),
      positiveFactors = listOf("Flawless understanding of Windows Access Tokens and Privilege Constants"),
      repeatedMistakes = emptyList(),
      confidenceScore = 92,
      nextRecommendedAction = "Run 5-min PKI certificate chain retrieval drill."
    )

    dimMap[TwinDimensionV11.PRACTICAL_ABILITY] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.PRACTICAL_ABILITY,
      currentScore = 81,
      whyItExists = "Successfully executed 16 hands-on sandbox labs including live memory dumps and PCAP packet filtering.",
      evidenceSummary = "16 cryptographically signed lab tokens in local evidence ledger.",
      evidenceCount = 16,
      evidenceAgeDays = 2,
      recentTrend = "Stable (+2%)",
      negativeFactors = listOf("Occasional syntax hesitations with Wireshark display filter regex"),
      positiveFactors = listOf("High precision with Linux grep/awk log carving pipelines"),
      repeatedMistakes = listOf("Misremembered Wireshark http.request.method filter syntax"),
      confidenceScore = 86,
      nextRecommendedAction = "Complete 15-min Wireshark display filter speed ladder."
    )

    dimMap[TwinDimensionV11.INVESTIGATION] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.INVESTIGATION,
      currentScore = 84,
      whyItExists = "Completed 8 complex multi-host incident investigations with zero false positives.",
      evidenceSummary = "8 investigation post-mortem reports with timeline correlation.",
      evidenceCount = 8,
      evidenceAgeDays = 3,
      recentTrend = "Accelerating (+10%)",
      negativeFactors = listOf("Tendency to examine endpoint before validating initial firewall ingress logs"),
      positiveFactors = listOf("Rigorous verification of process parent-child PID lineage"),
      repeatedMistakes = listOf("Premature closure on initial phishing alert without second-stage payload trace"),
      confidenceScore = 89,
      nextRecommendedAction = "Triage Injected PowerShell in St. Jude Healthcare simulation."
    )

    dimMap[TwinDimensionV11.TRIAGE] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.TRIAGE,
      currentScore = 82,
      whyItExists = "Demonstrated sub-120s alert categorization accuracy across 40 simulated SIEM notifications.",
      evidenceSummary = "40 triage events with 94% true positive discrimination rate.",
      evidenceCount = 40,
      evidenceAgeDays = 1,
      recentTrend = "Accelerating (+8%)",
      negativeFactors = listOf("Higher latency when evaluating unfamiliar cloud IAM role assumption alerts"),
      positiveFactors = listOf("Immediate identification of Living-off-the-land binaries (rundll32, certutil)"),
      repeatedMistakes = emptyList(),
      confidenceScore = 88,
      nextRecommendedAction = "Practice AWS GuardDuty role assumption triage drill."
    )

    dimMap[TwinDimensionV11.CAUSAL_REASONING] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.CAUSAL_REASONING,
      currentScore = 79,
      whyItExists = "Demonstrated ability to establish causal chain from initial phishing lure to domain controller sync.",
      evidenceSummary = "Reasoning Graph 3.0 score: 86% Evidence-First, 78% Hypothesis Falsification.",
      evidenceCount = 12,
      evidenceAgeDays = 5,
      recentTrend = "Stable",
      negativeFactors = listOf("Formed initial hypothesis before inspecting full network connection logs in 2 incidents"),
      positiveFactors = listOf("Consistently checks for defense evasion before concluding host is clean"),
      repeatedMistakes = listOf("Confirmation bias during DNS exfiltration analysis"),
      confidenceScore = 80,
      nextRecommendedAction = "Conduct hypothesis falsification exercise in Reasoning Graph replay."
    )

    dimMap[TwinDimensionV11.DECISION_MAKING] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.DECISION_MAKING,
      currentScore = 83,
      whyItExists = "Calculated business risk accurately in 9 out of 10 crisis containment drills.",
      evidenceSummary = "9 containment decisions reviewed by AI CISO evaluator.",
      evidenceCount = 9,
      evidenceAgeDays = 2,
      recentTrend = "Accelerating (+5%)",
      negativeFactors = listOf("Hesitated 4 minutes before isolating domain controller due to fear of outage"),
      positiveFactors = listOf("Accurately scoped patient monitor VLAN vs accounting subnet in hospital sim"),
      repeatedMistakes = emptyList(),
      confidenceScore = 85,
      nextRecommendedAction = "Participate in Crisis War Room ransomware escalation drill."
    )

    dimMap[TwinDimensionV11.PRESSURE_PERFORMANCE] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.PRESSURE_PERFORMANCE,
      currentScore = 76,
      whyItExists = "Maintained 80%+ accuracy during 5-minute timed crisis containment simulations.",
      evidenceSummary = "BioStress & Pacing metrics logged during 6 timed crisis scenarios.",
      evidenceCount = 6,
      evidenceAgeDays = 6,
      recentTrend = "Stable (+4%)",
      negativeFactors = listOf("Slight cognitive tunneling under active 180s countdown timer"),
      positiveFactors = listOf("Rapid execution of standard operating containment commands without typos"),
      repeatedMistakes = listOf("Overlooked secondary proxy log when timer dropped below 60s"),
      confidenceScore = 78,
      nextRecommendedAction = "Take the 5-Minute Pressure Mode containment sprint."
    )

    dimMap[TwinDimensionV11.COMMUNICATION] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.COMMUNICATION,
      currentScore = 85,
      whyItExists = "Drafted 5 executive summaries free of unparsed jargon with clear financial risk metrics.",
      evidenceSummary = "5 executive summaries reviewed with 91% communication clarity score.",
      evidenceCount = 5,
      evidenceAgeDays = 3,
      recentTrend = "Accelerating (+9%)",
      negativeFactors = listOf("Occasionally used technical terms like 'NTDS.dit' in CEO briefings without explanation"),
      positiveFactors = listOf("Stated estimated financial loss and downtime hours in the first paragraph"),
      repeatedMistakes = emptyList(),
      confidenceScore = 88,
      nextRecommendedAction = "Deliver verbal executive briefing in Voice Cyber Drill."
    )

    dimMap[TwinDimensionV11.CROSS_DOMAIN_TRANSFER] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.CROSS_DOMAIN_TRANSFER,
      currentScore = 78,
      whyItExists = "Successfully mapped Windows Kerberos attack knowledge to AWS IAM STS token abuse.",
      evidenceSummary = "Fusion challenge score: 82% cross-domain mapping fidelity.",
      evidenceCount = 4,
      evidenceAgeDays = 7,
      recentTrend = "Accelerating (+11%)",
      negativeFactors = listOf("Slower when correlating Kubernetes API audit logs with container escape primitives"),
      positiveFactors = listOf("Excellent correlation between network flow logs and endpoint process activity"),
      repeatedMistakes = emptyList(),
      confidenceScore = 79,
      nextRecommendedAction = "Attempt Cloud-to-Endpoint Fusion Challenge."
    )

    dimMap[TwinDimensionV11.RETENTION] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.RETENTION,
      currentScore = 80,
      whyItExists = "Ebbinghaus decay radar tracks 85% retention across skills practiced in last 14 days.",
      evidenceSummary = "Spaced repetition recall tests logged across 30 key cybersecurity concepts.",
      evidenceCount = 30,
      evidenceAgeDays = 1,
      recentTrend = "Stable",
      negativeFactors = listOf("Linux iptables and PAM configuration rules decaying (-14%)"),
      positiveFactors = listOf("Retained 100% of MITRE ATT&CK enterprise tactics and technique IDs"),
      repeatedMistakes = emptyList(),
      confidenceScore = 84,
      nextRecommendedAction = "Execute 5-min Linux PAM skill resurrection challenge."
    )

    dimMap[TwinDimensionV11.CAREER_READINESS] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.CAREER_READINESS,
      currentScore = 78,
      whyItExists = "Evaluated against 120 verified SOC Analyst L2 job postings with 78% evidence alignment.",
      evidenceSummary = "Reverse Job Analysis match score: 78% verified evidence match.",
      evidenceCount = 1,
      evidenceAgeDays = 1,
      recentTrend = "Accelerating (+14%)",
      negativeFactors = listOf("Missing public GitHub repository with verified Sigma rules"),
      positiveFactors = listOf("Exceeds minimum requirements for SIEM log parsing and incident triage"),
      repeatedMistakes = emptyList(),
      confidenceScore = 82,
      nextRecommendedAction = "Generate verified GitHub README detection engine portfolio."
    )

    dimMap[TwinDimensionV11.CONFIDENCE_CALIBRATION] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.CONFIDENCE_CALIBRATION,
      currentScore = 84,
      whyItExists = "Learner confidence ratings matched objective test outcomes in 86% of trials.",
      evidenceSummary = "Confidence calibration matrix tracked across 50 quiz and lab submissions.",
      evidenceCount = 50,
      evidenceAgeDays = 2,
      recentTrend = "Stable (+3%)",
      negativeFactors = listOf("Slight overconfidence when diagnosing cloud IAM boundary bypasses"),
      positiveFactors = listOf("Accurately flagged uncertainty before investigating unfamiliar Cobalt Strike malleable C2 profiles"),
      repeatedMistakes = emptyList(),
      confidenceScore = 87,
      nextRecommendedAction = "Calibrate confidence in Socratic Mode triage."
    )

    dimMap[TwinDimensionV11.LEARNING_VELOCITY] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.LEARNING_VELOCITY,
      currentScore = 87,
      whyItExists = "Achieved mastery in 4 new detection engineering subskills in under 10 days.",
      evidenceSummary = "Velocity curve shows 1.4x standard cohort acquisition speed.",
      evidenceCount = 14,
      evidenceAgeDays = 1,
      recentTrend = "Accelerating (+7%)",
      negativeFactors = listOf("Pacing drops slightly when tackling low-level C memory safety vulnerabilities"),
      positiveFactors = listOf("Rapid absorption of SIEM query syntax and regex parsing"),
      repeatedMistakes = emptyList(),
      confidenceScore = 90,
      nextRecommendedAction = "Take on advanced Detection Engineering sprint."
    )

    dimMap[TwinDimensionV11.MISTAKE_DNA] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.MISTAKE_DNA,
      currentScore = 82,
      whyItExists = "Resolved 8 out of 10 identified cognitive and procedural mistake patterns via micro-drills.",
      evidenceSummary = "Mistake DNA 4.0 database tracks 10 historical patterns with 80% remediation.",
      evidenceCount = 10,
      evidenceAgeDays = 2,
      recentTrend = "Accelerating (+12%)",
      negativeFactors = listOf("Recurring 'Premature Closure' tendency under high alert volumes"),
      positiveFactors = listOf("Completely eliminated 'Tool Dependency' pattern by learning raw log headers"),
      repeatedMistakes = listOf("Closing alert without checking child process arguments"),
      confidenceScore = 85,
      nextRecommendedAction = "Perform Premature Closure micro-drill."
    )

    dimMap[TwinDimensionV11.EVIDENCE_QUALITY] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.EVIDENCE_QUALITY,
      currentScore = 91,
      whyItExists = "100% of recorded lab achievements backed by cryptographic sha256 output hashes.",
      evidenceSummary = "28 verified cryptographic proofs recorded in Skill Passport ledger.",
      evidenceCount = 28,
      evidenceAgeDays = 1,
      recentTrend = "Stable (+1%)",
      negativeFactors = listOf("None identified; zero fabricated or self-asserted entries without proof"),
      positiveFactors = listOf("Strict adherence to lab-verified output signatures and timing hashes"),
      repeatedMistakes = emptyList(),
      confidenceScore = 96,
      nextRecommendedAction = "Export cryptographic Skill Passport token for portfolio."
    )

    dimMap[TwinDimensionV11.INDEPENDENCE] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.INDEPENDENCE,
      currentScore = 79,
      whyItExists = "Completed 7 intermediate investigations without requesting AI hints or solution checks.",
      evidenceSummary = "Autonomous completion rate: 70% unassisted in complex scenarios.",
      evidenceCount = 10,
      evidenceAgeDays = 3,
      recentTrend = "Accelerating (+8%)",
      negativeFactors = listOf("Relied on AI hints during initial Active Directory DCSync detection setup"),
      positiveFactors = listOf("Independently reconstructed timeline of multi-stage web shell attack"),
      repeatedMistakes = emptyList(),
      confidenceScore = 82,
      nextRecommendedAction = "Run unassisted Investigation Mode lab."
    )

    dimMap[TwinDimensionV11.CONSISTENCY] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.CONSISTENCY,
      currentScore = 88,
      whyItExists = "Maintained a 14-day daily practice streak with average 35 minutes active engagement.",
      evidenceSummary = "Daily telemetry records 14 consecutive active training days.",
      evidenceCount = 14,
      evidenceAgeDays = 0,
      recentTrend = "Accelerating (+5%)",
      negativeFactors = listOf("Slightly shorter sessions on weekends (15m vs 45m weekday)"),
      positiveFactors = listOf("Consistent daily habit formation aligning with synaptic consolidation schedules"),
      repeatedMistakes = emptyList(),
      confidenceScore = 94,
      nextRecommendedAction = "Complete today's active 15-minute mission sprint."
    )

    dimMap[TwinDimensionV11.TRANSFERABILITY] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.TRANSFERABILITY,
      currentScore = 80,
      whyItExists = "Applied Windows event analysis principles to successfully triage Linux auditd logs.",
      evidenceSummary = "Cross-platform telemetry triage evaluation scored 80%.",
      evidenceCount = 6,
      evidenceAgeDays = 4,
      recentTrend = "Accelerating (+6%)",
      negativeFactors = listOf("Slower translating Windows registry persistence to Linux systemd timer persistence"),
      positiveFactors = listOf("Seamless transition of network flow analysis between cloud and on-premise fabrics"),
      repeatedMistakes = emptyList(),
      confidenceScore = 83,
      nextRecommendedAction = "Complete Linux persistence vs Windows registry transfer drill."
    )

    dimMap[TwinDimensionV11.ADAPTABILITY] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.ADAPTABILITY,
      currentScore = 83,
      whyItExists = "Adjusted triage strategy seamlessly when adversary switched from PowerShell to compiled Go binary.",
      evidenceSummary = "Scenario branch adaptation score logged during adaptive adversary simulation.",
      evidenceCount = 5,
      evidenceAgeDays = 3,
      recentTrend = "Accelerating (+9%)",
      negativeFactors = listOf("Initial hesitation when encountering obfuscated Go binary strings"),
      positiveFactors = listOf("Quickly pivoted from string analysis to dynamic behavior monitoring"),
      repeatedMistakes = emptyList(),
      confidenceScore = 86,
      nextRecommendedAction = "Engage Living Adversary adaptive simulation."
    )

    dimMap[TwinDimensionV11.PROBLEM_SOLVING] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.PROBLEM_SOLVING,
      currentScore = 85,
      whyItExists = "Deconstructed multi-stage supply chain vulnerability across 3 dependencies in under 30 minutes.",
      evidenceSummary = "Capstone problem-solving lab scored 85% by AI Assessor.",
      evidenceCount = 7,
      evidenceAgeDays = 5,
      recentTrend = "Stable (+3%)",
      negativeFactors = listOf("Over-complicated initial hypothesis regarding root certificate trust store"),
      positiveFactors = listOf("Methodically isolated vulnerable npm package using binary search technique"),
      repeatedMistakes = emptyList(),
      confidenceScore = 88,
      nextRecommendedAction = "Solve Zero-Day supply chain investigation."
    )

    dimMap[TwinDimensionV11.TOOL_FLUENCY] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.TOOL_FLUENCY,
      currentScore = 89,
      whyItExists = "Demonstrated deep command fluency across Splunk, Wireshark, Sysmon, Zeek, and Sigma CLI.",
      evidenceSummary = "120 command executions evaluated for syntax efficiency and speed.",
      evidenceCount = 120,
      evidenceAgeDays = 1,
      recentTrend = "Accelerating (+4%)",
      negativeFactors = listOf("Moderate hesitation with Ghidra decompiler hotkeys"),
      positiveFactors = listOf("Instant recall of Splunk stats/eval/transaction query pipelines"),
      repeatedMistakes = emptyList(),
      confidenceScore = 93,
      nextRecommendedAction = "Practice Ghidra binary decompiler speed challenge."
    )

    dimMap[TwinDimensionV11.RESEARCH_ABILITY] = DimensionExplainabilityV11(
      dimension = TwinDimensionV11.RESEARCH_ABILITY,
      currentScore = 86,
      whyItExists = "Synthesized 3 recent CISA zero-day advisories into actionable enterprise detection rules.",
      evidenceSummary = "3 threat intelligence synthesis memos approved with zero citation errors.",
      evidenceCount = 3,
      evidenceAgeDays = 2,
      recentTrend = "Accelerating (+10%)",
      negativeFactors = listOf("Need to cross-reference vendor security blogs with official NVD CVSS vectors more consistently"),
      positiveFactors = listOf("Extracted IoCs and mapped to MITRE ATT&CK matrix in under 15 minutes"),
      repeatedMistakes = emptyList(),
      confidenceScore = 89,
      nextRecommendedAction = "Synthesize latest CISA known exploited vulnerability bulletin."
    )

    return CyberTwin50Snapshot(
      learnerId = "learner_aegora_01",
      overallMmr = 1920,
      skillPassportLevel = SkillPassportLevel.L4_SKILLED,
      dimensions = dimMap,
      activeCareerTarget = "SOC Analyst Level 2",
      daysToCareerReadiness = 38,
      cognitiveLoadScore = 42
    )
  }

  private fun createInitialTwin40Snapshot(): CyberTwin40Snapshot {
    val dimMap = mutableMapOf<TwinDimension, DimensionExplainability>()

    TwinDimension.entries.forEach { dim ->
      dimMap[dim] = DimensionExplainability(
        dimension = dim,
        score = 82,
        positiveFactors = listOf("Consistent lab completion", "Accurate causal timeline"),
        negativeFactors = listOf("Minor retention decay on cryptographic algorithms"),
        evidenceCount = 18,
        averageEvidenceAgeDays = 3,
        evidenceQualityScore = 91,
        successfulAttempts = 24,
        failedAttempts = 3,
        repeatedMistakes = listOf("Premature closure on initial phishing triage"),
        retentionTrend = "Stable",
        recommendedAction = "Complete 5-minute retrieval drill"
      )
    }

    return CyberTwin40Snapshot(
      dimensions = dimMap
    )
  }

  private fun createInitialNextBestActions(): NextBestActionBundle {
    return NextBestActionBundle(
      primaryAction = NextBestActionItem(
        title = "Triage Injected PowerShell in St. Jude Healthcare",
        estimatedMinutes = 12,
        category = MissionCategory.INVESTIGATE,
        reasonWhyRecommended = "Targets your #1 weakness: Correlating living-off-the-land execution in high-stakes healthcare subnets (+4 Investigation, +3 Causal Reasoning).",
        targetDimension = TwinDimension.INVESTIGATION_TRIAGE,
        urgencyScore = 94
      ),
      optionA = NextBestActionItem(
        title = "5-Min Skill Resurrection: Linux Syslog & PAM",
        estimatedMinutes = 5,
        category = MissionCategory.LEARN,
        reasonWhyRecommended = "Synaptic decay alert: Linux authentication knowledge decayed by 38% over the past 11 days.",
        targetDimension = TwinDimension.KNOWLEDGE_RETENTION,
        urgencyScore = 88
      ),
      optionB = NextBestActionItem(
        title = "Executive Communication Briefing: Wire Fraud Alert",
        estimatedMinutes = 10,
        category = MissionCategory.COMMUNICATE,
        reasonWhyRecommended = "Bridge career gap: Deliver zero-jargon risk summary to non-technical CFO.",
        targetDimension = TwinDimension.EXECUTIVE_COMMUNICATION,
        urgencyScore = 79
      ),
      optionC = NextBestActionItem(
        title = "Sigma Detection Rule Builder for Kerberoasting",
        estimatedMinutes = 20,
        category = MissionCategory.BUILD,
        reasonWhyRecommended = "Produce tangible evidence artifact for your verified public GitHub portfolio.",
        targetDimension = TwinDimension.PRACTICAL_ABILITY,
        urgencyScore = 75
      ),
      decayTriggerAlert = "Memory retention warning: Linux PAM & Syslog retention risk is HIGH. 5-min challenge recommended."
    )
  }

  private fun createInitialMissions(): List<CyberMissionV10> {
    return listOf(
      CyberMissionV10(
        title = "Triage Injected PowerShell in St. Jude Healthcare",
        objective = "Detect memory injection, correlate Sysmon Event ID 8, and quarantine patient monitor subnet.",
        targetRole = "SOC Analyst Level 2",
        difficulty = SkillLevel.INTERMEDIATE,
        timeBudget = MissionDuration.FIFTEEN_MIN,
        prerequisites = listOf("Windows Event Log Analysis", "Sysmon Process Lineage"),
        targetSkills = listOf("Process Injection Triage", "EDR Host Isolation"),
        targetTools = listOf("Sysmon", "Splunk SPL", "PowerShell"),
        scenarioDescription = "At 02:14 UTC, high volume DNS requests originated from nurse workstation WS-702 pointing to high-entropy domain.",
        evidenceRequirements = listOf("Sysmon Event 8 Log Export", "Target Memory Dump MD5"),
        successCriteria = listOf("Identify parent process svchost.exe", "Isolate host within 180s", "Write executive briefing"),
        safetyBoundary = "CONFIDENTIAL SIMULATED SANDBOX ONLY. Zero unauthorized network interaction.",
        debriefSummary = "Adversary attempted living-off-the-land execution via unquoted service path.",
        followUpAction = "Execute 5-min timeline correlation drill."
      ),
      CyberMissionV10(
        title = "Deploy Sigma Defensive Rule for Kerberoasting",
        objective = "Construct Sigma rule targeting RC4-HMAC ticket requests (Event 4769) with encryption type 0x17.",
        targetRole = "Detection Engineer",
        difficulty = SkillLevel.ADVANCED,
        timeBudget = MissionDuration.THIRTY_MIN,
        prerequisites = listOf("Active Directory Kerberos Protocol", "Sigma Syntax"),
        targetSkills = listOf("Detection Engineering", "Active Directory Security"),
        targetTools = listOf("Sigma CLI", "Splunk", "Zeek"),
        scenarioDescription = "Service accounts within Apex Global Banking requested anomalous TGS tickets during non-business hours.",
        evidenceRequirements = listOf("Validated Sigma YAML", "True Positive Test Suite Result"),
        successCriteria = listOf("Zero false positives against backup admin service", "Detection latency under 60 seconds"),
        safetyBoundary = "CONFIDENTIAL SIMULATED SANDBOX ONLY. Zero unauthorized network interaction.",
        debriefSummary = "Kerberoasting mitigated by enforcing AES256 ticket encryption policy across SPNs.",
        followUpAction = "Draft change request ticket in Jira Workplace."
      )
    )
  }

  private fun createInitialLearnerDiagnostics(): LearnerDiagnosticProfile {
    return LearnerDiagnosticProfile(
      whatTheyKnow = listOf(
        "OSI Model 7 Layers & Encapsulation",
        "TCP/IP 3-Way Handshake & FIN/RST Flags",
        "Windows Event IDs (4624, 4625, 4688, 4769)",
        "Sysmon Event ID 1 (Process Create) & ID 3 (Network Connect)"
      ),
      whatTheyDontKnow = listOf(
        "Post-Quantum Lattice Cryptography (ML-KEM, ML-DSA)",
        "SCADA Modbus Function Code Injections",
        "Advanced Kernel Driver Rootkit Hooks"
      ),
      whatTheyThinkTheyKnow = listOf(
        "AWS IAM STS AssumeRole boundary traversal (overconfidence gap identified)"
      ),
      whatTheyCanActuallyDo = listOf(
        "Parse raw PCAP files with tshark and Wireshark filters",
        "Construct Splunk SPL transaction and stats pipelines",
        "Author validated Sigma detection rules mapped to MITRE ATT&CK"
      ),
      whatTheyForget = listOf(
        "Linux PAM /etc/pam.d/ common-auth configuration syntax (Decay: 38%)",
        "Wireshark display filter regex flags"
      ),
      whatTheyStruggleWith = listOf(
        "Premature alert closure during high-velocity SIEM alert spikes"
      ),
      whatTheyEnjoy = listOf(
        "Interactive root-cause log forensics",
        "Defensive Sigma rule authoring"
      ),
      whatTheyAvoid = listOf(
        "Executive non-technical phone briefings"
      ),
      learningSpeedRating = "Top 10% in Network & SIEM analysis, Moderate in Binary Disassembly",
      transferabilityRating = "Proven ability to map Windows security concepts to Linux and Cloud environments"
    )
  }

  private fun createInitialSkillDecayRadar(): List<SkillDecayItemV11> {
    return listOf(
      SkillDecayItemV11(
        skillId = "decay_linux_pam",
        skillName = "Linux PAM & Authentication Modules",
        currentLevel = 62,
        peakLevel = 90,
        trend = "-28% over 14 days",
        decayRisk = DecayRiskLevel.HIGH,
        daysSinceLastActivePractice = 14,
        projectedRetentionDays = 6,
        recommendedResurrection = "5-minute graduated Linux PAM configuration challenge."
      ),
      SkillDecayItemV11(
        skillId = "decay_pki_x509",
        skillName = "PKI & X.509 Certificate Validation",
        currentLevel = 74,
        peakLevel = 88,
        trend = "-14% over 9 days",
        decayRisk = DecayRiskLevel.MEDIUM,
        daysSinceLastActivePractice = 9,
        projectedRetentionDays = 12,
        recommendedResurrection = "5-minute TLS certificate chain verification quiz."
      ),
      SkillDecayItemV11(
        skillId = "decay_sigma_syntax",
        skillName = "Sigma Rule YAML Syntax & Condition Modifiers",
        currentLevel = 86,
        peakLevel = 92,
        trend = "-6% over 4 days",
        decayRisk = DecayRiskLevel.LOW,
        daysSinceLastActivePractice = 4,
        projectedRetentionDays = 25,
        recommendedResurrection = "10-minute Sigma condition modifier speed run."
      )
    )
  }

  private fun createInitialResurrectionChallenges(): List<SkillResurrectionChallenge> {
    return listOf(
      SkillResurrectionChallenge(
        skillId = "skill_linux_pam",
        skillName = "Linux PAM & Authentication Hardening",
        decayPercentage = 38,
        promptRecallQuestion = "Which PAM control flag causes authentication to fail immediately without evaluating subsequent modules?",
        hint1 = "Think about the difference between 'requisite' and 'required'.",
        partialSupport = "In PAM: 'required' continues checking; 'r_______' terminates immediately on failure.",
        authoritativeAnswer = "requisite",
        rootExplanation = "The 'requisite' control flag causes PAM to abort immediately upon failure and return the error code to the application without evaluating further modules in the stack.",
        retryQuestion = "If you want PAM to log all failures but continue checking remaining modules, which control flag should you use?"
      ),
      SkillResurrectionChallenge(
        skillId = "skill_sysmon_event8",
        skillName = "Sysmon Event ID 8: CreateRemoteThread Injection",
        decayPercentage = 24,
        promptRecallQuestion = "What specific telemetry field in Sysmon Event ID 8 identifies the process that received the injected thread?",
        hint1 = "There is a SourceProcess and a _______Process field.",
        partialSupport = "SourceProcessId is the injector; Target_______Id is the victim process.",
        authoritativeAnswer = "TargetProcessId (or TargetImage)",
        rootExplanation = "Sysmon Event 8 records CreateRemoteThread API calls. TargetProcessId and TargetImage specify the process memory space that was injected by SourceImage.",
        retryQuestion = "What Windows API is typically used by adversaries immediately before CreateRemoteThread to allocate memory in the victim process?"
      )
    )
  }

  private fun createInitialMistakeHistory(): List<MistakeRecordV10> {
    return listOf(
      MistakeRecordV10(
        whatHappened = "Closed SIEM alert regarding PowerShell invocation on WS-702 after seeing valid code signature on parent process.",
        whyItHappened = "Assumed code signature on parent process (explorer.exe) guaranteed child process safety.",
        pattern = MistakePatternType.PREMATURE_CLOSURE,
        businessAndTechnicalImpact = "Delayed containment by 42 minutes, allowing adversary to complete memory staging.",
        correctApproach = "Always inspect child process command-line arguments (-enc, -w hidden) regardless of parent signature.",
        microDrillTitle = "Living-off-the-land Process Lineage Audit",
        microDrillPrompt = "Evaluate 5 process trees where legitimate signed binaries spawn obfuscated PowerShell child processes."
      )
    )
  }

  private fun createInitialMistakeDnaV11(): List<MistakePatternDetailV11> {
    return listOf(
      MistakePatternDetailV11(
        pattern = MistakePatternV11.PREMATURE_CLOSURE,
        observedEvidence = "Closed alert on rundll32.exe invocation after validating that rundll32.exe is a signed Microsoft binary without inspecting the DLL path parameter.",
        frequencyCount = 3,
        recentOccurrence = "2026-08-27 21:15 UTC (Hospital Sim)",
        businessImpact = "Allows living-off-the-land binary execution to proceed uncontained into patient subnet.",
        concreteExample = "rundll32.exe C:\\Users\\Public\\malware.dll,Start (Ignored untrusted folder path)",
        microDrillTitle = "Parameter-First Binary Audit",
        microDrillPrompt = "Audit 10 command-line strings to verify path reputations before closing tickets.",
        recommendedPracticeAction = "Run 5-minute Sysmon Event 1 command line inspection drill."
      ),
      MistakePatternDetailV11(
        pattern = MistakePatternV11.CONFIRMATION_BIAS,
        observedEvidence = "Searched exclusively for beaconing traffic on port 443 after forming initial HTTPS C2 hypothesis, ignoring DNS tunneling on port 53.",
        frequencyCount = 2,
        recentOccurrence = "2026-08-26 14:30 UTC",
        businessImpact = "Delayed detection of exfiltrated credentials by 2.5 hours.",
        concreteExample = "Overlooked 45,000 high-entropy TXT record requests on port 53 while filtering for SSL handshakes.",
        microDrillTitle = "Hypothesis Falsification Arena",
        microDrillPrompt = "List at least 2 contradicting hypotheses and search for negative evidence before concluding C2 channel.",
        recommendedPracticeAction = "Complete Reasoning Graph 4.0 Falsification drill."
      ),
      MistakePatternDetailV11(
        pattern = MistakePatternV11.JARGON_DEPENDENCE,
        observedEvidence = "Sent incident briefing to Chief Financial Officer using unparsed technical acronyms ('NTDS.dit DCSync via RPC-SMB').",
        frequencyCount = 2,
        recentOccurrence = "2026-08-25 10:12 UTC",
        businessImpact = "CFO failed to understand business urgency; wire transfer hold was delayed.",
        concreteExample = "Used 8 raw protocol acronyms without stating expected financial exposure.",
        microDrillTitle = "Executive Plain-Language Translation",
        microDrillPrompt = "Rewrite a technical vulnerability report for a Board of Directors audience with zero acronyms.",
        recommendedPracticeAction = "Take Executive Communication Mode Voice Drill."
      )
    )
  }

  private fun createInitialReasoningAudit(): ReasoningGraphAudit {
    val learnerSteps = listOf(
      ReasoningStepV10(
        stepIndex = 1,
        evidenceObserved = "Sysmon Event ID 1: powershell.exe -enc SQBFAFgA...",
        observation = "Encoded base64 string in command line arguments.",
        hypothesisFormed = "Potential fileless payload execution.",
        testExecuted = "Base64 decode string in CyberChef sandbox.",
        testResult = "Deobfuscated script downloads second-stage payload from 198.51.100.22.",
        decisionMade = "Confirm high-severity true positive.",
        actionTaken = "Triggered EDR network host isolation for WS-702.",
        outcome = "Host isolated within 140s; lateral staging halted."
      )
    )

    val referenceSteps = listOf(
      ReasoningStepV10(
        stepIndex = 1,
        evidenceObserved = "Sysmon Event ID 1: powershell.exe -enc SQBFAFgA...",
        observation = "Encoded base64 string in command line arguments.",
        hypothesisFormed = "Potential fileless payload execution.",
        testExecuted = "Base64 decode string and check network telemetry for external connections.",
        testResult = "Deobfuscated payload identified with C2 IP 198.51.100.22.",
        decisionMade = "Confirm high-severity true positive.",
        actionTaken = "Isolate host and block egress IP 198.51.100.22 on edge firewall.",
        outcome = "Complete enterprise-wide containment."
      )
    )

    return ReasoningGraphAudit(
      learnerTrace = learnerSteps,
      referenceModelTrace = referenceSteps,
      evidenceFirstScore = 92,
      hypothesisFalsificationScore = 84,
      timelineDisciplineScore = 88,
      unnecessaryActionCount = 0,
      missedEvidencePoints = listOf("Edge firewall egress block for C2 IP 198.51.100.22"),
      prematureConclusionFlag = false
    )
  }

  private fun createInitialInvestigationReplay(): List<InvestigationReplaySession> {
    return listOf(
      InvestigationReplaySession(
        incidentId = "replay_incident_702",
        incidentTitle = "Hospital Patient Subnet Lateral Breach",
        overallReasoningScore = 88,
        evidenceFirstFidelity = 92,
        steps = listOf(
          ReasoningReplayStepV11(
            stepIndex = 1,
            timestamp = "02:14:05 UTC",
            beliefHypothesis = "Anomalous DNS traffic might be routine NTP or DNS over HTTPS updater.",
            evidenceSupporting = "Port 53 standard DNS requests.",
            evidenceContradicting = "Query frequency was 250 requests/sec with high Shannon entropy (> 4.8).",
            evidenceIgnored = "Initial Sysmon Event 22 (DNS query) payload size warnings.",
            checksShouldHavePerformed = "Calculate Shannon entropy on subdomains before dismissing alert.",
            actionTaken = "Queried WHOIS on domain.",
            consequenceOutcome = "Domain registered 4 hours prior in bulletproof hosting AS."
          ),
          ReasoningReplayStepV11(
            stepIndex = 2,
            timestamp = "02:16:30 UTC",
            beliefHypothesis = "Adversary is staging in-memory payload via unquoted service path.",
            evidenceSupporting = "Sysmon Event 8 CreateRemoteThread targeting svchost.exe.",
            evidenceContradicting = "None.",
            evidenceIgnored = "None.",
            checksShouldHavePerformed = "Dump process memory string headers.",
            actionTaken = "Initiated host network quarantine.",
            consequenceOutcome = "Patient monitor subnet protected; lateral movement blocked."
          )
        ),
        coachCritique = "Excellent recovery after initial hesitation on DNS entropy. Host isolation timing (sub-180s) met enterprise SLA."
      )
    )
  }

  private fun createInitialRealityGraph(): CyberRealityGraphData {
    val nodes = listOf(
      CyberGraphNode("node_soc_analyst", "SOC Analyst L2", GraphNodeType.CAREER, "Defensive monitoring and triage", "Blue Team", 78),
      CyberGraphNode("node_siem", "SIEM Architecture", GraphNodeType.SKILL, "Centralized event log correlation", "Blue Team", 88),
      CyberGraphNode("node_splunk", "Splunk SPL", GraphNodeType.TOOL, "Search processing language query pipelines", "Blue Team", 92),
      CyberGraphNode("node_sysmon", "Windows Sysmon", GraphNodeType.TOOL, "System monitor process & network telemetry", "Blue Team", 86),
      CyberGraphNode("node_mitre", "MITRE ATT&CK", GraphNodeType.FRAMEWORK, "Adversary tactics, techniques, and procedures", "Intelligence", 90),
      CyberGraphNode("node_kerberoast", "Kerberoasting (T1558.003)", GraphNodeType.TECHNIQUE, "Requesting service tickets with RC4 encryption", "Active Directory", 84),
      CyberGraphNode("node_sigma", "Sigma Detection", GraphNodeType.DEFENSE, "Generic detection rule format", "Detection Engineering", 82),
      CyberGraphNode("node_lab_stjude", "Hospital SOC Range", GraphNodeType.LAB, "Healthcare ransomware simulation", "Practice", 95),
      CyberGraphNode("node_cve_2026", "CVE-2026-4401", GraphNodeType.CVE, "OpenSSH pre-auth buffer overflow", "Research", 70),
      CyberGraphNode("node_job_apex", "Apex Senior Analyst Job", GraphNodeType.JOB, "Tier-1 FinTech SOC position", "Career", 78)
    )

    val edges = listOf(
      CyberGraphEdge("node_soc_analyst", "node_siem", "REQUIRES"),
      CyberGraphEdge("node_siem", "node_splunk", "IMPLEMENTS"),
      CyberGraphEdge("node_soc_analyst", "node_sysmon", "REQUIRES"),
      CyberGraphEdge("node_sysmon", "node_mitre", "MAPS_TO"),
      CyberGraphEdge("node_kerberoast", "node_mitre", "CLASSIFIED_UNDER"),
      CyberGraphEdge("node_sigma", "node_kerberoast", "MITIGATES"),
      CyberGraphEdge("node_lab_stjude", "node_soc_analyst", "TESTS_COMPETENCY"),
      CyberGraphEdge("node_soc_analyst", "node_job_apex", "PREPARES_FOR")
    )

    return CyberRealityGraphData(nodes = nodes, edges = edges)
  }

  private fun createInitialMultiverse(): List<IncidentMultiverseScenario> {
    return listOf(
      IncidentMultiverseScenario(
        incidentId = "multi_apex_wire_breach",
        initialPivotalDecision = "Adversary initiated unauthorized API token generation on Prod-Auth server at 03:00 AM.",
        branches = listOf(
          MultiverseBranchV11(
            branchId = "branch_opt",
            decisionLabel = "Branch A: Immediate IAM Role Revocation & Token Invalidation",
            isOptimalBranch = true,
            consequenceSummary = "Token neutralized within 45 seconds. Zero unauthorized SWIFT transfer executions.",
            technicalImpact = "Single API credential revoked; automated failover token issued to payroll engine.",
            businessDowntimeHours = 0.0,
            financialLossEstimateUsd = 0,
            keyLesson = "Rapid identity revocation stops attack before lateral data movement."
          ),
          MultiverseBranchV11(
            branchId = "branch_isolate_all",
            decisionLabel = "Branch B: Unconditionally Isolate Entire Data Center Subnet",
            isOptimalBranch = false,
            consequenceSummary = "Attack stopped, but payroll and consumer banking interfaces went offline for 4.5 hours.",
            technicalImpact = "14 healthy microservices disconnected unexpectedly, triggering failover alerts.",
            businessDowntimeHours = 4.5,
            financialLossEstimateUsd = 450000,
            keyLesson = "Over-scoping containment without surgical IAM controls causes self-inflicted business outage."
          ),
          MultiverseBranchV11(
            branchId = "branch_delayed",
            decisionLabel = "Branch C: Delay Action to Collect 60 Minutes More Telemetry",
            isOptimalBranch = false,
            consequenceSummary = "Adversary exported session tokens and executed staging script to external S3 bucket.",
            technicalImpact = "12,000 user credentials exfiltrated; breach disclosure triggered under GDPR/SEC rules.",
            businessDowntimeHours = 18.0,
            financialLossEstimateUsd = 2800000,
            keyLesson = "Indecision and excessive hesitation under active compromise multiplies technical debt and regulatory penalty."
          )
        ),
        postMortemReview = "Optimal containment requires targeted IAM identity invalidation before considering heavy infrastructure isolation."
      )
    )
  }

  private fun createInitialAdversaryProfile(): LivingAdversaryProfile {
    return LivingAdversaryProfile(
      adversaryCodename = "PHANTOM_WEAVER (Adaptive AI Opponent)",
      detectedLearnerBlindSpots = listOf(
        "Learner frequently misses lateral movement using WMI process spawning",
        "Learner checks port 443 before validating high-entropy DNS tunneling"
      ),
      nextTargetedAttackVector = "WMI ExecMethod Infiltration across Windows VLAN 3",
      educationalTactics = listOf(
        "T1047: Windows Management Instrumentation",
        "T1071.004: DNS Tunneling Exfiltration",
        "T1078: Valid Accounts Abuse"
      ),
      sandboxBoundary = "STRICTLY AUTHORIZED LOCAL SANDBOX ONLY. Zero egress traffic."
    )
  }

  private fun createInitialFusionChallenges(): List<FusionChallengeScenario> {
    return listOf(
      FusionChallengeScenario(
        title = "Cloud Misconfiguration to Active Directory Domain Takeover",
        attackChainStory = "Adversary discovered exposed AWS S3 bucket containing Terraform state file -> extracted hardcoded domain join credentials -> assumed IAM role -> pivoted into on-premise AD via VPN gateway -> executed Kerberoasting.",
        domainScores = listOf(
          FusionDomainScore("Cloud Security", 88, "Identified exposed S3 ACL within 90s"),
          FusionDomainScore("Identity & IAM", 84, "Revoked compromised AWS STS session"),
          FusionDomainScore("Network Forensics", 80, "Traced VPN tunnel connection IP"),
          FusionDomainScore("Endpoint Telemetry", 86, "Detected Kerberoasting ticket requests via Event 4769"),
          FusionDomainScore("Threat Intelligence", 90, "Attributed TTPs to FIN7 playbook"),
          FusionDomainScore("Incident Response", 85, "Orchestrated dual cloud/on-prem containment")
        ),
        overallFusionScore = 85,
        isCompleted = false
      )
    )
  }

  private fun createInitialRedBlueSessions(): List<RedBlueSession> {
    return listOf(
      RedBlueSession(
        redPhaseAttackPlan = "Simulate spear-phishing payload deploying scheduled task persistence (T1053.005) executing PowerShell living-off-the-land script.",
        redSimulatedArtifactsGenerated = listOf(
          "Windows Event 4698: A scheduled task was created (TaskName: \\Microsoft\\Windows\\UpdateCheck)",
          "Sysmon Event 1: schtasks.exe /create /tn UpdateCheck /tr powershell.exe",
          "Network Connect Event 3: outbound connection to 198.51.100.44:8443"
        ),
        blueDetectionScore = 92,
        blueReasoningScore = 88,
        bluePrioritizationScore = 85,
        blueContainmentScore = 90,
        blueCommunicationScore = 86,
        isCompleted = true
      )
    )
  }

  private fun createInitialWorkplaceInbox(): List<WorkplaceItemV10> {
    return listOf(
      WorkplaceItemV10(
        senderName = "Marcus Vance",
        senderRole = "Chief Financial Officer",
        channel = WorkplaceChannelV10.EXECUTIVE_EMAIL,
        timestamp = "09:42 AM",
        subjectOrSnippet = "URGENT: Payroll batch delayed due to security hold",
        fullContent = "Team, I am hearing from HR that our automated payroll processing server was isolated by SOC automation. We have 4,000 employees expecting direct deposits at noon. Please un-isolate this server immediately or explain the exact business justification.",
        isUrgent = true,
        isFalsePositive = false,
        businessImpactDescription = "Potential employee dissatisfaction and regulatory labor penalty if delayed past noon.",
        requiredActionOptions = listOf(
          "Explain active living-off-the-land PowerShell breach in plain English with 20-min remediation timeline.",
          "Un-isolate server unconditionally to process payroll, risking active credential exfiltration.",
          "Provide read-only sanitized payroll enclave while maintaining active containment."
        )
      )
    )
  }

  private fun createInitialWorkplaceCrisesV11(): List<WorkplaceCrisisItemV11> {
    return listOf(
      WorkplaceCrisisItemV11(
        sector = EnterpriseSectorV11.FINTECH,
        senderName = "Marcus Vance",
        senderTitle = "Chief Financial Officer",
        channel = WorkplaceChannelV10.EXECUTIVE_EMAIL,
        subject = "URGENT: Payroll server isolated by SOC automation",
        bodyText = "We have 4,000 employees awaiting direct deposit at 12:00 PM. Un-isolate payroll host WS-PAY-01 immediately or give me a 10-minute briefing.",
        organizationalConstraint = "Zero tolerance for payroll delay vs high risk of wire fraud token theft.",
        isUrgent = true,
        options = listOf(
          "Option 1: Deploy sanitized read-only payroll staging enclave while keeping infected host isolated.",
          "Option 2: Un-isolate host immediately without investigating child process injection.",
          "Option 3: Ignore CFO email and continue deep forensic disk dump for 4 hours."
        ),
        selectedOptionIndex = 0,
        outcomeFeedback = "Optimal resolution: Balanced technical containment with zero critical business disruption."
      ),
      WorkplaceCrisisItemV11(
        sector = EnterpriseSectorV11.HEALTHCARE,
        senderName = "Dr. Eleanor Vance",
        senderTitle = "Chief Medical Officer",
        channel = WorkplaceChannelV10.PHONE_ESCALATION,
        subject = "Patient monitor telemetry lag in ICU 3",
        bodyText = "ICU Nurse station WS-702 is reporting a red countdown timer and network telemetry latency. We have 8 critical patients connected.",
        organizationalConstraint = "Human life safety: Patient telemetry cannot be interrupted without manual physician bedside backup.",
        isUrgent = true,
        options = listOf(
          "Option 1: Transition ICU bedside monitors to isolated offline local mode, then quarantine host network interface.",
          "Option 2: Broadcast shut down all medical IoT network switches remotely.",
          "Option 3: Dismiss alert as medical software update lag."
        )
      )
    )
  }

  private fun createInitialVoiceDrills(): List<VoiceCyberDrillScenario> {
    return listOf(
      VoiceCyberDrillScenario(
        personaTitle = "Panicked Employee (Sarah from Accounting)",
        promptAudioTranscript = "Help! I opened an email invoice from 'FedEx-Billing', and my screen turned completely black with a red skull and a 10-minute countdown! What should I do right now?!",
        learnerResponseTranscript = "Sarah, please stay calm. Do not turn off your computer yet. Unplug the blue Ethernet network cable from the back of your PC or disconnect Wi-Fi right now to stop it from spreading. I am opening an incident ticket and dispatching IT.",
        technicalCorrectnessScore = 95,
        questionQualityScore = 88,
        communicationCalmnessScore = 96,
        prioritizationScore = 94,
        completenessScore = 90,
        coachCritique = "Outstanding performance: Clear physical containment command (disconnect cable) given immediately without blaming the user or causing panic."
      )
    )
  }

  private fun createInitialAiAgentRegistry(): List<AiAgentStateV11> {
    return AiSpecialistRoleV11.entries.map { role ->
      AiAgentStateV11(
        role = role,
        modelIdentifier = "gemini-3.7-flash (Simulated Local/API)",
        promptInjectionShieldActive = true,
        confidenceRating = 94,
        lastAuditLog = "Role policy verified; prompt injection firewall active."
      )
    }
  }

  private fun createInitialEvidenceLedger(): List<SkillEvidenceEntryV11> {
    return listOf(
      SkillEvidenceEntryV11(
        skillName = "Process Injection Forensic Triage",
        activityTitle = "Hospital ICU Subnet Memory & Sysmon Triage",
        verificationType = EvidenceVerificationType.LAB_VERIFIED,
        timestamp = "2026-08-28 02:25",
        resultSummary = "Correctly correlated Sysmon Event 8 with svchost.exe injection. Host isolated within 140 seconds.",
        evidenceHash = "sha256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
      ),
      SkillEvidenceEntryV11(
        skillName = "Kerberoasting Detection Engineering",
        activityTitle = "Sigma Detection Rule Deployment (Event 4769)",
        verificationType = EvidenceVerificationType.PROJECT_EVIDENCED,
        timestamp = "2026-08-27 18:40",
        resultSummary = "Authored and tested Sigma rule against 10,000 real telemetry logs with 0 false positives.",
        evidenceHash = "sha256:4a91c284b3917dc9401ab1826fcd91e84712048591823abce12837482910fa88"
      ),
      SkillEvidenceEntryV11(
        skillName = "Executive Incident Communication",
        activityTitle = "CFO Wire Fraud Risk Briefing",
        verificationType = EvidenceVerificationType.MENTOR_REVIEWED,
        timestamp = "2026-08-26 11:15",
        resultSummary = "Approved by AI CISO evaluator with 94% clarity rating and zero unparsed technical jargon.",
        evidenceHash = "sha256:8812ab49f0182374619472018234857102938475610293847561029384756102"
      )
    )
  }

  private fun createInitialOpportunities(): List<OpportunityItemV10> {
    return listOf(
      OpportunityItemV10(
        title = "Junior Cyber Defense Analyst Intern",
        organization = "CyberShield Defense Institute",
        type = OpportunityTypeV10.INTERNSHIPS,
        matchPercentage = 84,
        missingRequirements = listOf("Sigma Rule Detection Engineering Capstone"),
        recommendedAction = "Complete 30-min Sigma Builder lab to bridge 16% gap.",
        deadline = "2026-09-15",
        liveStatus = DataLiveStatus.DEMO,
        sourceUrlNote = "Global Cyber Opportunities Catalog"
      ),
      OpportunityItemV10(
        title = "National Collegiate Cyber Defense Competition (CCDC)",
        organization = "Collegiate Cyber League",
        type = OpportunityTypeV10.CTFS,
        matchPercentage = 91,
        missingRequirements = listOf("Linux PAM authentication hardening drill"),
        recommendedAction = "Run 15-min Linux Syslog & PAM challenge.",
        deadline = "2026-10-01",
        liveStatus = DataLiveStatus.DEMO,
        sourceUrlNote = "National CTF & Competition Registry"
      )
    )
  }

  private fun createInitialOpportunitiesV11(): List<OpportunityRadarItemV11> {
    return listOf(
      OpportunityRadarItemV11(
        title = "Junior Cyber Defense Analyst Intern",
        organization = "CyberShield Defense Institute",
        type = OpportunityTypeV10.INTERNSHIPS,
        country = "United States / Remote",
        isRemote = true,
        experienceLevel = "Entry-Level / Student",
        matchPercentage = 84,
        missingRequirements = listOf("Sigma Rule Detection Engineering Capstone"),
        deadline = "2026-09-15",
        isFree = true,
        studentEligibility = "Enrolled undergraduate or self-taught portfolio candidate",
        provenanceStatus = DataLiveStatus.DEMO
      ),
      OpportunityRadarItemV11(
        title = "National Collegiate Cyber Defense Competition (CCDC)",
        organization = "Collegiate Cyber League",
        type = OpportunityTypeV10.CTFS,
        country = "Global / Hybrid",
        isRemote = true,
        experienceLevel = "All Levels",
        matchPercentage = 91,
        missingRequirements = listOf("Linux PAM authentication hardening drill"),
        deadline = "2026-10-01",
        isFree = true,
        studentEligibility = "Open to university teams & student researchers",
        provenanceStatus = DataLiveStatus.DEMO
      ),
      OpportunityRadarItemV11(
        title = "Women in Cybersecurity (WiCyS) Annual Fellowship",
        organization = "WiCyS Global Foundation",
        type = OpportunityTypeV10.FELLOWSHIPS,
        country = "International",
        isRemote = true,
        experienceLevel = "Student / Career Changer",
        matchPercentage = 88,
        missingRequirements = listOf("STAR Behavioral Interview Recording"),
        deadline = "2026-11-01",
        isFree = true,
        studentEligibility = "Underrepresented cybersecurity professionals",
        provenanceStatus = DataLiveStatus.DEMO
      )
    )
  }

  private fun createInitialPortfolio(): List<PortfolioArtifactV10> {
    return listOf(
      PortfolioArtifactV10(
        title = "Enterprise Sysmon & Sigma Detection Framework",
        type = "GitHub README",
        markdownContent = """
          # Enterprise Sysmon & Sigma Detection Framework
          A production-grade detection repository targeting MITRE ATT&CK T1059 (PowerShell) and T1558 (Kerberoasting).
          
          ## Capabilities
          - Automated Sysmon XML configuration tuned for zero noise.
          - 12 verified Sigma rules tested against live execution telemetry.
          - End-to-end incident triage playbook for Tier-1 SOC analysts.
        """.trimIndent(),
        verifiedEvidenceHashes = listOf("sha256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069")
      )
    )
  }

  private fun createAiSafetyRegistry(): List<AiMentorSafetyProfile> {
    return listOf(
      AiMentorSafetyProfile(
        agentName = "AI SOC Coach",
        authorizedRole = "Guide investigative reasoning and log correlation without providing raw answers",
        allowedInputs = listOf("Sanitized telemetry", "Student triage notes", "Investigation timestamps"),
        strictLimitations = listOf("Cannot execute live network commands", "Cannot view user credentials"),
        safetyEnforcementRule = "Enforce Socratic scaffolding; prompt for hypothesis before providing hints."
      ),
      AiMentorSafetyProfile(
        agentName = "AI CISO Interrogator",
        authorizedRole = "Simulate executive cross-examination under active business downtime pressure",
        allowedInputs = listOf("Student incident summaries", "Business impact assessments"),
        strictLimitations = listOf("Cannot modify sandbox environment state"),
        safetyEnforcementRule = "Evaluate jargon density and financial clarity strictly."
      ),
      AiMentorSafetyProfile(
        agentName = "Untrusted Threat Feed Ingestor",
        authorizedRole = "Parse external threat bulletins and CVE advisories",
        allowedInputs = listOf("Raw external JSON/RSS text feeds"),
        strictLimitations = listOf("Strictly quarantined from agent system prompt context"),
        safetyEnforcementRule = "All external text treated as untrusted data; prompt injection scanner runs on every token."
      )
    )
  }
}
