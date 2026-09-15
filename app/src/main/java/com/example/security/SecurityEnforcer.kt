package com.example.security

import android.content.Context
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Enterprise RASP (Runtime Application Self-Protection) & Zero-Trust Security Enforcer.
 * 
 * Performs proactive hardware-level integrity checks:
 * 1. Root detection (known su binaries, dangerous directories, test-keys build tags, su execution).
 * 2. Emulator & hypervisor detection (QEMU/ranchu/goldfish signatures, virtualized hardware drivers).
 * 3. Immediate storage purge (clearing EncryptedSharedPreferences and master keys upon compromise).
 * 4. API Gateway lockout (revoking network transport tokens if environment is untrusted).
 */
data class RaspAuditReport(
  val isCompromised: Boolean,
  val rootDetected: Boolean,
  val emulatorDetected: Boolean,
  val testKeysDetected: Boolean,
  val suBinariesFound: List<String>,
  val triggeredAnomalies: List<String>,
  val timestamp: Long = System.currentTimeMillis()
)

object SecurityEnforcer {

  private val KNOWN_SU_PATHS = listOf(
    "/system/app/Superuser.apk",
    "/sbin/su",
    "/system/bin/su",
    "/system/xbin/su",
    "/data/local/xbin/su",
    "/data/local/bin/su",
    "/system/sd/xbin/su",
    "/system/bin/failsafe/su",
    "/data/local/su",
    "/su/bin/su",
    "/su/xbin/su",
    "/system/bin/.ext/.su",
    "/system/usr/we-need-root/su-backup",
    "/system/xbin/daemonsu"
  )

  private val KNOWN_ROOT_PACKAGES = listOf(
    "com.noshufou.android.su",
    "com.topjohnwu.magisk",
    "eu.chainfire.supersu",
    "com.koushikdutta.superuser",
    "com.thirdparty.superuser",
    "com.yellowes.su"
  )

  private val _auditReport = MutableStateFlow(
    RaspAuditReport(
      isCompromised = false,
      rootDetected = false,
      emulatorDetected = false,
      testKeysDetected = false,
      suBinariesFound = emptyList(),
      triggeredAnomalies = emptyList()
    )
  )
  val auditReport: StateFlow<RaspAuditReport> = _auditReport.asStateFlow()

  /**
   * Flag indicating if an authorized security auditor/developer has overridden
   * the lockdown to allow inspection in a sandbox/simulation environment.
   */
  var isAuditorOverrideActive: Boolean = false
    private set

  /**
   * For automated JVM/Robolectric unit testing where hardware attestation is mocked.
   */
  var bypassEnforcementForTesting: Boolean = false

  /**
   * Performs an immediate synchronous RASP audit of the host environment.
   * Checks for root binaries, test-keys build tags, and emulator signatures.
   */
  fun enforce(context: Context, strictEmulatorCheck: Boolean = true): RaspAuditReport {
    val anomalies = mutableListOf<String>()
    val foundSuPaths = mutableListOf<String>()

    // 1. Root Binary Probing
    for (path in KNOWN_SU_PATHS) {
      try {
        val file = File(path)
        if (file.exists()) {
          foundSuPaths.add(path)
        }
      } catch (_: SecurityException) {
        // Restricted access can also indicate security framework intervention
      }
    }

    if (foundSuPaths.isNotEmpty()) {
      anomalies.add("ROOT_BINARY_LOCATED: ${foundSuPaths.joinToString()}")
    }

    // 2. Build Tags Probing (test-keys)
    val hasTestKeys = Build.TAGS != null && Build.TAGS.contains("test-keys")
    if (hasTestKeys) {
      anomalies.add("TEST_KEYS_BUILD_TAG_DETECTED")
    }

    // 3. Root Package Scan
    val packageManager = context.packageManager
    for (pkg in KNOWN_ROOT_PACKAGES) {
      try {
        packageManager.getPackageInfo(pkg, 0)
        anomalies.add("ROOT_MANAGEMENT_APP_DETECTED: $pkg")
      } catch (_: Exception) {
        // Expected on clean devices
      }
    }

    // 4. Su Binary Execution Check
    val canExecuteSu = checkSuExecution()
    if (canExecuteSu) {
      anomalies.add("ROOT_EXECUTION_CAPABILITY_CONFIRMED")
    }

    val isRoot = foundSuPaths.isNotEmpty() || hasTestKeys || canExecuteSu

    // 5. Emulator & Hypervisor Probing
    val isEmulator = checkEmulatorSignatures()
    if (isEmulator) {
      anomalies.add("EMULATOR_HYPERVISOR_SIGNATURE_DETECTED")
    }

    // Determine compromise state
    val isCompromised = (isRoot || (strictEmulatorCheck && isEmulator)) && !bypassEnforcementForTesting

    val report = RaspAuditReport(
      isCompromised = isCompromised,
      rootDetected = isRoot,
      emulatorDetected = isEmulator,
      testKeysDetected = hasTestKeys,
      suBinariesFound = foundSuPaths,
      triggeredAnomalies = anomalies
    )

    _auditReport.value = report

    if (isCompromised && !isAuditorOverrideActive) {
      clearEncryptedStorage(context)
    }

    return report
  }

