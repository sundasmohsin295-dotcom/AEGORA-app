package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.FlashcardReviewItem
import com.example.model.LessonNote
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

enum class VaultTab(val title: String) {
  NOTES("Knowledge Notes"),
  BOOKMARKS("Bookmarks"),
  SPACED_REVIEW("Review Queue")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeVaultScreen(
  onNavigateBack: () -> Unit,
  onNavigateToLesson: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(VaultTab.NOTES) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedDomainFilter by remember { mutableStateOf("All") }
  var showAddNoteDialog by remember { mutableStateOf(false) }

  val notes by AegoraRepository.userNotes.collectAsState()
  val bookmarkedIds by AegoraRepository.bookmarkedLessonIds.collectAsState()
  val reviewQueue by AegoraRepository.reviewQueue.collectAsState()
  val allLessons = AegoraRepository.lessons

  val bookmarkedLessons = remember(bookmarkedIds, allLessons) {
    allLessons.filter { bookmarkedIds.contains(it.id) }
  }

  val domains = listOf("All", "Defensive Security", "Network Security", "Cloud Security", "Forensics & Response")

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Knowledge Vault",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
            Text(
              text = "Personal Notes, Bookmarks & Spaced Repetition",
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
        actions = {
          if (selectedTab == VaultTab.NOTES) {
            FilledTonalButton(
              onClick = { showAddNoteDialog = true },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = VibrantPurpleContainer,
                contentColor = VibrantPurpleOnContainer
              ),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("New Note", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBackground)
      )
    },
    containerColor = CyberBackground,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
    ) {
      // 1. Vault Tab Selector
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = CyberSurfaceVariant,
        border = BorderStroke(1.dp, CyberBorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(modifier = Modifier.padding(4.dp)) {
          VaultTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            val dueCount = if (tab == VaultTab.SPACED_REVIEW) reviewQueue.count { it.isDue } else 0
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) VibrantPurpleContainer else Color.Transparent)
                .clickable { selectedTab = tab }
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = tab.title,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  ),
                  color = if (isSelected) VibrantPurpleOnContainer else TextSecondaryDark
                )
                if (dueCount > 0 && tab == VaultTab.SPACED_REVIEW) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .clip(CircleShape)
                      .background(VibrantPinkContainer)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "$dueCount",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = VibrantPinkOnContainer
                    )
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 2. Search and Domain Filter (For Notes and Bookmarks)
      if (selectedTab != VaultTab.SPACED_REVIEW) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search notes, keywords, tags...", color = TextSecondaryDark) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondaryDark)
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = CyberBorder,
            focusedContainerColor = CyberSurfaceVariant,
            unfocusedContainerColor = CyberSurfaceVariant,
            focusedTextColor = TextPrimaryDark,
            unfocusedTextColor = TextPrimaryDark
          ),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(domains) { domain ->
            val isSelected = selectedDomainFilter == domain
            FilterChip(
              selected = isSelected,
              onClick = { selectedDomainFilter = domain },
              label = { Text(domain) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VibrantBlueContainer,
                selectedLabelColor = VibrantBlueOnContainer,
                containerColor = CyberSurfaceVariant,
                labelColor = TextSecondaryDark
              ),
              border = BorderStroke(1.dp, if (isSelected) VibrantBlueContainer else CyberBorderSubtle),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))
      }

      // 3. Tab Content
      when (selectedTab) {
        VaultTab.NOTES -> {
          val filteredNotes = notes.filter { note ->
            (selectedDomainFilter == "All" || note.domain.contains(selectedDomainFilter, ignoreCase = true)) &&
                (searchQuery.isBlank() ||
                    note.noteContent.contains(searchQuery, ignoreCase = true) ||
                    note.lessonTitle.contains(searchQuery, ignoreCase = true) ||
                    note.tags.any { it.contains(searchQuery, ignoreCase = true) } ||
                    (note.highlightedText?.contains(searchQuery, ignoreCase = true) == true))
          }

          if (filteredNotes.isEmpty()) {
            EmptyVaultState(
              icon = Icons.Default.EditNote,
              title = "No notes found",
              subtitle = if (notes.isEmpty()) "Highlight text inside a lesson or add a freeform note to build your personal cyber knowledge vault." else "No notes match your active filter."
            )
          } else {
            LazyColumn(
              verticalArrangement = Arrangement.spacedBy(12.dp),
              contentPadding = PaddingValues(bottom = 32.dp),
              modifier = Modifier.fillMaxSize()
            ) {
              items(filteredNotes, key = { it.id }) { note ->
                NoteCard(
                  note = note,
                  onNavigateToLesson = { onNavigateToLesson(note.lessonId) },
                  onDelete = { AegoraRepository.deleteNote(note.id) }
                )
              }
            }
          }
        }

        VaultTab.BOOKMARKS -> {
          val filteredBookmarks = bookmarkedLessons.filter { lesson ->
            (selectedDomainFilter == "All" || lesson.moduleTitle.contains(selectedDomainFilter, ignoreCase = true)) &&
                (searchQuery.isBlank() ||
                    lesson.title.contains(searchQuery, ignoreCase = true) ||
                    lesson.coreExplanation.contains(searchQuery, ignoreCase = true))
          }

          if (filteredBookmarks.isEmpty()) {
            EmptyVaultState(
              icon = Icons.Default.BookmarkBorder,
              title = "No bookmarks yet",
              subtitle = "Tap the bookmark icon in any lesson detail view to save lessons for quick reference."
            )
          } else {
            LazyColumn(
              verticalArrangement = Arrangement.spacedBy(12.dp),
              contentPadding = PaddingValues(bottom = 32.dp),
              modifier = Modifier.fillMaxSize()
            ) {
              items(filteredBookmarks, key = { it.id }) { lesson ->
                CyberCard(
                  borderColor = CyberBorderSubtle,
                  backgroundColor = CyberSurfaceVariant,
                  shapeRadius = 18.dp,
                  onClick = { onNavigateToLesson(lesson.id) }
                ) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(VibrantPurpleContainer),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.School, contentDescription = null, tint = VibrantPurpleOnContainer, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = lesson.moduleTitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = VibrantPurpleOnContainer
                      )
                      Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimaryDark
                      )
                      Spacer(modifier = Modifier.height(2.dp))
                      Text(
                        text = "⏱ ${lesson.estimatedReadMinutes} min read • Knowledge Sprint",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryDark
                      )
                    }
                    IconButton(onClick = { AegoraRepository.toggleBookmark(lesson.id) }) {
                      Icon(Icons.Default.Bookmark, contentDescription = "Remove Bookmark", tint = VibrantPinkOnContainer)
                    }
                  }
                }
              }
            }
          }
        }

        VaultTab.SPACED_REVIEW -> {
          SpacedReviewQueueView(
            reviewItems = reviewQueue,
            onRecordResult = { cardId, isCorrect ->
              AegoraRepository.recordReviewResult(cardId, isCorrect)
            }
          )
        }
      }
    }
  }

  // Add Freeform Note Dialog
  if (showAddNoteDialog) {
    AddNoteDialog(
      onDismiss = { showAddNoteDialog = false },
      onSaveNote = { lessonId, lessonTitle, domain, noteContent, tags ->
        AegoraRepository.addNote(
          lessonId = lessonId,
          lessonTitle = lessonTitle,
          domain = domain,
          highlightedText = null,
          noteContent = noteContent,
          tags = tags
        )
        showAddNoteDialog = false
      }
    )
  }
}

