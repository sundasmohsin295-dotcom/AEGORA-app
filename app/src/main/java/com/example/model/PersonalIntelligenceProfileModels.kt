package com.example.model

import java.util.UUID

// ============================================================
// AEGORA PERSONAL INTELLIGENCE & CAPABILITY GRAPH DATA MODELS
// Globally Scalable Cyber Professional Capability Platform
// ============================================================

/**
 * 4 CORE COGNITIVE CAPABILITY CLUSTERS
 */
enum class CognitiveClusterType(
  val displayName: String,
  val subtitle: String,
  val benchmarkWeight: Float
) {
  FOUNDATION(
    "Foundation",
    "Protocol architecture, systems thinking, tool fluency, theoretical models",
    0.25f
  ),
  ACTIVE_DEFENSE(
    "Active Defense",
    "SIEM triage, detection engineering, incident response, containment",
    0.30f
  ),
  GENERALIZATION_STRESS(
    "Generalization & Stress",
    "Cross-telemetry transferability, crisis composure, adversarial noise resistance",
    0.25f
  ),
  METACOGNITIVE_STRATEGIC(
    "Metacognitive & Strategic",
    "Confidence calibration, mistake DNA resistance, long-term retention, leadership",
    0.20f
  )
}

/**
 * Status of an individual node in the Capability Graph
 */
enum class CapabilityNodeStatus {
  VERIFIED,      // Evidence demonstrated and verified
  EMERGING,      // Baseline understanding demonstrated, needs transfer test
  AT_RISK,       // Knowledge demonstrated in past but decaying without recent proof
  BOTTLENECK,    // Limiting downstream capabilities
  BLOCKED        // Upstream prerequisite is insufficient
}

/**
 * Node in the interactive Causal Capability Dependency Graph
 */
data class CapabilityDependencyNode(
  val id: String,
  val name: String,
  val cluster: CognitiveClusterType,
  val demonstratedScore: Int, // 0 - 100
  val status: CapabilityNodeStatus,
  val upstreamDependencyIds: List<String> = emptyList(),
  val downstreamImpactIds: List<String> = emptyList(),
  val downstreamImpactSummary: String,
  val failureRateDownstream: Int, // e.g. 18% error increase if weak
  val telemetryRequirement: String,
  val recommendedMissionId: String,
  val missionEstimatedMinutes: Int = 12
)

/**
 * Detailed explainability summary for each of the 4 cognitive clusters
 */
data class ClusterCapabilitySummary(
  val cluster: CognitiveClusterType,
  val score: Int, // 0 - 100
  val trendLabel: String, // e.g. "+8% this week", "Transfer gate bottleneck"
  val plainEnglishMeaning: String,
  val whatLearnerKnows: List<String>,
  val whatLearnerCanPerform: List<String>,
  val strugglePoints: List<String>,
  val untransferredConcepts: List<String>,
  val uncertaintyFrontiers: List<String>,
  val recentEvidence: List<EvidenceProofItem>,
  val learningPatterns: List<String>,
  val topGrowthMissions: List<String>
)

/**
 * Concrete, verifiable evidence unit supporting capability claims
 */
data class EvidenceProofItem(
  val id: String = UUID.randomUUID().toString(),
  val capabilityId: String,
  val capabilityName: String,
  val proofType: String, // "Live Telemetry Triage", "PCAP Forensic Extraction", "CloudTrail Pivot"
  val timestamp: String,
  val freshnessDays: Int,
  val telemetrySnippet: String,
  val verifiedHash: String, // SHA-256 simulation signature
  val confidenceScore: Int, // 0 - 100
  val isCryptographicallySigned: Boolean = true
)

/**
 * Cognitive Failure Modes for Failure Intelligence
 */
