package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.auth.PasswordStrengthLevel
import com.example.auth.PasswordStrengthValidator
import com.example.ui.screens.AuthVaultScreen
import com.example.ui.screens.EmailVerificationScreen
import com.example.ui.theme.AegoraTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthVaultSecurityTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun testPasswordStrengthValidator_enforcesStrictRegexCriteria() {
    // 1. Empty
    val emptyResult = PasswordStrengthValidator.validate("")
    assertFalse(emptyResult.isValid)
    assertEquals(PasswordStrengthLevel.EMPTY, emptyResult.strengthLevel)

    // 2. Short / missing criteria
    val weakResult = PasswordStrengthValidator.validate("abc")
    assertFalse(weakResult.isValid)
    assertEquals(PasswordStrengthLevel.WEAK, weakResult.strengthLevel)

    // 3. Length only
    val fairResult = PasswordStrengthValidator.validate("abcdefgh")
    assertFalse(fairResult.isValid)

    // 4. Missing special char
    val goodResult = PasswordStrengthValidator.validate("Abcdefg1")
    assertFalse(goodResult.isValid)
    assertTrue(goodResult.missingRequirements.any { it.contains("SPECIAL") })

    // 5. Unbreakable / valid
    val unbreakableResult = PasswordStrengthValidator.validate("CyberSafe#2026")
    assertTrue(unbreakableResult.isValid)
    assertEquals(PasswordStrengthLevel.UNBREAKABLE, unbreakableResult.strengthLevel)
    assertTrue(unbreakableResult.missingRequirements.isEmpty())
  }

  @Test
  fun testAuthVaultScreen_rendersVaultAndTogglesMode() {
    var authSuccessTriggered = false
    var otpEmailTriggered: String? = null

    composeTestRule.setContent {
      AegoraTheme {
        AuthVaultScreen(
          onAuthSuccess = { authSuccessTriggered = true },
          onNavigateToOtpVerification = { email -> otpEmailTriggered = email }
        )
      }
    }

    // Verify Vault Screen is displayed
    composeTestRule.onNodeWithTag("auth_vault_screen").assertIsDisplayed()
    composeTestRule.onNodeWithText("AUTHENTICATE").assertIsDisplayed()

    // Switch to Establish Identity (Sign Up) mode
    composeTestRule.onNodeWithTag("auth_tab_signup").performClick()
    composeTestRule.onNodeWithText("ESTABLISH IDENTITY").assertIsDisplayed()

    // In signup mode, Password Strength Meter must be present
    composeTestRule.onNodeWithTag("password_strength_meter").assertIsDisplayed()
  }

  @Test
  fun testEmailVerificationScreen_rendersOtpBoxesAndProtocolLabel() {
    var verified = false

    composeTestRule.setContent {
      AegoraTheme {
        EmailVerificationScreen(
          email = "operator.test@aegora.io",
          onVerificationComplete = { verified = true },
          onNavigateBack = {}
        )
      }
    }

    // Verify elements
    composeTestRule.onNodeWithTag("email_verification_screen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("verification_protocol_label").assertIsDisplayed()
    composeTestRule.onNodeWithTag("otp_boxes_row").assertIsDisplayed()
    composeTestRule.onNodeWithText("2FA CRYPTOGRAPHIC HANDSHAKE", substring = true).assertIsDisplayed()
  }
}
