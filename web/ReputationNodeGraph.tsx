// web/ReputationNodeGraph.tsx
/**
 * ============================================================================
 * REPUTATION NODE GRAPH (D3.JS FORCE-DIRECTED CENTRALITY VISUALIZER)
 * ============================================================================
 * Renders the top 5 highest-centrality nodes from NeuralGraphMeshEngine
 * as an interactive, cybernetic force-directed graph.
 * Features:
 *  - D3 force simulation (charge, link, center, collision, alphaDecay)
 *  - Drag-and-drop node physics with hover inspection & pin-down
 *  - High-Alert Crimson pulsing for Command-and-Control (C2) nodes
 *  - Visual edge vectors with animated flow and directional markers
 *  - Dynamic HUD widget summary showing centrality, reputation, and in/out degree
 *  - Live reactive updates subscribed to NeuralGraphMeshEngine
 *  - Obsidian Industrial palette (#030712, #0B0F19, #1E293B, #00E5FF, #EF4444)
 */

import React, { useEffect, useRef, useState, useCallback } from 'react';
import * as d3 from 'd3';
import {
  NeuralGraphMeshEngine,
  HighCentralityNodeReportItem,
  TopCentralityWidgetReport,
  NeuralEdge,
  NeuralGraphSnapshot
} from './NeuralGraphMeshEngine';

// Node interface for D3 Simulation
export interface D3ReputationNode extends d3.SimulationNodeDatum {
  id: string;
  label: string;
  type: string;
  category: string;
  centralityScore: number;
  reputationScore: number;
  inDegree: number;
  outDegree: number;
  totalConnections: number;
  weightedTotalDegree: number;
  isC2Node: boolean;
  threatWeight: number;
  rank: number;
}

// Edge interface for D3 Simulation
export interface D3ReputationLink extends d3.SimulationLinkDatum<D3ReputationNode> {
  id: string;
  source: D3ReputationNode | string;
  target: D3ReputationNode | string;
  weight: number;
  vectorType: string;
  bytesTransferred: number;
  anomalousEntropy: number;
}

interface ReputationNodeGraphProps {
  limit?: number;
  height?: number;
  onNodeSelect?: (node: HighCentralityNodeReportItem | null) => void;
}

