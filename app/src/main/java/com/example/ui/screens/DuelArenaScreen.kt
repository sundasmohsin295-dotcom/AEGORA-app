package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hardware.DynamicIconManager
import com.example.ui.components.CyberComboMultiplierCanvas
import com.example.ui.components.SecurityStatusModal
import com.example.ui.components.TacticalPanel
import com.example.ui.components.TacticalStatusLed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HighAlertCrimson
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceRaised
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateBorderBright
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTerminalGreen
import com.example.viewmodel.DuelArenaViewModel

@Composable
fun DuelArenaScreen(
  viewModel: DuelArenaViewModel,
  deepLinkSessionId: String? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsState()
  val currentScenario = viewModel.currentScenario
  val isAlertIconActive by DynamicIconManager.isAlertIconActive.collectAsState()

  var showAiMentorExpanded by remember { mutableStateOf(true) }
  var showTelemetryConsole by remember { mutableStateOf(false) }
  var showAuthVault by remember { mutableStateOf(deepLinkSessionId != null) }
  var showSettingsDashboard by remember { mutableStateOf(false) }
  var showSecurityModal by remember { mutableStateOf(false) }

  if (showSettingsDashboard) {
    SettingsDashboard(
      onNavigateBack = { showSettingsDashboard = false },
      modifier = modifier
    )
    return
  }

  if (showSecurityModal) {
    SecurityStatusModal(
      onDismiss = { showSecurityModal = false }
    )
  }

  if (showAuthVault) {
    AuthVaultScreen(
      onNavigateBack = { showAuthVault = false },
      claimedSessionId = deepLinkSessionId,
      modifier = modifier
    )
    return
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("duel_arena_scaffold"),
    containerColor = ObsidianBackground
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Top Industrial Action Bar
      TopActionBar(
        score = uiState.score,
        streak = uiState.comboStreak,
        isMuted = uiState.isAudioMuted,
        isAlertActive = isAlertIconActive,
        onToggleAudio = { viewModel.toggleAudio(context) },
        onOpenVault = { showAuthVault = true },
        onOpenSecurityStatus = { showSecurityModal = true },
        onOpenSettings = { showSettingsDashboard = true },
        onSimulateProcessDeath = {
          viewModel.simulateProcessDeath(88)
        },
        onReset = { viewModel.restartArena(context) }
      )

      // Alert Forensic Banner
      uiState.alertBannerMessage?.let { banner ->
        BannerAlert(message = banner)
      }

      // Tactical Telemetry HUD
      ThreatTelemetryHUD(
        playerShield = uiState.playerShieldIntegrity,
        adversaryBreach = uiState.adversaryBreachProgress,
        threatScore = uiState.systemThreatScore
      )

      // Fiery Cyan Multiplier (Best Game Award)
      if (uiState.comboStreak >= 3) {
        CyberComboMultiplierCanvas(
          comboStreak = uiState.comboStreak,
          modifier = Modifier.fillMaxWidth()
        )
      }

      if (uiState.isDuelComplete) {
        VictoryOrDefeatCard(
          playerShield = uiState.playerShieldIntegrity,
          finalScore = uiState.score,
          mitigatedCount = uiState.mitigatedCount,
          onRestart = { viewModel.restartArena(context) }
        )
      } else if (currentScenario != null) {
        // Active MITRE Tactical Frame
        MitreScenarioCard(
          scenario = currentScenario,
          currentIndex = uiState.currentScenarioIndex + 1,
          totalScenarios = uiState.scenarios.size
        )

        // Gemini AI Cyber Mentor Section
        AiMentorBriefingCard(
          isExpanded = showAiMentorExpanded,
          isLoading = uiState.isAiLoading,
          briefing = uiState.aiMentorBriefing,
          onToggleExpand = { showAiMentorExpanded = !showAiMentorExpanded },
          onRefreshAdvice = { viewModel.requestAiMentorBriefing() }
        )

        // Active Countermeasure Grid
        DefenseActionsSection(
          options = currentScenario.availableOptions,
          onSelectAction = { action ->
            viewModel.onSelectDefenseAction(context, action)
          }
        )
      }

      // Real-time Kernel Forensic Stream
      TelemetryLogConsole(
        isExpanded = showTelemetryConsole,
        logs = uiState.logs,
        onToggleConsole = { showTelemetryConsole = !showTelemetryConsole }
      )
    }
  }
}

