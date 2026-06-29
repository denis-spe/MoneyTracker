// Glory be the name of LORD our GOD
package com.example.moneytracker.ui.showAll

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToTime
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.overviewScreen.getGoalStatusText
import com.example.moneytracker.ui.screenManager.FulfillmentDetailScreenRouter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAllGoalScreen(
    viewModel: ShowAllViewModel,
    navController: NavHostController
) {
    // Load all goals once
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadAllGoal()
    }

    // Collect the state from the ViewModel
    val showAllStates = viewModel.showAllDataset.collectAsStateWithLifecycle()

    // Extract the goals from the state
    val goals = showAllStates.value.goal

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Active Goals") },
                navigationIcon = {
                    IconButton(onClick = { navController.safePopBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (goals) {
            is DataState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is DataState.Success -> {
                val data = goals.data

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
                ) {
                    items(data.size, key = { data[it].id }) { index ->
                        ShowAllGoalCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            financeEntityGoal = data[index],
                            onNavigate = navController
                        )
                    }
                }
            }

            is DataState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${goals.exception.message}")
                }
            }
        }
    }
}


@Composable
internal fun ShowAllGoalCard(
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
    val day = String.format(java.util.Locale.getDefault(), "%02d", createdAt.day)
    val createdAtDateTime = "Created at $day ${createdAt.month.name.title} " +
            "${createdAt.year} ${createdAt.hour formatToTime createdAt.minute}"

    ListItem(
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
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onNavigate?.navigate(
                    FulfillmentDetailScreenRouter(financeEntityGoal.id)
                )
            },
    )
}
