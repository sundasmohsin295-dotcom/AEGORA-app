package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.AegoraRepository
import com.example.ui.components.AegoraBottomNav
import com.example.ui.components.AegoraNavTab
import com.example.ui.components.AegoraTopBar
import com.example.ui.components.UniversalSearchDialog
import com.example.ui.screens.*
import com.example.ui.theme.AegoraTheme
import com.example.ui.theme.CyberBackground

sealed class ScreenDestination {
  data object MainHub : ScreenDestination()
  data object Onboarding : ScreenDestination()
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

    var showSearchDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }

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

        is ScreenDestination.ProfileSettings -> {
          ProfileAndSettingsScreen(
            onNavigateBack = { currentDestination = ScreenDestination.MainHub },
            onRestartOnboarding = { currentDestination = ScreenDestination.Onboarding },
            onNavigateToVault = { currentDestination = ScreenDestination.KnowledgeVault },
            onNavigateToCommunity = { currentDestination = ScreenDestination.Community },
            onNavigateToUniversityAdmin = { currentDestination = ScreenDestination.UniversityAdmin }
          )
        }

        is ScreenDestination.MainHub -> {
          Scaffold(
            topBar = {
              AegoraTopBar(
                userProfile = userProfile,
                onSearchClick = { showSearchDialog = true },
                onNotificationClick = { showNotificationDialog = true },
                onProfileClick = { currentDestination = ScreenDestination.ProfileSettings }
              )
            },
            bottomBar = {
              AegoraBottomNav(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
              )
            },
            containerColor = CyberBackground
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    }
                  )
                }
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
          }
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
