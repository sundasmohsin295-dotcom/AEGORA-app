package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import com.example.subscription.AegoraSubscriptionRepository

/**
 * ProofDossierModal
 * A centered, high-fidelity matte-dark card representing the user's
 * Verified Capability Passport, including a mock SHA-256 evidence digest,
 * status chips for completed skills, and action buttons for copying verification
 * links or performing biometric export simulations.
 */
@Composable
fun ProofDossierModal(
  onDismiss: () -> Unit,
  onShowPaywall: () -> Unit = {},
  operatorName: String = "SUNDAS MOHSIN",
  callsign: String = "AEG-2026-9942X",
  role: String = "Lvl 4 • Senior SOC Analyst",
  verifiedSkillsList: List<Pair<String, String>> = listOf(
    "Sysmon EDR Log Analysis" to "VERIFIED",
    "Cobalt Strike Stager Identification" to "VERIFIED",
    "PowerShell Base64 Deobfuscation" to "VERIFIED",
    "Active Directory ACL Recon" to "VERIFIED",
    "Memory Forensics Injection" to "VERIFIED",
    "Sigma Rule Detection Engineering" to "VERIFIED"
  ),
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  val obsidianBg = Color(0xFF090A0C)
  val matteSteel = Color(0xFF15171C)
  val slateBorder = Color(0xFF2D313A)
  val cobaltBlue = Color(0xFF2962FF)
  val emeraldGreen = Color(0xFF00E676)
  val mutedSlate = Color(0xFF8A919E)

  val mockSha256Digest = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
  val verificationUrl = "https://aegora.net/verify/passport/$callsign?sha256=${mockSha256Digest.take(16)}"

  var copiedLink by remember { mutableStateOf(false) }
  var copiedHash by remember { mutableStateOf(false) }
  var isExporting by remember { mutableStateOf(false) }
  var exportProgress by remember { mutableFloatStateOf(0f) }
  var exportComplete by remember { mutableStateOf(false) }

  val verifiedSkills = verifiedSkillsList

  fun copyToClipboard(label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.8f))
        .padding(20.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        colors = CardDefaults.cardColors(containerColor = obsidianBg),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, slateBorder),
        modifier = modifier
          .fillMaxWidth()
          .widthIn(max = 640.dp)
          .wrapContentHeight()
          .testTag("proof_dossier_modal_card")
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
          // Top Header Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .background(cobaltBlue.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                  .border(1.dp, cobaltBlue, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = cobaltBlue, modifier = Modifier.size(20.dp))
              }
              Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Text(
                    text = "VERIFIED CAPABILITY PASSPORT",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                  )
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = emeraldGreen.copy(alpha = 0.15f),
                    border = BorderStroke(0.6.dp, emeraldGreen)
                  ) {
                    Text(
                      text = "LIVE AUDIT",
                      color = emeraldGreen,
                      fontSize = 8.sp,
                      fontWeight = FontWeight.Bold,
                      fontFamily = FontFamily.Monospace,
                      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "CRYPTOGRAPHIC PROOF DOSSIER // RECRUITER AUDIT",
                  color = Color(0xFF8A919E),
                  fontSize = 9.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier.size(32.dp).testTag("proof_dossier_close_btn")
            ) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF8A919E))
            }
          }

          // Operator Identity Card
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(matteSteel, RoundedCornerShape(10.dp))
              .border(1.dp, slateBorder, RoundedCornerShape(10.dp))
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(cobaltBlue),
                contentAlignment = Alignment.Center
              ) {
                Text("SM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              }
              Column {
                Text(operatorName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(role, color = Color(0xFF8A919E), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              }
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("PASSPORT ID", color = Color(0xFF8A919E), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
              Text(callsign, color = emeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
          }

          // SHA-256 Evidence Digest Box
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "IMMUTABLE EVIDENCE DIGEST (SHA-256)",
                color = Color(0xFF8A919E),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              )
              Row(
                modifier = Modifier.clickable {
                  copyToClipboard("SHA-256 Digest", mockSha256Digest)
                  copiedHash = true
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  if (copiedHash) Icons.Default.Check else Icons.Default.ContentCopy,
                  contentDescription = null,
                  tint = if (copiedHash) emeraldGreen else cobaltBlue,
                  modifier = Modifier.size(12.dp)
                )
                Text(
                  text = if (copiedHash) "COPIED" else "COPY HASH",
                  color = if (copiedHash) emeraldGreen else cobaltBlue,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black, RoundedCornerShape(8.dp))
                .border(1.dp, slateBorder, RoundedCornerShape(8.dp))
                .padding(12.dp)
            ) {
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                  text = "// ROOT MERKLE LEAF HASH:",
                  color = Color(0xFF8A919E),
                  fontSize = 9.sp,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = mockSha256Digest,
                  color = emeraldGreen,
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  lineHeight = 16.sp
                )
                HorizontalDivider(color = slateBorder, thickness = 0.5.dp)
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("SIGNER: AEGORA-PROV-ED25519", color = Color(0xFF8A919E), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                  Text("ATTESTED: 2026-09-13T08:19:00Z", color = Color(0xFF8A919E), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }
              }
            }
          }

          // Status Chips for Completed Skills
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "PROVEN COGNITIVE & DEFENSIVE CAPABILITIES",
              color = Color(0xFF8A919E),
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            )

            if (verifiedSkills.isEmpty()) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = matteSteel,
                border = BorderStroke(1.dp, slateBorder),
                modifier = Modifier.fillMaxWidth().testTag("proof_dossier_empty_state")
              ) {
                Column(
                  modifier = Modifier.padding(20.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Icon(
                    Icons.Default.HourglassEmpty,
                    contentDescription = null,
                    tint = mutedSlate,
                    modifier = Modifier.size(32.dp)
                  )
                  Text(
                    text = "AWAITING MISSION TELEMETRY PROOF",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                  )
                  Text(
                    text = "No cryptographically verified leaves generated yet. Complete Live SOC Range or Adversary Duels to commit proof leaves to your capability Merkle tree.",
                    color = mutedSlate,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                  )
                }
              }
            } else {
              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                verifiedSkills.chunked(2).forEach { rowSkills ->
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    rowSkills.forEach { skill ->
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = matteSteel,
                        border = BorderStroke(1.dp, slateBorder),
                        modifier = Modifier.weight(1f)
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                          Icon(Icons.Default.CheckCircle, contentDescription = null, tint = emeraldGreen, modifier = Modifier.size(13.dp))
                          Text(
                            text = skill.first,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                          )
                          Text(
                            text = "[${skill.second}]",
                            color = emeraldGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                          )
                        }
                      }
                    }
                    if (rowSkills.size == 1) {
                      Spacer(modifier = Modifier.weight(1f))
                    }
                  }
                }
              }
            }
          }

          // Biometric Export Progress Simulation
          if (isExporting) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(cobaltBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .border(1.dp, cobaltBlue, RoundedCornerShape(8.dp))
                .padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "ATTESTING BIOMETRIC CREDENTIAL ENCLAVE...",
                  color = cobaltBlue,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
                Text(
                  text = "${(exportProgress * 100).toInt()}%",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
              LinearProgressIndicator(
                progress = { exportProgress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = cobaltBlue,
                trackColor = matteSteel
              )
            }
          }

          if (exportComplete) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = emeraldGreen.copy(alpha = 0.1f),
              border = BorderStroke(1.dp, emeraldGreen),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = emeraldGreen, modifier = Modifier.size(16.dp))
                Text(
                  text = "EXPORT SUCCESSFUL: aegora-passport-verified.json signed with hardware token.",
                  color = emeraldGreen,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }

          // Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                copyToClipboard("Verification Link", verificationUrl)
                copiedLink = true
              },
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = matteSteel),
              border = BorderStroke(1.dp, slateBorder),
              modifier = Modifier.weight(1f).height(48.dp).testTag("proof_dossier_copy_link_btn")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(
                  if (copiedLink) Icons.Default.Check else Icons.Default.ContentCopy,
                  contentDescription = null,
                  tint = if (copiedLink) emeraldGreen else Color.White,
                  modifier = Modifier.size(15.dp)
                )
                Text(
                  text = if (copiedLink) "LINK COPIED" else "COPY LINK",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Button(
              onClick = {
                if (!AegoraSubscriptionRepository.canAccessBiometricExport()) {
                  onShowPaywall()
                  return@Button
                }

                if (!isExporting) {
                  isExporting = true
                  exportProgress = 0f
                  exportComplete = false
                  coroutineScope.launch {
                    while (exportProgress < 1.0f) {
                      delay(200)
                      exportProgress = (exportProgress + 0.25f).coerceAtMost(1.0f)
                    }
                    isExporting = false
                    exportComplete = true
                  }
                }
              },
              enabled = !isExporting,
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(containerColor = cobaltBlue),
              modifier = Modifier.weight(1f).height(48.dp).testTag("proof_dossier_biometric_export_btn")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                Text(
                  text = if (isExporting) "SIMULATING..." else "BIOMETRIC EXPORT",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
