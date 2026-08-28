package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.AmbientObservation
import com.example.ui.theme.*

@Composable
fun AmbientCoPilotSurface(
  onAskAi: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val activeObservation by AegoraRepository.activeAmbientObservation.collectAsState()

  AnimatedVisibility(
    visible = activeObservation != null,
    enter = fadeIn() + slideInVertically { it / 2 },
    exit = fadeOut() + slideOutVertically { it / 2 },
    modifier = modifier
  ) {
    activeObservation?.let { obs ->
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = CyberSurfaceElevated.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, CyberIndigo.copy(alpha = 0.6f)),
        shadowElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .testTag("ambient_copilot_surface")
      ) {
        Column(
          modifier = Modifier
            .background(
              Brush.horizontalGradient(
                listOf(
                  CyberIndigo.copy(alpha = 0.15f),
                  CyberBackground.copy(alpha = 0.85f)
                )
              )
            )
            .padding(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(CyberIndigo.copy(alpha = 0.3f))
                  .border(1.dp, CyberCyan, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = CyberCyan,
                  modifier = Modifier.size(14.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "AMBIENT CO-PILOT",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberCyan
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberSurface,
                border = BorderStroke(0.5.dp, CyberBorder)
              ) {
                Text(
                  text = obs.groundingSource,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = TextSecondaryDark,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }

            IconButton(
              onClick = { AegoraRepository.dismissAmbientObservation() },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = TextSecondaryDark,
                modifier = Modifier.size(16.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = obs.observationText,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Tap to consult mentor →",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CyberCyan
              ),
              modifier = Modifier
                .clickable { onAskAi(obs.suggestedPrompt) }
                .padding(vertical = 4.dp, horizontal = 6.dp)
            )
          }
        }
      }
    }
  }
}
