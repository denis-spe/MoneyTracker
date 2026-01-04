// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.homeScreen.todayScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.components.charts.collections.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.ItemListArea
import com.example.moneytracker.ui.homeScreen.todayScreen.statArea.StatArea

@Composable
fun TodayScreen(
    paddingValues: PaddingValues,
    datasets: List<Dataset>
) {
    val filteredDatasets = datasets.filter { it.isForToday }

    val context = LocalContext.current
    val donutChartDataCollection = remember(filteredDatasets, context) {
        DonutChartDataCollection(
            filteredDatasets
                .groupBy { it.dataType }
                .values.toList()
                .map { lst ->
                    val firstItemInList = lst[0]
                    val amount = lst.sumOf { it.amount }.toFloat()
                    val colorInt = ContextCompat.getColor(context, firstItemInList.dataType.color)
                    val color = Color(colorInt)
                    val title = firstItemInList.dataType.text

                    DonutChartData(
                        amount,
                        color = color,
                        title = title
                    )
                }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Statistic
        StatArea(donutChartDataCollection, datasets = filteredDatasets)

        // Items list
        ItemListArea(filteredDatasets)
    }
}

