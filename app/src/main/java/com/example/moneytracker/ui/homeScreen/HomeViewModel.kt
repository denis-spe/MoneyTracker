package com.example.moneytracker.ui.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopBarNav
import com.example.moneytracker.ui.usecase.DatasetOperationsUseCase
import com.example.moneytracker.ui.usecase.GetAdjustDatasetUseCase
import com.example.moneytracker.ui.usecase.GetCurrentDateUseCase
import com.example.moneytracker.ui.usecase.GetCurrentWeekUseCase
import com.example.moneytracker.ui.usecase.GetLenOfActivatesUseCase
import com.example.moneytracker.ui.usecase.GetTodayChartDonutDataUseCase
import com.example.moneytracker.ui.usecase.GetTodayDatasetsUseCase
import com.example.moneytracker.ui.usecase.GetWeeklyDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayChartDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDataAdjustUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDatasetsUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayStatsUseCase
import com.example.moneytracker.ui.usecase.ObserveUserDataUseCase
import com.example.moneytracker.ui.usecase.RoutineWorkerUseCase
import com.example.moneytracker.ui.usecase.ScheduleAlarmUseCase
import com.example.moneytracker.ui.usecase.SortTodayDataAdjustUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountService: AccountServices,
    private val datasetOperationsUseCase: DatasetOperationsUseCase,
    private val observeUserDataUseCase: ObserveUserDataUseCase,
    private val scheduleAlarmUseCase: ScheduleAlarmUseCase,
    private val sortTodayDataAdjustUseCase: SortTodayDataAdjustUseCase,
    private val getYesterdayDataAdjustUseCase: GetYesterdayDataAdjustUseCase,
    private val getWeeklyDataUseCase: GetWeeklyDataUseCase,
    private val getCurrentWeekUseCase: GetCurrentWeekUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getTodayDatasetsUseCase: GetTodayDatasetsUseCase,
    private val getYesterdayDatasetsUseCase: GetYesterdayDatasetsUseCase,
    private val getLenOfActivatesUseCase: GetLenOfActivatesUseCase,
    private val getAdjustDatasetUseCase: GetAdjustDatasetUseCase,
    private val getTodayChartDonutDataUseCase: GetTodayChartDonutDataUseCase,
    private val getYesterdayChartDataUseCase: GetYesterdayChartDataUseCase,
    private val getYesterdayStatsUseCase: GetYesterdayStatsUseCase,
    private val routineWorker: RoutineWorkerUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private companion object {
        private const val STATE_TIMEOUT = 5_000L
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _dataState = MutableStateFlow(DataState())
    val dataState = _dataState.asStateFlow()

    /**
     * Collect this in Compose.
     * This is the only flow the UI should need.
     */
    val screenDataState: StateFlow<DataState> = dataState
        .map { state ->
            val datasets = state.datasets

            val todayDatasets = getTodayDatasetsUseCase(datasets)
            val yesterdayDatasets = getYesterdayDatasetsUseCase(datasets)
            val goalDatasets = datasets.filter { it.dataType == DataType.GOAL }
            val adjustDatasets = getAdjustDatasetUseCase(datasets)

            val weeklyData = getWeeklyDataUseCase(datasets, state.dates)
            val currentWeekDerived = getCurrentWeekUseCase(state.currentWeekDerived)
            val currentDateDerived = getCurrentDateUseCase(
                state.currentWeekDerived, state.currentDateDerived
            )

            val sortedYesterday = getYesterdayDataAdjustUseCase(datasets)

            val donutChartData = getTodayChartDonutDataUseCase(todayDatasets, context)
            val yesterdayChartData = getYesterdayChartDataUseCase(yesterdayDatasets, context)
            val yesterdayStats = getYesterdayStatsUseCase(yesterdayDatasets)
            val combinedDataWithAdjust = sortTodayDataAdjustUseCase(
                datasets = datasets,
                timeSorting = state.timeSorting,
                categorySorting = state.categorySorting,
                paymentSorting = state.paymentSorting,
                alphabeticalOrder = state.alphabeticalOrder,
                amountSorting = state.amountSorting,
                take = null
            )

            state.copy(
                goalDatasets = goalDatasets,
                adjustDatasets = adjustDatasets,
                todayDatasets = todayDatasets,
                yesterdayDatasets = yesterdayDatasets,
                weeklyData = weeklyData,
                sortedYesterdayDatasets = sortedYesterday,
                donutChartData = donutChartData,
                yesterdayChartData = yesterdayChartData,
                yesterdayStats = yesterdayStats,
                currentWeekDerived = currentWeekDerived,
                currentDateDerived = currentDateDerived,
                combinedDataWithAdjust = combinedDataWithAdjust
            )
        }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState()
        )

    init {
        observe()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observe() {
        accountService.userState
            .map { it?.uid }
            .distinctUntilChanged()
            .flatMapLatest { uid ->
                observeUserDataUseCase(flowOf(uid))
            }
            .onEach { homeData ->
                _uiState.update { current ->
                    current.copy(
                        datasets = homeData.datasets,
                        info = homeData.info,
                        error = homeData.error,
                        datasetState = homeData.datasetState
                    )
                }
                _dataState.update { current ->
                    current.copy(
                        datasets = homeData.datasets
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun addData(dataset: Dataset) = launchWithUid {
        datasetOperationsUseCase.addData(it, dataset)
    }

    fun updateData(old: Dataset, new: Dataset) = launchWithUid {
        datasetOperationsUseCase.updateData(it, old, new)
    }

    fun removeData(dataset: Dataset) = launchWithUid {
        datasetOperationsUseCase.removeData(it, dataset)
    }

    fun addAdjustment(dataset: Dataset, adj: Adjustment) = launchWithUid {
        datasetOperationsUseCase.addAdjustment(it, dataset.id, adj)
    }

    fun updateAdjustment(dataset: Dataset, old: Adjustment, new: Adjustment) = launchWithUid {
        datasetOperationsUseCase.updateAdjustment(it, dataset.id, old, new)
    }

    fun removeAdjustment(datasetId: String, adj: Adjustment) = launchWithUid {
        datasetOperationsUseCase.removeAdjustment(it, datasetId, adj)
    }

    private fun launchWithUid(block: suspend (String) -> Unit) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            block(uid)
        }
    }

    fun beginWork(dataset: Dataset) {
        val uid = accountService.userState.value?.uid ?: return
        if (dataset.routine.routine == Routine.Nothing) return

        routineWorker(uid, dataset.id, dataset.routine.triggerMillis, true)
    }

    fun updateTopTitle(nav: TopBarNav) {
        _uiState.update { it.copy(topTitle = nav) }
    }

    fun updateSelectedTabIndex(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun updateCurrentWeek(dates: List<LocalDate>) {
        _dataState.update { it.copy(currentWeekDerived = dates) }
    }

    fun updateWeekDays(dates: List<LocalDate>) {
        _dataState.update { it.copy(dates = dates) }
    }

    fun updateSorting(
        time: SortType? = null,
        category: String? = null,
        amount: SortType? = null,
        payment: PaymentMethod? = null,
        alpha: SortType? = null
    ) {
        _dataState.update {
            it.copy(
                timeSorting = time ?: it.timeSorting,
                categorySorting = category ?: it.categorySorting,
                amountSorting = amount ?: it.amountSorting,
                paymentSorting = payment ?: it.paymentSorting,
                alphabeticalOrder = alpha ?: it.alphabeticalOrder
            )
        }
    }

    fun updateTimeSorting(type: SortType) = updateSorting(time = type)
    fun updateCategorySorting(category: String) = updateSorting(category = category)
    fun updateAmountSorting(type: SortType) = updateSorting(amount = type)
    fun updatePaymentSorting(method: PaymentMethod?) = updateSorting(payment = method)
    fun updateAlphabeticalOrder(type: SortType) = updateSorting(alpha = type)

    fun updateOnDatasetModelBottomSheetShow(show: Boolean) {
        _uiState.update { it.copy(isDatasetBottomSheetOpen = show) }
    }

    fun updateOnAdjustModelBottomSheetShow(show: Boolean) {
        _uiState.update { it.copy(isAdjustmentBottomSheetOpen = show) }
    }

    fun updateIsBottomSheetContentLoading(loading: Boolean) {
        _uiState.update { it.copy(isBottomSheetContentLoading = loading) }
    }

    fun updateOnFilterClick(show: Boolean) {
        _uiState.update { it.copy(onFilterClick = show) }
    }

    fun updateOnActivateShow(show: Boolean) {
        _uiState.update { it.copy(onActivateShow = show) }
    }

    fun beginTheWork(dataset: Dataset) = beginWork(dataset)

    fun addAdjustmentData(dataset: Dataset, adj: Adjustment) = addAdjustment(dataset, adj)

    fun updateAdjustmentData(dataset: Dataset, old: Adjustment, new: Adjustment) =
        updateAdjustment(dataset, old, new)

    fun removeAdjustmentDataset(datasetId: String, adj: Adjustment) =
        removeAdjustment(datasetId, adj)

    fun getLenOfActivates(date: LocalDate): Int =
        getLenOfActivatesUseCase(uiState.value.datasets, date)
}