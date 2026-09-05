package com.example.intelligence

import com.example.capability.*
import com.example.data.AegoraRepository
import com.example.data.DemonstratedCapabilityRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * AEGORA v8.1 — Central Intelligence Connective Orchestrator
 *
 * Implements the continuous closed learning loop:
 * User Action -> Evidence Collector -> Evidence Normalizer -> Learner State Engine
 * -> Cyber Twin 3.0 -> Cognitive Diagnostics -> Next Best Action Engine
 * -> Learning Activity -> New Evidence
 */
object AegoraIntelligenceOrchestrator {

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
  private val shortDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

  // ============================================================================
  // 1. CYBER TWIN 3.0 STATE
  // ============================================================================

  private val initialDimensions = listOf(
    CompetencyDimension30(
      dimensionKey = "knowledge",
      title = "Theoretical Knowledge",
      score = 82,
      benchmarkTarget = 80,
      confidenceRating = "HIGH (±3%)",
      evidenceCount = 28,
      recentTrend = "ASCENDING (+4% this week)",
      longTermTrend = "CONSISTENT UPWARD",
      learningVelocity = "STEADY",
      decayRisk = "LOW",
      consistencyScore = 88,
      lastDemonstratedDate = "2026-08-27",
      strongestEvidenceProof = "Verified RFC 8446 TLS 1.3 Key Exchange handshake & MITRE ATT&CK Enterprise Matrix mapping.",
      weakestEvidenceProof = "Authentication vs Authorization collision in API JWT validation lab.",
      recommendedIntervention = "7-minute JWT Claim & Signature validation active recall drill.",
      whyScoreExistsBreakdown = listOf(
        "Completed 18 knowledge modules across Network Security and Cryptography.",
        "Passed 8 active recall tests with >85% retention score.",
        "Correctly mapped 24 MITRE ATT&CK techniques to telemetry sources."
      ),
      whyScoreWeaknessFactors = listOf(
        "Failed OAuth2 state parameter CSRF mitigation question twice.",
        "Slight hesitation when differentiating Kerberos AS-REQ vs TGS-REQ."
      )
    ),
    CompetencyDimension30(
      dimensionKey = "practical",
      title = "Practical Ability",
      score = 71,
      benchmarkTarget = 75,
      confidenceRating = "HIGH (±4%)",
      evidenceCount = 19,
      recentTrend = "ASCENDING (+6% last 7d)",
      longTermTrend = "ACCELERATING",
      learningVelocity = "RAPID",
      decayRisk = "MODERATE",
      consistencyScore = 79,
      lastDemonstratedDate = "2026-08-26",
      strongestEvidenceProof = "Crafted custom BPF syntax filtering outbound C2 beaconing on port 8443.",
      weakestEvidenceProof = "Powershell execution policy bypass script syntax error in Sandbox Lab #4.",
      recommendedIntervention = "15-minute PowerShell Obfuscation & AMSI Bypass deconstruction.",
      whyScoreExistsBreakdown = listOf(
        "Successfully isolated compromised host in live Linux CLI sandbox.",
        "Extracted malicious payload hashes from raw PCAP file.",
        "Configured Suricata IDS alert rules for lateral SMB scans."
      ),
      whyScoreWeaknessFactors = listOf(
        "Required 2 retries to parse Windows Registry RunKeys via Regedit CLI."
      )
    ),
    CompetencyDimension30(
      dimensionKey = "investigation",
      title = "Investigation & Triage",
      score = 76,
      benchmarkTarget = 80,
      confidenceRating = "HIGH (±3%)",
      evidenceCount = 22,
      recentTrend = "ASCENDING (+5%)",
      longTermTrend = "CONSISTENT UPWARD",
      learningVelocity = "STEADY",
      decayRisk = "LOW",
      consistencyScore = 84,
      lastDemonstratedDate = "2026-08-27",
      strongestEvidenceProof = "Correctly correlated Sysmon Event ID 1 (Process Spawn) to Event ID 3 (Network Connect) in under 4 minutes.",
      weakestEvidenceProof = "Premature closure during Alert #9042 triage: missed secondary scheduled task persistence.",
      recommendedIntervention = "20-minute Multi-Stage Persistence Investigation drill.",
      whyScoreExistsBreakdown = listOf(
        "Processed 14 simulated SOC queue alerts with 92% false-positive discrimination.",
        "Reconstructed end-to-end intrusion kill chain for Emotet malware variant.",
        "Validated parent-child process anomalies in svchost.exe lineage."
      ),
      whyScoreWeaknessFactors = listOf(
        "Showed premature closure pattern in 2 of last 5 investigation sessions."
      )
    ),
    CompetencyDimension30(
      dimensionKey = "reasoning",
      title = "Causal Reasoning",
      score = 68,
      benchmarkTarget = 75,
      confidenceRating = "PROVISIONAL",
      evidenceCount = 15,
      recentTrend = "STABLE",
      longTermTrend = "DEVELOPING",
      learningVelocity = "DELIBERATE",
      decayRisk = "MODERATE",
      consistencyScore = 72,
      lastDemonstratedDate = "2026-08-25",
      strongestEvidenceProof = "Falsified DNS exfiltration hypothesis by proving legitimate NTP synchronization traffic.",
      weakestEvidenceProof = "Confirmation bias when anchoring on initial phishing email vector while attacker entered via RDP.",
      recommendedIntervention = "10-minute Socratic Cognitive Autopsy on Hypothesis Falsification.",
      whyScoreExistsBreakdown = listOf(
        "Demonstrated systematic elimination of benign alternatives in 6 CTF challenges.",
        "Built accurate chronological attack timelines in 4 major lab scenarios."
      ),
      whyScoreWeaknessFactors = listOf(
        "Tended to accept first plausible hypothesis without testing competing theories.",
        "Tunnel vision detected during Active Directory lateral movement tracking."
      )
    ),
    CompetencyDimension30(
      dimensionKey = "decision_making",
      title = "Decision Making Under Pressure",
      score = 74,
      benchmarkTarget = 75,
      confidenceRating = "HIGH (±4%)",
      evidenceCount = 16,
      recentTrend = "ASCENDING (+3%)",
      longTermTrend = "STEADY",
      learningVelocity = "STEADY",
      decayRisk = "LOW",
      consistencyScore = 80,
      lastDemonstratedDate = "2026-08-27",
      strongestEvidenceProof = "Executed timely host isolation in under 90 seconds during simulated ransomware outbreak.",
      weakestEvidenceProof = "Delayed firewall egress block by 8 minutes due to over-validating non-critical database logs.",
      recommendedIntervention = "Timed 5-minute Rapid Containment Drills.",
      whyScoreExistsBreakdown = listOf(
        "Maintained high containment precision score (88%) across 3 SOC shift drills.",
        "Appropriately escalated high-blast-radius incidents to Tier 2."
      ),
      whyScoreWeaknessFactors = listOf(
        "Occasional hesitation when deciding whether to kill mission-critical production processes."
      )
    ),
    CompetencyDimension30(
      dimensionKey = "communication",
      title = "Executive & Crisis Communication",
      score = 61,
      benchmarkTarget = 70,
      confidenceRating = "PROVISIONAL",
      evidenceCount = 9,
      recentTrend = "ASCENDING (+8% last 14d)",
      longTermTrend = "RAPID RECOVERY",
      learningVelocity = "RAPID",
      decayRisk = "MODERATE",
      consistencyScore = 65,
      lastDemonstratedDate = "2026-08-27",
      strongestEvidenceProof = "Briefed simulated CISO on ransomware containment in under 2 minutes without jargon.",
      weakestEvidenceProof = "Used overly dense technical jargon ('C2 beaconing over port 8443 with Cobalt Strike Malleable profile') with non-technical CFO.",
      recommendedIntervention = "Voice Crisis Drill: Jargon-Free Executive Summary with CFO persona.",
      whyScoreExistsBreakdown = listOf(
        "Completed 2 Voice SOC crisis drill scenarios with >75% clarity rating.",
        "Authored structured incident response executive summary with clear impact and timeline."
      ),
      whyScoreWeaknessFactors = listOf(
        "Fell into 3 jargon traps during panicked CFO verbal drill.",
        "Need clearer dollar-impact translation for non-technical stakeholders."
      )
    ),
    CompetencyDimension30(
      dimensionKey = "transfer",
      title = "Cross-Domain Transfer",
      score = 58,
      benchmarkTarget = 70,
      confidenceRating = "PROVISIONAL",
      evidenceCount = 8,
      recentTrend = "STABLE",
      longTermTrend = "DEVELOPING",
      learningVelocity = "DELIBERATE",
      decayRisk = "HIGH",
      consistencyScore = 60,
      lastDemonstratedDate = "2026-08-24",
      strongestEvidenceProof = "Successfully translated Windows Sysmon Event ID 1 process tree logic to Linux auditd logs.",
      weakestEvidenceProof = "Struggled to map Active Directory Kerberos Golden Ticket attack concepts to AWS IAM STS Role assumption.",
      recommendedIntervention = "Comparative Learning Lab: Active Directory Kerberos vs AWS IAM Token Federation.",
      whyScoreExistsBreakdown = listOf(
        "Completed Linux and Windows hybrid log correlation lab.",
        "Demonstrated MITRE ATT&CK mapping across on-prem and cloud matrices."
      ),
      whyScoreWeaknessFactors = listOf(
        "Limited exposure to Kubernetes pod exec audit logs and container escape mechanics."
      )
    ),
    CompetencyDimension30(
      dimensionKey = "retention",
      title = "Knowledge Retention & Ebbinghaus Curve",
      score = 79,
      benchmarkTarget = 80,
      confidenceRating = "HIGH (±3%)",
      evidenceCount = 31,
      recentTrend = "ASCENDING (+2%)",
      longTermTrend = "STRONG",
      learningVelocity = "STEADY",
      decayRisk = "LOW",
      consistencyScore = 85,
      lastDemonstratedDate = "2026-08-27",
      strongestEvidenceProof = "Maintained 94% retention on TCP 3-Way Handshake & Subnetting after 45 days.",
      weakestEvidenceProof = "Decay detected in DNS Tunneling TXT record forensic query syntax (last practiced 19 days ago).",
      recommendedIntervention = "5-Minute Skill Resurrection: DNS Tunneling Detection.",
      whyScoreExistsBreakdown = listOf(
        "Maintained 12-day active study streak with daily spaced repetition intervals.",
        "Completed 6 Skill Resurrection micro-drills within 24 hours of decay triggers."
      ),
      whyScoreWeaknessFactors = listOf(
        "DNS Tunneling and Ghidra disassembly syntax approaching critical decay threshold."
      )
    ),
    CompetencyDimension30(
      dimensionKey = "career_readiness",
      title = "Target Career Readiness (SOC Analyst L1)",
      score = 69,
      benchmarkTarget = 85,
      confidenceRating = "HIGH (±2%)",
      evidenceCount = 34,
      recentTrend = "ASCENDING (+7% last 30d)",
      longTermTrend = "ACCELERATING",
      learningVelocity = "STEADY",
      decayRisk = "LOW",
      consistencyScore = 82,
      lastDemonstratedDate = "2026-08-27",
      strongestEvidenceProof = "Completed 8 verifiable labs, 2 capstone projects, and 5 CTF flags mapped directly to SOC Analyst job description.",
      weakestEvidenceProof = "Gaps remain in Detection Engineering (Sigma Rules) and Cloud Incident Triage.",
      recommendedIntervention = "Shortest Path Roadmap: Complete Sigma Detection Engineering Capstone Project.",
      whyScoreExistsBreakdown = listOf(
        "Meets 78% of core SOC Analyst L1 technical requirements.",
        "Verified Skill Passport contains 14 cryptographically sealed badges."
      ),
      whyScoreWeaknessFactors = listOf(
        "Requires 2 more independent SOC shift simulations and 1 cloud incident response proof."
      )
    )
  )

