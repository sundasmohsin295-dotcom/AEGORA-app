package com.example.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.security.SecureMemory
import java.io.PrintWriter
import java.io.StringWriter
import java.util.regex.Pattern

/**
 * Enterprise Diagnostic Report capturing uncaught crashes.
 */
data class SystemCrashReport(
  val timestamp: Long = System.currentTimeMillis(),
  val threadName: String,
  val exceptionClass: String,
  val message: String,
  val stackTrace: String,
  val isFatal: Boolean = true
)

/**
 * Global Anti-Crash Architecture & Zero-Crash Exception Handler.
 * Intercepts uncaught exceptions on any thread, prevents raw system "App Not Responding"
 * or "App has Stopped" crash dialogues, records the diagnostic telemetry, and transitions
 * the UI to a resilient dark-themed SystemRecoveryScreen.
 *
 * PHASE 21 BLACK-OPS TOMBSTONE SANITIZATION (CRASH BURNER):
 * Synchronously zero-wipes all active RAM buffers (tokens, passphrases, symmetric keys)
 * before processing crash reports. Scrubs stack traces to prevent credentials from
 * leaking into system /data/tombstones or Android dropbox logs.
 */
object GlobalExceptionHandler : Thread.UncaughtExceptionHandler {

  private const val TAG = "GlobalCrashHandler"

  // Regex patterns to scrub sensitive secrets from crash dumps / tombstones
  private val SECRET_PATTERNS = listOf(
    Pattern.compile("""(?i)bearer\s+[A-Za-z0-9._~+/-]+=*"""),
    Pattern.compile("""(?i)(password|passphrase|token|secret|apiKey|authorization)["':\s=]+([^"\s,;]+)"""),
    Pattern.compile("""(?i)(BEGIN\s+PRIVATE\s+KEY|BEGIN\s+RSA\s+PRIVATE\s+KEY|AEGORA_CIPHER)"""),
    Pattern.compile("""ey[A-Za-z0-9-_]+\.ey[A-Za-z0-9-_]+\.[A-Za-z0-9-_]+""") // JWT pattern
  )

  private var defaultHandler: Thread.UncaughtExceptionHandler? = null
  private var isInitialized = false

  private val _lastCrashReport = MutableStateFlow<SystemCrashReport?>(null)
  val lastCrashReport: StateFlow<SystemCrashReport?> = _lastCrashReport.asStateFlow()

  private var onCrashListener: ((SystemCrashReport) -> Unit)? = null

  /**
   * Initializes the Global Exception Handler at the process root (Application / Activity startup).
   */
  fun initialize(context: Context, onCrash: ((SystemCrashReport) -> Unit)? = null) {
    if (isInitialized) return
    isInitialized = true
    this.onCrashListener = onCrash

    defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler(this)
    Log.i(TAG, "AEGORA Zero-Crash GlobalExceptionHandler armed and active with Tombstone Sanitizer.")
  }

  override fun uncaughtException(thread: Thread, throwable: Throwable) {
    // 1. BLACK-OPS CRASH BURNER: Synchronously zero-out all registered sensitive RAM buffers
    try {
      SecureMemory.wipeAllActiveBuffers()
      Log.i(TAG, "Tombstone Sanitizer: Actively wiped all sensitive RAM key buffers.")
    } catch (e: Throwable) {
      Log.e(TAG, "Error during emergency RAM wipe: ${e.message}")
    }

    // 2. Capture and sanitize stack trace
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    throwable.printStackTrace(pw)
    val rawStackTrace = sw.toString()
    val sanitizedStackTrace = sanitizeForTombstone(rawStackTrace)
    val rawMessage = throwable.localizedMessage ?: throwable.message ?: "Unspecified runtime exception"
    val sanitizedMessage = sanitizeForTombstone(rawMessage)

    val report = SystemCrashReport(
      timestamp = System.currentTimeMillis(),
      threadName = thread.name,
      exceptionClass = throwable.javaClass.simpleName.ifBlank { "UnknownException" },
      message = sanitizedMessage,
      stackTrace = sanitizedStackTrace
    )

    Log.e(TAG, "CRITICAL: Caught unhandled exception on [${thread.name}]: ${report.exceptionClass} - ${report.message}")

    _lastCrashReport.value = report

    // Notify registered UI state or listener on main thread
    Handler(Looper.getMainLooper()).post {
      try {
        onCrashListener?.invoke(report)
      } catch (e: Throwable) {
        Log.e(TAG, "Error invoking onCrashListener: ${e.message}")
      }
    }
  }

  /**
   * Scrubs any leaked credentials, bearer tokens, or sensitive keys from tombstone logs.
   */
  private fun sanitizeForTombstone(input: String): String {
    var sanitized = input
    for (pattern in SECRET_PATTERNS) {
      val matcher = pattern.matcher(sanitized)
      sanitized = matcher.replaceAll("[REDACTED_BY_TOMBSTONE_BURNER]")
    }
    return sanitized
  }

  /**
   * Clears the active crash state after system reboot/recovery.
   */
  fun clearCrashState() {
    _lastCrashReport.value = null
  }
}
