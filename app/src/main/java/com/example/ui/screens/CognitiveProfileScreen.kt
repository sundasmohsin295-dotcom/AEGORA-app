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
import com.example.model.MistakeDnaRecord
import com.example.ui.components.ChamferedCutCornerShape
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.components.HexagonShape
import com.example.ui.components.cyberGridBackground
import com.example.ui.theme.*

@Composable
fun CognitiveProfileScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDrill: ((String) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val mistakeDnaList by AegoraRepository.mistakeDnaRecords.collectAsState()
  val activeReasoningGraph by AegoraRepository.activeReasoningGraph.collectAsState()
  var selectedFilter by remember { mutableStateOf("ALL") }
  var showRemediationDialog by remember { mutableStateOf<MistakeDnaRecord?>(null) }

  val filteredRecords = remember(mistakeDnaList, selectedFilter) {
    if (selectedFilter == "ALL") mistakeDnaList
    else mistakeDnaList.filter { it.category.contains(selectedFilter, ignoreCase = true) }
  }

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
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Cognitive Intelligence Profile",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            ),
            color = TextPrimaryDark
          )
        }
      }
    },
    containerColor = CyberBackground
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .background(CyberBackground)
        .cyberGridBackground()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Cognitive Fingerprint Hero
      item {
        CyberCard(
          borderColor = CyberViolet,
          backgroundColor = CyberSurface,
          shapeRadius = 24.dp
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(HexagonShape)
                  .background(CyberViolet.copy(alpha = 0.15f))
                  .border(1.2.dp, CyberViolet, HexagonShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = CyberViolet, modifier = Modifier.size(26.dp))
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "MISTAKE DNA & BIAS RADAR",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  ),
                  color = CyberViolet
                )
                Text(
                  text = "Analytical Fingerprint",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                  color = TextPrimaryDark
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CyberSurfaceElevated,
              border = BorderStroke(1.dp, CyberViolet.copy(alpha = 0.4f))
            ) {
              Text(
                text = "${mistakeDnaList.sumOf { it.occurrences }} Captured",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberViolet,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "AEGORA tracks your analytical decision habits across SOC investigations to isolate cognitive biases (Tunnel Vision, Premature Closure, Correlation Gaps).",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Cognitive Metrics Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            CognitiveStatPill(
              label = "Tunnel Vision Risk",
              value = "LOW",
              color = CyberEmerald,
              modifier = Modifier.weight(1f)
            )
            CognitiveStatPill(
              label = "Premature Closure",
              value = activeReasoningGraph.prematureClosureRisk,
              color = if (activeReasoningGraph.prematureClosureRisk == "LOW") CyberEmerald else CyberAmber,
              modifier = Modifier.weight(1f)
            )
            CognitiveStatPill(
              label = "Alert Fatigue",
              value = "MODERATE",
              color = CyberAmber,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      // 2. Filter Pills
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("ALL" to "All Patterns", "Cognitive" to "Cognitive Biases", "Analytical" to "Analytical Gaps", "Operational" to "Operational Noise").forEach { (key, label) ->
            val isSelected = selectedFilter == key
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) CyberCyan.copy(alpha = 0.15f) else CyberSurface,
              border = BorderStroke(1.dp, if (isSelected) CyberCyan else CyberBorderSubtle),
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { selectedFilter = key }
            ) {
              Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) CyberCyan else TextSecondaryDark,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      // 3. Mistake DNA List Cards
      item {
        CyberSectionHeader(
          title = "Diagnosed Bias Signatures",
          subtitle = "Actionable remediations and simulation drills"
        )
      }

      items(filteredRecords) { record ->
        CyberCard(
          borderColor = when (record.severity) {
            "Critical" -> CyberCrimson.copy(alpha = 0.6f)
            "Moderate" -> CyberAmber.copy(alpha = 0.6f)
            else -> CyberCyan.copy(alpha = 0.4f)
          },
          backgroundColor = CyberSurface,
          modifier = Modifier.clickable { showRemediationDialog = record }
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = when (record.severity) {
                    "Critical" -> CyberCrimson.copy(alpha = 0.15f)
                    "Moderate" -> CyberAmber.copy(alpha = 0.15f)
                    else -> CyberCyan.copy(alpha = 0.15f)
                  },
                  border = BorderStroke(
                    1.dp,
                    when (record.severity) {
                      "Critical" -> CyberCrimson
                      "Moderate" -> CyberAmber
                      else -> CyberCyan
                    }
                  )
                ) {
                  Text(
                    text = "${record.severity.uppercase()} • ${record.occurrences}x RECORDED",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontFamily = FontFamily.Monospace,
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    color = when (record.severity) {
                      "Critical" -> CyberCrimson
                      "Moderate" -> CyberAmber
                      else -> CyberCyan
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = record.category,
                  style = MaterialTheme.typography.labelSmall,
                  color = TextSecondaryDark
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = record.patternName,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
            }

            IconButton(onClick = { showRemediationDialog = record }) {
              Icon(Icons.Default.ChevronRight, contentDescription = "View Remediation", tint = CyberCyan)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = record.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = CyberSurfaceElevated,
            border = BorderStroke(1.dp, CyberBorderSubtle)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Build, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Target Fix: ${record.correctiveRemediation}",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                color = CyberCyan
              )
            }
          }
        }
      }
    }
  }

  if (showRemediationDialog != null) {
    val rec = showRemediationDialog!!
    AlertDialog(
      onDismissRequest = { showRemediationDialog = null },
      title = {
        Text(
          text = "Cognitive Remediation: ${rec.patternName}",
          color = TextPrimaryDark,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Diagnosed during: ${rec.diagnosedIncident}",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = CyberCyan
          )
          Text(
            text = rec.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = CyberSurfaceElevated,
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f))
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "RECOMMENDED ACTION",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = CyberEmerald
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = rec.correctiveRemediation,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimaryDark
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val drillAction = rec.correctiveRemediation
            showRemediationDialog = null
            onNavigateToDrill?.invoke(drillAction)
          },
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
        ) {
          Text("Launch Diagnostic Drill", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showRemediationDialog = null }) {
          Text("Close", color = TextSecondaryDark)
        }
      },
      containerColor = CyberSurface
    )
  }
}

@Composable
private fun CognitiveStatPill(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = CyberSurfaceElevated,
    border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
  ) {
    Column(
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        ),
        color = color
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = TextSecondaryDark
      )
    }
  }
}
