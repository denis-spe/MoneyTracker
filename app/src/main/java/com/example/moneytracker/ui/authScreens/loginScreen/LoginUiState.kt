package com.example.moneytracker.ui.authScreens.loginScreen

data class LoginUiState(
    val userId: String = "",
    val email: String = "",
    val password: String = "",
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val emailErrorMessage: String = "",
    val passwordErrorMessage: String = "",
    val credentialErrorMessage: String = "",
    val isLoading: Boolean = false,
)
