# AEGORA v6.2 — Comprehensive System Architecture Blueprint

## System Architecture Overview

AEGORA is a zero-trust, evidence-grounded cybersecurity education and career acceleration platform built on modern Android Jetpack Compose and Kotlin. This document outlines the end-to-end component flow, separation of concerns, and architectural boundaries.

```
+-----------------------------------------------------------------------------------+
|                                  PRESENTATION LAYER                               |
|   +---------------------+  +----------------------+  +------------------------+   |
|   | Jetpack Compose UI  |  |   AegoraApp NavHost  |  | Adaptive Window Insets |   |
|   | (41 Screens & Views)|  | (Sealed Destination) |  |   (Mobile & Tablet)    |   |
|   +---------------------+  +----------------------+  +------------------------+   |
+------------------------------------------+----------------------------------------+
                                           | StateFlow / UI Events
+------------------------------------------v----------------------------------------+
|                                 DOMAIN & ENGINE LAYER                             |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  | Next Best Action      | | Living Skill           | | Observed Learning       | |
|  | Engine (Phase 8)      | | Constellation (3D)     | | Pattern (Mistake DNA)   | |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  | 6-Dim Skill Genome    | | 10-Tier Ladder         | | Multi-Modal Fusion      | |
|  | (Evidence Calibrated) | | (L0 to L9 Gateways)    | | Engine (Audio & Graph)  | |
|  +-----------------------+ +------------------------+ +-------------------------+ |
+------------------------------------------+----------------------------------------+
                                           |
+------------------------------------------v----------------------------------------+
|                                   DATA LAYER                                      |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  | AegoraRepository      | | CyberExpertEngineRepo  | | ResourceUniverseRepo    | |
|  | (Core App State)      | | (Diagnostic & Skills)  | | (Curated Knowledge)     | |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  | WorkplaceExpRepo      | | ZeroTrustSecurityRepo  | | GeminiMentorService     | |
|  | (Enterprise Sim)      | | (Auth & Session State) | | (Multimodal AI Client)  | |
|  +-----------------------+ +------------------------+ +-------------------------+ |
+------------------------------------------+----------------------------------------+
                                           |
+------------------------------------------v----------------------------------------+
|                            PERSISTENCE & SECURITY BOUNDARY                        |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  | Local Room Database   | | Android Keystore /     | | [BACKEND REQUIRED]      | |
|  | (Encrypted Tables)    | | EncryptedSharedPrefs   | | Remote FIDO2 Server     | |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  +-----------------------+ +------------------------+ +-------------------------+ |
|  | [BACKEND REQUIRED]    | | [BACKEND REQUIRED]     | | [BACKEND REQUIRED]      | |
|  | Firestore Real Rules  | | Argon2id Auth Service  | | Play Integrity Attest   | |
|  +-----------------------+ +------------------------+ +-------------------------+ |
+-----------------------------------------------------------------------------------+
```

---

## 1. UI Layer

The UI layer is built entirely in **Jetpack Compose** following Material 3 guidelines and a custom cybersecurity design system:
- **Visual Identity**: Chamfered cut corners (`ChamferedCutCornerShape`), hexagonal badge shapes (`HexagonShape`), dark slate canvas (`CyberBackground`, `CyberSurfaceElevated`), and high-contrast telemetry colors (`CyberCyan`, `CyberEmerald`, `NeonCrimson`, `CyberAmber`, `CyberViolet`).
- **Composables & Screens**: 41 modular screens and views organized by educational, diagnostic, workplace, and incident-response domains.
- **Adaptive Sizing**: Full support for Compact, Medium, and Expanded Window Size Classes via `BreakpointClass` and `AdaptiveNetworkBanner`.

---

## 2. Navigation Architecture

Navigation is managed through a type-safe sealed class hierarchy (`ScreenDestination`) rendered within `AegoraApp.kt`:
- **Core Hub Navigation**: 5-tab primary navigation bar (`AegoraNavTab.RADAR`, `JOURNEY`, `LABS`, `AI_MENTOR`, `PASSPORT`).
- **Direct Drill-Down**: Parameterized destinations including `ScreenDestination.LessonDetail(lessonId)` and `ScreenDestination.Quiz(quizId)`.
- **Specialized Workspaces**: `WorkplaceSimulator`, `LiveSocRange`, `MultiModalFusion`, `ThreatAcoustic`, `BinaryDisassembler`, `CyberTerminal`, `SecurityCenter`, and `KnowledgeVault`.
- **Universal Search**: Global command launcher (`UniversalSearchDialog`) allowing instant jump to any tool, simulator, or role track.

