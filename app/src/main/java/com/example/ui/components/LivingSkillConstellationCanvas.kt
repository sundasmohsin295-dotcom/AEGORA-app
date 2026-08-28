package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import com.example.data.AegoraRepository
import com.example.model.ConstellationNode
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun LivingSkillConstellationCanvas(
  nodes: List<ConstellationNode>,
  onLaunchDiagnostic: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var rotationYaw by remember { mutableFloatStateOf(0.4f) }
  var rotationPitch by remember { mutableFloatStateOf(0.2f) }
  var zoomScale by remember { mutableFloatStateOf(1.0f) }
  var selectedNodeId by remember { mutableStateOf<String?>(nodes.find { it.decayRiskLevel == "CRITICAL" }?.skillId ?: nodes.firstOrNull()?.skillId) }

  val textMeasurer = rememberTextMeasurer()
  val selectedNode = nodes.find { it.idOrSkill == selectedNodeId }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(CyberBackground)
  ) {
    // 1. Constellation Controls & Legend Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AllInclusive,
          contentDescription = null,
          tint = CyberCyan,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "3D GENERATIVE SKILL CONSTELLATION",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          ),
          color = CyberCyan
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ConstellationLegendDot(color = CyberEmerald, label = "Active (80%+)")
        ConstellationLegendDot(color = CyberAmber, label = "Medium")
        ConstellationLegendDot(color = NeonCrimson, label = "Decaying (<60%)")
      }
    }

    // 2. Main Interactive 3D Canvas Box
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(340.dp)
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(CyberSurfaceElevated.copy(alpha = 0.8f))
        .border(1.dp, CyberIndigo.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
        .testTag("living_constellation_canvas")
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
              change.consume()
              rotationYaw += dragAmount.x * 0.006f
              rotationPitch = (rotationPitch + dragAmount.y * 0.006f).coerceIn(-1.2f, 1.2f)
            }
          }
          .pointerInput(nodes, rotationYaw, rotationPitch, zoomScale) {
            detectTapGestures { tapOffset ->
              val width = size.width
              val height = size.height
              val centerX = width / 2f
              val centerY = height / 2f
              val baseRadius = min(width, height) * 0.38f * zoomScale

              // Find closest project node
              var closestNode: ConstellationNode? = null
              var closestDist = Float.MAX_VALUE

              for (node in nodes) {
                val proj = project3D(node.normalizedX, node.normalizedY, node.normalizedZ, rotationYaw, rotationPitch)
                val screenX = centerX + proj.x * baseRadius
                val screenY = centerY + proj.y * baseRadius
                val touchDist = hypot(tapOffset.x - screenX, tapOffset.y - screenY)

                val nodeRadius = (12f + (node.masteryPercent / 10f)) * proj.scale
                if (touchDist < nodeRadius + 24f && touchDist < closestDist) {
                  closestDist = touchDist
                  closestNode = node
                }
              }

              if (closestNode != null) {
                selectedNodeId = closestNode.idOrSkill
              }
            }
          }
      ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        val baseRadius = min(width, height) * 0.38f * zoomScale

        // Draw background orbital grid rings
        drawOrbitalGrid(centerX, centerY, baseRadius)

        // Project all nodes
        val projectedNodes = nodes.map { node ->
          val proj = project3D(node.normalizedX, node.normalizedY, node.normalizedZ, rotationYaw, rotationPitch)
          val screenX = centerX + proj.x * baseRadius
          val screenY = centerY + proj.y * baseRadius
          ProjectedNode(node, screenX, screenY, proj.z, proj.scale)
        }.sortedBy { it.z } // Render back to front for proper 3D depth

        // 1. Draw connection lines between prerequisite nodes
        val nodeMap = projectedNodes.associateBy { it.node.idOrSkill }
        for (projNode in projectedNodes) {
          for (connectedId in projNode.node.connectedSkillIds) {
            val target = nodeMap[connectedId]
            if (target != null) {
              val isSelectedEdge = projNode.node.idOrSkill == selectedNodeId || target.node.idOrSkill == selectedNodeId
              val alpha = if (isSelectedEdge) 0.85f else 0.25f * min(projNode.scale, target.scale)
              val edgeColor = when {
                isSelectedEdge -> CyberCyan
                projNode.node.retentionPercent < 60 || target.node.retentionPercent < 60 -> NeonCrimson.copy(alpha = alpha)
                else -> CyberBlue.copy(alpha = alpha)
              }
              drawLine(
                color = edgeColor,
                start = Offset(projNode.screenX, projNode.screenY),
                end = Offset(target.screenX, target.screenY),
                strokeWidth = if (isSelectedEdge) 2.5f else 1.2f,
                cap = StrokeCap.Round
              )
            }
          }
        }

        // 2. Draw 3D nodes & labels
        for (p in projectedNodes) {
          val node = p.node
          val isSelected = node.idOrSkill == selectedNodeId
          val nodeRadius = (8f + (node.masteryPercent * 0.1f)) * p.scale

          val primaryColor = when {
            node.retentionPercent >= 80 -> CyberEmerald
            node.retentionPercent >= 60 -> CyberAmber
            else -> NeonCrimson
          }

          // Outer Glow Aura for Decaying / Selected nodes
          if (isSelected || node.retentionPercent < 60) {
            drawCircle(
              brush = Brush.radialGradient(
                colors = listOf(primaryColor.copy(alpha = if (isSelected) 0.55f else 0.35f), Color.Transparent),
                center = Offset(p.screenX, p.screenY),
                radius = nodeRadius * (if (isSelected) 2.8f else 2.0f)
              ),
              center = Offset(p.screenX, p.screenY),
              radius = nodeRadius * (if (isSelected) 2.8f else 2.0f)
            )
          }

          // Node core circle
          drawCircle(
            color = CyberSurface,
            center = Offset(p.screenX, p.screenY),
            radius = nodeRadius
          )

          drawCircle(
            color = primaryColor,
            center = Offset(p.screenX, p.screenY),
            radius = nodeRadius * 0.75f
          )

          drawCircle(
            color = if (isSelected) Color.White else primaryColor.copy(alpha = 0.9f),
            center = Offset(p.screenX, p.screenY),
            radius = nodeRadius,
            style = Stroke(width = if (isSelected) 2.5f else 1.5f)
          )

          // Draw skill name label if near or selected
          if (p.z > -0.2f || isSelected) {
            val labelText = "${node.skillName.split(" ").take(2).joinToString(" ")} (${node.retentionPercent}%)"
            val textLayout = textMeasurer.measure(
              text = labelText,
              style = TextStyle(
                color = if (isSelected) CyberCyan else TextPrimaryDark.copy(alpha = 0.85f),
                fontSize = (9f * p.scale).coerceIn(8f, 12f).sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            )
            val labelX = p.screenX - (textLayout.size.width / 2f)
            val labelY = p.screenY + nodeRadius + 3f
            drawText(textLayout, topLeft = Offset(labelX, labelY))
          }
        }
      }

      // Quick gesture guide overlay
      Row(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = CyberBackground.copy(alpha = 0.7f),
          border = androidx.compose.foundation.BorderStroke(0.5.dp, CyberBorder)
        ) {
          Text(
            text = "↻ Drag to rotate sphere | Tap node to inspect",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = TextSecondaryDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 3. Selected Node Diagnostic HUD Drawer
    selectedNode?.let { node ->
      CyberCard(
        borderColor = if (node.retentionPercent < 60) NeonCrimson else CyberCyan,
        backgroundColor = CyberSurface,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp)
          .testTag("constellation_node_hud")
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = node.domain.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = CyberCyan
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = when (node.decayRiskLevel) {
                  "CRITICAL" -> NeonCrimson.copy(alpha = 0.2f)
                  "HIGH" -> CyberAmber.copy(alpha = 0.2f)
                  else -> CyberEmerald.copy(alpha = 0.2f)
                }
              ) {
                Text(
                  text = "Decay Risk: ${node.decayRiskLevel}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = when (node.decayRiskLevel) {
                    "CRITICAL" -> NeonCrimson
                    "HIGH" -> CyberAmber
                    else -> CyberEmerald
                  },
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = node.skillName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
          }

          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "${node.retentionPercent}%",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              color = when {
                node.retentionPercent >= 80 -> CyberEmerald
                node.retentionPercent >= 60 -> CyberAmber
                else -> NeonCrimson
              }
            )
            Text(
              text = "Live Retention",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
              color = TextSecondaryDark
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Retention telemetry grid
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TelemetryMiniMetric(
            label = "Last Practiced",
            value = if (node.lastPracticedDaysAgo == 0) "Today" else "${node.lastPracticedDaysAgo}d ago",
            modifier = Modifier.weight(1f)
          )
          TelemetryMiniMetric(
            label = "Mastery Score",
            value = "${node.masteryPercent}%",
            modifier = Modifier.weight(1f)
          )
          TelemetryMiniMetric(
            label = "Prereq Bonds",
            value = "${node.connectedSkillIds.size} Linked",
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = {
            AegoraRepository.recordSkillPracticed(node.idOrSkill)
            onLaunchDiagnostic(node.idOrSkill)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (node.retentionPercent < 60) NeonCrimson else CyberCyan
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = if (node.retentionPercent < 60) Color.White else Color.Black,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (node.recommendedDiagnosticTitle.isNotBlank())
              "Launch Drill: ${node.recommendedDiagnosticTitle}"
            else "Launch Targeted Skill Drill",
            color = if (node.retentionPercent < 60) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }
  }
}

@Composable
private fun ConstellationLegendDot(color: Color, label: String) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(color)
    )
    Spacer(modifier = Modifier.width(3.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
      color = TextSecondaryDark
    )
  }
}

