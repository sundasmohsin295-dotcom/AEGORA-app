/**
 * Canonical Types for AEGORA Trusted Server Authority
 */

export type ServerVerificationState = 'UNVERIFIED' | 'PENDING_VERIFICATION' | 'VALIDATED' | 'VERIFIED' | 'REJECTED';

export type MasteryGateType = 
  | 'UNDERSTAND'
  | 'RECALL'
  | 'APPLY'
  | 'INVESTIGATE'
  | 'TRANSFER'
  | 'EXPLAIN'
  | 'UNCERTAINTY_RESILIENCE';

export type CognitiveClusterType =
  | 'Foundation'
  | 'Active Defense'
  | 'Generalization & Stress'
  | 'Metacognitive & Strategic';

export interface AuthoritativeMetadata {
  authoritySource: 'SERVER';
  verifiedAt: string;
  verifiedBy: string;
  algorithmVersion: string;
  sourceEvidenceIds: string[];
}

export interface CloudLearnerDocument {
  firebaseAuthUid: string;
  ownerAuthUid: string;
  canonicalLearnerId: string;
  email: string;
  displayName: string;
  createdAt: string;
  lastActiveAt: string;
  reputationScore: number;
  securityClearanceTier: string;
}

export interface CloudMissionAttempt {
  attemptId: string;
  ownerAuthUid: string;
  missionId: string;
  startedAt: string;
  completedAt?: string;
  status: 'INITIALIZED' | 'IN_PROGRESS' | 'SUBMITTED' | 'VERIFIED' | 'REJECTED';
  evaluatedScore: number;
  outcome: string;
  verifiedAt?: string;
}

export interface CloudEvidenceItem {
  evidenceId: string;
  ownerAuthUid: string;
  attemptId: string;
  missionId: string;
  skillKey: string;
  evidenceType: string;
  payloadRaw: string;
  integrityDigest: string; // SHA-256 for local integrity, NOT signature or auth
  createdAt: string;
  serverVerificationState: ServerVerificationState;
  verified: boolean;
  verifiedAt?: string;
  verifiedBy?: string;
  rejectionReason?: string;
  authorityMetadata?: AuthoritativeMetadata;
}

export interface GateResult {
  gate: MasteryGateType;
  score: number;
  isPassed: boolean;
  confidence: number;
  limitingFactor?: string | null;
  positiveFactor?: string | null;
}

export interface CloudCapabilityState {
  skillKey: string;
  ownerAuthUid: string;
  name: string;
  category: string;
  currentConfidence: number;
  isDemonstrated: boolean;
  retentionRisk: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  daysSinceLastVerified: number;
  limitingGate?: MasteryGateType | null;
  gateResults: Record<string, GateResult>;
  lastVerifiedAt?: string;
  authorityMetadata: AuthoritativeMetadata;
}

export interface ClusterMetrics {
  name: CognitiveClusterType;
  subtitle: string;
  score: number;
  weight: number;
  status: 'VERIFIED' | 'BOTTLENECK' | 'EMERGING';
  verifiedCapabilitiesCount: number;
}

export interface CloudMasteryAssessment {
  assessmentId: string;
  ownerAuthUid: string;
  skillKey?: string;
  overallScore: number;
  masteryStatus: 'EXPLORING' | 'IN_PROGRESS' | 'DEMONSTRATED' | 'VERIFIED_MASTERY';
  limitingGate?: MasteryGateType | null;
  gateResults: Record<string, GateResult>;
  clusterMetrics: ClusterMetrics[];
  evaluatedAt: string;
  authorityMetadata: AuthoritativeMetadata;
}

export interface CareerReadinessSignal {
  trackId: string;
  ownerAuthUid: string;
  targetRole: string;
  readinessScore: number;
  daysToReadiness: number;
  verifiedProofCount: number;
  criticalGaps: string[];
  nextMilestones: string[];
  lastEvaluatedAt: string;
  authorityMetadata: AuthoritativeMetadata;
}

export interface CloudCyberTreasure {
  treasureId: string;
  ownerAuthUid: string;
  title: string;
  category: string;
  evidenceId: string;
  integrityHash: string;
  demonstratedScore: number;
  unlockedAt: string;
  verifiedStatus: 'CRYPTOGRAPHICALLY_VERIFIED';
  authorityMetadata: AuthoritativeMetadata;
}

export interface CloudNextAction {
  actionId: string;
  ownerAuthUid: string;
  title: string;
  missionId: string;
  category: string;
  primaryGateTargeted: string;
  estimatedMinutes: number;
  urgencyScore: number;
  primaryReason: string;
  expectedImpact: string;
  userStatus: 'NEW' | 'ACKNOWLEDGED' | 'COMPLETED' | 'DISMISSED';
  createdAt: string;
  authorityMetadata: AuthoritativeMetadata;
}

export type SubscriptionTier = 'FREE' | 'PRO' | 'CAREER' | 'UNKNOWN';

