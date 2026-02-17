package com.example.moneytracker.ui.homeScreen

import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.Info
import com.example.moneytracker.ui.homeScreen.topPanel.TopBarNav
import kotlinx.datetime.LocalDate


data class HomeUiState(
    val datasets: List<Dataset> = emptyList(),
    val adjustment: List<Adjustment> = emptyList(),
    val info: Info = Info(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val topTitle: TopBarNav = TopBarNav.TODAY,
    val isUserDropdownVisible: Boolean = false,
    val isLogOutLoading: Boolean = false,
    val isBottomSheetOpen: Boolean = false,
    val combinedDataWithAdjust: List<DataAdjust> = emptyList(),
    val dates: List<LocalDate> = emptyList(),
)
