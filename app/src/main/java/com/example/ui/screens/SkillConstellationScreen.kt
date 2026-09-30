package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

data class ConstellationNode(
  val name: String,
  val level: String,
  val decay: String,
  val reviewIn: String,
  val angleDeg: Float,
  val color: Color
)

@Composable
fun SkillConstellationScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val nodes = remember {
    listOf(
      ConstellationNode("Malware Analysis", "Mastery L6", "Decay: 12%", "Next Review: 2 days", 270f, CyberViolet),
      ConstellationNode("Networking", "Mastery L4", "Decay: 10%", "Next Review: 3 days", 30f, ElectricCyan),
      ConstellationNode("Threat Intelligence", "Mastery L4", "Decay: 11%", "Next Review: 4 days", 100f, Color(0xFF38BDF8)),
      ConstellationNode("Incident Response", "Mastery L4", "Decay: 15%", "Next Review: 4 days", 160f, HighAlertCrimson),
      ConstellationNode("Linux", "Mastery L5", "Decay: 9%", "Next Review: 3 days", 215f, TacticalEmerald)
    )
  }

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
        .testTag("skill_constellation_screen")
    ) {
      // 1. Header Bar
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
          Column {
            Text("Cyber Skill Constellation", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            Text("Build Skills. Track Progress. Grow.", color = TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
          }
        }

        // Overall Progress Ring Mini Badge
        Row(
          modifier = Modifier
            .background(ObsidianSurface, RoundedCornerShape(8.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text("68%", color = TacticalEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
          Text("Mastery", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Responsive Content
      if (isWideScreen) {
        Row(
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          ConstellationCanvasView(nodes, modifier = Modifier.weight(1.5f).fillMaxHeight())
          ConstellationProgressSidebar(modifier = Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()))
        }
      } else {
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          ConstellationCanvasView(nodes, modifier = Modifier.fillMaxWidth().height(320.dp))
          ConstellationProgressSidebar(modifier = Modifier.fillMaxWidth())
        }
      }
    }
  }
}

@Composable
private fun ConstellationCanvasView(nodes: List<ConstellationNode>, modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_constellation")
  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(tween(45000, easing = LinearEasing)),
    label = "rotation"
  )

  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(12.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = kotlin.math.min(size.width, size.height) * 0.38f

        // Orbit ring
        drawCircle(SlateBorder.copy(alpha = 0.5f), radius = radius, center = center, style = Stroke(1.5f))
        drawCircle(SlateBorder.copy(alpha = 0.25f), radius = radius * 0.6f, center = center, style = Stroke(1f))

        // Center CYBER Node
        drawCircle(
          brush = Brush.radialGradient(listOf(ElectricCyan.copy(alpha = 0.6f), Color.Transparent)),
          radius = 36.dp.toPx(),
          center = center
        )
        drawCircle(Color(0xFF0F172A), radius = 24.dp.toPx(), center = center)
        drawCircle(ElectricCyan, radius = 24.dp.toPx(), center = center, style = Stroke(2f))

        // Branching constellation lines & outer nodes
        nodes.forEach { node ->
          val rad = Math.toRadians((node.angleDeg + rotationAngle).toDouble())
          val nodePos = Offset(
            center.x + (radius * cos(rad)).toFloat(),
            center.y + (radius * sin(rad)).toFloat()
          )

          // Glowing connecting line
          drawLine(
            color = node.color.copy(alpha = 0.6f),
            start = center,
            end = nodePos,
            strokeWidth = 2f
          )

          // Glowing outer node
          drawCircle(node.color.copy(alpha = 0.25f), radius = 18.dp.toPx(), center = nodePos)
          drawCircle(ObsidianSurface, radius = 12.dp.toPx(), center = nodePos)
          drawCircle(node.color, radius = 12.dp.toPx(), center = nodePos, style = Stroke(2f))
        }
      }

      // Center Text
      Text("CYBER", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

      // Overlay Instructions / Legend
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .padding(bottom = 12.dp)
          .background(ObsidianBackground.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
          .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text("5 Active Skill Vectors • Continuous Micro-Assessment", color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
      }
    }
  }
}

@Composable
private fun ConstellationProgressSidebar(modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {
    // Mastery Gauge Card
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
          Text("Overall Progress", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          Spacer(modifier = Modifier.height(2.dp))
          Text("68%", color = TacticalEmerald, fontWeight = FontWeight.Bold, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
          Text("Skill Mastery", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }

        Box(contentAlignment = Alignment.Center) {
          Canvas(modifier = Modifier.size(60.dp)) {
            val stroke = 6.dp.toPx()
            drawArc(SlateBorder, 0f, 360f, false, style = Stroke(stroke))
            drawArc(TacticalEmerald, -90f, 360f * 0.68f, false, style = Stroke(stroke))
          }
          Icon(Icons.Default.Check, contentDescription = null, tint = TacticalEmerald, modifier = Modifier.size(20.dp))
        }
      }
    }

    // Learning Path Stepper
    Card(
      modifier = Modifier.fillMaxWidth(),
      colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
      border = BorderStroke(1.dp, SlateBorder),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Learning Path", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace)

        PathStepItem("1. Linux Basics", isCompleted = true, isCurrent = false)
        PathStepItem("2. Network Security", isCompleted = true, isCurrent = false)
        PathStepItem("3. SIEM & Log Analysis", isCompleted = false, isCurrent = true)
        PathStepItem("4. Malware Analysis", isCompleted = false, isCurrent = false)
        PathStepItem("5. Cloud Security", isCompleted = false, isCurrent = false)

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
          onClick = {},
          modifier = Modifier.fillMaxWidth().height(38.dp),
          border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text("View Full Constellation", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
      }
    }
  }
}

@Composable
private fun PathStepItem(title: String, isCompleted: Boolean, isCurrent: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      title,
      color = if (isCurrent) ElectricCyan else if (isCompleted) TextPrimary else TextDim,
      fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
      fontSize = 11.sp,
      fontFamily = FontFamily.Monospace
    )

    if (isCompleted) {
      Icon(Icons.Default.Check, contentDescription = "Completed", tint = TacticalEmerald, modifier = Modifier.size(16.dp))
    } else if (isCurrent) {
      Box(
        modifier = Modifier
          .background(ElectricCyanDark, RoundedCornerShape(3.dp))
          .border(1.dp, ElectricCyan, RoundedCornerShape(3.dp))
          .padding(horizontal = 4.dp, vertical = 1.dp)
      ) {
        Text("ACTIVE", color = ElectricCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
      }
    } else {
      Text("...", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    }
  }
}
