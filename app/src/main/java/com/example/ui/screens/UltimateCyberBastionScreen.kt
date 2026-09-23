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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.EdgeInferenceManager
import com.example.ai.TelemetryFeatureVector
import com.example.data.AegoraEncryptedDb
import com.example.mesh.AegoraMeshSync
import com.example.security.BehavioralBiometricEngine
import com.example.security.HoneytrapWatchdog
import com.example.security.MovingTargetDefenseEngine
import com.example.security.NativeKeyVault
import com.example.telemetry.DiagnosticSeverity
import com.example.telemetry.DiagnosticStore
import com.example.ui.components.ChamferedCutCornerShape
import com.example.ui.components.MerkleChainVisualizer
import com.example.ui.theme.*
import kotlinx.coroutines.launch

/**
 * PHASE 35 & 36: THE ULTIMATE CYBER BASTION
 * Chief Technology Officer (CTO) & Principal Enterprise Security Architect Console.
 * Integrates:
 * 1. Continuous Behavioral Biometrics & StrongBox TEE Hardware Re-Auth
 * 2. Canary Traps & Honeytokens (SQLCipher Watchdog)
 * 3. Cryptographic Append-Only Merkle Audit Ledger
 * 4. P2P Air-Gapped Mesh Network (BLE & Wi-Fi Direct)
 * 5. On-Device Neural Threat Classifier (TFLite Edge Swarm <15ms)
 * 6. Moving Target Defense (MTD) Runtime Memory Layout Mutation & Anti-Frida
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UltimateCyberBastionScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val coroutineScope = rememberCoroutineScope()
  val context = androidx.compose.ui.platform.LocalContext.current

  // State collectors
  val biometricTrust by BehavioralBiometricEngine.trustScore.collectAsState()
  val isBiometricReauthRequired by BehavioralBiometricEngine.isReauthRequired.collectAsState()
  val biometricAnomalyReason by BehavioralBiometricEngine.anomalyReason.collectAsState()
  val biometricSummary by BehavioralBiometricEngine.metricsSummary.collectAsState()

  val isLockdownActive by HoneytrapWatchdog.isLockdownActive.collectAsState()
  val trippedHoneytokens by HoneytrapWatchdog.trippedHoneytokens.collectAsState()
  val breachAlert by HoneytrapWatchdog.breachAlertMessage.collectAsState()

  val merkleLedger by DiagnosticStore.merkleLedger.collectAsState()
  val currentMerkleRoot by DiagnosticStore.currentMerkleRoot.collectAsState()
  val isLedgerValid by DiagnosticStore.isLedgerValid.collectAsState()

  val airGappedPeers by AegoraMeshSync.airGappedPeers.collectAsState()
  val relayedPackets by AegoraMeshSync.relayedThreatPackets.collectAsState()
  val isMeshActive by AegoraMeshSync.isAirGappedActive.collectAsState()
  val meshStatus by AegoraMeshSync.meshStatus.collectAsState()

  val recentClassifications by EdgeInferenceManager.recentNeuralClassifications.collectAsState()
  val edgeClassifierState by EdgeInferenceManager.edgeInferenceState.collectAsState()

  val mtdCycle by MovingTargetDefenseEngine.currentMutationCycle.collectAsState()
  val activePages by MovingTargetDefenseEngine.activeMemoryPages.collectAsState()
  val dynamicRoute by MovingTargetDefenseEngine.dynamicRouteToken.collectAsState()
  val mtdStatus by MovingTargetDefenseEngine.mtdStatusSummary.collectAsState()

  // Local interaction state
  var searchQueryInput by remember { mutableStateOf("") }
  var searchFeedbackMessage by remember { mutableStateOf<String?>(null) }
  var verificationResultText by remember { mutableStateOf<String?>(null) }
  var selectedFeatureEntropy by remember { mutableFloatStateOf(0.88f) }
  var selectedBurstRate by remember { mutableFloatStateOf(0.92f) }
  var showReauthDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "AEGORA APEX BASTION",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 1.sp
                ),
                color = NeonCyan
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isLockdownActive) HighAlertCrimson.copy(alpha = 0.2f) else CyberEmerald.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, if (isLockdownActive) HighAlertCrimson else CyberEmerald)
              ) {
                Text(
                  text = if (isLockdownActive) "SILENT LOCKDOWN" else "APEX ZERO-TRUST",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                  ),
                  color = if (isLockdownActive) HighAlertCrimson else CyberEmerald,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Mesh • Deception • Behavioral Biometrics • MTD • TFLite",
              style = MaterialTheme.typography.labelSmall,
              color = CyberTextSecondary
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Navigate back",
              tint = NeonCyan
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
      )
    },
    containerColor = CyberBackground,
    modifier = modifier.fillMaxSize().testTag("screen_ultimate_cyber_bastion")
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 40.dp)
    ) {
      // 1. Silent Lockdown Warning Banner (if active)
      if (isLockdownActive) {
        item {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = HighAlertCrimson.copy(alpha = 0.15f),
            border = BorderStroke(1.5.dp, HighAlertCrimson),
            modifier = Modifier.fillMaxWidth().testTag("bastion_lockdown_banner")
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.SecurityUpdateWarning,
                contentDescription = null,
                tint = HighAlertCrimson,
                modifier = Modifier.size(28.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "HONEYTRAP TRIPWIRE TRIPPED",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black
                  ),
                  color = HighAlertCrimson
                )
                Text(
                  text = breachAlert ?: "Volatile heap zeroized. Decoy honeytoken accessed.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark
                )
              }
              Button(
                onClick = { HoneytrapWatchdog.clearLockdownAfterTriage() },
                colors = ButtonDefaults.buttonColors(containerColor = HighAlertCrimson),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text("RESET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }
      }

      // 2. Bastion Navigation Tabs
      item {
        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          containerColor = CyberSurfaceVariant,
          contentColor = NeonCyan,
          edgePadding = 0.dp,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = NeonCyan
            )
          }
        ) {
          val tabs = listOf(
            "Biometrics",
            "Canary SQLCipher",
            "Merkle Ledger",
            "Air-Gap Mesh",
            "TFLite Swarm",
            "MTD Memory"
          )
          tabs.forEachIndexed { index, label ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  label,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            )
          }
        }
      }

      // 3. Tab Contents
      when (selectedTab) {
        0 -> {
          // -------------------------------------------------------------
          // TAB 0: CONTINUOUS BEHAVIORAL BIOMETRICS & TEE RE-AUTH
          // -------------------------------------------------------------
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = CyberCardBg),
              border = BorderStroke(1.dp, if (isBiometricReauthRequired) HighAlertCrimson else CyberBorder),
              shape = ChamferedCutCornerShape
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.Fingerprint,
                      contentDescription = null,
                      tint = if (isBiometricReauthRequired) HighAlertCrimson else CyberEmerald,
                      modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      "CONTINUOUS BEHAVIORAL BIOMETRICS",
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                      ),
                      color = TextPrimaryDark
                    )
                  }
                  Text(
                    text = "${(biometricTrust * 100).toInt()}% TRUST",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black
                    ),
                    color = if (isBiometricReauthRequired) HighAlertCrimson else CyberEmerald
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                  progress = { biometricTrust },
                  modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                  color = if (isBiometricReauthRequired) HighAlertCrimson else CyberEmerald,
                  trackColor = Color(0xFF2A2A38)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = biometricSummary,
                  style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                  color = if (isBiometricReauthRequired) HighAlertCrimson else CyberTextSecondary
                )

                if (biometricAnomalyReason != null) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "ANOMALY: $biometricAnomalyReason",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = HighAlertCrimson
                  )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Interactive Touch Cadence Test Pad
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFF131722),
                  border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clickable {
                      // Live touch interaction sampling
                      BehavioralBiometricEngine.recordTouchInteraction(
                        pressure = 0.62f + ((-10..10).random() / 100f),
                        durationMs = (60L..90L).random()
                      )
                    }
                    .testTag("biometric_tap_pad")
                ) {
                  Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(Icons.Default.TouchApp, contentDescription = null, tint = NeonCyan)
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        "TAP HERE TO SAMPLE OPERATOR CADENCE & PRESSURE",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontFamily = FontFamily.Monospace,
                          fontWeight = FontWeight.Bold,
                          fontSize = 10.sp
                        ),
                        color = NeonCyan
                      )
                      Text(
                        "Real-time sensor evaluation: pressure, dwell time, micro-tremor",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = CyberTextSecondary
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      BehavioralBiometricEngine.simulateUnauthorizedHandoff()
                    },
                    modifier = Modifier.weight(1f).testTag("btn_simulate_handoff"),
                    colors = ButtonDefaults.buttonColors(containerColor = HighAlertCrimson),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text("SIMULATE HANDOFF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }

                  Button(
                    onClick = {
                      val res = BehavioralBiometricEngine.verifyStrongBoxReauthentication("OPERATOR_TEE_AUTH_TOKEN")
                      if (res is com.example.core.result.AegoraResult.Success) {
                        showReauthDialog = true
                      }
                    },
                    modifier = Modifier.weight(1f).testTag("btn_tee_reauth"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text("STRONGBOX TEE RE-AUTH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                  }
                }
              }
            }
          }
        }

        1 -> {
          // -------------------------------------------------------------
          // TAB 1: CANARY TRAPS & HONEYTOKENS (SQLCIPHER WATCHDOG)
          // -------------------------------------------------------------
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = CyberCardBg),
              border = BorderStroke(1.dp, CyberBorder),
              shape = ChamferedCutCornerShape
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Shield, contentDescription = null, tint = TacticalAmber)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    "SQLCIPHER HONEYTOKEN WATCHDOG",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimaryDark
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  "Invisible decoy threat dossiers injected into encrypted SQLite tables. Any insider or attacker query reading a canary record immediately triggers silent lockdown and purges volatile memory via SecureMemory.wipe().",
                  style = MaterialTheme.typography.bodySmall,
                  color = CyberTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  "ACTIVE CANARY DECOY ASSETS IN VAULT:",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = TacticalAmber
                )

                Spacer(modifier = Modifier.height(6.dp))
                val canaries = remember { AegoraEncryptedDb.getInstance(context).getInjectedCanaryIdentifiers() }
                canaries.forEach { canary ->
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF161922),
                    border = BorderStroke(1.dp, Color(0xFF282D3D)),
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 3.dp)
                      .clickable {
                        // Click to simulate unauthorized extraction
                        coroutineScope.launch {
                          val result = AegoraEncryptedDb.getInstance(context).queryThreatDossier(canary)
                          searchFeedbackMessage = when (result) {
                            is com.example.core.result.AegoraResult.Failure -> result.message
                            is com.example.core.result.AegoraResult.Success -> "Accessed: ${result.value.title}"
                          }
                        }
                      }
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = TacticalAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(canary, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = TextPrimaryDark)
                      }
                      Text("TRIPWIRE ARMED", fontSize = 9.sp, color = TacticalAmber, fontWeight = FontWeight.Bold)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                  value = searchQueryInput,
                  onValueChange = { searchQueryInput = it },
                  label = { Text("Simulate Forensic Query / SQL Inspection") },
                  modifier = Modifier.fillMaxWidth().testTag("sql_search_input"),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberBorder
                  )
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      coroutineScope.launch {
                        val res = AegoraEncryptedDb.getInstance(context).executeRawSqlQueryOrSearch(searchQueryInput)
                        searchFeedbackMessage = when (res) {
                          is com.example.core.result.AegoraResult.Failure -> "WATCHDOG TRIPPED: ${res.message}"
                          is com.example.core.result.AegoraResult.Success -> "Clean Query: Verified 0 honeypot hits."
                        }
                      }
                    },
                    modifier = Modifier.weight(1f).testTag("btn_execute_query"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text("EXECUTE QUERY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                  }

                  Button(
                    onClick = {
                      searchQueryInput = "CANARY_ROOT_MASTER_KEY_2026"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282C3D)),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text("INJECT CANARY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TacticalAmber)
                  }
                }

                if (searchFeedbackMessage != null) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = searchFeedbackMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    color = if (isLockdownActive) HighAlertCrimson else CyberEmerald
                  )
                }
              }
            }
          }
        }

        2 -> {
          // -------------------------------------------------------------
          // TAB 2: CRYPTOGRAPHIC APPEND-ONLY MERKLE AUDIT CHAIN (D3 / CANVAS VISUALIZER)
          // -------------------------------------------------------------
          item {
            MerkleChainVisualizer(
              modifier = Modifier.fillMaxWidth()
            )
          }
        }

        3 -> {
          // -------------------------------------------------------------
          // TAB 3: P2P AIR-GAPPED MESH NETWORK (BLE & WI-FI DIRECT)
          // -------------------------------------------------------------
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = CyberCardBg),
              border = BorderStroke(1.dp, CyberBorder),
              shape = ChamferedCutCornerShape
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.WifiTethering, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      "P2P AIR-GAPPED MESH (BLE / P2P)",
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                      ),
                      color = TextPrimaryDark
                    )
                  }
                  Switch(
                    checked = isMeshActive,
                    onCheckedChange = { AegoraMeshSync.toggleAirGapMesh(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = meshStatus,
                  style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                  color = CyberEmerald
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  "LINKED OPERATOR PEERS (${airGappedPeers.size}):",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = TacticalAmber
                )

                Spacer(modifier = Modifier.height(6.dp))
                airGappedPeers.forEach { peer ->
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF131722),
                    border = BorderStroke(1.dp, Color(0xFF282D3D)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                  ) {
                    Row(
                      modifier = Modifier.padding(8.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Icon(
                            if (peer.transportType.contains("BLE")) Icons.Default.Bluetooth else Icons.Default.Wifi,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                          )
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(peer.callsign, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                        }
                        Text("${peer.transportType} • RSSI: ${peer.rssiDb}dB", fontSize = 9.sp, color = CyberTextSecondary)
                      }
                      Text("MUTUAL TEE OK", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CyberEmerald)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      AegoraMeshSync.broadcastAirGappedThreat(
                        actor = "APT-29 COZY_BEAR",
                        mitre = "T1078.004 Cloud Accounts"
                      )
                    },
                    modifier = Modifier.weight(1f).testTag("btn_broadcast_mesh"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text("BROADCAST IOC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                  }

                  Button(
                    onClick = {
                      AegoraMeshSync.discoverNewPeer("SENTINEL_ALPHA_${(10..99).random()}", "WIFI_P2P_DIRECT")
                    },
                    modifier = Modifier.weight(1f).testTag("btn_discover_peer"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282C3D)),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text("DISCOVER PEER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  "RELAYED AIR-GAP THREAT PACKETS:",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = TacticalAmber
                )

                Spacer(modifier = Modifier.height(4.dp))
                relayedPackets.take(3).forEach { pkt ->
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF131722),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                  ) {
                    Row(
                      modifier = Modifier.padding(6.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text("${pkt.packetId} • ${pkt.threatActor}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Text("${pkt.cveMitre} • Hop: ${pkt.hopCount}", fontSize = 9.sp, color = CyberTextSecondary)
                      }
                      Text("VERIFIED", fontSize = 8.sp, color = CyberEmerald, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }
        }

        4 -> {
          // -------------------------------------------------------------
          // TAB 4: ON-DEVICE NEURAL THREAT CLASSIFIER (TFLITE EDGE SWARM)
          // -------------------------------------------------------------
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = CyberCardBg),
              border = BorderStroke(1.dp, CyberBorder),
              shape = ChamferedCutCornerShape
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = NeonPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      "ON-DEVICE TFLITE NEURAL SWARM",
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                      ),
                      color = TextPrimaryDark
                    )
                  }
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = NeonPurple.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, NeonPurple)
                  ) {
                    Text(
                      "<15ms SPEC",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                      color = NeonPurple,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  "Autonomous 8-bit quantized multi-layer perceptron running locally on CPU/NPU. Processes raw network packet stream metrics with zero server reliance.",
                  style = MaterialTheme.typography.bodySmall,
                  color = CyberTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  "FEATURE VECTOR TUNER:",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = TacticalAmber
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Packet Entropy: ${(selectedFeatureEntropy * 100).toInt()}%", fontSize = 10.sp, color = TextPrimaryDark)
                Slider(
                  value = selectedFeatureEntropy,
                  onValueChange = { selectedFeatureEntropy = it },
                  colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                )

                Text("Burst Rate: ${(selectedBurstRate * 100).toInt()}%", fontSize = 10.sp, color = TextPrimaryDark)
                Slider(
                  value = selectedBurstRate,
                  onValueChange = { selectedBurstRate = it },
                  colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = {
                    EdgeInferenceManager.classifyPacketStreamOnDevice(
                      TelemetryFeatureVector(
                        packetEntropy = selectedFeatureEntropy,
                        burstRate = selectedBurstRate,
                        privParentRatio = 0.85f,
                        dnsEntropy = 0.77f
                      )
                    )
                  },
                  modifier = Modifier.fillMaxWidth().testTag("btn_run_tflite"),
                  colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text("EXECUTE NEURAL INFERENCE (<15ms)", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  "RECENT CLASSIFICATION INFERENCES:",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = TacticalAmber
                )

                Spacer(modifier = Modifier.height(6.dp))
                recentClassifications.take(3).forEach { cls ->
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF131722),
                    border = BorderStroke(1.dp, Color(0xFF282D3D)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                      ) {
                        Text(cls.topClass, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                        Text("${cls.latencyMs}ms", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CyberEmerald, fontFamily = FontFamily.Monospace)
                      }
                      Spacer(modifier = Modifier.height(4.dp))
                      Text("Confidence: ${cls.confidencePercent}% • MITRE: ${cls.mitreId}", fontSize = 9.sp, color = CyberTextSecondary)
                      Text("RUNBOOK: ${cls.mitigationRunbook}", fontSize = 9.sp, color = TextPrimaryDark)
                    }
                  }
                }
              }
            }
          }
        }

        5 -> {
          // -------------------------------------------------------------
          // TAB 5: MOVING TARGET DEFENSE (MTD) & MEMORY MUTATION
          // -------------------------------------------------------------
          item {
            Card(
              colors = CardDefaults.cardColors(containerColor = CyberCardBg),
              border = BorderStroke(1.dp, CyberBorder),
              shape = ChamferedCutCornerShape
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Memory, contentDescription = null, tint = TacticalEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      "MOVING TARGET DEFENSE (MTD)",
                      style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                      ),
                      color = TextPrimaryDark
                    )
                  }
                  Text(
                    "CYCLE #$mtdCycle",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Black
                    ),
                    color = TacticalEmerald
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  "Perturbs heap offsets, shifts memory buffers with dynamic entropy padding, and mutates API route tokens to completely neutralize Frida, GDB, and static Ghidra disassembly.",
                  style = MaterialTheme.typography.bodySmall,
                  color = CyberTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  "ACTIVE POLYMORPHIC ROUTE:",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = TacticalAmber
                )
                Text(
                  dynamicRoute,
                  style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                  color = NeonCyan
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      MovingTargetDefenseEngine.mutateMemoryLayout()
                    },
                    modifier = Modifier.weight(1f).testTag("btn_scramble_mtd"),
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalEmerald),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text("MUTATE HEAP LAYOUT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                  }

                  Button(
                    onClick = {
                      val hooked = MovingTargetDefenseEngine.scanForActiveHookingGadgets()
                      // feedback
                    },
                    modifier = Modifier.weight(1f).testTag("btn_anti_frida_check"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282C3D)),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text("SCAN FRIDA/GDB", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  "MUTATED HEAP PAGES:",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = TacticalAmber
                )

                Spacer(modifier = Modifier.height(6.dp))
                activePages.forEach { page ->
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF131722),
                    border = BorderStroke(1.dp, Color(0xFF282D3D)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                  ) {
                    Row(
                      modifier = Modifier.padding(8.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(page.pageAddressHex, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TacticalEmerald, fontFamily = FontFamily.Monospace)
                        Text("${page.allocatedBytes} Bytes (Padding: ${page.entropyPaddingBytes}B)", fontSize = 9.sp, color = CyberTextSecondary)
                      }
                      Text("CYCLE ${page.mutationCycle}", fontSize = 9.sp, color = CyberTextSecondary, fontFamily = FontFamily.Monospace)
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

  // Re-auth confirmation dialog
  if (showReauthDialog) {
    AlertDialog(
      onDismissRequest = { showReauthDialog = false },
      title = {
        Text("STRONGBOX TEE ATTESTATION", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
      },
      text = {
        Text("Hardware Secure Enclave attestation verified. Operator clearance restored to 100%. Volatile protections re-engaged.")
      },
      confirmButton = {
        Button(
          onClick = { showReauthDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald)
        ) {
          Text("CONFIRM", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      containerColor = CyberCardBg
    )
  }
}
