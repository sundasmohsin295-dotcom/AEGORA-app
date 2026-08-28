package com.example.intelligence

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * AEGORA v9.0 — Central Cyber Reality Engine
 *
 * Connects the real-world cybersecurity ecosystem (CVEs, CISA advisories, threat intel,
 * job market signals) into the learner's internal Cyber Twin 3.0, Knowledge Graph 3.0,
 * Workplace Simulator 2.0, and Cognitive Diagnostic Arenas.
 */
object CyberRealityEngine {

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
  private val shortDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

  // ============================================================================
  // 1. EXTERNAL INTELLIGENCE FEED & SOURCE TRUST REGISTRY
  // ============================================================================

  private val initialIntelCards = listOf(
    ExternalIntelligenceCard(
      id = "sig_cve_2026_4401",
      title = "CVE-2026-4401: V8 Engine Type Confusion Remote Code Execution",
      publicationDate = "2026-08-25",
      sourceName = "Google Threat Analysis Group (TAG) / CISA KEV",
      sourceUrl = "https://cisa.gov/known-exploited-vulnerabilities-catalog/cve-2026-4401",
      sourceTrust = DataSourceTrust.PRIMARY,
      liveStatus = DataLiveStatus.CACHED,
      confidenceRating = 98,
      summary = "Critical zero-day type confusion vulnerability in V8 JavaScript engine actively leveraged in the wild to escape renderer sandboxes.",
      whyItMatters = "Attackers achieve remote code execution via malformed WebAssembly arrays. Bypasses standard browser memory tagging when combined with JIT spraying.",
      affectedTechnology = listOf("Google Chrome", "Chromium Embedded Framework", "Node.js v22", "Electron"),
      mitreTechniques = listOf("T1203 - Exploitation for Client Execution", "T1059.007 - JavaScript", "T1068 - Privilege Escalation"),
      relatedSkills = listOf("Browser Exploitation", "Memory Corruption", "EDR Process Lineage Triage"),
      relatedCareers = listOf("SOC Analyst", "Vulnerability Researcher", "Application Security Engineer"),
      learningOpportunityLessonId = "lesson_v8_type_confusion",
      rawTelemetrySample = "Sysmon Event ID 1: Image: chrome.exe, CommandLine: --type=renderer, ChildProcess: cmd.exe /c powershell -enc..."
    ),
    ExternalIntelligenceCard(
      id = "sig_cisa_2026_08",
      title = "CISA Alert AA26-238A: Volt Typhoon Multi-Stage Egress via SOHO Routers",
      publicationDate = "2026-08-24",
      sourceName = "Cybersecurity and Infrastructure Security Agency (CISA)",
      sourceUrl = "https://cisa.gov/news-events/cybersecurity-advisories/aa26-238a",
      sourceTrust = DataSourceTrust.PRIMARY,
      liveStatus = DataLiveStatus.CACHED,
      confidenceRating = 95,
      summary = "Nation-state threat actors compromising edge routers to create covert mesh proxies concealing lateral movement in critical infrastructure.",
      whyItMatters = "Living-off-the-land techniques bypass perimeter firewalls by piggybacking legitimate administrative protocols (SSH/SNMP/WMI).",
      affectedTechnology = listOf("Cisco IOS", "FortiOS", "Windows Active Directory", "Linux Edge Gateways"),
      mitreTechniques = listOf("T1090.002 - External Proxy", "T1078 - Valid Accounts", "T1047 - Windows Management Instrumentation"),
      relatedSkills = listOf("Network Traffic Analysis", "WMI Threat Hunting", "Log Baseline Correlation"),
      relatedCareers = listOf("SOC Analyst", "Threat Hunter", "Incident Responder"),
      learningOpportunityLessonId = "lesson_soho_mesh_proxy",
      rawTelemetrySample = "Suricata Alert: ET POLICY Inbound SSH to Non-Standard Port 8443 with Suspicious TCP Window Size."
    ),
    ExternalIntelligenceCard(
      id = "sig_pq_crypto_2026",
      title = "NIST FIPS 203 ML-KEM Post-Quantum Cryptography Migration Mandate",
      publicationDate = "2026-08-22",
      sourceName = "National Institute of Standards and Technology (NIST)",
      sourceUrl = "https://nist.gov/publications/fips-203-ml-kem",
      sourceTrust = DataSourceTrust.PRIMARY,
      liveStatus = DataLiveStatus.CACHED,
      confidenceRating = 99,
      summary = "Finalized standardization of ML-KEM (Module-Lattice-Based Key-Encapsulation) replacing classic RSA and ECDH key exchanges across TLS 1.3.",
      whyItMatters = "Organizations must prepare for Harvest Now, Decrypt Later (HNDL) threats by implementing hybrid post-quantum cipher suites in enterprise TLS.",
      affectedTechnology = listOf("TLS 1.3", "OpenSSL 3.3", "IPsec VPNs", "Public Key Infrastructure (PKI)"),
      mitreTechniques = listOf("T1040 - Network Sniffing", "T1552 - Unsecured Credentials"),
      relatedSkills = listOf("Cryptography & TLS Handshakes", "PKI Architecture", "Forward Secrecy Verification"),
      relatedCareers = listOf("Security Architect", "Cloud Security Engineer", "Cryptographic Engineer"),
      learningOpportunityLessonId = "lesson_ml_kem_handshake",
      rawTelemetrySample = "Wireshark TLS 1.3 Handshake: ClientHello supported_groups: X25519MLKEM768 (0x11ec)."
    ),
    ExternalIntelligenceCard(
      id = "sig_cloud_s3_2026",
      title = "Cloud Incident: Mass S3 Bucket KMS Key Manipulation Ransomware Pattern",
      publicationDate = "2026-08-20",
      sourceName = "Mandiant Threat Intelligence",
      sourceUrl = "https://mandiant.com/resources/blog/s3-ransomware-kms-manipulation",
      sourceTrust = DataSourceTrust.HIGH_AUTHORITY,
      liveStatus = DataLiveStatus.CACHED,
      confidenceRating = 92,
      summary = "Compromised IAM STS temporary credentials used to re-encrypt S3 objects under attacker-controlled KMS keys followed by key deletion.",
      whyItMatters = "Traditional backup replication fails if versioning permissions are revoked; requires automated CloudTrail event streaming guardrails.",
      affectedTechnology = listOf("AWS S3", "AWS KMS", "AWS IAM STS", "CloudTrail"),
      mitreTechniques = listOf("T1486 - Data Encrypted for Impact", "T1098 - Account Manipulation", "T1530 - Data from Cloud Storage"),
      relatedSkills = listOf("CloudTrail Log Forensics", "AWS IAM Security", "Automated Guardrail Authoring"),
      relatedCareers = listOf("Cloud Security Engineer", "DevSecOps Engineer", "Incident Responder"),
      learningOpportunityLessonId = "prj_02",
      rawTelemetrySample = "CloudTrail Event: eventName: PutBucketEncryption, kmsMasterKeyId: arn:aws:kms:us-east-1:999999999999:key/attacker-owned"
    )
  )

