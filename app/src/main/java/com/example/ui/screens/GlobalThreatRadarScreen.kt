package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlobalThreatRadarScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
  ) {
    val isWideScreen = maxWidth >= 840.dp

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .testTag("global_threat_radar_screen")
    ) {
      // 1. Top Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(ObsidianSurface)
              .border(1.dp, SlateBorder, CircleShape)
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricCyan, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Global Threat Radar",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextMuted, modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.MyLocation, contentDescription = "Center", tint = TextMuted, modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = TextMuted, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Main Content Layout (Responsive)
      if (isWideScreen) {
        Row(
          modifier = Modifier.fillMaxSize(),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          InteractiveGlobalMap(modifier = Modifier.weight(1.6f).fillMaxHeight())
          RadarMetricsSidebar(modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()))
        }
      } else {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          InteractiveGlobalMap(modifier = Modifier.fillMaxWidth().height(280.dp))
          RadarMetricsSidebar(modifier = Modifier.fillMaxWidth())
        }
      }
    }
  }
}

@Composable
private fun InteractiveGlobalMap(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")
  val pulsePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing)),
    label = "radar_phase"
  )

  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val w = size.width
        val h = size.height

        // Background latitude & longitude grid
        for (i in 1..4) {
          val y = h * (i / 5f)
          drawLine(SlateBorder.copy(alpha = 0.35f), Offset(0f, y), Offset(w, y), strokeWidth = 1f)
        }
        for (j in 1..6) {
          val x = w * (j / 7f)
          drawLine(SlateBorder.copy(alpha = 0.35f), Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
        }

        // Continental approximate shapes (High-tech vectorized cyber continents)
        val na = Offset(w * 0.24f, h * 0.38f)
        val sa = Offset(w * 0.32f, h * 0.68f)
        val eu = Offset(w * 0.50f, h * 0.32f)
        val af = Offset(w * 0.52f, h * 0.58f)
        val as_ = Offset(w * 0.72f, h * 0.34f)
        val au = Offset(w * 0.82f, h * 0.72f)

        drawCircle(SlateBorderBright.copy(alpha = 0.5f), radius = 24.dp.toPx(), center = na)
        drawCircle(SlateBorderBright.copy(alpha = 0.5f), radius = 18.dp.toPx(), center = sa)
        drawCircle(SlateBorderBright.copy(alpha = 0.5f), radius = 20.dp.toPx(), center = eu)
        drawCircle(SlateBorderBright.copy(alpha = 0.5f), radius = 22.dp.toPx(), center = af)
        drawCircle(SlateBorderBright.copy(alpha = 0.5f), radius = 32.dp.toPx(), center = as_)
        drawCircle(SlateBorderBright.copy(alpha = 0.5f), radius = 16.dp.toPx(), center = au)

        // Attack origins (Red)
        val originMoscow = Offset(w * 0.62f, h * 0.28f)
        val originBeijing = Offset(w * 0.76f, h * 0.36f)
        val originEasternEu = Offset(w * 0.54f, h * 0.30f)

        // Targets (Blue/Cyan)
        val targetUsEast = Offset(w * 0.28f, h * 0.36f)
        val targetFrankfurt = Offset(w * 0.48f, h * 0.32f)
        val targetTokyo = Offset(w * 0.84f, h * 0.38f)

        // Draw attack arcs with dynamic animated particles
        drawAttackArc(originMoscow, targetUsEast, HighAlertCrimson, pulsePhase)
        drawAttackArc(originBeijing, targetUsEast, TacticalAmber, (pulsePhase + 0.3f) % 1f)
        drawAttackArc(originEasternEu, targetFrankfurt, HighAlertCrimson, (pulsePhase + 0.6f) % 1f)
        drawAttackArc(originBeijing, targetTokyo, Color(0xFFEC4899), (pulsePhase + 0.45f) % 1f)

        // Draw nodes
        listOf(originMoscow, originBeijing, originEasternEu).forEach { pt ->
          drawCircle(HighAlertCrimson.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = pt)
          drawCircle(HighAlertCrimson, radius = 4.dp.toPx(), center = pt)
        }
        listOf(targetUsEast, targetFrankfurt, targetTokyo).forEach { pt ->
          drawCircle(ElectricCyan.copy(alpha = 0.25f), radius = 10.dp.toPx(), center = pt)
          drawCircle(ElectricCyan, radius = 4.dp.toPx(), center = pt)
        }
      }

      // Legend & Live Feed overlay
      Row(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(12.dp)
          .background(ObsidianBackground.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
          .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.width(10.dp).height(2.dp).background(HighAlertCrimson))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Attack Path", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HighAlertCrimson))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Origin", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ElectricCyan))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Target", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
      }

      // Status pill at top
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(12.dp)
          .background(ObsidianBackground.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
          .border(1.dp, TacticalEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("● LIVE • 12,482 events/min", color = TacticalEmerald, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
      }
    }
  }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAttackArc(
  start: Offset,
  end: Offset,
  color: Color,
  phase: Float
) {
  val midX = (start.x + end.x) / 2f
  val midY = kotlin.math.min(start.y, end.y) - 35f
  val path = Path().apply {
    moveTo(start.x, start.y)
    quadraticTo(midX, midY, end.x, end.y)
  }
  drawPath(path, color = color.copy(alpha = 0.5f), style = Stroke(width = 1.5f, cap = StrokeCap.Round))

  // Particle running along path
  val particleX = (1 - phase) * (1 - phase) * start.x + 2 * (1 - phase) * phase * midX + phase * phase * end.x
  val particleY = (1 - phase) * (1 - phase) * start.y + 2 * (1 - phase) * phase * midY + phase * phase * end.y
  drawCircle(color, radius = 3.dp.toPx(), center = Offset(particleX, particleY))
}

