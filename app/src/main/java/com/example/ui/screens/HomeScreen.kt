package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AegoraRepository
import com.example.intelligence.CyberOperatingSystemV12Engine
import com.example.intelligence.PersonalIntelligencePlatformEngine
import com.example.model.*
import com.example.ui.components.ChamferedCutCornerShape
import com.example.ui.components.CinematicCyberTwinVisualizer
import com.example.ui.components.CinematicVerificationModal
import com.example.ui.components.MissionExecutionSheet
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

/**
 * AEGORA COMMAND CENTER — CLEAN, INTERACTIVE, JOB-READY DASHBOARD
 *
 * Design Principle:
 * "Don't make students consume cybersecurity content. Make them experience cybersecurity."
 *
 * Answers 3 Questions Immediately:
 * 1. Where am I? (Operator HUD, current capability state, progression stage)
 * 2. What should I do next? (Dominant "NEXT MOVE" interactive hero)
 * 3. Why does this matter? (Contextual reason, verified career signal, tangible proof)
 */
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
  modifier: Modifier = Modifier
) {
  // State from authoritative repositories & engines
  val userProfile by AegoraRepository.userProfile.collectAsState()
  val dailyMission by AegoraRepository.dailyMission.collectAsState()
  val todaysMissionV8 by AegoraRepository.todaysMissionV8.collectAsState()
  val predictiveActions by AegoraRepository.predictiveNextActions.collectAsState()
  val cyberTwin60 by CyberOperatingSystemV12Engine.cyberTwin60.collectAsState()

  val clusterSummaries by PersonalIntelligencePlatformEngine.clusterSummaries.collectAsState()
  val verifiedProofs by PersonalIntelligencePlatformEngine.verifiedCapabilityProofs.collectAsState()
  val learningMemory by PersonalIntelligencePlatformEngine.learningMemory.collectAsState()
  val mistakeRecords by PersonalIntelligencePlatformEngine.mistakeRecords.collectAsState()

  var activeMissionAction by remember { mutableStateOf<PredictiveNextAction?>(null) }
  var selectedClusterForDetail by remember { mutableStateOf<ClusterCapabilitySummary?>(null) }
  var pendingVerificationData by remember {
    mutableStateOf<VerificationModalData?>(null)
  }

  val activeCareer = remember(userProfile.targetCareerId) {
    AegoraRepository.careerRoles.find { it.id == userProfile.targetCareerId }
      ?: AegoraRepository.careerRoles.first()
  }

  // Strongest and limiting clusters
  val strongestCluster = remember(clusterSummaries) {
    clusterSummaries.maxByOrNull { it.score }
  }
  val limitingCluster = remember(clusterSummaries) {
    clusterSummaries.minByOrNull { it.score }
  }

  // Derive dominant Next Move from authoritative state (or fallback to beginner-safe action)
  val nextMoveAction: PredictiveNextAction = remember(todaysMissionV8, predictiveActions) {
    predictiveActions.firstOrNull() ?: PredictiveNextAction(
      id = "mis_next_move_auth",
      title = todaysMissionV8.primaryAction.title.ifBlank { "Investigate suspicious authentication event" },
      category = "Active Defense",
      destinationTag = "live_soc_range",
      urgencyScore = 95,
      primaryReason = "Why: strengthens Incident Triage & clears primary bottleneck",
      reasoningTags = listOf("Active Defense", "Incident Triage", "Transfer Challenge"),
      estimatedMins = 8,
      xpReward = 350,
      telemetryMetric = "Targets Primary Blocker: Correlate Sysmon ID 3 & Suricata Alert"
    )
  }

  // Check if learner has zero evidence
  val isZeroEvidence = clusterSummaries.all { it.score == 0 } && verifiedProofs.isEmpty()

  fun handleMissionCompleted(delta: Int, dim: TwinDimensionV12) {
    val currentDim = cyberTwin60.dimensions[dim]
    if (currentDim != null) {
      val updatedState = (currentDim.currentState + delta).coerceAtMost(100)
      val updatedExplainability = currentDim.copy(
        currentState = updatedState,
        trend = "+$delta% demonstrated recently",
        evidenceCount = currentDim.evidenceCount + 1,
        confidence = (currentDim.confidence + 5).coerceAtMost(99),
        decayRisk = DecayRiskLevel.LOW,
        recentPerformance = "Verified in tactical containment and cross-context telemetry drill."
      )
      val updatedDims = cyberTwin60.dimensions.toMutableMap()
      updatedDims[dim] = updatedExplainability
      val updatedSnapshot = cyberTwin60.copy(
        overallScore = cyberTwin60.overallScore + 35,
        dimensions = updatedDims,
        daysToTargetReadiness = (cyberTwin60.daysToTargetReadiness - 2).coerceAtLeast(1)
      )
      CyberOperatingSystemV12Engine.updateCyberTwin60(updatedSnapshot)
    }
  }

  // Mission Execution Dialog
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

          // Trigger Cinematic Authoritative Verification Sequence
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
        activeMissionAction = null
      }
    )
  }

  // Cluster Detail Dialog
  val currentDetail = selectedClusterForDetail
  if (currentDetail != null) {
    ClusterDetailDialog(
      summary = currentDetail,
      onDismiss = { selectedClusterForDetail = null },
      onLaunchMission = {
        selectedClusterForDetail = null
        activeMissionAction = nextMoveAction
      },
      onViewDeepProfile = {
        selectedClusterForDetail = null
        onNavigateToPersonalIntelligence()
      }
    )
  }

  // Authoritative Cinematic Evidence Verification Modal
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

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_command_center_list")
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {

    // ============================================================
    // 1. WHERE AM I? — OPERATOR HUD & CURRENT STATUS
    // ============================================================
    item {
      OperatorHudSection(
        learnerId = if (isZeroEvidence) "NEW_OPERATOR" else cyberTwin60.learnerId.ifBlank { userProfile.callsign },
        targetRole = cyberTwin60.targetRole,
        readinessScore = if (isZeroEvidence) 0 else cyberTwin60.overallScore,
        daysToTarget = if (isZeroEvidence) 60 else cyberTwin60.daysToTargetReadiness,
        stage = if (isZeroEvidence) "ZERO EVIDENCE" else "INVESTIGATION",
        onOpenProfile = onNavigateToPersonalIntelligence
      )
    }

    // ============================================================
    // 2. ZERO EVIDENCE WELCOME (Honest Empty State)
    // ============================================================
    if (isZeroEvidence) {
      item {
        ZeroEvidenceWelcomeCard(
          onStartFirstMission = {
            activeMissionAction = nextMoveAction
          }
        )
      }
    }

    // ============================================================
    // 3. WHAT SHOULD I DO NEXT? — HERO: "NEXT MOVE"
    // ============================================================
    item {
      NextMoveHeroSection(
        action = nextMoveAction,
        onStart = {
          activeMissionAction = nextMoveAction
        }
      )
    }

    // ============================================================
    // 3B. 3D CYBER TWIN ORBITAL VISUALIZER (Authoritative Spatial Projection)
    // ============================================================
    item {
      CinematicCyberTwinVisualizer(
        skills = AegoraRepository.skillDomains.flatMap { it.skills },
        modifier = Modifier.testTag("home_3d_cyber_twin_visualizer")
      )
    }

    // ============================================================
    // 4. COMPACT CYBER TWIN (4 Cognitive Capability Clusters)
    // ============================================================
    item {
      CompactCyberTwinSection(
        clusterSummaries = clusterSummaries,
        strongestCluster = strongestCluster,
        limitingCluster = limitingCluster,
        onSelectCluster = { cluster ->
          selectedClusterForDetail = cluster
        },
        onOpenFullProfile = onNavigateToPersonalIntelligence
      )
    }

    // ============================================================
    // 5. CYBER TREASURE (Verified Career Capital)
    // ============================================================
    item {
      CyberTreasureSection(
        discoveriesCount = if (isZeroEvidence) 0 else (learningMemory.size + mistakeRecords.size),
        capabilitiesProvenCount = if (isZeroEvidence) 0 else verifiedProofs.size,
        investigationsCount = if (isZeroEvidence) 0 else (if (dailyMission.isCompleted) 4 else 3),
        onOpenVault = onNavigateToPersonalIntelligence
      )
    }

    // ============================================================
    // 6. ZERO → JOB READY (Compact Progression Pipeline)
    // ============================================================
    item {
      ZeroToJobReadySection(
        currentStage = if (isZeroEvidence) "FOUNDATION" else "INVESTIGATION",
        provenCount = if (isZeroEvidence) 0 else verifiedProofs.size,
        totalStageCapabilities = 8,
        nextMilestone = if (isZeroEvidence) "Initial SOC Diagnostic Baseline" else "Cross-Environment Transfer Triage",
        onClick = onNavigateToJourney
      )
    }

    // ============================================================
    // 7. ACTIVE MISSION (In-Flight Operational Mission)
    // ============================================================
    item {
      ActiveMissionSection(
        title = if (isZeroEvidence) "Initial SOC Diagnostic Challenge" else todaysMissionV8.primaryAction.title.ifBlank { "SOC Beacon Investigation" },
        subtitle = if (isZeroEvidence) "7-Gate Baseline Calibration" else todaysMissionV8.primaryAction.subtitle.ifBlank { "Correlate Sysmon ID 3 & Suricata Alert" },
        stepsCompleted = if (isZeroEvidence) 0 else 2,
        totalSteps = 4,
        onContinue = {
          activeMissionAction = nextMoveAction
        }
      )
    }

    // ============================================================
    // 8. PROOF OF SKILL ("PROVEN" Verified Cryptographic Telemetry)
    // ============================================================
    item {
      val latestProof = if (isZeroEvidence) null else verifiedProofs.firstOrNull()
      ProvenSkillSection(
        proof = latestProof,
        onViewProof = onNavigateToPersonalIntelligence
      )
    }

    // ============================================================
    // 9. WHY DOES THIS MATTER? — CAREER SIGNAL
    // ============================================================
    item {
      val provenSkillNames = if (isZeroEvidence) emptyList() else verifiedProofs.map { it.capabilityName }.distinct()
      val remainingCaps = if (isZeroEvidence) activeCareer.primarySkills.size else (activeCareer.primarySkills.size - provenSkillNames.size).coerceAtLeast(0)
      CareerSignalSection(
        targetRole = activeCareer.title,
        remainingCount = remainingCaps,
        demonstratedSkills = provenSkillNames.take(2),
        developingSkill = if (isZeroEvidence) "Awaiting Baseline Telemetry" else if (remainingCaps == 0) "Fully Qualified" else "${activeCareer.primarySkills.firstOrNull { it !in provenSkillNames } ?: "Threat Detection"} (In Progress)",
        onOpenCareers = onNavigateToCareers
      )
    }
  }
}

