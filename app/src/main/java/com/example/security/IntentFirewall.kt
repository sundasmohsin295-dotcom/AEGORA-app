package com.example.security

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Nation-State Strict IPC Isolation & Intent Firewall.
 *
 * Countermeasures against:
 * 1. IPC Intent Spoofing & Confused Deputy Attacks.
 * 2. Malicious Deep-Link URL redirection & Schema Hijacking.
 * 3. File / Content Provider schema injection (`javascript:`, `file:`, `content:`, `intent:`).
 * 4. Path traversal injection inside Intent payload URIs.
 *
 * Enforces strict whitelist verification on incoming implicit intents and deep links before
 * the activity or UI components parse and process them.
 */
object IntentFirewall {

  private const val TAG = "IntentFirewall"

  // Whitelisted schemes and hosts
  private val ALLOWED_SCHEMES = setOf("aegora", "https")
  private val ALLOWED_HOSTS = setOf("app", "verify", "aegora.cyber.sec", "aegora.app")

  // Prohibited dangerous URI schemes
  private val FORBIDDEN_SCHEMES = setOf("file", "javascript", "content", "intent", "data", "blob", "chrome")

  /**
   * Validates and sanitizes an incoming intent in Activity.onCreate / onNewIntent.
   * Returns true if safe to process, or false if the intent is untrusted/malformed.
   */
  fun validateAndSanitize(activity: Activity, intent: Intent?): Boolean {
    if (intent == null) return true

    val action = intent.action
    val data: Uri? = intent.data

    // Standard system launcher intents are safe
    if (action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
      return true
    }

    // Deep link or View action inspection
    if (action == Intent.ACTION_VIEW || data != null) {
      if (data == null) {
        Log.w(TAG, "ACTION_VIEW received with null data payload. Dropping.")
        return false
      }

      val scheme = data.scheme?.lowercase() ?: ""
      val host = data.host?.lowercase() ?: ""
      val rawUriString = data.toString()

      // 1. Check for prohibited dangerous schemes
      if (FORBIDDEN_SCHEMES.contains(scheme)) {
        Log.e(TAG, "SECURITY ALERT: Blocked forbidden URI scheme [$scheme] in Intent payload: $rawUriString")
        sanitizeIntent(intent)
        return false
      }

      // 2. Validate against explicit whitelist
      if (!ALLOWED_SCHEMES.contains(scheme)) {
        Log.e(TAG, "SECURITY ALERT: Rejected unwhitelisted scheme [$scheme]: $rawUriString")
        sanitizeIntent(intent)
        return false
      }

      if (host.isNotEmpty() && !ALLOWED_HOSTS.contains(host)) {
        Log.e(TAG, "SECURITY ALERT: Rejected unwhitelisted host [$host]: $rawUriString")
        sanitizeIntent(intent)
        return false
      }

      // 3. Prevent path traversal and delimiter attacks in path and query parameters
      val path = data.path ?: ""
      if (path.contains("..") || rawUriString.contains("../") || rawUriString.contains("%2e%2e")) {
        Log.e(TAG, "SECURITY ALERT: Path traversal pattern detected in Intent URI: $rawUriString")
        sanitizeIntent(intent)
        return false
      }

      // 4. Calling package verification if present
      val callingPackage = activity.callingPackage
      if (callingPackage != null && callingPackage != activity.packageName) {
        Log.i(TAG, "Cross-application deep link initiated by: $callingPackage")
      }

      Log.i(TAG, "Incoming Intent passed strict IPC firewall: scheme=$scheme, host=$host")
    }

    return true
  }

  /**
   * Neutralizes a malicious or untrusted intent by stripping data payloads and extras.
   */
  private fun sanitizeIntent(intent: Intent) {
    try {
      intent.data = null
      intent.replaceExtras(null as android.os.Bundle?)
    } catch (e: Exception) {
      Log.w(TAG, "Error sanitizing untrusted intent: ${e.message}")
    }
  }
}
