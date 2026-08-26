package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.AcousticThreatProfile
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun BioStressScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bioReading by AegoraRepository.bioStressReading.collectAsState()
  val acousticProfiles = AegoraRepository.acousticProfiles
  var selectedAcoustic by remember { mutableStateOf(acousticProfiles.first()) }
  var lastTapTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

  // Oscilloscope waveform phase animation
  val infiniteTransition = rememberInfiniteTransition(label = "oscilloscope")
  val phase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * Math.PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave_phase"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(PureBlack)
      .pointerInput(Unit) {
        detectTapGestures { offset ->
          val now = System.currentTimeMillis()
          val hesitation = (now - lastTapTime).coerceAtMost(3000)
          lastTapTime = now
          AegoraRepository.recordInteractionKinetics(offset.x % 300, hesitation)
        }
      }
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // 1. Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(CyberSurfaceElevated)
            .border(1.dp, CyberBorderSubtle, CircleShape)
        ) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan, modifier = Modifier.size(20.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = if (bioReading.cognitiveOverloadWarning) NeonPink else CyberEmerald, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "BIOMETRIC STRESS & ACOUSTIC RADAR",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = if (bioReading.cognitiveOverloadWarning) NeonPink else CyberEmerald
          )
        }
      }
    }

    // 2. Biometric Kinetics HUD & Composure Dial
    item {
      CyberCard(
        borderColor = if (bioReading.cognitiveOverloadWarning) NeonPink else CyberEmerald,
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "COGNITIVE COMPOSURE INDEX",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = CyberCyan
            )
            Text(
              text = "Live telemetry interaction kinetics during breach triage",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = (if (bioReading.cognitiveOverloadWarning) NeonPink else CyberEmerald).copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (bioReading.cognitiveOverloadWarning) NeonPink else CyberEmerald)
          ) {
            Text(
              text = "${bioReading.composureIndexScore}% COMPOSURE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              color = if (bioReading.cognitiveOverloadWarning) NeonPink else CyberEmerald,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Three Telemetry Dials
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${bioReading.simulatedBpm} BPM",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = if (bioReading.simulatedBpm > 100) NeonPink else CyberEmerald
              )
              Text("Heart Rate (Sim)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${bioReading.tapHesitationMs}ms",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = CyberCyan
              )
              Text("Hesitation Interval", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${bioReading.touchVelocityPxPerSec.toInt()} px/s",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = CyberAmber
              )
              Text("Touch Velocity", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CodeBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Speed, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CADENCE STATUS: ${bioReading.triageCadenceStatus}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = if (bioReading.cognitiveOverloadWarning) NeonPink else CodeGreen
            )
          }
        }
      }
    }

    // 3. Acoustic Threat Sonification Waveform Synthesizer
    item {
      CyberCard(
        borderColor = CyberCyan,
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "ACOUSTIC THREAT SONIFICATION",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = CyberCyan
            )
            Text(
              text = "Perceptual frequency synthesis from network telemetry",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberCyan.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
          ) {
            Text(
              text = "${selectedAcoustic.frequencyKhz} kHz",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberCyan,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Animated Oscilloscope Canvas
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = PureBlack,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
        ) {
          Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            val width = size.width
            val height = size.height
            val midY = height / 2f

            // Draw grid lines
            drawLine(
              color = Color.DarkGray.copy(alpha = 0.3f),
              start = Offset(0f, midY),
              end = Offset(width, midY),
              strokeWidth = 1f
            )

            val path = Path()
            val points = 80
            for (i in 0..points) {
              val x = (i.toFloat() / points) * width
              val freqMultiplier = when (selectedAcoustic.wavePattern) {
                "PULSING_C2_HEARTBEAT" -> 3f
                "CHIRPING_DNS_TUNNEL" -> 6f
                "HIGH_FREQ_SYN_SURGE" -> 12f
                else -> 1.5f
              }
              val amp = (height * 0.35f) * when (selectedAcoustic.wavePattern) {
                "PULSING_C2_HEARTBEAT" -> if (i % 20 < 4) 1.2f else 0.2f
                "HIGH_FREQ_SYN_SURGE" -> 0.9f
                else -> 0.6f
              }
              val y = midY + sin((i.toFloat() / 5f) * freqMultiplier + phase) * amp
              if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
              path = path,
              color = when (selectedAcoustic.wavePattern) {
                "HIGH_FREQ_SYN_SURGE" -> NeonPink
                "PULSING_C2_HEARTBEAT" -> CyberAmber
                "CHIRPING_DNS_TUNNEL" -> CyberViolet
                else -> CyberCyan
              },
              style = Stroke(width = 2.5f)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Acoustic profile selector chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          acousticProfiles.forEach { profile ->
            val isSel = profile.id == selectedAcoustic.id
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSel) CyberSurfaceElevated else Color.Transparent,
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyberCyan else CyberBorderSubtle),
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { selectedAcoustic = profile }
            ) {
              Text(
                text = profile.name.split(" ").take(2).joinToString(" "),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                  fontFamily = FontFamily.Monospace
                ),
                color = if (isSel) CyberCyan else TextSecondaryDark,
                modifier = Modifier.padding(6.dp),
                maxLines = 1
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Diagnostic signature info box
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CodeBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "DIAGNOSTIC SIGNATURE (${selectedAcoustic.trafficType}):",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberCyan
            )
            Text(
              text = selectedAcoustic.audioDescription,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )
            Text(
              text = "• ${selectedAcoustic.diagnosticSignature}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
              color = CodeGreen
            )
          }
        }
      }
    }
  }
}
