// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.isStartDateTimeNotEqualToDeadlineDateTime
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import java.util.UUID

val MODEL_DRAWER_ICON_SIZE = 25.dp
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
    if (isBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.updateOnModelBottomSheetShow(false)
                viewModel.updateIsBottomSheetContentLoading(true)
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            containerColor = BottomSheetDefaults.ContainerColor.copy(0.97f),

            ) {
            LaunchedEffect(viewModel.isBottomSheetContentLoading) {
                delay(800)
                viewModel.updateIsBottomSheetContentLoading(false)
            }

            /*... Text field and button ...*/
            DataAdditionModelDrawerContent(
                datasets = datasets,
                viewModel = viewModel,
                isBottomSheetOpen = isBottomSheetOpen
            )
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
) {

    val showDataTypeBottomSheet = remember { mutableStateOf(false) }
    val clickedDataType = remember { mutableStateOf<DataType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DataType.entries.forEach {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .clickable {
                                showDataTypeBottomSheet.value = true
                                clickedDataType.value = it
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(it.text, fontWeight = FONT_WEIGHT, color = colorResource(it.color))
                            Text(
                                it.typeDescription,
                                fontWeight = FontWeight.Light,
                                color = colorResource(it.color).copy(0.5f)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = it.text,
                            tint = colorResource(it.color)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color.LightGray.copy(0.4f)
                    )
                }
            }
        }
    }

    if (showDataTypeBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showDataTypeBottomSheet.value = false
                clickedDataType.value = null
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
        ) {
            when (clickedDataType.value) {
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

                else -> {}
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
    val amountToDisplay = rememberSaveable { mutableStateOf("") }
    val labelState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val wasRepaySuccess = remember { mutableStateOf(State.INITIAL) }
    val labelIconState = remember { mutableStateOf(TagIcon("description", R.drawable.description)) }
    val selectedDataset = remember { mutableStateOf<Dataset?>(null) }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val adjustAmountState = rememberTextFieldState()
    val lazyState = rememberLazyListState()
    val selectedTab = remember { mutableIntStateOf(0) }




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

    val adjustmentColor = when (dataType) {
        DataType.DEBT -> R.color.RepayDebt
        DataType.LENT -> R.color.RepayLoan
        else -> R.color.Attain
    }
    val noAdjustmentDataType = listOf(
        DataType.EXPENSE,
        DataType.EARNINGS,
        DataType.SAVINGS
    )

    val color = if (selectedTab.intValue == 0) colorResId
    else adjustmentColor


    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val iconImage = painterResource(
                id = if (
                    selectedTab.intValue == 0 || dataType in noAdjustmentDataType
                ) icon
                else when (dataType) {
                    DataType.DEBT -> R.drawable.outline_debt
                    DataType.LENT -> R.drawable.outline_lent
                    else -> R.drawable.outlined_goal
                }
            )

            val description = if (
                selectedTab.intValue == 0 ||
                dataType in noAdjustmentDataType
            ) dataType.typeDescription
            else when (dataType) {
                DataType.GOAL -> AdjustmentType.GOAL_ATTAIN.typeDescription
                DataType.DEBT -> AdjustmentType.DEBT_REPAY.typeDescription
                else -> AdjustmentType.LENT_REPAY.typeDescription
            }


            Icon(
                modifier = Modifier
                    .size(MODEL_DRAWER_ICON_SIZE)
                    .padding(bottom = 5.dp),
                painter = iconImage,
                contentDescription = dataType.text,
                tint = colorResource(color)
            )

            Text(
                description,
                color = colorResource(color),
                fontWeight = FontWeight.Medium
            )
        }

        if (dataType in listOf(DataType.DEBT, DataType.LENT, DataType.GOAL)) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab.intValue,
                modifier = Modifier.padding(vertical = 15.dp),
                containerColor = Color.Transparent,
                contentColor = colorResource(id = adjustmentColor),
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(
                            selectedTab.intValue,
                            matchContentSize = true
                        ),
                        color = colorResource(
                            id = if (selectedTab.intValue == 0) colorResId
                            else adjustmentColor
                        )
                    )

                },
                divider = {
                    HorizontalDivider(
                        color = colorResource(
                            id = if (selectedTab.intValue == 0) colorResId
                            else adjustmentColor
                        ).copy(0.5f)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab.intValue == 0,
                    onClick = { selectedTab.intValue = 0 },
                ) {
                    Text(dataType.text, color = colorResource(colorResId))
                }

                Tab(
                    selected = selectedTab.intValue == 1,
                    onClick = { selectedTab.intValue = 1 },
                ) {
                    Text(
                        when (dataType) {
                            DataType.DEBT -> AdjustmentType.DEBT_REPAY.text
                            DataType.LENT -> AdjustmentType.LENT_REPAY.text
                            else -> AdjustmentType.GOAL_ATTAIN.text
                        }
                    )
                }
            }
        }

        if (selectedTab.intValue == 0 || dataType in noAdjustmentDataType) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                state = lazyState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                // Amount
                item(key = 39) {
                    Row(
                        modifier = Modifier.animateItem()
                    ) {
                        ModelDrawerAmountField(
                            state = amountState,
                            placeholder = "0.0",
                            colorResId = colorResId,
                            wasSuccess = wasSuccess,
                            displayState = amountToDisplay
                        )
                    }
                }

                // Label
                item(key = 71) {
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
                            textLength = MAX_LABEL_LENGTH,
                            displayText = rememberSaveable { mutableStateOf("") }
                        )
                    }
                }

                // Tag
                item(key = 58) {
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

                // Description
                item(key = 45) {
                    Row(
                        modifier = Modifier.animateItem()
                    ) {
                        ModelDrawerTextField(
                            state = descriptionState,
                            placeholder = "Note (Optional)",
                            title = "Note",
                            description = "Take a note for the given amount",
                            colorResId = colorResId,
                            wasSuccess = null,
                            displayText = rememberSaveable { mutableStateOf("") }
                        )
                    }
                }

                // Date time picker
                item(key = 12) {
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

                // Repeatable transaction
                item(key = 120) {
                    if (dataType == DataType.GOAL) {
                        RepeatableTransaction(
                            DataType.GOAL,
                            wasRepaySuccess = wasRepaySuccess,
                        )
                    }
                }

                // Payment method
                item(key = 5) {
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
                item(key = 6) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .animateItem(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ModelDrawerButton(
                            text = buttonText,
                            wasSuccess = wasSuccess,
                            colorResId = colorResId,
                            filledColor = colorResource(colorResId),
                            textColor = Color.White
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
                                        tagIcon = labelIconState.value,
                                        paymentMethod = selectedPaymentMethod.value,
                                        deadlineDateTime = endLocalDateTimeState.value.toFirestoreTimestampUtc(),
                                        status = Status.PENDING
                                    )
                                )
                                wasSuccess.value = State.SUCCESS

                                // Reset all state
                                amountState.clearText()
                                labelState.clearText()
                                descriptionState.clearText()
                                labelIconState.value = TagIcon(
                                    "description",
                                    R.drawable.description
                                )

                                // Dismiss the model drawer.
                                onDismiss()

                            } else {
                                wasSuccess.value = State.ERROR
                            }
                        }
                    }
                }
            }
        }

        if (selectedTab.intValue == 1) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                state = lazyState,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                item(key = 921) {
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
                item(key = 45) {
                    Row(
                        modifier = Modifier.animateItem()
                    ) {
                        ModelDrawerTextField(
                            state = descriptionState,
                            placeholder = "Note (Optional)",
                            title = "Note",
                            description = "Take a note for the given amount",
                            colorResId = adjustmentColor,
                            wasSuccess = null,
                            displayText = rememberSaveable { mutableStateOf("") }
                        )
                    }
                }

                // Date time picker
                item(key = 12) {
                    // Show date time picker.
                    Row(
                        modifier = Modifier.animateItem()
                    ) {

                        DateTimeInput(
                            showTime = showTime,
                            showDate = showDate,
                            localDateTimeState = localDateTimeState,
                            colorResId = adjustmentColor
                        )
                    }
                }

                // Submit buttons
                item(key = 6) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .animateItem(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

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
                                        val adjustment = Adjustment(
                                            adjustmentId = UUID.randomUUID().toString(),
                                            amount = adjustAsDouble,
                                            dateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                                            label = if (dataType == DataType.DEBT)
                                                AdjustmentType.DEBT_REPAY.text
                                            else AdjustmentType.LENT_REPAY.text,
                                            description = descriptionState.text.toString(),
                                            tagIcon = it.tagIcon,
                                            paymentMethod = selectedPaymentMethod.value,
                                            adjustmentType = if (dataType == DataType.DEBT)
                                                AdjustmentType.DEBT_REPAY
                                            else AdjustmentType.LENT_REPAY,
                                        )

                                        adjustment.dataset = it

                                        viewModel.addAdjustmentData(
                                            it,
                                            adjustment
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
                                colorResId = color,
                                filledColor = Color.Transparent
                            ) {
                                selectedDataset.value?.let {
                                    if (adjustAsDouble != null) {

                                        if (
                                            adjustAsDouble > it.remainingAmount &&
                                            !it.isStartDateTimeNotEqualToDeadlineDateTime
                                        ) {
                                            wasRepaySuccess.value = State.ERROR
                                            return@ModelDrawerButton
                                        }
                                        val adjustment = Adjustment(
                                            adjustmentId = UUID.randomUUID().toString(),
                                            amount = adjustAsDouble,
                                            dateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                                            label = AdjustmentType.GOAL_ATTAIN.text,
                                            description = descriptionState.text.toString(),
                                            tagIcon = it.tagIcon,
                                            paymentMethod = selectedPaymentMethod.value,
                                            adjustmentType = AdjustmentType.GOAL_ATTAIN,
                                        )
                                        adjustment.dataset = it

                                        viewModel.addAdjustmentData(
                                            it,
                                            adjustment
                                        )
                                    }
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

