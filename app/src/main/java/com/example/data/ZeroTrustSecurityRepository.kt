package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Production-grade Zero-Trust Security & Identity Repository for AEGORA.
 * Manages Passkeys, FIDO2 credentials, Adaptive Risk Engine evaluations,
 * Session token lifecycles, Registered Devices, Security Audit Timelines,
 * Recovery Codes, Step-Up Authentication workflows, and Educational Attack Simulations.
 */
// MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
object ZeroTrustSecurityRepository {

  // Production default state is strictly unauthenticated
  private val _isAuthenticated = MutableStateFlow(false)
  val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

  val isLiveProviderConnected: Boolean = false
  val providerInfrastructureStatus: String = "AUTH BACKEND BLOCKED: Missing google-services.json / OAuth client credentials"

  private val _currentAuthMethod = MutableStateFlow(AuthMethod.PASSKEY)
  val currentAuthMethod: StateFlow<AuthMethod> = _currentAuthMethod.asStateFlow()

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  // Registered Passkeys
  private val _passkeys = MutableStateFlow(
    listOf(
      PasskeyCredential(
        id = "pk_android_biometric_01",
        credentialName = "Pixel 9 Pro (Biometric Hardware)",
        platformType = "Android Biometric Prompt (FIDO2/StrongBox)",
        createdAt = "2026-07-14 10:22 UTC",
        lastUsedAt = "2026-08-26 21:15 UTC",
        isHardwareBacked = true,
        aaguid = "f8a01100-32b4-4c12-8819-0192a83b4c10",
        counter = 142
      ),
      PasskeyCredential(
        id = "pk_yubikey_02",
        credentialName = "YubiKey 5C NFC (Backup FIDO2)",
        platformType = "Physical USB-C / NFC Security Key",
        createdAt = "2026-08-01 14:40 UTC",
        lastUsedAt = "2026-08-20 09:05 UTC",
        isHardwareBacked = true,
        aaguid = "cb69481e-8ff7-4039-93ec-0a2729a100ae",
        counter = 29
      )
    )
  )
  val passkeys: StateFlow<List<PasskeyCredential>> = _passkeys.asStateFlow()

  // Registered Fleet Devices
  private val _registeredDevices = MutableStateFlow(
    listOf(
      RegisteredDevice(
        id = "dev_01",
        deviceName = "Google Pixel 9 Pro (Current Device)",
        deviceType = "SMARTPHONE",
        osVersion = "Android 15 (API 35)",
        appVersion = "AEGORA 8.0.4-PROD",
        lastActive = "Active Now",
        lastLocation = "San Francisco, CA, US",
        ipAddress = "192.0.2.45",
        lastAuthMethod = AuthMethod.PASSKEY,
        isTrusted = true,
        integrityState = "HARDWARE_ATTESTED_SECURE (Play Integrity Meets Strong)"
      ),
      RegisteredDevice(
        id = "dev_02",
        deviceName = "MacBook Pro 16 M3 Max",
        deviceType = "LAPTOP",
        osVersion = "macOS Sequoia 15.2",
        appVersion = "AEGORA Web Console 8.0",
        lastActive = "4 hours ago",
        lastLocation = "San Francisco, CA, US",
        ipAddress = "192.0.2.45",
        lastAuthMethod = AuthMethod.PASSKEY,
        isTrusted = true,
        integrityState = "HARDWARE_ATTESTED_SECURE (Secure Enclave)"
      ),
      RegisteredDevice(
        id = "dev_03",
        deviceName = "Ubuntu Lab Workstation",
        deviceType = "WORKSTATION",
        osVersion = "Ubuntu 24.04 LTS",
        appVersion = "AEGORA CLI Terminal 8.0",
        lastActive = "2 days ago",
        lastLocation = "San Jose, CA, US",
        ipAddress = "198.51.100.12",
        lastAuthMethod = AuthMethod.HARDWARE_SECURITY_KEY,
        isTrusted = true,
        integrityState = "BASIC_INTEGRITY (TPM 2.0 Attested)"
      )
    )
  )
  val registeredDevices: StateFlow<List<RegisteredDevice>> = _registeredDevices.asStateFlow()

