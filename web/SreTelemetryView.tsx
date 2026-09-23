// web/SreTelemetryView.tsx
import React, { useEffect, useState } from 'react';
import {
  DiagnosticStore,
  CircuitBreakerMetric,
  FactCheckerEvent,
  FactCheckerAction,
  DiagnosticLogEvent,
  DiagnosticSeverity
} from './DiagnosticStore';

export const SreTelemetryView: React.FC = () => {
  const [circuitBreakers, setCircuitBreakers] = useState<readonly CircuitBreakerMetric[]>(
    DiagnosticStore.getCircuitBreakers()
  );
  const [factLogs, setFactLogs] = useState<readonly FactCheckerEvent[]>(
    DiagnosticStore.getFactCheckerTrail()
  );
  const [logEvents, setLogEvents] = useState<readonly DiagnosticLogEvent[]>(
    DiagnosticStore.getLogEvents()
  );
  const [selectedFilter, setSelectedFilter] = useState<'ALL' | 'INFO' | 'WARN' | 'CRITICAL'>('ALL');

  useEffect(() => {
    return DiagnosticStore.subscribe(() => {
      setCircuitBreakers([...DiagnosticStore.getCircuitBreakers()]);
      setFactLogs([...DiagnosticStore.getFactCheckerTrail()]);
      setLogEvents([...DiagnosticStore.getLogEvents()]);
    });
  }, []);

  const handleInjectFactEvent = () => {
    const now = new Date().toISOString().substring(11, 19);
    DiagnosticStore.recordFactCheck({
      id: `fact-auto-${Math.floor(Math.random() * 900 + 100)}`,
      timestamp: now,
      promptVector: 'Autonomous CVE Analysis Injection',
      flaggedReason: 'Unverified exploit CVE-2026-9912 flagged by deterministic MITRE filter',
      action: 'HALLUCINATION_BLOCKED',
      interceptedPayloadSnippet: '[AI_UNVERIFIED] Hallucinated mitigation replaced with static memory zeroize routine'
    });
  };

  const getStatusColor = (status: string) => {
    if (status === 'CLOSED') return '#10B981';
    if (status === 'HALF_OPEN') return '#F59E0B';
    return '#EF4444';
  };

  const getStatusBg = (status: string) => {
    if (status === 'CLOSED') return '#064E3B';
    if (status === 'HALF_OPEN') return '#78350F';
    return '#450A0A';
  };

  const getSeverityColor = (severity: DiagnosticSeverity) => {
    if (severity === 'INFO') return '#10B981';
    if (severity === 'WARN') return '#F59E0B';
    return '#EF4444';
  };

  const getSeverityBg = (severity: DiagnosticSeverity) => {
    if (severity === 'INFO') return '#064E3B';
    if (severity === 'WARN') return '#78350F';
    return '#450A0A';
  };

  const filteredLogs = selectedFilter === 'ALL'
    ? logEvents
    : logEvents.filter(e => e.severity === selectedFilter);

  const filterOptions: Array<'ALL' | 'INFO' | 'WARN' | 'CRITICAL'> = ['ALL', 'INFO', 'WARN', 'CRITICAL'];

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      gap: '16px',
      fontFamily: 'JetBrains Mono, monospace',
      color: '#F8FAFC'
    }}>
      {/* 1. Diagnostic Store Real-Time Log Table & Horizontal Severity Toggle Bar */}
      <div style={{
        backgroundColor: '#0B0F19',
        border: '1px solid #1E293B',
        padding: '14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '12px' }}>
            [SRE-LOG-01] // DIAGNOSTIC_STORE_TELEMETRY_STREAM
          </span>
          <span style={{ color: '#64748B', fontSize: '10px' }}>
            {logEvents.length} TOTAL LOGS ({filteredLogs.length} FILTERED)
          </span>
        </div>

        {/* Horizontal Toggle Button Bar for ['ALL', 'INFO', 'WARN', 'CRITICAL'] */}
        <div
          data-testid="severity-toggle-bar"
          style={{
            display: 'flex',
            gap: '8px',
            backgroundColor: '#030712',
            padding: '6px',
            border: '1px solid #1E293B'
          }}
        >
          {filterOptions.map(severity => {
            const isSelected = selectedFilter === severity;
            const count = severity === 'ALL'
              ? logEvents.length
              : logEvents.filter(e => e.severity === severity).length;

            const activeColor =
              severity === 'CRITICAL' ? '#EF4444' :
              severity === 'WARN' ? '#F59E0B' :
              severity === 'INFO' ? '#10B981' : '#00E5FF';

            const activeBg =
              severity === 'CRITICAL' ? '#450A0A' :
              severity === 'WARN' ? '#78350F' :
              severity === 'INFO' ? '#064E3B' : '#083344';

            return (
              <button
                key={severity}
                onClick={() => setSelectedFilter(severity)}
                data-testid={`filter-toggle-${severity.toLowerCase()}`}
                style={{
                  flex: 1,
                  backgroundColor: isSelected ? activeBg : '#0B0F19',
                  color: isSelected ? activeColor : '#64748B',
                  border: `1.5px solid ${isSelected ? activeColor : '#1E293B'}`,
                  padding: '7px 10px',
                  fontSize: '10px',
                  fontFamily: 'JetBrains Mono, monospace',
                  fontWeight: isSelected ? 'bold' : 'normal',
                  cursor: 'pointer',
                  transition: 'all 0.15s ease'
                }}
              >
                [{severity} ({count})]
              </button>
            );
          })}
        </div>

        {/* Action Controls: Inject Simulation Logs & Purge */}
        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            onClick={() => DiagnosticStore.simulateLog('INFO')}
            style={{
              flex: 1,
              backgroundColor: '#111827',
              border: '1px solid #1E293B',
              color: '#10B981',
              padding: '5px 8px',
              fontSize: '9px',
              fontFamily: 'JetBrains Mono, monospace',
              fontWeight: 'bold',
              cursor: 'pointer'
            }}
          >
            [+INJECT INFO]
          </button>
          <button
            onClick={() => DiagnosticStore.simulateLog('WARN')}
            style={{
              flex: 1,
              backgroundColor: '#111827',
              border: '1px solid #1E293B',
              color: '#F59E0B',
              padding: '5px 8px',
              fontSize: '9px',
              fontFamily: 'JetBrains Mono, monospace',
              fontWeight: 'bold',
              cursor: 'pointer'
            }}
          >
            [+INJECT WARN]
          </button>
          <button
            onClick={() => DiagnosticStore.simulateLog('CRITICAL')}
            style={{
              flex: 1,
              backgroundColor: '#111827',
              border: '1px solid #1E293B',
              color: '#EF4444',
              padding: '5px 8px',
              fontSize: '9px',
              fontFamily: 'JetBrains Mono, monospace',
              fontWeight: 'bold',
              cursor: 'pointer'
            }}
          >
            [+INJECT CRIT]
          </button>
          <button
            onClick={() => DiagnosticStore.clearLogs()}
            style={{
              flex: 1,
              backgroundColor: '#111827',
              border: '1px solid #1E293B',
              color: '#64748B',
              padding: '5px 8px',
              fontSize: '9px',
              fontFamily: 'JetBrains Mono, monospace',
              cursor: 'pointer'
            }}
          >
            [PURGE LOGS]
          </button>
        </div>

        {/* Dynamic Log Event Stream Table */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
          {filteredLogs.length === 0 ? (
            <div style={{
              backgroundColor: '#111827',
              border: '1px dashed #334155',
              padding: '16px',
              textAlign: 'center',
              color: '#64748B',
              fontSize: '11px'
            }}>
              NO LOG EVENTS IN BUFFER FOR SEVERITY FILTER: [{selectedFilter}]
            </div>
          ) : (
            filteredLogs.slice(0, 10).map(log => {
              const badgeColor = getSeverityColor(log.severity);
              const badgeBg = getSeverityBg(log.severity);

              return (
                <div key={log.id} style={{
                  backgroundColor: '#111827',
                  border: '1px solid #1E293B',
                  padding: '8px 10px',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '4px'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                      <span style={{
                        color: badgeColor,
                        backgroundColor: badgeBg,
                        border: `1px solid ${badgeColor}`,
                        padding: '1px 5px',
                        fontSize: '9px',
                        fontWeight: 'bold'
                      }}>
                        [{log.severity}]
                      </span>
                      <span style={{ color: '#F8FAFC', fontSize: '10px', fontWeight: 'bold' }}>
                        [{log.componentTag}]
                      </span>
                    </div>
                    <span style={{ color: '#64748B', fontSize: '9px' }}>
                      {log.timestamp}
                    </span>
                  </div>

                  <div style={{
                    color: log.severity === 'CRITICAL' ? '#EF4444' : '#E2E8F0',
                    fontSize: '10px',
                    lineHeight: '1.4'
                  }}>
                    {log.message}
                  </div>

                  {log.metadata && (
                    <div style={{ color: '#00E5FF', fontSize: '9px' }}>
                      META: {log.metadata}
                    </div>
                  )}
                </div>
              );
            })
          )}
        </div>
      </div>

      {/* 2. Circuit Breaker Telemetry */}
      <div style={{
        backgroundColor: '#0B0F19',
        border: '1px solid #1E293B',
        padding: '14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '10px'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '12px' }}>
            [SRE-CB-01] // RESILIENCE_CIRCUIT_BREAKERS
          </span>
          <span style={{ color: '#64748B', fontSize: '10px' }}>
            4 MONITORED PIPELINES
          </span>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {circuitBreakers.map(cb => (
            <div key={cb.id} style={{
              backgroundColor: '#111827',
              border: '1px solid #1E293B',
              padding: '10px',
              display: 'flex',
              flexDirection: 'column',
              gap: '4px'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <span style={{ color: '#F8FAFC', fontSize: '11px', fontWeight: 'bold' }}>
                  {cb.serviceName}
                </span>
                <span style={{
                  color: getStatusColor(cb.status),
                  backgroundColor: getStatusBg(cb.status),
                  border: `1px solid ${getStatusColor(cb.status)}`,
                  padding: '2px 6px',
                  fontSize: '9px',
                  fontWeight: 'bold'
                }}>
                  [{cb.status}]
                </span>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '10px' }}>
                <span style={{ color: '#00E5FF' }}>
                  LATENCY: P95={cb.latencyP95Ms}ms // P99={cb.latencyP99Ms}ms
                </span>
                <span style={{ color: cb.failureCount > 0 ? '#EF4444' : '#64748B' }}>
                  FAILURES: {cb.failureCount}
                </span>
              </div>

              <div style={{ color: '#94A3B8', fontSize: '10px' }}>
                FALLBACK: {cb.fallbackStrategy}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* 2. Latency P95 / P99 Histogram */}
      <div style={{
        backgroundColor: '#0B0F19',
        border: '1px solid #1E293B',
        padding: '14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '10px'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '12px' }}>
            [SRE-LAT-02] // LATENCY_DISTRIBUTION_HISTOGRAM
          </span>
          <span style={{ color: '#00E5FF', fontSize: '10px' }}>
            SLO: 99.98%
          </span>
        </div>

        {[
          { label: 'EDGE_TFLITE_INFERENCE', p95: 22, p99: 48, max: 400 },
          { label: 'REVENUECAT_SYNC', p95: 88, p99: 164, max: 400 },
          { label: 'GEMINI_2.5_FLASH_PIPELINE', p95: 142, p99: 285, max: 400 },
          { label: 'ONESIGNAL_PUSH_RELAY', p95: 320, p99: 540, max: 800 }
        ].map(item => {
          const pct = Math.min(100, (item.p95 / item.max) * 100);
          const barColor = item.p95 > 250 ? '#EF4444' : '#00E5FF';
          return (
            <div key={item.label} style={{ display: 'flex', flexDirection: 'column', gap: '3px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '10px' }}>
                <span style={{ color: '#F8FAFC' }}>{item.label}</span>
                <span style={{ color: barColor, fontWeight: 'bold' }}>
                  P95: {item.p95}ms | P99: {item.p99}ms
                </span>
              </div>
              <div style={{ backgroundColor: '#1E293B', height: '4px', width: '100%' }}>
                <div style={{ backgroundColor: barColor, height: '100%', width: `${pct}%` }} />
              </div>
            </div>
          );
        })}
      </div>

      {/* 3. Deterministic FactChecker Audit Trail */}
      <div style={{
        backgroundColor: '#0B0F19',
        border: '1px solid #1E293B',
        padding: '14px',
        display: 'flex',
        flexDirection: 'column',
        gap: '10px'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ color: '#10B981', fontWeight: 'bold', fontSize: '12px' }}>
            [FACT-03] // DETERMINISTIC_FACTCHECKER_AUDIT
          </span>
          <button
            onClick={handleInjectFactEvent}
            style={{
              backgroundColor: '#111827',
              border: '1px solid #334155',
              color: '#00E5FF',
              padding: '4px 8px',
              fontSize: '9px',
              fontFamily: 'JetBrains Mono, monospace',
              cursor: 'pointer'
            }}
          >
            [+INJECT TEST EVENT]
          </button>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {factLogs.map(fact => {
            const badgeColor = fact.action === 'VERIFIED' ? '#10B981' : fact.action === 'MASKED_PII' ? '#F59E0B' : '#EF4444';
            const badgeBg = fact.action === 'VERIFIED' ? '#064E3B' : fact.action === 'MASKED_PII' ? '#78350F' : '#450A0A';
            const badgeLabel = fact.action === 'HALLUCINATION_BLOCKED' ? '[AI_UNVERIFIED]' : `[${fact.action}]`;

            return (
              <div key={fact.id} style={{
                backgroundColor: '#111827',
                border: '1px solid #1E293B',
                padding: '8px',
                display: 'flex',
                flexDirection: 'column',
                gap: '2px'
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <span style={{
                      color: badgeColor,
                      backgroundColor: badgeBg,
                      border: `1px solid ${badgeColor}`,
                      padding: '1px 5px',
                      fontSize: '9px',
                      fontWeight: 'bold'
                    }}>
                      {badgeLabel}
                    </span>
                    <span style={{ color: '#F8FAFC', fontSize: '10px', fontWeight: 'bold' }}>
                      {fact.promptVector}
                    </span>
                  </div>
                  <span style={{ color: '#64748B', fontSize: '9px' }}>
                    {fact.timestamp}
                  </span>
                </div>
                <div style={{ color: '#00E5FF', fontSize: '10px' }}>
                  {fact.interceptedPayloadSnippet}
                </div>
                <div style={{ color: '#94A3B8', fontSize: '9px' }}>
                  REASON: {fact.flaggedReason}
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
