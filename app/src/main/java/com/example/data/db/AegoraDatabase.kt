package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.db.dao.CapabilityDao
import com.example.data.db.dao.CapabilityEvidenceDao
import com.example.data.db.dao.MasteryAssessmentDao

/**
 * Main Room Database for the AEGORA Demonstrated Capability Engine and local persistence.
 */
@Database(
  entities = [
    CapabilityEntity::class,
    CapabilityEvidenceEntity::class,
    MasteryAssessmentEntity::class
  ],
  version = 2,
  exportSchema = false
)
@TypeConverters(CapabilityTypeConverters::class)
abstract class AegoraDatabase : RoomDatabase() {

  abstract fun capabilityDao(): CapabilityDao
  abstract fun capabilityEvidenceDao(): CapabilityEvidenceDao
  abstract fun masteryAssessmentDao(): MasteryAssessmentDao

  companion object {
    @Volatile
    private var INSTANCE: AegoraDatabase? = null

    /**
     * Non-destructive migration from v1 to v2:
     * Converts Boolean historicalCapability (0/1) to Int historicalCapabilityScore (0..100)
     * Preserving all existing learner capability data.
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Create new table matching CapabilityEntity v2 schema
        db.execSQL("""
          CREATE TABLE IF NOT EXISTS `capabilities_new` (
            `id` TEXT NOT NULL PRIMARY KEY,
            `learnerId` TEXT NOT NULL,
            `skillKey` TEXT NOT NULL,
            `name` TEXT NOT NULL,
            `category` TEXT NOT NULL,
            `knowledgeScore` INTEGER NOT NULL,
            `recallScore` INTEGER NOT NULL,
            `applicationScore` INTEGER NOT NULL,
            `investigationScore` INTEGER NOT NULL,
            `transferScore` INTEGER NOT NULL,
            `explanationScore` INTEGER NOT NULL,
            `uncertaintyResilienceScore` INTEGER NOT NULL,
            `independenceScore` INTEGER NOT NULL,
            `evidenceQualityScore` INTEGER NOT NULL,
            `currentConfidence` INTEGER NOT NULL,
            `historicalCapabilityScore` INTEGER NOT NULL,
            `lastVerifiedAt` INTEGER NOT NULL,
            `createdAt` INTEGER NOT NULL,
            `updatedAt` INTEGER NOT NULL,
            `schemaVersion` INTEGER NOT NULL
          )
        """.trimIndent())

        // 2. Transfer data converting historicalCapability (boolean 1) to historicalCapabilityScore (currentConfidence or 85)
        db.execSQL("""
          INSERT INTO `capabilities_new` (
            id, learnerId, skillKey, name, category, knowledgeScore, recallScore,
            applicationScore, investigationScore, transferScore, explanationScore,
            uncertaintyResilienceScore, independenceScore, evidenceQualityScore,
            currentConfidence, historicalCapabilityScore, lastVerifiedAt, createdAt,
            updatedAt, schemaVersion
          )
          SELECT
            id, learnerId, skillKey, name, category, knowledgeScore, recallScore,
            applicationScore, investigationScore, transferScore, explanationScore,
            uncertaintyResilienceScore, independenceScore, evidenceQualityScore,
            currentConfidence,
            CASE WHEN historicalCapability = 1 THEN CASE WHEN currentConfidence > 0 THEN currentConfidence ELSE 85 END ELSE 0 END,
            lastVerifiedAt, createdAt, updatedAt, 2
          FROM `capabilities`
        """.trimIndent())

        // 3. Drop old table
        db.execSQL("DROP TABLE `capabilities`")

        // 4. Rename new table
        db.execSQL("ALTER TABLE `capabilities_new` RENAME TO `capabilities`")

        // 5. Recreate indices
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_capabilities_learnerId_skillKey` ON `capabilities` (`learnerId`, `skillKey`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_capabilities_learnerId` ON `capabilities` (`learnerId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_capabilities_skillKey` ON `capabilities` (`skillKey`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_capabilities_category` ON `capabilities` (`category`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_capabilities_lastVerifiedAt` ON `capabilities` (`lastVerifiedAt`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_capabilities_currentConfidence` ON `capabilities` (`currentConfidence`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_capabilities_historicalCapabilityScore` ON `capabilities` (`historicalCapabilityScore`)")
      }
    }

    fun getInstance(context: Context): AegoraDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AegoraDatabase::class.java,
          "aegora_capability_engine.db"
        )
          .addMigrations(MIGRATION_1_2)
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
