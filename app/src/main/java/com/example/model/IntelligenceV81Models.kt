package com.example.model

/**
 * AEGORA v8.1 — THE INTELLIGENCE CONNECTIVE LAYER MODELS
 * Unified data structures for:
 * 1. Evidence Engine & Verification
 * 2. Mastery Validation Engine (L0 to L9)
 * 3. Cyber Twin 3.0 (9 Multi-Dimensional Vectors + Explainability)
 * 4. Adaptive Learning Strategy Selector (18 Pedagogical Models)
 * 5. Mistake DNA 2.0 & Cognitive Autopsies
 * 6. Reasoning Graph 2.0 (Thinking Process vs Reference Model)
 * 7. Time-Aware Learning Selector (5m, 15m, 30m, 1h, 3h)
 * 8. Student Cognitive State Engine
 * 9. Reverse Job Roadmap & Skill Gap Analyzer
 * 10. Project Evidence Cards (CV & Skill Passport Ready)
 * 11. Weekly Personal Cyber Intelligence Report
 * 12. Developer Intelligence Trace & Observability
 */

// ============================================================================
// 1. UNIFIED EVIDENCE ENGINE & VERIFICATION
// ============================================================================

enum class EvidenceSourceType(val displayName: String) {
  LESSON_COMPLETION("Interactive Lesson"),
  QUIZ_RESULT("Knowledge Recall Quiz"),
  FLASHCARD_RECALL("Spaced Repetition Recall"),
  GUIDED_LAB("Guided Hands-on Lab"),
  INDEPENDENT_LAB("Independent Challenge Lab"),
  SOC_INVESTIGATION("Live SOC Triage Simulation"),
  INCIDENT_RESPONSE("Crisis Incident Response"),
  PURPLE_TEAM_DUEL("Purple Team Self-vs-Self Duel"),
  CODE_REVIEW("Security Code Audit"),
  THREAT_HUNTING("Threat Hunting Exercise"),
  VOICE_EXPLANATION("Voice Crisis / Executive Briefing"),
  TECHNICAL_INTERVIEW("Technical Scenario Interview"),
  CTF_FLAG("Capture The Flag Challenge"),
  PROJECT_SUBMISSION("Architecture & Capstone Project"),
  DFIR_MEMORY_FORENSICS("Memory & Disk Forensic Audit"),
  CLOUD_SECURITY_AUDIT("Cloud & Container Guardrail Test"),
  GRC_ASSESSMENT("GRC & Compliance Framework Audit")
}

enum class EvidenceVerificationStatus(val label: String) {
  UNVERIFIED("Unverified"),
  AUTOMATED_SANDBOX_VERIFIED("Verified by Sandbox Engine"),
  PEER_AUDITED("Peer Audited"),
  CRYPTOGRAPHICALLY_SEALED("Cryptographically Sealed")
}

data class EvidenceItem(
  val id: String,
  val timestamp: String,
  val activityTitle: String,
  val skillDomain: String,
  val subskill: String,
  val difficultyLevel: String, // "Beginner", "Intermediate", "Advanced", "Expert"
  val scoreAchieved: Int, // 0 - 100
  val mistakesRecordedCount: Int,
  val reasoningQualityScore: Int, // 0 - 100
  val confidenceStated: String, // "HIGH", "MEDIUM", "LOW"
  val timeSpentSeconds: Int,
  val attemptNumber: Int,
  val strength: EvidenceStrength,
  val sourceType: EvidenceSourceType,
  val verificationStatus: EvidenceVerificationStatus,
  val proofSnippet: String
)

// ============================================================================
// 2. MASTERY VALIDATION ENGINE (L0 to L9 Progression)
// ============================================================================

