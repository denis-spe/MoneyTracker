// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.overviewScreen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToTime
import com.example.moneytracker.helper.formattedDate
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.screenManager.FulfillmentDetailScreenRouter
import com.example.moneytracker.ui.screenManager.LiabilityDetailScreenRouter
import com.example.moneytracker.ui.screenManager.ShowAllGoalsScreenRouter
import com.example.moneytracker.ui.screenManager.ShowAllLiabilitiesScreenRouter
import com.example.moneytracker.ui.screenManager.ShowAllTransactionsScreenRouter
import com.example.moneytracker.ui.screenManager.TransactionDetailScreenRouter
import com.example.moneytracker.ui.theme.StewardTheme

private val CORNER_RADIUS = 16.dp

@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    onSeeAllClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        if (onSeeAllClick != null) {
            TextButton(
                onClick = onSeeAllClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "See All",
                    style = typography.labelLarge,
                    color = StewardTheme.colors.primary
                )
            }
        }
    }
}

@Composable
internal fun TransactionCard(
    modifier: Modifier = Modifier,
    financeEntity: FinanceEntity.Transaction,
    onNavigate: NavController?
) {
    val amount = financeEntity.amount.formatToAmount()
    val label = financeEntity.label
    val tagIcon = painterResource(financeEntity.tagIcon.icon)
    val transactionType = financeEntity.transactionType.text
    val color = colorResource(financeEntity.colorRes)
    val paymentMethod = financeEntity.paymentMethod


    val surfaceColor = MaterialTheme.colorScheme.surface

    val blendedColor = remember(color, surfaceColor) {
        color.copy(alpha = 0.1f).compositeOver(surfaceColor)
    }

    Surface(
        modifier = Modifier
            .width(170.dp)
            .then(modifier),
        color = blendedColor,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(CORNER_RADIUS)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNavigate?.navigate(
                        TransactionDetailScreenRouter(financeEntity.id)
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        label,
                        style = typography.titleSmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = when (financeEntity.transactionType) {
                            TransactionType.EARNINGS -> Icons.Default.ArrowUpward
                            TransactionType.EXPENSES -> Icons.Default.ArrowDownward
                            TransactionType.SAVINGS -> Icons.Default.ArrowUpward
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(color.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(5.dp)
                            )
                        }

                        Text(
                            transactionType,
                            style = typography.labelSmall,
                            color = color
                        )
                    }
                    Text(
                        amount,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawalCard(
    modifier: Modifier = Modifier,
    withdrawal: Withdrawal,
    onNavigate: NavController? = null
) {
    val amount = "-${withdrawal.amount.formatToAmount()}"
    val label = withdrawal.financeEntity?.label ?: "Withdrawal"
    val tagIcon = painterResource(withdrawal.financeEntity?.tagIcon?.icon ?: R.drawable.initial)
    val color = colorResource(R.color.Lent)
    val toPaymentMethod = withdrawal.toPaymentMethod

    val surfaceColor = MaterialTheme.colorScheme.surface

    val blendedColor = remember(color, surfaceColor) {
        color.copy(alpha = 0.1f).compositeOver(surfaceColor)
    }

    Surface(
        modifier = Modifier
            .width(170.dp)
            .then(modifier),
        color = blendedColor,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(CORNER_RADIUS)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    withdrawal.financeEntity?.let {
                        onNavigate?.navigate(
                            TransactionDetailScreenRouter(it.id)
                        )
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        label,
                        style = typography.titleSmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = Icons.Default.ArrowDownward
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(color.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(5.dp)
                            )
                        }

                        Text(
                            "Withdrawal",
                            style = typography.labelSmall,
                            color = color
                        )
                    }
                    Text(
                        amount,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
internal fun GoalCard(
    modifier: Modifier = Modifier,
    financeEntityGoal: FinanceEntity.Goal,
    onNavigate: NavController?
) {
    val goalStatus = financeEntityGoal.status
    val statusText = goalStatus.name.title

    val progressAmount = financeEntityGoal.settlement.sumOf { it.amount }
    val remainingAmount = (financeEntityGoal.amount - progressAmount).coerceAtLeast(0.0)
    val goalColor = colorResource(financeEntityGoal.colorRes)

    val finalDonutChartDataCollection = remember(progressAmount, remainingAmount, goalColor) {
        DonutChartDataCollection(
            listOf(
                DonutChartData(
                    amount = progressAmount.toFloat(),
                    color = goalColor,
                    title = "Progress"
                ),
                DonutChartData(
                    amount = remainingAmount.toFloat(),
                    color = Color.LightGray.copy(alpha = 0.3f),
                    title = "Remaining"
                )
            )
        )
    }

    val dateTimeText = remember(financeEntityGoal) {
        getGoalStatusText(financeEntityGoal)
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
    val day = String.format(LocalLocale.current.platformLocale, "%02d", createdAt.day)
    val createdAtDateTime = "Created at $day ${createdAt.month.name.title} " +
            "${createdAt.year} ${createdAt.hour formatToTime createdAt.minute}"

    val surfaceColor = MaterialTheme.colorScheme.surface
    val statusColor = colorResource(financeEntityGoal.status.color)
    val blendedColor = remember(statusColor, surfaceColor) {
        statusColor.copy(alpha = 0.07f).compositeOver(surfaceColor)
    }

    Surface(
        modifier = Modifier
            .then(modifier),
        color = blendedColor,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(CORNER_RADIUS)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNavigate?.navigate(
                        FulfillmentDetailScreenRouter(financeEntityGoal.id)
                    )
                }
        ) {
            ListItem(
                colors = ListItemDefaults.colors().copy(
                    containerColor = blendedColor
                ),
                headlineContent = {
                    Text(
                        text = financeEntityGoal.label,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                supportingContent = {
                    Column {
                        Text(
                            text = financeEntityGoal.amount.formatToAmount(),
                            style = typography.titleLarge,
                            color = goalColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = createdAtDateTime,
                            style = typography.bodySmall,
                            color = Color.Gray
                        )
                        dateTimeText?.let {
                            Text(
                                text = it,
                                style = typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                },
                leadingContent = {
                    DonutChart(
                        data = finalDonutChartDataCollection,
                        modifier = Modifier.size(60.dp),
                        chartSize = 50.dp,
                        strokeWidth = 6.dp,
                        strokeWidthSelected = 8.dp,
                        gapPercentage = 0.06f,
                        strokeCap = StrokeCap.Round,
                        selectionView = {
                            Text(
                                text = "$animatedPercentage%",
                                style = typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    )
                },
                trailingContent = {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = statusText,
                            style = typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(goalStatus.color)
                        )
                        Text(
                            text = routineName,
                            style = typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                modifier = modifier.fillMaxWidth(),
            )
        }
    }
}

fun getGoalStatusText(goal: FinanceEntity.Goal): String? {
    val endDateTime = goal.routine.deadlineDateTime.toLocalDateTimeUtc()
    val endDate = "${endDateTime.day}/${endDateTime.month.name.title}/${endDateTime.year}"
    val goalStatus = goal.status

    val endState = when (goalStatus) {
        Status.COMPLETED, Status.SUCCESS -> "Completed "
        Status.OVERDUE -> "Expired "
        Status.ACTIVE -> "Will end "
        else -> ""
    }

    return when (goal.routine.routine) {
        Routine.EveryMinute -> "${endState}at ${endDateTime.hour formatToTime endDateTime.minute}"
        Routine.EveryHour -> "$endState at ${endDateTime.hour formatToTime endDateTime.minute}"
        Routine.EveryDay, Routine.Weekly -> "$endState at $endDate"
        Routine.Monthly -> "$endState at ${endDateTime.month.name.title}"
        Routine.Yearly -> "$endState at ${endDateTime.year}"
        Routine.SpecifyDayOfTheYear -> "$endState at ${endDateTime.dayOfWeek.name.title}"
        else -> null
    }
}

@Composable
internal fun LiabilityCard(
    modifier: Modifier = Modifier,
    financeEntity: FinanceEntity.Liability,
    onNavigate: NavController?
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

    val donutChartDataCollection = remember(progressAmount, remainingAmount, color) {
        DonutChartDataCollection(
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
    }
    val surfaceColor = MaterialTheme.colorScheme.surface
    val blendedColor = remember(color, surfaceColor) {
        color.copy(alpha = 0.07f).compositeOver(surfaceColor)
    }

    Surface(
        modifier = Modifier
            .width(130.dp)
            .then(modifier),
        color = blendedColor,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(CORNER_RADIUS)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onNavigate?.navigate(
                        LiabilityDetailScreenRouter(financeEntity.id)
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DonutChart(
                    data = donutChartDataCollection,
                    modifier = Modifier.size(60.dp),
                    chartSize = 50.dp,
                    strokeWidth = 6.dp,
                    strokeWidthSelected = 8.dp,
                    gapPercentage = 0.06f,
                    strokeCap = StrokeCap.Round,
                    selectionView = {
                        Text(
                            text = "$animatedPercentage%",
                            style = typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                )

                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        label,
                        style = typography.titleSmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        createdAt.formattedDate,
                        style = typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        amount,
                        style = typography.titleMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SortComponent(
    modifier: Modifier = Modifier,
    isAscending: Boolean,
    transactionSort: TransactionSort,
    onSortClick: () -> Unit
) {
    val sortLabel = remember(isAscending, transactionSort) {
        transactionSort.getSortLabel(isAscending)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Activity",
            style = typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )
        )

        Surface(
            onClick = onSortClick,
            color = if (isAscending) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.AutoMirrored.Filled.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isAscending) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = sortLabel,
                    style = typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isAscending) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun <T> SortChips(
    modifier: Modifier = Modifier,
    options: Array<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    labelProvider: (T) -> String
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(options) { option ->
            FilterChip(
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) },
                label = { Text(labelProvider(option)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun OverviewScreen(
    onNavigate: NavController?,
    paddingValues: PaddingValues,
    allDataset: DataState<List<FinanceEntity>>,
    recentActivity: DataState<List<DataSettlement>>,
    sortedGoals: DataState<List<FinanceEntity.Goal>>,
    sortedLiabilities: DataState<List<FinanceEntity.Liability>>,
    isAscending: Boolean,
    transactionSort: TransactionSort,
    goalSort: GoalSort,
    liabilitySort: LiabilitySort,
    onToggleSort: () -> Unit,
    onTransactionSortChange: (TransactionSort) -> Unit,
    onGoalSortChange: (GoalSort) -> Unit,
    onLiabilitySortChange: (LiabilitySort) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {

        SortComponent(
            modifier = Modifier.padding(horizontal = 16.dp),
            isAscending = isAscending,
            transactionSort = transactionSort,
            onSortClick = onToggleSort
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
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
                        // ... existing empty state ...
                    } else {
                        // Recent Activity (Transactions & Withdrawals)
                        if (recentActivity is DataState.Success && recentActivity.data.isNotEmpty()) {
                            item {
                                Column {
                                    SectionHeader(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        title = "Recent Transactions",
                                        onSeeAllClick = {
                                            onNavigate?.navigate(ShowAllTransactionsScreenRouter)
                                        }
                                    )
                                    SortChips(
                                        options = TransactionSort.entries.toTypedArray(),
                                        selectedOption = transactionSort,
                                        onOptionSelected = onTransactionSortChange,
                                        labelProvider = { it.label }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        items(recentActivity.data, key = { it.id }) { activity ->
                                            when (activity) {
                                                is DataSettlement.SettlementData -> {
                                                    TransactionCard(
                                                        modifier = Modifier.animateItem(),
                                                        financeEntity = activity.financeEntity as FinanceEntity.Transaction,
                                                        onNavigate = onNavigate
                                                    )
                                                }

                                                is DataSettlement.SettlementWithdrawal -> {
                                                    WithdrawalCard(
                                                        modifier = Modifier.animateItem(),
                                                        withdrawal = activity.withdrawal,
                                                        onNavigate = onNavigate
                                                    )
                                                }

                                                else -> {}
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Goals
                        if (sortedGoals is DataState.Success && sortedGoals.data.isNotEmpty()) {
                            item {

                                Column {
                                    SectionHeader(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        title = "Active Goals",
                                        onSeeAllClick = {
                                            onNavigate?.navigate(ShowAllGoalsScreenRouter)
                                        }
                                    )
                                    SortChips(
                                        options = GoalSort.entries.toTypedArray(),
                                        selectedOption = goalSort,
                                        onOptionSelected = onGoalSortChange,
                                        labelProvider = { it.label }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Column(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        sortedGoals.data.take(3).forEach { goal ->
                                            GoalCard(
                                                modifier = Modifier.animateItem(),
                                                financeEntityGoal = goal,
                                                onNavigate = onNavigate
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Liabilities
                        if (sortedLiabilities is DataState.Success && sortedLiabilities.data.isNotEmpty()) {
                            item {
                                Column {
                                    SectionHeader(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        title = "Liabilities",
                                        onSeeAllClick = {
                                            onNavigate?.navigate(ShowAllLiabilitiesScreenRouter)
                                        }
                                    )
                                    SortChips(
                                        options = LiabilitySort.entries.toTypedArray(),
                                        selectedOption = liabilitySort,
                                        onOptionSelected = onLiabilitySortChange,
                                        labelProvider = { it.label }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 16.dp)
                                    ) {
                                        items(
                                            sortedLiabilities.data.take(5),
                                            key = { it.id }) { liability ->
                                            LiabilityCard(
                                                modifier = Modifier.animateItem(),
                                                financeEntity = liability,
                                                onNavigate = onNavigate
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // ... Error case ...

                is DataState.Error -> {
                    item {
                        Text(
                            text = "Something went wrong. Please try again.",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
