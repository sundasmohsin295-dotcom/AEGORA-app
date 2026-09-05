package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.model.CyberTwin60Snapshot
import com.example.model.DecayRiskLevel
import com.example.model.DimensionExplainabilityV12
import com.example.model.TwinDimensionV12
import com.example.ui.theme.*
import kotlin.math.*

/**
 * 3D Spatial Position mapped for a Cyber Twin 6.0 Dimension
 */
private data class Dimension3DCoord(
  val dimension: TwinDimensionV12,
  val x: Float,
  val y: Float,
  val z: Float,
  val isHubNode: Boolean = false
)

/**
 * Spatial coordinate layout organizing 27 dimensions into an interconnected
 * holographic geodesic cyber-twin lattice.
 */
private val DIMENSION_COORDINATES: List<Dimension3DCoord> = listOf(
  // Tier 1: Foundation & Core Technical (Lower hemisphere)
  Dimension3DCoord(TwinDimensionV12.KNOWLEDGE, -0.65f, 0.65f, -0.2f),
  Dimension3DCoord(TwinDimensionV12.PRACTICAL_ABILITY, 0.0f, 0.55f, 0.45f, isHubNode = true),
  Dimension3DCoord(TwinDimensionV12.TOOL_FLUENCY, 0.65f, 0.65f, -0.2f),
  Dimension3DCoord(TwinDimensionV12.RETENTION, -0.35f, 0.75f, 0.25f),
  Dimension3DCoord(TwinDimensionV12.LEARNING_VELOCITY, 0.35f, 0.75f, 0.25f),
  Dimension3DCoord(TwinDimensionV12.CONSISTENCY, 0.0f, 0.85f, -0.25f),

  // Tier 2: SOC Operations & Active Defense (Equatorial ring)
  Dimension3DCoord(TwinDimensionV12.INVESTIGATION, -0.75f, 0.05f, 0.35f, isHubNode = true),
  Dimension3DCoord(TwinDimensionV12.DETECTION, 0.75f, 0.05f, 0.35f, isHubNode = true),
  Dimension3DCoord(TwinDimensionV12.RESPONSE, -0.55f, -0.15f, 0.65f),
  Dimension3DCoord(TwinDimensionV12.RESEARCH, 0.55f, -0.15f, 0.65f),
  Dimension3DCoord(TwinDimensionV12.EVIDENCE_QUALITY, -0.85f, 0.2f, -0.25f),
  Dimension3DCoord(TwinDimensionV12.PRESSURE_PERFORMANCE, -0.45f, 0.3f, 0.65f),

  // Tier 3: Cognitive & Metacognition (Upper hemisphere)
  Dimension3DCoord(TwinDimensionV12.REASONING, -0.3f, -0.45f, 0.35f, isHubNode = true),
  Dimension3DCoord(TwinDimensionV12.DECISION_MAKING, 0.3f, -0.45f, 0.35f, isHubNode = true),
  Dimension3DCoord(TwinDimensionV12.PROBLEM_SOLVING, 0.0f, -0.35f, 0.75f),
  Dimension3DCoord(TwinDimensionV12.CONFIDENCE_CALIBRATION, -0.6f, -0.55f, -0.25f),
  Dimension3DCoord(TwinDimensionV12.MISTAKE_RESISTANCE, 0.6f, -0.55f, -0.25f),
  Dimension3DCoord(TwinDimensionV12.INDEPENDENCE, -0.2f, -0.25f, -0.7f),
  Dimension3DCoord(TwinDimensionV12.ADAPTABILITY, 0.2f, -0.25f, -0.7f),

  // Tier 4: Generalization & Apex Bridge
  Dimension3DCoord(TwinDimensionV12.TRANSFERABILITY, 0.0f, -0.8f, 0.2f, isHubNode = true),
  Dimension3DCoord(TwinDimensionV12.SYSTEMS_THINKING, -0.4f, -0.75f, 0.15f),
  Dimension3DCoord(TwinDimensionV12.THREAT_MODELING, 0.4f, -0.75f, 0.15f),

  // Tier 5: Professional & Strategic Leadership (Crown)
  Dimension3DCoord(TwinDimensionV12.COMMUNICATION, -0.65f, -0.15f, -0.55f),
  Dimension3DCoord(TwinDimensionV12.TECHNICAL_WRITING, 0.65f, -0.15f, -0.55f),
  Dimension3DCoord(TwinDimensionV12.COLLABORATION, -0.35f, -0.65f, -0.55f),
  Dimension3DCoord(TwinDimensionV12.LEADERSHIP_POTENTIAL, 0.35f, -0.65f, -0.55f),
  Dimension3DCoord(TwinDimensionV12.CAREER_READINESS, 0.0f, 0.1f, -0.85f, isHubNode = true)
)

