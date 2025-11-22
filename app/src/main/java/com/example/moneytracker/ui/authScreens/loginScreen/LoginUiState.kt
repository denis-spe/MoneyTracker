// Love the LORD your God with all your heart and with all your soul and
// with all your mind and with all your strength. And love your neighbor as yourself.

package com.example.moneytracker.ui.authScreens.loginScreen

/**
 * UI state for the Login Screen
 * @param email The email input
 * @param password The password input
 * @param isEmailError Whether there is an email error
 * @param isPasswordError Whether there is a password error
 * @param emailErrorMessage The email error message
 * @param passwordErrorMessage The password error message
 * @param credentialErrorMessage The credential error message
 * @param isLoading Whether the screen is loading
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val emailErrorMessage: String = "",
    val passwordErrorMessage: String = "",
    val credentialErrorMessage: String = "",
    val isLoading: Boolean = false,
)