  private val _cyberTwin = MutableStateFlow(
    CyberTwinV81State(
      callsign = "VANCE-SOC",
      targetCareerRole = "SOC Analyst (Tier 1 / Tier 2)",
      overallCareerReadinessPercent = 69,
      readinessStatus = "INTERMEDIATE_READY",
      primaryBlocker = "Premature Closure in Multi-Stage Investigations & Sigma Rule Crafting",
      learningVelocity = "STEADY (3.8 concepts / wk)",
      confidenceCalibrationState = "WELL_CALIBRATED (±4%)",
      evidenceIntegrityScore = 97,
      dimensions = initialDimensions,
      lastSyncTimestamp = "2026-08-27 21:55 UTC"
    )
  )
  val cyberTwin: StateFlow<CyberTwinV81State> = _cyberTwin.asStateFlow()

  // ============================================================================
  // 2. EVIDENCE STREAM
  // ============================================================================

  private val _evidenceStream = MutableStateFlow<List<EvidenceItem>>(
    listOf(
      EvidenceItem(
        id = "evi_901",
        timestamp = "2026-08-27 20:45",
        activityTitle = "Purple Team Arena: Defense Evasion & Mimikatz Detection",
        skillDomain = "Endpoint Security",
        subskill = "Sysmon Event ID 10 & LSASS Memory Access",
        difficultyLevel = "Advanced",
        scoreAchieved = 88,
        mistakesRecordedCount = 1,
        reasoningQualityScore = 85,
        confidenceStated = "HIGH",
        timeSpentSeconds = 840,
        attemptNumber = 1,
        strength = EvidenceStrength.EXPERT_DEMONSTRATED,
        sourceType = EvidenceSourceType.PURPLE_TEAM_DUEL,
        verificationStatus = EvidenceVerificationStatus.CRYPTOGRAPHICALLY_SEALED,
        proofSnippet = "Extracted ProcessGuid {d3b07384-9022} accessing lsass.exe with GrantedAccess 0x1010 (PROCESS_VM_READ)."
      ),
      EvidenceItem(
        id = "evi_902",
        timestamp = "2026-08-27 19:15",
        activityTitle = "Real SOC Shift Simulator: Alert Triage Queue (P1 Ransomware Outbreak)",
        skillDomain = "Incident Response",
        subskill = "Host Isolation & Process Tree Triage",
        difficultyLevel = "Intermediate",
        scoreAchieved = 84,
        mistakesRecordedCount = 2,
        reasoningQualityScore = 80,
        confidenceStated = "HIGH",
        timeSpentSeconds = 620,
        attemptNumber = 1,
        strength = EvidenceStrength.STRONG,
        sourceType = EvidenceSourceType.SOC_INVESTIGATION,
        verificationStatus = EvidenceVerificationStatus.AUTOMATED_SANDBOX_VERIFIED,
        proofSnippet = "Isolated WIN-SRV-FINANCE in 78s; blocked outbound C2 beacon to 198.51.100.44:8443."
      ),
      EvidenceItem(
        id = "evi_903",
        timestamp = "2026-08-27 17:30",
        activityTitle = "Voice Crisis Drill: Panicked CFO Ransomware Briefing",
        skillDomain = "Crisis Communication",
        subskill = "Jargon-Free Executive De-escalation",
        difficultyLevel = "Intermediate",
        scoreAchieved = 74,
        mistakesRecordedCount = 2,
        reasoningQualityScore = 78,
        confidenceStated = "MEDIUM",
        timeSpentSeconds = 180,
        attemptNumber = 1,
        strength = EvidenceStrength.MODERATE,
        sourceType = EvidenceSourceType.VOICE_EXPLANATION,
        verificationStatus = EvidenceVerificationStatus.AUTOMATED_SANDBOX_VERIFIED,
        proofSnippet = "Calmness 78%, Clarity 82%, Jargon score 65%. Clear containment status communicated without technical panic."
      ),
      EvidenceItem(
        id = "evi_904",
        timestamp = "2026-08-26 14:20",
        activityTitle = "Active Recall: TLS 1.3 Handshake & Ephemeral Diffie-Hellman",
        skillDomain = "Cryptography",
        subskill = "RFC 8446 Protocol State Machine",
        difficultyLevel = "Intermediate",
        scoreAchieved = 95,
        mistakesRecordedCount = 0,
        reasoningQualityScore = 92,
        confidenceStated = "HIGH",
        timeSpentSeconds = 300,
        attemptNumber = 1,
        strength = EvidenceStrength.MODERATE,
        sourceType = EvidenceSourceType.FLASHCARD_RECALL,
        verificationStatus = EvidenceVerificationStatus.AUTOMATED_SANDBOX_VERIFIED,
        proofSnippet = "Correctly derived Forward Secrecy advantages of ECDHE over static RSA key exchange."
      )
    )
  )
  val evidenceStream: StateFlow<List<EvidenceItem>> = _evidenceStream.asStateFlow()

