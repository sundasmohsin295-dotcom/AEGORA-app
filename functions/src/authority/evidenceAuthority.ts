import * as admin from 'firebase-admin';
import { HttpsError } from 'firebase-functions/v2/https';
import { CloudEvidenceItem, AuthoritativeMetadata, CloudFailurePattern, FailurePatternType } from '../models/types';
import { AuthVerificationService } from '../auth/authVerification';

const ALLOWED_EVIDENCE_TYPES = new Set([
  'NETWORK_PCAP',
  'AUTH_LOG',
  'MEMORY_DUMP',
  'ATTACK_SIMULATION_FLAG',
  'TERMINAL_TELEMETRY',
  'CODE_DIFF',
  'WAF_LOG',
  'CONTAINER_TELEMETRY',
  'MALWARE_SAMPLE'
]);

export interface EvidenceIngestionRequest {
  evidenceId: string;
  attemptId: string;
  missionId: string;
  skillKey: string;
  evidenceType: string;
  payloadRaw: string;
  integrityDigest: string;
  clientClaimedLearnerId?: string;
}

export class ServerEvidenceAuthority {
  private db: admin.firestore.Firestore;

  constructor(db?: admin.firestore.Firestore) {
    this.db = db || admin.firestore();
  }

  /**
   * Processes raw evidence through the trusted server authority pipeline.
   * Client-supplied claims of 'verified' are NEVER accepted.
   */
  public async ingestAndVerifyEvidence(
    authenticatedUid: string,
    req: EvidenceIngestionRequest
  ): Promise<CloudEvidenceItem> {
    // 1. Validate required fields
    if (!req.evidenceId || !req.attemptId || !req.missionId || !req.skillKey || !req.evidenceType || !req.payloadRaw) {
      throw new HttpsError('invalid-argument', 'Malformed evidence payload: Missing required structural attributes.');
    }

    // 2. Enforce allowed evidence taxonomy
    if (!ALLOWED_EVIDENCE_TYPES.has(req.evidenceType)) {
      throw new HttpsError('invalid-argument', `Invalid evidenceType: '${req.evidenceType}' is not in allowed taxonomy.`);
    }

    // 3. Idempotency check: inspect existing evidence document
    const evidenceRef = this.db.doc(`learners/${authenticatedUid}/evidence/${req.evidenceId}`);
    const existingDoc = await evidenceRef.get();
    if (existingDoc.exists) {
      const existingData = existingDoc.data() as CloudEvidenceItem;
      if (existingData.serverVerificationState === 'VERIFIED') {
        // Safe idempotent return
        return existingData;
      }
    }

    // 4. Validate Mission Attempt ownership
    const attemptRef = this.db.doc(`learners/${authenticatedUid}/mission_attempts/${req.attemptId}`);
    const attemptDoc = await attemptRef.get();
    if (attemptDoc.exists) {
      const attemptData = attemptDoc.data();
      if (attemptData?.ownerAuthUid && attemptData.ownerAuthUid !== authenticatedUid) {
        throw new HttpsError('permission-denied', 'Attempt does not belong to authenticated learner.');
      }
    }

    // 5. Validate integrity digest (SHA-256 integrity check)
    // NOTE: SHA-256 IS STRICTLY INTEGRITY / TAMPER-DETECTION.
    // NOT AUTHENTICATION, NOT AUTHORIZATION, NOT DIGITAL SIGNATURE, NOT NON-REPUDIATION.
    const learnerId = req.clientClaimedLearnerId || authenticatedUid;
    const isIntegrityValid = AuthVerificationService.verifyIntegrityDigest(
      learnerId,
      req.missionId,
      req.attemptId,
      req.evidenceType,
      req.payloadRaw,
      req.integrityDigest
    );

    const now = new Date().toISOString();

    if (!isIntegrityValid) {
      // Evidence payload was tampered with or corrupt
      const rejectedRecord: CloudEvidenceItem = {
        evidenceId: req.evidenceId,
        ownerAuthUid: authenticatedUid,
        attemptId: req.attemptId,
        missionId: req.missionId,
        skillKey: req.skillKey,
        evidenceType: req.evidenceType,
        payloadRaw: req.payloadRaw,
        integrityDigest: req.integrityDigest,
        createdAt: now,
        serverVerificationState: 'REJECTED',
        verified: false,
        rejectionReason: 'Integrity digest mismatch (SHA-256 payload tampering detected)'
      };
      await evidenceRef.set(rejectedRecord, { merge: true });
      return rejectedRecord;
    }

    // 6. Evaluate domain verification criteria
    let isCriteriaSatisfied = false;
    try {
      // Parse payload content for verification criteria (e.g. execution indicators, flags, logs)
      const payloadLower = req.payloadRaw.toLowerCase();
      const hasExecutableIndicators = 
        payloadLower.includes('exit_code: 0') || 
        payloadLower.includes('status: success') || 
        payloadLower.includes('artifact_hash:') ||
        payloadLower.includes('verified_flag:') ||
        payloadLower.includes('telemetry_ok: true') ||
        req.payloadRaw.length >= 32;

      isCriteriaSatisfied = hasExecutableIndicators && !payloadLower.includes('mock_error');
    } catch {
      isCriteriaSatisfied = false;
    }

    if (!isCriteriaSatisfied) {
      const rejectedRecord: CloudEvidenceItem = {
        evidenceId: req.evidenceId,
        ownerAuthUid: authenticatedUid,
        attemptId: req.attemptId,
        missionId: req.missionId,
        skillKey: req.skillKey,
        evidenceType: req.evidenceType,
        payloadRaw: req.payloadRaw,
        integrityDigest: req.integrityDigest,
        createdAt: now,
        serverVerificationState: 'REJECTED',
        verified: false,
        rejectionReason: 'Domain verification failed: Required execution criteria or artifacts missing'
      };
      await evidenceRef.set(rejectedRecord, { merge: true });
      return rejectedRecord;
    }

    // 7. Authoritative Verification Grant
    const authorityMetadata: AuthoritativeMetadata = {
      authoritySource: 'SERVER',
      verifiedAt: now,
      verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
      algorithmVersion: '3.0.0',
      sourceEvidenceIds: [req.evidenceId]
    };

    const verifiedRecord: CloudEvidenceItem = {
      evidenceId: req.evidenceId,
      ownerAuthUid: authenticatedUid,
      attemptId: req.attemptId,
      missionId: req.missionId,
      skillKey: req.skillKey,
      evidenceType: req.evidenceType,
      payloadRaw: req.payloadRaw,
      integrityDigest: req.integrityDigest,
      createdAt: now,
      serverVerificationState: 'VERIFIED',
      verified: true,
      verifiedAt: now,
      verifiedBy: 'AEGORA_SERVER_AUTHORITY',
      authorityMetadata
    };

    // Authoritative Admin SDK write to Firestore
    await evidenceRef.set(verifiedRecord, { merge: true });

    return verifiedRecord;
  }

