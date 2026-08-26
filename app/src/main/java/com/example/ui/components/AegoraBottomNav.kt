package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class AegoraNavTab(val label: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
  RADAR("Home", Icons.Filled.Radar, Icons.Outlined.Radar),
  JOURNEY("Journey", Icons.Filled.Timeline, Icons.Outlined.Timeline),
  LABS("Labs", Icons.Filled.Terminal, Icons.Outlined.Terminal),
  AI_MENTOR("AI Mentor", Icons.Filled.Psychology, Icons.Outlined.Psychology),
  PASSPORT("Passport", Icons.Filled.VerifiedUser, Icons.Outlined.VerifiedUser)
}

@Composable
fun AegoraBottomNav(
  currentTab: AegoraNavTab,
  onTabSelected: (AegoraNavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars),
    color = CyberSurface,
    tonalElevation = 4.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 6.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      AegoraNavTab.entries.forEach { tab ->
        val isSelected = tab == currentTab
        val iconColor by animateColorAsState(
          targetValue = if (isSelected) CyberCyan else TextSecondaryDark,
          label = "nav_icon_color"
        )

        Column(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onTabSelected(tab) }
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .testTag("nav_tab_${tab.name.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .height(32.dp)
              .width(52.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(if (isSelected) CyberCyan.copy(alpha = 0.18f) else Color.Transparent)
              .border(
                1.dp,
                if (isSelected) CyberCyan.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(16.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
              contentDescription = tab.label,
              tint = iconColor,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) CyberCyan else TextSecondaryDark
          )
        }
      }
    }
  }
}

