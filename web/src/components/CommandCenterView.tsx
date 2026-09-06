import React from 'react';
import {
  Terminal,
  Shield,
  Activity,
  ArrowRight,
  TrendingUp,
  Award,
  AlertTriangle,
  Lock,
  Cpu,
  Target,
  ExternalLink,
  ChevronRight
} from 'lucide-react';
import { WebCapabilityEngine } from '../core/intelligence/DemonstratedCapabilityEngine';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';

interface CommandCenterViewProps {
  authState: AuthState;
  onLaunchMission: (missionId: string) => void;
  onViewPassport: () => void;
  onOpenSecurityModal: () => void;
}

export const CommandCenterView: React.FC<CommandCenterViewProps> = ({
  authState,
  onLaunchMission,
  onViewPassport,
  onOpenSecurityModal
}) => {
  const clusters = WebCapabilityEngine.getAuthoritativeClusters();
  const nextMove = WebCapabilityEngine.getPredictiveNextMove();
  const careerSignal = WebCapabilityEngine.getCareerSignal();
  const cyberTreasure = WebCapabilityEngine.getCyberTreasure();

  const operatorId = authState.identity
    ? authState.identity.canonicalLearnerId
    : 'operator_guest_mode';

  return (
    <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '24px' }}>
      {/* 1. OPERATOR HUD */}
      <section
        style={{
          padding: '20px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          marginBottom: '24px',
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: '16px'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
          <div
            style={{
              width: '48px',
              height: '48px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--accent-cyan)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--accent-cyan)'
            }}
          >
            <Terminal size={24} />
          </div>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <span style={{ fontSize: '12px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                OPERATOR //
              </span>
              <span style={{ fontSize: '16px', fontWeight: 800, fontFamily: 'var(--font-mono)' }}>
                {operatorId}
              </span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginTop: '2px' }}>
              <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
                Target: <strong>{careerSignal.targetRole}</strong>
              </span>
              <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>•</span>
              <span
                style={{
                  fontSize: '11px',
                  fontFamily: 'var(--font-mono)',
                  color: 'var(--accent-emerald)',
                  fontWeight: 600
                }}
              >
                PLATFORM CONTINUITY ACTIVE
              </span>
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
              READINESS
            </div>
            <div style={{ fontSize: '20px', fontWeight: 800, color: 'var(--accent-cyan)' }}>
              {careerSignal.currentReadinessScore}%
            </div>
          </div>
          <div style={{ width: '1px', height: '32px', backgroundColor: 'var(--border-subtle)' }} />
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
              VERIFIED PROOFS
            </div>
            <div style={{ fontSize: '20px', fontWeight: 800, color: 'var(--accent-emerald)' }}>
              {careerSignal.verifiedProofCount}
            </div>
          </div>
          <div style={{ width: '1px', height: '32px', backgroundColor: 'var(--border-subtle)' }} />
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
              EST. DAYS
            </div>
            <div style={{ fontSize: '20px', fontWeight: 800, color: 'var(--accent-amber)' }}>
              {careerSignal.daysToReadiness}d
            </div>
          </div>
        </div>
      </section>

      {/* 2. DOMINANT NEXT MOVE HERO */}
      <section
        style={{
          padding: '24px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-card)',
          border: '1px solid var(--accent-cyan)',
          boxShadow: 'var(--shadow-md)',
          marginBottom: '28px',
          position: 'relative',
          overflow: 'hidden'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px' }}>
          <div style={{ flex: '1 1 500px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
              <span
                style={{
                  fontSize: '11px',
                  fontFamily: 'var(--font-mono)',
                  fontWeight: 700,
                  padding: '2px 8px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--accent-cyan-subtle)',
                  color: 'var(--accent-cyan)',
                  border: '1px solid var(--accent-cyan)'
                }}
              >
                CRITICAL NEXT MOVE // BOTTLENECK RESOLUTION
              </span>
              <span style={{ fontSize: '12px', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                GATE: {nextMove.primaryGateTargeted}
              </span>
            </div>

            <h2 style={{ fontSize: '22px', fontWeight: 800, marginBottom: '8px' }}>
              {nextMove.title}
            </h2>

            <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.5, marginBottom: '12px' }}>
              {nextMove.reason}
            </p>

            <div style={{ display: 'flex', alignItems: 'center', gap: '16px', fontSize: '12px', color: 'var(--text-muted)' }}>
              <span>Category: <strong>{nextMove.category}</strong></span>
              <span>•</span>
              <span>Estimated Duration: <strong>{nextMove.estimatedMinutes} min</strong></span>
              <span>•</span>
              <span>Expected Impact: <strong style={{ color: 'var(--accent-emerald)' }}>{nextMove.expectedImpact}</strong></span>
            </div>
          </div>

          <button
            onClick={() => onLaunchMission(nextMove.missionId)}
            style={{
              padding: '14px 28px',
              backgroundColor: 'var(--accent-cyan)',
              color: '#0a0e17',
              fontWeight: 800,
              fontSize: '14px',
              borderRadius: 'var(--radius-sm)',
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              alignSelf: 'center',
              boxShadow: 'var(--shadow-sm)'
            }}
          >
            <span>START INVESTIGATION</span>
            <ArrowRight size={18} />
          </button>
        </div>
      </section>

      {/* 2-COLUMN MAIN CONTENT: Cyber Twin (Left) + Career & Treasure (Right) */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(420px, 1fr))', gap: '24px' }}>
        {/* LEFT COLUMN: CYBER TWIN 6.0 */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          {/* Cyber Twin Header Card */}
          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <Cpu size={20} color="var(--accent-cyan)" />
                <h3 style={{ fontSize: '16px', fontWeight: 700 }}>
                  Cyber Twin 6.0 // Authoritative Cognitive Clusters
                </h3>
              </div>
              <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                7-GATE RUBRIC VERIFIED
              </span>
            </div>

            {/* 4 Authoritative Clusters */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {clusters.map((cluster, i) => (
                <div
                  key={i}
                  style={{
                    padding: '14px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--bg-tertiary)',
                    border:
                      cluster.status === 'BOTTLENECK'
                        ? '1px solid var(--accent-amber)'
                        : '1px solid var(--border-subtle)'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <span style={{ fontWeight: 700, fontSize: '14px' }}>{cluster.name}</span>
                      {cluster.status === 'BOTTLENECK' && (
                        <span
                          style={{
                            fontSize: '10px',
                            fontFamily: 'var(--font-mono)',
                            padding: '1px 6px',
                            borderRadius: 'var(--radius-sm)',
                            backgroundColor: 'var(--accent-amber-subtle)',
                            color: 'var(--accent-amber)',
                            fontWeight: 700
                          }}
                        >
                          BOTTLENECK
                        </span>
                      )}
                    </div>
                    <span
                      style={{
                        fontFamily: 'var(--font-mono)',
                        fontWeight: 700,
                        fontSize: '14px',
                        color:
                          cluster.score >= 80
                            ? 'var(--accent-emerald)'
                            : cluster.score >= 70
                            ? 'var(--accent-cyan)'
                            : 'var(--accent-amber)'
                      }}
                    >
                      {cluster.score}/100
                    </span>
                  </div>

                  {/* Progress Meter */}
                  <div
                    style={{
                      height: '6px',
                      borderRadius: '3px',
                      backgroundColor: 'var(--bg-primary)',
                      overflow: 'hidden',
                      marginBottom: '8px'
                    }}
                  >
                    <div
                      style={{
                        height: '100%',
                        width: `${cluster.score}%`,
                        backgroundColor:
                          cluster.status === 'BOTTLENECK'
                            ? 'var(--accent-amber)'
                            : cluster.score >= 80
                            ? 'var(--accent-emerald)'
                            : 'var(--accent-cyan)',
                        borderRadius: '3px'
                      }}
                    />
                  </div>

                  <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                    {cluster.subtitle}
                  </p>
                </div>
              ))}
            </div>
          </div>

          {/* Zero -> Job Ready Pipeline */}
          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <h4 style={{ fontSize: '14px', fontWeight: 700, marginBottom: '12px' }}>
              Zero → Job Ready Career Pipeline
            </h4>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '8px' }}>
              {[
                { label: 'Foundation', done: true },
                { label: 'Triage Operator', done: true },
                { label: 'Incident Responder', current: true },
                { label: 'Detection Specialist', done: false },
                { label: 'Job Ready', done: false }
              ].map((step, idx) => (
                <div key={idx} style={{ textAlign: 'center', flex: 1 }}>
                  <div
                    style={{
                      height: '4px',
                      borderRadius: '2px',
                      backgroundColor: step.done
                        ? 'var(--accent-emerald)'
                        : step.current
                        ? 'var(--accent-cyan)'
                        : 'var(--border-subtle)',
                      marginBottom: '6px'
                    }}
                  />
                  <span
                    style={{
                      fontSize: '11px',
                      fontWeight: step.current ? 700 : 500,
                      color: step.current
                        ? 'var(--accent-cyan)'
                        : step.done
                        ? 'var(--text-primary)'
                        : 'var(--text-muted)'
                    }}
                  >
                    {step.label}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* RIGHT COLUMN: CAREER SIGNAL + CYBER TREASURE */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          {/* Career Signal Card */}
          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <Target size={20} color="var(--accent-emerald)" />
                <h3 style={{ fontSize: '16px', fontWeight: 700 }}>
                  Career Signal // {careerSignal.targetRole}
                </h3>
              </div>
              <button
                onClick={onViewPassport}
                style={{
                  fontSize: '12px',
                  color: 'var(--accent-cyan)',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px',
                  fontWeight: 600
                }}
              >
                <span>View Passport</span>
                <ChevronRight size={14} />
              </button>
            </div>

            <div style={{ marginBottom: '16px' }}>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '4px' }}>
                CRITICAL CAPABILITY GAPS:
              </div>
              <ul style={{ paddingLeft: '18px', fontSize: '13px', color: 'var(--text-secondary)' }}>
                {careerSignal.criticalGaps.map((gap, i) => (
                  <li key={i} style={{ marginBottom: '4px' }}>{gap}</li>
                ))}
              </ul>
            </div>

            <div>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginBottom: '4px' }}>
                NEXT UNLOCKS:
              </div>
              <ul style={{ paddingLeft: '18px', fontSize: '13px', color: 'var(--accent-emerald)' }}>
                {careerSignal.nextMilestones.map((ms, i) => (
                  <li key={i} style={{ marginBottom: '4px' }}>{ms}</li>
                ))}
              </ul>
            </div>
          </div>

          {/* Cyber Treasure (Verified Capital) */}
          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <Award size={20} color="var(--accent-amber)" />
                <h3 style={{ fontSize: '16px', fontWeight: 700 }}>
                  Cyber Treasure // Cryptographic Proofs
                </h3>
              </div>
              <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                TAMPER-RESISTANT
              </span>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {cyberTreasure.map((item, i) => (
                <div
                  key={i}
                  style={{
                    padding: '12px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--bg-tertiary)',
                    border: '1px solid var(--border-subtle)'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontWeight: 600, fontSize: '13px' }}>{item.title}</span>
                    <span
                      style={{
                        fontSize: '12px',
                        fontFamily: 'var(--font-mono)',
                        color: 'var(--accent-emerald)',
                        fontWeight: 700
                      }}
                    >
                      {item.demonstratedScore}/100
                    </span>
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)', marginTop: '2px' }}>
                    {item.category} • Verified: {item.unlockedAt}
                  </div>
                  <div
                    style={{
                      marginTop: '6px',
                      fontSize: '10px',
                      fontFamily: 'var(--font-mono)',
                      color: 'var(--text-muted)',
                      wordBreak: 'break-all',
                      backgroundColor: 'var(--bg-primary)',
                      padding: '4px 6px',
                      borderRadius: '2px'
                    }}
                  >
                    {item.evidenceHash}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