/**
 * Meaningful capability dependencies connecting related dimensions in the Cyber Twin.
 */
private val DIMENSION_EDGES: List<Pair<TwinDimensionV12, TwinDimensionV12>> = listOf(
  TwinDimensionV12.KNOWLEDGE to TwinDimensionV12.PRACTICAL_ABILITY,
  TwinDimensionV12.PRACTICAL_ABILITY to TwinDimensionV12.TOOL_FLUENCY,
  TwinDimensionV12.PRACTICAL_ABILITY to TwinDimensionV12.INVESTIGATION,
  TwinDimensionV12.INVESTIGATION to TwinDimensionV12.DETECTION,
  TwinDimensionV12.DETECTION to TwinDimensionV12.RESPONSE,
  TwinDimensionV12.INVESTIGATION to TwinDimensionV12.EVIDENCE_QUALITY,
  TwinDimensionV12.RESPONSE to TwinDimensionV12.PRESSURE_PERFORMANCE,
  TwinDimensionV12.REASONING to TwinDimensionV12.DECISION_MAKING,
  TwinDimensionV12.REASONING to TwinDimensionV12.CONFIDENCE_CALIBRATION,
  TwinDimensionV12.DECISION_MAKING to TwinDimensionV12.MISTAKE_RESISTANCE,
  TwinDimensionV12.REASONING to TwinDimensionV12.PROBLEM_SOLVING,
  TwinDimensionV12.INVESTIGATION to TwinDimensionV12.TRANSFERABILITY,
  TwinDimensionV12.DETECTION to TwinDimensionV12.TRANSFERABILITY,
  TwinDimensionV12.PRACTICAL_ABILITY to TwinDimensionV12.TRANSFERABILITY,
  TwinDimensionV12.TRANSFERABILITY to TwinDimensionV12.SYSTEMS_THINKING,
  TwinDimensionV12.SYSTEMS_THINKING to TwinDimensionV12.THREAT_MODELING,
  TwinDimensionV12.COMMUNICATION to TwinDimensionV12.TECHNICAL_WRITING,
  TwinDimensionV12.COLLABORATION to TwinDimensionV12.LEADERSHIP_POTENTIAL,
  TwinDimensionV12.LEADERSHIP_POTENTIAL to TwinDimensionV12.CAREER_READINESS,
  TwinDimensionV12.RETENTION to TwinDimensionV12.CONSISTENCY,
  TwinDimensionV12.LEARNING_VELOCITY to TwinDimensionV12.ADAPTABILITY
)

private data class ProjectedDimension(
  val coord: Dimension3DCoord,
  val explainability: DimensionExplainabilityV12?,
  val screenX: Float,
  val screenY: Float,
  val zDepth: Float,
  val scale: Float
)

/**
 * World-class Interactive 3D Cyber Twin Component.
 *
 * Visual centerpiece of AEGORA:
 * - Real-time state connection: reflects 27 dynamic dimensions from CyberTwin60Snapshot.
 * - 3D Manipulation: interactive drag (pitch & yaw), controlled zoom, auto-orbit, reset camera.
 * - Semantic highlight: Transferability gate, weak dimensions, decay risk, mastery resonance.
 * - Node selection with inline Tactical Dimension Inspector.
 * - Fully accessible 2D Structured Matrix fallback.
 */
