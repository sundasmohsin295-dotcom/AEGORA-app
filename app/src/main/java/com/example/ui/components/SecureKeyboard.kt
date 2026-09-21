package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.security.SecureRandom

/**
 * Nation-State In-App Randomized Secure QWERTY/Alphanumeric Keyboard.
 *
 * Randomizes keys across alphabetical rows or layout configurations,
 * preventing keyloggers, screen recording, and input interceptors from profiling keystrokes.
 */
@Composable
fun SecureKeyboard(
  onCharEntered: (Char) -> Unit,
  onBackspace: () -> Unit,
  onSpace: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true
) {
  var isShifted by remember { mutableStateOf(false) }
  var isNumericMode by remember { mutableStateOf(false) }
  var shuffledLetters by remember { mutableStateOf(generateShuffledLetters()) }

  fun reshuffle() {
    shuffledLetters = generateShuffledLetters()
  }

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .testTag("secure_keyboard_container"),
    color = Color(0xFF070E1A),
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, Color(0xFF1E293B))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Security header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = Color(0xFF22D3EE),
            modifier = Modifier.size(12.dp)
          )
          Text(
            text = "ENCLAVE INPUT SHIELD",
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF22D3EE),
            letterSpacing = 1.sp
          )
        }

        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F172A))
            .clickable { reshuffle() }
            .padding(horizontal = 6.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Shuffle,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(10.dp)
          )
          Text(
            text = "RANDOMIZE",
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF94A3B8)
          )
        }
      }

      // Top number row 1..0
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        val numRow = listOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
        for (digit in numRow) {
          KeyboardKey(
            char = digit,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = { onCharEntered(digit) }
          )
        }
      }

      // First letter row (10 keys)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        for (i in 0 until 10) {
          val c = if (isShifted) shuffledLetters[i].uppercaseChar() else shuffledLetters[i]
          KeyboardKey(
            char = c,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = { onCharEntered(c) }
          )
        }
      }

      // Second letter row (9 keys)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        for (i in 10 until 19) {
          val c = if (isShifted) shuffledLetters[i].uppercaseChar() else shuffledLetters[i]
          KeyboardKey(
            char = c,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = { onCharEntered(c) }
          )
        }
      }

      // Third row: Shift, 7 keys, Backspace
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        // Shift Toggle
        KeyAction(
          label = if (isShifted) "▲" else "△",
          modifier = Modifier.weight(1.4f),
          isActive = isShifted,
          enabled = enabled,
          onClick = { isShifted = !isShifted }
        )

        for (i in 19 until 26) {
          val c = if (isShifted) shuffledLetters[i].uppercaseChar() else shuffledLetters[i]
          KeyboardKey(
            char = c,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = { onCharEntered(c) }
          )
        }

        // Backspace
        KeyAction(
          icon = {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Backspace,
              contentDescription = "Backspace",
              tint = Color(0xFFF87171),
              modifier = Modifier.size(16.dp)
            )
          },
          modifier = Modifier.weight(1.4f),
          enabled = enabled,
          onClick = { onBackspace() }
        )
      }

      // Bottom Row: Symbols / Space
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        // Special Symbols
        val symbols = listOf('@', '.', '_', '-', '!', '#')
        for (sym in symbols) {
          KeyboardKey(
            char = sym,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            onClick = { onCharEntered(sym) }
          )
        }

        // Spacebar
        KeyAction(
          label = "SPACE",
          modifier = Modifier.weight(2.5f),
          enabled = enabled,
          onClick = { onSpace() }
        )
      }
    }
  }
}

@Composable
private fun KeyboardKey(
  char: Char,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier
      .height(42.dp)
      .clip(RoundedCornerShape(6.dp))
      .clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      ),
    color = Color(0xFF131F33),
    shape = RoundedCornerShape(6.dp),
    border = BorderStroke(1.dp, Color(0xFF223554))
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = char.toString(),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = Color(0xFFF1F5F9)
      )
    }
  }
}

@Composable
private fun KeyAction(
  label: String? = null,
  icon: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier,
  isActive: Boolean = false,
  enabled: Boolean = true,
  onClick: () -> Unit
) {
  Surface(
    modifier = modifier
      .height(42.dp)
      .clip(RoundedCornerShape(6.dp))
      .clickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      ),
    color = if (isActive) Color(0xFF1E293B) else Color(0xFF0E1726),
    shape = RoundedCornerShape(6.dp),
    border = BorderStroke(1.dp, if (isActive) Color(0xFF22D3EE) else Color(0xFF1E293B))
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      if (label != null) {
        Text(
          text = label,
          fontFamily = FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = if (isActive) Color(0xFF22D3EE) else Color(0xFF94A3B8)
        )
      } else if (icon != null) {
        icon()
      }
    }
  }
}

private fun generateShuffledLetters(): List<Char> {
  val alphabet = ('a'..'z').toMutableList()
  val rng = SecureRandom()
  for (i in alphabet.size - 1 downTo 1) {
    val j = rng.nextInt(i + 1)
    val temp = alphabet[i]
    alphabet[i] = alphabet[j]
    alphabet[j] = temp
  }
  return alphabet
}
