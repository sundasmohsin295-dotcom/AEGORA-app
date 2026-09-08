package com.example.data.cloud

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

/**
 * Android client interface to the trusted AEGORA Firebase Cloud Functions backend.
 * Provides the bridge between the Android operational client and the server authority.
 */
object AegoraServerAuthorityClient {

  private val functionsInstance: FirebaseFunctions by lazy {
    FirebaseFunctions.getInstance()
  }

  /**
   * Submits raw evidence and integrity digest to the Cloud Function for authoritative evaluation.
   * Client-supplied verified claims are never accepted.
   */
  suspend fun submitAndVerifyEvidence(
    evidenceId: String,
    attemptId: String,
    missionId: String,
    skillKey: String,
    evidenceType: String,
    payloadRaw: String,
    integrityDigest: String
  ): Map<String, Any?> {
    val data = hashMapOf(
      "evidenceId" to evidenceId,
      "attemptId" to attemptId,
      "missionId" to missionId,
      "skillKey" to skillKey,
      "evidenceType" to evidenceType,
      "payloadRaw" to payloadRaw,
      "integrityDigest" to integrityDigest
    )

    val result = functionsInstance
      .getHttpsCallable("verifyAndIngestEvidence")
      .call(data)
      .await()

    @Suppress("UNCHECKED_CAST")
    return result.data as? Map<String, Any?> ?: emptyMap()
  }

  /**
   * Requests server evaluation of demonstrated capability state from verified evidence.
   */
  suspend fun requestAuthoritativeCapability(skillKey: String): Map<String, Any?> {
    val data = hashMapOf("skillKey" to skillKey)
    val result = functionsInstance
      .getHttpsCallable("evaluateAuthoritativeCapability")
      .call(data)
      .await()

    @Suppress("UNCHECKED_CAST")
    return result.data as? Map<String, Any?> ?: emptyMap()
  }

  /**
   * Requests server evaluation of the 7 Mastery Gates assessment.
   */
  suspend fun requestAuthoritativeMastery(assessmentId: String? = null): Map<String, Any?> {
    val data = hashMapOf<String, Any?>()
    if (assessmentId != null) data["assessmentId"] = assessmentId

    val result = functionsInstance
      .getHttpsCallable("evaluateAuthoritativeMastery")
      .call(data)
      .await()

    @Suppress("UNCHECKED_CAST")
    return result.data as? Map<String, Any?> ?: emptyMap()
  }

  /**
   * Requests server calculation of career readiness signals.
   */
  suspend fun requestAuthoritativeReadiness(trackId: String = "soc_analyst_t2"): Map<String, Any?> {
    val data = hashMapOf("trackId" to trackId)
    val result = functionsInstance
      .getHttpsCallable("calculateAuthoritativeReadiness")
      .call(data)
      .await()

    @Suppress("UNCHECKED_CAST")
    return result.data as? Map<String, Any?> ?: emptyMap()
  }

  /**
   * Requests server evaluation and grant of Cyber Treasure.
   */
  suspend fun requestAuthoritativeCyberTreasure(
    treasureId: String,
    title: String,
    category: String,
    evidenceId: String
  ): Map<String, Any?> {
    val data = hashMapOf(
      "treasureId" to treasureId,
      "title" to title,
      "category" to category,
      "evidenceId" to evidenceId
    )
    val result = functionsInstance
      .getHttpsCallable("evaluateAndGrantCyberTreasure")
      .call(data)
      .await()

    @Suppress("UNCHECKED_CAST")
    return result.data as? Map<String, Any?> ?: emptyMap()
  }

  /**
   * Requests server generation of authoritative NEXT MOVE based on limiting gates.
   */
  suspend fun requestAuthoritativeNextMove(actionId: String? = null): Map<String, Any?> {
    val data = hashMapOf<String, Any?>()
    if (actionId != null) data["actionId"] = actionId

    val result = functionsInstance
      .getHttpsCallable("generateAuthoritativeNextMove")
      .call(data)
      .await()

    @Suppress("UNCHECKED_CAST")
    return result.data as? Map<String, Any?> ?: emptyMap()
  }
}
