package com.example.moneytracker.ui

import androidx.compose.ui.graphics.Color

data class UserUiState(
    val isLoading: Boolean = false,
    val isUserDropdownVisible: Boolean = false,
    val isActionNotificationVisible: Boolean = false,
    val actionNotificationMessage: String = "",
    val actionNotificationColor: Color = Color.Gray
)