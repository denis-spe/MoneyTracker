// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.toFirestoreTimestampUtc
import com.example.moneytracker.ui.components.Current
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now

private val MODEL_DRAWER_ICON_SIZE = 25.dp
val FONT_WEIGHT = FontWeight.Bold



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionModelDrawer(
    onModelBottomSheetShowValue: Boolean,
    onModelBottomSheetShow: (Boolean) -> Unit,
    viewModel: HomeScreenViewModel,
) {
    val tabToDisplay = remember { mutableStateOf(DataType.EARNINGS) }

    // Create a remembered sheet state so the sheet (and its content) is pre-composed.
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    if (onModelBottomSheetShowValue)
        ModalBottomSheet(
            onDismissRequest = {
                // If the user dismisses the sheet via gesture or scrim tap, update the flag.
                onModelBottomSheetShow(false)
            },
            sheetState = sheetState,
        ) {
            DataAdditionModelDrawerTopTitle(tabToDisplay)
            DataAdditionModelDrawerContent(tabToDisplay, viewModel)
        }
}

@Composable
fun DataAdditionModelDrawerTopTitle(tabToDisplay: MutableState<DataType>) {
    val earningsColor = colorResource(R.color.Earnings)
    val expenseColor = colorResource(R.color.Expense)
    val debtColor = colorResource(R.color.Debt)
    val lentColor = colorResource(R.color.Lent)
    val savingsColor = colorResource(R.color.Savings)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Earnings
        TextButton(
            onClick = {
                tabToDisplay.value = DataType.EARNINGS
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Earnings",
                    color = earningsColor,
                    fontWeight = FONT_WEIGHT
                )
                if (tabToDisplay.value == DataType.EARNINGS)
                    Current(earningsColor)
            }

        }

        // Expense
        TextButton(
            onClick = {
                tabToDisplay.value = DataType.EXPENSE
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Expense",
                    color = expenseColor,
                    fontWeight = FONT_WEIGHT
                )
                if (tabToDisplay.value == DataType.EXPENSE)
                    Current(expenseColor)
            }
        }

        // Debt
        TextButton(
            onClick = {
                tabToDisplay.value = DataType.DEBT
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Debt",
                    color = debtColor,
                    fontWeight = FONT_WEIGHT
                )
                if (tabToDisplay.value == DataType.DEBT)
                    Current(debtColor)
            }
        }

        // Lent
        TextButton(
            onClick = {
                tabToDisplay.value = DataType.LENT
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Lent",
                    color = lentColor,
                    fontWeight = FONT_WEIGHT
                )
                if (tabToDisplay.value == DataType.LENT)
                    Current(lentColor)
            }
        }

        // Savings
        TextButton(
            onClick = {
                tabToDisplay.value = DataType.SAVINGS
            }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Savings",
                    color = savingsColor,
                    fontWeight = FONT_WEIGHT
                )
                if (tabToDisplay.value == DataType.SAVINGS)
                    Current(savingsColor)
            }
        }
    }
}

@Composable
fun DataAdditionModelDrawerContent(
    tabToDisplay: MutableState<DataType>,
    viewModel: HomeScreenViewModel
) {
    Column {
        when (tabToDisplay.value) {
            DataType.EARNINGS -> {
                EarningsModelDrawerContent(viewModel)
            }

            DataType.EXPENSE -> {
                ExpenseModelDrawerContent(viewModel)
            }

            DataType.DEBT -> {
                DebtModelDrawerContent(viewModel)
            }

            DataType.LENT -> {
                LentModelDrawerContent(viewModel)
            }

            DataType.SAVINGS -> {
                SavingsModelDrawerContent(viewModel)
            }

            DataType.REPAY -> {
                RepayModelDrawerContent(viewModel)
            }

        }
    }
}

