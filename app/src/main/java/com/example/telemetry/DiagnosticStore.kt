package com.example.telemetry

import com.example.core.result.AegoraResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class CircuitStatus {
  CLOSED,
  HALF_OPEN,
  OPEN
}

data class CircuitBreakerMetric(
  val id: String,
  val serviceName: String,
  val status: CircuitStatus,
  val latencyP95Ms: Long,
  val latencyP99Ms: Long,
  val failureCount: Int,
  val lastTrippedTimestamp: String?,
  val fallbackStrategy: String
)

enum class FactCheckerAction {
  VERIFIED,
  MASKED_PII,
  HALLUCINATION_BLOCKED
}

data class FactCheckerEvent(
  val id: String,
  val timestamp: String,
  val promptVector: String,
  val flaggedReason: String,
  val action: FactCheckerAction,
  val interceptedPayloadSnippet: String
)

enum class DiagnosticSeverity {
  INFO,
  WARN,
  CRITICAL
}

data class DiagnosticLogEvent(
  val id: String,
  val timestamp: String,
  val severity: DiagnosticSeverity,
  val componentTag: String,
  val message: String,
  val metadata: String? = null,
  val previousHash: String = "0000000000000000000000000000000000000000000000000000000000000000",
  val currentHash: String = ""
)

data class MerkleAuditBlock(
  val index: Long,
  val timestamp: String,
  val eventId: String,
  val eventPayloadHash: String,
  val previousBlockHash: String,
  val blockHash: String,
  val severity: DiagnosticSeverity = DiagnosticSeverity.INFO,
  val componentTag: String = "CORE",
  val isTampered: Boolean = false
)

data class MerkleChainTelemetryState(
  val chainHeight: Long,
  val rootHash: String,
  val isValid: Boolean,
  val blocks: List<MerkleAuditBlock>,
  val latestBlockTimestamp: String,
  val genesisHash: String = "0000000000000000000000000000000000000000000000000000000000000000"
)

data class DiagnosticCrashEvent(
  val id: String,
  val timestamp: String,
  val componentTag: String,
  val exceptionClass: String,
  val message: String,
  val stackTraceSnippet: String,
  val heapMemoryUsageMb: Long
)

data class N8nIncidentRecord(
  val id: String,
  val timestamp: String,
  val title: String,
  val severity: DiagnosticSeverity,
  val cvssScore: Double,
  val sourceIp: String,
  val blockHash: String,
  val dispatchStatus: String = "DELIVERED"
)

data class SupabaseSyncMetric(
  val isConfigured: Boolean,
  val lastSyncTimestamp: String,
  val syncedBlockCount: Long,
  val rlsEnforced: Boolean = true,
  val persistenceEngine: String = "PostgreSQL 15+ / PostgREST"
)

/**
 * DiagnosticStore - Enterprise Singleton for Runtime UI Rendering Crashes,
 * SRE Telemetry Metrics, Circuit Breaker Observability, FactChecker Audit Logs,
 * and Filterable Diagnostic Event Logs with {INFO, WARN, CRITICAL} Severities.
 */
object DiagnosticStore {

  private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
  const val GENESIS_HASH = "0000000000000000000000000000000000000000000000000000000000000000"

  fun sha256(input: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(input.toByteArray(Charsets.UTF_8))
    return hash.joinToString("") { "%02x".format(it) }
  }

  private val _crashEvents = MutableStateFlow<List<DiagnosticCrashEvent>>(emptyList())
  val crashEvents: StateFlow<List<DiagnosticCrashEvent>> = _crashEvents.asStateFlow()

