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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AlignVerticalBottom
import androidx.compose.material.icons.outlined.AlignVerticalTop
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.ui.components.StatusView
import com.example.moneytracker.ui.homeScreen.DataViewModel
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.theme.MoneyTrackerTheme

private val spacerWith = 14.dp
private val labelFontSize = 13.sp
private val AMOUNT_SIZE = 20.sp
private val FilterIconSize = 18.dp
private val OrganiseIconSize = 23.dp

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
                        imageVector = if (onFilterClick.value)
                            Icons.Outlined.AlignVerticalTop else
                            Icons.Outlined.AlignVerticalBottom,
                        contentDescription = "filter",
                        modifier = Modifier.size(17.dp)
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
                        tint = if (categorySorting.value != "Initial") MoneyTrackerTheme.colors.autoText
                        else Color.Gray
                    )
                }

                IconButton(
                    onClick = {
                        isTimeModelBottomOpen.value = true
                    }
                ) {
                    val color =
                        if (timeSorting.value != SortType.Initial) MoneyTrackerTheme.colors.autoText
                    else Color.Gray
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
                    ) MoneyTrackerTheme.colors.autoText else Color.Gray


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
                        tint = if (alphabeticalOrder.value != SortType.Initial)
                            MoneyTrackerTheme.colors.autoText else Color.Gray
                    )
                }
            }
        }

        if (isTimeModelBottomOpen.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    isTimeModelBottomOpen.value = false
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                                .size(OrganiseIconSize)
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
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                                .size(OrganiseIconSize)
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
                                .copy(contentColor = MoneyTrackerTheme.colors.autoText)
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
                                    contentColor = MoneyTrackerTheme.colors.autoText,
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
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                                .size(OrganiseIconSize)
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
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                                .size(OrganiseIconSize)
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
                        val selectedColor = MoneyTrackerTheme.colors.autoText
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
                                .copy(contentColor = MoneyTrackerTheme.colors.autoText)
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
                                    contentColor = MoneyTrackerTheme.colors.autoText,
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
    viewModel: DataViewModel,
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
        null
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
                    ItemCard(
                        modifier = Modifier.animateItem(),
                        dataAdjust = datasetItems[index]
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    dataAdjust: DataAdjust,
) {

    val colorResId = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.adjustmentType.color
        is DataAdjust.Data -> dataAdjust.dataset.dataType.color
    }

    val labelIcon = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.tagIcon.icon
        is DataAdjust.Data -> dataAdjust.dataset.tagIcon.icon
    }.let {
        painterResource(id = it)
    }

    val label = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.label
        is DataAdjust.Data -> dataAdjust.dataset.label
    }

    val description = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.description
        is DataAdjust.Data -> dataAdjust.dataset.description
    }.let {
        if (it.length > 20) it.take(20) + "..." else it
    }

    val dateTime = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.dateTime
        is DataAdjust.Data -> dataAdjust.dataset.dateTime
    }.formatToDateTime

    val paymentMethod = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.paymentMethod.icon
        is DataAdjust.Data -> dataAdjust.dataset.paymentMethod.icon
    }.let {
        painterResource(id = it)
    }

    val amount = when (dataAdjust) {
        is DataAdjust.Adjust -> dataAdjust.adjustment.amount
        is DataAdjust.Data -> dataAdjust.dataset.amount
    }.formatToAmount()

    when (dataAdjust) {
        is DataAdjust.Data -> dataAdjust.dataset.dataType
        else -> null
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

    val adjustTextDecoration = if (
        isAmountEqualWithAdjustAmount == true
    ) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val labelTextDecoration = if (
        isAmountEqualWithAdjustAmount == true && dataAdjust !is DataAdjust.Adjust
    ) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val color = colorResource(colorResId)
    val onShowDialog = remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListItem(
            modifier = Modifier
                .clickable {
                    onShowDialog.value = true
                },
            headlineContent = {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = label,
                        fontSize = labelFontSize,
                        fontWeight = FontWeight.Bold,
                        textDecoration = labelTextDecoration
                    )

                    adjustment?.let {
                        Text(
                            it,
                            fontSize = labelFontSize,
                            fontWeight = FontWeight.Bold,
                            textDecoration = adjustTextDecoration
                        )
                    }

                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            fontSize = labelFontSize,
                        )
                    }
                }
            },

            supportingContent = {
                Text(
                    text = dateTime,
                    fontSize = labelFontSize,
                )

            },

            leadingContent = {
                Image(
                    painter = labelIcon,
                    contentDescription = "TagIcon",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },

            trailingContent = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = amount,
                        fontSize = AMOUNT_SIZE,
                        color = color
                    )

                    Image(
                        painter = paymentMethod,
                        contentDescription = "PaymentMethod",
                        modifier = Modifier.size(ICON_SIZE)
                    )

                    StatusView(dataAdjust = dataAdjust)
                }
            }
        )
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.fillMaxSize(0.8f),
            color = color
        )
    }

    if (onShowDialog.value) {
        Receipt(
            dataAdjust = dataAdjust,
            onShowDialog = onShowDialog,
        )
    }
}
