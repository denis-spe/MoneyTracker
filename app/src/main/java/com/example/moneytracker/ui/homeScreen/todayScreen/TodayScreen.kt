// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.homeScreen.todayScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.ItemListArea
import com.example.moneytracker.ui.homeScreen.todayScreen.statArea.StatArea

@Composable
fun TodayScreen(
    paddingValues: PaddingValues,
    donutChartDataCollection: List<DonutChartData>,
    uiState: HomeUiState,
    todayDatasets: List<Dataset>,
    homeViewModel: HomeViewModel,
    isTodayDataLoading: Boolean,
    isTodayChartDataLoading: Boolean,
    isSortedTodayLoading: Boolean,
    datasetWithAdjust: List<DataAdjust>
) {
    val isTransactionListExpended = uiState.onActivateShow
    val configuration = LocalConfiguration.current


    if (configuration.orientation == ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedVisibility(
                visible = !isTransactionListExpended,
                exit = shrinkVertically() + fadeOut(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = LinearEasing
                    )
                )
            ) {
                if (!isTodayDataLoading && !isTodayChartDataLoading) {
                    StatArea(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .fillMaxWidth(0.85f)
                            .padding(bottom = 10.dp),
                        donutChartDataCollection = DonutChartDataCollection(
                            donutChartDataCollection
                        ),
                        datasets = todayDatasets
                    )
                } else {
                    StatAreaShimmer(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .fillMaxWidth(0.85f)
                            .padding(bottom = 10.dp)
                    )
                }
            }

            if (!isSortedTodayLoading) {
                ItemListArea(
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .fillMaxWidth(0.85f),
                    uiState = uiState,
                    viewModel = homeViewModel,
                    datasetWithAdjust = datasetWithAdjust
                )
            } else {
                ItemListAreaShimmer(
                    modifier = Modifier
                        .fillMaxHeight(1f)
                        .fillMaxWidth(0.85f)
                )
            }
        }
    }
}

