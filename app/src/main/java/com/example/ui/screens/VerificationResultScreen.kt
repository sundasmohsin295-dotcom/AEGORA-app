package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun VerificationResultScreen(
  onNavigateBack: () -> Unit,
  onViewAutopsy: () -> Unit,
  onViewPassport: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(SpecCanvasBg)
      .testTag("screen_verification_result"),
    containerColor = SpecCanvasBg,
    topBar = {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .background(SpecCanvasBg)
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SpecElevatedBg)
            .border(1.dp, SpecBorder, RoundedCornerShape(10.dp))
            .testTag("verification_result_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = SpecHeadingWhite
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
          text = "Verification Result",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = SpecHeadingWhite,
          fontFamily = FontFamily.Monospace
        )
      }
    },
    bottomBar = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(SpecCanvasBg)
          .navigationBarsPadding()
          .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // VIEW FAILURE AUTOPSY Button
        Button(
          onClick = onViewAutopsy,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("verification_result_autopsy_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = SpecElevatedBg),
          border = BorderStroke(1.dp, SpecPrimaryBlue),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Analytics,
            contentDescription = null,
            tint = SpecPrimaryBlue,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "VIEW FAILURE AUTOPSY",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SpecHeadingWhite,
            letterSpacing = 1.sp
          )
        }

        // Secondary: GO TO VERIFIED PASSPORT
        Button(
          onClick = onViewPassport,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("verification_result_passport_btn"),
          colors = ButtonDefaults.buttonColors(containerColor = SpecPrimaryBlue),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "INSPECT VERIFIED PROOF",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 0.5.sp
          )
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Large Emerald Circle with Checkmark
      Box(
        modifier = Modifier
          .size(100.dp)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors = listOf(
                SpecEmeraldVerification.copy(alpha = 0.25f),
                SpecEmeraldVerification.copy(alpha = 0.05f)
              )
            )
          )
          .border(2.dp, SpecEmeraldVerification, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Success",
          tint = SpecEmeraldVerification,
          modifier = Modifier.size(54.dp)
        )
      }

      Text(
        text = "AI FAILURE DETECTED ✓",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        color = SpecEmeraldVerification,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center
      )

      // Checklist Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("verification_result_checklist_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SpecEmeraldVerification.copy(alpha = 0.2f))
                .border(1.dp, SpecEmeraldVerification, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SpecEmeraldVerification,
                modifier = Modifier.size(14.dp)
              )
            }
            Text(
              text = "EVIDENCE VERIFIED",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecHeadingWhite
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SpecEmeraldVerification.copy(alpha = 0.2f))
                .border(1.dp, SpecEmeraldVerification, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SpecEmeraldVerification,
                modifier = Modifier.size(14.dp)
              )
            }
            Text(
              text = "HUMAN DECISION CORRECT",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecHeadingWhite
            )
          }
        }
      }

      // Diagnostic Card: FAILURE PATTERN IDENTIFIED
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("verification_result_diagnostic_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SpecCardBg),
        border = BorderStroke(1.dp, SpecBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            color = SpecWarningAmber.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, SpecWarningAmber.copy(alpha = 0.5f))
          ) {
            Text(
              text = "FAILURE PATTERN IDENTIFIED // Premature Conclusion",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace,
              color = SpecWarningAmber,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Text(
            text = "The AI relied on incomplete context and misinterpreted normal behavior as malicious.",
            fontSize = 13.sp,
            color = SpecSubtextSlate,
            lineHeight = 20.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
