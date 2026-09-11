import { describe, it, expect } from 'vitest';
import {
  buildSuspiciousLoginCyberRealityScenario,
  calculateReasoningDivergence,
  InvestigationStep
} from '../src/core/cyberReality/CyberRealityEngine';

describe('AEGORA Cyber Reality Engine Tests', () => {
  it('01: Builds canonical Cyber Reality scenario with hierarchical layers', () => {
    const scenario = buildSuspiciousLoginCyberRealityScenario();

    expect(scenario.scenarioId).toBe('scen_suspicious_login_reality');
    expect(scenario.incidentNumber).toBe('INCIDENT #SL-001');
    expect(scenario.nodes.length).toBeGreaterThanOrEqual(10);
    expect(scenario.relationships.length).toBeGreaterThanOrEqual(8);
    expect(scenario.epistemicSignals.length).toBeGreaterThanOrEqual(5);

    // Verify presence of all architectural layers
    const types = scenario.nodes.map(n => n.type);
    expect(types).toContain('USER');
    expect(types).toContain('LOGIN');
    expect(types).toContain('IP');
    expect(types).toContain('AUTH_EVENT');
    expect(types).toContain('INFERENCE');
    expect(types).toContain('CLAIM');
  });

  it('02: Accurately classifies epistemics: Facts, Telemetry Evidence, and Unsupported Assumptions', () => {
    const scenario = buildSuspiciousLoginCyberRealityScenario();

    // User is ground FACT
    const userNode = scenario.nodes.find(n => n.type === 'USER');
    expect(userNode?.epistemicStatus).toBe('FACT');
    expect(userNode?.isVerifiedByTelemetry).toBe(true);

    // AI Claimed IP (185.91.x.x) is ASSUMPTION with zero corroborating telemetry
    const claimedIpNode = scenario.nodes.find(n => n.id === 'node_ip_claimed');
    expect(claimedIpNode?.epistemicStatus).toBe('ASSUMPTION');
    expect(claimedIpNode?.isVerifiedByTelemetry).toBe(false);
    expect(claimedIpNode?.corroboratingEventIds).toEqual([]);

    // Actual attack IP (198.51.100.12) is verified EVIDENCE in tl_01, tl_02, tl_03
    const attackIpNode = scenario.nodes.find(n => n.id === 'node_ip_moscow');
    expect(attackIpNode?.epistemicStatus).toBe('EVIDENCE');
    expect(attackIpNode?.isVerifiedByTelemetry).toBe(true);
    expect(attackIpNode?.corroboratingEventIds).toContain('tl_01');
    expect(attackIpNode?.corroboratingEventIds).toContain('tl_03');

    // Transatlantic velocity delta is INFERENCE
    const velocityNode = scenario.nodes.find(n => n.id === 'node_inf_velocity');
    expect(velocityNode?.epistemicStatus).toBe('INFERENCE');
  });

  it('03: Detects reasoning divergence when learner accepts unsupported AI assumption', () => {
    const mockSteps: InvestigationStep[] = [
      {
        id: 's1',
        stepNumber: 1,
        timestamp: '08:14 UTC',
        actionType: 'OPEN_EVENT',
        targetLabel: 'Event Log tl_01',
        detail: 'Audited log payload'
      },
      {
        id: 's2',
        stepNumber: 2,
        timestamp: '08:16 UTC',
        actionType: 'ACCEPT_AI',
        targetLabel: 'Accepted AI Claim',
        detail: 'Accepted claim without verifying IP presence'
      }
    ];

    const divergence = calculateReasoningDivergence(
      mockSteps,
      ['tl_01'],
      'ACCEPT_AI',
      ['tl_01', 'tl_02', 'tl_03']
    );

    expect(divergence.hasDivergence).toBe(true);
    expect(divergence.divergenceType).toBe('ACCEPTED_UNVERIFIED_ASSUMPTION');
    expect(divergence.summary).toContain('You accepted the AI co-pilot\'s claim');
    expect(divergence.evidenceThatMattered).toContain('tl_03');
  });

  it('04: Confirms zero divergence when learner grounds challenge in authoritative telemetry', () => {
    const mockSteps: InvestigationStep[] = [
      {
        id: 's1',
        stepNumber: 1,
        timestamp: '08:14 UTC',
        actionType: 'OPEN_EVENT',
        targetLabel: 'Event Log tl_01',
        detail: 'Audited log payload'
      },
      {
        id: 's2',
        stepNumber: 2,
        timestamp: '08:15 UTC',
        actionType: 'CHALLENGE_AI',
        targetLabel: 'Challenged AI Claim',
        detail: 'Flagged missing IOC'
      }
    ];

    const divergence = calculateReasoningDivergence(
      mockSteps,
      ['tl_01', 'tl_02', 'tl_03'],
      'CHALLENGE_AI',
      ['tl_01', 'tl_02', 'tl_03']
    );

    expect(divergence.hasDivergence).toBe(false);
    expect(divergence.summary).toContain('Zero Divergence');
    expect(divergence.evidenceThatMattered).toEqual(['tl_01', 'tl_02', 'tl_03']);
  });

  it('05: Security Audit: Cyber Reality Scenario does not leak correct answers or hidden trap flags to client', () => {
    const scenario = buildSuspiciousLoginCyberRealityScenario() as any;

    expect(scenario.isUnsupportedTrap).toBeUndefined();
    expect(scenario.isUnsupportedPlantedTrap).toBeUndefined();
    expect(scenario.hiddenTrap).toBeUndefined();
    expect(scenario.groundTruth).toBeUndefined();
    expect(scenario.correctAnswer).toBeUndefined();
    expect(scenario.authoritativeOutcome).toBeUndefined();
    expect(scenario.serverScore).toBeUndefined();
  });
});
