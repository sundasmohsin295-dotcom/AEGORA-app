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
  modifier: Modifier = Modifier
) {
  val userProfile by AegoraRepository.userProfile.collectAsState()
  val dailyMission by AegoraRepository.dailyMission.collectAsState()
  val reviewQueue by AegoraRepository.reviewQueue.collectAsState()
  val skillDecays by AegoraRepository.skillDecayForecasts.collectAsState()
  val sessionState by AegoraRepository.crossDeviceSession.collectAsState()
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
    // 0. Universal Cross-Device Continue Hero
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

          Spacer(modifier = Modifier.height(12.dp))

          // Next Best Action Prompt (Chamfered Surface)
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = ChamferedCutCornerShape,
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "RECOMMENDED SPRINT",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontSize = 9.sp
                  ),
                  color = CyberCyan
                )
                Text(
                  text = "Sysmon Event ID 1 & Triage",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }

              Button(
                onClick = { onNavigateToLesson("les_102") },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("home_start_next_action")
              ) {
                Text("Resume", color = Color.Black, fontWeight = FontWeight.Bold)
              }
            }
          }
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

    // 7. Quick Access Shortcuts
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
