package com.example.model

/**
 * AEGORA v9.0 — CYBER REALITY ENGINE DATA MODELS
 *
 * Connects the real-world cybersecurity ecosystem (CVEs, CISA advisories, breaches,
 * threat intelligence, job market signals) directly into the learner's internal Cyber Twin 3.0,
 * Knowledge Graph 3.0, Career Simulation Universe, and Cognitive Assessment Engines.
 *
 * All external data sources are strictly labeled with their provenance and live status.
 * All security simulations contain explicit educational sandbox boundaries.
 */

// ============================================================================
// 1. EXTERNAL INTELLIGENCE SIGNAL & SOURCE TRUST ENGINE
// ============================================================================

enum class ExternalSignalType(val displayName: String, val badgeColor: Long) {
  CVE("CVE Vulnerability", 0xFFFF5252),
  CISA_ADVISORY("CISA Known Exploited", 0xFFFFB800),
  VENDOR_ADVISORY("Vendor Advisory", 0xFF00F0FF),
  MAJOR_BREACH("Major Incident / Breach", 0xFFFF0055),
  MALWARE_CAMPAIGN("Malware Campaign", 0xFFFF3366),
  THREAT_ACTOR_INTEL("Threat Actor TTP", 0xFFBD00FF),
  SUPPLY_CHAIN_INCIDENT("Supply Chain Incident", 0xFFFF9900),
  CLOUD_INCIDENT("Cloud Security Incident", 0xFF00E5FF),
  AI_SECURITY_DEVELOPMENT("AI / LLM Security", 0xFF76FF03),
  SECURITY_RESEARCH("Defensive Research Paper", 0xFF00E676),
  DEFENSIVE_TECHNIQUE("New Detection Method", 0xFF00B0FF),
  CONFERENCE_PAPER("BlackHat / DEFCON Intel", 0xFFE040FB),
  CERT_UPDATE("Certification Evolution", 0xFFFFAB00),
  CTF_COMPETITION("Live CTF Challenge", 0xFF69F0AE),
  JOB_MARKET_SIGNAL("Career Market Shift", 0xFFFFD700)
}

enum class DataSourceTrust(val label: String, val credibilityScore: Int, val badgeColor: Long) {
  PRIMARY("Primary Authority (CISA / NIST / RFC / Vendor)", 98, 0xFF00E676),
  HIGH_AUTHORITY("High Authority (MITRE / CERT-CC / Mandiant / CrowdStrike)", 92, 0xFF00F0FF),
  SECONDARY("Secondary Reporting (SecurityWeek / BleepingComputer)", 78, 0xFFFFB800),
  COMMUNITY("Community / GitHub / Social Intel", 65, 0xFFFF9900),
  UNVERIFIED("Unverified / Rumor / Speculation", 40, 0xFFFF5252)
}

enum class DataLiveStatus(val label: String, val badgeColor: Long) {
  LIVE("LIVE FEED", 0xFF00E676),
  SIMULATED("SIMULATED SCENARIO", 0xFF00F0FF),
  CACHED("CACHED SNAPSHOT", 0xFFFFB800),
  AI_GENERATED("AI-SYNTHESIZED", 0xFFBD00FF),
  DEMO("DEMO FIXTURE", 0xFF64748B)
}

data class ExternalIntelligenceCard(
  val id: String,
  val title: String,
  val publicationDate: String,
  val sourceName: String,
  val sourceUrl: String,
  val sourceTrust: DataSourceTrust,
  val liveStatus: DataLiveStatus,
  val confidenceRating: Int, // 0-100%
  val summary: String,
  val whyItMatters: String,
  val affectedTechnology: List<String>,
  val mitreTechniques: List<String>,
  val relatedSkills: List<String>,
  val relatedCareers: List<String>,
  val learningOpportunityLessonId: String? = null,
  val rawTelemetrySample: String? = null
)

// ============================================================================
// 2. CYBER EVENT → PERSONAL LESSON TRANSFORMER
// ============================================================================

data class AttackChainStep(
  val stepNumber: Int,
  val phaseName: String,
  val techniqueId: String,
  val description: String,
  val defensiveTelemetryIndicator: String
)

