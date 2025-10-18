package com.example.moneytracker.ui.authScreens.loginScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.helper.isEmailValid
import com.example.moneytracker.helper.isPasswordValid
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun onLoadingChange(isLoading: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading)
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



        if (!emailValidator.isValid || !passwordValidator.isValid) {
            return false
        }


        viewModelScope.launch {
            try {
                accountService.authenticate(email, password)
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