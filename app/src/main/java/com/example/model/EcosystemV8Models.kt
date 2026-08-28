package com.example.model

/**
 * AEGORA v8.0 Autonomous Cyber Learning & Career Ecosystem Models
 * Comprehensive data structures for Cyber Twin 2.0, Personal Cyber Mentor,
 * Purple Team Arena ("Self vs Self"), Real SOC Shift Simulator, Concept Graph,
 * 5-Minute Resurrection Engine, Voice SOC Drills, Multiverse Replay, and 18+ Career Tracks.
 */

// ============================================================================
// 1. CYBER TWIN 2.0 (Dynamic Multi-Dimensional Intelligence with Evidence Rationale)
// ============================================================================

data class CompetencyEvidenceVector(
  val dimensionKey: String,
  val title: String,
  val score: Int, // 0 - 100
  val benchmarkTarget: Int,
  val confidenceRating: String, // "HIGH_EVIDENCE", "PROVISIONAL", "CALIBRATING"
  val whyScoreExists: String, // Explicit explanation of proofs
  val proofCount: Int,
  val recentEvidenceSources: List<String>,
  val trendDescription: String,
  val targetIntervention: String
)

data class CyberTwinV8State(
  val callsign: String,
  val targetCareerRole: String,
  val overallCareerReadinessPercent: Int, // e.g. 69%
  val readinessStatus: String, // "DEVELOPING", "INTERMEDIATE_READY", "JOB_READY"
  val primaryBlocker: String,
  val learningVelocity: String, // "ACCELERATING", "STEADY", "DELIBERATE"
  val confidenceCalibrationState: String, // "WELL_CALIBRATED (±4%)", "TENDING_OVERCONFIDENT", "TENDING_HESITANT"
  val evidenceIntegrityScore: Int, // e.g. 96%
  val vectors: List<CompetencyEvidenceVector>,
  val lastGenomeSyncTimestamp: String
)

// ============================================================================
// 2. PERSONAL CYBER MENTOR ENGINE (Today's Mission & 1 Primary + 3 Optional Actions)
// ============================================================================

enum class MissionActivityType(val label: String, val badgeColor: Long) {
  RECOVERY("Skill Recovery", 0xFFFFB800),
  INVESTIGATION("Live Investigation", 0xFF00F0FF),
  COMMUNICATION("Stakeholder Briefing", 0xFF8B5CF6),
  MISTAKE_CORRECTION("Cognitive Autopsy Drill", 0xFFFF5252),
  PURPLE_TEAM("Purple Team Duel", 0xFFFF0055),
  VOICE_DRILL("Voice Crisis Call", 0xFF00E676)
}

data class MissionItem(
  val id: String,
  val estimatedMinutes: Int,
  val title: String,
  val subtitle: String,
  val activityType: MissionActivityType,
  val targetSkill: String,
  val isCompleted: Boolean = false,
  val navigationRoute: String
)

data class TodaysMissionV8(
  val dateLabel: String,
  val greeting: String,
  val learnerCallsign: String,
  val targetCareer: String,
  val primaryAction: MissionItem,
  val optionalActions: List<MissionItem>,
  val rationaleFromMentor: String
)

// ============================================================================
// 3. PURPLE TEAM ARENA ("SELF VS SELF" 5-ROUND SIGNATURE EXPERIENCE)
// ============================================================================

enum class PurpleDuelPhase(val roundNumber: Int, val title: String, val description: String) {
  RED_TEAM_PLAN(1, "ROUND 1: RED TEAM STAGING", "Design and configure your covert intrusion strategy in the authorized sandbox."),
  TELEMETRY_SYNTH(2, "ROUND 2: TELEMETRY CONVERSION", "AEGORA converts your red strategy into real PCAP, Sysmon, and EDR event streams."),
  BLUE_TEAM_INVESTIGATION(3, "ROUND 3: BLUE TEAM TRIAGE", "Investigate the generated telemetry without seeing your original red playbook."),
  INCIDENT_CONTAINMENT(4, "ROUND 4: INCIDENT RESPONSE", "Execute isolation, process termination, and perimeter firewall blocks."),
  FORENSIC_DEBRIEF(5, "ROUND 5: SELF VS SELF DEBRIEF", "Compare red stealth vs blue detection fidelity. Would you have caught yourself?")
}

