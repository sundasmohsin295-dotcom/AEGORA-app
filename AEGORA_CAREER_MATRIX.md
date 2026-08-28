# AEGORA v6.2 — Comprehensive Career Role Matrix & Competency Registry

This registry defines the complete 38-role career architecture spanning 6 specialized role families. Every role is specified with core responsibilities, toolsets, frameworks, learning paths, labs, capstones, target certifications, job requirements, and evidence standards.

---

## Summary of Role Families

| Role Family | Total Roles | Key Focus | Primary Frameworks |
| :--- | :---: | :--- | :--- |
| **1. Blue Team (Defensive)** | 8 | Triage, Incident Response, DFIR, Threat Hunting, Detection | MITRE ATT&CK, NIST SP 800-61, D3FEND, Sigma |
| **2. Red Team (Offensive)** | 5 | Adversary Emulation, PenTesting, Exploit Dev, Vulnerability Research | MITRE ATT&CK, PTES, OWASP Top 10, CWE |
| **3. Security Engineering** | 7 | Infrastructure Hardening, Cloud Security, AppSec, DevSecOps, IAM | CIS Benchmarks, NIST CSF, ISO 27001, AWS Well-Architected |
| **4. GRC & Risk** | 5 | Compliance, Third-Party Risk, Privacy, Security Audit | NIST CSF 2.0, SOC 2 Type II, ISO/IEC 27001, GDPR |
| **5. Leadership & Strategy** | 5 | CISO, Program Management, DPO, Product Security Leadership | FAIR, CMMI, SABSA, COBIT, Board Governance |
| **6. Specialized Domains** | 13 | AI/ML Security, LLM Red Teaming, ICS/OT, Automotive, Post-Quantum | OWASP Top 10 for LLM, NIST AI RMF, ISA/IEC 62443, NIST PQC |

---

## 1. Blue Team (Defensive Operations)

### 1.1 SOC Analyst L1 (Alert Triage Specialist)
- **Difficulty**: Entry Level (L1)
- **Description**: Monitors real-time SIEM/EDR queues, evaluates incoming alerts, filters false positives, and executes initial containment playbooks.
- **Responsibilities**: Alarm triage, initial PCAP filtering, malicious hash lookup, ticket escalation.
- **Skills**: TCP/IP analysis, Syslog parsing, Windows Event Log analysis, Phishing email header triage.
- **Tools**: Splunk, CrowdStrike Falcon, Wireshark, TheHive, VirusTotal.
- **Frameworks**: MITRE ATT&CK, NIST SP 800-61.
- **Learning Paths**: SOC Foundations, Network Defense Fundamentals, Endpoint Forensics L1.
- **Labs**: Phishing Header Investigation, Brute Force Triage Lab, Malicious Macro PCAP.
- **Capstone**: 24-hour Shift Triage Simulation with 10 complex multi-source alert queues.
- **Certifications**: CompTIA Security+, Cisco CyberOps Associate, Microsoft SC-200.
- **Job Requirements**: Understanding of OSI layers, basic scripting (Bash/Python), ticket SLA discipline.
- **Interview Questions**: "Walk me through how you investigate a suspicious PowerShell encoded command in Windows Event Log 4688."
- **Evidence Requirements**: 5 verified true/false positive triage write-ups with raw PCAP evidence.

### 1.2 SOC Analyst L2 (Incident Investigator)
- **Difficulty**: Associate (L2)
- **Description**: Handles escalated complex incidents, performs host memory triage, reconstructs attack timelines, and correlates multi-host indicators.
- **Responsibilities**: Deep dive triage, memory analysis, initial lateral movement scoping, firewall rule adjustment.
- **Tools**: Velociraptor, KAPE, Splunk ES, Zeek, YARA.
- **Certifications**: BTL1, CySA+, GIAC GCIH.

### 1.3 SOC Analyst L3 (Senior Incident Responder & Escalation Lead)
- **Difficulty**: Senior Specialist (L3)
- **Description**: Serves as highest escalation tier for enterprise intrusions, orchestrates containment across multi-cloud infrastructure, and mentors L1/L2 analysts.
- **Tools**: Volatility 3, Plaso, Ghidra, MISP, Sentinel.
- **Certifications**: GCFA, BTL2, Certified Incident Handler (EC-Council).

### 1.4 Incident Responder (CIRT Specialist)
- **Difficulty**: Senior Specialist (L3)
- **Description**: Rapid on-site and remote containment of ransomware, extortion campaigns, and active nation-state breaches.
- **Responsibilities**: Host isolation, enterprise eradication, blast radius quantification, executive response briefings.
- **Tools**: Volatility, FTK Imager, KAPE, AWS CloudTrail, CrowdStrike Real Time Response.
- **Frameworks**: NIST SP 800-61 Rev 2, SANS Incident Response Lifecycle.
- **Certifications**: SANS FOR508 (GCFA), SANS FOR500 (GCFE).

