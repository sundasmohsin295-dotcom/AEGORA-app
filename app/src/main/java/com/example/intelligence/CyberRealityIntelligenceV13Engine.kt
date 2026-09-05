package com.example.intelligence

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * AEGORA v13.0 — CYBER REALITY INTELLIGENCE OPERATING SYSTEM ENGINE
 * Unifies the Global Cyber World -> Intelligence -> Knowledge Graph -> Learner Cyber Twin -> Career Trajectory.
 */
object CyberRealityIntelligenceV13Engine {

  // ============================================================
  // 1. CYBER REALITY ENGINE 2.0 (ENTITIES & RELATIONSHIP GRAPH)
  // ============================================================
  private val _realityNodes = MutableStateFlow(createInitialRealityNodes())
  val realityNodes: StateFlow<List<CyberRealityEntityNodeV13>> = _realityNodes.asStateFlow()

  private val _realityEdges = MutableStateFlow(createInitialRealityEdges())
  val realityEdges: StateFlow<List<CyberRealityEdgeV13>> = _realityEdges.asStateFlow()

  // ============================================================
  // 2. REAL-TIME INTELLIGENCE INGESTION & NORMALIZATION
  // ============================================================
  private val _normalizedIntelFeed = MutableStateFlow(createInitialNormalizedIntel())
  val normalizedIntelFeed: StateFlow<List<NormalizedCyberIntelligenceItemV13>> = _normalizedIntelFeed.asStateFlow()

  private val _liveIntelStatus = MutableStateFlow(IntelLiveStatusV13.LIVE_INGESTED)
  val liveIntelStatus: StateFlow<IntelLiveStatusV13> = _liveIntelStatus.asStateFlow()

  // ============================================================
  // 3. CYBER EVENT TIMELINE
  // ============================================================
  private val _timelineEvents = MutableStateFlow(createInitialTimelineEvents())
  val timelineEvents: StateFlow<List<CyberTimelineEventV13>> = _timelineEvents.asStateFlow()

  private val _selectedTimelineFilter = MutableStateFlow("All")
  val selectedTimelineFilter: StateFlow<String> = _selectedTimelineFilter.asStateFlow()

  // ============================================================
  // 4. PERSONAL IMPACT ENGINE & THREAT RADAR
  // ============================================================
  private val _personalImpactAssessments = MutableStateFlow(createInitialImpactAssessments())
  val personalImpactAssessments: StateFlow<List<PersonalImpactAssessmentV13>> = _personalImpactAssessments.asStateFlow()

  private val _personalThreatRadar = MutableStateFlow(createInitialThreatRadar())
  val personalThreatRadar: StateFlow<List<PersonalThreatRadarItemV13>> = _personalThreatRadar.asStateFlow()

  // ============================================================
  // 5. EVENT-TO-MISSION GENERATOR
  // ============================================================
  private val _eventMissions = MutableStateFlow(createInitialEventMissions())
  val eventMissions: StateFlow<List<EventMissionV13>> = _eventMissions.asStateFlow()

  // ============================================================
  // 6. CAREER MARKET INTELLIGENCE & SKILL ECONOMY
  // ============================================================
  private val _marketSkillDemands = MutableStateFlow(createInitialMarketSkills())
  val marketSkillDemands: StateFlow<List<MarketSkillDemandV13>> = _marketSkillDemands.asStateFlow()

  private val _highLeverageSkills = MutableStateFlow(createInitialHighLeverageSkills())
  val highLeverageSkills: StateFlow<List<HighLeverageSkillCompoundingNodeV13>> = _highLeverageSkills.asStateFlow()

  private val _skillDependencies = MutableStateFlow(createInitialSkillDependencies())
  val skillDependencies: StateFlow<List<SkillPrerequisiteDependencyV13>> = _skillDependencies.asStateFlow()

  // ============================================================
  // 7. MASTERY TRANSFER GATES & EVIDENCE QUALITY 2.0
  // ============================================================
  private val _masteryGatesV13 = MutableStateFlow(createInitialMasteryGatesV13())
  val masteryGatesV13: StateFlow<List<MasteryTransferGateCheckV13>> = _masteryGatesV13.asStateFlow()

  private val _evidenceQualityMetrics = MutableStateFlow(createInitialEvidenceQuality())
  val evidenceQualityMetrics: StateFlow<List<EvidenceQuality20MetricV13>> = _evidenceQualityMetrics.asStateFlow()

  // ============================================================
  // 8. PERSONAL LEARNING EXPERIMENTS
  // ============================================================
  private val _learningExperiments = MutableStateFlow(createInitialLearningExperiments())
  val learningExperiments: StateFlow<List<PersonalLearningExperimentV13>> = _learningExperiments.asStateFlow()

  // ============================================================
  // 9. CYBER SEASON CAMPAIGNS
  // ============================================================
  private val _seasonCampaigns = MutableStateFlow(createInitialSeasonCampaigns())
  val seasonCampaigns: StateFlow<List<CyberSeasonCampaignV13>> = _seasonCampaigns.asStateFlow()

  // ============================================================
  // 10. ORGANIZATIONAL & INCIDENT MEMORY
  // ============================================================
  private val _enterpriseState = MutableStateFlow(FictionalEnterpriseStateV13())
  val enterpriseState: StateFlow<FictionalEnterpriseStateV13> = _enterpriseState.asStateFlow()

  private val _incidentMemoryLogs = MutableStateFlow(createInitialIncidentMemory())
  val incidentMemoryLogs: StateFlow<List<PersistentIncidentMemoryRecordV13>> = _incidentMemoryLogs.asStateFlow()

  // ============================================================
  // 11. CYBER SKILL CONSTELLATION 3.0
  // ============================================================
  private val _skillConstellation = MutableStateFlow(createInitialSkillConstellation())
  val skillConstellation: StateFlow<List<SkillConstellationNodeV13>> = _skillConstellation.asStateFlow()