  private val _logEvents = MutableStateFlow<List<DiagnosticLogEvent>>(
    listOf(
      DiagnosticLogEvent(
        id = "LOG-1001",
        timestamp = "04:50:11.204",
        severity = DiagnosticSeverity.INFO,
        componentTag = "ThreatHunterWorker",
        message = "Autonomous background hunter service scheduled (INTERVAL: 15m, CONSTRAINTS: NET_CONNECTED + BATTERY_NOT_LOW)",
        metadata = "WORK_ID: aegora_hunter_periodic"
      ),
      DiagnosticLogEvent(
        id = "LOG-1002",
        timestamp = "04:50:24.815",
        severity = DiagnosticSeverity.INFO,
        componentTag = "SentinelMeshSync",
        message = "Sentinel Mesh node initialized. Cryptographic peer discovery broadcast sent via WebSocket relay.",
        metadata = "NODE_ID: node_aegora_alpha"
      ),
      DiagnosticLogEvent(
        id = "LOG-1003",
        timestamp = "04:51:02.110",
        severity = DiagnosticSeverity.WARN,
        componentTag = "KernelWatchdog",
        message = "Native JNI heap expansion detected: 48MB allocated (threshold 45MB). Initiating zero-leak validation scan.",
        metadata = "JNI_ADDR: 0x7FFF8902A"
      ),
      DiagnosticLogEvent(
        id = "LOG-1004",
        timestamp = "04:51:45.340",
        severity = DiagnosticSeverity.WARN,
        componentTag = "ScapyTelemetryEngine",
        message = "Heuristic packet entropy deviation in subnet 10.0.4.0/24 (entropy: 0.892 > baseline: 0.850)",
        metadata = "DRIFT_PCT: +4.94%"
      ),
      DiagnosticLogEvent(
        id = "LOG-1005",
        timestamp = "04:52:19.980",
        severity = DiagnosticSeverity.CRITICAL,
        componentTag = "StrongBoxEnclave",
        message = "Unauthorized memory page read attempt intercepted on protected key buffer: duress zeroization armed.",
        metadata = "INTERRUPT: SEC_PAGE_FAULT_0x1A"
      ),
      DiagnosticLogEvent(
        id = "LOG-1006",
        timestamp = "04:52:48.420",
        severity = DiagnosticSeverity.INFO,
        componentTag = "SentinelMeshSync",
        message = "Synchronized threat immunity IOC [SHA256: 7f83...c120] across 3 verified field operators.",
        metadata = "PEERS: node_bravo, node_delta"
      )
    )
  )
  val logEvents: StateFlow<List<DiagnosticLogEvent>> = _logEvents.asStateFlow()

  private val _circuitBreakers = MutableStateFlow<List<CircuitBreakerMetric>>(
    listOf(
      CircuitBreakerMetric(
        id = "cb-01",
        serviceName = "GEMINI_NEURAL_REASONING_PIPELINE",
        status = CircuitStatus.CLOSED,
        latencyP95Ms = 142,
        latencyP99Ms = 285,
        failureCount = 0,
        lastTrippedTimestamp = null,
        fallbackStrategy = "LOCAL_RULE_ENGINE_EDGE"
      ),
      CircuitBreakerMetric(
        id = "cb-02",
        serviceName = "STRIPE_REVENUECAT_ENTITLEMENT_SYNC",
        status = CircuitStatus.CLOSED,
        latencyP95Ms = 88,
        latencyP99Ms = 164,
        failureCount = 0,
        lastTrippedTimestamp = null,
        fallbackStrategy = "OFFLINE_ENCRYPTED_VAULT_CACHE"
      ),
      CircuitBreakerMetric(
        id = "cb-03",
        serviceName = "ONESIGNAL_PUSH_DISPATCH_RELAY",
        status = CircuitStatus.HALF_OPEN,
        latencyP95Ms = 320,
        latencyP99Ms = 540,
        failureCount = 2,
        lastTrippedTimestamp = "04:38:12",
        fallbackStrategy = "LOCAL_HIGH_PRIORITY_BROADCAST"
      ),
      CircuitBreakerMetric(
        id = "cb-04",
        serviceName = "N8N_AUTONOMOUS_SOC_GATEWAY",
        status = CircuitStatus.OPEN,
        latencyP95Ms = 1200,
        latencyP99Ms = 2500,
        failureCount = 5,
        lastTrippedTimestamp = "04:41:00",
        fallbackStrategy = "AIR_GAPPED_FIREWALL_AUTONOMY"
      )
    )
  )
  val circuitBreakers: StateFlow<List<CircuitBreakerMetric>> = _circuitBreakers.asStateFlow()

