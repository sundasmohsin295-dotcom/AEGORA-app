package com.example.intelligence

import com.example.capability.*
import com.example.data.DemonstratedCapabilityRepository
import com.example.data.db.CapabilityEvidenceEntity
import com.example.data.db.MasteryStatus
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

/**
 * AEGORA PERSONAL INTELLIGENCE & CAPABILITY PLATFORM ENGINE
 * Core engine powering the Personal Intelligence Profile, Causal Capability Graph,
 * Failure Intelligence, Learning Memory, "Prove It" Verification, and Real-World Operational Simulations.
 */
object PersonalIntelligencePlatformEngine {

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

  // ============================================================
  // 0. LEARNER SCOPING & PROFILE UI STATE
  // ============================================================
  private val _currentLearnerId = MutableStateFlow<String?>("operator_7x")
  val currentLearnerId: StateFlow<String?> = _currentLearnerId.asStateFlow()

  private val _profileUiState = MutableStateFlow<PersonalIntelligenceUiState>(
    PersonalIntelligenceUiState.Ready(
      learnerId = "operator_7x",
      clusterSummaries = createInitialClusterSummaries(),
      primaryBottleneckCluster = CognitiveClusterType.GENERALIZATION_STRESS
    )
  )
  val profileUiState: StateFlow<PersonalIntelligenceUiState> = _profileUiState.asStateFlow()

  // ============================================================
  // 1. PERSONAL INTELLIGENCE PROFILE (4 COGNITIVE CLUSTERS)
  // ============================================================
  private val _clusterSummaries = MutableStateFlow(createInitialClusterSummaries())
  val clusterSummaries: StateFlow<List<ClusterCapabilitySummary>> = _clusterSummaries.asStateFlow()

  // ============================================================
  // 2. CAUSAL CAPABILITY GRAPH
  // ============================================================
  private val _capabilityNodes = MutableStateFlow(createInitialCapabilityNodes())
  val capabilityNodes: StateFlow<List<CapabilityDependencyNode>> = _capabilityNodes.asStateFlow()

  private val _selectedNodeId = MutableStateFlow<String?>("siem_investigation")
  val selectedNodeId: StateFlow<String?> = _selectedNodeId.asStateFlow()

  fun selectCapabilityNode(id: String) {
    _selectedNodeId.value = id
  }

  // ============================================================
  // 3. FAILURE INTELLIGENCE SYSTEM
  // ============================================================
  private val _mistakeRecords = MutableStateFlow(createInitialMistakeRecords())
  val mistakeRecords: StateFlow<List<MistakeIntelligenceRecord>> = _mistakeRecords.asStateFlow()

  fun recordMistake(
    missionTitle: String,
    failureType: FailureModeType,
    observedSymptom: String,
    rootCauseCausalLink: String,
    constructiveFeedback: String,
    remediationMission: String
  ) {
    val newRecord = MistakeIntelligenceRecord(
      timestamp = dateFormat.format(Date()),
      missionTitle = missionTitle,
      failureType = failureType,
      observedSymptom = observedSymptom,
      rootCauseCausalLink = rootCauseCausalLink,
      constructiveFeedback = constructiveFeedback,
      targetedRemediationMission = remediationMission,
      capabilityImpactLabel = "Classified as ${failureType.label} evidence"
    )
    _mistakeRecords.update { listOf(newRecord) + it }
  }

  // ============================================================
  // 4. LONG-TERM LEARNING MEMORY & RETENTION
  // ============================================================
  private val _learningMemory = MutableStateFlow(createInitialLearningMemory())
  val learningMemory: StateFlow<List<LearningMemoryItem>> = _learningMemory.asStateFlow()

  // ============================================================
  // 5. "PROVE IT" ON-DEMAND VERIFICATION SYSTEM
  // ============================================================
  private val _proveItChallenges = MutableStateFlow(createInitialProveItChallenges())
  val proveItChallenges: StateFlow<List<ProveItChallenge>> = _proveItChallenges.asStateFlow()

  private val _verifiedCapabilityProofs = MutableStateFlow(createInitialVerifiedProofs())
  val verifiedCapabilityProofs: StateFlow<List<EvidenceProofItem>> = _verifiedCapabilityProofs.asStateFlow()

  fun completeProveItChallenge(challengeId: String, selectedOptionId: String): Boolean {
    val challenge = _proveItChallenges.value.find { it.id == challengeId } ?: return false
    val option = challenge.triageOptions.find { it.id == selectedOptionId } ?: return false

    if (option.isOptimal) {
      val verifiedProof = EvidenceProofItem(
        capabilityId = challenge.id,
        capabilityName = challenge.capabilityName,
        proofType = "Verified Telemetry Demonstration",
        timestamp = dateFormat.format(Date()),
        freshnessDays = 0,
        telemetrySnippet = challenge.rawTelemetryLog.take(90) + "...",
        verifiedHash = challenge.verifiedCryptographicProof,
        confidenceScore = 96,
        isCryptographicallySigned = true
      )
      _verifiedCapabilityProofs.update { listOf(verifiedProof) + it }

      // Update Cluster Score positively
      _clusterSummaries.update { list ->
        list.map { summary ->
          if (summary.cluster == challenge.cluster) {
            summary.copy(
              score = (summary.score + 3).coerceAtMost(100),
              recentEvidence = listOf(verifiedProof) + summary.recentEvidence
            )
          } else summary
        }
      }

      // Update Capability Graph Node
      _capabilityNodes.update { nodes ->
        nodes.map { node ->
          if (node.id == challenge.id || node.name.contains(challenge.capabilityName, ignoreCase = true)) {
            node.copy(
              demonstratedScore = (node.demonstratedScore + 6).coerceAtMost(100),
              status = CapabilityNodeStatus.VERIFIED
            )
          } else node
        }
      }
      return true
    } else {
      // Record failure mode constructively
      option.failureModeIfChosen?.let { fMode ->
        recordMistake(
          missionTitle = "Prove It: ${challenge.capabilityName}",
          failureType = fMode,
          observedSymptom = option.justification,
          rootCauseCausalLink = "Chosen telemetry triage option diverted from ground-truth persistence evidence.",
          constructiveFeedback = "Review raw process command-line parentage before concluding benign activity.",
          remediationMission = "Sysmon Parent-Child Process Correlation (8 mins)"
        )
      }
      return false
    }
  }

  fun addVerifiedProof(
    proof: EvidenceProofItem,
    targetCluster: CognitiveClusterType = CognitiveClusterType.ACTIVE_DEFENSE
  ) {
    _verifiedCapabilityProofs.update { listOf(proof) + it }
    _clusterSummaries.update { list ->
      list.map { summary ->
        if (summary.cluster == targetCluster) {
          summary.copy(
            score = (summary.score + 3).coerceAtMost(100),
            recentEvidence = listOf(proof) + summary.recentEvidence
          )
        } else summary
      }
    }
  }

  // ============================================================
  // 6. CAREER INTELLIGENCE LAYER (8 CYBER ROLES)
  // ============================================================
  private val _careerProfiles = MutableStateFlow(createInitialCareerProfiles())
  val careerProfiles: StateFlow<List<CareerIntelligenceProfile>> = _careerProfiles.asStateFlow()

  // ============================================================
  // 7. REAL-WORLD OPERATIONAL SIMULATION MODES
  // ============================================================
  private val _operationalScenarios = MutableStateFlow(createInitialOperationalScenarios())
  val operationalScenarios: StateFlow<List<OperationalEnvironmentScenario>> = _operationalScenarios.asStateFlow()

  // ============================================================
  // 8. MULTIPLAYER COLLABORATIVE INCIDENT (ISOLATED)
  // ============================================================
  private val _collaborativeSession = MutableStateFlow(createInitialCollaborativeSession())
  val collaborativeSession: StateFlow<CollaborativeIncidentSession> = _collaborativeSession.asStateFlow()

