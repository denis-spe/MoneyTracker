// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.types.SettlementType
import com.example.moneytracker.helper.addNegativeToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.ui.OnDeleteReceipt
import com.example.moneytracker.ui.OnUpdate
import com.example.moneytracker.ui.Receipt
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.StatusView
import com.example.moneytracker.ui.components.Swipe
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE
import com.example.moneytracker.ui.theme.StewardTheme

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
    uiState: HomeUiState,
    onFilterClick: (Boolean) -> Unit,
    onActivateShow: (Boolean) -> Unit,
    categorySorting: (String) -> Unit,
    timeSorting: (SortType) -> Unit,
    amountSorting: (SortType) -> Unit,
    paymentSorting: (PaymentMethod?) -> Unit,
    alphabeticalOrder: (SortType) -> Unit
) {

    val isCategoryModelBottomOpen = remember { mutableStateOf(false) }
    val isTimeModelBottomOpen = remember { mutableStateOf(false) }
    val isPaymentModelBottomOpen = remember { mutableStateOf(false) }
    val isAlphabeticalOrderModelBottomOpen = remember { mutableStateOf(false) }

    val categoryState = remember { mutableStateOf("") }
    val paymentState = remember { mutableStateOf<PaymentMethod?>(null) }
    val amountState = remember { mutableStateOf(SortType.Initial) }

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
                        onActivateShow(!uiState.onActivateShow)
                    }
                ) {
                    Icon(
                        imageVector = if (uiState.onActivateShow)
                            Icons.Default.KeyboardArrowUp else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = "arrow",
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {
                        onFilterClick(!uiState.onFilterClick)
                    }
                ) {
                    Icon(
                        imageVector = if (uiState.onFilterClick)
                            Icons.Outlined.AlignVerticalTop else
                            Icons.Outlined.AlignVerticalBottom,
                        contentDescription = "filter",
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

        }

        AnimatedVisibility(
            visible = uiState.onFilterClick,
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
                        imageVector = if (uiState.categorySorting != "Initial") Icons.Default.Category
                        else Icons.Outlined.Category,
                        contentDescription = "Category",
                        modifier = Modifier.size(FilterIconSize),
                        tint = if (uiState.categorySorting != "Initial") StewardTheme.colors.onSurfaceText
                        else Color.Gray
                    )
                }

                IconButton(
                    onClick = {
                        isTimeModelBottomOpen.value = true
                    }
                ) {
                    val color =
                        if (uiState.timeSorting != SortType.Initial) StewardTheme.colors.onSurfaceText
                        else Color.Gray
                    val imageVec = if (
                        uiState.timeSorting == SortType.Ascending ||
                        uiState.timeSorting == SortType.Descending
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
                    val icon = if (uiState.paymentSorting != null
                        || uiState.amountSorting != SortType.Initial
                    ) Icons.Default.Payments
                    else Icons.Outlined.Payments
                    val color = if (uiState.paymentSorting != null
                        || uiState.amountSorting != SortType.Initial
                    ) StewardTheme.colors.onSurfaceText else Color.Gray


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
                    val iconResId = when (uiState.alphabeticalOrder) {
                        SortType.Descending -> R.drawable.sort_up
                        SortType.Ascending -> R.drawable.sort_down
                        else -> R.drawable.sort
                    }

                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = "Sort",
                        modifier = Modifier.size(FilterIconSize),
                        tint = if (uiState.alphabeticalOrder != SortType.Initial)
                            StewardTheme.colors.onSurfaceText else Color.Gray
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
                            timeSorting(SortType.Ascending)
                            isTimeModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(
                                contentColor =
                                    if (uiState.timeSorting == SortType.Ascending) Color.Green
                                    else Color.Gray
                            )
                    ) {
                        Text("Ascending")
                    }
                    TextButton(
                        onClick = {
                            timeSorting(SortType.Descending)
                            isTimeModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(
                                contentColor =
                                    if (uiState.timeSorting == SortType.Descending) Color.Red
                                    else Color.Gray
                            )
                    ) {
                        Text("Descending")
                    }

                    TextButton(
                        onClick = {
                            timeSorting(SortType.Initial)
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
            category = category + SettlementType.entries.map {
                if (it == SettlementType.INITIAL) {
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
                                        categorySorting("Initial")
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
                                categorySorting("Initial")
                                categoryState.value = ""
                                isCategoryModelBottomOpen.value = false
                            },
                            colors = ButtonDefaults.textButtonColors()
                                .copy(contentColor = StewardTheme.colors.onSurfaceText)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                categorySorting(categoryState.value)
                                isCategoryModelBottomOpen.value = false
                            },
                            colors = ButtonDefaults.buttonColors()
                                .copy(
                                    contentColor = StewardTheme.colors.onSurfaceText,
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
                            alphabeticalOrder(SortType.Ascending)
                            isAlphabeticalOrderModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(
                                contentColor =
                                    if (uiState.alphabeticalOrder == SortType.Ascending) Color.Green
                                    else Color.Gray
                            )
                    ) {
                        Text("Ascending")
                    }
                    TextButton(
                        onClick = {
                            alphabeticalOrder(SortType.Descending)
                            isAlphabeticalOrderModelBottomOpen.value = false
                        },
                        colors = ButtonDefaults.textButtonColors()
                            .copy(
                                contentColor =
                                    if (uiState.alphabeticalOrder == SortType.Descending) Color.Red
                                    else Color.Gray
                            )
                    ) {
                        Text("Descending")
                    }

                    TextButton(
                        onClick = {
                            alphabeticalOrder(SortType.Initial)
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
                        val selectedColor = StewardTheme.colors.onSurfaceText
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
                                paymentSorting(null)
                                paymentState.value = null
                                amountState.value = SortType.Initial
                                amountSorting(SortType.Initial)
                                isPaymentModelBottomOpen.value = false
                            },
                            colors = ButtonDefaults.textButtonColors()
                                .copy(contentColor = StewardTheme.colors.onSurfaceText)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                paymentSorting(paymentState.value)
                                amountSorting(amountState.value)
                                isPaymentModelBottomOpen.value = false
                            },
                            colors = ButtonDefaults.buttonColors()
                                .copy(
                                    contentColor = StewardTheme.colors.onSurfaceText,
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


@Composable
fun ItemListArea(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    datasetWithAdjust: DataState<List<DataSettlement>>,
    viewModel: HomeViewModel,
    userViewModel: UserViewModel
) {

    LazyColumn(
        modifier = modifier
    ) {
        item {
            ItemListAreaSort(
                uiState = uiState,
                onFilterClick = viewModel::updateOnFilterClick,
                onActivateShow = viewModel::updateOnActivateShow,
                categorySorting = viewModel::updateCategorySorting,
                timeSorting = viewModel::updateTimeSorting,
                amountSorting = viewModel::updateAmountSorting,
                paymentSorting = viewModel::updatePaymentSorting,
                alphabeticalOrder = viewModel::updateAlphabeticalOrder
            )
        }


        when (datasetWithAdjust) {
            is DataState.Loading -> {
                items(5) {
                    ItemCardShimmer()
                }
                item {
                    Spacer(modifier = Modifier.padding(bottom = 10.dp))
                }
            }

            is DataState.Success -> {
                val data = datasetWithAdjust.data

                items(data.size) { index ->
                    ItemCard(
                        modifier = Modifier,
                        dataSettlement = data[index],
                        viewModel = viewModel,
                        userViewModel = userViewModel
                    )
                }

                data.ifEmpty {
                    item {
                        Column(
                            modifier = Modifier
                                .fillParentMaxHeight(0.7f)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.empty_list),
                                contentDescription = "empty list",
                                modifier = Modifier.size(60.dp)
                            )
                            Text(
                                buildString {
                                    append("No activity recorded\n")
                                    append("for today")
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            is DataState.Error -> {
                item {
                    Text(
                        text = "Error: ${datasetWithAdjust.exception.message}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCard(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    dataSettlement: DataSettlement,
) {

    val colorResId = dataSettlement.colorRes

    val labelIcon = dataSettlement.tagIcon.let {
        painterResource(id = it.icon)
    }

    val categoryIcon = painterResource(id = dataSettlement.icon)

    val label = dataSettlement.label

    val description = dataSettlement.description.let {
        if (it.length > 20) it.take(20) + "..." else it
    }

    val dateTime = dataSettlement.createdAt.formatToDateTime

    val paymentMethod = painterResource(id = dataSettlement.paymentMethod.icon)

    val amount = dataSettlement.addNegativeToAmount

    val settlement = when (dataSettlement) {
        is DataSettlement.SettlementAdjust -> dataSettlement.settlement.financeEntity?.label
        is DataSettlement.SettlementWithdrawal -> dataSettlement.withdrawal.financeEntity?.label
        else -> null
    }

    val isAmountEqualWithAdjustAmount = dataSettlement.isAmountEqualWithAdjustAmount

    val adjustTextDecoration = if (
        isAmountEqualWithAdjustAmount == true
    ) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val labelTextDecoration = if (
        isAmountEqualWithAdjustAmount == true &&
        dataSettlement !is DataSettlement.SettlementAdjust &&
        dataSettlement !is DataSettlement.SettlementWithdrawal
    ) {
        TextDecoration.LineThrough
    } else {
        TextDecoration.None
    }

    val color = colorResource(colorResId)
    val onShowDialog = remember { mutableStateOf(false) }
    val onShowDeleteDialog = remember { mutableStateOf(false) }
    val isUpdateModelBottonOpen = remember { mutableStateOf(false) }

    Swipe(
        onStartToEnd = {
            isUpdateModelBottonOpen.value = true
        },
        onEndToStart = {
            onShowDeleteDialog.value = true
        }
    ) {
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

                        settlement?.let {
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
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(color),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = labelIcon,
                                contentDescription = "TagIcon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp)
                            )
                        }

                        Image(
                            painter = categoryIcon,
                            contentDescription = "CategoryIcon",
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                        )
                    }
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

                        StatusView(dataSettlement = dataSettlement)
                    }
                }
            )
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.fillMaxSize(0.8f),
                color = color
            )
        }
    }

    if (onShowDialog.value) {
        Receipt(
            dataSettlement = dataSettlement,
            onShowDialog = onShowDialog,
        )
    }

    OnDeleteReceipt(
        dataSettlement = dataSettlement,
        onShowDeleteDialog = onShowDeleteDialog,
    ) {
        when (dataSettlement) {
            is DataSettlement.SettlementData -> {
                viewModel.removeData(dataSettlement.financeEntity)
                userViewModel.showActionNotification("Data deleted successfully", Color.Red)
            }

            is DataSettlement.SettlementAdjust -> {
                viewModel.removeSettlementFinance(
                    dataSettlement.settlement.datasetId,
                    dataSettlement.financeEntityType,
                    dataSettlement.settlement
                )
                userViewModel.showActionNotification("Settlement deleted successfully", Color.Red)
            }

            is DataSettlement.SettlementWithdrawal -> {
                viewModel.removeWithdrawalFinance(
                    dataSettlement.withdrawal.datasetId,
                    dataSettlement.financeEntityType,
                    dataSettlement.withdrawal
                )
                userViewModel.showActionNotification("Withdrawal deleted successfully", Color.Red)
            }
        }
        onShowDialog.value = false
    }

    OnUpdate(
        dataSettlement = dataSettlement,
        viewModel = viewModel,
        userViewModel = userViewModel,
        isUpdateModelBottonOpen = isUpdateModelBottonOpen,
        onShowDialog = onShowDialog
    )
}

@Composable
fun ItemCardShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Icon
        Box(
            modifier = Modifier
                .shimmerEffect(shape = CircleShape, size = 40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Content Area
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .shimmerEffect(
                        shape = RoundedCornerShape(10.dp),
                        width = 150.dp,
                        height = 18.dp
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = RoundedCornerShape(4.dp), width = 100.dp, height = 14.dp)
            )
        }

        // Trailing Content
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = RoundedCornerShape(4.dp), width = 70.dp, height = 18.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = CircleShape, size = 20.dp)
            )
        }
    }
}
