package com.example.moneytracker.ui.usecase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.collections.ChartData
import javax.inject.Inject

class GetYesterdayChartDataUseCase @Inject constructor() {

    operator fun invoke(financeEntityList: List<FinanceEntity>, context: Context): List<ChartData> {
        if (financeEntityList.isEmpty()) return emptyList()

        // 🔥 Precompute X once
        val xAxis = DoubleArray(24) { it * 3600.0 }

        // 🔥 Map<String, IntArray(24)>
        val resultMap = mutableMapOf<String, IntArray>()
        val colorMap = mutableMapOf<String, Int>()

        for (finance in financeEntityList) {
            val type = finance.categoryText
            colorMap[type] = finance.colorRes

            // 🚀 Get or create bucket
            val hoursArray = resultMap.getOrPut(type) { IntArray(24) }

            // ⚡ Convert once
            val hour = finance.createdAt.toLocalDateTimeUtc().hour

            hoursArray[hour] += finance.amount.toInt()
        }

        // 🔥 Build final list
        return resultMap.map { (categoryText, hoursArray) ->
            ChartData(
                x = xAxis.toList(),
                y = hoursArray.map { it }, // or keep as IntArray if your chart supports it
                label = categoryText,
                color = Color(
                    ContextCompat.getColor(
                        context,
                        colorMap[categoryText] ?: 0
                    )
                )
            )
        }
    }
}