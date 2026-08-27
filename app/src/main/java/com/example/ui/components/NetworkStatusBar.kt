package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkSyncStatus
import com.example.ui.theme.*

/**
 * Animated network status banner that appears at the top of screens when offline or syncing,
 * assuring the user that offline local caching is active and their work is safe.
 */
@Composable
fun NetworkStatusBar(
  syncStatus: NetworkSyncStatus,
  onSyncClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = syncStatus == NetworkSyncStatus.OFFLINE || syncStatus == NetworkSyncStatus.SYNCING || syncStatus == NetworkSyncStatus.SYNC_ERROR,
    enter = expandVertically() + fadeIn(),
    exit = shrinkVertically() + fadeOut(),
    modifier = modifier.fillMaxWidth()
  ) {
    val (bgGradient, borderColor, contentColor, icon, message, actionLabel) = when (syncStatus) {
      NetworkSyncStatus.OFFLINE -> Tuple6(
        Color(0xFF261800),
        TerminalAmber,
        TerminalAmber,
        Icons.Default.CloudOff,
        "OFFLINE MODE ACTIVE — Local Cache Protected. Lessons & Notes available.",
        "Retry Sync"
      )
      NetworkSyncStatus.SYNCING -> Tuple6(
        Color(0xFF002233),
        NeonCyan,
        NeonCyan,
        Icons.Default.Sync,
        "SYNCHRONIZING CYBER TWIN — Uploading local telemetry & progress...",
        null
      )
      NetworkSyncStatus.SYNC_ERROR -> Tuple6(
        Color(0xFF330011),
        NeonCrimson,
        NeonCrimson,
        Icons.Default.Warning,
        "SYNC CONFLICT DETECTED — Review cloud state changes.",
        "Resolve"
      )
      NetworkSyncStatus.SYNCED -> Tuple6(
        Color(0xFF002211),
        NeonEmerald,
        NeonEmerald,
        Icons.Default.Sync,
        "Cloud Synchronized",
        null
      )
    }

    Surface(
      color = bgGradient,
      border = androidx.compose.foundation.BorderStroke(1.dp, borderColor.copy(alpha = 0.6f)),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp)
        .testTag("network_status_banner")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = message,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp,
              lineHeight = 14.sp
            ),
            color = contentColor
          )
        }

        if (actionLabel != null) {
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = contentColor.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, contentColor),
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .clickable { onSyncClick() }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = contentColor
              )
            }
          }
        }
      }
    }
  }
}

private data class Tuple6<A, B, C, D, E, F>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F
)
