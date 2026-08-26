package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.AegoraRepository
import com.example.model.UserRole
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

@Composable
fun ProfileAndSettingsScreen(
  onNavigateBack: () -> Unit,
  onRestartOnboarding: () -> Unit,
  onNavigateToVault: () -> Unit,
  onNavigateToCommunity: () -> Unit,
  onNavigateToUniversityAdmin: () -> Unit,
  onNavigateToAuth: () -> Unit = {},
  onNavigateToCognitiveProfile: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val userProfile by AegoraRepository.userProfile.collectAsState()
  var offlineCacheEnabled by remember { mutableStateOf(true) }
  var privacyModeEnabled by remember { mutableStateOf(true) }
  var hapticFeedbackEnabled by remember { mutableStateOf(true) }
  var showExportConfirmation by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      Surface(
        color = CyberBackground,
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Operator Profile & Hubs",
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
      // Profile Info Card
      item {
        CyberCard(
          borderColor = CyberBorderSubtle,
          backgroundColor = VibrantPurpleContainer,
          shapeRadius = 24.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.8f))
                .border(2.dp, VibrantPurpleOnContainer, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = VibrantPurpleOnContainer, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
              Text(userProfile.callsign, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = VibrantPurpleOnContainer)
              Text("ID: ${userProfile.passportId}", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = VibrantPurpleOnContainer.copy(alpha = 0.8f))
              Text("Active Role: ${userProfile.role.label}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = VibrantPurpleOnContainer)
            }
          }
        }
      }

      // Feature Portals & Hubs
      item {
        CyberSectionHeader(
          title = "Cyber Operator Hubs",
          subtitle = "Access Knowledge Vault, Community & University portals"
        )
      }

      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Zero-Trust Security Sentinel
          CyberCard(
            borderColor = CyberCyan.copy(alpha = 0.5f),
            backgroundColor = VibrantBlueContainer.copy(alpha = 0.4f),
            shapeRadius = 18.dp,
            onClick = onNavigateToAuth
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(CyberCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text("Zero-Trust Sentinel & Passkeys", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                Text("Hardware TEE enclave diagnostics & biometric attestation", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
              }
              Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan)
            }
          }

          // Cognitive Intelligence Profile
          CyberCard(
            borderColor = CyberViolet.copy(alpha = 0.5f),
            backgroundColor = VibrantPurpleContainer.copy(alpha = 0.4f),
            shapeRadius = 18.dp,
            onClick = onNavigateToCognitiveProfile
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(CyberViolet.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text("Cognitive Intelligence Profile", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                Text("Mistake DNA, bias analysis & reasoning graphs", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
              }
              Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberViolet)
            }
          }

          // Knowledge Vault
          CyberCard(
            borderColor = CyberBorderSubtle,
            backgroundColor = VibrantAmberContainer.copy(alpha = 0.35f),
            shapeRadius = 18.dp,
            onClick = onNavigateToVault
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(VibrantAmberContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Bookmark, contentDescription = null, tint = VibrantAmberOnContainer, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text("Knowledge Vault & Spaced Reviews", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                Text("Manage personal notes, bookmarked lessons & flashcards", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
              }
              Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
            }
          }

          // Community Hub
          CyberCard(
            borderColor = CyberBorderSubtle,
            backgroundColor = VibrantBlueContainer.copy(alpha = 0.35f),
            shapeRadius = 18.dp,
            onClick = onNavigateToCommunity
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(VibrantBlueContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Forum, contentDescription = null, tint = VibrantBlueOnContainer, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text("Cyber Community Hub", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                Text("Topic discussion rooms, case debates & peer help", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
              }
              Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
            }
          }

          // University & Admin
          CyberCard(
            borderColor = CyberBorderSubtle,
            backgroundColor = VibrantEmeraldContainer.copy(alpha = 0.35f),
            shapeRadius = 18.dp,
            onClick = onNavigateToUniversityAdmin
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(VibrantEmeraldContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.School, contentDescription = null, tint = VibrantEmeraldOnContainer, modifier = Modifier.size(24.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text("University Portal & Admin Mode", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                Text("Academic cohorts, student roster, role switcher & telemetry", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
              }
              Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
            }
          }
        }
      }

      // System Preferences
      item {
        CyberSectionHeader(
          title = "System & Ecosystem Preferences",
          subtitle = "Configure local storage, telemetry & offline sync"
        )

        CyberCard(
          borderColor = CyberBorderSubtle,
          backgroundColor = CyberSurfaceVariant,
          shapeRadius = 20.dp
        ) {
          // Offline cache
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Offline Lab & Threat Cache", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
              Text("Store MITRE database, Wireshark PCAPs & lessons on-device", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            }
            Switch(
              checked = offlineCacheEnabled,
              onCheckedChange = { offlineCacheEnabled = it }
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CyberBorderSubtle)

          // Privacy mode
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Zero-Telemetry Private Mode", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
              Text("Do not send anonymized telemetry or training logs", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            }
            Switch(
              checked = privacyModeEnabled,
              onCheckedChange = { privacyModeEnabled = it }
            )
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CyberBorderSubtle)

          // Haptics
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Terminal Haptic Pulses", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
              Text("Tactile feedback on CTF flag capture & log triage", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            }
            Switch(
              checked = hapticFeedbackEnabled,
              onCheckedChange = { hapticFeedbackEnabled = it }
            )
          }
        }
      }

      // Actions
      item {
        CyberSectionHeader(
          title = "Data & Account Management",
          subtitle = "Export or recalibrate"
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedButton(
            onClick = { showExportConfirmation = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantPurpleOnContainer),
            border = BorderStroke(1.dp, VibrantPurpleContainer),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Export Full Skill & Evidence Ledger (JSON)", fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onRestartOnboarding,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantAmberOnContainer),
            border = BorderStroke(1.dp, VibrantAmberContainer),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Recalibrate Target Role & Onboarding", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }

  if (showExportConfirmation) {
    AlertDialog(
      onDismissRequest = { showExportConfirmation = false },
      title = { Text("Export Successful", color = TextPrimaryDark, fontWeight = FontWeight.Bold) },
      text = { Text("Cryptographic JSON ledger exported: aegora_evidence_ledger_${userProfile.passportId}.json", color = TextSecondaryDark) },
      confirmButton = {
        Button(
          onClick = { showExportConfirmation = false },
          colors = ButtonDefaults.buttonColors(containerColor = VibrantPurpleContainer)
        ) {
          Text("Done", color = VibrantPurpleOnContainer, fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}