  // ============================================================
  // 9. PROGRESS NARRATIVE TIMELINE
  // ============================================================
  private val _progressMilestones = MutableStateFlow(createInitialProgressMilestones())
  val progressMilestones: StateFlow<List<ProgressNarrativeMilestone>> = _progressMilestones.asStateFlow()

  // ============================================================
  // 10. DISCOVERY ENGINE (STRENGTHS, DEPENDENCIES, UNKNOWNS)
  // ============================================================
  private val _discoveryInsights = MutableStateFlow(createInitialDiscoveries())
  val discoveryInsights: StateFlow<List<DiscoveryInsightItem>> = _discoveryInsights.asStateFlow()

  // ============================================================
  // 11. TRUST CENTER & EXPLAINABILITY
  // ============================================================
  private val _trustAudits = MutableStateFlow(createInitialTrustAudits())
  val trustAudits: StateFlow<List<TrustAuditItem>> = _trustAudits.asStateFlow()

  // ============================================================
  // 12. UNIVERSITY & EMPLOYER ARCHITECTURES
  // ============================================================
  private val _universityAnalytics = MutableStateFlow(createInitialUniversityAnalytics())
  val universityAnalytics: StateFlow<UniversityCohortAnalytics> = _universityAnalytics.asStateFlow()

  private val _employerVerification = MutableStateFlow(createInitialEmployerVerification())
  val employerVerification: StateFlow<EmployerCandidateVerification> = _employerVerification.asStateFlow()

  // ============================================================
  // DYNAMIC CAPABILITY PIPELINE INTEGRATION & LEARNER ISOLATION
  // ============================================================

  /**
   * Evaluates and updates the Personal Intelligence Profile from authoritative Demonstrated Capability results.
   * Strictly enforces learner isolation, deterministic cluster score calculation,
   * honest empty state handling, and 4-cluster explainability.
   */
  fun evaluateAndLoadProfile(
    learnerId: String,
    capabilityResults: List<CapabilityAssessmentResult>,
    allEvidence: List<CapabilityEvidenceEntity> = emptyList()
  ) {
    require(learnerId.isNotBlank()) { "Learner ID cannot be blank" }

    // Strict learner isolation check: prevent cross-learner leakage
    for (result in capabilityResults) {
      require(result.learnerId == learnerId) {
        "Learner isolation violation: Result learnerId '${result.learnerId}' does not match expected '$learnerId'"
      }
    }
    for (evidence in allEvidence) {
      require(evidence.learnerId == learnerId) {
        "Learner isolation violation: Evidence learnerId '${evidence.learnerId}' does not match expected '$learnerId'"
      }
    }

    _currentLearnerId.value = learnerId

    // Honest empty state when no capability data or evidence exists
    if (capabilityResults.isEmpty() && allEvidence.isEmpty()) {
      _clusterSummaries.value = createEmptyClusterSummaries(learnerId)
      _capabilityNodes.value = emptyList()
      _mistakeRecords.value = emptyList()
      _learningMemory.value = emptyList()
      _verifiedCapabilityProofs.value = emptyList()
      _profileUiState.value = PersonalIntelligenceUiState.Empty(learnerId = learnerId)
      return
    }

    // 1. Group authoritative gate scores for the 4 Cognitive Clusters
    val understandScores = capabilityResults.mapNotNull { it.gateResults[MasteryGateType.UNDERSTAND]?.score }
    val recallScores = capabilityResults.mapNotNull { it.gateResults[MasteryGateType.RECALL]?.score }
    val applyScores = capabilityResults.mapNotNull { it.gateResults[MasteryGateType.APPLY]?.score }
    val investigateScores = capabilityResults.mapNotNull { it.gateResults[MasteryGateType.INVESTIGATE]?.score }
    val transferScores = capabilityResults.mapNotNull { it.gateResults[MasteryGateType.TRANSFER]?.score }
    val uncertaintyScores = capabilityResults.mapNotNull { it.gateResults[MasteryGateType.UNCERTAINTY_RESILIENCE]?.score }
    val explainScores = capabilityResults.mapNotNull { it.gateResults[MasteryGateType.EXPLAIN]?.score }
    val independenceScores = capabilityResults.map { it.evidenceQualityBreakdown.independenceScore }

    // Calculate deterministic scores safely clamped to 0..100
    val foundationScore = ((understandScores + recallScores).takeIf { it.isNotEmpty() }?.average()?.toInt() ?: 0).coerceIn(0, 100)
    val activeDefenseScore = ((applyScores + investigateScores).takeIf { it.isNotEmpty() }?.average()?.toInt() ?: 0).coerceIn(0, 100)
    val generalizationScore = ((transferScores + uncertaintyScores).takeIf { it.isNotEmpty() }?.average()?.toInt() ?: 0).coerceIn(0, 100)
    val metacognitiveScore = ((explainScores + independenceScores).takeIf { it.isNotEmpty() }?.average()?.toInt() ?: 0).coerceIn(0, 100)

    // Map evidence items
    val proofItems = allEvidence.map { ev ->
      val capName = capabilityResults.find { it.capabilityId == ev.capabilityId }?.name ?: "Verified Telemetry"
      val daysAgo = ((System.currentTimeMillis() - ev.createdAt) / (24L * 60 * 60 * 1000)).toInt().coerceAtLeast(0)
      val displayHash = if (ev.evidenceHash.length > 16) ev.evidenceHash.take(16) + "..." else ev.evidenceHash
      EvidenceProofItem(
        id = ev.id,
        capabilityId = ev.capabilityId,
        capabilityName = capName,
        proofType = ev.evidenceType.replace("_", " "),
        timestamp = dateFormat.format(Date(ev.createdAt)),
        freshnessDays = daysAgo,
        telemetrySnippet = "Artifact [${ev.evidenceType}] • Complexity: ${ev.complexity}% • Independence: ${ev.independenceScore}%",
        verifiedHash = displayHash,
        confidenceScore = ev.evidenceQualityScore.coerceIn(0, 100),
        isCryptographicallySigned = (ev.verificationStatus == com.example.data.db.VerificationStatus.VERIFIED.name)
      )
    }
    _verifiedCapabilityProofs.value = proofItems

    val clusterMap = mapOf(
      CognitiveClusterType.FOUNDATION to foundationScore,
      CognitiveClusterType.ACTIVE_DEFENSE to activeDefenseScore,
      CognitiveClusterType.GENERALIZATION_STRESS to generalizationScore,
      CognitiveClusterType.METACOGNITIVE_STRATEGIC to metacognitiveScore
    )

    val summaries = CognitiveClusterType.entries.map { cluster ->
      val score = clusterMap[cluster] ?: 0
      val passingCaps = capabilityResults.filter { it.isDemonstrated }
      val limitingCaps = capabilityResults.filter { !it.isDemonstrated }

      val whatLearnerKnows = when (cluster) {
        CognitiveClusterType.FOUNDATION -> passingCaps.map { "Solid foundation in ${it.name}" }.ifEmpty { listOf("Protocol and architecture conceptual models") }
        CognitiveClusterType.ACTIVE_DEFENSE -> passingCaps.map { "Triage and response in ${it.name}" }.ifEmpty { listOf("Endpoint and telemetry investigation") }
        CognitiveClusterType.GENERALIZATION_STRESS -> passingCaps.map { "Stress-resilient execution in ${it.name}" }.ifEmpty { listOf("Multi-environment telemetry analysis") }
        CognitiveClusterType.METACOGNITIVE_STRATEGIC -> passingCaps.map { "Calibrated reasoning in ${it.name}" }.ifEmpty { listOf("Confidence calibration and root cause deduction") }
      }

      val whatLearnerCanPerform = capabilityResults.filter { it.currentConfidence >= 65 }.map { "Perform validated workflows in ${it.name}" }.ifEmpty {
        listOf("Initial diagnostic execution")
      }

      val strugglePoints = limitingCaps.flatMap { it.limitingFactors }.distinct().take(4).ifEmpty {
        if (score < 60) listOf("Awaiting verified high-independence telemetry") else listOf("No active critical struggle points observed")
      }

      val untransferredConcepts = capabilityResults.filter { (it.gateResults[MasteryGateType.TRANSFER]?.score ?: 100) < 70 }.map {
        "Cross-environment transfer for ${it.name} (${it.gateResults[MasteryGateType.TRANSFER]?.score ?: 0}/100)"
      }.ifEmpty { listOf("All tested capabilities transferred successfully across environments") }

      val uncertaintyFrontiers = capabilityResults.filter { it.retentionRisk == RetentionRisk.HIGH || it.retentionRisk == RetentionRisk.CRITICAL }.map {
        "Retention decay risk on ${it.name} (${it.daysSinceLastVerified}d since verified)"
      }.ifEmpty { listOf("Recent verifications are fresh and well-calibrated") }

      val topGrowthMissions = limitingCaps.map { it.recommendedAction }.distinct().take(3).ifEmpty {
        listOf("Advanced Multi-Cloud Adversary Emulation", "Zero-Day Exploit Root Cause Lab")
      }

      val plainMeaning = when (cluster) {
        CognitiveClusterType.FOUNDATION -> "Foundation $score/100 measures core protocols and theoretical models. Evaluated across ${understandScores.size + recallScores.size} mastery gates."
        CognitiveClusterType.ACTIVE_DEFENSE -> "Active Defense $score/100 measures hands-on SIEM triage, endpoint containment, and lab execution across ${applyScores.size + investigateScores.size} mastery gates."
        CognitiveClusterType.GENERALIZATION_STRESS -> "Generalization & Stress $score/100 measures cross-stack transfer and decision-making under uncertainty."
        CognitiveClusterType.METACOGNITIVE_STRATEGIC -> "Metacognitive & Strategic $score/100 measures root cause explanation quality and independence score."
      }

      ClusterCapabilitySummary(
        cluster = cluster,
        score = score,
        trendLabel = if (score >= 70) "Strong Demonstration" else if (score >= 40) "Developing Demonstration" else "Emerging Baseline",
        plainEnglishMeaning = plainMeaning,
        whatLearnerKnows = whatLearnerKnows,
        whatLearnerCanPerform = whatLearnerCanPerform,
        strugglePoints = strugglePoints,
        untransferredConcepts = untransferredConcepts,
        uncertaintyFrontiers = uncertaintyFrontiers,
        recentEvidence = proofItems.take(2),
        learningPatterns = listOf("Demonstrated capability engine verified across ${capabilityResults.size} skills."),
        topGrowthMissions = topGrowthMissions
      )
    }
    _clusterSummaries.value = summaries

    // Derive Causal Capability Graph Nodes directly from CapabilityAssessmentResults
    val nodes = capabilityResults.map { result ->
      val clusterType = when {
        result.category.contains("FOUNDATION", ignoreCase = true) || result.category.contains("NETWORK", ignoreCase = true) -> CognitiveClusterType.FOUNDATION
        result.category.contains("SIEM", ignoreCase = true) || result.category.contains("IAM", ignoreCase = true) || result.category.contains("ENDPOINT", ignoreCase = true) -> CognitiveClusterType.ACTIVE_DEFENSE
        result.category.contains("TRANSFER", ignoreCase = true) || result.category.contains("STRESS", ignoreCase = true) -> CognitiveClusterType.GENERALIZATION_STRESS
        else -> CognitiveClusterType.METACOGNITIVE_STRATEGIC
      }
      val nodeStatus = when {
        result.isDemonstrated -> CapabilityNodeStatus.VERIFIED
        result.limitingGate != null && result.currentConfidence < 60 -> CapabilityNodeStatus.BOTTLENECK
        result.retentionRisk == RetentionRisk.HIGH || result.retentionRisk == RetentionRisk.CRITICAL -> CapabilityNodeStatus.AT_RISK
        result.currentConfidence >= 45 -> CapabilityNodeStatus.EMERGING
        else -> CapabilityNodeStatus.BLOCKED
      }
      CapabilityDependencyNode(
        id = result.capabilityId,
        name = result.name,
        cluster = clusterType,
        demonstratedScore = result.currentConfidence.coerceIn(0, 100),
        status = nodeStatus,
        upstreamDependencyIds = emptyList(),
        downstreamImpactIds = emptyList(),
        downstreamImpactSummary = result.whyThisScore,
        failureRateDownstream = ((100 - result.currentConfidence) / 2).coerceIn(0, 100),
        telemetryRequirement = "Verified proofs: ${result.evidenceQualityBreakdown.verifiedCount} (Quality: ${result.evidenceQualityBreakdown.effectiveQualityScore}%)",
        recommendedMissionId = result.recommendedAction
      )
    }
    _capabilityNodes.value = nodes.ifEmpty { createInitialCapabilityNodes() }

    // Identify primary bottleneck cluster and partial evidence status
    val bottleneckCluster = summaries.minByOrNull { it.score }?.cluster
    val isPartial = capabilityResults.size < 4 || capabilityResults.any { it.overallMasteryStatus == MasteryStatus.NOT_STARTED }
    val partialWarning = if (isPartial) "Partial evidence detected: Profile reflects initial demonstrations. Complete cross-stack missions to solidify all mastery gates." else null

    _profileUiState.value = PersonalIntelligenceUiState.Ready(
      learnerId = learnerId,
      clusterSummaries = summaries,
      primaryBottleneckCluster = bottleneckCluster,
      isPartialEvidence = isPartial,
      partialEvidenceWarning = partialWarning
    )
  }