  // ============================================================
  // 12. SECURITY PRINCIPLE MATRIX & TOOL-AGNOSTIC LEARNING
  // ============================================================
  private val _principleMatrix = MutableStateFlow(createInitialPrincipleMatrix())
  val principleMatrix: StateFlow<List<SecurityPrincipleMatrixV13>> = _principleMatrix.asStateFlow()

  private val _toolAgnosticChallenges = MutableStateFlow(createInitialToolTransferChallenges())
  val toolAgnosticChallenges: StateFlow<List<ToolAgnosticTransferChallengeV13>> = _toolAgnosticChallenges.asStateFlow()

  // ============================================================
  // 13. REAL-WORLD CONSTRAINTS & ETHICAL DECISION LAB
  // ============================================================
  private val _constraintScenarios = MutableStateFlow(createInitialConstraintScenarios())
  val constraintScenarios: StateFlow<List<RealWorldConstraintScenarioV13>> = _constraintScenarios.asStateFlow()

  private val _ethicalCases = MutableStateFlow(createInitialEthicalCases())
  val ethicalCases: StateFlow<List<EthicalDecisionCaseV13>> = _ethicalCases.asStateFlow()

  private val _cultureCases = MutableStateFlow(createInitialCultureCases())
  val cultureCases: StateFlow<List<SecurityCultureSimulatorCaseV13>> = _cultureCases.asStateFlow()

  // ============================================================
  // 14. AI MENTOR STYLE & AI SECOND OPINION
  // ============================================================
  private val _activeMentorStyle = MutableStateFlow(AiMentorPersonaStyleV13.SOCRATIC)
  val activeMentorStyle: StateFlow<AiMentorPersonaStyleV13> = _activeMentorStyle.asStateFlow()

  private val _secondOpinionCases = MutableStateFlow(createInitialSecondOpinionCases())
  val secondOpinionCases: StateFlow<List<AiSecondOpinionComparisonV13>> = _secondOpinionCases.asStateFlow()

  // ============================================================
  // 15. TIME & ENERGY AWARE LEARNING (NEXT BEST ACTION 3.0)
  // ============================================================
  private val _learnerEnergy = MutableStateFlow(LearnerEnergyStateV13.NORMAL)
  val learnerEnergy: StateFlow<LearnerEnergyStateV13> = _learnerEnergy.asStateFlow()

  private val _availableTimeMinutes = MutableStateFlow(15)
  val availableTimeMinutes: StateFlow<Int> = _availableTimeMinutes.asStateFlow()

  private val _nextBestAction = MutableStateFlow(calculateNextBestAction(LearnerEnergyStateV13.NORMAL, 15))
  val nextBestAction: StateFlow<NextBestAction30V13> = _nextBestAction.asStateFlow()

  // ============================================================
  // 16. PROFESSIONAL SIMULATION REPORT & GROWTH INDEX
  // ============================================================
  private val _simulationDebriefReport = MutableStateFlow(createInitialDebriefReport())
  val simulationDebriefReport: StateFlow<ProfessionalSimulationDebriefReportV13> = _simulationDebriefReport.asStateFlow()

  private val _growthIndex = MutableStateFlow(AegoraProfessionalGrowthIndexV13())
  val growthIndex: StateFlow<AegoraProfessionalGrowthIndexV13> = _growthIndex.asStateFlow()

  // ============================================================
  // 17. RELEASE CONTROL & COMPLIANCE GATES
  // ============================================================
  private val _releaseCenter = MutableStateFlow(ReleaseCenterStateV13())
  val releaseCenter: StateFlow<ReleaseCenterStateV13> = _releaseCenter.asStateFlow()

  // ============================================================
  // PUBLIC ACTIONS & STATE MUTATIONS
  // ============================================================

  fun setTimelineFilter(filter: String) {
    _selectedTimelineFilter.value = filter
  }

  fun setMentorStyle(style: AiMentorPersonaStyleV13) {
    _activeMentorStyle.value = style
  }

  fun setLearnerEnergy(energy: LearnerEnergyStateV13) {
    _learnerEnergy.value = energy
    _nextBestAction.value = calculateNextBestAction(energy, _availableTimeMinutes.value)
  }

  fun setAvailableTimeMinutes(minutes: Int) {
    _availableTimeMinutes.value = minutes
    _nextBestAction.value = calculateNextBestAction(_learnerEnergy.value, minutes)
  }

  fun completeMasteryGateCheck(skillName: String) {
    _masteryGatesV13.update { list ->
      list.map { gate ->
        if (gate.skillName == skillName) {
          gate.copy(
            understandsConcept = true,
            recallAccuracy = true,
            labApplicationVerified = true,
            rawInvestigationPassed = true,
            crossContextTransferred = true,
            verbalExplanationClear = true,
            uncertaintyResiliencePassed = true,
            isDemonstratedCapabilityGranted = true
          )
        } else gate
      }
    }
  }

  fun recordIncidentMemory(
    scenarioTitle: String,
    actionsExecuted: List<String>,
    successes: List<String>,
    mistakes: List<String>,
    takeaway: String
  ) {
    val newRecord = PersistentIncidentMemoryRecordV13(
      scenarioTitle = scenarioTitle,
      actionsExecuted = actionsExecuted,
      successfulDefenses = successes,
      errorsOrMistakesMade = mistakes,
      keyTakeawayLearned = takeaway,
      generatedEvidenceProof = "sha256:v13mem_${UUID.randomUUID().toString().replace("-", "")}"
    )
    _incidentMemoryLogs.update { listOf(newRecord) + it }
  }

