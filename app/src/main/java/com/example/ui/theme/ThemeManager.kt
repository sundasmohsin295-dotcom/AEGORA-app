package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
  private val _isDarkTheme = MutableStateFlow(true)
  val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

  fun toggleTheme() {
    _isDarkTheme.value = !_isDarkTheme.value
  }

  fun setDark(isDark: Boolean) {
    _isDarkTheme.value = isDark
  }
}

data class AegoraColorTokens(
  val background: Color,
  val surface: Color,
  val surfaceRaised: Color,
  val border: Color,
  val borderBright: Color,
  val textPrimary: Color,
  val textMuted: Color,
  val textDim: Color,
  val accent: Color,
  val accentContainer: Color,
  val crimson: Color,
  val crimsonContainer: Color,
  val amber: Color,
  val amberContainer: Color,
  val emerald: Color,
  val emeraldContainer: Color,
  val isDark: Boolean
)

val DarkColorTokens = AegoraColorTokens(
  background = Color(0xFF030712),
  surface = Color(0xFF0B0F19),
  surfaceRaised = Color(0xFF111827),
  border = Color(0xFF1E293B),
  borderBright = Color(0xFF334155),
  textPrimary = Color(0xFFF8FAFC),
  textMuted = Color(0xFF94A3B8),
  textDim = Color(0xFF64748B),
  accent = Color(0xFF00E5FF),
  accentContainer = Color(0xFF082F49),
  crimson = Color(0xFFEF4444),
  crimsonContainer = Color(0xFF450A0A),
  amber = Color(0xFFF59E0B),
  amberContainer = Color(0xFF451A03),
  emerald = Color(0xFF10B981),
  emeraldContainer = Color(0xFF064E3B),
  isDark = true
)

val LightColorTokens = AegoraColorTokens(
  background = Color(0xFFF8FAFC),
  surface = Color(0xFFFFFFFF),
  surfaceRaised = Color(0xFFF1F5F9),
  border = Color(0xFFE2E8F0),
  borderBright = Color(0xFFCBD5E1),
  textPrimary = Color(0xFF0F172A),
  textMuted = Color(0xFF475569),
  textDim = Color(0xFF64748B),
  accent = Color(0xFF0284C7),
  accentContainer = Color(0xFFE0F2FE),
  crimson = Color(0xFFDC2626),
  crimsonContainer = Color(0xFFFEE2E2),
  amber = Color(0xFFD97706),
  amberContainer = Color(0xFFFEF3C7),
  emerald = Color(0xFF059669),
  emeraldContainer = Color(0xFFD1FAE5),
  isDark = false
)

val LocalAegoraTokens = staticCompositionLocalOf { DarkColorTokens }

object AegoraAppTheme {
  val colors: AegoraColorTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalAegoraTokens.current
}
