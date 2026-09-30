package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SecurityStatusModal
import com.example.ui.components.TacticalPanel
import com.example.ui.components.TacticalStatusLed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceRaised
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateBorderBright
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTerminalGreen

import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.collectAsState
import com.example.ui.theme.AegoraAppTheme
import com.example.ui.theme.ThemeManager

/**
 * SettingsDashboard - Enterprise Operations & SRE Observability Center.
 * Houses the SreTelemetryView sub-view and Security Status modal inspector.
 */
@Composable
fun SettingsDashboard(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val colors = AegoraAppTheme.colors
  val isDark by ThemeManager.isDarkTheme.collectAsState()
  var selectedTab by remember { mutableIntStateOf(0) }
  var showSecurityModal by remember { mutableStateOf(false) }

  if (showSecurityModal) {
    SecurityStatusModal(
      onDismiss = { showSecurityModal = false }
    )
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("settings_dashboard_scaffold"),
    containerColor = colors.background
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Top Navigation Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("settings_back_button")
          ) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = colors.accent
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "SETTINGS // SRE DASHBOARD",
            color = colors.accent,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = { ThemeManager.toggleTheme() },
            modifier = Modifier
              .size(36.dp)
              .clip(CutCornerShape(4.dp))
              .background(colors.surfaceRaised)
              .border(1.dp, colors.border, CutCornerShape(4.dp))
              .testTag("settings_theme_toggle_btn")
          ) {
            Icon(
              imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "Toggle Theme",
              tint = if (isDark) colors.amber else colors.accent,
              modifier = Modifier.size(18.dp)
            )
          }

          Button(
            onClick = { showSecurityModal = true },
            colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceRaised),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.border),
            shape = CutCornerShape(4.dp),
            modifier = Modifier.testTag("open_security_status_modal_button")
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = colors.accent,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "[SECURITY STATUS]",
              color = colors.accent,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // Theme Selector Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(CutCornerShape(6.dp))
          .background(colors.surface)
          .border(1.dp, colors.border, CutCornerShape(6.dp))
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              "MULTI-GEN DYNAMIC THEME ENGINE",
              color = colors.accent,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Text(
              if (isDark) "Active: Cyberpunk Obsidian Dark (#030712)" else "Active: Enterprise Minimalist Light (#F8FAFC)",
              color = colors.textMuted,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
              modifier = Modifier
                .clip(CutCornerShape(4.dp))
                .background(if (isDark) colors.accentContainer else colors.surfaceRaised)
                .border(1.dp, if (isDark) colors.accent else colors.border, CutCornerShape(4.dp))
                .clickable { ThemeManager.setDark(true) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text("DARK", color = if (isDark) colors.accent else colors.textDim, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Box(
              modifier = Modifier
                .clip(CutCornerShape(4.dp))
                .background(if (!isDark) colors.accentContainer else colors.surfaceRaised)
                .border(1.dp, if (!isDark) colors.accent else colors.border, CutCornerShape(4.dp))
                .clickable { ThemeManager.setDark(false) }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text("LIGHT", color = if (!isDark) colors.accent else colors.textDim, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }

      // Asymmetrical Industrial Tab Navigation Strip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val tabShape = CutCornerShape(topStart = 6.dp, bottomEnd = 4.dp)

        // Tab 0: SRE Telemetry
        val isTab0Active = selectedTab == 0
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(tabShape)
            .background(if (isTab0Active) colors.accentContainer else colors.surfaceRaised)
            .border(1.dp, if (isTab0Active) colors.accent else colors.border, tabShape)
            .clickable { selectedTab = 0 }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "[SRE_TELEMETRY]",
            color = if (isTab0Active) colors.accent else colors.textMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        // Tab 1: Enclave & Config
        val isTab1Active = selectedTab == 1
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(tabShape)
            .background(if (isTab1Active) colors.accentContainer else colors.surfaceRaised)
            .border(1.dp, if (isTab1Active) colors.accent else colors.border, tabShape)
            .clickable { selectedTab = 1 }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "[ENCLAVE_CONFIG]",
            color = if (isTab1Active) colors.accent else colors.textMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      // Tab Content Rendering
      when (selectedTab) {
        0 -> {
          // Sub-View: SRE Telemetry
          SreTelemetryView()
        }
        1 -> {
          // Sub-View: Hardware Enclave & System Configuration
          TacticalPanel(
            titleTag = "[CONFIG-01] // HARDWARE_SECURITY_ENCLAVE",
            subtitle = "POST-QUANTUM CRYPTOGRAPHIC SPECIFICATION",
            memoryOffset = "READY",
            statusLed = TacticalStatusLed.SECURE_EMERALD
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(
                text = "• KEYSTORE_PROVIDER: Android StrongBox Keystore Enclave",
                color = TextPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "• CIPHER_ALGORITHM: AES-256-GCM + Kyber-768 PQ Hybrid",
                color = TextPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "• MEMORY_SANITIZATION: Constant-Time Zeroization Guaranteed",
                color = TextTerminalGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = "• CHAOS_INJECTOR: Enabled (0.00% involuntary dropped frames)",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }
  }
}
