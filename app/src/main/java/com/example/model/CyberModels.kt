package com.example.model

enum class SkillLevel(val label: String, val multiplier: Float) {
  COMPLETE_BEGINNER("Complete Beginner", 0.2f),
  BEGINNER("Beginner", 0.4f),
  INTERMEDIATE("Intermediate", 0.65f),
  ADVANCED("Advanced", 0.85f),
  EXPERT("Professional / Expert", 1.0f)
}

enum class DailyCommitment(val label: String, val minutes: Int) {
  MIN_15("15 mins/day", 15),
  MIN_30("30 mins/day", 30),
  FORTY_FIVE_MINS("45 mins/day", 45),
  HOUR_1("1 hour/day", 60),
  HOUR_2("2 hours/day", 120),
  HOUR_4_PLUS("4+ hours/day", 240)
}

enum class TargetTimeline(val label: String, val months: Int) {
  MONTH_1("30 Days Sprint", 1),
  MONTH_3("3 Months (Fast Track)", 3),
  MONTH_6("6 Months (Standard)", 6),
  SIX_MONTHS("6 Months (Standard)", 6),
  YEAR_1("1 Year (Deep Mastery)", 12),
  NO_DEADLINE("Self-Paced / No Deadline", 24)
}

enum class LearningPreference(val label: String) {
  HANDS_ON("Hands-on Labs & CTFs"),
  PROBLEM_SOLVING("Incident Scenarios & Problem Solving"),
  VISUAL("Visual & Interactive Architecture"),
  READING("Deep Reading & Research Papers"),
  MIXED("Balanced Cyber Multi-modal")
}

enum class UserRole(val label: String) {
  STUDENT("Student"),
  INSTRUCTOR("Instructor"),
  ADMIN("Administrator")
}

data class UserProfile(
  val id: String = "usr_aegora_01",
  val name: String = "Alex Vance",
  val callsign: String = "VANCE-SOC",
  val email: String = "vance@cyber.aegora.net",
  val role: UserRole = UserRole.STUDENT,
  val targetCareerId: String = "soc_analyst",
  val currentLevel: SkillLevel = SkillLevel.BEGINNER,
  val dailyCommitment: DailyCommitment = DailyCommitment.HOUR_1,
  val targetTimeline: TargetTimeline = TargetTimeline.MONTH_6,
  val learningPreference: LearningPreference = LearningPreference.HANDS_ON,
  val xp: Int = 1450,
  val currentStreak: Int = 7,
  val jobReadinessScore: Int = 68,
  val passportId: String = "AEG-2026-9942X",
  val verifiedSkillCount: Int = 14,
  val completedLabsCount: Int = 8,
  val completedProjectsCount: Int = 3,
  val completedCtfsCount: Int = 5,
  val isOnboarded: Boolean = true
)

data class CareerRole(
  val id: String,
  val title: String,
  val category: String, // Defensive, Offensive, Cloud, Forensics, AppSec, GRC
  val iconName: String,
  val shortDesc: String,
  val fullDesc: String,
  val marketDemand: String, // "High Demand", "Critical Shortage"
  val averageSalarySample: String, // "Sample Data: $95,000 - $135,000"
  val primarySkills: List<String>,
  val essentialTools: List<String>,
  val targetCerts: List<String>,
  val prerequisites: List<String>,
  val matchPercentage: Int
) {
  val averageSalary: String get() = averageSalarySample
  val description: String get() = fullDesc
  val coreSkills: List<String> get() = primarySkills
  val certifications: List<String> get() = targetCerts
}

data class RoadmapTopic(
  val id: String,
  val title: String,
  val category: String,
  val estimatedHours: Int,
  val isCompleted: Boolean = false,
  val isLocked: Boolean = false,
  val hasPracticalLab: Boolean = true,
  val keyConcepts: List<String>
)

data class RoadmapPhase(
  val monthNumber: Int,
  val title: String,
  val focusDomain: String,
  val description: String,
  val topics: List<RoadmapTopic>,
  val milestoneProject: String,
  val isCompleted: Boolean = false,
  val isCurrent: Boolean = false
)

data class SkillEvidence(
  val id: String,
  val skillName: String,
  val evidenceType: String, // "Lab Verification", "CTF Flag Capture", "Project Build", "Incident Simulation"
  val title: String,
  val completedDate: String,
  val artifactHash: String,
  val scoreAchieved: String
)

