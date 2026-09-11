import React, { useState, useMemo } from 'react';
import {
  Shield,
  AlertTriangle,
  FileText,
  HelpCircle,
  CheckCircle2,
  XCircle,
  ArrowRight,
  Terminal,
  Lock,
  ChevronDown,
  ChevronUp,
  RotateCcw,
  Share2,
  Activity,
  Layers,
  Scale
} from 'lucide-react';
import {
  SUSPICIOUS_LOGIN_MISSION,
  SUSPICIOUS_LOGIN_AI_CLAIM,
  validateWebMission,
  evaluateWebAiClaimChallenge,
  TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING,
  evaluateWebAdaptiveChallengeSubmission
} from '../core/missions/SuspiciousLoginMission';
import {
  MissionValidationOutcome,
  ClientSafeAiVerificationResult,
  AuthoritativeAdaptiveEvaluationResult
} from '../core/types/platform';
import {
  buildSuspiciousLoginCyberRealityScenario,
  calculateReasoningDivergence,
  InvestigationStep,
  EvidenceNode
} from '../core/cyberReality/CyberRealityEngine';
import { EvidenceGraphView } from './EvidenceGraphView';
import { EpistemicAuditView } from './EpistemicAuditView';
import { InvestigationReplayView } from './InvestigationReplayView';

interface MissionInvestigationViewProps {
  onReturnToCommandCenter: () => void;
}

