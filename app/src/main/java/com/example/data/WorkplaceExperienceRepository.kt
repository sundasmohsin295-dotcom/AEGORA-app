package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for the Professional Workplace & Incident Experience Engine.
 * Manages realistic virtual companies (AEGORA Financial, Health, Cloud),
 * live workplace shifts, interactive ticketing, manager Slack communications,
 * evidence workbenches, reasoning graphs, and incident consequence trees.
 */
object WorkplaceExperienceRepository {

  val virtualEnterprises = listOf(
    VirtualEnterprise(
      id = "org_fin_01",
      name = "AEGORA Global Financial",
      industry = "FinTech & Banking Infrastructure",
      employeeCount = 4850,
      criticalAssets = listOf("Core SWIFT Gateway", "Customer PostgreSQL Cluster", "AWS Prod VPC", "Domain Controller 01"),
      riskProfile = "High regulatory scrutiny (PCI-DSS, SEC Cyber Disclosure, SOX compliance)",
      currentThreatLevel = "ELEVATED (Active FIN7 / LockBit Campaign in Sector)",
      activeIncidentsCount = 3
    ),
    VirtualEnterprise(
      id = "org_health_01",
      name = "Aegora Health & Memorial Network",
      industry = "Healthcare & Hospital Medical Systems",
      employeeCount = 7200,
      criticalAssets = listOf("EMR Patient Database", "ICU Telemetry Network", "PACS Imaging Server"),
      riskProfile = "Zero-downtime life-safety requirement (HIPAA, FDA Medical Device Security)",
      currentThreatLevel = "HIGH (Ransomware targeting regional hospital OT networks)",
      activeIncidentsCount = 2
    ),
    VirtualEnterprise(
      id = "org_saas_01",
      name = "Aegora Cloud HyperScale SaaS",
      industry = "B2B AI & Cloud Platform",
      employeeCount = 1400,
      criticalAssets = listOf("Kubernetes Production Cluster", "AWS S3 Multi-Tenant Buckets", "HashiCorp Vault Secrets"),
      riskProfile = "Supply chain, zero-day API abuse, cloud IAM privilege boundary bypass",
      currentThreatLevel = "MODERATE",
      activeIncidentsCount = 1
    )
  )

  private val initialEvidenceList = listOf(
    WorkbenchEvidence(
      id = "ev_01",
      title = "SIEM Alert: High Outbound Data Transfer",
      toolSource = "Splunk Enterprise Security",
      rawData = "timestamp=\"2026-08-26T09:14:22Z\" src_ip=\"10.0.14.82\" dest_ip=\"194.26.29.114\" dest_port=443 bytes_out=892301924 (851 MB) rule=\"Excessive Data Transfer to Uncategorized IP\"",
      isKeyIndicator = true,
      confidenceScore = 92,
      mitreTactic = "T1048.003 - Exfiltration Over Unencrypted/Encrypted Non-C2 Protocol"
    ),
    WorkbenchEvidence(
      id = "ev_02",
      title = "EDR Telemetry: Process Tree Anomaly",
      toolSource = "CrowdStrike Falcon Sensor",
      rawData = "host=\"SRV-FIN-PAYMENTS01\" parent=\"services.exe\" (PID: 612) -> child=\"cmd.exe\" (PID: 4902) -> child=\"powershell.exe -w hidden -enc JABjAGwAaQBlAG4AdAA...\" (PID: 8814) -> spawned by \"SYSTEM\"",
      isKeyIndicator = true,
      confidenceScore = 98,
      mitreTactic = "T1059.001 - PowerShell Execution via Service Host"
    ),
    WorkbenchEvidence(
      id = "ev_03",
      title = "Active Directory Event 4771: Kerberos Pre-Auth Failed",
      toolSource = "Windows Event Log (DC01)",
      rawData = "EventID=4771 Status=0x18 (Bad Password) TargetUserName=\"svc_backup_admin\" ClientAddress=\"::ffff:10.0.14.82\" FailureCount=14 within 60 seconds.",
      isKeyIndicator = false,
      confidenceScore = 65,
      mitreTactic = "T1110.003 - Password Spraying"
    ),
    WorkbenchEvidence(
      id = "ev_04",
      title = "DNS Query Log: High Entropy Domain Resolution",
      toolSource = "Zeek Core DNS",
      rawData = "qname=\"d3f9a1b2c4e5.sync-telemetry-cdn.xyz\" qtype=A rcode=NOERROR answers=[\"194.26.29.114\"] query_count=214",
      isKeyIndicator = true,
      confidenceScore = 88,
      mitreTactic = "T1071.004 - Application Layer Protocol: DNS C2 Tunneling"
    ),
    WorkbenchEvidence(
      id = "ev_05",
      title = "Vulnerability Scanner: Scheduled Nessus Agent Run",
      toolSource = "Tenable Nessus Scanner",
      rawData = "src_ip=\"10.0.99.10\" (Nessus Scanner Appliance) scanning subnet 10.0.14.0/24. Status: Scheduled Weekly Compliance Sweep.",
      isKeyIndicator = false,
      confidenceScore = 20,
      mitreTactic = "BENIGN FALSE POSITIVE - Authorized Security Scan"
    )
  )

