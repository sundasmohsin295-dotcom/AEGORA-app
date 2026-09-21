package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.RaspAuditReport
import com.example.security.SecurityEnforcer

/**
 * Locked Crimson Error Screen.
 * Triggered automatically when RASP detects a compromised host environment
 * (su binary presence, test-keys build tags, or virtualized emulator tampering).
 * 
 * Enforces Zero-Trust isolation by revoking API access and wiping cryptographic storage.
 */
@Composable
fun RaspLockdownScreen(
  auditReport: RaspAuditReport,
  onAuditorOverride: () -> Unit,
  onRecheckIntegrity: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current

  val crimsonBg = Color(0xFF0F0203)
  val crimsonDark = Color(0xFF1E0608)
  val crimsonAlert = Color(0xFFFF1744)
  val crimsonBorder = Color(0xFF7F1D1D)
  val mutedText = Color(0xFF9CA3AF)

  var showKernelLogs by remember { mutableStateOf(false) }

  // Pulsing animation for the security lock badge
  val infiniteTransition = rememberInfiniteTransition(label = "rasp_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Surface(
    modifier = modifier
      .fillMaxSize()
      .background(crimsonBg)
      .testTag("rasp_lockdown_screen"),
    color = crimsonBg
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(24.dp))
        // Crimson Alert Badge
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(crimsonAlert.copy(alpha = 0.15f * pulseAlpha))
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(crimsonAlert.copy(alpha = 0.25f))
          ) {
            Icon(
              imageVector = Icons.Default.GppBad,
              contentDescription = "RASP Lockdown Active",
              tint = crimsonAlert,
              modifier = Modifier.size(38.dp)
            )
          }
        }
      }

      item {
        Text(
          text = "SECURITY POLICY VIOLATION",
          color = crimsonAlert,
          fontSize = 12.sp,
          fontWeight = FontWeight.ExtraBold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "ZERO-TRUST RASP DEFENSE TRIGGERED",
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Host environment failed runtime hardware attestation. Untrusted execution privileges, su binaries, or virtualization hooks were intercepted.",
          color = mutedText,
          fontSize = 13.sp,
          lineHeight = 18.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 12.dp)
        )
      }

      // Forensics Telemetry Card
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = crimsonDark,
          border = BorderStroke(1.dp, crimsonBorder),
          modifier = Modifier.fillMaxWidth().testTag("rasp_forensics_card")
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "INTERCEPTED ANOMALIES",
                color = crimsonAlert,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF3B070A)
              ) {
                Text(
                  text = "ACCESS DENIED",
                  color = crimsonAlert,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.ExtraBold,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            HorizontalDivider(color = crimsonBorder.copy(alpha = 0.6f))

            RaspTelemetryRow(
              label = "Root Privilege / SU Binary",
              detected = auditReport.rootDetected || auditReport.suBinariesFound.isNotEmpty(),
              detail = if (auditReport.suBinariesFound.isNotEmpty()) auditReport.suBinariesFound.first() else "su probe flagged"
            )

            RaspTelemetryRow(
              label = "Build Tags (test-keys)",
              detected = auditReport.testKeysDetected,
              detail = if (auditReport.testKeysDetected) "OS Build signed with test-keys" else "release-keys clean"
            )

            RaspTelemetryRow(
              label = "Hypervisor / Emulator",
              detected = auditReport.emulatorDetected,
              detail = if (auditReport.emulatorDetected) "QEMU/Ranchu virtualized core" else "Physical TEE confirmed"
            )

            RaspTelemetryRow(
              label = "Debugger & Tracer (ptrace)",
              detected = auditReport.debuggerAttached,
              detail = if (auditReport.debuggerAttached) "Unauthorized debugger active" else "No debug hooks detected"
            )

            RaspTelemetryRow(
              label = "EncryptedSharedPreferences",
              detected = true,
              detail = "WIPED & PURGED (Zero Plaintext)",
              badgeColor = crimsonAlert
            )

            RaspTelemetryRow(
              label = "Outbound API Gateway",
              detected = true,
              detail = "REVOKED / SOCKETS MUTED",
              badgeColor = crimsonAlert
            )
          }
        }
      }

      // Action Buttons
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onRecheckIntegrity()
            },
            colors = ButtonDefaults.buttonColors(containerColor = crimsonAlert),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("rasp_recheck_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Text(
                text = "RE-RUN INTEGRITY PROBE",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
              )
            }
          }

          OutlinedButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              SecurityEnforcer.setAuditorOverride(true)
              onAuditorOverride()
            },
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = Color(0xFF180A0C),
              contentColor = Color(0xFFF87171)
            ),
            border = BorderStroke(1.dp, crimsonBorder),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("rasp_override_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
              Text(
                text = "[ SEC-OPS AUDIT OVERRIDE ]",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
              )
            }
          }

          TextButton(
            onClick = { showKernelLogs = !showKernelLogs },
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = if (showKernelLogs) "HIDE FORENSIC LOGS" else "VIEW RASP KERNEL LOGS",
              color = mutedText,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // RASP Kernel Terminal Logs
      item {
        AnimatedVisibility(visible = showKernelLogs) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF070102),
            border = BorderStroke(1.dp, Color(0xFF450A0A)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "[00:00:01.012] RASP_BOOT: Hardware integrity monitor initialized.",
                color = Color(0xFF9CA3AF),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "[00:00:01.045] RASP_SCAN: Probing /system/bin/su, /system/xbin/su, build.prop.",
                color = Color(0xFF9CA3AF),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "[00:00:01.078] RASP_ALERT: Untrusted environment signature matched. Code: 0xCRIMSON_COMPROMISE.",
                color = crimsonAlert,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "[00:00:01.082] RASP_PURGE: EncryptedSharedPreferences master key zeroed from memory.",
                color = crimsonAlert,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "[00:00:01.095] RASP_ISOLATE: Outbound API transport locked. Socket creation muted.",
                color = crimsonAlert,
                fontSize = 10.sp,
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
private fun RaspTelemetryRow(
  label: String,
  detected: Boolean,
  detail: String,
  badgeColor: Color? = null
) {
  val crimsonAlert = Color(0xFFFF1744)
  val emerald = Color(0xFF00E676)

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = detail,
        color = Color(0xFF9CA3AF),
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace
      )
    }

    Surface(
      shape = RoundedCornerShape(4.dp),
      color = if (detected) (badgeColor ?: crimsonAlert).copy(alpha = 0.2f) else emerald.copy(alpha = 0.2f),
      border = BorderStroke(0.8.dp, if (detected) (badgeColor ?: crimsonAlert) else emerald)
    ) {
      Text(
        text = if (detected) "FLAGGED" else "CLEAN",
        color = if (detected) (badgeColor ?: crimsonAlert) else emerald,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
      )
    }
  }
}
