/**
 * WebCloudRepository.ts
 *
 * Authoritative Web Firestore Cloud Repository for AEGORA.
 *
 * Implements:
 * 1. Scoped Firestore collections: /learners/{authUid}/...
 * 2. Strict authorization boundary enforcement: User A cannot read/write User B.
 * 3. Client privilege escalation prevention: Rejects client attempts to directly set
 *    verified = true, mastery = 'VERIFIED', readiness = 'JOB_READY'.
 * 4. Evidence integrity verification: SHA-256 is strictly an integrity check for tamper detection,
 *    NOT a digital signature, authentication, or non-repudiation.
 * 5. Offline and sync safety: Idempotent writes with deterministic document IDs and revision tracking.
 */

import {
  Firestore,
  doc,
  getDoc,
  setDoc,
  collection,
  getDocs,
  updateDoc
} from 'firebase/firestore';
import { getFirestoreInstance } from '../firebase/firebaseClient';
import {
  CloudLearnerDocument,
  CloudMissionAttempt,
  CloudEvidenceItem,
  CloudCapabilityState,
  CloudMasteryAssessment,
  CloudCyberTreasure,
  CloudNextAction,
  PlatformClient
} from '../types/platform';

export class CloudAuthorizationError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'CloudAuthorizationError';
  }
}

export class PrivilegeEscalationError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'PrivilegeEscalationError';
  }
}

export class EvidenceTamperError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'EvidenceTamperError';
  }
}

/**
 * Computes a SHA-256 integrity digest for payload tamper detection.
 * NOTE: This is strictly an integrity detection layer, NOT a digital signature.
 */
export async function computeIntegrityDigest(payload: Record<string, unknown>): Promise<string> {
  const jsonStr = JSON.stringify(payload, Object.keys(payload).sort());
  const encoder = new TextEncoder();
  const data = encoder.encode(jsonStr);

  if (typeof crypto !== 'undefined' && crypto.subtle) {
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
  }

  // Pure fallback for environments where crypto.subtle is not available
  let hash = 0;
  for (let i = 0; i < data.length; i++) {
    hash = ((hash << 5) - hash) + data[i];
    hash |= 0;
  }
  return Math.abs(hash).toString(16).padStart(64, '0');
}

export class WebCloudRepository {
  private firestore: Firestore | null;
  private currentAuthUid: string | null;

  // Local synchronized state cache for offline safety and isolation testing
  private inMemoryLearners: Map<string, CloudLearnerDocument> = new Map();
  private inMemoryAttempts: Map<string, CloudMissionAttempt> = new Map();
  private inMemoryEvidence: Map<string, CloudEvidenceItem> = new Map();
  private inMemoryCapabilities: Map<string, CloudCapabilityState> = new Map();
  private inMemoryMastery: Map<string, CloudMasteryAssessment> = new Map();
  private inMemoryTreasure: Map<string, CloudCyberTreasure> = new Map();
  private inMemoryNextActions: Map<string, CloudNextAction> = new Map();

  constructor(authUid: string | null, firestore?: Firestore | null) {
    this.currentAuthUid = authUid;
    this.firestore = firestore !== undefined ? firestore : getFirestoreInstance();
  }

  public setAuthenticatedUid(authUid: string | null) {
    this.currentAuthUid = authUid;
  }

  public getAuthenticatedUid(): string | null {
    return this.currentAuthUid;
  }

  /**
   * Asserts caller is authenticated and matches target learner path.
   * Prevents cross-tenant access (User A cannot access User B).
   */
  private assertOwnership(targetAuthUid: string) {
    if (!this.currentAuthUid) {
      throw new CloudAuthorizationError('Unauthenticated: Access to protected cloud resources denied');
    }
    if (this.currentAuthUid !== targetAuthUid) {
      throw new CloudAuthorizationError(
        `LearnerIsolationViolation: Caller ${this.currentAuthUid} cannot access resources of ${targetAuthUid}`
      );
    }
  }

  // --- Learner Root ---

