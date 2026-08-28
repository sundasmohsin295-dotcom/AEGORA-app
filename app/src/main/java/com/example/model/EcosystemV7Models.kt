package com.example.model

/**
 * AEGORA v7.0 Production Cyber Intelligence & Ecosystem Models
 * Fully grounded, evidence-driven, scientifically cautious data structures.
 */

// ============================================================================
// 1. CYBER LEARNING GENOME 2.0
// ============================================================================

enum class DimensionTrend(val label: String, val badgeColor: Long) {
  RAPIDLY_ASCENDING("Rapidly Ascending ↑↑", 0xFF00E676),
  STEADY_GROWTH("Steady Growth ↑", 0xFF00F0FF),
  STABLE("Stable ➔", 0xFF8B5CF6),
  AT_RISK_DECAY("At Risk (Decaying) ↓", 0xFFFFB800),
  BLOCKED_BY_PREREQ("Blocked by Prerequisite ⚠", 0xFFFF5252)
}

data class GenomeDimensionNode(
  val dimensionId: String,
  val name: String,
  val score: Int, // 0 - 100
  val evidenceCount: Int,
  val confidenceLevel: String, // "HIGH", "MEDIUM", "LOW", "PROVISIONAL"
  val recentTrend: DimensionTrend,
  val retentionHealthPercent: Int, // 0 - 100
  val weakPrerequisites: List<String> = emptyList(),
  val recommendedIntervention: String,
  val lastDemonstratedDate: String,
  val nextValidationType: String
)

data class CyberLearningGenomeV7(
  val callsign: String,
  val dimensions: List<GenomeDimensionNode>,
  val learningVelocity: String, // "HIGH", "BALANCED", "DELIBERATE"
  val currentPrimaryBottleneck: String,
  val confidenceCalibration: String, // "WELL_CALIBRATED", "TENDING_OVERCONFIDENT", "TENDING_HESITANT"
  val evidenceIntegrityScore: Int, // e.g. 94%
  val nextTargetInterventions: List<String>
)

// ============================================================================
// 2. INVESTIGATION FINGERPRINT & REFERENCE MODEL
// ============================================================================

data class InvestigationFingerprintMetric(
  val metricName: String,
  val learnerPercentage: Int,
  val referenceModelPercentage: Int, // Labeled: "Training Reference Model"
  val evaluationCategory: String, // "COGNITIVE_BIAS", "METHODOLOGY", "TOOL_FLUENCY", "DECISION_TEMPO"
  val observedTendency: String
)

data class InvestigationFingerprintReport(
  val reportId: String,
  val metrics: List<InvestigationFingerprintMetric>,
  val prematureClosureRisk: String, // "LOW", "MEDIUM", "ELEVATED"
  val evidenceFirstRatio: Int, // e.g. 82%
  val timelineUsageRatio: Int, // e.g. 71%
  val iocCorrelationRatio: Int, // e.g. 63%
  val contextValidationRatio: Int, // e.g. 48%
  val hypothesisTestingRatio: Int, // e.g. 77%
  val comparativeAnalysisNote: String = "Compared against AEGORA Training Reference Model (Heuristic Standard)",
  val recommendedRemediationDrill: String
)

// ============================================================================
// 3. MISTAKE DNA 2.0 & LEARNING AUTOPSY
// ============================================================================

enum class MistakeSeverity(val label: String, val badgeColor: Long) {
  CRITICAL("Critical Threat Blindspot", 0xFFFF5252),
  ELEVATED("Elevated Reasoning Flaw", 0xFFFF7043),
  MODERATE("Moderate Inefficiency", 0xFFFFB800),
  LOW_NOISE("Low / Minor Noise", 0xFF64748B)
}

data class MistakeDnaV2Record(
  val id: String,
  val patternName: String,
  val whatHappened: String,
  val whyItHappened: String,
  val causalSkill: String,
  val missingPrerequisite: String,
  val occurrenceCount: Int,
  val severity: MistakeSeverity,
  val trendStatus: String, // "IMPROVING", "PERSISTENT", "NEWLY_EMERGING"
  val remediationDrillTitle: String,
  val remediationLabRoute: String
)

data class LearningAutopsyReport(
  val autopsyId: String,
  val incidentTitle: String,
  val failurePointDescription: String,
  val rootCauseClassification: String,
  val identifiedMistakeDna: String,
  val missingPrerequisiteName: String,
  val evidenceIgnoredOrMissed: List<String>,
  val decisionQualityScore: Int, // 0 - 100
  val confidenceCalibrationAssessment: String,
  val prescribedLessonRoute: String,
  val prescribedRemediationLab: String,
  val retestValidationChallenge: String
)

// ============================================================================
// 4. KNOWLEDGE TRANSFER & CONCEPT COLLISION
// ============================================================================

enum class TransferStatus(val label: String, val badgeColor: Long) {
  DEMONSTRATED("TRANSFER DEMONSTRATED ✓", 0xFF00E676),
  PARTIAL("PARTIAL APPLICATION ⚠", 0xFFFFB800),
  NOT_YET_DEMONSTRATED("NOT YET DEMONSTRATED ✗", 0xFFFF5252)
}

