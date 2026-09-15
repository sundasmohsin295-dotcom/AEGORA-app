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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import com.example.data.AegoraRepository
import com.example.model.BreakpointClass
import com.example.ui.adaptive.*
import com.example.ui.components.AdaptiveNetworkBanner
import com.example.ui.components.AegoraBottomNav
import com.example.ui.components.AegoraNavTab
import com.example.ui.components.AegoraTopBar
import com.example.ui.components.BannerPosition
import com.example.ui.components.NetworkStatusBar
import com.example.ui.components.UniversalSearchDialog
import com.example.ui.screens.*
import com.example.ui.theme.*

sealed class ScreenDestination {
  data object Splash : ScreenDestination()
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
  data object ResourceUniverse : ScreenDestination()
  data object CyberExpertEngine : ScreenDestination()
  data object WorkplaceSimulator : ScreenDestination()
  data object SecurityCenter : ScreenDestination()
  data object MultiModalFusion : ScreenDestination()
  data object CyberLearningGenome : ScreenDestination()
  data object PurpleTeamArena : ScreenDestination()
  data object SocShiftSimulator : ScreenDestination()
  data object VoiceSocAndMultiverse : ScreenDestination()
  data object IntelligenceConnective : ScreenDestination()
  data object CyberRealityEngine : ScreenDestination()
  data object CyberOperatingSystem : ScreenDestination()
  data object CyberRealityIntelligenceV13 : ScreenDestination()
  data object Community : ScreenDestination()
  data object UniversityAdmin : ScreenDestination()
  data object PersonalIntelligence : ScreenDestination()
  data object LiveThreatIntel : ScreenDestination()
  data object ForensicToolArbitrator : ScreenDestination()
  data object AdaptiveSkillPassport : ScreenDestination()
  data object VulnerabilityTriageArena : ScreenDestination()
  data object DuelArena : ScreenDestination()
  data object IntelligenceCodex : ScreenDestination()
  data object OperatorDossier : ScreenDestination()
  data object OsintAgentChat : ScreenDestination()
  data object SubscriptionPaywall : ScreenDestination()
  data object MissionDiagnostic : ScreenDestination()
  data object AiAnalystChallenge : ScreenDestination()
  data object VerificationResult : ScreenDestination()
  data object CyberTwinRadar : ScreenDestination()
  data object RaspLockdown : ScreenDestination()
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AegoraApp(initialAuditReport: com.example.security.RaspAuditReport? = null) {
  AegoraTheme {
    val context = androidx.compose.ui.platform.LocalContext.current
    val auditReportState by com.example.security.SecurityEnforcer.auditReport.collectAsState()
    val effectiveAuditReport = initialAuditReport ?: auditReportState

    var currentDestination by remember {
      mutableStateOf<ScreenDestination>(
        if (effectiveAuditReport.isCompromised && !com.example.security.SecurityEnforcer.isAuditorOverrideActive && !com.example.security.SecurityEnforcer.bypassEnforcementForTesting) {
          ScreenDestination.RaspLockdown
        } else {
          ScreenDestination.Splash
        }
      )
    }
    var currentTab by remember { mutableStateOf(AegoraNavTab.RADAR) }
    val userProfile by AegoraRepository.userProfile.collectAsState()
    val syncStatus by AegoraRepository.networkSyncStatus.collectAsState()
    val performanceMode by AegoraRepository.performanceMode.collectAsState()
    val sessionState by AegoraRepository.crossDeviceSession.collectAsState()

    var showSearchDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showSyncDialog by remember { mutableStateOf(false) }
    var showPerformanceDialog by remember { mutableStateOf(false) }
    var showSubscriptionDialog by remember { mutableStateOf(false) }
    var showPremiumUpgradeSheet by remember { mutableStateOf(false) }
    val subscriptionState by com.example.subscription.AegoraSubscriptionRepository.subscriptionState.collectAsState()

    Surface(
      modifier = Modifier
        .fillMaxSize()
        .onPreviewKeyEvent { keyEvent ->
          if (keyEvent.type == KeyEventType.KeyDown && keyEvent.isAltPressed) {
            when (keyEvent.key) {
              Key.L -> {
                currentDestination = ScreenDestination.MainHub
                currentTab = AegoraNavTab.LEARN
                true
              }
              Key.P -> {
                currentDestination = ScreenDestination.MainHub
                currentTab = AegoraNavTab.OPERATE
                true
              }
              Key.H, Key.C -> {
                currentDestination = ScreenDestination.MainHub
                currentTab = AegoraNavTab.HOME
                true
              }
              Key.I -> {
                currentDestination = ScreenDestination.MainHub
                currentTab = AegoraNavTab.INTELLIGENCE
                true
              }
              Key.R, Key.O -> {
                currentDestination = ScreenDestination.MainHub
                currentTab = AegoraNavTab.PROOF
                true
              }
              Key.T -> {
                currentDestination = ScreenDestination.MainHub
                currentTab = AegoraNavTab.TOOLS
                true
              }
              Key.A -> {
                currentDestination = ScreenDestination.MainHub
                currentTab = AegoraNavTab.ACCOUNT
                true
              }
              else -> false
            }
          } else {
            false
          }
        },
      color = CyberBackground
    ) {
      when (val dest = currentDestination) {
        is ScreenDestination.RaspLockdown -> {
          RaspLockdownScreen(
            auditReport = effectiveAuditReport,
            onAuditorOverride = {
              com.example.security.SecurityEnforcer.setAuditorOverride(true)
              currentDestination = ScreenDestination.MainHub
            },
            onRecheckIntegrity = {
              val fresh = com.example.security.SecurityEnforcer.enforce(context)
              if (!fresh.isCompromised || com.example.security.SecurityEnforcer.isAuditorOverrideActive) {
                currentDestination = ScreenDestination.MainHub
              }
            }
          )
        }

        is ScreenDestination.Splash -> {
          SplashScreen(
            onSplashFinished = {
              currentDestination = ScreenDestination.CyberAuth
            }
          )
        }

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

        is ScreenDestination.ResourceUniverse -> {
          ResourceUniverseScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToLab = { _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.LABS
            },
            onAskAiAboutResource = { _, _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.AI_MENTOR
            }
          )
        }

        is ScreenDestination.CyberExpertEngine -> {
          CyberExpertEngineScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToLab = { _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.LABS
            },
            onNavigateToLesson = {
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.JOURNEY
            },
            onAskAi = { _, _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.AI_MENTOR
            }
          )
        }

        is ScreenDestination.WorkplaceSimulator -> {
          WorkplaceSimulatorScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToLab = { _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.LABS
            },
            onAskAi = { _, _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.AI_MENTOR
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

        is ScreenDestination.SecurityCenter -> {
          SecurityCenterScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToAuth = { currentDestination = ScreenDestination.CyberAuth }
          )
        }

        is ScreenDestination.CyberAuth -> {
          ProAuthScreen(
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

        is ScreenDestination.CyberLearningGenome -> {
          CyberLearningGenomeScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToIntervention = { _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.LABS
            }
          )
        }

        is ScreenDestination.PurpleTeamArena -> {
          PurpleTeamArenaScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.SocShiftSimulator -> {
          SocShiftSimulatorScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.VoiceSocAndMultiverse -> {
          VoiceSocAndMultiverseScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.IntelligenceConnective -> {
          IntelligenceConnectiveScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.CyberRealityEngine -> {
          CyberRealityScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.CyberOperatingSystem -> {
          CyberOperatingSystemScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.CyberRealityIntelligenceV13 -> {
          CyberRealityIntelligenceV13Screen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToLab = {
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.LABS
            }
          )
        }

        is ScreenDestination.PersonalIntelligence -> {
          PersonalIntelligenceScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onLaunchMission = { _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.LABS
            },
            onOpenSkillPassport = {
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.PASSPORT
            }
          )
        }

        is ScreenDestination.LiveThreatIntel -> {
          LiveThreatIntelScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.ForensicToolArbitrator -> {
          ForensicToolArbitratorScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.AdaptiveSkillPassport -> {
          AdaptiveSkillPassportScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.VulnerabilityTriageArena -> {
          VulnerabilityTriageArenaScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
          )
        }

        is ScreenDestination.DuelArena -> {
          DuelArenaScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onShowPaywall = { showPremiumUpgradeSheet = true }
          )
        }

        is ScreenDestination.IntelligenceCodex -> {
          IntelligenceCodexScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToDuel = { currentDestination = ScreenDestination.DuelArena }
          )
        }

        is ScreenDestination.OperatorDossier -> {
          OperatorDossierScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToDuel = { currentDestination = ScreenDestination.DuelArena },
            onShowPaywall = { showSubscriptionDialog = true }
          )
        }

        is ScreenDestination.OsintAgentChat -> {
          OsintAgentChatScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub }
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

        is ScreenDestination.MultiModalFusion -> {
          MultiModalFusionScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onAskAi = { _ ->
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.AI_MENTOR
            }
          )
        }

        is ScreenDestination.SubscriptionPaywall -> {
          com.example.ui.screens.PremiumUpgradeScreen(
            onPurchaseSuccess = {
              com.example.subscription.AegoraSubscriptionRepository.recordDirectPurchase(
                tier = com.example.model.SubscriptionTier.PRO,
                ownerAuthUid = userProfile.email
              )
              currentDestination = ScreenDestination.MainHub
            },
            onNavigateBack = {
              currentDestination = ScreenDestination.MainHub
            }
          )
        }

        is ScreenDestination.MissionDiagnostic -> {
          MissionDiagnosticScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onStartMission = {
              currentDestination = ScreenDestination.AiAnalystChallenge
            }
          )
        }

        is ScreenDestination.AiAnalystChallenge -> {
          AiAnalystChallengeScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MissionDiagnostic },
            onChallengeAi = {
              currentDestination = ScreenDestination.VerificationResult
            }
          )
        }

        is ScreenDestination.VerificationResult -> {
          VerificationResultScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onViewAutopsy = {
              currentDestination = ScreenDestination.SubscriptionPaywall
            },
            onViewPassport = {
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.PROOF
            }
          )
        }

        is ScreenDestination.CyberTwinRadar -> {
          CyberTwinRadarScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onNavigateToProof = {
              currentDestination = ScreenDestination.MainHub
              currentTab = AegoraNavTab.PROOF
            }
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
            onNavigateToCognitiveProfile = { currentDestination = ScreenDestination.CognitiveProfile },
            onNavigateToSecurityCenter = { currentDestination = ScreenDestination.SecurityCenter }
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
                    val icon: ImageVector = if (selected) tab.activeIcon else tab.inactiveIcon
                    val label: String = tab.label
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
                    onProfileClick = { currentDestination = ScreenDestination.OperatorDossier },
                    onSyncClick = { showSyncDialog = true },
                    onPerformanceClick = { showPerformanceDialog = true },
                    onSubscriptionClick = { showSubscriptionDialog = true }
                  )
                },
                bottomBar = {
                  if (isCompact) {
                    AegoraBottomNav(
                      currentTab = currentTab,
                      onTabSelected = { selectedTab ->
                        if (currentTab != selectedTab) {
                          currentTab = selectedTab
                        }
                      }
                    )
                  }
                },
                containerColor = CyberBackground,
                modifier = Modifier.weight(1f).imePadding()
              ) { innerPadding ->
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                  contentAlignment = Alignment.TopCenter
                ) {
                  Column(
                    modifier = Modifier
                      .fillMaxSize()
                      .widthIn(max = if (showRightIntelPanel) 1100.dp else 1280.dp)
                  ) {
                    // Adaptive Network Banner for Offline / Syncing notice (Respects safe insets & responsive width)
                    AdaptiveNetworkBanner(
                      syncStatus = syncStatus,
                      position = BannerPosition.TOP,
                      onActionClick = { showSyncDialog = true },
                      respectSafeAreaInsets = false
                    )

                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                    ) {
                      when (currentTab) {
                      AegoraNavTab.HOME -> {
                        HomeScreen(
                          onNavigateToJourney = { currentTab = AegoraNavTab.LEARN },
                          onNavigateToLabs = { currentTab = AegoraNavTab.OPERATE },
                          onNavigateToAi = { currentTab = AegoraNavTab.INTELLIGENCE },
                          onNavigateToPassport = { currentTab = AegoraNavTab.PROOF },
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
                          },
                          onNavigateToResourceUniverse = {
                            currentDestination = ScreenDestination.ResourceUniverse
                          },
                          onNavigateToCyberExpertEngine = {
                            currentDestination = ScreenDestination.CyberExpertEngine
                          },
                          onNavigateToWorkplaceSimulator = {
                            currentDestination = ScreenDestination.WorkplaceSimulator
                          },
                          onNavigateToSecurityCenter = {
                            currentDestination = ScreenDestination.SecurityCenter
                          },
                          onNavigateToAuth = {
                            currentDestination = ScreenDestination.CyberAuth
                          },
                          onNavigateToMultiModalFusion = {
                            currentDestination = ScreenDestination.MultiModalFusion
                          },
                          onNavigateToGenome = {
                            currentDestination = ScreenDestination.CyberLearningGenome
                          },
                          onNavigateToPurpleArena = {
                            currentDestination = ScreenDestination.PurpleTeamArena
                          },
                          onNavigateToSocShiftSimulator = {
                            currentDestination = ScreenDestination.SocShiftSimulator
                          },
                          onNavigateToVoiceSocAndMultiverse = {
                            currentDestination = ScreenDestination.VoiceSocAndMultiverse
                          },
                          onNavigateToIntelligenceConnective = {
                            currentDestination = ScreenDestination.IntelligenceConnective
                          },
                          onNavigateToCyberReality = {
                            currentDestination = ScreenDestination.CyberRealityEngine
                          },
                          onNavigateToCyberOperatingSystem = {
                            currentDestination = ScreenDestination.CyberOperatingSystem
                          },
                          onNavigateToV13Intelligence = {
                            currentDestination = ScreenDestination.CyberRealityIntelligenceV13
                          },
                          onNavigateToPersonalIntelligence = {
                            currentDestination = ScreenDestination.PersonalIntelligence
                          },
                          onNavigateToLiveThreatIntel = {
                            currentDestination = ScreenDestination.LiveThreatIntel
                          },
                          onNavigateToForensicArbitrator = {
                            currentDestination = ScreenDestination.ForensicToolArbitrator
                          },
                          onNavigateToAdaptiveSkillPassport = {
                            currentDestination = ScreenDestination.AdaptiveSkillPassport
                          },
                          onNavigateToVulnerabilityTriageArena = {
                            currentDestination = ScreenDestination.VulnerabilityTriageArena
                          },
                          onNavigateToDuel = {
                            val freeDuelsLeft = 2 - com.example.subscription.AegoraSubscriptionRepository.adversaryDuelsEngaged.value
                            if (subscriptionState.tier == com.example.model.SubscriptionTier.FREE && freeDuelsLeft <= 0) {
                              showPremiumUpgradeSheet = true
                            } else {
                              currentDestination = ScreenDestination.DuelArena
                            }
                          },
                          onNavigateToCodex = {
                            currentDestination = ScreenDestination.IntelligenceCodex
                          },
                          onNavigateToDossier = {
                            currentDestination = ScreenDestination.OperatorDossier
                          },
                          onNavigateToMissionDiagnostic = {
                            currentDestination = ScreenDestination.MissionDiagnostic
                          },
                          onNavigateToRadar = {
                            currentDestination = ScreenDestination.CyberTwinRadar
                          }
                        )
                      }

                      AegoraNavTab.LEARN -> {
                        JourneyScreen(
                          onNavigateToLesson = { lessonId ->
                            currentDestination = ScreenDestination.LessonDetail(lessonId)
                          },
                          onNavigateToLab = {
                            currentTab = AegoraNavTab.OPERATE
                          },
                          onNavigateToProjects = {
                            currentDestination = ScreenDestination.Projects
                          }
                        )
                      }

                      AegoraNavTab.OPERATE -> {
                        LabSimulatorScreen(
                          onNavigateToAiMentor = {
                            currentTab = AegoraNavTab.INTELLIGENCE
                          }
                        )
                      }

                      AegoraNavTab.INTELLIGENCE -> {
                        AegoraAiScreen()
                      }

                      AegoraNavTab.PROOF -> {
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

                      AegoraNavTab.TOOLS -> {
                        CyberTerminalScreen(
                          onNavigateBack = { currentTab = AegoraNavTab.HOME }
                        )
                      }

                      AegoraNavTab.ACCOUNT -> {
                        ProfileAndSettingsScreen(
                          onNavigateBack = { currentTab = AegoraNavTab.HOME },
                          onRestartOnboarding = { currentDestination = ScreenDestination.Onboarding },
                          onNavigateToVault = { currentDestination = ScreenDestination.KnowledgeVault },
                          onNavigateToCommunity = { currentDestination = ScreenDestination.Community },
                          onNavigateToUniversityAdmin = { currentDestination = ScreenDestination.UniversityAdmin },
                          onNavigateToAuth = { currentDestination = ScreenDestination.CyberAuth },
                          onNavigateToCognitiveProfile = { currentDestination = ScreenDestination.CognitiveProfile },
                          onNavigateToSecurityCenter = { currentDestination = ScreenDestination.SecurityCenter }
                        )
                      }
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
          },
          onNavigateToMultiModalFusion = {
            currentDestination = ScreenDestination.MultiModalFusion
          },
          onNavigateToPersonalIntelligence = {
            currentDestination = ScreenDestination.PersonalIntelligence
          }
        )
      }

      // Cross-Device Sync and Conflict Management Dialog
      if (showSyncDialog) {
        SyncStateAndConflictDialog(
          onDismiss = { showSyncDialog = false }
        )
      }

      // Subscription & Clearance Paywall Dialog
      if (showSubscriptionDialog) {
        com.example.ui.components.SubscriptionPaywallDialog(
          currentSubscription = subscriptionState,
          onDismiss = { showSubscriptionDialog = false },
          onOpenObsidianPaywall = {
            showSubscriptionDialog = false
            currentDestination = ScreenDestination.SubscriptionPaywall
          }
        )
      }

      // Premium Upgrade Bento Box Bottom Sheet (Smooth ModalBottomSheet)
      if (showPremiumUpgradeSheet) {
        ModalBottomSheet(
          onDismissRequest = { showPremiumUpgradeSheet = false },
          containerColor = Color(0xFF050B14),
          dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF1E3A5F)) }
        ) {
          com.example.ui.screens.PremiumUpgradeScreen(
            onPurchaseSuccess = {
              showPremiumUpgradeSheet = false
            },
            onNavigateBack = {
              showPremiumUpgradeSheet = false
            }
          )
        }
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
