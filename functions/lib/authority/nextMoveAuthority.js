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
exports.ServerNextMoveAuthority = void 0;
const admin = __importStar(require("firebase-admin"));
class ServerNextMoveAuthority {
    db;
    constructor(db) {
        this.db = db || admin.firestore();
    }
    /**
     * Authoritatively generates the NEXT MOVE based on limiting gates, retention decay, and mission outcomes.
     * Client-supplied actions cannot become authoritative recommendations.
     */
    async generateAuthoritativeNextMove(authenticatedUid, actionId) {
        const aid = actionId || `move_${Date.now()}`;
        // 1. Fetch authoritative capabilities
        const capSnapshot = await this.db.collection(`learners/${authenticatedUid}/capabilities`).get();
        const capabilities = [];
        capSnapshot.forEach(doc => capabilities.push(doc.data()));
        // 2. Identify limiting gate bottlenecks & retention risks
        const criticalDecay = capabilities.find(c => c.retentionRisk === 'CRITICAL' || c.retentionRisk === 'HIGH');
        const bottleneckCap = capabilities.find(c => !c.isDemonstrated && c.limitingGate != null);
        let title = 'Investigate a Suspicious Login: Impossible Travel & Credential Spraying';
        let missionId = 'lab_suspicious_login';
        let category = 'SOC Active Defense';
        let primaryGateTargeted = 'INVESTIGATE & CONTAIN';
        let urgencyScore = 85;
        let primaryReason = 'Active Defense cluster shows bottleneck in anomaly triage and correlation.';
        let expectedImpact = '+12% Active Defense confidence, unlocks Tier 2 Incident Responder missions';
        if (criticalDecay) {
            title = `Decay Refresher: ${criticalDecay.name}`;
            missionId = `mission_refresh_${criticalDecay.skillKey}`;
            category = 'Decay Prevention';
            primaryGateTargeted = 'RECALL & APPLY';
            urgencyScore = 95;
            primaryReason = `Retention decay detected: Last verified ${criticalDecay.daysSinceLastVerified} days ago. Current confidence decayed to ${criticalDecay.currentConfidence}%.`;
            expectedImpact = `Restores ${criticalDecay.name} to verified retention standard (85%+)`;
        }
        else if (bottleneckCap && bottleneckCap.limitingGate) {
            title = `${bottleneckCap.limitingGate} Drill: ${bottleneckCap.name}`;
            missionId = `mission_gate_${bottleneckCap.skillKey}`;
            category = 'Mistake Remediation';
            primaryGateTargeted = bottleneckCap.limitingGate;
            urgencyScore = 80;
            primaryReason = `Limiting gate detected in ${bottleneckCap.limitingGate} (${bottleneckCap.gateResults[bottleneckCap.limitingGate]?.score ?? 60}% score).`;
            expectedImpact = `Remediates limiting gate bottleneck to unlock demonstrated capability.`;
        }
        const nowIso = new Date().toISOString();
        const authorityMetadata = {
            authoritySource: 'SERVER',
            verifiedAt: nowIso,
            verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
            algorithmVersion: '3.0.0',
            sourceEvidenceIds: capabilities.flatMap(c => c.authorityMetadata.sourceEvidenceIds)
        };
        const action = {
            actionId: aid,
            ownerAuthUid: authenticatedUid,
            title,
            missionId,
            category,
            primaryGateTargeted,
            estimatedMinutes: 20,
            urgencyScore,
            primaryReason,
            expectedImpact,
            userStatus: 'NEW',
            createdAt: nowIso,
            authorityMetadata
        };
        // 3. Authoritative Write via Admin SDK
        await this.db.doc(`learners/${authenticatedUid}/next_actions/${aid}`).set(action, { merge: true });
        return action;
    }
}
exports.ServerNextMoveAuthority = ServerNextMoveAuthority;
//# sourceMappingURL=nextMoveAuthority.js.map