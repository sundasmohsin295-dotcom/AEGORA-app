/**
 * AEGORA Cross-Platform Core Domain Types
 * Strictly aligned with Android Kotlin platform contracts.
 */

export type PlatformClient = 'ANDROID' | 'WEB';

export interface CanonicalAegoraIdentity {
  canonicalUserId: string; // usr_<sanitizedUid>
  canonicalLearnerId: string; // operator_<sanitizedUid>
  providerUid: string;
  provider: string;
  email?: string | null;
  displayName: string;
  clientType: PlatformClient;
  authenticatedAt: number;
}

export type CognitiveClusterType =
  | 'Foundation'
  | 'Active Defense'
  | 'Generalization & Stress'
  | 'Metacognitive & Strategic';

export const COGNITIVE_CLUSTERS: readonly CognitiveClusterType[] = [
  'Foundation',
  'Active Defense',
  'Generalization & Stress',
  'Metacognitive & Strategic'
] as const;

export interface ClusterMetrics {
  name: CognitiveClusterType;
  subtitle: string;
  score: number; // 0 - 100
  weight: number;
  status: 'VERIFIED' | 'EMERGING' | 'AT_RISK' | 'BOTTLENECK';
  verifiedCapabilitiesCount: number;
}

export interface PredictiveNextMove {
  id: string;
  title: string;
  missionId: string;
  category: string;
  primaryGateTargeted: string;
  estimatedMinutes: number;
  reason: string;
  expectedImpact: string;
  urgency: 'HIGH' | 'MEDIUM' | 'NORMAL';
}

export interface CyberTreasureItem {
  id: string;
  title: string;
  category: string;
  evidenceHash: string;
  demonstratedScore: number;
  unlockedAt: string;
  verifiedStatus: 'CRYPTOGRAPHICALLY_VERIFIED' | 'ACADEMIC_EVALUATION';
}

export interface CareerSignal {
  targetRole: string;
  currentReadinessScore: number; // 0 - 100
  daysToReadiness: number;
  verifiedProofCount: number;
  criticalGaps: string[];
  nextMilestones: string[];
}

export interface InvestigationTimelineEvent {
  id: string;
  timestamp: string;
  eventType: string;
  sourceHost: string;
  processOrUser: string;
  summary: string;
  rawLog: string;
  isSuspicious: boolean;
}

export interface InvestigationQuestion {
  id: string;
  stepNumber: number;
  questionText: string;
  options: string[];
  correctOptionIndex: number;
  hints: {
    conceptual: string;
    evidence: string;
    direction: string;
    fullExplanation: string;
  };
}

export interface MissionState {
  missionId: string;
  title: string;
  objective: string;
  targetDomain: string;
  events: InvestigationTimelineEvent[];
  questions: InvestigationQuestion[];
}

export interface MissionValidationOutcome {
  isPassed: boolean;
  scorePercent: number;
  evidenceHash?: string;
  remediationAdvice?: string;
  reasoningFeedback: string;
  capabilityKey?: string;
}

// Canonical Cloud Data Models (Aligned with Android and Firestore Security Rules)
export interface CloudLearnerDocument {
  firebaseAuthUid: string;
  ownerAuthUid: string;
  canonicalLearnerId: string;
  email?: string | null;
  displayName: string;
  createdAt: number;
  lastActiveAt: number;
}

export interface CloudMissionAttempt {
  attemptId: string;
  missionId: string;
  ownerAuthUid: string;
  status: 'INITIALIZED' | 'IN_PROGRESS' | 'SUBMITTED' | 'EVALUATED';
  clientStartedAt: number;
  clientCompletedAt?: number;
  userInputs: Record<string, unknown>;
  evaluatedScore?: number;
  outcome?: 'PASS' | 'FAIL';
  verifiedOutcome?: boolean;
  masteryAwarded?: boolean;
  revision: number;
}

export interface CloudEvidenceItem {
  evidenceId: string;
  ownerAuthUid: string;
  attemptId: string;
  skillKey: string;
  evidenceType: string;
  rawPayload: Record<string, unknown>;
  clientDigest: string; // SHA-256 for integrity check only (NOT a digital signature)
  serverVerificationState: 'PENDING_VERIFICATION' | 'VERIFIED' | 'REJECTED';
  verified: boolean;
  sourcePlatform: PlatformClient;
  clientTimestamp: number;
  serverTimestamp?: number;
}

export interface CloudCapabilityState {
  skillKey: string;
  ownerAuthUid: string;
  level: 'NOVICE' | 'COMPETENT' | 'PROFICIENT' | 'EXPERT' | 'MASTERED';
  demonstratedState: boolean;
  verifiedState: boolean;
  confidence: number;
  evidenceCount: number;
  lastDemonstratedAt: number;
  revision: number;
}

export interface CloudMasteryAssessment {
  assessmentId: string;
  ownerAuthUid: string;
  skillKey: string;
  masteryLevel: string;
  confidenceScore: number;
  evaluationMethod: 'AUTOMATED_BENCHMARK' | 'HUMAN_EXPERT' | 'MULTI_MODAL_EVAL';
  evidenceReferences: string[];
  evaluatedAt: number;
}

export interface CloudCyberTreasure {
  treasureId: string;
  ownerAuthUid: string;
  title: string;
  rarity: 'COMMON' | 'RARE' | 'EPIC' | 'LEGENDARY';
  isUnlocked: boolean;
  unlockAuthority: string;
  grantReceiptToken?: string;
  unlockedAt: number;
}

export interface CloudNextAction {
  actionId: string;
  ownerAuthUid: string;
  targetSkillKey: string;
  prescribedMissionId: string;
  priority: 'HIGH' | 'MEDIUM' | 'NORMAL';
  reason: string;
  userStatus: 'PENDING' | 'ACCEPTED' | 'DISMISSED';
  acknowledgedAt?: number;
  dismissedAt?: number;
  generatedAt: number;
  expiresAt: number;
}

