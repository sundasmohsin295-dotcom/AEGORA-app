package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
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
fun SkillPassportMasterScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
  ) {
    val isWideScreen = maxWidth >= 840.dp

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .testTag("skill_passport_master_screen"),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ObsidianSurface)
                .border(1.dp, SlateBorder, CircleShape)
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("AEGORA SKILL PASSPORT", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
              Text("Verified Skills. Real Experience. Career Ready.", color = TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
          }

          Box(
            modifier = Modifier
              .background(TacticalEmeraldDark, RoundedCornerShape(4.dp))
              .border(1.dp, TacticalEmerald, RoundedCornerShape(4.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text("MERKLE VERIFIED", color = TacticalEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }
      }

      // 2. Profile Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
          border = BorderStroke(1.dp, SlateBorder),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(ElectricCyan.copy(alpha = 0.15f))
                .border(1.5.dp, ElectricCyan, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Person, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(28.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
              Text("Sundas", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
              Text("Cybersecurity Learner • SOC Analyst Track", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }

            Box(
              modifier = Modifier
                .background(ElectricCyanDark, RoundedCornerShape(6.dp))
                .border(1.dp, ElectricCyan, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text("Level 3", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }

      // 3. Skill Mastery & Evidence Checklist (Responsive Grid)
      item {
        if (isWideScreen) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            SkillMasteryBarsSection(modifier = Modifier.weight(1.2f))
            VerifiedEvidenceSection(modifier = Modifier.weight(1f))
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            SkillMasteryBarsSection(modifier = Modifier.fillMaxWidth())
            VerifiedEvidenceSection(modifier = Modifier.fillMaxWidth())
          }
        }
      }

      // 4. Career Explorer Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
          border = BorderStroke(1.dp, SlateBorder),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Career Explorer", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
              Spacer(modifier = Modifier.height(2.dp))
              Text("Explore roles, build skills, get hired.", color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }

            Button(
              onClick = {},
              colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text("View Opportunities", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SkillMasteryBarsSection(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text("Demonstrated Skill Mastery", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace)

      SkillProgressBar("SOC Analyst", 0.82f, "82%", ElectricCyan)
      SkillProgressBar("Incident Response", 0.71f, "71%", Color(0xFF38BDF8))
      SkillProgressBar("Network Security", 0.86f, "86%", TacticalEmerald)
      SkillProgressBar("Threat Intelligence", 0.63f, "63%", TacticalAmber)
    }
  }
}

@Composable
private fun SkillProgressBar(name: String, progress: Float, pct: String, color: Color) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(name, color = TextMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
      Text(pct, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    }
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
      color = color,
      trackColor = SlateBorder
    )
  }
}

@Composable
private fun VerifiedEvidenceSection(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("Verified Evidence", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace)

      EvidenceItem("SOC Investigation")
      EvidenceItem("PCAP Analysis")
      EvidenceItem("Malware Analysis")
      EvidenceItem("Web Security Lab")
      EvidenceItem("Incident Response Simulation")
    }
  }
}

@Composable
private fun EvidenceItem(title: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = TacticalEmerald, modifier = Modifier.size(16.dp))
    Text(title, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
  }
}
