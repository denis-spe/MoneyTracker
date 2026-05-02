// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.SettlementType
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.isAmountEqualToSettleAmount
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.StatusView

@Composable
fun ListForAll(dataSettlements: List<DataSettlement>) {
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
        items(dataSettlements.size) {
            val dataItem = dataSettlements[it]

            DataCard(
                modifier = Modifier.animateItem(),
                dataSettlement = dataItem
            )
        }
        item { Spacer(modifier = Modifier.size(10.dp)) }
    }
}

@Composable
fun DataCard(
    modifier: Modifier = Modifier,
    dataSettlement: DataSettlement
) {
    val label = when (dataSettlement) {
        is DataSettlement.SettlementData -> dataSettlement.financeEntity.label
        is DataSettlement.SettlementAdjust -> dataSettlement.settlement.label
    }

    val amount = when (dataSettlement) {
        is DataSettlement.SettlementData -> {
            val amount = dataSettlement.financeEntity.amount
            if ((dataSettlement.financeEntity is FinanceEntity.Liability && dataSettlement.financeEntity.liabilityType == LiabilityType.LOAN) ||
                (dataSettlement.financeEntity is FinanceEntity.Transaction && dataSettlement.financeEntity.transactionType == TransactionType.EXPENSES)
            ) {
                "-${amount.formatToAmount()}"
            } else {
                amount.formatToAmount()
            }
        }

        is DataSettlement.SettlementAdjust -> {
            val amount = dataSettlement.settlement.amount
            if (dataSettlement.settlement.settlementType == SettlementType.DEBT_REPAY) {
                "-${amount.formatToAmount()}"
            } else {
                amount.formatToAmount()
            }
        }
    }

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