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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AegoraAuthRepository
import com.example.data.ZeroTrustSecurityRepository
import com.example.model.*
import com.example.ui.components.ChamferedCutCornerShape
import com.example.ui.components.HexagonShape
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
/**
 * AEGORA ZERO-TRUST SECURITY CENTER & IDENTITY CONTROL HUB.
 * (For real production security requirements and status audit, see SECURITY_STATUS.md)
 * Provides:
 * 1. Real Security Posture Score & Control Audits
 * 2. Passkey & FIDO2 Credential Manager
 * 3. Active Sessions & Registered Device Fleet Management
 * 4. Tamper-Evident Security Audit Timeline
 * 5. Single-Use Cryptographic Recovery Code Vault
 * 6. Identity Architecture & Formal Threat Model Matrix
 * 7. "Can You Protect AEGORA?" Educational Attack Simulator
 * 8. Step-Up Authentication Enforcement
 */
// MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityCenterScreen(
  onNavigateBack: () -> Unit,
  onNavigateToAuth: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(0) } // 0: Overview & Score, 1: Passkeys & MFA, 2: Sessions & Devices, 3: Security Timeline, 4: Threat Model & Arch, 5: Attack Simulator
  val passkeys by ZeroTrustSecurityRepository.passkeys.collectAsState()
  val registeredDevices by ZeroTrustSecurityRepository.registeredDevices.collectAsState()
  val activeSessions by ZeroTrustSecurityRepository.activeSessions.collectAsState()
  val securityTimeline by ZeroTrustSecurityRepository.securityTimeline.collectAsState()
  val recoveryCodeSet by ZeroTrustSecurityRepository.recoveryCodeSet.collectAsState()
  val attackScenarios by ZeroTrustSecurityRepository.attackScenarios.collectAsState()
  val currentAuthMethod by ZeroTrustSecurityRepository.currentAuthMethod.collectAsState()

  val posture = remember(passkeys, activeSessions, recoveryCodeSet) {
    ZeroTrustSecurityRepository.calculateSecurityPostureScore()
  }

  // Step-Up Auth Dialog State
  var showStepUpDialog by remember { mutableStateOf(false) }
  var pendingSensitiveActionName by remember { mutableStateOf("") }
  var onStepUpSuccessCallback by remember { mutableStateOf<(() -> Unit)?>(null) }
  var isPerformingStepUp by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  // New Passkey Dialog State
  var showAddPasskeyDialog by remember { mutableStateOf(false) }
  var newPasskeyNameInput by remember { mutableStateOf("") }
  var newPasskeyIsHardware by remember { mutableStateOf(true) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "AEGORA SECURITY CENTER",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Black,
                  letterSpacing = 0.8.sp
                ),
                color = NeonCyan
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = CyberEmerald.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
              ) {
                Text(
                  text = "ZERO-TRUST",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp
                  ),
                  color = CyberEmerald,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Defense-in-Depth Identity & Access Management",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondaryDark
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
      )
    },
    containerColor = CyberBackground,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(bottom = 40.dp)
    ) {
      // 0. Architectural Honesty Banner
      item {
        val providerStatus = remember { AegoraAuthRepository.getProviderStatus() }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E140A),
          border = BorderStroke(1.dp, Color(0xFFFF9100).copy(alpha = 0.6f)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("security_center_provider_status_banner")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Warning,
              contentDescription = null,
              tint = Color(0xFFFF9100),
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "AUTH BACKEND: NOT CONNECTED",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                  ),
                  color = Color(0xFFFF9100)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(3.dp),
                  color = Color(0xFFFF9100).copy(alpha = 0.2f)
                ) {
                  Text(
                    text = "DEMO / ARCHITECTURAL",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontFamily = FontFamily.Monospace),
                    color = Color(0xFFFF9100),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "External identity provider (Firebase) is not connected (missing google-services.json). Displayed security center states demonstrate architectural specifications.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondaryDark
              )
            }
          }
        }
      }

      // 1. Posture Summary Header
      item {
        SecurityPostureHeaderCard(posture = posture, currentAuthMethod = currentAuthMethod)
      }

      // 2. Navigation Tabs
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
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("Overview & Score", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Passkeys & MFA (${passkeys.size})", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("Sessions & Devices (${activeSessions.size})", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 3,
            onClick = { selectedTab = 3 },
            text = { Text("Security Timeline", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 4,
            onClick = { selectedTab = 4 },
            text = { Text("Threat Model & Arch", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 5,
            onClick = { selectedTab = 5 },
            text = { Text("Attack Simulator", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
        }
      }

      // 3. Tab Contents
      when (selectedTab) {
        0 -> {
          // Tab 0: Comprehensive Posture & Actionable Recommendations
          item {
            SecurityPostureDimensionsCard(posture = posture)
          }

          item {
            Text(
              text = "PRIORITY SECURITY RECOMMENDATIONS",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
              color = NeonCyan
            )
          }

          items(posture.recommendations) { rec ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurface,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Recommend, contentDescription = null, tint = TerminalAmber, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                  text = rec,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark
                )
              }
            }
          }

          // Emergency Recovery Codes Section
          item {
            Surface(
              shape = ChamferedCutCornerShape,
              color = CyberSurfaceElevated,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "EMERGENCY RECOVERY CODES",
                      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                      color = CyberEmerald
                    )
                  }
                  Text(
                    text = "${recoveryCodeSet.remainingCount} Remaining",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = TextSecondaryDark
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Single-use cryptographically secure recovery tokens for offline emergency access if your hardware passkeys are lost.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(10.dp))
                if (recoveryCodeSet.isRevealed) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF07121E),
                    border = BorderStroke(1.dp, CyberBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      recoveryCodeSet.codes.chunked(2).forEach { pair ->
                        Row(
                          modifier = Modifier.fillMaxWidth(),
                          horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                          pair.forEach { code ->
                            Text(
                              text = code,
                              style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                              ),
                              color = NeonCyan,
                              modifier = Modifier.padding(vertical = 2.dp)
                            )
                          }
                        }
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = { ZeroTrustSecurityRepository.toggleRecoveryCodesVisibility() },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text(
                      if (recoveryCodeSet.isRevealed) "Hide Codes" else "View Codes",
                      fontSize = 11.sp
                    )
                  }

                  Button(
                    onClick = {
                      pendingSensitiveActionName = "Regenerate Emergency Recovery Codes"
                      onStepUpSuccessCallback = {
                        ZeroTrustSecurityRepository.generateNewRecoveryCodes()
                      }
                      showStepUpDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TerminalAmber),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("Regenerate Codes", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        1 -> {
          // Tab 1: Passkeys & FIDO2 Credential Manager
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "REGISTERED FIDO2 / WEBAUTHN PASSKEYS",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = NeonCyan
              )
              Button(
                onClick = { showAddPasskeyDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Passkey", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
          }

          items(passkeys) { pk ->
            Surface(
              shape = ChamferedCutCornerShape,
              color = CyberSurface,
              border = BorderStroke(1.dp, if (pk.isHardwareBacked) CyberEmerald.copy(alpha = 0.7f) else CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      if (pk.isHardwareBacked) Icons.Default.Fingerprint else Icons.Default.Key,
                      contentDescription = null,
                      tint = if (pk.isHardwareBacked) CyberEmerald else NeonCyan,
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = pk.credentialName,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = TextPrimaryDark
                    )
                  }

                  if (pk.isHardwareBacked) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = CyberEmerald.copy(alpha = 0.15f),
                      border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
                    ) {
                      Text(
                        text = "STRONGBOX / TEE",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.Bold),
                        color = CyberEmerald,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Platform: ${pk.platformType}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
                Text(
                  text = "Enrolled: ${pk.createdAt} • Sign Count: ${pk.counter}",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                  color = TextSecondaryDark
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End
                ) {
                  TextButton(
                    onClick = {
                      pendingSensitiveActionName = "Revoke Passkey (${pk.credentialName})"
                      onStepUpSuccessCallback = {
                        ZeroTrustSecurityRepository.revokePasskey(pk.id)
                      }
                      showStepUpDialog = true
                    }
                  ) {
                    Text("Revoke Credential", color = NeonPink, fontSize = 11.sp)
                  }
                }
              }
            }
          }
        }

        2 -> {
          // Tab 2: Sessions & Registered Devices
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ACTIVE ZERO-TRUST SESSIONS",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                color = NeonCyan
              )
              OutlinedButton(
                onClick = {
                  pendingSensitiveActionName = "Sign Out All Other Sessions"
                  onStepUpSuccessCallback = {
                    ZeroTrustSecurityRepository.terminateAllOtherSessions()
                  }
                  showStepUpDialog = true
                },
                border = BorderStroke(1.dp, NeonPink),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("Sign Out Others", color = NeonPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }

          items(activeSessions) { sess ->
            Surface(
              shape = ChamferedCutCornerShape,
              color = CyberSurface,
              border = BorderStroke(1.dp, if (sess.isCurrentSession) NeonCyan else CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      when (sess.deviceType) {
                        "Android Mobile" -> Icons.Default.PhoneAndroid
                        "Web Session" -> Icons.Default.Laptop
                        else -> Icons.Default.Terminal
                      },
                      contentDescription = null,
                      tint = if (sess.isCurrentSession) NeonCyan else TextSecondaryDark,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = sess.deviceName,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = TextPrimaryDark
                    )
                  }

                  if (sess.isCurrentSession) {
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = NeonCyan.copy(alpha = 0.15f),
                      border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                      Text(
                        text = "THIS DEVICE",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.Bold),
                        color = NeonCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "IP: ${sess.ipAddress} • Location: ${sess.approximateLocation}",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                  color = TextSecondaryDark
                )
                Text(
                  text = "Auth: ${sess.authMethod.displayName} • Lifetime: ${sess.accessTokenLifetimeMinutes}m tokens",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                  color = CyberEmerald
                )

                if (!sess.isCurrentSession) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                  ) {
                    TextButton(
                      onClick = {
                        pendingSensitiveActionName = "Terminate Session on ${sess.deviceName}"
                        onStepUpSuccessCallback = {
                          ZeroTrustSecurityRepository.terminateSession(sess.sessionId)
                        }
                        showStepUpDialog = true
                      }
                    ) {
                      Text("Terminate Session", color = NeonPink, fontSize = 11.sp)
                    }
                  }
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "REGISTERED DEVICE FLEET (PLAY INTEGRITY ATTESTED)",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
              color = CyberEmerald
            )
          }

          items(registeredDevices) { dev ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurfaceElevated,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = dev.deviceName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                  Text(
                    text = dev.lastActive,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                    color = TextSecondaryDark
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "${dev.osVersion} • ${dev.appVersion}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
                Text(
                  text = dev.integrityState,
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                  color = CyberEmerald
                )
              }
            }
          }
        }

        3 -> {
          // Tab 3: Tamper-Evident Security Timeline
          item {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = CyberSurface,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "CRYPTOGRAPHIC SECURITY AUDIT TRAIL",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NeonCyan)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Append-only, tamper-evident security telemetry. Tracks authentication, credential enrollments, step-up verifications, and token rotations.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
              }
            }
          }

          items(securityTimeline) { evt ->
            Surface(
              shape = ChamferedCutCornerShape,
              color = CyberSurface,
              border = BorderStroke(1.dp, if (evt.severity == "WARNING") TerminalAmber else CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = evt.eventType,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.sp
                    ),
                    color = if (evt.severity == "WARNING") TerminalAmber else NeonCyan
                  )
                  Text(
                    text = evt.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                    color = TextSecondaryDark
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = evt.title,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = evt.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Host: ${evt.deviceName} (${evt.ipAddress})",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                  color = TextSecondaryDark
                )
              }
            }
          }
        }

        4 -> {
          // Tab 4: Identity Architecture & Threat Model Viewer
          item {
            Surface(
              shape = ChamferedCutCornerShape,
              color = CyberSurfaceElevated,
              border = BorderStroke(1.2.dp, NeonCyan),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "AEGORA ZERO-TRUST IDENTITY ARCHITECTURE",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = NeonCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                val archSteps = listOf(
                  "1. USER & LOCAL DEVICE" to "Biometric prompt / Hardware Attestation (StrongBox/TEE)",
                  "2. ANTI-KEYLOGGER ENCLAVE" to "In-app randomized SecureNumpad & SecureKeyboard defeats screen loggers",
                  "3. IN-MEMORY WIPE ENCLAVE" to "Zero-out RAM buffers (SecureMemory.wipe) to stop heap dump scrapers",
                  "4. PLAY INTEGRITY ATTESTATION" to "Cryptographic nonce hardware token verification via Google Play",
                  "5. CERTIFICATE TRANSPARENCY (CT)" to "RFC 6962 embedded SCT audit & rogue proxy CA interception defense",
                  "6. MUTEX CONCURRENCY ENCLAVE" to "Server-side asyncio.Lock() shields against TOCTOU race conditions",
                  "7. PASSKEY / FIDO2 ASSERTION" to "Origin-bound challenge signed via WebAuthn clientDataJSON",
                  "8. ADAPTIVE RISK ENGINE" to "Geo-velocity, Device integrity, IP reputation, Anomaly check",
                  "9. SESSION & TOKEN ENGINE" to "15m short-lived access token + Refresh token rotation in Keystore",
                  "10. SECURE APPLICATION APIs" to "HTTPS/TLS 1.3 + Nonce validation + Zero client-side trust",
                  "11. THE DURESS PIN (ZERO-WIPE)" to "Under-duress instant purge of Room DB, EncryptedPrefs, and Keystore aliases",
                  "12. STRONGBOX SE HARDWARE ENCLAVE" to "StrongBox dedicated chip backing immune to cold-boot DRAM extraction & bus sniffing",
                  "13. NETWORK TRAFFIC PADDING" to "Fixed 4096 / 8192-byte noise padding defeats packet size analysis & endpoint sniffing",
                  "14. COGNITIVE INJECTION SHIELD" to "Scrubbed telemetry commands & enforced strict JSON schema for Gemini 1.5 Pro",
                  "15. STRICT IPC & PATH SHIELD" to "Zero unexported components, deep-link Intent firewall & canonical path traversal sandbox"
                )

                archSteps.forEach { (title, desc) ->
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF07121E),
                    border = BorderStroke(1.dp, CyberBorderSubtle),
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 3.dp)
                  ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                      Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                        color = CyberEmerald
                      )
                      Text(
                        text = desc,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = TextSecondaryDark
                      )
                    }
                  }
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "FORMAL THREAT MODEL MATRIX",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
              color = CyberViolet
            )
          }

          items(ZeroTrustSecurityRepository.threatModelItems) { tm ->
            Surface(
              shape = ChamferedCutCornerShape,
              color = CyberSurface,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = tm.threatName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (tm.riskLevel == "CRITICAL") NeonPink.copy(alpha = 0.2f) else TerminalAmber.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, if (tm.riskLevel == "CRITICAL") NeonPink else TerminalAmber)
                  ) {
                    Text(
                      text = tm.riskLevel,
                      style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 8.sp),
                      color = if (tm.riskLevel == "CRITICAL") NeonPink else TerminalAmber,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Attack Surface: ${tm.attackSurface}",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
                  color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "🛡️ Preventive: ${tm.preventiveControl}",
                  style = MaterialTheme.typography.bodySmall,
                  color = CyberEmerald
                )
                Text(
                  text = "🔍 Detection: ${tm.detectionControl}",
                  style = MaterialTheme.typography.bodySmall,
                  color = NeonCyan
                )
                Text(
                  text = "⚡ Response: ${tm.responseControl}",
                  style = MaterialTheme.typography.bodySmall,
                  color = TerminalAmber
                )
              }
            }
          }
        }

        5 -> {
          // Tab 5: "Can you protect AEGORA?" Educational Attack Simulator
          item {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = CyberSurface,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "EDUCATIONAL ATTACK DEFENSE SIMULATOR",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NeonCyan)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Defend fictional AEGORA identity gateways against live simulated attacks: Credential Stuffing, AiTM Reverse Proxy Phishing, and Stolen Session Token Rotation.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
              }
            }
          }

          items(attackScenarios) { scenario ->
            AttackSimulatorScenarioCard(
              scenario = scenario,
              onAnswer = { optionIndex ->
                ZeroTrustSecurityRepository.answerSimulationStep(scenario.id, optionIndex)
              }
            )
          }
        }
      }
    }
  }

  // Step-Up Authentication Dialog
  if (showStepUpDialog) {
    AlertDialog(
      onDismissRequest = { showStepUpDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = null, tint = TerminalAmber, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "STEP-UP AUTHENTICATION",
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = TerminalAmber
          )
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Sensitive Action Requested: $pendingSensitiveActionName",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
          Text(
            text = "Zero-Trust policy requires re-authenticating with your hardware-backed Passkey before privileged modifications can be committed.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )

          if (isPerformingStepUp) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 8.dp)
            ) {
              CircularProgressIndicator(modifier = Modifier.size(16.dp), color = CyberEmerald)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Attesting biometric challenge via StrongBox...",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = CyberEmerald
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            isPerformingStepUp = true
            coroutineScope.launch {
              delay(900)
              isPerformingStepUp = false
              showStepUpDialog = false
              onStepUpSuccessCallback?.invoke()
            }
          },
          enabled = !isPerformingStepUp,
          colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald)
        ) {
          Text("Verify Passkey", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showStepUpDialog = false },
          enabled = !isPerformingStepUp
        ) {
          Text("Cancel", color = TextSecondaryDark)
        }
      },
      containerColor = CyberSurface
    )
  }

  // Register Passkey Dialog
  if (showAddPasskeyDialog) {
    AlertDialog(
      onDismissRequest = { showAddPasskeyDialog = false },
      title = {
        Text(
          text = "ENROLL NEW PASSKEY",
          style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = NeonCyan
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = newPasskeyNameInput,
            onValueChange = { newPasskeyNameInput = it },
            label = { Text("Passkey Name (e.g., Pixel 9 StrongBox / YubiKey 5C)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { newPasskeyIsHardware = !newPasskeyIsHardware }
          ) {
            Checkbox(
              checked = newPasskeyIsHardware,
              onCheckedChange = { newPasskeyIsHardware = it }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Hardware-backed (FIDO2 / StrongBox TEE)",
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            ZeroTrustSecurityRepository.registerNewPasskey(newPasskeyNameInput, newPasskeyIsHardware)
            newPasskeyNameInput = ""
            showAddPasskeyDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
        ) {
          Text("Enroll Credential", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddPasskeyDialog = false }) {
          Text("Cancel", color = TextSecondaryDark)
        }
      },
      containerColor = CyberSurface
    )
  }
}

@Composable
fun SecurityPostureHeaderCard(posture: SecurityPostureScore, currentAuthMethod: AuthMethod) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, CyberEmerald),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "ACCOUNT SECURITY POSTURE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = CyberEmerald
          )
          Text(
            text = "${posture.overallScore}% • ${posture.postureRating}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CyberEmerald.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
        ) {
          Text(
            text = "ACTIVE AUTH: ${currentAuthMethod.name}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 8.sp
            ),
            color = CyberEmerald,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      LinearProgressIndicator(
        progress = { posture.overallScore / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = CyberEmerald,
        trackColor = CyberSurfaceElevated
      )
    }
  }
}

