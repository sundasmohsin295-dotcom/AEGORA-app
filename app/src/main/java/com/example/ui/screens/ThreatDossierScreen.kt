package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.AccessibilityShield

data class ThreatDossierItem(
  val id: String,
  val adversaryName: String,
  val ipAddress: String,
  val cveIdentifier: String,
  val threatScore: String,
  val vector: String,
  val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatDossierScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isMasked by AccessibilityShield.isMaskingActive.collectAsState()
  val detectedService by AccessibilityShield.detectedUntrustedService.collectAsState()
  val isSimulated by AccessibilityShield.isSimulationActive.collectAsState()

  // Ensure real-time evaluation
  LaunchedEffect(Unit) {
    AccessibilityShield.evaluateAccessibility(context)
  }

  val obsidianBg = Color(0xFF090A0C)
  val cardBg = Color(0xFF13161C)
  val borderCol = Color(0xFF262C38)
  val neonGreen = Color(0xFF00E676)
  val warningAmber = Color(0xFFF59E0B)
  val dangerRed = Color(0xFFEF4444)

  val threatItems = remember {
    listOf(
      ThreatDossierItem(
        id = "TH-9021",
        adversaryName = "VOLT_TYPHOON_PROXY_NODE",
        ipAddress = "198.51.100.45:443",
        cveIdentifier = "CVE-2024-38077 (RDL Remote Execution)",
        threatScore = "CVSS 9.8 [CRITICAL]",
        vector = "Post-Quantum TLS Probe via Non-DoH Port",
        status = "NEUTRALIZED"
      ),
      ThreatDossierItem(
        id = "TH-9022",
        adversaryName = "SANDWORM_COGNITIVE_BOT",
        ipAddress = "203.0.113.88:8080",
        cveIdentifier = "CVE-2024-21413 (Outlook Moniker Exploit)",
        threatScore = "CVSS 9.1 [CRITICAL]",
        vector = "Steganographic Token Injection",
        status = "SCRUBBED"
      ),
      ThreatDossierItem(
        id = "TH-9023",
        adversaryName = "EQUATION_SHADOW_RELAY",
        ipAddress = "45.33.32.156:5353",
        cveIdentifier = "CVE-2024-30078 (Wi-Fi Driver Elevation)",
        threatScore = "CVSS 8.8 [HIGH]",
        vector = "Plaintext DNS Eavesdropping Attempt",
        status = "BLOCKED_VIA_DOH"
      ),
      ThreatDossierItem(
        id = "TH-9024",
        adversaryName = "LAZARUS_SWAP_EXTRACTOR",
        ipAddress = "185.220.101.5:9001",
        cveIdentifier = "CVE-2023-45866 (Bluetooth Keystroke Hijack)",
        threatScore = "CVSS 7.5 [MEDIUM]",
        vector = "Kernel Paging & zRAM Inspection",
        status = "LOCKED_VIA_MLOCK"
      )
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "THREAT DOSSIER // INTEL",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color.White
            )
            Text(
              text = "PHASE 25: GHOST PROTOCOL ANTI-FORENSICS",
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp,
              color = neonGreen
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("threat_dossier_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = Color.White
            )
          }
        },
        actions = {
          FilledTonalButton(
            onClick = { AccessibilityShield.toggleSimulation(context) },
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = if (isMasked) warningAmber.copy(alpha = 0.2f) else Color(0xFF1E293B),
              contentColor = if (isMasked) warningAmber else Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Icon(
              imageVector = if (isMasked) Icons.Default.Warning else Icons.Default.Shield,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isSimulated) "SCRAPER: ACTIVE" else "SIMULATE SCRAPER",
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = obsidianBg
        )
      )
    },
    containerColor = obsidianBg,
    modifier = modifier.fillMaxSize()
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Accessibility Alert Banner
      item {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isMasked) warningAmber.copy(alpha = 0.12f) else neonGreen.copy(alpha = 0.08f),
          border = BorderStroke(1.dp, if (isMasked) warningAmber else neonGreen.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Icon(
              imageVector = if (isMasked) Icons.Default.Warning else Icons.Default.Security,
              contentDescription = null,
              tint = if (isMasked) warningAmber else neonGreen,
              modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (isMasked) "ACCESSIBILITY OVERRIDE: DATA MASKED" else "ACCESSIBILITY SHIELD: CLEAN ENVIRONMENT",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (isMasked) warningAmber else neonGreen
              )
              Text(
                text = if (isMasked)
                  "Hostile screen-reader/malware detected: ${detectedService ?: "Untrusted Accessibility Service"}. Sensitive IPs, CVEs, and Threat Scores redacted."
                else
                  "All active accessibility services are verified against system whitelist (Google TalkBack). Telemetry visible to authorized operator.",
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                modifier = Modifier.padding(top = 2.dp)
              )
            }
          }
        }
      }

      // Security Protocol Enclaves
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = cardBg),
          border = BorderStroke(1.dp, borderCol),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "ACTIVE GHOST ENCLAVES",
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = Color(0xFF94A3B8)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("DoH Stealth Routing:", fontSize = 11.sp, color = Color.White)
              Text("Cloudflare 1.1.1.1 (TLS)", fontSize = 11.sp, color = neonGreen, fontFamily = FontFamily.Monospace)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("True-Time Clock Sync:", fontSize = 11.sp, color = Color.White)
              Text("Authoritative Date (<5m skew)", fontSize = 11.sp, color = neonGreen, fontFamily = FontFamily.Monospace)
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Kernel Memory Lock:", fontSize = 11.sp, color = Color.White)
              Text("mlock() + MADV_DONTDUMP", fontSize = 11.sp, color = neonGreen, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }

      // Tactical Threat Items List
      items(threatItems) { item ->
        Card(
          colors = CardDefaults.cardColors(containerColor = cardBg),
          border = BorderStroke(1.dp, if (isMasked) warningAmber.copy(alpha = 0.4f) else borderCol),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = item.adversaryName,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
              )
              Surface(
                color = Color(0x2210B981),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, Color(0xFF10B981))
              ) {
                Text(
                  text = item.status,
                  fontSize = 9.sp,
                  fontFamily = FontFamily.Monospace,
                  color = Color(0xFF34D399),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            HorizontalDivider(color = borderCol.copy(alpha = 0.5f))

            // IP Address (Redacted if untrusted accessibility active)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Target IP Address:", fontSize = 11.sp, color = Color(0xFF94A3B8))
              Text(
                text = AccessibilityShield.maskSensitive(item.ipAddress, isMasked),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMasked) warningAmber else Color(0xFF38BDF8)
              )
            }

            // CVE (Redacted if untrusted accessibility active)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Vulnerability CVE:", fontSize = 11.sp, color = Color(0xFF94A3B8))
              Text(
                text = AccessibilityShield.maskSensitive(item.cveIdentifier, isMasked),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMasked) warningAmber else dangerRed
              )
            }

            // Threat Score (Redacted if untrusted accessibility active)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Threat Severity Index:", fontSize = 11.sp, color = Color(0xFF94A3B8))
              Text(
                text = AccessibilityShield.maskSensitive(item.threatScore, isMasked),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMasked) warningAmber else neonGreen
              )
            }

            // Infiltration Vector
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Observed Vector:", fontSize = 11.sp, color = Color(0xFF94A3B8))
              Text(
                text = item.vector,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color(0xFFCBD5E1)
              )
            }
          }
        }
      }
    }
  }
}
