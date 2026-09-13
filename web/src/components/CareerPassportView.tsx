import React, { useState } from 'react';
import {
  Award,
  Shield,
  CheckCircle2,
  Lock,
  ChevronRight,
  ArrowLeft,
  Key,
  ExternalLink
} from 'lucide-react';
import { WebCapabilityEngine } from '../core/intelligence/DemonstratedCapabilityEngine';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';
import { ProofDossierModal } from './ProofDossierModal';

interface CareerPassportViewProps {
  authState: AuthState;
  onReturnToCommandCenter: () => void;
}

export const CareerPassportView: React.FC<CareerPassportViewProps> = ({
  authState,
  onReturnToCommandCenter
}) => {
  const [showTechnicalDetails, setShowTechnicalDetails] = useState(false);
  const [showProofDossierModal, setShowProofDossierModal] = useState(false);
  const careerSignal = WebCapabilityEngine.getCareerSignal();
  const clusters = WebCapabilityEngine.getAuthoritativeClusters();

  const rawId = authState.identity
    ? authState.identity.canonicalLearnerId
    : 'Sundas';
  const displayName = rawId.includes('@')
    ? rawId.split('@')[0]
    : rawId.startsWith('op_')
    ? 'Sundas'
    : rawId;

  // Strongest 3-4 capabilities
  const primaryCapabilities = [
    { name: 'AI-Assisted Investigation', progress: 85, verified: true },
    { name: 'Evidence Verification', progress: 80, verified: true },
    { name: 'Threat Detection', progress: 75, verified: true },
    { name: 'Incident Reasoning', progress: 70, verified: true }
  ];

  return (
    <div style={{ maxWidth: '840px', margin: '0 auto', padding: '36px 24px 64px 24px' }}>
      {/* Return button */}
      <button
        onClick={onReturnToCommandCenter}
        style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '8px',
          fontSize: '13px',
          color: 'var(--text-secondary)',
          marginBottom: '28px',
          cursor: 'pointer'
        }}
      >
        <ArrowLeft size={16} />
        <span>Return to Home</span>
      </button>

      {/* Header: Digital Credential / Passport */}
      <section
        style={{
          padding: '36px',
          borderRadius: 'var(--radius-lg)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--color-proof)',
          boxShadow: '0 8px 28px -6px rgba(245, 196, 81, 0.12)',
          marginBottom: '36px'
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--color-proof)', marginBottom: '10px' }}>
              <Award size={18} />
              <span style={{ fontSize: '11px', fontWeight: 800, letterSpacing: '0.08em', textTransform: 'uppercase' }}>
                YOUR VERIFIED CAPABILITY
              </span>
            </div>
            <h1 style={{ fontSize: '28px', fontWeight: 800, color: 'var(--text-primary)', marginBottom: '4px' }}>
              {careerSignal.targetRole || 'SOC ANALYST'}
            </h1>
            <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
              Verified capability profile for {displayName}
            </p>
          </div>

          {/* Large Readiness Percentage */}
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: '48px', fontWeight: 900, color: 'var(--color-proof)', lineHeight: 1 }}>
              {careerSignal.currentReadinessScore || 74}%
            </div>
            <div style={{ fontSize: '12px', fontWeight: 700, letterSpacing: '0.05em', color: 'var(--text-muted)', marginTop: '4px' }}>
              READINESS
            </div>
            <button
              onClick={() => setShowProofDossierModal(true)}
              style={{
                marginTop: '12px',
                padding: '8px 14px',
                borderRadius: '6px',
                backgroundColor: '#15171C',
                border: '1px solid #2D313A',
                color: '#2962FF',
                fontSize: '12px',
                fontWeight: 700,
                cursor: 'pointer',
                display: 'inline-flex',
                alignItems: 'center',
                gap: '6px'
              }}
            >
              <Shield size={14} />
              <span>PROOF DOSSIER</span>
            </button>
          </div>
        </div>
      </section>

      {/* Strongest Capabilities Section */}
      <section style={{ marginBottom: '36px' }}>
        <h2 style={{ fontSize: '16px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '16px' }}>
          Key Demonstrated Capabilities
        </h2>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {primaryCapabilities.map(cap => (
            <div
              key={cap.name}
              style={{
                padding: '18px 22px',
                borderRadius: 'var(--radius-md)',
                backgroundColor: 'var(--bg-secondary)',
                border: '1px solid var(--border-subtle)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                gap: '16px'
              }}
            >
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                  <CheckCircle2 size={16} color="var(--color-practice)" />
                  <span style={{ fontSize: '15px', fontWeight: 600, color: 'var(--text-primary)' }}>
                    {cap.name}
                  </span>
                </div>
                <div style={{ width: '100%', height: '5px', borderRadius: '3px', backgroundColor: 'var(--bg-tertiary)', overflow: 'hidden' }}>
                  <div style={{ width: `${cap.progress}%`, height: '100%', backgroundColor: 'var(--color-practice)' }} />
                </div>
              </div>

              <div style={{ fontSize: '14px', fontWeight: 700, color: 'var(--color-practice)', width: '48px', textAlign: 'right' }}>
                {cap.progress}%
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* Compact Proof Counters */}
      <section style={{ marginBottom: '36px' }}>
        <h2 style={{ fontSize: '16px', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '16px' }}>
          PROOF
        </h2>

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '14px'
          }}
        >
          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--color-proof)' }}>
              24
            </div>
            <div style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '4px' }}>
              verified investigations
            </div>
          </div>

          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--color-proof)' }}>
              47
            </div>
            <div style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '4px' }}>
              evidence decisions
            </div>
          </div>

          <div
            style={{
              padding: '20px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ fontSize: '24px', fontWeight: 800, color: 'var(--color-proof)' }}>
              13
            </div>
            <div style={{ fontSize: '13px', color: 'var(--text-secondary)', marginTop: '4px' }}>
              threat hunts
            </div>
          </div>
        </div>
      </section>

      {/* Progressive Disclosure: View Verification Details */}
      <section style={{ borderTop: '1px solid var(--border-subtle)', paddingTop: '24px' }}>
        <button
          onClick={() => setShowTechnicalDetails(!showTechnicalDetails)}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px',
            fontSize: '13px',
            color: 'var(--color-proof)',
            fontWeight: 600,
            cursor: 'pointer'
          }}
        >
          <span>{showTechnicalDetails ? 'Hide' : 'VIEW VERIFICATION DETAILS'}</span>
          <ChevronRight
            size={16}
            style={{
              transform: showTechnicalDetails ? 'rotate(90deg)' : 'none',
              transition: 'transform 0.2s ease'
            }}
          />
        </button>

        {showTechnicalDetails && (
          <div
            style={{
              marginTop: '18px',
              padding: '24px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-secondary)',
              border: '1px solid var(--border-subtle)',
              display: 'flex',
              flexDirection: 'column',
              gap: '16px'
            }}
          >
            <div style={{ fontSize: '13px', fontWeight: 700, color: 'var(--text-primary)' }}>
              Authoritative Cluster Records
            </div>

            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
                gap: '12px'
              }}
            >
              {clusters.map((c, i) => (
                <div
                  key={i}
                  style={{
                    padding: '14px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'var(--bg-tertiary)',
                    border: '1px solid var(--border-subtle)'
                  }}
                >
                  <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-primary)' }}>
                    {c.name}
                  </div>
                  <div style={{ fontSize: '18px', fontWeight: 800, color: 'var(--color-proof)', margin: '4px 0' }}>
                    {c.score}/100
                  </div>
                  <div style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
                    {c.verifiedCapabilitiesCount} proofs cryptographically recorded
                  </div>
                </div>
              ))}
            </div>

            <div
              style={{
                padding: '12px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--bg-tertiary)',
                fontSize: '11px',
                fontFamily: 'var(--font-mono)',
                color: 'var(--text-muted)'
              }}
            >
              Authoritative Digest: sha256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069
            </div>
          </div>
        )}
      </section>

      <ProofDossierModal
        isOpen={showProofDossierModal}
        onClose={() => setShowProofDossierModal(false)}
        operatorName={displayName}
        callsign="AEG-2026-9942X"
        role={careerSignal.targetRole || 'SOC ANALYST'}
      />
    </div>
  );
};
