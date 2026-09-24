// web/NeuralGraphMeshEngine.ts
/**
 * ============================================================================
 * NEURAL GRAPH REPUTATION ENGINE (PAGE RANK / EIGENVECTOR APPROXIMATION)
 * ============================================================================
 * Models threat intelligence indicators of compromise (IOCs, IPs, CVE Hashes,
 * SHA-256 signatures, Domain Beacons) as directed weighted graphs.
 * Executes localized PageRank power-iterations to calculate dynamic reputation,
 * eigenvector centrality, and automated Command & Control (C2) detection.
 */

export type IOCVertexType = 'IP' | 'CVE' | 'HASH' | 'DOMAIN' | 'ENDPOINT' | 'CERTIFICATE';

export type ThreatCategory =
  | 'C2_CONTROLLER'
  | 'MALWARE_DROPPER'
  | 'LATERAL_EXPLOIT'
  | 'EXFILTRATION_SINK'
  | 'RECON_SCANNER'
  | 'BENIGN_INTERNAL';

export type AttackVectorType =
  | 'BEACON'
  | 'LATERAL_SMB'
  | 'EXPLOIT_PAYLOAD'
  | 'DNS_TUNNEL'
  | 'SSH_INFILTRATION'
  | 'ENCRYPTED_C2';

export interface NeuralVertex {
  id: string;
  label: string;
  type: IOCVertexType;
  category: ThreatCategory;
  threatWeight: number; // 0.0 to 1.0 (inherent threat severity)
  centralityScore: number; // 0.0 to 1.0 (PageRank centrality)
  reputationScore: number; // 0 to 100 (composite risk score; >70 = Critical Risk)
  inDegree: number;
  outDegree: number;
  weightedInDegree: number;
  weightedOutDegree: number;
  isC2Node: boolean;
  isAnomalousCluster: boolean;
  clusteringCoefficient: number;
  lastObserved: string;
  associatedIncidents: string[];
  geoMetadata?: {
    country: string;
    asn: string;
  };
}

export interface NeuralEdge {
  id: string;
  source: string; // Vertex ID
  target: string; // Vertex ID
  weight: number; // 0.1 to 10.0 (volume/frequency of vector communication)
  vectorType: AttackVectorType;
  bytesTransferred: number;
  packetCount: number;
  anomalousEntropy: number; // 0.0 to 1.0
  timestamp: string;
}

export interface PageRankIterationMetrics {
  iterations: number;
  deltaL1: number;
  converged: boolean;
  durationMs: number;
}

export interface ClusterAnomalyReport {
  vertexId: string;
  vertexLabel: string;
  anomalyType: 'C2_CONVERGENCE' | 'DENSE_EXFIL_CLIQUE' | 'ZERO_DAY_BURST';
  clusteringCoefficient: number;
  centralityScore: number;
  riskAssessment: string;
}

export interface HighCentralityNodeReportItem {
  id: string;
  label: string;
  type: IOCVertexType;
  category: ThreatCategory;
  centralityScore: number;
  reputationScore: number;
  inDegree: number;
  outDegree: number;
  totalConnections: number;
  weightedTotalDegree: number;
  isC2Node: boolean;
  threatWeight: number;
}

export interface TopCentralityWidgetReport {
  generatedAt: string;
  totalEvaluatedNodes: number;
  topNodes: HighCentralityNodeReportItem[];
}

export interface NeuralGraphSnapshot {
  vertices: readonly NeuralVertex[];
  edges: readonly NeuralEdge[];
  metrics: PageRankIterationMetrics;
  detectedC2Count: number;
  anomalousClusterCount: number;
  highestRiskVertex: NeuralVertex | null;
  lastCalculated: string;
}

class NeuralGraphMeshEngineService {
  private vertices: Map<string, NeuralVertex> = new Map();
  private edges: Map<string, NeuralEdge> = new Map();
  private listeners: Set<(snapshot: NeuralGraphSnapshot) => void> = new Set();
  private lastMetrics: PageRankIterationMetrics = {
    iterations: 0,
    deltaL1: 0,
    converged: false,
    durationMs: 0
  };

