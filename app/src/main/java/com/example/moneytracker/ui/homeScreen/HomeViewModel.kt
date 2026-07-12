// Glory be the name of LORD our GOD
package com.example.moneytracker.ui.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.StartupTimer
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.toDataState
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
import com.example.moneytracker.ui.usecase.GetLiabilityBalanceUseCase
import com.example.moneytracker.ui.usecase.GetProfessionalSummaryUseCase
import com.example.moneytracker.ui.usecase.GetTodayChartDonutDataUseCase
import com.example.moneytracker.ui.usecase.GetWeeklyDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayChartDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDataSettlementUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayFinanceUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayStatsUseCase
import com.example.moneytracker.ui.usecase.HomeData
import com.example.moneytracker.ui.usecase.ObserveUserDataUseCase
import com.example.moneytracker.ui.usecase.ProfessionalSummary
import com.example.moneytracker.ui.usecase.RoutineWorkerUseCase
import com.example.moneytracker.ui.usecase.SortTodayDataSettlementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
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
    private val financeOperationsUseCase: FinanceOperationsUseCase,
    observeUserDataUseCase: ObserveUserDataUseCase,
    private val sortTodayDataSettlementUseCase: SortTodayDataSettlementUseCase,
    private val getYesterdayDataSettlementUseCase: GetYesterdayDataSettlementUseCase,
    private val getWeeklyDataUseCase: GetWeeklyDataUseCase,
    private val getCurrentWeekUseCase: GetCurrentWeekUseCase,
    private val getCurrentDateUseCase: GetCurrentDateUseCase,
    private val getYesterdayFinanceUseCase: GetYesterdayFinanceUseCase,
    private val getCurrentAmountUseCase: GetCurrentAmountUseCase,
    private val getAdjustFinanceUseCase: GetAdjustFinanceUseCase,
    private val getLiabilityBalanceUseCase: GetLiabilityBalanceUseCase,
    private val getTodayChartDonutDataUseCase: GetTodayChartDonutDataUseCase,
    private val getYesterdayChartDataUseCase: GetYesterdayChartDataUseCase,
    private val getYesterdayStatsUseCase: GetYesterdayStatsUseCase,
    private val getProfessionalSummaryUseCase: GetProfessionalSummaryUseCase,
    private val routineWorker: RoutineWorkerUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

    // ---------------------------------------------------------------------------
    // UI state (sorting, tab, dates — user-driven, NOT data)
    // ---------------------------------------------------------------------------

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // ---------------------------------------------------------------------------
    // Secondary flows derived from uiState (sorting/dates only — NOT datasets)
    // ---------------------------------------------------------------------------

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
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SortingState(
                time = SortType.Descending,
                category = "Initial",
                payment = null,
                alphabetical = SortType.Initial,
                amount = SortType.Initial
            )
        )

    // ---------------------------------------------------------------------------
    // Single source of truth — one Firestore subscription for everything.
    // flowOn(Dispatchers.Default) means ALL downstream .map { } blocks run off
    // the main thread automatically. No withContext() needed in derived flows.
    // ---------------------------------------------------------------------------

    private val rawDatasetsFlow: StateFlow<HomeData> =
        observeUserDataUseCase(
            accountService.userState.map { it?.uid }   // Flow<String?>
        )
            .flowOn(Dispatchers.Default)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STATE_TIMEOUT),
                HomeData(datasetState = DatasetState.Loading)
            )

    // ---------------------------------------------------------------------------
    // init — single place for side effects:
    //   1. Auth change → reset UI / run migration
    //   2. Sync rawDatasetsFlow → _uiState so downstream code that still reads
    //      uiState.info / uiState.error stays consistent.
    // There is NO second observeUserDataUseCase call here.
    // ---------------------------------------------------------------------------

    init {
        StartupTimer.mark("ViewModel.init thread=${Thread.currentThread().name} hash=${this.hashCode()}")

        // 1. Auth side-effects (migration, state reset)
        viewModelScope.launch {
            accountService.userState
                .map { it?.uid }
                .distinctUntilChanged()
                .collect { uid ->
                    if (uid == null) {
                        _uiState.value = HomeUiState()
                    } else {
                        launch(Dispatchers.IO) {
                            financeOperationsUseCase.triggerMigration(uid)
                        }
                    }
                }
        }

        // 2. Mirror HomeData into uiState (info, error, datasetState only)
        viewModelScope.launch {
            rawDatasetsFlow.collect { homeData ->
                _uiState.update { current ->
                    current.copy(
                        info = homeData.info,
                        error = homeData.error,
                        datasetState = homeData.datasetState,
                    )
                }
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Derived data flows — all use toDataState { } extension + rawDatasetsFlow.
    // No flatMapLatest, no intermediate Loading emits, no withContext() needed.
    // ---------------------------------------------------------------------------

    val allDataset: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> datasets } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val todayFinance: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> datasets.filter { it.isForToday } } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val yesterdayFinance: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getYesterdayFinanceUseCase(datasets) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val fulfillmentFinanceEntity: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map {
            it.toDataState { datasets ->
                datasets.filter { entity ->
                    entity is FinanceEntity.Goal || entity is FinanceEntity.Liability
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val adjustFinance: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getAdjustFinanceUseCase(datasets) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val currentAccountBalance: StateFlow<DataState<Map<String, Double>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getCurrentAmountUseCase(datasets) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val liabilityBalance: StateFlow<DataState<Map<String, Double>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getLiabilityBalanceUseCase(datasets) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    // Chart flows derive directly from rawDatasetsFlow (not chained off todayFinance /
    // yesterdayFinance) to avoid stacked Loading frames.

    val donutChartData: StateFlow<DataState<List<DonutChartData>>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
                getTodayChartDonutDataUseCase(
                    datasets,
                    context
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val yesterdayChartData: StateFlow<DataState<List<ChartData>>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
                getYesterdayChartDataUseCase(
                    getYesterdayFinanceUseCase(datasets),
                    context
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val yesterdayStats: StateFlow<DataState<YesterdayStats>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
                getYesterdayStatsUseCase(getYesterdayFinanceUseCase(datasets))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    // Sorted lists — combine rawDatasetsFlow with user-driven sorting/date state.
    // No Loading flash: combine() only fires when either input actually changes.

    val sortedToday: StateFlow<DataState<List<DataSettlement>>> = combine(
        rawDatasetsFlow,
        sortingFlow
    ) { homeData, sorting ->
        homeData.toDataState { datasets ->
            sortTodayDataSettlementUseCase(
                timeSorting = sorting.time,
                categorySorting = sorting.category,
                paymentSorting = sorting.payment,
                alphabeticalOrder = sorting.alphabetical,
                amountSorting = sorting.amount,
                financeEntityList = datasets
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val sortedYesterday: StateFlow<DataState<List<DataSettlement>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getYesterdayDataSettlementUseCase(datasets) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val groupedWeeklyData: StateFlow<DataState<List<Pair<LocalDate, List<DataSettlement>>>>> =
        combine(
            rawDatasetsFlow,
            datesFlow
        ) { homeData, dates ->
            homeData.toDataState { datasets ->
                getWeeklyDataUseCase(datasets, dates)
                    .groupBy { it.createdAt.toLocalDateTimeUtc().date }
                    .toList()
                    .sortedByDescending { it.first }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val professionalSummary: StateFlow<DataState<ProfessionalSummary>> = combine(
        rawDatasetsFlow,
        datesFlow
    ) { homeData, dates ->
        homeData.toDataState { datasets ->
            getProfessionalSummaryUseCase(getWeeklyDataUseCase(datasets, dates))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val activityCounts: StateFlow<DataState<Map<LocalDate, Int>>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
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

                    if (entity is FinanceEntity.Transaction) {
                        entity.withdrawal.forEach { w ->
                            val sDate = w.createdAt.toLocalDateTimeUtc().date
                            map[sDate] = (map[sDate] ?: 0) + 1
                        }
                    }
                }
                map
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    // ---------------------------------------------------------------------------
    // Week / date helpers
    // ---------------------------------------------------------------------------

    val currentWeekDerived: StateFlow<List<LocalDate>> = currentWeekFlow
        .map { getCurrentWeekUseCase(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), emptyList())

    val currentDateDerived = combine(
        currentWeekFlow,
        uiState.map { it.date }
    ) { week, date ->
        getCurrentDateUseCase(week, date)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), LocalDate.now())

    // ---------------------------------------------------------------------------
    // isDataLoaded — true once ALL key flows have settled (Success or Error).
    // Used by the composable to switch from shimmer → content.
    // ---------------------------------------------------------------------------

    val isDataLoaded: StateFlow<Boolean> = combine(
        todayFinance,
        sortedToday
    ) { finance, sorted ->
        fun DataState<*>.isTerminal() = this is DataState.Success || this is DataState.Error
        // More lenient loaded check: Success even if some secondary data (like charts) is still pending
        finance.isTerminal() || sorted.isTerminal()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STATE_TIMEOUT),
        initialValue = false
    )

    // ---------------------------------------------------------------------------
    // CRUD operations — all go through financeOperationsUseCase
    // ---------------------------------------------------------------------------

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
            financeOperationsUseCase.addWithdrawal(it, financeId, financeType, withdrawal)
        }

    fun updateSettlement(
        financeId: String,
        financeType: String,
        old: Settlement,
        new: Settlement
    ) = launchWithUid {
        financeOperationsUseCase.updateSettlement(it, financeId, financeType, old, new)
    }

    fun removeSettlement(financeId: String, financeType: String, adj: Settlement) =
        launchWithUid {
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

    // Public aliases kept for backward compatibility with existing composables

    fun addSettlementData(financeId: String, financeType: String, adj: Settlement) =
        addSettlement(financeId, financeType, adj)

    fun addWithdrawalData(financeId: String, financeType: String, withdrawal: Withdrawal) =
        addWithdrawal(financeId, financeType, withdrawal)

    fun updateWithdrawalData(
        financeId: String, financeType: String, old: Withdrawal, new: Withdrawal
    ) = updateWithdrawal(financeId, financeType, old, new)

    fun removeWithdrawalFinance(financeId: String, financeType: String, withdrawal: Withdrawal) =
        removeWithdrawal(financeId, financeType, withdrawal)

    fun updateSettlementData(
        financeId: String, financeType: String, old: Settlement, new: Settlement
    ) = updateSettlement(financeId, financeType, old, new)

    fun removeSettlementFinance(financeId: String, financeType: String, adj: Settlement) =
        removeSettlement(financeId, financeType, adj)

    // ---------------------------------------------------------------------------
    // Routine / WorkManager
    // ---------------------------------------------------------------------------

    fun beginWork(financeEntity: FinanceEntity) {
        val uid = accountService.userState.value?.uid ?: return
        if (financeEntity is FinanceEntity.Goal) {
            if (financeEntity.routine.routine == Routine.Nothing) return
            routineWorker(
                uid,
                financeEntity.id,
                "GOAL",
                financeEntity.routine.triggerMillis,
                true
            )
        }
    }

    fun beginTheWork(financeEntity: FinanceEntity) = beginWork(financeEntity)

    // ---------------------------------------------------------------------------
    // UI state updaters
    // ---------------------------------------------------------------------------

    fun updateSelectedTabIndex(index: Int) =
        _uiState.update { it.copy(selectedTabIndex = index) }

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
        _uiState.update { it.copy(isSettlementBottomSheetOpen = show) }

    fun updateIsBottomSheetContentLoading(loading: Boolean) =
        _uiState.update { it.copy(isBottomSheetContentLoading = loading) }

    fun updateOnFilterClick(show: Boolean) =
        _uiState.update { it.copy(onFilterClick = show) }

    fun updateOnActivateShow(show: Boolean) =
        _uiState.update { it.copy(onActivateShow = show) }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private fun launchWithUid(block: suspend (String) -> Unit) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            block(uid)
        }
    }
}