package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.ui.theme.*

@Composable
fun MissionDiagnosticScreen(
  onNavigateBack: () -> Unit,
  onStartMission: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedEvidenceIndex by remember { mutableIntStateOf(0) }

  val evidenceItems = listOf(
    Triple("System Logs", "auth.log, syslog, auditd event streams", Icons.Default.Description),
    Triple("Process Tree Analysis", "Parent-child PID lineage & spawned shells", Icons.Default.ElectricBolt),
    Triple("Network Connections", "Active TCP/UDP sockets, foreign C2 IPs", Icons.Default.Language),
    Triple("File System Artifacts", "Modified binaries in /usr/bin and /tmp cron", Icons.Default.Save)
  )

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(SpecCanvasBg)
      .testTag("screen_mission_diagnostic"),
    containerColor = SpecCanvasBg,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .background(SpecCanvasBg)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SpecElevatedBg)
            .border(1.dp, SpecBorder, RoundedCornerShape(10.dp))
            .testTag("mission_diagnostic_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = SpecHeadingWhite
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
          text = "Mission",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = SpecHeadingWhite,
          fontFamily = FontFamily.Monospace
        )
      }
    },
    bottomBar = {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(SpecCanvasBg)
          .navigationBarsPadding()
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Button(
          onClick = onStartMission,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("mission_diagnostic_start_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = SpecPrimaryBlue),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "START MISSION",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 1.sp
          )
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Monospace Terminal / Laptop Graphic Container
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("mission_diagnostic_hero_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              color = SpecElevatedBg,
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SpecBorder)
            ) {
              Text(
                text = "DIAGNOSTIC REVIEW // LINUX CLI FORENSICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = SpecCyanHighlight,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            Surface(
              color = SpecElevatedBg,
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, SpecWarningAmber.copy(alpha = 0.5f))
            ) {
              Text(
                text = "[ ⏱ EST. 8 MIN ]",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = SpecWarningAmber,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
              )
            }
          }

          // Terminal Box Emulation
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp),
            color = SpecElevatedBg,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SpecBorder)
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF34D399)))
              }

              Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                  text = "root@soc-investigation:~# journalctl -u sshd --since '1 hour ago'",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  color = SpecHeadingWhite
                )
                Text(
                  text = "Failed password for invalid user admin from 10.0.1.23 port 54228",
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  color = SpecFailureRed
                )
                Text(
                  text = "Accepted publickey for ubuntu from 10.0.1.23 port 54230 ssh2",
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  color = SpecEmeraldVerification
                )
              }
            }
          }
        }
      }

      // Mission Brief Section
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "MISSION BRIEF",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = SpecSubtextSlate,
            letterSpacing = 1.sp
          )

          Text(
            text = "Analyze live endpoint telemetry to identify log tampering and lateral movement.\nVerify whether root privilege escalation succeeded across internal subnets.\nChallenge AI Analyst conclusions to prevent premature false-positive quarantine.",
            fontSize = 13.sp,
            color = SpecHeadingWhite,
            lineHeight = 20.sp
          )
        }
      }

      // Key Evidence Section
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "KEY EVIDENCE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = SpecSubtextSlate,
            letterSpacing = 1.sp
          )

          evidenceItems.forEachIndexed { index, (title, desc, icon) ->
            val isSelected = selectedEvidenceIndex == index
            Surface(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { selectedEvidenceIndex = index }
                .testTag("mission_evidence_row_$index"),
              color = if (isSelected) SpecElevatedBg else SpecCardBg,
              shape = RoundedCornerShape(10.dp),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) SpecPrimaryBlue else SpecBorder
              )
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) SpecPrimaryBlue.copy(alpha = 0.2f) else SpecElevatedBg),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) SpecPrimaryBlue else SpecSubtextSlate,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpecHeadingWhite
                  )
                  Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = SpecSubtextSlate
                  )
                }

                if (isSelected) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = SpecPrimaryBlue,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
