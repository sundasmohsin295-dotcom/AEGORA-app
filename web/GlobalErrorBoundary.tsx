// web/GlobalErrorBoundary.tsx
import React, { Component, ErrorInfo, ReactNode } from 'react';
import { DiagnosticStore, DiagnosticCrashEvent } from './DiagnosticStore';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  activeCrash: DiagnosticCrashEvent | null;
}

export class GlobalErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      hasError: false,
      activeCrash: null
    };
  }

  static getDerivedStateFromError(error: Error): State {
    DiagnosticStore.recordCrash(error, 'ReactRootTree');
    const latest = DiagnosticStore.getCrashEvents()[0] || null;
    return {
      hasError: true,
      activeCrash: latest
    };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('[SRE_ERROR_BOUNDARY_TRIPPED]', error, errorInfo);
  }

  handleReboot = () => {
    this.setState({ hasError: false, activeCrash: null });
  };

  render() {
    if (this.state.hasError && this.state.activeCrash) {
      const crash = this.state.activeCrash;
      return (
        <div style={{
          backgroundColor: '#030712',
          color: '#F8FAFC',
          fontFamily: 'JetBrains Mono, monospace',
          minHeight: '100vh',
          padding: '24px',
          display: 'flex',
          flexDirection: 'column',
          gap: '16px'
        }}>
          {/* Header Frame */}
          <div style={{
            backgroundColor: '#0B0F19',
            border: '1px solid #450A0A',
            borderLeft: '4px solid #EF4444',
            padding: '16px',
            clipPath: 'polygon(0 0, calc(100% - 8px) 0, 100% 8px, 100% 100%, 8px 100%, 0 calc(100% - 8px))'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ color: '#EF4444', fontWeight: 'bold', fontSize: '13px' }}>
                [SRE-FATAL] // RUNTIME_RECOVERY_ENGAGED
              </span>
              <span style={{ color: '#64748B', fontSize: '11px' }}>
                HEAP: {crash.heapMemoryUsageMb}MB
              </span>
            </div>
            <div style={{ marginTop: '8px', color: '#FCA5A5', fontSize: '12px' }}>
              FAULT ID: {crash.id} // TIMESTAMP: {crash.timestamp}
            </div>
            <div style={{ marginTop: '4px', color: '#94A3B8', fontSize: '11px' }}>
              ORIGIN: [{crash.componentTag}] // EXCEPTION: {crash.exceptionClass}
            </div>
            <div style={{ marginTop: '4px', color: '#EF4444', fontSize: '12px', fontWeight: 600 }}>
              {crash.message}
            </div>
          </div>

          {/* Stack Frame */}
          <div style={{
            backgroundColor: '#111827',
            border: '1px solid #1E293B',
            padding: '12px',
            borderRadius: '2px'
          }}>
            <div style={{ color: '#64748B', fontSize: '10px', marginBottom: '6px' }}>
              [STACK_TRACE_BUFFER]
            </div>
            <pre style={{
              margin: 0,
              color: '#94A3B8',
              fontSize: '11px',
              whiteSpace: 'pre-wrap',
              lineHeight: '1.4'
            }}>
              {crash.stackTraceSnippet}
            </pre>
          </div>

          {/* Recovery Button */}
          <button
            onClick={this.handleReboot}
            style={{
              backgroundColor: '#EF4444',
              color: '#FFFFFF',
              border: 'none',
              padding: '12px 24px',
              fontFamily: 'JetBrains Mono, monospace',
              fontWeight: 'bold',
              fontSize: '12px',
              cursor: 'pointer',
              letterSpacing: '0.5px'
            }}
          >
            [REBOOT RUNTIME SUBSYSTEM]
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
