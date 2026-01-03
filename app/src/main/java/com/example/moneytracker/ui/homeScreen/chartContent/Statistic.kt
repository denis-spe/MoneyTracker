// Hear oh Israel, The LORD our GOD, The LORD is one, Thou shall love the
// LORD thine GOD with all your soul and with all your mind
// And with all your strength and love your neighbor as your self.
package com.example.moneytracker.ui.homeScreen.chartContent

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.mean
import com.example.moneytracker.helper.median
import com.example.moneytracker.helper.std
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.InsightBar
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection

@Composable
fun Stat(
    datasets: List<Dataset>
) {
    if (datasets.isEmpty()) return

    val mean = datasets.groupBy { data -> data.dataType }.map { dataset ->
        dataset.key.text to dataset.value.mean
    }

    val median = datasets.groupBy { data -> data.dataType }.map { dataset ->
        dataset.key.text to dataset.value.median
    }

    val min = datasets.groupBy { data -> data.dataType }.map { dataset ->
        dataset.key.text to dataset.value.minOf { it.amount }
    }

    val max = datasets.groupBy { data -> data.dataType }.map { dataset ->
        dataset.key.text to dataset.value.maxOf { it.amount }
    }

    val std = datasets.groupBy { data -> data.dataType }.map { dataset ->
        dataset.key.text to dataset.value.std
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.7f),
    ) {
        item {
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                item {
                    Text(text = "Label")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "min")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "median")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "max")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "std")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "mean")

                }
            }
        }
        items(min.size) {
            val minItem = min[it]
            val maxItem = max[it]
            val stdItem = std[it]
            val meanItem = mean[it]
            val medianItem = median[it]

            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                item {
                    Text(text = minItem.first)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = minItem.second.formatToAmount())
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = medianItem.second.formatToAmount())
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = maxItem.second.formatToAmount())
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = stdItem.second.formatToAmount())
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = meanItem.second.formatToAmount())

                }
            }
        }
    }

}

@Composable
fun InsightBarPager(
    datasets: List<Dataset>,
) {
    val earnings = datasets.filter { it.dataType == DataType.EARNINGS }.sumOf { it.amount }
    val expense = datasets.filter { it.dataType == DataType.EXPENSE }.sumOf { it.amount }

    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var progress by remember { mutableDoubleStateOf(0.0) }

        LaunchedEffect(Unit) {
            progress = expense / earnings
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "${(progress * 100).toInt()}% of your earnings was spent",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        InsightBar(
            progress.toFloat(),
            modifier = Modifier.fillMaxWidth(0.6f),
            color = colorResource(R.color.Expense),
            barColor = colorResource(R.color.Earnings)
        )
    }
}

@Composable
fun DonutChartPager(
    donutChartDataCollection: DonutChartDataCollection,
    fontWeight: FontWeight,
    fontSize: TextUnit
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DonutChart(
            data = donutChartDataCollection,
            chartSize = 150.dp,
            gapPercentage = 0.06f,
            strokeCap = StrokeCap.Round,
            strokeWidthSelected = 30.dp
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                it?.let { donutChartData ->
                    Text(
                        text = donutChartData.title,
                        fontWeight = fontWeight, fontSize = fontSize
                    )
                    Text(
                        text = donutChartData.amount.formatToAmount(),
                        fontWeight = fontWeight, fontSize = fontSize
                    )
                } ?: run {
                    var enabled by remember { mutableStateOf(true) }
                    val totalAmount: Float by animateFloatAsState(
                        if (enabled)
                            donutChartDataCollection.totalAmount
                        else 0f,
                        label = "Overall Amount",
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = LinearEasing,
                        )
                    )

                    Text(
                        text = "Overall",
                        fontWeight = fontWeight, fontSize = fontSize
                    )
                    Text(
                        text = totalAmount.formatToAmount(),
                        fontWeight = fontWeight, fontSize = fontSize
                    )
                }
            }
        }
    }
}

@Composable
fun Statistic(
    donutChartDataCollection: DonutChartDataCollection,
    datasets: List<Dataset>,
) {
    val fontWeight = FontWeight.Bold
    val fontSize = 16.sp
    var state by remember { mutableStateOf("Chart") }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.3f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    state = "Chart"
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back"
                )
            }

            Row(
                modifier = Modifier
                    .width(80.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state,
                    fontWeight = fontWeight, fontSize = fontSize
                )
            }

            IconButton(
                onClick = { state = "Statistic" }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Forward"
                )
            }
        }
        when (state) {
            "Chart" -> {
                DonutChartPager(
                    donutChartDataCollection,
                    fontWeight = fontWeight,
                    fontSize = fontSize
                )
            }

            "Statistic" -> {
                InsightBarPager(datasets)
            }
        }
    }

}