// ============================================================================
// COMPONENT 1: OPERATOR HUD ("Where am I?")
// ============================================================================
@Composable
private fun OperatorHudSection(
  learnerId: String,
  targetRole: String,
  readinessScore: Int,
  daysToTarget: Int,
  stage: String,
  onOpenProfile: () -> Unit
) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = MaterialTheme.colorScheme.surface,
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("home_operator_identity_hud")
  ) {
    Column(
      modifier = Modifier
        .background(
          Brush.horizontalGradient(
            listOf(
              MaterialTheme.colorScheme.surfaceVariant,
              MaterialTheme.colorScheme.surface
            )
          )
        )
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(CyberCyan.copy(alpha = 0.15f))
              .border(1.2.dp, CyberCyan, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Terminal,
              contentDescription = "Operator Terminal",
              tint = CyberCyan,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "OPERATOR // ${learnerId.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp,
                  fontSize = 10.sp
                ),
                color = CyberCyan
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(3.dp),
                color = CyberEmerald.copy(alpha = 0.15f)
              ) {
                Text(
                  text = stage,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black
                  ),
                  color = CyberEmerald,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Text(
              text = targetRole,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "$readinessScore MMR",
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black
            ),
            color = CyberGold
          )
          Text(
            text = "${daysToTarget}d to Target",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Shortcut to Personal Intelligence Profile
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = CyberCyan.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onOpenProfile() }
          .testTag("btn_hud_open_intelligence")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Personal Intelligence Profile • 4 Clusters & Causal Graph",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.5.sp
              ),
              color = CyberCyan
            )
          }
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View Profile", tint = CyberCyan, modifier = Modifier.size(13.dp))
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 2: HERO "NEXT MOVE" ("What should I do next?")
// ============================================================================
@Composable
private fun NextMoveHeroSection(
  action: PredictiveNextAction,
  onStart: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = AegoraSurface
    ),
    border = BorderStroke(1.dp, AegoraCyanVerified.copy(alpha = 0.7f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("home_next_move_hero")
  ) {
    Column(
      modifier = Modifier
        .background(
          Brush.verticalGradient(
            listOf(
              AegoraCyanVerified.copy(alpha = 0.08f),
              AegoraSurface
            )
          )
        )
        .padding(18.dp)
    ) {
      // Header badge with pulsing indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(AegoraCyanVerified)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "NEXT MOVE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 1.2.sp,
              fontSize = 11.sp
            ),
            color = AegoraCyanVerified
          )
        }

        Surface(
          shape = RoundedCornerShape(4.dp),
          color = AegoraSurfaceElevated,
          border = BorderStroke(1.dp, AegoraBorder)
        ) {
          Text(
            text = "ESTIMATED: ${action.estimatedMins.coerceAtLeast(8)} MIN",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            ),
            color = AegoraTextSecondary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Adaptive Adversary targeted weakness banner if present
      if (action.category.contains("Remediation", ignoreCase = true) || action.primaryReason.contains("Remediation", ignoreCase = true) || action.reasoningTags.any { it.contains("Bias") || it.contains("Correlation") || it.contains("Escalation") || it.contains("Gate") }) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CyberAmber.copy(alpha = 0.12f),
          border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth().testTag("adaptive_adversary_callout")
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = CyberAmber,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "ADAPTIVE AI ADVERSARY • TARGETED CHALLENGE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp,
                  fontSize = 10.sp
                ),
                color = CyberAmber
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "AEGORA identified a reasoning weakness in your previous investigation.",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
              ),
              color = AegoraTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Your next challenge targets: ${action.telemetryMetric.ifBlank { action.title }}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              ),
              color = CyberCyan
            )
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Dominant action title
      Text(
        text = action.title.uppercase(),
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          letterSpacing = 0.2.sp
        ),
        color = AegoraTextPrimary
      )

      Spacer(modifier = Modifier.height(8.dp))

      // WHY THIS MISSION — Up to 3 concise bullet points
      Text(
        text = "WHY THIS MISSION:",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 9.sp,
          fontWeight = FontWeight.Bold
        ),
        color = AegoraTextSecondary
      )
      Spacer(modifier = Modifier.height(4.dp))

      val reasons = remember(action) {
        val list = mutableListOf<String>()
        list.add(action.primaryReason)
        if (action.reasoningTags.isNotEmpty()) {
          list.add("Targeted cognitive domain: ${action.reasoningTags.first()}")
        }
        list.add("Generates server-verified evidence for Skill Passport")
        list.take(3)
      }

      for (r in reasons) {
        Row(
          modifier = Modifier.padding(vertical = 1.dp),
          verticalAlignment = Alignment.Top
        ) {
          Text(
            text = "• ",
            style = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = AegoraCyanVerified
          )
          Text(
            text = r,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.5.sp,
              lineHeight = 16.sp
            ),
            color = AegoraTextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Expected Impact
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "EXPECTED IMPACT: ",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            ),
            color = AegoraTextSecondary
          )
          Text(
            text = "+12% CAPABILITY",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 9.5.sp
            ),
            color = AegoraCyanVerified
          )
        }

        Surface(
          shape = RoundedCornerShape(4.dp),
          color = AegoraSurfaceElevated,
          border = BorderStroke(1.dp, AegoraBorder)
        ) {
          Text(
            text = action.category.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Bold
            ),
            color = AegoraTextSecondary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Dominant Visually Centered Primary CTA
      Button(
        onClick = onStart,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = AegoraCyanVerified,
          contentColor = Color(0xFF0A0E14)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("home_btn_start_next_move")
      ) {
        Icon(
          Icons.Default.PlayArrow,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "EXECUTE MISSION",
          style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp,
            fontSize = 12.sp
          )
        )
      }
    }
  }
}

