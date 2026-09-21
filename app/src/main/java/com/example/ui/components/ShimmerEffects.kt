package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

// Palantir Enterprise Obsidian Shimmer Palette
val ShimmerBase = Color(0xFF0B1528)
val ShimmerHighlight = Color(0xFF1E3A5F)

/**
 * High-performance Shimmer skeleton loader brush.
 * Creates an animated specular highlight sweeping across a dark-slate placeholder.
 */
@Composable
fun rememberShimmerBrush(
  baseColor: Color = ShimmerBase,
  highlightColor: Color = ShimmerHighlight,
  durationMillis: Int = 1300
): Brush {
  val transition = rememberInfiniteTransition(label = "PalantirShimmer")
  val translateAnim by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1000f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = durationMillis, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ShimmerTranslate"
  )

  return Brush.linearGradient(
    colors = listOf(
      baseColor,
      highlightColor,
      baseColor
    ),
    start = Offset(translateAnim - 250f, translateAnim - 250f),
    end = Offset(translateAnim, translateAnim)
  )
}

/**
 * Modifier extension to apply a shimmer background effect to any composable placeholder.
 */
@Composable
fun Modifier.shimmerEffect(
  shape: Shape = RoundedCornerShape(8.dp),
  baseColor: Color = ShimmerBase,
  highlightColor: Color = ShimmerHighlight
): Modifier {
  val brush = rememberShimmerBrush(baseColor = baseColor, highlightColor = highlightColor)
  return this
    .clip(shape)
    .background(brush)
}

/**
 * Standalone Shimmer Box element for skeleton layouts.
 */
@Composable
fun ShimmerBox(
  modifier: Modifier = Modifier,
  shape: Shape = RoundedCornerShape(8.dp),
  baseColor: Color = ShimmerBase,
  highlightColor: Color = ShimmerHighlight
) {
  Box(
    modifier = modifier.shimmerEffect(
      shape = shape,
      baseColor = baseColor,
      highlightColor = highlightColor
    )
  )
}
