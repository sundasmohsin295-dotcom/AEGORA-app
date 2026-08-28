package com.example.ui.screens

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
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun CyberLearningGenomeScreen(
  onNavigateBack: () -> Unit,
  onNavigateToIntervention: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val genome by AegoraRepository.cyberLearningGenomeV7.collectAsState()
  val fingerprint by AegoraRepository.investigationFingerprint.collectAsState()
  val transferTests by AegoraRepository.knowledgeTransferTests.collectAsState()
  val conceptCollisions by AegoraRepository.conceptCollisionPairs.collectAsState()
  val reverseJob by AegoraRepository.reverseJobRoadmap.collectAsState()

  var selectedTab by remember { mutableStateOf(0) }
  val tabTitles = listOf("8-DIM GENOME", "INVESTIGATION FINGERPRINT", "TRANSFER TESTS", "CONCEPT COLLISIONS", "JOB REVERSE MAP")

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
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "CYBER LEARNING GENOME 2.0",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = CyberCyan
              )
              Text(
                text = "Adaptive Competency Engine",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black
                ),
                color = TextPrimaryDark
              )
            }
          }

          // Top Tab Bar
          ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberBackground,
            contentColor = CyberCyan,
            edgePadding = 16.dp,
            divider = {}
          ) {
            tabTitles.forEachIndexed { index, title ->
              Tab(
                selected = selectedTab == index,
                onClick = { selectedTab = index },
                text = {
                  Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (selectedTab == index) CyberCyan else TextSecondaryDark
                  )
                }
              )
            }
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
      when (selectedTab) {
        0 -> {
          // ==========================================
          // TAB 0: 8-DIMENSIONAL GENOME
          // ==========================================
          item {
            CyberCard(
              borderColor = CyberCyan,
              backgroundColor = CyberSurface,
              shapeRadius = 16.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "CALLSIGN: ${genome.callsign}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = CyberCyan
                  )
                  Text(
                    text = "Learning Velocity: ${genome.learningVelocity}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                }
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = CyberCyan.copy(alpha = 0.15f),
                  border = BorderStroke(1.dp, CyberCyan)
                ) {
                  Text(
                    text = "Integrity: ${genome.evidenceIntegrityScore}%",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = CyberCyan
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = "PRIMARY COGNITIVE BOTTLENECK",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = CyberAmber
                  )
                  Text(
                    text = genome.currentPrimaryBottleneck,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark
                  )
                }
              }
            }
          }

          items(genome.dimensions) { dim ->
            CyberCard(
              borderColor = Color(dim.recentTrend.badgeColor),
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 12.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = dim.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                  Text(
                    text = "${dim.recentTrend.label} • Evidence: ${dim.evidenceCount} proofs",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                  )
                }
                Text(
                  text = "${dim.score}",
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                  ),
                  color = Color(dim.recentTrend.badgeColor)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))
              LinearProgressIndicator(
                progress = { dim.score / 100f },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .clip(RoundedCornerShape(3.dp)),
                color = Color(dim.recentTrend.badgeColor),
                trackColor = CyberBorder
              )

              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Intervention: ${dim.recommendedIntervention}",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = CyberCyan
              )
            }
          }
        }

        1 -> {
          // ==========================================
          // TAB 1: INVESTIGATION FINGERPRINT
          // ==========================================
          item {
            CyberCard(
              borderColor = CyberViolet,
              backgroundColor = CyberSurface,
              shapeRadius = 16.dp
            ) {
              Text(
                text = "TRAINING REFERENCE COMPARISON",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberViolet
              )
              Text(
                text = fingerprint.comparativeAnalysisNote,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Premature Closure Risk: ${fingerprint.prematureClosureRisk}",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                ),
                color = CyberAmber
              )
            }
          }

          items(fingerprint.metrics) { metric ->
            CyberCard(
              borderColor = CyberBorder,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 12.dp
            ) {
              Text(
                text = metric.metricName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
              Text(
                text = metric.observedTendency,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )

              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Learner: ${metric.learnerPercentage}%",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                  color = CyberCyan
                )
                Text(
                  text = "Training Reference: ${metric.referenceModelPercentage}%",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                  color = CyberViolet
                )
              }
            }
          }
        }

        2 -> {
          // ==========================================
          // TAB 2: KNOWLEDGE TRANSFER TESTS
          // ==========================================
          items(transferTests) { test ->
            CyberCard(
              borderColor = Color(test.currentStatus.badgeColor),
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 14.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = test.domainContext,
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                  color = CyberCyan
                )
                Text(
                  text = test.currentStatus.label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = Color(test.currentStatus.badgeColor)
                )
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = test.transferScenarioTitle,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
              Text(
                text = "Learned Concept: ${test.sourceConceptTitle}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )

              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberSurface,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = test.transferPrompt,
                  modifier = Modifier.padding(10.dp),
                  style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                  color = TextPrimaryDark
                )
              }
            }
          }
        }

        3 -> {
          // ==========================================
          // TAB 3: CONCEPT COLLISIONS 2.0
          // ==========================================
          items(conceptCollisions) { col ->
            CyberCard(
              borderColor = if (col.isResolved) CyberGreen else CyberAmber,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 14.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${col.conceptA}  ⚡  ${col.conceptB}",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                  ),
                  color = TextPrimaryDark
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (col.isResolved) CyberGreen.copy(alpha = 0.15f) else CyberAmber.copy(alpha = 0.15f)
                ) {
                  Text(
                    text = if (col.isResolved) "RESOLVED" else "${col.confusionRatePercent}% CONFUSION",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = if (col.isResolved) CyberGreen else CyberAmber
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = col.coreDistinction,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Drill: ${col.microDrillTitle}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  color = CyberCyan
                )
              )
            }
          }
        }

        4 -> {
          // ==========================================
          // TAB 4: REVERSE JOB ROADMAP
          // ==========================================
          item {
            CyberCard(
              borderColor = CyberCyan,
              backgroundColor = CyberSurface,
              shapeRadius = 16.dp
            ) {
              Text(
                text = reverseJob.jobTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = TextPrimaryDark
              )
              Text(
                text = "Target Sector: ${reverseJob.targetCompanyOrSector}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )

              Spacer(modifier = Modifier.height(12.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "AEGORA Training Match Score",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                  color = TextSecondaryDark
                )
                Text(
                  text = "${reverseJob.aegoraTrainingMatchScore}%",
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                  ),
                  color = CyberCyan
                )
              }
            }
          }

          items(reverseJob.extractedSkills) { skill ->
            CyberCard(
              borderColor = if (skill.isDemonstrated) CyberGreen else CyberAmber,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 10.dp
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = skill.skillName,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
                Text(
                  text = if (skill.isDemonstrated) "DEMONSTRATED (${skill.learnerMasteryPercent}%)" else "GAP (${skill.learnerMasteryPercent ?: 0}%)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = if (skill.isDemonstrated) CyberGreen else CyberAmber
                )
              }
            }
          }
        }
      }
    }
  }
}
