// Bless be the name of name of LORD of hosts and to Lamb of GOD
package com.example.moneytracker.ui.detailScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Achievement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.backend.storage.Withdrawal
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.SettlementType
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.DeleteButton
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.dataAddition.DateTimeInput
import com.example.moneytracker.ui.homeScreen.dataAddition.ModelDrawerAmountField
import com.example.moneytracker.ui.homeScreen.dataAddition.ModelDrawerButton
import com.example.moneytracker.ui.homeScreen.dataAddition.ModelDrawerTag
import com.example.moneytracker.ui.homeScreen.dataAddition.ModelDrawerTextField
import com.example.moneytracker.ui.homeScreen.dataAddition.PaymentMethodDropdown
import com.example.moneytracker.ui.homeScreen.dataAddition.WasAmountReceived
import com.example.moneytracker.ui.theme.StewardTheme
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun DetailNote(
    title: String,
    note: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Text(
            text = note,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalAttained(
    color: Color,
    goalId: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    val detailState by viewModel.detailState.collectAsState()
    val goal = (detailState.financeEntity as? DataState.Success)?.data as? FinanceEntity.Goal

    // States for the dialog
    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf("0.0") }
    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val paymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val dateTime = remember { mutableStateOf(LocalDateTime.now()) }
    val tagIcon = remember { mutableStateOf(TagIcon("", R.drawable.oulined_attain)) }
    val topModifier = Modifier.clip(
        RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        )
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        )
    )

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    // Initialize values if goal is ready
    LaunchedEffect(showDialog.value, goal) {
        if (showDialog.value && goal != null) {
            labelState.setTextAndPlaceCursorAtEnd(goal.label)
            labelDisplay.value = goal.label
            tagIcon.value = goal.tagIcon
        }
    }

    IconButton(
        onClick = { showDialog.value = true },
        colors = IconButtonDefaults.iconButtonColors().copy(
            containerColor = color
        )
    ) {
        Icon(imageVector = Icons.Default.AddTask, contentDescription = "Add Attain")
    }

    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.oulined_attain),
                    contentDescription = "Attain",
                    tint = colorResource(R.color.Attain)
                )

                Text(
                    "Add Goal Attainment",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.Attain),
                    modifier = Modifier.padding(top = 2.dp)
                )


                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = R.color.Attain,
                    containerModifier = topModifier
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Details...",
                    colorResId = R.color.Attain
                )

                DateTimeInput(
                    showTime = showTimePicker,
                    showDate = showDatePicker,
                    localDateTimeState = dateTime,
                    colorResId = R.color.Attain,
                    timeContainerModifier = bottomModifier
                )

                PaymentMethodDropdown(
                    colorResId = R.color.Attain,
                    selectedPaymentMethod = paymentMethod
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModelDrawerButton(
                        text = "Add Attainment",
                        colorResId = R.color.Attain,
                        textColor = colorResource(R.color.Attain)
                    ) {
                        viewModel.addGoalAttainment(
                            goalId = goalId,
                            amount = amountDisplay.value.toDoubleOrNull() ?: 0.0,
                            label = "Attained",
                            description = descriptionDisplay.value,
                            paymentMethod = paymentMethod.value,
                            dateTime = dateTime.value.toFirestoreTimestampUtc(),
                            tagIcon = tagIcon.value
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoal(
    color: Color,
    goalId: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    val detailState by viewModel.detailState.collectAsState()
    val goal = (detailState.financeEntity as? DataState.Success)?.data as? FinanceEntity.Goal

    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val tagIcon = remember { mutableStateOf(TagIcon("", R.drawable.initial)) }
    val localDateState = remember { mutableStateOf(LocalDateTime.now()) }
    val paymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }

    val topModifier = Modifier.clip(
        RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        )
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        )
    )


    LaunchedEffect(showDialog.value, goal) {
        if (showDialog.value && goal != null) {
            labelState.setTextAndPlaceCursorAtEnd(goal.label)
            labelDisplay.value = goal.label
            descriptionState.setTextAndPlaceCursorAtEnd(goal.description)
            descriptionDisplay.value = goal.description
            localDateState.value = goal.createdAt.toLocalDateTimeUtc()
            paymentMethod.value = goal.paymentMethod
            tagIcon.value = goal.tagIcon
        }
    }

    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = color)) {
        OutlinedIconButton(
            onClick = { showDialog.value = true },
            border = BorderStroke(1.dp, color),
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Goal",
                tint = color
            )
        }
    }

    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.tag_goal),
                    contentDescription = "Goal",
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    "Edit Goal Details",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.Goal),
                    modifier = Modifier.padding(top = 2.dp)
                )

                ModelDrawerTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = "Goal Title",
                    colorResId = R.color.Goal,
                    containerModifier = topModifier
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Notes...",
                    colorResId = R.color.Goal
                )

                DateTimeInput(
                    showTime = remember { mutableStateOf(false) },
                    showDate = remember { mutableStateOf(false) },
                    localDateTimeState = localDateState,
                    colorResId = R.color.Goal
                )

                ModelDrawerTag(
                    colorResId = R.color.Goal,
                    title = "Icon",
                    iconState = tagIcon,
                    containerModifier = bottomModifier
                )

                PaymentMethodDropdown(
                    colorResId = R.color.Goal,
                    selectedPaymentMethod = paymentMethod
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModelDrawerButton(
                        text = "Update Goal",
                        colorResId = R.color.Goal,
                        filledColor = colorResource(R.color.Goal),
                        textColor = Color.White
                    ) {
                        viewModel.updateGoalInfo(
                            goalId = goalId,
                            label = labelDisplay.value,
                            description = descriptionDisplay.value,
                            tagIcon = tagIcon.value,
                            localDate = localDateState.value
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSettlementAmount(
    settlement: Settlement,
    financeType: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    val amountState = rememberTextFieldState(settlement.amount.toString())
    val amountDisplay = remember { mutableStateOf(settlement.amount.toString()) }
    val descriptionState = rememberTextFieldState(settlement.description)
    val descriptionDisplay = remember { mutableStateOf(settlement.description) }
    val localDateState = remember { mutableStateOf(settlement.dateTime.toLocalDateTimeUtc()) }

    val colorRes = settlement.settlementType.color
    val getColor = colorResource(colorRes)

    val topModifier = Modifier.clip(
        RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        )
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        )
    )


    OutlinedIconButton(
        onClick = { showDialog.value = true },
        border = BorderStroke(1.dp, getColor)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Settlement",
            tint = getColor,
            modifier = Modifier.size(20.dp)
        )
    }

    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                Icon(
                    painter = painterResource(settlement.settlementType.icon),
                    contentDescription = settlement.settlementType.text,
                    tint = getColor,
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    "Edit ${settlement.settlementType.text} Details",
                    fontWeight = FontWeight.Bold,
                    color = getColor,
                    modifier = Modifier.padding(top = 2.dp)
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = colorRes,
                    containerModifier = topModifier
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Details...",
                    colorResId = colorRes,
                )

                DateTimeInput(
                    showTime = remember { mutableStateOf(false) },
                    showDate = remember { mutableStateOf(false) },
                    localDateTimeState = localDateState,
                    colorResId = colorRes,
                    timeContainerModifier = bottomModifier
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    ModelDrawerButton(
                        text = "Update ${settlement.settlementType.text}",
                        colorResId = colorRes,
                        filledColor = getColor.copy(0.2f),
                        textColor = getColor
                    ) {
                        val newAmount = amountDisplay.value.toDoubleOrNull() ?: settlement.amount
                        viewModel.updateSettlement(
                            financeType = financeType,
                            oldSettlement = settlement,
                            newAmount = newAmount,
                            newDescription = descriptionDisplay.value,
                            localDate = localDateState.value.toFirestoreTimestampUtc()
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteSettlementButton(
    settlement: Settlement,
    financeType: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    DeleteButton(
        title = "Delete ${settlement.settlementType.text}?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this record?",
        onConfirm = {
            viewModel.deleteSettlement(financeType, settlement)
        }
    )
}

@Composable
fun SettlementDetailDialog(
    settlement: Settlement,
    financeType: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${settlement.settlementType.text} Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(
                        label = "Label",
                        value = settlement.label.ifEmpty { settlement.settlementType.text }
                    )
                    DetailRow(
                        label = "Amount",
                        value = settlement.amount.formatToAmount(),
                        valueColor = colorResource(settlement.settlementType.color)
                    )
                    DetailRow(
                        label = "Date",
                        value = settlement.dateTime.formatToDateTime
                    )
                    DetailRow(
                        label = "Payment",
                        value = settlement.paymentMethod.text
                    )
                    if (settlement.description.isNotEmpty()) {
                        DetailNote(
                            title = "Notes",
                            note = settlement.description
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EditSettlementAmount(
                        settlement = settlement,
                        financeType = financeType
                    )
                    DeleteSettlementButton(
                        settlement = settlement,
                        financeType = financeType
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        "Close",
                        color = StewardTheme.colors.onSurfaceText
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWithdrawalAmount(
    withdrawal: Withdrawal,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    val amountState = rememberTextFieldState(withdrawal.amount.toString())
    val amountDisplay = remember { mutableStateOf(withdrawal.amount.toString()) }
    val descriptionState = rememberTextFieldState(withdrawal.description)
    val descriptionDisplay = remember { mutableStateOf(withdrawal.description) }

    val colorRes = SettlementType.WITHDRAWAL.color
    val getColor = colorResource(colorRes)

    OutlinedIconButton(
        onClick = { showDialog.value = true },
        border = BorderStroke(1.dp, getColor)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Withdrawal",
            tint = getColor,
            modifier = Modifier.size(20.dp)
        )
    }

    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Icon(
                    painter = painterResource(SettlementType.WITHDRAWAL.icon),
                    contentDescription = "Withdrawal",
                    tint = getColor,
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    "Edit Withdrawal Details",
                    fontWeight = FontWeight.Bold,
                    color = getColor,
                    modifier = Modifier.padding(top = 2.dp)
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = colorRes,
                    containerModifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Details...",
                    colorResId = colorRes
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    ModelDrawerButton(
                        text = "Update Withdrawal",
                        colorResId = colorRes,
                        filledColor = getColor.copy(0.2f),
                        textColor = getColor
                    ) {
                        val newAmount = amountDisplay.value.toDoubleOrNull() ?: withdrawal.amount
                        viewModel.updateWithdrawal(
                            oldWithdrawal = withdrawal,
                            newAmount = newAmount,
                            newDescription = descriptionDisplay.value
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteWithdrawalButton(
    withdrawal: Withdrawal,
    viewModel: DetailViewModel = hiltViewModel()
) {
    DeleteButton(
        title = "Delete Withdrawal?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this record?",
        onConfirm = {
            viewModel.deleteWithdrawal(withdrawal)
        }
    )
}

@Composable
fun WithdrawalDetailDialog(
    withdrawal: Withdrawal,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Withdrawal Details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailRow(
                        label = "Label",
                        value = withdrawal.label.ifEmpty { "Withdrawal" }
                    )
                    DetailRow(
                        label = "Amount",
                        value = withdrawal.amount.formatToAmount(),
                        valueColor = colorResource(SettlementType.WITHDRAWAL.color)
                    )
                    DetailRow(
                        label = "Date",
                        value = withdrawal.createdAt.formatToDateTime
                    )
                    DetailRow(
                        label = "From",
                        value = withdrawal.fromPaymentMethod.name
                    )
                    DetailRow(
                        label = "To",
                        value = withdrawal.toPaymentMethod.name
                    )
                    if (withdrawal.description.isNotEmpty()) {
                        DetailRow(
                            label = "Notes",
                            value = withdrawal.description
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EditWithdrawalAmount(
                        withdrawal = withdrawal
                    )
                    DeleteWithdrawalButton(
                        withdrawal = withdrawal
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        "Close",
                        color = StewardTheme.colors.onSurfaceText
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAchievementAmount(
    achievement: Achievement,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    val amountState = rememberTextFieldState(achievement.totalSettlementAmount.toString())
    val amountDisplay = remember { mutableStateOf(achievement.totalSettlementAmount.toString()) }
    val color = remember(
        achievement.status
    ) {
        if (achievement.status == "COMPLETED") R.color.success_complete
        else R.color.error_color
    }
    val getColor = colorResource(color)


    OutlinedIconButton(
        onClick = { showDialog.value = true },
        border = BorderStroke(1.dp, getColor)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Amount",
            tint = getColor,
            modifier = Modifier.size(20.dp)
        )
    }

    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Image(
                    painter = painterResource(R.drawable.achievement),
                    contentDescription = "achievement",
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    "Edit Achievement Amount",
                    fontWeight = FontWeight.Bold,
                    color = getColor,
                    modifier = Modifier.padding(top = 2.dp)
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = color,
                    containerModifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {

                    ModelDrawerButton(
                        text = "Update Amount",
                        colorResId = color,
                        filledColor = getColor.copy(0.2f),
                        textColor = getColor
                    ) {
                        val newAmount = amountDisplay.value.toDoubleOrNull()
                            ?: achievement.totalSettlementAmount
                        viewModel.updateAchievementAmount(achievement, newAmount)
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteAchievementButton(
    achievement: Achievement,
    viewModel: DetailViewModel = hiltViewModel()
) {
    DeleteButton(
        title = "Delete Achievement?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this record?",
        onConfirm = {
            viewModel.deleteAchievement(achievement)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransaction(
    color: Color,
    transactionId: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    val detailState by viewModel.detailState.collectAsState()
    val transaction =
        (detailState.financeEntity as? DataState.Success)?.data as? FinanceEntity.Transaction

    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf("0.0") }
    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val tagIcon = remember { mutableStateOf(TagIcon("", R.drawable.initial)) }
    val paymentMethodState = remember {
        mutableStateOf(
            transaction?.paymentMethod ?: PaymentMethod.CASH
        )
    }
    val topModifier = Modifier.clip(
        RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        )
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        )
    )

    val localDateState = remember { mutableStateOf(LocalDateTime.now()) }


    LaunchedEffect(showDialog.value, transaction) {
        if (showDialog.value && transaction != null) {
            labelState.setTextAndPlaceCursorAtEnd(transaction.label)
            labelDisplay.value = transaction.label
            amountState.setTextAndPlaceCursorAtEnd(transaction.amount.toString())
            amountDisplay.value = transaction.amount.toString()
            descriptionState.setTextAndPlaceCursorAtEnd(transaction.description)
            descriptionDisplay.value = transaction.description
            localDateState.value = transaction.createdAt.toLocalDateTimeUtc()
            tagIcon.value = transaction.tagIcon
        }
    }

    OutlinedIconButton(
        onClick = { showDialog.value = true },
        border = BorderStroke(1.dp, color)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Transaction",
            tint = color
        )
    }


    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Image(
                    painter = painterResource(
                        transaction?.transactionType?.filledIcon ?: R.drawable.initial
                    ),
                    contentDescription = "Transaction",
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    "Edit Transaction Details",
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(top = 2.dp)
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings,
                    containerModifier = topModifier
                )

                ModelDrawerTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = "Title...",
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Notes...",
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings
                )

                DateTimeInput(
                    showTime = remember { mutableStateOf(false) },
                    showDate = remember { mutableStateOf(false) },
                    localDateTimeState = localDateState,
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings
                )

                ModelDrawerTag(
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings,
                    title = "Icon",
                    iconState = tagIcon,
                    containerModifier = bottomModifier
                )

                PaymentMethodDropdown(
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings,
                    selectedPaymentMethod = paymentMethodState
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModelDrawerButton(
                        text = "Update Transaction",
                        colorResId = transaction?.transactionType?.color ?: R.color.Earnings,
                        filledColor = color,
                        textColor = Color.White
                    ) {
                        viewModel.updateTransactionInfo(
                            transactionId = transactionId,
                            label = labelDisplay.value,
                            amount = amountDisplay.value.toDoubleOrNull() ?: 0.0,
                            description = descriptionDisplay.value,
                            tagIcon = tagIcon.value,
                            paymentMethod = paymentMethodState.value,
                            localDate = localDateState.value
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteTransactionButton(
    transaction: FinanceEntity.Transaction,
    viewModel: DetailViewModel = hiltViewModel(),
    onDeleteSuccess: () -> Unit = {}
) {
    DeleteButton(
        title = "Delete Transaction?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this transaction: \"${transaction.label}\"?",
        onConfirm = {
            viewModel.deleteTransaction(transaction.id)
            onDeleteSuccess()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLiability(
    color: Color,
    liabilityId: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    val detailState by viewModel.detailState.collectAsState()
    val liability =
        (detailState.financeEntity as? DataState.Success)?.data as? FinanceEntity.Liability

    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf("0.0") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val tagIcon = remember { mutableStateOf(TagIcon("", R.drawable.initial)) }
    val localDateState = remember { mutableStateOf(LocalDateTime.now()) }
    val isAmountReceived = remember { mutableStateOf(false) }

    val topModifier = Modifier.clip(
        RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        )
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        )
    )


    LaunchedEffect(showDialog.value, liability) {
        if (showDialog.value && liability != null) {
            labelState.setTextAndPlaceCursorAtEnd(liability.label)
            labelDisplay.value = liability.label
            descriptionState.setTextAndPlaceCursorAtEnd(liability.description)
            descriptionDisplay.value = liability.description
            tagIcon.value = liability.tagIcon
            amountState.setTextAndPlaceCursorAtEnd(liability.amount.toString())
            amountDisplay.value = liability.amount.toString()
            localDateState.value = liability.createdAt.toLocalDateTimeUtc()
            isAmountReceived.value = liability.isAmountReceived
        }
    }

    OutlinedIconButton(
        onClick = { showDialog.value = true },
        border = BorderStroke(1.dp, color),
        colors = IconButtonDefaults.iconButtonColors().copy(contentColor = color)
    ) {
        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Liability")
    }

    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Image(
                    painter = painterResource(
                        liability?.liabilityType?.filledIcon ?: R.drawable.initial
                    ),
                    contentDescription = "Liability",
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter
                        .tint(
                            colorResource(
                                liability?.liabilityType?.color ?: R.color.Debt
                            )
                        )
                )

                Text(
                    "Edit Liability Details",
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(top = 2.dp)
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = liability?.liabilityType?.color ?: R.color.Debt,
                    containerModifier = topModifier
                )

                ModelDrawerTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = "Liability Title",
                    colorResId = liability?.liabilityType?.color ?: R.color.Debt
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Notes...",
                    colorResId = liability?.liabilityType?.color ?: R.color.Debt
                )

                DateTimeInput(
                    showTime = remember { mutableStateOf(false) },
                    showDate = remember { mutableStateOf(false) },
                    localDateTimeState = localDateState,
                    colorResId = liability?.liabilityType?.color ?: R.color.Debt
                )

                ModelDrawerTag(
                    colorResId = liability?.liabilityType?.color ?: R.color.Debt,
                    title = "Icon",
                    iconState = tagIcon,
                    containerModifier = if (liability?.liabilityType == LiabilityType.DEBT) Modifier
                    else bottomModifier
                )

                if (liability?.liabilityType == LiabilityType.DEBT) {
                    WasAmountReceived(
                        containerModifier = bottomModifier,
                        isAmountReceived = isAmountReceived,
                        color = colorResource(liability.liabilityType.color)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModelDrawerButton(
                        text = "Update Liability",
                        colorResId = liability?.liabilityType?.color ?: R.color.Debt,
                        filledColor = colorResource(
                            liability?.liabilityType?.color ?: R.color.Debt
                        ),
                        textColor = Color.White
                    ) {
                        viewModel.updateLiabilityInfo(
                            liabilityId = liabilityId,
                            label = labelDisplay.value,
                            description = descriptionDisplay.value,
                            tagIcon = tagIcon.value,
                            isAmountReceived = isAmountReceived.value,
                            localDate = localDateState.value,
                            amount = amountDisplay.value.toDoubleOrNull() ?: 0.0
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteLiabilityButton(
    liability: FinanceEntity.Liability,
    viewModel: DetailViewModel = hiltViewModel(),
    onDeleteSuccess: () -> Unit = {}
) {
    DeleteButton(
        title = "Delete Liability?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this liability: \"${liability.label}\"?",
        onConfirm = {
            viewModel.deleteLiability(liability.id)
            onDeleteSuccess()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSettlement(
    color: Color,
    liabilityId: String,
    liability: FinanceEntity.Liability,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    // Settlement form states
    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf("0.0") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val paymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val dateTime = remember { mutableStateOf(LocalDateTime.now()) }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val topModifier = Modifier.clip(
        RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        )
    )
    val bottomModifier = Modifier.clip(
        RoundedCornerShape(
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        )
    )


    // Determine settlement type based on liability type
    val settlementType = remember(liability.liabilityType) {
        when (liability.liabilityType) {
            LiabilityType.DEBT -> SettlementType.DEBT_REPAY
            LiabilityType.LOAN -> SettlementType.LENT_REPAY
        }
    }

    IconButton(
        onClick = { showDialog.value = true },
        colors = IconButtonDefaults.iconButtonColors().copy(
            containerColor = colorResource(settlementType.color)
        )
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Settlement")
    }

    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    painter = painterResource(settlementType.icon),
                    contentDescription = settlementType.text,
                    tint = colorResource(settlementType.color),
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    "Add ${settlementType.text}",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(settlementType.color),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    settlementType.typeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = settlementType.color,
                    containerModifier = topModifier
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Details (Optional)",
                    colorResId = settlementType.color
                )

                DateTimeInput(
                    showTime = showTimePicker,
                    showDate = showDatePicker,
                    localDateTimeState = dateTime,
                    colorResId = settlementType.color,
                    timeContainerModifier = bottomModifier
                )

                PaymentMethodDropdown(
                    colorResId = settlementType.color,
                    selectedPaymentMethod = paymentMethod
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModelDrawerButton(
                        text = "Add ${settlementType.text}",
                        colorResId = settlementType.color,
                        textColor = colorResource(settlementType.color)
                    ) {
                        viewModel.addLiabilitySettlement(
                            liabilityId = liabilityId,
                            amount = amountDisplay.value.toDoubleOrNull() ?: 0.0,
                            label = settlementType.text,
                            description = descriptionDisplay.value,
                            paymentMethod = paymentMethod.value,
                            dateTime = dateTime.value.toFirestoreTimestampUtc(),
                            tagIcon = liability.tagIcon,
                            settlementType = settlementType
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWithdrawal(
    color: Color,
    transactionId: String,
    viewModel: DetailViewModel = hiltViewModel(),
    currentPaymentMethod: PaymentMethod
) {
    val showDialog = remember { mutableStateOf(false) }

    // Withdrawal form states
    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf("0.0") }
    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val toPaymentMethod = remember {
        mutableStateOf(
            if (currentPaymentMethod == PaymentMethod.CASH) PaymentMethod.CREDIT_CARD else
                PaymentMethod.CASH
        )
    }
    val fromPaymentMethod = remember { mutableStateOf(currentPaymentMethod) }
    val dateTime = remember { mutableStateOf(LocalDateTime.now()) }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    val withdrawalType = SettlementType.WITHDRAWAL

    IconButton(
        onClick = { showDialog.value = true },
        colors = IconButtonDefaults.iconButtonColors().copy(containerColor = color)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Withdrawal")
    }

    if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showDialog.value = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = BottomSheetDefaults.ContainerColor.copy(alpha = 0.98f),
            modifier = Modifier.statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(withdrawalType.icon),
                    contentDescription = withdrawalType.text,
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    "Add ${withdrawalType.text}",
                    fontWeight = FontWeight.Bold,
                    color = colorResource(withdrawalType.color),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    withdrawalType.typeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = withdrawalType.color
                )

                ModelDrawerTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = withdrawalType.text,
                    colorResId = withdrawalType.color
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Details (Optional)",
                    colorResId = withdrawalType.color
                )

                DateTimeInput(
                    showTime = showTimePicker,
                    showDate = showDatePicker,
                    localDateTimeState = dateTime,
                    colorResId = withdrawalType.color
                )

                Text(
                    "From account",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorResource(withdrawalType.color)
                )
                PaymentMethodDropdown(
                    colorResId = withdrawalType.color,
                    selectedPaymentMethod = fromPaymentMethod
                )

                Text(
                    "To account",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorResource(withdrawalType.color)
                )
                PaymentMethodDropdown(
                    colorResId = withdrawalType.color,
                    selectedPaymentMethod = toPaymentMethod
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModelDrawerButton(
                        text = "Add ${withdrawalType.text}",
                        colorResId = withdrawalType.color,
                        textColor = colorResource(withdrawalType.color)
                    ) {
                        viewModel.addWithdrawal(
                            datasetId = transactionId,
                            amount = amountDisplay.value.toDoubleOrNull() ?: 0.0,
                            label = labelDisplay.value.ifEmpty { withdrawalType.text },
                            description = descriptionDisplay.value,
                            toPaymentMethod = toPaymentMethod.value,
                            fromPaymentMethod = fromPaymentMethod.value,
                            dateTime = dateTime.value.toFirestoreTimestampUtc(),
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}