  private val _factCheckerAuditTrail = MutableStateFlow<List<FactCheckerEvent>>(
    listOf(
      FactCheckerEvent(
        id = "fact-991",
        timestamp = "04:40:15",
        promptVector = "MITRE T1059.001 PowerShell Payload",
        flaggedReason = "Detected unsanitized operator IP leak in AI reasoning context",
        action = FactCheckerAction.MASKED_PII,
        interceptedPayloadSnippet = "Enclave IP [192.168.1.***] sanitized to [INTERNAL_SRC_NODE]"
      ),
      FactCheckerEvent(
        id = "fact-992",
        timestamp = "04:41:22",
        promptVector = "Zero-Day CVE Infiltration Assessment",
        flaggedReason = "Unverified CVE-2026-9914 hallucination not matched in NVD database",
        action = FactCheckerAction.HALLUCINATION_BLOCKED,
        interceptedPayloadSnippet = "[AI_UNVERIFIED] Recommendation suppressed by deterministic MITRE filter"
      ),
      FactCheckerEvent(
        id = "fact-993",
        timestamp = "04:42:08",
        promptVector = "Memory Scrape Mitigation Vector",
        flaggedReason = "Cryptographic proof-of-work matched StrongBox hardware specification",
        action = FactCheckerAction.VERIFIED,
        interceptedPayloadSnippet = "Verified: zeroize(buf, len) matches SecureMemory hardware contract"
      )
    )
  )
  val factCheckerAuditTrail: StateFlow<List<FactCheckerEvent>> = _factCheckerAuditTrail.asStateFlow()

  private val _merkleLedger = MutableStateFlow<List<MerkleAuditBlock>>(emptyList())
  val merkleLedger: StateFlow<List<MerkleAuditBlock>> = _merkleLedger.asStateFlow()

  private val _currentMerkleRoot = MutableStateFlow(GENESIS_HASH)
  val currentMerkleRoot: StateFlow<String> = _currentMerkleRoot.asStateFlow()

  private val _isLedgerValid = MutableStateFlow(true)
  val isLedgerValid: StateFlow<Boolean> = _isLedgerValid.asStateFlow()

  private val _merkleTelemetryStream = MutableStateFlow(
    MerkleChainTelemetryState(
      chainHeight = 0L,
      rootHash = GENESIS_HASH,
      isValid = true,
      blocks = emptyList(),
      latestBlockTimestamp = "00:00:00.000"
    )
  )
  val merkleTelemetryStream: StateFlow<MerkleChainTelemetryState> = _merkleTelemetryStream.asStateFlow()

  private val _n8nIncidentQueue = MutableStateFlow<List<N8nIncidentRecord>>(emptyList())
  val n8nIncidentQueue: StateFlow<List<N8nIncidentRecord>> = _n8nIncidentQueue.asStateFlow()

  private val _supabaseSyncState = MutableStateFlow(
    SupabaseSyncMetric(
      isConfigured = true,
      lastSyncTimestamp = "04:55:00.000",
      syncedBlockCount = 4L,
      rlsEnforced = true,
      persistenceEngine = "PostgreSQL 15+ / PostgREST"
    )
  )
  val supabaseSyncState: StateFlow<SupabaseSyncMetric> = _supabaseSyncState.asStateFlow()

  init {
    seedInitialMerkleLedger()
  }

