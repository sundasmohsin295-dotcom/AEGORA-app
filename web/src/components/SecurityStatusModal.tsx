import React from 'react';
import { Shield, Lock, AlertTriangle, X, CheckCircle2 } from 'lucide-react';
import { AuthState } from '../core/auth/CrossPlatformAuthClient';

interface SecurityStatusModalProps {
  isOpen: boolean;
  onClose: () => void;
  authState: AuthState;
}

export const SecurityStatusModal: React.FC<SecurityStatusModalProps> = ({
  isOpen,
  onClose,
  authState
}) => {
  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.75)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '24px',
        zIndex: 100
      }}
      onClick={onClose}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '640px',
          borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--bg-secondary)',
          border: '1px solid var(--accent-amber)',
          boxShadow: 'var(--shadow-lg)',
          padding: '24px',
          position: 'relative'
        }}
        onClick={e => e.stopPropagation()}
      >
        <button
          onClick={onClose}
          style={{
            position: 'absolute',
            top: '16px',
            right: '16px',
            color: 'var(--text-muted)'
          }}
        >
          <X size={20} />
        </button>

        <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
          <div
            style={{
              padding: '8px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--accent-amber-subtle)',
              color: 'var(--accent-amber)'
            }}
          >
            <Lock size={20} />
          </div>
          <div>
            <h2 style={{ fontSize: '18px', fontWeight: 800 }}>
              Identity & Security Architecture Status
            </h2>
            <div style={{ fontSize: '11px', fontFamily: 'var(--font-mono)', color: 'var(--accent-amber)', fontWeight: 700 }}>
              STATUS: AUTH BACKEND BLOCKED
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px', fontSize: '13px', lineHeight: 1.6 }}>
          <div
            style={{
              padding: '12px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ fontWeight: 700, marginBottom: '4px', color: 'var(--text-primary)' }}>
              1. Architectural Transparency
            </div>
            <p style={{ color: 'var(--text-secondary)' }}>
              {authState.blockedReason ||
                'External Identity Provider credentials are not configured. The system strictly refuses to manufacture mock credentials or fake Firebase state.'}
            </p>
          </div>

          <div
            style={{
              padding: '12px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ fontWeight: 700, marginBottom: '4px', color: 'var(--text-primary)' }}>
              2. Deterministic Canonical Identity
            </div>
            <p style={{ color: 'var(--text-secondary)' }}>
              AEGORA derives canonical IDs deterministically:
              <br />
              <code>providerUid → usr_&lt;sanitizedUid&gt;</code> (Canonical User ID)
              <br />
              <code>providerUid → operator_&lt;sanitizedUid&gt;</code> (Canonical Learner ID)
              <br />
              This ensures that once real OAuth/Firebase authentication is supplied, Android and Web automatically resolve to the identical operator record.
            </p>
          </div>

          <div
            style={{
              padding: '12px',
              borderRadius: 'var(--radius-sm)',
              backgroundColor: 'var(--bg-tertiary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ fontWeight: 700, marginBottom: '4px', color: 'var(--text-primary)' }}>
              3. Zero-Trust Client UI Boundary
            </div>
            <p style={{ color: 'var(--text-secondary)' }}>
              Client UI inputs are never authoritative. Evidence hashes and capability updates require real 7-gate rubric evaluation on the mission engine. Arbitrary learner IDs passed in client parameters are rejected by domain guards.
            </p>
          </div>
        </div>

        <div style={{ marginTop: '20px', display: 'flex', justifyContent: 'flex-end' }}>
          <button
            onClick={onClose}
            style={{
              padding: '8px 18px',
              backgroundColor: 'var(--accent-cyan)',
              color: '#0a0e17',
              fontWeight: 700,
              fontSize: '13px',
              borderRadius: 'var(--radius-sm)'
            }}
          >
            Acknowledge & Close
          </button>
        </div>
      </div>
    </div>
  );
};
