package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// =============================================================================
// 1. NON-STANDARD CYBER-GEOMETRY SHAPES (Hexagons, Cut-Corners & Parallelograms)
// =============================================================================

/**
 * High-Tech Chamfered Polygon Shape (Cut-corners on top-left and bottom-right)
 */
val ChamferedCutCornerShape: Shape = GenericShape { size, _ ->
  val cut = 16f
  moveTo(cut, 0f)
  lineTo(size.width, 0f)
  lineTo(size.width, size.height - cut)
  lineTo(size.width - cut, size.height)
  lineTo(0f, size.height)
  lineTo(0f, cut)
  close()
}

/**
 * Asymmetrical Skewed Parallelogram / Mission Shape
 */
val SkewedCyberCardShape: Shape = GenericShape { size, _ ->
  val slant = 14f
  moveTo(slant, 0f)
  lineTo(size.width, 0f)
  lineTo(size.width - slant, size.height)
  lineTo(0f, size.height)
  close()
}

/**
 * Regular Hexagon Shape for Skill Nodes and Insignias
 */
val HexagonShape: Shape = GenericShape { size, _ ->
  val w = size.width
  val h = size.height
  moveTo(w * 0.5f, 0f)
  lineTo(w, h * 0.25f)
  lineTo(w, h * 0.75f)
  lineTo(w * 0.5f, h)
  lineTo(0f, h * 0.75f)
  lineTo(0f, h * 0.25f)
  close()
}

// =============================================================================
// 2. SUBTLE BACKGROUND CYBER GRID MODIFIER
// =============================================================================

/**
 * Renders technical grid dots and subtle blueprint guidelines on OLED black canvas
 */
fun Modifier.cyberGridBackground(
  gridColor: Color = Color(0xFF1E1E2E).copy(alpha = 0.4f),
  gridSize: Float = 48f
): Modifier = this.drawBehind {
  val width = size.width
  val height = size.height

  var x = 0f
  while (x < width) {
    drawLine(
      color = gridColor,
      start = Offset(x, 0f),
      end = Offset(x, height),
      strokeWidth = 0.8f
    )
    x += gridSize
  }

  var y = 0f
  while (y < height) {
    drawLine(
      color = gridColor,
      start = Offset(0f, y),
      end = Offset(width, y),
      strokeWidth = 0.8f
    )
    y += gridSize
  }

  // Crosshair corners
  val crossSize = 6f
  var cx = gridSize
  while (cx < width) {
    var cy = gridSize
    while (cy < height) {
      drawLine(
        color = gridColor.copy(alpha = 0.6f),
        start = Offset(cx - crossSize, cy),
        end = Offset(cx + crossSize, cy),
        strokeWidth = 1.2f
      )
      drawLine(
        color = gridColor.copy(alpha = 0.6f),
        start = Offset(cx, cy - crossSize),
        end = Offset(cx, cy + crossSize),
        strokeWidth = 1.2f
      )
      cy += gridSize * 2
    }
    cx += gridSize * 2
  }
}

// =============================================================================
// 3. CYBER ANGLED CARD & GLASSMORPHIC CONTAINERS
// =============================================================================

@Composable
fun CyberCard(
  modifier: Modifier = Modifier,
  borderColor: Color = CyberBorder,
  backgroundColor: Color = CyberSurface,
  shapeRadius: Dp = 20.dp,
  customShape: Shape? = null,
  isGlowing: Boolean = false,
  glowColor: Color = CyberCyan,
  onClick: (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit
) {
  val shape = customShape ?: RoundedCornerShape(shapeRadius)
  val borderStroke = if (isGlowing) 1.5.dp else 1.dp
  val borderCol = if (isGlowing) glowColor else borderColor

  val baseModifier = if (onClick != null) {
    modifier
      .clip(shape)
      .clickable { onClick() }
  } else {
    modifier.clip(shape)
  }

  Surface(
    modifier = baseModifier
      .border(borderStroke, borderCol, shape),
    color = backgroundColor,
    shape = shape,
    tonalElevation = 2.dp
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      content = content
    )
  }
}

