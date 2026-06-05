// Bless be the name of the LORD of host
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.DoDisturbOn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Achievement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.backend.storage.types.SettlementType
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.helper.toTimestamp
import com.example.moneytracker.ui.detailScreen.DetailViewModel
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.theme.StewardTheme
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

private val FLOAT_BUTTON_SIZE = 45.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionFloatingButton(
    viewModel: HomeViewModel = hiltViewModel(),
    uiState: HomeUiState,
    isLoading: Boolean = false
) {
    val isDatasetBottomSheetOpen = uiState.isDatasetBottomSheetOpen
    val isSettlementBottomSheetOpen = uiState.isSettlementBottomSheetOpen


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = CircleShape, size = FLOAT_BUTTON_SIZE)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .shimmerEffect(shape = CircleShape, size = 43.dp)
            )
        } else {
            FloatingActionButton(
                onClick = {
                    viewModel.updateOnDatasetModelBottomSheetShow(true)
                },
                shape = CircleShape,
                modifier = Modifier.size(FLOAT_BUTTON_SIZE),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp),
                containerColor = StewardTheme.colors.primaryAccent,
            ) {
                Icon(
                    imageVector = if (isDatasetBottomSheetOpen) Icons.Default.Clear else Icons.Default.Add,
                    contentDescription = "Add data",
                    tint = StewardTheme.colors.accentContent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FloatingActionButton(
                onClick = {
                    viewModel.updateOnAdjustModelBottomSheetShow(true)
                },
                shape = CircleShape,
                modifier = Modifier.size(43.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 5.dp),
                containerColor = StewardTheme.colors.primaryAccent
            ) {
                Icon(
                    imageVector = if (isSettlementBottomSheetOpen) Icons.Outlined.DoDisturbOn
                    else Icons.Default.Adjust,
                    contentDescription = "Add settlement",
                    tint = StewardTheme.colors.accentContent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ModelDrawerButton(
    text: String,
    colorResId: Int,
    modifier: Modifier = Modifier,
    icon: Int? = null,
    wasSuccess: MutableState<State>? = null,
    shape: Shape = ButtonDefaults.outlinedShape,
    filledColor: Color? = null,
    textColor: Color? = null,
    fontSize: TextUnit = 15.sp,
    onClick: () -> Unit,
) {
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp

    val color = if (wasSuccess != null && wasSuccess.value == State.ERROR)
        colorResource(R.color.error_color) else
        colorResource(id = colorResId)
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(bottom = 10.dp)
            .height(height),
        colors = ButtonDefaults.outlinedButtonColors().copy(
            contentColor = textColor ?: color,
            containerColor = filledColor ?: color.copy(alpha = 0.2f),
        ),
        shape = shape,
        border = BorderStroke(3.dp, color)
    ) {
        if (icon != null) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = icon,
                    contentDescription = text,
                    modifier = Modifier.size(MODEL_DRAWER_ICON_SIZE)
                )

                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))

                Text(
                    text = text,
                    fontSize = fontSize,
                    fontWeight = FONT_WEIGHT
                )

            }
        } else {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FONT_WEIGHT
            )
        }
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
        colors = IconButtonDefaults.iconButtonColors().copy(containerColor = color)
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    colorResId = R.color.Attain
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
                    colorResId = R.color.Attain
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
                            dateTime = dateTime.value.toTimestamp(),
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

    LaunchedEffect(showDialog.value, goal) {
        if (showDialog.value && goal != null) {
            labelState.setTextAndPlaceCursorAtEnd(goal.label)
            labelDisplay.value = goal.label
            descriptionState.setTextAndPlaceCursorAtEnd(goal.description)
            descriptionDisplay.value = goal.description
            tagIcon.value = goal.tagIcon
        }
    }

    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = color)) {
        OutlinedIconButton(onClick = { showDialog.value = true }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Goal")
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    colorResId = R.color.Goal
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Notes...",
                    colorResId = R.color.Goal
                )

                ModelDrawerTag(
                    colorResId = R.color.Goal,
                    title = "Icon",
                    iconState = tagIcon
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
    var showConfirm by remember { mutableStateOf(false) }

    FilledIconButton(
        onClick = { showConfirm = true },
        colors = IconButtonDefaults.filledIconButtonColors().copy(
            containerColor = colorResource(R.color.error_color)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Achievement",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(20.dp)
        )
    }

    if (showConfirm) {
        Dialog(
            onDismissRequest = { showConfirm = false }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Delete Achievement?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "This action cannot be undone. Are you sure you want to delete this record?",
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = { showConfirm = false }) {
                            Text(
                                "Cancel",
                                color = StewardTheme.colors.onSurfaceText
                            )
                        }
                        TextButton(
                            onClick = {
                                viewModel.deleteAchievement(achievement)
                                showConfirm = false
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colorResource(R.color.error_color)
                            )
                        ) {
                            Text("Delete", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
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

    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val tagIcon = remember { mutableStateOf(TagIcon("", R.drawable.initial)) }

    LaunchedEffect(showDialog.value, transaction) {
        if (showDialog.value && transaction != null) {
            labelState.setTextAndPlaceCursorAtEnd(transaction.label)
            labelDisplay.value = transaction.label
            descriptionState.setTextAndPlaceCursorAtEnd(transaction.description)
            descriptionDisplay.value = transaction.description
            tagIcon.value = transaction.tagIcon
        }
    }

    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = color)) {
        OutlinedIconButton(onClick = { showDialog.value = true }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Transaction")
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(
                        if (transaction != null) transaction.transactionType.filledIcon else R.drawable.initial
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

                ModelDrawerTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = "Transaction Title",
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings
                )

                ModelDrawerTextField(
                    title = "Description",
                    state = descriptionState,
                    displayText = descriptionDisplay,
                    placeholder = "Notes...",
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings
                )

                ModelDrawerTag(
                    colorResId = transaction?.transactionType?.color ?: R.color.Earnings,
                    title = "Icon",
                    iconState = tagIcon
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
                            description = descriptionDisplay.value,
                            tagIcon = tagIcon.value
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
    var showConfirm by remember { mutableStateOf(false) }
    transaction.transactionType.color

    FilledIconButton(
        onClick = { showConfirm = true },
        colors = IconButtonDefaults.filledIconButtonColors().copy(
            containerColor = colorResource(R.color.error_color)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Transaction",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(20.dp)
        )
    }

    if (showConfirm) {
        Dialog(
            onDismissRequest = { showConfirm = false }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Delete Transaction?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "This action cannot be undone. Are you sure you want to delete this transaction: \"${transaction.label}\"?",
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = { showConfirm = false }) {
                            Text(
                                "Cancel",
                                color = StewardTheme.colors.onSurfaceText
                            )
                        }
                        TextButton(
                            onClick = {
                                viewModel.deleteTransaction(transaction.id)
                                showConfirm = false
                                onDeleteSuccess()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colorResource(R.color.error_color)
                            )
                        ) {
                            Text("Delete", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
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
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val tagIcon = remember { mutableStateOf(TagIcon("", R.drawable.initial)) }
    val isAmountReceived = remember { mutableStateOf(false) }

    LaunchedEffect(showDialog.value, liability) {
        if (showDialog.value && liability != null) {
            labelState.setTextAndPlaceCursorAtEnd(liability.label)
            labelDisplay.value = liability.label
            descriptionState.setTextAndPlaceCursorAtEnd(liability.description)
            descriptionDisplay.value = liability.description
            tagIcon.value = liability.tagIcon
            isAmountReceived.value = liability.isAmountReceived
        }
    }

    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = color)) {
        OutlinedIconButton(onClick = { showDialog.value = true }) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Liability")
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(
                        if (liability != null) liability.liabilityType.filledIcon else R.drawable.initial
                    ),
                    contentDescription = "Liability",
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    "Edit Liability Details",
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(top = 2.dp)
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

                ModelDrawerTag(
                    colorResId = liability?.liabilityType?.color ?: R.color.Debt,
                    title = "Icon",
                    iconState = tagIcon
                )

                if (liability?.liabilityType == com.example.moneytracker.backend.storage.types.LiabilityType.DEBT) {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable {
                                isAmountReceived.value = !isAmountReceived.value
                            },
                        headlineContent = {
                            Text(
                                "Amount Received",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        supportingContent = {
                            Text(
                                if (isAmountReceived.value) "Yes" else "No",
                                color = if (isAmountReceived.value) colorResource(R.color.success_complete) else Color.Gray
                            )
                        },
                        trailingContent = {
                            androidx.compose.material3.Switch(
                                checked = isAmountReceived.value,
                                onCheckedChange = { isAmountReceived.value = it }
                            )
                        }
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
                        filledColor = color,
                        textColor = Color.White
                    ) {
                        viewModel.updateLiabilityInfo(
                            liabilityId = liabilityId,
                            label = labelDisplay.value,
                            description = descriptionDisplay.value,
                            tagIcon = tagIcon.value,
                            isAmountReceived = isAmountReceived.value
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
    var showConfirm by remember { mutableStateOf(false) }

    FilledIconButton(
        onClick = { showConfirm = true },
        colors = IconButtonDefaults.filledIconButtonColors().copy(
            containerColor = colorResource(R.color.error_color)
        )
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete Liability",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(20.dp)
        )
    }

    if (showConfirm) {
        Dialog(
            onDismissRequest = { showConfirm = false }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Delete Liability?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "This action cannot be undone. Are you sure you want to delete this liability: \"${liability.label}\"?",
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = { showConfirm = false }) {
                            Text(
                                "Cancel",
                                color = StewardTheme.colors.onSurfaceText
                            )
                        }
                        TextButton(
                            onClick = {
                                viewModel.deleteLiability(liability.id)
                                showConfirm = false
                                onDeleteSuccess()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = colorResource(R.color.error_color)
                            )
                        ) {
                            Text("Delete", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
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
    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val paymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
    val dateTime = remember { mutableStateOf(LocalDateTime.now()) }
    val tagIcon = remember { mutableStateOf(TagIcon("", R.drawable.initial)) }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    // Determine settlement type based on liability type
    val settlementType = remember(liability.liabilityType) {
        when (liability.liabilityType) {
            com.example.moneytracker.backend.storage.types.LiabilityType.DEBT -> SettlementType.DEBT_REPAY
            com.example.moneytracker.backend.storage.types.LiabilityType.LOAN -> SettlementType.LENT_REPAY
        }
    }

    IconButton(
        onClick = { showDialog.value = true },
        colors = IconButtonDefaults.iconButtonColors().copy(containerColor = color)
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    colorResId = settlementType.color
                )

                ModelDrawerTextField(
                    title = "Label",
                    state = labelState,
                    displayText = labelDisplay,
                    placeholder = settlementType.text,
                    colorResId = settlementType.color
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
                    colorResId = settlementType.color
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
                            label = labelDisplay.value.ifEmpty { settlementType.text },
                            description = descriptionDisplay.value,
                            paymentMethod = paymentMethod.value,
                            dateTime = dateTime.value.toTimestamp(),
                            tagIcon = tagIcon.value,
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
    viewModel: DetailViewModel = hiltViewModel()
) {
    val showDialog = remember { mutableStateOf(false) }

    // Withdrawal form states
    val amountState = rememberTextFieldState()
    val amountDisplay = remember { mutableStateOf("0.0") }
    val labelState = rememberTextFieldState()
    val labelDisplay = remember { mutableStateOf("") }
    val descriptionState = rememberTextFieldState()
    val descriptionDisplay = remember { mutableStateOf("") }
    val toPaymentMethod = remember { mutableStateOf(PaymentMethod.CREDIT_CARD) }
    val fromPaymentMethod = remember { mutableStateOf(PaymentMethod.CASH) }
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
                            dateTime = dateTime.value.toTimestamp()
                        )
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

