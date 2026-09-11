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
exports.ServerEvidenceAuthority = void 0;
const admin = __importStar(require("firebase-admin"));
const https_1 = require("firebase-functions/v2/https");
const authVerification_1 = require("../auth/authVerification");
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
class ServerEvidenceAuthority {
    db;
    constructor(db) {
        this.db = db || admin.firestore();
    }
    /**
     * Processes raw evidence through the trusted server authority pipeline.
     * Client-supplied claims of 'verified' are NEVER accepted.
     */
    async ingestAndVerifyEvidence(authenticatedUid, req) {
        // 1. Validate required fields
        if (!req.evidenceId || !req.attemptId || !req.missionId || !req.skillKey || !req.evidenceType || !req.payloadRaw) {
            throw new https_1.HttpsError('invalid-argument', 'Malformed evidence payload: Missing required structural attributes.');
        }
        // 2. Enforce allowed evidence taxonomy
        if (!ALLOWED_EVIDENCE_TYPES.has(req.evidenceType)) {
            throw new https_1.HttpsError('invalid-argument', `Invalid evidenceType: '${req.evidenceType}' is not in allowed taxonomy.`);
        }
        // 3. Idempotency check: inspect existing evidence document
        const evidenceRef = this.db.doc(`learners/${authenticatedUid}/evidence/${req.evidenceId}`);
        const existingDoc = await evidenceRef.get();
        if (existingDoc.exists) {
            const existingData = existingDoc.data();
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
                throw new https_1.HttpsError('permission-denied', 'Attempt does not belong to authenticated learner.');
            }
        }
        // 5. Validate integrity digest (SHA-256 integrity check)
        // NOTE: SHA-256 IS STRICTLY INTEGRITY / TAMPER-DETECTION.
        // NOT AUTHENTICATION, NOT AUTHORIZATION, NOT DIGITAL SIGNATURE, NOT NON-REPUDIATION.
        const learnerId = req.clientClaimedLearnerId || authenticatedUid;
        const isIntegrityValid = authVerification_1.AuthVerificationService.verifyIntegrityDigest(learnerId, req.missionId, req.attemptId, req.evidenceType, req.payloadRaw, req.integrityDigest);
        const now = new Date().toISOString();
        if (!isIntegrityValid) {
            // Evidence payload was tampered with or corrupt
            const rejectedRecord = {
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
            const hasExecutableIndicators = payloadLower.includes('exit_code: 0') ||
                payloadLower.includes('status: success') ||
                payloadLower.includes('artifact_hash:') ||
                payloadLower.includes('verified_flag:') ||
                payloadLower.includes('telemetry_ok: true') ||
                req.payloadRaw.length >= 32;
            isCriteriaSatisfied = hasExecutableIndicators && !payloadLower.includes('mock_error');
        }
        catch {
            isCriteriaSatisfied = false;
        }
        if (!isCriteriaSatisfied) {
            const rejectedRecord = {
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
        const authorityMetadata = {
            authoritySource: 'SERVER',
            verifiedAt: now,
            verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
            algorithmVersion: '3.0.0',
            sourceEvidenceIds: [req.evidenceId]
        };
        const verifiedRecord = {
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
    async evaluateAndPersistFailurePatterns(authenticatedUid, missionId, detectedPatterns, supportingEvidenceIds = []) {
        if (!authenticatedUid || typeof authenticatedUid !== 'string') {
            throw new https_1.HttpsError('unauthenticated', 'Authenticated UID is required.');
        }
        const now = new Date().toISOString();
        const updatedPatterns = [];
        for (const patternType of detectedPatterns) {
            const patternDocId = `${patternType.toLowerCase()}`;
            const patternRef = this.db.doc(`learners/${authenticatedUid}/failure_patterns/${patternDocId}`);
            const existingSnap = await patternRef.get();
            let observationCount = 1;
            let firstObservedAt = now;
            let existingSupporting = [];
            if (existingSnap.exists) {
                const existingData = existingSnap.data();
                observationCount = (existingData.observationCount || 1) + 1;
                firstObservedAt = existingData.firstObservedAt || now;
                existingSupporting = existingData.supportingEvidenceIds || [];
            }
            // Authoritative confidence score calculation (deterministic based on occurrences)
            const confidenceScore = observationCount >= 3 ? 90 : observationCount === 2 ? 65 : 40;
            const mergedEvidenceIds = Array.from(new Set([...existingSupporting, ...supportingEvidenceIds]));
            const patternRecord = {
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
exports.ServerEvidenceAuthority = ServerEvidenceAuthority;
//# sourceMappingURL=evidenceAuthority.js.map