/**
 * Skewed Mission Card with Chamfered Tech Header
 */
@Composable
fun CyberMissionCard(
  title: String,
  subtitle: String,
  tag: String,
  tagColor: Color = CyberCyan,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  content: @Composable ColumnScope.() -> Unit
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(ChamferedCutCornerShape)
      .border(1.2.dp, tagColor.copy(alpha = 0.7f), ChamferedCutCornerShape)
      .clickable { onClick() },
    color = CyberSurface,
    tonalElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .background(
          Brush.linearGradient(
            listOf(
              tagColor.copy(alpha = 0.10f),
              CyberSurface
            )
          )
        )
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = tagColor.copy(alpha = 0.18f),
          border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.6f))
        ) {
          Text(
            text = tag.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold,
              fontSize = 9.sp,
              letterSpacing = 0.5.sp
            ),
            color = tagColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Text(
          text = "// ACTIVE OPERATION",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp
          ),
          color = TextTertiaryDark
        )
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = TextPrimaryDark
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = TextSecondaryDark
      )

      Spacer(modifier = Modifier.height(10.dp))
      content()
    }
  }
}

// =============================================================================
// 4. BEHAVIORAL UX: ENDOWED PROGRESS EFFECT (Momentum Boost Progress Bars)
// =============================================================================

@Composable
fun EndowedProgressBar(
  progress: Float, // Actual user progress (0.0 to 1.0)
  endowedBonus: Float = 0.18f, // 18% bonus baseline to trigger goal-gradient completion drive
  modifier: Modifier = Modifier,
  label: String? = null,
  valueText: String? = null,
  barColor: Color = CyberCyan,
  bonusColor: Color = CyberEmerald,
  trackColor: Color = CyberSurfaceVariant,
  height: Dp = 12.dp,
  showBonusTag: Boolean = true
) {
  val animatedActualProgress by animateFloatAsState(
    targetValue = progress.coerceIn(0f, 1f),
    animationSpec = spring(stiffness = Spring.StiffnessLow),
    label = "actual_progress"
  )

  val totalEffectiveProgress = (animatedActualProgress + endowedBonus).coerceIn(0f, 1f)

  Column(modifier = modifier.fillMaxWidth()) {
    if (label != null || valueText != null || showBonusTag) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (label != null) {
            Text(
              text = label,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              color = TextPrimaryDark
            )
          }
          if (showBonusTag) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = bonusColor.copy(alpha = 0.18f),
              border = androidx.compose.foundation.BorderStroke(1.dp, bonusColor.copy(alpha = 0.4f))
            ) {
              Text(
                text = "+${(endowedBonus * 100).toInt()}% BONUS START",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                ),
                color = bonusColor,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }
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
        .border(1.dp, CyberBorderSubtle, RoundedCornerShape(height / 2))
    ) {
      // Endowed baseline bar
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(totalEffectiveProgress)
          .clip(RoundedCornerShape(height / 2))
          .background(bonusColor.copy(alpha = 0.35f))
      )
      // Earned progress bar
      Box(
        modifier = Modifier
          .fillMaxHeight()
          .fillMaxWidth(animatedActualProgress)
          .clip(RoundedCornerShape(height / 2))
          .background(barColor)
      )
    }
  }
}

@Composable
fun SkillProgressBar(
  progress: Float,
  modifier: Modifier = Modifier,
  label: String? = null,
  valueText: String? = null,
  barColor: Color = CyberCyan,
  trackColor: Color = CyberSurfaceVariant,
  height: Dp = 10.dp
) {
  EndowedProgressBar(
    progress = progress,
    endowedBonus = 0f,
    modifier = modifier,
    label = label,
    valueText = valueText,
    barColor = barColor,
    trackColor = trackColor,
    height = height,
    showBonusTag = false
  )
}

