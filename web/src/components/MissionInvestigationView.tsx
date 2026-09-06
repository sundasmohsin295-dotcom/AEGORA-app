import React, { useState } from 'react';
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
  RotateCcw
} from 'lucide-react';
import {
  SUSPICIOUS_LOGIN_MISSION,
  validateWebMission
} from '../core/missions/SuspiciousLoginMission';
import { MissionValidationOutcome } from '../core/types/platform';

interface MissionInvestigationViewProps {
  onReturnToCommandCenter: () => void;
}

export const MissionInvestigationView: React.FC<MissionInvestigationViewProps> = ({
  onReturnToCommandCenter
}) => {
  const mission = SUSPICIOUS_LOGIN_MISSION;

  const [selectedAnswers, setSelectedAnswers] = useState<Record<string, number>>({});
  const [reasoning, setReasoning] = useState<string>('');
  const [openHintIndex, setOpenHintIndex] = useState<number | null>(null);
  const [validationOutcome, setValidationOutcome] = useState<MissionValidationOutcome | null>(null);
  const [selectedEventId, setSelectedEventId] = useState<string>(mission.events[0].id);

  const handleSelectAnswer = (questionId: string, optionIndex: number) => {
    setSelectedAnswers(prev => ({ ...prev, [questionId]: optionIndex }));
  };

  const handleSubmitInvestigation = () => {
    const outcome = validateWebMission(selectedAnswers, reasoning);
    setValidationOutcome(outcome);
  };

  const handleReset = () => {
    setSelectedAnswers({});
    setReasoning('');
    setValidationOutcome(null);
    setOpenHintIndex(null);
  };

  const activeEvent = mission.events.find(e => e.id === selectedEventId) || mission.events[0];

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '24px' }}>
      {/* Top Mission Header */}
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
                fontWeight: 700
              }}
            >
              SEV-2 ALERT // LIVE SOC RANGE
            </span>
            <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
              Domain: {mission.targetDomain}
            </span>
          </div>
          <h1 style={{ fontSize: '20px', fontWeight: 800 }}>{mission.title}</h1>
          <p style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '2px' }}>
            {mission.objective}
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
            fontWeight: 600
          }}
        >
          Return to Command Center
        </button>
      </div>

      {/* 2-Column Split: Telemetry & Event Inspector (Left) | Decision Triage (Right) */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(460px, 1fr))', gap: '24px' }}>
        {/* LEFT COLUMN: TELEMETRY & LOG VIEWER */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
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
                  onClick={() => setSelectedEventId(event.id)}
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
        </div>

        {/* RIGHT COLUMN: DECISION TRIAGE & EVIDENCE VERIFICATION */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
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
                            transition: 'all 0.1s ease'
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
                        fontWeight: 600
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
                  gap: '8px'
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
                  color: 'var(--text-muted)'
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
        </div>
      </div>
    </div>
  );
};
