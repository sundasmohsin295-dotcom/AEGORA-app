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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.CareerRole
import com.example.model.ReverseRoadmapAnalysis
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.SkillProgressBar
import com.example.ui.theme.*

enum class CareerCenterTab {
  ROADMAP_COMPILER, TARGET_ROLES, RESUME_COMPILER
}

@Composable
fun CareerCenterScreen(
  onNavigateBack: () -> Unit,
  onSelectCareer: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val roles = AegoraRepository.careerRoles
  val userProfile by AegoraRepository.userProfile.collectAsState()
  val reverseRoadmap = AegoraRepository.reverseRoadmaps.first()
  var activeTab by remember { mutableStateOf(CareerCenterTab.ROADMAP_COMPILER) }
  var selectedRole by remember { mutableStateOf(roles.find { it.id == userProfile.targetCareerId } ?: roles.first()) }
  var showExportDialog by remember { mutableStateOf(false) }

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
            text = "Cyber Career Compiler & Reverse Roadmap",
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
      // 1. Hero Card
      item {
        CyberCard(
          borderColor = CyberCyan.copy(alpha = 0.4f),
          backgroundColor = CyberSurface
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "REVERSE ROADMAP ENGINE",
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Target Role: ${reverseRoadmap.targetJobTitle}",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimaryDark
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald)
            ) {
              Text(
                text = "${reverseRoadmap.matchPercentage}% Match",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Aegora deconstructs enterprise hiring requirements into precise competency gaps and builds your verified portfolio trail.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )
        }
      }

      // 2. Navigation Tabs
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          CareerCenterTab.entries.forEach { tab ->
            val isSelected = tab == activeTab
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { activeTab = tab }
                .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(8.dp)),
              color = if (isSelected) CyberSurfaceElevated else CyberSurface
            ) {
              Text(
                text = when (tab) {
                  CareerCenterTab.ROADMAP_COMPILER -> "Reverse Roadmap"
                  CareerCenterTab.TARGET_ROLES -> "Role Catalog"
                  CareerCenterTab.RESUME_COMPILER -> "Evidence Resume"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) CyberCyan else TextSecondaryDark,
                modifier = Modifier
                  .padding(vertical = 10.dp)
                  .wrapContentWidth(Alignment.CenterHorizontally)
              )
            }
          }
        }
      }

      // =======================================================================
      // TAB 1: REVERSE ROADMAP & COMPETENCY GAPS
      // =======================================================================
      if (activeTab == CareerCenterTab.ROADMAP_COMPILER) {
        item {
          CyberSectionHeader(
            title = "Missing Competency Gaps (${reverseRoadmap.missingCompetencies.size})",
            subtitle = "Estimated ${reverseRoadmap.estimatedHoursToCloseGap} hours of targeted lab work to reach 95%+ match"
          )
        }

        items(reverseRoadmap.missingCompetencies) { comp ->
          CyberCard(
            borderColor = CyberCrimson.copy(alpha = 0.4f),
            backgroundColor = CyberSurface
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = comp.competencyName,
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimaryDark
                )
                Text(
                  text = "Industry Demand Weight: ${comp.marketDemandWeight} • Est: ${comp.estimatedHoursToMaster}h",
                  style = MaterialTheme.typography.labelSmall,
                  color = CyberAmber
                )
              }
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberCrimson.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "GAP",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberCrimson,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "Why Employers Require This: ${comp.whyEmployersDemand}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Remedial Lab: ${comp.remedialLabModule}",
                  style = MaterialTheme.typography.labelSmall,
                  color = CyberCyan
                )
              }
            }
          }
        }

        // Verified Strengths
        item {
          CyberSectionHeader(
            title = "Verified Matching Competencies (${reverseRoadmap.verifiedMatchedCompetencies.size})",
            subtitle = "Backed by cryptographic SHA-256 lab artifacts"
          )
        }

        items(reverseRoadmap.verifiedMatchedCompetencies) { matched ->
          CyberCard(
            borderColor = CyberEmerald.copy(alpha = 0.4f)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = matched,
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimaryDark
                )
              }
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberEmerald.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "VERIFIED",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberEmerald,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }

      // =======================================================================
      // TAB 2: TARGET ROLE CATALOG
      // =======================================================================
      if (activeTab == CareerCenterTab.TARGET_ROLES) {
        items(roles) { role ->
          val isTarget = role.id == userProfile.targetCareerId
          val matchScore = if (isTarget) 86 else (58..76).random()

          CyberCard(
            borderColor = if (isTarget) CyberEmerald else CyberBorder,
            backgroundColor = if (isTarget) CyberSurfaceElevated else CyberSurface,
            onClick = {
              selectedRole = role
            }
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isTarget) CyberEmerald.copy(alpha = 0.2f) else CyberSurfaceElevated),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (role.category) {
                      "Defensive" -> Icons.Default.Shield
                      "Offensive" -> Icons.Default.Gavel
                      "Cloud" -> Icons.Default.Cloud
                      "Engineering" -> Icons.Default.Code
                      else -> Icons.Default.Psychology
                    },
                    contentDescription = null,
                    tint = if (isTarget) CyberEmerald else CyberCyan,
                    modifier = Modifier.size(22.dp)
                  )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(role.title, style = MaterialTheme.typography.titleLarge, color = TextPrimaryDark)
                  Text("${role.category} • Avg ${role.averageSalary}", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberSurface,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isTarget) CyberEmerald else CyberCyan
                )
              ) {
                Text(
                  text = "$matchScore% MATCH",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = if (isTarget) CyberEmerald else CyberCyan,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = role.description,
              style = MaterialTheme.typography.bodyMedium,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("REQUIRED SKILL PROFILE", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              role.coreSkills.take(3).forEach { skill ->
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = CyberSurfaceElevated,
                  border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
                ) {
                  Text(
                    text = skill,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimaryDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Certifications: ${role.certifications.joinToString(", ")}",
                style = MaterialTheme.typography.labelSmall,
                color = CyberGold,
                modifier = Modifier.weight(1f)
              )

              if (!isTarget) {
                Button(
                  onClick = {
                    AegoraRepository.updateTargetCareer(role.id)
                    onSelectCareer(role.id)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Set as Target", color = CyberBackground, fontWeight = FontWeight.Bold)
                }
              } else {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CyberEmerald.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = "Active Target ✓",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyberEmerald,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }
        }
      }

      // =======================================================================
      // TAB 3: EVIDENCE RESUME COMPILER
      // =======================================================================
      if (activeTab == CareerCenterTab.RESUME_COMPILER) {
        item {
          CyberCard(
            borderColor = CyberEmerald.copy(alpha = 0.5f),
            backgroundColor = CyberSurface
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "EVIDENCE-FIRST RESUME COMPILER",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = CyberEmerald
                )
                Text(
                  text = "Verified Bullet Points Generator",
                  style = MaterialTheme.typography.titleMedium,
                  color = TextPrimaryDark
                )
              }
              IconButton(onClick = { showExportDialog = true }) {
                Icon(Icons.Default.Download, contentDescription = "Export", tint = CyberCyan)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Instead of vague claims ('Knowledge of SIEM'), Aegora generates ATS-optimized bullet points linked directly to your completed lab hashes and MITRE ATT&CK techniques.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
              Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                ResumeBulletItem(
                  bullet = "• Triaged 47 live enterprise SOC incidents, isolating lateral movement and detecting Cobalt Strike C2 beacons in under 4 minutes with 0 false dismissals.",
                  tag = "Verified Lab #29 • T1071.001"
                )
                ResumeBulletItem(
                  bullet = "• Formulated detection engineering rules for Mimikatz LSASS credential dumping (T1003.001) mapped against MITRE ATT&CK framework with 91% precision.",
                  tag = "Verified Lab #14 • T1003.001"
                )
                ResumeBulletItem(
                  bullet = "• Executed end-to-end incident containment playbooks across AWS CloudTrail and Linux Auth logs, preserving evidence chains with SHA-256 hashing.",
                  tag = "Verified Lab #19 • T1078.004"
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = { showExportDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Export ATS Verified Resume (.PDF / .MD)", color = CyberBackground, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = { Text("Export Evidence Resume", color = TextPrimaryDark) },
      text = {
        Text(
          "Your verified ATS resume embeds active links to your Aegora Skill Passport (${userProfile.passportId}) and SHA-256 evidence logs so technical recruiters can verify your skills instantly.",
          color = TextSecondaryDark
        )
      },
      confirmButton = {
        Button(
          onClick = { showExportDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
        ) {
          Text("Download Markdown", color = CyberBackground, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      },
      containerColor = CyberSurface
    )
  }
}

@Composable
private fun ResumeBulletItem(
  bullet: String,
  tag: String,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Text(
      text = bullet,
      style = MaterialTheme.typography.bodyMedium,
      color = TextPrimaryDark
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = tag,
      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
      color = CyberCyan
    )
  }
}
