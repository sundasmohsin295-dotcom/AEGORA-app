import React from 'react';
import { Bell, Check, X, Shield, ArrowRight } from 'lucide-react';
import { AppView } from './NavigationSidebar';

interface NotificationsModalProps {
  isOpen: boolean;
  onClose: () => void;
  onNavigate: (view: AppView) => void;
  onLaunchMission: (missionId: string) => void;
}

export const NotificationsModal: React.FC<NotificationsModalProps> = ({
  isOpen,
  onClose,
  onNavigate,
  onLaunchMission
}) => {
  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(17, 24, 39, 0.45)',
        backdropFilter: 'blur(3px)',
        zIndex: 100,
        display: 'flex',
        alignItems: 'flex-start',
        justifyContent: 'flex-end',
        paddingTop: '64px',
        paddingRight: '20px'
      }}
      onClick={onClose}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '380px',
          backgroundColor: 'var(--bg-secondary)',
          borderRadius: 'var(--radius-lg)',
          border: '1px solid var(--border-subtle)',
          boxShadow: 'var(--shadow-lg)',
          overflow: 'hidden'
        }}
        onClick={e => e.stopPropagation()}
      >
        <div
          style={{
            padding: '16px 20px',
            borderBottom: '1px solid var(--border-subtle)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Bell size={16} color="var(--color-home)" />
            <h3 style={{ fontSize: '14px', fontWeight: 700, color: 'var(--text-primary)' }}>
              Notifications
            </h3>
          </div>
          <button
            onClick={onClose}
            style={{ color: 'var(--text-muted)', padding: '4px' }}
          >
            <X size={16} />
          </button>
        </div>

        <div style={{ padding: '12px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {/* Item 1 */}
          <div
            style={{
              padding: '12px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-primary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-investigate)' }}>
                TACTICAL MISSION
              </span>
              <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>Just now</span>
            </div>
            <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-primary)', marginTop: '2px' }}>
              Suspicious Login Triage Ready
            </div>
            <p style={{ fontSize: '11px', color: 'var(--text-secondary)', marginTop: '2px' }}>
              Target primary blocker: Correlate Sysmon ID 3 with external Suricata alert.
            </p>
            <button
              onClick={() => {
                onLaunchMission('mission_suspicious_login_reality');
                onClose();
              }}
              style={{
                marginTop: '8px',
                padding: '4px 10px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--color-home)',
                color: '#FFFFFF',
                fontSize: '11px',
                fontWeight: 700,
                display: 'inline-flex',
                alignItems: 'center',
                gap: '4px'
              }}
            >
              <span>Execute</span>
              <ArrowRight size={11} />
            </button>
          </div>

          {/* Item 2 */}
          <div
            style={{
              padding: '12px',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--bg-primary)',
              border: '1px solid var(--border-subtle)'
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <span style={{ fontSize: '10px', fontWeight: 700, color: 'var(--color-proof)' }}>
                PROOF SIGNED
              </span>
              <span style={{ fontSize: '10px', color: 'var(--text-muted)' }}>2h ago</span>
            </div>
            <div style={{ fontSize: '12px', fontWeight: 600, color: 'var(--text-primary)', marginTop: '2px' }}>
              Cryptographic Digest sha256:7f4ae91b
            </div>
            <p style={{ fontSize: '11px', color: 'var(--text-secondary)', marginTop: '2px' }}>
              Evidence verification recorded in local proof dossier.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
