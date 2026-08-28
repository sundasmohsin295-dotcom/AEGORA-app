package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.AegoraRepository
import com.example.model.SocShiftAlert
import com.example.model.SocTriageAction
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun SocShiftSimulatorScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shiftState by AegoraRepository.socShiftState.collectAsState()

  Scaffold(
    topBar = {
      Surface(
        color = CyberBackground,
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "REAL SOC SHIFT SIMULATOR 8.0",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = CyberCyan
            )
            Text(
              text = shiftState.shiftName,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black
              ),
              color = TextPrimaryDark
            )
          }
        }
      }
    },
    containerColor = CyberBackground
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .background(CyberBackground)
        .cyberGridBackground()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Shift Telemetry & Timer HUD
      item {
        CyberCard(
          borderColor = CyberCyan,
          backgroundColor = CyberSurface,
          shapeRadius = 16.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "SHIFT PROGRESS: ${shiftState.elapsedMinutes}m / ${shiftState.totalShiftDurationMinutes}m",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberCyan
              )
              Text(
                text = "Analyst: ${shiftState.analystCallsign}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (shiftState.isShiftComplete) CyberGreen.copy(alpha = 0.15f) else CyberAmber.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, if (shiftState.isShiftComplete) CyberGreen else CyberAmber)
            ) {
              Text(
                text = if (shiftState.isShiftComplete) "SHIFT COMPLETE" else "ACTIVE INGEST",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = if (shiftState.isShiftComplete) CyberGreen else CyberAmber
              )
            }
          }
        }
      }

      // 2. Active Alert Triage Queue
      items(shiftState.queue) { alert ->
        val isResolved = alert.resolvedAction != null
        val borderColor = when {
          isResolved -> CyberGreen
          alert.severity == com.example.model.SocAlertSeverity.CRITICAL -> CyberCrimson
          else -> CyberAmber
        }

        CyberCard(
          borderColor = borderColor,
          backgroundColor = CyberSurfaceElevated,
          shapeRadius = 14.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(alert.severity.badgeColor).copy(alpha = 0.2f),
              border = BorderStroke(1.dp, Color(alert.severity.badgeColor))
            ) {
              Text(
                text = alert.severity.label,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = Color(alert.severity.badgeColor)
              )
            }
            Text(
              text = alert.timestamp,
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = TextSecondaryDark
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = alert.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Target: ${alert.destinationHost} • Source: ${alert.sourceIp} • User: ${alert.userAccount}",
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            color = CyberCyan
          )

          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberBackground,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = alert.rawLogSnippet,
              modifier = Modifier.padding(10.dp),
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
              ),
              color = TextSecondaryDark
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Context / Conflicting Hint: ${alert.conflictingEvidenceHint}",
            style = MaterialTheme.typography.labelSmall,
            color = CyberAmber
          )

          Spacer(modifier = Modifier.height(12.dp))

          if (!isResolved) {
            Text(
              text = "SELECT TRIAGE ACTION:",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  AegoraRepository.triageSocAlert(alert.id, SocTriageAction.CONTAIN)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCrimson),
                modifier = Modifier.weight(1f)
              ) {
                Text("Contain Host", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = {
                  AegoraRepository.triageSocAlert(alert.id, SocTriageAction.CLOSE_FALSE_POSITIVE)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                modifier = Modifier.weight(1f)
              ) {
                Text("Close Benign", style = MaterialTheme.typography.labelSmall)
              }
            }
          } else {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberGreen.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberGreen),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "RESOLVED: ${alert.resolvedAction?.label} (Score: ${alert.triageScoreAwarded}/100)",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberGreen
              )
            }
          }
        }
      }

      // 3. Shift Debrief
      if (shiftState.isShiftComplete) {
        item {
          CyberCard(
            borderColor = CyberGold,
            backgroundColor = CyberSurface,
            shapeRadius = 16.dp
          ) {
            Text(
              text = "SOC SHIFT FORENSIC DEBRIEF",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = CyberGold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = shiftState.shiftDebriefNotes,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark
            )
          }
        }
      }
    }
  }
}
