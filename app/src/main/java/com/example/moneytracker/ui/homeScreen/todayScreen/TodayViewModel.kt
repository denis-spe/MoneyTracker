package com.example.moneytracker.ui.homeScreen.todayScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.toDataState
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.SortingState
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import com.example.moneytracker.ui.usecase.GetCurrentAmountUseCase
import com.example.moneytracker.ui.usecase.GetLiabilityBalanceUseCase
import com.example.moneytracker.ui.usecase.GetTodayChartDonutDataUseCase
import com.example.moneytracker.ui.usecase.HomeData
import com.example.moneytracker.ui.usecase.ObserveUserDataUseCase
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

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val accountService: AccountServices,
    observeUserDataUseCase: ObserveUserDataUseCase,
    private val sortTodayDataSettlementUseCase: SortTodayDataSettlementUseCase,
    private val getCurrentAmountUseCase: GetCurrentAmountUseCase,
    private val getLiabilityBalanceUseCase: GetLiabilityBalanceUseCase,
    private val getTodayChartDonutDataUseCase: GetTodayChartDonutDataUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

    private val _uiState = MutableStateFlow(TodayUiState())
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    private val rawDatasetsFlow: StateFlow<HomeData> =
        observeUserDataUseCase(
            accountService.userState.map { it?.uid }
        )
            .flowOn(Dispatchers.Default)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STATE_TIMEOUT),
                HomeData(datasetState = DatasetState.Loading)
            )

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

    val todayFinance: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> datasets.filter { it.isForToday } } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val donutChartData: StateFlow<DataState<List<DonutChartData>>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
                getTodayChartDonutDataUseCase(
                    datasets.filter { it.isForToday },
                    context
                )
            }
        }
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

    val currentAccountBalance: StateFlow<DataState<Map<String, Double>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getCurrentAmountUseCase(datasets) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val liabilityBalance: StateFlow<DataState<Map<String, Double>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getLiabilityBalanceUseCase(datasets) } }
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
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

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
