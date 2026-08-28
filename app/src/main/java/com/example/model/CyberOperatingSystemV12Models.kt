package com.example.model

import java.util.UUID

// ============================================================
// AEGORA v12.0 — CYBER REALITY OPERATING SYSTEM
// ADAPTIVE CYBER PROFESSIONAL DEVELOPMENT ENGINE DATA MODELS
// ============================================================

/**
 * 1. CYBER TWIN 6.0 (27 DYNAMIC CAPABILITY DIMENSIONS)
 */
enum class TwinDimensionV12(val displayName: String, val category: String) {
  KNOWLEDGE("Theoretical Knowledge", "Foundation"),
  PRACTICAL_ABILITY("Practical Ability", "Application"),
  INVESTIGATION("Investigation & Forensics", "SOC Operations"),
  DETECTION("Detection Engineering", "Defense"),
  RESPONSE("Incident Response", "Defense"),
  REASONING("Causal Reasoning", "Cognitive"),
  DECISION_MAKING("Decision Making", "Execution"),
  PRESSURE_PERFORMANCE("Pressure Performance", "Crisis"),
  COMMUNICATION("Executive & Peer Communication", "Leadership"),
  RESEARCH("Threat Research & Synthesis", "Intel"),
  TOOL_FLUENCY("Tool & Command Fluency", "Technical"),
  TRANSFERABILITY("Transferability", "Generalization"),
  RETENTION("Knowledge Retention", "Memory"),
  LEARNING_VELOCITY("Learning Velocity", "Growth"),
  INDEPENDENCE("Independence & Autonomy", "Execution"),
  CONSISTENCY("Consistency & Habit", "Discipline"),
  ADAPTABILITY("Adaptability & Resilience", "Behavioral"),
  PROBLEM_SOLVING("Complex Problem Solving", "Analytical"),
  EVIDENCE_QUALITY("Evidence Quality", "Verification"),
  CAREER_READINESS("Career Readiness", "Professional"),
  CONFIDENCE_CALIBRATION("Confidence Calibration", "Metacognition"),
  MISTAKE_RESISTANCE("Mistake DNA Resistance", "Diagnostic"),
  SYSTEMS_THINKING("Systems Thinking", "Architecture"),
  THREAT_MODELING("Threat Modeling", "Design"),
  TECHNICAL_WRITING("Technical Writing", "Documentation"),
  COLLABORATION("Team Collaboration", "Operations"),
  LEADERSHIP_POTENTIAL("Leadership Potential", "Strategy")
}

data class DimensionExplainabilityV12(
  val dimension: TwinDimensionV12,
  val currentState: Int, // 0 - 100
  val trend: String,     // "+12% this month", "Stable", "-6% decay risk"
  val evidenceCount: Int,
  val evidenceQuality: Int, // 0 - 100
  val evidenceAgeDays: Int,
  val confidence: Int,      // 0 - 100
  val recentPerformance: String,
  val weaknesses: List<String>,
  val strengths: List<String>,
  val decayRisk: DecayRiskLevel,
  val transferability: String,
  val recommendedAction: String,
  val isAlgorithmicEstimate: Boolean = true // Explicitly labeled estimate
)

data class CyberTwin60Snapshot(
  val learnerId: String = "learner_aegora_01",
  val overallScore: Int = 1960,
  val skillPassportLevel: SkillPassportLevel = SkillPassportLevel.L4_SKILLED,
  val dimensions: Map<TwinDimensionV12, DimensionExplainabilityV12>,
  val targetRole: String = "Senior SOC Analyst / Detection Engineer",
  val daysToTargetReadiness: Int = 32,
  val cognitiveLoadScore: Int = 38,
  val lastCalculatedLabel: String = "Algorithmic capability model updated today"
)

/**
 * 2. CAPABILITY VS KNOWLEDGE ENGINE (KNOWLEDGE != SKILL != PERFORMANCE)
 */
