package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.theme.*

data class TimelineStep(
  val time: String,
  val title: String,
  val detail: String,
  val isCompleted: Boolean = true
)

@Composable
fun IncidentInvestigationScreen(
  incidentId: String = "INC-20481",
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf("Overview") }
  var isHostIsolated by remember { mutableStateOf(false) }
  var isIpBlocked by remember { mutableStateOf(false) }
  var showPlaybookDialog by remember { mutableStateOf(false) }

  val timelineSteps = remember {
    listOf(
      TimelineStep("09:41:02", "Initial Access", "Phishing email link click on perimeter workstation."),
      TimelineStep("09:42:17", "PowerShell Spawned", "Suspicious child process spawned from Office binary."),
      TimelineStep("09:43:09", "Credential Access", "Attempted memory scrape on LSASS process detected."),
      TimelineStep("09:44:21", "Lateral Movement", "SMB connection initiated to host WIN-SRV-03."),
      TimelineStep("09:46:02", "Host Isolated", "Automated EDR containment executed on WIN-SRV-03.")
    )
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
  ) {
    val isWideScreen = maxWidth >= 840.dp

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .testTag("incident_investigation_screen")
    ) {
      // 1. Top Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(ObsidianSurface)
              .border(1.dp, SlateBorder, CircleShape)
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricCyan, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(incidentId, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp, fontFamily = FontFamily.Monospace)
              Box(
                modifier = Modifier
                  .background(HighAlertCrimsonDark, RoundedCornerShape(3.dp))
                  .border(1.dp, HighAlertCrimson, RoundedCornerShape(3.dp))
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text("CRITICAL", color = HighAlertCrimson, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
              }
            }
            Text("Suspicious PowerShell Execution", color = ElectricCyan, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Apr 26, 2025 09:41 AM", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Share, contentDescription = "Share", tint = TextMuted, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Navigation Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(ObsidianSurface, RoundedCornerShape(8.dp))
          .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        listOf("Overview", "Timeline", "Evidence", "MITRE", "Playbook").forEach { tab ->
          val isSelected = selectedTab == tab
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) ElectricCyanMuted else Color.Transparent)
              .clickable { selectedTab = tab }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = tab,
              color = if (isSelected) Color.White else TextMuted,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Main Split Content (Timeline & Key Details)
      if (isWideScreen) {
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          IncidentTimelineSection(timelineSteps, modifier = Modifier.weight(1.2f).fillMaxHeight())
          IncidentDetailsAndActionsSection(
            isHostIsolated = isHostIsolated,
            isIpBlocked = isIpBlocked,
            onToggleHostIsolated = { isHostIsolated = !isHostIsolated },
            onToggleIpBlocked = { isIpBlocked = !isIpBlocked },
            onOpenPlaybook = { showPlaybookDialog = true },
            modifier = Modifier.weight(1f).fillMaxHeight()
          )
        }
      } else {
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          IncidentTimelineSection(timelineSteps, modifier = Modifier.fillMaxWidth())
          IncidentDetailsAndActionsSection(
            isHostIsolated = isHostIsolated,
            isIpBlocked = isIpBlocked,
            onToggleHostIsolated = { isHostIsolated = !isHostIsolated },
            onToggleIpBlocked = { isIpBlocked = !isIpBlocked },
            onOpenPlaybook = { showPlaybookDialog = true },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }

  if (showPlaybookDialog) {
    AlertDialog(
      onDismissRequest = { showPlaybookDialog = false },
      title = {
        Text("SOAR Remediation Playbook #T1059", color = ElectricCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("1. Sever network socket on endpoint WIN-SRV-03 (Completed)", color = TacticalEmerald, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          Text("2. Dump process memory buffer for volatile payload carving.", color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          Text("3. Invalidate Kerberos TGT & rotate privileged domain credentials.", color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          Text("4. Broadcast zero-trust indicator across Sentinel Mesh relay.", color = TacticalEmerald, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
      },
      confirmButton = {
        Button(
          onClick = { showPlaybookDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
        ) {
          Text("Execute All Steps", color = ObsidianBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
      },
      containerColor = ObsidianSurface,
      shape = RoundedCornerShape(10.dp)
    )
  }
}

@Composable
private fun IncidentTimelineSection(steps: List<TimelineStep>, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Incident Timeline", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text("5 Events Recorded", color = TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
      }

      Spacer(modifier = Modifier.height(14.dp))

      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        steps.forEachIndexed { idx, step ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(if (idx == steps.lastIndex) TacticalEmerald else HighAlertCrimson)
              )
              if (idx < steps.size - 1) {
                Box(
                  modifier = Modifier
                    .width(1.5.dp)
                    .height(38.dp)
                    .background(SlateBorder)
                )
              }
            }

            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(step.time, color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(step.title, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(step.detail, color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace, lineHeight = 14.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun IncidentDetailsAndActionsSection(
  isHostIsolated: Boolean,
  isIpBlocked: Boolean,
  onToggleHostIsolated: () -> Unit,
  onToggleIpBlocked: () -> Unit,
  onOpenPlaybook: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Key Details Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Key Details", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(2.dp))

        DetailRow("Source IP", "45.32.11.76", HighAlertCrimson)
        DetailRow("Destination IP", "10.10.5.23", ElectricCyan)
        DetailRow("Host", "WIN-SRV-03", TextPrimary)
        DetailRow("User", "jdoe (Domain Admin)", TacticalAmber)
        DetailRow("Process", "powershell.exe", TextPrimary)
        DetailRow("Hash", "a3f5e7...82c1", TextDim)
        DetailRow("MITRE", "T1059, T1003, T1021", Color(0xFF38BDF8))
      }
    }

    // Action Remediation Controls
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Autonomous Actions", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

        Button(
          onClick = onToggleHostIsolated,
          modifier = Modifier.fillMaxWidth().height(42.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isHostIsolated) TacticalEmerald else HighAlertCrimson
          ),
          shape = RoundedCornerShape(6.dp)
        ) {
          Icon(if (isHostIsolated) Icons.Default.Check else Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            if (isHostIsolated) "HOST ISOLATED (REVERSE)" else "ISOLATE HOST",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        Button(
          onClick = onToggleIpBlocked,
          modifier = Modifier.fillMaxWidth().height(42.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isIpBlocked) TacticalEmerald else Color(0xFF2563EB)
          ),
          shape = RoundedCornerShape(6.dp)
        ) {
          Icon(if (isIpBlocked) Icons.Default.Check else Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            if (isIpBlocked) "IP BLOCKED IN FIREWALL" else "BLOCK IP (45.32.11.76)",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        OutlinedButton(
          onClick = onOpenPlaybook,
          modifier = Modifier.fillMaxWidth().height(42.dp),
          border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text("OPEN PLAYBOOK", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
      }
    }
  }
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, color = TextDim, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
    Text(value, color = valueColor, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
  }
}
