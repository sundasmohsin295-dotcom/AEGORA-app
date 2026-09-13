import React, { useState } from 'react';
import {
  ArrowRight,
  Shield,
  Play,
  CheckCircle2,
  AlertTriangle,
  Sparkles,
  Award,
  ChevronRight,
  TrendingUp,
  FileCheck2,
  Clock
} from 'lucide-react';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';
import { AppView } from './NavigationSidebar';

interface CommandCenterViewProps {
  authState: AuthState;
  onLaunchMission: (missionId: string) => void;
  onViewPassport: () => void;
  onOpenSecurityModal: () => void;
  onNavigateSection?: (section: AppView) => void;
}

export const CommandCenterView: React.FC<CommandCenterViewProps> = ({
  authState,
  onLaunchMission,
  onViewPassport,
  onOpenSecurityModal,
  onNavigateSection
}) => {
  const [activeTab, setActiveTab] = useState<'all' | 'verified' | 'remediated'>('all');
  const [showNextMoveWhy, setShowNextMoveWhy] = useState(false);

  const rawId = authState.identity
    ? authState.identity.canonicalLearnerId
    : 'Sundas';
  const displayName = rawId.includes('@')
    ? rawId.split('@')[0]
    : rawId.startsWith('op_')
    ? 'Sundas'
    : rawId;

  // 4–5 Compact Metrics
  const CAPABILITY_METRICS = [
    { label: 'Investigation', value: 82, color: 'var(--color-investigate)' },
    { label: 'Evidence Reasoning', value: 76, color: 'var(--color-practice)' },
    { label: 'AI Judgment', value: 71, color: 'var(--color-ai)' },
    { label: 'Decision Quality', value: 74, color: 'var(--color-home)' },
    { label: 'Readiness', value: 68, color: 'var(--color-proof)' }
  ];

  // 2–4 Important Active Work items
  const ACTIVE_WORK = [
    {
      id: 'work_mission',
      tag: 'ACTIVE MISSION',
      tagColor: 'var(--color-investigate)',
      title: 'Suspicious Login: Host vs. Network Telemetry',
      subtitle: 'Corroborate Sysmon ID 3 with Suricata external alert',
      actionLabel: 'Resume',
      onClick: () => onLaunchMission('mission_suspicious_login_reality')
    },
    {
      id: 'work_adversary',
      tag: 'ADAPTIVE CHALLENGE',
      tagColor: 'var(--color-error)',
      title: 'Detect Premature Conclusion in Auth Logs',
      subtitle: 'Catch flawed AI attributing external compromise prematurely',
      actionLabel: 'Challenge',
      onClick: () => onNavigateSection?.('adversary')
    },
    {
      id: 'work_proof',
      tag: 'PENDING PROOF',
      tagColor: 'var(--color-proof)',
      title: 'Authentication Triage Cryptographic Digest',
      subtitle: 'Ready for cryptographic signing into verified dossier',
      actionLabel: 'Sign & View',
      onClick: () => onNavigateSection?.('verified_proof')
    },
    {
      id: 'work_skill',
      tag: 'RECOMMENDED SKILL',
      tagColor: 'var(--color-learn)',
      title: 'Active Directory Kerberos Ticket Anomalies',
      subtitle: 'Event ID 4624 Type 3 & anomalous ticket requests',
      actionLabel: 'Explore',
      onClick: () => onNavigateSection?.('learn')
    }
  ];

  // Recent Activity Timeline
  const RECENT_ACTIVITY = [
    {
      id: 'act_1',
      title: 'Evidence Verified',
      detail: 'Sysmon Event ID 3 correlation confirmed against IP 198.51.100.24',
      status: 'VERIFIED',
      statusColor: 'var(--color-success)',
      time: '2 hours ago'
    },
    {
      id: 'act_2',
      title: 'AI Failure Detected',
      detail: 'Caught AI analyst Premature Conclusion asserting external breach without payload confirmation',
      status: 'DEFENDED',
      statusColor: 'var(--color-ai)',
      time: '5 hours ago'
    },
    {
      id: 'act_3',
      title: 'Capability Improvement Verified',
      detail: '+6% Decision Quality demonstrated in Active Defense cluster',
      status: '+6% GAIN',
      statusColor: 'var(--color-home)',
      time: 'Yesterday'
    }
  ];

  return (
    <div style={{ maxWidth: '980px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      {/* 1. WELCOME / STATUS — Small, Compact */}
      <section
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '12px',
          paddingBottom: '20px',
          borderBottom: '1px solid var(--border-subtle)',
          marginBottom: '24px'
        }}
      >
        <div>
          <div
            style={{
              fontSize: '11px',
              fontWeight: 700,
              letterSpacing: '0.08em',
              textTransform: 'uppercase',
              color: 'var(--text-muted)'
            }}
          >
            GOOD EVENING, {displayName.toUpperCase()}
          </div>
          <div
            style={{
              fontSize: '15px',
              fontWeight: 600,
              color: 'var(--text-primary)',
              marginTop: '2px'
            }}
          >
            Your next capability target is ready.
          </div>
        </div>

        <button
          onClick={() => onLaunchMission('mission_suspicious_login_reality')}
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px',
            padding: '8px 16px',
            backgroundColor: 'var(--bg-tertiary)',
            border: '1px solid var(--border-subtle)',
            borderRadius: 'var(--radius-md)',
            fontSize: '13px',
            fontWeight: 600,
            color: 'var(--text-primary)'
          }}
        >
          <span>Continue Mission</span>
          <ArrowRight size={14} color="var(--color-home)" />
        </button>
      </section>

      {/* 2. NEXT MOVE — ONE DOMINANT ACTIONABLE RECOMMENDATION */}
      <section
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          borderRadius: 'var(--radius-lg)',
          padding: '24px 28px',
          boxShadow: 'var(--shadow-sm)',
          marginBottom: '28px',
          position: 'relative',
          overflow: 'hidden'
        }}
      >
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            marginBottom: '10px'
          }}
        >
          <div
            style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              backgroundColor: 'var(--color-home)'
            }}
          />
          <span
            style={{
              fontSize: '11px',
              fontWeight: 800,
              letterSpacing: '0.08em',
              textTransform: 'uppercase',
              color: 'var(--color-home)'
            }}
          >
            NEXT MOVE
          </span>
        </div>

        <div
          style={{
            display: 'flex',
            alignItems: 'flex-start',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '20px'
          }}
        >
          <div style={{ flex: '1 1 480px' }}>
            <h2
              style={{
                fontSize: '18px',
                fontWeight: 700,
                color: 'var(--text-primary)',
                marginBottom: '6px'
              }}
            >
              Evidence Validation: Detect Unsupported AI Claims
            </h2>

            {/* Innovation C: Collapsible Contextual "Why This?" Toggle */}
            <div>
              <button
                onClick={() => setShowNextMoveWhy(!showNextMoveWhy)}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '4px',
                  fontSize: '12px',
                  fontWeight: 600,
                  color: 'var(--color-home)',
                  padding: '2px 0'
                }}
              >
                <span>{showNextMoveWhy ? 'Hide Reason' : 'Why this?'}</span>
                <ChevronRight
                  size={12}
                  style={{
                    transform: showNextMoveWhy ? 'rotate(90deg)' : 'none',
                    transition: 'transform 0.15s ease'
                  }}
                />
              </button>

              {showNextMoveWhy && (
                <div
                  style={{
                    marginTop: '6px',
                    padding: '8px 12px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--bg-primary)',
                    border: '1px solid var(--border-subtle)',
                    fontSize: '12px',
                    color: 'var(--text-secondary)',
                    lineHeight: 1.4
                  }}
                >
                  <strong style={{ color: 'var(--text-primary)' }}>Recommendation logic:</strong> Targets the capability with the highest current improvement value (+14% Decision Quality projected).
                </div>
              )}
            </div>
          </div>

          <button
            onClick={() => onLaunchMission('mission_suspicious_login_reality')}
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '10px',
              padding: '12px 24px',
              backgroundColor: 'var(--color-home)',
              color: '#FFFFFF',
              borderRadius: 'var(--radius-md)',
              fontSize: '14px',
              fontWeight: 700,
              boxShadow: '0 2px 8px rgba(77, 141, 255, 0.28)',
              cursor: 'pointer',
              flexShrink: 0
            }}
          >
            <span>EXECUTE</span>
            <ArrowRight size={16} />
          </button>
        </div>
      </section>

      {/* 3. CAPABILITY SNAPSHOT — 4–5 COMPACT METRICS MAX */}
      <section
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          borderRadius: 'var(--radius-lg)',
          padding: '20px 24px',
          marginBottom: '28px'
        }}
      >
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            marginBottom: '16px'
          }}
        >
          <span
            style={{
              fontSize: '11px',
              fontWeight: 700,
              letterSpacing: '0.06em',
              textTransform: 'uppercase',
              color: 'var(--text-muted)'
            }}
          >
            CAPABILITY SNAPSHOT
          </span>
          <button
            onClick={onViewPassport}
            style={{
              fontSize: '12px',
              fontWeight: 600,
              color: 'var(--color-home)',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '4px'
            }}
          >
            <span>View Skill Passport</span>
            <ChevronRight size={14} />
          </button>
        </div>

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))',
            gap: '16px'
          }}
        >
          {CAPABILITY_METRICS.map(metric => (
            <div
              key={metric.label}
              style={{
                backgroundColor: 'var(--bg-primary)',
                borderRadius: 'var(--radius-md)',
                padding: '12px 14px',
                border: '1px solid var(--border-subtle)'
              }}
            >
              <div
                style={{
                  display: 'flex',
                  alignItems: 'baseline',
                  justifyContent: 'space-between',
                  marginBottom: '8px'
                }}
              >
                <span style={{ fontSize: '12px', color: 'var(--text-secondary)', fontWeight: 500 }}>
                  {metric.label}
                </span>
                <span
                  style={{
                    fontSize: '15px',
                    fontWeight: 700,
                    color: 'var(--text-primary)',
                    fontFamily: 'var(--font-mono)'
                  }}
                >
                  {metric.value}%
                </span>
              </div>
              <div
                style={{
                  height: '4px',
                  backgroundColor: 'var(--border-subtle)',
                  borderRadius: '2px',
                  overflow: 'hidden'
                }}
              >
                <div
                  style={{
                    width: `${metric.value}%`,
                    height: '100%',
                    backgroundColor: metric.color,
                    borderRadius: '2px'
                  }}
                />
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 4. ACTIVE WORK — 2–4 IMPORTANT ITEMS ONLY */}
      <section style={{ marginBottom: '28px' }}>
        <div
          style={{
            fontSize: '11px',
            fontWeight: 700,
            letterSpacing: '0.06em',
            textTransform: 'uppercase',
            color: 'var(--text-muted)',
            marginBottom: '12px'
          }}
        >
          ACTIVE WORK
        </div>

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
            gap: '14px'
          }}
        >
          {ACTIVE_WORK.map(work => (
            <div
              key={work.id}
              onClick={work.onClick}
              style={{
                backgroundColor: 'var(--bg-secondary)',
                border: '1px solid var(--border-subtle)',
                borderRadius: 'var(--radius-md)',
                padding: '16px 18px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                gap: '12px',
                cursor: 'pointer',
                transition: 'all 0.15s ease'
              }}
            >
              <div style={{ minWidth: 0 }}>
                <span
                  style={{
                    fontSize: '10px',
                    fontWeight: 700,
                    letterSpacing: '0.04em',
                    color: work.tagColor,
                    marginBottom: '4px',
                    display: 'inline-block'
                  }}
                >
                  {work.tag}
                </span>
                <div
                  style={{
                    fontSize: '13px',
                    fontWeight: 600,
                    color: 'var(--text-primary)',
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis'
                  }}
                >
                  {work.title}
                </div>
                <div
                  style={{
                    fontSize: '11px',
                    color: 'var(--text-muted)',
                    marginTop: '2px',
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis'
                  }}
                >
                  {work.subtitle}
                </div>
              </div>

              <button
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '4px',
                  padding: '6px 12px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--bg-tertiary)',
                  border: '1px solid var(--border-subtle)',
                  fontSize: '12px',
                  fontWeight: 600,
                  color: 'var(--text-primary)',
                  flexShrink: 0
                }}
              >
                <span>{work.actionLabel}</span>
                <ChevronRight size={12} />
              </button>
            </div>
          ))}
        </div>
      </section>

      {/* 5. RECENT ACTIVITY — COMPACT TIMELINE / LIST (NOT GIANT CARDS) */}
      <section
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          borderRadius: 'var(--radius-lg)',
          padding: '20px 24px'
        }}
      >
        <div
          style={{
            fontSize: '11px',
            fontWeight: 700,
            letterSpacing: '0.06em',
            textTransform: 'uppercase',
            color: 'var(--text-muted)',
            marginBottom: '14px'
          }}
        >
          RECENT ACTIVITY
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {RECENT_ACTIVITY.map(act => (
            <div
              key={act.id}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                flexWrap: 'wrap',
                gap: '8px',
                padding: '10px 12px',
                backgroundColor: 'var(--bg-primary)',
                borderRadius: 'var(--radius-md)',
                border: '1px solid var(--border-subtle)'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px', minWidth: 0 }}>
                <span
                  style={{
                    fontSize: '10px',
                    fontWeight: 700,
                    letterSpacing: '0.04em',
                    padding: '2px 8px',
                    borderRadius: 'var(--radius-full)',
                    backgroundColor: 'var(--bg-secondary)',
                    border: '1px solid var(--border-subtle)',
                    color: act.statusColor,
                    flexShrink: 0
                  }}
                >
                  {act.status}
                </span>
                <div style={{ minWidth: 0 }}>
                  <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>
                    {act.title}
                  </div>
                  <div
                    style={{
                      fontSize: '11px',
                      color: 'var(--text-muted)',
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis'
                    }}
                  >
                    {act.detail}
                  </div>
                </div>
              </div>

              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px',
                  fontSize: '11px',
                  color: 'var(--text-muted)',
                  flexShrink: 0
                }}
              >
                <Clock size={12} />
                <span>{act.time}</span>
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
};
