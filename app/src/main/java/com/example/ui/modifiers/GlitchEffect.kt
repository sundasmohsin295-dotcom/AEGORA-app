package com.example.ui.modifiers

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Modifier that applies high-fidelity chromatic aberration and horizontal jitter glitch
 * when [isGlitching] is active, simulating a severed connection or hardware compromise.
 */
fun Modifier.glitchEffect(isGlitching: Boolean): Modifier = this.then(
  if (!isGlitching) Modifier
  else Modifier
    .graphicsLayer {
      // Rapid randomized translationX jitter
      val rnd = Random(System.currentTimeMillis() / 80)
      if (rnd.nextFloat() > 0.4f) {
        translationX = (rnd.nextFloat() - 0.5f) * 16f
        translationY = (rnd.nextFloat() - 0.5f) * 4f
      } else {
        translationX = 0f
        translationY = 0f
      }
    }
    .drawWithContent {
      // Draw base content
      drawContent()

      // Chromatic Aberration Simulation: Split second Red & Cyan offset scanlines
      val rnd = Random(System.currentTimeMillis() / 120)
      if (rnd.nextFloat() > 0.35f) {
        val yOffset = rnd.nextFloat() * size.height
        val sliceHeight = 12f + rnd.nextFloat() * 24f

        // Red aberration fringe
        drawRect(
          color = Color(0x33EF4444),
          topLeft = Offset(-6f, yOffset),
          size = androidx.compose.ui.geometry.Size(size.width + 12f, sliceHeight),
          blendMode = BlendMode.Screen
        )

        // Cyan aberration fringe
        drawRect(
          color = Color(0x3322D3EE),
          topLeft = Offset(6f, yOffset + 4f),
          size = androidx.compose.ui.geometry.Size(size.width + 12f, sliceHeight),
          blendMode = BlendMode.Screen
        )
      }
    }
)

/**
 * Fullscreen Hollywood-style Offline Glitch Overlay for when Uplink is severed.
 */
@Composable
fun OfflineGlitchScreen(
  onRetryConnection: () -> Unit = {},
  onContinueOffline: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var glitchCycle by remember { mutableIntStateOf(0) }
  var jitterX by remember { mutableFloatStateOf(0f) }
  var jitterY by remember { mutableFloatStateOf(0f) }
  var glitchAlpha by remember { mutableFloatStateOf(0.95f) }

  // High-frequency jitter loop
  LaunchedEffect(Unit) {
    while (true) {
      delay(60)
      glitchCycle++
      val rnd = Random.Default
      if (rnd.nextFloat() > 0.4f) {
        jitterX = (rnd.nextFloat() - 0.5f) * 14f
        jitterY = (rnd.nextFloat() - 0.5f) * 6f
        glitchAlpha = 0.85f + rnd.nextFloat() * 0.15f
      } else {
        jitterX = 0f
        jitterY = 0f
        glitchAlpha = 0.98f
      }
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse_glitch")
  val warningPulse by infiniteTransition.animateFloat(
    initialValue = 0.6f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "warning_pulse"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF07080A))
      .testTag("offline_glitch_screen"),
    contentAlignment = Alignment.Center
  ) {
    // Scanline Matrix Background
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
      val lineSpacing = 6f
      var y = 0f
      while (y < size.height) {
        drawLine(
          color = Color(0x1A22D3EE),
          start = Offset(0f, y),
          end = Offset(size.width, y),
          strokeWidth = 1f
        )
        y += lineSpacing
      }

      // Random horizontal tear line
      val tearY = (glitchCycle * 47f) % size.height
      drawLine(
        color = Color(0x55EF4444),
        start = Offset(0f, tearY),
        end = Offset(size.width, tearY),
        strokeWidth = 3f
      )
    }

    // Chromatic Split Layers
    // 1. Red Ghost Layer (Offset Left)
    Box(
      modifier = Modifier
        .offset(x = (-4).dp + (jitterX * 0.6f).dp, y = (jitterY * 0.4f).dp)
        .graphicsLayer { alpha = 0.45f * warningPulse }
    ) {
      GlitchContent(color = Color(0xFFEF4444), showControls = false)
    }

    // 2. Cyan Ghost Layer (Offset Right)
    Box(
      modifier = Modifier
        .offset(x = 4.dp - (jitterX * 0.6f).dp, y = (-jitterY * 0.4f).dp)
        .graphicsLayer { alpha = 0.45f * warningPulse }
    ) {
      GlitchContent(color = Color(0xFF22D3EE), showControls = false)
    }

    // 3. Primary Center Layer with Jitter
    Box(
      modifier = Modifier
        .offset(x = jitterX.dp, y = jitterY.dp)
        .graphicsLayer { alpha = glitchAlpha }
    ) {
      GlitchContent(
        color = Color.White,
        showControls = true,
        onRetry = onRetryConnection,
        onContinue = onContinueOffline
      )
    }
  }
}

@Composable
private fun GlitchContent(
  color: Color,
  showControls: Boolean,
  onRetry: () -> Unit = {},
  onContinue: () -> Unit = {}
) {
  Column(
    modifier = Modifier
      .fillMaxWidth(0.9f)
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Hex Protocol Warning
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = Icons.Default.WifiOff,
        contentDescription = "Severed Uplink",
        tint = if (color == Color.White) Color(0xFFEF4444) else color,
        modifier = Modifier.size(28.dp)
      )
      Text(
        text = "ERR_SOCKET_DISCONNECT_0xFD",
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        color = if (color == Color.White) Color(0xFFEF4444) else color,
        letterSpacing = 1.sp
      )
    }

    // Distressed Hero Typography
    Text(
      text = "⚡ UPLINK SEVERED ⚡",
      fontSize = 28.sp,
      fontWeight = FontWeight.Black,
      fontFamily = FontFamily.Monospace,
      color = if (color == Color.White) Color(0xFFF87171) else color,
      textAlign = TextAlign.Center,
      letterSpacing = 2.sp
    )

    Text(
      text = "CRITICAL TELEMETRY LINK OFFLINE // SATELLITE MESH SILENT\nSWITCHING ENCLAVE TO AUTONOMOUS ZERO-TRUST AIRGAP CACHE",
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.Medium,
      color = if (color == Color.White) Color(0xFF94A3B8) else color.copy(alpha = 0.7f),
      textAlign = TextAlign.Center,
      lineHeight = 16.sp
    )

    if (showControls) {
      Spacer(modifier = Modifier.height(8.dp))

      // Terminal Warning Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0x1AEF4444), RoundedCornerShape(8.dp))
          .border(1.dp, Color(0x66EF4444), RoundedCornerShape(8.dp))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "[STATUS: HARDWARE_CACHE_ENGAGED]",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEF4444)
          )
          Text(
            text = "• AI Neural Pipeline: Offline Fallback Active\n• Cloud Telemetry Sync: Paused\n• Local Proof Ledger: Cryptographically Sealed",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Color(0xFFCBD5E1),
            lineHeight = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = onContinue,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E293B),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f).height(46.dp).testTag("btn_offline_continue")
        ) {
          Text("AIRGAP MODE", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onRetry,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEF4444),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.weight(1f).height(46.dp).testTag("btn_offline_retry")
        ) {
          Text("RECONNECT", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