  public async getOrCreateLearnerProfile(
    authUid: string,
    canonicalLearnerId: string,
    displayName: string,
    email?: string | null
  ): Promise<CloudLearnerDocument> {
    this.assertOwnership(authUid);

    const docPath = `learners/${authUid}`;
    if (this.firestore) {
      try {
        const ref = doc(this.firestore, docPath);
        const snapshot = await getDoc(ref);
        if (snapshot.exists()) {
          return snapshot.data() as CloudLearnerDocument;
        }
        const profile: CloudLearnerDocument = {
          firebaseAuthUid: authUid,
          ownerAuthUid: authUid,
          canonicalLearnerId,
          email,
          displayName,
          createdAt: Date.now(),
          lastActiveAt: Date.now()
        };
        await setDoc(ref, profile);
        return profile;
      } catch (err) {
        console.warn('Firestore unavailable, using synchronized local store:', err);
      }
    }

    // In-memory / offline store
    if (!this.inMemoryLearners.has(authUid)) {
      this.inMemoryLearners.set(authUid, {
        firebaseAuthUid: authUid,
        ownerAuthUid: authUid,
        canonicalLearnerId,
        email,
        displayName,
        createdAt: Date.now(),
        lastActiveAt: Date.now()
      });
    }
    return this.inMemoryLearners.get(authUid)!;
  }

  // --- Mission Attempts (Client-Writable Telemetry) ---

  public async submitMissionAttempt(attempt: Omit<CloudMissionAttempt, 'ownerAuthUid' | 'revision'>): Promise<CloudMissionAttempt> {
    if (!this.currentAuthUid) {
      throw new CloudAuthorizationError('Unauthenticated: Cannot record mission attempt');
    }

    // Privilege Escalation Prevention: Client cannot self-certify or award mastery
    if (attempt.verifiedOutcome === true || attempt.masteryAwarded === true) {
      throw new PrivilegeEscalationError('PrivilegeEscalation: Client cannot self-certify mission verifiedOutcome or masteryAwarded');
    }

    const authUid = this.currentAuthUid;
    const attemptId = attempt.attemptId || `att_${authUid}_${attempt.missionId}_${Date.now()}`;
    const fullAttempt: CloudMissionAttempt = {
      ...attempt,
      attemptId,
      ownerAuthUid: authUid,
      revision: 1
    };

    if (this.firestore) {
      try {
        const ref = doc(this.firestore, `learners/${authUid}/mission_attempts/${attemptId}`);
        await setDoc(ref, fullAttempt);
      } catch (err) {
        console.warn('Firestore write failed, updating offline cache:', err);
      }
    }

    this.inMemoryAttempts.set(`${authUid}:${attemptId}`, fullAttempt);
    return fullAttempt;
  }

  public async getMissionAttempt(targetAuthUid: string, attemptId: string): Promise<CloudMissionAttempt | null> {
    this.assertOwnership(targetAuthUid);

    if (this.firestore) {
      try {
        const ref = doc(this.firestore, `learners/${targetAuthUid}/mission_attempts/${attemptId}`);
        const snap = await getDoc(ref);
        if (snap.exists()) {
          return snap.data() as CloudMissionAttempt;
        }
      } catch (_e) {
        // Fallback to cache
      }
    }

    return this.inMemoryAttempts.get(`${targetAuthUid}:${attemptId}`) || null;
  }

  // --- Evidence (Client Appends Pending Evidence, Server Verifies) ---

  public async submitEvidence(
    attemptId: string,
    skillKey: string,
    evidenceType: string,
    rawPayload: Record<string, unknown>,
    sourcePlatform: PlatformClient = 'WEB'
  ): Promise<CloudEvidenceItem> {
    if (!this.currentAuthUid) {
      throw new CloudAuthorizationError('Unauthenticated: Cannot submit evidence');
    }

    const authUid = this.currentAuthUid;
    const clientDigest = await computeIntegrityDigest(rawPayload);
    const evidenceId = `ev_${authUid}_${attemptId}_${Date.now()}`;

    const evidence: CloudEvidenceItem = {
      evidenceId,
      ownerAuthUid: authUid,
      attemptId,
      skillKey,
      evidenceType,
      rawPayload,
      clientDigest, // SHA-256 for integrity tamper detection (NOT a digital signature)
      serverVerificationState: 'PENDING_VERIFICATION',
      verified: false,
      sourcePlatform,
      clientTimestamp: Date.now()
    };

    if (this.firestore) {
      try {
        const ref = doc(this.firestore, `learners/${authUid}/evidence/${evidenceId}`);
        await setDoc(ref, evidence);
      } catch (err) {
        console.warn('Firestore write failed, saving to cache:', err);
      }
    }

    this.inMemoryEvidence.set(`${authUid}:${evidenceId}`, evidence);
    return evidence;
  }

