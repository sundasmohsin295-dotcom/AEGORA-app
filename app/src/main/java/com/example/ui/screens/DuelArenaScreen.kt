package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.EdgeInferenceManager
import com.example.audio.CyberSonificationManager
import com.example.data.AegoraRepository
import com.example.hardware.DynamicIconManager
import com.example.subscription.AegoraSubscriptionRepository
import com.example.ui.components.MatrixRainCanvas
import com.example.ui.components.PalantirMatteButton
import com.example.ui.components.ShimmerBox
import com.example.ui.components.shimmerEffect
import com.example.model.DuelScenario
import com.example.model.ScapyParsedPacket
import com.example.viewmodel.DuelArenaViewModel
import com.example.viewmodel.DuelIntent
import com.example.util.SocPdfReportGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 1. The Async Adversary Duel Arena
 * This screen forces the user to analyze raw telemetry and decide if the AI is hallucinating,
 * maintaining absolute visual focus on the terminal and the claim.
 * Connected to RevenueCat logic to enforce the paywall after free tier duels.
 */
@Composable
fun DuelArenaScreen(
  onNavigateBack: () -> Unit,
  onShowPaywall: () -> Unit,
  modifier: Modifier = Modifier
) {
  val obsidianBg = Color(0xFF090A0C)
  val matteSteel = Color(0xFF15171C)
  val slateBorder = Color(0xFF2D313A)
  val cobaltBlue = Color(0xFF2962FF)

  val haptic = LocalHapticFeedback.current
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  val sonificationManager = remember { CyberSonificationManager.getInstance(context) }
  var isGeigerMuted by remember { mutableStateOf(false) }
  var isExportingPdf by remember { mutableStateOf(false) }

  val subscriptionState by AegoraSubscriptionRepository.subscriptionState.collectAsState()
  val duelsEngaged by AegoraSubscriptionRepository.adversaryDuelsEngaged.collectAsState()

  val isPro = AegoraSubscriptionRepository.canAccessAdversaryDuel() ||
    subscriptionState.entitlementIdentifiers.contains(AegoraSubscriptionRepository.ENTITLEMENT_PRO)

  var isLoading by remember { mutableStateOf(false) }
  val snackbarHostState = remember { SnackbarHostState() }

  val viewModel = remember { DuelArenaViewModel(context) }
  val uiState by viewModel.viewState.collectAsState()
  val duelSeeds = DuelArenaViewModel.DEFAULT_SCENARIOS

  val activeSeedIndex = uiState.activeSeedIndex
  val currentScenario = uiState.scenario
  val hasDecodedPayload = uiState.hasDecodedPayload
  val duelOutcome = uiState.duelOutcome
  val isCorrectDecision = uiState.isCorrectDecision
  val showPcapTrace = uiState.showPcapTrace
  val isStreamingPcap = uiState.isStreamingPcap
  val streamedPackets = uiState.streamedPackets

  // Edge AI Neural Fallback Check
  val isDeviceOffline = remember(activeSeedIndex) { !EdgeInferenceManager.isOnline(context) }
  val edgeAiInference = remember(activeSeedIndex, isDeviceOffline) {
    if (isDeviceOffline) {
      EdgeInferenceManager.inferEdgeTelemetry(
        telemetryLog = currentScenario.rawTelemetry,
        adversary = currentScenario.adversary,
        threatScore = currentScenario.threatScore
      )
    } else null
  }

  // Active claim text: dynamically shows offline Edge Neural engine text if offline
  val effectiveAiClaim = edgeAiInference?.analysisSummary ?: currentScenario.aiClaim

  // Cyber Sonification (The Geiger Counter Effect):
  // Frequency/speed accelerates mathematically as ThreatScore approaches 100/100
  DisposableEffect(currentScenario.threatScore, isGeigerMuted) {
    sonificationManager.setMuted(isGeigerMuted)
    sonificationManager.startGeigerMonitoring(currentScenario.threatScore)
    onDispose {
      sonificationManager.stopGeigerMonitoring()
    }
  }

  // Dynamic App Icon integration: Updates launcher icon state based on threat severity
  LaunchedEffect(currentScenario.threatScore) {
    DynamicIconManager.updateIconForThreatScore(context, currentScenario.threatScore)
  }

  fun exportDossierPdf() {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    isExportingPdf = true
    coroutineScope.launch {
      delay(200)
      val reportData = SocPdfReportGenerator.DossierData(
        reportId = currentScenario.id,
        adversaryName = currentScenario.adversary,
        aiClaim = effectiveAiClaim,
        threatScore = currentScenario.threatScore,
        killChainStage = currentScenario.mitreKillChainStage,
        forensicEvidence = if (showPcapTrace && streamedPackets.isNotEmpty()) {
          streamedPackets.take(5).joinToString("\n") { 
            "#${it.number} [${it.timestamp}] ${it.protocol} | ${it.summary}" 
          }
        } else {
          currentScenario.rawTelemetry
        },
        aiAnalystVerdict = duelOutcome ?: currentScenario.explanation,
        isVerifiedThreat = !currentScenario.isAiHallucinating,
        isEdgeAiFallback = isDeviceOffline
      )

      val pdfFile = SocPdfReportGenerator.generateDossierPdf(context, reportData)
      isExportingPdf = false
      if (pdfFile != null) {
        SocPdfReportGenerator.shareDossier(context, pdfFile)
        snackbarHostState.showSnackbar("Dossier PDF Exported: ${pdfFile.name}")
      } else {
        snackbarHostState.showSnackbar("PDF Generation Encountered an Anomaly.")
      }
    }
  }

  fun ingestPcapTrace() {
    viewModel.processIntent(DuelIntent.TogglePcapTrace)
  }

  fun handleDuelDecision(userAcceptedClaim: Boolean) {
    // RevenueCat Gate Check
    if (!AegoraSubscriptionRepository.canAccessAdversaryDuel()) {
      onShowPaywall()
      return
    }

    // Record usage
    AegoraSubscriptionRepository.recordDuelEngaged()
    viewModel.processIntent(
      DuelIntent.SubmitDecision(
        claimedHallucination = !userAcceptedClaim,
        context = context
      )
    )
  }

  fun triggerSimulatedSync() {
    coroutineScope.launch {
      snackbarHostState.showSnackbar("Secure Connection Lost. Retrying...")
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(obsidianBg)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
    // Top Navigation & Header Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier.size(36.dp).testTag("duel_arena_back_button")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          "ACTIVE DUEL: ${currentScenario.id}",
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Geiger Sonification Audio Toggle
        IconButton(
          onClick = {
            isGeigerMuted = !isGeigerMuted
            sonificationManager.setMuted(isGeigerMuted)
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          },
          modifier = Modifier
            .size(32.dp)
            .testTag("geiger_audio_toggle_button")
        ) {
          Icon(
            if (isGeigerMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
            contentDescription = if (isGeigerMuted) "Unmute Geiger" else "Mute Geiger",
            tint = if (isGeigerMuted) Color(0xFF8A919E) else Color(0xFF34D399),
            modifier = Modifier.size(20.dp)
          )
        }

        // RevenueCat Entitlement & Quota Pill
        Surface(
          onClick = onShowPaywall,
          shape = RoundedCornerShape(12.dp),
          color = if (isPro) Color(0x222962FF) else Color(0x2200E676),
          border = BorderStroke(1.dp, if (isPro) cobaltBlue else Color(0x4400E676)),
          modifier = Modifier.testTag("duel_paywall_pill")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              if (isPro) Icons.Default.WorkspacePremium else Icons.Default.Bolt,
              contentDescription = null,
              tint = if (isPro) cobaltBlue else Color(0xFF00E676),
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = if (subscriptionState.tier != com.example.model.SubscriptionTier.FREE) "PRO • UNLIMITED" else "${2 - duelsEngaged}/2 DUELS LEFT",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }

    // Phase 26: UDF State Invariance Watchdog Verification Badge
    Surface(
      shape = RoundedCornerShape(6.dp),
      color = if (uiState.isWatchdogHealthy) Color(0x1500E676) else Color(0x22FF1744),
      border = BorderStroke(1.dp, if (uiState.isWatchdogHealthy) Color(0x3300E676) else Color(0x66FF1744)),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("state_watchdog_indicator")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = if (uiState.isWatchdogHealthy) Icons.Default.Shield else Icons.Default.Warning,
            contentDescription = "State Watchdog",
            tint = if (uiState.isWatchdogHealthy) Color(0xFF00E676) else Color(0xFFFF1744),
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "STATE WATCHDOG: [SHA-256: ${uiState.stateHash.take(8).uppercase()}] • THREAT: ${uiState.threatScore}/100",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (uiState.isWatchdogHealthy) Color(0xFFD1D5DB) else Color(0xFFFF80AB)
          )
        }
        Text(
          text = if (uiState.lastRehydrationTimestamp > 0) "[SELF-HEALED DB]" else "[INVARIANT VERIFIED]",
          fontSize = 9.sp,
          fontFamily = FontFamily.Monospace,
          color = Color(0xFF00E676)
        )
      }
    }

    // Adversary Selector Strip
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      duelSeeds.forEachIndexed { index, scenario ->
        val selected = index == activeSeedIndex
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (selected) cobaltBlue.copy(alpha = 0.25f) else matteSteel,
          border = BorderStroke(1.dp, if (selected) cobaltBlue else slateBorder),
          modifier = Modifier
            .weight(1f)
            .clickable {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              viewModel.processIntent(DuelIntent.LoadScenario(index))
            }
        ) {
          Text(
            text = scenario.id.removePrefix("SEED_"),
            color = if (selected) Color.White else Color(0xFF8A919E),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(vertical = 6.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    }

    // Raw SIEM Telemetry Terminal
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(Color.Black, RoundedCornerShape(8.dp))
        .border(1.dp, slateBorder, RoundedCornerShape(8.dp))
        .padding(16.dp)
        .testTag("raw_siem_terminal")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = if (showPcapTrace) "PCAP // SCAPY 2.5 DISSECTION" else "SIEM // EDR TELEMETRY STREAM",
              color = if (showPcapTrace) Color(0xFF22D3EE) else Color(0xFF8A919E),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            if (isStreamingPcap) {
              ShimmerBox(
                modifier = Modifier
                  .width(28.dp)
                  .height(10.dp),
                shape = RoundedCornerShape(2.dp),
                baseColor = Color(0xFF0E2238),
                highlightColor = Color(0xFF22D3EE)
              )
            }
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Button: [ INGEST .PCAP TRACE ]
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = if (showPcapTrace) Color(0x3322D3EE) else Color(0x222962FF),
              border = BorderStroke(1.dp, if (showPcapTrace) Color(0xFF22D3EE) else Color(0xFF2962FF)),
              modifier = Modifier
                .clickable {
                  haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                  if (!showPcapTrace) {
                    ingestPcapTrace()
                  } else {
                    showPcapTrace = false
                  }
                }
                .testTag("duel_ingest_pcap_btn")
            ) {
              Text(
                text = if (showPcapTrace) "[ SHOW SIEM ]" else "[ INGEST .PCAP TRACE ]",
                color = if (showPcapTrace) Color(0xFF22D3EE) else Color(0xFF82B1FF),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            Text(
              text = if (hasDecodedPayload) "[ HIDE DECODED ]" else "[ DECODE BASE64 ]",
              color = Color(0xFF00E676),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              modifier = Modifier.clickable {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                viewModel.processIntent(DuelIntent.TogglePayloadDecode)
              }
            )
          }
        }

        if (showPcapTrace) {
          // PCAP Streamed Output
          Text(
            text = "> SCAPY CAPTURE FILE: /traces/${currentScenario.id.lowercase()}.pcap\n" +
              "> DETERMINISTIC DISSECTION STREAM INGESTED (${streamedPackets.size}/${currentScenario.pcapPackets.size} FRAMES)",
            color = Color(0xFF22D3EE),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp
          )

          streamedPackets.forEach { pkt ->
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (pkt.isSuspicious) Color(0x22FF1744) else Color(0x1522D3EE),
              border = BorderStroke(0.8.dp, if (pkt.isSuspicious) Color(0x88FF1744) else Color(0x4422D3EE)),
              modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
            ) {
              Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "FRAME #${pkt.number} • ${pkt.timestamp} • ${pkt.protocol} ${pkt.flags}",
                    color = if (pkt.isSuspicious) Color(0xFFFF5252) else Color(0xFF67E8F9),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = pkt.layers,
                    color = Color(0xFF8A919E),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }

                Text(
                  text = "ETH: ${pkt.ethSrc} -> ${pkt.ethDst} | IP: ${pkt.ipSrc} -> ${pkt.ipDst}",
                  color = Color(0xFFB0BEC5),
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )

                Text(
                  text = "TCP: ${pkt.summary}",
                  color = Color(0xFFE0E0E0),
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )

                if (pkt.payloadHex.isNotBlank()) {
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0x33000000),
                    border = BorderStroke(0.5.dp, Color(0x33FFFFFF)),
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp)
                  ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                      Text(
                        text = "HEX: ${pkt.payloadHex}",
                        color = Color(0xFF8A919E),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                      )
                      Text(
                        text = "ASCII: ${pkt.payloadAscii}",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                  }
                }

                if (pkt.isSuspicious) {
                  Text(
                    text = "🚨 SCAPY FORENSICS: C2 BEACON STAGER DETECTED (MITRE T1071.001)",
                    color = Color(0xFFFF5252),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
            }
          }

          if (isStreamingPcap) {
            Text(
              text = "> [INGESTING NEXT PCAP FRAME VIA SCAPY ENGINE...]",
              color = Color(0xFF82B1FF),
              fontFamily = FontFamily.Monospace,
              fontSize = 10.sp
            )
          }
        } else {
          // Standard SIEM EDR Text
          Text(
            text = currentScenario.rawTelemetry,
            color = Color(0xFF00E676),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 18.sp
          )

          AnimatedVisibility(visible = hasDecodedPayload) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0x2200E676),
              border = BorderStroke(0.8.dp, Color(0x5500E676)),
              modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
              Text(
                text = currentScenario.decodedPayload,
                color = Color(0xFFB9F6CA),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
          }
        }
      }
    }

    // AI Analyst Claim Card with MITRE ATT&CK Kill Chain
    Card(
      colors = CardDefaults.cardColors(containerColor = matteSteel),
      shape = RoundedCornerShape(8.dp),
      border = BorderStroke(1.dp, if (isLoading) Color(0xFF34D399) else slateBorder),
      modifier = Modifier
        .wrapContentHeight()
        .testTag("ai_analyst_claim_card")
    ) {
      Box(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              if (isLoading) "NEURAL PROCESSING // MITRE ATT&CK" 
              else if (isDeviceOffline) "EDGE AI HEURISTIC ENGINE // OFFLINE" 
              else "AI ANALYST CLAIM",
              color = if (isLoading) Color(0xFF34D399) 
                else if (isDeviceOffline) Color(0xFFF59E0B) 
                else Color(0xFF8A919E),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Text(
              if (isLoading) "[FASTAPI STREAMING]" 
              else if (isDeviceOffline) "[AIR-GAPPED FALLBACK]" 
              else "CONFIDENCE: 94.2%",
              color = if (isLoading) Color(0xFF34D399) 
                else if (isDeviceOffline) Color(0xFFF59E0B) 
                else Color(0xFF2962FF),
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          // MITRE ATT&CK Horizontal Kill Chain Timeline (Recon -> Delivery -> Exploit -> C2)
          val killChainStages = listOf("Recon", "Delivery", "Exploit", "C2")
          val activeStage = currentScenario.mitreKillChainStage
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("mitre_kill_chain_timeline"),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            killChainStages.forEachIndexed { index, stage ->
              val isCurrentStage = stage.equals(activeStage, ignoreCase = true)
              Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(4.dp),
                color = if (isCurrentStage) Color(0x33EF4444) else Color(0x1F2D313A),
                border = BorderStroke(
                  1.dp,
                  if (isCurrentStage) Color(0xFFEF4444) else Color(0xFF2D313A)
                )
              ) {
                Box(
                  modifier = Modifier.padding(vertical = 5.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = stage.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = if (isCurrentStage) FontWeight.Black else FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                    color = if (isCurrentStage) Color(0xFFEF4444) else Color(0xFF8A919E)
                  )
                }
              }
              if (index < killChainStages.size - 1) {
                Text(
                  text = "›",
                  color = Color(0xFF4A5568),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Text(
            if (isLoading) "Intercepting neural inference stream from FastAPI defense backend..." else effectiveAiClaim,
            color = if (isLoading) Color(0xFFA7F3D0) else Color.White,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = if (isLoading) FontFamily.Monospace else FontFamily.Default
          )
        }

        // Dynamic Hacking Animation: Matrix-Style Rain Canvas while processing
        if (isLoading) {
          Box(
            modifier = Modifier
              .matchParentSize()
              .background(Color(0xCC090A0C))
          ) {
            MatrixRainCanvas(
              modifier = Modifier.fillMaxSize(),
              primaryColor = Color(0xFF34D399),
              leadColor = Color(0xFFE6FFFA)
            )
          }
        }
      }
    }

    // Duel Outcome Card if answered
    if (duelOutcome != null) {
      val isAiFailureDetected = duelOutcome?.contains("AI FAILURE DETECTED") == true
      val badgeAlpha by animateFloatAsState(
        targetValue = if (isAiFailureDetected) 1f else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "ai_failure_badge_alpha"
      )
      val badgeScale by animateFloatAsState(
        targetValue = if (isAiFailureDetected) 1f else 0.88f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "ai_failure_badge_scale"
      )

      Card(
        colors = CardDefaults.cardColors(
          containerColor = if (isCorrectDecision == true) Color(0x2200E676) else Color(0x22FF1744)
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isCorrectDecision == true) Color(0xFF00E676) else Color(0xFFFF1744)),
        modifier = Modifier.fillMaxWidth().testTag("duel_outcome_card")
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          if (isAiFailureDetected || (isCorrectDecision == true && !currentScenario.isAiHallucinating.not())) {
            Surface(
              color = Color(0x3300E676),
              shape = RoundedCornerShape(4.dp),
              border = BorderStroke(1.dp, Color(0xFF00E676)),
              modifier = Modifier
                .padding(bottom = 6.dp)
                .graphicsLayer(
                  alpha = badgeAlpha,
                  scaleX = badgeScale,
                  scaleY = badgeScale
                )
                .testTag("ai_failure_detected_badge")
            ) {
              Text(
                text = "[AI FAILURE DETECTED ✓]",
                color = Color(0xFF00E676),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = duelOutcome.orEmpty(),
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 17.sp
          )
        }
      }
    }

    // Phase 26: Watchdog Fault-Injection / Re-Hydration Integrity Trigger
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "INVARIANCE AUDIT: ${if (uiState.isWatchdogHealthy) "INTEGRITY 100% (ZERO DRIFT)" else "ANOMALY RECOVERY ACTIVE"}",
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        color = if (uiState.isWatchdogHealthy) Color(0xFF9CA3AF) else Color(0xFFF87171)
      )
      Text(
        text = "[SIMULATE REHYDRATION]",
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF38BDF8),
        modifier = Modifier
          .clickable {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            viewModel.simulateProcessDeath()
          }
          .testTag("simulate_process_death_button")
      )
    }

    // Action Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      PalantirMatteButton(
        text = "ACCEPT CLAIM",
        onClick = {
          if (!isLoading) {
            isLoading = true
            try {
              handleDuelDecision(userAcceptedClaim = true)
            } finally {
              isLoading = false
            }
          }
        },
        enabled = !isLoading,
        isLoading = isLoading,
        containerColor = matteSteel,
        contentColor = Color.White,
        borderColor = slateBorder,
        shape = RoundedCornerShape(8.dp),
        isMonospace = true,
        testTag = "duel_accept_claim_button",
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
      )

      PalantirMatteButton(
        text = "CHALLENGE AI",
        onClick = {
          if (!isLoading) {
            isLoading = true
            try {
              handleDuelDecision(userAcceptedClaim = false)
            } finally {
              isLoading = false
            }
          }
        },
        enabled = !isLoading,
        isLoading = isLoading,
        containerColor = cobaltBlue,
        contentColor = Color.White,
        borderColor = cobaltBlue,
        shape = RoundedCornerShape(8.dp),
        isMonospace = true,
        testTag = "duel_challenge_ai_button",
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
      )

      PalantirMatteButton(
        text = if (isExportingPdf) "GENERATING..." else "EXPORT DOSSIER",
        onClick = {
          if (!isExportingPdf) {
            exportDossierPdf()
          }
        },
        enabled = !isExportingPdf,
        isLoading = isExportingPdf,
        containerColor = Color(0xFF1B2332),
        contentColor = Color(0xFF38BDF8),
        borderColor = Color(0xFF0284C7),
        shape = RoundedCornerShape(8.dp),
        isMonospace = true,
        testTag = "export_dossier_button",
        modifier = Modifier
          .weight(1.2f)
          .height(50.dp)
      )
    }
  }

  // Sleek dark-themed Snackbar with Cobalt Blue text
  SnackbarHost(
    hostState = snackbarHostState,
    modifier = Modifier
      .align(Alignment.BottomCenter)
      .padding(16.dp)
      .testTag("duel_arena_snackbar_host")
  ) { data ->
    Snackbar(
      modifier = Modifier.border(BorderStroke(1.dp, slateBorder), RoundedCornerShape(8.dp)),
      containerColor = matteSteel,
      contentColor = cobaltBlue,
      shape = RoundedCornerShape(8.dp)
    ) {
      Text(
        text = data.visuals.message,
        color = cobaltBlue,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
      )
    }
  }
}
}
