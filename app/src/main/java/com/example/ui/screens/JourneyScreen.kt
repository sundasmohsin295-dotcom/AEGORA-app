package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.RoadmapPhase
import com.example.model.RoadmapTopic
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun JourneyScreen(
  onNavigateToLesson: (String) -> Unit,
  onNavigateToLab: () -> Unit,
  onNavigateToProjects: () -> Unit,
  modifier: Modifier = Modifier
) {
  val phases by AegoraRepository.roadmapPhases.collectAsState()
  val userProfile by AegoraRepository.userProfile.collectAsState()
  var selectedPhaseIndex by remember { mutableIntStateOf(0) }
  var showRecalibrationNotice by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .cyberGridBackground()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header & Dynamic Recalibration Engine (Chamfered Cyber Geometry)
    item {
      Surface(
        shape = ChamferedCutCornerShape,
        color = CyberSurface,
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberCyan.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .background(
              Brush.linearGradient(
                listOf(
                  CyberCyan.copy(alpha = 0.06f),
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
              Text(
                text = "CAREER ROADMAP TRAJECTORY",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  color = CyberCyan
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "SOC Analyst Master Track",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                color = TextPrimaryDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Paced at ${userProfile.dailyCommitment.label} • Target: ${userProfile.targetTimeline.label}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark
              )
            }

            IconButton(
              onClick = { showRecalibrationNotice = !showRecalibrationNotice },
              modifier = Modifier.testTag("journey_recalibrate_btn")
            ) {
              Icon(
                imageVector = Icons.Default.AutoMode,
                contentDescription = "AI Recalibrate",
                tint = CyberCyan
              )
            }
          }

          AnimatedVisibility(visible = showRecalibrationNotice) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .clip(ChamferedCutCornerShape)
                .background(CyberSurfaceElevated)
                .border(1.dp, CyberCyan.copy(alpha = 0.4f), ChamferedCutCornerShape)
                .padding(12.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  "AEGORA Adaptive Career Engine",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = CyberCyan
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                "Roadmap is dynamically optimized based on your DNS practical lab accuracy (+96%) and Active Directory Kerberos triage gaps. Next sprint prioritizes Sysmon Event 1 & Memory Analysis.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark
              )
            }
          }
        }
      }
    }

    // 2. Month / Phase Tab Selector with Hexagonal & Skewed Indicators
    item {
      CyberSectionHeader(
        title = "Curated Phases (6 Months)",
        subtitle = "From Systems Telemetry to Capstone Portfolio"
      )

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        phases.forEachIndexed { index, phase ->
          val isSelected = index == selectedPhaseIndex
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(ChamferedCutCornerShape)
              .clickable { selectedPhaseIndex = index }
              .border(
                1.2.dp,
                if (isSelected) CyberCyan else CyberBorder,
                ChamferedCutCornerShape
              ),
            color = if (isSelected) CyberSurfaceElevated else CyberSurface
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "M${phase.monthNumber}",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) CyberCyan else TextSecondaryDark
              )
              Text(
                text = if (phase.isCurrent) "ACTIVE" else if (phase.monthNumber == 1) "68%" else "QUEUED",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                ),
                color = if (phase.isCurrent) CyberEmerald else TextTertiaryDark
              )
            }
          }
        }
      }
    }

    // 3. Selected Phase Details & Capstone Milestone Project
    val currentPhase = phases.getOrElse(selectedPhaseIndex) { phases.first() }

    item {
      PulsingBreathingContainer(
        pulseColor = if (currentPhase.isCurrent) CyberEmerald else CyberCyan,
        isActive = currentPhase.isCurrent,
        modifier = Modifier.fillMaxWidth()
      ) {
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = CyberSurface,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "PHASE ${currentPhase.monthNumber}: ${currentPhase.focusDomain.uppercase()}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                  )
                )
                Text(
                  text = currentPhase.title,
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }

              if (currentPhase.isCurrent) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CyberEmerald.copy(alpha = 0.2f),
                  border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald)
                ) {
                  Text(
                    text = "CURRENT SPRINT",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = CyberEmerald,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = currentPhase.description,
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Endowed Phase Progress
            EndowedProgressBar(
              progress = if (currentPhase.isCurrent) 0.45f else if (currentPhase.monthNumber == 1) 0.68f else 0.1f,
              endowedBonus = 0.18f,
              label = "Phase Completion Momentum",
              valueText = if (currentPhase.isCurrent) "45% Complete" else if (currentPhase.monthNumber == 1) "68% Complete" else "Locked",
              barColor = if (currentPhase.isCurrent) CyberEmerald else CyberCyan
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Capstone Milestone Project Banner
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(ChamferedCutCornerShape)
                .clickable { onNavigateToProjects() },
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberIndigo.copy(alpha = 0.6f))
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(HexagonShape)
                    .background(CyberIndigo.copy(alpha = 0.25f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "CAPSTONE MILESTONE PROJECT",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      color = CyberCyan
                    )
                  )
                  Text(
                    text = currentPhase.milestoneProject,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan)
              }
            }
          }
        }
      }
    }

    // 4. Topics list in this Phase with Curiosity Gaps & Glitch Text
    item {
      CyberSectionHeader(
        title = "Topics & Practical Labs",
        subtitle = "${currentPhase.topics.size} progressive hands-on modules"
      )
    }

    items(currentPhase.topics) { topic ->
      TopicItemCard(
        topic = topic,
        onTopicClick = {
          if (!topic.isLocked) {
            onNavigateToLesson(if (topic.id == "top_102") "les_101" else "les_102")
          }
        }
      )
    }
  }
}

@Composable
fun TopicItemCard(
  topic: RoadmapTopic,
  onTopicClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      when {
        topic.isCompleted -> CyberEmerald.copy(alpha = 0.5f)
        topic.isLocked -> CyberBorderSubtle
        else -> CyberCyan.copy(alpha = 0.5f)
      }
    ),
    modifier = modifier
      .fillMaxWidth()
      .clickable(enabled = !topic.isLocked) { onTopicClick() }
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
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
              .size(36.dp)
              .clip(HexagonShape)
              .background(
                when {
                  topic.isCompleted -> CyberEmerald.copy(alpha = 0.2f)
                  topic.isLocked -> Color(0xFF2B2633)
                  else -> CyberCyan.copy(alpha = 0.2f)
                }
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = when {
                topic.isCompleted -> Icons.Default.Check
                topic.isLocked -> Icons.Default.Lock
                else -> Icons.Default.PlayArrow
              },
              contentDescription = null,
              tint = when {
                topic.isCompleted -> CyberEmerald
                topic.isLocked -> Color(0xFFFFB4AB)
                else -> CyberCyan
              },
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = topic.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = if (topic.isLocked) TextSecondaryDark else TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${topic.category} • ${topic.estimatedHours} hrs",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark
              )
              if (topic.hasPracticalLab) {
                Text(
                  text = " • Practical Lab",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberCyan
                )
              }
            }
          }
        }

        if (!topic.isLocked) {
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = CyberCyan,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Curiosity Gap: If topic is locked, display classified glitch text
      if (topic.isLocked) {
        Spacer(modifier = Modifier.height(10.dp))
        CuriosityGlitchText(
          secretText = "PREREQUISITE LAB: Complete Sysmon Event ID 1 & PCAP triage to decrypt",
          classification = "CLASSIFIED TOPIC // REQUIRES EVIDENCE",
          modifier = Modifier.fillMaxWidth()
        )
      } else if (topic.keyConcepts.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          topic.keyConcepts.forEach { concept ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
            ) {
              Text(
                text = concept,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                color = TextSecondaryDark,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}