  /**
   * Suspends to load all capabilities and evidence for a learner from Room and updates the profile.
   */
  suspend fun loadProfileForLearner(
    learnerId: String,
    repository: DemonstratedCapabilityRepository,
    engine: DemonstratedCapabilityEngine = DemonstratedCapabilityEngine(),
    currentTime: Long = System.currentTimeMillis()
  ) {
    require(learnerId.isNotBlank()) { "Learner ID cannot be blank" }
    _profileUiState.value = PersonalIntelligenceUiState.Loading

    try {
      val results = engine.evaluateAllForLearner(
        learnerId = learnerId,
        repository = repository,
        currentTime = currentTime
      )
      val evidenceList = repository.observeEvidenceForLearner(learnerId).first()
      evaluateAndLoadProfile(
        learnerId = learnerId,
        capabilityResults = results,
        allEvidence = evidenceList
      )
    } catch (e: Exception) {
      _profileUiState.value = PersonalIntelligenceUiState.Error(
        message = e.message ?: "Failed to evaluate intelligence profile for learner $learnerId",
        recoverableAction = "Retry Capability Evaluation"
      )
    }
  }

  /**
   * Resets the profile to an honest empty state for a new learner with no recorded telemetry.
   */
  fun resetToEmptyState(learnerId: String) {
    require(learnerId.isNotBlank()) { "Learner ID cannot be blank" }
    _currentLearnerId.value = learnerId
    _clusterSummaries.value = createEmptyClusterSummaries(learnerId)
    _capabilityNodes.value = emptyList()
    _mistakeRecords.value = emptyList()
    _learningMemory.value = emptyList()
    _verifiedCapabilityProofs.value = emptyList()
    _profileUiState.value = PersonalIntelligenceUiState.Empty(learnerId = learnerId)
  }

  /**
   * Restores default demonstration baseline for interactive preview.
   */
  fun resetToDefaultDemonstrationState() {
    _currentLearnerId.value = "operator_7x"
    val defaultSummaries = createInitialClusterSummaries()
    _clusterSummaries.value = defaultSummaries
    _capabilityNodes.value = createInitialCapabilityNodes()
    _mistakeRecords.value = createInitialMistakeRecords()
    _learningMemory.value = createInitialLearningMemory()
    _verifiedCapabilityProofs.value = createInitialVerifiedProofs()
    _profileUiState.value = PersonalIntelligenceUiState.Ready(
      learnerId = "operator_7x",
      clusterSummaries = defaultSummaries,
      primaryBottleneckCluster = CognitiveClusterType.GENERALIZATION_STRESS
    )
  }

