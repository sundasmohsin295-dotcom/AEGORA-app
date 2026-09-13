import React, { useState } from 'react';
import {
  BookOpen,
  Terminal,
  Search,
  Zap,
  Activity,
  Sparkles,
  Flame,
  Award,
  FileCheck2,
  Wrench,
  Settings,
  ArrowRight,
  CheckCircle2,
  AlertTriangle,
  Clock,
  ExternalLink,
  Copy,
  Check,
  ChevronRight,
  Shield,
  Layers,
  Cpu,
  Globe,
  Lock,
  Compass,
  Download,
  Share2,
  RefreshCw,
  Eye
} from 'lucide-react';
import { AppView } from './NavigationSidebar';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';
import { SubscriptionTier } from '../core/types/subscription';

interface ModuleDashboardProps {
  view: AppView;
  onNavigate: (view: AppView) => void;
  onLaunchMission: (missionId: string) => void;
  authState: AuthState;
  currentTier: SubscriptionTier;
  onOpenPlanModal: () => void;
}

/* =========================================================================
   1. LEARN DASHBOARD
   ========================================================================= */
export const LearnDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate' | 'onLaunchMission'>> = ({
  onNavigate,
  onLaunchMission
}) => {
  const [selectedModule, setSelectedModule] = useState<string | null>(null);

  const MODULES = [
    { id: 'm1', title: 'Network Protocol Framing & TCP States', category: 'Networking', progress: 85, duration: '20 min' },
    { id: 'm2', title: 'Sysmon Process & Network Telemetry Triage', category: 'Host Telemetry', progress: 70, duration: '25 min' },
    { id: 'm3', title: 'Windows Security Event ID 4624 & Kerberos', category: 'Active Directory', progress: 40, duration: '30 min' },
    { id: 'm4', title: 'AI Hallucination & Evidence Grounding Defense', category: 'AI Security', progress: 90, duration: '15 min' }
  ];

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>Learn</h1>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          Structured technical curricula grounded in real host and network telemetry.
        </p>
      </div>

      {/* Continue Learning Card */}
      <div
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          borderRadius: 'var(--radius-lg)',
          padding: '24px',
          marginBottom: '28px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '16px'
        }}
      >
        <div>
          <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--color-learn)', textTransform: 'uppercase' }}>
            CONTINUE LEARNING
          </span>
          <h3 style={{ fontSize: '17px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '4px' }}>
            Sysmon Event ID 3: Network Connection Corroboration
          </h3>
          <p style={{ fontSize: '13px', color: 'var(--text-muted)', marginTop: '2px' }}>
            Module 4 of 6 • Host Telemetry Deep Dive
          </p>
        </div>

        <button
          onClick={() => onLaunchMission('mission_suspicious_login_reality')}
          style={{
            padding: '10px 20px',
            backgroundColor: 'var(--color-learn)',
            color: '#FFFFFF',
            borderRadius: 'var(--radius-md)',
            fontSize: '13px',
            fontWeight: 700,
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}
        >
          <span>Resume Module</span>
          <ArrowRight size={14} />
        </button>
      </div>

      {/* Modules List */}
      <div style={{ fontSize: '11px', fontWeight: 700, letterSpacing: '0.06em', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '12px' }}>
        TECHNICAL CURRICULUM
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {MODULES.map(m => (
          <div
            key={m.id}
            onClick={() => setSelectedModule(m.id)}
            style={{
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-md)',
              padding: '16px 20px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              cursor: 'pointer'
            }}
          >
            <div>
              <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-learn)' }}>
                {m.category}
              </span>
              <div style={{ fontSize: '14px', fontWeight: 600, color: 'var(--text-primary)', marginTop: '2px' }}>
                {m.title}
              </div>
              <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
                {m.duration} • {m.progress}% Completed
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <div style={{ width: '80px', height: '4px', backgroundColor: 'var(--bg-tertiary)', borderRadius: '2px', overflow: 'hidden' }}>
                <div style={{ width: `${m.progress}%`, height: '100%', backgroundColor: 'var(--color-learn)' }} />
              </div>
              <ChevronRight size={16} color="var(--text-muted)" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

/* =========================================================================
   2. ROADMAP DASHBOARD
   ========================================================================= */
export const RoadmapDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate'>> = () => {
  const PHASES = [
    { phase: '01', title: 'Foundations & System Internals', status: 'COMPLETED', progress: 100, details: 'Networking, Linux CLI, Windows Architecture, Basic Cryptography' },
    { phase: '02', title: 'Telemetry & Host Visibility', status: 'IN PROGRESS', progress: 75, details: 'Sysmon configuration, Windows Security Logs, Packet Captures, Sigma Rules' },
    { phase: '03', title: 'SOC Triage & Threat Intelligence', status: 'UP NEXT', progress: 20, details: 'Incident containment, False-positive reduction, C2 beacon detection' },
    { phase: '04', title: 'AI-Augmented Cognitive Defense', status: 'LOCKED', progress: 0, details: 'Catching AI hallucinations, Premature conclusions, Adversarial Red Team' },
    { phase: '05', title: 'Employer Verified Tier-2 Ready', status: 'LOCKED', progress: 0, details: 'Cryptographic proof dossier, verified live mission demonstrations' }
  ];

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>Career Roadmap</h1>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          Authoritative skill progression from Zero to Verified Tier 2 SOC Analyst.
        </p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
        {PHASES.map(p => (
          <div
            key={p.phase}
            style={{
              backgroundColor: 'var(--bg-secondary)',
              border: p.status === 'IN PROGRESS' ? '1px solid var(--color-roadmap)' : '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-lg)',
              padding: '20px 24px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              gap: '16px',
              flexWrap: 'wrap'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div
                style={{
                  width: '40px',
                  height: '40px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: p.status === 'COMPLETED' ? 'rgba(16, 185, 129, 0.12)' : 'var(--bg-tertiary)',
                  color: p.status === 'COMPLETED' ? 'var(--color-success)' : 'var(--text-primary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '15px',
                  fontWeight: 800,
                  fontFamily: 'var(--font-mono)'
                }}
              >
                {p.phase}
              </div>

              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <h3 style={{ fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)' }}>
                    {p.title}
                  </h3>
                  <span
                    style={{
                      fontSize: '9px',
                      fontWeight: 700,
                      padding: '1px 6px',
                      borderRadius: 'var(--radius-full)',
                      backgroundColor:
                        p.status === 'COMPLETED'
                          ? 'rgba(16, 185, 129, 0.12)'
                          : p.status === 'IN PROGRESS'
                          ? 'rgba(245, 158, 11, 0.12)'
                          : 'var(--bg-primary)',
                      color:
                        p.status === 'COMPLETED'
                          ? 'var(--color-success)'
                          : p.status === 'IN PROGRESS'
                          ? 'var(--color-roadmap)'
                          : 'var(--text-muted)'
                    }}
                  >
                    {p.status}
                  </span>
                </div>
                <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
                  {p.details}
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <span style={{ fontSize: '13px', fontWeight: 700, fontFamily: 'var(--font-mono)', color: 'var(--text-secondary)' }}>
                {p.progress}%
              </span>
              <div style={{ width: '80px', height: '4px', backgroundColor: 'var(--bg-tertiary)', borderRadius: '2px', overflow: 'hidden' }}>
                <div style={{ width: `${p.progress}%`, height: '100%', backgroundColor: 'var(--color-roadmap)' }} />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

/* =========================================================================
   3. PRACTICE DASHBOARD
   ========================================================================= */
export const PracticeDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate' | 'onLaunchMission'>> = ({
  onLaunchMission
}) => {
  const CHALLENGES = [
    { id: 'c1', title: 'Suspicious External Auth Anomaly', difficulty: 'INTERMEDIATE', xp: '+350 XP', time: '12m', target: 'Sysmon / Suricata' },
    { id: 'c2', title: 'Encoded PowerShell Stager Extraction', difficulty: 'ADVANCED', xp: '+450 XP', time: '18m', target: 'Memory / Script Block' },
    { id: 'c3', title: 'Cloud IAM Privilege Escalation Triage', difficulty: 'HARD', xp: '+600 XP', time: '25m', target: 'CloudTrail Audit' }
  ];

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>Practice Range</h1>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          Hands-on simulation ranges designed to test operational decision making.
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '16px' }}>
        {CHALLENGES.map(c => (
          <div
            key={c.id}
            style={{
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-lg)',
              padding: '20px',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between'
            }}
          >
            <div>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
                <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-practice)' }}>
                  {c.difficulty}
                </span>
                <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--color-proof)' }}>
                  {c.xp}
                </span>
              </div>
              <h3 style={{ fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '4px' }}>
                {c.title}
              </h3>
              <p style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
                Target: {c.target} • {c.time}
              </p>
            </div>

            <button
              onClick={() => onLaunchMission('mission_suspicious_login_reality')}
              style={{
                marginTop: '16px',
                padding: '8px 14px',
                backgroundColor: 'var(--bg-tertiary)',
                border: '1px solid var(--border-subtle)',
                borderRadius: 'var(--radius-md)',
                fontSize: '12px',
                fontWeight: 600,
                color: 'var(--text-primary)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                gap: '6px'
              }}
            >
              <span>Launch Drill</span>
              <ArrowRight size={13} />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

/* =========================================================================
   4. INVESTIGATE DASHBOARD
   ========================================================================= */
export const InvestigateDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate' | 'onLaunchMission'>> = ({
  onLaunchMission
}) => {
  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>Investigate</h1>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          Active security incidents requiring host and network telemetry corroboration.
        </p>
      </div>

      {/* Dominant Active Investigation */}
      <div
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--color-investigate)',
          borderRadius: 'var(--radius-lg)',
          padding: '24px',
          marginBottom: '24px'
        }}
      >
        <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--color-investigate)', textTransform: 'uppercase' }}>
          FLAGSHIP INCIDENT
        </span>
        <h2 style={{ fontSize: '18px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '4px', marginBottom: '6px' }}>
          Suspicious Authentication: External Compromise Assertion
        </h2>
        <p style={{ fontSize: '13px', color: 'var(--text-secondary)', lineHeight: 1.5, marginBottom: '16px' }}>
          An automated AI copilot reported that user Sundas's account was compromised from external IP 198.51.100.24. Examine host Sysmon telemetry and network socket logs to corroborate or challenge this assertion.
        </p>
        <button
          onClick={() => onLaunchMission('mission_suspicious_login_reality')}
          style={{
            padding: '10px 20px',
            backgroundColor: 'var(--color-investigate)',
            color: '#FFFFFF',
            borderRadius: 'var(--radius-md)',
            fontSize: '13px',
            fontWeight: 700,
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px'
          }}
        >
          <span>Start Investigation</span>
          <ArrowRight size={14} />
        </button>
      </div>

      <div style={{ fontSize: '11px', fontWeight: 700, letterSpacing: '0.06em', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '12px' }}>
        RECOMMENDED INVESTIGATIONS
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <div style={{ padding: '14px 18px', backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div>
            <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>Base64 Encoded PowerShell Stager</div>
            <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Parent process tree analysis • Sysmon Event ID 1</div>
          </div>
          <button onClick={() => onLaunchMission('mission_suspicious_login_reality')} style={{ padding: '6px 12px', fontSize: '11px', fontWeight: 600, borderRadius: 'var(--radius-sm)', backgroundColor: 'var(--bg-tertiary)', border: '1px solid var(--border-subtle)' }}>Triage</button>
        </div>

        <div style={{ padding: '14px 18px', backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div>
            <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>Multi-Geo Impossible Travel Anomaly</div>
            <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Cloud Identity Okta System Log Analysis</div>
          </div>
          <button onClick={() => onLaunchMission('mission_suspicious_login_reality')} style={{ padding: '6px 12px', fontSize: '11px', fontWeight: 600, borderRadius: 'var(--radius-sm)', backgroundColor: 'var(--bg-tertiary)', border: '1px solid var(--border-subtle)' }}>Triage</button>
        </div>
      </div>
    </div>
  );
};

/* =========================================================================
   5. CYBER REALITY WORKSPACE (Section 16)
   ========================================================================= */
export const CyberRealityDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate' | 'onLaunchMission'>> = ({
  onLaunchMission
}) => {
  const [decision, setDecision] = useState<'accept' | 'challenge' | null>(null);
  const [verified, setVerified] = useState(false);

  return (
    <div style={{ maxWidth: '980px', margin: '0 auto', padding: '24px 20px 64px 20px' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '14px' }}>
        <div>
          <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-investigate)', letterSpacing: '0.06em' }}>
            CYBER REALITY WORKSPACE // TACTICAL TRIAGE
          </span>
          <h1 style={{ fontSize: '18px', fontWeight: 800, color: 'var(--text-primary)', marginTop: '2px' }}>
            Flagship Mission: Suspicious Authentication Investigation
          </h1>
        </div>
        <button
          onClick={() => onLaunchMission('mission_suspicious_login_reality')}
          style={{
            padding: '6px 14px',
            backgroundColor: 'var(--color-home)',
            color: '#FFFFFF',
            fontSize: '12px',
            fontWeight: 700,
            borderRadius: 'var(--radius-md)'
          }}
        >
          Full Mission View
        </button>
      </div>

      {/* 3-Pane Layout */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '16px', marginBottom: '20px' }}>
        {/* Left: Telemetry */}
        <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)', padding: '16px' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', marginBottom: '10px', textTransform: 'uppercase' }}>
            HOST / NETWORK TELEMETRY
          </div>
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: '11px', color: 'var(--text-secondary)', display: 'flex', flexDirection: 'column', gap: '8px' }}>
            <div style={{ padding: '8px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-sm)' }}>
              <strong>Sysmon ID 3:</strong> Network socket opened by svchost.exe to 198.51.100.24:443. Duration: 180ms.
            </div>
            <div style={{ padding: '8px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-sm)' }}>
              <strong>Suricata Alert:</strong> ET POLICY Outbound SSL to external VPS IP. Zero payload anomalies detected.
            </div>
            <div style={{ padding: '8px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-sm)' }}>
              <strong>Event ID 4624:</strong> Logon Type 3 (Network). User Sundas authenticated via authorized corporate VPN gateway.
            </div>
          </div>
        </div>

        {/* Center: AI Analyst Claim */}
        <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)', padding: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '10px' }}>
            <Sparkles size={14} color="var(--color-ai)" />
            <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--color-ai)', textTransform: 'uppercase' }}>
              AI COPILOT CLAIM
            </span>
          </div>
          <div style={{ fontSize: '13px', color: 'var(--text-primary)', lineHeight: 1.5, marginBottom: '12px' }}>
            "High confidence assertion: Account Sundas was compromised by external IP 198.51.100.24. Immediately isolate workstation."
          </div>
          <div style={{ fontSize: '11px', color: 'var(--text-muted)', padding: '6px 8px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-sm)' }}>
            Confidence: 94% • Risk of Premature Attribution: HIGH
          </div>
        </div>

        {/* Right: Human Decision */}
        <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)', padding: '16px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
          <div>
            <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', marginBottom: '10px', textTransform: 'uppercase' }}>
              OPERATOR DECISION
            </div>
            <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '14px' }}>
              Do you accept the AI attribution or challenge it based on missing payload evidence?
            </p>
          </div>

          <div style={{ display: 'flex', gap: '8px' }}>
            <button
              onClick={() => { setDecision('accept'); setVerified(true); }}
              style={{
                flex: 1,
                padding: '8px',
                fontSize: '12px',
                fontWeight: 700,
                borderRadius: 'var(--radius-sm)',
                backgroundColor: decision === 'accept' ? 'var(--color-investigate)' : 'var(--bg-tertiary)',
                color: decision === 'accept' ? '#FFFFFF' : 'var(--text-primary)',
                border: '1px solid var(--border-subtle)'
              }}
            >
              ACCEPT AI
            </button>
            <button
              onClick={() => { setDecision('challenge'); setVerified(true); }}
              style={{
                flex: 1,
                padding: '8px',
                fontSize: '12px',
                fontWeight: 700,
                borderRadius: 'var(--radius-sm)',
                backgroundColor: decision === 'challenge' ? 'var(--color-success)' : 'var(--bg-tertiary)',
                color: decision === 'challenge' ? '#FFFFFF' : 'var(--text-primary)',
                border: '1px solid var(--border-subtle)'
              }}
            >
              CHALLENGE AI
            </button>
          </div>
        </div>
      </div>

      {/* Bottom: Verification Result */}
      {verified && (
        <div
          style={{
            padding: '16px 20px',
            borderRadius: 'var(--radius-md)',
            backgroundColor: decision === 'challenge' ? 'rgba(16, 185, 129, 0.10)' : 'rgba(240, 68, 85, 0.10)',
            border: decision === 'challenge' ? '1px solid var(--color-success)' : '1px solid var(--color-investigate)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: '12px'
          }}
        >
          <div>
            <div style={{ fontSize: '13px', fontWeight: 700, color: decision === 'challenge' ? 'var(--color-success)' : 'var(--color-investigate)' }}>
              {decision === 'challenge' ? 'AI FAILURE DETECTED ✓ — VERIFIED' : 'UNCORROBORATED ACCEPTANCE ⚠️'}
            </div>
            <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>
              {decision === 'challenge'
                ? 'Correct operator judgment: AI committed Premature Conclusion without verifying malicious process execution.'
                : 'Warning: Isolating host without payload validation causes operational denial of service.'}
            </div>
          </div>

          <button
            onClick={() => onLaunchMission('mission_suspicious_login_reality')}
            style={{
              padding: '6px 14px',
              fontSize: '12px',
              fontWeight: 700,
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-sm)',
              color: 'var(--text-primary)'
            }}
          >
            Launch Full Interactive Lab
          </button>
        </div>
      )}
    </div>
  );
};

