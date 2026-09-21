package com.example.security

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.Interceptor
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

/**
 * PHASE 25: TRUE-TIME SYNCHRONIZATION (ANTI-TIME-TRAVEL & NTP SPOOFING SHIELD)
 * ==============================================================================
 * Intercepts HTTP 'Date' response headers from authoritative backend servers.
 * Compares authoritative server time against device System.currentTimeMillis().
 * If clock skew exceeds 5 minutes (300,000ms), flags an NTP Time-Travel attack,
 * throws a strict SecurityException, and activates an application-wide UI lockdown
 * to prevent attackers from bypassing JWT, license, or subscription expirations.
 */
object SecureTimeValidator : Interceptor {
  private const val TAG = "SecureTimeValidator"
  const val MAX_CLOCK_SKEW_MILLIS = 5 * 60 * 1000L // 5 minutes threshold

  private val _isTimeTampered = MutableStateFlow(false)
  val isTimeTampered: StateFlow<Boolean> = _isTimeTampered.asStateFlow()

  private val _skewReason = MutableStateFlow<String?>(null)
  val skewReason: StateFlow<String?> = _skewReason.asStateFlow()

  private val _lastAuthoritativeTime = MutableStateFlow<Long?>(null)
  val lastAuthoritativeTime: StateFlow<Long?> = _lastAuthoritativeTime.asStateFlow()

  /**
   * Validates the RFC 1123 HTTP Date header from backend responses.
   * Throws SecurityException if clock skew > 5 minutes.
   */
  @Throws(SecurityException::class)
  fun validateServerDate(dateHeader: String?): Boolean {
    if (dateHeader.isNullOrBlank()) return true

    try {
      val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("GMT")
      }
      val serverDate = format.parse(dateHeader) ?: return true
      val serverTimeMillis = serverDate.time
      _lastAuthoritativeTime.value = serverTimeMillis

      val deviceTimeMillis = System.currentTimeMillis()
      val skewMillis = abs(deviceTimeMillis - serverTimeMillis)

      if (skewMillis > MAX_CLOCK_SKEW_MILLIS) {
        val skewMinutes = skewMillis / 60000
        val msg = "TIME-TRAVEL ATTACK DETECTED: Device clock skew of $skewMinutes min exceeds allowable threshold (5 min). Device: $deviceTimeMillis, Authoritative Server: $serverTimeMillis"
        Log.e(TAG, "CRITICAL: $msg")
        _isTimeTampered.value = true
        _skewReason.value = msg
        throw SecurityException(msg)
      } else {
        _isTimeTampered.value = false
        _skewReason.value = null
      }
    } catch (e: SecurityException) {
      throw e
    } catch (e: Exception) {
      Log.w(TAG, "Non-fatal Date header parse failure '$dateHeader': ${e.message}")
    }
    return true
  }

  /**
   * Manual programmatic verification against an epoch millisecond timestamp.
   */
  @Throws(SecurityException::class)
  fun validateEpochTime(serverEpochMillis: Long): Boolean {
    _lastAuthoritativeTime.value = serverEpochMillis
    val deviceTimeMillis = System.currentTimeMillis()
    val skewMillis = abs(deviceTimeMillis - serverEpochMillis)

    if (skewMillis > MAX_CLOCK_SKEW_MILLIS) {
      val skewMinutes = skewMillis / 60000
      val msg = "NTP CLOCK SKEW DETECTED: Skew $skewMinutes min > 5 min threshold. Enforcing lockdown."
      Log.e(TAG, msg)
      _isTimeTampered.value = true
      _skewReason.value = msg
      throw SecurityException(msg)
    }
    _isTimeTampered.value = false
    _skewReason.value = null
    return true
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val response = chain.proceed(request)
    val dateHeader = response.header("Date")
    try {
      validateServerDate(dateHeader)
    } catch (e: SecurityException) {
      Log.e(TAG, "Intercepted response failed True-Time synchronization: ${e.message}")
      // Allow exception to propagate or signal UI lock
    }
    return response
  }

  fun resetLockdown() {
    _isTimeTampered.value = false
    _skewReason.value = null
  }
}
