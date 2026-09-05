package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.MasteryAssessmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for 7-Stage mastery assessments and evaluation history.
 */
@Dao
interface MasteryAssessmentDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(assessment: MasteryAssessmentEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(assessments: List<MasteryAssessmentEntity>)

  @Update
  suspend fun update(assessment: MasteryAssessmentEntity)

  @Delete
  suspend fun delete(assessment: MasteryAssessmentEntity)

  @Query("DELETE FROM mastery_assessments WHERE id = :id")
  suspend fun deleteById(id: String)

  @Query("DELETE FROM mastery_assessments WHERE learnerId = :learnerId")
  suspend fun deleteAllForLearner(learnerId: String)

  @Query("SELECT * FROM mastery_assessments WHERE id = :id LIMIT 1")
  suspend fun getById(id: String): MasteryAssessmentEntity?

  @Query("SELECT * FROM mastery_assessments WHERE capabilityId = :capabilityId ORDER BY createdAt DESC, id DESC")
  suspend fun getByCapabilityId(capabilityId: String): List<MasteryAssessmentEntity>

  @Query("SELECT * FROM mastery_assessments WHERE evidenceId = :evidenceId ORDER BY createdAt DESC, id DESC")
  suspend fun getByEvidenceId(evidenceId: String): List<MasteryAssessmentEntity>

  @Query("SELECT * FROM mastery_assessments WHERE learnerId = :learnerId ORDER BY createdAt DESC, id DESC")
  fun observeAssessmentsForLearner(learnerId: String): Flow<List<MasteryAssessmentEntity>>

  @Query("SELECT * FROM mastery_assessments WHERE learnerId = :learnerId ORDER BY createdAt DESC, id DESC")
  suspend fun getByLearnerId(learnerId: String): List<MasteryAssessmentEntity>

  @Query("""
    SELECT * FROM mastery_assessments 
    WHERE capabilityId = :capabilityId 
    ORDER BY createdAt DESC, id DESC 
    LIMIT 1
  """)
  suspend fun getLatestAssessment(capabilityId: String): MasteryAssessmentEntity?

  @Query("""
    SELECT * FROM mastery_assessments 
    WHERE capabilityId = :capabilityId 
    ORDER BY createdAt DESC, id DESC 
    LIMIT 1
  """)
  fun observeLatestAssessment(capabilityId: String): Flow<MasteryAssessmentEntity?>

  @Query("""
    SELECT * FROM mastery_assessments 
    WHERE learnerId = :learnerId AND masteryStatus = 'DEMONSTRATED' 
    ORDER BY createdAt DESC, id DESC
  """)
  suspend fun getDemonstratedAssessments(learnerId: String): List<MasteryAssessmentEntity>

  @Query("""
    SELECT * FROM mastery_assessments 
    WHERE learnerId = :learnerId AND masteryStatus = 'REQUIRES_REASSESSMENT' 
    ORDER BY createdAt DESC, id DESC
  """)
  suspend fun getAssessmentsRequiringReassessment(learnerId: String): List<MasteryAssessmentEntity>

  @Query("SELECT * FROM mastery_assessments WHERE capabilityId = :capabilityId ORDER BY createdAt DESC, id DESC")
  fun observeAssessmentsForCapability(capabilityId: String): Flow<List<MasteryAssessmentEntity>>

  @Query("SELECT COUNT(*) FROM mastery_assessments WHERE learnerId = :learnerId AND masteryStatus = 'DEMONSTRATED'")
  fun countDemonstratedAssessments(learnerId: String): Flow<Int>
}
