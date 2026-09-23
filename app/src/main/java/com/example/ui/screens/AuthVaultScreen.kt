package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.result.AegoraResult
import com.example.data.AegoraEncryptedDb
import com.example.layers.LayersExperimentManager
import com.example.security.SecureMemory
import com.example.ui.components.HackerErrorStateComponent
import com.example.ui.components.TacticalPanel
import com.example.ui.components.TacticalStatusLed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.HighAlertCrimson
import com.example.ui.theme.HighAlertCrimsonDark
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianSurfaceRaised
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateBorderBright
import com.example.ui.theme.TacticalAmber
import com.example.ui.theme.TacticalEmerald
import com.example.ui.theme.TextDim
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextTerminalGreen
import kotlinx.coroutines.launch

@Composable
fun AuthVaultScreen(
  onNavigateBack: () -> Unit,
  claimedSessionId: String? = null,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val activeLayersVariant by LayersExperimentManager.activeVariant.collectAsState()

  var isProUnlocked by remember { mutableStateOf(claimedSessionId != null) }
  var isCheckingClaim by remember { mutableStateOf(false) }
  var claimMessage by remember {
    mutableStateOf(
      if (claimedSessionId != null) "Verified Talent PRO entitlement active via Stripe Web-to-App deep link." else null
    )
  }

  var duressPin by remember { mutableStateOf("") }
  var duressStatus by remember { mutableStateOf<String?>(null) }
  var faultState by remember { mutableStateOf<AegoraResult.Failure?>(null) }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("auth_vault_scaffold"),
    containerColor = ObsidianBackground
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Top Industrial Header Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier.testTag("auth_vault_back_button")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back to Arena",
            tint = ElectricCyan
          )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "SECURITY VAULT & MONETIZATION",
          color = ElectricCyan,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace,
          letterSpacing = 0.5.sp
        )
      }

      // Fault Condition Component
      faultState?.let { failure ->
        HackerErrorStateComponent(
          failure = failure,
          onRetry = { faultState = null }
        )
      }

      // Funnel Vision Entitlement Frame
      claimMessage?.let { msg ->
        TacticalPanel(
          titleTag = "[FUNNEL-01] // STRIPE_VERIFIED_TALENT",
          subtitle = "CROSS-PLATFORM MONETIZATION SYNC",
          memoryOffset = "ENTITLEMENT_ACTIVE",
          statusLed = TacticalStatusLed.SECURE_EMERALD,
          modifier = Modifier.testTag("claim_status_card")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = TacticalEmerald,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "STRIPE WEB-TO-APP ENTITLEMENT CONFIRMED",
                color = TextTerminalGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
              Text(
                text = msg,
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      // Growth Loop: Layers A/B Testing Paywall Frame
      TacticalPanel(
        titleTag = "[GROWTH-02] // LAYERS_EXPERIMENT_GATE",
        subtitle = "REVENUECAT PAYWALL SPECIFICATION",
        memoryOffset = "EXP: ${activeLayersVariant.id}",
        statusLed = if (activeLayersVariant.id.contains("urgency")) TacticalStatusLed.ALERT_CRIMSON else TacticalStatusLed.ACTIVE_CYAN,
        modifier = Modifier.testTag("layers_paywall_card")
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Box(
            modifier = Modifier
              .clip(CutCornerShape(2.dp))
              .background(if (activeLayersVariant.id.contains("urgency")) HighAlertCrimsonDark else ElectricCyanDark)
              .border(1.dp, if (activeLayersVariant.id.contains("urgency")) HighAlertCrimson else ElectricCyan, CutCornerShape(2.dp))
              .padding(horizontal = 6.dp, vertical = 3.dp)
          ) {
            Text(
              text = "[${activeLayersVariant.urgencyTag.uppercase()}]",
              color = if (activeLayersVariant.id.contains("urgency")) HighAlertCrimson else ElectricCyan,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }

          Text(
            text = activeLayersVariant.headline,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )

          Text(
            text = activeLayersVariant.subheadline,
            color = TextMuted,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 15.sp
          )

          val ctaShape = CutCornerShape(4.dp)
          Button(
            onClick = {
              coroutineScope.launch {
                isCheckingClaim = true
                val result = claimWebProEntitlement(context, "sess_stripe_mock_verified_talent")
                isCheckingClaim = false
                result.onSuccess {
                  isProUnlocked = true
                  claimMessage = "Verified Talent PRO unlocked. RevenueCat CustomerInfo synchronized."
                }.onFailure { err ->
                  faultState = err
                }
              }
            },
            enabled = !isCheckingClaim && !isProUnlocked,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isProUnlocked) TacticalEmerald else ElectricCyanDark
            ),
            shape = ctaShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isProUnlocked) TacticalEmerald else ElectricCyan),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("claim_pro_cta_button")
          ) {
            if (isCheckingClaim) {
              CircularProgressIndicator(
                color = ElectricCyan,
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "COMMUNICATING WITH REVENUECAT...",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
              )
            } else if (isProUnlocked) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "[VERIFIED TALENT PRO ACTIVE]",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            } else {
              Icon(Icons.Default.Payment, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "[${activeLayersVariant.ctaLabel.uppercase()}]",
                color = ElectricCyan,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
              )
            }
          }
        }
      }

      // Duress Self-Destruct Zeroization Frame
      TacticalPanel(
        titleTag = "[DURESS-00] // HARDWARE_ZEROIZE_PROTOCOL",
        subtitle = "CRYPTOGRAPHIC EMERGENCY PURGE",
        memoryOffset = "<50MS PURGE",
        statusLed = TacticalStatusLed.ALERT_CRIMSON,
        borderColor = HighAlertCrimsonDark,
        modifier = Modifier.testTag("duress_wipe_card")
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Entering authorization duress vector initiates immediate destruction of SQLCipher tables and StrongBox master keys.",
            color = Color(0xFFFCA5A5),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 15.sp
          )

          OutlinedTextField(
            value = duressPin,
            onValueChange = { duressPin = it },
            label = { Text("[DURESS_CODE: 9999]", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            shape = CutCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              focusedBorderColor = HighAlertCrimson,
              unfocusedBorderColor = SlateBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("duress_pin_input")
          )

          val wipeBtnShape = CutCornerShape(4.dp)
          Button(
            onClick = {
              val pinChars = duressPin.toCharArray()
              SecureMemory.useAndWipe(pinChars) { sanitizedChars ->
                val entered = String(sanitizedChars)
                if (entered == "9999") {
                  coroutineScope.launch {
                    val db = AegoraEncryptedDb.getInstance(context)
                    db.duressWipeAllData()
                    duressStatus = "SECURITY_LEVEL_0: All cryptographic stores zeroized."
                    duressPin = ""
                  }
                } else {
                  duressStatus = "INVALID PROTOCOL: Purge aborted."
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = HighAlertCrimson),
            shape = wipeBtnShape,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("execute_duress_wipe_button")
          ) {
            Text(
              text = "[EXECUTE DURESS ZEROIZATION]",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          duressStatus?.let { status ->
            Text(
              text = status,
              color = if (status.contains("LEVEL_0")) TextTerminalGreen else HighAlertCrimson,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

/**
 * Simulates RevenueCat REST API customer info refresh following deep link:
 * aegora://claim-web-pro?session_id=...
 */
suspend fun claimWebProEntitlement(context: Context, sessionId: String): AegoraResult<Boolean> {
  return AegoraResult.runCatching("ERR_RCAT_ENTITLEMENT") {
    if (sessionId.isBlank()) {
      throw IllegalArgumentException("Invalid or empty Stripe checkout session ID")
    }
    true
  }
}