data class CyberSkill(
  val id: String,
  val name: String,
  val domain: String,
  val knowledgeScore: Int, // 0 - 100
  val practicalScore: Int, // 0 - 100
  val investigationScore: Int, // 0 - 100
  val overallMastery: Int,
  val verifiedEvidenceList: List<SkillEvidence>,
  val recurringMistakes: List<String> = emptyList(),
  val recommendedNextAction: String
)

data class SkillDomain(
  val id: String,
  val name: String,
  val icon: String,
  val masteryPercent: Int,
  val skills: List<CyberSkill>
)

data class DailyMission(
  val id: String,
  val title: String,
  val category: String,
  val difficulty: String,
  val estimatedTimeMinutes: Int,
  val skillsTargeted: List<String>,
  val scenarioContext: String,
  val xpReward: Int,
  val isCompleted: Boolean = false
)

data class LessonContent(
  val id: String,
  val title: String,
  val moduleTitle: String,
  val estimatedReadMinutes: Int,
  val coreExplanation: String,
  val simplifiedAnalogy: String,
  val deepDiveTechnical: String,
  val codeOrTerminalSnippet: String? = null,
  val keyTakeaways: List<String>,
  val practicalTaskDescription: String,
  val spacedRepetitionDue: Boolean = false
)

data class QuizQuestion(
  val id: String,
  val questionText: String,
  val scenarioContext: String? = null,
  val logSnippet: String? = null,
  val mitreTechnique: String? = null,
  val options: List<String>,
  val correctOptionIndex: Int,
  val detailedExplanation: String
)

enum class LabStatus {
  READY, RUNNING, INVESTIGATING, CONTAINED, COMPLETED
}

data class LogEvent(
  val timestamp: String,
  val source: String,
  val eventId: String,
  val severity: String,
  val details: String,
  val isMalicious: Boolean = false
)

data class EvidenceNode(
  val id: String,
  val label: String,
  val type: String, // IP, HASH, PROCESS, ACCOUNT, MALWARE
  val details: String,
  var isConnectedToIncident: Boolean = false
)

data class IncidentSimulation(
  val id: String,
  val title: String,
  val targetOrg: String,
  val threatActor: String,
  val scenarioBrief: String,
  val initialAlert: String,
  val logs: List<LogEvent>,
  val evidenceNodes: List<EvidenceNode>,
  val containmentOptions: List<String>,
  val correctContainmentIndex: Int,
  val mitreMapping: String,
  val learningOutcome: String
)

data class ProjectBlueprint(
  val id: String,
  val title: String,
  val difficulty: String,
  val estimatedWeeks: Int,
  val architectureSummary: String,
  val keyComponents: List<String>,
  val githubStructure: List<String>,
  val resumeBulletPoints: List<String>,
  val skillsDemonstrated: List<String>
)

data class CtfChallenge(
  val id: String,
  val title: String,
  val category: String, // Web, Crypto, Forensics, OSINT, BlueTeam
  val difficulty: String, // Beginner, Intermediate, Advanced
  val points: Int,
  val description: String,
  val hints: List<String>,
  val flag: String,
  val isSolved: Boolean = false
)

data class ThreatAdvisory(
  val id: String,
  val cveId: String,
  val title: String,
  val severity: String, // Critical, High, Medium
  val cvssScore: Float,
  val affectedSystems: String,
  val summary: String,
  val technicalImpact: String,
  val detectionRuleSummary: String,
  val mitigationSteps: String,
  val datePublished: String
) {
  val detectionRule: String get() = detectionRuleSummary
  val publishedDate: String get() = datePublished
}

data class ThreatActorDossier(
  val id: String,
  val name: String,
  val country: String,
  val aliases: List<String>,
  val description: String,
  val targetedSectors: List<String>,
  val primaryTTPs: List<String>
)

typealias ExperienceLevel = SkillLevel
typealias TimelineSprint = TargetTimeline



data class CyberEvent(
  val id: String,
  val title: String,
  val type: String, // Conference, CTF, Hackathon, Scholarship
  val organizer: String,
  val date: String,
  val location: String, // "Global / Online", "Las Vegas, NV"
  val isFree: Boolean,
  val targetAudience: String,
  val verifiedSource: String
) {
  val category: String get() = type
}

