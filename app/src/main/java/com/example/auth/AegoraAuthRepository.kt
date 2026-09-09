package com.example.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Central authentication repository for AEGORA.
 * Enforces that:
 * 1. Default state is strictly [AuthState.Unauthenticated].
 * 2. UI cannot directly forge or mutate authenticated state.
 * 3. Identity and learner ownership originate exclusively from an [AuthProvider].
 */
object AegoraAuthRepository {

  private var activeProvider: AuthProvider = tryResolveDefaultProvider()

  private fun tryResolveDefaultProvider(): AuthProvider {
    return try {
      val realProvider = RealFirebaseAuthProvider()
      if (realProvider.isConfigured) {
        realProvider
      } else {
        BlockedAuthProvider()
      }
    } catch (_: Throwable) {
      BlockedAuthProvider()
    }
  }

  // Single authoritative state flow - strictly unauthenticated by default
  private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
  val authState: StateFlow<AuthState> = _authState.asStateFlow()

  val isAuthenticated: Boolean
    get() = _authState.value is AuthState.Authenticated

  val currentIdentity: AuthenticatedIdentity?
    get() = (_authState.value as? AuthState.Authenticated)?.identity

  val currentLearnerId: String?
    get() = currentIdentity?.mappedLearnerId

  fun initializeWithContext(context: android.content.Context) {
    if (!activeProvider.isConfigured) {
      val real = RealFirebaseAuthProvider(context.applicationContext)
      if (real.isConfigured) {
        activeProvider = real
        _authState.value = real.authState.value
      }
    }
  }

  fun getProviderStatus(): ProviderStatus {
    return if (activeProvider.isConfigured) {
      ProviderStatus.LIVE(activeProvider.providerId)
    } else {
      ProviderStatus.BLOCKED("Identity Provider not connected. Missing google-services.json / OAuth client configuration.")
    }
  }

  suspend fun signInWithEmailPassword(email: String, pass: String): AuthResult {
    _authState.value = AuthState.Authenticating(activeProvider.providerId)
    val result = activeProvider.signInWithEmailPassword(email, pass)
    when (result) {
      is AuthResult.Success -> {
        _authState.value = AuthState.Authenticated(result.identity)
        com.example.subscription.AegoraSubscriptionRepository.syncSubscriptionForUser(result.identity.providerUid)
      }
      is AuthResult.Failure -> {
        _authState.value = AuthState.AuthenticationFailed(result.error)
      }
      is AuthResult.Blocked -> {
        _authState.value = AuthState.AuthenticationFailed("Auth Blocked: ${result.reason}")
      }
    }
    return result
  }

  suspend fun signInWithFederatedToken(idToken: String): AuthResult {
    _authState.value = AuthState.Authenticating(activeProvider.providerId)
    val result = activeProvider.signInWithFederatedToken(idToken)
    when (result) {
      is AuthResult.Success -> {
        _authState.value = AuthState.Authenticated(result.identity)
        com.example.subscription.AegoraSubscriptionRepository.syncSubscriptionForUser(result.identity.providerUid)
      }
      is AuthResult.Failure -> {
        _authState.value = AuthState.AuthenticationFailed(result.error)
      }
      is AuthResult.Blocked -> {
        _authState.value = AuthState.AuthenticationFailed("Auth Blocked: ${result.reason}")
      }
    }
    return result
  }

  suspend fun signOut() {
    _authState.value = AuthState.SigningOut
    activeProvider.signOut()
    com.example.subscription.AegoraSubscriptionRepository.onUserSignedOut()
    _authState.value = AuthState.Unauthenticated
  }

  /**
   * Internal test hook to inject a mock/test provider for isolated unit verification.
   * NOT accessible to production UI code.
   */
  internal fun setProviderForTesting(provider: AuthProvider) {
    activeProvider = provider
    _authState.value = provider.authState.value
  }

  internal fun resetToDefault() {
    activeProvider = tryResolveDefaultProvider()
    _authState.value = activeProvider.authState.value
  }
}

sealed class ProviderStatus {
  data class LIVE(val providerName: String) : ProviderStatus()
  data class BLOCKED(val reason: String) : ProviderStatus()
  object ARCHITECTURAL : ProviderStatus()
  object DEMO_SIMULATED : ProviderStatus()
}
