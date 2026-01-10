// Praise the LORD of God, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.todayScreen.statArea

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.ui.components.charts.InsightBar
import kotlinx.coroutines.launch

@Composable
fun Insights(
    firstFinancial: Double,
    secondFinancial: Double,
    colorResId: Int,
    barColorResId: Int,
    builder: () -> AnnotatedString
) {

    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var percentage by remember { mutableDoubleStateOf(0.0) }
        var marginRatio by remember { mutableDoubleStateOf(0.0) }
        var text by remember { mutableStateOf(buildAnnotatedString { "" }) }

        LaunchedEffect(firstFinancial, secondFinancial) {
            if (firstFinancial == 0.0) {
                percentage = 0.0
                marginRatio = 0.0 // Ensure it's not NaN
            } else {
                percentage = secondFinancial / firstFinancial

                // Calculate the raw difference ratio
                val ratio = (secondFinancial - firstFinancial) / firstFinancial

                // Use absolute value for the animation to prevent "NaN" or negative visuals
                // We handle the "direction" (Loss vs Gain) in the text builder
                marginRatio = if (ratio.isNaN()) 0.0 else ratio
            }
        }

        val animatePercentage = animateIntAsState(
            targetValue = (percentage.toFloat() * 100).toInt(),
            animationSpec = tween(durationMillis = 700, easing = LinearEasing)
        )
        val animateNetLoss = animateIntAsState(
            targetValue = (marginRatio.toFloat() * 100).toInt(),
            animationSpec = tween(durationMillis = 700, easing = LinearEasing)
        )

        LaunchedEffect(firstFinancial, secondFinancial) {
            text = builder()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }

        val barColor = when {
            firstFinancial > 0
                    && firstFinancial > secondFinancial
                    && secondFinancial != 0.0 -> colorResource(barColorResId)
                .copy(0.3f)

            firstFinancial == 0.0 && secondFinancial == 0.0 -> colorResource(barColorResId)
                .copy(0.3f)

            secondFinancial > firstFinancial -> colorResource(colorResId)
            else -> {
                colorResource(barColorResId)
            }
        }

        InsightBar(
            percentage.toFloat(),
            modifier = Modifier.fillMaxWidth(0.6f),
            color = colorResource(colorResId),
            barColor = barColor

        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${animatePercentage.value}% (${
                    if (animatePercentage.value > 0) animateNetLoss.value else 0
                } margin ratio)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CurrentPagerIndicator(
    pagerState: PagerState,
    items: List<PagerItem>,
) {
    val pagerScope = rememberCoroutineScope()
    val buttonSize = 10.dp
    val currentWidth = animateIntAsState(
        targetValue = 15,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing),
        label = "Current Width"
    )


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        items.forEachIndexed { idx, value ->
            if (pagerState.currentPage != idx) {
                IconButton(
                    onClick = {
                        pagerScope.launch {
                            pagerState.animateScrollToPage(idx)
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier.size(buttonSize)
                ) {

                    Box(
                        modifier = Modifier
                            .size(buttonSize)
                            .border(
                                1.dp,
                                color = colorResource(value.color.second),
                                CircleShape
                            )
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        pagerScope.launch {
                            pagerState.animateScrollToPage(idx)
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .height(buttonSize)
                        .width(currentWidth.value.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .height(buttonSize)
                            .width(currentWidth.value.dp)
                            .background(
                                colorResource(value.color.second),
                                RoundedCornerShape(10.dp)
                            )
                    )

                }
            }

            if (idx != items.size - 1) {
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}

@Composable
fun InsightsPager(datasets: List<Dataset>, pagerState: PagerState, items: List<PagerItem>) {

    val earnings =
        datasets.filter { it.dataType == DataType.EARNINGS }.sumOf { it.amount }
    val expense =
        datasets.filter { it.dataType == DataType.EXPENSE }.sumOf { it.amount }
    val debt =
        datasets.filter { it.dataType == DataType.DEBT }.sumOf { it.amount }
    val lent =
        datasets.filter { it.dataType == DataType.LENT }.sumOf { it.amount }
    val savings =
        datasets.filter { it.dataType == DataType.SAVINGS }.sumOf { it.amount }
    val debtRepay =
        datasets.filter { it.dataType == DataType.DEBT }.map { it.adjustment }
            .flatten()
            .sumOf { it.amount }
    val lentRepay =
        datasets.filter { it.dataType == DataType.LENT }.map { it.adjustment }
            .flatten()
            .sumOf { it.amount }
    val goal =
        datasets.filter { it.dataType == DataType.GOAL }.sumOf { it.amount }
    val score = datasets.filter { it.dataType == DataType.GOAL }
        .map { it.adjustment }
        .flatten()
        .sumOf { it.amount }



    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { it }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val pagerItem = items[it]

                when (pagerItem.label) {
                    "EarningsVsExpense" -> {
                        Insights(
                            firstFinancial = earnings,
                            secondFinancial = expense,
                            colorResId = R.color.Expense,
                            barColorResId = R.color.Earnings,
                        ) {
                            if (expense > earnings) {
                                buildAnnotatedString {
                                    append("You spent more then your earnings")
                                }
                            } else if (earnings > 0) {
                                buildAnnotatedString {
                                    append("Earnings are more then your expenses")
                                }
                            } else {
                                buildAnnotatedString {
                                    append("You have no earnings")
                                }
                            }
                        }
                    }

                    "DebtVsEarnings" -> {
                        Insights(
                            firstFinancial = earnings,
                            secondFinancial = debt,
                            colorResId = R.color.Debt,
                            barColorResId = R.color.Earnings,
                        ) {
                            buildAnnotatedString {
                                if (debt > earnings) {
                                    append("You have more debt than your earnings")
                                } else if (earnings > 0) {
                                    append("Earnings are more then your debt")
                                } else {
                                    append("You have no debts to worry about")
                                }
                            }
                        }
                    }

                    "GoalVsEarnings" -> {
                        Insights(
                            firstFinancial = earnings,
                            secondFinancial = goal,
                            colorResId = R.color.Goal,
                            barColorResId = R.color.Earnings,
                        ) {
                            buildAnnotatedString {
                                if (goal == earnings) {
                                    append("You have more goal than your earnings")
                                } else if (earnings > 0) {
                                    append("Earnings are more then your goal")
                                } else {
                                    append("You have no goal")
                                }
                            }
                        }
                    }

                    "LentVsEarnings" -> {
                        Insights(
                            firstFinancial = earnings,
                            secondFinancial = lent,
                            colorResId = R.color.Lent,
                            barColorResId = R.color.Earnings,
                        ) {
                            if (lent > earnings) {
                                buildAnnotatedString {
                                    append("You lend more than your earnings")
                                }
                            } else {
                                buildAnnotatedString {
                                    append("You have not lent to any one")
                                }
                            }
                        }
                    }

                    "SavingsVsEarnings" -> {
                        Insights(
                            firstFinancial = earnings,
                            secondFinancial = savings,
                            colorResId = R.color.Savings,
                            barColorResId = R.color.Earnings
                        ) {
                            if (savings > earnings) {
                                buildAnnotatedString {
                                    append("You have more savings than your earnings")
                                }
                            } else {
                                buildAnnotatedString {
                                    append("You have no savings")
                                }
                            }
                        }
                    }

                    "DebtRepay" -> {
                        Insights(
                            firstFinancial = debt,
                            secondFinancial = debtRepay,
                            colorResId = R.color.RepayDebt,
                            barColorResId = R.color.Debt
                        ) {
                            if (debtRepay == debt) {
                                buildAnnotatedString {
                                    append("You have repaid your debt")
                                }
                            } else if (debt > 0) {
                                buildAnnotatedString {
                                    append("You have a debt to repay")
                                }
                            } else {
                                buildAnnotatedString {
                                    append("You do not have any debt")
                                }
                            }
                        }
                    }

                    "LentRepay" -> {
                        Insights(
                            firstFinancial = lent,
                            secondFinancial = lentRepay,
                            colorResId = R.color.RepayLoan,
                            barColorResId = R.color.Lent
                        ) {
                            if (lentRepay == lent) {
                                buildAnnotatedString {
                                    append("All your loans has been repaid")
                                }
                            } else if (lent > 0) {
                                buildAnnotatedString {
                                    append("You owes someone")
                                }
                            } else {
                                buildAnnotatedString {
                                    append("You haven't lent to anyone")
                                }
                            }
                        }
                    }

                    "GoalVsScore" -> {
                        Insights(
                            firstFinancial = goal,
                            secondFinancial = score,
                            colorResId = R.color.SetGoal,
                            barColorResId = R.color.Goal
                        ) {

                            buildAnnotatedString {
                                if (score == goal) {
                                    append("You have met your goal")
                                } else if (goal > 0) {
                                    append("You have not met your goal")
                                } else {
                                    append("You have no goal")
                                }
                            }

                        }
                    }

                }
            }
        }
    }
}