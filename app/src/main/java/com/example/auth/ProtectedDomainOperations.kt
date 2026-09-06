package com.example.auth

import com.example.data.DemonstratedCapabilityRepository
import com.example.data.db.CapabilityEntity
import com.example.data.db.CapabilityEvidenceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Domain-level protected gateway for capability intelligence and evidence operations.
 * Protects learner data from unauthorized or unauthenticated access.
 */
object ProtectedCapabilityGateway {

  /**
   * Retrieves capabilities for a learner strictly after verifying that the caller
   * is authenticated and authorized to access [targetLearnerId].
   */
  fun getCapabilitiesForLearner(
    repository: DemonstratedCapabilityRepository,
    targetLearnerId: String
  ): AuthorizationResult<Flow<List<CapabilityEntity>>> {
    return AuthorizationBoundary.executeProtected(targetLearnerId) { identity ->
      repository.observeCapabilitiesForLearner(identity.mappedLearnerId)
    }
  }

  /**
   * Submits evidence for a learner strictly if the caller owns the evidence's learner ID.
   */
  suspend fun submitEvidence(
    repository: DemonstratedCapabilityRepository,
    evidence: CapabilityEvidenceEntity
  ): AuthorizationResult<Unit> {
    return AuthorizationBoundary.executeProtected(evidence.learnerId) { _ ->
      repository.recordEvidence(evidence)
    }
  }
}
