// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc

@Composable
fun ListForAll(dataAdjusts: List<DataAdjust>) {
    val topRoundedCornerModifier = Modifier
        .shadow(
            2.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        )
    val bottomRoundedCornerModifier = Modifier.shadow(
        2.dp,
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    )

    LazyColumn {
        item { Spacer(modifier = Modifier.size(10.dp)) }
        items(dataAdjusts.size) {
            val dataItem = dataAdjusts[it]
            val modifier = when (it) {
                0 -> topRoundedCornerModifier
                dataAdjusts.size - 1 -> bottomRoundedCornerModifier
                else -> Modifier
            }
            val shadowElevation = when (it) {
                0 -> 0.dp
                dataAdjusts.size - 1 -> 0.dp
                else -> 2.dp
            }

            DataCard(
                modifier = modifier,
                shadowElevation = shadowElevation,
                dataAdjust = dataItem
            )
        }
        item { Spacer(modifier = Modifier.size(10.dp)) }
    }
}

@Composable
fun DataCard(
    modifier: Modifier = Modifier,
    shadowElevation: Dp = 2.dp,
    dataAdjust: DataAdjust
) {
    val label = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.label
        is DataAdjust.Adjust -> dataAdjust.adjustment.label
    }

    val amount = when (dataAdjust) {
        is DataAdjust.Data -> {
            val amount = dataAdjust.dataset.amount
            if (dataAdjust.dataset.dataType in listOf(DataType.LENT, DataType.EXPENSE)) {
                "-${amount.formatToAmount()}"
            } else {
                amount.formatToAmount()
            }
        }

        is DataAdjust.Adjust -> {
            val amount = dataAdjust.adjustment.amount
            if (dataAdjust.adjustment.adjustmentType == AdjustmentType.DEBT_REPAY) {
                "-${amount.formatToAmount()}"
            } else {
                amount.formatToAmount()
            }
        }
    }

    val description = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.description
        is DataAdjust.Adjust -> dataAdjust.adjustment.description
    }.let {
        if (it.length > 16) it.take(20) + "..." else it
    }

    val dateTime = when (dataAdjust) {
        is DataAdjust.Data -> {
            val dateTime = dataAdjust.dataset.dateTime
            val date = dateTime.toLocalDateTimeUtc()
            val time = dateTime.toLocalDateTimeUtc()
            val hour = time.hour.addZeroIfLessThenTen
            val minute = time.minute.addZeroIfLessThenTen
            val weekDay = date.dayOfWeek.name.title

            "On $weekDay at $hour:$minute"
        }

        is DataAdjust.Adjust -> {
            val dateTime = dataAdjust.adjustment.dateTime
            val date = dateTime.toLocalDateTimeUtc()
            val time = dateTime.toLocalDateTimeUtc()
            val hour = time.hour.addZeroIfLessThenTen
            val minute = time.minute.addZeroIfLessThenTen
            val weekDay = date.dayOfWeek.name.title

            "On $weekDay at $hour:$minute"
        }
    }

    val tagIcon = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.tagIcon.icon
        is DataAdjust.Adjust -> dataAdjust.adjustment.tagIcon.icon
    }.let {
        painterResource(id = it)
    }

    val color = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.dataType.color
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.color
    }.let {
        colorResource(id = it)
    }

    val adjustment = if (dataAdjust is DataAdjust.Adjust)
        dataAdjust.adjustment.dataset?.label
    else null

    val isAmountEqualWithAdjustAmount = when (dataAdjust) {
        is DataAdjust.Data -> {
            dataAdjust.dataset.isAmountEqualToAdjustAmount()
        }

        is DataAdjust.Adjust -> {
            dataAdjust.adjustment.dataset?.isAmountEqualToAdjustAmount()
        }
    }

    val textDecoration = if (
        isAmountEqualWithAdjustAmount == true
    ) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        headlineContent = {
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = textDecoration
            )
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

        shadowElevation = shadowElevation,

        overlineContent = {
            adjustment?.let {
                Text(
                    it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = textDecoration
                )
            }
        }
    )
}