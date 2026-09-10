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
import com.example.model.*
import com.example.platform.CrossPlatformMissionBridge
import com.example.ui.theme.*

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
  onVerificationComplete: (ClientSafeAiVerificationResult) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val selectedEvidenceIds = remember { mutableStateListOf<String>() }
  var verificationResult by remember { mutableStateOf<ClientSafeAiVerificationResult?>(null) }
  var isEvaluating by remember { mutableStateOf(false) }

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
        // Accept AI Button
        OutlinedButton(
          onClick = {
            isEvaluating = true
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
            isEvaluating = true
            val outcome = CrossPlatformMissionBridge.evaluateAiClaimDecision(
              attemptId = "att_${System.currentTimeMillis()}",
              missionId = missionId,
              claimId = claim.claimId,
              learnerDecision = LearnerAiDecision.CHALLENGE_AI,
              selectedEvidenceIds = selectedEvidenceIds.toList()
            )
            verificationResult = outcome
            isEvaluating = false
            onVerificationComplete(outcome)
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
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = result.headline,
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
              ),
              color = if (result.isAiFailureDetected) CyberEmerald else CyberRed
            )
            Text(
              text = "AUTHORITATIVE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
              ),
              color = TextSecondaryDark
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = result.explanation,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
            color = TextPrimaryDark
          )

          if (result.detectedFailurePattern != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberAmber.copy(alpha = 0.2f),
              border = BorderStroke(0.5.dp, CyberAmber)
            ) {
              Text(
                text = "Weakness Detected: ${result.detectedFailurePattern.label}",
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
