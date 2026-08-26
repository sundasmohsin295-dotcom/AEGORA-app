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
import com.example.model.ThreatActorDossier
import com.example.model.ThreatAdvisory
import com.example.ui.components.CodeTerminalView
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

enum class IntelTab {
  CVE_ADVISORIES, THREAT_ACTORS, MITRE_MATRIX
}

@Composable
fun IntelligenceScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var activeTab by remember { mutableStateOf(IntelTab.CVE_ADVISORIES) }
  val advisories = AegoraRepository.threatAdvisories
  val actors = AegoraRepository.threatActors

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
            text = "Threat Intelligence Hub",
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
      // Header
      item {
        CyberCard(
          borderColor = CyberCrimson.copy(alpha = 0.4f),
          backgroundColor = CyberSurface
        ) {
          Text(
            text = "GLOBAL THREAT INTELLIGENCE",
            style = MaterialTheme.typography.labelSmall,
            color = CyberCrimson
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Live CVEs & Adversary Profiles",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimaryDark
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Curated real-time advisories, CVSS v3 ratings, detection engineering Sigma rules, and nation-state threat actor playbooks.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )
        }
      }

      // Tab selector
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IntelTab.entries.forEach { tab ->
            val isSelected = tab == activeTab
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { activeTab = tab }
                .border(1.dp, if (isSelected) CyberCyan else CyberBorder, RoundedCornerShape(8.dp)),
              color = if (isSelected) CyberSurfaceElevated else CyberSurface
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (tab) {
                    IntelTab.CVE_ADVISORIES -> "CVE Advisories"
                    IntelTab.THREAT_ACTORS -> "Threat Actors"
                    IntelTab.MITRE_MATRIX -> "MITRE ATT&CK"
                  },
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (isSelected) CyberCyan else TextSecondaryDark
                )
              }

            }
          }
        }
      }

      when (activeTab) {
        IntelTab.CVE_ADVISORIES -> {
          items(advisories) { advisory ->
            AdvisoryCard(advisory = advisory)
          }
        }

        IntelTab.THREAT_ACTORS -> {
          items(actors) { actor ->
            ActorCard(actor = actor)
          }
        }

        IntelTab.MITRE_MATRIX -> {
          item {
            CyberSectionHeader(
              title = "Enterprise ATT&CK Matrix",
              subtitle = "14 Tactics & Detection Coverage"
            )

            val tactics = listOf(
              "TA0001 Initial Access (92% Coverage)",
              "TA0002 Execution (88% Coverage)",
              "TA0003 Persistence (74% Coverage)",
              "TA0004 Privilege Escalation (80% Coverage)",
              "TA0005 Defense Evasion (65% Coverage)",
              "TA0006 Credential Access (78% Coverage)",
              "TA0008 Lateral Movement (70% Coverage)",
              "TA0010 Exfiltration (82% Coverage)"
            )

            CyberCard {
              tactics.forEach { tactic ->
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = CyberSurfaceElevated,
                  border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(tactic, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark)
                    Icon(Icons.Default.Verified, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(16.dp))
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdvisoryCard(advisory: ThreatAdvisory, modifier: Modifier = Modifier) {
  CyberCard(
    modifier = modifier,
    borderColor = if (advisory.cvssScore >= 9.0) CyberCrimson.copy(alpha = 0.6f) else CyberAmber.copy(alpha = 0.6f)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(4.dp),
        color = CyberCrimson.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCrimson)
      ) {
        Text(
          text = "${advisory.cveId} • CVSS ${advisory.cvssScore} ${advisory.severity.uppercase()}",
          style = MaterialTheme.typography.labelSmall,
          color = CyberCrimson,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
      Text(
        text = advisory.publishedDate,
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryDark
      )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = advisory.title,
      style = MaterialTheme.typography.titleLarge,
      color = TextPrimaryDark
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = advisory.summary,
      style = MaterialTheme.typography.bodyMedium,
      color = TextSecondaryDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    Surface(
      shape = RoundedCornerShape(8.dp),
      color = CyberSurfaceElevated,
      border = androidx.compose.foundation.BorderStroke(1.dp, CyberIndigo)
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Text("Detection Signature / Action:", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
        Spacer(modifier = Modifier.height(4.dp))
        Text(advisory.detectionRule, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark)
      }
    }
  }
}

@Composable
fun ActorCard(actor: ThreatActorDossier, modifier: Modifier = Modifier) {
  CyberCard(modifier = modifier, borderColor = CyberIndigo.copy(alpha = 0.6f)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = actor.name,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = CyberCyan
      )
      Text(
        text = "Origin: ${actor.country}",
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryDark
      )
    }

    Spacer(modifier = Modifier.height(2.dp))

    Text(
      text = "Aliases: ${actor.aliases.joinToString(", ")}",
      style = MaterialTheme.typography.labelSmall,
      color = TextTertiaryDark
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = actor.description,
      style = MaterialTheme.typography.bodyMedium,
      color = TextSecondaryDark
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
      text = "TARGETED SECTORS",
      style = MaterialTheme.typography.labelSmall,
      color = CyberGold
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      actor.targetedSectors.forEach { sector ->
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = CyberSurfaceElevated
        ) {
          Text(
            text = sector,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
