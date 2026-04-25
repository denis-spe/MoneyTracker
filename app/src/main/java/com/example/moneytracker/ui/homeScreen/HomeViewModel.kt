package com.example.moneytracker.ui.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopBarNav
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.utils.now
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStorage: DataStorage,
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

    /*******************
     * BASE FLOWS (Single Source of Truth)
     *******************/
    private val datasetsFlow = uiState
        .map { it.datasets }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), replay = 1)

    private val datesFlow = uiState
        .map { it.dates }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), replay = 1)

    private val currentWeekFlow = uiState
        .map { it.currentWeek }
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), replay = 1)

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
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), replay = 1)

    /*******************
     * LIVE UI STATE FLOWS
     *******************/

    val todayDataset = datasetsFlow
        .map { all -> all.filter { it.isForToday } }
        .onStart { loadTodayDatasets() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val yesterdayDataset = datasetsFlow
        .map { getYesterdayDatasetsUseCase(it) }
        .onStart { loadYesterdayDatasets() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val goalDataset = datasetsFlow
        .map { it.filter { d -> d.dataType == DataType.GOAL } }
        .onStart { loadGoalData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val adjustDataset = datasetsFlow
        .map { getAdjustDatasetUseCase(it) }
        .onStart { loadAdjustData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val allDataset = datasetsFlow
        .onStart { loadAllData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val donutChartData = todayDataset
        .mapLatest {
            withContext(Dispatchers.Default) {
                getTodayChartDonutDataUseCase(
                    it,
                    context
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val yesterdayChartData = yesterdayDataset
        .mapLatest {
            withContext(Dispatchers.Default) {
                getYesterdayChartDataUseCase(
                    it,
                    context
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val yesterdayStats = yesterdayDataset
        .mapLatest { withContext(Dispatchers.Default) { getYesterdayStatsUseCase(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), YesterdayStats())

    val sortedToday = combine(datasetsFlow, sortingFlow) { datasets, sorting ->
        withContext(Dispatchers.Default) {
            sortTodayDataAdjustUseCase(
                datasets = datasets,
                timeSorting = sorting.time,
                categorySorting = sorting.category,
                paymentSorting = sorting.payment,
                alphabeticalOrder = sorting.alphabetical,
                amountSorting = sorting.amount,
                take = null
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val sortedYesterday = datasetsFlow
        .mapLatest { withContext(Dispatchers.Default) { getYesterdayDataAdjustUseCase(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val weeklyData = combine(datasetsFlow, datesFlow) { datasets, dates ->
        withContext(Dispatchers.Default) { getWeeklyDataUseCase(datasets, dates) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val currentWeekDerived = currentWeekFlow
        .map { getCurrentWeekUseCase(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val currentDateDerived = combine(currentWeekFlow, uiState.map { it.date }) { week, date ->
        getCurrentDateUseCase(week, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), LocalDate.now())

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
            }
            .launchIn(viewModelScope)
    }

    private fun getDayRange(date: java.time.LocalDate): Pair<Timestamp, Timestamp> {
        val zone = ZoneId.systemDefault()
        val start = date.atStartOfDay(zone).toInstant()
        val end = date.plusDays(1).atStartOfDay(zone).toInstant()
        return Timestamp(start.epochSecond, start.nano) to Timestamp(end.epochSecond, end.nano)
    }

    fun loadTodayDatasets() = viewModelScope.launch {
        val uid = accountService.userState.value?.uid ?: return@launch
        _uiState.update { it.copy(isTodayDataLoading = true) }
        val (start, end) = getDayRange(java.time.LocalDate.now())
        datasetOperationsUseCase.filterDatasets(
            userId = uid,
            filter = Filter.and(
                Filter.greaterThanOrEqualTo("createdAt", start),
                Filter.lessThan("createdAt", end)
            )
        )
        _uiState.update { it.copy(isTodayDataLoading = false) }
    }

    fun loadYesterdayDatasets() = viewModelScope.launch {
        val uid = accountService.userState.value?.uid ?: return@launch
        _uiState.update { it.copy(isYesterdayDataLoading = true) }
        val (start, end) = getDayRange(java.time.LocalDate.now().minusDays(1))
        datasetOperationsUseCase.filterDatasets(
            userId = uid,
            filter = Filter.and(
                Filter.greaterThanOrEqualTo("createdAt", start),
                Filter.lessThan("createdAt", end)
            )
        )
        _uiState.update { it.copy(isYesterdayDataLoading = false) }
    }

    fun loadGoalData() = viewModelScope.launch {
        val uid = accountService.userState.value?.uid ?: return@launch
        _uiState.update { it.copy(isGoalDataLoading = true) }
        datasetOperationsUseCase.filterDatasets(
            userId = uid,
            filter = Filter.equalTo("dataType", DataType.GOAL.name)
        )
        _uiState.update { it.copy(isGoalDataLoading = false) }
    }

    fun loadAdjustData() = viewModelScope.launch {
        val uid = accountService.userState.value?.uid ?: return@launch
        _uiState.update { it.copy(isAdjustDataLoading = true) }
        // Fetch a broad set to ensure adjustments are loaded
        datasetOperationsUseCase.filterDatasets(
            userId = uid,
            filter = Filter.greaterThan("amount", -1)
        )
        _uiState.update { it.copy(isAdjustDataLoading = false) }
    }

    fun loadAllData() = viewModelScope.launch {
        accountService.userState.value?.uid ?: return@launch
        _uiState.update { it.copy(isAllDataLoading = true) }
        // The datasetsFlow is already real-time, this just triggers initial load state
        _uiState.update { it.copy(isAllDataLoading = false) }
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
        _uiState.update { it.copy(currentWeek = dates) }
    }

    fun updateWeekDays(dates: List<LocalDate>) {
        _uiState.update { it.copy(dates = dates) }
    }

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
