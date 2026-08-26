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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.CommunityPost
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedRoom by remember { mutableStateOf("All") }
  var showNewPostDialog by remember { mutableStateOf(false) }
  var activePostForReplies by remember { mutableStateOf<CommunityPost?>(null) }
  var showReportSuccessDialog by remember { mutableStateOf(false) }

  val posts by AegoraRepository.communityPosts.collectAsState()
  val rooms = listOf("All", "SOC", "Pentesting", "Cloud Security", "DFIR", "CTF", "Career")

  val filteredPosts = remember(posts, selectedRoom) {
    if (selectedRoom == "All") {
      posts.filter { !it.isReported }
    } else {
      posts.filter { it.roomCategory.equals(selectedRoom, ignoreCase = true) && !it.isReported }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Cyber Community Hub",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
            Text(
              text = "Topic Rooms, Case Discussions & Peer Support",
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
          FilledTonalButton(
            onClick = { showNewPostDialog = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = VibrantPurpleContainer,
              contentColor = VibrantPurpleOnContainer
            ),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Create Post", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
      // Notice Banner: Demo / Local-first foundation
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = VibrantBlueContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, VibrantBlueContainer),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Info, contentDescription = null, tint = VibrantBlueOnContainer, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Community Preview • Local-first proof of concept with client-side keyword moderation.",
            style = MaterialTheme.typography.labelSmall,
            color = VibrantBlueOnContainer
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Room Selector
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(rooms) { room ->
          val isSelected = selectedRoom == room
          FilterChip(
            selected = isSelected,
            onClick = { selectedRoom = room },
            label = { Text(if (room == "All") "🌐 All Rooms" else "#$room") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = VibrantPurpleContainer,
              selectedLabelColor = VibrantPurpleOnContainer,
              containerColor = CyberSurfaceVariant,
              labelColor = TextSecondaryDark
            ),
            border = BorderStroke(1.dp, if (isSelected) VibrantPurpleContainer else CyberBorderSubtle),
            shape = RoundedCornerShape(12.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Posts Stream
      if (filteredPosts.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Forum, contentDescription = null, tint = TextSecondaryDark, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("No discussions in this room yet", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Start the conversation by tapping 'Create Post' above.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(14.dp),
          contentPadding = PaddingValues(bottom = 32.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          items(filteredPosts, key = { it.id }) { post ->
            CommunityPostCard(
              post = post,
              onUpvote = { AegoraRepository.upvoteCommunityPost(post.id) },
              onOpenReplies = { activePostForReplies = post },
              onReport = {
                AegoraRepository.reportCommunityPost(post.id)
                showReportSuccessDialog = true
              }
            )
          }
        }
      }
    }
  }

  // New Post Dialog
  if (showNewPostDialog) {
    NewCommunityPostDialog(
      onDismiss = { showNewPostDialog = false },
      onSubmitPost = { room, title, body ->
        AegoraRepository.createCommunityPost(room, title, body)
        showNewPostDialog = false
      }
    )
  }

  // Replies Bottom Sheet / Modal
  if (activePostForReplies != null) {
    val livePost = posts.find { it.id == activePostForReplies?.id } ?: activePostForReplies!!
    CommunityRepliesDialog(
      post = livePost,
      onDismiss = { activePostForReplies = null },
      onAddReply = { replyBody ->
        AegoraRepository.addCommunityReply(livePost.id, replyBody)
      }
    )
  }

  // Report confirmation dialog
  if (showReportSuccessDialog) {
    AlertDialog(
      onDismissRequest = { showReportSuccessDialog = false },
      title = { Text("Post Reported", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
      text = {
        Text(
          "Thank you for keeping AEGORA safe. This post has been hidden from your feed and flagged for automated keyword review.",
          color = TextSecondaryDark
        )
      },
      confirmButton = {
        Button(onClick = { showReportSuccessDialog = false }) {
          Text("Got it")
        }
      }
    )
  }
}

@Composable
fun CommunityPostCard(
  post: CommunityPost,
  onUpvote: () -> Unit,
  onOpenReplies: () -> Unit,
  onReport: () -> Unit
) {
  var showMenu by remember { mutableStateOf(false) }

  CyberCard(
    borderColor = CyberBorderSubtle,
    backgroundColor = CyberSurfaceVariant,
    shapeRadius = 18.dp
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Header: Room badge, author, time, report menu
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(VibrantPurpleContainer)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "#${post.roomCategory}",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = VibrantPurpleOnContainer
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = post.authorName,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
          if (post.isVerifiedBadge) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Verified, contentDescription = "Verified Analyst", tint = VibrantBlueOnContainer, modifier = Modifier.size(14.dp))
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "• ${post.timestamp}",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark
          )
        }

        Box {
          IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
          }
          DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
          ) {
            DropdownMenuItem(
              text = { Text("Report Post", color = VibrantPinkOnContainer) },
              leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = VibrantPinkOnContainer) },
              onClick = {
                showMenu = false
                onReport()
              }
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = post.title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = post.body,
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Footer: Upvotes & Threaded replies count
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(VibrantEmeraldContainer)
            .clickable { onUpvote() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.ThumbUp, contentDescription = "Upvote", tint = VibrantEmeraldOnContainer, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${post.upvotes} Upvotes",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = VibrantEmeraldOnContainer
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(VibrantBlueContainer)
            .clickable { onOpenReplies() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Replies", tint = VibrantBlueOnContainer, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${post.replies.size} Replies",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = VibrantBlueOnContainer
          )
        }
      }
    }
  }
}

