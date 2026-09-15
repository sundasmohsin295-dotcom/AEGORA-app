package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberTwinRadar3D
import com.example.ui.components.RadarAxisData
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CyberTwinRadarScreen(
  onNavigateBack: () -> Unit,
  onNavigateToProof: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var is2DFallback by remember { mutableStateOf(false) }

  // The 5 Core Dimension Axes
  val axes = listOf(
    Pair("Investigation", 0.82f),
    Pair("Reasoning", 0.60f),
    Pair("Technical Skill", 0.76f),
    Pair("Evidence Discipline", 0.61f),
    Pair("AI Oversight", 0.73f)
  )

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(SpecCanvasBg)
      .testTag("screen_cyber_twin_radar"),
    containerColor = SpecCanvasBg,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .background(SpecCanvasBg)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(SpecElevatedBg)
              .border(1.dp, SpecBorder, RoundedCornerShape(10.dp))
              .testTag("cyber_twin_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = SpecHeadingWhite
            )
          }
          Spacer(modifier = Modifier.width(14.dp))
          Text(
            text = "Cyber Twin",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SpecHeadingWhite,
            fontFamily = FontFamily.Monospace
          )
        }

        // Action: Verified Proof
        Button(
          onClick = onNavigateToProof,
          colors = ButtonDefaults.buttonColors(containerColor = SpecElevatedBg),
          border = BorderStroke(1.dp, SpecPrimaryBlue),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SpecPrimaryBlue, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("PROOF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SpecPrimaryBlue)
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Segmented Toggle: [ 3D View ] | [ 2D Fallback ]
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp)
          .testTag("cyber_twin_toggle"),
        color = SpecCardBg,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Row(
          modifier = Modifier.fillMaxSize().padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // 3D View Tab
          Surface(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .clip(RoundedCornerShape(8.dp))
              .clickable { is2DFallback = false },
            color = if (!is2DFallback) SpecPrimaryBlue else Color.Transparent,
            shape = RoundedCornerShape(8.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "3D View",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (!is2DFallback) Color.White else SpecSubtextSlate
              )
            }
          }

          // 2D Fallback Tab
          Surface(
            modifier = Modifier
              .weight(1f)
              .fillMaxHeight()
              .clip(RoundedCornerShape(8.dp))
              .clickable { is2DFallback = true },
            color = if (is2DFallback) SpecPrimaryBlue else Color.Transparent,
            shape = RoundedCornerShape(8.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "2D Fallback",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (is2DFallback) Color.White else SpecSubtextSlate
              )
            }
          }
        }
      }

      // Radar Canvas Container Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("cyber_twin_spider_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (is2DFallback) "5-AXIS RADAR // 2D TOPOLOGY" else "3D HOLOGRAM // ACTIVE SIMULATION",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecCyanHighlight
            )
            Surface(
              color = SpecEmeraldVerification.copy(alpha = 0.15f),
              shape = RoundedCornerShape(4.dp),
              border = BorderStroke(1.dp, SpecEmeraldVerification.copy(alpha = 0.4f))
            ) {
              Text(
                text = "CALIBRATED",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = SpecEmeraldVerification,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          // 5-Axis Chart Container (3D Isometric vs 2D Fallback)
          if (!is2DFallback) {
            CyberTwinRadar3D(
              axes = axes.map { RadarAxisData(it.first, it.second) },
              modifier = Modifier.fillMaxWidth().height(280.dp)
            )
          } else {
            Box(
              modifier = Modifier
                .size(280.dp)
                .testTag("cyber_twin_spider_canvas"),
              contentAlignment = Alignment.Center
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.minDimension / 2f) - 36.dp.toPx()
                val angleStep = (2 * Math.PI / axes.size).toFloat()

                // Draw concentric reference polygons (25%, 50%, 75%, 100%)
                for (level in 1..4) {
                  val levelRadius = radius * (level / 4f)
                  val polyPath = Path()
                  for (i in axes.indices) {
                    val angle = -Math.PI.toFloat() / 2f + i * angleStep
                    val px = center.x + levelRadius * cos(angle)
                    val py = center.y + levelRadius * sin(angle)
                    if (i == 0) polyPath.moveTo(px, py) else polyPath.lineTo(px, py)
                  }
                  polyPath.close()
                  drawPath(
                    path = polyPath,
                    color = SpecBorder.copy(alpha = 0.6f),
                    style = Stroke(width = 1.dp.toPx())
                  )
                }

                // Draw radial axis spokes
                for (i in axes.indices) {
                  val angle = -Math.PI.toFloat() / 2f + i * angleStep
                  val px = center.x + radius * cos(angle)
                  val py = center.y + radius * sin(angle)
                  drawLine(
                    color = SpecBorder,
                    start = center,
                    end = Offset(px, py),
                    strokeWidth = 1.dp.toPx()
                  )
                }

                // Draw the data polygon
                val dataPath = Path()
                val apexPoints = mutableListOf<Offset>()

                for (i in axes.indices) {
                  val angle = -Math.PI.toFloat() / 2f + i * angleStep
                  val value = axes[i].second
                  val px = center.x + (radius * value) * cos(angle)
                  val py = center.y + (radius * value) * sin(angle)
                  val point = Offset(px, py)
                  apexPoints.add(point)

                  if (i == 0) dataPath.moveTo(px, py) else dataPath.lineTo(px, py)
                }
                dataPath.close()

                // Fill with gradient cobalt / blue alpha
                drawPath(
                  path = dataPath,
                  color = SpecPrimaryBlue.copy(alpha = 0.28f)
                )

                // Stroke the data perimeter
                drawPath(
                  path = dataPath,
                  color = SpecPrimaryBlue,
                  style = Stroke(width = 2.dp.toPx())
                )

                // Glowing apex nodes
                for (point in apexPoints) {
                  // Outer glow circle
                  drawCircle(
                    color = SpecCyanHighlight.copy(alpha = 0.35f),
                    radius = 8.dp.toPx(),
                    center = point
                  )
                  // Inner bright node
                  drawCircle(
                    color = SpecCyanHighlight,
                    radius = 4.dp.toPx(),
                    center = point
                  )
                }
              }
            }
          }
        }
      }

      // Axis Breakdown List
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "TELEMETRY METRICS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = SpecSubtextSlate,
            letterSpacing = 1.sp
          )

          axes.forEach { (name, score) ->
            val percentage = (score * 100).toInt()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = name.uppercase(),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  color = SpecHeadingWhite
                )
                Text(
                  text = "$percentage%",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  color = SpecCyanHighlight
                )
              }
              LinearProgressIndicator(
                progress = { score },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = SpecPrimaryBlue,
                trackColor = SpecElevatedBg
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
