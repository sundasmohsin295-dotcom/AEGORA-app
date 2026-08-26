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
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@Composable
fun SwarmArenaScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val combatState by AegoraRepository.swarmBattleState.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(PureBlack)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // 1. Header
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

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Shield, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "RED-VS-BLUE SWARM ARENA",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = CyberViolet
          )
        }
      }
    }

    // 2. Swarm Dominance Tug-of-War Gauge
    item {
      CyberCard(
        borderColor = if (combatState.isVictoryAchieved) CyberEmerald else CyberViolet,
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "SWARM DOMINANCE GAUGE (ROUND ${combatState.activeSwarmRound})",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberViolet
            )
            Text(
              text = "${combatState.blueSwarmName} vs ${combatState.redSwarmName}",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (combatState.isVictoryAchieved) CyberEmerald.copy(alpha = 0.2f) else NeonPink.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (combatState.isVictoryAchieved) CyberEmerald else NeonPink)
          ) {
            Text(
              text = if (combatState.isVictoryAchieved) "BLUE VICTORY" else "${combatState.enterpriseCompromisePercent}% INTRUSION",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = if (combatState.isVictoryAchieved) CyberEmerald else NeonPink,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress bar for tug of war
        LinearProgressIndicator(
          progress = { (100 - combatState.enterpriseCompromisePercent) / 100f },
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp)),
          color = CyberEmerald,
          trackColor = NeonPink
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("🛡️ Blue Sentinel: ${100 - combatState.enterpriseCompromisePercent}% Contained", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp), color = CyberEmerald)
          Text("⚡ Red Swarm: ${combatState.enterpriseCompromisePercent}% Foothold", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp), color = NeonPink)
        }
      }
    }

    // 3. Human Commander Injection Console
    item {
      CyberCard(
        borderColor = CyberCyan,
        backgroundColor = CyberSurface
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "HUMAN COMMANDER INJECTION CONSOLE",
              style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
              color = CyberCyan
            )
            Text(
              text = "Deploy real-time defensive heuristics to empower Blue Sentinel",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondaryDark
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          combatState.commanderActionsAvailable.forEach { action ->
            Button(
              onClick = { AegoraRepository.injectSwarmDirective(action) },
              colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceElevated),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth().testTag("swarm_action_${action.take(6)}")
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(action, color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }

    // 4. Live Autonomous Battle Feed
    item {
      CyberCard(
        borderColor = CyberBorderSubtle,
        backgroundColor = CyberSurface
      ) {
        Text(
          text = "AUTONOMOUS COMBAT TELEMETRY LOG",
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
          color = CyberCyan
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CodeBackground)
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          combatState.recentCombatLogs.forEach { log ->
            Text(
              text = log,
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
              color = when {
                log.contains("RED") -> NeonPink
                log.contains("BLUE") -> CyberEmerald
                log.contains("COMMANDER") -> CyberCyan
                else -> TextSecondaryDark
              }
            )
          }
        }
      }
    }
  }
}