  fun simulateCareerWhatIf(roleName: String): CareerWhatIfSimulationResultV13 {
    return when (roleName) {
      "Cloud Security Engineer" -> CareerWhatIfSimulationResultV13(
        careerRole = "Cloud Security Engineer",
        requiredCapabilities = listOf("AWS/GCP IAM Policy Evaluation", "Terraform Sentinel Guardrails", "Kubernetes RBAC", "Container Escape Detection"),
        currentMatchPercent = 78,
        estimatedEffortWeeks = 8,
        highLeverageSkillsToAcquire = listOf("Linux Internals", "Networking / VPC Peering", "API Security"),
        recommendedProjects = listOf("Automated Cloud Custodian Egress Auditor", "Serverless Threat Detection Engine"),
        certificationPathway = listOf("AWS Certified Security - Specialty", "Certified Kubernetes Security Specialist (CKS)")
      )
      "SOC Analyst (Tier 2)" -> CareerWhatIfSimulationResultV13(
        careerRole = "SOC Analyst (Tier 2)",
        requiredCapabilities = listOf("Multi-Stage Alert Correlation", "Sigma Rule Authoring", "Memory Dumping & Volatility Analysis", "PowerShell Deobfuscation"),
        currentMatchPercent = 91,
        estimatedEffortWeeks = 3,
        highLeverageSkillsToAcquire = listOf("Windows Event Logs (Sysmon)", "Regex Log Parsing", "PCAP Extraction"),
        recommendedProjects = listOf("Sigma Rule Library targeting APT29 TTPs", "Wazuh Automated Incident Response Pipeline"),
        certificationPathway = listOf("CompTIA CySA+", "BTL1 (Blue Team Level 1)", "GIAC GCIA")
      )
      "AI Security / LLM Red Teamer" -> CareerWhatIfSimulationResultV13(
        careerRole = "AI Security / LLM Red Teamer",
        requiredCapabilities = listOf("Prompt Injection Defense Architecture", "Model Weight Poisoning TTPs", "OWASP Top 10 for LLM", "RAG Data Leakage Guardrails"),
        currentMatchPercent = 74,
        estimatedEffortWeeks = 6,
        highLeverageSkillsToAcquire = listOf("Python Security Tooling", "API Security", "Vector DB Access Control"),
        recommendedProjects = listOf("LLM Guardrail Benchmark Harness", "Autonomous Red Team Agent Filter"),
        certificationPathway = listOf("Certified AI Security Professional (CAISP)", "Offensive AI Security Lab Proof")
      )
      else -> CareerWhatIfSimulationResultV13(
        careerRole = "Digital Forensics & Incident Response (DFIR)",
        requiredCapabilities = listOf("MFT/USN Journal Forensics", "Volatily 3 Memory Analysis", "Timeline Analysis with Plaso", "Malware Sandbox Triage"),
        currentMatchPercent = 82,
        estimatedEffortWeeks = 5,
        highLeverageSkillsToAcquire = listOf("Linux/Windows Internals", "Causal Event Graphing", "Chain-of-Custody Hashing"),
        recommendedProjects = listOf("Ransomware Post-Mortem Digital Forensic Report", "Autonomous Memory Dumper"),
        certificationPathway = listOf("GIAC GCFE", "GIAC GCFA", "BTL2")
      )
    }
  }

  // ============================================================
  // PRIVATE FACTORY & SEED METHODS
  // ============================================================

  private fun calculateNextBestAction(energy: LearnerEnergyStateV13, minutes: Int): NextBestAction30V13 {
    return when {
      energy == LearnerEnergyStateV13.LOW_ENERGY -> NextBestAction30V13(
        actionTitle = "Review CISA Advisory & Spaced Flashcards",
        whyRecommended = "Low energy mode active: reinforcing memory pathways for Kerberoasting and CVE-2026-4401 without high cognitive strain.",
        expectedCapabilityBenefit = "+3% Theoretical Knowledge retention, prevents decay risk.",
        estimatedMinutes = minOf(minutes, 10),
        difficultyTier = "Low / Cognitive Refresh",
        energyRequirement = LearnerEnergyStateV13.LOW_ENERGY,
        generatedEvidenceType = "Micro-Recall Verification Log",
        matchedIntelEvent = "CISA KEV 2026-4401"
      )
      minutes <= 15 -> NextBestAction30V13(
        actionTitle = "15-Min Micro Investigation: DNS Tunneling Detection",
        whyRecommended = "Matches your 15-minute window; addresses weak practical transfer in network forensics identified in Cyber Twin 6.0.",
        expectedCapabilityBenefit = "+7% Practical Ability in PCAP triage and SOC investigation.",
        estimatedMinutes = 15,
        difficultyTier = "Medium / Micro-Drill",
        energyRequirement = energy,
        generatedEvidenceType = "Cryptographic Lab Proof Artifact (SHA-256)",
        matchedIntelEvent = "MITRE ATT&CK T1071.004 (DNS Exfiltration)"
      )
      else -> NextBestAction30V13(
        actionTitle = "30-Min Attack Journey Defense: Cloud IAM Escalation",
        whyRecommended = "Optimal match for your high focus and career target. Validates transferability from AWS IAM policy evaluation to GCP Service Accounts.",
        expectedCapabilityBenefit = "+12% Cross-Context Transferability & Decision Making Under Uncertainty.",
        estimatedMinutes = 30,
        difficultyTier = "High / Multi-Stage Scenario",
        energyRequirement = LearnerEnergyStateV13.HIGH_FOCUS,
        generatedEvidenceType = "Verified Incident Response Report & Sigma Rule",
        matchedIntelEvent = "Cloud Credential Exfiltration Advisory"
      )
    }
  }