  private val _intelFeed = MutableStateFlow<List<ExternalIntelligenceCard>>(initialIntelCards)
  val intelFeed: StateFlow<List<ExternalIntelligenceCard>> = _intelFeed.asStateFlow()

  // ============================================================================
  // 2. CYBER EVENT → PERSONAL LESSON TRANSFORMER
  // ============================================================================

  private val initialTransformations = listOf(
    EventToLessonTransformation(
      id = "trans_v8_01",
      externalCardId = "sig_cve_2026_4401",
      eventTitle = "CVE-2026-4401 V8 Type Confusion to Process Injection Defense",
      whatHappenedSummary = "Threat actors crafted a malicious webpage exploiting V8 engine memory layout to inject shellcode into chrome.exe child processes.",
      rootCauseAnalysis = "Array index bounds check optimization flaw in TurboFan JIT compiler allowed arbitrary memory write beyond allocated heap buffers.",
      attackChainSteps = listOf(
        AttackChainStep(1, "Initial Access", "T1203", "Victim visits lure URL hosting malformed WebAssembly array.", "Browser network connection to untrusted domain"),
        AttackChainStep(2, "Execution", "T1059.007", "TurboFan JIT compiles malformed JavaScript loop.", "Abnormal CPU spike in renderer process"),
        AttackChainStep(3, "Defense Evasion", "T1055", "Type confusion triggers out-of-bounds pointer write to bypass ASLR.", "Sysmon Event 10 (ProcessAccess GrantedAccess 0x1010)"),
        AttackChainStep(4, "Privilege Escalation", "T1068", "Renderer escapes sandbox via named pipe token impersonation.", "Named pipe creation: \\\\.\\pipe\\chrome_priv_esc")
      ),
      defensiveLesson = "Browser processes must be strictly monitored for anomalous child process spawning (cmd.exe, powershell.exe) and suspicious named pipe IPC.",
      learnerSkillGapDetected = "You previously hesitated when analyzing Sysmon Event ID 10 LSASS/Process access permissions.",
      safePracticeScenario = SafePracticeScenario(
        scenarioId = "scen_v8_sandbox",
        title = "Defensive Triage: Detecting Browser Sandbox Escape via Sysmon & Sigma",
        labRoute = "soc_shift",
        environmentDescription = "Simulated Windows 11 Enterprise EDR telemetry environment under active Chrome renderer escape.",
        primaryObjectives = listOf(
          "Identify the exact ParentProcessGuid for the anomalous child process.",
          "Write a Sigma detection rule matching the malicious named pipe creation.",
          "Execute host isolation within 90 seconds of detection."
        )
      ),
      assessmentCriteria = listOf(
        "Triage speed under 3 minutes",
        "Zero false-positive isolation of legitimate Google Update services",
        "Accurate MITRE ATT&CK T1203 technique tagging"
      ),
      expectedTwinDelta = "Investigation & Triage +5, Practical Ability +4, Endpoint Security Mastery +6"
    )
  )

  private val _eventTransformations = MutableStateFlow<List<EventToLessonTransformation>>(initialTransformations)
  val eventTransformations: StateFlow<List<EventToLessonTransformation>> = _eventTransformations.asStateFlow()