data class ConceptCapabilityProfile(
  val conceptName: String,
  val knowledgeScore: Int,      // 0 - 100 (Can explain theory)
  val applicationScore: Int,    // 0 - 100 (Can execute in lab)
  val investigationScore: Int,  // 0 - 100 (Can identify in raw telemetry)
  val transferScore: Int,       // 0 - 100 (Can adapt to new context)
  val communicationScore: Int,  // 0 - 100 (Can explain business impact)
  val capabilityGap: Int,       // knowledge - application
  val gapAnalysisSummary: String,
  val prescriptiveFix: String
)

/**
 * 3. TRANSFERABILITY ENGINE
 */
enum class TransferContextType(val title: String) {
  WEB_APP("Web Application"),
  API("REST/GraphQL API"),
  CLOUD_FUNCTION("Serverless / Cloud Function"),
  LOG_INVESTIGATION("SIEM Log Investigation"),
  THREAT_INTEL_REPORT("Threat Intelligence Report"),
  CODE_REVIEW("Source Code Review"),
  INCIDENT_RESPONSE("Active Incident Response")
}

data class ConceptTransferTest(
  val conceptId: String,
  val conceptTitle: String,
  val contextsTested: Map<TransferContextType, Boolean>,
  val recognitionScore: Int,    // 0 - 100
  val applicationScore: Int,    // 0 - 100
  val transferScore: Int,       // 0 - 100
  val generalizationScore: Int, // 0 - 100
  val canExplain: Boolean,
  val canApply: Boolean,
  val canTransfer: Boolean,
  val evaluationNote: String
)

/**
 * 4. UNKNOWN UNKNOWN ENGINE (METACOGNITIVE CALIBRATION)
 */
data class UnknownUnknownGap(
  val topic: String,
  val selfConfidence: Int,       // 0 - 100
  val actualPerformance: Int,    // 0 - 100
  val scenarioPerformance: Int,  // 0 - 100
  val evidenceQuality: Int,      // 0 - 100
  val transferPerformance: Int,  // 0 - 100
  val gapDetected: Boolean,
  val respectfulFeedback: String,
  val calibrationExercisePrompt: String
)

/**
 * 5. CYBER DECISION LAB & UNCERTAINTY ENGINE
 */
enum class DecisionActionType(val label: String) {
  INVESTIGATE("Investigate Telemetry"),
  ESCALATE("Escalate to Tier 2/CISO"),
  CONTAIN("Contain & Isolate Host"),
  MONITOR("Passively Monitor & Log"),
  IGNORE("Mark False Positive / Ignore"),
  REQUEST_MORE_EVIDENCE("Request Memory/PCAP Evidence")
}

data class DecisionLabScenario(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val incompleteEvidenceSummary: String,
  val conflictingAlerts: List<String>,
  val timeLimitSeconds: Int = 120,
  val businessPressure: String,
  val attackerIntentUncertainty: String,
  val iocConfidencePercent: Int, // e.g. 42%
  val businessImpactRating: String, // "HIGH - $150k/hr downtime"
  val selectedAction: DecisionActionType? = null,
  val decisionQualityScore: Int = 88,
  val evidenceUsageScore: Int = 90,
  val riskAwarenessScore: Int = 85,
  val timeManagementScore: Int = 82,
  val businessImpactScore: Int = 86,
  val reversibilityScore: Int = 80,
  val whatWasKnown: String,
  val whatWasAssumed: String,
  val whatRemainedUncertain: String,
  val postDecisionDebrief: String
)

/**
 * 6. ATTACKER JOURNEY RECONSTRUCTION & DIGITAL CRIME SCENE
 */
enum class MitreAttackStage(val phaseName: String, val code: String) {
  INITIAL_ACCESS("Initial Access", "TA0001"),
  EXECUTION("Execution", "TA0002"),
  PERSISTENCE("Persistence", "TA0003"),
  PRIVILEGE_ESCALATION("Privilege Escalation", "TA0004"),
  DEFENSE_EVASION("Defense Evasion", "TA0005"),
  CREDENTIAL_ACCESS("Credential Access", "TA0006"),
  DISCOVERY("Discovery", "TA0007"),
  LATERAL_MOVEMENT("Lateral Movement", "TA0008"),
  COLLECTION("Collection", "TA0009"),
  EXFILTRATION("Exfiltration", "TA0010")
}

