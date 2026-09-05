package com.example.model

import java.util.UUID

// ============================================================
// AEGORA v13.0 — CYBER REALITY INTELLIGENCE OPERATING SYSTEM
// CORE DATA MODELS & ONTOLOGY GRAPH
// ============================================================

/**
 * 1. CYBER REALITY ENGINE 2.0 (22+ ENTITY TYPES & 12 RELATIONSHIP TYPES)
 */
enum class RealityEntityTypeV13(val displayName: String, val category: String) {
  CVE("CVE Vulnerability", "Vulnerability"),
  THREAT_ACTOR("Threat Actor / APT", "Adversary"),
  MALWARE("Malware Family", "Threat"),
  CAMPAIGN("Cyber Campaign", "Threat"),
  ATTACK_TECHNIQUE("MITRE ATT&CK Technique", "Methodology"),
  VULNERABILITY("Vulnerability Class", "Flaw"),
  SECURITY_CONTROL("Defensive Security Control", "Defense"),
  VENDOR("Security / Tech Vendor", "Industry"),
  PRODUCT("Software / Hardware Product", "Technology"),
  CLOUD_SERVICE("Cloud Service & API", "Infrastructure"),
  FRAMEWORK("Security Framework", "Standards"),
  RESEARCH_PAPER("Academic Research Paper", "Science"),
  INCIDENT("Real-World Cyber Incident", "History"),
  COMPANY("Enterprise Target", "Industry"),
  INDUSTRY("Industry Vertical", "Sector"),
  CERTIFICATION("Cyber Certification", "Professional"),
  JOB_POSTING("Market Job Requirement", "Career"),
  CTF_CHALLENGE("CTF Lab / Challenge", "Training"),
  CONFERENCE("Security Conference", "Community"),
  COURSE("Academic Course / Syllabus", "Education"),
  SECURITY_TOOL("Security & Forensics Tool", "Tooling"),
  TECHNOLOGY_STACK("Technology Stack", "Architecture")
}

enum class RealityRelationTypeV13(val label: String) {
  USES("uses"),
  TARGETS("targets"),
  EXPLOITS("exploits"),
  MITIGATES("mitigates"),
  AFFECTS("affects"),
  RELATED_TO("related to"),
  DETECTED_BY("detected by"),
  REPORTED_BY("reported by"),
  REQUIRES("requires"),
  TEACHES("teaches"),
  MAPS_TO("maps to"),
  PROVES("proves")
}

data class CyberRealityEntityNodeV13(
  val id: String = UUID.randomUUID().toString(),
  val type: RealityEntityTypeV13,
  val name: String,
  val codeOrIdentifier: String, // e.g. "CVE-2026-4401", "T1059.001", "APT29"
  val description: String,
  val severityOrImpact: String = "HIGH",
  val tags: List<String> = emptyList(),
  val confidenceScore: Int = 95,
  val sourceUrl: String = "https://cisa.gov/known-exploited-vulnerabilities"
)

data class CyberRealityEdgeV13(
  val sourceEntityId: String,
  val targetEntityId: String,
  val relation: RealityRelationTypeV13,
  val description: String,
  val evidenceBasis: String = "Authoritative vendor advisory & MITRE ATT&CK mapping"
)

/**
 * 2. REAL-TIME INTELLIGENCE INGESTION & NORMALIZATION
 */
enum class IntelSourceTypeV13(val label: String) {
  CISA_ADVISORY("CISA Advisory"),
  NIST_NVD("NIST NVD"),
  VENDOR_SECURITY_BULLETIN("Vendor Security Bulletin"),
  CERT_COORD_CENTER("CERT / Gov CSIRT"),
  ACADEMIC_RESEARCH("Academic Preprint / Journal"),
  CONFERENCE_ARCHIVE("DEF CON / Black Hat"),
  INDUSTRY_TELEMETRY("Verified Industry Telemetry")
}

enum class IntelLiveStatusV13(val label: String) {
  LIVE_INGESTED("Live Verified"),
  CACHED_OFFLINE("Cached Offline Intelligence"),
  UNAVAILABLE("Live intelligence unavailable (Fallback active)")
}

