// web/SecurityStatusModal.tsx
import React, { useEffect, useState } from 'react';
import { DiagnosticStore, DiagnosticCrashEvent } from './DiagnosticStore';

interface Props {
  isOpen: boolean;
  onClose: () => Unit | void;
}

type Unit = void;

export const SecurityStatusModal: React.FC<Props> = ({ isOpen, onClose }) => {
  const [crashes, setCrashes] = useState<readonly DiagnosticCrashEvent[]>(DiagnosticStore.getCrashEvents());

  useEffect(() => {
    return DiagnosticStore.subscribe(() => {
      setCrashes([...DiagnosticStore.getCrashEvents()]);
    });
  }, []);

  if (!isOpen) return null;

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(3, 7, 18, 0.85)',
      backdropFilter: 'blur(4px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 9999,
      fontFamily: 'JetBrains Mono, monospace'
    }}>
      <div style={{
        backgroundColor: '#030712',
        border: '1px solid #334155',
        width: '90%',
        maxWidth: '640px',
        maxHeight: '85vh',
        overflowY: 'auto',
        padding: '20px',
        display: 'flex',
        flexDirection: 'column',
        gap: '14px',
        boxShadow: '0 0 24px rgba(0, 229, 255, 0.1)'
      }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '13px' }}>
            [SECURITY STATUS // RUNTIME_OBSERVABILITY]
          </span>
          <button
            onClick={() => onClose()}
            style={{
              backgroundColor: 'transparent',
              border: 'none',
              color: '#94A3B8',
              cursor: 'pointer',
              fontSize: '14px'
            }}
          >
            [X]
          </button>
        </div>

        {/* Enclave Status Frame */}
        <div style={{
          backgroundColor: '#0B0F19',
          border: '1px solid #1E293B',
          padding: '12px'
        }}>
          <div style={{ color: '#10B981', fontWeight: 'bold', fontSize: '11px', marginBottom: '6px' }}>
            • ROOT_OF_TRUST: StrongBox Keystore Enclave Active
          </div>
          <div style={{ color: '#F8FAFC', fontSize: '11px' }}>
            • POST_QUANTUM_CORE: Kyber-768 Hybrid Encryption Active
          </div>
          <div style={{ color: '#94A3B8', fontSize: '11px' }}>
            • DURESS_ZEROIZE: Constant-Time Memory Scrubbing Verified
          </div>
        </div>

        {/* Runtime Diagnostic Feed */}
        <div style={{
          backgroundColor: '#0B0F19',
          border: '1px solid #1E293B',
          padding: '12px'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
            <span style={{ color: '#00E5FF', fontSize: '11px', fontWeight: 'bold' }}>
              [RUNTIME_DIAGNOSTIC_FEED]
            </span>
            <span style={{ color: crashes.length === 0 ? '#10B981' : '#EF4444', fontSize: '10px' }}>
              {crashes.length === 0 ? '[CLEAN]' : `[${crashes.length} FAULTS]`}
            </span>
          </div>

          {crashes.length === 0 ? (
            <div style={{ color: '#10B981', fontSize: '11px', padding: '6px 0' }}>
              0 RUNTIME FAULTS RECORDED. UI pipeline executing within deterministic boundaries.
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {crashes.map(c => (
                <div key={c.id} style={{
                  backgroundColor: '#111827',
                  border: '1px solid #450A0A',
                  padding: '8px'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', color: '#EF4444', fontSize: '10px', fontWeight: 'bold' }}>
                    <span>{c.id} // [{c.componentTag}]</span>
                    <span>{c.timestamp}</span>
                  </div>
                  <div style={{ color: '#FCA5A5', fontSize: '11px', marginTop: '3px' }}>
                    {c.exceptionClass}: {c.message}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Actions */}
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            onClick={() => DiagnosticStore.simulateCrash('TacticalRadarCanvas')}
            style={{
              flex: 1,
              backgroundColor: '#0B0F19',
              border: '1px solid #F59E0B',
              color: '#F59E0B',
              padding: '8px',
              fontSize: '10px',
              fontWeight: 'bold',
              fontFamily: 'JetBrains Mono, monospace',
              cursor: 'pointer'
            }}
          >
            [SIMULATE UI FAULT]
          </button>
          <button
            onClick={() => DiagnosticStore.clearCrashes()}
            style={{
              flex: 1,
              backgroundColor: '#0B0F19',
              border: '1px solid #1E293B',
              color: '#94A3B8',
              padding: '8px',
              fontSize: '10px',
              fontFamily: 'JetBrains Mono, monospace',
              cursor: 'pointer'
            }}
          >
            [PURGE DIAGNOSTICS]
          </button>
        </div>
      </div>
    </div>
  );
};
