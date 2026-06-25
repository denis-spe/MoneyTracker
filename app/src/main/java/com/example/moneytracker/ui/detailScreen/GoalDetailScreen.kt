// Bless be the  names of LORD of hosts and of his JESUS CHRIST
package com.example.moneytracker.ui.detailScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Summarize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.CountAchievement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.helper.formatResult
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.formatValueOnly
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.VicoLineChart
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.components.charts.collections.ChartDataCollection
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.theme.StewardTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: String,
    navController: NavHostController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val detailStates by viewModel.detailState.collectAsStateWithLifecycle()
    val financeEntity = detailStates.financeEntity
    val countAchievement = detailStates.countAchievement
    val attainColor = colorResource(R.color.Attain)
    val goalColor = colorResource(R.color.Goal)

    var selectedAchievement by remember {
        mutableStateOf<com.example.moneytracker.backend.storage.Achievement?>(
            null
        )
    }

    var selectedSettlement by remember {
        mutableStateOf<Settlement?>(null)
    }

    LaunchedEffect(goalId) {
        viewModel.loadGoal(goalId)
        viewModel.loadAchievementCounts(goalId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Goal Details") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.safePopBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = financeEntity,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(
                        animationSpec = tween(
                            500
                        )
                    )
                },
                label = "GoalDetailContent"
            ) { state ->
                when (state) {
                    is DataState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is DataState.Success -> {
                        val currentGoal = state.data as? FinanceEntity.Goal
                        if (currentGoal != null) {
                            val countAchievementData = if (countAchievement is DataState.Success) {
                                countAchievement.data
                            } else {
                                null
                            }

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    GoalSummaryCard(
                                        currentGoal = currentGoal,
                                        countAchievement = countAchievementData
                                    )
                                }

                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        EditGoal(
                                            color = goalColor,
                                            goalId = goalId,
                                            label = "Edit Goal",
                                            detailButtonType = DetailButtonType.OUTLINE
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        AddGoalAttained(
                                            color = attainColor,
                                            goalId = goalId,
                                            label = "Add Attain",
                                            detailButtonType = DetailButtonType.FILLED
                                        )
                                    }
                                }

                                if (currentGoal.settlement.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Attainment History",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    items(
                                        items = currentGoal.settlement,
                                        key = { it.settlementId.ifEmpty { it.dateTime.toString() } }
                                    ) { settlement ->
                                        Box(modifier = Modifier.animateItem()) {
                                            SettlementItem(
                                                settlement = settlement,
                                                onClick = { selectedSettlement = settlement }
                                            )
                                        }
                                    }
                                }

                                if (currentGoal.achievement.isNotEmpty()) {
                                    item {
                                        Text(
                                            text = "Achievement History",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    items(
                                        items = currentGoal.achievement,
                                        key = { it.achievementId.ifEmpty { it.startDateTime.toString() } }
                                    ) { achievement ->
                                        Box(modifier = Modifier.animateItem()) {
                                            AchievementItem(
                                                achievement = achievement,
                                                onClick = { selectedAchievement = achievement }
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Goal not found")
                            }
                        }
                    }

                    is DataState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error: ${state.exception.message}")
                        }
                    }
                }
            }
        }
    }

    selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = { selectedAchievement = null }
        )
    }

    selectedSettlement?.let { settlement ->
        SettlementDetailDialog(
            settlement = settlement,
            financeType = "GOAL",
            onDismiss = { selectedSettlement = null }
        )
    }
}