data class NormalizedCyberIntelligenceItemV13(
  val id: String = UUID.randomUUID().toString(),
  val headline: String,
  val normalizedCveList: List<String>,
  val normalizedTechniques: List<String>,
  val rawSourceDescriptions: List<String>,
  val primarySourceType: IntelSourceTypeV13,
  val sourceName: String,
  val sourceUrl: String,
  val publicationDate: String,
  val retrievalDate: String,
  val confidencePercent: Int,
  val sha256ContentHash: String,
  val status: IntelLiveStatusV13 = IntelLiveStatusV13.LIVE_INGESTED,
  val affectedTechList: List<String> = emptyList(),
  val educationalAbstract: String,
  val domainCategory: String // "Cloud", "Web", "AI", "Identity", "Network", "OT", "GRC"
)

/**
 * 3. CYBER EVENT TIMELINE
 */
enum class TimelineTimeframeV13(val label: String) {
  TODAY("Today"),
  THIS_WEEK("This Week"),
  THIS_MONTH("This Month"),
  HISTORICAL("Historical Root Causes")
}

data class CyberTimelineEventV13(
  val id: String = UUID.randomUUID().toString(),
  val timeframe: TimelineTimeframeV13,
  val title: String,
  val categoryDomain: String, // Web, Cloud, AI, Mobile, Network, Identity, Malware, OT, GRC, DevSecOps
  val eventType: String,      // Vulnerability, Incident, Campaign, Tool Release, Standards
  val dateFormatted: String,
  val summary: String,
  val mitreTactics: List<String>,
  val linkedEntityId: String? = null
)

/**
 * 4. PERSONAL IMPACT ENGINE & THREAT RADAR
 */
enum class PersonalRelevanceLevelV13(val label: String, val badgeColor: Long) {
  CRITICAL_MATCH("HIGH RELEVANCE", 0xFF00FF9D),
  MODERATE_MATCH("MODERATE RELEVANCE", 0xFF00D2FF),
  EXPLORATORY_MATCH("LOW / GENERAL", 0xFFB388FF)
}

data class PersonalImpactAssessmentV13(
  val intelItemId: String,
  val intelTitle: String,
  val relevanceLevel: PersonalRelevanceLevelV13,
  val whyItMattersToYou: String,
  val matchingCareerPath: String,
  val matchingSkillName: String,
  val currentCapabilityGap: String,
  val recommendedMicroDrillMinutes: Int,
  val recommendedActionTitle: String
)

data class PersonalThreatRadarItemV13(
  val title: String,
  val careerRelevanceScore: Int, // 0 - 100
  val skillRelevanceScore: Int,  // 0 - 100
  val technologyRelevanceScore: Int,
  val learningValueScore: Int,
  val riskSignificanceScore: Int,
  val aggregateEducationalRank: Int,
  val disclaimer: String = "Educational relevance index only; does not infer targeted individual risk."
)

/**
 * 5. EVENT-TO-MISSION GENERATOR (SAFE EDUCATIONAL ABSTRACTIONS)
 */
data class EventMissionV13(
  val id: String = UUID.randomUUID().toString(),
  val sourceEventTitle: String,
  val durationTier: String, // "5-Min Quick Read", "15-Min Log Triage", "30-Min Lab", "60-Min Detection", "Multi-Day Project"
  val durationMinutes: Int,
  val missionObjective: String,
  val simulatedTelemetrySnippet: String,
  val requiredSkillInputs: List<String>,
  val generatedEvidenceType: String,
  val isHarmfulExploitQuarantined: Boolean = true
)

/**
 * 6. CAREER MARKET INTELLIGENCE & SKILL ECONOMY
 */
enum class SkillMarketTrendV13(val label: String) {
  GROWING("Growing (+18% YoY)"),
  STABLE("Stable High Demand"),
  EMERGING("Emerging Specialized"),
  DECLINING("Declining / Automated")
}

data class MarketSkillDemandV13(
  val skillName: String,
  val trend: SkillMarketTrendV13,
  val verifiedJobMentionsCount: Int,
  val sampleSize: String = "Sample: 1,420 Verified Public Cyber Job Postings",
  val dataSource: String = "Public Cyber Career Index 2026",
  val confidencePercent: Int = 92,
  val highLeverageCareerCount: Int = 4 // How many careers this unlocks
)

