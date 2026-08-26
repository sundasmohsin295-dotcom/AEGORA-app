package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.data.AegoraRepository
import com.example.model.BreakpointClass
import com.example.ui.adaptive.*
import com.example.ui.components.AegoraBottomNav
import com.example.ui.components.AegoraNavTab
import com.example.ui.components.AegoraTopBar
import com.example.ui.components.UniversalSearchDialog
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class ScreenDestination {
  data object MainHub : ScreenDestination()
  data object Onboarding : ScreenDestination()
  data object CyberAuth : ScreenDestination()
  data object CognitiveProfile : ScreenDestination()
  data object ShadowRange : ScreenDestination()
  data object TimelineFork : ScreenDestination()
  data object BioStress : ScreenDestination()
  data object CrisisWarRoom : ScreenDestination()
  data object ZeroDayLab : ScreenDestination()
  data object GlobalRadar : ScreenDestination()
  data object SwarmArena : ScreenDestination()
  data object BinaryDisassembler : ScreenDestination()
  data object CyberTerminal : ScreenDestination()
  data object LiveSocRange : ScreenDestination()
  data object ThreatAcoustic : ScreenDestination()
  data class LessonDetail(val lessonId: String) : ScreenDestination()
  data class Quiz(val quizId: String) : ScreenDestination()
  data object Projects : ScreenDestination()
  data object Intelligence : ScreenDestination()
  data object CareerCenter : ScreenDestination()
  data object EventsAndMap : ScreenDestination()
  data object ProfileSettings : ScreenDestination()
  data object KnowledgeVault : ScreenDestination()
  data object Community : ScreenDestination()
  data object UniversityAdmin : ScreenDestination()
}

