package com.example.capability

import com.example.data.DemonstratedCapabilityRepository
import com.example.model.FailureModeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Server-authoritative manager for Failure Patterns on the client/local domain.
 *
 * Enforces:
 * 1. Client-submitted failure labels or confidence scores are IGNORED.
 * 2. Confidence is strictly computed from deterministic observation counts.
 * 3. Scoped strictly to the authenticated learnerId.
 */
class AuthoritativeFailurePatternManager(
  private val repository: DemonstratedCapabilityRepository? = null
) {
  // In-memory backing state for detected authoritative failure patterns
  private val _learnerPatterns = MutableStateFlow<Map<String, List<AuthoritativeFailurePatternRecord>>>(emptyMap())
  val learnerPatterns: StateFlow<Map<String, List<AuthoritativeFailurePatternRecord>>> = _learnerPatterns.asStateFlow()

  /**
   * Authoritatively records observations for a learner.
   * Derives tier and observation counts server-authoritatively.
   */
  fun recordObservation(
    authenticatedUid: String,
    missionId: String,
    detectedResults: List<FailurePatternDetectionResult>,
    timestamp: Long = System.currentTimeMillis()
  ): List<AuthoritativeFailurePatternRecord> {
    require(authenticatedUid.isNotBlank()) { "Authenticated learner UID is required." }

    val currentRecords = _learnerPatterns.value[authenticatedUid]?.toMutableList() ?: mutableListOf()
    val updatedRecords = mutableListOf<AuthoritativeFailurePatternRecord>()

    detectedResults.filter { it.isDetected }.forEach { detected ->
      val existingIndex = currentRecords.indexOfFirst { it.pattern == detected.pattern }
      if (existingIndex >= 0) {
        val existing = currentRecords[existingIndex]
        val newCount = existing.observationCount + 1
        val updated = existing.copy(
          observationCount = newCount,
          confidenceTier = DeterministicFailurePatternDetector.computeConfidenceTier(newCount),
          lastObservedMissionId = missionId,
          lastObservedTimestamp = timestamp,
          detectionDetails = (existing.detectionDetails + detected.observedSymptom).takeLast(5)
        )
        currentRecords[existingIndex] = updated
        updatedRecords.add(updated)
      } else {
        val newRecord = AuthoritativeFailurePatternRecord(
          patternId = "fp_${UUID.randomUUID().toString().take(8)}",
          learnerId = authenticatedUid,
          pattern = detected.pattern,
          confidenceTier = FailurePatternConfidenceTier.OBSERVED,
          observationCount = 1,
          lastObservedMissionId = missionId,
          lastObservedTimestamp = timestamp,
          firstObservedTimestamp = timestamp,
          detectionDetails = listOf(detected.observedSymptom)
        )
        currentRecords.add(newRecord)
        updatedRecords.add(newRecord)
      }
    }

    val updatedMap = _learnerPatterns.value.toMutableMap()
    updatedMap[authenticatedUid] = currentRecords
    _learnerPatterns.value = updatedMap

    return updatedRecords
  }

  fun getPatternsForLearner(authenticatedUid: String): List<AuthoritativeFailurePatternRecord> {
    return _learnerPatterns.value[authenticatedUid] ?: emptyList()
  }

  fun getHighConfidencePatterns(authenticatedUid: String): List<AuthoritativeFailurePatternRecord> {
    return getPatternsForLearner(authenticatedUid).filter {
      it.confidenceTier == FailurePatternConfidenceTier.HIGH_CONFIDENCE
    }
  }

  /**
   * Security enforcement: Attempting to inject a client-controlled failure record directly
   * will be sanitized or rejected.
   */
  fun sanitizeAndRejectClientInjectedPattern(
    authenticatedUid: String,
    clientClaimedPattern: FailureModeType,
    clientClaimedConfidence: String
  ): Boolean {
    // Client cannot dictate confidence or patterns directly; always rejected
    return false
  }
}
