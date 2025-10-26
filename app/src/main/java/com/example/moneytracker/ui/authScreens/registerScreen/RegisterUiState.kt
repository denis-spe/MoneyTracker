package com.example.moneytracker.ui.authScreens.registerScreen

data class RegisterUiState(
    val userId: String = "",
    val firstName: String = "",
    val isErrorInFirstName: Boolean = false,
    val lastName: String = "",
    val isErrorInLastName: Boolean = false,
    val email: String = "",
    val isErrorInEmail: Boolean = false,
    val password: String = "",
    val isErrorInPassword: Boolean = false,
    val confirmPassword: String = "",
    val isErrorInConfirmPassword: Boolean = false,
    val errorMessage: String = ""
)