enum class FailureModeType(val label: String, val badgeColorHex: String) {
  KNOWLEDGE_GAP("Knowledge Gap", "#3B82F6"),
  REASONING_ERROR("Causal Reasoning Error", "#F59E0B"),
  PROCEDURAL_ERROR("Procedural / Order of Ops", "#EF4444"),
  PATTERN_RECOGNITION_ERROR("Obfuscation / Pattern Miss", "#8B5CF6"),
  TRANSFER_FAILURE("Transfer Failure (Cross-Context)", "#EC4899"),
  OVERCONFIDENCE("Overconfidence Calibration", "#F97316"),
  UNCERTAINTY_PARALYSIS("Uncertainty Paralysis", "#6B7280"),
  INCOMPLETE_INVESTIGATION("Incomplete Investigation", "#10B981"),
  PREMATURE_CONCLUSION("Premature Conclusion", "#E11D48"),
  // Observable Task-Behavior Categories (Phase 2 Deterministic Failure Patterns)
  PREMATURE_ESCALATION("Premature Escalation", "#DC2626"),
  EVIDENCE_OVERWEIGHTING("Evidence Overweighting", "#D97706"),
  CONFIRMATION_BIAS("Confirmation Bias", "#7C3AED"),
  INSUFFICIENT_CORRELATION("Insufficient Correlation", "#2563EB"),
  WEAK_UNCERTAINTY_HANDLING("Weak Uncertainty Handling", "#4B5563"),
  CONTEXT_IGNORANCE("Context Ignorance", "#EA580C"),
  INCORRECT_PRIORITIZATION("Incorrect Prioritization", "#059669")
}

/**
 * Failure Intelligence Record that turns mistakes into high-value evidence
 */
data class MistakeIntelligenceRecord(
  val id: String = UUID.randomUUID().toString(),
  val timestamp: String,
  val missionTitle: String,
  val failureType: FailureModeType,
  val observedSymptom: String,
  val rootCauseCausalLink: String,
  val constructiveFeedback: String,
  val targetedRemediationMission: String,
  val capabilityImpactLabel: String,
  val remediationMinutes: Int = 8
)

/**
 * Retention status for Long-term Learning Memory
 */
enum class RetentionHealthStatus(val label: String, val urgencyColorHex: String) {
  FRESH("Demonstrated Recently", "#10B981"),
  DRIFTING("Evidence Aging (14+ days)", "#3B82F6"),
  AT_RISK("Decay Risk (30+ days)", "#F59E0B"),
  NEEDS_REACTIVATION("Reactivation Recommended", "#EF4444")
}

/**
 * Long-term Learning Memory Item with constructive retention framing
 */
data class LearningMemoryItem(
  val id: String = UUID.randomUUID().toString(),
  val topic: String,
  val cluster: CognitiveClusterType,
  val lastDemonstratedTimestamp: String,
  val daysSinceDemonstrated: Int,
  val retentionHealth: RetentionHealthStatus,
  val decayMessage: String,
  val reactivationMissionTitle: String,
  val reactivationMissionMinutes: Int,
  val previousMistakesOvercome: List<String>,
  val successfulStrategies: List<String>
)

/**
 * "PROVE IT" on-demand verification challenge
 */
data class ProveItChallenge(
  val id: String,
  val capabilityName: String,
  val cluster: CognitiveClusterType,
  val difficulty: String, // "Warm-up", "Challenge", "Stress Test", "Transfer", "Mastery"
  val telemetryEnvironment: String, // "AWS CloudTrail", "Windows Sysmon", "Zeek Network PCAP"
  val rawTelemetryLog: String,
  val challengePrompt: String,
  val triageOptions: List<ProveItOption>,
  val verifiedCryptographicProof: String
)

data class ProveItOption(
  val id: String,
  val actionTitle: String,
  val isOptimal: Boolean,
  val justification: String,
  val failureModeIfChosen: FailureModeType?
)

/**
 * Career Intelligence Role with current capability mapping
 */
data class CareerIntelligenceProfile(
  val roleId: String,
  val title: String,
  val requiredClusterScores: Map<CognitiveClusterType, Int>,
  val currentClusterScores: Map<CognitiveClusterType, Int>,
  val overallMatchPercentage: Int,
  val primaryBottleneckGap: String,
  val targetedMissionSequence: List<String>,
  val readinessIndex: Int
)

/**
 * Real-World Operational Simulation Environment
 */
enum class OperationalSimulationType(val displayName: String, val iconDescription: String) {
  SOC_ALERT_TRIAGE("SOC Mode", "Live alert queue, false-positive filtering, SIEM pivoting"),
  INCIDENT_RESPONSE("Incident Mode", "Host isolation, firewall containment, executive brief"),
  THREAT_HUNTING("Threat Hunt Mode", "Hypothesis-driven search across endpoint event logs"),
  DIGITAL_FORENSICS("Forensics Mode", "Memory volatility analysis, MFT disk artifact recovery"),
  CLOUD_TELEMETRY("Cloud Mode", "AWS CloudTrail & Kubernetes audit log correlation"),
  RED_BLUE_ARENA("Purple Team Arena", "Simulate adversary execution path & defensive detection")
}

