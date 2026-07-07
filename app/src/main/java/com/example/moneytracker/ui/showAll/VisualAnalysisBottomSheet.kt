// Glory be the name of LORD our GOD
package com.example.moneytracker.ui.showAll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.VicoBarChart
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.components.charts.collections.ChartDataCollection
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.theme.StewardTheme
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualAnalysisBottomSheet(
    entities: List<FinanceEntity>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Visual Analysis",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = StewardTheme.colors.primaryAccent
            )

            if (entities.isEmpty()) {
                Text(
                    text = "No data available for analysis",
                    style = typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                when (entities.firstOrNull()) {
                    is FinanceEntity.Transaction -> {
                        TransactionVisuals(entities.filterIsInstance<FinanceEntity.Transaction>())
                    }

                    is FinanceEntity.Liability -> {
                        LiabilityVisuals(entities.filterIsInstance<FinanceEntity.Liability>())
                    }

                    is FinanceEntity.Goal -> {
                        GoalVisuals(entities.filterIsInstance<FinanceEntity.Goal>())
                    }

                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TransactionVisuals(transactions: List<FinanceEntity.Transaction>) {
    val earningsColor = colorResource(R.color.Earnings)
    val expenseColor = colorResource(R.color.Expense)
    val savingsColor = colorResource(R.color.Savings)

    // 1. Donut Chart - Breakdown by Type
    val typeBreakdown = remember(transactions, earningsColor, expenseColor, savingsColor) {
        val groups = transactions.groupBy { it.transactionType }
        DonutChartDataCollection(
            groups.map { (type, list) ->
                DonutChartData(
                    amount = list.sumOf { it.amount }.toFloat(),
                    color = when (type) {
                        TransactionType.EARNINGS -> earningsColor
                        TransactionType.EXPENSES -> expenseColor
                        TransactionType.SAVINGS -> savingsColor
                    },
                    title = type.text
                )
            }
        )
    }

    AnalysisSection(title = "Volume Distribution") {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DonutChart(
                data = typeBreakdown,
                modifier = Modifier.size(200.dp),
                chartSize = 160.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            SimpleLegend(typeBreakdown.items)
        }
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    // 2. Bar Chart - Trends (Last 7 distinct days represented in the data)
    val trendData = remember(transactions, earningsColor, expenseColor, savingsColor) {
        val dailyData = transactions
            .groupBy { it.createdAt.toLocalDateTimeUtc().date }
            .toSortedMap()
            .toList()
            .takeLast(7)

        val xValues = dailyData.indices.map { it.toDouble() }

        val earningsY = dailyData.map { (_, list) ->
            list.filter { it.transactionType == TransactionType.EARNINGS }.sumOf { it.amount }
        }
        val expensesY = dailyData.map { (_, list) ->
            list.filter { it.transactionType == TransactionType.EXPENSES }.sumOf { it.amount }
        }
        val savingsY = dailyData.map { (_, list) ->
            list.filter { it.transactionType == TransactionType.SAVINGS }.sumOf { it.amount }
        }

        ChartDataCollection(
            listOf(
                ChartData(x = xValues, y = earningsY, color = earningsColor, label = "Earnings"),
                ChartData(x = xValues, y = savingsY, color = savingsColor, label = "Savings"),
                ChartData(x = xValues, y = expensesY, color = expenseColor, label = "Expenses")
            )
        )
    }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd") }
    val dateLabels = remember(transactions) {
        transactions
            .groupBy { it.createdAt.toLocalDateTimeUtc().date }
            .toSortedMap()
            .keys
            .toList()
            .takeLast(7)
    }

    AnalysisSection(title = "Recent Trends") {
        VicoBarChart(
            chartDataCollection = trendData,
            showLegend = true,
            xValueFormatter = { value ->
                dateLabels.getOrNull(value.toInt())?.toJavaLocalDate()
                    ?.let { dateFormatter.format(it) } ?: ""
            },
            yValueFormatter = { it.formatToAmount() }
        )
    }
}

@Composable
private fun LiabilityVisuals(liabilities: List<FinanceEntity.Liability>) {
    val earningsColor = colorResource(R.color.Earnings)
    val errorColor = MaterialTheme.colorScheme.error

    val totalAmount = liabilities.sumOf { it.amount }
    val totalSettled = liabilities.sumOf { it.settlement.sumOf { s -> s.amount } }
    val remaining = (totalAmount - totalSettled).coerceAtLeast(0.0)

    val progressData = remember(liabilities, earningsColor, errorColor) {
        DonutChartDataCollection(
            listOf(
                DonutChartData(
                    amount = totalSettled.toFloat(),
                    color = earningsColor,
                    title = "Settled"
                ),
                DonutChartData(
                    amount = remaining.toFloat(),
                    color = errorColor,
                    title = "Remaining"
                )
            )
        )
    }

    AnalysisSection(title = "Overall Debt Progress") {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DonutChart(
                data = progressData,
                modifier = Modifier.size(200.dp),
                chartSize = 160.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            SimpleLegend(progressData.items)
        }
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    // Bar chart for individual liabilities (Top 5 by amount)
    val topLiabilities = remember(liabilities) {
        liabilities.sortedByDescending { it.amount }.take(5)
    }

    val barData = remember(topLiabilities, earningsColor) {
        val xValues = topLiabilities.indices.map { it.toDouble() }
        val targets = topLiabilities.map { it.amount }
        val settled = topLiabilities.map { it.settlement.sumOf { s -> s.amount } }

        ChartDataCollection(
            listOf(
                ChartData(
                    x = xValues,
                    y = targets,
                    color = Color.Gray.copy(alpha = 0.5f),
                    label = "Target"
                ),
                ChartData(x = xValues, y = settled, color = earningsColor, label = "Settled")
            )
        )
    }

    AnalysisSection(title = "Top Obligations") {
        VicoBarChart(
            chartDataCollection = barData,
            showLegend = true,
            xValueFormatter = { value ->
                topLiabilities.getOrNull(value.toInt())?.label ?: ""
            },
            yValueFormatter = { it.formatToAmount() }
        )
    }
}

@Composable
private fun GoalVisuals(goals: List<FinanceEntity.Goal>) {
    val attainColor = colorResource(R.color.Attain)

    val totalTarget = goals.sumOf { it.amount }
    val totalAchieved = goals.sumOf { it.settlement.sumOf { s -> s.amount } }
    val remaining = (totalTarget - totalAchieved).coerceAtLeast(0.0)

    val progressData = remember(goals, attainColor) {
        DonutChartDataCollection(
            listOf(
                DonutChartData(
                    amount = totalAchieved.toFloat(),
                    color = attainColor,
                    title = "Achieved"
                ),
                DonutChartData(
                    amount = remaining.toFloat(),
                    color = Color.LightGray.copy(alpha = 0.3f),
                    title = "Remaining"
                )
            )
        )
    }

    AnalysisSection(title = "Collective Goal Progress") {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DonutChart(
                data = progressData,
                modifier = Modifier.size(200.dp),
                chartSize = 160.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            SimpleLegend(progressData.items)
        }
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    // Individual goal progress (Top 5)
    val topGoals = remember(goals) {
        goals.sortedByDescending { it.amount }.take(5)
    }

    val barData = remember(topGoals, attainColor) {
        val xValues = topGoals.indices.map { it.toDouble() }
        val targets = topGoals.map { it.amount }
        val achieved = topGoals.map { it.settlement.sumOf { s -> s.amount } }

        ChartDataCollection(
            listOf(
                ChartData(
                    x = xValues,
                    y = targets,
                    color = Color.Gray.copy(alpha = 0.5f),
                    label = "Target"
                ),
                ChartData(x = xValues, y = achieved, color = attainColor, label = "Achieved")
            )
        )
    }

    AnalysisSection(title = "Key Goal Targets") {
        VicoBarChart(
            chartDataCollection = barData,
            showLegend = true,
            xValueFormatter = { value ->
                topGoals.getOrNull(value.toInt())?.label ?: ""
            },
            yValueFormatter = { it.formatToAmount() }
        )
    }
}

@Composable
private fun AnalysisSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = title,
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

@Composable
private fun SimpleLegend(items: List<DonutChartData>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                rowItems.forEach { item ->
                    LegendItem(color = item.color, label = item.title)
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
