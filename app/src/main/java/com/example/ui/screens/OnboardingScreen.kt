package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.DailyCommitment
import com.example.model.SkillLevel
import com.example.model.TargetTimeline
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@Composable
fun OnboardingScreen(
  onOnboardingComplete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var step by remember { mutableIntStateOf(1) } // 1: Career Role, 2: Experience, 3: Daily Time, 4: Sprint Target, 5: Generated Journey
  var selectedCareerId by remember { mutableStateOf("soc_analyst") }
  var selectedExperience by remember { mutableStateOf(SkillLevel.INTERMEDIATE) }
  var selectedCommitment by remember { mutableStateOf(DailyCommitment.FORTY_FIVE_MINS) }
  var selectedTimeline by remember { mutableStateOf(TargetTimeline.SIX_MONTHS) }


  val careerRoles = AegoraRepository.careerRoles

  Scaffold(
    modifier = modifier.fillMaxSize().background(CyberBackground),
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
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (step > 1 && step < 5) {
            OutlinedButton(
              onClick = { step-- },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Back")
            }
          } else {
            Spacer(modifier = Modifier.width(1.dp))
          }

          Button(
            onClick = {
              if (step < 5) {
                step++
              } else {
                AegoraRepository.updateTargetCareer(selectedCareerId)
                AegoraRepository.updatePreferences(selectedExperience, selectedCommitment, selectedTimeline)
                onOnboardingComplete()
              }
            },
            modifier = Modifier.testTag("onboarding_next_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
            shape = RoundedCornerShape(20.dp)
          ) {
            Text(
              text = when (step) {
                4 -> "Generate Cyber Journey ⚡"
                5 -> "Enter Command Center 🚀"
                else -> "Continue"
              },
              color = Color.White,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(CyberBackground)
        .padding(paddingValues)
        .padding(16.dp)
    ) {
      // Step Indicator
      Row(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(CyberCyan),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "AEGORA ONBOARDING",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = CyberCyan
          )
        }

        Text(
          text = "STEP $step OF 5",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      when (step) {
        1 -> {
          Text(
            text = "Select Your Target Cybersecurity Role",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "AEGORA calibrates your roadmap, incident simulations, and interview prep to your target outcome.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(16.dp))

          LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
          ) {
            items(careerRoles) { role ->
              val isSelected = role.id == selectedCareerId
              CyberCard(
                borderColor = if (isSelected) CyberCyan else CyberBorder,
                backgroundColor = if (isSelected) CyberSurfaceElevated else CyberSurface,
                onClick = { selectedCareerId = role.id }
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(role.title, style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
                    Text("${role.category} • ${role.averageSalary}", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                  }
                  RadioButton(
                    selected = isSelected,
                    onClick = { selectedCareerId = role.id },
                    colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                  )
                }
              }
            }
          }
        }

        2 -> {
          Text(
            text = "What is your current technical background?",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "We adapt starting difficulty so you don't repeat fundamentals you already know.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(16.dp))

          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SkillLevel.entries.forEach { level ->
              val isSelected = level == selectedExperience
              CyberCard(
                borderColor = if (isSelected) CyberCyan else CyberBorder,
                backgroundColor = if (isSelected) CyberSurfaceElevated else CyberSurface,
                onClick = { selectedExperience = level }
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(level.label, style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
                    Text(
                      text = when (level) {
                        SkillLevel.COMPLETE_BEGINNER, SkillLevel.BEGINNER -> "New to IT & Networking fundamentals"
                        SkillLevel.INTERMEDIATE -> "Familiar with Linux CLI, TCP/IP, basic scripting"
                        SkillLevel.ADVANCED, SkillLevel.EXPERT -> "Hands-on experience in IT/SOC or Sysadmin"
                      },
                      style = MaterialTheme.typography.labelSmall,
                      color = TextSecondaryDark
                    )
                  }
                  RadioButton(
                    selected = isSelected,
                    onClick = { selectedExperience = level },
                    colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                  )
                }
              }
            }
          }

        }

        3 -> {
          Text(
            text = "How much time can you commit daily?",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Consistent 20-45 minute daily practice yields the highest retention and skill mastery.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(16.dp))

          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            DailyCommitment.entries.forEach { commitment ->
              val isSelected = commitment == selectedCommitment
              CyberCard(
                borderColor = if (isSelected) CyberCyan else CyberBorder,
                backgroundColor = if (isSelected) CyberSurfaceElevated else CyberSurface,
                onClick = { selectedCommitment = commitment }
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(commitment.label, style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
                  RadioButton(
                    selected = isSelected,
                    onClick = { selectedCommitment = commitment },
                    colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                  )
                }
              }
            }
          }
        }

        4 -> {
          Text(
            text = "Select Your Target Timeline Sprint",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Choose the pacing that aligns with your hiring deadlines or study goals.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(16.dp))

          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TargetTimeline.entries.forEach { sprint ->
              val isSelected = sprint == selectedTimeline
              CyberCard(
                borderColor = if (isSelected) CyberCyan else CyberBorder,
                backgroundColor = if (isSelected) CyberSurfaceElevated else CyberSurface,
                onClick = { selectedTimeline = sprint }
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(sprint.label, style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
                  RadioButton(
                    selected = isSelected,
                    onClick = { selectedTimeline = sprint },
                    colors = RadioButtonDefaults.colors(selectedColor = CyberCyan)
                  )
                }
              }
            }
          }

        }

        5 -> {
          // Journey Generated Summary
          CyberCard(borderColor = CyberEmerald) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(CircleShape)
                  .background(CyberEmerald.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(26.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "CUSTOM JOURNEY GENERATED",
                  style = MaterialTheme.typography.labelSmall,
                  color = CyberEmerald
                )
                Text(
                  text = "Your Cyber Strategy is Ready",
                  style = MaterialTheme.typography.titleLarge,
                  color = TextPrimaryDark
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text("CAREER FOCUS: SOC Analyst L1/L2", style = MaterialTheme.typography.titleMedium, color = CyberCyan)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• 6 Progressive Roadmap Months", color = TextPrimaryDark)
                Text("• 14 Hands-on Incident Simulations & Labs", color = TextPrimaryDark)
                Text("• 2 Real-world Capstone Portfolios (Wazuh & AWS IR)", color = TextPrimaryDark)
                Text("• Verifiable Skill Passport & Job Match System", color = TextPrimaryDark)
              }
            }
          }
        }
      }
    }
  }
}
