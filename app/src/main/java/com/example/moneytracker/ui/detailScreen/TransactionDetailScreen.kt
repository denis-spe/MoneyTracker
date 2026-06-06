// Glory be to the name of LORD of hosts
package com.example.moneytracker.ui.detailScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.helper.title
import com.example.moneytracker.ui.homeScreen.DataState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: String,
    navController: NavHostController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val detailStates by viewModel.detailState.collectAsStateWithLifecycle()
    val financeEntity = detailStates.financeEntity

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.safePopBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    val currentTransaction =
                        (financeEntity as? DataState.Success)?.data as? FinanceEntity.Transaction
                    if (currentTransaction != null) {
                        val color = colorResource(id = currentTransaction.transactionType.color)
                        val withdrawalColor = colorResource(
                            R.color.Withdrawal
                        )

                        EditTransaction(
                            color = color,
                            transactionId = transactionId
                        )

                        DeleteTransactionButton(
                            transaction = currentTransaction,
                            onDeleteSuccess = { navController.safePopBackStack() }
                        )

                        if (
                            currentTransaction.transactionType == TransactionType.EARNINGS
                        ) {
                            AddWithdrawal(
                                color = withdrawalColor,
                                transactionId = transactionId,
                                currentPaymentMethod = currentTransaction.paymentMethod,
                            )
                        }
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
                label = "TransactionDetailContent"
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
                        val transaction = state.data as? FinanceEntity.Transaction
                        if (transaction != null) {
                            TransactionContent(transaction)
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Transaction not found")
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
}

@Composable
fun TransactionContent(transaction: FinanceEntity.Transaction) {
    var selectedWithdrawal by remember { mutableStateOf<Withdrawal?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TransactionSummaryCard(transaction)
        }

        // If there are withdrawals/transfers related to this transaction
        if (transaction.withdrawal.isNotEmpty()) {
            item {
                Text(
                    text = "Withdrawal History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(transaction.withdrawal.size) { index ->
                val withdrawal = transaction.withdrawal[index]
                // Ensure financeEntity is set for WithdrawalCard to work
                withdrawal.financeEntity = transaction
                WithdrawalItem(
                    withdrawal = withdrawal,
                    onClick = { selectedWithdrawal = withdrawal }
                )
            }
        }
    }

    selectedWithdrawal?.let { withdrawal ->
        WithdrawalDetailDialog(
            withdrawal = withdrawal,
            onDismiss = { selectedWithdrawal = null }
        )
    }
}

@Composable
fun WithdrawalItem(
    withdrawal: Withdrawal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = withdrawal.toPaymentMethod.icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = withdrawal.label,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = withdrawal.amount.formatToAmount(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = withdrawal.description.ifEmpty { "No description" },
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TransactionSummaryCard(transaction: FinanceEntity.Transaction) {
    val color = colorResource(id = transaction.transactionType.color)
    val withdrawalColor = colorResource(id = R.color.Withdrawal)

    val totalWithdrawn = transaction.withdrawal.sumOf { it.amount }
    val progress =
        if (transaction.amount > 0) (totalWithdrawn / transaction.amount).toFloat() else 0f

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween<Float>(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(transaction.tagIcon.icon),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Column {
                        Text(
                            text = transaction.label.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = transaction.transactionType.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                label = "Amount",
                value = transaction.amount.formatToAmount(),
                valueColor = color
            )
            DetailRow(label = "Date", value = transaction.createdAt.formatToDateTime)
            DetailRow(label = "Method", value = transaction.paymentMethod.text)
            if (transaction.transactionType == TransactionType.EARNINGS && transaction.withdrawal.isNotEmpty()) {
                DetailRow(
                    label = "Withdrawn",
                    value = totalWithdrawn.formatToAmount(),
                    valueColor = withdrawalColor
                )

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = withdrawalColor,
                    trackColor = withdrawalColor.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(progress * 100).toInt()}% withdrawn",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.End),
                    color = withdrawalColor
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (transaction.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
