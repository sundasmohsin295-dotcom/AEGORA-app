package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.model.QuizQuestion
import com.example.ui.components.CodeTerminalView
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@Composable
fun QuizScreen(
  quizId: String,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val questions = AegoraRepository.sampleQuizzes
  var currentQuestionIndex by remember { mutableIntStateOf(0) }
  val currentQuestion = questions.getOrElse(currentQuestionIndex) { questions.first() }

  var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
  var isSubmitted by remember { mutableStateOf(false) }
  var score by remember { mutableIntStateOf(0) }

  Scaffold(
    topBar = {
      Surface(
        color = CyberBackground,
        modifier = Modifier.fillMaxWidth().statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateBack) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimaryDark)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Scenario Assessment (${currentQuestionIndex + 1}/${questions.size})",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark
            )
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
          ) {
            Text(
              text = "Score: $score",
              style = MaterialTheme.typography.labelMedium,
              color = CyberCyan,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
      }
    },
    bottomBar = {
      Surface(
        color = CyberSurface,
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          if (!isSubmitted) {
            Button(
              onClick = {
                if (selectedOptionIndex != null) {
                  isSubmitted = true
                  if (selectedOptionIndex == currentQuestion.correctOptionIndex) {
                    score += 100
                  }
                }
              },
              enabled = selectedOptionIndex != null,
              modifier = Modifier.fillMaxWidth().testTag("quiz_submit_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Submit Answer", color = CyberBackground, fontWeight = FontWeight.Bold)
            }
          } else {
            Button(
              onClick = {
                if (currentQuestionIndex < questions.size - 1) {
                  currentQuestionIndex++
                  selectedOptionIndex = null
                  isSubmitted = false
                } else {
                  onNavigateBack()
                }
              },
              modifier = Modifier.fillMaxWidth().testTag("quiz_next_btn"),
              colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = if (currentQuestionIndex < questions.size - 1) "Next Question" else "Complete Assessment",
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .background(CyberBackground)
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Question Statement
      item {
        CyberCard {
          if (currentQuestion.mitreTechnique != null) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberIndigo.copy(alpha = 0.2f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberIndigo)
            ) {
              Text(
                text = "MITRE ATT&CK: ${currentQuestion.mitreTechnique}",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
          }

          Text(
            text = currentQuestion.questionText,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark,
            lineHeight = 26.sp
          )

          if (currentQuestion.scenarioContext != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = currentQuestion.scenarioContext!!,
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondaryDark
            )
          }
        }
      }

      // 2. Telemetry / Log Snippet
      if (currentQuestion.logSnippet != null) {
        item {
          CodeTerminalView(
            code = currentQuestion.logSnippet!!,
            title = "LOG SNIPPET / EVENT TELEMETRY"
          )
        }
      }

      // 3. Multiple Choice Options
      items(currentQuestion.options.size) { index ->
        val optionText = currentQuestion.options[index]
        val isSelected = selectedOptionIndex == index
        val isCorrect = isSubmitted && index == currentQuestion.correctOptionIndex
        val isWrongSelection = isSubmitted && isSelected && !isCorrect

        val borderColor = when {
          isCorrect -> CyberEmerald
          isWrongSelection -> CyberCrimson
          isSelected -> CyberCyan
          else -> CyberBorder
        }

        val backgroundColor = when {
          isCorrect -> CyberEmerald.copy(alpha = 0.15f)
          isWrongSelection -> CyberCrimson.copy(alpha = 0.15f)
          isSelected -> CyberSurfaceElevated
          else -> CyberSurface
        }

        CyberCard(
          borderColor = borderColor,
          backgroundColor = backgroundColor,
          onClick = if (!isSubmitted) { { selectedOptionIndex = index } } else null
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                  when {
                    isCorrect -> CyberEmerald
                    isWrongSelection -> CyberCrimson
                    isSelected -> CyberCyan
                    else -> CyberSurfaceElevated
                  }
                ),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = ('A' + index).toString(),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected || isCorrect || isWrongSelection) CyberBackground else TextSecondaryDark
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
              text = optionText,
              style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
              ),
              color = TextPrimaryDark,
              modifier = Modifier.weight(1f)
            )

            if (isCorrect) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald)
            } else if (isWrongSelection) {
              Icon(Icons.Default.Cancel, contentDescription = null, tint = CyberCrimson)
            }
          }
        }
      }

      // 4. Detailed Explanation (Revealed after submission)
      if (isSubmitted) {
        item {
          CyberCard(
            borderColor = if (selectedOptionIndex == currentQuestion.correctOptionIndex) CyberEmerald else CyberAmber
          ) {
            Text(
              text = "TECHNICAL EXPLANATION & MITRE ATT&CK MAPPING",
              style = MaterialTheme.typography.labelSmall,
              color = if (selectedOptionIndex == currentQuestion.correctOptionIndex) CyberEmerald else CyberAmber
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = currentQuestion.detailedExplanation,
              style = MaterialTheme.typography.bodyMedium,
              color = TextPrimaryDark,
              lineHeight = 22.sp
            )
          }
        }
      }
    }
  }
}
