package com.example.ui.screens

import androidx.compose.animation.*
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
import com.example.model.AdversaryMutationPhase
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@Composable
fun ShadowRangeScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shadowState by AegoraRepository.shadowState.collectAsState()
  var cliInput by remember { mutableStateOf("") }
  var cliHistory by remember { mutableStateOf(listOf("Shadow Adversary Sandbox initialized.", "Type 'help' or click quick commands below.")) }
  var showCanaryDialog by remember { mutableStateOf(false) }
  var canaryName by remember { mutableStateOf("AWS_BACKUP_ACCESS_KEY") }
  var canaryType by remember { mutableStateOf("AWS IAM Secret") }
  var canaryHost by remember { mutableStateOf("PROD-DC01") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(PureBlack)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // 1. Top Header Banner
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(CyberSurfaceElevated)
            .border(1.dp, CyberBorderSubtle, CircleShape)
        ) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan, modifier = Modifier.size(20.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(if (shadowState.isContained) CyberEmerald else CyberAmber)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (shadowState.isContained) "ZERO-DAY NEUTRALIZED" else "LIVE POLYMORPHIC ADVERSARY",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = if (shadowState.isContained) CyberEmerald else CyberAmber
          )
        }
      }
    }

    // 2. Adversary Threat Card
    item {
      CyberCard(
        borderColor = if (shadowState.isContained) CyberEmerald else NeonPink,
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "AUTONOMOUS ADVERSARY: ${shadowState.threatActorName}",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Target: ${shadowState.targetedHost} | C2: ${shadowState.activeAdversaryIp}",
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              color = TextSecondaryDark
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (shadowState.isContained) CyberEmerald.copy(alpha = 0.2f) else NeonPink.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (shadowState.isContained) CyberEmerald else NeonPink)
          ) {
            Text(
              text = "${shadowState.compromiseLevelPercent}% COMPROMISED",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              color = if (shadowState.isContained) CyberEmerald else NeonPink,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mutation Phase Indicator
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(shadowState.currentPhase.colorHex).copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(shadowState.currentPhase.colorHex))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.AutoMode, contentDescription = null, tint = Color(shadowState.currentPhase.colorHex), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "CURRENT MUTATION STATE",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = FontFamily.Monospace),
                color = TextSecondaryDark
              )
              Text(
                text = shadowState.currentPhase.displayName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = Color(shadowState.currentPhase.colorHex)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Adversary AI Mutation Log
        Text(
          text = "DYNAMIC TACTIC MUTATION HISTORY",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = CyberCyan
        )
        Spacer(modifier = Modifier.height(6.dp))
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CodeBackground)
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          shadowState.mutationHistory.takeLast(4).forEach { mutation ->
            Text(
              text = "• $mutation",
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
              color = if (mutation.contains("DEFENDER")) CyberCyan else if (mutation.contains("ADVERSARY")) NeonPink else CodeGreen
            )
          }
        }
      }
    }

    // 3. MITRE ATT&CK Hexagonal Matrix Progression
    item {
      CyberCard(
        borderColor = CyberCyan.copy(alpha = 0.4f),
        backgroundColor = CyberSurface
      ) {
        Text(
          text = "MITRE ATT&CK® REAL-TIME MATRIX",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = CyberCyan
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(shadowState.tactics) { tactic ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = when {
                tactic.isMitigated -> CyberEmerald.copy(alpha = 0.15f)
                tactic.isCurrentActive -> NeonPink.copy(alpha = 0.2f)
                else -> CyberSurfaceElevated
              },
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when {
                  tactic.isMitigated -> CyberEmerald
                  tactic.isCurrentActive -> NeonPink
                  else -> CyberBorderSubtle
                }
              ),
              modifier = Modifier.width(130.dp)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = when {
                      tactic.isMitigated -> Icons.Default.CheckCircle
                      tactic.isCurrentActive -> Icons.Default.Warning
                      else -> Icons.Default.Circle
                    },
                    contentDescription = null,
                    tint = when {
                      tactic.isMitigated -> CyberEmerald
                      tactic.isCurrentActive -> NeonPink
                      else -> TextTertiaryDark
                    },
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = tactic.tacticCode,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                    color = TextSecondaryDark
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = tactic.tacticName,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                  color = TextPrimaryDark,
                  maxLines = 1
                )
                Text(
                  text = "${tactic.techniqueId}: ${tactic.techniqueName}",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                  color = if (tactic.isCurrentActive) NeonPink else TextSecondaryDark,
                  maxLines = 2
                )
              }
            }
          }
        }
      }
    }

    // 4. Deception Grid Studio (Canary Defense)
    item {
      CyberCard(
        borderColor = CyberEmerald.copy(alpha = 0.4f),
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "DECEPTION GRID & CANARY DEFENSE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = CyberEmerald
            )
            Text(
              text = "Lure adversary bots into synthetic traps",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }

          Button(
            onClick = { showCanaryDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Plant Canary", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          shadowState.deployedCanaries.forEach { canary ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (canary.isTripped) NeonPink.copy(alpha = 0.12f) else CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (canary.isTripped) NeonPink else CyberBorderSubtle
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (canary.isTripped) Icons.Default.NotificationImportant else Icons.Default.Shield,
                  contentDescription = null,
                  tint = if (canary.isTripped) NeonPink else CyberEmerald,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = canary.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    color = TextPrimaryDark
                  )
                  Text(
                    text = "Type: ${canary.type} | Host: ${canary.deployedHost}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = TextSecondaryDark
                  )
                  if (canary.isTripped) {
                    Text(
                      text = "🚨 ALARM TRIPPED by ${canary.adversaryIp} (${canary.tripTimestamp})",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NeonPink)
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = if (canary.isTripped) NeonPink else CyberEmerald.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = if (canary.isTripped) "TRIPPED" else "ARMED",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp,
                      color = if (canary.isTripped) Color.White else CyberEmerald
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 5. Interactive Containment CLI Terminal
    item {
      CyberCard(
        borderColor = CyberCyan,
        backgroundColor = CyberSurface
      ) {
        Text(
          text = "INTERACTIVE CONTAINMENT TERMINAL",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = CyberCyan
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Quick action chip buttons
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            SuggestionChip(
              onClick = {
                cliInput = "isolate-host --ip 192.168.1.45"
                val res = AegoraRepository.executeContainmentCommand(cliInput)
                cliHistory = cliHistory + "> $cliInput" + res
                cliInput = ""
              },
              label = { Text("isolate-host", color = CyberCyan, fontSize = 10.sp) }
            )
          }
          item {
            SuggestionChip(
              onClick = {
                cliInput = "revoke-session --user admin"
                val res = AegoraRepository.executeContainmentCommand(cliInput)
                cliHistory = cliHistory + "> $cliInput" + res
                cliInput = ""
              },
              label = { Text("revoke-session", color = CyberAmber, fontSize = 10.sp) }
            )
          }
          item {
            SuggestionChip(
              onClick = {
                cliInput = "deploy-canary --type aws-iam"
                val res = AegoraRepository.executeContainmentCommand(cliInput)
                cliHistory = cliHistory + "> $cliInput" + res
                cliInput = ""
              },
              label = { Text("deploy-canary", color = CyberEmerald, fontSize = 10.sp) }
            )
          }
          item {
            SuggestionChip(
              onClick = {
                cliInput = "contain-full --enforce-zero-trust"
                val res = AegoraRepository.executeContainmentCommand(cliInput)
                cliHistory = cliHistory + "> $cliInput" + res
                cliInput = ""
              },
              label = { Text("contain-full", color = NeonPink, fontSize = 10.sp) }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CLI Output Window
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CodeBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp, max = 220.dp)
        ) {
          LazyColumn(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            items(cliHistory) { line ->
              Text(
                text = line,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  color = when {
                    line.startsWith(">") -> CyberCyan
                    line.startsWith("✓") -> CyberEmerald
                    line.contains("ALERT") || line.contains("TRIP") -> NeonPink
                    else -> TextSecondaryDark
                  }
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Command line input
        OutlinedTextField(
          value = cliInput,
          onValueChange = { cliInput = it },
          placeholder = { Text("Enter command (e.g. isolate-host, flush-dns)...", color = TextTertiaryDark, fontSize = 12.sp) },
          leadingIcon = { Text("aegora#", color = CyberCyan, fontFamily = FontFamily.Monospace, fontSize = 11.sp, modifier = Modifier.padding(start = 10.dp)) },
          trailingIcon = {
            IconButton(
              onClick = {
                if (cliInput.isNotBlank()) {
                  val res = AegoraRepository.executeContainmentCommand(cliInput)
                  cliHistory = cliHistory + "> $cliInput" + res
                  cliInput = ""
                }
              }
            ) {
              Icon(Icons.Default.Send, contentDescription = "Execute", tint = CyberCyan)
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(10.dp),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
          keyboardActions = KeyboardActions(onSend = {
            if (cliInput.isNotBlank()) {
              val res = AegoraRepository.executeContainmentCommand(cliInput)
              cliHistory = cliHistory + "> $cliInput" + res
              cliInput = ""
            }
          }),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = CyberBorderSubtle,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedContainerColor = CyberSurfaceElevated,
            unfocusedContainerColor = CyberSurfaceElevated
          ),
          modifier = Modifier.fillMaxWidth().testTag("shadow_range_cli_input")
        )
      }
    }
  }

  // Plant Canary Dialog
  if (showCanaryDialog) {
    AlertDialog(
      onDismissRequest = { showCanaryDialog = false },
      title = { Text("Deploy Synthetic Deception Token", color = TextPrimaryDark, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Configure a synthetic honey-token to monitor threat actor memory/credential sweeps:", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          OutlinedTextField(
            value = canaryName,
            onValueChange = { canaryName = it },
            label = { Text("Token Identifier", color = CyberCyan) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = canaryType,
            onValueChange = { canaryType = it },
            label = { Text("Decoy Type", color = CyberCyan) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = canaryHost,
            onValueChange = { canaryHost = it },
            label = { Text("Target Host / Service", color = CyberCyan) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            AegoraRepository.plantCanaryToken(canaryName, canaryType, canaryHost)
            showCanaryDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald)
        ) {
          Text("Arm Token", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showCanaryDialog = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      },
      containerColor = CyberSurface
    )
  }
}
