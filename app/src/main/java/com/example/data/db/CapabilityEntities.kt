package com.example.data.db

import androidx.room.*
import java.security.MessageDigest

/**
 * Domain / Category classifications for Demonstrated Capabilities.
 */
enum class CapabilityCategory(val displayName: String, val categoryCode: String) {
  THREAT_DETECTION("Threat Detection & Telemetry", "DET"),
  CLOUD_IAM("Cloud Security & IAM", "IAM"),
  INCIDENT_RESPONSE("Incident Response & Forensics", "IR"),
  SECURE_DEVELOPMENT("Secure Architecture & AppSec", "DEV"),
  VULNERABILITY_RESEARCH("Vulnerability Research & Exploits", "VR"),
  CRYPTOGRAPHY("Applied Cryptography & Privacy", "CRYPTO"),
  OFFENSIVE_OPERATIONS("Adversary Emulation & Red Team", "RED"),
  AI_SECURITY("AI/LLM Threat Modeling & Defense", "AISEC")
}

/**
 * Standard classification for capability mastery status.
 */
enum class MasteryStatus(val displayName: String) {
  NOT_STARTED("Not Started"),
  IN_PROGRESS("In Progress"),
  PROVISIONAL("Provisional Mastery"),
  DEMONSTRATED("Demonstrated Capability"),
  REQUIRES_REASSESSMENT("Requires Reassessment")
}

/**
 * Verification state of an evidence artifact.
 */
enum class VerificationStatus(val displayName: String) {
  UNVERIFIED("Unverified"),
  VERIFIED("Cryptographically Verified"),
  INVALID("Invalid Evidence Artifact"),
  REVOKED("Revoked by Supervisor/Audit")
}

/**
 * Types of evidence artifacts supported across labs, war rooms, and missions.
 */
enum class EvidenceType(val displayName: String, val defaultWeight: Float) {
  LAB_SUBMISSION("Live Sandbox Lab Solution", 0.85f),
  CTF_FLAG_CAPTURE("Hands-on CTF Challenge Solve", 0.90f),
  SIMULATION_TELEMETRY("War Room SOC Telemetry Analysis", 0.95f),
  CODE_ARTIFACT("Secure Script / Policy Artifact", 0.88f),
  PEER_DEFENSE_REVIEW("Peer Code & Architecture Review", 0.80f),
  VERBAL_EXPLANATION("Socratic Explanation & Reasoning", 0.92f),
  WAR_ROOM_INCIDENT("Live Incident Containment Event", 1.00f),
  CROSS_ENVIRONMENT_TRANSFER("Unknown Tool / Zero-Context Transfer", 1.00f)
}

/**
 * Evaluator identities for audit and provenance tracking.
 */
enum class EvaluatorType(val displayName: String) {
  SYSTEM_AUTOMATED("Automated Rule & Regex Engine"),
  AI_MENTOR_DESK("AI Socratic Examiner (Gemini Model)"),
  KERNEL_EMULATOR("KEM Sandbox Runtime Execution"),
  PEER_VERIFIER("Peer Review Verification"),
  ENTERPRISE_SUPERVISOR("Lead Instructor / Enterprise Verifier")
}

/**
 * Room Entity representing a measurable cybersecurity capability for a specific learner.
 *
 * Implements composite uniqueness on (learnerId, skillKey) so multiple active records
 * cannot be created accidentally for a single learner.
 * Distinguishes historicalCapability from decay-adjusted currentConfidence.
 */
@Entity(
  tableName = "capabilities",
  indices = [
    Index(value = ["learnerId", "skillKey"], unique = true),
    Index(value = ["learnerId"]),
    Index(value = ["skillKey"]),
    Index(value = ["category"]),
    Index(value = ["lastVerifiedAt"]),
    Index(value = ["currentConfidence"]),
    Index(value = ["historicalCapabilityScore"])
  ]
)
data class CapabilityEntity(
  @PrimaryKey
  val id: String,
  val learnerId: String,
  val skillKey: String, // e.g. "linux_log_analysis", "aws_iam_privesc", "heap_overflow_triage"
  val name: String,
  val category: String, // e.g. CapabilityCategory.THREAT_DETECTION.name
  val knowledgeScore: Int = 0,               // 0..100
  val recallScore: Int = 0,                  // 0..100
  val applicationScore: Int = 0,             // 0..100
  val investigationScore: Int = 0,           // 0..100
  val transferScore: Int = 0,                // 0..100
  val explanationScore: Int = 0,             // 0..100
  val uncertaintyResilienceScore: Int = 0,   // 0..100
  val independenceScore: Int = 0,            // 0..100
  val evidenceQualityScore: Int = 0,         // 0..100
  val currentConfidence: Int = 0,            // 0..100 (Recency / decay adjusted)
  val historicalCapabilityScore: Int = 0,    // 0..100 (Highest trustworthy demonstrated baseline, never decays)
  val lastVerifiedAt: Long = 0L,             // Timestamp in millis
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val schemaVersion: Int = 2
) {
  @delegate:Ignore
  val isDemonstrated: Boolean by lazy {
    historicalCapabilityScore >= 80 || currentConfidence >= 80
  }
}

