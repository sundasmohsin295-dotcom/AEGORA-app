package com.example.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Production provider active when external identity infrastructure (e.g. google-services.json)
 * is unavailable in the environment.
 * Honestly reports that authentication is blocked rather than simulating fake success.
 */
class BlockedAuthProvider(
  private val blockerReason: String = "External Identity Provider (Firebase) is not configured. Missing google-services.json."
) : AuthProvider {

  override val providerId: String = "FIREBASE_BLOCKED"
  override val isConfigured: Boolean = false

  private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
  override val authState: StateFlow<AuthState> = _authState.asStateFlow()

  override val currentIdentity: AuthenticatedIdentity?
    get() = null

  override suspend fun signInWithEmailPassword(email: String, pass: String): AuthResult {
    _authState.value = AuthState.AuthenticationFailed("Authentication blocked: $blockerReason")
    return AuthResult.Blocked(blockerReason)
  }

  override suspend fun signInWithFederatedToken(idToken: String): AuthResult {
    _authState.value = AuthState.AuthenticationFailed("Authentication blocked: $blockerReason")
    return AuthResult.Blocked(blockerReason)
  }

  override suspend fun signOut(): Boolean {
    _authState.value = AuthState.Unauthenticated
    return true
  }
}