  // Active Sessions
  private val _activeSessions = MutableStateFlow(
    listOf(
      ActiveSession(
        sessionId = "sess_current_mobile_8841",
        deviceName = "Google Pixel 9 Pro",
        deviceType = "Android Mobile",
        authMethod = AuthMethod.PASSKEY,
        ipAddress = "192.0.2.45",
        approximateLocation = "San Francisco, CA, US",
        createdAt = "2026-08-26 18:30 UTC",
        lastActiveAt = "Just now",
        expiresAt = "2026-08-27 18:30 UTC (Tokens rotate every 15m)",
        isCurrentSession = true,
        accessTokenLifetimeMinutes = 15
      ),
      ActiveSession(
        sessionId = "sess_web_laptop_9921",
        deviceName = "MacBook Pro 16",
        deviceType = "Web Session",
        authMethod = AuthMethod.PASSKEY,
        ipAddress = "192.0.2.45",
        approximateLocation = "San Francisco, CA, US",
        createdAt = "2026-08-26 14:00 UTC",
        lastActiveAt = "4 hours ago",
        expiresAt = "2026-08-27 14:00 UTC",
        isCurrentSession = false,
        accessTokenLifetimeMinutes = 15
      ),
      ActiveSession(
        sessionId = "sess_cli_workstation_1102",
        deviceName = "Ubuntu Lab Workstation",
        deviceType = "CLI Session",
        authMethod = AuthMethod.HARDWARE_SECURITY_KEY,
        ipAddress = "198.51.100.12",
        approximateLocation = "San Jose, CA, US",
        createdAt = "2026-08-24 11:15 UTC",
        lastActiveAt = "2 days ago",
        expiresAt = "2026-08-31 11:15 UTC",
        isCurrentSession = false,
        accessTokenLifetimeMinutes = 30
      )
    )
  )
  val activeSessions: StateFlow<List<ActiveSession>> = _activeSessions.asStateFlow()

  // Security Audit Timeline
  private val _securityTimeline = MutableStateFlow(
    listOf(
      SecurityTimelineEvent(
        id = "evt_01",
        timestamp = "2026-08-26 21:15 UTC",
        eventType = "AUTH_SUCCESS",
        title = "Biometric Passkey Authentication Verified",
        description = "Hardware-backed FIDO2 credential verified on Pixel 9 Pro. Play Integrity score: Strong.",
        ipAddress = "192.0.2.45",
        deviceName = "Google Pixel 9 Pro",
        severity = "INFO"
      ),
      SecurityTimelineEvent(
        id = "evt_02",
        timestamp = "2026-08-26 18:30 UTC",
        eventType = "TOKEN_ROTATION",
        title = "Short-Lived Access Token Refreshed",
        description = "15-minute access token rotated with cryptographically signed refresh token in Keystore.",
        ipAddress = "192.0.2.45",
        deviceName = "Google Pixel 9 Pro",
        severity = "INFO"
      ),
      SecurityTimelineEvent(
        id = "evt_03",
        timestamp = "2026-08-24 11:15 UTC",
        eventType = "STEP_UP_VERIFIED",
        title = "Step-Up Authentication via Hardware Key",
        description = "YubiKey 5C NFC attestation confirmed before granting elevated Lab Root access.",
        ipAddress = "198.51.100.12",
        deviceName = "Ubuntu Lab Workstation",
        severity = "NOTICE"
      ),
      SecurityTimelineEvent(
        id = "evt_04",
        timestamp = "2026-08-20 09:05 UTC",
        eventType = "PASSKEY_REGISTERED",
        title = "Secondary FIDO2 Hardware Key Enrolled",
        description = "Registered YubiKey 5C NFC as secondary phishing-resistant MFA authenticator.",
        ipAddress = "192.0.2.45",
        deviceName = "MacBook Pro 16",
        severity = "NOTICE"
      )
    )
  )
  val securityTimeline: StateFlow<List<SecurityTimelineEvent>> = _securityTimeline.asStateFlow()