  constructor() {
    this.seedBaselineThreatTopology();
    this.runPageRank();
  }

  // ==========================================================================
  // BASELINE THREAT INTELLIGENCE SEEDING
  // ==========================================================================
  private seedBaselineThreatTopology() {
    const timestamp = new Date().toISOString();

    // Vertices: C2 controllers, weaponized hashes, vulnerable endpoints, benign relays
    const seedVertices: Partial<NeuralVertex>[] = [
      {
        id: 'IP:185.220.101.5',
        label: 'Tor C2 Exit Relay (ShadowNet)',
        type: 'IP',
        category: 'C2_CONTROLLER',
        threatWeight: 0.95,
        associatedIncidents: ['APT-41-BEACON', 'SHADOW_TUNNEL_01'],
        geoMetadata: { country: 'RO', asn: 'AS208323' }
      },
      {
        id: 'DOMAIN:telemetry-sync-cdn.top',
        label: 'Fast-Flux DGA Domain Sink',
        type: 'DOMAIN',
        category: 'C2_CONTROLLER',
        threatWeight: 0.92,
        associatedIncidents: ['DGA-BOTNET-ALPHA'],
        geoMetadata: { country: 'RU', asn: 'AS49505' }
      },
      {
        id: 'HASH:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
        label: 'CobaltStrike Stager v4.9 (DLL)',
        type: 'HASH',
        category: 'MALWARE_DROPPER',
        threatWeight: 0.88,
        associatedIncidents: ['PAYLOAD_STAGER_X']
      },
      {
        id: 'CVE:CVE-2024-38077',
        label: 'Windows Remote Desktop Lic. RCE',
        type: 'CVE',
        category: 'LATERAL_EXPLOIT',
        threatWeight: 0.98,
        associatedIncidents: ['ZERO_DAY_PROBE']
      },
      {
        id: 'ENDPOINT:WS-CORP-FIN-04',
        label: 'Finance Workstation 04',
        type: 'ENDPOINT',
        category: 'BENIGN_INTERNAL',
        threatWeight: 0.15,
        associatedIncidents: []
      },
      {
        id: 'ENDPOINT:WS-ENG-DEV-12',
        label: 'Engineering Build Node 12',
        type: 'ENDPOINT',
        category: 'BENIGN_INTERNAL',
        threatWeight: 0.20,
        associatedIncidents: []
      },
      {
        id: 'ENDPOINT:DB-PROD-CORE-01',
        label: 'Production Master PostgreSQL',
        type: 'ENDPOINT',
        category: 'EXFILTRATION_SINK',
        threatWeight: 0.40,
        associatedIncidents: ['DATABASE_EXFIL_WATCH']
      },
      {
        id: 'IP:198.51.100.42',
        label: 'Internal Subnet Gateway 10.0.4.1',
        type: 'IP',
        category: 'BENIGN_INTERNAL',
        threatWeight: 0.05,
        associatedIncidents: []
      }
    ];

    for (const v of seedVertices) {
      this.vertices.set(v.id!, {
        id: v.id!,
        label: v.label || v.id!,
        type: v.type || 'IP',
        category: v.category || 'BENIGN_INTERNAL',
        threatWeight: v.threatWeight || 0.1,
        centralityScore: 0.0,
        reputationScore: Math.round((v.threatWeight || 0.1) * 50),
        inDegree: 0,
        outDegree: 0,
        weightedInDegree: 0,
        weightedOutDegree: 0,
        isC2Node: v.category === 'C2_CONTROLLER',
        isAnomalousCluster: false,
        clusteringCoefficient: 0,
        lastObserved: timestamp,
        associatedIncidents: v.associatedIncidents || [],
        geoMetadata: v.geoMetadata
      });
    }

    // Directed edges (Attack and exfiltration vectors)
    const seedEdges: Partial<NeuralEdge>[] = [
      {
        id: 'E1',
        source: 'ENDPOINT:WS-CORP-FIN-04',
        target: 'HASH:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
        weight: 3.5,
        vectorType: 'EXPLOIT_PAYLOAD',
        bytesTransferred: 262144,
        packetCount: 142,
        anomalousEntropy: 0.86
      },
      {
        id: 'E2',
        source: 'HASH:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855',
        target: 'IP:185.220.101.5',
        weight: 6.2,
        vectorType: 'ENCRYPTED_C2',
        bytesTransferred: 1048576,
        packetCount: 890,
        anomalousEntropy: 0.94
      },
      {
        id: 'E3',
        source: 'ENDPOINT:WS-ENG-DEV-12',
        target: 'CVE:CVE-2024-38077',
        weight: 4.0,
        vectorType: 'LATERAL_SMB',
        bytesTransferred: 524288,
        packetCount: 312,
        anomalousEntropy: 0.79
      },
      {
        id: 'E4',
        source: 'CVE:CVE-2024-38077',
        target: 'ENDPOINT:DB-PROD-CORE-01',
        weight: 5.5,
        vectorType: 'LATERAL_SMB',
        bytesTransferred: 4194304,
        packetCount: 2210,
        anomalousEntropy: 0.72
      },
      {
        id: 'E5',
        source: 'ENDPOINT:DB-PROD-CORE-01',
        target: 'DOMAIN:telemetry-sync-cdn.top',
        weight: 7.8,
        vectorType: 'DNS_TUNNEL',
        bytesTransferred: 8388608,
        packetCount: 4500,
        anomalousEntropy: 0.97
      },
      {
        id: 'E6',
        source: 'ENDPOINT:WS-CORP-FIN-04',
        target: 'DOMAIN:telemetry-sync-cdn.top',
        weight: 4.8,
        vectorType: 'BEACON',
        bytesTransferred: 131072,
        packetCount: 220,
        anomalousEntropy: 0.88
      },
      {
        id: 'E7',
        source: 'ENDPOINT:WS-CORP-FIN-04',
        target: 'IP:198.51.100.42',
        weight: 1.2,
        vectorType: 'SSH_INFILTRATION',
        bytesTransferred: 65536,
        packetCount: 88,
        anomalousEntropy: 0.22
      },
      {
        id: 'E8',
        source: 'ENDPOINT:WS-ENG-DEV-12',
        target: 'IP:198.51.100.42',
        weight: 1.0,
        vectorType: 'SSH_INFILTRATION',
        bytesTransferred: 40960,
        packetCount: 60,
        anomalousEntropy: 0.18
      }
    ];

    for (const e of seedEdges) {
      this.edges.set(e.id!, {
        id: e.id!,
        source: e.source!,
        target: e.target!,
        weight: e.weight || 1.0,
        vectorType: e.vectorType || 'BEACON',
        bytesTransferred: e.bytesTransferred || 1024,
        packetCount: e.packetCount || 10,
        anomalousEntropy: e.anomalousEntropy || 0.5,
        timestamp: timestamp
      });
    }
  }

