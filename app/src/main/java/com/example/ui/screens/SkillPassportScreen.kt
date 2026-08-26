package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.MitreTacticCoverage
import com.example.model.SimulatedExperienceLedger
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.EvidenceBadge
import com.example.ui.components.SkillProgressBar
import com.example.ui.theme.*

@Composable
fun SkillPassportScreen(
  onNavigateToCareers: () -> Unit,
  onNavigateToProjects: () -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by AegoraRepository.userProfile.collectAsState()
  val domains = AegoraRepository.skillDomains
  val simulatedLedger by AegoraRepository.simulatedExperience.collectAsState()
  val mitreCoverages = AegoraRepository.mitreTacticCoverages
  var showShareDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Digital Passport Hero Card
    item {
      CyberCard(
        borderColor = CyberEmerald,
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  Brush.linearGradient(
                    colors = listOf(CyberEmerald, CyberCyan)
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "AEGORA SKILL PASSPORT",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
              Text(
                text = "PASSPORT ID: ${userProfile.passportId}",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = CyberCyan
              )
            }
          }

          IconButton(
            onClick = { showShareDialog = true },
            modifier = Modifier.testTag("passport_share_btn")
          ) {
            Icon(Icons.Default.Share, contentDescription = "Share Passport", tint = CyberCyan)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Details
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text("OPERATOR CALLSIGN", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            Text(userProfile.callsign, style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
          }
          Column {
            Text("VERIFIED LABS", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            Text("${userProfile.completedLabsCount} Completed", style = MaterialTheme.typography.titleMedium, color = CyberEmerald)
          }
          Column {
            Text("JOB READINESS", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            Text("${userProfile.jobReadinessScore}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = CyberCyan)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CyberSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Cryptographically signed by AEGORA Verification Authority. Evidence items verified via SHA-256 artifact hashes.",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )
          }
        }
      }
    }

    // 2. Simulated Experience Ledger (Transparent Portfolio)
    item {
      CyberCard(
        borderColor = CyberCyan.copy(alpha = 0.5f),
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "PRACTICAL EXPERIENCE LEDGER",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberCyan
            )
            Text(
              text = "Simulated Experience Breakdown",
              style = MaterialTheme.typography.titleMedium,
              color = TextPrimaryDark
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
          ) {
            Text(
              text = "Verified Simulation",
              style = MaterialTheme.typography.labelSmall,
              color = CyberCyan,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ExperienceMetricBox(
            count = simulatedLedger.socInvestigationsCount,
            label = "SOC Triage",
            color = CyberEmerald,
            modifier = Modifier.weight(1f)
          )
          ExperienceMetricBox(
            count = simulatedLedger.incidentSimulationsCount,
            label = "IR Drills",
            color = CyberCyan,
            modifier = Modifier.weight(1f)
          )
          ExperienceMetricBox(
            count = simulatedLedger.detectionEngineeringExercises,
            label = "Detections",
            color = CyberBlue,
            modifier = Modifier.weight(1f)
          )
          ExperienceMetricBox(
            count = simulatedLedger.crisisDecisionsCount,
            label = "Decisions",
            color = CyberViolet,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 3. MITRE ATT&CK Matrix Evidence Coverage
    item {
      CyberSectionHeader(
        title = "MITRE ATT&CK® Matrix Coverage",
        subtitle = "Demonstrated TTPs verified against real enterprise telemetry"
      )
    }

    items(mitreCoverages) { tactic ->
      MitreTacticCard(tactic = tactic)
    }

    // 4. Career Navigation Shortcut
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberCyan.copy(alpha = 0.5f),
          onClick = onNavigateToCareers
        ) {
          Icon(Icons.Default.WorkOutline, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.height(6.dp))
          Text("Reverse Roadmap", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
          Text("Job Match: 86% SOC", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
        }

        CyberCard(
          modifier = Modifier.weight(1f),
          borderColor = CyberIndigo.copy(alpha = 0.5f),
          onClick = onNavigateToProjects
        ) {
          Icon(Icons.Default.Code, contentDescription = null, tint = CyberIndigo, modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.height(6.dp))
          Text("Portfolio Studio", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
          Text("GitHub Blueprints", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        }
      }
    }

    // 5. Verified Skills & Evidence Log
    item {
      CyberSectionHeader(
        title = "Cryptographic Evidence Trail",
        subtitle = "Independently Verifiable Lab Artifacts"
      )
    }

    val allEvidence = domains.flatMap { domain ->
      domain.skills.flatMap { it.verifiedEvidenceList }
    }

    items(allEvidence) { evidence ->
      EvidenceBadge(
        title = evidence.title,
        type = evidence.evidenceType,
        date = evidence.completedDate,
        hash = evidence.artifactHash
      )
    }
  }

  if (showShareDialog) {
    AlertDialog(
      onDismissRequest = { showShareDialog = false },
      title = { Text("Export Verified Skill Passport", color = TextPrimaryDark) },
      text = {
        Column {
          Text(
            "Shareable Public URL:",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
          ) {
            Text(
              "https://aegora.network/passport/${userProfile.passportId}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = CyberCyan,
              modifier = Modifier.padding(8.dp)
            )
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            "Your passport embeds your verified lab evidence, MITRE ATT&CK coverage, CTF records, and GitHub capstone projects for hiring managers and university admissions.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimaryDark
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showShareDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
        ) {
          Text("Copy Link", color = CyberBackground, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showShareDialog = false }) {
          Text("Close", color = TextSecondaryDark)
        }
      },
      containerColor = CyberSurface
    )
  }
}

@Composable
private fun ExperienceMetricBox(
  count: Int,
  label: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = CyberSurfaceElevated,
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = "$count",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = color
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = TextSecondaryDark
      )
    }
  }
}

@Composable
private fun MitreTacticCard(
  tactic: MitreTacticCoverage,
  modifier: Modifier = Modifier
) {
  CyberCard(
    modifier = modifier,
    borderColor = if (tactic.coveragePercent >= 70) CyberEmerald.copy(alpha = 0.4f) else CyberBorder
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "${tactic.tacticId} • ${tactic.tacticName}",
          style = MaterialTheme.typography.titleMedium,
          color = TextPrimaryDark
        )
        Text(
          text = "${tactic.verifiedTechniquesCount} / ${tactic.totalTechniquesInMatrix} Techniques Demonstrated",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = CyberSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (tactic.coveragePercent >= 70) CyberEmerald else CyberCyan
        )
      ) {
        Text(
          text = "${tactic.coveragePercent}%",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = if (tactic.coveragePercent >= 70) CyberEmerald else CyberCyan,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    SkillProgressBar(
      progress = tactic.coveragePercent / 100f,
      label = "Evidence Matrix Verified",
      valueText = "${tactic.coveragePercent}%",
      barColor = if (tactic.coveragePercent >= 70) CyberEmerald else CyberCyan,
      height = 4.dp
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Technique Badges
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      tactic.demonstratedTechniques.take(2).forEach { tech ->
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = CyberSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
        ) {
          Text(
            text = tech,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = TextSecondaryDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