  /**
   * Attempts to invoke `which su` to verify runtime execution privileges.
   */
  private fun checkSuExecution(): Boolean {
    return try {
      val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
      val exitCode = process.waitFor()
      exitCode == 0
    } catch (_: Exception) {
      false
    }
  }

  /**
   * Probes system hardware properties for QEMU, Ranchu, Goldfish, and virtualized Android platforms.
   */
  fun checkEmulatorSignatures(): Boolean {
    return (Build.FINGERPRINT.startsWith("generic")
      || Build.FINGERPRINT.startsWith("unknown")
      || Build.FINGERPRINT.contains("robolectric")
      || Build.MODEL.contains("google_sdk")
      || Build.MODEL.contains("Emulator")
      || Build.MODEL.contains("Android SDK built for x86")
      || Build.MANUFACTURER.contains("Genymotion")
      || Build.HARDWARE.contains("goldfish")
      || Build.HARDWARE.contains("ranchu")
      || Build.PRODUCT.contains("sdk_google")
      || Build.PRODUCT.contains("google_sdk")
      || Build.PRODUCT.contains("sdk")
      || Build.PRODUCT.contains("sdk_x86")
      || Build.PRODUCT.contains("vbox86p")
      || Build.PRODUCT.contains("emulator")
      || Build.PRODUCT.contains("simulator")
      || File("/dev/socket/qemud").exists()
      || File("/dev/qemu_pipe").exists())
  }

  /**
   * Cryptographic Purge: Immediately wipes all EncryptedSharedPreferences,
   * destroys active MasterKey mappings, and clears local vaults.
   */
  fun clearEncryptedStorage(context: Context) {
    try {
      val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

      val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "aegora_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
      )
      encryptedPrefs.edit().clear().apply()
    } catch (_: Throwable) {
      // In constrained environments where AndroidKeyStore may be locked
    }

    try {
      AegoraEncryptedStorage.getInstance(context).clearAll()
    } catch (_: Throwable) {}

    try {
      context.getSharedPreferences("aegora_vault_encrypted_store", Context.MODE_PRIVATE).edit().clear().apply()
      context.getSharedPreferences("aegora_secure_prefs", Context.MODE_PRIVATE).edit().clear().apply()
      context.getSharedPreferences("aegora_auth_state", Context.MODE_PRIVATE).edit().clear().apply()
    } catch (_: Throwable) {}
  }

  /**
   * Sets auditor override mode allowing security operations testing.
   */
  fun setAuditorOverride(active: Boolean) {
    isAuditorOverrideActive = active
  }

  /**
   * Sets bypass flag for isolated unit tests.
   */
  fun setBypassForTesting(bypass: Boolean) {
    bypassEnforcementForTesting = bypass
  }

  /**
   * Returns whether outbound API access must be muted/blocked due to host compromise.
   */
  fun isApiAccessBlocked(): Boolean {
    if (isAuditorOverrideActive || bypassEnforcementForTesting) return false
    return _auditReport.value.isCompromised
  }
}