@Composable
fun GoalLineChartSummary(
    currentGoal: FinanceEntity.Goal
) {
    if (currentGoal.achievement.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No achievement history yet",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        return
    }

    val sortedAchievements = remember(currentGoal.achievement) {
        currentGoal.achievement.sortedBy { it.deadlineDateTime }
    }

    val xValues = remember(sortedAchievements) {
        sortedAchievements.indices.map { it.toDouble() }
    }
    val yValues = remember(sortedAchievements) {
        sortedAchievements.map { it.totalSettlementAmount }
    }

    val chartColor = colorResource(currentGoal.colorRes)
    val chartDataCollection = remember(xValues, yValues, chartColor) {
        ChartDataCollection(
            listOf(
                ChartData(
                    x = xValues,
                    y = yValues,
                    color = chartColor
                )
            )
        )
    }

    VicoLineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(top = 16.dp),
        chartDataCollection = chartDataCollection,
        fillArea = true,
        xValueFormatter = { value ->
            val index = value.toInt()
            if (index in sortedAchievements.indices) {
                sortedAchievements[index].deadlineDateTime.toLocalDateTimeUtc().date.toString()
                    .substring(5)
            } else ""
        },
        yValueFormatter = { value -> value.formatValueOnly() }
    )
}

@Composable
fun GoalStatistic(
    currentGoal: FinanceEntity.Goal
) {
    val achievements = currentGoal.achievement
    if (achievements.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No achievement history yet",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        return
    }

    val totalAchievements = achievements.size
    val completedCount = achievements.count { it.status == "COMPLETED" }
    val successRate = (completedCount.toDouble() / totalAchievements) * 100

    val amounts = achievements.map { it.totalSettlementAmount }
    val average = amounts.average()
    val max = amounts.maxOrNull() ?: 0.0
    val min = amounts.minOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticRow(label = "Average Achievement", value = average.formatToAmount())
        StatisticRow(label = "Highest Achievement", value = max.formatToAmount())
        StatisticRow(label = "Lowest Achievement", value = min.formatToAmount())
        StatisticRow(label = "Success Rate", value = "${successRate.formatResult}%")
        StatisticRow(label = "Total Cycles", value = totalAchievements.toString())

        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))

        Text(
            text = "Data Science Insights",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        val variance = if (amounts.isEmpty()) 0.0
        else {
            val avg = amounts.average()
            amounts.sumOf { (it - avg) * (it - avg) } / amounts.size
        }
        val stdDev = kotlin.math.sqrt(variance)

        StatisticRow(label = "Variance", value = variance.formatResult)
        StatisticRow(label = "Std Deviation", value = stdDev.formatResult)
    }
}

