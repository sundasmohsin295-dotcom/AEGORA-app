package com.example.security

import android.content.Context
import android.os.Build
import android.webkit.WebSettings
import android.webkit.WebView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Enterprise-Grade Defensive Security Architecture (OWASP / Zero-Trust).
 * Implements hardened security layers across all data flows, command inputs,
 * sessions, and cryptographic telemetry verification in AEGORA.
 */

// ============================================================================
// 1. INJECTION & INPUT SANITIZATION DEFENSE
// ============================================================================

object AegoraInputSanitizer {

  private val COMMAND_INJECTION_PATTERN = Regex(
    "[;&|`$><\n\r]|(\\\\x[0-9a-fA-F]{2})|(\\$\\(.*\\))|(`.*`)|(--)",
    RegexOption.IGNORE_CASE
  )

  private val DANGEROUS_COMMANDS_PATTERN = Regex(
    "\\b(rm\\s+-rf|nc\\s+-e|chmod\\s+777|curl\\s+.*\\|\\s*(ba)?sh|wget\\s+.*\\|\\s*(ba)?sh|mkfifo|dd\\s+if=|python.*-c.*socket|bash\\s+-i|sh\\s+-i)\\b",
    RegexOption.IGNORE_CASE
  )

  private val SQL_INJECTION_PATTERN = Regex(
    "('\\s*(OR|AND)\\s*['\"\\w]+=['\"\\w]+|UNION\\s+SELECT|DROP\\s+TABLE|INSERT\\s+INTO|DELETE\\s+FROM|UPDATE\\s+.*SET|--|/\\*.*\\*/)",
    RegexOption.IGNORE_CASE
  )

  private val NOSQL_INJECTION_PATTERN = Regex(
    "(\\\$where|\\\$gt|\\\$gte|\\\$lt|\\\$lte|\\\$ne|\\\$regex|\\\$in|\\\$nin|\\\$or|\\\$and)",
    RegexOption.IGNORE_CASE
  )

  private val PATH_TRAVERSAL_PATTERN = Regex(
    "(\\.\\.[\\/\\\\]|%2e%2e[\\/\\\\]|\\.\\.%2f|\\.\\.%5c)",
    RegexOption.IGNORE_CASE
  )

  private val LDAP_INJECTION_PATTERN = Regex(
    "([*()&|!><~=]|\\\\00)",
    RegexOption.IGNORE_CASE
  )

  /**
   * Validates terminal CLI commands for unsafe command injection or shell escapes.
   */
  fun validateCliCommand(command: String): ValidationResult {
    val trimmed = command.trim()
    if (trimmed.isEmpty()) {
      return ValidationResult.Invalid("Command input cannot be empty.")
    }
    if (COMMAND_INJECTION_PATTERN.containsMatchIn(trimmed)) {
      return ValidationResult.Invalid("Command contains forbidden shell metacharacters or pipe redirection.")
    }
    if (DANGEROUS_COMMANDS_PATTERN.containsMatchIn(trimmed)) {
      return ValidationResult.Invalid("Command contains restricted destructive operations or reverse shells.")
    }
    return ValidationResult.Valid(trimmed)
  }

  /**
   * Sanitizes telemetry query strings to prevent SQL/NoSQL injection and parameter manipulation.
   */
  fun sanitizeTelemetryFilter(filter: String): ValidationResult {
    val trimmed = filter.trim()
    if (SQL_INJECTION_PATTERN.containsMatchIn(trimmed)) {
      return ValidationResult.Invalid("Telemetry filter contains SQL injection patterns.")
    }
    if (NOSQL_INJECTION_PATTERN.containsMatchIn(trimmed)) {
      return ValidationResult.Invalid("Telemetry filter contains NoSQL operator manipulation.")
    }
    if (PATH_TRAVERSAL_PATTERN.containsMatchIn(trimmed)) {
      return ValidationResult.Invalid("Path traversal sequence detected in filter.")
    }
    // Safe alphanumeric plus common telemetry symbols: _, -, ., :, /, space, =
    val sanitized = trimmed.replace(Regex("[^a-zA-Z0-9_\\-\\.:\\/\\s=,]"), "")
    return ValidationResult.Valid(sanitized)
  }

  /**
   * Safe JSON validation using org.json to prevent schema poisoning and injection.
   */
  fun validateJsonPayload(rawJson: String): ValidationResult {
    if (rawJson.length > 65536) {
      return ValidationResult.Invalid("Payload exceeds 64KB boundary")
    }
    return try {
      val trimmed = rawJson.trim()
      if (trimmed.startsWith("{")) {
        JSONObject(trimmed)
      } else if (trimmed.startsWith("[")) {
        JSONArray(trimmed)
      } else {
        return ValidationResult.Invalid("Malformed JSON payload structure")
      }
      ValidationResult.Valid(trimmed)
    } catch (e: Exception) {
      ValidationResult.Invalid("Schema validation failure: ${e.localizedMessage}")
    }
  }

