// Bless be the name of LORD GOD of hosts, For his mercy endures forever,
package com.example.moneytracker.ui.homeScreen.allScreen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.usecase.ProfessionalSummary

@Composable
fun ProfessionalSummarySection(
    summaryState: DataState<ProfessionalSummary>
) {
    val onShown = remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { onShown.value = true }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            when (summaryState) {
                is DataState.Loading -> SummaryShimmer()
                is DataState.Success -> SummaryContent(summaryState.data)
                is DataState.Error -> Text("Failed to load summary", color = Color.Red)
            }
        }
    }

    SummaryExplanation(onShown)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryExplanation(
    onShown: MutableState<Boolean>
) {
    if (onShown.value) {
        ModalBottomSheet(
            onDismissRequest = { onShown.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Understanding Your Summary",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Explanation Items
                ExplanationItem(
                    title = "Net Period Balance",
                    description = "Your total financial position for the selected timeframe. Calculated as Total Income minus Total Outcome. A positive value (green) shows a surplus, while negative (red) indicates deficit.",
                    icon = Icons.Default.Wallet,
                    iconColor = MaterialTheme.colorScheme.primary
                )

                ExplanationItem(
                    title = "Total Income",
                    description = "The sum of all monetary inflows recorded during this period, such as earnings and other receipts but not including goals.",
                    icon = Icons.Default.ArrowUpward,
                    iconColor = Color(0xFF4CAF50)
                )

                ExplanationItem(
                    title = "Total Outcome",
                    description = "The sum of all monetary outflows excluding withdraw and attain, including both your daily expenses and any amounts you've set aside as savings.",
                    icon = Icons.Default.ArrowDownward,
                    iconColor = Color(0xFFF44336)
                )

                ExplanationItem(
                    title = "Total Savings",
                    description = "A subset of your outcome that you've specifically designated as savings. This tracks your progress in building future wealth.",
                    icon = Icons.Default.Savings,
                    iconColor = Color(0xFF2196F3)
                )

                ExplanationItem(
                    title = "Transactions",
                    description = "The count of individual financial events recorded in this period. This gives you an idea of your overall financial activity level.",
                    icon = Icons.Default.History,
                    iconColor = Color(0xFF9C27B0)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ExplanationItem(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}



@Composable
private fun SummaryContent(summary: ProfessionalSummary) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Main Balance Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Net Period Balance",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = summary.netBalance.formatToAmount(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (summary.netBalance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Secondary Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Total Income",
                value = summary.totalIncome.formatToAmount(),
                icon = Icons.Default.ArrowUpward,
                iconColor = Color(0xFF4CAF50)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Total Outcome",
                value = summary.totalOutcome.formatToAmount(),
                icon = Icons.Default.ArrowDownward,
                iconColor = Color(0xFFF44336)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Total Savings",
                value = summary.savings.formatToAmount(),
                icon = Icons.Default.Savings,
                iconColor = Color(0xFF2196F3)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Transactions",
                value = "${summary.transactionCount}",
                icon = Icons.Default.History,
                iconColor = Color(0xFF9C27B0)
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = iconColor
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SummaryShimmer() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shimmerEffect(shape = RoundedCornerShape(16.dp))
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
                    .shimmerEffect(shape = RoundedCornerShape(16.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
                    .shimmerEffect(shape = RoundedCornerShape(16.dp))
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
                    .shimmerEffect(shape = RoundedCornerShape(16.dp))
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(70.dp)
                    .shimmerEffect(shape = RoundedCornerShape(16.dp))
            )
        }
    }
}