  private fun createInitialRealityNodes(): List<CyberRealityEntityNodeV13> {
    return listOf(
      CyberRealityEntityNodeV13(
        id = "cve-2026-4401",
        type = RealityEntityTypeV13.CVE,
        name = "CVE-2026-4401 (HTTP/2 Rapid Reset Variant)",
        codeOrIdentifier = "CVE-2026-4401",
        description = "Stream multiplexing exhaustion flaw causing resource starvation in edge reverse proxies.",
        severityOrImpact = "CRITICAL (CVSS 9.8)",
        tags = listOf("Web", "DoS", "HTTP/2", "EdgeProxy"),
        sourceUrl = "https://nvd.nist.gov/vuln/detail/CVE-2026-4401"
      ),
      CyberRealityEntityNodeV13(
        id = "actor-apt29",
        type = RealityEntityTypeV13.THREAT_ACTOR,
        name = "APT29 (Cozy Bear / Midnight Blizzard)",
        codeOrIdentifier = "G0016",
        description = "State-sponsored cyber espionage group targeting cloud identity, token theft, and OAuth integrations.",
        severityOrImpact = "HIGH",
        tags = listOf("NationState", "CloudIdentity", "TokenTheft"),
        sourceUrl = "https://attack.mitre.org/groups/G0016/"
      ),
      CyberRealityEntityNodeV13(
        id = "technique-t1558",
        type = RealityEntityTypeV13.ATTACK_TECHNIQUE,
        name = "Steal or Forge Kerberos Tickets (T1558)",
        codeOrIdentifier = "T1558.003",
        description = "Adversaries request service tickets with weak encryption to perform offline Kerberoasting password cracking.",
        severityOrImpact = "HIGH",
        tags = listOf("ActiveDirectory", "CredentialAccess", "Kerberos"),
        sourceUrl = "https://attack.mitre.org/techniques/T1558/003/"
      ),
      CyberRealityEntityNodeV13(
        id = "tool-wazuh",
        type = RealityEntityTypeV13.SECURITY_TOOL,
        name = "Wazuh SIEM / XDR",
        codeOrIdentifier = "WAZUH-XDR",
        description = "Open source security monitoring, log analysis, vulnerability detection, and active response framework.",
        severityOrImpact = "DEFENSIVE",
        tags = listOf("SIEM", "EDR", "LogAnalysis", "BlueTeam"),
        sourceUrl = "https://wazuh.com"
      ),
      CyberRealityEntityNodeV13(
        id = "framework-mitre",
        type = RealityEntityTypeV13.FRAMEWORK,
        name = "MITRE ATT&CK Enterprise v15",
        codeOrIdentifier = "MITRE-ATTACK-15",
        description = "Curated knowledge base and model for cyber adversary behavior reflecting phases of adversary lifecycle.",
        severityOrImpact = "STANDARD",
        tags = listOf("Framework", "TTPs", "Taxonomy"),
        sourceUrl = "https://attack.mitre.org"
      )
    )
  }

  private fun createInitialRealityEdges(): List<CyberRealityEdgeV13> {
    return listOf(
      CyberRealityEdgeV13(
        sourceEntityId = "actor-apt29",
        targetEntityId = "technique-t1558",
        relation = RealityRelationTypeV13.USES,
        description = "APT29 actively leverages Kerberoasting to escalate privileges during lateral movement."
      ),
      CyberRealityEdgeV13(
        sourceEntityId = "technique-t1558",
        targetEntityId = "tool-wazuh",
        relation = RealityRelationTypeV13.DETECTED_BY,
        description = "Event ID 4769 with Ticket Options 0x40810000 and RC4 encryption is alerted by Wazuh rule 60105."
      ),
      CyberRealityEdgeV13(
        sourceEntityId = "cve-2026-4401",
        targetEntityId = "framework-mitre",
        relation = RealityRelationTypeV13.MAPS_TO,
        description = "Maps to MITRE ATT&CK T1499 (Endpoint Denial of Service) and T1498 (Network Denial of Service)."
      )
    )
  }

  private fun createInitialNormalizedIntel(): List<NormalizedCyberIntelligenceItemV13> {
    return listOf(
      NormalizedCyberIntelligenceItemV13(
        headline = "CISA Adds Critical Windows Kerberos & Cloud Token Abuse to Known Exploited Vulnerabilities Catalog",
        normalizedCveList = listOf("CVE-2026-4401", "CVE-2025-21298"),
        normalizedTechniques = listOf("T1558.003", "T1078.004", "T1134"),
        rawSourceDescriptions = listOf(
          "CISA Advisory AA26-114A: Active exploitation of Kerberoasting and OAuth token hijacking in hybrid enterprises.",
          "Vendor Security Bulletin MSRC-2026-08: Mandatory AES-256 ticket validation updates."
        ),
        primarySourceType = IntelSourceTypeV13.CISA_ADVISORY,
        sourceName = "CISA Cybersecurity Alerts",
        sourceUrl = "https://www.cisa.gov/news-events/cybersecurity-advisories/aa26-114a",
        publicationDate = "2026-08-28",
        retrievalDate = "2026-08-28 04:00 UTC",
        confidencePercent = 98,
        sha256ContentHash = "sha256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
        status = IntelLiveStatusV13.LIVE_INGESTED,
        affectedTechList = listOf("Active Directory", "Azure AD / Entra ID", "Kerberos KDC", "Windows Server 2022/2025"),
        educationalAbstract = "Adversaries extract Service Principal Name (SPN) tickets to crack passwords offline and hijack cloud session tokens. Teaches the boundary between on-premise AD trust and federated cloud IAM.",
        domainCategory = "Identity"
      ),
      NormalizedCyberIntelligenceItemV13(
        headline = "High-Severity OpenSSL / Edge Proxy Resource Exhaustion Vulnerability Disclosed",
        normalizedCveList = listOf("CVE-2026-4401"),
        normalizedTechniques = listOf("T1499.004", "T1498"),
        rawSourceDescriptions = listOf(
          "OpenSSL Foundation Advisory: Malformed RST_STREAM multiplexing triggers memory leak.",
          "NVD Entry: CVSS 9.8 Remote unauthenticated denial of service."
        ),
        primarySourceType = IntelSourceTypeV13.NIST_NVD,
        sourceName = "NIST National Vulnerability Database",
        sourceUrl = "https://nvd.nist.gov/vuln/detail/CVE-2026-4401",
        publicationDate = "2026-08-27",
        retrievalDate = "2026-08-28 04:00 UTC",
        confidencePercent = 95,
        sha256ContentHash = "sha256:cb2250269d7bb3ff29fcb0f0237e1a3c77e23118933b91faad91a0c4f420cf67",
        status = IntelLiveStatusV13.LIVE_INGESTED,
        affectedTechList = listOf("Envoy", "NGINX", "HAProxy", "Go net/http"),
        educationalAbstract = "Teaches protocol parsing, HTTP/2 state machines, stream limits, and rate-limiting defensive architecture at reverse proxies.",
        domainCategory = "Web"
      )
    )
  }

