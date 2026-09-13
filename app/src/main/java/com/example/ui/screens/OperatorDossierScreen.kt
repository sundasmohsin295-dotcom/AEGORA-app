package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegoraRepository
import com.example.ui.components.ProofDossierModal
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3. Operator Dossier & Threat Ranking
 * This strips out messy profile tabs and combines the user's cognitive stats
 * with a sleek, 1-line leaderboard row layout and 2D Cyber Twin capability radar.
 */
@Composable
fun OperatorDossierScreen(
  onNavigateBack: () -> Unit,
  onNavigateToDuel: (() -> Unit)? = null,
  onShowPaywall: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val obsidianBg = Color(0xFF090A0C)
  val matteSteel = Color(0xFF15171C)
  val slateBorder = Color(0xFF2D313A)
  val cobaltBlue = Color(0xFF2962FF)
  val emeraldGreen = Color(0xFF00E676)
  val mutedSlate = Color(0xFF8A919E)

  val userProfile by AegoraRepository.userProfile.collectAsState()
  var showProofDossierModal by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(obsidianBg)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    // Back navigation & Header action
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onNavigateBack,
        modifier = Modifier.size(36.dp).testTag("dossier_back_button")
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
      }

      Text(
        text = "OPERATOR DOSSIER // LEVEL 4",
        color = Color(0xFF8A919E),
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold
      )

      if (onNavigateToDuel != null) {
        Surface(
          onClick = onNavigateToDuel,
          shape = RoundedCornerShape(6.dp),
          color = Color(0x222962FF),
          border = androidx.compose.foundation.BorderStroke(1.dp, cobaltBlue)
        ) {
          Text(
            text = "ENTER DUEL",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }

    // Profile Header
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dossier_profile_header")
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(cobaltBlue),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "SM",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 16.sp
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column {
        Text(
          text = "SUNDAS MOHSIN",
          color = Color.White,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        Text(
          text = "Lvl 4 • Senior SOC Analyst",
          color = Color(0xFF8A919E),
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace
        )
      }

      Spacer(modifier = Modifier.weight(1f))

      OutlinedButton(
        onClick = { showProofDossierModal = true },
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = cobaltBlue),
        border = androidx.compose.foundation.BorderStroke(1.dp, cobaltBlue),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
        modifier = Modifier.testTag("dossier_open_passport_btn")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(13.dp))
          Text(
            text = "PASSPORT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    // 2D Cyber Twin Radar Visual
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .background(matteSteel, RoundedCornerShape(8.dp))
        .border(1.dp, slateBorder, RoundedCornerShape(8.dp))
        .testTag("cyber_twin_radar_box"),
      contentAlignment = Alignment.Center
    ) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.height * 0.38f

        // Draw concentric guide circles
        for (step in 1..3) {
          drawCircle(
            color = slateBorder,
            radius = radius * (step / 3f),
            center = center,
            style = Stroke(width = 1f)
          )
        }

        // Draw 6 axes
        val axisLabels = listOf("T-Hunt", "DFIR", "Reverse", "Cloud", "AD Sec", "AppSec")
        val statValues = listOf(0.85f, 0.90f, 0.75f, 0.88f, 0.92f, 0.78f)

        for (i in 0 until 6) {
          val angle = Math.toRadians(i * 60.0 - 90.0)
          val x = center.x + (radius * cos(angle)).toFloat()
          val y = center.y + (radius * sin(angle)).toFloat()
          drawLine(
            color = slateBorder,
            start = center,
            end = Offset(x, y),
            strokeWidth = 1f
          )
        }

        // Draw Radar polygon
        val polyPath = Path()
        statValues.forEachIndexed { i, value ->
          val angle = Math.toRadians(i * 60.0 - 90.0)
          val x = center.x + (radius * value * cos(angle)).toFloat()
          val y = center.y + (radius * value * sin(angle)).toFloat()
          if (i == 0) polyPath.moveTo(x, y) else polyPath.lineTo(x, y)
        }
        polyPath.close()

        drawPath(
          path = polyPath,
          color = cobaltBlue.copy(alpha = 0.35f)
        )
        drawPath(
          path = polyPath,
          color = cobaltBlue,
          style = Stroke(width = 2.dp.toPx())
        )

        // Draw apex nodes
        statValues.forEachIndexed { i, value ->
          val angle = Math.toRadians(i * 60.0 - 90.0)
          val x = center.x + (radius * value * cos(angle)).toFloat()
          val y = center.y + (radius * value * sin(angle)).toFloat()
          drawCircle(
            color = emeraldGreen,
            radius = 3.dp.toPx(),
            center = Offset(x, y)
          )
        }
      }

      // Center Overlay Label
      Text(
        text = "[ 2D CAPABILITY RADAR ]",
        color = Color(0xFF8A919E),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
      )
    }

    // Verified Missions & Telemetry Section
    Text(
      text = "VERIFIED INCIDENT MISSIONS",
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )

    if (userProfile.completedLabsCount == 0) {
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = matteSteel,
        border = BorderStroke(1.dp, slateBorder),
        modifier = Modifier.fillMaxWidth().testTag("operator_dossier_empty_state")
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            Icons.Default.HourglassEmpty,
            contentDescription = null,
            tint = mutedSlate,
            modifier = Modifier.size(28.dp)
          )
          Text(
            text = "NO MISSIONS ENGAGED YET",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = "Complete hands-on incident simulations in OPERATE or DUEL ARENA to materialize cryptographically verified leaves.",
            color = mutedSlate,
            fontSize = 10.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      val verifiedMissions = listOf("Sysmon EDR", "Cobalt Strike", "PowerShell T1059").take(userProfile.completedLabsCount.coerceAtMost(3))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        verifiedMissions.forEach { missionId ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = matteSteel,
            border = BorderStroke(1.dp, emeraldGreen.copy(alpha = 0.5f)),
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = emeraldGreen, modifier = Modifier.size(12.dp))
              Text(missionId, color = Color.White, fontSize = 9.sp, maxLines = 1)
            }
          }
        }
      }
    }

    // Leaderboard
    Text(
      text = "GLOBAL THREAT OPS RANKING",
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )

    LazyColumn(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .testTag("threat_ops_ranking_list")
    ) {
      val topOperators = listOf(
        Triple("#1", "Operator_100", "5400 XP"),
        Triple("#2", "Operator_101", "5200 XP"),
        Triple("#3", "Operator_102", "5000 XP"),
        Triple("#4", "Sundas_Mohsin (YOU)", "${userProfile.xp + 4800} XP"),
        Triple("#5", "Operator_104", "4650 XP")
      )

      items(topOperators.size) { index ->
        val rankData = topOperators[index]
        val isUser = rankData.first == "#4"
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              color = if (isUser) cobaltBlue.copy(alpha = 0.18f) else matteSteel,
              shape = RoundedCornerShape(8.dp)
            )
            .border(
              width = 1.dp,
              color = if (isUser) cobaltBlue else slateBorder,
              shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = rankData.first,
              color = if (isUser) cobaltBlue else Color(0xFF8A919E),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
              text = rankData.second,
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal
            )
          }
          Text(
            text = rankData.third,
            color = emeraldGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }
  }

  if (showProofDossierModal) {
    ProofDossierModal(
      onDismiss = { showProofDossierModal = false },
      onShowPaywall = onShowPaywall,
      operatorName = "SUNDAS MOHSIN",
      callsign = "AEG-2026-9942X",
      role = "Lvl 4 • Senior SOC Analyst"
    )
  }
}
