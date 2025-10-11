package com.example.moneytracker.ui.authScreens.registerScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(firstName = newValue)
    }

    fun onLastNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(lastName = newValue)
    }

    fun onEmailChange(newValue: String) {
        _uiState.value = _uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(password = newValue)
    }

    fun onConfirmPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = newValue)
    }

    fun validateNameBeforeNavigate(): Boolean {
        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName

        // TODO: validation logic

        return true
    }

    fun validateEmailBeforeNavigate(): Boolean {
        val email = _uiState.value.email

        // TODO: validation logic

        return true
    }

    fun validatePasswordBeforeNavigate(): Boolean {
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        // TODO: validation logic

        return true
    }

}