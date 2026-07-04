package com.example.moneytracker.ui.homeScreen.overviewScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.FinanceRepository
import com.example.moneytracker.backend.storage.toDataState
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.usecase.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val financeRepository: FinanceRepository,
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

    private val rawDatasetsFlow: StateFlow<HomeData> = financeRepository.rawDatasetsFlow

    val allDataset: StateFlow<DataState<List<FinanceEntity>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> datasets } }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)
}
