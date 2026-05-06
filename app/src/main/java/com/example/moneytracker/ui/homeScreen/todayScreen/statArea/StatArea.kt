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
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.mean
import com.example.moneytracker.helper.median
import com.example.moneytracker.helper.std
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection

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
fun StatArea(
    modifier: Modifier = Modifier,
    donutChartDataCollection: DonutChartDataCollection,
    todayFinanceEntityList: List<FinanceEntity>,
    fulfillmentFinanceEntityList: List<FinanceEntity>,
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
    rememberPagerState(pageCount = { items.size })

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Chart Area
        Box(
            modifier = Modifier
                .weight(1.1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                DonutChartPager(
                    donutChartDataCollection,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // Pager Area
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FulfillmentInsightPager(fulfillmentFinanceEntityList)
            }
        }

    }
}