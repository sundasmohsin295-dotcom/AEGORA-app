package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * KILLER FEATURE — Authoritative Server Evidence Verification Sequence
 *
 * Sequence steps:
 * 1. Freeze current UI (~150ms)
 * 2. Display: VERIFYING EVIDENCE
 * 3. Central evidence envelope
 * 4. 3 verification stages: EVIDENCE -> INTEGRITY -> SERVER
 * 5. Display actual server verification metadata (Evidence ID, Timestamp, Digest)
 * 6. Cyan chain-link visual: EVIDENCE -> VERIFIER -> CAPABILITY
 * 7. VERIFIED state
 * 8. Authoritative capability delta animation (previous -> newly fetched server value)
 * 9. Twin Updated signal
 * 10. Direct completion
 */
@Composable
fun CinematicVerificationModal(
  evidenceId: String,
  missionTitle: String,
  submissionId: String,
  verificationDigest: String,
  previousCapability: Int,
  authoritativeCapability: Int,
  affectedSkillName: String,
  onComplete: () -> Unit
) {
  var stageIndex by remember { mutableIntStateOf(0) }
  // Stage 0: Initial freeze & envelope reveal
  // Stage 1: Evidence Integrity check
  // Stage 2: Server-side authority validation
  // Stage 3: Chain-link commitment
  // Stage 4: Verified & Capability Delta animation
  // Stage 5: Twin Updated confirmation

  val animatedCapability = remember { Animatable(previousCapability.toFloat()) }

  LaunchedEffect(Unit) {
    delay(180) // Step 1: UI Freeze
    stageIndex = 1
    delay(500) // Step 2: Evidence check
    stageIndex = 2
    delay(600) // Step 3: Integrity Digest check
    stageIndex = 3
    delay(700) // Step 4: Server verification commit
    stageIndex = 4
    // Step 8: Animate authoritative capability
    animatedCapability.animateTo(
      targetValue = authoritativeCapability.toFloat(),
      animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
    )
    delay(400)
    stageIndex = 5 // Step 9: Twin updated
    delay(1000)
    onComplete()
  }

  Dialog(
    onDismissRequest = {},
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(16.dp))
        .border(1.5.dp, if (stageIndex >= 4) AegoraCyanVerified else AegoraBorder, RoundedCornerShape(16.dp))
        .testTag("cinematic_verification_dialog"),
      color = AegoraBackground
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Status Top Badge
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = if (stageIndex >= 4) AegoraCyanVerified.copy(alpha = 0.15f) else AegoraSurfaceElevated,
          border = BorderStroke(1.dp, if (stageIndex >= 4) AegoraCyanVerified else AegoraBorder)
        ) {
          Text(
            text = if (stageIndex >= 4) "VERIFIED" else "VERIFYING EVIDENCE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.2.sp,
              fontSize = 10.sp
            ),
            color = if (stageIndex >= 4) AegoraCyanVerified else AegoraTextSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Central Animated Envelope / Chain Element
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              if (stageIndex >= 4) AegoraCyanVerified.copy(alpha = 0.15f)
              else AegoraSurfaceElevated
            )
            .border(
              2.dp,
              if (stageIndex >= 4) AegoraCyanVerified else AegoraBorder,
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          if (stageIndex < 4) {
            val rotationTransition = rememberInfiniteTransition(label = "verifying_spin")
            val spin by rotationTransition.animateFloat(
              initialValue = 0f,
              targetValue = 360f,
              animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
              label = "spin_angle"
            )
            Icon(
              imageVector = Icons.Default.Sync,
              contentDescription = "Verifying",
              tint = AegoraCyanVerified,
              modifier = Modifier
                .size(32.dp)
                .rotate(spin)
            )
          } else {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Verified",
              tint = AegoraCyanVerified,
              modifier = Modifier.size(36.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = missionTitle,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = AegoraTextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Verification Stages: EVIDENCE -> INTEGRITY -> SERVER
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          StageIndicator(label = "EVIDENCE", state = if (stageIndex >= 1) StageState.VERIFIED else StageState.NEUTRAL)
          StageConnector(isActive = stageIndex >= 2)
          StageIndicator(label = "INTEGRITY", state = if (stageIndex >= 2) StageState.VERIFIED else StageState.NEUTRAL)
          StageConnector(isActive = stageIndex >= 3)
          StageIndicator(label = "SERVER", state = if (stageIndex >= 3) StageState.VERIFIED else StageState.NEUTRAL)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Server Verification Telemetry Card
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = AegoraSurfaceElevated,
          border = BorderStroke(1.dp, AegoraBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "VERIFICATION RECORD",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 9.sp
                ),
                color = AegoraTextSecondary
              )
              Text(
                text = "STATUS: ${if (stageIndex >= 4) "VERIFIED" else "PROCESSING"}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = if (stageIndex >= 4) AegoraCyanVerified else AegoraAmberDecay
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            TelemetryRow(label = "Evidence ID", value = evidenceId)
            TelemetryRow(label = "Submission ID", value = submissionId)
            TelemetryRow(label = "Integrity Hash", value = verificationDigest.take(28) + "...")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cyan Chain-Link Visual: EVIDENCE -> VERIFIER -> CAPABILITY
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = AegoraSurface,
          border = BorderStroke(1.dp, if (stageIndex >= 4) AegoraCyanVerified.copy(alpha = 0.5f) else AegoraBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            ChainPill(text = "EVIDENCE", isConnected = stageIndex >= 1)
            Icon(Icons.Default.Link, contentDescription = null, tint = if (stageIndex >= 2) AegoraCyanVerified else AegoraTextSecondary, modifier = Modifier.size(16.dp))
            ChainPill(text = "VERIFIER", isConnected = stageIndex >= 3)
            Icon(Icons.Default.Link, contentDescription = null, tint = if (stageIndex >= 4) AegoraCyanVerified else AegoraTextSecondary, modifier = Modifier.size(16.dp))
            ChainPill(text = "CAPABILITY", isConnected = stageIndex >= 4)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Authoritative Capability Delta
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = AegoraSurfaceElevated,
          border = BorderStroke(1.dp, if (stageIndex >= 4) AegoraCyanVerified.copy(alpha = 0.6f) else AegoraBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = affectedSkillName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = AegoraTextPrimary
              )
              Text(
                text = if (stageIndex >= 5) "TWIN UPDATED" else "AUTHORITATIVE LEVEL",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp
                ),
                color = if (stageIndex >= 5) AegoraCyanVerified else AegoraTextSecondary
              )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "$previousCapability%",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 14.sp
                ),
                color = AegoraTextSecondary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = AegoraCyanVerified,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "${animatedCapability.value.toInt()}%",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 20.sp
                ),
                color = AegoraCyanVerified
              )
            }
          }
        }
      }
    }
  }
}

