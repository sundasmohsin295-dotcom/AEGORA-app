package com.example.model

/**
 * AEGORA v6.2 Comprehensive Domain Architecture Models
 * Grounded in verified evidence, multi-dimensional skill evaluation, and zero-trust principles.
 */

// ============================================================================
// PHASE 5: CAREER ARCHITECTURE & ROLE REGISTRY
// ============================================================================

enum class RoleFamily(val displayName: String, val badgeColor: Long) {
  BLUE_TEAM("Blue Team (Defensive)", 0xFF00F0FF),
  RED_TEAM("Red Team (Offensive)", 0xFFFF5252),
  SECURITY_ENGINEERING("Security Engineering", 0xFF8B5CF6),
  GRC("Governance, Risk & Compliance", 0xFFFFB800),
  LEADERSHIP("Leadership & Strategy", 0xFF00E676),
  SPECIALIZED("Specialized Security Domains", 0xFFEC4899)
}

enum class CareerDifficulty(val label: String) {
  ENTRY_LEVEL("Entry Level (L1)"),
  ASSOCIATE("Associate (L2)"),
  SENIOR("Senior Specialist (L3)"),
  STAFF_PRINCIPAL("Staff / Principal Lead"),
  EXECUTIVE("Executive / Strategic")
}

data class DetailedCareerRole(
  val id: String,
  val title: String,
  val family: RoleFamily,
  val difficulty: CareerDifficulty,
  val description: String,
  val responsibilities: List<String>,
  val requiredSkills: List<String>,
  val primaryTools: List<String>,
  val standardFrameworks: List<String>,
  val learningPaths: List<String>,
  val recommendedLabs: List<String>,
  val capstoneProject: String,
  val targetCertifications: List<String>,
  val industryJobRequirements: List<String>,
  val sampleInterviewQuestions: List<String>,
  val evidenceRequirements: List<String>,
  val marketDemandRating: String = "High Demand"
)

// ============================================================================
// PHASE 6: MULTI-DIMENSIONAL SKILL GENOME
// ============================================================================

enum class EvidenceStrength(
  val label: String,
  val weightMultiplier: Double = 1.0,
  val badgeColor: Long = 0xFF00F0FF
) {
  UNKNOWN("UNKNOWN", 0.0, 0xFF64748B),
  LOW("Low (Self-Reported / Basic Quiz)", 0.25, 0xFF64748B),
  MEDIUM("Medium (Guided Labs Completed)", 0.65, 0xFFFFB800),
  HIGH("High (Independent Unscripted Labs)", 1.00, 0xFF00F0FF),
  VERIFIED_PROOF("Verified (Cryptographic / Capstone Proof)", 1.50, 0xFF00E676),
  WEAK("Weak (Passive / Quiz)", 0.25, 0xFF64748B),
  MODERATE("Moderate (Guided Lab / Active Recall)", 0.65, 0xFFFFB800),
  STRONG("Strong (Independent Lab / SOC Triage)", 1.00, 0xFF00F0FF),
  EXPERT_DEMONSTRATED("Expert Demonstrated (Novel Scenario / CTF / Incident)", 1.50, 0xFF00E676)
}

data class DimensionalSkillMetric(
  val score: Int?, // null represents UNKNOWN instead of inventing a score
  val isUnknown: Boolean = score == null
) {
  val displayValue: String get() = if (score == null) "UNKNOWN" else "$score"
}

data class SkillGenome(
  val skillId: String,
  val skillName: String,
  val category: String,
  val knowledge: DimensionalSkillMetric,
  val practicalAbility: DimensionalSkillMetric,
  val reasoning: DimensionalSkillMetric,
  val transfer: DimensionalSkillMetric,
  val retention: DimensionalSkillMetric,
  val independence: DimensionalSkillMetric,
  val confidenceCalibration: DimensionalSkillMetric,
  val evidenceStrength: EvidenceStrength,
  val lastObservedEvidenceDate: String? = null
)

// ============================================================================
// PHASE 7: CYBER TWIN (OBSERVED LEARNING PATTERNS)
// ============================================================================

data class ObservedLearningPatternSummary(
  val learnerCallsign: String,
  val observedStrengths: List<String>,
  val observedWeaknesses: List<String>,
  val fragileSkills: List<String>,
  val unknownSkills: List<String>,
  val careerInterests: List<String>,
  val recentLearningHistory: List<String>,
  val reasoningPatterns: List<String>,
  val activeSkillDecayList: List<String>,
  val verifiedEvidenceArtifacts: List<String>,
  val currentIndependenceLevel: String,
  val recommendedNextActions: List<String>
)

// ============================================================================
// PHASE 8: NEXT BEST ACTION ENGINE
// ============================================================================

enum class ActionType(val label: String, val iconName: String) {
  LESSON("Core Architecture Lesson", "MenuBook"),
  PRACTICE("Guided Skill Practice", "Code"),
  LAB("Hands-On Incident Lab", "Terminal"),
  REVIEW("Spaced Retrieval Review", "Psychology"),
  TRANSFER_CHALLENGE("Unscripted Transfer Challenge", "FlashOn"),
  PROJECT("Portfolio Capstone Project", "FolderZip"),
  CAREER_PREPARATION("Career & Role Alignment", "Work"),
  INTERVIEW("Technical Mock Interview", "RecordVoiceOver"),
  CERTIFICATION_PREP("Certification Exam Readiness", "FactCheck")
}

