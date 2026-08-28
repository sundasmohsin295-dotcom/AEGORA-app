package com.example.model

/**
 * AEGORA Living Intelligence Layer Data Models
 * Genuine, real capabilities with honest data grounding and explainability.
 */

// 1. Living Skill Constellation Node
data class ConstellationNode(
  val skillId: String,
  val skillName: String,
  val domain: String,
  val retentionPercent: Int, // 0 - 100
  val masteryPercent: Int,   // 0 - 100
  val decayRiskLevel: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
  val lastPracticedDaysAgo: Int,
  val normalizedX: Float, // -1.0f to 1.0f
  val normalizedY: Float, // -1.0f to 1.0f
  val normalizedZ: Float, // -1.0f to 1.0f (orbital depth)
  val connectedSkillIds: List<String> = emptyList(),
  val recommendedDiagnosticTitle: String = ""
)

// 2. Ambient Co-Pilot Observation
data class AmbientObservation(
  val id: String,
  val screenContext: String,
  val observationText: String,
  val groundingSource: String, // e.g. "Mistake DNA: Premature Closure", "Decay Radar: Linux Forensics"
  val suggestedPrompt: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isDismissed: Boolean = false
)

// 3. Grounded Predictive Next-Action
data class PredictiveNextAction(
  val id: String,
  val title: String,
  val category: String, // "Decay Prevention", "Roadmap Milestone", "Mistake Remediation", "Spaced Review"
  val destinationTag: String,
  val urgencyScore: Int, // 0 - 100
  val primaryReason: String, // One-line honest data derivation
  val reasoningTags: List<String>, // e.g. listOf("Decay Risk: High", "Roadmap Prerequisite")
  val estimatedMins: Int,
  val xpReward: Int,
  val telemetryMetric: String // e.g. "Retention: 48% (Threshold: 60%)"
)

// 4. Multi-Modal Fusion Visual Evidence Node
data class FusionEvidenceNode(
  val id: String,
  val label: String,
  val category: String, // "PROCESS_TREE", "NETWORK_FLOW", "AUTH_EVENT", "FILE_SYSTEM", "MEMORY_ARTIFACT"
  val details: String,
  val timestampUtc: String,
  val isFlaggedSuspicious: Boolean,
  val isCurrentlyDiscussedInAudio: Boolean = false,
  val visualCoordinates: Pair<Float, Float> = Pair(0f, 0f)
)

data class MultiModalFusionSession(
  val sessionId: String,
  val scenarioTitle: String,
  val attackChainSummary: String,
  val targetRole: String,
  val isLiveAudioActive: Boolean,
  val activeTranscript: List<Pair<String, String>>, // Speaker -> Spoken Text
  val evidenceNodes: List<FusionEvidenceNode>,
  val selectedNodeId: String? = null
)

// 5. Generative Scenario Mutation & Rubric
data class ScenarioRubricEvaluation(
  val mitreAlignmentPassed: Boolean,
  val solvabilityConfidencePercent: Int, // 0 - 100
  val chronologicalIntegrityPassed: Boolean,
  val benignVsMaliciousClarityScore: Int, // 0 - 100
  val reviewerNotes: String
) {
  val isApprovedForLearner: Boolean
    get() = mitreAlignmentPassed && chronologicalIntegrityPassed && solvabilityConfidencePercent >= 80 && benignVsMaliciousClarityScore >= 75
}

data class GeneratedScenarioRecord(
  val id: String,
  val baseConceptTitle: String,
  val generatedTitle: String,
  val targetMitreTactic: String,
  val dynamicIocs: List<String>,
  val targetHostname: String,
  val attackTimestampUtc: String,
  val rawLogPayload: String,
  val rubric: ScenarioRubricEvaluation,
  val generatedAt: String,
  val generationSource: String = "Gemini-3.5-Flash + Rule Verifier"
)

// 6. Flow & Behavioral Pacing Suggestion
data class BehavioralPacingSuggestion(
  val sessionDurationMins: Int,
  val recentMistakeCount: Int,
  val consecutiveTriageCount: Int,
  val shouldSuggestPacing: Boolean,
  val questionPrompt: String,
  val suggestedActionTitle: String,
  val suggestedActionTag: String
)

// 7. Cross-Session Memory Retrospective
data class TopicHistoricalMemory(
  val topicId: String,
  val topicName: String,
  val lastStudiedDate: String,
  val daysSinceLastAttempt: Int,
  val priorScore: Int,
  val pastMistakeNoted: String?,
  val proactiveGuidanceMessage: String
)
