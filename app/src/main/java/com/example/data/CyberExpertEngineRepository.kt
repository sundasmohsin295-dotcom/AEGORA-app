package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Cyber Twin & Expert Engine Repository.
 * Tracks granular capability over completion, dynamic twin nodes,
 * prerequisite diagnostic chains, and professional judgment missions.
 */
object CyberExpertEngineRepository {

  private val _cyberTwin = MutableStateFlow(
    PersonalCyberTwin(
      callsign = "VANCE-SOC",
      targetCareer = "SOC Analyst (Level 1-2)",
      currentExpertiseLevel = ExpertiseLevel.L3_GUIDED_PRACTICE,
      nextLevelGateRemaining = 4, // 4 more unassisted investigations to unlock L4 Independent Practice
      capabilityMatrix = MultiDimensionalCapabilityScore(
        technicalKnowledge = 78,
        practicalHandsOn = 64,
        hypothesisReasoning = 72,
        investigationDepth = 69,
        businessCommunication = 82,
        professionalJudgment = 75,
        overallIndependenceScore = 84
      ),
      skillsGrid = listOf(
        CyberTwinSkillNode(
          skillId = "skill_net_01",
          name = "TCP/IP & Packet Dissection",
          domain = "Networking",
          status = CompetencyStatus.STRONG,
          score = 88,
          currentLevel = ExpertiseLevel.L4_INDEPENDENT_PRACTICE,
          whyThisMatters = "Packet inspection is the baseline truth in every network intrusion triage.",
          lastPracticedDate = "2 days ago",
          verifiedEvidenceArtifact = "Wireshark PCAP Filter Drill • Hash 0x9AF8"
        ),
        CyberTwinSkillNode(
          skillId = "skill_linux_01",
          name = "Linux Syscall & Process Forensics",
          domain = "Systems",
          status = CompetencyStatus.STRONG,
          score = 85,
          currentLevel = ExpertiseLevel.L4_INDEPENDENT_PRACTICE,
          whyThisMatters = "Understanding /proc, fork, and execve prevents rootkit concealment.",
          lastPracticedDate = "Yesterday",
          verifiedEvidenceArtifact = "Ghidra Stack Frame Analysis • Hash 0xB4E1"
        ),
        CyberTwinSkillNode(
          skillId = "skill_siem_01",
          name = "SIEM Correlation & Splunk SPL",
          domain = "Detection",
          status = CompetencyStatus.DEVELOPING,
          score = 62,
          currentLevel = ExpertiseLevel.L3_GUIDED_PRACTICE,
          missingPrerequisites = listOf("Log Normalization & RFC 5424 Syslog Parsing"),
          whyThisMatters = "Correlation rules turn thousands of noisy raw events into actionable alerts.",
          lastPracticedDate = "3 days ago",
          verifiedEvidenceArtifact = "Kerberoasting SOC Triage • Hash 0x77C0"
        ),
        CyberTwinSkillNode(
          skillId = "skill_cloud_01",
          name = "AWS IAM Policy Escalation & STS",
          domain = "Cloud Security",
          status = CompetencyStatus.WEAK,
          score = 42,
          currentLevel = ExpertiseLevel.L2_UNDERSTANDING,
          missingPrerequisites = listOf("STS AssumeRole Trust Relationships", "JSON Policy Eval Engine"),
          whyThisMatters = "99% of cloud breaches stem from IAM wildcard privilege creep.",
          lastPracticedDate = "6 days ago",
          verifiedEvidenceArtifact = null
        ),
        CyberTwinSkillNode(
          skillId = "skill_dfir_01",
          name = "Memory Volatility & MFT Timelines",
          domain = "DFIR",
          status = CompetencyStatus.UNKNOWN,
          score = 0,
          currentLevel = ExpertiseLevel.L0_UNFAMILIAR,
          whyThisMatters = "Volatile memory contains active decryption keys and un-persisted payload injections.",
          lastPracticedDate = "Untested",
          verifiedEvidenceArtifact = null
        ),
        CyberTwinSkillNode(
          skillId = "skill_ebpf_01",
          name = "eBPF Kernel Telemetry Hooks",
          domain = "Advanced Detection",
          status = CompetencyStatus.UNKNOWN,
          score = 0,
          currentLevel = ExpertiseLevel.L0_UNFAMILIAR,
          whyThisMatters = "Zero-overhead kernel observation without brittle kernel modules.",
          lastPracticedDate = "Untested",
          verifiedEvidenceArtifact = null
        )
      ),
      activePrerequisiteChains = listOf(
        PrerequisiteChain(
          targetSkill = "SIEM Multi-Source Correlation",
          detectedGap = "Struggling to correlate Windows Event ID 4624 with Firewall Traffic",
          rootCause = "Missing Prerequisite: Syslog UTC Timestamp Normalization & NAT IP Translation",
          prescribedStep = "Run 10-Minute Network Address Translation & Syslog Normalization Drill",
          estimatedMins = 10,
          actionRoute = "lab_simulator"
        ),
        PrerequisiteChain(
          targetSkill = "AWS IAM Privilege Escalation",
          detectedGap = "Unable to evaluate PassRole permission boundary bypass",
          rootCause = "Missing Prerequisite: IAM JSON Evaluation Logic (Explicit Deny > Explicit Allow)",
          prescribedStep = "Complete IAM Policy Boundary Visualizer & Sandbox Lab",
          estimatedMins = 15,
          actionRoute = "learning"
        )
      ),
      dailyHighValueMission = "Today's High-Value Action: Investigate 3 Windows Event 4624/4672 Privilege Escalation alerts under 10-minute realistic SOC timer.",
      weeklyReviewSummary = "Demonstrated strong network packet reasoning (88/100). Cloud IAM is currently at risk of decay. Tutorial illusion detected in SIEM theory: high quiz score (95%) vs moderate hands-on speed (62%).",
      tutorialIllusionDetected = true
    )
  )
  val cyberTwin: StateFlow<PersonalCyberTwin> = _cyberTwin.asStateFlow()

