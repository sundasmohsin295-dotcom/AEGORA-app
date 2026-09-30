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
fun IncidentReportExecutiveScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showExportSuccess by remember { mutableStateOf(false) }

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
        .testTag("incident_report_executive_screen"),
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
              Text("SECURITY INCIDENT REPORT", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
              Text("Apr 26, 2025 09:41 AM", color = TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
          }

          Button(
            onClick = { showExportSuccess = true },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            shape = RoundedCornerShape(6.dp)
          ) {
            Icon(Icons.Default.Download, contentDescription = null, tint = ObsidianBackground, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Download Report", color = ObsidianBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
          }
        }
      }

      // 2. Incident Summary Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
          border = BorderStroke(1.dp, SlateBorder),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("INC-20481", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                Box(
                  modifier = Modifier
                    .background(HighAlertCrimsonDark, RoundedCornerShape(3.dp))
                    .border(1.dp, HighAlertCrimson, RoundedCornerShape(3.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text("CRITICAL", color = HighAlertCrimson, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text("Threat: Credential Compromise", color = TextMuted, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace)
            }

            Box(
              modifier = Modifier
                .background(TacticalEmeraldDark, RoundedCornerShape(4.dp))
                .border(1.dp, TacticalEmerald, RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text("Status: Contained", color = TacticalEmerald, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }

      // 3. Impact Metrics (3 Hosts, 1 Account, 0 Exfiltration)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          ImpactMetricCard("3", "Hosts Affected", Icons.Default.Computer, Modifier.weight(1f))
          ImpactMetricCard("1", "Compromised Account", Icons.Default.Person, Modifier.weight(1f))
          ImpactMetricCard("0", "Exfiltrated Data", Icons.Default.Shield, Modifier.weight(1f))
        }
      }

      // 4. Response Checklist & AI Assessment (Responsive Grid)
      item {
        if (isWideScreen) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            ExecutiveResponseChecklist(modifier = Modifier.weight(1.2f))
            ExecutiveAiAssessment(modifier = Modifier.weight(1f))
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ExecutiveResponseChecklist(modifier = Modifier.fillMaxWidth())
            ExecutiveAiAssessment(modifier = Modifier.fillMaxWidth())
          }
        }
      }
    }
  }

  if (showExportSuccess) {
    AlertDialog(
      onDismissRequest = { showExportSuccess = false },
      title = { Text("Report Generated & Sealed", color = ElectricCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
      text = {
        Text(
          "Cryptographically signed PDF summary for INC-20481 has been exported into the secure enclave cache.",
          color = TextPrimary,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
      },
      confirmButton = {
        Button(
          onClick = { showExportSuccess = false },
          colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
        ) {
          Text("Done", color = ObsidianBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        }
      },
      containerColor = ObsidianSurface,
      shape = RoundedCornerShape(10.dp)
    )
  }
}

@Composable
private fun ImpactMetricCard(
  value: String,
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(8.dp)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(22.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
      Text(label, color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
  }
}

@Composable
private fun ExecutiveResponseChecklist(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("Remediation Response", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace)

      ExecutiveCheckItem("Account suspended (jdoe)")
      ExecutiveCheckItem("Host isolated (WIN-SRV-03)")
      ExecutiveCheckItem("Malicious IP blocked (45.32.11.76)")
      ExecutiveCheckItem("Kerberos credentials rotated")
    }
  }
}

@Composable
private fun ExecutiveCheckItem(text: String) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TacticalEmerald, modifier = Modifier.size(16.dp))
    Text(text, color = TextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
  }
}

@Composable
private fun ExecutiveAiAssessment(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
    border = BorderStroke(1.dp, SlateBorder),
    shape = RoundedCornerShape(10.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Text("AI Assessment", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = FontFamily.Monospace)

      AssessmentBar("MITRE Alignment", 0.96f, "96%")
      AssessmentBar("Investigation Completeness", 0.91f, "91%")
    }
  }
}

@Composable
private fun AssessmentBar(label: String, progress: Float, pct: String) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(label, color = TextMuted, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
      Text(pct, color = TacticalEmerald, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
    }
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
      color = TacticalEmerald,
      trackColor = SlateBorder
    )
  }
}