data class CalculatedAction(
  val id: String,
  val title: String,
  val type: ActionType,
  val targetSkillOrTopic: String,
  val estimatedMinutes: Int,
  val rationaleReason: String,
  val urgencyScore: Int, // 0 - 100
  val navigationRoute: String
)

data class NextBestActionSession(
  val primaryAction: CalculatedAction,
  val optionalActions: List<CalculatedAction> // Exactly 3 options
)

// ============================================================================
// PHASE 9: ADAPTIVE LEARNING MODES
// ============================================================================

enum class AdaptiveLearningMode(val displayName: String, val triggerWeakness: String, val pedagogicalFocus: String) {
  EXPLAIN("Explanation & Theory", "Knowledge Weakness", "Foundational mental models, RFC deep-dives, and mechanism breakdowns"),
  VISUAL("Interactive Architecture", "Spatial / Concept Blur", "Visual packet flow diagrams, topology maps, and interactive data structures"),
  PRACTICE("Targeted Practice", "Execution Inconsistency", "Step-by-step terminal execution, command repetition, and syntax drill"),
  CASE_STUDY("Real-World Case Study", "Contextual Gap", "Post-mortem analysis of actual APT breaches and executive response decisions"),
  SIMULATION("Live SOC / Red Range", "Practical Ability Weakness", "High-stress terminal emulation with real-time log pipelines and active alarms"),
  TEACH_BACK("Teach-Back Explanation", "Communication Weakness", "Articulate threat impact and remediation reasoning clearly as if to an executive"),
  RETRIEVAL("Spaced Flash Retrieval", "Retention Decay", "Active recall drills and flashcard challenges targeting memory decay curves"),
  TRANSFER("Novel Transfer Challenge", "Transfer Inflexibility", "Apply familiar techniques in completely unfamiliar, unscripted environments"),
  INTERVIEW("Technical Defense Interview", "Career Readiness Gap", "Defend technical choices under pressure against AI Assessors"),
  EXAM("Proctored Knowledge Exam", "Certification Readiness", "Timed diagnostic assessments mapped directly to industry certification domains"),
  RESEARCH("Deep Threat Research", "Specialist Mastery", "Deconstruct zero-day CVE advisories, reverse engineer exploits, and craft detection rules")
}

// ============================================================================
// PHASE 11: REASONING GRAPH & INVESTIGATION LOGS
// ============================================================================

data class InvestigationReasoningStep(
  val stepNumber: Int,
  val evidenceViewed: String,
  val formedHypothesis: String,
  val executedAction: String,
  val observedResult: String,
  val revisedHypothesis: String,
  val stepConfidence: Int // 0 - 100
)

data class InvestigationReasoningTrail(
  val investigationId: String,
  val incidentTitle: String,
  val steps: List<InvestigationReasoningStep>,
  val finalConclusion: String,
  val outcomeAccuracy: String, // "ACCURATE", "PARTIAL", "INACCURATE"
  val identifiedReasoningPattern: String,
  val isPrivateToLearner: Boolean = true // Private by default to respect learner autonomy
)

// ============================================================================
// PHASE 12: MISTAKE DNA (OBSERVED LEARNING PATTERNS)
// ============================================================================

enum class ObservedMistakePattern(
  val patternName: String,
  val neutralDescription: String,
  val correctiveGuidance: String
) {
  PREMATURE_CLOSURE(
    "Observed Pattern: Premature Closure",
    "Tendency to stop investigation at the first plausible explanation without verifying lateral movement.",
    "Formulate at least two alternative hypotheses before executing containment actions."
  ),
  CONFIRMATION_BIAS_PATTERN(
    "Observed Pattern: Confirmation Bias Tendency",
    "Selecting evidence that exclusively supports initial suspicion while disregarding contradictory telemetry.",
    "Actively query benign baseline metrics to falsify your leading hypothesis."
  ),
  TUNNEL_VISION_PATTERN(
    "Observed Pattern: Tunnel Vision",
    "Deeply analyzing a single suspicious process or IP while missing simultaneous alerts on adjacent subnets.",
    "Conduct a periodic 15-minute global perimeter sweep during high-intensity triage."
  ),
  WEAK_PRIORITIZATION(
    "Observed Pattern: Low-Severity Distraction",
    "Spending significant time on low-impact anomalies while critical exfiltration alerts remain unacknowledged.",
    "Filter triage queue strictly by asset criticality and threat actor impact score."
  ),
  EVIDENCE_CORRELATION_WEAKNESS(
    "Observed Pattern: Isolated IOC Analysis",
    "Analyzing individual alerts in isolation rather than chaining telemetry across network, auth, and host logs.",
    "Pivot from single alerts to multi-source timeline construction across auth and DNS."
  ),
  TOOL_FIXATION(
    "Observed Pattern: Tool-Specific Fixation",
    "Over-relying on automated tool verdicts without validating underlying raw packet or memory artifacts.",
    "Inspect raw payload bytes and memory hex dumps when automated scores show ambiguity."
  ),
  OVERCONFIDENCE(
    "Observed Pattern: Low Evidence Calibration (Overconfident)",
    "Assigning high confidence to conclusions with limited supporting forensic indicators.",
    "Require at least two independent telemetry sources before declaring a false or true positive."
  ),
  UNDERCONFIDENCE(
    "Observed Pattern: Action Hesitation (Underconfident)",
    "Delaying critical containment actions despite clear, overwhelming IOC alignment.",
    "Review standard operating playbooks to build certainty in high-confidence automated indicators."
  )
}