@Composable
fun NewCommunityPostDialog(
  onDismiss: () -> Unit,
  onSubmitPost: (String, String, String) -> Unit
) {
  var room by remember { mutableStateOf("SOC") }
  var title by remember { mutableStateOf("") }
  var body by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val rooms = listOf("SOC", "Pentesting", "Cloud Security", "DFIR", "CTF", "Career")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Start a Community Discussion", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text("Target Topic Room:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(rooms) { r ->
            FilterChip(
              selected = room == r,
              onClick = { room = r },
              label = { Text("#$r") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = VibrantPurpleContainer,
                selectedLabelColor = VibrantPurpleOnContainer
              )
            )
          }
        }

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Discussion Title") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = body,
          onValueChange = { body = it },
          label = { Text("Share your question, case insight, or takeaway...") },
          minLines = 4,
          maxLines = 6,
          modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
          Text(
            text = errorMessage!!,
            style = MaterialTheme.typography.labelSmall,
            color = VibrantPinkOnContainer
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank() && body.isNotBlank()) {
            onSubmitPost(room, title, body)
          } else {
            errorMessage = "Please enter both a title and details."
          }
        },
        enabled = title.isNotBlank() && body.isNotBlank()
      ) {
        Text("Publish Post")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

@Composable
fun CommunityRepliesDialog(
  post: CommunityPost,
  onDismiss: () -> Unit,
  onAddReply: (String) -> Unit
) {
  var replyInput by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Column {
        Text(
          text = post.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = TextPrimaryDark
        )
        Text(
          text = "By ${post.authorName} (${post.authorCallsign})",
          style = MaterialTheme.typography.labelSmall,
          color = TextSecondaryDark
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 400.dp)
      ) {
        Text(
          text = post.body,
          style = MaterialTheme.typography.bodyMedium,
          color = TextPrimaryDark
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = CyberBorderSubtle)
        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "REPLIES (${post.replies.size})",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = CyberCyan
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f, fill = false)
        ) {
          if (post.replies.isEmpty()) {
            item {
              Text("No replies yet. Be the first to chime in!", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
            }
          } else {
            items(post.replies, key = { it.id }) { reply ->
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = CyberSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = reply.authorName,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = TextPrimaryDark
                    )
                    Text(
                      text = reply.timestamp,
                      style = MaterialTheme.typography.labelSmall,
                      color = TextSecondaryDark
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = reply.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryDark
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = replyInput,
          onValueChange = { replyInput = it },
          placeholder = { Text("Write a reply...") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          trailingIcon = {
            IconButton(
              onClick = {
                if (replyInput.isNotBlank()) {
                  onAddReply(replyInput)
                  replyInput = ""
                }
              },
              enabled = replyInput.isNotBlank()
            ) {
              Icon(Icons.Default.Send, contentDescription = "Send", tint = VibrantPurpleOnContainer)
            }
          }
        )
      }
    },
    confirmButton = {
      Button(onClick = onDismiss) {
        Text("Close")
      }
    }
  )
}
