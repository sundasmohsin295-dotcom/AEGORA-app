package com.example.capability

import com.example.data.db.MasteryStatus

/**
 * The Seven Mastery Gates forming the evaluation pillars of the Demonstrated Capability Engine.
 */
enum class MasteryGateType(val displayName: String, val description: String) {
  UNDERSTAND("Understand", "Conceptual foundation and theoretical mechanism comprehension"),
  RECALL("Recall", "Accurate, rapid retrieval of security concepts without lookup aids"),
  APPLY("Apply", "Hands-on technical lab and terminal execution in realistic environments"),
  INVESTIGATE("Investigate", "Triage, root-cause deduction, and anomaly hunting in raw telemetry"),
  TRANSFER("Transfer", "Generalization and adaptation to novel scenarios, stacks, and architectures"),
  EXPLAIN("Explain", "Technical writing, root-cause reporting, and executive communication"),
  UNCERTAINTY_RESILIENCE("Uncertainty Resilience", "Calibrated decision making amidst incomplete or conflicting telemetry")
}

/**
 * Epistemic confidence classification for evidence and hypothesis evaluation.
 */
enum class EpistemicUncertaintyState(val label: String, val confidenceMultiplier: Double) {
  CONFIRMED("Confirmed Evidence", 1.0),
  HIGHLY_LIKELY("Highly Likely (>85%)", 0.88),
  LIKELY("Likely (>70%)", 0.72),
  POSSIBLE("Possible (>50%)", 0.50),
  UNKNOWN("Unknown / Ambiguous", 0.25),
  INSUFFICIENT_EVIDENCE("Insufficient Verifiable Telemetry", 0.15),
  CONTRADICTED("Contradicted by Ground Truth", 0.0)
}

/**
 * Retention and decay risk categories for capability monitoring.
 */
enum class RetentionRisk(val label: String, val level: Int) {
  LOW("Low Decay Risk - Freshly Verified", 1),
  MEDIUM("Medium Decay Risk - Review Recommended", 2),
  HIGH("High Decay Risk - Active Degradation", 3),
  CRITICAL("Critical Decay Risk - Urgent Refresher Required", 4)
}

/**
 * Evaluation output for an individual mastery gate.
 */
data class GateResult(
  val gate: MasteryGateType,
  val score: Int,
  val isPassed: Boolean,
  val masteryStatus: MasteryStatus,
  val evidenceCount: Int,
  val confidence: Int,
  val limitingFactor: String? = null,
  val positiveFactor: String? = null
)

/**
 * Detailed telemetry breakdown of evidence evaluation.
 */
data class EvidenceQualityBreakdown(
  val effectiveQualityScore: Int,
  val authenticityScore: Int,
  val independenceScore: Int,
  val transferabilityScore: Int,
  val diversityFactor: Double,
  val verifiedCount: Int,
  val pendingCount: Int,
  val unverifiedCount: Int,
  val revokedExcludedCount: Int,
  val totalConsidered: Int
)

/**
 * Comprehensive, explainable assessment result for a capability.
 */
data class CapabilityAssessmentResult(
  val capabilityId: String,
  val learnerId: String,
  val skillKey: String,
  val name: String,
  val category: String,
  val historicalCapabilityScore: Int, // Stable peak baseline (never wiped by decay)
  val currentConfidence: Int,         // Recency and decay adjusted confidence
  val isDemonstrated: Boolean,        // True only if policy requirements & gates are fully satisfied
  val overallMasteryStatus: MasteryStatus,
  val gateResults: Map<MasteryGateType, GateResult>,
  val limitingGate: MasteryGateType?,
  val evidenceQualityBreakdown: EvidenceQualityBreakdown,
  val retentionRisk: RetentionRisk,
  val daysSinceLastVerified: Int,
  val limitingFactors: List<String>,
  val positiveFactors: List<String>,
  val recommendedAction: String,
  val whyThisScore: String,
  val calculatedAt: Long = System.currentTimeMillis()
)
