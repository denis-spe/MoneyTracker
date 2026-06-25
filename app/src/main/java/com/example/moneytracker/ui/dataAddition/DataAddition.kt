// =====
// Praise be the LORD GOD,
// For the LORD is good and his mercy endures forever
// =====

package com.example.moneytracker.ui.dataAddition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.types.FinanceCategory
import com.example.moneytracker.backend.storage.types.GoalType
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.SettlementType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.example.moneytracker.helper.GoalWarning
import com.example.moneytracker.helper.InputState
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.isAmountValid
import com.example.moneytracker.helper.isLabelValid
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

val MODEL_DRAWER_ICON_SIZE = 30.dp
val FONT_WEIGHT = FontWeight.Bold
const val MAX_LABEL_LENGTH = 15

private val drawerShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionModelDrawer(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    uiState: HomeUiState,
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val adjustFinance by viewModel.adjustFinance.collectAsStateWithLifecycle()

    if (uiState.isDatasetBottomSheetOpen) {

        ModalBottomSheet(
            onDismissRequest = {
                viewModel.updateOnDatasetModelBottomSheetShow(false)
            },

            sheetState = sheetState,

            containerColor = BottomSheetDefaults.ContainerColor.copy(
                alpha = 0.97f
            ),

            dragHandle = null,

            modifier = Modifier.statusBarsPadding()
        ) {

            val entries = remember {
                TransactionType.entries +
                        LiabilityType.entries +
                        listOf(GoalType)
            }

            DataAdditionModelDrawerContent(
                viewModel = viewModel,
                userViewModel = userViewModel,
                entries = entries,
                adjustFinance = adjustFinance,
            )
        }
    }

    if (uiState.isSettlementBottomSheetOpen) {

        ModalBottomSheet(
            onDismissRequest = {
                viewModel.updateOnAdjustModelBottomSheetShow(false)
            },

            sheetState = sheetState,

            containerColor = BottomSheetDefaults.ContainerColor.copy(
                alpha = 0.97f
            ),

            dragHandle = null,

            modifier = Modifier.statusBarsPadding()
        ) {

            val entries = remember {
                SettlementType.entries.filter {
                    it != SettlementType.INITIAL
                }
            }

            DataAdditionModelDrawerContent(
                viewModel = viewModel,
                userViewModel = userViewModel,
                entries = entries,
                adjustFinance = adjustFinance,
            )
        }
    }
}

