package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.AegoraTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AegoraCommandCenterDashboardTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testCommandCenterHierarchy_rendersAllPrimarySections() {
    var journeyNavigated = false
    var intelligenceNavigated = false

    composeTestRule.setContent {
      AegoraTheme {
        HomeScreen(
          onNavigateToJourney = { journeyNavigated = true },
          onNavigateToLabs = {},
          onNavigateToAi = {},
          onNavigateToPassport = {},
          onNavigateToLesson = {},
          onNavigateToIntelligence = {},
          onNavigateToCareers = {},
          onNavigateToPersonalIntelligence = { intelligenceNavigated = true }
        )
      }
    }

    // 1. Operator HUD ("Where am I?")
    composeTestRule.onNodeWithTag("home_operator_identity_hud").assertIsDisplayed()
    composeTestRule.onNodeWithText("OPERATOR //", substring = true).assertIsDisplayed()

    // 2. Dominant "NEXT MOVE" Hero ("What should I do next?")
    composeTestRule.onNodeWithTag("home_next_move_hero").assertIsDisplayed()
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_btn_start_next_move"))
    composeTestRule.onNodeWithTag("home_btn_start_next_move").assertIsDisplayed()

    // 3. Compact Cyber Twin ("What can I do?")
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_compact_cyber_twin"))
    composeTestRule.onNodeWithTag("home_compact_cyber_twin").assertIsDisplayed()
    composeTestRule.onNodeWithText("Foundation", substring = true).assertIsDisplayed()
    composeTestRule.onNodeWithText("Active Defense", substring = true).assertIsDisplayed()

    // 4. Cyber Treasure (Verified Career Capital) - scroll to find
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_cyber_treasure"))
    composeTestRule.onNodeWithTag("home_cyber_treasure").assertIsDisplayed()

    // 5. Zero -> Job Ready (Compact Progression Pipeline) - scroll to find
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_zero_to_job_ready"))
    composeTestRule.onNodeWithTag("home_zero_to_job_ready").assertIsDisplayed()

    // 6. Active Mission - scroll to find
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_active_mission"))
    composeTestRule.onNodeWithTag("home_active_mission").assertIsDisplayed()
    composeTestRule.onNodeWithTag("home_btn_continue_active_mission").assertIsDisplayed()

    // 7. Proven Skill (Cryptographic Telemetry Evidence) - scroll to find
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_proven_skill"))
    composeTestRule.onNodeWithTag("home_proven_skill").assertIsDisplayed()

    // 8. Career Signal ("Why does this matter?") - scroll to find
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_career_signal"))
    composeTestRule.onNodeWithTag("home_career_signal").assertIsDisplayed()
  }

  @Test
  fun testNextMove_clickingStart_launchesMissionExecution() {
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

    // Click START on NEXT MOVE
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_btn_start_next_move"))
    composeTestRule.onNodeWithTag("home_btn_start_next_move").performClick()

    // Mission Execution Sheet should now be displayed
    composeTestRule.onNodeWithTag("mission_execution_dialog").assertIsDisplayed()
    composeTestRule.onNodeWithText("TACTICAL OPERATION BRIEFING", substring = true).assertIsDisplayed()
  }

  @Test
  fun testZeroToJobReady_clickingSection_navigatesToJourney() {
    var journeyNavigated = false

    composeTestRule.setContent {
      AegoraTheme {
        HomeScreen(
          onNavigateToJourney = { journeyNavigated = true },
          onNavigateToLabs = {},
          onNavigateToAi = {},
          onNavigateToPassport = {},
          onNavigateToLesson = {},
          onNavigateToIntelligence = {},
          onNavigateToCareers = {}
        )
      }
    }

    // Scroll to Zero to Job Ready section and click
    composeTestRule.onNodeWithTag("home_command_center_list")
      .performScrollToNode(hasTestTag("home_zero_to_job_ready"))
    composeTestRule.onNodeWithTag("home_zero_to_job_ready").performClick()
    assertTrue(journeyNavigated)
  }
}
