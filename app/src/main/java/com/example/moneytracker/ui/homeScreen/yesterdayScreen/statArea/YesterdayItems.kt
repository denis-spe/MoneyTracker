// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// love the LORD your GOD with all your soul and with all your mind
// and with all your strength and love your neighbor as your self.
package com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.outlinedIcon
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.StatusView
import com.example.moneytracker.ui.homeScreen.dataAddition.FONT_WEIGHT

private val ICON_SIZE = 20.dp
private val TIME_FONT_SIZE = 13.sp
private val LABEL_FONT_SIZE = 15.sp
private val DESCRIPTION_FONT_SIZE = 12.sp
private val AMOUNT_FONT_SIZE = 18.sp


@Composable
fun YesterdayItem(dataAdjust: DataAdjust) {
    val amount = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.amount.formatToAmount()
        is DataAdjust.Adjust -> dataAdjust.adjustment.amount.formatToAmount()
    }
    val label = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.label
        is DataAdjust.Adjust -> dataAdjust.adjustment.label
    }

    val description = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.description
        is DataAdjust.Adjust -> dataAdjust.adjustment.description
    }.let {
        if (it.length > 16) it.take(20) + "..." else it
    }

    val dateTime = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.createdAt.toLocalDateTimeUtc()
        is DataAdjust.Adjust -> dataAdjust.adjustment.dateTime.toLocalDateTimeUtc()
    }

    val color = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.colorRes
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.color
    }.let {
        colorResource(id = it)
    }

    val dataTypeIcon = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.outlinedIcon
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.icon
    }.let {
        painterResource(id = it)
    }

    val tagIcon = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.tagIcon.icon
        is DataAdjust.Adjust -> dataAdjust.adjustment.tagIcon.icon
    }.let {
        painterResource(id = it)
    }

    val paymentMethod = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.financeEntity.paymentMethod.icon
        is DataAdjust.Adjust -> dataAdjust.adjustment.paymentMethod.icon
    }.let {
        painterResource(id = it)
    }

    val adjustment = if (dataAdjust is DataAdjust.Adjust)
        dataAdjust.adjustment.financeEntity?.label
    else null

    val textDecoration = when (dataAdjust) {
        is DataAdjust.Data -> {
            dataAdjust.financeEntity.isAmountEqualToAdjustAmount()
        }

        is DataAdjust.Adjust -> {
            dataAdjust.adjustment.financeEntity?.isAmountEqualToAdjustAmount()
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

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .shadow(2.dp, spotColor = color),
        headlineContent = {
            adjustment?.let {
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
                        modifier = Modifier.size(ICON_SIZE)
                    )

                    // Tag Image
                    Image(
                        painter = tagIcon,
                        contentDescription = null,
                        modifier = Modifier.size(ICON_SIZE)
                    )

                    // Payment Method Image
                    Image(
                        painter = paymentMethod,
                        contentDescription = null,
                        modifier = Modifier.size(ICON_SIZE)
                    )
                }

                StatusView(dataAdjust)


                // Time
                Text(
                    "By $hour:$minute",
                    fontSize = TIME_FONT_SIZE,
                )
            }
        },
        shadowElevation = 2.dp
    )

}

@Composable
fun YesterdayItems(
    modifier: Modifier = Modifier,
    dataAdjust: List<DataAdjust>
) {
    Column(
        modifier = modifier
    ) {
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
                Spacer(modifier = Modifier.heightIn(10.dp))
            }
            items(dataAdjust.size) {
                val dataItem = dataAdjust[it]
                YesterdayItem(dataItem)
            }
        }
    }
}

