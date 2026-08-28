# AEGORA v6.3 — Production Release & Verification Checklist

This checklist must be fully executed and verified before tagging and deploying any release build.

---

## 1. Pre-Flight Release Gates

| Category | Verification Item | Verification Command / Method | Pass Criteria | Status |
| :--- | :--- | :--- | :--- | :---: |
| **1. Code & Build** | Full Gradle compilation | `gradle assembleDebug` / `compile_applet` | Build Succeeded with 0 errors | **PASS** |
| **2. Unit Tests** | Core unit & logic tests | `gradle testDebugUnitTest` | 100% tests green | **PASS** |
| **3. Security Annotations** | Mock security warnings | Grep for `// MOCK - NOT PRODUCTION SECURITY` | All simulated security screens tagged | **PASS** |
| **4. Secrets Scan** | Hardcoded API keys / tokens | Static analysis search for API keys | Zero hardcoded private keys | **PASS** |
| **5. Permissions** | AndroidManifest check | Inspect `AndroidManifest.xml` | Only `INTERNET` & `ACCESS_NETWORK_STATE` | **PASS** |
| **6. Offline Fallback** | Airplane mode test | Run app with network disabled | Heuristic mentor & local lessons work | **PASS** |
| **7. Navigation & Backstack**| Deep-link & back press | Navigate through all 41 screens | Zero navigation stack crashes | **PASS** |
| **8. Memory & Canvas** | 3D Constellation stress | Rapid tab switching on Skill Graph | Zero OutOfMemoryError / leaks | **PASS** |
| **9. Accessibility** | TalkBack content descriptions | Manual TalkBack & semantics inspection | All icons & action buttons labeled | **PASS** |
| **10. Privacy & Legal** | Disclaimer presence | Verify disclaimers on lab screens | Educational disclaimers displayed | **PASS** |

---

## 2. Release Rollback Protocol

In the event of an unpredicted critical crash, data corruption, or security defect discovered post-release:

1. **Phase 1 — Immediate Halt**:
   - Halt Play Store rollout immediately from Google Play Console (Halt phased rollout).
2. **Phase 2 — Root Cause Identification**:
   - Analyze crash traces from Play Console and Crashlytics logs to pinpoint the offending commit.
3. **Phase 3 — Fast-Path Patch or Hotfix**:
   - If an immediate fix is available: Increment patch version (e.g. `v6.3.1`), execute the release checklist, and submit an emergency hotfix build.
4. **Phase 4 — Rollback to Prior Stable**:
   - If investigation requires more than 4 hours: Re-publish previous stable release bundle (`v6.2.x`) with updated version code to protect user base.
5. **Phase 5 — Post-Mortem Documentation**:
   - Draft an internal incident post-mortem documenting root cause, detection time, remediation time, and test cases added to prevent recurrence.
