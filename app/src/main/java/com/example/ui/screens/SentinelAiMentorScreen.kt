package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class ChatMessage(
  val sender: String,
  val text: String,
  val isAi: Boolean = true
)

@Composable
fun SentinelAiMentorScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var userPrompt by remember { mutableStateOf("") }
  val clipboardManager = LocalClipboardManager.current
  var queryCopied by remember { mutableStateOf(false) }

  val chatMessages = remember {
    mutableStateListOf(
      ChatMessage("SENTINEL AI", "Operator, I have analyzed telemetry for INC-20481. The PowerShell execution exhibits credential scraping signatures matching MITRE T1059.001. Awaiting your command.")
    )
  }

  fun sendPrompt() {
    if (userPrompt.isBlank()) return
    val query = userPrompt.trim()
    chatMessages.add(ChatMessage("OPERATOR", query, isAi = false))
    userPrompt = ""

    // Autonomous SOC reasoning response
    val aiResponse = when {
      query.contains("powershell", ignoreCase = true) ->
        "PowerShell process spawned with base64 encoded payload: [IEX (New-Object Net.WebClient)...]. Immediate host isolation on WIN-SRV-03 is strongly advised."
      query.contains("ip", ignoreCase = true) || query.contains("block", ignoreCase = true) ->
        "Ingress source IP 45.32.11.76 has been flagged across 14 global threat feeds. Firewall DROP rule has been autonomously staged."
      else ->
        "Affirmative. Correlating telemetry against active Merkle audit blocks. Threat score elevated to 72/100. All zero-trust credentials revoked for user 'jdoe'."
    }
    chatMessages.add(ChatMessage("SENTINEL AI", aiResponse, isAi = true))
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(ObsidianBackground)
      .padding(16.dp)
      .testTag("sentinel_ai_mentor_screen")
  ) {
    // 1. Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(ObsidianSurface)
            .border(1.dp, SlateBorder, CircleShape)
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ElectricCyan, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(ElectricCyan.copy(alpha = 0.15f))
            .border(1.dp, ElectricCyan, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.SmartToy, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text("SENTINEL AI", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
          Text("Your AI SOC Mentor", color = TextDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
      }

      Box(
        modifier = Modifier
          .background(TacticalEmeraldDark, RoundedCornerShape(4.dp))
          .border(1.dp, TacticalEmerald, RoundedCornerShape(4.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text("NEURAL ACTIVE", color = TacticalEmerald, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 2. Incident Summary & Confidence Ring Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
          border = BorderStroke(1.dp, SlateBorder),
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("Incident Detected:", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              Text("Possible credential compromise.", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
              Spacer(modifier = Modifier.height(8.dp))
              Text("Recommended Investigation:", color = ElectricCyan, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
              Text("1. Inspect authentication logs\n2. Check unusual source IPs\n3. Review privilege escalation", color = TextMuted, fontSize = 10.sp, lineHeight = 14.sp, fontFamily = FontFamily.Monospace)
            }

            // Confidence Dial
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(64.dp)) {
                  val stroke = 6.dp.toPx()
                  drawArc(SlateBorder, 0f, 360f, false, style = Stroke(stroke))
                  drawArc(TacticalEmerald, -90f, 360f * 0.94f, false, style = Stroke(stroke))
                }
                Text("94%", color = TacticalEmerald, fontWeight = FontWeight.Bold, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text("Confidence", color = TextDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
          }
        }
      }

      // 3. Explanation Card: "Why is this alert suspicious?"
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
          border = BorderStroke(1.dp, SlateBorder),
          shape = RoundedCornerShape(10.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Why is this alert suspicious?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              BulletItem("Unusual login from a foreign IP (45.32.11.76)")
              BulletItem("PowerShell execution (T1059.001)")
              BulletItem("Attempted access to protected domain LSASS key buffer")
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Suggested Query
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianBackground, RoundedCornerShape(6.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(6.dp))
                .padding(10.dp)
            ) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Suggested SIEM Query:", color = TextDim, fontSize = 9.5.sp, fontFamily = FontFamily.Monospace)
                Text(
                  if (queryCopied) "COPIED!" else "COPY",
                  color = ElectricCyan,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace,
                  modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString("index=security EventID=4688 | where ProcessName=\"powershell.exe\""))
                    queryCopied = true
                  }
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                "index=security EventID=4688 | where ProcessName=\"powershell.exe\"",
                color = ElectricCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }
      }

      // 4. Chat Feed
      items(chatMessages) { msg ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = if (msg.isAi) Arrangement.Start else Arrangement.End
        ) {
          Box(
            modifier = Modifier
              .widthIn(max = 320.dp)
              .background(
                if (msg.isAi) ObsidianSurface else ElectricCyanDark,
                RoundedCornerShape(8.dp)
              )
              .border(
                1.dp,
                if (msg.isAi) SlateBorder else ElectricCyan,
                RoundedCornerShape(8.dp)
              )
              .padding(10.dp)
          ) {
            Column {
              Text(
                text = msg.sender,
                color = if (msg.isAi) ElectricCyan else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = msg.text,
                color = TextPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 15.sp
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 5. Input Field
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(ObsidianSurface, RoundedCornerShape(8.dp))
        .border(1.dp, SlateBorder, RoundedCornerShape(8.dp))
        .padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      TextField(
        value = userPrompt,
        onValueChange = { userPrompt = it },
        placeholder = { Text("Ask Sentinel anything...", color = TextDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
        colors = TextFieldDefaults.colors(
          focusedContainerColor = Color.Transparent,
          unfocusedContainerColor = Color.Transparent,
          focusedIndicatorColor = Color.Transparent,
          unfocusedIndicatorColor = Color.Transparent,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        ),
        modifier = Modifier.weight(1f),
        singleLine = true
      )

      IconButton(
        onClick = { sendPrompt() },
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(ElectricCyan)
      ) {
        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = ObsidianBackground, modifier = Modifier.size(16.dp))
      }
    }
  }
}

@Composable
private fun BulletItem(text: String) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(ElectricCyan))
    Text(text, color = TextMuted, fontSize = 10.5.sp, fontFamily = FontFamily.Monospace)
  }
}
