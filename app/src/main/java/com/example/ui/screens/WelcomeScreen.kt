package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
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
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ================================================================================
 * PHASE 7: THE CINEMATIC "BOOT SEQUENCE" (WELCOME / SPLASH SCREEN POLISH)
 * ================================================================================
 * Standards: Ex-Apple / Palantir Principal UI/UX Motion Designer
 *
 * 1. ANIMATED BRANDING (THE GLOW & SCALE):
 *    - Jetpack Compose `Animatable` and `animateFloatAsState`.
 *    - AEGORA logo & title smoothly fade in and scale up using `Spring.DampingRatioLowBouncy`.
 *    - Glowing drop shadow & radiant aura behind text (Cobalt Blue `#3B82F6` & Cyan `#00E5FF`).
 *
 * 2. TERMINAL BOOT SEQUENCE (CYBERSECURITY MILITARY-GRADE VIBE):
 *    - Rapidly updating terminal text effect at the bottom of the screen.
 *    - Monospace glowing Emerald (`#34D399`) & Cyan telemetry strings:
 *      "[SYS] Initializing Kernel & Hardware Keys...",
 *      "[SYS] Decrypting Secure Enclave...",
 *      "[NET] Establishing WSS Telemetry Mesh...",
 *      "[AI] Warming Neural Core & MITRE Engines...",
 *      "[AUTH] Zero-Trust Verified • Enclave Ready"
 *
 * 3. ZERO-LATENCY TRANSITION:
 *    - Holds for exactly 2.5 seconds total runtime.
 *    - Smooth crossfade / scale-zoom into destination without white flashing.
 *    - Background strictly anchored to deep Obsidian (`#050B14`).
 */
