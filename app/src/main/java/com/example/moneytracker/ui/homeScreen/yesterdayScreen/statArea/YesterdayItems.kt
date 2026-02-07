// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// love the LORD your GOD with all your soul and with all your mind
// and with all your strength and love your neighbor as your self.
package com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.dataAddition.FONT_WEIGHT

private val ICON_SIZE = 20.dp
private val TIME_FONT_SIZE = 13.sp
private val LABEL_FONT_SIZE = 15.sp
private val DESCRIPTION_FONT_SIZE = 12.sp
private val AMOUNT_FONT_SIZE = 18.sp


@Composable
fun <T> YesterdayItem(dataAdjust: T) {
    when (dataAdjust) {
        is Dataset -> {

            val amount = dataAdjust.amount.formatToAmount()
            val label = dataAdjust.label
            val description = if (dataAdjust.description.length > 16)
                dataAdjust.description.take(16) + "..."
            else
                dataAdjust.description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Amount
                        Text(
                            amount,
                            fontSize = AMOUNT_FONT_SIZE,
                            fontWeight = FONT_WEIGHT,
                            color = colorResource(dataAdjust.dataType.color)
                        )
                        // Label
                        Text(
                            label,
                            fontSize = LABEL_FONT_SIZE,
                            fontWeight = FONT_WEIGHT
                        )

                        // Description
                        if (description.isNotEmpty()) {
                            Text(description, fontSize = DESCRIPTION_FONT_SIZE)
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // DataType Image
                            Image(
                                painter = painterResource(id = dataAdjust.dataType.outlinedIcon),
                                contentDescription = null,
                                modifier = Modifier.size(ICON_SIZE)
                            )

                            // Payment Method Image
                            Image(
                                painter = painterResource(id = dataAdjust.tagIcon.icon),
                                contentDescription = null,
                                modifier = Modifier.size(ICON_SIZE)
                            )

                            // Payment Method Image
                            Image(
                                painter = painterResource(id = dataAdjust.paymentMethod.icon),
                                contentDescription = null,
                                modifier = Modifier.size(ICON_SIZE)
                            )
                        }

                        // Time and day
                        Row {
                            val dateTime = dataAdjust.dateTime.toLocalDateTimeUtc()
                            val hour = dateTime.hour.addZeroIfLessThenTen
                            val minute = dateTime.minute.addZeroIfLessThenTen

                            Text("At $hour:$minute", fontSize = TIME_FONT_SIZE)
                        }

                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 25.dp, vertical = 5.dp),
                    color = Color.LightGray
                )
            }
        }
    }
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
                .padding(start = 30.dp, bottom = 10.dp)
        ) {
            Text(
                "Late Transactions",
                fontSize = 18.sp,
                fontWeight = FONT_WEIGHT,
            )
        }
        LazyColumn(
            modifier = Modifier
                .heightIn(200.dp, 300.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            items(dataAdjust.size) {
                when (val data = dataAdjust[it]) {
                    is DataAdjust.Data -> YesterdayItem(data.dataset)
                    is DataAdjust.Adjust -> YesterdayItem(data.adjustment)
                }
            }
        }
    }
}

