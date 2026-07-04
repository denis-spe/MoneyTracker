package com.example.moneytracker.ui.showAll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DataStorage
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.ui.homeScreen.DataState
import com.google.firebase.firestore.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowAllViewModel @Inject constructor(
    private val storage: DataStorage,
    private val account: AccountServices,
) : ViewModel() {
    private val _showAllDataset = MutableStateFlow(ShowAllStates())
    val showAllDataset: StateFlow<ShowAllStates> = _showAllDataset.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchQueryLiability = MutableStateFlow("")
    val searchQueryLiability: StateFlow<String> = _searchQueryLiability.asStateFlow()

    private val _searchQueryGoal = MutableStateFlow("")
    val searchQueryGoal: StateFlow<String> = _searchQueryGoal.asStateFlow()

    val filteredTransactions = combine(
        _showAllDataset,
        _searchQuery
    ) { state, query ->
        val transactionState = state.transaction
        if (transactionState is DataState.Success) {
            val filtered = if (query.isEmpty()) {
                transactionState.data
            } else {
                transactionState.data.filter {
                    it.label.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true) ||
                            it.paymentMethod.text.contains(query, ignoreCase = true) ||
                            it.transactionType.text.contains(query, ignoreCase = true)
                }
            }
            DataState.Success(filtered)
        } else {
            transactionState
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataState.Loading)

    val filteredLiabilities = combine(
        _showAllDataset,
        _searchQueryLiability
    ) { state, query ->
        val liabilityState = state.liability
        if (liabilityState is DataState.Success) {
            val filtered = if (query.isEmpty()) {
                liabilityState.data
            } else {
                liabilityState.data.filter {
                    it.label.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true) ||
                            it.liabilityType.name.contains(query, ignoreCase = true)
                }
            }
            DataState.Success(filtered)
        } else {
            liabilityState
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataState.Loading)

    val filteredGoals = combine(
        _showAllDataset,
        _searchQueryGoal
    ) { state, query ->
        val goalState = state.goal
        if (goalState is DataState.Success) {
            val filtered = if (query.isEmpty()) {
                goalState.data
            } else {
                goalState.data.filter {
                    it.label.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true) ||
                            it.routine.routine.text.contains(query, ignoreCase = true)
                }
            }
            DataState.Success(filtered)
        } else {
            goalState
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DataState.Loading)

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSearchQueryLiabilityChange(newQuery: String) {
        _searchQueryLiability.value = newQuery
    }

    fun onSearchQueryGoalChange(newQuery: String) {
        _searchQueryGoal.value = newQuery
    }


    fun loadAllTransaction() {
        viewModelScope.launch {
            // Only set to Loading if we don't have data yet to avoid flickering
            if (_showAllDataset.value.transaction !is DataState.Success) {
                _showAllDataset.value = _showAllDataset.value.copy(transaction = DataState.Loading)
            }

            val userId = account.currentUserId
            val financeType = "TRANSACTION"

            val transactions = storage.filterDatasets(
                userId = userId,
                filter = Filter.or(
                    Filter.equalTo("financeType", financeType)
                ),
            ).map { it as FinanceEntity.Transaction }
                .sortedByDescending { it.createdAt }

            _showAllDataset.value =
                _showAllDataset.value.copy(transaction = DataState.Success(transactions))
        }
    }

    fun loadAllLiability() {
        viewModelScope.launch {
            if (_showAllDataset.value.liability !is DataState.Success) {
                _showAllDataset.value = _showAllDataset.value.copy(liability = DataState.Loading)
            }

            val userId = account.currentUserId
            val financeType = "LIABILITY"

            val liabilities = storage.filterDatasets(
                userId = userId,
                filter = Filter.or(
                    Filter.equalTo("financeType", financeType)
                ),
            ).map { it as FinanceEntity.Liability }
                .sortedByDescending { it.createdAt }

            _showAllDataset.value =
                _showAllDataset.value.copy(liability = DataState.Success(liabilities))
        }
    }

    fun loadAllGoal() {
        viewModelScope.launch {
            if (_showAllDataset.value.goal !is DataState.Success) {
                _showAllDataset.value = _showAllDataset.value.copy(goal = DataState.Loading)
            }

            val userId = account.currentUserId
            val financeType = "GOAL"

            val goals = storage.filterDatasets(
                userId = userId,
                filter = Filter.or(
                    Filter.equalTo("financeType", financeType)
                ),
            ).map { it as FinanceEntity.Goal }
                .sortedByDescending { it.createdAt }

            _showAllDataset.value = _showAllDataset.value.copy(goal = DataState.Success(goals))
        }
    }
}