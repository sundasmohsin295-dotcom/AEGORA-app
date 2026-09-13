package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class OsintChatMessage(
  val id: String = UUID.randomUUID().toString(),
  val sender: OsintSender,
  val text: String,
  val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date()),
  val tags: List<String> = emptyList(),
  val isCodeOrIoc: Boolean = false
)

enum class OsintSender {
  USER,
  OSINT_AGENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsintAgentChatScreen(
  onNavigateBack: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val obsidianBg = Color(0xFF090A0C)
  val matteSteel = Color(0xFF15171C)
  val slateBorder = Color(0xFF2D313A)
  val cobaltBlue = Color(0xFF2962FF)
  val emeraldGreen = Color(0xFF00E676)
  val mutedSlate = Color(0xFF8A919E)

  var inputText by remember { mutableStateOf("") }
  var isAgentThinking by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val messages = remember {
    mutableStateListOf(
      OsintChatMessage(
        sender = OsintSender.OSINT_AGENT,
        text = "AEGORA AUTONOMOUS OSINT RADAR ACTIVE.\n" +
          "Monitoring open-source intelligence feeds, CTF registries, darknet forum dumps, and global adversary C2 telemetry.",
        tags = listOf("FEED: REALTIME", "AGENT: RECON-9"),
        isCodeOrIoc = true
      )
    )
  }

  val quickActionPills = remember {
    listOf(
      "[🔍 Latest Tech Hackathons]",
      "[🌐 APT C2 Infrastructure]",
      "[⚡ Zero-Day Telegram Leaks]",
      "[🕵️ Credential Breach Dump]"
    )
  }

  fun sendQuery(query: String) {
    if (query.isBlank()) return
    val userMsg = OsintChatMessage(
      sender = OsintSender.USER,
      text = query.trim()
    )
    messages.add(userMsg)
    inputText = ""

    coroutineScope.launch {
      delay(50)
      listState.animateScrollToItem(messages.size - 1)
      isAgentThinking = true

      // Graceful degradation simulation (< 300ms responsive feedback)
      delay(250)
      isAgentThinking = false

      val responseText = when {
        query.contains("Hackathon", ignoreCase = true) -> {
          "TARGET INTEL RECON: LIVE CYBER & AI HACKATHONS\n" +
            "---------------------------------------------------\n" +
            "1. Google AI Studio Global Agentic Challenge (Online)\n" +
            "   Focus: Multi-agent orchestrations, Gemini 2.5 Flash telemetry, Tool calling.\n" +
            "   Status: ACTIVE | Prize: $100,000 | Target: Defensive Cyber Agents\n\n" +
            "2. DEF CON 34 AI x Red Team Cyber CTF (Las Vegas / Remote)\n" +
            "   Focus: Jailbreak defenses, LLM adversarial injection, Memory exploits.\n" +
            "   Status: OPEN REGISTRATION | Verification: Ed25519 Signed\n\n" +
            "3. HackTheBox Global University CTF 2026\n" +
            "   Focus: Reverse engineering, Active Directory lateral movement, Sigma rules.\n" +
            "   Status: LIVE TELEMETRY STREAM"
        }
        query.contains("C2", ignoreCase = true) -> {
          "ADVERSARY C2 INFRASTRUCTURE TELEMETRY:\n" +
            "Detected 14 active Cobalt Strike HTTPS stagers resolving to Bulletproof ASN 49822.\n" +
            "IOC: 185.220.101.42 (TLS Cert SHA-256: 4f8a...92b1) -> Beacon interval 45s."
        }
        query.contains("Zero-Day", ignoreCase = true) -> {
          "ZERO-DAY EARLY WARNING FEED:\n" +
            "CVE-2026-8819: Remote Code Execution in Kerberos Ticket Granting Service.\n" +
            "Mitigation: Enforce PAC validation and disable RC4-HMAC cipher suites immediately."
        }
        else -> {
          "OSINT TELEMETRY REPORT FOR [${query.take(32)}]:\n" +
            "Cross-referenced against VirusTotal, Shodan, and MITRE ATT&CK Enterprise Matrix.\n" +
            "No public breach correlation found in raw OSINT indices. Threat severity: LOW."
        }
      }

      val agentMsg = OsintChatMessage(
        sender = OsintSender.OSINT_AGENT,
        text = responseText,
        tags = listOf("IOC-VERIFIED", "MERKLE-LEAF"),
        isCodeOrIoc = query.contains("Hackathon") || query.contains("C2")
      )
      messages.add(agentMsg)
      delay(50)
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(obsidianBg)
      .imePadding(), // Ensure software keyboard never overlaps critical UI
    containerColor = obsidianBg,
    topBar = {
      Surface(
        color = matteSteel,
        border = BorderStroke(1.dp, slateBorder),
        modifier = Modifier.fillMaxWidth().statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = onNavigateBack,
              modifier = Modifier.size(36.dp).testTag("osint_back_button")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "OSINT THREAT INTEL AGENT",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Autonomous Reconnaissance & Vulnerability Radar",
                color = mutedSlate,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }

          // Online telemetry status badge
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0x2200E676),
            border = BorderStroke(1.dp, emeraldGreen.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(emeraldGreen))
              Text(
                text = "ONLINE",
                color = emeraldGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(matteSteel)
          .border(BorderStroke(1.dp, slateBorder))
          .navigationBarsPadding()
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        // Quick Action Filter / OSINT Pills
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
          items(quickActionPills) { pill ->
            val isHackathonPill = pill.contains("Hackathons")
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (isHackathonPill) cobaltBlue.copy(alpha = 0.2f) else obsidianBg,
              border = BorderStroke(1.dp, if (isHackathonPill) cobaltBlue else slateBorder),
              modifier = Modifier
                .clickable { sendQuery(pill) }
                .testTag(if (isHackathonPill) "pill_latest_tech_hackathons" else "pill_${pill.filter { it.isLetterOrDigit() }}")
            ) {
              Text(
                text = pill,
                color = if (isHackathonPill) Color.White else mutedSlate,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
              )
            }
          }
        }

        // Message Input Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = {
              Text("Enter target domain, CVE, or threat actor...", color = mutedSlate, fontSize = 12.sp)
            },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = obsidianBg,
              unfocusedContainerColor = obsidianBg,
              focusedBorderColor = cobaltBlue,
              unfocusedBorderColor = slateBorder,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .weight(1f)
              .heightIn(min = 46.dp)
              .testTag("osint_input_field"),
            singleLine = true
          )

