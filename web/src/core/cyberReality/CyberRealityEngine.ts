/**
 * AEGORA CYBER REALITY ENGINE
 *
 * Core conceptual adapter and execution boundary for Cyber Reality scenarios.
 * Connects telemetry datasets, evidence graphs, epistemic classification,
 * investigation replay, and authoritative verification.
 *
 * IMPORTANT: The Cyber Reality Engine is an interaction and visualization layer.
 * All verification decisions remain authoritatively bound to the verified server/client
 * evaluation engines.
 */

import {
  InvestigationTimelineEvent,
  FailurePatternType,
  LearnerAiDecision,
  VerificationOutcomeStatus
} from '../types/platform';
import {
  SUSPICIOUS_LOGIN_MISSION,
  SUSPICIOUS_LOGIN_AI_CLAIM
} from '../missions/SuspiciousLoginMission';

export type EvidenceNodeType =
  | 'USER'
  | 'LOGIN'
  | 'DEVICE'
  | 'IP'
  | 'AUTH_EVENT'
  | 'PROCESS'
  | 'NETWORK_EVENT'
  | 'INFERENCE'
  | 'CLAIM';

export type EpistemicStatus =
  | 'FACT'
  | 'EVIDENCE'
  | 'INFERENCE'
  | 'ASSUMPTION'
  | 'UNKNOWN';

export interface EvidenceNode {
  id: string;
  type: EvidenceNodeType;
  label: string;
  subLabel: string;
  epistemicStatus: EpistemicStatus;
  isVerifiedByTelemetry: boolean;
  corroboratingEventIds: string[];
  details: string;
  metadata?: Record<string, string>;
}

export interface EvidenceRelationship {
  id: string;
  sourceNodeId: string;
  targetNodeId: string;
  relationType: string;
  label: string;
  isVerifiedByTelemetry: boolean;
  notes: string;
}

export interface EpistemicSignal {
  id: string;
  claim: string;
  source: 'SYSTEM_TELEMETRY' | 'AI_CO_PILOT' | 'EXTERNAL_INTEL';
  epistemicStatus: EpistemicStatus;
  explanation: string;
  corroboratingEventIds: string[];
}

export interface InvestigationStep {
  id: string;
  stepNumber: number;
  timestamp: string;
  actionType:
    | 'OPEN_EVENT'
    | 'INSPECT_IP'
    | 'INSPECT_GRAPH_NODE'
    | 'AUDIT_EPISTEMIC'
    | 'TOGGLE_EVIDENCE'
    | 'AUDIT_AI_CLAIM'
    | 'CHALLENGE_AI'
    | 'ACCEPT_AI'
    | 'SUBMIT_DECISION';
  targetLabel: string;
  detail: string;
}

export interface ReasoningDivergence {
  hasDivergence: boolean;
  divergenceType?: 'ACCEPTED_UNVERIFIED_ASSUMPTION' | 'CHALLENGED_VALID_FACT' | 'IGNORED_CORRELATION';
  summary: string;
  evidenceThatMattered: string[];
  evidenceUsed: string[];
  correctReasoningPath: string;
}

export interface CyberRealityScenario {
  scenarioId: string;
  incidentNumber: string;
  title: string;
  severity: 'SEV-1' | 'SEV-2' | 'SEV-3';
  objective: string;
  targetDomain: string;
  userContext: {
    userId: string;
    role: string;
    department: string;
    assignedLocation: string;
    status: string;
  };
  nodes: EvidenceNode[];
  relationships: EvidenceRelationship[];
  epistemicSignals: EpistemicSignal[];
  evidenceThatMatteredIds: string[];
}

/**
 * Builds the canonical Suspicious Login Cyber Reality Scenario.
 * All ground telemetry is mapped directly from SUSPICIOUS_LOGIN_MISSION without mutation.
 */
