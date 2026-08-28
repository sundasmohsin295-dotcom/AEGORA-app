package com.example.model

// MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
/**
 * Authentication Method supported by AEGORA Zero-Trust Architecture.
 * (For real production security status audit, see SECURITY_STATUS.md)
 */
enum class AuthMethod(val displayName: String, val securityTier: String, val isHardwareBacked: Boolean) {
  PASSKEY("Passkey (Biometric / Screen Lock)", "Tier 1 - Phishing-Resistant FIDO2", true),
  HARDWARE_SECURITY_KEY("FIDO2 / WebAuthn Hardware Key", "Tier 1 - Physical Attestation (YubiKey/Titan)", true),
  GOOGLE_OAUTH("Google Identity / Credential Manager", "Tier 2 - OAuth 2.0 / OIDC", false),
  TOTP_MFA("Time-based One-Time Password (TOTP)", "Tier 2 - Authenticator App", false),
  PASSWORD_ARGON2("Email + Password (Argon2id)", "Tier 3 - Memory-Hard Salted Hash", false),
  RECOVERY_CODE("Cryptographic Emergency Recovery Code", "Tier 2 - Single-Use Token", false)
}

/**
 * Adaptive Risk Engine Evaluation Levels.
 */
enum class AuthRiskLevel(val label: String, val badgeColorHex: Long, val actionRequired: String) {
  LOW("LOW RISK", 0xFF00E676, "Normal authenticated session granted with standard token lifetime."),
  MEDIUM("MEDIUM RISK", 0xFFFFD700, "Step-up verification required via registered Passkey or TOTP."),
  HIGH("HIGH RISK", 0xFFFF9100, "Strict phishing-resistant MFA required before granting scoped access."),
  CRITICAL("CRITICAL RISK", 0xFFFF1744, "Authentication blocked. Emergency security incident workflow triggered.")
}

/**
 * Real-time Adaptive Risk Signal detected during authentication.
 */
data class RiskSignal(
  val signalName: String,
  val category: String, // "DEVICE", "NETWORK", "GEOGRAPHY", "BEHAVIOR", "REPUTATION"
  val isAnomalous: Boolean,
  val weightScore: Int, // 0 - 100
  val telemetryDetails: String
)

/**
 * Complete Evaluation from AEGORA Identity Risk Engine.
 */
data class RiskEvaluationResult(
  val riskLevel: AuthRiskLevel,
  val compositeScore: Int, // 0 (safest) to 100 (highest risk)
  val detectedSignals: List<RiskSignal>,
  val timestamp: String,
  val clientIp: String,
  val clientLocation: String,
  val deviceFingerprint: String
)

/**
 * Registered Passkey Credential (FIDO2 / WebAuthn standard).
 */
data class PasskeyCredential(
  val id: String,
  val credentialName: String,
  val platformType: String, // "Android Biometric Prompt", "YubiKey 5 NFC", "Titan Security Key", "macOS TouchID"
  val createdAt: String,
  val lastUsedAt: String,
  val isHardwareBacked: Boolean,
  val aaguid: String,
  val counter: Long,
  val isRevoked: Boolean = false
)

/**
 * Registered Device within the Zero-Trust Fleet.
 */
data class RegisteredDevice(
  val id: String,
  val deviceName: String,
  val deviceType: String, // "SMARTPHONE", "LAPTOP", "TABLET", "WORKSTATION"
  val osVersion: String,
  val appVersion: String,
  val lastActive: String,
  val lastLocation: String,
  val ipAddress: String,
  val lastAuthMethod: AuthMethod,
  val isTrusted: Boolean,
  val integrityState: String // "HARDWARE_ATTESTED_SECURE", "STRONG_PLAY_INTEGRITY", "BASIC_INTEGRITY"
)

/**
 * Active Session with Controlled Lifetime and Token Rotation.
 */
data class ActiveSession(
  val sessionId: String,
  val deviceName: String,
  val deviceType: String,
  val authMethod: AuthMethod,
  val ipAddress: String,
  val approximateLocation: String,
  val createdAt: String,
  val lastActiveAt: String,
  val expiresAt: String,
  val isCurrentSession: Boolean,
  val accessTokenLifetimeMinutes: Int = 15,
  val isRevoked: Boolean = false
)

/**
 * Tamper-Evident Security Audit Event.
 */
data class SecurityTimelineEvent(
  val id: String,
  val timestamp: String,
  val eventType: String, // "AUTH_SUCCESS", "PASSKEY_REGISTERED", "MFA_ENABLED", "NEW_DEVICE", "STEP_UP_VERIFIED", "SESSION_REVOKED"
  val title: String,
  val description: String,
  val ipAddress: String,
  val deviceName: String,
  val severity: String, // "INFO", "NOTICE", "WARNING", "ALERT"
  val requiresReview: Boolean = false
)

/**
 * Security Notification / Alert.
 */
data class SecurityAlertItem(
  val id: String,
  val title: String,
  val message: String,
  val timestamp: String,
  val severity: AuthRiskLevel,
  val isResolved: Boolean = false,
  val recommendedAction: String
)

/**
 * Single-use Cryptographically Secure Recovery Codes.
 */
data class RecoveryCodeSet(
  val codes: List<String>,
  val generatedAt: String,
  val remainingCount: Int,
  val isRevealed: Boolean = false
)

/**
 * Comprehensive Account Security Posture Score.
 */
data class SecurityPostureScore(
  val overallScore: Int, // 0 - 100
  val postureRating: String, // "Strong Posture", "Optimal Defense", "Action Needed"
  val passkeyCoveragePercent: Int,
  val mfaEnforced: Boolean,
  val recoveryReadinessPercent: Int,
  val sessionHygienePercent: Int,
  val recommendations: List<String>
)

/**
 * Formal Threat Model Mapping.
 */
data class ThreatModelItem(
  val id: String,
  val threatName: String,
  val attackSurface: String,
  val riskLevel: String,
  val preventiveControl: String,
  val detectionControl: String,
  val responseControl: String
)

/**
 * Educational Attack Simulation Step.
 */
data class AttackSimStep(
  val stepNumber: Int,
  val prompt: String,
  val scenarioDetails: String,
  val defensiveOptions: List<String>,
  val correctOptionIndex: Int,
  val explanation: String,
  val technicalLesson: String
)

/**
 * Educational Attack Scenario ("Can you protect AEGORA?").
 */
data class EducationalAttackScenario(
  val id: String,
  val title: String,
  val category: String, // "CREDENTIAL_STUFFING", "PHISHING", "STOLEN_SESSION", "MFA_FATIGUE", "DEVICE_COMPROMISE"
  val threatActorSummary: String,
  val simulatedTargetAsset: String,
  val steps: List<AttackSimStep>,
  var currentStepIndex: Int = 0,
  var isCompleted: Boolean = false,
  var scoreEarned: Int = 0
)
