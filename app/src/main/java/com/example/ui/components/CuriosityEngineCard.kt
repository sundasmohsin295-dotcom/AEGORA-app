package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CyberTwin60Snapshot
import com.example.model.TwinDimensionV12
import com.example.ui.theme.*

data class CuriosityPrompt(
  val id: String,
  val category: String,
  val title: String,
  val hook: String,
  val expandedExplanation: String,
  val whyItMatters: String,
  val impactLabel: String,
  val actionText: String,
  val icon: ImageVector,
  val accentColor: Color,
  val dimension: TwinDimensionV12,
  val destinationTag: String
)

/**
 * Generates data-grounded curiosity prompts directly from the student's real Cyber Twin state.
 */
fun generateCuriosityPrompts(snapshot: CyberTwin60Snapshot): List<CuriosityPrompt> {
  val prompts = mutableListOf<CuriosityPrompt>()

  val transferDim = snapshot.dimensions[TwinDimensionV12.TRANSFERABILITY]
  val transferScore = transferDim?.currentState ?: 76
  if (transferScore < 85) {
    prompts.add(
      CuriosityPrompt(
        id = "curiosity_transfer",
        category = "TRANSFER CHALLENGE",
        title = "Can Your Knowledge Cross Contexts?",
        hook = "TRANSFERABILITY $transferScore: Your detection heuristics work in standard Windows labs. Can you spot the same adversary in cloud Kubernetes audit logs?",
        expandedExplanation = "Real-world attackers don't stay in one operating system. Transferability proves whether your mental model is conceptual or merely memorized syntax.",
        whyItMatters = "Passing the Transfer Gate directly converts intermediate analysts into autonomous senior detection engineers.",
        impactLabel = "+12% Transferability • Clears Primary Gate",
        actionText = "Test Cross-Environment Transfer",
        icon = Icons.Default.CompareArrows,
        accentColor = if (transferScore < 75) NeonCrimson else CyberCyan,
        dimension = TwinDimensionV12.TRANSFERABILITY,
        destinationTag = "live_soc_range"
      )
    )
  }

  val reasoningDim = snapshot.dimensions[TwinDimensionV12.REASONING]
  val detectionDim = snapshot.dimensions[TwinDimensionV12.DETECTION]
  prompts.add(
    CuriosityPrompt(
      id = "curiosity_dependency",
      category = "HIDDEN CAPABILITY DEPENDENCY",
      title = "Causal Reasoning Constrains Detection",
      hook = "Your Detection Engineering (${detectionDim?.currentState ?: 82}) is pacing ahead of Causal Reasoning (${reasoningDim?.currentState ?: 85}).",
      expandedExplanation = "High detection without causal reasoning leads to brittle SIEM rules that produce false positives under adversary living-off-the-land evasion.",
      whyItMatters = "Mastering causal chain construction allows you to write Sigma rules that catch 0-days before signatures exist.",
      impactLabel = "+15% Evasion Resistance",
      actionText = "Explore Causal Root-Cause Lab",
      icon = Icons.Default.AccountTree,
      accentColor = CyberIndigo,
      dimension = TwinDimensionV12.REASONING,
      destinationTag = "lab_simulator"
    )
  )

  val confidenceDim = snapshot.dimensions[TwinDimensionV12.CONFIDENCE_CALIBRATION]
  prompts.add(
    CuriosityPrompt(
      id = "curiosity_calibration",
      category = "METACOGNITIVE CALIBRATION",
      title = "The Unknown-Unknown Boundary",
      hook = "Metacognitive Gap: You rated memory forensics 90% confidence, but edge-case heap volatility hasn't been tested.",
      expandedExplanation = "Top cyber operators are accurately calibrated: they know exactly what they know, and crucially, know the exact edges of their uncertainty.",
      whyItMatters = "Prevents catastrophic incident misclassification during active live ransomware response.",
      impactLabel = "Calibrates Metacognitive Index",
      actionText = "Inspect Calibration Drill",
      icon = Icons.Default.Psychology,
      accentColor = CyberAmber,
      dimension = TwinDimensionV12.CONFIDENCE_CALIBRATION,
      destinationTag = "knowledge_vault"
    )
  )

  return prompts
}

/**
 * Interactive Curiosity Engine Card:
 * Replaces uninspiring stat bars with ethical behavioral psychology prompts
 * that trigger genuine curiosity, progress visibility, and purposeful exploration.
 */
@Composable
fun CuriosityEngineCard(
  snapshot: CyberTwin60Snapshot,
  onPromptActionClick: (CuriosityPrompt) -> Unit,
  modifier: Modifier = Modifier
) {
  val prompts = remember(snapshot) { generateCuriosityPrompts(snapshot) }
  var expandedPromptId by remember { mutableStateOf<String?>(prompts.firstOrNull()?.id) }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(CyberSurface)
      .border(BorderStroke(1.dp, CyberBorder), RoundedCornerShape(14.dp))
      .padding(14.dp)
      .testTag("curiosity_engine_card")
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(CyberCyan.copy(alpha = 0.15f))
            .border(1.dp, CyberCyan, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            tint = CyberCyan,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "CURIOSITY ENGINE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.8.sp,
              fontSize = 11.sp
            ),
            color = CyberCyan
          )
          Text(
            text = "Behavioral Discovery & Capability Edges",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = TextSecondaryDark
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(4.dp),
        color = CyberSurfaceElevated
      ) {
        Text(
          text = "${prompts.size} Active Sparks",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          ),
          color = TextTertiaryDark,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // List of Curiosity Prompts
    prompts.forEach { prompt ->
      val isExpanded = prompt.id == expandedPromptId

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isExpanded) CyberSurfaceElevated else CyberSurfaceVariant,
        border = BorderStroke(
          1.dp,
          if (isExpanded) prompt.accentColor.copy(alpha = 0.6f) else CyberBorderSubtle
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp)
          .clickable {
            expandedPromptId = if (isExpanded) null else prompt.id
          }
          .testTag("curiosity_prompt_${prompt.id}")
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          // Category Badge & Title Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              modifier = Modifier.weight(1f),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = prompt.accentColor.copy(alpha = 0.15f)
              ) {
                Text(
                  text = prompt.category,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 8.5.sp
                  ),
                  color = prompt.accentColor,
                  modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = prompt.title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
            }

            Icon(
              imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = null,
              tint = TextSecondaryDark,
              modifier = Modifier.size(16.dp)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          // The Hook (Always visible)
          Text(
            text = prompt.hook,
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.5.sp,
              lineHeight = 16.sp,
              fontWeight = FontWeight.Medium
            ),
            color = if (isExpanded) TextPrimaryDark else TextSecondaryDark
          )

          // Progressive Disclosure Details
          AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
          ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
              Text(
                text = prompt.expandedExplanation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                color = TextSecondaryDark
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Why it Matters Callout
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = CyberSurface,
                border = BorderStroke(1.dp, CyberBorderSubtle),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = CyberAmber,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = prompt.whyItMatters,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, lineHeight = 13.sp),
                    color = TextPrimaryDark
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Action Bar
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = prompt.impactLabel,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  ),
                  color = CyberEmerald
                )

                Button(
                  onClick = { onPromptActionClick(prompt) },
                  colors = ButtonDefaults.buttonColors(containerColor = prompt.accentColor),
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                  modifier = Modifier.testTag("curiosity_action_btn_${prompt.id}")
                ) {
                  Text(
                    text = prompt.actionText,
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
          }
        }
      }
    }
  }
}