enum class MasteryLevel(val levelCode: String, val title: String, val description: String, val requiredProofCount: Int) {
  L0_UNEXPOSED("L0", "Unexposed", "No recorded interaction with this concept.", 0),
  L1_AWARE("L1", "Aware", "Encountered in curriculum or threat intelligence.", 1),
  L2_UNDERSTANDS("L2", "Understands", "Can explain RFC protocol and theoretical mechanics.", 2),
  L3_GUIDED_PRACTICE("L3", "Guided Practice", "Completed scaffolded labs with step-by-step guidance.", 3),
  L4_INDEPENDENT_PRACTICE("L4", "Independent Practice", "Solved unstructured challenge labs without hints.", 5),
  L5_RELIABLE_PRACTITIONER("L5", "Reliable Practitioner", "Consistently triages true vs false positives under normal conditions.", 7),
  L6_APPLIED("L6", "Applied", "Applied skills during multi-stage incident containment.", 10),
  L7_ADVANCED("L7", "Advanced", "Demonstrated cross-domain transfer to novel environments.", 14),
  L8_SPECIALIST("L8", "Specialist", "Authored detection rules, automated playbooks, or threat advisories.", 18),
  L9_MASTERY_DEMONSTRATED("L9", "Mastery Demonstrated", "Teaches, debriefs others, and solves zero-day scenarios under pressure.", 24)
}

data class TopicMasteryNode(
  val topicId: String,
  val topicName: String,
  val domain: String,
  val currentMastery: MasteryLevel,
  val masteryProgressPercent: Int,
  val completedProofTypes: List<EvidenceSourceType>,
  val missingProofRequirements: List<String>,
  val transferDemonstrated: Boolean,
  val antiGamingPassRate: Double // e.g. 1.0 (no low-effort farming detected)
)

// ============================================================================
// 3. CYBER TWIN 3.0 (9 Competency Dimensions + Explainability)
// ============================================================================

data class CompetencyDimension30(
  val dimensionKey: String,
  val title: String,
  val score: Int, // 0 - 100
  val benchmarkTarget: Int,
  val confidenceRating: String, // "HIGH (±3%)", "PROVISIONAL", "CALIBRATING"
  val evidenceCount: Int,
  val recentTrend: String, // "ASCENDING (+6% last 7d)", "STABLE", "DECAYING (-4%)"
  val longTermTrend: String, // "CONSISTENT UPWARD", "PLATEAUED"
  val learningVelocity: String, // "RAPID", "STEADY", "DELIBERATE"
  val decayRisk: String, // "CRITICAL", "MODERATE", "LOW"
  val consistencyScore: Int, // 0 - 100
  val lastDemonstratedDate: String,
  val strongestEvidenceProof: String,
  val weakestEvidenceProof: String,
  val recommendedIntervention: String,
  val whyScoreExistsBreakdown: List<String>,
  val whyScoreWeaknessFactors: List<String>
)

data class CyberTwinV81State(
  val callsign: String,
  val targetCareerRole: String,
  val overallCareerReadinessPercent: Int,
  val readinessStatus: String, // "DEVELOPING", "INTERMEDIATE_READY", "JOB_READY"
  val primaryBlocker: String,
  val learningVelocity: String,
  val confidenceCalibrationState: String,
  val evidenceIntegrityScore: Int,
  val dimensions: List<CompetencyDimension30>,
  val lastSyncTimestamp: String
)

// ============================================================================
// 4. ADAPTIVE LEARNING STRATEGY SELECTOR (18 Pedagogical Models)
// ============================================================================