@Composable
private fun TelemetryMiniMetric(
  label: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = CyberSurfaceElevated,
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = TextSecondaryDark
      )
      Text(
        text = value,
        style = MaterialTheme.typography.bodySmall.copy(
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        ),
        color = TextPrimaryDark
      )
    }
  }
}

private data class Projected3D(val x: Float, val y: Float, val z: Float, val scale: Float)
private data class ProjectedNode(val node: ConstellationNode, val screenX: Float, val screenY: Float, val z: Float, val scale: Float)

private fun project3D(x: Float, y: Float, z: Float, yaw: Float, pitch: Float): Projected3D {
  // Yaw rotation around Y axis
  val cosY = cos(yaw)
  val sinY = sin(yaw)
  val x1 = x * cosY - z * sinY
  val z1 = x * sinY + z * cosY

  // Pitch rotation around X axis
  val cosP = cos(pitch)
  val sinP = sin(pitch)
  val y2 = y * cosP - z1 * sinP
  val z2 = y * sinP + z1 * cosP

  // Perspective projection
  val scale = (1.0f / (1.0f - z2 * 0.35f)).coerceIn(0.6f, 1.6f)
  return Projected3D(x1 * scale, y2 * scale, z2, scale)
}

private fun DrawScope.drawOrbitalGrid(centerX: Float, centerY: Float, radius: Float) {
  drawCircle(
    color = CyberBorder.copy(alpha = 0.35f),
    center = Offset(centerX, centerY),
    radius = radius * 0.45f,
    style = Stroke(width = 0.8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
  )
  drawCircle(
    color = CyberBorder.copy(alpha = 0.3f),
    center = Offset(centerX, centerY),
    radius = radius * 0.85f,
    style = Stroke(width = 0.8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f)))
  )
}

private val ConstellationNode.idOrSkill: String
  get() = skillId.ifBlank { skillName }
