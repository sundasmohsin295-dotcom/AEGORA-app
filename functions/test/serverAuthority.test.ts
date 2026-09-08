import { describe, it, expect, beforeEach } from 'vitest';
import * as crypto from 'crypto';
import { AuthVerificationService } from '../src/auth/authVerification';
import { ServerEvidenceAuthority } from '../src/authority/evidenceAuthority';
import { ServerCapabilityAuthority } from '../src/authority/capabilityAuthority';
import { ServerMasteryAuthority } from '../src/authority/masteryAuthority';
import { ServerReadinessAuthority } from '../src/authority/readinessAuthority';
import { ServerCyberTreasureAuthority } from '../src/authority/cyberTreasureAuthority';
import { ServerNextMoveAuthority } from '../src/authority/nextMoveAuthority';
import { CloudEvidenceItem, CloudCapabilityState } from '../src/models/types';

/**
 * High-fidelity in-memory Firestore fixture for backend authority testing
 */
class InMemoryFirestore {
  private storage: Map<string, any> = new Map();

  public doc(path: string) {
    const storage = this.storage;
    return {
      get: async () => {
        const data = storage.get(path);
        return {
          exists: data !== undefined,
          data: () => data
        };
      },
      set: async (data: any, options?: { merge?: boolean }) => {
        if (options?.merge && storage.has(path)) {
          const current = storage.get(path);
          storage.set(path, { ...current, ...data });
        } else {
          storage.set(path, { ...data });
        }
      }
    };
  }

  public collection(collectionPath: string) {
    const storage = this.storage;
    return {
      where: (field: string, op: string, value: any) => {
        let filters: Array<{ field: string; op: string; value: any }> = [{ field, op, value }];
        const queryObj = {
          where: (f2: string, op2: string, v2: any) => {
            filters.push({ field: f2, op: op2, value: v2 });
            return queryObj;
          },
          get: async () => {
            const docs: any[] = [];
            for (const [key, docData] of storage.entries()) {
              if (key.startsWith(collectionPath + '/')) {
                const subPath = key.substring(collectionPath.length + 1);
                if (!subPath.includes('/')) {
                  const matches = filters.every(f => {
                    if (f.op === '==') return docData[f.field] === f.value;
                    return true;
                  });
                  if (matches) {
                    docs.push({
                      id: subPath,
                      data: () => docData
                    });
                  }
                }
              }
            }
            return {
              docs,
              size: docs.length,
              forEach: (callback: (doc: any) => void) => docs.forEach(callback)
            };
          }
        };
        return queryObj;
      },
      get: async () => {
        const docs: any[] = [];
        for (const [key, docData] of storage.entries()) {
          if (key.startsWith(collectionPath + '/')) {
            const subPath = key.substring(collectionPath.length + 1);
            if (!subPath.includes('/')) {
              docs.push({
                id: subPath,
                data: () => docData
              });
            }
          }
        }
        return {
          docs,
          size: docs.length,
          forEach: (callback: (doc: any) => void) => docs.forEach(callback)
        };
      }
    };
  }

  public clear() {
    this.storage.clear();
  }
}

