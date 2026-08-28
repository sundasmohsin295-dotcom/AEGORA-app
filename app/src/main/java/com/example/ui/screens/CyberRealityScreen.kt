package com.example.ui.screens

import androidx.compose.animation.*
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
import com.example.model.*
import com.example.ui.theme.*

private val CyberTextPrimary = TextPrimaryDark
private val CyberTextSecondary = TextSecondaryDark

@Composable
fun CyberRealityScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf(
    "Radar & Reality",
    "Constellation 3.0",
    "Job Lab Builder",
    "Workplace & Multiverse",
    "Cognitive Arenas",
    "Vault & Portfolio",
    "Fair Rank & Trace"
  )

  val intelFeed by AegoraRepository.v9IntelFeed.collectAsState()
  val personalRadar by AegoraRepository.v9PersonalRadar.collectAsState()
  val transformations by AegoraRepository.v9EventTransformations.collectAsState()
  val knowledgeNodes by AegoraRepository.v9KnowledgeNodes.collectAsState()
  val knowledgeEdges by AegoraRepository.v9KnowledgeEdges.collectAsState()
  val workplaceFeed by AegoraRepository.v9WorkplaceFeed.collectAsState()
  val portfolioProjects by AegoraRepository.v9PortfolioProjects.collectAsState()
  val vaultNotes by AegoraRepository.v9VaultNotes.collectAsState()
  val leaderboard by AegoraRepository.v9CompetitionLeaderboard.collectAsState()
  val traceLogs by AegoraRepository.v9TraceStream.collectAsState()

  // Dynamic simulation states
  var expandedLessonId by remember { mutableStateOf<String?>(null) }
  var selectedNodeId by remember { mutableStateOf<String?>(null) }
  var rawJobInput by remember {
    mutableStateOf(
      "Tier 2 SOC Analyst needed. Must have 2+ years experience in Splunk SPL threat hunting, Windows Sysmon event correlation, Python log automation, AWS CloudTrail incident triage, and Sigma detection engineering."
    )
  }
  var generatedJobResult by remember { mutableStateOf<JobToTrainingSimulationResult?>(null) }
  var selectedConsequenceDecision by remember { mutableStateOf<String?>("IMMEDIATE_ISOLATION") }
  var consequenceOutcome by remember { mutableStateOf<ConsequenceOutcome?>(null) }

  // Feynman Arena states
  var feynmanConcept by remember { mutableStateOf("Kerberos Authentication & Kerberoasting") }
  var feynmanAudience by remember { mutableStateOf(FeynmanAudience.BEGINNER) }
  var feynmanExplanationText by remember {
    mutableStateOf("Kerberos is like a theme park ticket booth. You show your ID once at the gate to get a master ticket (TGT), and then use that master ticket to get wristbands for individual rollercoasters without showing your ID again.")
  }
  var feynmanResult by remember { mutableStateOf<CyberFeynmanAssessment?>(null) }

  // Vault Note Creator state
  var showAddNoteDialog by remember { mutableStateOf(false) }
  var newNoteTitle by remember { mutableStateOf("") }
  var newNoteCategory by remember { mutableStateOf("CONCEPT") }
  var newNoteContent by remember { mutableStateOf("") }
  var newNoteTags by remember { mutableStateOf("sysmon, detection, soc") }

  Scaffold(
    containerColor = CyberBackground,
    topBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(CyberSurface)
          .padding(top = 8.dp, bottom = 4.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("v9_back_button")
          ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "AEGORA v9.0",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = CyberCyan
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                color = Color(0xFF00E676).copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
              ) {
                Text(
                  text = "CYBER REALITY ENGINE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF00E676),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "External Reality → Cyber Twin 3.0 → Multiverse",
              fontSize = 11.sp,
              color = TextSecondaryDark
            )
          }
        }

        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.Transparent,
          contentColor = CyberCyan,
          edgePadding = 16.dp,
          divider = {}
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  text = title,
                  fontSize = 12.sp,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == index) CyberCyan else TextSecondaryDark
                )
              }
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        0 -> RadarAndRealityTab(
          radarItems = personalRadar,
          intelCards = intelFeed,
          transformations = transformations,
          expandedLessonId = expandedLessonId,
          onToggleLesson = { id -> expandedLessonId = if (expandedLessonId == id) null else id }
        )
        1 -> ConstellationTab(
          nodes = knowledgeNodes,
          edges = knowledgeEdges,
          selectedNodeId = selectedNodeId,
          onSelectNode = { id -> selectedNodeId = id }
        )
        2 -> JobLabBuilderTab(
          rawJobInput = rawJobInput,
          onJobInputChange = { rawJobInput = it },
          generatedResult = generatedJobResult,
          onGenerate = {
            generatedJobResult = AegoraRepository.generateV9JobTrainingSimulation(rawJobInput)
          }
        )
        3 -> WorkplaceAndMultiverseTab(
          feedItems = workplaceFeed,
          selectedDecision = selectedConsequenceDecision,
          onSelectDecision = { decision ->
            selectedConsequenceDecision = decision
            consequenceOutcome = AegoraRepository.evaluateV9DecisionConsequence(decision)
          },
          consequenceOutcome = consequenceOutcome ?: AegoraRepository.evaluateV9DecisionConsequence("IMMEDIATE_ISOLATION")
        )
        4 -> CognitiveArenasTab(
          concept = feynmanConcept,
          onConceptChange = { feynmanConcept = it },
          audience = feynmanAudience,
          onAudienceChange = { feynmanAudience = it },
          explanation = feynmanExplanationText,
          onExplanationChange = { feynmanExplanationText = it },
          assessmentResult = feynmanResult,
          onEvaluate = {
            feynmanResult = AegoraRepository.evaluateV9Feynman(feynmanConcept, feynmanAudience, feynmanExplanationText)
          }
        )
        5 -> VaultAndPortfolioTab(
          vaultNotes = vaultNotes,
          portfolioProjects = portfolioProjects,
          onOpenAddNote = { showAddNoteDialog = true }
        )
        6 -> FairRankAndTraceTab(
          leaderboard = leaderboard,
          traceLogs = traceLogs
        )
      }
    }
  }

  if (showAddNoteDialog) {
    AlertDialog(
      onDismissRequest = { showAddNoteDialog = false },
      containerColor = CyberSurface,
      title = { Text("Add Personal Knowledge Note", fontWeight = FontWeight.Bold, color = CyberCyan) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = newNoteTitle,
            onValueChange = { newNoteTitle = it },
            label = { Text("Note Title") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = newNoteContent,
            onValueChange = { newNoteContent = it },
            label = { Text("Content / Key Takeaway") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
          )
          OutlinedTextField(
            value = newNoteTags,
            onValueChange = { newNoteTags = it },
            label = { Text("Tags (comma separated)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newNoteTitle.isNotBlank() && newNoteContent.isNotBlank()) {
              val tagList = newNoteTags.split(",").map { it.trim() }.filter { it.isNotBlank() }
              AegoraRepository.saveV9VaultNote(newNoteTitle, newNoteCategory, newNoteContent, tagList)
              showAddNoteDialog = false
              newNoteTitle = ""
              newNoteContent = ""
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
        ) {
          Text("Save & Link Node", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddNoteDialog = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      }
    )
  }
}

// ============================================================================
// TAB 0: RADAR & REALITY ENGINE
// ============================================================================

@Composable
private fun RadarAndRealityTab(
  radarItems: List<PersonalRadarItem>,
  intelCards: List<ExternalIntelligenceCard>,
  transformations: List<EventToLessonTransformation>,
  expandedLessonId: String?,
  onToggleLesson: (String) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Radar, contentDescription = null, tint = CyberCyan)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "PERSONAL CYBER RADAR",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = CyberCyan,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "External signals prioritized by your target career (SOC Analyst), current skill decay risks, and Mistake DNA triggers.",
            fontSize = 12.sp,
            color = CyberTextSecondary
          )
        }
      }
    }

    item {
      Text(
        text = "TOP PRIORITY RADAR SIGNALS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = CyberTextSecondary,
        fontFamily = FontFamily.Monospace
      )
    }

    items(radarItems) { item ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(item.priorityTag.badgeColor).copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = Color(item.priorityTag.badgeColor).copy(alpha = 0.15f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = item.priorityTag.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(item.priorityTag.badgeColor),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "Relevance: ${item.personalRelevanceScore}%",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = CyberCyan,
              fontFamily = FontFamily.Monospace
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = item.intelCard.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = CyberTextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = item.relevanceRationale,
            fontSize = 12.sp,
            color = CyberTextSecondary
          )
          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = { onToggleLesson(item.intelCard.id) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CyberSurface),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(6.dp)
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (expandedLessonId == item.intelCard.id) "Hide Educational Breakdown" else "Transform Signal → Safe Lesson",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = CyberCyan
            )
          }

          // Unfolded Cyber Event -> Lesson Transformation
          AnimatedVisibility(visible = expandedLessonId == item.intelCard.id) {
            val trans = transformations.find { it.externalCardId == item.intelCard.id } ?: transformations.first()
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .background(Color(0xFF060D1A), RoundedCornerShape(8.dp))
                .padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "CYBER EVENT → PERSONAL LESSON BLUEPRINT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E676),
                fontFamily = FontFamily.Monospace
              )

              Text(
                text = "1. Root Cause: ${trans.rootCauseAnalysis}",
                fontSize = 12.sp,
                color = CyberTextPrimary
              )

              Text(
                text = "2. Attack Path (4 Stages):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan
              )
              trans.attackChainSteps.forEach { step ->
                Text(
                  text = "  • Stage ${step.stepNumber} [${step.techniqueId} ${step.phaseName}]: ${step.description}",
                  fontSize = 11.sp,
                  color = CyberTextSecondary
                )
              }

              Text(
                text = "3. Defensive Lesson: ${trans.defensiveLesson}",
                fontSize = 12.sp,
                color = Color(0xFFFFB800)
              )

              Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text(
                    text = "SAFE SANDBOX PRACTICE SCENARIO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E5FF)
                  )
                  Text(
                    text = trans.safePracticeScenario.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  Text(
                    text = trans.safePracticeScenario.safetyNotice,
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8)
                  )
                }
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "AUTHORITATIVE EXTERNAL INTEL REGISTRY (PROVENANCE VERIFIED)",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = CyberTextSecondary,
        fontFamily = FontFamily.Monospace
      )
    }

    items(intelCards) { card ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(8.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = Color(card.liveStatus.badgeColor).copy(alpha = 0.15f),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = card.liveStatus.label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(card.liveStatus.badgeColor),
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
            Text(
              text = card.publicationDate,
              fontSize = 10.sp,
              color = CyberTextSecondary,
              fontFamily = FontFamily.Monospace
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = card.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = CyberTextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Source: ${card.sourceName} • Trust: ${card.sourceTrust.label}",
            fontSize = 11.sp,
            color = Color(card.sourceTrust.badgeColor)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = card.summary,
            fontSize = 11.sp,
            color = CyberTextSecondary
          )
        }
      }
    }
  }
}

