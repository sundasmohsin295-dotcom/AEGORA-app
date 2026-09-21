package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.model.PredictiveNextAction
import com.example.subscription.AegoraSubscriptionRepository
import com.example.ui.components.BiometricGuard
import com.example.ui.components.SubscriptionPaywallDialog
import com.example.ui.theme.*

data class AdaptiveSkillNode(
  val id: String,
  val name: String,
  val careerTrack: String,
  val currentProficiency: Int,
  val peakProficiency: Int,
  val daysSinceValidation: Int,
  val falsePositiveCount: Int,
  val isDecayed: Boolean,
  val decayExplanation: String,
  val targetedRemediationChallenge: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveSkillPassportScreen(
  onNavigateBack: () -> Unit,
  onLaunchChallenge: (PredictiveNextAction) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val subscriptionState by AegoraSubscriptionRepository.subscriptionState.collectAsState()
  var showPaywall by remember { mutableStateOf(false) }

  val featureAccess = remember(subscriptionState) {
    AegoraSubscriptionRepository.checkFeatureAccess("ADAPTIVE_SKILL_PASSPORT")
  }

  val skillNodes = remember {
    listOf(
      AdaptiveSkillNode(
        id = "skill_mem_forensics",
        name = "Volatile Memory Forensics",
        careerTrack = "Senior Threat Hunter",
        currentProficiency = 48,
        peakProficiency = 86,
        daysSinceValidation = 24,
        falsePositiveCount = 2,
        isDecayed = true,
        decayExplanation = "Decayed -38% due to 24 days without active volatility triage & 2 uncalibrated false positives on svchost hollow processes.",
        targetedRemediationChallenge = "Volatility 3 Malfind & VAD Tree Hollow Injection Triage"
      ),
      AdaptiveSkillNode(
        id = "skill_kerberos_delegation",
        name = "Kerberos Unconstrained Delegation",
        careerTrack = "Purple Team Operator",
        currentProficiency = 54,
        peakProficiency = 82,
        daysSinceValidation = 16,
        falsePositiveCount = 1,
        isDecayed = true,
        decayExplanation = "Decayed -28% due to missed RBCD (Resource-Based Constrained Delegation) transition in Active Directory audit.",
        targetedRemediationChallenge = "TGS Request Ticket Arbitrage & S4U2Self Impersonation"
      ),
      AdaptiveSkillNode(
        id = "skill_pcap_dns",
        name = "PCAP Protocol & DNS Tunneling",
        careerTrack = "SOC Analyst Level 2",
        currentProficiency = 88,
        peakProficiency = 92,
        daysSinceValidation = 2,
        falsePositiveCount = 0,
        isDecayed = false,
        decayExplanation = "Calibrated. Fresh evidence submitted 48 hours ago with zero false positives.",
        targetedRemediationChallenge = "Advanced ICMP Tunneling & Covert Channel Arbitration"
      ),
      AdaptiveSkillNode(
        id = "skill_edr_evasion",
        name = "EDR Telemetry & Sysmon Correlation",
        careerTrack = "Detection Engineer",
        currentProficiency = 76,
        peakProficiency = 80,
        daysSinceValidation = 5,
        falsePositiveCount = 0,
        isDecayed = false,
        decayExplanation = "Nominal retention. Parent-child process tree correlation verified.",
        targetedRemediationChallenge = "Reflective PE Loading Detection via Sysmon Event ID 7"
      ),
      AdaptiveSkillNode(
        id = "skill_incident_containment",
        name = "Firewall & EDR Perimeter Containment",
        careerTrack = "Incident Commander",
        currentProficiency = 92,
        peakProficiency = 95,
        daysSinceValidation = 1,
        falsePositiveCount = 0,
        isDecayed = false,
        decayExplanation = "Peak proficiency. Cryptographic proof minted yesterday.",
        targetedRemediationChallenge = "BGP Null-Route & Enterprise Microsegmentation"
      )
    )
  }

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
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("passport_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = TextPrimaryDark
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
              text = "ADAPTIVE SKILL PASSPORT & DYNAMIC DECAY",
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                fontSize = 13.sp,
                lineHeight = 18.sp
              ),
              color = CyberCyan
            )
            Text(
              text = "LIVING CONSTELLATION • EVIDENCE-BASED COMPETENCY MATRIX",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                lineHeight = 13.sp
              ),
              color = TextTertiaryDark
            )
          }
        }
      }
    },
    containerColor = CyberBackground
  ) { innerPadding ->
    BiometricGuard(
      title = "Adaptive Skill Passport & Career Constellation",
      subtitle = "Zero-Trust Biometric Hardware Enclave Attestation",
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp)
    ) {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 48.dp)
      ) {
        // 1. Constellation Overview Header Card
        item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CyberSurface),
          border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
        ) {
          Column(
            modifier = Modifier
              .background(
                Brush.verticalGradient(listOf(CyberSurfaceElevated, CyberSurface))
              )
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Hub, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "CYBER TWIN CAPABILITY STATUS",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp
                  ),
                  color = CyberCyan
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberAmber.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.5f))
              ) {
                Text(
                  text = "2 DECAYED NODES",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  ),
                  color = CyberAmber,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = "Skills degrade based on inactivity and real false-positive operational triage. Tap any degraded node below to launch targeted telemetry calibration.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
              color = TextSecondaryDark
            )
          }
        }
      }

      // 2. Skill Nodes List
      items(skillNodes) { node ->
        val isDecayed = node.isDecayed
        val progressColor = if (isDecayed) CyberAmber else CyberEmerald

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = CyberSurface),
          border = BorderStroke(
            1.dp,
            if (isDecayed) CyberAmber.copy(alpha = 0.5f) else CyberBorder
          ),
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .testTag("skill_node_${node.id}")
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                  text = node.name,
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  ),
                  color = TextPrimaryDark
                )
                Text(
                  text = node.careerTrack,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = TextTertiaryDark
                )
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = progressColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, progressColor.copy(alpha = 0.5f))
              ) {
                Text(
                  text = "${node.currentProficiency}%",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                  ),
                  color = progressColor,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }

            // Progress Bar
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CyberBorder)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxHeight()
                  .fillMaxWidth(node.currentProficiency / 100f)
                  .background(progressColor)
              )
            }

            // Decay Details
            Text(
              text = node.decayExplanation,
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                lineHeight = 15.sp
              ),
              color = if (isDecayed) CyberAmber else TextSecondaryDark
            )

            if (isDecayed) {
              Spacer(modifier = Modifier.height(4.dp))
              Button(
                onClick = {
                  val action = PredictiveNextAction(
                    id = "remediate_${node.id}",
                    title = node.targetedRemediationChallenge,
                    category = "Skill Calibration",
                    destinationTag = "live_soc_range",
                    urgencyScore = 98,
                    primaryReason = node.decayExplanation,
                    reasoningTags = listOf("Remediation", "Decay Recovery", node.name),
                    estimatedMins = 8,
                    xpReward = 350,
                    telemetryMetric = "Retention: ${node.currentProficiency}% (Peak: ${node.peakProficiency}%)"
                  )
                  onLaunchChallenge(action)
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = CyberAmber,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 44.dp)
                  .testTag("btn_calibrate_${node.id}")
              ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "CALIBRATE NODE (+${node.peakProficiency - node.currentProficiency}% RECOVERY)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.5.sp
                  )
                )
              }
            }
          }
        }
      }
    }
    }
  }

  if (showPaywall) {
    SubscriptionPaywallDialog(
      currentSubscription = subscriptionState,
      onDismiss = { showPaywall = false }
    )
  }
}