@Composable
fun CyberTwin3DView(
  snapshot: CyberTwin60Snapshot,
  onLaunchMissionForDimension: (TwinDimensionV12) -> Unit,
  modifier: Modifier = Modifier,
  initialSelectedDimension: TwinDimensionV12? = null
) {
  var is3DMode by remember { mutableStateOf(true) }
  var rotationYaw by remember { mutableFloatStateOf(0.4f) }
  var rotationPitch by remember { mutableFloatStateOf(-0.15f) }
  var zoomScale by remember { mutableFloatStateOf(1.0f) }
  var isAutoOrbitEnabled by remember { mutableStateOf(true) }

  // Detect lowest dimension or decay risk to recommend as initial focus
  val defaultDimension = remember(snapshot) {
    initialSelectedDimension
      ?: snapshot.dimensions.values.minByOrNull { it.currentState }?.dimension
      ?: TwinDimensionV12.TRANSFERABILITY
  }
  var selectedDimension by remember { mutableStateOf<TwinDimensionV12?>(defaultDimension) }

  // Continuous subtle cybernetic orbit when auto-orbit is enabled
  val infiniteTransition = rememberInfiniteTransition(label = "cyber_twin_orbit")
  val autoOrbitAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 28000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "auto_orbit_angle"
  )

  // Energy pulse for active or decaying nodes
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.35f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  val effectiveYaw = if (isAutoOrbitEnabled) rotationYaw + (autoOrbitAngle * 0.15f) else rotationYaw
  val textMeasurer = rememberTextMeasurer()

  val selectedExplainability = selectedDimension?.let { snapshot.dimensions[it] }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(CyberSurface)
      .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
      .testTag("cyber_twin_3d_container")
  ) {
    // 1. Top HUD Bar & View Controls
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CyberSurfaceElevated.copy(alpha = 0.6f))
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(NeonCyan)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "CYBER TWIN 6.0",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp
          ),
          color = NeonCyan
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          color = CyberCyan.copy(alpha = 0.15f),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "${snapshot.overallScore} MMR",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp
            ),
            color = CyberCyan,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }

      // Action Tool Buttons
      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Mode toggle: 3D Hologram vs 2D Matrix (Accessibility mandate)
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (is3DMode) CyberCyan.copy(alpha = 0.2f) else CyberSurfaceVariant,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (is3DMode) CyberCyan else CyberBorder
          ),
          modifier = Modifier
            .clickable { is3DMode = !is3DMode }
            .testTag("cyber_twin_mode_toggle")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = if (is3DMode) Icons.Default.ViewInAr else Icons.Default.GridOn,
              contentDescription = "Toggle 3D View",
              tint = if (is3DMode) CyberCyan else TextSecondaryDark,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (is3DMode) "3D HOLO" else "2D MATRIX",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              ),
              color = if (is3DMode) CyberCyan else TextSecondaryDark
            )
          }
        }

        if (is3DMode) {
          // Orbit Toggle Button
          IconButton(
            onClick = { isAutoOrbitEnabled = !isAutoOrbitEnabled },
            modifier = Modifier
              .size(28.dp)
              .testTag("cyber_twin_orbit_toggle")
          ) {
            Icon(
              imageVector = if (isAutoOrbitEnabled) Icons.Default.Pause else Icons.Default.RotateRight,
              contentDescription = "Toggle Orbit",
              tint = if (isAutoOrbitEnabled) NeonEmerald else TextSecondaryDark,
              modifier = Modifier.size(16.dp)
            )
          }

          // Reset Camera Button
          IconButton(
            onClick = {
              rotationYaw = 0.4f
              rotationPitch = -0.15f
              zoomScale = 1.0f
            },
            modifier = Modifier
              .size(28.dp)
              .testTag("cyber_twin_reset_camera")
          ) {
            Icon(
              imageVector = Icons.Default.CenterFocusStrong,
              contentDescription = "Reset Camera",
              tint = TextSecondaryDark,
              modifier = Modifier.size(16.dp)
            )
          }

          // Focus Bottleneck Button
          IconButton(
            onClick = {
              // Target Transferability or lowest score
              val bottleneck = snapshot.dimensions.values.minByOrNull { it.currentState }?.dimension
                ?: TwinDimensionV12.TRANSFERABILITY
              selectedDimension = bottleneck
              rotationYaw = 0.0f
              rotationPitch = 0.6f
              zoomScale = 1.25f
            },
            modifier = Modifier
              .size(28.dp)
              .testTag("cyber_twin_focus_bottleneck")
          ) {
            Icon(
              imageVector = Icons.Default.FilterCenterFocus,
              contentDescription = "Focus Bottleneck",
              tint = NeonCrimson,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    if (is3DMode) {
      // 2. Interactive 3D Holographic Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(310.dp)
          .background(CyberBackground)
          .testTag("cyber_twin_canvas_viewport")
      ) {
        Canvas(
          modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
              detectDragGestures { change, dragAmount ->
                change.consume()
                isAutoOrbitEnabled = false
                rotationYaw += dragAmount.x * 0.007f
                rotationPitch = (rotationPitch + dragAmount.y * 0.007f).coerceIn(-1.3f, 1.3f)
              }
            }
            .pointerInput(snapshot, effectiveYaw, rotationPitch, zoomScale) {
              detectTapGestures { tapOffset ->
                val width = size.width
                val height = size.height
                val centerX = width / 2f
                val centerY = height / 2f
                val baseRadius = min(width, height) * 0.38f * zoomScale

                // Find closest tapped node
                var closestDim: TwinDimensionV12? = null
                var minDistance = Float.MAX_VALUE

                for (coord in DIMENSION_COORDINATES) {
                  val proj = project3D(coord.x, coord.y, coord.z, effectiveYaw, rotationPitch)
                  val screenX = centerX + proj.x * baseRadius
                  val screenY = centerY + proj.y * baseRadius
                  val dist = hypot(tapOffset.x - screenX, tapOffset.y - screenY)

                  val hitRadius = (16f * proj.scale) + 20f
                  if (dist < hitRadius && dist < minDistance) {
                    minDistance = dist
                    closestDim = coord.dimension
                  }
                }

                if (closestDim != null) {
                  selectedDimension = closestDim
                }
              }
            }
        ) {
          val width = size.width
          val height = size.height
          val centerX = width / 2f
          val centerY = height / 2f
          val baseRadius = min(width, height) * 0.38f * zoomScale

          // 2a. Draw Ambient Cybernetic Coordinate Grid Rings
          drawHolographicLattice(centerX, centerY, baseRadius)

          // 2b. Project all 27 dimensions into 2D camera plane
          val projectedNodes = DIMENSION_COORDINATES.map { coord ->
            val proj = project3D(coord.x, coord.y, coord.z, effectiveYaw, rotationPitch)
            val screenX = centerX + proj.x * baseRadius
            val screenY = centerY + proj.y * baseRadius
            val explainability = snapshot.dimensions[coord.dimension]
            ProjectedDimension(coord, explainability, screenX, screenY, proj.z, proj.scale)
          }.sortedBy { it.zDepth } // Render back-to-front for proper depth ordering

          val projectedMap = projectedNodes.associateBy { it.coord.dimension }

          // 2c. Draw Interconnecting Cybernetic Capability Tethers
          for ((dimA, dimB) in DIMENSION_EDGES) {
            val nodeA = projectedMap[dimA]
            val nodeB = projectedMap[dimB]
            if (nodeA != null && nodeB != null) {
              val isSelectedTether = dimA == selectedDimension || dimB == selectedDimension
              val isTransferTether = dimA == TwinDimensionV12.TRANSFERABILITY || dimB == TwinDimensionV12.TRANSFERABILITY

              val baseAlpha = if (isSelectedTether) 0.9f else 0.22f * min(nodeA.scale, nodeB.scale)
              val tetherColor = when {
                isSelectedTether -> CyberCyan
                isTransferTether && (nodeA.explainability?.currentState ?: 100) < 75 -> NeonCrimson.copy(alpha = baseAlpha)
                else -> CyberBlue.copy(alpha = baseAlpha)
              }

              drawLine(
                color = tetherColor,
                start = Offset(nodeA.screenX, nodeA.screenY),
                end = Offset(nodeB.screenX, nodeB.screenY),
                strokeWidth = if (isSelectedTether) 2.2f else 1.0f,
                cap = StrokeCap.Round
              )
            }
          }

          // 2d. Render 3D Projected Dimension Nodes
          for (node in projectedNodes) {
            val dim = node.coord.dimension
            val isSelected = dim == selectedDimension
            val score = node.explainability?.currentState ?: 70
            val decayRisk = node.explainability?.decayRisk ?: DecayRiskLevel.LOW
            val isWeakTransfer = dim == TwinDimensionV12.TRANSFERABILITY && score < 75

            val nodeColor = when {
              isWeakTransfer || decayRisk == DecayRiskLevel.HIGH || score < 65 -> NeonCrimson
              score >= 82 -> CyberEmerald
              score >= 75 -> CyberCyan
              else -> CyberAmber
            }

            val baseRadiusPx = if (node.coord.isHubNode) 7.5f else 5.5f
            val nodeRadius = baseRadiusPx * node.scale * if (isSelected) 1.45f else 1.0f
            val nodeCenter = Offset(node.screenX, node.screenY)

            // Outer Aura for selected or alerting nodes
            if (isSelected || isWeakTransfer || decayRisk == DecayRiskLevel.HIGH) {
              val auraRadius = nodeRadius * (1.8f + (0.3f * pulseAlpha))
              drawCircle(
                color = nodeColor.copy(alpha = 0.25f * pulseAlpha),
                radius = auraRadius,
                center = nodeCenter
              )
              drawCircle(
                color = nodeColor.copy(alpha = 0.7f),
                radius = auraRadius,
                center = nodeCenter,
                style = Stroke(width = 1.2f)
              )
            }

            // Core Solid Node
            drawCircle(
              color = if (isSelected) Color.White else nodeColor,
              radius = nodeRadius,
              center = nodeCenter
            )

            // Inner Ring for Depth
            drawCircle(
              color = Color.Black.copy(alpha = 0.4f),
              radius = nodeRadius * 0.45f,
              center = nodeCenter
            )

            // Node Label for Primary / Selected Nodes
            if (isSelected || node.coord.isHubNode || node.scale > 0.95f) {
              val labelText = if (isSelected) {
                "${dim.displayName.take(14).uppercase()} [$score]"
              } else if (node.coord.isHubNode) {
                dim.displayName.take(10).uppercase()
              } else {
                ""
              }

              if (labelText.isNotEmpty()) {
                val measured = textMeasurer.measure(
                  text = labelText,
                  style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    fontSize = if (isSelected) 9.sp else 7.5.sp,
                    color = if (isSelected) Color.White else TextSecondaryDark.copy(alpha = 0.85f * node.scale)
                  )
                )
                drawText(
                  textLayoutResult = measured,
                  topLeft = Offset(
                    node.screenX - (measured.size.width / 2f),
                    node.screenY + nodeRadius + 4f
                  )
                )
              }
            }
          }
        }

        // Overlay 3D Zoom Controls
        Row(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(10.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = CyberSurfaceElevated.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.size(28.dp).clickable {
              zoomScale = (zoomScale * 1.15f).coerceAtMost(2.0f)
            }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = TextPrimaryDark, modifier = Modifier.size(16.dp))
            }
          }
          Surface(
            shape = CircleShape,
            color = CyberSurfaceElevated.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier.size(28.dp).clickable {
              zoomScale = (zoomScale / 1.15f).coerceAtLeast(0.7f)
            }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = TextPrimaryDark, modifier = Modifier.size(16.dp))
            }
          }
        }
      }
    } else {
      // 2b. Accessible 2D Matrix Fallback (Full accessibility & screen-reader compliance)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(310.dp)
          .background(CyberBackground)
          .padding(8.dp)
          .testTag("cyber_twin_2d_matrix")
      ) {
        Text(
          text = "CAPABILITY DIMENSION MATRIX (27 DIMENSIONS)",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          ),
          color = TextSecondaryDark,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          items(snapshot.dimensions.values.toList().sortedBy { it.currentState }) { dim ->
            val isSelected = dim.dimension == selectedDimension
            val dimColor = when {
              dim.dimension == TwinDimensionV12.TRANSFERABILITY && dim.currentState < 75 -> NeonCrimson
              dim.currentState >= 82 -> CyberEmerald
              dim.currentState >= 75 -> CyberCyan
              else -> CyberAmber
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) CyberSurfaceElevated else CyberSurface,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) CyberCyan else CyberBorder
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedDimension = dim.dimension }
                .testTag("matrix_row_${dim.dimension.name.lowercase()}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = dim.dimension.displayName,
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                      ),
                      color = if (isSelected) TextPrimaryDark else TextSecondaryDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(3.dp),
                      color = CyberBorder
                    ) {
                      Text(
                        text = dim.dimension.category,
                        fontSize = 8.sp,
                        color = TextTertiaryDark,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                  Text(
                    text = dim.trend,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = dimColor
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "${dim.currentState}",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black
                    ),
                    color = dimColor
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextTertiaryDark,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // 3. Tactical Dimension Inspector (Contextual details for selected node)
    selectedExplainability?.let { explain ->
      TacticalDimensionInspector(
        explain = explain,
        onLaunchMission = { onLaunchMissionForDimension(explain.dimension) }
      )
    }
  }
}