enum class AdaptiveLearningModel(val modelName: String, val shortDesc: String) {
  ACTIVE_RECALL("Active Recall", "Forced memory retrieval without external cues."),
  SPACED_REPETITION("Spaced Repetition", "Ebbinghaus curve-optimized revision intervals."),
  FEYNMAN_TECHNIQUE("Feynman Technique", "Explain complex security concepts in plain, jargon-free English."),
  RETRIEVAL_PRACTICE("Retrieval Practice", "Interleaved memory tests to reinforce long-term storage."),
  DELIBERATE_PRACTICE("Deliberate Practice", "High-repetition focus strictly on the learner's weakest micro-step."),
  SCENARIO_BASED("Scenario-Based Learning", "Real-world simulated incident scenarios with contextual noise."),
  PROBLEM_BASED("Problem-Based Learning", "Open-ended forensic problem solving without a predefined recipe."),
  PROJECT_BASED("Project-Based Learning", "End-to-end architecture creation, testing, and documentation."),
  INTERLEAVING("Interleaving", "Mixing network, host, and cloud investigation topics in one session."),
  PROGRESSIVE_DIFFICULTY("Progressive Difficulty", "Scaffolded escalation from guided tasks to expert challenges."),
  SOCRATIC_QUESTIONING("Socratic Questioning", "Guiding discovery through probing questions rather than answers."),
  CASE_BASED("Case-Based Learning", "Analyzing historical enterprise breach post-mortems."),
  SIMULATION_LEARNING("Simulation Learning", "Dynamic cyber range environments with reactive adversary behavior."),
  TEACH_BACK("Teach-Back Technique", "Student records executive/peer walkthrough explaining threat mechanics."),
  ERROR_BASED("Error-Based Learning", "Cognitive autopsies analyzing where reasoning broke down."),
  REFLECTION("Structured Reflection", "Post-lab analysis of decisions, false leads, and time allocation."),
  COMPARATIVE_LEARNING("Comparative Learning", "Side-by-side analysis of attack techniques (e.g. Pass-the-Hash vs Overpass-the-Hash)."),
  CONCEPT_MAPPING("Concept Mapping", "Visualizing interconnects between protocols, tools, and MITRE TTPs.")
}

data class LearningStrategyRecommendation(
  val detectedCognitiveNeed: String,
  val selectedModel: AdaptiveLearningModel,
  val rationale: String,
  val targetActivity: String
)

// ============================================================================
// 5. MISTAKE DNA 2.0 & COGNITIVE AUTOPSIES
// ============================================================================

enum class MistakeArchetype(val displayName: String, val cognitiveCause: String) {
  PREMATURE_CLOSURE("Premature Closure", "Terminating triage upon finding one benign explanation while missing secondary persistence."),
  CONFIRMATION_BIAS("Confirmation Bias", "Over-indexing on an initial threat hypothesis and ignoring contradictory telemetry."),
  TUNNEL_VISION("Tunnel Vision", "Focusing exclusively on one host and neglecting network lateral movement indicators."),
  ANCHORING("Anchoring Bias", "Fixating on the first alert severity rating rather than raw packet ground truth."),
  WEAK_TIMELINE_REASONING("Weak Timeline Correlation", "Failing to order authentication anomalies chronologically preceding execution."),
  POOR_EVIDENCE_CORRELATION("Poor Evidence Correlation", "Treating Sysmon, DNS, and Firewall logs as disconnected silos."),
  ALERT_FATIGUE("Alert Fatigue", "Batch-closing low severity alerts without spot-checking parent process lineage."),
  OVERCONFIDENCE("Overconfidence Bias", "Rushing isolation decisions without verifying blast radius or business disruption."),
  EXCESSIVE_HESITATION("Excessive Hesitation", "Delaying host containment while attacker actively exfiltrates sensitive tables."),
  SCOPE_NEGLECT("Scope Neglect", "Containing the staging server while ignoring compromised Active Directory domain controller."),
  MISSING_PREREQUISITE("Missing Prerequisite", "Attempting Kerberoasting without foundational understanding of SPNs and Kerberos TGS."),
  TOOL_DEPENDENCE("Tool Dependence", "Relying on automated scanner score without validating manual exploitation feasibility."),
  WEAK_COMMUNICATION("Weak Stakeholder Communication", "Using overly dense technical jargon when briefing executive leadership."),
  FAILURE_TO_VALIDATE_HYPOTHESIS("Unvalidated Hypothesis", "Executing destructive remediation before proving root-cause vector.")
}

data class MistakeDnaEntry(
  val id: String,
  val archetype: MistakeArchetype,
  val occurrenceCount: Int,
  val lastOccurrenceDate: String,
  val contextWhereOccurred: String,
  val detectedPatternDescription: String,
  val correctiveDrillTitle: String,
  val correctiveDrillRoute: String
)