@Composable
fun AegoraApp() {
  AegoraTheme {
    var currentDestination by remember { mutableStateOf<ScreenDestination>(ScreenDestination.MainHub) }
    var currentTab by remember { mutableStateOf(AegoraNavTab.RADAR) }
    val userProfile by AegoraRepository.userProfile.collectAsState()
    val syncStatus by AegoraRepository.networkSyncStatus.collectAsState()
    val performanceMode by AegoraRepository.performanceMode.collectAsState()
    val sessionState by AegoraRepository.crossDeviceSession.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var showPerformanceDialog by remember { mutableStateOf(false) }

    Surface(
      modifier = Modifier.fillMaxSize(),
      color = CyberBackground
    ) {
      when (val dest = currentDestination) {
        is ScreenDestination.Onboarding -> {
          OnboardingScreen(
            onOnboardingComplete = {
              currentDestination = ScreenDestination.MainHub
            }
          )
        }

        is ScreenDestination.LessonDetail -> {
          LessonDetailScreen(
            lessonId = dest.lessonId,
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToQuiz = { qzId -> currentDestination = ScreenDestination.Quiz(qzId) },
            onNavigateToVault = { currentDestination = ScreenDestination.KnowledgeVault }
          )
        }

        is ScreenDestination.Quiz -> {
          QuizScreen(
            quizId = dest.quizId,
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.Projects -> {
          ProjectsScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.Intelligence -> {
          IntelligenceScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.CareerCenter -> {
          CareerCenterScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onSelectCareer = {
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.JOURNEY
            }
          )
        }

        is ScreenDestination.EventsAndMap -> {
          EventsAndMapScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.KnowledgeVault -> {
          KnowledgeVaultScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToLesson = { lessonId ->
              currentDestination = ScreenDestination.LessonDetail(lessonId)
            }
          )
        }

        is ScreenDestination.Community -> {
          CommunityScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.UniversityAdmin -> {
          UniversityAndAdminScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.CyberAuth -> {
          CyberAuthScreen(
            onAuthSuccess = { currentDestination = ScreenDestination.MainHub },
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.CognitiveProfile -> {
          CognitiveProfileScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToDrill = { _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.LABS
            }
          )
        }

        is ScreenDestination.ShadowRange -> {
          ShadowRangeScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.TimelineFork -> {
          TimelineForkScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.BioStress -> {
          BioStressScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.CrisisWarRoom -> {
          CrisisWarRoomScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.ZeroDayLab -> {
          ZeroDayLabScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.GlobalRadar -> {
          GlobalRadarScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToSwarmArena = { currentDestination = ScreenDestination.SwarmArena }
          )
        }

        is ScreenDestination.SwarmArena -> {
          SwarmArenaScreen(
            onNavigateBack = { currentDestination = ScreenDestination.GlobalRadar }
          )
        }

        is ScreenDestination.BinaryDisassembler -> {
          BinaryDisassemblerScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.CyberTerminal -> {
          CyberTerminalScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.LiveSocRange -> {
          LiveSocRangeScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.ThreatAcoustic -> {
          ThreatAcousticScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.ProfileSettings -> {
          ProfileAndSettingsScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onRestartOnboarding = { currentDestination = ScreenDestination.Onboarding },
            onNavigateToVault = { currentDestination = ScreenDestination.KnowledgeVault },
            onNavigateToCommunity = { currentDestination = ScreenDestination.Community },
            onNavigateToUniversityAdmin = { currentDestination = ScreenDestination.UniversityAdmin },
            onNavigateToAuth = { currentDestination = ScreenDestination.CyberAuth },
            onNavigateToCognitiveProfile = { currentDestination = ScreenDestination.CognitiveProfile }
          )
        }

        is ScreenDestination.MainHub -> {
          BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val breakpoint = rememberBreakpoint(maxWidth)
            val isCompact = breakpoint == BreakpointClass.COMPACT
            val isMedium = breakpoint == BreakpointClass.MEDIUM
            val isDesktopOrWider = breakpoint == BreakpointClass.EXPANDED || breakpoint == BreakpointClass.LARGE || breakpoint == BreakpointClass.ULTRAWIDE
            val showRightIntelPanel = breakpoint == BreakpointClass.LARGE || breakpoint == BreakpointClass.ULTRAWIDE

            Row(modifier = Modifier.fillMaxSize()) {
              // 1. Desktop Persistent Sidebar (Expanded / Large / Ultrawide)
              if (isDesktopOrWider) {
                DesktopPersistentSidebar(
                  currentTab = currentTab,
                  onTabSelected = { currentTab = it },
                  onOpenSearch = { showSearchDialog = true },
                  onOpenSettings = { currentDestination = ScreenDestination.ProfileSettings }
                )
              } else if (isMedium) {
                // 2. Navigation Rail for Tablets / Foldables
                NavigationRail(
                  containerColor = CyberDarkSlate,
                  contentColor = NeonCyan,
                  header = {
                    Box(
                      modifier = Modifier
                        .padding(vertical = 12.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonCyan.copy(alpha = 0.2f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.Shield, contentDescription = "AEGORA", tint = NeonCyan)
                    }
                  }
                ) {
                  AegoraNavTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    val icon: ImageVector = when (tab) {
                      AegoraNavTab.RADAR -> Icons.Default.Radar
                      AegoraNavTab.JOURNEY -> Icons.Default.Map
                      AegoraNavTab.LABS -> Icons.Default.Terminal
                      AegoraNavTab.AI_MENTOR -> Icons.Default.Psychology
                      AegoraNavTab.PASSPORT -> Icons.Default.Badge
                    }
                    val label: String = when (tab) {
                      AegoraNavTab.RADAR -> "Radar"
                      AegoraNavTab.JOURNEY -> "Path"
                      AegoraNavTab.LABS -> "Labs"
                      AegoraNavTab.AI_MENTOR -> "Mentor"
                      AegoraNavTab.PASSPORT -> "Passport"
                    }
                    NavigationRailItem(
                      selected = selected,
                      onClick = { currentTab = tab },
                      icon = { Icon(icon, contentDescription = label) },
                      label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                      colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = NeonCyan,
                        indicatorColor = NeonCyan,
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                      )
                    )
                  }
                }
              }

              // 3. Central Main Workspace
              Scaffold(
                topBar = {
                  AegoraTopBar(
                    userProfile = userProfile,
                    syncStatus = syncStatus,
                    performanceMode = performanceMode,
                    onSearchClick = { showSearchDialog = true },
                    onNotificationClick = { showNotificationDialog = true },
                    onProfileClick = { currentDestination = ScreenDestination.ProfileSettings },
                    onSyncClick = { showSyncDialog = true },
                    onPerformanceClick = { showPerformanceDialog = true }
                  )
                },
                bottomBar = {
                  if (isCompact) {
                    AegoraBottomNav(
                      currentTab = currentTab,
                      onTabSelected = { currentTab = it }
                    )
                  }
                },
                containerColor = CyberBackground,
                modifier = Modifier.weight(1f)
              ) { innerPadding ->
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                  contentAlignment = Alignment.TopCenter
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .widthIn(max = if (showRightIntelPanel) 1100.dp else 1280.dp)
                  ) {
                    when (currentTab) {
                      AegoraNavTab.RADAR -> {
                        HomeScreen(
                          onNavigateToJourney = { currentTab = AegoraNavTab.JOURNEY },
                          onNavigateToLabs = { currentTab = AegoraNavTab.LABS },
                          onNavigateToAi = { currentTab = AegoraNavTab.AI_MENTOR },
                          onNavigateToPassport = { currentTab = AegoraNavTab.PASSPORT },
                          onNavigateToLesson = { lessonId ->
                            currentDestination = ScreenDestination.LessonDetail(lessonId)
                          },
                          onNavigateToIntelligence = {
                            currentDestination = ScreenDestination.Intelligence
                          },
                          onNavigateToCareers = {
                            currentDestination = ScreenDestination.CareerCenter
                          },
                          onNavigateToVault = {
                            currentDestination = ScreenDestination.KnowledgeVault
                          },
                          onNavigateToCommunity = {
                            currentDestination = ScreenDestination.Community
                          },
                          onNavigateToUniversity = {
                            currentDestination = ScreenDestination.UniversityAdmin
                          },
                          onNavigateToShadowRange = {
                            currentDestination = ScreenDestination.ShadowRange
                          },
                          onNavigateToTimelineFork = {
                            currentDestination = ScreenDestination.TimelineFork
                          },
                          onNavigateToBioStress = {
                            currentDestination = ScreenDestination.BioStress
                          },
                          onNavigateToCrisisWarRoom = {
                            currentDestination = ScreenDestination.CrisisWarRoom
                          },
                          onNavigateToZeroDayLab = {
                            currentDestination = ScreenDestination.ZeroDayLab
                          },
                          onNavigateToGlobalRadar = {
                            currentDestination = ScreenDestination.GlobalRadar
                          },
                          onNavigateToSwarmArena = {
                            currentDestination = ScreenDestination.SwarmArena
                          },
                          onNavigateToBinaryDisassembler = {
                            currentDestination = ScreenDestination.BinaryDisassembler
                          },
                          onNavigateToCyberTerminal = {
                            currentDestination = ScreenDestination.CyberTerminal
                          },
                          onNavigateToLiveSocRange = {
                            currentDestination = ScreenDestination.LiveSocRange
                          },
                          onNavigateToThreatAcoustic = {
                            currentDestination = ScreenDestination.ThreatAcoustic
                          }
                        )
                      }

                      AegoraNavTab.JOURNEY -> {
                        JourneyScreen(
                          onNavigateToLesson = { lessonId ->
                            currentDestination = ScreenDestination.LessonDetail(lessonId)
                          },
                          onNavigateToLab = {
                            currentTab = AegoraNavTab.LABS
                          },
                          onNavigateToProjects = {
                            currentDestination = ScreenDestination.Projects
                          }
                        )
                      }

                      AegoraNavTab.LABS -> {
                        LabSimulatorScreen(
                          onNavigateToAiMentor = {
                            currentTab = AegoraNavTab.AI_MENTOR
                          }
                        )
                      }

                      AegoraNavTab.AI_MENTOR -> {
                        AegoraAiScreen()
                      }

                      AegoraNavTab.PASSPORT -> {
                        SkillPassportScreen(
                          onNavigateToCareers = {
                            currentDestination = ScreenDestination.CareerCenter
                          },
                          onNavigateToProjects = {
                            currentDestination = ScreenDestination.Projects
                          },
                          onNavigateToCognitiveProfile = {
                            currentDestination = ScreenDestination.CognitiveProfile
                          }
                        )
                      }
                    }
                  }
                }
              }

              // 4. Desktop Right Contextual Intelligence Panel (Large & Ultra-wide)
              if (showRightIntelPanel) {
                DesktopContextualIntelPanel(
                  onNavigateToAi = { currentTab = AegoraNavTab.AI_MENTOR },
                  onNavigateToTerminal = { currentDestination = ScreenDestination.CyberTerminal },
                  onNavigateToSocRange = { currentDestination = ScreenDestination.LiveSocRange }
                )
              }
            }
          }
        }
      }

      // Universal Command Palette Dialog
      if (showSearchDialog) {
        UniversalSearchDialog(
          onDismiss = { showSearchDialog = false },
          onNavigateToLesson = { lesId ->
            currentDestination = ScreenDestination.LessonDetail(lesId)
          },
          onNavigateToLab = {
            currentTab = AegoraNavTab.LABS
            currentDestination = ScreenDestination.MainHub
          },
          onNavigateToIntelligence = {
            currentDestination = ScreenDestination.Intelligence
          },
          onNavigateToCareer = {
            currentDestination = ScreenDestination.CareerCenter
          },
          onNavigateToVault = {
            currentDestination = ScreenDestination.KnowledgeVault
          },
          onNavigateToCommunity = {
            currentDestination = ScreenDestination.Community
          },
          onNavigateToUniversity = {
            currentDestination = ScreenDestination.UniversityAdmin
          },
          onNavigateToShadowRange = {
            currentDestination = ScreenDestination.ShadowRange
          },
          onNavigateToTimelineFork = {
            currentDestination = ScreenDestination.TimelineFork
          },
          onNavigateToBioStress = {
            currentDestination = ScreenDestination.BioStress
          },
          onNavigateToCrisisWarRoom = {
            currentDestination = ScreenDestination.CrisisWarRoom
          },
          onNavigateToZeroDayLab = {
            currentDestination = ScreenDestination.ZeroDayLab
          },
          onNavigateToGlobalRadar = {
            currentDestination = ScreenDestination.GlobalRadar
          },
          onNavigateToBinaryDisassembler = {
            currentDestination = ScreenDestination.BinaryDisassembler
          },
          onNavigateToCyberTerminal = {
            currentDestination = ScreenDestination.CyberTerminal
          },
          onNavigateToLiveSocRange = {
            currentDestination = ScreenDestination.LiveSocRange
          },
          onNavigateToThreatAcoustic = {
            currentDestination = ScreenDestination.ThreatAcoustic
          }
        )
      }

      // Cross-Device Sync and Conflict Management Dialog
      if (showSyncDialog) {
        SyncStateAndConflictDialog(
          onDismiss = { showSyncDialog = false }
        )
      }

      // Performance and Display Mode Dialog
      if (showPerformanceDialog) {
        PerformanceModeDialog(
          onDismiss = { showPerformanceDialog = false }
        )
      }

      // Notifications Dialog
      if (showNotificationDialog) {
        AlertDialog(
          onDismissRequest = { showNotificationDialog = false },
          title = { Text("Security Notifications", color = MaterialTheme.colorScheme.onSurface) },
          text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("• Daily Mission available: 'Investigate Suspicious PowerShell Base64 Encoded Command' (+250 XP)", color = MaterialTheme.colorScheme.onSurface)
              Text("• New Critical Threat Advisory: CVE-2024-3094 published by AEGORA Threat Intelligence.", color = MaterialTheme.colorScheme.onSurface)
            }
          },
          confirmButton = {
            Button(onClick = { showNotificationDialog = false }) {
              Text("Acknowledge")
            }
          }
        )
      }
    }
  }
}
