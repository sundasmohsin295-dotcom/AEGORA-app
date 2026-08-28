package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun VoiceSocAndMultiverseScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val voiceScenarios by AegoraRepository.voiceSocScenarios.collectAsState()
  val multiverseBranches by AegoraRepository.multiverseBranches.collectAsState()
  var selectedTab by remember { mutableStateOf(0) }

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
                text = "CRISIS COMMUNICATIONS & MULTIVERSE 8.0",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                ),
                color = CyberCyan
              )
              Text(
                text = "Voice SOC Drills & Multiverse Replay",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Black
                ),
                color = TextPrimaryDark
              )
            }
          }

          TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CyberBackground,
            contentColor = CyberCyan
          ) {
            Tab(
              selected = selectedTab == 0,
              onClick = { selectedTab = 0 },
              text = { Text("VOICE SOC CRISIS DRILL", style = MaterialTheme.typography.labelSmall) }
            )
            Tab(
              selected = selectedTab == 1,
              onClick = { selectedTab = 1 },
              text = { Text("INCIDENT MULTIVERSE REPLAY", style = MaterialTheme.typography.labelSmall) }
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
      if (selectedTab == 0) {
        // TAB 0: VOICE SOC CRISIS DRILL
        items(voiceScenarios) { scenario ->
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
              Text(
                text = "INCOMING CALLER",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberAmber
              )
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberGreen.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberGreen)
              ) {
                Text(
                  text = "VERBAL CALL",
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = CyberGreen
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = scenario.callerPersona,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
              color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberBackground,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "\"${scenario.callerPromptAudioText}\"",
                  style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                  color = CyberCyan
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "YOUR RECORDED VERBAL RESPONSE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = TextSecondaryDark
            )
            Text(
              text = scenario.recordedLearnerResponse ?: "No response recorded.",
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Calmness: ${scenario.calmnessScore}%", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
              Text("Clarity: ${scenario.clarityScore}%", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
              Text("Accuracy: ${scenario.technicalAccuracyScore}%", style = MaterialTheme.typography.labelSmall, color = CyberAmber)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurface,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "AI Debrief: ${scenario.aiVoiceDebrief}",
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = TextSecondaryDark
              )
            }
          }
        }
      } else {
        // TAB 1: INCIDENT MULTIVERSE REPLAY
        items(multiverseBranches) { branch ->
          val isBaseline = branch.branchId == "multi_01"
          CyberCard(
            borderColor = if (isBaseline) CyberGreen else CyberCrimson,
            backgroundColor = CyberSurfaceElevated,
            shapeRadius = 14.dp
          ) {
            Text(
              text = branch.hypothesisTitle,
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
              ),
              color = if (isBaseline) CyberGreen else CyberCrimson
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = branch.whatIfDecisionText,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = branch.simulatedOutcomeDescription,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Simulated Loss: $${branch.financialImpactEstimateDollars}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = if (isBaseline) CyberGreen else CyberCrimson
              )
              Text(
                text = "Hosts Compromised: ${branch.lateralMovementHostsAffected}",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = CyberAmber
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Key Takeaway: ${branch.forensicKeyTakeaway}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = CyberCyan
            )
          }
        }
      }
    }
  }
}