// ============================================================================
// COMPONENT 3: COMPACT CYBER TWIN (4 Cognitive Clusters)
// ============================================================================
@Composable
private fun CompactCyberTwinSection(
  clusterSummaries: List<ClusterCapabilitySummary>,
  strongestCluster: ClusterCapabilitySummary?,
  limitingCluster: ClusterCapabilitySummary?,
  onSelectCluster: (ClusterCapabilitySummary) -> Unit,
  onOpenFullProfile: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("home_compact_cyber_twin")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "CYBER TWIN",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.8.sp,
              fontSize = 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "4 Cognitive Capability Clusters",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        TextButton(
          onClick = onOpenFullProfile,
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          modifier = Modifier.heightIn(min = 48.dp)
        ) {
          Text(
            text = "Full Profile",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = CyberCyan
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open Profile",
            tint = CyberCyan,
            modifier = Modifier.size(12.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 4 Cluster Grid/List
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (summary in clusterSummaries) {
          val isStrongest = summary.cluster == strongestCluster?.cluster
          val isLimiting = summary.cluster == limitingCluster?.cluster
          ClusterSummaryRow(
            summary = summary,
            isStrongest = isStrongest,
            isLimiting = isLimiting,
            onClick = { onSelectCluster(summary) }
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Diagnostic Footnote
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Tap cluster to view verified evidence & recommended action",
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 9.5.sp
          ),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
private fun ClusterSummaryRow(
  summary: ClusterCapabilitySummary,
  isStrongest: Boolean,
  isLimiting: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    border = BorderStroke(
      1.dp,
      when {
        isLimiting -> CyberCrimson.copy(alpha = 0.5f)
        isStrongest -> CyberEmerald.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
      }
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .padding(horizontal = 12.dp, vertical = 9.dp)
        .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = summary.cluster.displayName,
            style = MaterialTheme.typography.bodySmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          if (isStrongest) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "STRONGEST",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black
              ),
              color = CyberEmerald
            )
          } else if (isLimiting) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "LIMITING",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black
              ),
              color = CyberCrimson
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Score bar
        LinearProgressIndicator(
          progress = { (summary.score / 100f).coerceIn(0f, 1f) },
          modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = when {
            summary.score >= 75 -> CyberEmerald
            summary.score >= 50 -> CyberCyan
            else -> CyberAmber
          },
          trackColor = MaterialTheme.colorScheme.surface
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "${summary.score}%",
          style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
          ),
          color = when {
            summary.score >= 75 -> CyberEmerald
            summary.score >= 50 -> CyberCyan
            else -> CyberAmber
          }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          Icons.Default.ChevronRight,
          contentDescription = "Inspect Cluster",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

// ============================================================================
// COMPONENT 4: "CYBER TREASURE" (Verified Career Capital)
// ============================================================================
@Composable
private fun CyberTreasureSection(
  discoveriesCount: Int,
  capabilitiesProvenCount: Int,
  investigationsCount: Int,
  onOpenVault: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpenVault() }
      .testTag("home_cyber_treasure")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = null, tint = CyberGold, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CYBER TREASURE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.8.sp,
              fontSize = 11.sp
            ),
            color = CyberGold
          )
        }

        Text(
          text = "Verified Career Capital",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TreasureMetricTile(
          count = discoveriesCount,
          label = "Discoveries",
          subLabel = "Verified patterns",
          tint = CyberCyan,
          modifier = Modifier.weight(1f)
        )
        TreasureMetricTile(
          count = capabilitiesProvenCount,
          label = "Proven",
          subLabel = "Cryptographic gates",
          tint = CyberEmerald,
          modifier = Modifier.weight(1f)
        )
        TreasureMetricTile(
          count = investigationsCount,
          label = "Investigations",
          subLabel = "Live telemetry",
          tint = CyberIndigo,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun TreasureMetricTile(
  count: Int,
  label: String,
  subLabel: String,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant,
    border = BorderStroke(1.dp, tint.copy(alpha = 0.25f)),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "$count",
        style = MaterialTheme.typography.titleMedium.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          fontSize = 17.sp
        ),
        color = tint
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 10.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subLabel,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 8.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

// ============================================================================
// COMPONENT 5: ZERO → JOB READY (Compact Progression Pipeline)
// ============================================================================
@Composable
private fun ZeroToJobReadySection(
  currentStage: String,
  provenCount: Int,
  totalStageCapabilities: Int,
  nextMilestone: String,
  onClick: () -> Unit
) {
  val stages = listOf("FOUNDATION", "OPERATIONS", "INVESTIGATION", "SPECIALIZATION", "PROVE IT", "JOB READY")

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("home_zero_to_job_ready")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "ZERO → JOB READY",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp,
            fontSize = 11.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = "$provenCount / $totalStageCapabilities capabilities proven",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold
          ),
          color = CyberCyan
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Compact Progression pipeline row
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        items(stages) { stageName ->
          val isCurrent = stageName == currentStage
          val isPast = stages.indexOf(stageName) < stages.indexOf(currentStage)

          Surface(
            shape = RoundedCornerShape(4.dp),
            color = when {
              isCurrent -> CyberCyan.copy(alpha = 0.2f)
              isPast -> CyberEmerald.copy(alpha = 0.15f)
              else -> MaterialTheme.colorScheme.surfaceVariant
            },
            border = BorderStroke(
              1.dp,
              when {
                isCurrent -> CyberCyan
                isPast -> CyberEmerald.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
              }
            )
          ) {
            Text(
              text = if (isPast) "$stageName ✓" else stageName,
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Normal
              ),
              color = when {
                isCurrent -> CyberCyan
                isPast -> CyberEmerald
                else -> MaterialTheme.colorScheme.onSurfaceVariant
              },
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Next milestone: $nextMilestone",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
          Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = "View Journey",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(13.dp)
        )
      }
    }
  }
}