  // ==========================================================================
  // PAGERANK APPROXIMATION ALGORITHM (POWER ITERATION WITH DAMPING)
  // ==========================================================================
  public runPageRank(dampingFactor = 0.85, maxIterations = 30, tolerance = 1e-5): PageRankIterationMetrics {
    const startTime = performance.now();
    const nodeIds = Array.from(this.vertices.keys());
    const N = nodeIds.length;

    if (N === 0) {
      return { iterations: 0, deltaL1: 0, converged: true, durationMs: 0 };
    }

    // Reset degree counters
    const inDegrees = new Map<string, number>();
    const outDegrees = new Map<string, number>();
    const weightedInDegrees = new Map<string, number>();
    const weightedOutDegrees = new Map<string, number>();

    // Adjacency maps for incoming & outgoing directed edges
    const incomingEdges = new Map<string, { source: string; weight: number }[]>();
    const outgoingWeights = new Map<string, number>();

    for (const id of nodeIds) {
      inDegrees.set(id, 0);
      outDegrees.set(id, 0);
      weightedInDegrees.set(id, 0);
      weightedOutDegrees.set(id, 0);
      incomingEdges.set(id, []);
      outgoingWeights.set(id, 0);
    }

    for (const edge of this.edges.values()) {
      if (!this.vertices.has(edge.source) || !this.vertices.has(edge.target)) continue;

      // Increment degrees
      outDegrees.set(edge.source, (outDegrees.get(edge.source) || 0) + 1);
      inDegrees.set(edge.target, (inDegrees.get(edge.target) || 0) + 1);
      weightedOutDegrees.set(edge.source, (weightedOutDegrees.get(edge.source) || 0) + edge.weight);
      weightedInDegrees.set(edge.target, (weightedInDegrees.get(edge.target) || 0) + edge.weight);

      // Record incoming linkage
      incomingEdges.get(edge.target)!.push({ source: edge.source, weight: edge.weight });
      outgoingWeights.set(edge.source, (outgoingWeights.get(edge.source) || 0) + edge.weight);
    }

    // Initialize PageRank vectors uniformly: PR_0(u) = 1 / N
    let rank = new Map<string, number>();
    for (const id of nodeIds) {
      rank.set(id, 1 / N);
    }

    let iterations = 0;
    let deltaL1 = 1.0;
    let converged = false;

    while (iterations < maxIterations && deltaL1 > tolerance) {
      iterations++;
      const nextRank = new Map<string, number>();

      // Sum rank of dangling nodes (nodes with 0 outgoing weight)
      let danglingSum = 0;
      for (const id of nodeIds) {
        if ((outgoingWeights.get(id) || 0) === 0) {
          danglingSum += rank.get(id) || 0;
        }
      }

      // Base teleportation probability
      const baseTeleport = (1 - dampingFactor) / N + (dampingFactor * danglingSum) / N;

      deltaL1 = 0;
      for (const id of nodeIds) {
        let incomingContribution = 0;
        const inLinks = incomingEdges.get(id) || [];

        for (const link of inLinks) {
          const totalOutWeight = outgoingWeights.get(link.source) || 1;
          const sourceRank = rank.get(link.source) || 0;
          incomingContribution += sourceRank * (link.weight / totalOutWeight);
        }

        const newScore = baseTeleport + dampingFactor * incomingContribution;
        nextRank.set(id, newScore);
        deltaL1 += Math.abs(newScore - (rank.get(id) || 0));
      }

      rank = nextRank;
    }

    converged = deltaL1 <= tolerance;
    const durationMs = Math.round((performance.now() - startTime) * 100) / 100;

    // Find maximum rank to normalize centrality to [0, 1]
    let maxRank = 0.00001;
    for (const val of rank.values()) {
      if (val > maxRank) maxRank = val;
    }

    // Compute clustering coefficients and assign finalized reputation scores
    for (const id of nodeIds) {
      const v = this.vertices.get(id)!;
      const rawScore = rank.get(id) || 0;
      const centralityNormalized = Math.min(1.0, rawScore / maxRank);

      v.inDegree = inDegrees.get(id) || 0;
      v.outDegree = outDegrees.get(id) || 0;
      v.weightedInDegree = Math.round((weightedInDegrees.get(id) || 0) * 10) / 10;
      v.weightedOutDegree = Math.round((weightedOutDegrees.get(id) || 0) * 10) / 10;
      v.centralityScore = Math.round(centralityNormalized * 1000) / 1000;

      // Calculate clustering coefficient
      v.clusteringCoefficient = this.calculateClusteringCoefficient(id);

      // Automated C2 & Threat Detection Heuristic:
      // High centrality + incoming attacks from multiple sources, or classified C2
      const isHighCentrality = v.centralityScore >= 0.70;
      const hasSignificantInbound = v.inDegree >= 2 && v.weightedInDegree >= 4.0;
      const isKnownThreatCategory =
        v.category === 'C2_CONTROLLER' ||
        v.category === 'EXFILTRATION_SINK' ||
        v.category === 'MALWARE_DROPPER';

      v.isC2Node = (isHighCentrality && hasSignificantInbound) || v.category === 'C2_CONTROLLER';

      // Detect cluster anomaly: tight sink or abnormal clustering coefficient
      v.isAnomalousCluster =
        (v.clusteringCoefficient > 0.65 && v.inDegree >= 2) ||
        (v.inDegree >= 3 && v.outDegree === 0 && v.threatWeight >= 0.5);

      // Composite dynamic reputation score (0 - 100)
      const baseThreat = v.threatWeight * 40;
      const centralityFactor = v.centralityScore * 40;
      const clusterPenalty = v.isAnomalousCluster ? 12 : 0;
      const c2Penalty = v.isC2Node ? 8 : 0;

      v.reputationScore = Math.min(
        100,
        Math.max(1, Math.round(baseThreat + centralityFactor + clusterPenalty + c2Penalty))
      );
    }

    this.lastMetrics = { iterations, deltaL1, converged, durationMs };
    this.notify();
    return this.lastMetrics;
  }

