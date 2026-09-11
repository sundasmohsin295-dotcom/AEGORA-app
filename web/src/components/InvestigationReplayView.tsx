import React from 'react';
import {
  Clock,
  CheckCircle2,
  AlertTriangle,
  ArrowRight,
  ShieldAlert,
  GitFork,
  FileSearch,
  Check
} from 'lucide-react';
import {
  InvestigationStep,
  ReasoningDivergence
} from '../core/cyberReality/CyberRealityEngine';

interface InvestigationReplayViewProps {
  steps: InvestigationStep[];
  divergence: ReasoningDivergence;
  evidenceThatMattered: string[];
  evidenceUsed: string[];
}

export const InvestigationReplayView: React.FC<InvestigationReplayViewProps> = ({
  steps,
  divergence,
  evidenceThatMattered,
  evidenceUsed
}) => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '20px',
        backgroundColor: 'var(--bg-secondary)',
        borderRadius: 'var(--radius-md)',
        border: '1px solid var(--border-subtle)',
        padding: '24px'
      }}
    >
      {/* Header */}
      <div style={{ borderBottom: '1px solid var(--border-subtle)', paddingBottom: '14px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <Clock size={16} color="var(--accent-cyan)" />
          <span
            style={{
              fontSize: '11px',
              fontFamily: 'var(--font-mono)',
              fontWeight: 800,
              color: 'var(--accent-cyan)'
            }}
          >
            INVESTIGATION REPLAY // COGNITIVE TRACE
          </span>
        </div>
        <h3 style={{ fontSize: '16px', fontWeight: 800, marginTop: '4px' }}>
          Investigation Forensic Audit & Path Replay
        </h3>
        <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>
          Educational trace of your forensic investigative sequence, evidence grounding, and decision points.
        </p>
      </div>

      {/* 3-Section Diagnostic Grid: Path (Left) | Evidence That Mattered (Middle) | Reasoning Divergence (Right) */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
          gap: '16px'
        }}
      >
        {/* SECTION 1: YOUR INVESTIGATION PATH */}
        <div
          style={{
            backgroundColor: 'var(--bg-primary)',
            padding: '16px',
            borderRadius: 'var(--radius-sm)',
            border: '1px solid var(--border-subtle)',
            display: 'flex',
            flexDirection: 'column'
          }}
        >
          <div
            style={{
              fontSize: '11px',
              fontFamily: 'var(--font-mono)',
              fontWeight: 800,
              color: 'var(--accent-cyan)',
              marginBottom: '12px'
            }}
          >
            YOUR INVESTIGATION PATH ({steps.length} STEPS)
          </div>

          <div
            style={{
              display: 'flex',
              flexDirection: 'column',
              gap: '10px',
              maxHeight: '360px',
              overflowY: 'auto'
            }}
          >
            {steps.length === 0 ? (
              <div style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                No active steps recorded yet.
              </div>
            ) : (
              steps.map((step, idx) => (
                <div
                  key={step.id}
                  style={{
                    display: 'flex',
                    alignItems: 'flex-start',
                    gap: '10px',
                    fontSize: '11px'
                  }}
                >
                  <span
                    style={{
                      fontFamily: 'var(--font-mono)',
                      color: 'var(--text-muted)',
                      flexShrink: 0,
                      marginTop: '2px'
                    }}
                  >
                    {step.timestamp}
                  </span>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontWeight: 700, color: 'var(--text-primary)' }}>
                      {step.targetLabel}
                    </div>
                    <div style={{ color: 'var(--text-secondary)', fontSize: '10px' }}>
                      {step.detail}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* SECTION 2: EVIDENCE THAT MATTERED */}
        <div
          style={{
            backgroundColor: 'var(--bg-primary)',
            padding: '16px',
            borderRadius: 'var(--radius-sm)',
            border: '1px solid var(--border-subtle)',
            display: 'flex',
            flexDirection: 'column'
          }}
        >
          <div
            style={{
              fontSize: '11px',
              fontFamily: 'var(--font-mono)',
              fontWeight: 800,
              color: 'var(--accent-emerald)',
              marginBottom: '12px'
            }}
          >
            EVIDENCE THAT MATTERED
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
            {evidenceThatMattered.map(eviId => {
              const wasUsed = evidenceUsed.includes(eviId);
              return (
                <div
                  key={eviId}
                  style={{
                    padding: '10px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: wasUsed ? 'rgba(16, 185, 129, 0.08)' : 'var(--bg-secondary)',
                    border: wasUsed ? '1px solid var(--accent-emerald)' : '1px solid var(--border-subtle)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between'
                  }}
                >
                  <div>
                    <div
                      style={{
                        fontSize: '11px',
                        fontFamily: 'var(--font-mono)',
                        fontWeight: 700,
                        color: wasUsed ? 'var(--accent-emerald)' : 'var(--text-primary)'
                      }}
                    >
                      {eviId}
                    </div>
                    <div style={{ fontSize: '10px', color: 'var(--text-secondary)' }}>
                      {eviId === 'tl_01' && 'Failed logon attempt (Event 4625) with Moscow IP'}
                      {eviId === 'tl_02' && 'Rapid burst logon failures confirming brute-force'}
                      {eviId === 'tl_03' && 'Successful logon (Event 4624) from Moscow IP'}
                      {eviId === 'tl_04' && 'Austin VPN logon confirming impossible travel'}
                    </div>
                  </div>
                  {wasUsed ? (
                    <span
                      style={{
                        fontSize: '9px',
                        fontFamily: 'var(--font-mono)',
                        fontWeight: 800,
                        color: 'var(--accent-emerald)',
                        padding: '2px 6px',
                        borderRadius: '2px',
                        backgroundColor: 'rgba(16, 185, 129, 0.15)'
                      }}
                    >
                      UTILIZED ✓
                    </span>
                  ) : (
                    <span
                      style={{
                        fontSize: '9px',
                        fontFamily: 'var(--font-mono)',
                        color: 'var(--text-muted)'
                      }}
                    >
                      OMITTED
                    </span>
                  )}
                </div>
              );
            })}
          </div>

          <div
            style={{
              marginTop: 'auto',
              paddingTop: '12px',
              fontSize: '11px',
              color: 'var(--text-muted)',
              lineHeight: 1.4
            }}
          >
            Authoritative SOC audit requires grounding all escalation or containment actions in verified event logs rather than secondary hearsay.
          </div>
        </div>

        {/* SECTION 3: WHERE YOUR REASONING DIVERGED */}
        <div
          style={{
            backgroundColor: divergence.hasDivergence
              ? 'rgba(239, 68, 68, 0.06)'
              : 'rgba(16, 185, 129, 0.06)',
            padding: '16px',
            borderRadius: 'var(--radius-sm)',
            border: divergence.hasDivergence
              ? '1px solid var(--accent-rose)'
              : '1px solid var(--accent-emerald)',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}
        >
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
              {divergence.hasDivergence ? (
                <GitFork size={16} color="var(--accent-rose)" />
              ) : (
                <CheckCircle2 size={16} color="var(--accent-emerald)" />
              )}
              <div
                style={{
                  fontSize: '11px',
                  fontFamily: 'var(--font-mono)',
                  fontWeight: 800,
                  color: divergence.hasDivergence ? 'var(--accent-rose)' : 'var(--accent-emerald)'
                }}
              >
                WHERE YOUR REASONING DIVERGED
              </div>
            </div>

            <p
              style={{
                fontSize: '12px',
                color: 'var(--text-primary)',
                lineHeight: 1.5,
                marginBottom: '12px'
              }}
            >
              {divergence.summary}
            </p>

            <div
              style={{
                padding: '10px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--bg-primary)',
                border: '1px solid var(--border-subtle)',
                fontSize: '11px',
                lineHeight: 1.4
              }}
            >
              <strong style={{ color: 'var(--accent-cyan)' }}>Optimal Forensic Path:</strong>
              <div style={{ marginTop: '4px', color: 'var(--text-secondary)' }}>
                {divergence.correctReasoningPath}
              </div>
            </div>
          </div>

          <div
            style={{
              marginTop: '14px',
              paddingTop: '10px',
              borderTop: '1px solid var(--border-subtle)',
              fontSize: '10px',
              fontFamily: 'var(--font-mono)',
              color: 'var(--text-muted)'
            }}
          >
            AEGORA Cyber Reality Replay Engine
          </div>
        </div>
      </div>
    </div>
  );
};
