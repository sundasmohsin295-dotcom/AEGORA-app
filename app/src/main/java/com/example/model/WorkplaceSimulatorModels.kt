package com.example.model

/**
 * Career role definition for the First Day on the Job & Workplace Experience Engine.
 */
enum class CareerRoleType(
  val roleTitle: String,
  val department: String,
  val description: String,
  val primaryTools: List<String>,
  val firstDayMission: String
) {
  SOC_ANALYST("SOC Analyst (L1/L2)", "Security Operations Center", "Triages raw SIEM/EDR alarms, validates true/false positives, and escalates active intrusions.", listOf("Splunk SIEM", "CrowdStrike EDR", "Wireshark", "TheHive"), "Onboard to the SOC Queue, review night shift handoff, and triage 5 incoming high-severity alerts."),
  INCIDENT_RESPONDER("Incident Responder", "Cyber Incident Response Team (CIRT)", "Conducts rapid containment, host isolation, memory triage, and root-cause blast radius analysis.", listOf("Velociraptor", "Volatility 3", "KAPE", "MISP"), "Investigate a potential financial database exfiltration and execute critical host isolation protocol."),
  THREAT_HUNTER("Threat Hunter", "Threat Operations", "Formulates hypotheses to detect stealthy persistent adversaries bypassing automated detection rules.", listOf("Sigma Rules", "Jupyter Notebooks", "Zeek", "Sysmon"), "Hunt for living-off-the-land PowerShell encoded downloads across 2,400 enterprise endpoints."),
  DIGITAL_FORENSICS("Digital Forensics Analyst", "Forensics Laboratory", "Performs deep bit-stream disk imaging, MFT parsing, browser history extraction, and chain-of-custody preservation.", listOf("Autopsy", "FTK Imager", "Plaso", "YARA"), "Extract encrypted USB persistence artifacts from a compromised executive workstation."),
  THREAT_INTEL("Threat Intelligence Analyst", "Strategic Intelligence", "Analyzes adversary TTPs, maps APT campaigns to MITRE ATT&CK, and produces strategic executive advisories.", listOf("OpenCTI", "YARA", "Shodan", "VirusTotal Intelligence"), "Assess active LockBit 4.0 ransomware affiliate IOCs and publish an actionable SOC detection brief."),
  PEN_TESTER("Penetration Tester", "Offensive Security (Red Team)", "Executes authorized security assessments, weaponization, and privilege escalation simulations.", listOf("Burp Suite Pro", "BloodHound", "Metasploit", "Nmap"), "Conduct an authorized internal network assessment against Aegora Financial's staging cluster."),
  SECURITY_ENGINEER("Security Engineer", "Infrastructure Security", "Architects defensive telemetry, zero-trust network segmentation, firewalls, and SIEM correlation pipelines.", listOf("Terraform", "Suricata", "Wazuh", "Vault"), "Harden AWS IAM assume-role trust relationships and configure egress perimeter filtering."),
  CLOUD_SECURITY("Cloud Security Engineer", "Cloud Defense", "Protects multi-cloud AWS/Azure/GCP environments against storage bucket exposure and IAM privilege creep.", listOf("AWS CloudTrail", "GuardDuty", "Kubescape", "Trivy"), "Remediate a publicly accessible S3 customer database bucket and audit IAM access logs."),
  APPSEC_ENGINEER("Application Security Engineer", "Product Security", "Performs threat modeling, SAST/DAST pipeline reviews, API authentication audits, and secure coding coaching.", listOf("Semgrep", "OWASP ZAP", "SonarQube", "Postman"), "Review a critical JWT authentication pull request submitted by the core engineering team."),
  DEVSECOPS("DevSecOps Engineer", "Platform Security", "Automates security gates into CI/CD pipelines, container scanning, and infrastructure-as-code linting.", listOf("GitHub Actions", "Checkov", "Cosign", "Snyk"), "Triage a high-severity container escape CVE detected in the production deployment pipeline."),
  GRC_ANALYST("GRC Analyst / Auditor", "Governance, Risk & Compliance", "Evaluates SOC2/ISO27001/NIST CSF control implementations, vendor third-party risk, and audit readiness.", listOf("OneTrust", "Jira Compliance", "Risk Register", "NIST CSF 2.0"), "Conduct a vendor third-party risk assessment for a new AI analytics platform requesting DB access.")
}