  /**
   * Creates empty cluster summaries for honest zero-evidence states.
   */
  fun createEmptyClusterSummaries(learnerId: String): List<ClusterCapabilitySummary> {
    return CognitiveClusterType.entries.map { cluster ->
      ClusterCapabilitySummary(
        cluster = cluster,
        score = 0,
        trendLabel = "Awaiting initial evidence",
        plainEnglishMeaning = "AEGORA has not yet observed demonstrated evidence for ${cluster.displayName}. Complete relevant lab baselines to establish your capability baseline.",
        whatLearnerKnows = emptyList(),
        whatLearnerCanPerform = emptyList(),
        strugglePoints = listOf("No telemetry demonstrated yet"),
        untransferredConcepts = listOf("Awaiting cross-environment execution"),
        uncertaintyFrontiers = listOf("Full epistemic frontier unmapped"),
        recentEvidence = emptyList(),
        learningPatterns = listOf("Complete initial missions to discover cognitive learning patterns."),
        topGrowthMissions = listOf(
          "Network Protocol Packet Triage Baseline",
          "Sysmon & Process Lineage Diagnostic",
          "Prove It On-Demand Verification"
        )
      )
    }
  }

  // ============================================================
  // DATA FACTORY IMPLEMENTATIONS
  // ============================================================

  private fun createInitialClusterSummaries(): List<ClusterCapabilitySummary> = listOf(
    ClusterCapabilitySummary(
      cluster = CognitiveClusterType.FOUNDATION,
      score = 82,
      trendLabel = "+6% demonstrated this month",
      plainEnglishMeaning = "Foundation 82 indicates you have solid mastery of OSI protocol headers, Windows/Linux process architecture, and security syntax. You can dissect standard attack mechanics clearly.",
      whatLearnerKnows = listOf(
        "TCP three-way handshake & flag anomalies",
        "Windows Sysmon Event IDs (1, 3, 8, 10)",
        "Linux ELF binary headers & permissions",
        "MITRE ATT&CK Enterprise Matrix taxonomies"
      ),
      whatLearnerCanPerform = listOf(
        "Execute Wireshark packet filter strings",
        "Write base Snort & YARA rules with valid syntax",
        "Parse Windows security event logs via PowerShell"
      ),
      strugglePoints = listOf(
        "Memory heap structure vs stack exploitation differences",
        "Advanced Kerberos ticket encryption downgrades"
      ),
      untransferredConcepts = listOf(
        "Translating Windows privilege escalation heuristics into Kubernetes RBAC audits"
      ),
      uncertaintyFrontiers = listOf(
        "Autonomous firmware reverse engineering (no prior evidence demonstrated)"
      ),
      recentEvidence = listOf(
        EvidenceProofItem(
          capabilityId = "networking_pcap",
          capabilityName = "Protocol Packet Analysis",
          proofType = "Zeek / PCAP Live Extraction",
          timestamp = "2026-09-02",
          freshnessDays = 1,
          telemetrySnippet = "GET /beacon.php HTTP/1.1 (C2 jitter delta: 4.1s verified)",
          verifiedHash = "0x7F4A...B902",
          confidenceScore = 94
        )
      ),
      learningPatterns = listOf(
        "Learns fastest through visual packet graphs and hands-on terminal dissection."
      ),
      topGrowthMissions = listOf(
        "Deep Memory Volatility Extraction (15m)",
        "eBPF Linux Kernel Auditing (12m)"
      )
    ),
    ClusterCapabilitySummary(
      cluster = CognitiveClusterType.ACTIVE_DEFENSE,
      score = 71,
      trendLabel = "+14% triage velocity",
      plainEnglishMeaning = "Active Defense 71 means you accurately investigate and contain incidents in familiar on-premise SIEM labs. You triage false positives cleanly under routine noise.",
      whatLearnerKnows = listOf(
        "Splunk SPL & KQL query structuring",
        "Endpoint containment & network host isolation protocols",
        "Malware staging and LOLBAS execution chains"
      ),
      whatLearnerCanPerform = listOf(
        "Isolate compromised workstations within 3.5 minutes",
        "Reconstruct multi-stage attack timelines from Sysmon logs",
        "Differentiate benign admin PowerShell from obfuscated download cradles"
      ),
      strugglePoints = listOf(
        "High-volume alert queues with concurrent multi-host pivots",
        "Detecting beaconing disguised within legitimate Microsoft Teams traffic"
      ),
      untransferredConcepts = listOf(
        "Applying on-premise Active Directory lateral movement detection to AWS IAM session tokens"
      ),
      uncertaintyFrontiers = listOf(
        "ICS/SCADA Modbus protocol anomalies"
      ),
      recentEvidence = listOf(
        EvidenceProofItem(
          capabilityId = "siem_investigation",
          capabilityName = "SIEM Log Investigation",
          proofType = "Live SOC Alert Triage",
          timestamp = "2026-09-01",
          freshnessDays = 2,
          telemetrySnippet = "Correlated Sysmon Event 1 (certutil -urlcache) with Event 3 (outbound 443)",
          verifiedHash = "0x89C1...2D44",
          confidenceScore = 91
        )
      ),
      learningPatterns = listOf(
        "Performs best when starting from initial access rather than mid-chain alerts."
      ),
      topGrowthMissions = listOf(
        "CloudTrail STS AssumeRole Compromise (12m)",
        "Golden SAML Federation Triage (18m)"
      )
    ),
    ClusterCapabilitySummary(
      cluster = CognitiveClusterType.GENERALIZATION_STRESS,
      score = 54,
      trendLabel = "Primary Limiting Bottleneck",
      plainEnglishMeaning = "Generalization 54 is your primary bottleneck. While you perform well on familiar Windows logs, you experience a 42% capability drop when telemetry shifts to unfamiliar Cloud, Kubernetes, or noisy environments.",
      whatLearnerKnows = listOf(
        "Understands that attacker techniques remain invariant across environments",
        "Recognizes the theoretical concept of cloud shared responsibility"
      ),
      whatLearnerCanPerform = listOf(
        "Detect living-off-the-land binaries in standard Windows paths"
      ),
      strugglePoints = listOf(
        "Adapting fileless detection rules when executed inside Linux containers",
        "Maintaining systematic triage composure under 2-minute crisis deadlines"
      ),
      untransferredConcepts = listOf(
        "Windows Registry Run keys -> Linux systemd services & crontab persistence",
        "PsExec lateral movement -> SSH key injection in AWS EC2 user data"
      ),
      uncertaintyFrontiers = listOf(
        "Multi-cloud telemetry correlation (GCP + Azure + AWS)"
      ),
      recentEvidence = listOf(
        EvidenceProofItem(
          capabilityId = "cross_context_transfer",
          capabilityName = "Cross-Telemetry Transferability",
          proofType = "Cloud Security Drill",
          timestamp = "2026-08-30",
          freshnessDays = 4,
          telemetrySnippet = "AWS GuardDuty UnauthorizedAccess:IAMUser/InstanceCredentialExfiltration",
          verifiedHash = "0x11B3...F891",
          confidenceScore = 62
        )
      ),
      learningPatterns = listOf(
        "Relies heavily on Windows command syntax instead of core behavioral invariants."
      ),
      topGrowthMissions = listOf(
        "Cross-Environment Telemetry Translation (12m)",
        "Noisy SOC Shift Multi-Alert Triage (10m)"
      )
    ),
    ClusterCapabilitySummary(
      cluster = CognitiveClusterType.METACOGNITIVE_STRATEGIC,
      score = 68,
      trendLabel = "+9% calibration accuracy",
      plainEnglishMeaning = "Metacognitive 68 shows improving self-awareness of your own knowledge boundaries. You overconfidence rate dropped from 34% to 11%, meaning you know when to seek more evidence rather than guessing.",
      whatLearnerKnows = listOf(
        "Cognitive biases that affect SOC analysts (Premature Closure, Confirmation Bias)",
        "Executive communication framing for technical security findings"
      ),
      whatLearnerCanPerform = listOf(
        "Explicitly flag when evidence is insufficient before declaring a host clean",
        "Draft concise CISO incident briefing summaries"
      ),
      strugglePoints = listOf(
        "Reversible vs Irreversible containment decision calculus under ambiguity"
      ),
      untransferredConcepts = listOf(
        "Applying risk-based triage thresholds across differing business units"
      ),
      uncertaintyFrontiers = listOf(
        "Evaluating third-party vendor supply-chain blast radius"
      ),
      recentEvidence = listOf(
        EvidenceProofItem(
          capabilityId = "confidence_calibration",
          capabilityName = "Confidence Calibration",
          proofType = "Decision Lab Debrief",
          timestamp = "2026-08-29",
          freshnessDays = 5,
          telemetrySnippet = "Correctly withheld containment decision pending Memory Dump verification",
          verifiedHash = "0x44D2...AA77",
          confidenceScore = 88
        )
      ),
      learningPatterns = listOf(
        "Benefits strongly from post-mission debriefs that explain 'What was assumed vs what was proven'."
      ),
      topGrowthMissions = listOf(
        "Executive Ransomware Negotiation Dilemma (14m)",
        "Unknown-Unknown Boundary Calibration (10m)"
      )
    )
  )

