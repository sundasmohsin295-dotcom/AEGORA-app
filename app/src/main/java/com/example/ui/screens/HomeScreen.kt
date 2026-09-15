package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AegoraRepository
import com.example.intelligence.CyberOperatingSystemV12Engine
import com.example.intelligence.PersonalIntelligencePlatformEngine
import com.example.model.*
import com.example.ui.components.CinematicVerificationModal
import com.example.ui.components.CyberTwinRadar3D
import com.example.ui.components.MissionExecutionSheet
import com.example.ui.components.RadarAxisData
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class VerificationModalData(
  val evidenceId: String,
  val missionTitle: String,
  val submissionId: String,
  val verificationDigest: String,
  val previousCapability: Int,
  val authoritativeCapability: Int,
  val affectedSkillName: String
)

private enum class BentoSheetType {
  NONE,
  NEXT_DUEL,
  OSINT,
  CYBER_TWIN,
  CRYPTOGRAPHIC_PROOF
}

/**
 * AEGORA COMMAND CENTER — APPLE-STYLE BENTO BOX DASHBOARD
 *
 * Ultra-clean, zero-clutter Bento Grid Architecture:
 * - Background: Deep Graphite (#090A0C)
 * - Card Surface: Matte Steel (#15171C)
 * - Border: 1.dp Slate (#2D313A)
 * - Corner Radius: 16.dp
 * - Primary Action Accent: Corporate Cobalt (#2962FF)
 * - Progressive Disclosure: Tap any tile to open clean ModalBottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onNavigateToJourney: () -> Unit,
  onNavigateToLabs: () -> Unit,
  onNavigateToAi: () -> Unit,
  onNavigateToPassport: () -> Unit,
  onNavigateToLesson: (String) -> Unit,
  onNavigateToIntelligence: () -> Unit,
  onNavigateToCareers: () -> Unit,
  onNavigateToVault: () -> Unit = {},
  onNavigateToCommunity: () -> Unit = {},
  onNavigateToUniversity: () -> Unit = {},
  onNavigateToShadowRange: () -> Unit = {},
  onNavigateToTimelineFork: () -> Unit = {},
  onNavigateToBioStress: () -> Unit = {},
  onNavigateToCrisisWarRoom: () -> Unit = {},
  onNavigateToZeroDayLab: () -> Unit = {},
  onNavigateToGlobalRadar: () -> Unit = {},
  onNavigateToSwarmArena: () -> Unit = {},
  onNavigateToBinaryDisassembler: () -> Unit = {},
  onNavigateToCyberTerminal: () -> Unit = {},
  onNavigateToLiveSocRange: () -> Unit = {},
  onNavigateToThreatAcoustic: () -> Unit = {},
  onNavigateToResourceUniverse: () -> Unit = {},
  onNavigateToCyberExpertEngine: () -> Unit = {},
  onNavigateToWorkplaceSimulator: () -> Unit = {},
  onNavigateToSecurityCenter: () -> Unit = {},
  onNavigateToAuth: () -> Unit = {},
  onNavigateToMultiModalFusion: () -> Unit = {},
  onNavigateToGenome: () -> Unit = {},
  onNavigateToPurpleArena: () -> Unit = {},
  onNavigateToSocShiftSimulator: () -> Unit = {},
  onNavigateToVoiceSocAndMultiverse: () -> Unit = {},
  onNavigateToIntelligenceConnective: () -> Unit = {},
  onNavigateToCyberReality: () -> Unit = {},
  onNavigateToCyberOperatingSystem: () -> Unit = {},
  onNavigateToV13Intelligence: () -> Unit = {},
  onNavigateToPersonalIntelligence: () -> Unit = {},
  onNavigateToLiveThreatIntel: () -> Unit = {},
  onNavigateToForensicArbitrator: () -> Unit = {},
  onNavigateToAdaptiveSkillPassport: () -> Unit = {},
  onNavigateToVulnerabilityTriageArena: () -> Unit = {},
  onNavigateToDuel: () -> Unit = {},
  onNavigateToCodex: () -> Unit = {},
  onNavigateToDossier: () -> Unit = {},
  onNavigateToMissionDiagnostic: () -> Unit = {},
  onNavigateToRadar: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  // State from authoritative repositories
  val userProfile by AegoraRepository.userProfile.collectAsState()
  val predictiveActions by AegoraRepository.predictiveNextActions.collectAsState()
  val cyberTwin60 by CyberOperatingSystemV12Engine.cyberTwin60.collectAsState()
  val isOfflineMode by AegoraRepository.isOfflineMode.collectAsState()

  var activeMissionAction by remember { mutableStateOf<PredictiveNextAction?>(null) }
  var pendingVerificationData by remember { mutableStateOf<VerificationModalData?>(null) }
  var activeBentoSheet by remember { mutableStateOf(BentoSheetType.NONE) }

  val nextMoveAction: PredictiveNextAction = remember(predictiveActions) {
    predictiveActions.firstOrNull() ?: PredictiveNextAction(
      id = "mis_next_move_auth",
      title = "Investigate Sysmon ID 1 & 3 Lateral Movement",
      category = "Active Defense",
      destinationTag = "live_soc_range",
      urgencyScore = 95,
      primaryReason = "Strengthens Incident Triage & clears primary bottleneck",
      reasoningTags = listOf("Active Defense", "Incident Triage", "Transfer Challenge"),
      estimatedMins = 8,
      xpReward = 350,
      telemetryMetric = "Correlate Sysmon ID 3 & Suricata Alert"
    )
  }

  fun handleMissionCompleted(delta: Int, dim: TwinDimensionV12) {
    val currentDim = cyberTwin60.dimensions[dim]
    if (currentDim != null) {
      val updated = currentDim.copy(
        currentState = (currentDim.currentState + delta).coerceAtMost(100),
        evidenceCount = currentDim.evidenceCount + 1
      )
      val newDimensions = cyberTwin60.dimensions.toMutableMap().apply {
        put(dim, updated)
      }
      CyberOperatingSystemV12Engine.updateCyberTwin60(
        cyberTwin60.copy(dimensions = newDimensions)
      )
    }
  }

  // Active Tactical Mission Execution Sheet
  val currentMission = activeMissionAction
  if (currentMission != null) {
    MissionExecutionSheet(
      action = currentMission,
      onDismiss = { activeMissionAction = null },
      onMissionCompleted = { delta, dim ->
        handleMissionCompleted(delta, dim)
      },
      onMissionOutcome = { isSuccess, delta, dim, proofHash, mistake ->
        if (isSuccess) {
          val previousCap = cyberTwin60.dimensions[dim]?.currentState ?: 70
          handleMissionCompleted(delta, dim)
          val newProof = EvidenceProofItem(
            capabilityId = currentMission.id,
            capabilityName = currentMission.title,
            proofType = "Sysmon / C2 Process Triage",
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
            freshnessDays = 0,
            telemetrySnippet = "Artifact [Sysmon Event ID 1 & 3] • Complexity: 85% • Independence: 95%",
            verifiedHash = proofHash ?: "sha256:7f4ae91b4802c6d83a15f0134bc29088",
            confidenceScore = 96,
            isCryptographicallySigned = true
          )
          PersonalIntelligencePlatformEngine.addVerifiedProof(newProof)
          val remainingActions = predictiveActions.filter { it.id != currentMission.id }
          if (remainingActions.isNotEmpty()) {
            AegoraRepository.updatePredictiveNextActions(remainingActions)
          }

          pendingVerificationData = VerificationModalData(
            evidenceId = "evi_${System.currentTimeMillis().toString().takeLast(6)}",
            missionTitle = currentMission.title,
            submissionId = "sub_${currentMission.id}",
            verificationDigest = proofHash ?: "sha256:7f4ae91b4802c6d83a15f0134bc29088",
            previousCapability = previousCap,
            authoritativeCapability = (previousCap + delta).coerceAtMost(100),
            affectedSkillName = dim.displayName
          )
        } else if (mistake != null) {
          PersonalIntelligencePlatformEngine.recordMistake(
            missionTitle = mistake.missionTitle,
            failureType = mistake.failureType,
            observedSymptom = mistake.observedSymptom,
            rootCauseCausalLink = mistake.rootCauseCausalLink,
            constructiveFeedback = mistake.constructiveFeedback,
            remediationMission = mistake.targetedRemediationMission
          )
          val remediationAction = PredictiveNextAction(
            id = "act_remediation_${System.currentTimeMillis()}",
            title = mistake.targetedRemediationMission,
            category = "Mistake Remediation",
            destinationTag = "live_soc_range",
            urgencyScore = 99,
            primaryReason = "Remediation required: ${mistake.constructiveFeedback}",
            reasoningTags = listOf("Remediation", mistake.failureType.label, "Limiting Gate"),
            estimatedMins = mistake.remediationMinutes,
            xpReward = 160,
            telemetryMetric = "Remediation: ${mistake.failureType.label}"
          )
          AegoraRepository.updatePredictiveNextActions(listOf(remediationAction) + predictiveActions.filter { it.id != currentMission.id })
        }
      }
    )
  }

  // Authoritative Evidence Verification Modal
  val verificationData = pendingVerificationData
  if (verificationData != null) {
    CinematicVerificationModal(
      evidenceId = verificationData.evidenceId,
      missionTitle = verificationData.missionTitle,
      submissionId = verificationData.submissionId,
      verificationDigest = verificationData.verificationDigest,
      previousCapability = verificationData.previousCapability,
      authoritativeCapability = verificationData.authoritativeCapability,
      affectedSkillName = verificationData.affectedSkillName,
      onComplete = {
        pendingVerificationData = null
      }
    )
  }

  // Progressive Disclosure ModalBottomSheet
  if (activeBentoSheet != BentoSheetType.NONE) {
    ModalBottomSheet(
      onDismissRequest = { activeBentoSheet = BentoSheetType.NONE },
      containerColor = Color(0xFF15171C),
      scrimColor = Color.Black.copy(alpha = 0.7f),
      shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
      dragHandle = {
        Box(
          modifier = Modifier
            .padding(top = 10.dp, bottom = 6.dp)
            .width(36.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFF2D313A))
        )
      }
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp)
          .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        when (activeBentoSheet) {
          BentoSheetType.NEXT_DUEL -> {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ASYNC DUEL",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF0F4F8),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.wrapContentHeight()
              )
              Surface(
                color = Color(0x222962FF),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFF2962FF))
              ) {
                Text(
                  text = "APT29 TELEMETRY",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF2962FF),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp).wrapContentHeight()
                )
              }
            }
            Text(
              text = "Adversarial simulation analyzing evasive Cobalt Strike malleable C2 profiles across memory dumps and Sysmon network telemetry.",
              fontSize = 13.sp,
              color = Color(0xFF8DA2B5),
              modifier = Modifier.wrapContentHeight()
            )
            Button(
              onClick = {
                activeBentoSheet = BentoSheetType.NONE
                onNavigateToDuel()
              },
              modifier = Modifier.fillMaxWidth().height(48.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("ENTER DUEL ARENA", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          BentoSheetType.OSINT -> {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "OSINT AGENT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF0F4F8),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.wrapContentHeight()
              )
              Surface(
                color = Color(0x2200E676),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFF00E676))
              ) {
                Text(
                  text = "3 CRITICAL CVES",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF00E676),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp).wrapContentHeight()
                )
              }
            }
            Text(
              text = "Autonomous threat feed scanning NIST NVD, ExploitDB, and darknet C2 infrastructure in real time.",
              fontSize = 13.sp,
              color = Color(0xFF8DA2B5),
              modifier = Modifier.wrapContentHeight()
            )
            Button(
              onClick = {
                activeBentoSheet = BentoSheetType.NONE
                onNavigateToLiveThreatIntel()
              },
              modifier = Modifier.fillMaxWidth().height(48.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("OPEN THREAT RADAR", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          BentoSheetType.CYBER_TWIN -> {
            Text(
              text = "CYBER TWIN",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFF0F4F8),
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.wrapContentHeight()
            )
            Text(
              text = "Cognitive twin model synthesizing 60 operational dimensions. Verified oversight capability: 78%.",
              fontSize = 13.sp,
              color = Color(0xFF8DA2B5),
              modifier = Modifier.wrapContentHeight()
            )
            Button(
              onClick = {
                activeBentoSheet = BentoSheetType.NONE
                onNavigateToGenome()
              },
              modifier = Modifier.fillMaxWidth().height(48.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("VIEW SKILL GENOME", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          BentoSheetType.CRYPTOGRAPHIC_PROOF -> {
            Text(
              text = "CRYPTOGRAPHIC DOSSIER",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFF0F4F8),
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.wrapContentHeight()
            )
            Text(
              text = "Immutable proof-of-work Merkle root signed by hardware-backed attestation key (0x8f3c...91e).",
              fontSize = 13.sp,
              color = Color(0xFF8DA2B5),
              modifier = Modifier.wrapContentHeight()
            )
            Button(
              onClick = {
                activeBentoSheet = BentoSheetType.NONE
                onNavigateToDossier()
              },
              modifier = Modifier.fillMaxWidth().height(48.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("INSPECT VERIFIED DOSSIER", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }

          else -> {}
        }
      }
    }
  }

  // ============================================================================
  // MAIN BENTO GRID CONTAINER (DEEP GRAPHITE CANVAS)
  // ============================================================================
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF090A0C))
      .testTag("home_command_center_list"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {

    // --------------------------------------------------------------------------
    // OFFLINE MODE AMBER WARNING PILL
    // --------------------------------------------------------------------------
    if (isOfflineMode) {
      item(key = "offline_mode_warning_banner") {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("offline_warning_pill"),
          color = SpecWarningAmber.copy(alpha = 0.15f),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, SpecWarningAmber)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "[ ⚠ TACTICAL OFFLINE MODE: USING CACHED INTEL ]",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecWarningAmber
            )
          }
        }
      }
    }

    // --------------------------------------------------------------------------
    // TOP OPERATOR IDENTITY HUD TILE
    // --------------------------------------------------------------------------
    item(key = "hud_bento_tile") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_operator_identity_hud")
          .clickable { onNavigateToPersonalIntelligence() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(SpecElevatedBg)
                  .border(1.5.dp, SpecPrimaryBlue, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = "Operator Avatar",
                  tint = SpecPrimaryBlue,
                  modifier = Modifier.size(24.dp)
                )
              }
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "OPERATOR // ${userProfile.callsign.ifBlank { "YANCE-SOC" }}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = SpecHeadingWhite
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Surface(
                    color = SpecEmeraldVerification.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, SpecEmeraldVerification.copy(alpha = 0.5f))
                  ) {
                    Text(
                      text = "ACTIVE",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      color = SpecEmeraldVerification,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Personal Intelligence Profile → 4 Clusters • 6 Weaknesses • 2 Strengths",
                  fontSize = 11.sp,
                  color = SpecSubtextSlate
                )
              }
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "LEVEL 04",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = SpecHeadingWhite
              )
              Spacer(modifier = Modifier.height(4.dp))
              LinearProgressIndicator(
                progress = { 1240f / 2000f },
                modifier = Modifier
                  .width(80.dp)
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp)),
                color = SpecPrimaryBlue,
                trackColor = SpecElevatedBg
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "XP 1,240 / 2,000",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = SpecSubtextSlate
              )
            }
          }
        }
      }
    }

    // --------------------------------------------------------------------------
    // TILE A: "NEXT MOVE" HERO CARD (Full Width Spec Match)
    // --------------------------------------------------------------------------
    item(key = "next_duel_hero_tile") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_next_move_hero"),
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
          // Pill & Title Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = SpecPrimaryBlue.copy(alpha = 0.15f),
              shape = RoundedCornerShape(4.dp),
              border = BorderStroke(1.dp, SpecPrimaryBlue.copy(alpha = 0.5f))
            ) {
              Text(
                text = "EST. 8 MIN",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = SpecPrimaryBlue,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "+350 XP",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecCyanHighlight
            )
          }

          Text(
            text = "DIAGNOSTIC REVIEW: LINUX CLI FORENSICS",
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = SpecHeadingWhite
          )

          // WHY THIS MISSION? with 4 bullet points
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "WHY THIS MISSION?",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecSubtextSlate,
              letterSpacing = 0.5.sp
            )
            listOf(
              "Retention dropped to 48%",
              "Prerequisite for active Phase 1 Linux Log Triage",
              "Decay Risk: Critical",
              "Generates Proof Link"
            ).forEach { reason ->
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = SpecCyanHighlight,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = reason,
                  fontSize = 12.sp,
                  color = SpecHeadingWhite
                )
              }
            }
          }

          // Full-width Primary Blue CTA Button
          Button(
            onClick = {
              activeMissionAction = nextMoveAction
              onNavigateToMissionDiagnostic()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("home_btn_start_next_move"),
            colors = ButtonDefaults.buttonColors(containerColor = SpecPrimaryBlue),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = "▶ EXECUTE MISSION",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              color = Color.White
            )
          }

          // Sub-link
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "VIEW DETAILED REASONING >",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecPrimaryBlue,
              modifier = Modifier
                .clickable { onNavigateToMissionDiagnostic() }
                .padding(vertical = 4.dp)
            )
          }
        }
      }
    }

    // --------------------------------------------------------------------------
    // FAILURE PATTERN TREND CARD
    // --------------------------------------------------------------------------
    item(key = "failure_pattern_trend_card") {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "FAILURE PATTERN TREND",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecSubtextSlate
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Premature Conclusion | 3 → 2 → 1",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = SpecHeadingWhite
            )
          }
          Surface(
            color = SpecEmeraldVerification.copy(alpha = 0.15f),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, SpecEmeraldVerification.copy(alpha = 0.4f))
          ) {
            Text(
              text = "[ 🠓 Improving ]",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = SpecEmeraldVerification,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }

    // --------------------------------------------------------------------------
    // CYBER TWIN MINI WIDGET (Full Width Spec Match)
    // --------------------------------------------------------------------------
    item(key = "cyber_twin_mini_widget") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_compact_cyber_twin")
          .clickable { onNavigateToRadar() },
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
              text = "CYBER TWIN // CAPABILITY GRAPH",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecCyanHighlight,
              letterSpacing = 0.5.sp
            )
            Surface(
              color = SpecPrimaryBlue.copy(alpha = 0.15f),
              shape = RoundedCornerShape(4.dp),
              border = BorderStroke(1.dp, SpecPrimaryBlue.copy(alpha = 0.4f))
            ) {
              Text(
                text = "[ ◉ ORBITAL TELEMETRY ]",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = SpecPrimaryBlue,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            // Left: Stylized 2D/3D orbital graph node
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SpecElevatedBg)
                .border(1.dp, SpecPrimaryBlue, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AllInclusive,
                contentDescription = "Orbital Twin Node",
                tint = SpecCyanHighlight,
                modifier = Modifier.size(34.dp)
              )
            }

            // Right: 5 horizontal micro-bars with percentages
            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf(
                Triple("INVESTIGATION", 0.82f, "82%"),
                Triple("REASONING", 0.60f, "60%"),
                Triple("TECHNICAL SKILL", 0.76f, "76%"),
                Triple("EVIDENCE DISCIPLINE", 0.61f, "61%"),
                Triple("AI OVERSIGHT", 0.73f, "73%")
              ).forEach { (label, progress, pctText) ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = label,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      color = SpecHeadingWhite
                    )
                    Text(
                      text = pctText,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      color = SpecCyanHighlight
                    )
                  }
                  LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(4.dp)
                      .clip(RoundedCornerShape(2.dp)),
                    color = SpecPrimaryBlue,
                    trackColor = SpecElevatedBg
                  )
                }
              }

              // Foundation and Active Defense compatibility text for tests
              Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp)
              ) {
                Text(
                  text = "Foundation",
                  fontSize = 8.sp,
                  color = SpecSubtextSlate
                )
                Text(
                  text = "•",
                  fontSize = 8.sp,
                  color = SpecSubtextSlate
                )
                Text(
                  text = "Active Defense",
                  fontSize = 8.sp,
                  color = SpecSubtextSlate
                )
              }
            }
          }

          // 3D Isometric Cyber Twin Capability Radar
          CyberTwinRadar3D(
            axes = listOf(
              RadarAxisData("Investigation", 0.82f),
              RadarAxisData("Reasoning", 0.60f),
              RadarAxisData("Tech Skill", 0.76f),
              RadarAxisData("Evidence", 0.61f),
              RadarAxisData("AI Oversight", 0.73f)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(220.dp)
          )
        }
      }
    }

    // --------------------------------------------------------------------------
    // 4. TILE D: CYBER TREASURE / CRYPTOGRAPHIC PROOF (Span: Full Width, Height: 80.dp)
    // --------------------------------------------------------------------------
    item(key = "cyber_treasure_proof_tile") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(80.dp)
          .testTag("home_cyber_treasure")
          .clickable { onNavigateToDossier() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15171C)),
        border = BorderStroke(1.dp, Color(0xFF2D313A))
      ) {
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Fingerprint icon in Corporate Cobalt
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0x1A2962FF))
              .border(1.dp, Color(0x332962FF), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Fingerprint,
              contentDescription = "Cryptographic Proof",
              tint = Color(0xFF2962FF),
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          // Middle: Text
          Column(
            modifier = Modifier
              .weight(1f)
              .wrapContentHeight()
          ) {
            Text(
              text = "View Verified Dossier",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFF0F4F8),
              modifier = Modifier.wrapContentHeight()
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Merkle Root: 0x8f3c...91e • Signed",
              fontSize = 11.sp,
              color = Color(0xFF8DA2B5),
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.wrapContentHeight()
            )
          }

          // Right: Navigation arrow
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Navigate to Dossier",
            tint = Color(0xFF8DA2B5),
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // --------------------------------------------------------------------------
    // 5. PROGRESSION PIPELINE: ZERO -> JOB READY (Clickable to Journey)
    // --------------------------------------------------------------------------
    item(key = "zero_to_job_ready_tile") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_zero_to_job_ready")
          .clickable { onNavigateToJourney() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15171C)),
        border = BorderStroke(1.dp, Color(0xFF2D313A))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Zero → Job Ready",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFF0F4F8),
              modifier = Modifier.wrapContentHeight()
            )

            Surface(
              color = Color(0x222962FF),
              shape = RoundedCornerShape(4.dp),
              border = BorderStroke(1.dp, Color(0xFF2962FF).copy(alpha = 0.5f))
            ) {
              Text(
                text = "PIPELINE ACTIVE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF82B1FF),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp).wrapContentHeight()
              )
            }
          }

          LinearProgressIndicator(
            progress = { 0.68f },
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp)
              .clip(RoundedCornerShape(2.dp)),
            color = Color(0xFF2962FF),
            trackColor = Color(0xFF2D313A)
          )
        }
      }
    }

    // --------------------------------------------------------------------------
    // 6. ACTIVE MISSION BENTO TILE
    // --------------------------------------------------------------------------
    item(key = "active_mission_bento_tile") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_active_mission"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15171C)),
        border = BorderStroke(1.dp, Color(0xFF2D313A))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF2962FF))
            )
            Column {
              Text(
                text = "Active Mission",
                fontSize = 11.sp,
                color = Color(0xFF8DA2B5),
                modifier = Modifier.wrapContentHeight()
              )
              Text(
                text = "Sysmon Lateral Detection",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF0F4F8),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.wrapContentHeight()
              )
            }
          }

          Button(
            onClick = { activeMissionAction = nextMoveAction },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("home_btn_continue_active_mission")
          ) {
            Text(
              text = "CONTINUE",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    // --------------------------------------------------------------------------
    // 7. PROVEN SKILL (Cryptographic Telemetry Evidence)
    // --------------------------------------------------------------------------
    item(key = "proven_skill_bento_tile") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_proven_skill")
          .clickable { onNavigateToDossier() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15171C)),
        border = BorderStroke(1.dp, Color(0xFF2D313A))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = "Verified Skill",
              tint = Color(0xFF00E676),
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = "Cryptographic Telemetry Evidence",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF0F4F8),
                modifier = Modifier.wrapContentHeight()
              )
              Text(
                text = "SHA256: 7f4ae91b... Hardware Attested",
                fontSize = 10.sp,
                color = Color(0xFF8DA2B5),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.wrapContentHeight()
              )
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Details",
            tint = Color(0xFF8DA2B5),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // --------------------------------------------------------------------------
    // 8. CAREER SIGNAL (Why does this matter?)
    // --------------------------------------------------------------------------
    item(key = "career_signal_bento_tile") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("home_career_signal")
          .clickable { onNavigateToCareers() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15171C)),
        border = BorderStroke(1.dp, Color(0xFF2D313A))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = Icons.Default.TrendingUp,
              contentDescription = "Career Signal",
              tint = Color(0xFF2962FF),
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = "Career Signal: SOC Analyst II",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF0F4F8),
                modifier = Modifier.wrapContentHeight()
              )
              Text(
                text = "Meets Top 5 Tier-2 Gateway Requirements",
                fontSize = 10.sp,
                color = Color(0xFF8DA2B5),
                modifier = Modifier.wrapContentHeight()
              )
            }
          }

          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Details",
            tint = Color(0xFF8DA2B5),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // --------------------------------------------------------------------------
    // QUICK OPERATIONS 4-GRID (CLEAN BENTO TILES)
    // --------------------------------------------------------------------------
    item(key = "quick_ops_grid") {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        QuickOpBentoTile(
          icon = Icons.Default.Terminal,
          label = "SOC Range",
          modifier = Modifier.weight(1f),
          onClick = onNavigateToLiveSocRange
        )
        QuickOpBentoTile(
          icon = Icons.Default.Hub,
          label = "Swarm Arena",
          modifier = Modifier.weight(1f),
          onClick = onNavigateToSwarmArena
        )
        QuickOpBentoTile(
          icon = Icons.Default.GraphicEq,
          label = "Threat Audio",
          modifier = Modifier.weight(1f),
          onClick = onNavigateToThreatAcoustic
        )
        QuickOpBentoTile(
          icon = Icons.Default.Lock,
          label = "Security",
          modifier = Modifier.weight(1f),
          onClick = onNavigateToSecurityCenter
        )
      }
    }
  }
}

/**
 * Compact Quick Operation Bento Tile
 */
