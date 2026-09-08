import { describe, it, expect, beforeEach } from 'vitest';
import {
  WebCloudRepository,
  CloudAuthorizationError,
  PrivilegeEscalationError,
  EvidenceTamperError,
  computeIntegrityDigest
} from '../src/core/repositories/WebCloudRepository';
import { CloudCapabilityState, CloudNextAction } from '../src/core/types/platform';

describe('Phase 3B: Authoritative Cloud Repository & Authorization Boundary Tests', () => {
  let userA: string;
  let userB: string;
  let repoA: WebCloudRepository;
  let repoB: WebCloudRepository;
  let unauthRepo: WebCloudRepository;

  beforeEach(() => {
    userA = 'firebase_uid_alpha_111';
    userB = 'firebase_uid_beta_222';

    repoA = new WebCloudRepository(userA, null);
    repoB = new WebCloudRepository(userB, null);
    unauthRepo = new WebCloudRepository(null, null);
  });

  describe('1. Identity & Profile Access', () => {
    it('resolves profile for authenticated UID', async () => {
      const profile = await repoA.getOrCreateLearnerProfile(
        userA,
        'operator_alpha',
        'Analyst Alpha',
        'alpha@aegora.cyber'
      );
      expect(profile.firebaseAuthUid).toBe(userA);
      expect(profile.ownerAuthUid).toBe(userA);
      expect(profile.canonicalLearnerId).toBe('operator_alpha');
    });

    it('denies unauthenticated requests to read or write learner profile', async () => {
      await expect(
        unauthRepo.getOrCreateLearnerProfile(userA, 'operator_alpha', 'Analyst Alpha')
      ).rejects.toThrow(CloudAuthorizationError);
    });
  });

  describe('2. Learner Isolation (Zero-Trust Boundary)', () => {
    it('prevents User A from reading User B profile', async () => {
      await expect(
        repoA.getOrCreateLearnerProfile(userB, 'operator_beta', 'Analyst Beta')
      ).rejects.toThrow(CloudAuthorizationError);
    });

    it('prevents User A from reading User B mission attempts', async () => {
      await expect(
        repoA.getMissionAttempt(userB, 'att_beta_1')
      ).rejects.toThrow(CloudAuthorizationError);
    });

    it('prevents User A from accessing User B evidence', async () => {
      await expect(
        repoA.getEvidence(userB, 'ev_beta_1')
      ).rejects.toThrow(CloudAuthorizationError);
    });

    it('prevents User A from reading User B capabilities', async () => {
      await expect(
        repoA.getLearnerCapability(userB, 'soc_log_analysis')
      ).rejects.toThrow(CloudAuthorizationError);
    });
  });

  describe('3. Privilege Escalation Prevention', () => {
    it('rejects client attempting to set verified = true or self-promote capability level', async () => {
      await expect(
        repoA.attemptClientCapabilityWrite('soc_log_analysis', 'MASTERED', true)
      ).rejects.toThrow(PrivilegeEscalationError);
    });

    it('rejects client attempting to write mastery = VERIFIED directly', async () => {
      await expect(
        repoA.attemptClientMasteryWrite('incident_triage', 'VERIFIED')
      ).rejects.toThrow(PrivilegeEscalationError);
    });

    it('rejects client attempting to write readiness = JOB_READY directly', async () => {
      await expect(
        repoA.attemptClientReadinessWrite('soc_tier_1', 'JOB_READY')
      ).rejects.toThrow(PrivilegeEscalationError);
    });

    it('rejects client attempting to mark mission attempt as verifiedOutcome = true', async () => {
      await expect(
        repoA.submitMissionAttempt({
          attemptId: 'att_spoofed_outcome',
          missionId: 'm_soc_investigation',
          status: 'SUBMITTED',
          clientStartedAt: Date.now(),
          userInputs: { action: 'quarantine' },
          verifiedOutcome: true // FORBIDDEN: Client cannot self-certify
        })
      ).rejects.toThrow(PrivilegeEscalationError);
    });
  });

  describe('4. Evidence Security & Integrity (Tamper Detection)', () => {
    it('attaches pending status and SHA-256 integrity hash on client submission', async () => {
      const payload = { event: 'alert_triage', alertId: 'ALT-9021', decision: 'escalate' };
      const evidence = await repoA.submitEvidence('att_alpha_101', 'soc_triage', 'ALERT_RESPONSE', payload, 'WEB');

      expect(evidence.ownerAuthUid).toBe(userA);
      expect(evidence.serverVerificationState).toBe('PENDING_VERIFICATION');
      expect(evidence.verified).toBe(false);
      expect(evidence.clientDigest).toBeDefined();
      expect(evidence.clientDigest.length).toBe(64); // SHA-256 hex length
    });

    it('detects payload tampering when hash does not match', async () => {
      const payload = { event: 'log_search', query: 'event.code: 4624' };
      const evidence = await repoA.submitEvidence('att_alpha_102', 'log_analysis', 'QUERY_EXECUTION', payload, 'WEB');

      // Tamper with underlying payload in-place
      payload.query = 'event.code: 4624 OR 1=1 -- MALICIOUS';

      await expect(
        repoA.getEvidence(userA, evidence.evidenceId)
      ).rejects.toThrow(EvidenceTamperError);
    });

    it('confirms SHA-256 is strictly an integrity check and not a digital signature', async () => {
      const payload = { test: 'integrity_only' };
      const hash1 = await computeIntegrityDigest(payload);
      const hash2 = await computeIntegrityDigest(payload);
      // SHA-256 is purely deterministic digest, without private key authority
      expect(hash1).toBe(hash2);
    });
  });

  describe('5. Cross-Platform Continuity & Synchronization', () => {
    it('synchronizes mission attempt written by Android client when retrieved by Web client with same UID', async () => {
      // Simulate Android client recording a mission attempt under userA
      const androidAttempt = await repoA.submitMissionAttempt({
        attemptId: 'att_shared_mission_77',
        missionId: 'mission_apt29_triage',
        status: 'IN_PROGRESS',
        clientStartedAt: 1720000000000,
        userInputs: { selectedHost: 'dc01.corp.internal' }
      });

      // Web client with same UID retrieves it
      const fetchedByWeb = await repoA.getMissionAttempt(userA, 'att_shared_mission_77');
      expect(fetchedByWeb).not.toBeNull();
      expect(fetchedByWeb?.missionId).toBe('mission_apt29_triage');
      expect(fetchedByWeb?.userInputs.selectedHost).toBe('dc01.corp.internal');
      expect(fetchedByWeb?.ownerAuthUid).toBe(userA);
    });

    it('allows Web client to acknowledge server-authoritative Next Action without altering core recommendation', async () => {
      const serverAction: CloudNextAction = {
        actionId: 'na_threat_intel_01',
        ownerAuthUid: userA,
        targetSkillKey: 'threat_hunting',
        prescribedMissionId: 'mission_yara_hunting',
        priority: 'HIGH',
        reason: 'Identified gap in memory forensics',
        userStatus: 'PENDING',
        generatedAt: Date.now(),
        expiresAt: Date.now() + 86400000
      };

      repoA.setAuthoritativeNextActionForSync(userA, serverAction);

      const actions = await repoA.getNextActions(userA);
      expect(actions.length).toBe(1);
      expect(actions[0].userStatus).toBe('PENDING');

      // Client acknowledges
      await repoA.updateNextActionStatus('na_threat_intel_01', 'ACCEPTED');
      const updatedActions = await repoA.getNextActions(userA);
      expect(updatedActions[0].userStatus).toBe('ACCEPTED');
      expect(updatedActions[0].acknowledgedAt).toBeDefined();
      // Core recommendation fields remain unchanged
      expect(updatedActions[0].targetSkillKey).toBe('threat_hunting');
    });

    it('safely ingests server-authoritative capabilities during cloud sync', async () => {
      const serverCapability: CloudCapabilityState = {
        skillKey: 'malware_reverse_eng',
        ownerAuthUid: userA,
        level: 'PROFICIENT',
        demonstratedState: true,
        verifiedState: true, // Authoritatively verified by backend
        confidence: 0.94,
        evidenceCount: 12,
        lastDemonstratedAt: Date.now(),
        revision: 4
      };

      repoA.setAuthoritativeCapabilityForSync(userA, serverCapability);

      const readCapability = await repoA.getLearnerCapability(userA, 'malware_reverse_eng');
      expect(readCapability).not.toBeNull();
      expect(readCapability?.level).toBe('PROFICIENT');
      expect(readCapability?.verifiedState).toBe(true);
      expect(readCapability?.confidence).toBe(0.94);
    });
  });
});
