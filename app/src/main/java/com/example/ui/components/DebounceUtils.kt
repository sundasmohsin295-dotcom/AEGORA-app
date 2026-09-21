package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role

/**
 * Enterprise Debounce Manager.
 * Guarantees a minimum threshold (default 300ms) between clicks to prevent rapid-fire double taps,
 * racing state mutations, or queued background duplicate tasks.
 */
object DebounceManager {
  private var lastClickTime: Long = 0L
  const val DEFAULT_DEBOUNCE_MILLIS: Long = 300L

  /**
   * Thread-safe check ensuring an action only fires once per debounce window.
   */
  @Synchronized
  fun canClick(thresholdMillis: Long = DEFAULT_DEBOUNCE_MILLIS): Boolean {
    val currentTime = System.currentTimeMillis()
    val elapsed = currentTime - lastClickTime
    return if (elapsed >= thresholdMillis) {
      lastClickTime = currentTime
      true
    } else {
      false
    }
  }

  /**
   * Wraps an onClick lambda inside a 300ms debounce guard.
   */
  fun wrap(thresholdMillis: Long = DEFAULT_DEBOUNCE_MILLIS, onClick: () -> Unit): () -> Unit {
    return {
      if (canClick(thresholdMillis)) {
        onClick()
      }
    }
  }
}

/**
 * Composable extension on Modifier to safely handle debounced clicks.
 */
@Composable
fun Modifier.debouncedClickable(
  thresholdMillis: Long = DebounceManager.DEFAULT_DEBOUNCE_MILLIS,
  enabled: Boolean = true,
  role: Role? = null,
  onClick: () -> Unit
): Modifier {
  val interactionSource = remember { MutableInteractionSource() }
  return this.clickable(
    interactionSource = interactionSource,
    indication = null,
    enabled = enabled,
    role = role,
    onClick = {
      if (DebounceManager.canClick(thresholdMillis)) {
        onClick()
      }
    }
  )
}