enum class StageState { NEUTRAL, ACTIVE, VERIFIED }

@Composable
private fun StageIndicator(label: String, state: StageState) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Box(
      modifier = Modifier
        .size(18.dp)
        .clip(CircleShape)
        .background(
          when (state) {
            StageState.VERIFIED -> AegoraCyanVerified
            StageState.ACTIVE -> AegoraAmberDecay
            StageState.NEUTRAL -> AegoraBorder
          }
        ),
      contentAlignment = Alignment.Center
    ) {
      if (state == StageState.VERIFIED) {
        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Bold
      ),
      color = if (state == StageState.VERIFIED) AegoraCyanVerified else AegoraTextSecondary
    )
  }
}

@Composable
private fun StageConnector(isActive: Boolean) {
  Box(
    modifier = Modifier
      .width(28.dp)
      .height(2.dp)
      .background(if (isActive) AegoraCyanVerified else AegoraBorder)
  )
}

@Composable
private fun ChainPill(text: String, isConnected: Boolean) {
  Surface(
    shape = RoundedCornerShape(4.dp),
    color = if (isConnected) AegoraCyanVerified.copy(alpha = 0.12f) else AegoraBackground,
    border = BorderStroke(1.dp, if (isConnected) AegoraCyanVerified else AegoraBorder)
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Bold
      ),
      color = if (isConnected) AegoraCyanVerified else AegoraTextSecondary,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
  }
}

@Composable
private fun TelemetryRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 1.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp
      ),
      color = AegoraTextSecondary
    )
    Text(
      text = value,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium
      ),
      color = AegoraTextPrimary
    )
  }
}
