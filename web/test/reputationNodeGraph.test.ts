import { describe, it, expect, beforeEach } from 'vitest';
import {
  NeuralGraphMeshEngine,
  NeuralGraphMeshEngineService
} from '../src/NeuralGraphMeshEngine';
import { ReputationNodeGraph } from '../ReputationNodeGraph';

describe('ReputationNodeGraph & NeuralGraphMeshEngine Unit Tests', () => {
  let engine: NeuralGraphMeshEngineService;

  beforeEach(() => {
    engine = new NeuralGraphMeshEngineService();
  });

  it('01: getTopCentralityReport returns top 5 highest-centrality nodes', () => {
    const report = engine.getTopCentralityReport(5);
    expect(report.topNodes.length).toBeLessThanOrEqual(5);
    expect(report.topNodes.length).toBeGreaterThan(0);
    expect(report.totalEvaluatedNodes).toBeGreaterThanOrEqual(report.topNodes.length);

    // Verify descending order by centralityScore
    for (let i = 0; i < report.topNodes.length - 1; i++) {
      expect(report.topNodes[i].centralityScore).toBeGreaterThanOrEqual(
        report.topNodes[i + 1].centralityScore
      );
    }
  });

  it('02: Node report items contain all required dashboard fields', () => {
    const report = engine.getTopCentralityReport(5);
    for (const node of report.topNodes) {
      expect(node.id).toBeDefined();
      expect(node.label).toBeDefined();
      expect(node.type).toBeDefined();
      expect(node.category).toBeDefined();
      expect(node.centralityScore).toBeGreaterThanOrEqual(0);
      expect(node.centralityScore).toBeLessThanOrEqual(1.0);
      expect(node.reputationScore).toBeGreaterThanOrEqual(0);
      expect(node.reputationScore).toBeLessThanOrEqual(100);
      expect(node.totalConnections).toBe(node.inDegree + node.outDegree);
      expect(node.weightedTotalDegree).toBeGreaterThanOrEqual(0);
      expect(typeof node.isC2Node).toBe('boolean');
    }
  });

  it('03: Zero-day C2 injection recalculates top centrality dynamically', () => {
    const injected = engine.injectZeroDayC2Vector();
    expect(injected.isC2Node).toBe(true);

    const report = engine.getTopCentralityReport(5);
    const topIds = report.topNodes.map(n => n.id);
    expect(topIds).toContain(injected.id);

    const topNode = report.topNodes.find(n => n.id === injected.id);
    expect(topNode).toBeDefined();
    expect(topNode!.isC2Node).toBe(true);
    expect(topNode!.centralityScore).toBeGreaterThan(0.5);
  });

  it('04: ReputationNodeGraph component is defined and exportable', () => {
    expect(ReputationNodeGraph).toBeDefined();
    expect(typeof ReputationNodeGraph).toBe('function');
  });
});
