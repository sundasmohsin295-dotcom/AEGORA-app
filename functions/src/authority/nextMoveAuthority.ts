import * as admin from 'firebase-admin';
import { CloudNextAction, CloudCapabilityState, AuthoritativeMetadata } from '../models/types';

export class ServerNextMoveAuthority {
  private db: admin.firestore.Firestore;

  constructor(db?: admin.firestore.Firestore) {
    this.db = db || admin.firestore();
  }

  /**
   * Authoritatively generates the NEXT MOVE based on limiting gates, retention decay, and mission outcomes.
   * Client-supplied actions cannot become authoritative recommendations.
   */
  public async generateAuthoritativeNextMove(
    authenticatedUid: string,
    actionId?: string
  ): Promise<CloudNextAction> {
    const aid = actionId || `move_${Date.now()}`;

    // 1. Fetch authoritative capabilities
    const capSnapshot = await this.db.collection(`learners/${authenticatedUid}/capabilities`).get();
    const capabilities: CloudCapabilityState[] = [];
    capSnapshot.forEach(doc => capabilities.push(doc.data() as CloudCapabilityState));

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
    } else if (bottleneckCap && bottleneckCap.limitingGate) {
      title = `${bottleneckCap.limitingGate} Drill: ${bottleneckCap.name}`;
      missionId = `mission_gate_${bottleneckCap.skillKey}`;
      category = 'Mistake Remediation';
      primaryGateTargeted = bottleneckCap.limitingGate;
      urgencyScore = 80;
      primaryReason = `Limiting gate detected in ${bottleneckCap.limitingGate} (${bottleneckCap.gateResults[bottleneckCap.limitingGate]?.score ?? 60}% score).`;
      expectedImpact = `Remediates limiting gate bottleneck to unlock demonstrated capability.`;
    }

    const nowIso = new Date().toISOString();
    const authorityMetadata: AuthoritativeMetadata = {
      authoritySource: 'SERVER',
      verifiedAt: nowIso,
      verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
      algorithmVersion: '3.0.0',
      sourceEvidenceIds: capabilities.flatMap(c => c.authorityMetadata.sourceEvidenceIds)
    };

    const action: CloudNextAction = {
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
