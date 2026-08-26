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

@Composable
fun AegoraTheme(
  darkTheme: Boolean = true,
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = TrueCyberDarkColorScheme,
    typography = Typography,
    content = content
  )
}



