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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.helper.formatResult
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDate
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.formatToTime
import com.example.moneytracker.helper.formatValueOnly
import com.example.moneytracker.helper.iqr
import com.example.moneytracker.helper.kurtosis
import com.example.moneytracker.helper.limitLength
import com.example.moneytracker.helper.mode
import com.example.moneytracker.helper.monthName
import com.example.moneytracker.helper.quartiles
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.helper.skewness
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.std
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.helper.variance
import com.example.moneytracker.helper.weekDay
import com.example.moneytracker.helper.year
import com.example.moneytracker.helper.zScore
import com.example.moneytracker.ui.components.charts.RotatedBarChart
import com.example.moneytracker.ui.components.charts.VicoLineChart
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.components.charts.collections.ChartDataCollection
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.theme.StewardTheme
import io.androidpoet.drafter.bars.renderer.HistogramRenderer
import io.androidpoet.drafter.boxplot.BoxPlotChart
import io.androidpoet.drafter.boxplot.BoxPlotChartRenderer
import io.androidpoet.drafter.boxplot.model.BoxGroup
import io.androidpoet.drafter.boxplot.model.BoxPlotData
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
                                        SettlementItem(
                                            modifier = Modifier.animateItem(),
                                            settlement = settlement,
                                            onClick = { selectedSettlement = settlement }
                                        )
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
                                        AchievementItem(
                                            modifier = Modifier.animateItem(),
                                            achievement = achievement,
                                            onClick = { selectedAchievement = achievement }
                                        )
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
fun GoalMoreCharts(
    currentGoal: FinanceEntity.Goal,
    onDialogShow: MutableState<Boolean>
) {
    if (onDialogShow.value && currentGoal.achievement.isNotEmpty()) {
        val bins = remember { mutableIntStateOf(15) }
        val goalColor = colorResource(currentGoal.colorRes)

        val achievements = remember(currentGoal.achievement) {
            currentGoal.achievement.map { it.totalSettlementAmount }
        }

        val stats = remember(achievements) {
            val (q1, median, q3) = achievements.quartiles()
            val min = achievements.minOrNull() ?: 0.0
            val max = achievements.maxOrNull() ?: 0.0
            val iqr = achievements.iqr()
            object {
                val min = min
                val q1 = q1
                val median = median
                val q3 = q3
                val max = max
                val iqr = iqr
            }
        }

        val histAchievement = remember(achievements, bins.intValue, goalColor) {
            HistogramRenderer(
                dataPoints = achievements.map { it.toFloat() },
                color = goalColor,
                binCount = bins.intValue
            )
        }

        val boxGroups = remember(stats, currentGoal.label) {
            listOf(
                BoxGroup(
                    label = currentGoal.label,
                    min = stats.min.toFloat(),
                    q1 = stats.q1.toFloat(),
                    median = stats.median.toFloat(),
                    q3 = stats.q3.toFloat(),
                    max = stats.max.toFloat(),
                    color = goalColor
                )
            )
        }

        val boxPlotRenderer = remember(boxGroups) {
            BoxPlotChartRenderer(
                BoxPlotData(
                    groups = boxGroups
                ),
            )
        }


        // Show more charts
        Dialog(
            onDismissRequest = { onDialogShow.value = false }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.98f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = { onDialogShow.value = false },
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item(key = "MoreChartsHeader") {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Advanced Distribution",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Analyzing ${currentGoal.label} achievements",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        item(key = "SummaryStats") {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                        alpha = 0.3f
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Summary Statistics",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            StatisticRow(
                                                label = "Min",
                                                value = stats.min.formatToAmount()
                                            )
                                            StatisticRow(
                                                label = "Q1",
                                                value = stats.q1.formatToAmount()
                                            )
                                            StatisticRow(
                                                label = "Median",
                                                value = stats.median.formatToAmount()
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            StatisticRow(
                                                label = "Q3",
                                                value = stats.q3.formatToAmount()
                                            )
                                            StatisticRow(
                                                label = "Max",
                                                value = stats.max.formatToAmount()
                                            )
                                            StatisticRow(
                                                label = "IQR",
                                                value = stats.iqr.formatToAmount()
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item(key = "HistogramChart") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Frequency Distribution (Histogram)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Shows how often different achievement amounts occur. Adjust bins to change granularity.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Bins: ${bins.intValue}",
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.width(60.dp)
                                    )
                                    Slider(
                                        value = bins.intValue.toFloat(),
                                        onValueChange = { bins.intValue = it.toInt() },
                                        valueRange = 5f..50f,
                                        modifier = Modifier.weight(1f),
                                        colors = SliderDefaults.colors().copy(
                                            thumbColor = StewardTheme.colors.primaryAccent,
                                            activeTrackColor = StewardTheme.colors.primaryAccent
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                ) {
                                    RotatedBarChart(
                                        modifier = Modifier.fillMaxSize(),
                                        renderer = histAchievement,
                                        labelRotation = -45f
                                    )
                                }
                            }
                        }

                        item(key = "BoxPlotChart") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Box and Whisker Plot",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Visualizes the spread, quartiles, and range of your performance.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    BoxPlotChart(
                                        renderer = boxPlotRenderer,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GoalLineChartSummary(
    currentGoal: FinanceEntity.Goal
) {
    val onMoreChartDialog = remember { mutableStateOf(false) }

    if (currentGoal.achievement.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
            yValueFormatter = { value -> value.formatValueOnly() },
            markerFormatter = { x, y ->
                val index = x.toInt()
                if (index in sortedAchievements.indices) {
                    val achievement = sortedAchievements[index]
                    when (currentGoal.routine.routine) {
                        Routine.Weekly -> {
                            "${achievement.deadlineDateTime.formatToDate}, ${y.formatToAmount()}"
                        }

                        Routine.Monthly -> {
                            "${achievement.deadlineDateTime.monthName}, ${y.formatToAmount()}"
                        }

                        Routine.Yearly -> {
                            "${achievement.deadlineDateTime.year}, ${y.formatToAmount()}"
                        }

                        Routine.SpecificDayOfTheWeek -> {
                            "${achievement.deadlineDateTime.weekDay}, ${y.formatToAmount()}"
                        }

                        Routine.SpecifyDayOfTheYear -> {
                            "${achievement.deadlineDateTime.formatToDate}, ${y.formatToAmount()}"
                        }

                        Routine.EveryDay -> {
                            "${achievement.deadlineDateTime.formatToDate}, ${y.formatToAmount()}"
                        }

                        Routine.EveryHour -> {
                            "${achievement.deadlineDateTime.formatToTime}, ${y.formatToAmount()}"
                        }

                        Routine.EveryMinute -> {
                            "${achievement.deadlineDateTime.formatToTime}, ${y.formatToAmount()}"
                        }

                        else -> {
                            "${achievement.startDateTime.formatToTime}, ${y.formatToAmount()}"
                        }
                    }
                } else {
                    y.formatToAmount()
                }
            }
        )

        TextButton(
            onClick = { onMoreChartDialog.value = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "More Advanced Chart",
                style = MaterialTheme.typography.labelLarge,
                color = colorResource(currentGoal.colorRes)
            )
        }

        GoalMoreCharts(
            currentGoal = currentGoal,
            onDialogShow = onMoreChartDialog
        )
    }
}

@Composable
fun GoalStatistic(
    currentGoal: FinanceEntity.Goal
) {
    var showInsightDialog by remember { mutableStateOf(false) }
    val achievements = currentGoal.achievement
    if (achievements.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
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
            .height(300.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticRow(label = "Average Achievement", value = average.formatToAmount())
        StatisticRow(label = "Highest Achievement", value = max.formatToAmount())
        StatisticRow(label = "Lowest Achievement", value = min.formatToAmount())
        StatisticRow(label = "Success Rate", value = "${successRate.formatResult}%")
        StatisticRow(label = "Total Cycles", value = totalAchievements.toString())

        TextButton(
            onClick = { showInsightDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "View Advanced Analysis",
                style = MaterialTheme.typography.labelLarge,
                color = colorResource(currentGoal.colorRes)
            )
        }
    }

    if (showInsightDialog) {
        InsightDetailDialog(
            currentGoal = currentGoal,
            onDismiss = { showInsightDialog = false }
        )
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
fun GoalFullDescription(
    currentGoal: FinanceEntity.Goal,
    onShowDialog: MutableState<Boolean>
) {
    if (onShowDialog.value && currentGoal.description.isNotEmpty()) {
        Dialog(
            onDismissRequest = { onShowDialog.value = false }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.98f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = currentGoal.label.title + " Full Description",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentGoal.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun GoalInfo(
    currentGoal: FinanceEntity.Goal,
    countAchievement: CountAchievement?,
    animatedSettled: Animatable<Float, AnimationVector1D>,
    currentAnimatedProgress: Float
) {
    val onDescShow = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (currentGoal.description.isNotEmpty()) {
            TextButton(
                onClick = { onDescShow.value = true },
            ) {
                Text(
                    text = currentGoal.description.limitLength(30),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
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
                value = currentGoal.routine.startDateTime.formatToDateTime,
            )
            InfoRow(
                label = "Deadline",
                value = currentGoal.routine.deadlineDateTime.formatToDateTime,
            )
            InfoRow(
                label = "Status",
                value = currentGoal.status.text,
                color = colorResource(currentGoal.status.color)
            )
            InfoRow(label = "Created At", value = currentGoal.createdAt.formatToDateTime)
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

    GoalFullDescription(currentGoal = currentGoal, onShowDialog = onDescShow)
}

@Composable
fun InfoRow(label: String, value: String, color: Color? = null) {
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
            fontWeight = FontWeight.Medium,
            color = color ?: MaterialTheme.colorScheme.onSurface
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                                2 -> Icons.TwoTone.Insights
                                else -> Icons.Default.Info
                            },
                            contentDescription = "left arrow"
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (pager.currentPage) {
                        0 -> "Overview"
                        1 -> "Performance"
                        2 -> "Analytics"
                        else -> "Overview"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
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
    modifier: Modifier = Modifier,
    achievement: com.example.moneytracker.backend.storage.Achievement,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
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

@Composable
fun InsightDetailDialog(
    currentGoal: FinanceEntity.Goal,
    onDismiss: () -> Unit
) {
    val onShow = remember { mutableStateOf(false) }
    val achievements = currentGoal.achievement
    val amounts = achievements.map { it.totalSettlementAmount }

    val median = if (amounts.isEmpty()) 0.0
    else {
        val sorted = amounts.sorted()
        val mid = sorted.size / 2
        if (sorted.size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2.0 else sorted[mid]
    }

    val range = (amounts.maxOrNull() ?: 0.0) - (amounts.minOrNull() ?: 0.0)

    val mode = amounts.mode()
    val (q1, _, q3) = amounts.quartiles()
    val iqr = amounts.iqr()
    val skewness = amounts.skewness()
    val kurtosis = amounts.kurtosis()
    val latestZScore = amounts.lastOrNull()?.zScore(amounts) ?: 0.0


    val trend = if (achievements.size >= 4) {
        val mid = achievements.size / 2
        val firstHalf = achievements.take(mid).map { it.totalSettlementAmount }.average()
        val secondHalf = achievements.takeLast(mid).map { it.totalSettlementAmount }.average()
        when {
            secondHalf > firstHalf * 1.05 -> "Improving"
            secondHalf < firstHalf * 0.95 -> "Declining"
            else -> "Stable"
        }
    } else "Insufficient Data"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Analysis",
                        tint = StewardTheme.colors.primaryAccent,
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Advanced Analysis",
                        color = StewardTheme.colors.primaryAccent,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "Deep dive into performance",
                        color = Color.Gray, fontSize = 12.sp
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(label = "Median", value = median.formatToAmount())
                    if (mode.size == 1) {
                        DetailRow(
                            label = "Mode",
                            value = mode[0].formatToAmount()
                        )
                    } else {
                        DetailRow(
                            label = "Mode",
                            value = "N/A"
                        )
                    }
                    DetailRow(label = "Range", value = range.formatToAmount())
                    StatisticRow(label = "Variance", value = amounts.variance.formatResult)
                    StatisticRow(label = "Std Deviation", value = amounts.std.formatResult)
                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))

                    Text(
                        "Quartiles & IQR",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    DetailRow(label = "Q1 (25%)", value = q1.formatToAmount())
                    DetailRow(label = "Q3 (75%)", value = q3.formatToAmount())
                    DetailRow(label = "IQR", value = iqr.formatToAmount())

                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
                    Text(
                        "Distribution Shapes",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    DetailRow(label = "Skewness", value = skewness.formatResult)
                    DetailRow(label = "Kurtosis", value = kurtosis.formatResult)

                    HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.2f))
                    DetailRow(label = "Latest Z-Score", value = latestZScore.formatResult)
                    DetailRow(label = "Overall Trend", value = trend)
                }

                IconButton(
                    onClick = {
                        onShow.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = "help"
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

    InsightHelpBottomDrawer(onShow = onShow)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightHelpBottomDrawer(
    onShow: MutableState<Boolean>
) {
    if (onShow.value) {
        ModalBottomSheet(
            onDismissRequest = { onShow.value = false },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Understanding Your Analysis",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StewardTheme.colors.primaryAccent
                )

                Text(
                    text = "These metrics help you understand your long-term performance patterns and consistency.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                HorizontalDivider(thickness = 0.5.dp)

                HelpItem(
                    title = "Median",
                    description = "The true middle value of your achievements. Unlike the average, it isn't skewed by one-off very high or very low results."
                )

                HelpItem(
                    title = "Mode",
                    description = "The specific amount you hit most frequently across all your completed goal cycles."
                )

                HelpItem(
                    title = "Range",
                    description = "The total gap between your all-time best performance and your lowest point. A smaller range means higher stability."
                )

                HelpItem(
                    title = "Variance & Standard Deviation",
                    description = "These measure how much your results jump around cycle-to-cycle. Low values indicate you are very consistent."
                )

                HelpItem(
                    title = "Quartiles (Q1 & Q3)",
                    description = "Q1 (25%) is your 'performance floor'—you hit at least this much 75% of the time. Q3 (75%) is your 'ceiling'—you reach this level in your top 25% of cycles."
                )

                HelpItem(
                    title = "Interquartile Range (IQR)",
                    description = "The spread of your middle 50% of results. It describes your most 'typical' performance range."
                )

                HelpItem(
                    title = "Skewness",
                    description = "Measures if your results lean in a direction. Positive means you often over-perform your average; negative means you often fall short."
                )

                HelpItem(
                    title = "Kurtosis",
                    description = "Measures volatility. High kurtosis means you have occasional extreme jumps (very high or low) rather than steady progress."
                )

                HelpItem(
                    title = "Z-Score",
                    description = "How your most recent result compares to history. 0 is exactly average, +1 is well above average, and -1 is below average."
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HelpItem(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
