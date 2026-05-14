// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.homeScreen.todayScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.ItemListArea
import com.example.moneytracker.ui.homeScreen.todayScreen.statArea.StatArea

@Composable
fun TodayScreen(
    paddingValues: PaddingValues,
    donutChartDataCollection: DataState<List<DonutChartData>>,
    uiState: HomeUiState,
    fulfillmentFinanceEntityList: DataState<List<FinanceEntity>>,
    homeViewModel: HomeViewModel,
    datasetWithAdjust: DataState<List<DataSettlement>>,
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
                modifier = if (!isTransactionListExpended) Modifier.weight(0.4f) else Modifier,
                exit = shrinkVertically() + fadeOut(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = LinearEasing
                    )
                )
            ) {
                StatArea(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(bottom = 10.dp),
                    donutChartDataCollection = donutChartDataCollection,
                    fulfillmentFinanceEntityList = fulfillmentFinanceEntityList
                )
            }

            ItemListArea(
                modifier = Modifier
                    .weight(if (isTransactionListExpended) 1f else 0.6f)
                    .fillMaxWidth(0.85f),
                uiState = uiState,
                viewModel = homeViewModel,
                datasetWithAdjust = datasetWithAdjust,
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedVisibility(
                visible = !isTransactionListExpended,
                modifier = if (!isTransactionListExpended) Modifier.weight(0.5f) else Modifier,
                exit = shrinkHorizontally() + fadeOut(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = LinearEasing
                    )
                )
            ) {
                StatArea(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    donutChartDataCollection = donutChartDataCollection,
                    fulfillmentFinanceEntityList = fulfillmentFinanceEntityList
                )
            }

            ItemListArea(
                modifier = Modifier
                    .weight(if (isTransactionListExpended) 1f else 0.5f)
                    .fillMaxWidth(),
                uiState = uiState,
                viewModel = homeViewModel,
                datasetWithAdjust = datasetWithAdjust,
            )
        }
    }
}

