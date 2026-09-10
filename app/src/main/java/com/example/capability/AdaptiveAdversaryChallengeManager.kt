package com.example.capability

import com.example.model.FailureModeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

enum class AdaptiveChallengeTier {
  OBSERVED,
  REPEATED,
  HIGH_CONFIDENCE
}

data class AdaptiveChallengePolicy(
  val targetFailureMode: FailureModeType,
  val challengeTier: AdaptiveChallengeTier,
  val policyObjective: String,
  val adversaryRole: String,
  val recoveryCriteria: String,
  val evidenceRequirements: List<String>
)

data class AdaptiveAiClaim(
  val analystName: String,
  val claimText: String,
  val assertedEvidenceIds: List<String>,
  val recommendedAction: String,
  val confidencePercentage: Int
)

data class AdaptiveEvidenceItem(
  val id: String,
  val timestamp: String,
  val source: String,
  val eventType: String,
  val summary: String
)

data class AdaptiveActionOption(
  val id: String,
  val label: String,
  val description: String
)

data class LearnerSafeAdaptiveChallenge(
  val challengeId: String,
  val targetFailureMode: FailureModeType,
  val challengeTier: AdaptiveChallengeTier,
  val weaknessNarrative: String,
  val targetedSkill: String,
  val scenarioTitle: String,
  val scenarioBriefing: String,
  val aiAnalystClaim: AdaptiveAiClaim,
  val evidencePool: List<AdaptiveEvidenceItem>,
  val actionOptions: List<AdaptiveActionOption>
)

data class AuthoritativeAdaptiveChallengeState(
  val challengeId: String,
  val ownerAuthUid: String,
  val sourceFailurePatternId: String,
  val targetFailureMode: FailureModeType,
  val challengeTier: AdaptiveChallengeTier,
  val policy: AdaptiveChallengePolicy,
  val createdAt: String,
  val isUnsupportedPlantedTrap: Boolean,
  val authoritativeCorrectActionId: String,
  val authoritativeRequiredEvidenceIds: List<String>,
  val plantedTrapRationale: String,
  val learnerSafePayload: LearnerSafeAdaptiveChallenge
)

/**
 * Client/Local Service for Adaptive Adversary Challenges.
 * Enforces server authority:
 * - Client cannot choose its target failure mode
 * - Client cannot set challenge tier / confidence
 * - Client cannot modify trap state or ground-truth answer
 * - Client receives only LearnerSafeAdaptiveChallenge
 */
