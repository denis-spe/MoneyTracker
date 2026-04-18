package com.example.moneytracker.ui.homeScreen

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
import com.example.moneytracker.ui.usecase.GetTodayDatasetsUseCase
import com.example.moneytracker.ui.usecase.GetWeeklyDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDataAdjustUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDatasetsUseCase
import com.example.moneytracker.ui.usecase.ObserveUserDataUseCase
import com.example.moneytracker.ui.usecase.RoutineWorkerUseCase
import com.example.moneytracker.ui.usecase.ScheduleAlarmUseCase
import com.example.moneytracker.ui.usecase.SortTodayDataAdjustUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now
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
    private val routineWorker: RoutineWorkerUseCase
) : ViewModel() {

    /*******************
     * UI STATE
     *******************/
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observe()
    }

    private fun observe() {
        viewModelScope.launch {
            observeUserDataUseCase(
                accountService.userState.map { it?.uid }
            ).collect { homeData ->
                _uiState.update {
                    it.copy(
                        datasets = homeData.datasets,
                        info = homeData.info,
                        error = homeData.error,
                        datasetState = homeData.datasetState
                    )
                }
            }
        }
    }

    /*******************
     * BASE FLOWS (Single Source of Truth)
     *******************/
    private val datasetsFlow = uiState
        .map { it.datasets }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    private val datesFlow = uiState
        .map { it.dates }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    private val currentWeekFlow = uiState
        .map { it.currentWeek }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    private val currentDateFlow = uiState
        .map { it.date to it.currentWeek }
        .distinctUntilChanged()

    private val sortingFlow = uiState
        .map {
            SortingState(
                time = it.timeSorting,
                category = it.categorySorting,
                payment = it.paymentSorting,
                alphabetical = it.alphabeticalOrder,
                amount = it.amountSorting
            )
        }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), replay = 1)

    /*******************
     * DERIVED FLOWS (Business Logic)
     *******************/
    val goalDatasetsFlow = datasetsFlow
        .map { it.filter { d -> d.dataType == DataType.GOAL } }

    val adjustDatasetsFlow = datasetsFlow
        .map { getAdjustDatasetUseCase(it) }

    val todayDatasetsFlow = datasetsFlow
        .map { getTodayDatasetsUseCase(it) }

    val yesterdayDatasetsFlow = datasetsFlow
        .map { getYesterdayDatasetsUseCase(it) }

    val weeklyDataFlow = combine(datasetsFlow, datesFlow) { datasets, dates ->
        getWeeklyDataUseCase(datasets, dates)
    }

    val currentWeekDerivedFlow = currentWeekFlow
        .map { getCurrentWeekUseCase(it) }

    val currentDateDerivedFlow = currentDateFlow
        .map { (date, week) ->
            getCurrentDateUseCase(week, date)
        }

    val sortedTodayFlow = combine(datasetsFlow, sortingFlow) { datasets, sorting ->
        sortTodayDataAdjustUseCase(
            datasets,
            sorting.time,
            sorting.category,
            sorting.payment,
            sorting.alphabetical,
            sorting.amount,
            null
        )
    }

    val sortedYesterdayFlow = datasetsFlow
        .map { getYesterdayDataAdjustUseCase(it) }

    /*******************
     * UI STATE FLOWS (ONLY what UI collects)
     *******************/
    val todayDatasetsState = todayDatasetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val yesterdayDatasetsState = yesterdayDatasetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val goalDatasetsState = goalDatasetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val adjustDatasetsState = adjustDatasetsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val weeklyDataState = weeklyDataFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val sortedTodayState = sortedTodayFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val sortedYesterdayState = sortedYesterdayFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val currentWeekState = currentWeekDerivedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val currentDateState = currentDateDerivedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LocalDate.now())


    /*******************
     * OPERATIONS
     *******************/
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

    /*******************
     * UI STATE MUTATIONS
     *******************/
    fun updateTopTitle(nav: TopBarNav) {
        _uiState.update { it.copy(topTitle = nav) }
    }

    fun updateSelectedTabIndex(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun updateCurrentWeek(dates: List<LocalDate>) =
        _uiState.update { it.copy(currentWeek = dates) }

    fun updateWeekDays(dates: List<LocalDate>) =
        _uiState.update { it.copy(dates = dates) }

    fun updateSorting(
        time: SortType? = null,
        category: String? = null,
        amount: SortType? = null,
        payment: PaymentMethod? = null,
        alpha: SortType? = null
    ) {
        _uiState.update {
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

    fun updateOnDatasetModelBottomSheetShow(show: Boolean) =
        _uiState.update { it.copy(isDatasetBottomSheetOpen = show) }

    fun updateOnAdjustModelBottomSheetShow(show: Boolean) =
        _uiState.update { it.copy(isAdjustmentBottomSheetOpen = show) }

    fun updateIsBottomSheetContentLoading(loading: Boolean) =
        _uiState.update { it.copy(isBottomSheetContentLoading = loading) }

    fun updateOnFilterClick(show: Boolean) =
        _uiState.update { it.copy(onFilterClick = show) }

    fun updateOnActivateShow(show: Boolean) =
        _uiState.update { it.copy(onActivateShow = show) }

    fun beginTheWork(dataset: Dataset) = beginWork(dataset)

    fun addAdjustmentData(dataset: Dataset, adj: Adjustment) = addAdjustment(dataset, adj)

    fun updateAdjustmentData(dataset: Dataset, old: Adjustment, new: Adjustment) =
        updateAdjustment(dataset, old, new)

    fun removeAdjustmentDataset(datasetId: String, adj: Adjustment) =
        removeAdjustment(datasetId, adj)

    val fetchLiveChangeDataset: Flow<List<Dataset>> = datasetsFlow

    fun getLenOfActivates(date: LocalDate): Int =
        getLenOfActivatesUseCase(uiState.value.datasets, date)
}