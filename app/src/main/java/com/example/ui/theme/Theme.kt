package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TrueCyberDarkColorScheme = darkColorScheme(
  primary = Color(0xFF00F0FF),
  onPrimary = Color(0xFF000000),
  primaryContainer = Color(0xFF12121A),
  onPrimaryContainer = Color(0xFF00F0FF),
  secondary = Color(0xFF00FF41),
  onSecondary = Color(0xFF000000),
  secondaryContainer = Color(0xFF12121A),
  onSecondaryContainer = Color(0xFF00FF41),
  tertiary = Color(0xFF8A2BE2),
  onTertiary = Color(0xFF000000),
  tertiaryContainer = Color(0xFF12121A),
  onTertiaryContainer = Color(0xFF8A2BE2),
  background = Color(0xFF000000),
  onBackground = Color(0xFFF0F0F5),
  surface = Color(0xFF09090D),
  onSurface = Color(0xFFF0F0F5),
  surfaceVariant = Color(0xFF12121A),
  onSurfaceVariant = Color(0xFFA1A1AA),
  outline = Color(0xFF1F1F2E),
  outlineVariant = Color(0xFF1F1F2E),
  error = Color(0xFFFF003C),
  onError = Color(0xFFF0F0F5)
)

private val TrueCyberLightColorScheme = androidx.compose.material3.lightColorScheme(
  primary = Color(0xFF006B75),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFFE0F7FA),
  onPrimaryContainer = Color(0xFF00373D),
  secondary = Color(0xFF007A2B),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFFE6F9ED),
  onSecondaryContainer = Color(0xFF003911),
  tertiary = Color(0xFF5E17EB),
  onTertiary = Color(0xFFFFFFFF),
  tertiaryContainer = Color(0xFFF3E8FF),
  onTertiaryContainer = Color(0xFF380D8F),
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color(0xFFFFFFFF),
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1),
  outlineVariant = Color(0xFFE2E8F0),
  error = Color(0xFFDC2626),
  onError = Color(0xFFFFFFFF)
)

@Composable
fun AegoraTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit
) {
  val colors = if (darkTheme) TrueCyberDarkColorScheme else TrueCyberLightColorScheme
  MaterialTheme(
    colorScheme = colors,
    typography = Typography,
    content = content
  )
}