  // ==========================================================================
  // CLUSTERING COEFFICIENT CALCULATION
  // ==========================================================================
  private calculateClusteringCoefficient(vertexId: string): number {
    const neighbors = new Set<string>();

    // Collect all direct in/out neighbors
    for (const e of this.edges.values()) {
      if (e.source === vertexId) neighbors.add(e.target);
      if (e.target === vertexId) neighbors.add(e.source);
    }

    const k = neighbors.size;
    if (k < 2) return 0.0;

    let existingEdgesBetweenNeighbors = 0;
    const neighborArray = Array.from(neighbors);

    for (let i = 0; i < neighborArray.length; i++) {
      for (let j = 0; j < neighborArray.length; j++) {
        if (i === j) continue;
        const u = neighborArray[i];
        const v = neighborArray[j];
        for (const e of this.edges.values()) {
          if (e.source === u && e.target === v) {
            existingEdgesBetweenNeighbors++;
            break;
          }
        }
      }
    }

    // Directed graph maximum possible edges = k * (k - 1)
    const maxPossible = k * (k - 1);
    return Math.round((existingEdgesBetweenNeighbors / maxPossible) * 1000) / 1000;
  }

  // ==========================================================================
  // ZERO-DAY C2 ATTACK INJECTION & TOPOLOGY MUTATION
  // ==========================================================================
  public injectZeroDayC2Vector(): NeuralVertex {
    const randomHex = Math.random().toString(16).slice(2, 10);
    const zeroDayId = `C2:ZERO-DAY-${randomHex.toUpperCase()}`;
    const timestamp = new Date().toISOString();

    const zeroDayNode: NeuralVertex = {
      id: zeroDayId,
      label: `Zero-Day C2 Node (${randomHex})`,
      type: 'IP',
      category: 'C2_CONTROLLER',
      threatWeight: 0.99,
      centralityScore: 0.0,
      reputationScore: 99,
      inDegree: 0,
      outDegree: 0,
      weightedInDegree: 0,
      weightedOutDegree: 0,
      isC2Node: true,
      isAnomalousCluster: true,
      clusteringCoefficient: 0,
      lastObserved: timestamp,
      associatedIncidents: ['ZERO_DAY_BURST_TACTIC', 'UNCLASSIFIED_EXFIL'],
      geoMetadata: { country: 'KP', asn: 'AS131279' }
    };

    this.vertices.set(zeroDayId, zeroDayNode);

    // Form directed high-volume attack vectors from internal endpoints to this zero-day
    const internalEndpoints = Array.from(this.vertices.values()).filter(
      v => v.category === 'BENIGN_INTERNAL' || v.type === 'ENDPOINT'
    );

    let edgeCounter = 1;
    for (const ep of internalEndpoints) {
      const edgeId = `ZD_EDGE_${randomHex}_${edgeCounter++}`;
      this.edges.set(edgeId, {
        id: edgeId,
        source: ep.id,
        target: zeroDayId,
        weight: 8.5,
        vectorType: 'ENCRYPTED_C2',
        bytesTransferred: 5242880,
        packetCount: 3100,
        anomalousEntropy: 0.98,
        timestamp
      });
    }

    // Run PageRank to recalculate global topology and centrality instantly
    this.runPageRank();
    return this.vertices.get(zeroDayId)!;
  }

