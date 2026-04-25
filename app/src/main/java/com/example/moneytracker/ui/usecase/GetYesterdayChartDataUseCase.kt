package com.example.moneytracker.ui.usecase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.collections.ChartData
import javax.inject.Inject

class GetYesterdayChartDataUseCase @Inject constructor() {

    operator fun invoke(datasets: List<Dataset>, context: Context): List<ChartData> {
        if (datasets.isEmpty()) return emptyList()

        // 🔥 Precompute X once
        val xAxis = DoubleArray(24) { it * 3600.0 }

        // 🔥 Map<DataType, IntArray(24)>
        val resultMap = mutableMapOf<DataType, IntArray>()

        for (dataset in datasets) {
            val type = dataset.dataType

            // 🚀 Get or create bucket
            val hoursArray = resultMap.getOrPut(type) { IntArray(24) }

            // ⚡ Convert once
            val hour = dataset.createdAt.toLocalDateTimeUtc().hour

            hoursArray[hour] += dataset.amount.toInt()
        }

        // 🔥 Build final list
        return resultMap.map { (dataType, hoursArray) ->
            ChartData(
                x = xAxis.toList(),
                y = hoursArray.map { it }, // or keep as IntArray if your chart supports it
                label = dataType.text,
                color = Color(
                    ContextCompat.getColor(
                        context,
                        dataType.color
                    )
                ) // ✅ just pass resource ID or raw color
            )
        }
    }
}