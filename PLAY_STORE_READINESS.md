# AEGORA v6.3 — Google Play Store Readiness & Compliance

This specification audits all Google Play Store policies, Android requirements, store listing assets, and Data Safety disclosures.

---

## 1. Technical & Policy Checklist

| Item | Requirement | App State | Compliance Status |
| :--- | :--- | :--- | :---: |
| **Application ID** | Unique package identifier | `com.aistudio.cyberguard.pzkq` | **PASS** |
| **App Name** | Launcher label matching strings.xml | "AEGORA" | **PASS** |
| **Target SDK Version** | Target Android 14/15 (API 34+) | `targetSdk = 34`, `compileSdk = 34` | **PASS** |
| **64-bit Architecture** | Support 64-bit ABIs (arm64-v8a, x86_64) | Default Gradle NDK build targets | **PASS** |
| **App Bundle (AAB)** | Android App Bundle format for release | `assembleRelease` / `bundleRelease` configured | **PASS** |
| **Permission Minimization** | Request only essential permissions | Exactly 2 permissions: `INTERNET`, `ACCESS_NETWORK_STATE` | **PASS** |
| **Data Safety Section** | Accurate disclosure of all data collected | No personal data shared; zero ad trackers | **PASS** |
| **Content Rating** | IARC Questionnaire readiness | Rated Teen (13+) / Everyone 10+ (Educational) | **PASS** |
| **Account Deletion Link** | In-app and web-based account deletion | Local one-tap data wipe + web deletion portal | **PASS** |
| **Adaptive Launcher Icon** | Custom foreground vector with safe-zone | Custom cyber shield vector configured | **PASS** |

---

## 2. Android Permission Minimization Audit

| Permission | Technical Reason | User-Facing Benefit | Security & Privacy Risk | Alternative Evaluated | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| `android.permission.INTERNET` | Connect to Gemini AI API and live threat feeds | Enables AI mentor tutoring, dynamic scenario generation, and real-time CVE news | Low (Sandboxed network communication) | Offline-only heuristic fallback (Available when offline) | **KEPT (Essential)** |
| `android.permission.ACCESS_NETWORK_STATE` | Listen to connectivity changes via ConnectivityManager | Displays real-time offline warning banner and pauses background sync | None (Read-only network capability flag) | Polling network sockets (Consumes battery) | **KEPT (Essential)** |

*All dangerous permissions (Camera, Microphone, Precise Location, Read Contacts, Storage) have been intentionally omitted to ensure absolute device privacy.*

---

## 3. Store Listing Copy & Asset Blueprint

### App Title
**AEGORA — Cyber Learning Universe**

### Short Description (80 characters max)
*Master cybersecurity with 3D skill graphs, live SOC labs, and AI mentorship.*

### Full Description
Step into the AEGORA Cyber Universe — an evidence-grounded cybersecurity education and career acceleration platform designed for aspiring defenders, penetration testers, security engineers, and leaders.

**Core Capabilities**:
- **3D Living Skill Constellation**: Explore your personalized skill galaxy with real-time orbital depth, glow intensity, and retention decay rings.
- **Hands-on Cyber Workspaces**: Triage alerts in the Live SOC Range, analyze PCAPs, explore binary disassemblies, and investigate incidents in the Workplace Simulator.
- **Multi-Modal AI Mentorship**: Receive context-aware coaching, Socratic troubleshooting hints, and 4-point rubric scorecards grounded in verified evidence.
- **38 Specialized Career Roles**: Align your learning journey with Blue Team, Red Team, Security Engineering, GRC, Leadership, and AI Security career paths.
- **Offline-First Resilience**: Continue studying core lessons, reviewing flashcards, and practicing terminal commands even without an active internet connection.

### Screenshot Asset Specifications
1. **Screen 1 (Home Dashboard)**: "Next Best Action Engine & Observed Learning Telemetry"
2. **Screen 2 (Skill Graph)**: "Interactive 3D Living Skill Constellation"
3. **Screen 3 (Live SOC Range)**: "Real-time SIEM Alert Triage & Incident Investigation"
4. **Screen 4 (Multi-Modal Fusion)**: "Synchronized Incident Audio & Network Topology Graph"
5. **Screen 5 (Career Center)**: "38 Specialized Cybersecurity Roles & Capstone Roadmaps"
6. **Screen 6 (Security Center)**: "Transparent Zero-Trust Security Posture & Identity Controls"