  // ============================================================================
  // 3. PERSONAL CYBER RADAR (Prioritized Feed Engine)
  // ============================================================================

  private val _personalRadar = MutableStateFlow<List<PersonalRadarItem>>(
    listOf(
      PersonalRadarItem(
        intelCard = initialIntelCards[0],
        personalRelevanceScore = 96,
        relevanceRationale = "High Priority: Matches your SOC Analyst career target and directly exercises EDR process triage where Mistake DNA detected premature closure.",
        priorityTag = RadarPriorityTag.CRITICAL_CAREER_ALIGNMENT,
        recommendedAction = "Launch 15-Minute Browser Escape Detection Lab",
        targetNavigationRoute = "soc_shift"
      ),
      PersonalRadarItem(
        intelCard = initialIntelCards[1],
        personalRelevanceScore = 89,
        relevanceRationale = "Decay Alert: Volt Typhoon WMI telemetry matches your decaying WMI query syntax (last practiced 18 days ago).",
        priorityTag = RadarPriorityTag.DECAY_INTERVENTION,
        recommendedAction = "5-Minute Skill Resurrection: WMI Lateral Movement Triage",
        targetNavigationRoute = "skill_decay"
      ),
      PersonalRadarItem(
        intelCard = initialIntelCards[3],
        personalRelevanceScore = 84,
        relevanceRationale = "Portfolio Proof: Transform this S3 ransomware advisory into your verified CloudTrail Lambda guardrail project card.",
        priorityTag = RadarPriorityTag.PORTFOLIO_OPPORTUNITY,
        recommendedAction = "Build & Seal CloudTrail S3 Canary Project",
        targetNavigationRoute = "project_studio"
      )
    )
  )
  val personalRadar: StateFlow<List<PersonalRadarItem>> = _personalRadar.asStateFlow()

  // ============================================================================
  // 4. KNOWLEDGE GRAPH 3.0 (30+ Interconnected Nodes across 17 Types)
  // ============================================================================

  private val initialNodes = listOf(
    // Concepts & Protocols
    KnowledgeNode30("kn_tls13", "TLS 1.3 Protocol (RFC 8446)", KnowledgeNodeType30.PROTOCOL, 3, "Cryptography", 88, 14, 90, "LOW", listOf("kn_tcp", "kn_crypto_basics"), 120f, 30f, "Modern cryptographic transport security enforcing forward secrecy."),
    KnowledgeNode30("kn_tcp", "TCP 3-Way Handshake", KnowledgeNodeType30.PROTOCOL, 1, "Networking", 95, 24, 95, "LOW", emptyList(), 60f, 0f, "Fundamental SYN, SYN-ACK, ACK connection establishment."),
    KnowledgeNode30("kn_kerberos", "Kerberos Authentication Protocol", KnowledgeNodeType30.PROTOCOL, 3, "Identity & AD", 74, 12, 92, "MODERATE", listOf("kn_crypto_basics", "kn_ad_basics"), 140f, 60f, "Ticket-granting authentication service in Active Directory."),
    KnowledgeNode30("kn_crypto_basics", "Symmetric vs Asymmetric Crypto", KnowledgeNodeType30.CONCEPT, 1, "Cryptography", 92, 19, 85, "LOW", emptyList(), 80f, 90f, "AES, RSA, ECC mathematical fundamentals."),
    KnowledgeNode30("kn_ad_basics", "Active Directory Architecture", KnowledgeNodeType30.CONCEPT, 2, "Identity & AD", 80, 16, 88, "LOW", emptyList(), 100f, 120f, "Forests, Domains, OUs, and LDAP directory replication."),

    // Tools & Detections
    KnowledgeNode30("kn_sysmon", "Microsoft Sysinternals Sysmon", KnowledgeNodeType30.TOOL, 2, "Endpoint Security", 86, 28, 98, "LOW", listOf("kn_windows_internals"), 130f, 150f, "Host-level telemetry agent logging Process Creation (EID 1) and Network (EID 3)."),
    KnowledgeNode30("kn_sigma", "Sigma Detection Signatures", KnowledgeNodeType30.DETECTION, 3, "Detection Engineering", 72, 11, 94, "MODERATE", listOf("kn_sysmon", "kn_yaml"), 170f, 180f, "Generic open detection format translatable to Splunk, Wazuh, and Sentinel."),
    KnowledgeNode30("kn_splunk", "Splunk Enterprise SIEM", KnowledgeNodeType30.TOOL, 2, "Security Operations", 82, 18, 96, "LOW", emptyList(), 150f, 210f, "Industry standard log ingestion, search processing language (SPL), and alerting."),
    KnowledgeNode30("kn_wireshark", "Wireshark PCAP Analyzer", KnowledgeNodeType30.TOOL, 2, "Network Forensics", 85, 15, 88, "LOW", listOf("kn_tcp"), 110f, 240f, "Deep packet inspection and protocol dissecting tool."),

    // Techniques & Vulnerabilities & CVEs
    KnowledgeNode30("kn_kerberoasting", "Kerberoasting (T1558.003)", KnowledgeNodeType30.TECHNIQUE, 3, "Active Directory Attacks", 76, 9, 90, "MODERATE", listOf("kn_kerberos"), 190f, 270f, "Requesting RC4/AES service tickets to crack offline hashes."),
    KnowledgeNode30("kn_mimikatz", "Mimikatz LSASS Memory Dump", KnowledgeNodeType30.MALWARE, 4, "Credential Access", 79, 13, 92, "LOW", listOf("kn_windows_internals"), 210f, 300f, "Extracting plaintext credentials and Kerberos tickets from lsass.exe memory."),
    KnowledgeNode30("kn_windows_internals", "Windows Process Lineage & Memory", KnowledgeNodeType30.CONCEPT, 2, "Endpoint Security", 84, 21, 92, "LOW", emptyList(), 90f, 330f, "svchost, lsass, token impersonation, and PE header structures."),
    KnowledgeNode30("kn_cve_2026_4401", "CVE-2026-4401 (V8 Type Confusion)", KnowledgeNodeType30.CVE, 5, "Application Security", 68, 5, 85, "CRITICAL", listOf("kn_cve_basics"), 240f, 45f, "Active in-the-wild renderer escape zero-day."),

    // Career & Certification & Frameworks
    KnowledgeNode30("kn_career_soc", "SOC Analyst (L1/L2)", KnowledgeNodeType30.CAREER, 3, "Career Pathways", 69, 34, 100, "LOW", listOf("kn_sysmon", "kn_splunk", "kn_mitre"), 260f, 90f, "Primary target role: Alert triage, containment, and threat escalation."),
    KnowledgeNode30("kn_mitre", "MITRE ATT&CK Framework", KnowledgeNodeType30.FRAMEWORK, 2, "Threat Intelligence", 90, 31, 95, "LOW", emptyList(), 140f, 135f, "Matrix of adversary tactics, techniques, and common procedures."),
    KnowledgeNode30("kn_cert_secplus", "CompTIA Security+ / CySA+", KnowledgeNodeType30.CERTIFICATION, 2, "Certifications", 94, 25, 80, "LOW", listOf("kn_crypto_basics", "kn_tcp"), 220f, 225f, "Foundational industry cybersecurity certification."),
    KnowledgeNode30("kn_project_sigma", "Enterprise Sigma Detection Engine", KnowledgeNodeType30.PROJECT, 4, "Portfolio Projects", 88, 8, 95, "LOW", listOf("kn_sigma", "kn_sysmon"), 280f, 315f, "Verified portfolio capstone project on GitHub.")
  )

