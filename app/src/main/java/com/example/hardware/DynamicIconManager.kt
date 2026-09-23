package com.example.hardware

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Enterprise Dynamic App Icon Switcher.
 * Uses PackageManager.setComponentEnabledSetting with DONT_KILL_APP to dynamically toggle
 * between the default Obsidian/Cyan icon and a high-urgency Crimson Alert icon
 * when high-severity threat vulnerabilities or active exploits are detected.
 */
object DynamicIconManager {
  private const val TAG = "DynamicIconManager"

  const val ALIAS_DEFAULT = "com.example.MainActivityDefault"
  const val ALIAS_ALERT = "com.example.MainActivityAlert"

  private val _isAlertIconActive = MutableStateFlow(false)
  val isAlertIconActive: StateFlow<Boolean> = _isAlertIconActive.asStateFlow()

  /**
   * Switches the launcher icon to the Red "Threat Active" alert state.
   */
  fun setAlertIcon(context: Context) {
    applyIconState(context, isAlert = true)
  }

  /**
   * Reverts the launcher icon to standard Obsidian/Cyan default state.
   */
  fun setDefaultIcon(context: Context) {
    applyIconState(context, isAlert = false)
  }

  /**
   * Invoked when a critical MITRE ATT&CK threat or zero-day is successfully mitigated.
   * Restores the default system telemetry icon.
   */
  fun onMitreThreatMitigated(context: Context) {
    setDefaultIcon(context)
  }

  /**
   * Toggles the dynamic launcher icon based on current threat score or active vulnerability state.
   */
  fun updateIconForThreatScore(context: Context, threatScore: Int) {
    if (threatScore >= 75) {
      setAlertIcon(context)
    } else {
      setDefaultIcon(context)
    }
  }

  private fun applyIconState(context: Context, isAlert: Boolean) {
    try {
      val pm = context.packageManager
      val pkgName = context.packageName

      val defaultComponent = ComponentName(pkgName, "$pkgName.MainActivityDefault")
      val alertComponent = ComponentName(pkgName, "$pkgName.MainActivityAlert")

      val targetEnable = if (isAlert) alertComponent else defaultComponent
      val targetDisable = if (isAlert) defaultComponent else alertComponent

      pm.setComponentEnabledSetting(
        targetEnable,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP
      )
      pm.setComponentEnabledSetting(
        targetDisable,
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
        PackageManager.DONT_KILL_APP
      )
      _isAlertIconActive.value = isAlert
      Log.d(TAG, "Dynamic icon state updated: alertActive=$isAlert")
    } catch (e: Exception) {
      Log.w(TAG, "Failed to apply dynamic icon state: ${e.message}")
    }
  }
}