data class PurpleTeamArenaState(
  val duelId: String,
  val scenarioTitle: String,
  val activePhase: PurpleDuelPhase,
  val redSelectedTtp: String,
  val redC2Technique: String,
  val redPersistenceMethod: String,
  val redStealthScore: Int,
  val blueDetectedArtifactsCount: Int,
  val blueMissedArtifactsCount: Int,
  val containmentSpeedSeconds: Int,
  val wouldHaveCaughtYourselfVerdict: String,
  val debriefSummary: String,
  val isCompleted: Boolean = false
)

// ============================================================================
// 4. REAL SOC SHIFT SIMULATOR (Multi-Alert Triage Queue & Incident Decisions)
// ============================================================================

enum class SocAlertSeverity(val label: String, val badgeColor: Long) {
  CRITICAL("P1 - Critical Alert", 0xFFFF5252),
  HIGH("P2 - High Alert", 0xFFFF7043),
  MEDIUM("P3 - Medium Alert", 0xFFFFB800),
  LOW("P4 - Low Informational", 0xFF64748B)
}

enum class SocTriageAction(val label: String, val actionColor: Long) {
  INVESTIGATE("Investigate Raw Logs", 0xFF00F0FF),
  CONTAIN("Isolate & Contain Host", 0xFFFF0055),
  ESCALATE("Escalate to Tier 2 / Incident Commander", 0xFFFFB800),
  CLOSE_FALSE_POSITIVE("Close as Benign / False Positive", 0xFF00E676),
  REQUEST_MORE_EVIDENCE("Request Host PCAP / Memory Dump", 0xFF8B5CF6)
}

data class SocShiftAlert(
  val id: String,
  val timestamp: String,
  val title: String,
  val sourceIp: String,
  val destinationHost: String,
  val userAccount: String,
  val severity: SocAlertSeverity,
  val rawLogSnippet: String,
  val conflictingEvidenceHint: String,
  val isTruePositive: Boolean,
  val resolvedAction: SocTriageAction? = null,
  val triageScoreAwarded: Int = 0
)

data class SocShiftState(
  val shiftId: String,
  val shiftName: String,
  val analystCallsign: String,
  val elapsedMinutes: Int,
  val totalShiftDurationMinutes: Int = 30,
  val queue: List<SocShiftAlert>,
  val activeAlertIndex: Int = 0,
  val falsePositiveHandlingScore: Int = 0,
  val containmentPrecisionScore: Int = 0,
  val speedUnderPressureScore: Int = 0,
  val isShiftComplete: Boolean = false,
  val shiftDebriefNotes: String = ""
)

// ============================================================================
// 5. CYBER CONCEPT GRAPH & PREREQUISITE DIAGNOSTIC TRACE
// ============================================================================

data class CyberConceptGraphNode(
  val conceptId: String,
  val name: String,
  val category: String, // "IDENTITY", "NETWORK", "CRYPTOGRAPHY", "SYSTEMS"
  val masteryScore: Int,
  val prerequisiteNodeIds: List<String>,
  val directChildNodeIds: List<String>,
  val diagnosticRemediationAdvice: String,
  val associatedMitreTechniques: List<String>,
  val relatedLabRoute: String
)

// ============================================================================
// 6. SKILL DECAY RADAR & 5-MINUTE RESURRECTION ENGINE
// ============================================================================