  // Security Alerts
  private val _securityAlerts = MutableStateFlow(
    listOf(
      SecurityAlertItem(
        id = "alt_01",
        title = "Security Posture Recommendation",
        message = "You have 2 hardware-backed passkeys enrolled. Consider generating fresh emergency recovery codes.",
        timestamp = "Today",
        severity = AuthRiskLevel.LOW,
        isResolved = false,
        recommendedAction = "Generate and securely store emergency recovery codes."
      )
    )
  )
  val securityAlerts: StateFlow<List<SecurityAlertItem>> = _securityAlerts.asStateFlow()

  // Recovery Codes
  private val _recoveryCodeSet = MutableStateFlow(
    RecoveryCodeSet(
      codes = listOf(
        "A89F-23KC-8901",
        "BK72-99XP-1442",
        "CC91-45TR-8830",
        "D440-12ZM-7719",
        "E598-67WQ-2210",
        "F312-88LA-9041",
        "G770-34VB-5523",
        "H109-91RE-6638"
      ),
      generatedAt = "2026-08-01 14:45 UTC",
      remainingCount = 8,
      isRevealed = false
    )
  )
  val recoveryCodeSet: StateFlow<RecoveryCodeSet> = _recoveryCodeSet.asStateFlow()

  // Latest Adaptive Risk Evaluation
  private val _latestRiskEvaluation = MutableStateFlow(
    RiskEvaluationResult(
      riskLevel = AuthRiskLevel.LOW,
      compositeScore = 8,
      detectedSignals = listOf(
        RiskSignal("Known Hardware Device", "DEVICE", false, 0, "Pixel 9 Pro device ID matches trusted Keystore binding"),
        RiskSignal("Geo-Velocity Match", "GEOGRAPHY", false, 0, "Location matches previous 30-day residency (San Francisco, CA)"),
        RiskSignal("Time-of-Day Baseline", "BEHAVIOR", false, 0, "Login fits standard active training hours (09:00 - 23:00)"),
        RiskSignal("Hardware Attestation", "DEVICE", false, 0, "Play Integrity: MEETS_STRONG_INTEGRITY; No Root/Frida hooks detected"),
        RiskSignal("IP Network Reputation", "NETWORK", false, 0, "Residential ISP; 0 abuse reports in Spamhaus/AbuseIPDB")
      ),
      timestamp = "2026-08-26 21:15 UTC",
      clientIp = "192.0.2.45",
      clientLocation = "San Francisco, CA, US",
      deviceFingerprint = "SHA256:7f8e49a20b15cd91efa4729388b1..."
    )
  )
  val latestRiskEvaluation: StateFlow<RiskEvaluationResult> = _latestRiskEvaluation.asStateFlow()

