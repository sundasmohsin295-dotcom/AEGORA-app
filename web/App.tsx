// web/App.tsx
import React, { useState } from 'react';
import { GlobalErrorBoundary } from './GlobalErrorBoundary';
import { SettingsDashboard } from './SettingsDashboard';
import { SecurityStatusModal } from './SecurityStatusModal';
import { DiagnosticStore } from './DiagnosticStore';
import { MerkleChainVisualizer } from './MerkleChainVisualizer';

export const MainAppContent: React.FC = () => {
  const [currentView, setCurrentView] = useState<'ARENA' | 'SETTINGS'>('ARENA');
  const [isSecurityModalOpen, setSecurityModalOpen] = useState(false);
  const [shouldCrash, setShouldCrash] = useState(false);

  if (shouldCrash) {
    throw new Error('Simulated critical UI render failure in MainAppContent tree');
  }

  if (currentView === 'SETTINGS') {
    return <SettingsDashboard onBack={() => setCurrentView('ARENA')} />;
  }

  return (
    <div style={{
      backgroundColor: '#030712',
      minHeight: '100vh',
      padding: '20px',
      color: '#F8FAFC',
      fontFamily: 'JetBrains Mono, monospace',
      display: 'flex',
      flexDirection: 'column',
      gap: '16px'
    }}>
      <SecurityStatusModal
        isOpen={isSecurityModalOpen}
        onClose={() => setSecurityModalOpen(false)}
      />

      {/* Industrial Action Bar */}
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderBottom: '1px solid #1E293B',
        paddingBottom: '12px'
      }}>
        <div>
          <div style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '18px', letterSpacing: '1px' }}>
            CYBER DUEL ARENA
          </div>
          <div style={{ color: '#64748B', fontSize: '11px', marginTop: '2px' }}>
            SRE TELEMETRY & DETERMINISTIC SECURITY ACTIVE
          </div>
        </div>

        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={() => setSecurityModalOpen(true)}
            style={{
              backgroundColor: '#0B0F19',
              border: '1px solid #1E293B',
              color: '#00E5FF',
              padding: '6px 12px',
              fontSize: '10px',
              fontFamily: 'JetBrains Mono, monospace',
              cursor: 'pointer'
            }}
          >
            [SECURITY STATUS]
          </button>

          <button
            onClick={() => setCurrentView('SETTINGS')}
            style={{
              backgroundColor: '#0B0F19',
              border: '1px solid #00E5FF',
              color: '#00E5FF',
              padding: '6px 12px',
              fontSize: '10px',
              fontWeight: 'bold',
              fontFamily: 'JetBrains Mono, monospace',
              cursor: 'pointer'
            }}
          >
            [SRE DASHBOARD]
          </button>

          <button
            onClick={() => setShouldCrash(true)}
            style={{
              backgroundColor: '#450A0A',
              border: '1px solid #EF4444',
              color: '#EF4444',
              padding: '6px 12px',
              fontSize: '10px',
              fontWeight: 'bold',
              fontFamily: 'JetBrains Mono, monospace',
              cursor: 'pointer'
            }}
          >
            [TEST CRASH BOUNDARY]
          </button>
        </div>
      </div>

      {/* Main Terminal Area */}
      <div style={{
        backgroundColor: '#0B0F19',
        border: '1px solid #1E293B',
        padding: '16px',
        display: 'flex',
        flexDirection: 'column',
        gap: '14px'
      }}>
        <div style={{ color: '#10B981', fontWeight: 'bold', fontSize: '12px' }}>
          [TERMINAL_ONLINE] // CONTROL ROOM READY
        </div>
        <div style={{ color: '#94A3B8', fontSize: '11px', lineHeight: '1.5' }}>
          Real-time circuit breaker metrics, latency P95/P99 distributions, and FactChecker audit records are streaming continuously.
          Use the [SRE DASHBOARD] above to inspect the sub-view or [TEST CRASH BOUNDARY] to verify the Global Error Boundary fallback.
        </div>

        {/* Real-time D3.js Merkle Audit Chain Visualizer */}
        <MerkleChainVisualizer />
      </div>
    </div>
  );
};

export default function App() {
  return (
    <GlobalErrorBoundary>
      <MainAppContent />
    </GlobalErrorBoundary>
  );
}
