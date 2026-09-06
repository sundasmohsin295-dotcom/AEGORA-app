package com.example.auth

/**
 * Domain-level authorization results for protected operations.
 */
sealed class AuthorizationResult<out T> {
  data class Authorized<T>(val data: T) : AuthorizationResult<T>()
  data class Unauthenticated(val message: String = "Operation requires an authenticated session.") : AuthorizationResult<Nothing>()
  data class Forbidden(val message: String = "Identity does not have permission to access requested learner data.") : AuthorizationResult<Nothing>()
}

/**
 * Enforces ownership and authentication on protected domain operations.
 * UI components cannot bypass this boundary by providing another user's learner ID.
 */
object AuthorizationBoundary {

  /**
   * Validates that the active session is authenticated and authoritatively owns [targetLearnerId].
   */
  fun validateLearnerAccess(targetLearnerId: String): AuthorizationCheckResult {
    val identity = AegoraAuthRepository.currentIdentity
      ?: return AuthorizationCheckResult.Unauthenticated

    if (identity.mappedLearnerId != targetLearnerId) {
      return AuthorizationCheckResult.Forbidden(
        "Learner isolation violation: authenticated as ${identity.mappedLearnerId}, cannot access $targetLearnerId"
      )
    }

    return AuthorizationCheckResult.Authorized(identity)
  }

  /**
   * Executes a protected operation strictly if the caller's authenticated identity matches [targetLearnerId].
   */
  inline fun <T> executeProtected(
    targetLearnerId: String,
    operation: (AuthenticatedIdentity) -> T
  ): AuthorizationResult<T> {
    val check = validateLearnerAccess(targetLearnerId)
    return when (check) {
      is AuthorizationCheckResult.Authorized -> AuthorizationResult.Authorized(operation(check.identity))
      is AuthorizationCheckResult.Unauthenticated -> AuthorizationResult.Unauthenticated()
      is AuthorizationCheckResult.Forbidden -> AuthorizationResult.Forbidden(check.reason)
    }
  }

  /**
   * Executes an operation using the caller's authenticated identity.
   * Fails if unauthenticated.
   */
  inline fun <T> executeWithAuthenticatedIdentity(
    operation: (AuthenticatedIdentity) -> T
  ): AuthorizationResult<T> {
    val identity = AegoraAuthRepository.currentIdentity
      ?: return AuthorizationResult.Unauthenticated()
    return AuthorizationResult.Authorized(operation(identity))
  }
}

sealed class AuthorizationCheckResult {
  data class Authorized(val identity: AuthenticatedIdentity) : AuthorizationCheckResult()
  object Unauthenticated : AuthorizationCheckResult()
  data class Forbidden(val reason: String) : AuthorizationCheckResult()
}
