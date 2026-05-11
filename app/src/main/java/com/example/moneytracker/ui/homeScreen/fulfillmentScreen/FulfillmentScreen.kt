// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.fulfillmentScreen

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.types.FinanceCategory
import com.example.moneytracker.backend.storage.types.GoalType
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToTime
import com.example.moneytracker.helper.formattedDate
import com.example.moneytracker.helper.formattedTime
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.screenManager.FulfillmentDetailScreenRouter

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
                Text(
                    amount,
                    style = typography.titleSmall
                )
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
                            val percentage = if (financeEntityGoal.amount > 0)
                                ((progressAmount / financeEntityGoal.amount) * 100).toInt()
                            else 0
                            Text(
                                text = "$percentage%",
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
                    val percentage = if (financeEntity.amount > 0)
                        ((progressAmount / financeEntity.amount) * 100).toInt()
                    else 0
                    Text(
                        text = "$percentage%",
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
fun FulfillmentScreen(
    onNavigate: NavController?,
    paddingValues: PaddingValues,
    allDataset: DataState<List<FinanceEntity>>,
    uiState: HomeUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(10)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { Spacer(modifier = Modifier.size(10.dp)) }

            when (allDataset) {
                is DataState.Loading -> {
                    items(5) {
                        GoalCardShimmer(modifier = Modifier.padding(10.dp))
                    }
                }

                is DataState.Success -> {
                    allDataset.data.groupBy {
                        val type = it.financeType
                        if (type is TransactionType) "Transactions" else type
                    }.forEach { finance ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val headerText = when (val key = finance.key) {
                                    is String -> key
                                    is FinanceCategory -> {
                                        when (key) {
                                            GoalType -> "${key.text} Fulfillment"
                                            is LiabilityType -> "${key.text} Settlements"
                                            else -> key.text
                                        }
                                    }

                                    else -> key.toString()
                                }

                                Text(
                                    text = headerText,
                                    style = typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        item {
                            val goals = finance.value.filterIsInstance<FinanceEntity.Goal>()
                                .sortedBy {
                                    it.routine.deadlineDateTime
                                }

                            Spacer(modifier = Modifier.width(10.dp))
                            goals.forEach { goal ->
                                GoalCard(
                                    financeEntityGoal = goal,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10))
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
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        item {
                            val liabilities =
                                finance.value.filterIsInstance<FinanceEntity.Liability>()
                                    .sortedBy {
                                        it.createdAt
                                    }

                            LazyRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    Spacer(modifier = Modifier.width(10.dp))
                                    liabilities.forEach { liability ->
                                        SettlementCard(
                                            financeEntity = liability,
                                            modifier = Modifier
                                                .padding(
                                                    top = 10.dp,
                                                    bottom = 10.dp,
                                                    end = 8.dp
                                                )
                                                .clip(RoundedCornerShape(10))
                                                .background(
                                                    colorResource(id = liability.colorRes)
                                                        .copy(alpha = 0.1f)
                                                )
                                                .clickable {
                                                    onNavigate?.navigate(
                                                        FulfillmentDetailScreenRouter(goalId = liability.id),
                                                    )
                                                }
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                }
                            }
                        }

                        item {
                            val transactions =
                                finance.value.filterIsInstance<FinanceEntity.Transaction>()
                                    .sortedByDescending {
                                        it.createdAt
                                    }
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Spacer(modifier = Modifier.width(10.dp))
                                LazyRow(
                                    modifier = Modifier
                                        .padding(
                                            top = 10.dp,
                                            bottom = 10.dp,
                                            end = 8.dp
                                        )
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
                                        transactions.forEach { transaction ->
                                            TransactionCard(
                                                modifier = Modifier
                                                    .padding(end = 5.dp)
                                                    .animateItem(),
                                                financeEntity = transaction
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                }

                is DataState.Error -> {
                }
            }
        }
    }
}

