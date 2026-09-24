// web/MerkleChainVisualizer.tsx
import React, { useEffect, useRef, useState, useMemo } from 'react';
import * as d3 from 'd3';
import { DiagnosticStore, MerkleAuditBlock, GENESIS_HASH, deterministicSha256 } from './DiagnosticStore';

interface MerkleChainVisualizerProps {
  blocks?: readonly MerkleAuditBlock[];
  onBlockSelect?: (block: MerkleAuditBlock | null) => void;
}

interface D3NodeData {
  id: string;
  index: number;
  block?: MerkleAuditBlock;
  isGenesis: boolean;
  x: number;
  y: number;
  severity: 'INFO' | 'WARN' | 'CRITICAL';
  isTampered: boolean;
  hash: string;
  prevHash: string;
  payloadHash: string;
  timestamp: string;
  componentTag: string;
}

interface D3LinkData {
  source: D3NodeData;
  target: D3NodeData;
  isSevered: boolean;
}

export const MerkleChainVisualizer: React.FC<MerkleChainVisualizerProps> = ({
  blocks: externalBlocks,
  onBlockSelect
}) => {
  const svgRef = useRef<SVGSVGElement | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);

  const [blocks, setBlocks] = useState<readonly MerkleAuditBlock[]>(
    externalBlocks || DiagnosticStore.getMerkleLedger()
  );
  const [selectedBlock, setSelectedBlock] = useState<MerkleAuditBlock | null>(null);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [isChainValid, setIsChainValid] = useState<boolean>(DiagnosticStore.isChainValid());
  const [currentRoot, setCurrentRoot] = useState<string>(DiagnosticStore.getCurrentMerkleRoot());
  const [verificationFeedback, setVerificationFeedback] = useState<string | null>(null);
  const [copiedHashKey, setCopiedHashKey] = useState<string | null>(null);

  // Sync state with DiagnosticStore
  useEffect(() => {
    const updateFromStore = () => {
      if (!externalBlocks) {
        const freshBlocks = [...DiagnosticStore.getMerkleLedger()];
        setBlocks(freshBlocks);
        // If inspecting a block, keep its state synchronized
        if (selectedBlock) {
          const updatedSelected = freshBlocks.find(b => b.index === selectedBlock.index);
          if (updatedSelected) {
            setSelectedBlock(updatedSelected);
          }
        }
      }
      setIsChainValid(DiagnosticStore.isChainValid());
      setCurrentRoot(DiagnosticStore.getCurrentMerkleRoot());
    };

    updateFromStore();
    const unsubscribe = DiagnosticStore.subscribe(updateFromStore);
    return () => unsubscribe();
  }, [externalBlocks, selectedBlock]);

  useEffect(() => {
    if (externalBlocks) {
      setBlocks(externalBlocks);
    }
  }, [externalBlocks]);

  // Handle ESC key to dismiss inspection modal
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        setIsModalOpen(false);
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  // Copy hash helper
  const handleCopyHash = (text: string, key: string) => {
    navigator.clipboard.writeText(text);
    setCopiedHashKey(key);
    setTimeout(() => setCopiedHashKey(null), 2000);
  };

  // Recompute verification for selected block in modal
  const modalProofVerification = useMemo(() => {
    if (!selectedBlock) return null;
    if (selectedBlock.index === 0) {
      return {
        isValid: true,
        recomputedPayloadHash: GENESIS_HASH,
        recomputedBlockHash: GENESIS_HASH,
        matches: true
      };
    }

    const recomputedPayloadHash = deterministicSha256(selectedBlock.rawPayload);
    const recomputedBlockHash = deterministicSha256(`${selectedBlock.eventPayloadHash}+${selectedBlock.previousBlockHash}`);
    const matches =
      !selectedBlock.isTampered &&
      recomputedPayloadHash === selectedBlock.eventPayloadHash &&
      recomputedBlockHash === selectedBlock.blockHash;

    return {
      isValid: matches,
      recomputedPayloadHash,
      recomputedBlockHash,
      matches
    };
  }, [selectedBlock]);

  // D3 Rendering
  useEffect(() => {
    if (!svgRef.current || !containerRef.current) return;

    const width = containerRef.current.clientWidth || 900;
    const height = 290;
    const svg = d3.select(svgRef.current);
    svg.selectAll('*').remove();

    // Definitions for markers and filters
    const defs = svg.append('defs');

    // Arrow markers
    defs.append('marker')
      .attr('id', 'arrow-cyan')
      .attr('viewBox', '0 -5 10 10')
      .attr('refX', 8)
      .attr('refY', 0)
      .attr('markerWidth', 6)
      .attr('markerHeight', 6)
      .attr('orient', 'auto')
      .append('path')
      .attr('d', 'M0,-4L8,0L0,4')
      .attr('fill', '#00E5FF');

    defs.append('marker')
      .attr('id', 'arrow-crimson')
      .attr('viewBox', '0 -5 10 10')
      .attr('refX', 8)
      .attr('refY', 0)
      .attr('markerWidth', 6)
      .attr('markerHeight', 6)
      .attr('orient', 'auto')
      .append('path')
      .attr('d', 'M0,-4L8,0L0,4')
      .attr('fill', '#EF4444');

    // Zoom container
    const g = svg.append('g').attr('class', 'merkle-zoom-container');

    const zoom = d3.zoom<SVGSVGElement, unknown>()
      .scaleExtent([0.4, 2.5])
      .on('zoom', (event) => {
        g.attr('transform', event.transform);
      });

    svg.call(zoom);

    // Build Nodes & Links
    const nodeWidth = 186;
    const nodeHeight = 88;
    const horizontalSpacing = 250;
    const startX = 60;
    const startY = height / 2 - nodeHeight / 2;

    // Genesis Block Node (#00)
    const genesisBlock: MerkleAuditBlock = {
      index: 0,
      timestamp: '00:00:00.000',
      eventId: 'GENESIS-00',
      rawPayload: JSON.stringify({
        anchor: 'GENESIS_STATE_ROOT',
        stateRoot: GENESIS_HASH,
        status: 'IMMUTABLE_CONSENSUS_ORIGIN'
      }, null, 2),
      eventPayloadHash: GENESIS_HASH,
      previousBlockHash: '0000000000000000000000000000000000000000000000000000000000000000',
      blockHash: GENESIS_HASH,
      severity: 'INFO',
      componentTag: 'GENESIS_ANCHOR',
      isTampered: false
    };

    const genesisNode: D3NodeData = {
      id: 'genesis',
      index: 0,
      block: genesisBlock,
      isGenesis: true,
      x: startX,
      y: startY,
      severity: 'INFO',
      isTampered: false,
      hash: GENESIS_HASH,
      prevHash: '0x0000000000000000',
      payloadHash: GENESIS_HASH,
      timestamp: '00:00:00.000',
      componentTag: 'GENESIS_ANCHOR'
    };

    const blockNodes: D3NodeData[] = blocks.map((b, i) => ({
      id: `block-${b.index}`,
      index: b.index,
      block: b,
      isGenesis: false,
      x: startX + (i + 1) * horizontalSpacing,
      y: startY,
      severity: b.severity,
      isTampered: b.isTampered,
      hash: b.blockHash,
      prevHash: b.previousBlockHash,
      payloadHash: b.eventPayloadHash,
      timestamp: b.timestamp,
      componentTag: b.componentTag
    }));

    const allNodes: D3NodeData[] = [genesisNode, ...blockNodes];

    // Build Links and identify fractures
    const links: D3LinkData[] = [];
    let chainFractured = false;
    for (let i = 0; i < allNodes.length - 1; i++) {
      const source = allNodes[i];
      const target = allNodes[i + 1];

      // A link is severed if the target is tampered or if any prior block was tampered
      if (target.isTampered || source.isTampered) {
        chainFractured = true;
      }
      const isSevered = target.isTampered || source.isTampered || chainFractured;
      links.push({ source, target, isSevered });
    }

    // Render Links
    const linkGroup = g.append('g').attr('class', 'links-layer');

    links.forEach((link) => {
      const x1 = link.source.x + nodeWidth;
      const y1 = link.source.y + nodeHeight / 2;
      const x2 = link.target.x;
      const y2 = link.target.y + nodeHeight / 2;
      const midX = (x1 + x2) / 2;

      if (link.isSevered) {
        // Jagged severed fracture path
        const fracturePath = `M ${x1} ${y1} L ${midX - 10} ${y1 - 10} L ${midX + 6} ${y1 + 10} L ${midX - 2} ${y1 - 4} L ${x2} ${y2}`;

        // Red fracture line
        linkGroup.append('path')
          .attr('d', fracturePath)
          .attr('fill', 'none')
          .attr('stroke', '#EF4444')
          .attr('stroke-width', 2.8)
          .attr('stroke-linecap', 'round')
          .attr('marker-end', 'url(#arrow-crimson)');

        // Warning fracture pulse circle
        linkGroup.append('circle')
          .attr('cx', midX)
          .attr('cy', y1)
          .attr('r', 6)
          .attr('fill', '#EF4444')
          .attr('stroke', '#FECACA')
          .attr('stroke-width', 1.5)
          .attr('class', 'animate-pulse');

        // Fracture text label
        linkGroup.append('text')
          .attr('x', midX)
          .attr('y', y1 - 16)
          .attr('text-anchor', 'middle')
          .attr('fill', '#EF4444')
          .attr('font-size', '8px')
          .attr('font-family', 'JetBrains Mono, monospace')
          .attr('font-weight', 'bold')
          .text('⚡ SEVERED');
      } else {
        // Smooth cryptographic bus link
        const dx = (x2 - x1) / 2;
        const smoothPath = `M ${x1} ${y1} C ${x1 + dx} ${y1}, ${x2 - dx} ${y2}, ${x2} ${y2}`;

        // Base solid link
        linkGroup.append('path')
          .attr('d', smoothPath)
          .attr('fill', 'none')
          .attr('stroke', '#1E293B')
          .attr('stroke-width', 2);

        // Photonic animated dash flow
        linkGroup.append('path')
          .attr('d', smoothPath)
          .attr('fill', 'none')
          .attr('stroke', '#00E5FF')
          .attr('stroke-width', 2)
          .attr('stroke-dasharray', '8, 8')
          .attr('marker-end', 'url(#arrow-cyan)')
          .style('animation', 'merkleDash 1.4s linear infinite');
      }
    });

    // Render Nodes (Chamfered cut-corner tactical cards)
    const nodeGroup = g.append('g').attr('class', 'nodes-layer');

    const nodeG = nodeGroup.selectAll('.merkle-node')
      .data(allNodes)
      .enter()
      .append('g')
      .attr('class', 'merkle-node')
      .attr('transform', (d) => `translate(${d.x}, ${d.y})`)
      .style('cursor', 'pointer')
      .on('click', (_, d) => {
        if (d.block) {
          setSelectedBlock(d.block);
          setIsModalOpen(true);
          onBlockSelect?.(d.block);
        }
      });

    // Node Box (Chamfered tactical polygon)
    nodeG.each(function (d) {
      const sel = d3.select(this);
      const c = 8; // chamfer size
      const points = `
        ${c},0 
        ${nodeWidth - c},0 
        ${nodeWidth},${c} 
        ${nodeWidth},${nodeHeight - c} 
        ${nodeWidth - c},${nodeHeight} 
        ${c},${nodeHeight} 
        0,${nodeHeight - c} 
        0,${c}
      `.replace(/\s+/g, ' ').trim();

      const strokeColor = d.isTampered
        ? '#EF4444'
        : d.severity === 'CRITICAL'
        ? '#EF4444'
        : d.severity === 'WARN'
        ? '#F59E0B'
        : '#00E5FF';

      const fillColor = d.isTampered ? '#450A0A' : '#0B0F19';

      // Background Card
      const poly = sel.append('polygon')
        .attr('points', points)
        .attr('fill', fillColor)
        .attr('stroke', strokeColor)
        .attr('stroke-width', d.isTampered ? 3 : 1.2)
        .attr('stroke-opacity', 1)
        .style('transition', 'all 0.2s ease');

      // If tampered, apply animated high-contrast pulse
      if (d.isTampered) {
        poly.attr('class', 'tampered-pulse-card');
      }

      // Top Header Row: Block Number & Tag
      const headerG = sel.append('g').attr('transform', 'translate(10, 18)');

      // LED dot
      headerG.append('rect')
        .attr('x', 0)
        .attr('y', -6)
        .attr('width', 6)
        .attr('height', 6)
        .attr('fill', strokeColor);

      // Title
      headerG.append('text')
        .attr('x', 12)
        .attr('y', 0)
        .attr('fill', d.isTampered ? '#EF4444' : '#F8FAFC')
        .attr('font-size', '10px')
        .attr('font-family', 'JetBrains Mono, monospace')
        .attr('font-weight', 'bold')
        .text(d.isGenesis ? 'GENESIS [#00]' : `BLOCK #${d.index.toString().padStart(2, '0')}`);

      // Timestamp
      headerG.append('text')
        .attr('x', nodeWidth - 20)
        .attr('y', 0)
        .attr('text-anchor', 'end')
        .attr('fill', d.isTampered ? '#FECACA' : '#64748B')
        .attr('font-size', '8px')
        .attr('font-family', 'JetBrains Mono, monospace')
        .text(d.timestamp.slice(-8));

      // Component Subtitle
      sel.append('text')
        .attr('x', 10)
        .attr('y', 36)
        .attr('fill', strokeColor)
        .attr('font-size', '8.5px')
        .attr('font-family', 'JetBrains Mono, monospace')
        .attr('font-weight', 'bold')
        .text(d.componentTag);

      // Truncated Payload Hash
      sel.append('text')
        .attr('x', 10)
        .attr('y', 50)
        .attr('fill', d.isTampered ? '#FCA5A5' : '#64748B')
        .attr('font-size', '8px')
        .attr('font-family', 'JetBrains Mono, monospace')
        .text(`P: 0x${d.payloadHash.slice(0, 10).toUpperCase()}...`);

      // Truncated Block Hash
      sel.append('text')
        .attr('x', 10)
        .attr('y', 64)
        .attr('fill', d.isTampered ? '#EF4444' : '#10B981')
        .attr('font-size', '9px')
        .attr('font-family', 'JetBrains Mono, monospace')
        .attr('font-weight', 'bold')
        .text(`H: 0x${d.hash.slice(-10).toUpperCase()}`);

      // Bottom Status Indicator
      sel.append('text')
        .attr('x', 10)
        .attr('y', 78)
        .attr('fill', d.isTampered ? '#EF4444' : '#00E5FF')
        .attr('font-size', '7.5px')
        .attr('font-family', 'JetBrains Mono, monospace')
        .attr('font-weight', 'bold')
        .text(d.isTampered ? '[FRACTURE_TAMPERED]' : '[SEALED_SHA256]');

      sel.append('text')
        .attr('x', nodeWidth - 10)
        .attr('y', 78)
        .attr('text-anchor', 'end')
        .attr('fill', d.isTampered ? '#FECACA' : '#00E5FF')
        .attr('font-size', '7.5px')
        .attr('font-family', 'JetBrains Mono, monospace')
        .attr('font-weight', 'bold')
        .text('INSPECT →');
    });

    // Auto-scroll / initial center
    const totalChainWidth = startX + (allNodes.length) * horizontalSpacing + 100;
    if (totalChainWidth > width) {
      const targetTransform = d3.zoomIdentity.translate(width - totalChainWidth, 0);
      svg.transition().duration(500).call(zoom.transform, targetTransform);
    }
  }, [blocks, isChainValid]);

  return (
    <div
      ref={containerRef}
      style={{
        backgroundColor: '#030712',
        border: `1px solid ${isChainValid ? '#1E293B' : '#EF4444'}`,
        color: '#F8FAFC',
        fontFamily: 'JetBrains Mono, monospace',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
        padding: '16px',
        position: 'relative'
      }}
    >
      {/* Keyframe styles for animated flow and high-alert tampered pulse */}
      <style>{`
        @keyframes merkleDash {
          from { stroke-dashoffset: 16; }
          to { stroke-dashoffset: 0; }
        }
        @keyframes tamperedGlow {
          0%, 100% {
            fill: #450A0A;
            stroke: #EF4444;
            filter: drop-shadow(0 0 6px rgba(239, 68, 68, 0.8));
          }
          50% {
            fill: #7F1D1D;
            stroke: #FCA5A5;
            filter: drop-shadow(0 0 16px rgba(239, 68, 68, 1));
          }
        }
        .tampered-pulse-card {
          animation: tamperedGlow 1.2s infinite ease-in-out;
        }
      `}</style>

      {/* Telemetry HUD Header */}
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderBottom: '1px solid #1E293B',
        paddingBottom: '10px'
      }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <div style={{
              width: '8px',
              height: '8px',
              backgroundColor: isChainValid ? '#00E5FF' : '#EF4444',
              borderRadius: '1px'
            }} />
            <span style={{
              color: isChainValid ? '#00E5FF' : '#EF4444',
              fontWeight: 'bold',
              fontSize: '11px',
              letterSpacing: '1px'
            }}>
              {isChainValid
                ? 'CRYPTOGRAPHIC MERKLE AUDIT CHAIN [D3.JS FORENSIC LEDGER]'
                : 'FRACTURE DETECTED // TAMPER-EVIDENT FORENSIC ALARM TRIGGERED'}
            </span>
          </div>
          <div style={{ color: '#64748B', fontSize: '9px', marginTop: '3px' }}>
            STATE ROOT: 0x{currentRoot.slice(0, 16).toUpperCase()}...{currentRoot.slice(-8).toUpperCase()} • SHA-256 STATE BUS
          </div>
        </div>

        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <div style={{
            backgroundColor: '#0B0F19',
            border: '1px solid #1E293B',
            padding: '4px 8px',
            fontSize: '10px',
            color: '#10B981'
          }}>
            HEIGHT: <span style={{ fontWeight: 'bold' }}>{blocks.length}</span>
          </div>
          <div style={{
            backgroundColor: isChainValid ? '#0B0F19' : '#450A0A',
            border: `1px solid ${isChainValid ? '#1E293B' : '#EF4444'}`,
            padding: '4px 8px',
            fontSize: '10px',
            color: isChainValid ? '#10B981' : '#EF4444',
            fontWeight: 'bold'
          }}>
            {isChainValid ? 'SEAL: VALID' : 'SEAL: COMPROMISED'}
          </div>
        </div>
      </div>

      {/* D3 SVG Interactive Visualizer Canvas */}
      <div style={{
        backgroundColor: '#050A14',
        border: `1px solid ${isChainValid ? '#1E293B' : '#EF4444'}`,
        position: 'relative',
        overflow: 'hidden',
        height: '290px'
      }}>
        <svg
          ref={svgRef}
          width="100%"
          height="290"
          style={{ cursor: 'grab' }}
        />

        {/* Tactical Overlay Legend */}
        <div style={{
          position: 'absolute',
          bottom: '8px',
          left: '12px',
          display: 'flex',
          gap: '12px',
          fontSize: '9px',
          color: '#64748B',
          pointerEvents: 'none'
        }}>
          <span><span style={{ color: '#00E5FF' }}>●</span> INFO</span>
          <span><span style={{ color: '#F59E0B' }}>●</span> WARN</span>
          <span><span style={{ color: '#EF4444' }}>●</span> CRITICAL</span>
          <span><span style={{ color: '#EF4444', fontWeight: 'bold' }}>⚡</span> JAGGED FRACTURE (TAMPERED)</span>
          <span><span style={{ color: '#00E5FF' }}>→</span> PHOTONIC BUS LINK</span>
        </div>
      </div>

      {/* Tactical Operator Control Toolbar */}
      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
        <button
          onClick={() => {
            const res = DiagnosticStore.verifyMerkleLedgerIntegrity();
            setIsChainValid(res.isValid);
            setVerificationFeedback(res.message);
          }}
          style={{
            flex: 1,
            backgroundColor: isChainValid ? '#10B981' : '#0B0F19',
            border: `1px solid ${isChainValid ? '#10B981' : '#EF4444'}`,
            color: isChainValid ? '#030712' : '#EF4444',
            padding: '7px 12px',
            fontSize: '10px',
            fontWeight: 'bold',
            fontFamily: 'JetBrains Mono, monospace',
            cursor: 'pointer'
          }}
        >
          [VERIFY MERKLE INTEGRITY (SHA-256)]
        </button>

        <button
          onClick={() => {
            // Randomized tamper simulation: no index passed picks random block
            const targetIdx = DiagnosticStore.simulateTamperAttack();
            setIsChainValid(false);
            setVerificationFeedback(
              `ADVERSARIAL ATTACK SIMULATED: Injected adversarial payload into Block #${targetIdx}. Cryptographic continuity fractured!`
            );
          }}
          style={{
            flex: 1,
            backgroundColor: '#450A0A',
            border: '1px solid #EF4444',
            color: '#EF4444',
            padding: '7px 12px',
            fontSize: '10px',
            fontWeight: 'bold',
            fontFamily: 'JetBrains Mono, monospace',
            cursor: 'pointer'
          }}
        >
          [SIMULATE RANDOM TAMPER ATTACK]
        </button>

        <button
          onClick={() => {
            DiagnosticStore.restoreLedgerIntegrity();
            setIsChainValid(true);
            setVerificationFeedback('LEDGER HEALED & RESEALED: Recalculated state root hashes from Genesis anchor.');
          }}
          style={{
            flex: 1,
            backgroundColor: '#0B0F19',
            border: '1px solid #1E293B',
            color: '#00E5FF',
            padding: '7px 12px',
            fontSize: '10px',
            fontWeight: 'bold',
            fontFamily: 'JetBrains Mono, monospace',
            cursor: 'pointer'
          }}
        >
          [RESEAL & HEAL LEDGER]
        </button>

        <button
          onClick={() => {
            DiagnosticStore.simulateLog('INFO');
            setVerificationFeedback('INJECTED RUNTIME AUDIT RECORD: Appended new block to cryptographic chain.');
          }}
          style={{
            backgroundColor: '#0B0F19',
            border: '1px solid #00E5FF',
            color: '#00E5FF',
            padding: '7px 12px',
            fontSize: '10px',
            fontWeight: 'bold',
            fontFamily: 'JetBrains Mono, monospace',
            cursor: 'pointer'
          }}
        >
          [+AUDIT EVENT]
        </button>

        <button
          onClick={() => {
            const csvContent = DiagnosticStore.exportAuditLogCSV();
            const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
            const url = URL.createObjectURL(blob);
            const link = document.createElement('a');
            link.setAttribute('href', url);
            link.setAttribute('download', 'aegora_merkle_audit_ledger_signed.csv');
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url);
            setVerificationFeedback('FORENSIC CSV EXPORTED: Downloaded aegora_merkle_audit_ledger_signed.csv with cryptographic provenance seal.');
          }}
          style={{
            backgroundColor: '#0B0F19',
            border: '1px solid #10B981',
            color: '#10B981',
            padding: '7px 12px',
            fontSize: '10px',
            fontWeight: 'bold',
            fontFamily: 'JetBrains Mono, monospace',
            cursor: 'pointer'
          }}
        >
          [EXPORT LOGS (CSV)]
        </button>
      </div>

      {/* Verification Feedback Banner */}
      {verificationFeedback && (
        <div style={{
          backgroundColor: isChainValid ? 'rgba(16, 185, 129, 0.12)' : 'rgba(239, 68, 68, 0.2)',
          border: `1px solid ${isChainValid ? '#10B981' : '#EF4444'}`,
          padding: '8px 12px',
          fontSize: '10px',
          color: isChainValid ? '#10B981' : '#EF4444',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <span style={{ fontWeight: '500' }}>{verificationFeedback}</span>
          <button
            onClick={() => setVerificationFeedback(null)}
            style={{
              background: 'none',
              border: 'none',
              color: '#64748B',
              cursor: 'pointer',
              fontSize: '10px',
              fontWeight: 'bold'
            }}
          >
            [X]
          </button>
        </div>
      )}

      {/* =========================================================================
          TACTICAL INSPECTION MODAL (FULL 64-CHAR HASH & RAW PAYLOAD FORENSICS)
      ========================================================================= */}
      {isModalOpen && selectedBlock && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(3, 7, 18, 0.88)',
            backdropFilter: 'blur(8px)',
            zIndex: 9999,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '20px',
            fontFamily: 'JetBrains Mono, monospace'
          }}
          onClick={(e) => {
            if (e.target === e.currentTarget) {
              setIsModalOpen(false);
            }
          }}
        >
          <div
            style={{
              width: '100%',
              maxWidth: '840px',
              maxHeight: '90vh',
              backgroundColor: '#030712',
              border: `2px solid ${selectedBlock.isTampered ? '#EF4444' : '#00E5FF'}`,
              boxShadow: selectedBlock.isTampered
                ? '0 0 32px rgba(239, 68, 68, 0.35)'
                : '0 0 32px rgba(0, 229, 255, 0.2)',
              display: 'flex',
              flexDirection: 'column',
              overflow: 'hidden'
            }}
          >
            {/* Modal Header */}
            <div style={{
              backgroundColor: selectedBlock.isTampered ? '#450A0A' : '#0B0F19',
              borderBottom: `1px solid ${selectedBlock.isTampered ? '#EF4444' : '#1E293B'}`,
              padding: '12px 16px',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <div style={{
                  width: '10px',
                  height: '10px',
                  backgroundColor: selectedBlock.isTampered ? '#EF4444' : '#00E5FF',
                  borderRadius: '1px'
                }} />
                <span style={{
                  color: selectedBlock.isTampered ? '#EF4444' : '#00E5FF',
                  fontWeight: 'bold',
                  fontSize: '12px',
                  letterSpacing: '1px'
                }}>
                  FORENSIC AUDITOR // CRYPTOGRAPHIC MERKLE BLOCK #{selectedBlock.index.toString().padStart(2, '0')}
                </span>
                {selectedBlock.index === 0 && (
                  <span style={{
                    backgroundColor: '#1E293B',
                    color: '#00E5FF',
                    padding: '2px 6px',
                    fontSize: '9px',
                    fontWeight: 'bold'
                  }}>
                    GENESIS ANCHOR
                  </span>
                )}
              </div>

              <button
                onClick={() => setIsModalOpen(false)}
                style={{
                  background: 'none',
                  border: `1px solid ${selectedBlock.isTampered ? '#EF4444' : '#1E293B'}`,
                  color: selectedBlock.isTampered ? '#EF4444' : '#94A3B8',
                  padding: '4px 10px',
                  fontSize: '11px',
                  fontFamily: 'JetBrains Mono, monospace',
                  cursor: 'pointer'
                }}
              >
                [DISMISS (ESC)]
              </button>
            </div>

            {/* Modal Content Scroll Body */}
            <div style={{
              padding: '16px',
              overflowY: 'auto',
              display: 'flex',
              flexDirection: 'column',
              gap: '14px',
              fontSize: '10px'
            }}>
              {/* Metadata Badges Row */}
              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
                gap: '8px'
              }}>
                <div style={{ backgroundColor: '#0B0F19', border: '1px solid #1E293B', padding: '8px' }}>
                  <div style={{ color: '#64748B', fontSize: '8.5px' }}>BLOCK HEIGHT</div>
                  <div style={{ color: '#F8FAFC', fontWeight: 'bold', fontSize: '11px', marginTop: '2px' }}>
                    #{selectedBlock.index.toString().padStart(2, '0')}
                  </div>
                </div>

                <div style={{ backgroundColor: '#0B0F19', border: '1px solid #1E293B', padding: '8px' }}>
                  <div style={{ color: '#64748B', fontSize: '8.5px' }}>TIMESTAMP</div>
                  <div style={{ color: '#F8FAFC', fontWeight: 'bold', fontSize: '11px', marginTop: '2px' }}>
                    {selectedBlock.timestamp}
                  </div>
                </div>

                <div style={{ backgroundColor: '#0B0F19', border: '1px solid #1E293B', padding: '8px' }}>
                  <div style={{ color: '#64748B', fontSize: '8.5px' }}>SUBSYSTEM / COMPONENT</div>
                  <div style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '11px', marginTop: '2px' }}>
                    {selectedBlock.componentTag}
                  </div>
                </div>

                <div style={{ backgroundColor: '#0B0F19', border: '1px solid #1E293B', padding: '8px' }}>
                  <div style={{ color: '#64748B', fontSize: '8.5px' }}>EVENT CLASSIFICATION</div>
                  <div style={{
                    color: selectedBlock.severity === 'CRITICAL' ? '#EF4444' : selectedBlock.severity === 'WARN' ? '#F59E0B' : '#00E5FF',
                    fontWeight: 'bold',
                    fontSize: '11px',
                    marginTop: '2px'
                  }}>
                    {selectedBlock.severity}
                  </div>
                </div>
              </div>

              {/* Status Banner */}
              <div style={{
                backgroundColor: selectedBlock.isTampered ? 'rgba(239, 68, 68, 0.15)' : 'rgba(16, 185, 129, 0.1)',
                border: `1px solid ${selectedBlock.isTampered ? '#EF4444' : '#10B981'}`,
                padding: '10px 14px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}>
                <div>
                  <div style={{
                    color: selectedBlock.isTampered ? '#EF4444' : '#10B981',
                    fontWeight: 'bold',
                    fontSize: '11px'
                  }}>
                    MERKLE PROOF STATUS:{' '}
                    {selectedBlock.isTampered
                      ? '[FRACTURE_DETECTED_INVALID_PROOF]'
                      : '[CRYPTOGRAPHICALLY_SEALED]'}
                  </div>
                  <div style={{ color: selectedBlock.isTampered ? '#FCA5A5' : '#6EE7B7', fontSize: '9px', marginTop: '3px' }}>
                    {selectedBlock.isTampered
                      ? 'CRITICAL INTEGRITY FAILURE: Recomputed SHA-256 digest on this payload mismatches block seal! Adversarial tampering confirmed.'
                      : 'MATHEMATICALLY VERIFIED: Hash successfully links to previous state root and payload hash without alteration.'}
                  </div>
                </div>

                <div style={{
                  padding: '4px 8px',
                  backgroundColor: selectedBlock.isTampered ? '#450A0A' : '#064E3B',
                  border: `1px solid ${selectedBlock.isTampered ? '#EF4444' : '#10B981'}`,
                  color: selectedBlock.isTampered ? '#EF4444' : '#10B981',
                  fontWeight: 'bold',
                  fontSize: '9px'
                }}>
                  {selectedBlock.isTampered ? 'COMPROMISED' : '100% SECURE'}
                </div>
              </div>

              {/* Full 64-Character SHA-256 Hashes Display */}
              <div style={{
                backgroundColor: '#0B0F19',
                border: '1px solid #1E293B',
                padding: '12px',
                display: 'flex',
                flexDirection: 'column',
                gap: '10px'
              }}>
                <div style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '10px', letterSpacing: '0.5px' }}>
                  IMMUTABLE HASH REGISTERS (FULL 64-CHARACTER SHA-256)
                </div>

                {/* Block Hash */}
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '2px' }}>
                    <span style={{ color: '#64748B', fontSize: '9px' }}>CURRENT BLOCK HASH (H_n):</span>
                    <button
                      onClick={() => handleCopyHash(selectedBlock.blockHash, 'blockHash')}
                      style={{
                        background: 'none',
                        border: 'none',
                        color: copiedHashKey === 'blockHash' ? '#10B981' : '#00E5FF',
                        cursor: 'pointer',
                        fontSize: '9px',
                        fontFamily: 'JetBrains Mono, monospace'
                      }}
                    >
                      {copiedHashKey === 'blockHash' ? '[COPIED!]' : '[COPY HEX]'}
                    </button>
                  </div>
                  <div style={{
                    backgroundColor: '#030712',
                    border: `1px solid ${selectedBlock.isTampered ? '#EF4444' : '#1E293B'}`,
                    padding: '8px',
                    wordBreak: 'break-all',
                    color: selectedBlock.isTampered ? '#EF4444' : '#10B981',
                    fontSize: '10px',
                    fontWeight: 'bold'
                  }}>
                    {selectedBlock.blockHash}
                  </div>
                </div>

                {/* Previous Block Hash */}
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '2px' }}>
                    <span style={{ color: '#64748B', fontSize: '9px' }}>PREVIOUS BLOCK HASH (H_(n-1)):</span>
                    <button
                      onClick={() => handleCopyHash(selectedBlock.previousBlockHash, 'prevHash')}
                      style={{
                        background: 'none',
                        border: 'none',
                        color: copiedHashKey === 'prevHash' ? '#10B981' : '#00E5FF',
                        cursor: 'pointer',
                        fontSize: '9px',
                        fontFamily: 'JetBrains Mono, monospace'
                      }}
                    >
                      {copiedHashKey === 'prevHash' ? '[COPIED!]' : '[COPY HEX]'}
                    </button>
                  </div>
                  <div style={{
                    backgroundColor: '#030712',
                    border: '1px solid #1E293B',
                    padding: '8px',
                    wordBreak: 'break-all',
                    color: '#94A3B8',
                    fontSize: '10px'
                  }}>
                    {selectedBlock.previousBlockHash}
                  </div>
                </div>

                {/* Event Payload Hash */}
                <div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '2px' }}>
                    <span style={{ color: '#64748B', fontSize: '9px' }}>EVENT PAYLOAD DIGEST (PayloadHash_n):</span>
                    <button
                      onClick={() => handleCopyHash(selectedBlock.eventPayloadHash, 'payloadHash')}
                      style={{
                        background: 'none',
                        border: 'none',
                        color: copiedHashKey === 'payloadHash' ? '#10B981' : '#00E5FF',
                        cursor: 'pointer',
                        fontSize: '9px',
                        fontFamily: 'JetBrains Mono, monospace'
                      }}
                    >
                      {copiedHashKey === 'payloadHash' ? '[COPIED!]' : '[COPY HEX]'}
                    </button>
                  </div>
                  <div style={{
                    backgroundColor: '#030712',
                    border: `1px solid ${selectedBlock.isTampered ? '#EF4444' : '#1E293B'}`,
                    padding: '8px',
                    wordBreak: 'break-all',
                    color: selectedBlock.isTampered ? '#FCA5A5' : '#38BDF8',
                    fontSize: '10px'
                  }}>
                    {selectedBlock.eventPayloadHash}
                  </div>
                </div>
              </div>

              {/* Mathematical Verification Breakdown */}
              <div style={{
                backgroundColor: '#0B0F19',
                border: '1px solid #1E293B',
                padding: '12px',
                display: 'flex',
                flexDirection: 'column',
                gap: '8px'
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '10px' }}>
                    MATHEMATICAL CONTRACT: H_n = SHA256( PayloadHash_n + H_(n-1) )
                  </span>
                  <span style={{
                    color: modalProofVerification?.matches ? '#10B981' : '#EF4444',
                    fontWeight: 'bold',
                    fontSize: '10px'
                  }}>
                    {modalProofVerification?.matches ? '[CONTRACT HONORED]' : '[CONTRACT VIOLATED]'}
                  </span>
                </div>

                <div style={{
                  backgroundColor: '#030712',
                  border: '1px solid #1E293B',
                  padding: '8px',
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '4px',
                  fontSize: '9.5px'
                }}>
                  <div>
                    <span style={{ color: '#64748B' }}>1. INPUT PAIR: </span>
                    <span style={{ color: '#F8FAFC' }}>
                      {selectedBlock.eventPayloadHash.slice(0, 16)}... + {selectedBlock.previousBlockHash.slice(0, 16)}...
                    </span>
                  </div>
                  <div>
                    <span style={{ color: '#64748B' }}>2. RECOMPUTED DIGEST: </span>
                    <span style={{ color: modalProofVerification?.matches ? '#10B981' : '#EF4444', fontWeight: 'bold' }}>
                      {modalProofVerification?.recomputedBlockHash}
                    </span>
                  </div>
                  <div>
                    <span style={{ color: '#64748B' }}>3. STORED SEAL: </span>
                    <span style={{ color: '#F8FAFC' }}>{selectedBlock.blockHash}</span>
                  </div>
                  <div>
                    <span style={{ color: '#64748B' }}>4. VERDICT: </span>
                    <span style={{
                      color: modalProofVerification?.matches ? '#10B981' : '#EF4444',
                      fontWeight: 'bold'
                    }}>
                      {modalProofVerification?.matches
                        ? 'PASS // BIT-EXACT MATCH CONFIRMED (NO ADVERSARIAL DRIFT)'
                        : 'FAIL // CRYPTOGRAPHIC ANOMALY: PAYLOAD MUTATION INVALIDATES PARENT CHAIN'}
                    </span>
                  </div>
                </div>
              </div>

              {/* Raw Event Payload Monospace Inspector */}
              <div style={{
                backgroundColor: '#0B0F19',
                border: '1px solid #1E293B',
                padding: '12px',
                display: 'flex',
                flexDirection: 'column',
                gap: '8px'
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ color: '#00E5FF', fontWeight: 'bold', fontSize: '10px' }}>
                    RAW EVENT PAYLOAD (AUDIT PAYLOAD BODY)
                  </span>
                  <span style={{ color: '#64748B', fontSize: '8.5px' }}>
                    PAYLOAD SIZE: {selectedBlock.rawPayload.length} BYTES
                  </span>
                </div>

                <pre style={{
                  backgroundColor: '#030712',
                  border: `1px solid ${selectedBlock.isTampered ? '#EF4444' : '#1E293B'}`,
                  padding: '12px',
                  margin: 0,
                  fontSize: '9.5px',
                  color: selectedBlock.isTampered ? '#FCA5A5' : '#E2E8F0',
                  lineHeight: '1.4',
                  overflowX: 'auto',
                  maxHeight: '180px',
                  fontFamily: 'JetBrains Mono, monospace'
                }}>
                  {selectedBlock.rawPayload}
                </pre>
              </div>

              {/* Action Toolbar Inside Modal */}
              <div style={{ display: 'flex', gap: '8px', marginTop: '6px' }}>
                <button
                  onClick={() => {
                    const res = DiagnosticStore.verifyMerkleLedgerIntegrity();
                    setIsChainValid(res.isValid);
                    setVerificationFeedback(res.message);
                  }}
                  style={{
                    flex: 1,
                    backgroundColor: '#0B0F19',
                    border: '1px solid #10B981',
                    color: '#10B981',
                    padding: '8px 12px',
                    fontSize: '10px',
                    fontWeight: 'bold',
                    fontFamily: 'JetBrains Mono, monospace',
                    cursor: 'pointer'
                  }}
                >
                  [RE-VERIFY FULL LEDGER]
                </button>

                {!selectedBlock.isTampered && selectedBlock.index !== 0 && (
                  <button
                    onClick={() => {
                      DiagnosticStore.simulateTamperAttack(selectedBlock.index);
                      setIsChainValid(false);
                      setVerificationFeedback(
                        `TAMPER INJECTED: Maliciously forged payload into Block #${selectedBlock.index}.`
                      );
                    }}
                    style={{
                      flex: 1,
                      backgroundColor: '#450A0A',
                      border: '1px solid #EF4444',
                      color: '#EF4444',
                      padding: '8px 12px',
                      fontSize: '10px',
                      fontWeight: 'bold',
                      fontFamily: 'JetBrains Mono, monospace',
                      cursor: 'pointer'
                    }}
                  >
                    [TAMPER THIS BLOCK]
                  </button>
                )}

                {selectedBlock.isTampered && (
                  <button
                    onClick={() => {
                      DiagnosticStore.restoreLedgerIntegrity();
                      setIsChainValid(true);
                      setVerificationFeedback('LEDGER RESTORED & RESEALED FROM GENESIS ROOT.');
                    }}
                    style={{
                      flex: 1,
                      backgroundColor: '#0B0F19',
                      border: '1px solid #00E5FF',
                      color: '#00E5FF',
                      padding: '8px 12px',
                      fontSize: '10px',
                      fontWeight: 'bold',
                      fontFamily: 'JetBrains Mono, monospace',
                      cursor: 'pointer'
                    }}
                  >
                    [RESTORE & RESEAL CHAIN]
                  </button>
                )}

                <button
                  onClick={() => setIsModalOpen(false)}
                  style={{
                    backgroundColor: '#1E293B',
                    border: '1px solid #334155',
                    color: '#F8FAFC',
                    padding: '8px 14px',
                    fontSize: '10px',
                    fontWeight: 'bold',
                    fontFamily: 'JetBrains Mono, monospace',
                    cursor: 'pointer'
                  }}
                >
                  [CLOSE]
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
