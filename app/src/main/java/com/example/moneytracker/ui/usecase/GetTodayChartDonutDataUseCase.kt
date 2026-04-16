package com.example.moneytracker.ui.usecase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.components.charts.DonutChartData
import javax.inject.Inject

class GetTodayChartDonutDataUseCase @Inject constructor() {
    operator fun invoke(
        datasets: List<Dataset>,
        context: Context
    ): List<DonutChartData> {
        val coupledData = coupleDatasetsWithAdjustments(datasets).filter { item ->
            when (item) {
                is DataAdjust.Data -> item.dataset.isForToday
                is DataAdjust.Adjust -> item.adjustment.isForToday
            }
        }.filterNot {
            when (it) {
                is DataAdjust.Data -> it.dataset.dataType == DataType.GOAL
                is DataAdjust.Adjust -> it.adjustment.adjustmentType == AdjustmentType.GOAL_ATTAIN
            }
        }

        val grouped = coupledData.groupBy { item ->
            when (item) {
                is DataAdjust.Data -> item.dataset.dataType
                is DataAdjust.Adjust -> item.adjustment.adjustmentType
            }
        }

        return grouped.values.map { list ->
            val firstItem = list.first()

            val colorResId = when (firstItem) {
                is DataAdjust.Data -> firstItem.dataset.dataType.color
                is DataAdjust.Adjust -> firstItem.adjustment.adjustmentType.color
            }

            val title = when (firstItem) {
                is DataAdjust.Data -> firstItem.dataset.dataType.text
                is DataAdjust.Adjust -> firstItem.adjustment.adjustmentType.text
            }

            val amount = list.sumOf {
                when (it) {
                    is DataAdjust.Data -> it.dataset.amount
                    is DataAdjust.Adjust -> it.adjustment.amount
                }
            }.toFloat()

            val color = Color(ContextCompat.getColor(context, colorResId))

            DonutChartData(
                amount = amount,
                color = color,
                title = title
            )
        }
    }
}