package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val VibrantColorScheme = lightColorScheme(
  primary = CyberCyan,
  onPrimary = CyberSurface,
  primaryContainer = VibrantPurpleContainer,
  onPrimaryContainer = VibrantPurpleOnContainer,
  secondary = CyberBlue,
  onSecondary = CyberSurface,
  secondaryContainer = VibrantBlueContainer,
  onSecondaryContainer = VibrantBlueOnContainer,
  tertiary = CyberViolet,
  onTertiary = CyberSurface,
  tertiaryContainer = VibrantPinkContainer,
  onTertiaryContainer = VibrantPinkOnContainer,
  background = CyberBackground,
  onBackground = TextPrimaryDark,
  surface = CyberSurface,
  onSurface = TextPrimaryDark,
  surfaceVariant = CyberSurfaceVariant,
  onSurfaceVariant = TextSecondaryDark,
  outline = CyberBorder,
  outlineVariant = CyberBorderSubtle,
  error = CyberCrimson,
  onError = CyberSurface
)

@Composable
fun AegoraTheme(
  darkTheme: Boolean = false,
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = VibrantColorScheme,
    typography = Typography,
    content = content
  )
}