// ============================================================================
// TAB 1: KNOWLEDGE GRAPH 3.0 & CONSTELLATION
// ============================================================================

@Composable
private fun ConstellationTab(
  nodes: List<KnowledgeNode30>,
  edges: List<KnowledgeEdge30>,
  selectedNodeId: String?,
  onSelectNode: (String) -> Unit
) {
  val selectedNode = nodes.find { it.id == selectedNodeId } ?: nodes.firstOrNull()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFBD00FF).copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Hub, contentDescription = null, tint = Color(0xFFBD00FF))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "KNOWLEDGE GRAPH 3.0 & CONSTELLATION",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFFBD00FF),
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "30+ interconnected nodes across 17 ontological types and 10 edge semantics. Tap any node to inspect evidence, decay risk, prerequisites, and career impact.",
            fontSize = 12.sp,
            color = CyberTextSecondary
          )
        }
      }
    }

    // Interactive Node Matrix Carousel
    item {
      Text(
        text = "CONSTELLATION NODES (SELECT TO INSPECT)",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = CyberTextSecondary,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(nodes) { node ->
          val isSelected = node.id == selectedNode?.id
          Surface(
            onClick = { onSelectNode(node.id) },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) Color(node.type.badgeColor).copy(alpha = 0.25f) else CyberSurface,
            border = BorderStroke(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) Color(node.type.badgeColor) else Color.DarkGray
            )
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Surface(
                color = Color(node.type.badgeColor).copy(alpha = 0.15f),
                shape = RoundedCornerShape(3.dp)
              ) {
                Text(
                  text = node.type.label,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(node.type.badgeColor),
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = node.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else CyberTextPrimary,
                maxLines = 1
              )
              Text(
                text = "Mastery: ${node.masteryLevel}%",
                fontSize = 10.sp,
                color = CyberCyan,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    }

    // Selected Node Deep Inspector
    if (selectedNode != null) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, Color(selectedNode.type.badgeColor).copy(alpha = 0.6f))
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = selectedNode.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
              Surface(
                color = Color(selectedNode.type.badgeColor).copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "Tier ${selectedNode.tierLevel} • ${selectedNode.domain}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(selectedNode.type.badgeColor),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = selectedNode.description,
              fontSize = 12.sp,
              color = CyberTextSecondary
            )

            HorizontalDivider(color = Color.DarkGray)

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("Mastery Level", fontSize = 10.sp, color = CyberTextSecondary)
                Text("${selectedNode.masteryLevel}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyberCyan)
              }
              Column {
                Text("Evidence Proofs", fontSize = 10.sp, color = CyberTextSecondary)
                Text("${selectedNode.evidenceProofCount} verified", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
              }
              Column {
                Text("Career Alignment", fontSize = 10.sp, color = CyberTextSecondary)
                Text("${selectedNode.careerRelevance}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB800))
              }
              Column {
                Text("Decay Risk", fontSize = 10.sp, color = CyberTextSecondary)
                Text(selectedNode.decayRisk, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (selectedNode.decayRisk == "LOW") Color(0xFF00E676) else Color(0xFFFF5252))
              }
            }

            // Connected Edges for this node
            val connectedEdges = edges.filter { it.sourceId == selectedNode.id || it.targetId == selectedNode.id }
            if (connectedEdges.isNotEmpty()) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "RELATIONSHIP PATHWAYS IN CONSTELLATION (${connectedEdges.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                fontFamily = FontFamily.Monospace
              )
              connectedEdges.forEach { edge ->
                val otherNodeId = if (edge.sourceId == selectedNode.id) edge.targetId else edge.sourceId
                val otherNode = nodes.find { it.id == otherNodeId }
                Surface(
                  color = CyberSurface,
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "[${edge.edgeType.label}] → ${otherNode?.name ?: otherNodeId}: ${edge.directionalRationale}",
                      fontSize = 11.sp,
                      color = CyberTextSecondary
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// TAB 2: CAREER MARKET REALITY & JOB LAB BUILDER
// ============================================================================

@Composable
private fun JobLabBuilderTab(
  rawJobInput: String,
  onJobInputChange: (String) -> Unit,
  generatedResult: JobToTrainingSimulationResult?,
  onGenerate: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.WorkHistory, contentDescription = null, tint = Color(0xFFFFD700))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "JOB DESCRIPTION → TRAINING SIMULATION",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFFFFD700),
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Paste any real-world enterprise job posting. AEGORA compares requirements against your Cyber Twin 3.0 and synthesizes a tailored hands-on multi-tool lab.",
            fontSize = 12.sp,
            color = CyberTextSecondary
          )
        }
      }
    }

    item {
      OutlinedTextField(
        value = rawJobInput,
        onValueChange = onJobInputChange,
        label = { Text("Paste Raw Job Description", color = CyberCyan) },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = CyberCyan,
          unfocusedBorderColor = Color.DarkGray,
          focusedTextColor = CyberTextPrimary,
          unfocusedTextColor = CyberTextPrimary
        )
      )
      Spacer(modifier = Modifier.height(8.dp))
      Button(
        onClick = onGenerate,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Engineering, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Synthesize Custom Lab & Gap Map", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    }

    if (generatedResult != null) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = generatedResult.jobTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
              Surface(
                color = Color(0xFF00E676).copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "Match: ${generatedResult.overallMatchPercentage}%",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF00E676),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = "Target: ${generatedResult.targetCompany}",
              fontSize = 12.sp,
              color = CyberTextSecondary
            )

            HorizontalDivider(color = Color.DarkGray)

            Text(
              text = "SYNTHESIZED MULTI-TOOL LAB: ${generatedResult.customLab.title}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = CyberCyan,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = generatedResult.customLab.simulatedScenario,
              fontSize = 12.sp,
              color = CyberTextPrimary
            )

            generatedResult.customLab.stepByStepTasks.forEach { task ->
              Text("  • $task", fontSize = 11.sp, color = CyberTextSecondary)
            }

            Surface(
              color = Color(0xFF060D1A),
              shape = RoundedCornerShape(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("Expected Cryptographic Proof:", fontSize = 10.sp, color = Color(0xFFFFB800), fontWeight = FontWeight.Bold)
                Text(generatedResult.customLab.expectedArtifactProof, fontSize = 11.sp, color = Color.White)
              }
            }

            Text(
              text = "INTERVIEW READINESS QUESTIONS (3)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFBD00FF),
              fontFamily = FontFamily.Monospace
            )
            generatedResult.interviewQuestions.forEach { q ->
              Text("  Q: $q", fontSize = 11.sp, color = CyberTextSecondary)
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// TAB 3: WORKPLACE SIMULATOR 2.0 & MULTIVERSE CONSEQUENCE ENGINE
// ============================================================================

@Composable
private fun WorkplaceAndMultiverseTab(
  feedItems: List<WorkplaceFeedItem>,
  selectedDecision: String?,
  onSelectDecision: (String) -> Unit,
  consequenceOutcome: ConsequenceOutcome
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFF0055).copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = Color(0xFFFF0055))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CYBER WORKPLACE 2.0 & CONSEQUENCE ENGINE",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFFFF0055),
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Simulated enterprise inbox with real ambiguity, executive pressure, and branching consequence multiverse timelines.",
            fontSize = 12.sp,
            color = CyberTextSecondary
          )
        }
      }
    }

    item {
      Text(
        text = "ACTIVE ENTERPRISE INBOX (APEX GLOBAL BANKING)",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = CyberTextSecondary,
        fontFamily = FontFamily.Monospace
      )
    }

    items(feedItems) { item ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(item.channel.badgeColor).copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Surface(
              color = Color(item.channel.badgeColor).copy(alpha = 0.15f),
              shape = RoundedCornerShape(3.dp)
            ) {
              Text(
                text = item.channel.displayName,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(item.channel.badgeColor),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
            Text(text = item.timestamp, fontSize = 10.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = item.subject, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
          Text(text = "From: ${item.sender}", fontSize = 11.sp, color = CyberCyan)
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = item.body, fontSize = 11.sp, color = CyberTextSecondary)
        }
      }
    }

    // Branching Consequence Decision Picker
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "DECISION BRANCH POINT: MIMIKATZ ON FINANCE SERVER",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CyberCyan,
            fontFamily = FontFamily.Monospace
          )

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = { onSelectDecision("IMMEDIATE_ISOLATION") },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedDecision == "IMMEDIATE_ISOLATION") Color(0xFF00E676) else CyberSurface
              ),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text("Isolate Host Now", fontSize = 11.sp, color = if (selectedDecision == "IMMEDIATE_ISOLATION") Color.Black else Color.White)
            }

            Button(
              onClick = { onSelectDecision("WAIT_FOR_MORE_LOGS") },
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedDecision == "WAIT_FOR_MORE_LOGS") Color(0xFFFF5252) else CyberSurface
              ),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text("Wait & Monitor", fontSize = 11.sp, color = if (selectedDecision == "WAIT_FOR_MORE_LOGS") Color.Black else Color.White)
            }
          }

          // Consequence Debrief
          Surface(
            color = Color(0xFF060D1A),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = "MULTIVERSE CONSEQUENCE DEBRIEF",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (consequenceOutcome.isOptimalDecision) Color(0xFF00E676) else Color(0xFFFF5252)
              )
              Text(text = consequenceOutcome.businessImpactDescription, fontSize = 11.sp, color = Color.White)
              Text(text = "Attacker Delta: ${consequenceOutcome.attackerMovementDelta}", fontSize = 11.sp, color = CyberCyan)
              Text(text = "Takeaway: ${consequenceOutcome.educationalDebrief}", fontSize = 11.sp, color = Color(0xFFFFB800))
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// TAB 4: ADVANCED COGNITIVE ARENAS (FEYNMAN, CALIBRATION)
// ============================================================================