/* =========================================================================
   6. ADAPTIVE ADVERSARY DASHBOARD (Section 17 & 18)
   ========================================================================= */
export const AdaptiveAdversaryDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate' | 'onLaunchMission'>> = ({
  onLaunchMission
}) => {
  const [userChallenged, setUserChallenged] = useState<boolean | null>(null);
  const [showAutopsy, setShowAutopsy] = useState(false);

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
          <Flame size={20} color="var(--color-error)" />
          <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>
            Adaptive Adversary
          </h1>
        </div>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          "Can you catch an AI that is trying to fool you?" Red team tests of reasoning integrity.
        </p>
      </div>

      {/* Adversarial Scenario Card */}
      <div
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          borderRadius: 'var(--radius-lg)',
          padding: '24px',
          marginBottom: '24px'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '12px' }}>
          <span style={{ fontSize: '10px', fontWeight: 800, padding: '2px 8px', borderRadius: 'var(--radius-full)', backgroundColor: 'rgba(240, 68, 85, 0.12)', color: 'var(--color-error)' }}>
            REASONING ATTACK: PREMATURE CONCLUSION
          </span>
          <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>Case #AD-841</span>
        </div>

        <div style={{ padding: '14px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', marginBottom: '16px' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', marginBottom: '4px' }}>
            AI COPILOT ASSERTION (CONFIDENCE: 98%)
          </div>
          <div style={{ fontSize: '14px', color: 'var(--text-primary)', fontWeight: 600 }}>
            "This account was compromised by the external IP 198.51.100.24. Quarantine user immediately."
          </div>
        </div>

        <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '16px' }}>
          <strong>Evidence Telemetry:</strong> Sysmon Event ID 3 shows brief network connection to authorized cloud CDN endpoint. No executable stager or command line invocation observed in Event ID 1.
        </div>

        {userChallenged === null ? (
          <div style={{ display: 'flex', gap: '12px' }}>
            <button
              onClick={() => setUserChallenged(false)}
              style={{
                padding: '10px 20px',
                backgroundColor: 'var(--bg-tertiary)',
                border: '1px solid var(--border-subtle)',
                borderRadius: 'var(--radius-md)',
                fontSize: '13px',
                fontWeight: 700,
                color: 'var(--text-primary)'
              }}
            >
              ACCEPT AI CLAIM
            </button>
            <button
              onClick={() => setUserChallenged(true)}
              style={{
                padding: '10px 20px',
                backgroundColor: 'var(--color-error)',
                color: '#FFFFFF',
                borderRadius: 'var(--radius-md)',
                fontSize: '13px',
                fontWeight: 700
              }}
            >
              CHALLENGE AI
            </button>
          </div>
        ) : (
          <div>
            <div
              style={{
                padding: '14px 18px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: userChallenged ? 'rgba(16, 185, 129, 0.12)' : 'rgba(240, 68, 85, 0.12)',
                border: userChallenged ? '1px solid var(--color-success)' : '1px solid var(--color-error)',
                marginBottom: '16px'
              }}
            >
              <div style={{ fontSize: '14px', fontWeight: 700, color: userChallenged ? 'var(--color-success)' : 'var(--color-error)' }}>
                {userChallenged ? 'AI FAILURE DETECTED ✓ — OPERATOR SUCCESS' : 'FAILED TO DETECT AI REASONING TRAP ✗'}
              </div>
              <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                Failure Pattern: <strong>Premature Conclusion & Confirmation Trap</strong>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '10px' }}>
              <button
                onClick={() => setShowAutopsy(!showAutopsy)}
                style={{
                  padding: '8px 16px',
                  backgroundColor: 'var(--bg-tertiary)',
                  border: '1px solid var(--border-subtle)',
                  borderRadius: 'var(--radius-md)',
                  fontSize: '12px',
                  fontWeight: 600,
                  color: 'var(--text-primary)'
                }}
              >
                {showAutopsy ? 'Hide Autopsy' : 'View Failure Autopsy'}
              </button>
              <button
                onClick={() => { setUserChallenged(null); setShowAutopsy(false); }}
                style={{
                  padding: '8px 16px',
                  backgroundColor: 'var(--color-home)',
                  color: '#FFFFFF',
                  borderRadius: 'var(--radius-md)',
                  fontSize: '12px',
                  fontWeight: 600
                }}
              >
                Next Challenge
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Failure Autopsy Drawer (Section 18) */}
      {showAutopsy && (
        <div
          style={{
            backgroundColor: 'var(--bg-secondary)',
            border: '1px solid var(--border-subtle)',
            borderRadius: 'var(--radius-lg)',
            padding: '24px'
          }}
        >
          <h3 style={{ fontSize: '16px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px' }}>
            Failure Autopsy: Premature Attribution
          </h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', fontSize: '13px' }}>
            <div>
              <strong style={{ color: 'var(--text-primary)' }}>What happened:</strong>
              <p style={{ color: 'var(--text-secondary)', marginTop: '2px' }}>
                The AI analyst attributed an account compromise based purely on an IP reputation match without verifying persistence or code execution.
              </p>
            </div>

            <div>
              <strong style={{ color: 'var(--text-primary)' }}>Why it failed:</strong>
              <ul style={{ color: 'var(--text-secondary)', paddingLeft: '18px', marginTop: '2px' }}>
                <li>Overweighted network alert while ignoring absence of host process telemetry</li>
                <li>Premature conclusion triggered by anchoring on automated vendor threshold</li>
              </ul>
            </div>

            <div>
              <strong style={{ color: 'var(--text-primary)' }}>Missed signal:</strong>
              <p style={{ color: 'var(--text-secondary)', marginTop: '2px' }}>
                Sysmon Event ID 1 showed no process spawned by svchost.exe. The connection was benign corporate CDN traffic.
              </p>
            </div>

            <div>
              <strong style={{ color: 'var(--text-primary)' }}>Correction:</strong>
              <p style={{ color: 'var(--text-secondary)', marginTop: '2px' }}>
                Always require corroboration across host process creation (ID 1) before declaring account compromise.
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

/* =========================================================================
   7. INTELLIGENCE DASHBOARD
   ========================================================================= */
export const IntelligenceDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate'>> = ({ onNavigate }) => {
  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>Intelligence Cockpit</h1>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          Authoritative telemetry of your cognitive decision-making and AI judgment trends.
        </p>
      </div>

      {/* Top 3 Stat Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '16px', marginBottom: '24px' }}>
        <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-lg)', padding: '20px' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>COGNITIVE TWIN</div>
          <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--color-intel)', marginTop: '4px' }}>Tier-2 Ready</div>
          <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>Demonstrated in 12 live scenarios</div>
        </div>

        <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-lg)', padding: '20px' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>AI DEFENSE ACCURACY</div>
          <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--color-success)', marginTop: '4px' }}>92.4%</div>
          <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>Caught 11 of 12 reasoning traps</div>
        </div>

        <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-lg)', padding: '20px' }}>
          <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase' }}>PRIMARY LIMITING GATE</div>
          <div style={{ fontSize: '16px', fontWeight: 700, color: 'var(--color-warning)', marginTop: '6px' }}>Memory Artifacts</div>
          <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>Target: Volatility 3 malfind</div>
        </div>
      </div>

      {/* Innovation E: Compact Failure Pattern Trends */}
      <div
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          borderRadius: 'var(--radius-lg)',
          padding: '24px',
          marginBottom: '24px'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
          <div>
            <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--text-muted)', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
              ADAPTIVE ADVERSARY REASONING TRENDS (LAST 5 MISSIONS)
            </div>
            <div style={{ fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '2px' }}>
              Cognitive Failure Pattern Reduction
            </div>
          </div>
          <span style={{ fontSize: '11px', fontWeight: 700, color: 'var(--color-success)', backgroundColor: 'rgba(16, 185, 129, 0.12)', padding: '3px 8px', borderRadius: 'var(--radius-full)' }}>
            +28% RESILIENCE
          </span>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {/* Pattern 1 */}
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '12px 16px',
              backgroundColor: 'var(--bg-primary)',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div>
              <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>
                Premature Conclusion Trap
              </div>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                Accepting automated IP attribution without process validation
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: '13px', fontWeight: 700, color: 'var(--color-success)' }}>
                3 → 2 → 1 <span style={{ fontSize: '11px', color: 'var(--color-success)' }}>(-67% ↓)</span>
              </div>
              <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-success)', border: '1px solid rgba(16, 185, 129, 0.3)', padding: '2px 6px', borderRadius: 'var(--radius-sm)' }}>
                IMPROVING
              </span>
            </div>
          </div>

          {/* Pattern 2 */}
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '12px 16px',
              backgroundColor: 'var(--bg-primary)',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div>
              <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>
                Confirmation Bias Trap
              </div>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                Selectively filtering alert telemetry to fit initial hypothesis
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: '13px', fontWeight: 700, color: 'var(--color-success)' }}>
                2 → 1 → 0 <span style={{ fontSize: '11px', color: 'var(--color-success)' }}>(RESOLVED ✓)</span>
              </div>
              <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-home)', border: '1px solid rgba(77, 141, 255, 0.3)', padding: '2px 6px', borderRadius: 'var(--radius-sm)' }}>
                STABILIZED
              </span>
            </div>
          </div>

          {/* Pattern 3 */}
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '12px 16px',
              backgroundColor: 'var(--bg-primary)',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div>
              <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>
                Evidence Overweighting
              </div>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                Treating low-severity Suricata alerts as definitive breach evidence
              </div>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: '13px', fontWeight: 700, color: 'var(--color-success)' }}>
                2 → 0 <span style={{ fontSize: '11px', color: 'var(--color-success)' }}>(RESOLVED ✓)</span>
              </div>
              <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-home)', border: '1px solid rgba(77, 141, 255, 0.3)', padding: '2px 6px', borderRadius: 'var(--radius-sm)' }}>
                STABILIZED
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Innovation F: Context-Aware Next Actions */}
      <div
        style={{
          padding: '20px',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          borderRadius: 'var(--radius-lg)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '14px'
        }}
      >
        <div>
          <div style={{ fontSize: '11px', fontWeight: 700, color: 'var(--color-intel)', letterSpacing: '0.08em', textTransform: 'uppercase' }}>
            CONTEXT-AWARE ACTION RECOMMENDED
          </div>
          <div style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '2px' }}>
            Train Remaining Weakness: Premature Conclusion Autopsy
          </div>
        </div>

        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={() => onNavigate?.('adversary')}
            style={{
              padding: '8px 16px',
              backgroundColor: 'var(--color-error)',
              color: '#FFFFFF',
              borderRadius: 'var(--radius-md)',
              fontSize: '12px',
              fontWeight: 700
            }}
          >
            TRAIN THIS WEAKNESS
          </button>
          <button
            onClick={() => onNavigate?.('verified_proof')}
            style={{
              padding: '8px 16px',
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-md)',
              fontSize: '12px',
              fontWeight: 600,
              color: 'var(--text-primary)'
            }}
          >
            VIEW PROOF
          </button>
        </div>
      </div>
    </div>
  );
};

