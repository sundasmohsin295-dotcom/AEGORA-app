package com.example.core.result

/**
 * Enterprise Monadic Result Container.
 * Enforces absolute correctness and deterministic error states across all
 * network operations, database queries, and AI model inferences.
 */
sealed class AegoraResult<out T> {
  data class Success<out T>(val value: T) : AegoraResult<T>()
  data class Failure(
    val code: String,
    val message: String,
    val cause: Throwable? = null,
    val isRecoverable: Boolean = true
  ) : AegoraResult<Nothing>()

  val isSuccess: Boolean get() = this is Success
  val isFailure: Boolean get() = this is Failure

  fun getOrNull(): T? = when (this) {
    is Success -> value
    is Failure -> null
  }

  fun getOrDefault(defaultValue: @UnsafeVariance T): T = when (this) {
    is Success -> value
    is Failure -> defaultValue
  }

  inline fun getOrElse(onFailure: (Failure) -> @UnsafeVariance T): T = when (this) {
    is Success -> value
    is Failure -> onFailure(this)
  }

  inline fun <R> map(transform: (T) -> R): AegoraResult<R> = when (this) {
    is Success -> Success(transform(value))
    is Failure -> this
  }

  inline fun <R> flatMap(transform: (T) -> AegoraResult<R>): AegoraResult<R> = when (this) {
    is Success -> transform(value)
    is Failure -> this
  }

  inline fun onSuccess(action: (T) -> Unit): AegoraResult<T> {
    if (this is Success) action(value)
    return this
  }

  inline fun onFailure(action: (Failure) -> Unit): AegoraResult<T> {
    if (this is Failure) action(this)
    return this
  }

  inline fun recover(rescue: (Failure) -> @UnsafeVariance T): T = when (this) {
    is Success -> value
    is Failure -> rescue(this)
  }

  companion object {
    inline fun <T> runCatching(
      errorCode: String = "ERR_GENERIC_OP",
      block: () -> T
    ): AegoraResult<T> {
      return try {
        Success(block())
      } catch (t: Throwable) {
        Failure(
          code = errorCode,
          message = t.message ?: "Unknown runtime execution fault",
          cause = t,
          isRecoverable = true
        )
      }
    }
  }
}