data class MistakeDnaProfile(
  val totalMistakesCataloged: Int,
  val dominantArchetype: MistakeArchetype,
  val highRiskPatterns: List<MistakeDnaEntry>,
  val cognitiveResilienceScore: Int // 0 - 100
)

// ============================================================================
// 6. REASONING GRAPH 2.0 (Thinking Process vs Reference Model)
// ============================================================================

enum class ReasoningGraphStepType(val label: String, val badgeColor: Long) {
  EVIDENCE_INGEST("Evidence Ingest", 0xFF64748B),
  OBSERVATION("Observation", 0xFF00F0FF),
  HYPOTHESIS_FORMED("Hypothesis Formed", 0xFFFFB800),
  HYPOTHESIS_TESTED("Hypothesis Tested", 0xFF8B5CF6),
  HYPOTHESIS_REJECTED("Hypothesis Rejected", 0xFFFF5252),
  DECISION_POINT("Decision Point", 0xFFFF0055),
  ACTION_EXECUTED("Action Executed", 0xFF00E676),
  OUTCOME_VERIFIED("Outcome Verified", 0xFF00B0FF)
}

data class ReasoningStepNode(
  val stepNumber: Int,
  val type: ReasoningGraphStepType,
  val learnerActionDescription: String,
  val referenceModelOptimalAction: String,
  val alignmentVerdict: String, // "OPTIMAL_PATH", "SUBOPTIMAL_DETOUR", "CRITICAL_FLAW"
  val timeSpentSeconds: Int
)

data class ReasoningGraphSession(
  val sessionId: String,
  val incidentScenarioTitle: String,
  val overallThinkingProcessScore: Int, // 0 - 100
  val evidenceFirstScore: Int,
  val timelineConstructionScore: Int,
  val hypothesisFalsificationScore: Int,
  val steps: List<ReasoningStepNode>,
  val aiDebriefInsight: String
)

// ============================================================================
// 7. TIME-AWARE LEARNING SELECTOR
// ============================================================================

enum class TimeAvailabilityOption(val minutes: Int, val label: String, val recommendedFocus: String) {
  MIN_5(5, "5 MIN", "Skill Resurrection & Micro Active Recall"),
  MIN_15(15, "15 MIN", "Concept Collision & Mitre TTP Drill"),
  MIN_30(30, "30 MIN", "SOC Alert Queue & Live Telemetry Triage"),
  HOUR_1(60, "1 HOUR", "Full Purple Team Duel & Multiverse Replay"),
  HOURS_3(180, "3 HOURS", "Deep Architectural Project & Capstone Lab")
}

data class TimeFilteredRecommendation(
  val selectedTime: TimeAvailabilityOption,
  val mission: MissionItem,
  val pedagogicalModel: AdaptiveLearningModel,
  val targetedCompetency: String,
  val expectedScoreDelta: String
)

// ============================================================================
// 8. STUDENT COGNITIVE STATE ENGINE
// ============================================================================

enum class StudentCognitiveState(val displayName: String, val adaptiveAdjustment: String, val badgeColor: Long) {
  CURIOUS("Curious & Exploring", "Provide open-ended knowledge graph exploration and threat intel deep-dives.", 0xFF00F0FF),
  FOCUSED("Deep Flow & Focused", "Present uninterrupted multi-stage challenge labs with minimal scaffolding.", 0xFF00E676),
  STRUGGLING("Cognitive Friction / Struggling", "Switch to Socratic questioning, scaffolded hints, and prerequisite reviews.", 0xFFFF5252),
  OVERCONFIDENT("Tending Overconfident", "Introduce blind scenarios with hidden decoy telemetry and subtle persistence.", 0xFFFFB800),
  HESITANT("Hesitant / Analysis Paralysis", "Introduce timed decision micro-drills to build containment confidence.", 0xFF8B5CF6),
  FATIGUED("Cognitive Fatigue Detected", "Recommend 5-minute flashcard recall or structured reflection rather than deep labs.", 0xFF64748B),
  IMPROVING("Accelerating & Improving", "Increase adversarial evasion complexity and reduce guidance.", 0xFF00B0FF),
  STAGNATING("Plateaued / Stagnating", "Trigger Interleaving challenges and cross-domain transfer scenarios.", 0xFFFF7043),
  READY_FOR_CHALLENGE("Ready for Boss Challenge", "Deploy Purple Team Arena 5-Round Duel or Real SOC Shift Simulator.", 0xFFFF0055)
}

