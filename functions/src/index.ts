import * as admin from 'firebase-admin';
import { onCall, HttpsError } from 'firebase-functions/v2/https';
import { AuthVerificationService } from './auth/authVerification';
import { ServerEvidenceAuthority, EvidenceIngestionRequest } from './authority/evidenceAuthority';
import { ServerCapabilityAuthority } from './authority/capabilityAuthority';
import { ServerMasteryAuthority } from './authority/masteryAuthority';
import { ServerReadinessAuthority } from './authority/readinessAuthority';
import { ServerCyberTreasureAuthority, TreasureGrantRequest } from './authority/cyberTreasureAuthority';
import { ServerNextMoveAuthority } from './authority/nextMoveAuthority';

// Initialize Firebase Admin SDK ONLY inside trusted server execution environment.
// Never expose Admin credentials to clients or commit private keys.
if (!admin.apps.length) {
  admin.initializeApp();
}

const evidenceAuthority = new ServerEvidenceAuthority();
const capabilityAuthority = new ServerCapabilityAuthority();
const masteryAuthority = new ServerMasteryAuthority();
const readinessAuthority = new ServerReadinessAuthority();
const treasureAuthority = new ServerCyberTreasureAuthority();
const nextMoveAuthority = new ServerNextMoveAuthority();

/**
 * 1. SERVER-SIDE EVIDENCE INGESTION & VERIFICATION
 */
export const verifyAndIngestEvidence = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  const ingestionReq: EvidenceIngestionRequest = request.data;
  return await evidenceAuthority.ingestAndVerifyEvidence(authenticatedUid, ingestionReq);
});

/**
 * 2. SERVER-SIDE CAPABILITY AUTHORITY
 */
export const evaluateAuthoritativeCapability = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  const skillKey = request.data?.skillKey;
  if (!skillKey) {
    throw new HttpsError('invalid-argument', 'skillKey parameter is required.');
  }
  return await capabilityAuthority.evaluateAuthoritativeCapability(authenticatedUid, skillKey);
});

/**
 * 3. SERVER-SIDE MASTERY AUTHORITY (7 Mastery Gates & 4 Cognitive Clusters)
 */
export const evaluateAuthoritativeMastery = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  return await masteryAuthority.evaluateAuthoritativeMastery(authenticatedUid, request.data?.assessmentId);
});

/**
 * 4. SERVER-SIDE READINESS AUTHORITY
 */
export const calculateAuthoritativeReadiness = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  const trackId = request.data?.trackId || 'soc_analyst_t2';
  return await readinessAuthority.calculateAuthoritativeReadiness(authenticatedUid, trackId);
});

/**
 * 5. SERVER-SIDE CYBER TREASURE AUTHORITY
 */
export const evaluateAndGrantCyberTreasure = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  const grantReq: TreasureGrantRequest = request.data;
  return await treasureAuthority.evaluateAndGrantTreasure(authenticatedUid, grantReq);
});

/**
 * 6. SERVER-SIDE NEXT MOVE AUTHORITY
 */
export const generateAuthoritativeNextMove = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  return await nextMoveAuthority.generateAuthoritativeNextMove(authenticatedUid, request.data?.actionId);
});

/**
 * 7. ORCHESTRATED FULL LEARNER PIPELINE
 * Evaluates evidence -> updates capability -> computes mastery -> updates readiness -> generates next move
 */
export const evaluateFullLearnerState = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  const skillKey = request.data?.skillKey || 'siem_log_triage';

  const capability = await capabilityAuthority.evaluateAuthoritativeCapability(authenticatedUid, skillKey);
  const mastery = await masteryAuthority.evaluateAuthoritativeMastery(authenticatedUid);
  const readiness = await readinessAuthority.calculateAuthoritativeReadiness(authenticatedUid);
  const nextMove = await nextMoveAuthority.generateAuthoritativeNextMove(authenticatedUid);

  return {
    authenticatedUid,
    capability,
    mastery,
    readiness,
    nextMove
  };
});
