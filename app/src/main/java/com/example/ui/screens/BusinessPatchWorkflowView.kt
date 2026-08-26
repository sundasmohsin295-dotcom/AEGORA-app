package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.model.BusinessAppPatchLab
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberSectionHeader
import com.example.ui.theme.*

@Composable
fun BusinessPatchWorkflowView(
  modifier: Modifier = Modifier
) {
  val patchLabs = AegoraRepository.businessAppPatchLabs
  var selectedLabId by remember { mutableStateOf(patchLabs.first().id) }
  val activeLab = patchLabs.find { it.id == selectedLabId } ?: patchLabs.first()

  var selectedPatchId by remember { mutableStateOf<String?>(null) }
  var isPatchVerified by remember { mutableStateOf(false) }
  var showComplianceDetails by remember { mutableStateOf(false) }

  LaunchedEffect(selectedLabId) {
    selectedPatchId = null
    isPatchVerified = false
  }

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Enterprise Target Application Picker
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(patchLabs) { lab ->
        val isSelected = lab.id == selectedLabId
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isSelected) CyberSurfaceElevated else CyberSurface,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) CyberCyan else CyberBorder
          ),
          modifier = Modifier
            .clickable { selectedLabId = lab.id }
            .testTag("patch_lab_${lab.id}")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Business,
              contentDescription = null,
              tint = if (isSelected) CyberCyan else TextSecondaryDark,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = lab.appName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) CyberCyan else TextPrimaryDark
              )
              Text(
                text = lab.businessDomain,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = TextSecondaryDark
              )
            }
          }
        }
      }
    }

    // 2. Business Impact & Regulatory Risk Assessment Card
    CyberCard(
      borderColor = CyberCrimson.copy(alpha = 0.5f),
      backgroundColor = CyberSurface
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "STEP 1: BUSINESS RISK & REGULATORY IMPACT",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = CyberCrimson
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = activeLab.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryDark
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = activeLab.businessImpactSummary,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "REGULATORY / COMPLIANCE VIOLATIONS:",
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
        color = TextSecondaryDark
      )
      Spacer(modifier = Modifier.height(4.dp))
      activeLab.complianceViolations.forEach { comp ->
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(vertical = 2.dp)
        ) {
          Icon(Icons.Default.Warning, contentDescription = null, tint = CyberAmber, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(comp, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = CyberAmber)
        }
      }
    }

    // 3. Vulnerable Source Code Viewer
    Surface(
      shape = RoundedCornerShape(10.dp),
      color = Color(0xFF0D1117),
      border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "STEP 2: VULNERABLE PRODUCTION CODE",
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            color = CyberAmber
          )
          Text(
            text = activeLab.vulnerableCodeLanguage,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
            color = CyberCyan
          )
        }
        Divider(modifier = Modifier.padding(vertical = 6.dp), color = CyberBorderSubtle)
        Text(
          text = activeLab.vulnerableCodeSnippet,
          style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp, lineHeight = 15.sp),
          color = Color(0xFFE6EDF3)
        )
      }
    }

    // 4. Proposed Fixes & Architectural Trade-offs
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(
        text = "STEP 3: PROPOSE & DEPLOY SECURE CODE PATCH",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = CyberCyan
      )

      activeLab.patchOptions.forEach { patch ->
        val isSelected = patch.id == selectedPatchId
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = when {
            isSelected && isPatchVerified && patch.isCorrect -> CyberEmerald.copy(alpha = 0.15f)
            isSelected && isPatchVerified && !patch.isCorrect -> CyberCrimson.copy(alpha = 0.15f)
            isSelected -> CyberSurfaceElevated
            else -> CyberSurface
          },
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            when {
              isSelected && isPatchVerified && patch.isCorrect -> CyberEmerald
              isSelected && isPatchVerified && !patch.isCorrect -> CyberCrimson
              isSelected -> CyberCyan
              else -> CyberBorderSubtle
            }
          ),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              selectedPatchId = patch.id
              isPatchVerified = false
            }
            .testTag("patch_option_${patch.id}")
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = patch.codeSnippet,
              style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 15.sp
              ),
              color = Color(0xFFC9D1D9)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Tradeoff: ${patch.architecturalTradeoff}",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = TextSecondaryDark
            )
          }
        }
      }
    }

    // 5. Verify and Deploy Patch Button
    Button(
      onClick = {
        isPatchVerified = true
      },
      enabled = selectedPatchId != null,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("apply_patch_button"),
      colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
      shape = RoundedCornerShape(8.dp)
    ) {
      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Verify & Deploy Patch to CI/CD Pipeline", color = Color.Black, fontWeight = FontWeight.Bold)
    }

    // 6. Verification Result
    AnimatedVisibility(visible = isPatchVerified) {
      val chosen = activeLab.patchOptions.find { it.id == selectedPatchId }
      val isSuccess = chosen?.isCorrect == true

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSuccess) CyberEmerald.copy(alpha = 0.15f) else CyberCrimson.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSuccess) CyberEmerald else CyberCrimson),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              if (isSuccess) Icons.Default.Verified else Icons.Default.Cancel,
              contentDescription = null,
              tint = if (isSuccess) CyberEmerald else CyberCrimson,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isSuccess) "CI/CD BUILD PASSED: +${activeLab.xpReward} XP" else "CI/CD SECURITY REGRESSION DETECTED",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (isSuccess) CyberEmerald else CyberCrimson
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isSuccess) activeLab.verifiedFixExplanation else "This fix introduced subtle logic flaws or relied on client-side trust. Review the RBAC implementation.",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimaryDark
          )
        }
      }
    }
  }
}
