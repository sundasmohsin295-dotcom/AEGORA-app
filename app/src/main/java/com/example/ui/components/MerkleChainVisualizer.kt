package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.result.AegoraResult
import com.example.telemetry.DiagnosticSeverity
import com.example.telemetry.DiagnosticStore
import com.example.telemetry.MerkleAuditBlock
import com.example.ui.theme.*

/**
 * PHASE 37: THE MERKLE LEDGER LIVE D3.JS / CANVAS TELEMETRY VISUALIZER
 *
 * Implements a real-time reactive cryptographic hash chain visualization panel
 * for the Main Terminal Area. Illustrates node linkages, parent hash references,
 * photonic data pulses along cryptographic edges, and instant visual fracture
 * upon adversarial tamper injection.
 *
 * Complies strictly with the Obsidian Industrial Design System:
 * - Obsidian background #030712
 * - 1dp Slate borders #1E293B
 * - Monospace JetBrains / Cyber typography
 * - High-contrast tactical severity color accents
 */
@Composable
fun MerkleChainVisualizer(
  modifier: Modifier = Modifier,
  onNodeClick: ((MerkleAuditBlock) -> Unit)? = null
) {
  val telemetryState by DiagnosticStore.merkleTelemetryStream.collectAsState()
  val blocks = telemetryState.blocks
  val isChainValid = telemetryState.isValid
  val currentRoot = telemetryState.rootHash

  var selectedBlockForInspection by remember { mutableStateOf<MerkleAuditBlock?>(null) }
  var verificationFeedback by remember { mutableStateOf<String?>(null) }

  // Animated pulse phase for photonic link transmission (0.0f .. 1.0f)
  val infiniteTransition = rememberInfiniteTransition(label = "merkle_link_pulse")
  val pulsePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "photon_phase"
  )

  val warningBlink by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "tamper_blink"
  )

  val headerBorderColor by animateColorAsState(
    targetValue = if (isChainValid) SlateBorder else HighAlertCrimson,
    animationSpec = tween(300),
    label = "border_color"
  )

  TacticalPanel(
    titleTag = "CRYPTOGRAPHIC_MERKLE_LEDGER_D3",
    subtitle = "IMMUTABLE AUDIT TRAIL • SHA-256 HASH CHAIN",
    memoryOffset = "0x7FFF0040",
    statusLed = if (isChainValid) TacticalStatusLed.ACTIVE_CYAN else TacticalStatusLed.ALERT_CRIMSON,
    isProcessing = !isChainValid,
    borderColor = headerBorderColor,
    modifier = modifier.fillMaxWidth().testTag("merkle_chain_visualizer_panel")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // 1. Telemetry HUD / Status Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(
                  if (isChainValid) ElectricCyan else HighAlertCrimson.copy(alpha = warningBlink),
                  CutCornerShape(2.dp)
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isChainValid) "CHAIN INTEGRITY: SECURE (100% MATHEMATICAL PROOF)" else "TAMPER DETECTED: CRYPTOGRAPHIC FRACTURE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
              ),
              color = if (isChainValid) ElectricCyan else HighAlertCrimson
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "ROOT: 0x${currentRoot.take(16).uppercase()}...${currentRoot.takeLast(8).uppercase()}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp
            ),
            color = TextDim
          )
        }

        // Height & Blocks Badge
        Surface(
          shape = CutCornerShape(4.dp),
          color = ObsidianSurfaceRaised,
          border = BorderStroke(1.dp, SlateBorder)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "HEIGHT: ",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
              ),
              color = TextDim
            )
            Text(
              text = "${blocks.size}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
              ),
              color = TacticalEmerald
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Interactive Horizontal Node-and-Link Visual Graph
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF040813), CutCornerShape(4.dp))
          .border(1.dp, SlateBorder, CutCornerShape(4.dp))
          .padding(vertical = 14.dp)
          .testTag("merkle_nodes_scroll_container")
      ) {
        if (blocks.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "[NO BLOCKS COMMITTED TO LEDGER]",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = TextDim
            )
          }
        } else {
          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(0.dp)
          ) {
            // Genesis Block Representation
            item {
              GenesisNode()
            }

            // Cryptographic Vector Link from Genesis to Block #1
            item {
              val firstTampered = blocks.firstOrNull()?.isTampered == true
              CryptographicLinkConnector(
                isSevered = firstTampered,
                pulsePhase = pulsePhase,
                warningBlink = warningBlink
              )
            }

            // Individual Merkle Audit Blocks
            itemsIndexed(blocks) { index, block ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                MerkleBlockNodeCard(
                  block = block,
                  isSelected = selectedBlockForInspection?.index == block.index,
                  warningBlink = warningBlink,
                  onClick = {
                    selectedBlockForInspection = block
                    onNodeClick?.invoke(block)
                  }
                )

                // Connector line to next block (if not last)
                if (index < blocks.size - 1) {
                  val nextBlock = blocks[index + 1]
                  val isLinkSevered = block.isTampered || nextBlock.isTampered || !isChainValid && index == 0
                  CryptographicLinkConnector(
                    isSevered = isLinkSevered,
                    pulsePhase = pulsePhase,
                    warningBlink = warningBlink
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 3. Tactical Control Toolbar (Verify, Tamper Simulation, Reseal, Inject Log)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Verify Button
        Button(
          onClick = {
            val result = DiagnosticStore.verifyMerkleLedgerIntegrity()
            verificationFeedback = when (result) {
              is AegoraResult.Success -> "MATHEMATICAL PROOF: Validated all ${blocks.size} blocks back to Genesis [0x0000]. Zero fractures."
              is AegoraResult.Failure -> "CRITICAL FRACTURE: ${result.message}"
            }
          },
          modifier = Modifier.weight(1f).testTag("merkle_btn_verify"),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isChainValid) TacticalEmerald else ObsidianSurfaceRaised
          ),
          border = BorderStroke(1.dp, if (isChainValid) TacticalEmerald else HighAlertCrimson),
          shape = CutCornerShape(4.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Icon(
            Icons.Default.Verified,
            contentDescription = null,
            tint = if (isChainValid) Color.Black else HighAlertCrimson,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "VERIFY PROOF",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            color = if (isChainValid) Color.Black else HighAlertCrimson
          )
        }

        // Simulate Tamper Button
        Button(
          onClick = {
            val targetIdx = selectedBlockForInspection?.index ?: 1L
            DiagnosticStore.simulateTamperAttack(targetIdx)
            verificationFeedback = "ADVERSARIAL ATTACK SIMULATED: Block #$targetIdx payload altered! Chain hash link severed."
          },
          modifier = Modifier.weight(1f).testTag("merkle_btn_tamper"),
          colors = ButtonDefaults.buttonColors(containerColor = HighAlertCrimson),
          shape = CutCornerShape(4.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Icon(
            Icons.Default.BugReport,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "SIMULATE TAMPER",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            color = Color.White
          )
        }

        // Reseal & Heal Button
        Button(
          onClick = {
            DiagnosticStore.restoreLedgerIntegrity()
            verificationFeedback = "LEDGER RESEALED: All hashes re-chained from Genesis. Cryptographic consensus restored."
          },
          modifier = Modifier.weight(1f).testTag("merkle_btn_reseal"),
          colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
          border = BorderStroke(1.dp, SlateBorderBright),
          shape = CutCornerShape(4.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Icon(
            Icons.Default.Refresh,
            contentDescription = null,
            tint = ElectricCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "RESEAL CHAIN",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            color = ElectricCyan
          )
        }

        // Inject Live Log
        Button(
          onClick = {
            DiagnosticStore.simulateLogInjection(DiagnosticSeverity.INFO)
            verificationFeedback = "INJECTED LIVE EVENT: Appended new immutable block to chain."
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF131D2E)),
          border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
          shape = CutCornerShape(4.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
        ) {
          Icon(
            Icons.Default.Add,
            contentDescription = null,
            tint = ElectricCyan,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "+EVENT",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            ),
            color = ElectricCyan
          )
        }
      }

      // Verification Result Notification Banner
      AnimatedVisibility(visible = verificationFeedback != null) {
        Surface(
          shape = CutCornerShape(4.dp),
          color = if (isChainValid) TacticalEmerald.copy(alpha = 0.1f) else HighAlertCrimson.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, if (isChainValid) TacticalEmerald else HighAlertCrimson),
          modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              if (isChainValid) Icons.Default.CheckCircle else Icons.Default.Error,
              contentDescription = null,
              tint = if (isChainValid) TacticalEmerald else HighAlertCrimson,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = verificationFeedback ?: "",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
              ),
              color = if (isChainValid) TacticalEmerald else HighAlertCrimson,
              modifier = Modifier.weight(1f)
            )
            IconButton(
              onClick = { verificationFeedback = null },
              modifier = Modifier.size(18.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextDim, modifier = Modifier.size(12.dp))
            }
          }
        }
      }

      // 4. In-Depth Cryptographic Proof Inspector (When a node is selected)
      AnimatedVisibility(visible = selectedBlockForInspection != null) {
        val sel = selectedBlockForInspection
        if (sel != null) {
          Spacer(modifier = Modifier.height(14.dp))
          CryptographicProofInspectorPanel(
            block = sel,
            onClose = { selectedBlockForInspection = null }
          )
        }
      }
    }
  }
}