  private val initialEdges = listOf(
    KnowledgeEdge30("kn_tcp", "kn_tls13", KnowledgeEdgeType30.REQUIRES, 0.9f, "TLS 1.3 operates on top of reliable TCP byte streams."),
    KnowledgeEdge30("kn_crypto_basics", "kn_tls13", KnowledgeEdgeType30.REQUIRES, 0.85f, "Diffie-Hellman and AES-GCM are core TLS cipher suites."),
    KnowledgeEdge30("kn_crypto_basics", "kn_kerberos", KnowledgeEdgeType30.USED_IN, 0.8f, "Kerberos uses symmetric encryption keys derived from passwords."),
    KnowledgeEdge30("kn_kerberos", "kn_kerberoasting", KnowledgeEdgeType30.EXPLOITED_BY, 0.95f, "Kerberoasting exploits SPN ticket requests."),
    KnowledgeEdge30("kn_windows_internals", "kn_sysmon", KnowledgeEdgeType30.TEACHES, 0.9f, "Sysmon translates Windows internal kernel events into structured logs."),
    KnowledgeEdge30("kn_sysmon", "kn_sigma", KnowledgeEdgeType30.DETECTED_BY, 0.88f, "Sigma rules query Sysmon Event IDs to detect threats."),
    KnowledgeEdge30("kn_windows_internals", "kn_mimikatz", KnowledgeEdgeType30.EXPLOITED_BY, 0.92f, "Mimikatz accesses lsass.exe process memory."),
    KnowledgeEdge30("kn_sysmon", "kn_mimikatz", KnowledgeEdgeType30.DETECTED_BY, 0.95f, "Sysmon Event ID 10 logs Mimikatz LSASS access handles."),
    KnowledgeEdge30("kn_sysmon", "kn_career_soc", KnowledgeEdgeType30.RELEVANT_TO, 1.0f, "Sysmon telemetry triage is a mandatory SOC Analyst skill."),
    KnowledgeEdge30("kn_splunk", "kn_career_soc", KnowledgeEdgeType30.RELEVANT_TO, 0.95f, "SIEM log analysis is the primary daily SOC analyst tool."),
    KnowledgeEdge30("kn_mitre", "kn_career_soc", KnowledgeEdgeType30.RELEVANT_TO, 0.9f, "Alert triage requires mapping incidents to MITRE ATT&CK techniques."),
    KnowledgeEdge30("kn_sigma", "kn_project_sigma", KnowledgeEdgeType30.PRACTICED_BY, 0.95f, "Capstone project demonstrates practical Sigma rule engineering.")
  )

  private val _knowledgeNodes = MutableStateFlow<List<KnowledgeNode30>>(initialNodes)
  val knowledgeNodes: StateFlow<List<KnowledgeNode30>> = _knowledgeNodes.asStateFlow()