data class SafePracticeScenario(
  val scenarioId: String,
  val title: String,
  val labRoute: String,
  val environmentDescription: String,
  val primaryObjectives: List<String>,
  val safetyNotice: String = "AUTHORIZED SANDBOX ENVIRONMENT — Fictional telemetry for educational simulation only."
)

data class EventToLessonTransformation(
  val id: String,
  val externalCardId: String,
  val eventTitle: String,
  val whatHappenedSummary: String,
  val rootCauseAnalysis: String,
  val attackChainSteps: List<AttackChainStep>,
  val defensiveLesson: String,
  val learnerSkillGapDetected: String,
  val safePracticeScenario: SafePracticeScenario,
  val assessmentCriteria: List<String>,
  val expectedTwinDelta: String
)

// ============================================================================
// 3. PERSONAL CYBER RADAR (Prioritized Feed)
// ============================================================================

enum class RadarPriorityTag(val label: String, val badgeColor: Long) {
  CRITICAL_CAREER_ALIGNMENT("Target Career Core Requirement", 0xFFFF0055),
  DECAY_INTERVENTION("Ebbinghaus Skill Decay Trigger", 0xFFFFB800),
  MISTAKE_DNA_TRIGGER("Mistake Pattern Detected", 0xFFFF5252),
  PORTFOLIO_OPPORTUNITY("High Impact CV Project", 0xFF00E676),
  GENERAL_INTEREST("Emerging Threat Trend", 0xFF00F0FF)
}

data class PersonalRadarItem(
  val intelCard: ExternalIntelligenceCard,
  val personalRelevanceScore: Int, // 0-100%
  val relevanceRationale: String,
  val priorityTag: RadarPriorityTag,
  val recommendedAction: String,
  val targetNavigationRoute: String
)

// ============================================================================
// 4. KNOWLEDGE GRAPH 3.0 & CYBER CONSTELLATIONS
// ============================================================================

enum class KnowledgeNodeType30(val label: String, val badgeColor: Long) {
  CONCEPT("Concept", 0xFF00F0FF),
  TOOL("Tool", 0xFF00E676),
  PROTOCOL("Protocol", 0xFF76FF03),
  VULNERABILITY("Vulnerability", 0xFFFF5252),
  CVE("CVE", 0xFFFF0055),
  THREAT_ACTOR("Threat Actor", 0xFFBD00FF),
  MALWARE("Malware", 0xFFFF3366),
  TECHNIQUE("MITRE Technique", 0xFFFF9900),
  DETECTION("Detection Rule", 0xFF00E5FF),
  FRAMEWORK("Framework", 0xFFFFD700),
  CAREER("Career Role", 0xFFFFAB00),
  CERTIFICATION("Certification", 0xFFE040FB),
  LAB("Hands-on Lab", 0xFF69F0AE),
  PROJECT("Portfolio Project", 0xFF00B0FF),
  INCIDENT("Case Study", 0xFFFF6E40),
  COMPANY("Company Archetype", 0xFF64FFDA),
  RESEARCH_PAPER("Research Paper", 0xFFB388FF)
}

enum class KnowledgeEdgeType30(val label: String) {
  REQUIRES("Requires Prerequisite"),
  RELATED_TO("Related To"),
  DETECTED_BY("Detected By"),
  EXPLOITED_BY("Exploited By"),
  USED_IN("Used In"),
  TEACHES("Teaches"),
  PRACTICED_BY("Practiced By"),
  RELEVANT_TO("Relevant To Career"),
  PRECEDES("Precedes In Attack Chain"),
  CONTRADICTS("Contradicts Alternative")
}

data class KnowledgeNode30(
  val id: String,
  val name: String,
  val type: KnowledgeNodeType30,
  val tierLevel: Int, // 1 (Foundational) to 5 (Mastery)
  val domain: String,
  val masteryLevel: Int, // 0-100%
  val evidenceProofCount: Int,
  val careerRelevance: Int, // 0-100%
  val decayRisk: String, // "LOW", "MODERATE", "CRITICAL"
  val prerequisites: List<String>,
  val orbitRadius: Float, // For constellation visualization
  val orbitAngle: Float,
  val description: String
)