@Composable
private fun TopActionBar(
  score: Int,
  streak: Int,
  isMuted: Boolean,
  isAlertActive: Boolean,
  onToggleAudio: () -> Unit,
  onOpenVault: () -> Unit,
  onOpenSecurityStatus: () -> Unit,
  onOpenSettings: () -> Unit,
  onSimulateProcessDeath: () -> Unit,
  onReset: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("top_action_bar"),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = "CYBER DUEL ARENA",
        color = ElectricCyan,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "SCORE: $score",
          color = TextPrimary,
          fontWeight = FontWeight.SemiBold,
          fontFamily = FontFamily.Monospace,
          fontSize = 13.sp
        )
        if (streak > 0) {
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "[CHAIN x$streak]",
            color = TacticalAmber,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )
        }
      }
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(CutCornerShape(2.dp))
          .background(if (isAlertActive) HighAlertCrimson else TacticalEmerald)
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = if (isAlertActive) "[CRIT]" else "[NORM]",
          color = Color.White,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }

      IconButton(
        onClick = onOpenSecurityStatus,
        modifier = Modifier.testTag("open_security_modal_action_button")
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Security Status & Enclave",
          tint = ElectricCyan
        )
      }

      IconButton(
        onClick = onOpenSettings,
        modifier = Modifier.testTag("open_settings_action_button")
      ) {
        Icon(
          imageVector = Icons.Default.Tune,
          contentDescription = "SRE Telemetry & Settings",
          tint = ElectricCyan
        )
      }

      IconButton(
        onClick = onOpenVault,
        modifier = Modifier.testTag("open_vault_action_button")
      ) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Security Vault & Pro",
          tint = ElectricCyan
        )
      }

      IconButton(
        onClick = onSimulateProcessDeath,
        modifier = Modifier.testTag("simulate_process_death_button")
      ) {
        Icon(
          imageVector = Icons.Default.Dangerous,
          contentDescription = "Simulate Process Death",
          tint = HighAlertCrimson
        )
      }

      IconButton(
        onClick = onToggleAudio,
        modifier = Modifier.testTag("toggle_audio_button")
      ) {
        Icon(
          imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
          contentDescription = "Toggle Audio Sonification",
          tint = TextMuted
        )
      }

      IconButton(
        onClick = onReset,
        modifier = Modifier.testTag("reset_arena_button")
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = "Reset Arena",
          tint = TextMuted
        )
      }
    }
  }
}

@Composable
private fun BannerAlert(message: String) {
  val isSuccess = message.contains("NEUTRALIZED") || message.contains("VICTORY")
  val borderColor = if (isSuccess) TacticalEmerald else HighAlertCrimson
  val chipShape = CutCornerShape(4.dp)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(chipShape)
      .background(if (isSuccess) Color(0xFF042F2E) else Color(0xFF450A0A))
      .border(1.dp, borderColor, chipShape)
      .padding(12.dp)
      .testTag("banner_alert")
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
        contentDescription = null,
        tint = if (isSuccess) TacticalEmerald else HighAlertCrimson,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = message,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

@Composable
private fun ThreatTelemetryHUD(
  playerShield: Int,
  adversaryBreach: Int,
  threatScore: Int
) {
  val animatedShield by animateFloatAsState(targetValue = playerShield / 100f, label = "shield")
  val animatedBreach by animateFloatAsState(targetValue = adversaryBreach / 100f, label = "breach")

  val hudStatusLed = when {
    threatScore >= 80 -> TacticalStatusLed.ALERT_CRIMSON
    threatScore >= 50 -> TacticalStatusLed.STANDBY_AMBER
    else -> TacticalStatusLed.ACTIVE_CYAN
  }

  TacticalPanel(
    titleTag = "[HUD-01] // SYSTEM_TELEMETRY",
    subtitle = "SHIELD INTEGRITY VS INFILTRATION DEPTH",
    memoryOffset = "RADAR: $threatScore%",
    statusLed = hudStatusLed,
    modifier = Modifier.testTag("threat_telemetry_hud")
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
      // Defensive Shield Bar
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "[SHIELD_INTEGRITY]",
            color = TextMuted,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "$playerShield%",
            color = ElectricCyan,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
          progress = { animatedShield },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(CutCornerShape(2.dp)),
          color = ElectricCyan,
          trackColor = SlateBorder
        )
      }

      // Adversary Infiltration Depth Bar
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "[ADVERSARY_INFILTRATION]",
            color = TextMuted,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "$adversaryBreach%",
            color = HighAlertCrimson,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
          progress = { animatedBreach },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(CutCornerShape(2.dp)),
          color = HighAlertCrimson,
          trackColor = SlateBorder
        )
      }
    }
  }
}

