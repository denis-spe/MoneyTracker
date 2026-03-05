package com.example.moneytracker.ui.homeScreen

import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.Info
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopBarNav
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now


data class HomeUiState(
    val datasets: List<Dataset> = emptyList(),
    val adjustment: List<Adjustment> = emptyList(),
    val info: Info = Info(),
    var isLoading: Boolean = false,
    val error: String? = null,
    val topTitle: TopBarNav = TopBarNav.TODAY,
    val isUserDropdownVisible: Boolean = false,
    val isLogOutLoading: Boolean = false,
    val isDatasetBottomSheetOpen: Boolean = false,
    val isAdjustmentBottomSheetOpen: Boolean = false,
    val combinedDataWithAdjust: List<DataAdjust> = emptyList(),
    val dates: List<LocalDate> = emptyList(),
    val currentWeek: List<java.time.LocalDate> = emptyList(),
    val selectedTabIndex: Int = 1,
    val date: LocalDate = LocalDate.now(),
    val localDateList: List<List<LocalDate>> = emptyList(),
    val consecutiveGoal: List<Dataset> = emptyList()
)