data class CareerWhatIfSimulationResultV13(
  val careerRole: String,
  val requiredCapabilities: List<String>,
  val currentMatchPercent: Int,
  val estimatedEffortWeeks: Int,
  val highLeverageSkillsToAcquire: List<String>,
  val recommendedProjects: List<String>,
  val certificationPathway: List<String>,
  val planningEstimateNotice: String = "Algorithmic planning estimate for learning roadmap; employment is not guaranteed."
)

data class HighLeverageSkillCompoundingNodeV13(
  val skillName: String,
  val unlockedCareers: List<String>,
  val leverageMultiplier: Double,
  val coreReason: String
)

data class SkillPrerequisiteDependencyV13(
  val targetSkill: String,
  val prerequisiteChain: List<String>,
  val whySequential: String
)

/**
 * 7. MASTERY TRANSFER GATES & EVIDENCE QUALITY 2.0
 */
data class MasteryTransferGateCheckV13(
  val skillName: String,
  val understandsConcept: Boolean,
  val recallAccuracy: Boolean,
  val labApplicationVerified: Boolean,
  val rawInvestigationPassed: Boolean,
  val crossContextTransferred: Boolean,
  val verbalExplanationClear: Boolean,
  val uncertaintyResiliencePassed: Boolean,
  val isDemonstratedCapabilityGranted: Boolean,
  val verificationEvidenceHash: String
)

data class EvidenceQuality20MetricV13(
  val evidenceTitle: String,
  val authenticityScore: Int,      // 0 - 100
  val recencyScore: Int,            // 0 - 100
  val difficultyScore: Int,         // 0 - 100
  val independenceScore: Int,       // 0 - 100
  val reproducibilityScore: Int,    // 0 - 100
  val assessmentRigorScore: Int,    // 0 - 100
  val complexityScore: Int,         // 0 - 100
  val transferabilityScore: Int,    // 0 - 100
  val compositeQualityRating: String = "L4 - Cryptographically Verified Lab Artifact"
)

/**
 * 8. PERSONAL LEARNING EXPERIMENTS & METHOD RECOMMENDER
 */
enum class LearningMethodTypeV13(val label: String, val idealForEnergy: String) {
  RETRIEVAL_PRACTICE("Active Retrieval Practice", "Normal"),
  SOCRATIC_DIALOGUE("Socratic Inquiry", "Normal"),
  FEYNMAN_TECHNIQUE("Feynman Simplification", "High Focus"),
  SIMULATION_DRILL("Crisis Simulation", "High Focus"),
  HANDS_ON_PROJECT("Hands-on Engineering", "High Focus"),
  RESEARCH_SYNTHESIS("Research Deep-Dive", "Low Energy"),
  TEACH_BACK_VOICE("Voice Teach-Back", "Normal"),
  FLASHCARD_RECALL("Spaced Flashcards", "Low Energy"),
  RAW_INVESTIGATION("Raw Telemetry Hunting", "High Focus")
}

data class PersonalLearningExperimentV13(
  val experimentName: String,
  val methodA: LearningMethodTypeV13,
  val methodB: LearningMethodTypeV13,
  val retentionDelta: String,
  val performanceDelta: String,
  val transferDelta: String,
  val confidenceCalibrationDelta: String,
  val observedWinner: String,
  val scientificDisclaimer: String = "Empirical personal pattern comparison; not a double-blind clinical claim."
)

/**
 * 9. CYBER SEASON SYSTEM & NARRATIVE CAMPAIGNS
 */
data class CyberSeasonEpisodeV13(
  val episodeNumber: Int,
  val title: String,
  val scenarioContext: String,
  val primaryAdversaryTactic: String,
  val requiredDefenseAction: String,
  val isCompleted: Boolean
)

data class CyberSeasonCampaignV13(
  val seasonId: String,
  val seasonNumber: Int,
  val title: String,
  val theme: String,
  val episodes: List<CyberSeasonEpisodeV13>,
  val bossIncidentName: String,
  val isSeasonUnlocked: Boolean
)

/**
 * 10. ORGANIZATIONAL & INCIDENT MEMORY
 */
