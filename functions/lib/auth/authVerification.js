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
exports.AuthVerificationService = void 0;
const admin = __importStar(require("firebase-admin"));
const https_1 = require("firebase-functions/v2/https");
const crypto = __importStar(require("crypto"));
/**
 * Trusted Identity & Security Helpers for AEGORA Server Authority
 */
class AuthVerificationService {
    /**
     * Verifies that the caller has a valid, authenticated Firebase UID.
     * If client supplies a targetAuthUid or learnerId, strictly verifies it matches the authenticated UID.
     * Cross-user writes or identity spoofing are unconditionally rejected.
     */
    static verifyCaller(request, requestedUid) {
        if (!request.auth || !request.auth.uid) {
            throw new https_1.HttpsError('unauthenticated', 'Unauthenticated request: Valid Firebase authentication credentials required.');
        }
        const authenticatedUid = request.auth.uid;
        if (requestedUid && requestedUid !== authenticatedUid) {
            throw new https_1.HttpsError('permission-denied', `Cross-user violation: Authenticated UID '${authenticatedUid}' cannot act on behalf of '${requestedUid}'.`);
        }
        return authenticatedUid;
    }
    /**
     * Verifies an ID token passed via standard HTTP headers (e.g. Bearer token)
     */
    static async verifyHttpBearerToken(authHeader) {
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            throw new https_1.HttpsError('unauthenticated', 'Missing or malformed Authorization header with Bearer token.');
        }
        const idToken = authHeader.split('Bearer ')[1];
        try {
            const decodedToken = await admin.auth().verifyIdToken(idToken);
            if (!decodedToken.uid) {
                throw new https_1.HttpsError('unauthenticated', 'Invalid token: UID missing from payload.');
            }
            return decodedToken.uid;
        }
        catch (err) {
            throw new https_1.HttpsError('unauthenticated', `ID Token verification failed: ${err.message}`);
        }
    }
    static generateDigest(rawString) {
        return 'sha256:' + crypto.createHash('sha256').update(rawString, 'utf8').digest('hex');
    }
    /**
     * Evaluates SHA-256 integrity digest for evidence payload.
     * NOTE: SHA-256 IS STRICTLY INTEGRITY/TAMPER-DETECTION.
     * SHA-256 IS NOT AUTHENTICATION, NOT AUTHORIZATION, NOT DIGITAL SIGNATURE, AND NOT NON-REPUDIATION.
     */
    static verifyIntegrityDigest(learnerId, missionId, attemptId, evidenceType, payloadRaw, providedDigest) {
        const rawString = `${learnerId}:${missionId}:${attemptId}:${evidenceType}:${payloadRaw}`;
        const calculatedDigest = 'sha256:' + crypto.createHash('sha256').update(rawString, 'utf8').digest('hex');
        return calculatedDigest === providedDigest;
    }
}
exports.AuthVerificationService = AuthVerificationService;
//# sourceMappingURL=authVerification.js.map