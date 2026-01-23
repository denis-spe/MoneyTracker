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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.rememberPagerState
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
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.mean
import com.example.moneytracker.helper.median
import com.example.moneytracker.helper.std
import com.example.moneytracker.ui.components.charts.DonutChart
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
fun StatArea(
    donutChartDataCollection: DonutChartDataCollection,
    datasets: List<Dataset>,
) {
    val items = listOf(
        PagerItem(
            "EarningsVsExpense",
            Pair(R.color.Earnings, R.color.Expense)
        ),
        PagerItem(
            "DebtVsEarnings",
            Pair(R.color.Earnings, R.color.Debt)
        ),
        PagerItem(
            "LentVsEarnings",
            Pair(R.color.Earnings, R.color.Lent)
        ),
        PagerItem(
            "SavingsVsEarnings",
            Pair(R.color.Earnings, R.color.Savings)
        ),
        PagerItem(
            "DebtRepay",
            Pair(R.color.Debt, R.color.RepayLoan)
        ),
        PagerItem(
            "LentRepay",
            Pair(R.color.Lent, R.color.RepayLoan)
        ),
        PagerItem(
            "GoalVsEarnings",
            Pair(R.color.Earnings, R.color.Goal)
        ),
        PagerItem(
            "GoalVsScore",
            Pair(R.color.Goal, R.color.Attain)
        )
        )
    val pagerState = rememberPagerState(pageCount = { items.size })

    Column(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .heightIn(max = 280.dp, min = 180.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Chart Area
        Box(modifier = Modifier.weight(0.8f)) {
            DonutChartPager(
                donutChartDataCollection,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        // Pager Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f), // Using weight is safer than fillMaxHeight(0.2f)
            contentAlignment = Alignment.Center
        ) {
            InsightsPager(datasets, pagerState, items)
        }

        // Pager Indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f),
            contentAlignment = Alignment.Center
        ) {
            CurrentPagerIndicator(pagerState = pagerState, items = items)
        }
    }

}