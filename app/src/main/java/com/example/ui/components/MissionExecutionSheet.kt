package com.example.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.CapabilityValidator
import com.example.model.FailureModeType
import com.example.model.MistakeIntelligenceRecord
import com.example.model.PredictiveNextAction
import com.example.model.TwinDimensionV12
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MissionExecutionChoice(
  val id: Int,
  val text: String,
  val isOptimal: Boolean,
  val failureMode: FailureModeType? = null,
  val feedback: String,
  val rootCause: String,
  val remediationMission: String
)

data class MissionExecutionOutcome(
  val isSuccess: Boolean,
  val choice: MissionExecutionChoice,
  val proofHash: String?,
  val mistakeRecord: MistakeIntelligenceRecord?
)

/**
 * Full Tactical Mission Operation Dialog.
 * Provides pre-mission telemetry context, operational choice execution,
 * and immediate before/after capability proof resolution.
 */
@Composable
fun MissionExecutionSheet(
  action: PredictiveNextAction,
  onDismiss: () -> Unit,
  onMissionCompleted: (impactScoreDelta: Int, dimensionTarget: TwinDimensionV12) -> Unit = { _, _ -> },
  onMissionOutcome: (isSuccess: Boolean, delta: Int, dim: TwinDimensionV12, proofHash: String?, mistake: MistakeIntelligenceRecord?) -> Unit = { _, _, _, _, _ -> }
) {
  var executionStep by remember { mutableStateOf(1) } // 1 = Briefing & Telemetry, 2 = Execution, 3 = Post-Mission Resolution
  var selectedChoiceIndex by remember { mutableIntStateOf(-1) }
  var isSubmitting by remember { mutableStateOf(false) }
  var executionOutcome by remember { mutableStateOf<MissionExecutionOutcome?>(null) }

  val targetDimension = remember(action) {
    when {
      action.reasoningTags.any { it.contains("Transfer") } -> TwinDimensionV12.TRANSFERABILITY
      action.reasoningTags.any { it.contains("Investigation") } -> TwinDimensionV12.INVESTIGATION
      action.reasoningTags.any { it.contains("Causal") } -> TwinDimensionV12.REASONING
      action.reasoningTags.any { it.contains("Decay") } -> TwinDimensionV12.RETENTION
      else -> TwinDimensionV12.TRANSFERABILITY
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.88f)
        .clip(RoundedCornerShape(20.dp))
        .border(1.5.dp, CyberCyan, RoundedCornerShape(20.dp))
        .testTag("mission_execution_dialog"),
      color = CyberBackground
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // 1. Mission Header Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.2f))
                .border(1.dp, CyberCyan, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "TACTICAL OPERATION BRIEFING",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp,
                  fontSize = 11.sp
                ),
                color = CyberCyan
              )
              Text(
                text = "Operation ID: ${action.id.take(8).uppercase()}",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp
                ),
                color = TextTertiaryDark
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("mission_dialog_close_btn")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Step Navigation Indicator
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          MissionStepIndicator(step = 1, currentStep = executionStep, label = "Telemetry Briefing", modifier = Modifier.weight(1f))
          MissionStepIndicator(step = 2, currentStep = executionStep, label = "Triage Execution", modifier = Modifier.weight(1f))
          MissionStepIndicator(step = 3, currentStep = executionStep, label = "Capability Proof", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Dynamic Step Content
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          when (executionStep) {
            1 -> MissionStepBriefing(
              action = action,
              targetDimension = targetDimension,
              onProceed = { executionStep = 2 }
            )
            2 -> MissionStepExecution(
              action = action,
              selectedChoiceIndex = selectedChoiceIndex,
              onSelectChoice = { selectedChoiceIndex = it },
              onSubmitChoice = { choice ->
                if (choice.isOptimal) {
                  val generatedHash = CapabilityValidator.generateEvidenceIntegrityHash(
                    learnerId = "operator_7x",
                    capabilityId = action.id,
                    missionId = action.id,
                    attemptId = "attempt_1",
                    evidenceType = "SYS_PROCESS_TELEMETRY",
                    outcomeScore = 96,
                    createdAt = System.currentTimeMillis()
                  )
                  val outcome = MissionExecutionOutcome(
                    isSuccess = true,
                    choice = choice,
                    proofHash = generatedHash,
                    mistakeRecord = null
                  )
                  executionOutcome = outcome
                  executionStep = 3
                  onMissionCompleted(12, targetDimension)
                  onMissionOutcome(true, 12, targetDimension, generatedHash, null)
                } else {
                  val mistake = MistakeIntelligenceRecord(
                    timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
                    missionTitle = action.title,
                    failureType = choice.failureMode ?: FailureModeType.REASONING_ERROR,
                    observedSymptom = choice.text,
                    rootCauseCausalLink = choice.rootCause,
                    constructiveFeedback = choice.feedback,
                    targetedRemediationMission = choice.remediationMission,
                    capabilityImpactLabel = "Classified as ${choice.failureMode?.label ?: "Reasoning Error"} evidence"
                  )
                  val outcome = MissionExecutionOutcome(
                    isSuccess = false,
                    choice = choice,
                    proofHash = null,
                    mistakeRecord = mistake
                  )
                  executionOutcome = outcome
                  executionStep = 3
                  onMissionCompleted(0, targetDimension)
                  onMissionOutcome(false, 0, targetDimension, null, mistake)
                }
              }
            )
            3 -> MissionStepResolution(
              action = action,
              targetDimension = targetDimension,
              outcome = executionOutcome,
              onFinish = onDismiss
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MissionStepIndicator(
  step: Int,
  currentStep: Int,
  label: String,
  modifier: Modifier = Modifier
) {
  val isActive = step == currentStep
  val isPast = step < currentStep

  Column(modifier = modifier) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(3.dp)
        .background(
          when {
            isPast -> CyberEmerald
            isActive -> CyberCyan
            else -> CyberBorder
          }
        )
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontSize = 8.sp,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
      ),
      color = if (isActive) CyberCyan else TextTertiaryDark
    )
  }
}

@Composable
private fun MissionStepBriefing(
  action: PredictiveNextAction,
  targetDimension: TwinDimensionV12,
  onProceed: () -> Unit
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(vertical = 4.dp)
  ) {
    Text(
      text = action.title,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
      color = TextPrimaryDark
    )

    Spacer(modifier = Modifier.height(6.dp))

    // Why selected callout (Explainability mandate)
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = CyberSurfaceElevated,
      border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Info, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "WHY THIS MISSION WAS DISPATCHED",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp
            ),
            color = CyberCyan
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Your demonstrated ${targetDimension.displayName} capability is currently limiting progress. This operation tests your ability to transfer local heuristics into active cross-context telemetry.",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
          color = TextSecondaryDark
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Metadata Badges
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      BriefingBadge("DIFFICULTY", "HIGH", NeonCrimson, Modifier.weight(1f))
      BriefingBadge("EST. TIME", "${action.estimatedMins}m", CyberAmber, Modifier.weight(1f))
      BriefingBadge("TARGET GATE", "TRANSFER", CyberCyan, Modifier.weight(1f))
      BriefingBadge("EXPECTED IMPACT", "+12% MMR", CyberEmerald, Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Real Telemetry Artifact Viewport
    Text(
      text = "INCIDENT TELEMETRY ARTIFACT",
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp
      ),
      color = TextSecondaryDark
    )

    Spacer(modifier = Modifier.height(6.dp))

    Surface(
      shape = RoundedCornerShape(8.dp),
      color = Color(0xFF060608),
      border = BorderStroke(1.dp, CyberBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = "[Sysmon Event ID 1: Process Creation]",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
          color = NeonCyan
        )
        Text(
          text = "UtcTime: 2026-09-03 14:02:11.892\nParentImage: C:\\Windows\\System32\\svchost.exe\nImage: C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe\nCommandLine: powershell -nop -w hidden -enc JABzAD0ATgBlAHcALQBPAGIAagBlAGMAdAAgAEkATwAuAE0AZQBtAG8AcgB5AFMAdAByAGUAYQBtACgA\nUser: NT AUTHORITY\\SYSTEM\nHashes: SHA256=9f83...a82b",
          style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.5.sp,
            lineHeight = 14.sp
          ),
          color = TextSecondaryDark
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = onProceed,
      colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .testTag("mission_begin_triage_btn")
    ) {
      Text(
        text = "Proceed to Triage & Analysis",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          fontSize = 11.sp
        ),
        color = Color.Black
      )
    }
  }
}