// ============================================================================
// COMPONENT 6: ACTIVE MISSION
// ============================================================================
@Composable
private fun ActiveMissionSection(
  title: String,
  subtitle: String,
  stepsCompleted: Int,
  totalSteps: Int,
  onContinue: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("home_active_mission")
  ) {
    Row(
      modifier = Modifier
        .padding(14.dp)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "ACTIVE MISSION",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.8.sp,
              fontSize = 10.sp
            ),
            color = CyberCyan
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "$stepsCompleted / $totalSteps steps",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = title,
          style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.5.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      Button(
        onClick = onContinue,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = CyberCyan
        ),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
        modifier = Modifier
          .heightIn(min = 48.dp)
          .testTag("home_btn_continue_active_mission")
      ) {
        Text(
          text = "CONTINUE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black
          )
        )
      }
    }
  }
}

// ============================================================================
// COMPONENT 7: PROOF OF SKILL ("PROVEN")
// ============================================================================
@Composable
private fun ProvenSkillSection(
  proof: EvidenceProofItem?,
  onViewProof: () -> Unit
) {
  val isProven = proof != null
  val title = proof?.capabilityName ?: "Awaiting First Verified Proof"
  val proofType = proof?.proofType ?: "Pending Lab Telemetry"
  val hash = proof?.verifiedHash ?: "SHA-256: Unverified (0 Proofs)"

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onViewProof() }
      .testTag("home_proven_skill")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isProven) Icons.Default.Verified else Icons.Default.HourglassEmpty,
            contentDescription = null,
            tint = if (isProven) CyberEmerald else CyberAmber,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isProven) "PROVEN" else "PENDING",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.8.sp,
              fontSize = 11.sp
            ),
            color = if (isProven) CyberEmerald else CyberAmber
          )
        }

        Text(
          text = if (isProven) "Active Defense" else "Zero Evidence",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = if (isProven) CyberCyan else CyberAmber
          )
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(2.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Evidence: $proofType",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = hash,
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp
          ),
          color = if (isProven) CyberEmerald else CyberAmber
        )
      }
    }
  }
}

