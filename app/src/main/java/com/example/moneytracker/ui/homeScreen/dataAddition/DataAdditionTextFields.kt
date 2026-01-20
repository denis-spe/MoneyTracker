// Praise be the LORD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.AdjustmentStatus
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.status
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import kotlinx.coroutines.android.awaitFrame
import java.text.NumberFormat
import java.util.Locale


const val MaxWidth = 0.7f
val BOTTOM_SHEET_PADDING = 10.dp

@Composable
fun ModelDrawerTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholder: String,
    colorResId: Int,
    iconState: MutableState<Int>? = null,
    textLength: Int? = null,
    wasSuccess: MutableState<State>? = null,
    viewModel: HomeScreenViewModel,
) {
    val modifier = modifier.fillMaxWidth(MaxWidth)
    val isError = wasSuccess != null && state.text.isEmpty() && wasSuccess.value == State.ERROR
    val color = if (isError)
        colorResource(R.color.error_color) else
        colorResource(id = colorResId)
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp
    val modifiedPlaceholder = if (isError)
        "Fill the Label" else placeholder

    OutlinedTextField(
        modifier = modifier
            .padding(bottom = BOTTOM_SHEET_PADDING)
            .height(height),
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = {
            Text(
                text = modifiedPlaceholder,
                color = color,
                fontSize = fontSize
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
            fontSize = fontSize
        ),
        shape = CircleShape,
        leadingIcon = {
            if (iconState != null) {
                IconButton(
                    onClick = {
                        viewModel.showIconDialog(true)
                    }
                ) {
                    Image(
                        painter = painterResource(id = iconState.value),
                        contentDescription = "Icon",
                        modifier = Modifier.size(ICON_SIZE)
                    )
                }
            }
        },
        inputTransformation = if (textLength != null)
            InputTransformation.maxLength(textLength) else
            null
    )
}


@Composable
fun ModelDrawerAmountField(
    state: TextFieldState,
    placeholder: String,
    colorResId: Int,
    modifier: Modifier = Modifier.fillMaxWidth(MaxWidth),
    shape: Shape = CircleShape,
    wasSuccess: MutableState<State>? = null,
) {
    val isError = wasSuccess != null && state.text.isEmpty() && wasSuccess.value == State.ERROR
    val color = if (isError)
        colorResource(R.color.error_color) else
        colorResource(id = colorResId)

    val locale = Locale.getDefault()
    val numberFormat = remember(locale) { NumberFormat.getCurrencyInstance(locale) }
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp


    OutlinedTextField(
        modifier = modifier
            .padding(bottom = BOTTOM_SHEET_PADDING)
            .height(height),
        state = state,
        shape = shape,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = {
            Text(
                text = placeholder,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize
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
            fontSize = fontSize
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number // only digits expected
        ),
        leadingIcon = {
            Text(
                text = numberFormat.currency?.symbol ?: "$",
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = fontSize
            )
        },

        inputTransformation = InputTransformation.maxLength(16).then(
            CustomInputTransformation()
        ),
        outputTransformation = CustomOutputTransformation(),
    )
}

@Composable
fun AdjustmentField(
    sheetVisible: Boolean,
    datatype: DataType,
    amountState: TextFieldState,
    datasets: List<Dataset>,
    colorResId: Int,
    selectedDataset: MutableState<Dataset?>,
    modifier: Modifier = Modifier,
    wasRepaySuccess: MutableState<State>
) {
    var expanded by remember { mutableStateOf(false) }

    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp
    val corner = 30.dp

    /* ----------------------------------------------------------
     * 1) React to Firestore datasets ONLY when sheet is visible
     * ---------------------------------------------------------- */
    LaunchedEffect(sheetVisible, datasets) {
        if (!sheetVisible) return@LaunchedEffect

        val current = selectedDataset.value ?: return@LaunchedEffect

        val matched = datasets.firstOrNull {
            it.label == current.label && it.dateTime == current.dateTime
        }

        if (matched != null && matched !== current) {
            selectedDataset.value = matched
        }
    }

    /* ----------------------------------------------------------
     * 2) UI
     * ---------------------------------------------------------- */
    Row(
        modifier = modifier
            .fillMaxWidth(MaxWidth),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        /* ---------- Dropdown ---------- */
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            datasets
                .filterNot { it.isAmountEqualToAdjustAmount() && it.status == AdjustmentStatus.PENDING }
                .forEach { dataset ->

                    DropdownMenuItem(
                        text = {
                            Text(dataset.label, fontSize = fontSize)
                        },
                        onClick = {
                            expanded = false
                            selectedDataset.value = dataset
                        },
                        leadingIcon = {
                            Image(
                                painter = painterResource(dataset.labelIcon),
                                contentDescription = dataset.label,
                                modifier = Modifier.size(ICON_SIZE)
                            )
                        }
                    )
                }
        }

        /* ---------- Dataset button ---------- */
        ModelDrawerButton(
            text = selectedDataset.value?.label ?: "Select ${datatype.text}",
            shape = RoundedCornerShape(
                topStart = corner,
                bottomStart = corner
            ),
            modifier = Modifier.weight(0.5f),
            wasSuccess = wasRepaySuccess,
            colorResId = colorResId,
            filledColor = Color.Transparent
        ) {
            expanded = true
        }

        /* ---------- Amount field ---------- */
        ModelDrawerAmountField(
            state = amountState,
            placeholder = "0",
            colorResId = colorResId,
            modifier = Modifier.weight(0.5f),
            shape = RoundedCornerShape(
                topEnd = corner,
                bottomEnd = corner
            ),
            wasSuccess = wasRepaySuccess
        )
    }

    /* ----------------------------------------------------------
     * 3) Update amount AFTER selection (deferred, safe)
     * ---------------------------------------------------------- */
    val selected = selectedDataset.value
    LaunchedEffect(sheetVisible, selected) {
        if (!sheetVisible || selected == null) return@LaunchedEffect

        // wait for BottomSheet to render first frame
        awaitFrame()

        val newValue = selected.remainingAmount.toLong().toString()
        if (amountState.text.toString() != newValue) {
            amountState.setTextAndPlaceCursorAtEnd(newValue)
        }
    }
}

@Composable
fun PaymentMethodDropdown(
    colorResId: Int,
    selectedPaymentMethod: MutableState<PaymentMethod>,
) {
    val expanded = remember { mutableStateOf(false) }
    val paymentMethods = PaymentMethod.entries.toTypedArray()
        .toList()
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ModelDrawerButton(
            text = selectedPaymentMethod.value.text,
            wasSuccess = null,
            colorResId = colorResId,
            filledColor = Color.Transparent,
            icon = selectedPaymentMethod.value.icon,
            modifier = Modifier.fillMaxWidth(MaxWidth),
            fontSize = 10.sp
        ) {
            expanded.value = true
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            paymentMethods
                .forEach { paymentMethod ->

                    DropdownMenuItem(
                        text = {
                            Text(paymentMethod.text, fontSize = fontSize)
                        },
                        onClick = {
                            expanded.value = false
                            selectedPaymentMethod.value = paymentMethod
                        },
                        leadingIcon = {
                            // Replace Image(painter = painterResource(...)) with:
                            AsyncImage(
                                model = paymentMethod.icon,
                                contentDescription = paymentMethod.text,
                                modifier = Modifier.size(ICON_SIZE)
                            )
                        }
                    )
                }
        }
    }

}
