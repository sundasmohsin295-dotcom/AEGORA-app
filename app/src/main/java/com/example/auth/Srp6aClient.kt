package com.example.auth

import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * PHASE 24: ZERO-KNOWLEDGE PROOF (ZKP) AUTHENTICATION (SRP-6a PROTOCOL)
 * ======================================================================
 * RFC 5054 / SRP-6a (Secure Remote Password) protocol client implementation.
 *
 * Provides cryptographic zero-knowledge proof authentication:
 * - The client never transmits the password or even its hash over the network.
 * - Both client and server independently compute ephemeral session keys (K)
 *   and exchange session proof tokens (M1 and M2).
 * - Eavesdroppers and server compromise cannot reveal plaintext passwords or
 *   impersonate the client in subsequent sessions.
 */
object Srp6aClient {

  // RFC 5054 1024-bit prime group N
  private const val N_HEX =
    "EEAF0AB9ADB38DD69C33F80AFA8FC5E86072618775FF3C0B9EA2314C9C256576D674DF7496EA81D3383B4813D692C6FF3B05A0FA23637C04376424C9EE0A92F7" +
    "914B3FB9F319B77AEC34401AAEC9768329634F24C42F2C1BADC3410D0D566AFB"

  val N: BigInteger = BigInteger(N_HEX, 16)
  val g: BigInteger = BigInteger.valueOf(2)

  // SRP-6a multiplier: k = H(N, g)
  val k: BigInteger by lazy {
    val hash = sha256(N.toByteArray() + g.toByteArray())
    BigInteger(1, hash)
  }

  private val random = SecureRandom()

  /**
   * Generates a random salt (16 bytes hex).
   */
  fun generateSalt(): String {
    val salt = ByteArray(16)
    random.nextBytes(salt)
    return salt.joinToString("") { "%02x".format(it) }
  }

  /**
   * Computes the password verifier `v` for registration:
   * x = H(s | H(I | ":" | P))
   * v = g^x mod N
   */
  fun computeVerifier(saltHex: String, identity: String, passChars: CharArray): String {
    val x = computePrivateKey(saltHex, identity, passChars)
    val v = g.modPow(x, N)
    return v.toString(16)
  }

  /**
   * Generates client ephemeral keys for authentication initiation:
   * a = random secret exponent
   * A = g^a mod N
   */
  fun generateClientEphemeral(): Pair<BigInteger, String> {
    val a = BigInteger(256, random)
    val bigA = g.modPow(a, N)
    return Pair(a, bigA.toString(16))
  }

  /**
   * Computes client session proof M1:
   * u = H(A, B)
   * S = (B - k * (g^x mod N))^(a + u*x) mod N
   * K = H(S)
   * M1 = H(H(N) xor H(g) | H(I) | s | A | B | K)
   */
  fun computeClientProof(
    identity: String,
    saltHex: String,
    passChars: CharArray,
    clientSecretA: BigInteger,
    clientEphemeralAHex: String,
    serverEphemeralBHex: String
  ): SrpClientProofResult {
    val bigA = BigInteger(clientEphemeralAHex, 16)
    val bigB = BigInteger(serverEphemeralBHex, 16)

    // Security check: B mod N != 0
    if (bigB.mod(N) == BigInteger.ZERO) {
      throw SecurityException("Hostile server ephemeral B rejected (B mod N == 0)")
    }

    // u = H(A, B)
    val uBytes = sha256(bigA.toByteArray() + bigB.toByteArray())
    val u = BigInteger(1, uBytes)
    if (u == BigInteger.ZERO) {
      throw SecurityException("Scrambling parameter u cannot be zero")
    }

    // x = H(s, H(I:P))
    val x = computePrivateKey(saltHex, identity, passChars)

    // v = g^x mod N
    val v = g.modPow(x, N)

    // Base = (B - k*v) mod N
    val kv = k.multiply(v).mod(N)
    val base = bigB.subtract(kv).mod(N)

    // Exp = a + u*x
    val exp = clientSecretA.add(u.multiply(x))

    // Premaster Secret S = Base^Exp mod N
    val sPremaster = base.modPow(exp, N)
    val sharedKeyK = sha256(sPremaster.toByteArray())
    val sharedKeyHex = sharedKeyK.joinToString("") { "%02x".format(it) }

    // Compute M1 = H(A, B, K, s)
    val m1Bytes = sha256(bigA.toByteArray() + bigB.toByteArray() + sharedKeyK + hexStringToByteArray(saltHex))
    val m1Hex = m1Bytes.joinToString("") { "%02x".format(it) }

    // Expected Server Proof M2 = H(A, M1, K)
    val m2Bytes = sha256(bigA.toByteArray() + m1Bytes + sharedKeyK)
    val expectedM2Hex = m2Bytes.joinToString("") { "%02x".format(it) }

    return SrpClientProofResult(
      clientProofM1 = m1Hex,
      expectedServerProofM2 = expectedM2Hex,
      sessionKeyHex = sharedKeyHex
    )
  }

  private fun computePrivateKey(saltHex: String, identity: String, passChars: CharArray): BigInteger {
    val passBytes = String(passChars).toByteArray(Charsets.UTF_8)
    val idPassHash = sha256("${identity.trim().lowercase()}:".toByteArray(Charsets.UTF_8) + passBytes)
    val saltBytes = hexStringToByteArray(saltHex)
    val xBytes = sha256(saltBytes + idPassHash)
    return BigInteger(1, xBytes)
  }

  private fun sha256(data: ByteArray): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(data)
  }

  private fun hexStringToByteArray(s: String): ByteArray {
    val len = s.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
      data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
      i += 2
    }
    return data
  }
}

data class SrpClientProofResult(
  val clientProofM1: String,
  val expectedServerProofM2: String,
  val sessionKeyHex: String
)
