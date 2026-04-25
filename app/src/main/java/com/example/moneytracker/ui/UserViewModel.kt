package com.example.moneytracker.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val accountService: AccountServices
) : ViewModel() {

    val userState = accountService.userState

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _snackBarHostState = MutableStateFlow(SnackbarHostState())
    val snackBarHostState: StateFlow<SnackbarHostState> = _snackBarHostState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<Unit>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val hasUser: Boolean
        get() = accountService.hasUser

    fun handleLogout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                accountService.signOut()
                _navigationEvents.emit(Unit)
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateIsUserDropdownVisible(isVisible: Boolean) {
        _uiState.update { it.copy(isUserDropdownVisible = isVisible) }
    }

    fun launchSnackBarHostState(message: String) {
        viewModelScope.launch {
            _snackBarHostState.value.showSnackbar(message)
        }
    }

    fun showActionNotification(message: String, color: Color) {
        _uiState.update {
            it.copy(
                isActionNotificationVisible = true,
                actionNotificationMessage = message,
                actionNotificationColor = color
            )
        }
    }

    fun dismissActionNotification() {
        _uiState.update { it.copy(isActionNotificationVisible = false) }
    }
}