  // ============================================================================
  // 3. MISTAKE DNA 2.0 & COGNITIVE AUTOPSIES
  // ============================================================================

  private val initialMistakeEntries = listOf(
    MistakeDnaEntry(
      id = "mst_01",
      archetype = MistakeArchetype.PREMATURE_CLOSURE,
      occurrenceCount = 4,
      lastOccurrenceDate = "2026-08-27",
      contextWhereOccurred = "Alert #9042 and Purple Team Duel Staging",
      detectedPatternDescription = "You stopped looking for persistence after identifying the primary benign script, allowing a secondary scheduled task to go undetected.",
      correctiveDrillTitle = "Multi-Stage Persistence & Secondary Egress Triage Drill",
      correctiveDrillRoute = "lab_persistence_triage"
    ),
    MistakeDnaEntry(
      id = "mst_02",
      archetype = MistakeArchetype.CONFIRMATION_BIAS,
      occurrenceCount = 3,
      lastOccurrenceDate = "2026-08-25",
      contextWhereOccurred = "Network Forensics PCAP Analysis",
      detectedPatternDescription = "Over-indexed on an initial phishing email hypothesis and ignored DNS beaconing telemetry originating from a different workstation.",
      correctiveDrillTitle = "Hypothesis Falsification & Decoy Telemetry Drill",
      correctiveDrillRoute = "lab_hypothesis_falsification"
    ),
    MistakeDnaEntry(
      id = "mst_03",
      archetype = MistakeArchetype.WEAK_TIMELINE_REASONING,
      occurrenceCount = 2,
      lastOccurrenceDate = "2026-08-24",
      contextWhereOccurred = "DFIR Event Log Correlation",
      detectedPatternDescription = "Attempted to attribute execution without first establishing whether authentication event occurred prior to or after process creation.",
      correctiveDrillTitle = "Chronological Attack Sequence Reconstruction",
      correctiveDrillRoute = "lab_timeline_reconstruction"
    ),
    MistakeDnaEntry(
      id = "mst_04",
      archetype = MistakeArchetype.WEAK_COMMUNICATION,
      occurrenceCount = 3,
      lastOccurrenceDate = "2026-08-27",
      contextWhereOccurred = "Voice SOC Crisis Call with CFO",
      detectedPatternDescription = "Used dense protocol acronyms (C2, AS-REP, DLL Sideloading) instead of translating risk to business continuity and operational downtime.",
      correctiveDrillTitle = "Executive Plain-English Translation Drill",
      correctiveDrillRoute = "voice_executive_drill"
    )
  )

