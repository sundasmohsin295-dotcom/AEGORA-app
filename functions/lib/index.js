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
exports.evaluateFullLearnerState = exports.generateAuthoritativeNextMove = exports.evaluateAndGrantCyberTreasure = exports.calculateAuthoritativeReadiness = exports.evaluateAuthoritativeMastery = exports.evaluateAuthoritativeCapability = exports.verifyAndIngestEvidence = void 0;
const admin = __importStar(require("firebase-admin"));
const https_1 = require("firebase-functions/v2/https");
const authVerification_1 = require("./auth/authVerification");
const evidenceAuthority_1 = require("./authority/evidenceAuthority");
const capabilityAuthority_1 = require("./authority/capabilityAuthority");
const masteryAuthority_1 = require("./authority/masteryAuthority");
const readinessAuthority_1 = require("./authority/readinessAuthority");
const cyberTreasureAuthority_1 = require("./authority/cyberTreasureAuthority");
const nextMoveAuthority_1 = require("./authority/nextMoveAuthority");
// Initialize Firebase Admin SDK ONLY inside trusted server execution environment.
// Never expose Admin credentials to clients or commit private keys.
if (!admin.apps.length) {
    admin.initializeApp();
}
const evidenceAuthority = new evidenceAuthority_1.ServerEvidenceAuthority();
const capabilityAuthority = new capabilityAuthority_1.ServerCapabilityAuthority();
const masteryAuthority = new masteryAuthority_1.ServerMasteryAuthority();
const readinessAuthority = new readinessAuthority_1.ServerReadinessAuthority();
const treasureAuthority = new cyberTreasureAuthority_1.ServerCyberTreasureAuthority();
const nextMoveAuthority = new nextMoveAuthority_1.ServerNextMoveAuthority();
/**
 * 1. SERVER-SIDE EVIDENCE INGESTION & VERIFICATION
 */
exports.verifyAndIngestEvidence = (0, https_1.onCall)(async (request) => {
    const authenticatedUid = authVerification_1.AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
    const ingestionReq = request.data;
    return await evidenceAuthority.ingestAndVerifyEvidence(authenticatedUid, ingestionReq);
});
/**
 * 2. SERVER-SIDE CAPABILITY AUTHORITY
 */
exports.evaluateAuthoritativeCapability = (0, https_1.onCall)(async (request) => {
    const authenticatedUid = authVerification_1.AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
    const skillKey = request.data?.skillKey;
    if (!skillKey) {
        throw new https_1.HttpsError('invalid-argument', 'skillKey parameter is required.');
    }
    return await capabilityAuthority.evaluateAuthoritativeCapability(authenticatedUid, skillKey);
});
/**
 * 3. SERVER-SIDE MASTERY AUTHORITY (7 Mastery Gates & 4 Cognitive Clusters)
 */
exports.evaluateAuthoritativeMastery = (0, https_1.onCall)(async (request) => {
    const authenticatedUid = authVerification_1.AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
    return await masteryAuthority.evaluateAuthoritativeMastery(authenticatedUid, request.data?.assessmentId);
});
/**
 * 4. SERVER-SIDE READINESS AUTHORITY
 */
exports.calculateAuthoritativeReadiness = (0, https_1.onCall)(async (request) => {
    const authenticatedUid = authVerification_1.AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
    const trackId = request.data?.trackId || 'soc_analyst_t2';
    return await readinessAuthority.calculateAuthoritativeReadiness(authenticatedUid, trackId);
});
/**
 * 5. SERVER-SIDE CYBER TREASURE AUTHORITY
 */
exports.evaluateAndGrantCyberTreasure = (0, https_1.onCall)(async (request) => {
    const authenticatedUid = authVerification_1.AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
    const grantReq = request.data;
    return await treasureAuthority.evaluateAndGrantTreasure(authenticatedUid, grantReq);
});
/**
 * 6. SERVER-SIDE NEXT MOVE AUTHORITY
 */
exports.generateAuthoritativeNextMove = (0, https_1.onCall)(async (request) => {
    const authenticatedUid = authVerification_1.AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
    return await nextMoveAuthority.generateAuthoritativeNextMove(authenticatedUid, request.data?.actionId);
});
/**
 * 7. ORCHESTRATED FULL LEARNER PIPELINE
 * Evaluates evidence -> updates capability -> computes mastery -> updates readiness -> generates next move
 */
exports.evaluateFullLearnerState = (0, https_1.onCall)(async (request) => {
    const authenticatedUid = authVerification_1.AuthVerificationService.verifyCaller(request, request.data?.targetAuthUid);
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
//# sourceMappingURL=index.js.map