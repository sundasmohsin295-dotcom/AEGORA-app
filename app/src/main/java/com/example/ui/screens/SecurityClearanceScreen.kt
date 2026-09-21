package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.data.SecurityClearanceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 1. "SECURITY CLEARANCE" ONBOARDING (FIRST-TIME UX)
 *
 * Requirements:
 * - DataStore ensures this screen only appears on the very first app launch.
 * - Deep space background.
 * - Blinking terminal cursor prompts: "ENTER OPERATOR ALIAS".
 * - When the user types their name and hits enter:
 *   - High-tech scanning line sweeps over the text.
 *   - Followed by a heavy, glowing Emerald stamp: "OPERATOR STATUS: VERIFIED".
 * - Smoothly transitions to HomeScreen after a 1.5-second delay.
 */
@Composable
fun SecurityClearanceScreen(
  onClearanceGranted: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptic = LocalHapticFeedback.current
  val coroutineScope = rememberCoroutineScope()
  val repository = remember { SecurityClearanceRepository(context) }

  var operatorAlias by remember { mutableStateOf("") }
  var isScanning by remember { mutableStateOf(false) }
  var isVerified by remember { mutableStateOf(false) }
  var scanProgress by remember { mutableFloatStateOf(0f) }

  // Terminal cursor blink animation
  val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
  val cursorAlpha by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(500, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "cursor_alpha"
  )

  // Starfield background points
  val stars = remember {
    List(60) {
      Offset(Random.nextFloat(), Random.nextFloat())
    }
  }

  // Scan line animation
  LaunchedEffect(isScanning) {
    if (isScanning) {
      val scanAnim = TargetBasedAnimation(
        animationSpec = tween(durationMillis = 1100, easing = LinearOutSlowInEasing),
        typeConverter = Float.VectorConverter,
        initialValue = 0f,
        targetValue = 1f
      )
      val startTime = withFrameNanos { it }
      do {
        val frameTime = withFrameNanos { it }
        val playTime = frameTime - startTime
        scanProgress = scanAnim.getValueFromNanos(playTime)
      } while (!scanAnim.isFinishedFromNanos(playTime))

      // Trigger heavy verified stamp
      haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
      isVerified = true
      isScanning = false

      // Persist to DataStore
      val finalAlias = operatorAlias.trim().ifEmpty { "CIPHER_OPERATOR" }
      repository.recordClearanceVerified(finalAlias)
      AegoraRepository.updateProfile(
        AegoraRepository.userProfile.value.copy(name = finalAlias)
      )

      // 1.5-second cinematic delay before navigating to HomeScreen
      delay(1500)
      onClearanceGranted()
    }
  }

  fun submitClearance() {
    if (isScanning || isVerified) return
    isScanning = true
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF030712)) // Deep space obsidian
      .testTag("security_clearance_screen"),
    contentAlignment = Alignment.Center
  ) {
    // 1. Deep Space Cosmic Starfield Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      // Cosmic radial gradient
      drawRect(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF0B192C),
            Color(0xFF030712)
          ),
          center = Offset(size.width * 0.5f, size.height * 0.35f),
          radius = size.width * 0.9f
        )
      )

      // Stars
      stars.forEach { star ->
        drawCircle(
          color = Color.White.copy(alpha = 0.25f + (star.x * 0.45f)),
          radius = (star.y * 2.2f).coerceAtLeast(0.8f),
          center = Offset(star.x * size.width, star.y * size.height)
        )
      }

      // Tech Grid subtle lines
      val gridSpacing = 40.dp.toPx()
      var x = 0f
      while (x < size.width) {
        drawLine(
          color = Color(0x0822D3EE),
          start = Offset(x, 0f),
          end = Offset(x, size.height),
          strokeWidth = 1f
        )
        x += gridSpacing
      }
    }

    // 2. Main Terminal Panel
    Column(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      // Aegora Department Crest
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0x1A22D3EE),
        border = BorderStroke(1.dp, Color(0x3322D3EE))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = Color(0xFF22D3EE),
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "AEGORA HIGH COMMAND // ENCLAVE INITIALIZATION",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF22D3EE),
            letterSpacing = 1.2.sp
          )
        }
      }

      Text(
        text = "SECURITY CLEARANCE",
        fontSize = 24.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        color = Color(0xFFF0F4F8),
        letterSpacing = 2.sp
      )

      Text(
        text = "INITIAL OPERATOR ONBOARDING PROTOCOL\nIDENTITY HARDWARE ATTESTATION IN PROGRESS",
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        color = Color(0xFF8A919E),
        textAlign = TextAlign.Center,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Terminal Card Container
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, if (isVerified) Color(0xFF10B981) else Color(0xFF1E293B)),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("terminal_clearance_card")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Terminal Titlebar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFEF4444)))
              Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFF59E0B)))
              Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFF10B981)))
            }
            Text(
              text = "tty1 // bash-enc-v9",
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )
          }

          Divider(color = Color(0xFF1E293B), thickness = 1.dp)

          // Prompt & Input Box with Scan Line Overlay
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF090D16), RoundedCornerShape(8.dp))
              .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
              .padding(14.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "> ENTER OPERATOR ALIAS:",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF22D3EE),
                letterSpacing = 1.sp
              )

              // Interactive Text Field with Terminal Cursor
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "$ ",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF34D399)
                )

                BasicTextField(
                  value = operatorAlias,
                  onValueChange = {
                    if (!isScanning && !isVerified) operatorAlias = it
                  },
                  enabled = !isScanning && !isVerified,
                  singleLine = true,
                  textStyle = TextStyle(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  cursorBrush = SolidColor(Color.Transparent),
                  keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                  keyboardActions = KeyboardActions(onDone = { submitClearance() }),
                  modifier = Modifier
                    .weight(1f)
                    .testTag("operator_alias_input"),
                  decorationBox = { innerTextField ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      if (operatorAlias.isEmpty() && !isScanning && !isVerified) {
                        Text(
                          text = "e.g. CIPHER_VIPER",
                          fontFamily = FontFamily.Monospace,
                          fontSize = 14.sp,
                          color = Color(0xFF475569)
                        )
                      }
                      innerTextField()
                      if (!isScanning && !isVerified) {
                        // Blinking cursor
                        Box(
                          modifier = Modifier
                            .width(8.dp)
                            .height(18.dp)
                            .background(Color(0xFF34D399).copy(alpha = cursorAlpha))
                        )
                      }
                    }
                  }
                )
              }
            }

            // High-Tech Scanning Line Sweeping Over Input Box
            if (isScanning) {
              Canvas(modifier = Modifier.matchParentSize()) {
                val lineY = scanProgress * size.height

                // Scanline Beam
                drawLine(
                  brush = Brush.horizontalGradient(
                    colors = listOf(
                      Color.Transparent,
                      Color(0xFF22D3EE),
                      Color(0xFF10B981),
                      Color(0xFF22D3EE),
                      Color.Transparent
                    )
                  ),
                  start = Offset(0f, lineY),
                  end = Offset(size.width, lineY),
                  strokeWidth = 4f
                )

                // Sweep Glow Behind Line
                drawRect(
                  brush = Brush.verticalGradient(
                    colors = listOf(
                      Color(0x3322D3EE),
                      Color.Transparent
                    ),
                    startY = lineY - 30f,
                    endY = lineY
                  ),
                  topLeft = Offset(0f, (lineY - 30f).coerceAtLeast(0f)),
                  size = androidx.compose.ui.geometry.Size(size.width, 30f)
                )
              }
            }
          }

          // Bottom Action Button / Status
          if (!isVerified) {
            Button(
              onClick = { submitClearance() },
              enabled = !isScanning,
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22D3EE),
                contentColor = Color(0xFF030712)
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_verify_clearance")
            ) {
              if (isScanning) {
                CircularProgressIndicator(
                  color = Color(0xFF030712),
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "SCANNING HARDWARE ENCLAVE...",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              } else {
                Text(
                  text = "CONFIRM CLEARANCE CREDENTIALS",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          // Heavy Glowing Emerald Stamp: "OPERATOR STATUS: VERIFIED"
          AnimatedVisibility(
            visible = isVerified,
            enter = fadeIn(tween(250)) + scaleIn(tween(300, easing = OvershootInterpolatorEasing))
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x2210B981), RoundedCornerShape(8.dp))
                .border(2.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp, horizontal = 12.dp)
                .testTag("clearance_verified_stamp"),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF34D399),
                    modifier = Modifier.size(20.dp)
                  )
                  Text(
                    text = "OPERATOR STATUS: VERIFIED",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF34D399),
                    letterSpacing = 1.5.sp
                  )
                }
                Text(
                  text = "CRYPTOGRAPHIC ENCLAVE GRANTED // ENTERING HOMELAB...",
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  color = Color(0xFF6EE7B7)
                )
              }
            }
          }
        }
      }
    }
  }
}

private val OvershootInterpolatorEasing = Easing { fraction ->
  val tension = 2.0f
  val t = fraction - 1.0f
  t * t * ((tension + 1) * t + tension) + 1.0f
}
