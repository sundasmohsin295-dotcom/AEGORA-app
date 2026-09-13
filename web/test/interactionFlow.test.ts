import { describe, it, expect } from 'vitest';
import {
  buildSuspiciousLoginCyberRealityScenario,
  calculateReasoningDivergence,
  InvestigationStep
} from '../src/core/cyberReality/CyberRealityEngine';
import {
  SUSPICIOUS_LOGIN_MISSION,
  SUSPICIOUS_LOGIN_AI_CLAIM,
  evaluateWebAiClaimChallenge,
  evaluateWebAdaptiveChallengeSubmission,
  validateWebMission,
  TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING
} from '../src/core/missions/SuspiciousLoginMission';

describe('Real User Interaction Flow Verification', () => {
  it('Flow 1: Command Center -> Launch Mission loads scenario and epistemic signals', () => {
    // 1. Mission data loads
    const scenario = buildSuspiciousLoginCyberRealityScenario();
    expect(scenario).toBeDefined();
    expect(scenario.scenarioId).toBe('scen_suspicious_login_reality');
    expect(scenario.incidentNumber).toBe('INCIDENT #SL-001');

    // Controls and nodes are enabled
    expect(scenario.nodes.length).toBeGreaterThanOrEqual(10);
    expect(scenario.relationships.length).toBeGreaterThanOrEqual(8);
    expect(scenario.epistemicSignals.length).toBeGreaterThanOrEqual(5);

    // Verify key epistemic nodes exist and can be interacted with
    const groundTruthIp = scenario.nodes.find(n => n.id === 'node_ip_moscow');
    expect(groundTruthIp?.epistemicStatus).toBe('EVIDENCE');
    expect(groundTruthIp?.isVerifiedByTelemetry).toBe(true);

    const claimedIp = scenario.nodes.find(n => n.id === 'node_ip_claimed');
    expect(claimedIp?.epistemicStatus).toBe('ASSUMPTION');
    expect(claimedIp?.isVerifiedByTelemetry).toBe(false);
  });

  it('Flow 2: Evidence Selection -> Toggle corroborating evidence updates investigation state', () => {
    const selectedEvidenceIdsForAi: string[] = [];
    const steps: InvestigationStep[] = [];

    // User selects event tl_01
    selectedEvidenceIdsForAi.push('tl_01');
    steps.push({
      id: 'step_1',
      stepNumber: 1,
      timestamp: '14:02:11 UTC',
      actionType: 'TOGGLE_EVIDENCE',
      targetLabel: 'tl_01 Selected',
      detail: 'Corroborating evidence pool updated for AI audit.'
    });

    // User selects event tl_03 (the anomalous service account login)
    selectedEvidenceIdsForAi.push('tl_03');
    steps.push({
      id: 'step_2',
      stepNumber: 2,
      timestamp: '14:02:45 UTC',
      actionType: 'TOGGLE_EVIDENCE',
      targetLabel: 'tl_03 Selected',
      detail: 'Corroborating evidence pool updated for AI audit.'
    });

    expect(selectedEvidenceIdsForAi).toEqual(['tl_01', 'tl_03']);
    expect(steps.length).toBe(2);
  });

  it('Flow 3: Accept AI -> Authoritative divergence detection and unverified assumption feedback', () => {
    const steps: InvestigationStep[] = [
      {
        id: 's1',
        stepNumber: 1,
        timestamp: '14:02:11 UTC',
        actionType: 'TOGGLE_EVIDENCE',
        targetLabel: 'tl_01 Selected',
        detail: 'Selected tl_01'
      },
      {
        id: 's2',
        stepNumber: 2,
        timestamp: '14:03:00 UTC',
        actionType: 'ACCEPT_AI',
        targetLabel: 'Accepted AI Analyst Claim',
        detail: 'Submitted human verification decision for claim claim_login_malicious_ip'
      }
    ];

    // User accepts unsupported AI claim
    const outcome = evaluateWebAiClaimChallenge(
      'att_test_accept',
      'claim_login_malicious_ip',
      'ACCEPT_AI',
      ['tl_01'],
      'AI analyst says IP is malicious so accepting it.'
    );

    // AI failure was NOT caught by the user
    expect(outcome.isAiFailureDetected).toBe(false);
    expect(outcome.evidenceVerified).toBe(false);
    expect(outcome.detectedFailurePattern).toBe('EVIDENCE_OVERWEIGHTING');

    // Divergence engine catches that the user accepted an unverified assumption
    const divergence = calculateReasoningDivergence(
      steps,
      ['tl_01'],
      'ACCEPT_AI',
      ['tl_01', 'tl_02', 'tl_03']
    );
    expect(divergence.hasDivergence).toBe(true);
    expect(divergence.divergenceType).toBe('ACCEPTED_UNVERIFIED_ASSUMPTION');
    expect(divergence.evidenceThatMattered).toContain('tl_03');
  });

  it('Flow 4: Challenge AI -> Detects AI failure, delivers authoritative Failure Autopsy', () => {
    // User challenges AI claim with evidence
    const outcome = evaluateWebAiClaimChallenge(
      'att_test_challenge',
      'claim_login_malicious_ip',
      'CHALLENGE_AI',
      ['tl_01', 'tl_02', 'tl_03'],
      'The source IP in the claim (185.91.x.x) is completely absent from all authentication events.'
    );

    expect(outcome.isAiFailureDetected).toBe(true);
    expect(outcome.evidenceVerified).toBe(true);
    expect(outcome.outcome).toBe('AI_FAILURE_DETECTED');
    expect(outcome.headline).toBe('AI FAILURE DETECTED ✓');
  });

  it('Flow 5: Adaptive Challenge -> Submits remediation action and verifies improvement', () => {
    const adaptiveResult = evaluateWebAdaptiveChallengeSubmission(
      TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.challengeId,
      'act_correlate_telemetry',
      ['evi_proxy_01'],
      'Auditing proxy 404 zero-transfer status confirms connectivity probe, avoiding unnecessary disruption.'
    );

    expect(adaptiveResult.isPassed).toBe(true);
    expect(adaptiveResult.isImprovementVerified).toBe(true);
    expect(adaptiveResult.evidenceDigest).toMatch(/^sha256:/);
  });

  it('Flow 6: Final Mission Submit -> Authoritative validation with scoring & cryptographic hash', () => {
    const missionOutcome = validateWebMission(
      {
        q_login_1: 0,
        q_login_2: 0,
        q_login_3: 0
      },
      'Event 4625 failed logons preceded Event 4624 successful logon of j.smith from 198.51.100.12.'
    );

    expect(missionOutcome.isPassed).toBe(true);
    expect(missionOutcome.scorePercent).toBe(100);
    expect(missionOutcome.evidenceHash).toMatch(/^sha256:/);
    expect(missionOutcome.capabilityKey).toBe('sec_auth_impossible_travel_triage');
  });
});
