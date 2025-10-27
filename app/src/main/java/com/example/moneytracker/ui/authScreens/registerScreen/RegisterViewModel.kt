package com.example.moneytracker.ui.authScreens.registerScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.helper.isEmailValid
import com.example.moneytracker.helper.isPasswordValid
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountService: AccountServices
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(
            firstName = newValue,
            isErrorInFirstName = false,
            firstNameErrorMessage = ""
        )

    }

    fun onLastNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(
            lastName = newValue,
            isErrorInLastName = false,
            lastNameErrorMessage = ""
        )
    }

    fun onEmailChange(newValue: String) {
        _uiState.value = _uiState.value.copy(
            email = newValue,
            isErrorInEmail = false,
            emailErrorMessage = ""
        )
    }

    fun onPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(
            password = newValue,
            isErrorInPassword = false,
            passwordErrorMessage = "",
            credentialErrorMessage = ""
        )
    }

    fun onConfirmPasswordChange(newValue: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = newValue,
            isErrorInFirstName = false,
            isErrorInConfirmPassword = false,
            firstNameErrorMessage = "",
            confirmPasswordErrorMessage = "",
            credentialErrorMessage = ""
        )
    }

    fun validateNameBeforeNavigateToEmail(block: () -> Unit): Boolean {
        // Reset previous errors
        _uiState.value = _uiState.value.copy(
            isErrorInFirstName = false,
            isErrorInLastName = false,
            firstNameErrorMessage = "",
            credentialErrorMessage = ""
        )

        // Get current values
        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName

        if (firstName.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isErrorInFirstName = true,
                firstNameErrorMessage = "First name cannot be empty."
            )
            return false
        }

        if (firstName.isNotEmpty() && lastName == firstName) {
            _uiState.value = _uiState.value.copy(
                isErrorInLastName = true,
                lastNameErrorMessage = "Last name cannot be the same as first name."
            )
            return false
        }

        block()
        return true

    }

    fun validateEmailBeforeNavigateToPassword(block: () -> Unit): Boolean {
        // Reset previous errors
        _uiState.value = _uiState.value.copy(
            isErrorInEmail = false,
            emailErrorMessage = "",
        )

        // Get current input values
        val email = _uiState.value.email

        // Validate inputs
        val emailValidator = email.isEmailValid

        // Update UI state with validation results
        _uiState.value = _uiState.value.copy(
            emailErrorMessage = emailValidator.errorMessage,
        )

        if (!emailValidator.isValid) {
            _uiState.value = _uiState.value.copy(
                isErrorInEmail = true,
            )
            return false
        }
        block()
        return true
    }

    fun validatePasswordBeforeNavigateToHome(block: (userId: String) -> Unit): Boolean {
        // Prepare UI State
        _uiState.value = _uiState.value.copy(
            isErrorInPassword = false,
            isErrorInConfirmPassword = false,
            credentialErrorMessage = "",
        )

        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName
        val email = _uiState.value.email
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        // Validate inputs
        val passwordValidator = password.isPasswordValid
        val confirmPasswordValidator = confirmPassword.isPasswordValid

        // Update UI state with validation results
        _uiState.value = _uiState.value.copy(
            passwordErrorMessage = passwordValidator.errorMessage,
            confirmPasswordErrorMessage = confirmPasswordValidator.errorMessage
        )

        if (!confirmPasswordValidator.isValid || !passwordValidator.isValid) {
            _uiState.value = _uiState.value.copy(
                isErrorInConfirmPassword = !confirmPasswordValidator.isValid,
                isErrorInPassword = !passwordValidator.isValid
            )
            return false
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                isErrorInPassword = true,
                isErrorInConfirmPassword = true,
                credentialErrorMessage = "Passwords do not match.",
            )
            return false
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                Log.d("RegisterViewModel", "Registering user with email: $email")
                Log.d("RegisterViewModel", "Registering user with password: $confirmPassword")
                accountService.register(
                    firstName,
                    lastName = lastName,
                    email = email,
                    password = confirmPassword
                )
                delay(1000)
                _uiState.value = _uiState.value.copy(isLoading = false)
                block(accountService.currentUserId)
            } catch (_: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "No account found with these credentials.",
                    isLoading = false,
                )
            } catch (_: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Too many unsuccessful login attempts. " +
                            "Please try again later.",
                    isLoading = false,
                )
            } catch (_: FirebaseNetworkException) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "Network error occurred. " +
                            "Please check your connection and try again.",
                    isLoading = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "An unexpected error occurred. Please try again.",
                    isLoading = false,
                )
                Log.e("RegisterViewModel", "Error registering user ${e.message}", e)
            }
        }
        return _uiState.value.credentialErrorMessage.isEmpty()
    }
}