data class KnowledgeEdge30(
  val sourceId: String,
  val targetId: String,
  val edgeType: KnowledgeEdgeType30,
  val strengthWeight: Float, // 0.1 to 1.0
  val directionalRationale: String
)

data class ConstellationCluster(
  val domainName: String,
  val centerCoordinates: Pair<Float, Float>,
  val nodeIds: List<String>,
  val clusterColor: Long
)

// ============================================================================
// 5. CAREER MARKET REALITY & JOB SIMULATION GENERATOR
// ============================================================================

data class JobExtractedSkillItem(
  val skillName: String,
  val category: String, // "TOOL", "FRAMEWORK", "CORE_SKILL", "SOFT_SKILL"
  val isMandatory: Boolean,
  val learnerMasteryPercent: Int,
  val verifiedEvidenceProofId: String? = null
)

data class CustomMultiToolLab(
  val labId: String,
  val title: String,
  val targetToolsIntegrated: List<String>,
  val simulatedScenario: String,
  val stepByStepTasks: List<String>,
  val expectedArtifactProof: String,
  val estimatedMinutes: Int
)

data class JobToTrainingSimulationResult(
  val jobTitle: String,
  val targetCompany: String,
  val overallMatchPercentage: Int,
  val extractedSkills: List<JobExtractedSkillItem>,
  val missingSkills: List<String>,
  val customLab: CustomMultiToolLab,
  val interviewQuestions: List<String>,
  val recommendedPortfolioProject: String,
  val shortestPreparationRoadmap: List<String>
)

// ============================================================================
// 6. CAREER SIMULATION UNIVERSE (8 COMPANY ARCHETYPES)
// ============================================================================

enum class SimulatedCompanyEnvironment(
  val displayName: String,
  val industrySector: String,
  val primaryRiskProfile: String,
  val complianceFramework: String,
  val badgeColor: Long
) {
  FINANCIAL_SERVICES("Apex Global Banking", "FinTech / Banking", "Wire Fraud, Credential Stuffing, Ransomware", "PCI-DSS 4.0, SOX, GLBA", 0xFF00F0FF),
  CLOUD_SAAS("OmniCloud Enterprise SaaS", "Multi-Tenant Cloud", "IAM Privilege Escalation, API Abuse, Multi-Tenancy Breach", "SOC 2 Type II, ISO 27001", 0xFF00E676),
  HEALTHCARE_SYSTEM("St. Jude Memorial Health", "Healthcare & MedTech", "Medical IoT Ransomware, EHR Exfiltration, Life Safety", "HIPAA, HITECH, NIST CSF", 0xFFFF5252),
  UNIVERSITY_CAMPUS("Aegora State University", "Higher Education & Research", "Decentralized BYOD, Phishing, IP Espionage", "FERPA, NIST 800-171", 0xFFFFB800),
  GOVERNMENT_AGENCY("Federal Cyber Infrastructure Agency", "Public Sector / Critical Infra", "Nation-State APTs, Supply Chain, Zero-Days", "FedRAMP High, NIST 800-53", 0xFFBD00FF),
  INDUSTRIAL_MANUFACTURING("Titan Heavy Industries", "OT / ICS / SCADA", "Modbus Manipulation, Safety Instrumented System Bypass", "IEC 62443, NIST SP 800-82", 0xFFFF9900),
  ECOMMERCE_PLATFORM("Zephyr Marketplace", "Retail & High-Concurrency Web", "Magecart Web Skimming, DDoS, Account Takeover", "PCI-DSS, CCPA / GDPR", 0xFF00E5FF),
  TECH_STARTUP("NovaStream AI Labs", "AI / Tech Startup", "Prompt Injection, Shadow SaaS, Supply Chain Typosquatting", "CIS Critical Security Controls", 0xFF76FF03)
}