  private val _mistakeDnaProfile = MutableStateFlow(
    MistakeDnaProfile(
      totalMistakesCataloged = 12,
      dominantArchetype = MistakeArchetype.PREMATURE_CLOSURE,
      highRiskPatterns = initialMistakeEntries,
      cognitiveResilienceScore = 74
    )
  )
  val mistakeDnaProfile: StateFlow<MistakeDnaProfile> = _mistakeDnaProfile.asStateFlow()

  // ============================================================================
  // 4. STUDENT COGNITIVE STATE ENGINE
  // ============================================================================

  private val _studentState = MutableStateFlow(
    StudentStateEstimate(
      currentState = StudentCognitiveState.READY_FOR_CHALLENGE,
      confidenceScore = 84,
      recentActivityVelocity = "3.8 sessions / day",
      diagnosticRationale = "Learner recently completed purple team duel with 88% precision and demonstrated high calibration accuracy. Ready for high-concurrency SOC shift simulations."
    )
  )
  val studentState: StateFlow<StudentStateEstimate> = _studentState.asStateFlow()

  // ============================================================================
  // 5. TIME-AWARE NEXT BEST ACTION ENGINE (1 Primary + 3 Optional Actions)
  // ============================================================================

  private val _selectedTimeBudget = MutableStateFlow(TimeAvailabilityOption.MIN_30)
  val selectedTimeBudget: StateFlow<TimeAvailabilityOption> = _selectedTimeBudget.asStateFlow()

  private val _timeFilteredMission = MutableStateFlow(
    TimeFilteredRecommendation(
      selectedTime = TimeAvailabilityOption.MIN_30,
      mission = MissionItem(
        id = "nba_primary_01",
        estimatedMinutes = 24,
        title = "Investigate Suspicious Encoded PowerShell (Alert #9042)",
        subtitle = "Correlate Sysmon Event 1 & 3 telemetry without premature closure traps.",
        activityType = MissionActivityType.INVESTIGATION,
        targetSkill = "Sysmon & Process Tree Analysis",
        navigationRoute = "soc_shift"
      ),
      pedagogicalModel = AdaptiveLearningModel.SCENARIO_BASED,
      targetedCompetency = "Investigation & Triage (+4 expected)",
      expectedScoreDelta = "Investigation +4, Reasoning +3, SOC Readiness +2"
    )
  )
  val timeFilteredMission: StateFlow<TimeFilteredRecommendation> = _timeFilteredMission.asStateFlow()

  // ============================================================================
  // 6. REASONING GRAPH 2.0 SESSION
  // ============================================================================

  private val _reasoningGraphSession = MutableStateFlow(
    ReasoningGraphSession(
      sessionId = "rsg_2026_090",
      incidentScenarioTitle = "Intrusion Triage: Cobalt Strike Named Pipe Execution",
      overallThinkingProcessScore = 82,
      evidenceFirstScore = 88,
      timelineConstructionScore = 84,
      hypothesisFalsificationScore = 74,
      steps = listOf(
        ReasoningStepNode(
          stepNumber = 1,
          type = ReasoningGraphStepType.EVIDENCE_INGEST,
          learnerActionDescription = "Ingested Sysmon Event ID 1 for powershell.exe with Base64 argument.",
          referenceModelOptimalAction = "Ingest Sysmon Event ID 1 and record ParentProcessGuid immediately.",
          alignmentVerdict = "OPTIMAL_PATH",
          timeSpentSeconds = 45
        ),
        ReasoningStepNode(
          stepNumber = 2,
          type = ReasoningGraphStepType.OBSERVATION,
          learnerActionDescription = "Decoded Base64 payload containing IEX download cradle.",
          referenceModelOptimalAction = "Decode payload and extract destination IP and URI.",
          alignmentVerdict = "OPTIMAL_PATH",
          timeSpentSeconds = 90
        ),
        ReasoningStepNode(
          stepNumber = 3,
          type = ReasoningGraphStepType.HYPOTHESIS_FORMED,
          learnerActionDescription = "Hypothesized user clicked phishing link in Outlook.",
          referenceModelOptimalAction = "Form dual hypotheses: Email phish vs External RDP brute force.",
          alignmentVerdict = "SUBOPTIMAL_DETOUR",
          timeSpentSeconds = 60
        ),
        ReasoningStepNode(
          stepNumber = 4,
          type = ReasoningGraphStepType.HYPOTHESIS_TESTED,
          learnerActionDescription = "Queried Outlook parent process tree; discovered parent was actually wmiprvse.exe (WMI lateral movement).",
          referenceModelOptimalAction = "Query parent process ID before assuming email vector.",
          alignmentVerdict = "OPTIMAL_PATH",
          timeSpentSeconds = 120
        ),
        ReasoningStepNode(
          stepNumber = 5,
          type = ReasoningGraphStepType.HYPOTHESIS_REJECTED,
          learnerActionDescription = "Rejected phishing hypothesis based on WMI execution evidence.",
          referenceModelOptimalAction = "Reject initial hypothesis and pivot to lateral movement investigation.",
          alignmentVerdict = "OPTIMAL_PATH",
          timeSpentSeconds = 30
        ),
        ReasoningStepNode(
          stepNumber = 6,
          type = ReasoningGraphStepType.DECISION_POINT,
          learnerActionDescription = "Decided to isolate host WIN-FINANCE-04 and block source IP at firewall.",
          referenceModelOptimalAction = "Isolate host and harvest memory dump before process termination.",
          alignmentVerdict = "OPTIMAL_PATH",
          timeSpentSeconds = 50
        ),
        ReasoningStepNode(
          stepNumber = 7,
          type = ReasoningGraphStepType.ACTION_EXECUTED,
          learnerActionDescription = "Executed network containment command via EDR CLI.",
          referenceModelOptimalAction = "Execute network containment and verify no active sessions remain.",
          alignmentVerdict = "OPTIMAL_PATH",
          timeSpentSeconds = 40
        ),
        ReasoningStepNode(
          stepNumber = 8,
          type = ReasoningGraphStepType.OUTCOME_VERIFIED,
          learnerActionDescription = "Verified zero outbound packets to C2 IP 198.51.100.44.",
          referenceModelOptimalAction = "Verify egress zero-traffic and export telemetry for debrief.",
          alignmentVerdict = "OPTIMAL_PATH",
          timeSpentSeconds = 35
        )
      ),
      aiDebriefInsight = "Strong evidence-first behavior. You avoided premature closure by checking process lineage when initial phishing assumption failed. Main improvement: Form competing hypotheses earlier in step 3."
    )
  )
  val reasoningGraphSession: StateFlow<ReasoningGraphSession> = _reasoningGraphSession.asStateFlow()

