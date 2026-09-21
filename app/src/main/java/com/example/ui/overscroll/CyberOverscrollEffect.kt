package com.example.ui.overscroll

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Cyberpunk / Matrix Aesthetic Cyan Boundary Glow
private val CyberCyanGlow = Color(0xFF00E5FF)

/**
 * Custom Cyber Overscroll Effect replacing standard Android stretch/glow with
 * a responsive high-tech Cyan boundary emission when reaching scroll edges.
 */
@OptIn(ExperimentalFoundationApi::class)
class CyberOverscrollEffect(
  private val scope: CoroutineScope,
  private val glowColor: Color = CyberCyanGlow
) : OverscrollEffect {

  private val topGlowAlpha = Animatable(0f)
  private val bottomGlowAlpha = Animatable(0f)

  override val isInProgress: Boolean
    get() = topGlowAlpha.value > 0f || bottomGlowAlpha.value > 0f

  override val effectModifier: Modifier = Modifier.drawWithContent {
    drawContent()
    drawGlow(this)
  }

  override fun applyToScroll(
    delta: Offset,
    source: NestedScrollSource,
    performScroll: (Offset) -> Offset
  ): Offset {
    val consumed = performScroll(delta)
    val unconsumed = delta - consumed

    if (unconsumed.y > 0f) {
      // Pulling down at top edge
      scope.launch {
        val target = (topGlowAlpha.value + (unconsumed.y / 250f)).coerceIn(0f, 0.85f)
        topGlowAlpha.snapTo(target)
        topGlowAlpha.animateTo(0f, animationSpec = tween(durationMillis = 400))
      }
    } else if (unconsumed.y < 0f) {
      // Pulling up at bottom edge
      scope.launch {
        val target = (bottomGlowAlpha.value + (-unconsumed.y / 250f)).coerceIn(0f, 0.85f)
        bottomGlowAlpha.snapTo(target)
        bottomGlowAlpha.animateTo(0f, animationSpec = tween(durationMillis = 400))
      }
    }

    return consumed
  }

  override suspend fun applyToFling(
    velocity: Velocity,
    performFling: suspend (Velocity) -> Velocity
  ) {
    val remaining = performFling(velocity)
    if (remaining.y > 0f) {
      scope.launch {
        topGlowAlpha.snapTo(0.75f)
        topGlowAlpha.animateTo(0f, animationSpec = tween(durationMillis = 500))
      }
    } else if (remaining.y < 0f) {
      scope.launch {
        bottomGlowAlpha.snapTo(0.75f)
        bottomGlowAlpha.animateTo(0f, animationSpec = tween(durationMillis = 500))
      }
    }
  }

  fun drawGlow(drawScope: DrawScope) {
    with(drawScope) {
      // Draw top cyber boundary glow
      if (topGlowAlpha.value > 0.01f) {
        val glowHeight = 36.dp.toPx()
        drawRect(
          brush = Brush.verticalGradient(
            colors = listOf(
              glowColor.copy(alpha = topGlowAlpha.value * 0.9f),
              glowColor.copy(alpha = topGlowAlpha.value * 0.25f),
              Color.Transparent
            ),
            startY = 0f,
            endY = glowHeight
          ),
          topLeft = Offset.Zero,
          size = Size(size.width, glowHeight)
        )
      }

      // Draw bottom cyber boundary glow
      if (bottomGlowAlpha.value > 0.01f) {
        val glowHeight = 36.dp.toPx()
        drawRect(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              glowColor.copy(alpha = bottomGlowAlpha.value * 0.25f),
              glowColor.copy(alpha = bottomGlowAlpha.value * 0.9f)
            ),
            startY = size.height - glowHeight,
            endY = size.height
          ),
          topLeft = Offset(0f, size.height - glowHeight),
          size = Size(size.width, glowHeight)
        )
      }
    }
  }
}

/**
 * Modifier to apply the custom Cyber Overscroll effect to any scrollable layout.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberCyberOverscrollEffect(glowColor: Color = CyberCyanGlow): CyberOverscrollEffect {
  val scope = rememberCoroutineScope()
  return remember(scope, glowColor) {
    CyberOverscrollEffect(scope = scope, glowColor = glowColor)
  }
}

/**
 * Modifier drawing the cyber cyan boundary glow over content.
 */
fun Modifier.drawCyberOverscroll(effect: CyberOverscrollEffect): Modifier {
  return this.drawWithContent {
    drawContent()
    effect.drawGlow(this)
  }
}
