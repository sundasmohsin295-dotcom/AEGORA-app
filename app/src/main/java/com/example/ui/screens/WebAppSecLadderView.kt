package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.model.AppSecDifficulty
import com.example.model.WebAppSecCategory
import com.example.model.WebAppSecLevel
import com.example.ui.components.CyberCard
import com.example.ui.theme.*

@Composable
fun WebAppSecLadderView(
  modifier: Modifier = Modifier
) {
  val categories = AegoraRepository.webAppSecCategories
  var selectedCategoryId by remember { mutableStateOf(categories.first().id) }
  val activeCategory = categories.find { it.id == selectedCategoryId } ?: categories.first()

  var selectedLevelId by remember { mutableStateOf(activeCategory.levels.first().levelId) }
  val activeLevel = activeCategory.levels.find { it.levelId == selectedLevelId } ?: activeCategory.levels.first()

  var userPayload by remember { mutableStateOf(activeLevel.mutatedPayloadTemplate) }
  var executedResponse by remember { mutableStateOf<String?>(null) }
  var isMutatedSeedActive by remember { mutableStateOf(true) }

  LaunchedEffect(selectedCategoryId) {
    val cat = categories.find { it.id == selectedCategoryId } ?: categories.first()
    selectedLevelId = cat.levels.first().levelId
    userPayload = cat.levels.first().mutatedPayloadTemplate
    executedResponse = null
  }

  LaunchedEffect(selectedLevelId) {
    userPayload = activeLevel.mutatedPayloadTemplate
    executedResponse = null
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Vulnerability Class Selector (SQLi, XSS, SSRF, IDOR)
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(categories) { cat ->
        val isSelected = cat.id == selectedCategoryId
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) CyberSurfaceElevated else CyberSurface,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CyberCyan else CyberBorder
          ),
          modifier = Modifier
            .clickable { selectedCategoryId = cat.id }
            .testTag("appsec_cat_${cat.id}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              when (cat.id) {
                "sqli" -> Icons.Default.Storage
                "xss" -> Icons.Default.Code
                "ssrf" -> Icons.Default.Cloud
                else -> Icons.Default.Lock
              },
              contentDescription = null,
              tint = if (isSelected) CyberCyan else TextSecondaryDark,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = cat.name,
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              ),
              color = if (isSelected) CyberCyan else TextSecondaryDark
            )
          }
        }
      }
    }

    // 2. Scenario Mutation Seed Banner
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = CyberMagenta.copy(alpha = 0.12f),
      border = androidx.compose.foundation.BorderStroke(1.dp, CyberMagenta.copy(alpha = 0.4f)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(Icons.Default.Shuffle, contentDescription = null, tint = CyberMagenta, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "SCENARIO MUTATION ENGINE ACTIVE",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = CyberMagenta
          )
          Text(
            text = "Dynamic parameters prevent walkthrough memorization. Understand the mechanic, don't copy-paste.",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = TextSecondaryDark
          )
        }
      }
    }

    // 3. Sequential Level Sub-selector
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      activeCategory.levels.forEach { level ->
        val isSelected = level.levelId == selectedLevelId
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = if (isSelected) CyberSurfaceElevated else CyberSurface,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(level.difficulty.colorHex) else CyberBorderSubtle
          ),
          modifier = Modifier
            .weight(1f)
            .clickable { selectedLevelId = level.levelId }
        ) {
          Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = level.difficulty.label.uppercase(),
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
              color = Color(level.difficulty.colorHex)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = level.title.substringBefore(":"),
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
              ),
              color = if (isSelected) TextPrimaryDark else TextSecondaryDark
            )
          }
        }
      }
    }

    // 4. Lab Context & CWE Metadata
    CyberCard(
      borderColor = Color(activeLevel.difficulty.colorHex).copy(alpha = 0.5f),
      backgroundColor = CyberSurface
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(activeLevel.difficulty.colorHex).copy(alpha = 0.2f)
          ) {
            Text(
              text = activeLevel.cweId,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(activeLevel.difficulty.colorHex),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = activeLevel.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = activeLevel.scenarioContext,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(6.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Target Endpoint: ", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        Text(activeLevel.targetEndpoint, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberCyan)
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Vulnerable Param: ", style = MaterialTheme.typography.labelSmall, color = TextSecondaryDark)
        Text(activeLevel.vulnerableParameter, style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberAmber)
      }
    }

    // 5. Interactive HTTP Request Payload Console
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = Color(0xFF0D1117),
      border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "PAYLOAD CRAFTING CONSOLE",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = CyberCyan
          )
          Text(
            text = "HTTP/2 RAW STREAM",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 9.sp),
            color = TextSecondaryDark
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = userPayload,
          onValueChange = { userPayload = it },
          label = { Text("Injection Payload", style = MaterialTheme.typography.labelSmall) },
          textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, color = CyberCyan),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("appsec_payload_input"),
          shape = RoundedCornerShape(8.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CyberCyan,
            unfocusedBorderColor = CyberBorder
          )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = {
            executedResponse = activeLevel.simulatedHttpResponse
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("submit_payload_button"),
          colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
          shape = RoundedCornerShape(8.dp)
        ) {
          Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Transmit Injection Payload", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      }
    }

    // 6. Response & Defense Takeaway
    AnimatedVisibility(visible = executedResponse != null) {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF161B22),
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "INTERCEPTED SERVER RESPONSE",
                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                color = CyberEmerald
              )
              Text("200 OK", style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace), color = CyberEmerald)
            }
            Divider(modifier = Modifier.padding(vertical = 6.dp), color = CyberBorderSubtle)
            Text(
              text = executedResponse ?: "",
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
              color = Color(0xFF58A6FF)
            )
          }
        }

        // Defensive Remediation Card
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = CyberSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Shield, contentDescription = null, tint = CyberEmerald, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "DEFENSE IN DEPTH: DEVELOPER PATCH",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = CyberEmerald
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = activeLevel.defenseExplanation,
              style = MaterialTheme.typography.bodySmall,
              color = TextPrimaryDark
            )
          }
        }
      }
    }
  }
}
