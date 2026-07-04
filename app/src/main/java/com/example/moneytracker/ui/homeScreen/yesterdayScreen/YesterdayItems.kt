// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// love the LORD your GOD with all your soul and with all your mind
// and with all your strength and love your neighbor as your self.
package com.example.moneytracker.ui.homeScreen.yesterdayScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.helper.addNegativeToAmount
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.isAmountEqualToSettleAmount
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.OnDeleteReceipt
import com.example.moneytracker.ui.OnUpdate
import com.example.moneytracker.ui.Receipt
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.StatusView
import com.example.moneytracker.ui.components.Swipe
import com.example.moneytracker.ui.dataAddition.FONT_WEIGHT
import com.example.moneytracker.ui.homeScreen.HomeViewModel

private val ICON_SIZE = 20.dp
private val TIME_FONT_SIZE = 13.sp
private val LABEL_FONT_SIZE = 15.sp
private val DESCRIPTION_FONT_SIZE = 12.sp
private val AMOUNT_FONT_SIZE = 18.sp


@Composable
fun YesterdayItem(
    modifier: Modifier = Modifier,
    dataSettlement: DataSettlement,
    showDivider: Boolean = true,
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    backgroundColor: Color
) {
    val amount = dataSettlement.addNegativeToAmount

    val label = dataSettlement.label

    val description = dataSettlement.description.let {
        if (it.length > 16) it.take(20) + "..." else it
    }

    val dateTime = dataSettlement.createdAt.toLocalDateTimeUtc()

    val color = colorResource(id = dataSettlement.colorRes)

    val dataTypeIcon = painterResource(id = dataSettlement.icon)

    val tagIcon = painterResource(id = dataSettlement.tagIcon.icon)

    val paymentMethod = painterResource(id = dataSettlement.paymentMethod.icon)

    val settlement = if (dataSettlement is DataSettlement.SettlementAdjust)
        dataSettlement.settlement.financeEntity?.label
    else null

    val textDecoration = when (dataSettlement) {
        is DataSettlement.SettlementData -> {
            dataSettlement.financeEntity.isAmountEqualToSettleAmount()
        }

        is DataSettlement.SettlementAdjust -> {
            dataSettlement.settlement.financeEntity?.isAmountEqualToSettleAmount()
        }

        is DataSettlement.SettlementWithdrawal -> {
            dataSettlement.withdrawal.financeEntity?.isAmountEqualToSettleAmount()
        }
    }.let {
        if (it == true) {
            TextDecoration.LineThrough
        } else {
            TextDecoration.None
        }
    }

    val hour = dateTime.hour.addZeroIfLessThenTen
    val minute = dateTime.minute.addZeroIfLessThenTen
    val onShowDialog = remember {
        mutableStateOf(false)
    }

    val onShowDeleteDialog = remember { mutableStateOf(false) }
    val isUpdateModelBottonOpen = remember { mutableStateOf(false) }



    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onShowDialog.value = true
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Swipe(
            onStartToEnd = {
                isUpdateModelBottonOpen.value = true
            },
            onEndToStart = {
                onShowDeleteDialog.value = true
            }
        ) {
            ListItem(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ListItemDefaults.colors()
                    .copy(
                        containerColor = backgroundColor
                    ),
                headlineContent = {
                    settlement?.let {
                        Text(
                            it,
                            fontSize = LABEL_FONT_SIZE,
                            fontWeight = FONT_WEIGHT,
                            color = Color.Gray,
                            textDecoration = textDecoration
                        )
                    }
                },
                overlineContent = {
                    // Amount
                    Text(
                        amount,
                        fontSize = AMOUNT_FONT_SIZE,
                        fontWeight = FONT_WEIGHT,
                        color = color,
                        textDecoration = textDecoration
                    )

                },

                supportingContent = {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Label
                        Text(
                            label,
                            fontSize = LABEL_FONT_SIZE,
                            fontWeight = FONT_WEIGHT,
                            textDecoration = textDecoration
                        )

                        // Description
                        if (description.isNotEmpty()) {
                            Text(description, fontSize = DESCRIPTION_FONT_SIZE)
                        }
                    }
                },

                trailingContent = {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier.padding(bottom = 5.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // DataType Image
                            Image(
                                painter = dataTypeIcon,
                                contentDescription = null,
                                modifier = Modifier.size(ICON_SIZE),
                                colorFilter = ColorFilter.tint(color)
                            )

                            Spacer(modifier = Modifier.size(2.dp))

                            // Tag Image
                            Image(
                                painter = tagIcon,
                                contentDescription = null,
                                modifier = Modifier.size(ICON_SIZE)
                            )

                            Spacer(modifier = Modifier.size(2.dp))

                            // Payment Method Image
                            Image(
                                painter = paymentMethod,
                                contentDescription = null,
                                modifier = Modifier.size(ICON_SIZE)
                            )
                        }

                        StatusView(dataSettlement)


                        // Time
                        Text(
                            "By $hour:$minute",
                            fontSize = TIME_FONT_SIZE,
                        )
                    }
                },
                shadowElevation = 0.dp
            )

            if (showDivider) HorizontalDivider()
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
                viewModel.removeSettlementFinance(
                    dataSettlement.settlement.datasetId,
                    dataSettlement.financeEntityType,
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
fun YesterdayItemShimmer(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ListItem(
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            colors = ListItemDefaults.colors().copy(
                containerColor = Color.Transparent
            ),
            headlineContent = {
                // Shimmer for headline
                Box(
                    modifier = Modifier
                        .shimmerEffect(
                            shape = RoundedCornerShape(10.dp),
                            width = 100.dp,
                            height = 20.dp
                        )
                )
            },

            supportingContent = {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Shimmer for label
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .shimmerEffect(
                                shape = RoundedCornerShape(10.dp),
                                width = 120.dp,
                                height = 20.dp
                            )
                    )

                    // Shimmer for description
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .shimmerEffect(
                                shape = RoundedCornerShape(10.dp),
                                width = 150.dp,
                                height = 15.dp
                            )
                    )
                }
            },

            trailingContent = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Shimmer for dataType Image
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .shimmerEffect(
                                    shape = CircleShape,
                                    size = ICON_SIZE
                                )
                        )

                        // Shimmer for tag Image
                        Box(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .shimmerEffect(
                                    shape = CircleShape,
                                    size = ICON_SIZE
                                )
                        )

                        // Shimmer for payment Method Image
                        Box(
                            modifier = Modifier
                                .shimmerEffect(
                                    shape = CircleShape,
                                    size = ICON_SIZE
                                )
                        )
                    }

                    // Shimmer for status view
                    Box(
                        modifier = Modifier
                            .shimmerEffect(
                                shape = RoundedCornerShape(10.dp),
                                width = 60.dp,
                                height = 20.dp
                            )
                    )


                    // Shimmer for time
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .shimmerEffect(
                                shape = RoundedCornerShape(10.dp),
                                width = 80.dp,
                                height = 15.dp
                            )
                    )
                }
            },
            shadowElevation = 0.dp
        )

        HorizontalDivider(
            modifier = Modifier
                .shadow(
                    2.dp,
                    spotColor = Color.LightGray
                ),
            color = Color.LightGray.copy(0.6f)
        )
    }

}


@Composable
fun YesterdayItems(
    modifier: Modifier = Modifier,
    dataSettlements: List<DataSettlement>,
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    backgroundColor: Color
) {
    // A single elevated Surface creates a cohesive card look for the group
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            dataSettlements.forEachIndexed { index, settlement ->
                val isItemNotTheLast = index < dataSettlements.size - 1

                YesterdayItem(
                    modifier = Modifier.fillMaxWidth(),
                    dataSettlement = settlement,
                    showDivider = true,
                    viewModel = viewModel,
                    userViewModel = userViewModel,
                    backgroundColor = backgroundColor
                )
            }
        }
    }
}



/**
 * ========================= Previews ======================================
 */
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun YesterdayItemShimmerPreview() {
    Column {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Text(
                "Late Transactions",
                fontSize = 18.sp,
                fontWeight = FONT_WEIGHT,
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(220.dp, 300.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            item {
                YesterdayItemShimmer()
            }
        }
    }
}