  private fun createInitialTimelineEvents(): List<CyberTimelineEventV13> {
    return listOf(
      CyberTimelineEventV13(
        timeframe = TimelineTimeframeV13.TODAY,
        title = "CISA Mandates Patching of Active Directory SPN Token Hijack Vulnerabilities",
        categoryDomain = "Identity",
        eventType = "Government Advisory",
        dateFormatted = "August 28, 2026",
        summary = "Federal agencies given 14-day directive to enforce AES-Kerberos encryption and rotate service account passwords.",
        mitreTactics = listOf("Credential Access", "Privilege Escalation")
      ),
      CyberTimelineEventV13(
        timeframe = TimelineTimeframeV13.THIS_WEEK,
        title = "Defensive Sigma Release: Rule Pack v2026.8 targeting Cross-Cloud Token Replay",
        categoryDomain = "Cloud",
        eventType = "Defense Tooling",
        dateFormatted = "August 26, 2026",
        summary = "Detection community releases 42 new rules identifying token replay across AWS and Azure environments.",
        mitreTactics = listOf("Defense Evasion", "Persistence")
      ),
      CyberTimelineEventV13(
        timeframe = TimelineTimeframeV13.THIS_MONTH,
        title = "MITRE ATT&CK Enterprise v15 Published with Enhanced AI Attack Surface Matrix",
        categoryDomain = "AI",
        eventType = "Framework Update",
        dateFormatted = "August 12, 2026",
        summary = "Standardized 18 new techniques covering Prompt Injection, Model Weight Theft, and Vector DB Poisoning.",
        mitreTactics = listOf("Initial Access", "Exfiltration", "Execution")
      ),
      CyberTimelineEventV13(
        timeframe = TimelineTimeframeV13.HISTORICAL,
        title = "The Morris Worm & The Genesis of Computer Security Incident Response Teams",
        categoryDomain = "Network",
        eventType = "Historical Milestone",
        dateFormatted = "November 2, 1988",
        summary = "First major internet worm exploiting fingerd and sendmail; led directly to the founding of CERT/CC at CMU.",
        mitreTactics = listOf("Initial Access", "Propagation")
      )
    )
  }

  private fun createInitialImpactAssessments(): List<PersonalImpactAssessmentV13> {
    return listOf(
      PersonalImpactAssessmentV13(
        intelItemId = "cve-2026-4401",
        intelTitle = "CISA Active Directory & Kerberoasting Advisory",
        relevanceLevel = PersonalRelevanceLevelV13.CRITICAL_MATCH,
        whyItMattersToYou = "You are preparing for a SOC Analyst Tier 2 role. Event ID 4769 ticket investigation is a primary interview and shift triage competency.",
        matchingCareerPath = "SOC Analyst / Threat Detection Engineer",
        matchingSkillName = "Active Directory Event Log Forensics",
        currentCapabilityGap = "High theoretical knowledge (92%) but low practice in parsing RC4 vs AES encryption downgrade logs.",
        recommendedMicroDrillMinutes = 15,
        recommendedActionTitle = "15-Min Drill: Parse Windows Event 4769 & Write Sigma Filter"
      ),
      PersonalImpactAssessmentV13(
        intelItemId = "http2-rapid-reset-v13",
        intelTitle = "HTTP/2 Proxy Resource Exhaustion Vulnerability",
        relevanceLevel = PersonalRelevanceLevelV13.MODERATE_MATCH,
        whyItMattersToYou = "Relevant to your AppSec & Web Security ladder; reinforces reverse-proxy rate limiting principles.",
        matchingCareerPath = "Application Security Specialist",
        matchingSkillName = "Web Architecture & Proxy Hardening",
        currentCapabilityGap = "Need practical verification on Envoy / NGINX rate-limiting config blocks.",
        recommendedMicroDrillMinutes = 10,
        recommendedActionTitle = "10-Min Read & Config Review: Envoy Stream Rate Limits"
      )
    )
  }

  private fun createInitialThreatRadar(): List<PersonalThreatRadarItemV13> {
    return listOf(
      PersonalThreatRadarItemV13(
        title = "Active Directory Kerberoasting & Token Hijack TTPs",
        careerRelevanceScore = 96,
        skillRelevanceScore = 92,
        technologyRelevanceScore = 90,
        learningValueScore = 95,
        riskSignificanceScore = 94,
        aggregateEducationalRank = 1
      ),
      PersonalThreatRadarItemV13(
        title = "Cloud IAM Role Chaining & Cross-Account Egress",
        careerRelevanceScore = 88,
        skillRelevanceScore = 85,
        technologyRelevanceScore = 89,
        learningValueScore = 91,
        riskSignificanceScore = 87,
        aggregateEducationalRank = 2
      ),
      PersonalThreatRadarItemV13(
        title = "LLM Vector Store Exfiltration via Indirect Prompt Injection",
        careerRelevanceScore = 78,
        skillRelevanceScore = 72,
        technologyRelevanceScore = 84,
        learningValueScore = 88,
        riskSignificanceScore = 80,
        aggregateEducationalRank = 3
      )
    )
  }

  private fun createInitialEventMissions(): List<EventMissionV13> {
    return listOf(
      EventMissionV13(
        sourceEventTitle = "CISA Active Directory Advisory",
        durationTier = "15-Min Log Triage",
        durationMinutes = 15,
        missionObjective = "Inspect simulated Windows Security Logs (Event ID 4769) to identify Kerberoasting ticket requests with RC4-HMAC (0x17) encryption.",
        simulatedTelemetrySnippet = "Event 4769: TargetService: MSSQLSvc/sql01.corp:1433 | TicketOptions: 0x40810000 | TicketEncryptionType: 0x17",
        requiredSkillInputs = listOf("Windows Event Logs", "Kerberos RC4 vs AES", "Log Filtering"),
        generatedEvidenceType = "SOC L2 Forensic Log Triage Proof (SHA-256)"
      ),
      EventMissionV13(
        sourceEventTitle = "Cloud IAM Token Replay",
        durationTier = "30-Min Lab",
        durationMinutes = 30,
        missionObjective = "Analyze AWS CloudTrail JSON logs to detect assume-role API calls originating from unapproved ASN/IP regions without MFA context.",
        simulatedTelemetrySnippet = "eventName: AssumeRole | userIdentity: { arn: aws:iam::123:role/SecOps } | sourceIP: 198.51.100.44 | mfaAuthenticated: false",
        requiredSkillInputs = listOf("AWS CloudTrail", "IAM AssumeRole", "Anomaly Triage"),
        generatedEvidenceType = "Cloud Security Incident Debrief Artifact"
      )
    )
  }

