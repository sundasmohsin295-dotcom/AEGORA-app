package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Capability management within the Demonstrated Capability Engine.
 */
@Dao
interface CapabilityDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(capability: CapabilityEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(capabilities: List<CapabilityEntity>)

  @Update
  suspend fun update(capability: CapabilityEntity)

  @Delete
  suspend fun delete(capability: CapabilityEntity)

  @Query("DELETE FROM capabilities WHERE id = :id")
  suspend fun deleteById(id: String)

  @Query("DELETE FROM capabilities WHERE learnerId = :learnerId")
  suspend fun deleteAllForLearner(learnerId: String)

  @Query("SELECT * FROM capabilities WHERE id = :id LIMIT 1")
  suspend fun getById(id: String): CapabilityEntity?

  @Query("SELECT * FROM capabilities WHERE learnerId = :learnerId ORDER BY historicalCapabilityScore DESC, currentConfidence DESC, name ASC")
  fun observeByLearner(learnerId: String): Flow<List<CapabilityEntity>>

  @Query("SELECT * FROM capabilities WHERE learnerId = :learnerId ORDER BY historicalCapabilityScore DESC, currentConfidence DESC, name ASC")
  suspend fun getByLearnerId(learnerId: String): List<CapabilityEntity>

  @Query("SELECT * FROM capabilities WHERE learnerId = :learnerId AND skillKey = :skillKey LIMIT 1")
  suspend fun getByLearnerAndSkill(learnerId: String, skillKey: String): CapabilityEntity?

  @Query("SELECT * FROM capabilities WHERE learnerId = :learnerId AND category = :category ORDER BY currentConfidence DESC")
  fun getCapabilitiesByCategory(learnerId: String, category: String): Flow<List<CapabilityEntity>>

  @Query("""
    SELECT * FROM capabilities 
    WHERE learnerId = :learnerId 
      AND (lastVerifiedAt < :thresholdTimestamp OR currentConfidence < :confidenceThreshold)
    ORDER BY currentConfidence ASC, lastVerifiedAt ASC
  """)
  suspend fun getCapabilitiesNeedingReassessment(
    learnerId: String,
    thresholdTimestamp: Long,
    confidenceThreshold: Int = 70
  ): List<CapabilityEntity>

  @Query("""
    SELECT * FROM capabilities 
    WHERE learnerId = :learnerId AND lastVerifiedAt > 0 
    ORDER BY lastVerifiedAt DESC 
    LIMIT :limit
  """)
  suspend fun getRecentlyVerified(learnerId: String, limit: Int = 10): List<CapabilityEntity>

  @Query("""
    SELECT * FROM capabilities 
    WHERE learnerId = :learnerId AND (historicalCapabilityScore >= 80 OR currentConfidence >= 80)
    ORDER BY currentConfidence DESC
  """)
  fun getDemonstratedForLearner(learnerId: String): Flow<List<CapabilityEntity>>

  @Query("SELECT COUNT(*) FROM capabilities WHERE learnerId = :learnerId")
  fun countForLearner(learnerId: String): Flow<Int>

  @Query("SELECT COUNT(*) FROM capabilities WHERE learnerId = :learnerId AND (historicalCapabilityScore >= 80 OR currentConfidence >= 80)")
  fun countDemonstratedForLearner(learnerId: String): Flow<Int>

  @Query("SELECT AVG(currentConfidence) FROM capabilities WHERE learnerId = :learnerId")
  fun getAverageConfidenceForLearner(learnerId: String): Flow<Float?>

  @Transaction
  @Query("SELECT * FROM capabilities WHERE id = :id")
  fun getCapabilityWithEvidence(id: String): Flow<CapabilityWithEvidence?>

  @Transaction
  @Query("SELECT * FROM capabilities WHERE id = :id")
  fun getCapabilityWithMastery(id: String): Flow<CapabilityWithMastery?>

  @Transaction
  @Query("SELECT * FROM capabilities WHERE id = :id")
  fun getCapabilityWithFullMastery(id: String): Flow<CapabilityWithFullMastery?>

  @Transaction
  @Query("SELECT * FROM capabilities WHERE learnerId = :learnerId AND (historicalCapabilityScore >= 80 OR currentConfidence >= 80)")
  fun getAllDemonstratedWithEvidence(learnerId: String): Flow<List<CapabilityWithEvidence>>
}