  private fun createInitialCapabilityNodes(): List<CapabilityDependencyNode> = listOf(
    CapabilityDependencyNode(
      id = "networking",
      name = "Networking Protocols",
      cluster = CognitiveClusterType.FOUNDATION,
      demonstratedScore = 88,
      status = CapabilityNodeStatus.VERIFIED,
      upstreamDependencyIds = emptyList(),
      downstreamImpactIds = listOf("packet_analysis", "threat_detection"),
      downstreamImpactSummary = "Strong protocol foundation accelerates packet anomaly recognition.",
      failureRateDownstream = 4,
      telemetryRequirement = "TCP/UDP/DNS/HTTP stream decoding",
      recommendedMissionId = "pcap_dns_tunneling"
    ),
    CapabilityDependencyNode(
      id = "packet_analysis",
      name = "Packet Analysis",
      cluster = CognitiveClusterType.FOUNDATION,
      demonstratedScore = 84,
      status = CapabilityNodeStatus.VERIFIED,
      upstreamDependencyIds = listOf("networking"),
      downstreamImpactIds = listOf("threat_detection"),
      downstreamImpactSummary = "Validates malicious beaconing intervals and TLS ja3 fingerprinting.",
      failureRateDownstream = 6,
      telemetryRequirement = "Full packet capture (PCAP) & Zeek conn.log",
      recommendedMissionId = "tls_ja3_c2_identification"
    ),
    CapabilityDependencyNode(
      id = "threat_detection",
      name = "Threat Detection",
      cluster = CognitiveClusterType.ACTIVE_DEFENSE,
      demonstratedScore = 79,
      status = CapabilityNodeStatus.VERIFIED,
      upstreamDependencyIds = listOf("networking", "packet_analysis"),
      downstreamImpactIds = listOf("siem_investigation"),
      downstreamImpactSummary = "Determines whether raw security events trigger actionable high-fidelity alerts.",
      failureRateDownstream = 12,
      telemetryRequirement = "Sysmon, Auditd, Suricata signatures",
      recommendedMissionId = "sigma_rule_engineering"
    ),
    CapabilityDependencyNode(
      id = "siem_investigation",
      name = "SIEM Investigation",
      cluster = CognitiveClusterType.ACTIVE_DEFENSE,
      demonstratedScore = 71,
      status = CapabilityNodeStatus.BOTTLENECK,
      upstreamDependencyIds = listOf("threat_detection"),
      downstreamImpactIds = listOf("incident_response", "cross_context_transfer"),
      downstreamImpactSummary = "When SIEM investigation struggles with noisy logs, incident response containment decisions are delayed by an average of 8.4 minutes.",
      failureRateDownstream = 24,
      telemetryRequirement = "Multi-source correlated log indexing (Splunk / Elasticsearch)",
      recommendedMissionId = "multi_log_sysmon_pcap_correlation"
    ),
    CapabilityDependencyNode(
      id = "cross_context_transfer",
      name = "Cross-Telemetry Transfer",
      cluster = CognitiveClusterType.GENERALIZATION_STRESS,
      demonstratedScore = 54,
      status = CapabilityNodeStatus.BOTTLENECK,
      upstreamDependencyIds = listOf("siem_investigation"),
      downstreamImpactIds = listOf("incident_response"),
      downstreamImpactSummary = "Inability to transfer on-premise triage logic into CloudTrail produces a 42% false-positive misclassification rate in AWS scenarios.",
      failureRateDownstream = 42,
      telemetryRequirement = "AWS CloudTrail, Kubernetes audit, Linux ebpf",
      recommendedMissionId = "cloudtrail_to_sysmon_translation"
    ),
    CapabilityDependencyNode(
      id = "incident_response",
      name = "Incident Response & Containment",
      cluster = CognitiveClusterType.ACTIVE_DEFENSE,
      demonstratedScore = 69,
      status = CapabilityNodeStatus.EMERGING,
      upstreamDependencyIds = listOf("siem_investigation", "cross_context_transfer"),
      downstreamImpactIds = listOf("crisis_leadership"),
      downstreamImpactSummary = "Containment procedures depend strictly on accurate root-cause identification.",
      failureRateDownstream = 19,
      telemetryRequirement = "Host isolation, firewall rule injection, account revocation",
      recommendedMissionId = "live_containment_ransomware"
    ),
    CapabilityDependencyNode(
      id = "crisis_leadership",
      name = "Crisis Decision Leadership",
      cluster = CognitiveClusterType.METACOGNITIVE_STRATEGIC,
      demonstratedScore = 65,
      status = CapabilityNodeStatus.EMERGING,
      upstreamDependencyIds = listOf("incident_response"),
      downstreamImpactIds = emptyList(),
      downstreamImpactSummary = "Synthesizes containment status into high-stakes executive business actions.",
      failureRateDownstream = 15,
      telemetryRequirement = "Executive debrief, stakeholder blast-radius mitigation",
      recommendedMissionId = "ciso_executive_breach_brief"
    )
  )

  private fun createInitialMistakeRecords(): List<MistakeIntelligenceRecord> = listOf(
    MistakeIntelligenceRecord(
      timestamp = "2026-09-02 14:15",
      missionTitle = "SOC Alert: Suspicious PowerShell EncodedCommand",
      failureType = FailureModeType.PREMATURE_CONCLUSION,
      observedSymptom = "Closed alert as benign administration without checking parent process or outbound network beaconing.",
      rootCauseCausalLink = "Relied solely on the benign script name 'UpdateCheck.ps1' without checking whether it was spawned by WINWORD.EXE.",
      constructiveFeedback = "Always verify the parent-process spawning chain (Sysmon Event ID 1) before declaring encoded PowerShell benign.",
      targetedRemediationMission = "Parent-Child Process Ancestry Dissection (8m)",
      capabilityImpactLabel = "Active Defense: +4% precision calibrated"
    ),
    MistakeIntelligenceRecord(
      timestamp = "2026-08-31 09:30",
      missionTitle = "CloudTrail Incident: Unauthorized IAM Privilege Escalation",
      failureType = FailureModeType.TRANSFER_FAILURE,
      observedSymptom = "Searched for Windows Event ID 4624 in AWS CloudTrail JSON telemetry.",
      rootCauseCausalLink = "Attempted to directly translate on-premise Windows logon event syntax into AWS IAM API logs instead of looking for 'ConsoleLogin' and 'AssumeRole'.",
      constructiveFeedback = "Techniques are invariant, but telemetry syntax differs. Map the conceptual action (Credential Access) to cloud API calls.",
      targetedRemediationMission = "CloudTrail vs Windows Security Event Rosetta Stone (10m)",
      capabilityImpactLabel = "Generalization: Transfer gate awareness unblocked"
    ),
    MistakeIntelligenceRecord(
      timestamp = "2026-08-28 16:45",
      missionTitle = "Ransomware Crisis Containment Drill",
      failureType = FailureModeType.PROCEDURAL_ERROR,
      observedSymptom = "Rebooted infected domain controller prior to preserving live RAM memory capture.",
      rootCauseCausalLink = "Panic response under 3-minute time limit caused immediate power-cycle, destroying encryption keys held in memory.",
      constructiveFeedback = "Order of Volatility must always precede power state changes. Live RAM holds active C2 keys.",
      targetedRemediationMission = "Order of Volatility Preservation Drill (7m)",
      capabilityImpactLabel = "Active Defense: Incident Response procedure reinforced"
    )
  )