data class OperationalEnvironmentScenario(
  val id: String = UUID.randomUUID().toString(),
  val type: OperationalSimulationType,
  val title: String,
  val targetHost: String,
  val telemetryData: String,
  val activeObjectives: List<String>,
  val timePressureSeconds: Int,
  val simulatedToolCommands: List<String>,
  val difficultyLabel: String
)

/**
 * Multiplayer Collaborative Incident Session (Privacy-Isolated)
 */
data class CollaborativeIncidentSession(
  val sessionId: String = UUID.randomUUID().toString(),
  val scenarioTitle: String,
  val learnerAssignedRole: String, // "Incident Commander", "Threat Hunter", "SOC Analyst", "Forensics Specialist"
  val teamMembers: List<TeamMemberStatus>,
  val incidentTimeline: List<String>,
  val individualContributionScore: Int,
  val teamCoordinationScore: Int,
  val isolationGuarantee: String = "Individual learner evidence is evaluated strictly from direct actions and never corrupted by teammates."
)

data class TeamMemberStatus(
  val callsign: String,
  val role: String,
  val activeAction: String,
  val status: String
)

/**
 * University and Employer Institutional Views
 */
data class UniversityCohortAnalytics(
  val cohortName: String,
  val totalLearners: Int,
  val clusterAverages: Map<CognitiveClusterType, Int>,
  val curriculumGaps: List<String>,
  val highPerformingConcepts: List<String>,
  val privacyStatement: String = "Strictly aggregated capability indicators. Zero individual psychological or mistake profiling exposed."
)

data class EmployerCandidateVerification(
  val candidateCallsign: String,
  val passportId: String,
  val targetRole: String,
  val verifiedClusterScores: Map<CognitiveClusterType, Int>,
  val verifiedEvidenceCount: Int,
  val verifiedAuditHash: String,
  val verificationAuthority: String = "AEGORA Verifiable Capability Ledger v12.0 (SHA-256 Verified)"
)

/**
 * Progress Narrative Timeline: Explaining personal growth
 */
data class ProgressNarrativeMilestone(
  val timeframe: String, // "30 Days Ago", "14 Days Ago", "Today", "Next Frontier"
  val title: String,
  val narrative: String,
  val capabilityDelta: String,
  val nextRecommendedAction: String
)

/**
 * Discovery Engine: Proactive insights into strengths & dependencies
 */
enum class DiscoveryType {
  HIDDEN_STRENGTH,
  CRITICAL_DEPENDENCY,
  UNKNOWN_FRONTIER
}

data class DiscoveryInsightItem(
  val id: String = UUID.randomUUID().toString(),
  val type: DiscoveryType,
  val headline: String,
  val explanation: String,
  val actionText: String,
  val destinationTag: String
)

/**
 * Trust Center & Explainability
 */
data class TrustAuditItem(
  val category: String,
  val whatIsStored: String,
  val whyStored: String,
  val exportable: Boolean = true,
  val canBeDeleted: Boolean = true
)

/**
 * UI State for Personal Intelligence Profile
 * Supports loading, empty state, partial evidence, ready, and error states.
 */
sealed class PersonalIntelligenceUiState {
  data object Loading : PersonalIntelligenceUiState()

  data class Empty(
    val learnerId: String,
    val title: String = "AEGORA is still learning what you can do",
    val message: String = "No verified capability evidence has been recorded for this profile yet. Complete diagnostic baselines or live missions to build your verifiable capability identity.",
    val recommendedFirstActions: List<String> = listOf(
      "Execute Network Protocol Packet Triage Baseline",
      "Complete Sysmon & Process Lineage Diagnostic",
      "Take on-demand 'Prove It' verification challenge"
    )
  ) : PersonalIntelligenceUiState()

  data class Ready(
    val learnerId: String,
    val clusterSummaries: List<ClusterCapabilitySummary>,
    val primaryBottleneckCluster: CognitiveClusterType?,
    val isPartialEvidence: Boolean = false,
    val partialEvidenceWarning: String? = null
  ) : PersonalIntelligenceUiState()

  data class Error(
    val message: String,
    val recoverableAction: String = "Retry Loading Profile"
  ) : PersonalIntelligenceUiState()
}