export const MissionInvestigationView: React.FC<MissionInvestigationViewProps> = ({
  onReturnToCommandCenter
}) => {
  const mission = SUSPICIOUS_LOGIN_MISSION;
  const aiClaim = SUSPICIOUS_LOGIN_AI_CLAIM;
  const cyberRealityScenario = useMemo(() => buildSuspiciousLoginCyberRealityScenario(), []);

  const [selectedAnswers, setSelectedAnswers] = useState<Record<string, number>>({});
  const [reasoning, setReasoning] = useState<string>('');
  const [openHintIndex, setOpenHintIndex] = useState<number | null>(null);
  const [validationOutcome, setValidationOutcome] = useState<MissionValidationOutcome | null>(null);
  const [selectedEventId, setSelectedEventId] = useState<string>(mission.events[0].id);

  // Workspace View Mode: TELEMETRY (Logs) | GRAPH (Evidence Graph) | EPISTEMIC (Controlled Uncertainty)
  const [workspaceMode, setWorkspaceMode] = useState<'TELEMETRY' | 'GRAPH' | 'EPISTEMIC'>('TELEMETRY');

  // AI Hallucination & Human Verification state
  const [selectedEvidenceIdsForAi, setSelectedEvidenceIdsForAi] = useState<string[]>([]);
  const [aiVerificationResult, setAiVerificationResult] = useState<ClientSafeAiVerificationResult | null>(null);
  const [lastAiDecision, setLastAiDecision] = useState<'ACCEPT_AI' | 'CHALLENGE_AI' | null>(null);

  // Investigation Replay Steps Trace
  const [investigationSteps, setInvestigationSteps] = useState<InvestigationStep[]>([
    {
      id: 'step_init',
      stepNumber: 1,
      timestamp: '08:14 UTC',
      actionType: 'OPEN_EVENT',
      targetLabel: 'SEV-2 Alert: Impossible Travel Velocity',
      detail: 'Dossier initialized for identity j.smith between Moscow and Austin endpoints.'
    }
  ]);

  // Adaptive Adversary Challenge state
  const [isAdaptiveModalOpen, setIsAdaptiveModalOpen] = useState(false);
  const [adaptiveSelectedAction, setAdaptiveSelectedAction] = useState<string>('');
  const [adaptiveSelectedEvidence, setAdaptiveSelectedEvidence] = useState<string[]>([]);
  const [adaptiveReasoning, setAdaptiveReasoning] = useState<string>('');
  const [adaptiveResult, setAdaptiveResult] = useState<AuthoritativeAdaptiveEvaluationResult | null>(null);

  const addInvestigationStep = (
    actionType: InvestigationStep['actionType'],
    targetLabel: string,
    detail: string
  ) => {
    const timeStr = new Date().toISOString().substring(11, 19) + ' UTC';
    setInvestigationSteps(prev => [
      ...prev,
      {
        id: `step_${Date.now()}_${prev.length + 1}`,
        stepNumber: prev.length + 1,
        timestamp: timeStr,
        actionType,
        targetLabel,
        detail
      }
    ]);
  };

  const handleSelectEvent = (eventId: string) => {
    setSelectedEventId(eventId);
    addInvestigationStep('OPEN_EVENT', `Event Log ${eventId}`, `Inspected Windows Event Log payload for ${eventId}`);
  };

  const handleToggleAdaptiveEvidence = (id: string) => {
    setAdaptiveSelectedEvidence(prev =>
      prev.includes(id) ? prev.filter(e => e !== id) : [...prev, id]
    );
  };

  const handleLaunchAdaptiveChallenge = () => {
    setIsAdaptiveModalOpen(true);
    setAdaptiveSelectedAction('');
    setAdaptiveSelectedEvidence([]);
    setAdaptiveReasoning('');
    setAdaptiveResult(null);
  };

  const handleSubmitAdaptiveChallenge = () => {
    const res = evaluateWebAdaptiveChallengeSubmission(
      TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.challengeId,
      adaptiveSelectedAction,
      adaptiveSelectedEvidence,
      adaptiveReasoning
    );
    setAdaptiveResult(res);
  };

  const handleToggleEvidenceForAi = (eventId: string) => {
    setSelectedEvidenceIdsForAi(prev => {
      const willBeChecked = !prev.includes(eventId);
      addInvestigationStep(
        'TOGGLE_EVIDENCE',
        `${eventId} ${willBeChecked ? 'Selected' : 'Deselected'}`,
        `Corroborating evidence pool updated for AI audit.`
      );
      return willBeChecked ? [...prev, eventId] : prev.filter(id => id !== eventId);
    });
  };

  const handleAiDecision = (decision: 'ACCEPT_AI' | 'CHALLENGE_AI') => {
    setLastAiDecision(decision);
    addInvestigationStep(
      decision,
      decision === 'CHALLENGE_AI' ? 'Challenged AI Analyst Claim' : 'Accepted AI Analyst Claim',
      `Submitted human verification decision for claim ${aiClaim.claimId}`
    );

    const outcome = evaluateWebAiClaimChallenge(
      `att_${Date.now()}`,
      aiClaim.claimId,
      decision,
      selectedEvidenceIdsForAi,
      reasoning
    );
    setAiVerificationResult(outcome as ClientSafeAiVerificationResult);
  };

  const handleSelectAnswer = (questionId: string, optionIndex: number) => {
    setSelectedAnswers(prev => ({ ...prev, [questionId]: optionIndex }));
  };

  const handleSubmitInvestigation = () => {
    addInvestigationStep('SUBMIT_DECISION', 'Submitted SOC Operator Triage', `Documented forensic containment plan.`);
    const outcome = validateWebMission(selectedAnswers, reasoning);
    setValidationOutcome(outcome);
  };

  const handleReset = () => {
    setSelectedAnswers({});
    setReasoning('');
    setValidationOutcome(null);
    setOpenHintIndex(null);
  };

  const handleGraphNodeInspected = (node: EvidenceNode) => {
    addInvestigationStep(
      'INSPECT_GRAPH_NODE',
      `Inspected Graph: ${node.label}`,
      `Examined node epistemic status: [${node.epistemicStatus}] and telemetry links.`
    );
  };

  const activeEvent = mission.events.find(e => e.id === selectedEventId) || mission.events[0];

  const reasoningDivergence = useMemo(() => {
    return calculateReasoningDivergence(
      investigationSteps,
      selectedEvidenceIdsForAi,
      lastAiDecision,
      cyberRealityScenario.evidenceThatMatteredIds
    );
  }, [investigationSteps, selectedEvidenceIdsForAi, lastAiDecision, cyberRealityScenario]);

  return (
    <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '24px' }}>
      {/* Top Cyber Reality Incident Command Bar */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: '16px 20px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          marginBottom: '20px',
          flexWrap: 'wrap',
          gap: '12px'
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
            <span
              style={{
                fontSize: '11px',
                fontFamily: 'var(--font-mono)',
                padding: '2px 8px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--accent-rose-subtle)',
                color: 'var(--accent-rose)',
                border: '1px solid var(--accent-rose)',
                fontWeight: 800
              }}
            >
              {cyberRealityScenario.incidentNumber} // {cyberRealityScenario.severity}
            </span>
            <span
              style={{
                fontSize: '11px',
                fontFamily: 'var(--font-mono)',
                color: 'var(--accent-cyan)',
                padding: '2px 6px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--accent-cyan-subtle)'
              }}
            >
              CYBER REALITY ENGINE
            </span>
            <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
              Target: {cyberRealityScenario.userContext.userId} ({cyberRealityScenario.userContext.role})
            </span>
          </div>
          <h1 style={{ fontSize: '20px', fontWeight: 900 }}>{cyberRealityScenario.title}</h1>
          <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '2px' }}>
            {cyberRealityScenario.objective}
          </p>
        </div>

        <button
          onClick={onReturnToCommandCenter}
          style={{
            padding: '8px 16px',
            backgroundColor: 'var(--bg-tertiary)',
            color: 'var(--text-primary)',
            border: '1px solid var(--border-subtle)',
            borderRadius: 'var(--radius-sm)',
            fontSize: '13px',
            fontWeight: 600,
            cursor: 'pointer'
          }}
        >
          Return to Command Center
        </button>
      </div>

      {/* Cyber Reality Workspace Mode Selector Tabs */}
      <div
        style={{
          display: 'flex',
          gap: '8px',
          marginBottom: '20px',
          borderBottom: '1px solid var(--border-subtle)',
          paddingBottom: '12px'
        }}
      >
        <button
          onClick={() => setWorkspaceMode('TELEMETRY')}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            padding: '8px 16px',
            borderRadius: 'var(--radius-sm)',
            backgroundColor: workspaceMode === 'TELEMETRY' ? 'var(--accent-cyan-subtle)' : 'transparent',
            border: workspaceMode === 'TELEMETRY' ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
            color: workspaceMode === 'TELEMETRY' ? 'var(--accent-cyan)' : 'var(--text-secondary)',
            fontSize: '12px',
            fontWeight: 700,
            cursor: 'pointer',
            fontFamily: 'var(--font-mono)'
          }}
        >
          <Activity size={14} />
          TELEMETRY & RAW LOGS ({mission.events.length})
        </button>

        <button
          onClick={() => setWorkspaceMode('GRAPH')}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            padding: '8px 16px',
            borderRadius: 'var(--radius-sm)',
            backgroundColor: workspaceMode === 'GRAPH' ? 'var(--accent-cyan-subtle)' : 'transparent',
            border: workspaceMode === 'GRAPH' ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
            color: workspaceMode === 'GRAPH' ? 'var(--accent-cyan)' : 'var(--text-secondary)',
            fontSize: '12px',
            fontWeight: 700,
            cursor: 'pointer',
            fontFamily: 'var(--font-mono)'
          }}
        >
          <Layers size={14} />
          EVIDENCE GRAPH ({cyberRealityScenario.nodes.length} NODES)
        </button>

        <button
          onClick={() => setWorkspaceMode('EPISTEMIC')}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            padding: '8px 16px',
            borderRadius: 'var(--radius-sm)',
            backgroundColor: workspaceMode === 'EPISTEMIC' ? 'var(--accent-cyan-subtle)' : 'transparent',
            border: workspaceMode === 'EPISTEMIC' ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
            color: workspaceMode === 'EPISTEMIC' ? 'var(--accent-cyan)' : 'var(--text-secondary)',
            fontSize: '12px',
            fontWeight: 700,
            cursor: 'pointer',
            fontFamily: 'var(--font-mono)'
          }}
        >
          <Scale size={14} />
          EPISTEMIC AUDIT (FACT vs ASSUMPTION)
        </button>
      </div>

      {/* Primary Investigation Layout */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(480px, 1fr))', gap: '24px' }}>
        {/* LEFT COLUMN: ACTIVE WORKSPACE TAB */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {workspaceMode === 'TELEMETRY' && (
            <>
              {/* Anomaly Detection Banner */}
              <div
                style={{
                  padding: '16px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--accent-amber-subtle)',
                  border: '1px solid var(--accent-amber)',
                  display: 'flex',
                  gap: '12px',
                  alignItems: 'flex-start'
                }}
              >
                <AlertTriangle color="var(--accent-amber)" size={22} style={{ flexShrink: 0, marginTop: '2px' }} />
                <div>
                  <h3 style={{ fontSize: '14px', fontWeight: 700, color: 'var(--accent-amber)' }}>
                    Impossible Travel Velocity Anomaly
                  </h3>
                  <p style={{ fontSize: '12px', color: 'var(--text-primary)', marginTop: '4px', lineHeight: 1.5 }}>
                    Identity <code>j.smith</code> authenticated from <strong>Moscow, RU (IP 198.51.100.12)</strong> at 08:15:40 UTC,
                    followed by an authentication from <strong>Austin, Texas (IP 73.189.44.10)</strong> at 08:22:10 UTC.
                    Delta: <strong>6 minutes 30 seconds</strong> across ~9,000 km.
                  </p>
                </div>
              </div>

              {/* Timeline Event List */}
              <div
                style={{
                  padding: '16px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--bg-secondary)',
                  border: '1px solid var(--border-subtle)'
                }}
              >
                <h3 style={{ fontSize: '14px', fontWeight: 700, marginBottom: '12px' }}>
                  Windows Event Log Telemetry ({mission.events.length} Events)
                </h3>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                  {mission.events.map(event => (
                    <div
                      key={event.id}
                      onClick={() => handleSelectEvent(event.id)}
                      style={{
                        padding: '12px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor:
                          selectedEventId === event.id ? 'var(--bg-card-hover)' : 'var(--bg-tertiary)',
                        border:
                          selectedEventId === event.id
                            ? '1px solid var(--accent-cyan)'
                            : '1px solid var(--border-subtle)',
                        cursor: 'pointer'
                      }}
                    >
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <span
                          style={{
                            fontSize: '11px',
                            fontFamily: 'var(--font-mono)',
                            color: event.isSuspicious ? 'var(--accent-rose)' : 'var(--accent-emerald)',
                            fontWeight: 700
                          }}
                        >
                          {event.eventType}
                        </span>
                        <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                          {event.timestamp}
                        </span>
                      </div>
                      <div style={{ fontSize: '12px', fontWeight: 600, marginTop: '4px' }}>
                        {event.summary}
                      </div>
                      <div style={{ fontSize: '11px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                        {event.processOrUser}
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              {/* Raw Log Inspector */}
              <div
                style={{
                  padding: '16px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--bg-secondary)',
                  border: '1px solid var(--border-subtle)'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                  <Terminal size={16} color="var(--accent-cyan)" />
                  <h3 style={{ fontSize: '13px', fontWeight: 700, fontFamily: 'var(--font-mono)' }}>
                    RAW LOG PAYLOAD // {activeEvent.id}
                  </h3>
                </div>
                <pre
                  style={{
                    fontSize: '11px',
                    fontFamily: 'var(--font-mono)',
                    backgroundColor: 'var(--bg-primary)',
                    padding: '12px',
                    borderRadius: 'var(--radius-sm)',
                    color: 'var(--accent-cyan)',
                    overflowX: 'auto',
                    border: '1px solid var(--border-strong)'
                  }}
                >
                  {activeEvent.rawLog}
                </pre>
              </div>
            </>
          )}

          {workspaceMode === 'GRAPH' && (
            <EvidenceGraphView
              scenario={cyberRealityScenario}
              selectedEvidenceIds={selectedEvidenceIdsForAi}
              onToggleEvidence={handleToggleEvidenceForAi}
              onNodeInspected={handleGraphNodeInspected}
            />
          )}

          {workspaceMode === 'EPISTEMIC' && (
            <EpistemicAuditView
              signals={cyberRealityScenario.epistemicSignals}
            />
          )}
        </div>

        {/* RIGHT COLUMN: DECISION TRIAGE, AI AUDIT & INVESTIGATION REPLAY */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          {/* FLAGSHIP AI HALLUCINATION DETECTION / HUMAN VERIFICATION MECHANIC */}
          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: aiVerificationResult?.isAiFailureDetected
                ? '1px solid var(--accent-emerald)'
                : '1px solid var(--accent-amber)',
              boxShadow: '0 4px 12px rgba(0,0,0,0.2)'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span
                  style={{
                    fontSize: '10px',
                    fontFamily: 'var(--font-mono)',
                    fontWeight: 800,
                    padding: '2px 8px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'rgba(245, 158, 11, 0.15)',
                    color: 'var(--accent-amber)',
                    border: '1px solid var(--accent-amber)'
                  }}
                >
                  AI CO-PILOT CLAIM
                </span>
                <span style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                  {aiClaim.analystName}
                </span>
              </div>
              <span
                style={{
                  fontSize: '11px',
                  fontFamily: 'var(--font-mono)',
                  color: 'var(--accent-cyan)',
                  padding: '2px 6px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--bg-tertiary)'
                }}
              >
                {aiClaim.confidenceScore}% CONFIDENCE
              </span>
            </div>

            {/* AI Claim Statement */}
            <div
              style={{
                padding: '12px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--bg-primary)',
                border: '1px solid var(--border-subtle)',
                marginBottom: '12px',
                fontFamily: 'var(--font-mono)',
                fontSize: '12px',
                lineHeight: 1.5,
                color: 'var(--text-primary)'
              }}
            >
              "{aiClaim.claimText}"
              <div style={{ marginTop: '8px', display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                {aiClaim.assertedIocs.map(ioc => (
                  <span
                    key={ioc}
                    style={{
                      fontSize: '10px',
                      padding: '2px 6px',
                      borderRadius: 'var(--radius-sm)',
                      backgroundColor: 'var(--bg-secondary)',
                      color: 'var(--accent-cyan)',
                      border: '1px solid var(--border-subtle)'
                    }}
                  >
                    IOC: {ioc}
                  </span>
                ))}
              </div>
            </div>

            <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '14px' }}>
              <strong>Recommended Action:</strong> {aiClaim.recommendedAction}
            </div>

            {/* Evidence Checklist to Audit Claim */}
            <div style={{ marginBottom: '16px' }}>
              <div
                style={{
                  fontSize: '11px',
                  fontFamily: 'var(--font-mono)',
                  fontWeight: 700,
                  color: 'var(--accent-cyan)',
                  marginBottom: '8px'
                }}
              >
                AUDIT AUTHORITATIVE EVIDENCE (Select corroborating / refuting logs):
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                {mission.events.map(event => {
                  const isChecked = selectedEvidenceIdsForAi.includes(event.id);
                  return (
                    <label
                      key={event.id}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '10px',
                        padding: '8px 10px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor: isChecked ? 'var(--bg-tertiary)' : 'transparent',
                        border: isChecked ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
                        cursor: 'pointer',
                        fontSize: '11px'
                      }}
                    >
                      <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => handleToggleEvidenceForAi(event.id)}
                      />
                      <span style={{ fontFamily: 'var(--font-mono)', color: 'var(--accent-cyan)', fontWeight: 700 }}>
                        {event.id}
                      </span>
                      <span style={{ color: 'var(--text-secondary)' }}>
                        {event.timestamp} - {event.eventType}: {event.summary}
                      </span>
                    </label>
                  );
                })}
              </div>
            </div>

            {/* Accept / Challenge Decision Buttons */}
            {!aiVerificationResult ? (
              <div style={{ display: 'flex', gap: '12px' }}>
                <button
                  onClick={() => handleAiDecision('ACCEPT_AI')}
                  style={{
                    flex: 1,
                    padding: '10px 14px',
                    backgroundColor: 'transparent',
                    border: '1px solid var(--border-strong)',
                    borderRadius: 'var(--radius-sm)',
                    color: 'var(--text-secondary)',
                    fontWeight: 700,
                    fontSize: '12px',
                    cursor: 'pointer'
                  }}
                >
                  ACCEPT AI
                </button>
                <button
                  onClick={() => handleAiDecision('CHALLENGE_AI')}
                  style={{
                    flex: 1,
                    padding: '10px 14px',
                    backgroundColor: 'var(--accent-cyan)',
                    border: 'none',
                    borderRadius: 'var(--radius-sm)',
                    color: '#0a0e17',
                    fontWeight: 800,
                    fontSize: '12px',
                    cursor: 'pointer'
                  }}
                >
                  CHALLENGE AI
                </button>
              </div>
            ) : (
              /* Authoritative Verification Result */
              <div
                style={{
                  padding: '16px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: aiVerificationResult.isAiFailureDetected
                    ? 'rgba(16, 185, 129, 0.08)'
                    : 'rgba(239, 68, 68, 0.08)',
                  border: aiVerificationResult.isAiFailureDetected
                    ? '1px solid var(--accent-emerald)'
                    : '1px solid var(--accent-rose)'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                  <h4
                    style={{
                      fontSize: '14px',
                      fontWeight: 900,
                      color: aiVerificationResult.isAiFailureDetected
                        ? 'var(--accent-emerald)'
                        : 'var(--accent-rose)',
                      fontFamily: 'var(--font-mono)',
                      letterSpacing: '0.05em'
                    }}
                  >
                    {aiVerificationResult.headline}
                  </h4>
                  <span
                    style={{
                      fontSize: '10px',
                      fontFamily: 'var(--font-mono)',
                      padding: '2px 8px',
                      borderRadius: '12px',
                      backgroundColor: aiVerificationResult.isAiFailureDetected ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)',
                      color: aiVerificationResult.isAiFailureDetected ? 'var(--accent-emerald)' : 'var(--accent-rose)',
                      fontWeight: 700
                    }}
                  >
                    {aiVerificationResult.evidenceVerified ? 'Evidence Verified ✓' : 'Authoritative Engine'}
                  </span>
                </div>

                <p style={{ fontSize: '12px', color: 'var(--text-primary)', lineHeight: 1.5, marginBottom: '8px' }}>
                  {aiVerificationResult.explanation}
                </p>

                {/* KILLER MOMENT: 3-Point Verified Breakdown */}
                {aiVerificationResult.isAiFailureDetected && (
                  <div
                    style={{
                      marginTop: '12px',
                      padding: '14px',
                      borderRadius: 'var(--radius-sm)',
                      backgroundColor: 'rgba(16, 185, 129, 0.12)',
                      border: '1px solid var(--accent-emerald)',
                      display: 'flex',
                      flexDirection: 'column',
                      gap: '8px'
                    }}
                  >
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <CheckCircle2 size={16} color="var(--accent-emerald)" />
                      <span style={{ fontSize: '13px', fontWeight: 900, color: 'var(--accent-emerald)', fontFamily: 'var(--font-mono)' }}>
                        VERIFIED CAPABILITY: AI Hallucination & Telemetry Triage
                      </span>
                    </div>

                    <div style={{ fontSize: '11px', color: 'var(--text-primary)', lineHeight: 1.4 }}>
                      Your decision was supported by the supplied telemetry.
                    </div>

                    <div style={{ fontSize: '11px', padding: '8px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
                      <div style={{ fontWeight: 700, color: 'var(--accent-rose)' }}>WHAT THE AI GOT WRONG:</div>
                      <div style={{ color: 'var(--text-secondary)', marginTop: '2px' }}>
                        The AI analyst asserted that IP 185.91.x.x was associated with the attack, but this address is completely absent from all supplied authentication logs.
                      </div>
                    </div>

                    <div style={{ fontSize: '11px', padding: '8px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
                      <div style={{ fontWeight: 700, color: 'var(--accent-emerald)' }}>WHAT EVIDENCE PROVED IT:</div>
                      <div style={{ color: 'var(--text-secondary)', marginTop: '2px' }}>
                        Domain Controller security events tl_01, tl_02, tl_03 (Event IDs 4625 & 4624) establish that the actual attack IP was 198.51.100.12 (Moscow, RU).
                      </div>
                    </div>

                    <div style={{ fontSize: '11px', padding: '8px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
                      <div style={{ fontWeight: 700, color: 'var(--accent-cyan)' }}>WHY YOUR REASONING WAS CORRECT:</div>
                      <div style={{ color: 'var(--text-secondary)', marginTop: '2px' }}>
                        You audited ground telemetry truth before accepting containment actions, preventing an ineffective firewall block and operational disruption.
                      </div>
                    </div>

                    <div style={{ fontSize: '10px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                      Proof Digest: {aiVerificationResult.evidenceDigest}
                    </div>
                  </div>
                )}

                {/* FAILURE AUTOPSY (7 Mandatory Elements) */}
                {aiVerificationResult.failureAutopsy && (
                  <div
                    style={{
                      marginTop: '14px',
                      padding: '14px',
                      borderRadius: 'var(--radius-sm)',
                      backgroundColor: 'var(--bg-primary)',
                      border: '1px solid rgba(239, 68, 68, 0.4)'
                    }}
                  >
                    <div
                      style={{
                        fontSize: '11px',
                        fontFamily: 'var(--font-mono)',
                        fontWeight: 800,
                        color: 'var(--accent-rose)',
                        marginBottom: '10px',
                        letterSpacing: '0.05em'
                      }}
                    >
                      FAILURE AUTOPSY // CANONICAL REASONING BREAKDOWN
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '11px' }}>
                      <div>
                        <span style={{ fontWeight: 700, color: 'var(--text-muted)' }}>1. YOUR DECISION: </span>
                        <span style={{ color: 'var(--text-primary)' }}>{aiVerificationResult.failureAutopsy.yourDecision}</span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 700, color: 'var(--text-muted)' }}>2. AI CLAIM: </span>
                        <span style={{ color: 'var(--text-primary)', fontFamily: 'var(--font-mono)' }}>"{aiVerificationResult.failureAutopsy.aiClaim}"</span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 700, color: 'var(--text-muted)' }}>3. EVIDENCE YOU USED: </span>
                        <span style={{ color: 'var(--accent-cyan)', fontFamily: 'var(--font-mono)' }}>
                          {aiVerificationResult.failureAutopsy.evidenceYouUsed.length > 0
                            ? aiVerificationResult.failureAutopsy.evidenceYouUsed.join(', ')
                            : 'None selected'}
                        </span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 700, color: 'var(--text-muted)' }}>4. EVIDENCE THAT MATTERED: </span>
                        <span style={{ color: 'var(--accent-emerald)', fontFamily: 'var(--font-mono)', fontWeight: 700 }}>
                          {aiVerificationResult.failureAutopsy.evidenceThatMattered.join(', ')}
                        </span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 700, color: 'var(--text-muted)' }}>5. WHAT WENT WRONG: </span>
                        <span style={{ color: 'var(--accent-amber)' }}>{aiVerificationResult.failureAutopsy.whatWentWrong}</span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 700, color: 'var(--text-muted)' }}>6. BETTER REASONING: </span>
                        <span style={{ color: 'var(--text-primary)' }}>{aiVerificationResult.failureAutopsy.betterReasoning}</span>
                      </div>
                      <div>
                        <span style={{ fontWeight: 700, color: 'var(--text-muted)' }}>7. NEXT CHALLENGE: </span>
                        <span style={{ color: 'var(--accent-cyan)', fontWeight: 700 }}>{aiVerificationResult.failureAutopsy.nextChallengeTitle}</span>
                      </div>
                    </div>

                    {!isAdaptiveModalOpen && (
                      <button
                        onClick={handleLaunchAdaptiveChallenge}
                        style={{
                          marginTop: '12px',
                          width: '100%',
                          padding: '10px',
                          backgroundColor: 'var(--accent-cyan)',
                          border: 'none',
                          borderRadius: 'var(--radius-sm)',
                          color: '#0a0e17',
                          fontWeight: 800,
                          fontSize: '12px',
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          gap: '6px'
                        }}
                      >
                        LAUNCH TARGETED ADAPTIVE CHALLENGE <ArrowRight size={14} />
                      </button>
                    )}
                  </div>
                )}

                {/* TARGETED ADAPTIVE CHALLENGE INLINE WORKSPACE */}
                {isAdaptiveModalOpen && (
                  <div
                    style={{
                      marginTop: '16px',
                      padding: '16px',
                      borderRadius: 'var(--radius-md)',
                      backgroundColor: 'var(--bg-secondary)',
                      border: '1px solid var(--accent-cyan)'
                    }}
                  >
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                      <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-cyan)', fontWeight: 700 }}>
                        ADAPTIVE CHALLENGE // REASONING RECOVERY
                      </span>
                      <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>
                        TIER: {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.challengeTier}
                      </span>
                    </div>

                    <h3 style={{ fontSize: '14px', fontWeight: 800, marginBottom: '6px', color: 'var(--text-primary)' }}>
                      {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.scenarioTitle}
                    </h3>
                    <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '12px' }}>
                      {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.scenarioBriefing}
                    </p>

                    {/* Adversarial Co-Pilot Claim */}
                    <div
                      style={{
                        padding: '10px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor: 'var(--bg-primary)',
                        border: '1px solid var(--border-subtle)',
                        fontSize: '11px',
                        fontFamily: 'var(--font-mono)',
                        color: 'var(--text-primary)',
                        marginBottom: '12px'
                      }}
                    >
                      <strong style={{ color: 'var(--accent-amber)' }}>
                        {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.aiAnalystClaim.analystName}:
                      </strong>{' '}
                      "{TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.aiAnalystClaim.claimText}"
                    </div>

                    {/* Evidence Checklist */}
                    <div style={{ marginBottom: '12px' }}>
                      <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-cyan)', fontWeight: 700, marginBottom: '6px' }}>
                        CORROBORATING EVIDENCE POOL:
                      </div>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                        {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.evidencePool.map(evi => {
                          const isChecked = adaptiveSelectedEvidence.includes(evi.id);
                          return (
                            <label
                              key={evi.id}
                              style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: '8px',
                                padding: '6px 8px',
                                borderRadius: 'var(--radius-sm)',
                                backgroundColor: isChecked ? 'var(--bg-tertiary)' : 'transparent',
                                border: isChecked ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
                                cursor: 'pointer',
                                fontSize: '11px'
                              }}
                            >
                              <input
                                type="checkbox"
                                checked={isChecked}
                                onChange={() => handleToggleAdaptiveEvidence(evi.id)}
                              />
                              <span style={{ fontFamily: 'var(--font-mono)', color: 'var(--accent-cyan)' }}>
                                {evi.id}
                              </span>
                              <span style={{ color: 'var(--text-secondary)' }}>
                                {evi.source}: {evi.summary}
                              </span>
                            </label>
                          );
                        })}
                      </div>
                    </div>

                    {/* Action Selection */}
                    <div style={{ marginBottom: '12px' }}>
                      <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-cyan)', fontWeight: 700, marginBottom: '6px' }}>
                        OPERATIONAL ACTION SELECTION:
                      </div>
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                        {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.actionOptions.map(action => {
                          const isSelected = adaptiveSelectedAction === action.id;
                          return (
                            <button
                              key={action.id}
                              onClick={() => setAdaptiveSelectedAction(action.id)}
                              style={{
                                textAlign: 'left',
                                padding: '8px 10px',
                                borderRadius: 'var(--radius-sm)',
                                backgroundColor: isSelected ? 'var(--accent-cyan-subtle)' : 'var(--bg-primary)',
                                border: isSelected ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
                                color: isSelected ? 'var(--accent-cyan)' : 'var(--text-primary)',
                                cursor: 'pointer',
                                fontSize: '11px'
                              }}
                            >
                              <div style={{ fontWeight: 700 }}>{action.label}</div>
                              <div style={{ fontSize: '10px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                                {action.description}
                              </div>
                            </button>
                          );
                        })}
                      </div>
                    </div>

                    {/* Adaptive Reasoning Capture */}
                    <div style={{ marginBottom: '12px' }}>
                      <label style={{ fontSize: '11px', fontWeight: 700, display: 'block', marginBottom: '4px' }}>
                        Forensic Reasoning:
                      </label>
                      <textarea
                        value={adaptiveReasoning}
                        onChange={e => setAdaptiveReasoning(e.target.value)}
                        placeholder="Explain why proxy 404 status and zero bytes transferred refutes the external alert..."
                        rows={2}
                        style={{
                          width: '100%',
                          padding: '8px',
                          borderRadius: 'var(--radius-sm)',
                          backgroundColor: 'var(--bg-primary)',
                          border: '1px solid var(--border-strong)',
                          color: 'var(--text-primary)',
                          fontSize: '11px'
                        }}
                      />
                    </div>

                    {/* Submit Adaptive Challenge */}
                    {!adaptiveResult ? (
                      <button
                        onClick={handleSubmitAdaptiveChallenge}
                        style={{
                          width: '100%',
                          padding: '10px',
                          backgroundColor: 'var(--accent-emerald)',
                          border: 'none',
                          borderRadius: 'var(--radius-sm)',
                          color: '#0a0e17',
                          fontWeight: 800,
                          fontSize: '12px',
                          cursor: 'pointer'
                        }}
                      >
                        SUBMIT ADAPTIVE TRIAGE
                      </button>
                    ) : (
                      /* IMPROVEMENT VERIFIED RESULT */
                      <div
                        style={{
                          padding: '12px',
                          borderRadius: 'var(--radius-sm)',
                          backgroundColor: adaptiveResult.isImprovementVerified
                            ? 'rgba(16, 185, 129, 0.12)'
                            : 'rgba(239, 68, 68, 0.12)',
                          border: adaptiveResult.isImprovementVerified
                            ? '1px solid var(--accent-emerald)'
                            : '1px solid var(--accent-rose)'
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                          <CheckCircle2
                            size={16}
                            color={adaptiveResult.isImprovementVerified ? 'var(--accent-emerald)' : 'var(--accent-rose)'}
                          />
                          <span
                            style={{
                              fontSize: '13px',
                              fontWeight: 900,
                              color: adaptiveResult.isImprovementVerified ? 'var(--accent-emerald)' : 'var(--accent-rose)',
                              fontFamily: 'var(--font-mono)'
                            }}
                          >
                            {adaptiveResult.headline}
                          </span>
                        </div>
                        <div style={{ fontSize: '11px', color: 'var(--text-primary)', marginBottom: '6px' }}>
                          {adaptiveResult.explanation}
                        </div>
                        {adaptiveResult.demonstratedImprovementSummary && (
                          <div
                            style={{
                              fontSize: '11px',
                              color: 'var(--accent-emerald)',
                              fontWeight: 700,
                              marginBottom: '6px'
                            }}
                          >
                            {adaptiveResult.demonstratedImprovementSummary}
                          </div>
                        )}
                        <div style={{ fontSize: '10px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                          Proof: {adaptiveResult.verifiedProofArtifactId} // Digest: {adaptiveResult.evidenceDigest.slice(0, 32)}...
                        </div>
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>

          {/* SOC OPERATOR DECISION TRIAGE */}
          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <h2 style={{ fontSize: '16px', fontWeight: 700, marginBottom: '16px' }}>
              SOC Operator Decision Triage
            </h2>

            {/* Questions List */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              {mission.questions.map((q, qIndex) => (
                <div
                  key={q.id}
                  style={{
                    padding: '16px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--bg-tertiary)',
                    border: '1px solid var(--border-subtle)'
                  }}
                >
                  <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-cyan)', fontWeight: 700, marginBottom: '6px' }}>
                    STEP {q.stepNumber} OF {mission.questions.length}
                  </div>
                  <h4 style={{ fontSize: '13px', fontWeight: 700, marginBottom: '12px' }}>
                    {q.questionText}
                  </h4>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {q.options.map((opt, optIndex) => {
                      const isSelected = selectedAnswers[q.id] === optIndex;
                      return (
                        <button
                          key={optIndex}
                          onClick={() => handleSelectAnswer(q.id, optIndex)}
                          style={{
                            textAlign: 'left',
                            padding: '10px 12px',
                            borderRadius: 'var(--radius-sm)',
                            backgroundColor: isSelected
                              ? 'var(--accent-cyan-subtle)'
                              : 'var(--bg-primary)',
                            border: isSelected
                              ? '1px solid var(--accent-cyan)'
                              : '1px solid var(--border-subtle)',
                            color: isSelected ? 'var(--accent-cyan)' : 'var(--text-primary)',
                            fontSize: '12px',
                            fontWeight: isSelected ? 700 : 400,
                            lineHeight: 1.4,
                            transition: 'all 0.1s ease',
                            cursor: 'pointer'
                          }}
                        >
                          {opt}
                        </button>
                      );
                    })}
                  </div>

                  {/* Progressive Hint Accordion */}
                  <div style={{ marginTop: '12px' }}>
                    <button
                      onClick={() => setOpenHintIndex(openHintIndex === qIndex ? null : qIndex)}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '6px',
                        fontSize: '11px',
                        color: 'var(--text-muted)',
                        fontWeight: 600,
                        backgroundColor: 'transparent',
                        border: 'none',
                        cursor: 'pointer'
                      }}
                    >
                      <HelpCircle size={12} />
                      <span>{openHintIndex === qIndex ? 'Hide Triage Hint' : 'Request Triage Hint'}</span>
                      {openHintIndex === qIndex ? <ChevronUp size={12} /> : <ChevronDown size={12} />}
                    </button>

                    {openHintIndex === qIndex && (
                      <div
                        style={{
                          marginTop: '8px',
                          padding: '10px',
                          borderRadius: 'var(--radius-sm)',
                          backgroundColor: 'var(--bg-primary)',
                          border: '1px solid var(--border-strong)',
                          fontSize: '11px',
                          color: 'var(--text-secondary)'
                        }}
                      >
                        <strong>Hint:</strong> {q.hints.conceptual}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>

            {/* Operator Reasoning Capture */}
            <div style={{ marginTop: '20px' }}>
              <label
                style={{
                  display: 'block',
                  fontSize: '12px',
                  fontWeight: 700,
                  marginBottom: '6px'
                }}
              >
                Forensic Reasoning & Containment Justification (Mandatory):
              </label>
              <textarea
                value={reasoning}
                onChange={e => setReasoning(e.target.value)}
                placeholder="Explain the technical evidence that justifies your decisions (minimum 15 characters)..."
                rows={3}
                style={{
                  width: '100%',
                  padding: '10px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--bg-primary)',
                  border: '1px solid var(--border-strong)',
                  color: 'var(--text-primary)',
                  fontSize: '12px',
                  resize: 'vertical'
                }}
              />
              <div style={{ fontSize: '11px', color: reasoning.length < 15 ? 'var(--accent-amber)' : 'var(--accent-emerald)', marginTop: '4px' }}>
                {reasoning.length < 15
                  ? `Reasoning length: ${reasoning.length}/15 chars (insufficient)`
                  : `Reasoning accepted (${reasoning.length} chars)`}
              </div>
            </div>

            {/* Action Buttons */}
            <div style={{ display: 'flex', gap: '12px', marginTop: '20px' }}>
              <button
                onClick={handleSubmitInvestigation}
                style={{
                  flex: 1,
                  padding: '12px 20px',
                  backgroundColor: 'var(--accent-cyan)',
                  color: '#0a0e17',
                  fontWeight: 800,
                  fontSize: '13px',
                  borderRadius: 'var(--radius-sm)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '8px',
                  border: 'none',
                  cursor: 'pointer'
                }}
              >
                <span>VERIFY & SUBMIT INVESTIGATION</span>
                <ArrowRight size={16} />
              </button>

              <button
                onClick={handleReset}
                title="Reset Answers"
                style={{
                  padding: '12px',
                  backgroundColor: 'var(--bg-tertiary)',
                  border: '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-sm)',
                  color: 'var(--text-muted)',
                  cursor: 'pointer'
                }}
              >
                <RotateCcw size={16} />
              </button>
            </div>

            {/* Outcome Display */}
            {validationOutcome && (
              <div
                style={{
                  marginTop: '20px',
                  padding: '16px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: validationOutcome.isPassed
                    ? 'var(--accent-emerald-subtle)'
                    : 'var(--accent-rose-subtle)',
                  border: validationOutcome.isPassed
                    ? '1px solid var(--accent-emerald)'
                    : '1px solid var(--accent-rose)'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  {validationOutcome.isPassed ? (
                    <CheckCircle2 color="var(--accent-emerald)" size={20} />
                  ) : (
                    <XCircle color="var(--accent-rose)" size={20} />
                  )}
                  <h4 style={{ fontSize: '14px', fontWeight: 800 }}>
                    {validationOutcome.isPassed
                      ? `VERIFIED // PASSED (${validationOutcome.scorePercent}%)`
                      : `FAILED // SCORE: ${validationOutcome.scorePercent}%`}
                  </h4>
                </div>

                <p style={{ fontSize: '12px', marginTop: '8px', color: 'var(--text-primary)', lineHeight: 1.5 }}>
                  {validationOutcome.reasoningFeedback}
                </p>

                {validationOutcome.isPassed && validationOutcome.evidenceHash && (
                  <div style={{ marginTop: '12px' }}>
                    <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-emerald)', fontWeight: 700 }}>
                      IMMUTABLE EVIDENCE HASH GENERATED:
                    </span>
                    <pre
                      style={{
                        marginTop: '4px',
                        padding: '6px 10px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor: 'var(--bg-primary)',
                        border: '1px solid var(--accent-emerald)',
                        fontSize: '11px',
                        color: 'var(--accent-emerald)',
                        fontFamily: 'var(--font-mono)',
                        wordBreak: 'break-all'
                      }}
                    >
                      {validationOutcome.evidenceHash}
                    </pre>
                  </div>
                )}

                {!validationOutcome.isPassed && validationOutcome.remediationAdvice && (
                  <div style={{ marginTop: '10px', fontSize: '12px', color: 'var(--accent-rose)' }}>
                    <strong>Remediation:</strong> {validationOutcome.remediationAdvice}
                  </div>
                )}
              </div>
            )}
          </div>

          {/* INVESTIGATION REPLAY // COGNITIVE TRACE (Shown whenever triage or AI decision occurs) */}
          {(aiVerificationResult || validationOutcome) && (
            <InvestigationReplayView
              steps={investigationSteps}
              divergence={reasoningDivergence}
              evidenceThatMattered={cyberRealityScenario.evidenceThatMatteredIds}
              evidenceUsed={selectedEvidenceIdsForAi}
            />
          )}
        </div>
      </div>
    </div>
  );
};
