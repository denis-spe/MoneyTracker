package com.example.moneytracker.ui.authScreens.loginScreen

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val errorMessage: String = ""
)
