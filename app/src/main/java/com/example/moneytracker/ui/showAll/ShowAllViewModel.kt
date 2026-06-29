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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowAllViewModel @Inject constructor(
    private val storage: DataStorage,
    private val account: AccountServices,
) : ViewModel() {
    private val _showAllDataset = MutableStateFlow(ShowAllStates())
    val showAllDataset: StateFlow<ShowAllStates> = _showAllDataset.asStateFlow()


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