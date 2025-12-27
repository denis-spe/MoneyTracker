// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.ui.components.Current
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import java.text.NumberFormat
import java.util.Locale

private val ICON_SIZE = 25.dp
private val FONT_WEIGHT = FontWeight.Bold
private val AMOUNT_FONT_SIZE = 18.sp

@Composable
fun DataAdditionFloatingButton(
    onModelBottomSheetShow: (Boolean) -> Unit,
) {
    FloatingActionButton(
        onClick = { onModelBottomSheetShow(true) },
        shape = CircleShape
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add data")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAdditionModelDrawer(
    onModelBottomSheetShowValue: Boolean,
    onModelBottomSheetShow: (Boolean) -> Unit,
    viewModel: HomeScreenViewModel,
) {
    val tabToDisplay = remember { mutableStateOf(DataType.EARNINGS) }

    if (!onModelBottomSheetShowValue) return

    ModalBottomSheet(
        onDismissRequest = { onModelBottomSheetShow(false) }
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
                ExpenseModelDrawerContent()
            }

            DataType.DEBT -> {
                DebtModelDrawerContent()
            }

            DataType.LENT -> {
                LentModelDrawerContent()
            }

            DataType.SAVINGS -> {
                SavingsModelDrawerContent()
            }

        }
    }
}

@Composable
fun ModelDrawerContent(
    color: Int,
    icon: Int,
    dataType: DataType,
    description: String,
    content: @Composable (ColumnScope.() -> Unit)
) {
    val color = colorResource(color)

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
            Icon(
                modifier = Modifier
                    .size(ICON_SIZE)
                    .padding(end = 5.dp),
                painter = painterResource(id = icon),
                contentDescription = dataType.text,
                tint = color
            )
            Text(description, color = color)
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
fun ModelDrawerTextField(
    state: TextFieldState,
    placeholder: String,
    color: Int,
    iconState: MutableState<Int>,
    viewModel: HomeScreenViewModel,
) {
    val color = colorResource(color)
    val modifier = Modifier.fillMaxWidth(0.7f)
    val uiState = viewModel.uiState.collectAsState()

    OutlinedTextField(
        modifier = modifier.padding(bottom = 10.dp),
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = {
            Text(
                text = placeholder,
                color = color,
                fontWeight = FontWeight.Bold
            )
        },
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedTextColor = color,
            unfocusedTextColor = color.copy(alpha = 0.5f),
            cursorColor = color,
            focusedIndicatorColor = color,
            unfocusedIndicatorColor = color.copy(alpha = 0.5f),
            focusedContainerColor = color.copy(alpha = 0.1f),
            unfocusedContainerColor = color.copy(alpha = 0.2f),
        ),
        textStyle = TextStyle(
            color = color,
            fontWeight = FontWeight.Bold
        ),
        shape = CircleShape,
        leadingIcon = {
            IconButton(
                onClick = {
                    viewModel.updateIsDescriptionIconVisible(true)
                }
            ) {
                Image(
                    painter = painterResource(id = uiState.value.descriptionIcon),
                    contentDescription = "Icon",
                )
            }
        }
    )
}


@Composable
fun ModelDrawerAmountField(
    state: TextFieldState,
    placeholder: String,
    colorResId: Int, // renamed to avoid shadowing
) {
    val color = colorResource(id = colorResId)
    val modifier = Modifier.fillMaxWidth(0.7f)

    val locale = Locale.getDefault()
    val numberFormat = remember(locale) { NumberFormat.getCurrencyInstance(locale) }


    OutlinedTextField(
        modifier = modifier.padding(bottom = 10.dp),
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = {
            Text(
                text = placeholder,
                color = color,
                fontWeight = FontWeight.Bold
            )
        },
        colors = OutlinedTextFieldDefaults.colors().copy(
            focusedTextColor = color,
            unfocusedTextColor = color.copy(alpha = 0.5f),
            cursorColor = color,
            focusedIndicatorColor = color,
            unfocusedIndicatorColor = color.copy(alpha = 0.5f),
            focusedContainerColor = color.copy(alpha = 0.1f),
            unfocusedContainerColor = color.copy(alpha = 0.2f),
        ),
        textStyle = TextStyle(
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = AMOUNT_FONT_SIZE
        ),
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number // only digits expected
        ),
        leadingIcon = {
            Text(
                text = numberFormat.currency?.symbol ?: "$",
                color = color,
                fontWeight = FontWeight.Bold
            )
        },

        inputTransformation = InputTransformation.maxLength(16).then(
            CustomInputTransformation()
        ),
        outputTransformation = CustomOutputTransformation(),
    )
}

@Composable
fun ModelDrawerButton() {
}

@Composable
fun EarningsModelDrawerContent(viewModel: HomeScreenViewModel) {

    ModelDrawerContent(
        color = R.color.Earnings,
        icon = R.drawable.filled_earnings,
        dataType = DataType.EARNINGS,
        description = "Add your earnings here"
    ) {
        ModelDrawerAmountField(
            state = rememberTextFieldState(),
            placeholder = "0.0",
            colorResId = R.color.Earnings,
        )
        ModelDrawerTextField(
            state = TextFieldState(),
            placeholder = "Description",
            color = R.color.Earnings,
            iconState = remember { mutableStateOf(R.drawable.description) },
            viewModel
        )
    }
}

@Composable
fun ExpenseModelDrawerContent() {
    ModelDrawerContent(
        color = R.color.Expense,
        icon = R.drawable.filled_expenditure,
        dataType = DataType.EXPENSE,
        description = "Put your expenses here"
    ) {

    }
}

@Composable
fun DebtModelDrawerContent() {
    ModelDrawerContent(
        color = R.color.Debt,
        icon = R.drawable.filled_debt,
        dataType = DataType.DEBT,
        description = "Set your debts here"
    ) {

    }
}

@Composable
fun LentModelDrawerContent() {
    ModelDrawerContent(
        color = R.color.Lent,
        icon = R.drawable.filled_lent,
        dataType = DataType.LENT,
        description = "Put your lent here"
    ) {

    }
}

@Composable
fun SavingsModelDrawerContent() {
    ModelDrawerContent(
        color = R.color.Savings,
        icon = R.drawable.filled_savings,
        dataType = DataType.SAVINGS,
        description = "Add your savings here"
    ) {

    }
}
