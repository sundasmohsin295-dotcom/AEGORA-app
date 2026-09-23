package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.CyberSonificationManager
import com.example.core.result.AegoraResult
import com.example.data.AegoraEncryptedDb
import com.example.data.GeminiMentorService
import com.example.hardware.DynamicIconManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ThreatScenario(
  val id: String,
  val title: String,
  val mitreTactic: String,
  val mitreId: String,
  val description: String,
  val threatScore: Int,
  val adversaryProfile: String,
  val iocList: List<String>,
  val correctMitigation: String,
  val availableOptions: List<String>
)

data class DuelArenaUiState(
  val scenarios: List<ThreatScenario> = emptyList(),
  val currentScenarioIndex: Int = 0,
  val playerShieldIntegrity: Int = 100,
  val adversaryBreachProgress: Int = 20,
  val systemThreatScore: Int = 45,
  val score: Int = 0,
  val comboStreak: Int = 0,
  val mitigatedCount: Int = 0,
  val logs: List<String> = emptyList(),
  val aiMentorBriefing: String = "",
  val isAiLoading: Boolean = false,
  val isAudioMuted: Boolean = false,
  val isProcessDeathSimulated: Boolean = false,
  val isDuelComplete: Boolean = false,
  val alertBannerMessage: String? = null
)

class DuelArenaViewModel : ViewModel() {

  private val mentorService = GeminiMentorService()

  private val _uiState = MutableStateFlow(
    DuelArenaUiState(
      scenarios = getInitialScenarios(),
      logs = listOf(
        "[00:00.01] SYSTEM_BOOT: Aegora Cyber Duel Core initialized.",
        "[00:00.04] TELEMETRY_ACTIVE: Real-time MITRE ATT&CK sensor online.",
        "[00:00.09] THREAT_FEED: Threat vector #T1059.001 detected on perimeter."
      )
    )
  )
  val uiState: StateFlow<DuelArenaUiState> = _uiState.asStateFlow()

  val currentScenario: ThreatScenario?
    get() {
      val state = _uiState.value
      return state.scenarios.getOrNull(state.currentScenarioIndex)
    }

  init {
    requestAiMentorBriefing()
  }

  fun onSelectDefenseAction(context: Context, chosenAction: String) {
    val scenario = currentScenario ?: return
    val sonification = CyberSonificationManager.getInstance(context)

    sonification.playMicroClick()

    if (chosenAction == scenario.correctMitigation) {
      // Successful Mitigation
      val newScore = _uiState.value.score + 250 + (_uiState.value.comboStreak * 50)
      val newStreak = _uiState.value.comboStreak + 1
      val newThreatScore = (_uiState.value.systemThreatScore - 20).coerceAtLeast(10)
      val newBreach = (_uiState.value.adversaryBreachProgress - 15).coerceAtLeast(0)
      val newMitigated = _uiState.value.mitigatedCount + 1

      sonification.playThreatNeutralizedSound()
      DynamicIconManager.onMitreThreatMitigated(context)
      DynamicIconManager.updateIconForThreatScore(context, newThreatScore)

      // Persist high score and telemetry count securely in SQLCipher DB
      viewModelScope.launch {
        val db = AegoraEncryptedDb.getInstance(context)
        db.saveHighScore(newScore)
        db.recordMitigatedThreat(scenario.id)
      }

      appendLog("[DEFENSE_SUCCESS] Mitigation confirmed: '$chosenAction'. Threat neutralized.")

      _uiState.value = _uiState.value.copy(
        score = newScore,
        comboStreak = newStreak,
        systemThreatScore = newThreatScore,
        adversaryBreachProgress = newBreach,
        mitigatedCount = newMitigated,
        alertBannerMessage = "THREAT NEUTRALIZED! +250 PTS"
      )

      advanceToNextScenario(context)
    } else {
      // Countermeasure failure
      val penalty = 20
      val newShield = (_uiState.value.playerShieldIntegrity - penalty).coerceAtLeast(0)
      val newBreach = (_uiState.value.adversaryBreachProgress + 25).coerceAtMost(100)
      val newThreatScore = (_uiState.value.systemThreatScore + 30).coerceAtMost(100)

      sonification.playGlitchSound()
      sonification.playZeroDayAlert()
      DynamicIconManager.updateIconForThreatScore(context, newThreatScore)

      appendLog("[EXPLOIT_ALERT] Action failed! Adversary bypassed filter with payload.")

      _uiState.value = _uiState.value.copy(
        playerShieldIntegrity = newShield,
        comboStreak = 0,
        adversaryBreachProgress = newBreach,
        systemThreatScore = newThreatScore,
        alertBannerMessage = "COUNTERMEASURE BYPASS! SHIELD DAMAGE -20%"
      )

      if (newShield <= 0 || newBreach >= 100) {
        _uiState.value = _uiState.value.copy(isDuelComplete = true)
      }
    }
  }