data class SkillDecayNodeV8(
  val skillId: String,
  val skillName: String,
  val daysSinceLastPractice: Int,
  val currentRetentionPercent: Int, // e.g. 54%
  val decayVelocity: String, // "MODERATE_DECAY", "CRITICAL_DECAY", "STABLE"
  val careerImportance: String, // "CORE_PREREQUISITE", "SPECIALIZED"
  val resurrectionChallengeTitle: String,
  val resurrectionDurationMinutes: Int = 5,
  val resurrectionLabRoute: String
)

// ============================================================================
// 7. VOICE SOC CRISIS CALL DRILLS
// ============================================================================

data class VoiceSocScenario(
  val id: String,
  val callerPersona: String, // "Panicked Chief Financial Officer", "Incident Commander", "SOC Tier 3 Lead"
  val callerPromptAudioText: String,
  val criticalPointsToAddress: List<String>,
  val forbiddenJargonTraps: List<String>,
  val recordedLearnerResponse: String? = null,
  val calmnessScore: Int = 0,
  val technicalAccuracyScore: Int = 0,
  val clarityScore: Int = 0,
  val escalationScore: Int = 0,
  val aiVoiceDebrief: String = ""
)

// ============================================================================
// 8. INCIDENT REPLAY MULTIVERSE (Alternate Timeline Branching)
// ============================================================================

data class MultiverseBranch(
  val branchId: String,
  val hypothesisTitle: String,
  val whatIfDecisionText: String, // e.g. "What if containment happened 15 minutes later?"
  val simulatedOutcomeDescription: String,
  val financialImpactEstimateDollars: Long,
  val lateralMovementHostsAffected: Int,
  val forensicKeyTakeaway: String
)

// ============================================================================
// 9. 18+ INFINITE CAREER FAMILIES & SHORTEST-PATH ROADMAP
// ============================================================================

enum class CyberCareerFamily(val familyName: String, val badgeColor: Long) {
  BLUE_TEAM("Blue Team & SOC Defense", 0xFF00F0FF),
  RED_TEAM("Red Team & Penetration Testing", 0xFFFF0055),
  SECURITY_ENGINEERING("Security Engineering", 0xFF00E676),
  CLOUD_SECURITY("Cloud & Container Security", 0xFF8B5CF6),
  APP_SEC("Application Security", 0xFFFFB800),
  DEVSECOPS("DevSecOps & CI/CD Hardening", 0xFF00B0FF),
  GRC("Governance, Risk & Compliance (GRC)", 0xFF64748B),
  DIGITAL_FORENSICS("Digital Forensics & Incident Response (DFIR)", 0xFF00E5FF),
  THREAT_INTEL("Cyber Threat Intelligence (CTI)", 0xFFFF7043),
  SECURITY_ARCHITECTURE("Enterprise Security Architecture", 0xFF7C4DFF),
  SECURITY_MANAGEMENT("Security Management & CISO Track", 0xFFFFD700),
  AI_SECURITY("AI/ML Security & LLM Red Teaming", 0xFF00E676),
  ICS_OT_SECURITY("ICS / SCADA / OT Critical Infrastructure", 0xFFFF9100),
  HARDWARE_FIRMWARE("Hardware & Firmware Embedded Security", 0xFF00ACC1),
  CRYPTOGRAPHY("Applied Cryptography & Post-Quantum", 0xFF7E57C2),
  PRIVACY("Data Privacy & Cryptographic Provenance", 0xFF26A69A),
  SECURITY_RESEARCH("Vulnerability Research & Exploit Dev", 0xFFFF1744),
  EMERGING_FRONTIER("Autonomous Cyber Defense Systems", 0xFF00F0FF)
}

data class CareerRoleProfile(
  val id: String,
  val roleTitle: String,
  val family: CyberCareerFamily,
  val experienceLevel: String, // "Entry", "Mid", "Senior", "Principal"
  val shortDescription: String,
  val keyResponsibilities: List<String>,
  val requiredTools: List<String>,
  val keyFrameworks: List<String>,
  val demonstratedReadinessScore: Int, // 0 - 100
  val shortestPathEvidenceSteps: List<String>,
  val topRecommendedLab: String
)
