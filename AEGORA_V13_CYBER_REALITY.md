# AEGORA v13.0 — Cyber Reality Engine 2.0 & Entity Ontology

## Entity Types (22+ Core Classes)
1. **CVE**: Vulnerabilities indexed with CVSS v3.1/v4 scores and CISA KEV tags.
2. **Threat Actor / APT**: Nation-state and cybercrime adversary profiles (e.g. APT29, FIN7).
3. **Malware Family**: Ransomware, loaders, RATs, infostealers.
4. **Campaign**: Coordinated cyber offensive operations.
5. **Attack Technique**: MITRE ATT&CK Enterprise & AI Matrix sub-techniques.
6. **Vulnerability Class**: Memory corruption, race condition, auth bypass, injection.
7. **Security Control**: CIS Benchmarks, NIST SP 800-53 controls, zero-trust primitives.
8. **Vendor**: Technology providers & security vendors.
9. **Product**: Operating systems, servers, firmware, enterprise SaaS.
10. **Cloud Service**: AWS, Azure, GCP IAM and serverless components.
11. **Framework**: MITRE ATT&CK, NIST CSF 2.0, OWASP Top 10, ISO 27001.
12. **Research Paper**: Peer-reviewed cybersecurity and AI security literature.
13. **Incident**: Public post-mortems and historical breach records.
14. **Company**: Target enterprise profile models.
15. **Industry**: Financial, Healthcare, Critical Infrastructure (OT/ICS), Defense.
16. **Certification**: CompTIA, GIAC, Offensive Security, AWS, BTL1/2.
17. **Job Posting**: Market demand telemetry from public listings.
18. **CTF Challenge**: Practical hands-on training scenarios.
19. **Conference**: DEF CON, Black Hat, RSA, BSides, IEEE S&P.
20. **Course**: Academic syllabi and university cybersecurity programs.
21. **Security Tool**: Wireshark, Zeek, Wazuh, Ghidra, Volatility, Sigma.
22. **Technology Stack**: Linux, Windows, Kubernetes, eBPF, Active Directory.

## Relationship Types (12 Invariant Edges)
- `USES`: Adversary uses technique / tool
- `TARGETS`: Campaign targets industry / entity
- `EXPLOITS`: Attack technique exploits CVE
- `MITIGATES`: Security control mitigates technique
- `AFFECTS`: CVE affects software product / cloud service
- `RELATED_TO`: Conceptual correlation
- `DETECTED_BY`: Technique detected by security tool / Sigma rule
- `REPORTED_BY`: Intel reported by CISA / vendor / researcher
- `REQUIRES`: Skill / technology prerequisite dependency
- `TEACHES`: Lab / module teaches technique / principle
- `MAPS_TO`: Normalization mapping to standard taxonomy
- `PROVES`: Artifact proves demonstrated capability