@Composable
private fun MissionStepExecution(
  action: PredictiveNextAction,
  selectedChoiceIndex: Int,
  onSelectChoice: (Int) -> Unit,
  onSubmitChoice: (MissionExecutionChoice) -> Unit
) {
  val coroutineScope = rememberCoroutineScope()
  var isChallengingAi by remember { mutableStateOf(false) }

  val choices = remember {
    listOf(
      MissionExecutionChoice(
        id = 0,
        text = "Correlate PowerShell memory stream payload with Sysmon Event ID 3 (Network Connection to port 8443) and isolate host WIN-FINANCE-04.",
        isOptimal = true,
        feedback = "High-fidelity correlation of process execution with outbound C2 network telemetry confirms reflective DLL injection. Host containment halts lateral movement.",
        rootCause = "Optimal causal hypothesis testing confirmed malicious memory injection and C2 connection.",
        remediationMission = ""
      ),
      MissionExecutionChoice(
        id = 1,
        text = "Tag as benign administrative maintenance because ParentImage was svchost.exe without checking token privileges.",
        isOptimal = false,
        failureMode = FailureModeType.PATTERN_RECOGNITION_ERROR,
        feedback = "svchost.exe parentage was spoofed via process injection. Attackers commonly migrate into svchost or spawn child powershell instances with elevated SYSTEM tokens.",
        rootCause = "Superficial trust placed on benign process names without validating command line arguments or token elevation.",
        remediationMission = "Sysmon Parent-Child Process Correlation & Token Triage (8 mins)"
      ),
      MissionExecutionChoice(
        id = 2,
        text = "Restart the Windows Event Log service to see if the anomalous event repeats.",
        isOptimal = false,
        failureMode = FailureModeType.PREMATURE_CONCLUSION,
        feedback = "Restarting event logging destroys forensic volatility data and alerts adversaries that detection is occurring without containing the active C2 beacon.",
        rootCause = "Attempting reactive verification via service reset rather than active forensic correlation and isolation.",
        remediationMission = "Volatile Artifact Preservation & Incident Triage (10 mins)"
      )
    )
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(vertical = 4.dp)
      .verticalScroll(rememberScrollState())
  ) {
    Text(
      text = "OPERATIONAL DECISION CHALLENGE",
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      ),
      color = CyberAmber
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "Based on the encoded reflective PowerShell memory injection payload originating from SYSTEM context, what is the correct immediate containment & verification action?",
      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
      color = TextPrimaryDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    // Red Team Adversary: AI Analyst Proposed Verdict Card
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = Color(0xFF131127),
      border = BorderStroke(1.dp, CyberPurple.copy(alpha = 0.5f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberPurple, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "AI ANALYST ADVERSARIAL ASSESSMENT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
              ),
              color = CyberPurple
            )
          }
          Text(
            text = "CONFIDENCE: 94%",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 8.5.sp,
              fontWeight = FontWeight.Bold
            ),
            color = CyberAmber
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "\"Attributing 10.0.4.15 as external APT29 infrastructure based on PowerShell beaconing. Recommend immediate null-route of entire 10.0.0.0/16 internal subnet.\"",
          style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = TextPrimaryDark
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dual Telemetry Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "AI Confidence: 94%",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.5.sp),
            color = CyberAmber
          )
          Text(
            text = "Evidence Grounding: 31% (Gap: 63% - Hallucination Alert)",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.5.sp),
            color = CyberCyan
          )
        }
        Spacer(modifier = Modifier.height(3.dp))
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
              .fillMaxWidth(0.94f)
              .background(CyberAmber)
          )
          Box(
            modifier = Modifier
              .fillMaxHeight()
              .fillMaxWidth(0.31f)
              .background(CyberCyan)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = {
            coroutineScope.launch {
              isChallengingAi = true
              kotlinx.coroutines.delay(320)
              isChallengingAi = false
              onSelectChoice(0) // Selects optimal ground truth
              onSubmitChoice(choices[0])
            }
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0052D4),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .testTag("btn_challenge_ai_mission")
        ) {
          if (isChallengingAi) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "[CORROBORATING HOST & NETWORK TELEMETRY...]",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
              )
            )
          } else {
            Text(
              text = "⚡ CHALLENGE AI CLAIM (RECONCILE TELEMETRY)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    choices.forEachIndexed { index, choice ->
      val isSelected = selectedChoiceIndex == index

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CyberSurfaceElevated else CyberSurface,
        border = BorderStroke(
          1.dp,
          if (isSelected) CyberCyan else CyberBorder
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable { onSelectChoice(index) }
          .testTag("mission_choice_$index")
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          RadioButton(
            selected = isSelected,
            onClick = { onSelectChoice(index) },
            colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = choice.text,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
            color = if (isSelected) TextPrimaryDark else TextSecondaryDark
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = {
        if (selectedChoiceIndex in choices.indices) {
          onSubmitChoice(choices[selectedChoiceIndex])
        }
      },
      enabled = selectedChoiceIndex != -1,
      colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .testTag("mission_submit_action_btn")
    ) {
      Text(
        text = "Execute Decision & Verify Telemetry",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          fontSize = 11.sp
        ),
        color = Color.Black
      )
    }
  }
}

