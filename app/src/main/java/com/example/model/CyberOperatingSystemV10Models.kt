package com.example.model

import java.util.UUID

// ============================================================
// AEGORA v10.0 — CYBER OPERATING SYSTEM DATA MODELS
// ============================================================

/**
 * 1. CYBER MISSION GENERATOR MODELS
 */
enum class MissionDuration(val label: String, val minutesBudget: Int) {
  FIVE_MIN("5 Min Quick Sprint", 5),
  FIFTEEN_MIN("15 Min Focused Drill", 15),
  THIRTY_MIN("30 Min Tactical Sim", 30),
  SIXTY_MIN("60 Min Deep Investigation", 60),
  THREE_HOURS("3 Hours Capstone Incident", 180),
  MULTI_DAY("Multi-Day Strategic Exercise", 1440)
}

enum class MissionCategory(val title: String) {
  LEARN("Learn Core Theory"),
  INVESTIGATE("Investigate Incident"),
  DEFEND("Defend & Contain"),
  ATTACK_SANDBOX("Attack (Authorized Sandbox)"),
  ANALYZE("Analyze Telemetry"),
  RESEARCH("Research Threat TTP"),
  BUILD("Build Detection/Tool"),
  EXPLAIN("Explain & Teach"),
  COMMUNICATE("Executive Communication"),
  INTERVIEW("Interview Drill"),
  CAREER_PREP("Career Portfolio Prep")
}

data class CyberMissionV10(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val objective: String,
  val targetRole: String,
  val difficulty: SkillLevel,
  val timeBudget: MissionDuration,
  val prerequisites: List<String>,
  val targetSkills: List<String>,
  val targetTools: List<String>,
  val scenarioDescription: String,
  val evidenceRequirements: List<String>,
  val successCriteria: List<String>,
  val safetyBoundary: String = "CONFIDENTIAL SIMULATED SANDBOX ONLY. Zero unauthorized network interaction.",
  val debriefSummary: String,
  val followUpAction: String,
  val isCompleted: Boolean = false,
  val scoreEarned: Int = 0
)

/**
 * 2. CYBER TWIN 4.0 (16 DIMENSIONS WITH EXPLICIT EXPLAINABILITY)
 */
enum class TwinDimension(val displayName: String, val category: String) {
  THEORETICAL_KNOWLEDGE("Theoretical Knowledge", "Foundation"),
  PRACTICAL_ABILITY("Practical Ability", "Application"),
  INVESTIGATION_TRIAGE("Investigation & Triage", "SOC Operations"),
  CAUSAL_REASONING("Causal Reasoning", "Cognitive"),
  DECISION_UNDER_PRESSURE("Decision Under Pressure", "Crisis"),
  EXECUTIVE_COMMUNICATION("Executive Communication", "Leadership"),
  CROSS_DOMAIN_TRANSFER("Cross-Domain Transfer", "Cognitive"),
  KNOWLEDGE_RETENTION("Knowledge Retention", "Memory"),
  CAREER_READINESS("Career Readiness", "Professional"),
  CONFIDENCE_CALIBRATION("Confidence Calibration", "Metacognition"),
  LEARNING_VELOCITY("Learning Velocity", "Growth"),
  MISTAKE_DNA("Mistake DNA Resistance", "Diagnostic"),
  EVIDENCE_QUALITY("Evidence Quality", "Verification"),
  INDEPENDENCE("Independence & Autonomy", "Execution"),
  CONSISTENCY("Consistency & Discipline", "Habit"),
  TRANSFERABILITY("Transferability", "Generalization")
}

data class DimensionExplainability(
  val dimension: TwinDimension,
  val score: Int, // 0 - 100
  val positiveFactors: List<String>,
  val negativeFactors: List<String>,
  val evidenceCount: Int,
  val averageEvidenceAgeDays: Int,
  val evidenceQualityScore: Int, // 0 - 100
  val successfulAttempts: Int,
  val failedAttempts: Int,
  val repeatedMistakes: List<String>,
  val retentionTrend: String, // "Stable", "Declining", "Accelerating"
  val recommendedAction: String
)

data class CyberTwin40Snapshot(
  val learnerId: String = "learner_aegora_01",
  val overallMmr: Int = 1845,
  val skillPassportLevel: SkillPassportLevel = SkillPassportLevel.L4_SKILLED,
  val dimensions: Map<TwinDimension, DimensionExplainability>,
  val activeCareerTarget: String = "SOC Analyst Level 2",
  val daysToCareerReadiness: Int = 42,
  val lastUpdatedEpochMs: Long = System.currentTimeMillis()
)

/**
 * 3. NEXT BEST ACTION ENGINE 2.0
 */
