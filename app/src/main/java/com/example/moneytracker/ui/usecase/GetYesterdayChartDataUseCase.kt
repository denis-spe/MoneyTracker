package com.example.moneytracker.ui.usecase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.collections.ChartData
import javax.inject.Inject

class GetYesterdayChartDataUseCase @Inject constructor() {
    operator fun invoke(datasets: List<Dataset>, context: Context): List<ChartData> {
        if (datasets.isEmpty()) return emptyList()

        return datasets
            .groupBy { it.dataType }
            .map { (dataType, typeDatasets) ->
                // Group by hour to show a better distribution over time
                val hourMap = typeDatasets.groupBy {
                    it.createdAt.toLocalDateTimeUtc().hour
                }

                // Ensure all 24 hours are represented to keep X-axis consistent and prevent bars from stretching
                val x = (0..23).map { it.toDouble() * 3600 }
                val y = (0..23).map { hour ->
                    hourMap[hour]?.sumOf { it.amount.toInt() } ?: 0
                }

                ChartData(
                    x = x,
                    y = y,
                    label = dataType.text,
                    color = Color(ContextCompat.getColor(context, dataType.color))
                )
            }
    }
}