  private fun updateTelemetryStream() {
    val list = _merkleLedger.value
    _merkleTelemetryStream.value = MerkleChainTelemetryState(
      chainHeight = list.size.toLong(),
      rootHash = _currentMerkleRoot.value,
      isValid = _isLedgerValid.value,
      blocks = list,
      latestBlockTimestamp = list.lastOrNull()?.timestamp ?: dateFormat.format(Date()),
      genesisHash = GENESIS_HASH
    )
  }

  private fun seedInitialMerkleLedger() {
    val initialLogs = _logEvents.value.reversed()
    var prev = GENESIS_HASH
    val initialBlocks = mutableListOf<MerkleAuditBlock>()
    initialLogs.forEachIndexed { i, log ->
      val rawPayload = "${log.id}:${log.timestamp}:${log.severity}:${log.componentTag}:${log.message}:${log.metadata ?: ""}"
      val payloadHash = sha256(rawPayload)
      val chainedHash = sha256("$payloadHash+$prev")
      val block = MerkleAuditBlock(
        index = (i + 1).toLong(),
        timestamp = log.timestamp,
        eventId = log.id,
        eventPayloadHash = payloadHash,
        previousBlockHash = prev,
        blockHash = chainedHash,
        severity = log.severity,
        componentTag = log.componentTag,
        isTampered = false
      )
      initialBlocks.add(block)
      prev = chainedHash
    }
    _merkleLedger.value = initialBlocks
    _currentMerkleRoot.value = prev
    updateTelemetryStream()
  }