/**
 * Fictional Organization Profile with Living Cyber Environment characteristics.
 */
data class VirtualEnterprise(
  val id: String,
  val name: String,
  val industry: String,
  val employeeCount: Int,
  val criticalAssets: List<String>,
  val riskProfile: String,
  val currentThreatLevel: String,
  val activeIncidentsCount: Int
)

/**
 * Workplace Communication Message (Slack / Email / Manager Ping).
 */
data class WorkplaceMessage(
  val id: String,
  val senderName: String,
  val senderRole: String,
  val channelOrSubject: String,
  val timestamp: String,
  val content: String,
  val priority: String, // "CRITICAL", "HIGH", "NORMAL"
  val requiresResponse: Boolean,
  val responseOptions: List<String> = emptyList(),
  val resolvedResponse: String? = null
)

/**
 * Investigation Evidence Item for the Evidence-First Workbench.
 */
data class WorkbenchEvidence(
  val id: String,
  val title: String,
  val toolSource: String, // "Splunk SIEM", "CrowdStrike EDR", "Wireshark PCAP", "Zeek DNS", "AuditD"
  val rawData: String,
  val isKeyIndicator: Boolean,
  val confidenceScore: Int, // 0 - 100
  val mitreTactic: String,
  var isExamined: Boolean = false,
  var isPinnedToHypothesis: Boolean = false
)

/**
 * Reasoning Graph Node for Hypothesis-Driven Investigation.
 */
data class ReasoningNode(
  val id: String,
  val stage: String, // "OBSERVATION", "HYPOTHESIS", "EVIDENCE_TEST", "CONCLUSION", "ACTION"
  val description: String,
  val isValidBranch: Boolean,
  val tradeOffOrRisk: String
)

/**
 * Consequence Result of an Incident Response Action.
 */
data class IncidentConsequence(
  val actionTaken: String,
  val businessImpact: String, // e.g. "Downtime: $42,000/hr batch delay prevented, 2 servers isolated."
  val attackerProgression: String, // e.g. "Attacker C2 beacon severed immediately."
  val forensicConfidenceChange: Int, // e.g. +25
  val executiveSatisfaction: String // e.g. "CISO praised rapid containment without enterprise-wide panic."
)

/**
 * Security Incident Ticket in the workplace.
 */
data class WorkplaceTicket(
  val ticketId: String,
  val title: String,
  val assignedTo: String,
  val severity: String,
  val status: String, // "OPEN", "INVESTIGATING", "CONTAINED", "CLOSED"
  val organization: String,
  val summary: String,
  val affectedHost: String,
  val evidenceIds: List<String>,
  val rootCause: String? = null,
  val businessImpactSummary: String? = null
)

/**
 * Complete Shift State for the Professional Work Simulator.
 */
data class WorkplaceShiftSession(
  val shiftId: String,
  val careerRole: CareerRoleType,
  val organization: VirtualEnterprise,
  val shiftTime: String, // e.g. "09:45 AM (Hour 2 of 8)"
  val shiftProgressPercent: Int,
  val tickets: List<WorkplaceTicket>,
  val activeAlerts: List<WorkbenchEvidence>,
  val messages: List<WorkplaceMessage>,
  val reasoningNodes: List<ReasoningNode>,
  val latestConsequence: IncidentConsequence?,
  val professionalJudgmentScore: Int, // 0 - 100
  val independenceScore: Int // 0 - 100
)