/**
 * Genesis Block Node (Anchor point of the Merkle Tree)
 */
@Composable
private fun GenesisNode() {
  Surface(
    shape = CutCornerShape(4.dp),
    color = ObsidianSurfaceRaised,
    border = BorderStroke(1.dp, SlateBorderBright),
    modifier = Modifier.width(130.dp).testTag("merkle_node_genesis")
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "GENESIS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
          ),
          color = ElectricCyan
        )
        Text(
          "#00",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp
          ),
          color = TextDim
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        "PREV_HASH:",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 7.sp),
        color = TextDim
      )
      Text(
        "0x0000000000000000",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp),
        color = TextDim
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        "ANCHOR_ROOT: OK",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 8.sp
        ),
        color = TacticalEmerald
      )
    }
  }
}

/**
 * Individual Merkle Audit Block Card
 */
@Composable
private fun MerkleBlockNodeCard(
  block: MerkleAuditBlock,
  isSelected: Boolean,
  warningBlink: Float,
  onClick: () -> Unit
) {
  val severityColor = when (block.severity) {
    DiagnosticSeverity.INFO -> ElectricCyan
    DiagnosticSeverity.WARN -> TacticalAmber
    DiagnosticSeverity.CRITICAL -> HighAlertCrimson
  }

  val cardBorderColor = when {
    block.isTampered -> HighAlertCrimson.copy(alpha = warningBlink)
    isSelected -> ElectricCyan
    else -> SlateBorder
  }

  val cardBg = when {
    block.isTampered -> Color(0xFF1E0A0E)
    isSelected -> Color(0xFF0C1424)
    else -> ObsidianSurface
  }

  Surface(
    shape = CutCornerShape(4.dp),
    color = cardBg,
    border = BorderStroke(if (block.isTampered || isSelected) 1.5.dp else 1.dp, cardBorderColor),
    modifier = Modifier
      .width(170.dp)
      .clickable { onClick() }
      .testTag("merkle_block_node_${block.index}")
  ) {
    Column(modifier = Modifier.padding(10.dp)) {
      // Header: Index & Severity
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(if (block.isTampered) HighAlertCrimson else severityColor, CutCornerShape(1.dp))
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "BLOCK #${block.index}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            ),
            color = if (block.isTampered) HighAlertCrimson else TextPrimary
          )
        }
        Text(
          text = block.timestamp.takeLast(8),
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp
          ),
          color = TextDim
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = block.componentTag,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 8.sp
        ),
        color = severityColor
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Hash Signatures
      Text(
        text = "PAYLOAD: 0x${block.eventPayloadHash.take(8).uppercase()}...",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp),
        color = TextDim
      )
      Text(
        text = "CHAIN:   0x${block.blockHash.take(8).uppercase()}...",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 8.sp
        ),
        color = if (block.isTampered) HighAlertCrimson else TacticalEmerald
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Status indicator tag
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (block.isTampered) "FRACTURE [TAMPERED]" else "SEALED [SHA-256]",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 7.sp
          ),
          color = if (block.isTampered) HighAlertCrimson else TacticalEmerald
        )
        Text(
          text = "INSPECT →",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 7.sp
          ),
          color = ElectricCyan
        )
      }
    }
  }
}

