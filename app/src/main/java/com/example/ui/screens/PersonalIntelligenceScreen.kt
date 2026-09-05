package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.intelligence.PersonalIntelligencePlatformEngine
import com.example.model.*

/**
 * AEGORA PERSONAL INTELLIGENCE & CAPABILITY OPERATING SYSTEM SCREEN
 * 
 * Unifies:
 * 1. Personal Intelligence Profile (4 Cognitive Clusters + Deep Explainability)
 * 2. Causal Capability Graph (Downstream impact & failure rate analysis)
 * 3. Failure Intelligence (Classifying mistakes as high-value evidence)
 * 4. Learning Memory & Retention (Decay without punishment + Reactivation)
 * 5. "PROVE IT" System (On-demand verification with cryptographic signatures)
 * 6. Career Intelligence (Target role gap mapping)
 * 7. Progress Narrative & Discovery Engine
 * 8. Trust Center & 2D Accessible Alternative
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalIntelligenceScreen(
  onNavigateBack: () -> Unit,
  onLaunchMission: (String) -> Unit = {},
  onOpenSkillPassport: () -> Unit = {}
) {
  var selectedTab by remember { mutableStateOf(PersonalIntelligenceTab.PROFILE) }
  var showTrustCenterDialog by remember { mutableStateOf(false) }
  var is2DModeEnabled by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Personal Intelligence Profile",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Text(
              text = "Living Capability Identity • Verifiable Evidence",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF00E5FF)
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("nav_back_intel_profile")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Navigate Back",
              tint = Color.White
            )
          }
        },
        actions = {
          IconButton(
            onClick = { is2DModeEnabled = !is2DModeEnabled },
            modifier = Modifier.testTag("toggle_2d_accessible")
          ) {
            Icon(
              imageVector = if (is2DModeEnabled) Icons.Default.Visibility else Icons.Default.ViewInAr,
              contentDescription = "Toggle 2D Accessible View",
              tint = if (is2DModeEnabled) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.7f)
            )
          }
          IconButton(
            onClick = { showTrustCenterDialog = true },
            modifier = Modifier.testTag("btn_trust_center")
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = "Trust & Privacy Center",
              tint = Color(0xFF10B981)
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color(0xFF0B101E)
        )
      )
    },
    containerColor = Color(0xFF060913)
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Top Navigation Tabs
      ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = Color(0xFF0B101E),
        contentColor = Color(0xFF00E5FF),
        edgePadding = 16.dp,
        divider = {}
      ) {
        PersonalIntelligenceTab.values().forEach { tab ->
          Tab(
            selected = selectedTab == tab,
            onClick = { selectedTab = tab },
            modifier = Modifier.testTag("tab_${tab.name.lowercase()}"),
            text = {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  imageVector = tab.icon,
                  contentDescription = tab.label,
                  modifier = Modifier.size(16.dp),
                  tint = if (selectedTab == tab) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.6f)
                )
                Text(
                  text = tab.label,
                  fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                  color = if (selectedTab == tab) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.6f)
                )
              }
            }
          )
        }
      }

      // Main Tab Content & UI State Handling
      val uiState by PersonalIntelligencePlatformEngine.profileUiState.collectAsState()

      when (val state = uiState) {
        is PersonalIntelligenceUiState.Loading -> {
          IntelligenceLoadingState()
        }
        is PersonalIntelligenceUiState.Empty -> {
          IntelligenceEmptyState(
            state = state,
            onLaunchMission = onLaunchMission,
            onLoadDemoProfile = {
              PersonalIntelligencePlatformEngine.resetToDefaultDemonstrationState()
            }
          )
        }
        is PersonalIntelligenceUiState.Error -> {
          IntelligenceErrorState(
            state = state,
            onRetry = {
              PersonalIntelligencePlatformEngine.resetToDefaultDemonstrationState()
            }
          )
        }
        is PersonalIntelligenceUiState.Ready -> {
          Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
              PersonalIntelligenceTab.PROFILE -> {
                IntelligenceProfileTabContent(
                  is2DMode = is2DModeEnabled,
                  onLaunchMission = onLaunchMission,
                  onOpenPassport = onOpenSkillPassport,
                  partialEvidenceWarning = state.partialEvidenceWarning
                )
              }
              PersonalIntelligenceTab.CAPABILITY_GRAPH -> {
                CapabilityGraphTabContent(onLaunchMission = onLaunchMission)
              }
              PersonalIntelligenceTab.PROVE_IT -> {
                ProveItTabContent(onOpenPassport = onOpenSkillPassport)
              }
              PersonalIntelligenceTab.FAILURE_INTEL -> {
                FailureIntelligenceTabContent(onLaunchMission = onLaunchMission)
              }
              PersonalIntelligenceTab.MEMORY_RETENTION -> {
                LearningMemoryTabContent(onLaunchMission = onLaunchMission)
              }
              PersonalIntelligenceTab.CAREER_GAP -> {
                CareerIntelligenceTabContent(onLaunchMission = onLaunchMission)
              }
            }
          }
        }
      }
    }

    if (showTrustCenterDialog) {
      TrustCenterDialog(onDismiss = { showTrustCenterDialog = false })
    }
  }
}

enum class PersonalIntelligenceTab(val label: String, val icon: ImageVector) {
  PROFILE("Intelligence Profile", Icons.Default.Psychology),
  CAPABILITY_GRAPH("Capability Graph", Icons.Default.AccountTree),
  PROVE_IT("Prove It", Icons.Default.Verified),
  FAILURE_INTEL("Failure Intel", Icons.Default.ReportProblem),
  MEMORY_RETENTION("Learning Memory", Icons.Default.History),
  CAREER_GAP("Career Mapping", Icons.Default.Work)
}

// ============================================================
// 1. INTELLIGENCE PROFILE TAB CONTENT
// ============================================================
@Composable
private fun IntelligenceProfileTabContent(
  is2DMode: Boolean,
  onLaunchMission: (String) -> Unit,
  onOpenPassport: () -> Unit,
  partialEvidenceWarning: String? = null
) {
  val clusterSummaries by PersonalIntelligencePlatformEngine.clusterSummaries.collectAsState()
  val progressMilestones by PersonalIntelligencePlatformEngine.progressMilestones.collectAsState()
  val discoveryInsights by PersonalIntelligencePlatformEngine.discoveryInsights.collectAsState()
  var expandedCluster by remember { mutableStateOf<CognitiveClusterType?>(CognitiveClusterType.GENERALIZATION_STRESS) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    if (partialEvidenceWarning != null) {
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E170A)),
          border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("notice_partial_evidence")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              imageVector = Icons.Default.WarningAmber,
              contentDescription = null,
              tint = Color(0xFFF59E0B),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "EMERGING CAPABILITY BASELINE // PARTIAL TELEMETRY",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = Color(0xFFF59E0B)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = partialEvidenceWarning,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                BadgePill("Not Demonstrated (<30)", Color(0xFF64748B))
                BadgePill("Weak (30-69)", Color(0xFFF59E0B))
                BadgePill("Strong (70+)", Color(0xFF10B981))
              }
            }
          }
        }
      }
    }

    // Top Headline Banner
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
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
                text = "CYBERSECURITY CAPABILITY IDENTITY",
                style = MaterialTheme.typography.labelSmall.copy(
                  letterSpacing = 1.5.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = Color(0xFF00E5FF)
              )
              Text(
                text = "Multidimensional Cognitive Profile",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
            }
            Surface(
              color = Color(0xFF10B981).copy(alpha = 0.15f),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, Color(0xFF10B981))
            ) {
              Text(
                text = "L4 SKILLED",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF10B981)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4 Cognitive Cluster Metric Bars
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            clusterSummaries.forEach { summary ->
              val clusterColor = when (summary.cluster) {
                CognitiveClusterType.FOUNDATION -> Color(0xFF3B82F6)
                CognitiveClusterType.ACTIVE_DEFENSE -> Color(0xFF10B981)
                CognitiveClusterType.GENERALIZATION_STRESS -> Color(0xFFF59E0B)
                CognitiveClusterType.METACOGNITIVE_STRATEGIC -> Color(0xFF8B5CF6)
              }
              Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                  width = if (expandedCluster == summary.cluster) 1.5.dp else 1.dp,
                  color = if (expandedCluster == summary.cluster) clusterColor else Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable {
                    expandedCluster = if (expandedCluster == summary.cluster) null else summary.cluster
                  }
                  .testTag("cluster_chip_${summary.cluster.name.lowercase()}")
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "${summary.score}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = clusterColor
                  )
                  Text(
                    text = summary.cluster.displayName.take(8),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Scores represent verified execution under raw telemetry, not course completion percentage.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f)
          )
        }
      }
    }

    // Discovery Insights: Proactive Strengths & Dependencies
    item {
      Text(
        text = "DISCOVERIES & INTEL FRONTIERS",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.2.sp,
          fontWeight = FontWeight.Bold
        ),
        color = Color(0xFF94A3B8)
      )
    }

    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(discoveryInsights) { insight ->
          DiscoveryInsightCard(insight = insight, onAction = { onLaunchMission(insight.destinationTag) })
        }
      }
    }

    // Deep Cluster Explainability Section (Explaining What the Numbers Mean)
    item {
      Text(
        text = "DETAILED CLUSTER EXPLAINABILITY",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.2.sp,
          fontWeight = FontWeight.Bold
        ),
        color = Color(0xFF94A3B8)
      )
    }

    items(clusterSummaries) { summary ->
      ClusterExplainabilityCard(
        summary = summary,
        isExpanded = expandedCluster == summary.cluster,
        onToggle = {
          expandedCluster = if (expandedCluster == summary.cluster) null else summary.cluster
        },
        onLaunchMission = onLaunchMission
      )
    }

    // Progress Narrative: Personal Capability Timeline
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "PROGRESS NARRATIVE TIMELINE",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.2.sp,
          fontWeight = FontWeight.Bold
        ),
        color = Color(0xFF94A3B8)
      )
    }

    items(progressMilestones) { milestone ->
      ProgressMilestoneCard(milestone = milestone)
    }

    // Skill Passport Link Action
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Verifiable Skill Passport",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Text(
              text = "Export cryptographically signed evidence ledger for universities and employers.",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.7f)
            )
          }
          Button(
            onClick = onOpenPassport,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("btn_open_passport_from_profile")
          ) {
            Text("View Passport", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// ============================================================
// 2. CAUSAL CAPABILITY GRAPH TAB CONTENT
// ============================================================
@Composable
private fun CapabilityGraphTabContent(onLaunchMission: (String) -> Unit) {
  val nodes by PersonalIntelligencePlatformEngine.capabilityNodes.collectAsState()
  val selectedId by PersonalIntelligencePlatformEngine.selectedNodeId.collectAsState()
  val selectedNode = nodes.find { it.id == selectedId } ?: nodes.firstOrNull()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AccountTree,
              contentDescription = null,
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CAUSAL CAPABILITY DEPENDENCY GRAPH",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF00E5FF)
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Cyber capabilities are interconnected. A weak foundational capability causes measurable downstream failure rates in active defense and incident response.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
          )
        }
      }
    }

    // Node Exploration Strip
    item {
      Text(
        text = "SELECT CAPABILITY NODE TO INSPECT CAUSAL IMPACT",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = Color(0xFF94A3B8)
      )
    }

    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(nodes) { node ->
          val isSelected = node.id == selectedId
          val statusColor = when (node.status) {
            CapabilityNodeStatus.VERIFIED -> Color(0xFF10B981)
            CapabilityNodeStatus.EMERGING -> Color(0xFF3B82F6)
            CapabilityNodeStatus.AT_RISK -> Color(0xFFF59E0B)
            CapabilityNodeStatus.BOTTLENECK -> Color(0xFFEF4444)
            CapabilityNodeStatus.BLOCKED -> Color(0xFF6B7280)
          }

          Surface(
            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) Color(0xFF00E5FF) else statusColor.copy(alpha = 0.5f)
            ),
            modifier = Modifier
              .clickable { PersonalIntelligencePlatformEngine.selectCapabilityNode(node.id) }
              .testTag("node_${node.id}")
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.Start
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = node.status.name,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                  color = statusColor
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = node.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
              Text(
                text = "Score: ${node.demonstratedScore}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF00E5FF)
              )
            }
          }
        }
      }
    }

    // Detailed Selected Node Causal Card
    selectedNode?.let { node ->
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
          border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
          shape = RoundedCornerShape(16.dp),
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
                  text = node.cluster.displayName.uppercase(),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF00E5FF)
                )
                Text(
                  text = node.name,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
              Surface(
                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFEF4444))
              ) {
                Text(
                  text = "+${node.failureRateDownstream}% DOWNSTREAM FAILURE RATE",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFFEF4444)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(14.dp))

            // Causal Relationship Flow
            Text(
              text = "CAUSAL DEPENDENCY FLOW",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
              if (node.upstreamDependencyIds.isEmpty()) {
                Surface(
                  color = Color(0xFF1E293B),
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.padding(end = 8.dp)
                ) {
                  Text(
                    text = "Root Prerequisite",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                  )
                }
              } else {
                node.upstreamDependencyIds.forEach { upId ->
                  Surface(
                    color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f)),
                    modifier = Modifier.padding(end = 6.dp)
                  ) {
                    Text(
                      text = upId.replace("_", " ").replaceFirstChar { it.uppercase() },
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      style = MaterialTheme.typography.labelSmall,
                      color = Color(0xFF60A5FA)
                    )
                  }
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                      .size(14.dp)
                      .padding(end = 6.dp)
                  )
                }
              }

              Surface(
                color = Color(0xFF00E5FF).copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFF00E5FF)),
                modifier = Modifier.padding(end = 8.dp)
              ) {
                Text(
                  text = node.name,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF00E5FF)
                )
              }

              if (node.downstreamImpactIds.isNotEmpty()) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  tint = Color.White.copy(alpha = 0.5f),
                  modifier = Modifier
                    .size(14.dp)
                    .padding(end = 6.dp)
                )
                node.downstreamImpactIds.forEach { downId ->
                  Surface(
                    color = Color(0xFFEF4444).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                    modifier = Modifier.padding(end = 6.dp)
                  ) {
                    Text(
                      text = downId.replace("_", " ").replaceFirstChar { it.uppercase() },
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      style = MaterialTheme.typography.labelSmall,
                      color = Color(0xFFF87171)
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Downstream Impact Analysis:",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = node.downstreamImpactSummary,
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Telemetry Requirement: ${node.telemetryRequirement}",
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              color = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = { onLaunchMission(node.recommendedMissionId) },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("btn_launch_causal_remediation")
            ) {
              Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.Black
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Unblock Downstream: Launch ${node.missionEstimatedMinutes}m Drill",
                color = Color.Black,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

// ============================================================
// 3. "PROVE IT" ON-DEMAND VERIFICATION TAB CONTENT
// ============================================================
@Composable
private fun ProveItTabContent(onOpenPassport: () -> Unit) {
  val challenges by PersonalIntelligencePlatformEngine.proveItChallenges.collectAsState()
  val verifiedProofs by PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.collectAsState()
  var activeChallengeIndex by remember { mutableStateOf(0) }
  var selectedOptionId by remember { mutableStateOf<String?>("opt_c2_isolate") }
  var verificationResult by remember { mutableStateOf<Boolean?>(null) }
  var resultFeedback by remember { mutableStateOf("") }

  val currentChallenge = challenges.getOrNull(activeChallengeIndex) ?: return

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "PROVE IT: ON-DEMAND VERIFICATION",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF10B981)
              )
            }
            Surface(
              color = Color(0xFF10B981).copy(alpha = 0.2f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "${verifiedProofs.size} VERIFIED CLAIMS",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF10B981)
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Instead of claiming course completion, demonstrate operational capability under real telemetry to earn verifiable cryptographic proof.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
          )
        }
      }
    }

    // Active Challenge Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
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
                text = "CHALLENGE: ${currentChallenge.difficulty.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF00E5FF)
              )
              Text(
                text = currentChallenge.capabilityName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
            }
            Surface(
              color = Color(0xFF1E293B),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = currentChallenge.telemetryEnvironment,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = Color(0xFF38BDF8)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Raw Telemetry Console Block
          Text(
            text = "LIVE RAW TELEMETRY EVENT STREAM:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF94A3B8)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            color = Color(0xFF020617),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = currentChallenge.rawTelemetryLog,
              modifier = Modifier.padding(12.dp),
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp
              ),
              color = Color(0xFF4ADE80)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = currentChallenge.challengePrompt,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.White
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Triage Options
          currentChallenge.triageOptions.forEach { option ->
            val isSelected = selectedOptionId == option.id
            Surface(
              color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0B101E),
              shape = RoundedCornerShape(10.dp),
              border = BorderStroke(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.1f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable {
                  selectedOptionId = option.id
                  verificationResult = null
                }
                .testTag("prove_it_opt_${option.id}")
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = {
                    selectedOptionId = option.id
                    verificationResult = null
                  },
                  colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFF00E5FF),
                    unselectedColor = Color.White.copy(alpha = 0.4f)
                  )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = option.actionTitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Verification Action Button
          Button(
            onClick = {
              selectedOptionId?.let { optId ->
                val success = PersonalIntelligencePlatformEngine.completeProveItChallenge(
                  currentChallenge.id,
                  optId
                )
                verificationResult = success
                resultFeedback = if (success) {
                  "VERIFIED: Demonstration validated against ground truth. Hash ${currentChallenge.verifiedCryptographicProof} added to Skill Passport."
                } else {
                  "DEMONSTRATION REJECTED: Triage decision diverted from root-cause evidence. Recorded in Failure Intelligence for constructive remediation."
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_submit_prove_it")
          ) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Submit Evidence & Verify Capability",
              color = Color.Black,
              fontWeight = FontWeight.Bold
            )
          }

          // Result Feedback Box
          verificationResult?.let { isSuccess ->
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              color = if (isSuccess) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = if (isSuccess) "CAPABILITY DEMONSTRATED: VERIFIED" else "FAILURE MODE DETECTED",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = resultFeedback,
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.9f)
                )
              }
            }
          }
        }
      }
    }

    // Verified Evidence Ledger
    item {
      Text(
        text = "VERIFIED CAPABILITY LEDGER (PORTABLE PROOFS)",
        style = MaterialTheme.typography.labelSmall.copy(
          letterSpacing = 1.2.sp,
          fontWeight = FontWeight.Bold
        ),
        color = Color(0xFF94A3B8)
      )
    }

    items(verifiedProofs) { proof ->
      EvidenceProofLedgerItem(proof = proof)
    }
  }
}

// ============================================================
// 4. FAILURE INTELLIGENCE TAB CONTENT
// ============================================================
@Composable
private fun FailureIntelligenceTabContent(onLaunchMission: (String) -> Unit) {
  val mistakes by PersonalIntelligencePlatformEngine.mistakeRecords.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ReportProblem,
              contentDescription = null,
              tint = Color(0xFFEF4444),
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "FAILURE INTELLIGENCE SYSTEM",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFFEF4444)
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Failure is not 'wrong answer'. AEGORA diagnoses cognitive failure modes (Knowledge Gap, Reasoning Error, Premature Conclusion, Transfer Miss) and turns mistakes into actionable capability growth.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
          )
        }
      }
    }

    item {
      Text(
        text = "DIAGNOSED COGNITIVE MISTAKE RECORDS",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = Color(0xFF94A3B8)
      )
    }

    items(mistakes) { record ->
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = Color(android.graphics.Color.parseColor(record.failureType.badgeColorHex)).copy(alpha = 0.2f),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(1.dp, Color(android.graphics.Color.parseColor(record.failureType.badgeColorHex)))
            ) {
              Text(
                text = record.failureType.label,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(android.graphics.Color.parseColor(record.failureType.badgeColorHex))
              )
            }
            Text(
              text = record.timestamp,
              style = MaterialTheme.typography.labelSmall,
              color = Color.White.copy(alpha = 0.5f)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = record.missionTitle,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Observed Symptom: ${record.observedSymptom}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Root Cause: ${record.rootCauseCausalLink}",
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
            color = Color(0xFFFBBF24)
          )

          Spacer(modifier = Modifier.height(6.dp))
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Constructive Guidance: ${record.constructiveFeedback}",
              modifier = Modifier.padding(10.dp),
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFF38BDF8)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          Button(
            onClick = { onLaunchMission(record.targetedRemediationMission) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Refresh,
              contentDescription = null,
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Execute Remediation: ${record.targetedRemediationMission}",
              color = Color(0xFF00E5FF),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}

// ============================================================
// 5. LEARNING MEMORY & RETENTION TAB CONTENT
// ============================================================
@Composable
private fun LearningMemoryTabContent(onLaunchMission: (String) -> Unit) {
  val memories by PersonalIntelligencePlatformEngine.learningMemory.collectAsState()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = null,
              tint = Color(0xFF8B5CF6),
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "LONG-TERM LEARNING MEMORY & RETENTION",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF8B5CF6)
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "AEGORA remembers learning evidence across sessions. Inactivity is never punished with score penalties. Instead, constructive reactivation drills keep demonstration fresh.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
          )
        }
      }
    }

    item {
      Text(
        text = "ACTIVE CAPABILITY RETENTION TRACKER",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = Color(0xFF94A3B8)
      )
    }

    items(memories) { memory ->
      val statusColor = Color(android.graphics.Color.parseColor(memory.retentionHealth.urgencyColorHex))
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = memory.topic,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White,
              modifier = Modifier.weight(1f)
            )
            Surface(
              color = statusColor.copy(alpha = 0.15f),
              shape = RoundedCornerShape(6.dp),
              border = BorderStroke(1.dp, statusColor)
            ) {
              Text(
                text = memory.retentionHealth.label,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = statusColor
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Last demonstrated: ${memory.lastDemonstratedTimestamp} (${memory.daysSinceDemonstrated} days ago)",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.5f)
          )

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = memory.decayMessage,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
          )

          if (memory.previousMistakesOvercome.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Past Mistake Overcome: ${memory.previousMistakesOvercome.first()}",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = Color(0xFF10B981)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = { onLaunchMission(memory.reactivationMissionTitle) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, statusColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = null,
              tint = statusColor,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Launch ${memory.reactivationMissionMinutes}m Reactivation Drill",
              color = statusColor,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}

// ============================================================
// 6. CAREER INTELLIGENCE TAB CONTENT
// ============================================================
@Composable
private fun CareerIntelligenceTabContent(onLaunchMission: (String) -> Unit) {
  val careers by PersonalIntelligencePlatformEngine.careerProfiles.collectAsState()
  var selectedCareerId by remember { mutableStateOf("soc_analyst") }
  val activeCareer = careers.find { it.roleId == selectedCareerId } ?: careers.first()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(vertical = 16.dp)
  ) {
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Work,
              contentDescription = null,
              tint = Color(0xFF00E5FF),
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CAREER INTELLIGENCE LAYER",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF00E5FF)
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Current Capability -> Target Role -> Capability Gap -> Targeted Missions. Map your verified evidence directly to industry roles.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
          )
        }
      }
    }

    // Role Selector Strip
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(careers) { role ->
          val isSelected = role.roleId == selectedCareerId
          Surface(
            color = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.15f)
            ),
            modifier = Modifier
              .clickable { selectedCareerId = role.roleId }
              .testTag("career_role_${role.roleId}")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = role.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
              Text(
                text = "Match: ${role.overallMatchPercentage}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF10B981)
              )
            }
          }
        }
      }
    }

    // Selected Role Detailed Gap Breakdown
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = activeCareer.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
            Surface(
              color = Color(0xFF10B981).copy(alpha = 0.15f),
              shape = RoundedCornerShape(8.dp),
              border = BorderStroke(1.dp, Color(0xFF10B981))
            ) {
              Text(
                text = "${activeCareer.readinessIndex}% JOB READINESS",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF10B981)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "PRIMARY BOTTLENECK GAP:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFF59E0B)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = activeCareer.primaryBottleneckGap,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
          )

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "TARGETED MISSION SEQUENCE TO CLOSE GAP:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF00E5FF)
          )
          Spacer(modifier = Modifier.height(6.dp))
          activeCareer.targetedMissionSequence.forEach { missionTitle ->
            Surface(
              color = Color(0xFF0F172A),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = missionTitle,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                  color = Color.White
                )
                Button(
                  onClick = { onLaunchMission(missionTitle) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Text("Launch", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
// HELPER COMPONENTS
// ============================================================

@Composable
fun ClusterExplainabilityCard(
  summary: ClusterCapabilitySummary,
  isExpanded: Boolean,
  onToggle: () -> Unit,
  onLaunchMission: (String) -> Unit
) {
  val clusterColor = when (summary.cluster) {
    CognitiveClusterType.FOUNDATION -> Color(0xFF3B82F6)
    CognitiveClusterType.ACTIVE_DEFENSE -> Color(0xFF10B981)
    CognitiveClusterType.GENERALIZATION_STRESS -> Color(0xFFF59E0B)
    CognitiveClusterType.METACOGNITIVE_STRATEGIC -> Color(0xFF8B5CF6)
  }

  Card(
    colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
    border = BorderStroke(1.dp, if (isExpanded) clusterColor else Color.White.copy(alpha = 0.1f)),
    shape = RoundedCornerShape(14.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onToggle() }
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(clusterColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = summary.cluster.displayName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = Color.White
            )
          }
          Text(
            text = summary.trendLabel,
            style = MaterialTheme.typography.labelSmall,
            color = clusterColor
          )
        }
        Text(
          text = "${summary.score}",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
          color = clusterColor
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = summary.plainEnglishMeaning,
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.85f)
      )

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 12.dp)) {
          HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
          Spacer(modifier = Modifier.height(10.dp))

          ExplainabilityBulletSection("What You Know:", summary.whatLearnerKnows, Color(0xFF10B981))
          Spacer(modifier = Modifier.height(8.dp))
          ExplainabilityBulletSection("What You Can Actually Perform:", summary.whatLearnerCanPerform, Color(0xFF38BDF8))
          Spacer(modifier = Modifier.height(8.dp))
          ExplainabilityBulletSection("Where You Struggle:", summary.strugglePoints, Color(0xFFEF4444))
          Spacer(modifier = Modifier.height(8.dp))
          ExplainabilityBulletSection("Concepts Understood But Not Transferred:", summary.untransferredConcepts, Color(0xFFF59E0B))

          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = { onLaunchMission(summary.topGrowthMissions.first()) },
            colors = ButtonDefaults.buttonColors(containerColor = clusterColor),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "Launch Targeted Drill: ${summary.topGrowthMissions.first()}",
              color = if (summary.cluster == CognitiveClusterType.GENERALIZATION_STRESS) Color.Black else Color.White,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun ExplainabilityBulletSection(title: String, items: List<String>, bulletColor: Color) {
  Column {
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = bulletColor
    )
    items.forEach { item ->
      Row(
        modifier = Modifier.padding(top = 2.dp),
        verticalAlignment = Alignment.Top
      ) {
        Text("• ", color = bulletColor, fontWeight = FontWeight.Bold)
        Text(
          text = item,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = Color.White.copy(alpha = 0.8f)
        )
      }
    }
  }
}

@Composable
fun DiscoveryInsightCard(insight: DiscoveryInsightItem, onAction: () -> Unit) {
  val (badgeColor, badgeLabel) = when (insight.type) {
    DiscoveryType.HIDDEN_STRENGTH -> Color(0xFF10B981) to "HIDDEN STRENGTH"
    DiscoveryType.CRITICAL_DEPENDENCY -> Color(0xFFF59E0B) to "CRITICAL DEPENDENCY"
    DiscoveryType.UNKNOWN_FRONTIER -> Color(0xFF8B5CF6) to "UNKNOWN FRONTIER"
  }

  Surface(
    color = Color(0xFF0F172A),
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
    modifier = Modifier.width(260.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Surface(
          color = badgeColor.copy(alpha = 0.15f),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = badgeLabel,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
            color = badgeColor
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = insight.headline,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = Color.White,
          maxLines = 2
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = insight.explanation,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = Color.White.copy(alpha = 0.7f),
          maxLines = 3,
          overflow = TextOverflow.Ellipsis
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "${insight.actionText} →",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = badgeColor,
        modifier = Modifier.clickable { onAction() }
      )
    }
  }
}

@Composable
fun ProgressMilestoneCard(milestone: ProgressNarrativeMilestone) {
  Card(
    colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
    shape = RoundedCornerShape(12.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = milestone.timeframe.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = Color(0xFF00E5FF)
        )
        Text(
          text = milestone.capabilityDelta,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = Color(0xFF10B981)
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = milestone.title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = Color.White
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = milestone.narrative,
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.8f)
      )
    }
  }
}

@Composable
fun EvidenceProofLedgerItem(proof: EvidenceProofItem) {
  Surface(
    color = Color(0xFF0F172A),
    shape = RoundedCornerShape(10.dp),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = proof.capabilityName,
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
        Surface(
          color = Color(0xFF10B981).copy(alpha = 0.2f),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "CONFIDENCE: ${proof.confidenceScore}%",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
            color = Color(0xFF10B981)
          )
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Type: ${proof.proofType} • Date: ${proof.timestamp}",
        style = MaterialTheme.typography.labelSmall,
        color = Color.White.copy(alpha = 0.6f)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = proof.telemetrySnippet,
        style = MaterialTheme.typography.bodySmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 10.sp
        ),
        color = Color(0xFF38BDF8)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Ledger Signature: ${proof.verifiedHash}",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp
        ),
        color = Color(0xFF94A3B8)
      )
    }
  }
}

@Composable
fun TrustCenterDialog(onDismiss: () -> Unit) {
  val audits by PersonalIntelligencePlatformEngine.trustAudits.collectAsState()

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = Color(0xFF10B981)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Trust & Explainability Center",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "AEGORA is committed to complete algorithmic explainability and privacy isolation. Your intelligence profile is generated deterministically from real security evidence.",
          style = MaterialTheme.typography.bodySmall,
          color = Color.White.copy(alpha = 0.8f)
        )
        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

        audits.forEach { audit ->
          Column {
            Text(
              text = audit.category,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF00E5FF)
            )
            Text(
              text = "What is stored: ${audit.whatIsStored}",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = Color.White.copy(alpha = 0.7f)
            )
            Text(
              text = "Why: ${audit.whyStored}",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = Color.White.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(4.dp))
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
      ) {
        Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    },
    containerColor = Color(0xFF0F172A)
  )
}

// ============================================================
// STATE COMPOSABLES (LOADING, EMPTY, ERROR, BADGES)
// ============================================================

@Composable
private fun BadgePill(text: String, color: Color) {
  Surface(
    shape = RoundedCornerShape(4.dp),
    color = color.copy(alpha = 0.15f),
    border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
      color = color,
      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
    )
  }
}

@Composable
private fun IntelligenceLoadingState() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("intelligence_loading_indicator")
    ) {
      Column(
        modifier = Modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        CircularProgressIndicator(
          color = Color(0xFF00E5FF),
          strokeWidth = 3.dp,
          modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "SYNCHRONIZING CAPABILITY ENGINE",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
          ),
          color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Evaluating 7 mastery gates across verified telemetry and mapping multidimensional cognitive clusters...",
          style = MaterialTheme.typography.bodySmall,
          color = Color.White.copy(alpha = 0.7f),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
      }
    }
  }
}

@Composable
private fun IntelligenceEmptyState(
  state: PersonalIntelligenceUiState.Empty,
  onLaunchMission: (String) -> Unit,
  onLoadDemoProfile: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 24.dp)
      .testTag("intelligence_empty_state"),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    item {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(Color(0xFF00E5FF).copy(alpha = 0.1f))
          .border(1.5.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Psychology,
          contentDescription = null,
          tint = Color(0xFF00E5FF),
          modifier = Modifier.size(36.dp)
        )
      }
    }

    item {
      Text(
        text = state.title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = Color.White,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }

    item {
      Text(
        text = state.message,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.7f),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }

    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "EVIDENCE REQUIRED FOR COGNITIVE MAPPING",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = Color(0xFF00E5FF)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "AEGORA does not estimate or guess your abilities. Every dimension requires verifiable telemetry from interactive labs, SIEM triage, or hands-on simulations.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.8f)
          )
          Spacer(modifier = Modifier.height(14.dp))
          state.recommendedFirstActions.forEachIndexed { idx, action ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = CircleShape,
                color = Color(0xFF10B981).copy(alpha = 0.2f),
                modifier = Modifier.size(22.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text("${idx + 1}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFF10B981))
                }
              }
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = action,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = Color.White,
                modifier = Modifier.weight(1f)
              )
              IconButton(
                onClick = { onLaunchMission("baseline_diag_${idx + 1}") },
                modifier = Modifier.size(28.dp).testTag("btn_launch_diag_${idx + 1}")
              ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Start", tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }

    item {
      OutlinedButton(
        onClick = onLoadDemoProfile,
        border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.6f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
        modifier = Modifier.fillMaxWidth().testTag("btn_load_demo_profile")
      ) {
        Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Preview Sample Operator Telemetry")
      }
    }
  }
}

@Composable
private fun IntelligenceErrorState(
  state: PersonalIntelligenceUiState.Error,
  onRetry: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f)),
      shape = RoundedCornerShape(16.dp),
      modifier = Modifier.fillMaxWidth().testTag("intelligence_error_state")
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "Telemetry Synchronization Notice",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = state.message,
          style = MaterialTheme.typography.bodySmall,
          color = Color.White.copy(alpha = 0.7f),
          textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
          onClick = onRetry,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
          border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
          modifier = Modifier.testTag("btn_retry_intelligence")
        ) {
          Text(state.recoverableAction, color = Color(0xFF00E5FF))
        }
      }
    }
  }
}