@Composable
fun ModelDrawerContent(
    colorResId: Int,
    icon: Int,
    dataType: DataType,
    description: String,
    buttonText: String,
    viewModel: HomeScreenViewModel,
) {

    val showDateTime = remember { mutableStateOf(false) }
    val localDateTimeState = remember { mutableStateOf(LocalDateTime.now()) }
    val amountState = rememberTextFieldState()
    val labelState = rememberTextFieldState()
    val descriptionState = rememberTextFieldState()
    val wasSuccess = remember { mutableStateOf(State.INITIAL) }
    val labelIconState = remember { mutableIntStateOf(R.drawable.description) }
    val selectedDataset = remember { mutableStateOf<Dataset?>(null) }
    val repayAmount = rememberTextFieldState()
    val datasetState = viewModel.uiState.collectAsState()
    val dataset = datasetState.value.datasets

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

    val amountAsDouble = amountState.text.toString().toDoubleOrNull()

    Column(
        modifier = Modifier.fillMaxWidth(),
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

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Amount
            ModelDrawerAmountField(
                state = amountState,
                placeholder = "0.0",
                colorResId = colorResId,
                wasSuccess = wasSuccess,
            )

            // Label
            ModelDrawerTextField(
                state = labelState,
                placeholder = "Label",
                colorResId = colorResId,
                iconState = labelIconState,
                viewModel = viewModel,
                wasSuccess = wasSuccess,
                textLength = 15
            )

            // Description
            ModelDrawerTextField(
                state = descriptionState,
                placeholder = "Description (Optional)",
                colorResId = colorResId,
                viewModel = viewModel
            )

            // Show date time picker.
            ChainNetworkDateTimeButton(
                showDateTime,
                localDateTimeState,
                colorResId = colorResId
            )

            if (dataType == DataType.LENT) {
                RepayField(
                    state = repayAmount,
                    datatype = DataType.LENT,
                    colorResId = colorResId,
                    datasets = dataset.filter {
                        it.dataType == DataType.LENT
                    },
                    selectedDataset = selectedDataset,
                )
            }

            if (dataType == DataType.DEBT) {
                RepayField(
                    state = repayAmount,
                    datatype = DataType.DEBT,
                    colorResId = colorResId,
                    datasets = dataset.filter {
                        it.dataType == DataType.DEBT
                    },
                    selectedDataset = selectedDataset,
                )
            }

            ModelDrawerButton(
                text = buttonText,
                wasSuccess = wasSuccess,
                colorResId = colorResId
            ) {
                if (amountAsDouble != null && labelState.text.toString().isNotEmpty()) {
                    viewModel.addData(
                        Dataset(
                            dataType = dataType,
                            amount = amountAsDouble,
                            label = labelState.text.toString(),
                            description = descriptionState.text.toString(),
                            dateTime = localDateTimeState.value.toFirestoreTimestampUtc(),
                            labelIcon = labelIconState.value
                        )
                    )
                    wasSuccess.value = State.SUCCESS

                    // Reset all state
                    amountState.clearText()
                    labelState.clearText()
                    descriptionState.clearText()
                    labelIconState.value = R.drawable.description

                } else {
                    wasSuccess.value = State.ERROR
                }
            }
        }
    }

    // Show all icons for label and description.
    IconList(
        labelIconState,
        viewModel = viewModel
    )
}

@Composable
fun EarningsModelDrawerContent(
    viewModel: HomeScreenViewModel
) {
    ModelDrawerContent(
        colorResId = R.color.Earnings,
        icon = R.drawable.filled_earnings,
        dataType = DataType.EARNINGS,
        description = "Add your earnings here",
        buttonText = "Received",
        viewModel = viewModel
    )
}

@Composable
fun ExpenseModelDrawerContent(
    viewModel: HomeScreenViewModel
) {
    ModelDrawerContent(
        colorResId = R.color.Expense,
        icon = R.drawable.filled_expenditure,
        dataType = DataType.EXPENSE,
        description = "Add your expenses here",
        buttonText = "Spent",
        viewModel = viewModel
    )
}

@Composable
fun DebtModelDrawerContent(
    viewModel: HomeScreenViewModel
) {

    ModelDrawerContent(
        colorResId = R.color.Debt,
        icon = R.drawable.filled_debt,
        dataType = DataType.DEBT,
        description = "Set your debts here",
        buttonText = "Set Debt",
        viewModel = viewModel
    )
}

@Composable
fun LentModelDrawerContent(
    viewModel: HomeScreenViewModel
) {
    ModelDrawerContent(
        colorResId = R.color.Lent,
        icon = R.drawable.filled_lent,
        dataType = DataType.LENT,
        description = "Put your lent here",
        buttonText = "Lent",
        viewModel = viewModel
    )
}

@Composable
fun SavingsModelDrawerContent(
    viewModel: HomeScreenViewModel
) {
    ModelDrawerContent(
        colorResId = R.color.Savings,
        icon = R.drawable.filled_savings,
        dataType = DataType.SAVINGS,
        description = "Add your savings here",
        buttonText = "Saved",
        viewModel = viewModel
    )
}

@Composable
fun RepayModelDrawerContent(viewModel: HomeScreenViewModel) {
    TODO("Not yet implemented")
}
