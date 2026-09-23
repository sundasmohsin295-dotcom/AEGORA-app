package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.result.AegoraResult
import com.example.ui.theme.HighAlertCrimson
import com.example.ui.theme.HighAlertCrimsonDark
import com.example.ui.theme.ObsidianSurfaceRaised
import com.example.ui.theme.TacticalAmber

/**
 * Tactical Forensic Error Component (Industrial Specification).
 * Monospace fault status, zero emojis, cut-corner industrial frame, and non-blocking circuit recovery.
 */
@Composable
fun HackerErrorStateComponent(
  failure: AegoraResult.Failure,
  onRetry: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "glitch_transition")
  val glitchAlpha by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glitch_alpha"
  )

  val frameShape = CutCornerShape(topStart = 8.dp, bottomEnd = 6.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .alpha(glitchAlpha)
      .clip(frameShape)
      .background(ObsidianSurfaceRaised)
      .border(1.dp, HighAlertCrimson, frameShape)
      .padding(14.dp)
      .testTag("hacker_error_component")
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Dangerous,
            contentDescription = "Fault Condition",
            tint = HighAlertCrimson,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "[FAULT: ${failure.code}]",
            color = HighAlertCrimson,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }

        Text(
          text = if (failure.isRecoverable) "[RECOVERABLE]" else "[TERMINAL]",
          color = if (failure.isRecoverable) TacticalAmber else HighAlertCrimson,
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        )
      }

      Text(
        text = failure.message,
        color = Color(0xFFFCA5A5),
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 15.sp
      )

      if (onRetry != null && failure.isRecoverable) {
        Spacer(modifier = Modifier.height(4.dp))
        val retryBtnShape = CutCornerShape(4.dp)
        Button(
          onClick = onRetry,
          colors = ButtonDefaults.buttonColors(containerColor = HighAlertCrimsonDark),
          shape = retryBtnShape,
          border = androidx.compose.foundation.BorderStroke(1.dp, HighAlertCrimson),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("retry_fault_button")
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = Color(0xFFFCA5A5),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "[ENGAGE CIRCUIT RECOVERY]",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}
