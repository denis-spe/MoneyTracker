// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.SettlementType
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.backend.storage.types.FinanceCategory
import com.example.moneytracker.backend.storage.types.GoalType
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.GoalWarning
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.isStartDateTimeNotEqualToDeadlineDateTime
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.toEpochMilli
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.helper.toMidnight
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import java.util.UUID

val MODEL_DRAWER_ICON_SIZE = 25.dp
val FONT_WEIGHT = FontWeight.Bold
const val MAX_LABEL_LENGTH = 15

val lazyListRoundedCornerShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionModelDrawer(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    financeEntityList: List<FinanceEntity>,
    uiState: HomeUiState,
) {

    if (uiState.isDatasetBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.updateOnDatasetModelBottomSheetShow(false)
                viewModel.updateIsBottomSheetContentLoading(true)
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            containerColor = BottomSheetDefaults.ContainerColor.copy(0.97f),

            ) {

            // Combine enums and Goal for selection
            val entries = TransactionType.entries + LiabilityType.entries + listOf(GoalType)
            DataAdditionModelDrawerContent(
                viewModel = viewModel,
                userViewModel = userViewModel,
                entries = entries,
                financeEntityList = financeEntityList
            )
        }
    }

    if (uiState.isSettlementBottomSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.updateOnAdjustModelBottomSheetShow(false)
                viewModel.updateIsBottomSheetContentLoading(true)
            },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            containerColor = BottomSheetDefaults.ContainerColor.copy(0.97f),

            ) {

            /*... Text field and button ...*/
            DataAdditionModelDrawerContent(
                viewModel = viewModel,
                userViewModel = userViewModel,
                entries = SettlementType.entries.filter { it != SettlementType.INITIAL },
                financeEntityList = financeEntityList
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DataAdditionModelDrawerContent(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    financeEntityList: List<FinanceEntity>,
    entries: List<T>,
) {

    val showDataTypeBottomSheet = remember { mutableStateOf(false) }
    val clickedDataType = remember { mutableStateOf<T?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
    ) {
        items(items = entries) {
            val text = when (it) {
                is FinanceCategory -> it.text
                is SettlementType -> it.text
                else -> ""
            }

            val typeDescription = when (it) {
                is FinanceCategory -> it.typeDescription
                is SettlementType -> it.typeDescription
                else -> ""
            }

            val color = when (it) {
                is FinanceCategory -> it.color
                is SettlementType -> it.color
                else -> R.color.error_color
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showDataTypeBottomSheet.value = true
                        clickedDataType.value = it
                    }
            ) {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 1.dp),
                    headlineContent = {
                        Text(
                            text,
                            fontWeight = FONT_WEIGHT,
                            color = colorResource(color)
                        )
                    },
                    supportingContent = {
                        Text(
                            typeDescription,
                            fontWeight = FontWeight.Light,
                            color = colorResource(color).copy(0.5f)
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = text,
                            tint = colorResource(color)
                        )
                    }
                )

                HorizontalDivider(
                    color = Color.LightGray.copy(0.4f)
                )
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

            when (val clicked = clickedDataType.value) {
                is TransactionType -> {
                    val placeholder = when (clicked) {
                        TransactionType.EARNINGS -> "Earned from"
                        TransactionType.SAVINGS -> "Savings from"
                        TransactionType.EXPENSES -> "Spent on"
                    }
                    val buttonText = when (clicked) {
                        TransactionType.EARNINGS -> "Received"
                        TransactionType.SAVINGS -> "Saved"
                        TransactionType.EXPENSES -> "Spent"
                    }
                    FinancialDataInput(
                        placeholder = placeholder,
                        type = clicked,
                        buttonText = buttonText,
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                is LiabilityType -> {
                    val placeholder = when (clicked) {
                        LiabilityType.DEBT -> "Borrowed from"
                        LiabilityType.LOAN -> "Lent to"
                    }
                    val buttonText = when (clicked) {
                        LiabilityType.DEBT -> "Set Debt"
                        LiabilityType.LOAN -> "Lent"
                    }
                    FinancialDataInput(
                        placeholder = placeholder,
                        type = clicked,
                        buttonText = buttonText,
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                is GoalType -> {
                    GoalDataInput(
                        placeholder = "Goal for",
                        buttonText = "Start Goal",
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                SettlementType.LENT_REPAY -> {
                    SettlementDataInputs(
                        LiabilityType.LOAN,
                        SettlementType.LENT_REPAY,
                        financeEntityList = financeEntityList,
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnAdjustModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                SettlementType.GOAL_ATTAIN -> {
                    SettlementDataInputs(
                        GoalType,
                        SettlementType.GOAL_ATTAIN,
                        financeEntityList = financeEntityList,
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnAdjustModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                SettlementType.DEBT_REPAY -> {
                    SettlementDataInputs(
                        LiabilityType.DEBT,
                        SettlementType.DEBT_REPAY,
                        financeEntityList = financeEntityList,
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnAdjustModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                else -> {}
            }
        }
    }
}

/*
 * Add data Input
 */
@Composable
fun FinancialDataInput(
    placeholder: String,
    type: FinanceCategory,
    buttonText: String,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
) {
    val text = type.text
    val colorResId = type.color
    val filledIcon = type.filledIcon
    val typeDescription = type.typeDescription

    val showDate = remember { mutableStateOf(false) }
    val showTime = remember { mutableStateOf(false) }
    val creationDateTime = remember { mutableStateOf(LocalDateTime.now()) }
    val amountState = rememberTextFieldState()
    val amountToDisplay = rememberSaveable { mutableStateOf("") }
    val labelState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val tag = TagIcon(
        text.lowercase(),
        icon = type.tagIconRes
    )
    val labelIconState = remember {
        mutableStateOf(tag)
    }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val lazyState = rememberLazyListState()
    val amountAsDouble = amountState.text.toString().toDoubleOrNull()
    val viewModel: HomeViewModel = hiltViewModel()
    val iconImage = painterResource(filledIcon)
    val description = typeDescription
    val color = (if (wasSuccess.value == State.ERROR) colorResource(R.color.error_color)
    else colorResource(colorResId))



    LaunchedEffect(amountState.text) {
        snapshotFlow { amountState.text }.collect {
            if (wasSuccess.value == State.ERROR) {
                wasSuccess.value = State.INITIAL
            }
        }
    }

    LaunchedEffect(labelState.text) {
        snapshotFlow { labelState.text }.collect {
            if (wasSuccess.value == State.ERROR) {
                wasSuccess.value = State.INITIAL
            }
        }
    }

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
            Icon(
                modifier = Modifier
                    .size(MODEL_DRAWER_ICON_SIZE)
                    .padding(bottom = 5.dp),
                painter = iconImage,
                contentDescription = text,
                tint = color
            )

            Text(
                description,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            state = lazyState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Amount
            item(key = 39) {
                Row(
                    modifier = Modifier
                        .animateItem()
                ) {
                    ModelDrawerAmountField(
                        state = amountState,
                        placeholder = "0",
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
                DateTimeInput(
                    showTime = showTime,
                    showDate = showDate,
                    localDateTimeState = creationDateTime,
                    colorResId = colorResId
                )
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
                        val label = labelState.text.toString()

                        if (
                            amountAsDouble != null
                            && label.isNotEmpty()
                        ) {
                            val financeEntity = when (type) {
                                is TransactionType -> FinanceEntity.Transaction(
                                    id = UUID.randomUUID().toString(),
                                    transactionType = type,
                                    amount = amountAsDouble,
                                    label = label,
                                    description = descriptionState.text.toString(),
                                    createdAt = creationDateTime.value.toFirestoreTimestampUtc(),
                                    tagIcon = labelIconState.value,
                                    paymentMethod = selectedPaymentMethod.value
                                )

                                is LiabilityType -> FinanceEntity.Liability(
                                    id = UUID.randomUUID().toString(),
                                    liabilityType = type,
                                    amount = amountAsDouble,
                                    label = label,
                                    description = descriptionState.text.toString(),
                                    createdAt = creationDateTime.value.toFirestoreTimestampUtc(),
                                    tagIcon = labelIconState.value,
                                    paymentMethod = selectedPaymentMethod.value
                                )

                                else -> FinanceEntity.Transaction(
                                    id = UUID.randomUUID().toString(),
                                    amount = amountAsDouble,
                                    label = label,
                                    description = descriptionState.text.toString(),
                                    createdAt = creationDateTime.value.toFirestoreTimestampUtc(),
                                    tagIcon = labelIconState.value,
                                    paymentMethod = selectedPaymentMethod.value
                                )
                            }
                            viewModel.addData(financeEntity)
                            wasSuccess.value = State.SUCCESS

                            // Reset all state
                            amountState.clearText()
                            labelState.clearText()
                            descriptionState.clearText()
                            labelIconState.value = tag
                            // Dismiss the model drawer.
                            onDismiss()

                            // Show snackbar
                            userViewModel.launchSnackBarHostState(
                                "$label was added successfully",
                            )
                        } else {
                            var errorMessage: String? = null
                            if (amountAsDouble == null) {
                                errorMessage = "Amount cannot be empty"
                            }
                            if (labelState.text.toString().isEmpty()) {
                                errorMessage = "Label cannot be empty"
                            }
                            if (amountAsDouble == null && labelState.text.toString().isEmpty()) {
                                errorMessage = "Amount and label cannot be empty"
                            }
                            if (!errorMessage.isNullOrEmpty()) {
                                wasSuccess.value = State.ERROR

                                // Show snackbar
                                userViewModel.showActionNotification(
                                    errorMessage,
                                    color = Color.Red.copy(0.5f)
                                )
                                return@ModelDrawerButton
                            }
                            wasSuccess.value = State.ERROR
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalDataInput(
    placeholder: String,
    buttonText: String,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
) {
    val colorResId = GoalType.color
    val localDateTimeState = remember { mutableStateOf(LocalDateTime.now().toMidnight()) }
    val endLocalDateTimeState = remember { mutableStateOf(LocalDateTime.now().toMidnight()) }
    val amountState = rememberTextFieldState()
    val amountToDisplay = rememberSaveable { mutableStateOf("") }
    val labelState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val tagIcon = TagIcon("goal", GoalType.tagIconRes)
    val labelIconState = remember {
        mutableStateOf(
            tagIcon
        )
    }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val lazyState = rememberLazyListState()
    val amountAsDouble = amountState.text.toString().toDoubleOrNull()
    val goalDateTimeWarningState = remember { mutableStateOf(GoalWarning.INITIAL) }
    val viewModel: HomeViewModel = hiltViewModel()
    val routineData = remember { mutableStateOf(RoutineData()) }
    val iconImage = painterResource(GoalType.filledIcon)
    val color = colorResource(GoalType.color)
    val description = GoalType.typeDescription

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
            Icon(
                modifier = Modifier
                    .size(MODEL_DRAWER_ICON_SIZE)
                    .padding(bottom = 5.dp),
                painter = iconImage,
                contentDescription = GoalType.text,
                tint = color
            )

            Text(
                description,
                color = color,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

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

            // Date time range
            item(key = 12) {
                DateTimeRange(
                    startLocalDateTimeState = localDateTimeState,
                    endLocalDateTimeState = endLocalDateTimeState,
                    colorResId = colorResId,
                    goalDateTimeWarningState = goalDateTimeWarningState
                )
            }

            // Repeatable transaction
            item(key = 120) {
                RepeatableTransaction(
                    routineData,
                    dataType = DataType.GOAL,
                    startLocalDateTimeState = localDateTimeState,
                    endLocalDateTimeState = endLocalDateTimeState,
                    goalDateTimeWarningState = goalDateTimeWarningState,
                )
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
                        if (
                            amountState.text.toString().isEmpty() ||
                            labelState.text.toString().isEmpty()
                        ) {
                            wasSuccess.value = State.ERROR
                            return@ModelDrawerButton
                        }

                        if (
                            goalDateTimeWarningState.value == GoalWarning.INITIAL
                        ) {
                            goalDateTimeWarningState.value = GoalWarning.ERROR
                            return@ModelDrawerButton
                        }

                        if (
                            goalDateTimeWarningState.value != GoalWarning.ERROR &&
                            wasSuccess.value != State.ERROR && amountAsDouble != null &&
                            endLocalDateTimeState.value > localDateTimeState.value
                        ) {
                            val isLongRoutine = routineData.value.routine in listOf<Routine>(
                                Routine.EveryDay,
                                Routine.Weekly,
                                Routine.Monthly,
                                Routine.Yearly,
                                Routine.SpecifyDayOfTheWeek
                            )
                            val normalizedStart =
                                if (isLongRoutine) localDateTimeState.value.toMidnight()
                                else localDateTimeState.value

                            val normalizedEnd =
                                if (isLongRoutine) endLocalDateTimeState.value.toMidnight()
                                else endLocalDateTimeState.value

                            val routine = routineData.value.copy(
                                startDateTime = normalizedStart.toFirestoreTimestampUtc(),
                                deadlineDateTime = normalizedEnd.toFirestoreTimestampUtc(),
                                triggerMillis = normalizedEnd.toFirestoreTimestampUtc()
                                    .toEpochMilli()
                            )

                            val financeEntity = FinanceEntity.Goal(
                                id = UUID.randomUUID().toString(),
                                amount = amountAsDouble,
                                label = labelState.text.toString(),
                                description = descriptionState.text.toString(),
                                createdAt = normalizedStart.toFirestoreTimestampUtc(),
                                tagIcon = labelIconState.value,
                                paymentMethod = selectedPaymentMethod.value,
                                routine = routine
                            )

                            viewModel.addData(financeEntity)
                            viewModel.beginTheWork(financeEntity)

                            wasSuccess.value = State.SUCCESS

                            // Reset all state
                            amountState.clearText()
                            labelState.clearText()
                            descriptionState.clearText()
                            labelIconState.value = tagIcon
                            goalDateTimeWarningState.value = GoalWarning.INITIAL

                            // Dismiss the model drawer.
                            onDismiss()

                        } else {
                            var errorMessage: String? = null
                            if (amountAsDouble == null) {
                                errorMessage = "Amount cannot be empty"
                            }
                            if (labelState.text.toString().isEmpty()) {
                                errorMessage = "Label cannot be empty"
                            }
                            if (amountAsDouble == null && labelState.text.toString().isEmpty()) {
                                errorMessage = "Amount and label cannot be empty"
                            }
                            if (!errorMessage.isNullOrEmpty()) {
                                wasSuccess.value = State.ERROR

                                // Show snackbar
                                userViewModel.showActionNotification(
                                    errorMessage,
                                    color = color
                                )
                                return@ModelDrawerButton
                            }

                            wasSuccess.value = State.ERROR
                            goalDateTimeWarningState.value = GoalWarning.ERROR
                        }
                    }
                }
            }
        }
    }
}


/*
 * Settlement Inputs
 */

@Composable
fun SettlementDataInputs(
    type: Any, // FinanceCategory or SettlementType
    settlementType: SettlementType,
    userViewModel: UserViewModel,
    financeEntityList: List<FinanceEntity>,
    onDismiss: () -> Unit
) {
    val lazyState = rememberLazyListState()
    val showDate = remember { mutableStateOf(false) }
    val showTime = remember { mutableStateOf(false) }
    val localDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val selectedFinanceEntity = remember { mutableStateOf<FinanceEntity?>(null) }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val adjustAmountState = rememberTextFieldState()
    val settleAsDouble = adjustAmountState.text.toString().toDoubleOrNull()
    val isBottomSheetOpen by remember { mutableStateOf(true) }
    val viewModel = hiltViewModel<HomeViewModel>()
    val iconImage = painterResource(settlementType.icon)
    val color = colorResource(settlementType.color)
    val description = settlementType.typeDescription
    val text = when (type) {
        is FinanceCategory -> type.text
        else -> ""
    }

    val dataType = when (type) {
        is TransactionType -> when (type) {
            TransactionType.EARNINGS -> DataType.EARNINGS
            TransactionType.EXPENSES -> DataType.EXPENSE
            TransactionType.SAVINGS -> DataType.SAVINGS
        }

        is LiabilityType -> when (type) {
            LiabilityType.LOAN -> DataType.LENT
            LiabilityType.DEBT -> DataType.DEBT
        }

        GoalType -> DataType.GOAL
        else -> DataType.EXPENSE
    }

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
            Icon(
                modifier = Modifier
                    .size(MODEL_DRAWER_ICON_SIZE)
                    .padding(bottom = 5.dp),
                painter = iconImage,
                contentDescription = text,
                tint = color
            )

            Text(
                description,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            state = lazyState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            item(key = 921) {
                SettlementField(
                    sheetVisible = isBottomSheetOpen,
                    datatype = dataType,
                    amountState = adjustAmountState,
                    financeEntityList = financeEntityList,
                    colorResId = settlementType.color,
                    selectedFinanceEntity = selectedFinanceEntity,
                    wasSuccess = wasSuccess
                )
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
                        colorResId = settlementType.color,
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
                        colorResId = settlementType.color
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
                        text = "Add",
                        wasSuccess = wasSuccess,
                        colorResId = settlementType.color,
                        filledColor = Color.Transparent
                    ) {
                        selectedFinanceEntity.value?.let {
                            if (settleAsDouble != null) {

                                if (
                                    settleAsDouble > it.remainingAmount &&
                                    !it.isStartDateTimeNotEqualToDeadlineDateTime
                                ) {
                                    wasSuccess.value = State.ERROR
                                    return@ModelDrawerButton
                                }
                                val settlement = Settlement(
                                    settlementId = UUID.randomUUID().toString(),
                                    amount = settleAsDouble,
                                    dateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                                    label = settlementType.text,
                                    description = descriptionState.text.toString(),
                                    tagIcon = it.tagIcon,
                                    paymentMethod = selectedPaymentMethod.value,
                                    settlementType = settlementType,
                                )
                                settlement.financeEntity = it

                                val financeEntityType = when (it) {
                                    is FinanceEntity.Transaction -> "TRANSACTION"
                                    is FinanceEntity.Goal -> "GOAL"
                                    is FinanceEntity.Liability -> "LIABILITY"
                                }

                                viewModel.addSettlementData(
                                    it.id,
                                    financeEntityType,
                                    settlement
                                )

                                adjustAmountState.clearText()

                                // Dismiss the model drawer.
                                onDismiss()
                            }
                        } ?: run {
                            wasSuccess.value = State.ERROR
                            val errorMessage = when (dataType) {
                                DataType.LENT -> "Please select a loan to repay"
                                DataType.DEBT -> "Please select a debt to repay"
                                else -> "Please select a goal to attain"
                            }

                            // Show snackbar
                            userViewModel.showActionNotification(
                                errorMessage,
                                color = Color.Red.copy(0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
