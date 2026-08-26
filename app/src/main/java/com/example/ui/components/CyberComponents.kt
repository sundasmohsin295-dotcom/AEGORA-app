package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CyberCard(
  modifier: Modifier = Modifier,
  borderColor: Color = CyberBorder,
  backgroundColor: Color = CyberSurface,
  shapeRadius: Dp = 24.dp,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = RoundedCornerShape(shapeRadius)
  val cardModifier = if (onClick != null) {
    modifier
      .clip(shape)
      .clickable { onClick() }
  } else {
    modifier.clip(shape)
  }

  Surface(
    modifier = cardModifier
      .border(1.dp, borderColor, shape),
    color = backgroundColor,
    shape = shape,
    tonalElevation = 1.dp
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      content = content
    )
  }
}

@Composable
fun SkillProgressBar(
  progress: Float, // 0.0 to 1.0
  modifier: Modifier = Modifier,
  label: String? = null,
  valueText: String? = null,
  barColor: Color = CyberCyan,
  trackColor: Color = CyberSurfaceVariant,
  height: Dp = 10.dp
) {
  val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "skill_progress")

  Column(modifier = modifier.fillMaxWidth()) {
    if (label != null || valueText != null) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (label != null) {
          Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            color = TextPrimaryDark
          )
        }
        if (valueText != null) {
          Text(
            text = valueText,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = barColor
          )
        }
      }
    }

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .clip(RoundedCornerShape(height / 2))
        .background(trackColor)
    ) {
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(animatedProgress)
          .clip(RoundedCornerShape(height / 2))
          .background(barColor)
      )
    }
  }
}

@Composable
fun EvidenceBadge(
  title: String,
  type: String,
  date: String,
  hash: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, CyberBorderSubtle, RoundedCornerShape(16.dp)),
    color = CyberSurface
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(VibrantMintContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Verified,
          contentDescription = "Verified Evidence",
          tint = VibrantMintOnContainer,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          color = TextPrimaryDark
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = type,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = CyberCyan
          )
          Text(
            text = " • $date",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark
          )
        }
        Text(
          text = hash,
          style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
          color = TextTertiaryDark,
          maxLines = 1
        )
      }
    }
  }
}

@Composable
fun CodeTerminalView(
  code: String,
  title: String = "TERMINAL / LOG STREAM",
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, CyberBorder, RoundedCornerShape(16.dp)),
    color = CodeBackground
  ) {
    Column {
      // Terminal Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF2C2834))
          .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF27C93F)))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = Color(0xFFCAC4D0)
          )
        }

        Icon(
          imageVector = Icons.Default.Terminal,
          contentDescription = null,
          tint = Color(0xFFD0BCFF),
          modifier = Modifier.size(16.dp)
        )
      }

      // Monospace Code Output
      Text(
        text = code,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontFamily = FontFamily.Monospace,
          fontSize = 12.sp,
          lineHeight = 18.sp
        ),
        color = CodeGreen,
        modifier = Modifier.padding(14.dp)
      )
    }
  }
}

@Composable
fun CyberSectionHeader(
  title: String,
  subtitle: String? = null,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondaryDark
        )
      }
    }

    if (actionText != null && onActionClick != null) {
      TextButton(onClick = onActionClick) {
        Text(
          text = actionText,
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = CyberCyan
        )
      }
    }
  }
}