/**
 * Tactical Inspector drawer revealing verified facts, evidence age, strengths,
 * weaknesses, and immediate mission action for the selected Cyber Twin dimension.
 */
@Composable
private fun TacticalDimensionInspector(
  explain: DimensionExplainabilityV12,
  onLaunchMission: () -> Unit
) {
  val scoreColor = when {
    explain.dimension == TwinDimensionV12.TRANSFERABILITY && explain.currentState < 75 -> NeonCrimson
    explain.currentState >= 82 -> CyberEmerald
    explain.currentState >= 75 -> CyberCyan
    else -> CyberAmber
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(CyberSurfaceElevated)
      .border(androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle))
      .padding(14.dp)
      .testTag("tactical_dimension_inspector")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = explain.dimension.displayName.uppercase(),
            style = MaterialTheme.typography.titleSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            ),
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = scoreColor.copy(alpha = 0.15f)
          ) {
            Text(
              text = explain.dimension.category.uppercase(),
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              ),
              color = scoreColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
        Text(
          text = explain.recentPerformance,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
          color = TextSecondaryDark,
          modifier = Modifier.padding(top = 2.dp)
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${explain.currentState}",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black
          ),
          color = scoreColor
        )
        Text(
          text = explain.trend,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
          color = if (explain.trend.startsWith("+")) CyberEmerald else NeonCrimson
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Evidence Quality & Calibration Badges
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      InspectorMetricChip(
        label = "EVIDENCE",
        value = "${explain.evidenceCount} verified",
        color = CyberCyan,
        modifier = Modifier.weight(1f)
      )
      InspectorMetricChip(
        label = "QUALITY",
        value = "${explain.evidenceQuality}%",
        color = CyberEmerald,
        modifier = Modifier.weight(1f)
      )
      InspectorMetricChip(
        label = "AGE",
        value = "${explain.evidenceAgeDays}d ago",
        color = if (explain.evidenceAgeDays > 10) CyberAmber else TextSecondaryDark,
        modifier = Modifier.weight(1f)
      )
      InspectorMetricChip(
        label = "CONFIDENCE",
        value = "${explain.confidence}%",
        color = CyberCyan,
        modifier = Modifier.weight(1f)
      )
    }

    // Weakness & Transferability Callout
    if (explain.weaknesses.isNotEmpty() || explain.dimension == TwinDimensionV12.TRANSFERABILITY) {
      Spacer(modifier = Modifier.height(8.dp))
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = CyberSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
      ) {
        Column(modifier = Modifier.padding(8.dp)) {
          if (explain.dimension == TwinDimensionV12.TRANSFERABILITY && explain.currentState < 75) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = NeonCrimson, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "PRIMARY BOTTLENECK: TRANSFERABILITY GATE LIMITING",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp,
                  fontFamily = FontFamily.Monospace
                ),
                color = NeonCrimson
              )
            }
            Text(
              text = explain.transferability,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
              color = TextSecondaryDark,
              modifier = Modifier.padding(top = 2.dp)
            )
          } else if (explain.weaknesses.isNotEmpty()) {
            Text(
              text = "Known Gap: ${explain.weaknesses.first()}",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
              color = CyberAmber
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Recommended Action & 1-Tap Mission Launch
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
        Text(
          text = "RECOMMENDED ACTION",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          ),
          color = TextTertiaryDark
        )
        Text(
          text = explain.recommendedAction,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = TextPrimaryDark
        )
      }

      Button(
        onClick = onLaunchMission,
        colors = ButtonDefaults.buttonColors(containerColor = scoreColor),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("inspector_launch_mission_btn")
      ) {
        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Launch Drill",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 11.sp
          ),
          color = Color.Black
        )
      }
    }
  }
}

