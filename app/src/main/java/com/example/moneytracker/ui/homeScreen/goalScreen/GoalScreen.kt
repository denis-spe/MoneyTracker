// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.goalScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.HomeUiState

@Composable
fun GoalCard(
    modifier: Modifier = Modifier,
    dataset: Dataset
) {
    val startDateTime = dataset.routine.startDateTime.toLocalDateTimeUtc()
    val endDateTime = dataset.routine.deadlineDateTime.toLocalDateTimeUtc()
    "${startDateTime.day}/${startDateTime.month.name.title}/${startDateTime.year}"
    val endDate = "${endDateTime.day}/${endDateTime.month.name.title}/${endDateTime.year}"
    val status = dataset.status.name.title

    val progressAmount = dataset.adjustment.sumOf { it.amount }
    val remainingAmount = (dataset.amount - progressAmount).coerceAtLeast(0.0)

    val donutChartDataCollection = DonutChartDataCollection(
        listOf(
            DonutChartData(
                amount = progressAmount.toFloat(),
                color = colorResource(dataset.dataType.color),
                title = "Progress"
            ),

            DonutChartData(
                amount = remainingAmount.toFloat(),
                color = Color.LightGray.copy(alpha = 0.3f),
                title = "Remaining"
            )
        )
    )


    val endState = when (dataset.status) {
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


    val dateTime = when (dataset.routine.routine) {
        Routine.EveryMinute -> {
            "${endState}end at ${endDateTime.hour}:${endDateTime.minute}"
        }

        Routine.EveryHour -> {
            "$endState at ${endDateTime.hour}:${endDateTime.minute}"
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

    val routineName = if (dataset.routine.routine == Routine.Nothing) {
        "Not repeatable"
    } else {
        dataset.routine.routine.text
    }

    val createdAt = dataset.createdAt.toLocalDateTimeUtc()
    val createdAtDateTime = "Created at ${createdAt.day} ${createdAt.month.name.title} " +
            "${createdAt.year} ${createdAt.hour}:${createdAt.minute}"


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
                        text = dataset.label,
                        style = typography.titleMedium
                    )
                }
            },
            overlineContent = {

            },
            supportingContent = {
                Text(
                    text = dataset.amount.toString(),
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
                            val percentage = if (dataset.amount > 0)
                                ((progressAmount / dataset.amount) * 100).toInt()
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
                        style = typography.bodySmall
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
fun GoalScreen(
    paddingValues: PaddingValues,
    goalDatasets: List<Dataset>,
    uiState: HomeUiState,
    hasLoadedData: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (hasLoadedData) {
            LazyColumn(
                modifier = Modifier
                    .clip(RoundedCornerShape(10)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item { Spacer(modifier = Modifier.size(10.dp)) }
                items(goalDatasets) { dataset ->
                    GoalCard(
                        dataset = dataset,
                        modifier = Modifier
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                            .clip(RoundedCornerShape(10))
                            .background(
                                colorResource(id = dataset.dataType.color)
                                    .copy(alpha = 0.1f)
                            )
                    )
                }
                item { Spacer(modifier = Modifier.size(10.dp)) }
            }
        } else {
            GoalScreenShimmer()
        }
    }
}

