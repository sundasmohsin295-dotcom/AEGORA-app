package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.data.AegoraRepository
import com.example.model.TopicHistoricalMemory
import com.example.ui.components.CodeTerminalView
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

@Composable
fun LessonDetailScreen(
  lessonId: String,
  onNavigateBack: () -> Unit,
  onNavigateToQuiz: (String) -> Unit,
  onNavigateToVault: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val lesson = AegoraRepository.lessons.find { it.id == lessonId }
    ?: AegoraRepository.lessons.first()

  val bookmarkedIds by AegoraRepository.bookmarkedLessonIds.collectAsState()
  val isBookmarked = bookmarkedIds.contains(lesson.id)
  val historicalMemories by AegoraRepository.topicHistoricalMemories.collectAsState()

  var showSimplifiedAnalogy by remember { mutableStateOf(false) }
  var showDeepDive by remember { mutableStateOf(false) }
  var isCompleted by remember { mutableStateOf(false) }
  var showAddNoteDialog by remember { mutableStateOf(false) }
  var noteSnippetToSave by remember { mutableStateOf("") }
  var noteContentInput by remember { mutableStateOf("") }

  val relevantMemory: TopicHistoricalMemory? = historicalMemories.values.firstOrNull()

  Scaffold(
    topBar = {
      Surface(
        color = CyberBackground,
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
          Spacer(modifier = Modifier.width(4.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = lesson.moduleTitle.uppercase(),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = VibrantPurpleOnContainer
            )
            Text(
              text = lesson.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark,
              maxLines = 1
            )
          }
          IconButton(onClick = { AegoraRepository.toggleBookmark(lesson.id) }) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "Bookmark",
              tint = if (isBookmarked) VibrantPinkOnContainer else TextSecondaryDark
            )
          }
          IconButton(onClick = {
            noteSnippetToSave = lesson.coreExplanation.take(160)
            showAddNoteDialog = true
          }) {
            Icon(
              imageVector = Icons.Default.EditNote,
              contentDescription = "Add to Vault",
              tint = VibrantPurpleOnContainer
            )
          }
        }
      }
    },
    bottomBar = {
      Surface(
        color = CyberSurfaceVariant,
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars),
        border = BorderStroke(1.dp, CyberBorderSubtle)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = { onNavigateToQuiz("qz_01") },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = VibrantPurpleOnContainer),
            border = BorderStroke(1.dp, VibrantPurpleContainer),
            shape = RoundedCornerShape(16.dp)
          ) {
            Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Knowledge Check", fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              if (!isCompleted) {
                isCompleted = true
                AegoraRepository.completeLesson(lesson.id)
              }
              onNavigateBack()
            },
            modifier = Modifier
              .weight(1f)
              .testTag("lesson_complete_btn"),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isCompleted) VibrantEmeraldContainer else VibrantPurpleContainer
            ),
            shape = RoundedCornerShape(16.dp)
          ) {
            Text(
              text = if (isCompleted) "Completed ✓" else "Complete (+100 XP)",
              color = if (isCompleted) VibrantEmeraldOnContainer else VibrantPurpleOnContainer,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .background(CyberBackground)
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 0. Cross-Session Memory Retrospective Card
      relevantMemory?.let { mem ->
        item {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurfaceElevated,
            border = BorderStroke(1.dp, CyberViolet.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().testTag("cross_session_memory_card")
          ) {
            Column(
              modifier = Modifier
                .background(
                  Brush.horizontalGradient(
                    listOf(CyberViolet.copy(alpha = 0.15f), CyberSurfaceElevated)
                  )
                )
                .padding(14.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "CROSS-SESSION RETROSPECTIVE MEMORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontWeight = FontWeight.Bold,
                      fontSize = 9.5.sp
                    ),
                    color = CyberViolet
                  )
                }
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = CyberSurface,
                  border = BorderStroke(0.5.dp, CyberBorder)
                ) {
                  Text(
                    text = "Score: ${mem.priorScore}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                    color = if (mem.priorScore >= 80) CyberEmerald else CyberAmber,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = mem.proactiveGuidanceMessage,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark
              )

              if (mem.pastMistakeNoted != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = CyberSurface,
                  border = BorderStroke(0.5.dp, CyberCyan.copy(alpha = 0.4f))
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Historical Focus: ${mem.pastMistakeNoted}",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                      color = CyberCyan
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 1. Core Architecture Card
      item {
        CyberCard(
          borderColor = CyberBorderSubtle,
          backgroundColor = CyberSurfaceVariant,
          shapeRadius = 20.dp
        ) {
          Text(
            text = "CORE TELEMETRY & ATTACK DYNAMICS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = VibrantPurpleOnContainer
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = lesson.coreExplanation,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimaryDark,
            lineHeight = 24.sp
          )
        }
      }

      // 2. Interactive Simplified Analogy Card
      item {
        CyberCard(
          borderColor = CyberBorderSubtle,
          backgroundColor = VibrantAmberContainer.copy(alpha = 0.35f),
          shapeRadius = 20.dp
        ) {
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
                  .background(VibrantAmberContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = VibrantAmberOnContainer, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Mental Analogy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                Text("Explain simply without jargon", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
              }
            }
            Switch(
              checked = showSimplifiedAnalogy,
              onCheckedChange = { showSimplifiedAnalogy = it }
            )
          }

          AnimatedVisibility(visible = showSimplifiedAnalogy) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
              HorizontalDivider(color = CyberBorderSubtle)
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = lesson.simplifiedAnalogy,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark,
                lineHeight = 22.sp
              )
            }
          }
        }
      }

      // 3. Live Forensic Telemetry (Code/Log Snippet)
      if (lesson.codeOrTerminalSnippet != null) {
        item {
          CyberSectionHeader(
            title = "Live Forensic Telemetry",
            subtitle = "Sysmon event log / Wireshark packet capture"
          )
          CodeTerminalView(code = lesson.codeOrTerminalSnippet)
        }
      }

      // 4. Deep Dive Technical Section
      item {
        CyberCard(
          borderColor = CyberBorderSubtle,
          backgroundColor = VibrantBlueContainer.copy(alpha = 0.35f),
          shapeRadius = 20.dp
        ) {
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
                  .background(VibrantBlueContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Engineering, contentDescription = null, tint = VibrantBlueOnContainer, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text("Detection Engineering", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
                Text("Sigma rules & EDR hunting logic", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
              }
            }
            Switch(
              checked = showDeepDive,
              onCheckedChange = { showDeepDive = it }
            )
          }

          AnimatedVisibility(visible = showDeepDive) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
              HorizontalDivider(color = CyberBorderSubtle)
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = lesson.deepDiveTechnical,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimaryDark,
                lineHeight = 22.sp
              )
            }
          }
        }
      }

      // 5. Key Defensive Takeaways
      item {
        CyberCard(
          borderColor = CyberBorderSubtle,
          backgroundColor = VibrantEmeraldContainer.copy(alpha = 0.35f),
          shapeRadius = 20.dp
        ) {
          Column {
            Text(
              text = "KEY DEFENSIVE TAKEAWAYS",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = VibrantEmeraldOnContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            for (point in lesson.keyTakeaways) {
              Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text("• ", color = VibrantEmeraldOnContainer, fontWeight = FontWeight.Bold)
                Text(point, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark)
              }
            }
          }
        }
      }
    }
  }

  // Save Note Dialog
  if (showAddNoteDialog) {
    AlertDialog(
      onDismissRequest = { showAddNoteDialog = false },
      title = { Text("Save Note to Knowledge Vault", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Lesson: ${lesson.title}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = VibrantPurpleOnContainer
          )
          if (noteSnippetToSave.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurfaceElevated,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "“$noteSnippetToSave”",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryDark,
                modifier = Modifier.padding(8.dp)
              )
            }
          }
          OutlinedTextField(
            value = noteContentInput,
            onValueChange = { noteContentInput = it },
            placeholder = { Text("Your insight, memory hook, or analysis...") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (noteContentInput.isNotBlank()) {
              AegoraRepository.addNote(
                lessonId = lesson.id,
                lessonTitle = lesson.title,
                domain = lesson.moduleTitle,
                highlightedText = noteSnippetToSave.ifBlank { null },
                noteContent = noteContentInput,
                tags = listOf("LessonNote", lesson.moduleTitle.take(8))
              )
              noteContentInput = ""
              showAddNoteDialog = false
            }
          },
          enabled = noteContentInput.isNotBlank()
        ) {
          Text("Save to Vault")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddNoteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

