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
exports.ServerCapabilityAuthority = void 0;
const admin = __importStar(require("firebase-admin"));
const https_1 = require("firebase-functions/v2/https");
class ServerCapabilityAuthority {
    db;
    constructor(db) {
        this.db = db || admin.firestore();
    }
    /**
     * Authoritatively evaluates learner capability from verified evidence.
     * Client claims of demonstrated capability are strictly ignored.
     */
    async evaluateAuthoritativeCapability(authenticatedUid, skillKey) {
        if (!skillKey) {
            throw new https_1.HttpsError('invalid-argument', 'skillKey is required.');
        }
        // 1. Fetch all VERIFIED evidence for this skill from Firestore
        const evidenceSnapshot = await this.db
            .collection(`learners/${authenticatedUid}/evidence`)
            .where('skillKey', '==', skillKey)
            .where('serverVerificationState', '==', 'VERIFIED')
            .get();
        const verifiedItems = [];
        evidenceSnapshot.forEach(doc => {
            verifiedItems.push(doc.data());
        });
        const now = new Date();
        const nowIso = now.toISOString();
        // 2. Derive Gate Scores from verified evidence
        const verifiedCount = verifiedItems.length;
        let baseScore = 40;
        if (verifiedCount >= 3) {
            baseScore = 88;
        }
        else if (verifiedCount === 2) {
            baseScore = 78;
        }
        else if (verifiedCount === 1) {
            baseScore = 75;
        }
        const gateResults = {};
        const gates = [
            'UNDERSTAND',
            'RECALL',
            'APPLY',
            'INVESTIGATE',
            'TRANSFER',
            'EXPLAIN',
            'UNCERTAINTY_RESILIENCE'
        ];
        let limitingGate = null;
        let minScore = 100;
        for (const gate of gates) {
            // Gate score adjustments based on evidence volume and gate requirements
            let gScore = baseScore;
            if (gate === 'INVESTIGATE')
                gScore = Math.max(35, baseScore - 5);
            if (gate === 'TRANSFER')
                gScore = Math.max(30, baseScore - 10);
            if (gate === 'UNCERTAINTY_RESILIENCE')
                gScore = Math.max(40, baseScore - 8);
            const isPassed = gScore >= 75;
            if (gScore < minScore) {
                minScore = gScore;
                limitingGate = gate;
            }
            gateResults[gate] = {
                gate,
                score: gScore,
                isPassed,
                confidence: gScore,
                limitingFactor: !isPassed ? `${gate} score (${gScore}%) below benchmark (75%)` : null,
                positiveFactor: isPassed ? `${gate} successfully verified via authoritative server evidence` : null
            };
        }
        // 3. Retention decay calculation
        let daysSinceLastVerified = 0;
        let lastVerifiedAt = undefined;
        if (verifiedItems.length > 0) {
            const dates = verifiedItems
                .map(i => (i.verifiedAt ? new Date(i.verifiedAt).getTime() : 0))
                .filter(t => t > 0);
            if (dates.length > 0) {
                const latestTime = Math.max(...dates);
                lastVerifiedAt = new Date(latestTime).toISOString();
                daysSinceLastVerified = Math.max(0, Math.floor((now.getTime() - latestTime) / (1000 * 60 * 60 * 24)));
            }
        }
        let retentionRisk = 'LOW';
        if (daysSinceLastVerified > 30) {
            retentionRisk = 'CRITICAL';
        }
        else if (daysSinceLastVerified > 14) {
            retentionRisk = 'HIGH';
        }
        else if (daysSinceLastVerified > 7) {
            retentionRisk = 'MEDIUM';
        }
        // 4. Demonstrated status is TRUE only when verified evidence exists and passing threshold met
        const isDemonstrated = verifiedCount > 0 && baseScore >= 75;
        const authorityMetadata = {
            authoritySource: 'SERVER',
            verifiedAt: nowIso,
            verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
            algorithmVersion: '3.0.0',
            sourceEvidenceIds: verifiedItems.map(i => i.evidenceId)
        };
        const capabilityState = {
            skillKey,
            ownerAuthUid: authenticatedUid,
            name: formatSkillName(skillKey),
            category: categorizeSkill(skillKey),
            currentConfidence: isDemonstrated ? baseScore : Math.min(50, baseScore),
            isDemonstrated,
            retentionRisk,
            daysSinceLastVerified,
            limitingGate: isDemonstrated ? null : limitingGate,
            gateResults,
            lastVerifiedAt,
            authorityMetadata
        };
        // 5. Authoritative Write to Firestore via Admin SDK
        const capRef = this.db.doc(`learners/${authenticatedUid}/capabilities/${skillKey}`);
        await capRef.set(capabilityState, { merge: true });
        return capabilityState;
    }
}
exports.ServerCapabilityAuthority = ServerCapabilityAuthority;
function formatSkillName(key) {
    return key
        .split('_')
        .map(w => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
        .join(' ');
}
function categorizeSkill(key) {
    const k = key.toLowerCase();
    if (k.includes('siem') || k.includes('triage') || k.includes('incident') || k.includes('detection')) {
        return 'Active Defense';
    }
    if (k.includes('pcap') || k.includes('network') || k.includes('protocol')) {
        return 'Foundation';
    }
    if (k.includes('malware') || k.includes('forensic') || k.includes('dump')) {
        return 'Generalization & Stress';
    }
    return 'Metacognitive & Strategic';
}
//# sourceMappingURL=capabilityAuthority.js.map