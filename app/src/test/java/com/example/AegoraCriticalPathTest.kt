package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.auth.AegoraAuthRepository
import com.example.model.SubscriptionTier
import com.example.subscription.AegoraSubscriptionRepository
import com.example.ui.components.ProofDossierModal
import com.example.ui.screens.DuelArenaScreen
import com.example.ui.screens.IntelligenceCodexScreen
import com.example.ui.screens.OsintAgentChatScreen
import com.example.ui.theme.AegoraTheme
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AegoraCriticalPathTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  /**
   * Test 1: Render DuelArenaScreen, assert the raw SIEM telemetry box exists,
   * click "CHALLENGE AI", and assert the [AI FAILURE DETECTED ✓] state appears.
   */
  @Test
  fun testDuelArena_detectsAiFailureUponChallenge() {
    runBlocking {
      AegoraSubscriptionRepository.syncSubscriptionForUser("test_operator_critical_path")
    }

    composeTestRule.setContent {
      AegoraTheme {
        DuelArenaScreen(
          onNavigateBack = {},
          onShowPaywall = {}
        )
      }
    }

    // 1. Assert raw SIEM telemetry box exists
    val rawSiemBox = composeTestRule.onNodeWithTag("raw_siem_terminal")
    rawSiemBox.assertIsDisplayed()

    // 2. Click "CHALLENGE AI"
    val challengeButton = composeTestRule.onNodeWithTag("duel_challenge_ai_button")
    challengeButton.performClick()

    // 3. Assert [AI FAILURE DETECTED ✓] state appears
    composeTestRule.onNodeWithTag("ai_failure_detected_badge").assertIsDisplayed()
    composeTestRule.onNodeWithText("[AI FAILURE DETECTED ✓]").assertIsDisplayed()
  }

  /**
   * Test 2: Render OsintAgentChatScreen, assert the [🔍 Latest Tech Hackathons] pill exists,
   * click it, and assert a chat bubble is added to the LazyColumn.
   */
  @Test
  fun testOsintAgentChat_clickingHackathonsPillAddsChatBubble() {
    composeTestRule.setContent {
      AegoraTheme {
        OsintAgentChatScreen(
          onNavigateBack = {}
        )
      }
    }

    // 1. Assert the [🔍 Latest Tech Hackathons] pill exists
    val hackathonPill = composeTestRule.onNodeWithTag("pill_latest_tech_hackathons")
    hackathonPill.assertIsDisplayed()

    // 2. Click the pill
    hackathonPill.performClick()

    // 3. Assert a chat bubble containing the query is added to the LazyColumn
    composeTestRule.onNodeWithTag("osint_chat_messages_list").assertIsDisplayed()
    composeTestRule.onNodeWithTag("user_chat_bubble").assertIsDisplayed()
  }

  /**
   * Test 3: Render IntelligenceCodexScreen, click the "Wireshark" grid item,
   * and assert the ModalBottomSheet expands.
   */
  @Test
  fun testIntelligenceCodex_clickingWiresharkExpandsBottomSheet() {
    composeTestRule.setContent {
      AegoraTheme {
        IntelligenceCodexScreen(
          onNavigateBack = {},
          onNavigateToDuel = {}
        )
      }
    }

    // 1. Find and click "Wireshark" grid item
    val wiresharkCard = composeTestRule.onNodeWithTag("codex_card_wireshark")
    wiresharkCard.performScrollTo()
    wiresharkCard.assertIsDisplayed()
    wiresharkCard.performClick()

    // 2. Assert ModalBottomSheet expands and content is rendered
    composeTestRule.onNodeWithTag("codex_bottom_sheet_content").assertIsDisplayed()
    composeTestRule.onNodeWithText("OPERATIONAL DEFINITION").assertIsDisplayed()
  }

  /**
   * Test 4: RevenueCat Commerce & Guest Judge Bypass Validation:
   * Verify "Continue as Guest (Judge Bypass)" assigns the FREE tier,
   * and PRO-tier actions (Biometric Export) trigger the paywall.
   */
  @Test
  fun testRevenueCatCommerce_guestJudgeBypassAndPaywallTrigger() {
    runBlocking {
      AegoraAuthRepository.continueAsJudgeGuest()
    }

    val state = AegoraSubscriptionRepository.subscriptionState.value
    assertEquals("Judge bypass must default strictly to FREE tier", SubscriptionTier.FREE, state.tier)

    var paywallTriggered = false
    composeTestRule.setContent {
      AegoraTheme {
        ProofDossierModal(
          onDismiss = {},
          onShowPaywall = { paywallTriggered = true }
        )
      }
    }

    // Click Biometric Export which is a PRO/CAREER gated action
    val biometricExportBtn = composeTestRule.onNodeWithTag("proof_dossier_biometric_export_btn")
    biometricExportBtn.performScrollTo()
    biometricExportBtn.performClick()

    assertTrue("Clicking Biometric Export without PRO subscription must summon paywall", paywallTriggered)
  }
}
