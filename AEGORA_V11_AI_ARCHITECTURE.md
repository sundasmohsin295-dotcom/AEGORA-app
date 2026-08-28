# AEGORA v11.0: AI AGENT ARCHITECTURE & SAFETY FRAMEWORK

## AI Agent Topology (17 Specialized Roles)

AEGORA v11.0 deploys a modular, least-privilege multi-agent architecture. Each agent operates within strictly defined behavioral boundaries, input sanitization rules, and output rubrics.

```
                                +---------------------------+
                                |  Untrusted Data Firewall  |
                                |  (Injection Quarantine)   |
                                +-------------+-------------+
                                              |
                     +------------------------+------------------------+
                     |                        |                        |
         +-----------v-----------++-----------v-----------++-----------v-----------+
         |     COGNITIVE HUB     | |     SIMULATION HUB    | |      CAREER HUB       |
         | - AI Tutor            | | - AI Simulation Dir   | | - AI Career Advisor   |
         | - AI Mentor           | | - AI SOC Coach        | | - AI Interviewer      |
         | - AI Learning Science | | - Living Adversary    | | - AI Project Advisor  |
         | - AI Communication    | | - AI Threat Intel     | | - AI Portfolio Review |
         +-----------------------+ +-----------------------+ +-----------------------+
                     |                        |                        |
         +-----------v-----------+            |            +-----------v-----------+
         |     EVALUATION HUB    |            |            |     CURATION HUB      |
         | - AI Assessor         |<-----------+----------->| - AI Content Curator  |
         | - AI Evidence Auditor |                         | - AI Cert Planner     |
         | - AI Quality Monitor  |                         +-----------------------+
         +-----------------------+
```

### Specialized Roles & Authorized Boundaries
1. **AI Tutor**: Foundational theory and Socratic mental models. Boundary: Cannot provide direct answers on active lab flags.
2. **AI Mentor**: Strategic long-term career planning. Boundary: Objective guidance based on verified market telemetry.
3. **AI Assessor**: Evidence-gated grading of student submissions. Boundary: Strict rubric adherence without grade inflation.
4. **AI Career Advisor**: Resume and job description gap analysis. Boundary: Transparent match percentages and shortest-path calculation.
5. **AI Simulation Director**: Controls scenario injection and branch evolution. Boundary: Sandboxed execution only.
6. **AI Research Assistant**: CVE and whitepaper synthesis. Boundary: Fact-checked citations from authoritative sources (NVD, CISA).
7. **AI Interviewer**: STAR behavioral and technical questioning. Boundary: Realistic pressure and objective feedback.
8. **AI Project Advisor**: Architectural code reviews for detection engineering. Boundary: Secure coding standards enforcement.
9. **AI Content Curator**: Vets external resources and tools. Boundary: Authenticity verification.
10. **AI Learning Scientist**: Optimizes Ebbinghaus retention curves. Boundary: Metacognitive awareness calibration.
11. **AI SOC Coach**: Real-time triage pacing and alert fatigue mitigation. Boundary: Standard Operating Procedure compliance.
12. **AI Threat Intelligence Analyst**: Maps attack artifacts to MITRE ATT&CK and Diamond models. Boundary: Accurate threat context.
13. **AI Portfolio Reviewer**: Audits GitHub READMEs and STAR reports. Boundary: Zero-fabrication validation.
14. **AI Communication Coach**: Eliminates unparsed technical jargon in executive briefings. Boundary: Plain-language clarity rubric.
15. **AI Certification Planner**: Maps syllabi and exam domains. Boundary: Objective credential value comparison.
16. **AI Evidence Auditor**: Validates cryptographic hashes and output signatures. Boundary: Strict provenance verification.
17. **AI Quality Evaluator**: Monitors responses for hallucinations and bias. Boundary: Automated self-correction filtering.

## Prompt Injection Defense & Untrusted Content Quarantine
All external threat intelligence feeds, raw user strings, and simulated adversary payloads pass through `UntrustedContentQuarantine`:
- High-entropy heuristic scanning for system prompt override attempts.
- Enforced read-only data isolation when processing external text feeds.
- Zero execution privileges for unparsed LLM inputs.
