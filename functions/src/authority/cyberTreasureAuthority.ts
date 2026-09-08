import * as admin from 'firebase-admin';
import { HttpsError } from 'firebase-functions/v2/https';
import { CloudCyberTreasure, CloudEvidenceItem, AuthoritativeMetadata } from '../models/types';

export interface TreasureGrantRequest {
  treasureId: string;
  title: string;
  category: string;
  evidenceId: string;
}

export class ServerCyberTreasureAuthority {
  private db: admin.firestore.Firestore;

  constructor(db?: admin.firestore.Firestore) {
    this.db = db || admin.firestore();
  }

  /**
   * Authoritatively grants Cyber Treasure when backed by verified evidence.
   * Clients cannot self-grant treasure.
   */
  public async evaluateAndGrantTreasure(
    authenticatedUid: string,
    req: TreasureGrantRequest
  ): Promise<CloudCyberTreasure> {
    if (!req.treasureId || !req.evidenceId || !req.title) {
      throw new HttpsError('invalid-argument', 'treasureId, evidenceId, and title are required.');
    }

    const treasureRef = this.db.doc(`learners/${authenticatedUid}/cyber_treasure/${req.treasureId}`);

    // 1. Idempotency check
    const existingDoc = await treasureRef.get();
    if (existingDoc.exists) {
      return existingDoc.data() as CloudCyberTreasure;
    }

    // 2. Verify backing evidence exists and is VERIFIED by the server
    const evidenceRef = this.db.doc(`learners/${authenticatedUid}/evidence/${req.evidenceId}`);
    const evidenceDoc = await evidenceRef.get();

    if (!evidenceDoc.exists) {
      throw new HttpsError('failed-precondition', `Evidence '${req.evidenceId}' not found for learner.`);
    }

    const evidenceData = evidenceDoc.data() as CloudEvidenceItem;
    if (evidenceData.serverVerificationState !== 'VERIFIED') {
      throw new HttpsError('failed-precondition', `Evidence '${req.evidenceId}' is not VERIFIED by server authority.`);
    }

    const nowIso = new Date().toISOString();
    const authorityMetadata: AuthoritativeMetadata = {
      authoritySource: 'SERVER',
      verifiedAt: nowIso,
      verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
      algorithmVersion: '3.0.0',
      sourceEvidenceIds: [req.evidenceId]
    };

    const treasure: CloudCyberTreasure = {
      treasureId: req.treasureId,
      ownerAuthUid: authenticatedUid,
      title: req.title,
      category: req.category || 'Threat Hunting',
      evidenceId: req.evidenceId,
      integrityHash: evidenceData.integrityDigest,
      demonstratedScore: 92,
      unlockedAt: nowIso,
      verifiedStatus: 'CRYPTOGRAPHICALLY_VERIFIED',
      authorityMetadata
    };

    // 3. Authoritative Write via Admin SDK
    await treasureRef.set(treasure, { merge: true });

    return treasure;
  }
}
