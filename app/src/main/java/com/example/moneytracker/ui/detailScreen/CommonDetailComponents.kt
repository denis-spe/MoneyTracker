// Bless be the name of LORD of hosts and to Lamb of GOD
package com.example.moneytracker.ui.detailScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.moneytracker.helper.InputState
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.formatToDateTime
import com.example.moneytracker.helper.formatValueOnly
import com.example.moneytracker.helper.isAmountValid
import com.example.moneytracker.helper.isLabelValid
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.example.moneytracker.ui.components.DeleteDialog
import com.example.moneytracker.ui.dataAddition.AffectCurrentAccount
import com.example.moneytracker.ui.dataAddition.DateTimeInput
import com.example.moneytracker.ui.dataAddition.ModelDrawerAmountField
import com.example.moneytracker.ui.dataAddition.ModelDrawerButton
import com.example.moneytracker.ui.dataAddition.ModelDrawerDescriptionTextField
import com.example.moneytracker.ui.dataAddition.ModelDrawerLabelTextField
import com.example.moneytracker.ui.dataAddition.ModelDrawerTag
import com.example.moneytracker.ui.dataAddition.PaymentMethodDropdown
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.theme.StewardTheme
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now


@Composable
fun SettlementItem(
    modifier: Modifier = Modifier,
    settlement: Settlement,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = settlement.label.ifEmpty { settlement.dateTime.formatToDateTime },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = settlement.amount.formatToAmount(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: ${settlement.dateTime.formatToDateTime}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

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

enum class DetailButtonType {
    OUTLINE, FILLED, ICON_TEXT, ICON_ONLY
}

@Composable
fun DetailButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    detailButtonType: DetailButtonType,
    onClick: () -> Unit
) {
    when (detailButtonType) {
        DetailButtonType.OUTLINE -> {
            OutlinedButton(
                onClick = onClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = contentColor
                ),
                border = BorderStroke(1.dp, containerColor),
                modifier = modifier,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = contentColor
                )
            }
        }

        DetailButtonType.FILLED -> {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor
                ),
                modifier = modifier,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = contentColor
                )
            }
        }

        DetailButtonType.ICON_TEXT -> {
            TextButton(
                onClick = onClick,
                shape = RoundedCornerShape(5.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(35.dp)
                            .border(1.dp, containerColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = contentColor,
                            modifier = Modifier.padding(5.dp)
                        )
                    }

                    Text(
                        text = label,
                        color = contentColor
                    )
                }
            }
        }

        DetailButtonType.ICON_ONLY -> {
            OutlinedIconButton(
                onClick = onClick,
                colors = IconButtonDefaults.outlinedIconButtonColors(
                    contentColor = contentColor
                ),
                border = BorderStroke(1.dp, containerColor),
                modifier = modifier
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = contentColor
                )
            }
        }
    }
}

