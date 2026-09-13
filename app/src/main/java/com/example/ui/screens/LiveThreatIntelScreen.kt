package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SubscriptionTier
import com.example.security.AuthoritativeVerificationHandler
import com.example.security.SignedProofDossier
import com.example.subscription.AegoraSubscriptionRepository
import com.example.ui.components.SubscriptionPaywallDialog
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LiveThreatCveItem(
  val cveId: String,
  val cvssScore: Double,
  val severity: String,
  val affectedProduct: String,
  val publicationDate: String,
  val attackVector: String,
  val summary: String,
  val proposedMitigation: String,
  val intentionalAiFlaw: String,
  val flawExplanation: String,
  val correctMitigation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveThreatIntelScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val subscriptionState by AegoraSubscriptionRepository.subscriptionState.collectAsState()
  var showPaywall by remember { mutableStateOf(false) }

  val featureAccess = remember(subscriptionState) {
    AegoraSubscriptionRepository.checkFeatureAccess("LIVE_THREAT_INTEL")
  }

  val cveList = remember {
    listOf(
      LiveThreatCveItem(
        cveId = "CVE-2024-3094",
        cvssScore = 10.0,
        severity = "CRITICAL",
        affectedProduct = "xz-utils (liblzma 5.6.0 & 5.6.1)",
        publicationDate = "March 2024",
        attackVector = "Supply Chain Upstream / SSH Auth Hook",
        summary = "Malicious backdoor inserted into upstream tarballs intercepting RSA_public_decrypt inside OpenSSH sshd to grant pre-auth root RCE.",
        proposedMitigation = "AI Proposed Rule:\n# Block all port 22 incoming on primary gateway\niptables -A INPUT -p tcp --dport 22 -s 10.0.0.0/8 -j DROP",
        intentionalAiFlaw = "SYNTAX & SCOPE MISATTRIBUTION: Drops legitimate internal 10.0.0.0/8 cluster traffic while leaving external attacker proxy 198.51.100.44 unrestricted.",
        flawExplanation = "The AI Analyst prematurely panicked and severed internal administrative subnets. The true zero-day payload arrives via public-facing WAN SSH gateways using injected liblzma hooks.",
        correctMitigation = "Downgrade xz-utils to verified release 5.4.x, patch build pipelines with cryptographic reproducible builds, and inspect sshd systemd dependencies."
      ),
      LiveThreatCveItem(
        cveId = "CVE-2023-38606",
        cvssScore = 9.8,
        severity = "CRITICAL",
        affectedProduct = "Apple iOS / macOS XNU Kernel",
        publicationDate = "December 2023",
        attackVector = "Hardware MMIO Memory Register Bypass",
        summary = "Operation Triangulation kernel exploit chaining undocumented MMIO registers to bypass hardware memory page protection (PPL/SPTM).",
        proposedMitigation = "AI Proposed Rule:\n# Install standard EDR daemon and audit /Library/LaunchDaemons",
        intentionalAiFlaw = "DEFENSE INADEQUACY: Hardware-level MMIO registers cannot be audited by userland or basic EDR daemons once kernel integrity is compromised.",
        flawExplanation = "The AI assumed standard userland telemetry could remediate a silicon-level hardware register overwrite.",
        correctMitigation = "Deploy system firmware microcode patches, enforce Lockdown Mode, and isolate device for offline hardware forensic dump."
      ),
      LiveThreatCveItem(
        cveId = "CVE-2024-21413",
        cvssScore = 9.8,
        severity = "CRITICAL",
        affectedProduct = "Microsoft Outlook #MonikerLink",
        publicationDate = "February 2024",
        attackVector = "NTLM Credential Relay & Remote Code Execution",
        summary = "Bypasses Protected View via file:// links appending exclamation points (!), coercing SMB authentication to rogue attacker servers.",
        proposedMitigation = "AI Proposed Rule:\n# Filter email body for regex string 'http://*'",
        intentionalAiFlaw = "INCORRECT PROTOCOL REGEX: Exploit leverages file:// protocol Moniker handlers, not HTTP links.",
        flawExplanation = "The AI regex fails to catch file:///\\\\attacker-ip\\share!test SMB coercion payloads.",
        correctMitigation = "Block outbound TCP port 445 (SMB) at perimeter firewall, enforce NTLM disablement via Group Policy, and patch Outlook."
      )
    )
  }

  var selectedCve by remember { mutableStateOf(cveList.first()) }
  var isReconciling by remember { mutableStateOf(false) }
  var challengeResolved by remember { mutableStateOf(false) }
  var signedProof by remember { mutableStateOf<SignedProofDossier?>(null) }

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
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("threat_intel_back_btn")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = TextPrimaryDark
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
              text = "LIVE THREAT INTEL // ADVERSARY GENERATOR",
              style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                fontSize = 13.sp,
                lineHeight = 18.sp
              ),
              color = CyberCyan
            )
            Text(
              text = "STREAM: NVD / CISA KEV ACTIVE INGEST • ZERO-DAY TELEMETRY",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                lineHeight = 13.sp
              ),
              color = TextTertiaryDark
            )
          }
          Spacer(modifier = Modifier.weight(1f))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberEmerald.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(CyberEmerald)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "LIVE FEED",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 8.5.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberEmerald
              )
            }
          }
        }
      }
    },
    containerColor = CyberBackground
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      contentPadding = PaddingValues(top = 10.dp, bottom = 48.dp)
    ) {
      // 1. Feature Gate Notice if Free tier
      if (!featureAccess.granted) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = CyberSurfaceElevated,
            border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.6f)),
            modifier = Modifier
              .fillMaxWidth()
              .wrapContentHeight()
              .testTag("threat_intel_pro_paywall_banner")
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "PRO CLEARANCE REQUIRED",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                  ),
                  color = CyberAmber
                )
                Text(
                  text = "Live adversary generation & zero-day telemetry requires PRO clearance ($19.99/mo). Previewing triage mode.",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                  color = TextSecondaryDark
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = { showPaywall = true },
                colors = ButtonDefaults.buttonColors(containerColor = CyberAmber, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text("UPGRADE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
              }
            }
          }
        }
      }

      // 2. Real-Time CVE Feed Selector Carousel
      item {
        Text(
          text = "ACTIVE ZERO-DAY INGEST FEED",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            fontSize = 10.sp
          ),
          color = TextSecondaryDark
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          cveList.forEach { cve ->
            val isSelected = cve.cveId == selectedCve.cveId
            Surface(
              onClick = {
                selectedCve = cve
                challengeResolved = false
                signedProof = null
              },
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) CyberSurfaceElevated else CyberSurface,
              border = BorderStroke(
                1.dp,
                if (isSelected) CyberCyan else CyberBorder
              ),
              modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .testTag("cve_selector_${cve.cveId}")
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = cve.cveId,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.5.sp
                    ),
                    color = if (isSelected) CyberCyan else TextPrimaryDark
                  )
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CyberCrimson.copy(alpha = 0.2f),
                    border = BorderStroke(0.8.dp, CyberCrimson)
                  ) {
                    Text(
                      text = "${cve.cvssScore}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                      ),
                      color = CyberCrimson,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                  }
                }
                Text(
                  text = cve.affectedProduct,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, lineHeight = 12.sp),
                  color = TextTertiaryDark,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      // 3. Telemetry Dossier Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CyberSurface),
          border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .testTag("threat_dossier_card")
        ) {
          Column(
            modifier = Modifier
              .background(
                Brush.verticalGradient(
                  listOf(CyberSurfaceElevated, CyberSurface)
                )
              )
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = selectedCve.cveId,
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                  ),
                  color = TextPrimaryDark
                )
                Text(
                  text = selectedCve.attackVector,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = CyberAmber
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberCrimson.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, CyberCrimson)
              ) {
                Text(
                  text = "CVSS ${selectedCve.cvssScore} ${selectedCve.severity}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                  ),
                  color = CyberCrimson,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Text(
              text = selectedCve.summary,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
              color = TextSecondaryDark
            )

            HorizontalDivider(color = CyberBorder, thickness = 0.8.dp)

            // AI Proposed Mitigation (Contains intentional hallucination / flaw)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF0D121F),
              border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "AI ANALYST PROPOSED MITIGATION",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                      ),
                      color = CyberAmber
                    )
                  }
                  Text(
                    text = "CONFIDENCE: 94%",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    ),
                    color = CyberAmber
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = selectedCve.proposedMitigation,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                  ),
                  color = Color(0xFFE2E8F0)
                )
              }
            }

            // Dual Telemetry Bar (AI Confidence vs Evidence Grounding)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "AI Confidence: 94%",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.5.sp),
                  color = CyberAmber
                )
                Text(
                  text = "Evidence Grounding: 31% (Gap: 63% - Hallucination Alert)",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.5.sp),
                  color = CyberCyan
                )
              }
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp))
                  .background(CyberBorder)
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.94f)
                    .background(CyberAmber)
                )
                Box(
                  modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.31f)
                    .background(CyberCyan)
                )
              }
            }

            // Interactive Challenge Trigger
            if (!challengeResolved) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedButton(
                  onClick = {
                    // Operator mistakenly trusts flawed AI
                  },
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                ) {
                  Text(
                    text = "APPROVE AS-IS",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    )
                  )
                }

                Button(
                  onClick = {
                    coroutineScope.launch {
                      isReconciling = true
                      delay(320) // Crisp 320ms telemetry reconciliation
                      isReconciling = false
                      challengeResolved = true
                      signedProof = AuthoritativeVerificationHandler.issueSignedProofDossier(
                        missionId = selectedCve.cveId,
                        missionTitle = "Mitigation Arbitration: ${selectedCve.cveId}",
                        aiConfidence = 94,
                        evidenceGrounding = 31,
                        cognitiveBias = "PREMATURE_CONCLUSION",
                        capabilityChips = listOf("ZERO_DAY_TRIAGE: 96%", "MITIGATION_VERIFIED", "AI_OVERSIGHT: 92%")
                      )
                    }
                  },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0052D4),
                    contentColor = Color.White
                  ),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("btn_challenge_ai_threat_intel")
                ) {
                  if (isReconciling) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RECONCILING...", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                  } else {
                    Text(
                      text = "⚡ CHALLENGE AI",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                      )
                    )
                  }
                }
              }
            }

            // Post-Challenge Revealed Status Banner & Proof Dossier
            AnimatedVisibility(visible = challengeResolved) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = CyberEmerald.copy(alpha = 0.15f),
                  border = BorderStroke(1.dp, CyberEmerald),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "AI EXPLOIT MISATTRIBUTION DETECTED ✓",
                        style = MaterialTheme.typography.labelMedium.copy(
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.Black,
                          fontSize = 11.5.sp
                        ),
                        color = CyberEmerald
                      )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "EVIDENCE VERIFIED ✓ | HUMAN DECISION CORRECT ✓",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                      ),
                      color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = selectedCve.flawExplanation,
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                      color = TextPrimaryDark
                    )
                  }
                }

                // Correct Verified Ground Truth
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = CyberSurfaceElevated,
                  border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      text = "AUTHORITATIVE MITIGATION STRATEGY:",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp
                      ),
                      color = CyberCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = selectedCve.correctMitigation,
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                      color = TextPrimaryDark
                    )
                  }
                }

                // Cryptographic Proof Receipt
                if (signedProof != null) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF030712),
                    border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Text(
                        text = "IMMUTABLE ENCLAVE PROOF RECEIPT",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.Bold,
                          fontSize = 8.5.sp
                        ),
                        color = CyberEmerald
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = "SHA-256 Digest: ${signedProof!!.missionDigestSha256}",
                        style = MaterialTheme.typography.bodySmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontSize = 9.sp
                        ),
                        color = CyberCyan
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = "HMAC Signature: ${signedProof!!.cryptographicSignature.take(24)}...",
                        style = MaterialTheme.typography.bodySmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontSize = 8.5.sp
                        ),
                        color = TextTertiaryDark
                      )
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

  if (showPaywall) {
    SubscriptionPaywallDialog(
      currentSubscription = subscriptionState,
      onDismiss = { showPaywall = false }
    )
  }
}