data class AttackerJourneyStageItem(
  val stage: MitreAttackStage,
  val reconstructedArtifact: String,
  val sourceTelemetry: String,
  val isCorrelated: Boolean
)

data class AttackerJourneyScenario(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val targetEnvironment: String,
  val stages: List<AttackerJourneyStageItem>,
  val mitreMappingSummary: String
)

data class DigitalCrimeSceneItem(
  val id: String = UUID.randomUUID().toString(),
  val artifactType: String, // Disk, Memory, Browser, EventLog, NetworkPCAP, Email, AuthRecord
  val evidenceName: String,
  val isPreserved: Boolean,
  val isAnalyzed: Boolean,
  val isRelevant: Boolean,
  val chainOfCustodyHash: String,
  val forensicNotes: String
)

/**
 * 7. SOC SHIFT MODE & CYBER FATIGUE MODE
 */
data class SocShiftAlertV12(
  val alertId: String = UUID.randomUUID().toString(),
  val alertTitle: String,
  val severity: String,
  val rawTelemetry: String,
  val isTruePositive: Boolean,
  val triagedAction: String? = null,
  val mttaSeconds: Int = 45,
  val handlingScore: Int = 90
)

data class SocShiftSessionV12(
  val sessionId: String = UUID.randomUUID().toString(),
  val shiftDurationMinutes: Int = 45,
  val alerts: List<SocShiftAlertV12>,
  val mttaAverageSec: Double = 48.5,
  val triageAccuracyPercent: Int = 94,
  val falsePositiveHandlingScore: Int = 92,
  val escalationQualityScore: Int = 88,
  val documentationScore: Int = 90,
  val containmentReasoningScore: Int = 89,
  val isSimulationExplicitlyLabeled: Boolean = true
)

data class CyberFatigueScenario(
  val totalEventsCount: Int = 280,
  val criticalSignalsCount: Int = 5,
  val noiseEventsCount: Int = 275,
  val attentionScore: Int = 85,
  val prioritizationScore: Int = 88,
  val missedSignals: List<String>,
  val whyMissedAnalysis: String,
  val recommendedMicroDrill: String
)

/**
 * 8. MULTI-PERSON INCIDENT SIMULATION & EXECUTIVE BOARDROOM
 */
enum class IncidentPersonaRole(val roleTitle: String) {
  SOC_ANALYST("SOC Tier 1 Analyst"),
  INCIDENT_RESPONDER("Lead IR Specialist"),
  CISO("Chief Information Security Officer"),
  IT_ADMINISTRATOR("Senior IT Systems Admin"),
  DEVELOPER("Lead Backend Architect"),
  LEGAL("Corporate Legal Counsel"),
  COMMUNICATIONS("Director of PR & Comms"),
  EXECUTIVE("Chief Operating Officer")
}

data class MultiPersonMessage(
  val senderRole: IncidentPersonaRole,
  val senderName: String,
  val messageText: String,
  val stanceOrConcern: String
)

data class MultiPersonSimulationScenario(
  val scenarioId: String = UUID.randomUUID().toString(),
  val title: String,
  val learnerRole: IncidentPersonaRole = IncidentPersonaRole.SOC_ANALYST,
  val conversationHistory: List<MultiPersonMessage>,
  val coordinationScore: Int = 88
)

data class ExecutiveBoardroomBriefing(
  val briefingId: String = UUID.randomUUID().toString(),
  val incidentOverview: String,
  val financialImpactEstimate: String,
  val regulatoryRiskSummary: String,
  val clarityScore: Int = 92,
  val riskFramingScore: Int = 90,
  val businessLanguageScore: Int = 86,
  val uncertaintyCommunicationScore: Int = 88,
  val boardFeedback: String
)

/**
 * 9. CYBER COMMUNICATION LAB
 */
