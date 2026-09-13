package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.capability.AdaptiveAdversaryChallengeManager
import com.example.capability.AuthoritativeAdaptiveEvaluationResult
import com.example.capability.LearnerSafeAdaptiveChallenge
import com.example.model.*
import com.example.platform.CrossPlatformMissionBridge
import com.example.subscription.AegoraSubscriptionRepository
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Flagship AI Hallucination & Human Verification Component.
 * Presents AI Analyst claims, allows human operators to audit supplied evidence,
 * and submit ACCEPT or CHALLENGE decisions to authoritative verification.
 */
@Composable
fun AiHallucinationVerificationCard(
  claim: AiAnalystClaim,
  availableEvidence: List<LogEvent>,
  missionId: String = "lab_suspicious_login",
  learnerUid: String = "learner_local_operator",
  onVerificationComplete: (ClientSafeAiVerificationResult) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val selectedEvidenceIds = remember { mutableStateListOf<String>() }
  var verificationResult by remember { mutableStateOf<ClientSafeAiVerificationResult?>(null) }
  var isEvaluating by remember { mutableStateOf(false) }

  // Adaptive Adversary Challenge State
  val adaptiveManager = remember { AdaptiveAdversaryChallengeManager() }
  var adaptiveChallenge by remember { mutableStateOf<LearnerSafeAdaptiveChallenge?>(null) }
  var isAdaptiveChallengeOpen by remember { mutableStateOf(false) }
  var selectedAdaptiveActionId by remember { mutableStateOf("") }
  val selectedAdaptiveEvidenceIds = remember { mutableStateListOf<String>() }
  var adaptiveReasoning by remember { mutableStateOf("") }
  var adaptiveEvaluationResult by remember { mutableStateOf<AuthoritativeAdaptiveEvaluationResult?>(null) }

  CyberCard(
    modifier = modifier.testTag("ai_hallucination_verification_card"),
    borderColor = if (verificationResult?.isAiFailureDetected == true) CyberEmerald else CyberAmber.copy(alpha = 0.6f)
  ) {
    // 1. Header Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = CyberAmber.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, CyberAmber)
        ) {
          Text(
            text = "AI CO-PILOT CLAIM",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              fontSize = 10.sp
            ),
            color = CyberAmber,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = claim.analystName,
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }

      Surface(
        shape = RoundedCornerShape(4.dp),
        color = CyberSurfaceElevated,
        border = BorderStroke(1.dp, CyberBorder)
      ) {
        Text(
          text = "${claim.confidenceScore}% CONFIDENCE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
          ),
          color = CyberCyan,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. AI Claim Statement & IOCs
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = Color(0xFF0A0D14),
      border = BorderStroke(1.dp, CyberBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = "\"${claim.claimText}\"",
          style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            lineHeight = 18.sp
          ),
          color = TextPrimaryDark,
          modifier = Modifier.testTag("ai_claim_text")
        )

        if (claim.assertedIocs.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            claim.assertedIocs.forEach { ioc ->
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberSurfaceElevated,
                border = BorderStroke(0.5.dp, CyberBorder)
              ) {
                Text(
                  text = "IOC: $ioc",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                  ),
                  color = CyberCyan,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Recommended Action from AI
    Text(
      text = "Recommended SOC Action: ${claim.recommendedAction}",
      style = MaterialTheme.typography.bodySmall,
      color = TextSecondaryDark
    )

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Evidence Checklist for Verification
    Text(
      text = "SELECT SUPPORTING / CONTRADICTORY TELEMETRY:",
      style = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      ),
      color = CyberCyan
    )

    Spacer(modifier = Modifier.height(6.dp))

    availableEvidence.forEach { event ->
      val isSelected = selectedEvidenceIds.contains(event.eventId)
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSelected) CyberSurfaceElevated else CyberSurface,
        border = BorderStroke(1.dp, if (isSelected) CyberCyan else CyberBorder),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp)
          .clickable {
            if (isSelected) {
              selectedEvidenceIds.remove(event.eventId)
            } else {
              selectedEvidenceIds.add(event.eventId)
            }
          }
          .testTag("evidence_checkbox_${event.eventId}")
      ) {
        Row(
          modifier = Modifier.padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = isSelected,
            onCheckedChange = { checked ->
              if (checked) selectedEvidenceIds.add(event.eventId) else selectedEvidenceIds.remove(event.eventId)
            },
            colors = CheckboxDefaults.colors(checkedColor = CyberCyan)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Column {
            Text(
              text = "${event.source} [${event.eventId}] - ${event.timestamp}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp
              ),
              color = if (event.isMalicious) CyberRed else CyberCyan
            )
            Text(
              text = event.details,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
              color = TextPrimaryDark,
              maxLines = 1
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 4. Accept vs Challenge Buttons
    val result = verificationResult
    if (result == null) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val coroutineScope = rememberCoroutineScope()
        var evaluationStatusText by remember { mutableStateOf("") }
        var showUpgradePrompt by remember { mutableStateOf(false) }

        if (showUpgradePrompt) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberAmber.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, CyberAmber),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "PRO CLEARANCE REQUIRED",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  ),
                  color = CyberAmber
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "You have used all 3 free AI Reality Check missions. Upgrade to PRO for unlimited adversarial challenges.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = AegoraTextPrimary
              )
            }
          }
        }

        if (isEvaluating) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberCyan.copy(alpha = 0.1f),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = CyberCyan,
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = evaluationStatusText.ifEmpty { "[CORROBORATING HOST & NETWORK TELEMETRY...]" },
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp
                ),
                color = CyberCyan
              )
            }
          }
        } else {
          // Accept AI Button
          OutlinedButton(
            onClick = {
              coroutineScope.launch {
                isEvaluating = true
                evaluationStatusText = "[AUDITING EVIDENCE REPOSITORY...]"
                delay(320)
                val outcome = CrossPlatformMissionBridge.evaluateAiClaimDecision(
                  attemptId = "att_${System.currentTimeMillis()}",
                  missionId = missionId,
                  claimId = claim.claimId,
                  learnerDecision = LearnerAiDecision.ACCEPT_AI,
                  selectedEvidenceIds = selectedEvidenceIds.toList()
                )
                verificationResult = outcome
                isEvaluating = false
                onVerificationComplete(outcome)
              }
            },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, CyberBorder),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("btn_accept_ai_claim")
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "ACCEPT AI",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp
              ),
              color = TextPrimaryDark
            )
          }

          // Challenge AI Button
          Button(
            onClick = {
              if (!AegoraSubscriptionRepository.canAccessAiRealityCheck()) {
                showUpgradePrompt = true
                return@Button
              }
              showUpgradePrompt = false
              coroutineScope.launch {
                isEvaluating = true
                evaluationStatusText = "[CORROBORATING HOST & NETWORK TELEMETRY...]"
                delay(320)
                val outcome = CrossPlatformMissionBridge.evaluateAiClaimDecision(
                  attemptId = "att_${System.currentTimeMillis()}",
                  missionId = missionId,
                  claimId = claim.claimId,
                  learnerDecision = LearnerAiDecision.CHALLENGE_AI,
                  selectedEvidenceIds = selectedEvidenceIds.toList()
                )
                AegoraSubscriptionRepository.recordAiRealityMissionCompleted()
                verificationResult = outcome
                isEvaluating = false
                onVerificationComplete(outcome)
              }
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
            modifier = Modifier
              .weight(1f)
              .height(44.dp)
              .testTag("btn_challenge_ai_claim")
          ) {
            Icon(Icons.Default.Gavel, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "CHALLENGE AI",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 10.5.sp
              ),
              color = Color.Black
            )
          }
        }
      }
    } else {
      // 5. Authoritative Verification Result Card
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (result.isAiFailureDetected) CyberEmerald.copy(alpha = 0.15f) else CyberRed.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, if (result.isAiFailureDetected) CyberEmerald else CyberRed),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("verification_result_banner")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            val headlineText = if (result.isAiFailureDetected) {
              "AI FAILURE DETECTED ✓ | EVIDENCE VERIFIED ✓ | HUMAN DECISION CORRECT ✓"
            } else {
              result.headline
            }
            Text(
              text = headlineText,
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 11.5.sp
              ),
              color = if (result.isAiFailureDetected) CyberEmerald else CyberRed,
              modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (result.isAiFailureDetected) CyberEmerald.copy(alpha = 0.2f) else CyberRed.copy(alpha = 0.2f)
            ) {
              Text(
                text = if (result.evidenceVerified) "Evidence Verified ✓" else "AUTHORITATIVE",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = if (result.isAiFailureDetected) CyberEmerald else CyberRed,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = result.explanation,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
            color = TextPrimaryDark
          )

          // KILLER MOMENT: Verified Capability Artifact
          if (result.isAiFailureDetected) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberEmerald.copy(alpha = 0.12f),
              border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.6f)),
              modifier = Modifier.fillMaxWidth().testTag("verified_capability_box")
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "VERIFIED CAPABILITY: AI Hallucination & Telemetry Triage",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp
                    ),
                    color = CyberEmerald
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Demonstrated independence from misleading co-pilot attribution by grounding triage in authoritative raw Event ID 4624/4625 logs.",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, lineHeight = 14.sp),
                  color = TextSecondaryDark
                )
              }
            }
          }

          // FAILURE AUTOPSY: 7 Mandatory Elements
          result.failureAutopsy?.let { autopsy ->
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurfaceElevated,
              border = BorderStroke(1.dp, CyberRed.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth().testTag("failure_autopsy_card")
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "FAILURE AUTOPSY // CANONICAL REASONING BREAKDOWN",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 9.5.sp
                  ),
                  color = CyberRed
                )
                Spacer(modifier = Modifier.height(6.dp))

                // 1. Your Decision
                Text(
                  text = "1. YOUR DECISION: ${autopsy.yourDecision}",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp),
                  color = TextPrimaryDark
                )
                // 2. AI Claim
                Text(
                  text = "2. AI CLAIM: \"${autopsy.aiClaim}\"",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, fontFamily = FontFamily.Monospace),
                  color = TextSecondaryDark
                )
                // 3. Evidence You Used
                Text(
                  text = "3. EVIDENCE YOU USED: ${if (autopsy.evidenceYouUsed.isEmpty()) "None selected" else autopsy.evidenceYouUsed.joinToString()}",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, fontFamily = FontFamily.Monospace),
                  color = CyberCyan
                )
                // 4. Evidence That Mattered
                Text(
                  text = "4. EVIDENCE THAT MATTERED: ${autopsy.evidenceThatMattered.joinToString()}",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberEmerald
                )
                // 5. What Went Wrong
                Text(
                  text = "5. WHAT WENT WRONG: ${autopsy.whatWentWrong}",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp),
                  color = CyberAmber
                )
                // 6. Better Reasoning
                Text(
                  text = "6. BETTER REASONING: ${autopsy.betterReasoning}",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp),
                  color = TextPrimaryDark
                )
                // 7. Next Challenge
                Text(
                  text = "7. NEXT CHALLENGE: ${autopsy.nextChallengeTitle}",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                  color = CyberCyan
                )

                if (!isAdaptiveChallengeOpen) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Button(
                    onClick = {
                      adaptiveChallenge = adaptiveManager.generateTargetedChallenge(authenticatedUid = learnerUid)
                      isAdaptiveChallengeOpen = true
                      selectedAdaptiveActionId = ""
                      selectedAdaptiveEvidenceIds.clear()
                      adaptiveReasoning = ""
                      adaptiveEvaluationResult = null
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("btn_launch_adaptive_challenge")
                  ) {
                    Text(
                      text = "LAUNCH TARGETED ADAPTIVE CHALLENGE",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                      )
                    )
                  }
                }
              }
            }
          }

          // TARGETED ADAPTIVE CHALLENGE INLINE WORKSPACE
          if (isAdaptiveChallengeOpen && adaptiveChallenge != null) {
            val challenge = adaptiveChallenge!!
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurfaceElevated,
              border = BorderStroke(1.dp, CyberCyan),
              modifier = Modifier.fillMaxWidth().testTag("adaptive_adversary_challenge_card")
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "ADAPTIVE CHALLENGE // REASONING RECOVERY",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 9.5.sp
                    ),
                    color = CyberCyan
                  )
                  Text(
                    text = "TIER: ${challenge.challengeTier}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = FontFamily.Monospace),
                    color = TextSecondaryDark
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = challenge.scenarioTitle,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                  color = TextPrimaryDark
                )
                Text(
                  text = challenge.scenarioBriefing,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 14.sp),
                  color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Co-pilot Adversarial Claim
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = CyberSurfaceElevated,
                  border = BorderStroke(0.5.dp, CyberBorder),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                      text = "${challenge.aiAnalystClaim.analystName} (${challenge.aiAnalystClaim.confidencePercentage}% Confidence):",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                      color = CyberAmber
                    )
                    Text(
                      text = "\"${challenge.aiAnalystClaim.claimText}\"",
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, fontFamily = FontFamily.Monospace),
                      color = TextPrimaryDark
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Audit Telemetry Pool
                Text(
                  text = "AUDIT TELEMETRY POOL:",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                  color = CyberCyan
                )
                challenge.evidencePool.forEach { evi ->
                  val isChecked = selectedAdaptiveEvidenceIds.contains(evi.id)
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 2.dp)
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (isChecked) CyberCyan.copy(alpha = 0.1f) else Color.Transparent)
                      .clickable {
                        if (isChecked) selectedAdaptiveEvidenceIds.remove(evi.id) else selectedAdaptiveEvidenceIds.add(evi.id)
                      }
                      .padding(4.dp)
                  ) {
                    Checkbox(
                      checked = isChecked,
                      onCheckedChange = {
                        if (it) selectedAdaptiveEvidenceIds.add(evi.id) else selectedAdaptiveEvidenceIds.remove(evi.id)
                      },
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "${evi.id}: ${evi.source} - ${evi.summary}",
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                      color = TextPrimaryDark,
                      maxLines = 1
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Triage Actions
                Text(
                  text = "SELECT TRIAGE ACTION:",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                  color = CyberCyan
                )
                challenge.actionOptions.forEach { opt ->
                  val isSelected = selectedAdaptiveActionId == opt.id
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 2.dp)
                      .clip(RoundedCornerShape(4.dp))
                      .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                      .clickable { selectedAdaptiveActionId = opt.id }
                      .padding(4.dp)
                  ) {
                    RadioButton(
                      selected = isSelected,
                      onClick = { selectedAdaptiveActionId = opt.id },
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Text(
                        text = opt.label,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp),
                        color = if (isSelected) CyberCyan else TextPrimaryDark
                      )
                      Text(
                        text = opt.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.5.sp),
                        color = TextSecondaryDark
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                // Operator Justification
                OutlinedTextField(
                  value = adaptiveReasoning,
                  onValueChange = { adaptiveReasoning = it },
                  placeholder = { Text("Document why local host execution refutes the co-pilot's claim (min 15 chars)...", style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp)) },
                  modifier = Modifier.fillMaxWidth().testTag("adaptive_reasoning_input"),
                  textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(8.dp))
                val evalRes = adaptiveEvaluationResult
                if (evalRes == null) {
                  Button(
                    onClick = {
                      val res = adaptiveManager.evaluateChallengeSubmission(
                        challengeId = challenge.challengeId,
                        learnerUid = learnerUid,
                        selectedActionId = selectedAdaptiveActionId,
                        selectedEvidenceIds = selectedAdaptiveEvidenceIds.toList(),
                        reasoning = adaptiveReasoning
                      )
                      adaptiveEvaluationResult = res
                    },
                    enabled = selectedAdaptiveActionId.isNotBlank() && adaptiveReasoning.trim().length >= 15,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(38.dp).testTag("btn_submit_adaptive_triage")
                  ) {
                    Text("SUBMIT ADAPTIVE TRIAGE", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 10.sp))
                  }
                } else {
                  // IMPROVEMENT VERIFIED RESULT BANNER
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (evalRes.isImprovementVerified) CyberEmerald.copy(alpha = 0.15f) else CyberRed.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, if (evalRes.isImprovementVerified) CyberEmerald else CyberRed),
                    modifier = Modifier.fillMaxWidth().testTag("adaptive_improvement_banner")
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                          if (evalRes.isImprovementVerified) Icons.Default.CheckCircle else Icons.Default.Warning,
                          contentDescription = null,
                          tint = if (evalRes.isImprovementVerified) CyberEmerald else CyberRed,
                          modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = evalRes.headline,
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                          ),
                          color = if (evalRes.isImprovementVerified) CyberEmerald else CyberRed
                        )
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = evalRes.explanation,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = TextPrimaryDark
                      )
                      evalRes.demonstratedImprovementSummary?.let { summary ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = summary,
                          style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.5.sp, fontWeight = FontWeight.Bold),
                          color = CyberEmerald
                        )
                      }
                      evalRes.verifiedProofArtifactId?.let { artifactId ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                          text = "Proof Artifact: $artifactId // Digest: ${evalRes.evidenceDigest.take(28)}...",
                          style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, fontFamily = FontFamily.Monospace),
                          color = CyberCyan
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Proof Digest: ${result.evidenceDigest.take(28)}...",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontSize = 8.5.sp
            ),
            color = CyberCyan
          )
        }
      }
    }
  }
}