@Composable
fun NoteCard(
  note: LessonNote,
  onNavigateToLesson: () -> Unit,
  onDelete: () -> Unit
) {
  CyberCard(
    borderColor = CyberBorderSubtle,
    backgroundColor = CyberSurfaceVariant,
    shapeRadius = 18.dp
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(VibrantPurpleContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = note.domain,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = VibrantPurpleOnContainer
          )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = note.createdDate,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark
          )
          Spacer(modifier = Modifier.width(4.dp))
          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Note", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = note.lessonTitle,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark,
        modifier = Modifier.clickable { onNavigateToLesson() }
      )

      if (!note.highlightedText.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = CyberSurfaceElevated,
          border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(modifier = Modifier.padding(10.dp)) {
            Box(
              modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(CyberCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "“${note.highlightedText}”",
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
              ),
              color = TextPrimaryDark
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = note.noteContent,
        style = MaterialTheme.typography.bodyMedium,
        color = TextPrimaryDark
      )

      if (note.tags.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          note.tags.forEach { tag ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(VibrantBlueContainer)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "#$tag",
                style = MaterialTheme.typography.labelSmall,
                color = VibrantBlueOnContainer
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SpacedReviewQueueView(
  reviewItems: List<FlashcardReviewItem>,
  onRecordResult: (String, Boolean) -> Unit
) {
  val dueItems = remember(reviewItems) { reviewItems.filter { it.isDue } }
  var currentCardIndex by remember { mutableStateOf(0) }
  var isFlipped by remember { mutableStateOf(false) }

  if (dueItems.isEmpty()) {
    EmptyVaultState(
      icon = Icons.Default.CheckCircleOutline,
      title = "All caught up on reviews! 🎉",
      subtitle = "No flashcards are due right now. Complete lesson quizzes to automatically schedule spaced repetition reviews (1, 3, 7, and 14 days interval)."
    )
  } else {
    val activeItem = dueItems.getOrNull(currentCardIndex.coerceIn(0, dueItems.size - 1))

    if (activeItem != null) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Progress header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CARD ${currentCardIndex + 1} OF ${dueItems.size}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = CyberCyan
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(VibrantAmberContainer)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "Repetition Level ${activeItem.repetitionLevel} • ${activeItem.intervalDays}d Interval",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = VibrantAmberOnContainer
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Flip Card
        CyberCard(
          borderColor = if (isFlipped) CyberCyan else CyberBorder,
          backgroundColor = if (isFlipped) CyberSurfaceElevated else CyberSurfaceVariant,
          shapeRadius = 24.dp,
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clickable { isFlipped = !isFlipped }
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = activeItem.domain.uppercase(),
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = VibrantPurpleOnContainer
                )
                Text(
                  text = if (isFlipped) "ANSWER (TAP TO FLIP BACK)" else "QUESTION (TAP TO REVEAL)",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              if (!isFlipped) {
                Text(
                  text = activeItem.question,
                  style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "Lesson Source: ${activeItem.lessonTitle}",
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextSecondaryDark
                )
              } else {
                Text(
                  text = activeItem.answer,
                  style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                  color = VibrantEmeraldOnContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                  text = "Detailed Breakdown:",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = activeItem.detailedExplanation,
                  style = MaterialTheme.typography.bodyMedium,
                  color = TextSecondaryDark
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                if (isFlipped) Icons.Default.FlipToBack else Icons.Default.FlipToFront,
                contentDescription = null,
                tint = TextSecondaryDark,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isFlipped) "Tap card to review question" else "Tap card to reveal answer",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondaryDark
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        if (isFlipped) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Button(
              onClick = {
                onRecordResult(activeItem.id, false)
                isFlipped = false
                if (currentCardIndex >= dueItems.size - 1) {
                  currentCardIndex = 0
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = VibrantPinkContainer),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
            ) {
              Icon(Icons.Default.Close, contentDescription = null, tint = VibrantPinkOnContainer)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Needs Work (Reset to Day 1)", color = VibrantPinkOnContainer, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                onRecordResult(activeItem.id, true)
                isFlipped = false
                if (currentCardIndex >= dueItems.size - 1) {
                  currentCardIndex = 0
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = VibrantEmeraldContainer),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = VibrantEmeraldOnContainer)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Got it! (+Interval)", color = VibrantEmeraldOnContainer, fontWeight = FontWeight.Bold)
            }
          }
        } else {
          Button(
            onClick = { isFlipped = true },
            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurpleContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
          ) {
            Icon(Icons.Default.Visibility, contentDescription = null, tint = VibrantPurpleOnContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reveal Answer", color = VibrantPurpleOnContainer, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun EmptyVaultState(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(CircleShape)
          .background(CyberSurfaceVariant),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(32.dp))
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }
  }
}

@Composable
fun AddNoteDialog(
  onDismiss: () -> Unit,
  onSaveNote: (String, String, String, String, List<String>) -> Unit
) {
  var lessonTitle by remember { mutableStateOf("General Cybersecurity Knowledge") }
  var domain by remember { mutableStateOf("Defensive Security") }
  var noteContent by remember { mutableStateOf("") }
  var tagsText by remember { mutableStateOf("SOC, BestPractices") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create Knowledge Note", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = lessonTitle,
          onValueChange = { lessonTitle = it },
          label = { Text("Topic / Lesson Context") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = domain,
          onValueChange = { domain = it },
          label = { Text("Domain (e.g. Defensive, Network, Cloud)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = noteContent,
          onValueChange = { noteContent = it },
          label = { Text("Your Note / Key Insight") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = tagsText,
          onValueChange = { tagsText = it },
          label = { Text("Tags (comma-separated)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (noteContent.isNotBlank()) {
            val tags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            onSaveNote("general_note", lessonTitle, domain, noteContent, tags)
          }
        },
        enabled = noteContent.isNotBlank()
      ) {
        Text("Save Note")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