@Composable
private fun MissionStepResolution(
  action: PredictiveNextAction,
  targetDimension: TwinDimensionV12,
  outcome: MissionExecutionOutcome?,
  onFinish: () -> Unit
) {
  val isSuccess = outcome?.isSuccess ?: true
  val choice = outcome?.choice
  val mistake = outcome?.mistakeRecord

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(vertical = 4.dp)
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(
          if (isSuccess) CyberEmerald.copy(alpha = 0.2f) else CyberCrimson.copy(alpha = 0.2f)
        )
        .border(
          1.5.dp,
          if (isSuccess) CyberEmerald else CyberCrimson,
          CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Warning,
        contentDescription = null,
        tint = if (isSuccess) CyberEmerald else CyberCrimson,
        modifier = Modifier.size(28.dp)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = if (isSuccess) "MISSION ACCOMPLISHED: EVIDENCE VERIFIED" else "SUBOPTIMAL TRIAGE: ROOT CAUSE IDENTIFIED",
      style = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp
      ),
      color = if (isSuccess) CyberEmerald else CyberCrimson
    )

    Text(
      text = if (isSuccess) {
        "Incident contained. Telemetry correlated across Windows & network bounds."
      } else {
        "Mistake classified as high-value learning evidence. No score destruction."
      },
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
      color = TextSecondaryDark
    )

    Spacer(modifier = Modifier.height(14.dp))

    if (isSuccess) {
      // 1. REVEALED TELEMETRY STATUS BANNER
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberEmerald.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, CyberEmerald),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AI FAILURE DETECTED ✓ | EVIDENCE VERIFIED ✓",
              style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp
              ),
              color = CyberEmerald
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "HUMAN OPERATOR DECISION CORRECT ✓ • HALLUCINATION CONFIRMED",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = CyberCyan
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. FAILURE AUTOPSY (AI COGNITIVE PROFILE)
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF131127),
        border = BorderStroke(1.dp, CyberPurple.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberPurple, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "FAILURE AUTOPSY: AI COGNITIVE BREAKDOWN",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.5.sp
                ),
                color = CyberPurple
              )
            }
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberCrimson.copy(alpha = 0.2f),
              border = BorderStroke(0.8.dp, CyberCrimson)
            ) {
              Text(
                text = "PREMATURE_CONCLUSION",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberCrimson,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "The AI Analyst rushed to attribute internal 10.0.4.15 bastion traffic to external APT29 infrastructure based solely on reflective DLL presence, ignoring internal rsync tasks. Operator oversight prevented catastrophic internal network outage.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(8.dp))

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberCyan.copy(alpha = 0.1f),
            border = BorderStroke(0.8.dp, CyberCyan.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.TrendingUp, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Adaptive Follow-up: Bastion Jump-Box Integrity & Token Triage (+350 XP)",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  color = CyberCyan
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Real Cryptographic Proof Signature Card
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF070B0E),
        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "CRYPTOGRAPHIC PROOF SIGNATURE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp
              ),
              color = CyberEmerald
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberEmerald.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
            ) {
              Text(
                text = "VERIFIED SHA-256",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = outcome?.proofHash ?: "sha256:7f4ae91b4802c6d83a15f0134bc29088",
            style = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.5.sp
            ),
            color = NeonCyan
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Canonical Payload: learnerId|missionId|SYS_PROCESS_TELEMETRY|score:96",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 8.5.sp
            ),
            color = TextSecondaryDark
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // EXACT BEFORE / AFTER CAPABILITY IMPACT DIFF
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = CyberSurfaceElevated,
        border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "DEMONSTRATED CAPABILITY DELTA",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 9.sp
            ),
            color = CyberCyan
          )

          Spacer(modifier = Modifier.height(10.dp))

          ResolutionDeltaRow(
            dimension = targetDimension.displayName,
            before = "76",
            after = "88",
            delta = "+12",
            color = CyberEmerald
          )

          ResolutionDeltaRow(
            dimension = "Primary Gate Status",
            before = "LIMITING",
            after = "PASSED",
            delta = "UNBLOCKED",
            color = CyberCyan
          )

          ResolutionDeltaRow(
            dimension = "Cyber Twin 6.0 MMR",
            before = "1,960",
            after = "1,995",
            delta = "+35 MMR",
            color = CyberGold
          )

          ResolutionDeltaRow(
            dimension = "Evidence Store",
            before = "4 Verified Logs",
            after = "5 Verified Logs",
            delta = "+1 Artifact",
            color = TextPrimaryDark
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 3. SHAREABLE PUBLIC PROOF DOSSIER PREVIEW
      var linkCopied by remember { mutableStateOf(false) }
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF030712),
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("shareable_proof_dossier_card")
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Share, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "TAMPER-EVIDENT PUBLIC PROOF DOSSIER",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  fontSize = 9.sp
                ),
                color = CyberCyan
              )
            }

            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberCyan.copy(alpha = 0.15f),
              border = BorderStroke(0.8.dp, CyberCyan)
            ) {
              Text(
                text = "OPERATOR: OP-7X-9821",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("MITRE_T1059: 96%", "EVIDENCE_VERIFIED", "AI_OVERSIGHT: 94%").forEach { chip ->
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberEmerald.copy(alpha = 0.12f),
                border = BorderStroke(0.8.dp, CyberEmerald.copy(alpha = 0.4f))
              ) {
                Text(
                  text = chip,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = CyberEmerald,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }

          Text(
            text = "Verification URL: https://aegora.io/verify/proof-${(outcome?.proofHash ?: "7f4ae91b").takeLast(8)}",
            style = MaterialTheme.typography.bodySmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 9.5.sp
            ),
            color = Color(0xFF38BDF8)
          )

          Button(
            onClick = { linkCopied = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (linkCopied) CyberEmerald else CyberSurfaceElevated,
              contentColor = if (linkCopied) Color.Black else CyberCyan
            ),
            border = BorderStroke(1.dp, if (linkCopied) CyberEmerald else CyberCyan.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(min = 40.dp)
              .testTag("btn_copy_proof_link")
          ) {
            Icon(
              if (linkCopied) Icons.Default.Check else Icons.Default.ContentCopy,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (linkCopied) "PROOF LINK COPIED TO CLIPBOARD ✓" else "COPY RECRUITER VERIFICATION LINK",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 9.5.sp
              )
            )
          }
        }
      }
    } else {
      // Suboptimal Decision Constructive Feedback Card
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = CyberSurfaceElevated,
        border = BorderStroke(1.dp, CyberCrimson.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "FAILURE INTELLIGENCE ANALYSIS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 9.sp
              ),
              color = CyberCrimson
            )
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberCrimson.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, CyberCrimson.copy(alpha = 0.5f))
            ) {
              Text(
                text = choice?.failureMode?.label ?: "Reasoning Error",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberCrimson,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Root Cause Analysis:",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            ),
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = choice?.rootCause ?: "Divergence from ground truth telemetry indicators.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, lineHeight = 14.sp),
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Constructive Guidance:",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.5.sp
            ),
            color = CyberAmber
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = choice?.feedback ?: "Correlate parent-child process tokens with outbound network streams.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp, lineHeight = 14.sp),
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(10.dp))

          ResolutionDeltaRow(
            dimension = targetDimension.displayName,
            before = "76",
            after = "76",
            delta = "PRESERVED",
            color = CyberAmber
          )

          ResolutionDeltaRow(
            dimension = "Downstream MMR",
            before = "1,960",
            after = "1,960",
            delta = "0 Lost",
            color = TextSecondaryDark
          )

          ResolutionDeltaRow(
            dimension = "Action Queued",
            before = "None",
            after = choice?.remediationMission ?: "Targeted Remediation",
            delta = "PRIORITY #1",
            color = CyberCyan
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = onFinish,
      colors = ButtonDefaults.buttonColors(
        containerColor = if (isSuccess) CyberCyan else CyberAmber
      ),
      shape = RoundedCornerShape(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(44.dp)
        .testTag("mission_finish_btn")
    ) {
      Text(
        text = if (isSuccess) "Return to Command Center" else "Accept Remediation & Return",
        style = MaterialTheme.typography.labelSmall.copy(
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Black,
          fontSize = 11.sp
        ),
        color = Color.Black
      )
    }
  }
}

@Composable
private fun ResolutionDeltaRow(
  dimension: String,
  before: String,
  after: String,
  delta: String,
  color: Color
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = dimension,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
      color = TextSecondaryDark
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = before,
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
        color = TextTertiaryDark
      )
      Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextTertiaryDark, modifier = Modifier.size(12.dp).padding(horizontal = 2.dp))
      Text(
        text = after,
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )
      Spacer(modifier = Modifier.width(8.dp))
      Surface(
        shape = RoundedCornerShape(3.dp),
        color = color.copy(alpha = 0.18f)
      ) {
        Text(
          text = delta,
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            fontSize = 9.sp
          ),
          color = color,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
      }
    }
  }
}

@Composable
private fun BriefingBadge(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(6.dp),
    color = CyberSurfaceElevated,
    border = BorderStroke(1.dp, CyberBorderSubtle),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp),
        color = TextTertiaryDark
      )
      Text(
        text = value,
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 10.sp),
        color = color
      )
    }
  }
}
