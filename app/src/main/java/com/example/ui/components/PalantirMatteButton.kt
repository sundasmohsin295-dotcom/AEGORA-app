package com.example.ui.components

import android.view.SoundEffectConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * Palantir-grade Matte Steel Button with 1.dp hairline border,
 * 0.98f scale spring physics on press, tactile haptic feedback,
 * and guaranteed touch event handling via explicit Modifier.clickable(onClick = onClick).
 */
@Composable
fun PalantirMatteButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  containerColor: Color = SpecCardBg,
  contentColor: Color = Color.White,
  borderColor: Color = SpecBorder,
  shape: Shape = RoundedCornerShape(12.dp),
  elevation: Dp = 0.dp,
  fontSize: TextUnit = 14.sp,
  fontWeight: FontWeight = FontWeight.Bold,
  isMonospace: Boolean = false,
  testTag: String = "palantir_matte_button",
  leadingIcon: (@Composable () -> Unit)? = null,
  trailingIcon: (@Composable () -> Unit)? = null
) {
  val haptic = LocalHapticFeedback.current
  val view = LocalView.current
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  // 0.98f scale spring physics on press
  val scale by animateFloatAsState(
    targetValue = if (isPressed && enabled && !isLoading) 0.98f else 1f,
    animationSpec = spring(
      dampingRatio = 0.6f,
      stiffness = 500f
    ),
    label = "button_scale"
  )

  Surface(
    modifier = modifier
      .scale(scale)
      .testTag(testTag)
      .clip(shape)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled && !isLoading,
        role = Role.Button,
        onClick = {
          if (DebounceManager.canClick(300L)) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
          }
        }
      ),
    shape = shape,
    color = if (enabled && !isLoading) containerColor else containerColor.copy(alpha = 0.5f),
    contentColor = if (enabled && !isLoading) contentColor else contentColor.copy(alpha = 0.5f),
    border = BorderStroke(1.dp, if (enabled && !isLoading) borderColor else borderColor.copy(alpha = 0.4f)),
    shadowElevation = elevation
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isLoading) {
        ShimmerBox(
          modifier = Modifier
            .width(80.dp)
            .height(16.dp),
          shape = RoundedCornerShape(4.dp)
        )
      } else {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(8.dp))
          }
          Text(
            text = text,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.SansSerif,
            color = if (enabled && !isLoading) contentColor else contentColor.copy(alpha = 0.5f)
          )
          if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingIcon()
          }
        }
      }
    }
  }
}
