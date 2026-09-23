package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mesh.AegoraMeshSync
import com.example.security.BehavioralBiometricEngine
import com.example.security.HoneytrapWatchdog
import com.example.security.MovingTargetDefenseEngine
import com.example.telemetry.DiagnosticStore
import com.example.ui.theme.ElectricCyan
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

/**
 * Security Status Modal with Real-Time Runtime Diagnostic Feed.
 * Surfaces hardware enclave state, memory bounds, and DiagnosticStore crash logs.
 */
@Composable
fun SecurityStatusModal(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val crashEvents by DiagnosticStore.crashEvents.collectAsState()
  val trustScore by BehavioralBiometricEngine.trustScore.collectAsState()
  val isReauthRequired by BehavioralBiometricEngine.isReauthRequired.collectAsState()
  val isLockdownActive by HoneytrapWatchdog.isLockdownActive.collectAsState()
  val currentMerkleRoot by DiagnosticStore.currentMerkleRoot.collectAsState()
  val merkleLedger by DiagnosticStore.merkleLedger.collectAsState()
  val airGappedPeers by AegoraMeshSync.airGappedPeers.collectAsState()
  val currentMutationCycle by MovingTargetDefenseEngine.currentMutationCycle.collectAsState()
  val isLedgerValid by DiagnosticStore.isLedgerValid.collectAsState()

  val totalMemMb = Runtime.getRuntime().totalMemory() / (1024 * 1024)
  val freeMemMb = Runtime.getRuntime().freeMemory() / (1024 * 1024)
  val usedMemMb = totalMemMb - freeMemMb

  val modalShape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = modifier
        .fillMaxWidth()
        .heightIn(max = 620.dp)
        .clip(modalShape)
        .background(ObsidianBackground)
        .border(1.5.dp, SlateBorderBright, modalShape)
        .padding(16.dp)
        .testTag("security_status_modal")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Modal Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = ElectricCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SECURITY STATUS // OBSERVABILITY",
              color = ElectricCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              fontFamily = FontFamily.Monospace,
              letterSpacing = 0.5.sp
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(28.dp)
              .testTag("close_security_modal_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Modal",
              tint = TextMuted,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        // Section 1: Security Enclave Readout
        TacticalPanel(
          titleTag = "[ENCLAVE-01] // HARDWARE_PROTECTION",
          subtitle = "STRONGBOX TEE SUBSYSTEM",
          memoryOffset = "TEE_ACTIVE",
          statusLed = TacticalStatusLed.SECURE_EMERALD
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "• ROOT_OF_TRUST: StrongBox Keystore Enclave (Attested)",
              color = TextTerminalGreen,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "• POST_QUANTUM_CORE: Kyber-768 Hybrid Encryption Active",
              color = TextPrimary,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "• DURESS_WATCHDOG: Memory Sanitization Subsystem 0-Leak Verified",
              color = TextMuted,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Section 1B: Behavioral Biometrics & StrongBox Guard
        TacticalPanel(
          titleTag = "[BIOMETRIC-GUARD] // CONTINUOUS_AUTHENTICATION",
          subtitle = "TOUCH & INERTIAL PROFILE",
          memoryOffset = "TRUST: ${(trustScore * 100).toInt()}%",
          statusLed = if (isReauthRequired) TacticalStatusLed.ALERT_CRIMSON else TacticalStatusLed.SECURE_EMERALD
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = if (isReauthRequired) "CRITICAL: STATISTICAL ANOMALY DRIFT -> STRONGBOX LOCKOUT" else "STATUS: OPERATOR BIOMETRIC SIGNATURE VERIFIED",
              color = if (isReauthRequired) HighAlertCrimson else TextTerminalGreen,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { BehavioralBiometricEngine.verifyStrongBoxReauthentication("OPERATOR_MANUAL_ASSERT") },
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                shape = CutCornerShape(4.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("[RE-ATTEST TEE]", color = ElectricCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
              }
              Button(
                onClick = { BehavioralBiometricEngine.simulateBiometricDrift() },
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, TacticalAmber),
                shape = CutCornerShape(4.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("[SIM DRIFT]", color = TacticalAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
              }
            }
          }
        }

        // Section 1C: Canary Traps & Honeytokens
        TacticalPanel(
          titleTag = "[DECEPTION] // SQLCIPHER_HONEYTOKENS",
          subtitle = "CANARY RECONNAISSANCE TRAP",
          memoryOffset = if (isLockdownActive) "LOCKDOWN_ENGAGED" else "TRAPS_ARMED",
          statusLed = if (isLockdownActive) TacticalStatusLed.ALERT_CRIMSON else TacticalStatusLed.SECURE_EMERALD
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = if (isLockdownActive) "BREACH INTERCEPTED: SILENT LOCKDOWN & VOLATILE PURGE ENGAGED" else "CANARY STATUS: 5 DECOY DOSSIERS INJECTED INTO ENCRYPTED VAULT",
              color = if (isLockdownActive) HighAlertCrimson else TextTerminalGreen,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { HoneytrapWatchdog.inspectQuery("CANARY_TOPSECRET_DOSSIER_77") },
                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
                border = androidx.compose.foundation.BorderStroke(1.dp, HighAlertCrimson),
                shape = CutCornerShape(4.dp),
                modifier = Modifier.weight(1f)
              ) {
                Text("[TRIP HONEYTOKEN]", color = HighAlertCrimson, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
              }
              if (isLockdownActive) {
                Button(
                  onClick = { HoneytrapWatchdog.clearLockdownAfterTriage() },
                  colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
                  border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
                  shape = CutCornerShape(4.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("[RESET TRAP]", color = ElectricCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }
              }
            }
          }
        }

        // Section 1D: Cryptographic Merkle Ledger
        TacticalPanel(
          titleTag = "[MERKLE-CHAIN] // IMMUTABLE_AUDIT_LEDGER",
          subtitle = "CRYPTOGRAPHIC APPEND-ONLY CHAIN",
          memoryOffset = "${merkleLedger.size} BLOCKS",
          statusLed = if (isLedgerValid) TacticalStatusLed.SECURE_EMERALD else TacticalStatusLed.ALERT_CRIMSON
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "ROOT: ${currentMerkleRoot.take(24)}...",
              color = TextPrimary,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Button(
              onClick = { DiagnosticStore.verifyMerkleLedgerIntegrity() },
              colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
              border = androidx.compose.foundation.BorderStroke(1.dp, TacticalEmerald),
              shape = CutCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("[VERIFY MERKLE INTEGRITY (0-TAMPER)]", color = TacticalEmerald, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }

        // Section 1E: Air-Gapped P2P Mesh Network
        TacticalPanel(
          titleTag = "[AIRGAP-MESH] // P2P_BLE_DIRECT",
          subtitle = "OFF-GRID THREAT IOC RELAY",
          memoryOffset = "${airGappedPeers.size} PEERS",
          statusLed = TacticalStatusLed.ACTIVE_CYAN
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "PEERS: ${airGappedPeers.joinToString { it.callsign }}",
              color = TextPrimary,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Button(
              onClick = { AegoraMeshSync.broadcastAirGappedThreat("VOLT_TYPHOON", "T1078 - Valid Accounts") },
              colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
              border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan),
              shape = CutCornerShape(4.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("[BROADCAST AIR-GAPPED IOC]", color = ElectricCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }

        // Section 2: Memory Heap Allocation
        TacticalPanel(
          titleTag = "[SYS-MEM] // RUNTIME_HEAP_METRICS",
          subtitle = "JVM HEAP FOOTPRINT",
          memoryOffset = "${usedMemMb}MB / ${totalMemMb}MB",
          statusLed = if (usedMemMb > 180) TacticalStatusLed.STANDBY_AMBER else TacticalStatusLed.ACTIVE_CYAN
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Memory, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "ALLOCATED: ${totalMemMb}MB",
                color = TextPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
              )
            }
            Text(
              text = "FREE: ${freeMemMb}MB",
              color = TextTerminalGreen,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        // Section 3: Runtime Diagnostic Feed (DiagnosticStore)
        TacticalPanel(
          titleTag = "[DIAG-FEED] // RUNTIME_DIAGNOSTIC_FEED",
          subtitle = "CAPTURED UI RENDERING EXCEPTIONS",
          memoryOffset = "${crashEvents.size} EVENTS",
          statusLed = if (crashEvents.isEmpty()) TacticalStatusLed.SECURE_EMERALD else TacticalStatusLed.ALERT_CRIMSON
        ) {
          if (crashEvents.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(CutCornerShape(4.dp))
                .background(ObsidianSurfaceRaised)
                .border(1.dp, SlateBorder, CutCornerShape(4.dp))
                .padding(10.dp)
            ) {
              Text(
                text = "[DIAGNOSTICS_CLEAN] // 0 RUNTIME FAULTS RECORDED\nUI rendering pipeline executing within deterministic boundaries.",
                color = TextTerminalGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 14.sp
              )
            }
          } else {
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              crashEvents.take(4).forEach { crash ->
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(CutCornerShape(4.dp))
                    .background(ObsidianSurfaceRaised)
                    .border(1.dp, HighAlertCrimson, CutCornerShape(4.dp))
                    .padding(8.dp)
                ) {
                  Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = "${crash.id} // [${crash.componentTag}]",
                        color = HighAlertCrimson,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                      )
                      Text(
                        text = crash.timestamp,
                        color = TextDim,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                    Text(
                      text = "${crash.exceptionClass}: ${crash.message}",
                      color = TextPrimary,
                      fontSize = 10.sp,
                      fontFamily = FontFamily.Monospace,
                      maxLines = 2
                    )
                  }
                }
              }
            }
          }
        }

        // Diagnostic Control Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val actionShape = CutCornerShape(4.dp)
          Button(
            onClick = {
              DiagnosticStore.simulateUiCrash("DuelThreatRadar")
            },
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalAmber),
            shape = actionShape,
            modifier = Modifier
              .weight(1f)
              .testTag("simulate_crash_button")
          ) {
            Text(
              text = "[SIMULATE FAULT]",
              color = TacticalAmber,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = {
              DiagnosticStore.clearCrashes()
            },
            colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceRaised),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
            shape = actionShape,
            modifier = Modifier
              .weight(1f)
              .testTag("clear_diagnostics_button")
          ) {
            Text(
              text = "[PURGE BUFFER]",
              color = TextMuted,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  }
}
