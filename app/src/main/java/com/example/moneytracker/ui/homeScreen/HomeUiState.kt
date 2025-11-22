package com.example.moneytracker.ui.homeScreen

import com.example.moneytracker.backend.storage.Dataset

data class HomeUiState(
    val datasets: List<Dataset> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