  private fun createInitialLearningMemory(): List<LearningMemoryItem> = listOf(
    LearningMemoryItem(
      topic = "Cross-Context Cloud Threat Detection",
      cluster = CognitiveClusterType.GENERALIZATION_STRESS,
      lastDemonstratedTimestamp = "2026-08-18",
      daysSinceDemonstrated = 16,
      retentionHealth = RetentionHealthStatus.DRIFTING,
      decayMessage = "This capability hasn't been demonstrated in 16 days. Telemetry translation fluency decays without spaced practice.",
      reactivationMissionTitle = "7-Minute CloudTrail Privilege Escalation Reactivation",
      reactivationMissionMinutes = 7,
      previousMistakesOvercome = listOf("Overcame searching for Windows Event IDs in AWS logs"),
      successfulStrategies = listOf("Grouping CloudTrail events by userIdentity.arn before querying actions")
    ),
    LearningMemoryItem(
      topic = "Snort & Suricata Network Rule Writing",
      cluster = CognitiveClusterType.FOUNDATION,
      lastDemonstratedTimestamp = "2026-08-04",
      daysSinceDemonstrated = 30,
      retentionHealth = RetentionHealthStatus.AT_RISK,
      decayMessage = "Snort rule syntax hasn't been exercised in 30 days. High risk of forgetting payload inspection modifier syntax.",
      reactivationMissionTitle = "5-Minute Suricata Fast-Pattern Rule Refresh",
      reactivationMissionMinutes = 5,
      previousMistakesOvercome = listOf("Fixed missing depth/offset constraints causing engine timeouts"),
      successfulStrategies = listOf("Using fast_pattern matching before checking regex content")
    ),
    LearningMemoryItem(
      topic = "Sysmon Parent-Child Process Analysis",
      cluster = CognitiveClusterType.ACTIVE_DEFENSE,
      lastDemonstratedTimestamp = "2026-09-02",
      daysSinceDemonstrated = 1,
      retentionHealth = RetentionHealthStatus.FRESH,
      decayMessage = "Freshly demonstrated with high confidence. No decay risk detected.",
      reactivationMissionTitle = "Advanced LOLBAS Parentage Challenge (Optional)",
      reactivationMissionMinutes = 10,
      previousMistakesOvercome = listOf("Stopped jumping to premature conclusions on script names"),
      successfulStrategies = listOf("Checking OriginalFileName header to bypass renamed binary tricks")
    )
  )

  private fun createInitialProveItChallenges(): List<ProveItChallenge> = listOf(
    ProveItChallenge(
      id = "prove_siem_beacon",
      capabilityName = "SIEM Beaconing Correlation",
      cluster = CognitiveClusterType.ACTIVE_DEFENSE,
      difficulty = "Challenge",
      telemetryEnvironment = "Zeek + Sysmon Correlated Telemetry",
      rawTelemetryLog = "14:22:01 Host: WS-091 IP: 192.168.1.104 -> 185.220.101.5:443 (Bytes: 812, Interval: 60.1s)\n14:23:01 Host: WS-091 IP: 192.168.1.104 -> 185.220.101.5:443 (Bytes: 814, Interval: 60.0s)\n14:24:01 Host: WS-091 Process: spoolsv.exe -> rundll32.exe (Command: rundll32.exe C:\\ProgramData\\update.dll,Init)",
      challengePrompt = "Analyze the periodic traffic and process ancestry above. Determine the exact operational threat state.",
      triageOptions = listOf(
        ProveItOption(
          id = "opt_c2_isolate",
          actionTitle = "Isolate WS-091: Confirmed C2 Beaconing disguised via rundll32 DLL injection",
          isOptimal = true,
          justification = "Strict 60.0s interval with small constant byte sizes coupled with rundll32 executing from ProgramData indicates Cobalt Strike beaconing.",
          failureModeIfChosen = null
        ),
        ProveItOption(
          id = "opt_benign_print",
          actionTitle = "Mark Benign: Legitimate Windows Print Spooler routine telemetry",
          isOptimal = false,
          justification = "spoolsv.exe legitimately handles printer drivers and regularly communicates with network endpoints.",
          failureModeIfChosen = FailureModeType.PATTERN_RECOGNITION_ERROR
        ),
        ProveItOption(
          id = "opt_monitor_passive",
          actionTitle = "Monitor Passively: Wait for further byte volume before acting",
          isOptimal = false,
          justification = "Beaconing interval indicates active interactive C2 session; waiting grants adversary time for credential dumping.",
          failureModeIfChosen = FailureModeType.UNCERTAINTY_PARALYSIS
        )
      ),
      verifiedCryptographicProof = "SHA-256: 0x9B8A...F104-VERIFIED-L4"
    ),
    ProveItChallenge(
      id = "prove_cloud_privesc",
      capabilityName = "Cloud IAM Privilege Escalation",
      cluster = CognitiveClusterType.GENERALIZATION_STRESS,
      difficulty = "Transfer",
      telemetryEnvironment = "AWS CloudTrail Event Log",
      rawTelemetryLog = """{"eventName": "AttachUserPolicy", "userIdentity": {"arn": "arn:aws:iam::123456789012:user/intern_temp"}, "requestParameters": {"policyArn": "arn:aws:iam::aws:policy/AdministratorAccess"}, "sourceIPAddress": "34.201.88.12", "errorCode": null}""",
      challengePrompt = "The temporary account 'intern_temp' just attached AdministratorAccess to itself from an external IP. What is your immediate containment action?",
      triageOptions = listOf(
        ProveItOption(
          id = "opt_revoke_keys_deny",
          actionTitle = "Revoke active IAM access keys, attach explicit DenyAll inline policy, and audit role assumption session history",
          isOptimal = true,
          justification = "Self-granting of AdministratorAccess from an untrusted public IP is a textbook compromised credential privilege escalation.",
          failureModeIfChosen = null
        ),
        ProveItOption(
          id = "opt_delete_user_only",
          actionTitle = "Delete the user account immediately without auditing existing assumed roles",
          isOptimal = false,
          justification = "Deleting the user without invalidating active STS session tokens allows the attacker to maintain persistence via established roles.",
          failureModeIfChosen = FailureModeType.INCOMPLETE_INVESTIGATION
        ),
        ProveItOption(
          id = "opt_ask_intern",
          actionTitle = "Send Slack message to intern asking if they requested admin rights",
          isOptimal = false,
          justification = "External IP address confirms off-network access; waiting for human response gives full tenant compromise window.",
          failureModeIfChosen = FailureModeType.PROCEDURAL_ERROR
        )
      ),
      verifiedCryptographicProof = "SHA-256: 0x1A4C...33EE-VERIFIED-CLOUD"
    )
  )

