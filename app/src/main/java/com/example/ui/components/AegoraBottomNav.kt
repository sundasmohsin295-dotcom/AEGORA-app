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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

val NavActiveCyan = Color(0xFF22D3EE)
val NavMutedSlate = Color(0xFF8CA3C7)

enum class AegoraNavTab(
  val label: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector,
  val color: Color = NavActiveCyan
) {
  HOME("Home", Icons.Filled.Shield, Icons.Outlined.Shield),
  LEARN("Learn", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
  OPERATE("Operate", Icons.Filled.Terminal, Icons.Outlined.Terminal),
  INTELLIGENCE("Intelligence", Icons.Filled.Psychology, Icons.Outlined.Psychology),
  PROOF("Proof", Icons.Filled.VerifiedUser, Icons.Outlined.VerifiedUser),
  TOOLS("Tools", Icons.Filled.Build, Icons.Outlined.Build),
  ACCOUNT("Account", Icons.Filled.Person, Icons.Outlined.Person);

  companion object {
    val RADAR get() = HOME
    val JOURNEY get() = LEARN
    val LABS get() = OPERATE
    val AI_MENTOR get() = INTELLIGENCE
    val PASSPORT get() = PROOF
  }
}

@Composable
fun AegoraBottomNav(
  currentTab: AegoraNavTab,
  onTabSelected: (AegoraNavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  var lastTabClickTime by remember { mutableLongStateOf(0L) }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars),
    color = AegoraSurface,
    tonalElevation = 4.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, AegoraBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      AegoraNavTab.entries.forEach { tab ->
        val isSelected = tab == currentTab
        val activeColor = NavActiveCyan
        val inactiveColor = NavMutedSlate
        val iconColor by animateColorAsState(
          targetValue = if (isSelected) activeColor else inactiveColor,
          label = "nav_icon_color"
        )

        Column(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              val now = System.currentTimeMillis()
              if (now - lastTabClickTime > 250L && tab != currentTab) {
                lastTabClickTime = now
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onTabSelected(tab)
              }
            }
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .testTag("nav_tab_${tab.name.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .height(28.dp)
              .width(42.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) activeColor.copy(alpha = 0.12f) else Color.Transparent)
              .border(
                0.8.dp,
                if (isSelected) activeColor.copy(alpha = 0.35f) else Color.Transparent,
                RoundedCornerShape(14.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
              contentDescription = tab.label,
              tint = iconColor,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 9.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              letterSpacing = 0.2.sp
            ),
            color = if (isSelected) activeColor else inactiveColor
          )

          Spacer(modifier = Modifier.height(2.dp))

          // Subtle indicator dot for active item
          Box(
            modifier = Modifier
              .size(3.dp)
              .clip(CircleShape)
              .background(if (isSelected) activeColor else Color.Transparent)
          )
        }
      }
    }
  }
}