enum class AiMentorMode(val displayName: String, val badge: String, val systemPromptFocus: String) {
  SOC_MENTOR("SOC Mentor", "Blue Defense", "You are an elite L3 SOC Incident Response mentor on AEGORA. Guide students through alert analysis, log forensics, SIEM detections, and MITRE ATT&CK mapping with professional calm and analytical precision."),
  TUTOR("Cyber Tutor", "Knowledge Core", "You are a master cybersecurity educator on AEGORA. Explain complex security topics with clarity, progressive depth, and relatable real-world engineering analogies."),
  SOCRATIC("Socratic Coach", "Inquiry Mode", "You are a Socratic cybersecurity coach. Never provide direct answers immediately; ask probing, guiding questions that lead the student to discover the vulnerability, log anomaly, or defense solution."),
  BLUE_TEAM("Blue Team Shield", "Defensive Ops", "You are a senior defensive security architect on AEGORA. Guide students on detection engineering, Sigma rules, threat hunting queries, firewall hardening, and endpoint defense."),
  RED_TEAM("Red Team Mentor", "Authorized Labs", "You are an ethical penetration testing instructor on AEGORA. Teach offensive methodologies STRICTLY within authorized educational labs and defensive remediation contexts."),
  CAREER_MENTOR("Career Navigator", "Career & Jobs", "You are a senior cyber talent and hiring advisor on AEGORA. Help students evaluate job descriptions, bridge skill gaps, build GitHub portfolios, and plan certification strategies."),
  INTERVIEWER("Mock Interviewer", "Tech & HR Arena", "You are a rigorous cybersecurity technical interviewer. Ask realistic scenario questions, probe reasoning on breach response, and evaluate technical precision."),
  EXAM_COACH("Cert Coach", "Certification", "You are a certification preparation coach (Security+, CySA+, CEH, OSCP, CISSP). Test student readiness with scenario drill questions."),
  PROGRESSIVE_HINT("Progressive Hints", "No-Spoiler Mode", "You are in Don't Give Me The Answer Mode. Offer only level-1 subtle hints first, stepping up only when requested.")
}

data class AiChatMessage(
  val id: String,
  val sender: String, // "user" or "aegora_ai"
  val text: String,
  val timestamp: String,
  val mode: AiMentorMode = AiMentorMode.SOC_MENTOR,
  val suggestedFollowUps: List<String> = emptyList()
)

// ==========================================
// Phase 2: Knowledge Vault & Spaced Repetition
// ==========================================

data class LessonNote(
  val id: String,
  val lessonId: String,
  val lessonTitle: String,
  val domain: String,
  val highlightedText: String? = null,
  val noteContent: String,
  val createdDate: String,
  val tags: List<String> = emptyList()
)

data class FlashcardReviewItem(
  val id: String,
  val lessonId: String,
  val lessonTitle: String,
  val domain: String,
  val question: String,
  val answer: String,
  val detailedExplanation: String,
  val intervalDays: Int = 1, // 1, 3, 7, 14
  val repetitionLevel: Int = 1,
  val nextReviewDate: String,
  val isDue: Boolean = true
)

// ==========================================
// Phase 4: Structured Investigation Labs
// ==========================================

data class ProgressiveHints(
  val hint1Conceptual: String,
  val hint2Evidence: String,
  val hint3Direction: String,
  val fullExplanation: String
)

data class InvestigationQuestion(
  val id: String,
  val stepNumber: Int,
  val questionText: String,
  val isMultipleChoice: Boolean = true,
  val options: List<String> = emptyList(),
  val correctOptionIndex: Int = 0,
  val expectedAnswerKeyword: String? = null, // for free-text
  val hints: ProgressiveHints
)

data class InvestigationTimelineEvent(
  val id: String,
  val timestamp: String,
  val eventType: String,
  val sourceHost: String,
  val processOrUser: String,
  val summary: String,
  val rawLog: String,
  val isSuspicious: Boolean
)

data class AiAnalystClaim(
  val claimId: String,
  val analystName: String = "AEGORA Tier-2 SOC AI Co-Pilot",
  val claimText: String,
  val assertedIocs: List<String> = emptyList(),
  val recommendedAction: String,
  val confidenceScore: Int = 89,
  // Note: planted trap field is author-defined. It must never be exposed to learner UI.
  val isUnsupportedTrap: Boolean = false
)

data class InvestigationLab(
  val id: String,
  val title: String,
  val category: String, // "SOC", "Networking", "DFIR"
  val difficulty: String, // "Beginner", "Intermediate", "Advanced"
  val estimatedTimeMinutes: Int,
  val objective: String,
  val prerequisites: List<String>,
  val targetDomain: String,
  val timelineEvents: List<InvestigationTimelineEvent>,
  val questions: List<InvestigationQuestion>,
  val isCompleted: Boolean = false,
  val bestScore: Int = 0,
  val aiAnalystOutput: AiAnalystClaim? = null
)