  // ============================================================================
  // 7. PROJECT EVIDENCE CARDS
  // ============================================================================

  private val _projectEvidenceCards = MutableStateFlow(
    listOf(
      ProjectEvidenceCard(
        projectId = "prj_01",
        title = "Enterprise Sigma Detection Engine for WMI Lateral Movement",
        problemStatement = "Detecting fileless WMI lateral movement (MITRE T1047) across 500+ Windows endpoints without noisy false alarms.",
        architectureDescription = "Configured Sysmon Event ID 1/3 rules, authored custom Sigma YAML detection signatures, and verified against simulated Cobalt Strike telemetry.",
        technologiesUsed = listOf("Sigma Rules", "Sysmon", "Splunk / Wazuh", "YAML", "PowerShell"),
        securityDecisionsMade = listOf(
          "Excluded legitimate SCCM management service accounts to prevent alert fatigue.",
          "Enforced strict parent-child correlation requiring wmiprvse.exe -> cmd.exe / powershell.exe lineage."
        ),
        threatModelFramework = "MITRE ATT&CK T1047 & STRIDE Threat Model",
        verificationProofSnippet = "Verified rule against 5,000 raw event stream; achieved 100% true-positive capture with 0% false positives.",
        gitEvidenceLink = "github.com/aegora-evidence/vance-wmi-sigma-engine",
        demoVerificationBadge = "Cryptographically Sealed (Proof #AEG-SIGMA-9942)",
        lessonsLearned = "Edge cases in service account naming conventions require automated exception baselining.",
        targetCareerFamily = CyberCareerFamily.BLUE_TEAM
      ),
      ProjectEvidenceCard(
        projectId = "prj_02",
        title = "Automated CloudTrail S3 Ransomware Canary & Isolation Guardrail",
        problemStatement = "Detecting mass unauthorized encryption of S3 buckets and revoking IAM STS sessions in under 60 seconds.",
        architectureDescription = "EventBridge rule streaming CloudTrail S3 PutObject events to AWS Lambda python guardrail with automated IAM role policy detachment.",
        technologiesUsed = listOf("AWS CloudTrail", "EventBridge", "AWS Lambda (Python)", "IAM STS", "Terraform"),
        securityDecisionsMade = listOf(
          "Implemented rate-limiting threshold (50 encryption ops / 5s) to avoid killing batch ETL pipelines.",
          "Stored immutable audit trail in separate locked S3 bucket with MFA delete."
        ),
        threatModelFramework = "CIS AWS Foundations Benchmark v1.4",
        verificationProofSnippet = "Lambda contained simulated ransomware script in 42 seconds; isolated attacker IAM session successfully.",
        gitEvidenceLink = "github.com/aegora-evidence/vance-s3-ransomware-canary",
        demoVerificationBadge = "Verified Sandbox Pass (Proof #AEG-CLOUD-1049)",
        lessonsLearned = "IAM role detachment must be paired with active session invalidation to revoke existing temporary STS credentials.",
        targetCareerFamily = CyberCareerFamily.CLOUD_SECURITY
      )
    )
  )
  val projectEvidenceCards: StateFlow<List<ProjectEvidenceCard>> = _projectEvidenceCards.asStateFlow()

  // ============================================================================
  // 8. WEEKLY CYBER INTELLIGENCE PERSONAL REPORT
  // ============================================================================

  private val _weeklyReport = MutableStateFlow(
    WeeklyIntelligenceReport(
      weekLabel = "Week 35 (Aug 21 - Aug 27, 2026)",
      totalStudyHours = 14.5,
      verifiedEvidenceItemsCount = 18,
      conceptsMastered = listOf(
        "Sysmon Event ID 1/3 Correlation",
        "TLS 1.3 Key Exchange Mechanics",
        "Cobalt Strike Named Pipe Detection",
        "Incident Triage & Rapid Host Isolation"
      ),
      conceptsDecaying = listOf(
        "DNS Tunneling TXT Query Forensics (Decayed -6%)",
        "Ghidra Decompiler Navigation (Decayed -4%)"
      ),
      areasStruggledWith = listOf(
        "Premature closure during multi-stage persistence investigations",
        "Jargon-heavy executive communication with non-technical personas"
      ),
      dominantMistakePattern = "Premature Closure (4 occurrences cataloged)",
      investigationStyleVerdict = "Systematic & Evidence-First (88% correlation score)",
      careerReadinessDelta = "+4% toward SOC Analyst L1 (Now at 69%)",
      keyCyberThreatsAnalyzed = listOf(
        "CVE-2026-4401: Chromium V8 Zero-Day Remote Code Execution",
        "Qakbot Resurgence utilizing ISO Container Sideloading",
        "New Post-Quantum ML-KEM Migration Guidance"
      ),
      recommendedNextWeekPlan = listOf(
        "Complete 5-Minute DNS Tunneling Resurrection Drill",
        "Practice Voice SOC Executive Briefing without jargon traps",
        "Publish Sigma Detection Project to verifiable Skill Passport"
      ),
      projectOpportunities = listOf(
        "Author Sigma rule for CVE-2026-4401 telemetry",
        "Build Kubernetes Pod Exec Audit log parser"
      ),
      interviewReadinessScore = 72
    )
  )
  val weeklyReport: StateFlow<WeeklyIntelligenceReport> = _weeklyReport.asStateFlow()

  // ============================================================================
  // 9. DEVELOPER INTELLIGENCE TRACE & OBSERVABILITY
  // ============================================================================

