package com.example.security

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * PHASE 25: ACCESSIBILITY ABUSE SHIELD (DATA MASKING & ANTI-SCREEN-SCRAPING)
 * ==============================================================================
 * Detects untrusted Accessibility Services running on the device.
 * Malicious accessibility malware frequently intercepts on-screen text,
 * node hierarchies, and credentials.
 *
 * If an active service is detected that is NOT an approved system screen reader
 * (e.g. Google TalkBack, Samsung Voice Assistant), dynamically engages MaskingState:
 * all IP addresses, CVEs, and Threat Scores are immediately replaced with
 * "[REDACTED - ACCESSIBILITY OVERRIDE]".
 */
object AccessibilityShield {
  private const val TAG = "AccessibilityShield"

  // Whitelisted official system screen readers
  private val WHITELISTED_SERVICES = setOf(
    "com.google.android.marvin.talkback",
    "com.google.android.apps.accessibility.voiceaccess",
    "com.samsung.android.accessibility.talkback",
    "com.android.talkback"
  )

  private val _isMaskingActive = MutableStateFlow(false)
  val isMaskingActive: StateFlow<Boolean> = _isMaskingActive.asStateFlow()

  private val _detectedUntrustedService = MutableStateFlow<String?>(null)
  val detectedUntrustedService: StateFlow<String?> = _detectedUntrustedService.asStateFlow()

  private val _isSimulationActive = MutableStateFlow(false)
  val isSimulationActive: StateFlow<Boolean> = _isSimulationActive.asStateFlow()

  /**
   * Evaluates active accessibility services against the trusted whitelist.
   */
  fun evaluateAccessibility(context: Context): Boolean {
    if (_isSimulationActive.value) {
      _isMaskingActive.value = true
      _detectedUntrustedService.value = "com.malicious.screenscraper.overlay [SIMULATED]"
      return true
    }

    try {
      val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
      if (manager == null || !manager.isEnabled) {
        _isMaskingActive.value = false
        _detectedUntrustedService.value = null
        return false
      }

      val enabledServices = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
      for (service in enabledServices) {
        val packageName = service.resolveInfo?.serviceInfo?.packageName ?: service.id.split("/").firstOrNull() ?: ""
        val isWhitelisted = WHITELISTED_SERVICES.any { packageName.startsWith(it, ignoreCase = true) }
        
        if (!isWhitelisted && packageName.isNotBlank()) {
          Log.w(TAG, "UNTRUSTED ACCESSIBILITY SERVICE DETECTED: $packageName. Engaging data redaction.")
          _isMaskingActive.value = true
          _detectedUntrustedService.value = packageName
          return true
        }
      }

      _isMaskingActive.value = false
      _detectedUntrustedService.value = null
      return false
    } catch (e: Exception) {
      Log.e(TAG, "Error evaluating accessibility services: ${e.message}")
      return false
    }
  }

  /**
   * Registers dynamic accessibility state changes to respond instantly when malware starts.
   */
  fun registerListener(context: Context) {
    try {
      val manager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
      manager?.addAccessibilityStateChangeListener {
        evaluateAccessibility(context)
      }
      evaluateAccessibility(context)
    } catch (e: Exception) {
      Log.w(TAG, "Accessibility listener registration warning: ${e.message}")
    }
  }

  /**
   * Masks sensitive information if untrusted accessibility services are active.
   */
  fun maskSensitive(value: String, isMasked: Boolean = _isMaskingActive.value): String {
    return if (isMasked) "[REDACTED - ACCESSIBILITY OVERRIDE]" else value
  }

  /**
   * Allows security evaluators to toggle simulation of untrusted accessibility malware.
   */
  fun toggleSimulation(context: Context? = null) {
    _isSimulationActive.value = !_isSimulationActive.value
    if (context != null) {
      evaluateAccessibility(context)
    } else {
      _isMaskingActive.value = _isSimulationActive.value
      _detectedUntrustedService.value = if (_isSimulationActive.value) "com.malware.keylogger.service" else null
    }
  }
}
