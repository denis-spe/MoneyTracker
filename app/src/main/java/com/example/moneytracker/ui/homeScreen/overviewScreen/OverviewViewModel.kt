package com.example.moneytracker.ui.homeScreen.overviewScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.toDataState
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.usecase.HomeData
import com.example.moneytracker.ui.usecase.ObserveUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val accountService: AccountServices,
    observeUserDataUseCase: ObserveUserDataUseCase,
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

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

    val allDataset: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> datasets } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)
}
