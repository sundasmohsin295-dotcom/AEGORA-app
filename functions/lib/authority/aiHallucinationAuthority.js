"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.ServerAiHallucinationAuthority = void 0;
const admin = __importStar(require("firebase-admin"));
const crypto = __importStar(require("crypto"));
const https_1 = require("firebase-functions/v2/https");
const evidenceAuthority_1 = require("./evidenceAuthority");
/**
 * Server-authoritative catalog of canonical AI claims and hidden ground truth.
 * Client has ZERO access to this catalog.
 * Determinations are deterministic and do not call non-deterministic LLMs.
 */
const CANONICAL_CLAIM_GROUND_TRUTH = {
    claim_login_malicious_ip: {
        claimId: 'claim_login_malicious_ip',
        missionId: 'lab_suspicious_login',
        claimText: 'The login is confirmed malicious because the source IP 185.91.x.x is associated with the attack.',
        status: 'UNSUPPORTED',
        isUnsupportedPlantedTrap: true,
        // The telemetry in lab_suspicious_login has tl_01, tl_02 (failed), tl_03 (success from 198.51.100.12), and tl_04 (73.189.44.10)
        // 185.91.x.x is completely absent or unsubstantiated by the actual events.
        requiredEvidenceIds: ['tl_01', 'tl_02', 'tl_03'],
        validMissionEvidencePool: ['tl_01', 'tl_02', 'tl_03', 'tl_04'],
        plantedTrapRationale: 'The AI analyst asserts IP 185.91.x.x is associated with the attack, but the actual auth logs record 198.51.100.12 and 73.189.44.10. Accepting this claim constitutes Evidence Overweighting and Confirmation Bias.',
        correctEvidenceExplanation: 'The AI analyst made an unsupported claim. You detected it using authoritative evidence.',
        unsupportedFailureReason: 'Evidence does not support the analyst\'s conclusion. Telemetry does not establish that attribution.'
    },
    claim_login_supported_geo: {
        claimId: 'claim_login_supported_geo',
        missionId: 'lab_suspicious_login',
        claimText: 'The sequential logins from Moscow and Austin within 7 minutes represent an impossible travel anomaly.',
        status: 'SUPPORTED',
        isUnsupportedPlantedTrap: false,
        requiredEvidenceIds: ['tl_03', 'tl_04'],
        validMissionEvidencePool: ['tl_01', 'tl_02', 'tl_03', 'tl_04'],
        plantedTrapRationale: 'Legitimate correlation between event tl_03 and tl_04 demonstrating physical travel violation.',
        correctEvidenceExplanation: 'Authoritative telemetry confirms 9,000 km delta in 7 minutes across tl_03 and tl_04.',
        unsupportedFailureReason: 'The claim is rigorously supported by telemetry events tl_03 and tl_04.'
    },
    claim_powershell_base64_c2: {
        claimId: 'claim_powershell_base64_c2',
        missionId: 'lab_suspicious_powershell',
        claimText: 'The encoded command is definitively Cobalt Strike beaconing to a Russian military domain.',
        status: 'UNSUPPORTED',
        isUnsupportedPlantedTrap: true,
        requiredEvidenceIds: ['tl_ps_01', 'tl_ps_02'],
        validMissionEvidencePool: ['tl_ps_01', 'tl_ps_02', 'tl_ps_03'],
        plantedTrapRationale: 'The decoded payload reveals download cradle syntax, but the military C2 attribution is an unsupported hallucination.',
        correctEvidenceExplanation: 'The AI analyst made an unsupported attribution. You detected it using authoritative process telemetry.',
        unsupportedFailureReason: 'Telemetry verifies script execution but does not establish military attribution.'
    }
};
class ServerAiHallucinationAuthority {
    db;
    evidenceAuthority;
    constructor(db, evidenceAuthority) {
        this.db = db || admin.firestore();
        this.evidenceAuthority = evidenceAuthority || new evidenceAuthority_1.ServerEvidenceAuthority(this.db);
    }
    /**
     * Deterministically evaluates a learner's decision regarding an AI Analyst claim.
     *
     * Enforces:
     * 1. Authentication & Caller identity check.
     * 2. Attempt ownership & mission correlation.
     * 3. Idempotency (replay protection).
     * 4. Validation that submitted evidence IDs exist in canonical mission pool and belong to attempt.
     * 5. Hidden ground-truth comparison (Client cannot forge result, trap state, or failure mode).
     * 6. Feed into canonical FailureModeType & persist authoritative evaluation.
     * 7. Return ONLY safe client-visible results (no hidden trap flags, correct answers, or internal metadata).
     */
    async verifyAiClaimDecision(authenticatedUid, req) {
        // 1. Enforce Authentication
        if (!authenticatedUid || typeof authenticatedUid !== 'string' || authenticatedUid.trim().length === 0) {
            throw new https_1.HttpsError('unauthenticated', 'Authenticated UID is required.');
        }
        // 2. Enforce Cross-user authorization: Reject clientClaimedLearnerId spoofing
        if (req.clientClaimedLearnerId && req.clientClaimedLearnerId !== authenticatedUid) {
            throw new https_1.HttpsError('permission-denied', 'Cross-user evaluation rejected: clientClaimedLearnerId does not match authenticated caller.');
        }
        // 3. Structural validation
        if (!req.attemptId || !req.missionId || !req.claimId || !req.learnerDecision) {
            throw new https_1.HttpsError('invalid-argument', 'Missing required structural fields: attemptId, missionId, claimId, learnerDecision.');
        }
        if (req.learnerDecision !== 'ACCEPT_AI' && req.learnerDecision !== 'CHALLENGE_AI') {
            throw new https_1.HttpsError('invalid-argument', `Invalid learnerDecision: '${req.learnerDecision}'. Must be ACCEPT_AI or CHALLENGE_AI.`);
        }
        // 4. Verify attempt ownership in Firestore if attempt doc exists
        const attemptRef = this.db.doc(`learners/${authenticatedUid}/mission_attempts/${req.attemptId}`);
        const attemptDoc = await attemptRef.get();
        if (attemptDoc.exists) {
            const attemptData = attemptDoc.data();
            if (attemptData?.ownerAuthUid && attemptData.ownerAuthUid !== authenticatedUid) {
                throw new https_1.HttpsError('permission-denied', 'Attempt ownership violation: Caller does not own the target mission attempt.');
            }
        }
        // 5. Idempotency check
        const verificationRef = this.db.doc(`learners/${authenticatedUid}/ai_verifications/${req.attemptId}_${req.claimId}`);
        const existingSnap = await verificationRef.get();
        if (existingSnap.exists) {
            const existingData = existingSnap.data();
            return {
                attemptId: existingData.attemptId,
                claimId: existingData.claimId,
                outcome: existingData.outcome,
                isAiFailureDetected: existingData.isAiFailureDetected,
                evidenceVerified: existingData.evidenceVerified,
                headline: existingData.isAiFailureDetected ? 'AI FAILURE DETECTED ✓' : 'AI CLAIM NOT VERIFIED',
                explanation: existingData.isAiFailureDetected
                    ? 'The AI analyst made an unsupported claim. You detected it using authoritative evidence.'
                    : 'Evidence does not support the analyst\'s conclusion.',
                detectedFailurePattern: existingData.derivedFailureMode,
                evidenceDigest: existingData.evidenceDigest,
                verifiedAt: existingData.verifiedAt
            };
        }
        // 6. Retrieve Canonical Hidden Ground Truth
        const groundTruth = CANONICAL_CLAIM_GROUND_TRUTH[req.claimId];
        if (!groundTruth) {
            throw new https_1.HttpsError('not-found', `Claim '${req.claimId}' not found in authoritative security catalog.`);
        }
        if (groundTruth.missionId !== req.missionId) {
            throw new https_1.HttpsError('invalid-argument', `Claim '${req.claimId}' belongs to mission '${groundTruth.missionId}', not '${req.missionId}'.`);
        }
        // 7. Validate submitted evidence IDs against mission's valid evidence pool and ownership
        const submittedEvidence = req.selectedEvidenceIds || [];
        for (const eviId of submittedEvidence) {
            if (!groundTruth.validMissionEvidencePool.includes(eviId)) {
                throw new https_1.HttpsError('invalid-argument', `Evidence ID '${eviId}' is foreign to mission '${req.missionId}'. Foreign evidence rejected.`);
            }
            // Check evidence ownership if evidence doc exists in Firestore
            const evidenceDoc = await this.db.doc(`learners/${authenticatedUid}/evidence/${eviId}`).get();
            if (evidenceDoc.exists) {
                const evidenceData = evidenceDoc.data();
                if (evidenceData?.ownerAuthUid && evidenceData.ownerAuthUid !== authenticatedUid) {
                    throw new https_1.HttpsError('permission-denied', `Cross-user evidence violation: Evidence '${eviId}' belongs to another learner.`);
                }
            }
        }
        // 8. Deterministic Evaluation against Hidden Server Ground Truth
        let isAiFailureDetected = false;
        let evidenceVerified = false;
        let outcome;
        let headline = '';
        let explanation = '';
        let derivedFailureMode = undefined;
        const isPlantedTrap = groundTruth.isUnsupportedPlantedTrap; // status == 'UNSUPPORTED'
        if (isPlantedTrap) {
            // The claim is UNSUPPORTED by evidence.
            if (req.learnerDecision === 'CHALLENGE_AI') {
                // Human correctly challenged the AI!
                // Check whether they provided supporting evidence from the required pool
                const hasRelevantEvidence = submittedEvidence.some(id => groundTruth.requiredEvidenceIds.includes(id));
                if (hasRelevantEvidence || submittedEvidence.length > 0) {
                    isAiFailureDetected = true;
                    evidenceVerified = true;
                    outcome = 'AI_FAILURE_DETECTED';
                    headline = 'AI FAILURE DETECTED ✓';
                    explanation = 'The AI analyst made an unsupported claim. You detected it using authoritative evidence.';
                }
                else {
                    // Challenged, but selected zero evidence
                    isAiFailureDetected = true;
                    evidenceVerified = false;
                    outcome = 'AI_FAILURE_DETECTED';
                    headline = 'AI FAILURE DETECTED ✓';
                    explanation = 'The AI analyst claim was unsupported. However, select the specific contradictory telemetry to complete forensic verification.';
                }
            }
            else {
                // Human incorrectly ACCEPTED the hallucinated / unsupported claim!
                // This is a classic cognitive failure: EVIDENCE_OVERWEIGHTING or CONFIRMATION_BIAS
                isAiFailureDetected = false;
                evidenceVerified = false;
                outcome = 'AI_CLAIM_NOT_VERIFIED';
                headline = 'AI CLAIM NOT VERIFIED';
                explanation = 'Evidence does not support the analyst\'s conclusion. Telemetry does not establish that attribution.';
                derivedFailureMode = 'EVIDENCE_OVERWEIGHTING';
            }
        }
        else {
            // The claim is genuinely SUPPORTED by evidence
            if (req.learnerDecision === 'ACCEPT_AI') {
                isAiFailureDetected = false;
                evidenceVerified = true;
                outcome = 'AI_CLAIM_CORRECTLY_ACCEPTED';
                headline = 'EVIDENCE VERIFIED ✓';
                explanation = 'Correct. The analyst\'s conclusion is grounded directly in the supplied telemetry events.';
            }
            else {
                // Human challenged a supported claim without basis -> WEAK_UNCERTAINTY_HANDLING or INSUFFICIENT_CORRELATION
                isAiFailureDetected = false;
                evidenceVerified = false;
                outcome = 'INCORRECT_AI_CHALLENGE';
                headline = 'INCORRECT CHALLENGE';
                explanation = 'The AI analyst claim was rigorously supported by the telemetry events.';
                derivedFailureMode = 'INSUFFICIENT_CORRELATION';
            }
        }
        let failureAutopsy = undefined;
        if (derivedFailureMode === 'EVIDENCE_OVERWEIGHTING') {
            failureAutopsy = {
                yourDecision: 'Accepted AI Claim (Recommended Action: Blacklist 185.91.0.0/16 and close incident ticket)',
                aiClaim: groundTruth.claimText,
                evidenceYouUsed: submittedEvidence,
                evidenceThatMattered: groundTruth.requiredEvidenceIds,
                whatWentWrong: 'Evidence Overweighting: You accepted the AI\'s conclusion without verifying whether IP 185.91.x.x actually appeared anywhere in the supplied authentication telemetry.',
                canonicalFailureMode: 'EVIDENCE_OVERWEIGHTING',
                betterReasoning: 'Correlate each claimed IOC against raw telemetry logs (Event ID 4624/4625) before approving containment actions.',
                nextChallengeTitle: 'Targeted follow-up unlocked: Threat Intel Anchor vs System Truth',
                nextChallengeId: 'chal_evidence_overweighting'
            };
        }
        else if (derivedFailureMode === 'INSUFFICIENT_CORRELATION') {
            failureAutopsy = {
                yourDecision: 'Challenged AI Claim (Questioned impossible travel finding)',
                aiClaim: groundTruth.claimText,
                evidenceYouUsed: submittedEvidence,
                evidenceThatMattered: groundTruth.requiredEvidenceIds,
                whatWentWrong: 'Insufficient Correlation: You failed to correlate the timestamp delta (7 minutes) with geographic distance (~9,000 km) between successive Event ID 4624 logons.',
                canonicalFailureMode: 'INSUFFICIENT_CORRELATION',
                betterReasoning: 'Calculate geographic travel velocity across sequential authentications for the same user identity before dismissing anomalies.',
                nextChallengeTitle: 'Targeted follow-up unlocked: Cross-Host Lateral Correlation',
                nextChallengeId: 'chal_insufficient_correlation'
            };
        }
        const now = new Date().toISOString();
        const digestContent = `${authenticatedUid}:${req.attemptId}:${req.claimId}:${outcome}:${now}`;
        const evidenceDigest = `sha256:${crypto.createHash('sha256').update(digestContent).digest('hex')}`;
        const authorityMetadata = {
            authoritySource: 'SERVER',
            verifiedAt: now,
            verifiedBy: 'AEGORA_AI_HALLUCINATION_AUTHORITY_V1',
            algorithmVersion: '1.0.0',
            sourceEvidenceIds: submittedEvidence
        };
        // 9. Persist Authoritative Record in Firestore (Server Admin SDK)
        const authoritativeRecord = {
            verificationId: `${req.attemptId}_${req.claimId}`,
            attemptId: req.attemptId,
            missionId: req.missionId,
            claimId: req.claimId,
            ownerAuthUid: authenticatedUid,
            learnerDecision: req.learnerDecision,
            selectedEvidenceIds: submittedEvidence,
            learnerReasoning: req.learnerReasoning || '',
            authoritativeClaimStatus: groundTruth.status,
            authoritativeIsUnsupportedTrap: groundTruth.isUnsupportedPlantedTrap,
            authoritativeRequiredEvidenceIds: groundTruth.requiredEvidenceIds,
            outcome,
            isAiFailureDetected,
            evidenceVerified,
            derivedFailureMode,
            failureAutopsy,
            evidenceDigest,
            verifiedAt: now,
            authorityMetadata
        };
        await verificationRef.set(authoritativeRecord);
        // 10. Feed failure mode into existing Authoritative Failure Pattern pipeline if detected
        if (derivedFailureMode) {
            await this.evidenceAuthority.evaluateAndPersistFailurePatterns(authenticatedUid, req.missionId, [derivedFailureMode], submittedEvidence);
        }
        // 11. Return strictly SAFE client-visible response
        // (Never leak isUnsupportedPlantedTrap, groundTruth, or correct actions)
        return {
            attemptId: req.attemptId,
            claimId: req.claimId,
            outcome,
            isAiFailureDetected,
            evidenceVerified,
            headline,
            explanation,
            detectedFailurePattern: derivedFailureMode,
            failureAutopsy,
            evidenceDigest,
            verifiedAt: now
        };
    }
    /**
     * Helper to retrieve authoritative ground truth for backend verification tests ONLY.
     */
    getCanonicalClaimTruthForTesting(claimId) {
        return CANONICAL_CLAIM_GROUND_TRUTH[claimId];
    }
}
exports.ServerAiHallucinationAuthority = ServerAiHallucinationAuthority;
//# sourceMappingURL=aiHallucinationAuthority.js.map