  private fun createInitialMarketSkills(): List<MarketSkillDemandV13> {
    return listOf(
      MarketSkillDemandV13(
        skillName = "SIEM & Sigma Rule Authoring",
        trend = SkillMarketTrendV13.GROWING,
        verifiedJobMentionsCount = 890,
        highLeverageCareerCount = 4
      ),
      MarketSkillDemandV13(
        skillName = "Cloud Security Posture (AWS/Azure IAM)",
        trend = SkillMarketTrendV13.GROWING,
        verifiedJobMentionsCount = 950,
        highLeverageCareerCount = 5
      ),
      MarketSkillDemandV13(
        skillName = "Digital Forensics & Memory Analysis (Volatility)",
        trend = SkillMarketTrendV13.STABLE,
        verifiedJobMentionsCount = 620,
        highLeverageCareerCount = 3
      ),
      MarketSkillDemandV13(
        skillName = "AI Guardrails & LLM Red Teaming",
        trend = SkillMarketTrendV13.EMERGING,
        verifiedJobMentionsCount = 340,
        highLeverageCareerCount = 3
      )
    )
  }

  private fun createInitialHighLeverageSkills(): List<HighLeverageSkillCompoundingNodeV13> {
    return listOf(
      HighLeverageSkillCompoundingNodeV13(
        skillName = "Linux Internals & Syscall Tracing",
        unlockedCareers = listOf("SOC Analyst", "Cloud Security", "DFIR", "Pentesting", "DevSecOps"),
        leverageMultiplier = 4.8,
        coreReason = "Essential foundation across container runtimes, endpoint agent behavior, reverse engineering, and cloud OS environments."
      ),
      HighLeverageSkillCompoundingNodeV13(
        skillName = "Network Protocols & Packet Forensics",
        unlockedCareers = listOf("SOC Analyst", "Network Defender", "Threat Hunter", "Red Teamer"),
        leverageMultiplier = 4.2,
        coreReason = "Every distributed attack crosses network boundaries; DNS, TLS, TCP, and HTTP/2 analysis powers high-fidelity attribution."
      ),
      HighLeverageSkillCompoundingNodeV13(
        skillName = "Active Directory & Identity Architecture",
        unlockedCareers = listOf("SOC Analyst", "Incident Responder", "Enterprise Architect", "Penetration Tester"),
        leverageMultiplier = 4.5,
        coreReason = "90% of Fortune 500 enterprise breaches involve credential escalation and lateral movement via hybrid Active Directory."
      )
    )
  }

  private fun createInitialSkillDependencies(): List<SkillPrerequisiteDependencyV13> {
    return listOf(
      SkillPrerequisiteDependencyV13(
        targetSkill = "API Security & OAuth Token Hardening",
        prerequisiteChain = listOf("Networking Fundamentals", "TCP/IP & Ports", "HTTP/HTTPS Protocol", "Web Security Fundamentals (OWASP)", "OAuth 2.0 / OIDC Flow"),
        whySequential = "You cannot evaluate OAuth token hijacking or JWT signing flaws without first mastering HTTP headers, stateless transport, and session semantics."
      ),
      SkillPrerequisiteDependencyV13(
        targetSkill = "Memory Forensics & Rootkit Detection",
        prerequisiteChain = listOf("Computer Architecture", "x86/x64 Registers & Virtual Memory", "Operating System Processes & DLLs", "Volatility Framework"),
        whySequential = "Analyzing memory dumps without understanding page tables and process trees leads to false conclusions on hidden injection techniques."
      )
    )
  }

  private fun createInitialMasteryGatesV13(): List<MasteryTransferGateCheckV13> {
    return listOf(
      MasteryTransferGateCheckV13(
        skillName = "Active Directory Kerberoasting Detection",
        understandsConcept = true,
        recallAccuracy = true,
        labApplicationVerified = true,
        rawInvestigationPassed = true,
        crossContextTransferred = true,
        verbalExplanationClear = true,
        uncertaintyResiliencePassed = true,
        isDemonstratedCapabilityGranted = true,
        verificationEvidenceHash = "sha256:gate_ad_kerb_98124018239012"
      ),
      MasteryTransferGateCheckV13(
        skillName = "DNS Tunneling Exfiltration Hunting",
        understandsConcept = true,
        recallAccuracy = true,
        labApplicationVerified = true,
        rawInvestigationPassed = false,
        crossContextTransferred = false,
        verbalExplanationClear = true,
        uncertaintyResiliencePassed = false,
        isDemonstratedCapabilityGranted = false,
        verificationEvidenceHash = "sha256:gate_dns_hunt_pending_eval"
      )
    )
  }

  private fun createInitialEvidenceQuality(): List<EvidenceQuality20MetricV13> {
    return listOf(
      EvidenceQuality20MetricV13(
        evidenceTitle = "End-to-End SOC Shift Telemetry Incident Report",
        authenticityScore = 95,
        recencyScore = 98,
        difficultyScore = 90,
        independenceScore = 92,
        reproducibilityScore = 94,
        assessmentRigorScore = 96,
        complexityScore = 91,
        transferabilityScore = 89,
        compositeQualityRating = "L5 - Verified Multi-Stage Incident Debrief"
      )
    )
  }

