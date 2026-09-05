package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intelligence.CyberRealityIntelligenceV13Engine
import com.example.model.*
import com.example.ui.theme.*

enum class V13NavigationHubTab(val label: String, val icon: ImageVector) {
  INTELLIGENCE_LAYER("Reality & Intel", Icons.Filled.Public),
  IMPACT_RADAR("Impact & Radar", Icons.Filled.Radar),
  NEXT_BEST_ACTION("Next Action", Icons.Filled.Bolt),
  CAREER_ECONOMY("Career & Skills", Icons.AutoMirrored.Filled.TrendingUp),
  MASTERY_GATES("Mastery Gates", Icons.Filled.Verified),
  SEASONS_MEMORY("Seasons & Memory", Icons.Filled.Memory),
  PRINCIPLES_LAB("Principles & Ethics", Icons.Filled.Security),
  AI_MENTOR_DESK("AI Mentor & Second Opinion", Icons.Filled.Psychology),
  RELEASE_CONTROL("Release Center", Icons.Filled.CheckCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberRealityIntelligenceV13Screen(
  onNavigateBack: () -> Unit,
  onNavigateToLab: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  var activeTab by remember { mutableStateOf(V13NavigationHubTab.INTELLIGENCE_LAYER) }
  val liveStatus by CyberRealityIntelligenceV13Engine.liveIntelStatus.collectAsState()
  val nextAction by CyberRealityIntelligenceV13Engine.nextBestAction.collectAsState()
  val learnerEnergy by CyberRealityIntelligenceV13Engine.learnerEnergy.collectAsState()
  val availableTime by CyberRealityIntelligenceV13Engine.availableTimeMinutes.collectAsState()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "AEGORA v13.0",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                ),
                color = CyberCyan
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                color = CyberGreen.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen.copy(alpha = 0.5f))
              ) {
                Text(
                  text = "INTELLIGENCE LAYER",
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = CyberGreen,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
            Text(
              text = "Cyber Reality • Knowledge Graph • Learner Twin • Career Trajectory",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("btn_back_v13_reality")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Navigate Back",
              tint = TextPrimaryDark
            )
          }
        },
        actions = {
          Surface(
            color = CyberSurfaceElevated,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (liveStatus == IntelLiveStatusV13.LIVE_INGESTED) CyberGreen else CyberAmber)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = liveStatus.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = CyberSurface
        )
      )
    },
    containerColor = CyberBackground
  ) { paddingValues ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Hub Navigation Ribbon
      ScrollableTabRow(
        selectedTabIndex = activeTab.ordinal,
        containerColor = CyberSurface,
        contentColor = CyberCyan,
        edgePadding = 12.dp,
        divider = {}
      ) {
        V13NavigationHubTab.entries.forEach { tab ->
          Tab(
            selected = activeTab == tab,
            onClick = { activeTab = tab },
            text = {
              Text(
                text = tab.label,
                fontSize = 11.sp,
                fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.Monospace
              )
            },
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                modifier = Modifier.size(16.dp)
              )
            },
            modifier = Modifier.testTag("tab_v13_${tab.name.lowercase()}")
          )
        }
      }

      // Sub-Screen Content based on active tab
      Box(modifier = Modifier.fillMaxSize()) {
        when (activeTab) {
          V13NavigationHubTab.INTELLIGENCE_LAYER -> RealityAndIntelligenceTab()
          V13NavigationHubTab.IMPACT_RADAR -> ImpactAndRadarTab()
          V13NavigationHubTab.NEXT_BEST_ACTION -> NextBestActionTab(
            nextAction = nextAction,
            learnerEnergy = learnerEnergy,
            availableTime = availableTime,
            onEnergyChange = { CyberRealityIntelligenceV13Engine.setLearnerEnergy(it) },
            onTimeChange = { CyberRealityIntelligenceV13Engine.setAvailableTimeMinutes(it) }
          )
          V13NavigationHubTab.CAREER_ECONOMY -> CareerEconomyTab()
          V13NavigationHubTab.MASTERY_GATES -> MasteryGatesTab()
          V13NavigationHubTab.SEASONS_MEMORY -> SeasonsAndMemoryTab()
          V13NavigationHubTab.PRINCIPLES_LAB -> PrinciplesAndEthicsTab()
          V13NavigationHubTab.AI_MENTOR_DESK -> AiMentorAndSecondOpinionTab()
          V13NavigationHubTab.RELEASE_CONTROL -> ReleaseControlTab()
        }
      }
    }
  }
}

