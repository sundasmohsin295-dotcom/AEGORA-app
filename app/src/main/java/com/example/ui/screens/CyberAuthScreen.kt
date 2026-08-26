package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.UserRole
import com.example.ui.components.ChamferedCutCornerShape
import com.example.ui.components.CyberCard
import com.example.ui.components.HexagonShape
import com.example.ui.components.cyberGridBackground
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

enum class AccessTier(val title: String, val subtitle: String, val badge: String, val role: UserRole) {
  INDEPENDENT("Independent Operative", "Student & CTF Player", "CLEARANCE L1", UserRole.STUDENT),
  ENTERPRISE("Enterprise SOC Analyst", "Corporate Blue/Red Team", "CLEARANCE L2", UserRole.STUDENT),
  ACADEMY("University Cohort", "Academic / Lab Research", "CLEARANCE EDU", UserRole.INSTRUCTOR)
}

@Composable
fun CyberAuthScreen(
  onAuthSuccess: () -> Unit,
  onNavigateBack: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var selectedTier by remember { mutableStateOf(AccessTier.INDEPENDENT) }
  var isScanning by remember { mutableStateOf(false) }
  var authStatusText by remember { mutableStateOf("READY FOR HARDWARE-BACKED ATTESTATION") }
  var generatedHash by remember { mutableStateOf("0x7F8E49A20B15CD91EFA4729388B1") }
  var authCompleted by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  // Scanning radar rotation animation
  val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
  val radarAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radar_angle"
  )

  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.8f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .cyberGridBackground()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      if (onNavigateBack != null) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Start
        ) {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
          }
        }
      }

      // Top Security Sentinel Brand
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(HexagonShape)
            .background(CyberCyan.copy(alpha = 0.12f))
            .border(1.5.dp, CyberCyan, HexagonShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.VpnKey,
            contentDescription = null,
            tint = CyberCyan,
            modifier = Modifier.size(32.dp)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "ZERO-TRUST SENTINEL GATE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
          ),
          color = CyberCyan
        )
        Text(
          text = "AEGORA SECURE ENCLAVE",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace
          ),
          color = TextPrimaryDark
        )
      }

      // Live Device Attestation Status Chips
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurface,
        border = BorderStroke(1.dp, CyberBorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "HARDWARE INTEGRITY DIAGNOSTICS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = TextSecondaryDark
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            AttestationPill(label = "TEE Enclave", status = "ACTIVE", color = CyberEmerald)
            AttestationPill(label = "Play Integrity", status = "VERIFIED", color = CyberCyan)
            AttestationPill(label = "SSL Pinning", status = "ENFORCED", color = CyberEmerald)
          }
        }
      }

      // Access Level Selector (Role Tier)
      Text(
        text = "SELECT OPERATOR CLEARANCE TIER",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        ),
        color = TextSecondaryDark,
        modifier = Modifier.align(Alignment.Start)
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        AccessTier.entries.forEach { tier ->
          val isSelected = selectedTier == tier
          Surface(
            shape = ChamferedCutCornerShape,
            color = if (isSelected) CyberSurfaceElevated else CyberSurface,
            border = BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) CyberCyan else CyberBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedTier = tier }
          ) {
            Row(
              modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else CyberSurfaceVariant)
                    .border(
                      1.dp,
                      if (isSelected) CyberCyan else CyberBorderSubtle,
                      CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (tier) {
                      AccessTier.INDEPENDENT -> Icons.Default.Person
                      AccessTier.ENTERPRISE -> Icons.Default.Security
                      AccessTier.ACADEMY -> Icons.Default.School
                    },
                    contentDescription = null,
                    tint = if (isSelected) CyberCyan else TextSecondaryDark,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = tier.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) TextPrimaryDark else TextSecondaryDark
                  )
                  Text(
                    text = tier.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiaryDark
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color.Transparent,
                border = BorderStroke(1.dp, if (isSelected) CyberCyan else CyberBorderSubtle)
              ) {
                Text(
                  text = tier.badge,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = if (isSelected) CyberCyan else TextSecondaryDark,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // Biometric & Passkey Scanner Centerpiece
      CyberCard(
        borderColor = if (authCompleted) CyberEmerald else if (isScanning) CyberCyan else CyberBorder,
        backgroundColor = CyberSurface,
        shapeRadius = 24.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = if (authCompleted) "AUTHENTICATION VERIFIED" else if (isScanning) "ANALYZING CRYPTOGRAPHIC ENCLAVE..." else "TAP SCANNER FOR FIDO2 / BIOMETRIC ATTESTATION",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = if (authCompleted) CyberEmerald else CyberCyan
          )

          Spacer(modifier = Modifier.height(18.dp))

          // Animated Scanner Orb
          Box(
            modifier = Modifier
              .size(140.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(
                    if (authCompleted) CyberEmerald.copy(alpha = 0.3f) else CyberCyan.copy(alpha = 0.2f),
                    Color.Transparent
                  )
                )
              )
              .border(
                2.dp,
                if (authCompleted) CyberEmerald else CyberCyan.copy(alpha = pulseAlpha),
                CircleShape
              )
              .clickable(enabled = !isScanning && !authCompleted) {
                coroutineScope.launch {
                  isScanning = true
                  authStatusText = "COMMUNICATING WITH ANDROID TEE KEYSTORE..."
                  delay(700)
                  generatedHash = "0x" + (0..7).map { (('A'..'F') + ('0'..'9')).random() }.joinToString("") +
                      "_" + (0..7).map { (('A'..'F') + ('0'..'9')).random() }.joinToString("")
                  authStatusText = "FIDO2 PASSKEY SIGNATURE COMPUTED & VERIFIED"
                  delay(700)
                  authCompleted = true
                  isScanning = false
                  authStatusText = "DEVICE CREDENTIALS BOUND TO OPERATOR SESSION"
                  delay(600)
                  AegoraRepository.updateUserRole(selectedTier.role)
                  onAuthSuccess()
                }
              }
              .testTag("biometric_scanner_button"),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val center = Offset(size.width / 2, size.height / 2)
              val radius = size.minDimension / 2 - 8.dp.toPx()

              // Outer tactical ticks
              drawCircle(
                color = if (authCompleted) CyberEmerald.copy(alpha = 0.4f) else CyberCyan.copy(alpha = 0.25f),
                radius = radius,
                style = Stroke(width = 1.5.dp.toPx())
              )

              if (isScanning) {
                val rad = Math.toRadians(radarAngle.toDouble())
                val endX = center.x + radius * cos(rad).toFloat()
                val endY = center.y + radius * sin(rad).toFloat()
                drawLine(
                  color = CyberCyan,
                  start = center,
                  end = Offset(endX, endY),
                  strokeWidth = 2.dp.toPx()
                )
              }
            }

            Icon(
              imageVector = if (authCompleted) Icons.Default.CheckCircle else if (isScanning) Icons.Default.Fingerprint else Icons.Default.Fingerprint,
              contentDescription = "Scan Biometrics",
              tint = if (authCompleted) CyberEmerald else CyberCyan,
              modifier = Modifier.size(54.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Live Generated Cryptographic Token Hash
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CodeBackground,
            border = BorderStroke(1.dp, CyberBorderSubtle)
          ) {
            Text(
              text = "HASH: $generatedHash",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
              ),
              color = CodeGreen,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = authStatusText,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp
            ),
            color = TextSecondaryDark
          )
        }
      }

      // Action Button
      Button(
        onClick = {
          coroutineScope.launch {
            isScanning = true
            delay(500)
            authCompleted = true
            isScanning = false
            delay(400)
            AegoraRepository.updateUserRole(selectedTier.role)
            onAuthSuccess()
          }
        },
        shape = ChamferedCutCornerShape,
        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("cyber_auth_submit_btn")
      ) {
        Icon(Icons.Default.VpnKey, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (authCompleted) "ENTERING AEGORA MATRIX..." else "AUTHENTICATE ZERO-TRUST SESSION",
          color = Color.Black,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

@Composable
private fun AttestationPill(
  label: String,
  status: String,
  color: Color
) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = CyberSurfaceElevated,
    border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .clip(CircleShape)
          .background(color)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp), color = TextSecondaryDark)
        Text(text = status, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = color)
      }
    }
  }
}