  public resetToBaseline() {
    this.vertices.clear();
    this.edges.clear();
    this.seedBaselineThreatTopology();
    this.runPageRank();
  }

  // ==========================================================================
  // QUERIES & ANOMALY REPORTS
  // ==========================================================================
  public getSnapshot(): NeuralGraphSnapshot {
    const vertList = Array.from(this.vertices.values());
    const edgeList = Array.from(this.edges.values());

    const detectedC2Count = vertList.filter(v => v.isC2Node).length;
    const anomalousClusterCount = vertList.filter(v => v.isAnomalousCluster).length;

    let highestRisk: NeuralVertex | null = null;
    for (const v of vertList) {
      if (!highestRisk || v.reputationScore > highestRisk.reputationScore) {
        highestRisk = v;
      }
    }

    return {
      vertices: vertList,
      edges: edgeList,
      metrics: this.lastMetrics,
      detectedC2Count,
      anomalousClusterCount,
      highestRiskVertex: highestRisk,
      lastCalculated: new Date().toISOString()
    };
  }

  public getC2Nodes(): NeuralVertex[] {
    return Array.from(this.vertices.values()).filter(v => v.isC2Node);
  }

  public getClusterAnomalies(): ClusterAnomalyReport[] {
    const reports: ClusterAnomalyReport[] = [];
    for (const v of this.vertices.values()) {
      if (v.isAnomalousCluster || v.isC2Node) {
        reports.push({
          vertexId: v.id,
          vertexLabel: v.label,
          anomalyType: v.isC2Node ? 'C2_CONVERGENCE' : 'DENSE_EXFIL_CLIQUE',
          clusteringCoefficient: v.clusteringCoefficient,
          centralityScore: v.centralityScore,
          riskAssessment: `Eigenvector centrality: ${v.centralityScore.toFixed(3)}, Inbound Volume: ${v.weightedInDegree}`
        });
      }
    }
    return reports;
  }

