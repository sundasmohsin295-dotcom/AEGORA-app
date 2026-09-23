package com.example.chaos

import android.util.Log
import com.example.core.result.AegoraResult
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Chaos Monkey Fault Injector for Aegora Cyber Resilience.
 * Dynamically introduces simulated failures: network drops, 500 server errors,
 * CPU throttling, and thread starvation into operations to verify circuit breakers.
 */
object ChaosInjector {
  private const val TAG = "ChaosInjector"

  enum class FaultMode {
    NONE,
    NETWORK_DROP,
    HTTP_500_SERVER_ERROR,
    CPU_THROTTLING,
    THREAD_STARVATION
  }

  @Volatile
  var activeFaultMode: FaultMode = FaultMode.NONE

  @Volatile
  var failureRate: Double = 0.0 // 0.0 to 1.0

  fun enableFault(mode: FaultMode, rate: Double = 1.0) {
    activeFaultMode = mode
    failureRate = rate
    Log.w(TAG, "CHAOS FAULT ENABLED: mode=$mode, rate=$rate")
  }

  fun disable() {
    activeFaultMode = FaultMode.NONE
    failureRate = 0.0
    Log.i(TAG, "Chaos injection disengaged.")
  }

  /**
   * Executes a block under active chaos injection conditions.
   * If a chaos fault triggers, it intercepts with an AegoraResult.Failure instead of crashing.
   */
  suspend fun <T> executeWithChaos(
    operationName: String,
    block: suspend () -> T
  ): AegoraResult<T> {
    if (activeFaultMode != FaultMode.NONE && Math.random() < failureRate) {
      when (activeFaultMode) {
        FaultMode.NETWORK_DROP -> {
          Log.w(TAG, "Chaos: Simulating sudden socket drop in $operationName")
          return AegoraResult.Failure(
            code = "ERR_CHAOS_NET_DROP",
            message = "Simulated TCP socket termination during $operationName",
            cause = IOException("Connection reset by peer"),
            isRecoverable = true
          )
        }
        FaultMode.HTTP_500_SERVER_ERROR -> {
          Log.w(TAG, "Chaos: Simulating HTTP 500 Internal Error in $operationName")
          return AegoraResult.Failure(
            code = "ERR_CHAOS_HTTP_500",
            message = "Gateway encountered 500 Internal Server Error during $operationName",
            isRecoverable = true
          )
        }
        FaultMode.CPU_THROTTLING -> {
          Log.w(TAG, "Chaos: Simulating 800ms CPU throttling delay in $operationName")
          delay(800)
        }
        FaultMode.THREAD_STARVATION -> {
          Log.w(TAG, "Chaos: Simulating thread starvation in $operationName")
          delay(1200)
          return AegoraResult.Failure(
            code = "ERR_CHAOS_THREAD_TIMEOUT",
            message = "Worker thread pool starvation simulated during $operationName",
            isRecoverable = true
          )
        }
        FaultMode.NONE -> {}
      }
    }

    return try {
      AegoraResult.Success(block())
    } catch (t: Throwable) {
      AegoraResult.Failure(
        code = "ERR_CHAOS_UNCAUGHT",
        message = t.message ?: "Chaos trapped unhandled exception",
        cause = t,
        isRecoverable = true
      )
    }
  }
}