// =============================================================================
// 5. CURIOSITY GAPS & INFORMATION WITHHOLDING (Glitch & Redacted UI)
// =============================================================================

@Composable
fun CuriosityGlitchText(
  secretText: String,
  isUnlocked: Boolean = false,
  classification: String = "RESTRICTED // SCIF LEVEL 4",
  modifier: Modifier = Modifier
) {
  val chars = "█▓▒░01§ΔΨλΩ#%&?*!+"
  var displayText by remember { mutableStateOf("") }
  var isTemporarilyDecrypted by remember { mutableStateOf(false) }

  LaunchedEffect(isUnlocked, isTemporarilyDecrypted) {
    if (isUnlocked || isTemporarilyDecrypted) {
      displayText = secretText
    } else {
      while (true) {
        val glitch = buildString {
          for (i in 0 until secretText.length.coerceAtMost(32)) {
            append(chars[Random.nextInt(chars.length)])
          }
        }
        displayText = glitch
        delay(120)
      }
    }
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = CodeBackground,
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isUnlocked || isTemporarilyDecrypted) CyberEmerald.copy(alpha = 0.7f) else CyberCrimson.copy(alpha = 0.6f)
    ),
    modifier = modifier
      .clickable {
        if (!isUnlocked) {
          isTemporarilyDecrypted = true
        }
      }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
        contentDescription = null,
        tint = if (isUnlocked || isTemporarilyDecrypted) CyberEmerald else CyberCrimson,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Column {
        Text(
          text = if (isUnlocked) "UNLOCKED" else classification,
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
          ),
          color = if (isUnlocked || isTemporarilyDecrypted) CyberEmerald else CyberCrimson
        )
        Text(
          text = displayText,
          style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          ),
          color = if (isUnlocked || isTemporarilyDecrypted) CodeGreen else CodeAmber
        )
      }
    }
  }
}

// =============================================================================
// 6. ATTENTION-PULLING ANIMATION: RHYTHMIC BREATHING PULSE GLOW
// =============================================================================

