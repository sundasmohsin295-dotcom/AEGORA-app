package com.example.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class BiometricAuthResult {
  object Success : BiometricAuthResult()
  data class Error(val errorCode: Int, val errString: CharSequence) : BiometricAuthResult()
  object Failed : BiometricAuthResult()
  object Unavailable : BiometricAuthResult()
}

object BiometricSecureEnclave {

  fun isBiometricAvailable(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    return when (biometricManager.canAuthenticate(authenticators)) {
      BiometricManager.BIOMETRIC_SUCCESS -> true
      else -> false
    }
  }

  suspend fun authenticate(
    activity: FragmentActivity,
    title: String = "Zero-Trust Biometric Attestation",
    subtitle: String = "Cryptographic Enclave Access Required",
    description: String = "Authenticate via Biometrics/Device PIN to unlock your Cryptographic Passport SHA-256 Ledger."
  ): BiometricAuthResult = suspendCancellableCoroutine { continuation ->
    val executor = ContextCompat.getMainExecutor(activity)

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle(title)
      .setSubtitle(subtitle)
      .setDescription(description)
      .setAllowedAuthenticators(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
        BiometricManager.Authenticators.BIOMETRIC_WEAK or
        BiometricManager.Authenticators.DEVICE_CREDENTIAL
      )
      .build()

    val biometricPrompt = BiometricPrompt(
      activity,
      executor,
      object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
          super.onAuthenticationSucceeded(result)
          if (continuation.isActive) {
            continuation.resume(BiometricAuthResult.Success)
          }
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
          super.onAuthenticationError(errorCode, errString)
          if (continuation.isActive) {
            continuation.resume(BiometricAuthResult.Error(errorCode, errString))
          }
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          // Notice: onAuthenticationFailed does not end the prompt, but if canceled by user, onAuthenticationError fires.
        }
      }
    )

    continuation.invokeOnCancellation {
      biometricPrompt.cancelAuthentication()
    }

    try {
      biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
      if (continuation.isActive) {
        continuation.resume(BiometricAuthResult.Unavailable)
      }
    }
  }

  /**
   * Performs constant-time cryptographic verification of authentication tokens
   * or security PINs using MessageDigest.isEqual to eliminate side-channel timing attacks.
   */
  fun verifyCredentialConstantTime(provided: String?, expected: String?): Boolean {
    return SecureMemory.constantTimeEquals(provided, expected)
  }

  /**
   * Performs constant-time cryptographic verification of token hashes or digest signatures.
   */
  fun verifyHashConstantTime(hashA: ByteArray?, hashB: ByteArray?): Boolean {
    return SecureMemory.constantTimeEquals(hashA, hashB)
  }
}