data class CareerTransitionPath(
  val currentRole: String,
  val targetRole: String,
  val transferableSkills: List<String>,
  val skillGaps: List<String>,
  val estimatedWeeksToTransition: Int,
  val recommendedExperimentLab: String,
  val feasibilityScore: Int // 0-100%
)

// ============================================================================
// 7. CYBER WORKPLACE SIMULATOR 2.0 & CONSEQUENCE ENGINE
// ============================================================================

enum class WorkplaceChannel(val displayName: String, val badgeColor: Long) {
  EMAIL("Corporate Email", 0xFF00F0FF),
  TICKET("Jira / ServiceNow Ticket", 0xFFFFB800),
  SIEM_ALERT("SIEM Alert Queue", 0xFFFF0055),
  SLACK_MSG("Internal Slack / Teams", 0xFF00E676),
  INCIDENT_NOTICE("P1 Crisis Page", 0xFFFF3366),
  MANAGER_REQUEST("Manager Request", 0xFFBD00FF),
  CUSTOMER_INQUIRY("Client Security Inquiry", 0xFFFF9900),
  SECURITY_FINDING("Vulnerability Finding", 0xFF76FF03)
}

data class WorkplaceFeedItem(
  val id: String,
  val channel: WorkplaceChannel,
  val sender: String,
  val subject: String,
  val body: String,
  val timestamp: String,
  val isFalsePositive: Boolean,
  val ambiguityLevel: String, // "LOW", "MEDIUM", "HIGH"
  val requiredActionType: String,
  val isAddressed: Boolean = false
)

data class ConsequenceOutcome(
  val businessImpactDescription: String,
  val attackerMovementDelta: String,
  val detectionSpeedScore: Int,
  val operationalCostImpact: String,
  val isOptimalDecision: Boolean,
  val educationalDebrief: String
)

data class MultiverseTimelineNode(
  val timelineName: String, // "Your Timeline", "Premature Containment", "Optimal Best-Practice"
  val outcomeSummary: String,
  val businessDowntimeHours: Double,
  val dataExfiltratedMB: Double,
  val totalIncidentCostUSD: String
)

// ============================================================================
// 8. ADVANCED COGNITIVE DIAGNOSTIC ARENAS
// ============================================================================

enum class CalibrationDiagnosis(val displayName: String, val badgeColor: Long) {
  CALIBRATED("Well-Calibrated (Confidence Matches Skill)", 0xFF00E676),
  OVERCONFIDENT("Overconfident (High Confidence, Errors Made)", 0xFFFF5252),
  UNDERCONFIDENT("Underconfident (Hesitant, High Performance)", 0xFFFFB800),
  HESITANT("Hesitant Under Time Pressure", 0xFFFF9900)
}

data class ConfidenceCalibrationRecord(
  val challengeId: String,
  val challengeTitle: String,
  val statedConfidencePercent: Int,
  val actualScorePercent: Int,
  val deltaVariance: Int,
  val diagnosis: CalibrationDiagnosis,
  val recommendedIntervention: String
)

data class SkillTransferTest(
  val testId: String,
  val sourceConceptTitle: String, // e.g. "Windows Event ID 4688 Process Creation"
  val novelDomainTitle: String, // e.g. "Kubernetes Pod Exec Audit Logs"
  val transferScenarioPrompt: String,
  val analogousReasoningKey: String,
  val learnerResponse: String? = null,
  val evaluationScore: Int? = null,
  val debriefInsight: String
)

data class UnknownProblemLab(
  val labId: String,
  val title: String,
  val rawTelemetrySnippet: String,
  val hiddenRootCause: String,
  val hintProvidedCount: Int,
  val problemDecompositionQualityScore: Int,
  val evidenceCollectionMethodology: String,
  val debriefSummary: String
)

enum class FeynmanAudience(val label: String, val personaDescription: String) {
  BEGINNER("Beginner / High Schooler", "No technical jargon, use everyday metaphors (doors, mailmen)."),
  TECHNICAL_ENGINEER("Software Engineer", "Focus on APIs, stack traces, TCP packets, and memory layout."),
  SOC_ANALYST("Peer SOC Analyst", "Focus on Sysmon event IDs, Sigma rule logic, PCAP offsets."),
  CISO("Chief Information Security Officer", "Focus on dollar risk, business continuity, regulatory compliance.")
}

