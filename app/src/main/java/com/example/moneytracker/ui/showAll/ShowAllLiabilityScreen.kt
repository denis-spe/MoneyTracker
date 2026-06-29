// Glory be the name of LORD our GOD
package com.example.moneytracker.ui.showAll

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.limitLength
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.screenManager.LiabilityDetailScreenRouter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAllLiabilityScreen(
    viewModel: ShowAllViewModel,
    navController: NavHostController
) {
    // Load all liabilities once
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadAllLiability()
    }

    // Collect the state from the ViewModel
    val showAllStates = viewModel.showAllDataset.collectAsStateWithLifecycle()

    // Extract the liabilities from the state
    val liabilities = showAllStates.value.liability

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Liabilities") },
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
        when (liabilities) {
            is DataState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is DataState.Success -> {
                val data = liabilities.data

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    items(data.size, key = { data[it].id }) { index ->
                        ShowAlLiabilityCard(
                            financeEntity = data[index],
                            onNavigate = navController
                        )
                    }
                }
            }

            is DataState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${liabilities.exception.message}")
                }
            }
        }
    }
}


@Composable
internal fun ShowAlLiabilityCard(
    modifier: Modifier = Modifier,
    financeEntity: FinanceEntity.Liability,
    onNavigate: NavController?
) {
    val createdAt = financeEntity.createdAt.formatToDateTime
    val amount = financeEntity.amount.formatToAmount()
    val label = financeEntity.label

    val progressAmount = financeEntity.settlement.sumOf { it.amount }
    val remainingAmount = (financeEntity.amount - progressAmount).coerceAtLeast(0.0)
    val color = colorResource(financeEntity.colorRes)
    val paymentMethod = financeEntity.paymentMethod
    val description = financeEntity.description
    val liabilityType = financeEntity.liabilityType.name

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

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onNavigate?.navigate(
                    LiabilityDetailScreenRouter(financeEntity.id)
                )
            },

        overlineContent = {
            Text(
                createdAt,
                style = typography.bodySmall.copy(fontSize = 12.sp),
                color = Color.Gray,
            )
        },
        headlineContent = {
            Text(
                label,
                style = typography.titleSmall,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        },

        leadingContent = {
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
        },

        supportingContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(paymentMethod.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(ICON_SIZE)
                            .clip(RoundedCornerShape(15.dp))
                    )

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.border(
                            1.dp, color,
                            RoundedCornerShape(5.dp)
                        )
                    ) {
                        Text(
                            liabilityType,
                            style = typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(5.dp)
                        )
                    }
                }

                if (description.isNotEmpty()) {
                    Text(
                        description.limitLength(250),
                        style = typography.bodySmall.copy(fontSize = 13.sp),
                        color = Color.Gray,
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            }
        },
        trailingContent = {
            Text(
                amount,
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    )


}