  private fun createInitialLearningExperiments(): List<PersonalLearningExperimentV13> {
    return listOf(
      PersonalLearningExperimentV13(
        experimentName = "Active Retrieval vs. Passive Re-reading (Network Forensics)",
        methodA = LearningMethodTypeV13.RETRIEVAL_PRACTICE,
        methodB = LearningMethodTypeV13.FLASHCARD_RECALL,
        retentionDelta = "+24% 14-day retention under Active Retrieval",
        performanceDelta = "+18% faster identification of beaconing intervals in Wireshark",
        transferDelta = "+14% cross-tool transfer from Wireshark to Zeek logs",
        confidenceCalibrationDelta = "-35% overconfidence gap (well-calibrated)",
        observedWinner = "Active Retrieval Practice demonstrated superior transfer to raw logs."
      )
    )
  }

  private fun createInitialSeasonCampaigns(): List<CyberSeasonCampaignV13> {
    return listOf(
      CyberSeasonCampaignV13(
        seasonId = "season-01",
        seasonNumber = 1,
        title = "SEASON 01: THE FIRST BREACH",
        theme = "Perimeter Compromise & Lateral Movement in a Regional Health Network",
        episodes = listOf(
          CyberSeasonEpisodeV13(1, "The Phished Registrar", "Compromised employee credentials used for VPN ingress", "T1566.002", "Isolate VPN session and enforce FIDO2 MFA", true),
          CyberSeasonEpisodeV13(2, "Living off the Land", "PowerShell encoded scripts spawning from winword.exe", "T1059.001", "Kill parent process tree & deploy EDR block rule", true),
          CyberSeasonEpisodeV13(3, "The Ghost in Domain Controller", "Kerberoasting SPN ticket extraction on finance server", "T1558.003", "Rotate Kerberos KRBTGT account and service passwords", true),
          CyberSeasonEpisodeV13(4, "Exfiltration over DNS", "Encrypted clinical records chunked into DNS TXT queries", "T1071.004", "Block rogue DNS servers at perimeter firewall", true)
        ),
        bossIncidentName = "Boss Incident: Total Network Isolation & Containment War Room",
        isSeasonUnlocked = true
      ),
      CyberSeasonCampaignV13(
        seasonId = "season-02",
        seasonNumber = 2,
        title = "SEASON 02: CLOUD SHADOW",
        theme = "Multi-Cloud Infrastructure Takeover & Serverless Persistence",
        episodes = listOf(
          CyberSeasonEpisodeV13(1, "The Leaked Lambda Key", "Hardcoded AWS secret key committed to public repo", "T1552.001", "Revoke IAM key, audit CloudTrail assume-role events", false),
          CyberSeasonEpisodeV13(2, "Metadata Service Poisoning", "SSRF targeting IMDSv1 to steal instance profile tokens", "T1552.005", "Enforce IMDSv2 and block SSRF vectors", false)
        ),
        bossIncidentName = "Boss Incident: Multi-Region Kubernetes Ransomware Containment",
        isSeasonUnlocked = false
      )
    )
  }

  private fun createInitialIncidentMemory(): List<PersistentIncidentMemoryRecordV13> {
    return listOf(
      PersistentIncidentMemoryRecordV13(
        scenarioTitle = "AegoraBank Payment Gateway Credential Spraying Incident",
        actionsExecuted = listOf("Analyzed WAF 401 logs", "Identified proxy rotation pool", "Created IP reputation blocklist", "Enforced step-up SMS/FIDO2 MFA"),
        successfulDefenses = listOf("Stopped automated credential stuffing attack within 8 minutes", "Zero customer accounts breached"),
        errorsOrMistakesMade = listOf("Initial containment delayed by 2 minutes due to unverified IP address lookup"),
        keyTakeawayLearned = "Always prioritize user identity containment over ephemeral rotating residential IP blockades.",
        generatedEvidenceProof = "Cryptographically Verified Incident Report"
      )
    )
  }

  private fun createInitialSkillConstellation(): List<SkillConstellationNodeV13> {
    return listOf(
      SkillConstellationNodeV13("const-net", "Network Forensics (PCAP)", "Network", ConstellationNodeStateV13.STRONG, 92),
      SkillConstellationNodeV13("const-ad", "Active Directory Security", "Identity", ConstellationNodeStateV13.DEMONSTRATED, 86),
      SkillConstellationNodeV13("const-siem", "SIEM Log Correlation", "Detection", ConstellationNodeStateV13.STRONG, 94),
      SkillConstellationNodeV13("const-cloud", "AWS/Cloud IAM Hardening", "Cloud", ConstellationNodeStateV13.LEARNING, 68),
      SkillConstellationNodeV13("const-mal", "Static Malware Triage", "Malware", ConstellationNodeStateV13.PRACTICING, 74),
      SkillConstellationNodeV13("const-rev", "Binary Reverse Engineering", "Binary", ConstellationNodeStateV13.UNKNOWN, 25),
      SkillConstellationNodeV13("const-mem", "Memory Forensics (Volatility)", "Forensics", ConstellationNodeStateV13.STALE, 60)
    )
  }

  private fun createInitialPrincipleMatrix(): List<SecurityPrincipleMatrixV13> {
    return listOf(
      SecurityPrincipleMatrixV13(
        principleName = "Least Privilege (Separation of Duties)",
        coreInvariant = "A subject should be given only those privileges necessary to complete its specified task.",
        linuxApplication = "chmod/chown restrictive permissions, sudoers fine-grained commands, non-root systemd daemons",
        cloudIamApplication = "ABAC / RBAC with zero wildcard (*) permissions, condition keys with short session expiration",
        containerK8sApplication = "readOnlyRootFilesystem: true, drop ALL Linux capabilities, runAsNonRoot: true",
        databaseApplication = "Dedicated service user with SELECT/INSERT on specific tables only, no DROP/ALTER grants",
        applicationSecApplication = "Principle of least authorization in API endpoints; validating object-level ownership (BOLA defense)"
      ),
      SecurityPrincipleMatrixV13(
        principleName = "Defense in Depth (Layered Defense)",
        coreInvariant = "Multiple defensive controls must be placed along the attack path so failure of one does not cause compromise.",
        linuxApplication = "SELinux/AppArmor + iptables host firewall + centralized auditd log forwarding",
        cloudIamApplication = "MFA + VPC Service Controls + AWS SCP guardrails + GuardDuty anomaly detection",
        containerK8sApplication = "NetworkPolicies + Image Signing (Cosign) + Runtime eBPF monitoring (Falco)",
        databaseApplication = "TLS encryption in transit + AES-256 at rest + database firewall + query audit logging",
        applicationSecApplication = "WAF + Input validation + parameterized queries + CSP headers + rate limiting"
      )
    )
  }