  private val _intelligenceTrace = MutableStateFlow<List<IntelligenceTraceEntry>>(
    listOf(
      IntelligenceTraceEntry(
        traceId = "trc_101",
        timestamp = "2026-08-27 21:30",
        learnerAction = "Completed Purple Team Arena 5-Round Duel (Round 5 Debrief)",
        evidenceGenerated = "Proof #EVI_901: LSASS Process Access GrantedAccess 0x1010",
        skillAffected = "Endpoint Security & Memory Forensics",
        scoreDelta = "+5 Practical, +4 Investigation",
        detectedCognitiveSignal = "READY_FOR_CHALLENGE (Calibration ±4%)",
        recommendationOutput = "Promoted Real SOC Shift Simulator Alert Queue to Today's Mission"
      ),
      IntelligenceTraceEntry(
        traceId = "trc_102",
        timestamp = "2026-08-27 19:40",
        learnerAction = "Encountered Premature Closure in Alert #9042",
        evidenceGenerated = "Proof #EVI_902: Closed alert after finding benign script, missed scheduled task",
        skillAffected = "Investigation & Triage",
        scoreDelta = "-2 Reasoning (Mistake DNA Logged)",
        detectedCognitiveSignal = "PREMATURE_CLOSURE (Occurrence #4)",
        recommendationOutput = "Scheduled Cognitive Autopsy on Secondary Persistence"
      ),
      IntelligenceTraceEntry(
        traceId = "trc_103",
        timestamp = "2026-08-27 18:10",
        learnerAction = "Voice Crisis Drill: Panicked CFO Call",
        evidenceGenerated = "Proof #EVI_903: Calmness 78%, Clarity 82%, Jargon 65%",
        skillAffected = "Executive & Crisis Communication",
        scoreDelta = "+8 Communication (Rapid Recovery)",
        detectedCognitiveSignal = "WEAK_COMMUNICATION (Jargon Traps)",
        recommendationOutput = "Suggested Plain-English Executive Summary Drill"
      )
    )
  )
  val intelligenceTrace: StateFlow<List<IntelligenceTraceEntry>> = _intelligenceTrace.asStateFlow()

  // ============================================================================
  // 10. SYNC STATUS
  // ============================================================================

  private val _syncStatus = MutableStateFlow(SyncStatus.LOCAL_ACTIVE)
  val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

  // ============================================================================
  // 11. CORE PIPELINE METHODS (The Intelligence Connective Loop)
  // ============================================================================

  /**
   * Continuous closed learning loop:
   * Action -> Evidence -> Normalizer -> Cyber Twin -> Cognitive Diagnostics -> Next Best Action
   */
  fun recordLearnerActionAndEvidence(
    activityTitle: String,
    skillDomain: String,
    subskill: String,
    difficulty: String,
    score: Int,
    mistakesCount: Int,
    reasoningScore: Int,
    statedConfidence: String,
    timeSpentSeconds: Int,
    strength: EvidenceStrength,
    sourceType: EvidenceSourceType,
    proofSnippet: String,
    mistakeArchetype: MistakeArchetype? = null
  ) {
    val timestamp = dateFormat.format(Date())
    val evidenceId = "evi_${System.currentTimeMillis().toString().takeLast(4)}"

    val newEvidence = EvidenceItem(
      id = evidenceId,
      timestamp = timestamp,
      activityTitle = activityTitle,
      skillDomain = skillDomain,
      subskill = subskill,
      difficultyLevel = difficulty,
      scoreAchieved = score,
      mistakesRecordedCount = mistakesCount,
      reasoningQualityScore = reasoningScore,
      confidenceStated = statedConfidence,
      timeSpentSeconds = timeSpentSeconds,
      attemptNumber = 1,
      strength = strength,
      sourceType = sourceType,
      verificationStatus = EvidenceVerificationStatus.CRYPTOGRAPHICALLY_SEALED,
      proofSnippet = proofSnippet
    )

    // 1. Append evidence
    val updatedEvidence = listOf(newEvidence) + _evidenceStream.value
    _evidenceStream.value = updatedEvidence

    // 2. Update Cyber Twin 3.0 Dimensions
    val updatedDimensions = _cyberTwin.value.dimensions.map { dim ->
      if (dim.title.contains(skillDomain, ignoreCase = true) || dim.dimensionKey == "practical" || dim.dimensionKey == "investigation") {
        val scoreBoost = if (score >= 80) 2 else if (score >= 60) 1 else -1
        val newScore = (dim.score + scoreBoost).coerceIn(10, 100)
        dim.copy(
          score = newScore,
          evidenceCount = dim.evidenceCount + 1,
          recentTrend = if (scoreBoost > 0) "ASCENDING (+${scoreBoost}%)" else "STABLE",
          lastDemonstratedDate = shortDateFormat.format(Date()),
          strongestEvidenceProof = proofSnippet
        )
      } else {
        dim
      }
    }

    val overallReadiness = (updatedDimensions.map { it.score }.average()).toInt()
    _cyberTwin.value = _cyberTwin.value.copy(
      overallCareerReadinessPercent = overallReadiness,
      dimensions = updatedDimensions,
      lastSyncTimestamp = timestamp
    )

    // 3. Update Mistake DNA if mistake occurred
    if (mistakeArchetype != null && mistakesCount > 0) {
      val existingPatterns = _mistakeDnaProfile.value.highRiskPatterns.map { entry ->
        if (entry.archetype == mistakeArchetype) {
          entry.copy(
            occurrenceCount = entry.occurrenceCount + 1,
            lastOccurrenceDate = shortDateFormat.format(Date()),
            contextWhereOccurred = activityTitle
          )
        } else {
          entry
        }
      }
      _mistakeDnaProfile.value = _mistakeDnaProfile.value.copy(
        totalMistakesCataloged = _mistakeDnaProfile.value.totalMistakesCataloged + 1,
        highRiskPatterns = existingPatterns
      )
    }

    // 4. Update Cognitive State
    val newState = when {
      mistakesCount >= 3 -> StudentCognitiveState.STRUGGLING
      score >= 85 && reasoningScore >= 85 -> StudentCognitiveState.READY_FOR_CHALLENGE
      score >= 75 -> StudentCognitiveState.IMPROVING
      statedConfidence == "HIGH" && score < 60 -> StudentCognitiveState.OVERCONFIDENT
      else -> StudentCognitiveState.FOCUSED
    }
    _studentState.value = _studentState.value.copy(
      currentState = newState,
      diagnosticRationale = "Derived from recent activity '$activityTitle' (Score $score%, Mistakes: $mistakesCount)."
    )

    // 5. Add Trace Log
    val traceEntry = IntelligenceTraceEntry(
      traceId = "trc_${UUID.randomUUID().toString().take(6)}",
      timestamp = timestamp,
      learnerAction = activityTitle,
      evidenceGenerated = "Proof #${newEvidence.id}: $proofSnippet",
      skillAffected = "$skillDomain ($subskill)",
      scoreDelta = "+${if (score >= 70) 3 else 1} Evidence Count",
      detectedCognitiveSignal = newState.displayName,
      recommendationOutput = "Refreshed Today's Mission & Cyber Twin"
    )
    _intelligenceTrace.value = listOf(traceEntry) + _intelligenceTrace.value
  }