/* =========================================================================
   8. AI ANALYST DASHBOARD
   ========================================================================= */
export const AiAnalystDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate'>> = () => {
  const [query, setQuery] = useState('');
  const [messages, setMessages] = useState<Array<{ sender: 'user' | 'ai'; text: string }>>([
    {
      sender: 'ai',
      text: 'AEGORA AI Analyst online. Query telemetry signatures, request reasoning breakdowns, or prepare for adversarial drills.'
    }
  ]);

  const handleSend = () => {
    if (!query.trim()) return;
    const userQ = query;
    setMessages(prev => [
      ...prev,
      { sender: 'user', text: userQ },
      {
        sender: 'ai',
        text: `Analysis of "${userQ}": Host evidence must corroborate network alerts. Always check parent process IDs in Sysmon Event ID 1 to verify legitimacy.`
      }
    ]);
    setQuery('');
  };

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '20px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>AI Analyst Co-Pilot</h1>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          Operational reasoning assistant for triage explanation and telemetry validation.
        </p>
      </div>

      <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-lg)', display: 'flex', flexDirection: 'column', height: '420px', overflow: 'hidden' }}>
        <div style={{ flex: 1, overflowY: 'auto', padding: '20px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {messages.map((m, idx) => (
            <div
              key={idx}
              style={{
                alignSelf: m.sender === 'user' ? 'flex-end' : 'flex-start',
                maxWidth: '75%',
                padding: '10px 14px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: m.sender === 'user' ? 'var(--color-home)' : 'var(--bg-primary)',
                color: m.sender === 'user' ? '#FFFFFF' : 'var(--text-primary)',
                fontSize: '13px',
                border: m.sender === 'ai' ? '1px solid var(--border-subtle)' : 'none'
              }}
            >
              {m.text}
            </div>
          ))}
        </div>

        <div style={{ padding: '12px 16px', borderTop: '1px solid var(--border-subtle)', display: 'flex', gap: '10px', backgroundColor: 'var(--bg-primary)' }}>
          <input
            type="text"
            placeholder="Ask about Sysmon Event ID 3, false positive reduction, or stager detection..."
            value={query}
            onChange={e => setQuery(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleSend()}
            style={{ flex: 1, padding: '8px 12px', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', backgroundColor: 'var(--bg-secondary)', fontSize: '13px' }}
          />
          <button
            onClick={handleSend}
            style={{ padding: '8px 16px', backgroundColor: 'var(--color-ai)', color: '#FFFFFF', borderRadius: 'var(--radius-md)', fontSize: '13px', fontWeight: 700 }}
          >
            Send
          </button>
        </div>
      </div>
    </div>
  );
};

/* =========================================================================
   9. PROOF DASHBOARD / VERIFIED PROOF (Section 19)
   ========================================================================= */
export const ProofDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate'>> = () => {
  const [proofCopied, setProofCopied] = useState(false);
  const [showProofModal, setShowProofModal] = useState(false);

  const VERIFIED_CAPABILITIES = [
    { title: 'Evidence Verification', status: 'VERIFIED', hash: 'sha256:7f4ae91b' },
    { title: 'AI Failure Detection', status: 'VERIFIED', hash: 'sha256:3c8d19a2' },
    { title: 'Incident Triage', status: 'VERIFIED', hash: 'sha256:e92b841f' },
    { title: 'Uncertainty Handling', status: 'VERIFIED', hash: 'sha256:5a71df04' },
    { title: 'Human-AI Decision Making', status: 'VERIFIED', hash: 'sha256:91c4ea33' }
  ];

  const handleShare = () => {
    setShowProofModal(true);
  };

  const handleCopyLink = () => {
    navigator.clipboard?.writeText(window.location.origin + '?verify=aegora_proof_sundas_2026');
    setProofCopied(true);
    setTimeout(() => setProofCopied(false), 2000);
  };

  const handleExport = () => {
    const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify({
      passportOwner: "operator_sundas",
      verificationAuthority: "AEGORA Multi-Platform Engine",
      missionsVerified: 12,
      cryptographicSignature: "sha256:7f4ae91b4802c6d83a15f0134bc29088",
      timestamp: new Date().toISOString()
    }, null, 2));
    const dlAnchor = document.createElement('a');
    dlAnchor.setAttribute("href", dataStr);
    dlAnchor.setAttribute("download", "aegora_verified_dossier.json");
    dlAnchor.click();
  };

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '14px', marginBottom: '24px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Award size={22} color="var(--color-proof)" />
            <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>Skill Passport</h1>
          </div>
          <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
            Cryptographically signed proof-of-work dossier.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            onClick={handleShare}
            style={{
              padding: '8px 14px',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-md)',
              fontSize: '12px',
              fontWeight: 600,
              color: 'var(--text-primary)',
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
          >
            {proofCopied ? <Check size={14} color="var(--color-success)" /> : <Share2 size={14} />}
            <span>{proofCopied ? 'Link Copied!' : 'Share Proof'}</span>
          </button>

          <button
            onClick={handleExport}
            style={{
              padding: '8px 14px',
              backgroundColor: 'var(--color-proof)',
              color: '#FFFFFF',
              borderRadius: 'var(--radius-md)',
              fontSize: '12px',
              fontWeight: 700,
              display: 'flex',
              alignItems: 'center',
              gap: '6px'
            }}
          >
            <Download size={14} />
            <span>Export Dossier</span>
          </button>
        </div>
      </div>

      {/* Verified Seal Header */}
      <div
        style={{
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          borderRadius: 'var(--radius-lg)',
          padding: '24px',
          marginBottom: '24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          flexWrap: 'wrap',
          gap: '16px'
        }}
      >
        <div>
          <span style={{ fontSize: '10px', fontWeight: 800, letterSpacing: '0.08em', padding: '2px 8px', borderRadius: 'var(--radius-full)', backgroundColor: 'rgba(217, 154, 0, 0.12)', color: 'var(--color-proof)' }}>
            AEGORA VERIFIED
          </span>
          <h2 style={{ fontSize: '18px', fontWeight: 800, color: 'var(--text-primary)', marginTop: '4px' }}>
            AI-Augmented Investigation & Triage Dossier
          </h2>
          <div style={{ fontSize: '12px', color: 'var(--text-muted)', marginTop: '2px' }}>
            Missions Verified: 12 • Cryptographic Hash: sha256:7f4ae91b...
          </div>
        </div>

        <button
          onClick={() => setShowProofModal(true)}
          style={{
            padding: '8px 16px',
            backgroundColor: 'var(--bg-tertiary)',
            border: '1px solid var(--border-subtle)',
            borderRadius: 'var(--radius-md)',
            fontSize: '12px',
            fontWeight: 600,
            color: 'var(--text-primary)',
            display: 'flex',
            alignItems: 'center',
            gap: '6px'
          }}
        >
          <Eye size={14} />
          <span>View Proof Digest</span>
        </button>
      </div>

      {/* Verified Capabilities */}
      <div style={{ fontSize: '11px', fontWeight: 700, letterSpacing: '0.06em', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '12px' }}>
        VERIFIED CORE CAPABILITIES
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {VERIFIED_CAPABILITIES.map(c => (
          <div
            key={c.title}
            style={{
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-md)',
              padding: '14px 18px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <CheckCircle2 size={16} color="var(--color-success)" />
              <div>
                <div style={{ fontSize: '13px', fontWeight: 600, color: 'var(--text-primary)' }}>{c.title}</div>
                <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>{c.hash}</div>
              </div>
            </div>

            <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-success)', padding: '2px 8px', borderRadius: 'var(--radius-full)', backgroundColor: 'rgba(16, 185, 129, 0.10)' }}>
              {c.status}
            </span>
          </div>
        ))}
      </div>

      {/* Innovation D: Real Public Proof Link Preview Modal */}
      {showProofModal && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(17, 24, 39, 0.55)',
            backdropFilter: 'blur(4px)',
            zIndex: 100,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '16px'
          }}
          onClick={() => setShowProofModal(false)}
        >
          <div
            style={{
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-lg)',
              maxWidth: '540px',
              width: '100%',
              padding: '24px',
              boxShadow: 'var(--shadow-lg)'
            }}
            onClick={e => e.stopPropagation()}
          >
            {/* Real Public Preview Header */}
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '14px', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '12px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Shield size={18} color="var(--color-proof)" />
                <span style={{ fontSize: '12px', fontWeight: 800, letterSpacing: '0.06em', color: 'var(--text-primary)' }}>
                  AEGORA VERIFIED // PUBLIC PROOF RECORD
                </span>
              </div>
              <span style={{ fontSize: '10px', fontWeight: 700, padding: '2px 8px', borderRadius: 'var(--radius-full)', backgroundColor: 'rgba(16, 185, 129, 0.12)', color: 'var(--color-success)' }}>
                CRYPTOGRAPHICALLY VERIFIED ✓
              </span>
            </div>

            {/* Subject Info */}
            <div style={{ padding: '12px 14px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)', marginBottom: '14px' }}>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 700 }}>
                VERIFIED OPERATOR
              </div>
              <div style={{ fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)', marginTop: '2px' }}>
                Operator Sundas (SOC Tier 2 Candidate)
              </div>
              <div style={{ fontSize: '12px', color: 'var(--text-secondary)', marginTop: '2px' }}>
                12 Missions Verified • Zero Client-Side Bypass
              </div>
            </div>

            {/* Capabilities */}
            <div style={{ marginBottom: '14px' }}>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 700, marginBottom: '6px' }}>
                AUTHENTICATED CAPABILITIES
              </div>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px' }}>
                {VERIFIED_CAPABILITIES.map(c => (
                  <span
                    key={c.title}
                    style={{
                      fontSize: '11px',
                      fontWeight: 600,
                      padding: '3px 8px',
                      backgroundColor: 'var(--bg-primary)',
                      border: '1px solid var(--border-subtle)',
                      borderRadius: 'var(--radius-sm)',
                      color: 'var(--text-primary)'
                    }}
                  >
                    ✓ {c.title}
                  </span>
                ))}
              </div>
            </div>

            {/* Evidence & Digest */}
            <div style={{ marginBottom: '20px', padding: '10px 12px', backgroundColor: 'var(--bg-primary)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)', fontWeight: 700, textTransform: 'uppercase' }}>
                IMMUTABLE EVIDENCE DIGEST
              </div>
              <div style={{ fontFamily: 'var(--font-mono)', fontSize: '11px', color: 'var(--color-proof)', marginTop: '2px', wordBreak: 'break-all' }}>
                sha256:7f4ae91b4802c6d83a15f0134bc29088514930ba
              </div>
            </div>

            {/* Real Public Actions */}
            <div style={{ display: 'flex', gap: '10px' }}>
              <button
                onClick={handleCopyLink}
                style={{
                  flex: 1,
                  padding: '10px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--color-proof)',
                  color: '#FFFFFF',
                  fontSize: '13px',
                  fontWeight: 700,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '6px'
                }}
              >
                {proofCopied ? <Check size={16} /> : <Share2 size={16} />}
                <span>{proofCopied ? 'LINK COPIED!' : 'COPY PUBLIC LINK'}</span>
              </button>

              <button
                onClick={() => setShowProofModal(false)}
                style={{
                  padding: '10px 20px',
                  borderRadius: 'var(--radius-md)',
                  backgroundColor: 'var(--bg-tertiary)',
                  border: '1px solid var(--border-subtle)',
                  color: 'var(--text-primary)',
                  fontSize: '13px',
                  fontWeight: 600
                }}
              >
                CLOSE
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