  private fun createInitialToolTransferChallenges(): List<ToolAgnosticTransferChallengeV13> {
    return listOf(
      ToolAgnosticTransferChallengeV13(
        securityConcept = "Beaconing Interval Detection in Proxy Logs",
        familiarToolName = "Wireshark GUI / Display Filters",
        targetUnknownToolName = "Zeek (Bro) conn.log CLI Parser",
        problemPrompt = "You know how to find regular HTTP interval connections in Wireshark. Now analyze 10,000 lines of Zeek conn.log using awk/jq to calculate delta intervals between connections to port 443.",
        evaluationRubric = "Accurately extracts timestamps, computes timestamp differentials, and identifies regular ~60s heartbeat with <2s jitter."
      )
    )
  }

  private fun createInitialConstraintScenarios(): List<RealWorldConstraintScenarioV13> {
    return listOf(
      RealWorldConstraintScenarioV13(
        title = "Critical Hospital EMR Patch vs. Operating Room Downtime",
        budgetConstraint = "$0 emergency budget approved; only internal staff available",
        timeLimitMinutes = 45,
        personnelConstraint = "2 On-Call SecOps Engineers; no vendor support available",
        legacyTechHurdle = "EMR Server runs legacy Windows Server 2012 R2 with custom DLL dependencies",
        businessDowntimeTolerance = "Zero scheduled downtime permitted during surgical shifts (06:00 - 18:00)",
        objective = "Mitigate active wormable SMB vulnerability (CVE-2026-X) without taking EMR database offline.",
        tradeOffsSummary = "Immediate network micro-segmentation and port 445 perimeter block implemented as compensatory control; full binary patch scheduled for midnight maintenance window."
      )
    )
  }

  private fun createInitialEthicalCases(): List<EthicalDecisionCaseV13> {
    return listOf(
      EthicalDecisionCaseV13(
        title = "Responsible Disclosure vs. Active Unpatched Exploitation",
        scenarioContext = "You discovered a zero-day vulnerability in a popular smart medical device library. The vendor has not responded after 60 days, and threat intelligence reports indicate underground forums are beginning to trade exploit PoCs.",
        dilemmaType = "Responsible Disclosure vs Threat Mitigation",
        optionA = "Full Public Disclosure: Publish technical details and signatures immediately so defenders and hospitals can write custom Snort/Suricata rules.",
        optionB = "Coordinated Multi-Stakeholder Escalation: Involve CISA / CERT/CC and healthcare ISACs to coordinate private mitigation without publishing full exploit code.",
        ethicalNuanceExploration = "Option A arms defenders but immediately enables automated script kiddies; Option B protects patients while navigating bureaucratic vendor delays."
      )
    )
  }

  private fun createInitialCultureCases(): List<SecurityCultureSimulatorCaseV13> {
    return listOf(
      SecurityCultureSimulatorCaseV13(
        title = "Engineering Pushback on Mandatory Pre-Commit Secret Scanning",
        stakeholderPushback = "\"This git hook slows down my commits by 4 seconds and blocks urgent production hotfixes!\"",
        stakeholderRole = "Staff Software Architect",
        recommendedInfluenceStrategy = "Empathy & Automation: Move heavy regex scanning to async CI/CD branch checks while optimizing pre-commit hook to execute under 300ms on staged files only.",
        collaborativeResolution = "Engineers agreed to lightweight local hook after security team demonstrated it caught 14 leaked AWS keys in staging."
      )
    )
  }

  private fun createInitialSecondOpinionCases(): List<AiSecondOpinionComparisonV13> {
    return listOf(
      AiSecondOpinionComparisonV13(
        query = "Should our organization immediately disconnect an infected Domain Controller during an active ransomware attack?",
        aiTutorConclusion = "Yes, immediate physical/logical disconnection stops ransomware encryption packets from reaching adjacent subnet nodes.",
        aiResearchAnalystConclusion = "Isolate via switch VLAN / EDR host isolation rather than ungraceful power pull or total cable cut, preserving live volatile RAM artifacts and allowing analysts to track C2 beacon traffic under containment.",
        pointsOfConsensus = listOf("Containment is highest urgency", "Lateral spread must be halted immediately"),
        pointsOfDivergence = listOf("Total link severing destroys volatile forensic memory and ongoing C2 intelligence"),
        criticalThinkingTakeaway = "Defensive decisions must balance containment speed against forensic visibility and business continuity."
      )
    )
  }

  private fun createInitialDebriefReport(): ProfessionalSimulationDebriefReportV13 {
    return ProfessionalSimulationDebriefReportV13(
      scenarioTitle = "Multi-Stage Healthcare Ransomware Containment Scenario",
      learnerScoreOverall = 93,
      containmentDecisionsSummary = "Successfully isolated patient billing subnet within 4 minutes; contained rogue Domain Admin credential usage.",
      forensicEvidenceQualityScore = 96,
      identifiedWeaknesses = listOf("Took 2 extra minutes to correlate external DNS exfiltration queries with internal infected host IP"),
      demonstratedStrengths = listOf("Flawless command of Event 4624/4625 logon types", "Clean containment without breaking hospital telephony services"),
      mistakeAnalysis = "Slight delay in DNS log cross-referencing; remedied by building automated Zeek query filter.",
      actionableRemediationPlan = "Complete the 15-Minute DNS Tunneling Micro-Drill to solidify automated Zeek/Wireshark regex correlations."
    )
  }
}