/**
 * Room Entity representing a concrete verifiable artifact proving capability mastery.
 *
 * Preserves historical learning evidence even if capabilities are reconfigured.
 */
@Entity(
  tableName = "capability_evidence",
  foreignKeys = [
    ForeignKey(
      entity = CapabilityEntity::class,
      parentColumns = ["id"],
      childColumns = ["capabilityId"],
      onDelete = ForeignKey.NO_ACTION // Preserve historical evidence
    )
  ],
  indices = [
    Index(value = ["learnerId"]),
    Index(value = ["capabilityId"]),
    Index(value = ["missionId"]),
    Index(value = ["attemptId"]),
    Index(value = ["verificationStatus"]),
    Index(value = ["createdAt"]),
    Index(value = ["evidenceHash"])
  ]
)
data class CapabilityEvidenceEntity(
  @PrimaryKey
  val id: String,
  val learnerId: String,
  val capabilityId: String,
  val missionId: String? = null,
  val attemptId: String? = null,
  val evidenceType: String = EvidenceType.LAB_SUBMISSION.name,
  val complexity: Int = 50,                   // 0..100
  val independenceScore: Int = 100,           // 0..100 (reduced by hints/assistance)
  val authenticityScore: Int = 95,            // 0..100
  val transferabilityScore: Int = 85,         // 0..100
  val evidenceQualityScore: Int = 90,         // 0..100
  val outcomeScore: Int = 100,                // 0..100
  val hintsUsed: Int = 0,                     // >= 0
  val retries: Int = 0,                       // >= 0
  val timeSpentSeconds: Int = 0,              // >= 0
  val reasoningQualityScore: Int = 90,        // 0..100
  val confidenceDeclared: Int = 80,           // 0..100
  val confidenceCalibrated: Int = 85,         // 0..100
  val evidenceHash: String,                   // SHA-256 integrity hash
  val verificationStatus: String = VerificationStatus.VERIFIED.name,
  val createdAt: Long = System.currentTimeMillis(),
  val schemaVersion: Int = 1
)

/**
 * Room Entity representing an evaluation against AEGORA's 7 mastery dimensions.
 */
@Entity(
  tableName = "mastery_assessments",
  foreignKeys = [
    ForeignKey(
      entity = CapabilityEntity::class,
      parentColumns = ["id"],
      childColumns = ["capabilityId"],
      onDelete = ForeignKey.NO_ACTION
    ),
    ForeignKey(
      entity = CapabilityEvidenceEntity::class,
      parentColumns = ["id"],
      childColumns = ["evidenceId"],
      onDelete = ForeignKey.SET_NULL
    )
  ],
  indices = [
    Index(value = ["learnerId"]),
    Index(value = ["capabilityId"]),
    Index(value = ["evidenceId"]),
    Index(value = ["missionId"]),
    Index(value = ["masteryStatus"]),
    Index(value = ["createdAt"])
  ]
)
data class MasteryAssessmentEntity(
  @PrimaryKey
  val id: String,
  val learnerId: String,
  val capabilityId: String,
  val evidenceId: String? = null,
  val missionId: String? = null,
  val understandScore: Int = 0,               // 0..100
  val recallScore: Int = 0,                   // 0..100
  val applyScore: Int = 0,                    // 0..100
  val investigateScore: Int = 0,              // 0..100
  val transferScore: Int = 0,                 // 0..100
  val explainScore: Int = 0,                  // 0..100
  val uncertaintyResilienceScore: Int = 0,    // 0..100
  val overallScore: Int = 0,                  // 0..100
  val masteryStatus: String = MasteryStatus.IN_PROGRESS.name,
  val evaluatorType: String = EvaluatorType.AI_MENTOR_DESK.name,
  val confidence: Int = 85,                   // 0..100
  val assessmentReason: String,
  val createdAt: Long = System.currentTimeMillis(),
  val updatedAt: Long = System.currentTimeMillis(),
  val schemaVersion: Int = 1
)

/**
 * Relational model: Capability with associated Evidence artifacts.
 */
data class CapabilityWithEvidence(
  @Embedded val capability: CapabilityEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "capabilityId"
  )
  val evidenceList: List<CapabilityEvidenceEntity> = emptyList()
)

/**
 * Relational model: Capability with associated Mastery Assessments.
 */
data class CapabilityWithMastery(
  @Embedded val capability: CapabilityEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "capabilityId"
  )
  val assessments: List<MasteryAssessmentEntity> = emptyList()
)

/**
 * Full Composite Relational model: Capability with Evidence and Mastery Assessments.
 */
data class CapabilityWithFullMastery(
  @Embedded val capability: CapabilityEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "capabilityId"
  )
  val evidenceList: List<CapabilityEvidenceEntity> = emptyList(),
  @Relation(
    parentColumn = "id",
    entityColumn = "capabilityId"
  )
  val assessments: List<MasteryAssessmentEntity> = emptyList()
)
