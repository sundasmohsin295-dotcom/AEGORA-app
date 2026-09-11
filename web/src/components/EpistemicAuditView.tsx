import React, { useState } from 'react';
import {
  ShieldCheck,
  AlertOctagon,
  HelpCircle,
  CheckCircle2,
  FileCheck2,
  Sparkles,
  ArrowRight
} from 'lucide-react';
import { EpistemicSignal, EpistemicStatus } from '../core/cyberReality/CyberRealityEngine';

interface EpistemicAuditViewProps {
  signals: EpistemicSignal[];
  onAuditCompleted?: () => void;
}

export const EpistemicAuditView: React.FC<EpistemicAuditViewProps> = ({ signals }) => {
  const [selectedSignalId, setSelectedSignalId] = useState<string>(signals[0]?.id || '');
  const activeSignal = signals.find(s => s.id === selectedSignalId) || signals[0];

  const getStatusColor = (status: EpistemicStatus) => {
    switch (status) {
      case 'FACT':
        return 'var(--accent-indigo)';
      case 'EVIDENCE':
        return 'var(--accent-cyan)';
      case 'INFERENCE':
        return 'var(--accent-emerald)';
      case 'ASSUMPTION':
        return 'var(--accent-rose)';
      case 'UNKNOWN':
        return 'var(--accent-amber)';
    }
  };

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '16px',
        backgroundColor: 'var(--bg-secondary)',
        borderRadius: 'var(--radius-md)',
        border: '1px solid var(--border-subtle)',
        padding: '20px'
      }}
    >
      {/* Header */}
      <div style={{ borderBottom: '1px solid var(--border-subtle)', paddingBottom: '12px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span
            style={{
              fontSize: '10px',
              fontFamily: 'var(--font-mono)',
              fontWeight: 800,
              color: 'var(--accent-cyan)',
              padding: '2px 6px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--accent-cyan-subtle)'
            }}
          >
            CONTROLLED UNCERTAINTY & REASONING AUDIT
          </span>
          <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
            Decoupling AI Confidence from Ground Truth
          </span>
        </div>
        <h3 style={{ fontSize: '15px', fontWeight: 800, marginTop: '4px' }}>
          Epistemic Classification: Fact vs Evidence vs Inference vs Assumption vs Unknown
        </h3>
      </div>

      {/* Epistemic Definitions Legend */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))',
          gap: '8px'
        }}
      >
        <EpistemicLegendItem
          status="FACT"
          color="var(--accent-indigo)"
          desc="Immutable ground reality (User ID, Ground Clock)"
        />
        <EpistemicLegendItem
          status="EVIDENCE"
          color="var(--accent-cyan)"
          desc="Recorded audit logs (Event 4624/4625 payload)"
        />
        <EpistemicLegendItem
          status="INFERENCE"
          color="var(--accent-emerald)"
          desc="Valid mathematical deduction (Velocity delta)"
        />
        <EpistemicLegendItem
          status="ASSUMPTION"
          color="var(--accent-rose)"
          desc="Uncorroborated claim (AI asserted IP)"
        />
        <EpistemicLegendItem
          status="UNKNOWN"
          color="var(--accent-amber)"
          desc="Limits of evidence (Compromise vector)"
        />
      </div>

      {/* Signals List & Detailed Breakdown */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
          gap: '16px',
          marginTop: '8px'
        }}
      >
        {/* Signal List */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {signals.map(sig => {
            const isSelected = selectedSignalId === sig.id;
            return (
              <div
                key={sig.id}
                onClick={() => setSelectedSignalId(sig.id)}
                style={{
                  padding: '12px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: isSelected ? 'var(--bg-tertiary)' : 'var(--bg-primary)',
                  border: isSelected ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
                  cursor: 'pointer'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                  <span
                    style={{
                      fontSize: '10px',
                      fontFamily: 'var(--font-mono)',
                      fontWeight: 800,
                      color: getStatusColor(sig.epistemicStatus)
                    }}
                  >
                    [{sig.epistemicStatus}] // {sig.source}
                  </span>
                  <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>
                    {sig.corroboratingEventIds.length} telemetry logs
                  </span>
                </div>
                <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-primary)', lineHeight: 1.4 }}>
                  {sig.claim}
                </div>
              </div>
            );
          })}
        </div>

        {/* Selected Signal Detail */}
        {activeSignal && (
          <div
            style={{
              padding: '16px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--border-subtle)',
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between'
            }}
          >
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '10px' }}>
                <span
                  style={{
                    fontSize: '11px',
                    fontFamily: 'var(--font-mono)',
                    fontWeight: 900,
                    padding: '3px 8px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--bg-primary)',
                    color: getStatusColor(activeSignal.epistemicStatus),
                    border: `1px solid ${getStatusColor(activeSignal.epistemicStatus)}`
                  }}
                >
                  CLASSIFICATION: {activeSignal.epistemicStatus}
                </span>
                <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                  SOURCE: {activeSignal.source}
                </span>
              </div>

              <div
                style={{
                  padding: '12px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: 'var(--bg-primary)',
                  border: '1px solid var(--border-subtle)',
                  fontSize: '13px',
                  fontWeight: 600,
                  lineHeight: 1.5,
                  marginBottom: '14px'
                }}
              >
                "{activeSignal.claim}"
              </div>

              <div style={{ fontSize: '12px', color: 'var(--text-secondary)', lineHeight: 1.5, marginBottom: '14px' }}>
                <strong>Epistemic Defense Analysis:</strong> {activeSignal.explanation}
              </div>

              {activeSignal.corroboratingEventIds.length > 0 ? (
                <div>
                  <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-cyan)', fontWeight: 700, marginBottom: '6px' }}>
                    GROUND TELEMETRY CORROBORATION:
                  </div>
                  <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                    {activeSignal.corroboratingEventIds.map(id => (
                      <span
                        key={id}
                        style={{
                          fontSize: '10px',
                          fontFamily: 'var(--font-mono)',
                          padding: '3px 8px',
                          borderRadius: '2px',
                          backgroundColor: 'var(--bg-primary)',
                          border: '1px solid var(--accent-cyan)',
                          color: 'var(--accent-cyan)'
                        }}
                      >
                        {id} VERIFIED
                      </span>
                    ))}
                  </div>
                </div>
              ) : (
                <div
                  style={{
                    padding: '10px 12px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'rgba(239, 68, 68, 0.1)',
                    border: '1px solid var(--accent-rose)',
                    color: 'var(--accent-rose)',
                    fontSize: '11px',
                    lineHeight: 1.4
                  }}
                >
                  <strong>Cognitive Hazard:</strong> This assertion was generated by an external or AI source without corroboration from host or network audit logs. Accepting it without local verification violates SOC evidence standards.
                </div>
              )}
            </div>

            <div
              style={{
                marginTop: '16px',
                paddingTop: '12px',
                borderTop: '1px solid var(--border-subtle)',
                fontSize: '10px',
                fontFamily: 'var(--font-mono)',
                color: 'var(--text-muted)'
              }}
            >
              AEGORA Epistemic Rigor Standard // Audit Key: {activeSignal.id}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

interface EpistemicLegendItemProps {
  status: string;
  color: string;
  desc: string;
}

const EpistemicLegendItem: React.FC<EpistemicLegendItemProps> = ({ status, color, desc }) => (
  <div
    style={{
      padding: '8px',
      borderRadius: 'var(--radius-sm)',
      backgroundColor: 'var(--bg-primary)',
      border: '1px solid var(--border-subtle)'
    }}
  >
    <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', fontWeight: 800, color }}>
      {status}
    </div>
    <div style={{ fontSize: '10px', color: 'var(--text-secondary)', marginTop: '2px', lineHeight: 1.3 }}>
      {desc}
    </div>
  </div>
);
