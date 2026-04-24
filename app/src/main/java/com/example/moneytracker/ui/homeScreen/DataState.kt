package com.example.moneytracker.ui.homeScreen

import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now

data class DataState(
    val datasets: List<Dataset> = emptyList(),
    val goalDatasets: List<Dataset> = emptyList(),
    val adjustDatasets: List<Dataset> = emptyList(),
    val todayDatasets: List<Dataset> = emptyList(),
    val yesterdayDatasets: List<Dataset> = emptyList(),
    val weeklyData: List<DataAdjust> = emptyList(),
    val sortedYesterdayDatasets: List<DataAdjust> = emptyList(),
    val donutChartData: List<DonutChartData> = emptyList(),
    val yesterdayChartData: List<ChartData> = emptyList(),
    val yesterdayStats: YesterdayStats = YesterdayStats(),
    val currentWeekDerived: List<LocalDate> = emptyList(),
    val currentDateDerived: LocalDate = LocalDate.now(),
    val combinedDataWithAdjust: List<DataAdjust> = emptyList(),
    val dates: List<LocalDate> = emptyList(),
    val localDateList: List<List<LocalDate>> = emptyList(),
    val consecutiveGoal: List<Dataset> = emptyList(),
    val adjustment: List<Adjustment> = emptyList(),
    val timeSorting: SortType = SortType.Descending,
    val onFilterClick: Boolean = false,
    val categorySorting: String = "Initial",
    val amountSorting: SortType = SortType.Initial,
    val paymentSorting: PaymentMethod? = null,
    val alphabeticalOrder: SortType = SortType.Initial,
)