enum class CommunicationArtifactType(val label: String) {
  INCIDENT_REPORT("Formal Incident Report"),
  EXECUTIVE_SUMMARY("C-Suite Executive Summary"),
  SOC_HANDOVER("Shift Change SOC Handover"),
  TECHNICAL_DOCUMENTATION("Technical Detection Playbook"),
  EMAIL_ESCALATION("Urgent IT Escalation Email"),
  RISK_STATEMENT("Audit & Risk Statement"),
  SECURITY_RECOMMENDATIONS("Remediation Roadmap")
}

data class CyberCommunicationEvaluation(
  val type: CommunicationArtifactType,
  val submittedText: String,
  val accuracyScore: Int,
  val clarityScore: Int,
  val completenessScore: Int,
  val jargonScore: Int,
  val audienceSuitabilityScore: Int,
  val aiCritique: String
)

/**
 * 10. RESEARCH INTELLIGENCE ENGINE & SOURCE TRUST GRAPH & CLAIM VERIFIER
 */
enum class ResearchCategoryV12(val title: String) {
  CVE("CVE & Zero-Day"),
  ADVISORY("CISA / Gov Advisory"),
  THREAT_REPORT("Vendor Threat Report"),
  RESEARCH_PAPER("Academic Cryptography / Sec Paper"),
  MAJOR_INCIDENT("Historical Root Cause Analysis"),
  SECURITY_BLOG("Authoritative Security Engineering"),
  VENDOR_DOCS("Cloud & Tool Standards"),
  STANDARDS("NIST SP 800-53 / ISO 27001"),
  CONFERENCES("DEF CON / Black Hat Archive"),
  CTFS("National CTF Writeups"),
  JOBS("Verified Cyber Job Listings"),
  INTERNSHIPS("Student Security Internships"),
  SCHOLARSHIPS("Cybersecurity Fellowships")
}

data class ResearchDeskItem(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val category: ResearchCategoryV12,
  val sourceName: String,
  val sourceUrl: String,
  val publicationDate: String,
  val lastCheckedDate: String,
  val confidenceScore: Int,
  val summary: String,
  val isUntrustedContentQuarantined: Boolean = true
)

data class SourceTrustNode(
  val claim: String,
  val sources: List<String>,
  val corroboratingCount: Int,
  val contradictingCount: Int,
  val evidenceQuality: String,
  val informationLiteracyLesson: String
)

enum class ClaimVerificationVerdict(val label: String) {
  SUPPORTED("Supported by Authoritative Standards"),
  PARTIALLY_SUPPORTED("Partially Supported with Nuance"),
  UNCERTAIN("Uncertain / Incomplete Evidence"),
  CONTRADICTED("Contradicted by Empirical CVE Telemetry"),
  INSUFFICIENT_EVIDENCE("Insufficient Verifiable Data")
}

data class ClaimVerificationResult(
  val claimText: String,
  val verdict: ClaimVerificationVerdict,
  val evidenceBasis: String,
  val sourceQuality: String,
  val contradictionsFound: String,
  val missingContext: String,
  val nuanceNotice: String = "Algorithmic evaluation for information literacy; not absolute legal truth."
)

/**
 * 11. CAREER PATH SIMULATOR & CAREER MULTIVERSE & JOB READINESS
 */
enum class CyberCareerRoleV12(val title: String) {
  SOC_ANALYST("SOC Analyst (L1 / L2)"),
  PENETRATION_TESTER("Penetration Tester / Offensive Security"),
  CLOUD_SECURITY("Cloud Security Engineer"),
  APPSEC("Application Security Engineer"),
  THREAT_INTELLIGENCE("Threat Intelligence Analyst"),
  DFIR("Digital Forensics & Incident Response"),
  GRC("Governance, Risk & Compliance"),
  AI_SECURITY("AI & LLM Security Specialist"),
  SECURITY_ENGINEERING("Detection & Security Infrastructure Engineer")
}

data class CareerPathBlueprintV12(
  val role: CyberCareerRoleV12,
  val currentCapabilityMatchPercent: Int,
  val requiredCapabilities: List<String>,
  val missingCapabilities: List<String>,
  val recommendedProjects: List<String>,
  val recommendedLabs: List<String>,
  val recommendedCertifications: List<String>,
  val estimatedLearningEffortWeeks: Int,
  val noPlacementGuaranteeDisclaimer: String = "Learning path estimate only; employment not guaranteed."
)

