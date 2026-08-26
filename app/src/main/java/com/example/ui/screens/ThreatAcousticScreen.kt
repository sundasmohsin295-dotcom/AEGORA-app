package com.example.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin

enum class AcousticThreatType(val title: String, val frequencyDesc: String, val color: Color) {
  BASELINE_HTTPS("Normal HTTPS Baseline", "440Hz Steady Sine Wave • Low Variance", CyberCyan),
  C2_BEACON("C2 Periodic Heartbeat", "880Hz Rhythmic 15s Pulse Spike", NeonCrimson),
  SYN_FLOOD("Volumetric SYN Flood", "1760Hz High-Jitter Chaos Oscillation", NeonPink),
  DNS_TUNNEL("DNS Exfiltration Chirp", "620Hz Fast Modulated Frequency Bursts", CyberAmber)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatAcousticScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  var selectedThreat by remember { mutableStateOf(AcousticThreatType.C2_BEACON) }
  var isDrillActive by remember { mutableStateOf(false) }
  var drillTimeRemaining by remember { mutableIntStateOf(30) }
  var drillScore by remember { mutableIntStateOf(0) }
  var drillFeedback by remember { mutableStateOf<String?>(null) }

  val infiniteTransition = rememberInfiniteTransition(label = "oscilloscope_animation")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = (2 * PI).toFloat(),
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wave_phase"
  )

  LaunchedEffect(isDrillActive) {
    if (isDrillActive) {
      drillTimeRemaining = 30
      while (drillTimeRemaining > 0 && isDrillActive) {
        kotlinx.coroutines.delay(1000)
        drillTimeRemaining--
      }
      if (drillTimeRemaining == 0) {
        isDrillActive = false
      }
    }
  }

  fun submitDrillGuess(guess: AcousticThreatType) {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    if (guess == selectedThreat) {
      drillScore += 100
      drillFeedback = "✓ Correct Signal Identification! (+100 PTS)"
      // Randomize next target
      val next = AcousticThreatType.entries.filter { it != selectedThreat }.random()
      selectedThreat = next
    } else {
      drillScore = (drillScore - 30).coerceAtLeast(0)
      drillFeedback = "✗ False Identification! Signal was ${selectedThreat.title}"
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(NeonViolet.copy(alpha = 0.15f))
                .border(1.dp, NeonViolet, RoundedCornerShape(6.dp)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                "THREAT SONIFICATION RADAR",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = TextPrimaryDark
              )
              Text(
                "Acoustic Telemetry Waveform Oscilloscope",
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
    containerColor = CyberBlack
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(CyberBlack)
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Oscilloscope Live Canvas
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF040407),
        border = androidx.compose.foundation.BorderStroke(1.dp, selectedThreat.color.copy(alpha = 0.6f)),
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val w = size.width
          val h = size.height
          val midY = h / 2f

          // Draw radar grid lines
          val gridColor = selectedThreat.color.copy(alpha = 0.12f)
          for (x in 0..8) {
            val posX = (w / 8) * x
            drawLine(gridColor, Offset(posX, 0f), Offset(posX, h), strokeWidth = 1f)
          }
          for (y in 0..6) {
            val posY = (h / 6) * y
            drawLine(gridColor, Offset(0f, posY), Offset(w, posY), strokeWidth = 1f)
          }

          // Center horizon
          drawLine(
            selectedThreat.color.copy(alpha = 0.3f),
            Offset(0f, midY),
            Offset(w, midY),
            strokeWidth = 1.5.dp.toPx()
          )

          // Waveform path
          val path = Path()
          val points = 200
          for (i in 0..points) {
            val x = (w / points) * i
            val progress = (i.toFloat() / points) * 4 * PI.toFloat()

            val yOffset = when (selectedThreat) {
              AcousticThreatType.BASELINE_HTTPS -> {
                sin(progress + wavePhase) * (h * 0.22f)
              }
              AcousticThreatType.C2_BEACON -> {
                val spike = if (i % 50 in 20..26) (h * 0.42f) else (h * 0.08f)
                sin(progress * 2 + wavePhase) * spike
              }
              AcousticThreatType.SYN_FLOOD -> {
                val noise = if (i % 2 == 0) 15f else -15f
                sin(progress * 4 + wavePhase) * (h * 0.36f) + noise
              }
              AcousticThreatType.DNS_TUNNEL -> {
                val burst = sin(progress * 6 + wavePhase) * (h * 0.28f)
                burst * (0.5f + 0.5f * sin(progress + wavePhase))
              }
            }

            val y = midY + yOffset
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
          }

          drawPath(
            path = path,
            color = selectedThreat.color,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
          )

          // Glow bloom
          drawPath(
            path = path,
            color = selectedThreat.color.copy(alpha = 0.25f),
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
          )
        }
      }

      // Signal Metadata Card
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              selectedThreat.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
              color = selectedThreat.color
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = selectedThreat.color.copy(alpha = 0.2f)
            ) {
              Text(
                "ACTIVE FREQUENCY",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                color = selectedThreat.color,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            selectedThreat.frequencyDesc,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = TextSecondaryDark
          )
        }
      }

      // Acoustic Drill Section
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberCardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "ACOUSTIC CLASSIFICATION DRILL",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberCyan
            )
            if (isDrillActive) {
              Text(
                "TIMER: ${drillTimeRemaining}s • SCORE: $drillScore",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberEmerald
              )
            }
          }

          if (!isDrillActive) {
            Button(
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                isDrillActive = true
                drillScore = 0
                drillFeedback = null
                selectedThreat = AcousticThreatType.entries.random()
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("start_acoustic_drill_btn")
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Start 30-Second Drill", color = Color.Black, fontWeight = FontWeight.Bold)
            }
          } else {
            if (drillFeedback != null) {
              Text(
                text = drillFeedback ?: "",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = if (drillFeedback?.startsWith("✓") == true) CyberEmerald else NeonCrimson
              )
            }

            Text(
              "Listen / Inspect waveform and classify the active cyber signal:",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )

            // 4 option buttons
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              AcousticThreatType.entries.forEach { threat ->
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CyberSurfaceElevated,
                  border = androidx.compose.foundation.BorderStroke(1.dp, threat.color.copy(alpha = 0.5f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .clickable { submitDrillGuess(threat) }
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(threat.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      threat.title,
                      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                      color = TextPrimaryDark
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
