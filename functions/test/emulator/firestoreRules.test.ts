import { describe, it, expect, beforeAll, afterAll, beforeEach } from 'vitest';
import {
  initializeTestEnvironment,
  RulesTestEnvironment,
  assertFails,
  assertSucceeds
} from '@firebase/rules-unit-testing';
import * as fs from 'fs';
import * as path from 'path';

// Emulators test suite for Firestore Security Rules execution against real Firestore Emulator
describe('AEGORA Phase 3D: Firestore Security Rules on Real Emulator', () => {
  let testEnv: RulesTestEnvironment;

  const PROJECT_ID = 'aegora-adc1a';
  const USER_A = 'firebase_user_alpha_111';
  const USER_B = 'firebase_user_bravo_222';

  beforeAll(async () => {
    const rulesPath = path.resolve(__dirname, '../../../firestore.rules');
    const rules = fs.readFileSync(rulesPath, 'utf8');

    testEnv = await initializeTestEnvironment({
      projectId: PROJECT_ID,
      firestore: {
        host: '127.0.0.1',
        port: 8080,
        rules
      }
    });
  });

  afterAll(async () => {
    if (testEnv) {
      await testEnv.cleanup();
    }
  });

  beforeEach(async () => {
    if (testEnv) {
      await testEnv.clearFirestore();
    }
  });

  // 1. UNAUTHENTICATED ACCESS
  describe('1. Unauthenticated Access Protection', () => {
    it('blocks unauthenticated read from any learner collection', async () => {
      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertFails(unauthDb.doc(`learners/${USER_A}`).get());
      await assertFails(unauthDb.doc(`learners/${USER_A}/evidence/ev_01`).get());
      await assertFails(unauthDb.doc(`learners/${USER_A}/capabilities/cap_01`).get());
    });

    it('blocks unauthenticated write to any collection', async () => {
      const unauthDb = testEnv.unauthenticatedContext().firestore();
      await assertFails(unauthDb.doc(`learners/${USER_A}`).set({ test: true }));
      await assertFails(unauthDb.doc(`learners/${USER_A}/evidence/ev_01`).set({ test: true }));
    });
  });

  // 2. CROSS-USER ISOLATION
  describe('2. Cross-User Boundary (User A vs User B)', () => {
    it('blocks User A from reading User B data', async () => {
      // Seed User B data using admin context
      await testEnv.withSecurityRulesDisabled(async (adminContext) => {
        await adminContext.firestore().doc(`learners/${USER_B}`).set({
          firebaseAuthUid: USER_B,
          ownerAuthUid: USER_B,
          displayName: 'Operator Bravo'
        });
        await adminContext.firestore().doc(`learners/${USER_B}/evidence/ev_b1`).set({
          evidenceId: 'ev_b1',
          ownerAuthUid: USER_B,
          verified: true
        });
      });

      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      await assertFails(userADb.doc(`learners/${USER_B}`).get());
      await assertFails(userADb.doc(`learners/${USER_B}/evidence/ev_b1`).get());
      await assertFails(userADb.doc(`learners/${USER_B}/capabilities/cap_01`).get());
      await assertFails(userADb.doc(`learners/${USER_B}/mastery_assessments/m_01`).get());
      await assertFails(userADb.doc(`learners/${USER_B}/cyber_treasure/tr_01`).get());
    });

    it('blocks User A from writing to User B paths', async () => {
      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      await assertFails(
        userADb.doc(`learners/${USER_B}/evidence/ev_forge`).set({
          evidenceId: 'ev_forge',
          ownerAuthUid: USER_B,
          serverVerificationState: 'PENDING_VERIFICATION',
          verified: false
        })
      );
      await assertFails(
        userADb.doc(`learners/${USER_B}`).set({
          firebaseAuthUid: USER_B,
          ownerAuthUid: USER_A
        })
      );
    });
  });

  // 3. CLIENT PRIVILEGE ESCALATION BLOCK
  describe('3. Client Privilege Escalation Prevention', () => {
    it('blocks direct client write to capabilities', async () => {
      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      await assertFails(
        userADb.doc(`learners/${USER_A}/capabilities/siem_log_triage`).set({
          skillKey: 'siem_log_triage',
          isDemonstrated: true,
          currentConfidence: 100
        })
      );
    });

    it('blocks direct client write to mastery_assessments', async () => {
      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      await assertFails(
        userADb.doc(`learners/${USER_A}/mastery_assessments/asm_01`).set({
          assessmentId: 'asm_01',
          mastered: true,
          status: 'VERIFIED'
        })
      );
    });

    it('blocks direct client write to cyber_treasure', async () => {
      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      await assertFails(
        userADb.doc(`learners/${USER_A}/cyber_treasure/tr_01`).set({
          treasureId: 'tr_01',
          verifiedStatus: 'CRYPTOGRAPHICALLY_VERIFIED',
          grant: true
        })
      );
    });

    it('blocks direct client create to next_actions', async () => {
      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      await assertFails(
        userADb.doc(`learners/${USER_A}/next_actions/act_01`).set({
          actionId: 'act_01',
          title: 'Forged Next Action'
        })
      );
    });

    it('blocks client from self-verifying evidence (must be PENDING_VERIFICATION and verified=false)', async () => {
      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      // Malicious attempt to self-verify:
      await assertFails(
        userADb.doc(`learners/${USER_A}/evidence/ev_forged_verified`).set({
          evidenceId: 'ev_forged_verified',
          ownerAuthUid: USER_A,
          serverVerificationState: 'VERIFIED',
          verified: true
        })
      );
    });

    it('allows client to append legitimate PENDING_VERIFICATION evidence with verified=false', async () => {
      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      await assertSucceeds(
        userADb.doc(`learners/${USER_A}/evidence/ev_valid_pending`).set({
          evidenceId: 'ev_valid_pending',
          ownerAuthUid: USER_A,
          serverVerificationState: 'PENDING_VERIFICATION',
          verified: false,
          integrityDigest: 'sha256:abc123',
          submittedAt: new Date().toISOString()
        })
      );
    });
  });

  // 4. OWNER PROFILE IMMUTABILITY
  describe('4. Owner Profile Guard', () => {
    it('prevents user from modifying immutable profile security keys', async () => {
      // Seed initial profile
      const userADb = testEnv.authenticatedContext(USER_A).firestore();
      await assertSucceeds(
        userADb.doc(`learners/${USER_A}`).set({
          firebaseAuthUid: USER_A,
          ownerAuthUid: USER_A,
          displayName: 'Alpha One',
          reputationScore: 50,
          createdAt: new Date().toISOString()
        })
      );

      // Attempt to tamper with reputationScore
      await assertFails(
        userADb.doc(`learners/${USER_A}`).update({
          reputationScore: 9999
        })
      );

      // Attempt to tamper with firebaseAuthUid
      await assertFails(
        userADb.doc(`learners/${USER_A}`).update({
          firebaseAuthUid: 'some_other_uid'
        })
      );
    });
  });
});
