package com.example.moneytracker.ui.homeScreen.yesterdayScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceRepository
import com.example.moneytracker.backend.storage.toDataState
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import com.example.moneytracker.ui.usecase.GetYesterdayChartDataUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayDataSettlementUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayFinanceUseCase
import com.example.moneytracker.ui.usecase.GetYesterdayStatsUseCase
import com.example.moneytracker.ui.usecase.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class YesterdayViewModel @Inject constructor(
    financeRepository: FinanceRepository,
    private val getYesterdayDataSettlementUseCase: GetYesterdayDataSettlementUseCase,
    private val getYesterdayFinanceUseCase: GetYesterdayFinanceUseCase,
    private val getYesterdayChartDataUseCase: GetYesterdayChartDataUseCase,
    private val getYesterdayStatsUseCase: GetYesterdayStatsUseCase,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    companion object {
        const val STATE_TIMEOUT = 5_000L
    }

    private val rawDatasetsFlow: StateFlow<HomeData> = financeRepository.rawDatasetsFlow

    val yesterdayChartData: StateFlow<DataState<List<ChartData>>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
                getYesterdayChartDataUseCase(
                    getYesterdayFinanceUseCase(datasets),
                    context
                )
            }
        }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val yesterdayStats: StateFlow<DataState<YesterdayStats>> = rawDatasetsFlow
        .map { homeData ->
            homeData.toDataState { datasets ->
                getYesterdayStatsUseCase(getYesterdayFinanceUseCase(datasets))
            }
        }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)

    val sortedYesterday: StateFlow<DataState<List<DataSettlement>>> = rawDatasetsFlow
        .map { it.toDataState { datasets -> getYesterdayDataSettlementUseCase(datasets) } }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT), DataState.Loading)
}
