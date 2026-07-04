// Glory be the name of LORD our GOD
package com.example.moneytracker.ui.showAll

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateBounds
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.twotone.Insights
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.formatResult
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.iqr
import com.example.moneytracker.helper.kurtosis
import com.example.moneytracker.helper.limitLength
import com.example.moneytracker.helper.mode
import com.example.moneytracker.helper.quartiles
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.helper.skewness
import com.example.moneytracker.helper.std
import com.example.moneytracker.helper.variance
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.screenManager.LiabilityDetailScreenRouter
import com.example.moneytracker.ui.theme.StewardTheme


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
    val liabilities by viewModel.filteredLiabilities.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQueryLiability.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = liabilities) {
                is DataState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DataState.Success -> {
                    val data = state.data

                    ShowAllLiabilityHeroHeader(
                        liabilities = data,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.onSearchQueryLiabilityChange(it) },
                        onBackClick = { navController.safePopBackStack() }
                    )

                    LookaheadScope {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            item(key = "LiabilityHistoryHeader") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 20.dp,
                                            end = 20.dp,
                                            top = 24.dp,
                                            bottom = 8.dp
                                        ),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = StewardTheme.colors.primaryAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (searchQuery.isEmpty()) "Outstanding Obligations" else "Search Results",
                                            style = typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }

                                    if (searchQuery.isNotEmpty()) {
                                        Text(
                                            text = "${data.size} items",
                                            style = typography.labelSmall,
                                            color = StewardTheme.colors.primaryAccent
                                        )
                                    }
                                }
                            }

                            items(data.size, key = { data[it].id }) { index ->
                                ShowAlLiabilityCard(
                                    modifier = Modifier
                                        .animateItem()
                                        .animateBounds(this@LookaheadScope),
                                    financeEntity = data[index],
                                    onNavigate = navController
                                )
                            }

                            if (data.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No liabilities found",
                                            style = typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                is DataState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.exception.message}")
                    }
                }
            }
        }
    }
}

@Composable
fun ShowAllLiabilityHeroHeader(
    liabilities: List<FinanceEntity.Liability>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val totalAmount = remember(liabilities) { liabilities.sumOf { it.amount } }
    val totalSettled =
        remember(liabilities) { liabilities.sumOf { it.settlement.sumOf { s -> s.amount } } }
    val totalRemaining = (totalAmount - totalSettled).coerceAtLeast(0.0)

    var showHelp by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            // Navigation Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        CircleShape
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Liabilities",
                        style = typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Manage your debt",
                        style = typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = if (isSearchActive) StewardTheme.colors.primaryAccent else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showStats = true }) {
                        Icon(
                            imageVector = Icons.TwoTone.Insights,
                            contentDescription = "Statistics",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { showHelp = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = "Help",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isSearchActive,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search by label or type...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, null)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = StewardTheme.colors.primaryAccent
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Metric Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "TOTAL OUTSTANDING",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = totalRemaining.formatToAmount(),
                    style = typography.headlineLarge.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Integrated Stats Dashboard
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiabilityHeroStatItem(
                        label = "Total Debt",
                        amount = totalAmount,
                        icon = Icons.Default.ArrowUpward,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )

                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )

                    LiabilityHeroStatItem(
                        label = "Settled",
                        amount = totalSettled,
                        icon = Icons.Default.ArrowDownward,
                        color = colorResource(R.color.Earnings),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showHelp) {
        LiabilityHelpSheet(onDismiss = { showHelp = false })
    }

    if (showStats) {
        LiabilityStatisticsDialog(
            liabilities = DataState.Success(liabilities),
            onDismiss = { showStats = false }
        )
    }
}

@Composable
private fun LiabilityHeroStatItem(
    label: String,
    amount: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Text(
                text = label,
                style = typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = amount.formatToAmount(),
            style = typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = color,
            letterSpacing = (-0.5).sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiabilityHelpSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Liability Concepts",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )

            LiabilityHelpItem(
                title = "Total Outstanding",
                description = "The current net amount you still owe across all active liabilities. Calculated as Total Debt minus any Settlements made."
            )

            LiabilityHelpItem(
                title = "Total Debt",
                description = "The original principal amount of all your loans, borrowings, or debts added to the system."
            )

            LiabilityHelpItem(
                title = "Settled Amount",
                description = "The total amount you have successfully paid back towards your liabilities. Increasing this value reduces your outstanding debt."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LiabilityHelpItem(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = description,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LiabilityStatisticsDialog(
    liabilities: DataState<List<FinanceEntity.Liability>>,
    onDismiss: () -> Unit
) {
    val data = (liabilities as? DataState.Success)?.data ?: emptyList()
    val amounts = remember(data) { data.map { it.amount } }

    val average = amounts.average()
    val median = if (amounts.isEmpty()) 0.0
    else {
        val sorted = amounts.sorted()
        val mid = sorted.size / 2
        if (sorted.size % 2 == 0) (sorted[mid - 1] + sorted[mid]) / 2.0 else sorted[mid]
    }

    val mode = amounts.mode()
    val (q1, _, q3) = amounts.quartiles()
    val iqr = amounts.iqr()
    val skewness = amounts.skewness()
    val kurtosis = amounts.kurtosis()
    val stdDev = amounts.std
    val variance = amounts.variance

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Insights,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(40.dp)
                )

                Text(
                    text = "Liability Analytics",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LiabilityStatRow("Total Count", data.size.toString())
                    LiabilityStatRow("Average Debt", average.formatToAmount())
                    LiabilityStatRow("Median Debt", median.formatToAmount())

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    LiabilityStatRow("Standard Deviation", stdDev.formatResult)
                    LiabilityStatRow("Skewness", skewness.formatResult)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    LiabilityStatRow("Q1 (25th)", q1.formatToAmount())
                    LiabilityStatRow("Q3 (75th)", q3.formatToAmount())
                    LiabilityStatRow("IQR", iqr.formatToAmount())
                }

                TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        "Close",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun LiabilityStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
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

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        ListItem(
            modifier = Modifier
                .clickable {
                    onNavigate?.navigate(
                        LiabilityDetailScreenRouter(financeEntity.id)
                    )
                },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            overlineContent = {
                Text(
                    createdAt,
                    style = typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            },
            headlineContent = {
                Text(
                    label,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            },

            leadingContent = {
                DonutChart(
                    data = donutChartDataCollection,
                    modifier = Modifier.size(56.dp),
                    chartSize = 48.dp,
                    strokeWidth = 5.dp,
                    strokeWidthSelected = 7.dp,
                    gapPercentage = 0.05f,
                    strokeCap = StrokeCap.Round,
                    selectionView = {
                        Text(
                            text = "$animatedPercentage%",
                            style = typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Image(
                            painter = painterResource(paymentMethod.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = color.copy(alpha = 0.1f),
                            modifier = Modifier.border(
                                0.5.dp,
                                color.copy(alpha = 0.2f),
                                RoundedCornerShape(6.dp)
                            )
                        ) {
                            Text(
                                text = liabilityType,
                                style = typography.labelSmall,
                                color = color,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (description.isNotEmpty()) {
                        Text(
                            text = description.limitLength(60),
                            style = typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            },
            trailingContent = {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = amount,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Remaining: ${remainingAmount.formatToAmount()}",
                        style = typography.labelSmall,
                        color = if (remainingAmount > 0) MaterialTheme.colorScheme.error else colorResource(
                            R.color.Earnings
                        )
                    )
                }
            }
        )
    }
}
