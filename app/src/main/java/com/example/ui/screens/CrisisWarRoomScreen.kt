package com.example.ui.screens

import androidx.compose.animation.*
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
import com.example.model.WarRoomPersona
import com.example.model.WarRoomPersonaRole
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@Composable
fun CrisisWarRoomScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val personas by AegoraRepository.warRoomPersonas.collectAsState()
  val dials by AegoraRepository.warRoomDials.collectAsState()
  var activePersona by remember { mutableStateOf(personas.first()) }
  var crisisDecisionMade by remember { mutableStateOf<String?>(null) }
  var countdownSeconds by remember { mutableIntStateOf(180) }

  LaunchedEffect(Unit) {
    while (countdownSeconds > 0) {
      kotlinx.coroutines.delay(1000)
      countdownSeconds--
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(PureBlack)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // 1. Header & Live Countdown
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(CyberSurfaceElevated)
            .border(1.dp, CyberBorderSubtle, CircleShape)
        ) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan, modifier = Modifier.size(20.dp))
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = NeonPink.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "CRISIS TIMER: ${countdownSeconds / 60}m ${countdownSeconds % 60}s",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = NeonPink
            )
          }
        }
      }
    }

    // 2. War-Room Triage Dials
    item {
      CyberCard(
        borderColor = NeonPink.copy(alpha = 0.6f),
        backgroundColor = CyberSurface
      ) {
        Text(
          text = "BOARDROOM & REGULATORY PRESSURE DIALS",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = NeonPink
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Downtime Dial
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${dials.operationalDowntimePercent}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = CyberAmber)
              Text("Downtime", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }

          // Reputational Risk
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${dials.reputationalRiskPercent}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = NeonPink)
              Text("Reputation", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }

          // Exfiltration
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${dials.exfiltrationBlastRadiusPercent}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = CyberViolet)
              Text("Data Blast", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }

          // GDPR/SEC Fine Exposure
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.weight(1f)
          ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
              Text("${dials.regulatoryFineExposurePercent}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace), color = CyberEmerald)
              Text("SEC Penalty", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondaryDark)
            }
          }
        }
      }
    }

    // 3. Multi-Agent Persona Caller Roster
    item {
      Text(
        text = "ACTIVE INCOMING STAKEHOLDER FEEDS",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
        color = CyberCyan
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        personas.forEach { persona ->
          val isSel = persona.id == activePersona.id
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSel) CyberSurfaceElevated else CyberSurface,
            border = androidx.compose.foundation.BorderStroke(
              1.5.dp,
              if (isSel) (if (persona.role == WarRoomPersonaRole.EXTORTIONIST) NeonPink else CyberCyan) else CyberBorderSubtle
            ),
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable { activePersona = persona }
          ) {
            Column(
              modifier = Modifier.padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = when (persona.role) {
                  WarRoomPersonaRole.CISO -> Icons.Default.Security
                  WarRoomPersonaRole.LEGAL_COUNSEL -> Icons.Default.Gavel
                  WarRoomPersonaRole.PR_COMMUNICATIONS -> Icons.Default.Campaign
                  WarRoomPersonaRole.CEO -> Icons.Default.CorporateFare
                  WarRoomPersonaRole.EXTORTIONIST -> Icons.Default.Warning
                },
                contentDescription = null,
                tint = if (persona.role == WarRoomPersonaRole.EXTORTIONIST) NeonPink else CyberCyan,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = persona.name.split(" ").first(),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 10.sp,
                  fontFamily = FontFamily.Monospace
                ),
                color = if (isSel) TextPrimaryDark else TextSecondaryDark,
                maxLines = 1
              )
            }
          }
        }
      }
    }

    // 4. Active Persona Audio Dispatch & Demands
    item {
      CyberCard(
        borderColor = if (activePersona.role == WarRoomPersonaRole.EXTORTIONIST) NeonPink else CyberCyan,
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(
                (if (activePersona.role == WarRoomPersonaRole.EXTORTIONIST) NeonPink else CyberCyan).copy(alpha = 0.15f)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.RecordVoiceOver,
              contentDescription = null,
              tint = if (activePersona.role == WarRoomPersonaRole.EXTORTIONIST) NeonPink else CyberCyan,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "${activePersona.name} (${activePersona.title})",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = TextPrimaryDark
            )
            Text(
              text = "Stress Index: ${activePersona.stressLevel}% | Tone: ${activePersona.role.defaultVoiceTone}",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
              color = TextSecondaryDark
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Persona Voice Quote
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = CodeBackground,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = activePersona.activeQuote,
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
            color = if (activePersona.role == WarRoomPersonaRole.EXTORTIONIST) NeonPink else CyberCyan,
            modifier = Modifier.padding(12.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // High-Stakes Triage Actions
        Text(
          text = "HIGH-STAKES TRIAGE DECISION:",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = CyberAmber
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = {
              crisisDecisionMade = "Option A: Immediate Hard-Isolation & SEC Form 8-K Regulatory Filing Dispatched"
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Authorize Full Subnet Isolation & Issue 8-K", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }

          Button(
            onClick = {
              crisisDecisionMade = "Option B: Deploy Decoy Canaries, Mirror Traffic, Delay Disclosure for 2 Hours"
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberViolet),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Deploy Decoy Honey-Tokens & Trace C2", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }

        if (crisisDecisionMade != null) {
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberEmerald.copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "✓ EXECUTED: $crisisDecisionMade",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberEmerald,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      }
    }
  }
}
