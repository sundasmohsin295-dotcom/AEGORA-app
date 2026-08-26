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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.RoadmapPhase
import com.example.model.RoadmapTopic
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
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
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header & Recalibration Engine
    item {
      CyberCard(
        borderColor = CyberCyan.copy(alpha = 0.4f),
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "PERSONAL CYBER JOURNEY",
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "SOC Analyst Master Track",
              style = MaterialTheme.typography.headlineLarge,
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
              .clip(RoundedCornerShape(8.dp))
              .background(CyberSurfaceElevated)
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("AEGORA Adaptive Engine", style = MaterialTheme.typography.titleMedium, color = CyberCyan)
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

    // Month / Phase Tab Selector
    item {
      CyberSectionHeader(
        title = "Curated Phases (6 Months)",
        subtitle = "From Systems Telemetry to Professional Portfolio"
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
              .clip(RoundedCornerShape(8.dp))
              .clickable { selectedPhaseIndex = index }
              .border(
                1.dp,
                if (isSelected) CyberCyan else CyberBorder,
                RoundedCornerShape(8.dp)
              ),
            color = if (isSelected) CyberSurfaceElevated else CyberSurface
          ) {
            Column(
              modifier = Modifier.padding(vertical = 8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "M${phase.monthNumber}",
                style = MaterialTheme.typography.labelLarge.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) CyberCyan else TextSecondaryDark
              )
              Text(
                text = if (phase.isCurrent) "ACTIVE" else if (phase.monthNumber == 1) "68%" else "QUEUED",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = if (phase.isCurrent) CyberEmerald else TextTertiaryDark
              )
            }
          }
        }
      }
    }

    // Selected Phase Details
    val currentPhase = phases.getOrElse(selectedPhaseIndex) { phases.first() }

    item {
      CyberCard(
        borderColor = if (currentPhase.isCurrent) CyberEmerald.copy(alpha = 0.5f) else CyberBorder
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "PHASE ${currentPhase.monthNumber}: ${currentPhase.focusDomain.uppercase()}",
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan
            )
            Text(
              text = currentPhase.title,
              style = MaterialTheme.typography.headlineMedium,
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
                style = MaterialTheme.typography.labelSmall,
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

        Spacer(modifier = Modifier.height(14.dp))

        // Milestone Project Banner
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onNavigateToProjects() },
          color = CyberSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberIndigo.copy(alpha = 0.6f))
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CyberIndigo.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.FolderSpecial, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "CAPSTONE MILESTONE PROJECT",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
              )
              Text(
                text = currentPhase.milestoneProject,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark
              )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
          }
        }
      }
    }

    // Topics list in this Phase
    item {
      CyberSectionHeader(
        title = "Topics & Practical Labs",
        subtitle = "${currentPhase.topics.size} structured modules"
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
  CyberCard(
    modifier = modifier,
    borderColor = if (topic.isCompleted) CyberEmerald.copy(alpha = 0.4f) else CyberBorder,
    onClick = if (!topic.isLocked) onTopicClick else null
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
            .size(32.dp)
            .clip(CircleShape)
            .background(
              when {
                topic.isCompleted -> CyberEmerald.copy(alpha = 0.2f)
                topic.isLocked -> CyberSurfaceElevated
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
            } ,
            contentDescription = null,
            tint = when {
              topic.isCompleted -> CyberEmerald
              topic.isLocked -> TextTertiaryDark
              else -> CyberCyan
            },
            modifier = Modifier.size(18.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = topic.title,
            style = MaterialTheme.typography.titleMedium,
            color = if (topic.isLocked) TextTertiaryDark else TextPrimaryDark
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
                style = MaterialTheme.typography.labelSmall,
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
          tint = TextSecondaryDark,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    if (topic.keyConcepts.isNotEmpty() && !topic.isLocked) {
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
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}