// ============================================================
// 1. REALITY & INTELLIGENCE TAB (ENTITIES, FEED & TIMELINE)
// ============================================================
@Composable
private fun RealityAndIntelligenceTab() {
  val nodes by CyberRealityIntelligenceV13Engine.realityNodes.collectAsState()
  val edges by CyberRealityIntelligenceV13Engine.realityEdges.collectAsState()
  val intelFeed by CyberRealityIntelligenceV13Engine.normalizedIntelFeed.collectAsState()
  val timeline by CyberRealityIntelligenceV13Engine.timelineEvents.collectAsState()
  val filter by CyberRealityIntelligenceV13Engine.selectedTimelineFilter.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Hub, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("CYBER REALITY GRAPH 2.0", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            "Tracking 22+ entity classes across CVEs, Threat Actors, Malware, Techniques, Controls, Research Papers, and Tooling with verified MITRE relationships.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.height(12.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatBadge(label = "Entities Tracked", value = "${nodes.size}", color = CyberCyan)
            StatBadge(label = "Graph Edges", value = "${edges.size}", color = CyberGreen)
            StatBadge(label = "Sources Active", value = "CISA / NIST / CERT", color = CyberGold)
          }
        }
      }
    }

    item {
      Text("LATEST NORMALIZED INTELLIGENCE FEED", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(intelFeed) { item ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = CyberCyan.copy(alpha = 0.15f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = item.domainCategory.uppercase(),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                fontFamily = FontFamily.Monospace
              )
            }
            Text(
              text = item.publicationDate,
              fontSize = 11.sp,
              color = TextSecondaryDark,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(item.headline, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text(item.educationalAbstract, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(10.dp))

          // Normalization & Hashes
          Surface(
            color = CyberBackground,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text("Source: ${item.sourceName} (${item.primarySourceType.label})", fontSize = 10.sp, color = CyberGreen)
              Text("Hash: ${item.sha256ContentHash}", fontSize = 9.sp, color = TextSecondaryDark, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
              Text("Normalized CVEs: ${item.normalizedCveList.joinToString()}", fontSize = 10.sp, color = CyberGold)
              Text("Techniques: ${item.normalizedTechniques.joinToString()}", fontSize = 10.sp, color = CyberCyan)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text("CYBER TIMELINE & HISTORICAL MILESTONES", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(timeline) { event ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
          Surface(
            color = CyberSurfaceElevated,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.padding(end = 10.dp)
          ) {
            Text(
              text = event.timeframe.label,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = CyberCyan
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(event.title, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(event.summary, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              event.mitreTactics.forEach { tactic ->
                Surface(
                  color = CyberBackground,
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(tactic, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 9.sp, color = CyberGreen)
                }
              }
            }
          }
        }
      }
    }
  }
}

// ============================================================
// 2. PERSONAL IMPACT & THREAT RADAR TAB
// ============================================================
@Composable
private fun ImpactAndRadarTab() {
  val assessments by CyberRealityIntelligenceV13Engine.personalImpactAssessments.collectAsState()
  val radarItems by CyberRealityIntelligenceV13Engine.personalThreatRadar.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Radar, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("PERSONAL THREAT RADAR", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Ranks real cyber developments by educational relevance to your Cyber Twin profile and target career path.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    item {
      Text("TOP RELEVANCE RANKINGS", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(radarItems) { item ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(item.title, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Surface(
              color = CyberGreen.copy(alpha = 0.15f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "Rank #${item.aggregateEducationalRank}",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyberGreen
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScorePill(label = "Career Match", score = item.careerRelevanceScore)
            ScorePill(label = "Skill Relevance", score = item.skillRelevanceScore)
            ScorePill(label = "Learning Value", score = item.learningValueScore)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text("WHY THIS MATTERS TO YOU (IMPACT ASSESSMENTS)", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(assessments) { assess ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(assess.intelTitle, fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 13.sp)
            Surface(
              color = Color(assess.relevanceLevel.badgeColor).copy(alpha = 0.15f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = assess.relevanceLevel.label,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(assess.relevanceLevel.badgeColor)
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(assess.whyItMattersToYou, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Target Role: ${assess.matchingCareerPath}", fontSize = 11.sp, color = TextSecondaryDark)
          Text("Identified Gap: ${assess.currentCapabilityGap}", fontSize = 11.sp, color = CyberGold)
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            color = CyberBackground,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(assess.recommendedActionTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberGreen)
            }
          }
        }
      }
    }
  }
}

// ============================================================
// 3. NEXT BEST ACTION 3.0 TAB (TIME & ENERGY AWARE)
// ============================================================
@Composable
private fun NextBestActionTab(
  nextAction: NextBestAction30V13,
  learnerEnergy: LearnerEnergyStateV13,
  availableTime: Int,
  onEnergyChange: (LearnerEnergyStateV13) -> Unit,
  onTimeChange: (Int) -> Unit
) {
  val missions by CyberRealityIntelligenceV13Engine.eventMissions.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("CONTEXT & AVAILABILITY CONTROLS", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
          Spacer(modifier = Modifier.height(10.dp))
          Text("Select Your Current Available Time:", fontSize = 11.sp, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 15, 30, 60).forEach { mins ->
              FilterChip(
                selected = availableTime == mins,
                onClick = { onTimeChange(mins) },
                label = { Text("${mins}m") }
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text("Select Your Focus / Energy Level:", fontSize = 11.sp, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(6.dp))
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            LearnerEnergyStateV13.entries.forEach { energy ->
              Surface(
                color = if (learnerEnergy == energy) CyberCyan.copy(alpha = 0.15f) else CyberBackground,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (learnerEnergy == energy) CyberCyan else CyberBorder
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onEnergyChange(energy) }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = learnerEnergy == energy,
                    onClick = { onEnergyChange(energy) },
                    colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(energy.label, fontSize = 12.sp, color = TextPrimaryDark)
                }
              }
            }
          }
        }
      }
    }

    item {
      Text("CALCULATED NEXT BEST ACTION 3.0", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Bolt, contentDescription = null, tint = CyberGold, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(nextAction.actionTitle, fontWeight = FontWeight.Black, color = TextPrimaryDark, fontSize = 15.sp)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text("WHY RECOMMENDED:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
          Text(nextAction.whyRecommended, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(8.dp))
          Text("EXPECTED BENEFIT:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberGreen)
          Text(nextAction.expectedCapabilityBenefit, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(10.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Est: ${nextAction.estimatedMinutes} mins", fontSize = 11.sp, color = TextSecondaryDark)
            Text("Difficulty: ${nextAction.difficultyTier}", fontSize = 11.sp, color = CyberGold)
            Text("Evidence: ${nextAction.generatedEvidenceType}", fontSize = 11.sp, color = CyberGreen)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text("EVENT-TO-MISSION GENERATOR (SAFE ABSTRACTIONS)", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(missions) { mission ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(mission.durationTier, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
            Text("${mission.durationMinutes}m", fontSize = 11.sp, color = TextSecondaryDark)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(mission.missionObjective, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Surface(
            color = CyberBackground,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = mission.simulatedTelemetrySnippet,
              modifier = Modifier.padding(6.dp),
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace,
              color = CyberGreen
            )
          }
        }
      }
    }
  }
}

// ============================================================
// 4. CAREER & SKILL ECONOMY TAB
// ============================================================
@Composable
private fun CareerEconomyTab() {
  val demands by CyberRealityIntelligenceV13Engine.marketSkillDemands.collectAsState()
  val leverageSkills by CyberRealityIntelligenceV13Engine.highLeverageSkills.collectAsState()
  val dependencies by CyberRealityIntelligenceV13Engine.skillDependencies.collectAsState()
  var selectedRole by remember { mutableStateOf("SOC Analyst (Tier 2)") }
  val whatIfResult = remember(selectedRole) { CyberRealityIntelligenceV13Engine.simulateCareerWhatIf(selectedRole) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("CAREER WHAT-IF SIMULATOR", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(8.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("SOC Analyst (Tier 2)", "Cloud Security Engineer", "AI Security / LLM Red Teamer", "DFIR Specialist")) { role ->
              FilterChip(
                selected = selectedRole == role,
                onClick = { selectedRole = role },
                label = { Text(role, fontSize = 11.sp) }
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text("Target: ${whatIfResult.careerRole}", fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 13.sp)
          Text("Current Match: ${whatIfResult.currentMatchPercent}% (Est. Effort: ${whatIfResult.estimatedEffortWeeks} weeks)", fontSize = 12.sp, color = CyberGreen)
          Spacer(modifier = Modifier.height(6.dp))
          Text("High-Leverage Focus: ${whatIfResult.highLeverageSkillsToAcquire.joinToString()}", fontSize = 11.sp, color = TextSecondaryDark)
          Text("Cert Pathways: ${whatIfResult.certificationPathway.joinToString()}", fontSize = 11.sp, color = CyberGold)
          Spacer(modifier = Modifier.height(4.dp))
          Text(whatIfResult.planningEstimateNotice, fontSize = 9.sp, color = TextSecondaryDark)
        }
      }
    }

    item {
      Text("HIGH-LEVERAGE COMPOUNDING SKILLS", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(leverageSkills) { item ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.skillName, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
            Text("${item.leverageMultiplier}x Multiplier", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberGreen)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("Unlocks: ${item.unlockedCareers.joinToString()}", fontSize = 11.sp, color = CyberCyan)
          Spacer(modifier = Modifier.height(4.dp))
          Text(item.coreReason, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text("SKILL MARKET DEMAND (VERIFIED PUBLIC DATA)", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(demands) { demand ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(demand.skillName, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
            Text(demand.trend.label, fontSize = 11.sp, color = CyberGreen)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text("${demand.verifiedJobMentionsCount} verified postings • ${demand.sampleSize}", fontSize = 10.sp, color = TextSecondaryDark)
        }
      }
    }
  }
}

// ============================================================
// 5. MASTERY GATES & EVIDENCE QUALITY 2.0 TAB
// ============================================================
@Composable
private fun MasteryGatesTab() {
  val gates by CyberRealityIntelligenceV13Engine.masteryGatesV13.collectAsState()
  val evidenceMetrics by CyberRealityIntelligenceV13Engine.evidenceQualityMetrics.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("MASTERY TRANSFER GATES (7-STAGE VERIFICATION)", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            "Course completion does not equal capability. Requires passing Understand, Recall, Apply, Investigate, Transfer, Explain, and Uncertainty resilience.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
      }
    }

    items(gates) { gate ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (gate.isDemonstratedCapabilityGranted) CyberGreen else CyberAmber)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(gate.skillName, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
            Text(
              if (gate.isDemonstratedCapabilityGranted) "DEMONSTRATED" else "IN PROGRESS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (gate.isDemonstratedCapabilityGranted) CyberGreen else CyberAmber
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            GateCheckItem(label = "Understand", passed = gate.understandsConcept)
            GateCheckItem(label = "Recall", passed = gate.recallAccuracy)
            GateCheckItem(label = "Apply", passed = gate.labApplicationVerified)
            GateCheckItem(label = "Investigate", passed = gate.rawInvestigationPassed)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            GateCheckItem(label = "Transfer", passed = gate.crossContextTransferred)
            GateCheckItem(label = "Explain", passed = gate.verbalExplanationClear)
            GateCheckItem(label = "Uncertainty", passed = gate.uncertaintyResiliencePassed)
            Text("Proof: Valid", fontSize = 9.sp, color = CyberCyan)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text("EVIDENCE QUALITY 2.0 RADAR", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(evidenceMetrics) { ev ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(ev.evidenceTitle, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(ev.compositeQualityRating, fontSize = 11.sp, color = CyberGreen, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScorePill(label = "Authenticity", score = ev.authenticityScore)
            ScorePill(label = "Independence", score = ev.independenceScore)
            ScorePill(label = "Transfer", score = ev.transferabilityScore)
          }
        }
      }
    }
  }
}

// ============================================================
// 6. SEASONS & ORGANIZATIONAL MEMORY TAB
// ============================================================
@Composable
private fun SeasonsAndMemoryTab() {
  val campaigns by CyberRealityIntelligenceV13Engine.seasonCampaigns.collectAsState()
  val enterprise by CyberRealityIntelligenceV13Engine.enterpriseState.collectAsState()
  val incidentMemory by CyberRealityIntelligenceV13Engine.incidentMemoryLogs.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("ORGANIZATIONAL MEMORY (${enterprise.enterpriseName})", fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(enterprise.infrastructureSummary, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(6.dp))
          Text(enterprise.studentDecisionsImpactSummary, fontSize = 11.sp, color = CyberGreen)
        }
      }
    }

    item {
      Text("CYBER SEASON CAMPAIGNS", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(campaigns) { campaign ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (campaign.isSeasonUnlocked) CyberCyan else CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(campaign.title, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
            Text(if (campaign.isSeasonUnlocked) "UNLOCKED" else "LOCKED", fontSize = 10.sp, color = if (campaign.isSeasonUnlocked) CyberGreen else TextSecondaryDark)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(campaign.theme, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(8.dp))
          campaign.episodes.forEach { ep ->
            Text("• Ep ${ep.episodeNumber}: ${ep.title} (${if (ep.isCompleted) "Completed" else "Available"})", fontSize = 11.sp, color = if (ep.isCompleted) CyberGreen else TextPrimaryDark)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text("PERSISTENT INCIDENT MEMORY LOGS", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(incidentMemory) { mem ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(mem.scenarioTitle, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text("What Worked: ${mem.successfulDefenses.joinToString()}", fontSize = 11.sp, color = CyberGreen)
          Text("Mistakes Analyzed: ${mem.errorsOrMistakesMade.joinToString()}", fontSize = 11.sp, color = CyberAmber)
          Text("Key Takeaway: ${mem.keyTakeawayLearned}", fontSize = 11.sp, color = CyberCyan)
        }
      }
    }
  }
}

// ============================================================
// 7. PRINCIPLES & ETHICS TAB
// ============================================================
@Composable
private fun PrinciplesAndEthicsTab() {
  val principles by CyberRealityIntelligenceV13Engine.principleMatrix.collectAsState()
  val ethicalCases by CyberRealityIntelligenceV13Engine.ethicalCases.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Text("CORE SECURITY INVARIANTS (TRANSFERABLE PRINCIPLES)", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(principles) { p ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(p.principleName, fontWeight = FontWeight.Bold, color = CyberCyan, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(p.coreInvariant, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
          Spacer(modifier = Modifier.height(8.dp))
          Text("• Linux: ${p.linuxApplication}", fontSize = 11.sp, color = TextSecondaryDark)
          Text("• Cloud IAM: ${p.cloudIamApplication}", fontSize = 11.sp, color = TextSecondaryDark)
          Text("• K8s / Containers: ${p.containerK8sApplication}", fontSize = 11.sp, color = TextSecondaryDark)
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text("ETHICAL DECISION LAB (PROFESSIONAL DILEMMAS)", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(ethicalCases) { eth ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(eth.title, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(4.dp))
          Text(eth.scenarioContext, style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Option A: ${eth.optionA}", fontSize = 11.sp, color = CyberCyan)
          Text("Option B: ${eth.optionB}", fontSize = 11.sp, color = CyberGreen)
          Spacer(modifier = Modifier.height(6.dp))
          Text(eth.ethicalNuanceExploration, fontSize = 10.sp, color = CyberGold)
        }
      }
    }
  }
}

// ============================================================
// 8. AI MENTOR STYLE & SECOND OPINION TAB
// ============================================================
@Composable
private fun AiMentorAndSecondOpinionTab() {
  val activeStyle by CyberRealityIntelligenceV13Engine.activeMentorStyle.collectAsState()
  val secondOpinions by CyberRealityIntelligenceV13Engine.secondOpinionCases.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("AI MENTOR PERSONALITY ADAPTATION", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(10.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AiMentorPersonaStyleV13.entries.forEach { style ->
              Surface(
                color = if (activeStyle == style) CyberCyan.copy(alpha = 0.15f) else CyberBackground,
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (activeStyle == style) CyberCyan else CyberBorder
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { CyberRealityIntelligenceV13Engine.setMentorStyle(style) }
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = activeStyle == style,
                    onClick = { CyberRealityIntelligenceV13Engine.setMentorStyle(style) },
                    colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(style.label, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 12.sp)
                    Text(style.toneDescription, fontSize = 10.sp, color = TextSecondaryDark)
                  }
                }
              }
            }
          }
        }
      }
    }

    item {
      Text("AI SECOND OPINION DESK (CRITICAL THINKING)", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    items(secondOpinions) { op ->
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text("Query: ${op.query}", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(8.dp))
          Text("AI Tutor View: ${op.aiTutorConclusion}", fontSize = 11.sp, color = CyberCyan)
          Spacer(modifier = Modifier.height(4.dp))
          Text("AI Research Analyst View: ${op.aiResearchAnalystConclusion}", fontSize = 11.sp, color = CyberGreen)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Takeaway: ${op.criticalThinkingTakeaway}", fontSize = 11.sp, color = CyberGold)
        }
      }
    }
  }
}

// ============================================================
// 9. RELEASE CONTROL & COMPLIANCE TAB
// ============================================================
@Composable
private fun ReleaseControlTab() {
  val releaseCenter by CyberRealityIntelligenceV13Engine.releaseCenter.collectAsState()
  val debrief by CyberRealityIntelligenceV13Engine.simulationDebriefReport.collectAsState()
  val growthIndex by CyberRealityIntelligenceV13Engine.growthIndex.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("RELEASE CONTROL CENTER v${releaseCenter.version}", fontWeight = FontWeight.Black, color = TextPrimaryDark, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(releaseCenter.releaseCodename, fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(10.dp))
          releaseCenter.gates.forEach { gate ->
            Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(gate.label, fontSize = 11.sp, color = TextPrimaryDark)
              Text("PASSED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberGreen)
            }
          }
        }
      }
    }

    item {
      Text("AEGORA PROFESSIONAL GROWTH INDEX", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScorePill("Capability", growthIndex.capabilityIndex)
            ScorePill("Evidence", growthIndex.evidenceQualityIndex)
            ScorePill("Transfer", growthIndex.transferabilityIndex)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            ScorePill("Consistency", growthIndex.consistencyIndex)
            ScorePill("Career Align", growthIndex.careerAlignmentIndex)
            ScorePill("Freshness", growthIndex.knowledgeFreshnessIndex)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(growthIndex.compositeSummary, style = MaterialTheme.typography.bodySmall, color = CyberGreen)
        }
      }
    }

    item {
      Text("LATEST PROFESSIONAL SIMULATION DEBRIEF REPORT", fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }

    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(debrief.scenarioTitle, fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(6.dp))
          Text("Decisions: ${debrief.containmentDecisionsSummary}", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          Spacer(modifier = Modifier.height(4.dp))
          Text("Demonstrated Strengths: ${debrief.demonstratedStrengths.joinToString()}", fontSize = 11.sp, color = CyberGreen)
          Text("Mistake Analysis: ${debrief.mistakeAnalysis}", fontSize = 11.sp, color = CyberAmber)
          Text("Actionable Remediation: ${debrief.actionableRemediationPlan}", fontSize = 11.sp, color = CyberCyan)
        }
      }
    }
  }
}

// ============================================================
// HELPER COMPOSABLES
// ============================================================
@Composable
private fun StatBadge(label: String, value: String, color: Color) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 14.sp)
    Text(label, fontSize = 10.sp, color = TextSecondaryDark)
  }
}

@Composable
private fun ScorePill(label: String, score: Int) {
  Surface(
    color = CyberBackground,
    shape = RoundedCornerShape(6.dp)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("$score/100", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberGreen)
      Text(label, fontSize = 9.sp, color = TextSecondaryDark)
    }
  }
}

@Composable
private fun GateCheckItem(label: String, passed: Boolean) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(6.dp)
        .clip(CircleShape)
        .background(if (passed) CyberGreen else CyberAmber)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(label, fontSize = 10.sp, color = if (passed) TextPrimaryDark else TextSecondaryDark)
  }
}