// ============================================================================
// COMPONENT 8: CAREER SIGNAL ("Why does this matter?")
// ============================================================================
@Composable
private fun CareerSignalSection(
  targetRole: String,
  remainingCount: Int,
  demonstratedSkills: List<String>,
  developingSkill: String,
  onOpenCareers: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onOpenCareers() }
      .testTag("home_career_signal")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Badge, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(15.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "CAREER SIGNAL",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.8.sp,
              fontSize = 11.sp
            ),
            color = CyberCyan
          )
        }

        Text(
          text = "$remainingCount capabilities remaining",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = CyberAmber
          )
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = targetRole,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (demonstratedSkills.isEmpty()) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.3f))
          ) {
            Text(
              text = "0/${remainingCount} Demonstrated • Complete initial diagnostic",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
              ),
              color = CyberAmber,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        } else {
          demonstratedSkills.forEach { skill ->
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberEmerald.copy(alpha = 0.15f)
            ) {
              Text(
                text = "$skill ✓",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Surface(
          shape = RoundedCornerShape(4.dp),
          color = CyberAmber.copy(alpha = 0.15f)
        ) {
          Text(
            text = developingSkill,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            ),
            color = CyberAmber,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

// ============================================================================
// COMPONENT 9: ZERO EVIDENCE WELCOME CARD
// ============================================================================
@Composable
private fun ZeroEvidenceWelcomeCard(
  onStartFirstMission: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.5.dp, CyberEmerald),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("home_empty_state")
  ) {
    Column(
      modifier = Modifier
        .background(
          Brush.verticalGradient(
            listOf(
              CyberEmerald.copy(alpha = 0.1f),
              MaterialTheme.colorScheme.surface
            )
          )
        )
        .padding(16.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Explore, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "YOUR CAPABILITY MAP STARTS HERE",
          style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 11.5.sp
          ),
          color = CyberEmerald
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "You don't need cybersecurity experience. AEGORA maps your verified capabilities as you complete live telemetry investigations.",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(10.dp))

      Button(
        onClick = onStartFirstMission,
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = CyberEmerald,
          contentColor = Color.Black
        ),
        modifier = Modifier
          .heightIn(min = 48.dp)
          .testTag("home_btn_start_from_zero")
      ) {
        Text(
          text = "START FROM ZERO: PROTOCOL PACKET ANALYSIS",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black
          )
        )
      }
    }
  }
}

