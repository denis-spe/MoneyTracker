// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.homeScreen.todayScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.moneytracker.ui.theme.StewardTheme

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

    StewardTheme.colors.secondarySurface
    val cardColor = CardDefaults.cardColors().copy(
        containerColor = Color.Transparent
    )

    val statWeight by animateFloatAsState(
        targetValue = if (isTransactionListExpended) 0.001f else 0.45f,
        label = "stat weight",
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    val listWeight by animateFloatAsState(
        targetValue = if (isTransactionListExpended) 1f else 0.55f,
        label = "list weight",
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )


    if (configuration.orientation == ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !isTransactionListExpended,
                modifier = Modifier
                    .weight(statWeight)
                    .fillMaxWidth(0.95f)
                    .padding(bottom = 10.dp),
                enter = expandVertically(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) + fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = cardColor
                ) {
                    StatArea(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        donutChartDataCollection = donutChartDataCollection,
                        fulfillmentFinanceEntityList = fulfillmentFinanceEntityList
                    )
                }
            }

            ItemListArea(
                modifier = Modifier
                    .weight(listWeight)
                    .fillMaxWidth(0.95f),
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = !isTransactionListExpended,
                modifier = Modifier
                    .weight(statWeight)
                    .fillMaxHeight(),
                enter = expandHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) + fadeIn(),
                exit = shrinkHorizontally(
                    animationSpec = tween(500, easing = FastOutSlowInEasing)
                ) + fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    colors = cardColor
                ) {
                    StatArea(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        donutChartDataCollection = donutChartDataCollection,
                        fulfillmentFinanceEntityList = fulfillmentFinanceEntityList
                    )
                }
            }

            ItemListArea(
                modifier = Modifier
                    .weight(listWeight)
                    .fillMaxHeight(),
                uiState = uiState,
                viewModel = homeViewModel,
                datasetWithAdjust = datasetWithAdjust,
            )
        }
    }
}

