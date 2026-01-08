// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE
import com.google.firebase.Timestamp

private val spacerWith = 14.dp

@Composable
fun ItemFilter(
    items: List<String>,
    selected: MutableState<String>
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(0.4f),
    ) {
        items(items.size) {
            val text = items[it]
            TextButton(
                onClick = {
                    selected.value = text
                }
            ) {

                Text(
                    buildAnnotatedString {
                        if (selected.value == text) {
                            withStyle(
                                style = SpanStyle(
                                    textDecoration = TextDecoration.Underline,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            ) {
                                append(text)
                            }
                        } else {
                            append(text)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ItemListArea(datasets: List<Dataset>) {
    val recentFilterState = remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            Text(
                "Recently activates",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Row(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            ItemFilter(
                items = listOf("All", "Earnings", "Expense", "Debt", "Lent", "Repay", "Savings"),
                selected = recentFilterState
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.9f),
        ) {
            items(datasets.size, key = { it }) {
                val dataset = datasets[it]

                when (recentFilterState.value) {
                    "All" -> {
                        Row(
                            modifier = Modifier.animateItem(
                                fadeInSpec = tween(durationMillis = 250),
                                fadeOutSpec = tween(durationMillis = 100),
                                placementSpec = spring(
                                    stiffness = Spring.StiffnessLow,
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            )
                        ) {
                            ItemCard(
                                label = dataset.label,
                                labelIcon = dataset.labelIcon,
                                amount = dataset.amount,
                                dataType = dataset.dataType,
                                colorResId = dataset.dataType.color,
                                description = dataset.description,
                                dateTime = dataset.dateTime,
                                isRepay = if (dataset.dataType == DataType.DEBT ||
                                    dataset.dataType == DataType.LENT
                                )
                                    dataset.wasRepaid() else false
                            )
                        }
                        val repayments = dataset.repay
                        if (repayments.isNotEmpty()) {
                            for (repay in repayments) {
                                Row(
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = tween(durationMillis = 250),
                                        fadeOutSpec = tween(durationMillis = 100),
                                        placementSpec = spring(
                                            stiffness = Spring.StiffnessLow,
                                            dampingRatio = Spring.DampingRatioMediumBouncy
                                        )
                                    )
                                ) {
                                    ItemCard(
                                        label = repay.label,
                                        labelIcon = repay.repayIcon,
                                        amount = repay.amount,
                                        dataType = null,
                                        colorResId = R.color.Repay,
                                        description = repay.description,
                                        dateTime = repay.dateTime
                                    )
                                }
                            }
                        }
                    }

                    "Earnings" -> {
                        if (dataset.dataType == DataType.EARNINGS) {
                            Row(
                                modifier = Modifier.animateItem(
                                    fadeInSpec = tween(durationMillis = 250),
                                    fadeOutSpec = tween(durationMillis = 100),
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    )
                                )
                            ) {
                                ItemCard(
                                    label = dataset.label,
                                    labelIcon = dataset.labelIcon,
                                    amount = dataset.amount,
                                    dataType = dataset.dataType,
                                    colorResId = dataset.dataType.color,
                                    description = dataset.description,
                                    dateTime = dataset.dateTime
                                )
                            }
                        }
                    }

                    "Expense" -> {
                        if (dataset.dataType == DataType.EXPENSE) {
                            Row(
                                modifier = Modifier.animateItem(
                                    fadeInSpec = tween(durationMillis = 250),
                                    fadeOutSpec = tween(durationMillis = 100),
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    )
                                )
                            ) {
                                ItemCard(
                                    label = dataset.label,
                                    labelIcon = dataset.labelIcon,
                                    amount = dataset.amount,
                                    dataType = dataset.dataType,
                                    colorResId = dataset.dataType.color,
                                    description = dataset.description,
                                    dateTime = dataset.dateTime
                                )
                            }
                        }
                    }

                    "Debt" -> {
                        if (dataset.dataType == DataType.DEBT) {
                            Row(
                                modifier = Modifier.animateItem(
                                    fadeInSpec = tween(durationMillis = 250),
                                    fadeOutSpec = tween(durationMillis = 100),
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    )
                                )
                            ) {
                                ItemCard(
                                    label = dataset.label,
                                    labelIcon = dataset.labelIcon,
                                    amount = dataset.amount,
                                    dataType = dataset.dataType,
                                    colorResId = dataset.dataType.color,
                                    description = dataset.description,
                                    dateTime = dataset.dateTime,
                                    isRepay = dataset.wasRepaid()
                                )
                            }
                        }
                    }

                    "Repay" -> {
                        val repayments = dataset.repay
                        if (repayments.isNotEmpty()) {
                            for (repay in repayments) {
                                Row(
                                    modifier = Modifier.animateItem(
                                        fadeInSpec = tween(durationMillis = 250),
                                        fadeOutSpec = tween(durationMillis = 100),
                                        placementSpec = spring(
                                            stiffness = Spring.StiffnessLow,
                                            dampingRatio = Spring.DampingRatioMediumBouncy
                                        )
                                    )
                                ) {
                                    ItemCard(
                                        label = repay.label,
                                        labelIcon = repay.repayIcon,
                                        amount = repay.amount,
                                        dataType = null,
                                        colorResId = R.color.Repay,
                                        description = repay.description,
                                        dateTime = repay.dateTime
                                    )
                                }
                            }
                        }
                    }

                    "Lent" -> {
                        if (dataset.dataType == DataType.LENT) {
                            Row(
                                modifier = Modifier.animateItem(
                                    fadeInSpec = tween(durationMillis = 250),
                                    fadeOutSpec = tween(durationMillis = 100),
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    )
                                )
                            ) {
                                ItemCard(
                                    label = dataset.label,
                                    labelIcon = dataset.labelIcon,
                                    amount = dataset.amount,
                                    dataType = dataset.dataType,
                                    colorResId = dataset.dataType.color,
                                    description = dataset.description,
                                    dateTime = dataset.dateTime,
                                    isRepay = dataset.wasRepaid()
                                )
                            }
                        }
                    }

                    "Savings" -> {
                        if (dataset.dataType == DataType.SAVINGS) {
                            Row(
                                modifier = Modifier.animateItem(
                                    fadeInSpec = tween(durationMillis = 250),
                                    fadeOutSpec = tween(durationMillis = 100),
                                    placementSpec = spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    )
                                )
                            ) {
                                ItemCard(
                                    label = dataset.label,
                                    labelIcon = dataset.labelIcon,
                                    amount = dataset.amount,
                                    dataType = dataset.dataType,
                                    colorResId = dataset.dataType.color,
                                    description = dataset.description,
                                    dateTime = dataset.dateTime
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(
    label: String,
    labelIcon: Int,
    amount: Double,
    dataType: DataType? = null,
    colorResId: Int,
    description: String,
    dateTime: Timestamp,
    isRepay: Boolean = false
) {
    val color = colorResource(colorResId)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.8f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Icon column
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Image(
                        painter = painterResource(labelIcon),
                        contentDescription = "list icon",
                        modifier = Modifier.size(ICON_SIZE)
                    )
                }

                Spacer(modifier = Modifier.width(spacerWith))

                // Label, description, and date time column
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    var description = description
                    if (description.length > 10)
                        description = description.substring(0..10) + "..."

                    val dateTime = dateTime.toLocalDateTimeUtc()
                    val day = dateTime.day.addZeroIfLessThenTen
                    val hour = dateTime.hour.addZeroIfLessThenTen
                    val minute = dateTime.minute.addZeroIfLessThenTen
                    val month = dateTime.month.name.take(3).title
                    val year = dateTime.year
                    val dateTimeAsString = "$day $month $year, $hour:$minute"

                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isRepay) TextDecoration.LineThrough
                        else TextDecoration.None
                    )
                    if (description.isNotEmpty()) {
                        Text(text = description)
                    }
                    Text(text = dateTimeAsString, color = Color.Gray, fontSize = 14.sp)
                }
            }

            // Amount column
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                var amount = amount.formatToAmount()
                amount = when (dataType) {
                    DataType.EXPENSE -> "-$amount"
                    DataType.DEBT -> "-$amount"
                    else -> amount
                }

                Text(
                    text = amount,
                    color = color
                )
            }

        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(top = 5.dp),
            color = color,
            thickness = 1.dp
        )
    }

}
