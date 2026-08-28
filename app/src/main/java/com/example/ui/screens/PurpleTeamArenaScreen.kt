package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.PurpleDuelPhase
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun PurpleTeamArenaScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val duelState by AegoraRepository.purpleTeamArenaState.collectAsState()

  Scaffold(
    topBar = {
      Surface(
        color = CyberBackground,
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCrimson)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "PURPLE TEAM ARENA 8.0",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
              ),
              color = CyberCrimson
            )
            Text(
              text = "Self vs Self: Adversary Breach Duel",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black
              ),
              color = TextPrimaryDark
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
      // 1. Scenario Header & 5-Round Progress Bar
      item {
        CyberCard(
          borderColor = CyberCrimson,
          backgroundColor = CyberSurface,
          shapeRadius = 16.dp
        ) {
          Text(
            text = "OPERATION: ${duelState.scenarioTitle.uppercase()}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = CyberCrimson
          )
          Text(
            text = duelState.activePhase.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = TextPrimaryDark
          )
          Text(
            text = duelState.activePhase.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(16.dp))

          // 5-Round Indicators
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            PurpleDuelPhase.values().forEach { phase ->
              val isCompleted = phase.roundNumber < duelState.activePhase.roundNumber
              val isCurrent = phase == duelState.activePhase
              val color = when {
                isCompleted -> CyberGreen
                isCurrent -> CyberCrimson
                else -> CyberBorder
              }

              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = if (isCurrent || isCompleted) 0.25f else 0.1f))
                    .border(1.5.dp, color, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "R${phase.roundNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = color
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = when (phase.roundNumber) {
                    1 -> "Red"
                    2 -> "Synth"
                    3 -> "Blue"
                    4 -> "Contain"
                    else -> "Debrief"
                  },
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = if (isCurrent) CyberCrimson else TextSecondaryDark
                )
              }
            }
          }
        }
      }

      // 2. Active Phase Interactive Sandbox
      item {
        when (duelState.activePhase) {
          PurpleDuelPhase.RED_TEAM_PLAN -> {
            CyberCard(
              borderColor = CyberCrimson,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 14.dp
            ) {
              Text(
                text = "STAGE RED INTRUSION PLAYBOOK (AUTHORIZED SANDBOX)",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberCrimson
              )
              Spacer(modifier = Modifier.height(8.dp))

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberBackground,
                border = BorderStroke(1.dp, CyberBorder),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = "Ingress Vector: ${duelState.redSelectedTtp}",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberCyan
                  )
                  Text(
                    text = "C2 Channel: ${duelState.redC2Technique}",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberAmber
                  )
                  Text(
                    text = "Persistence: ${duelState.redPersistenceMethod}",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberCrimson
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = {
                  AegoraRepository.advancePurpleTeamDuel(PurpleDuelPhase.TELEMETRY_SYNTH)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCrimson),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("purple_advance_btn")
              ) {
                Text("COMPILE ATTACK TO TELEMETRY (ROUND 2)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
              }
            }
          }

          PurpleDuelPhase.TELEMETRY_SYNTH -> {
            CyberCard(
              borderColor = CyberAmber,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 14.dp
            ) {
              Text(
                text = "SYNTHESIZING SYSTEM TELEMETRY...",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberAmber
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "AEGORA is executing your attack against a sandbox Windows Server + EDR cluster, generating 14,800 Sysmon events, 42 DNS queries, and 3 firewall flow logs.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )
              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = {
                  AegoraRepository.advancePurpleTeamDuel(PurpleDuelPhase.BLUE_TEAM_INVESTIGATION)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberAmber),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("SWITCH TO BLUE TEAM TRIAGE (ROUND 3)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black))
              }
            }
          }

          PurpleDuelPhase.BLUE_TEAM_INVESTIGATION -> {
            CyberCard(
              borderColor = CyberCyan,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 14.dp
            ) {
              Text(
                text = "BLUE TEAM TRIAGE: UNLABELED RAW LOGS",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberCyan
              )
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberBackground,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = "Sysmon Event ID 1: powershell.exe -ExecutionPolicy Bypass -enc SQBFAFgA...",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberCyan
                  )
                  Text(
                    text = "Suricata Flow: Port 8443 beacon detected to external host 198.51.100.44",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = CyberAmber
                  )
                }
              }
              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = {
                  AegoraRepository.advancePurpleTeamDuel(PurpleDuelPhase.INCIDENT_CONTAINMENT)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("EXECUTE CONTAINMENT (ROUND 4)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black))
              }
            }
          }

          PurpleDuelPhase.INCIDENT_CONTAINMENT -> {
            CyberCard(
              borderColor = CyberGreen,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 14.dp
            ) {
              Text(
                text = "EXECUTE INCIDENT RESPONSE CONTAINMENT",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberGreen
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Apply host isolation to WIN-FIN-04 and add perimeter egress drop rule for port 8443.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark
              )
              Spacer(modifier = Modifier.height(14.dp))
              Button(
                onClick = {
                  AegoraRepository.advancePurpleTeamDuel(PurpleDuelPhase.FORENSIC_DEBRIEF)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("SEAL INCIDENT & RUN SELF-DEBRIEF (ROUND 5)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.Black))
              }
            }
          }

          PurpleDuelPhase.FORENSIC_DEBRIEF -> {
            CyberCard(
              borderColor = CyberGold,
              backgroundColor = CyberSurfaceElevated,
              shapeRadius = 16.dp
            ) {
              Text(
                text = "FINAL VERDICT: WOULD YOU HAVE CAUGHT YOURSELF?",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberGold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = duelState.wouldHaveCaughtYourselfVerdict,
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Black,
                  fontFamily = FontFamily.Monospace
                ),
                color = CyberCyan
              )

              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberBackground,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(
                    text = "FORENSIC ATTRIBUTION SUMMARY",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = TextSecondaryDark
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = duelState.debriefSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimaryDark
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))
              OutlinedButton(
                onClick = {
                  AegoraRepository.advancePurpleTeamDuel(PurpleDuelPhase.RED_TEAM_PLAN)
                },
                border = BorderStroke(1.dp, CyberGold),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text("RESET DUEL & TRY NEW TTP STRATEGY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = CyberGold))
              }
            }
          }
        }
      }
    }
  }
}
