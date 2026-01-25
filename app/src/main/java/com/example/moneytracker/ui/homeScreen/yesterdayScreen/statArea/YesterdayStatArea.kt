// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.VicoLineChart
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.components.charts.collections.ChartDataCollection


@Composable
fun YesterdayStatArea(
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
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(text = "Stats", fontWeight = FontWeight.Bold, fontSize = 20.sp)

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
fun YesterdayStatChart(datasets: List<Dataset>) {
    val groupedDataset = datasets
        .groupBy { it.dataType }
        .map { (dataType, datasets) ->
            ChartData(
                x = datasets.map { it.amount.toInt() },
                y = datasets.map {
                    val time = it.dateTime.toLocalDateTimeUtc().time
                    "${time.hour}.${time.minute}".toDouble()
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
        Text("No data to display")
    } else {
        VicoLineChart(
            chartDataCollection = ChartDataCollection(state.value),
            fillArea = true,
            xValueFormatter = { value -> value.formatToAmount() },
            yValueFormatter = { value ->
                val formatValue = String.format("%.2f", value)
                var hour = formatValue.substringBefore(",").toInt()
                var minute = formatValue.substringAfter(",").toInt()

                minute = if (minute > 60) {
                    hour += 1
                    minute -= 60
                    minute
                } else minute

                String.format("%02d:%02d", hour, minute)
            },
            markerFormatter = { x, y ->
                val formatValue = y.toString()
                val hour = formatValue.substringBefore(".").toInt()
                    .addZeroIfLessThenTen
                val minute = formatValue.substringAfter(".").toInt()

                "${x.formatToAmount()} at $hour:$minute"
            },
            showLegend = true
        )
    }
}