package com.example

import com.example.mesh.MeshThreatFingerprint
import com.example.mesh.SentinelMeshSync
import com.example.security.KernelWatchdog
import com.example.telemetry.DiagnosticSeverity
import com.example.telemetry.DiagnosticStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DiagnosticStoreAndSentinelMeshTest {

  @Before
  fun setUp() {
    DiagnosticStore.clearLogs()
  }

  @Test
  fun testDiagnosticStoreSeverityLevelsAndFiltering() {
    // 1. Record events of each severity
    DiagnosticStore.recordLog(
      severity = DiagnosticSeverity.INFO,
      componentTag = "TestComponent",
      message = "Informational event"
    )
    DiagnosticStore.recordLog(
      severity = DiagnosticSeverity.WARN,
      componentTag = "TestComponent",
      message = "Warning event"
    )
    DiagnosticStore.recordLog(
      severity = DiagnosticSeverity.CRITICAL,
      componentTag = "TestComponent",
      message = "Critical fault event"
    )

    val logs = DiagnosticStore.logEvents.value
    assertEquals(3, logs.size)

    val infoLogs = logs.filter { it.severity == DiagnosticSeverity.INFO }
    val warnLogs = logs.filter { it.severity == DiagnosticSeverity.WARN }
    val critLogs = logs.filter { it.severity == DiagnosticSeverity.CRITICAL }

    assertEquals(1, infoLogs.size)
    assertEquals(1, warnLogs.size)
    assertEquals(1, critLogs.size)
    assertEquals("Critical fault event", critLogs[0].message)
  }

  @Test
  fun testSentinelMeshBroadcastAndVerification() {
    val fingerprint = SentinelMeshSync.broadcastThreatFingerprint(
      threatActor = "TEST_THREAT_ACTOR",
      mitreTechnique = "T1059",
      entropy = 0.88
    )

    assertNotNull(fingerprint)
    assertTrue(fingerprint.signatureHash.startsWith("SHA256:"))
    assertEquals("LOCAL_AEGORA_CORE", fingerprint.originNodeId)

    // Test ingestion
    val isIngested = SentinelMeshSync.verifyAndIngestPeerFingerprint(
      MeshThreatFingerprint(
        id = "TEST-IOC-01",
        threatActor = "MOCK_ACTOR",
        mitreTechnique = "T1003",
        entropyScore = 0.75,
        signatureHash = "SHA256:abc123456",
        timestamp = "12:00:00",
        originNodeId = "PEER-REMOTE"
      )
    )
    assertTrue(isIngested)
  }

  @Test
  fun testKernelWatchdogSelfHealingRecoveryUnder15ms() {
    // Warm up execution once to avoid first-call JVM classloading JIT overhead
    KernelWatchdog.triggerEnclaveRecoverySequence("WARMUP_CALL")

    val initialRecoveries = KernelWatchdog.healthState.value.recoveryCount

    // Trigger recovery sequence
    KernelWatchdog.triggerEnclaveRecoverySequence("TEST_ANOMALY")

    val updatedState = KernelWatchdog.healthState.value
    assertEquals(initialRecoveries + 1, updatedState.recoveryCount)
    assertTrue(updatedState.isJniAllocationSafe)
    assertTrue(updatedState.isMemoryPageIntact)
    assertTrue("Internal watchdog self-healing must satisfy <15ms SLO", updatedState.lastScanLatencyMs <= 15L)
  }
}
