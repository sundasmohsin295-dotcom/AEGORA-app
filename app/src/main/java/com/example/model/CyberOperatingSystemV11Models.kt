package com.example.model

import java.util.UUID

// ============================================================
// AEGORA v11.0 — CYBER REALITY & PERSONAL CYBER OPERATING SYSTEM
// DATA MODELS
// ============================================================

/**
 * 1. CYBER TWIN 5.0 (22 EXPLAINABLE DIMENSIONS)
 */
enum class TwinDimensionV11(val displayName: String, val category: String) {
  THEORETICAL_KNOWLEDGE("Theoretical Knowledge", "Foundation"),
  PRACTICAL_ABILITY("Practical Ability", "Application"),
  INVESTIGATION("Investigation", "SOC Operations"),
  TRIAGE("Triage & Scoping", "SOC Operations"),
  CAUSAL_REASONING("Causal Reasoning", "Cognitive"),
  DECISION_MAKING("Decision Making", "Execution"),
  PRESSURE_PERFORMANCE("Pressure Performance", "Crisis"),
  COMMUNICATION("Executive & Peer Communication", "Leadership"),
  CROSS_DOMAIN_TRANSFER("Cross-Domain Transfer", "Cognitive"),
  RETENTION("Knowledge Retention", "Memory"),
  CAREER_READINESS("Career Readiness", "Professional"),
  CONFIDENCE_CALIBRATION("Confidence Calibration", "Metacognition"),
  LEARNING_VELOCITY("Learning Velocity", "Growth"),
  MISTAKE_DNA("Mistake DNA Resistance", "Diagnostic"),
  EVIDENCE_QUALITY("Evidence Quality", "Verification"),
  INDEPENDENCE("Independence & Autonomy", "Execution"),
  CONSISTENCY("Consistency & Discipline", "Habit"),
  TRANSFERABILITY("Transferability", "Generalization"),
  ADAPTABILITY("Adaptability & Resilience", "Behavioral"),
  PROBLEM_SOLVING("Complex Problem Solving", "Analytical"),
  TOOL_FLUENCY("Tool & Command Fluency", "Technical"),
  RESEARCH_ABILITY("Threat Research & Synthesis", "Intel")
}

data class DimensionExplainabilityV11(
  val dimension: TwinDimensionV11,
  val currentScore: Int, // 0 - 100
  val whyItExists: String,
  val evidenceSummary: String,
  val evidenceCount: Int,
  val evidenceAgeDays: Int,
  val recentTrend: String, // "Accelerating (+12%)", "Stable", "Decaying (-5%)"
  val negativeFactors: List<String>,
  val positiveFactors: List<String>,
  val repeatedMistakes: List<String>,
  val confidenceScore: Int, // 0 - 100
  val nextRecommendedAction: String
)