  private val _judgmentScenarios = MutableStateFlow(
    listOf(
      RealisticJudgmentScenario(
        id = "scen_soc_01",
        title = "Three Simultaneous Alerts: Triage Under Pressure",
        rolePersona = "SOC Level 1 Analyst (Night Shift • 03:14 AM)",
        situationBrief = "Three concurrent alerts trigger in your SIEM queue within 90 seconds:\n" +
            "1. Alert A: Outbound connection from Core Financial Database to external IP (Port 443, 850MB transferred).\n" +
            "2. Alert B: Multiple failed Kerberos Pre-Auth tickets (Event ID 4771) on Domain Controller.\n" +
            "3. Alert C: CEO laptop generating 50 rapid DNS requests to newly registered .xyz domain.\n" +
            "You only have 3 minutes before the executive crisis escalation SLA window closes.",
        constraints = listOf(
          "Limited time window (3 mins)",
          "Cannot take the entire enterprise offline without verification",
          "CEO is currently on an international flight"
        ),
        choices = listOf(
          JudgmentChoice(
            id = "c1",
            actionText = "Immediately isolate the Core Financial DB host from the network and notify the on-call Incident Commander.",
            justificationPrompt = "Prioritize active data exfiltration impact on critical financial assets over credential guessing.",
            tradeOffs = "Causes temporary batch job disruption for accounting, but halts potential multi-gigabyte data breach.",
            professionalScore = 95,
            isOptimal = true
          ),
          JudgmentChoice(
            id = "c2",
            actionText = "Triage the CEO's laptop DNS alert first because executive devices hold high-profile access.",
            justificationPrompt = "Executive risk prioritization.",
            tradeOffs = "Leaves potential live exfiltration on the core financial database unaddressed during triage.",
            professionalScore = 55,
            isOptimal = false
          ),
          JudgmentChoice(
            id = "c3",
            actionText = "Block the Kerberos attacking IP on the firewall and reset the DC admin account.",
            justificationPrompt = "Stop potential active Active Directory takeover.",
            tradeOffs = "Event 4771 is often benign password spraying that didn't succeed; high probability of false positive.",
            professionalScore = 65,
            isOptimal = false
          )
        ),
        expertRationaleComparison = "Expert SOC Lead Reasoning: Alert A exhibits high confirmed data volume leaving the crown-jewel asset. Even if false positive, containment protocol mandates immediate host isolation while simultaneously triggering escalation. Alert B is unauthenticated spraying, and Alert C is single-endpoint DNS noise."
      )
    )
  )
  val judgmentScenarios: StateFlow<List<RealisticJudgmentScenario>> = _judgmentScenarios.asStateFlow()

  fun recordJudgmentDecision(scenarioId: String, choiceId: String) {
    // Updates twin state based on judgment demonstration
    val current = _cyberTwin.value
    val updatedMatrix = current.capabilityMatrix.copy(
      professionalJudgment = (current.capabilityMatrix.professionalJudgment + 3).coerceAtMost(100),
      overallIndependenceScore = (current.capabilityMatrix.overallIndependenceScore + 1).coerceAtMost(100)
    )
    _cyberTwin.value = current.copy(capabilityMatrix = updatedMatrix)
  }

  fun resolvePrerequisiteChain(targetSkill: String) {
    val current = _cyberTwin.value
    val updatedChains = current.activePrerequisiteChains.filterNot { it.targetSkill == targetSkill }
    _cyberTwin.value = current.copy(activePrerequisiteChains = updatedChains)
  }
}
