// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.theme.autoTextColorChange
import com.google.firebase.Timestamp

private val spacerWith = 14.dp
private val labelFontSize = 13.sp
private val FilterIconSize = 25.dp

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

sealed class DatasetItem {
    data class Data(val dataset: Dataset) : DatasetItem()
    data class Adjust(val adjustment: Adjustment) : DatasetItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListAreaSort(
    onFilterClick: MutableState<Boolean>,
    onActivateShow: MutableState<Boolean>,
    categorySorting: MutableState<String>,
    timeSorting: MutableState<SortType>,
    amountSorting: MutableState<SortType>,
    paymentSorting: MutableState<PaymentMethod?>,
    alphabeticalOrder: MutableState<SortType>
) {

    val isCategoryModelBottomOpen = remember { mutableStateOf(false) }
    val isTimeModelBottomOpen = remember { mutableStateOf(false) }
    val isPaymentModelBottomOpen = remember { mutableStateOf(false) }
    val isAlphabeticalOrderModelBottomOpen = remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = if (onFilterClick.value) Modifier else
                Modifier.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recently activates",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            IconButton(
                onClick = {
                    onActivateShow.value = !onActivateShow.value
                }
            ) {
                Icon(
                    imageVector = if (onActivateShow.value) Icons.Default.KeyboardArrowDown
                    else Icons.Default.KeyboardArrowUp,
                    contentDescription = "arrow",
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = {
                    onFilterClick.value = !onFilterClick.value
                }
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "filter",
                    modifier = Modifier.size(20.dp)
                )
            }

        }

        AnimatedVisibility(
            visible = onFilterClick.value,
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isCategoryModelBottomOpen.value = true
                    }
                ) {
                    Icon(
                        imageVector = if (categorySorting.value != "Initial") Icons.Default.Category
                        else Icons.Outlined.Category,
                        contentDescription = "Category",
                        modifier = Modifier.size(FilterIconSize),
                        tint = if (categorySorting.value != "Initial") Color.Gray else Color.autoTextColorChange
                    )
                }

                IconButton(
                    onClick = {
                        isTimeModelBottomOpen.value = true
                    }
                ) {
                    Icon(
                        imageVector = if (
                            timeSorting.value == SortType.Ascending ||
                            timeSorting.value == SortType.Descending
                        ) Icons.Default.AccessTimeFilled
                        else Icons.Outlined.AccessTime,
                        contentDescription = "timeline",
                        modifier = Modifier.size(FilterIconSize),
                        tint = if (isTimeModelBottomOpen.value) Color.Gray else Color.autoTextColorChange
                    )
                }

                IconButton(
                    onClick = {
                        isPaymentModelBottomOpen.value = true
                    }
                ) {
                    val icon = if (paymentSorting.value != null
                        || amountSorting.value != SortType.Initial
                    ) Icons.Default.Payments
                    else Icons.Outlined.Payments
                    val color = if (paymentSorting.value != null
                        || amountSorting.value != SortType.Initial
                    ) Color.Gray
                    else Color.autoTextColorChange


                    Icon(
                        imageVector = icon,
                        contentDescription = "Payments",
                        modifier = Modifier.size(FilterIconSize),
                        tint = color
                    )
                }

                IconButton(
                    onClick = {
                        isAlphabeticalOrderModelBottomOpen.value = true
                    }
                ) {
                    val iconResId = when (alphabeticalOrder.value) {
                        SortType.Ascending -> R.drawable.sort_up
                        SortType.Descending -> R.drawable.sort_down
                        else -> R.drawable.sort
                    }

                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = "Sort",
                        modifier = Modifier.size(FilterIconSize),
                        tint = if (alphabeticalOrder.value != SortType.Initial) Color.Gray else Color.autoTextColorChange
                    )
                }
            }
        }

        if (isTimeModelBottomOpen.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    isTimeModelBottomOpen.value = false
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Sort by time",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HorizontalDivider()
                    TextButton(
                        onClick = {
                            timeSorting.value = SortType.Ascending
                            isTimeModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(
                                contentColor =
                                    if (timeSorting.value == SortType.Ascending) Color.Green
                                    else Color.Gray
                            )
                    ) {
                        Text("Ascending")
                    }
                    TextButton(
                        onClick = {
                            timeSorting.value = SortType.Descending
                            isTimeModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(
                                contentColor =
                                    if (timeSorting.value == SortType.Descending) Color.Red
                                    else Color.Gray
                            )
                    ) {
                        Text("Descending")
                    }

                    TextButton(
                        onClick = {
                            timeSorting.value = SortType.Initial
                            isTimeModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(contentColor = Color.Gray)
                    ) {
                        Text("Initial")
                    }
                }
            }
        }

        if (isCategoryModelBottomOpen.value) {
            var category = DataType.entries.map { it.text }
            category = category + AdjustmentType.entries.map { it.text }
            val categoryState = remember { mutableStateOf("") }

            ModalBottomSheet(
                onDismissRequest = {
                    isCategoryModelBottomOpen.value = false
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Sort by category", modifier = Modifier.padding(bottom = 8.dp))
                    HorizontalDivider()

                    LazyVerticalGrid(
                        GridCells.Fixed(2),
                        modifier = Modifier.padding(3.dp)

                    ) {
                        items(category.size) {
                            val modifier = if (categoryState.value == category[it])
                                Modifier.border(1.dp, Color.Gray, CircleShape)
                            else Modifier

                            TextButton(
                                onClick = {
                                    categoryState.value = category[it]
                                },
                                modifier = modifier
                            ) {
                                Text(category[it])
                            }
                        }
                    }

                    HorizontalDivider()
                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                categorySorting.value = "Initial"
                                isCategoryModelBottomOpen.value = false
                            }
                        ) {
                            Text("Cancel")
                        }

                        TextButton(
                            onClick = {
                                categorySorting.value = categoryState.value
                                isCategoryModelBottomOpen.value = false
                            }
                        ) {
                            Text("Apply")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ItemListArea(
    datasets: List<Dataset>,
    todayAdjustment: List<Adjustment>,
    onActivateShow: MutableState<Boolean>
) {
    // States
    val onFilterClick = remember { mutableStateOf(false) }

    val categorySorting = remember { mutableStateOf("Initial") }
    val timeSorting = remember { mutableStateOf(SortType.Ascending) }
    val amountSorting = remember { mutableStateOf(SortType.Initial) }
    val paymentSorting = remember { mutableStateOf<PaymentMethod?>(null) }
    val alphabeticalOrder = remember { mutableStateOf(SortType.Initial) }

    val dataItems = datasets.map { DatasetItem.Data(it) }
    val adjustmentItems = todayAdjustment.map { DatasetItem.Adjust(it) }
    val datasetWithAdjust = dataItems + adjustmentItems
    val datasetItems = remember { mutableStateOf(emptyList<DatasetItem>()) }

    LaunchedEffect(
        datasets,
        onActivateShow.value,
        timeSorting.value,
        categorySorting.value,
        amountSorting.value,
        paymentSorting.value,
        alphabeticalOrder.value
    ) {
        if (onActivateShow.value) {
            datasetItems.value = datasetWithAdjust.take(4)
        } else {
            datasetItems.value = datasetWithAdjust
        }

        // Sort by time
        when (timeSorting.value) {
            SortType.Ascending -> {
                datasetItems.value = datasetWithAdjust.sortedBy {
                    when (it) {
                        is DatasetItem.Data -> it.dataset.dateTime
                        is DatasetItem.Adjust -> it.adjustment.dateTime
                    }
                }
            }

            SortType.Descending -> {
                datasetItems.value = datasetWithAdjust.sortedByDescending {
                    when (it) {
                        is DatasetItem.Data -> it.dataset.dateTime
                        is DatasetItem.Adjust -> it.adjustment.dateTime
                    }
                }
            }

            else -> {
                datasetItems.value = datasetWithAdjust
            }
        }

        // Sort by category
        when (categorySorting.value) {
            "Initial" -> {
                datasetItems.value = datasetWithAdjust
            }

            "Earnings" -> {
                datasetItems.value = datasetWithAdjust.filter {
                    when (it) {
                        is DatasetItem.Data -> it.dataset.dataType == DataType.EARNINGS
                        is DatasetItem.Adjust -> it.adjustment.dataset?.dataType == DataType.EARNINGS
                    }
                }
            }

            "Expense" -> {
                datasetItems.value = datasetWithAdjust.filter {
                    when (it) {
                        is DatasetItem.Data -> it.dataset.dataType == DataType.EXPENSE
                        is DatasetItem.Adjust -> it.adjustment.dataset?.dataType == DataType.EXPENSE
                    }
                }
            }
        }

    }


    ItemListAreaSort(
        onFilterClick,
        onActivateShow = onActivateShow,
        categorySorting = categorySorting,
        timeSorting = timeSorting,
        amountSorting = amountSorting,
        paymentSorting = paymentSorting,
        alphabeticalOrder = alphabeticalOrder
    )


    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(0.9f)
    ) {
        items(datasetItems.value.size, key = { it }) { index ->
            when (val dataset = datasetItems.value[index]) {
                is DatasetItem.Data -> {
                    val wasCompleted = if (dataset.dataset.dataType == DataType.DEBT ||
                        dataset.dataset.dataType == DataType.LENT ||
                        dataset.dataset.dataType == DataType.GOAL
                    )
                        dataset.dataset.isAmountEqualToAdjustAmount() else false
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
                            label = dataset.dataset.label,
                            labelIcon = dataset.dataset.labelIcon,
                            amount = dataset.dataset.amount,
                            dataType = dataset.dataset.dataType,
                            colorResId = dataset.dataset.dataType.color,
                            description = dataset.dataset.description,
                            dateTime = dataset.dataset.dateTime,
                            paymentMethod = dataset.dataset.paymentMethod,
                            dataset = dataset.dataset,
                            isCompleted = wasCompleted
                        )
                    }
                }

                is DatasetItem.Adjust -> {
                    val dataset = dataset.adjustment

                    val colorResId = when (dataset.adjustmentType) {
                        AdjustmentType.ATTAIN -> R.color.Attain
                        AdjustmentType.REPAYMENT -> {
                            if (dataset.dataset?.dataType == DataType.DEBT)
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
                        dataset.dataset?.let {
                            ItemCard(
                                label = dataset.label,
                                labelIcon = dataset.adjustmentIcon,
                                amount = dataset.amount,
                                dataType = null,
                                colorResId = colorResId,
                                description = dataset.description,
                                dateTime = dataset.dateTime,
                                paymentMethod = dataset.paymentMethod,
                                adjustment = dataset,
                                dataset = it
                            )
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
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(5.dp))
                .clickable {
                    onShowDialog.value = true
                }
        ) {
            Row(
                modifier = Modifier.padding(start = 5.dp),
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
                        fontSize = labelFontSize,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough
                        else TextDecoration.None
                    )

                    if (adjustment != null) {
                        Text(text = dataset.label, color = color, fontSize = labelFontSize)
                    }

                    if (description.isNotEmpty()) {
                        Text(text = description)
                    }
                    Text(text = dateTimeAsString, color = Color.Gray, fontSize = 14.sp)
                }
            }

            // Amount column
            Column(
                modifier = Modifier.padding(end = 5.dp),
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
                    horizontalAlignment = Alignment.End
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
