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
fun GlobalRadarScreen(
  onNavigateBack: () -> Unit,
  onNavigateToSwarmArena: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val radarItems = AegoraRepository.globalRadarItems
  val teams = AegoraRepository.cyberLeagueTeams
  var activeCategoryFilter by remember { mutableStateOf("ALL") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(PureBlack)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
  ) {
    // 1. Top Header Banner
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
          Icon(Icons.Default.Public, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "GLOBAL RADAR & TOURNAMENT LEAGUE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = CyberCyan
          )
        }
      }
    }

    // 2. Swarm Arena Quick Banner
    item {
      CyberCard(
        borderColor = CyberViolet,
        backgroundColor = VibrantPurpleContainer.copy(alpha = 0.4f),
        onClick = onNavigateToSwarmArena
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(CyberViolet.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.SmartToy, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(24.dp))
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("Autonomous Red-vs-Blue Swarm Arena", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimaryDark)
            Text("Human-in-the-Loop Strike Commander Console", style = MaterialTheme.typography.bodySmall, color = TextSecondaryDark)
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CyberViolet)
        }
      }
    }

    // 3. Global Threat & CTF Ticker
    item {
      Text(
        text = "GLOBAL EVENT & HACKATHON TICKER",
        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
        color = CyberCyan
      )
      Spacer(modifier = Modifier.height(8.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        radarItems.forEach { item ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = CyberSurface,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (item.liveStatus == "LIVE NOW") NeonPink.copy(alpha = 0.6f) else CyberBorderSubtle
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (item.liveStatus == "LIVE NOW") NeonPink.copy(alpha = 0.15f) else CyberSurfaceElevated),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (item.liveStatus == "LIVE NOW") Icons.Default.LiveTv else Icons.Default.Event,
                  contentDescription = null,
                  tint = if (item.liveStatus == "LIVE NOW") NeonPink else CyberCyan,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimaryDark
                  )
                }
                Text(
                  text = "${item.organizer} • ${item.dateOrTimeLeft}",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                  color = TextSecondaryDark
                )
                Text(
                  text = "Prize / Points: ${item.prizeOrPoints}",
                  style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                  color = CyberEmerald
                )
              }

              Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (item.liveStatus == "LIVE NOW") NeonPink.copy(alpha = 0.2f) else CyberSurfaceElevated
              ) {
                Text(
                  text = item.liveStatus,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (item.liveStatus == "LIVE NOW") NeonPink else TextSecondaryDark
                  ),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }
    }

    // 4. University & Enterprise Cyber League Leaderboard
    item {
      CyberCard(
        borderColor = CyberEmerald.copy(alpha = 0.4f),
        backgroundColor = CyberSurface
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "AEGORA CYBER LEAGUE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = CyberEmerald
          )
          Text(
            text = "VERIFIED MATCHMAKING",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
            color = TextSecondaryDark
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          teams.forEach { team ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = CyberSurfaceElevated,
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "#${team.rank}",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = when (team.rank) {
                      1 -> CyberAmber
                      2 -> CyberCyan
                      else -> TextSecondaryDark
                    }
                  ),
                  modifier = Modifier.width(32.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = team.teamName,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                      color = TextPrimaryDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = CyberCyan.copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = team.verificationBadge,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                        color = CyberCyan,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                  Text(
                    text = "${team.tier} • ${team.organization}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextSecondaryDark
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = "${team.totalScore} pts",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                    color = CyberEmerald
                  )
                  Text(
                    text = "ATK ${team.attackPoints} / DEF ${team.defensePoints}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontFamily = FontFamily.Monospace),
                    color = TextSecondaryDark
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
