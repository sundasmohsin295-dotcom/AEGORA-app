package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * AEGORA CINEMATIC BOOT SEQUENCE / SPLASH SCREEN
 * Delegates directly to the military-grade enterprise WelcomeScreen boot sequence.
 */
@Composable
fun SplashScreen(
  onSplashFinished: () -> Unit,
  modifier: Modifier = Modifier
) {
  WelcomeScreen(
    onBootComplete = onSplashFinished,
    modifier = modifier
  )
}
