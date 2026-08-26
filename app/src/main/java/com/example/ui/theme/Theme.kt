package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TrueCyberDarkColorScheme = darkColorScheme(
  primary = CyberCyan,
  onPrimary = CyberBackground,
  primaryContainer = VibrantPurpleContainer,
  onPrimaryContainer = VibrantPurpleOnContainer,
  secondary = CyberBlue,
  onSecondary = CyberBackground,
  secondaryContainer = VibrantBlueContainer,
  onSecondaryContainer = VibrantBlueOnContainer,
  tertiary = CyberViolet,
  onTertiary = CyberBackground,
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
  onError = TextPrimaryDark
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


