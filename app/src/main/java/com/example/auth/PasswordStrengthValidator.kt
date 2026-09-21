package com.example.auth

/**
 * Military-Grade Password Strength Validator
 * Enforces strict criteria:
 * - Minimum 8 characters
 * - At least 1 Uppercase letter
 * - At least 1 Lowercase letter
 * - At least 1 Digit
 * - At least 1 Special character
 */
enum class PasswordStrengthLevel(val label: String, val score: Float) {
  EMPTY("ENTER PASSWORD", 0.0f),
  WEAK("WEAK CIPHER", 0.25f),
  FAIR("FAIR RESILIENCE", 0.5f),
  GOOD("GOOD DEFENSE", 0.75f),
  UNBREAKABLE("UNBREAKABLE // ENCLAVE GRADE", 1.0f)
}

data class PasswordValidationResult(
  val hasMinLength: Boolean,
  val hasUppercase: Boolean,
  val hasLowercase: Boolean,
  val hasNumber: Boolean,
  val hasSpecialChar: Boolean,
  val missingRequirements: List<String>,
  val strengthLevel: PasswordStrengthLevel
) {
  val isValid: Boolean = hasMinLength && hasUppercase && hasLowercase && hasNumber && hasSpecialChar

  val primaryMissingRequirementLabel: String? = missingRequirements.firstOrNull()?.let { "MISSING: $it" }
}

object PasswordStrengthValidator {

  private val UPPERCASE_REGEX = Regex("[A-Z]")
  private val LOWERCASE_REGEX = Regex("[a-z]")
  private val NUMBER_REGEX = Regex("[0-9]")
  private val SPECIAL_CHAR_REGEX = Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?`~]")

  fun validate(password: String): PasswordValidationResult {
    if (password.isEmpty()) {
      return PasswordValidationResult(
        hasMinLength = false,
        hasUppercase = false,
        hasLowercase = false,
        hasNumber = false,
        hasSpecialChar = false,
        missingRequirements = listOf("MINIMUM 8 CHARACTERS", "UPPERCASE LETTER", "LOWERCASE LETTER", "NUMBER", "SPECIAL CHARACTER"),
        strengthLevel = PasswordStrengthLevel.EMPTY
      )
    }

    val hasMinLength = password.length >= 8
    val hasUppercase = UPPERCASE_REGEX.containsMatchIn(password)
    val hasLowercase = LOWERCASE_REGEX.containsMatchIn(password)
    val hasNumber = NUMBER_REGEX.containsMatchIn(password)
    val hasSpecialChar = SPECIAL_CHAR_REGEX.containsMatchIn(password)

    val missing = mutableListOf<String>()
    if (!hasMinLength) missing.add("MINIMUM 8 CHARACTERS")
    if (!hasUppercase) missing.add("UPPERCASE LETTER")
    if (!hasLowercase) missing.add("LOWERCASE LETTER")
    if (!hasNumber) missing.add("NUMBER (0-9)")
    if (!hasSpecialChar) missing.add("SPECIAL CHARACTER (!@#$...)")

    val passedCriteriaCount = listOf(hasMinLength, hasUppercase, hasLowercase, hasNumber, hasSpecialChar).count { it }

    val strengthLevel = when {
      passedCriteriaCount == 5 -> PasswordStrengthLevel.UNBREAKABLE
      passedCriteriaCount >= 4 -> PasswordStrengthLevel.GOOD
      passedCriteriaCount >= 2 -> PasswordStrengthLevel.FAIR
      else -> PasswordStrengthLevel.WEAK
    }

    return PasswordValidationResult(
      hasMinLength = hasMinLength,
      hasUppercase = hasUppercase,
      hasLowercase = hasLowercase,
      hasNumber = hasNumber,
      hasSpecialChar = hasSpecialChar,
      missingRequirements = missing,
      strengthLevel = strengthLevel
    )
  }
}