export function buildSuspiciousLoginCyberRealityScenario(): CyberRealityScenario {
  const nodes: EvidenceNode[] = [
    {
      id: 'node_user',
      type: 'USER',
      label: 'j.smith',
      subLabel: 'Senior Financial Analyst',
      epistemicStatus: 'FACT',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_01', 'tl_02', 'tl_03', 'tl_04'],
      details: 'Corporate Active Directory user account. Standard business hours 08:00-17:00 CST (Austin, TX).'
    },
    {
      id: 'node_login_moscow',
      type: 'LOGIN',
      label: 'Login Attempt (Moscow)',
      subLabel: '08:14 - 08:15 UTC',
      epistemicStatus: 'FACT',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_01', 'tl_02', 'tl_03'],
      details: 'Rapid consecutive authentication failures followed by success on Domain Controller.'
    },
    {
      id: 'node_login_austin',
      type: 'LOGIN',
      label: 'VPN Login (Austin)',
      subLabel: '08:22 UTC',
      epistemicStatus: 'FACT',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_04'],
      details: 'Legitimate corporate VPN session from assigned employee home location.'
    },
    {
      id: 'node_ip_moscow',
      type: 'IP',
      label: '198.51.100.12',
      subLabel: 'Source IP // Moscow, RU',
      epistemicStatus: 'EVIDENCE',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_01', 'tl_02', 'tl_03'],
      details: 'External non-VPN IP originating rapid NTLM logon failures and 1 success.'
    },
    {
      id: 'node_ip_austin',
      type: 'IP',
      label: '73.189.44.10',
      subLabel: 'Source IP // Austin, TX',
      epistemicStatus: 'EVIDENCE',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_04'],
      details: 'Residential ISP IP known in user profile history as legitimate home office connection.'
    },
    {
      id: 'node_ip_claimed',
      type: 'CLAIM',
      label: '185.91.x.x',
      subLabel: 'AI Asserted Threat IP',
      epistemicStatus: 'ASSUMPTION',
      isVerifiedByTelemetry: false,
      corroboratingEventIds: [],
      details: 'IP asserted by AI Co-Pilot as "associated with attack". COMPLETELY ABSENT from raw system telemetry.'
    },
    {
      id: 'node_auth_4625',
      type: 'AUTH_EVENT',
      label: 'Event ID 4625 (Failures)',
      subLabel: 'Status 0xC000006A (Bad Password)',
      epistemicStatus: 'EVIDENCE',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_01', 'tl_02'],
      details: 'Multiple rapid failed authentication attempts in under 90 seconds (LogonType 3 Network).'
    },
    {
      id: 'node_auth_4624_moscow',
      type: 'AUTH_EVENT',
      label: 'Event ID 4624 (Success)',
      subLabel: 'LogonType 3 (Network Logon)',
      epistemicStatus: 'EVIDENCE',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_03'],
      details: 'Password accepted for j.smith following rapid failures from Moscow IP.'
    },
    {
      id: 'node_auth_4624_austin',
      type: 'AUTH_EVENT',
      label: 'Event ID 4624 (VPN)',
      subLabel: 'LogonType 2 (Interactive)',
      epistemicStatus: 'EVIDENCE',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_04'],
      details: 'Successful VPN connection via CiscoAnyConnect client.'
    },
    {
      id: 'node_inf_velocity',
      type: 'INFERENCE',
      label: 'Impossible Travel Velocity',
      subLabel: '9,000 km in 6m 30s',
      epistemicStatus: 'INFERENCE',
      isVerifiedByTelemetry: true,
      corroboratingEventIds: ['tl_03', 'tl_04'],
      details: 'Physical travel velocity across transatlantic endpoints is mathematically impossible.'
    },
    {
      id: 'node_unk_vector',
      type: 'INFERENCE',
      label: 'Initial Compromise Vector',
      subLabel: 'Phishing vs Credential Stuffing',
      epistemicStatus: 'UNKNOWN',
      isVerifiedByTelemetry: false,
      corroboratingEventIds: [],
      details: 'Logs confirm brute-force behavior, but cannot prove whether user password was previously leaked.'
    }
  ];

  const relationships: EvidenceRelationship[] = [
    {
      id: 'rel_user_login1',
      sourceNodeId: 'node_user',
      targetNodeId: 'node_login_moscow',
      relationType: 'AUTHENTICATION_TARGET',
      label: 'Target of Rapid Logons',
      isVerifiedByTelemetry: true,
      notes: 'Identity targeted on DC-PRIMARY-01.'
    },
    {
      id: 'rel_user_login2',
      sourceNodeId: 'node_user',
      targetNodeId: 'node_login_austin',
      relationType: 'AUTHENTICATION_TARGET',
      label: 'Employee Work Session',
      isVerifiedByTelemetry: true,
      notes: 'Legitimate morning logon.'
    },
    {
      id: 'rel_login1_ip1',
      sourceNodeId: 'node_login_moscow',
      targetNodeId: 'node_ip_moscow',
      relationType: 'ORIGINATED_FROM',
      label: 'Source 198.51.100.12',
      isVerifiedByTelemetry: true,
      notes: 'Recorded in Event 4625 & 4624 logs.'
    },
    {
      id: 'rel_login2_ip2',
      sourceNodeId: 'node_login_austin',
      targetNodeId: 'node_ip_austin',
      relationType: 'ORIGINATED_FROM',
      label: 'Source 73.189.44.10',
      isVerifiedByTelemetry: true,
      notes: 'Recorded in VPN gateway logs.'
    },
    {
      id: 'rel_login1_auth_fail',
      sourceNodeId: 'node_login_moscow',
      targetNodeId: 'node_auth_4625',
      relationType: 'GENERATED_EVENT',
      label: 'Triggers Failures',
      isVerifiedByTelemetry: true,
      notes: 'Bad password events (tl_01, tl_02).'
    },
    {
      id: 'rel_login1_auth_succ',
      sourceNodeId: 'node_login_moscow',
      targetNodeId: 'node_auth_4624_moscow',
      relationType: 'GENERATED_EVENT',
      label: 'Triggers Success',
      isVerifiedByTelemetry: true,
      notes: 'Network logon success (tl_03).'
    },
    {
      id: 'rel_login2_auth_succ',
      sourceNodeId: 'node_login_austin',
      targetNodeId: 'node_auth_4624_austin',
      relationType: 'GENERATED_EVENT',
      label: 'Triggers Interactive Logon',
      isVerifiedByTelemetry: true,
      notes: 'Interactive Cisco VPN logon (tl_04).'
    },
    {
      id: 'rel_velocity_calc',
      sourceNodeId: 'node_auth_4624_moscow',
      targetNodeId: 'node_inf_velocity',
      relationType: 'CORRELATED_WITH',
      label: 'Delta vs 08:22 Austin',
      isVerifiedByTelemetry: true,
      notes: 'Temporal delta of 6m 30s confirms impossible travel.'
    },
    {
      id: 'rel_ai_unsupported',
      sourceNodeId: 'node_ip_claimed',
      targetNodeId: 'node_login_moscow',
      relationType: 'ATTRIBUTED_BY_AI',
      label: 'AI Hallucinated Attribution',
      isVerifiedByTelemetry: false,
      notes: 'AI claims 185.91.x.x is attack source, but zero telemetry records this IP.'
    }
  ];

  const epistemicSignals: EpistemicSignal[] = [
    {
      id: 'sig_01',
      claim: 'User j.smith experienced consecutive authentication failures at 08:14 UTC followed by success at 08:15 UTC.',
      source: 'SYSTEM_TELEMETRY',
      epistemicStatus: 'FACT',
      explanation: 'Verified directly in Domain Controller security logs Event ID 4625 and 4624.',
      corroboratingEventIds: ['tl_01', 'tl_02', 'tl_03']
    },
    {
      id: 'sig_02',
      claim: 'The source IP of the failed and successful Moscow logons was 198.51.100.12.',
      source: 'SYSTEM_TELEMETRY',
      epistemicStatus: 'EVIDENCE',
      explanation: 'Extracted directly from raw Windows Event log payload parameters (SrcIP=198.51.100.12).',
      corroboratingEventIds: ['tl_01', 'tl_02', 'tl_03']
    },
    {
      id: 'sig_03',
      claim: 'Consecutive logins across 9,000 km within 7 minutes represents impossible travel velocity.',
      source: 'SYSTEM_TELEMETRY',
      epistemicStatus: 'INFERENCE',
      explanation: 'Deduction derived from comparing timestamp deltas against physical aviation transit constraints.',
      corroboratingEventIds: ['tl_03', 'tl_04']
    },
    {
      id: 'sig_04',
      claim: 'The login is confirmed malicious because source IP 185.91.x.x is associated with the attack.',
      source: 'AI_CO_PILOT',
      epistemicStatus: 'ASSUMPTION',
      explanation: 'Unsubstantiated assertion by AI assistant. IP 185.91.x.x does not exist in any provided event.',
      corroboratingEventIds: []
    },
    {
      id: 'sig_05',
      claim: 'Whether the legitimate employee clicked a phishing email or reused credentials from an external breach.',
      source: 'SYSTEM_TELEMETRY',
      epistemicStatus: 'UNKNOWN',
      explanation: 'Authentication logs confirm password success, but cannot establish pre-attack root cause credential origin.',
      corroboratingEventIds: []
    }
  ];

  return {
    scenarioId: 'scen_suspicious_login_reality',
    incidentNumber: 'INCIDENT #SL-001',
    title: 'Suspicious Login & Impossible Travel Reality',
    severity: 'SEV-2',
    objective: 'Investigate live authentication telemetry, distinguish verified facts from uncorroborated AI assumptions, and execute defensible containment.',
    targetDomain: 'Enterprise Identity & SOC Operations',
    userContext: {
      userId: 'j.smith',
      role: 'Senior Financial Analyst',
      department: 'Corporate Treasury',
      assignedLocation: 'Austin, Texas, US',
      status: 'UNDER ACTIVE TRIAGE'
    },
    nodes,
    relationships,
    epistemicSignals,
    evidenceThatMatteredIds: ['tl_01', 'tl_02', 'tl_03']
  };
}

