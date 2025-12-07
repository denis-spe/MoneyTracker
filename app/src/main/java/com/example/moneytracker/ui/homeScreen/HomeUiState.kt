package com.example.moneytracker.ui.homeScreen

import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.ui.homeScreen.topTitle.CurrentTopTitle

data class HomeUiState(
    val datasets: List<Dataset> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val topTitle: CurrentTopTitle = CurrentTopTitle.TODAY
)
