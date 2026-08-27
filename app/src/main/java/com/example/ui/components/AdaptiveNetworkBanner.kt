package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkSyncStatus
import com.example.network.NetworkStatus
import com.example.ui.theme.*

/**
 * Screen position placement for the adaptive network banner.
 */
enum class BannerPosition {
  TOP,
  BOTTOM
}

/**
 * AdaptiveNetworkBanner composable that dynamically presents network connectivity states
 * ('OFFLINE', 'SYNCING', 'SYNC_ERROR') at the top or bottom of the screen.
 *
 * It strictly adheres to Android Safe Area guidelines by incorporating WindowInsets
 * (status bars, display cutouts, and navigation bars) to ensure visibility across phones,
 * foldables, tablets, and desktop orientations without overlapping system UI or notch areas.
 *
 * @param syncStatus The current synchronization and connectivity state.
 * @param position Placement at either [BannerPosition.TOP] or [BannerPosition.BOTTOM].
 * @param onActionClick Optional callback when user taps retry/action on the banner.
 * @param respectSafeAreaInsets When true, automatically applies safe-area padding for cutouts/bars.
 * @param modifier Additional Compose modifier.
 */
@Composable
fun AdaptiveNetworkBanner(
  syncStatus: NetworkSyncStatus,
  modifier: Modifier = Modifier,
  position: BannerPosition = BannerPosition.TOP,
  onActionClick: () -> Unit = {},
  respectSafeAreaInsets: Boolean = true
) {
  val isVisible = syncStatus == NetworkSyncStatus.OFFLINE ||
      syncStatus == NetworkSyncStatus.SYNCING ||
      syncStatus == NetworkSyncStatus.SYNC_ERROR

  // Continuous rotation for active sync icon
  val infiniteTransition = rememberInfiniteTransition(label = "SyncRotation")
  val syncRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "SyncRotationAngle"
  )

  // Pulsing alpha for offline status dot
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "PulseAlpha"
  )

  AnimatedVisibility(
    visible = isVisible,
    enter = if (position == BannerPosition.TOP) {
      slideInVertically(initialOffsetY = { -it }) + expandVertically() + fadeIn()
    } else {
      slideInVertically(initialOffsetY = { it }) + expandVertically() + fadeIn()
    },
    exit = if (position == BannerPosition.TOP) {
      slideOutVertically(targetOffsetY = { -it }) + shrinkVertically() + fadeOut()
    } else {
      slideOutVertically(targetOffsetY = { it }) + shrinkVertically() + fadeOut()
    },
    modifier = modifier.fillMaxWidth()
  ) {
    val insetsModifier = if (respectSafeAreaInsets) {
      if (position == BannerPosition.TOP) {
        Modifier.windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.displayCutout))
      } else {
        Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.displayCutout))
      }
    } else {
      Modifier
    }

    val (bgGradient, borderColor, contentColor, icon, title, subtitle, actionLabel) = when (syncStatus) {
      NetworkSyncStatus.OFFLINE -> BannerData(
        backgroundColor = Color(0xFF1E1405),
        borderColor = TerminalAmber,
        contentColor = TerminalAmber,
        icon = Icons.Default.CloudOff,
        title = "OFFLINE MODE ACTIVE",
        subtitle = "Local cache active. Lessons, notes & telemetry safely stored locally.",
        actionLabel = "Retry Sync"
      )
      NetworkSyncStatus.SYNCING -> BannerData(
        backgroundColor = Color(0xFF041824),
        borderColor = NeonCyan,
        contentColor = NeonCyan,
        icon = Icons.Default.Sync,
        title = "SYNCHRONIZING CYBER TWIN",
        subtitle = "Uploading local telemetry & reconciling cloud progress...",
        actionLabel = null
      )
      NetworkSyncStatus.SYNC_ERROR -> BannerData(
        backgroundColor = Color(0xFF26050C),
        borderColor = NeonCrimson,
        contentColor = NeonCrimson,
        icon = Icons.Default.Warning,
        title = "TELEMETRY SYNC CONFLICT",
        subtitle = "A sync dispute occurred. Tap resolve to verify cloud state.",
        actionLabel = "Resolve"
      )
      NetworkSyncStatus.SYNCED -> BannerData(
        backgroundColor = Color(0xFF031A0F),
        borderColor = NeonEmerald,
        contentColor = NeonEmerald,
        icon = Icons.Default.Sync,
        title = "SYSTEM ONLINE",
        subtitle = "All telemetry verified.",
        actionLabel = null
      )
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .then(insetsModifier)
        .padding(horizontal = 12.dp, vertical = 6.dp),
      contentAlignment = if (position == BannerPosition.TOP) Alignment.TopCenter else Alignment.BottomCenter
    ) {
      Surface(
        color = bgGradient,
        border = BorderStroke(1.2.dp, borderColor.copy(alpha = 0.75f)),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 680.dp) // Adaptive layout constraint for foldables/tablets/desktop
          .testTag("adaptive_network_banner")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Left: Animated Status Indicator Icon + Text Column
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(contentColor.copy(alpha = 0.15f))
                .border(1.dp, contentColor.copy(alpha = 0.4f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              if (syncStatus == NetworkSyncStatus.SYNCING) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = contentColor,
                  modifier = Modifier
                    .size(16.dp)
                    .rotate(syncRotation)
                )
              } else {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = contentColor,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                // Pulsing dot indicator
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = pulseAlpha))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = title,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.6.sp
                  ),
                  color = contentColor
                )
              }

              Spacer(modifier = Modifier.height(2.dp))

              Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 10.5.sp,
                  lineHeight = 13.sp
                ),
                color = TextSecondaryDark,
                maxLines = 2
              )
            }
          }

          // Right: Action Button (e.g. Retry Sync / Resolve)
          if (actionLabel != null) {
            Spacer(modifier = Modifier.width(10.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = contentColor.copy(alpha = 0.18f),
              border = BorderStroke(1.dp, contentColor.copy(alpha = 0.8f)),
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { onActionClick() }
                .testTag("network_retry_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
}

/**
 * Overload for [AdaptiveNetworkBanner] accepting [NetworkStatus].
 */
@Composable
fun AdaptiveNetworkBanner(
  networkStatus: NetworkStatus,
  modifier: Modifier = Modifier,
  position: BannerPosition = BannerPosition.TOP,
  onActionClick: () -> Unit = {},
  respectSafeAreaInsets: Boolean = true
) {
  val syncStatus = when (networkStatus) {
    is NetworkStatus.Online -> NetworkSyncStatus.SYNCED
    is NetworkStatus.Offline -> NetworkSyncStatus.OFFLINE
    is NetworkStatus.Connecting -> NetworkSyncStatus.SYNCING
  }

  AdaptiveNetworkBanner(
    syncStatus = syncStatus,
    modifier = modifier,
    position = position,
    onActionClick = onActionClick,
    respectSafeAreaInsets = respectSafeAreaInsets
  )
}

private data class BannerData(
  val backgroundColor: Color,
  val borderColor: Color,
  val contentColor: Color,
  val icon: androidx.compose.ui.graphics.vector.ImageVector,
  val title: String,
  val subtitle: String,
  val actionLabel: String?
)