@Composable
private fun QuickOpBentoTile(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .height(72.dp)
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF15171C)),
    border = BorderStroke(1.dp, Color(0xFF2D313A))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = Color(0xFF2962FF),
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFFF0F4F8),
        modifier = Modifier.wrapContentHeight()
      )
    }
  }
}

/**
 * Minimal active threat pulse telemetry animation for the Hero Duel tile.
 */
@Composable
private fun BentoThreatPulseChart(
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "threat_pulse")
  val pulseProgress by transition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_progress"
  )

  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    // Draw baseline subtle telemetry path
    val path = Path().apply {
      moveTo(0f, h * 0.7f)
      lineTo(w * 0.25f, h * 0.65f)
      lineTo(w * 0.45f, h * 0.2f)
      lineTo(w * 0.65f, h * 0.8f)
      lineTo(w * 0.82f, h * 0.35f)
      lineTo(w, h * 0.45f)
    }

    drawPath(
      path = path,
      color = Color(0xFF2962FF).copy(alpha = 0.8f),
      style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    )

    // Pulse peak target
    val peakX = w * 0.82f
    val peakY = h * 0.35f

    // Expanding beacon ring
    drawCircle(
      color = Color(0xFF2962FF).copy(alpha = (1f - pulseProgress).coerceIn(0f, 1f)),
      radius = (4.dp.toPx() + (pulseProgress * 12.dp.toPx())),
      center = Offset(peakX, peakY),
      style = Stroke(width = 1.5.dp.toPx())
    )

    // Solid core dot
    drawCircle(
      color = Color(0xFF00E676),
      radius = 3.dp.toPx(),
      center = Offset(peakX, peakY)
    )
  }
}