data class CyberTwin50Snapshot(
  val learnerId: String = "learner_aegora_01",
  val overallMmr: Int = 1920,
  val skillPassportLevel: SkillPassportLevel = SkillPassportLevel.L4_SKILLED,
  val dimensions: Map<TwinDimensionV11, DimensionExplainabilityV11>,
  val activeCareerTarget: String = "SOC Analyst Level 2",
  val daysToCareerReadiness: Int = 38,
  val cognitiveLoadScore: Int = 42,
  val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

/**
 * 2. CYBER REALITY GRAPH (CONNECTED ENTITY GRAPH)
 */
enum class GraphNodeType {
  CAREER,
  SKILL,
  SUBSKILL,
  TOOL,
  FRAMEWORK,
  TECHNIQUE,
  ATTACK,
  DEFENSE,
  LAB,
  PROJECT,
  CERTIFICATION,
  JOB,
  COMPANY,
  INCIDENT,
  THREAT_ACTOR,
  CVE,
  RESEARCH,
  OPPORTUNITY
}

data class CyberGraphNode(
  val id: String,
  val label: String,
  val type: GraphNodeType,
  val description: String,
  val domain: String,
  val masteryOrMatch: Int = 75,
  val metadata: Map<String, String> = emptyMap()
)

data class CyberGraphEdge(
  val sourceId: String,
  val targetId: String,
  val relationship: String // "REQUIRES", "IMPLEMENTS", "MITIGATES", "EXPLOITS", "TESTED_IN", "PREPARES_FOR"
)

data class CyberRealityGraphData(
  val nodes: List<CyberGraphNode>,
  val edges: List<CyberGraphEdge>,
  val activeDomainFilter: String = "All Domains"
)

/**
 * 3. LEARNING INTELLIGENCE ENGINE & 13 LEARNING MODES
 */
enum class LearningModeV11(val title: String, val shortDesc: String, val badge: String) {
  BEGINNER_DISCOVERY("Beginner Discovery Mode", "Zero-jargon mental models, visuals and guided step-by-step practice", "DISCOVER"),
  SOCRATIC("Socratic Tutor Mode", "AI guides you through diagnostic questioning rather than giving answers", "SOCRATIC"),
  FEYNMAN("Feynman Arena Mode", "Explain concepts simply back to different audience personas", "FEYNMAN"),
  RETRIEVAL("Retrieval Practice Mode", "Closed-book memory retrieval to strengthen synaptic retention", "RECALL"),
  INVESTIGATION("Investigation Mode", "Raw logs, PCAPs, and Sysmon artifacts with evidence-first triage", "INVESTIGATE"),
  SIMULATION("Simulation Mode", "Realistic enterprise workplace incident with live branching consequences", "SIMULATION"),
  TEACH_BACK("Teach-Back Mode", "Train and guide an AI junior analyst on defense methodologies", "TEACH"),
  PRESSURE("Pressure & Crisis Mode", "High-stress active incident containment under tight time clocks", "PRESSURE"),
  PROJECT("Portfolio Project Mode", "Build real detection engines, parsers, and defense tooling", "BUILD"),
  INTERVIEW("Interview Drill Mode", "STAR technical and behavioral questioning with real-time feedback", "INTERVIEW"),
  RESEARCH("Threat Research Mode", "Analyze CVE disclosures, threat actor advisories, and crypto papers", "RESEARCH"),
  REVERSE_ENGINEERING("Reverse Engineering Mode", "Disassemble binaries, inspect shellcode, and deobfuscate scripts", "REVERSE"),
  EXECUTIVE("Executive Communication Mode", "Brief non-technical C-suite leadership on business risks & downtime", "EXECUTIVE")
}

data class LearnerDiagnosticProfile(
  val whatTheyKnow: List<String>,
  val whatTheyDontKnow: List<String>,
  val whatTheyThinkTheyKnow: List<String>, // Overconfidence gaps
  val whatTheyCanActuallyDo: List<String>, // Lab-verified capabilities
  val whatTheyForget: List<String>,       // Decay alerts
  val whatTheyStruggleWith: List<String>, // High error rates
  val whatTheyEnjoy: List<String>,
  val whatTheyAvoid: List<String>,
  val learningSpeedRating: String = "High in Network & SIEM, Moderate in Binary Analysis",
  val transferabilityRating: String = "Demonstrated cross-domain pivoting from Network to Host logs"
)

/**
 * 4. SKILL DECAY RADAR 2.0
 */
enum class DecayRiskLevel(val label: String) {
  LOW("Low Decay Risk"),
  MEDIUM("Medium Decay Risk"),
  HIGH("High Decay Risk"),
  CRITICAL("Critical Synaptic Decay")
}

data class SkillDecayItemV11(
  val skillId: String,
  val skillName: String,
  val currentLevel: Int, // 0 - 100
  val peakLevel: Int,    // 0 - 100
  val trend: String,     // "-12% over 14 days"
  val decayRisk: DecayRiskLevel,
  val daysSinceLastActivePractice: Int,
  val projectedRetentionDays: Int,
  val recommendedResurrection: String
)

/**
 * 5. MISTAKE DNA 4.0 (17 PATTERNS)
 */
enum class MistakePatternV11(val displayName: String, val category: String) {
  PREMATURE_CLOSURE("Premature Closure", "Cognitive"),
  CONFIRMATION_BIAS("Confirmation Bias", "Cognitive"),
  TUNNEL_VISION("Tunnel Vision", "Cognitive"),
  WEAK_TIMELINE_CORRELATION("Weak Timeline Correlation", "Procedural"),
  IGNORING_NEGATIVE_EVIDENCE("Ignoring Negative Evidence", "Analytical"),
  POOR_HYPOTHESIS_TESTING("Poor Hypothesis Testing", "Analytical"),
  OVERCONFIDENCE("Overconfidence", "Metacognitive"),
  UNDERCONFIDENCE("Underconfidence", "Metacognitive"),
  ALERT_FATIGUE("Alert Fatigue", "Operational"),
  JARGON_DEPENDENCE("Jargon Dependence", "Communication"),
  TOOL_DEPENDENCE("Tool Dependence", "Technical"),
  WEAK_DOCUMENTATION("Weak Documentation", "Procedural"),
  POOR_PRIORITIZATION("Poor Prioritization", "Operational"),
  FAILURE_TO_ESCALATE("Failure to Escalate", "Communication"),
  INCORRECT_SCOPE("Incorrect Scope", "Analytical"),
  WEAK_COMMUNICATION("Weak Communication", "Leadership"),
  FAILURE_TO_VALIDATE_ASSUMPTIONS("Failure to Validate Assumptions", "Cognitive")
}

data class MistakePatternDetailV11(
  val pattern: MistakePatternV11,
  val observedEvidence: String,
  val frequencyCount: Int,
  val recentOccurrence: String,
  val businessImpact: String,
  val concreteExample: String,
  val microDrillTitle: String,
  val microDrillPrompt: String,
  val recommendedPracticeAction: String
)

/**
 * 6. REASONING GRAPH 4.0 & INVESTIGATION REPLAY
 */
data class ReasoningReplayStepV11(
  val stepIndex: Int,
  val timestamp: String,
  val beliefHypothesis: String,
  val evidenceSupporting: String,
  val evidenceContradicting: String,
  val evidenceIgnored: String,
  val checksShouldHavePerformed: String,
  val actionTaken: String,
  val consequenceOutcome: String
)

data class InvestigationReplaySession(
  val incidentId: String,
  val incidentTitle: String,
  val overallReasoningScore: Int, // 0 - 100
  val evidenceFirstFidelity: Int,
  val steps: List<ReasoningReplayStepV11>,
  val coachCritique: String
)

/**
 * 7. INCIDENT MULTIVERSE (ALTERNATIVE BRANCHES)
 */
data class MultiverseBranchV11(
  val branchId: String,
  val decisionLabel: String,
  val isOptimalBranch: Boolean,
  val consequenceSummary: String,
  val technicalImpact: String,
  val businessDowntimeHours: Double,
  val financialLossEstimateUsd: Long,
  val keyLesson: String
)

data class IncidentMultiverseScenario(
  val incidentId: String,
  val initialPivotalDecision: String,
  val branches: List<MultiverseBranchV11>,
  val postMortemReview: String
)

/**
 * 8. LIVING ADVERSARY ENGINE (ADAPTIVE SANDBOXED ADVERSARY)
 */
data class LivingAdversaryProfile(
  val adversaryCodename: String = "PHANTOM_WEAVER",
  val detectedLearnerBlindSpots: List<String>,
  val nextTargetedAttackVector: String,
  val educationalTactics: List<String>,
  val sandboxBoundary: String = "STRICTLY AUTHORIZED LOCAL SANDBOX ONLY. Zero egress traffic.",
  val adaptationLevel: String = "Adapts dynamically to defensive blind spots"
)

/**
 * 9. FUSION CHALLENGES (CROSS-DOMAIN SCENARIOS)
 */
data class FusionDomainScore(
  val domainName: String, // "Cloud", "Identity", "Network", "Endpoint", "Threat Intel", "IR"
  val score: Int,         // 0 - 100
  val evidenceGenerated: String
)

data class FusionChallengeScenario(
  val challengeId: String = UUID.randomUUID().toString(),
  val title: String,
  val attackChainStory: String,
  val domainScores: List<FusionDomainScore>,
  val overallFusionScore: Int,
  val isCompleted: Boolean = false
)

/**
 * 10. RED -> BLUE MODE (ATTACK SIMULATION THEN DEFENSE TRIAGE)
 */
data class RedBlueSession(
  val sessionId: String = UUID.randomUUID().toString(),
  val redPhaseAttackPlan: String,
  val redSimulatedArtifactsGenerated: List<String>,
  val blueDetectionScore: Int,
  val blueReasoningScore: Int,
  val bluePrioritizationScore: Int,
  val blueContainmentScore: Int,
  val blueCommunicationScore: Int,
  val isCompleted: Boolean = false
)

/**
 * 11. WORKPLACE REALITY ENGINE 4.0 (12 ARCHETYPES)
 */
enum class EnterpriseSectorV11(val companyName: String, val sectorTitle: String, val constraintSummary: String) {
  FINTECH("Apex Global Bank", "FinTech & Banking", "Wire fraud, credential stuffing, PCI-DSS 4.0, zero downtime SLA"),
  HEALTHCARE("St. Jude Memorial Hospital", "Healthcare", "Medical IoT devices, ransomware, HIPAA, patient safety"),
  UNIVERSITY("Aegora State University", "Higher Education", "BYOD open network, student phishing, FERPA compliance"),
  GOVERNMENT("Federal Cybersecurity Agency", "Public Sector", "Nation-state APTs, supply-chain attacks, FedRAMP High"),
  CLOUD_SAAS("OmniCloud Enterprise", "Cloud Infrastructure", "IAM privilege escalation, multi-tenant bypass, SOC 2"),
  ECOMMERCE("Zephyr Retail", "E-Commerce", "Magecart skimming, DDoS flash mobs, peak shopping holiday SLAs"),
  INDUSTRIAL_OT("Titan Heavy Industries", "Energy & SCADA", "Modbus protocol safety, air-gapped network bridge, human safety"),
  AI_STARTUP("NovaStream AI Labs", "AI & LLM Services", "Prompt injection, training data poisoning, API token exfiltration"),
  BANK("Capital Trust International", "Commercial Banking", "SWIFT transfer monitoring, insider threat, liquidity risk"),
  TELECOM("GlobalWave Communications", "Telecommunications", "BGP hijacking, SS7 interception, core fiber router compromise"),
  MSSP("CyberGuard Managed Security", "MSSP / MDR", "Handling 40 client SIEMs concurrently with strict 15-min SLA"),
  CRITICAL_INFRA("Metro Water & Power", "Critical Infrastructure", "Water purification SCADA safety, power grid substation defense")
}

data class WorkplaceCrisisItemV11(
  val id: String = UUID.randomUUID().toString(),
  val sector: EnterpriseSectorV11,
  val senderName: String,
  val senderTitle: String,
  val channel: WorkplaceChannelV10,
  val subject: String,
  val bodyText: String,
  val organizationalConstraint: String,
  val isUrgent: Boolean,
  val options: List<String>,
  val selectedOptionIndex: Int? = null,
  val outcomeFeedback: String? = null
)

/**
 * 12. VOICE CYBER DRILLS
 */
data class VoiceCyberDrillScenario(
  val drillId: String = UUID.randomUUID().toString(),
  val personaTitle: String = "Panicked Employee (Sarah from Accounting)",
  val promptAudioTranscript: String = "Help! I clicked an invoice PDF from FedEx, and my screen turned black with a red countdown timer!",
  val learnerResponseTranscript: String = "",
  val technicalCorrectnessScore: Int = 0,
  val questionQualityScore: Int = 0,
  val communicationCalmnessScore: Int = 0,
  val prioritizationScore: Int = 0,
  val completenessScore: Int = 0,
  val coachCritique: String = "Clear instructions to disconnect Ethernet immediately and preserve memory state."
)

/**
 * 13. AI AGENT ROLES (17 SPECIALIZED ROLES)
 */
enum class AiSpecialistRoleV11(val title: String, val responsibility: String, val safetyBoundary: String) {
  AI_TUTOR("AI Tutor", "Foundational theory and mental models", "Socratic guidance only"),
  AI_MENTOR("AI Mentor", "Long-term career progression and strategy", "Non-directive career advice"),
  AI_ASSESSOR("AI Assessor", "Objective evaluation of lab submissions", "Evidence-gated rubric scoring"),
  AI_CAREER_ADVISOR("AI Career Advisor", "Resume, job description gap analysis", "Transparent market data"),
  AI_SIMULATION_DIRECTOR("AI Simulation Director", "Controls scenario injection and adversaries", "Sandboxed execution"),
  AI_RESEARCH_ASSISTANT("AI Research Assistant", "CVE and whitepaper synthesis", "Fact-checked citation grounding"),
  AI_INTERVIEWER("AI Interviewer", "STAR behavioral and technical questioning", "Objective grading criteria"),
  AI_PROJECT_ADVISOR("AI Project Advisor", "Code and architecture guidance", "Secure coding review"),
  AI_CONTENT_CURATOR("AI Content Curator", "Vetting authentic resource universe links", "Legitimate source verification"),
  AI_LEARNING_SCIENTIST("AI Learning Scientist", "Ebbinghaus decay curve optimization", "Metacognitive feedback"),
  AI_SOC_COACH("AI SOC Coach", "Triage pacing and alert fatigue mitigation", "Standard Operating Procedure adherence"),
  AI_THREAT_INTEL_ANALYST("AI Threat Intel Analyst", "MITRE ATT&CK and Diamond model mapping", "Threat context grounding"),
  AI_PORTFOLIO_REVIEWER("AI Portfolio Reviewer", "Audits GitHub READMEs and STAR reports", "Zero-fabrication validation"),
  AI_COMMUNICATION_COACH("AI Communication Coach", "Eliminates jargon in executive briefings", "Clarity and impact rubric"),
  AI_CERTIFICATION_PLANNER("AI Certification Planner", "Syllabus mapping and exam strategy", "Objective credential comparison"),
  AI_EVIDENCE_AUDITOR("AI Evidence Auditor", "Validates cryptographic hashes and artifacts", "Strict provenance verification"),
  AI_QUALITY_EVALUATOR("AI Quality Evaluator", "Monitors AI responses for hallucinations", "Self-correction filtering")
}

data class AiAgentStateV11(
  val role: AiSpecialistRoleV11,
  val modelIdentifier: String = "gemini-3.7-flash (Simulated Local/API)",
  val promptInjectionShieldActive: Boolean = true,
  val confidenceRating: Int = 94,
  val lastAuditLog: String = "Input sanitized via UntrustedContentQuarantine"
)

/**
 * 14. EVIDENCE ENGINE & SKILL PASSPORT 4.0
 */
enum class EvidenceVerificationType(val label: String, val trustWeight: Int) {
  SELF_REPORTED("Self-Reported", 10),
  AI_ASSESSED("AI-Assessed", 50),
  LAB_VERIFIED("Lab-Verified", 85),
  PROJECT_EVIDENCED("Project-Evidenced", 95),
  MENTOR_REVIEWED("Mentor-Reviewed", 100)
}

data class SkillEvidenceEntryV11(
  val evidenceId: String = UUID.randomUUID().toString(),
  val skillName: String,
  val activityTitle: String,
  val verificationType: EvidenceVerificationType,
  val timestamp: String = "2026-08-28 09:30",
  val resultSummary: String,
  val evidenceHash: String = "sha256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
  val confidenceScore: Int = 92
)

/**
 * 15. OPPORTUNITY RADAR 2.0 (HONEST PROVENANCE)
 */
data class OpportunityRadarItemV11(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val organization: String,
  val type: OpportunityTypeV10,
  val country: String,
  val isRemote: Boolean,
  val experienceLevel: String,
  val matchPercentage: Int,
  val missingRequirements: List<String>,
  val deadline: String,
  val isFree: Boolean,
  val studentEligibility: String,
  val provenanceStatus: DataLiveStatus = DataLiveStatus.DEMO
)

/**
 * 16. QUALITY CENTER & RELEASE CONTROL MODELS
 */
enum class ReleaseGateStatus {
  DEVELOPMENT,
  INTERNAL_TEST,
  CLOSED_TEST,
  OPEN_TEST,
  RELEASE_CANDIDATE,
  PRODUCTION_READY
}

data class ReleaseControlAudit(
  val currentStage: ReleaseGateStatus = ReleaseGateStatus.RELEASE_CANDIDATE,
  val unitTestPassRate: Int = 100,
  val securityAuditScore: Int = 98,
  val performanceScore: Int = 95,
  val accessibilityScore: Int = 96,
  val offlineReadinessScore: Int = 94,
  val legalPrivacyCompliant: Boolean = true,
  val googlePlayAssetReady: Boolean = true,
  val isProductionCertified: Boolean = false,
  val certificationNote: String = "Production certification requires live backend integration verification."
)
