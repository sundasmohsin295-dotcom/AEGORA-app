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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.CyberExpertEngineRepository
import com.example.model.*
import com.example.ui.components.ChamferedCutCornerShape
import com.example.ui.components.HexagonShape
import com.example.ui.theme.*

/**
 * AEGORA Cyber Expert Development Engine (V8 Master Architecture).
 * Dynamic Personal Cyber Twin, 10-Tier Expertise Ladder, Prerequisite Discovery Graph,
 * Multi-dimensional Capability Matrix, and Realistic Professional Judgment Simulator.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberExpertEngineScreen(
  onNavigateBack: () -> Unit,
  onNavigateToLab: (String) -> Unit,
  onNavigateToLesson: () -> Unit,
  onAskAi: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val twin by CyberExpertEngineRepository.cyberTwin.collectAsState()
  val judgmentScenarios by CyberExpertEngineRepository.judgmentScenarios.collectAsState()
  var selectedTab by remember { mutableStateOf(0) } // 0: Twin & Ladder, 1: Prerequisite Discovery, 2: Judgment Scenarios
  var activeScenarioIndex by remember { mutableStateOf(0) }
  var selectedChoiceId by remember { mutableStateOf<String?>(null) }
  var hasSubmittedDecision by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "CYBER EXPERT ENGINE",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                ),
                color = NeonCyan
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = NeonCyan.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
              ) {
                Text(
                  text = "V8 ENGINE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  ),
                  color = NeonCyan,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Capability Over Completion • 10-Tier Ladder • Personal Cyber Twin",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
      )
    },
    containerColor = CyberBackground,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 36.dp)
    ) {
      // 1. High-Value Action of the Day
      item {
        Surface(
          shape = ChamferedCutCornerShape,
          color = CyberSurfaceElevated,
          border = BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.7f)),
          modifier = Modifier.fillMaxWidth()
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
                    .size(36.dp)
                    .clip(HexagonShape)
                    .background(NeonCyan.copy(alpha = 0.2f))
                    .border(1.dp, NeonCyan, HexagonShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Bolt, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "DAILY HIGH-VALUE MISSION",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 0.8.sp
                    ),
                    color = NeonCyan
                  )
                  Text(
                    text = "Personalized for ${twin.callsign}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberEmerald.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
              ) {
                Text(
                  text = "INDEPENDENCE: ${twin.capabilityMatrix.overallIndependenceScore}%",
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

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = twin.dailyHighValueMission,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = TextPrimaryDark
            )
          }
        }
      }

      // 2. Tutorial Illusion Alert (if active)
      if (twin.tutorialIllusionDetected) {
        item {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF261205),
            border = BorderStroke(1.2.dp, TerminalAmber),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = TerminalAmber, modifier = Modifier.size(26.dp))
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "TUTORIAL ILLUSION DETECTOR ACTIVE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = TerminalAmber
                )
                Text(
                  text = "High quiz scores detected (95%), but hands-on incident speed is lagging (62%). Shift from passive reading to unguided terminal drills to build real practitioner reflex.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark
                )
              }
            }
          }
        }
      }

      // 3. Navigation Tabs
      item {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = CyberSurfaceVariant,
          contentColor = NeonCyan,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = NeonCyan
            )
          }
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Cyber Twin & Ladder", style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Prerequisite Discovery", style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("Judgment Simulator", style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
        }
      }

      // 4. Tab Contents
      when (selectedTab) {
        0 -> {
          // 10-Tier Current Level & Next Gate
          item {
            ExpertiseLadderCard(twin = twin)
          }

          // 6-Dimension Capability Matrix
          item {
            CapabilityMatrixCard(matrix = twin.capabilityMatrix)
          }

          // Skills Grid in Cyber Twin (Mastered, Strong, Developing, Weak, Unknown)
          item {
            Text(
              text = "PERSONAL CYBER TWIN REPOSITORY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              ),
              color = NeonCyan
            )
          }

          items(twin.skillsGrid, key = { it.skillId }) { node ->
            CyberTwinNodeCard(node = node, onAskAi = onAskAi)
          }
        }

        1 -> {
          // Prerequisite Gap Engine
          item {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CyberSurface,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "PREREQUISITE GAP ENGINE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberEmerald
                  )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "When an analyst struggles with complex correlation or exploitation, AEGORA isolates the foundational root cause (e.g. missing packet flag knowledge, UTC normalization) and prescribes targeted 10-minute interventions.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
              }
            }
          }

          items(twin.activePrerequisiteChains) { chain ->
            PrerequisiteChainCard(
              chain = chain,
              onStartDrill = {
                CyberExpertEngineRepository.resolvePrerequisiteChain(chain.targetSkill)
                onNavigateToLab(chain.actionRoute)
              }
            )
          }
        }

        2 -> {
          // Realistic "What Would You Do?" Problem Training
          val scenario = judgmentScenarios.getOrNull(activeScenarioIndex)
          if (scenario != null) {
            item {
              RealisticScenarioCard(
                scenario = scenario,
                selectedChoiceId = selectedChoiceId,
                hasSubmitted = hasSubmittedDecision,
                onSelectChoice = { selectedChoiceId = it },
                onSubmitDecision = {
                  hasSubmittedDecision = true
                  selectedChoiceId?.let { cid ->
                    CyberExpertEngineRepository.recordJudgmentDecision(scenario.id, cid)
                  }
                },
                onNextScenario = {
                  hasSubmittedDecision = false
                  selectedChoiceId = null
                }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ExpertiseLadderCard(twin: PersonalCyberTwin) {
  val level = twin.currentExpertiseLevel
  val color = Color(level.themeColor)

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, color),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = color.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, color)
          ) {
            Text(
              text = level.shortCode,
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
              ),
              color = color,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "CURRENT EXPERTISE LEVEL",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              ),
              color = TextSecondaryDark
            )
            Text(
              text = level.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
          }
        }

        Text(
          text = "Tier ${level.levelNumber} / 9",
          style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
          color = color
        )
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = level.description,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(12.dp))
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = CyberSurfaceElevated,
        border = BorderStroke(1.dp, CyberBorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "NEXT LEVEL GATE:",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = NeonCyan
          )
          Text(
            text = "${twin.nextLevelGateRemaining} unassisted incident triages required",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimaryDark
          )
        }
      }
    }
  }
}

@Composable
fun CapabilityMatrixCard(matrix: MultiDimensionalCapabilityScore) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = CyberSurface,
    border = BorderStroke(1.dp, CyberBorderSubtle),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "6-DIMENSION CAPABILITY MATRIX",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.8.sp
        ),
        color = NeonCyan
      )
      Spacer(modifier = Modifier.height(10.dp))

      DimensionBar("Technical Knowledge", matrix.technicalKnowledge, NeonCyan)
      DimensionBar("Practical Hands-On", matrix.practicalHandsOn, CyberEmerald)
      DimensionBar("Hypothesis Reasoning", matrix.hypothesisReasoning, CyberViolet)
      DimensionBar("Investigation Depth", matrix.investigationDepth, TerminalAmber)
      DimensionBar("Business Communication", matrix.businessCommunication, Color(0xFF00E5FF))
      DimensionBar("Professional Judgment", matrix.professionalJudgment, NeonPink)
    }
  }
}

@Composable
fun DimensionBar(label: String, score: Int, color: Color) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
      Text("$score/100", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold), color = color)
    }
    Spacer(modifier = Modifier.height(3.dp))
    LinearProgressIndicator(
      progress = { score / 100f },
      modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = CyberSurfaceElevated
    )
  }
}

@Composable
fun CyberTwinNodeCard(node: CyberTwinSkillNode, onAskAi: (String, String) -> Unit) {
  val statusColor = Color(node.status.badgeColor)

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.dp, CyberBorderSubtle),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = statusColor.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
          ) {
            Text(
              text = node.status.label,
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              ),
              color = statusColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = node.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
        }

        Text(
          text = "${node.score}%",
          style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = statusColor
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "${node.domain} • Current Tier: ${node.currentLevel.shortCode}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      if (node.missingPrerequisites.isNotEmpty()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Info, contentDescription = null, tint = TerminalAmber, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Missing Prerequisite: ${node.missingPrerequisites.joinToString(", ")}",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
            color = TerminalAmber
          )
        }
      }

      if (node.verifiedEvidenceArtifact != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CyberEmerald.copy(alpha = 0.1f),
          border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.3f))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Demonstrated: ${node.verifiedEvidenceArtifact}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
              color = CyberEmerald
            )
          }
        }
      }
    }
  }
}

@Composable
fun PrerequisiteChainCard(chain: PrerequisiteChain, onStartDrill: () -> Unit) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, TerminalAmber.copy(alpha = 0.8f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "PREREQUISITE DIAGNOSTIC",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          ),
          color = TerminalAmber
        )
        Text(
          text = "${chain.estimatedMins} Min Drill",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
          color = TextSecondaryDark
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = chain.targetSkill,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Observed Struggle: ${chain.detectedGap}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(6.dp))
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = TerminalAmber.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, TerminalAmber.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = chain.rootCause,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
          color = TerminalAmber,
          modifier = Modifier.padding(10.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))
      Button(
        onClick = onStartDrill,
        colors = ButtonDefaults.buttonColors(containerColor = TerminalAmber),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Launch 1-Click Prerequisite Intervention", color = Color.Black, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun RealisticScenarioCard(
  scenario: RealisticJudgmentScenario,
  selectedChoiceId: String?,
  hasSubmitted: Boolean,
  onSelectChoice: (String) -> Unit,
  onSubmitDecision: () -> Unit,
  onNextScenario: () -> Unit
) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, NeonCyan),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Surface(
        shape = RoundedCornerShape(4.dp),
        color = NeonCyan.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
      ) {
        Text(
          text = scenario.rolePersona.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          ),
          color = NeonCyan,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = scenario.title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = scenario.situationBrief,
        style = MaterialTheme.typography.bodySmall,
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = "WHAT WOULD YOU DO?",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold
        ),
        color = NeonCyan
      )

      Spacer(modifier = Modifier.height(8.dp))
      scenario.choices.forEach { choice ->
        val isSelected = selectedChoiceId == choice.id
        val choiceBorder = if (isSelected) NeonCyan else CyberBorderSubtle
        val choiceBg = if (isSelected) NeonCyan.copy(alpha = 0.12f) else CyberSurfaceVariant

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = choiceBg,
          border = BorderStroke(1.dp, choiceBorder),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = !hasSubmitted) { onSelectChoice(choice.id) }
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              RadioButton(
                selected = isSelected,
                onClick = { if (!hasSubmitted) onSelectChoice(choice.id) },
                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = choice.actionText,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = TextPrimaryDark
              )
            }

            if (hasSubmitted && isSelected) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Trade-offs: ${choice.tradeOffs}",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = if (choice.isOptimal) CyberEmerald else TerminalAmber
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (!hasSubmitted) {
        Button(
          onClick = onSubmitDecision,
          enabled = selectedChoiceId != null,
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Commit Professional Judgment Decision", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      } else {
        // Expert Comparison Box
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = CyberSurfaceElevated,
          border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "EXPERT DECISION COMPARISON",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberEmerald
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = scenario.expertRationaleComparison,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )
          }
        }
      }
    }
  }
}
