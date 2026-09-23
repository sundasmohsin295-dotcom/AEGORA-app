package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.HighAlertCrimson
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateBorderBright
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextPrimary

enum class TacticalStatusLed {
  ACTIVE_CYAN,
  ALERT_CRIMSON,
  STANDBY_AMBER,
  SECURE_EMERALD,
  OFF
}

/**
 * Tactical Telemetry Panel (Palantir / Bloomberg Defense Specification).
 * Replaces generic bento boxes with high-density asymmetrical industrial frames,
 * 1dp sharp slate borders, corner chamfers, and active status LED dots.
 */
@Composable
fun TacticalPanel(
  titleTag: String,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  memoryOffset: String? = null,
  statusLed: TacticalStatusLed = TacticalStatusLed.ACTIVE_CYAN,
  isProcessing: Boolean = false,
  borderColor: Color = SlateBorder,
  headerTrailingContent: (@Composable () -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "tactical_led_pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = if (isProcessing) 0.35f else 0.75f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isProcessing) 450 else 900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "led_alpha"
  )

  val ledColor = when (statusLed) {
    TacticalStatusLed.ACTIVE_CYAN -> ElectricCyan
    TacticalStatusLed.ALERT_CRIMSON -> HighAlertCrimson
    TacticalStatusLed.STANDBY_AMBER -> TacticalAmber
    TacticalStatusLed.SECURE_EMERALD -> TacticalEmerald
    TacticalStatusLed.OFF -> TextDim
  }

  // Industrial cut-corner shape (asymmetrical chamfer)
  val industrialShape = CutCornerShape(topStart = 8.dp, bottomEnd = 6.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(industrialShape)
      .background(ObsidianSurface)
      .border(1.dp, borderColor, industrialShape)
      .testTag("tactical_panel_${titleTag.replace(Regex("[^A-Za-z0-9_]"), "_")}")
  ) {
    // Technical Corner Reticle Brackets (Canvas Overlay)
    Canvas(modifier = Modifier.matchParentSize()) {
      val bracketLen = 14.dp.toPx()
      val strokeW = 1.5.dp.toPx()

      // Top-right technical crosshair
      drawLine(
        color = SlateBorderBright,
        start = Offset(size.width - bracketLen, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = strokeW
      )
      drawLine(
        color = SlateBorderBright,
        start = Offset(size.width, 0f),
        end = Offset(size.width, bracketLen),
        strokeWidth = strokeW
      )

      // Bottom-left technical crosshair
      drawLine(
        color = SlateBorderBright,
        start = Offset(0f, size.height - bracketLen),
        end = Offset(0f, size.height),
        strokeWidth = strokeW
      )
      drawLine(
        color = SlateBorderBright,
        start = Offset(0f, size.height),
        end = Offset(bracketLen, size.height),
        strokeWidth = strokeW
      )
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // Tactical Header Strip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Status LED Dot
          if (statusLed != TacticalStatusLed.OFF) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CutCornerShape(2.dp))
                .background(ledColor.copy(alpha = pulseAlpha))
            )
            Spacer(modifier = Modifier.width(8.dp))
          }

          Column {
            Text(
              text = titleTag,
              color = if (statusLed == TacticalStatusLed.ALERT_CRIMSON) HighAlertCrimson else ElectricCyan,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 0.8.sp
            )
            if (subtitle != null) {
              Text(
                text = subtitle,
                color = TextDim,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (memoryOffset != null) {
            Text(
              text = memoryOffset,
              color = TextDim,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(8.dp))
          }

          headerTrailingContent?.invoke()
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Panel Body
      content()
    }
  }
}
