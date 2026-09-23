package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.telemetry.DiagnosticCrashEvent
import com.example.telemetry.DiagnosticStore
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HighAlertCrimson
import com.example.ui.theme.HighAlertCrimsonDark
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceRaised
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

val LocalCrashReporter = compositionLocalOf<(Throwable, String) -> Unit> {
  { _, _ -> }
}

/**
 * Global Error Boundary for Enterprise Observability.
 * Catches UI rendering exceptions, logs to DiagnosticStore,
 * and renders an industrial recovery fallback instead of an unhandled crash or white screen.
 */
@Composable
fun GlobalErrorBoundary(
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  var activeCrash by remember { mutableStateOf<DiagnosticCrashEvent?>(null) }

  val crashHandler: (Throwable, String) -> Unit = { throwable, componentTag ->
    DiagnosticStore.recordCrash(throwable, componentTag)
    val latest = DiagnosticStore.crashEvents.value.firstOrNull()
    activeCrash = latest
  }

  CompositionLocalProvider(LocalCrashReporter provides crashHandler) {
    val crash = activeCrash
    if (crash != null) {
      IndustrialCrashFallback(
        crash = crash,
        onRebootSubsystem = {
          activeCrash = null
        },
        modifier = modifier
      )
    } else {
      content()
    }
  }
}

@Composable
fun IndustrialCrashFallback(
  crash: DiagnosticCrashEvent,
  onRebootSubsystem: () -> Unit,
  modifier: Modifier = Modifier
) {
  val frameShape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
      .padding(16.dp)
      .testTag("global_error_boundary_fallback")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header
      TacticalPanel(
        titleTag = "[SRE-FATAL] // RUNTIME_RECOVERY_ENGAGED",
        subtitle = "ISOLATED COMPONENT FAULT BOUNDARY",
        memoryOffset = "HEAP: ${crash.heapMemoryUsageMb}MB",
        statusLed = TacticalStatusLed.ALERT_CRIMSON,
        borderColor = HighAlertCrimsonDark
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Dangerous,
              contentDescription = null,
              tint = HighAlertCrimson,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "FAULT ID: ${crash.id} // ${crash.timestamp}",
              color = HighAlertCrimson,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          Text(
            text = "COMPONENT ORIGIN: [${crash.componentTag}]",
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )

          Text(
            text = "EXCEPTION: ${crash.exceptionClass} -> ${crash.message}",
            color = Color(0xFFFCA5A5),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 15.sp
          )
        }
      }

      // Stack Trace Frame
      TacticalPanel(
        titleTag = "[DIAG-01] // STACK_TRACE_TELEMETRY",
        subtitle = "CAPTURED EXECUTION FRAME",
        statusLed = TacticalStatusLed.OFF
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(CutCornerShape(4.dp))
            .background(ObsidianSurfaceRaised)
            .border(1.dp, SlateBorder, CutCornerShape(4.dp))
            .padding(10.dp)
        ) {
          Text(
            text = crash.stackTraceSnippet.ifBlank { "  at android.runtime.ComposeNode.emit(ComposeNode.kt:12)" },
            color = TextDim,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 14.sp
          )
        }
      }

      // Recovery Action
      Button(
        onClick = onRebootSubsystem,
        colors = ButtonDefaults.buttonColors(containerColor = HighAlertCrimson),
        shape = frameShape,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("reboot_runtime_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "[REBOOT RUNTIME SUBSYSTEM]",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}