data class KnowledgeTransferTest(
  val id: String,
  val sourceConceptTitle: String,
  val transferScenarioTitle: String,
  val domainContext: String, // e.g. "Cloud Endpoint / Kubernetes Telemetry"
  val transferPrompt: String,
  val measuredDimensions: List<String>, // Recall, Application, Transfer, Generalization
  val currentStatus: TransferStatus,
  val evaluationFeedback: String,
  val remediationDrill: String
)

data class ConceptCollisionPair(
  val id: String,
  val conceptA: String,
  val conceptB: String,
  val confusionRatePercent: Int, // e.g. 78%
  val coreDistinction: String,
  val practicalTrapExample: String,
  val microDrillTitle: String,
  val isResolved: Boolean = false
)

// ============================================================================
// 5. REVERSE JOB ROADMAP & SIMULATION TWIN
// ============================================================================

data class JobSkillMatch(
  val skillName: String,
  val isDemonstrated: Boolean,
  val learnerMasteryPercent: Int?,
  val requiredLevel: String,
  val priorityRank: Int
)

data class ReverseJobRoadmapResult(
  val jobTitle: String,
  val targetCompanyOrSector: String,
  val rawDescriptionSample: String,
  val extractedSkills: List<JobSkillMatch>,
  val aegoraTrainingMatchScore: Int, // 0 - 100 (Labeled: "AEGORA Training Match Score")
  val shortestEvidenceGapPlan: List<String>,
  val recommendedNextProject: String
)

data class JobSimulationReport(
  val reportId: String,
  val rolePersona: String,
  val technicalTriageScore: Int,
  val investigationRigorScore: Int,
  val stakeholderCommunicationScore: Int,
  val documentationQualityScore: Int,
  val decisionMakingUnderPressureScore: Int,
  val prioritizationEfficiencyScore: Int,
  val professionalismScore: Int,
  val overallWorkplaceRating: String, // "READY", "DEVELOPING", "GAPS_FOUND"
  val managerHandoffNotes: String,
  val isSimulatedWorkplaceAssessment: Boolean = true
)

// ============================================================================
// 6. STAKEHOLDER TRANSLATION & CRISIS ROOM
// ============================================================================

enum class StakeholderAudience(val roleTitle: String, val focusDescription: String) {
  SOC_PEER("SOC Analyst (Peer)", "Technical hashes, parent PIDs, raw PCAPs, exact CLI syntax"),
  SECURITY_MANAGER("Security Operations Manager", "Containment status, shift escalation, SLA timeline, resource needs"),
  CTO("Chief Technology Officer", "Architecture impact, infrastructure isolation, system downtime, patch feasibility"),
  CISO("Chief Information Security Officer (CISO)", "Regulatory exposure, threat actor intent, blast radius, board summary"),
  LEGAL_COUNSEL("General / Privacy Legal Counsel", "PII/GDPR breach thresholds, notification clock, chain of custody"),
  EXECUTIVE_CEO("Chief Executive Officer (CEO)", "Customer trust, financial risk, operational continuity, press statement"),
  AFFECTED_CUSTOMER("Enterprise Client / Customer", "Service availability, data safety guarantee, transparent remediation")
}

data class StakeholderTranslationSubmission(
  val audience: StakeholderAudience,
  val learnerSummaryText: String,
  val accuracyScore: Int,
  val clarityScore: Int,
  val businessImpactScore: Int,
  val jargonControlScore: Int,
  val actionabilityScore: Int,
  val feedbackCritique: String
)

data class CrisisWarRoomState(
  val crisisId: String,
  val scenarioTitle: String,
  val activePhase: String,
  val elapsedSimulatedMinutes: Int,
  val businessImpactDollars: Long,
  val compromisedHostsCount: Int,
  val decisionLog: List<Pair<String, String>>, // Timestamp -> Action
  val counterfactualOutcomes: List<String>,
  val isContained: Boolean = false
)

// ============================================================================
// 7. REAL-TIME KNOWLEDGE & CYBER INTELLIGENCE EXPLAINER
// ============================================================================

enum class IntelligenceSourceTier(val label: String, val badgeColor: Long) {
  OFFICIAL_ADVISORY("CISA KEV / NVD CVE", 0xFF00F0FF),
  VENDOR_DISCLOSURE("Vendor Security Advisory", 0xFF8B5CF6),
  RESEARCH_PUBLICATION("Security Research / Whitepaper", 0xFF00E676),
  DEMO_SIMULATION("Educational Simulation (Demo)", 0xFFFFB800),
  BACKEND_INTEGRATION_REQUIRED("External Live Stream (Adapter Ready)", 0xFF64748B)
}

data class CyberIntelligenceExplainerItem(
  val id: String,
  val cveOrThreatTitle: String,
  val sourceTier: IntelligenceSourceTier,
  val publicationDate: String,
  val retrievedTimestamp: String,
  val confidenceScore: Int,
  val whatHappened: String,
  val whoIsAffected: String,
  val whyItMatters: String,
  val technicalRootCause: String,
  val attackPathBreakdown: String,
  val detectionSigmaRule: String,
  val mitigationAndPatch: String,
  val relatedMitreTechniques: List<String>,
  val relatedAegoraSkills: List<String>,
  val practiceLabRoute: String,
  val officialSourceLink: String
)