@Composable
private fun RadarMetricsSidebar(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Active Threats KPI
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Active Threats", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          Spacer(modifier = Modifier.height(2.dp))
          Text("342", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Box(
          modifier = Modifier
            .background(HighAlertCrimsonDark, RoundedCornerShape(4.dp))
            .border(1.dp, HighAlertCrimson, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Text("↑ 18%", color = HighAlertCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
      }
    }

    // 2. Top Attack Origins
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Top Attack Origins", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        RegionPercentBar("Russia", 0.22f, "22%", HighAlertCrimson)
        RegionPercentBar("China", 0.18f, "18%", TacticalAmber)
        RegionPercentBar("USA", 0.12f, "12%", ElectricCyan)
        RegionPercentBar("Ukraine", 0.09f, "9%", TacticalEmerald)
        RegionPercentBar("Other", 0.39f, "39%", TextDim)
      }
    }

    // 3. Top Target Regions
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Top Target Regions", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        RegionPercentBar("US", 0.28f, "28%", ElectricCyan)
        RegionPercentBar("EU", 0.22f, "22%", CyberViolet)
        RegionPercentBar("Asia", 0.18f, "18%", TacticalEmerald)
        RegionPercentBar("Middle East", 0.14f, "14%", TacticalAmber)
        RegionPercentBar("Other", 0.18f, "18%", TextDim)
      }
    }

    // 4. Threat Index Dial & APT Activity
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Threat Index", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          Spacer(modifier = Modifier.height(6.dp))

          Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(72.dp)) {
              val stroke = 8.dp.toPx()
              drawArc(
                color = SlateBorder,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
              )
              drawArc(
                brush = Brush.sweepGradient(listOf(TacticalEmerald, TacticalAmber, HighAlertCrimson)),
                startAngle = 135f,
                sweepAngle = 270f * 0.72f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
              )
            }
            Text("72", color = HighAlertCrimson, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
          Text("/ 100", color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("APT Activity", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(HighAlertCrimson))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Active", color = HighAlertCrimson, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }
  }
}

@Composable
private fun RegionPercentBar(label: String, fraction: Float, pctText: String, color: Color) {
  Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(label, color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
      Text(pctText, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
    LinearProgressIndicator(
      progress = { fraction },
      modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
      color = color,
      trackColor = SlateBorder
    )
  }
}
