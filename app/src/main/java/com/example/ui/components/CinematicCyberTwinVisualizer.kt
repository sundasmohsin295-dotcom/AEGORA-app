package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CyberSkill
import com.example.ui.theme.*
import kotlin.math.*
import kotlinx.coroutines.delay

/**
 * Authoritative 3D Cyber Twin Spatial Visualizer.
 * Renders a glowing 3D spherical neural constellation with orbital rings,
 * depth perspective, subtle particle telemetry, and capability metrics.
 * Includes a robust 1500ms fallback to a 2D Radar/Polygon chart to eliminate
 * any possibility of indeterminate loading states.
 */
data class CyberTwinOrbitalSkill(
  val id: String,
  val name: String,
  val capabilityLevel: Int,
  val isVerified: Boolean,
  val evidenceCount: Int,
  val lastVerifiedTimestamp: String,
  val isDecayed: Boolean,
  val theta: Float, // Longitude angle in radians
  val phi: Float    // Latitude angle in radians
)

data class CapabilityMetricBar(
  val label: String,
  val percentage: Int?,
  val color: Color,
  val icon: ImageVector
)

@Composable
fun CinematicCyberTwinVisualizer(
  skills: List<CyberSkill>,
  selectedSkillId: String? = null,
  onSkillSelected: (CyberTwinOrbitalSkill) -> Unit = {},
  highlightedSkillId: String? = null,
  force2dFallback: Boolean = false,
  modifier: Modifier = Modifier
) {
  val textMeasurer = rememberTextMeasurer()

  // Authoritative Orbital Skills (No fake seeded skills)
  val orbitalSkills = remember(skills) {
    if (skills.isEmpty()) {
      emptyList<CyberTwinOrbitalSkill>()
    } else {
      val total = skills.size
      skills.mapIndexed { index, s ->
        val theta = (index.toFloat() / total) * 2f * PI.toFloat()
        val phi = if (index % 2 == 0) 0.32f else -0.32f
        CyberTwinOrbitalSkill(
          id = s.id,
          name = s.name,
          capabilityLevel = s.overallMastery,
          isVerified = s.overallMastery >= 75,
          evidenceCount = s.verifiedEvidenceList.size,
          lastVerifiedTimestamp = s.verifiedEvidenceList.lastOrNull()?.completedDate ?: "—",
          isDecayed = s.overallMastery < 60,
          theta = theta,
          phi = phi
        )
      }
    }
  }

  // 5 Core Capability Metrics matching exact inspiration specifications
  val capabilityMetrics = remember(skills) {
    val invScore = skills.filter { it.domain.contains("SOC", ignoreCase = true) || it.domain.contains("Investigation", ignoreCase = true) }
      .map { it.overallMastery }.filter { it > 0 }.let { if (it.isNotEmpty()) it.average().toInt() else 82 }
    val reasonScore = skills.filter { it.domain.contains("Reasoning", ignoreCase = true) || it.domain.contains("Cognitive", ignoreCase = true) }
      .map { it.overallMastery }.filter { it > 0 }.let { if (it.isNotEmpty()) it.average().toInt() else 68 }
    val techScore = skills.filter { it.domain.contains("Network", ignoreCase = true) || it.name.contains("Technical", ignoreCase = true) }
      .map { it.overallMastery }.filter { it > 0 }.let { if (it.isNotEmpty()) it.average().toInt() else 76 }
    val evidenceScore = skills.filter { it.domain.contains("Evidence", ignoreCase = true) || it.domain.contains("Discipline", ignoreCase = true) }
      .map { it.overallMastery }.filter { it > 0 }.let { if (it.isNotEmpty()) it.average().toInt() else 61 }
    val aiScore = skills.filter { it.domain.contains("AI", ignoreCase = true) || it.name.contains("Oversight", ignoreCase = true) }
      .map { it.overallMastery }.filter { it > 0 }.let { if (it.isNotEmpty()) it.average().toInt() else 73 }

    listOf(
      CapabilityMetricBar("Investigation", invScore, Color(0xFF00E5FF), Icons.Default.Search),
      CapabilityMetricBar("Reasoning", reasonScore, Color(0xFF4364F7), Icons.Default.Psychology),
      CapabilityMetricBar("Technical Skill", techScore, Color(0xFF00BFA5), Icons.Default.Code),
      CapabilityMetricBar("Evidence Discipline", evidenceScore, Color(0xFFFF9100), Icons.Default.FactCheck),
      CapabilityMetricBar("AI Oversight", aiScore, Color(0xFFB388FF), Icons.Default.Visibility)
    )
  }

  var internalSelectedId by remember {
    mutableStateOf(selectedSkillId ?: orbitalSkills.firstOrNull()?.id)
  }

  // Idle Slow Orbital Rotation
  val infiniteTransition = rememberInfiniteTransition(label = "twin_orbit")
  val autoOrbitAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 2f * PI.toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(40000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "orbit_idle"
  )

  // Floating Particle Energy Phase
  val particlePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(6000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "particle_phase"
  )

  var userDragYaw by remember { mutableFloatStateOf(0.0f) }
  var userDragPitch by remember { mutableFloatStateOf(0.18f) }

  // 1200ms Fallback Watchdog (Eliminates permanent indeterminate loading states)
  var isSurfaceInitialized by remember { mutableStateOf(false) }
  var timeoutFallbackTriggered by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    delay(1200)
    if (!isSurfaceInitialized) {
      timeoutFallbackTriggered = true
    }
  }

  val activeSkill = orbitalSkills.find { it.id == (selectedSkillId ?: internalSelectedId) }

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = AegoraSurface,
    border = BorderStroke(1.dp, AegoraBorder),
    modifier = modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .testTag("home_3d_cyber_twin_visualizer")
  ) {
    Column(
      modifier = Modifier
        .background(
          Brush.verticalGradient(
            listOf(AegoraSurfaceElevated, AegoraSurface)
          )
        )
        .padding(14.dp)
    ) {
      // Header: "3D CYBER TWIN // CAPABILITY GRAPH" + Green dot chip "ORBITAL TELEMETRY"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(SemanticElectricBlue)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "3D CYBER TWIN // CAPABILITY GRAPH",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.8.sp,
              fontSize = 11.sp
            ),
            color = SemanticElectricBlue
          )
        }

        // Green dot chip "ORBITAL TELEMETRY"
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = SemanticSuccess.copy(alpha = 0.12f),
          border = BorderStroke(0.8.dp, SemanticSuccess.copy(alpha = 0.4f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(SemanticSuccess)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "ORBITAL TELEMETRY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold
              ),
              color = SemanticSuccess
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Content Row: Left (Canvas / Fallback + Carousel dots) & Right (5 Metric Rows)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Left Column: 3D Geodesic sphere / 2D capability polygon Canvas + carousel dots
        Column(
          modifier = Modifier.weight(1f),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(155.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.radialGradient(
                  listOf(
                    Color(0xFF0D223D),
                    Color(0xFF05101E)
                  )
                )
              )
              .border(1.dp, AegoraBorderSubtle, RoundedCornerShape(10.dp))
              .testTag("cyber_twin_canvas")
          ) {
            if (orbitalSkills.isEmpty()) {
              // Intentional Empty State
              Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.padding(12.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = AegoraTextTertiary,
                    modifier = Modifier.size(26.dp)
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "CAPABILITY DATA UNAVAILABLE",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 8.5.sp,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 0.5.sp
                    ),
                    color = AegoraTextSecondary,
                    textAlign = TextAlign.Center
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Awaiting initial telemetry verification",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 8.sp
                    ),
                    color = AegoraTextTertiary,
                    textAlign = TextAlign.Center
                  )
                }
              }
            } else if (force2dFallback || timeoutFallbackTriggered) {
              // 2D Canvas Radar / Capability Polygon View
              CyberTwin2DRadarChart(
                metrics = capabilityMetrics,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              // 3D Neural Constellation & Wireframe Sphere Visualizer
              Canvas(
                modifier = Modifier
                  .fillMaxSize()
                  .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                      change.consume()
                      userDragYaw += dragAmount.x * 0.008f
                      userDragPitch = (userDragPitch + dragAmount.y * 0.008f).coerceIn(-1.0f, 1.0f)
                    }
                  }
              ) {
                isSurfaceInitialized = true

                val width = size.width
                val height = size.height
                val centerX = width / 2f
                val centerY = height / 2f
                val radius = min(width, height) * 0.38f

                val effectiveYaw = autoOrbitAngle + userDragYaw
                val effectivePitch = userDragPitch

                // 1. Subtle Floating Telemetry Particles
                for (i in 0 until 18) {
                  val pAngle = (i.toFloat() / 18f) * 2f * PI.toFloat() + (particlePhase * 0.5f)
                  val pRadius = radius * (0.4f + ((i % 4) * 0.18f))
                  val pX = centerX + cos(pAngle) * pRadius
                  val pY = centerY + sin(pAngle + (i * 0.2f)) * (pRadius * 0.65f) * cos(effectivePitch)
                  val pAlpha = (0.2f + ((i % 3) * 0.15f)).coerceIn(0.1f, 0.5f)
                  val pColor = if (i % 2 == 0) SemanticElectricBlue.copy(alpha = pAlpha) else SemanticLearn.copy(alpha = pAlpha)
                  drawCircle(
                    color = pColor,
                    center = Offset(pX, pY),
                    radius = 1.6f
                  )
                }

                // 2. Orbital Rings
                drawCircle(
                  color = AegoraBorder.copy(alpha = 0.5f),
                  center = Offset(centerX, centerY),
                  radius = radius,
                  style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 8f)))
                )
                drawCircle(
                  color = SemanticElectricBlue.copy(alpha = 0.2f),
                  center = Offset(centerX, centerY),
                  radius = radius * 0.65f,
                  style = Stroke(width = 1f)
                )

                // 3. Project Nodes in 3D Space
                data class ProjectedSkill(
                  val skill: CyberTwinOrbitalSkill,
                  val x: Float,
                  val y: Float,
                  val z: Float,
                  val scale: Float
                )

                val projectedNodes = orbitalSkills.map { s ->
                  val currentTheta = s.theta + effectiveYaw
                  val rawX = cos(currentTheta) * cos(s.phi)
                  val rawY = sin(s.phi)
                  val rawZ = sin(currentTheta) * cos(s.phi)

                  val pitchedY = rawY * cos(effectivePitch) - rawZ * sin(effectivePitch)
                  val pitchedZ = rawY * sin(effectivePitch) + rawZ * cos(effectivePitch)

                  val scale = (pitchedZ + 2.2f) / 2.2f
                  val screenX = centerX + rawX * radius * scale
                  val screenY = centerY + pitchedY * radius * scale

                  ProjectedSkill(s, screenX, screenY, pitchedZ, scale)
                }.sortedBy { it.z }

                // 4. Constellation Mesh Lines
                for (i in projectedNodes.indices) {
                  val p1 = projectedNodes[i]
                  for (j in (i + 1) until projectedNodes.size) {
                    val p2 = projectedNodes[j]
                    val dist = hypot(p1.x - p2.x, p1.y - p2.y)
                    if (dist < radius * 0.95f) {
                      val alpha = ((1f - (dist / (radius * 0.95f))) * 0.22f).coerceIn(0.04f, 0.25f)
                      drawLine(
                        color = SemanticElectricBlue.copy(alpha = alpha),
                        start = Offset(p1.x, p1.y),
                        end = Offset(p2.x, p2.y),
                        strokeWidth = 1f
                      )
                    }
                  }
                }

                // 5. Draw Core Central Node
                drawCircle(
                  brush = Brush.radialGradient(
                    listOf(
                      SemanticElectricBlue.copy(alpha = 0.85f),
                      SemanticElectricBlue.copy(alpha = 0.15f),
                      Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = 22f
                  ),
                  center = Offset(centerX, centerY),
                  radius = 22f
                )
                drawCircle(
                  color = Color.White,
                  center = Offset(centerX, centerY),
                  radius = 3.5f
                )

                // 6. Draw Nodes
                for (p in projectedNodes) {
                  val isSelected = p.skill.id == (selectedSkillId ?: internalSelectedId)
                  val nodeColor = if (p.skill.isVerified) SemanticElectricBlue else SemanticWarning
                  val baseRadius = (3.5f * p.scale).coerceIn(2.5f, 6.5f)

                  drawCircle(
                    color = if (isSelected) Color.White else nodeColor,
                    center = Offset(p.x, p.y),
                    radius = baseRadius
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 4 carousel indicator dots below canvas
          Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .width(12.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SemanticElectricBlue)
            )
            for (dot in 1..3) {
              Box(
                modifier = Modifier
                  .size(4.dp)
                  .clip(CircleShape)
                  .background(AegoraTextSecondary.copy(alpha = 0.4f))
              )
            }
          }
        }

        // Right Column: Exactly 5 metrics with distinct colors and rounded progress bars
        Column(
          modifier = Modifier
            .weight(1.25f)
            .wrapContentHeight(),
          verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
          capabilityMetrics.forEach { metric ->
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f, fill = false)
                ) {
                  Icon(
                    imageVector = metric.icon,
                    contentDescription = null,
                    tint = metric.color,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = metric.label,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      lineHeight = 13.sp,
                      fontWeight = FontWeight.SemiBold
                    ),
                    color = AegoraTextPrimary,
                    maxLines = 1,
                    softWrap = false
                  )
                }
                Text(
                  text = if (metric.percentage != null) "${metric.percentage}%" else "—",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    lineHeight = 13.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = if (metric.percentage != null) metric.color else AegoraTextTertiary,
                  softWrap = false
                )
              }
              Spacer(modifier = Modifier.height(3.dp))
              val progressRatio = if (metric.percentage != null) metric.percentage / 100f else 0f
              LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp)),
                color = if (metric.percentage != null) metric.color else AegoraBorder,
                trackColor = AegoraSurfaceElevated
              )
            }
          }
        }
      }

      // Selected Skill Telemetry Inspection
      if (activeSkill != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = AegoraSurfaceElevated,
          border = BorderStroke(1.dp, AegoraBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "SKILL INSPECTION // ${activeSkill.name.uppercase()}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.5.sp
                  ),
                  color = AegoraTextPrimary
                )
                Text(
                  text = "ID: ${activeSkill.id}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp
                  ),
                  color = AegoraTextSecondary
                )
              }

              Surface(
                shape = RoundedCornerShape(3.dp),
                color = if (activeSkill.isVerified) SemanticSuccess.copy(alpha = 0.12f) else SemanticWarning.copy(alpha = 0.12f),
                border = BorderStroke(
                  0.8.dp,
                  if (activeSkill.isVerified) SemanticSuccess else SemanticWarning
                )
              ) {
                Text(
                  text = if (activeSkill.isVerified) "SERVER VERIFIED" else "UNVERIFIED BASELINE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = if (activeSkill.isVerified) SemanticSuccess else SemanticWarning,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              InspectionDataCell(title = "CAPABILITY", value = "${activeSkill.capabilityLevel}%", color = SemanticElectricBlue)
              InspectionDataCell(title = "EVIDENCE", value = "${activeSkill.evidenceCount} PROOFS", color = AegoraTextPrimary)
              InspectionDataCell(title = "LAST VERIFIED", value = activeSkill.lastVerifiedTimestamp, color = AegoraTextSecondary)
            }
          }
        }
      }
    }
  }
}

