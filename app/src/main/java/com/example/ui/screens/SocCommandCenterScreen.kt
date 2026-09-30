package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import kotlin.math.cos
import kotlin.math.sin

data class SocIncidentItem(
  val id: String,
  val type: String,
  val severity: String,
  val status: String,
  val timeAgo: String
)

private val SAMPLE_INCIDENTS = listOf(
  SocIncidentItem("INC-20481", "Credential Compromise", "CRITICAL", "Investigating", "10m ago"),
  SocIncidentItem("INC-20477", "Suspicious Login", "HIGH", "Contained", "32m ago"),
  SocIncidentItem("INC-20471", "Malware Detection", "HIGH", "Investigating", "1h ago"),
  SocIncidentItem("INC-20465", "Data Exfiltration Attempt", "MEDIUM", "Resolved", "2h ago"),
  SocIncidentItem("INC-20452", "DDoS Attack", "MEDIUM", "Contained", "3h ago")
)

@Composable
fun SocCommandCenterScreen(
  onNavigateToIncident: (String) -> Unit,
  onNavigateToRadar: () -> Unit,
  onNavigateToAi: () -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }

  // Pulsing Live Indicator
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_live")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
  ) {
    val isWideScreen = maxWidth >= 840.dp
    val isMediumScreen = maxWidth >= 600.dp && maxWidth < 840.dp

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("soc_command_center_screen"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Top Search & Header Bar
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Search Bar
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("soc_search_input"),
              placeholder = {
                Text(
                  text = "Search threats, IPs, hosts, or ask Sentinel...",
                  color = TextDim,
                  fontSize = 12.sp,
                  fontFamily = FontFamily.Monospace
                )
              },
              leadingIcon = {
                Icon(
                  Icons.Default.Search,
                  contentDescription = "Search",
                  tint = TextMuted,
                  modifier = Modifier.size(18.dp)
                )
              },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ObsidianSurface,
                unfocusedContainerColor = ObsidianSurface,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
              ),
              shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Status Badge & Clock
            Row(
              modifier = Modifier
                .background(ObsidianSurface, RoundedCornerShape(8.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(TacticalEmerald.copy(alpha = pulseAlpha))
              )
              Text(
                text = "Online",
                color = TacticalEmerald,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Box(
                modifier = Modifier
                  .width(1.dp)
                  .height(14.dp)
                  .background(SlateBorder)
              )
              Text(
                text = "14:32 • Apr 26",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          // Main Title Banner
          Column {
            Text(
              text = "SOC COMMAND CENTER",
              color = ElectricCyan,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 1.sp
            )
            Text(
              text = "Smarter Detection. Faster Response. Safer Tomorrow.",
              color = TextMuted,
              fontSize = 12.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // 2. 4 Top Metric Cards (Adaptive Grid)
      item {
        if (isWideScreen) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            SocMetricCard("Security Posture", "87%", "+6%", TacticalEmerald, Icons.Default.Shield, Modifier.weight(1f))
            SocMetricCard("Critical Alerts", "04", "+2%", HighAlertCrimson, Icons.Default.Warning, Modifier.weight(1f))
            SocMetricCard("Active Incidents", "07", "+1%", Color(0xFF38BDF8), Icons.Default.Security, Modifier.weight(1f))
            SocMetricCard("Threats Blocked", "1,284", "+12%", TacticalEmerald, Icons.Default.CheckCircle, Modifier.weight(1f))
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              SocMetricCard("Security Posture", "87%", "+6%", TacticalEmerald, Icons.Default.Shield, Modifier.weight(1f))
              SocMetricCard("Critical Alerts", "04", "+2%", HighAlertCrimson, Icons.Default.Warning, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              SocMetricCard("Active Incidents", "07", "+1%", Color(0xFF38BDF8), Icons.Default.Security, Modifier.weight(1f))
              SocMetricCard("Threats Blocked", "1,284", "+12%", TacticalEmerald, Icons.Default.CheckCircle, Modifier.weight(1f))
            }
          }
        }
      }

      // 3. Network Activity Chart & Threat Distribution Donut
      item {
        if (isWideScreen) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            NetworkActivityCard(modifier = Modifier.weight(1.3f))
            ThreatDistributionCard(modifier = Modifier.weight(1f))
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NetworkActivityCard(modifier = Modifier.fillMaxWidth())
            ThreatDistributionCard(modifier = Modifier.fillMaxWidth())
          }
        }
      }

      // 4. Sentinel AI Recommendation Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("sentinel_ai_recommendation_card"),
          colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
          border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(ElectricCyan.copy(alpha = 0.15f))
                .border(1.dp, ElectricCyan, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.SmartToy, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "AI Recommendation • Sentinel AI",
                color = ElectricCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Suspicious lateral movement detected from host 10.10.5.23. Recommended: isolate host and check for credential dumping.",
                color = TextMuted,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                fontFamily = FontFamily.Monospace
              )
            }

            Button(
              onClick = { onNavigateToIncident("INC-20481") },
              colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.testTag("view_incident_details_btn")
            ) {
              Text("View Details", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }

      // 5. Recent Incidents Table & Live Threat Map Preview
      item {
        if (isWideScreen) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            RecentIncidentsTable(
              incidents = SAMPLE_INCIDENTS,
              onSelectIncident = onNavigateToIncident,
              modifier = Modifier.weight(1.3f)
            )
            LiveThreatMapWidget(
              onOpenRadar = onNavigateToRadar,
              modifier = Modifier.weight(1f)
            )
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RecentIncidentsTable(
              incidents = SAMPLE_INCIDENTS,
              onSelectIncident = onNavigateToIncident,
              modifier = Modifier.fillMaxWidth()
            )
            LiveThreatMapWidget(
              onOpenRadar = onNavigateToRadar,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SocMetricCard(
  title: String,
  value: String,
  change: String,
  accentColor: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          color = TextDim,
          fontSize = 11.sp,
          fontFamily = FontFamily.Monospace
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = value,
          color = TextPrimary,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = change,
          color = accentColor,
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

@Composable
private fun NetworkActivityCard(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Network Activity",
          color = TextPrimary,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ElectricCyan))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Inbound", color = TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyberViolet))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Outbound", color = TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Custom Canvas Wave Graph
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
      ) {
        val width = size.width
        val height = size.height
        val stepX = width / 5f

        // Draw horizontal grid lines
        for (i in 0..3) {
          val y = height * (i / 3f)
          drawLine(
            color = SlateBorder.copy(alpha = 0.5f),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 1f
          )
        }

        // Inbound points
        val inboundPoints = listOf(
          Offset(0f, height * 0.7f),
          Offset(stepX, height * 0.45f),
          Offset(stepX * 2, height * 0.35f),
          Offset(stepX * 3, height * 0.6f),
          Offset(stepX * 4, height * 0.25f),
          Offset(width, height * 0.4f)
        )

        val inboundPath = Path().apply {
          moveTo(inboundPoints[0].x, inboundPoints[0].y)
          for (i in 1 until inboundPoints.size) {
            val p0 = inboundPoints[i - 1]
            val p1 = inboundPoints[i]
            quadraticTo(p0.x + (p1.x - p0.x) / 2f, p0.y, p1.x, p1.y)
          }
        }

        val inboundFill = Path().apply {
          addPath(inboundPath)
          lineTo(width, height)
          lineTo(0f, height)
          close()
        }

        drawPath(
          path = inboundFill,
          brush = Brush.verticalGradient(
            colors = listOf(ElectricCyan.copy(alpha = 0.3f), Color.Transparent)
          )
        )

        drawPath(
          path = inboundPath,
          color = ElectricCyan,
          style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // Outbound path
        val outboundPoints = listOf(
          Offset(0f, height * 0.85f),
          Offset(stepX, height * 0.7f),
          Offset(stepX * 2, height * 0.5f),
          Offset(stepX * 3, height * 0.75f),
          Offset(stepX * 4, height * 0.45f),
          Offset(width, height * 0.6f)
        )

        val outboundPath = Path().apply {
          moveTo(outboundPoints[0].x, outboundPoints[0].y)
          for (i in 1 until outboundPoints.size) {
            val p0 = outboundPoints[i - 1]
            val p1 = outboundPoints[i]
            quadraticTo(p0.x + (p1.x - p0.x) / 2f, p0.y, p1.x, p1.y)
          }
        }

        drawPath(
          path = outboundPath,
          color = CyberViolet,
          style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        listOf("00:00", "04:00", "08:00", "12:00", "16:00", "20:00").forEach { time ->
          Text(text = time, color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
      }
    }
  }
}

@Composable
private fun ThreatDistributionCard(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "Threat Distribution",
        color = TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        // Donut Chart Canvas
        Box(contentAlignment = Alignment.Center) {
          Canvas(modifier = Modifier.size(110.dp)) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val radius = diameter / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            val segments = listOf(
              38f to Color(0xFF38BDF8), // Malware
              22f to HighAlertCrimson,  // Phishing
              16f to TacticalAmber,     // Brute Force
              12f to CyberViolet,       // C2
              12f to TacticalEmerald    // Other
            )

            var startAngle = -90f
            segments.forEach { (pct, color) ->
              val sweep = 360f * (pct / 100f)
              drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep - 3f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(diameter, diameter),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
              )
              startAngle += sweep
            }
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("1,284", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            Text("Total", color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
          }
        }

        // Legend
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          LegendRow("Malware", "38%", Color(0xFF38BDF8))
          LegendRow("Phishing", "22%", HighAlertCrimson)
          LegendRow("Brute Force", "16%", TacticalAmber)
          LegendRow("C2", "12%", CyberViolet)
          LegendRow("Other", "12%", TacticalEmerald)
        }
      }
    }
  }
}

@Composable
private fun LegendRow(label: String, pct: String, color: Color) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(color))
    Spacer(modifier = Modifier.width(6.dp))
    Text(text = label, color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(72.dp))
    Text(text = pct, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
  }
}

