// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentStatus
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.ui.components.Current
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import java.util.UUID

private val MODEL_DRAWER_ICON_SIZE = 25.dp
val FONT_WEIGHT = FontWeight.Bold
val MAX_LABEL_LENGTH = 15


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionModelDrawer(
    datasets: List<Dataset>,
    viewModel: HomeScreenViewModel,
    isBottomSheetOpen: Boolean,
) {
    val dataTypes = DataType.entries.toList()
    val pagerState = rememberPagerState { dataTypes.size }
    val scope = rememberCoroutineScope()


    if (isBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.updateOnModelBottomSheetShow(false)
                viewModel.updateIsBottomSheetContentLoading(true)
            },
            containerColor = BottomSheetDefaults.ContainerColor.copy(0.97f),

        ) {
            LaunchedEffect(viewModel.isBottomSheetContentLoading) {
                delay(800)
                viewModel.updateIsBottomSheetContentLoading(false)
            }
            /*.... Tab buttons ....*/
            DataAdditionModelDrawerTopTitle(
                dataTypes = dataTypes,
                pageState = pagerState,
                scope = scope
            )

            /*.... Horizontal divider .... */
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }


            if (viewModel.isBottomSheetContentLoading) {

                /*... Loading indicator ...*/
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        strokeCap = StrokeCap.Square
                    )
                }

            } else {
                /*... Text field and button ...*/
                DataAdditionModelDrawerContent(
                    dataTypes = dataTypes,
                    pagerState = pagerState,
                    datasets = datasets,
                    viewModel = viewModel,
                    isBottomSheetOpen = isBottomSheetOpen
                )
            }
        }
    }
}

