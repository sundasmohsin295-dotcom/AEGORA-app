package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.LadderPlatform
import com.example.model.TerminalLadderLevel
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

@Composable
fun TerminalLadderView(
  modifier: Modifier = Modifier
) {
  var selectedPlatform by remember { mutableStateOf(LadderPlatform.LINUX_BANDIT) }
  val linuxLadder by AegoraRepository.linuxLadder.collectAsState()
  val powershellLadder by AegoraRepository.powershellLadder.collectAsState()

  val currentLadder = if (selectedPlatform == LadderPlatform.LINUX_BANDIT) linuxLadder else powershellLadder
  var selectedLevelNumber by remember { mutableStateOf(0) }
  val activeLevel = currentLadder.find { it.levelNumber == selectedLevelNumber } ?: currentLadder.first()

  var terminalCommand by remember { mutableStateOf("") }
  var terminalHistory by remember { mutableStateOf(listOf("Welcome to Aegora Terminal Ladder Range.", "Type your command or choose quick snippets below.")) }
  var showHint by remember { mutableStateOf(false) }
  var feedbackMessage by remember { mutableStateOf<String?>(null) }

  // Sync selected level if switched platform
  LaunchedEffect(selectedPlatform) {
    selectedLevelNumber = 0
    terminalCommand = ""
    feedbackMessage = null
    terminalHistory = listOf(
      if (selectedPlatform == LadderPlatform.LINUX_BANDIT)
        "Connected to aegora-bandit.sandbox.internal [Linux x86_64]\nType Linux CLI commands to locate credentials."
      else
        "Windows PowerShell v7.4.2 [Host: AEGORACORP-WS01]\nUse PowerShell cmdlets and pipeline filtering."
    )
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Platform Switcher (Bandit Linux vs UnderTheWire PowerShell)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Surface(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(8.dp))
          .clickable { selectedPlatform = LadderPlatform.LINUX_BANDIT }
          .border(
            1.dp,
            if (selectedPlatform == LadderPlatform.LINUX_BANDIT) CyberCyan else CyberBorder,
            RoundedCornerShape(8.dp)
          ),
        color = if (selectedPlatform == LadderPlatform.LINUX_BANDIT) CyberSurfaceElevated else CyberSurface
      ) {
        Row(
          modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            Icons.Default.Terminal,
            contentDescription = null,
            tint = if (selectedPlatform == LadderPlatform.LINUX_BANDIT) CyberCyan else TextSecondaryDark,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Linux Bandit Ladder",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = if (selectedPlatform == LadderPlatform.LINUX_BANDIT) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (selectedPlatform == LadderPlatform.LINUX_BANDIT) CyberCyan else TextSecondaryDark
          )
        }
      }

      Surface(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(8.dp))
          .clickable { selectedPlatform = LadderPlatform.WINDOWS_POWERSHELL }
          .border(
            1.dp,
            if (selectedPlatform == LadderPlatform.WINDOWS_POWERSHELL) CyberEmerald else CyberBorder,
            RoundedCornerShape(8.dp)
          ),
        color = if (selectedPlatform == LadderPlatform.WINDOWS_POWERSHELL) CyberSurfaceElevated else CyberSurface
      ) {
        Row(
          modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            Icons.Default.Code,
            contentDescription = null,
            tint = if (selectedPlatform == LadderPlatform.WINDOWS_POWERSHELL) CyberEmerald else TextSecondaryDark,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "PowerShell Ladder",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = if (selectedPlatform == LadderPlatform.WINDOWS_POWERSHELL) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (selectedPlatform == LadderPlatform.WINDOWS_POWERSHELL) CyberEmerald else TextSecondaryDark
          )
        }
      }
    }

    // 2. Sequential Level Progress Carousel
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "SEQUENTIAL UNLOCK PROGRESSION",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
        Text(
          text = "${currentLadder.count { it.isCompleted }} / ${currentLadder.size} Solved",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = CyberEmerald
        )
      }
      Spacer(modifier = Modifier.height(8.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(currentLadder) { level ->
          val isSelected = level.levelNumber == selectedLevelNumber
          val isUnlocked = level.isUnlocked
          val isCompleted = level.isCompleted

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = when {
              isSelected -> CyberSurfaceElevated
              isCompleted -> CyberSurface
              isUnlocked -> CyberSurface
              else -> CyberBackground
            },
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              when {
                isSelected -> CyberCyan
                isCompleted -> CyberEmerald
                isUnlocked -> CyberBorder
                else -> CyberBorderSubtle
              }
            ),
            modifier = Modifier
              .clickable(enabled = isUnlocked) {
                selectedLevelNumber = level.levelNumber
                feedbackMessage = null
                showHint = false
              }
              .testTag("ladder_level_${level.levelNumber}")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (isCompleted) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = CyberEmerald, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
              } else if (!isUnlocked) {
                Icon(Icons.Default.Lock, contentDescription = "Locked", tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
              }
              Text(
                text = "Lvl ${level.levelNumber}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = when {
                  isSelected -> CyberCyan
                  isCompleted -> CyberEmerald
                  isUnlocked -> TextPrimaryDark
                  else -> TextSecondaryDark
                }
              )
            }
          }
        }
      }
    }

    // 3. Active Level Directive Card
    CyberCard(
      borderColor = if (activeLevel.isCompleted) CyberEmerald.copy(alpha = 0.5f) else CyberCyan.copy(alpha = 0.5f),
      backgroundColor = CyberSurface
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "LEVEL ${activeLevel.levelNumber} DIRECTIVE • ${activeLevel.skillArea.uppercase()}",
            style = MaterialTheme.typography.labelSmall,
            color = CyberCyan
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = activeLevel.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
        }
        if (activeLevel.isCompleted) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = CyberEmerald.copy(alpha = 0.2f)
          ) {
            Text(
              text = "PASSED",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberEmerald,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = activeLevel.goalDescription,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = { showHint = !showHint },
          contentPadding = PaddingValues(0.dp)
        ) {
          Icon(Icons.Default.HelpOutline, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            if (showHint) "Hide Hint" else "Reveal Minimal Hint (No hand-holding)",
            style = MaterialTheme.typography.labelSmall,
            color = CyberAmber
          )
        }

        Text(
          text = "+${activeLevel.xpReward} Skill Graph XP",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = CyberGold
        )
      }

      AnimatedVisibility(visible = showHint) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CyberAmber.copy(alpha = 0.1f),
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            activeLevel.hints.forEach { hint ->
              Text("• $hint", style = MaterialTheme.typography.bodySmall, color = CyberAmber)
            }
          }
        }
      }
    }

    // 4. Interactive Live Terminal Emulator
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color(0xFF0D1117),
      border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        // Terminal Window Header Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CyberCrimson))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CyberAmber))
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CyberEmerald))
          }
          Text(
            text = if (selectedPlatform == LadderPlatform.LINUX_BANDIT) "bash • 80x24" else "pwsh • 80x24",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
            color = TextSecondaryDark
          )
        }

        Divider(
          modifier = Modifier.padding(vertical = 8.dp),
          color = CyberBorderSubtle
        )

        // Terminal Output Stream
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 220.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          terminalHistory.takeLast(6).forEach { line ->
            Text(
              text = line,
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 16.sp
              ),
              color = when {
                line.startsWith("[+]") || line.startsWith("[✓]") || line.contains("succeeded") -> CyberEmerald
                line.startsWith("bash: command") || line.contains("error") -> CyberCrimson
                line.startsWith("aegora-bandit") || line.startsWith("PS C:") -> CyberCyan
                else -> Color(0xFFC9D1D9)
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Snippets / Autocomplete Pills
        Text("QUICK EXECUTION SNIPPETS:", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(activeLevel.sampleCommands) { cmd ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
              modifier = Modifier.clickable {
                terminalCommand = cmd
              }
            ) {
              Text(
                text = cmd,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Command Input Field
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${activeLevel.systemPrompt} ",
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = CyberCyan
          )
          TextField(
            value = terminalCommand,
            onValueChange = { terminalCommand = it },
            placeholder = {
              Text("Enter command...", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = TextSecondaryDark)
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedTextColor = Color(0xFF58A6FF),
              unfocusedTextColor = Color(0xFF58A6FF),
              cursorColor = CyberCyan,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("terminal_input_field"),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
              onSend = {
                if (terminalCommand.isNotBlank()) {
                  val cmdToRun = terminalCommand
                  terminalCommand = ""
                  val (isSuccess, output) = AegoraRepository.submitTerminalCommand(
                    platform = selectedPlatform,
                    levelNumber = activeLevel.levelNumber,
                    commandInput = cmdToRun
                  )
                  terminalHistory = terminalHistory + "${activeLevel.systemPrompt} $cmdToRun" + output
                  if (isSuccess && activeLevel.levelNumber + 1 < currentLadder.size) {
                    selectedLevelNumber = activeLevel.levelNumber + 1
                  }
                }
              }
            )
          )

          IconButton(
            onClick = {
              if (terminalCommand.isNotBlank()) {
                val cmdToRun = terminalCommand
                terminalCommand = ""
                val (isSuccess, output) = AegoraRepository.submitTerminalCommand(
                  platform = selectedPlatform,
                  levelNumber = activeLevel.levelNumber,
                  commandInput = cmdToRun
                )
                terminalHistory = terminalHistory + "${activeLevel.systemPrompt} $cmdToRun" + output
                if (isSuccess && activeLevel.levelNumber + 1 < currentLadder.size) {
                  selectedLevelNumber = activeLevel.levelNumber + 1
                }
              }
            },
            modifier = Modifier.testTag("run_command_button")
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = CyberCyan)
          }
        }
      }
    }
  }
}
