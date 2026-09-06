package com.example.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Provider-agnostic contract for authentication operations in AEGORA.
 * Allows future integration with Firebase Authentication or Android Credential Manager
 * without mutating domain or UI architectural boundaries.
 */
interface AuthProvider {
  val providerId: String
  val isConfigured: Boolean
  val authState: StateFlow<AuthState>
  val currentIdentity: AuthenticatedIdentity?

  suspend fun signInWithEmailPassword(email: String, pass: String): AuthResult
  suspend fun signInWithFederatedToken(idToken: String): AuthResult
  suspend fun signOut(): Boolean
}

sealed class AuthResult {
  data class Success(val identity: AuthenticatedIdentity) : AuthResult()
  data class Failure(val error: String) : AuthResult()
  data class Blocked(val reason: String) : AuthResult()
}
