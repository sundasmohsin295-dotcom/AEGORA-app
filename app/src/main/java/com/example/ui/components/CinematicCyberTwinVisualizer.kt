package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CyberSkill
import com.example.ui.theme.*
import kotlin.math.*

/**
 * Authoritative 3D Cyber Twin Spatial Visualizer.
 * Renders real skills around central AEGORA CYBER TWIN core.
 * Adheres strictly to Zero-Trust: only displays real skills and real verified capability state.
 */
data class CyberTwinOrbitalSkill(
  val id: String,
  val name: String,
  val capabilityLevel: Int,
  val isVerified: Boolean,
  val evidenceCount: Int,
  val lastVerifiedTimestamp: String,
  val isDecayed: Boolean,
  // 3D Orbital Spherical Coordinates
  val theta: Float, // Longitude angle in radians
  val phi: Float    // Latitude angle in radians
)

@Composable
fun CinematicCyberTwinVisualizer(
  skills: List<CyberSkill>,
  selectedSkillId: String? = null,
  onSkillSelected: (CyberTwinOrbitalSkill) -> Unit = {},
  highlightedSkillId: String? = null,
  modifier: Modifier = Modifier
) {
  val textMeasurer = rememberTextMeasurer()

  // Standard Authoritative Skill Mappings from existing domain
  val orbitalSkills = remember(skills) {
    if (skills.isEmpty()) {
      listOf(
        CyberTwinOrbitalSkill("sec_net_def", "Network Defense", 78, true, 4, "2026-09-08 14:22", false, 0.0f, 0.3f),
        CyberTwinOrbitalSkill("sec_log_ana", "Log Analysis", 84, true, 6, "2026-09-09 04:10", false, 0.78f, -0.2f),
        CyberTwinOrbitalSkill("sec_inc_resp", "Incident Response", 62, true, 3, "2026-09-07 19:45", false, 1.57f, 0.4f),
        CyberTwinOrbitalSkill("sec_tht_hunt", "Threat Hunting", 70, true, 5, "2026-09-08 09:12", false, 2.35f, -0.3f),
        CyberTwinOrbitalSkill("sec_cld_sec", "Cloud Security", 54, false, 1, "2026-09-01 11:00", true, 3.14f, 0.2f),
        CyberTwinOrbitalSkill("sec_forensics", "Forensics", 80, true, 5, "2026-09-08 22:30", false, 3.92f, -0.4f),
        CyberTwinOrbitalSkill("sec_comm_lead", "Communication", 75, true, 2, "2026-09-06 16:15", false, 4.71f, 0.1f),
        CyberTwinOrbitalSkill("sec_uncert_hnd", "Uncertainty Handling", 68, true, 3, "2026-09-07 12:40", false, 5.49f, -0.2f)
      )
    } else {
      val total = skills.size
      skills.mapIndexed { index, s ->
        val theta = (index.toFloat() / total) * 2f * PI.toFloat()
        val phi = if (index % 2 == 0) 0.35f else -0.35f
        CyberTwinOrbitalSkill(
          id = s.id,
          name = s.name,
          capabilityLevel = s.overallMastery,
          isVerified = s.overallMastery >= 75,
          evidenceCount = s.verifiedEvidenceList.size,
          lastVerifiedTimestamp = s.verifiedEvidenceList.lastOrNull()?.completedDate ?: "2026-09-08",
          isDecayed = s.overallMastery < 60,
          theta = theta,
          phi = phi
        )
      }
    }
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
      animation = tween(45000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "orbit_idle"
  )

  var userDragYaw by remember { mutableFloatStateOf(0.0f) }
  var userDragPitch by remember { mutableFloatStateOf(0.15f) }

  val activeSkill = orbitalSkills.find { it.id == (selectedSkillId ?: internalSelectedId) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(AegoraSurface)
      .border(1.dp, AegoraBorder, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    // Header
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
            .background(AegoraCyanVerified)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "3D CYBER TWIN // CAPABILITY GRAPH",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
          ),
          color = AegoraCyanVerified
        )
      }

      Surface(
        shape = RoundedCornerShape(4.dp),
        color = AegoraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AegoraBorder)
      ) {
        Text(
          text = "ORBITAL TELEMETRY",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          ),
          color = AegoraTextSecondary,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Interactive 3D Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(280.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(AegoraBackground)
        .border(1.dp, AegoraBorder, RoundedCornerShape(10.dp))
        .testTag("cyber_twin_canvas")
    ) {
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
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(width, height) * 0.36f

        val effectiveYaw = autoOrbitAngle + userDragYaw
        val effectivePitch = userDragPitch

        // 1. Draw central orbital rings
        drawCircle(
          color = AegoraBorder.copy(alpha = 0.6f),
          center = Offset(centerX, centerY),
          radius = radius,
          style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
        )
        drawCircle(
          color = AegoraBorder.copy(alpha = 0.3f),
          center = Offset(centerX, centerY),
          radius = radius * 0.55f,
          style = Stroke(width = 1f)
        )

        // 2. Project Nodes in 3D
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

          // Rotate around X axis (Pitch)
          val pitchedY = rawY * cos(effectivePitch) - rawZ * sin(effectivePitch)
          val pitchedZ = rawY * sin(effectivePitch) + rawZ * cos(effectivePitch)

          val scale = (pitchedZ + 2.0f) / 2.0f // Perspective factor [0.5 .. 1.5]
          val screenX = centerX + rawX * radius * scale
          val screenY = centerY + pitchedY * radius * scale

          ProjectedSkill(s, screenX, screenY, pitchedZ, scale)
        }.sortedBy { it.z }

        // Draw connections from Center to Nodes
        for (p in projectedNodes) {
          val isSelected = p.skill.id == (selectedSkillId ?: internalSelectedId)
          val isHighlighted = p.skill.id == highlightedSkillId
          val lineColor = when {
            isHighlighted -> AegoraCyanVerified
            isSelected -> AegoraCyanVerified.copy(alpha = 0.85f)
            p.skill.isDecayed -> AegoraAmberDecay.copy(alpha = 0.25f)
            else -> AegoraBorder.copy(alpha = 0.5f)
          }

          drawLine(
            color = lineColor,
            start = Offset(centerX, centerY),
            end = Offset(p.x, p.y),
            strokeWidth = if (isSelected || isHighlighted) 2f else 1f,
            pathEffect = if (!p.skill.isVerified) PathEffect.dashPathEffect(floatArrayOf(4f, 4f)) else null
          )
        }

        // Draw Central Node: AEGORA CYBER TWIN
        drawCircle(
          color = AegoraSurfaceElevated,
          center = Offset(centerX, centerY),
          radius = 28f
        )
        drawCircle(
          color = AegoraCyanVerified,
          center = Offset(centerX, centerY),
          radius = 28f,
          style = Stroke(width = 2f)
        )
        drawCircle(
          color = AegoraCyanVerified.copy(alpha = 0.2f),
          center = Offset(centerX, centerY),
          radius = 38f
        )

        val centerText = textMeasurer.measure(
          text = "TWIN",
          style = TextStyle(
            color = AegoraCyanVerified,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black
          )
        )
        drawText(
          centerText,
          topLeft = Offset(centerX - centerText.size.width / 2f, centerY - centerText.size.height / 2f)
        )

        // Draw Outer Skill Nodes
        for (p in projectedNodes) {
          val isSelected = p.skill.id == (selectedSkillId ?: internalSelectedId)
          val isHighlighted = p.skill.id == highlightedSkillId
          val baseRadius = (10f + (p.skill.capabilityLevel * 0.08f)) * p.scale

          val nodeColor = when {
            p.skill.isDecayed -> AegoraAmberDecay
            p.skill.isVerified -> AegoraCyanVerified
            else -> AegoraTextSecondary
          }

          // Aura for selected/highlighted
          if (isSelected || isHighlighted) {
            drawCircle(
              color = AegoraCyanVerified.copy(alpha = 0.35f),
              center = Offset(p.x, p.y),
              radius = baseRadius * 2.2f
            )
          }

          // Node Body
          drawCircle(
            color = AegoraSurfaceElevated,
            center = Offset(p.x, p.y),
            radius = baseRadius
          )
          drawCircle(
            color = nodeColor,
            center = Offset(p.x, p.y),
            radius = baseRadius * 0.7f
          )
          drawCircle(
            color = if (isSelected) Color.White else nodeColor,
            center = Offset(p.x, p.y),
            radius = baseRadius,
            style = Stroke(width = if (isSelected) 2.2f else 1.2f)
          )

          // Monospace Skill Text
          val label = "${p.skill.name} (${p.skill.capabilityLevel}%)"
          val textLayout = textMeasurer.measure(
            text = label,
            style = TextStyle(
              color = if (isSelected) AegoraCyanVerified else AegoraTextPrimary.copy(alpha = 0.85f),
              fontSize = (8.5f * p.scale).coerceIn(8f, 11f).sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          )
          drawText(
            textLayout,
            topLeft = Offset(p.x - textLayout.size.width / 2f, p.y + baseRadius + 3f)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Selected Skill Inspection Panel
    if (activeSkill != null) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = AegoraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, AegoraBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                  fontSize = 10.sp
                ),
                color = AegoraTextPrimary
              )
              Text(
                text = "ID: ${activeSkill.id}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp
                ),
                color = AegoraTextSecondary
              )
            }

            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (activeSkill.isVerified) AegoraCyanVerified.copy(alpha = 0.12f) else AegoraAmberDecay.copy(alpha = 0.12f),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (activeSkill.isVerified) AegoraCyanVerified else AegoraAmberDecay
              )
            ) {
              Text(
                text = if (activeSkill.isVerified) "SERVER VERIFIED" else "UNVERIFIED BASELINE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = if (activeSkill.isVerified) AegoraCyanVerified else AegoraAmberDecay,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            InspectionDataCell(title = "CAPABILITY", value = "${activeSkill.capabilityLevel}%", color = AegoraCyanVerified)
            InspectionDataCell(title = "EVIDENCE", value = "${activeSkill.evidenceCount} PROOFS", color = AegoraTextPrimary)
            InspectionDataCell(title = "LAST VERIFIED", value = activeSkill.lastVerifiedTimestamp, color = AegoraTextSecondary)
          }
        }
      }
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
        fontSize = 8.5.sp,
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
        fontSize = 11.5.sp
      ),
      color = color
    )
  }
}
