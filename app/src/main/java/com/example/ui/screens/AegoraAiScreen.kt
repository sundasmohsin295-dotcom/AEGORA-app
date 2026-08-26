package com.example.ui.screens

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
import com.example.data.AegoraRepository
import com.example.data.GeminiMentorService
import com.example.model.AiChatMessage
import com.example.model.AiMentorMode
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AegoraAiScreen(
  modifier: Modifier = Modifier
) {
  val messages by AegoraRepository.chatMessages.collectAsState()
  val userProfile by AegoraRepository.userProfile.collectAsState()
  var currentMode by remember { mutableStateOf(AiMentorMode.SOC_MENTOR) }
  var inputText by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }

  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  Scaffold(
    modifier = modifier.fillMaxSize().background(CyberBackground),
    topBar = {
      Surface(
        color = CyberBackground,
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        tonalElevation = 4.dp
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(CyberIndigo.copy(alpha = 0.3f))
                  .border(1.dp, CyberCyan, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "AEGORA AI MENTOR",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
                Text(
                  text = "${currentMode.displayName} • ${currentMode.badge}",
                  style = MaterialTheme.typography.labelSmall,
                  color = CyberCyan
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyberEmerald))
                Spacer(modifier = Modifier.width(6.dp))
                Text("LIVE MENTOR", style = MaterialTheme.typography.labelSmall, color = TextPrimaryDark)
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Mentor Mode Selector Pills
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(AiMentorMode.entries) { mode ->
              val isSelected = mode == currentMode
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) CyberSurfaceElevated else CyberSurface,
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) CyberCyan else CyberBorderSubtle
                ),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { currentMode = mode }
                  .testTag("ai_mode_${mode.name.lowercase()}")
              ) {
                Text(
                  text = mode.displayName,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (isSelected) CyberCyan else TextSecondaryDark,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    },
    bottomBar = {
      Surface(
        color = CyberSurface,
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
          .padding(bottom = 72.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          // Input Box
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = inputText,
              onValueChange = { inputText = it },
              placeholder = { Text("Ask ${currentMode.displayName} anything...", color = TextTertiaryDark, fontSize = 13.sp) },
              modifier = Modifier
                .weight(1f)
                .testTag("ai_message_input"),
              maxLines = 3,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = CyberBorder,
                focusedTextColor = TextPrimaryDark,
                unfocusedTextColor = TextPrimaryDark,
                focusedContainerColor = CyberSurfaceElevated,
                unfocusedContainerColor = CyberSurfaceElevated
              )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
              onClick = {
                val query = inputText.trim()
                if (query.isNotBlank() && !isLoading) {
                  inputText = ""
                  val userMsg = AiChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = "user",
                    text = query,
                    timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    mode = currentMode
                  )
                  AegoraRepository.addChatMessage(userMsg)
                  isLoading = true

                  coroutineScope.launch {
                    val (answer, followUps) = GeminiMentorService.askMentor(
                      userPrompt = query,
                      mode = currentMode,
                      userCareerContext = userProfile.targetCareerId
                    )
                    val aiMsg = AiChatMessage(
                      id = UUID.randomUUID().toString(),
                      sender = "aegora_ai",
                      text = answer,
                      timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                      mode = currentMode,
                      suggestedFollowUps = followUps
                    )
                    AegoraRepository.addChatMessage(aiMsg)
                    isLoading = false
                    listState.animateScrollToItem((messages.size + 1).coerceAtLeast(0))
                  }
                }
              },
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (inputText.isNotBlank()) CyberCyan else CyberSurfaceElevated)
                .testTag("ai_send_button")
            ) {
              if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CyberCyan, strokeWidth = 2.dp)
              } else {
                Icon(
                  imageVector = Icons.Default.Send,
                  contentDescription = "Send",
                  tint = if (inputText.isNotBlank()) CyberBackground else TextTertiaryDark,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .background(CyberBackground)
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      items(messages) { msg ->
        ChatMessageBubble(
          message = msg,
          onFollowUpClick = { followUp ->
            inputText = followUp
          }
        )
      }
    }
  }
}

@Composable
fun ChatMessageBubble(
  message: AiChatMessage,
  onFollowUpClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val isUser = message.sender == "user"

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(bottom = 4.dp)
    ) {
      if (!isUser) {
        Icon(
          imageVector = Icons.Default.Psychology,
          contentDescription = null,
          tint = CyberCyan,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = message.mode.displayName,
          style = MaterialTheme.typography.labelSmall,
          color = CyberCyan
        )
      } else {
        Text(
          text = "You",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = message.timestamp,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = TextTertiaryDark
      )
    }

    Surface(
      shape = RoundedCornerShape(
        topStart = 14.dp,
        topEnd = 14.dp,
        bottomStart = if (isUser) 14.dp else 2.dp,
        bottomEnd = if (isUser) 2.dp else 14.dp
      ),
      color = if (isUser) CyberIndigo.copy(alpha = 0.35f) else CyberSurface,
      border = androidx.compose.foundation.BorderStroke(
        1.dp,
        if (isUser) CyberIndigo else CyberBorder
      ),
      modifier = Modifier.widthIn(max = 320.dp)
    ) {
      Text(
        text = message.text,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontFamily = if (message.text.contains("`") || message.text.contains("Sysmon") || message.text.contains("T1059")) FontFamily.Monospace else FontFamily.Default,
          lineHeight = 20.sp
        ),
        color = TextPrimaryDark,
        modifier = Modifier.padding(14.dp)
      )
    }

    // Follow-up Suggestion Chips
    if (message.suggestedFollowUps.isNotEmpty()) {
      Spacer(modifier = Modifier.height(8.dp))
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        message.suggestedFollowUps.forEach { suggestion ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable { onFollowUpClick(suggestion) }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = suggestion,
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan
              )
            }
          }
        }
      }
    }
  }
}