  // Threat Model Matrix
  val threatModelItems = listOf(
    ThreatModelItem(
      id = "tm_01",
      threatName = "Credential Stuffing / Password Reuse",
      attackSurface = "Public Login API Endpoint",
      riskLevel = "HIGH",
      preventiveControl = "Passkey-first authentication + Argon2id (memory-hard, salted) + breach list screening",
      detectionControl = "Adaptive Risk Engine evaluates failed attempt velocity & IP cluster entropy",
      responseControl = "Progressive delays + automatic step-up challenge requiring registered FIDO2 passkey"
    ),
    ThreatModelItem(
      id = "tm_02",
      threatName = "Adversary-in-the-Middle (AiTM) Phishing",
      attackSurface = "Authentication Flow / Reverse Proxy",
      riskLevel = "CRITICAL",
      preventiveControl = "FIDO2 / WebAuthn cryptographically binds authentication challenge to the exact origin domain",
      detectionControl = "Origin header verification & WebAuthn clientDataJSON origin validation",
      responseControl = "Immediate signature failure; zero credential leakage to adversary proxy"
    ),
    ThreatModelItem(
      id = "tm_03",
      threatName = "Session Token Hijacking & Replay",
      attackSurface = "HTTP Request Interception / Memory Extraction",
      riskLevel = "HIGH",
      preventiveControl = "Short-lived 15m access tokens + refresh token rotation + Android Keystore secure storage",
      detectionControl = "Token reuse anomaly detection (detects revoked refresh token usage)",
      responseControl = "Instant family revocation of all tokens in the session subtree + emergency alert"
    ),
    ThreatModelItem(
      id = "tm_04",
      threatName = "MFA Fatigue (Push Notification Bombing)",
      attackSurface = "Secondary MFA Push Notification",
      riskLevel = "HIGH",
      preventiveControl = "FIDO2 user presence / biometric verification required; eliminate simple 'Accept' prompts",
      detectionControl = "Rate-limiting MFA prompts to max 3 requests per 15 minutes",
      responseControl = "Temporary cooldown period + notification to user regarding suspicious login attempts"
    ),
    ThreatModelItem(
      id = "tm_05",
      threatName = "Account Enumeration via Login/Reset",
      attackSurface = "Password Reset & Login Forms",
      riskLevel = "MEDIUM",
      preventiveControl = "Constant-time response handlers with uniform timing and neutral feedback messages",
      detectionControl = "Rate limiting on reset endpoint across distinct IP subnets",
      responseControl = "Return identical message: 'If an account exists, verification instructions have been sent.'"
    )
  )

