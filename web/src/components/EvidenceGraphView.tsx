import React, { useState } from 'react';
import {
  User,
  LogIn,
  Laptop,
  Globe,
  FileCode2,
  AlertOctagon,
  HelpCircle,
  CheckCircle2,
  AlertTriangle,
  ArrowRight,
  Info,
  ShieldAlert
} from 'lucide-react';
import {
  EvidenceNode,
  EvidenceRelationship,
  EpistemicStatus,
  CyberRealityScenario
} from '../core/cyberReality/CyberRealityEngine';

interface EvidenceGraphViewProps {
  scenario: CyberRealityScenario;
  selectedEvidenceIds: string[];
  onToggleEvidence: (id: string) => void;
  onNodeInspected?: (node: EvidenceNode) => void;
}

export const EvidenceGraphView: React.FC<EvidenceGraphViewProps> = ({
  scenario,
  selectedEvidenceIds,
  onToggleEvidence,
  onNodeInspected
}) => {
  const [activeNodeId, setActiveNodeId] = useState<string>('node_user');
  const [filterEpistemic, setFilterEpistemic] = useState<EpistemicStatus | 'ALL'>('ALL');

  const activeNode = scenario.nodes.find(n => n.id === activeNodeId) || scenario.nodes[0];

  const handleSelectNode = (node: EvidenceNode) => {
    setActiveNodeId(node.id);
    if (onNodeInspected) {
      onNodeInspected(node);
    }
  };

  const getEpistemicBadgeStyle = (status: EpistemicStatus) => {
    switch (status) {
      case 'FACT':
        return {
          bg: 'rgba(59, 130, 246, 0.15)',
          color: 'var(--accent-indigo)',
          border: '1px solid rgba(99, 102, 241, 0.4)'
        };
      case 'EVIDENCE':
        return {
          bg: 'rgba(6, 182, 212, 0.15)',
          color: 'var(--accent-cyan)',
          border: '1px solid rgba(6, 182, 212, 0.4)'
        };
      case 'INFERENCE':
        return {
          bg: 'rgba(16, 185, 129, 0.15)',
          color: 'var(--accent-emerald)',
          border: '1px solid rgba(16, 185, 129, 0.4)'
        };
      case 'ASSUMPTION':
        return {
          bg: 'rgba(239, 68, 68, 0.15)',
          color: 'var(--accent-rose)',
          border: '1px solid rgba(239, 68, 68, 0.4)'
        };
      case 'UNKNOWN':
        return {
          bg: 'rgba(245, 158, 11, 0.15)',
          color: 'var(--accent-amber)',
          border: '1px solid rgba(245, 158, 11, 0.4)'
        };
    }
  };

  const getNodeIcon = (node: EvidenceNode) => {
    switch (node.type) {
      case 'USER':
        return <User size={16} color="var(--accent-indigo)" />;
      case 'LOGIN':
        return <LogIn size={16} color="var(--accent-cyan)" />;
      case 'DEVICE':
        return <Laptop size={16} color="var(--accent-cyan)" />;
      case 'IP':
        return <Globe size={16} color="var(--accent-cyan)" />;
      case 'AUTH_EVENT':
        return <FileCode2 size={16} color="var(--accent-amber)" />;
      case 'CLAIM':
        return <AlertOctagon size={16} color="var(--accent-rose)" />;
      case 'INFERENCE':
        return <AlertTriangle size={16} color="var(--accent-emerald)" />;
      default:
        return <Info size={16} color="var(--text-muted)" />;
    }
  };

  // Group nodes hierarchically
  const userNodes = scenario.nodes.filter(n => n.type === 'USER');
  const loginNodes = scenario.nodes.filter(n => n.type === 'LOGIN');
  const networkAndHostNodes = scenario.nodes.filter(
    n => n.type === 'IP' || n.type === 'DEVICE' || n.type === 'CLAIM'
  );
  const authEventNodes = scenario.nodes.filter(n => n.type === 'AUTH_EVENT');
  const deductionNodes = scenario.nodes.filter(n => n.type === 'INFERENCE');

  const filteredNodes =
    filterEpistemic === 'ALL'
      ? scenario.nodes
      : scenario.nodes.filter(n => n.epistemicStatus === filterEpistemic);

  const connectedRelationships = scenario.relationships.filter(
    r => r.sourceNodeId === activeNodeId || r.targetNodeId === activeNodeId
  );

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
      {/* Header & Epistemic Filter */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '12px',
          borderBottom: '1px solid var(--border-subtle)',
          paddingBottom: '14px'
        }}
      >
        <div>
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
              VISUAL INVESTIGATION MODEL
            </span>
            <span style={{ fontSize: '12px', color: 'var(--text-muted)' }}>
              Non-authoritative forensic relationship view
            </span>
          </div>
          <h3 style={{ fontSize: '15px', fontWeight: 800, marginTop: '4px' }}>
            Evidence Graph: User → Login → Device / IP → Auth Event
          </h3>
        </div>

        {/* Epistemic Filters */}
        <div style={{ display: 'flex', gap: '4px', flexWrap: 'wrap' }}>
          {(['ALL', 'FACT', 'EVIDENCE', 'INFERENCE', 'ASSUMPTION', 'UNKNOWN'] as const).map(
            filter => (
              <button
                key={filter}
                onClick={() => setFilterEpistemic(filter)}
                style={{
                  fontSize: '10px',
                  fontFamily: 'var(--font-mono)',
                  fontWeight: 700,
                  padding: '3px 8px',
                  borderRadius: 'var(--radius-sm)',
                  border:
                    filterEpistemic === filter
                      ? '1px solid var(--accent-cyan)'
                      : '1px solid var(--border-subtle)',
                  backgroundColor:
                    filterEpistemic === filter ? 'var(--accent-cyan-subtle)' : 'var(--bg-tertiary)',
                  color:
                    filterEpistemic === filter ? 'var(--accent-cyan)' : 'var(--text-secondary)',
                  cursor: 'pointer'
                }}
              >
                {filter}
              </button>
            )
          )}
        </div>
      </div>

      {/* 2-Column: Hierarchical Graph Explorer (Left) | Node Inspector (Right) */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))',
          gap: '16px'
        }}
      >
        {/* GRAPH COLUMN */}
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            gap: '12px',
            backgroundColor: 'var(--bg-primary)',
            padding: '16px',
            borderRadius: 'var(--radius-sm)',
            border: '1px solid var(--border-subtle)',
            maxHeight: '480px',
            overflowY: 'auto'
          }}
        >
          {/* Level 1: USER */}
          <div>
            <div
              style={{
                fontSize: '10px',
                fontFamily: 'var(--font-mono)',
                color: 'var(--text-muted)',
                fontWeight: 700,
                marginBottom: '6px'
              }}
            >
              1. IDENTITY LAYER (USER)
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {userNodes
                .filter(n => filteredNodes.includes(n))
                .map(node => (
                  <NodeCard
                    key={node.id}
                    node={node}
                    isSelected={activeNodeId === node.id}
                    onClick={() => handleSelectNode(node)}
                    icon={getNodeIcon(node)}
                    badgeStyle={getEpistemicBadgeStyle(node.epistemicStatus)}
                  />
                ))}
            </div>
          </div>

          <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '11px' }}>↓</div>

          {/* Level 2: LOGINS */}
          <div>
            <div
              style={{
                fontSize: '10px',
                fontFamily: 'var(--font-mono)',
                color: 'var(--text-muted)',
                fontWeight: 700,
                marginBottom: '6px'
              }}
            >
              2. AUTHENTICATION SESSION LAYER
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {loginNodes
                .filter(n => filteredNodes.includes(n))
                .map(node => (
                  <NodeCard
                    key={node.id}
                    node={node}
                    isSelected={activeNodeId === node.id}
                    onClick={() => handleSelectNode(node)}
                    icon={getNodeIcon(node)}
                    badgeStyle={getEpistemicBadgeStyle(node.epistemicStatus)}
                  />
                ))}
            </div>
          </div>

          <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '11px' }}>↓</div>

          {/* Level 3: NETWORK & HOSTS */}
          <div>
            <div
              style={{
                fontSize: '10px',
                fontFamily: 'var(--font-mono)',
                color: 'var(--text-muted)',
                fontWeight: 700,
                marginBottom: '6px'
              }}
            >
              3. NETWORK & ENDPOINT LAYER (IPs & CLAIMS)
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {networkAndHostNodes
                .filter(n => filteredNodes.includes(n))
                .map(node => (
                  <NodeCard
                    key={node.id}
                    node={node}
                    isSelected={activeNodeId === node.id}
                    onClick={() => handleSelectNode(node)}
                    icon={getNodeIcon(node)}
                    badgeStyle={getEpistemicBadgeStyle(node.epistemicStatus)}
                  />
                ))}
            </div>
          </div>

          <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '11px' }}>↓</div>

          {/* Level 4: AUTH EVENTS */}
          <div>
            <div
              style={{
                fontSize: '10px',
                fontFamily: 'var(--font-mono)',
                color: 'var(--text-muted)',
                fontWeight: 700,
                marginBottom: '6px'
              }}
            >
              4. RAW AUDIT LOG LAYER (WINDOWS EVENT IDs)
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {authEventNodes
                .filter(n => filteredNodes.includes(n))
                .map(node => (
                  <NodeCard
                    key={node.id}
                    node={node}
                    isSelected={activeNodeId === node.id}
                    onClick={() => handleSelectNode(node)}
                    icon={getNodeIcon(node)}
                    badgeStyle={getEpistemicBadgeStyle(node.epistemicStatus)}
                  />
                ))}
            </div>
          </div>

          <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '11px' }}>↓</div>

          {/* Level 5: INFERENCES & DEDUCTIONS */}
          <div>
            <div
              style={{
                fontSize: '10px',
                fontFamily: 'var(--font-mono)',
                color: 'var(--text-muted)',
                fontWeight: 700,
                marginBottom: '6px'
              }}
            >
              5. REASONING & CORRELATION LAYER
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              {deductionNodes
                .filter(n => filteredNodes.includes(n))
                .map(node => (
                  <NodeCard
                    key={node.id}
                    node={node}
                    isSelected={activeNodeId === node.id}
                    onClick={() => handleSelectNode(node)}
                    icon={getNodeIcon(node)}
                    badgeStyle={getEpistemicBadgeStyle(node.epistemicStatus)}
                  />
                ))}
            </div>
          </div>
        </div>

        {/* NODE DETAIL INSPECTOR */}
        <div
          style={{
            backgroundColor: 'var(--bg-tertiary)',
            padding: '16px',
            borderRadius: 'var(--radius-sm)',
            border: '1px solid var(--border-subtle)',
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}
        >
          <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
              <div>
                <span
                  style={{
                    fontSize: '10px',
                    fontFamily: 'var(--font-mono)',
                    fontWeight: 700,
                    padding: '2px 6px',
                    borderRadius: 'var(--radius-sm)',
                    ...getEpistemicBadgeStyle(activeNode.epistemicStatus)
                  }}
                >
                  {activeNode.epistemicStatus}
                </span>
                <h4 style={{ fontSize: '16px', fontWeight: 800, marginTop: '8px' }}>
                  {activeNode.label}
                </h4>
                <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                  {activeNode.subLabel}
                </div>
              </div>

              <div
                style={{
                  padding: '6px 8px',
                  borderRadius: 'var(--radius-sm)',
                  backgroundColor: activeNode.isVerifiedByTelemetry
                    ? 'rgba(16, 185, 129, 0.12)'
                    : 'rgba(239, 68, 68, 0.12)',
                  border: activeNode.isVerifiedByTelemetry
                    ? '1px solid var(--accent-emerald)'
                    : '1px solid var(--accent-rose)',
                  fontSize: '10px',
                  fontFamily: 'var(--font-mono)',
                  fontWeight: 700,
                  color: activeNode.isVerifiedByTelemetry
                    ? 'var(--accent-emerald)'
                    : 'var(--accent-rose)'
                }}
              >
                {activeNode.isVerifiedByTelemetry ? 'TELEMETRY VERIFIED ✓' : 'UNVERIFIED CLAIM ✗'}
              </div>
            </div>

            {/* Description / Detail */}
            <div
              style={{
                marginTop: '14px',
                padding: '12px',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--bg-primary)',
                border: '1px solid var(--border-subtle)',
                fontSize: '12px',
                lineHeight: 1.5,
                color: 'var(--text-primary)'
              }}
            >
              {activeNode.details}
            </div>

            {/* Corroborating Telemetry Events */}
            <div style={{ marginTop: '14px' }}>
              <div
                style={{
                  fontSize: '11px',
                  fontFamily: 'var(--font-mono)',
                  fontWeight: 700,
                  color: 'var(--accent-cyan)',
                  marginBottom: '6px'
                }}
              >
                CORROBORATING TELEMETRY EVENTS:
              </div>
              {activeNode.corroboratingEventIds.length > 0 ? (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
                  {activeNode.corroboratingEventIds.map(eventId => {
                    const isSelected = selectedEvidenceIds.includes(eventId);
                    return (
                      <div
                        key={eventId}
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'space-between',
                          padding: '6px 10px',
                          borderRadius: 'var(--radius-sm)',
                          backgroundColor: isSelected ? 'var(--accent-cyan-subtle)' : 'var(--bg-primary)',
                          border: isSelected ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
                          fontSize: '11px'
                        }}
                      >
                        <span style={{ fontFamily: 'var(--font-mono)', fontWeight: 700 }}>
                          {eventId}
                        </span>
                        <button
                          onClick={() => onToggleEvidence(eventId)}
                          style={{
                            padding: '3px 8px',
                            borderRadius: '2px',
                            border: 'none',
                            backgroundColor: isSelected ? 'var(--accent-cyan)' : 'var(--bg-tertiary)',
                            color: isSelected ? '#000' : 'var(--text-secondary)',
                            fontSize: '10px',
                            fontWeight: 700,
                            cursor: 'pointer'
                          }}
                        >
                          {isSelected ? 'SELECTED FOR AUDIT ✓' : '+ ADD TO AUDIT'}
                        </button>
                      </div>
                    );
                  })}
                </div>
              ) : (
                <div
                  style={{
                    padding: '8px 10px',
                    borderRadius: 'var(--radius-sm)',
                    backgroundColor: 'rgba(239, 68, 68, 0.08)',
                    border: '1px dashed var(--accent-rose)',
                    fontSize: '11px',
                    color: 'var(--accent-rose)'
                  }}
                >
                  Zero corroborating event logs found in system truth. This item is an unverified assumption.
                </div>
              )}
            </div>

            {/* Evidence Relationships List */}
            <div style={{ marginTop: '14px' }}>
              <div
                style={{
                  fontSize: '11px',
                  fontFamily: 'var(--font-mono)',
                  fontWeight: 700,
                  color: 'var(--text-muted)',
                  marginBottom: '6px'
                }}
              >
                CONNECTED RELATIONSHIPS ({connectedRelationships.length}):
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                {connectedRelationships.map(rel => (
                  <div
                    key={rel.id}
                    style={{
                      fontSize: '11px',
                      padding: '4px 8px',
                      borderRadius: 'var(--radius-sm)',
                      backgroundColor: 'var(--bg-primary)',
                      border: '1px solid var(--border-subtle)',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '6px'
                    }}
                  >
                    <ArrowRight size={12} color="var(--accent-cyan)" />
                    <span style={{ fontWeight: 600, color: 'var(--text-secondary)' }}>
                      {rel.label}
                    </span>
                    <span
                      style={{
                        marginLeft: 'auto',
                        fontSize: '9px',
                        fontFamily: 'var(--font-mono)',
                        color: rel.isVerifiedByTelemetry ? 'var(--accent-emerald)' : 'var(--accent-rose)'
                      }}
                    >
                      {rel.isVerifiedByTelemetry ? 'VERIFIED' : 'UNVERIFIED'}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

interface NodeCardProps {
  node: EvidenceNode;
  isSelected: boolean;
  onClick: () => void;
  icon: React.ReactNode;
  badgeStyle: { bg: string; color: string; border: string };
}

const NodeCard: React.FC<NodeCardProps> = ({
  node,
  isSelected,
  onClick,
  icon,
  badgeStyle
}) => (
  <div
    onClick={onClick}
    style={{
      padding: '8px 12px',
      borderRadius: 'var(--radius-sm)',
      backgroundColor: isSelected ? 'var(--bg-tertiary)' : 'var(--bg-secondary)',
      border: isSelected ? '1px solid var(--accent-cyan)' : '1px solid var(--border-subtle)',
      cursor: 'pointer',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      gap: '8px',
      transition: 'all 0.15s ease'
    }}
  >
    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
      {icon}
      <div>
        <div style={{ fontSize: '12px', fontWeight: 700, color: 'var(--text-primary)' }}>
          {node.label}
        </div>
        <div style={{ fontSize: '10px', color: 'var(--text-secondary)' }}>
          {node.subLabel}
        </div>
      </div>
    </div>

    <span
      style={{
        fontSize: '9px',
        fontFamily: 'var(--font-mono)',
        fontWeight: 800,
        padding: '2px 5px',
        borderRadius: '2px',
        ...badgeStyle
      }}
    >
      {node.epistemicStatus}
    </span>
  </div>
);