export type FailurePatternType =
  | 'PREMATURE_ESCALATION'
  | 'EVIDENCE_OVERWEIGHTING'
  | 'CONFIRMATION_BIAS'
  | 'INSUFFICIENT_CORRELATION'
  | 'WEAK_UNCERTAINTY_HANDLING'
  | 'CONTEXT_IGNORANCE'
  | 'INCORRECT_PRIORITIZATION'
  | 'KNOWLEDGE_GAP'
  | 'REASONING_ERROR'
  | 'PROCEDURAL_ERROR'
  | 'PATTERN_RECOGNITION_ERROR'
  | 'TRANSFER_FAILURE'
  | 'OVERCONFIDENCE'
  | 'UNCERTAINTY_PARALYSIS'
  | 'INCOMPLETE_INVESTIGATION'
  | 'PREMATURE_CONCLUSION';

export interface CloudFailurePattern {
  patternId: string;
  ownerAuthUid: string;
  patternType: FailurePatternType;
  confidenceScore: number; // 0 - 100
  observationCount: number;
  lastObservedMissionId: string;
  lastObservedAt: string;
  firstObservedAt: string;
  supportingEvidenceIds: string[];
  decayHalfLifeDays: number;
  authorityMetadata: AuthoritativeMetadata;
}

export interface AuthoritativeSubscriptionState {
  ownerAuthUid: string;
  tier: SubscriptionTier;
  active: boolean;
  entitlementIdentifiers: string[];
  productIdentifier: string | null;
  expiresAt: string | null;
  provider: 'REVENUECAT' | 'SYSTEM_DEFAULT';
  customerId: string;
  checkedAt: string;
  sourceEventId?: string;
  authorityMetadata: AuthoritativeMetadata;
}

export type AdaptiveChallengeTier = 'OBSERVED' | 'REPEATED' | 'HIGH_CONFIDENCE';

export interface AdaptiveChallengePolicy {
  targetFailureMode: FailurePatternType;
  challengeTier: AdaptiveChallengeTier;
  policyObjective: string;
  adversaryRole: string;
  recoveryCriteria: string;
  evidenceRequirements: string[];
}

export interface LearnerSafeAdaptiveChallenge {
  challengeId: string;
  targetFailureMode: FailurePatternType;
  challengeTier: AdaptiveChallengeTier;
  weaknessNarrative: string;
  targetedSkill: string;
  scenarioTitle: string;
  scenarioBriefing: string;
  aiAnalystClaim: {
    analystName: string;
    claimText: string;
    assertedEvidenceIds: string[];
    recommendedAction: string;
    confidencePercentage: number;
  };
  evidencePool: Array<{
    id: string;
    timestamp: string;
    source: string;
    eventType: string;
    summary: string;
  }>;
  actionOptions: Array<{
    id: string;
    label: string;
    description: string;
  }>;
}

export interface AuthoritativeAdaptiveChallengeState {
  challengeId: string;
  ownerAuthUid: string;
  sourceFailurePatternId: string;
  targetFailureMode: FailurePatternType;
  challengeTier: AdaptiveChallengeTier;
  policy: AdaptiveChallengePolicy;
  createdAt: string;
  isUnsupportedPlantedTrap: boolean;
  authoritativeCorrectActionId: string;
  authoritativeRequiredEvidenceIds: string[];
  plantedTrapRationale: string;
  learnerSafePayload: LearnerSafeAdaptiveChallenge;
  authorityMetadata: AuthoritativeMetadata;
}

export type LearnerAiDecision = 'ACCEPT_AI' | 'CHALLENGE_AI';
export type AuthoritativeAiClaimStatus = 'UNSUPPORTED' | 'SUPPORTED';
export type VerificationOutcomeStatus =
  | 'AI_FAILURE_DETECTED'
  | 'AI_CLAIM_NOT_VERIFIED'
  | 'AI_CLAIM_CORRECTLY_ACCEPTED'
  | 'INCORRECT_AI_CHALLENGE';

export interface LearnerAiClaimVerificationRequest {
  attemptId: string;
  missionId: string;
  claimId: string;
  learnerDecision: LearnerAiDecision;
  selectedEvidenceIds: string[];
  learnerReasoning?: string;
  clientClaimedLearnerId?: string;
  // Attack vectors/forged flags (Must be rejected/ignored by server)
  forgedIsUnsupportedPlantedTrap?: boolean;
  forgedCorrect?: boolean;
  forgedFailureMode?: FailurePatternType;
}

export interface ClientSafeAiVerificationResult {
  attemptId: string;
  claimId: string;
  outcome: VerificationOutcomeStatus;
  isAiFailureDetected: boolean;
  evidenceVerified: boolean;
  headline: string;
  explanation: string;
  detectedFailurePattern?: FailurePatternType;
  evidenceDigest: string;
  verifiedAt: string;
}

export interface AuthoritativeAiClaimVerificationRecord {
  verificationId: string;
  attemptId: string;
  missionId: string;
  claimId: string;
  ownerAuthUid: string;
  learnerDecision: LearnerAiDecision;
  selectedEvidenceIds: string[];
  learnerReasoning?: string;
  authoritativeClaimStatus: AuthoritativeAiClaimStatus;
  authoritativeIsUnsupportedTrap: boolean;
  authoritativeRequiredEvidenceIds: string[];
  outcome: VerificationOutcomeStatus;
  isAiFailureDetected: boolean;
  evidenceVerified: boolean;
  derivedFailureMode?: FailurePatternType;
  evidenceDigest: string;
  verifiedAt: string;
  authorityMetadata: AuthoritativeMetadata;
}


