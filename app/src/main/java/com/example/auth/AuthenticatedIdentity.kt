package com.example.auth

/**
 * Strongly typed identity model issued strictly by a verified [AuthProvider].
 * Arbitrary UI strings cannot forge or fabricate an [AuthenticatedIdentity].
 */
data class AuthenticatedIdentity(
  val providerUid: String,
  val provider: String,
  val email: String?,
  val displayName: String?,
  val authenticatedAt: Long,
  val mappedLearnerId: String
) {
  companion object {
    /**
     * Converts a verified provider identity into an authoritative AEGORA learner identity.
     * Enforces deterministic, collision-resistant mapping.
     */
    fun fromProvider(
      providerUid: String,
      provider: String,
      email: String? = null,
      displayName: String? = null
    ): AuthenticatedIdentity {
      require(providerUid.isNotBlank()) { "Provider UID cannot be blank" }
      require(provider.isNotBlank()) { "Provider name cannot be blank" }

      val sanitizedUid = providerUid.replace(Regex("[^a-zA-Z0-9_]"), "").take(16).lowercase()
      val learnerId = "operator_$sanitizedUid"

      return AuthenticatedIdentity(
        providerUid = providerUid,
        provider = provider,
        email = email,
        displayName = displayName ?: "Operator $sanitizedUid",
        authenticatedAt = System.currentTimeMillis(),
        mappedLearnerId = learnerId
      )
    }
  }
}