@Stable
private class DrawerSelection<T>(
    initial: T? = null
) {
    var visible by mutableStateOf(false)
    var selected by mutableStateOf(initial, structuralEqualityPolicy())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DataAdditionModelDrawerContent(
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    entries: List<T>,
    adjustFinance: List<FinanceEntity>,
) {

    val selection = remember {
        DrawerSelection<T>()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(drawerShape)
            .verticalScroll(rememberScrollState())
    ) {

        entries.forEach { item ->

            val text = when (item) {
                is FinanceCategory -> item.text
                is SettlementType -> item.text
                else -> ""
            }

            val description = when (item) {
                is FinanceCategory -> item.typeDescription
                is SettlementType -> item.typeDescription
                else -> ""
            }

            val color = when (item) {
                is FinanceCategory -> item.color
                is SettlementType -> item.color
                else -> R.color.error_color
            }

            val painter = when (item) {
                is FinanceCategory -> item.filledIcon
                is SettlementType -> item.icon
                else -> R.drawable.initial
            }

            DrawerItem(
                text = text,
                description = description,
                color = color,
                isItemLast = item == entries.last(),
                painter = painter
            ) {
                selection.visible = true
                selection.selected = item
            }
        }
    }

    if (selection.visible) {

        ModalBottomSheet(
            onDismissRequest = {
                selection.visible = false
                selection.selected = null
            },

            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),

            dragHandle = null
        ) {
            when (val clicked = selection.selected) {

                is TransactionType -> {

                    FinancialDataInput(
                        placeholder = when (clicked) {
                            TransactionType.EARNINGS -> "Earned from"
                            TransactionType.SAVINGS -> "Savings from"
                            TransactionType.EXPENSES -> "Spent on"
                        },

                        type = clicked,

                        buttonText = when (clicked) {
                            TransactionType.EARNINGS -> "Received"
                            TransactionType.SAVINGS -> "Saved"
                            TransactionType.EXPENSES -> "Spent"
                        },

                        userViewModel = userViewModel
                    ) {

                        viewModel.updateOnDatasetModelBottomSheetShow(false)

                        selection.visible = false
                    }
                }

                is LiabilityType -> {

                    FinancialDataInput(
                        placeholder = when (clicked) {
                            LiabilityType.DEBT -> "Borrowed from"
                            LiabilityType.LOAN -> "Lent to"
                        },

                        type = clicked,

                        buttonText = when (clicked) {
                            LiabilityType.DEBT -> "Set Debt"
                            LiabilityType.LOAN -> "Lent"
                        },

                        userViewModel = userViewModel
                    ) {

                        viewModel.updateOnDatasetModelBottomSheetShow(false)

                        selection.visible = false
                    }
                }

                is GoalType -> {

                    GoalDataInput(
                        placeholder = "Goal for",

                        buttonText = "Start Goal",

                        userViewModel = userViewModel
                    ) {

                        viewModel.updateOnDatasetModelBottomSheetShow(false)

                        selection.visible = false
                    }
                }

                SettlementType.LENT_REPAY -> {

                    SettlementDataInputs(
                        type = LiabilityType.LOAN,

                        settlementType = SettlementType.LENT_REPAY,

                        userViewModel = userViewModel,

                        adjustFinance = adjustFinance
                    ) {

                        viewModel.updateOnAdjustModelBottomSheetShow(false)

                        selection.visible = false
                    }
                }

                SettlementType.DEBT_REPAY -> {

                    SettlementDataInputs(
                        type = LiabilityType.DEBT,

                        settlementType = SettlementType.DEBT_REPAY,


                        userViewModel = userViewModel,

                        adjustFinance = adjustFinance
                    ) {
                        viewModel.updateOnAdjustModelBottomSheetShow(false)

                        selection.visible = false
                    }
                }

                SettlementType.GOAL_ATTAIN -> {

                    SettlementDataInputs(
                        type = GoalType,

                        settlementType = SettlementType.GOAL_ATTAIN,


                        userViewModel = userViewModel,

                        adjustFinance = adjustFinance
                    ) {

                        viewModel.updateOnAdjustModelBottomSheetShow(false)

                        selection.visible = false
                    }
                }

                SettlementType.WITHDRAWAL -> {

                    WithdrawalInputs(
                        type = TransactionType.EARNINGS,

                        settlementType = SettlementType.WITHDRAWAL,


                        userViewModel = userViewModel,

                        adjustFinance = adjustFinance
                    ) {

                        viewModel.updateOnAdjustModelBottomSheetShow(false)

                        selection.visible = false
                    }
                }

            }
        }
    }
}