data class LabScorecard(
  val labId: String,
  val detectionScore: Int, // 0-100
  val analysisScore: Int,  // 0-100
  val reasoningScore: Int, // 0-100
  val overallScore: Int,   // 0-100
  val hintsUsedCount: Int,
  val xpAwarded: Int,
  val feedback: String
)

// ==========================================
// Phase 9: Community Topic Rooms
// ==========================================

data class CommunityReply(
  val id: String,
  val authorName: String,
  val authorCallsign: String,
  val isVerifiedBadge: Boolean,
  val body: String,
  val timestamp: String
)

data class CommunityPost(
  val id: String,
  val roomCategory: String, // "SOC", "Pentesting", "Cloud Security", "DFIR", "CTF", "Career"
  val title: String,
  val body: String,
  val authorName: String,
  val authorCallsign: String,
  val isVerifiedBadge: Boolean,
  val timestamp: String,
  val upvotes: Int,
  val replies: List<CommunityReply> = emptyList(),
  val isReported: Boolean = false
)

// ==========================================
// Phase 10: University & Admin Scaffolding
// ==========================================

data class StudentProgressItem(
  val studentId: String,
  val studentName: String,
  val callsign: String,
  val completionRatePercent: Int,
  val completedLessonsCount: Int,
  val completedLabsCount: Int,
  val lastActive: String
)

data class ClassroomRoster(
  val id: String,
  val name: String,
  val instructorName: String,
  val assignedLessonIds: List<String>,
  val students: List<StudentProgressItem>
)

data class AdminPlatformStats(
  val totalLessonsCount: Int,
  val totalLabsCount: Int,
  val totalChallengesCount: Int,
  val totalProjectsCount: Int,
  val activeSimulations: Int,
  val totalRegisteredLearners: Int
)

// ==========================================
// Voice Interview Record
// ==========================================

data class VoiceInterviewScorecard(
  val sessionId: String,
  val roleName: String,
  val overallScore: Int,
  val technicalDepthScore: Int,
  val incidentResponseScore: Int,
  val communicationScore: Int,
  val transcript: List<Pair<String, String>>, // Speaker -> Message
  val timestamp: String
)

// ==========================================
// Aegora Master Architecture: Signature 7 Systems
// ==========================================

// 1. Cyber Learning Genome
data class CyberLearningGenome(
  val knowledgeScore: Int = 78,
  val practicalScore: Int = 64,
  val reasoningScore: Int = 71,
  val investigationScore: Int = 58,
  val communicationScore: Int = 83,
  val retentionScore: Int = 69,
  val decisionMakingScore: Int = 61,
  val learningVelocity: Int = 84, // 0 - 100
  val currentBottleneck: String = "Investigation Correlation & Timestamp Triage",
  val bottleneckDomain: String = "SOC Defense / DFIR",
  val recommendedIntervention: String = "3 Multi-Source Log Correlation Scenarios in SOC Range",
  val recommendedActionTarget: String = "lab_soc_01"
) {
  val overallMasteryScore: Int get() = (knowledgeScore + practicalScore + reasoningScore + investigationScore + communicationScore + retentionScore + decisionMakingScore) / 7
  val prerequisiteHealthScore: Int get() = 92
}

// 2. Reasoning Graph & Investigation Fingerprint
data class ReasoningGraphStep(
  val stepId: String,
  val nodeLabel: String,
  val nodeType: String, // "EVIDENCE_INSPECT", "HYPOTHESIS_TEST", "FALSE_LEAD_DISMISSED", "IOC_LINKED", "CONTAINMENT_TRIGGERED"
  val actionDescription: String,
  val timeOffsetSeconds: Int,
  val isOptimalStep: Boolean = true
)

data class ReasoningGraph(
  val incidentId: String,
  val scenarioTitle: String,
  val investigatedAt: String,
  val totalInvestigationSeconds: Int,
  val steps: List<ReasoningGraphStep>,
  val fingerprintEvidenceFirst: Int = 82,
  val fingerprintHypothesisFirst: Int = 41,
  val fingerprintTimelineAnalysis: Int = 64,
  val fingerprintIocCorrelation: Int = 71,
  val fingerprintContextChecking: Int = 39,
  val prematureClosureRisk: String = "MEDIUM", // "LOW", "MEDIUM", "HIGH"
  val expertShadowComparison: String = "You prioritized IOC hashing and IP lookup before establishing chronological context. Strong analysts establish the initial timeline first to narrow log scope.",
  val expertSequenceSteps: List<String> = listOf(
    "1. Chronological Timeline Anchoring (Sysmon Event ID 1)",
    "2. Parent Process & User Context Check (winword.exe -> powershell.exe)",
    "3. Outbound Network & C2 IP Correlation (Port 443 Beaconing)",
    "4. Memory Injection & Lateral Movement Scoping"
  )
)