### 1.5 Digital Forensics Analyst (DFIR Specialist)
- **Difficulty**: Senior Specialist (L3)
- **Description**: Executes bit-stream forensic disk acquisitions, deep MFT filesystem parsing, registry analysis, and chain-of-custody documentation.
- **Tools**: Autopsy, EnCase, FTK Imager, X-Ways, Plaso, Volatility.
- **Certifications**: GCFA, EnCE, CCE.

### 1.6 Threat Hunter (Proactive Adversary Hunting)
- **Difficulty**: Senior Specialist (L3)
- **Description**: Formulates hypotheses to uncover persistent adversaries residing in enterprise environments without active alarms.
- **Tools**: Sigma, Zeek, Jupyter Notebooks, Elastic EQL, Sysmon.
- **Frameworks**: MITRE ATT&CK, Diamond Model of Intrusion Analysis.
- **Certifications**: SANS SEC599, SANS SEC555 (GCDA).

### 1.7 Threat Intelligence Analyst (CTI Specialist)
- **Difficulty**: Associate / Senior
- **Description**: Tracks APT campaigns, maps adversary TTPs, curates indicator feeds, and delivers strategic threat advisories for CISOs.
- **Tools**: OpenCTI, MISP, Shodan, Recorded Future, Maltego.
- **Certifications**: SANS FOR578 (GCTI), Certified Threat Intelligence Analyst (CTIA).

### 1.8 Detection Engineer (Defensive Content Developer)
- **Difficulty**: Senior Specialist (L3)
- **Description**: Engineers, tests, and deploys high-fidelity detection rules (Sigma, YARA, Snort, Splunk SPL) with automated CI/CD validation.
- **Tools**: Sigma CLI, SubZero, GitHub Actions, Atomic Red Team, Splunk.
- **Certifications**: SANS SEC555, Applied Detection Engineering (TCM).

---

## 2. Red Team (Offensive Security)

### 2.1 Penetration Tester (Network & Web Assessment)
- **Difficulty**: Associate / Senior
- **Description**: Conducts authorized, goal-oriented penetration tests against network perimeters, Active Directory, and web applications.
- **Tools**: Burp Suite Pro, Nmap, Metasploit, BloodHound, Impacket.
- **Frameworks**: PTES, OWASP Testing Guide, MITRE ATT&CK.
- **Certifications**: OSCP (OffSec Certified Professional), PNPT, eWPTX.

### 2.2 Red Team Operator (Adversary Simulation Specialist)
- **Difficulty**: Senior / Staff
- **Description**: Simulates full-scope advanced adversary campaigns bypassing EDR, executing stealthy C2 communication, and lateral movement.
- **Tools**: Cobalt Strike, Sliver, Mythic, BloodHound, Mimikatz.
- **Certifications**: CRTO (Certified Red Team Operator), OSEP.

### 2.3 Vulnerability Researcher (Zero-Day Discovery)
- **Difficulty**: Principal / Researcher (L8/L9)
- **Description**: Analyzes proprietary software and protocols to discover previously unknown security vulnerabilities (0-days).
- **Tools**: IDA Pro, Ghidra, AFL++, WinDbg, Frida.
- **Certifications**: OffSec SED-301 (OSEE).

### 2.4 Bug Bounty Researcher
- **Difficulty**: Independent Specialist
- **Description**: Hunts security vulnerabilities across global public and private vulnerability disclosure programs.
- **Tools**: Burp Suite, Nuclei, ffuf, Amass, httpx.

### 2.5 Exploit Developer (Binary & Memory Weaponization)
- **Difficulty**: Principal / Researcher
- **Description**: Crafts robust proof-of-concept exploits bypassing modern OS mitigations (ASLR, DEP, CET, SafeSEH, CFG).
- **Tools**: GDB, Pwntools, ROPgadget, Radare2.

---

## 3. Security Engineering & Architecture

### 3.1 Security Engineer (Infrastructure & Telemetry)
- **Tools**: Terraform, Wazuh, Suricata, Vault, Ansible.
- **Certifications**: CompTIA Security+, SANS SEC505.

### 3.2 Security Architect (Enterprise Security Design)
- **Frameworks**: SABSA, TOGAF, NIST SP 800-53, Zero Trust Architecture (NIST SP 800-207).
- **Certifications**: CISSP-ISSAP, CCISO.

