// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.helper.addNegativeToAmount
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.OnDeleteReceipt
import com.example.moneytracker.ui.OnUpdate
import com.example.moneytracker.ui.Receipt
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.StatusView
import com.example.moneytracker.ui.components.Swipe
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeViewModel

@Composable
fun ListForAll(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    dataSettlements: DataState<List<DataSettlement>>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        when (dataSettlements) {
            is DataState.Success -> {
                val data = dataSettlements.data

                if (data.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillParentMaxHeight(0.8f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.empty_list),
                                contentDescription = "empty list",
                                modifier = Modifier.size(120.dp),
                                alpha = 0.5f
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No activity recorded yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Start by adding your first transaction!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(
                        count = data.size,
                        key = { index -> data[index].id }
                    ) { index ->
                        val dataItem = data[index]

                        CardForAllItem(
                            modifier = Modifier.animateItem(),
                            dataSettlement = dataItem,
                            viewModel = viewModel,
                            userViewModel = userViewModel,
                        )
                    }
                }
            }

            is DataState.Loading -> {
                items(7) {
                    CardForAllItemShimmer()
                }
            }

            is DataState.Error -> {
                val error = dataSettlements.exception.message ?: "Failed to Load"
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: $error",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardForAllItem(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    dataSettlement: DataSettlement
) {
    val label = dataSettlement.label
    val amount = dataSettlement.addNegativeToAmount
    val description = dataSettlement.description.let {
        if (it.length > 20) it.take(20) + "..." else it
    }

    val dateTime = run {
        val dt = dataSettlement.createdAt
        val date = dt.toLocalDateTimeUtc()
        val time = dt.toLocalDateTimeUtc()
        val hour = time.hour.addZeroIfLessThenTen
        val minute = time.minute.addZeroIfLessThenTen
        val weekDay = date.dayOfWeek.name.title

        "On $weekDay at $hour:$minute"
    }

    val tagIcon = painterResource(id = dataSettlement.tagIcon.icon)
    val color = colorResource(id = dataSettlement.colorRes)
    painterResource(id = dataSettlement.icon)
    val paymentMethodIcon = painterResource(id = dataSettlement.paymentMethod.icon)

    val settlementLabel = when (dataSettlement) {
        is DataSettlement.SettlementAdjust -> dataSettlement.settlement.financeEntity?.label
        is DataSettlement.SettlementWithdrawal -> dataSettlement.withdrawal.financeEntity?.label
        else -> null
    }

    val isAmountEqualWithAdjustAmount = dataSettlement.isAmountEqualWithAdjustAmount

    val adjustTextDecoration = if (isAmountEqualWithAdjustAmount == true) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val labelTextDecoration = if (
        isAmountEqualWithAdjustAmount == true &&
        dataSettlement !is DataSettlement.SettlementAdjust &&
        dataSettlement !is DataSettlement.SettlementWithdrawal
    ) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val onShowDialog = remember { mutableStateOf(false) }
    val onShowDeleteDialog = remember { mutableStateOf(false) }
    val isUpdateModelBottonOpen = remember { mutableStateOf(false) }

    Swipe(
        onStartToEnd = { isUpdateModelBottonOpen.value = true },
        onEndToStart = { onShowDeleteDialog.value = true }
    ) {
        Column(
            modifier = modifier.clickable { onShowDialog.value = true },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                headlineContent = {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = labelTextDecoration
                        )
                        StatusView(dataSettlement, fontSize = 12.sp)
                    }
                },
                supportingContent = {
                    if (description.isNotEmpty()) {
                        Text(description, fontSize = 12.sp)
                    }
                },
                trailingContent = {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            amount,
                            fontSize = 14.sp,
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                        Text(dateTime)
                    }
                },
                leadingContent = {
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(color),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                tagIcon,
                                contentDescription = "Tag Icon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp)
                            )
                        }
                        
                        Image(
                            paymentMethodIcon,
                            contentDescription = "Payment method Icon",
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background),
                        )
                    }
                },
                shadowElevation = 0.dp,
                overlineContent = {
                    settlementLabel?.let {
                        Text(
                            it,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textDecoration = adjustTextDecoration
                        )
                    }
                }
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray.copy(0.3f)
            )
        }
    }

    if (onShowDialog.value) {
        Receipt(
            dataSettlement = dataSettlement,
            onShowDialog = onShowDialog,
            viewModel = viewModel
        )
    }

    OnDeleteReceipt(
        dataSettlement = dataSettlement,
        onShowDeleteDialog = onShowDeleteDialog,
    ) {
        when (dataSettlement) {
            is DataSettlement.SettlementData -> {
                viewModel.removeData(dataSettlement.financeEntity)
                userViewModel.showActionNotification("Data deleted successfully", Color.Red)
            }
            is DataSettlement.SettlementAdjust -> {
                val financeEntityType = when (dataSettlement.settlement.financeEntity!!) {
                    is FinanceEntity.Transaction -> "TRANSACTION"
                    is FinanceEntity.Goal -> "GOAL"
                    is FinanceEntity.Liability -> "LIABILITY"
                }
                viewModel.removeSettlementFinance(
                    dataSettlement.settlement.financeEntity!!.id,
                    financeEntityType,
                    dataSettlement.settlement
                )
                userViewModel.showActionNotification("Settlement deleted successfully", Color.Red)
            }

            is DataSettlement.SettlementWithdrawal -> {
                viewModel.removeWithdrawalFinance(
                    dataSettlement.withdrawal.datasetId,
                    dataSettlement.financeEntityType,
                    dataSettlement.withdrawal
                )
                userViewModel.showActionNotification("Withdrawal deleted successfully", Color.Red)
            }
        }
        onShowDialog.value = false
    }

    OnUpdate(
        dataSettlement = dataSettlement,
        viewModel = viewModel,
        userViewModel = userViewModel,
        isUpdateModelBottonOpen = isUpdateModelBottonOpen,
        onShowDialog = onShowDialog
    )
}

@Composable
fun CardForAllItemShimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth(),
            headlineContent = {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .shimmerEffect(
                                height = 14.dp,
                                width = 100.dp,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .shimmerEffect(
                                height = 12.dp,
                                width = 60.dp,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            },
            supportingContent = {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .shimmerEffect(
                            height = 12.dp,
                            width = 150.dp,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            },
            trailingContent = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .shimmerEffect(
                                height = 14.dp,
                                width = 80.dp,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .shimmerEffect(
                                height = 12.dp,
                                width = 100.dp,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            },
            leadingContent = {
                Column(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .shimmerEffect(
                                shape = CircleShape,
                                size = 26.dp
                            )
                    )
                }
            },
            shadowElevation = 0.dp,
        )
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray.copy(0.3f)
        )
    }
}