  private fun createInitialVerifiedProofs(): List<EvidenceProofItem> = listOf(
    EvidenceProofItem(
      capabilityId = "pcap_dns_tunneling",
      capabilityName = "DNS Tunneling Anomaly Extraction",
      proofType = "PCAP Live Telemetry Investigation",
      timestamp = "2026-09-02 11:20",
      freshnessDays = 1,
      telemetrySnippet = "Decoded base64 payload from subdomains: exfil-01.a89f...attacker.xyz",
      verifiedHash = "0x91F2...C412",
      confidenceScore = 98,
      isCryptographicallySigned = true
    ),
    EvidenceProofItem(
      capabilityId = "sysmon_lsass_dump",
      capabilityName = "Credential Theft Detection (LSASS)",
      proofType = "Sysmon Event ID 10 Audit",
      timestamp = "2026-08-30 15:45",
      freshnessDays = 4,
      telemetrySnippet = "procdump.exe opened handle with PROCESS_ALL_ACCESS (0x1FFFFF) to lsass.exe",
      verifiedHash = "0x33A1...B774",
      confidenceScore = 95,
      isCryptographicallySigned = true
    )
  )

  private fun createInitialCareerProfiles(): List<CareerIntelligenceProfile> = listOf(
    CareerIntelligenceProfile(
      roleId = "soc_analyst",
      title = "SOC Analyst (Tier 1 / Tier 2)",
      requiredClusterScores = mapOf(
        CognitiveClusterType.FOUNDATION to 75,
        CognitiveClusterType.ACTIVE_DEFENSE to 70,
        CognitiveClusterType.GENERALIZATION_STRESS to 60,
        CognitiveClusterType.METACOGNITIVE_STRATEGIC to 65
      ),
      currentClusterScores = mapOf(
        CognitiveClusterType.FOUNDATION to 82,
        CognitiveClusterType.ACTIVE_DEFENSE to 71,
        CognitiveClusterType.GENERALIZATION_STRESS to 54,
        CognitiveClusterType.METACOGNITIVE_STRATEGIC to 68
      ),
      overallMatchPercentage = 86,
      primaryBottleneckGap = "Your largest gap is Generalization (54% vs 60% requirement). Transferring familiar Windows alert triage into noisy Linux & Cloud telemetry will bring you to 94% job readiness.",
      targetedMissionSequence = listOf(
        "Sysmon vs CloudTrail Alert Triage (12m)",
        "Noisy False-Positive Filtering Sprint (10m)",
        "Live Containment Speed Drill (8m)"
      ),
      readinessIndex = 86
    ),
    CareerIntelligenceProfile(
      roleId = "threat_hunter",
      title = "Threat Hunter",
      requiredClusterScores = mapOf(
        CognitiveClusterType.FOUNDATION to 85,
        CognitiveClusterType.ACTIVE_DEFENSE to 85,
        CognitiveClusterType.GENERALIZATION_STRESS to 80,
        CognitiveClusterType.METACOGNITIVE_STRATEGIC to 75
      ),
      currentClusterScores = mapOf(
        CognitiveClusterType.FOUNDATION to 82,
        CognitiveClusterType.ACTIVE_DEFENSE to 71,
        CognitiveClusterType.GENERALIZATION_STRESS to 54,
        CognitiveClusterType.METACOGNITIVE_STRATEGIC to 68
      ),
      overallMatchPercentage = 68,
      primaryBottleneckGap = "Threat hunting requires hypothesis-driven exploration without alerts. Needs deeper forensic reconstruction and cross-context capability.",
      targetedMissionSequence = listOf(
        "Hypothesis-Driven Hunting in Raw Event Logs (15m)",
        "LOLBAS Living-off-the-land Execution Tracing (14m)",
        "Memory Volatility Artifact Reconstruction (20m)"
      ),
      readinessIndex = 68
    ),
    CareerIntelligenceProfile(
      roleId = "cloud_security_engineer",
      title = "Cloud Security Engineer",
      requiredClusterScores = mapOf(
        CognitiveClusterType.FOUNDATION to 80,
        CognitiveClusterType.ACTIVE_DEFENSE to 75,
        CognitiveClusterType.GENERALIZATION_STRESS to 85,
        CognitiveClusterType.METACOGNITIVE_STRATEGIC to 70
      ),
      currentClusterScores = mapOf(
        CognitiveClusterType.FOUNDATION to 82,
        CognitiveClusterType.ACTIVE_DEFENSE to 71,
        CognitiveClusterType.GENERALIZATION_STRESS to 54,
        CognitiveClusterType.METACOGNITIVE_STRATEGIC to 68
      ),
      overallMatchPercentage = 64,
      primaryBottleneckGap = "Significant transferability gap in Kubernetes API audits and multi-cloud IAM delegation policies.",
      targetedMissionSequence = listOf(
        "AWS GuardDuty & CloudTrail Deep Pivot (15m)",
        "Kubernetes Pod Escape & ServiceAccount Audit (18m)",
        "Terraform Infrastructure-as-Code Security Scanning (12m)"
      ),
      readinessIndex = 64
    )
  )

  private fun createInitialOperationalScenarios(): List<OperationalEnvironmentScenario> = listOf(
    OperationalEnvironmentScenario(
      type = OperationalSimulationType.SOC_ALERT_TRIAGE,
      title = "Enterprise Alert Queue: Concurrent Lateral Movement Alert",
      targetHost = "corp-dc01.internal / corp-ws09.internal",
      telemetryData = "[ALERT-891] Mimikatz sekurlsa::logonpasswords detected via LSASS read\n[ALERT-892] Remote WMI process creation 'wmic /node:corp-dc01 process call create cmd.exe'\n[ALERT-893] SMB session opened on port 445 using Administrator token",
      activeObjectives = listOf(
        "Correlate source workstation for stolen credentials",
        "Block RPC/SMB lateral movement at perimeter firewall",
        "Extract compromised Kerberos TGT tickets"
      ),
      timePressureSeconds = 180,
      simulatedToolCommands = listOf("grep -i 'wmic' winevent.log", "netstat -ano | findstr 445", "isolate-host corp-ws09"),
      difficultyLabel = "Real SOC Operations"
    ),
    OperationalEnvironmentScenario(
      type = OperationalSimulationType.INCIDENT_RESPONSE,
      title = "Crisis Containment: Multi-Host LockBit 3.0 Ransomware Surge",
      targetHost = "Storage SAN / 14 Production Database Servers",
      telemetryData = "[CRISIS] Shadow copies deleted via vssadmin.exe delete shadows /all /quiet\n[CRISIS] BitLocker encryption initiated on D:\\ Shared Volume\n[CRITICAL] External exfiltration beaconing to 45.134.22.18:8443",
      activeObjectives = listOf(
        "Sever core network switch VLAN connection to SAN",
        "Halt running encryption binaries without triggering wiper killswitch",
        "Preserve volatile cryptographic key states in RAM"
      ),
      timePressureSeconds = 120,
      simulatedToolCommands = listOf("switchport access vlan 999 (isolation)", "taskkill /f /im enc.exe", "dump-ram --pid 4182"),
      difficultyLabel = "High-Pressure Crisis"
    ),
    OperationalEnvironmentScenario(
      type = OperationalSimulationType.CLOUD_TELEMETRY,
      title = "Cloud Security: AWS IAM Metadata Service SSRF Exploitation",
      targetHost = "i-09f182c18ab2 (EC2 Web Proxy)",
      telemetryData = """GET http://169.254.169.254/latest/meta-data/iam/security-credentials/production-role\nReturned: AccessKeyId: ASIA..., SecretAccessKey: ..., Token: ...\nFollowed by: s3:ListBuckets from IP 198.51.100.44""",
      activeObjectives = listOf(
        "Enforce IMDSv2 token requirement on EC2 metadata service",
        "Revoke compromised instance profile STS temporary session token",
        "Audit S3 bucket access logs for unauthorized object downloads"
      ),
      timePressureSeconds = 240,
      simulatedToolCommands = listOf("aws ec2 modify-instance-metadata-options --http-tokens required", "aws iam put-role-policy --deny-all-token", "aws s3api get-bucket-logging"),
      difficultyLabel = "Cloud Native Triage"
    )
  )

