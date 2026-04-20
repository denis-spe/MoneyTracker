package com.example.moneytracker.ui.usecase

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.mean
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.collections.ChartData
import javax.inject.Inject

class GetYesterdayChartDataUseCase @Inject constructor() {
    operator fun invoke(datasets: List<Dataset>, context: Context): List<ChartData> {
        return datasets
            .groupBy { it.dataType }
            .map { (dataType, datasets) ->
                val x = listOf(datasets.mean {
                    val time = it.createdAt.toLocalDateTimeUtc()
                    ((time.hour * 3600) + (time.minute * 60) + time.second).toDouble()
                })
                val y = listOf(datasets.sumOf { it.amount.toInt() })

                ChartData(
                    x = x,
                    y = y,
                    label = dataType.text,
                    color = Color(ContextCompat.getColor(context, dataType.color))
                )
            }
    }
}