  /**
   * Returns a structured report of the top 5 (or specified limit) highest-centrality nodes,
   * including their individual reputation scores, connection counts (in, out, total),
   * and threat metadata, formatted for immediate rendering in a dashboard widget.
   */
  public getTopCentralityReport(limit: number = 5): TopCentralityWidgetReport {
    const sorted = Array.from(this.vertices.values())
      .sort((a, b) => b.centralityScore - a.centralityScore || b.reputationScore - a.reputationScore)
      .slice(0, limit)
      .map(v => ({
        id: v.id,
        label: v.label,
        type: v.type,
        category: v.category,
        centralityScore: v.centralityScore,
        reputationScore: v.reputationScore,
        inDegree: v.inDegree,
        outDegree: v.outDegree,
        totalConnections: v.inDegree + v.outDegree,
        weightedTotalDegree: Math.round((v.weightedInDegree + v.weightedOutDegree) * 10) / 10,
        isC2Node: v.isC2Node,
        threatWeight: v.threatWeight
      }));

    return {
      generatedAt: new Date().toISOString(),
      totalEvaluatedNodes: this.vertices.size,
      topNodes: sorted
    };
  }

  /**
   * Convenience accessor returning the top 5 highest-centrality node items directly.
   */
  public getTopCentralityNodes(limit: number = 5): HighCentralityNodeReportItem[] {
    return this.getTopCentralityReport(limit).topNodes;
  }

  // Reactive subscription
  public subscribe(callback: (snapshot: NeuralGraphSnapshot) => void): () => void {
    this.listeners.add(callback);
    callback(this.getSnapshot());
    return () => this.listeners.delete(callback);
  }

  private notify() {
    const snap = this.getSnapshot();
    for (const cb of this.listeners) {
      try {
        cb(snap);
      } catch (err) {
        console.error('[NeuralGraphMeshEngine] Listener exception:', err);
      }
    }
  }
}

export const NeuralGraphMeshEngine = new NeuralGraphMeshEngineService();
