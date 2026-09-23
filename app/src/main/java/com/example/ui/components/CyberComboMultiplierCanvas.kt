package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.ObsidianSurfaceRaised
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import kotlin.math.cos
import kotlin.math.sin

/**
 * Tactical Combo Multiplier Telemetry Canvas (Industrial Specification).
 * Monospace security tagging, zero emojis, sharp 1dp borders, and dynamic laser-pulse arcs.
 */
@Composable
fun CyberComboMultiplierCanvas(
  comboStreak: Int,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "combo_particles")

  val pulseProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1100, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_progress"
  )

  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.5f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  val industrialShape = CutCornerShape(topStart = 8.dp, bottomEnd = 8.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(64.dp)
      .clip(industrialShape)
      .background(ObsidianSurfaceRaised)
      .border(1.dp, ElectricCyan.copy(alpha = glowAlpha), industrialShape)
      .testTag("cyber_combo_canvas_container")
  ) {
    // Dynamic Particle & Arc Canvas
    Canvas(modifier = Modifier.matchParentSize()) {
      val center = Offset(size.width * 0.12f, size.height / 2f)
      val maxRadius = size.height * 0.65f

      // Radiating energy pulse rings
      val currentRadius = maxRadius * pulseProgress
      drawCircle(
        color = ElectricCyan.copy(alpha = (1f - pulseProgress) * 0.75f),
        radius = currentRadius,
        center = center,
        style = Stroke(width = 2.5f)
      )

      // Tactical cyber sparks
      for (i in 0 until 8) {
        val angle = (i * (360f / 8) + (pulseProgress * 360f)) * (Math.PI / 180.0)
        val sparkDist = (maxRadius * 0.75f) * pulseProgress
        val sparkX = (center.x + cos(angle) * sparkDist).toFloat()
        val sparkY = (center.y + sin(angle) * sparkDist).toFloat()

        drawCircle(
          color = ElectricCyan,
          radius = 3f * (1f - pulseProgress * 0.5f),
          center = Offset(sparkX, sparkY)
        )
      }

      // Trailing technical baseline
      drawLine(
        brush = Brush.horizontalGradient(
          colors = listOf(ElectricCyan.copy(alpha = 0.85f), ElectricCyanDark, Color.Transparent),
          startX = size.width * 0.12f,
          endX = size.width * 0.95f
        ),
        start = Offset(size.width * 0.12f, size.height / 2f),
        end = Offset(size.width * 0.95f, size.height / 2f),
        strokeWidth = 1.5f,
        cap = StrokeCap.Square
      )
    }

    // Overlay Monospace Telemetry Strip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.ElectricBolt,
          contentDescription = "Threat Chain",
          tint = ElectricCyan,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "[CHAIN_ACTIVE] // ${comboStreak}x MULTIPLIER",
            color = ElectricCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
          Text(
            text = "YIELD: +${comboStreak * 50} PTS // MITRE_ATT&CK_STREAK",
            color = TextMuted,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(CutCornerShape(3.dp))
          .background(ElectricCyan.copy(alpha = 0.15f))
          .border(1.dp, ElectricCyan.copy(alpha = 0.6f), CutCornerShape(3.dp))
          .padding(horizontal = 6.dp, vertical = 3.dp)
      ) {
        Text(
          text = "[SEC_BURST_${comboStreak}]",
          color = ElectricCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}