  /**
   * Authoritative evaluation and persistence of Failure Patterns.
   * Client-supplied failure labels or confidence scores are IGNORED.
   * Scoped strictly to authenticated UID.
   */
  public async evaluateAndPersistFailurePatterns(
    authenticatedUid: string,
    missionId: string,
    detectedPatterns: FailurePatternType[],
    supportingEvidenceIds: string[] = []
  ): Promise<CloudFailurePattern[]> {
    if (!authenticatedUid || typeof authenticatedUid !== 'string') {
      throw new HttpsError('unauthenticated', 'Authenticated UID is required.');
    }

    const now = new Date().toISOString();
    const updatedPatterns: CloudFailurePattern[] = [];

    for (const patternType of detectedPatterns) {
      const patternDocId = `${patternType.toLowerCase()}`;
      const patternRef = this.db.doc(`learners/${authenticatedUid}/failure_patterns/${patternDocId}`);

      const existingSnap = await patternRef.get();
      let observationCount = 1;
      let firstObservedAt = now;
      let existingSupporting: string[] = [];

      if (existingSnap.exists) {
        const existingData = existingSnap.data() as CloudFailurePattern;
        observationCount = (existingData.observationCount || 1) + 1;
        firstObservedAt = existingData.firstObservedAt || now;
        existingSupporting = existingData.supportingEvidenceIds || [];
      }

      // Authoritative confidence score calculation (deterministic based on occurrences)
      const confidenceScore = observationCount >= 3 ? 90 : observationCount === 2 ? 65 : 40;

      const mergedEvidenceIds = Array.from(new Set([...existingSupporting, ...supportingEvidenceIds]));

      const patternRecord: CloudFailurePattern = {
        patternId: patternDocId,
        ownerAuthUid: authenticatedUid,
        patternType,
        confidenceScore,
        observationCount,
        lastObservedMissionId: missionId,
        lastObservedAt: now,
        firstObservedAt,
        supportingEvidenceIds: mergedEvidenceIds,
        decayHalfLifeDays: 14,
        authorityMetadata: {
          authoritySource: 'SERVER',
          verifiedAt: now,
          verifiedBy: 'AEGORA_FAILURE_AUTHORITY_V2',
          algorithmVersion: '2.0.0',
          sourceEvidenceIds: mergedEvidenceIds
        }
      };

      await patternRef.set(patternRecord, { merge: true });
      updatedPatterns.push(patternRecord);
    }

    return updatedPatterns;
  }
}

