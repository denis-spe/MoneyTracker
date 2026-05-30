package com.example.moneytracker.ui.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import com.example.moneytracker.ui.usecase.FinanceOperationsUseCase
import com.example.moneytracker.ui.usecase.GetAdjustFinanceUseCase
import com.example.moneytracker.ui.usecase.GetCurrentAmountUseCase
import com.example.moneytracker.ui.usecase.GetCurrentDateUseCase
import com.example.moneytracker.ui.usecase.GetCurrentWeekUseCase
import com.example.moneytracker.ui.usecase.GetTodayChartDonutDataUseCase
import com.example.moneytracker.ui.usecase.GetWeeklyDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayChartDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDataSettlementUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayFinanceUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayStatsUseCase
import com.example.moneytracker.ui.usecase.ObserveUserDataUseCase
import com.example.moneytracker.ui.usecase.RoutineWorkerUseCase
import com.example.moneytracker.ui.usecase.SortTodayDataSettlementUseCase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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
    private val accountService: AccountServices,
    private val financeOperationsUseCase: FinanceOperationsUseCase,
    private val observeUserDataUseCase: ObserveUserDataUseCase,
    private val sortTodayDataSettlementUseCase: SortTodayDataSettlementUseCase,
    private val getYesterdayDataSettlementUseCase: GetYesterdayDataSettlementUseCase,
    private val getWeeklyDataUseCase: GetWeeklyDataUseCase,
    private val getCurrentWeekUseCase: GetCurrentWeekUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getYesterdayFinanceUseCase: GetYesterdayFinanceUseCase,
    private val getCurrentAmountUseCase: GetCurrentAmountUseCase,
    private val getAdjustFinanceUseCase: GetAdjustFinanceUseCase,
    private val getTodayChartDonutDataUseCase: GetTodayChartDonutDataUseCase,
    private val getYesterdayChartDataUseCase: GetYesterdayChartDataUseCase,
    private val getYesterdayStatsUseCase: GetYesterdayStatsUseCase,
    private val routineWorker: RoutineWorkerUseCase,
    @param:ApplicationContext private val context: Context
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
    @OptIn(ExperimentalCoroutinesApi::class)
    val allDataset: StateFlow<DataState<List<FinanceEntity>>> = datasetsFlow
        .flatMapLatest { datasets ->
            flow {
                emit(DataState.Loading)
                try {
                    emit(DataState.Success(datasets))
                } catch (e: Exception) {
                    emit(DataState.Error(e))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayFinance: StateFlow<DataState<List<FinanceEntity>>> = datasetsFlow
        .flatMapLatest { datasets ->
            flow {
                emit(DataState.Loading)
                try {
                    // Ensure the UseCase runs on a background thread
                    val result = withContext(Dispatchers.Default) {
                        datasets.filter { it.isForToday }
                    }
                    emit(DataState.Success(result))
                } catch (e: Exception) {
                    emit(DataState.Error(e))
                }
            }
        }
        .onStart { loadTodayDatasets() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val yesterdayFinance: StateFlow<DataState<List<FinanceEntity>>> = datasetsFlow
        .flatMapLatest { datasets ->
            flow {
                emit(DataState.Loading)
                try {
                    // Ensure the UseCase runs on a background thread
                    val result = withContext(Dispatchers.Default) {
                        getYesterdayFinanceUseCase(datasets)
                    }
                    emit(DataState.Success(result))
                } catch (e: Exception) {
                    emit(DataState.Error(e))
                }
            }
        }
        .onStart { loadYesterdayDatasets() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val fulfillmentFinanceEntity: StateFlow<DataState<List<FinanceEntity>>> = datasetsFlow
        .flatMapLatest { datasets ->
            flow {
                emit(DataState.Loading)
                try {
                    // Ensure the UseCase runs on a background thread
                    val result = withContext(Dispatchers.Default) {
                        datasets.filter { entity ->
                            entity is FinanceEntity.Goal ||
                                    entity is FinanceEntity.Liability
                        }
                    }
                    emit(DataState.Success(result))
                } catch (e: Exception) {
                    emit(DataState.Error(e))
                }
            }
        }
        .onStart { loadFulfillmentData() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )

    val adjustFinance = datasetsFlow
        .map { getAdjustFinanceUseCase(it) }
        .onStart { loadAdjustData() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val donutChartData = todayFinance
        .flatMapLatest { state ->
            // Explicitly type the flows to match the StateFlow's generic type
            when (state) {
                is DataState.Success -> flow<DataState<List<DonutChartData>>> {
                    emit(DataState.Loading)
                    try {
                        val chartData = withContext(Dispatchers.Default) {
                            getTodayChartDonutDataUseCase(
                                state.data,
                                context
                            )
                        }
                        emit(DataState.Success(chartData))
                    } catch (e: Exception) {
                        emit(DataState.Error(e))
                    }
                }
                // Use <List<ChartData>> here to satisfy the compiler
                is DataState.Error -> flowOf<DataState<List<DonutChartData>>>(DataState.Error(state.exception))
                DataState.Loading -> flowOf<DataState<List<DonutChartData>>>(DataState.Loading)
            }
        }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentAccountBalance: StateFlow<DataState<Map<String, Double>>> = datasetsFlow
        .flatMapLatest { datasets ->
            flow {
                emit(DataState.Loading)
                try {
                    val balance = withContext(Dispatchers.Default) {
                        getCurrentAmountUseCase(datasets)
                    }
                    emit(DataState.Success(balance))
                } catch (e: Exception) {
                    emit(DataState.Error(e))
                }
            }
        }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val yesterdayChartData: StateFlow<DataState<List<ChartData>>> = yesterdayFinance
        .flatMapLatest { state ->
            // Explicitly type the flows to match the StateFlow's generic type
            when (state) {
                is DataState.Success -> flow<DataState<List<ChartData>>> {
                    emit(DataState.Loading)
                    try {
                        val chartData = withContext(Dispatchers.Default) {
                            getYesterdayChartDataUseCase(state.data, context)
                        }
                        emit(DataState.Success(chartData))
                    } catch (e: Exception) {
                        emit(DataState.Error(e))
                    }
                }
                // Use <List<ChartData>> here to satisfy the compiler
                is DataState.Error -> flowOf<DataState<List<ChartData>>>(DataState.Error(state.exception))
                DataState.Loading -> flowOf<DataState<List<ChartData>>>(DataState.Loading)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )


    @OptIn(ExperimentalCoroutinesApi::class)
    val yesterdayStats: StateFlow<DataState<YesterdayStats>> = yesterdayFinance
        .flatMapLatest { state ->
            when (state) {
                is DataState.Success -> flow {
                    emit(DataState.Loading)
                    try {
                        val stats = withContext(Dispatchers.Default) {
                            getYesterdayStatsUseCase(state.data)
                        }
                        emit(DataState.Success(stats))
                    } catch (e: Exception) {
                        emit(DataState.Error(e))
                    }
                }
                // Explicitly type the shared states to avoid the "DataState<Nothing>" mismatch
                is DataState.Error -> flowOf<DataState<YesterdayStats>>(DataState.Error(state.exception))
                DataState.Loading -> flowOf<DataState<YesterdayStats>>(DataState.Loading)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )


    @OptIn(ExperimentalCoroutinesApi::class)
    val sortedToday: StateFlow<DataState<List<DataSettlement>>> = combine(
        datasetsFlow,
        sortingFlow
    ) { datasets, sorting ->
        datasets to sorting
    }.flatMapLatest { (datasets, sorting) ->
        flow {
            emit(DataState.Loading)
            try {
                val result = withContext(Dispatchers.Default) {
                    sortTodayDataSettlementUseCase(
                        timeSorting = sorting.time,
                        categorySorting = sorting.category,
                        paymentSorting = sorting.payment,
                        alphabeticalOrder = sorting.alphabetical,
                        amountSorting = sorting.amount,
                        financeEntityList = datasets
                    )
                }
                emit(DataState.Success(result))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STATE_TIMEOUT),
        DataState.Loading
    )


    @OptIn(ExperimentalCoroutinesApi::class)
    val sortedYesterday: StateFlow<DataState<List<DataSettlement>>> = datasetsFlow
        .flatMapLatest { datasets ->
            flow {
                emit(DataState.Loading)
                try {
                    val result = withContext(Dispatchers.Default) {
                        getYesterdayDataSettlementUseCase(datasets)
                    }
                    emit(DataState.Success(result))
                } catch (e: Exception) {
                    emit(DataState.Error(e))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )


    // 2. Create the flow that emits the actual data state
    @OptIn(ExperimentalCoroutinesApi::class)
    val weeklyData: StateFlow<DataState<List<DataSettlement>>> =
        combine(datasetsFlow, datesFlow) { datasets, dates ->
            datasets to dates
        }.flatMapLatest { (datasets, dates) ->
            flow {
                // Emit Loading immediately when inputs change
                emit(DataState.Loading)

                try {
                    // Fetch data
                    val result = withContext(Dispatchers.Default) {
                        getWeeklyDataUseCase(datasets, dates)
                    }
                    // Emit the actual result
                    emit(DataState.Success(result))
                } catch (e: Exception) {
                    emit(DataState.Error(e))
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // 5 seconds timeout
            initialValue = DataState.Loading
        )


    val currentWeekDerived = currentWeekFlow
        .map { getCurrentWeekUseCase(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val activityCounts: StateFlow<DataState<Map<LocalDate, Int>>> = datasetsFlow
        .flatMapLatest { datasets ->
            flow {
                // 1. Emit Loading immediately
                emit(DataState.Loading)

                try {
                    // 2. Perform calculation on background thread
                    val counts = withContext(Dispatchers.Default) {
                        val map = mutableMapOf<LocalDate, Int>()

                        datasets.forEach { entity ->
                            val date = entity.createdAt.toLocalDateTimeUtc().date
                            map[date] = (map[date] ?: 0) + 1

                            val settlements = when (entity) {
                                is FinanceEntity.Goal -> entity.settlement
                                is FinanceEntity.Liability -> entity.settlement
                                else -> emptyList()
                            }

                            settlements.forEach { s ->
                                val sDate = s.dateTime.toLocalDateTimeUtc().date
                                map[sDate] = (map[sDate] ?: 0) + 1
                            }

                            val withdrawal = if (entity is FinanceEntity.Transaction) {
                                entity.withdrawal
                            } else emptyList()

                            withdrawal.forEach { s ->
                                val sDate = s.createdAt.toLocalDateTimeUtc().date
                                map[sDate] = (map[sDate] ?: 0) + 1
                            }
                        }
                        map // Return the calculated map
                    }

                    // 3. Emit Success
                    emit(DataState.Success(counts))

                } catch (e: Exception) {
                    // 4. Catch and emit Error
                    emit(DataState.Error(e))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_TIMEOUT),
            initialValue = DataState.Loading
        )


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
            .onEach { uid ->
                if (uid == null) {
                    _uiState.value = HomeUiState()
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        financeOperationsUseCase.triggerMigration(uid)
                    }
                }
            }
            .flatMapLatest { uid ->
                observeUserDataUseCase(flowOf(uid))
            }
            .onEach { homeData ->
                _uiState.update { current ->
                    current.copy(
                        datasets = homeData.datasets,
                        info = homeData.info,
                        error = homeData.error,
                        datasetState = homeData.datasetState,
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
        val (start, end) = getDayRange(java.time.LocalDate.now())
        financeOperationsUseCase.filterFinances(
            userId = uid,
            filter = Filter.and(
                Filter.greaterThanOrEqualTo("createdAt", start),
                Filter.lessThan("createdAt", end)
            )
        )
    }

    fun loadYesterdayDatasets() = viewModelScope.launch {
        val uid = accountService.userState.value?.uid ?: return@launch
        val (start, end) = getDayRange(java.time.LocalDate.now().minusDays(1))
        financeOperationsUseCase.filterFinances(
            userId = uid,
            filter = Filter.and(
                Filter.greaterThanOrEqualTo("createdAt", start),
                Filter.lessThan("createdAt", end)
            )
        )
    }

    fun loadFulfillmentData() = viewModelScope.launch {
        val uid = accountService.userState.value?.uid ?: return@launch
        financeOperationsUseCase.filterFinances(
            userId = uid,
            filter = Filter.or(
                Filter.equalTo("financeType", "GOAL"),
                Filter.equalTo("financeType", "LIABILITY")
            )
        )
    }

    fun loadAdjustData() = viewModelScope.launch {
        val uid = accountService.userState.value?.uid ?: return@launch
        // Fetch a broad set to ensure settlements are loaded
        financeOperationsUseCase.filterFinances(
            userId = uid,
            filter = Filter.greaterThan("amount", -1)
        )
    }

    // 2. Combine them into a single reactive StateFlow
    val isDataLoaded: StateFlow<Boolean> = combine(
        todayFinance,
        donutChartData,
        currentAccountBalance,
        sortedToday
    ) { finance, donut, balance, sorted ->
        // Helper to check if a state has finished loading (either successfully or with an error)
        fun DataState<*>.isTerminal() = this is DataState.Success || this is DataState.Error

        // Evaluates to true only when ALL 4 flows have finished their network requests
        finance.isTerminal() && donut.isTerminal() && balance.isTerminal() && sorted.isTerminal()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Keeps flow alive for 5s during config changes
        initialValue = false // Starts as false while data fetches
    )


    fun addData(financeEntity: FinanceEntity) = launchWithUid {
        financeOperationsUseCase.addData(it, financeEntity)
    }

    fun updateData(old: FinanceEntity, new: FinanceEntity) = launchWithUid {
        financeOperationsUseCase.updateData(it, old, new)
    }

    fun removeData(financeEntity: FinanceEntity) = launchWithUid {
        financeOperationsUseCase.removeData(it, financeEntity)
    }

    fun addSettlement(financeId: String, financeType: String, adj: Settlement) = launchWithUid {
        financeOperationsUseCase.addSettlement(it, financeId, financeType, adj)
    }

    fun addWithdrawal(financeId: String, financeType: String, withdrawal: Withdrawal) =
        launchWithUid {
            financeOperationsUseCase.addWithdrawal(
                it, financeId, financeType, withdrawal
            )
        }

    fun updateSettlement(
        financeId: String,
        financeType: String,
        old: Settlement,
        new: Settlement
    ) = launchWithUid {
        financeOperationsUseCase.updateSettlement(it, financeId, financeType, old, new)
    }

    fun removeSettlement(financeId: String, financeType: String, adj: Settlement) = launchWithUid {
        financeOperationsUseCase.removeSettlement(it, financeId, financeType, adj)
    }

    fun updateWithdrawal(
        financeId: String,
        financeType: String,
        old: Withdrawal,
        new: Withdrawal
    ) = launchWithUid {
        financeOperationsUseCase.updateWithdrawal(it, financeId, financeType, old, new)
    }

    fun removeWithdrawal(financeId: String, financeType: String, withdrawal: Withdrawal) =
        launchWithUid {
            financeOperationsUseCase.removeWithdrawal(it, financeId, financeType, withdrawal)
        }

    private fun launchWithUid(block: suspend (String) -> Unit) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            block(uid)
        }
    }

    fun beginWork(financeEntity: FinanceEntity) {
        val uid = accountService.userState.value?.uid ?: return
        if (financeEntity is FinanceEntity.Goal) {
            if (financeEntity.routine.routine == Routine.Nothing) return
            val financeEntityType = "GOAL"
            routineWorker(
                uid,
                financeEntity.id,
                financeEntityType,
                financeEntity.routine.triggerMillis,
                true
            )
        }
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
        _uiState.update { it.copy(isSettlementBottomSheetOpen = show) }
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

    fun beginTheWork(financeEntity: FinanceEntity) = beginWork(financeEntity)

    fun addSettlementData(financeId: String, financeType: String, adj: Settlement) =
        addSettlement(financeId, financeType, adj)

    fun addWithdrawalData(financeId: String, financeType: String, withdrawal: Withdrawal) =
        addWithdrawal(
            financeId,
            financeType,
            withdrawal = withdrawal
        )

    fun updateWithdrawalData(
        financeId: String,
        financeType: String,
        old: Withdrawal,
        new: Withdrawal
    ) = updateWithdrawal(financeId, financeType, old, new)

    fun removeWithdrawalFinance(financeId: String, financeType: String, withdrawal: Withdrawal) =
        removeWithdrawal(financeId, financeType, withdrawal)

    fun updateSettlementData(
        financeId: String,
        financeType: String,
        old: Settlement,
        new: Settlement
    ) = updateSettlement(financeId, financeType, old, new)

    fun removeSettlementFinance(financeId: String, financeType: String, adj: Settlement) =
        removeSettlement(financeId, financeType, adj)
}