@Composable
private fun CognitiveArenasTab(
  concept: String,
  onConceptChange: (String) -> Unit,
  audience: FeynmanAudience,
  onAudienceChange: (FeynmanAudience) -> Unit,
  explanation: String,
  onExplanationChange: (String) -> Unit,
  assessmentResult: CyberFeynmanAssessment?,
  onEvaluate: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF76FF03).copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFF76FF03))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "CYBER FEYNMAN & COGNITIVE ARENA",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF76FF03),
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Test your deep conceptual understanding by explaining complex technical attacks across four distinct personas (Beginner, Engineer, SOC, CISO).",
            fontSize = 12.sp,
            color = CyberTextSecondary
          )
        }
      }
    }

    item {
      OutlinedTextField(
        value = concept,
        onValueChange = onConceptChange,
        label = { Text("Cyber Concept", color = CyberCyan) },
        modifier = Modifier.fillMaxWidth()
      )
    }

    item {
      Text(
        text = "SELECT TARGET AUDIENCE PERSONA",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = CyberTextSecondary,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.height(6.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(FeynmanAudience.entries.toTypedArray()) { aud ->
          val isSelected = aud == audience
          Surface(
            onClick = { onAudienceChange(aud) },
            shape = RoundedCornerShape(6.dp),
            color = if (isSelected) Color(0xFF76FF03).copy(alpha = 0.2f) else CyberSurface,
            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) Color(0xFF76FF03) else Color.DarkGray)
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text(
                text = aud.label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF76FF03) else Color.White
              )
              Text(
                text = aud.personaDescription,
                fontSize = 9.sp,
                color = CyberTextSecondary,
                maxLines = 1
              )
            }
          }
        }
      }
    }

    item {
      OutlinedTextField(
        value = explanation,
        onValueChange = onExplanationChange,
        label = { Text("Your Explanation", color = CyberCyan) },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4
      )
      Spacer(modifier = Modifier.height(8.dp))
      Button(
        onClick = onEvaluate,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76FF03)),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color.Black)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Evaluate Explanation & Jargon Control", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    }

    if (assessmentResult != null) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
          shape = RoundedCornerShape(10.dp),
          border = BorderStroke(1.dp, Color(0xFF76FF03).copy(alpha = 0.6f))
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("FEYNMAN SCORE: ${assessmentResult.overallScore}/100", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Color(0xFF76FF03))
              Text("Audience: ${assessmentResult.selectedAudience.label}", fontSize = 11.sp, color = CyberTextSecondary)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Accuracy: ${assessmentResult.accuracyScore}%", fontSize = 11.sp, color = CyberCyan)
              Text("Clarity: ${assessmentResult.clarityScore}%", fontSize = 11.sp, color = Color(0xFF00E676))
              Text("Jargon Control: ${assessmentResult.jargonScore}%", fontSize = 11.sp, color = Color(0xFFFFB800))
              Text("Adaptation: ${assessmentResult.adaptationScore}%", fontSize = 11.sp, color = Color(0xFFBD00FF))
            }

            HorizontalDivider(color = Color.DarkGray)
            Text(
              text = assessmentResult.aiEvaluationDebrief,
              fontSize = 12.sp,
              color = Color.White
            )
          }
        }
      }
    }
  }
}