@Composable
private fun DrawerItem(
    text: String,
    description: String,
    color: Int,
    painter: Int,
    isItemLast: Boolean = false,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {

        ListItem(
            modifier = Modifier
                .fillMaxWidth(),

            headlineContent = {

                Text(
                    text = text,
                    fontWeight = FONT_WEIGHT,
                    color = colorResource(color)
                )
            },

            leadingContent = {
                Image(
                    painter = painterResource(painter),
                    contentDescription = text,
                    colorFilter = ColorFilter.tint(colorResource(color)),
                    modifier = Modifier.size(MODEL_DRAWER_ICON_SIZE)
                )
            },

            supportingContent = {

                Text(
                    text = description,
                    fontWeight = FontWeight.Light,
                    color = colorResource(color).copy(alpha = 0.5f)
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

        if (!isItemLast) {
            HorizontalDivider(
                color = Color.LightGray.copy(alpha = 0.4f)
            )
        }
    }
}

@Immutable
private data class FormUiState(
    val isError: Boolean = false
)

@Composable
fun FinancialDataInput(
    placeholder: String,
    type: FinanceCategory,
    buttonText: String,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit,
) {

    val viewModel: HomeViewModel = hiltViewModel()

    val scrollState = rememberScrollState()

    val amountState = rememberTextFieldState()

    val labelState = rememberTextFieldState()

    val descriptionState = rememberTextFieldState()

    var uiState by remember {
        mutableStateOf(FormUiState())
    }

    val amountAsDouble by remember {
        derivedStateOf {
            amountState.text.toString().toDoubleOrNull()
        }
    }

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }
    val wasLabelSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

    LaunchedEffect(amountState.text, labelState.text) {

        if (uiState.isError) {
            uiState = uiState.copy(isError = false)
        }
        wasAmountSuccess.isAmountValid(amount = amountAsDouble)
        wasLabelSuccess.isLabelValid(label = labelState.text.toString())
    }

    val creationDateTime = remember {
        mutableStateOf(LocalDateTime.now())
    }

    val tag = remember(type) {
        TagIcon(
            type.text.lowercase(),
            type.tagIconRes
        )
    }

    val tagState = remember {
        mutableStateOf(tag)
    }

    val paymentMethod = remember {
        mutableStateOf(PaymentMethod.CASH)
    }

    val color = colorResource(type.color)

    val affectCurrentAccountState = remember {
        mutableStateOf(true)
    }
    val topModifier = Modifier.clip(
        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(vertical = 16.dp)
            .verticalScroll(scrollState),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier
                .size(MODEL_DRAWER_ICON_SIZE),

            painter = painterResource(type.filledIcon),

            contentDescription = type.text,

            tint = color
        )

        Text(
            text = type.text,
            color = color,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = when {
                wasLabelSuccess.value is InputState.Error &&
                        wasAmountSuccess.value is InputState.Error -> {
                    (wasAmountSuccess.value as InputState.Error).message + "\n" +
                            (wasLabelSuccess.value as InputState.Error).message
                }

                wasAmountSuccess.value is InputState.Error -> (wasAmountSuccess.value as InputState.Error)
                    .message

                wasLabelSuccess.value is InputState.Error -> (wasLabelSuccess.value as InputState.Error)
                    .message

                else -> type.typeDescription
            },
            color = when {
                wasLabelSuccess.value is InputState.Error &&
                        wasAmountSuccess.value is InputState.Error -> Color.Red

                wasAmountSuccess.value is InputState.Error -> Color.Red
                wasLabelSuccess.value is InputState.Error -> Color.Red
                else -> Color.Gray
            },
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )


        ModelDrawerAmountField(
            state = amountState,
            placeholder = "0",
            colorResId = type.color,
            wasSuccess = wasAmountSuccess,
            displayState = rememberSaveable {
                mutableStateOf("")
            },
            modifier = topModifier,
        )

        ModelDrawerLabelTextField(
            state = labelState,
            title = "Label",
            description = "Add a label",
            placeholder = placeholder,
            colorResId = type.color,
            wasSuccess = wasLabelSuccess,
            textLength = MAX_LABEL_LENGTH,
            displayText = rememberSaveable {
                mutableStateOf("")
            }
        )

        ModelDrawerTag(
            colorResId = type.color,
            title = "Tag",
            iconState = tagState
        )

        ModelDrawerDescriptionTextField(
            state = descriptionState,
            placeholder = "Note (Optional)",
            title = "Note",
            description = "Take a note",
            colorResId = type.color,
            wasSuccess = null,
            displayText = rememberSaveable {
                mutableStateOf("")
            }
        )

        DateTimeInput(
            showTime = remember { mutableStateOf(false) },
            showDate = remember { mutableStateOf(false) },
            localDateTimeState = creationDateTime,
            colorResId = type.color,
            timeContainerModifier = if (type != GoalType) topModifier else bottomModifier
        )

        if (type != GoalType) {
            AffectCurrentAccount(
                label = when (type) {
                    is TransactionType -> "Affect current account"
                    is LiabilityType -> {
                        when (type) {
                            LiabilityType.DEBT -> "Was Amount Received"
                            LiabilityType.LOAN -> "Affect current account"
                        }
                    }

                    else -> ""
                },
                modifier = bottomModifier,
                color = colorResource(type.color),
                affectCurrentAccountState = affectCurrentAccountState
            )
        }

        PaymentMethodDropdown(
            colorResId = type.color,
            selectedPaymentMethod = paymentMethod
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),

            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ModelDrawerButton(
                    text = buttonText,

                    isError = wasLabelSuccess.value is InputState.Error ||
                            wasAmountSuccess.value is InputState.Error,

                    colorResId = type.color,

                    filledColor = color,

                    textColor = Color.White
                ) {

                    val label = labelState.text.toString()

                    wasLabelSuccess.isLabelValid(label = label)
                    wasAmountSuccess.isAmountValid(amount = amountAsDouble)

                    if (
                        wasLabelSuccess.value is InputState.Error ||
                        wasAmountSuccess.value is InputState.Error
                    ) {
                        return@ModelDrawerButton
                    }

                    val entity = when (type) {

                        is TransactionType -> {

                            FinanceEntity.Transaction(
                                id = UUID.randomUUID().toString(),
                                transactionType = type,
                                amount = amountAsDouble!!,
                                label = label,
                                description = descriptionState.text.toString(),
                                createdAt = creationDateTime.value.toFirestoreTimestampUtc(),
                                tagIcon = tagState.value,
                                paymentMethod = paymentMethod.value,
                                affectCurrentAccount = affectCurrentAccountState.value
                            )
                        }

                        is LiabilityType -> {

                            FinanceEntity.Liability(
                                id = UUID.randomUUID().toString(),
                                liabilityType = type,
                                amount = amountAsDouble!!,
                                label = label,
                                description = descriptionState.text.toString(),
                                createdAt = creationDateTime.value.toFirestoreTimestampUtc(),
                                tagIcon = tagState.value,
                                paymentMethod = paymentMethod.value,
                                affectCurrentAccount = affectCurrentAccountState.value
                            )
                        }

                        else -> return@ModelDrawerButton
                    }

                    if (
                        wasLabelSuccess.value is InputState.Success
                        && wasAmountSuccess.value is InputState.Success
                    ) {
                        viewModel.addData(entity)

                        amountState.clearText()
                        labelState.clearText()
                        descriptionState.clearText()

                        onDismiss()

                        userViewModel.launchSnackBarHostState(
                            message = "$label was added successfully"
                        )
                    }
                }

                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Cancel",
                        color = colorResource(type.color),
                        fontWeight = FontWeight.Medium
                    )
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
    val viewModel: HomeViewModel = hiltViewModel()

    val scrollState = rememberScrollState()

    val amountState = rememberTextFieldState()
    val labelState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()


    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }
    val wasLabelSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

    val amountAsDouble by remember {
        derivedStateOf {
            amountState.text.toString().toDoubleOrNull()
        }
    }

    val localDateTimeState = remember {
        mutableStateOf(LocalDateTime.now().toMidnight())
    }
    val endLocalDateTimeState = remember {
        mutableStateOf(LocalDateTime.now().toMidnight())
    }

    val goalDateTimeWarningState = remember {
        mutableStateOf(GoalWarning.INITIAL)
    }

    val routineData = remember {
        mutableStateOf(RoutineData())
    }

    val tagIcon = remember {
        TagIcon("goal", GoalType.tagIconRes)
    }
    val tagIconState = remember {
        mutableStateOf(tagIcon)
    }

    val paymentMethod = remember {
        mutableStateOf(PaymentMethod.CASH)
    }

    val color = colorResource(GoalType.color)

    val topModifier = Modifier.clip(
        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(vertical = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier.size(MODEL_DRAWER_ICON_SIZE),
            painter = painterResource(GoalType.filledIcon),
            contentDescription = GoalType.text,
            tint = color
        )


        Text(
            text = GoalType.text,
            color = color,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = when {
                wasLabelSuccess.value is InputState.Error &&
                        wasAmountSuccess.value is InputState.Error -> {
                    (wasAmountSuccess.value as InputState.Error).message + "\n" +
                            (wasLabelSuccess.value as InputState.Error).message
                }

                wasAmountSuccess.value is InputState.Error -> (wasAmountSuccess.value as InputState.Error)
                    .message

                wasLabelSuccess.value is InputState.Error -> (wasLabelSuccess.value as InputState.Error)
                    .message

                else -> GoalType.typeDescription
            },
            color = when {
                wasLabelSuccess.value is InputState.Error &&
                        wasAmountSuccess.value is InputState.Error -> Color.Red

                wasAmountSuccess.value is InputState.Error -> Color.Red
                wasLabelSuccess.value is InputState.Error -> Color.Red
                else -> Color.Gray
            },
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )

        ModelDrawerAmountField(
            state = amountState,
            placeholder = "0.0",
            colorResId = GoalType.color,
            wasSuccess = wasAmountSuccess,
            displayState = rememberSaveable { mutableStateOf("") },
            modifier = topModifier
        )

        ModelDrawerLabelTextField(
            state = labelState,
            title = "Label",
            description = "Add a label for the given amount",
            placeholder = placeholder,
            colorResId = GoalType.color,
            wasSuccess = wasLabelSuccess,
            textLength = MAX_LABEL_LENGTH,
            displayText = rememberSaveable { mutableStateOf("") }
        )

        ModelDrawerTag(
            colorResId = GoalType.color,
            title = "Tag",
            iconState = tagIconState
        )

        ModelDrawerDescriptionTextField(
            state = descriptionState,
            placeholder = "Note (Optional)",
            title = "Note",
            description = "Take a note for the given amount",
            colorResId = GoalType.color,
            wasSuccess = null,
            displayText = rememberSaveable { mutableStateOf("") }
        )

        DateTimeRange(
            startLocalDateTimeState = localDateTimeState,
            endLocalDateTimeState = endLocalDateTimeState,
            colorResId = GoalType.color,
            goalDateTimeWarningState = goalDateTimeWarningState
        )

        RepeatableTransaction(
            repeatByState = routineData,
            dataType = DataType.GOAL,
            startLocalDateTimeState = localDateTimeState,
            endLocalDateTimeState = endLocalDateTimeState,
            goalDateTimeWarningState = goalDateTimeWarningState,
            modifier = bottomModifier
        )

        PaymentMethodDropdown(
            colorResId = GoalType.color,
            selectedPaymentMethod = paymentMethod
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ModelDrawerButton(
                    text = buttonText,
                    isError = wasLabelSuccess.value is InputState.Error ||
                            wasAmountSuccess.value is InputState.Error,
                    colorResId = GoalType.color,
                    filledColor = color,
                    textColor = Color.White
                ) {
                    val label = labelState.text.toString()

                    wasLabelSuccess.isLabelValid(label = label)
                    wasAmountSuccess.isAmountValid(amount = amountAsDouble)

                    if (
                        wasLabelSuccess.value is InputState.Error ||
                        wasAmountSuccess.value is InputState.Error
                    ) {
                        return@ModelDrawerButton
                    }

                    val isLongRoutine = routineData.value.routine in listOf(
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
                        triggerMillis = normalizedEnd.toFirestoreTimestampUtc().toEpochMilli()
                    )

                    val entity = FinanceEntity.Goal(
                        id = UUID.randomUUID().toString(),
                        amount = amountAsDouble ?: 0.0,
                        label = label,
                        description = descriptionState.text.toString(),
                        createdAt = normalizedStart.toFirestoreTimestampUtc(),
                        tagIcon = tagIconState.value,
                        paymentMethod = paymentMethod.value,
                        routine = routine
                    )

                    viewModel.addData(entity)
                    viewModel.beginTheWork(entity)

                    amountState.clearText()
                    labelState.clearText()
                    descriptionState.clearText()
                    tagIconState.value = tagIcon
                    goalDateTimeWarningState.value = GoalWarning.INITIAL

                    onDismiss()
                }
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Cancel",
                        color = colorResource(GoalType.color),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SettlementDataInputs(
    type: FinanceCategory,
    settlementType: SettlementType,
    userViewModel: UserViewModel,
    adjustFinance: List<FinanceEntity>,
    onDismiss: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val financeEntityList = remember(adjustFinance, type) {
        adjustFinance.filter { it.financeType == type }
    }

    val scrollState = rememberScrollState()

    val descriptionState = rememberTextFieldState()
    val adjustAmountState = rememberTextFieldState()

    var wasSuccess by remember { mutableStateOf(State.INITIAL) }
    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }
    
    val selectedFinanceEntity = remember {
        mutableStateOf<FinanceEntity?>(null)
    }
    val selectedPaymentMethod = remember(selectedFinanceEntity.value) {
        mutableStateOf(
            selectedFinanceEntity.value?.paymentMethod
                ?: PaymentMethod.CASH
        )
    }
    val localDateTimeState = remember {
        mutableStateOf(LocalDateTime.now())
    }

    val settleAsDouble by remember {
        derivedStateOf {
            adjustAmountState.text.toString().toDoubleOrNull()
        }
    }
    val affectCurrentAccountState = remember {
        mutableStateOf(true)
    }

    val color = colorResource(settlementType.color)
    val topModifier = Modifier.clip(
        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
    )

    val iconImage = painterResource(settlementType.icon)

    val dataType = when (type) {
        TransactionType.EARNINGS -> DataType.EARNINGS
        TransactionType.EXPENSES -> DataType.EXPENSE
        TransactionType.SAVINGS -> DataType.SAVINGS
        LiabilityType.LOAN -> DataType.LENT
        LiabilityType.DEBT -> DataType.DEBT
        GoalType -> DataType.GOAL
    }

    LaunchedEffect(settleAsDouble) {
        if (settleAsDouble != null)
            wasAmountSuccess.isAmountValid(settleAsDouble)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(vertical = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier.size(MODEL_DRAWER_ICON_SIZE),
            painter = iconImage,
            contentDescription = settlementType.text,
            tint = color
        )


        Text(
            text = settlementType.text,
            color = color,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = when {
                wasAmountSuccess.value is InputState.Error -> (wasAmountSuccess.value as InputState.Error)
                    .message

                else -> settlementType.typeDescription
            },
            color = when {
                wasAmountSuccess.value is InputState.Error -> Color.Red
                else -> Color.Gray
            },
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )

        SettlementField(
            sheetVisible = true,
            datatype = dataType,
            amountState = adjustAmountState,
            financeEntityList = financeEntityList,
            colorResId = settlementType.color,
            selectedFinanceEntity = selectedFinanceEntity,
            wasSuccess = wasAmountSuccess,
            modifier = topModifier
        )

        ModelDrawerDescriptionTextField(
            state = descriptionState,
            placeholder = "Note (Optional)",
            title = "Note",
            description = "Take a note for the given amount",
            colorResId = settlementType.color,
            wasSuccess = null,
            displayText = rememberSaveable { mutableStateOf("") }
        )

        DateTimeInput(
            showTime = remember { mutableStateOf(false) },
            showDate = remember { mutableStateOf(false) },
            localDateTimeState = localDateTimeState,
            colorResId = settlementType.color,
            timeContainerModifier = bottomModifier
        )

        if (type != GoalType) {
            AffectCurrentAccount(
                label = when (type) {
                    is TransactionType -> "Affect current account"
                    is LiabilityType -> {
                        when (type) {
                            LiabilityType.DEBT -> "Was Amount Received"
                            LiabilityType.LOAN -> "Was Amount Paid"
                        }
                    }

                    else -> ""
                },
                modifier = bottomModifier,
                color = colorResource(settlementType.color),
                affectCurrentAccountState = affectCurrentAccountState
            )
        }

        PaymentMethodDropdown(
            colorResId = settlementType.color,
            selectedPaymentMethod = selectedPaymentMethod
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ModelDrawerButton(
                    text = "Add",
                    isError = wasAmountSuccess.value is InputState.Error,
                    colorResId = settlementType.color,
                    filledColor = Color.Transparent,
                    textColor = color
                ) {
                    val entity = selectedFinanceEntity.value
                    val settleAmount = settleAsDouble

                    if (entity == null) {
                        wasAmountSuccess.value = InputState.Error(
                            "Please select a ${settlementType.text.lowercase()}"
                        )
                        return@ModelDrawerButton
                    }

                    if (!wasAmountSuccess.isAmountValid(settleAmount)) return@ModelDrawerButton

                    if (
                        settleAmount!! > entity.remainingAmount &&
                        !entity.isStartDateTimeNotEqualToDeadlineDateTime
                    ) {
                        wasSuccess = State.ERROR
                        return@ModelDrawerButton
                    }

                    val settlement = Settlement(
                        settlementId = UUID.randomUUID().toString(),
                        amount = settleAmount,
                        dateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                        label = settlementType.text,
                        description = descriptionState.text.toString(),
                        tagIcon = entity.tagIcon,
                        paymentMethod = selectedPaymentMethod.value,
                        settlementType = settlementType,
                        affectCurrentAccount = affectCurrentAccountState.value
                    ).apply {
                        financeEntity = entity
                    }

                    val financeEntityType = when (entity) {
                        is FinanceEntity.Transaction -> "TRANSACTION"
                        is FinanceEntity.Goal -> "GOAL"
                        is FinanceEntity.Liability -> "LIABILITY"
                    }

                    viewModel.addSettlementData(
                        entity.id,
                        financeEntityType,
                        settlement
                    )

                    adjustAmountState.clearText()
                    descriptionState.clearText()

                    onDismiss()
                }
                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Cancel",
                        color = colorResource(settlementType.color),
                        fontWeight = FontWeight.Medium
                    )
                }

            }
        }
    }
}


