package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Speed
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hunter.AutonomousThreatHunterScheduler
import com.example.mesh.SentinelMeshSync
import com.example.security.KernelWatchdog
import com.example.telemetry.CircuitStatus
import com.example.telemetry.DiagnosticLogEvent
import com.example.telemetry.DiagnosticSeverity
import com.example.telemetry.DiagnosticStore
import com.example.telemetry.FactCheckerAction
import com.example.telemetry.FactCheckerEvent
import com.example.ui.components.TacticalPanel
import com.example.ui.components.TacticalStatusLed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.HighAlertCrimson
import com.example.ui.theme.HighAlertCrimsonDark
import com.example.ui.theme.ObsidianSurfaceRaised
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateBorderBright
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalAmberDark
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TacticalEmeraldDark
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTerminalGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * SreTelemetryView - Industrial Sub-View within SettingsDashboard.
 * Visualizes Circuit Breaker states, Latency P95/P99 distributions, and FactChecker audit logs.
 */
@Composable
fun SreTelemetryView(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val circuitBreakers by DiagnosticStore.circuitBreakers.collectAsState()
  val factCheckerLogs by DiagnosticStore.factCheckerAuditTrail.collectAsState()
  val logEvents by DiagnosticStore.logEvents.collectAsState()
  val meshStatus by SentinelMeshSync.meshStatus.collectAsState()
  val activePeers by SentinelMeshSync.activePeers.collectAsState()
  val watchdogHealth by KernelWatchdog.healthState.collectAsState()

  var selectedSeverity by remember { mutableStateOf<DiagnosticSeverity?>(null) }

  val filteredLogs = remember(logEvents, selectedSeverity) {
    if (selectedSeverity == null) logEvents
    else logEvents.filter { it.severity == selectedSeverity }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("sre_telemetry_view"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Diagnostic Store Real-Time Log Filter Component (Part 1 Requirement)
    TacticalPanel(
      titleTag = "[SRE-LOG-01] // DIAGNOSTIC_STORE_SEVERITY_FILTER",
      subtitle = "REAL-TIME UNIFIED EVENT STREAM",
      memoryOffset = "${logEvents.size} LOGS",
      statusLed = TacticalStatusLed.ACTIVE_CYAN,
      modifier = Modifier.testTag("diagnostic_filter_panel")
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Toggle Buttons Filter Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("severity_filter_toggle_row"),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          val infoCount = logEvents.count { it.severity == DiagnosticSeverity.INFO }
          val warnCount = logEvents.count { it.severity == DiagnosticSeverity.WARN }
          val critCount = logEvents.count { it.severity == DiagnosticSeverity.CRITICAL }

          // ALL Toggle
          SeverityToggleButton(
            label = "ALL (${logEvents.size})",
            isSelected = selectedSeverity == null,
            activeColor = ElectricCyan,
            activeBg = ElectricCyanDark,
            onClick = { selectedSeverity = null },
            modifier = Modifier
              .weight(1f)
              .testTag("filter_toggle_all")
          )

          // INFO Toggle
          SeverityToggleButton(
            label = "INFO ($infoCount)",
            isSelected = selectedSeverity == DiagnosticSeverity.INFO,
            activeColor = TacticalEmerald,
            activeBg = TacticalEmeraldDark,
            onClick = {
              selectedSeverity = if (selectedSeverity == DiagnosticSeverity.INFO) null else DiagnosticSeverity.INFO
            },
            modifier = Modifier
              .weight(1f)
              .testTag("filter_toggle_info")
          )

          // WARN Toggle
          SeverityToggleButton(
            label = "WARN ($warnCount)",
            isSelected = selectedSeverity == DiagnosticSeverity.WARN,
            activeColor = TacticalAmber,
            activeBg = TacticalAmberDark,
            onClick = {
              selectedSeverity = if (selectedSeverity == DiagnosticSeverity.WARN) null else DiagnosticSeverity.WARN
            },
            modifier = Modifier
              .weight(1f)
              .testTag("filter_toggle_warn")
          )

          // CRITICAL Toggle
          SeverityToggleButton(
            label = "CRIT ($critCount)",
            isSelected = selectedSeverity == DiagnosticSeverity.CRITICAL,
            activeColor = HighAlertCrimson,
            activeBg = HighAlertCrimsonDark,
            onClick = {
              selectedSeverity = if (selectedSeverity == DiagnosticSeverity.CRITICAL) null else DiagnosticSeverity.CRITICAL
            },
            modifier = Modifier
              .weight(1f)
              .testTag("filter_toggle_critical")
          )
        }

        // Action controls to inject simulation logs and purge
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Button(
            onClick = { DiagnosticStore.simulateLogInjection(DiagnosticSeverity.INFO) },
            modifier = Modifier
              .weight(1f)
              .height(30.dp)
              .testTag("inject_info_log_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            shape = CutCornerShape(2.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
          ) {
            Text(
              text = "[+INFO]",
              color = TacticalEmerald,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = { DiagnosticStore.simulateLogInjection(DiagnosticSeverity.WARN) },
            modifier = Modifier
              .weight(1f)
              .height(30.dp)
              .testTag("inject_warn_log_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            shape = CutCornerShape(2.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
          ) {
            Text(
              text = "[+WARN]",
              color = TacticalAmber,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = { DiagnosticStore.simulateLogInjection(DiagnosticSeverity.CRITICAL) },
            modifier = Modifier
              .weight(1f)
              .height(30.dp)
              .testTag("inject_crit_log_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            shape = CutCornerShape(2.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
          ) {
            Text(
              text = "[+CRIT]",
              color = HighAlertCrimson,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = { DiagnosticStore.clearLogs() },
            modifier = Modifier
              .weight(1f)
              .height(30.dp)
              .testTag("clear_logs_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            shape = CutCornerShape(2.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)
          ) {
            Text(
              text = "[PURGE]",
              color = TextMuted,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Stream of Filtered Logs
        if (filteredLogs.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(CutCornerShape(4.dp))
              .background(ObsidianSurfaceRaised)
              .border(1.dp, SlateBorder, CutCornerShape(4.dp))
              .padding(12.dp)
          ) {
            Text(
              text = "NO EVENTS IN CURRENT SEVERITY FILTER BUFFER",
              color = TextMuted,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            filteredLogs.take(8).forEach { event ->
              DiagnosticLogCard(event = event)
            }
          }
        }
      }
    }

    // 2. Phase 33: Sentinel Mesh & Autonomous Hunter Protocol
    TacticalPanel(
      titleTag = "[SRE-MESH-02] // SENTINEL_MESH_&_KERNEL_WATCHDOG",
      subtitle = "AUTONOMOUS THREAT HUNTING & SELF-HEALING ENCLAVE",
      memoryOffset = "SLO < 15MS",
      statusLed = TacticalStatusLed.ACTIVE_CYAN,
      modifier = Modifier.testTag("sentinel_mesh_panel")
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Mesh Status Row
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(4.dp))
            .background(ObsidianSurfaceRaised)
            .border(1.dp, SlateBorder, CutCornerShape(4.dp))
            .padding(10.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "SENTINEL MESH STATUS",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = meshStatus,
                color = ElectricCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
            Text(
              text = "PEERS IN MESH: ${activePeers.joinToString(", ") { it.operatorCallsign }}",
              color = TextMuted,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Kernel Watchdog Health Row
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(4.dp))
            .background(ObsidianSurfaceRaised)
            .border(1.dp, SlateBorder, CutCornerShape(4.dp))
            .padding(10.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "KERNEL WATCHDOG (SELF-HEAL)",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = watchdogHealth.statusText,
                color = TacticalEmerald,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
            Text(
              text = "JNI SAFE: ${watchdogHealth.isJniAllocationSafe} | MEMORY PAGE: INTACT | HEAL COUNT: ${watchdogHealth.recoveryCount}",
              color = TextMuted,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Interactive Execution Buttons for Phase 33
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Button(
            onClick = { AutonomousThreatHunterScheduler.runImmediateHunt(context) },
            modifier = Modifier
              .weight(1f)
              .height(34.dp)
              .testTag("trigger_background_hunt_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            shape = CutCornerShape(2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
          ) {
            Text(
              text = "[TRIGGER HUNT]",
              color = ElectricCyan,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = { KernelWatchdog.simulateMemoryCorruption() },
            modifier = Modifier
              .weight(1f)
              .height(34.dp)
              .testTag("simulate_corruption_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            shape = CutCornerShape(2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, HighAlertCrimson)
          ) {
            Text(
              text = "[CORRUPT PAGE]",
              color = HighAlertCrimson,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = { SentinelMeshSync.syncWithMeshRelay() },
            modifier = Modifier
              .weight(1f)
              .height(34.dp)
              .testTag("sync_mesh_relay_button"),
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            shape = CutCornerShape(2.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald)
          ) {
            Text(
              text = "[SYNC MESH]",
              color = TacticalEmerald,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 3. Circuit Breaker Status Telemetry
    TacticalPanel(
      titleTag = "[SRE-CB-03] // RESILIENCE_CIRCUIT_BREAKERS",
      subtitle = "RUNTIME PIPELINES & EDGE AI FALLBACKS",
      memoryOffset = "4 MONITORED",
      statusLed = TacticalStatusLed.ACTIVE_CYAN,
      modifier = Modifier.testTag("circuit_breaker_telemetry_panel")
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        circuitBreakers.forEach { cb ->
          val statusBadgeColor = when (cb.status) {
            CircuitStatus.CLOSED -> TacticalEmerald
            CircuitStatus.HALF_OPEN -> TacticalAmber
            CircuitStatus.OPEN -> HighAlertCrimson
          }
          val statusBgColor = when (cb.status) {
            CircuitStatus.CLOSED -> TacticalEmeraldDark
            CircuitStatus.HALF_OPEN -> TacticalAmberDark
            CircuitStatus.OPEN -> HighAlertCrimsonDark
          }
          val badgeShape = CutCornerShape(3.dp)

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(CutCornerShape(4.dp))
              .background(ObsidianSurfaceRaised)
              .border(1.dp, SlateBorder, CutCornerShape(4.dp))
              .padding(10.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = cb.serviceName,
                  color = TextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )

                Box(
                  modifier = Modifier
                    .clip(badgeShape)
                    .background(statusBgColor)
                    .border(1.dp, statusBadgeColor, badgeShape)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "[${cb.status.name}]",
                    color = statusBadgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "LATENCY: P95=${cb.latencyP95Ms}ms // P99=${cb.latencyP99Ms}ms",
                  color = ElectricCyan,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = "FAILURES: ${cb.failureCount}",
                  color = if (cb.failureCount > 0) HighAlertCrimson else TextDim,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
              }

              Text(
                text = "FALLBACK: ${cb.fallbackStrategy}",
                color = TextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }

    // 2. Latency P95 / P99 Metric Visualizer
    TacticalPanel(
      titleTag = "[SRE-LAT-02] // LATENCY_DISTRIBUTION_HISTOGRAM",
      subtitle = "NETWORK & GEMINI INFERENCE ROUND-TRIP TIME",
      memoryOffset = "P95 PEAK: 142MS",
      statusLed = TacticalStatusLed.ACTIVE_CYAN,
      modifier = Modifier.testTag("latency_metric_panel")
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        LatencyBar(label = "EDGE_TFLITE_INFERENCE", p95 = 22, p99 = 48, maxMs = 500)
        LatencyBar(label = "REVENUECAT_SYNC", p95 = 88, p99 = 164, maxMs = 500)
        LatencyBar(label = "GEMINI_2.5_FLASH_PIPELINE", p95 = 142, p99 = 285, maxMs = 500)
        LatencyBar(label = "ONESIGNAL_PUSH_RELAY", p95 = 320, p99 = 540, maxMs = 1000)

        // Monospace Legend
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "TARGET SLO: <200ms P95 // ZERO_DOWNTIME",
            color = TextTerminalGreen,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "SLO ATTAINMENT: 99.98%",
            color = ElectricCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    // 3. FactChecker Audit Trail
    TacticalPanel(
      titleTag = "[FACT-03] // DETERMINISTIC_FACTCHECKER_AUDIT",
      subtitle = "INTERCEPTED HALLUCINATIONS & MASKED PII",
      memoryOffset = "${factCheckerLogs.size} ENTRIES",
      statusLed = TacticalStatusLed.SECURE_EMERALD,
      headerTrailingContent = {
        Button(
          onClick = {
            val now = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
            DiagnosticStore.recordFactCheck(
              FactCheckerEvent(
                id = "fact-auto-${System.currentTimeMillis() % 1000}",
                timestamp = now,
                promptVector = "Dynamic Memory Containment Verification",
                flaggedReason = "Unverified CVE-2026-8812 flagged & sanitized by MITRE filter",
                action = FactCheckerAction.HALLUCINATION_BLOCKED,
                interceptedPayloadSnippet = "[AI_UNVERIFIED] Replaced hallucinated exploit vector with static kernel patch"
              )
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
          shape = CutCornerShape(3.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorderBright),
          modifier = Modifier.testTag("trigger_fact_check_event_button")
        ) {
          Text(
            text = "[+INJECT TEST EVENT]",
            color = ElectricCyan,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        }
      },
      modifier = Modifier.testTag("fact_checker_panel")
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        factCheckerLogs.take(5).forEach { fact ->
          val (badgeText, badgeColor, badgeBg) = when (fact.action) {
            FactCheckerAction.VERIFIED -> Triple("[VERIFIED]", TacticalEmerald, TacticalEmeraldDark)
            FactCheckerAction.MASKED_PII -> Triple("[MASKED_PII]", TacticalAmber, TacticalAmberDark)
            FactCheckerAction.HALLUCINATION_BLOCKED -> Triple("[AI_UNVERIFIED]", HighAlertCrimson, HighAlertCrimsonDark)
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(CutCornerShape(4.dp))
              .background(ObsidianSurfaceRaised)
              .border(1.dp, SlateBorder, CutCornerShape(4.dp))
              .padding(8.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .clip(CutCornerShape(2.dp))
                      .background(badgeBg)
                      .border(1.dp, badgeColor, CutCornerShape(2.dp))
                      .padding(horizontal = 5.dp, vertical = 1.dp)
                  ) {
                    Text(
                      text = badgeText,
                      color = badgeColor,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = fact.promptVector,
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                }

                Text(
                  text = fact.timestamp,
                  color = TextDim,
                  fontSize = 9.sp,
                  fontFamily = FontFamily.Monospace
                )
              }

              Text(
                text = fact.interceptedPayloadSnippet,
                color = ElectricCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )

              Text(
                text = "REASON: ${fact.flaggedReason}",
                color = TextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LatencyBar(
  label: String,
  p95: Long,
  p99: Long,
  maxMs: Long
) {
  val p95Fraction = (p95.toFloat() / maxMs.toFloat()).coerceIn(0f, 1f)

  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = label,
        color = TextPrimary,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = "P95: ${p95}ms | P99: ${p99}ms",
        color = if (p95 > 250) HighAlertCrimson else ElectricCyan,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { p95Fraction },
      modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .clip(CutCornerShape(2.dp)),
      color = if (p95 > 250) HighAlertCrimson else ElectricCyan,
      trackColor = SlateBorder
    )
  }
}

@Composable
private fun SeverityToggleButton(
  label: String,
  isSelected: Boolean,
  activeColor: Color,
  activeBg: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(CutCornerShape(2.dp))
      .background(if (isSelected) activeBg else ObsidianSurfaceRaised)
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) activeColor else SlateBorder,
        shape = CutCornerShape(2.dp)
      )
      .clickable(onClick = onClick)
      .padding(vertical = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = label,
      color = if (isSelected) activeColor else TextMuted,
      fontSize = 9.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
      fontFamily = FontFamily.Monospace
    )
  }
}

@Composable
private fun DiagnosticLogCard(event: DiagnosticLogEvent) {
  val (badgeText, badgeColor, badgeBg) = when (event.severity) {
    DiagnosticSeverity.INFO -> Triple("[INFO]", TacticalEmerald, TacticalEmeraldDark)
    DiagnosticSeverity.WARN -> Triple("[WARN]", TacticalAmber, TacticalAmberDark)
    DiagnosticSeverity.CRITICAL -> Triple("[CRIT]", HighAlertCrimson, HighAlertCrimsonDark)
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(CutCornerShape(3.dp))
      .background(ObsidianSurfaceRaised)
      .border(1.dp, SlateBorder, CutCornerShape(3.dp))
      .padding(8.dp)
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(CutCornerShape(2.dp))
              .background(badgeBg)
              .border(1.dp, badgeColor, CutCornerShape(2.dp))
              .padding(horizontal = 4.dp, vertical = 1.dp)
          ) {
            Text(
              text = badgeText,
              color = badgeColor,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "[${event.componentTag}]",
            color = TextPrimary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        Text(
          text = event.timestamp,
          color = TextDim,
          fontSize = 8.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      Text(
        text = event.message,
        color = if (event.severity == DiagnosticSeverity.CRITICAL) HighAlertCrimson else TextPrimary,
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace
      )

      event.metadata?.let { meta ->
        Text(
          text = "META: $meta",
          color = ElectricCyan,
          fontSize = 8.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