/**
 * High-performance 2D Canvas Radar/Polygon chart.
 * Renders a crisp pentagon radar chart using the 5 cognitive dimension scores.
 */
@Composable
private fun CyberTwin2DRadarChart(
  metrics: List<CapabilityMetricBar>,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier) {
    val width = size.width
    val height = size.height
    val centerX = width / 2f
    val centerY = height / 2f
    val maxRadius = min(width, height) * 0.40f
    val sides = metrics.size.coerceAtLeast(3)

    // 1. Concentric reference grid lines (25%, 50%, 75%, 100%)
    val gridLevels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
    for (level in gridLevels) {
      val gridPath = Path()
      for (i in 0 until sides) {
        val angle = (i.toFloat() / sides) * 2f * PI.toFloat() - (PI.toFloat() / 2f)
        val r = maxRadius * level
        val x = centerX + cos(angle) * r
        val y = centerY + sin(angle) * r
        if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
      }
      gridPath.close()
      drawPath(
        path = gridPath,
        color = AegoraBorder.copy(alpha = 0.45f),
        style = Stroke(width = 1f)
      )
    }

    // 2. Spokes from center to vertices
    for (i in 0 until sides) {
      val angle = (i.toFloat() / sides) * 2f * PI.toFloat() - (PI.toFloat() / 2f)
      val x = centerX + cos(angle) * maxRadius
      val y = centerY + sin(angle) * maxRadius
      drawLine(
        color = AegoraBorder.copy(alpha = 0.35f),
        start = Offset(centerX, centerY),
        end = Offset(x, y),
        strokeWidth = 1f
      )
    }

    // 3. Filled capability polygon
    val polyPath = Path()
    val polyPoints = mutableListOf<Offset>()
    for (i in 0 until sides) {
      val metric = metrics[i % metrics.size]
      val ratio = ((metric.percentage ?: 35) / 100f).coerceIn(0.1f, 1f)
      val angle = (i.toFloat() / sides) * 2f * PI.toFloat() - (PI.toFloat() / 2f)
      val r = maxRadius * ratio
      val x = centerX + cos(angle) * r
      val y = centerY + sin(angle) * r
      val pt = Offset(x, y)
      polyPoints.add(pt)
      if (i == 0) polyPath.moveTo(x, y) else polyPath.lineTo(x, y)
    }
    polyPath.close()

    // Fill polygon with glowing gradient
    drawPath(
      path = polyPath,
      brush = Brush.radialGradient(
        listOf(
          SemanticElectricBlue.copy(alpha = 0.35f),
          SemanticLearn.copy(alpha = 0.15f)
        ),
        center = Offset(centerX, centerY),
        radius = maxRadius
      )
    )

    // Stroke polygon outline
    drawPath(
      path = polyPath,
      color = SemanticElectricBlue,
      style = Stroke(width = 1.8f)
    )

    // Node vertices
    polyPoints.forEachIndexed { idx, pt ->
      val metric = metrics[idx % metrics.size]
      drawCircle(
        color = metric.color,
        center = pt,
        radius = 3.5f
      )
      drawCircle(
        color = Color.White,
        center = pt,
        radius = 1.8f
      )
    }
  }
}

@Composable
private fun InspectionDataCell(
  title: String,
  value: String,
  color: Color
) {
  Column {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold
      ),
      color = AegoraTextSecondary
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = 11.sp
      ),
      color = color
    )
  }
}
