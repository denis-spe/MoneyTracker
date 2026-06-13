// Love the LORD you GOD with all your soul and with all your mind
// and with all your strength and love your neighbor has yourself
package com.example.moneytracker.ui.detailScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.ui.homeScreen.DataState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementDetailScreen(
    liabilityId: String,
    navController: NavHostController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val detailStates by viewModel.detailState.collectAsStateWithLifecycle()
    val financeEntity = detailStates.financeEntity

    LaunchedEffect(liabilityId) {
        viewModel.loadLiability(liabilityId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Liability Details") },
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
                label = "LiabilityDetailContent"
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
                        val liability = state.data as? FinanceEntity.Liability
                        if (liability != null) {
                            LiabilityContent(
                                liability = liability,
                                liabilityId = liabilityId,
                                onDeleteSuccess = { navController.safePopBackStack() }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Liability not found")
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
fun LiabilityContent(
    liability: FinanceEntity.Liability,
    liabilityId: String,
    onDeleteSuccess: () -> Unit
) {
    var selectedSettlement by remember { mutableStateOf<Settlement?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LiabilitySummaryCard(liability)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val color = colorResource(id = liability.liabilityType.color)

                EditLiability(
                    color = color,
                    liabilityId = liabilityId,
                    label = "Edit",
                    detailButtonType = DetailButtonType.ICON_TEXT
                )

                Spacer(modifier = Modifier.width(8.dp))

                DeleteLiabilityButton(
                    liability = liability,
                    onDeleteSuccess = onDeleteSuccess,
                    label = "Delete",
                    detailButtonType = DetailButtonType.ICON_TEXT
                )

                Spacer(modifier = Modifier.width(8.dp))

                AddSettlement(
                    color = color,
                    liabilityId = liabilityId,
                    liability = liability,
                    label = "Settlement",
                    detailButtonType = DetailButtonType.ICON_TEXT
                )
            }
        }

        if (liability.settlement.isNotEmpty()) {
            item {
                Text(
                    text = if (liability.liabilityType == LiabilityType.DEBT) "Payment History" else "Refund History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(liability.settlement) { settlement ->
                SettlementItem(
                    settlement = settlement,
                    onClick = { selectedSettlement = settlement }
                )
            }
        }
    }

    selectedSettlement?.let { settlement ->
        SettlementDetailDialog(
            settlement = settlement,
            financeType = "LIABILITY",
            onDismiss = { selectedSettlement = null }
        )
    }
}

@Composable
fun LiabilitySummaryCard(liability: FinanceEntity.Liability) {
    val totalSettled = liability.settlement.sumOf { it.amount }
    val remaining = liability.remainingAmount
    val progress = if (liability.amount > 0) (totalSettled / liability.amount).toFloat() else 0f

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween<Float>(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    val color = colorResource(id = liability.liabilityType.color)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(liability.tagIcon.icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = liability.label,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = liability.liabilityType.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(label = "Total Amount", value = liability.amount.formatToAmount())
            DetailRow(label = "Settled", value = totalSettled.formatToAmount(), valueColor = color)
            DetailRow(
                label = "Remaining",
                value = remaining.formatToAmount(),
                valueColor = if (remaining > 0) color else colorResource(id = R.color.success_complete)
            )
            DetailRow(
                label = when (liability.liabilityType) {
                    LiabilityType.DEBT -> "Deposited At"
                    LiabilityType.LOAN -> "Lent At"
                }, value = liability.createdAt.formatToDateTime
            )
            DetailRow(label = "Method", value = liability.paymentMethod.text)

            if (liability.liabilityType == LiabilityType.DEBT) {
                DetailRow(
                    label = "Received",
                    value = if (liability.isAmountReceived) "Yes" else "No",
                    valueColor = if (liability.isAmountReceived) colorResource(id = R.color.success_complete) else Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { animatedProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progress * 100).toInt()}% settled",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End),
                color = color
            )

            if (liability.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray
                )
                Text(
                    text = liability.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
