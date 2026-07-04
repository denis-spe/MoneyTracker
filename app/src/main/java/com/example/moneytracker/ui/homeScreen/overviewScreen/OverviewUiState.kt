package com.example.moneytracker.ui.homeScreen.overviewScreen

import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.ui.homeScreen.DataState

data class OverviewUiState(
    val allDataset: DataState<List<FinanceEntity>> = DataState.Loading
)
