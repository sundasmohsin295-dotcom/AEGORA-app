package com.example.ui.screens

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.ClassroomRoster
import com.example.model.UserRole
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversityAndAdminScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val userProfile by AegoraRepository.userProfile.collectAsState()
  val classrooms by AegoraRepository.classrooms.collectAsState()
  val adminStats = AegoraRepository.adminStats

  var showCreateClassDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = when (userProfile.role) {
                UserRole.INSTRUCTOR -> "Instructor Command Portal"
                UserRole.ADMIN -> "Platform Administration & Telemetry"
                UserRole.STUDENT -> "Academic & University Mode"
              },
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
            Text(
              text = "Role: ${userProfile.role.label} • Multi-tenant Scaffolding",
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
          if (userProfile.role == UserRole.INSTRUCTOR) {
            FilledTonalButton(
              onClick = { showCreateClassDialog = true },
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = VibrantPurpleContainer,
                contentColor = VibrantPurpleOnContainer
              ),
              modifier = Modifier.padding(end = 8.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("New Cohort", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
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
      contentPadding = PaddingValues(top = 4.dp, bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Role Switcher Preview Bar
      item {
        CyberCard(
          borderColor = CyberBorderSubtle,
          backgroundColor = CyberSurfaceVariant,
          shapeRadius = 18.dp
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Text(
              text = "ROLE SWITCHER (PREVIEW PROTOTYPE)",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
              color = CyberCyan
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Switch roles instantly to inspect Instructor, Student, or Admin views:",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              UserRole.entries.forEach { role ->
                val isSelected = userProfile.role == role
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) VibrantPurpleContainer else CyberSurfaceElevated)
                    .border(1.dp, if (isSelected) VibrantPurpleContainer else CyberBorderSubtle, RoundedCornerShape(12.dp))
                    .clickable { AegoraRepository.setUserRole(role) }
                    .padding(vertical = 10.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = role.label,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) VibrantPurpleOnContainer else TextSecondaryDark
                  )
                }
              }
            }
          }
        }
      }

      // Architecture Notice
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = VibrantBlueContainer.copy(alpha = 0.5f),
          border = BorderStroke(1.dp, VibrantBlueContainer),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = VibrantBlueOnContainer, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "University Mode Architecture: Scaffolding data models ready for institution sync, SSO SAML, and LTI 1.3 LMS integration.",
              style = MaterialTheme.typography.bodySmall,
              color = VibrantBlueOnContainer
            )
          }
        }
      }

      // 2. Instructor View
      if (userProfile.role == UserRole.INSTRUCTOR || userProfile.role == UserRole.STUDENT) {
        item {
          Text(
            text = "ASSIGNED ACADEMIC COHORTS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = TextSecondaryDark
          )
        }

        items(classrooms, key = { it.id }) { classroom ->
          ClassroomCohortCard(classroom = classroom)
        }
      }

      // 3. Admin View
      if (userProfile.role == UserRole.ADMIN) {
        item {
          Text(
            text = "GLOBAL PLATFORM TELEMETRY",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = CyberCyan
          )
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            AdminStatCard(
              title = "Core Lessons",
              value = "${adminStats.totalLessonsCount}",
              icon = Icons.Default.School,
              containerColor = VibrantPurpleContainer,
              contentColor = VibrantPurpleOnContainer,
              modifier = Modifier.weight(1f)
            )
            AdminStatCard(
              title = "Investigation Labs",
              value = "${adminStats.totalLabsCount}",
              icon = Icons.Default.Terminal,
              containerColor = VibrantBlueContainer,
              contentColor = VibrantBlueOnContainer,
              modifier = Modifier.weight(1f)
            )
          }
        }

        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            AdminStatCard(
              title = "CTF Flags",
              value = "${adminStats.totalChallengesCount}",
              icon = Icons.Default.Flag,
              containerColor = VibrantPinkContainer,
              contentColor = VibrantPinkOnContainer,
              modifier = Modifier.weight(1f)
            )
            AdminStatCard(
              title = "Registered Learners",
              value = "${adminStats.totalRegisteredLearners}",
              icon = Icons.Default.People,
              containerColor = VibrantEmeraldContainer,
              contentColor = VibrantEmeraldOnContainer,
              modifier = Modifier.weight(1f)
            )
          }
        }

        item {
          CyberCard(
            borderColor = CyberBorderSubtle,
            backgroundColor = CyberSurfaceVariant,
            shapeRadius = 18.dp
          ) {
            Column(modifier = Modifier.fillMaxWidth()) {
              Text(
                text = "SYSTEM HEALTH & ENGINE STATUS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = TextSecondaryDark
              )
              Spacer(modifier = Modifier.height(10.dp))
              SystemHealthRow(name = "Telemetry Ingestion Pipeline", status = "ONLINE (99.98% uptime)")
              SystemHealthRow(name = "Investigation Simulation Engine", status = "ONLINE (42 active)")
              SystemHealthRow(name = "Gemini Mentor AI Gateway", status = "ACTIVE")
              SystemHealthRow(name = "Skill Passport Cryptographic Ledger", status = "SYNCED")
            }
          }
        }
      }
    }
  }

  if (showCreateClassDialog) {
    CreateClassroomDialog(
      onDismiss = { showCreateClassDialog = false },
      onCreate = { name, lessons ->
        AegoraRepository.createClassroom(name, lessons)
        showCreateClassDialog = false
      }
    )
  }
}