@Composable
private fun RecentIncidentsTable(
  incidents: List<SocIncidentItem>,
  onSelectIncident: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "Recent Incidents",
        color = TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(10.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        incidents.forEach { inc ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(ObsidianBackground)
              .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
              .clickable { onSelectIncident(inc.id) }
              .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(inc.id, color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              Text(inc.type, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              val (sevBg, sevColor) = if (inc.severity == "CRITICAL") HighAlertCrimsonDark to HighAlertCrimson
              else if (inc.severity == "HIGH") TacticalAmberDark to TacticalAmber
              else TacticalEmeraldDark to TacticalEmerald

              Box(
                modifier = Modifier
                  .background(sevBg, RoundedCornerShape(3.dp))
                  .border(1.dp, sevColor, RoundedCornerShape(3.dp))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(inc.severity, color = sevColor, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
              }

              Text(inc.timeAgo, color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LiveThreatMapWidget(
  onOpenRadar: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.clickable { onOpenRadar() },
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.3f)),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Live Threat Map", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Box(
          modifier = Modifier
            .background(HighAlertCrimsonDark, RoundedCornerShape(4.dp))
            .border(1.dp, HighAlertCrimson, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text("● LIVE", color = HighAlertCrimson, fontSize = 8.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Simplified World Map Silhouette Canvas with animated attack arcs
      val infinite = rememberInfiniteTransition(label = "arc_flow")
      val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "phase"
      )

      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(110.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(ObsidianBackground)
      ) {
        val w = size.width
        val h = size.height

        // Continental approximate clusters
        drawCircle(SlateBorderBright.copy(alpha = 0.4f), radius = 18.dp.toPx(), center = Offset(w * 0.28f, h * 0.4f)) // North America
        drawCircle(SlateBorderBright.copy(alpha = 0.4f), radius = 14.dp.toPx(), center = Offset(w * 0.35f, h * 0.7f)) // South America
        drawCircle(SlateBorderBright.copy(alpha = 0.4f), radius = 16.dp.toPx(), center = Offset(w * 0.52f, h * 0.35f)) // Europe
        drawCircle(SlateBorderBright.copy(alpha = 0.4f), radius = 18.dp.toPx(), center = Offset(w * 0.55f, h * 0.6f)) // Africa
        drawCircle(SlateBorderBright.copy(alpha = 0.4f), radius = 24.dp.toPx(), center = Offset(w * 0.72f, h * 0.38f)) // Asia

        // Origins (red)
        val origins = listOf(Offset(w * 0.68f, h * 0.32f), Offset(w * 0.75f, h * 0.42f))
        // Targets (cyan)
        val targets = listOf(Offset(w * 0.28f, h * 0.38f), Offset(w * 0.52f, h * 0.34f))

        origins.forEach { o ->
          drawCircle(HighAlertCrimson, radius = 4.dp.toPx(), center = o)
        }
        targets.forEach { t ->
          drawCircle(ElectricCyan, radius = 4.dp.toPx(), center = t)
        }

        // Curved attack arcs
        val arc1 = Path().apply {
          moveTo(origins[0].x, origins[0].y)
          quadraticTo((origins[0].x + targets[0].x) / 2f, 10f, targets[0].x, targets[0].y)
        }
        drawPath(arc1, color = HighAlertCrimson.copy(alpha = 0.6f), style = Stroke(width = 2f, cap = StrokeCap.Round))

        val arc2 = Path().apply {
          moveTo(origins[1].x, origins[1].y)
          quadraticTo((origins[1].x + targets[1].x) / 2f, 20f, targets[1].x, targets[1].y)
        }
        drawPath(arc2, color = TacticalAmber.copy(alpha = 0.6f), style = Stroke(width = 2f, cap = StrokeCap.Round))
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Tap to expand 3D Global Radar →",
        color = ElectricCyan,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.align(Alignment.End)
      )
    }
  }
}
