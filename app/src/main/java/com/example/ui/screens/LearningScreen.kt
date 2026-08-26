package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.LessonContent
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

@Composable
fun LearningScreen(
  onSelectLesson: (String) -> Unit,
  onNavigateToQuiz: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val lessons = AegoraRepository.sampleLessons

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(CyberBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      CyberCard(
        borderColor = CyberCyan.copy(alpha = 0.4f),
        backgroundColor = CyberSurface
      ) {
        Text(
          text = "LEARNING ENGINE",
          style = MaterialTheme.typography.labelSmall,
          color = CyberCyan
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Learn → Understand → Practice → Prove",
          style = MaterialTheme.typography.headlineLarge,
          color = TextPrimaryDark
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Interactive lessons with deep-dive technical architecture, simplified mental models, and real-world Wireshark/Sysmon terminal snippets.",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondaryDark
        )
      }
    }

    item {
      CyberSectionHeader(
        title = "Core Cybersecurity Lessons",
        subtitle = "${lessons.size} interactive telemetry modules"
      )
    }

    items(lessons) { lesson ->
      LessonCard(
        lesson = lesson,
        onClick = { onSelectLesson(lesson.id) }
      )
    }

    // Knowledge Assessment Drill Shortcut
    item {
      CyberCard(
        borderColor = CyberIndigo.copy(alpha = 0.6f),
        backgroundColor = CyberSurfaceElevated,
        onClick = { onNavigateToQuiz("qz_01") }
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CyberIndigo.copy(alpha = 0.3f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Quiz, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text("Interactive Assessment Drill", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
              Text("Test your MITRE ATT&CK & Sysmon analysis", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
            }
          }
          Icon(Icons.Default.ArrowForward, contentDescription = null, tint = CyberCyan)
        }
      }
    }
  }
}

@Composable
fun LessonCard(
  lesson: LessonContent,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  CyberCard(
    modifier = modifier,
    borderColor = CyberBorder,
    onClick = onClick
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = lesson.moduleTitle.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = CyberCyan
      )
      Text(
        text = "${lesson.estimatedReadMinutes} min read",
        style = MaterialTheme.typography.labelSmall,
        color = TextSecondaryDark
      )
    }

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = lesson.title,
      style = MaterialTheme.typography.titleLarge,
      color = TextPrimaryDark
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = lesson.coreExplanation,
      style = MaterialTheme.typography.bodyMedium,
      color = TextSecondaryDark,
      maxLines = 2
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (lesson.spacedRepetitionDue) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = CyberAmber.copy(alpha = 0.2f),
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberAmber)
        ) {
          Text(
            text = "Due for Spaced Review",
            style = MaterialTheme.typography.labelSmall,
            color = CyberAmber,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      } else {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = CyberSurfaceElevated
        ) {
          Text(
            text = "Practical Lab Included",
            style = MaterialTheme.typography.labelSmall,
            color = CyberCyan,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Start Lesson", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
      }
    }
  }
}