@Composable
fun ClassroomCohortCard(classroom: ClassroomRoster) {
  CyberCard(
    borderColor = CyberBorderSubtle,
    backgroundColor = CyberSurfaceVariant,
    shapeRadius = 20.dp
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = classroom.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
          Text(
            text = "Instructor: ${classroom.instructorName} • ${classroom.assignedLessonIds.size} Modules Assigned",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(VibrantPurpleContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${classroom.students.size} Enrolled",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = VibrantPurpleOnContainer
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "STUDENT PROGRESS ROSTER",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = CyberCyan
      )

      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        classroom.students.forEach { student ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurfaceElevated,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = student.studentName,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = TextPrimaryDark
                )
                Text(
                  text = "Callsign: ${student.callsign} • Labs: ${student.completedLabsCount} | Lessons: ${student.completedLessonsCount}",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark
                )
              }
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (student.completionRatePercent >= 80) VibrantEmeraldContainer else VibrantAmberContainer)
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "${student.completionRatePercent}% Done",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = if (student.completionRatePercent >= 80) VibrantEmeraldOnContainer else VibrantAmberOnContainer
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun AdminStatCard(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  containerColor: Color,
  contentColor: Color,
  modifier: Modifier = Modifier
) {
  CyberCard(
    borderColor = CyberBorderSubtle,
    backgroundColor = containerColor,
    shapeRadius = 18.dp,
    modifier = modifier
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = contentColor.copy(alpha = 0.8f)
        )
        Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = contentColor
      )
    }
  }
}

@Composable
fun SystemHealthRow(name: String, status: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(name, style = MaterialTheme.typography.bodySmall, color = TextPrimaryDark)
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(VibrantEmeraldOnContainer)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(status, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = VibrantEmeraldOnContainer)
    }
  }
}

@Composable
fun CreateClassroomDialog(
  onDismiss: () -> Unit,
  onCreate: (String, List<String>) -> Unit
) {
  var name by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Create Academic Cohort", fontWeight = FontWeight.Bold, color = TextPrimaryDark) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Cohort Name (e.g. CYBER-201 Fall)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Text(
          "Pre-assigned curriculum includes: Sysmon Event ID 1, DNS Telemetry, and Cloud IAM Security.",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondaryDark
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onCreate(name, listOf("les_101", "les_102", "les_201"))
          }
        },
        enabled = name.isNotBlank()
      ) {
        Text("Create Cohort")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