data class CyberFeynmanAssessment(
  val conceptTitle: String,
  val selectedAudience: FeynmanAudience,
  val learnerExplanation: String,
  val accuracyScore: Int,
  val clarityScore: Int,
  val jargonScore: Int,
  val adaptationScore: Int,
  val overallScore: Int,
  val aiEvaluationDebrief: String
)

data class ExecutiveIncidentBoardroom(
  val incidentTitle: String,
  val attendeePersonas: List<String>, // "CISO", "CFO", "CEO", "General Counsel"
  val situationReportText: String,
  val businessImpactStatement: String,
  val containmentRecommendation: String,
  val boardApprovalVerdict: String, // "UNANIMOUS_APPROVAL", "CONDITIONAL_APPROVAL", "REJECTED_DUE_TO_JARGON"
  val executiveCommunicationScore: Int,
  val critiquePoints: List<String>
)

// ============================================================================
// 9. PORTFOLIO EVIDENCE & PROJECT GENERATOR
// ============================================================================

data class V9PortfolioProject(
  val id: String,
  val title: String,
  val problemStatement: String,
  val targetRole: String,
  val architectureDescription: String,
  val threatModelFramework: String,
  val technologies: List<String>,
  val stepByStepImplementationGuide: List<String>,
  val verificationAndTestProof: String,
  val githubRepoStructure: String,
  val resumeBulletPoint: String,
  val starInterviewStory: String, // Situation, Task, Action, Result
  val demonstratedCompetenciesCount: Int
)

// ============================================================================
// 10. FAIR COMPETITION & KNOWLEDGE VAULT
// ============================================================================

enum class SkillBandTier(val tierName: String, val ratingRange: String, val badgeColor: Long) {
  TIER_1_NOVICE("Novice Cadre", "0 - 1,200 MMR", 0xFF64748B),
  TIER_2_ADEPT("Adept Practitioner", "1,201 - 1,800 MMR", 0xFF00F0FF),
  TIER_3_SPECIALIST("Senior Specialist", "1,801 - 2,400 MMR", 0xFFFFB800),
  TIER_4_ELITE("Elite Sentinel", "2,401+ MMR", 0xFFBD00FF)
}

data class CompetitionLeaderboardEntry(
  val rank: Int,
  val callsign: String,
  val overallRating: Int,
  val investigationScore: Int,
  val defenseScore: Int,
  val reasoningScore: Int,
  val communicationScore: Int,
  val learningGrowthScore: Int,
  val skillBand: SkillBandTier,
  val isCurrentLearner: Boolean = false
)

data class PersonalKnowledgeVaultNote(
  val id: String,
  val title: String,
  val category: String, // "CONCEPT", "COMMAND_CHEAT", "INCIDENT_TAKEAWAY", "CVE_ANALYSIS"
  val content: String,
  val connectedKnowledgeNodeIds: List<String>,
  val cveReferences: List<String>,
  val commandSnippets: List<String>,
  val tags: List<String>,
  val createdAt: String
)

// ============================================================================
// 11. AEGORA TRUTH LAYER (Certainty Attribution)
// ============================================================================

enum class CertaintyState(val label: String, val badgeColor: Long) {
  KNOWN("KNOWN (Directly verified by telemetry/RFC/evidence)", 0xFF00E676),
  INFERRED("INFERRED (Derived from heuristic pattern matching)", 0xFF00F0FF),
  ESTIMATED("ESTIMATED (Probabilistic calibration score)", 0xFFFFB800),
  GENERATED("GENERATED (AI synthesized with sandbox validation)", 0xFFBD00FF),
  UNKNOWN("UNKNOWN (Insufficient evidence, explicitly acknowledged)", 0xFFFF5252)
}

data class TruthAttributedAnswer(
  val claimText: String,
  val certainty: CertaintyState,
  val supportingEvidenceId: String? = null,
  val epistemicNote: String? = null
)
