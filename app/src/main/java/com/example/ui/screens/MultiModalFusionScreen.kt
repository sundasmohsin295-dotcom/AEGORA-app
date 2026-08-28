package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.FusionEvidenceNode
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiModalFusionScreen(
  onNavigateBack: () -> Unit,
  onAskAi: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val fusionState by AegoraRepository.multiModalFusionSession.collectAsState()
  var isAudioPlaying by remember { mutableStateOf(false) }
  var audioPlaybackIndex by remember { mutableIntStateOf(0) }

  // Auto-advance simulated audio briefing stream
  LaunchedEffect(isAudioPlaying) {
    if (isAudioPlaying) {
      while (isAudioPlaying && audioPlaybackIndex < fusionState.evidenceNodes.size) {
        val currentNode = fusionState.evidenceNodes[audioPlaybackIndex]
        AegoraRepository.selectFusionEvidenceNode(currentNode.id)
        delay(3500)
        audioPlaybackIndex = (audioPlaybackIndex + 1) % fusionState.evidenceNodes.size
      }
    }
  }

  val selectedNode = fusionState.evidenceNodes.find { it.id == fusionState.selectedNodeId }
    ?: fusionState.evidenceNodes.firstOrNull()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(CyberCyan.copy(alpha = 0.15f))
                .border(1.dp, CyberCyan, RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.GraphicEq, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                "MULTIMODAL FUSION ENGINE",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = TextPrimaryDark
              )
              Text(
                "Synchronized Visual Topology & Live Audio Stream",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = CyberCyan
              )
            }
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDarkSlate)
      )
    },
    containerColor = CyberBackground
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Session Overview Card
      item {
        CyberCard(
          borderColor = CyberCyan.copy(alpha = 0.6f),
          backgroundColor = CyberSurface
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "SCENARIO • ${fusionState.targetRole}",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
              )
              Text(
                text = fusionState.scenarioTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = fusionState.attackChainSummary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )
            }

            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberEmerald.copy(alpha = 0.15f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
            ) {
              Text(
                text = "LIVE SYNC",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }

      // 2. Interactive Topology Graph (Canvas-based Visual Sync)
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = CyberDarkSlate,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("multimodal_topology_canvas")
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "EVIDENCE TOPOLOGY GRAPH",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberCyan
              )
              Text(
                text = "Tap node to sync audio transcript",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextSecondaryDark
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Canvas node map
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CyberSurface)
                .border(0.5.dp, CyberBorder, RoundedCornerShape(8.dp))
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Base coordinate mapping
                val n1 = Offset(w * 0.2f, h * 0.35f)
                val n2 = Offset(w * 0.5f, h * 0.25f)
                val n3 = Offset(w * 0.8f, h * 0.45f)
                val n4 = Offset(w * 0.5f, h * 0.75f)

                val nodeOffsets = mapOf(
                  "fev_01" to n1,
                  "fev_02" to n2,
                  "fev_03" to n3,
                  "fev_04" to n4
                )

                // Edges
                drawLine(color = CyberBorder, start = n1, end = n2, strokeWidth = 2f)
                drawLine(color = CyberBorder, start = n2, end = n3, strokeWidth = 2f)
                drawLine(color = CyberBorder, start = n2, end = n4, strokeWidth = 2f)

                // Render Nodes
                fusionState.evidenceNodes.forEach { node ->
                  val pos = nodeOffsets[node.id] ?: Offset(w * node.visualCoordinates.first, h * node.visualCoordinates.second)
                  val isSelected = node.id == fusionState.selectedNodeId
                  val nodeColor = when {
                    node.isFlaggedSuspicious -> NeonCrimson
                    isSelected -> CyberCyan
                    else -> CyberEmerald
                  }

                  if (isSelected || node.isCurrentlyDiscussedInAudio) {
                    drawCircle(
                      color = nodeColor.copy(alpha = 0.35f),
                      center = pos,
                      radius = 26f
                    )
                  }

                  drawCircle(
                    color = CyberSurfaceElevated,
                    center = pos,
                    radius = 16f
                  )
                  drawCircle(
                    color = nodeColor,
                    center = pos,
                    radius = 12f
                  )
                  drawCircle(
                    color = if (isSelected) Color.White else nodeColor,
                    center = pos,
                    radius = 16f,
                    style = Stroke(width = if (isSelected) 3f else 1.5f)
                  )
                }
              }

              // Clickable Node Selector Buttons at bottom of canvas
              Row(
                modifier = Modifier
                  .align(Alignment.BottomCenter)
                  .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                fusionState.evidenceNodes.forEach { node ->
                  val isSelected = node.id == fusionState.selectedNodeId
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isSelected) CyberCyan.copy(alpha = 0.25f) else CyberSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyberCyan else CyberBorder),
                    modifier = Modifier.clickable {
                      AegoraRepository.selectFusionEvidenceNode(node.id)
                    }
                  ) {
                    Text(
                      text = node.category.split("_").first(),
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                      color = if (isSelected) CyberCyan else TextPrimaryDark,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 3. Synchronized Voice Narration & Live Transcript Stream
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = CyberSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { isAudioPlaying = !isAudioPlaying },
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CyberCyan)
                ) {
                  Icon(
                    imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Aegora Incident Lead Narration",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                  Text(
                    text = if (isAudioPlaying) "Voice audio actively tracking evidence node..." else "Narration paused. Press play to resume sync stream.",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = CyberCyan
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Highlighted Active Node Details & Live Transcript Sync
            selectedNode?.let { node ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (node.isFlaggedSuspicious) NeonCrimson else CyberCyan)
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = "📍 Synced Node: ${node.label}",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = if (node.isFlaggedSuspicious) NeonCrimson else CyberCyan
                    )
                    Text(
                      text = node.timestampUtc,
                      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                      color = TextSecondaryDark
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = node.details,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Transcript Turns
            Text(
              text = "AUDIO BRIEFING TRANSCRIPT:",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
              color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            fusionState.activeTranscript.forEach { (speaker, text) ->
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberDarkSlate,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 2.dp)
              ) {
                Row(modifier = Modifier.padding(8.dp)) {
                  Text(
                    text = "$speaker: ",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CyberCyan
                  )
                  Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextPrimaryDark
                  )
                }
              }
            }
          }
        }
      }

      // 4. Ask AI In-Context Inquiry Button
      item {
        Button(
          onClick = {
            onAskAi("Regarding multimodal evidence '${selectedNode?.label}': what forensic IOC steps should I prioritize?")
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Chat, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Query AI Mentor on Synced Evidence", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
