package com.example.moneytracker.ui.homeScreen.todayScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.FinanceRepository
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.toDataState
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.SortingState
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.usecase.GetCurrentAmountUseCase
import com.example.moneytracker.ui.usecase.GetLiabilityBalanceUseCase
import com.example.moneytracker.ui.usecase.GetTodayChartDonutDataUseCase
import com.example.moneytracker.ui.usecase.HomeData
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TodayScreenData(
    val uiState: TodayUiState = TodayUiState(),
    val donutChartData: DataState<List<DonutChartData>> = DataState.Loading,
    val sortedToday: DataState<List<DataSettlement>> = DataState.Loading,
    val fulfillmentFinanceEntity: DataState<List<FinanceEntity>> = DataState.Loading,
    val currentAccountBalance: DataState<Map<String, Double>> = DataState.Loading,
    val liabilityBalance: DataState<Map<String, Double>> = DataState.Loading
)

@HiltViewModel
class TodayViewModel @Inject constructor(
    financeRepository: FinanceRepository,
    private val sortTodayDataSettlementUseCase: SortTodayDataSettlementUseCase,
    private val getCurrentAmountUseCase: GetCurrentAmountUseCase,
    private val getLiabilityBalanceUseCase: GetLiabilityBalanceUseCase,
    private val getTodayChartDonutDataUseCase: GetTodayChartDonutDataUseCase,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    private val rawDatasetsFlow: StateFlow<HomeData> = financeRepository.rawDatasetsFlow

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

    val donutChartData: StateFlow<DataState<List<DonutChartData>>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
                getTodayChartDonutDataUseCase(datasets, context)
            }
        }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val fulfillmentFinanceEntity: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map {
            it.toDataState { datasets ->
                datasets.filter { entity ->
                    entity is FinanceEntity.Goal || entity is FinanceEntity.Liability
                }
            }
        }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val currentAccountBalance: StateFlow<DataState<Map<String, Double>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getCurrentAmountUseCase(datasets) } }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val liabilityBalance: StateFlow<DataState<Map<String, Double>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getLiabilityBalanceUseCase(datasets) } }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

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
    }
        .flowOn(Dispatchers.Default)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val screenData: StateFlow<TodayScreenData> = combine(
        uiState,
        donutChartData,
        sortedToday,
        fulfillmentFinanceEntity,
        currentAccountBalance,
        liabilityBalance
    ) { flows ->
        TodayScreenData(
            uiState = flows[0] as TodayUiState,
            donutChartData = flows[1] as DataState<List<DonutChartData>>,
            sortedToday = flows[2] as DataState<List<DataSettlement>>,
            fulfillmentFinanceEntity = flows[3] as DataState<List<FinanceEntity>>,
            currentAccountBalance = flows[4] as DataState<Map<String, Double>>,
            liabilityBalance = flows[5] as DataState<Map<String, Double>>
        )
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), TodayScreenData())

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
    fun updateOnFilterClick(show: Boolean) = _uiState.update { it.copy(onFilterClick = show) }
}