data class StudentStateEstimate(
  val currentState: StudentCognitiveState,
  val confidenceScore: Int, // 0 - 100
  val recentActivityVelocity: String,
  val diagnosticRationale: String,
  val isEstimateOnlyNotice: String = "Estimated from learning interaction telemetry. Not a psychological diagnosis."
)

// ============================================================================
// 9. REVERSE JOB ROADMAP & SKILL GAP ANALYZER
// ============================================================================

data class ExtractedJobRequirement(
  val category: String, // "CORE_SKILL", "TOOL", "FRAMEWORK", "SOFT_SKILL", "CERTIFICATION"
  val name: String,
  val importance: String, // "REQUIRED", "PREFERRED"
  val learnerMasteryStatus: String, // "DEMONSTRATED", "IN_PROGRESS", "MISSING"
  val matchingEvidenceId: String? = null
)

data class V81JobRoadmapAnalysis(
  val extractedRoleTitle: String,
  val targetCompanyOrIndustry: String,
  val overallMatchPercentage: Int,
  val demonstratedSkillsCount: Int,
  val totalRequiredSkillsCount: Int,
  val requirements: List<ExtractedJobRequirement>,
  val missingSkillsSummary: List<String>,
  val evidenceGaps: List<String>,
  val projectGaps: List<String>,
  val interviewGaps: List<String>,
  val shortestPreparationRoadmap: List<String>
)

// ============================================================================
// 10. PROJECT EVIDENCE CARDS (CV & Skill Passport Ready)
// ============================================================================

data class ProjectEvidenceCard(
  val projectId: String,
  val title: String,
  val problemStatement: String,
  val architectureDescription: String,
  val technologiesUsed: List<String>,
  val securityDecisionsMade: List<String>,
  val threatModelFramework: String,
  val verificationProofSnippet: String,
  val gitEvidenceLink: String,
  val demoVerificationBadge: String,
  val lessonsLearned: String,
  val targetCareerFamily: CyberCareerFamily
)

// ============================================================================
// 11. WEEKLY PERSONAL CYBER INTELLIGENCE REPORT
// ============================================================================

data class WeeklyIntelligenceReport(
  val weekLabel: String,
  val totalStudyHours: Double,
  val verifiedEvidenceItemsCount: Int,
  val conceptsMastered: List<String>,
  val conceptsDecaying: List<String>,
  val areasStruggledWith: List<String>,
  val dominantMistakePattern: String,
  val investigationStyleVerdict: String,
  val careerReadinessDelta: String,
  val keyCyberThreatsAnalyzed: List<String>,
  val recommendedNextWeekPlan: List<String>,
  val projectOpportunities: List<String>,
  val interviewReadinessScore: Int
)

// ============================================================================
// 12. DEVELOPER INTELLIGENCE TRACE & OBSERVABILITY
// ============================================================================

data class IntelligenceTraceEntry(
  val traceId: String,
  val timestamp: String,
  val learnerAction: String,
  val evidenceGenerated: String,
  val skillAffected: String,
  val scoreDelta: String,
  val detectedCognitiveSignal: String,
  val recommendationOutput: String
)

data class IntelligencePipelineTrace(
  val isObservabilityEnabled: Boolean = true,
  val recentTraces: List<IntelligenceTraceEntry>
)

// ============================================================================
// 13. CLOUD / OFFLINE SYNC STATUS
// ============================================================================

enum class SyncStatus(val label: String, val badgeColor: Long) {
  LOCAL_ACTIVE("LOCAL SECURE", 0xFF00F0FF),
  SYNC_PENDING("SYNC PENDING", 0xFFFFB800),
  SYNCED("CLOUD SYNCED", 0xFF00E676),
  OFFLINE_MODE("OFFLINE MODE", 0xFF64748B)
}
