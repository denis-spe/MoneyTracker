// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.homeScreen.todayScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.ui.components.charts.collections.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.ItemListArea
import com.example.moneytracker.ui.homeScreen.todayScreen.statArea.StatArea

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayScreen(
    paddingValues: PaddingValues,
) {
    val viewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
    val todayDatasets by viewModel.todayDatasets.collectAsState()
    val todayAdjustment by viewModel.todayAdjustment.collectAsState()

    val configuration = LocalConfiguration.current
    val onActivateShow = remember { mutableStateOf(true) }



    val context = LocalContext.current
    val donutChartDataCollection = remember(todayDatasets, context) {
        DonutChartDataCollection(
            todayDatasets
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

    if (configuration.orientation == ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedVisibility(
                onActivateShow.value,
                exit = fadeOut(animationSpec = tween(easing = LinearEasing)) +
                        shrinkVertically(animationSpec = tween(easing = LinearEasing))
            ) {
                // Statistic
                StatArea(donutChartDataCollection, datasets = todayDatasets)
            }

            // Items list
            ItemListArea(todayDatasets, todayAdjustment, onActivateShow)
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Statistic
            StatArea(donutChartDataCollection, datasets = todayDatasets)

            // Items list
            ItemListArea(todayDatasets, todayAdjustment, onActivateShow)
        }
    }
}