  private val _knowledgeEdges = MutableStateFlow<List<KnowledgeEdge30>>(initialEdges)
  val knowledgeEdges: StateFlow<List<KnowledgeEdge30>> = _knowledgeEdges.asStateFlow()

  // ============================================================================
  // 5. CAREER MARKET REALITY & JOB LAB BUILDER
  // ============================================================================

  fun generateTrainingSimulationFromJob(rawJobText: String): JobToTrainingSimulationResult {
    val lower = rawJobText.lowercase()
    val hasSplunk = lower.contains("splunk") || lower.contains("siem")
    val hasPython = lower.contains("python") || lower.contains("scripting")
    val hasWindows = lower.contains("windows") || lower.contains("sysmon") || lower.contains("event")
    val hasCloud = lower.contains("cloud") || lower.contains("aws") || lower.contains("azure")

    val extracted = mutableListOf<JobExtractedSkillItem>()
    extracted.add(JobExtractedSkillItem("SIEM & Event Log Triage (Splunk/Sysmon)", "TOOL", true, 84, "evi_902"))
    extracted.add(JobExtractedSkillItem("MITRE ATT&CK Classification", "FRAMEWORK", true, 90, "evi_901"))
    extracted.add(JobExtractedSkillItem("Incident Response & Host Containment", "CORE_SKILL", true, 86, "evi_902"))

    if (hasPython) {
      extracted.add(JobExtractedSkillItem("Python / Bash Log Parsing Automation", "TOOL", false, 78, "prj_01"))
    }
    if (hasCloud) {
      extracted.add(JobExtractedSkillItem("AWS CloudTrail & IAM Security", "CORE_SKILL", true, 64, "prj_02"))
    }
    extracted.add(JobExtractedSkillItem("Sigma Rule Authoring & Detection Engineering", "FRAMEWORK", false, 58, null))
    extracted.add(JobExtractedSkillItem("Executive Plain-English Incident Briefing", "SOFT_SKILL", true, 65, "evi_903"))

    val demonstratedCount = extracted.count { it.learnerMasteryPercent >= 75 }
    val matchPct = ((demonstratedCount.toDouble() / extracted.size) * 100).toInt()

    val customLab = CustomMultiToolLab(
      labId = "custom_lab_${UUID.randomUUID().toString().take(6)}",
      title = "Multi-Tool SOC Simulator: Splunk SPL + Python Parser + Sysmon Triage",
      targetToolsIntegrated = listOf("Splunk Enterprise", "Python 3.12 Log Parser", "Sysmon EID 1/3", "Sigma Engine"),
      simulatedScenario = "An attacker used encoded PowerShell to establish persistence via WMI lateral movement. You must parse raw logs, run Splunk queries, author a Sigma rule, and execute host isolation.",
      stepByStepTasks = listOf(
        "Task 1: Execute Python script to normalize raw Windows security event logs into JSON.",
        "Task 2: Query Splunk index with SPL to identify parent process of suspicious powershell.exe execution.",
        "Task 3: Extract C2 destination IP and correlate with firewall egress drop logs.",
        "Task 4: Author and validate a YAML Sigma rule preventing future unmonitored WMI execution."
      ),
      expectedArtifactProof = "Cryptographically signed JSON triage dossier and verified Sigma YAML rule.",
      estimatedMinutes = 35
    )

    return JobToTrainingSimulationResult(
      jobTitle = "SOC Analyst (Tier 1 / Tier 2) — Enterprise Security Operations",
      targetCompany = "FinTech / Cloud Infrastructure Enterprise",
      overallMatchPercentage = matchPct,
      extractedSkills = extracted,
      missingSkills = listOf(
        "Sigma Rule Engineering (Current: 58%, Need: 80%)",
        "AWS CloudTrail Log Triage (Current: 64%, Need: 75%)",
        "Executive Communication without Jargon (Current: 65%, Need: 75%)"
      ),
      customLab = customLab,
      interviewQuestions = listOf(
        "Explain how you correlate Sysmon Event ID 1 with Sysmon Event ID 3 in Splunk.",
        "How do you investigate a Pass-the-Hash alert without using technical jargon when speaking to a CFO?",
        "What is the difference between Kerberoasting and AS-REP Roasting?"
      ),
      recommendedPortfolioProject = "Enterprise Sigma Detection Engine for WMI Lateral Movement",
      shortestPreparationRoadmap = listOf(
        "Day 1: Run Generated Multi-Tool SOC Lab (35 min)",
        "Day 2: Complete Voice Crisis Drill with CFO Persona (10 min)",
        "Day 3: Publish Sigma Detection Capstone to GitHub (45 min)",
        "Day 4: Export Verified Skill Passport & Apply (5 min)"
      )
    )
  }

  // ============================================================================
  // 6. CYBER WORKPLACE SIMULATOR 2.0 & INBOX FEEDS
  // ============================================================================

