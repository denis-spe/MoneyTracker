package com.example.moneytracker.ui.homeScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.usecase.GetTodayChartDonutDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val getTodayChartDonutDataUseCase: GetTodayChartDonutDataUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun todayChartData(financeEntityList: List<FinanceEntity>): Flow<List<DonutChartData>> {
        return kotlinx.coroutines.flow.flowOf(
            getTodayChartDonutDataUseCase(
                financeEntityList,
                context
            )
        )
    }
}