export const ReputationNodeGraph: React.FC<ReputationNodeGraphProps> = ({
  limit = 5,
  height = 420,
  onNodeSelect
}) => {
  const svgRef = useRef<SVGSVGElement | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);

  const [report, setReport] = useState<TopCentralityWidgetReport>(() =>
    NeuralGraphMeshEngine.getTopCentralityReport(limit)
  );
  const [selectedNode, setSelectedNode] = useState<HighCentralityNodeReportItem | null>(null);
  const [hoveredNode, setHoveredNode] = useState<HighCentralityNodeReportItem | null>(null);
  const [filterCategory, setFilterCategory] = useState<string>('ALL');
  const [simulationState, setSimulationState] = useState<'RUNNING' | 'STABILIZED'>('RUNNING');

  // Sync with NeuralGraphMeshEngine reactive state
  useEffect(() => {
    const unsubscribe = NeuralGraphMeshEngine.subscribe((snapshot: NeuralGraphSnapshot) => {
      const updatedReport = NeuralGraphMeshEngine.getTopCentralityReport(limit);
      setReport(updatedReport);
    });
    return () => unsubscribe();
  }, [limit]);

  // Color helper matching Obsidian Industrial Cybernetic palette
  const getNodeColor = useCallback((node: { isC2Node: boolean; category: string; threatWeight: number }) => {
    if (node.isC2Node || node.category === 'C2_CONTROLLER') {
      return '#EF4444'; // High-Alert Crimson
    }
    if (node.category === 'MALWARE_DROPPER' || node.threatWeight >= 0.8) {
      return '#F59E0B'; // Tactical Amber
    }
    if (node.category === 'EXFILTRATION_SINK' || node.category === 'LATERAL_EXPLOIT') {
      return '#A855F7'; // Electric Purple
    }
    if (node.category === 'RECON_SCANNER') {
      return '#EAB308'; // Warning Yellow
    }
    return '#00E5FF'; // Electric Cyan (Benign Internal / Core Endpoint)
  }, []);

  const getNodeRadius = useCallback((centrality: number, isC2: boolean) => {
    const base = 22 + centrality * 20; // 22px - 42px
    return isC2 ? base + 4 : base;
  }, []);

  // Main D3 Simulation Effect
  useEffect(() => {
    if (!svgRef.current || !containerRef.current) return;

    const svg = d3.select(svgRef.current);
    svg.selectAll('*').remove(); // Clean previous render

    const width = containerRef.current.clientWidth || 760;
    svg.attr('width', width).attr('height', height);

    // Filter nodes based on category if needed
    const topNodes = report.topNodes.filter(n =>
      filterCategory === 'ALL' ? true : n.category === filterCategory
    );

    if (topNodes.length === 0) {
      const gEmpty = svg.append('g').attr('transform', `translate(${width / 2}, ${height / 2})`);
      gEmpty
        .append('text')
        .attr('text-anchor', 'middle')
        .attr('fill', '#64748B')
        .attr('font-size', '12px')
        .attr('font-family', 'JetBrains Mono, monospace')
        .text('[NO IOC VERTICES MATCHING CURRENT FILTER CRITERIA]');
      return;
    }

    // Prepare D3 Node Data
    const nodeMap = new Map<string, D3ReputationNode>();
    const d3Nodes: D3ReputationNode[] = topNodes.map((n, idx) => {
      const d3Node: D3ReputationNode = {
        ...n,
        rank: idx + 1,
        x: width / 2 + (Math.cos((idx / topNodes.length) * 2 * Math.PI) * width) / 3.5,
        y: height / 2 + (Math.sin((idx / topNodes.length) * 2 * Math.PI) * height) / 3.5
      };
      nodeMap.set(n.id, d3Node);
      return d3Node;
    });

    // Extract Relevant Inter-Node Edges
    const topNodeIds = new Set(d3Nodes.map(n => n.id));
    const allEdges = NeuralGraphMeshEngine.getSnapshot().edges;

    const d3Links: D3ReputationLink[] = [];
    for (const edge of allEdges) {
      if (topNodeIds.has(edge.source) && topNodeIds.has(edge.target)) {
        d3Links.push({
          id: edge.id,
          source: edge.source,
          target: edge.target,
          weight: edge.weight,
          vectorType: edge.vectorType,
          bytesTransferred: edge.bytesTransferred,
          anomalousEntropy: edge.anomalousEntropy
        });
      }
    }

    // Definitions & Marker Gradients
    const defs = svg.append('defs');

    // Arrowhead marker for normal vectors
    defs
      .append('marker')
      .attr('id', 'arrowhead-cyan')
      .attr('viewBox', '0 -5 10 10')
      .attr('refX', 28)
      .attr('refY', 0)
      .attr('markerWidth', 6)
      .attr('markerHeight', 6)
      .attr('orient', 'auto')
      .append('path')
      .attr('d', 'M0,-5L10,0L0,5')
      .attr('fill', '#00E5FF');

    // Arrowhead marker for C2 / Critical vectors
    defs
      .append('marker')
      .attr('id', 'arrowhead-crimson')
      .attr('viewBox', '0 -5 10 10')
      .attr('refX', 30)
      .attr('refY', 0)
      .attr('markerWidth', 7)
      .attr('markerHeight', 7)
      .attr('orient', 'auto')
      .append('path')
      .attr('d', 'M0,-5L10,0L0,5')
      .attr('fill', '#EF4444');

    // Glow filter
    const filter = defs
      .append('filter')
      .attr('id', 'c2-glow')
      .attr('x', '-50%')
      .attr('y', '-50%')
      .attr('width', '200%')
      .attr('height', '200%');
    filter.append('feGaussianBlur').attr('stdDeviation', '4').attr('result', 'coloredBlur');
    const feMerge = filter.append('feMerge');
    feMerge.append('feMergeNode').attr('in', 'coloredBlur');
    feMerge.append('feMergeNode').attr('in', 'SourceGraphic');

    // Root Group for Pan / Zoom
    const g = svg.append('g').attr('class', 'main-graph-group');

    // Setup Zoom Behavior
    const zoomBehavior = d3
      .zoom<SVGSVGElement, unknown>()
      .scaleExtent([0.5, 2.5])
      .on('zoom', event => {
        g.attr('transform', event.transform);
      });
    svg.call(zoomBehavior);

    // Force Simulation Setup
    setSimulationState('RUNNING');
    const simulation = d3
      .forceSimulation<D3ReputationNode>(d3Nodes)
      .force(
        'link',
        d3
          .forceLink<D3ReputationNode, D3ReputationLink>(d3Links)
          .id(d => d.id)
          .distance(150)
          .strength(0.6)
      )
      .force('charge', d3.forceManyBody<D3ReputationNode>().strength(-480))
      .force('center', d3.forceCenter(width / 2, height / 2).strength(0.12))
      .force(
        'collision',
        d3
          .forceCollide<D3ReputationNode>()
          .radius(d => getNodeRadius(d.centralityScore, d.isC2Node) + 26)
          .iterations(3)
      )
      .alphaDecay(0.028);

    // Render Links Group
    const linkGroup = g.append('g').attr('class', 'links');
    const linkElements = linkGroup
      .selectAll<SVGLineElement, D3ReputationLink>('line')
      .data(d3Links)
      .enter()
      .append('line')
      .attr('stroke', d => {
        const sourceNode = typeof d.source === 'object' ? d.source : nodeMap.get(d.source);
        const targetNode = typeof d.target === 'object' ? d.target : nodeMap.get(d.target);
        if (sourceNode?.isC2Node || targetNode?.isC2Node) return '#EF4444';
        return '#1E293B';
      })
      .attr('stroke-width', d => Math.min(4, Math.max(1.5, d.weight * 0.6)))
      .attr('stroke-opacity', 0.8)
      .attr('stroke-dasharray', d => (d.anomalousEntropy > 0.8 ? '6,3' : 'none'))
      .attr('marker-end', d => {
        const targetNode = typeof d.target === 'object' ? d.target : nodeMap.get(d.target);
        return targetNode?.isC2Node ? 'url(#arrowhead-crimson)' : 'url(#arrowhead-cyan)';
      });

    // Link Labels Group
    const linkTextGroup = g.append('g').attr('class', 'link-labels');
    const linkLabels = linkTextGroup
      .selectAll<SVGTextElement, D3ReputationLink>('text')
      .data(d3Links)
      .enter()
      .append('text')
      .attr('font-size', '9px')
      .attr('fill', '#64748B')
      .attr('font-family', 'JetBrains Mono, monospace')
      .attr('text-anchor', 'middle')
      .text(d => `${d.vectorType} [${d.weight.toFixed(1)}]`);

    // Render Nodes Group
    const nodeGroup = g.append('g').attr('class', 'nodes');
    const nodeElements = nodeGroup
      .selectAll<SVGGElement, D3ReputationNode>('g')
      .data(d3Nodes, d => d.id)
      .enter()
      .append('g')
      .attr('class', 'node-item')
      .attr('cursor', 'pointer');

    // Drag behavior
    const dragBehavior = d3
      .drag<SVGGElement, D3ReputationNode>()
      .on('start', (event, d) => {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
      })
      .on('drag', (event, d) => {
        d.fx = event.x;
        d.fy = event.y;
      })
      .on('end', (event, d) => {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
      });

    nodeElements.call(dragBehavior);

    // Pulsing Outer Aura for C2 / Critical Nodes
    nodeElements
      .filter(d => d.isC2Node || d.centralityScore >= 0.85)
      .append('circle')
      .attr('r', d => getNodeRadius(d.centralityScore, d.isC2Node) + 12)
      .attr('fill', 'none')
      .attr('stroke', d => (d.isC2Node ? '#EF4444' : '#00E5FF'))
      .attr('stroke-width', 1.5)
      .attr('stroke-dasharray', '3,3')
      .attr('opacity', 0.6)
      .attr('filter', d => (d.isC2Node ? 'url(#c2-glow)' : 'none'));

    // Secondary Outline Halo
    nodeElements
      .append('circle')
      .attr('r', d => getNodeRadius(d.centralityScore, d.isC2Node) + 4)
      .attr('fill', 'none')
      .attr('stroke', d => (d.isC2Node ? '#EF4444' : '#1E293B'))
      .attr('stroke-width', 1.5);

    // Primary Core Circle
    nodeElements
      .append('circle')
      .attr('r', d => getNodeRadius(d.centralityScore, d.isC2Node))
      .attr('fill', d => (d.isC2Node ? '#1F0606' : '#0B0F19'))
      .attr('stroke', d => getNodeColor(d))
      .attr('stroke-width', d => (d.isC2Node ? 2.5 : 1.8));

    // Central Tactical Icon / Label inside Node
    nodeElements
      .append('text')
      .attr('text-anchor', 'middle')
      .attr('dy', '-4px')
      .attr('font-size', '10px')
      .attr('font-weight', 'bold')
      .attr('font-family', 'JetBrains Mono, monospace')
      .attr('fill', d => getNodeColor(d))
      .text(d => `#${d.rank}`);

    // Centrality Sub-score inside Node
    nodeElements
      .append('text')
      .attr('text-anchor', 'middle')
      .attr('dy', '10px')
      .attr('font-size', '8.5px')
      .attr('font-weight', '600')
      .attr('font-family', 'JetBrains Mono, monospace')
      .attr('fill', '#94A3B8')
      .text(d => `PR:${d.centralityScore.toFixed(2)}`);

    // External Node Label (ID / Category)
    nodeElements
      .append('text')
      .attr('text-anchor', 'middle')
      .attr('dy', d => `${getNodeRadius(d.centralityScore, d.isC2Node) + 16}px`)
      .attr('font-size', '9.5px')
      .attr('font-weight', 'bold')
      .attr('font-family', 'JetBrains Mono, monospace')
      .attr('fill', d => (d.isC2Node ? '#FCA5A5' : '#E2E8F0'))
      .text(d => (d.label.length > 20 ? d.label.substring(0, 18) + '...' : d.label));

    // Node Threat Type Tag underneath
    nodeElements
      .append('text')
      .attr('text-anchor', 'middle')
      .attr('dy', d => `${getNodeRadius(d.centralityScore, d.isC2Node) + 27}px`)
      .attr('font-size', '8px')
      .attr('font-family', 'JetBrains Mono, monospace')
      .attr('fill', '#64748B')
      .text(d => `${d.type} // RISK:${d.reputationScore}`);

    // Hover & Selection Interactivity
    nodeElements
      .on('mouseenter', (event, d) => {
        setHoveredNode(d);
        d3.select(event.currentTarget)
          .select('circle:nth-child(3)')
          .transition()
          .duration(150)
          .attr('stroke-width', 3.5)
          .attr('stroke', '#38BDF8');
      })
      .on('mouseleave', (event, d) => {
        setHoveredNode(null);
        d3.select(event.currentTarget)
          .select('circle:nth-child(3)')
          .transition()
          .duration(150)
          .attr('stroke-width', d.isC2Node ? 2.5 : 1.8)
          .attr('stroke', getNodeColor(d));
      })
      .on('click', (event, d) => {
        event.stopPropagation();
        setSelectedNode(d);
        if (onNodeSelect) onNodeSelect(d);
      });

    // Click outside deselects
    svg.on('click', () => {
      setSelectedNode(null);
      if (onNodeSelect) onNodeSelect(null);
    });

    // Simulation Tick Updates
    simulation.on('tick', () => {
      linkElements
        .attr('x1', d => (d.source as D3ReputationNode).x || 0)
        .attr('y1', d => (d.source as D3ReputationNode).y || 0)
        .attr('x2', d => (d.target as D3ReputationNode).x || 0)
        .attr('y2', d => (d.target as D3ReputationNode).y || 0);

      linkLabels
        .attr('x', d => (((d.source as D3ReputationNode).x || 0) + ((d.target as D3ReputationNode).x || 0)) / 2)
        .attr('y', d => (((d.source as D3ReputationNode).y || 0) + ((d.target as D3ReputationNode).y || 0)) / 2 - 4);

      nodeElements.attr('transform', d => `translate(${d.x || 0}, ${d.y || 0})`);
    });

    simulation.on('end', () => {
      setSimulationState('STABILIZED');
    });

    return () => {
      simulation.stop();
    };
  }, [report, height, limit, filterCategory, getNodeColor, getNodeRadius, onNodeSelect]);

  const activeInspectNode = hoveredNode || selectedNode || report.topNodes[0] || null;

  return (
    <div
      ref={containerRef}
      style={{
        backgroundColor: '#030712',
        border: '1px solid #1E293B',
        fontFamily: 'JetBrains Mono, monospace',
        display: 'flex',
        flexDirection: 'column',
        position: 'relative',
        overflow: 'hidden'
      }}
    >
      {/* Visualizer Header Toolbar */}
      <div
        style={{
          backgroundColor: '#0B0F19',
          borderBottom: '1px solid #1E293B',
          padding: '10px 14px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '8px'
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <div
            style={{
              width: '8px',
              height: '8px',
              borderRadius: '50%',
              backgroundColor: report.topNodes.some(n => n.isC2Node) ? '#EF4444' : '#10B981',
              boxShadow: report.topNodes.some(n => n.isC2Node)
                ? '0 0 8px #EF4444'
                : '0 0 8px #10B981'
            }}
          />
          <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '11px', letterSpacing: '0.5px' }}>
            NEURAL REPUTATION GRAPH // TOP 5 EIGENVECTOR CENTRALITY
          </span>
          <span
            style={{
              fontSize: '9px',
              color: '#64748B',
              backgroundColor: '#030712',
              padding: '2px 6px',
              border: '1px solid #1E293B'
            }}
          >
            PAGERANK d=0.85
          </span>
        </div>

        {/* Tactical Controls */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
          {/* Category Filter */}
          <select
            value={filterCategory}
            onChange={e => setFilterCategory(e.target.value)}
            style={{
              backgroundColor: '#030712',
              border: '1px solid #1E293B',
              color: '#94A3B8',
              fontSize: '10px',
              fontFamily: 'JetBrains Mono, monospace',
              padding: '4px 6px',
              cursor: 'pointer'
            }}
          >
            <option value="ALL">ALL CATEGORIES</option>
            <option value="C2_CONTROLLER">C2 CONTROLLERS</option>
            <option value="MALWARE_DROPPER">MALWARE DROPPERS</option>
            <option value="LATERAL_EXPLOIT">LATERAL EXPLOITS</option>
            <option value="EXFILTRATION_SINK">EXFIL SINK</option>
            <option value="BENIGN_INTERNAL">BENIGN NODES</option>
          </select>

          {/* Zero-Day C2 Injection Button */}
          <button
            onClick={() => {
              NeuralGraphMeshEngine.injectZeroDayC2Vector();
              setReport(NeuralGraphMeshEngine.getTopCentralityReport(limit));
            }}
            style={{
              backgroundColor: '#450A0A',
              border: '1px solid #EF4444',
              color: '#EF4444',
              fontSize: '10px',
              fontWeight: 'bold',
              fontFamily: 'JetBrains Mono, monospace',
              padding: '4px 8px',
              cursor: 'pointer'
            }}
            title="Inject simulated zero-day C2 node to test real-time PageRank convergence"
          >
            [+INJECT C2]
          </button>

          {/* Reset Baseline */}
          <button
            onClick={() => {
              NeuralGraphMeshEngine.resetToBaseline();
              setReport(NeuralGraphMeshEngine.getTopCentralityReport(limit));
              setSelectedNode(null);
            }}
            style={{
              backgroundColor: '#030712',
              border: '1px solid #1E293B',
              color: '#64748B',
              fontSize: '10px',
              fontFamily: 'JetBrains Mono, monospace',
              padding: '4px 8px',
              cursor: 'pointer'
            }}
          >
            [RESET]
          </button>
        </div>
      </div>

      {/* Main D3 Graph Canvas Area */}
      <div style={{ position: 'relative', width: '100%', height: `${height}px` }}>
        <svg
          ref={svgRef}
          style={{
            width: '100%',
            height: '100%',
            display: 'block',
            backgroundColor: '#030712'
          }}
        />

        {/* Simulation Physics Status Watermark */}
        <div
          style={{
            position: 'absolute',
            bottom: '8px',
            left: '10px',
            fontSize: '9px',
            color: '#475569',
            pointerEvents: 'none'
          }}
        >
          FORCE_SIMULATION: {simulationState} // NODES EVALUATED: {report.totalEvaluatedNodes} // TOP: {report.topNodes.length}
        </div>

        {/* Live Forensic Telemetry Card (Hover / Selected Node) */}
        {activeInspectNode && (
          <div
            style={{
              position: 'absolute',
              top: '10px',
              right: '10px',
              width: '280px',
              backgroundColor: 'rgba(11, 15, 25, 0.95)',
              border: `1px solid ${activeInspectNode.isC2Node ? '#EF4444' : '#1E293B'}`,
              boxShadow: activeInspectNode.isC2Node ? '0 0 16px rgba(239, 68, 68, 0.25)' : 'none',
              padding: '10px 12px',
              fontSize: '10px',
              lineHeight: '1.4',
              backdropFilter: 'blur(4px)',
              pointerEvents: 'none'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ color: '#94A3B8', fontSize: '9px' }}>INSPECTION TELEMETRY</span>
              <span
                style={{
                  color: activeInspectNode.isC2Node ? '#EF4444' : '#10B981',
                  fontWeight: 'bold',
                  fontSize: '9px'
                }}
              >
                {activeInspectNode.isC2Node ? '[CRITICAL_C2_FLAGGED]' : '[ACTIVE_IOC]'}
              </span>
            </div>

            <div
              style={{
                color: activeInspectNode.isC2Node ? '#FCA5A5' : '#00E5FF',
                fontWeight: 'bold',
                fontSize: '11px',
                marginBottom: '2px',
                wordBreak: 'break-all'
              }}
            >
              {activeInspectNode.label}
            </div>

            <div style={{ color: '#64748B', fontSize: '9px', marginBottom: '8px' }}>
              {activeInspectNode.id}
            </div>

            {/* Metrics Grid */}
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                gap: '6px',
                borderTop: '1px solid #1E293B',
                paddingTop: '6px'
              }}
            >
              <div>
                <div style={{ color: '#64748B', fontSize: '8.5px' }}>CENTRALITY (PR)</div>
                <div style={{ color: '#38BDF8', fontWeight: 'bold', fontSize: '11px' }}>
                  {activeInspectNode.centralityScore.toFixed(3)}
                </div>
              </div>

              <div>
                <div style={{ color: '#64748B', fontSize: '8.5px' }}>REPUTATION RISK</div>
                <div
                  style={{
                    color:
                      activeInspectNode.reputationScore >= 75
                        ? '#EF4444'
                        : activeInspectNode.reputationScore >= 40
                        ? '#F59E0B'
                        : '#10B981',
                    fontWeight: 'bold',
                    fontSize: '11px'
                  }}
                >
                  {activeInspectNode.reputationScore}/100
                </div>
              </div>

              <div>
                <div style={{ color: '#64748B', fontSize: '8.5px' }}>CONNECTIONS (IN/OUT)</div>
                <div style={{ color: '#E2E8F0', fontWeight: 'bold' }}>
                  ↓{activeInspectNode.inDegree} / ↑{activeInspectNode.outDegree} ({activeInspectNode.totalConnections})
                </div>
              </div>

              <div>
                <div style={{ color: '#64748B', fontSize: '8.5px' }}>WEIGHTED VOLUME</div>
                <div style={{ color: '#E2E8F0', fontWeight: 'bold' }}>
                  {activeInspectNode.weightedTotalDegree.toFixed(1)}x
                </div>
              </div>
            </div>

            <div style={{ marginTop: '8px', borderTop: '1px solid #1E293B', paddingTop: '6px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748B', fontSize: '8.5px' }}>
                <span>CATEGORY:</span>
                <span style={{ color: '#94A3B8' }}>{activeInspectNode.category}</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', color: '#64748B', fontSize: '8.5px', marginTop: '2px' }}>
                <span>IOC TYPE:</span>
                <span style={{ color: '#94A3B8' }}>{activeInspectNode.type}</span>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Footer Top-5 Centrality Leaderboard Table */}
      <div
        style={{
          backgroundColor: '#0B0F19',
          borderTop: '1px solid #1E293B',
          padding: '8px 12px',
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))',
          gap: '8px'
        }}
      >
        {report.topNodes.map((n, i) => (
          <div
            key={n.id}
            onClick={() => setSelectedNode(n)}
            style={{
              backgroundColor: selectedNode?.id === n.id ? '#1E293B' : '#030712',
              border: `1px solid ${selectedNode?.id === n.id ? '#00E5FF' : n.isC2Node ? '#7F1D1D' : '#1E293B'}`,
              padding: '6px 8px',
              cursor: 'pointer',
              display: 'flex',
              flexDirection: 'column',
              gap: '2px',
              transition: 'background-color 0.15s ease'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span
                style={{
                  fontSize: '9px',
                  fontWeight: 'bold',
                  color: n.isC2Node ? '#EF4444' : '#00E5FF'
                }}
              >
                #{i + 1} {n.isC2Node ? '⚡C2' : ''}
              </span>
              <span style={{ fontSize: '8.5px', color: '#38BDF8', fontWeight: 'bold' }}>
                PR:{n.centralityScore.toFixed(2)}
              </span>
            </div>

            <div
              style={{
                fontSize: '9px',
                color: '#E2E8F0',
                whiteSpace: 'nowrap',
                overflow: 'hidden',
                textOverflow: 'ellipsis'
              }}
              title={n.label}
            >
              {n.label}
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '8px', color: '#64748B' }}>
              <span>RISK:{n.reputationScore}</span>
              <span>DEG:{n.totalConnections}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ReputationNodeGraph;