  private val initialMessages = listOf(
    WorkplaceMessage(
      id = "msg_01",
      senderName = "Sarah Chen",
      senderRole = "SOC Shift Lead / Manager",
      channelOrSubject = "#soc-incident-war-room",
      timestamp = "09:32 AM",
      content = "Morning team! Vance, saw that 850MB spike alert on SRV-FIN-PAYMENTS01. What do we know right now? Can you confirm if this is the scheduled accounting batch export or an active exfiltration before the 10:00 AM executive brief?",
      priority = "CRITICAL",
      requiresResponse = true,
      responseOptions = listOf(
        "Confirming active C2 & exfiltration. Process tree shows SYSTEM-spawned hidden PowerShell communicating to untrusted IP 194.26.29.114. Recommending immediate host network isolation.",
        "It matches the daily accounting batch job. Closing ticket as False Positive to avoid interrupting finance operations.",
        "Need 30 more minutes to inspect all DC logs before taking any action or giving an assessment."
      )
    ),
    WorkplaceMessage(
      id = "msg_02",
      senderName = "Marcus Brody",
      senderRole = "Infrastructure SysAdmin",
      channelOrSubject = "#infra-helpdesk-direct",
      timestamp = "09:40 AM",
      content = "Hey Vance, we are seeing some batch delays on the payment gateway. If you are doing forensics on payments01, please don't pull the physical plug without isolating via EDR first so we preserve RAM.",
      priority = "HIGH",
      requiresResponse = false
    )
  )

  private val initialReasoningNodes = listOf(
    ReasoningNode(
      id = "r_01",
      stage = "OBSERVATION",
      description = "850MB outbound SSL session to uncategorized foreign IP from SRV-FIN-PAYMENTS01 at 09:14 UTC.",
      isValidBranch = true,
      tradeOffOrRisk = "Baseline volume is typically under 15MB for this server."
    ),
    ReasoningNode(
      id = "r_02",
      stage = "HYPOTHESIS",
      description = "Hypothesis A: Attacker leveraged compromised service account to dump database and exfiltrate over HTTPS/DNS.",
      isValidBranch = true,
      tradeOffOrRisk = "Requires isolating server; accounting team batch processing will be paused for ~20 mins."
    ),
    ReasoningNode(
      id = "r_03",
      stage = "HYPOTHESIS",
      description = "Hypothesis B: Benign quarterly financial archive backup running unannounced.",
      isValidBranch = false,
      tradeOffOrRisk = "Dismissing early risks catastrophic customer data breach."
    ),
    ReasoningNode(
      id = "r_04",
      stage = "EVIDENCE_TEST",
      description = "Correlated CrowdStrike process tree (PID 8814 PowerShell spawned by services.exe) with Zeek high-entropy DNS resolution.",
      isValidBranch = true,
      tradeOffOrRisk = "Proves malicious payload execution beyond reasonable doubt."
    ),
    ReasoningNode(
      id = "r_05",
      stage = "ACTION",
      description = "Executed EDR network containment with live RAM preservation + rotated DC service account credentials.",
      isValidBranch = true,
      tradeOffOrRisk = "Immediate threat neutralisation while preserving full volatile memory artifacts."
    )
  )

  private val _currentShift = MutableStateFlow(
    WorkplaceShiftSession(
      shiftId = "SHIFT-2026-0826-01",
      careerRole = CareerRoleType.SOC_ANALYST,
      organization = virtualEnterprises[0],
      shiftTime = "09:45 AM (Hour 2 of 8)",
      shiftProgressPercent = 35,
      tickets = listOf(
        WorkplaceTicket(
          ticketId = "INC-7092",
          title = "Active Data Exfiltration Investigation - SRV-FIN-PAYMENTS01",
          assignedTo = "VANCE-SOC (You)",
          severity = "CRITICAL (P1)",
          status = "INVESTIGATING",
          organization = "AEGORA Global Financial",
          summary = "High-volume encrypted outbound transfer detected on core payment processing host following anomalous parent-child PowerShell execution.",
          affectedHost = "SRV-FIN-PAYMENTS01 (10.0.14.82)",
          evidenceIds = listOf("ev_01", "ev_02", "ev_04")
        ),
        WorkplaceTicket(
          ticketId = "INC-7093",
          title = "Nessus Scan Anomaly Triage",
          assignedTo = "VANCE-SOC (You)",
          severity = "LOW (P4)",
          status = "OPEN",
          organization = "AEGORA Global Financial",
          summary = "Routine vulnerability scanning on subnet 10.0.14.0/24 flagged by perimeter firewall.",
          affectedHost = "10.0.99.10",
          evidenceIds = listOf("ev_05")
        )
      ),
      activeAlerts = initialEvidenceList,
      messages = initialMessages,
      reasoningNodes = initialReasoningNodes,
      latestConsequence = null,
      professionalJudgmentScore = 86,
      independenceScore = 90
    )
  )
  val currentShift: StateFlow<WorkplaceShiftSession> = _currentShift.asStateFlow()