@Composable
fun DeleteButton(
    title: String,
    paragraph: String,
    label: String,
    containerColor: Color = colorResource(R.color.error_color),
    contentColor: Color = Color.White,
    detailButtonType: DetailButtonType = DetailButtonType.OUTLINE,
    onConfirm: () -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }

    DetailButton(
        label = label,
        icon = Icons.Default.Delete,
        containerColor = containerColor,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) contentColor else containerColor,
        detailButtonType = detailButtonType,
        onClick = { showDialog.value = true }
    )

    DeleteDialog(
        showDialog = showDialog,
        title = title,
        paragraph = paragraph,
        onConfirm = onConfirm
    )
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
    label: String = "Attainment",
    detailButtonType: DetailButtonType = DetailButtonType.FILLED,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    val detailState by viewModel.detailState.collectAsState()
    val goal = (detailState.financeEntity as? DataState.Success)?.data as? FinanceEntity.Goal

    // States for the dialog
    val amountState = rememberTextFieldState()
    val remainingAmount = goal?.remainingAmount ?: 0.0
    val amountDisplay = remember { mutableStateOf(remainingAmount.toString()) }
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

    val showTimePicker = remember { mutableStateOf(false) }

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }
    val showDatePicker = remember { mutableStateOf(false) }

    // Initialize values if goal is ready
    LaunchedEffect(showDialog.value, goal) {
        if (showDialog.value && goal != null) {
            labelState.setTextAndPlaceCursorAtEnd(goal.label)
            labelDisplay.value = goal.label
            tagIcon.value = goal.tagIcon
            wasAmountSuccess.value = InputState.Initial
        }
    }

    DetailButton(
        label = label,
        icon = Icons.Default.Add,
        containerColor = color,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else color,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                    text = "Attain",
                    color = colorResource(R.color.Attain),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Attain your goal",
                    color = Color.Gray, fontSize = 12.sp
                )


                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = remainingAmount.formatValueOnly(),
                    colorResId = R.color.Attain,
                    modifier = topModifier,
                    wasSuccess = wasAmountSuccess
                )

                ModelDrawerDescriptionTextField(
                    title = "Description",
                    description = "Take a note",
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
                        if (!wasAmountSuccess.isAmountValid(amountDisplay.value.toDoubleOrNull())) return@ModelDrawerButton

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
    label: String = "Edit",
    detailButtonType: DetailButtonType = DetailButtonType.FILLED,
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

    val wasLabelSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }


    LaunchedEffect(showDialog.value, goal) {
        if (showDialog.value && goal != null) {
            labelState.setTextAndPlaceCursorAtEnd(goal.label)
            labelDisplay.value = goal.label
            descriptionState.setTextAndPlaceCursorAtEnd(goal.description)
            descriptionDisplay.value = goal.description
            localDateState.value = goal.createdAt.toLocalDateTimeUtc()
            paymentMethod.value = goal.paymentMethod
            tagIcon.value = goal.tagIcon
            wasLabelSuccess.value = InputState.Initial
        }
    }

    DetailButton(
        label = label,
        icon = Icons.Default.Edit,
        containerColor = color,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else color,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                    text = "Goal",
                    color = colorResource(R.color.Goal),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Edit Goal Details",
                    color = Color.Gray, fontSize = 12.sp
                )


                ModelDrawerLabelTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = "Goal Title",
                    colorResId = R.color.Goal,
                    modifier = topModifier,
                    wasSuccess = wasLabelSuccess
                )

                ModelDrawerDescriptionTextField(
                    title = "Description",
                    description = "Take a note",
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
                    modifier = bottomModifier
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
                        if (!wasLabelSuccess.isLabelValid(labelDisplay.value)) return@ModelDrawerButton

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
    label: String = "Edit",
    detailButtonType: DetailButtonType = DetailButtonType.ICON_ONLY,
    viewModel: DetailViewModel = hiltViewModel(),
    onUpdateSuccess: () -> Unit = {}
) {
    val showDialog = remember { mutableStateOf(false) }

    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf(settlement.amount.toString()) }
    val descriptionState = rememberTextFieldState(settlement.description)
    val descriptionDisplay = remember { mutableStateOf(settlement.description) }
    val localDateState = remember { mutableStateOf(settlement.dateTime.toLocalDateTimeUtc()) }
    val affectCurrentAccount = remember { mutableStateOf(false) }

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

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

    DetailButton(
        label = label,
        icon = Icons.Default.Edit,
        containerColor = getColor,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else getColor,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                    text = "Edit ${settlement.settlementType.text} Details",
                    color = getColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Modify the amount and details",
                    color = Color.Gray, fontSize = 12.sp
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = settlement.amount.formatValueOnly(),
                    colorResId = colorRes,
                    modifier = topModifier,
                    wasSuccess = wasAmountSuccess,
                    clearOnCancel = true
                )

                ModelDrawerDescriptionTextField(
                    title = "Description",
                    description = "Take a note",
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
                )

                AffectCurrentAccount(
                    color = colorResource(colorRes),
                    modifier = bottomModifier,
                    label = when (settlement.settlementType) {
                        SettlementType.DEBT_REPAY -> "Was Debt Repaid"
                        SettlementType.LENT_REPAY -> "Was Lent Repaid"
                        else -> "Affect Current Account"
                    },
                    affectCurrentAccountState = affectCurrentAccount
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
                        if (!wasAmountSuccess.isAmountValid(amountDisplay.value.toDoubleOrNull())) return@ModelDrawerButton

                        val newAmount = amountDisplay.value.toDoubleOrNull() ?: settlement.amount
                        viewModel.updateSettlement(
                            financeType = financeType,
                            oldSettlement = settlement,
                            newAmount = newAmount,
                            newDescription = descriptionDisplay.value,
                            localDate = localDateState.value.toFirestoreTimestampUtc(),
                            affectCurrentAccount = affectCurrentAccount.value
                        )
                        showDialog.value = false
                        onUpdateSuccess()
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
    viewModel: DetailViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    DeleteButton(
        title = "Delete ${settlement.settlementType.text}?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this record?",
        label = "Delete",
        detailButtonType = DetailButtonType.ICON_ONLY,
        onConfirm = {
            viewModel.deleteSettlement(financeType, settlement)
            onDismiss()
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
        Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        painter = painterResource(settlement.settlementType.icon),
                        contentDescription = settlement.settlementType.text,
                        tint = colorResource(settlement.settlementType.color),
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "${settlement.settlementType.text} Details",
                        color = colorResource(settlement.settlementType.color),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "View settlement details",
                        color = Color.Gray, fontSize = 12.sp
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                        financeType = financeType,
                        onUpdateSuccess = onDismiss
                    )
                    DeleteSettlementButton(
                        settlement = settlement,
                        financeType = financeType,
                        onDismiss = onDismiss
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
    label: String = "Edit",
    detailButtonType: DetailButtonType = DetailButtonType.ICON_ONLY,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf(withdrawal.amount.toString()) }
    val descriptionState = rememberTextFieldState(withdrawal.description)
    val descriptionDisplay = remember { mutableStateOf(withdrawal.description) }
    val affectCurrentAccount = remember { mutableStateOf(withdrawal.affectCurrentAccount) }

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

    val colorRes = SettlementType.WITHDRAWAL.color
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

    DetailButton(
        label = label,
        icon = Icons.Default.Edit,
        containerColor = getColor,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else getColor,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                    painter = painterResource(SettlementType.WITHDRAWAL.icon),
                    contentDescription = "Withdrawal",
                    tint = getColor,
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    text = "Edit Withdrawal Details",
                    color = getColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Modify withdrawal amount and details",
                    color = Color.Gray, fontSize = 12.sp
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = withdrawal.amount.formatValueOnly(),
                    colorResId = colorRes,
                    modifier = topModifier,
                    wasSuccess = wasAmountSuccess,
                    clearOnCancel = true
                )

                ModelDrawerDescriptionTextField(
                    title = "Description",
                    description = "Take a note",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Details...",
                    colorResId = colorRes
                )

                AffectCurrentAccount(
                    color = getColor,
                    modifier = bottomModifier,
                    label = "Affect Current Account",
                    affectCurrentAccountState = affectCurrentAccount
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
                        if (!wasAmountSuccess.isAmountValid(amountDisplay.value.toDoubleOrNull())) return@ModelDrawerButton

                        val newAmount = amountDisplay.value.toDoubleOrNull() ?: withdrawal.amount
                        viewModel.updateWithdrawal(
                            oldWithdrawal = withdrawal,
                            newAmount = newAmount,
                            newDescription = descriptionDisplay.value,
                            affectCurrentAccount = affectCurrentAccount.value
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
        label = "Delete",
        detailButtonType = DetailButtonType.ICON_ONLY,
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
        Card(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        painter = painterResource(SettlementType.WITHDRAWAL.icon),
                        contentDescription = "Withdrawal",
                        tint = colorResource(SettlementType.WITHDRAWAL.color),
                        modifier = Modifier.size(30.dp)
                    )

                    Text(
                        text = "Withdrawal Details",
                        color = colorResource(SettlementType.WITHDRAWAL.color),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )

                    Text(
                        text = "View withdrawal details",
                        color = Color.Gray, fontSize = 12.sp
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                        value = withdrawal.fromPaymentMethod.text
                    )
                    DetailRow(
                        label = "To",
                        value = withdrawal.toPaymentMethod.text
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
    label: String = "Edit",
    detailButtonType: DetailButtonType = DetailButtonType.ICON_ONLY,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf(achievement.totalSettlementAmount.toString()) }
    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

    val color = remember(
        achievement.status
    ) {
        if (achievement.status == "COMPLETED") R.color.success_complete
        else R.color.error_color
    }
    val getColor = colorResource(color)


    DetailButton(
        label = label,
        icon = Icons.Default.Edit,
        containerColor = getColor,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else getColor,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                    text = "Achievement",
                    color = getColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Edit Achievement Amount",
                    color = Color.Gray, fontSize = 12.sp
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = achievement.totalSettlementAmount.formatValueOnly(),
                    colorResId = color,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp)),
                    wasSuccess = wasAmountSuccess,
                    clearOnCancel = true
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
                        if (!wasAmountSuccess.isAmountValid(amountDisplay.value.toDoubleOrNull())) return@ModelDrawerButton

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
    viewModel: DetailViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    DeleteButton(
        title = "Delete Achievement?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this record?",
        label = "Delete",
        detailButtonType = DetailButtonType.ICON_ONLY,
        onConfirm = {
            viewModel.deleteAchievement(achievement)
            onDismiss()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransaction(
    color: Color,
    transactionId: String,
    label: String = "Edit",
    detailButtonType: DetailButtonType = DetailButtonType.FILLED,
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
    val affectCurrentAccount =
        remember { mutableStateOf(transaction?.affectCurrentAccount ?: false) }
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

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }
    val wasLabelSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

    val localDateState = remember { mutableStateOf(LocalDateTime.now()) }


    LaunchedEffect(showDialog.value, transaction) {
        if (showDialog.value && transaction != null) {
            labelState.setTextAndPlaceCursorAtEnd(transaction.label)
            labelDisplay.value = transaction.label
            amountDisplay.value = transaction.amount.toString()
            descriptionState.setTextAndPlaceCursorAtEnd(transaction.description)
            descriptionDisplay.value = transaction.description
            localDateState.value = transaction.createdAt.toLocalDateTimeUtc()
            tagIcon.value = transaction.tagIcon
            affectCurrentAccount.value = transaction.affectCurrentAccount
            wasAmountSuccess.value = InputState.Initial
            wasLabelSuccess.value = InputState.Initial
        }
    }

    DetailButton(
        label = label,
        icon = Icons.Default.Edit,
        containerColor = color,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else color,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(
                        colorResource(
                            transaction?.transactionType?.color ?: R.color.Earnings
                        )
                    )
                )

                Text(
                    text = "Edit Transaction Details",
                    color = color,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Modify transaction details",
                    color = Color.Gray, fontSize = 12.sp
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = transaction?.amount?.formatValueOnly() ?: "0.0",
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings,
                    modifier = topModifier,
                    wasSuccess = wasAmountSuccess,
                    clearOnCancel = true
                )

                ModelDrawerLabelTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = "Title...",
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings,
                    wasSuccess = wasLabelSuccess
                )

                ModelDrawerDescriptionTextField(
                    title = "Description",
                    description = "Take a note",
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
                    iconState = tagIcon
                )

                AffectCurrentAccount(
                    color = color,
                    modifier = bottomModifier,
                    label = "Affect Current Account",
                    affectCurrentAccountState = affectCurrentAccount
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
                        val isAmountOk =
                            wasAmountSuccess.isAmountValid(amountDisplay.value.toDoubleOrNull())
                        val isLabelOk = wasLabelSuccess.isLabelValid(labelDisplay.value)

                        if (!isAmountOk || !isLabelOk) return@ModelDrawerButton

                        viewModel.updateTransactionInfo(
                            transactionId = transactionId,
                            label = labelDisplay.value,
                            amount = amountDisplay.value.toDoubleOrNull() ?: 0.0,
                            description = descriptionDisplay.value,
                            tagIcon = tagIcon.value,
                            paymentMethod = paymentMethodState.value,
                            localDate = localDateState.value,
                            affectCurrentAccount = affectCurrentAccount.value
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
    label: String = "Delete",
    detailButtonType: DetailButtonType = DetailButtonType.OUTLINE,
    viewModel: DetailViewModel = hiltViewModel(),
    onDeleteSuccess: () -> Unit = {}
) {
    DeleteButton(
        title = "Delete Transaction?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this transaction: \"${transaction.label}\"?",
        label = label,
        detailButtonType = detailButtonType,
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
    label: String = "Edit",
    detailButtonType: DetailButtonType = DetailButtonType.FILLED,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }
    val detailState by viewModel.detailState.collectAsState()
    val liability =
        (detailState.financeEntity as? DataState.Success)?.data as? FinanceEntity.Liability
            ?: return

    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf(liability.amount.toString()) }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val tagIcon = remember { mutableStateOf(TagIcon("", R.drawable.initial)) }
    val localDateState = remember { mutableStateOf(LocalDateTime.now()) }
    val affectCurrentAccountState = remember { mutableStateOf(false) }

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

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }
    val wasLabelSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }


    LaunchedEffect(showDialog.value, liability) {
        if (showDialog.value) {
            labelState.setTextAndPlaceCursorAtEnd(liability.label)
            labelDisplay.value = liability.label
            descriptionState.setTextAndPlaceCursorAtEnd(liability.description)
            descriptionDisplay.value = liability.description
            tagIcon.value = liability.tagIcon
            localDateState.value = liability.createdAt.toLocalDateTimeUtc()
            affectCurrentAccountState.value = liability.affectCurrentAccount
            wasAmountSuccess.value = InputState.Initial
            wasLabelSuccess.value = InputState.Initial
        }
    }

    DetailButton(
        label = label,
        icon = Icons.Default.Edit,
        containerColor = color,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else color,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                        liability.liabilityType.filledIcon
                    ),
                    contentDescription = "Liability",
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter
                        .tint(
                            colorResource(
                                liability.liabilityType.color
                            )
                        )
                )

                Text(
                    text = "Edit Liability Details",
                    color = color,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = "Modify liability details",
                    color = Color.Gray, fontSize = 12.sp
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = liability.amount.toString(),
                    colorResId = liability.liabilityType.color,
                    modifier = topModifier,
                    wasSuccess = wasAmountSuccess,
                    clearOnCancel = true
                )

                ModelDrawerLabelTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = "Liability Title",
                    colorResId = liability.liabilityType.color,
                    wasSuccess = wasLabelSuccess
                )

                ModelDrawerDescriptionTextField(
                    title = "Description",
                    description = "Take a note",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Notes...",
                    colorResId = liability.liabilityType.color
                )

                DateTimeInput(
                    showTime = remember { mutableStateOf(false) },
                    showDate = remember { mutableStateOf(false) },
                    localDateTimeState = localDateState,
                    colorResId = liability.liabilityType.color
                )

                ModelDrawerTag(
                    colorResId = liability.liabilityType.color,
                    title = "Icon",
                    iconState = tagIcon,
                )

                AffectCurrentAccount(
                    label = when (liability.liabilityType) {
                        LiabilityType.DEBT -> "Was Amount Received"
                        LiabilityType.LOAN -> "Was Amount Paid"
                    },
                    modifier = bottomModifier,
                    affectCurrentAccountState = affectCurrentAccountState,
                    color = colorResource(liability.liabilityType.color)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ModelDrawerButton(
                        text = "Update Liability",
                        colorResId = liability.liabilityType.color,
                        filledColor = colorResource(
                            liability.liabilityType.color
                        ),
                        textColor = Color.White
                    ) {
                        val isAmountOk =
                            wasAmountSuccess.isAmountValid(amountDisplay.value.toDoubleOrNull())
                        val isLabelOk = wasLabelSuccess.isLabelValid(labelDisplay.value)

                        if (!isAmountOk || !isLabelOk) return@ModelDrawerButton

                        viewModel.updateLiabilityInfo(
                            liabilityId = liabilityId,
                            label = labelDisplay.value,
                            description = descriptionDisplay.value,
                            tagIcon = tagIcon.value,
                            isAmountReceived = affectCurrentAccountState.value,
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
    label: String = "Delete",
    detailButtonType: DetailButtonType = DetailButtonType.OUTLINE,
    viewModel: DetailViewModel = hiltViewModel(),
    onDeleteSuccess: () -> Unit = {}
) {
    DeleteButton(
        title = "Delete Liability?",
        paragraph = "This action cannot be undone. Are you sure you want to delete this liability: \"${liability.label}\"?",
        label = label,
        detailButtonType = detailButtonType,
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
    label: String = "Add Settlement",
    detailButtonType: DetailButtonType = DetailButtonType.FILLED,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    // Settlement form states
    val amountState = rememberTextFieldState()
    val remainingAmountState = remember { mutableStateOf(liability.remainingAmount) }
    val amountDisplay = remember { mutableStateOf(remainingAmountState.value.toString()) }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val paymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val dateTime = remember { mutableStateOf(LocalDateTime.now()) }
    val affectCurrentAccount = remember { mutableStateOf(true) }

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

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

    DetailButton(
        label = label,
        icon = Icons.Default.Add,
        containerColor = color,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else color,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                    text = "Add ${settlementType.text}",
                    color = colorResource(settlementType.color),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = settlementType.typeDescription,
                    color = Color.Gray, fontSize = 12.sp
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = remainingAmountState.value.formatValueOnly(),
                    colorResId = settlementType.color,
                    modifier = topModifier,
                    wasSuccess = wasAmountSuccess
                )

                ModelDrawerDescriptionTextField(
                    title = "Description",
                    description = "Take a note",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Details (Optional)",
                    colorResId = settlementType.color
                )

                DateTimeInput(
                    showTime = showTimePicker,
                    showDate = showDatePicker,
                    localDateTimeState = dateTime,
                    colorResId = settlementType.color
                )

                AffectCurrentAccount(
                    color = colorResource(settlementType.color),
                    modifier = bottomModifier,
                    label = when (settlementType) {
                        SettlementType.DEBT_REPAY -> "Was Debt Repaid"
                        SettlementType.LENT_REPAY -> "Was Lent Repaid"
                        else -> "Affect Current Account"
                    },
                    affectCurrentAccountState = affectCurrentAccount
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
                        if (!wasAmountSuccess.isAmountValid(amountDisplay.value.toDoubleOrNull())) return@ModelDrawerButton

                        viewModel.addLiabilitySettlement(
                            liabilityId = liabilityId,
                            amount = amountDisplay.value.toDoubleOrNull() ?: 0.0,
                            label = settlementType.text,
                            description = descriptionDisplay.value,
                            paymentMethod = paymentMethod.value,
                            dateTime = dateTime.value.toFirestoreTimestampUtc(),
                            tagIcon = liability.tagIcon,
                            settlementType = settlementType,
                            affectCurrentAccount = affectCurrentAccount.value
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
    label: String = "Add Withdrawal",
    detailButtonType: DetailButtonType = DetailButtonType.FILLED,
    viewModel: DetailViewModel = hiltViewModel(),
    currentPaymentMethod: PaymentMethod
) {
    val showDialog = remember { mutableStateOf(false) }

    // Withdrawal form states
    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf("0.0") }
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
    val affectCurrentAccount = remember { mutableStateOf(true) }

    val wasAmountSuccess = remember { mutableStateOf<InputState>(InputState.Initial) }

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

    val withdrawalType = SettlementType.WITHDRAWAL

    DetailButton(
        label = label,
        icon = Icons.Default.Add,
        containerColor = color,
        contentColor = if (detailButtonType == DetailButtonType.FILLED) Color.White else color,
        detailButtonType = detailButtonType
    ) {
        showDialog.value = true
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
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(
                        colorResource(withdrawalType.color)
                    )
                )

                Text(
                    text = "Add ${withdrawalType.text}",
                    color = colorResource(withdrawalType.color),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = withdrawalType.typeDescription,
                    color = Color.Gray, fontSize = 12.sp
                )

                ModelDrawerAmountField(
                    state = amountState,
                    displayState = amountDisplay,
                    placeholder = "0.0",
                    colorResId = withdrawalType.color,
                    modifier = topModifier,
                    wasSuccess = wasAmountSuccess
                )

                ModelDrawerDescriptionTextField(
                    title = "Description",
                    description = "Take a note",
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

                AffectCurrentAccount(
                    color = colorResource(withdrawalType.color),
                    modifier = bottomModifier,
                    label = "Affect Current Account",
                    affectCurrentAccountState = affectCurrentAccount
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
                        val isAmountOk =
                            wasAmountSuccess.isAmountValid(amountDisplay.value.toDoubleOrNull())

                        if (!isAmountOk) return@ModelDrawerButton

                        viewModel.addWithdrawal(
                            datasetId = transactionId,
                            amount = amountDisplay.value.toDoubleOrNull() ?: 0.0,
                            description = descriptionDisplay.value,
                            toPaymentMethod = toPaymentMethod.value,
                            fromPaymentMethod = fromPaymentMethod.value,
                            dateTime = dateTime.value.toFirestoreTimestampUtc(),
                            affectCurrentAccount = affectCurrentAccount.value
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}