@Composable
fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GoalInfo(
    currentGoal: FinanceEntity.Goal,
    countAchievement: CountAchievement?,
    animatedSettled: Animatable<Float, AnimationVector1D>,
    currentAnimatedProgress: Float
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentGoal.description.isNotEmpty()) {
            Text(
                text = currentGoal.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        countAchievement?.let {
            val verticalDividerModifier = Modifier
                .height(40.dp)
                .padding(horizontal = 10.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        it.achievement.toString(),
                        fontWeight = FontWeight.Bold
                    )
                    Text("Achieved")
                }

                VerticalDivider(modifier = verticalDividerModifier)

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        it.overdue.toString(),
                        fontWeight = FontWeight.Bold
                    )
                    Text("Overdue")
                }

                VerticalDivider(modifier = verticalDividerModifier)

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        it.countAll.toString(),
                        fontWeight = FontWeight.Bold
                    )
                    Text("Overall")
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow(label = "Frequency", value = currentGoal.routine.routine.text)
            InfoRow(
                label = "Start Date",
                value = currentGoal.routine.startDateTime.formatToDateTime
            )
            InfoRow(
                label = "Deadline",
                value = currentGoal.routine.deadlineDateTime.formatToDateTime
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Target: ${currentGoal.amount.formatToAmount()}",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Settled: ${animatedSettled.value.toDouble().formatToAmount()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = colorResource(id = R.color.success_complete)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { currentAnimatedProgress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = colorResource(id = R.color.success_complete),
                trackColor = colorResource(id = R.color.gray).copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(currentAnimatedProgress * 100).toInt()}% of target reached",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun GoalSummaryCard(
    currentGoal: FinanceEntity.Goal,
    countAchievement: CountAchievement?
) {
    val totalSettled = currentGoal.settlement.sumOf { it.amount }
    val targetAmount = currentGoal.amount

    val animatedSettled = remember { Animatable(0f) }
    LaunchedEffect(totalSettled) {
        animatedSettled.animateTo(
            targetValue = totalSettled.toFloat(),
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val currentAnimatedProgress =
        if (targetAmount > 0) (animatedSettled.value / targetAmount).toFloat() else 0f
    val numberOfPages = 3
    val pager = rememberPagerState(pageCount = { numberOfPages })
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Image(
                        painter = painterResource(currentGoal.tagIcon.icon),
                        contentDescription = currentGoal.tagIcon.name,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = currentGoal.label,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                val page = pager.currentPage
                                if (page > 0) {
                                    pager.animateScrollToPage(page - 1)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "left arrow"
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (pager.currentPage) {
                                0 -> Icons.Outlined.Info
                                1 -> Icons.AutoMirrored.Filled.ShowChart
                                2 -> Icons.Rounded.Summarize
                                else -> Icons.Default.Info
                            },
                            contentDescription = "left arrow"
                        )

                        Text(
                            text = when (pager.currentPage) {
                                0 -> "Info"
                                1 -> "Chart"
                                2 -> "Summary"
                                else -> "Info"
                            },
                            fontSize = 10.sp
                        )

                    }

                    IconButton(
                        onClick = {
                            scope.launch {
                                val page = pager.currentPage
                                if (page < numberOfPages) {
                                    pager.animateScrollToPage(page + 1)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "left arrow"
                        )
                    }
                }
            }

            HorizontalPager(
                state = pager,
                modifier = Modifier.fillMaxWidth()
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> {
                        GoalInfo(
                            currentGoal = currentGoal,
                            countAchievement = countAchievement,
                            animatedSettled = animatedSettled,
                            currentAnimatedProgress = currentAnimatedProgress
                        )
                    }

                    1 -> {
                        GoalLineChartSummary(currentGoal = currentGoal)
                    }

                    2 -> {
                        GoalStatistic(currentGoal = currentGoal)
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(
    achievement: com.example.moneytracker.backend.storage.Achievement,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = achievement.status,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.status == "COMPLETED") colorResource(id = R.color.success_complete) else colorResource(
                        id = R.color.error_color
                    )
                )
                Text(
                    text = achievement.totalSettlementAmount.formatToAmount(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Period: ${achievement.startDateTime.formatToDateTime} - ${achievement.deadlineDateTime.formatToDateTime}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AchievementDetailDialog(
    achievement: com.example.moneytracker.backend.storage.Achievement,
    onDismiss: () -> Unit
) {
    val status by remember {
        mutableStateOf(achievement.status)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.achievement),
                        contentDescription = "Achievement",
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Achievement Details",
                        color = if (status == "COMPLETED") colorResource(id = R.color.success_complete) else colorResource(
                            id = R.color.error_color
                        ),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "View achievement records",
                        color = Color.Gray, fontSize = 12.sp
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(
                        label = "Status",
                        value = status,
                        valueColor = if (status == "COMPLETED") colorResource(id = R.color.success_complete) else colorResource(
                            id = R.color.error_color
                        )
                    )
                    DetailRow(
                        label = "Amount",
                        value = achievement.totalSettlementAmount.formatToAmount()
                    )
                    DetailRow(label = "Start", value = achievement.startDateTime.formatToDateTime)
                    DetailRow(
                        label = "Deadline",
                        value = achievement.deadlineDateTime.formatToDateTime
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    EditAchievementAmount(
                        achievement = achievement
                    )

                    DeleteAchievementButton(
                        achievement = achievement,
                        onDismiss = onDismiss
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        "Close",
                        color = StewardTheme.colors.onSurfaceText
                    )
                }
            }
        }
    }
}
