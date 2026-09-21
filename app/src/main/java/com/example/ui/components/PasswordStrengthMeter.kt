package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.PasswordStrengthLevel
import com.example.auth.PasswordValidationResult

/**
 * Visual Segmented & Linear Password Strength Meter
 * Colors:
 * - Red (Weak) -> Yellow (Fair) -> Green (Good) -> Glowing Cyan (Unbreakable)
 * Monospace text label confirming exact missing requirements.
 */
@Composable
fun PasswordStrengthMeter(
  validation: PasswordValidationResult,
  modifier: Modifier = Modifier
) {
  val targetColor = when (validation.strengthLevel) {
    PasswordStrengthLevel.EMPTY -> Color(0xFF334155)
    PasswordStrengthLevel.WEAK -> Color(0xFFEF4444) // Red
    PasswordStrengthLevel.FAIR -> Color(0xFFF59E0B) // Yellow
    PasswordStrengthLevel.GOOD -> Color(0xFF10B981) // Green
    PasswordStrengthLevel.UNBREAKABLE -> Color(0xFF22D3EE) // Glowing Cyan
  }

  val animatedColor by animateColorAsState(
    targetValue = targetColor,
    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
    label = "strength_color"
  )

  val animatedScore by animateFloatAsState(
    targetValue = validation.strengthLevel.score,
    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
    label = "strength_score"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .testTag("password_strength_meter"),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // Top Row: Label and Strength Level
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "CIPHER STRENGTH",
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 0.8.sp
      )

      Text(
        text = validation.strengthLevel.label,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        color = animatedColor,
        letterSpacing = 0.5.sp
      )
    }

    // Segmented Bars (4 segments)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      val activeSegments = when (validation.strengthLevel) {
        PasswordStrengthLevel.EMPTY -> 0
        PasswordStrengthLevel.WEAK -> 1
        PasswordStrengthLevel.FAIR -> 2
        PasswordStrengthLevel.GOOD -> 3
        PasswordStrengthLevel.UNBREAKABLE -> 4
      }

      for (i in 1..4) {
        val isSegmentActive = i <= activeSegments
        Box(
          modifier = Modifier
            .weight(1f)
            .height(5.dp)
            .clip(RoundedCornerShape(2.5.dp))
            .background(if (isSegmentActive) animatedColor else Color(0xFF1E293B))
        )
      }
    }

    // Missing Requirement Label
    AnimatedVisibility(
      visible = validation.missingRequirements.isNotEmpty(),
      enter = fadeIn(tween(180)),
      exit = fadeOut(tween(180))
    ) {
      Text(
        text = validation.primaryMissingRequirementLabel ?: "",
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = if (validation.strengthLevel == PasswordStrengthLevel.EMPTY) Color(0xFF64748B) else Color(0xFFF87171),
        letterSpacing = 0.4.sp,
        modifier = Modifier.testTag("password_missing_requirement_label")
      )
    }
  }
}