@Composable
fun SecurityPostureDimensionsCard(posture: SecurityPostureScore) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurfaceElevated,
    border = BorderStroke(1.dp, CyberBorderSubtle),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = "REAL-WORLD DEFENSIVE CONTROL METRICS",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
        color = NeonCyan
      )
      Spacer(modifier = Modifier.height(8.dp))

      val dimensions = listOf(
        "Passkey Coverage" to "${posture.passkeyCoveragePercent}%",
        "MFA Enforced" to if (posture.mfaEnforced) "Active (FIDO2)" else "Disabled",
        "Session Hygiene" to "${posture.sessionHygienePercent}%",
        "Recovery Readiness" to "${posture.recoveryReadinessPercent}%"
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        dimensions.forEach { (label, value) ->
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = value,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = CyberEmerald
            )
            Text(
              text = label,
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp),
              color = TextSecondaryDark
            )
          }
        }
      }
    }
  }
}

@Composable
fun AttackSimulatorScenarioCard(
  scenario: EducationalAttackScenario,
  onAnswer: (Int) -> Boolean
) {
  var selectedFeedback by remember { mutableStateOf<String?>(null) }
  var feedbackIsCorrect by remember { mutableStateOf(false) }

  val step = scenario.steps.getOrNull(scenario.currentStepIndex)

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, if (scenario.isCompleted) CyberEmerald else CyberViolet),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "CATEGORY: ${scenario.category}",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
          color = CyberViolet
        )
        Text(
          text = if (scenario.isCompleted) "DEFENSE MASTERED" else "STEP ${scenario.currentStepIndex + 1}/${scenario.steps.size}",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = if (scenario.isCompleted) CyberEmerald else TerminalAmber
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = scenario.title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = scenario.threatActorSummary,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      if (step != null && !scenario.isCompleted) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFF07121E),
          border = BorderStroke(1.dp, CyberBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "SCENARIO CHALLENGE:",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
              color = TerminalAmber
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = step.prompt,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = TextPrimaryDark
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        step.defensiveOptions.forEachIndexed { idx, option ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberSurfaceVariant,
            border = BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
              .clickable {
                val ok = onAnswer(idx)
                feedbackIsCorrect = ok
                selectedFeedback = if (ok) step.explanation else "Incorrect mitigation. Review Zero-Trust principles and try again."
              }
          ) {
            Text(
              text = option,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      }

      selectedFeedback?.let { feedback ->
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (feedbackIsCorrect) CyberEmerald.copy(alpha = 0.15f) else NeonPink.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, if (feedbackIsCorrect) CyberEmerald else NeonPink),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = feedback,
            style = MaterialTheme.typography.bodySmall,
            color = if (feedbackIsCorrect) CyberEmerald else NeonPink,
            modifier = Modifier.padding(8.dp)
          )
        }
      }
    }
  }
}