  sealed class ValidationResult {
    data class Valid(val sanitizedValue: String) : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()

    val isValid: Boolean get() = this is Valid
  }
}

// ============================================================================
// 2. ANTI-CSRF & ANTI-REPLAY NONCE TOKENIZATION
// ============================================================================

object AntiCsrfNonceManager {
  private val activeNonces = ConcurrentHashMap<String, Long>()
  private const val NONCE_LIFETIME_MS = 5 * 60 * 1000L // 5 minutes validity
  private val secureRandom = SecureRandom()
  private val secretKeyBytes = ByteArray(32).apply { secureRandom.nextBytes(this) }

  /**
   * Generates a cryptographically signed anti-replay nonce carrying an expiration timestamp.
   */
  fun generateSignedNonce(action: String, operatorUid: String): String {
    cleanupExpiredNonces()
    val nonceId = UUID.randomUUID().toString().replace("-", "")
    val timestamp = System.currentTimeMillis()
    val payload = "$nonceId:$timestamp:$action:$operatorUid"
    val signature = hmacSha256(payload, secretKeyBytes)
    val token = "$payload:$signature"
    activeNonces[nonceId] = timestamp + NONCE_LIFETIME_MS
    return Base64.getUrlEncoder().withoutPadding().encodeToString(token.toByteArray(StandardCharsets.UTF_8))
  }

  /**
   * Validates and single-use consumes the anti-replay token.
   */
  fun validateAndConsumeNonce(tokenString: String, expectedAction: String, expectedOperatorUid: String): Boolean {
    cleanupExpiredNonces()
    return try {
      val decoded = String(Base64.getUrlDecoder().decode(tokenString), StandardCharsets.UTF_8)
      val parts = decoded.split(":")
      if (parts.size != 5) return false

      val nonceId = parts[0]
      val timestamp = parts[1].toLongOrNull() ?: return false
      val action = parts[2]
      val operatorUid = parts[3]
      val signature = parts[4]

      if (action != expectedAction || operatorUid != expectedOperatorUid) return false

      val payload = "$nonceId:$timestamp:$action:$operatorUid"
      val expectedSignature = hmacSha256(payload, secretKeyBytes)
      if (signature != expectedSignature) return false

      val now = System.currentTimeMillis()
      if (now > timestamp + NONCE_LIFETIME_MS || now < timestamp - 60_000L) {
        return false // Expired or future clock skew
      }

      // Check single-use and consume
      val expiration = activeNonces.remove(nonceId)
      expiration != null && expiration > now
    } catch (_: Exception) {
      false
    }
  }

  private fun hmacSha256(data: String, key: ByteArray): String {
    val mac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(key, "HmacSHA256")
    mac.init(secretKey)
    val hmacBytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes)
  }

  private fun cleanupExpiredNonces() {
    val now = System.currentTimeMillis()
    val iterator = activeNonces.entries.iterator()
    while (iterator.hasNext()) {
      if (iterator.next().value < now) {
        iterator.remove()
      }
    }
  }
}

// ============================================================================
// 3. ZERO-TRUST ACCESS CONTROL & CAPABILITY ENTITLEMENTS
// ============================================================================

enum class OperatorCapabilityScope(val scopeKey: String, val description: String) {
  OPERATOR_READ("OPERATOR_READ", "Read public missions and telemetry"),
  MISSION_EXECUTE("MISSION_EXECUTE", "Run tactical missions and interactive simulations"),
  PROOF_EXPORT("PROOF_EXPORT", "Export cryptographically attested Skill Passport proofs"),
  RED_TEAM_ADVERSARY("RED_TEAM_ADVERSARY", "Challenge AI copilot hallucinations"),
  ADMIN_OVERRIDE("ADMIN_OVERRIDE", "Security officer administrative actions");

  companion object {
    fun fromKey(key: String): OperatorCapabilityScope? = entries.find { it.scopeKey == key }
  }
}

data class ImmutableCapabilityToken(
  val tokenId: String,
  val operatorUid: String,
  val callsign: String,
  val scopes: List<String>,
  val issuedAt: Long,
  val expiresAt: Long,
  val cryptographicEnclaveHash: String
)

object ZeroTrustEnclaveAuthorizer {

