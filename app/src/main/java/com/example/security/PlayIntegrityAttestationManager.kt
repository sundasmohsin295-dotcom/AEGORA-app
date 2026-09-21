package com.example.security

import android.content.Context
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

/**
 * Google Play Integrity API Manager for Nation-State Cryptographic Device & Binary Attestation.
 *
 * Provides cryptographic attestation tokens proving:
 * 1. UNTAMPERED_BINARY: App has genuine cryptographic signature matching official release.
 * 2. RECOGNIZED_DEVICE: Hardware TEE / StrongBox passes device integrity evaluation (non-emulated, non-rooted).
 * 3. PLAY_RECOGNIZED: App was genuinely provisioned through Google Play distribution.
 */
object PlayIntegrityAttestationManager {

  private const val TAG = "PlayIntegrity"
  private const val GOOGLE_CLOUD_PROJECT_NUMBER = 697777356497L

  /**
   * Requests a signed Google Play Integrity token bound to a cryptographically secure nonce.
   */
  suspend fun requestIntegrityToken(
    context: Context,
    requestHashPayload: String = UUID.randomUUID().toString()
  ): PlayIntegrityResult = withContext(Dispatchers.IO) {
    try {
      val integrityManager = IntegrityManagerFactory.create(context.applicationContext)

      // Bind the request to a SHA-256 digest of the client nonce / request payload
      val sha256Digest = MessageDigest.getInstance("SHA-256")
      val nonceBytes = sha256Digest.digest(requestHashPayload.toByteArray(Charsets.UTF_8))
      val nonceBase64 = android.util.Base64.encodeToString(
        nonceBytes,
        android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
      )

      val request = IntegrityTokenRequest.builder()
        .setCloudProjectNumber(GOOGLE_CLOUD_PROJECT_NUMBER)
        .setNonce(nonceBase64)
        .build()

      val response: IntegrityTokenResponse = integrityManager.requestIntegrityToken(request).await()
      val token = response.token()

      Log.i(TAG, "Attestation token generated successfully (len: ${token.length})")
      PlayIntegrityResult.Success(
        token = token,
        nonce = nonceBase64
      )
    } catch (e: Exception) {
      Log.w(TAG, "Play Integrity attestation evaluation note: ${e.message}")
      // In development / testing emulator environments without Play Store service connection,
      // provide a structured local evaluation result for seamless fallback
      PlayIntegrityResult.SimulationFallback(
        syntheticToken = "INTEGRITY_ATTESTATION_TOKEN_${UUID.randomUUID()}",
        reason = e.message ?: "Development enclave environment"
      )
    }
  }
}

sealed class PlayIntegrityResult {
  data class Success(val token: String, val nonce: String) : PlayIntegrityResult()
  data class SimulationFallback(val syntheticToken: String, val reason: String) : PlayIntegrityResult()
  data class Error(val error: String) : PlayIntegrityResult()
}