  /**
   * Set Time Availability & Adapt Recommendation
   */
  fun setTimeBudget(timeOption: TimeAvailabilityOption) {
    _selectedTimeBudget.value = timeOption
    val (missionItem, pedModel, comp, delta) = when (timeOption) {
      TimeAvailabilityOption.MIN_5 -> Quad(
        MissionItem(
          id = "nba_5m",
          estimatedMinutes = 5,
          title = "5-Min Skill Resurrection: DNS Tunneling Detection",
          subtitle = "Forced active recall on base64 TXT record queries before total decay.",
          activityType = MissionActivityType.RECOVERY,
          targetSkill = "DNS Protocol Forensics",
          navigationRoute = "skill_decay"
        ),
        AdaptiveLearningModel.ACTIVE_RECALL,
        "Retention & Ebbinghaus Decay (+8 expected)",
        "Retention +8, DNS Forensic Mastery +5"
      )
      TimeAvailabilityOption.MIN_15 -> Quad(
        MissionItem(
          id = "nba_15m",
          estimatedMinutes = 15,
          title = "15-Min Concept Collision: OAuth2 vs SAML Token Spoofing",
          subtitle = "Contrast token validation mechanics and resolve authentication blindspots.",
          activityType = MissionActivityType.MISTAKE_CORRECTION,
          targetSkill = "Identity & Token Security",
          navigationRoute = "genome"
        ),
        AdaptiveLearningModel.COMPARATIVE_LEARNING,
        "Knowledge & Prerequisite Clarity (+6 expected)",
        "Theoretical Knowledge +6, Cross-Domain +4"
      )
      TimeAvailabilityOption.MIN_30 -> Quad(
        MissionItem(
          id = "nba_30m",
          estimatedMinutes = 24,
          title = "Investigate Suspicious Encoded PowerShell (Alert #9042)",
          subtitle = "Correlate Sysmon Event 1 & 3 telemetry without premature closure traps.",
          activityType = MissionActivityType.INVESTIGATION,
          targetSkill = "Sysmon & Process Tree Analysis",
          navigationRoute = "soc_shift"
        ),
        AdaptiveLearningModel.SCENARIO_BASED,
        "Investigation & Triage (+4 expected)",
        "Investigation +4, Reasoning +3, SOC Readiness +2"
      )
      TimeAvailabilityOption.HOUR_1 -> Quad(
        MissionItem(
          id = "nba_60m",
          estimatedMinutes = 50,
          title = "Purple Team Arena: 5-Round Self-vs-Self Intrusion Duel",
          subtitle = "Stage red attack, synthesize EDR logs, triage blue defense, and face self-debrief.",
          activityType = MissionActivityType.PURPLE_TEAM,
          targetSkill = "Adversary Simulation & Defense",
          navigationRoute = "purple_arena"
        ),
        AdaptiveLearningModel.SIMULATION_LEARNING,
        "Practical Ability & Decision Making (+7 expected)",
        "Practical +7, Decision Making +6, SOC Readiness +5"
      )
      TimeAvailabilityOption.HOURS_3 -> Quad(
        MissionItem(
          id = "nba_180m",
          estimatedMinutes = 150,
          title = "Capstone Lab: Build & Verify CloudTrail S3 Ransomware Canary",
          subtitle = "Author automated Lambda guardrail, test under simulated attack, and generate CV card.",
          activityType = MissionActivityType.INVESTIGATION,
          targetSkill = "Cloud Security Architecture",
          navigationRoute = "project_studio"
        ),
        AdaptiveLearningModel.PROJECT_BASED,
        "Career Readiness & Portfolio Proof (+10 expected)",
        "Career Readiness +10, Practical +9, Verified CV Card Generated"
      )
    }

    _timeFilteredMission.value = TimeFilteredRecommendation(
      selectedTime = timeOption,
      mission = missionItem,
      pedagogicalModel = pedModel,
      targetedCompetency = comp,
      expectedScoreDelta = delta
    )
  }

  /**
   * Reverse Job Description Analyzer
   */
  fun analyzeJobDescription(rawJobText: String): V81JobRoadmapAnalysis {
    val hasCloud = rawJobText.contains("cloud", ignoreCase = true) || rawJobText.contains("aws", ignoreCase = true) || rawJobText.contains("azure", ignoreCase = true)
    val hasSiem = rawJobText.contains("siem", ignoreCase = true) || rawJobText.contains("splunk", ignoreCase = true) || rawJobText.contains("soc", ignoreCase = true)
    val hasPython = rawJobText.contains("python", ignoreCase = true) || rawJobText.contains("scripting", ignoreCase = true)
    val hasMitre = rawJobText.contains("mitre", ignoreCase = true) || rawJobText.contains("incident", ignoreCase = true)
    val hasCerts = rawJobText.contains("security+", ignoreCase = true) || rawJobText.contains("cysa+", ignoreCase = true) || rawJobText.contains("btl1", ignoreCase = true)

    val requirements = mutableListOf<ExtractedJobRequirement>()

    requirements.add(
      ExtractedJobRequirement(
        category = "CORE_SKILL",
        name = "SIEM & Telemetry Triage (Splunk/Sysmon)",
        importance = "REQUIRED",
        learnerMasteryStatus = "DEMONSTRATED",
        matchingEvidenceId = "evi_902"
      )
    )
    requirements.add(
      ExtractedJobRequirement(
        category = "CORE_SKILL",
        name = "MITRE ATT&CK Incident Classification",
        importance = "REQUIRED",
        learnerMasteryStatus = "DEMONSTRATED",
        matchingEvidenceId = "evi_901"
      )
    )
    requirements.add(
      ExtractedJobRequirement(
        category = "TOOL",
        name = "Endpoint Detection & Response (Sysmon/EDR)",
        importance = "REQUIRED",
        learnerMasteryStatus = "DEMONSTRATED",
        matchingEvidenceId = "evi_901"
      )
    )

    if (hasCloud) {
      requirements.add(
        ExtractedJobRequirement(
          category = "CORE_SKILL",
          name = "AWS CloudTrail & GuardDuty Monitoring",
          importance = "REQUIRED",
          learnerMasteryStatus = "IN_PROGRESS",
          matchingEvidenceId = "prj_02"
        )
      )
    }

    if (hasPython) {
      requirements.add(
        ExtractedJobRequirement(
          category = "TOOL",
          name = "Python / Bash Scripting for Automation",
          importance = "PREFERRED",
          learnerMasteryStatus = "DEMONSTRATED",
          matchingEvidenceId = "prj_01"
        )
      )
    }

    requirements.add(
      ExtractedJobRequirement(
        category = "FRAMEWORK",
        name = "Sigma Rule Authoring & Detection Engineering",
        importance = "PREFERRED",
        learnerMasteryStatus = "MISSING",
        matchingEvidenceId = null
      )
    )

    requirements.add(
      ExtractedJobRequirement(
        category = "SOFT_SKILL",
        name = "Crisis Communication & Executive Incident Briefings",
        importance = "REQUIRED",
        learnerMasteryStatus = "IN_PROGRESS",
        matchingEvidenceId = "evi_903"
      )
    )

    val demonstratedCount = requirements.count { it.learnerMasteryStatus == "DEMONSTRATED" }
    val matchPercent = ((demonstratedCount.toDouble() / requirements.size.toDouble()) * 100).toInt()

    return V81JobRoadmapAnalysis(
      extractedRoleTitle = "Security Operations Center (SOC) Analyst L1 / L2",
      targetCompanyOrIndustry = "Enterprise FinTech / Cloud Defense",
      overallMatchPercentage = matchPercent,
      demonstratedSkillsCount = demonstratedCount,
      totalRequiredSkillsCount = requirements.size,
      requirements = requirements,
      missingSkillsSummary = listOf(
        "Sigma Rule Detection Engineering",
        "Kubernetes & Container Audit Log Triage",
        "Court-ready Incident Dossier Authoring"
      ),
      evidenceGaps = listOf(
        "Needs 1 verified Sigma rule repo on GitHub",
        "Needs 1 live multi-host lateral movement containment recording"
      ),
      projectGaps = listOf(
        "Complete 'Enterprise Sigma Detection Engine' capstone project"
      ),
      interviewGaps = listOf(
        "Practice scenario interview: 'Explain how you investigate a Pass-the-Hash alert without technical jargon.'"
      ),
      shortestPreparationRoadmap = listOf(
        "1. Complete Sigma Rule authoring lab (2 hours)",
        "2. Run 1 Real SOC Shift Simulator under 30-min time pressure",
        "3. Record 1 Voice Crisis Drill with CFO persona",
        "4. Export Cryptographic Skill Passport PDF"
      )
    )
  }

