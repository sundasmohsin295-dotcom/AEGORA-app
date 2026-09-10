import * as admin from 'firebase-admin';
import { onCall, HttpsError } from 'firebase-functions/v2/https';
import { AuthVerificationService } from './auth/authVerification';
import { ServerEvidenceAuthority, EvidenceIngestionRequest } from './authority/evidenceAuthority';
import { ServerCapabilityAuthority } from './authority/capabilityAuthority';
import { ServerMasteryAuthority } from './authority/masteryAuthority';
import { ServerReadinessAuthority } from './authority/readinessAuthority';
import { ServerCyberTreasureAuthority, TreasureGrantRequest } from './authority/cyberTreasureAuthority';
import { ServerNextMoveAuthority } from './authority/nextMoveAuthority';
import { SubscriptionAuthority } from './authority/subscriptionAuthority';
import { ServerAdaptiveAdversaryAuthority } from './authority/adaptiveAdversaryAuthority';
import { ServerAiHallucinationAuthority } from './authority/aiHallucinationAuthority';
import { onRequest } from 'firebase-functions/v2/https';

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
const subscriptionAuthority = new SubscriptionAuthority();
const adversaryAuthority = new ServerAdaptiveAdversaryAuthority();
const aiHallucinationAuthority = new ServerAiHallucinationAuthority(undefined, evidenceAuthority);

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

/**
 * 8. SERVER-AUTHORITATIVE SUBSCRIPTION STATE VERIFICATION
 * Never trusts client-reported tier or entitlements. Derives state directly from
 * authoritative Firestore record or verified server lookup.
 */
export const getOrSyncSubscriptionState = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  return await subscriptionAuthority.getAuthoritativeSubscription(authenticatedUid);
});

/**
 * 9. REVENUECAT SERVER-TO-SERVER WEBHOOK HANDLER
 * Idempotent, tamper-proof webhook processor that persists authoritative subscription state.
 */
export const revenuecatWebhook = onRequest(async (req, res) => {
  if (req.method !== 'POST') {
    res.status(405).json({ error: 'METHOD_NOT_ALLOWED' });
    return;
  }

  const authHeader = req.headers.authorization || (req.headers['x-revenuecat-webhook-auth'] as string | undefined);
  const result = await subscriptionAuthority.processRevenueCatWebhook(req.body, authHeader);

  if (!result.success) {
    if (result.reason === 'UNAUTHORIZED_WEBHOOK') {
      res.status(401).json({ error: 'UNAUTHORIZED' });
      return;
    }
    res.status(400).json({ error: result.reason || 'BAD_REQUEST' });
    return;
  }

  res.status(200).json({ status: 'OK', processed: result.processed, reason: result.reason });
});

/**
 * 10. SERVER-AUTHORITATIVE FAILURE PATTERNS
 * Derives and records failure patterns strictly within caller's authenticated learner scope.
 * Client claims of confidence or forged failure types are discarded.
 */
export const recordAuthoritativeFailurePatterns = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  const missionId = request.data?.missionId;
  const detectedPatterns = request.data?.detectedPatterns || [];
  const supportingEvidenceIds = request.data?.supportingEvidenceIds || [];

  if (!missionId) {
    throw new HttpsError('invalid-argument', 'missionId is required.');
  }

  return await evidenceAuthority.evaluateAndPersistFailurePatterns(
    authenticatedUid,
    missionId,
    detectedPatterns,
    supportingEvidenceIds
  );
});

/**
 * 11. SERVER-AUTHORITATIVE ADAPTIVE CHALLENGE GENERATION
 * Derives challenge strictly from caller's authoritative failure patterns.
 * Client claims of difficulty, target failure mode, or trap states are rejected.
 */
export const generateAuthoritativeAdaptiveChallenge = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  return await adversaryAuthority.generateAuthoritativeAdaptiveChallenge(authenticatedUid);
});

/**
 * 12. SERVER-AUTHORITATIVE AI HALLUCINATION & CLAIM VERIFICATION
 * Client is NEVER authoritative for claim verification, hidden trap state, or ground truth.
 * Validates learner accept/challenge against authoritative mission evidence.
 */
export const verifyAiClaimDecision = onCall(async (request) => {
  const authenticatedUid = AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
  return await aiHallucinationAuthority.verifyAiClaimDecision(authenticatedUid, request.data);
});