enum class ActionFeedbackType {
  NONE,
  DO_NOW,
  SAVED,
  SNOOZED,
  NOT_RELEVANT,
  TOO_EASY,
  TOO_HARD
}

data class NextBestActionItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val estimatedMinutes: Int,
  val category: MissionCategory,
  val reasonWhyRecommended: String,
  val targetDimension: TwinDimension,
  val urgencyScore: Int, // 1 - 100
  val feedbackState: ActionFeedbackType = ActionFeedbackType.NONE
)

data class NextBestActionBundle(
  val primaryAction: NextBestActionItem,
  val optionA: NextBestActionItem,
  val optionB: NextBestActionItem,
  val optionC: NextBestActionItem,
  val decayTriggerAlert: String? = null
)

/**
 * 5. ADAPTIVE LEARNING MODES (16 MODES)
 */
enum class AdaptiveLearningModeV10(val title: String, val description: String) {
  EXPLAIN_LIKE_IM_NEW("Explain Like I'm New", "Intuitive zero-jargon mental models and everyday metaphors"),
  SOCRATIC_TUTOR("Socratic Tutor", "Guiding questions that prompt the learner to derive the solution"),
  FEYNMAN("Feynman Arena", "Explain concepts back to different audience personas with automated jargon audit"),
  RETRIEVAL_PRACTICE("Retrieval Practice", "Active recall without reference notes to fortify memory consolidation"),
  SPACED_REPETITION("Spaced Repetition", "Ebbinghaus-timed refresher drills targeting decaying synaptic pathways"),
  CASE_BASED("Case-Based Learning", "Real-world incident retrospectives and post-mortem triage"),
  INVESTIGATION("Investigation Mode", "Raw logs, Sysmon events, and PCAP inspection with no hand-holding"),
  SIMULATION("Simulation Mode", "Dynamic enterprise SOC workplace with branching consequences"),
  TEACH_BACK("Teach-Back", "Formulate defensible briefings for junior analysts and peers"),
  DELIBERATE_PRACTICE("Deliberate Practice", "Repetitive targeting of narrow sub-skills at edge of capability"),
  EXAM_MODE("Exam Mode", "Strictly timed, closed-book certification test simulations"),
  INTERVIEW_MODE("Interview Mode", "STAR technical and behavioral questioning with real-time feedback"),
  WORKPLACE_MODE("Workplace Mode", "Multi-channel inbox with conflicting manager/executive pressures"),
  RESEARCH_MODE("Research Mode", "Deep analysis of CVE disclosures and academic crypto papers"),
  PROJECT_MODE("Project Mode", "Building portfolio-grade detection engines, scripts, and parsers"),
  CRISIS_MODE("Crisis Mode", "High-stress active incident containment under tight time clocks")
}

/**
 * 6. LEARNING SCIENCE ENGINE & 5-MIN SKILL RESURRECTION
 */
data class SkillResurrectionChallenge(
  val skillId: String,
  val skillName: String,
  val decayPercentage: Int, // e.g. 38% decayed
  val promptRecallQuestion: String,
  val hint1: String,
  val partialSupport: String,
  val authoritativeAnswer: String,
  val rootExplanation: String,
  val retryQuestion: String,
  val currentStage: Int = 0 // 0=Recall, 1=Hint, 2=Partial, 3=Answer, 4=Explanation, 5=Success
)

/**
 * 7. MISTAKE DNA 3.0 (15+ COGNITIVE & PROCEDURAL PATTERNS)
 */
enum class MistakePatternType(val label: String) {
  PREMATURE_CLOSURE("Premature Closure"),
  CONFIRMATION_BIAS("Confirmation Bias"),
  TUNNEL_VISION("Tunnel Vision"),
  WEAK_TIMELINE_CORRELATION("Weak Timeline Correlation"),
  EVIDENCE_NEGLECT("Evidence Neglect"),
  OVERCONFIDENCE("Overconfidence"),
  EXCESSIVE_HESITATION("Excessive Hesitation"),
  TOOL_DEPENDENCY("Tool Dependency"),
  WEAK_HYPOTHESIS_TESTING("Weak Hypothesis Testing"),
  POOR_PRIORITIZATION("Poor Prioritization"),
  ALERT_FATIGUE("Alert Fatigue"),
  COMMUNICATION_FAILURE("Communication Failure"),
  JARGON_OVERLOAD("Jargon Overload"),
  POOR_ESCALATION("Poor Escalation"),
  INCORRECT_RISK_ASSESSMENT("Incorrect Risk Assessment")
}

