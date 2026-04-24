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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.helper.GoalWarning
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.isStartDateTimeNotEqualToDeadlineDateTime
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.ActionNotification
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now
import java.util.UUID

val MODEL_DRAWER_ICON_SIZE = 25.dp
val FONT_WEIGHT = FontWeight.Bold
const val MAX_LABEL_LENGTH = 15


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionModelDrawer(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    datasets: List<Dataset>,
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

            /*... Text field and button ...*/
            DataAdditionModelDrawerContent(
                viewModel = viewModel,
                userViewModel = userViewModel,
                entries = DataType.entries,
                datasets = datasets
            )
        }
    }

    if (uiState.isAdjustmentBottomSheetOpen) {
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
                entries = AdjustmentType.entries.filter { it != AdjustmentType.INITIAL },
                datasets = datasets
            )
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DataAdditionModelDrawerContent(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    datasets: List<Dataset>,
    entries: List<T>,
) {

    val showDataTypeBottomSheet = remember { mutableStateOf(false) }
    val clickedDataType = remember { mutableStateOf<T?>(null) }
    val userUiState by userViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
    ) {
        items(items = entries) {
            val text = when (it) {
                is DataType -> it.text
                is AdjustmentType -> it.text
                else -> ""
            }

            val typeDescription = when (it) {
                is DataType -> it.typeDescription
                is AdjustmentType -> it.typeDescription
                else -> ""
            }

            val color = when (it) {
                is DataType -> it.color
                is AdjustmentType -> it.color
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

            when (clickedDataType.value) {
                DataType.EARNINGS -> {
                    FinancialDataInput(
                        placeholder = "Earned from",
                        dataType = DataType.EARNINGS,
                        buttonText = "Received",
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.EXPENSE -> {
                    FinancialDataInput(
                        placeholder = "Spent on",
                        dataType = DataType.EXPENSE,
                        buttonText = "Spent",
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.DEBT -> {
                    FinancialDataInput(
                        placeholder = "Borrowed from",
                        dataType = DataType.DEBT,
                        buttonText = "Set Debt",
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.LENT -> {
                    FinancialDataInput(
                        placeholder = "Lent to",
                        dataType = DataType.LENT,
                        buttonText = "Lent",
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.SAVINGS -> {
                    FinancialDataInput(
                        placeholder = "Savings from",
                        dataType = DataType.SAVINGS,
                        buttonText = "Saved",
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                DataType.GOAL -> {
                    GoalDataInput(
                        placeholder = "Goal for",
                        dataType = DataType.GOAL,
                        buttonText = "Start Goal",
                        userViewModel = userViewModel
                    ) {
                        viewModel.updateOnDatasetModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                AdjustmentType.LENT_REPAY -> {
                    AdjustmentDataInputs(
                        DataType.LENT,
                        AdjustmentType.LENT_REPAY,
                        datasets = datasets
                    ) {
                        viewModel.updateOnAdjustModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                AdjustmentType.GOAL_ATTAIN -> {
                    AdjustmentDataInputs(
                        DataType.GOAL,
                        AdjustmentType.GOAL_ATTAIN,
                        datasets = datasets
                    ) {
                        viewModel.updateOnAdjustModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                AdjustmentType.DEBT_REPAY -> {
                    AdjustmentDataInputs(
                        DataType.DEBT,
                        AdjustmentType.DEBT_REPAY,
                        datasets = datasets
                    ) {
                        viewModel.updateOnAdjustModelBottomSheetShow(false)
                        viewModel.updateIsBottomSheetContentLoading(true)
                    }
                }

                else -> {}
            }

            // Action Notification
            ActionNotification(
                backgroundColor = userUiState.actionNotificationColor,
                visible = userUiState.isActionNotificationVisible,
                onDismiss = { userViewModel.dismissActionNotification() }
            ) {
                Text(
                    text = userUiState.actionNotificationMessage,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
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
    dataType: DataType,
    buttonText: String,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
) {
    val colorResId = dataType.color
    val showDate = remember { mutableStateOf(false) }
    val showTime = remember { mutableStateOf(false) }
    val creationDateTime = remember { mutableStateOf(LocalDateTime.now()) }
    remember { mutableStateOf(LocalDateTime.now()) }
    val amountState = rememberTextFieldState()
    val amountToDisplay = rememberSaveable { mutableStateOf("") }
    val labelState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val tag = TagIcon(
        dataType.text.lowercase(),
        icon = when (dataType) {
            DataType.DEBT -> R.drawable.debt
            DataType.LENT -> R.drawable.lent
            DataType.SAVINGS -> R.drawable.savings
            DataType.EXPENSE -> R.drawable.expense
            DataType.EARNINGS -> R.drawable.earnings
            else -> R.drawable.description
        }
    )
    val labelIconState = remember {
        mutableStateOf(tag)
    }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val lazyState = rememberLazyListState()
    val amountAsDouble = amountState.text.toString().toDoubleOrNull()
    val viewModel: HomeViewModel = hiltViewModel()
    val iconImage = painterResource(dataType.filledIcon)
    val description = dataType.typeDescription
    val color = (if (wasSuccess.value == State.ERROR) colorResource(R.color.error_color)
    else colorResource(dataType.color))



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
                contentDescription = dataType.text,
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
            // Amount
            item(key = 39) {
                Row(
                    modifier = Modifier.animateItem()
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
                            val dataset = Dataset(
                                id = UUID.randomUUID().toString(),
                                dataType = dataType,
                                amount = amountAsDouble,
                                label = label,
                                description = descriptionState.text.toString(),
                                createdAt = creationDateTime.value
                                    .toFirestoreTimestampUtc(),
                                tagIcon = labelIconState.value,
                                paymentMethod = selectedPaymentMethod.value,
                            )
                            viewModel.addData(dataset)
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
                            if (errorMessage != null) {
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
    dataType: DataType,
    buttonText: String,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
) {
    val colorResId = dataType.color
    val localDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val endLocalDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val amountState = rememberTextFieldState()
    val amountToDisplay = rememberSaveable { mutableStateOf("") }
    val labelState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val tagIcon = TagIcon("goal", R.drawable.tag_goal)
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
    val iconImage = painterResource(dataType.filledIcon)
    val color = colorResource(dataType.color)
    val description = dataType.typeDescription

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
                contentDescription = dataType.text,
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
                if (dataType == DataType.GOAL) {
                    RepeatableTransaction(
                        routineData,
                        startLocalDateTimeState = localDateTimeState,
                        endLocalDateTimeState = endLocalDateTimeState,
                        dataType = DataType.GOAL,
                        goalDateTimeWarningState = goalDateTimeWarningState,
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
                        if (
                            amountState.text.toString().isEmpty() ||
                            labelState.text.toString().isEmpty()
                        ) {
                            wasSuccess.value = State.ERROR
                            return@ModelDrawerButton
                        }

                        if (
                            dataType == DataType.GOAL &&
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
                            val routine = routineData.value.copy(
                                deadlineDateTime = endLocalDateTimeState.value
                                    .toFirestoreTimestampUtc()
                            )

                            val dataset = Dataset(
                                id = UUID.randomUUID().toString(),
                                dataType = dataType,
                                amount = amountAsDouble,
                                label = labelState.text.toString(),
                                description = descriptionState.text.toString(),
                                createdAt = localDateTimeState.value
                                    .toFirestoreTimestampUtc(),
                                tagIcon = labelIconState.value,
                                paymentMethod = selectedPaymentMethod.value,
                                routine = routine
                            )

                            viewModel.addData(dataset)
                            viewModel.beginTheWork(dataset)

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
                            if (errorMessage != null) {
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
 * Adjustment Inputs
 */

@Composable
fun AdjustmentDataInputs(
    dataType: DataType,
    adjustmentType: AdjustmentType,
    datasets: List<Dataset>,
    onDismiss: () -> Unit
) {
    val lazyState = rememberLazyListState()
    val showDate = remember { mutableStateOf(false) }
    val showTime = remember { mutableStateOf(false) }
    val localDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val descriptionState = rememberTextFieldState()
    val wasRepaySuccess = remember { mutableStateOf(State.INITIAL) }
    val selectedDataset = remember { mutableStateOf<Dataset?>(null) }
    val selectedPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val adjustAmountState = rememberTextFieldState()
    val adjustAsDouble = adjustAmountState.text.toString().toDoubleOrNull()
    val isBottomSheetOpen by remember { mutableStateOf(true) }
    val viewModel = hiltViewModel<HomeViewModel>()
    val iconImage = painterResource(adjustmentType.icon)
    val color = colorResource(adjustmentType.color)
    val description = adjustmentType.typeDescription

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
                contentDescription = dataType.text,
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
                AdjustmentField(
                    isBottomSheetOpen,
                    datatype = dataType,
                    amountState = adjustAmountState,
                    datasets = datasets,
                    wasRepaySuccess = wasRepaySuccess,
                    selectedDataset = selectedDataset,
                    colorResId = adjustmentType.color
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
                        colorResId = adjustmentType.color,
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
                        colorResId = adjustmentType.color
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
                        wasSuccess = wasRepaySuccess,
                        colorResId = adjustmentType.color,
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
                                    label = adjustmentType.text,
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

                                adjustAmountState.clearText()

                                // Dismiss the model drawer.
                                onDismiss()
                            }
                        } ?: run {
                            wasRepaySuccess.value = State.ERROR
                        }

                    }
                }
            }
        }
    }
}


