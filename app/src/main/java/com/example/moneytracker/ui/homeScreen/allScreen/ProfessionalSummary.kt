// Bless be the name of LORD GOD of hosts, For his mercy endures forever,
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.usecase.ProfessionalSummary

@Composable
fun ProfessionalSummarySection(
    summaryState: DataState<ProfessionalSummary>
) {
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
            Text(
                text = "Professional Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            when (summaryState) {
                is DataState.Loading -> SummaryShimmer()
                is DataState.Success -> SummaryContent(summaryState.data)
                is DataState.Error -> Text("Failed to load summary", color = Color.Red)
            }
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
