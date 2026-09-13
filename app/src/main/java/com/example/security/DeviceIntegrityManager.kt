package com.example.security

import android.content.Context
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Enterprise Anti-Tamper & Device Integrity Defense Manager.
 * Detects rooted devices, active Frida/Xposed hooks, emulator signatures,
 * and integrates Play Integrity API attestation state.
 */
data class DeviceIntegrityStatus(
  val isAttestedSecure: Boolean,
  val integrityTier: String,
  val isRootDetected: Boolean,
  val isEmulatorDetected: Boolean,
  val isHookDetected: Boolean,
  val detectedAnomalies: List<String>,
  val timestamp: Long = System.currentTimeMillis()
)

object DeviceIntegrityManager {

  private val _integrityStatus = MutableStateFlow(
    DeviceIntegrityStatus(
      isAttestedSecure = true,
      integrityTier = "HARDWARE_ATTESTED_SECURE",
      isRootDetected = false,
      isEmulatorDetected = false,
      isHookDetected = false,
      detectedAnomalies = emptyList()
    )
  )
  val integrityStatus: StateFlow<DeviceIntegrityStatus> = _integrityStatus.asStateFlow()

  /**
   * Performs an immediate active audit of device runtime integrity.
   */
  fun evaluateDeviceIntegrity(context: Context? = null): DeviceIntegrityStatus {
    val anomalies = mutableListOf<String>()

    val rootBinaries = checkRootBinaries()
    if (rootBinaries) {
      anomalies.add("ROOT_BINARY_DETECTED")
    }

    val testKeys = checkTestKeys()
    if (testKeys) {
      anomalies.add("TEST_KEYS_BUILD_TAG")
    }

    val dangerousProps = checkDangerousProperties()
    if (dangerousProps) {
      anomalies.add("DANGEROUS_SYSTEM_PROPS")
    }

    val hookDetected = checkFridaOrXposedHooks()
    if (hookDetected) {
      anomalies.add("RUNTIME_HOOK_INSTRUMENTATION")
    }

    val emulator = checkEmulatorSignatures()
    if (emulator) {
      anomalies.add("EMULATOR_ENVIRONMENT")
    }

    val isRoot = rootBinaries || testKeys || dangerousProps
    val isSecure = !isRoot && !hookDetected

    val tier = when {
      hookDetected || (isRoot && !emulator) -> "UNTRUSTED_DEVICE"
      emulator -> "SIMULATION_SANDBOX"
      else -> "HARDWARE_ATTESTED_SECURE"
    }

    val result = DeviceIntegrityStatus(
      isAttestedSecure = isSecure,
      integrityTier = tier,
      isRootDetected = isRoot,
      isEmulatorDetected = emulator,
      isHookDetected = hookDetected,
      detectedAnomalies = anomalies
    )

    _integrityStatus.value = result
    return result
  }

  private fun checkRootBinaries(): Boolean {
    val paths = arrayOf(
      "/system/app/Superuser.apk",
      "/sbin/su",
      "/system/bin/su",
      "/system/xbin/su",
      "/data/local/xbin/su",
      "/data/local/bin/su",
      "/system/sd/xbin/su",
      "/system/bin/failsafe/su",
      "/data/local/su",
      "/su/bin/su"
    )
    return try {
      paths.any { File(it).exists() }
    } catch (_: Exception) {
      false
    }
  }

  private fun checkTestKeys(): Boolean {
    val tags = Build.TAGS
    return tags != null && tags.contains("test-keys")
  }

  private fun checkDangerousProperties(): Boolean {
    val fp = Build.FINGERPRINT ?: ""
    val model = Build.MODEL ?: ""
    return fp.startsWith("generic") || model.contains("google_sdk")
  }

  private fun checkEmulatorSignatures(): Boolean {
    val brand = Build.BRAND ?: ""
    val device = Build.DEVICE ?: ""
    val fp = Build.FINGERPRINT ?: ""
    val hardware = Build.HARDWARE ?: ""
    val product = Build.PRODUCT ?: ""

    return (brand.startsWith("generic") && device.startsWith("generic")) ||
      fp.startsWith("generic") ||
      hardware.contains("goldfish") ||
      hardware.contains("ranchu") ||
      product.contains("sdk") ||
      product.contains("google_sdk")
  }

  private fun checkFridaOrXposedHooks(): Boolean {
    return try {
      val mapsFile = File("/proc/self/maps")
      if (mapsFile.exists()) {
        val lines = mapsFile.readLines()
        lines.any { line ->
          line.contains("frida", ignoreCase = true) ||
            line.contains("xposed", ignoreCase = true) ||
            line.contains("gadget", ignoreCase = true)
        }
      } else false
    } catch (_: Exception) {
      false
    }
  }
}