// 3. Mistake DNA & Cognitive Bias Passport
data class MistakeDnaRecord(
  val id: String,
  val patternName: String, // "Tunnel Vision", "Confirmation Bias", "Missed Correlation", "Alert Fatigue Pattern", "Premature Closure"
  val category: String, // Cognitive Bias / Analytical Failure
  val occurrences: Int,
  val severity: String, // "Critical", "Moderate", "Minor"
  val description: String,
  val diagnosedIncident: String,
  val correctiveRemediation: String
)

// 4. Skill Decay Radar & Forgetting Forecast
data class SkillDecayForecast(
  val skillId: String,
  val skillName: String,
  val domain: String,
  val currentHealth: Int, // 0 - 100
  val retentionScore: Int, // 0 - 100
  val lastPracticedDaysAgo: Int,
  val riskLevel: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
  val projected7Days: Int,
  val projected30Days: Int,
  val projected60Days: Int,
  val recommendedDiagnosticTitle: String,
  val diagnosticEstimatedMins: Int
) {
  val daysUntilCriticalDecay: Int get() = when (riskLevel) {
    "CRITICAL" -> 3
    "HIGH" -> 6
    "MEDIUM" -> 14
    else -> 30
  }
}

// 5. Concept Collision Engine
data class ConceptCollision(
  val id: String,
  val pairTitle: String, // e.g. "SIEM vs SOAR", "IDS vs IPS", "Hashing vs Encryption"
  val conceptA: String,
  val conceptB: String,
  val confusionRatePercent: Int,
  val scenarioPrompt: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String,
  val distinctionKey: String
)

// 6. Alert Fatigue Simulator Item
data class AlertFatigueItem(
  val id: String,
  val timestamp: String,
  val ruleName: String,
  val sourceIp: String,
  val targetHost: String,
  val severity: String, // "Critical", "High", "Medium", "Low", "Info"
  val summary: String,
  val isTruePositiveCritical: Boolean,
  val isBenignNoise: Boolean
)

// 7. Uncertainty Training Scenario
data class UncertaintyScenario(
  val id: String,
  val title: String,
  val contextBrief: String,
  val rawLogSnippet: String,
  val maliciousConfidence: Int, // e.g. 64%
  val benignConfidence: Int,    // e.g. 21%
  val unknownConfidence: Int,   // e.g. 15%
  val expertRecommendedDecision: String,
  val expertRationale: String
)

// 8. Counterfactual Consequence Branch
data class CounterfactualBranch(
  val id: String,
  val decisionChoice: String,
  val outcomeTitle: String,
  val simulationResultDescription: String,
  val lateralMovementOccurred: Boolean,
  val dataExfiltratedMb: Int,
  val businessImpactScore: String // "Minimal Impact", "Severe Breach", "Catastrophic"
)

// 9. Stakeholder Translation Drill
data class StakeholderTranslationScenario(
  val id: String,
  val incidentCode: String,
  val technicalIncidentBrief: String,
  val executivePersona: String, // "CEO", "CTO", "Security Manager", "SOC Analyst", "End Customer"
  val personaGoal: String,
  val goodSampleSummary: String,
  val flawedJargonSummary: String,
  val scoringRubricNotes: String
)

// 10. MITRE ATT&CK Evidence Coverage
data class MitreTacticCoverage(
  val tacticId: String,
  val tacticName: String,
  val totalTechniquesInMatrix: Int,
  val verifiedTechniquesCount: Int,
  val coveragePercent: Int,
  val demonstratedTechniques: List<String>
)

// 11. Simulated Experience Ledger
data class SimulatedExperienceLedger(
  val socInvestigationsCount: Int = 47,
  val incidentSimulationsCount: Int = 31,
  val detectionEngineeringExercises: Int = 19,
  val ctfFlagsCapturedCount: Int = 14,
  val portfolioProjectsCompleted: Int = 7,
  val crisisDecisionsCount: Int = 83
)

data class MissingCompetencyGap(
  val competencyName: String,
  val marketDemandWeight: String,
  val estimatedHoursToMaster: Int,
  val whyEmployersDemand: String,
  val remedialLabModule: String
)

