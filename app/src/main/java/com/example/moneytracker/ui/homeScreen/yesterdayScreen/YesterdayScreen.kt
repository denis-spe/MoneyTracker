// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// LORD GOD with all your heart and with all your soul and with all your mind
// and with all your strength, and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.yesterdayScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStatArea
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats
import com.example.moneytracker.ui.theme.StewardTheme

private val CORNER_RADIUS = 16.dp

@Composable
fun YesterdayScreen(
    paddingValues: PaddingValues,
    sortAbleDataSettlementDataState: DataState<List<DataSettlement>>,
    yesterdayChartDataState: DataState<List<ChartData>>,
    yesterdayStatsDataState: DataState<YesterdayStats>,
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
) {
    val userColor = StewardTheme.colors.secondarySurface
    val cardColor = CardDefaults.cardColors().copy(
        containerColor = userColor.copy(0.4f),
    )

    val surfaceColor = MaterialTheme.colorScheme.surface

    val blendedColor = remember(userColor, surfaceColor) {
        userColor.copy(alpha = 0.4f).compositeOver(surfaceColor)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CORNER_RADIUS)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = blendedColor)
            ) {
                YesterdayStatArea(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    chartData = yesterdayChartDataState,
                    stats = yesterdayStatsDataState
                )
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Yesterday's Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            when (sortAbleDataSettlementDataState) {
                is DataState.Error -> {
                    Text(
                        text = "Failed to load data",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                is DataState.Loading -> {
                    // Show shimmer effect
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(CORNER_RADIUS)),
                        colors = cardColor
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
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

                is DataState.Success -> {
                    val data = sortAbleDataSettlementDataState.data

                    if (data.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillParentMaxHeight(0.6f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.empty_list),
                                contentDescription = "empty list",
                                modifier = Modifier.size(120.dp),
                                alpha = 0.5f
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No activity recorded for yesterday",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        data.forEach { settlement ->
                            YesterdayItem(
                                modifier = Modifier
                                    .animateItem()
                                    .fillMaxWidth(),
                                dataSettlement = settlement,
                                showDivider = data.indexOf(settlement) < data.size - 1,
                                viewModel = viewModel,
                                userViewModel = userViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}


