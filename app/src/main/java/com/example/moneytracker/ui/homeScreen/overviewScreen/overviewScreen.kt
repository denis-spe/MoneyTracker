// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.overviewScreen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.types.FinanceCategory
import com.example.moneytracker.backend.storage.types.GoalType
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToTime
import com.example.moneytracker.helper.formattedDate
import com.example.moneytracker.helper.formattedTime
import com.example.moneytracker.helper.limit
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.screenManager.FulfillmentDetailScreenRouter
import com.example.moneytracker.ui.screenManager.LiabilityDetailScreenRouter
import com.example.moneytracker.ui.screenManager.TransactionDetailScreenRouter
import com.example.moneytracker.ui.usecase.coupleDatasetsWithSettlements

private val SPACE = 10.dp

@Composable
fun TransactionCard(
    modifier: Modifier = Modifier,
    financeEntity: FinanceEntity.Transaction
) {
    val amount = financeEntity.amount.formatToAmount()
    val label = financeEntity.label
    val tagIcon = painterResource(financeEntity.tagIcon.icon)
    val transactionType = financeEntity.transactionType.text
    val color = colorResource(financeEntity.colorRes)
    val paymentMethod = financeEntity.paymentMethod

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .then(modifier)
    ) {
        Row(
            modifier = Modifier
                .width(165.dp)
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Image(
                    modifier = Modifier.size(ICON_SIZE),
                    painter = tagIcon,
                    contentDescription = label,
                )
                Image(
                    painter = painterResource(paymentMethod.icon),
                    contentDescription = paymentMethod.text,
                    modifier = Modifier.size(ICON_SIZE)
                )
            }

            Column {
                Text(
                    label,
                    style = typography.titleSmall
                )
                Text(
                    transactionType,
                    style = typography.titleSmall,
                    color = color
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        amount,
                        style = typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawalCard(
    modifier: Modifier = Modifier,
    withdrawal: Withdrawal
) {
    val amount = "-${withdrawal.amount.formatToAmount()}"
    val label = withdrawal.financeEntity!!.label
    val tagIcon = painterResource(withdrawal.financeEntity?.tagIcon?.icon ?: R.drawable.initial)
    val color = colorResource(R.color.Lent)
    val toPaymentMethod = withdrawal.toPaymentMethod

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(50))
    ) {
        Row(
            modifier = Modifier
                .width(165.dp)
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Image(
                    modifier = Modifier.size(ICON_SIZE),
                    painter = tagIcon,
                    contentDescription = label,
                )
                Image(
                    painter = painterResource(toPaymentMethod.icon),
                    contentDescription = toPaymentMethod.text,
                    modifier = Modifier.size(ICON_SIZE)
                )
            }

            Column {
                Text(
                    label,
                    style = typography.titleSmall
                )
                Text(
                    "Withdrawal",
                    style = typography.titleSmall,
                    color = color
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        amount,
                        style = typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
fun GoalCard(
    modifier: Modifier = Modifier,
    financeEntityGoal: FinanceEntity.Goal
) {
    financeEntityGoal.routine.startDateTime.toLocalDateTimeUtc()
    val endDateTime = financeEntityGoal.routine.deadlineDateTime.toLocalDateTimeUtc()
    val endDate = "${endDateTime.day}/${endDateTime.month.name.title}/${endDateTime.year}"
    val status = financeEntityGoal.status.name.title

    val progressAmount = financeEntityGoal.settlement.sumOf { it.amount }
    val remainingAmount = (financeEntityGoal.amount - progressAmount).coerceAtLeast(0.0)

    val donutChartDataCollection = DonutChartDataCollection(
        listOf(
            DonutChartData(
                amount = progressAmount.toFloat(),
                color = colorResource(financeEntityGoal.colorRes),
                title = "Progress"
            ),

            DonutChartData(
                amount = remainingAmount.toFloat(),
                color = Color.LightGray.copy(alpha = 0.3f),
                title = "Remaining"
            )
        )
    )

    val endState = when (financeEntityGoal.status) {
        Status.SUCCESS -> {
            "Completed "
        }

        Status.OVERDUE -> {
            "Expired "
        }

        Status.ACTIVE -> {
            "Will end "
        }

        else -> {
            ""
        }
    }

    val dateTime = when (financeEntityGoal.routine.routine) {
        Routine.EveryMinute -> {
            "${endState}end at ${endDateTime.hour formatToTime endDateTime.minute}"
        }

        Routine.EveryHour -> {
            "$endState at ${endDateTime.hour formatToTime endDateTime.minute}"
        }

        Routine.EveryDay -> {
            "$endState at $endDate"
        }

        Routine.Weekly -> {
            "$endState at $endDate"
        }

        Routine.Monthly -> {
            "$endState at ${endDateTime.month}"
        }

        Routine.Yearly -> {
            "$endState at ${endDateTime.year}"
        }

        Routine.SpecifyDayOfTheWeek -> {
            "$endState at ${endDateTime.dayOfWeek}"
        }

        else -> {
            null
        }
    }

    val routineName = if (financeEntityGoal.routine.routine == Routine.Nothing) {
        "Not repeatable"
    } else {
        financeEntityGoal.routine.routine.text
    }

    val targetPercentage = if (financeEntityGoal.amount > 0)
        ((progressAmount / financeEntityGoal.amount) * 100).toInt()
    else 0

    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(targetPercentage) {
        enabled = true
    }
    val animatedPercentage by animateIntAsState(
        targetValue = if (enabled) targetPercentage else 0,
        label = "GoalPercentageAnimation",
        animationSpec = tween(1000)
    )

    val createdAt = financeEntityGoal.createdAt.toLocalDateTimeUtc()
    val day = String.format(java.util.Locale.getDefault(), "%02d", createdAt.day)
    val createdAtDateTime = "Created at $day ${createdAt.month.name.title} " +
            "${createdAt.year} ${createdAt.hour formatToTime createdAt.minute}"

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ListItem(
            headlineContent = {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = financeEntityGoal.label,
                        style = typography.titleMedium
                    )
                }
            },
            overlineContent = {

            },
            supportingContent = {
                Text(
                    text = financeEntityGoal.amount.formatToAmount(),
                    style = typography.titleLarge
                )
            },
            leadingContent = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DonutChart(
                        data = donutChartDataCollection,
                        modifier = Modifier.size(40.dp),
                        chartSize = 40.dp,
                        strokeWidth = 4.dp,
                        strokeWidthSelected = 6.dp,
                        gapPercentage = 0.06f,
                        strokeCap = StrokeCap.Round,
                        selectionView = {
                            Text(
                                text = "$animatedPercentage%",
                                style = typography.labelSmall.copy(fontSize = 8.sp)
                            )
                        }
                    )
                }
            },
            trailingContent = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = status,
                        style = typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(financeEntityGoal.status.color)
                    )
                    Text(
                        text = routineName,
                        style = typography.bodySmall
                    )
                }
            },
            modifier = modifier.fillMaxWidth(),
            colors = ListItemDefaults.colors(),
            tonalElevation = ListItemDefaults.Elevation,
            shadowElevation = ListItemDefaults.Elevation
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = createdAtDateTime,
                style = typography.bodySmall
            )
            dateTime?.let {
                Text(
                    text = it,
                    style = typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun SettlementCard(
    modifier: Modifier,
    financeEntity: FinanceEntity.Liability
) {
    val createdAt = financeEntity.createdAt.toLocalDateTimeUtc()
    val amount = financeEntity.amount.formatToAmount()
    val label = financeEntity.label

    val progressAmount = financeEntity.settlement.sumOf { it.amount }
    val remainingAmount = (financeEntity.amount - progressAmount).coerceAtLeast(0.0)
    val color = colorResource(financeEntity.colorRes)

    val targetPercentage = if (financeEntity.amount > 0)
        ((progressAmount / financeEntity.amount) * 100).toInt()
    else 0

    var enabled by remember { mutableStateOf(false) }
    LaunchedEffect(targetPercentage) {
        enabled = true
    }
    val animatedPercentage by animateIntAsState(
        targetValue = if (enabled) targetPercentage else 0,
        label = "SettlementPercentageAnimation",
        animationSpec = tween(1000)
    )

    val donutChartDataCollection = DonutChartDataCollection(
        listOf(
            DonutChartData(
                amount = progressAmount.toFloat(),
                color = color,
                title = "Progress"
            ),

            DonutChartData(
                amount = remainingAmount.toFloat(),
                color = Color.LightGray.copy(alpha = 0.3f),
                title = "Remaining"
            )
        )
    )

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors().copy(
            containerColor = color.copy(0.08f)
        ),
    ) {
        Column(
            modifier = Modifier
                .width(130.dp)
                .padding(5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DonutChart(
                data = donutChartDataCollection,
                modifier = Modifier.size(50.dp),
                chartSize = 40.dp,
                strokeWidth = 4.dp,
                strokeWidthSelected = 6.dp,
                gapPercentage = 0.06f,
                strokeCap = StrokeCap.Round,
                selectionView = {
                    Text(
                        text = "$animatedPercentage%",
                        style = typography.labelSmall.copy(fontSize = 8.sp)
                    )
                }
            )

            Column {
                Text(
                    label,
                    style = typography.titleMedium
                )
                Text(
                    createdAt.formattedDate,
                    style = typography.titleSmall
                )
                Text(
                    createdAt.formattedTime,
                    style = typography.titleSmall
                )
                Text(
                    amount,
                    style = typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun OverviewScreen(
    onNavigate: NavController?,
    paddingValues: PaddingValues,
    allDataset: DataState<List<FinanceEntity>>,
    uiState: HomeUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(15.dp)
                .clip(RoundedCornerShape(6)),
        ) {
            when (allDataset) {
                is DataState.Loading -> {
                    item {
                        OverviewShimmer()
                    }
                }

                is DataState.Success -> {
                    val data = allDataset.data
                    if (data.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillParentMaxHeight()
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.empty_list),
                                    contentDescription = "empty list",
                                    modifier = Modifier.size(60.dp)
                                )
                                Text(
                                    buildString {
                                        append("No activity recorded\n")
                                        append("yet")
                                    },
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        item { Spacer(modifier = Modifier.height(SPACE)) }
                        val coupledData = coupleDatasetsWithSettlements(data)

                        coupledData.groupBy {
                            val type = when (it) {
                                is DataSettlement.SettlementData -> it.financeEntity.financeType
                                is DataSettlement.SettlementAdjust -> it.settlement.financeEntity?.financeType
                                    ?: TransactionType.EARNINGS

                                is DataSettlement.SettlementWithdrawal -> it.withdrawal.financeEntity?.financeType
                                    ?: TransactionType.EARNINGS
                            }
                            if (type is TransactionType || it is DataSettlement.SettlementWithdrawal) "Transactions" else type
                        }.forEach { (key, list) ->
                            // Title part
                            item {
                                Spacer(modifier = Modifier.height(SPACE))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val headerText = when (val k = key) {
                                        is String -> k
                                        is FinanceCategory -> {
                                            when (k) {
                                                GoalType -> "${k.text} Fulfillment"
                                                is LiabilityType -> "${k.text} Settlements"
                                                else -> k.text
                                            }
                                        }

                                        else -> k.toString()
                                    }

                                    Text(
                                        text = headerText,
                                        style = typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(SPACE))
                            }

                            // Goal part
                            item {
                                val goals = list
                                    .filterIsInstance<DataSettlement.SettlementData>()
                                    .map { it.financeEntity }
                                    .filterIsInstance<FinanceEntity.Goal>()
                                    .sortedBy {
                                        it.routine.deadlineDateTime
                                    }
                                    .limit(3)
                                goals.forEachIndexed { index, goal ->
                                    GoalCard(
                                        financeEntityGoal = goal,
                                        modifier = Modifier
                                            .background(
                                                colorResource(id = goal.colorRes)
                                                    .copy(alpha = 0.1f)
                                            )
                                            .clickable {
                                                onNavigate?.navigate(
                                                    FulfillmentDetailScreenRouter(goalId = goal.id),
                                                )
                                            }
                                    )
                                    if (index < goals.size && goals.isNotEmpty()) Spacer(
                                        modifier = Modifier.height(
                                            SPACE
                                        )
                                    )
                                }
                            }

                            // Liabilities part
                            item {
                                val liabilities = list
                                    .filterIsInstance<DataSettlement.SettlementData>()
                                    .map { it.financeEntity }
                                    .filterIsInstance<FinanceEntity.Liability>()
                                    .sortedBy {
                                        it.createdAt
                                    }
                                    .limit(10)

                                if (liabilities.isNotEmpty()) {
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        item {
                                            liabilities.forEach { liability ->
                                                SettlementCard(
                                                    financeEntity = liability,
                                                    modifier = Modifier
                                                        .padding(end = SPACE)
                                                        .clip(RoundedCornerShape(10))
                                                        .background(
                                                            colorResource(id = liability.colorRes)
                                                                .copy(alpha = 0.1f)
                                                        )
                                                        .clickable {
                                                            onNavigate?.navigate(
                                                                LiabilityDetailScreenRouter(
                                                                    liabilityId = liability.id
                                                                ),
                                                            )
                                                        }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Transactions part (now includes withdrawals and adjustments)
                            item {
                                val sortedList = list
                                    .sortedByDescending { it.createdAt }
                                    .limit(15)

                                if (sortedList.isNotEmpty()) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        LazyRow(
                                            modifier = Modifier
                                                .clip(
                                                    RoundedCornerShape(
                                                        topStart = 50.dp,
                                                        topEnd = 50.dp,
                                                        bottomStart = 50.dp,
                                                        bottomEnd = 50.dp
                                                    )
                                                )
                                        ) {
                                            item {
                                                sortedList.forEachIndexed { index, item ->
                                                    when (item) {
                                                        is DataSettlement.SettlementData -> {
                                                            if (item.financeEntity is FinanceEntity.Transaction) {
                                                                TransactionCard(
                                                                    modifier = Modifier
                                                                        .animateItem()
                                                                        .clickable {
                                                                            onNavigate?.navigate(
                                                                                TransactionDetailScreenRouter(
                                                                                    transactionId = item.financeEntity.id
                                                                                )
                                                                            )
                                                                        },
                                                                    financeEntity = item.financeEntity
                                                                )

                                                                if (index < sortedList.size - 1)
                                                                    Spacer(
                                                                        modifier = Modifier.width(
                                                                            SPACE
                                                                        )
                                                                    )
                                                            }
                                                        }

                                                        is DataSettlement.SettlementWithdrawal -> {
//                                                            WithdrawalCard(
//                                                                modifier = Modifier
//                                                                    .padding(end = SPACE)
//                                                                    .animateItem()
//                                                                    .clickable {
//                                                                        onNavigate?.navigate(
//                                                                            TransactionDetailScreenRouter(
//                                                                                transactionId = item.withdrawal.withdrawalId
//                                                                            )
//                                                                        )
//                                                                    },
//                                                                withdrawal = item.withdrawal
//                                                            )
                                                        }

                                                        is DataSettlement.SettlementAdjust -> {
                                                            // Optional: show adjustment cards too
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item { Spacer(modifier = Modifier.height(SPACE)) }
                    }
                }

                is DataState.Error -> {
                }
            }

            item { Spacer(modifier = Modifier.height(SPACE)) }
        }
    }
}

