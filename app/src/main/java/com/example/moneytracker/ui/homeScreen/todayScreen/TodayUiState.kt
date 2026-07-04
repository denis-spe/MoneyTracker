package com.example.moneytracker.ui.homeScreen.todayScreen

import androidx.compose.runtime.Stable
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType

@Stable
data class TodayUiState(
    val timeSorting: SortType = SortType.Descending,
    val categorySorting: String = "Initial",
    val amountSorting: SortType = SortType.Initial,
    val paymentSorting: PaymentMethod? = null,
    val alphabeticalOrder: SortType = SortType.Initial,
    val onFilterClick: Boolean = false
)