@Composable
private fun MitreScenarioCard(
  scenario: com.example.viewmodel.ThreatScenario,
  currentIndex: Int,
  totalScenarios: Int
) {
  TacticalPanel(
    titleTag = "[MITRE-${scenario.mitreId}] // ${scenario.mitreTactic.uppercase()}",
    subtitle = "VECTOR: ${scenario.title}",
    memoryOffset = "SEQ $currentIndex/$totalScenarios",
    statusLed = TacticalStatusLed.ALERT_CRIMSON,
    modifier = Modifier.testTag("mitre_scenario_card")
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "ADVERSARY_PROFILE: ${scenario.adversaryProfile}",
        color = TacticalAmber,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold
      )

      Text(
        text = scenario.description,
        color = TextMuted,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = "INDICATORS OF COMPROMISE [IOCs]:",
        color = TextPrimary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )

      scenario.iocList.forEach { ioc ->
        Row(
          modifier = Modifier.padding(vertical = 1.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.BugReport,
            contentDescription = null,
            tint = HighAlertCrimson,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = ioc,
            color = TextPrimary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}

@Composable
private fun AiMentorBriefingCard(
  isExpanded: Boolean,
  isLoading: Boolean,
  briefing: String,
  onToggleExpand: () -> Unit,
  onRefreshAdvice: () -> Unit
) {
  TacticalPanel(
    titleTag = "[AI-SOC-03] // GEMINI_NEURAL_MENTOR",
    subtitle = "REAL-TIME MITIGATION KNOWLEDGE GRAPH",
    memoryOffset = if (isLoading) "ANALYZING..." else "READY",
    isProcessing = isLoading,
    statusLed = TacticalStatusLed.ACTIVE_CYAN,
    headerTrailingContent = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onRefreshAdvice,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh Advice",
            tint = ElectricCyan,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (isExpanded) "[-]" else "[+]",
          color = TextDim,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.clickable { onToggleExpand() }
        )
      }
    },
    modifier = Modifier.testTag("ai_mentor_card")
  ) {
    AnimatedVisibility(visible = isExpanded) {
      Column {
        if (isLoading) {
          Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(14.dp),
              color = ElectricCyan,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Parsing MITRE ATT&CK mitigation vectors...",
              color = TextMuted,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        } else {
          Text(
            text = briefing.ifBlank { "Ready to evaluate active telemetry IOCs." },
            color = TextPrimary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}

@Composable
private fun DefenseActionsSection(
  options: List<String>,
  onSelectAction: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("defense_actions_section")
  ) {
    Text(
      text = "[DEPLOY_ACTIVE_COUNTERMEASURE]:",
      color = TextMuted,
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    options.forEachIndexed { index, option ->
      val btnShape = CutCornerShape(4.dp)
      Button(
        onClick = { onSelectAction(option) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 3.dp)
          .testTag("defense_action_button_$index"),
        shape = btnShape,
        colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderBright)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = ElectricCyan,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "[0${index + 1}] $option",
            color = TextPrimary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}

@Composable
private fun TelemetryLogConsole(
  isExpanded: Boolean,
  logs: List<String>,
  onToggleConsole: () -> Unit
) {
  TacticalPanel(
    titleTag = "[LOG-IOC] // FORENSIC_KERNEL_STREAM",
    subtitle = "LIVE TELEMETRY SHIFT LOG",
    memoryOffset = if (isExpanded) "[-]" else "[+]",
    statusLed = TacticalStatusLed.SECURE_EMERALD,
    headerTrailingContent = {
      Text(
        text = if (isExpanded) "[HIDE]" else "[STREAM]",
        color = TextDim,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.clickable { onToggleConsole() }
      )
    },
    modifier = Modifier.testTag("telemetry_log_console")
  ) {
    AnimatedVisibility(visible = isExpanded) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .verticalScroll(rememberScrollState())
      ) {
        logs.takeLast(14).forEach { log ->
          Text(
            text = log,
            color = when {
              log.contains("ALERT") || log.contains("FAILED") -> HighAlertCrimson
              log.contains("SUCCESS") || log.contains("PURGED") -> TextTerminalGreen
              else -> TextMuted
            },
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 14.sp
          )
        }
      }
    }
  }
}

@Composable
private fun VictoryOrDefeatCard(
  playerShield: Int,
  finalScore: Int,
  mitigatedCount: Int,
  onRestart: () -> Unit
) {
  val isVictory = playerShield > 0
  TacticalPanel(
    titleTag = "[EVALUATION] // ARENA_MISSION_SUMMARY",
    subtitle = if (isVictory) "INFRASTRUCTURE SECURED" else "CRITICAL BREACH RECORDED",
    statusLed = if (isVictory) TacticalStatusLed.SECURE_EMERALD else TacticalStatusLed.ALERT_CRIMSON,
    modifier = Modifier.testTag("victory_defeat_card")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = if (isVictory) Icons.Default.Security else Icons.Default.Dangerous,
        contentDescription = null,
        tint = if (isVictory) TacticalEmerald else HighAlertCrimson,
        modifier = Modifier.size(40.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = if (isVictory) "STATUS: PERIMETER INTACT" else "STATUS: SYSTEM COMPROMISED",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        fontFamily = FontFamily.Monospace
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "SCORE: $finalScore PTS // MITIGATIONS: $mitigatedCount",
        color = TextMuted,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace
      )

      Spacer(modifier = Modifier.height(12.dp))

      val btnShape = CutCornerShape(4.dp)
      Button(
        onClick = onRestart,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isVictory) TacticalEmerald else HighAlertCrimson
        ),
        shape = btnShape,
        modifier = Modifier.testTag("restart_arena_final_button")
      ) {
        Text(
          text = "[ENGAGE NEXT CAMPAIGN]",
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }
    }
  }
}