  private fun advanceToNextScenario(context: Context) {
    viewModelScope.launch {
      delay(1200)
      val nextIndex = _uiState.value.currentScenarioIndex + 1
      if (nextIndex < _uiState.value.scenarios.size) {
        _uiState.value = _uiState.value.copy(
          currentScenarioIndex = nextIndex,
          alertBannerMessage = null
        )
        val nextScenario = _uiState.value.scenarios[nextIndex]
        DynamicIconManager.updateIconForThreatScore(context, nextScenario.threatScore)
        appendLog("[THREAT_ENGAGE] Inbound target: ${nextScenario.title} [${nextScenario.mitreId}]")
        requestAiMentorBriefing()
      } else {
        _uiState.value = _uiState.value.copy(
          isDuelComplete = true,
          alertBannerMessage = "ALL VECTORS CONTAINED! ARENA VICTORY."
        )
      }
    }
  }

  fun requestAiMentorBriefing() {
    val scenario = currentScenario ?: return
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isAiLoading = true)
      val result = mentorService.analyzeThreat(
        scenarioTitle = scenario.title,
        mitreTactic = scenario.mitreTactic,
        threatScore = scenario.threatScore,
        iocList = scenario.iocList
      )
      val briefing = result.recover { failure ->
        mentorService.getLocalMentorAdvice(scenario.title, scenario.mitreTactic, scenario.threatScore)
      }
      _uiState.value = _uiState.value.copy(
        aiMentorBriefing = briefing,
        isAiLoading = false
      )
    }
  }

  /**
   * Simulates high-stress Android process death or threat corruption.
   * MUST BE PUBLIC for UI test controls and developer telemetry simulations.
   */
  fun simulateProcessDeath(corruptedThreatScore: Int = 85) {
    val current = _uiState.value
    _uiState.value = current.copy(
      systemThreatScore = corruptedThreatScore,
      isProcessDeathSimulated = true,
      alertBannerMessage = "KERNEL OVERRIDE: Process kill simulated. Threat score spiked to $corruptedThreatScore%"
    )
    appendLog("[KERNEL_WARNING] OS Process death injection executed. Threat score = $corruptedThreatScore.")
  }

  fun restartArena(context: Context) {
    val initial = getInitialScenarios()
    _uiState.value = DuelArenaUiState(
      scenarios = initial,
      currentScenarioIndex = 0,
      playerShieldIntegrity = 100,
      adversaryBreachProgress = 20,
      systemThreatScore = initial.first().threatScore,
      score = 0,
      comboStreak = 0,
      mitigatedCount = 0,
      logs = listOf("[RESET] Security perimeter refreshed. Ready for engagement."),
      isDuelComplete = false,
      isProcessDeathSimulated = false
    )
    DynamicIconManager.setDefaultIcon(context)
    CyberSonificationManager.getInstance(context).playSuccessChimeTone()
    requestAiMentorBriefing()
  }

  fun toggleAudio(context: Context) {
    val sonification = CyberSonificationManager.getInstance(context)
    val muted = sonification.toggleMute()
    _uiState.value = _uiState.value.copy(isAudioMuted = muted)
  }

  private fun appendLog(line: String) {
    val timestamp = System.currentTimeMillis() % 100000 / 1000.0
    val formatted = String.format("[%.2fs] %s", timestamp, line)
    val currentLogs = _uiState.value.logs.takeLast(25)
    _uiState.value = _uiState.value.copy(logs = currentLogs + formatted)
  }

  companion object {
    private fun getInitialScenarios(): List<ThreatScenario> {
      return listOf(
        ThreatScenario(
          id = "SCENARIO_01",
          title = "PowerShell Reflective DLL Injection",
          mitreTactic = "Execution",
          mitreId = "T1059.001",
          description = "An unquoted service path was leveraged to launch an obfuscated PowerShell cradle loading an in-memory reflective DLL.",
          threatScore = 78,
          adversaryProfile = "APT-29 (Cozy Bear)",
          iocList = listOf("powershell.exe -enc JABzAD0A...", "PID 4192 unbacked memory region", "Outbound beacon to 185.220.101.4:443"),
          correctMitigation = "ConstrainedLanguage Mode & AMFI Memory Kill",
          availableOptions = listOf(
            "Restart IIS Web Application Pool",
            "ConstrainedLanguage Mode & AMFI Memory Kill",
            "Increase Cloud Logging Retention to 90 Days",
            "Clear Local DNS Resolver Cache"
          )
        ),
        ThreatScenario(
          id = "SCENARIO_02",
          title = "Spring4Shell Remote Code Execution",
          mitreTactic = "Initial Access",
          mitreId = "T1190",
          description = "Adversary exploiting CVE-2022-22965 via DataBinder property binding allowing arbitrary JSP webshell upload on Tomcat container.",
          threatScore = 92,
          adversaryProfile = "Fin7 Financial Cybercrime Syndicate",
          iocList = listOf("POST /helloworld/greeting class.module.classLoader...", "File written: /webapps/ROOT/shell.jsp", "Spawning /bin/sh child"),
          correctMitigation = "WAF Regex Filter & Tomcat ClassLoader Patch",
          availableOptions = listOf(
            "WAF Regex Filter & Tomcat ClassLoader Patch",
            "Send Phishing Warning Email to All Staff",
            "Change Default MySQL Admin Password",
            "Export Thread Dump for Forensic Audit"
          )
        ),
        ThreatScenario(
          id = "SCENARIO_03",
          title = "OAuth 2.0 Illicit Consent Grant",
          mitreTactic = "Credential Access",
          mitreId = "T1078.004",
          description = "Malicious rogue cloud application registered under tenant inducing high-privilege users to grant Mail.ReadWrite and Files.ReadWrite.All delegated scopes.",
          threatScore = 65,
          adversaryProfile = "Midnight Blizzard",
          iocList = listOf("AppID 7e8b11fa granted offline_access", "Graph API bulk mailbox download", "IP location: Tor Exit Node"),
          correctMitigation = "Revoke OAuth App Consent & Invalidate Refresh Tokens",
          availableOptions = listOf(
            "Disable Outbound ICMP Ping Echo",
            "Revoke OAuth App Consent & Invalidate Refresh Tokens",
            "Block Port 80 HTTP Globally",
            "Update SSL Certificate Fingerprint"
          )
        ),
        ThreatScenario(
          id = "SCENARIO_04",
          title = "Kerberoasting Service Account Ticket Extraction",
          mitreTactic = "Privilege Escalation",
          mitreId = "T1558.003",
          description = "Adversary requesting RC4-encrypted Kerberos TGS service tickets for SPNs with weak passwords to crack offline via hashcat.",
          threatScore = 70,
          adversaryProfile = "Wizard Spider (TrickBot/Ryuk)",
          iocList = listOf("Event ID 4769 with Ticket Encryption Type 0x17", "Mass TGS requests from single workstation"),
          correctMitigation = "Upgrade SPNs to AES-256 & 25-Char Managed Service Accounts",
          availableOptions = listOf(
            "Restart Domain Controller Service",
            "Upgrade SPNs to AES-256 & 25-Char Managed Service Accounts",
            "Flush ARP Routing Tables",
            "Disable Local Windows Defender Firewall"
          )
        ),
        ThreatScenario(
          id = "SCENARIO_05",
          title = "Ransomware Volume Shadow Copy Deletion",
          mitreTactic = "Impact",
          mitreId = "T1486",
          description = "High-velocity encryption payload invoking 'vssadmin.exe delete shadows /all /quiet' and bcdedit to inhibit system recovery.",
          threatScore = 98,
          adversaryProfile = "LockBit 3.0",
          iocList = listOf("Process: vssadmin delete shadows", "Mass .lockbit file renames across shares", "Ransom note README.txt created"),
          correctMitigation = "Isolate Network Adapter & EDR ShadowCopy Tamper Guard",
          availableOptions = listOf(
            "Isolate Network Adapter & EDR ShadowCopy Tamper Guard",
            "Pay Monero Ransom to Tor Hidden Service",
            "Reboot Host into Safe Mode",
            "Compress Backup Archive into ZIP"
          )
        )
      )
    }
  }
}

private fun CyberSonificationManager.playSuccessChimeTone() {
  playThreatNeutralizedSound()
}
