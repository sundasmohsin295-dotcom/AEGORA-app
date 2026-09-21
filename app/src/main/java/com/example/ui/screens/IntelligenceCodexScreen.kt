package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 2. Unified Intelligence Codex
 * This replaces infinite scrolling lists of text with a high-contrast, scannable grid
 * for tools and glossary terms.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntelligenceCodexScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDuel: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val obsidianBg = Color(0xFF090A0C)
  val matteSteel = Color(0xFF15171C)
  val slateBorder = Color(0xFF2D313A)
  val cobaltBlue = Color(0xFF2962FF)

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("ALL") }
  var activeDetailItem by remember { mutableStateOf<CodexItem?>(null) }

  val allItems = listOf(
    CodexItem("Wireshark", "Network Analysis", "Packet inspection, pcap dissections, TLS handshakes, and malicious TCP stream reassembly.", "T1040 Network Sniffing", "Blue Team / Forensics"),
    CodexItem("BloodHound", "Active Directory", "Graph-based domain privilege escalation path reconnaissance and ACL abuse visualizer.", "T1069.002 Domain Groups", "Red Team / AD Sec"),
    CodexItem("CSRF", "Web Vulnerability", "Cross-Site Request Forgery tricking authenticated sessions into dispatching unwanted state changes.", "CWE-352 / OWASP A01", "Web Application Sec"),
    CodexItem("Cobalt Strike", "Adversary Sim", "Commercial post-exploitation adversary emulator with Malleable C2 and Beacon in-memory loaders.", "T1071 Application Layer Protocol", "Red Team / APT Sim"),
    CodexItem("Ghidra", "Reverse Engineering", "NSA-developed open-source software reverse engineering suite supporting decompiler and IL disassembly.", "T1027 Binary Obfuscation", "Malware Analysis"),
    CodexItem("Volatility", "Memory Forensics", "Advanced memory extraction and volatility profile analysis for discovering inject code and unlinked DLLs.", "T1055 Process Injection", "DFIR / Incident Response"),
    CodexItem("Mimikatz", "Credential Extraction", "Extracts plaintext passwords, kerberos tickets, and NTLM hashes directly from LSASS memory space.", "T1003.001 LSASS Memory", "Adversary TTP"),
    CodexItem("Sigma", "Detection Engineering", "Generic, open-source rule format for defining log detections regardless of SIEM backend vendor.", "Defense Evasion Detection", "Detection Engineering"),
    CodexItem("Burp Suite", "Web Penetration", "Industry standard HTTP proxy for intercepting requests, automating fuzzing, and auditing APIs.", "OWASP Top 10 Audit", "AppSec"),
    CodexItem("YARA", "Malware Signature", "Rule-based identification tool classifying malware families through hex, string, and regex conditions.", "T1204 User Execution", "SOC / Threat Hunting")
  )

  val filteredItems = allItems.filter { item ->
    val matchesCategory = when (selectedCategory) {
      "NETWORK" -> item.second.contains("Network", ignoreCase = true)
      "AD" -> item.second.contains("Active Directory", ignoreCase = true)
      "MALWARE" -> item.second.contains("Adversary", ignoreCase = true) || item.second.contains("Reverse", ignoreCase = true) || item.second.contains("Memory", ignoreCase = true)
      "WEB" -> item.second.contains("Web", ignoreCase = true)
      else -> true
    }
    val matchesSearch = item.first.contains(searchQuery, ignoreCase = true) ||
      item.second.contains(searchQuery, ignoreCase = true) ||
      item.description.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesSearch
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(obsidianBg)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier.size(36.dp).testTag("codex_back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text("UNIFIED INTELLIGENCE CODEX", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Text("HIGH-CONTRAST SCANNABLE ARTIFACTS", color = Color(0xFF8A919E), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
      }

      if (onNavigateToDuel != null) {
        Surface(
          onClick = onNavigateToDuel,
          shape = RoundedCornerShape(8.dp),
          color = cobaltBlue.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, cobaltBlue)
        ) {
          Text(
            text = "DUEL ARENA",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
          )
        }
      }
    }

    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Filter tools, MITRE techniques, CVEs...", fontSize = 12.sp, color = Color(0xFF8A919E)) },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF8A919E), modifier = Modifier.size(18.dp))
      },
      singleLine = true,
      shape = RoundedCornerShape(8.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = matteSteel,
        unfocusedContainerColor = matteSteel,
        focusedBorderColor = cobaltBlue,
        unfocusedBorderColor = slateBorder,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
      ),
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 44.dp)
        .testTag("codex_search_input")
    )

    // Filter Chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      listOf("ALL", "NETWORK", "MALWARE", "WEB", "AD").forEach { cat ->
        val isSelected = selectedCategory == cat
        Surface(
          onClick = { selectedCategory = cat },
          shape = RoundedCornerShape(6.dp),
          color = if (isSelected) cobaltBlue else matteSteel,
          border = BorderStroke(1.dp, if (isSelected) cobaltBlue else slateBorder)
        ) {
          Text(
            text = cat,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    // High-Contrast Scannable 2-Column Grid
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .testTag("codex_grid"),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredItems.size) { index ->
        val item = filteredItems[index]
        Card(
          colors = CardDefaults.cardColors(containerColor = matteSteel),
          shape = RoundedCornerShape(8.dp),
          border = BorderStroke(1.dp, slateBorder),
          modifier = Modifier
            .clickable { activeDetailItem = item }
            .wrapContentHeight()
            .testTag("codex_card_${item.first.lowercase()}")
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // High-Contrast Tool Indicator Badge
            Box(
              modifier = Modifier
                .size(24.dp)
                .background(Color(0xFF2D313A), RoundedCornerShape(4.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = item.first.take(1),
                color = Color(0xFF00E676),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
              )
            }
            Text(item.first, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(item.second, color = Color(0xFF8A919E), fontSize = 10.sp)
          }
        }
      }
    }
  }

  // Detail Modal Bottom Sheet
  activeDetailItem?.let { item ->
    ModalBottomSheet(
      onDismissRequest = { activeDetailItem = null },
      containerColor = matteSteel,
      shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
      modifier = Modifier.testTag("codex_bottom_sheet")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
          .padding(bottom = 32.dp)
          .testTag("codex_bottom_sheet_content"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(item.first, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(item.second, color = Color(0xFF00E676), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0x222962FF),
            border = BorderStroke(1.dp, cobaltBlue)
          ) {
            Text(
              text = item.domain,
              color = Color.White,
              fontSize = 10.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        HorizontalDivider(color = slateBorder)

        Text("OPERATIONAL DEFINITION", color = Color(0xFF8A919E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(
          text = item.description,
          color = Color(0xFFE2E8F0),
          fontSize = 13.sp,
          lineHeight = 19.sp
        )

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color.Black,
          border = BorderStroke(1.dp, slateBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("MITRE REFERENCE:", color = Color(0xFF8A919E), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            Text(item.mitreRef, color = Color(0xFF00E676), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
        }

        Button(
          onClick = { activeDetailItem = null },
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.buttonColors(containerColor = cobaltBlue),
          modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
          Text("CLOSE ARTIFACT", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

private data class CodexItem(
  val first: String,
  val second: String,
  val description: String,
  val mitreRef: String,
  val domain: String
)
