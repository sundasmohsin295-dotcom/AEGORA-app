# AEGORA — Engineering & Production Hardening Roadmap

This roadmap details the engineering phases required to transition AEGORA's current interactive client and simulated zero-trust features to cryptographically hardened, production-grade cloud services.

---

## Strategic Milestone Overview

```
+-----------------------------------------------------------------------------------+
|  v6.2 (Current State)      |  v6.3 (Local Persistence)   |  v7.0 (Zero-Trust Cloud)   |
|  - 41 Functional Screens   |  - Room Offline DB Tables   |  - Real FIDO2 WebAuthn RP  |
|  - Multi-Modal Fusion      |  - Encrypted Keystore Prefs |  - Argon2id Auth Service   |
|  - 38 Career Roles         |  - WorkManager Sync Engine  |  - Play Integrity Attest   |
|  - 8-Dim Skill Genome      |  - Offline PCAP Parsers     |  - Merkle SIEM Audit Sync  |
+-----------------------------------------------------------------------------------+
```

---

## Phase 1: v6.3 — Local Hardening & Full Room Persistence (Next Sprint)

### 1.1 Local SQLite / Room Database Deployment
- **Goal**: Transition in-memory `StateFlow` registries to persistent on-device Room entities with Room KSP compiler.
- **Tasks**:
  - Implement `UserEntity`, `SkillGenomeEntity`, `InvestigationReasoningEntity`, and `SpacedRepetitionEntity`.
  - Add `AegoraDatabase` with schema versioning and database migration test suite.
  - Implement Room TypeConverters for JSON lists, enums, and timestamp coordinates.

### 1.2 Android Keystore Token Protection
- **Goal**: Secure all sensitive local state and simulated tokens using hardware-backed cryptographic keys.
- **Tasks**:
  - Replace in-memory token holding with `MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)` and `EncryptedSharedPreferences`.
  - Store learner signing keys inside the Android Hardware Security Module (HSM) / StrongBox Keymaster.

### 1.3 Local Network PCAP Engine
- **Goal**: Provide offline PCAP parsing without external web services.
- **Tasks**:
  - Integrate a lightweight Kotlin byte-parser for standard `.pcap` / `.pcapng` packet headers to parse Ethernet, IPv4/IPv6, TCP, UDP, and DNS frames on-device.

---

## Phase 2: v6.4 — Real Play Integrity & AI Gateway Hardening

### 2.1 Google Play Integrity Client Implementation
- **Goal**: Protect the app from emulator tampered binaries, rooted execution environments, and repackaged APKs.
- **Tasks**:
  - Add `com.google.android.play:integrity` dependency.
  - Request dynamic integrity tokens with cloud nonce during sensitive operations (Skill Passport signing, Capstone submissions).
  - Implement client-side error handling for unverified environments with prominent safety simulation fallback.

### 2.2 Streaming Gemini Multimodal Audio Integration
- **Goal**: Expand the Multi-Modal Fusion screen from recorded briefings to real-time interactive AI voice stream.
- **Tasks**:
  - Connect `GeminiMentorService` to WebRTC / WebSocket streaming endpoints for low-latency bidirectional voice coaching during live SOC triage.

---

## Phase 3: v7.0 — Production Zero-Trust Cloud Backend Integration

### 3.1 FIDO2 / WebAuthn Relying Party Service
- **Goal**: Upgrade `CyberAuthScreen` from simulated passkey enrollment to production FIDO2 registration and authentication.
- **Tasks**:
  - Integrate `androidx.credentials:credentials` and `androidx.credentials:credentials-play-services-auth`.
  - Deploy a cloud-hosted FIDO2 Relying Party (RP) verifying public key credentials, client data JSON hashes, and authenticator counter increments.

### 3.2 Argon2id Authentication & Central Session Service
- **Goal**: Replace in-memory password verification with an enterprise-grade auth microservice.
- **Tasks**:
  - Deploy backend API running **Argon2id** password hashing with cryptographically unique salts.
  - Implement JWT refresh token rotation with centralized Redis session revocation registry.

### 3.3 Production Firestore Rules Deployment
- **Goal**: Enforce strict server-side authorization on all cloud-synchronized user artifacts.
- **Tasks**:
  - Deploy verified `firestore.rules` enforcing granular document access:
    ```javascript
    rules_version = '2';
    service cloud.firestore {
      match /databases/{database}/documents {
        match /users/{userId} {
          allow read, write: if request.auth != null && request.auth.uid == userId;
        }
        match /evidence_vault/{evidenceId} {
          allow read: if request.auth != null;
          allow create: if request.auth != null && request.resource.data.authorUid == request.auth.uid;
        }
      }
    }
    ```

### 3.4 Cryptographically Chained SIEM Audit Logging
- **Goal**: Guarantee non-repudiation of all security-sensitive learner actions.
- **Tasks**:
  - Implement SHA-256 Merkle tree log aggregation on client.
  - Stream signed audit bundles to an immutable Write-Once-Read-Many (WORM) storage bucket or enterprise SIEM endpoint.
