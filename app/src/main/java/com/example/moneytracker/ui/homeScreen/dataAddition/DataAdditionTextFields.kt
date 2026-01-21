// Praise be the LORD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.AdjustmentStatus
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.status
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import kotlinx.coroutines.android.awaitFrame
import java.text.NumberFormat
import java.util.Locale


const val MaxWidth = 0.7f
val BOTTOM_SHEET_PADDING = 10.dp
val MODIFIER_DRAWER = Modifier
    .fillMaxWidth()
    .padding(vertical = 6.dp, horizontal = 13.dp)
val INNER_MODIFIER_DRAWER = Modifier
    .fillMaxWidth()
    .padding(start = 10.dp, end = 10.dp)
val SHAPE = RoundedCornerShape(30.dp)

@Composable
fun ModelDrawerTextField(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
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

    val onDialogShow = remember { mutableStateOf(false) }
    val text = remember { mutableStateOf("No Text") }


    if (onDialogShow.value) {
        Dialog(
            onDismissRequest = { onDialogShow.value = false },
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(title, fontSize = fontSize, fontWeight = FontWeight.Bold)
                    Text(description)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
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
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                        textStyle = TextStyle(
                            color = color,
                            fontSize = fontSize
                        ),
                        shape = SHAPE,
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

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp)
                    ) {
                        TextButton(
                            onClick = { onDialogShow.value = false }
                        ) {
                            Text("Cancel", fontSize = fontSize)
                        }

                        TextButton(
                            onClick = {
                                onDialogShow.value = false
                                text.value = state.text.toString()
                            }
                        ) {
                            Text("OK", fontSize = fontSize)
                        }
                    }
                }

            }
        }
    }

    Row(
        modifier = MODIFIER_DRAWER
            .height(height)
            .background(color.copy(alpha = 0.1f))
            .clickable {
                onDialogShow.value = true
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = INNER_MODIFIER_DRAWER,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, fontSize = fontSize, fontWeight = FONT_WEIGHT, color = color)
            Text(text.value.take(9), color = color, fontSize = fontSize)
        }
    }
}


@Composable
fun ModelDrawerAmountField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    placeholder: String,
    colorResId: Int,
    shape: Shape = CircleShape,
    wasSuccess: MutableState<State>? = null,
) {
    modifier.fillMaxWidth(MaxWidth)
    val isError = wasSuccess != null && state.text.isEmpty() && wasSuccess.value == State.ERROR
    val color = if (isError)
        colorResource(R.color.error_color) else
        colorResource(id = colorResId)

    val locale = Locale.getDefault()
    val numberFormat = remember(locale) { NumberFormat.getCurrencyInstance(locale) }
    val symbol = numberFormat.currency?.symbol ?: "$"
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp
    val onDialogShow = remember { mutableStateOf(false) }
    var amountToDisplay by remember { mutableStateOf("${symbol}0.0") }


    if (onDialogShow.value) {
        Dialog(
            onDismissRequest = { onDialogShow.value = false },
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Amount", fontSize = fontSize, fontWeight = FontWeight.Bold)
                    Text("Enter the amount")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    OutlinedTextField(
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
                                text = symbol,
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

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp)
                    ) {
                        TextButton(
                            onClick = { onDialogShow.value = false }
                        ) {
                            Text("Cancel", fontSize = fontSize)
                        }

                        TextButton(
                            onClick = {
                                onDialogShow.value = false
                                amountToDisplay = if (state.text.isNotEmpty())
                                    state.text.toString().toDouble().formatToAmount() else
                                    "${symbol}0.0"
                            }
                        ) {
                            Text("OK", fontSize = fontSize)
                        }
                    }

                }
            }
        }
    }

    Row(
        modifier = MODIFIER_DRAWER
            .height(height)
            .background(color.copy(alpha = 0.1f))
            .clickable {
                onDialogShow.value = true
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = INNER_MODIFIER_DRAWER,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Amount", fontSize = fontSize, fontWeight = FONT_WEIGHT, color = color)
            Text(amountToDisplay, color = color, fontSize = fontSize)
        }
    }


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
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    30.dp

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
    val locale = Locale.getDefault()
    val numberFormat = remember(locale) { NumberFormat.getCurrencyInstance(locale) }
    val symbol = numberFormat.currency?.symbol ?: "$"
    val onDialogShow = remember { mutableStateOf(false) }
    var amountToDisplay by remember { mutableStateOf("${symbol}0.0") }
    val color = colorResource(colorResId)

    if (onDialogShow.value) {
        Dialog(
            onDismissRequest = { onDialogShow.value = false },
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
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
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = { expanded = true }
                        ) {
                            Text(
                                selectedDataset.value?.label ?: "Select ${datatype.text}",
                                fontSize = fontSize,
                            )
                        }

                        /* ---------- Amount field ---------- */
                        TextField(
                            state = amountState,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            placeholder = {
                                Text(
                                    text = "0.00",
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
                                    text = symbol,
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
                }
            }
        }
    }

    Row(
        modifier = MODIFIER_DRAWER
            .height(height)
            .background(color.copy(alpha = 0.1f))
            .clickable {
                onDialogShow.value = true
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = INNER_MODIFIER_DRAWER,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(datatype.text, fontSize = fontSize, fontWeight = FONT_WEIGHT, color = color)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                if (selectedDataset.value != null && selectedDataset.value?.label?.isEmpty() == false) {
                    Text(
                        selectedDataset.value?.label ?: "",
                        color = color, fontSize = fontSize
                    )
                }
                Text(amountToDisplay, color = color, fontSize = fontSize)
            }
        }
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
