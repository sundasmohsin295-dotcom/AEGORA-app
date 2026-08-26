package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun AegoraTopBar(
  userProfile: UserProfile,
  onSearchClick: () -> Unit,
  onNotificationClick: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding(),
    color = CyberBackground,
    tonalElevation = 0.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Logo & User Greeting
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { onProfileClick() }
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(HexagonShape)
            .background(CyberCyan.copy(alpha = 0.15f))
            .border(1.2.dp, CyberCyan, HexagonShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = userProfile.callsign.take(1).uppercase(),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            ),
            color = CyberCyan
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = "Hello, ${userProfile.callsign}",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold
            ),
            color = TextPrimaryDark
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(CyberEmerald)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "AEGORA ONLINE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
              ),
              color = CyberEmerald
            )
          }
        }
      }

      // Right: Stats & Actions
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Streak Chip
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = CyberSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = "Streak",
              tint = CyberAmber,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "${userProfile.currentStreak}d",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              color = CyberAmber
            )
          }
        }

        // XP Chip
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = CyberSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = "XP",
              tint = CyberCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "${userProfile.xp}",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              ),
              color = CyberCyan
            )
          }
        }

        // Universal Search Button
        IconButton(
          onClick = onSearchClick,
          modifier = Modifier
            .size(36.dp)
            .testTag("topbar_search_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Universal Search",
            tint = TextPrimaryDark,
            modifier = Modifier.size(20.dp)
          )
        }

        // Notifications
        IconButton(
          onClick = onNotificationClick,
          modifier = Modifier
            .size(36.dp)
            .testTag("topbar_notifications_button")
        ) {
          BadgedBox(badge = {
            Badge(containerColor = CyberCrimson) {
              Text("2", color = Color.White, fontSize = 9.sp)
            }
          }) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Notifications",
              tint = TextPrimaryDark,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }
}

