package com.example

import com.example.security.SecureMemory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureMemoryTest {

  @Test
  fun testCharArrayIsZeroedOutImmediatelyAfterUsage() {
    val sensitivePassword = "SuperSecretOperatorToken2026!".toCharArray()
    val initialCopy = sensitivePassword.clone()

    // Assert that initially it is not wiped
    assertFalse(SecureMemory.isWiped(sensitivePassword))

    // Use and wipe
    var processedLength = 0
    SecureMemory.useAndWipe(sensitivePassword) { chars ->
      processedLength = chars.size
      assertEquals(initialCopy.size, chars.size)
      assertEquals(initialCopy[0], chars[0])
    }

    // Mathematical proof: After execution, every single char must be '\0'
    assertTrue("Memory buffer must be zeroized", SecureMemory.isWiped(sensitivePassword))
    for (i in sensitivePassword.indices) {
      assertEquals('\u0000', sensitivePassword[i])
    }
  }

  @Test
  fun testByteArrayIsZeroedOutImmediately() {
    val keyBytes = byteArrayOf(0x1F, 0x2A, 0x3B, 0x4C, 0x5D)
    assertFalse(SecureMemory.isWiped(keyBytes))

    SecureMemory.wipe(keyBytes)

    assertTrue(SecureMemory.isWiped(keyBytes))
    for (b in keyBytes) {
      assertEquals(0.toByte(), b)
    }
  }
}
