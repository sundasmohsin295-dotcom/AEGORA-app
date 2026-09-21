package com.example.security

import java.util.Arrays

/**
 * Enterprise In-Memory Wiping and Cryptographic Memory Protection.
 *
 * Implements:
 * 1. Overwriting sensitive buffers (passwords, PINs, auth tokens, symmetric cipher keys)
 *    with zero values ('\0' / 0x00) immediately upon consumption.
 * 2. Scoped block execution (`useAndWipe`) ensuring guaranteed zeroing out in memory
 *    even in the event of unexpected runtime exceptions.
 * 3. Prevents Java String interning and garbage-collector stale memory dumps from
 *    exposing plaintext credentials to heap-inspection or RAM-scraping tools (e.g. Frida / gdb).
 */
object SecureMemory {

  private val activeCharBuffers = java.util.Collections.newSetFromMap(java.util.WeakHashMap<CharArray, Boolean>())
  private val activeByteBuffers = java.util.Collections.newSetFromMap(java.util.WeakHashMap<ByteArray, Boolean>())

  /**
   * Registers an active buffer containing sensitive material so that the
   * Global Crash Burner / Tombstone Sanitizer can zero it out in emergency scenarios.
   */
  fun registerActiveBuffer(buffer: CharArray) {
    synchronized(activeCharBuffers) {
      activeCharBuffers.add(buffer)
    }
  }

  fun registerActiveBuffer(buffer: ByteArray) {
    synchronized(activeByteBuffers) {
      activeByteBuffers.add(buffer)
    }
  }

  fun unregisterActiveBuffer(buffer: CharArray) {
    synchronized(activeCharBuffers) {
      activeCharBuffers.remove(buffer)
    }
  }

  fun unregisterActiveBuffer(buffer: ByteArray) {
    synchronized(activeByteBuffers) {
      activeByteBuffers.remove(buffer)
    }
  }

  /**
   * Synchronously zeroes out all registered active buffers.
   * Invoked by the Tombstone Sanitizer / Crash Burner before unhandled exceptions terminate.
   */
  fun wipeAllActiveBuffers() {
    synchronized(activeCharBuffers) {
      for (buf in activeCharBuffers) {
        wipe(buf)
      }
      activeCharBuffers.clear()
    }
    synchronized(activeByteBuffers) {
      for (buf in activeByteBuffers) {
        wipe(buf)
      }
      activeByteBuffers.clear()
    }
  }

  /**
   * Securely overwrites a CharArray buffer with zeroes.
   */
  fun wipe(buffer: CharArray?) {
    if (buffer == null || buffer.isEmpty()) return
    Arrays.fill(buffer, '\u0000')
  }

  /**
   * Securely overwrites a ByteArray buffer with zeroes.
   */
  fun wipe(buffer: ByteArray?) {
    if (buffer == null || buffer.isEmpty()) return
    Arrays.fill(buffer, 0.toByte())
  }

  /**
   * Executes a sensitive cryptographic operation with a CharArray and guarantees
   * that the array is zeroed out in memory immediately afterwards.
   */
  inline fun <R> useAndWipe(chars: CharArray, block: (CharArray) -> R): R {
    registerActiveBuffer(chars)
    try {
      return block(chars)
    } finally {
      wipe(chars)
      unregisterActiveBuffer(chars)
    }
  }

  /**
   * Executes a sensitive cryptographic operation with a ByteArray and guarantees
   * that the array is zeroed out in memory immediately afterwards.
   */
  inline fun <R> useAndWipe(bytes: ByteArray, block: (ByteArray) -> R): R {
    registerActiveBuffer(bytes)
    try {
      return block(bytes)
    } finally {
      wipe(bytes)
      unregisterActiveBuffer(bytes)
    }
  }

  /**
   * Constant-Time string comparison using MessageDigest.isEqual to defeat CPU side-channel timing attacks.
   */
  fun constantTimeEquals(a: String?, b: String?): Boolean {
    if (a == null || b == null) return a === b
    val bytesA = a.toByteArray(Charsets.UTF_8)
    val bytesB = b.toByteArray(Charsets.UTF_8)
    return java.security.MessageDigest.isEqual(bytesA, bytesB)
  }

  /**
   * Constant-Time byte array comparison using MessageDigest.isEqual.
   */
  fun constantTimeEquals(a: ByteArray?, b: ByteArray?): Boolean {
    if (a == null || b == null) return a === b
    return java.security.MessageDigest.isEqual(a, b)
  }

  /**
   * Securely compares two CharArrays in constant time to prevent timing attacks.
   */
  fun constantTimeEquals(a: CharArray?, b: CharArray?): Boolean {
    if (a == null || b == null) return a === b
    if (a.size != b.size) return false
    var result = 0
    for (i in a.indices) {
      result = result or (a[i].code xor b[i].code)
    }
    return result == 0
  }
}