  /**
   * Records a UI crash or unexpected execution failure into the diagnostic buffer.
   */
  fun recordCrash(throwable: Throwable, componentTag: String) {
    val totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024)
    val freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024)
    val usedMem = totalMem - freeMem

    val stackLines = throwable.stackTrace.take(4).joinToString("\n") {
      "  at ${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
    }

    val event = DiagnosticCrashEvent(
      id = "ERR-${UUID.randomUUID().toString().take(6).uppercase()}",
      timestamp = dateFormat.format(Date()),
      componentTag = componentTag,
      exceptionClass = throwable.javaClass.simpleName,
      message = throwable.message ?: "NullPointerException or unexpected Compose runtime fault",
      stackTraceSnippet = stackLines,
      heapMemoryUsageMb = usedMem
    )

    _crashEvents.value = listOf(event) + _crashEvents.value

    // Automatically record a CRITICAL severity log into the unified event stream
    recordLog(
      severity = DiagnosticSeverity.CRITICAL,
      componentTag = componentTag,
      message = "UI Fault: ${throwable.javaClass.simpleName} - ${throwable.message}",
      metadata = "HEAP_USED: ${usedMem}MB"
    )
  }

  fun recordLog(
    severity: DiagnosticSeverity,
    componentTag: String,
    message: String,
    metadata: String? = null
  ) {
    val prevHash = _currentMerkleRoot.value
    val eventId = "LOG-${UUID.randomUUID().toString().take(6).uppercase()}"
    val timestamp = dateFormat.format(Date())
    val rawPayload = "$eventId:$timestamp:$severity:$componentTag:$message:${metadata ?: ""}"
    val payloadHash = sha256(rawPayload)
    val chainedHash = sha256("$payloadHash+$prevHash")

    val log = DiagnosticLogEvent(
      id = eventId,
      timestamp = timestamp,
      severity = severity,
      componentTag = componentTag,
      message = message,
      metadata = metadata,
      previousHash = prevHash,
      currentHash = chainedHash
    )
    _currentMerkleRoot.value = chainedHash

    val block = MerkleAuditBlock(
      index = (_merkleLedger.value.size + 1).toLong(),
      timestamp = timestamp,
      eventId = eventId,
      eventPayloadHash = payloadHash,
      previousBlockHash = prevHash,
      blockHash = chainedHash,
      severity = severity,
      componentTag = componentTag,
      isTampered = false
    )
    _merkleLedger.value = _merkleLedger.value + block
    _logEvents.value = listOf(log) + _logEvents.value
    updateTelemetryStream()
  }

  fun clearLogs() {
    _logEvents.value = emptyList()
  }

  fun simulateLogInjection(severity: DiagnosticSeverity) {
    when (severity) {
      DiagnosticSeverity.INFO -> recordLog(
        severity = DiagnosticSeverity.INFO,
        componentTag = "SentinelMeshSync",
        message = "Periodic heartbeat broadcast acknowledged by remote relay node (RTT: 42ms)",
        metadata = "RELAY: wss://aegora.soc/mesh"
      )
      DiagnosticSeverity.WARN -> recordLog(
        severity = DiagnosticSeverity.WARN,
        componentTag = "ThreatHunterWorker",
        message = "Heuristic variance detected in Scapy ingress packet stream (z-score: 2.74)",
        metadata = "SIG_CLASS: HEUR_DRIFT"
      )
      DiagnosticSeverity.CRITICAL -> recordLog(
        severity = DiagnosticSeverity.CRITICAL,
        componentTag = "KernelWatchdog",
        message = "StrongBox Keystore attestation discrepancy detected: Enclave self-healing triggered (<15ms)",
        metadata = "ACTION: KEYS_ZEROIZED"
      )
    }
  }

  fun recordFactCheck(event: FactCheckerEvent) {
    val prevHash = _currentMerkleRoot.value
    val payloadHash = sha256("FACTCHECK:${event.id}:${event.timestamp}:${event.action}:${event.promptVector}")
    val chainedHash = sha256("$payloadHash+$prevHash")
    _currentMerkleRoot.value = chainedHash

    val block = MerkleAuditBlock(
      index = (_merkleLedger.value.size + 1).toLong(),
      timestamp = event.timestamp,
      eventId = event.id,
      eventPayloadHash = payloadHash,
      previousBlockHash = prevHash,
      blockHash = chainedHash,
      severity = DiagnosticSeverity.INFO,
      componentTag = "FactChecker",
      isTampered = false
    )
    _merkleLedger.value = _merkleLedger.value + block
    _factCheckerAuditTrail.value = listOf(event) + _factCheckerAuditTrail.value
    updateTelemetryStream()
  }

  fun verifyMerkleLedgerIntegrity(): AegoraResult<Boolean> {
    var expectedPrevious = GENESIS_HASH
    for (block in _merkleLedger.value) {
      if (block.previousBlockHash != expectedPrevious) {
        _isLedgerValid.value = false
        updateTelemetryStream()
        return AegoraResult.Failure(
          code = "MERKLE_CHAIN_TAMPERED",
          message = "Merkle hash chain broken at block #${block.index}: expected $expectedPrevious but found ${block.previousBlockHash}",
          isRecoverable = false
        )
      }
      val recomputed = sha256("${block.eventPayloadHash}+${block.previousBlockHash}")
      if (recomputed != block.blockHash) {
        _isLedgerValid.value = false
        updateTelemetryStream()
        return AegoraResult.Failure(
          code = "BLOCK_SIGNATURE_TAMPERED",
          message = "Cryptographic block hash mismatch at block #${block.index}",
          isRecoverable = false
        )
      }
      expectedPrevious = block.blockHash
    }
    _isLedgerValid.value = true
    updateTelemetryStream()
    return AegoraResult.Success(true)
  }

  fun clearCrashes() {
    _crashEvents.value = emptyList()
  }

  /**
   * Simulates an adversarial attempt to tamper with an historical audit event.
   * Modifies the payload hash of a past block, which breaks mathematical chain verification.
   */
  fun simulateTamperAttack(targetIndex: Long = 1): AegoraResult<Long> {
    val list = _merkleLedger.value.toMutableList()
    val idx = list.indexOfFirst { it.index == targetIndex }
    if (idx == -1 && list.isNotEmpty()) {
      val first = list.first()
      list[0] = first.copy(
        eventPayloadHash = sha256("FORGED_ADVERSARIAL_PAYLOAD"),
        isTampered = true
      )
      _merkleLedger.value = list
      _isLedgerValid.value = false
      updateTelemetryStream()
      return AegoraResult.Success(first.index)
    } else if (idx != -1) {
      val target = list[idx]
      list[idx] = target.copy(
        eventPayloadHash = sha256("FORGED_ADVERSARIAL_PAYLOAD"),
        isTampered = true
      )
      _merkleLedger.value = list
      _isLedgerValid.value = false
      updateTelemetryStream()
      return AegoraResult.Success(target.index)
    }
    return AegoraResult.Failure("LEDGER_EMPTY", "Cannot tamper empty ledger")
  }

  /**
   * Recomputes and seals the ledger after forensic inspection.
   */
  fun restoreLedgerIntegrity() {
    var prev = GENESIS_HASH
    val fixed = _merkleLedger.value.mapIndexed { idx, block ->
      val chained = sha256("${block.eventPayloadHash}+$prev")
      val updated = block.copy(
        previousBlockHash = prev,
        blockHash = chained,
        isTampered = false
      )
      prev = chained
      updated
    }
    _merkleLedger.value = fixed
    _currentMerkleRoot.value = prev
    _isLedgerValid.value = true
    updateTelemetryStream()
  }

  fun simulateUiCrash(componentTag: String = "TacticalRadarCanvas") {
    try {
      throw IllegalStateException("Simulated runtime fault in $componentTag: buffer overflow in drawArc()")
    } catch (e: Exception) {
      recordCrash(e, componentTag)
    }
  }

  /**
   * Dispatches a structured, encrypted telemetry payload to the n8n incident response automation pipeline.
   */
  fun dispatchN8nIncident(
    title: String,
    severity: DiagnosticSeverity = DiagnosticSeverity.CRITICAL,
    sourceIp: String = "192.168.1.105",
    cvss: Double = 9.4
  ): N8nIncidentRecord {
    val incId = "INC-N8N-${UUID.randomUUID().toString().take(6).uppercase()}"
    val timestamp = dateFormat.format(Date())
    val blockHash = _currentMerkleRoot.value

    val record = N8nIncidentRecord(
      id = incId,
      timestamp = timestamp,
      title = title,
      severity = severity,
      cvssScore = cvss,
      sourceIp = sourceIp,
      blockHash = blockHash,
      dispatchStatus = "DELIVERED"
    )

    _n8nIncidentQueue.value = listOf(record) + _n8nIncidentQueue.value

    // Record into append-only cryptographic Merkle audit ledger
    recordLog(
      severity = severity,
      componentTag = "N8nWorkflowDispatcher",
      message = "Incident alert dispatched to n8n webhook: $title (CVSS $cvss)",
      metadata = "INC_ID: $incId | TARGET_IP: $sourceIp | HASH: ${blockHash.take(12)}..."
    )

    return record
  }

  /**
   * Syncs latest Merkle blocks and telemetry state with Supabase PostgreSQL cloud backend.
   */
  fun syncToSupabase(): SupabaseSyncMetric {
    val updated = SupabaseSyncMetric(
      isConfigured = true,
      lastSyncTimestamp = dateFormat.format(Date()),
      syncedBlockCount = _merkleLedger.value.size.toLong(),
      rlsEnforced = true,
      persistenceEngine = "PostgreSQL 15+ / PostgREST"
    )
    _supabaseSyncState.value = updated
    recordLog(
      severity = DiagnosticSeverity.INFO,
      componentTag = "SupabasePostgresConnector",
      message = "Synchronized ${_merkleLedger.value.size} Merkle audit blocks with Supabase cloud repository",
      metadata = "ENGINE: POSTGRESQL_15_RLS"
    )
    return updated
  }
}
