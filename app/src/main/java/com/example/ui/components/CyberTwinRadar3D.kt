package com.example.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hardware.DeviceThermalState
import com.example.hardware.rememberDeviceThermalState
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class RadarAxisData(
  val name: String,
  val score: Float // Normalized 0.0f .. 1.0f
)

/**
 * CYBER TWIN 3D CAPABILITY RADAR
 *
 * Custom Canvas drawing an isometric 5-axis polygon:
 * - Investigation
 * - Reasoning
 * - Tech Skill
 * - Evidence
 * - AI Oversight
 *
 * 3D Effect: Multi-layered isometric polygons with decreasing alpha opacity.
 * Hardware-Aware: Automatically reduces shader complexity (disables BlurMaskFilter)
 * and simplifies depth projection layers if device enters MODERATE/SEVERE/CRITICAL thermal state.
 */
@Composable
fun CyberTwinRadar3D(
  axes: List<RadarAxisData> = listOf(
    RadarAxisData("Investigation", 0.88f),
    RadarAxisData("Reasoning", 0.74f),
    RadarAxisData("Tech Skill", 0.92f),
    RadarAxisData("Evidence", 0.68f),
    RadarAxisData("AI Oversight", 0.82f)
  ),
  modifier: Modifier = Modifier,
  isInteractive: Boolean = true
) {
  val thermalState by rememberDeviceThermalState()

  // In throttled thermal state, disable continuous pulse or reduce frame overhead
  val enablePulse = thermalState == DeviceThermalState.NORMAL
  val enableBlurGlowShader = thermalState == DeviceThermalState.NORMAL || thermalState == DeviceThermalState.MODERATE
  val depthLayers = when (thermalState) {
    DeviceThermalState.NORMAL -> 4
    DeviceThermalState.MODERATE -> 2
    DeviceThermalState.SEVERE, DeviceThermalState.CRITICAL -> 1
  }

  // Smooth entry animation for capability values
  val transition = updateTransition(targetState = axes, label = "RadarAnimation")

  val animatedScores = axes.mapIndexed { index, axis ->
    val animVal by animateFloatAsState(
      targetValue = axis.score,
      animationSpec = tween(durationMillis = 1000 + (index * 150), easing = FastOutSlowInEasing),
      label = "AxisScore_$index"
    )
    animVal
  }

  // Continuous subtle pulse animation for 3D holographic effect (only if not thermally throttled)
  val infiniteTransition = rememberInfiniteTransition(label = "Radar3DPulse")
  val pulsePhase by if (enablePulse) {
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(2400, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      ),
      label = "pulsePhase"
    )
  } else {
    remember { mutableFloatStateOf(0.5f) }
  }

  // Reusable cached framework Paint & MaskFilter to eliminate allocations in onDraw
  val cachedGlowPaint = remember {
    android.graphics.Paint().apply {
      isAntiAlias = true
      color = android.graphics.Color.parseColor("#3B82F6")
      style = android.graphics.Paint.Style.STROKE
      maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
    }
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .heightIn(min = 200.dp, max = 280.dp)
      .testTag("cyber_twin_radar_3d")
      .clip(RoundedCornerShape(16.dp))
      .background(
        Brush.radialGradient(
          colors = listOf(
            SpecPrimaryBlue.copy(alpha = 0.18f),
            SpecCardBg.copy(alpha = 0.85f),
            SpecCanvasBg
          ),
          radius = 500f
        )
      )
      .border(1.dp, SpecBorder.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
    contentAlignment = Alignment.Center
  ) {
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
      val center = Offset(size.width / 2f, size.height / 2f + 10.dp.toPx())
      val radius = (minOf(size.width, size.height) / 2f) * 0.72f

      // Isometric projection factors: compress Y to tilt plane into 3D space
      val isometricYScale = 0.65f
      val numAxes = 5
      val angleStep = (2 * PI / numAxes).toFloat()
      val startAngle = (-PI / 2).toFloat() // Start at 12 o'clock

      // ------------------------------------------------------------------------
      // 1. ISOMETRIC DEPTH FOUNDATION (Layered grid polygons at decreasing depths)
      // ------------------------------------------------------------------------
      val layerElevationPx = 14.dp.toPx()

      for (layer in (depthLayers - 1) downTo 0) {
        val yOffset = layer * layerElevationPx
        val layerAlpha = when (layer) {
          0 -> 0.45f
          1 -> 0.28f
          2 -> 0.16f
          else -> 0.08f
        }
        val gridSteps = 3
        for (step in 1..gridSteps) {
          val stepFraction = step / gridSteps.toFloat()
          val gridPath = Path()

          for (i in 0 until numAxes) {
            val angle = startAngle + i * angleStep
            val r = radius * stepFraction
            val x = center.x + r * cos(angle)
            val y = center.y - yOffset + (r * sin(angle) * isometricYScale)
            if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
          }
          gridPath.close()

          drawPath(
            path = gridPath,
            color = SpecBorder.copy(alpha = layerAlpha),
            style = Stroke(width = if (layer == 0) 1.2f.dp.toPx() else 0.8f.dp.toPx())
          )
        }
      }

      // Vertical 3D Extrusion Struts linking the base depth to the top plane
      for (i in 0 until numAxes) {
        val angle = startAngle + i * angleStep
        val topX = center.x + radius * cos(angle)
        val topY = center.y + (radius * sin(angle) * isometricYScale)
        val bottomY = center.y - ((depthLayers - 1) * layerElevationPx) + (radius * sin(angle) * isometricYScale)

        drawLine(
          color = SpecBorder.copy(alpha = 0.25f),
          start = Offset(topX, bottomY),
          end = Offset(topX, topY),
          strokeWidth = 1.dp.toPx(),
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        )

        // Radial axis lines on primary surface
        drawLine(
          color = SpecBorder.copy(alpha = 0.6f),
          start = center,
          end = Offset(topX, topY),
          strokeWidth = 1.dp.toPx()
        )
      }

      // ------------------------------------------------------------------------
      // 2. LAYERED 3D CAPABILITY POLYGONS (Decreasing alpha depth layers)
      // ------------------------------------------------------------------------
      val capabilityDepths = listOf(
        Pair(18.dp.toPx(), 0.12f), // Shadow base projection
        Pair(9.dp.toPx(), 0.25f),  // Mid-depth holographic projection
        Pair(0.dp.toPx(), 1.00f)   // Top active glowing layer
      )

      capabilityDepths.forEach { (elevation, alphaMult) ->
        val capPath = Path()
        for (i in 0 until numAxes) {
          val angle = startAngle + i * angleStep
          val score = animatedScores.getOrElse(i) { 0.5f }
          val r = radius * score
          val x = center.x + r * cos(angle)
          val y = center.y - elevation + (r * sin(angle) * isometricYScale)
          if (i == 0) capPath.moveTo(x, y) else capPath.lineTo(x, y)
        }
        capPath.close()

        if (elevation > 0f) {
          // Under-layer fill
          drawPath(
            path = capPath,
            brush = Brush.verticalGradient(
              colors = listOf(
                SpecPrimaryBlue.copy(alpha = 0.20f * alphaMult),
                SpecCyanHighlight.copy(alpha = 0.05f * alphaMult)
              )
            )
          )
          drawPath(
            path = capPath,
            color = SpecPrimaryBlue.copy(alpha = 0.35f * alphaMult),
            style = Stroke(width = 1.2f.dp.toPx())
          )
        } else {
          // TOP ACTIVE POLYGON: Translucent fill gradient
          drawPath(
            path = capPath,
            brush = Brush.radialGradient(
              colors = listOf(
                SpecPrimaryBlue.copy(alpha = 0.42f),
                SpecCyanHighlight.copy(alpha = 0.20f),
                SpecPrimaryBlue.copy(alpha = 0.06f)
              ),
              center = center,
              radius = radius
            )
          )

          // Glowing Cobalt Blue Stroke with Shadow Blur (using recycled framework paint, disabled in severe thermal states)
          if (enableBlurGlowShader) {
            drawIntoCanvas { canvas ->
              cachedGlowPaint.strokeWidth = 2.5f.dp.toPx()
              canvas.nativeCanvas.drawPath(capPath.asAndroidPath(), cachedGlowPaint)
            }
          }

          // Crisp primary stroke on top of the blur glow
          drawPath(
            path = capPath,
            color = SpecPrimaryBlue,
            style = Stroke(width = 2.2f.dp.toPx())
          )

          // --------------------------------------------------------------------
          // 3. GLOWING CYAN DATA POINTS (Circles at each vertex)
          // --------------------------------------------------------------------
          for (i in 0 until numAxes) {
            val angle = startAngle + i * angleStep
            val score = animatedScores.getOrElse(i) { 0.5f }
            val r = radius * score
            val vx = center.x + r * cos(angle)
            val vy = center.y + (r * sin(angle) * isometricYScale)

            // Outer cyan glow aura
            drawCircle(
              color = SpecCyanHighlight.copy(alpha = 0.35f),
              radius = 8.dp.toPx(),
              center = Offset(vx, vy)
            )
            // Intermediate stroke
            drawCircle(
              color = Color.White,
              radius = 4.5f.dp.toPx(),
              center = Offset(vx, vy)
            )
            // Solid center cyan core
            drawCircle(
              color = SpecCyanHighlight,
              radius = 3.dp.toPx(),
              center = Offset(vx, vy)
            )
          }
        }
      }
    }

    // Top-right holographic telemetry badge
    Column(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 4.dp, end = 12.dp),
      horizontalAlignment = Alignment.End
    ) {
      Text(
        text = "3D RADAR // ACTIVE",
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        color = SpecCyanHighlight,
        letterSpacing = 1.sp
      )
      Text(
        text = "ISOMETRIC 5-AXIS",
        fontSize = 9.sp,
        fontFamily = FontFamily.Monospace,
        color = SpecEmeraldVerification
      )
    }
  }
}
