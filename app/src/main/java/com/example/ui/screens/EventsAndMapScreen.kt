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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.model.CyberEvent
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

@Composable
fun EventsAndMapScreen(
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val events = AegoraRepository.upcomingEvents

  Scaffold(
    topBar = {
      Surface(
        color = CyberBackground,
        modifier = Modifier.fillMaxWidth().statusBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryDark)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Cyber World Map & Events",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
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
      contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // World Map Visual Representation
      item {
        CyberCard(borderColor = CyberCyan.copy(alpha = 0.5f)) {
          Text(
            text = "GLOBAL CYBER DEFENSE NETWORK",
            style = MaterialTheme.typography.labelSmall,
            color = CyberCyan
          )
          Spacer(modifier = Modifier.height(6.dp))

          // Simulated High-tech Map grid
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = CyberSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
          ) {
            Box(
              modifier = Modifier.fillMaxSize(),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Public, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("142 Nodes Active • 18 Live CTFs Worldwide", style = MaterialTheme.typography.titleMedium, color = TextPrimaryDark)
                Text("Las Vegas • Singapore • London • Tokyo • Global Online", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
              }
            }
          }
        }
      }

      item {
        CyberSectionHeader(
          title = "Conferences & CTF Tournaments",
          subtitle = "Join global cybersecurity competitions & summits"
        )
      }

      items(events) { event ->
        CyberCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = CyberIndigo.copy(alpha = 0.2f),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberIndigo)
            ) {
              Text(
                text = event.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = CyberCyan,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = event.date,
              style = MaterialTheme.typography.labelSmall,
              color = CyberGold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = event.title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimaryDark
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Location: ${event.location}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            OutlinedButton(
              onClick = { },
              colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
              border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("Register / Details")
            }
          }
        }
      }
    }
  }
}