@Composable
fun WithdrawalInputs(
    type: FinanceCategory,
    settlementType: SettlementType,
    userViewModel: UserViewModel,
    adjustFinance: List<FinanceEntity>,
    onDismiss: () -> Unit
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val financeEntityList = remember(adjustFinance, type) {
        adjustFinance.filter { it.financeType == type }
    }

    val scrollState = rememberScrollState()

    val descriptionState = rememberTextFieldState()
    val adjustAmountState = rememberTextFieldState()

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

    val selectedFinanceEntity = remember {
        mutableStateOf<FinanceEntity?>(null)
    }
    val fromPaymentMethod = remember(selectedFinanceEntity.value) {
        mutableStateOf(
            selectedFinanceEntity.value?.paymentMethod
                ?: PaymentMethod.CASH
        )
    }
    val toPaymentMethod = remember {
        mutableStateOf(PaymentMethod.CASH)
    }
    val localDateTimeState = remember {
        mutableStateOf(LocalDateTime.now())
    }

    val settleAsDouble by remember {
        derivedStateOf {
            adjustAmountState.text.toString().toDoubleOrNull()
        }
    }

    val affectCurrentAccountState = remember {
        mutableStateOf(true)
    }

    val color = colorResource(settlementType.color)

    val iconImage = painterResource(settlementType.icon)

    val dataType = when (type) {
        TransactionType.EARNINGS -> DataType.EARNINGS
        TransactionType.EXPENSES -> DataType.EXPENSE
        TransactionType.SAVINGS -> DataType.SAVINGS
        LiabilityType.LOAN -> DataType.LENT
        LiabilityType.DEBT -> DataType.DEBT
        GoalType -> DataType.GOAL
    }
    val topModifier = Modifier.clip(
        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
    )

    LaunchedEffect(settleAsDouble) {
        if (settleAsDouble != null)
            wasAmountSuccess.isAmountValid(settleAsDouble)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .padding(vertical = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier.size(MODEL_DRAWER_ICON_SIZE),
            painter = iconImage,
            contentDescription = settlementType.text,
            colorFilter = ColorFilter.tint(color)
        )

        Text(
            text = settlementType.text,
            color = color,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Text(
            text = when {
                wasAmountSuccess.value is InputState.Error -> (wasAmountSuccess.value as InputState.Error)
                    .message

                else -> settlementType.typeDescription
            },
            color = when {
                wasAmountSuccess.value is InputState.Error -> Color.Red
                else -> Color.Gray
            },
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )

        SettlementField(
            sheetVisible = true,
            datatype = dataType,
            amountState = adjustAmountState,
            financeEntityList = financeEntityList,
            colorResId = settlementType.color,
            selectedFinanceEntity = selectedFinanceEntity,
            wasSuccess = wasAmountSuccess,
            modifier = topModifier
        )

        ModelDrawerDescriptionTextField(
            state = descriptionState,
            placeholder = "Note (Optional)",
            title = "Note",
            description = "Take a note for the given amount",
            colorResId = settlementType.color,
            wasSuccess = null,
            displayText = rememberSaveable { mutableStateOf("") }
        )

        DateTimeInput(
            showTime = remember { mutableStateOf(false) },
            showDate = remember { mutableStateOf(false) },
            localDateTimeState = localDateTimeState,
            colorResId = settlementType.color,
            timeContainerModifier = topModifier
        )

        AffectCurrentAccount(
            label = "Affect current account",
            modifier = bottomModifier,
            color = color,
            affectCurrentAccountState = affectCurrentAccountState
        )

        Text(
            "From account:",
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        PaymentMethodDropdown(
            colorResId = settlementType.color,
            selectedPaymentMethod = fromPaymentMethod
        )

        Text(
            "To account:",
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
        PaymentMethodDropdown(
            colorResId = settlementType.color,
            selectedPaymentMethod = toPaymentMethod
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ModelDrawerButton(
                    text = "Add",
                    isError = wasAmountSuccess.value is InputState.Error,
                    colorResId = settlementType.color,
                    filledColor = Color.Transparent,
                    textColor = color
                ) {
                    val entity = selectedFinanceEntity.value
                    val settleAmount = settleAsDouble

                    if (entity == null) {
                        wasAmountSuccess.value = InputState.Error(
                            "Please select a ${settlementType.text.lowercase()}"
                        )

                        return@ModelDrawerButton
                    }

                    if (!wasAmountSuccess.isAmountValid(settleAmount)) return@ModelDrawerButton

                    val withdrawal = Withdrawal(
                        withdrawalId = UUID.randomUUID().toString(),
                        amount = settleAmount!!,
                        createdAt = localDateTimeState.value.toFirestoreTimestampUtc(),
                        label = settlementType.text,
                        description = descriptionState.text.toString(),
                        toPaymentMethod = toPaymentMethod.value,
                        fromPaymentMethod = fromPaymentMethod.value,
                        affectCurrentAccount = affectCurrentAccountState.value
                    )

                    val financeEntityType = when (entity) {
                        is FinanceEntity.Transaction -> "TRANSACTION"
                        is FinanceEntity.Goal -> "GOAL"
                        is FinanceEntity.Liability -> "LIABILITY"
                    }

                    viewModel.addWithdrawalData(
                        entity.id,
                        financeEntityType,
                        withdrawal = withdrawal
                    )

                    adjustAmountState.clearText()
                    descriptionState.clearText()

                    onDismiss()
                }

                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Cancel",
                        color = color,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

