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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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

    val configuration = LocalConfiguration.current
    val onActivateShow = remember { mutableStateOf(true) }



    val context = LocalContext.current
    val donutChartDataCollection = viewModel.todayChartData(context).collectAsState(emptyList())

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
                StatArea(
                    modifier = Modifier
                        .fillMaxHeight(0.4f)
                        .fillMaxWidth(0.85f)
                        .padding(bottom = 10.dp),
                    donutChartDataCollection = DonutChartDataCollection(
                        donutChartDataCollection.value
                    ),
                    datasets = todayDatasets
                )
            }

            // Items list
            ItemListArea(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(0.85f),
                viewModel = viewModel,
                onActivateShow = onActivateShow
            )
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
            StatArea(
                modifier = Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(0.3f),
                donutChartDataCollection = DonutChartDataCollection(
                    donutChartDataCollection.value
                ),
                datasets = todayDatasets
            )

            // Items list
            ItemListArea(
                Modifier
                    .fillMaxHeight(1f)
                    .fillMaxWidth(0.8f),
                viewModel = viewModel,
                onActivateShow = onActivateShow
            )
        }
    }
}

