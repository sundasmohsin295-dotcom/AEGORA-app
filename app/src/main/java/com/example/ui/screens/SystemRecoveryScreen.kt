package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import com.example.ui.components.DebounceManager
import com.example.ui.theme.*
import com.example.util.GlobalExceptionHandler
import com.example.util.SystemCrashReport
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enterprise System Recovery Screen.
 * Displayed when an unhandled exception occurs anywhere in the application runtime.
 * Prevents OS crash dialogues, isolates corrupt state, and gives the engineer/user
 * full diagnostic stack inspection, clipboard diagnostic export, and state reset capabilities.
 */
@Composable
fun SystemRecoveryScreen(
  crashReport: SystemCrashReport?,
  onRestartSession: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current

  val amberAlert = Color(0xFFFFB300)
  val amberBorder = Color(0xFFD97706)
  val recoveryBg = Color(0xFF090D16)
  val terminalBg = Color(0xFF05080E)

  var showFullStackTrace by remember { mutableStateOf(false) }

  // Pulsing shield animation
  val infiniteTransition = rememberInfiniteTransition(label = "recovery_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.45f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(recoveryBg)
      .testTag("screen_system_recovery"),
    containerColor = recoveryBg
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .padding(paddingValues)
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      item {
        Spacer(modifier = Modifier.height(16.dp))

        // Amber Shield Beacon
        Box(
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(amberAlert.copy(alpha = 0.15f * pulseAlpha)),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(amberAlert.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.WarningAmber,
              contentDescription = "System Intercept Active",
              tint = amberAlert,
              modifier = Modifier.size(32.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "FAULT ISOLATION // ZERO-CRASH SHIELD",
          color = amberAlert,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "SYSTEM RECOVERY ACTIVE",
          color = SpecHeadingWhite,
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "An unhandled exception was safely intercepted by AEGORA Anti-Crash Architecture. Memory state has been safeguarded to prevent corruption.",
          color = SpecSubtextSlate,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )
      }

      // Diagnostic Summary Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = SpecCardBg),
          border = BorderStroke(1.dp, amberBorder.copy(alpha = 0.6f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "INTERCEPT TELEMETRY",
                color = amberAlert,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Surface(
                color = amberAlert.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(0.8.dp, amberAlert)
              ) {
                Text(
                  text = "CONTAINED",
                  color = amberAlert,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            HorizontalDivider(color = SpecBorder.copy(alpha = 0.5f))

            TelemetryItem(
              label = "Exception Class",
              value = crashReport?.exceptionClass ?: "UnknownException"
            )
            TelemetryItem(
              label = "Thread",
              value = crashReport?.threadName ?: "main"
            )
            TelemetryItem(
              label = "Timestamp",
              value = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
                .format(Date(crashReport?.timestamp ?: System.currentTimeMillis()))
            )
            TelemetryItem(
              label = "Detail",
              value = crashReport?.message ?: "Interception completed without fault payload"
            )
          }
        }
      }

      // Actions Card
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              if (DebounceManager.canClick(300L)) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                GlobalExceptionHandler.clearCrashState()
                onRestartSession()
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SpecPrimaryBlue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("recovery_restart_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
              Text(
                text = "REBOOT SYSTEM // REINITIALIZE SUBSYSTEMS",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = Color.White
              )
            }
          }

          OutlinedButton(
            onClick = {
              if (DebounceManager.canClick(300L)) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val clip = ClipData.newPlainText(
                  "AEGORA Crash Diagnostics",
                  """
                  --- AEGORA CRASH DIAGNOSTIC LOG ---
                  Exception: ${crashReport?.exceptionClass}
                  Message: ${crashReport?.message}
                  Thread: ${crashReport?.threadName}
                  Time: ${crashReport?.timestamp}
                  StackTrace:
                  ${crashReport?.stackTrace ?: "No trace available"}
                  -----------------------------------
                  """.trimIndent()
                )
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(context, "Diagnostic stack copied to clipboard", Toast.LENGTH_SHORT).show()
              }
            },
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = SpecCardBg,
              contentColor = SpecHeadingWhite
            ),
            border = BorderStroke(1.dp, SpecBorder),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("recovery_copy_diagnostics_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = null, tint = SpecCyanHighlight, modifier = Modifier.size(16.dp))
              Text(
                text = "COPY FORENSIC STACKTRACE",
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = SpecHeadingWhite
              )
            }
          }

          TextButton(
            onClick = { showFullStackTrace = !showFullStackTrace },
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = if (showFullStackTrace) "▲ HIDE FORENSIC KERNEL DUMP" else "▼ INSPECT FORENSIC KERNEL DUMP",
              color = SpecSubtextSlate,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // Stack Trace View
      item {
        AnimatedVisibility(visible = showFullStackTrace) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = terminalBg,
            border = BorderStroke(1.dp, SpecBorder),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = crashReport?.stackTrace ?: "No raw stack available in current frame buffer.",
                color = Color(0xFFE2E8F0),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 14.sp
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
private fun TelemetryItem(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = label,
      color = SpecSubtextSlate,
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.width(110.dp)
    )
    Text(
      text = value,
      color = SpecHeadingWhite,
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.weight(1f),
      textAlign = TextAlign.End
    )
  }
}