data class MistakeRecordV10(
  val id: String = UUID.randomUUID().toString(),
  val whatHappened: String,
  val whyItHappened: String,
  val pattern: MistakePatternType,
  val businessAndTechnicalImpact: String,
  val correctApproach: String,
  val microDrillTitle: String,
  val microDrillPrompt: String,
  val retestCompleted: Boolean = false,
  val timestamp: String = "2026-08-27 21:15"
)

/**
 * 8. REASONING GRAPH 3.0
 */
data class ReasoningStepV10(
  val stepIndex: Int,
  val evidenceObserved: String,
  val observation: String,
  val hypothesisFormed: String,
  val testExecuted: String,
  val testResult: String,
  val decisionMade: String,
  val actionTaken: String,
  val outcome: String
)

data class ReasoningGraphAudit(
  val learnerTrace: List<ReasoningStepV10>,
  val referenceModelTrace: List<ReasoningStepV10>,
  val evidenceFirstScore: Int, // 0 - 100
  val hypothesisFalsificationScore: Int, // 0 - 100
  val timelineDisciplineScore: Int, // 0 - 100
  val unnecessaryActionCount: Int,
  val missedEvidencePoints: List<String>,
  val prematureConclusionFlag: Boolean,
  val referenceModelLabel: String = "AEGORA TRAINING REFERENCE MODEL"
)

/**
 * 9. CYBER WORKPLACE 3.0
 */
enum class CorporateArchetype(val companyName: String, val sector: String, val coreRisk: String) {
  FINTECH("Apex Global Banking", "Financial Services", "Wire fraud, credential stuffing, ransomware, PCI-DSS 4.0"),
  SAAS("OmniCloud Enterprise", "Cloud SaaS", "IAM privilege escalation, multi-tenant bypass, SOC 2"),
  HEALTHCARE("St. Jude Memorial Health", "Healthcare Systems", "Medical IoT ransomware, EHR exfiltration, HIPAA, life safety"),
  UNIVERSITY("Aegora State University", "Higher Education", "Decentralized BYOD, student credential phishing, FERPA"),
  GOVERNMENT("Federal Cyber Agency", "Public Sector", "Nation-state APTs, supply-chain infiltration, FedRAMP High"),
  INDUSTRIAL_OT("Titan Heavy Industries", "Manufacturing & Energy", "Modbus/SCADA manipulation, safety instrumented system bypass"),
  ECOMMERCE("Zephyr Marketplace", "Retail & Payments", "Magecart web skimming, DDoS mitigation, GDPR/CCPA"),
  AI_STARTUP("NovaStream AI Labs", "Artificial Intelligence", "LLM prompt injection, shadow SaaS, typosquatting")
}

enum class WorkplaceChannelV10 {
  SECURITY_ALERT,
  EXECUTIVE_EMAIL,
  JIRA_TICKET,
  SLACK_TEAMS_MESSAGE,
  PHONE_ESCALATION
}

data class WorkplaceItemV10(
  val id: String = UUID.randomUUID().toString(),
  val senderName: String,
  val senderRole: String,
  val channel: WorkplaceChannelV10,
  val timestamp: String,
  val subjectOrSnippet: String,
  val fullContent: String,
  val isUrgent: Boolean,
  val isFalsePositive: Boolean,
  val businessImpactDescription: String,
  val requiredActionOptions: List<String>,
  val selectedAction: String? = null
)

/**
 * 10. CAREER REALITY & REVERSE JOB DESCRIPTION ENGINE (18 FAMILIES)
 */
