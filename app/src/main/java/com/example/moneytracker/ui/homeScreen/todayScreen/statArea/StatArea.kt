// Hear oh Israel, The LORD our GOD, The LORD is one, Thou shall love the
// LORD thine GOD with all your soul and with all your mind
// And with all your strength and love your neighbor as your self.
package com.example.moneytracker.ui.homeScreen.todayScreen.statArea

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatValueOnly
import com.example.moneytracker.helper.getCurrencySymbol
import com.example.moneytracker.helper.isAmountEqualToSettleAmount
import com.example.moneytracker.helper.mean
import com.example.moneytracker.helper.median
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.helper.std
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.theme.StewardTheme
import kotlinx.coroutines.launch

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
                        "Refund",
                            -> (incoming + item.amount) to outgoing

                        "Expense",
                        "Lent",
                        "Savings",
                        "Payback" -> incoming to (outgoing + item.amount)

                        else -> incoming to outgoing
                    }
                }

                Log.e("Donut chart", donutChartDataCollection.items.toString())

                var enabled by remember { mutableStateOf(false) }
                LaunchedEffect(flowIn - flowOut) {
                    enabled = true
                }
                val totalAmount: Int by animateIntAsState(
                    targetValue = if (enabled) (flowIn - flowOut).toInt() else 0,
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
                val sign = if (totalAmount < 0) "-" else ""
                val symbol = getCurrencySymbol()
                Text(
                    text = "$sign$symbol ${totalAmount.toDouble().formatValueOnly()}",
                    fontWeight = fontWeight,
                    fontSize = fontSize
                )
            }
        }
    }
}

@Composable
fun DonutChartShimmer() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .shimmerEffect(shape = CircleShape, size = 150.dp)
        )
    }
}

@Composable
fun CurrentAmountBalanceSection(
    currentAmountBalance: DataState<Map<String, Double>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (currentAmountBalance) {
            is DataState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row {
                        Box(
                            modifier = Modifier.shimmerEffect(
                                shape = RoundedCornerShape(40),
                                width = 150.dp,
                                height = 25.dp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row {
                        listOf(1, 2).forEach { _ ->
                            Column(
                                modifier = Modifier.padding(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier.shimmerEffect(
                                        shape = RoundedCornerShape(40),
                                        width = 60.dp,
                                        height = 20.dp
                                    )
                                )

                                Box(
                                    modifier = Modifier.shimmerEffect(
                                        shape = RoundedCornerShape(40),
                                        width = 80.dp,
                                        height = 20.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            is DataState.Success -> {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Current Amount",
                        style = typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Row {
                        currentAmountBalance.data.forEach { (accountName, amount) ->
                            Column(
                                modifier = Modifier

                                    .padding(top = 5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Image(
                                        painter = painterResource(
                                            id = when (accountName) {
                                                "Card" -> R.drawable.credit_card
                                                "Cash" -> R.drawable.cash
                                                else -> R.drawable.steward
                                            }
                                        ),
                                        modifier = Modifier.size(25.dp),

                                        contentDescription = accountName
                                    )
                                    Text(
                                        accountName,
                                        style = typography.titleMedium
                                    )
                                }

                                var enabled by remember { mutableStateOf(false) }
                                LaunchedEffect(amount) {
                                    enabled = true
                                }
                                val animatedAmount: Int by animateIntAsState(
                                    targetValue = if (enabled) amount.toInt() else 0,
                                    label = "AccountBalanceAnimation",
                                    animationSpec = tween(1000, easing = LinearEasing)
                                )

                                val sign = if (animatedAmount < 0) "-" else ""
                                val symbol = getCurrencySymbol()
                                Column(
                                    modifier = Modifier
                                        .width(120.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$sign$symbol ${
                                            animatedAmount.toDouble().formatValueOnly()
                                        }",
                                        style = typography.labelLarge,
                                        fontFamily = FontFamily(
                                            Font(
                                                R.font.digital
                                            )
                                        ),
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            if (accountName != "Cash") {
                                VerticalDivider(modifier = Modifier.height(50.dp))
                            }
                        }
                    }
                }
            }

            is DataState.Error -> {}
        }
    }
}

fun LazyListScope.statArea(
    donutChartDataCollection: DataState<List<DonutChartData>>,
    fulfillmentFinanceEntityList: DataState<List<FinanceEntity>>,
    currentAmountBalance: DataState<Map<String, Double>>,
) {
    val idx = listOf(21, 3212)

    item {
        val pagerState = rememberPagerState { 2 }
        val scope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (currentAmountBalance) {
                is DataState.Success -> {
                    Row {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors().copy(
                                containerColor = if (pagerState.currentPage == 0)
                                    StewardTheme.colors.secondarySurface else
                                    Color.Unspecified
                            ),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.DonutLarge,
                                contentDescription = "Donut chart",
                                modifier = Modifier
                                    .padding(3.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors().copy(
                                containerColor = if (pagerState.currentPage == 1)
                                    StewardTheme.colors.secondarySurface else
                                    Color.Unspecified
                            ),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueryStats,
                                contentDescription = "Donut chart",
                                modifier = Modifier
                                    .padding(3.dp)
                            )
                        }
                    }
                }

                is DataState.Loading -> {
                    Row {
                        Box(
                            modifier = Modifier.shimmerEffect(
                                shape = CircleShape,
                                size = 40.dp
                            )
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Box(
                            modifier = Modifier.shimmerEffect(
                                shape = CircleShape,
                                size = 40.dp
                            )
                        )
                    }
                }

                is DataState.Error -> {
                    Text("Error")
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            pageSpacing = 0.dp,
            snapPosition = SnapPosition.Start,
            userScrollEnabled = true,
            // Prevent unnecessary page recreation
            key = { idx[it] },
        ) {
            when (pagerState.currentPage) {
                0 -> {
                    // Chart Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
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
                    }
                }

                1 -> {
                    // Current account Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CurrentAmountBalanceSection(currentAmountBalance)
                    }
                }
            }
        }

    }



    item {
        // Pager Area
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (fulfillmentFinanceEntityList) {
                is DataState.Success -> {
                    FulfillmentInsightPager(
                        fulfillmentFinanceEntityList.data
                            .filterNot { it.isAmountEqualToSettleAmount() })
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
