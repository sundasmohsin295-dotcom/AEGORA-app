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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

@Composable
fun CoverageMapChecklistView(
  modifier: Modifier = Modifier
) {
  val domains = AegoraRepository.domainCoverageAreas

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Differentiator Header Banner
    CyberCard(
      borderColor = CyberCyan.copy(alpha = 0.5f),
      backgroundColor = CyberSurface
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "AEGORA 360° HANDS-ON COVERAGE MAP",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = CyberCyan
          )
          Text(
            text = "Every Reference Platform Mechanic Tied Into One Legible Career Story",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Unlike fragmented siloed training sites where CTFs, terminal practice, and alert triage sit in disconnected accounts, AEGORA aggregates every completed packet into your verifiable Skill Graph and Evidence Passport.",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )
    }

    // 2. Comprehensive Domain Matrix
    Text(
      text = "REFERENCE PLATFORMS & HANDS-ON DOMAIN CHECKLIST",
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = TextSecondaryDark
    )

    domains.forEach { dom ->
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = CyberSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                when (dom.domainId) {
                  "soc_blue" -> Icons.Default.Shield
                  "dfir_forensics" -> Icons.Default.Search
                  "linux_windows_ladders" -> Icons.Default.Terminal
                  "web_appsec_ladder" -> Icons.Default.Language
                  "appsec_patch_workflow" -> Icons.Default.Build
                  "binary_exploitation" -> Icons.Default.Memory
                  "mobile_security" -> Icons.Default.Smartphone
                  "iot_scada_ot" -> Icons.Default.Sensors
                  else -> Icons.Default.Cloud
                },
                contentDescription = null,
                tint = CyberCyan,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = dom.domainName,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
            }

            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberEmerald.copy(alpha = 0.2f)
            ) {
              Text(
                text = dom.statusBadge,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                color = CyberEmerald,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Reference Mechanic: ", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            Text(dom.referencePlatform, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = CyberAmber)
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = dom.handsOnMechanic,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "MITRE: ${dom.mitreMapping}",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = CyberCyan
            )
            Text(
              text = "${dom.coveredModulesCount} Active Modules",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberGold
            )
          }
        }
      }
    }
  }
}
