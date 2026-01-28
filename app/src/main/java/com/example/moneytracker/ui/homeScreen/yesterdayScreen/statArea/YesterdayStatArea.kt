// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.VicoLineChart
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.components.charts.collections.ChartDataCollection
import com.example.moneytracker.ui.theme.autoTextColorChange
import kotlinx.coroutines.launch


@Composable
fun YesterdayStat(
    datasets: List<Dataset>,
) {
    val earnings = datasets.filter { it.dataType == DataType.EARNINGS }.sumOf { it.amount }
    val expenses = datasets.filter { it.dataType == DataType.EXPENSE }.sumOf { it.amount }
    val debts = datasets.filter { it.dataType == DataType.DEBT }.sumOf { it.amount }
    val lent = datasets.filter { it.dataType == DataType.LENT }.sumOf { it.amount }
    val savings = datasets.filter { it.dataType === DataType.SAVINGS }.sumOf { it.amount }
    datasets.filter { it.dataType == DataType.GOAL }.sumOf { it.amount }
    val attained = datasets.filter { it.dataType == DataType.GOAL }
        .map { it.adjustment }
        .flatten()
        .sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 230.dp, max = 400.dp)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Earned:")
            Text(earnings.formatToAmount())
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Expenses:")
            Text(expenses.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Settle Debts:")
            Text(debts.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Refund:")
            Text(lent.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Savings:")
            Text(savings.formatToAmount())
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Attained:")
            Text(attained.formatToAmount())
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Reminder:")
            val reminder = (earnings - expenses) - (debts + lent + savings + attained)
            Text(reminder.formatToAmount())
        }
    }
}

@Composable
fun YesterdayChart(datasets: List<Dataset>) {
    val groupedDataset = datasets
        .groupBy { it.dataType }
        .map { (dataType, datasets) ->
            ChartData(
                x = datasets.map { it.amount.toInt() },
                y = datasets.map {
                    val time = it.dateTime.toLocalDateTimeUtc().time
                    ((time.hour * 3600) + (time.minute * 60) + time.second).toDouble()
                }, // x: hour , // y: amount
                label = dataType.text,
                color = colorResource(id = dataType.color)
            )
        }

    val state = remember { mutableStateOf<List<ChartData>>(emptyList()) }

    LaunchedEffect(datasets) {
        state.value = groupedDataset
    }

    // Optionally, show a message if no data
    if (state.value.isEmpty()) {
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
        VicoLineChart(
            modifier = Modifier
                .height(230.dp),
            chartDataCollection = ChartDataCollection(state.value),
            fillArea = true,
            xValueFormatter = { value -> value.formatToAmount() },
            yValueFormatter = { value ->
                val hour = (value / 3600).toInt()
                val minute = ((value % 3600) / 60).toInt()

                "%02d:%02d".format(hour, minute)
            },
            markerFormatter = { x, y ->
                val hour = (y / 3600).toInt()
                val minute = ((y % 3600) / 60).toInt()

                val time = "%02d:%02d".format(hour, minute)

                "${x.formatToAmount()} at $time"
            },
            showLegend = true
        )
    }
}

@Composable
fun YesterdayStatArea(datasets: List<Dataset>) {
    // Page state
    val pageState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 230.dp, max = 400.dp),
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
                        Color.White else Color.autoTextColorChange,
                    containerColor = if (pageState.currentPage == 0) {
                        Color.Blue
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
                        Color.White else Color.autoTextColorChange,
                    containerColor = if (pageState.currentPage == 1) {
                        Color.Blue
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
                0 -> YesterdayStat(datasets)
                1 -> YesterdayChart(datasets)
            }
        }
    }
}