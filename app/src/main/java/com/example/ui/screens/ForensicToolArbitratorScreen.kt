package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.AuthoritativeVerificationHandler
import com.example.security.SignedProofDossier
import com.example.subscription.AegoraSubscriptionRepository
import com.example.ui.components.SubscriptionPaywallDialog
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ForensicInvestigationCase(
  val id: String,
  val toolName: String,
  val category: String,
  val incidentContext: String,
  val rawTelemetryLogs: String,
  val aiSummaryClaim: String,
  val containsHallucination: Boolean,
  val hallucinationDetail: String,
  val groundTruthEvidence: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForensicToolArbitratorScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val subscriptionState by AegoraSubscriptionRepository.subscriptionState.collectAsState()
  var showPaywall by remember { mutableStateOf(false) }

  val featureAccess = remember(subscriptionState) {
    AegoraSubscriptionRepository.checkFeatureAccess("FORENSIC_ARBITRATOR")
  }

  val cases = remember {
    listOf(
      ForensicInvestigationCase(
        id = "case_bloodhound_01",
        toolName = "BloodHound Neo4j",
        category = "Active Directory Graph Path",
        incidentContext = "Domain Privilege Escalation Path Audit across CORP.LOCAL",
        rawTelemetryLogs = """
          MATCH p = shortestPath((u:User {name:"J.DOE@CORP.LOCAL"})-[*1..5]->(g:Group {name:"DOMAIN ADMINS"}))
          RETURN p;
          
          PATH NODES:
          1. (User: J.DOE) -> [MemberOf] -> (Group: IT_SUPPORT)
          2. (Group: IT_SUPPORT) -> [GenericAll] -> (Computer: JUMP-SRV-02.CORP.LOCAL)
          3. (Computer: JUMP-SRV-02) -> [UnconstrainedDelegation: TRUE]
          4. (Session: DA_SERVICE) -> [LoggedOn] -> (Computer: JUMP-SRV-02)
          5. (DA_SERVICE) -> [MemberOf] -> (Group: DOMAIN ADMINS)
        """.trimIndent(),
        aiSummaryClaim = "AI Analyst Finding: 'J.DOE has direct Domain Admin membership through group inheritance without requiring lateral movement or ticket extraction.'",
        containsHallucination = true,
        hallucinationDetail = "AI HALLUCINATION: J.DOE does NOT possess direct group inheritance to Domain Admins. J.DOE has GenericAll over JUMP-SRV-02, which possesses Unconstrained Delegation. Attack requires coercing DA_SERVICE authentication (e.g. PrinterBug) to harvest Kerberos TGT tickets from LSASS.",
        groundTruthEvidence = "Graph requires Kerberos TGT harvesting via unconstrained delegation relay, not direct group membership inheritance."
      ),
      ForensicInvestigationCase(
        id = "case_wireshark_02",
        toolName = "Wireshark PCAP",
        category = "Network Packet & Protocol Analysis",
        incidentContext = "Outbound anomalous DNS telemetry stream from internal host 172.16.4.22",
        rawTelemetryLogs = """
          Frame 4102: 128 bytes on wire (1024 bits), 128 bytes captured
          Ethernet II, Src: 00:0c:29:4f:8e:12, Dst: 00:50:56:fd:21:aa
          Internet Protocol Version 4, Src: 172.16.4.22, Dst: 8.8.8.8
          User Datagram Protocol, Src Port: 53211, Dst: 53
          Domain Name System (query)
              Queries:
                  aW5maWwucGFzc3dk.c2-beacon-infra.io: type TXT, class IN
                  Payload (Base64 decoded): 'infil.passwd'
              Query Frequency: 48 queries / second (Jitter: 0.02s)
        """.trimIndent(),
        aiSummaryClaim = "AI Analyst Finding: 'Standard DNS lookup traffic for cloud telemetry health-checks. No exfiltration detected.'",
        containsHallucination = true,
        hallucinationDetail = "AI HALLUCINATION: High-frequency (48 q/sec) TXT queries with Base64 encoded labels ('infil.passwd') represent active DNS Tunneling Data Exfiltration (MITRE T1071.004).",
        groundTruthEvidence = "Subdomain strings encode file contents directly into TXT queries to bypass stateful firewalls."
      ),
      ForensicInvestigationCase(
        id = "case_sysmon_03",
        toolName = "Sysmon & EDR Logs",
        category = "Host Volatile Artifacts",
        incidentContext = "Process execution and DLL image loading alert on Workstation WS-FIN-08",
        rawTelemetryLogs = """
          <Event xmlns="http://schemas.microsoft.com/win/2004/08/events/event">
            <System>
              <EventID>1</EventID>
              <TimeCreated SystemTime="2026-09-12T02:14:02.112Z"/>
            </System>
            <EventData>
              <Data Name="Image">C:\Windows\System32\WindowsPowerShell\v1.0\powershell.exe</Data>
              <Data Name="CommandLine">powershell.exe -w hidden -c "[Ref].Assembly.GetType('System.Management.Automation.AmsiUtils').GetField('amsiInitFailed','NonPublic,Static').SetValue(${'$'}null,${'$'}true)"</Data>
              <Data Name="ParentImage">C:\Windows\System32\cmd.exe</Data>
              <Data Name="IntegrityLevel">High</Data>
            </EventData>
          </Event>
        """.trimIndent(),
        aiSummaryClaim = "AI Analyst Finding: 'Administrative diagnostic PowerShell script verifying Windows PowerShell SDK initialization status.'",
        containsHallucination = true,
        hallucinationDetail = "AI HALLUCINATION: The command line reflects in-memory Antimalware Scan Interface (AMSI) memory patch evasion (setting amsiInitFailed to true) immediately prior to malicious reflective DLL loading.",
        groundTruthEvidence = "Classic Matt Graeber AMSI memory patch syntax targeting AMSI telemetry suppression."
      )
    )
  }

  var selectedCase by remember { mutableStateOf(cases.first()) }
  var isArbitrating by remember { mutableStateOf(false) }
  var arbitrationOutcome by remember { mutableStateOf<String?>(null) }
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
            modifier = Modifier.testTag("arbitrator_back_btn")
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
              text = "FORENSIC TOOL TELEMETRY ARBITRATOR",
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
              text = "CROSS-CORROBORATE RAW RUNTIME LOGS AGAINST AI CLAIMS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                lineHeight = 13.sp
              ),
              color = TextTertiaryDark
            )
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
      // 1. Paywall Notice if not Pro
      if (!featureAccess.granted) {
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = CyberSurfaceElevated,
            border = BorderStroke(1.dp, CyberAmber.copy(alpha = 0.6f)),
            modifier = Modifier
              .fillMaxWidth()
              .wrapContentHeight()
              .testTag("arbitrator_paywall_banner")
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
                  text = "Forensic arbitrator tooling requires PRO clearance ($19.99/mo). Preview mode active.",
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

      // 2. Tool Case Selector Tabs
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          cases.forEach { c ->
            val isSelected = c.id == selectedCase.id
            Surface(
              onClick = {
                selectedCase = c
                arbitrationOutcome = null
                signedProof = null
              },
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) CyberSurfaceElevated else CyberSurface,
              border = BorderStroke(1.dp, if (isSelected) CyberCyan else CyberBorder),
              modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .testTag("case_tab_${c.id}")
            ) {
              Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = c.toolName,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                  ),
                  color = if (isSelected) CyberCyan else TextPrimaryDark
                )
                Text(
                  text = c.category,
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.5.sp),
                  color = TextTertiaryDark,
                  maxLines = 1
                )
              }
            }
          }
        }
      }

      // 3. Raw Evidence Telemetry Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = CyberSurface),
          border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
          modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .testTag("raw_telemetry_terminal_card")
        ) {
          Column(
            modifier = Modifier
              .background(
                Brush.verticalGradient(listOf(CyberSurfaceElevated, CyberSurface))
              )
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Terminal, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "RAW FORENSIC TELEMETRY STREAM",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                  ),
                  color = CyberCyan
                )
              }

              Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.Black,
                border = BorderStroke(0.8.dp, CyberBorder)
              ) {
                Text(
                  text = selectedCase.toolName.uppercase(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.5.sp,
                    color = CyberEmerald
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = selectedCase.incidentContext,
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
              color = TextSecondaryDark
            )

            // Code / Log Block
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF060B12),
              border = BorderStroke(1.dp, CyberBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = selectedCase.rawTelemetryLogs,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 10.sp,
                  lineHeight = 15.sp
                ),
                color = Color(0xFF38BDF8)
              )
            }

            // AI Summary Claim with potential hallucination
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFF131127),
              border = BorderStroke(1.dp, CyberPurple.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "AI ANALYST TELEMETRY SUMMARY",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                      ),
                      color = CyberPurple
                    )
                  }
                  Text(
                    text = "CONFIDENCE: 91%",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 8.5.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    color = CyberAmber
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = selectedCase.aiSummaryClaim,
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = TextPrimaryDark
                  )
                )
              }
            }

            // Arbitrator Action Buttons
            if (arbitrationOutcome == null) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedButton(
                  onClick = {
                    coroutineScope.launch {
                      isArbitrating = true
                      delay(300)
                      isArbitrating = false
                      arbitrationOutcome = "MISTAKE: You accepted the AI summary. The AI hallucinated critical forensic context."
                    }
                  },
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                ) {
                  Text(
                    text = "ACCEPT AI SUMMARY",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  )
                }

                Button(
                  onClick = {
                    coroutineScope.launch {
                      isArbitrating = true
                      delay(320)
                      isArbitrating = false
                      arbitrationOutcome = "SUCCESS"
                      signedProof = AuthoritativeVerificationHandler.issueSignedProofDossier(
                        missionId = selectedCase.id,
                        missionTitle = "Forensic Arbitrator: ${selectedCase.toolName}",
                        aiConfidence = 91,
                        evidenceGrounding = 24,
                        cognitiveBias = "AUTOMATION_BIAS_SUPPRESSION",
                        capabilityChips = listOf("LOG_CORROBORATION: 95%", "HALLUCINATION_DETECTED", "FORENSIC_ACCURACY: 98%")
                      )
                    }
                  },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052D4), contentColor = Color.White),
                  modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .testTag("btn_flag_hallucination")
                ) {
                  if (isArbitrating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                  } else {
                    Text(
                      text = "🚩 FLAG HALLUCINATION",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.5.sp
                      )
                    )
                  }
                }
              }
            }

            // Resolution State
            AnimatedVisibility(visible = arbitrationOutcome != null) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                if (arbitrationOutcome == "SUCCESS") {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CyberEmerald.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, CyberEmerald),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = "AI HALLUCINATION CONFIRMED ✓",
                          style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.5.sp
                          ),
                          color = CyberEmerald
                        )
                      }
                      Spacer(modifier = Modifier.height(6.dp))
                      Text(
                        text = selectedCase.hallucinationDetail,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                        color = TextPrimaryDark
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "GROUND TRUTH: ${selectedCase.groundTruthEvidence}",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontSize = 9.sp
                        ),
                        color = CyberCyan
                      )
                    }
                  }

                  if (signedProof != null) {
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = Color(0xFF030712),
                      border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                          text = "IMMUTABLE FORENSIC PROOF ISSUED",
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                          ),
                          color = CyberEmerald
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                          text = "SHA-256: ${signedProof?.missionDigestSha256 ?: "N/A"}",
                          style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                          ),
                          color = CyberCyan
                        )
                      }
                    }
                  }
                } else {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = CyberCrimson.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, CyberCrimson),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = arbitrationOutcome ?: "",
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                      color = CyberCrimson,
                      modifier = Modifier.padding(12.dp)
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

  if (showPaywall) {
    SubscriptionPaywallDialog(
      currentSubscription = subscriptionState,
      onDismiss = { showPaywall = false }
    )
  }
}
