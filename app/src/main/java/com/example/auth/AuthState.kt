package com.example.auth

/**
 * Authoritative states for the AEGORA authentication boundary.
 * Defaults strictly to [Unauthenticated] upon cold start.
 */
sealed class AuthState {
  object Unauthenticated : AuthState()
  data class Authenticating(val providerName: String) : AuthState()
  data class Authenticated(val identity: AuthenticatedIdentity) : AuthState()
  data class AuthenticationFailed(
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
  ) : AuthState()
  object SigningOut : AuthState()
  data class SessionExpired(val expiredAt: Long) : AuthState()
}

/**
 * Architectural foundation for future provider-backed sessions.
 * Note: Actual cryptographic token issuance is provider-dependent and
 * remains uninitialized while the external identity provider is blocked.
 */
data class SessionIdentity(
  val sessionId: String,
  val identity: AuthenticatedIdentity,
  val createdAt: Long,
  val expiresAt: Long,
  val state: SessionState
)

enum class SessionState {
  UNINITIALIZED,
  ACTIVE,
  EXPIRED,
  TERMINATED
}
