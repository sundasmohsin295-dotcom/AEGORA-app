import React, { useState, useMemo } from 'react';
import {
  Shield,
  AlertTriangle,
  FileText,
  HelpCircle,
  CheckCircle2,
  XCircle,
  ArrowRight,
  ArrowLeft,
  Terminal,
  ChevronDown,
  ChevronUp,
  RotateCcw,
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

  // Progressive Disclosure: show full telemetry inspector
  const [showTelemetryDetails, setShowTelemetryDetails] = useState(false);

  // Workspace View Mode: TELEMETRY (Logs) | GRAPH (Evidence Graph) | EPISTEMIC (Controlled Uncertainty)
  const [workspaceMode, setWorkspaceMode] = useState<'TELEMETRY' | 'GRAPH' | 'EPISTEMIC'>('TELEMETRY');

  // AI Hallucination & Human Verification state
  const [selectedEvidenceIdsForAi, setSelectedEvidenceIdsForAi] = useState<string[]>([]);
  const [aiVerificationResult, setAiVerificationResult] = useState<ClientSafeAiVerificationResult | null>(null);
  const [lastAiDecision, setLastAiDecision] = useState<'ACCEPT_AI' | 'CHALLENGE_AI' | null>(null);
  const [isVerifyingDecision, setIsVerifyingDecision] = useState(false);

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
    setIsVerifyingDecision(true);
    addInvestigationStep(
      decision,
      decision === 'CHALLENGE_AI' ? 'Challenged AI Analyst Claim' : 'Accepted AI Analyst Claim',
      `Submitted human verification decision for claim ${aiClaim.claimId}`
    );

    // Innovation A: 320ms crisp security verification transition
    setTimeout(() => {
      const outcome = evaluateWebAiClaimChallenge(
        `att_${Date.now()}`,
        aiClaim.claimId,
        decision,
        selectedEvidenceIdsForAi,
        reasoning
      );
      setAiVerificationResult(outcome as ClientSafeAiVerificationResult);
      setIsVerifyingDecision(false);
    }, 320);
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
    <div style={{ maxWidth: '1080px', margin: '0 auto', padding: '36px 24px 64px 24px' }}>
      {/* Return Button */}
      <button
        onClick={onReturnToCommandCenter}
        style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '8px',
          fontSize: '13px',
          color: 'var(--text-secondary)',
          marginBottom: '24px',
          cursor: 'pointer'
        }}
      >
        <ArrowLeft size={16} />
        <span>Return to Home</span>
      </button>

      {/* HEADER: Flagship Coral/Red Section (#FF5C67) */}
      <section style={{ marginBottom: '28px' }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '8px', color: 'var(--color-investigate)', marginBottom: '8px' }}>
          <Shield size={18} />
          <span style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.06em' }}>
            FLAGSHIP OPERATION
          </span>
        </div>
        <h1 style={{ fontSize: '32px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '6px' }}>
          INVESTIGATE
        </h1>
        <p style={{ fontSize: '16px', color: 'var(--text-secondary)' }}>
          Reality-based cybersecurity missions.
        </p>
      </section>

      {/* LARGE MISSION HERO */}
      <section
        style={{
          padding: '32px',
          borderRadius: 'var(--radius-lg)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--color-investigate)',
          boxShadow: '0 8px 24px -6px rgba(255, 92, 103, 0.15)',
          marginBottom: '36px'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <h2 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '8px' }}>
              SUSPICIOUS LOGIN
            </h2>
            <p style={{ fontSize: '15px', color: 'var(--text-secondary)', lineHeight: 1.5, maxWidth: '640px' }}>
              An AI analyst has made a claim. Can you prove it?
            </p>
          </div>

          <div
            style={{
              padding: '6px 14px',
              borderRadius: 'var(--radius-full)',
              backgroundColor: 'rgba(255, 92, 103, 0.12)',
              color: 'var(--color-investigate)',
              fontSize: '12px',
              fontWeight: 700
            }}
          >
            SEV-2 Incident
          </div>
        </div>
      </section>

      {/* 3 CLEAR SECTIONS: AI CLAIM, EVIDENCE, YOUR DECISION */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '32px' }}>

        {/* SECTION 1: AI CLAIM */}
        <section
          style={{
            padding: '28px',
            borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--bg-secondary)',
            border: '1px solid var(--border-subtle)'
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.06em', color: 'var(--color-warning)' }}>
                1. AI CLAIM
              </span>
              <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                • {aiClaim.analystName}
              </span>
            </div>

            <span
              style={{
                fontSize: '12px',
                fontWeight: 700,
                color: 'var(--color-home)'
              }}
            >
              {aiClaim.confidenceScore}% Confidence
            </span>
          </div>

          <div
            style={{
              padding: '16px 20px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--border-subtle)',
              fontSize: '15px',
              color: 'var(--text-primary)',
              lineHeight: 1.6,
              marginBottom: '14px'
            }}
          >
            "{aiClaim.claimText}"
          </div>

          <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
            <strong>Recommended Action:</strong> {aiClaim.recommendedAction}
          </div>
        </section>

        {/* SECTION 2: EVIDENCE */}
        <section
          style={{
            padding: '28px',
            borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--bg-secondary)',
            border: '1px solid var(--border-subtle)'
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap', gap: '12px' }}>
            <div>
              <span style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.06em', color: 'var(--color-home)' }}>
                2. EVIDENCE
              </span>
              <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                Select corroborating or refuting telemetry logs to audit the claim.
              </p>
            </div>

            {/* View Mode Switcher */}
            <div style={{ display: 'flex', gap: '6px' }}>
              {[
                { id: 'TELEMETRY', label: 'Logs' },
                { id: 'GRAPH', label: 'Graph' },
                { id: 'EPISTEMIC', label: 'Audit' }
              ].map(mode => (
                <button
                  key={mode.id}
                  onClick={() => setWorkspaceMode(mode.id as any)}
                  style={{
                    padding: '6px 12px',
                    borderRadius: 'var(--radius-full)',
                    backgroundColor: workspaceMode === mode.id ? 'var(--bg-tertiary)' : 'transparent',
                    border: workspaceMode === mode.id ? '1px solid var(--border-strong)' : '1px solid transparent',
                    color: workspaceMode === mode.id ? 'var(--text-primary)' : 'var(--text-muted)',
                    fontSize: '12px',
                    fontWeight: 600,
                    cursor: 'pointer'
                  }}
                >
                  {mode.label}
                </button>
              ))}
            </div>
          </div>

          {/* Mode: Telemetry Logs */}
          {workspaceMode === 'TELEMETRY' && (
            <div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                {mission.events.map(event => {
                  const isChecked = selectedEvidenceIdsForAi.includes(event.id);
                  const isSelected = selectedEventId === event.id;
                  return (
                    <div
                      key={event.id}
                      style={{
                        padding: '14px 18px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor: isChecked ? 'var(--bg-tertiary)' : 'transparent',
                        border: isChecked ? '1px solid var(--color-home)' : '1px solid var(--border-subtle)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'space-between',
                        gap: '14px',
                        cursor: 'pointer'
                      }}
                    >
                      <label
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: '12px',
                          cursor: 'pointer',
                          flex: 1
                        }}
                      >
                        <input
                          type="checkbox"
                          checked={isChecked}
                          onChange={() => handleToggleEvidenceForAi(event.id)}
                          style={{ width: '16px', height: '16px' }}
                        />
                        <div>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                            <span style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-primary)' }}>
                              {event.summary}
                            </span>
                            <span
                              style={{
                                fontSize: '11px',
                                color: event.isSuspicious ? 'var(--color-error)' : 'var(--color-success)',
                                fontWeight: 700
                              }}
                            >
                              {event.eventType}
                            </span>
                          </div>
                          <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
                            {event.timestamp} • {event.processOrUser}
                          </div>
                        </div>
                      </label>

                      <button
                        onClick={() => {
                          handleSelectEvent(event.id);
                          setShowTelemetryDetails(true);
                        }}
                        style={{
                          fontSize: '11px',
                          color: 'var(--color-home)',
                          fontWeight: 600,
                          cursor: 'pointer'
                        }}
                      >
                        View Raw
                      </button>
                    </div>
                  );
                })}
              </div>

              {/* Collapsible Raw Inspector */}
              {showTelemetryDetails && (
                <div
                  style={{
                    marginTop: '16px',
                    padding: '16px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--bg-primary)',
                    border: '1px solid var(--border-subtle)'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '12px', fontWeight: 700, color: 'var(--text-primary)' }}>
                      <Terminal size={14} color="var(--color-home)" />
                      <span>Raw Log Payload ({activeEvent.id})</span>
                    </div>
                    <button
                      onClick={() => setShowTelemetryDetails(false)}
                      style={{ fontSize: '11px', color: 'var(--text-muted)', cursor: 'pointer' }}
                    >
                      Close
                    </button>
                  </div>
                  <pre
                    style={{
                      fontSize: '11px',
                      color: 'var(--text-secondary)',
                      fontFamily: 'var(--font-mono)',
                      whiteSpace: 'pre-wrap',
                      wordBreak: 'break-all',
                      lineHeight: 1.5
                    }}
                  >
                    {activeEvent.rawLog}
                  </pre>
                </div>
              )}
            </div>
          )}

          {/* Mode: Evidence Graph */}
          {workspaceMode === 'GRAPH' && (
            <EvidenceGraphView
              scenario={cyberRealityScenario}
              selectedEvidenceIds={selectedEvidenceIdsForAi}
              onToggleEvidence={handleToggleEvidenceForAi}
              onNodeInspected={handleGraphNodeInspected}
            />
          )}

          {/* Mode: Epistemic Audit */}
          {workspaceMode === 'EPISTEMIC' && (
            <EpistemicAuditView
              signals={cyberRealityScenario.epistemicSignals}
            />
          )}
        </section>

        {/* SECTION 3: YOUR DECISION */}
        <section
          style={{
            padding: '28px',
            borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--bg-secondary)',
            border: '1px solid var(--border-subtle)'
          }}
        >
          <div style={{ marginBottom: '16px' }}>
            <span style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.06em', color: 'var(--color-investigate)' }}>
              3. YOUR DECISION
            </span>
            <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '2px' }}>
              Based on the ground telemetry truth, does the AI claim hold up?
            </p>
          </div>

          {isVerifyingDecision ? (
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                padding: '16px 20px',
                backgroundColor: 'var(--bg-tertiary)',
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--border-subtle)'
              }}
            >
              <Activity size={18} color="var(--color-home)" />
              <span style={{ fontSize: '13px', fontWeight: 600, fontFamily: 'var(--font-mono)', color: 'var(--text-primary)' }}>
                [CORROBORATING HOST & NETWORK TELEMETRY...]
              </span>
            </div>
          ) : !aiVerificationResult ? (
            <div style={{ display: 'flex', gap: '14px', flexWrap: 'wrap' }}>
              <button
                onClick={() => handleAiDecision('ACCEPT_AI')}
                style={{
                  flex: 1,
                  minWidth: '160px',
                  padding: '14px 20px',
                  backgroundColor: 'transparent',
                  border: '1px solid var(--border-strong)',
                  borderRadius: 'var(--radius-md)',
                  color: 'var(--text-secondary)',
                  fontSize: '14px',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                ACCEPT AI
              </button>

              <button
                onClick={() => handleAiDecision('CHALLENGE_AI')}
                style={{
                  flex: 1,
                  minWidth: '160px',
                  padding: '14px 20px',
                  backgroundColor: 'var(--color-investigate)',
                  border: 'none',
                  borderRadius: 'var(--radius-md)',
                  color: '#FFFFFF',
                  fontSize: '14px',
                  fontWeight: 700,
                  boxShadow: '0 4px 14px rgba(255, 92, 103, 0.35)',
                  cursor: 'pointer'
                }}
              >
                CHALLENGE AI
              </button>
            </div>
          ) : (
            /* Authoritative Verification Result */
            <div style={{ animation: 'fadeIn 0.25s ease-out' }}>
              {/* KILLER MOMENT BANNER */}
              <div
                style={{
                  padding: '20px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: aiVerificationResult.isAiFailureDetected
                    ? 'rgba(53, 211, 154, 0.12)'
                    : 'rgba(255, 92, 103, 0.12)',
                  border: aiVerificationResult.isAiFailureDetected
                    ? '1px solid var(--color-success)'
                    : '1px solid var(--color-error)',
                  marginBottom: '20px'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '8px', marginBottom: '8px' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <CheckCircle2
                      size={20}
                      color={aiVerificationResult.isAiFailureDetected ? 'var(--color-success)' : 'var(--color-error)'}
                    />
                    <span
                      style={{
                        fontSize: '16px',
                        fontWeight: 800,
                        color: aiVerificationResult.isAiFailureDetected ? 'var(--color-success)' : 'var(--color-error)'
                      }}
                    >
                      {aiVerificationResult.isAiFailureDetected
                        ? 'AI FAILURE DETECTED ✓'
                        : 'DECISION RECORDED'}
                    </span>
                  </div>

                  <span
                    style={{
                      fontSize: '12px',
                      fontWeight: 700,
                      color: 'var(--color-success)'
                    }}
                  >
                    EVIDENCE VERIFIED ✓
                  </span>
                </div>

                <p style={{ fontSize: '14px', color: 'var(--text-primary)', lineHeight: 1.5 }}>
                  {aiVerificationResult.explanation}
                </p>
              </div>

              {/* Clean Failure Autopsy (When Challenged) */}
              {aiVerificationResult.failureAutopsy && (
                <div
                  style={{
                    padding: '20px',
                    borderRadius: 'var(--radius-md)',
                    backgroundColor: 'var(--bg-tertiary)',
                    border: '1px solid var(--border-subtle)',
                    marginBottom: '20px'
                  }}
                >
                  <div style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px' }}>
                    Failure Autopsy Breakdown
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', fontSize: '13px' }}>
                    <div>
                      <span style={{ color: 'var(--text-muted)' }}>What went wrong: </span>
                      <span style={{ color: 'var(--color-warning)' }}>{aiVerificationResult.failureAutopsy.whatWentWrong}</span>
                    </div>
                    <div>
                      <span style={{ color: 'var(--text-muted)' }}>Better reasoning: </span>
                      <span style={{ color: 'var(--text-primary)' }}>{aiVerificationResult.failureAutopsy.betterReasoning}</span>
                    </div>
                  </div>

                  {!isAdaptiveModalOpen && (
                    <button
                      onClick={handleLaunchAdaptiveChallenge}
                      style={{
                        marginTop: '16px',
                        padding: '10px 18px',
                        backgroundColor: 'var(--color-home)',
                        color: '#FFFFFF',
                        borderRadius: 'var(--radius-sm)',
                        fontSize: '13px',
                        fontWeight: 700,
                        cursor: 'pointer'
                      }}
                    >
                      Launch Adaptive Remediation Challenge
                    </button>
                  )}
                </div>
              )}

              {/* TARGETED ADAPTIVE CHALLENGE MODAL / WORKSPACE */}
              {isAdaptiveModalOpen && (
                <div
                  style={{
                    padding: '20px',
                    borderRadius: 'var(--radius-md)',
                    backgroundColor: 'var(--bg-tertiary)',
                    border: '1px solid var(--color-home)',
                    marginBottom: '20px'
                  }}
                >
                  <h3 style={{ fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '6px' }}>
                    {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.scenarioTitle}
                  </h3>
                  <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '14px' }}>
                    {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.scenarioBriefing}
                  </p>

                  {/* Actions */}
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginBottom: '14px' }}>
                    {TARGETED_ADAPTIVE_CHALLENGE_OVERWEIGHTING.actionOptions.map(action => (
                      <button
                        key={action.id}
                        onClick={() => setAdaptiveSelectedAction(action.id)}
                        style={{
                          textAlign: 'left',
                          padding: '10px 14px',
                          borderRadius: 'var(--radius-sm)',
                          backgroundColor: adaptiveSelectedAction === action.id ? 'rgba(77, 141, 255, 0.15)' : 'var(--bg-secondary)',
                          border: adaptiveSelectedAction === action.id ? '1px solid var(--color-home)' : '1px solid var(--border-subtle)',
                          color: adaptiveSelectedAction === action.id ? 'var(--color-home)' : 'var(--text-primary)',
                          fontSize: '13px',
                          cursor: 'pointer'
                        }}
                      >
                        <div style={{ fontWeight: 700 }}>{action.label}</div>
                        <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '2px' }}>
                          {action.description}
                        </div>
                      </button>
                    ))}
                  </div>

                  {!adaptiveResult ? (
                    <button
                      onClick={handleSubmitAdaptiveChallenge}
                      style={{
                        padding: '10px 20px',
                        backgroundColor: 'var(--color-practice)',
                        color: '#07090D',
                        borderRadius: 'var(--radius-sm)',
                        fontSize: '13px',
                        fontWeight: 700,
                        cursor: 'pointer'
                      }}
                    >
                      Submit Adaptive Action
                    </button>
                  ) : (
                    <div
                      style={{
                        padding: '12px 16px',
                        borderRadius: 'var(--radius-sm)',
                        backgroundColor: 'rgba(53, 211, 154, 0.12)',
                        border: '1px solid var(--color-practice)',
                        color: 'var(--color-practice)',
                        fontSize: '13px',
                        fontWeight: 700
                      }}
                    >
                      {adaptiveResult.headline} • Improvement Verified ✓
                    </div>
                  )}
                </div>
              )}
            </div>
          )}
        </section>

        {/* SECTION 4: FINAL MISSION SUBMIT (Diagnostic Triage Questions) */}
        <section
          style={{
            padding: '28px',
            borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--bg-secondary)',
            border: '1px solid var(--border-subtle)'
          }}
        >
          <div style={{ marginBottom: '20px' }}>
            <span style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.06em', color: 'var(--color-proof)' }}>
              4. MISSION VERIFICATION
            </span>
            <h3 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '4px' }}>
              Document Incident Findings
            </h3>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', marginBottom: '24px' }}>
            {mission.questions.map((q, qIndex) => (
              <div
                key={q.id}
                style={{
                  padding: '18px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--bg-tertiary)',
                  border: '1px solid var(--border-subtle)'
                }}
              >
                <div style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-primary)', marginBottom: '12px' }}>
                  {q.questionText}
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  {q.options.map((opt, optIndex) => {
                    const isSelected = selectedAnswers[q.id] === optIndex;
                    return (
                      <button
                        key={optIndex}
                        onClick={() => handleSelectAnswer(q.id, optIndex)}
                        style={{
                          textAlign: 'left',
                          padding: '10px 14px',
                          borderRadius: 'var(--radius-sm)',
                          backgroundColor: isSelected ? 'rgba(77, 141, 255, 0.15)' : 'var(--bg-primary)',
                          border: isSelected ? '1px solid var(--color-home)' : '1px solid var(--border-subtle)',
                          color: isSelected ? 'var(--color-home)' : 'var(--text-primary)',
                          fontSize: '13px',
                          fontWeight: isSelected ? 600 : 400,
                          cursor: 'pointer'
                        }}
                      >
                        {opt}
                      </button>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>

          {/* Reasoning textarea */}
          <div style={{ marginBottom: '24px' }}>
            <label style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)', display: 'block', marginBottom: '6px' }}>
              Forensic Reasoning (Minimum 15 characters):
            </label>
            <textarea
              value={reasoning}
              onChange={e => setReasoning(e.target.value)}
              placeholder="State the ground telemetry truth and corroborating events..."
              rows={3}
              style={{
                width: '100%',
                padding: '12px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--bg-primary)',
                border: '1px solid var(--border-subtle)',
                color: 'var(--text-primary)',
                fontSize: '13px',
                outline: 'none'
              }}
            />
          </div>

          {/* Action row */}
          <div style={{ display: 'flex', gap: '12px' }}>
            <button
              onClick={handleSubmitInvestigation}
              style={{
                flex: 1,
                padding: '14px 24px',
                backgroundColor: 'var(--color-home)',
                color: '#FFFFFF',
                borderRadius: 'var(--radius-md)',
                fontSize: '15px',
                fontWeight: 700,
                cursor: 'pointer'
              }}
            >
              Verify & Submit Mission
            </button>

            <button
              onClick={handleReset}
              style={{
                padding: '14px 18px',
                backgroundColor: 'var(--bg-tertiary)',
                color: 'var(--text-muted)',
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--border-subtle)',
                cursor: 'pointer'
              }}
            >
              <RotateCcw size={18} />
            </button>
          </div>

          {/* Validation Outcome Result */}
          {validationOutcome && (
            <div
              style={{
                marginTop: '24px',
                padding: '20px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: validationOutcome.isPassed ? 'rgba(53, 211, 154, 0.1)' : 'rgba(255, 92, 103, 0.1)',
                border: validationOutcome.isPassed ? '1px solid var(--color-success)' : '1px solid var(--color-error)'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                <CheckCircle2 color={validationOutcome.isPassed ? 'var(--color-success)' : 'var(--color-error)'} size={18} />
                <span style={{ fontSize: '15px', fontWeight: 800, color: validationOutcome.isPassed ? 'var(--color-success)' : 'var(--color-error)' }}>
                  {validationOutcome.isPassed ? `PASSED (${validationOutcome.scorePercent}%)` : `SCORE: ${validationOutcome.scorePercent}%`}
                </span>
              </div>
              <p style={{ fontSize: '13px', color: 'var(--text-primary)', lineHeight: 1.5 }}>
                {validationOutcome.reasoningFeedback}
              </p>
            </div>
          )}
        </section>

        {/* Cognitive Trace / Replay (Progressive Disclosure) */}
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
  );
};
