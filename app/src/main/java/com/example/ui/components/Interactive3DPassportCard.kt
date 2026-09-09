package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

/**
 * 3D Interactive Skill Passport Card.
 * Tilts subtly (X: -4 to +4 deg, Y: -4 to +4 deg) on pointer touch and springs back to neutral.
 * Avoids heavy shadows, uses 1dp crisp technical borders, controlled translucency, and digital provenance metadata.
 */
@Composable
fun Interactive3DPassportCard(
  targetRole: String,
  readinessScore: Int,
  verifiedCapabilitiesCount: Int,
  evidenceProofsCount: Int,
  lastVerifiedDate: String,
  verificationStatus: String,
  onVerifyPassport: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var dragOffsetX by remember { mutableFloatStateOf(0f) }
  var dragOffsetY by remember { mutableFloatStateOf(0f) }

  val animatedRotationY by animateFloatAsState(
    targetValue = (dragOffsetX / 100f).coerceIn(-4f, 4f),
    animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
    label = "passport_rot_y"
  )

  val animatedRotationX by animateFloatAsState(
    targetValue = (-dragOffsetY / 100f).coerceIn(-4f, 4f),
    animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
    label = "passport_rot_x"
  )

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = AegoraSurface),
    border = BorderStroke(1.dp, AegoraCyanVerified.copy(alpha = 0.5f)),
    modifier = modifier
      .fillMaxWidth()
      .graphicsLayer {
        rotationX = animatedRotationX
        rotationY = animatedRotationY
        cameraDistance = 14f * density
      }
      .pointerInput(Unit) {
        detectDragGestures(
          onDrag = { change, dragAmount ->
            change.consume()
            dragOffsetX += dragAmount.x
            dragOffsetY += dragAmount.y
          },
          onDragEnd = {
            dragOffsetX = 0f
            dragOffsetY = 0f
          },
          onDragCancel = {
            dragOffsetX = 0f
            dragOffsetY = 0f
          }
        )
      }
      .testTag("interactive_3d_passport_card")
  ) {
    Column(
      modifier = Modifier
        .background(
          Brush.linearGradient(
            colors = listOf(
              AegoraCyanVerified.copy(alpha = 0.07f),
              AegoraSurface,
              AegoraSurfaceElevated
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
          )
        )
        .padding(18.dp)
    ) {
      // Passport Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(AegoraCyanVerified.copy(alpha = 0.15f))
              .border(1.dp, AegoraCyanVerified, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = "Verified Passport",
              tint = AegoraCyanVerified,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "AEGORA // SKILL PASSPORT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = AegoraCyanVerified
            )
            Text(
              text = "VERIFIED OPERATIONAL CREDENTIAL",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp
              ),
              color = AegoraTextSecondary
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(4.dp),
          color = AegoraCyanVerified.copy(alpha = 0.12f),
          border = BorderStroke(1.dp, AegoraCyanVerified)
        ) {
          Text(
            text = verificationStatus.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = AegoraCyanVerified,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Target Role & Readiness
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = "TARGET ROLE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            ),
            color = AegoraTextSecondary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = targetRole,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold
            ),
            color = AegoraTextPrimary
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "READINESS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            ),
            color = AegoraTextSecondary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$readinessScore%",
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 22.sp
            ),
            color = AegoraCyanVerified
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Provenance Metrics Bar
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = AegoraSurfaceElevated,
        border = BorderStroke(1.dp, AegoraBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          MetricItem(title = "VERIFIED SKILLS", value = "$verifiedCapabilitiesCount CAPABILITIES")
          MetricItem(title = "EVIDENCE CHAIN", value = "$evidenceProofsCount PROOFS")
          MetricItem(title = "LAST VERIFIED", value = lastVerifiedDate)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Chain Verification Provenance Footer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Link,
            contentDescription = null,
            tint = AegoraCyanVerified,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "SERVER VERIFIED • SHA-256 PROVENANCE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 8.5.sp,
              fontWeight = FontWeight.SemiBold
            ),
            color = AegoraTextSecondary
          )
        }

        OutlinedButton(
          onClick = onVerifyPassport,
          shape = RoundedCornerShape(6.dp),
          border = BorderStroke(1.dp, AegoraCyanVerified),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = AegoraCyanVerified),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.testTag("btn_verify_passport_chain")
        ) {
          Text(
            text = "VERIFY",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            )
          )
        }
      }
    }
  }
}

@Composable
private fun MetricItem(title: String, value: String) {
  Column {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold
      ),
      color = AegoraTextSecondary
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      ),
      color = AegoraTextPrimary
    )
  }
}