  private val initialWorkplaceFeed = listOf(
    WorkplaceFeedItem(
      id = "wp_msg_01",
      channel = WorkplaceChannel.INCIDENT_NOTICE,
      sender = "SOC P1 Alert Engine <soc-pager@apexbank.internal>",
      subject = "CRITICAL ALERT: Potential Mimikatz LSASS Access on WIN-SRV-FINANCE-02",
      body = "EDR telemetry triggered Alert #9042: ProcessGuid {d3b07384} opened handle to lsass.exe with GrantedAccess 0x1010. Host contains production payroll records.",
      timestamp = "10:42 AM",
      isFalsePositive = false,
      ambiguityLevel = "LOW",
      requiredActionType = "Immediate Host Isolation & Memory Dump"
    ),
    WorkplaceFeedItem(
      id = "wp_msg_02",
      channel = WorkplaceChannel.SLACK_MSG,
      sender = "Sarah Chen (Tier 2 Lead)",
      subject = "#soc-incident-war-room",
      body = "@vance Can you double check if this is legitimate backup agent activity before pulling the network plug? Finance team is running month-end payroll right now.",
      timestamp = "10:43 AM",
      isFalsePositive = false,
      ambiguityLevel = "MEDIUM",
      requiredActionType = "Process Lineage Verification"
    ),
    WorkplaceFeedItem(
      id = "wp_msg_03",
      channel = WorkplaceChannel.EMAIL,
      sender = "David Vance (CFO) <dvance@apexbank.internal>",
      subject = "URGENT: Is payroll down? Why is my team getting authentication errors?",
      body = "Please brief me immediately in plain English. Are we under a cyber attack or is IT running maintenance?",
      timestamp = "10:46 AM",
      isFalsePositive = false,
      ambiguityLevel = "HIGH",
      requiredActionType = "Executive Plain-English De-escalation Briefing"
    ),
    WorkplaceFeedItem(
      id = "wp_msg_04",
      channel = WorkplaceChannel.TICKET,
      sender = "ServiceNow Auto-Ticketer",
      subject = "TICKET-8891: Developer requesting local admin on Linux build server",
      body = "Developer John Doe requested sudo access to install Docker container packages.",
      timestamp = "10:48 AM",
      isFalsePositive = true,
      ambiguityLevel = "LOW",
      requiredActionType = "Standard Least-Privilege Role Assignment"
    )
  )

  private val _workplaceFeed = MutableStateFlow<List<WorkplaceFeedItem>>(initialWorkplaceFeed)
  val workplaceFeed: StateFlow<List<WorkplaceFeedItem>> = _workplaceFeed.asStateFlow()

  // ============================================================================
  // 7. CONSEQUENCE ENGINE & MULTIVERSE BRANCH COMPARISON
  // ============================================================================

  fun evaluateDecisionConsequence(decisionOption: String): ConsequenceOutcome {
    return when (decisionOption) {
      "IMMEDIATE_ISOLATION" -> ConsequenceOutcome(
        businessImpactDescription = "Payroll server isolated. Month-end batch job delayed by 25 minutes ($1,200 delay cost), but zero lateral movement occurred.",
        attackerMovementDelta = "CONTAINED IN 78 SECONDS. Attacker locked out of Domain Controller.",
        detectionSpeedScore = 95,
        operationalCostImpact = "Low ($1,200 delay vs $2.4M ransomware)",
        isOptimalDecision = true,
        educationalDebrief = "Optimal action: Host isolation contained the threat before LSASS credentials could be relayed across Active Directory."
      )
      "WAIT_FOR_MORE_LOGS" -> ConsequenceOutcome(
        businessImpactDescription = "Attacker successfully harvested Domain Admin NTLM hashes and moved laterally to WIN-DC-01. Ransomware deployed at 11:15 AM.",
        attackerMovementDelta = "UNCONTAINED LATERAL MOVEMENT. Full Domain Compromise.",
        detectionSpeedScore = 35,
        operationalCostImpact = "Catastrophic ($2.4M incident response & regulatory fines)",
        isOptimalDecision = false,
        educationalDebrief = "Failure to act on high-confidence LSASS access telemetry allowed attacker to escalate from workstation to Domain Controller."
      )
      else -> ConsequenceOutcome(
        businessImpactDescription = "Cautious isolation with secondary memory dump captured attacker C2 beacon IP without disrupting read-only database replicas.",
        attackerMovementDelta = "CONTAINED SAFELY. Memory artifacts preserved for law enforcement dossier.",
        detectionSpeedScore = 90,
        operationalCostImpact = "Minimal ($500 verification cost)",
        isOptimalDecision = true,
        educationalDebrief = "Balanced response: Preserved forensic memory artifacts while executing network containment."
      )
    }
  }

  val multiverseTimelines = listOf(
    MultiverseTimelineNode("Your Timeline (Optimal Containment)", "Isolated in 78s. LSASS credentials uncompromised. Payroll delayed 25m.", 0.4, 0.0, "$1,200"),
    MultiverseTimelineNode("Hesitation Timeline (Delayed 12m)", "Attacker harvested DA hashes, dumped SAM database, established C2 beacon.", 4.5, 450.0, "$185,000"),
    MultiverseTimelineNode("Catastrophic Timeline (Ignored Alert)", "Full enterprise ransomware encryption across 42 servers and AWS backups.", 72.0, 12800.0, "$2,450,000")
  )