          IconButton(
            onClick = { sendQuery(inputText) },
            enabled = inputText.isNotBlank(),
            modifier = Modifier
              .size(46.dp)
              .background(if (inputText.isNotBlank()) cobaltBlue else matteSteel, RoundedCornerShape(10.dp))
              .border(1.dp, if (inputText.isNotBlank()) cobaltBlue else slateBorder, RoundedCornerShape(10.dp))
              .testTag("osint_send_button")
          ) {
            Icon(
              Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = if (inputText.isNotBlank()) Color.White else mutedSlate,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 14.dp, vertical = 10.dp)
        .testTag("osint_chat_messages_list"),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        val isUser = msg.sender == OsintSender.USER
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("osint_message_${msg.id}"),
          horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
          // Sender Header Tag
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 3.dp)
          ) {
            Text(
              text = if (isUser) "OPERATOR [YOU]" else "AEGORA OSINT AGENT",
              color = if (isUser) cobaltBlue else emeraldGreen,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = msg.timestamp,
              color = mutedSlate,
              fontSize = 9.sp,
              fontFamily = FontFamily.Monospace
            )
          }

          // Message Container
          Surface(
            shape = RoundedCornerShape(
              topStart = 10.dp,
              topEnd = 10.dp,
              bottomStart = if (isUser) 10.dp else 2.dp,
              bottomEnd = if (isUser) 2.dp else 10.dp
            ),
            color = if (isUser) cobaltBlue.copy(alpha = 0.25f) else matteSteel,
            border = BorderStroke(1.dp, if (isUser) cobaltBlue else slateBorder),
            modifier = Modifier.widthIn(max = 320.dp).testTag(if (isUser) "user_chat_bubble" else "agent_chat_bubble")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = msg.text,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontFamily = if (msg.isCodeOrIoc) FontFamily.Monospace else FontFamily.Default
              )

              if (msg.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  msg.tags.forEach { tagText ->
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = obsidianBg,
                      border = BorderStroke(1.dp, slateBorder)
                    ) {
                      Text(
                        text = tagText,
                        color = emeraldGreen,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      if (isAgentThinking) {
        item {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 6.dp)
          ) {
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              color = cobaltBlue,
              strokeWidth = 2.dp
            )
            Text(
              text = "AGENT TRAVERSING OSINT GRAPH...",
              color = mutedSlate,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  }
}