/**
 * Animated Cryptographic Vector Link Connector between Nodes.
 * Renders glowing photonic particles traveling along the parent-child vector line,
 * or a broken, fractured jagged red line if tampered.
 */
@Composable
private fun CryptographicLinkConnector(
  isSevered: Boolean,
  pulsePhase: Float,
  warningBlink: Float
) {
  val linkColor = if (isSevered) HighAlertCrimson.copy(alpha = warningBlink) else ElectricCyan

  Canvas(
    modifier = Modifier
      .width(42.dp)
      .height(60.dp)
      .testTag("merkle_link_vector")
  ) {
    val startX = 0f
    val endX = size.width
    val centerY = size.height / 2f

    if (isSevered) {
      // Draw fractured jagged line
      val midX = size.width / 2f
      val path = Path().apply {
        moveTo(startX, centerY)
        lineTo(midX - 6f, centerY - 8f)
        lineTo(midX + 2f, centerY + 8f)
        lineTo(midX - 2f, centerY - 4f)
        lineTo(endX, centerY)
      }
      drawPath(
        path = path,
        color = HighAlertCrimson,
        style = Stroke(width = 2.5f, cap = StrokeCap.Round)
      )
      // Warning fracture mark in middle
      drawCircle(
        color = HighAlertCrimson.copy(alpha = warningBlink),
        radius = 4f,
        center = Offset(midX, centerY)
      )
    } else {
      // Draw smooth solid cryptographic bus line
      drawLine(
        color = SlateBorderBright,
        start = Offset(startX, centerY),
        end = Offset(endX, centerY),
        strokeWidth = 2f
      )

      // Photonic energy packet traveling along vector link
      val packetX = startX + (endX - startX) * pulsePhase
      drawCircle(
        color = ElectricCyan,
        radius = 3.5f,
        center = Offset(packetX, centerY)
      )
      drawCircle(
        color = ElectricCyan.copy(alpha = 0.4f),
        radius = 7f,
        center = Offset(packetX, centerY)
      )

      // Arrow indicator for directional verification
      val arrowPath = Path().apply {
        moveTo(endX - 4f, centerY - 4f)
        lineTo(endX, centerY)
        lineTo(endX - 4f, centerY + 4f)
      }
      drawPath(
        path = arrowPath,
        color = ElectricCyan,
        style = Stroke(width = 2f, cap = StrokeCap.Round)
      )
    }
  }
}

