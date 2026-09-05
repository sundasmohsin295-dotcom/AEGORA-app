package com.example.capability

/**
 * Centralized policy configuration model for the Demonstrated Capability Engine.
 * Configures thresholds, weights, and decay dynamics without hardcoding.
 */
data class MasteryPolicy(
  val minGatePassingScore: Int = 70,
  val minDemonstrationScore: Int = 75,
  val minEvidenceQuality: Int = 70,
  val minIndependenceScore: Int = 70,
  val minTransferScore: Int = 65,
  val minUncertaintyScore: Int = 65,
  val minVerifiedEvidenceCount: Int = 1,
  val decayHalfLifeDays: Double = 30.0,
  val maxDecayPenaltyPercent: Double = 40.0,
  val verifiedMultiplier: Double = 1.0,
  val pendingMultiplier: Double = 0.65,
  val unverifiedMultiplier: Double = 0.35,
  val hintPenaltyPerUnit: Double = 5.0,
  val retryPenaltyPerUnit: Double = 4.0,
  val understandWeight: Double = 0.10,
  val recallWeight: Double = 0.10,
  val applyWeight: Double = 0.20,
  val investigateWeight: Double = 0.20,
  val transferWeight: Double = 0.15,
  val explainWeight: Double = 0.10,
  val uncertaintyResilienceWeight: Double = 0.15
) {
  companion object {
    val DEFAULT = MasteryPolicy()
    val STRICT = MasteryPolicy(
      minGatePassingScore = 75,
      minDemonstrationScore = 80,
      minEvidenceQuality = 75,
      minIndependenceScore = 80,
      minTransferScore = 70,
      minUncertaintyScore = 70,
      minVerifiedEvidenceCount = 2,
      decayHalfLifeDays = 21.0
    )
    val ACCELERATED = MasteryPolicy(
      minGatePassingScore = 65,
      minDemonstrationScore = 70,
      minEvidenceQuality = 60,
      minIndependenceScore = 65,
      decayHalfLifeDays = 45.0
    )
  }
}
