import { getFunctionsInstance, httpsCallable } from '../firebase/firebaseClient';
import {
  CloudEvidenceItem,
  CloudCapabilityState,
  CloudMasteryAssessment,
  CloudCyberTreasure,
  CloudNextAction,
  CareerSignal
} from '../types/platform';

export class ServerAuthorityClient {
  /**
   * Submits evidence to the trusted server authority backend for verification.
   */
  public static async submitAndVerifyEvidence(payload: {
    evidenceId: string;
    attemptId: string;
    missionId: string;
    skillKey: string;
    evidenceType: string;
    payloadRaw: string;
    integrityDigest: string;
    clientClaimedLearnerId?: string;
  }): Promise<CloudEvidenceItem> {
    const functions = getFunctionsInstance();
    if (!functions) {
      throw new Error('Firebase Functions is not configured.');
    }
    const callFn = httpsCallable<typeof payload, CloudEvidenceItem>(functions, 'verifyAndIngestEvidence');
    const result = await callFn(payload);
    return result.data;
  }

  /**
   * Requests server evaluation of capability state from verified evidence.
   */
  public static async requestAuthoritativeCapability(skillKey: string): Promise<CloudCapabilityState> {
    const functions = getFunctionsInstance();
    if (!functions) {
      throw new Error('Firebase Functions is not configured.');
    }
    const callFn = httpsCallable<{ skillKey: string }, CloudCapabilityState>(functions, 'evaluateAuthoritativeCapability');
    const result = await callFn({ skillKey });
    return result.data;
  }

  /**
   * Requests server evaluation of 7-gate mastery assessment.
   */
  public static async requestAuthoritativeMastery(assessmentId?: string): Promise<CloudMasteryAssessment> {
    const functions = getFunctionsInstance();
    if (!functions) {
      throw new Error('Firebase Functions is not configured.');
    }
    const callFn = httpsCallable<{ assessmentId?: string }, CloudMasteryAssessment>(functions, 'evaluateAuthoritativeMastery');
    const result = await callFn({ assessmentId });
    return result.data;
  }

  /**
   * Requests server calculation of career readiness signal.
   */
  public static async requestAuthoritativeReadiness(trackId?: string): Promise<CareerSignal> {
    const functions = getFunctionsInstance();
    if (!functions) {
      throw new Error('Firebase Functions is not configured.');
    }
    const callFn = httpsCallable<{ trackId?: string }, CareerSignal>(functions, 'calculateAuthoritativeReadiness');
    const result = await callFn({ trackId });
    return result.data;
  }

  /**
   * Requests server evaluation and grant of Cyber Treasure.
   */
  public static async requestAuthoritativeCyberTreasure(payload: {
    treasureId: string;
    title: string;
    category: string;
    evidenceId: string;
  }): Promise<CloudCyberTreasure> {
    const functions = getFunctionsInstance();
    if (!functions) {
      throw new Error('Firebase Functions is not configured.');
    }
    const callFn = httpsCallable<typeof payload, CloudCyberTreasure>(functions, 'evaluateAndGrantCyberTreasure');
    const result = await callFn(payload);
    return result.data;
  }

  /**
   * Requests server generation of authoritative NEXT MOVE.
   */
  public static async requestAuthoritativeNextMove(actionId?: string): Promise<CloudNextAction> {
    const functions = getFunctionsInstance();
    if (!functions) {
      throw new Error('Firebase Functions is not configured.');
    }
    const callFn = httpsCallable<{ actionId?: string }, CloudNextAction>(functions, 'generateAuthoritativeNextMove');
    const result = await callFn({ actionId });
    return result.data;
  }
}
