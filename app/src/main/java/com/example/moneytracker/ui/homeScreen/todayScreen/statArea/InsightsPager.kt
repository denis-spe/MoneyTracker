// Praise the LORD of God, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.todayScreen.statArea

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.ui.components.charts.InsightBar
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Insights(
    modifier: Modifier = Modifier,
    label: String,
    richTooltipSubheadText: String = "",
    richTooltipActionText: String = "Close",
    firstFinancial: Double,
    secondFinancial: Double,
    colorResId: Int,
    barColorResId: Int,
    showMargin: Boolean = false,
    builder: () -> AnnotatedString,
) {

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var percentage by remember { mutableDoubleStateOf(0.0) }
        var marginRatio by remember { mutableDoubleStateOf(0.0) }
        var text by remember { mutableStateOf(buildAnnotatedString { "" }) }
        val tooltipState = rememberTooltipState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(firstFinancial, secondFinancial) {
            if (firstFinancial == 0.0) {
                percentage = 0.0
                marginRatio = 0.0
            } else {
                percentage = secondFinancial / firstFinancial
                val ratio = (secondFinancial - firstFinancial) / firstFinancial
                marginRatio = if (ratio.isNaN()) 0.0 else ratio
            }
            text = builder()
        }

        val animatePercentage = animateIntAsState(
            targetValue = (percentage.toFloat() * 100).toInt(),
            animationSpec = tween(durationMillis = 700, easing = LinearEasing),
            label = "percentage"
        )
        val animateNetLoss = animateIntAsState(
            targetValue = (marginRatio.toFloat() * 100).toInt(),
            animationSpec = tween(durationMillis = 700, easing = LinearEasing),
            label = "margin"
        )

        when {
            firstFinancial > 0 && firstFinancial > secondFinancial && secondFinancial != 0.0 ->
                colorResource(barColorResId).copy(0.3f)

            firstFinancial == 0.0 && secondFinancial == 0.0 ->
                colorResource(barColorResId).copy(0.3f)

            secondFinancial > firstFinancial ->
                colorResource(colorResId)

            else -> colorResource(barColorResId)
        }


        TooltipBox(
            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                TooltipAnchorPosition.Below
            ),
            tooltip = {
                RichTooltip(
                    title = { Text(richTooltipSubheadText.ifEmpty { label }) },
                    action = {
                        TextButton(onClick = {
                            coroutineScope.launch { tooltipState.dismiss() }
                        }) {
                            Text(richTooltipActionText)
                        }
                    },
                ) {
                    Text(text, textAlign = TextAlign.Start)
                }
            },
            state = tooltipState
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                InsightBar(
                    percentage.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .padding(vertical = 4.dp)
                        .clickable {
                            coroutineScope.launch { tooltipState.show() }
                        },
                    color = colorResource(colorResId),
                    barColor = colorResource(barColorResId).copy(alpha = 0.3f)
                )
            }
        }

        if (showMargin) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${animatePercentage.value}% (${animateNetLoss.value} margin ratio)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(0.6f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0%", fontSize = 11.sp)
                Text(
                    text = "${animatePercentage.value}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("100%", fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun CurrentPagerIndicator(
    pagerState: PagerState,
    items: List<PagerItem>,
    modifier: Modifier = Modifier
) {
    val pagerScope = rememberCoroutineScope()
    val buttonSize = 10.dp
    val currentWidth = animateIntAsState(
        targetValue = 15,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing),
        label = "Current Width"
    )


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        items.forEachIndexed { idx, value ->
            val isSelected = pagerState.currentPage == idx
            IconButton(
                onClick = {
                    pagerScope.launch {
                        pagerState.animateScrollToPage(idx)
                    }
                },
                modifier = Modifier
                    .height(buttonSize)
                    .width(if (isSelected) currentWidth.value.dp else buttonSize)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            colorResource(value.color.second),
                            if (isSelected) RoundedCornerShape(10.dp) else CircleShape
                        )
                        .then(
                            if (!isSelected) {
                                Modifier
                                    .border(
                                        1.dp,
                                        colorResource(value.color.second),
                                        CircleShape
                                    )
                                    .background(
                                        colorResource(value.color.second).copy(0.2f),
                                        CircleShape
                                    )
                            } else Modifier
                        )
                )
            }

            if (idx != items.size - 1) {
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
    }
}

