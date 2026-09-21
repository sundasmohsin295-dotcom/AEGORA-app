package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MatrixRainCanvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 3. 2FA / EMAIL VERIFICATION OTP SCREEN (CRYPTOGRAPHIC HANDSHAKE)
 *
 * Requirements:
 * - Route here after successful signup.
 * - Displays: "VERIFICATION PROTOCOL INITIATED. CHECK SECURE INBOX."
 * - Custom 6-digit OTP input row (6 distinct monospace boxes).
 * - Slight scale-up spring animation and heavy haptic click on digit entry.
 * - Once 6 digits are entered: Matrix-style processing shimmer before showing "ACCESS GRANTED" biometric handoff.
 */
@Composable
fun EmailVerificationScreen(
  email: String,
  onVerificationComplete: () -> Unit,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val focusRequester = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current

  var otpText by remember { mutableStateOf("") }
  var isProcessing by remember { mutableStateOf(false) }
  var isAccessGranted by remember { mutableStateOf(false) }
  var resendCountdown by remember { mutableIntStateOf(45) }

  // Countdown timer for code resend
  LaunchedEffect(Unit) {
    while (resendCountdown > 0) {
      delay(1000)
      resendCountdown--
    }
  }

  // Auto-focus on entry
  LaunchedEffect(Unit) {
    delay(200)
    focusRequester.requestFocus()
  }

  // Handle 6-digit completion
  LaunchedEffect(otpText) {
    if (otpText.length == 6 && !isProcessing && !isAccessGranted) {
      focusManager.clearFocus()
      isProcessing = true
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)

      // Matrix-style processing shimmer simulation
      delay(1600)
      isProcessing = false
      isAccessGranted = true
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)

      // Biometric handoff delay
      delay(1400)
      onVerificationComplete()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF030712)) // Obsidian space canvas
      .testTag("email_verification_screen"),
    contentAlignment = Alignment.Center
  ) {
    // Background Space & Tech Grid Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawRect(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF030712)
          ),
          center = Offset(size.width * 0.5f, size.height * 0.4f),
          radius = size.width * 0.95f
        )
      )

      val spacing = 36.dp.toPx()
      var x = 0f
      while (x < size.width) {
        drawLine(
          color = Color(0x0622D3EE),
          start = Offset(x, 0f),
          end = Offset(x, size.height),
          strokeWidth = 1f
        )
        x += spacing
      }
    }

    // Top Bar Back Button
    Box(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .padding(16.dp),
      contentAlignment = Alignment.TopStart
    ) {
      IconButton(
        onClick = onNavigateBack,
        modifier = Modifier
          .clip(CircleShape)
          .background(Color(0x1F334155))
          .testTag("otp_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = Color(0xFF94A3B8)
        )
      }
    }

    // Central Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)),
      border = BorderStroke(
        1.dp,
        if (isAccessGranted) Color(0xFF10B981) else if (isProcessing) Color(0xFF22D3EE) else Color(0xFF1E293B)
      ),
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .testTag("otp_container_card")
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
          // Cryptographic Handshake Crest
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(
                if (isAccessGranted) Color(0x2210B981) else Color(0x1A22D3EE)
              )
              .border(
                1.5.dp,
                if (isAccessGranted) Color(0xFF10B981) else Color(0x5522D3EE),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isAccessGranted) Icons.Default.CheckCircle else Icons.Default.MarkEmailRead,
              contentDescription = null,
              tint = if (isAccessGranted) Color(0xFF34D399) else Color(0xFF22D3EE),
              modifier = Modifier.size(28.dp)
            )
          }

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "2FA CRYPTOGRAPHIC HANDSHAKE",
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF22D3EE),
              letterSpacing = 1.2.sp
            )

            Text(
              text = "AUTHENTICATION PROTOCOL",
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = Color(0xFFF1F5F9)
            )

            Text(
              text = "VERIFICATION PROTOCOL INITIATED.\nCHECK SECURE INBOX.",
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF34D399),
              textAlign = TextAlign.Center,
              lineHeight = 16.sp,
              modifier = Modifier.testTag("verification_protocol_label")
            )

            Text(
              text = "Dispatched 6-digit cryptographic token to:\n$email",
              fontSize = 11.sp,
              color = Color(0xFF94A3B8),
              textAlign = TextAlign.Center,
              lineHeight = 15.sp
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // 6-Digit OTP Boxes with Hidden BasicTextField for Accessibility and Native Keyboard Focus
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { focusRequester.requestFocus() },
            contentAlignment = Alignment.Center
          ) {
            // Invisible Input Receiver
            BasicTextField(
              value = otpText,
              onValueChange = { input ->
                val clean = input.filter { it.isDigit() }.take(6)
                if (clean.length > otpText.length) {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                otpText = clean
              },
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
              ),
              keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
              modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester)
                .testTag("otp_hidden_input")
            )

            // Visual 6-Box Row
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("otp_boxes_row"),
              horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
              for (index in 0 until 6) {
                val digit = otpText.getOrNull(index)?.toString() ?: ""
                val isFocusedBox = otpText.length == index && !isProcessing && !isAccessGranted
                val isFilled = digit.isNotEmpty()

                val boxScale by animateFloatAsState(
                  targetValue = if (isFocusedBox) 1.08f else if (isFilled) 1.02f else 1.0f,
                  animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                  label = "otp_box_scale"
                )

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.85f)
                    .scale(boxScale)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                      if (isFilled) Color(0xFF0F1E36) else Color(0xFF080D1A)
                    )
                    .border(
                      width = if (isFocusedBox) 1.8.dp else 1.dp,
                      color = when {
                        isAccessGranted -> Color(0xFF10B981)
                        isFocusedBox -> Color(0xFF22D3EE)
                        isFilled -> Color(0xFF38BDF8)
                        else -> Color(0xFF1E293B)
                      },
                      shape = RoundedCornerShape(8.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = digit,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = if (isAccessGranted) Color(0xFF34D399) else Color(0xFFF8FAFC)
                  )
                }
              }
            }
          }

          // Resend Code or Status
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (resendCountdown > 0) "RESEND AVAILABLE: ${resendCountdown}S" else "CODE EXPIRED",
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              color = Color(0xFF64748B)
            )

            TextButton(
              onClick = {
                resendCountdown = 45
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              },
              enabled = resendCountdown == 0 && !isProcessing && !isAccessGranted,
              contentPadding = PaddingValues(0.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  modifier = Modifier.size(12.dp),
                  tint = if (resendCountdown == 0) Color(0xFF22D3EE) else Color(0xFF475569)
                )
                Text(
                  text = "REQUEST NEW CIPHER",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (resendCountdown == 0) Color(0xFF22D3EE) else Color(0xFF475569)
                )
              }
            }
          }

          // Biometric / Access Granted Handoff Banner
          AnimatedVisibility(
            visible = isAccessGranted,
            enter = fadeIn(tween(250)) + scaleIn(tween(300))
          ) {
            Surface(
              color = Color(0x2210B981),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.5.dp, Color(0xFF10B981)),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("access_granted_banner")
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Fingerprint,
                  contentDescription = null,
                  tint = Color(0xFF34D399),
                  modifier = Modifier.size(24.dp)
                )
                Column {
                  Text(
                    text = "ACCESS GRANTED // BIOMETRIC HANDOFF",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF34D399),
                    letterSpacing = 1.sp
                  )
                  Text(
                    text = "Hardware Enclave Session Provisioned",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8)
                  )
                }
              }
            }
          }
        }

        // Matrix-Style Processing Shimmer Overlay
        if (isProcessing) {
          Box(
            modifier = Modifier
              .matchParentSize()
              .background(Color(0xE6050B14))
              .testTag("otp_matrix_processing_overlay"),
            contentAlignment = Alignment.Center
          ) {
            MatrixRainCanvas(
              modifier = Modifier.fillMaxSize(),
              primaryColor = Color(0xFF22D3EE),
              leadColor = Color(0xFFE0F2FE),
              dropCount = 14
            )

            Surface(
              color = Color(0xDD0B1220),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, Color(0xFF22D3EE))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                CircularProgressIndicator(
                  color = Color(0xFF22D3EE),
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "VERIFYING CRYPTOGRAPHIC TOKEN...",
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF22D3EE)
                )
              }
            }
          }
        }
      }
    }
  }
}