  // ============================================================================
  // 12. DEMONSTRATED CAPABILITY INTELLIGENCE PIPELINE
  // ============================================================================

  /**
   * Real Evidence -> DemonstratedCapabilityEngine -> CapabilityAssessmentResult
   * -> CyberTwinAdapter -> Existing Cyber Twin 6.0 -> Capability Bottleneck
   * -> NextBestActionAdapter -> Existing Next Best Action -> Real Mission
   */
  suspend fun executeCapabilityIntelligencePipeline(
    learnerId: String,
    repository: DemonstratedCapabilityRepository,
    engine: DemonstratedCapabilityEngine = DemonstratedCapabilityEngine(),
    cyberTwinAdapter: CyberTwinAdapter = DefaultCyberTwinAdapter(),
    nextBestActionAdapter: NextBestActionAdapter = DefaultNextBestActionAdapter(),
    currentTime: Long = System.currentTimeMillis()
  ): CapabilityPipelineExecutionResult {
    require(learnerId.isNotBlank()) { "Learner ID cannot be blank" }

    // 1. Evaluate all persisted capabilities and evidence for this learner from Room
    val results = engine.evaluateAllForLearner(
      learnerId = learnerId,
      repository = repository,
      currentTime = currentTime
    )

    // 2. Map capability assessment results through CyberTwinAdapter
    val twinSnapshot = cyberTwinAdapter.mapToCyberTwinSnapshot(
      learnerId = learnerId,
      results = results
    )

    // 3. Update existing Cyber Twin 6.0 state
    CyberOperatingSystemV12Engine.updateCyberTwin60(twinSnapshot)

    // 4. Identify primary capability bottleneck across failed gates
    val primaryBottleneck = results.firstOrNull { !it.isDemonstrated && it.limitingGate != null }?.limitingGate

    // 5. Synthesize Next Best Actions grounded in capability bottlenecks
    val nextActions = nextBestActionAdapter.generateNextActions(results)

    // 6. Update existing Next Best Action state flow in AegoraRepository
    AegoraRepository.updatePredictiveNextActions(nextActions)

    // 7. Record pipeline trace in orchestrator intelligence log
    val traceEntry = IntelligenceTraceEntry(
      traceId = "trc_cap_${UUID.randomUUID().toString().take(6)}",
      timestamp = dateFormat.format(Date(currentTime)),
      learnerAction = "Capability Intelligence Pipeline Evaluation",
      evidenceGenerated = "${results.size} capabilities evaluated across 7-Gate Rubric",
      skillAffected = if (results.isNotEmpty()) results.joinToString { it.name }.take(40) else "General Diagnostic",
      scoreDelta = "Cyber Twin MMR: ${twinSnapshot.overallScore}",
      detectedCognitiveSignal = if (primaryBottleneck != null) "Bottleneck: ${primaryBottleneck.displayName}" else "All Gates Passed",
      recommendationOutput = "Generated ${nextActions.size} Next Best Actions"
    )
    _intelligenceTrace.value = listOf(traceEntry) + _intelligenceTrace.value

    return CapabilityPipelineExecutionResult(
      learnerId = learnerId,
      capabilityResults = results,
      cyberTwinSnapshot = twinSnapshot,
      primaryBottleneckGate = primaryBottleneck,
      nextBestActions = nextActions
    )
  }

  /**
   * Evaluates a single persisted capability, updates its Room entity, and runs full pipeline propagation.
   */
  suspend fun evaluateCapabilityAndPropagate(
    capabilityId: String,
    repository: DemonstratedCapabilityRepository,
    engine: DemonstratedCapabilityEngine = DemonstratedCapabilityEngine(),
    cyberTwinAdapter: CyberTwinAdapter = DefaultCyberTwinAdapter(),
    nextBestActionAdapter: NextBestActionAdapter = DefaultNextBestActionAdapter(),
    currentTime: Long = System.currentTimeMillis()
  ): CapabilityPipelineExecutionResult? {
    val singleResult = engine.evaluateAndPersist(
      capabilityId = capabilityId,
      repository = repository,
      currentTime = currentTime
    ) ?: return null

    return executeCapabilityIntelligencePipeline(
      learnerId = singleResult.learnerId,
      repository = repository,
      engine = engine,
      cyberTwinAdapter = cyberTwinAdapter,
      nextBestActionAdapter = nextBestActionAdapter,
      currentTime = currentTime
    )
  }

  data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
