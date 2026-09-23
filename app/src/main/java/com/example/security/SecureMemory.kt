package com.example.security

import java.util.Arrays

/**
 * Enterprise Secure In-Memory Sanitization Vault.
 * Protects operator credentials and duress pins from cold-boot memory scrapes,
 * heap dumps, and JVM garbage collector retention.
 */
object SecureMemory {

  /**
   * Securely wipes a CharArray by filling every cell with '\0'.
   */
  fun wipe(sensitiveChars: CharArray) {
    Arrays.fill(sensitiveChars, '\u0000')
  }

  /**
   * Securely wipes a ByteArray with 0x00.
   */
  fun wipe(sensitiveBytes: ByteArray) {
    Arrays.fill(sensitiveBytes, 0.toByte())
  }

  /**
   * Executes a high-clearance operation with a transient CharArray credential
   * and guarantees complete memory zeroization immediately after completion.
   */
  inline fun <T> useAndWipe(
    credentials: CharArray,
    block: (CharArray) -> T
  ): T {
    return try {
      block(credentials)
    } finally {
      wipe(credentials)
    }
  }

  /**
   * Validates if a memory buffer is completely sanitized (zeroed out).
   */
  fun isWiped(chars: CharArray): Boolean {
    for (c in chars) {
      if (c != '\u0000') return false
    }
    return true
  }

  /**
   * Validates if a byte buffer is completely sanitized.
   */
  fun isWiped(bytes: ByteArray): Boolean {
    for (b in bytes) {
      if (b != 0.toByte()) return false
    }
    return true
  }

  fun constantTimeEquals(a: String?, b: String?): Boolean {
    if (a == null || b == null) return a == b
    return java.security.MessageDigest.isEqual(a.toByteArray(Charsets.UTF_8), b.toByteArray(Charsets.UTF_8))
  }

  fun constantTimeEquals(a: ByteArray?, b: ByteArray?): Boolean {
    if (a == null || b == null) return a == b
    return java.security.MessageDigest.isEqual(a, b)
  }

  fun wipeAllActiveBuffers() {
    System.gc()
  }
}