class AdaptiveAdversaryChallengeManager(
  private val failurePatternManager: AuthoritativeFailurePatternManager = AuthoritativeFailurePatternManager()
) {

  private val _activeChallenges = MutableStateFlow<Map<String, AuthoritativeAdaptiveChallengeState>>(emptyMap())
  val activeChallenges: StateFlow<Map<String, AuthoritativeAdaptiveChallengeState>> = _activeChallenges.asStateFlow()

  /**
   * Generates a targeted adaptive challenge based STRICTLY on the learner's authoritative failure patterns.
   * Disregards any client-suggested target mode or difficulty.
   */
  fun generateTargetedChallenge(
    authenticatedUid: String,
    suggestedClientTarget: FailureModeType? = null,
    suggestedTier: AdaptiveChallengeTier? = null
  ): LearnerSafeAdaptiveChallenge {
    if (authenticatedUid.isBlank()) {
      throw IllegalArgumentException("Authenticated UID is required to generate adaptive challenge.")
    }

    // 1. Authoritatively inspect learner's verified failure patterns
    val learnerPatterns = failurePatternManager.getPatternsForLearner(authenticatedUid)
    
    // Sort deterministically:
    // 1. Highest observation count
    // 2. Highest confidence tier
    // 3. Most recent observation
    val sortedPatterns = learnerPatterns.sortedWith(
      compareByDescending<AuthoritativeFailurePatternRecord> { it.observationCount }
        .thenByDescending { it.confidenceTier.ordinal }
        .thenByDescending { it.lastObservedTimestamp }
        .thenBy { it.pattern.name }
    )

    val targetPatternRecord = sortedPatterns.firstOrNull()
    val targetFailureMode = targetPatternRecord?.pattern ?: FailureModeType.INSUFFICIENT_CORRELATION
    val tier = when (targetPatternRecord?.confidenceTier) {
      FailurePatternConfidenceTier.HIGH_CONFIDENCE -> AdaptiveChallengeTier.HIGH_CONFIDENCE
      FailurePatternConfidenceTier.REPEATED -> AdaptiveChallengeTier.REPEATED
      else -> AdaptiveChallengeTier.OBSERVED
    }

    val policy = buildPolicy(targetFailureMode, tier)
    val challengeId = "chal_${targetFailureMode.name.lowercase()}_${System.currentTimeMillis()}"
    val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

    val template = buildChallengeTemplate(policy, challengeId)

    // Construct server-authoritative state containing hidden trap ground truth
    val authoritativeState = AuthoritativeAdaptiveChallengeState(
      challengeId = challengeId,
      ownerAuthUid = authenticatedUid,
      sourceFailurePatternId = targetPatternRecord?.patternId ?: "baseline_default",
      targetFailureMode = targetFailureMode,
      challengeTier = tier,
      policy = policy,
      createdAt = now,
      isUnsupportedPlantedTrap = template.isUnsupportedPlantedTrap,
      authoritativeCorrectActionId = template.correctActionId,
      authoritativeRequiredEvidenceIds = template.requiredEvidenceIds,
      plantedTrapRationale = template.plantedTrapRationale,
      learnerSafePayload = template.learnerPayload
    )

    _activeChallenges.value = _activeChallenges.value + (challengeId to authoritativeState)

    // Return strictly learner-safe payload (NO trap state, NO correct answers)
    return template.learnerPayload
  }

  /**
   * Deterministic challenge policies for all 7 failure modes.
   */
  fun buildPolicy(mode: FailureModeType, tier: AdaptiveChallengeTier): AdaptiveChallengePolicy {
    return when (mode) {
      FailureModeType.PREMATURE_ESCALATION -> AdaptiveChallengePolicy(
        targetFailureMode = mode,
        challengeTier = tier,
        policyObjective = "Test ability to resist hasty destructive containment and verify operational impact before escalating.",
        adversaryRole = "Panic-inducing AI co-pilot demanding instantaneous emergency server shutdown.",
        recoveryCriteria = "Triage baseline telemetry and verify scope before escalating containment.",
        evidenceRequirements = listOf("evi_auth_01", "evi_health_01")
      )

      FailureModeType.EVIDENCE_OVERWEIGHTING -> AdaptiveChallengePolicy(
        targetFailureMode = mode,
        challengeTier = tier,
        policyObjective = "Test multi-indicator corroboration when one high-salience threat intel indicator conflicts with host telemetry.",
        adversaryRole = "Overconfident AI co-pilot anchored to a single high-reputation threat intel match.",
        recoveryCriteria = "Evaluate host execution lineage rather than anchoring to a single noisy indicator.",
        evidenceRequirements = listOf("evi_intel_01", "evi_pcap_01", "evi_process_01")
      )

      FailureModeType.CONFIRMATION_BIAS -> AdaptiveChallengePolicy(
        targetFailureMode = mode,
        challengeTier = tier,
        policyObjective = "Test active disconfirmation search when an early benign explanation masks stealthy intrusion.",
        adversaryRole = "Affirmative AI co-pilot endorsing the convenient benign explanation.",
        recoveryCriteria = "Actively search for and evaluate disconfirming telemetry before closing alert.",
        evidenceRequirements = listOf("evi_ticket_01", "evi_ps_script_01")
      )

      FailureModeType.INSUFFICIENT_CORRELATION -> AdaptiveChallengePolicy(
        targetFailureMode = mode,
        challengeTier = tier,
        policyObjective = "Test multi-sensor temporal correlation to detect distributed impossible travel and session hijacking.",
        adversaryRole = "Siloed AI co-pilot analyzing each authentication event in isolation.",
        recoveryCriteria = "Synthesize events across multiple disparate log sources to construct unified incident timeline.",
        evidenceRequirements = listOf("evi_vpn_01", "evi_idp_01")
      )

      FailureModeType.WEAK_UNCERTAINTY_HANDLING -> AdaptiveChallengePolicy(
        targetFailureMode = mode,
        challengeTier = tier,
        policyObjective = "Test epistemic humility and uncertainty preservation when forensic evidence is truncated or incomplete.",
        adversaryRole = "Dogmatic AI co-pilot inventing unwarranted certainty from fragmented data.",
        recoveryCriteria = "Acknowledge missing data, preserve confidence bounds, and request additional telemetry.",
        evidenceRequirements = listOf("evi_sparse_pcap")
      )

      FailureModeType.CONTEXT_IGNORANCE -> AdaptiveChallengePolicy(
        targetFailureMode = mode,
        challengeTier = tier,
        policyObjective = "Test operational context awareness by evaluating asset role and automated pipeline authorizations.",
        adversaryRole = "Context-blind AI co-pilot treating authorized DevSecOps scans as hostile ransomware.",
        recoveryCriteria = "Examine host business purpose, user roles, and maintenance approvals to contextualize events.",
        evidenceRequirements = listOf("evi_tool_01", "evi_role_01")
      )

      FailureModeType.INCORRECT_PRIORITIZATION -> AdaptiveChallengePolicy(
        targetFailureMode = mode,
        challengeTier = tier,
        policyObjective = "Test business-impact triage when loud cosmetic noise competes with silent critical exfiltration.",
        adversaryRole = "Distracted AI co-pilot prioritizing high-visibility defacement over high-impact database theft.",
        recoveryCriteria = "Prioritize triage order strictly by data classification, asset criticality, and containment urgency.",
        evidenceRequirements = listOf("evi_deface_01", "evi_exfil_01")
      )

      else -> AdaptiveChallengePolicy(
        targetFailureMode = FailureModeType.INSUFFICIENT_CORRELATION,
        challengeTier = tier,
        policyObjective = "General correlation drill.",
        adversaryRole = "Standard AI Analyst.",
        recoveryCriteria = "Correlate disparate event logs.",
        evidenceRequirements = emptyList()
      )
    }
  }

  /**
   * Retrieves the authoritative challenge state for a specific challenge.
   * Enforces learner isolation: User A cannot read User B's challenge state.
   */
  fun getAuthoritativeState(challengeId: String, authenticatedUid: String): AuthoritativeAdaptiveChallengeState? {
    val state = _activeChallenges.value[challengeId] ?: return null
    if (state.ownerAuthUid != authenticatedUid) {
      throw SecurityException("Learner isolation violation: cannot access challenge belonging to another user.")
    }
    return state
  }

  private data class ChallengeTemplateInternal(
    val learnerPayload: LearnerSafeAdaptiveChallenge,
    val isUnsupportedPlantedTrap: Boolean,
    val correctActionId: String,
    val requiredEvidenceIds: List<String>,
    val plantedTrapRationale: String
  )

  private fun buildChallengeTemplate(
    policy: AdaptiveChallengePolicy,
    challengeId: String
  ): ChallengeTemplateInternal {
    val pattern = policy.targetFailureMode
    val tier = policy.challengeTier

    return when (pattern) {
      FailureModeType.PREMATURE_ESCALATION -> ChallengeTemplateInternal(
        isUnsupportedPlantedTrap = true,
        correctActionId = "act_investigate_baseline",
        requiredEvidenceIds = listOf("evi_auth_01", "evi_health_01"),
        plantedTrapRationale = "AI recommends emergency reboot of Domain Controller on single failed batch, causing company-wide outage.",
        learnerPayload = LearnerSafeAdaptiveChallenge(
          challengeId = challengeId,
          targetFailureMode = pattern,
          challengeTier = tier,
          weaknessNarrative = "AEGORA detected premature service escalation in previous incidents without confirming actual operational impact.",
          targetedSkill = "Incident Containment & Escalation Discipline",
          scenarioTitle = "ADVERSARIAL LAB: Tactical Escalation vs False Alarm",
          scenarioBriefing = "Multiple Kerberos TGT pre-auth failure alerts triggered on the primary production Domain Controller. Executive leadership is monitoring the war room.",
          aiAnalystClaim = AdaptiveAiClaim(
            analystName = "AEGORA Tactical AI Co-Pilot",
            claimText = "Critical Kerberos replay exploit detected on DC-01. Recommend immediate hard reboot and network quarantine of the domain controller immediately to stop lateral movement.",
            assertedEvidenceIds = listOf("evi_auth_01"),
            recommendedAction = "act_emergency_reboot_dc",
            confidencePercentage = 94
          ),
          evidencePool = listOf(
            AdaptiveEvidenceItem("evi_auth_01", "2026-09-10 11:15:02 UTC", "DC-01 Security Event Log", "EVENT_4768", "15 Kerberos pre-authentication failures originating from internal service host SRV-SYNC-02."),
            AdaptiveEvidenceItem("evi_health_01", "2026-09-10 11:15:10 UTC", "Directory Services Telemetry", "SERVICE_HEARTBEAT", "DC-01 operational state: Healthy. Zero anomalous process executions or LSASS access."),
            AdaptiveEvidenceItem("evi_sync_log", "2026-09-10 11:14:50 UTC", "SRV-SYNC-02 Task Scheduler", "APP_CONFIG_SYNC", "Scheduled batch sync job running with expired service account password.")
          ),
          actionOptions = listOf(
            AdaptiveActionOption("act_emergency_reboot_dc", "Quarantine & Reboot DC-01", "Sever network connectivity and reboot primary domain controller immediately."),
            AdaptiveActionOption("act_investigate_baseline", "Triage SRV-SYNC-02 Service Account", "Verify source host credentials and confirm DC-01 service health before escalating."),
            AdaptiveActionOption("act_ignore_completely", "Ignore Alert Queue", "Mark alerts as noise without inspecting sync service.")
          )
        )
      )

      FailureModeType.EVIDENCE_OVERWEIGHTING -> ChallengeTemplateInternal(
        isUnsupportedPlantedTrap = true,
        correctActionId = "act_analyze_host_execution",
        requiredEvidenceIds = listOf("evi_intel_01", "evi_process_01"),
        plantedTrapRationale = "AI anchors to a high-reputation public threat intel feed hit that was triggered by benign research telemetry.",
        learnerPayload = LearnerSafeAdaptiveChallenge(
          challengeId = challengeId,
          targetFailureMode = pattern,
          challengeTier = tier,
          weaknessNarrative = "AEGORA observed evidence overweighting where an isolated threat indicator blinded holistic investigation.",
          targetedSkill = "Multi-Source Corroboration & Salience Balance",
          scenarioTitle = "ADVERSARIAL LAB: Threat Intel Anchor vs System Truth",
          scenarioBriefing = "A critical threat intelligence alert flagged an IP communicating with workstation WS-092 as an APT29 C2 node.",
          aiAnalystClaim = AdaptiveAiClaim(
            analystName = "AEGORA Tactical AI Co-Pilot",
            claimText = "Threat feed gives 198.51.100.77 a 100/100 malicious score for APT29. We must instantly burn WS-092 and isolate the subnet.",
            assertedEvidenceIds = listOf("evi_intel_01"),
            recommendedAction = "act_nuke_workstation",
            confidencePercentage = 98
          ),
          evidencePool = listOf(
            AdaptiveEvidenceItem("evi_intel_01", "2026-09-10 11:20:00 UTC", "Global Threat Intelligence Feed", "THREAT_REPUTATION", "198.51.100.77 historically associated with APT29 research honeypots."),
            AdaptiveEvidenceItem("evi_pcap_01", "2026-09-10 11:22:15 UTC", "Suricata NIDS", "HTTP_GET", "Outbound HTTP GET to /robots.txt; zero payload, 404 response received."),
            AdaptiveEvidenceItem("evi_process_01", "2026-09-10 11:22:14 UTC", "Sysmon Event ID 1", "PROCESS_CREATE", "Parent curl.exe executed by authorized security auditor during scheduled external sweep.")
          ),
          actionOptions = listOf(
            AdaptiveActionOption("act_nuke_workstation", "Re-image Workstation & Isolate Subnet", "Treat single IP hit as definitive compromise and initiate destructive remediation."),
            AdaptiveActionOption("act_analyze_host_execution", "Verify Host Execution & Security Audit Scope", "Inspect Sysmon process lineage and check against security audit schedule."),
            AdaptiveActionOption("act_whitelist_ip", "Permanently Whitelist IP Globally", "Remove IP from all firewall and threat monitoring rules without review.")
          )
        )
      )

      FailureModeType.CONFIRMATION_BIAS -> ChallengeTemplateInternal(
        isUnsupportedPlantedTrap = true,
        correctActionId = "act_inspect_powershell_payload",
        requiredEvidenceIds = listOf("evi_ticket_01", "evi_ps_script_01"),
        plantedTrapRationale = "AI confirms initial benign hypothesis (scheduled maintenance) ignoring hidden malicious obfuscated script.",
        learnerPayload = LearnerSafeAdaptiveChallenge(
          challengeId = challengeId,
          targetFailureMode = pattern,
          challengeTier = tier,
          weaknessNarrative = "AEGORA detected confirmation bias where early benign explanations caused disconfirming anomalies to be dismissed.",
          targetedSkill = "Hypothesis Testing & Disconfirmation Search",
          scenarioTitle = "ADVERSARIAL LAB: Disconfirming the Benign Ticket",
          scenarioBriefing = "A late-night administrative PowerShell script executed on DB-CLUSTER-01 during a scheduled maintenance window.",
          aiAnalystClaim = AdaptiveAiClaim(
            analystName = "AEGORA Tactical AI Co-Pilot",
            claimText = "Ticket CHG-9921 confirms DB backup maintenance. The activity matches the scheduled window; alert can be closed safely.",
            assertedEvidenceIds = listOf("evi_ticket_01"),
            recommendedAction = "act_close_as_maintenance",
            confidencePercentage = 91
          ),
          evidencePool = listOf(
            AdaptiveEvidenceItem("evi_ticket_01", "2026-09-10 10:00:00 UTC", "IT Service Desk", "CHANGE_REQUEST", "Approved maintenance: DB-CLUSTER-01 backup script execution at 11:30 UTC."),
            AdaptiveEvidenceItem("evi_ps_script_01", "2026-09-10 11:32:04 UTC", "Sysmon Event ID 4104", "SCRIPT_BLOCK_LOGGING", "PowerShell script invokes DownloadString from untrusted cloud storage and reflection memory injection."),
            AdaptiveEvidenceItem("evi_egress_flow", "2026-09-10 11:33:10 UTC", "VPC Flow Logs", "EGRESS_SESSION", "Outbound TLS connection to dynamic VPS host in foreign AS.")
          ),
          actionOptions = listOf(
            AdaptiveActionOption("act_close_as_maintenance", "Close Ticket as Valid Maintenance", "Accept the maintenance ticket explanation without analyzing script block content."),
            AdaptiveActionOption("act_inspect_powershell_payload", "Intercept Script & Isolate DB Cluster", "Disconfirm maintenance assumption by analyzing obfuscated PowerShell payload."),
            AdaptiveActionOption("act_reboot_server", "Reboot Server Immediately", "Perform server power cycle discarding volatile script block memory.")
          )
        )
      )

      FailureModeType.INSUFFICIENT_CORRELATION -> ChallengeTemplateInternal(
        isUnsupportedPlantedTrap = true,
        correctActionId = "act_correlate_geo_identity",
        requiredEvidenceIds = listOf("evi_vpn_01", "evi_idp_01"),
        plantedTrapRationale = "AI treats each authentication as valid because credentials matched, failing to correlate cross-region delta.",
        learnerPayload = LearnerSafeAdaptiveChallenge(
          challengeId = challengeId,
          targetFailureMode = pattern,
          challengeTier = tier,
          weaknessNarrative = "AEGORA observed insufficient cross-telemetry correlation across disparate event logs.",
          targetedSkill = "Cross-Telemetry Time & Space Correlation",
          scenarioTitle = "ADVERSARIAL LAB: Invisible Impossible Travel",
          scenarioBriefing = "Two separate successful logins occurred for user a.morales within a 12-minute window across disparate providers.",
          aiAnalystClaim = AdaptiveAiClaim(
            analystName = "AEGORA Tactical AI Co-Pilot",
            claimText = "Both logins used valid multi-factor tokens and passed identity checks. No brute-force signatures detected; recommend closing alert.",
            assertedEvidenceIds = listOf("evi_vpn_01"),
            recommendedAction = "act_close_valid_logins",
            confidencePercentage = 88
          ),
          evidencePool = listOf(
            AdaptiveEvidenceItem("evi_vpn_01", "2026-09-10 11:00:15 UTC", "Corporate VPN Gateway", "VPN_SESSION_ESTABLISHED", "User a.morales connected from Frankfurt, Germany (IP: 85.214.132.10)."),
            AdaptiveEvidenceItem("evi_idp_01", "2026-09-10 11:11:45 UTC", "Azure AD / Entra ID Sign-in", "INTERACTIVE_SIGNIN", "User a.morales authenticated to Office 365 from Tokyo, Japan (IP: 133.242.18.5)."),
            AdaptiveEvidenceItem("evi_travel_calc", "2026-09-10 11:12:00 UTC", "Identity Analytics", "TRAVEL_DELTA", "Physical distance: 9,300 km in 11 minutes (requires 50,000 km/h velocity).")
          ),
          actionOptions = listOf(
            AdaptiveActionOption("act_close_valid_logins", "Approve Logins as Multi-Device Sync", "Assume user is connected via roaming proxy without correlating geographical delta."),
            AdaptiveActionOption("act_correlate_geo_identity", "Trigger Session Revocation & Investigate Session Hijack", "Correlate Frankfurt and Tokyo timestamps to prove impossible travel token compromise."),
            AdaptiveActionOption("act_block_entire_asn", "Block Tokyo ISP ASN Globally", "Block all inbound Japanese traffic across entire corporate network.")
          )
        )
      )

      FailureModeType.WEAK_UNCERTAINTY_HANDLING -> ChallengeTemplateInternal(
        isUnsupportedPlantedTrap = true,
        correctActionId = "act_preserve_uncertainty_request_memory",
        requiredEvidenceIds = listOf("evi_sparse_pcap"),
        plantedTrapRationale = "AI invents total certainty from 2 truncated packets, advising high-impact destructive action without proof.",
        learnerPayload = LearnerSafeAdaptiveChallenge(
          challengeId = challengeId,
          targetFailureMode = pattern,
          challengeTier = tier,
          weaknessNarrative = "AEGORA detected weak uncertainty handling where speculative conclusions were drawn from incomplete evidence.",
          targetedSkill = "Epistemic Humility & Evidence Sufficiency Assessment",
          scenarioTitle = "ADVERSARIAL LAB: Preserving Uncertainty Under Pressure",
          scenarioBriefing = "Network sensors captured 3 truncated TCP packets with malformed TLS handshakes. An automated triage ticket was generated.",
          aiAnalystClaim = AdaptiveAiClaim(
            analystName = "AEGORA Tactical AI Co-Pilot",
            claimText = "Malformed packet structure proves Cobalt Strike malleable C2 beaconing. Definitively categorize as SEV-1 breach and sever cloud interconnects.",
            assertedEvidenceIds = listOf("evi_sparse_pcap"),
            recommendedAction = "act_sever_cloud_interconnect",
            confidencePercentage = 97
          ),
          evidencePool = listOf(
            AdaptiveEvidenceItem("evi_sparse_pcap", "2026-09-10 11:05:00 UTC", "Core Router NetFlow", "PACKET_FRAGMENT", "3 SYN packets with non-standard TCP window size to ephemeral port 4443. Payload truncated at 64 bytes."),
            AdaptiveEvidenceItem("evi_edr_missing", "2026-09-10 11:05:30 UTC", "Endpoint Agent", "STATUS_QUERY", "Endpoint agent on host WS-044 was offline during packet window; memory dump unavailable.")
          ),
          actionOptions = listOf(
            AdaptiveActionOption("act_sever_cloud_interconnect", "Sever All Cloud Interconnects", "Treat 3 truncated packets as confirmed high-severity APT breach."),
            AdaptiveActionOption("act_preserve_uncertainty_request_memory", "Acknowledge Uncertainty & Collect Full Endpoint Triage", "State low diagnostic confidence, document missing artifacts, and pull endpoint memory when host reconnects."),
            AdaptiveActionOption("act_dismiss_permanently", "Dismiss Alert Permanently", "Mark incident resolved without recording inconclusive state.")
          )
        )
      )

      FailureModeType.CONTEXT_IGNORANCE -> ChallengeTemplateInternal(
        isUnsupportedPlantedTrap = true,
        correctActionId = "act_validate_business_role_context",
        requiredEvidenceIds = listOf("evi_tool_01", "evi_role_01"),
        plantedTrapRationale = "AI flags vulnerability scanner used by internal DevOps team as an external BlackCat ransomware precursor.",
        learnerPayload = LearnerSafeAdaptiveChallenge(
          challengeId = challengeId,
          targetFailureMode = pattern,
          challengeTier = tier,
          weaknessNarrative = "AEGORA observed context ignorance where legitimate operational role context was ignored.",
          targetedSkill = "Operational Context & Role-Aware Threat Analysis",
          scenarioTitle = "ADVERSARIAL LAB: Role Context vs Threat Blindness",
          scenarioBriefing = "Host DEV-SCAN-01 executed nmap and masscan against internal staging networks at 02:00 AM.",
          aiAnalystClaim = AdaptiveAiClaim(
            analystName = "AEGORA Tactical AI Co-Pilot",
            claimText = "Aggressive internal port scanning matches BlackCat reconnaissance. Immediate containment and host wipe required.",
            assertedEvidenceIds = listOf("evi_tool_01"),
            recommendedAction = "act_wipe_dev_host",
            confidencePercentage = 92
          ),
          evidencePool = listOf(
            AdaptiveEvidenceItem("evi_tool_01", "2026-09-10 02:00:10 UTC", "EDR Alerting", "NETWORK_SCAN_DETECTED", "Rapid SYN scan across staging subnet 10.20.0.0/24 from 10.20.1.15."),
            AdaptiveEvidenceItem("evi_role_01", "2026-09-10 02:00:00 UTC", "CMDB Asset Catalog", "ASSET_ROLE", "Host DEV-SCAN-01 designated as Automated DevSecOps Continuous Compliance Scanner."),
            AdaptiveEvidenceItem("evi_pipeline_log", "2026-09-10 01:59:50 UTC", "GitLab CI/CD", "PIPELINE_TRIGGER", "Scheduled weekly vulnerability regression pipeline job #88123 started by service_ci.")
          ),
          actionOptions = listOf(
            AdaptiveActionOption("act_wipe_dev_host", "Isolate & Wipe DEV-SCAN-01", "Treat automated DevSecOps scan as hostile intruder and destroy container image."),
            AdaptiveActionOption("act_validate_business_role_context", "Correlate with CI/CD Pipeline & Validate Asset Role", "Confirm asset purpose from CMDB and verify matching active pipeline job ID."),
            AdaptiveActionOption("act_disable_all_firewalls", "Disable Internal Network Firewalls", "Disable firewalls so scans run faster without triggering alerts.")
          )
        )
      )

      FailureModeType.INCORRECT_PRIORITIZATION -> ChallengeTemplateInternal(
        isUnsupportedPlantedTrap = true,
        correctActionId = "act_prioritize_silent_exfiltration",
        requiredEvidenceIds = listOf("evi_deface_01", "evi_exfil_01"),
        plantedTrapRationale = "AI urges focusing all resources on a public cosmetic defacement while silent high-volume patient database exfiltration continues in the background.",
        learnerPayload = LearnerSafeAdaptiveChallenge(
          challengeId = challengeId,
          targetFailureMode = pattern,
          challengeTier = tier,
          weaknessNarrative = "AEGORA detected incorrect prioritization where high-noise cosmetic events overshadowed critical threat severity.",
          targetedSkill = "Impact-Driven Triage & Criticality Prioritization",
          scenarioTitle = "ADVERSARIAL LAB: Public Noise vs Stealth Critical Exfiltration",
          scenarioBriefing = "Simultaneous alerts landed on the SOC board: a defaced public marketing blog and anomalous encrypted outbound transfer from customer database.",
          aiAnalystClaim = AdaptiveAiClaim(
            analystName = "AEGORA Tactical AI Co-Pilot",
            claimText = "Public blog defacement is active reputation damage and executive visibility SEV-1. Commit all tier-2 analysts to blog web server recovery.",
            assertedEvidenceIds = listOf("evi_deface_01"),
            recommendedAction = "act_focus_on_blog_defacement",
            confidencePercentage = 95
          ),
          evidencePool = listOf(
            AdaptiveEvidenceItem("evi_deface_01", "2026-09-10 11:30:00 UTC", "External Uptime Monitor", "WEB_DEFACEMENT", "Marketing static blog homepage modified with hacker crew text banner. Hosted on isolated AWS S3."),
            AdaptiveEvidenceItem("evi_exfil_01", "2026-09-10 11:30:15 UTC", "DLP / Firewall Sensor", "DATABASE_EXFILTRATION", "Continuous 400 MB/s encrypted outbound stream from production Customer-DB-01 to external cloud IP."),
            AdaptiveEvidenceItem("evi_crit_01", "2026-09-10 11:30:20 UTC", "Asset Criticality Matrix", "DATA_CLASSIFICATION", "Customer-DB-01 contains 5,000,000 PII/PHI records; business impact of breach is catastrophic.")
          ),
          actionOptions = listOf(
            AdaptiveActionOption("act_focus_on_blog_defacement", "Commit All Resources to Marketing Blog Restoration", "Prioritize visible executive reputation issue over background database anomaly."),
            AdaptiveActionOption("act_prioritize_silent_exfiltration", "Sever Database Egress Channel & Isolate DB-01 as P0", "Sever active Customer-DB-01 exfiltration stream as maximum criticality P0 before addressing cosmetic blog."),
            AdaptiveActionOption("act_pause_all_investigations", "Pause SOC Queue for Meeting", "Hold all actions until morning incident management briefing.")
          )
        )
      )

      else -> ChallengeTemplateInternal(
        isUnsupportedPlantedTrap = false,
        correctActionId = "act_default",
        requiredEvidenceIds = emptyList(),
        plantedTrapRationale = "Baseline fallback",
        learnerPayload = LearnerSafeAdaptiveChallenge(
          challengeId = challengeId,
          targetFailureMode = FailureModeType.INSUFFICIENT_CORRELATION,
          challengeTier = tier,
          weaknessNarrative = "Targeted baseline investigation.",
          targetedSkill = "Baseline Investigation",
          scenarioTitle = "ADVERSARIAL LAB: Baseline Drill",
          scenarioBriefing = "Standard investigation drill.",
          aiAnalystClaim = AdaptiveAiClaim(
            analystName = "AEGORA Co-Pilot",
            claimText = "Proceed with caution.",
            assertedEvidenceIds = emptyList(),
            recommendedAction = "act_default",
            confidencePercentage = 80
          ),
          evidencePool = emptyList(),
          actionOptions = emptyList()
        )
      )
    }
  }
}
