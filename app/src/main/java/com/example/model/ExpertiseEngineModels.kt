package com.example.model

/**
 * 10-Tier Mastery Ladder (AEGORA Engine).
 * Progresses the learner from Zero Knowledge to Leader/Researcher with verified capability gates.
 */
enum class ExpertiseLevel(
  val levelNumber: Int,
  val title: String,
  val shortCode: String,
  val description: String,
  val minDemonstratedCapabilities: Int,
  val independenceThresholdPercent: Int,
  val themeColor: Long
) {
  L0_UNFAMILIAR(0, "Unfamiliar (Zero Knowledge)", "L0", "No prior exposure. Focus on computing fundamentals, mental models & OS architecture.", 0, 0, 0xFF64748B),
  L1_AWARE(1, "Aware", "L1", "Understands definitions, security risks, vocabulary, and why security matters.", 5, 20, 0xFF00F0FF),
  L2_UNDERSTANDING(2, "Understanding", "L2", "Grasps system mechanics, data flows, protocols, and conceptual architectures.", 12, 40, 0xFF00E676),
  L3_GUIDED_PRACTICE(3, "Guided Practice", "L3", "Executes tools, scripts, and triage with hint ladders and coach feedback.", 20, 55, 0xFFFFB800),
  L4_INDEPENDENT_PRACTICE(4, "Independent Practice", "L4", "Solves novel, unscripted problems without step-by-step guidance.", 32, 70, 0xFFFF7043),
  L5_PROFESSIONAL(5, "Professional (Job Ready)", "L5", "Demonstrates job-ready speed, evidence preservation, and business communication.", 45, 80, 0xFF8B5CF6),
  L6_ADVANCED(6, "Advanced", "L6", "Handles ambiguous multi-stage attacks, conflicting evidence, and novel evasion.", 60, 88, 0xFFEC4899),
  L7_SPECIALIST(7, "Specialist", "L7", "Deep subject matter expertise in specialized domains (e.g., eBPF, zero-day, ICS/SCADA).", 78, 92, 0xFF06B6D4),
  L8_EXPERT(8, "Expert", "L8", "Authoritative architecture evaluation, crisis war room command, and strategic defense.", 95, 96, 0xFFF59E0B),
  L9_RESEARCHER(9, "Leader / Researcher", "L9", "Discovers novel vulnerability classes, publishes peer-reviewed research & creates tools.", 120, 99, 0xFF10B981)
}

/**
 * Competency status in the Personal Cyber Twin model.
 */
enum class CompetencyStatus(val label: String, val badgeColor: Long) {
  MASTERED("Mastered", 0xFF00E676),
  STRONG("Strong", 0xFF00F0FF),
  DEVELOPING("Developing", 0xFFFFB800),
  WEAK("Needs Practice (Weak)", 0xFFFF5252),
  UNKNOWN("Untested (Unknown)", 0xFF64748B),
  FORGOTTEN("Decaying (Forgotten)", 0xFFFF7043)
}

/**
 * 6-Dimension Capability Matrix.
 */
data class MultiDimensionalCapabilityScore(
  val technicalKnowledge: Int,      // 0 - 100
  val practicalHandsOn: Int,        // 0 - 100
  val hypothesisReasoning: Int,     // 0 - 100
  val investigationDepth: Int,      // 0 - 100
  val businessCommunication: Int,   // 0 - 100
  val professionalJudgment: Int,    // 0 - 100
  val overallIndependenceScore: Int // 0 - 100 (e.g. 86% independent)
)

/**
 * Personal Cyber Twin Profile Node.
 */
data class CyberTwinSkillNode(
  val skillId: String,
  val name: String,
  val domain: String,
  val status: CompetencyStatus,
  val score: Int,
  val currentLevel: ExpertiseLevel,
  val missingPrerequisites: List<String> = emptyList(),
  val whyThisMatters: String,
  val lastPracticedDate: String,
  val verifiedEvidenceArtifact: String? = null
)

/**
 * Prerequisite Diagnostic Path Item.
 */
data class PrerequisiteChain(
  val targetSkill: String,
  val detectedGap: String,
  val rootCause: String, // "Missing Prerequisite: TCP Handshake & Flag Inspection"
  val prescribedStep: String,
  val estimatedMins: Int,
  val actionRoute: String
)

/**
 * "What Would You Do?" Professional Judgment Scenario.
 */
data class RealisticJudgmentScenario(
  val id: String,
  val title: String,
  val rolePersona: String, // "SOC L1 Analyst on Night Shift"
  val situationBrief: String,
  val constraints: List<String>,
  val choices: List<JudgmentChoice>,
  val expertRationaleComparison: String
)

data class JudgmentChoice(
  val id: String,
  val actionText: String,
  val justificationPrompt: String,
  val tradeOffs: String,
  val professionalScore: Int,
  val isOptimal: Boolean
)

/**
 * Cyber Twin State Model.
 */
data class PersonalCyberTwin(
  val callsign: String,
  val targetCareer: String,
  val currentExpertiseLevel: ExpertiseLevel,
  val nextLevelGateRemaining: Int,
  val capabilityMatrix: MultiDimensionalCapabilityScore,
  val skillsGrid: List<CyberTwinSkillNode>,
  val activePrerequisiteChains: List<PrerequisiteChain>,
  val dailyHighValueMission: String,
  val weeklyReviewSummary: String,
  val tutorialIllusionDetected: Boolean = false
)