// 12. Reverse Roadmap Job Analysis
data class ReverseRoadmapAnalysis(
  val jobTitle: String,
  val targetCompanySample: String,
  val matchPercentage: Int,
  val verifiedMatchingSkills: List<String>,
  val missingGapSkills: List<String>,
  val shortestEvidencePath: List<String>,
  val estimatedWeeksToCloseGap: Int
) {
  val targetJobTitle: String get() = jobTitle
  val estimatedHoursToCloseGap: Int get() = estimatedWeeksToCloseGap * 8
  val missingCompetencies: List<MissingCompetencyGap> get() = missingGapSkills.map {
    MissingCompetencyGap(
      competencyName = it,
      marketDemandWeight = "High Priority",
      estimatedHoursToMaster = 6,
      whyEmployersDemand = "Required for standard enterprise SOC triage and response SLA adherence",
      remedialLabModule = "Module 4 • Live Endpoint Telemetry & Incident Containment"
    )
  }
  val verifiedMatchedCompetencies: List<String> get() = verifiedMatchingSkills
}

// 13. Energy-Aware Mode
enum class EnergyLevel(val displayName: String, val iconName: String, val subtitle: String) {
  LOW("Low Energy", "Flashcards & Quick Reads", "15m Spaced review, concept collision, bite-sized quizzes"),
  BALANCED("Balanced", "Standard Missions", "30-45m Guided lessons, triage practice, audio drills"),
  HIGH("High Energy / Deep Work", "Deep Range & Coding", "60-90m Live incident labs, adversary simulation, Sigma rules")
}

// 14. Sequential Terminal Ladder (Bandit & UnderTheWire)
enum class LadderPlatform {
  LINUX_BANDIT, WINDOWS_POWERSHELL
}

data class TerminalLadderLevel(
  val levelNumber: Int,
  val title: String,
  val systemPrompt: String,
  val goalDescription: String,
  val expectedCommandPattern: String,
  val solutionPasscode: String,
  val hints: List<String>,
  val sampleCommands: List<String>,
  val mockTerminalOutput: String,
  val skillArea: String,
  val xpReward: Int = 150,
  val isUnlocked: Boolean = false,
  val isCompleted: Boolean = false
)

// 15. Web AppSec Sequential Ladder (PortSwigger shape)
enum class AppSecDifficulty(val label: String, val colorHex: Long) {
  APPRENTICE("Apprentice", 0xFF00875A),
  PRACTITIONER("Practitioner", 0xFFD97706),
  EXPERT("Expert", 0xFFB3261E)
}

data class WebAppSecLevel(
  val levelId: String,
  val categoryId: String, // sqli, xss, ssrf, idor, cmd_injection
  val title: String,
  val difficulty: AppSecDifficulty,
  val scenarioContext: String,
  val targetEndpoint: String,
  val vulnerableParameter: String,
  val mutatedPayloadTemplate: String,
  val correctPayload: String,
  val simulatedHttpResponse: String,
  val defenseExplanation: String,
  val cweId: String,
  val isCompleted: Boolean = false
)

data class WebAppSecCategory(
  val id: String,
  val name: String,
  val iconName: String,
  val description: String,
  val totalLevels: Int,
  val completedLevels: Int,
  val levels: List<WebAppSecLevel>
)

// 16. Business AppSec & Remediation Patch Workflow (CMD+CTRL shape)
data class AppSecPatchOption(
  val id: String,
  val codeSnippet: String,
  val isCorrect: Boolean,
  val architecturalTradeoff: String
)

data class BusinessAppPatchLab(
  val id: String,
  val appName: String,
  val businessDomain: String, // FinTech, Healthcare EHR, HR & Payroll, E-Commerce
  val title: String,
  val businessImpactSummary: String, // Financial loss, GDPR/HIPAA fines, Reputational damage
  val complianceViolations: List<String>,
  val vulnerableCodeLanguage: String,
  val vulnerableCodeSnippet: String,
  val interceptedRequest: String,
  val exploitationProof: String,
  val patchOptions: List<AppSecPatchOption>,
  val verifiedFixExplanation: String,
  val xpReward: Int = 350
)

// 17. Binary Exploitation & Memory Safety Track (pwn.college shape)
data class BinaryExploitationModule(
  val id: String,
  val title: String,
  val tier: String, // "Specialist Track • Security Research"
  val conceptSummary: String,
  val disassemblyCode: String,
  val stackLayoutExplanation: String,
  val registersState: Map<String, String>, // RIP, RSP, RBP, RAX
  val mitigationMechanisms: List<String>, // ASLR, Stack Canary, DEP/NX, Rust Memory Safety
  val interactiveVerificationPrompt: String,
  val correctCalculatedOffset: String,
  val securityResearchTakeaway: String
)

