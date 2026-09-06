# AEGORA // PLATFORM v1.4 UNIFIED ANDROID + WEB ARCHITECTURE REPORT

## EXECUTIVE ARCHITECTURAL SUMMARY
AEGORA has successfully transitioned from an Android-isolated implementation to a unified **multi-client cybersecurity capability operating system**. Both Android and Web clients operate on the identical foundational principles:

> **One Identity → One Learner → One Capability Graph → Real Missions → Cryptographic Evidence → Career Intelligence**

---

## 1. CANONICAL IDENTITY & SECURITY BOUNDARIES

### A. Deterministic Cross-Platform Mapping
Arbitrary strings passed by client UI components are never accepted as authoritative learner identifiers.
- Android (`com.example.platform.CanonicalAegoraIdentity`) and Web (`web/src/core/auth/CrossPlatformAuthClient.ts`) implement identical resolution:
  - Input: `providerUid`, `provider`, `email`
  - Sanitization: Alphanumeric and underscores only, length capped at 16, lowercased.
  - `canonicalUserId = "usr_" + sanitizedUid`
  - `canonicalLearnerId = "operator_" + sanitizedUid`

### B. Client UI Domain Guard
- Client UI code is never trusted to specify a target learner ID directly.
- Calls attempting to read, update, or claim capabilities for another learner ID are rejected with an explicit `Authorization Violation` error.

### C. Phase 1 & 1C Authentication Integrity
- External Identity Provider configuration (`google-services.json` / Web Firebase config) remains unconfigured in this container environment.
- Status is strictly and honestly maintained as **AUTH BACKEND BLOCKED**.
- No mock credentials, fake passwords, simulated Firebase tokens, or synthetic authentication booleans are used.
- Architectural transparency is enforced via visible badges and modals on both Android and Web clients.

---

## 2. CROSS-PLATFORM STATE CONTINUITY
- `CrossPlatformContinuityManager` tracks multi-client operational sessions.
- State snapshots guarantee that when an operator moves between Android and Web, the session handover maintains:
  - Canonical Learner Identity
  - Current Active Mission & Step
  - Verified Evidence Ledger
  - Cyber Twin Capability Scores
  - Timestamp of state synchronization

---

## 3. AUTHORITATIVE COGNITIVE CLUSTERS (CYBER TWIN 6.0)
Both platforms evaluate operators against the 4 core cognitive clusters:
1. **Foundation (Weight: 0.25)**: Protocol architecture, network internals, theoretical models.
2. **Active Defense (Weight: 0.30)**: SIEM triage, detection engineering, containment protocols.
3. **Generalization & Stress (Weight: 0.25)**: Cross-telemetry transferability, adversarial noise resistance.
4. **Metacognitive & Strategic (Weight: 0.20)**: Confidence calibration, mistake DNA resistance, long-term retention.

---

## 4. SOC RANGE MISSION ENGINE: "INVESTIGATE A SUSPICIOUS LOGIN"
Both platforms provide identical triage mechanics:
- **Telemetry Ingestion**: Windows Event Logs (Event ID 4625 brute-force failures, Event ID 4624 successes).
- **Anomaly Detection**: Impossible travel velocity alert (Moscow, RU IP `198.51.100.12` vs. Austin, TX IP `73.189.44.10` in 6 min 30 sec).
- **Decision Triage**: 3 sequential containment decisions.
- **Operator Reasoning Requirement**: Mandatory technical justification (minimum 15 characters).
- **Evidence Verification**: Passing operators (≥70%) earn an immutable SHA-256 evidence hash.

---

## 5. WEB CLIENT IMPLEMENTATION OVERVIEW (`/web`)
- **Framework**: React 19 + TypeScript 5.8 + Vite 8.2 + Vitest.
- **Design System**: Strict cybersecurity operations UI, restrained high-contrast palette, technical typography (`JetBrains Mono` + `Plus Jakarta Sans`).
- **Responsive Views**:
  - `LandingView`: Serious cybersecurity product entry ("Learn. Operate. Prove. Become Job Ready.").
  - `CommandCenterView`: Operator HUD, Dominant NEXT MOVE Hero, Cyber Twin 6.0, Cyber Treasure, Career Signal.
  - `MissionInvestigationView`: Live SOC range with raw log inspector and 3-step decision triage.
  - `CareerPassportView`: Evidence-backed capability ledger.
  - `SecurityStatusModal`: Transparent disclosure of `AUTH BACKEND BLOCKED` and zero-trust boundaries.
- **Light & Dark Mode**: Full WCAG-compliant theme system with toggle.

---

## 6. VERIFICATION & TEST SUITE METRICS

| Platform | Test Suite | Tests Run | Result |
| :--- | :--- | :--- | :--- |
| **Android** | `CrossPlatformIdentityAndContinuityTest` | 7 | **PASSED (0 failures)** |
| **Android** | Full Suite (`testDebugUnitTest`) | 125 | **PASSED (0 failures)** |
| **Web** | Vitest (`platform.test.ts`) | 5 | **PASSED (0 failures)** |
| **Web** | TypeScript Typecheck (`tsc --noEmit`) | Full repo | **PASSED (0 errors)** |
| **Web** | Production Vite Bundle (`vite build`) | 1,843 modules | **PASSED (395ms)** |