// ============================================================================
// TAB 5: PERSONAL KNOWLEDGE VAULT & PORTFOLIO GRAPH
// ============================================================================

@Composable
private fun VaultAndPortfolioTab(
  vaultNotes: List<PersonalKnowledgeVaultNote>,
  portfolioProjects: List<V9PortfolioProject>,
  onOpenAddNote: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "PERSONAL KNOWLEDGE VAULT",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = CyberCyan,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "${vaultNotes.size} notes connected to Knowledge Graph 3.0",
            fontSize = 11.sp,
            color = CyberTextSecondary
          )
        }
        Button(
          onClick = onOpenAddNote,
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
          shape = RoundedCornerShape(6.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Note", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    items(vaultNotes) { note ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(8.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = note.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = note.createdAt, fontSize = 10.sp, color = CyberTextSecondary)
          }
          Text(text = note.content, fontSize = 11.sp, color = CyberTextSecondary)
          if (note.commandSnippets.isNotEmpty()) {
            Surface(
              color = Color(0xFF060D1A),
              shape = RoundedCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = note.commandSnippets.first(),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = CyberCyan,
                modifier = Modifier.padding(6.dp)
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "PORTFOLIO CAPSTONE BLUEPRINTS",
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF00E676),
        fontFamily = FontFamily.Monospace
      )
    }

    items(portfolioProjects) { prj ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.5f))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(text = prj.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
          Text(text = "Target Role: ${prj.targetRole}", fontSize = 11.sp, color = CyberCyan)
          Text(text = prj.problemStatement, fontSize = 11.sp, color = CyberTextSecondary)

          Surface(
            color = Color(0xFF060D1A),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text("Resume Bullet:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB800))
              Text("• ${prj.resumeBulletPoint}", fontSize = 11.sp, color = Color.White)
            }
          }
        }
      }
    }
  }
}