describe('AEGORA Phase 3C: Trusted Server Authority Test Suite', () => {
  let mockDb: any;
  let evidenceAuth: ServerEvidenceAuthority;
  let capabilityAuth: ServerCapabilityAuthority;
  let masteryAuth: ServerMasteryAuthority;
  let readinessAuth: ServerReadinessAuthority;
  let treasureAuth: ServerCyberTreasureAuthority;
  let nextMoveAuth: ServerNextMoveAuthority;

  const USER_A_UID = 'firebase_uid_operator_alpha_123';
  const USER_B_UID = 'firebase_uid_operator_beta_456';

  beforeEach(() => {
    mockDb = new InMemoryFirestore();
    evidenceAuth = new ServerEvidenceAuthority(mockDb as any);
    capabilityAuth = new ServerCapabilityAuthority(mockDb as any);
    masteryAuth = new ServerMasteryAuthority(mockDb as any);
    readinessAuth = new ServerReadinessAuthority(mockDb as any);
    treasureAuth = new ServerCyberTreasureAuthority(mockDb as any);
    nextMoveAuth = new ServerNextMoveAuthority(mockDb as any);
  });

  // 1. AUTHENTICATION & IDENTITY VERIFICATION
  describe('1. Authentication & Identity Verification', () => {
    it('rejects unauthenticated requests without auth context', () => {
      const mockReq: any = { auth: null, data: {} };
      expect(() => AuthVerificationService.verifyCaller(mockReq)).toThrowError(/Unauthenticated/);
    });

    it('rejects unauthenticated requests with missing UID', () => {
      const mockReq: any = { auth: {}, data: {} };
      expect(() => AuthVerificationService.verifyCaller(mockReq)).toThrowError(/Unauthenticated/);
    });

    it('accepts valid authenticated Firebase UID', () => {
      const mockReq: any = { auth: { uid: USER_A_UID }, data: {} };
      const resolvedUid = AuthVerificationService.verifyCaller(mockReq);
      expect(resolvedUid).toBe(USER_A_UID);
    });

    it('rejects client attempts to pass spoofed targetAuthUid (Cross-user violation)', () => {
      const mockReq: any = { auth: { uid: USER_A_UID }, data: {} };
      expect(() => AuthVerificationService.verifyCaller(mockReq, USER_B_UID)).toThrowError(/Cross-user violation/);
    });
  });

  // 2. LEARNER ISOLATION
  describe('2. Learner Isolation & Cross-User Security', () => {
    it('blocks User A from submitting or verifying evidence for User B attempt', async () => {
      // Seed User B's mission attempt
      await mockDb.doc(`learners/${USER_B_UID}/mission_attempts/att_b_01`).set({
        attemptId: 'att_b_01',
        ownerAuthUid: USER_B_UID,
        status: 'IN_PROGRESS'
      });

      // User A tries to ingest evidence referencing User B's attempt under User A's path
      // Attempt doc under User A won't exist or won't match
      const rawPayload = 'telemetry_ok: true\nexit_code: 0\nartifact_hash: test';
      const digest = 'sha256:' + crypto.createHash('sha256').update(`${USER_A_UID}:m1:att_b_01:NETWORK_PCAP:${rawPayload}`).digest('hex');

      // Attempting under User B path as User A is blocked by caller verification:
      const mockReq: any = { auth: { uid: USER_A_UID } };
      expect(() => AuthVerificationService.verifyCaller(mockReq, USER_B_UID)).toThrowError(/Cross-user/);
    });
  });

  // 3. EVIDENCE INGESTION, INTEGRITY & TAMPER DETECTION
  describe('3. Evidence Ingestion & Verification Authority', () => {
    it('accepts valid evidence, validates SHA-256 integrity, and awards authoritative VERIFIED state', async () => {
      const rawPayload = 'status: success\nexit_code: 0\nartifact_hash: deadbeef1234567890';
      const rawString = `${USER_A_UID}:m_siem_01:att_01:NETWORK_PCAP:${rawPayload}`;
      const validDigest = 'sha256:' + crypto.createHash('sha256').update(rawString).digest('hex');

      const result = await evidenceAuth.ingestAndVerifyEvidence(USER_A_UID, {
        evidenceId: 'ev_01',
        attemptId: 'att_01',
        missionId: 'm_siem_01',
        skillKey: 'siem_log_triage',
        evidenceType: 'NETWORK_PCAP',
        payloadRaw: rawPayload,
        integrityDigest: validDigest
      });

      expect(result.serverVerificationState).toBe('VERIFIED');
      expect(result.verified).toBe(true);
      expect(result.authorityMetadata?.authoritySource).toBe('SERVER');
      expect(result.authorityMetadata?.verifiedBy).toBe('AEGORA_COGNITIVE_ENGINE_V3');

      // Check Firestore storage
      const saved = await mockDb.doc(`learners/${USER_A_UID}/evidence/ev_01`).get();
      expect(saved.exists).toBe(true);
      expect(saved.data().serverVerificationState).toBe('VERIFIED');
    });

    it('rejects tampered evidence when payload does not match SHA-256 digest', async () => {
      const rawPayload = 'status: success\nexit_code: 0\nartifact_hash: valid';
      const fakeDigest = 'sha256:0000000000000000000000000000000000000000000000000000000000000000';

      const result = await evidenceAuth.ingestAndVerifyEvidence(USER_A_UID, {
        evidenceId: 'ev_tampered',
        attemptId: 'att_01',
        missionId: 'm_siem_01',
        skillKey: 'siem_log_triage',
        evidenceType: 'NETWORK_PCAP',
        payloadRaw: rawPayload,
        integrityDigest: fakeDigest
      });

      expect(result.serverVerificationState).toBe('REJECTED');
      expect(result.verified).toBe(false);
      expect(result.rejectionReason).toContain('SHA-256 payload tampering detected');
    });

    it('rejects malformed evidence or disallowed evidence types', async () => {
      await expect(
        evidenceAuth.ingestAndVerifyEvidence(USER_A_UID, {
          evidenceId: 'ev_invalid',
          attemptId: 'att_01',
          missionId: 'm_siem_01',
          skillKey: 'siem_log_triage',
          evidenceType: 'FORGED_INVALID_TYPE',
          payloadRaw: 'some data',
          integrityDigest: 'dummy'
        })
      ).rejects.toThrowError(/Invalid evidenceType/);
    });

    it('is strictly idempotent on duplicate evidence submission', async () => {
      const rawPayload = 'status: success\nexit_code: 0\nartifact_hash: deadbeef';
      const digest = 'sha256:' + crypto.createHash('sha256').update(`${USER_A_UID}:m_siem_01:att_01:NETWORK_PCAP:${rawPayload}`).digest('hex');

      const first = await evidenceAuth.ingestAndVerifyEvidence(USER_A_UID, {
        evidenceId: 'ev_idempotent',
        attemptId: 'att_01',
        missionId: 'm_siem_01',
        skillKey: 'siem_log_triage',
        evidenceType: 'NETWORK_PCAP',
        payloadRaw: rawPayload,
        integrityDigest: digest
      });

      const second = await evidenceAuth.ingestAndVerifyEvidence(USER_A_UID, {
        evidenceId: 'ev_idempotent',
        attemptId: 'att_01',
        missionId: 'm_siem_01',
        skillKey: 'siem_log_triage',
        evidenceType: 'NETWORK_PCAP',
        payloadRaw: rawPayload,
        integrityDigest: digest
      });

      expect(first.evidenceId).toBe(second.evidenceId);
      expect(second.serverVerificationState).toBe('VERIFIED');
    });
  });

  // 4. CAPABILITY AUTHORITY & CLIENT PRIVILEGE ESCALATION BLOCK
  describe('4. Server-Authoritative Capability Evaluation', () => {
    it('unverified evidence cannot produce demonstrated capability', async () => {
      // No verified evidence seeded
      const result = await capabilityAuth.evaluateAuthoritativeCapability(USER_A_UID, 'siem_log_triage');
      expect(result.isDemonstrated).toBe(false);
      expect(result.currentConfidence).toBeLessThanOrEqual(50);
      expect(result.authorityMetadata.authoritySource).toBe('SERVER');
    });

    it('verified evidence produces authoritative demonstrated capability with 7-gate scores', async () => {
      // Seed 2 verified evidence documents
      const now = new Date().toISOString();
      await mockDb.doc(`learners/${USER_A_UID}/evidence/ev_01`).set({
        evidenceId: 'ev_01',
        ownerAuthUid: USER_A_UID,
        skillKey: 'siem_log_triage',
        serverVerificationState: 'VERIFIED',
        verified: true,
        verifiedAt: now
      });
      await mockDb.doc(`learners/${USER_A_UID}/evidence/ev_02`).set({
        evidenceId: 'ev_02',
        ownerAuthUid: USER_A_UID,
        skillKey: 'siem_log_triage',
        serverVerificationState: 'VERIFIED',
        verified: true,
        verifiedAt: now
      });

      const result = await capabilityAuth.evaluateAuthoritativeCapability(USER_A_UID, 'siem_log_triage');
      expect(result.isDemonstrated).toBe(true);
      expect(result.currentConfidence).toBeGreaterThanOrEqual(75);
      expect(result.gateResults['UNDERSTAND'].isPassed).toBe(true);
      expect(result.authorityMetadata.authoritySource).toBe('SERVER');
      expect(result.authorityMetadata.sourceEvidenceIds).toContain('ev_01');
      expect(result.authorityMetadata.sourceEvidenceIds).toContain('ev_02');
    });
  });

  // 5. MASTERY AUTHORITY (7 GATES & 4 CLUSTERS)
  describe('5. Server-Authoritative Mastery Evaluation', () => {
    it('derives mastery status strictly from server-verified capabilities', async () => {
      // Seed an authoritative demonstrated capability
      await mockDb.doc(`learners/${USER_A_UID}/capabilities/siem_log_triage`).set({
        skillKey: 'siem_log_triage',
        ownerAuthUid: USER_A_UID,
        name: 'Siem Log Triage',
        category: 'Active Defense',
        currentConfidence: 85,
        isDemonstrated: true,
        retentionRisk: 'LOW',
        daysSinceLastVerified: 1,
        gateResults: {},
        authorityMetadata: {
          authoritySource: 'SERVER',
          verifiedAt: new Date().toISOString(),
          verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
          algorithmVersion: '3.0.0',
          sourceEvidenceIds: ['ev_01']
        }
      });

      const assessment = await masteryAuth.evaluateAuthoritativeMastery(USER_A_UID, 'asm_test_01');
      expect(assessment.assessmentId).toBe('asm_test_01');
      expect(assessment.ownerAuthUid).toBe(USER_A_UID);
      expect(assessment.clusterMetrics.length).toBe(4);
      expect(assessment.authorityMetadata.authoritySource).toBe('SERVER');
    });
  });

  // 6. READINESS AUTHORITY
  describe('6. Server-Authoritative Career Readiness', () => {
    it('calculates readiness score from authoritative capability baseline', async () => {
      const readiness = await readinessAuth.calculateAuthoritativeReadiness(USER_A_UID, 'soc_analyst_t2');
      expect(readiness.ownerAuthUid).toBe(USER_A_UID);
      expect(readiness.readinessScore).toBeGreaterThan(0);
      expect(readiness.targetRole).toContain('SOC Analyst');
      expect(readiness.criticalGaps.length).toBeGreaterThan(0);
      expect(readiness.authorityMetadata.authoritySource).toBe('SERVER');
    });
  });

  // 7. CYBER TREASURE AUTHORITY
  describe('7. Server-Authoritative Cyber Treasure', () => {
    it('rejects Cyber Treasure grant when evidence is missing or unverified', async () => {
      await expect(
        treasureAuth.evaluateAndGrantTreasure(USER_A_UID, {
          treasureId: 'tr_fake',
          title: 'Fake Achievement',
          category: 'Threat Hunting',
          evidenceId: 'non_existent_ev'
        })
      ).rejects.toThrowError(/not found/);
    });

    it('grants Cyber Treasure when evidence is verified by server', async () => {
      // Seed verified evidence
      await mockDb.doc(`learners/${USER_A_UID}/evidence/ev_verified_hunter`).set({
        evidenceId: 'ev_verified_hunter',
        ownerAuthUid: USER_A_UID,
        serverVerificationState: 'VERIFIED',
        verified: true,
        integrityDigest: 'sha256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069'
      });

      const treasure = await treasureAuth.evaluateAndGrantTreasure(USER_A_UID, {
        treasureId: 'tr_pcap_beacon',
        title: 'Network PCAP Beaconing Detection',
        category: 'Threat Hunting',
        evidenceId: 'ev_verified_hunter'
      });

      expect(treasure.treasureId).toBe('tr_pcap_beacon');
      expect(treasure.verifiedStatus).toBe('CRYPTOGRAPHICALLY_VERIFIED');
      expect(treasure.authorityMetadata.authoritySource).toBe('SERVER');
    });

    it('is strictly idempotent on duplicate treasure grant requests', async () => {
      await mockDb.doc(`learners/${USER_A_UID}/evidence/ev_01`).set({
        evidenceId: 'ev_01',
        ownerAuthUid: USER_A_UID,
        serverVerificationState: 'VERIFIED',
        verified: true,
        integrityDigest: 'sha256:1111'
      });

      const first = await treasureAuth.evaluateAndGrantTreasure(USER_A_UID, {
        treasureId: 'tr_01',
        title: 'Title 1',
        category: 'Endpoint Security',
        evidenceId: 'ev_01'
      });

      const second = await treasureAuth.evaluateAndGrantTreasure(USER_A_UID, {
        treasureId: 'tr_01',
        title: 'Title 1',
        category: 'Endpoint Security',
        evidenceId: 'ev_01'
      });

      expect(first.treasureId).toBe(second.treasureId);
      expect(first.unlockedAt).toBe(second.unlockedAt);
    });
  });

  // 8. NEXT MOVE AUTHORITY
  describe('8. Server-Authoritative NEXT MOVE Authority', () => {
    it('generates prescriptive next move targeting limiting gate and retention decay', async () => {
      // Seed capability with High decay risk
      await mockDb.doc(`learners/${USER_A_UID}/capabilities/cap_decayed`).set({
        skillKey: 'cap_decayed',
        ownerAuthUid: USER_A_UID,
        name: 'Authentication Protocol Hardening',
        category: 'Foundation',
        currentConfidence: 62,
        isDemonstrated: true,
        retentionRisk: 'CRITICAL',
        daysSinceLastVerified: 35,
        gateResults: {},
        authorityMetadata: {
          authoritySource: 'SERVER',
          verifiedAt: new Date().toISOString(),
          verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
          algorithmVersion: '3.0.0',
          sourceEvidenceIds: []
        }
      });

      const nextMove = await nextMoveAuth.generateAuthoritativeNextMove(USER_A_UID, 'move_decay_remediation');
      expect(nextMove.title).toContain('Decay Refresher');
      expect(nextMove.urgencyScore).toBeGreaterThanOrEqual(90);
      expect(nextMove.authorityMetadata.authoritySource).toBe('SERVER');
    });
  });
});
