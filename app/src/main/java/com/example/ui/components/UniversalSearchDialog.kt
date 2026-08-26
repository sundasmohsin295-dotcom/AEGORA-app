package com.example.ui.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AegoraRepository
import com.example.ui.theme.*

@Composable
fun UniversalSearchDialog(
  onDismiss: () -> Unit,
  onNavigateToLesson: (String) -> Unit,
  onNavigateToLab: () -> Unit,
  onNavigateToIntelligence: () -> Unit,
  onNavigateToCareer: () -> Unit,
  onNavigateToVault: () -> Unit = {},
  onNavigateToCommunity: () -> Unit = {},
  onNavigateToUniversity: () -> Unit = {}
) {
  var searchQuery by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = CyberSurface,
      border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(max = 540.dp),
      tonalElevation = 6.dp
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search CVEs, vault notes, flashcards, labs...", color = TextTertiaryDark, fontSize = 13.sp) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberCyan) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondaryDark)
              }
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("universal_search_input"),
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = CyberBorder,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark,
            focusedContainerColor = CyberSurfaceVariant,
            unfocusedContainerColor = CyberSurfaceVariant
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Command / Result List
        Text(
          text = if (searchQuery.isBlank()) "RECOMMENDED QUICK ACTIONS" else "SEARCH RESULTS",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = CyberCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          if (searchQuery.isBlank()) {
            item {
              SearchItemRow(
                icon = Icons.Default.Bookmark,
                title = "Open Knowledge Vault & Flashcards",
                category = "Vault & Spaced Repetition",
                chipColor = VibrantAmberContainer,
                chipTextColor = VibrantAmberOnContainer,
                onClick = {
                  onDismiss()
                  onNavigateToVault()
                }
              )
            }
            item {
              SearchItemRow(
                icon = Icons.Default.Forum,
                title = "Cyber Community & Discussions",
                category = "Topic Rooms & Q&A",
                chipColor = VibrantBlueContainer,
                chipTextColor = VibrantBlueOnContainer,
                onClick = {
                  onDismiss()
                  onNavigateToCommunity()
                }
              )
            }
            item {
              SearchItemRow(
                icon = Icons.Default.Terminal,
                title = "Launch Incident Simulator Lab",
                category = "Practice Lab",
                chipColor = VibrantBlueContainer,
                chipTextColor = VibrantBlueOnContainer,
                onClick = {
                  onDismiss()
                  onNavigateToLab()
                }
              )
            }
            item {
              SearchItemRow(
                icon = Icons.Default.School,
                title = "Sysmon Event ID 1 & PowerShell Triage",
                category = "Lesson Sprint",
                chipColor = VibrantPurpleContainer,
                chipTextColor = VibrantPurpleOnContainer,
                onClick = {
                  onDismiss()
                  onNavigateToLesson("les_102")
                }
              )
            }
          } else {
            val q = searchQuery.lowercase()
            if ("note".contains(q) || "vault".contains(q) || "flashcard".contains(q) || "spaced".contains(q) || "bookmark".contains(q)) {
              item {
                SearchItemRow(
                  icon = Icons.Default.Bookmark,
                  title = "Knowledge Vault & Spaced Reviews",
                  category = "Vault & Review Queue",
                  chipColor = VibrantAmberContainer,
                  chipTextColor = VibrantAmberOnContainer,
                  onClick = {
                    onDismiss()
                    onNavigateToVault()
                  }
                )
              }
            }
            if ("community".contains(q) || "forum".contains(q) || "discuss".contains(q) || "peer".contains(q)) {
              item {
                SearchItemRow(
                  icon = Icons.Default.Forum,
                  title = "Cyber Community Hub",
                  category = "Topic Discussions",
                  chipColor = VibrantBlueContainer,
                  chipTextColor = VibrantBlueOnContainer,
                  onClick = {
                    onDismiss()
                    onNavigateToCommunity()
                  }
                )
              }
            }
            if ("university".contains(q) || "cohort".contains(q) || "instructor".contains(q) || "admin".contains(q)) {
              item {
                SearchItemRow(
                  icon = Icons.Default.School,
                  title = "University & Admin Portal",
                  category = "Academic Mode",
                  chipColor = VibrantEmeraldContainer,
                  chipTextColor = VibrantEmeraldOnContainer,
                  onClick = {
                    onDismiss()
                    onNavigateToUniversity()
                  }
                )
              }
            }
            if ("powershell".contains(q) || "sysmon".contains(q) || "process".contains(q)) {
              item {
                SearchItemRow(
                  icon = Icons.Default.School,
                  title = "Sysmon Event ID 1 & Process Creation",
                  category = "Lesson Sprint",
                  chipColor = VibrantPurpleContainer,
                  chipTextColor = VibrantPurpleOnContainer,
                  onClick = {
                    onDismiss()
                    onNavigateToLesson("les_102")
                  }
                )
              }
            }
            if ("dns".contains(q) || "tunnel".contains(q) || "pcap".contains(q) || "network".contains(q)) {
              item {
                SearchItemRow(
                  icon = Icons.Default.School,
                  title = "DNS Telemetry & Tunneling Exfiltration",
                  category = "Lesson Sprint",
                  chipColor = VibrantPurpleContainer,
                  chipTextColor = VibrantPurpleOnContainer,
                  onClick = {
                    onDismiss()
                    onNavigateToLesson("les_101")
                  }
                )
              }
            }
            if ("cve".contains(q) || "threat".contains(q) || "xz".contains(q) || "moveit".contains(q)) {
              item {
                SearchItemRow(
                  icon = Icons.Default.BugReport,
                  title = "CVE-2024-3094 (CVSS 10.0)",
                  category = "Threat Intel",
                  chipColor = VibrantPinkContainer,
                  chipTextColor = VibrantPinkOnContainer,
                  onClick = {
                    onDismiss()
                    onNavigateToIntelligence()
                  }
                )
              }
            }
            if ("lab".contains(q) || "incident".contains(q) || "sim".contains(q) || "ctf".contains(q)) {
              item {
                SearchItemRow(
                  icon = Icons.Default.Terminal,
                  title = "APT29 Incident Simulator",
                  category = "Practice Lab",
                  chipColor = VibrantBlueContainer,
                  chipTextColor = VibrantBlueOnContainer,
                  onClick = {
                    onDismiss()
                    onNavigateToLab()
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun SearchItemRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  category: String,
  chipColor: androidx.compose.ui.graphics.Color = VibrantPurpleContainer,
  chipTextColor: androidx.compose.ui.graphics.Color = VibrantPurpleOnContainer,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = CyberSurfaceVariant,
    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(chipColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = chipTextColor, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = TextPrimaryDark)
        Text(category, style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
      }
      Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
    }
  }
}