@Composable
private fun InspectorMetricChip(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = CyberSurfaceVariant,
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 8.sp,
          fontWeight = FontWeight.Bold
        ),
        color = TextTertiaryDark
      )
      Text(
        text = value,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp,
          fontWeight = FontWeight.Black
        ),
        color = color
      )
    }
  }
}

/**
 * 3D Orthographic / Perspective Projection
 */
private data class ProjectedPoint(val x: Float, val y: Float, val z: Float, val scale: Float)

private fun project3D(x: Float, y: Float, z: Float, yaw: Float, pitch: Float): ProjectedPoint {
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

  // Perspective scaling
  val cameraDistance = 3.2f
  val perspective = cameraDistance / (cameraDistance + z2)

  return ProjectedPoint(
    x = x1 * perspective,
    y = y2 * perspective,
    z = z2,
    scale = perspective
  )
}

/**
 * Draw background holographic coordinates, latitude rings, and core axis
 */
private fun DrawScope.drawHolographicLattice(centerX: Float, centerY: Float, baseRadius: Float) {
  val gridColor = CyberIndigo.copy(alpha = 0.14f)

  // Outer Geodesic boundary
  drawCircle(
    color = gridColor,
    radius = baseRadius * 1.05f,
    center = Offset(centerX, centerY),
    style = Stroke(width = 1.0f)
  )

  // Middle Ring
  drawCircle(
    color = gridColor.copy(alpha = 0.08f),
    radius = baseRadius * 0.7f,
    center = Offset(centerX, centerY),
    style = Stroke(width = 1.0f)
  )

  // Equatorial axis
  drawLine(
    color = gridColor,
    start = Offset(centerX - baseRadius * 1.05f, centerY),
    end = Offset(centerX + baseRadius * 1.05f, centerY),
    strokeWidth = 0.8f
  )

  // Vertical axis
  drawLine(
    color = gridColor,
    start = Offset(centerX, centerY - baseRadius * 1.05f),
    end = Offset(centerX, centerY + baseRadius * 1.05f),
    strokeWidth = 0.8f
  )
}