@Composable
fun DataAdditionModelDrawerTopTitle(
    dataTypes: List<DataType>,
    pageState: PagerState,
    scope: CoroutineScope
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dataTypes.forEachIndexed { idx, it ->
            item(key = it) {
                // Color
                val color = colorResource(it.color)

                // Text button for data type
                TextButton(
                    modifier = Modifier.animateItem(),
                    onClick = {
                        scope.launch {
                            pageState.animateScrollToPage(idx)
                        }
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = it.text,
                            color = color,
                            fontWeight = FONT_WEIGHT
                        )
                        if (pageState.currentPage == idx)
                            Current(color)
                    }

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionModelDrawerContent(
    datasets: List<Dataset>,
    viewModel: HomeScreenViewModel,
    isBottomSheetOpen: Boolean,
    pagerState: PagerState,
    dataTypes: List<DataType>
) {
    Column {
        HorizontalPager(
            pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 500.dp),
            key = { it },
            beyondViewportPageCount = 2,
        ) {
            val dataTypeTab = dataTypes[it]

            when (dataTypeTab) {
                DataType.EARNINGS -> {
                    ModelDrawerContent(
                        placeholder = "Earned from",
                        colorResId = R.color.Earnings,
                        icon = R.drawable.filled_earnings,
                        dataType = DataType.EARNINGS,
                        description = "Add your earnings here",
                        buttonText = "Received",
                        datasets = datasets,
                        viewModel = viewModel,
                        isBottomSheetOpen = isBottomSheetOpen,
                    ) {
                        viewModel.updateOnModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.EXPENSE -> {
                    ModelDrawerContent(
                        placeholder = "Spent on",
                        colorResId = R.color.Expense,
                        icon = R.drawable.filled_expenditure,
                        dataType = DataType.EXPENSE,
                        description = "Add your expenses here",
                        buttonText = "Spent",
                        isBottomSheetOpen = isBottomSheetOpen,
                        datasets = datasets,
                        viewModel = viewModel,
                    ) {
                        viewModel.updateOnModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.DEBT -> {
                    ModelDrawerContent(
                        placeholder = "Borrowed from",
                        colorResId = R.color.Debt,
                        icon = R.drawable.filled_debt,
                        dataType = DataType.DEBT,
                        description = "Set your debts here",
                        buttonText = "Set Debt",
                        isBottomSheetOpen = isBottomSheetOpen,
                        datasets = datasets,
                        viewModel = viewModel,
                    ) {
                        viewModel.updateOnModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.LENT -> {
                    ModelDrawerContent(
                        placeholder = "Lent to",
                        colorResId = R.color.Lent,
                        icon = R.drawable.filled_lent,
                        dataType = DataType.LENT,
                        description = "Put your lent here",
                        buttonText = "Lent",
                        isBottomSheetOpen = isBottomSheetOpen,
                        datasets = datasets,
                        viewModel = viewModel,
                    ) {
                        viewModel.updateOnModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.SAVINGS -> {
                    ModelDrawerContent(
                        placeholder = "Savings from",
                        colorResId = R.color.Savings,
                        icon = R.drawable.filled_savings,
                        dataType = DataType.SAVINGS,
                        description = "Add your savings here",
                        buttonText = "Saved",
                        isBottomSheetOpen = isBottomSheetOpen,
                        datasets = datasets,
                        viewModel = viewModel,
                    ) {
                        viewModel.updateOnModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.GOAL -> {
                    ModelDrawerContent(
                        placeholder = "Goal for",
                        colorResId = R.color.Goal,
                        icon = R.drawable.filled_goal,
                        dataType = DataType.GOAL,
                        description = "Set your goal",
                        buttonText = "Start Goal",
                        isBottomSheetOpen = isBottomSheetOpen,
                        datasets = datasets,
                        viewModel = viewModel,
                    ) {
                        viewModel.updateOnModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDrawerContent(
    placeholder: String,
    colorResId: Int,
    icon: Int,
    dataType: DataType,
    description: String,
    buttonText: String,
    datasets: List<Dataset>,
    viewModel: HomeScreenViewModel,
    isBottomSheetOpen: Boolean,
    onDismiss: () -> Unit,
) {

    val showDate = remember { mutableStateOf(false) }
    val showTime = remember { mutableStateOf(false) }
    val localDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val endLocalDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val amountState = rememberTextFieldState()
    val labelState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val wasRepaySuccess = remember { mutableStateOf(State.INITIAL) }
    val labelIconState = remember { mutableStateOf(Pair("description", R.drawable.description)) }
    val selectedDataset = remember { mutableStateOf<Dataset?>(null) }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val adjustAmountState = rememberTextFieldState()


    LaunchedEffect(amountState.text.toString()) {
        if (wasSuccess.value == State.ERROR) {
            wasSuccess.value = State.INITIAL
        }
    }

    LaunchedEffect(labelState.text.toString()) {
        if (wasSuccess.value == State.ERROR) {
            wasSuccess.value = State.INITIAL
        }
    }

    LaunchedEffect(adjustAmountState.text.toString()) {
        if (wasRepaySuccess.value == State.ERROR) {
            wasRepaySuccess.value = State.INITIAL
        }
    }

    val amountAsDouble = amountState.text.toString().toDoubleOrNull()
    val adjustAsDouble = adjustAmountState.text.toString().toDoubleOrNull()


    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = colorResource(id = colorResId)

            Icon(
                modifier = Modifier
                    .size(MODEL_DRAWER_ICON_SIZE)
                    .padding(end = 5.dp),
                painter = painterResource(id = icon),
                contentDescription = dataType.text,
                tint = color
            )

            Text(description, color = color, fontWeight = FONT_WEIGHT)
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Amount
            item {
                Row(
                    modifier = Modifier.animateItem()
                ) {
                    ModelDrawerAmountField(
                        state = amountState,
                        placeholder = "0.0",
                        colorResId = colorResId,
                        wasSuccess = wasSuccess,
                    )
                }
            }

            // Label
            item {
                Row(
                    modifier = Modifier.animateItem()
                ) {
                    ModelDrawerTextField(
                        state = labelState,
                        title = "Label",
                        description = "Add a label for the given amount",
                        placeholder = placeholder,
                        colorResId = colorResId,
                        wasSuccess = wasSuccess,
                        textLength = MAX_LABEL_LENGTH
                    )
                }
            }

            // Tag
            item {
                Row(
                    modifier = Modifier.animateItem()
                ) {
                    ModelDrawerTag(
                        colorResId = colorResId,
                        title = "Tag",
                        iconState = labelIconState
                    )
                }
            }


            // Adjust amount
            item {
                when (dataType) {
                    DataType.LENT -> {
                        Row(
                            modifier = Modifier.animateItem()
                        ) {
                            AdjustmentField(
                                isBottomSheetOpen,
                                datatype = DataType.LENT,
                                amountState = adjustAmountState,
                                datasets = datasets.filter {
                                    it.dataType == DataType.LENT
                                },
                                wasRepaySuccess = wasRepaySuccess,
                                selectedDataset = selectedDataset,
                                colorResId = R.color.RepayLoan
                            )
                        }
                    }

                    DataType.DEBT -> {
                        Row(
                            modifier = Modifier.animateItem()
                        ) {
                            AdjustmentField(
                                isBottomSheetOpen,
                                datatype = DataType.DEBT,
                                amountState = adjustAmountState,
                                datasets = datasets.filter {
                                    it.dataType == DataType.DEBT
                                },
                                wasRepaySuccess = wasRepaySuccess,
                                selectedDataset = selectedDataset,
                                colorResId = R.color.RepayDebt
                            )
                        }
                    }

                    DataType.GOAL -> {
                        Row(
                            modifier = Modifier.animateItem()
                        ) {
                            AdjustmentField(
                                isBottomSheetOpen,
                                datatype = DataType.GOAL,
                                amountState = adjustAmountState,
                                datasets = datasets.filter {
                                    it.dataType == DataType.GOAL
                                },
                                wasRepaySuccess = wasRepaySuccess,
                                selectedDataset = selectedDataset,
                                colorResId = R.color.Attain
                            )
                        }
                    }

                    else -> {}
                }
            }

            // Description
            item {
                Row(
                    modifier = Modifier.animateItem()
                ) {
                    ModelDrawerTextField(
                        state = descriptionState,
                        placeholder = "Note (Optional)",
                        title = "Note",
                        description = "Take a note for the given amount",
                        colorResId = colorResId,
                        wasSuccess = wasSuccess
                    )
                }
            }

            // Date time picker
            item {
                // Show date time picker.
                Row(
                    modifier = Modifier.animateItem()
                ) {
                    if (dataType == DataType.GOAL) {
                        DateTimeRange(
                            startLocalDateTimeState = localDateTimeState,
                            endLocalDateTimeState = endLocalDateTimeState,
                            colorResId = colorResId
                        )
                    } else {
                        DateTimeInput(
                            showTime = showTime,
                            showDate = showDate,
                            localDateTimeState = localDateTimeState,
                            colorResId = colorResId
                        )
                    }
                }
            }

            // Payment method
            item {
                Row(
                    modifier = Modifier.animateItem()
                ) {
                    PaymentMethodDropdown(
                        colorResId = colorResId,
                        selectedPaymentMethod = selectedPaymentMethod,
                    )
                }
            }

            // Submit buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModelDrawerButton(
                        text = buttonText,
                        wasSuccess = wasSuccess,
                        colorResId = colorResId,
                        filledColor = Color.Transparent
                    ) {
                        if (amountAsDouble != null && labelState.text.toString().isNotEmpty()) {
                            viewModel.addData(
                                Dataset(
                                    id = UUID.randomUUID().toString(),
                                    dataType = dataType,
                                    amount = amountAsDouble,
                                    label = labelState.text.toString(),
                                    description = descriptionState.text.toString(),
                                    dateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                                    labelIcon = labelIconState.value.second,
                                    paymentMethod = selectedPaymentMethod.value,
                                    deadlineDateTime = endLocalDateTimeState.value.toFirestoreTimestampUtc(),
                                    adjustmentStatus = AdjustmentStatus.PENDING
                                )
                            )
                            wasSuccess.value = State.SUCCESS

                            // Reset all state
                            amountState.clearText()
                            labelState.clearText()
                            descriptionState.clearText()
                            labelIconState.value = Pair(
                                "description",
                                R.drawable.description
                            )

                            // Dismiss the model drawer.
                            onDismiss()

                        } else {
                            wasSuccess.value = State.ERROR
                        }
                    }
                    Spacer(modifier = Modifier.width(5.dp))

                    if (dataType == DataType.DEBT || dataType == DataType.LENT) {
                        ModelDrawerButton(
                            text = "Repay",
                            wasSuccess = wasRepaySuccess,
                            colorResId = if (dataType == DataType.DEBT) R.color.RepayDebt
                            else R.color.RepayLoan,
                            filledColor = Color.Transparent
                        ) {
                            if (adjustAsDouble != null) {
                                selectedDataset.value?.let {
                                    if (adjustAsDouble > it.remainingAmount) {
                                        wasRepaySuccess.value = State.ERROR
                                        return@ModelDrawerButton
                                    }
                                    viewModel.addRepayData(
                                        it,
                                        Adjustment(
                                            adjustmentId = UUID.randomUUID().toString(),
                                            amount = adjustAsDouble,
                                            dateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                                            label = "${it.dataType.text} Repaid: ${it.label}",
                                            description = descriptionState.text.toString(),
                                            adjustmentIcon = it.labelIcon,
                                            paymentMethod = selectedPaymentMethod.value,
                                            adjustmentType = AdjustmentType.REPAYMENT,
                                        )
                                    )
                                } ?: run {
                                    wasRepaySuccess.value = State.ERROR
                                }

                                adjustAmountState.clearText()
                                selectedDataset.value = null

                                // Dismiss the model drawer.
                                onDismiss()
                            }
                        }
                    }

                    if (dataType == DataType.GOAL) {
                        ModelDrawerButton(
                            text = "Add",
                            wasSuccess = wasRepaySuccess,
                            colorResId = R.color.Attain,
                            filledColor = Color.Transparent
                        ) {
                            if (adjustAsDouble != null) {
                                selectedDataset.value?.let {
                                    if (adjustAsDouble > it.remainingAmount) {
                                        wasRepaySuccess.value = State.ERROR
                                        return@ModelDrawerButton
                                    }
                                    viewModel.addRepayData(
                                        it,
                                        Adjustment(
                                            adjustmentId = UUID.randomUUID().toString(),
                                            amount = adjustAsDouble,
                                            dateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                                            label = "Attained: ${it.label}",
                                            description = descriptionState.text.toString(),
                                            adjustmentIcon = it.labelIcon,
                                            paymentMethod = selectedPaymentMethod.value,
                                            adjustmentType = AdjustmentType.ATTAIN,
                                        )
                                    )
                                } ?: run {
                                    wasRepaySuccess.value = State.ERROR
                                }

                                adjustAmountState.clearText()
                                selectedDataset.value = null

                                // Dismiss the model drawer.
                                onDismiss()
                            }
                        }
                    }
                }
            }
        }
    }
}

