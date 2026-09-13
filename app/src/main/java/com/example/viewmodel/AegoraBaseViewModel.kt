package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Enterprise Base ViewModel for AEGORA.
 * Enforces:
 * 1. Global [CoroutineExceptionHandler] so unexpected coroutine failures NEVER crash the app.
 * 2. Graceful degradation (< 1500ms timeout) that seamlessly swaps in high-fidelity mock payloads.
 * 3. Reactive error states and telemetry monitoring.
 */
abstract class AegoraBaseViewModel : ViewModel() {

  protected val tag: String = this::class.java.simpleName

  private val _isDegradedState = MutableStateFlow(false)
  val isDegradedState: StateFlow<Boolean> = _isDegradedState.asStateFlow()

  private val _lastErrorMessage = MutableStateFlow<String?>(null)
  val lastErrorMessage: StateFlow<String?> = _lastErrorMessage.asStateFlow()

  /**
   * Global CoroutineExceptionHandler preventing unhandled coroutine exceptions
   * from propagating to the Android system thread and crashing the application.
   */
  protected val globalCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.e(tag, "[AEGORA_COROUTINE_SHIELD] Intercepted unhandled exception: ${throwable.localizedMessage}", throwable)
    _lastErrorMessage.value = throwable.localizedMessage ?: "Unknown telemetry fault intercepted"
    _isDegradedState.value = true
    onCoroutineExceptionCaught(throwable)
  }

  /**
   * Hook for child ViewModels to react to intercepted exceptions.
   */
  open fun onCoroutineExceptionCaught(throwable: Throwable) {
    // Default: silent graceful interception
  }

  /**
   * Safely launches a coroutine under the global exception handler shield.
   */
  protected fun launchSafely(
    fallback: (suspend () -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit
  ) {
    viewModelScope.launch(globalCoroutineExceptionHandler) {
      try {
        block()
      } catch (t: Throwable) {
        Log.w(tag, "[AEGORA_SAFE_LAUNCH] Intercepted runtime exception in launchSafely: ${t.message}")
        _isDegradedState.value = true
        _lastErrorMessage.value = t.message
        fallback?.invoke()
      }
    }
  }

  /**
   * Executes a remote call with a strict timeout (default 1500ms).
   * If the remote call throws or times out, it gracefully falls back to [fallbackPayload]
   * ensuring zero UI freeze and instantaneous render.
   */
  suspend fun <T> executeWithGracefulDegradation(
    timeoutMs: Long = 1500L,
    fallbackPayload: () -> T,
    remoteCall: suspend () -> T
  ): T {
    return try {
      val result = withTimeoutOrNull(timeoutMs) {
        remoteCall()
      }
      if (result != null) {
        _isDegradedState.value = false
        result
      } else {
        Log.w(tag, "[AEGORA_DEGRADATION] Telemetry call timed out after ${timeoutMs}ms. Injecting high-fidelity fallback.")
        _isDegradedState.value = true
        fallbackPayload()
      }
    } catch (e: Exception) {
      Log.e(tag, "[AEGORA_DEGRADATION] Exception in remote telemetry call: ${e.message}. Injecting fallback.", e)
      _isDegradedState.value = true
      fallbackPayload()
    }
  }

  fun clearError() {
    _lastErrorMessage.value = null
  }
}
