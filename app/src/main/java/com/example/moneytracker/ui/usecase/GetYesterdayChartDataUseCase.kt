package com.example.moneytracker.ui.usecase

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.isCreatedAtEqualTo
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.collections.ChartData
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import network.chaintech.kmp_date_time_picker.utils.now
import javax.inject.Inject

class GetYesterdayChartDataUseCase @Inject constructor() {

    operator fun invoke(financeEntityList: List<FinanceEntity>, context: Context): List<ChartData> {
        return try {
            if (financeEntityList.isEmpty()) return emptyList()

            val yesterday = LocalDateTime.now().date.minus(1, DateTimeUnit.DAY)

            // 🔥 Precompute X once
            val xAxis = DoubleArray(24) { it * 3600.0 }

            // 🔥 Map<String, DoubleArray(24)>
            val resultMap = mutableMapOf<String, DoubleArray>()
            val colorMap = mutableMapOf<String, Int>()

            for (finance in financeEntityList) {
                // Process the main entity if it was created yesterday
                if (finance.isCreatedAtEqualTo(yesterday)) {
                    val type = finance.categoryText
                    colorMap[type] = finance.colorRes

                    // 🚀 Get or create bucket
                    val hoursArray = resultMap.getOrPut(type) { DoubleArray(24) }

                    // ⚡ Convert once
                    val hour = finance.createdAt.toLocalDateTimeUtc().hour
                    if (hour in 0..23) {
                        hoursArray[hour] += finance.amount
                    }
                }

                // Process settlements regardless of when the parent entity was created
                val settlements = when (finance) {
                    is FinanceEntity.Goal -> finance.settlement
                    is FinanceEntity.Liability -> finance.settlement
                    is FinanceEntity.Transaction -> emptyList()
                }

                for (settlement in settlements) {
                    if (settlement.isCreatedAtEqualTo(yesterday)) {
                        val type = settlement.settlementType.text
                        colorMap[type] = settlement.settlementType.color

                        val hoursArray = resultMap.getOrPut(type) { DoubleArray(24) }
                        val hour = settlement.dateTime.toLocalDateTimeUtc().hour
                        if (hour in 0..23) {
                            hoursArray[hour] += settlement.amount
                        }
                    }
                }
            }

            // 🔥 Build final list
            resultMap.map { (categoryText, hoursArray) ->
                val colorRes = colorMap[categoryText] ?: android.R.color.darker_gray
                ChartData(
                    x = xAxis.toList(),
                    y = hoursArray.map { it },
                    label = categoryText,
                    color = Color(ContextCompat.getColor(context, colorRes))
                )
            }
        } catch (e: Exception) {
            Log.e("GetYesterdayChartData", "Error generating chart data", e)
            emptyList()
        }
    }
}