### 3.3 Cloud Security Engineer (AWS/Azure/GCP Defense)
- **Tools**: AWS CloudTrail, GuardDuty, Terraform, Wiz, Prisma Cloud.
- **Certifications**: AWS Certified Security Specialty, Azure Security Engineer (AZ-500), CCSP.

### 3.4 Application Security Engineer (AppSec / Product Security)
- **Tools**: Semgrep, OWASP ZAP, Burp Suite, SonarQube, Postman.
- **Certifications**: CASE, CSSLP, OSWE (OffSec Web Expert).

### 3.5 DevSecOps Engineer (Pipeline Security Automation)
- **Tools**: GitHub Actions, Trivy, Checkov, Cosign, Snyk.
- **Certifications**: Certified DevSecOps Professional (CDP).

### 3.6 IAM Engineer (Identity & Zero Trust Access)
- **Tools**: Okta, Ping Identity, CyberArk, SailPoint, OAuth2/OIDC.
- **Certifications**: CIAM, Microsoft SC-300.

### 3.7 Network Security Engineer (Perimeter & Segmentation)
- **Tools**: Palo Alto Networks, Fortinet, Wireshark, Zeek, pfSense.
- **Certifications**: CCNP Security, PCNSE.

---

## 4. Governance, Risk & Compliance (GRC)

### 4.1 IT Risk Analyst (Quantitative Risk Management)
- **Frameworks**: FAIR, NIST SP 800-30, ISO 27005.
- **Certifications**: CRISC, CISA.

### 4.2 Cybersecurity Auditor (Controls Verification)
- **Frameworks**: SOC 2 Type II, ISO/IEC 27001:2022, PCI-DSS 4.0.
- **Certifications**: CISA, ISO 27001 Lead Auditor.

### 4.3 Compliance Analyst (Regulatory Governance)
- **Frameworks**: HIPAA Security Rule, FedRAMP, GDPR, NIS2, DORA.
- **Certifications**: GRCP, CIPP/E.

### 4.4 Privacy Specialist (Data Protection & Privacy by Design)
- **Frameworks**: GDPR, CCPA/CPRA, ISO 27701.
- **Certifications**: CIPP/US, CIPM, CIPT.

### 4.5 Third-Party Risk Manager (Vendor Ecosystem Assurance)
- **Frameworks**: SIG Lite, CAIQ, OneTrust TPRM.
- **Certifications**: CTPRP.

---

## 5. Leadership & Program Management

### 5.1 Security Manager (SOC / Team Lead)
- **Certifications**: CISM, CISSP.

### 5.2 Security Program Manager (Security PMO)
- **Certifications**: PMP, CISSP.

### 5.3 Chief Information Security Officer (CISO)
- **Responsibilities**: Board reporting, cybersecurity budget, enterprise risk posture, regulatory liaison.
- **Certifications**: CCISO, CISM, CISSP.

### 5.4 Data Protection Officer (DPO)
- **Certifications**: CDPO, CIPP/E.

### 5.5 Cybersecurity Product Manager
- **Responsibilities**: Security software roadmap, customer telemetry, threat compliance features.

---

## 6. Specialized & Emerging Domains

### 6.1 Cryptographer & Post-Quantum Security Specialist
- **Focus**: Lattice-based cryptography, Kyber/Dilithium, TLS 1.3 implementation, zero-knowledge proofs.

### 6.2 ICS/OT Security Engineer (SCADA & Critical Infrastructure)
- **Focus**: Modbus, DNP3, Siemens S7 protocols, Purdue Model, air-gap defense, ISA/IEC 62443.
- **Certifications**: GICSP (Global Industrial Cyber Security Professional).

### 6.3 AI Security Engineer & ML Security Researcher
- **Focus**: Adversarial machine learning, model evasion, training data poisoning, model inversion attacks, NIST AI RMF.

### 6.4 LLM Security & AI Red Teaming Specialist
- **Focus**: Prompt injection, indirect prompt hijacking, jailbreak extraction, model backdoors, OWASP Top 10 for LLMs.

### 6.5 Hardware & Firmware Security Engineer
- **Focus**: JTAG/UART debugging, SPI flash dumping, side-channel power analysis, fault injection, Ghidra firmware reversing.

### 6.6 IoT & Mobile Security Engineer
- **Focus**: Android/iOS application security, ARM reversing, BLE sniffing, Frida instrumentation, OWASP MASVS.

### 6.7 Automotive Security Engineer
- **Focus**: CAN bus protocol injection, ECU firmware analysis, ISO/SAE 21434, UNECE WP.29 compliance.

### 6.8 Blockchain & Smart Contract Security Auditor
- **Focus**: Reentrancy, integer overflow, flash loan exploits, EVM bytecode reversing, Slither/Foundry analysis.
