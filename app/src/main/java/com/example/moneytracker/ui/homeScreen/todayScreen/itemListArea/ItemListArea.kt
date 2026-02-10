// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.title
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.theme.autoTextColorChange

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

    val categoryState = remember { mutableStateOf("") }
    val amountState = remember { mutableStateOf(SortType.Initial) }
    val paymentState = remember { mutableStateOf<PaymentMethod?>(null) }






    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent Transactions",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

        }

        AnimatedVisibility(
            visible = onFilterClick.value,
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Row(
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
                    val color = if (timeSorting.value != SortType.Initial) Color.Gray
                    else Color.autoTextColorChange
                    val imageVec = if (
                        timeSorting.value == SortType.Ascending ||
                        timeSorting.value == SortType.Descending
                    ) Icons.Default.AccessTimeFilled else Icons.Outlined.AccessTime

                    Icon(
                        imageVector = imageVec,
                        contentDescription = "timeline",
                        modifier = Modifier.size(FilterIconSize),
                        tint = color
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
                        SortType.Descending -> R.drawable.sort_up
                        SortType.Ascending -> R.drawable.sort_down
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
                    Row(
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTimeFilled,
                            contentDescription = "Time",
                            modifier = Modifier
                                .size(FilterIconSize)
                                .padding(end = 8.dp)
                        )

                        Text(
                            "Sort by time",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
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
                        Text("Don't sort")
                    }
                }
            }
        }

        if (isCategoryModelBottomOpen.value) {
            var category = DataType.entries.map { Pair(it.text, it.color) }
            category = category + AdjustmentType.entries.map {
                if (it == AdjustmentType.INITIAL) {
                    Pair("Don't sort", it.color)
                } else {
                    Pair(it.text, it.color)
                }
            }

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
                    Row(
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = "Category",
                            modifier = Modifier
                                .size(FilterIconSize)
                                .padding(end = 8.dp)
                        )
                        Text(
                            "Sort by category",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    HorizontalDivider()

                    LazyVerticalGrid(
                        GridCells.Fixed(2),
                        modifier = Modifier.padding(3.dp)

                    ) {
                        items(category.size) {


                            val selectedCategory = category[it].first
                            val colorResId = category[it].second


                            TextButton(
                                onClick = {
                                    if (selectedCategory == "Don't sort") {
                                        categoryState.value = ""
                                        categorySorting.value = "Initial"
                                        isCategoryModelBottomOpen.value = false
                                    } else {
                                        categoryState.value = selectedCategory
                                    }
                                },
                            ) {
                                Text(
                                    text = selectedCategory,
                                    color = if (selectedCategory == categoryState.value)
                                        colorResource(colorResId)
                                    else Color.Gray.copy(alpha = 0.5f),
                                    fontSize = if (categoryState.value == selectedCategory) 18.sp
                                    else 15.sp,
                                )
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
                                categoryState.value = ""
                                isCategoryModelBottomOpen.value = false
                            },
                            colors = ButtonDefaults.textButtonColors()
                                .copy(contentColor = Color.autoTextColorChange)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                categorySorting.value = categoryState.value
                                isCategoryModelBottomOpen.value = false
                            },
                            colors = ButtonDefaults.buttonColors()
                                .copy(
                                    contentColor = Color.autoTextColorChange,
                                    containerColor = Color.LightGray.copy(alpha = 0.3f)
                                )
                        ) {
                            Text("Apply")
                        }
                    }
                }
            }
        }

        if (isAlphabeticalOrderModelBottomOpen.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    isAlphabeticalOrderModelBottomOpen.value = false
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sort),
                            contentDescription = "Sort",
                            modifier = Modifier
                                .size(FilterIconSize)
                                .padding(end = 8.dp)
                        )

                        Text(
                            "Sort with label",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    HorizontalDivider()
                    TextButton(
                        onClick = {
                            alphabeticalOrder.value = SortType.Ascending
                            isAlphabeticalOrderModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(
                                contentColor =
                                    if (alphabeticalOrder.value == SortType.Ascending) Color.Green
                                    else Color.Gray
                            )
                    ) {
                        Text("Ascending")
                    }
                    TextButton(
                        onClick = {
                            alphabeticalOrder.value = SortType.Descending
                            isAlphabeticalOrderModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(
                                contentColor =
                                    if (alphabeticalOrder.value == SortType.Descending) Color.Red
                                    else Color.Gray
                            )
                    ) {
                        Text("Descending")
                    }

                    TextButton(
                        onClick = {
                            alphabeticalOrder.value = SortType.Initial
                            isAlphabeticalOrderModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(contentColor = Color.Gray)
                    ) {
                        Text("Don't sort")
                    }
                }
            }
        }

        if (isPaymentModelBottomOpen.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    isPaymentModelBottomOpen.value = false
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = "Payment",
                            modifier = Modifier
                                .size(FilterIconSize)
                                .padding(end = 8.dp)
                        )

                        Text(
                            "Sort by Payment",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    HorizontalDivider()

                    Row(
                        modifier = Modifier.padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        val selectedColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                        val unselectedColor = Color.Gray.copy(0.4f)

                        Column {
                            PaymentMethod.entries.forEach {
                                TextButton(
                                    onClick = {
                                        paymentState.value = it
                                    }) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = it.icon),
                                            contentDescription = it.text,
                                            modifier = Modifier
                                                .size(30.dp)
                                                .padding(end = 8.dp)
                                        )
                                        Text(
                                            it.text,
                                            fontSize = 15.sp,
                                            color = if (paymentState.value == it) selectedColor
                                            else unselectedColor
                                        )
                                    }
                                }
                            }
                        }

                        Column(Modifier.selectableGroup()) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = amountState.value == SortType.Ascending,
                                    onClick = { amountState.value = SortType.Ascending },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Localized Description"
                                    },
                                    colors = RadioButtonDefaults.colors().copy(
                                        selectedColor = selectedColor,
                                        unselectedColor = unselectedColor
                                    )
                                )
                                Text(
                                    "Ascending", color =
                                        if (amountState.value == SortType.Ascending) selectedColor
                                        else unselectedColor
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = amountState.value == SortType.Descending,
                                    onClick = { amountState.value = SortType.Descending },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Localized Description"
                                    },
                                    colors = RadioButtonDefaults.colors().copy(
                                        selectedColor = selectedColor,
                                        unselectedColor = unselectedColor
                                    )
                                )
                                Text(
                                    "Descending",
                                    color = if (amountState.value == SortType.Descending) selectedColor
                                    else unselectedColor
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = amountState.value == SortType.Initial,
                                    onClick = { amountState.value = SortType.Initial },
                                    modifier = Modifier.semantics {
                                        contentDescription = "Localized Description"
                                    },
                                    colors = RadioButtonDefaults.colors().copy(
                                        selectedColor = selectedColor,
                                        unselectedColor = unselectedColor
                                    )
                                )
                                Text(
                                    "Don't sort",
                                    color = if (amountState.value == SortType.Initial) selectedColor
                                    else unselectedColor
                                )
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
                                paymentSorting.value = null
                                paymentState.value = null
                                amountState.value = SortType.Initial
                                amountSorting.value = SortType.Initial
                                isPaymentModelBottomOpen.value = false
                            },
                            colors = ButtonDefaults.textButtonColors()
                                .copy(contentColor = Color.autoTextColorChange)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                paymentSorting.value = paymentState.value
                                amountSorting.value = amountState.value
                                isPaymentModelBottomOpen.value = false
                            },
                            colors = ButtonDefaults.buttonColors()
                                .copy(
                                    contentColor = Color.autoTextColorChange,
                                    containerColor = Color.LightGray.copy(alpha = 0.3f)
                                )
                        ) {
                            Text("Apply")
                        }
                    }

                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemListArea(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    onActivateShow: MutableState<Boolean>
) {
    // States
    val onFilterClick = remember { mutableStateOf(false) }

    val categorySorting = remember { mutableStateOf("Initial") }
    val timeSorting = remember { mutableStateOf(SortType.Descending) }
    val amountSorting = remember { mutableStateOf(SortType.Initial) }
    val paymentSorting = remember { mutableStateOf<PaymentMethod?>(null) }
    val alphabeticalOrder = remember { mutableStateOf(SortType.Initial) }

    // Sort with date time
    val datasetWithAdjust = viewModel.sortTodayDataAdjust(
        timeSorting.value,
        categorySorting.value,
        paymentSorting.value,
        alphabeticalOrder.value,
        amountSorting.value,
        if (onActivateShow.value) 4 else null
    )


    val datasetItems = datasetWithAdjust.collectAsState(initial = emptyList()).value


    Column(
        modifier = modifier
    ) {
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
                .fillMaxSize()
        ) {
            items(datasetItems.size, key = { it }) { index ->
                Row(
                    modifier = Modifier.animateItem(
                        fadeInSpec = spring(
                            dampingRatio = Spring.DampingRatioHighBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                ) {
                    ItemCard(dataAdjust = datasetItems[index])
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemCard(
    dataAdjust: DataAdjust,
) {

    val colorResId = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.color
        is DataAdjust.Data -> dataAdjust.dataset.dataType.color
    }

    val labelIcon = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.tagIcon
        is DataAdjust.Data -> dataAdjust.dataset.tagIcon
    }
    val label = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.label
        is DataAdjust.Data -> dataAdjust.dataset.label
    }
    val description = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.description
        is DataAdjust.Data -> dataAdjust.dataset.description
    }
    val dateTime = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.dateTime
        is DataAdjust.Data -> dataAdjust.dataset.dateTime
    }
    val paymentMethod = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.paymentMethod
        is DataAdjust.Data -> dataAdjust.dataset.paymentMethod
    }

    val amount = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.amount
        is DataAdjust.Data -> dataAdjust.dataset.amount
    }

    val dataType = when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.dataType
        else -> null
    }

    val isCompleted = when (dataAdjust) {
        is DataAdjust.Data -> {
            if (dataAdjust.dataset.dataType == DataType.DEBT ||
                dataAdjust.dataset.dataType == DataType.LENT ||
                dataAdjust.dataset.dataType == DataType.GOAL
            )
                dataAdjust.dataset.isAmountEqualToAdjustAmount() else false
        }

        is DataAdjust.Adjust -> false
    }

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
                .fillMaxWidth()
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
                        painter = painterResource(labelIcon.icon),
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

                    when (dataAdjust) {
                        is DataAdjust.Adjust -> {
                            val datasetLabel = dataAdjust.adjustment.dataset!!.label



                            Text(
                                text = datasetLabel,
                                color = color,
                                fontSize = labelFontSize
                            )
                        }

                        else -> {}
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
                    DataType.LENT -> "-$amount"
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
            dataAdjust = dataAdjust,
            onShowDialog = onShowDialog,
        )
    }
}