/**
 * Calculates reasoning divergence comparing learner investigative steps against
 * authoritative requirements.
 */
export function calculateReasoningDivergence(
  steps: InvestigationStep[],
  selectedEvidenceIds: string[],
  aiDecision: LearnerAiDecision | null,
  claimedEvidenceMattered: string[] = ['tl_01', 'tl_02', 'tl_03']
): ReasoningDivergence {
  if (!aiDecision) {
    return {
      hasDivergence: false,
      summary: 'Investigation in progress.',
      evidenceThatMattered: claimedEvidenceMattered,
      evidenceUsed: selectedEvidenceIds,
      correctReasoningPath: 'Audit telemetry -> Identify discrepancy -> Challenge unsupported AI claim -> Contain identity.'
    };
  }

  // Did the learner accept the unsupported AI claim?
  if (aiDecision === 'ACCEPT_AI') {
    return {
      hasDivergence: true,
      divergenceType: 'ACCEPTED_UNVERIFIED_ASSUMPTION',
      summary:
        'Divergence: You accepted the AI co-pilot\'s claim that IP 185.91.x.x was the attack source. However, inspecting raw telemetry proves the attack IP was 198.51.100.12. You anchored on co-pilot confidence without verifying telemetry truth.',
      evidenceThatMattered: claimedEvidenceMattered,
      evidenceUsed: selectedEvidenceIds,
      correctReasoningPath:
        'Verify every claimed IOC against system Event IDs (4624/4625) before accepting AI containment recommendations.'
    };
  }

  // Learner challenged AI correctly
  const usedRelevantEvidence = selectedEvidenceIds.some(id => claimedEvidenceMattered.includes(id));

  return {
    hasDivergence: false,
    summary: usedRelevantEvidence
      ? 'Zero Divergence: Your investigation path correctly distinguished telemetry evidence from unsupported AI assertions.'
      : 'Supported Challenge: You challenged the AI claim correctly, but ensure you select corroborating logs (tl_01, tl_02, tl_03) in the evidentiary record.',
    evidenceThatMattered: claimedEvidenceMattered,
    evidenceUsed: selectedEvidenceIds,
    correctReasoningPath:
      'Audit raw authentication events -> Catch absent IOC (185.91.x.x) -> Challenge AI -> Ground triage in system logs.'
  };
}
