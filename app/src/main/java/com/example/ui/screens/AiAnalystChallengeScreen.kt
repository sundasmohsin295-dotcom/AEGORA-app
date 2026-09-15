package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ChaosMonkeyReportUi(
  val confidenceScore: Int,
  val threatSummary: String,
  val evidencePoints: List<String>,
  val plantedHallucinationFlag: String,
  val hallucinatedIndex: Int
)

@Composable
fun AiAnalystChallengeScreen(
  onNavigateBack: () -> Unit,
  onChallengeAi: () -> Unit,
  onViewEvidence: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  var showEvidenceDetails by remember { mutableStateOf(false) }
  var hasChallenged by remember { mutableStateOf(false) }
  var selectedEvidenceIndex by remember { mutableStateOf<Int?>(null) }
  var scenarioIndex by remember { mutableIntStateOf(0) }

  // High-fidelity Red Team AI Chaos Monkey Scenarios enforcing the exact system prompt & JSON schema
  val scenarios = remember {
    listOf(
      ChaosMonkeyReportUi(
        confidenceScore = 96,
        threatSummary = "Multi-stage Advanced Persistent Threat (APT) exfiltration detected over ICMP tunnel with active C2 orchestration.",
        evidencePoints = listOf(
          "Abnormally oversized 1024-byte ICMP Echo Request packets transmitted to external destination 198.51.100.44 (standard baseline 64B)",
          "Shannon entropy measured at 4.12 inside ICMP payloads, confirming encrypted byte encapsulation",
          "Subnet route analysis reveals internal host 192.168.1.256 initiated the initial staging beacon",
          "Cleartext credential leak discovered in simultaneous HTTP POST auth stream"
        ),
        plantedHallucinationFlag = "Subtle Hallucination: IP address 192.168.1.256 contains an invalid IPv4 octet (> 255), violating standard CIDR subnet bounds.",
        hallucinatedIndex = 2
      ),
      ChaosMonkeyReportUi(
        confidenceScore = 94,
        threatSummary = "Cobalt Strike Malleable C2 communication intercepted alongside cleartext administrator credential exposure.",
        evidencePoints = listOf(
          "HTTP POST transmission to portal.corp.lan exposed Basic Authentication header 'admin:P1n3apple!99!' in cleartext",
          "Periodic GET check-ins to /api/v1/checkin match known Cobalt Strike malleable HTTP profile signatures",
          "Origin destination 198.51.100.44 confirmed as an authoritative Akamai CDN update relay",
          "User-Agent spoofing MSIE 9.0 on Windows NT 6.1 identified across external socket connections"
        ),
        plantedHallucinationFlag = "Subtle Hallucination: 198.51.100.44 is RFC 5737 TEST-NET-2 documentation space, NOT an Akamai CDN update relay.",
        hallucinatedIndex = 2
      ),
      ChaosMonkeyReportUi(
        confidenceScore = 91,
        threatSummary = "Covert DNS Tunneling exfiltration with high-rate TXT query amplification.",
        evidencePoints = listOf(
          "DNS query velocity exceeded 420 requests/second targeting subdomain 'e2a4.stage.threat-intel.corp'",
          "Average label length of 34 characters with Shannon entropy 3.92 indicates binary exfiltration",
          "DNS server at 1.1.1.1 responded with TCP RST indicating firewall drop on port 53",
          "Simultaneous ICMP ping telemetry observed matching heartbeat intervals"
        ),
        plantedHallucinationFlag = "Subtle Hallucination: Standard DNS operates primarily over UDP/53 and does not return TCP RST packets during standard query resolution.",
        hallucinatedIndex = 2
      )
    )
  }

  val currentReport = scenarios[scenarioIndex % scenarios.size]

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(SpecCanvasBg)
      .testTag("screen_ai_analyst_challenge"),
    containerColor = SpecCanvasBg,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .background(SpecCanvasBg)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SpecElevatedBg)
            .border(1.dp, SpecBorder, RoundedCornerShape(10.dp))
            .testTag("ai_analyst_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = SpecHeadingWhite
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(
            text = "RED TEAM AI // CHAOS MONKEY",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = SpecHeadingWhite,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "Autonomous Adversary Hallucination Arena",
            fontSize = 11.sp,
            color = SpecSubtextSlate
          )
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(SpecCanvasBg)
          .navigationBarsPadding()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Primary Action: CHALLENGE AI
        Button(
          onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            hasChallenged = true
            onChallengeAi()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("ai_analyst_challenge_btn"),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (hasChallenged) SpecEmeraldVerification else SpecPrimaryBlue
          ),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = if (hasChallenged) Icons.Default.CheckCircle else Icons.Default.Security,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (hasChallenged) "HALLUCINATION EXPOSED (+300 XP)" else "CHALLENGE AI HALLUCINATION",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 0.5.sp
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Cycle telemetry
          OutlinedButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              hasChallenged = false
              selectedEvidenceIndex = null
              showEvidenceDetails = false
              scenarioIndex++
            },
            modifier = Modifier.weight(1f).height(46.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = SpecElevatedBg),
            border = BorderStroke(1.dp, SpecBorder),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = SpecCyanHighlight, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("NEW TELEMETRY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SpecCyanHighlight)
          }

          // Secondary Action: VIEW EVIDENCE
          OutlinedButton(
            onClick = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              showEvidenceDetails = !showEvidenceDetails
              onViewEvidence()
            },
            modifier = Modifier.weight(1f).height(46.dp).testTag("ai_analyst_view_evidence_btn"),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = SpecCardBg),
            border = BorderStroke(1.dp, SpecBorder),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(
              text = if (showEvidenceDetails) "HIDE LOGS" else "RAW EVIDENCE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = SpecSubtextSlate,
              letterSpacing = 0.5.sp
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Top Alert Banner: Red/Crimson badge
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_analyst_alert_banner"),
        color = SpecFailureRed.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SpecFailureRed.copy(alpha = 0.6f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(SpecFailureRed)
            )
            Text(
              text = "RED TEAM AGENT // CONFIDENCE: ${currentReport.confidenceScore}%",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecFailureRed
            )
          }
          Surface(
            color = SpecFailureRed.copy(alpha = 0.3f),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = "CHAOS MONKEY",
              fontSize = 9.sp,
              fontWeight = FontWeight.Black,
              fontFamily = FontFamily.Monospace,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      // Claim Box: Quote container
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_analyst_claim_box"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = null,
              tint = SpecCyanHighlight,
              modifier = Modifier.size(22.dp)
            )
            Text(
              text = "AUTONOMOUS INCIDENT REPORT",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecSubtextSlate,
              letterSpacing = 1.sp
            )
          }

          Text(
            text = "“${currentReport.threatSummary}”",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = SpecHeadingWhite,
            lineHeight = 20.sp
          )
        }
      }

      // EVIDENCE POINTS (Interactive selectable telemetry)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("ai_analyst_evidence_analysis_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TELEMETRY EVIDENCE POINTS",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecSubtextSlate,
              letterSpacing = 1.sp
            )
            Text(
              text = "TAP TO SPOT FLAW",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              color = SpecCyanHighlight
            )
          }

          currentReport.evidencePoints.forEachIndexed { idx, point ->
            val isSelected = selectedEvidenceIndex == idx
            val isHallucinated = idx == currentReport.hallucinatedIndex
            val borderColor = when {
              hasChallenged && isHallucinated -> SpecEmeraldVerification
              isSelected -> SpecCyanHighlight
              else -> SpecBorder
            }
            val bgColor = when {
              hasChallenged && isHallucinated -> SpecEmeraldVerification.copy(alpha = 0.15f)
              isSelected -> SpecCyanHighlight.copy(alpha = 0.1f)
              else -> SpecElevatedBg
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = bgColor,
              border = BorderStroke(1.dp, borderColor),
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  selectedEvidenceIndex = idx
                }
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Text(
                  text = "[#${idx + 1}]",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  color = if (hasChallenged && isHallucinated) SpecEmeraldVerification else SpecSubtextSlate
                )
                Text(
                  text = point,
                  fontSize = 12.sp,
                  color = SpecHeadingWhite,
                  lineHeight = 17.sp,
                  modifier = Modifier.weight(1f)
                )
                if (hasChallenged && isHallucinated) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Planted Hallucination Located",
                    tint = SpecEmeraldVerification,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }

      // REVEALED HALLUCINATION BANNER
      AnimatedVisibility(visible = hasChallenged) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFF062816),
          border = BorderStroke(1.dp, SpecEmeraldVerification),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = SpecEmeraldVerification, modifier = Modifier.size(20.dp))
              Text(
                text = "PLANTED HALLUCINATION IDENTIFIED",
                color = SpecEmeraldVerification,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
            Text(
              text = currentReport.plantedHallucinationFlag,
              color = Color.White,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
            Text(
              text = "+300 XP Awarded • HAMM Cognitive Attestation Verified",
              color = SpecEmeraldVerification,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }

      if (showEvidenceDetails) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SpecElevatedBg),
          border = BorderStroke(1.dp, SpecCyanHighlight.copy(alpha = 0.4f))
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "SCAPY PCAP KERNEL INGESTION LOGS",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecCyanHighlight
            )
            Text(
              text = "[PCAP_RX] Dissected 1,482 packets across loopback and external eth0 interfaces.\n[INSPECTION] ICMP Echo request size=1024 bytes (baseline threshold 64B).\n[AUTH_INSPECT] Cleartext credentials exposed in unencrypted HTTP stream.\n[RED_TEAM_SEED] Subtle hallucination injected into JSON schema output.",
              fontSize = 11.sp,
              color = SpecHeadingWhite,
              fontFamily = FontFamily.Monospace,
              lineHeight = 16.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
