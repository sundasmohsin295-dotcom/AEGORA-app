import React, { useState, useEffect } from 'react';
import { GlobalErrorBoundary } from './GlobalErrorBoundary';
import { SettingsDashboard } from './SettingsDashboard';
import { SecurityStatusModal } from './SecurityStatusModal';
import { DiagnosticStore } from './DiagnosticStore';
import { MerkleChainVisualizer } from './MerkleChainVisualizer';
import { ReputationNodeGraph } from './ReputationNodeGraph';
import { StrixPentestTelemetry } from './StrixPentestTelemetry';

export const MainAppContent: React.FC = () => {
  const [currentView, setCurrentView] = useState<'ARENA' | 'SETTINGS'>('ARENA');
  const [isSecurityModalOpen, setSecurityModalOpen] = useState(false);
  const [shouldCrash, setShouldCrash] = useState(false);
  const [, setTick] = useState(0);

  useEffect(() => {
    return DiagnosticStore.subscribe(() => {
      setTick(t => t + 1);
    });
  }, []);

  const logEvents = DiagnosticStore.getLogEvents();
  const crashEvents = DiagnosticStore.getCrashEvents();
  const circuitBreakers = DiagnosticStore.getCircuitBreakers();

  const criticalThreatCount = logEvents.filter(e => e.severity === 'CRITICAL').length +
    crashEvents.length +
    circuitBreakers.filter(cb => cb.status === 'OPEN').length;

  const warnThreatCount = logEvents.filter(e => e.severity === 'WARN').length +
    circuitBreakers.filter(cb => cb.status === 'HALF_OPEN').length;

  const totalActiveAlerts = criticalThreatCount + warnThreatCount;

  const badgeColor = criticalThreatCount > 0 ? '#EF4444' : warnThreatCount > 0 ? '#F59E0B' : '#10B981';
  const badgeBg = criticalThreatCount > 0 ? 'rgba(69, 10, 10, 0.7)' : warnThreatCount > 0 ? 'rgba(69, 26, 3, 0.7)' : 'rgba(6, 78, 59, 0.5)';

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
      <style>{`
        @keyframes headerThreatPulse {
          0% { opacity: 0.5; box-shadow: 0 0 4px rgba(0,0,0,0.5); }
          50% { opacity: 1; box-shadow: 0 0 10px ${badgeColor}66; }
          100% { opacity: 0.5; box-shadow: 0 0 4px rgba(0,0,0,0.5); }
        }
      `}</style>
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
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
            <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '18px', letterSpacing: '1px' }}>
              CYBER DUEL ARENA
            </span>
            <div style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '6px',
              backgroundColor: badgeBg,
              border: `1px solid ${badgeColor}`,
              borderRadius: '4px',
              padding: '3px 8px',
              fontSize: '10px',
              fontWeight: 'bold',
              color: badgeColor,
              animation: 'headerThreatPulse 1.2s infinite ease-in-out'
            }}>
              <span style={{
                width: '6px',
                height: '6px',
                borderRadius: '50%',
                backgroundColor: badgeColor
              }} />
              <span>ACTIVE ALERTS: {totalActiveAlerts}</span>
              <span style={{ fontSize: '9px', opacity: 0.9 }}>
                {criticalThreatCount > 0 ? `[${criticalThreatCount} CRIT]` : warnThreatCount > 0 ? `[${warnThreatCount} WARN]` : '[SECURE]'}
              </span>
            </div>
          </div>
          <div style={{ color: '#64748B', fontSize: '11px', marginTop: '4px' }}>
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

        {/* Real-time Strix Pentest Telemetry Console */}
        <StrixPentestTelemetry />

        {/* Real-time D3.js Neural Reputation Graph (Top 5 High-Centrality C2 & Attack Graph) */}
        <ReputationNodeGraph />

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