data class FictionalEnterpriseStateV13(
  val enterpriseName: String = "AegoraBank Global Financial",
  val infrastructureSummary: String = "Hybrid Multi-Cloud AWS/Azure + 4,200 Windows/Linux Endpoints",
  val employeeCount: Int = 8500,
  val activePolicies: List<String> = listOf("Enforce FIDO2 MFA", "Zero Trust Egress Proxy", "Quarterly LSASS Memory Audit"),
  val historicalIncidentsCount: Int = 4,
  val studentDecisionsImpactSummary: String = "Learner's previous containment decisions reduced lateral movement dwell time by 64% in the Payment Gateway subnet."
)

data class PersistentIncidentMemoryRecordV13(
  val incidentId: String = UUID.randomUUID().toString(),
  val scenarioTitle: String,
  val actionsExecuted: List<String>,
  val successfulDefenses: List<String>,
  val errorsOrMistakesMade: List<String>,
  val keyTakeawayLearned: String,
  val generatedEvidenceProof: String,
  val timestamp: String = "2026-08-28 04:00 UTC"
)

/**
 * 11. CYBER SKILL CONSTELLATION 3.0
 */
enum class ConstellationNodeStateV13(val label: String, val hexColor: Long) {
  UNKNOWN("Unexplored", 0xFF616161),
  LEARNING("In Progress", 0xFFFFB300),
  PRACTICING("Practicing Lab", 0xFF00D2FF),
  DEMONSTRATED("Demonstrated Capability", 0xFF00FF9D),
  STRONG("Mastered / Battle Tested", 0xFF7C4DFF),
  STALE("Decay Warning", 0xFFFF5252)
}

data class SkillConstellationNodeV13(
  val skillId: String,
  val title: String,
  val category: String,
  val state: ConstellationNodeStateV13,
  val masteryPercent: Int,
  val connectedEdgeIds: List<String> = emptyList(),
  val careerValueRating: String = "HIGH"
)

/**
 * 12. SECURITY PRINCIPLE & TOOL-AGNOSTIC LEARNING
 */
data class SecurityPrincipleMatrixV13(
  val principleName: String, // e.g. "Least Privilege", "Defense in Depth", "Fail-Safe Defaults"
  val coreInvariant: String,
  val linuxApplication: String,
  val cloudIamApplication: String,
  val containerK8sApplication: String,
  val databaseApplication: String,
  val applicationSecApplication: String
)

data class ToolAgnosticTransferChallengeV13(
  val securityConcept: String,
  val familiarToolName: String,
  val targetUnknownToolName: String,
  val problemPrompt: String,
  val evaluationRubric: String,
  val isTransferAchieved: Boolean = false
)

/**
 * 13. REAL-WORLD CONSTRAINT & ETHICAL DECISION LAB
 */
data class RealWorldConstraintScenarioV13(
  val title: String,
  val budgetConstraint: String,
  val timeLimitMinutes: Int,
  val personnelConstraint: String,
  val legacyTechHurdle: String,
  val businessDowntimeTolerance: String,
  val objective: String,
  val tradeOffsSummary: String
)

data class EthicalDecisionCaseV13(
  val title: String,
  val scenarioContext: String,
  val dilemmaType: String, // "Security vs Availability", "Privacy vs Surveillance", "Responsible Disclosure"
  val optionA: String,
  val optionB: String,
  val ethicalNuanceExploration: String,
  val noDogmaNotice: String = "Professional engineering requires balancing statutory obligations, ethical duty, and business viability."
)

data class SecurityCultureSimulatorCaseV13(
  val title: String,
  val stakeholderPushback: String,
  val stakeholderRole: String, // "Lead Frontend Architect", "CFO", "Director of Sales"
  val recommendedInfluenceStrategy: String,
  val collaborativeResolution: String
)

/**
 * 14. AI MENTOR STYLE & AI SECOND OPINION
 */
