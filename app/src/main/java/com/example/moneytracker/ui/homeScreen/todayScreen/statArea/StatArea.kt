// Hear oh Israel, The LORD our GOD, The LORD is one, Thou shall love the
// LORD thine GOD with all your soul and with all your mind
// And with all your strength and love your neighbor as your self.
package com.example.moneytracker.ui.homeScreen.todayScreen.statArea

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.mean
import com.example.moneytracker.helper.median
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.helper.std
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.DataState

@Composable
fun Stat(
    financeEntityList: List<FinanceEntity>
) {
    if (financeEntityList.isEmpty()) return

    val mean = financeEntityList.groupBy { data -> data.categoryText }.map { entry ->
        entry.key to entry.value.mean { it.amount }
    }

    val median = financeEntityList.groupBy { data -> data.categoryText }.map { entry ->
        entry.key to entry.value.median
    }

    val min = financeEntityList.groupBy { data -> data.categoryText }.map { entry ->
        entry.key to entry.value.minOf { it.amount }
    }

    val max = financeEntityList.groupBy { data -> data.categoryText }.map { entry ->
        entry.key to entry.value.maxOf { it.amount }
    }

    val std = financeEntityList.groupBy { data -> data.categoryText }.map { entry ->
        entry.key to entry.value.std
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
fun DonutChartPager(
    donutChartDataCollection: DonutChartDataCollection,
    fontWeight: FontWeight,
    fontSize: TextUnit
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
                val (flowIn, flowOut) = donutChartDataCollection.items.fold(0f to 0f) { (incoming, outgoing), item ->
                    when (item.title) {
                        "Earnings",
                        "Debt",
                        "Loan Refund",
                            -> (incoming + item.amount) to outgoing

                        "Expense",
                        "Lent",
                        "Savings",
                        "Debt Payback" -> incoming to (outgoing + item.amount)

                        else -> incoming to outgoing
                    }
                }

                var enabled by remember { mutableStateOf(true) }
                val totalAmount: Float by animateFloatAsState(
                    if (enabled)
                        flowIn - flowOut
                    else 0f,
                    label = "Amount flow",
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = LinearEasing,
                    )
                )

                Text(
                    text = "Flow",
                    fontWeight = fontWeight,
                    fontSize = fontSize
                )
                Text(
                    text = totalAmount.formatToAmount(),
                    fontWeight = fontWeight,
                    fontSize = fontSize
                )
            }
        }
    }
}

@Composable
fun DonutChartShimmer() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1.1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = CircleShape, size = 150.dp)
            )
        }
    }
}


@Composable
fun StatArea(
    modifier: Modifier = Modifier,
    donutChartDataCollection: DataState<List<DonutChartData>>,
    fulfillmentFinanceEntityList: DataState<List<FinanceEntity>>,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Chart Area
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (donutChartDataCollection) {
                is DataState.Success -> {
                    val donutChartData = DonutChartDataCollection(
                        donutChartDataCollection.data
                    )
                    DonutChartPager(
                        donutChartData,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                is DataState.Loading -> {
                    DonutChartShimmer()
                }

                is DataState.Error -> {
                    Text("Error in Loading")
                }

            }
        }

        // Pager Area
        Box(
            modifier = Modifier
                .weight(0.5f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (fulfillmentFinanceEntityList) {
                is DataState.Success -> {
                    FulfillmentInsightPager(fulfillmentFinanceEntityList.data)
                }

                is DataState.Loading -> {
                    FulfillmentInsightPagerShimmer()
                }

                is DataState.Error -> {
                    Text("Failed to Loading fulfillment data")
                }

            }
        }
    }
}
