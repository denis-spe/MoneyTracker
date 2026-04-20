// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.ui.components.charts.VicoBarChart
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.components.charts.collections.ChartDataCollection
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import kotlinx.coroutines.launch

@Composable
fun YesterdayStat(
    stats: YesterdayStats,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 230.dp, max = 400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Earned:")
            Text(stats.earnings.formatToAmount())
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Expenses:")
            Text(stats.expenses.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Settle Debts:")
            Text(stats.debts.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Refund:")
            Text(stats.lent.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Savings:")
            Text(stats.savings.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Attained:")
            Text(stats.attained.formatToAmount())
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reminder:")
            Text(stats.reminder.formatToAmount())
        }
    }
}

@Composable
fun YesterdayChart(chartData: List<ChartData>) {
    // Optionally, show a message if no data
    if (chartData.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No data to display")
        }
    } else {
        VicoBarChart(
            modifier = Modifier
                .height(230.dp),
            chartDataCollection = ChartDataCollection(chartData),
            yValueFormatter = { value -> value.formatToAmount() },

            xValueFormatter = { value ->
                val hour = (value / 3600).toInt()
                val minute = ((value % 3600) / 60).toInt()

                "%02d:%02d".format(hour, minute)
            },
            markerFormatter = { value ->
                value.formatToAmount()
            },
            showLegend = true
        )
    }
}

@Composable
fun YesterdayStatArea(
    modifier: Modifier = Modifier,
    datasets: List<Dataset>,
    chartData: List<ChartData>,
    stats: YesterdayStats
) {
    // Page state
    val pageState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = {
                    scope.launch {
                        pageState.animateScrollToPage(0)
                    }
                },
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = if (pageState.currentPage == 0)
                        Color.White else MoneyTrackerTheme.colors.autoText,
                    containerColor = if (pageState.currentPage == 0) {
                        MoneyTrackerTheme.colors.themeColor
                    } else {
                        Color.Transparent
                    }
                )
            ) {
                Text("Stat")
            }
            TextButton(
                onClick = {
                    scope.launch {
                        pageState.animateScrollToPage(1)
                    }
                },
                colors = ButtonDefaults.textButtonColors().copy(
                    contentColor = if (pageState.currentPage == 1)
                        Color.White else MoneyTrackerTheme.colors.autoText,
                    containerColor = if (pageState.currentPage == 1) {
                        MoneyTrackerTheme.colors.themeColor
                    } else {
                        Color.Transparent
                    }
                )
            ) {
                Text("Chart")
            }
        }

        HorizontalPager(
            state = pageState
        ) {
            when (it) {
                0 -> YesterdayStat(stats)
                1 -> YesterdayChart(chartData)
            }
        }
    }
}