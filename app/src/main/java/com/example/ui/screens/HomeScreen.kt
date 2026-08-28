package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.DailyMission
import com.example.model.UserProfile
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  onNavigateToJourney: () -> Unit,
  onNavigateToLabs: () -> Unit,
  onNavigateToAi: () -> Unit,
  onNavigateToPassport: () -> Unit,
  onNavigateToLesson: (String) -> Unit,
  onNavigateToIntelligence: () -> Unit,
  onNavigateToCareers: () -> Unit,
  onNavigateToVault: () -> Unit = {},
  onNavigateToCommunity: () -> Unit = {},
  onNavigateToUniversity: () -> Unit = {},
  onNavigateToShadowRange: () -> Unit = {},
  onNavigateToTimelineFork: () -> Unit = {},
  onNavigateToBioStress: () -> Unit = {},
  onNavigateToCrisisWarRoom: () -> Unit = {},
  onNavigateToZeroDayLab: () -> Unit = {},
  onNavigateToGlobalRadar: () -> Unit = {},
  onNavigateToSwarmArena: () -> Unit = {},
  onNavigateToBinaryDisassembler: () -> Unit = {},
  onNavigateToCyberTerminal: () -> Unit = {},
  onNavigateToLiveSocRange: () -> Unit = {},
  onNavigateToThreatAcoustic: () -> Unit = {},
  onNavigateToResourceUniverse: () -> Unit = {},
  onNavigateToCyberExpertEngine: () -> Unit = {},
  onNavigateToWorkplaceSimulator: () -> Unit = {},
  onNavigateToSecurityCenter: () -> Unit = {},
  onNavigateToAuth: () -> Unit = {},
  onNavigateToMultiModalFusion: () -> Unit = {},
  onNavigateToGenome: () -> Unit = {},
  onNavigateToPurpleArena: () -> Unit = {},
  onNavigateToSocShiftSimulator: () -> Unit = {},
  onNavigateToVoiceSocAndMultiverse: () -> Unit = {},
  onNavigateToIntelligenceConnective: () -> Unit = {},
  onNavigateToCyberReality: () -> Unit = {},
  onNavigateToCyberOperatingSystem: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val userProfile by AegoraRepository.userProfile.collectAsState()
  val dailyMission by AegoraRepository.dailyMission.collectAsState()
  val cyberTwinV8 by AegoraRepository.cyberTwinV8.collectAsState()
  val todaysMissionV8 by AegoraRepository.todaysMissionV8.collectAsState()
  val reviewQueue by AegoraRepository.reviewQueue.collectAsState()
  val skillDecays by AegoraRepository.skillDecayForecasts.collectAsState()
  val sessionState by AegoraRepository.crossDeviceSession.collectAsState()
  val predictiveActions by AegoraRepository.predictiveNextActions.collectAsState()
  val behavioralPacing by AegoraRepository.behavioralPacing.collectAsState()
  val dueReviewCount = reviewQueue.count { it.isDue }

  var showRewardModal by remember { mutableStateOf(false) }

  val activeCareer = AegoraRepository.careerRoles.find { it.id == userProfile.targetCareerId }
    ?: AegoraRepository.careerRoles.first()

  if (showRewardModal) {
    DecryptingCacheDialog(
      onDismiss = { showRewardModal = false },
      reward = DecryptedReward(
        xpMultiplier = "3.5x CRITICAL SURGE",
        bonusXp = 500,
        rareBadgeName = "Master SOC Telemetry Infiltrator",
        rareBadgeIcon = Icons.Default.Shield,
        loreFragment = "Declassified Memo #902: Sysmon telemetry detected evasive reflective DLL injection across memory space."
      )
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .cyberGridBackground()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 0a. Ambient Co-Pilot Grounded Observation Chip
    item {
      AmbientCoPilotSurface(
        onAskAi = { prompt -> onNavigateToAi() }
      )
    }

    // 0a-1. AEGORA v10.0 PERSONAL CYBER OPERATING SYSTEM MASTER BANNER
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurface,
        border = BorderStroke(1.5.dp, NeonGreen),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToCyberOperatingSystem() }
          .testTag("home_cyber_os_v10_card")
      ) {
        Column(
          modifier = Modifier
            .background(
              Brush.horizontalGradient(
                listOf(NeonGreen.copy(alpha = 0.15f), CyberSurface)
              )
            )
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(HexagonShape)
                  .background(NeonGreen.copy(alpha = 0.2f))
                  .border(1.2.dp, NeonGreen, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Hub, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(26.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "AEGORA v12.0",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      letterSpacing = 0.8.sp
                    ),
                    color = NeonGreen
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    color = NeonGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "PERSONAL CYBER OS & REALITY",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = NeonGreen,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
                Text(
                  text = "Command Center & Cyber Reality",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = NeonGreen,
              modifier = Modifier.clickable { onNavigateToCyberOperatingSystem() }
            ) {
              Text(
                text = "OPEN OS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                ),
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Cyber Twin 6.0 (27 Dimensions) • Reality Graph • Decision Lab • Attacker Journey • Crime Scene Forensics • SOC Shift • Research Desk • Boss Incidents",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
            color = TextSecondaryDark
          )
        }
      }
    }

    // 0a-2. AEGORA v9.0 CYBER REALITY ENGINE MASTER BANNER
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurface,
        border = BorderStroke(1.2.dp, CyberCyan),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToCyberReality() }
          .testTag("home_cyber_reality_v9_card")
      ) {
        Column(
          modifier = Modifier
            .background(
              Brush.horizontalGradient(
                listOf(CyberCyan.copy(alpha = 0.12f), CyberSurface)
              )
            )
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(CyberCyan.copy(alpha = 0.2f))
                  .border(1.2.dp, CyberCyan, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Radar, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "AEGORA v9.0",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      letterSpacing = 0.8.sp
                    ),
                    color = CyberCyan
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    color = Color(0xFF00E676).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "ONLINE",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF00E676),
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
                Text(
                  text = "Cyber Reality Engine",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberCyan,
              modifier = Modifier.clickable { onNavigateToCyberReality() }
            ) {
              Text(
                text = "OPEN v9.0",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                ),
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Personal Cyber Radar • Constellation 3.0 • Job Description Lab Builder • Workplace 2.0 • Multiverse Consequences • Feynman Arena",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
            color = TextSecondaryDark
          )
        }
      }
    }

    // 0b. Self-Reported Flow & Behavioral Pacing Suggestion (Zero Biometrics, 100% Honest Telemetry)
    if (behavioralPacing.shouldSuggestPacing) {
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = CyberSurfaceElevated,
          border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.6f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("behavioral_pacing_banner")
        ) {
          Column(
            modifier = Modifier
              .background(
                Brush.horizontalGradient(
                  listOf(CyberAmber.copy(alpha = 0.12f), CyberSurfaceElevated)
                )
              )
              .padding(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.SelfImprovement,
                  contentDescription = null,
                  tint = CyberAmber,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "HONEST PACING ENGINE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  ),
                  color = CyberAmber
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = CyberSurface,
                  border = BorderStroke(0.5.dp, CyberBorder)
                ) {
                  Text(
                    text = "${behavioralPacing.sessionDurationMins}m session • ${behavioralPacing.recentMistakeCount} tough triage checks",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                  )
                }
              }

              IconButton(
                onClick = { AegoraRepository.dismissBehavioralPacing() },
                modifier = Modifier.size(22.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextSecondaryDark, modifier = Modifier.size(14.dp))
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = behavioralPacing.questionPrompt,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  AegoraRepository.dismissBehavioralPacing()
                  onNavigateToVault()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = behavioralPacing.suggestedActionTitle,
                  color = Color.Black,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp
                )
              }

              OutlinedButton(
                onClick = { AegoraRepository.dismissBehavioralPacing() },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.weight(0.6f)
              ) {
                Text("Continue", color = TextSecondaryDark, fontSize = 11.sp)
              }
            }
          }
        }
      }
    }

    // 0c. Universal Cross-Device Continue Hero
    item {
      com.example.ui.adaptive.UniversalContinueHero(
        sessionState = sessionState,
        onContinueAction = { screenTag ->
          when (screenTag) {
            "live_soc_range" -> onNavigateToLiveSocRange()
            "binary_disassembler" -> onNavigateToBinaryDisassembler()
            "shadow_range" -> onNavigateToShadowRange()
            "threat_acoustic" -> onNavigateToThreatAcoustic()
            "crisis_war_room" -> onNavigateToCrisisWarRoom()
            "zero_day_lab" -> onNavigateToZeroDayLab()
            "swarm_arena" -> onNavigateToSwarmArena()
            "cyber_terminal" -> onNavigateToCyberTerminal()
            else -> onNavigateToLabs()
          }
        }
      )
    }

    // 1. Hero Command Center Banner (Non-Standard Chamfered Geometry & Endowed Progress)
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(ChamferedCutCornerShape)
          .border(1.5.dp, CyberCyan.copy(alpha = 0.7f), ChamferedCutCornerShape),
        color = CyberSurface,
        tonalElevation = 3.dp
      ) {
        Column(
          modifier = Modifier
            .background(
              Brush.linearGradient(
                listOf(
                  CyberCyan.copy(alpha = 0.10f),
                  CyberSurface
                )
              )
            )
            .padding(18.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = CyberCyan.copy(alpha = 0.15f),
                  border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
                ) {
                  Text(
                    text = "TARGET CAREER TRACK",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp,
                      fontSize = 9.sp
                    ),
                    color = CyberCyan,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = activeCareer.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.Black
                ),
                color = TextPrimaryDark
              )
              Text(
                text = "Phase 1 • Foundational Systems & Telemetry",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
              )
            }

            // Hexagonal Job Readiness Badge
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(HexagonShape)
                .background(CyberCyan.copy(alpha = 0.15f))
                .border(1.5.dp, CyberCyan, HexagonShape),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "${userProfile.jobReadinessScore}%",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                  ),
                  color = CyberCyan
                )
                Text(
                  text = "READY",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  ),
                  color = TextSecondaryDark
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Endowed Momentum Progress Bar
          EndowedProgressBar(
            progress = userProfile.jobReadinessScore / 100f,
            endowedBonus = 0.18f,
            label = "Career Milestone Momentum",
            valueText = "${userProfile.jobReadinessScore}% (Goal: 100%)",
            barColor = CyberCyan,
            bonusColor = CyberEmerald
          )

          Spacer(modifier = Modifier.height(14.dp))

          // PREDICTIVE NEXT-ACTION ENGINE (Grounded & Explainable)
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Insights, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "PREDICTIVE NEXT-ACTION ENGINE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp
                  ),
                  color = CyberCyan
                )
              }
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberCyan.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "Data-Grounded",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                  color = CyberCyan,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Render top ranked predictive actions
            predictiveActions.take(2).forEach { action ->
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                color = CyberSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (action.urgencyScore >= 90) NeonCrimson.copy(alpha = 0.6f) else CyberBorder
                )
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        action.reasoningTags.forEach { tag ->
                          Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = if (tag.contains("Critical") || tag.contains("Decay"))
                              NeonCrimson.copy(alpha = 0.18f)
                            else CyberCyan.copy(alpha = 0.12f)
                          ) {
                            Text(
                              text = tag,
                              style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                              ),
                              color = if (tag.contains("Critical") || tag.contains("Decay"))
                                NeonCrimson
                              else CyberCyan,
                              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                          }
                        }
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = action.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                      )
                    }

                    Button(
                      onClick = {
                        when (action.destinationTag) {
                          "live_soc_range" -> onNavigateToLiveSocRange()
                          "knowledge_vault" -> onNavigateToVault()
                          "lab_simulator" -> onNavigateToLabs()
                          else -> onNavigateToLesson("les_102")
                        }
                      },
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (action.urgencyScore >= 90) NeonCrimson else CyberCyan
                      ),
                      shape = RoundedCornerShape(8.dp),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                      Text(
                        text = "Resolve",
                        color = if (action.urgencyScore >= 90) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "💡 Grounding: ${action.primaryReason}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                    color = TextSecondaryDark
                  )
                }
              }
            }
          }
        }
      }
    }

    // 1b. AEGORA Cyber Expert Engine Master Launcher (10-Tier Ladder & Personal Cyber Twin)
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurface,
        border = BorderStroke(1.2.dp, NeonCyan),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToCyberExpertEngine() }
          .testTag("home_cyber_expert_engine_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(NeonCyan.copy(alpha = 0.2f))
                  .border(1.2.dp, NeonCyan, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "CYBER EXPERT DEVELOPMENT ENGINE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = NeonCyan
                )
                Text(
                  text = "10-Tier Ladder • Cyber Twin • Judgment Simulator",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberEmerald.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
            ) {
              Text(
                text = "TIER L3 → L4",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Track capability over completion, resolve root-cause prerequisite gaps with 1-click diagnostic drills, and test real-world judgment under uncertainty.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 1c. PROFESSIONAL WORKPLACE EXPERIENCE ENGINE (First Day on the Job, Live Shift, Manager Slack, Reasoning Graph)
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurfaceElevated,
        border = BorderStroke(1.2.dp, CyberEmerald),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToWorkplaceSimulator() }
          .testTag("home_workplace_simulator_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(CyberEmerald.copy(alpha = 0.2f))
                  .border(1.2.dp, CyberEmerald, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.WorkHistory, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "PROFESSIONAL WORK EXPERIENCE SIMULATOR",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = CyberEmerald
                )
                Text(
                  text = "Live Shifts • Tickets • Slack • Consequence Tree",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberEmerald.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
            ) {
              Text(
                text = "SHIFT ACTIVE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Experience authentic cybersecurity work in living virtual enterprises. Investigate multi-source telemetry, brief AI managers, manage business downtime trade-offs, and construct forensic reasoning graphs.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 1d. AEGORA ZERO-TRUST SECURITY CENTER & PASSKEY IDENTITY GATEWAY
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurfaceElevated,
        border = BorderStroke(1.2.dp, NeonCyan),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToSecurityCenter() }
          .testTag("home_security_center_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(NeonCyan.copy(alpha = 0.2f))
                  .border(1.2.dp, NeonCyan, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "ZERO-TRUST SECURITY CENTER",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = NeonCyan
                )
                Text(
                  text = "Passkeys • Risk Engine • Attack Sim",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberEmerald.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
            ) {
              Text(
                text = "POSTURE 92%",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Production-grade Zero-Trust Identity management: FIDO2 Passkeys, hardware-backed Keystore attestation, Adaptive Risk telemetry, and interactive attack defense simulations.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 1d2. CYBER LEARNING GENOME 2.0 & MULTI-DIMENSIONAL INTELLIGENCE
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurfaceElevated,
        border = BorderStroke(1.2.dp, CyberCyan),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToGenome() }
          .testTag("home_cyber_genome_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(CyberCyan.copy(alpha = 0.2f))
                  .border(1.2.dp, CyberCyan, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "CYBER LEARNING GENOME 2.0",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = CyberCyan
                )
                Text(
                  text = "8-Dim Competency • Fingerprint • Transfer",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberCyan.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
              Text(
                text = "v7.0 GENOME",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Track capability across 8 multi-dimensional vectors, audit your Investigation Fingerprint against the Training Reference Model, and execute Cross-Domain Transfer challenges.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 1d2b. AEGORA v8.1 THE INTELLIGENCE CONNECTIVE LAYER (Centralized Orchestrator)
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurfaceElevated,
        border = BorderStroke(1.4.dp, CyberCyan),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToIntelligenceConnective() }
          .testTag("home_intelligence_connective_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(HexagonShape)
                  .background(CyberCyan.copy(alpha = 0.2f))
                  .border(1.2.dp, CyberCyan, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Hub, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(26.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "THE INTELLIGENCE CONNECTIVE LAYER",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  ),
                  color = CyberCyan
                )
                Text(
                  text = "Autonomous Cyber Learning OS",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberCyan.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
              Text(
                text = "v8.1 CORE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Every learner action generates cryptographically verifiable evidence, updates the living Cyber Twin 3.0, audits Mistake DNA 2.0, and selects the Next Best Action across 18 pedagogical learning models.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("• 9 Vector Twin 3.0", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberGreen)
            Text("• Time-Aware NBA", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberGold)
            Text("• Mistake DNA 2.0", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberCrimson)
          }
        }
      }
    }

    // 1d3. AEGORA v8.0 SIGNATURE PURPLE TEAM ARENA ("SELF VS SELF")
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurfaceElevated,
        border = BorderStroke(1.2.dp, CyberCrimson),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToPurpleArena() }
          .testTag("home_purple_arena_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(CyberCrimson.copy(alpha = 0.2f))
                  .border(1.2.dp, CyberCrimson, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = CyberCrimson, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "PURPLE TEAM ARENA (SELF VS SELF)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = CyberCrimson
                )
                Text(
                  text = "5-Round Adversary Breach Duel",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberCrimson.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberCrimson.copy(alpha = 0.4f))
            ) {
              Text(
                text = "v8.0 SIGNATURE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberCrimson,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Stage an authorized red team attack playbook, synthesize realistic EDR telemetry, switch to blue team to investigate, contain the breach, and face the forensic debrief: 'Would you have caught yourself?'",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 1d4. REAL SOC SHIFT SIMULATOR
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurfaceElevated,
        border = BorderStroke(1.2.dp, CyberCyan),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToSocShiftSimulator() }
          .testTag("home_soc_shift_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(CyberCyan.copy(alpha = 0.2f))
                  .border(1.2.dp, CyberCyan, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Dns, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "REAL SOC SHIFT SIMULATOR",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = CyberCyan
                )
                Text(
                  text = "Tier 1 Ingest Queue & Alert Decisions",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberCyan.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
              Text(
                text = "v8.0 LIVE QUEUE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Process concurrent SIEM/EDR alerts under shift time constraints, isolate true positives from noisy false positives, and earn forensic triage scores.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 1d5. VOICE SOC CRISIS DRILL & MULTIVERSE REPLAY
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurfaceElevated,
        border = BorderStroke(1.2.dp, CyberEmerald),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToVoiceSocAndMultiverse() }
          .testTag("home_voice_multiverse_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(CyberEmerald.copy(alpha = 0.2f))
                  .border(1.2.dp, CyberEmerald, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "VOICE SOC & MULTIVERSE REPLAY",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = CyberEmerald
                )
                Text(
                  text = "Executive Crisis Calls & Alternate Timelines",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberEmerald.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
            ) {
              Text(
                text = "v8.0 DRILLS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Calm panicked stakeholders without jargon traps, and explore alternate counterfactual branches ('What if containment was delayed 15 minutes?').",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 1e. MULTIMODAL FUSION ENGINE (Synchronized Graph Topology + Audio Voice Stream)
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurfaceElevated,
        border = BorderStroke(1.2.dp, CyberCyan),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToMultiModalFusion() }
          .testTag("home_multimodal_fusion_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(CyberCyan.copy(alpha = 0.2f))
                  .border(1.2.dp, CyberCyan, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "MULTIMODAL FUSION ENGINE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = CyberCyan
                )
                Text(
                  text = "Synchronized Voice & Topology Graph",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberCyan.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
              Text(
                text = "AUDIO SYNC",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Correlate audio incident briefings directly with live interactive visual network graphs. As narration speaks, relevant attack nodes highlight and sync in real time.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 2. Behavioral Attention: Spaced Repetition Due Card with Breathing Pulse Glow
    item {
      PulsingBreathingContainer(
        pulseColor = if (dueReviewCount > 0) CyberAmber else CyberCyan,
        isActive = dueReviewCount > 0,
        modifier = Modifier.fillMaxWidth()
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (dueReviewCount > 0) CyberSurfaceElevated else CyberSurface,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (dueReviewCount > 0) CyberAmber.copy(alpha = 0.5f) else CyberBorderSubtle
          ),
          modifier = Modifier.clickable { onNavigateToVault() }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(HexagonShape)
                  .background(if (dueReviewCount > 0) CyberAmber.copy(alpha = 0.15f) else CyberCyan.copy(alpha = 0.15f))
                  .border(
                    1.dp,
                    if (dueReviewCount > 0) CyberAmber else CyberCyan,
                    HexagonShape
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (dueReviewCount > 0) Icons.Default.PsychologyAlt else Icons.Default.Bookmark,
                  contentDescription = null,
                  tint = if (dueReviewCount > 0) CyberAmber else CyberCyan,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = if (dueReviewCount > 0) "SPACED REPETITION QUEUE" else "KNOWLEDGE VAULT",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 9.sp
                  ),
                  color = if (dueReviewCount > 0) CyberAmber else CyberCyan
                )
                Text(
                  text = if (dueReviewCount > 0) "$dueReviewCount Flashcards Due for Review" else "Personal Notes & Bookmarks",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Button(
              onClick = onNavigateToVault,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (dueReviewCount > 0) CyberAmber else CyberCyan,
                contentColor = Color.Black
              )
            ) {
              Text(if (dueReviewCount > 0) "Review" else "Open", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2b. Skill Decay Radar (Forgetting Forecast)
    item {
      CyberCard(
        borderColor = CyberAmber.copy(alpha = 0.4f),
        backgroundColor = CyberSurface,
        shapeRadius = 20.dp
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(HexagonShape)
                .background(CyberAmber.copy(alpha = 0.15f))
                .border(1.dp, CyberAmber, HexagonShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "SKILL DECAY FORECAST & RETENTION",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp,
                  fontSize = 9.sp
                ),
                color = CyberAmber
              )
              Text(
                text = "Predictive Memory Degradation",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = CyberAmber.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.5f))
          ) {
            Text(
              text = "EBBINGHAUS AI",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = CyberAmber,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val highestRiskSkill = skillDecays.find { it.riskLevel == "HIGH" } ?: skillDecays.first()
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = CyberSurfaceElevated,
          border = BorderStroke(1.dp, CyberBorderSubtle)
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = highestRiskSkill.skillName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
              Text(
                text = "${highestRiskSkill.currentHealth}% Retention",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = if (highestRiskSkill.currentHealth < 60) CyberCrimson else CyberAmber
              )
            }

            Text(
              text = "Projected decay: ${highestRiskSkill.currentHealth}% → ${highestRiskSkill.projected30Days}% in 30 days without reinforcement.",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "⚡ Drill: ${highestRiskSkill.recommendedDiagnosticTitle}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp
                ),
                color = CyberCyan,
                modifier = Modifier.weight(1f)
              )
              Button(
                onClick = onNavigateToLabs,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
              ) {
                Text("Start Drill", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 3. Tactile 'Hold-to-Hack' Active Daily Mission (Skewed Parallelogram Shape)
    item {
      CyberMissionCard(
        title = dailyMission.title,
        subtitle = dailyMission.scenarioContext,
        tag = "Daily Mission • +${dailyMission.xpReward} XP",
        tagColor = CyberMagenta,
        onClick = onNavigateToLabs
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated
          ) {
            Text(
              text = "${dailyMission.difficulty} • ${dailyMission.estimatedTimeMinutes}m Estimated",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          if (!dailyMission.isCompleted) {
            HoldToHackButton(
              text = "HOLD TO COMPLETE",
              onComplete = {
                AegoraRepository.completeDailyMission()
                showRewardModal = true
              },
              primaryColor = CyberMagenta,
              activeColor = CyberEmerald,
              testTag = "home_complete_mission_btn"
            )
          } else {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberEmerald.copy(alpha = 0.2f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald)
            ) {
              Text(
                text = "✓ Solved & Verified",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // 4. Hexagonal Skill Radar Constellation Overview
    item {
      CyberSectionHeader(
        title = "Skill Radar & Constellation",
        subtitle = "Multi-vector mastery nodes",
        actionText = "Full Graph",
        onActionClick = onNavigateToPassport
      )

      CyberCard(
        borderColor = CyberCyan.copy(alpha = 0.4f),
        backgroundColor = CyberSurface,
        shapeRadius = 20.dp
      ) {
        Text(
          text = "CORE TELEMETRY CLUSTER",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = CyberCyan
          )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          HexagonalSkillNode(
            title = "PCAP Analysis",
            level = 4,
            masteryPct = 85,
            icon = Icons.Default.NetworkCheck,
            accentColor = CyberCyan,
            isSelected = true,
            onClick = onNavigateToPassport
          )
          HexagonalSkillNode(
            title = "Sysmon / EDR",
            level = 3,
            masteryPct = 78,
            icon = Icons.Default.Shield,
            accentColor = CyberBlue,
            onClick = onNavigateToPassport
          )
          HexagonalSkillNode(
            title = "OWASP Web",
            level = 2,
            masteryPct = 63,
            icon = Icons.Default.Language,
            accentColor = CyberViolet,
            onClick = onNavigateToPassport
          )
          HexagonalSkillNode(
            title = "Cloud IAM",
            level = 2,
            masteryPct = 57,
            icon = Icons.Default.Cloud,
            accentColor = CyberAmber,
            onClick = onNavigateToPassport
          )
        }
      }
    }

    // 5. Curiosity Gaps & Redacted Classified Intel Card
    item {
      CyberSectionHeader(
        title = "Live Cyber Threat Intelligence",
        subtitle = "Zero-day advisories & CISA alerts",
        actionText = "Intel Hub",
        onActionClick = onNavigateToIntelligence
      )

      val threat = AegoraRepository.threatAdvisories.first()
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurface,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCrimson.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToIntelligence() }
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberCrimson.copy(alpha = 0.15f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberCrimson.copy(alpha = 0.4f))
            ) {
              Text(
                text = "${threat.cveId} • CVSS ${threat.cvssScore}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberCrimson,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Text(
              text = "LIVE ADVISORY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = CyberCrimson
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = threat.title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = threat.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            maxLines = 2
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Curiosity Gap: Classified Payload Signature
          CuriosityGlitchText(
            secretText = "IOC PAYLOAD: 0x4F77AC -> Kerberoast TGS-REQ Hashcat Rule",
            classification = "CONFIDENTIAL // NSA TAO SIGNATURE",
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // 6. Next-Gen Sovereign Cyber-Intelligence Innovations
    item {
      CyberSectionHeader(
        title = "Sovereign Intelligence & Cyber Range",
        subtitle = "Zero-day engines & autonomous simulations",
        actionText = "Radar Hub",
        onActionClick = onNavigateToGlobalRadar
      )

      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Shadow Agent Range
        Surface(
          shape = ChamferedCutCornerShape,
          color = CyberSurface,
          border = androidx.compose.foundation.BorderStroke(1.2.dp, NeonPink.copy(alpha = 0.6f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToShadowRange() }
            .testTag("home_shadow_agent_card")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NeonPink.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.SecurityUpdateWarning, contentDescription = null, tint = NeonPink, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "AUTONOMOUS ADVERSARY // SHADOW RANGE",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = NeonPink
              )
              Text("Dynamic Zero-Day Chains & Deception Grid", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NeonPink)
          }
        }

        // Timeline Fork & Time Machine
        Surface(
          shape = ChamferedCutCornerShape,
          color = CyberSurface,
          border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCyan.copy(alpha = 0.6f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToTimelineFork() }
            .testTag("home_timeline_fork_card")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CyberCyan.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.AltRoute, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "INCIDENT TIME-MACHINE & FORKING",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberCyan
              )
              Text("Dual-Timeline Hypothesis & Blast Diff", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan)
          }
        }

        // Crisis War Room & Executive Escalation
        Surface(
          shape = ChamferedCutCornerShape,
          color = CyberSurface,
          border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberAmber.copy(alpha = 0.6f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToCrisisWarRoom() }
            .testTag("home_crisis_war_room_card")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CyberAmber.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "VOICE WAR ROOM & CRISIS SIMULATOR",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberAmber
              )
              Text("Multi-Agent CISO/Legal/CEO Escalation", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberAmber)
          }
        }

        // Row of 2: Zero-Day Studio & Bio Stress HUD
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onNavigateToZeroDayLab() }
              .testTag("home_zero_day_lab_card")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Icon(Icons.Default.BugReport, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.height(6.dp))
              Text("ZERO-DAY LAB", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = CyberEmerald)
              Text("Sigma/YARA Studio", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberViolet.copy(alpha = 0.5f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onNavigateToBioStress() }
              .testTag("home_bio_stress_card")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Icon(Icons.Default.MonitorHeart, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.height(6.dp))
              Text("BIO STRESS HUD", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = CyberViolet)
              Text("Composure Tracker", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            }
          }
        }

        // Row of 2: Binary Disassembler & Tactical CLI Terminal
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onNavigateToBinaryDisassembler() }
              .testTag("home_disassembler_card")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Icon(Icons.Default.Code, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.height(6.dp))
              Text("DISASSEMBLER & CFG", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = NeonCyan)
              Text("x86-64 Hex Dissector", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen.copy(alpha = 0.6f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onNavigateToCyberTerminal() }
              .testTag("home_terminal_card")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Icon(Icons.Default.Terminal, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.height(6.dp))
              Text("TACTICAL TERMINAL", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = NeonGreen)
              Text("eBPF Sandboxed CLI", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            }
          }
        }

        // Row of 2: Live SOC Range & Threat Acoustic Sonification
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCrimson.copy(alpha = 0.6f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onNavigateToLiveSocRange() }
              .testTag("home_soc_range_card")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Icon(Icons.Default.SecurityUpdateWarning, contentDescription = null, tint = NeonCrimson, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.height(6.dp))
              Text("LIVE SOC RANGE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = NeonCrimson)
              Text("One-Tap SIEM Triage", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            }
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet.copy(alpha = 0.6f)),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable { onNavigateToThreatAcoustic() }
              .testTag("home_threat_acoustic_card")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.height(6.dp))
              Text("SONIC RADAR", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = NeonViolet)
              Text("Waveform Triage Drill", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            }
          }
        }
      }
    }

    // 7. Sequential Terminal & AppSec Ladders Hub (Parallelogram Cut)
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurface,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberEmerald.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToLabs() }
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(HexagonShape)
                  .background(CyberEmerald.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "SEQUENTIAL TERMINAL & APPSEC LADDERS",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = CyberEmerald
                )
                Text(
                  text = "Bandit Linux • PowerShell • Web • Binary Track",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberEmerald)
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Zero-hand-holding 16-level terminal ladders and live AppSec patch workflows directly connected to your career story.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 7. Resource Knowledge Universe Hub (V13 Engine)
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurface,
        border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.7f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onNavigateToResourceUniverse() }
          .testTag("home_resource_universe_card")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(HexagonShape)
                  .background(NeonCyan.copy(alpha = 0.18f))
                  .border(1.2.dp, NeonCyan, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "RESOURCE INTELLIGENCE UNIVERSE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                  ),
                  color = NeonCyan
                )
                Text(
                  text = "Authoritative Books, RFCs, Papers & Standards",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NeonCyan)
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Explore NIST SP 800-61, MITRE ATT&CK, PortSwigger WAHH, and IETF RFCs connected to your career path with transparent quality scores.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    // 8. Quick Access Shortcuts
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // AI Mentor
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberCyan.copy(alpha = 0.35f),
          backgroundColor = CyberSurface,
          shapeRadius = 20.dp,
          onClick = onNavigateToAi
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(HexagonShape)
              .background(CyberViolet.copy(alpha = 0.18f))
              .border(1.dp, CyberViolet, HexagonShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = null,
              tint = CyberViolet,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text("Aegora AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
          Text("SOC Mentor & Socratic Tutor", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        }

        // Career Tracks
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberAmber.copy(alpha = 0.35f),
          backgroundColor = CyberSurface,
          shapeRadius = 20.dp,
          onClick = onNavigateToCareers
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(HexagonShape)
              .background(CyberAmber.copy(alpha = 0.18f))
              .border(1.dp, CyberAmber, HexagonShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.WorkOutline,
              contentDescription = null,
              tint = CyberAmber,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text("Career Hub", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
          Text("6 Cybersecurity Roles", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        }
      }
    }
  }
}
