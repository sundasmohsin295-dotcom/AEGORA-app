package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.AegoraRepository
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@Composable
fun TimelineForkScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val branches by AegoraRepository.timelineBranches.collectAsState()
  val scrubPoints = AegoraRepository.timelineScrubPoints
  var scrubIndex by remember { mutableFloatStateOf(0f) }
  val activePoint = scrubPoints[scrubIndex.toInt().coerceIn(0, scrubPoints.lastIndex)]
  val selectedBranch = branches.firstOrNull { it.isSelected } ?: branches.first()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(PureBlack)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // 1. Header
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
          Icon(Icons.Default.AltRoute, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "INCIDENT TIME-MACHINE & FORKING",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = CyberCyan
          )
        }
      }
    }

    // 2. Incident Rollback Scrubber
    item {
      CyberCard(
        borderColor = CyberCyan,
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TELEMETRY ROLLBACK SCRUBBER",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = CyberCyan
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberCyan.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
          ) {
            Text(
              text = "T+${activePoint.timeLabel}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberCyan,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Slider(
          value = scrubIndex,
          onValueChange = { scrubIndex = it },
          valueRange = 0f..scrubPoints.lastIndex.toFloat(),
          steps = scrubPoints.size - 2,
          colors = SliderDefaults.colors(
            thumbColor = CyberCyan,
            activeTrackColor = CyberCyan,
            inactiveTrackColor = CyberSurfaceElevated
          ),
          modifier = Modifier.fillMaxWidth().testTag("incident_rollback_slider")
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Forensic inspection point box
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CodeBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, if (activePoint.isRootCauseTrigger) NeonPink else CyberBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (activePoint.isRootCauseTrigger) Icons.Default.Dangerous else Icons.Default.History,
                contentDescription = null,
                tint = if (activePoint.isRootCauseTrigger) NeonPink else CyberCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = activePoint.systemEvent,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = if (activePoint.isRootCauseTrigger) NeonPink else TextPrimaryDark
              )
            }
            Text(
              text = "Packet Hex: ${activePoint.packetHexSummary}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
              color = CodeGreen
            )
            Text(
              text = "Forensic Finding: ${activePoint.forensicFinding}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = TextSecondaryDark
            )
          }
        }
      }
    }

    // 3. Branching Decision Dual-Timeline Selector
    item {
      Text(
        text = "SPLIT-TIMELINE HYPOTHESIS & TRADE-OFF DIFF",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        ),
        color = CyberAmber
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        branches.forEach { branch ->
          val isSel = branch.isSelected
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSel) CyberSurfaceElevated else CyberSurface,
            border = androidx.compose.foundation.BorderStroke(
              1.5.dp,
              if (isSel) CyberCyan else CyberBorderSubtle
            ),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { AegoraRepository.selectTimelineBranch(branch.branchId) }
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                  selected = isSel,
                  onClick = { AegoraRepository.selectTimelineBranch(branch.branchId) },
                  colors = RadioButtonDefaults.colors(selectedColor = CyberCyan, unselectedColor = TextTertiaryDark),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (branch.branchId.contains("alpha")) "Alpha" else "Beta",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                  color = if (isSel) CyberCyan else TextPrimaryDark
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = branch.strategyLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                color = if (isSel) TextPrimaryDark else TextSecondaryDark,
                maxLines = 2
              )
            }
          }
        }
      }
    }

    // 4. Selected Timeline Impact Analysis & Blast Radius
    item {
      CyberCard(
        borderColor = if (selectedBranch.branchId.contains("alpha")) CyberCyan else CyberAmber,
        backgroundColor = CyberSurface
      ) {
        Text(
          text = selectedBranch.branchName,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
          color = TextPrimaryDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = selectedBranch.description,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Blast Radius Metric Grid
        Text(
          text = "BUSINESS IMPACT & RISK METRICS",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = CyberCyan
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Downtime
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${selectedBranch.metrics.operationalDowntimeHours}h",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = if (selectedBranch.metrics.operationalDowntimeHours > 2f) NeonPink else CyberEmerald
              )
              Text("Downtime", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }

          // Exfiltration
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${selectedBranch.metrics.exfiltrationBlastRadiusMb} MB",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = if (selectedBranch.metrics.exfiltrationBlastRadiusMb > 50) NeonPink else CyberEmerald
              )
              Text("Exfiltration", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }

          // Fine exposure
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = if (selectedBranch.metrics.estimatedComplianceFineUsd == 0L) "$0" else "$${selectedBranch.metrics.estimatedComplianceFineUsd / 1000}k",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = if (selectedBranch.metrics.estimatedComplianceFineUsd > 0) NeonPink else CyberEmerald
              )
              Text("Fine Risk", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "DECISION EXECUTION LOG (THIS TIMELINE)",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = CyberCyan
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          selectedBranch.actionsTaken.forEach { action ->
            Text(
              text = "• $action",
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
              color = CodeGreen
            )
          }
        }
      }
    }
  }
}
