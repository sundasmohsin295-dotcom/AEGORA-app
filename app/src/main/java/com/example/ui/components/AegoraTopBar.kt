package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkSyncStatus
import com.example.model.PerformanceMode
import com.example.model.UserProfile
import com.example.ui.adaptive.NetworkSyncStatusCapsule
import com.example.ui.theme.*

@Composable
fun AegoraTopBar(
  userProfile: UserProfile,
  syncStatus: NetworkSyncStatus = NetworkSyncStatus.SYNCED,
  performanceMode: PerformanceMode = PerformanceMode.FULL_VISUAL,
  onSearchClick: () -> Unit,
  onNotificationClick: () -> Unit,
  onProfileClick: () -> Unit,
  onSyncClick: () -> Unit = {},
  onPerformanceClick: () -> Unit = {},
  onSubscriptionClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding(),
    color = AegoraBackground,
    tonalElevation = 0.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(AegoraSurface, AegoraBackground)
          )
        )
        .border(
          width = 1.dp,
          brush = Brush.horizontalGradient(
            listOf(AegoraBorderSubtle, AegoraBorder, AegoraBorderSubtle)
          ),
          shape = androidx.compose.ui.graphics.RectangleShape
        )
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      var showOverflowMenu by remember { mutableStateOf(false) }

      // Left: AEGORA Identity & Operator Status
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onProfileClick() }
          .padding(2.dp)
      ) {
        // Hexagonal emblem with glowing cyan stroke & 'V' glyph
        Box(
          modifier = Modifier
            .size(38.dp)
            .testTag("topbar_hex_emblem"),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.size(38.dp)) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
              moveTo(w * 0.5f, 1.dp.toPx())
              lineTo(w - 1.dp.toPx(), h * 0.25f)
              lineTo(w - 1.dp.toPx(), h * 0.75f)
              lineTo(w * 0.5f, h - 1.dp.toPx())
              lineTo(1.dp.toPx(), h * 0.75f)
              lineTo(1.dp.toPx(), h * 0.25f)
              close()
            }
            drawPath(path, color = Color(0x2A00E5FF))
            drawPath(
              path,
              color = Color(0xFF00E5FF),
              style = Stroke(width = 1.8.dp.toPx())
            )
          }
          Text(
            text = "V",
            style = TextStyle(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              color = Color(0xFF00E5FF)
            )
          )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
          Text(
            text = "AEGORA",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 1.5.sp,
              fontSize = 16.sp
            ),
            color = Color.White
          )
          Text(
            text = "VERIFIED OPERATIONS",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp
            ),
            color = Color(0xFF7E99B8)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E676))
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "OPERATOR: ONLINE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 8.sp
              ),
              color = Color(0xFF00E676)
            )
            Text(
              text = " | ",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.5.sp),
              color = Color(0xFF7E99B8)
            )
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E5FF))
            )
            Spacer(modifier = Modifier.width(3.dp))
            val opCallsign = if (userProfile.callsign.isNotBlank() && userProfile.callsign != "Operator") {
              userProfile.callsign.uppercase()
            } else {
              "VANCE-SOC"
            }
            Text(
              text = "$opCallsign • ACTIVE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 8.sp
              ),
              color = Color(0xFF00E5FF),
              maxLines = 1
            )
          }
        }
      }

      // Right: Truthful Runtime Status Pill & Actions
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Truthful Runtime Sync Status Capsule
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = AegoraSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when (syncStatus) {
              NetworkSyncStatus.SYNCED -> SemanticSuccess.copy(alpha = 0.4f)
              NetworkSyncStatus.SYNCING -> SemanticElectricBlue.copy(alpha = 0.4f)
              NetworkSyncStatus.OFFLINE -> AegoraTextTertiary.copy(alpha = 0.3f)
              NetworkSyncStatus.SYNC_ERROR -> SemanticWarning.copy(alpha = 0.4f)
              null -> AegoraBorder
            }
          ),
          modifier = Modifier
            .clickable { onSyncClick() }
            .testTag("topbar_sync_status")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(
                  when (syncStatus) {
                    NetworkSyncStatus.SYNCED -> SemanticSuccess
                    NetworkSyncStatus.SYNCING -> SemanticElectricBlue
                    NetworkSyncStatus.OFFLINE -> AegoraTextTertiary
                    NetworkSyncStatus.SYNC_ERROR -> SemanticWarning
                    null -> AegoraTextTertiary
                  }
                )
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
              text = when (syncStatus) {
                NetworkSyncStatus.SYNCED -> "SYNCED"
                NetworkSyncStatus.SYNCING -> "SYNCING"
                NetworkSyncStatus.OFFLINE -> "OFFLINE"
                NetworkSyncStatus.SYNC_ERROR -> "ERROR"
                null -> "SYNC STATUS UNAVAILABLE"
              },
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.6.sp
              ),
              color = when (syncStatus) {
                NetworkSyncStatus.SYNCED -> SemanticSuccess
                NetworkSyncStatus.SYNCING -> SemanticElectricBlue
                NetworkSyncStatus.OFFLINE -> AegoraTextSecondary
                NetworkSyncStatus.SYNC_ERROR -> SemanticWarning
                null -> AegoraTextSecondary
              }
            )
          }
        }

        // 3-dot overflow menu
        Box {
          IconButton(
            onClick = { showOverflowMenu = !showOverflowMenu },
            modifier = Modifier
              .size(32.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(AegoraSurfaceElevated)
              .border(1.dp, AegoraBorder, RoundedCornerShape(8.dp))
              .testTag("topbar_overflow_menu")
          ) {
            Icon(
              imageVector = Icons.Default.MoreVert,
              contentDescription = "More Options",
              tint = AegoraTextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }

          DropdownMenu(
            expanded = showOverflowMenu,
            onDismissRequest = { showOverflowMenu = false },
            modifier = Modifier.background(AegoraSurfaceElevated).border(1.dp, AegoraBorderHighlight)
          ) {
            DropdownMenuItem(
              text = { Text("Command Palette / Search", color = Color.White, fontSize = 12.sp) },
              leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SemanticElectricBlue, modifier = Modifier.size(16.dp)) },
              onClick = {
                showOverflowMenu = false
                onSearchClick()
              }
            )
            DropdownMenuItem(
              text = { Text("Performance Mode", color = Color.White, fontSize = 12.sp) },
              leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, tint = SemanticWarning, modifier = Modifier.size(16.dp)) },
              onClick = {
                showOverflowMenu = false
                onPerformanceClick()
              }
            )
            DropdownMenuItem(
              text = { Text("Clearance Upgrade", color = Color.White, fontSize = 12.sp) },
              leadingIcon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = SemanticWarning, modifier = Modifier.size(16.dp)) },
              onClick = {
                showOverflowMenu = false
                onSubscriptionClick()
              }
            )
            DropdownMenuItem(
              text = { Text("Notifications (2)", color = Color.White, fontSize = 12.sp) },
              leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null, tint = SemanticElectricBlue, modifier = Modifier.size(16.dp)) },
              onClick = {
                showOverflowMenu = false
                onNotificationClick()
              }
            )
          }
        }

        // Search trigger
        IconButton(
          onClick = onSearchClick,
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AegoraSurfaceElevated)
            .border(1.dp, AegoraBorder, RoundedCornerShape(8.dp))
            .testTag("topbar_search_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Command Palette",
            tint = AegoraTextSecondary,
            modifier = Modifier.size(16.dp)
          )
        }

        // Performance Mode Toggle button
        IconButton(
          onClick = onPerformanceClick,
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AegoraSurfaceElevated)
            .border(1.dp, AegoraBorder, RoundedCornerShape(8.dp))
            .testTag("topbar_performance_button")
        ) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = "Performance Mode",
            tint = if (performanceMode == PerformanceMode.FULL_VISUAL) SemanticElectricBlue else SemanticWarning,
            modifier = Modifier.size(16.dp)
          )
        }

        // Subscription / Clearance Upgrade Button
        IconButton(
          onClick = onSubscriptionClick,
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AegoraSurfaceElevated)
            .border(1.dp, AegoraBorder, RoundedCornerShape(8.dp))
            .testTag("topbar_subscription_button")
        ) {
          Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = "Clearance Subscription",
            tint = SemanticWarning,
            modifier = Modifier.size(16.dp)
          )
        }

        // Notifications
        IconButton(
          onClick = onNotificationClick,
          modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(AegoraSurfaceElevated)
            .border(1.dp, AegoraBorder, RoundedCornerShape(8.dp))
            .testTag("topbar_notifications_button")
        ) {
          BadgedBox(badge = {
            Badge(containerColor = SemanticElectricBlue) {
              Text("2", color = Color.White, fontSize = 8.sp)
            }
          }) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Notifications",
              tint = AegoraTextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}


