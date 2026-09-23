package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ObsidianColorScheme = darkColorScheme(
  primary = ElectricCyan,
  onPrimary = ObsidianBackground,
  primaryContainer = ElectricCyanDark,
  onPrimaryContainer = ElectricCyan,
  secondary = SlateBorderBright,
  onSecondary = TextPrimary,
  secondaryContainer = ObsidianSurfaceRaised,
  onSecondaryContainer = TextMuted,
  tertiary = TacticalAmber,
  onTertiary = ObsidianBackground,
  error = HighAlertCrimson,
  onError = ObsidianBackground,
  errorContainer = HighAlertCrimsonDark,
  onErrorContainer = HighAlertCrimson,
  background = ObsidianBackground,
  onBackground = TextPrimary,
  surface = ObsidianSurface,
  onSurface = TextPrimary,
  surfaceVariant = ObsidianSurfaceRaised,
  onSurfaceVariant = TextMuted,
  outline = SlateBorder
)

val MonospaceTypography = Typography(
  displayLarge = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 38.sp,
    letterSpacing = 0.5.sp,
    color = TextPrimary
  ),
  headlineMedium = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.25.sp,
    color = TextPrimary
  ),
  titleMedium = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.15.sp,
    color = TextPrimary
  ),
  bodyLarge = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 0.25.sp,
    color = TextMuted
  ),
  bodyMedium = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.2.sp,
    color = TextMuted
  ),
  labelSmall = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.5.sp,
    color = TextDim
  )
)

@Composable
fun ObsidianIndustrialTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = ObsidianColorScheme,
    typography = MonospaceTypography,
    content = content
  )
}

@Composable
fun AegoraTheme(
  content: @Composable () -> Unit
) {
  ObsidianIndustrialTheme(content = content)
}
