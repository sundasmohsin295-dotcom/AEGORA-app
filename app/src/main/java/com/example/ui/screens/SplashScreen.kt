package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * AEGORA CINEMATIC SPLASH SCREEN ("THE SUN PRODUCTION")
 * Exact animation timeline:
 * - 0 - 400ms: Glowing cyan solar eclipse and geometric corona aura fade in.
 * - 400 - 1000ms: "THE SUN PRODUCTION" tracked typography fades in.
 * - 1000 - 1400ms: Hold for clarity and brand recognition.
 * - 1400 - 1800ms: Solar corona expands and crossfades directly to Authentication.
 */
@Composable
fun SplashScreen(
  onSplashFinished: () -> Unit,
  modifier: Modifier = Modifier
) {
  var animationStep by remember { mutableIntStateOf(0) }
  val interactionSource = remember { MutableInteractionSource() }

  // Orchestrated timing sequence (1800ms total)
  LaunchedEffect(Unit) {
    // 0 - 400ms: Logo fade-in
    delay(50)
    animationStep = 1

    // 400 - 1000ms: Brand subtitle typography
    delay(400)
    animationStep = 2

    // 1000 - 1400ms: Hold
    delay(600)
    animationStep = 3

    // 1400 - 1800ms: Expansion and exit transition
    delay(400)
    animationStep = 4

    delay(400)
    onSplashFinished()
  }

  // Smooth continuous corona ray pulsation
  val infiniteTransition = rememberInfiniteTransition(label = "corona_pulse")
  val coronaPulse by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "corona_pulse"
  )

  // Animated scale & alpha transitions for the logo
  val logoScale by animateFloatAsState(
    targetValue = when (animationStep) {
      0 -> 0.7f
      1 -> 1.0f
      2, 3 -> 1.02f
      4 -> 1.35f
      else -> 1.4f
    },
    animationSpec = tween(
      durationMillis = if (animationStep == 4) 400 else 600,
      easing = FastOutSlowInEasing
    ),
    label = "logo_scale"
  )

  val logoAlpha by animateFloatAsState(
    targetValue = when (animationStep) {
      0 -> 0f
      1, 2, 3 -> 1f
      4 -> 0f
      else -> 0f
    },
    animationSpec = tween(
      durationMillis = if (animationStep == 4) 380 else 400,
      easing = LinearEasing
    ),
    label = "logo_alpha"
  )

  val textAlpha by animateFloatAsState(
    targetValue = if (animationStep in 2..3) 1f else 0f,
    animationSpec = tween(durationMillis = 350, easing = LinearEasing),
    label = "text_alpha"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF000000))
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onSplashFinished
      )
      .testTag("splash_screen_root"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .wrapContentHeight()
        .padding(horizontal = 24.dp)
    ) {
      // Geometric Sun & Solar Eclipse Corona Visual
      Box(
        modifier = Modifier
          .size(140.dp)
          .scale(logoScale)
          .alpha(logoAlpha),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val center = Offset(size.width / 2f, size.height / 2f)
          val baseRadius = size.minDimension / 2.6f

          // Outer Solar Corona Blur/Aura (Electric Blue & Dark Cyan)
          drawCircle(
            brush = Brush.radialGradient(
              colors = listOf(
                Color(0x9900E5FF),
                Color(0x440052D4),
                Color(0x18001F4D),
                Color.Transparent
              ),
              center = center,
              radius = baseRadius * 1.5f * coronaPulse
            ),
            radius = baseRadius * 1.5f * coronaPulse,
            center = center
          )

          // Inner Dark Celestial Core (The Eclipse)
          drawCircle(
            color = Color(0xFF030712),
            radius = baseRadius * 0.92f,
            center = center
          )

          // Glowing Eclipse Rim Ring
          drawCircle(
            brush = Brush.sweepGradient(
              colors = listOf(
                Color(0xFF00E5FF),
                Color(0xFF3B82F6),
                Color(0xFF0052D4),
                Color(0xFF00E5FF)
              ),
              center = center
            ),
            radius = baseRadius * 0.94f,
            center = center,
            style = Stroke(width = 2.8.dp.toPx())
          )

          // Sleek Minimalist Geometric Hexagon Anchor inside Core
          val hexRadius = baseRadius * 0.46f
          val hexPath = androidx.compose.ui.graphics.Path()
          for (i in 0 until 6) {
            val angle = Math.toRadians((i * 60.0) - 30.0)
            val x = center.x + (hexRadius * Math.cos(angle)).toFloat()
            val y = center.y + (hexRadius * Math.sin(angle)).toFloat()
            if (i == 0) hexPath.moveTo(x, y) else hexPath.lineTo(x, y)
          }
          hexPath.close()

          drawPath(
            path = hexPath,
            brush = Brush.verticalGradient(
              listOf(Color(0xFF00E5FF), Color(0xFF0052D4))
            ),
            style = Stroke(width = 2.dp.toPx())
          )
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      // Production Studio Subtitle
      Text(
        text = "THE SUN PRODUCTION",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          letterSpacing = 4.sp,
          fontSize = 12.sp,
          lineHeight = 16.sp,
          fontWeight = FontWeight.SemiBold
        ),
        color = Color(0xFF7DD3FC).copy(alpha = textAlpha),
        modifier = Modifier.alpha(textAlpha)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Subtle Ecosystem Tagline
      Text(
        text = "AEGORA // NEXT-GEN CYBER ENCLAVE",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          letterSpacing = 2.sp,
          fontSize = 9.sp,
          lineHeight = 13.sp,
          fontWeight = FontWeight.Normal
        ),
        color = Color(0xFF475569).copy(alpha = textAlpha),
        modifier = Modifier.alpha(textAlpha)
      )
    }

    // Direct Bypass pill for impatient hackathon judges
    Surface(
      onClick = onSplashFinished,
      shape = RoundedCornerShape(20.dp),
      color = Color(0x330B1728),
      border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0x3338BDF8)),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 36.dp)
        .testTag("skip_splash_button")
    ) {
      Text(
        text = "SKIP INTRO",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        ),
        color = Color(0xFF94A3B8),
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
      )
    }
  }
}
