package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.*
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.SkillProgressBar
import com.example.ui.theme.*

enum class LabViewTab {
  INCIDENT_RANGE,
  TERMINAL_LADDERS,
  WEB_APPSEC_LADDER,
  BUSINESS_APP_PATCH,
  BINARY_RESEARCH,
  REASONING_GRAPH,
  MISTAKE_DNA,
  ADAPTIVE_DRILLS,
  COVERAGE_MAP,
  CTF_ARENA
}

enum class AdaptiveDrillType {
  CONCEPT_COLLISION, ALERT_FATIGUE, UNCERTAINTY_TRAINING, STAKEHOLDER_TRANSLATION
}

@Composable
fun LabSimulatorScreen(
  onNavigateToAiMentor: () -> Unit,
  modifier: Modifier = Modifier
) {
  var activeTab by remember { mutableStateOf(LabViewTab.INCIDENT_RANGE) }
  val simulation = AegoraRepository.incidentSimulations.first()
  val ctfList = AegoraRepository.ctfChallenges

  // Reasoning Graph State
  val activeReasoningGraph by AegoraRepository.activeReasoningGraph.collectAsState()
  val mistakeDnaList by AegoraRepository.mistakeDnaRecords.collectAsState()

  // Adaptive Drills State
  var selectedDrillType by remember { mutableStateOf(AdaptiveDrillType.CONCEPT_COLLISION) }
  val conceptCollisions = AegoraRepository.conceptCollisions
  var activeCollisionIndex by remember { mutableStateOf(0) }
  var selectedCollisionOption by remember { mutableStateOf<Int?>(null) }
  var showCollisionExplanation by remember { mutableStateOf(false) }

  // Alert Fatigue Simulator State
  val alertQueue = AegoraRepository.alertFatigueQueue
  var alertQueueIndex by remember { mutableStateOf(0) }
  var triagedScore by remember { mutableStateOf(0) }
  var alertFeedback by remember { mutableStateOf<String?>(null) }

  // Incident Simulator & Counterfactual State
  var selectedContainmentIndex by remember { mutableStateOf<Int?>(null) }
  var isContained by remember { mutableStateOf(false) }
  var showCounterfactuals by remember { mutableStateOf(false) }
  var flagInput by remember { mutableStateOf("") }
  var flagFeedback by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header
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
              text = "LAYER 3 • ADAPTIVE SIMULATION & COGNITIVE LABS",
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Aegora Cyber Simulation Suite",
              style = MaterialTheme.typography.headlineLarge,
              color = TextPrimaryDark
            )
          }
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(CyberCyan.copy(alpha = 0.15f))
              .border(1.dp, CyberCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
          }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Capture the sequence of your reasoning, dissect analytical mistakes, and train in high-noise alert fatigue and uncertainty environments.",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondaryDark
        )
      }
    }

    // 2. Primary Navigation Bar
    item {
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(LabViewTab.entries) { tab ->
          val isSelected = tab == activeTab
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { activeTab = tab }
              .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(8.dp)),
            color = if (isSelected) CyberSurfaceElevated else CyberSurface
          ) {
            Text(
              text = when (tab) {
                LabViewTab.INCIDENT_RANGE -> "🎯 Range Sim"
                LabViewTab.TERMINAL_LADDERS -> "💻 Terminal Ladders"
                LabViewTab.WEB_APPSEC_LADDER -> "🌐 Web AppSec"
                LabViewTab.BUSINESS_APP_PATCH -> "🛠 App Patch"
                LabViewTab.BINARY_RESEARCH -> "🧬 Binary Research"
                LabViewTab.REASONING_GRAPH -> "🧠 Reasoning Graph"
                LabViewTab.MISTAKE_DNA -> "🧬 Mistake DNA"
                LabViewTab.ADAPTIVE_DRILLS -> "⚡ Adaptive Drills"
                LabViewTab.COVERAGE_MAP -> "🗺 Coverage Map"
                LabViewTab.CTF_ARENA -> "🚩 CTF Arena"
              },
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              ),
              color = if (isSelected) CyberCyan else TextSecondaryDark,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            )
          }
        }
      }
    }

    // =========================================================================
    // TAB 1: INCIDENT RANGE & COUNTERFACTUAL BRANCHES
    // =========================================================================
    if (activeTab == LabViewTab.INCIDENT_RANGE) {
      item {
        CyberCard(borderColor = CyberCrimson.copy(alpha = 0.5f)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberCrimson.copy(alpha = 0.2f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberCrimson)
            ) {
              Text(
                text = "ACTIVE SIMULATION #29",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCrimson,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = simulation.threatActor,
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = simulation.title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Target Organization: ${simulation.targetOrg}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(8.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
          ) {
            Text(
              text = "Initial Alert: ${simulation.initialAlert}",
              style = MaterialTheme.typography.bodyMedium,
              color = CyberGold,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      }

      // Live Log Stream
      item {
        CyberSectionHeader(
          title = "SIEM Log Stream & Telemetry",
          subtitle = "${simulation.logs.size} forensic events (Tap log to inspect & log into Reasoning Graph)"
        )
      }

      items(simulation.logs) { log ->
        LogItemCard(
          log = log,
          onInspect = {
            AegoraRepository.logReasoningStep(
              ReasoningGraphStep(
                stepId = "step_${System.currentTimeMillis()}",
                nodeLabel = log.source,
                nodeType = "EVIDENCE_INSPECT",
                actionDescription = "Inspected: ${log.details.take(60)}...",
                timeOffsetSeconds = 15,
                isOptimalStep = log.isMalicious
              )
            )
          }
        )
      }

      // Containment Decision Matrix
      item {
        CyberSectionHeader(
          title = "Containment & Decision Matrix",
          subtitle = "NIST Incident Response Decision Matrix"
        )

        CyberCard {
          Text(
            text = "Select immediate containment response:",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(10.dp))

          simulation.containmentOptions.forEachIndexed { index, option ->
            val isSelected = selectedContainmentIndex == index
            val isCorrect = isContained && index == simulation.correctContainmentIndex

            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { if (!isContained) selectedContainmentIndex = index }
                .border(
                  1.dp,
                  if (isCorrect) CyberEmerald else if (isSelected) CyberCyan else CyberBorder,
                  RoundedCornerShape(8.dp)
                ),
              color = if (isCorrect) CyberEmerald.copy(alpha = 0.15f) else if (isSelected) CyberSurfaceElevated else CyberSurface
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = { if (!isContained) selectedContainmentIndex = index },
                  colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = option,
                  style = MaterialTheme.typography.bodyMedium,
                  color = if (isCorrect) CyberEmerald else TextPrimaryDark
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          if (!isContained) {
            Button(
              onClick = {
                if (selectedContainmentIndex != null) {
                  isContained = true
                  AegoraRepository.logReasoningStep(
                    ReasoningGraphStep(
                      stepId = "step_containment",
                      nodeLabel = "Containment Decision",
                      nodeType = "CONTAINMENT_TRIGGERED",
                      actionDescription = "Selected action: ${simulation.containmentOptions[selectedContainmentIndex!!]}",
                      timeOffsetSeconds = 30,
                      isOptimalStep = selectedContainmentIndex == simulation.correctContainmentIndex
                    )
                  )
                }
              },
              enabled = selectedContainmentIndex != null,
              colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Execute Containment Action", color = CyberBackground, fontWeight = FontWeight.Bold)
            }
          } else {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberEmerald.copy(alpha = 0.15f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = if (selectedContainmentIndex == simulation.correctContainmentIndex)
                    "✓ Optimal Containment: C2 Egress blocked at gateway and compromised host isolated."
                  else
                    "⚠ Sub-optimal action executed. Review counterfactual branches below.",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (selectedContainmentIndex == simulation.correctContainmentIndex) CyberEmerald else CyberAmber
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = { showCounterfactuals = !showCounterfactuals },
                  colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(
                    if (showCounterfactuals) "Hide Counterfactual Blast Radius" else "Show 'What If' Counterfactual Branches",
                    color = CyberCyan
                  )
                }
              }
            }
          }
        }
      }

      // Counterfactual Branches
      if (showCounterfactuals) {
        item {
          CyberSectionHeader(
            title = "Counterfactual Consequence Analysis",
            subtitle = "Downstream impact simulation across alternative choices"
          )
        }

        items(AegoraRepository.counterfactualBranches) { branch ->
          CyberCard(
            borderColor = if (branch.lateralMovementOccurred) CyberRed.copy(alpha = 0.4f) else CyberEmerald.copy(alpha = 0.4f)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = branch.outcomeTitle,
                style = MaterialTheme.typography.titleMedium,
                color = if (branch.lateralMovementOccurred) CyberRed else CyberEmerald
              )
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberSurfaceElevated
              ) {
                Text(
                  text = branch.businessImpactScore,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (branch.lateralMovementOccurred) CyberRed else CyberEmerald,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Decision Choice: ${branch.decisionChoice}",
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = branch.simulationResultDescription,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }
      }
    }

    // =========================================================================
    // TAB: TERMINAL LADDERS (Linux Bandit & PowerShell UnderTheWire)
    // =========================================================================
    if (activeTab == LabViewTab.TERMINAL_LADDERS) {
      item {
        TerminalLadderView()
      }
    }

    // =========================================================================
    // TAB: WEB APPSEC LADDER (PortSwigger Shape + Mutation Engine)
    // =========================================================================
    if (activeTab == LabViewTab.WEB_APPSEC_LADDER) {
      item {
        WebAppSecLadderView()
      }
    }

    // =========================================================================
    // TAB: BUSINESS APP PATCH WORKFLOW (CMD+CTRL Shape)
    // =========================================================================
    if (activeTab == LabViewTab.BUSINESS_APP_PATCH) {
      item {
        BusinessPatchWorkflowView()
      }
    }

    // =========================================================================
    // TAB: BINARY EXPLOITATION & LOW-LEVEL CS (pwn.college Shape)
    // =========================================================================
    if (activeTab == LabViewTab.BINARY_RESEARCH) {
      item {
        BinaryExploitationView()
      }
    }

    // =========================================================================
    // TAB: COVERAGE MAP & CAREER FIELD CHECKLIST
    // =========================================================================
    if (activeTab == LabViewTab.COVERAGE_MAP) {
      item {
        CoverageMapChecklistView()
      }
    }

    // =========================================================================
    // TAB 2: REASONING GRAPH ("SHOW YOUR WORK")
    // =========================================================================
    if (activeTab == LabViewTab.REASONING_GRAPH) {
      item {
        CyberCard(
          borderColor = CyberViolet.copy(alpha = 0.5f),
          backgroundColor = CyberSurface
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Share, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "SIGNATURE #1: REASONING GRAPH",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberViolet
              )
              Text(
                text = activeReasoningGraph.scenarioTitle,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimaryDark
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Every investigation step is logged as a directed graph. Aegora analyzes your sequence (evidence first vs hypothesis anchoring) and compares your workflow against senior analysts.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Cognitive Investigation Fingerprint
          Text(
            text = "YOUR INVESTIGATION FINGERPRINT",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = CyberCyan
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FingerprintBox(label = "Evidence First", value = "${activeReasoningGraph.fingerprintEvidenceFirst}%", color = CyberEmerald, modifier = Modifier.weight(1f))
            FingerprintBox(label = "Timeline Analysis", value = "${activeReasoningGraph.fingerprintTimelineAnalysis}%", color = CyberCyan, modifier = Modifier.weight(1f))
            FingerprintBox(label = "IOC Correlate", value = "${activeReasoningGraph.fingerprintIocCorrelation}%", color = CyberBlue, modifier = Modifier.weight(1f))
            FingerprintBox(label = "Premature Risk", value = activeReasoningGraph.prematureClosureRisk, color = CyberAmber, modifier = Modifier.weight(1f))
          }
        }
      }

      // Expert Shadow Comparison
      item {
        CyberCard(
          borderColor = CyberCyan.copy(alpha = 0.4f),
          backgroundColor = CyberSurface
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "EXPERT SHADOW COMPARISON",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberCyan
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = activeReasoningGraph.expertShadowComparison,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Senior SOC Analyst Baseline Path:",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            activeReasoningGraph.expertSequenceSteps.forEach { step ->
              Text(
                text = step,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = CyberCyan
              )
            }
          }
        }
      }

      // Recorded Step Sequence
      item {
        CyberSectionHeader(
          title = "Recorded Investigation Trace",
          subtitle = "${activeReasoningGraph.steps.size} steps taken in ${activeReasoningGraph.totalInvestigationSeconds}s"
        )
      }

      itemsIndexed(activeReasoningGraph.steps) { index, step ->
        ReasoningStepCard(index = index + 1, step = step)
      }
    }

    // =========================================================================
    // TAB 3: MISTAKE DNA & COGNITIVE BIAS PASSPORT
    // =========================================================================
    if (activeTab == LabViewTab.MISTAKE_DNA) {
      item {
        CyberCard(
          borderColor = CyberAmber.copy(alpha = 0.5f),
          backgroundColor = CyberSurface
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "SIGNATURE #2: MISTAKE DNA",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberAmber
              )
              Text(
                text = "Cognitive Bias & Error Diagnostics",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimaryDark
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Aegora classifies repeated analytical errors: anchoring bias, alert fatigue dismissals, and premature case closures to generate custom targeted drills.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )
        }
      }

      items(mistakeDnaList) { record ->
        MistakeDnaCard(record = record)
      }
    }

    // =========================================================================
    // TAB 4: ADAPTIVE DRILLS (Concept Collision, Alert Fatigue, Uncertainty)
    // =========================================================================
    if (activeTab == LabViewTab.ADAPTIVE_DRILLS) {
      // Sub-tab switcher
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          AdaptiveDrillType.entries.forEach { drill ->
            val isSelected = drill == selectedDrillType
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { selectedDrillType = drill }
                .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(8.dp)),
              color = if (isSelected) CyberSurfaceElevated else CyberSurface
            ) {
              Text(
                text = when (drill) {
                  AdaptiveDrillType.CONCEPT_COLLISION -> "Collision"
                  AdaptiveDrillType.ALERT_FATIGUE -> "Alert Fatigue"
                  AdaptiveDrillType.UNCERTAINTY_TRAINING -> "Uncertainty"
                  AdaptiveDrillType.STAKEHOLDER_TRANSLATION -> "CEO Brief"
                },
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) CyberCyan else TextSecondaryDark,
                modifier = Modifier
                  .padding(vertical = 8.dp)
                  .wrapContentWidth(Alignment.CenterHorizontally)
              )
            }
          }
        }
      }

      // 1. CONCEPT COLLISION ENGINE
      if (selectedDrillType == AdaptiveDrillType.CONCEPT_COLLISION) {
        val collision = conceptCollisions[activeCollisionIndex % conceptCollisions.size]

        item {
          CyberCard(borderColor = CyberCyan.copy(alpha = 0.5f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "CONCEPT COLLISION #${activeCollisionIndex + 1}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberCyan
              )
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberAmber.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber.copy(alpha = 0.4f))
              ) {
                Text(
                  text = "${collision.confusionRatePercent}% Industry Confusion Rate",
                  style = MaterialTheme.typography.labelSmall,
                  color = CyberAmber,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = collision.pairTitle,
              style = MaterialTheme.typography.headlineMedium,
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
              Text(
                text = collision.scenarioPrompt,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark,
                modifier = Modifier.padding(12.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Options
            collision.options.forEachIndexed { index, opt ->
              val isSelected = selectedCollisionOption == index
              val isCorrect = showCollisionExplanation && index == collision.correctIndex

              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { if (!showCollisionExplanation) selectedCollisionOption = index }
                  .border(
                    1.dp,
                    if (isCorrect) CyberEmerald else if (isSelected) CyberCyan else CyberBorder,
                    RoundedCornerShape(8.dp)
                  ),
                color = if (isCorrect) CyberEmerald.copy(alpha = 0.15f) else if (isSelected) CyberSurfaceElevated else CyberSurface
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${('A'.code + index).toChar()}.",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isCorrect) CyberEmerald else CyberCyan
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = opt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCorrect) CyberEmerald else TextPrimaryDark
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!showCollisionExplanation) {
              Button(
                onClick = { showCollisionExplanation = true },
                enabled = selectedCollisionOption != null,
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("Verify Distinction", color = CyberBackground, fontWeight = FontWeight.Bold)
              }
            } else {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = "Key Distinction: ${collision.distinctionKey}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = CyberCyan
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = collision.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                  )
                  Spacer(modifier = Modifier.height(10.dp))
                  Button(
                    onClick = {
                      activeCollisionIndex = (activeCollisionIndex + 1) % conceptCollisions.size
                      selectedCollisionOption = null
                      showCollisionExplanation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text("Next Concept Collision", color = CyberBackground, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }

      // 2. ALERT FATIGUE SIMULATOR (120 alerts high-speed triage)
      if (selectedDrillType == AdaptiveDrillType.ALERT_FATIGUE) {
        val currentAlert = alertQueue[alertQueueIndex % alertQueue.size]

        item {
          CyberCard(borderColor = CyberRed.copy(alpha = 0.5f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "ALERT FATIGUE SPEED DRILL",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberRed
                )
                Text(
                  text = "Alert #${alertQueueIndex + 1} of 120 Queue",
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimaryDark
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
              ) {
                Text(
                  text = "Triage Score: $triagedScore",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberCyan,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, if (currentAlert.severity == "Critical") CyberCrimson else CyberBorder)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = currentAlert.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberCyan
                  )
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (currentAlert.severity == "Critical") CyberCrimson.copy(alpha = 0.2f) else CyberSurface
                  ) {
                    Text(
                      text = currentAlert.severity,
                      style = MaterialTheme.typography.labelSmall,
                      color = if (currentAlert.severity == "Critical") CyberCrimson else TextSecondaryDark,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = currentAlert.ruleName,
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Host: ${currentAlert.targetHost} | Source: ${currentAlert.sourceIp}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = currentAlert.summary,
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextPrimaryDark
                )
              }
            }

            if (alertFeedback != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = alertFeedback!!,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = if (alertFeedback!!.startsWith("✓")) CyberEmerald else CyberAmber
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Triage Decision Actions
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  if (currentAlert.isBenignNoise) {
                    triagedScore += 10
                    alertFeedback = "✓ Correctly dismissed benign noise without wasting investigation time."
                  } else {
                    triagedScore = (triagedScore - 15).coerceAtLeast(0)
                    alertFeedback = "✗ FALSE NEGATIVE: Missed a critical true positive alert!"
                  }
                  alertQueueIndex++
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Dismiss (Noise)", color = TextSecondaryDark)
              }

              Button(
                onClick = {
                  if (currentAlert.isTruePositiveCritical) {
                    triagedScore += 20
                    alertFeedback = "✓ TRUE POSITIVE CAUGHT! Threat contained before propagation."
                  } else {
                    triagedScore = (triagedScore - 5).coerceAtLeast(0)
                    alertFeedback = "⚠ False Alarm escalated to Tier 2."
                  }
                  alertQueueIndex++
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("Escalate (Malicious)", color = Color.White, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 3. UNCERTAINTY TRAINING
      if (selectedDrillType == AdaptiveDrillType.UNCERTAINTY_TRAINING) {
        val unc = AegoraRepository.uncertaintyScenarios.first()

        item {
          CyberCard(borderColor = CyberViolet.copy(alpha = 0.5f)) {
            Text(
              text = "UNCERTAINTY TRAINING (PROBABILISTIC REASONING)",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberViolet
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = unc.title,
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = unc.contextBrief,
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
              Text(
                text = unc.rawLogSnippet,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = CyberCyan,
                modifier = Modifier.padding(10.dp)
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "CONFIDENCE PROBABILITY CALIBRATION",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              FingerprintBox(label = "Malicious Probability", value = "${unc.maliciousConfidence}%", color = CyberRed, modifier = Modifier.weight(1f))
              FingerprintBox(label = "Benign Probability", value = "${unc.benignConfidence}%", color = CyberEmerald, modifier = Modifier.weight(1f))
              FingerprintBox(label = "Unknown / Inconclusive", value = "${unc.unknownConfidence}%", color = CyberAmber, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "Expert SOC Triage Call: ${unc.expertRecommendedDecision}",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = CyberCyan
            )
            Text(
              text = unc.expertRationale,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }
      }

      // 4. STAKEHOLDER TRANSLATION
      if (selectedDrillType == AdaptiveDrillType.STAKEHOLDER_TRANSLATION) {
        val stk = AegoraRepository.stakeholderScenarios.first()

        item {
          CyberCard(borderColor = CyberEmerald.copy(alpha = 0.5f)) {
            Text(
              text = "STAKEHOLDER TRANSLATION DRILL (${stk.executivePersona})",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberEmerald
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Incident ${stk.incidentCode}: Executive Briefing",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Technical Reality: ${stk.technicalIncidentBrief}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "Executive Persona Goal: ${stk.personaGoal}",
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberEmerald.copy(alpha = 0.12f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "✓ High-Impact Executive Summary (Recommended):",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberEmerald
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = stk.goodSampleSummary,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberRed.copy(alpha = 0.08f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberRed.copy(alpha = 0.3f))
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "✗ Flawed Jargon-Heavy Summary (Avoid in Boardrooms):",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = stk.flawedJargonSummary,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
              }
            }
          }
        }
      }
    }

    // =========================================================================
    // TAB 5: CTF ARENA
    // =========================================================================
    if (activeTab == LabViewTab.CTF_ARENA) {
      item {
        CyberCard(
          borderColor = CyberGold.copy(alpha = 0.5f),
          backgroundColor = CyberSurface
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CyberGold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "AEGORA CAPTURE THE FLAG ARENA",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberGold
              )
              Text(
                text = "Hands-On Exploitation & Forensics",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimaryDark
              )
            }
          }
        }
      }

      items(ctfList) { ctf ->
        CyberCard(
          borderColor = if (ctf.isSolved) CyberEmerald.copy(alpha = 0.4f) else CyberBorder
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = ctf.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryDark
              )
              Text(
                text = "${ctf.category} • ${ctf.difficulty} • ${ctf.points} pts",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, if (ctf.isSolved) CyberEmerald else CyberBorder)
            ) {
              Text(
                text = if (ctf.isSolved) "✓ SOLVED" else "${ctf.points} PTS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (ctf.isSolved) CyberEmerald else CyberGold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = ctf.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(10.dp))

          if (!ctf.isSolved) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = flagInput,
                onValueChange = { flagInput = it },
                label = { Text("Enter Flag (aegora{...})", color = TextSecondaryDark, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true
              )
              Button(
                onClick = {
                  if (flagInput.trim().equals(ctf.flag, ignoreCase = true)) {
                    flagFeedback = "✓ Correct Flag Captured!"
                    flagInput = ""
                  } else {
                    flagFeedback = "✗ Invalid Flag. Keep hunting."
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.CenterVertically)
              ) {
                Text("Submit", color = CyberBackground, fontWeight = FontWeight.Bold)
              }
            }
            if (flagFeedback != null) {
              Spacer(modifier = Modifier.height(4.dp))
              Text(text = flagFeedback!!, color = if (flagFeedback!!.startsWith("✓")) CyberEmerald else CyberRed, style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      }
    }
  }
}

@Composable
fun LogItemCard(
  log: LogEvent,
  onInspect: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  CyberCard(
    modifier = modifier.clickable { onInspect() },
    borderColor = if (log.isMalicious) CyberCrimson.copy(alpha = 0.5f) else CyberBorder,
    backgroundColor = if (log.isMalicious) CyberSurfaceElevated else CyberSurface
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = log.timestamp,
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
          color = CyberCyan
        )
        Text(
          text = " • ${log.source} [${log.eventId}]",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }

      Surface(
        shape = RoundedCornerShape(4.dp),
        color = if (log.isMalicious) CyberCrimson.copy(alpha = 0.2f) else CyberSurfaceElevated
      ) {
        Text(
          text = log.severity,
          style = MaterialTheme.typography.labelSmall,
          color = if (log.isMalicious) CyberCrimson else TextSecondaryDark,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = log.details,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = if (log.isMalicious) FontFamily.Monospace else FontFamily.Default,
        fontSize = 12.sp
      ),
      color = if (log.isMalicious) TextPrimaryDark else TextSecondaryDark
    )
  }
}

@Composable
private fun FingerprintBox(
  label: String,
  value: String,
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
        text = value,
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
private fun ReasoningStepCard(
  index: Int,
  step: ReasoningGraphStep,
  modifier: Modifier = Modifier
) {
  CyberCard(
    modifier = modifier,
    borderColor = if (step.isOptimalStep) CyberEmerald.copy(alpha = 0.4f) else CyberAmber.copy(alpha = 0.4f)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (step.isOptimalStep) CyberEmerald.copy(alpha = 0.2f) else CyberAmber.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "$index",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (step.isOptimalStep) CyberEmerald else CyberAmber
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = step.nodeLabel,
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark
        )
      }
      Surface(
        shape = RoundedCornerShape(4.dp),
        color = CyberSurfaceElevated
      ) {
        Text(
          text = "+${step.timeOffsetSeconds}s",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
          color = CyberCyan,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = step.actionDescription,
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondaryDark
    )
  }
}

@Composable
private fun MistakeDnaCard(
  record: MistakeDnaRecord,
  modifier: Modifier = Modifier
) {
  CyberCard(
    modifier = modifier,
    borderColor = CyberAmber.copy(alpha = 0.4f)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = record.patternName,
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark
        )
        Text(
          text = "${record.category} • Diagnosed in ${record.diagnosedIncident}",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = CyberAmber.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber)
      ) {
        Text(
          text = "${record.occurrences}x Observed",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = CyberAmber,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = record.description,
      style = MaterialTheme.typography.bodyMedium,
      color = TextPrimaryDark
    )

    Spacer(modifier = Modifier.height(8.dp))
    Surface(
      shape = RoundedCornerShape(6.dp),
      color = CyberSurfaceElevated,
      border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Healing, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Remediation: ${record.correctiveRemediation}",
          style = MaterialTheme.typography.labelSmall,
          color = CyberCyan
        )
      }
    }
  }
}
