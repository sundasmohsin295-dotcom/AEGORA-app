package com.example.auth

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

/**
 * Real Firebase Authentication Provider for AEGORA Android.
 * Integrates directly with the official Firebase Auth SDK.
 *
 * Requirements:
 * 1. Operates on real Firebase identity if FirebaseApp is initialized with app/google-services.json.
 * 2. Defaults strictly to [AuthState.Unauthenticated] if no Firebase user exists.
 * 3. Maps Firebase UID deterministically to [AuthenticatedIdentity].
 * 4. Honestly reports [AuthResult.Blocked] when Firebase infrastructure is not configured.
 */
class RealFirebaseAuthProvider(
  private val context: Context? = null
) : AuthProvider {

  override val providerId: String = "FIREBASE"

  private val firebaseAuth: FirebaseAuth? by lazy {
    try {
      val app: FirebaseApp? = try {
        if (context != null) {
          if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
          } else {
            FirebaseApp.getInstance()
          }
        } else {
          FirebaseApp.getInstance()
        }
      } catch (_: Exception) {
        null
      }

      if (app != null) {
        FirebaseAuth.getInstance(app)
      } else {
        null
      }
    } catch (_: Throwable) {
      null
    }
  }

  override val isConfigured: Boolean
    get() = firebaseAuth != null

  private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
  override val authState: StateFlow<AuthState> = _authState.asStateFlow()

  override val currentIdentity: AuthenticatedIdentity?
    get() = (_authState.value as? AuthState.Authenticated)?.identity

  init {
    attachListener()
  }

  private fun attachListener() {
    val auth = firebaseAuth ?: return
    try {
      auth.addAuthStateListener { fa ->
        val user = fa.currentUser
        if (user != null) {
          val identity = mapFirebaseUser(user)
          _authState.value = AuthState.Authenticated(identity)
        } else {
          _authState.value = AuthState.Unauthenticated
        }
      }
    } catch (_: Exception) {
      _authState.value = AuthState.Unauthenticated
    }
  }

  private fun mapFirebaseUser(user: FirebaseUser): AuthenticatedIdentity {
    return AuthenticatedIdentity.fromProvider(
      providerUid = user.uid,
      provider = "FIREBASE",
      email = user.email,
      displayName = user.displayName
    )
  }

  override suspend fun signInWithEmailPassword(email: String, pass: String): AuthResult {
    val auth = firebaseAuth
    if (auth == null) {
      val blockerReason = "External Identity Provider (Firebase) is not configured. Missing app/google-services.json."
      _authState.value = AuthState.AuthenticationFailed(blockerReason)
      return AuthResult.Blocked(blockerReason)
    }

    _authState.value = AuthState.Authenticating(providerId)
    return try {
      val authResult = auth.signInWithEmailAndPassword(email, pass).awaitTask()
      val user = authResult.user
      if (user != null) {
        val identity = mapFirebaseUser(user)
        _authState.value = AuthState.Authenticated(identity)
        AuthResult.Success(identity)
      } else {
        val err = "Authentication succeeded but Firebase user payload is null"
        _authState.value = AuthState.AuthenticationFailed(err)
        AuthResult.Failure(err)
      }
    } catch (e: Exception) {
      val err = e.localizedMessage ?: "Firebase authentication failed"
      _authState.value = AuthState.AuthenticationFailed(err)
      AuthResult.Failure(err)
    }
  }

  override suspend fun signUpWithEmailPassword(email: String, pass: String): AuthResult {
    val auth = firebaseAuth
    if (auth == null) {
      val blockerReason = "External Identity Provider (Firebase) is not configured. Missing app/google-services.json."
      _authState.value = AuthState.AuthenticationFailed(blockerReason)
      return AuthResult.Blocked(blockerReason)
    }

    _authState.value = AuthState.Authenticating(providerId)
    return try {
      val authResult = auth.createUserWithEmailAndPassword(email, pass).awaitTask()
      val user = authResult.user
      if (user != null) {
        val identity = mapFirebaseUser(user)
        _authState.value = AuthState.Authenticated(identity)
        AuthResult.Success(identity)
      } else {
        val err = "Registration succeeded but Firebase user payload is null"
        _authState.value = AuthState.AuthenticationFailed(err)
        AuthResult.Failure(err)
      }
    } catch (e: Exception) {
      val err = e.localizedMessage ?: "Firebase registration failed"
      _authState.value = AuthState.AuthenticationFailed(err)
      AuthResult.Failure(err)
    }
  }

  override suspend fun signInWithFederatedToken(idToken: String): AuthResult {
    val auth = firebaseAuth
    if (auth == null) {
      val blockerReason = "External Identity Provider (Firebase) is not configured. Missing app/google-services.json."
      _authState.value = AuthState.AuthenticationFailed(blockerReason)
      return AuthResult.Blocked(blockerReason)
    }
    return AuthResult.Failure("Federated token provider requires Google credential token configuration")
  }

  override suspend fun signOut(): Boolean {
    val auth = firebaseAuth
    if (auth != null) {
      try {
        auth.signOut()
      } catch (_: Exception) {}
    }
    _authState.value = AuthState.Unauthenticated
    return true
  }

  private suspend fun <T> Task<T>.awaitTask(): T =
    suspendCancellableCoroutine { cont ->
      addOnCompleteListener { task ->
        if (task.isSuccessful) {
          @Suppress("UNCHECKED_CAST")
          cont.resume(task.result as T) { _, _, _ -> }
        } else {
          cont.resumeWithException(task.exception ?: RuntimeException("Task failed without an exception"))
        }
      }
    }
}