  // ============================================================================
  // 8. COGNITIVE ARENAS (Feynman, Executive Boardroom, Calibration)
  // ============================================================================

  fun evaluateFeynmanExplanation(concept: String, audience: FeynmanAudience, text: String): CyberFeynmanAssessment {
    val length = text.length
    val hasJargon = text.contains("as-req", ignoreCase = true) || text.contains("malleable c2", ignoreCase = true) || text.contains("bpf syntax", ignoreCase = true)
    val hasMetaphor = text.contains("like", ignoreCase = true) || text.contains("imagine", ignoreCase = true) || text.contains("badge", ignoreCase = true) || text.contains("passport", ignoreCase = true)

    val (accuracy, clarity, jargonScore, adaptation) = when (audience) {
      FeynmanAudience.BEGINNER -> {
        val jScore = if (hasJargon) 45 else 92
        val adScore = if (hasMetaphor) 94 else 60
        listOf(88, 86, jScore, adScore)
      }
      FeynmanAudience.CISO -> {
        val bizFocus = text.contains("risk", ignoreCase = true) || text.contains("business", ignoreCase = true) || text.contains("cost", ignoreCase = true) || text.contains("downtime", ignoreCase = true)
        listOf(90, 88, 75, if (bizFocus) 92 else 55)
      }
      FeynmanAudience.TECHNICAL_ENGINEER -> {
        val techFocus = text.contains("api", ignoreCase = true) || text.contains("packet", ignoreCase = true) || text.contains("process", ignoreCase = true)
        listOf(92, 90, 88, if (techFocus) 90 else 70)
      }
      FeynmanAudience.SOC_ANALYST -> {
        listOf(94, 92, 90, 92)
      }
    }

    val overall = ((accuracy + clarity + jargonScore + adaptation) / 4)

    val debrief = when {
      audience == FeynmanAudience.BEGINNER && hasJargon -> "You used dense acronyms. When explaining to beginners, replace technical protocol terms with everyday physical metaphors (e.g. hotel room keys or airport checkpoints)."
      audience == FeynmanAudience.CISO && overall >= 85 -> "Excellent executive briefing! You translated technical mechanism into business impact, containment status, and risk mitigation clearly."
      else -> "Strong conceptual grasp. Good balance of technical accuracy and clear explanatory pacing."
    }

    return CyberFeynmanAssessment(
      conceptTitle = concept,
      selectedAudience = audience,
      learnerExplanation = text,
      accuracyScore = accuracy,
      clarityScore = clarity,
      jargonScore = jargonScore,
      adaptationScore = adaptation,
      overallScore = overall,
      aiEvaluationDebrief = debrief
    )
  }

  // ============================================================================
  // 9. PORTFOLIO PROJECTS & PERSONAL KNOWLEDGE VAULT
  // ============================================================================

  private val initialPortfolioProjects = listOf(
    V9PortfolioProject(
      id = "prj_v9_01",
      title = "Enterprise Sigma Detection Engine for WMI Lateral Movement (MITRE T1047)",
      problemStatement = "Detecting fileless WMI process spawning across 500+ endpoints without alerting on legitimate administrative SCCM jobs.",
      targetRole = "SOC Analyst / Detection Engineer",
      architectureDescription = "Sysmon Event ID 1/3 kernel streaming -> Wazuh/Splunk log pipeline -> Custom Sigma YAML rules with baseline exception filters.",
      threatModelFramework = "MITRE ATT&CK T1047 & STRIDE Framework",
      technologies = listOf("Sigma YAML", "Sysmon", "Splunk SPL", "PowerShell", "GitHub Actions"),
      stepByStepImplementationGuide = listOf(
        "1. Configure Sysmon XML schema logging ParentImage and CommandLine parameters.",
        "2. Author Sigma rule detecting wmiprvse.exe spawning cmd.exe or powershell.exe with encoded payloads.",
        "3. Filter benign service account SIDs via automated baseline dictionary.",
        "4. Validate rule against 10,000 raw production log events in CI/CD pipeline."
      ),
      verificationAndTestProof = "Achieved 100% true-positive capture against simulated Cobalt Strike lateral movement with 0% false positives.",
      githubRepoStructure = "github.com/aegora-evidence/vance-wmi-sigma-engine\n├── rules/\n│   └── wmi_lateral_movement.yml\n├── tests/\n│   └── test_telemetry.pcap\n└── README.md",
      resumeBulletPoint = "Engineered custom Sigma detection rules for MITRE T1047 WMI lateral movement, reducing endpoint alert noise by 42% across 500+ simulated nodes.",
      starInterviewStory = "Situation: High volume of fileless lateral movement bypassed standard antivirus.\nTask: Create reliable detection without alerting on SCCM.\nAction: Authored Sigma rules correlating wmiprvse.exe process lineage.\nResult: 100% detection rate and zero false-positive disruption.",
      demonstratedCompetenciesCount = 8
    )
  )

  private val _portfolioProjects = MutableStateFlow<List<V9PortfolioProject>>(initialPortfolioProjects)
  val portfolioProjects: StateFlow<List<V9PortfolioProject>> = _portfolioProjects.asStateFlow()