/* =========================================================================
   10. TOOLS DASHBOARD
   ========================================================================= */
export const ToolsDashboard: React.FC<Pick<ModuleDashboardProps, 'onNavigate'>> = () => {
  const [copiedTool, setCopiedTool] = useState<string | null>(null);

  const TOOL_CATEGORIES = [
    {
      category: 'Network & Protocols',
      tools: [
        { name: 'Nmap', cmd: 'nmap -sV -sC -p- 198.51.100.24', desc: 'Port scanning & service enumeration' },
        { name: 'Wireshark', cmd: 'tshark -r capture.pcap -Y "http.request"', desc: 'Deep PCAP packet inspection' }
      ]
    },
    {
      category: 'Host Forensics',
      tools: [
        { name: 'Volatility 3', cmd: 'vol -f memdump.raw windows.malfind', desc: 'Kernel memory dump analysis' },
        { name: 'Sysmon', cmd: 'sysmon64 -c config.xml', desc: 'Windows process & network telemetry tracking' }
      ]
    },
    {
      category: 'Detection & Threat Intel',
      tools: [
        { name: 'Sigma', cmd: 'sigmac -t splunk rule.yml', desc: 'Generic detection rule converter' },
        { name: 'YARA', cmd: 'yara -r rules.yar /sample', desc: 'Malware pattern matching engine' }
      ]
    }
  ];

  const handleCopy = (cmd: string, name: string) => {
    navigator.clipboard?.writeText(cmd);
    setCopiedTool(name);
    setTimeout(() => setCopiedTool(null), 1500);
  };

  return (
    <div style={{ maxWidth: '960px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>Security Tools</h1>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          Standard command-line utilities and detection frameworks used in SOC operations.
        </p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        {TOOL_CATEGORIES.map(cat => (
          <div key={cat.category}>
            <div style={{ fontSize: '11px', fontWeight: 700, letterSpacing: '0.06em', textTransform: 'uppercase', color: 'var(--text-muted)', marginBottom: '10px' }}>
              {cat.category}
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '12px' }}>
              {cat.tools.map(t => (
                <div
                  key={t.name}
                  style={{
                    backgroundColor: 'var(--bg-secondary)',
                    border: '1px solid var(--border-subtle)',
                    borderRadius: 'var(--radius-md)',
                    padding: '16px'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '6px' }}>
                    <h3 style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-primary)' }}>{t.name}</h3>
                    <button
                      onClick={() => handleCopy(t.cmd, t.name)}
                      style={{ padding: '4px', color: 'var(--text-muted)', borderRadius: 'var(--radius-sm)' }}
                      title="Copy CLI command"
                    >
                      {copiedTool === t.name ? <Check size={14} color="var(--color-success)" /> : <Copy size={14} />}
                    </button>
                  </div>
                  <p style={{ fontSize: '12px', color: 'var(--text-secondary)', marginBottom: '10px' }}>
                    {t.desc}
                  </p>
                  <div
                    style={{
                      fontFamily: 'var(--font-mono)',
                      fontSize: '11px',
                      padding: '6px 8px',
                      backgroundColor: 'var(--bg-primary)',
                      borderRadius: 'var(--radius-sm)',
                      color: 'var(--text-primary)',
                      overflowX: 'auto',
                      whiteSpace: 'nowrap'
                    }}
                  >
                    {t.cmd}
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

/* =========================================================================
   11. SETTINGS DASHBOARD
   ========================================================================= */
export const SettingsDashboard: React.FC<Pick<ModuleDashboardProps, 'authState' | 'currentTier' | 'onOpenPlanModal'>> = ({
  authState,
  currentTier,
  onOpenPlanModal
}) => {
  const learnerId = authState.identity?.canonicalLearnerId || 'operator_sundas';

  return (
    <div style={{ maxWidth: '780px', margin: '0 auto', padding: '28px 24px 64px 24px' }}>
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ fontSize: '24px', fontWeight: 800, color: 'var(--text-primary)' }}>Settings & Account</h1>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
          Manage your operator identity, security clearance, and environment preferences.
        </p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        {/* Profile */}
        <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-lg)', padding: '20px' }}>
          <h3 style={{ fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '12px' }}>Operator Identity</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', fontSize: '13px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '8px' }}>
              <span style={{ color: 'var(--text-muted)' }}>Learner ID</span>
              <span style={{ fontFamily: 'var(--font-mono)', color: 'var(--text-primary)' }}>{learnerId}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--border-subtle)', paddingBottom: '8px' }}>
              <span style={{ color: 'var(--text-muted)' }}>Security Level</span>
              <span style={{ color: 'var(--color-success)', fontWeight: 600 }}>Level 2 (Tier 2 SOC Candidate)</span>
            </div>
          </div>
        </div>

        {/* Clearance & Subscription */}
        <div style={{ backgroundColor: 'var(--bg-secondary)', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-lg)', padding: '20px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
            <h3 style={{ fontSize: '15px', fontWeight: 700, color: 'var(--text-primary)' }}>Clearance Plan</h3>
            <button
              onClick={onOpenPlanModal}
              style={{ padding: '6px 12px', fontSize: '12px', fontWeight: 700, borderRadius: 'var(--radius-md)', backgroundColor: 'var(--color-home)', color: '#FFFFFF' }}
            >
              Change Plan
            </button>
          </div>
          <p style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
            Active clearance: <strong>{currentTier}</strong>. Entitlements managed authoritatively via RevenueCat.
          </p>
        </div>
      </div>
    </div>
  );
};