  private fun createInitialCollaborativeSession(): CollaborativeIncidentSession = CollaborativeIncidentSession(
    scenarioTitle = "Operation Red Typhoon: Advanced Supply Chain Compromise",
    learnerAssignedRole = "Threat Hunter & Forensic Lead",
    teamMembers = listOf(
      TeamMemberStatus("Operator-Echo", "Incident Commander", "Coordinating C-Suite Escalation", "Active"),
      TeamMemberStatus("Operator-Viper", "Network Defender", "Applying Perimeter Ingress Firewall Rules", "Active"),
      TeamMemberStatus("You (Callsign: Aegora)", "Threat Hunter", "Correlating Endpoint In-Memory DLL Injections", "Active"),
      TeamMemberStatus("Operator-Ghost", "Cloud Specialist", "Auditing AWS KMS Key Decryption Logs", "Standby")
    ),
    incidentTimeline = listOf(
      "10:01 - Initial intrusion via SolarWinds-style malicious patch binary",
      "10:04 - C2 beacon established to external domain update-microsoft-cdn.net",
      "10:08 - Lateral movement across 3 critical domain controllers",
      "10:12 - Team containment command executed (Firewall isolated hosts)"
    ),
    individualContributionScore = 92,
    teamCoordinationScore = 88,
    isolationGuarantee = "Individual learner evidence is evaluated strictly from direct actions and never corrupted by teammates."
  )

  private fun createInitialProgressMilestones(): List<ProgressNarrativeMilestone> = listOf(
    ProgressNarrativeMilestone(
      timeframe = "30 Days Ago",
      title = "Theoretical Foundations Established",
      narrative = "Strong conceptual grasp of networking protocols and syntax, but high uncertainty when confronted with ambiguous live alerts.",
      capabilityDelta = "Baseline Foundation Score: 64",
      nextRecommendedAction = "Transitioned from textbook study to hands-on SIEM telemetry triage."
    ),
    ProgressNarrativeMilestone(
      timeframe = "14 Days Ago",
      title = "First Live Incident Containment",
      narrative = "Successfully isolated compromised workstation and identified Cobalt Strike beaconing interval with zero false positives.",
      capabilityDelta = "Active Defense: +15% (56 -> 71)",
      nextRecommendedAction = "Encountered initial transfer barrier when moving from Windows to Linux."
    ),
    ProgressNarrativeMilestone(
      timeframe = "Today",
      title = "Active Transfer Gate Unblocking",
      narrative = "Calibrated confidence error rate down to 11%. Actively resolving the cross-telemetry transfer bottleneck in cloud environments.",
      capabilityDelta = "Overall Capability MMR: 1,960 (L4 Skilled)",
      nextRecommendedAction = "Stress-test capability under high-volume noisy telemetry in SOC Shift simulator."
    ),
    ProgressNarrativeMilestone(
      timeframe = "Next Frontier",
      title = "Cross-Domain Operational Mastery",
      narrative = "Achieve 75+ in Generalization across AWS CloudTrail and Kubernetes audit telemetry to qualify for L5 Expert Skill Passport.",
      capabilityDelta = "Projected Readiness: 94% SOC Analyst / Cloud Sec",
      nextRecommendedAction = "Execute Cloud IAM Privilege Escalation Prove It Challenge."
    )
  )

  private fun createInitialDiscoveries(): List<DiscoveryInsightItem> = listOf(
    DiscoveryInsightItem(
      type = DiscoveryType.HIDDEN_STRENGTH,
      headline = "High-Precision Network Anomaly Intuition",
      explanation = "Across 14 PCAP investigation drills, you demonstrated a 96% accuracy rate in spotting periodic beaconing jitter anomalies, outperforming 88% of learners in your cohort.",
      actionText = "Explore Advanced Threat Hunting Track",
      destinationTag = "threat_hunt"
    ),
    DiscoveryInsightItem(
      type = DiscoveryType.CRITICAL_DEPENDENCY,
      headline = "Cloud Investigation Depends on Protocol Foundations",
      explanation = "Your cloud telemetry investigation speed is limited by unfamiliarity with HTTP REST status codes and JSON API verb structure. Strengthening this prerequisite will unlock a 22% triage speed boost.",
      actionText = "Review API Security Prerequisite Drill",
      destinationTag = "api_prereq"
    ),
    DiscoveryInsightItem(
      type = DiscoveryType.UNKNOWN_FRONTIER,
      headline = "Firmware & Hardware Forensics Unassessed",
      explanation = "AEGORA currently has zero verified evidence regarding your capability in IoT/OT firmware analysis. This frontier remains completely unknown.",
      actionText = "Take 10-Minute Firmware Diagnostic",
      destinationTag = "diagnostic_firmware"
    )
  )

  private fun createInitialTrustAudits(): List<TrustAuditItem> = listOf(
    TrustAuditItem(
      category = "Demonstrated Capability Telemetry",
      whatIsStored = "Aggregated scores, verified log extraction hashes, mission triage decisions, and mistake categorizations.",
      whyStored = "To construct your living Cyber Twin 6.0, identify bottlenecks, and recommend optimal next actions.",
      exportable = true,
      canBeDeleted = true
    ),
    TrustAuditItem(
      category = "Privacy & Isolation Boundary",
      whatIsStored = "Zero personal psychological traits or chat surveillance data. Only technical security evidence and reasoning choices are retained.",
      whyStored = "To provide completely objective, evidence-grounded skill verification without subjective bias.",
      exportable = true,
      canBeDeleted = true
    ),
    TrustAuditItem(
      category = "Local Room Database Architecture",
      whatIsStored = "All capability states persist locally on your device with cryptographic signatures for export.",
      whyStored = "Ensures full offline operation, zero unauthorized cloud egress, and user ownership of learning data.",
      exportable = true,
      canBeDeleted = true
    )
  )

  private fun createInitialUniversityAnalytics(): UniversityCohortAnalytics = UniversityCohortAnalytics(
    cohortName = "Cyber Defense Academy - Fall Cohort 2026",
    totalLearners = 48,
    clusterAverages = mapOf(
      CognitiveClusterType.FOUNDATION to 84,
      CognitiveClusterType.ACTIVE_DEFENSE to 73,
      CognitiveClusterType.GENERALIZATION_STRESS to 51,
      CognitiveClusterType.METACOGNITIVE_STRATEGIC to 66
    ),
    curriculumGaps = listOf(
      "Cohort wide bottleneck: Generalization to Cloud Telemetry (Average: 51%)",
      "High rate of Premature Conclusion errors in first 5 minutes of incident triage"
    ),
    highPerformingConcepts = listOf(
      "Wireshark protocol dissection (91% pass rate)",
      "Windows Sysmon Event correlation (86% pass rate)"
    ),
    privacyStatement = "Strictly aggregated capability indicators. Zero individual psychological or mistake profiling exposed."
  )

  private fun createInitialEmployerVerification(): EmployerCandidateVerification = EmployerCandidateVerification(
    candidateCallsign = "Aegora-Operator-01",
    passportId = "PASSPORT-AEGORA-9914-L4",
    targetRole = "SOC Analyst / Detection Engineer",
    verifiedClusterScores = mapOf(
      CognitiveClusterType.FOUNDATION to 82,
      CognitiveClusterType.ACTIVE_DEFENSE to 71,
      CognitiveClusterType.GENERALIZATION_STRESS to 54,
      CognitiveClusterType.METACOGNITIVE_STRATEGIC to 68
    ),
    verifiedEvidenceCount = 18,
    verifiedAuditHash = "SHA-256: 0x8F92A1B0...91CE4421-VERIFIED-L4",
    verificationAuthority = "AEGORA Verifiable Capability Ledger v12.0 (SHA-256 Verified)"
  )
}
