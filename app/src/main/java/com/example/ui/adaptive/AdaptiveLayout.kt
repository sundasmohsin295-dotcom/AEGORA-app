package com.example.ui.adaptive

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.BreakpointClass
import com.example.model.CrossDeviceSessionState
import com.example.model.NetworkSyncStatus
import com.example.model.PerformanceMode
import com.example.ui.components.AegoraNavTab
import com.example.ui.components.HexagonShape
import com.example.ui.theme.*

/**
 * Calculates current BreakpointClass based on available width in Dp.
 */
@Composable
fun rememberBreakpoint(widthDp: Dp): BreakpointClass {
  return remember(widthDp) {
    when {
      widthDp < 600.dp -> BreakpointClass.COMPACT
      widthDp < 840.dp -> BreakpointClass.MEDIUM
      widthDp < 1200.dp -> BreakpointClass.EXPANDED
      widthDp < 1600.dp -> BreakpointClass.LARGE
      else -> BreakpointClass.ULTRAWIDE
    }
  }
}

/**
 * Global Universal Continue Banner - synchronizes and restores last active investigation,
 * lab step, or lesson seamlessly across phone, tablet, and desktop.
 */
@Composable
fun UniversalContinueHero(
  sessionState: CrossDeviceSessionState,
  onContinueAction: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("universal_continue_hero"),
    shape = RoundedCornerShape(14.dp),
    color = CyberDarkSlate,
    border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.6f))
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              NeonCyan.copy(alpha = 0.12f),
              Color(0xFF0D1B2A).copy(alpha = 0.4f),
              CyberBackground
            )
          )
        )
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(HexagonShape)
              .background(NeonCyan.copy(alpha = 0.2f))
              .border(1.2.dp, NeonCyan, HexagonShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Resume",
              tint = NeonCyan,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "UNIVERSAL CONTINUE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = NeonCyan
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "• ${sessionState.originDeviceName}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark
              )
            }
            Text(
              text = sessionState.lastActivityTitle,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
              ),
              color = TextPrimaryDark
            )
            Text(
              text = "${sessionState.lastActivityCategory} // ${sessionState.lastActivityProgress}",
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace
              ),
              color = NeonEmerald
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Button(
          onClick = { onContinueAction(sessionState.targetScreenTag) },
          colors = ButtonDefaults.buttonColors(
            containerColor = NeonCyan,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
          modifier = Modifier.testTag("resume_mission_button")
        ) {
          Icon(
            imageVector = Icons.Default.DoubleArrow,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "RESUME",
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }
}

/**
 * Universal Network Synchronization & Offline Indicator with interactive Manual Sync
 * and Conflict Resolution triggers.
 */
@Composable
fun NetworkSyncStatusCapsule(
  status: NetworkSyncStatus,
  onTriggerSync: () -> Unit,
  onOpenSyncDetails: () -> Unit,
  modifier: Modifier = Modifier
) {
  val (bgColor, borderColor, textColor, icon) = when (status) {
    NetworkSyncStatus.SYNCED -> Quadruple(
      Color(0xFF00241B),
      NeonEmerald.copy(alpha = 0.6f),
      NeonEmerald,
      Icons.Default.CloudDone
    )
    NetworkSyncStatus.SYNCING -> Quadruple(
      Color(0xFF002B3D),
      NeonCyan.copy(alpha = 0.6f),
      NeonCyan,
      Icons.Default.Sync
    )
    NetworkSyncStatus.OFFLINE -> Quadruple(
      Color(0xFF332000),
      TerminalAmber.copy(alpha = 0.6f),
      TerminalAmber,
      Icons.Default.CloudOff
    )
    NetworkSyncStatus.SYNC_ERROR -> Quadruple(
      Color(0xFF3B0014),
      NeonCrimson.copy(alpha = 0.6f),
      NeonCrimson,
      Icons.Default.Warning
    )
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable { onOpenSyncDetails() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = status.label,
        tint = textColor,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = status.badgeText,
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        ),
        color = textColor
      )
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

/**
 * Desktop Persistent Side Navigation Bar (Expanded / Large / Ultra-wide)
 */
@Composable
fun DesktopPersistentSidebar(
  currentTab: AegoraNavTab,
  onTabSelected: (AegoraNavTab) -> Unit,
  onOpenSearch: () -> Unit,
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .width(240.dp)
      .fillMaxHeight(),
    color = CyberDarkSlate,
    border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = CyberSurfaceElevated)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      // Aegora Header Logo
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 24.dp)
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(HexagonShape)
            .background(NeonCyan.copy(alpha = 0.2f))
            .border(1.5.dp, NeonCyan, HexagonShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Shield, contentDescription = "AEGORA Logo", tint = NeonCyan, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "AEGORA",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 1.5.sp
            ),
            color = NeonCyan
          )
          Text(
            text = "CYBER OS // v11",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            ),
            color = TextSecondaryDark
          )
        }
      }

      // Quick Search / Command Bar Trigger
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceElevated),
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { onOpenSearch() }
          .padding(bottom = 16.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Search...", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          }
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = CyberSurfaceElevated
          ) {
            Text(
              text = "Ctrl+K",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
              ),
              color = NeonCyan,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
          }
        }
      }

      Text(
        text = "CORE NAVIGATION",
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        ),
        color = TextSecondaryDark,
        modifier = Modifier.padding(vertical = 8.dp)
      )

      // Navigation Items
      AegoraNavTab.entries.forEach { tab ->
        val isSelected = currentTab == tab
        val (icon, title, desc) = when (tab) {
          AegoraNavTab.RADAR -> Triple(Icons.Default.Radar, "Command Radar", "Live Telemetry & Missions")
          AegoraNavTab.JOURNEY -> Triple(Icons.Default.Map, "Skill Path", "Roadmaps & Career Graph")
          AegoraNavTab.LABS -> Triple(Icons.Default.Terminal, "Tactical Range", "SOC, Disassembler, Labs")
          AegoraNavTab.AI_MENTOR -> Triple(Icons.Default.Psychology, "AI Cyber Mentor", "Socratic Security Tutor")
          AegoraNavTab.PASSPORT -> Triple(Icons.Default.Badge, "Evidence Passport", "Verified Skills & DNA")
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.Transparent,
          border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onTabSelected(tab) }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = icon,
              contentDescription = title,
              tint = if (isSelected) NeonCyan else TextSecondaryDark,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) NeonCyan else TextPrimaryDark
              )
              Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondaryDark
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.weight(1f))

      // Footer System Status
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberBackground,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable { onOpenSettings() }
          .padding(8.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(NeonEmerald)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Settings & Profile", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}

/**
 * Desktop Contextual Right Panel (Live Intel, eBPF stream, AI Mentor Quick View)
 */
@Composable
fun DesktopContextualIntelPanel(
  onNavigateToAi: () -> Unit,
  onNavigateToTerminal: () -> Unit,
  onNavigateToSocRange: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .width(320.dp)
      .fillMaxHeight(),
    color = CyberDarkSlate,
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceElevated)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Hub, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "LIVE TELEMETRY STREAM",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = NeonCyan
          )
        }
        Box(
          modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(NeonEmerald)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Real-time eBPF Syscall Monitor snippet
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberBackground,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberSurfaceElevated),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "eBPF KERNEL TRACER",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            ),
            color = TerminalAmber
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "[SYS_EXECVE] /usr/bin/curl -> 185.220.101.5\n[SEC_ALERT] Process injected into PID 1042\n[AUDITD] /etc/shadow access intercepted",
            style = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              lineHeight = 14.sp
            ),
            color = TextPrimaryDark
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // AI Mentor Quick Inquiry card
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF131124),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberViolet.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "SOCRATIC MENTOR INSIGHT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = CyberViolet
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "\"Why did the adversary pivot through Kerberoasting instead of Pass-the-Hash in this subnet? Consider the encryption tier.\"",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedButton(
            onClick = onNavigateToAi,
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberViolet)
          ) {
            Text("Open Socratic Dialogue", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Tactical Quick-Launch actions
      Text(
        text = "RAPID RANGE LAUNCH",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        ),
        color = TextSecondaryDark
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = onNavigateToSocRange,
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson.copy(alpha = 0.2f), contentColor = NeonCrimson),
          modifier = Modifier.weight(1f),
          contentPadding = PaddingValues(4.dp)
        ) {
          Text("SOC Range", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
        Button(
          onClick = onNavigateToTerminal,
          shape = RoundedCornerShape(6.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald.copy(alpha = 0.2f), contentColor = NeonEmerald),
          modifier = Modifier.weight(1f),
          contentPadding = PaddingValues(4.dp)
        ) {
          Text("CLI Terminal", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