// 18. Domain Coverage Mapping & Integration Check
data class SpecializedDomainOverview(
  val domainId: String,
  val domainName: String,
  val iconName: String,
  val referencePlatform: String,
  val statusBadge: String,
  val handsOnMechanic: String,
  val coveredModulesCount: Int,
  val mitreMapping: String
)

// 19. Autonomous Adversary "Shadow Agent" & Deception Grid
enum class AdversaryMutationPhase(val displayName: String, val colorHex: Long) {
  INITIAL_RECON("Phase 1: Recon & Spray", 0xFF00F0FF),
  ENDPOINT_PIVOT("Phase 2: Lateral Pivot", 0xFFFFB800),
  DLL_HIJACK("Phase 3: DLL Side-Loading & Memory Injection", 0xFFFF80BF),
  WMI_PERSISTENCE("Phase 4: WMI/Task Persistence", 0xFF8B5CF6),
  DATA_EXFILTRATION("Phase 5: DNS Tunneling Exfiltration", 0xFFFF003C)
}

data class DeceptionCanaryToken(
  val id: String,
  val name: String,
  val type: String, // "AWS IAM Secret", "Kerberos SPN", "Synthetic DB Admin Credential", "Decoy S3 Canary File"
  val deployedHost: String,
  val isTripped: Boolean,
  val tripTimestamp: String? = null,
  val adversaryIp: String? = null
)

data class MitreTacticStep(
  val tacticCode: String,
  val tacticName: String,
  val techniqueId: String,
  val techniqueName: String,
  val isCurrentActive: Boolean,
  val isMitigated: Boolean
)

data class ShadowAdversaryState(
  val scenarioId: String,
  val threatActorName: String,
  val currentPhase: AdversaryMutationPhase,
  val compromiseLevelPercent: Int,
  val activeAdversaryIp: String,
  val targetedHost: String,
  val mutationHistory: List<String>,
  val telemetryStream: List<String>,
  val tactics: List<MitreTacticStep>,
  val deployedCanaries: List<DeceptionCanaryToken>,
  val isContained: Boolean = false
)

// 20. Incident Time-Machine & Split-Timeline Forking
data class TimelineDiffMetrics(
  val operationalDowntimeHours: Float,
  val exfiltrationBlastRadiusMb: Int,
  val reputationalImpactScore: Int, // 0-100
  val estimatedComplianceFineUsd: Long,
  val containmentConfidencePercent: Int
)

data class TimelineBranch(
  val branchId: String,
  val branchName: String,
  val strategyLabel: String,
  val description: String,
  val actionsTaken: List<String>,
  val metrics: TimelineDiffMetrics,
  val isSelected: Boolean = false
)

data class TimelineIncidentScrubPoint(
  val timestampSeconds: Int,
  val timeLabel: String,
  val systemEvent: String,
  val packetHexSummary: String,
  val isRootCauseTrigger: Boolean = false,
  val forensicFinding: String
)

// 21. Biometric Stress & Acoustic Threat Sonification
data class BioStressReading(
  val simulatedBpm: Int,
  val touchVelocityPxPerSec: Float,
  val tapHesitationMs: Long,
  val composureIndexScore: Int, // 0-100%
  val cognitiveOverloadWarning: Boolean,
  val triageCadenceStatus: String
)

data class AcousticThreatProfile(
  val id: String,
  val name: String,
  val trafficType: String,
  val wavePattern: String, // "STEADY_SIN_WAVE", "PULSING_C2_HEARTBEAT", "HIGH_FREQ_SYN_SURGE", "CHIRPING_DNS_TUNNEL"
  val frequencyKhz: Float,
  val audioDescription: String,
  val diagnosticSignature: String
)

// 22. Voice Incident War Room & Executive Escalation
enum class WarRoomPersonaRole(val roleName: String, val defaultVoiceTone: String) {
  CISO("Chief Information Security Officer", "Direct & Urgency-Driven"),
  LEGAL_COUNSEL("General Counsel / Legal", "Compliance & Liability Focused"),
  PR_COMMUNICATIONS("Head of Communications / PR", "Reputational Risk & Media"),
  CEO("Chief Executive Officer", "Business Continuity & Board Pressure"),
  EXTORTIONIST("Ransomware Threat Syndicate", "Menacing Ransom Demand")
}

