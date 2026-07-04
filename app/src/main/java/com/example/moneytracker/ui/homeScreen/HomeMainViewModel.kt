package com.example.moneytracker.ui.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.FinanceRepository
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.toDataState
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.usecase.FinanceOperationsUseCase
import com.example.moneytracker.ui.usecase.GetAdjustFinanceUseCase
import com.example.moneytracker.ui.usecase.HomeData
import com.example.moneytracker.ui.usecase.RoutineWorkerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeMainViewModel @Inject constructor(
    private val accountService: AccountServices,
    private val financeOperationsUseCase: FinanceOperationsUseCase,
    private val financeRepository: FinanceRepository,
    private val getAdjustFinanceUseCase: GetAdjustFinanceUseCase,
    private val routineWorker: RoutineWorkerUseCase,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val rawDatasetsFlow: StateFlow<HomeData> = financeRepository.rawDatasetsFlow

    init {
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

    val todayFinance: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> datasets.filter { it.isForToday } } }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val adjustFinance: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getAdjustFinanceUseCase(datasets) } }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val isDataLoaded: StateFlow<Boolean> = todayFinance.map {
        it is DataState.Success || it is DataState.Error
    }.distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), false)

    // CRUD operations
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

    fun updateOnDatasetModelBottomSheetShow(show: Boolean) =
        _uiState.update { it.copy(isDatasetBottomSheetOpen = show) }

    fun updateOnAdjustModelBottomSheetShow(show: Boolean) =
        _uiState.update { it.copy(isSettlementBottomSheetOpen = show) }

    private fun launchWithUid(block: suspend (String) -> Unit) {
        viewModelScope.launch {
            val uid = accountService.userState.value?.uid ?: return@launch
            block(uid)
        }
    }
}
