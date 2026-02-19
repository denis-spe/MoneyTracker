// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc

@Composable
fun ListOfData(data: List<DataAdjust>) {
    LazyColumn {
        items(data.size) {
            val dataItem = data[it]
            DataCard(dataItem)
        }
    }
}

@Composable
fun DataCard(data: DataAdjust) {
    val label = when (data) {
        is DataAdjust.Data -> data.dataset.label
        is DataAdjust.Adjust -> data.adjustment.label
    }

    val amount = when (data) {
        is DataAdjust.Data -> {
            val amount = data.dataset.amount
            if (data.dataset.dataType in listOf(DataType.LENT, DataType.EXPENSE)) {
                "-${amount.formatToAmount()}"
            } else {
                amount.formatToAmount()
            }
        }

        is DataAdjust.Adjust -> {
            val amount = data.adjustment.amount
            if (data.adjustment.adjustmentType == AdjustmentType.DEBT_REPAY) {
                "-${amount.formatToAmount()}"
            } else {
                amount.formatToAmount()
            }
        }
    }

    val description = when (data) {
        is DataAdjust.Data -> data.dataset.description
        is DataAdjust.Adjust -> data.adjustment.description
    }.let {
        if (it.length > 16) it.take(20) + "..." else it
    }

    val dateTime = when (data) {
        is DataAdjust.Data -> {
            val dateTime = data.dataset.dateTime
            val date = dateTime.toLocalDateTimeUtc()
            val time = dateTime.toLocalDateTimeUtc()
            val hour = time.hour.addZeroIfLessThenTen
            val minute = time.minute.addZeroIfLessThenTen
            val weekDay = date.dayOfWeek.name.title

            "On $weekDay at $hour:$minute"
        }

        is DataAdjust.Adjust -> {
            val dateTime = data.adjustment.dateTime
            val date = dateTime.toLocalDateTimeUtc()
            val time = dateTime.toLocalDateTimeUtc()
            val hour = time.hour.addZeroIfLessThenTen
            val minute = time.minute.addZeroIfLessThenTen
            val weekDay = date.dayOfWeek.name.title

            "On $weekDay at $hour:$minute"
        }
    }

    val tagIcon = when (data) {
        is DataAdjust.Data -> data.dataset.tagIcon.icon
        is DataAdjust.Adjust -> data.adjustment.tagIcon.icon
    }.let {
        painterResource(id = it)
    }

    val color = when (data) {
        is DataAdjust.Data -> data.dataset.dataType.color
        is DataAdjust.Adjust -> data.adjustment.adjustmentType.color
    }.let {
        colorResource(id = it)
    }


    ListItem(
        headlineContent = {
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium)
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
                Text(amount, fontSize = 15.sp, color = color)
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

        shadowElevation = 2.dp,

        overlineContent = {
        }
    )
}