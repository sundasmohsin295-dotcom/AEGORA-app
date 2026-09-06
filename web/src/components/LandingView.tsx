import React from 'react';
import { Shield, Award, Terminal, Target, ArrowRight, CheckCircle2, Lock, Cpu, Database } from 'lucide-react';

interface LandingViewProps {
  onLaunchCommandCenter: () => void;
  onLaunchMission: () => void;
}

export const LandingView: React.FC<LandingViewProps> = ({
  onLaunchCommandCenter,
  onLaunchMission
}) => {
  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '48px 24px' }}>
      {/* Hero Section */}
      <section style={{ textAlign: 'center', marginBottom: '64px' }}>
        <div
          style={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: '8px',
            padding: '4px 12px',
            borderRadius: 'var(--radius-sm)',
            backgroundColor: 'var(--accent-cyan-subtle)',
            border: '1px solid var(--accent-cyan)',
            color: 'var(--accent-cyan)',
            fontSize: '12px',
            fontFamily: 'var(--font-mono)',
            fontWeight: 600,
            marginBottom: '20px'
          }}
        >
          <Terminal size={14} />
          <span>CYBERSECURITY CAPABILITY OPERATING SYSTEM</span>
        </div>

        <h1
          style={{
            fontSize: '48px',
            fontWeight: 800,
            lineHeight: 1.15,
            letterSpacing: '-0.02em',
            marginBottom: '16px',
            color: 'var(--text-primary)'
          }}
        >
          Learn. Operate. Prove. <br />
          <span style={{ color: 'var(--accent-cyan)' }}>Become Job Ready.</span>
        </h1>

        <p
          style={{
            fontSize: '18px',
            color: 'var(--text-secondary)',
            maxWidth: '720px',
            margin: '0 auto 32px auto',
            lineHeight: 1.6
          }}
        >
          AEGORA replaces passive videos and superficial badges with live SOC telemetry,
          incident triage, and cryptographically verified evidence of what you can actually do.
        </p>

        <div style={{ display: 'flex', justifyContent: 'center', gap: '16px', flexWrap: 'wrap' }}>
          <button
            onClick={onLaunchCommandCenter}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              padding: '12px 28px',
              backgroundColor: 'var(--accent-cyan)',
              color: '#0a0e17',
              fontWeight: 700,
              fontSize: '15px',
              borderRadius: 'var(--radius-sm)',
              boxShadow: 'var(--shadow-md)',
              transition: 'transform 0.15s ease'
            }}
          >
            <span>LAUNCH COMMAND CENTER</span>
            <ArrowRight size={16} />
          </button>

          <button
            onClick={onLaunchMission}
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '10px',
              padding: '12px 24px',
              backgroundColor: 'var(--bg-tertiary)',
              color: 'var(--text-primary)',
              border: '1px solid var(--border-strong)',
              fontWeight: 600,
              fontSize: '15px',
              borderRadius: 'var(--radius-sm)'
            }}
          >
            <Shield size={16} color="var(--accent-emerald)" />
            <span>EXECUTE LIVE MISSION // SUSPICIOUS LOGIN</span>
          </button>
        </div>
      </section>

      {/* Core Architectural Pillars */}
      <section style={{ marginBottom: '64px' }}>
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <span
            style={{
              fontSize: '12px',
              fontFamily: 'var(--font-mono)',
              color: 'var(--text-muted)',
              textTransform: 'uppercase',
              letterSpacing: '0.1em'
            }}
          >
            THE PLATFORM ARCHITECTURE
          </span>
          <h2 style={{ fontSize: '28px', fontWeight: 700, marginTop: '4px' }}>
            One Identity. One Capability Graph. Real Evidence.
          </h2>
        </div>

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))',
            gap: '20px'
          }}
        >
          {[
            {
              icon: <Terminal color="var(--accent-cyan)" size={24} />,
              title: 'Real Telemetry Missions',
              desc: 'Live incident simulations with authentic Windows Event logs (4624/4625), memory dumps, and PCAP data.'
            },
            {
              icon: <Cpu color="var(--accent-indigo)" size={24} />,
              title: 'Cyber Twin 6.0 Engine',
              desc: 'Evaluates your capability across 4 authoritative cognitive clusters without fake point gamification.'
            },
            {
              icon: <Lock color="var(--accent-emerald)" size={24} />,
              title: 'Cryptographic Proof',
              desc: 'Every demonstrated skill generates an immutable evidence hash tied authoritatively to your canonical identity.'
            },
            {
              icon: <Award color="var(--accent-amber)" size={24} />,
              title: 'Verifiable Skill Passport',
              desc: 'Translates technical triage into measurable employer readiness metrics for Senior SOC and Detection roles.'
            }
          ].map((card, idx) => (
            <div
              key={idx}
              style={{
                padding: '24px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-card)',
                border: '1px solid var(--border-subtle)',
                display: 'flex',
                flexDirection: 'column',
                gap: '12px'
              }}
            >
              <div
                style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--bg-tertiary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  border: '1px solid var(--border-subtle)'
                }}
              >
                {card.icon}
              </div>
              <h3 style={{ fontSize: '16px', fontWeight: 700 }}>{card.title}</h3>
              <p style={{ fontSize: '13px', color: 'var(--text-secondary)', lineHeight: 1.5 }}>
                {card.desc}
              </p>
            </div>
          ))}
        </div>
      </section>

      {/* Cross-Platform Continuity Guarantee */}
      <section
        style={{
          padding: '32px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-strong)',
          display: 'flex',
          flexDirection: 'column',
          gap: '20px'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Database size={22} color="var(--accent-cyan)" />
          <h3 style={{ fontSize: '18px', fontWeight: 700 }}>
            Android + Web Synchronized Ecosystem
          </h3>
        </div>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)', lineHeight: 1.6 }}>
          Start an investigation on Android while on shift, pick up deep PCAP packet analysis on
          AEGORA Web on your desktop workstation. The exact same canonical learner profile,
          demonstrated capability graph, and cryptographic evidence hashes follow your authenticated
          identity seamlessly.
        </p>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '16px',
            fontSize: '13px'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <CheckCircle2 size={16} color="var(--accent-emerald)" />
            <span>Shared Canonical Identity</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <CheckCircle2 size={16} color="var(--accent-emerald)" />
            <span>Zero-Trust Client Authorization</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <CheckCircle2 size={16} color="var(--accent-emerald)" />
            <span>Real 7-Gate Rubric Evaluation</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <CheckCircle2 size={16} color="var(--accent-emerald)" />
            <span>Tamper-Resistant Proof Hashes</span>
          </div>
        </div>
      </section>
    </div>
  );
};
