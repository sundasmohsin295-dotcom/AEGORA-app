# AEGORA v8.0 Data Model Reference

```kotlin
// Cyber Twin 2.0 Dynamic State
data class CyberTwinV8State(
  val callsign: String,
  val targetCareerRole: String,
  val overallCareerReadinessPercent: Int,
  val readinessStatus: String,
  val primaryBlocker: String,
  val learningVelocity: String,
  val confidenceCalibrationState: String,
  val evidenceIntegrityScore: Int,
  val vectors: List<CompetencyEvidenceVector>,
  val lastGenomeSyncTimestamp: String
)

// Evidence Proof Vector
data class CompetencyEvidenceVector(
  val dimensionKey: String,
  val title: String,
  val score: Int,
  val benchmarkTarget: Int,
  val confidenceRating: String,
  val whyScoreExists: String,
  val proofCount: Int,
  val recentEvidenceSources: List<String>,
  val trendDescription: String,
  val targetIntervention: String
)

// Purple Team Arena "Self vs Self" State
data class PurpleTeamArenaState(
  val duelId: String,
  val scenarioTitle: String,
  val activePhase: PurpleDuelPhase,
  val redSelectedTtp: String,
  val redC2Technique: String,
  val redPersistenceMethod: String,
  val redStealthScore: Int,
  val blueDetectedArtifactsCount: Int,
  val blueMissedArtifactsCount: Int,
  val containmentSpeedSeconds: Int,
  val wouldHaveCaughtYourselfVerdict: String,
  val debriefSummary: String,
  val isCompleted: Boolean = false
)

// SOC Shift Simulator State
data class SocShiftState(
  val shiftId: String,
  val shiftName: String,
  val analystCallsign: String,
  val elapsedMinutes: Int,
  val totalShiftDurationMinutes: Int = 30,
  val queue: List<SocShiftAlert>,
  val activeAlertIndex: Int = 0,
  val falsePositiveHandlingScore: Int = 0,
  val containmentPrecisionScore: Int = 0,
  val speedUnderPressureScore: Int = 0,
  val isShiftComplete: Boolean = false,
  val shiftDebriefNotes: String = ""
)
```
