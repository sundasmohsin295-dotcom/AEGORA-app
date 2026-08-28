# AEGORA v6.2 — Core Domain & Data Model Specification

This document details the complete domain model architecture representing the 28 core AEGORA entities across educational, diagnostic, investigative, and career progression domains.

---

## 1. Domain Entity Relationship Diagram

```
+------------------------------------------------------------------------------------+
|                                    USER PROFILE                                    |
|   +-------------------+       +-----------------------+       +----------------+   |
|   |    UserProfile    | <---> |   PersonalCyberTwin   | <---> |  SkillPassport |   |
|   +-------------------+       +-----------------------+       +----------------+   |
+-------------+-----------------------------+---------------------------+------------+
              |                             |                           |
              v                             v                           v
+-------------+-------------+ +-------------+-------------+ +-----------+------------+
|        CAREER DOMAIN      | |     SKILL & LEARNING      | |   EVIDENCE & PORTFOLIO |
|  - DetailedCareerRole     | |  - SkillGenome (8-Dim)    | |  - WorkbenchEvidence   |
|  - RoleFamily (6 Families)| |  - RoadmapTopic & Lesson  | |  - InvestigationTrail  |
|  - TargetCertification    | |  - InteractiveSocIncident | |  - CyberProject        |
|  - IndustryJobRequirement | |  - SpacedRepetitionItem   | |  - VerifiedSkillBadge  |
+---------------------------+ +---------------------------+ +------------------------+
              |                             |                           |
              +-----------------------------+---------------------------+
                                            |
                                            v
+-------------------------------------------+----------------------------------------+
|                               INVESTIGATION & TELEMETRY                            |
|  - InvestigationReasoningStep             - ObservedMistakePattern                 |
|  - TopicHistoricalMemory                  - SecurityAuditEvent                     |
|  - VirtualEnterprise                      - FusionEvidenceNode                     |
+------------------------------------------------------------------------------------+
```

---

## 2. Core Entity Definitions

### 1. User & Identity
- **`UserProfile`** (`CyberModels.kt`): Core learner profile storing callsign, XP, target career, skill level, daily commitment, streak count, and job readiness score.
- **`ZeroTrustIdentityState`** (`ZeroTrustAuthModels.kt`): Real-time session state, active FIDO2 passkeys, registered devices, and risk telemetry score.

### 2. Cyber Twin & Learning Patterns
- **`PersonalCyberTwin`** (`ExpertiseEngineModels.kt`): Holistic profile tracking 6-dimension capability scores, prerequisite chains, and high-value daily missions.
- **`ObservedLearningPatternSummary`** (`CoreArchitectureModels.kt`): Objective record of strengths, fragile skills, decay vectors, and verified evidence artifacts without clinical or psychological claims.

### 3. Multi-Dimensional Skill Genome
- **`SkillGenome`** (`CoreArchitectureModels.kt`): 8-vector evaluation structure containing Knowledge, Practical Ability, Reasoning, Transfer, Retention, Independence, Confidence Calibration, and Evidence Strength.
- **`DimensionalSkillMetric`**: Stores integer score or explicit `UNKNOWN` state to prevent hallucinated capability metrics.

### 4. Career Architecture & Role Registry
- **`DetailedCareerRole`** (`CoreArchitectureModels.kt`): Complete role registry entity containing identity, difficulty, responsibilities, tools, frameworks, learning paths, labs, capstones, certifications, and interview rubrics.
- **`RoleFamily`**: Enum supporting **Blue Team**, **Red Team**, **Security Engineering**, **GRC**, **Leadership**, and **Specialized**.

### 5. Learning Paths, Lessons & Activities
- **`RoadmapPhase` & `RoadmapTopic`** (`CyberModels.kt`): Structured curriculum tracks with estimated study hours, practical labs, and completion flags.
- **`CyberLesson` & `LessonContent`** (`CyberModels.kt`): Deep instructional modules with theory, packet diagrams, and interactive terminal challenges.
- **`AdaptiveLearningMode`** (`CoreArchitectureModels.kt`): 11 targeted pedagogy modes triggered by specific learner weakness (Explain, Visual, Practice, Case Study, Simulation, Teach-Back, Retrieval, Transfer, Interview, Exam, Research).

### 6. Labs, Scenarios & Workplace Simulators
- **`InteractiveSocIncident`** (`CyberModels.kt`): Live SOC incident containing raw logs, IOCs, alarm severities, MITRE tactic mapping, and multi-step investigation questions.
- **`VirtualEnterprise`** (`WorkplaceSimulatorModels.kt`): Profile of the simulated enterprise (e.g., Aegora Financial) including risk score, threat level, and critical server assets.
- **`WorkplaceTicket` & `WorkplaceMessage`**: Enterprise communication streams requiring analyst triage, response selection, and consequence execution.

### 7. Investigations, Evidence & Reasoning Graphs
- **`WorkbenchEvidence`** (`WorkplaceSimulatorModels.kt`): Raw SIEM/EDR/PCAP indicators with confidence ratings, examination states, and hypothesis pins.
- **`InvestigationReasoningTrail` & `InvestigationReasoningStep`** (`CoreArchitectureModels.kt`): Step-by-step reasoning ledger documenting Evidence Viewed -> Formed Hypothesis -> Executed Action -> Observed Result -> Revised Hypothesis.
- **`FusionEvidenceNode`** (`LivingIntelligenceModels.kt`): Multimodal nodes synchronized across visual topology graphs and audio narration timestamps.

### 8. Mistake DNA & Cognitive Telemetry
- **`ObservedMistakePattern`** (`CoreArchitectureModels.kt`): Formally cataloged learning tendencies (Premature Closure, Confirmation Bias, Tunnel Vision, Weak Prioritization, Isolated IOC Analysis, Tool Fixation, Over/Under-confidence).
- **`TopicHistoricalMemory`** (`LivingIntelligenceModels.kt`): Preserves prior attempt scores, noted mistakes, and proactive coaching advice across learning sessions.

### 9. Knowledge, Research & Intelligence
- **`ResourceKnowledgeItem`** (`ResourceKnowledgeModels.kt`): Peer-reviewed whitepapers, NIST standards, RFCs, and books evaluated by authority, practical value, and recency scores.
- **`LiveCyberIntelReport`** (`CyberModels.kt`): Source-backed real-time threat advisories with affected technology breakdowns and career relevance notes.

### 10. Verification, Credentials & Security
- **`SkillPassportCredential`** (`CyberModels.kt`): Cryptographically hashed competency record linking verified lab completion, PCAP submissions, and capstone scores.
- **`SecurityAuditEvent`** (`ZeroTrustAuthModels.kt`): Time-stamped security ledger recording login attempts, MFA challenges, session terminations, and risk elevation events.
