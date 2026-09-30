package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
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

private val AegoraLightColorScheme = lightColorScheme(
  primary = Color(0xFF0284C7),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = Color(0xFF64748B),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFF1F5F9),
  onSecondaryContainer = Color(0xFF334155),
  tertiary = Color(0xFFD97706),
  onTertiary = Color.White,
  error = Color(0xFFDC2626),
  onError = Color.White,
  errorContainer = Color(0xFFFEE2E2),
  onErrorContainer = Color(0xFF991B1B),
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1)
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
  val isDark by ThemeManager.isDarkTheme.collectAsState()
  val colorScheme = if (isDark) ObsidianColorScheme else AegoraLightColorScheme
  val tokens = if (isDark) DarkColorTokens else LightColorTokens

  CompositionLocalProvider(LocalAegoraTokens provides tokens) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = MonospaceTypography,
      content = content
    )
  }
}

@Composable
fun AegoraTheme(
  content: @Composable () -> Unit
) {
  ObsidianIndustrialTheme(content = content)
}
