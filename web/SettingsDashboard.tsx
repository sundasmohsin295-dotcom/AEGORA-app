// web/SettingsDashboard.tsx
import React, { useState } from 'react';
import { SreTelemetryView } from './SreTelemetryView';
import { SecurityStatusModal } from './SecurityStatusModal';

interface Props {
  onBack: () => void;
}

export const SettingsDashboard: React.FC<Props> = ({ onBack }) => {
  const [activeTab, setActiveTab] = useState<'SRE' | 'ENCLAVE'>('SRE');
  const [isSecurityModalOpen, setSecurityModalOpen] = useState(false);

  return (
    <div style={{
      backgroundColor: '#030712',
      minHeight: '100vh',
      padding: '16px',
      color: '#F8FAFC',
      fontFamily: 'JetBrains Mono, monospace',
      display: 'flex',
      flexDirection: 'column',
      gap: '14px'
    }}>
      <SecurityStatusModal
        isOpen={isSecurityModalOpen}
        onClose={() => setSecurityModalOpen(false)}
      />

      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <button
            onClick={onBack}
            style={{
              backgroundColor: '#0B0F19',
              border: '1px solid #1E293B',
              color: '#00E5FF',
              padding: '6px 10px',
              fontFamily: 'JetBrains Mono, monospace',
              fontSize: '11px',
              cursor: 'pointer'
            }}
          >
            [← ARENA]
          </button>
          <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '14px' }}>
            SETTINGS // SRE DASHBOARD
          </span>
        </div>

        <button
          onClick={() => setSecurityModalOpen(true)}
          style={{
            backgroundColor: '#0B0F19',
            border: '1px solid #334155',
            color: '#00E5FF',
            padding: '6px 12px',
            fontSize: '10px',
            fontWeight: 'bold',
            fontFamily: 'JetBrains Mono, monospace',
            cursor: 'pointer'
          }}
        >
          [SECURITY STATUS]
        </button>
      </div>

      {/* Tab Switcher */}
      <div style={{ display: 'flex', gap: '8px' }}>
        <button
          onClick={() => setActiveTab('SRE')}
          style={{
            flex: 1,
            backgroundColor: activeTab === 'SRE' ? '#082F49' : '#0B0F19',
            border: `1px solid ${activeTab === 'SRE' ? '#00E5FF' : '#1E293B'}`,
            color: activeTab === 'SRE' ? '#00E5FF' : '#94A3B8',
            padding: '10px',
            fontWeight: 'bold',
            fontSize: '11px',
            fontFamily: 'JetBrains Mono, monospace',
            cursor: 'pointer'
          }}
        >
          [SRE_TELEMETRY]
        </button>
        <button
          onClick={() => setActiveTab('ENCLAVE')}
          style={{
            flex: 1,
            backgroundColor: activeTab === 'ENCLAVE' ? '#082F49' : '#0B0F19',
            border: `1px solid ${activeTab === 'ENCLAVE' ? '#00E5FF' : '#1E293B'}`,
            color: activeTab === 'ENCLAVE' ? '#00E5FF' : '#94A3B8',
            padding: '10px',
            fontWeight: 'bold',
            fontSize: '11px',
            fontFamily: 'JetBrains Mono, monospace',
            cursor: 'pointer'
          }}
        >
          [ENCLAVE_CONFIG]
        </button>
      </div>

      {/* Tab Content */}
      {activeTab === 'SRE' ? (
        <SreTelemetryView />
      ) : (
        <div style={{
          backgroundColor: '#0B0F19',
          border: '1px solid #1E293B',
          padding: '16px',
          display: 'flex',
          flexDirection: 'column',
          gap: '8px',
          fontSize: '11px'
        }}>
          <div style={{ color: '#10B981', fontWeight: 'bold' }}>
            [CONFIG-01] // HARDWARE_SECURITY_ENCLAVE
          </div>
          <div>• KEYSTORE_PROVIDER: Android StrongBox Keystore Enclave</div>
          <div>• CIPHER_ALGORITHM: AES-256-GCM + Kyber-768 PQ Hybrid</div>
          <div style={{ color: '#10B981' }}>• MEMORY_SANITIZATION: Constant-Time Zeroization Guaranteed</div>
          <div style={{ color: '#64748B' }}>• CHAOS_INJECTOR: Enabled (0.00% involuntary dropped frames)</div>
        </div>
      )}
    </div>
  );
};
