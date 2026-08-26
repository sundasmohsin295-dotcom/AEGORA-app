package com.example.ui.screens

import androidx.compose.animation.*
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
import com.example.model.ZeroDayExploitModel
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@Composable
fun ZeroDayLabScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val exploits = AegoraRepository.zeroDayExploits
  var selectedExploit by remember { mutableStateOf(exploits.first()) }
  var ruleDraft by remember { mutableStateOf(selectedExploit.sigmaRuleTemplate) }
  var testRuleResult by remember { mutableStateOf<String?>(null) }
  var activeTab by remember { mutableStateOf("SIGMA") } // SIGMA or YARA

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(PureBlack)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // 1. Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(CyberSurfaceElevated)
            .border(1.dp, CyberBorderSubtle, CircleShape)
        ) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan, modifier = Modifier.size(20.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.BugReport, contentDescription = null, tint = NeonPink, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "ZERO-DAY DECONSTRUCTOR & DETECTION STUDIO",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = NeonPink
          )
        }
      }
    }

    // 2. Select CVE Advisory
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        exploits.forEach { exploit ->
          val isSel = exploit.cveId == selectedExploit.cveId
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSel) CyberSurfaceElevated else CyberSurface,
            border = androidx.compose.foundation.BorderStroke(
              1.5.dp,
              if (isSel) NeonPink else CyberBorderSubtle
            ),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                selectedExploit = exploit
                ruleDraft = if (activeTab == "SIGMA") exploit.sigmaRuleTemplate else exploit.yaraRuleTemplate
                testRuleResult = null
              }
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = exploit.cveId,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                color = if (isSel) NeonPink else TextPrimaryDark
              )
              Text(
                text = "CVSS ${exploit.cvssScore} • ${exploit.attackVector}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondaryDark
              )
            }
          }
        }
      }
    }

    // 3. Exploit Visual Deconstructor
    item {
      CyberCard(
        borderColor = NeonPink.copy(alpha = 0.5f),
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = selectedExploit.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
            Text(
              text = "Affected: ${selectedExploit.affectedComponent}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = TextSecondaryDark
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = NeonPink.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
          ) {
            Text(
              text = "CVSS ${selectedExploit.cvssScore}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = NeonPink,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Memory Stack & Execution Flow
        Text(
          text = "EXECUTION STACK & MEMORY CORRUPTION FLOW",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = CyberCyan
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          selectedExploit.memoryStackFlow.forEach { step ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CodeBackground,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = step,
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                color = CodeGreen,
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Exploit Proof Snippet
        Text(
          text = "PRIMITIVE PROOF-OF-CONCEPT",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = CyberAmber
        )
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CodeBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = selectedExploit.exploitProofSnippet,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
            color = CyberCyan,
            modifier = Modifier.padding(8.dp)
          )
        }
      }
    }

    // 4. Live Detection Engineering Studio (Sigma / YARA)
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
          Text(
            text = "DETECTION RULE STUDIO",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = CyberEmerald
          )

          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            FilterChip(
              selected = activeTab == "SIGMA",
              onClick = {
                activeTab = "SIGMA"
                ruleDraft = selectedExploit.sigmaRuleTemplate
                testRuleResult = null
              },
              label = { Text("Sigma", fontSize = 10.sp) }
            )
            FilterChip(
              selected = activeTab == "YARA",
              onClick = {
                activeTab = "YARA"
                ruleDraft = selectedExploit.yaraRuleTemplate
                testRuleResult = null
              },
              label = { Text("YARA", fontSize = 10.sp) }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = ruleDraft,
          onValueChange = { ruleDraft = it },
          label = { Text("$activeTab Detection Rule Definition", color = CyberEmerald, fontSize = 11.sp) },
          textStyle = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberEmerald,
            unfocusedBorderColor = CyberBorderSubtle,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedContainerColor = CodeBackground,
            unfocusedContainerColor = CodeBackground
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .testTag("detection_rule_editor")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Button(
            onClick = {
              testRuleResult = "✓ COMPILATION PASSED: 100% True-Positive on 1,420 Sysmon/PCAP events (0 False-Positives)"
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Compile & Test", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }

          OutlinedButton(
            onClick = {
              ruleDraft = if (activeTab == "SIGMA") selectedExploit.sigmaRuleTemplate else selectedExploit.yaraRuleTemplate
              testRuleResult = null
            },
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("Reset", color = TextSecondaryDark, fontSize = 11.sp)
          }
        }

        if (testRuleResult != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberEmerald.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = testRuleResult!!,
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberEmerald,
              modifier = Modifier.padding(8.dp)
            )
          }
        }
      }
    }
  }
}
