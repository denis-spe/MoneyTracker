// Love the LORD your God with all your heart and with all your soul and
// with all your mind and with all your strength. And love your neighbor as yourself.

package com.example.moneytracker.ui.authScreens.loginScreen

import com.example.moneytracker.backend.auth.User
import kotlinx.coroutines.flow.Flow

data class LoginUiState(
    val user: Flow<User>? = null,
    val email: String = "",
    val password: String = "",
    val isEmailError: Boolean = false,
    val isPasswordError: Boolean = false,
    val emailErrorMessage: String = "",
    val passwordErrorMessage: String = "",
    val credentialErrorMessage: String = "",
    val isLoading: Boolean = false,
)
