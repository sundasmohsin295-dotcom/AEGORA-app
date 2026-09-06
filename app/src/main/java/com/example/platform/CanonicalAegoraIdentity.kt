package com.example.platform

import com.example.auth.AuthenticatedIdentity

/**
 * Enumeration of supported client applications accessing the AEGORA platform.
 */
enum class PlatformClient {
  ANDROID,
  WEB
}

/**
 * Authoritative canonical user identity across the AEGORA platform.
 * Both Android and Web clients authenticate through an Identity Provider (e.g., Firebase Auth)
 * and resolve to the EXACT SAME [CanonicalAegoraIdentity].
 *
 * Enforces the architectural invariant:
 * Provider UID -> Canonical User ID -> Canonical Learner ID
 */
data class CanonicalAegoraIdentity(
  val canonicalUserId: String,
  val canonicalLearnerId: String,
  val providerUid: String,
  val provider: String,
  val email: String?,
  val displayName: String?,
  val clientType: PlatformClient,
  val authenticatedAt: Long
) {
  companion object {
    /**
     * Deterministic, platform-wide identity derivation algorithm.
     * Guaranteed to produce identical Canonical User ID and Learner ID across Android and Web.
     */
    fun resolve(
      providerUid: String,
      provider: String,
      email: String? = null,
      displayName: String? = null,
      clientType: PlatformClient = PlatformClient.ANDROID
    ): CanonicalAegoraIdentity {
      require(providerUid.isNotBlank()) { "Provider UID cannot be blank" }
      require(provider.isNotBlank()) { "Provider name cannot be blank" }

      val sanitizedUid = providerUid.replace(Regex("[^a-zA-Z0-9_]"), "").take(16).lowercase()
      val userId = "usr_$sanitizedUid"
      val learnerId = "operator_$sanitizedUid"

      return CanonicalAegoraIdentity(
        canonicalUserId = userId,
        canonicalLearnerId = learnerId,
        providerUid = providerUid,
        provider = provider,
        email = email,
        displayName = displayName ?: "Operator $sanitizedUid",
        clientType = clientType,
        authenticatedAt = System.currentTimeMillis()
      )
    }

    /**
     * Bridges an existing [AuthenticatedIdentity] into a [CanonicalAegoraIdentity].
     */
    fun fromAuthenticatedIdentity(
      identity: AuthenticatedIdentity,
      clientType: PlatformClient = PlatformClient.ANDROID
    ): CanonicalAegoraIdentity {
      return resolve(
        providerUid = identity.providerUid,
        provider = identity.provider,
        email = identity.email,
        displayName = identity.displayName,
        clientType = clientType
      )
    }
  }
}