  fun selectCareerRole(role: CareerRoleType) {
    val current = _currentShift.value
    _currentShift.value = current.copy(
      careerRole = role,
      shiftProgressPercent = 15,
      latestConsequence = null
    )
  }

  fun switchEnterprise(enterprise: VirtualEnterprise) {
    val current = _currentShift.value
    _currentShift.value = current.copy(organization = enterprise)
  }

  fun examineEvidence(evidenceId: String) {
    val current = _currentShift.value
    val updated = current.activeAlerts.map {
      if (it.id == evidenceId) it.copy(isExamined = true) else it
    }
    _currentShift.value = current.copy(activeAlerts = updated)
  }

  fun pinEvidenceToHypothesis(evidenceId: String) {
    val current = _currentShift.value
    val updated = current.activeAlerts.map {
      if (it.id == evidenceId) it.copy(isPinnedToHypothesis = !it.isPinnedToHypothesis) else it
    }
    _currentShift.value = current.copy(activeAlerts = updated)
  }

  fun respondToManagerMessage(messageId: String, selectedOption: String) {
    val current = _currentShift.value
    val updatedMessages = current.messages.map { msg ->
      if (msg.id == messageId) msg.copy(resolvedResponse = selectedOption) else msg
    }

    val isOptimalResponse = selectedOption.startsWith("Confirming active C2")
    val consequence = if (isOptimalResponse) {
      IncidentConsequence(
        actionTaken = "Informed SOC Lead with precise technical evidence and recommended EDR isolation.",
        businessImpact = "Minimal disruption: Financial database isolated in 4 minutes, preventing estimated 4.2GB data breach ($1.8M regulatory exposure saved).",
        attackerProgression = "Attacker C2 channel disconnected. Payload persistence quarantined.",
        forensicConfidenceChange = +28,
        executiveSatisfaction = "SOC Lead and CISO commended clear communication and evidence-backed rationale."
      )
    } else {
      IncidentConsequence(
        actionTaken = "Delayed or misclassified incident as routine accounting export.",
        businessImpact = "High risk: Attacker persisted for 45 additional minutes before secondary alerts triggered.",
        attackerProgression = "Exfiltration continued across 3 additional database tables.",
        forensicConfidenceChange = -15,
        executiveSatisfaction = "Manager issued urgent escalation after secondary detection."
      )
    }

    val newJudgmentScore = if (isOptimalResponse) (current.professionalJudgmentScore + 5).coerceAtMost(100) else (current.professionalJudgmentScore - 8).coerceAtLeast(0)

    _currentShift.value = current.copy(
      messages = updatedMessages,
      latestConsequence = consequence,
      professionalJudgmentScore = newJudgmentScore,
      shiftProgressPercent = (current.shiftProgressPercent + 25).coerceAtMost(100)
    )
  }

  fun executeIncidentAction(actionType: String) {
    val current = _currentShift.value
    val consequence = when (actionType) {
      "ISOLATE_EDR" -> IncidentConsequence(
        actionTaken = "Isolated SRV-FIN-PAYMENTS01 from network via CrowdStrike Falcon (EDR Tunnel Kept Open).",
        businessImpact = "Zero network propagation. Payment transaction queue redirected to hot-standby secondary node.",
        attackerProgression = "Attacker blocked from pivoting to Domain Controller 01.",
        forensicConfidenceChange = +30,
        executiveSatisfaction = "Best Practice Execution: Host isolated with zero evidence destruction."
      )
      "KILL_PHYSICAL_POWER" -> IncidentConsequence(
        actionTaken = "Hard physical shutdown / power pull of server.",
        businessImpact = "Immediate downtime for 180 corporate branches + corrupted in-flight transaction DB table.",
        attackerProgression = "Attacker kicked off, but volatile RAM memory containing encryption keys was permanently destroyed.",
        forensicConfidenceChange = -35,
        executiveSatisfaction = "Incident Commander noted severe loss of forensic root-cause telemetry."
      )
      else -> IncidentConsequence(
        actionTaken = "Initiated forensic memory dump via KAPE & Volatility 3.",
        businessImpact = "No service downtime. Memory artifacts preserved for legal chain-of-custody.",
        attackerProgression = "Attacker monitored under controlled honeynet observation.",
        forensicConfidenceChange = +20,
        executiveSatisfaction = "Evidence integrity preserved for law enforcement handoff."
      )
    }

    _currentShift.value = current.copy(
      latestConsequence = consequence,
      shiftProgressPercent = (current.shiftProgressPercent + 20).coerceAtMost(100)
    )
  }
}
