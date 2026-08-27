package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository catalog for the Resource Intelligence Universe.
 * Authoritative, peer-reviewed, and official cybersecurity books, RFCs, research papers,
 * cheat sheets, tool guides, and incident case studies mapped directly to career paths & skills.
 */
object ResourceUniverseRepository {

  private val _resources = MutableStateFlow(
    listOf(
      ResourceKnowledgeItem(
        id = "res_wahh_01",
        title = "The Web Application Hacker's Handbook",
        subtitle = "Finding and Exploiting Security Flaws (2nd Edition)",
        category = ResourceCategory.BOOKS,
        authorOrOrg = "Dafydd Stuttard & Marcus Pinto (PortSwigger)",
        publicationYear = "2011 (Classic / Evergreen)",
        trustLevel = ResourceTrustLevel.OFFICIAL,
        accessType = ResourceAccessType.PUBLISHER_PREVIEW,
        readingOrStudyTime = "14 Hours (Structured Chapters)",
        quality = ResourceQualityScore(
          overallScore = 98,
          authorityScore = 99,
          practicalValueScore = 98,
          careerRelevanceScore = 96,
          recencyScore = 88
        ),
        targetedCareerPaths = listOf("pentester", "appsec_engineer", "soc_analyst"),
        targetedSkills = listOf("Web App Pentesting", "OWASP Top 10", "SQL Injection", "Session Security", "XSS"),
        keyTakeaways = listOf(
          "Core mechanics of client-side validation bypass and HTTP manipulation.",
          "Systematic methodologies for stateful authentication and CSRF token verification.",
          "Deep-dive input sanitization vs output encoding defense patterns."
        ),
        whyThisMatters = "This is the seminal foundational textbook that defined modern AppSec methodology. Mastering its core principles makes every PortSwigger and Bug Bounty challenge straightforward.",
        relatedLabId = "lab_appsec_01",
        relatedLabTitle = "AppSec Ladder Level 1 • SQLi & Parameter Injection",
        officialUrlOrDoc = "https://portswigger.net/web-security"
      ),
      ResourceKnowledgeItem(
        id = "res_nist_800_61",
        title = "NIST SP 800-61 Rev. 2",
        subtitle = "Computer Security Incident Handling Guide",
        category = ResourceCategory.STANDARDS,
        authorOrOrg = "National Institute of Standards and Technology (NIST)",
        publicationYear = "NIST Authoritative",
        trustLevel = ResourceTrustLevel.OFFICIAL,
        accessType = ResourceAccessType.OFFICIAL_SPEC,
        readingOrStudyTime = "45 Min Read",
        quality = ResourceQualityScore(
          overallScore = 99,
          authorityScore = 100,
          practicalValueScore = 97,
          careerRelevanceScore = 100,
          recencyScore = 94
        ),
        targetedCareerPaths = listOf("soc_analyst", "dfir_analyst", "threat_hunter"),
        targetedSkills = listOf("Incident Response Lifecycle", "Containment Strategies", "Evidence Preservation", "Chain of Custody"),
        keyTakeaways = listOf(
          "The four canonical IR phases: Preparation -> Detection & Analysis -> Containment, Eradication & Recovery -> Post-Incident Activity.",
          "Standardized containment criteria: Short-term isolation vs Long-term eradication without evidence destruction.",
          "Standardized incident reporting schemas for legal and regulatory compliance."
        ),
        whyThisMatters = "Virtually all global enterprise SOCs and CIRTs base their incident playbooks directly on NIST SP 800-61. Every SOC interview tests these four phases.",
        relatedLabId = "lab_soc_01",
        relatedLabTitle = "Live SOC Incident Range • Kerberoasting Alert Triage",
        officialUrlOrDoc = "https://csrc.nist.gov/publications/detail/sp/800-61/rev-2/final"
      ),
      ResourceKnowledgeItem(
        id = "res_mitre_attack_spec",
        title = "MITRE ATT&CK® Enterprise Framework",
        subtitle = "Adversary Tactics, Techniques, and Common Knowledge",
        category = ResourceCategory.STANDARDS,
        authorOrOrg = "The MITRE Corporation",
        publicationYear = "v15.1 Active Knowledgebase",
        trustLevel = ResourceTrustLevel.OFFICIAL,
        accessType = ResourceAccessType.OPEN_ACCESS,
        readingOrStudyTime = "Continuous Reference",
        quality = ResourceQualityScore(
          overallScore = 99,
          authorityScore = 100,
          practicalValueScore = 99,
          careerRelevanceScore = 100,
          recencyScore = 100
        ),
        targetedCareerPaths = listOf("soc_analyst", "threat_hunter", "pentester", "dfir_analyst"),
        targetedSkills = listOf("MITRE ATT&CK Matrix", "Adversary Emulation", "Telemetry Mapping", "Detection Engineering"),
        keyTakeaways = listOf(
          "Standardized matrix mapping 14 tactics from Reconnaissance to Exfiltration and Impact.",
          "Sub-technique classification linking adversary procedures to Sysmon, eBPF, and CloudTrail telemetry.",
          "Actionable mitigation and detection analytics (Sigma/YARA mappings)."
        ),
        whyThisMatters = "ATT&CK provides the universal grammar for threat hunters, detection engineers, and red teamers to measure defensive coverage objectively.",
        relatedLabId = "lab_shadow_01",
        relatedLabTitle = "Autonomous Adversary Shadow Range • Phase 1-5 Simulation",
        officialUrlOrDoc = "https://attack.mitre.org/"
      ),
      ResourceKnowledgeItem(
        id = "res_practical_malware_analysis",
        title = "Practical Malware Analysis",
        subtitle = "The Hands-On Guide to Dissecting Malicious Software",
        category = ResourceCategory.BOOKS,
        authorOrOrg = "Michael Sikorski & Andrew Honig",
        publicationYear = "No Starch Press",
        trustLevel = ResourceTrustLevel.OFFICIAL,
        accessType = ResourceAccessType.PUBLISHER_PREVIEW,
        readingOrStudyTime = "18 Hours (Interactive Labs)",
        quality = ResourceQualityScore(
          overallScore = 97,
          authorityScore = 98,
          practicalValueScore = 99,
          careerRelevanceScore = 95,
          recencyScore = 90
        ),
        targetedCareerPaths = listOf("dfir_analyst", "threat_hunter", "soc_analyst"),
        targetedSkills = listOf("Static Analysis", "x86-64 Disassembly", "Ghidra / IDA Pro", "Dynamic Debugging", "PE Headers"),
        keyTakeaways = listOf(
          "Safe virtualization and sandbox detonation methodologies.",
          "Reading x86 assembly: registers, stack frames, call instructions, and jump branches.",
          "Unpacking techniques, anti-analysis detection bypass, and memory scraping."
        ),
        whyThisMatters = "The gold-standard reference for reverse engineering malware binaries. Explains how code executes at the CPU instruction level.",
        relatedLabId = "lab_disassembler_01",
        relatedLabTitle = "Binary Disassembler Studio • Stack Canary & ASLR Bypass",
        officialUrlOrDoc = "https://nostarch.com/malware"
      ),
      ResourceKnowledgeItem(
        id = "res_rfc_7617_8446",
        title = "RFC 8446 & RFC 7230 Specifications",
        subtitle = "The Transport Layer Security (TLS) 1.3 & HTTP/1.1 Message Syntax",
        category = ResourceCategory.STANDARDS,
        authorOrOrg = "Internet Engineering Task Force (IETF)",
        publicationYear = "IETF Standards Track",
        trustLevel = ResourceTrustLevel.OFFICIAL,
        accessType = ResourceAccessType.OPEN_ACCESS,
        readingOrStudyTime = "1 Hour 15 Min",
        quality = ResourceQualityScore(
          overallScore = 96,
          authorityScore = 100,
          practicalValueScore = 94,
          careerRelevanceScore = 95,
          recencyScore = 98
        ),
        targetedCareerPaths = listOf("soc_analyst", "pentester", "cloud_security", "appsec_engineer"),
        targetedSkills = listOf("Network Protocols", "TLS Handshake 1.3", "Packet Capture Dissection", "Diffie-Hellman Key Exchange"),
        keyTakeaways = listOf(
          "1-RTT TLS Handshake structure eliminating obsolete insecure ciphers (RC4, DES, 3DES).",
          "Forward secrecy guarantees: Ephemeral ECDHE key agreements.",
          "HTTP pipelining, chunked transfer encoding, and request smuggling attack vectors."
        ),
        whyThisMatters = "Understanding raw wire protocols separates surface-level tool operators from true network security engineers who can dissect encrypted traffic flows.",
        relatedLabId = "lab_acoustic_01",
        relatedLabTitle = "Sonic Radar Threat Sonification • DNS C2 Waveform Drill",
        officialUrlOrDoc = "https://datatracker.ietf.org/doc/html/rfc8446"
      ),
      ResourceKnowledgeItem(
        id = "res_cloud_threat_report",
        title = "Unit 42 Cloud Threat Report & AWS CIS Benchmark v3.0",
        subtitle = "Lateral Movement & IAM Identity Compromise in Multi-Cloud",
        category = ResourceCategory.PAPERS,
        authorOrOrg = "Palo Alto Networks Unit 42 & CIS Security",
        publicationYear = "2024 Research Report",
        trustLevel = ResourceTrustLevel.INDUSTRY,
        accessType = ResourceAccessType.OPEN_ACCESS,
        readingOrStudyTime = "35 Min",
        quality = ResourceQualityScore(
          overallScore = 95,
          authorityScore = 96,
          practicalValueScore = 96,
          careerRelevanceScore = 98,
          recencyScore = 99
        ),
        targetedCareerPaths = listOf("cloud_security", "threat_hunter", "soc_analyst"),
        targetedSkills = listOf("AWS IAM Policies", "STS AssumeRole Escalation", "S3 Bucket Auditing", "CloudTrail Triage"),
        keyTakeaways = listOf(
          "99% of cloud identity compromises stem from overly permissive IAM roles and wildcard permissions.",
          "Adversary lateral movement techniques using temporary STS tokens and metadata service (IMDSv2) scraping.",
          "Automated drift detection strategies with Terraform IaC compliance gates."
        ),
        whyThisMatters = "Cloud security is predominantly identity security. This report provides exact blueprints on how attackers pivot across cloud accounts.",
        relatedLabId = "lab_cloud_iam_01",
        relatedLabTitle = "Cloud IAM Least Privilege Scanner & Remediation",
        officialUrlOrDoc = "https://unit42.paloaltonetworks.com/"
      ),
      ResourceKnowledgeItem(
        id = "res_sigma_yara_cheatsheet",
        title = "SigmaHQ & YARA Rule Creation Cheat Sheet",
        subtitle = "Generic Detection Rules for SIEMs and File Scanners",
        category = ResourceCategory.CHEAT_SHEETS,
        authorOrOrg = "SigmaHQ & Florian Roth (Nextron Systems)",
        publicationYear = "Curated Community Standard",
        trustLevel = ResourceTrustLevel.OFFICIAL,
        accessType = ResourceAccessType.OPEN_ACCESS,
        readingOrStudyTime = "20 Min Practical Guide",
        quality = ResourceQualityScore(
          overallScore = 96,
          authorityScore = 98,
          practicalValueScore = 100,
          careerRelevanceScore = 97,
          recencyScore = 98
        ),
        targetedCareerPaths = listOf("soc_analyst", "threat_hunter", "dfir_analyst"),
        targetedSkills = listOf("Sigma Rule Writing", "YARA String Patterns", "Sysmon Event ID Queries", "Detection as Code"),
        keyTakeaways = listOf(
          "Sigma schema syntax: selection fields, condition boolean logic, and logsource categorization.",
          "Compiling Sigma rules to Splunk SPL, Elastic DSL, and Microsoft Sentinel KQL.",
          "Writing optimized YARA byte-sequences with wildcard masks and condition loops."
        ),
        whyThisMatters = "Modern detection engineers write vendor-neutral Sigma rules. Mastering this lets you deploy detections instantly to any enterprise SIEM.",
        relatedLabId = "lab_zero_day_01",
        relatedLabTitle = "Zero-Day Lab • Sigma/YARA Studio & Live Rule Compiler",
        officialUrlOrDoc = "https://github.com/SigmaHQ/sigma"
      ),
      ResourceKnowledgeItem(
        id = "res_solarwinds_case_study",
        title = "SUNBURST / SolarWinds Supply Chain Incident Analysis",
        subtitle = "Technical Deep-Dive into Advanced Persistent Threat (APT29 / Nobelium)",
        category = ResourceCategory.CASE_STUDIES,
        authorOrOrg = "CISA, Mandiant & Microsoft Security Intelligence",
        publicationYear = "Authoritative Forensic Case Study",
        trustLevel = ResourceTrustLevel.OFFICIAL,
        accessType = ResourceAccessType.OPEN_ACCESS,
        readingOrStudyTime = "50 Min Deep Study",
        quality = ResourceQualityScore(
          overallScore = 99,
          authorityScore = 100,
          practicalValueScore = 98,
          careerRelevanceScore = 99,
          recencyScore = 95
        ),
        targetedCareerPaths = listOf("dfir_analyst", "threat_hunter", "soc_analyst", "cloud_security"),
        targetedSkills = listOf("Supply Chain Security", "DLL Backdooring", "DGA DNS C2 Channels", "Golden SAML Token Forgery"),
        keyTakeaways = listOf(
          "How adversaries injected lightweight backdoors into build pipelines before compilation.",
          "DNS tunneling communication imitating legitimate SolarWinds network protocol packets.",
          "SAML token signing certificate compromise leading to cloud federation lateral movement without passwords."
        ),
        whyThisMatters = "The most sophisticated supply chain cyber espionage operation in history. Understanding its timeline builds elite forensic instincts.",
        relatedLabId = "lab_timeline_01",
        relatedLabTitle = "Timeline Forking & Time-Machine • Dual-Timeline Blast Diff",
        officialUrlOrDoc = "https://www.cisa.gov/news-events/cybersecurity-advisories"
      )
    )
  )
  val resources: StateFlow<List<ResourceKnowledgeItem>> = _resources.asStateFlow()

  fun toggleBookmark(resourceId: String) {
    _resources.value = _resources.value.map { item ->
      if (item.id == resourceId) item.copy(isBookmarked = !item.isBookmarked) else item
    }
  }

  fun toggleComplete(resourceId: String) {
    _resources.value = _resources.value.map { item ->
      if (item.id == resourceId) item.copy(isCompleted = !item.isCompleted) else item
    }
  }
}