---

## 3. Presentation & State Management

- **Reactive State Flow**: All repositories expose immutably wrapped `StateFlow` and `asStateFlow()` instances.
- **Lifecycle Awareness**: View composables consume state via `collectAsState()` and `collectAsStateWithLifecycle()`.
- **Coroutines & Concurrency**: Asynchronous I/O, simulation ticks, and AI generation execute on `Dispatchers.IO` and `Dispatchers.Default`.

---

## 4. Domain & Intelligence Layer

1. **Next Best Action Engine**:
   - Calculates 1 Primary Action and 3 Optional Actions dynamically per session based on skill decay, current career targets, unscripted evidence gaps, and time availability.
2. **Multi-Dimensional Skill Genome**:
   - Scores skills across 8 separate vectors: Knowledge, Practical Ability, Hypothesis Reasoning, Transfer, Retention, Independence, Confidence Calibration, and Evidence Strength.
   - Strictly outputs `UNKNOWN` when empirical evidence is missing rather than inventing false proficiency scores.
3. **Cyber Twin (Observed Learning Patterns)**:
   - Aggregates observed telemetry without psychological diagnosis. Tracks fragile skills, decay vectors, verified artifacts, and independence level.
4. **Mistake DNA & Reasoning Graph**:
   - Captures evidence-testing trails during investigations: `Evidence Viewed -> Hypothesis -> Action -> Result -> Revised Hypothesis -> Final Conclusion`.
   - Flags neutral, constructive learning patterns such as *Premature Closure*, *Confirmation Bias Tendency*, *Tunnel Vision*, and *Isolated IOC Analysis*.
5. **Adaptive Learning Modes**:
   - Maps learner friction to targeted pedagogy: `EXPLAIN`, `VISUAL`, `PRACTICE`, `CASE_STUDY`, `SIMULATION`, `TEACH_BACK`, `RETRIEVAL`, `TRANSFER`, `INTERVIEW`, `EXAM`, and `RESEARCH`.

---

## 5. Data & Repository Layer

- **`AegoraRepository`**: Centralized in-memory and state coordinator for user profiles, roadmaps, lessons, quizzes, badges, and learning history.
- **`CyberExpertEngineRepository`**: Manages the 10-tier ladder (`L0_UNFAMILIAR` to `L9_RESEARCHER`), diagnostic prerequisite chains, and cognitive judgment scenarios.
- **`WorkplaceExperienceRepository`**: Orchestrates enterprise simulation, virtual enterprise profiles (`VirtualEnterprise`), incoming manager messages (`WorkplaceMessage`), workbench evidence, and consequence calculation.
- **`ResourceUniverseRepository`**: Curates authoritative books, academic whitepapers, standards (NIST, RFCs, ISO), and verified tools.
- **`ZeroTrustSecurityRepository`**: Manages identity state, biometric tokens, passkey records, active device sessions, and audit events.
- **`GeminiMentorService`**: Executes live Gemini API calls via OkHttp with fallback to a cybersecurity heuristic mentor.

---

## 6. AI Integration Architecture

- **Primary Client**: REST API integration to Google Gemini (`gemini-3.5-flash` / `gemini-1.5-flash`) via `OkHttpClient`.
- **Security Safeguards**:
  - Treats all external content and logs strictly as **DATA**, never instructions.
  - Implements prompt isolation and input sanitization.
  - AI recommendations pass through client-side validation gates before triggering application state changes.
  - Offline heuristic engine ensures full functionality without network dependency.

---

## 7. Security Architecture & Boundary Verification

All security mechanisms are classified in accordance with `SECURITY_STATUS.md`:
- **Client-Implemented**: Biometric Prompt integration, UI Step-Up dialogs, session inspection views, and local telemetry collectors.
- **Simulated / Mock**: Passkey FIDO2 challenge-response, Argon2id password hashing, server-side session revocation, and automated risk scoring.
- **Requires External Backend**: Remote WebAuthn Relying Party, Firebase Firestore real security rules, Play Integrity server verification, and immutable Merkle SIEM logging.

---

## 8. Persistence & Local Storage

- **Room Database Engine**: Pre-configured via `androidx.room` compiler (KSP) for structured SQLite storage.
- **State Serialization**: Local state snapshots and cross-device session simulations cached in memory and expandable to Room entities.
- **Widget Integration**: Glance AppWidget provider (`SkillDecayRadarWidget`) reading directly from skill decay state.
