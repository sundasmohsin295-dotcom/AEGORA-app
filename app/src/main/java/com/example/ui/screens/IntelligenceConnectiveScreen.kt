package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun IntelligenceConnectiveScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cyberTwin by AegoraRepository.cyberTwinV81.collectAsState()
  val evidenceList by AegoraRepository.evidenceStream.collectAsState()
  val mistakeProfile by AegoraRepository.mistakeDnaProfile.collectAsState()
  val studentState by AegoraRepository.studentState.collectAsState()
  val selectedTime by AegoraRepository.selectedTimeBudget.collectAsState()
  val timeMission by AegoraRepository.timeFilteredMission.collectAsState()
  val reasoningSession by AegoraRepository.reasoningGraphSession.collectAsState()
  val projectCards by AegoraRepository.projectEvidenceCards.collectAsState()
  val weeklyReport by AegoraRepository.weeklyReport.collectAsState()
  val traces by AegoraRepository.intelligenceTrace.collectAsState()
  val syncStatus by AegoraRepository.syncStatus.collectAsState()

  var selectedTab by remember { mutableStateOf(0) }
  var expandedDimensionKey by remember { mutableStateOf<String?>("investigation") }
  var sampleJobInput by remember {
    mutableStateOf(
      "Senior SOC Analyst role requiring 2+ years SIEM log triage in Splunk, AWS CloudTrail monitoring, MITRE ATT&CK incident classification, and Sigma detection rule engineering. Python scripting preferred."
    )
  }
  var parsedJobResult by remember { mutableStateOf<V81JobRoadmapAnalysis?>(null) }
  var actionSimulationNotice by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      Surface(
        color = CyberBackground,
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
      ) {
        Column {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = onNavigateBack) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "AEGORA v8.1",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  ),
                  color = CyberCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = Color(syncStatus.badgeColor).copy(alpha = 0.15f),
                  border = BorderStroke(1.dp, Color(syncStatus.badgeColor))
                ) {
                  Text(
                    text = syncStatus.label,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    ),
                    color = Color(syncStatus.badgeColor)
                  )
                }
              }
              Text(
                text = "Intelligence Connective Layer",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = TextPrimaryDark
              )
            }
          }

          ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberBackground,
            contentColor = CyberCyan,
            edgePadding = 16.dp
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 },
              text = { Text("CLOSED LOOP", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
            )
            Tab(
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 },
              text = { Text("CYBER TWIN 3.0", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
            )
            Tab(
              selected = selectedTab == 2,
              onClick = { selectedTab = 2 },
              text = { Text("MISTAKE DNA", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
            )
            Tab(
              selected = selectedTab == 3,
              onClick = { selectedTab = 3 },
              text = { Text("REASONING GRAPH", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
            )
            Tab(
              selected = selectedTab == 4,
              onClick = { selectedTab = 4 },
              text = { Text("JOB ROADMAP", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
            )
            Tab(
              selected = selectedTab == 5,
              onClick = { selectedTab = 5 },
              text = { Text("WEEKLY REPORT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
            )
            Tab(
              selected = selectedTab == 6,
              onClick = { selectedTab = 6 },
              text = { Text("INTEL TRACE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
            )
          }
        }
      }
    },
    containerColor = CyberBackground
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .background(CyberBackground)
        .cyberGridBackground()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

      // TAB 0: THE CONTINUOUS CLOSED LEARNING LOOP
      if (selectedTab == 0) {
        item {
          CyberCard(
            borderColor = CyberCyan,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Text(
              text = "AEGORA AUTONOMOUS INTELLIGENCE CORE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Continuous Closed Learning Loop Pipeline",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Every learner action generates cryptographically verifiable evidence, updates the Cyber Twin dimensions, triggers cognitive diagnostics, and selects the Next Best Action.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(14.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberBackground,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("1. USER ACTION → Trigger Lab / Drill / Scenario", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = CyberGreen)
                Text("2. EVIDENCE COLLECTOR → Normalizes Telemetry & Performance", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = CyberCyan)
                Text("3. CYBER TWIN 3.0 → Updates 9 Evidence Vectors & Proof Trails", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = CyberAmber)
                Text("4. COGNITIVE DIAGNOSTICS → Mistake DNA & Decay Radar Check", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = CyberCrimson)
                Text("5. NEXT BEST ACTION ENGINE → 1 Primary + 3 Optional Missions", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = CyberGold)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = {
                AegoraRepository.recordEvidence(
                  activityTitle = "Live Sandbox: Sysmon Event ID 1 Obfuscated PowerShell Analysis",
                  skillDomain = "Endpoint Security",
                  subskill = "Process Lineage & AMSI Deobfuscation",
                  difficulty = "Advanced",
                  score = 92,
                  mistakesCount = 0,
                  reasoningScore = 90,
                  statedConfidence = "HIGH",
                  timeSpentSeconds = 540,
                  strength = EvidenceStrength.STRONG,
                  sourceType = EvidenceSourceType.INDEPENDENT_LAB,
                  proofSnippet = "Extracted deobfuscated payload sha256: 4f8b9... successfully decoded AMSI bypass cradle.",
                  mistakeArchetype = null
                )
                actionSimulationNotice = "Simulation Executed! New evidence generated, Cyber Twin 3.0 updated, and trace logged."
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("simulate_action_btn")
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
              Spacer(modifier = Modifier.width(8.dp))
              Text("SIMULATE LEARNER ACTION & TRIGGER LOOP", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black))
            }

            if (actionSimulationNotice != null) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = actionSimulationNotice ?: "",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = CyberGreen
              )
            }
          }
        }

        // TIME-AWARE ADAPTIVE SELECTOR
        item {
          CyberCard(
            borderColor = CyberGold,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Text(
              text = "TIME-AWARE LEARNING SELECTOR",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tell AEGORA how much time you have right now:",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(TimeAvailabilityOption.values()) { option ->
                val isSelected = selectedTime == option
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) CyberGold else CyberSurface,
                  border = BorderStroke(1.dp, if (isSelected) CyberGold else CyberBorderSubtle),
                  modifier = Modifier.clickable { AegoraRepository.setTimeBudget(option) }
                ) {
                  Text(
                    text = option.label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) Color.Black else TextPrimaryDark
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberBackground,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "${timeMission.mission.estimatedMinutes} MIN ALLOCATION",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                    color = CyberGold
                  )
                  Text(
                    text = timeMission.pedagogicalModel.modelName,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberCyan
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = timeMission.mission.title,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = timeMission.mission.subtitle,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Expected Delta: ${timeMission.expectedScoreDelta}",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                  color = CyberGreen
                )
              }
            }
          }
        }

        // RECENT VERIFIED EVIDENCE STREAM
        item {
          Text(
            text = "RECENT VERIFIED EVIDENCE STREAM (${evidenceList.size} PROOFS)",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = TextSecondaryDark
          )
        }

        items(evidenceList) { evidence ->
          CyberCard(
            borderColor = Color(evidence.strength.badgeColor),
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 12.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = evidence.sourceType.displayName,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = Color(evidence.strength.badgeColor)
              )
              Text(
                text = evidence.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                color = TextSecondaryDark
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = evidence.activityTitle,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Domain: ${evidence.skillDomain} (${evidence.subskill})",
              style = MaterialTheme.typography.bodySmall,
              color = CyberCyan
            )

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberBackground,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "Proof: ${evidence.proofSnippet}",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                color = TextSecondaryDark
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Score: ${evidence.scoreAchieved}%", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
              Text("Reasoning: ${evidence.reasoningQualityScore}%", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
              Text("Strength: ${evidence.strength.label.split(" ").first()}", style = MaterialTheme.typography.labelSmall, color = Color(evidence.strength.badgeColor))
            }
          }
        }
      }

      // TAB 1: CYBER TWIN 3.0 (9 Multi-Dimensional Vectors + Explainability)
      if (selectedTab == 1) {
        item {
          CyberCard(
            borderColor = CyberCyan,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "CYBER TWIN 3.0",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberCyan
                )
                Text(
                  text = "${cyberTwin.callsign} — ${cyberTwin.targetCareerRole}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                  color = TextPrimaryDark
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberGreen)
              ) {
                Text(
                  text = "${cyberTwin.overallCareerReadinessPercent}% READY",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberGreen
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Calibration: ${cyberTwin.confidenceCalibrationState}", style = MaterialTheme.typography.labelSmall, color = CyberAmber)
              Text("Integrity: ${cyberTwin.evidenceIntegrityScore}%", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Primary Blocker: ${cyberTwin.primaryBlocker}",
              style = MaterialTheme.typography.bodySmall,
              color = CyberCrimson
            )
          }
        }

        item {
          Text(
            text = "9 EVIDENCE-BACKED COMPETENCY DIMENSIONS",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = TextSecondaryDark
          )
        }

        items(cyberTwin.dimensions) { dim ->
          val isExpanded = expandedDimensionKey == dim.dimensionKey
          CyberCard(
            borderColor = if (dim.score >= dim.benchmarkTarget) CyberGreen else CyberAmber,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 12.dp
          ) {
            Column(modifier = Modifier.clickable { expandedDimensionKey = if (isExpanded) null else dim.dimensionKey }) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = dim.title.uppercase(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                  Text(
                    text = "${dim.evidenceCount} verified proofs • ${dim.confidenceRating}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                    color = TextSecondaryDark
                  )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "${dim.score}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      fontFamily = FontFamily.Monospace
                    ),
                    color = if (dim.score >= dim.benchmarkTarget) CyberGreen else CyberAmber
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(
                    if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = CyberCyan
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              LinearProgressIndicator(
                progress = { dim.score / 100f },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = if (dim.score >= dim.benchmarkTarget) CyberGreen else CyberAmber,
                trackColor = CyberSurface
              )

              if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = CyberBorderSubtle)
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                  text = "WHY THIS SCORE EXISTS (EVIDENCE TRAIL)",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberGreen
                )
                dim.whyScoreExistsBreakdown.forEach { reason ->
                  Text(
                    text = "• $reason",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark,
                    modifier = Modifier.padding(vertical = 2.dp)
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "DETECTED WEAKNESS FACTORS",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberCrimson
                )
                dim.whyScoreWeaknessFactors.forEach { weakness ->
                  Text(
                    text = "• $weakness",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(vertical = 2.dp)
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CyberBackground,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                      text = "Recommended Intervention:",
                      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                      color = CyberCyan
                    )
                    Text(
                      text = dim.recommendedIntervention,
                      style = MaterialTheme.typography.bodySmall,
                      color = TextPrimaryDark
                    )
                  }
                }
              }
            }
          }
        }
      }

      // TAB 2: MISTAKE DNA 2.0 & COGNITIVE AUTOPSIES
      if (selectedTab == 2) {
        item {
          CyberCard(
            borderColor = CyberCrimson,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "MISTAKE DNA 2.0 & COGNITIVE PROFILE",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberCrimson
                )
                Text(
                  text = "Dominant: ${mistakeProfile.dominantArchetype.displayName}",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberCrimson.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberCrimson)
              ) {
                Text(
                  text = "${mistakeProfile.totalMistakesCataloged} CATALOGED",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberCrimson
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "AEGORA continuously audits your reasoning to identify systematic cognitive breakdown patterns rather than just marking wrong answers.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }

        items(mistakeProfile.highRiskPatterns) { pattern ->
          CyberCard(
            borderColor = CyberCrimson,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 12.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = pattern.archetype.displayName.uppercase(),
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                ),
                color = CyberCrimson
              )
              Text(
                text = "${pattern.occurrenceCount}x Detected",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberAmber
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = pattern.detectedPatternDescription,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Context: ${pattern.contextWhereOccurred} (Last: ${pattern.lastOccurrenceDate})",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberBackground,
              border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "TARGETED REMEDIATION DRILL",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                    color = CyberCyan
                  )
                  Text(
                    text = pattern.correctiveDrillTitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                }
                IconButton(onClick = { /* Launch drill */ }) {
                  Icon(Icons.Default.PlayCircle, contentDescription = null, tint = CyberCyan)
                }
              }
            }
          }
        }
      }

      // TAB 3: REASONING GRAPH 2.0 (Thinking Process vs Reference Model)
      if (selectedTab == 3) {
        item {
          CyberCard(
            borderColor = CyberGreen,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "REASONING GRAPH 2.0 AUDIT",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberGreen
                )
                Text(
                  text = reasoningSession.incidentScenarioTitle,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberGreen)
              ) {
                Text(
                  text = "${reasoningSession.overallThinkingProcessScore}% PROCESS",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberGreen
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Evidence-First: ${reasoningSession.evidenceFirstScore}%", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
              Text("Timeline: ${reasoningSession.timelineConstructionScore}%", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
              Text("Hypothesis Testing: ${reasoningSession.hypothesisFalsificationScore}%", style = MaterialTheme.typography.labelSmall, color = CyberAmber)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberBackground,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "AI Debrief: ${reasoningSession.aiDebriefInsight}",
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = TextSecondaryDark
              )
            }
          }
        }

        item {
          Text(
            text = "STEP-BY-STEP THINKING SEQUENCE AUDIT",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = TextSecondaryDark
          )
        }

        items(reasoningSession.steps) { step ->
          val isOptimal = step.alignmentVerdict == "OPTIMAL_PATH"
          CyberCard(
            borderColor = if (isOptimal) CyberGreen else CyberAmber,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 12.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "STEP ${step.stepNumber}: ${step.type.label.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = Color(step.type.badgeColor)
              )
              Text(
                text = if (isOptimal) "OPTIMAL ALIGNMENT" else "SUBOPTIMAL DETOUR",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = if (isOptimal) CyberGreen else CyberAmber
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Your Action: ${step.learnerActionDescription}",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Reference Optimal: ${step.referenceModelOptimalAction}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }
      }

      // TAB 4: REVERSE JOB ROADMAP & SKILL GAP ANALYZER
      if (selectedTab == 4) {
        item {
          CyberCard(
            borderColor = CyberGold,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Text(
              text = "REVERSE JOB ROADMAP & GAP MAP",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Paste or inspect enterprise job description to compute shortest-path preparation roadmap:",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = sampleJobInput,
              onValueChange = { sampleJobInput = it },
              modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .testTag("job_desc_input"),
              textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberBorderSubtle,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark,
                focusedContainerColor = CyberBackground,
                unfocusedContainerColor = CyberBackground
              )
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = {
                parsedJobResult = AegoraRepository.analyzeJobDescription(sampleJobInput)
              },
              colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("analyze_job_btn")
            ) {
              Text("AUDIT AGAINST CYBER TWIN & GENERATE ROADMAP", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black))
            }
          }
        }

        if (parsedJobResult != null) {
          val result = parsedJobResult!!
          item {
            CyberCard(
              borderColor = CyberGreen,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 14.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = result.extractedRoleTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                  Text(
                    text = "Demonstrated ${result.demonstratedSkillsCount} / ${result.totalRequiredSkillsCount} competencies",
                    style = MaterialTheme.typography.bodySmall,
                    color = CyberCyan
                  )
                }
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CyberGreen.copy(alpha = 0.15f),
                  border = BorderStroke(1.dp, CyberGreen)
                ) {
                  Text(
                    text = "${result.overallMatchPercentage}% MATCH",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black),
                    color = CyberGreen
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "SHORTEST REALISTIC PREPARATION ROADMAP",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberGold
              )
              result.shortestPreparationRoadmap.forEach { step ->
                Text(
                  text = step,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark,
                  modifier = Modifier.padding(vertical = 2.dp)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "EVIDENCE & PROJECT GAPS TO CLOSE",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberCrimson
              )
              result.evidenceGaps.forEach { gap ->
                Text(
                  text = "• $gap",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark,
                  modifier = Modifier.padding(vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // TAB 5: WEEKLY PERSONAL CYBER REPORT & PROJECT CARDS
      if (selectedTab == 5) {
        item {
          CyberCard(
            borderColor = CyberCyan,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "WEEKLY CYBER INTELLIGENCE REPORT",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberCyan
                )
                Text(
                  text = weeklyReport.weekLabel,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberCyan.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberCyan)
              ) {
                Text(
                  text = "${weeklyReport.totalStudyHours}h STUDIED",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberCyan
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Career Delta: ${weeklyReport.careerReadinessDelta}",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = CyberGreen
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Concepts Mastered:",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = CyberGreen
            )
            weeklyReport.conceptsMastered.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark) }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Decaying Skills Requiring Resurrection:",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = CyberAmber
            )
            weeklyReport.conceptsDecaying.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark) }
          }
        }

        item {
          Text(
            text = "VERIFIED PROJECT EVIDENCE CARDS (${projectCards.size})",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = TextSecondaryDark
          )
        }

        items(projectCards) { card ->
          CyberCard(
            borderColor = CyberEmerald,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 12.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = card.targetCareerFamily.familyName.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberEmerald
              )
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberGreen)
              ) {
                Text(
                  text = "VERIFIED CV ASSET",
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                  color = CyberGreen
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = card.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = card.problemStatement,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberBackground,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text("Proof: ${card.verificationProofSnippet}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp), color = CyberCyan)
                Text("Git Link: ${card.gitEvidenceLink}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp), color = CyberGold)
              }
            }
          }
        }
      }

      // TAB 6: DEVELOPER INTELLIGENCE TRACE & OBSERVABILITY
      if (selectedTab == 6) {
        item {
          CyberCard(
            borderColor = CyberCyan,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Text(
              text = "DEVELOPER INTELLIGENCE PIPELINE TRACE",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Real-time deterministic observability stream showing how user events propagate through Evidence -> Cyber Twin -> Cognitive Diagnostics -> Next Best Action.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }

        items(traces) { trace ->
          CyberCard(
            borderColor = CyberBorderSubtle,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 10.dp
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = trace.traceId,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberCyan
              )
              Text(
                text = trace.timestamp,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                color = TextSecondaryDark
              )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Action: ${trace.learnerAction}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
            Text("Evidence: ${trace.evidenceGenerated}", style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp), color = CyberGreen)
            Text("Skill Delta: ${trace.skillAffected} (${trace.scoreDelta})", style = MaterialTheme.typography.bodySmall, color = CyberAmber)
            Text("Cognitive Signal: ${trace.detectedCognitiveSignal}", style = MaterialTheme.typography.bodySmall, color = CyberCrimson)
            Text("Recommendation: ${trace.recommendationOutput}", style = MaterialTheme.typography.bodySmall, color = CyberGold)
          }
        }
      }
    }
  }
}
