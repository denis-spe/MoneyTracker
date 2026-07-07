// Glory be the name of LORD our GOD
package com.example.moneytracker.ui.showAll

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateBounds
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.moneytracker.backend.storage.types.TransactionType
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
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.screenManager.TransactionDetailScreenRouter
import com.example.moneytracker.ui.theme.StewardTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowAllTransactionScreen(
    viewModel: ShowAllViewModel,
    navController: NavHostController
) {
    // Load all transactions once
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadAllTransaction()
    }

    // Collect the state from the ViewModel
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = transactions) {
                is DataState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is DataState.Success -> {
                    val data = state.data

                    ShowAllHeroHeader(
                        transactions = data,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        onBackClick = { navController.safePopBackStack() }
                    )

                    LookaheadScope {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            item(key = "HistoryHeader") {
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
                                            text = if (searchQuery.isEmpty()) "Recent Transactions" else "Search Results",
                                            style = typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }

                                    if (searchQuery.isNotEmpty()) {
                                        Text(
                                            text = "${data.size} found",
                                            style = typography.labelSmall,
                                            color = StewardTheme.colors.primaryAccent
                                        )
                                    }
                                }
                            }

                            items(data.size, key = { data[it].id }) { index ->
                                ShowAllTransactionCard(
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
                                            text = "No transactions found",
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
fun ShowAllHeroHeader(
    transactions: List<FinanceEntity.Transaction>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val totalEarnings = remember(transactions) {
        transactions.filter { it.transactionType == TransactionType.EARNINGS }.sumOf { it.amount }
    }
    val totalSavings = remember(transactions) {
        transactions.filter { it.transactionType == TransactionType.SAVINGS }.sumOf { it.amount }
    }
    val totalExpenses = remember(transactions) {
        transactions.filter { it.transactionType == TransactionType.EXPENSES }.sumOf { it.amount }
    }

    val totalIncome = totalEarnings + totalSavings
    val netBalance = totalIncome - totalExpenses

    var showHelp by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showVisualAnalysis by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 5.dp
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
                        text = "Transactions",
                        style = typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Master your spending",
                        style = typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                LazyRow(modifier = Modifier.padding(horizontal = 8.dp)) {

                    // Search button
                    item {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (isSearchActive) StewardTheme.colors.primaryAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Statistics button
                    item {
                        IconButton(onClick = { showStats = true }) {
                            Icon(
                                imageVector = Icons.TwoTone.Insights,
                                contentDescription = "Statistics",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Visualizations button
                    item {
                        IconButton(onClick = { showVisualAnalysis = true }) {
                            Icon(
                                imageVector = Icons.TwoTone.Insights,
                                contentDescription = "visualization",
                                tint = if (showVisualAnalysis) StewardTheme.colors.primaryAccent else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    item {
                        IconButton(onClick = { showHelp = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = "Help",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
                        placeholder = { Text("Search...") },
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
                            focusedBorderColor = StewardTheme.colors.primaryAccent,
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
                    color = StewardTheme.colors.primaryAccent.copy(alpha = 0.05f),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "TOTAL NET BALANCE",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StewardTheme.colors.primaryAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = netBalance.formatToAmount(),
                    style = typography.headlineLarge.copy(
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = if (netBalance >= 0) colorResource(R.color.Earnings) else colorResource(
                        R.color.Expense
                    )
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
                    HeroStatItem(
                        label = "Inflow",
                        amount = totalIncome,
                        icon = Icons.Default.ArrowUpward,
                        color = colorResource(R.color.Earnings),
                        modifier = Modifier.weight(1f)
                    )

                    VerticalDivider(
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )

                    HeroStatItem(
                        label = "Outflow",
                        amount = totalExpenses,
                        icon = Icons.Default.ArrowDownward,
                        color = colorResource(R.color.Expense),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtle Meta Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Analyzing ${transactions.size} financial records",
                    style = typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showHelp) {
        TransactionHelpSheet(onDismiss = { showHelp = false })
    }

    if (showStats) {
        TransactionStatisticsDialog(
            transactions = transactions,
            onDismiss = { showStats = false }
        )
    }

    if (showVisualAnalysis) {
        VisualAnalysisBottomSheet(
            entities = transactions,
            onDismiss = { showVisualAnalysis = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHelpSheet(onDismiss: () -> Unit) {
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
                text = "Financial Definitions",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = StewardTheme.colors.primaryAccent
            )

            HelpContentItem(
                title = "Net Balance",
                description = "Your total financial position calculated as (Earnings + Savings) - Expenses. A positive value means you have a surplus, while a negative value indicates you've spent more than you've received."
            )

            HelpContentItem(
                title = "Inflow",
                description = "The total amount of money coming into your accounts. This typically includes your base salary, side income (Earnings), and any money set aside for future use (Savings)."
            )

            HelpContentItem(
                title = "Outflow",
                description = "The total amount of money leaving your accounts. This represents all your spending, bills, and other financial deductions (Expenses)."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HelpContentItem(title: String, description: String) {
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
fun TransactionStatisticsDialog(
    transactions: List<FinanceEntity.Transaction>,
    onDismiss: () -> Unit
) {
    val amounts = remember(transactions) { transactions.map { it.amount } }

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
                    tint = StewardTheme.colors.primaryAccent,
                    modifier = Modifier.size(40.dp)
                )

                Text(
                    text = "Data Science Statistics",
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StewardTheme.colors.primaryAccent
                )

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatDetailRow("Count", transactions.size.toString())
                    StatDetailRow("Mean (Average)", average.formatToAmount())
                    StatDetailRow("Median", median.formatToAmount())
                    StatDetailRow("Mode", mode.firstOrNull()?.formatToAmount() ?: "N/A")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    StatDetailRow("Standard Deviation", stdDev.formatResult)
                    StatDetailRow("Variance", variance.formatResult)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    StatDetailRow("Q1 (25th Percentile)", q1.formatToAmount())
                    StatDetailRow("Q3 (75th Percentile)", q3.formatToAmount())
                    StatDetailRow("IQR", iqr.formatToAmount())

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    StatDetailRow("Skewness", skewness.formatResult)
                    StatDetailRow("Kurtosis", kurtosis.formatResult)
                }

                TextButton(onClick = onDismiss, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatDetailRow(label: String, value: String) {
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
private fun HeroStatItem(
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

@Composable
internal fun ShowAllTransactionCard(
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
    val dateTime = financeEntity.createdAt
    val description = financeEntity.description

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        ListItem(
            modifier = Modifier
                .clickable {
                    onNavigate?.navigate(
                        TransactionDetailScreenRouter(financeEntity.id)
                    )
                },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            overlineContent = {
                Text(
                    dateTime.formatToDateTime,
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
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = tagIcon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },

            supportingContent = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = color.copy(alpha = 0.2f),
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            transactionType,
                            style = typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (description.isNotEmpty()) {
                        Text(
                            description.limitLength(100),
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
                        amount,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Image(
                        painter = painterResource(paymentMethod.icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .alpha(0.7f)
                    )
                }
            }
        )
    }
}
