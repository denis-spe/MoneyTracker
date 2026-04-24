package com.example.moneytracker.ui.homeScreen

import androidx.compose.ui.graphics.Color
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.Info
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
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
    val currentWeek: List<LocalDate> = emptyList(),
    val selectedTabIndex: Int = 1,

    val date: LocalDate = LocalDate.now(),
    val localDateList: List<List<LocalDate>> = emptyList(),
    val consecutiveGoal: List<Dataset> = emptyList(),
    val isActionNotificationVisible: Boolean = false,
    val actionNotificationMessage: String = "",
    val actionNotificationColor: Color = Color.Gray,
    val timeSorting: SortType = SortType.Descending,
    val onFilterClick: Boolean = false,
    val categorySorting: String = "Initial",
    val amountSorting: SortType = SortType.Initial,
    val paymentSorting: PaymentMethod? = null,
    val alphabeticalOrder: SortType = SortType.Initial,
    val datasetState: DatasetState = DatasetState.Loading,
    val isBottomSheetContentLoading: Boolean = true,
    val onActivateShow: Boolean = false,

    // Loading flags for granular shimmers
    val isTodayDataLoading: Boolean = false,
    val isYesterdayDataLoading: Boolean = false,
    val isGoalDataLoading: Boolean = false,
    val isAdjustDataLoading: Boolean = false,
    val isAllDataLoading: Boolean = false,
    val isTodayChartDataLoading: Boolean = false,
    val isYesterdayChartDataLoading: Boolean = false,
    val isYesterdayStatsLoading: Boolean = false,
    val isWeeklyDataLoading: Boolean = false,
    val isSortedTodayLoading: Boolean = false,
    val isSortedYesterdayLoading: Boolean = false,
)