// ============================================================================
// TAB 6: FAIR COMPETITION & V9 INTEL TRACE
// ============================================================================

@Composable
private fun FairRankAndTraceTab(
  leaderboard: List<CompetitionLeaderboardEntry>,
  traceLogs: List<String>
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Leaderboard, contentDescription = null, tint = CyberCyan)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "FAIR MULTI-DIMENSIONAL COMPETITION",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = CyberCyan,
              fontFamily = FontFamily.Monospace
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Tiered skill bands (Novice to Elite) evaluated across Investigation, Defense, Reasoning, Communication, and Learning Growth.",
            fontSize = 12.sp,
            color = CyberTextSecondary
          )
        }
      }
    }

    items(leaderboard) { entry ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (entry.isCurrentLearner) Color(0xFF0F2B48) else CyberSurface),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(if (entry.isCurrentLearner) 1.5.dp else 1.dp, if (entry.isCurrentLearner) CyberCyan else Color.DarkGray)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("#${entry.rank}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = CyberCyan, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(entry.callsign, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
              Text(entry.skillBand.tierName, fontSize = 10.sp, color = Color(entry.skillBand.badgeColor))
            }
          }
          Column(horizontalAlignment = Alignment.End) {
            Text("${entry.overallRating} MMR", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00E676), fontFamily = FontFamily.Monospace)
            Text("Inv:${entry.investigationScore} Def:${entry.defenseScore} Growth:${entry.learningGrowthScore}", fontSize = 9.sp, color = CyberTextSecondary)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "DEVELOPER V9 INTELLIGENCE TRACE & OBSERVABILITY",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = CyberTextSecondary,
        fontFamily = FontFamily.Monospace
      )
    }

    items(traceLogs) { log ->
      Surface(
        color = Color(0xFF060D1A),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = log,
          fontSize = 10.sp,
          fontFamily = FontFamily.Monospace,
          color = CyberCyan,
          modifier = Modifier.padding(8.dp)
        )
      }
    }
  }
}
