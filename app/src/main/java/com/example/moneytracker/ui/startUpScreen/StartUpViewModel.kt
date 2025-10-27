package com.example.moneytracker.ui.startUpScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
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
class StartUpViewModel @Inject constructor(
    private val accountService: AccountServices
) : ViewModel() {
    private val _uiState = MutableStateFlow(StartUpUiState())
    val uiState: StateFlow<StartUpUiState> = _uiState.asStateFlow()

    fun anonymousLogin(block: (userId: String) -> Unit): Boolean {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                accountService.createAnonymousAccount()
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