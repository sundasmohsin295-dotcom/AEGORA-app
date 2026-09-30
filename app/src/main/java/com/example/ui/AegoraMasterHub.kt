package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.DuelArenaViewModel

enum class AegoraScreen(
  val title: String,
  val icon: ImageVector,
  val tag: String
) {
  COMMAND_CENTER("Home", Icons.Filled.Dashboard, "nav_home"),
  INCIDENT_INVESTIGATION("Incidents", Icons.Filled.Search, "nav_incidents"),
  GLOBAL_RADAR("Threat Radar", Icons.Filled.Public, "nav_radar"),
  AI_MENTOR("AI Assistant", Icons.Filled.SmartToy, "nav_ai"),
  SKILL_CONSTELLATION("Skills", Icons.Filled.AutoAwesome, "nav_skills"),
  CAREER_PASSPORT("Career", Icons.Filled.Badge, "nav_career"),
  ZERO_TRUST("Zero-Trust", Icons.Filled.Lock, "nav_zero_trust"),
  DUEL_ARENA("Duel Arena", Icons.Filled.Security, "nav_duel"),
  EXECUTIVE_REPORT("Reports", Icons.Filled.Description, "nav_reports"),
  SETTINGS("Settings", Icons.Filled.Tune, "nav_settings")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AegoraMasterHub(
  duelViewModel: DuelArenaViewModel,
  deepLinkSessionId: String? = null,
  modifier: Modifier = Modifier
) {
  var currentScreen by remember { mutableStateOf(AegoraScreen.COMMAND_CENTER) }
  var selectedIncidentId by remember { mutableStateOf("INC-20481") }
  var showMoreMenuDrawer by remember { mutableStateOf(false) }

  // Back-stack handling: returning to Command Center when in sub-screen
  BackHandler(enabled = currentScreen != AegoraScreen.COMMAND_CENTER || showMoreMenuDrawer) {
    if (showMoreMenuDrawer) {
      showMoreMenuDrawer = false
    } else {
      currentScreen = AegoraScreen.COMMAND_CENTER
    }
  }

  val colors = AegoraAppTheme.colors
  val isDark by ThemeManager.isDarkTheme.collectAsState()

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(colors.background)
  ) {
    val isTabletOrDesktop = maxWidth >= 840.dp

    if (isTabletOrDesktop) {
      // Wide Screen: Side Navigation Rail + Main Viewport
      Row(modifier = Modifier.fillMaxSize()) {
        MasterSidebarRail(
          currentScreen = currentScreen,
          onSelectScreen = { currentScreen = it }
        )
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
          MasterScreenContent(
            currentScreen = currentScreen,
            selectedIncidentId = selectedIncidentId,
            duelViewModel = duelViewModel,
            deepLinkSessionId = deepLinkSessionId,
            onNavigateToIncident = { id ->
              selectedIncidentId = id
              currentScreen = AegoraScreen.INCIDENT_INVESTIGATION
            },
            onNavigateToRadar = { currentScreen = AegoraScreen.GLOBAL_RADAR },
            onNavigateToAi = { currentScreen = AegoraScreen.AI_MENTOR },
            onNavigateBack = { currentScreen = AegoraScreen.COMMAND_CENTER }
          )
        }
      }
    } else {
      // Mobile / Compact Screen: Top Header with Logo + Content + Bottom Nav
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background,
        topBar = {
          MasterMobileTopBar(
            currentScreen = currentScreen,
            onOpenMoreMenu = { showMoreMenuDrawer = true }
          )
        },
        bottomBar = {
          MasterMobileBottomBar(
            currentScreen = currentScreen,
            onSelectScreen = { currentScreen = it },
            onOpenMore = { showMoreMenuDrawer = true }
          )
        }
      ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
          MasterScreenContent(
            currentScreen = currentScreen,
            selectedIncidentId = selectedIncidentId,
            duelViewModel = duelViewModel,
            deepLinkSessionId = deepLinkSessionId,
            onNavigateToIncident = { id ->
              selectedIncidentId = id
              currentScreen = AegoraScreen.INCIDENT_INVESTIGATION
            },
            onNavigateToRadar = { currentScreen = AegoraScreen.GLOBAL_RADAR },
            onNavigateToAi = { currentScreen = AegoraScreen.AI_MENTOR },
            onNavigateBack = { currentScreen = AegoraScreen.COMMAND_CENTER }
          )
        }
      }

      // Quick Switcher Bottom Sheet
      if (showMoreMenuDrawer) {
        ModalBottomSheet(
          onDismissRequest = { showMoreMenuDrawer = false },
          containerColor = colors.surface,
          shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "ALL AEGORA PLATFORM SCREENS",
                color = colors.accent,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
              )
              Row(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(colors.surfaceRaised)
                  .clickable { ThemeManager.toggleTheme() }
                  .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                  contentDescription = null,
                  tint = if (isDark) TacticalAmber else colors.accent,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  if (isDark) "Switch to Light" else "Switch to Dark",
                  color = colors.textPrimary,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            AegoraScreen.entries.forEach { screen ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (currentScreen == screen) colors.accentContainer else colors.surfaceRaised)
                  .clickable {
                    currentScreen = screen
                    showMoreMenuDrawer = false
                  }
                  .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Icon(
                  screen.icon,
                  contentDescription = null,
                  tint = if (currentScreen == screen) colors.accent else colors.textMuted,
                  modifier = Modifier.size(20.dp)
                )
                Text(
                  screen.title,
                  color = if (currentScreen == screen) colors.accent else colors.textPrimary,
                  fontWeight = if (currentScreen == screen) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 13.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
            Spacer(modifier = Modifier.height(16.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun MasterScreenContent(
  currentScreen: AegoraScreen,
  selectedIncidentId: String,
  duelViewModel: DuelArenaViewModel,
  deepLinkSessionId: String?,
  onNavigateToIncident: (String) -> Unit,
  onNavigateToRadar: () -> Unit,
  onNavigateToAi: () -> Unit,
  onNavigateBack: () -> Unit
) {
  when (currentScreen) {
    AegoraScreen.COMMAND_CENTER -> SocCommandCenterScreen(
      onNavigateToIncident = onNavigateToIncident,
      onNavigateToRadar = onNavigateToRadar,
      onNavigateToAi = onNavigateToAi,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.GLOBAL_RADAR -> GlobalThreatRadarScreen(
      onNavigateBack = onNavigateBack,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.INCIDENT_INVESTIGATION -> IncidentInvestigationScreen(
      incidentId = selectedIncidentId,
      onNavigateBack = onNavigateBack,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.AI_MENTOR -> SentinelAiMentorScreen(
      onNavigateBack = onNavigateBack,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.ZERO_TRUST -> ZeroTrustSecurityScreen(
      onNavigateBack = onNavigateBack,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.SKILL_CONSTELLATION -> SkillConstellationScreen(
      onNavigateBack = onNavigateBack,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.CAREER_PASSPORT -> SkillPassportMasterScreen(
      onNavigateBack = onNavigateBack,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.EXECUTIVE_REPORT -> IncidentReportExecutiveScreen(
      onNavigateBack = onNavigateBack,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.DUEL_ARENA -> DuelArenaScreen(
      viewModel = duelViewModel,
      deepLinkSessionId = deepLinkSessionId,
      modifier = Modifier.fillMaxSize()
    )
    AegoraScreen.SETTINGS -> SettingsDashboard(
      onNavigateBack = onNavigateBack,
      modifier = Modifier.fillMaxSize()
    )
  }
}

@Composable
private fun MasterSidebarRail(
  currentScreen: AegoraScreen,
  onSelectScreen: (AegoraScreen) -> Unit
) {
  val colors = AegoraAppTheme.colors
  val isDark by ThemeManager.isDarkTheme.collectAsState()

  Surface(
    modifier = Modifier
      .width(220.dp)
      .fillMaxHeight(),
    color = colors.surface,
    border = BorderStroke(1.dp, colors.border)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Logo & Brand Header
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.padding(bottom = 14.dp)
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_aegora_chevron_logo),
            contentDescription = "AEGORA Logo",
            modifier = Modifier.size(32.dp)
          )
          Column {
            Text("AEGORA", color = colors.accent, fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
            Text("Autonomous SOC", color = colors.textDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
          }
        }

        // Nav items
        AegoraScreen.entries.forEach { screen ->
          val isSelected = currentScreen == screen
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(6.dp))
              .background(if (isSelected) colors.accentContainer else Color.Transparent)
              .clickable { onSelectScreen(screen) }
              .padding(horizontal = 10.dp, vertical = 8.dp)
              .testTag(screen.tag),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              screen.icon,
              contentDescription = screen.title,
              tint = if (isSelected) colors.accent else colors.textMuted,
              modifier = Modifier.size(18.dp)
            )
            Text(
              screen.title,
              color = if (isSelected) colors.accent else colors.textMuted,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 11.5.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      // Bottom Section: Theme Toggle + Operator Profile Pill
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Theme Toggle Switcher Button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(colors.surfaceRaised)
            .border(1.dp, colors.border, RoundedCornerShape(6.dp))
            .clickable { ThemeManager.toggleTheme() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("theme_toggle_sidebar_btn"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
              imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = "Toggle Theme",
              tint = if (isDark) TacticalAmber else colors.accent,
              modifier = Modifier.size(16.dp)
            )
            Text(
              if (isDark) "Light Mode" else "Dark Mode",
              color = colors.textPrimary,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.SemiBold
            )
          }
          Text(
            if (isDark) "DARK" else "LIGHT",
            color = if (isDark) colors.textDim else colors.emerald,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          )
        }

        // Bottom Operator Profile Pill (Sundas, SOC Analyst | L2)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceRaised, RoundedCornerShape(8.dp))
            .border(1.dp, colors.border, RoundedCornerShape(8.dp))
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(colors.accent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
          }
          Column {
            Text("Sundas", color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text("SOC Analyst | L2", color = colors.textDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
          }
        }
      }
    }
  }
}

@Composable
private fun MasterMobileTopBar(
  currentScreen: AegoraScreen,
  onOpenMoreMenu: () -> Unit
) {
  val colors = AegoraAppTheme.colors
  val isDark by ThemeManager.isDarkTheme.collectAsState()

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(colors.surface)
      .windowInsetsPadding(WindowInsets.statusBars)
      .border(BorderStroke(1.dp, colors.border))
      .padding(horizontal = 14.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Image(
        painter = painterResource(id = R.drawable.ic_aegora_chevron_logo),
        contentDescription = "AEGORA Logo",
        modifier = Modifier.size(24.dp)
      )
      Text("AEGORA", color = colors.accent, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      // Mobile Theme Toggle Button
      IconButton(
        onClick = { ThemeManager.toggleTheme() },
        modifier = Modifier
          .size(30.dp)
          .clip(CircleShape)
          .background(colors.surfaceRaised)
          .border(1.dp, colors.border, CircleShape)
          .testTag("theme_toggle_mobile_btn")
      ) {
        Icon(
          imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
          contentDescription = "Toggle Theme",
          tint = if (isDark) TacticalAmber else colors.accent,
          modifier = Modifier.size(16.dp)
        )
      }

      Box(
        modifier = Modifier
          .background(colors.surfaceRaised, RoundedCornerShape(4.dp))
          .border(1.dp, colors.border, RoundedCornerShape(4.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text("Sundas • L2", color = colors.textMuted, fontSize = 9.5.sp, fontFamily = FontFamily.Monospace)
      }

      IconButton(onClick = onOpenMoreMenu, modifier = Modifier.size(30.dp)) {
        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = colors.accent, modifier = Modifier.size(20.dp))
      }
    }
  }
}

@Composable
private fun MasterMobileBottomBar(
  currentScreen: AegoraScreen,
  onSelectScreen: (AegoraScreen) -> Unit,
  onOpenMore: () -> Unit
) {
  val colors = AegoraAppTheme.colors
  val primaryTabs = listOf(
    AegoraScreen.COMMAND_CENTER,
    AegoraScreen.INCIDENT_INVESTIGATION,
    AegoraScreen.GLOBAL_RADAR,
    AegoraScreen.AI_MENTOR,
    AegoraScreen.DUEL_ARENA
  )

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars),
    color = colors.surface,
    border = BorderStroke(1.dp, colors.border)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      primaryTabs.forEach { screen ->
        val isSelected = currentScreen == screen
        Column(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelectScreen(screen) }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(screen.tag),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(
            screen.icon,
            contentDescription = screen.title,
            tint = if (isSelected) colors.accent else colors.textDim,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = screen.title,
            color = if (isSelected) colors.accent else colors.textDim,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      // More menu button
      Column(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onOpenMore() }
          .padding(horizontal = 8.dp, vertical = 4.dp)
          .testTag("nav_more"),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(Icons.Default.Apps, contentDescription = "More", tint = colors.textDim, modifier = Modifier.size(20.dp))
        Text(
          text = "More",
          color = colors.textDim,
          fontSize = 9.sp,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