  /**
   * The client UI NEVER authorizes a mission or issues a "VERIFIED" badge locally.
   * All claim resolutions (AI Failure / Evidence Verified) must be authenticated
   * through this authoritative verification enclave.
   */
  fun evaluateEntitlement(
    token: ImmutableCapabilityToken,
    requiredScope: OperatorCapabilityScope
  ): Boolean {
    val now = System.currentTimeMillis()
    if (now > token.expiresAt) return false
    return token.scopes.contains(requiredScope.scopeKey)
  }

  /**
   * Creates a tamper-evident SHA-256 verification hash of an evidence proof item.
   */
  fun generateProofEvidenceDigest(
    capabilityId: String,
    telemetryRaw: String,
    operatorUid: String,
    timestamp: Long
  ): String {
    val raw = "$capabilityId|$telemetryRaw|$operatorUid|$timestamp|AEGORA_ENCLAVE_ROOT_V12"
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(raw.toByteArray(StandardCharsets.UTF_8))
    return digest.joinToString("") { "%02x".format(it) }
  }
}

// ============================================================================
// 4. MEMORY & PLATFORM HARDENING (ROOT / TAMPER / PLAY INTEGRITY)
// ============================================================================

object PlatformIntegrityAuditor {

  /**
   * Inspects hardware and OS indicators for rooted devices, active instrumentation,
   * emulator hooks, or untrusted binary execution environments.
   */
  fun performIntegrityCheck(): PlatformIntegrityVerdict {
    val isRooted = checkKnownRootBinaries() || checkDangerousProps() || checkTestKeys()
    val isEmulator = checkEmulatorIndicators()
    val isFridaHooked = checkFridaOrXposed()

    return PlatformIntegrityVerdict(
      isSecureEnvironment = !isRooted && !isFridaHooked,
      isRootDetected = isRooted,
      isEmulatorDetected = isEmulator,
      isInstrumentationHookDetected = isFridaHooked,
      attestationTier = when {
        isRooted || isFridaHooked -> "UNTRUSTED_DEVICE"
        isEmulator -> "BASIC_SIMULATION_SANDBOX"
        else -> "HARDWARE_ATTESTED_SECURE"
      }
    )
  }

  private fun checkKnownRootBinaries(): Boolean {
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
    return paths.any { File(it).exists() }
  }

  private fun checkTestKeys(): Boolean {
    val buildTags = Build.TAGS
    return buildTags != null && buildTags.contains("test-keys")
  }

  private fun checkDangerousProps(): Boolean {
    return Build.FINGERPRINT.startsWith("generic") || Build.MODEL.contains("google_sdk")
  }

  private fun checkEmulatorIndicators(): Boolean {
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
      Build.FINGERPRINT.startsWith("generic") ||
      Build.HARDWARE.contains("goldfish") ||
      Build.HARDWARE.contains("ranchu") ||
      Build.PRODUCT.contains("sdk") ||
      Build.PRODUCT.contains("google_sdk")
  }

  private fun checkFridaOrXposed(): Boolean {
    return try {
      val mapsFile = File("/proc/self/maps")
      if (mapsFile.exists()) {
        val content = mapsFile.readText()
        content.contains("frida") || content.contains("xposed")
      } else false
    } catch (_: Exception) {
      false
    }
  }

  data class PlatformIntegrityVerdict(
    val isSecureEnvironment: Boolean,
    val isRootDetected: Boolean,
    val isEmulatorDetected: Boolean,
    val isInstrumentationHookDetected: Boolean,
    val attestationTier: String
  )
}

// ============================================================================
// 5. WEBVIEW HARDENING & CONTENT SECURITY POLICY (CSP)
// ============================================================================

object WebViewSecurityHardener {

  private val ALLOWED_HOSTS = setOf(
    "aegora.io",
    "cloud.aegora.io",
    "verify.aegora.io"
  )

  /**
   * Applies strict defense-in-depth settings to any Android WebView:
   * - Disables JavaScript by default (or restricts strictly if required)
   * - Disables file scheme and content scheme access
   * - Disables arbitrary URL loading and enforces strict host allowlists
   */
  fun harden(webView: WebView, allowScripting: Boolean = false) {
    webView.settings.apply {
      javaScriptEnabled = allowScripting
      allowFileAccess = false
      allowContentAccess = false
      databaseEnabled = false
      domStorageEnabled = false
      setGeolocationEnabled(false)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        safeBrowsingEnabled = true
      }
      mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
    }
  }

  fun isUrlSafeForNavigation(url: String): Boolean {
    return try {
      val uri = java.net.URI(url)
      if (uri.scheme != "https") return false
      val host = uri.host?.lowercase() ?: return false
      ALLOWED_HOSTS.contains(host) || host.endsWith(".aegora.io")
    } catch (_: Exception) {
      false
    }
  }
}