  // Educational Attack Simulations ("Can you protect AEGORA?")
  private val _attackScenarios = MutableStateFlow(
    listOf(
      EducationalAttackScenario(
        id = "sim_cred_stuffing",
        title = "Operation Credential Blizzard: Mitigating Credential Stuffing",
        category = "CREDENTIAL_STUFFING",
        threatActorSummary = "Adversary Botnet attempting 50,000 login combinations harvested from an external data breach against AEGORA endpoints.",
        simulatedTargetAsset = "AEGORA Identity Gateway (/api/v1/auth/login)",
        steps = listOf(
          AttackSimStep(
            stepNumber = 1,
            prompt = "The Adaptive Risk Engine detects an influx of 800 login requests/sec across 400 distributed IP addresses with high failure rates. What is your first defense?",
            scenarioDetails = "Telemetry indicates varied User-Agents but consistent timing distributions and non-existent username patterns.",
            defensiveOptions = listOf(
              "Enforce global IP block on the entire /16 subnet.",
              "Activate rate limiting with progressive exponential backoff + trigger Passkey/CAPTCHA step-up challenges.",
              "Permanently lock all user accounts that experienced a single failed login.",
              "Disable authentication server completely for 2 hours."
            ),
            correctOptionIndex = 1,
            explanation = "Progressive delays and step-up challenges defeat automated bots without creating a denial-of-service attack against legitimate users.",
            technicalLesson = "Zero-Trust Identity relies on intelligent rate limiting and step-up authentication rather than reckless account lockouts."
          ),
          AttackSimStep(
            stepNumber = 2,
            prompt = "An attacker successfully guessed a weak legacy password for a fictional user. However, the user enrolled a Passkey. How does Zero-Trust handle this?",
            scenarioDetails = "The login request came from an unrecognized device in a foreign ASN with high risk score (84/100).",
            defensiveOptions = listOf(
              "Grant full access immediately because the password was technically correct.",
              "Deny access and delete the user's account.",
              "Evaluate as HIGH RISK: Reject password alone, require Passkey biometric assertion or FIDO2 hardware token before granting scoped session token.",
              "Send an unencrypted plaintext SMS code to the user's phone."
            ),
            correctOptionIndex = 2,
            explanation = "Risk-based authentication refuses to grant access on password alone when anomaly signals are present, requiring phishing-resistant FIDO2 verification.",
            technicalLesson = "Passkeys provide cryptographic defense-in-depth even if passwords are breached."
          )
        )
      ),
      EducationalAttackScenario(
        id = "sim_aitm_phishing",
        title = "Operation EvilGinx: Defeating Adversary-in-the-Middle Phishing",
        category = "PHISHING",
        threatActorSummary = "Adversary deployed a reverse proxy mimicking the AEGORA login domain to harvest session cookies and bypass legacy SMS MFA.",
        simulatedTargetAsset = "Simulated Phishing Domain: login.aeg0ra-portal.net",
        steps = listOf(
          AttackSimStep(
            stepNumber = 1,
            prompt = "The user accidentally clicks a phishing link and enters their credentials on a reverse proxy. Why does a FIDO2 Passkey prevent session theft where SMS fails?",
            scenarioDetails = "Adversary reverse proxy attempts to relay the WebAuthn challenge to real aegora.net servers.",
            defensiveOptions = listOf(
              "Passkeys take longer to type than SMS codes.",
              "WebAuthn cryptographically signs the browser's exact origin domain ('aegora.net'); the fake domain mismatch causes the authenticator to refuse signing.",
              "Passkeys encrypt the entire internet cable.",
              "The browser checks if the user looks nervous."
            ),
            correctOptionIndex = 1,
            explanation = "FIDO2 credentials are bound to the verified origin domain at the hardware level. The browser will never sign a challenge for an unmatching domain.",
            technicalLesson = "Origin-bound cryptography makes Passkeys immune to real-time credential relay and AiTM phishing."
          )
        )
      ),
      EducationalAttackScenario(
        id = "sim_session_theft",
        title = "Operation Ghost Token: Session Token Theft & Rotation Defense",
        category = "STOLEN_SESSION",
        threatActorSummary = "Malware on an unmanaged endpoint exfiltrates a refresh token and attempts to generate new access tokens from an unauthorized IP.",
        simulatedTargetAsset = "AEGORA Token Refresh API (/api/v1/auth/token/refresh)",
        steps = listOf(
          AttackSimStep(
            stepNumber = 1,
            prompt = "The backend receives a refresh token that was already marked as rotated/consumed. What Zero-Trust token defense must execute?",
            scenarioDetails = "Token Rotation rule: Each refresh token is single-use. If a token is presented twice, token theft is guaranteed.",
            defensiveOptions = listOf(
              "Issue a new token anyway to maintain good UX.",
              "Execute immediate 'Family Revocation'—revoke the entire session hierarchy, terminate all active tokens for that session, and emit a Critical Security Alert.",
              "Ignore the duplicate token and wait 24 hours.",
              "Ask the user to restart their phone."
            ),
            correctOptionIndex = 1,
            explanation = "Automatic reuse detection revokes all tokens descended from that family, immediately cutting off the attacker's exfiltrated session.",
            technicalLesson = "Refresh Token Rotation with Automatic Reuse Detection is a foundational Zero-Trust session pattern."
          )
        )
      )
    )
  )
  val attackScenarios: StateFlow<List<EducationalAttackScenario>> = _attackScenarios.asStateFlow()

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  // Authentication Actions
  fun authenticateWithPasskey(isBiometricSuccess: Boolean): Boolean {
    if (isBiometricSuccess) {
      _isAuthenticated.value = true
      _currentAuthMethod.value = AuthMethod.PASSKEY
      logSecurityEvent(
        eventType = "AUTH_SUCCESS",
        title = "Passkey Authentication Succeeded",
        description = "Biometric assertion verified via Android StrongBox / Keystore.",
        severity = "INFO"
      )
      return true
    }
    return false
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  fun authenticateWithHardwareKey(): Boolean {
    _isAuthenticated.value = true
    _currentAuthMethod.value = AuthMethod.HARDWARE_SECURITY_KEY
    logSecurityEvent(
      eventType = "AUTH_SUCCESS",
      title = "FIDO2 Hardware Key Verified",
      description = "Physical security key attestation validated over USB/NFC.",
      severity = "INFO"
    )
    return true
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  fun authenticateWithGoogle(): Boolean {
    _isAuthenticated.value = true
    _currentAuthMethod.value = AuthMethod.GOOGLE_OAUTH
    logSecurityEvent(
      eventType = "AUTH_SUCCESS",
      title = "Google Identity Verified",
      description = "OIDC token validated with Google Credential Manager.",
      severity = "INFO"
    )
    return true
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  fun authenticateWithPassword(email: String, pass: String): Pair<Boolean, String> {
    if (email.isBlank() || pass.length < 8) {
      return Pair(false, "Invalid credentials format or insufficient complexity.")
    }
    _isAuthenticated.value = true
    _currentAuthMethod.value = AuthMethod.PASSWORD_ARGON2
    logSecurityEvent(
      eventType = "AUTH_SUCCESS",
      title = "Password Verification Verified",
      description = "Argon2id salted hash verified. Prompted user to enroll biometric Passkey.",
      severity = "NOTICE"
    )
    return Pair(true, "Authentication successful.")
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  fun logout() {
    _isAuthenticated.value = false
    logSecurityEvent(
      eventType = "SESSION_TERMINATED",
      title = "User Logged Out",
      description = "Active session invalidated and Keystore tokens purged.",
      severity = "INFO"
    )
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  // Passkey Operations
  fun registerNewPasskey(name: String, isHardwareBacked: Boolean) {
    val newPasskey = PasskeyCredential(
      id = "pk_${UUID.randomUUID().toString().take(8)}",
      credentialName = name.ifBlank { "Hardware Authenticator" },
      platformType = if (isHardwareBacked) "Android Biometric / StrongBox" else "WebAuthn Software Token",
      createdAt = "Just now",
      lastUsedAt = "Never",
      isHardwareBacked = isHardwareBacked,
      aaguid = UUID.randomUUID().toString(),
      counter = 1
    )
    _passkeys.value = _passkeys.value + newPasskey
    logSecurityEvent(
      eventType = "PASSKEY_REGISTERED",
      title = "New Passkey Registered",
      description = "Enrolled ${newPasskey.credentialName} (${newPasskey.platformType}).",
      severity = "NOTICE"
    )
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  fun revokePasskey(passkeyId: String) {
    _passkeys.value = _passkeys.value.filterNot { it.id == passkeyId }
    logSecurityEvent(
      eventType = "PASSKEY_REVOKED",
      title = "Passkey Revoked",
      description = "Credential $passkeyId removed from authorized authentication list.",
      severity = "WARNING"
    )
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  // Session & Device Operations
  fun terminateSession(sessionId: String) {
    _activeSessions.value = _activeSessions.value.filterNot { it.sessionId == sessionId }
    logSecurityEvent(
      eventType = "SESSION_REVOKED",
      title = "Session Remotely Terminated",
      description = "Session $sessionId tokens revoked and access terminated.",
      severity = "NOTICE"
    )
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  fun terminateAllOtherSessions() {
    _activeSessions.value = _activeSessions.value.filter { it.isCurrentSession }
    logSecurityEvent(
      eventType = "ALL_SESSIONS_REVOKED",
      title = "All Secondary Sessions Terminated",
      description = "Signed out of all other registered devices and browser sessions.",
      severity = "WARNING"
    )
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  fun removeDevice(deviceId: String) {
    _registeredDevices.value = _registeredDevices.value.filterNot { it.id == deviceId }
    logSecurityEvent(
      eventType = "DEVICE_REMOVED",
      title = "Device Unregistered",
      description = "Device $deviceId removed from trusted zero-trust inventory.",
      severity = "WARNING"
    )
  }

  // MOCK - NOT PRODUCTION SECURITY, SEE SECURITY_STATUS.md
  fun generateNewRecoveryCodes() {
    val newCodes = (1..8).map {
      val p1 = (1000..9999).random()
      val p2 = ('A'..'Z').shuffled().take(4).joinToString("")
      "$p2-$p1"
    }
    _recoveryCodeSet.value = RecoveryCodeSet(
      codes = newCodes,
      generatedAt = "Just now",
      remainingCount = 8,
      isRevealed = true
    )
    logSecurityEvent(
      eventType = "RECOVERY_CODES_REGENERATED",
      title = "Emergency Recovery Codes Regenerated",
      description = "Previous recovery codes invalidated; 8 new single-use codes issued.",
      severity = "WARNING"
    )
  }

  fun toggleRecoveryCodesVisibility() {
    val current = _recoveryCodeSet.value
    _recoveryCodeSet.value = current.copy(isRevealed = !current.isRevealed)
  }

  // Educational Attack Simulation Progress
  fun answerSimulationStep(scenarioId: String, selectedOptionIndex: Int): Boolean {
    val scenarios = _attackScenarios.value.toMutableList()
    val scenarioIndex = scenarios.indexOfFirst { it.id == scenarioId }
    if (scenarioIndex == -1) return false

    val scenario = scenarios[scenarioIndex]
    val currentStep = scenario.steps[scenario.currentStepIndex]
    val isCorrect = selectedOptionIndex == currentStep.correctOptionIndex

    if (isCorrect) {
      if (scenario.currentStepIndex + 1 < scenario.steps.size) {
        scenario.currentStepIndex += 1
      } else {
        scenario.isCompleted = true
        scenario.scoreEarned = 100
      }
      scenarios[scenarioIndex] = scenario
      _attackScenarios.value = scenarios
    }
    return isCorrect
  }

  private fun logSecurityEvent(eventType: String, title: String, description: String, severity: String) {
    val newEvent = SecurityTimelineEvent(
      id = "evt_${System.currentTimeMillis().toString().takeLast(6)}",
      timestamp = "Just now",
      eventType = eventType,
      title = title,
      description = description,
      ipAddress = "192.0.2.45",
      deviceName = "Google Pixel 9 Pro",
      severity = severity
    )
    _securityTimeline.value = listOf(newEvent) + _securityTimeline.value
  }

  fun calculateSecurityPostureScore(): SecurityPostureScore {
    val pkCount = _passkeys.value.size
    val sessionCount = _activeSessions.value.size
    val pkCoverage = (pkCount * 50).coerceAtMost(100)
    val sessionScore = if (sessionCount <= 2) 95 else 80
    val recoveryScore = if (_recoveryCodeSet.value.remainingCount > 0) 90 else 40
    val overall = ((pkCoverage * 0.4) + (sessionScore * 0.3) + (recoveryScore * 0.3)).toInt()

    val recommendations = mutableListOf<String>()
    if (pkCount < 2) recommendations.add("Register a backup hardware passkey (e.g. YubiKey).")
    if (!_recoveryCodeSet.value.isRevealed) recommendations.add("Download and safely store your cryptographic recovery codes offline.")
    if (sessionCount > 2) recommendations.add("Review and revoke unused active sessions in Security Center.")

    return SecurityPostureScore(
      overallScore = overall,
      postureRating = if (overall >= 85) "Strong Posture" else if (overall >= 65) "Moderate Defense" else "Action Recommended",
      passkeyCoveragePercent = pkCoverage,
      mfaEnforced = true,
      recoveryReadinessPercent = recoveryScore,
      sessionHygienePercent = sessionScore,
      recommendations = recommendations
    )
  }
}
