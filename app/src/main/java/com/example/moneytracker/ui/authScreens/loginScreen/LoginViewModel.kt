package com.example.moneytracker.ui.authScreens.loginScreen

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

/**
 * ViewModel for the Login Screen
 * @param accountService The account services
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountServices
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Updates the email in the UI state
     */
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            isEmailError = false,
            isLoading = false,
            emailErrorMessage = "",
            credentialErrorMessage = "",
        )
    }

    /**
     * Updates the password in the UI state
     */
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            isPasswordError = false,
            isLoading = false,
            passwordErrorMessage = "",
            credentialErrorMessage = "",
        )
    }

    /**
     * Validates the input fields before navigating to the Home Screen
     * @param block The block to execute if validation is successful
     */
    fun validateBeforeNavigatingToHome(block: (userId: String) -> Unit): Boolean {
        // Prepare UI State
        _uiState.value = _uiState.value.copy(
            isEmailError = false,
            isPasswordError = false,
            credentialErrorMessage = "",
        )

        // Get current input values
        val email = _uiState.value.email
        val password = _uiState.value.password

        // Validate inputs
        val emailValidator = email.isEmailValid
        val passwordValidator = password.isPasswordValid

        // Update UI state with validation results
        _uiState.value = _uiState.value.copy(
            emailErrorMessage = emailValidator.errorMessage,
            passwordErrorMessage = passwordValidator.errorMessage
        )

        if (!emailValidator.isValid || !passwordValidator.isValid) {
            _uiState.value = _uiState.value.copy(
                isEmailError = !emailValidator.isValid,
                isPasswordError = !passwordValidator.isValid
            )
            return false
        }


        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                accountService.login(email, password)
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
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    credentialErrorMessage = "An unexpected error occurred. Please try again.",
                    isLoading = false,
                )
            }
        }
        return _uiState.value.credentialErrorMessage.isEmpty()
    }
}