@Composable
fun InsightsPager(
    financeEntityList: List<FinanceEntity>,
    pagerState: PagerState,
    items: List<PagerItem>
) {
    // Single pass calculation for better performance
    val totals = remember(financeEntityList) {
        val map = mutableMapOf<String, Double>()
        val adjMap = mutableMapOf<String, Double>()
        financeEntityList.forEach { finance ->
            val typeText = finance.categoryText
            map[typeText] = (map[typeText] ?: 0.0) + finance.amount

            val settlements = when (finance) {
                is FinanceEntity.Goal -> finance.settlement
                is FinanceEntity.Liability -> finance.settlement
                is FinanceEntity.Transaction -> emptyList()
            }
            adjMap[typeText] = (adjMap[typeText] ?: 0.0) + settlements.sumOf { it.amount }
        }
        object {
            val earnings = map["Earnings"] ?: 0.0
            val expense = map["Expense"] ?: 0.0
            val debt = map["Debt"] ?: 0.0
            val lent = map["Lent"] ?: 0.0
            val savings = map["Savings"] ?: 0.0
            val goal = map["Goal"] ?: 0.0
            val debtRepay = adjMap["Debt"] ?: 0.0
            val lentRepay = adjMap["Lent"] ?: 0.0
            val score = adjMap["Goal"] ?: 0.0
        }
    }


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            key = { it }
        ) { pageIndex ->
            val pagerItem = items[pageIndex]
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (pagerItem.label) {
                    "EarningsVsExpense" -> {
                        Insights(
                            label = "Earnings vs Expense",
                            firstFinancial = totals.earnings,
                            secondFinancial = totals.expense,
                            colorResId = R.color.Expense,
                            barColorResId = R.color.Earnings,
                            builder = {
                                buildAnnotatedString {
                                    when {
                                        totals.expense > totals.earnings -> append("You spent more than your earnings")
                                        totals.earnings > 0 -> append("Earnings are more than your expenses")
                                        else -> append("You have no earnings")
                                    }
                                }
                            }
                        )
                    }

                    "DebtVsEarnings" -> {
                        Insights(
                            label = "Debt vs Earnings",
                            firstFinancial = totals.earnings,
                            secondFinancial = totals.debt,
                            colorResId = R.color.Debt,
                            barColorResId = R.color.Earnings,
                            builder = {
                                buildAnnotatedString {
                                    when {
                                        totals.debt > totals.earnings -> append("You have more debt than your earnings")
                                        totals.earnings > 0 -> append("Earnings are more than your debt")
                                        else -> append("You have no debts to worry about")
                                    }
                                }
                            }
                        )
                    }

                    "GoalVsEarnings" -> {
                        Insights(
                            label = "Goal vs Earnings",
                            firstFinancial = totals.earnings,
                            secondFinancial = totals.goal,
                            colorResId = R.color.Goal,
                            barColorResId = R.color.Earnings,
                            builder = {
                                buildAnnotatedString {
                                    when {
                                        totals.goal >= totals.earnings && totals.goal > 0 -> append(
                                            "You have more goal than your earnings"
                                        )

                                        totals.earnings > 0 -> append("Earnings are more than your goal")
                                        else -> append("You have no goal")
                                    }
                                }
                            },
                        )
                    }

                    "LentVsEarnings" -> {
                        Insights(
                            label = "Lent vs Earnings",
                            firstFinancial = totals.earnings,
                            secondFinancial = totals.lent,
                            colorResId = R.color.Lent,
                            barColorResId = R.color.Earnings,
                            builder = {
                                buildAnnotatedString {
                                    if (totals.lent > totals.earnings) append("You lend more than your earnings")
                                    else append("You have not lent significant amounts")
                                }
                            },
                        )
                    }

                    "SavingsVsEarnings" -> {
                        Insights(
                            label = "Savings vs Earnings",
                            firstFinancial = totals.earnings,
                            secondFinancial = totals.savings,
                            colorResId = R.color.Savings,
                            barColorResId = R.color.Earnings,
                            builder = {
                                buildAnnotatedString {
                                    if (totals.savings > totals.earnings) append("You have more savings than your earnings")
                                    else append("You have no significant savings")
                                }
                            }
                        )
                    }

                    "DebtRepay" -> {
                        Insights(
                            label = "Debt Repayment",
                            firstFinancial = totals.debt,
                            secondFinancial = totals.debtRepay,
                            colorResId = R.color.RepayDebt,
                            barColorResId = R.color.Debt,
                            builder = {
                                buildAnnotatedString {
                                    when {
                                        totals.debtRepay >= totals.debt && totals.debt > 0 -> append(
                                            "You have repaid your debt"
                                        )

                                        totals.debt > 0 -> append("You have a debt to repay")
                                        else -> append("You do not have any debt")
                                    }
                                }
                            }
                        )
                    }

                    "LentRepay" -> {
                        Insights(
                            label = "Lent Repayment",
                            firstFinancial = totals.lent,
                            secondFinancial = totals.lentRepay,
                            colorResId = R.color.RepayLoan,
                            barColorResId = R.color.Lent,
                            builder = {
                                buildAnnotatedString {
                                    when {
                                        totals.lentRepay >= totals.lent && totals.lent > 0 -> append(
                                            "All your loans have been repaid"
                                        )

                                        totals.lent > 0 -> append("You are owed money")
                                        else -> append("You haven't lent to anyone")
                                    }
                                }
                            }
                        )
                    }

                    "GoalVsScore" -> {
                        Insights(
                            label = "Goal Attainment",
                            firstFinancial = totals.goal,
                            secondFinancial = totals.score,
                            colorResId = R.color.Attain,
                            barColorResId = R.color.Goal,
                            builder = {
                                buildAnnotatedString {
                                    when {
                                        totals.score >= totals.goal && totals.goal > 0 -> append("You have met your goal")
                                        totals.goal > 0 -> append("You have not met your goal")
                                        else -> append("You have no goal")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CurrentPagerIndicator(pagerState = pagerState, items = items)
    }
}


@Composable
fun GoalInsightPager(financeEntityList: List<FinanceEntity>) {
    val goals = remember(financeEntityList) {
        financeEntityList.filterIsInstance<FinanceEntity.Goal>()
    }

    if (goals.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Insights(
                label = "No Goal",
                firstFinancial = 0.0,
                secondFinancial = 0.0,
                colorResId = R.color.Attain,
                barColorResId = R.color.Attain,
                builder = {
                    buildAnnotatedString {
                        append("No goal has been set so far")
                    }
                }
            )
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { goals.size })

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            key = { goals[it].id }
        ) { pageIndex ->
            val goal = goals[pageIndex]
            val score = goal.settlement.sumOf { it.amount }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Insights(
                    label = goal.label.ifEmpty { "No goal for today" },
                    firstFinancial = goal.amount,
                    secondFinancial = score,
                    colorResId = R.color.Attain,
                    barColorResId = R.color.Goal,
                    builder = {
                        val remaining = goal.amount - score
                        val now = System.currentTimeMillis()
                        val deadline = goal.routine.deadlineDateTime.toDate().time
                        val diffMillis = deadline - now

                        buildAnnotatedString {
                            when {
                                score >= goal.amount && goal.amount > 0 -> append("Goal Achieved!")
                                goal.amount > 0 -> {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(remaining.formatToAmount())
                                    }
                                    append(" more to go\n")
                                    if (diffMillis > 0) {
                                        val minute = 60 * 1000L
                                        val hour = 60 * minute
                                        val day = 24 * hour
                                        val week = 7 * day
                                        val month = 30 * day
                                        val year = 365 * day

                                        val (unitValue, unitLabel) = when {
                                            diffMillis >= year -> year to "year"
                                            diffMillis >= month -> month to "month"
                                            diffMillis >= week -> week to "week"
                                            diffMillis >= day -> day to "day"
                                            diffMillis >= hour -> hour to "hour"
                                            else -> minute to "minute"
                                        }

                                        val rate = remaining / (diffMillis.toDouble() / unitValue)
                                        if (rate > 0.0) {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(rate.formatToAmount())
                                            }
                                            append(" every $unitLabel to achieve your goal")
                                        }
                                    }
                                }

                                else -> append("Invalid goal amount")
                            }
                        }
                    }
                )
            }
        }

        if (goals.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(goals.size) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) R.color.Goal else R.color.gray
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(colorResource(color))
                            .size(8.dp)
                    )
                }
            }
        }
    }
}