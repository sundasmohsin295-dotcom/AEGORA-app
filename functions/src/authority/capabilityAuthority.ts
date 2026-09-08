import * as admin from 'firebase-admin';
import { HttpsError } from 'firebase-functions/v2/https';
import { CloudCapabilityState, CloudEvidenceItem, MasteryGateType, GateResult, AuthoritativeMetadata } from '../models/types';

export class ServerCapabilityAuthority {
  private db: admin.firestore.Firestore;

  constructor(db?: admin.firestore.Firestore) {
    this.db = db || admin.firestore();
  }

  /**
   * Authoritatively evaluates learner capability from verified evidence.
   * Client claims of demonstrated capability are strictly ignored.
   */
  public async evaluateAuthoritativeCapability(
    authenticatedUid: string,
    skillKey: string
  ): Promise<CloudCapabilityState> {
    if (!skillKey) {
      throw new HttpsError('invalid-argument', 'skillKey is required.');
    }

    // 1. Fetch all VERIFIED evidence for this skill from Firestore
    const evidenceSnapshot = await this.db
      .collection(`learners/${authenticatedUid}/evidence`)
      .where('skillKey', '==', skillKey)
      .where('serverVerificationState', '==', 'VERIFIED')
      .get();

    const verifiedItems: CloudEvidenceItem[] = [];
    evidenceSnapshot.forEach(doc => {
      verifiedItems.push(doc.data() as CloudEvidenceItem);
    });

    const now = new Date();
    const nowIso = now.toISOString();

    // 2. Derive Gate Scores from verified evidence
    const verifiedCount = verifiedItems.length;
    let baseScore = 40;
    if (verifiedCount >= 3) {
      baseScore = 88;
    } else if (verifiedCount === 2) {
      baseScore = 78;
    } else if (verifiedCount === 1) {
      baseScore = 75;
    }

    const gateResults: Record<string, GateResult> = {};
    const gates: MasteryGateType[] = [
      'UNDERSTAND',
      'RECALL',
      'APPLY',
      'INVESTIGATE',
      'TRANSFER',
      'EXPLAIN',
      'UNCERTAINTY_RESILIENCE'
    ];

    let limitingGate: MasteryGateType | null = null;
    let minScore = 100;

    for (const gate of gates) {
      // Gate score adjustments based on evidence volume and gate requirements
      let gScore = baseScore;
      if (gate === 'INVESTIGATE') gScore = Math.max(35, baseScore - 5);
      if (gate === 'TRANSFER') gScore = Math.max(30, baseScore - 10);
      if (gate === 'UNCERTAINTY_RESILIENCE') gScore = Math.max(40, baseScore - 8);

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
    let lastVerifiedAt: string | undefined = undefined;

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

    let retentionRisk: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' = 'LOW';
    if (daysSinceLastVerified > 30) {
      retentionRisk = 'CRITICAL';
    } else if (daysSinceLastVerified > 14) {
      retentionRisk = 'HIGH';
    } else if (daysSinceLastVerified > 7) {
      retentionRisk = 'MEDIUM';
    }

    // 4. Demonstrated status is TRUE only when verified evidence exists and passing threshold met
    const isDemonstrated = verifiedCount > 0 && baseScore >= 75;

    const authorityMetadata: AuthoritativeMetadata = {
      authoritySource: 'SERVER',
      verifiedAt: nowIso,
      verifiedBy: 'AEGORA_COGNITIVE_ENGINE_V3',
      algorithmVersion: '3.0.0',
      sourceEvidenceIds: verifiedItems.map(i => i.evidenceId)
    };

    const capabilityState: CloudCapabilityState = {
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

function formatSkillName(key: string): string {
  return key
    .split('_')
    .map(w => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
    .join(' ');
}

function categorizeSkill(key: string): string {
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