@Composable
fun PulsingBreathingContainer(
  pulseColor: Color = CyberAmber,
  isActive: Boolean = true,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
  val alpha by infiniteTransition.animateFloat(
    initialValue = 0.25f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  val borderThickness = if (isActive) (1.dp + (alpha * 1.5).dp) else 1.dp

  Box(
    modifier = modifier
      .drawBehind {
        if (isActive) {
          drawRoundRect(
            color = pulseColor.copy(alpha = alpha * 0.45f),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
            style = Stroke(width = 10.dp.toPx())
          )
        }
      }
      .border(
        width = borderThickness,
        color = if (isActive) pulseColor.copy(alpha = alpha) else CyberBorder,
        shape = RoundedCornerShape(20.dp)
      )
      .clip(RoundedCornerShape(20.dp))
  ) {
    content()
  }
}

// =============================================================================
// 7. TACTILE 'HOLD-TO-HACK' MICRO-INTERACTION BUTTON
// =============================================================================

@Composable
fun HoldToHackButton(
  text: String,
  onComplete: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector = Icons.Default.Bolt,
  holdDurationMs: Long = 1000L,
  primaryColor: Color = CyberCyan,
  activeColor: Color = CyberEmerald,
  testTag: String = "hold_to_hack_btn"
) {
  val coroutineScope = rememberCoroutineScope()
  var isHolding by remember { mutableStateOf(false) }
  val holdProgress = remember { Animatable(0f) }
  val shakeOffset = remember { Animatable(0f) }

  // Localized screen shake on holding
  LaunchedEffect(isHolding) {
    if (isHolding) {
      while (isHolding) {
        shakeOffset.animateTo(
          targetValue = (Random.nextFloat() - 0.5f) * 6f,
          animationSpec = tween(durationMillis = 40)
        )
      }
      shakeOffset.animateTo(0f, tween(100))
    } else {
      shakeOffset.animateTo(0f, tween(100))
    }
  }

  val currentColor = Color(
    red = primaryColor.red + (activeColor.red - primaryColor.red) * holdProgress.value,
    green = primaryColor.green + (activeColor.green - primaryColor.green) * holdProgress.value,
    blue = primaryColor.blue + (activeColor.blue - primaryColor.blue) * holdProgress.value,
    alpha = 1f
  )

  Surface(
    shape = ChamferedCutCornerShape,
    color = CyberSurfaceElevated,
    border = androidx.compose.foundation.BorderStroke(1.5.dp, currentColor),
    modifier = modifier
      .offset(x = shakeOffset.value.dp)
      .testTag(testTag)
      .pointerInput(Unit) {
        detectTapGestures(
          onPress = {
            isHolding = true
            val job = coroutineScope.launch {
              holdProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = holdDurationMs.toInt(), easing = LinearEasing)
              )
              if (holdProgress.value >= 0.99f) {
                onComplete()
              }
            }
            tryAwaitRelease()
            isHolding = false
            job.cancel()
            coroutineScope.launch {
              holdProgress.animateTo(0f, tween(200))
            }
          }
        )
      }
  ) {
    Box(
      modifier = Modifier
        .padding(horizontal = 18.dp, vertical = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      // Hold progress background bar
      if (holdProgress.value > 0f) {
        Box(
          modifier = Modifier
            .matchParentSize()
            .drawBehind {
              drawRect(
                color = currentColor.copy(alpha = 0.35f),
                size = Size(size.width * holdProgress.value, size.height)
              )
            }
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = if (holdProgress.value > 0.8f) Icons.Default.CheckCircle else icon,
          contentDescription = null,
          tint = currentColor,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isHolding) "HACKING... ${(holdProgress.value * 100).toInt()}%" else text,
          style = MaterialTheme.typography.labelLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          ),
          color = currentColor
        )
      }
    }
  }
}

// =============================================================================
// 8. VARIABLE REWARDS MODAL: "DECRYPTING CACHE..." (Skinner Box Engagement)
// =============================================================================

data class DecryptedReward(
  val xpMultiplier: String,
  val bonusXp: Int,
  val rareBadgeName: String,
  val rareBadgeIcon: ImageVector,
  val loreFragment: String
)

@Composable
fun DecryptingCacheDialog(
  onDismiss: () -> Unit,
  reward: DecryptedReward = DecryptedReward(
    xpMultiplier = "3.2x CRITICAL OVERCLOCK",
    bonusXp = 450,
    rareBadgeName = "Zero-Day Memory Sentinel",
    rareBadgeIcon = Icons.Default.MilitaryTech,
    loreFragment = "Declassified Memo #881: NSA Tailored Access Operations kernel rootkit signature analyzed."
  )
) {
  var stage by remember { mutableIntStateOf(0) } // 0: Decrypting spinner, 1: Reward revealed

  LaunchedEffect(Unit) {
    delay(1600)
    stage = 1
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
  ) {
    Surface(
      shape = ChamferedCutCornerShape,
      color = Color(0xFF09090D),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberGold),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (stage == 0) {
          // Decrypting state animation
          Icon(
            imageVector = Icons.Default.Key,
            contentDescription = null,
            tint = CyberGold,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "DECRYPTING CLASSIFIED CACHE...",
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = CyberGold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Synthesizing dynamic XP multiplier & intelligence fragment...",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondaryDark,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))
          LinearProgressIndicator(
            color = CyberGold,
            trackColor = Color(0xFF1E1E2E),
            modifier = Modifier.fillMaxWidth().height(6.dp)
          )
        } else {
          // Revealed Variable Reward
          Surface(
            shape = CircleShape,
            color = CyberGold.copy(alpha = 0.2f),
            modifier = Modifier.size(56.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(reward.rareBadgeIcon, contentDescription = null, tint = CyberGold, modifier = Modifier.size(32.dp))
            }
          }

          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "CACHE UNLOCKED!",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = CyberGold
          )

          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = CyberEmerald.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberEmerald)
          ) {
            Text(
              text = reward.xpMultiplier,
              style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
              ),
              color = CyberEmerald,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "+${reward.bonusXp} BONUS EXP GRANTED",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )

          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF13131A),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "RARE ARTIFACT REWARD:",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                color = CyberGold
              )
              Text(
                text = reward.rareBadgeName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimaryDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = reward.loreFragment,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                color = TextSecondaryDark
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Claim & Sync to Passport", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// =============================================================================
// 9. MATRIX CODE RAIN / SUCCESS CONFETTI OVERLAY
// =============================================================================

@Composable
fun MatrixCodeRainOverlay(
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.fillMaxSize()) {
    val cols = 16
    val colWidth = size.width / cols
    for (i in 0 until cols) {
      val y = (System.currentTimeMillis() / (20 + (i % 5) * 10) + i * 80) % size.height.toInt()
      drawCircle(
        color = Color(0xFF00FF41).copy(alpha = 0.45f),
        radius = 3.dp.toPx(),
        center = Offset(i * colWidth + colWidth / 2, y.toFloat())
      )
    }
  }
}

