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
