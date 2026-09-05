package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.CapabilityEvidenceEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing verifiable cryptographic evidence artifacts.
 */
@Dao
interface CapabilityEvidenceDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(evidence: CapabilityEvidenceEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(evidenceList: List<CapabilityEvidenceEntity>)

  @Update
  suspend fun update(evidence: CapabilityEvidenceEntity)

  @Delete
  suspend fun delete(evidence: CapabilityEvidenceEntity)

  @Query("DELETE FROM capability_evidence WHERE id = :id")
  suspend fun deleteById(id: String)

  @Query("DELETE FROM capability_evidence WHERE learnerId = :learnerId")
  suspend fun deleteAllForLearner(learnerId: String)

  @Query("SELECT * FROM capability_evidence WHERE id = :id LIMIT 1")
  suspend fun getById(id: String): CapabilityEvidenceEntity?

  @Query("SELECT * FROM capability_evidence WHERE capabilityId = :capabilityId ORDER BY createdAt DESC, id DESC")
  suspend fun getByCapabilityId(capabilityId: String): List<CapabilityEvidenceEntity>

  @Query("SELECT * FROM capability_evidence WHERE learnerId = :learnerId ORDER BY createdAt DESC, id DESC")
  fun observeEvidenceForLearner(learnerId: String): Flow<List<CapabilityEvidenceEntity>>

  @Query("SELECT * FROM capability_evidence WHERE learnerId = :learnerId ORDER BY createdAt DESC, id DESC")
  suspend fun getByLearnerId(learnerId: String): List<CapabilityEvidenceEntity>

  @Query("SELECT * FROM capability_evidence WHERE learnerId = :learnerId AND missionId = :missionId ORDER BY createdAt DESC")
  suspend fun getByMissionId(learnerId: String, missionId: String): List<CapabilityEvidenceEntity>

  @Query("""
    SELECT * FROM capability_evidence 
    WHERE learnerId = :learnerId AND verificationStatus = 'VERIFIED' 
    ORDER BY createdAt DESC, id DESC
  """)
  suspend fun getVerifiedEvidence(learnerId: String): List<CapabilityEvidenceEntity>

  @Query("""
    SELECT * FROM capability_evidence 
    WHERE learnerId = :learnerId 
    ORDER BY createdAt DESC, id DESC 
    LIMIT :limit
  """)
  suspend fun getRecentEvidence(learnerId: String, limit: Int = 10): List<CapabilityEvidenceEntity>

  @Query("SELECT * FROM capability_evidence WHERE evidenceHash = :hash LIMIT 1")
  suspend fun getByHash(hash: String): CapabilityEvidenceEntity?

  @Query("SELECT COUNT(*) FROM capability_evidence WHERE capabilityId = :capabilityId")
  suspend fun countEvidenceForCapability(capabilityId: String): Int

  @Query("SELECT COUNT(*) FROM capability_evidence WHERE capabilityId = :capabilityId AND verificationStatus = 'VERIFIED'")
  suspend fun countVerifiedEvidenceForCapability(capabilityId: String): Int

  @Query("SELECT COUNT(*) FROM capability_evidence WHERE learnerId = :learnerId AND verificationStatus = 'VERIFIED'")
  fun observeVerifiedCountForLearner(learnerId: String): Flow<Int>

  @Query("SELECT * FROM capability_evidence WHERE capabilityId = :capabilityId ORDER BY createdAt DESC, id DESC")
  fun observeEvidenceForCapability(capabilityId: String): Flow<List<CapabilityEvidenceEntity>>
}
