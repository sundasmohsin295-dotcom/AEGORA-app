package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 2. ADVANCED THREAT INTEL DASHBOARD (GLOWING CANVAS GRAPHS)
 * Renders an Apple/Palantir SaaS-grade telemetry card with smooth Bezier curve line graph,
 * glowing Cyan (#22D3EE) stroke, and vertical gradient fill (Cyan to transparent).
 */
@Composable
fun ThreatIntelDashboardCard(
  modifier: Modifier = Modifier,
  onInspectTelemetry: () -> Unit = {}
) {
  // Telemetry points for "Successful AI Defeats" over last 7 days / iterations
  val defeatDataPoints = remember {
    listOf(12f, 19f, 15f, 28f, 34f, 42f, 58f)
  }

  val transition = rememberInfiniteTransition(label = "pulse_intel")
  val glowPulse by transition.animateFloat(
    initialValue = 0.6f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_pulse"
  )

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF15171C)),
    border = BorderStroke(1.dp, Color(0xFF2D313A)),
    modifier = modifier
      .fillMaxWidth()
      .testTag("threat_intel_dashboard_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Top Metric Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF22D3EE).copy(alpha = glowPulse))
            )
            Text(
              text = "THREAT INTEL // ADVERSARY DEFENSE",
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF8A919E),
              letterSpacing = 1.sp
            )
          }
          Text(
            text = "Successful AI Defeats",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF0F4F8)
          )
        }

        Surface(
          color = Color(0x2222D3EE),
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, Color(0x6622D3EE))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.TrendingUp,
              contentDescription = null,
              tint = Color(0xFF22D3EE),
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "+38.4%",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFF22D3EE)
            )
          }
        }
      }

      // Stats Highlights
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Column {
          Text(
            text = "58 DEFEATS",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF22D3EE)
          )
          Text(
            text = "Zero-Day & Hallucinations Mitigated",
            fontSize = 11.sp,
            color = Color(0xFF8A919E)
          )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "99.4% CONF",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF34D399)
          )
          Text(
            text = "Hardware Enclave Signed",
            fontSize = 11.sp,
            color = Color(0xFF8A919E)
          )
        }
      }

      // Glowing Canvas Bezier Curve Graph
      ThreatIntelBezierGraph(
        dataPoints = defeatDataPoints,
        glowPulse = glowPulse,
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .padding(top = 8.dp)
      )

      // Time axis labels
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        listOf("T-6d", "T-5d", "T-4d", "T-3d", "T-2d", "T-1d", "LIVE").forEach { label ->
          Text(
            text = label,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (label == "LIVE") FontWeight.Bold else FontWeight.Normal,
            color = if (label == "LIVE") Color(0xFF22D3EE) else Color(0xFF64748B)
          )
        }
      }
    }
  }
}

/**
 * Custom Jetpack Compose Canvas Bezier Curve Graph with Glowing Cyan Stroke
 * and Rich Vertical Gradient Fill.
 */
@Composable
fun ThreatIntelBezierGraph(
  dataPoints: List<Float>,
  glowPulse: Float,
  modifier: Modifier = Modifier
) {
  Canvas(
    modifier = modifier
      .fillMaxWidth()
      .testTag("threat_intel_bezier_canvas")
  ) {
    if (dataPoints.size < 2) return@Canvas

    val width = size.width
    val height = size.height
    val paddingBottom = 12f
    val paddingTop = 12f
    val availableHeight = height - paddingTop - paddingBottom

    val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
    val minVal = (dataPoints.minOrNull() ?: 0f).coerceAtMost(maxVal - 1f)
    val range = (maxVal - minVal).coerceAtLeast(1f)

    val stepX = width / (dataPoints.size - 1)

    // Calculate (x, y) coordinates for each point
    val coordinates = dataPoints.mapIndexed { index, value ->
      val x = index * stepX
      val normalized = (value - minVal) / range
      val y = height - paddingBottom - (normalized * availableHeight)
      Offset(x, y)
    }

    // Build smooth Cubic Bezier Path
    val strokePath = Path()
    strokePath.moveTo(coordinates[0].x, coordinates[0].y)

    for (i in 0 until coordinates.size - 1) {
      val p0 = coordinates[i]
      val p1 = coordinates[i + 1]
      val controlX1 = p0.x + (p1.x - p0.x) / 2f
      val controlY1 = p0.y
      val controlX2 = p0.x + (p1.x - p0.x) / 2f
      val controlY2 = p1.y

      strokePath.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
    }

    // Build Closed Gradient Fill Path (Cyan to transparent)
    val fillPath = Path().apply {
      addPath(strokePath)
      lineTo(coordinates.last().x, height)
      lineTo(coordinates.first().x, height)
      close()
    }

    // 1. Draw smooth vertical gradient under line
    drawPath(
      path = fillPath,
      brush = Brush.verticalGradient(
        colors = listOf(
          Color(0xFF22D3EE).copy(alpha = 0.35f * glowPulse),
          Color(0xFF22D3EE).copy(alpha = 0.12f * glowPulse),
          Color.Transparent
        ),
        startY = paddingTop,
        endY = height
      )
    )

    // 2. Outer Glow Stroke
    drawPath(
      path = strokePath,
      color = Color(0xFF22D3EE).copy(alpha = 0.25f * glowPulse),
      style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 3. Crisp Cyan Main Stroke
    drawPath(
      path = strokePath,
      brush = Brush.horizontalGradient(
        colors = listOf(
          Color(0xFF0EA5E9),
          Color(0xFF22D3EE),
          Color(0xFF67E8F9)
        )
      ),
      style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 4. Glowing End Anchor Dot
    val lastPoint = coordinates.last()
    drawCircle(
      color = Color(0xFF22D3EE).copy(alpha = 0.3f * glowPulse),
      radius = 9f,
      center = lastPoint
    )
    drawCircle(
      color = Color(0xFF22D3EE),
      radius = 4.5f,
      center = lastPoint
    )
    drawCircle(
      color = Color.White,
      radius = 2f,
      center = lastPoint
    )
  }
}
