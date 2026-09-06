import React from 'react';
import { Award, Shield, CheckCircle2, Lock, ExternalLink, Terminal } from 'lucide-react';
import { WebCapabilityEngine } from '../core/intelligence/DemonstratedCapabilityEngine';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';

interface CareerPassportViewProps {
  authState: AuthState;
  onReturnToCommandCenter: () => void;
}

export const CareerPassportView: React.FC<CareerPassportViewProps> = ({
  authState,
  onReturnToCommandCenter
}) => {
  const clusters = WebCapabilityEngine.getAuthoritativeClusters();
  const careerSignal = WebCapabilityEngine.getCareerSignal();
  const treasures = WebCapabilityEngine.getCyberTreasure();

  const operatorId = authState.identity
    ? authState.identity.canonicalLearnerId
    : 'operator_guest_mode';

  return (
    <div style={{ maxWidth: '1100px', margin: '0 auto', padding: '24px' }}>
      {/* Passport Header */}
      <div
        style={{
          padding: '24px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          marginBottom: '24px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '16px'
        }}
      >
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
            <Award color="var(--accent-amber)" size={20} />
            <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-amber)', fontWeight: 700 }}>
              IMMUTABLE CAPABILITY RECORD
            </span>
          </div>
          <h1 style={{ fontSize: '24px', fontWeight: 800 }}>AEGORA Verifiable Skill Passport</h1>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginTop: '4px', fontSize: '13px', color: 'var(--text-secondary)' }}>
            <span>Operator: <code style={{ color: 'var(--accent-cyan)' }}>{operatorId}</code></span>
            <span>•</span>
            <span>Career Target: <strong>{careerSignal.targetRole}</strong></span>
            <span>•</span>
            <span>Readiness: <strong style={{ color: 'var(--accent-emerald)' }}>{careerSignal.currentReadinessScore}%</strong></span>
          </div>
        </div>

        <button
          onClick={onReturnToCommandCenter}
          style={{
            padding: '10px 18px',
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

      {/* Cluster Capability Matrix */}
      <div
        style={{
          padding: '20px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)',
          marginBottom: '24px'
        }}
      >
        <h3 style={{ fontSize: '15px', fontWeight: 700, marginBottom: '16px' }}>
          Authoritative Cognitive Cluster Breakdown
        </h3>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
            gap: '16px'
          }}
        >
          {clusters.map((c, i) => (
            <div
              key={i}
              style={{
                padding: '16px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--bg-tertiary)',
                border: '1px solid var(--border-subtle)'
              }}
            >
              <div style={{ fontSize: '13px', fontWeight: 700 }}>{c.name}</div>
              <div
                style={{
                  fontSize: '22px',
                  fontWeight: 800,
                  fontFamily: 'var(--font-mono)',
                  color: 'var(--accent-cyan)',
                  margin: '8px 0'
                }}
              >
                {c.score}/100
              </div>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                {c.verifiedCapabilitiesCount} verified capabilities logged
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Verified Evidence Ledger */}
      <div
        style={{
          padding: '20px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--border-subtle)'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h3 style={{ fontSize: '15px', fontWeight: 700 }}>
            Cryptographically Verified Evidence Ledger ({treasures.length} Proofs)
          </h3>
          <span style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-emerald)', fontWeight: 600 }}>
            ZERO-TAMPER GUARANTEED
          </span>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {treasures.map(item => (
            <div
              key={item.id}
              style={{
                padding: '14px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--bg-tertiary)',
                border: '1px solid var(--border-subtle)',
                display: 'flex',
                flexDirection: 'column',
                gap: '6px'
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <CheckCircle2 color="var(--accent-emerald)" size={16} />
                  <span style={{ fontWeight: 700, fontSize: '13px' }}>{item.title}</span>
                </div>
                <span
                  style={{
                    fontSize: '12px',
                    fontFamily: 'var(--font-mono)',
                    color: 'var(--accent-emerald)',
                    fontWeight: 700
                  }}
                >
                  Score: {item.demonstratedScore}%
                </span>
              </div>
              <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                Category: {item.category} • Date: {item.unlockedAt}
              </div>
              <div
                style={{
                  fontFamily: 'var(--font-mono)',
                  fontSize: '11px',
                  backgroundColor: 'var(--bg-primary)',
                  padding: '6px 8px',
                  borderRadius: 'var(--radius-sm)',
                  color: 'var(--accent-cyan)',
                  wordBreak: 'break-all',
                  border: '1px solid var(--border-subtle)'
                }}
              >
                {item.evidenceHash}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
