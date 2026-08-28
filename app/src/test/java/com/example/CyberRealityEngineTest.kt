package com.example

import com.example.intelligence.CyberRealityEngine
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class CyberRealityEngineTest {

  @Test
  fun testExternalIntelFeedIngestionAndTrust() {
    val feed = CyberRealityEngine.intelFeed.value
    assertTrue("Feed must contain authoritative external intelligence", feed.isNotEmpty())

    val cve = feed.find { it.id == "sig_cve_2026_4401" }
    assertNotNull("CVE-2026-4401 must exist", cve)
    assertEquals(DataSourceTrust.PRIMARY, cve?.sourceTrust)
    assertEquals(DataLiveStatus.CACHED, cve?.liveStatus)
    assertTrue("Confidence rating must be >= 90%", (cve?.confidenceRating ?: 0) >= 90)
    assertTrue("Affected technology must be populated", cve?.affectedTechnology?.isNotEmpty() == true)
  }

  @Test
  fun testEventToLessonTransformationPipeline() {
    val transformations = CyberRealityEngine.eventTransformations.value
    assertTrue("Transformations list must not be empty", transformations.isNotEmpty())

    val trans = transformations.first()
    assertEquals("sig_cve_2026_4401", trans.externalCardId)
    assertTrue("Attack chain must contain at least 3 stages", trans.attackChainSteps.size >= 3)
    assertTrue("Safe practice scenario must contain safety notice", trans.safePracticeScenario.safetyNotice.contains("SANDBOX"))
    assertNotNull("Learner skill gap must be detected", trans.learnerSkillGapDetected)
  }

  @Test
  fun testKnowledgeGraph30Integrity() {
    val nodes = CyberRealityEngine.knowledgeNodes.value
    val edges = CyberRealityEngine.knowledgeEdges.value

    assertTrue("Knowledge Graph must contain 15+ nodes", nodes.size >= 15)
    assertTrue("Knowledge Graph must contain 10+ edges", edges.size >= 10)

    val socRole = nodes.find { it.type == KnowledgeNodeType30.CAREER }
    assertNotNull("SOC Analyst career node must exist", socRole)

    val sysmonNode = nodes.find { it.id == "kn_sysmon" }
    assertNotNull("Sysmon node must exist", sysmonNode)

    val connectedEdge = edges.find { it.sourceId == "kn_sysmon" && it.targetId == "kn_career_soc" }
    assertNotNull("Sysmon must connect to SOC Career node", connectedEdge)
    assertEquals(KnowledgeEdgeType30.RELEVANT_TO, connectedEdge?.edgeType)
  }

  @Test
  fun testJobDescriptionToMultiToolLabSynthesis() {
    val jobText = "Senior SOC Analyst needed. Must have strong Splunk SPL skills, Windows Sysmon EDR analysis, Python scripting, and Sigma detection engineering."
    val result = CyberRealityEngine.generateTrainingSimulationFromJob(jobText)

    assertNotNull("Generated result should not be null", result)
    assertTrue("Match percentage should be realistic (30-95%)", result.overallMatchPercentage in 30..95)
    assertTrue("Extracted skills must contain Splunk", result.extractedSkills.any { it.skillName.contains("Splunk") })
    assertTrue("Custom lab must integrate multiple tools", result.customLab.targetToolsIntegrated.size >= 3)
    assertTrue("Interview questions must be generated", result.interviewQuestions.isNotEmpty())
  }

  @Test
  fun testConsequenceEngineBranching() {
    val immediateIsolation = CyberRealityEngine.evaluateDecisionConsequence("IMMEDIATE_ISOLATION")
    assertTrue("Immediate isolation should be marked optimal", immediateIsolation.isOptimalDecision)
    assertTrue("Detection speed score should be >= 90", immediateIsolation.detectionSpeedScore >= 90)

    val waitForLogs = CyberRealityEngine.evaluateDecisionConsequence("WAIT_FOR_MORE_LOGS")
    assertFalse("Waiting for logs should be marked non-optimal", waitForLogs.isOptimalDecision)
    assertTrue("Should indicate domain compromise", waitForLogs.businessImpactDescription.contains("Domain"))
  }

  @Test
  fun testCyberFeynmanAudienceAdaptation() {
    val beginnerExplanationWithMetaphor = "Kerberos is like an amusement park ticket booth where you get a wristband once."
    val beginnerResult = CyberRealityEngine.evaluateFeynmanExplanation(
      concept = "Kerberos Authentication",
      audience = FeynmanAudience.BEGINNER,
      text = beginnerExplanationWithMetaphor
    )
    assertTrue("Beginner explanation with metaphor should have high adaptation score", beginnerResult.adaptationScore >= 80)
    assertTrue("Beginner explanation without dense jargon should score high", beginnerResult.jargonScore >= 80)

    val cisoExplanation = "This vulnerability introduces significant business downtime risk and potential regulatory fines under PCI-DSS."
    val cisoResult = CyberRealityEngine.evaluateFeynmanExplanation(
      concept = "Kerberos Authentication",
      audience = FeynmanAudience.CISO,
      text = cisoExplanation
    )
    assertTrue("CISO explanation focusing on risk and business impact should score high", cisoResult.adaptationScore >= 80)
  }

  @Test
  fun testPersonalKnowledgeVaultPersistence() {
    val initialCount = CyberRealityEngine.vaultNotes.value.size
    CyberRealityEngine.saveVaultNote(
      title = "New Test Note on Memory Corruption",
      category = "CONCEPT",
      content = "Type confusion occurs when a memory buffer is allocated as one type and accessed as another.",
      tags = listOf("memory", "exploitation", "v8")
    )
    val afterCount = CyberRealityEngine.vaultNotes.value.size
    assertEquals("Vault note count should increment by 1", initialCount + 1, afterCount)
    val latest = CyberRealityEngine.vaultNotes.value.first()
    assertEquals("New Test Note on Memory Corruption", latest.title)
  }
}
