# AEGORA v8.0 AI Multi-Agent Hierarchy

AEGORA v8.0 enforces a strict **13-agent specialized hierarchy**, preventing monolithic prompt hallucination and ensuring targeted cognitive assistance across every training domain.

### 1. Agent Specializations
1. **AI Learning Strategist**: Calculates optimal pedagogical intervention (active recall, interleaving, scaffolded drill).
2. **AI Cyber Debriefer**: Analyzes forensic telemetry after drills to compare learner actions vs ground truth adversary paths.
3. **AI Career Matcher**: Audits candidate skill passports against enterprise job descriptions and maps shortest evidence paths.
4. **AI Incident Coach**: Guides real-time alert triage during high-concurrency SOC shift simulations.
5. **AI Tutor**: Breaks down complex RFC protocol structures and mathematical cryptography primitives.
6. **AI Mentor**: Manages "Today's Mission" and the Next Best Action engine (1 primary + 3 optional).
7. **AI Assessor**: Evaluates investigation rigor, timeline correlation, and hypothesis falsification quality.
8. **AI Simulation Director**: Dynamically adjusts adversary TTP complexity based on learner weakness signals.
9. **AI Research Assistant**: Summarizes incoming CVE advisories into safe hands-on learning labs.
10. **AI Interviewer**: Conducts adaptive technical and scenario interviews for SOC, Red Team, and Cloud Sec roles.
11. **AI Project Advisor**: Generates realistic, unscripted cybersecurity capstone architectures.
12. **AI Content Curator**: Filters knowledge universe resources into top-3 highest-yield references.
13. **AI Voice Evaluator**: Scores spoken crisis communication for executive clarity, calmness, and jargon elimination.

### 2. Prompt Injection & AI Safety Isolation
External data (PCAP files, CVE advisories, user notes, CTF payload strings) is treated as strictly untrusted and wrapped in safety delimiters `<untrusted_telemetry>` before processing.