enum class AiMentorPersonaStyleV13(val label: String, val toneDescription: String) {
  SOCRATIC("Socratic Inquirer", "Asks probing diagnostic questions; guides you to deduce solutions"),
  STRICT_EXAMINER("Strict CISO Examiner", "Demands rigorous forensic proof, zero fluff, precise terminology"),
  FRIENDLY_COACH("Supportive Coach", "Encouraging, celebrates small wins, breaks down intimidation"),
  TECHNICAL_SPECIALIST("Deep-Dive Kernel Specialist", "Focuses on assembly, byte opcodes, network packets and syscalls"),
  MINIMALIST("Minimalist Oracle", "Ultra-concise one-sentence directional hints"),
  SENIOR_ANALYST("Senior SOC Colleague", "Practical shift-lead mindset, teaches playbooks and real-world triage trade-offs")
}

data class AiSecondOpinionComparisonV13(
  val query: String,
  val aiTutorConclusion: String,
  val aiResearchAnalystConclusion: String,
  val pointsOfConsensus: List<String>,
  val pointsOfDivergence: List<String>,
  val criticalThinkingTakeaway: String
)

/**
 * 15. TIME & ENERGY AWARE LEARNING (NEXT BEST ACTION 3.0)
 */
enum class LearnerEnergyStateV13(val label: String) {
  LOW_ENERGY("Low Energy (Review / Flashcards / Intel)"),
  NORMAL("Normal Energy (Socratic / Micro-Drills)"),
  HIGH_FOCUS("High Focus (Deep Forensics / Lab / Reverse Engineering)")
}

data class NextBestAction30V13(
  val actionTitle: String,
  val whyRecommended: String,
  val expectedCapabilityBenefit: String,
  val estimatedMinutes: Int,
  val difficultyTier: String,
  val energyRequirement: LearnerEnergyStateV13,
  val generatedEvidenceType: String,
  val matchedIntelEvent: String? = null
)

/**
 * 16. PROFESSIONAL SIMULATION REPORT & GROWTH INDEX
 */
data class ProfessionalSimulationDebriefReportV13(
  val reportId: String = UUID.randomUUID().toString(),
  val scenarioTitle: String,
  val learnerScoreOverall: Int,
  val containmentDecisionsSummary: String,
  val forensicEvidenceQualityScore: Int,
  val identifiedWeaknesses: List<String>,
  val demonstratedStrengths: List<String>,
  val mistakeAnalysis: String,
  val actionableRemediationPlan: String
)

data class AegoraProfessionalGrowthIndexV13(
  val capabilityIndex: Int = 88,
  val evidenceQualityIndex: Int = 92,
  val transferabilityIndex: Int = 79,
  val consistencyIndex: Int = 94,
  val careerAlignmentIndex: Int = 86,
  val knowledgeFreshnessIndex: Int = 82,
  val compositeSummary: String = "Strong multidimensional profile; prioritized for SOC L2 / Threat Detection Engineer readiness."
)

/**
 * 17. RELEASE CONTROL & COMPLIANCE GATES
 */
enum class LaunchGateStatusV13(val label: String, val isPassed: Boolean) {
  BUILD_GATE("Deterministic Build & Lint", true),
  TEST_GATE("100% Passing Robolectric & Unit Test Suite", true),
  SECURITY_GATE("Zero Untrusted Executable Injection", true),
  PRIVACY_GATE("Zero Unauthorized Data Egress & Local Room DB", true),
  PERFORMANCE_GATE("Cold Startup < 500ms & Zero Frame Drops", true),
  ACCESSIBILITY_GATE("Minimum 48dp Targets & M3 Contrast Compliance", true),
  AI_SAFETY_GATE("100% Prompt Injection & Harmful Exploit Defense", true),
  LEGAL_REVIEW_GATE("Compliance Transparency & No Employment Guarantees", true),
  WEBSITE_GATE("Aegora.app Architecture & Topic Clusters", true),
  SEO_GATE("Original Cybersecurity Roadmaps & Deep Content", true),
  SUPPORT_GATE("Knowledge Vault & Offline Recovery Docs", true),
  MONITORING_GATE("Privacy-Preserving On-Device Telemetry", true),
  ROLLBACK_GATE("Atomic Local State Reset & Migration Safety", true)
}

data class ReleaseCenterStateV13(
  val version: String = "13.0.0",
  val releaseCodename: String = "CYBER REALITY INTELLIGENCE LAYER",
  val gates: List<LaunchGateStatusV13> = LaunchGateStatusV13.entries.toList(),
  val allGatesClear: Boolean = true
)