@Composable
fun WelcomeScreen(
  onBootComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val interactionSource = remember { MutableInteractionSource() }

  // 1. BRANDING ANIMATION STATES
  val brandAlpha = remember { Animatable(0f) }
  val brandScale = remember { Animatable(0.85f) }
  val glowIntensity = remember { Animatable(0f) }

  // 2. TERMINAL BOOT TELEMETRY SEQUENCING
  val bootSteps = listOf(
    "[SYS] Initializing Kernel & Keystore v4.9...",
    "[CRYPT] Decrypting Secure Enclave & Hardware Keys...",
    "[NET] Establishing WSS Telemetry Mesh :443...",
    "[AI] Warming Neural Core & MITRE Knowledge Engine...",
    "[AUTH] Zero-Trust Verified • Enclave Ready"
  )
  val displayedLogs = remember { mutableStateListOf<String>() }
  var currentTypingLine by remember { mutableStateOf("") }
  var bootProgress by remember { mutableFloatStateOf(0.08f) }
  var isExitTransitionTriggered by remember { mutableStateOf(false) }

  // Exit transition animatables
  val exitAlpha by animateFloatAsState(
    targetValue = if (isExitTransitionTriggered) 0f else 1f,
    animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
    label = "exit_alpha"
  )
  val exitScale by animateFloatAsState(
    targetValue = if (isExitTransitionTriggered) 1.08f else 1f,
    animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
    label = "exit_scale"
  )

  // Continuous subtle pulse for the background security grid & ring aura
  val infiniteTransition = rememberInfiniteTransition(label = "enclave_pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 0.94f,
    targetValue = 1.06f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_radius"
  )

  // Terminal cursor blink animation
  val cursorAlpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(450, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "cursor_blink"
  )

  // Orchestrated lifecycle: Animated terminal text effect simulating system decryption and telemetry initialization
  LaunchedEffect(Unit) {
    // Stage 1: Brand & Logo Fade-in with Bouncy Spring
    launch {
      brandAlpha.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
      )
    }
    launch {
      brandScale.animateTo(
        targetValue = 1f,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioLowBouncy,
          stiffness = Spring.StiffnessLow
        )
      )
    }
    launch {
      glowIntensity.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing)
      )
    }

    delay(200)

    // Stage 2: Realistic Terminal Typewriter Decryption Sequence
    bootSteps.forEachIndexed { index, stepText ->
      currentTypingLine = ""
      bootProgress = ((index + 1).toFloat() / bootSteps.size.toFloat()).coerceIn(0.15f, 1f)

      // Character-by-character typewriter effect simulating decryption stream
      for (charIndex in 1..stepText.length) {
        currentTypingLine = stepText.substring(0, charIndex)
        // High-velocity typewriter pace (12-18ms per character)
        delay(14)
      }

      displayedLogs.add(stepText)
      currentTypingLine = ""
      delay(120) // Brief pause between completed telemetry steps
    }

    // Final hold on zero-trust verified state
    delay(400)
    isExitTransitionTriggered = true

    // Crossfade exit window
    delay(320)
    onBootComplete()
  }

  // Deep military Obsidian canvas (#050B14) guaranteed throughout
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF050B14))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = {
          if (!isExitTransitionTriggered) {
            isExitTransitionTriggered = true
            onBootComplete()
          }
        }
      )
      .testTag("welcome_boot_screen_root"),
    contentAlignment = Alignment.Center
  ) {
    // Dynamic Ambient Glowing Radial Background (Cobalt #3B82F6 & Deep Indigo)
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .alpha(exitAlpha)
    ) {
      val center = Offset(size.width / 2f, size.height * 0.42f)
      val radius = size.minDimension * 0.75f * pulseRadius

      // Ambient Cobalt Glow Behind Logo & Text
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF3B82F6).copy(alpha = 0.35f * glowIntensity.value),
            Color(0xFF00E5FF).copy(alpha = 0.20f * glowIntensity.value),
            Color(0xFF1D4ED8).copy(alpha = 0.08f * glowIntensity.value),
            Color.Transparent
          ),
          center = center,
          radius = radius
        ),
        radius = radius,
        center = center
      )

      // Technical crosshairs & fine telemetry guidelines
      val lineAlpha = 0.08f * glowIntensity.value
      drawLine(
        color = Color(0xFF38BDF8).copy(alpha = lineAlpha),
        start = Offset(0f, center.y),
        end = Offset(size.width, center.y),
        strokeWidth = 1f
      )
      drawLine(
        color = Color(0xFF38BDF8).copy(alpha = lineAlpha),
        start = Offset(center.x, 0f),
        end = Offset(center.x, size.height),
        strokeWidth = 1f
      )
    }

    // MAIN BRANDING CORE (HERO ICON + LOGO + GLOW)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 28.dp)
        .scale(brandScale.value * exitScale)
        .alpha(brandAlpha.value * exitAlpha),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Geometric Cyber Enclave Monogram / Solar Crest
      Box(
        modifier = Modifier.size(130.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val center = Offset(size.width / 2f, size.height / 2f)
          val baseRadius = size.minDimension / 2.5f

          // Cobalt Blue drop shadow / outer radiant aura
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(
                Color(0xAA3B82F6),
                Color(0x5500E5FF),
                Color.Transparent
              ),
              center = center,
              radius = baseRadius * 1.55f * pulseRadius
            ),
            radius = baseRadius * 1.55f * pulseRadius,
            center = center
          )

          // Dark core plate
          drawCircle(
            color = Color(0xFF070F1E),
            radius = baseRadius * 0.95f,
            center = center
          )

          // Double Concentric Enclave Rim Rings
          drawCircle(
            brush = Brush.sweepGradient(
              colors = listOf(
                Color(0xFF00E5FF),
                Color(0xFF3B82F6),
                Color(0xFF2563EB),
                Color(0xFF00E5FF)
              ),
              center = center
            ),
            radius = baseRadius * 0.96f,
            center = center,
            style = Stroke(width = 2.4.dp.toPx())
          )

          // Geometric Hexagon Matrix Core
          val hexRadius = baseRadius * 0.48f
          val hexPath = Path()
          for (i in 0 until 6) {
            val angle = Math.toRadians((i * 60.0) - 30.0)
            val x = center.x + (hexRadius * kotlin.math.cos(angle)).toFloat()
            val y = center.y + (hexRadius * kotlin.math.sin(angle)).toFloat()
            if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
          }
          hexPath.close()

          drawPath(
            path = hexPath,
            brush = Brush.verticalGradient(
              listOf(Color(0xFF00E5FF), Color(0xFF3B82F6))
            ),
            style = Stroke(width = 2.2.dp.toPx())
          )

          // Center Aegis Vertex Dot
          drawCircle(
            color = Color(0xFF34D399),
            radius = 3.dp.toPx(),
            center = center
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      // AEGORA BRAND TITLE WITH COBALT GLOW
      Box(contentAlignment = Alignment.Center) {
        // Glowing Drop Shadow Layer (Cobalt Blue #3B82F6)
        Text(
          text = "AEGORA",
          style = MaterialTheme.typography.displayMedium.copy(
            fontFamily = FontFamily.Monospace,
            letterSpacing = 10.sp,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black
          ),
          color = Color(0xFF3B82F6).copy(alpha = 0.55f * glowIntensity.value),
          modifier = Modifier.offset(y = 2.dp)
        )

        // Crisp Display Forefront
        Text(
          text = "AEGORA",
          style = MaterialTheme.typography.displayMedium.copy(
            fontFamily = FontFamily.Monospace,
            letterSpacing = 10.sp,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black
          ),
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Military-Grade System Subtitle
      Text(
        text = "NEXT-GEN CYBER DEFENSE OPERATING SYSTEM",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          letterSpacing = 3.sp,
          fontSize = 9.sp,
          fontWeight = FontWeight.SemiBold
        ),
        color = Color(0xFF7DD3FC)
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "ENTERPRISE ZERO-TRUST ENCLAVE // MIL-STD-810H",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          letterSpacing = 1.5.sp,
          fontSize = 8.sp,
          fontWeight = FontWeight.Normal
        ),
        color = Color(0xFF475569)
      )
    }

    // BOTTOM TERMINAL BOOT SEQUENCE CONSOLE
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 24.dp, vertical = 24.dp)
        .alpha(exitAlpha),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Boot Status Terminal Card
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("boot_terminal_console"),
        shape = RoundedCornerShape(8.dp),
        color = Color(0x990A1324),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3338BDF8))
      ) {
        Column(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              // Pulsing status dot (Emerald when verified)
              val dotColor = if (bootProgress >= 1f) Color(0xFF34D399) else Color(0xFF00E5FF)
              Surface(
                modifier = Modifier.size(6.dp),
                shape = RoundedCornerShape(3.dp),
                color = dotColor
              ) {}

              Text(
                text = "BOOTLOADER v4.9.1",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF64748B)
              )
            }

            Text(
              text = "${(bootProgress * 100).toInt()}%",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFF34D399)
            )
          }

          // Monospace glowing boot sequence stream with active typewriter effect
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
          ) {
            // Show previously completed lines (up to last 2 for compact high-tech layout)
            displayedLogs.takeLast(2).forEach { logLine ->
              Text(
                text = logLine,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF64748B),
                maxLines = 1
              )
            }

            // Current active decryption / telemetry line with blinking cursor
            val activeText = if (currentTypingLine.isNotEmpty()) {
              currentTypingLine
            } else if (displayedLogs.isNotEmpty()) {
              displayedLogs.last()
            } else {
              bootSteps.first()
            }

            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = activeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = if (bootProgress >= 1f) Color(0xFF34D399) else Color(0xFF00E5FF),
                maxLines = 1,
                modifier = Modifier.weight(1f, fill = false)
              )
              if (bootProgress < 1f) {
                Spacer(modifier = Modifier.width(2.dp))
                Surface(
                  modifier = Modifier
                    .width(6.dp)
                    .height(13.dp)
                    .alpha(cursorAlpha),
                  color = Color(0xFF00E5FF)
                ) {}
              }
            }
          }

          // High-precision slim progress track
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(2.dp)
              .background(Color(0xFF1E293B), RoundedCornerShape(1.dp))
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(bootProgress)
                .height(2.dp)
                .background(
                  Brush.horizontalGradient(
                    listOf(Color(0xFF00E5FF), Color(0xFF34D399))
                  ),
                  RoundedCornerShape(1.dp)
                )
            )
          }
        }
      }

      // Fast-pass skip affordance for instant navigation
      Surface(
        onClick = {
          if (!isExitTransitionTriggered) {
            isExitTransitionTriggered = true
            onBootComplete()
          }
        },
        shape = RoundedCornerShape(14.dp),
        color = Color(0x330F172A),
        border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0x22475569)),
        modifier = Modifier.testTag("btn_skip_boot")
      ) {
        Text(
          text = "TAP ANYWHERE TO BYPASS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = Color(0xFF64748B),
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
      }
    }
  }
}
