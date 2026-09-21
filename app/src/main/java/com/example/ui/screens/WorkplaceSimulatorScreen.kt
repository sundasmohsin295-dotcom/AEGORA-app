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
import com.example.data.WorkplaceExperienceRepository
import com.example.model.*
import com.example.ui.components.ChamferedCutCornerShape
import com.example.ui.components.HexagonShape
import com.example.ui.theme.*

/**
 * AEGORA Professional Cybersecurity Workplace & Incident Experience Engine.
 * Simulates real-world workplace workflows: First Day on the Job, Live SOC Shifts,
 * Evidence-First Investigation Workbench, Reasoning Graph, Manager/Executive Comm Slack,
 * False Positive Triage, and the Incident Consequence Trade-off Engine.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkplaceSimulatorScreen(
  onNavigateBack: () -> Unit,
  onNavigateToLab: (String) -> Unit,
  onAskAi: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val currentShift by WorkplaceExperienceRepository.currentShift.collectAsState()
  var selectedTab by remember { mutableStateOf(0) } // 0: Live Shift & Tickets, 1: Investigation Workbench, 2: Reasoning Graph, 3: Manager Comm & Consequence, 4: Career & Organization
  var selectedEvidenceForDetail by remember { mutableStateOf<WorkbenchEvidence?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "WORKPLACE EXPERIENCE ENGINE",
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
                  text = "SIMULATION EXPERIENCE",
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
              text = "${currentShift.organization.name} • ${currentShift.careerRole.roleTitle}",
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
      contentPadding = PaddingValues(bottom = 36.dp)
    ) {
      // 1. Workplace Header: Shift Progress, Enterprise Risk & Scores
      item {
        WorkplaceShiftHeader(shift = currentShift)
      }

      // 2. Navigation Tabs
      item {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = CyberSurfaceVariant,
          contentColor = NeonCyan,
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
            text = { Text("Shift & Tickets", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("Investigation Workbench", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("Reasoning Graph", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 3,
            onClick = { selectedTab = 3 },
            text = { Text("Manager Comm & Tradeoffs", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
          Tab(
            selected = selectedTab == 4,
            onClick = { selectedTab = 4 },
            text = { Text("Company & Careers", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)) }
          )
        }
      }

      // 3. Tab Contents
      when (selectedTab) {
        0 -> {
          // Tab 0: Shift Timeline & Assigned Ticket Queue
          item {
            FirstDayWelcomeCard(role = currentShift.careerRole, org = currentShift.organization)
          }

          item {
            Text(
              text = "ACTIVE INCIDENT & TICKET QUEUE",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              ),
              color = NeonCyan
            )
          }

          items(currentShift.tickets) { ticket ->
            WorkplaceTicketCard(ticket = ticket, onInvestigate = { selectedTab = 1 })
          }
        }

        1 -> {
          // Tab 1: Evidence-First Investigation Workbench
          item {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = CyberSurface,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "EVIDENCE-FIRST INVESTIGATION WORKBENCH",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NeonCyan)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Real cybersecurity work requires gathering multi-source telemetry (SIEM, EDR, DNS, Firewalls) before drawing conclusions. Pin verified artifacts to your hypothesis branch.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
              }
            }
          }

          items(currentShift.activeAlerts) { evidence ->
            WorkbenchEvidenceCard(
              evidence = evidence,
              onExamine = {
                WorkplaceExperienceRepository.examineEvidence(evidence.id)
                selectedEvidenceForDetail = evidence
              },
              onPin = { WorkplaceExperienceRepository.pinEvidenceToHypothesis(evidence.id) }
            )
          }

          // Incident Containment Action Bar
          item {
            Surface(
              shape = ChamferedCutCornerShape,
              color = CyberSurfaceElevated,
              border = BorderStroke(1.2.dp, TerminalAmber),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "INCIDENT RESPONSE ACTIONS (TRADE-OFF DECISION ENGINE)",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = TerminalAmber
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Every defensive action impacts business continuity or forensic evidence integrity. Choose wisely:",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextPrimaryDark
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Button(
                    onClick = {
                      WorkplaceExperienceRepository.executeIncidentAction("ISOLATE_EDR")
                      selectedTab = 3
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("Isolate via EDR", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                  OutlinedButton(
                    onClick = {
                      WorkplaceExperienceRepository.executeIncidentAction("KILL_PHYSICAL_POWER")
                      selectedTab = 3
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink),
                    border = BorderStroke(1.dp, NeonPink),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text("Hard Power Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        2 -> {
          // Tab 2: Hypothesis-Driven Reasoning Graph
          item {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = CyberSurface,
              border = BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "HYPOTHESIS-DRIVEN REASONING GRAPH",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CyberViolet)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Visualizes: Observation → Competing Hypotheses → Evidence Tests → Conclusions → Defensive Actions. Prevents premature closure and confirmation bias.",
                  style = MaterialTheme.typography.bodySmall,
                  color = TextSecondaryDark
                )
              }
            }
          }

          items(currentShift.reasoningNodes) { node ->
            ReasoningNodeCard(node = node)
          }
        }

        3 -> {
          // Tab 3: Manager / Executive Slack Channel & Consequence Results
          currentShift.latestConsequence?.let { consequence ->
            item {
              IncidentConsequenceCard(consequence = consequence)
            }
          }

          item {
            Text(
              text = "WORKPLACE COMMUNICATION CHANNELS (#WAR-ROOM)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
              ),
              color = NeonCyan
            )
          }

          items(currentShift.messages) { msg ->
            WorkplaceMessageCard(
              message = msg,
              onSelectResponse = { choice ->
                WorkplaceExperienceRepository.respondToManagerMessage(msg.id, choice)
              }
            )
          }
        }

        4 -> {
          // Tab 4: 11 Career Roles & Living Fictional Organizations
          item {
            Text(
              text = "SELECT CAREER SIMULATION ROLE",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
              color = NeonCyan
            )
          }

          item {
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              contentPadding = PaddingValues(vertical = 4.dp)
            ) {
              items(CareerRoleType.entries.toTypedArray()) { role ->
                val isSelected = currentShift.careerRole == role
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant,
                  border = BorderStroke(1.dp, if (isSelected) NeonCyan else CyberBorderSubtle),
                  modifier = Modifier.clickable { WorkplaceExperienceRepository.selectCareerRole(role) }
                ) {
                  Text(
                    text = role.roleTitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                    color = if (isSelected) NeonCyan else TextSecondaryDark,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                  )
                }
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "LIVING VIRTUAL ORGANIZATIONS (PERSISTENT ENVIRONMENTS)",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
              color = CyberEmerald
            )
          }

          items(WorkplaceExperienceRepository.virtualEnterprises) { org ->
            VirtualEnterpriseCard(
              org = org,
              isSelected = currentShift.organization.id == org.id,
              onSelect = { WorkplaceExperienceRepository.switchEnterprise(org) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun WorkplaceShiftHeader(shift: WorkplaceShiftSession) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, NeonCyan),
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
            text = "ACTIVE SIMULATION SHIFT",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp
            ),
            color = NeonCyan
          )
          Text(
            text = shift.shiftTime,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberEmerald.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
          ) {
            Text(
              text = "JUDGMENT: ${shift.professionalJudgmentScore}%",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
              color = CyberEmerald,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = NeonCyan.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f))
          ) {
            Text(
              text = "INDEPENDENCE: ${shift.independenceScore}%",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
              color = NeonCyan,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      LinearProgressIndicator(
        progress = { shift.shiftProgressPercent / 100f },
        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
        color = NeonCyan,
        trackColor = CyberSurfaceElevated
      )
    }
  }
}

@Composable
fun FirstDayWelcomeCard(role: CareerRoleType, org: VirtualEnterprise) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurfaceElevated,
    border = BorderStroke(1.dp, CyberBorderSubtle),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Badge, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "DAY 1 ONBOARDING BRIEF",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = NeonCyan
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Welcome to ${org.name}! You are stationed in the ${role.department} as a ${role.roleTitle}.",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = TextPrimaryDark
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "First Mission: ${role.firstDayMission}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Authorized Tools: ${role.primaryTools.joinToString(" • ")}",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
        color = CyberEmerald
      )
    }
  }
}

@Composable
fun WorkplaceTicketCard(ticket: WorkplaceTicket, onInvestigate: () -> Unit) {
  val isCritical = ticket.severity.contains("CRITICAL")
  val badgeColor = if (isCritical) NeonPink else TerminalAmber

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.6f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${ticket.ticketId} • ${ticket.status}",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
          color = badgeColor
        )
        Text(
          text = ticket.severity,
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = badgeColor
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = ticket.title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = ticket.summary,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Target Host: ${ticket.affectedHost} • Evidence Artifacts: ${ticket.evidenceIds.size}",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(8.dp))
      Button(
        onClick = onInvestigate,
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Open Investigation Workbench", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }
  }
}

@Composable
fun WorkbenchEvidenceCard(
  evidence: WorkbenchEvidence,
  onExamine: () -> Unit,
  onPin: () -> Unit
) {
  val border = if (evidence.isPinnedToHypothesis) CyberEmerald else if (evidence.isExamined) NeonCyan else CyberBorderSubtle

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, border),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Terminal, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = evidence.toolSource.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
            color = NeonCyan
          )
        }

        if (evidence.isPinnedToHypothesis) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = CyberEmerald.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f))
          ) {
            Text(
              text = "PINNED ARTIFACT",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.Bold),
              color = CyberEmerald,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = evidence.title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(6.dp))
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF0D1117),
        border = BorderStroke(1.dp, CyberBorderSubtle),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = evidence.rawData,
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
          color = TextPrimaryDark,
          modifier = Modifier.padding(8.dp)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "MITRE: ${evidence.mitreTactic}",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedButton(
          onClick = onExamine,
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("Inspect Artifact", fontSize = 11.sp)
        }
        Button(
          onClick = onPin,
          colors = ButtonDefaults.buttonColors(containerColor = if (evidence.isPinnedToHypothesis) CyberEmerald else CyberSurfaceElevated),
          shape = RoundedCornerShape(6.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            if (evidence.isPinnedToHypothesis) "Pinned to Graph" else "Pin to Hypothesis",
            color = if (evidence.isPinnedToHypothesis) Color.Black else TextPrimaryDark,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
fun ReasoningNodeCard(node: ReasoningNode) {
  val stageColor = when (node.stage) {
    "OBSERVATION" -> NeonCyan
    "HYPOTHESIS" -> CyberViolet
    "EVIDENCE_TEST" -> TerminalAmber
    "CONCLUSION" -> CyberEmerald
    else -> Color(0xFF00E5FF)
  }

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.dp, stageColor.copy(alpha = 0.7f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = stageColor.copy(alpha = 0.15f),
          border = BorderStroke(1.dp, stageColor.copy(alpha = 0.5f))
        ) {
          Text(
            text = node.stage,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
            color = stageColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Text(
          text = if (node.isValidBranch) "VALID BRANCH" else "INVALID LEAD",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
          color = if (node.isValidBranch) CyberEmerald else TerminalAmber
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = node.description,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Trade-off / Risk: ${node.tradeOffOrRisk}",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
        color = TextSecondaryDark
      )
    }
  }
}

@Composable
fun WorkplaceMessageCard(message: WorkplaceMessage, onSelectResponse: (String) -> Unit) {
  val isCritical = message.priority == "CRITICAL"

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, if (isCritical) NeonCyan else CyberBorderSubtle),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Chat, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${message.senderName} (${message.senderRole})",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
        }

        Text(
          text = message.timestamp,
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
          color = TextSecondaryDark
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = message.content,
        style = MaterialTheme.typography.bodySmall,
        color = TextPrimaryDark
      )

      if (message.requiresResponse && message.resolvedResponse == null) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "CHOOSE YOUR PROFESSIONAL BRIEFING RESPONSE:",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
          color = NeonCyan
        )
        Spacer(modifier = Modifier.height(6.dp))
        message.responseOptions.forEach { option ->
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceVariant,
            border = BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
              .clickable { onSelectResponse(option) }
          ) {
            Text(
              text = option,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      } else if (message.resolvedResponse != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = CyberEmerald.copy(alpha = 0.12f),
          border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(
              text = "YOUR COMMITTED BRIEFING:",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.sp),
              color = CyberEmerald
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = message.resolvedResponse,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )
          }
        }
      }
    }
  }
}

@Composable
fun IncidentConsequenceCard(consequence: IncidentConsequence) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurfaceElevated,
    border = BorderStroke(1.2.dp, CyberEmerald),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "INCIDENT CONSEQUENCE EVALUATION",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = CyberEmerald
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "Action: ${consequence.actionTaken}",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Business Impact: ${consequence.businessImpact}",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Attacker Progression: ${consequence.attackerProgression}",
        style = MaterialTheme.typography.bodySmall,
        color = CyberEmerald
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Executive Feedback: ${consequence.executiveSatisfaction}",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
        color = NeonCyan
      )
    }
  }
}

@Composable
fun VirtualEnterpriseCard(org: VirtualEnterprise, isSelected: Boolean, onSelect: () -> Unit) {
  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurface,
    border = BorderStroke(1.2.dp, if (isSelected) NeonCyan else CyberBorderSubtle),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onSelect() }
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = org.name,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = TextPrimaryDark
        )
        Text(
          text = "${org.activeIncidentsCount} Incidents Active",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = if (org.activeIncidentsCount > 1) NeonPink else CyberEmerald
        )
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "${org.industry} • ${org.employeeCount} Employees",
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Risk: ${org.riskProfile}",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
        color = TerminalAmber
      )

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Critical Assets: ${org.criticalAssets.joinToString(", ")}",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
        color = TextSecondaryDark
      )
    }
  }
}
