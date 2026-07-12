package com.example.moneytracker.ui.homeScreen.overviewScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.FinanceRepository
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.toDataState
import com.example.moneytracker.helper.limit
import com.example.moneytracker.helper.status
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.map
import com.example.moneytracker.ui.usecase.HomeData
import com.example.moneytracker.ui.usecase.coupleDatasetsWithSettlements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

    private val rawDatasetsFlow: StateFlow<HomeData> = financeRepository.rawDatasetsFlow

    private val _isAscending = MutableStateFlow(false)
    val isAscending: StateFlow<Boolean> = _isAscending.asStateFlow()

    private val _transactionSort = MutableStateFlow(TransactionSort.DATE)
    val transactionSort: StateFlow<TransactionSort> = _transactionSort.asStateFlow()

    private val _goalSort = MutableStateFlow(GoalSort.DEADLINE)
    val goalSort: StateFlow<GoalSort> = _goalSort.asStateFlow()

    private val _liabilitySort = MutableStateFlow(LiabilitySort.DATE)
    val liabilitySort: StateFlow<LiabilitySort> = _liabilitySort.asStateFlow()

    val allDataset: StateFlow<DataState<List<FinanceEntity>>> = combine(
        rawDatasetsFlow,
        isAscending,
        goalSort,
        liabilitySort
    ) { homeData, ascending, gSort, lSort ->
        homeData.toDataState { datasets ->
            datasets.map { entity ->
                when (entity) {
                    is FinanceEntity.Goal -> entity
                    is FinanceEntity.Liability -> entity
                    is FinanceEntity.Transaction -> entity
                }
            } // Keep all entities for filtering downstream if needed, or sort here
            // But usually we sort per section in the UI or separate flows.
            // Let's sort here for the 'allDataset' but separate flows are better for Overview.
            datasets
        }
    }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val sortedGoals: StateFlow<DataState<List<FinanceEntity.Goal>>> = combine(
        rawDatasetsFlow,
        isAscending,
        goalSort
    ) { homeData, ascending, sort ->
        homeData.toDataState { datasets ->
            val goals = datasets.filterIsInstance<FinanceEntity.Goal>()
            when (sort) {
                GoalSort.DEADLINE -> if (ascending) goals.sortedBy { it.routine.deadlineDateTime } else goals.sortedByDescending { it.routine.deadlineDateTime }
                GoalSort.AMOUNT -> if (ascending) goals.sortedBy { it.amount } else goals.sortedByDescending { it.amount }
                GoalSort.ACTIVE -> {
                    // Ascending: Active first, Descending: Inactive first
                    if (ascending) goals.sortedByDescending { it.status == Status.ACTIVE }
                    else goals.sortedBy { it.status == Status.ACTIVE }
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val sortedLiabilities: StateFlow<DataState<List<FinanceEntity.Liability>>> = combine(
        rawDatasetsFlow,
        isAscending,
        liabilitySort
    ) { homeData, ascending, sort ->
        homeData.toDataState { datasets ->
            val liabilities = datasets.filterIsInstance<FinanceEntity.Liability>()
            when (sort) {
                LiabilitySort.DATE -> if (ascending) liabilities.sortedBy { it.createdAt } else liabilities.sortedByDescending { it.createdAt }
                LiabilitySort.AMOUNT -> if (ascending) liabilities.sortedBy { it.amount } else liabilities.sortedByDescending { it.amount }
                LiabilitySort.PAID -> {
                    // Ascending: Paid first, Descending: Unpaid first
                    if (ascending) liabilities.sortedBy { it.status == Status.PAYBACK || it.status == Status.REFUNDED }
                    else liabilities.sortedByDescending { it.status == Status.PAYBACK || it.status == Status.REFUNDED }
                }

                LiabilitySort.UNPAID -> {
                    if (ascending) liabilities.sortedBy { it.status == Status.INITIAL }  // Unpaid first
                    else liabilities.sortedByDescending { it.status == Status.INITIAL }  // Paid first
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val recentActivity: StateFlow<DataState<List<DataSettlement>>> = combine(
        rawDatasetsFlow,
        isAscending,
        transactionSort
    ) { homeData, ascending, sort ->
        homeData.toDataState { entities ->
            val coupledData = coupleDatasetsWithSettlements(entities)
            val filtered = coupledData.filter {
                it is DataSettlement.SettlementData && it.financeEntity is FinanceEntity.Transaction ||
                        it is DataSettlement.SettlementWithdrawal
            }

            val sorted = when (sort) {
                TransactionSort.DATE -> if (ascending) filtered.sortedBy { it.createdAt } else filtered.sortedByDescending { it.createdAt }
                TransactionSort.AMOUNT -> if (ascending) filtered.sortedBy { it.amount } else filtered.sortedByDescending { it.amount }
                TransactionSort.NAME -> if (ascending) filtered.sortedBy { it.label } else filtered.sortedByDescending { it.label }
            }

            sorted.limit(10)
        }.map { it }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    fun toggleSortOrder() {
        _isAscending.update { !it }
    }

    fun updateTransactionSort(sort: TransactionSort) {
        _transactionSort.value = sort
    }

    fun updateGoalSort(sort: GoalSort) {
        _goalSort.value = sort
    }

    fun updateLiabilitySort(sort: LiabilitySort) {
        _liabilitySort.value = sort
    }
}