// ============================================================================
// COMPONENT 10: CLUSTER DETAIL DIALOG
// ============================================================================
@Composable
private fun ClusterDetailDialog(
  summary: ClusterCapabilitySummary,
  onDismiss: () -> Unit,
  onLaunchMission: () -> Unit,
  onViewDeepProfile: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = MaterialTheme.colorScheme.surface,
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = summary.cluster.displayName.uppercase(),
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
              ),
              color = CyberCyan
            )
            Text(
              text = summary.trendLabel,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Text(
            text = "${summary.score}%",
            style = MaterialTheme.typography.titleLarge.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black
            ),
            color = if (summary.score >= 75) CyberEmerald else CyberCyan
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = summary.plainEnglishMeaning,
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Recent Evidence
        val proof = summary.recentEvidence.firstOrNull()
        if (proof != null) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text(
                text = "LATEST VERIFIED EVIDENCE:",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberEmerald
              )
              Text(
                text = proof.telemetrySnippet,
                style = MaterialTheme.typography.bodySmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
        }

        // Recommended Action
        Text(
          text = "RECOMMENDED ACTION:",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          ),
          color = CyberGold
        )
        Text(
          text = summary.topGrowthMissions.firstOrNull() ?: "Complete tactical verification mission",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onViewDeepProfile,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Text(
              text = "CAUSAL GRAPH",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              )
            )
          }

          Button(
            onClick = onLaunchMission,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = CyberCyan,
              contentColor = Color.Black
            ),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 48.dp)
          ) {
            Text(
              text = "PROVE IT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
              )
            )
          }
        }
      }
    }
  }
}
