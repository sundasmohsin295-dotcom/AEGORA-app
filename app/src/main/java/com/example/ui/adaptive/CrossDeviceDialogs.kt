package com.example.ui.adaptive

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AegoraRepository
import com.example.model.NetworkSyncStatus
import com.example.model.PerformanceMode
import com.example.ui.theme.*

@Composable
fun SyncStateAndConflictDialog(
  onDismiss: () -> Unit
) {
  val syncStatus by AegoraRepository.networkSyncStatus.collectAsState()
  val sessionState by AegoraRepository.crossDeviceSession.collectAsState()

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = CyberDarkSlate,
      border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("sync_conflict_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CloudSync, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "CROSS-DEVICE SYNC & TELEMETRY",
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = NeonCyan
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Card
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = CyberBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceElevated),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Sync Status", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = when (syncStatus) {
                  NetworkSyncStatus.SYNCED -> Color(0xFF002914)
                  NetworkSyncStatus.SYNCING -> Color(0xFF002B3D)
                  NetworkSyncStatus.OFFLINE -> Color(0xFF332000)
                  NetworkSyncStatus.SYNC_ERROR -> Color(0xFF3B0014)
                }
              ) {
                Text(
                  text = syncStatus.badgeText,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  ),
                  color = when (syncStatus) {
                    NetworkSyncStatus.SYNCED -> NeonEmerald
                    NetworkSyncStatus.SYNCING -> NeonCyan
                    NetworkSyncStatus.OFFLINE -> TerminalAmber
                    NetworkSyncStatus.SYNC_ERROR -> NeonCrimson
                  },
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Last Synced Device: ${sessionState.originDeviceName}",
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )
            Text(
              text = "Active Investigation: ${sessionState.lastActivityTitle}",
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              color = NeonCyan
            )
            Text(
              text = "Timestamp: ${sessionState.lastActiveTimestamp}",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Manual Sync & Network Simulation Controls
        Text(
          text = "NETWORK SIMULATION & ACTIONS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          ),
          color = TextSecondaryDark
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              AegoraRepository.setNetworkSyncStatus(
                if (syncStatus == NetworkSyncStatus.OFFLINE) NetworkSyncStatus.SYNCED else NetworkSyncStatus.OFFLINE
              )
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text(if (syncStatus == NetworkSyncStatus.OFFLINE) "Go Online" else "Simulate Offline", fontSize = 11.sp)
          }

          Button(
            onClick = {
              AegoraRepository.triggerManualSync()
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sync Now", fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        if (sessionState.isConflictPresent) {
          Spacer(modifier = Modifier.height(14.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF2E000A),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCrimson),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("Sync Conflict Detected", style = MaterialTheme.typography.titleSmall, color = NeonCrimson, fontWeight = FontWeight.Bold)
              Text("Edits on Desktop Station conflict with uncommitted local notes on this Phone.", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
              Spacer(modifier = Modifier.height(8.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = { AegoraRepository.resolveCrossDeviceConflict(useRemote = true) },
                  colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Keep Remote", fontSize = 10.sp)
                }
                Button(
                  onClick = { AegoraRepository.resolveCrossDeviceConflict(useRemote = false) },
                  colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Color.Black),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("Merge Notes", fontSize = 10.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun PerformanceModeDialog(
  onDismiss: () -> Unit
) {
  val currentMode by AegoraRepository.performanceMode.collectAsState()

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = CyberDarkSlate,
      border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("performance_mode_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Speed, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "PERFORMANCE & DISPLAY",
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = NeonCyan
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        PerformanceMode.entries.forEach { mode ->
          val isSelected = mode == currentMode
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) NeonCyan.copy(alpha = 0.15f) else CyberBackground,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isSelected) NeonCyan else CyberSurfaceElevated
            ),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                AegoraRepository.setPerformanceMode(mode)
              }
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = isSelected,
                onClick = { AegoraRepository.setPerformanceMode(mode) },
                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = mode.label,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (isSelected) NeonCyan else TextPrimaryDark
                )
                Text(
                  text = mode.subtitle,
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("APPLY SETTING", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
      }
    }
  }
}
