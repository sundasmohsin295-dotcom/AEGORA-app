package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import com.example.data.AegoraRepository
import com.example.model.PredictiveNextAction
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.AegoraTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MissionInteractionTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testExecuteMissionButtonTriggersMissionFlow() {
    val testAction = PredictiveNextAction(
      id = "act_test_transfer_01",
      title = "Investigate Sysmon ID 1 & 3 Lateral Movement",
      category = "Active Defense",
      destinationTag = "live_soc_range",
      urgencyScore = 95,
      primaryReason = "Blocker: demonstrated correlation under uncertainty requires verification.",
      reasoningTags = listOf("Active Defense", "Incident Triage"),
      estimatedMins = 8,
      xpReward = 350,
      telemetryMetric = "Correlate Sysmon ID 3 & Suricata Alert"
    )

    AegoraRepository.updatePredictiveNextActions(listOf(testAction))

    composeTestRule.setContent {
      AegoraTheme {
        HomeScreen(
          onNavigateToJourney = {},
          onNavigateToLabs = {},
          onNavigateToAi = {},
          onNavigateToPassport = {},
          onNavigateToLesson = {},
          onNavigateToIntelligence = {},
          onNavigateToCareers = {}
        )
      }
    }

    // 1. Verify "EXECUTE MISSION" button exists and is displayed
    val executeButton = composeTestRule.onNodeWithTag("home_btn_start_next_move")
    executeButton.performScrollTo()
    executeButton.assertIsDisplayed()

    // 2. Perform Real Click on "EXECUTE MISSION"
    executeButton.performClick()

    // 3. Verify that clicking opens the Tactical Operation Briefing dialog
    composeTestRule.onNodeWithTag("mission_execution_dialog").assertIsDisplayed()

    // 4. Scroll to and click the "Proceed to Triage & Analysis" button inside the briefing dialog
    val proceedButton = composeTestRule.onNodeWithTag("mission_begin_triage_btn")
    proceedButton.performScrollTo()
    proceedButton.assertIsDisplayed()
    proceedButton.performClick()

    // 5. In Step 2, select choice 0
    val choice0 = composeTestRule.onNodeWithTag("mission_choice_0")
    choice0.performScrollTo()
    choice0.assertIsDisplayed()
    choice0.performClick()

    // 6. Submit decision in Step 2
    val submitButton = composeTestRule.onNodeWithTag("mission_submit_action_btn")
    submitButton.performScrollTo()
    submitButton.assertIsDisplayed()
    submitButton.performClick()

    // 7. Verify that resolution is reached with capability delta and finish button (inside verticalScroll)
    val finishButton = composeTestRule.onNodeWithTag("mission_finish_btn")
    finishButton.performScrollTo()
    finishButton.assertIsDisplayed()
    finishButton.performClick()

    // 8. Verify dialog closes and returns to Command Center
    composeTestRule.onNodeWithTag("home_btn_start_next_move").assertIsDisplayed()
  }
}
