// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
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
                items = listOf(
                    "All", "Earnings", "Expense",
                    "Debt", "Lent", "Repay", "Savings",
                    "Goal", "Score"
                ),
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
                        val wasCompleted = if (dataset.dataType == DataType.DEBT ||
                            dataset.dataType == DataType.LENT ||
                            dataset.dataType == DataType.GOAL
                        )
                            dataset.isAmountEqualToAdjustAmount() else false
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
                                paymentMethod = dataset.paymentMethod,
                                dataset = dataset,
                                isCompleted = wasCompleted
                            )
                        }
                        val adjustments = dataset.adjustment
                        if (adjustments.isNotEmpty()) {
                            for (adjustment in adjustments) {

                                val colorResId = when (adjustment.adjustmentType) {
                                    AdjustmentType.ATTAIN -> R.color.Attain
                                    AdjustmentType.REPAY -> {
                                        if (dataset.dataType == DataType.DEBT)
                                            R.color.RepayDebt else R.color.RepayLoan
                                    }
                                }

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
                                        label = adjustment.label,
                                        labelIcon = adjustment.adjustmentIcon,
                                        amount = adjustment.amount,
                                        dataType = null,
                                        colorResId = colorResId,
                                        description = adjustment.description,
                                        dateTime = adjustment.dateTime,
                                        paymentMethod = adjustment.paymentMethod,
                                        adjustment = adjustment,
                                        dataset = dataset
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
                                    dateTime = dataset.dateTime,
                                    dataset = dataset,
                                    paymentMethod = dataset.paymentMethod
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
                                    dateTime = dataset.dateTime,
                                    dataset = dataset,
                                    paymentMethod = dataset.paymentMethod
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
                                    dataset = dataset,
                                    isCompleted = dataset.isAmountEqualToAdjustAmount(),
                                    paymentMethod = dataset.paymentMethod
                                )
                            }
                        }
                    }

                    "Repay" -> {
                        val adjustments = dataset.adjustment
                        if (adjustments.isNotEmpty()) {
                            for (adjustment in adjustments) {
                                if (adjustment.adjustmentType == AdjustmentType.REPAY) {
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
                                            label = adjustment.label,
                                            labelIcon = adjustment.adjustmentIcon,
                                            amount = adjustment.amount,
                                            dataType = DataType.REPAY,
                                            colorResId = if (dataset.dataType == DataType.DEBT)
                                                R.color.RepayDebt else R.color.RepayLoan,
                                            description = adjustment.description,
                                            dateTime = adjustment.dateTime,
                                            adjustment = adjustment,
                                            paymentMethod = adjustment.paymentMethod,
                                            dataset = dataset
                                        )
                                    }
                                }
                            }
                        }
                    }

                    "Score" -> {
                        val adjustments = dataset.adjustment
                        if (adjustments.isNotEmpty()) {
                            for (adjustment in adjustments) {
                                if (adjustment.adjustmentType == AdjustmentType.ATTAIN) {
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
                                            label = adjustment.label,
                                            labelIcon = adjustment.adjustmentIcon,
                                            amount = adjustment.amount,
                                            dataType = DataType.GOAL,
                                            colorResId = R.color.Attain,
                                            description = adjustment.description,
                                            dateTime = adjustment.dateTime,
                                            adjustment = adjustment,
                                            paymentMethod = adjustment.paymentMethod,
                                            dataset = dataset
                                        )
                                    }
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
                                    dataset = dataset,
                                    isCompleted = dataset.isAmountEqualToAdjustAmount(),
                                    paymentMethod = dataset.paymentMethod
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
                                    dateTime = dataset.dateTime,
                                    dataset = dataset,
                                    paymentMethod = dataset.paymentMethod
                                )
                            }
                        }
                    }

                    "Goal" -> {
                        if (dataset.dataType == DataType.GOAL) {
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
                                    paymentMethod = dataset.paymentMethod,
                                    dataset = dataset,
                                    isCompleted = dataset.isAmountEqualToAdjustAmount()
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
    dataset: Dataset,
    dataType: DataType? = null,
    colorResId: Int,
    description: String,
    dateTime: Timestamp,
    paymentMethod: PaymentMethod,
    adjustment: Adjustment? = null,
    isCompleted: Boolean = false
) {

    val color = colorResource(colorResId)
    val onShowDialog = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(5.dp))
                .clickable {
                    onShowDialog.value = true
                }
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
                        textDecoration = if (isCompleted) TextDecoration.LineThrough
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

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = amount,
                        color = color
                    )

                    Column(
                        modifier = Modifier.border(
                            1.dp,
                            Brush.linearGradient(
                                colors = listOf(
                                    color,
                                    color.copy(alpha = 0.5f),
                                    color.copy(alpha = 0.2f)
                                )
                            ),
                            CircleShape
                        )
                    ) {
                        Image(
                            painter = painterResource(paymentMethod.icon),
                            contentDescription = "payment method icon",
                            modifier = Modifier
                                .size(ICON_SIZE)
                                .padding(3.dp)
                        )
                    }
                }
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

    if (onShowDialog.value) {
        Receipt(
            dataset = dataset,
            adjustment = adjustment,
            onShowDialog = onShowDialog,
        )
    }
}