data class WarRoomPersona(
  val id: String,
  val name: String,
  val title: String,
  val role: WarRoomPersonaRole,
  val avatarIcon: String,
  val activeQuote: String,
  val stressLevel: Int // 0-100
)

data class WarRoomTriageDials(
  val timeRemainingSeconds: Int,
  val operationalDowntimePercent: Int,
  val reputationalRiskPercent: Int,
  val exfiltrationBlastRadiusPercent: Int,
  val regulatoryFineExposurePercent: Int
)

// 23. Zero-Day Deconstructor & Detection Engineering Studio
data class ZeroDayExploitModel(
  val cveId: String,
  val title: String,
  val cvssScore: Float,
  val attackVector: String,
  val affectedComponent: String,
  val memoryStackFlow: List<String>,
  val exploitProofSnippet: String,
  val sigmaRuleTemplate: String,
  val yaraRuleTemplate: String,
  val mitigationStrategy: String
)

// 24. Global Cyber Radar & CTF Tournament League
data class GlobalRadarItem(
  val id: String,
  val title: String,
  val category: String, // "DEF CON Livecast", "Black Hat Briefing", "World CTF Match", "Live Bug Bounty", "Zero-Day Advisory"
  val organizer: String,
  val dateOrTimeLeft: String,
  val prizeOrPoints: String,
  val liveStatus: String, // "LIVE NOW", "UPCOMING", "ACTIVE BOUNTY"
  val deepLinkTarget: String
)

data class CyberLeagueTeam(
  val rank: Int,
  val teamName: String,
  val tier: String, // "Solo Operative", "University Cohort", "Enterprise SOC Squad"
  val organization: String,
  val attackPoints: Int,
  val defensePoints: Int,
  val totalScore: Int,
  val verificationBadge: String
)

// 25. Autonomous Red-vs-Blue Swarm Arena
data class SwarmArenaCombatState(
  val battleId: String,
  val redSwarmName: String,
  val blueSwarmName: String,
  val enterpriseCompromisePercent: Int, // 0 to 100
  val activeSwarmRound: Int,
  val recentCombatLogs: List<String>,
  val commanderActionsAvailable: List<String>,
  val isVictoryAchieved: Boolean = false
)

// ========================================================
// 26. UNIVERSAL CROSS-DEVICE ARCHITECTURE (V11 ENGINE)
// ========================================================

enum class BreakpointClass(val label: String, val minWidthDp: Int, val maxWidthDp: Int) {
  COMPACT("Compact Phone", 0, 599),
  MEDIUM("Foldable / Tablet", 600, 839),
  EXPANDED("Laptop / Chromebook", 840, 1199),
  LARGE("Desktop Monitor", 1200, 1599),
  ULTRAWIDE("Ultra-Wide Command Station", 1600, 9999)
}

enum class PerformanceMode(val label: String, val subtitle: String) {
  FULL_VISUAL("Full Cyber 2096", "Hardware shaders, neon bloom, orbital particles & dynamic waveforms"),
  BALANCED("Balanced Mode", "Standard framerate with glassmorphism & optimized glow effects"),
  PERFORMANCE("Light Performance", "Zero particle overhead, high-contrast dark theme, ultra-low battery drain"),
  REDUCED_MOTION("Reduced Motion", "Instantaneous state transitions, static diagrams, WCAG compliant")
}

enum class NetworkSyncStatus(val label: String, val badgeText: String) {
  SYNCED("Cloud Synchronized", "SYNCED"),
  SYNCING("Synchronizing Cyber Twin...", "SYNCING"),
  OFFLINE("Offline Local Cache Active", "OFFLINE"),
  SYNC_ERROR("Sync Conflict Detected", "SYNC ERROR")
}

data class CrossDeviceSessionState(
  val activeSessionId: String = "sess_v11_9942",
  val lastActivityTitle: String = "Investigation #4821 — Golden Ticket Ransomware",
  val lastActivityCategory: String = "Live SOC Incident Range",
  val lastActivityProgress: String = "Step 3/5: Kerberoasting Anomaly Triage",
  val lastActiveTimestamp: String = "Synchronized 2 min ago",
  val originDeviceName: String = "MacBook Pro / Desktop Station",
  val targetScreenTag: String = "live_soc_range",
  val uncommittedNotesCount: Int = 2,
  val isConflictPresent: Boolean = false,
  val syncStatus: NetworkSyncStatus = NetworkSyncStatus.SYNCED
)