enum class CyberCareerFamilyV10(val title: String, val focus: String) {
  BLUE_TEAM("Blue Team & SOC", "Defensive monitoring, detection engineering, and incident response"),
  RED_TEAM("Red Team & Pentesting", "Adversary simulation, vulnerability exploitation, and offensive tradecraft"),
  SECURITY_ENGINEERING("Security Engineering", "Hardening infrastructure, IAM, cryptography, and pipeline controls"),
  CLOUD_SECURITY("Cloud Security", "AWS/Azure/GCP identity boundaries, Kubernetes security, and CSPM"),
  APPSEC("Application Security", "SAST/DAST, threat modeling, code auditing, and secure coding practices"),
  DEVSECOPS("DevSecOps", "Automated CI/CD scanning, policy-as-code, and container supply-chain security"),
  GRC("GRC & Compliance", "Risk frameworks, ISO 27001, NIST CSF, audit readiness, and policy governance"),
  DFIR("Digital Forensics & IR", "Memory dumps, disk forensics, reverse engineering, and threat containment"),
  THREAT_INTELLIGENCE("Threat Intelligence (CTI)", "Adversary tracking, TTP mapping, attribution, and diamond model analysis"),
  AI_SECURITY("AI & LLM Security", "Adversarial ML, prompt injection defense, model extraction, and agent safety"),
  OT_ICS("OT / ICS Security", "SCADA telemetry, PLC programming safety, Modbus/DNP3 protocol hardening"),
  CRYPTOGRAPHY("Cryptography & Quantum", "Public key infrastructure, TLS 1.3, post-quantum lattice algorithms (ML-KEM)"),
  SECURITY_RESEARCH("Security Research", "0-day discovery, kernel fuzzing, vulnerability disclosure, and patch analysis"),
  HARDWARE_FIRMWARE("Hardware & Firmware", "UEFI rootkit analysis, JTAG debugging, and side-channel power analysis"),
  PRIVACY("Privacy Engineering", "Differential privacy, zero-knowledge proofs, anonymization, and GDPR/CCPA"),
  SECURITY_ARCHITECTURE("Security Architecture", "Zero Trust design, micro-segmentation, and enterprise threat modeling"),
  SECURITY_PRODUCT("Security Product Management", "Translating technical security capabilities into market offerings"),
  SECURITY_LEADERSHIP("Security Leadership & CISO", "Boardroom communication, risk quantification, budget allocation, and culture")
}

data class ReverseJobAnalysisResult(
  val parsedJobTitle: String,
  val targetCompanyArchetype: String,
  val requiredSkills: List<String>,
  val preferredSkills: List<String>,
  val inferredSkills: List<String>,
  val learnerVerifiedEvidence: List<String>,
  val missingEvidenceGaps: List<String>,
  val overallMatchPercentage: Int,
  val recommendedLabs: List<String>,
  val recommendedProjects: List<String>,
  val recommendedCertifications: List<String>,
  val interviewPreparationQuestions: List<String>,
  val shortestRealisticPathWeeks: Int,
  val legalDisclaimer: String = "Informational analysis only. AEGORA does not promise or guarantee employment."
)

/**
 * 11. OPPORTUNITY RADAR (HONEST PROVENANCE)
 */
enum class OpportunityTypeV10(val label: String) {
  INTERNSHIPS("Internships"),
  ENTRY_LEVEL_JOBS("Entry-Level Jobs"),
  SCHOLARSHIPS("Scholarships"),
  HACKATHONS("Hackathons"),
  CTFS("CTF Competitions"),
  CONFERENCES("Conferences"),
  FELLOWSHIPS("Fellowships"),
  RESEARCH_OPPORTUNITIES("Research Grants")
}

data class OpportunityItemV10(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val organization: String,
  val type: OpportunityTypeV10,
  val matchPercentage: Int,
  val missingRequirements: List<String>,
  val recommendedAction: String,
  val deadline: String,
  val liveStatus: DataLiveStatus = DataLiveStatus.DEMO,
  val sourceUrlNote: String = "Verified Educational Catalog"
)

/**
 * 12. PORTFOLIO EVIDENCE ENGINE
 */
data class PortfolioArtifactV10(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val type: String, // "GitHub README", "Incident Report", "SOC Case Study", "STAR Story", "CV Bullet"
  val markdownContent: String,
  val verifiedEvidenceHashes: List<String>,
  val createdDate: String = "2026-08-27"
)

/**
 * 13. SKILL PASSPORT 3.0 (EVIDENCE-GATED LEVELS L0 TO L9)
 */
enum class SkillPassportLevel(val code: String, val title: String, val minEvidencePoints: Int) {
  L0_AWARENESS("L0", "Awareness", 0),
  L1_BEGINNER("L1", "Beginner", 50),
  L2_FOUNDATION("L2", "Foundation", 150),
  L3_PRACTITIONER("L3", "Practitioner", 350),
  L4_SKILLED("L4", "Skilled", 650),
  L5_ADVANCED("L5", "Advanced", 1000),
  L6_SPECIALIST("L6", "Specialist", 1500),
  L7_EXPERT("L7", "Expert", 2200),
  L8_MENTOR("L8", "Mentor", 3000),
  L9_MASTERY("L9", "Mastery", 4000)
}

/**
 * 14. AI SAFETY & PROMPT-INJECTION DEFENSE
 */
data class UntrustedContentQuarantine(
  val sourceIdentifier: String,
  val rawContent: String,
  val isPromptInjectionSuspected: Boolean,
  val detectedInjectionHeuristics: List<String>,
  val sanitizedContentForAnalysis: String
)

data class AiMentorSafetyProfile(
  val agentName: String,
  val authorizedRole: String,
  val allowedInputs: List<String>,
  val strictLimitations: List<String>,
  val safetyEnforcementRule: String
)
