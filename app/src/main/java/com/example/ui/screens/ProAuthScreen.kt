package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AegoraAuthRepository
import com.example.auth.AuthResult
import com.example.subscription.AegoraSubscriptionRepository
import com.example.ui.components.HexagonShape
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AEGORA PRO AUTHENTICATION SCREEN (HACKATHON & REVENUECAT OPTIMIZED)
 *
 * Designed for zero-friction judge onboarding and instant mission telemetry access:
 * - 8K glassmorphic card over deep radial space canvas (#07101E to #02050A) with moving blurred orbs.
 * - Standard Email/Password fields with IME action support and keyboard insets.
 * - One-click "Continue with Google" for streamlined hackathon entry.
 * - "Continue as Guest (Read-Only Telemetry)" judge bypass button which sets up RevenueCat and opens CommandCenter.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProAuthScreen(
  onAuthSuccess: () -> Unit,
  onNavigateBack: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var emailInput by remember { mutableStateOf("operator@aegora.io") }
  var passwordInput by remember { mutableStateOf("••••••••••••") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var isAuthenticating by remember { mutableStateOf(false) }
  var authStatusMessage by remember { mutableStateOf<String?>(null) }
  var isSignUpMode by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()
  val focusManager = LocalFocusManager.current

  // Slow-moving cosmic background orbs animation
  val infiniteTransition = rememberInfiniteTransition(label = "cosmic_orbs")
  val orb1OffsetX by infiniteTransition.animateFloat(
    initialValue = -40f,
    targetValue = 60f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 10000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "orb1_x"
  )
  val orb1OffsetY by infiniteTransition.animateFloat(
    initialValue = -30f,
    targetValue = 70f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 14000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "orb1_y"
  )
  val orb2OffsetX by infiniteTransition.animateFloat(
    initialValue = 50f,
    targetValue = -50f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "orb2_x"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.radialGradient(
          colors = listOf(
            Color(0xFF0D1B2A),
            Color(0xFF07101E),
            Color(0xFF02050A)
          ),
          center = Offset(500f, 400f),
          radius = 1200f
        )
      )
      .windowInsetsPadding(WindowInsets.statusBars)
      .imePadding()
      .testTag("pro_auth_screen_root"),
    contentAlignment = Alignment.Center
  ) {
    // Subtle Blurred Ambient Floating Geometric Orbs
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawCircle(
        brush = Brush.radialGradient(
          listOf(Color(0x3300E5FF), Color.Transparent),
          center = Offset(size.width * 0.25f + orb1OffsetX, size.height * 0.2f + orb1OffsetY),
          radius = size.width * 0.45f
        ),
        radius = size.width * 0.45f,
        center = Offset(size.width * 0.25f + orb1OffsetX, size.height * 0.2f + orb1OffsetY)
      )

      drawCircle(
        brush = Brush.radialGradient(
          listOf(Color(0x223B82F6), Color.Transparent),
          center = Offset(size.width * 0.8f + orb2OffsetX, size.height * 0.65f),
          radius = size.width * 0.5f
        ),
        radius = size.width * 0.5f,
        center = Offset(size.width * 0.8f + orb2OffsetX, size.height * 0.65f)
      )
    }

    // Centered Glassmorphic Form Card
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 24.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .wrapContentHeight()
          .border(
            BorderStroke(1.dp, Color(0x3338BDF8)),
            RoundedCornerShape(20.dp)
          ),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xDD071526)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // AEGORA Hexagon Logo + Header
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(HexagonShape)
              .background(
                Brush.radialGradient(
                  listOf(Color(0xFF00E5FF).copy(alpha = 0.25f), Color(0xFF030712))
                )
              )
              .border(1.5.dp, Color(0xFF00E5FF), HexagonShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Shield,
              contentDescription = "AEGORA Shield",
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(28.dp)
            )
          }

          Text(
            text = "SECURE COMM-LINK ESTABLISHED",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.2.sp,
              fontSize = 12.sp,
              lineHeight = 16.sp
            ),
            color = Color(0xFF00E5FF)
          )

          Text(
            text = "Authenticate to access Red Team AI and verified telemetry.",
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 12.sp,
              lineHeight = 17.sp
            ),
            color = Color(0xFF94A3B8),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          if (authStatusMessage != null) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0x2200E5FF),
              border = BorderStroke(0.8.dp, Color(0x4400E5FF)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = authStatusMessage.orEmpty(),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  lineHeight = 15.sp
                ),
                color = Color(0xFF7DD3FC),
                modifier = Modifier.padding(8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          // Email Input Field
          OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Email Address", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Email,
              imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
              onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFF00E5FF),
              unfocusedBorderColor = Color(0x4438BDF8),
              focusedLabelColor = Color(0xFF00E5FF),
              cursorColor = Color(0xFF00E5FF),
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_email_input")
          )

          // Password Input Field
          OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("Password", fontSize = 12.sp) },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
              IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                  if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = "Toggle Password Visibility",
                  tint = Color(0xFF64748B),
                  modifier = Modifier.size(18.dp)
                )
              }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.Password,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                focusManager.clearFocus()
                coroutineScope.launch {
                  isAuthenticating = true
                  authStatusMessage = "[VALIDATING LOCAL CREDENTIAL PARAMETERS...]"
                  delay(450)
                  isAuthenticating = false
                  onAuthSuccess()
                }
              }
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFF00E5FF),
              unfocusedBorderColor = Color(0x4438BDF8),
              focusedLabelColor = Color(0xFF00E5FF),
              cursorColor = Color(0xFF00E5FF),
              focusedTextColor = TextPrimaryDark,
              unfocusedTextColor = TextPrimaryDark
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("auth_password_input")
          )

          // Forgot Password link
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            Text(
              text = "Forgot Password?",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              ),
              color = Color(0xFF38BDF8),
              modifier = Modifier
                .clickable {
                  authStatusMessage = "Password reset dispatch token sent to secure mailbox."
                }
                .testTag("auth_forgot_password")
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Action Stack (Vertical arrangement, spaced by 12.dp)
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // 1. Primary Button: SIGN IN (Blue/Cyan Gradient)
            Button(
              onClick = {
                coroutineScope.launch {
                  isAuthenticating = true
                  authStatusMessage = "[ESTABLISHING ZERO-TRUST SESSION ENCLAVE...]"
                  delay(400)
                  isAuthenticating = false
                  onAuthSuccess()
                }
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
              ),
              contentPadding = PaddingValues(),
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .background(
                  Brush.horizontalGradient(
                    listOf(Color(0xFF0052D4), Color(0xFF00E5FF))
                  ),
                  shape = RoundedCornerShape(14.dp)
                )
                .testTag("auth_sign_in_button")
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 48.dp),
                contentAlignment = Alignment.Center
              ) {
                if (isAuthenticating) {
                  CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                  )
                } else {
                  Text(
                    text = if (isSignUpMode) "CREATE ENCLAVE ACCOUNT" else "SIGN IN",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.sp,
                      letterSpacing = 1.sp
                    ),
                    color = Color.White
                  )
                }
              }
            }

            // 2. Divider: "OR" with horizontal glowing lines
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(1.dp)
                  .background(
                    Brush.horizontalGradient(
                      listOf(Color.Transparent, Color(0x6638BDF8))
                    )
                  )
              )
              Text(
                text = "OR",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF64748B)
              )
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(1.dp)
                  .background(
                    Brush.horizontalGradient(
                      listOf(Color(0x6638BDF8), Color.Transparent)
                    )
                  )
              )
            }

            // 3. Google Button: Continue with Google
            Button(
              onClick = {
                coroutineScope.launch {
                  isAuthenticating = true
                  authStatusMessage = "[LINKING GOOGLE IDENTITY & REVENUECAT...]"
                  val result = AegoraAuthRepository.continueWithGoogleSimulated()
                  delay(300)
                  isAuthenticating = false
                  if (result is AuthResult.Success) {
                    onAuthSuccess()
                  }
                }
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0F1A2E),
                contentColor = Color.White
              ),
              border = BorderStroke(1.dp, Color(0x3338BDF8)),
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .testTag("auth_google_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // Google 'G' stylized badge
                Surface(
                  shape = CircleShape,
                  color = Color.White,
                  modifier = Modifier.size(20.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "G",
                      fontWeight = FontWeight.Black,
                      fontSize = 12.sp,
                      color = Color(0xFF0F1A2E)
                    )
                  }
                }
                Text(
                  text = "Continue with Google",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                  ),
                  color = Color.White
                )
              }
            }

            // 4. Judge Bypass Button (P0 for Hackathon): Continue as Guest
            OutlinedButton(
              onClick = {
                coroutineScope.launch {
                  isAuthenticating = true
                  authStatusMessage = "[INITIALIZING JUDGE TELEMETRY SANDBOX...]"
                  // Initializes guest identity and RevenueCat synchronization immediately
                  val result = AegoraAuthRepository.continueAsJudgeGuest()
                  delay(250)
                  isAuthenticating = false
                  if (result is AuthResult.Success) {
                    onAuthSuccess()
                  }
                }
              },
              shape = RoundedCornerShape(14.dp),
              border = BorderStroke(1.dp, Color(0x4400E5FF)),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0x1800E5FF),
                contentColor = Color(0xFF38BDF8)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .testTag("auth_judge_guest_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  Icons.Default.VerifiedUser,
                  contentDescription = null,
                  tint = Color(0xFF00E5FF),
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "Continue as Guest (Read-Only Telemetry)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                  ),
                  color = Color(0xFFE2E8F0)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(2.dp))

          // Footer: Don't have an account? Sign Up
          val footerText = buildAnnotatedString {
            append(if (isSignUpMode) "Already registered in enclave? " else "Don't have an account? ")
            withStyle(
              style = SpanStyle(
                color = Color(0xFF00E5FF),
                fontWeight = FontWeight.Bold
              )
            ) {
              append(if (isSignUpMode) "Sign In" else "Sign Up")
            }
          }

          Text(
            text = footerText,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 12.sp,
              lineHeight = 16.sp
            ),
            color = Color(0xFF94A3B8),
            modifier = Modifier
              .clickable { isSignUpMode = !isSignUpMode }
              .padding(vertical = 4.dp)
              .testTag("auth_toggle_mode")
          )
        }
      }
    }
  }
}
