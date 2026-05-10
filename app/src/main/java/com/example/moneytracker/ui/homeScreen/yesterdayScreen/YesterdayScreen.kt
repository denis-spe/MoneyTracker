// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// LORD GOD with all your heart and with all your soul and with all your mind
// and with all your strength, and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.yesterdayScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.dataAddition.FONT_WEIGHT
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStatArea
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import com.example.moneytracker.ui.theme.MoneyTrackerTheme

@Composable
fun YesterdayScreen(
    paddingValues: PaddingValues,
    sortAbleDataSettlementDataState: DataState<List<DataSettlement>>,
    yesterdayChartDataState: DataState<List<ChartData>>,
    yesterdayStatsDataState: DataState<YesterdayStats>,
    uiState: HomeUiState,
) {
    val configuration = LocalConfiguration.current
    val userColor = MoneyTrackerTheme.colors.customBackground
    val cardColor = CardDefaults.cardColors().copy(
        containerColor = userColor.copy(0.4f)
    )

    if (configuration.orientation == ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = cardColor,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            YesterdayStatArea(
                                modifier = Modifier.fillMaxWidth(),
                                chartData = yesterdayChartDataState,
                                stats = yesterdayStatsDataState
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.size(20.dp)) }
                item {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Text(
                            "Late Transactions",
                            fontSize = 18.sp,
                            fontWeight = FONT_WEIGHT,
                        )
                    }
                }
                item { Spacer(modifier = Modifier.size(10.dp)) }
                when (sortAbleDataSettlementDataState) {
                    is DataState.Error -> {
                        item {
                            Text(
                                "Failed to load data",
                                color = Color.Red,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }

                    is DataState.Loading -> {
                        // Show shimmer effect
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                colors = cardColor
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    repeat(7) {
                                        YesterdayItemShimmer(
                                            modifier = Modifier.animateItem()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    is DataState.Success -> {
                        val data = sortAbleDataSettlementDataState.data

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                colors = cardColor
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    data.forEach {
                                        YesterdayItem(
                                            modifier = Modifier.animateItem(),
                                            dataSettlement = it
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.size(10.dp)) }
            }
        }
    }
}


