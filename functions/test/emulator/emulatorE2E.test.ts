import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import * as crypto from 'crypto';
import { initializeApp, deleteApp, FirebaseApp } from 'firebase/app';
import {
  getAuth,
  connectAuthEmulator,
  signInWithCustomToken,
  signOut,
  UserCredential
} from 'firebase/auth';
import {
  getFunctions,
  connectFunctionsEmulator,
  httpsCallable
} from 'firebase/functions';
import {
  getFirestore,
  connectFirestoreEmulator,
  doc,
  getDoc,
  setDoc,
  Firestore
} from 'firebase/firestore';
import * as admin from 'firebase-admin';

describe('AEGORA Phase 3D: Real Firebase Emulators Integrated E2E Test', () => {
  const PROJECT_ID = 'aegora-adc1a';
  const USER_A = 'emulator_user_alpha_3d';
  const USER_B = 'emulator_user_bravo_3d';

  let clientApp: FirebaseApp;
  let clientAuth: ReturnType<typeof getAuth>;
  let clientFunctions: ReturnType<typeof getFunctions>;
  let clientDb: Firestore;
  let adminApp: admin.app.App;
  let adminDb: admin.firestore.Firestore;

  let userAToken: string;
  let userBToken: string;

  beforeAll(async () => {
    // 1. Initialize Admin SDK pointing to emulators
    process.env.FIRESTORE_EMULATOR_HOST = '127.0.0.1:8080';
    process.env.FIREBASE_AUTH_EMULATOR_HOST = '127.0.0.1:9099';

    if (!admin.apps.length) {
      adminApp = admin.initializeApp({
        projectId: PROJECT_ID
      });
    } else {
      adminApp = admin.apps[0]!;
    }
    adminDb = admin.firestore();

    // Generate Custom Auth Tokens for test identities via Admin SDK
    userAToken = await admin.auth().createCustomToken(USER_A);
    userBToken = await admin.auth().createCustomToken(USER_B);

    // 2. Initialize Client SDK configured to connect to emulators
    clientApp = initializeApp({
      apiKey: 'fake-api-key-for-emulator',
      projectId: PROJECT_ID,
      authDomain: `${PROJECT_ID}.firebaseapp.com`
    }, 'emulatorClientApp');

    clientAuth = getAuth(clientApp);
    connectAuthEmulator(clientAuth, 'http://127.0.0.1:9099', { disableWarnings: true });

    clientFunctions = getFunctions(clientApp, 'us-central1');
    connectFunctionsEmulator(clientFunctions, '127.0.0.1', 5001);

    clientDb = getFirestore(clientApp);
    connectFirestoreEmulator(clientDb, '127.0.0.1', 8080);
  });

  afterAll(async () => {
    if (clientAuth) {
      await signOut(clientAuth);
    }
    if (clientApp) {
      await deleteApp(clientApp);
    }
  });

  // 1. UNAUTHENTICATED ACCESS
  describe('1. Unauthenticated Callable Invocations', () => {
    beforeAll(async () => {
      await signOut(clientAuth);
    });

    it('rejects unauthenticated calls to all authoritative functions', { timeout: 30000 }, async () => {
      const endpoints = [
        'verifyAndIngestEvidence',
        'evaluateAuthoritativeCapability',
        'evaluateAuthoritativeMastery',
        'calculateAuthoritativeReadiness',
        'evaluateAndGrantCyberTreasure',
        'generateAuthoritativeNextMove',
        'evaluateFullLearnerState'
      ];

      for (const fnName of endpoints) {
        const fn = httpsCallable(clientFunctions, fnName);
        await expect(fn({})).rejects.toThrow();
      }
    });
  });

  // 2. AUTHENTICATED IDENTITY & CROSS-USER TAMPERING
  describe('2. Authenticated Identity & Cross-User Protection', () => {
    beforeAll(async () => {
      await signInWithCustomToken(clientAuth, userAToken);
    });

    it('verifies caller identity resolves to authenticated UID (USER_A)', () => {
      expect(clientAuth.currentUser?.uid).toBe(USER_A);
    });

    it('rejects cross-user targetAuthUid mismatch from USER_A targeting USER_B', async () => {
      const fn = httpsCallable(clientFunctions, 'evaluateAuthoritativeCapability');
      await expect(
        fn({
          skillKey: 'siem_log_triage',
          targetAuthUid: USER_B
        })
      ).rejects.toThrow(/Cross-user violation/);
    });
  });

  // 3. FULL EVIDENCE & CAPABILITY AUTHORITATIVE LIFECYCLE
  describe('3. Authoritative Evidence Ingestion & Capability Calculation', () => {
    const evidenceId = 'ev_real_emulator_01';
    const missionId = 'm_siem_soc_01';
    const attemptId = 'att_real_emulator_01';
    const skillKey = 'siem_log_triage';

    beforeAll(async () => {
      await signInWithCustomToken(clientAuth, userAToken);

      // Seed valid mission attempt in Firestore via Admin SDK
      await adminDb.doc(`learners/${USER_A}/mission_attempts/${attemptId}`).set({
        attemptId,
        missionId,
        ownerAuthUid: USER_A,
        status: 'IN_PROGRESS'
      });
    });

    it('rejects tampered evidence with invalid SHA-256 integrity digest', async () => {
      const rawPayload = 'exit_code: 0\nstatus: success\nartifact_hash: test123';
      const fakeDigest = 'sha256:0000000000000000000000000000000000000000000000000000000000000000';

      const fn = httpsCallable(clientFunctions, 'verifyAndIngestEvidence');
      const res: any = await fn({
        evidenceId: 'ev_tampered_3d',
        attemptId,
        missionId,
        skillKey,
        evidenceType: 'NETWORK_PCAP',
        payloadRaw: rawPayload,
        integrityDigest: fakeDigest
      });

      expect(res.data.serverVerificationState).toBe('REJECTED');
      expect(res.data.verified).toBe(false);
      expect(res.data.rejectionReason).toContain('SHA-256 payload tampering detected');
    });

    it('accepts valid evidence and server-authoritatively verifies it', async () => {
      const rawPayload = 'exit_code: 0\nstatus: success\nartifact_hash: deadbeef999';
      const rawString = `${USER_A}:${missionId}:${attemptId}:NETWORK_PCAP:${rawPayload}`;
      const validDigest = 'sha256:' + crypto.createHash('sha256').update(rawString).digest('hex');

      const fn = httpsCallable(clientFunctions, 'verifyAndIngestEvidence');
      const res: any = await fn({
        evidenceId,
        attemptId,
        missionId,
        skillKey,
        evidenceType: 'NETWORK_PCAP',
        payloadRaw: rawPayload,
        integrityDigest: validDigest
      });

      expect(res.data.serverVerificationState).toBe('VERIFIED');
      expect(res.data.verified).toBe(true);
      expect(res.data.authorityMetadata.authoritySource).toBe('SERVER');

      // Verify direct in real Firestore Emulator
      const savedDoc = await adminDb.doc(`learners/${USER_A}/evidence/${evidenceId}`).get();
      expect(savedDoc.exists).toBe(true);
      expect(savedDoc.data()?.serverVerificationState).toBe('VERIFIED');
      expect(savedDoc.data()?.verified).toBe(true);
    });

    it('derives authoritative capability from verified evidence in Firestore', async () => {
      const fn = httpsCallable(clientFunctions, 'evaluateAuthoritativeCapability');
      const res: any = await fn({ skillKey });

      expect(res.data.skillKey).toBe(skillKey);
      expect(res.data.isDemonstrated).toBe(true);
      expect(res.data.currentConfidence).toBeGreaterThanOrEqual(75);
      expect(res.data.authorityMetadata.authoritySource).toBe('SERVER');

      // Check real Firestore state
      const capDoc = await adminDb.doc(`learners/${USER_A}/capabilities/${skillKey}`).get();
      expect(capDoc.exists).toBe(true);
      expect(capDoc.data()?.isDemonstrated).toBe(true);
    });
  });

  // 4. MASTERY, READINESS, TREASURE & NEXT MOVE
  describe('4. Authoritative Mastery, Readiness, Cyber Treasure & NEXT MOVE', () => {
    beforeAll(async () => {
      await signInWithCustomToken(clientAuth, userAToken);
    });

    it('evaluates authoritative 7-gate mastery across cognitive clusters', async () => {
      const fn = httpsCallable(clientFunctions, 'evaluateAuthoritativeMastery');
      const res: any = await fn({});

      expect(res.data.ownerAuthUid).toBe(USER_A);
      expect(res.data.clusterMetrics.length).toBe(4);
      expect(res.data.authorityMetadata.authoritySource).toBe('SERVER');
    });

    it('calculates authoritative career readiness signal', async () => {
      const fn = httpsCallable(clientFunctions, 'calculateAuthoritativeReadiness');
      const res: any = await fn({ trackId: 'soc_analyst_t2' });

      expect(res.data.ownerAuthUid).toBe(USER_A);
      expect(res.data.targetRole).toContain('SOC Analyst');
      expect(res.data.readinessScore).toBeGreaterThan(0);
      expect(res.data.authorityMetadata.authoritySource).toBe('SERVER');
    });

    it('evaluates and grants Cyber Treasure only from verified evidence', async () => {
      const fn = httpsCallable(clientFunctions, 'evaluateAndGrantCyberTreasure');
      const res: any = await fn({
        treasureId: 'tr_pcap_master_3d',
        title: 'PCAP Master Beacon Hunter',
        category: 'Threat Hunting',
        evidenceId: 'ev_real_emulator_01'
      });

      expect(res.data.treasureId).toBe('tr_pcap_master_3d');
      expect(res.data.verifiedStatus).toBe('CRYPTOGRAPHICALLY_VERIFIED');
      expect(res.data.authorityMetadata.authoritySource).toBe('SERVER');

      // Check Firestore Emulator
      const trDoc = await adminDb.doc(`learners/${USER_A}/cyber_treasure/tr_pcap_master_3d`).get();
      expect(trDoc.exists).toBe(true);
      expect(trDoc.data()?.verifiedStatus).toBe('CRYPTOGRAPHICALLY_VERIFIED');
    });

    it('generates authoritative NEXT MOVE based on limiting gates', async () => {
      const fn = httpsCallable(clientFunctions, 'generateAuthoritativeNextMove');
      const res: any = await fn({});

      expect(res.data.ownerAuthUid).toBe(USER_A);
      expect(res.data.actionId).toBeDefined();
      expect(res.data.urgencyScore).toBeGreaterThan(0);
      expect(res.data.authorityMetadata.authoritySource).toBe('SERVER');
    });
  });

  // 5. IDEMPOTENCY & REPLAY ATTACK DEFENSE
  describe('5. Real Emulator Idempotency & Replay Resistance', () => {
    beforeAll(async () => {
      await signInWithCustomToken(clientAuth, userAToken);
    });

    it('safely handles 10 repeated submissions of the exact same evidence (idempotent)', async () => {
      const rawPayload = 'exit_code: 0\nstatus: success\nartifact_hash: deadbeef999';
      const rawString = `${USER_A}:m_siem_soc_01:att_real_emulator_01:NETWORK_PCAP:${rawPayload}`;
      const validDigest = 'sha256:' + crypto.createHash('sha256').update(rawString).digest('hex');

      const fn = httpsCallable(clientFunctions, 'verifyAndIngestEvidence');

      for (let i = 0; i < 10; i++) {
        const res: any = await fn({
          evidenceId: 'ev_real_emulator_01',
          attemptId: 'att_real_emulator_01',
          missionId: 'm_siem_soc_01',
          skillKey: 'siem_log_triage',
          evidenceType: 'NETWORK_PCAP',
          payloadRaw: rawPayload,
          integrityDigest: validDigest
        });

        expect(res.data.serverVerificationState).toBe('VERIFIED');
        expect(res.data.evidenceId).toBe('ev_real_emulator_01');
      }

      // Check that only ONE evidence record exists in Firestore
      const snapshot = await adminDb.collection(`learners/${USER_A}/evidence`)
        .where('evidenceId', '==', 'ev_real_emulator_01')
        .get();
      expect(snapshot.size).toBe(1);
    });

    it('prevents replay attack from creating duplicate Cyber Treasure', async () => {
      const fn = httpsCallable(clientFunctions, 'evaluateAndGrantCyberTreasure');

      for (let i = 0; i < 5; i++) {
        const res: any = await fn({
          treasureId: 'tr_pcap_master_3d',
          title: 'PCAP Master Beacon Hunter',
          category: 'Threat Hunting',
          evidenceId: 'ev_real_emulator_01'
        });
        expect(res.data.treasureId).toBe('tr_pcap_master_3d');
      }

      const snapshot = await adminDb.collection(`learners/${USER_A}/cyber_treasure`)
        .where('treasureId', '==', 'tr_pcap_master_3d')
        .get();
      expect(snapshot.size).toBe(1);
    });
  });
});