data class JobReadinessDeconstruction(
  val role: CyberCareerRoleV12,
  val technicalEvidenceScore: Int,
  val practicalEvidenceScore: Int,
  val communicationEvidenceScore: Int,
  val projectEvidenceScore: Int,
  val interviewEvidenceScore: Int,
  val roleAlignmentScore: Int,
  val readyAreas: List<String>,
  val gapAreas: List<String>,
  val unprovenAreas: List<String>
)

/**
 * 12. PORTFOLIO AUDITOR & PROJECT GENERATOR 3.0 & PROBLEM BANK
 */
data class PortfolioAuditReport(
  val projectName: String,
  val technicalDepthScore: Int,
  val originalityScore: Int,
  val reproducibilityScore: Int,
  val securityArchitectureScore: Int,
  val readmeQualityScore: Int,
  val isTutorialCopySuspected: Boolean,
  val unsupportedClaimsFound: List<String>,
  val actionableImprovements: List<String>
)

data class GeneratedProjectBlueprint30(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val durationScope: String, // "1 Hour", "1 Day", "1 Week", "1 Month", "Semester", "Capstone"
  val targetCareer: CyberCareerRoleV12,
  val problemStatement: String,
  val architectureOverview: String,
  val learningOutcomes: List<String>,
  val toolsUsed: List<String>,
  val milestones: List<String>,
  val securityRequirements: List<String>,
  val testingPlan: String,
  val portfolioArtifactSummary: String
)

data class ProblemBankItem(
  val id: String = UUID.randomUUID().toString(),
  val category: String, // Detection, IR, Cloud, Network, Web, Mobile, Identity, Malware, Forensics, GRC, AISec, OT, IoT, Privacy, DevSecOps
  val title: String,
  val context: String,
  val constraints: List<String>,
  val evidenceProvided: String,
  val objective: String,
  val difficulty: String,
  val expectedSkills: List<String>,
  val evaluationRubric: String
)

/**
 * 13. ADAPTIVE DIFFICULTY, LAB BUILDER & SAFETY GATE
 */
data class UserLabHardwareProfile(
  val hardwareDescription: String = "Windows 11 / Linux VM / 16GB RAM / WSL2 / Cloud Free Tier",
  val recommendedLocalArchitecture: String = "VirtualBox isolated host-only network with Kali VM and Ubuntu Target VM",
  val safeExercises: List<String> = listOf("Sysmon process analysis", "Splunk SPL queries", "Snort IDS rules", "Docker CTF challenges"),
  val localTools: List<String> = listOf("Wireshark", "Volatility 3", "Ghidra", "Suricata", "YARA")
)

data class LabSafetyGateClassification(
  val targetClassification: String = "LOCAL_SANDBOX_127_0_0_1",
  val authorizationStatus: String = "AUTHORIZED_EDUCATIONAL_ENV",
  val environmentSafety: String = "ISOLATED_CONTAINER_ZERO_EGRESS",
  val riskRating: String = "SAFE_SIMULATION",
  val explicitConfirmationRequired: Boolean = false,
  val guardrailNote: String = "External scanning or attacks strictly forbidden."
)

/**
 * 14. SKILL EVIDENCE GRAPH & MASTERY GATES & BOSS INCIDENTS
 */
data class SkillEvidenceNode(
  val skillName: String,
  val activityName: String,
  val performanceProof: String,
  val evidenceHash: String,
  val assessmentMethod: String,
  val careerRequirementTarget: String,
  val freshnessDays: Int,
  val isStillFresh: Boolean
)

data class MasteryGate(
  val gateTitle: String,
  val requiredProofList: List<String>,
  val isUnlocked: Boolean,
  val unlockedScenarioName: String
)

data class BossIncident(
  val bossId: String = UUID.randomUUID().toString(),
  val title: String,
  val threatActor: String,
  val multiStagePhases: List<String>,
  val masteryReward: String,
  val isConquered: Boolean = false
)

