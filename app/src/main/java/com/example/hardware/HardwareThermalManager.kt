package com.example.hardware

import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * System thermal status levels for battery optimization and throttling.
 */
enum class DeviceThermalState {
  NORMAL,
  MODERATE,
  SEVERE,
  CRITICAL;

  val isThrottled: Boolean
    get() = this == SEVERE || this == CRITICAL

  val isDegraded: Boolean
    get() = this != NORMAL
}

/**
 * Enterprise hardware thermal state monitoring.
 * Leverages Android Q+ PowerManager.OnThermalStatusChangedListener
 * to dynamically tune frame animation rates, shader complexity, and canvas drops.
 */
class HardwareThermalManager private constructor(private val context: Context) {

  private val powerManager: PowerManager? =
    context.getSystemService(Context.POWER_SERVICE) as? PowerManager

  private val _thermalState = MutableStateFlow(resolveCurrentThermalState())
  val thermalState: StateFlow<DeviceThermalState> = _thermalState.asStateFlow()

  private var thermalListener: PowerManager.OnThermalStatusChangedListener? = null

  init {
    registerThermalListener()
  }

  private fun resolveCurrentThermalState(): DeviceThermalState {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null) {
      return when (powerManager.currentThermalStatus) {
        PowerManager.THERMAL_STATUS_NONE,
        PowerManager.THERMAL_STATUS_LIGHT -> DeviceThermalState.NORMAL
        PowerManager.THERMAL_STATUS_MODERATE -> DeviceThermalState.MODERATE
        PowerManager.THERMAL_STATUS_SEVERE -> DeviceThermalState.SEVERE
        PowerManager.THERMAL_STATUS_CRITICAL,
        PowerManager.THERMAL_STATUS_EMERGENCY,
        PowerManager.THERMAL_STATUS_SHUTDOWN -> DeviceThermalState.CRITICAL
        else -> DeviceThermalState.NORMAL
      }
    }
    return DeviceThermalState.NORMAL
  }

  private fun registerThermalListener() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && powerManager != null) {
      val listener = PowerManager.OnThermalStatusChangedListener { status ->
        val mappedState = when (status) {
          PowerManager.THERMAL_STATUS_NONE,
          PowerManager.THERMAL_STATUS_LIGHT -> DeviceThermalState.NORMAL
          PowerManager.THERMAL_STATUS_MODERATE -> DeviceThermalState.MODERATE
          PowerManager.THERMAL_STATUS_SEVERE -> DeviceThermalState.SEVERE
          PowerManager.THERMAL_STATUS_CRITICAL,
          PowerManager.THERMAL_STATUS_EMERGENCY,
          PowerManager.THERMAL_STATUS_SHUTDOWN -> DeviceThermalState.CRITICAL
          else -> DeviceThermalState.NORMAL
        }
        _thermalState.value = mappedState
      }
      thermalListener = listener
      try {
        powerManager.addThermalStatusListener(context.mainExecutor, listener)
      } catch (_: Throwable) {
        // Fallback gracefully on devices or emulators without hardware HAL support
      }
    }
  }

  fun updateSimulatedThermalState(state: DeviceThermalState) {
    _thermalState.value = state
  }

  companion object {
    @Volatile
    private var INSTANCE: HardwareThermalManager? = null

    fun getInstance(context: Context): HardwareThermalManager {
      return INSTANCE ?: synchronized(this) {
        INSTANCE ?: HardwareThermalManager(context.applicationContext).also { INSTANCE = it }
      }
    }
  }
}

/**
 * Composable helper to observe real-time hardware thermal states.
 */
@Composable
fun rememberDeviceThermalState(): State<DeviceThermalState> {
  val context = LocalContext.current
  val thermalManager = remember(context) { HardwareThermalManager.getInstance(context) }
  return thermalManager.thermalState.collectAsState()
}