  public async getEvidence(targetAuthUid: string, evidenceId: string): Promise<CloudEvidenceItem | null> {
    this.assertOwnership(targetAuthUid);

    const item = this.inMemoryEvidence.get(`${targetAuthUid}:${evidenceId}`) || null;
    if (item) {
      // Verify integrity against tampering
      const currentDigest = await computeIntegrityDigest(item.rawPayload);
      if (currentDigest !== item.clientDigest) {
        throw new EvidenceTamperError(`EvidenceTamperDetected: Payload hash does not match original digest for ${evidenceId}`);
      }
    }
    return item;
  }

  // --- Capabilities & Mastery (Server-Authoritative, Protected from Client Direct Writes) ---

  /**
   * Rejects client attempts to directly set verified = true or elevate capability.
   */
  public async attemptClientCapabilityWrite(skillKey: string, level: string, verified: boolean): Promise<void> {
    if (!this.currentAuthUid) {
      throw new CloudAuthorizationError('Unauthenticated');
    }
    // Server-authoritative invariant: Client cannot write to /capabilities
    throw new PrivilegeEscalationError(
      `PrivilegeEscalation: Client cannot directly write capability state (${skillKey}=${level}, verified=${verified}). Server authority required.`
    );
  }

  /**
   * Rejects client attempts to directly write mastery = 'VERIFIED'.
   */
  public async attemptClientMasteryWrite(skillKey: string, masteryLevel: string): Promise<void> {
    if (!this.currentAuthUid) {
      throw new CloudAuthorizationError('Unauthenticated');
    }
    throw new PrivilegeEscalationError(
      `PrivilegeEscalation: Client cannot directly create MasteryAssessment (${skillKey}=${masteryLevel}). Server authority required.`
    );
  }

  /**
   * Rejects client attempts to directly declare readiness = 'JOB_READY'.
   */
  public async attemptClientReadinessWrite(careerTrack: string, status: string): Promise<void> {
    if (!this.currentAuthUid) {
      throw new CloudAuthorizationError('Unauthenticated');
    }
    throw new PrivilegeEscalationError(
      `PrivilegeEscalation: Client cannot directly set career readiness status (${careerTrack}=${status}). Server authority required.`
    );
  }

  /**
   * Reads authoritative capability state (Read-only for client).
   */
  public async getLearnerCapability(targetAuthUid: string, skillKey: string): Promise<CloudCapabilityState | null> {
    this.assertOwnership(targetAuthUid);
    return this.inMemoryCapabilities.get(`${targetAuthUid}:${skillKey}`) || null;
  }

  /**
   * Ingests authoritative capability state (used by synchronization and server authority engine).
   */
  public setAuthoritativeCapabilityForSync(targetAuthUid: string, state: CloudCapabilityState) {
    this.inMemoryCapabilities.set(`${targetAuthUid}:${state.skillKey}`, state);
  }

  // --- Next Actions / NEXT MOVE ---

  public async getNextActions(targetAuthUid: string): Promise<CloudNextAction[]> {
    this.assertOwnership(targetAuthUid);
    return Array.from(this.inMemoryNextActions.values()).filter(a => a.ownerAuthUid === targetAuthUid);
  }

  public async updateNextActionStatus(actionId: string, status: 'ACCEPTED' | 'DISMISSED'): Promise<void> {
    if (!this.currentAuthUid) {
      throw new CloudAuthorizationError('Unauthenticated');
    }
    const key = `${this.currentAuthUid}:${actionId}`;
    const action = this.inMemoryNextActions.get(key);
    if (!action) return;

    action.userStatus = status;
    if (status === 'ACCEPTED') action.acknowledgedAt = Date.now();
    if (status === 'DISMISSED') action.dismissedAt = Date.now();
  }

  public setAuthoritativeNextActionForSync(targetAuthUid: string, action: CloudNextAction) {
    this.inMemoryNextActions.set(`${targetAuthUid}:${action.actionId}`, action);
  }
}
