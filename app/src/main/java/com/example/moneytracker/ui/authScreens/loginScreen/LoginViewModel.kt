package com.example.moneytracker.ui.authScreens.loginScreen

import androidx.lifecycle.ViewModel
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.helper.isEmailValid
import com.example.moneytracker.helper.isPasswordValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountServices
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun validateBeforeNavigatingToHome(): Boolean {
        val email = _uiState.value.email
        val password = _uiState.value.password

        val emailValidator = email.isEmailValid
        val passwordValidator = password.isPasswordValid

        _uiState.value = _uiState.value.copy(
            emailErrorMessage = emailValidator.errorMessage,
            passwordErrorMessage = passwordValidator.errorMessage
        )

        return emailValidator.isValid && passwordValidator.isValid
    }
}