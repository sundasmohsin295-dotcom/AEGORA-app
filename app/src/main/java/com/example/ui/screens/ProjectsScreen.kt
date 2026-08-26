package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.ProjectBlueprint
import com.example.ui.components.CodeTerminalView
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

@Composable
fun ProjectsScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val blueprints = AegoraRepository.projectBlueprints
  var selectedProject by remember { mutableStateOf(blueprints.first()) }
  var showExportModal by remember { mutableStateOf(false) }

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
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Portfolio Project Builder",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
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
      contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        CyberCard(
          borderColor = CyberCyan.copy(alpha = 0.4f),
          backgroundColor = CyberSurface
        ) {
          Text(
            text = "REAL-WORLD CAPSTONE PROJECTS",
            style = MaterialTheme.typography.labelSmall,
            color = CyberCyan
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Build Proof of Competency",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Complete end-to-end security architectures, deploy detection rules, and export curated GitHub READMEs and resume impact bullet points.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )
        }
      }

      // Project Selection Pills
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          blueprints.forEach { project ->
            val isSelected = project.id == selectedProject.id
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { selectedProject = project }
                .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(8.dp)),
              color = if (isSelected) CyberSurfaceElevated else CyberSurface
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 10.dp, horizontal = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = project.title.split(":").first(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (isSelected) CyberCyan else TextSecondaryDark,
                  maxLines = 1
                )
              }

            }
          }
        }
      }

      // Active Project Details
      item {
        CyberCard(borderColor = CyberCyan) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberIndigo.copy(alpha = 0.2f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberIndigo)
            ) {
              Text(
                text = "${selectedProject.difficulty.uppercase()} • ${selectedProject.estimatedWeeks} WEEKS",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Button(
              onClick = { showExportModal = true },
              colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Export Portfolio", color = CyberBackground, fontWeight = FontWeight.Bold)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = selectedProject.title,
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = selectedProject.architectureSummary,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            lineHeight = 22.sp
          )
        }
      }

      // Key Components
      item {
        CyberSectionHeader(
          title = "Architecture Components",
          subtitle = "Technologies & detection engines deployed"
        )

        CyberCard {
          selectedProject.keyComponents.forEach { component ->
            Row(
              modifier = Modifier.padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(component, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark)
            }
          }
        }
      }

      // GitHub File Structure
      item {
        CyberSectionHeader(
          title = "Recommended GitHub Structure",
          subtitle = "Industry-standard open source layout"
        )

        CodeTerminalView(
          code = selectedProject.githubStructure.joinToString("\n├── ") { it }.let { ".\n├── $it" },
          title = "REPOSITORY BLUEPRINT"
        )
      }

      // Resume Bullet Points
      item {
        CyberSectionHeader(
          title = "Resume & LinkedIn Bullets",
          subtitle = "Evidence-backed achievement points"
        )

        CyberCard(borderColor = CyberEmerald.copy(alpha = 0.4f)) {
          selectedProject.resumeBulletPoints.forEach { bullet ->
            Row(
              modifier = Modifier.padding(vertical = 6.dp),
              verticalAlignment = Alignment.Top
            ) {
              Text("• ", color = CyberEmerald, fontWeight = FontWeight.Bold)
              Text(bullet, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark, lineHeight = 20.sp)
            }
          }
        }
      }
    }
  }

  if (showExportModal) {
    AlertDialog(
      onDismissRequest = { showExportModal = false },
      title = { Text("Export Portfolio Artifacts", color = TextPrimaryDark) },
      text = {
        Column {
          Text("Your capstone project summary, GitHub repository boilerplate, and resume impact bullets are ready to export to your portfolio.", color = TextSecondaryDark)
        }
      },
      confirmButton = {
        Button(
          onClick = { showExportModal = false },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
        ) {
          Text("Download README.md", color = CyberBackground, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportModal = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      },
      containerColor = CyberSurface
    )
  }
}
