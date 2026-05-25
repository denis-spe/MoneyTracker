// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.moneytracker.helper.isAmountEqualToSettleAmount
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
    Modifier
        .offset(y = (-4).dp)
        .shadow(
            20.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        )
    Modifier
        .offset(y = (-4).dp)
        .shadow(
            20.dp,
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        )

    LazyColumn {
        item { Spacer(modifier = Modifier.size(10.dp)) }
        when (dataSettlements) {
            is DataState.Success -> {
                val data = dataSettlements.data

                if (data.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillParentMaxHeight()
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.empty_list),
                                contentDescription = "empty list",
                                modifier = Modifier.size(60.dp)
                            )
                            Text(
                                buildString {
                                    append("No activity recorded\n")
                                    append("for this period")
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(
                        count = data.size,
                        key = { index ->
                            when (val item = data[index]) {
                                is DataSettlement.SettlementData -> item.financeEntity.id
                                is DataSettlement.SettlementAdjust -> item.settlement.settlementId
                            }
                        }
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
                        Text("Error: $error")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.size(10.dp)) }
    }
}

@Composable
fun CardForAllItem(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    dataSettlement: DataSettlement
) {
    val label = when (dataSettlement) {
        is DataSettlement.SettlementData -> dataSettlement.financeEntity.label
        is DataSettlement.SettlementAdjust -> dataSettlement.settlement.label
    }

    val amount = dataSettlement.addNegativeToAmount

    val description = when (dataSettlement) {
        is DataSettlement.SettlementData -> dataSettlement.financeEntity.description
        is DataSettlement.SettlementAdjust -> dataSettlement.settlement.description
    }.let {
        if (it.length > 16) it.take(20) + "..." else it
    }

    val dateTime = when (dataSettlement) {
        is DataSettlement.SettlementData -> {
            val dateTime = dataSettlement.financeEntity.createdAt
            val date = dateTime.toLocalDateTimeUtc()
            val time = dateTime.toLocalDateTimeUtc()
            val hour = time.hour.addZeroIfLessThenTen
            val minute = time.minute.addZeroIfLessThenTen
            val weekDay = date.dayOfWeek.name.title

            "On $weekDay at $hour:$minute"
        }

        is DataSettlement.SettlementAdjust -> {
            val dateTime = dataSettlement.settlement.dateTime
            val date = dateTime.toLocalDateTimeUtc()
            val time = dateTime.toLocalDateTimeUtc()
            val hour = time.hour.addZeroIfLessThenTen
            val minute = time.minute.addZeroIfLessThenTen
            val weekDay = date.dayOfWeek.name.title

            "On $weekDay at $hour:$minute"
        }
    }

    val tagIcon = when (dataSettlement) {
        is DataSettlement.SettlementData -> dataSettlement.financeEntity.tagIcon.icon
        is DataSettlement.SettlementAdjust -> dataSettlement.settlement.tagIcon.icon
    }.let {
        painterResource(id = it)
    }

    val color = when (dataSettlement) {
        is DataSettlement.SettlementData -> dataSettlement.financeEntity.colorRes
        is DataSettlement.SettlementAdjust -> dataSettlement.settlement.settlementType.color
    }.let {
        colorResource(id = it)
    }

    val settlement = if (dataSettlement is DataSettlement.SettlementAdjust)
        dataSettlement.settlement.financeEntity?.label
    else null

    val isAmountEqualWithAdjustAmount = when (dataSettlement) {
        is DataSettlement.SettlementData -> {
            dataSettlement.financeEntity.isAmountEqualToSettleAmount()
        }

        is DataSettlement.SettlementAdjust -> {
            dataSettlement.settlement.financeEntity?.isAmountEqualToSettleAmount()
        }
    }

    val adjustTextDecoration = if (
        isAmountEqualWithAdjustAmount == true
    ) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val labelTextDecoration = if (
        isAmountEqualWithAdjustAmount == true && dataSettlement !is DataSettlement.SettlementAdjust
    ) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val onShowDialog = remember {
        mutableStateOf(false)
    }


    val onShowDeleteDialog = remember { mutableStateOf(false) }
    val isUpdateModelBottonOpen = remember { mutableStateOf(false) }

    Swipe(
        onStartToEnd = {
            isUpdateModelBottonOpen.value = true
        },
        onEndToStart = {
            onShowDeleteDialog.value = true
        }
    ) {
        Column(
            modifier = modifier.clickable {
                onShowDialog.value = true
            },
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
                                .size(20.dp)
                                .padding(3.dp)
                        )
                    }
                },

                shadowElevation = 0.dp,

                overlineContent = {
                    settlement?.let {
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
                    // Shimmer for label
                    Box(
                        modifier = Modifier
                            .shimmerEffect(
                                height = 14.dp,
                                width = 100.dp,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )


                    // Shimmer for status
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
                // Shimmer for description
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
                    // Shimmer for amount
                    Box(
                        modifier = Modifier
                            .shimmerEffect(
                                height = 14.dp,
                                width = 80.dp,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Shimmer for dateTime
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
                    // Shimmer for tag icon
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