/**
 * Detailed Cryptographic Proof Inspector Panel
 */
@Composable
private fun CryptographicProofInspectorPanel(
  block: MerkleAuditBlock,
  onClose: () -> Unit
) {
  Surface(
    shape = CutCornerShape(4.dp),
    color = Color(0xFF090D18),
    border = BorderStroke(1.dp, if (block.isTampered) HighAlertCrimson else ElectricCyan.copy(alpha = 0.6f)),
    modifier = Modifier.fillMaxWidth().testTag("merkle_proof_inspector_panel")
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            Icons.Default.Security,
            contentDescription = null,
            tint = if (block.isTampered) HighAlertCrimson else ElectricCyan,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CRYPTOGRAPHIC PROOF INSPECTOR [BLOCK #${block.index}]",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            ),
            color = if (block.isTampered) HighAlertCrimson else ElectricCyan
          )
        }
        IconButton(onClick = onClose, modifier = Modifier.size(20.dp)) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDim, modifier = Modifier.size(14.dp))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Verification Formula
      Text(
        text = "MATHEMATICAL CONTRACT:",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp),
        color = TextDim
      )
      Text(
        text = "H_n = SHA256( PayloadHash_n + H_(n-1) )",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        ),
        color = TacticalEmerald
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Previous Block Hash
      Text(
        text = "PREVIOUS BLOCK HASH (H_(n-1)):",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp),
        color = TextDim
      )
      Text(
        text = block.previousBlockHash,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 8.sp
        ),
        color = TextPrimary
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Event Payload Hash
      Text(
        text = "EVENT PAYLOAD HASH (PayloadHash_n):",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp),
        color = TextDim
      )
      Text(
        text = block.eventPayloadHash,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 8.sp
        ),
        color = if (block.isTampered) HighAlertCrimson else TextPrimary
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Current Block Hash
      Text(
        text = "CHAIN BLOCK HASH (H_n):",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp),
        color = TextDim
      )
      Text(
        text = block.blockHash,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 8.sp
        ),
        color = if (block.isTampered) HighAlertCrimson else TacticalEmerald
      )

      if (block.isTampered) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = CutCornerShape(2.dp),
          color = HighAlertCrimson.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, HighAlertCrimson)
        ) {
          Text(
            text = "VERIFICATION RESULT: FRACTURE DETECTED. Recomputed hash does not match block signature.",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 8.sp
            ),
            color = HighAlertCrimson,
            modifier = Modifier.padding(6.dp)
          )
        }
      }
    }
  }
}