// =============================================================================
// 10. HEXAGONAL SKILL NODE COMPONENT (Hollow Neon Outline / Glowing Solid Fill)
// =============================================================================

@Composable
fun HexagonalSkillNode(
  title: String,
  level: Int,
  masteryPct: Int,
  icon: ImageVector,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  accentColor: Color = CyberCyan,
  isSelected: Boolean = false
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .width(84.dp)
      .clickable { onClick() }
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(HexagonShape)
        .background(
          if (isSelected) accentColor.copy(alpha = 0.25f) else Color.Transparent
        )
        .border(
          width = if (isSelected) 2.dp else 1.2.dp,
          color = if (isSelected) accentColor else accentColor.copy(alpha = 0.45f),
          shape = HexagonShape
        ),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = if (isSelected) accentColor else TextSecondaryDark,
          modifier = Modifier.size(22.dp)
        )
        Text(
          text = "${masteryPct}%",
          style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          ),
          color = accentColor
        )
      }
    }

    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        fontSize = 11.sp
      ),
      color = if (isSelected) accentColor else TextPrimaryDark,
      maxLines = 1
    )
    Text(
      text = "Lvl $level",
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 9.sp,
        color = TextSecondaryDark
      )
    )
  }
}

// =============================================================================
// 11. CODE TERMINAL VIEW (Pitch Black Monospace Output & Glass Header)
// =============================================================================

@Composable
fun CodeTerminalView(
  code: String,
  title: String = "TERMINAL / LOG STREAM",
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(ChamferedCutCornerShape)
      .border(1.2.dp, CyberCyan.copy(alpha = 0.6f), ChamferedCutCornerShape),
    color = CodeBackground
  ) {
    Column {
      // Terminal Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF13131A))
          .border(androidx.compose.foundation.BorderStroke(1.dp, CyberBorderSubtle))
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
          Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF00FF41)))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Bold
            ),
            color = TextSecondaryDark
          )
        }

        Icon(
          imageVector = Icons.Default.Terminal,
          contentDescription = null,
          tint = CyberCyan,
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

// =============================================================================
// 12. EVIDENCE BADGE & SECTION HEADERS
// =============================================================================

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
      .clip(ChamferedCutCornerShape)
      .border(1.dp, CyberBorderSubtle, ChamferedCutCornerShape),
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
          .clip(HexagonShape)
          .background(VibrantMintContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Verified,
          contentDescription = "Verified Evidence",
          tint = VibrantMintOnContainer,
          modifier = Modifier.size(20.dp)
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