/**
 * 15. PERSONAL KNOWLEDGE VAULT, AI MEMORY & PRIVACY & OBSERVABILITY
 */
data class VaultKnowledgeEntry(
  val id: String = UUID.randomUUID().toString(),
  val title: String,
  val type: String, // Note, Bookmark, Highlight, Flashcard, Research, LabFinding, MistakeRecord
  val content: String,
  val linkedGraphNodeIds: List<String> = emptyList(),
  val dateCreated: String = "2026-08-28"
)

data class AiMemoryConsentState(
  val isPersonalizationEnabled: Boolean = true,
  val rememberedItems: List<String>,
  val retentionRationale: String = "Personalized learning paths tailored without storing sensitive credentials"
)

data class PrivacyAuditState(
  val dataCategoriesStored: List<String>,
  val telemetryScope: String = "On-device Room database persistence with zero unauthorized egress",
  val thirdPartyServices: List<String> = emptyList(),
  val exportAvailable: Boolean = true
)

data class PerformanceTelemetrySnapshot(
  val startupStatus: String = "PASS (<420ms)",
  val memoryStatus: String = "PASS (<110MB)",
  val cpuStatus: String = "PASS (<6% avg)",
  val batteryImpact: String = "LOW",
  val uiRenderFps: Int = 60,
  val aiLatencyMs: Int = 165,
  val crashFreeRate: String = "100%"
)

/**
 * 16. AI AGENT EVALUATION & RED TEAMING
 */
data class AiAgentEvaluationScore(
  val roleName: String,
  val accuracyScore: Int,
  val consistencyScore: Int,
  val safetyScore: Int,
  val hallucinationRate: String,
  val citationGroundingScore: Int,
  val promptInjectionDefenseRate: String = "100%",
  val scoringReliability: Int
)

data class AiRedTeamTestCase(
  val testName: String,
  val attackVector: String,
  val simulatedPayload: String,
  val expectedDefenseAction: String,
  val actualDefenseResult: String,
  val isShieldVerified: Boolean = true
)

/**
 * 17. MULTI-FACETED TRUST SCORE & DISCOVERY ENGINE
 */
data class AegoraMultiFacetedTrustScore(
  val skillName: String,
  val capabilityLevel: String,
  val evidenceConfidence: String,
  val evidenceFreshness: String,
  val verificationMethods: List<String>
)

data class DiscoveryInsight(
  val insightTitle: String,
  val reasonExplanation: String,
  val suggestedAction: String
)

/**
 * 18. DAILY BRIEF, WEEKLY DEBRIEF & MONTHLY REVIEW
 */
data class DailyBriefSnapshot(
  val todaysMission: String,
  val skillAtRisk: String,
  val newCyberIntel: String,
  val careerOpportunity: String,
  val projectProgress: String,
  val recommendedAction: String
)

data class WeeklyDebriefSnapshot(
  val whatImproved: List<String>,
  val whatDeclined: List<String>,
  val whatWasPracticed: List<String>,
  val whatWasForgotten: List<String>,
  val mistakePatternsDetected: List<String>,
  val evidenceCreatedCount: Int,
  val careerProgressSummary: String,
  val recommendedNextWeekSprint: String
)

data class MonthlyCapabilityReview(
  val capabilityDeltas: Map<String, String>,
  val majorAchievements: List<String>,
  val persistentWeaknesses: List<String>,
  val transferabilityGrowth: String,
  val evidenceGrowthCount: Int,
  val careerReadinessDelta: String,
  val strategicDirectionRecommendation: String
)

/**
 * 19. LEARNER PROFICIENCY TIERS
 */
enum class LearnerProficiencyTier(val title: String, val philosophy: String) {
  BEGINNER("Beginner", "Guided, zero-jargon, visual, high-scaffolding, safe exploration"),
  INTERMEDIATE("Intermediate", "Growing autonomy, evidence generation, less scaffolding"),
  ADVANCED("Advanced", "High ambiguity, research-driven, crisis pressure, cross-domain"),
  EXPERT("Expert", "Architecture, leadership, complex forensics, mentorship & research")
}
