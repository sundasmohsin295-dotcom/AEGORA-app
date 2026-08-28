package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.model.CyberSkill
import com.example.model.SkillDecayForecast
import com.example.model.SkillDomain
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.EvidenceBadge
import com.example.ui.components.LivingSkillConstellationCanvas
import com.example.ui.components.SkillProgressBar
import com.example.ui.theme.*

enum class SkillGraphViewTab {
  LIVING_CONSTELLATION, LEARNING_GENOME, SKILL_NODES, DECAY_FORECAST
}

@Composable
fun SkillGraphScreen(
  onNavigateToPractice: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val domains = AegoraRepository.skillDomains
  val genome by AegoraRepository.learningGenome.collectAsState()
  val decayForecasts by AegoraRepository.skillDecayForecasts.collectAsState()
  val constellationNodes by AegoraRepository.constellationNodes.collectAsState()
  var selectedTab by remember { mutableStateOf(SkillGraphViewTab.LIVING_CONSTELLATION) }
  var selectedDomainId by remember { mutableStateOf(domains.first().id) }
  val activeDomain = domains.find { it.id == selectedDomainId } ?: domains.first()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header & System Spine
    item {
      CyberCard(
        borderColor = CyberCyan.copy(alpha = 0.5f),
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "LIVING INTELLIGENCE LAYER",
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Living Skill Constellation & Genome",
              style = MaterialTheme.typography.headlineLarge,
              color = TextPrimaryDark
            )
          }
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(CyberCyan.copy(alpha = 0.15f))
              .border(1.dp, CyberCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.AllInclusive, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Aegora's generative 3D skill constellation is computed live from your real telemetry. Nodes visibly dim as skills decay and brighten with spaced practice.",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondaryDark
        )
      }
    }

    // 2. View Switcher Tabs
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        SkillGraphViewTab.entries.forEach { tab ->
          val isSelected = tab == selectedTab
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(8.dp))
              .clickable { selectedTab = tab }
              .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(8.dp)),
            color = if (isSelected) CyberSurfaceElevated else CyberSurface
          ) {
            Text(
              text = when (tab) {
                SkillGraphViewTab.LIVING_CONSTELLATION -> "🌌 3D Space"
                SkillGraphViewTab.LEARNING_GENOME -> "🧬 Genome"
                SkillGraphViewTab.SKILL_NODES -> "🌐 Tree"
                SkillGraphViewTab.DECAY_FORECAST -> "📉 Decay"
              },
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
              color = if (isSelected) CyberCyan else TextSecondaryDark,
              modifier = Modifier
                .padding(vertical = 8.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
            )
          }
        }
      }
    }

    // TAB 0: LIVING 3D CONSTELLATION
    if (selectedTab == SkillGraphViewTab.LIVING_CONSTELLATION) {
      item {
        LivingSkillConstellationCanvas(
          nodes = constellationNodes,
          onLaunchDiagnostic = { skillId -> onNavigateToPractice(skillId) }
        )
      }
    }

    // TAB 1: CYBER LEARNING GENOME
    if (selectedTab == SkillGraphViewTab.LEARNING_GENOME) {
      item {
        CyberCard(
          borderColor = CyberViolet.copy(alpha = 0.5f),
          backgroundColor = CyberSurface
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "COGNITIVE CAPABILITY GENOME",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberViolet
              )
              Text(
                text = "Metacognitive Learning Profile",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberViolet.copy(alpha = 0.2f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberViolet)
            ) {
              Text(
                text = "Velocity ${genome.learningVelocity}%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberViolet,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // 7 Dimensions
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SkillProgressBar(
              progress = genome.knowledgeScore / 100f,
              label = "Theoretical Knowledge",
              valueText = "${genome.knowledgeScore}%",
              barColor = CyberCyan,
              height = 6.dp
            )
            SkillProgressBar(
              progress = genome.practicalScore / 100f,
              label = "Practical Tool Execution",
              valueText = "${genome.practicalScore}%",
              barColor = CyberBlue,
              height = 6.dp
            )
            SkillProgressBar(
              progress = genome.reasoningScore / 100f,
              label = "Analytical Reasoning",
              valueText = "${genome.reasoningScore}%",
              barColor = CyberViolet,
              height = 6.dp
            )
            SkillProgressBar(
              progress = genome.investigationScore / 100f,
              label = "Investigation Correlation (Bottleneck)",
              valueText = "${genome.investigationScore}%",
              barColor = CyberAmber,
              height = 6.dp
            )
            SkillProgressBar(
              progress = genome.communicationScore / 100f,
              label = "Audience-Aware Communication",
              valueText = "${genome.communicationScore}%",
              barColor = CyberEmerald,
              height = 6.dp
            )
            SkillProgressBar(
              progress = genome.retentionScore / 100f,
              label = "Spaced Repetition Retention",
              valueText = "${genome.retentionScore}%",
              barColor = CyberMagenta,
              height = 6.dp
            )
            SkillProgressBar(
              progress = genome.decisionMakingScore / 100f,
              label = "Decision Making Under Uncertainty",
              valueText = "${genome.decisionMakingScore}%",
              barColor = CyberCyan,
              height = 6.dp
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Current Bottleneck Alert
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = CyberAmber.copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber.copy(alpha = 0.5f))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "CURRENT LIMITING BOTTLENECK",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberAmber
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = genome.currentBottleneck,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
              Text(
                text = "Domain: ${genome.bottleneckDomain}",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Recommended Intervention: ${genome.recommendedIntervention}",
                style = MaterialTheme.typography.bodySmall,
                color = CyberCyan
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = { onNavigateToPractice(genome.recommendedActionTarget) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberAmber),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Launch Targeted Remediation Lab", color = CyberBackground, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Prerequisite Dependency Failure Detector
      item {
        CyberCard(
          borderColor = CyberAmber.copy(alpha = 0.4f),
          backgroundColor = CyberSurface
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AccountTree, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SKILL DEPENDENCY FAILURE DETECTOR",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberAmber
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Your Threat Hunting progression (72%) is currently constrained by 2 prerequisite foundation gaps: Linux CLI File Analysis (61%) and TCP/IP Packet Filtering (58%).",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
              Text(
                text = "🔗 Linux CLI Gaps",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
              Text(
                text = "🔗 PCAP Routing Gaps",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // TAB 2: SKILL NODES (TREE)
    if (selectedTab == SkillGraphViewTab.SKILL_NODES) {
      // Domain Selector Pills
      item {
        CyberSectionHeader(
          title = "Domains",
          subtitle = "Select domain to explore verified capability nodes"
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          domains.forEach { domain ->
            val isSelected = domain.id == selectedDomainId
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedDomainId = domain.id }
                .border(
                  1.dp,
                  if (isSelected) CyberCyan else CyberBorder,
                  RoundedCornerShape(10.dp)
                ),
              color = if (isSelected) CyberSurfaceElevated else CyberSurface
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "${domain.masteryPercent}%",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (isSelected) CyberCyan else TextPrimaryDark
                )
                Text(
                  text = domain.name.split(" ").first(),
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = TextSecondaryDark,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      // Domain Title
      item {
        CyberSectionHeader(
          title = activeDomain.name,
          subtitle = "${activeDomain.skills.size} verified skill nodes | Evidence Confidence: HIGH"
        )
      }

      // Skill Nodes
      items(activeDomain.skills) { skill ->
        SkillNodeCard(
          skill = skill,
          onPracticeClick = { onNavigateToPractice(skill.id) }
        )
      }
    }

    // TAB 3: SKILL DECAY RADAR & FORGETTING FORECAST
    if (selectedTab == SkillGraphViewTab.DECAY_FORECAST) {
      item {
        CyberCard(
          borderColor = CyberMagenta.copy(alpha = 0.5f),
          backgroundColor = CyberSurface
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = CyberMagenta, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "FORGETTING CURVE FORECAST ENGINE",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberMagenta
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Ebbinghaus Predictive Decay Projections",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Skills naturally decay without spaced retrieval. Aegora models your exact forgetting curves and projects your retention 7, 30, and 60 days out.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )
        }
      }

      items(decayForecasts) { forecast ->
        DecayForecastCard(
          forecast = forecast,
          onDiagnosticClick = { onNavigateToPractice(forecast.skillId) }
        )
      }
    }
  }
}

@Composable
fun DecayForecastCard(
  forecast: SkillDecayForecast,
  onDiagnosticClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val riskColor = when (forecast.riskLevel) {
    "LOW" -> CyberEmerald
    "MEDIUM" -> CyberAmber
    "HIGH" -> CyberRed
    else -> CyberRed
  }

  CyberCard(
    modifier = modifier,
    borderColor = riskColor.copy(alpha = 0.4f)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = forecast.skillName,
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark
        )
        Text(
          text = "${forecast.domain} • Last practiced ${forecast.lastPracticedDaysAgo} days ago",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = riskColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, riskColor)
      ) {
        Text(
          text = "${forecast.riskLevel} RISK",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = riskColor,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Projected Decay Bar Matrix
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      DecayPillar(label = "Now", score = forecast.currentHealth, color = CyberCyan, modifier = Modifier.weight(1f))
      DecayPillar(label = "In 7 Days", score = forecast.projected7Days, color = CyberBlue, modifier = Modifier.weight(1f))
      DecayPillar(label = "In 30 Days", score = forecast.projected30Days, color = CyberAmber, modifier = Modifier.weight(1f))
      DecayPillar(label = "In 60 Days", score = forecast.projected60Days, color = CyberRed, modifier = Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Recommended Diagnostic
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Recommended Refresher:",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
        Text(
          text = forecast.recommendedDiagnosticTitle,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
          color = CyberCyan
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Button(
        onClick = onDiagnosticClick,
        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("${forecast.diagnosticEstimatedMins}m Drill", color = CyberBackground, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
private fun DecayPillar(
  label: String,
  score: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = CyberSurfaceElevated,
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "$score%",
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = color
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = TextSecondaryDark
      )
    }
  }
}

@Composable
fun SkillNodeCard(
  skill: CyberSkill,
  onPracticeClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  CyberCard(
    modifier = modifier,
    borderColor = if (skill.overallMastery >= 75) CyberEmerald.copy(alpha = 0.5f) else CyberBorder
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = skill.name,
          style = MaterialTheme.typography.titleLarge,
          color = TextPrimaryDark
        )
        Text(
          text = "${skill.domain} Domain",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }

      // Mastery Badge
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (skill.overallMastery >= 75) CyberEmerald else CyberCyan
        )
      ) {
        Text(
          text = "${skill.overallMastery}% MASTERY",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = if (skill.overallMastery >= 75) CyberEmerald else CyberCyan,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Multi-dimensional Skill Breakdown
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      SkillProgressBar(
        progress = skill.knowledgeScore / 100f,
        label = "Knowledge & Concepts",
        valueText = "${skill.knowledgeScore}%",
        barColor = CyberCyan,
        height = 6.dp
      )
      SkillProgressBar(
        progress = skill.practicalScore / 100f,
        label = "Practical Tool Execution",
        valueText = "${skill.practicalScore}%",
        barColor = CyberBlue,
        height = 6.dp
      )
      SkillProgressBar(
        progress = skill.investigationScore / 100f,
        label = "Incident Investigation & Triage",
        valueText = "${skill.investigationScore}%",
        barColor = CyberViolet,
        height = 6.dp
      )
    }

    // Recurring Mistakes if any
    if (skill.recurringMistakes.isNotEmpty()) {
      Spacer(modifier = Modifier.height(12.dp))
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberAmber.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber.copy(alpha = 0.4f))
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Recurring Mistake: ${skill.recurringMistakes.first()}",
            style = MaterialTheme.typography.labelSmall,
            color = CyberAmber
          )
        }
      }
    }

    // Verified Evidence List
    if (skill.verifiedEvidenceList.isNotEmpty()) {
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "VERIFIED EVIDENCE TRAIL (${skill.verifiedEvidenceList.size})",
        style = MaterialTheme.typography.labelSmall,
        color = CyberEmerald
      )
      Spacer(modifier = Modifier.height(6.dp))
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        skill.verifiedEvidenceList.forEach { evidence ->
          EvidenceBadge(
            title = evidence.title,
            type = evidence.evidenceType,
            date = evidence.completedDate,
            hash = evidence.artifactHash
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Recommended Action Button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = skill.recommendedNextAction,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondaryDark,
        modifier = Modifier.weight(1f)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Button(
        onClick = onPracticeClick,
        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Practice", color = CyberBackground, fontWeight = FontWeight.Bold)
      }
    }
  }
}