  private val initialVaultNotes = listOf(
    PersonalKnowledgeVaultNote(
      id = "vlt_01",
      title = "Sysmon Event ID 1 vs Event ID 3 Correlation Rules",
      category = "COMMAND_CHEAT",
      content = "Always correlate Event ID 1 (Process Creation) with Event ID 3 (Network Connection) using ProcessGuid rather than ProcessId (PID), because PIDs are recycled by Windows kernel.",
      connectedKnowledgeNodeIds = listOf("kn_sysmon", "kn_windows_internals"),
      cveReferences = listOf("CVE-2026-4401"),
      commandSnippets = listOf("index=windows EventCode=1 | join type=inner ProcessGuid [search EventCode=3]"),
      tags = listOf("sysmon", "splunk", "soc-cheat", "process-lineage"),
      createdAt = "2026-08-26"
    ),
    PersonalKnowledgeVaultNote(
      id = "vlt_02",
      title = "TLS 1.3 Key Exchange Ephemeral Diffie-Hellman Proof",
      category = "CONCEPT",
      content = "TLS 1.3 deprecated static RSA key exchange entirely. All handshakes use ECDHE or ML-KEM to ensure Forward Secrecy: compromising server private key does not allow retroactive decryption of recorded PCAPs.",
      connectedKnowledgeNodeIds = listOf("kn_tls13", "kn_crypto_basics"),
      cveReferences = emptyList(),
      commandSnippets = listOf("openssl s_client -connect apexbank.internal:443 -tls1_3"),
      tags = listOf("cryptography", "tls13", "forward-secrecy"),
      createdAt = "2026-08-25"
    )
  )

  private val _vaultNotes = MutableStateFlow<List<PersonalKnowledgeVaultNote>>(initialVaultNotes)
  val vaultNotes: StateFlow<List<PersonalKnowledgeVaultNote>> = _vaultNotes.asStateFlow()

  // ============================================================================
  // 10. FAIR COMPETITION LEADERBOARD (Multi-Dimensional Skill Bands)
  // ============================================================================

  private val _competitionLeaderboard = MutableStateFlow<List<CompetitionLeaderboardEntry>>(
    listOf(
      CompetitionLeaderboardEntry(1, "VANCE-SOC (You)", 2180, 88, 84, 85, 74, 94, SkillBandTier.TIER_3_SPECIALIST, true),
      CompetitionLeaderboardEntry(2, "CYBER-VALKYRIE", 2150, 92, 80, 82, 80, 88, SkillBandTier.TIER_3_SPECIALIST, false),
      CompetitionLeaderboardEntry(3, "NULL_POINTER_IR", 2090, 84, 88, 80, 78, 85, SkillBandTier.TIER_3_SPECIALIST, false),
      CompetitionLeaderboardEntry(4, "SENTINEL_PRIME", 1980, 80, 82, 78, 85, 90, SkillBandTier.TIER_3_SPECIALIST, false),
      CompetitionLeaderboardEntry(5, "HEX_OVERFLOW", 1850, 78, 76, 75, 70, 92, SkillBandTier.TIER_3_SPECIALIST, false)
    )
  )
  val competitionLeaderboard: StateFlow<List<CompetitionLeaderboardEntry>> = _competitionLeaderboard.asStateFlow()

  // ============================================================================
  // 11. DEVELOPER V9 TRACE STREAM
  // ============================================================================

  private val _v9TraceStream = MutableStateFlow<List<String>>(
    listOf(
      "[2026-08-27 22:10] V9 REALITY ENGINE: Ingested CVE-2026-4401 (Primary Trust 98%) -> Generated Safe Sandbox Lesson scen_v8_sandbox",
      "[2026-08-27 22:05] PERSONAL RADAR: Prioritized S3 Ransomware Canary for Portfolio Project Generation (Score: 84%)",
      "[2026-08-27 21:50] KNOWLEDGE GRAPH 3.0: Recalibrated 30 nodes across 17 types. Career alignment: SOC Analyst L1 (69%)",
      "[2026-08-27 21:40] CONSEQUENCE ENGINE: Evaluated branch 'IMMEDIATE_ISOLATION' -> Optimal decision recorded, +5 Investigation delta"
    )
  )
  val v9TraceStream: StateFlow<List<String>> = _v9TraceStream.asStateFlow()

  fun addTraceLog(message: String) {
    val timestamp = dateFormat.format(Date())
    _v9TraceStream.value = listOf("[$timestamp] $message") + _v9TraceStream.value
  }

  fun saveVaultNote(title: String, category: String, content: String, tags: List<String>) {
    val newNote = PersonalKnowledgeVaultNote(
      id = "vlt_${UUID.randomUUID().toString().take(6)}",
      title = title,
      category = category,
      content = content,
      connectedKnowledgeNodeIds = listOf("kn_sysmon", "kn_career_soc"),
      cveReferences = emptyList(),
      commandSnippets = emptyList(),
      tags = tags,
      createdAt = shortDateFormat.format(Date())
    )
    _vaultNotes.value = listOf(newNote) + _vaultNotes.value
    addTraceLog("PERSONAL VAULT: Saved new note '${newNote.title}' connected to Knowledge Graph 3.0")
  }
}
