// Praise be the LORD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.input.then
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.AdjustmentStatus
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.addZeroIfLessThenTen
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.helper.isAmountEqualToAdjustAmount
import com.example.moneytracker.helper.remainingAmount
import com.example.moneytracker.helper.status
import com.example.moneytracker.helper.title
import kotlinx.coroutines.android.awaitFrame
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime
import kotlinx.datetime.number
import kotlinx.datetime.toKotlinLocalDate
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale


const val MaxWidth = 0.7f
val BOTTOM_SHEET_PADDING = 10.dp
private val MODIFIER_DRAWER = Modifier
    .fillMaxWidth()
    .padding(vertical = 6.dp, horizontal = 13.dp)
private val INNER_MODIFIER_DRAWER = Modifier
    .fillMaxWidth()
    .padding(start = 10.dp, end = 10.dp)
private val SHAPE = RoundedCornerShape(30.dp)
private val DIALOG_CARD_MODIFIER = Modifier.fillMaxWidth(0.8f)
private val AMOUNT_FONT_SIZE = 20.sp

@Composable
fun ModelDrawerTag(
    colorResId: Int,
    title: String,
    iconState: MutableState<Pair<String, Int>>,
) {
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp
    val color = colorResource(colorResId)
    val onDialogShow = remember { mutableStateOf(false) }

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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.tag
                    ),
                    contentDescription = "tag",
                    modifier = Modifier.size(ICON_SIZE)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(title, fontSize = fontSize, fontWeight = FONT_WEIGHT, color = color)
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(iconState.value.first.title, color = color, fontSize = fontSize)
                Spacer(modifier = Modifier.width(5.dp))
                Image(
                    painter = painterResource(id = iconState.value.second),
                    contentDescription = "icon",
                    modifier = Modifier.size(ICON_SIZE)
                )
            }
        }
    }

    // Show all icons for label and description.
    IconList(
        onConfirm = iconState,
        onDialogOpen = onDialogShow
    )
}

@Composable
fun ModelDrawerTextField(
    modifier: Modifier = Modifier,
    title: String = "",
    description: String = "",
    state: TextFieldState,
    placeholder: String,
    colorResId: Int,
    textLength: Int? = null,
    wasSuccess: MutableState<State>? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.SingleLine,
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
            Card(
                modifier = DIALOG_CARD_MODIFIER
            ) {
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
                            .fillMaxWidth()
                            .padding(
                                vertical = 5.dp,
                                horizontal = 5.dp
                            ),
                        state = state,
                        lineLimits = lineLimits,
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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(
                        id = if (title == "Label") R.drawable.label
                        else R.drawable.note
                    ),
                    contentDescription = "labelOrNote",
                    modifier = Modifier.size(ICON_SIZE)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(title, fontSize = fontSize, fontWeight = FONT_WEIGHT, color = color)
            }
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
            Card(
                modifier = DIALOG_CARD_MODIFIER
            ) {
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
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
//                            focusedIndicatorColor = color,
//                            unfocusedIndicatorColor = color.copy(alpha = 0.5f),
//                            focusedContainerColor = color.copy(alpha = 0.1f),
//                            unfocusedContainerColor = color.copy(alpha = 0.2f),
                        ),
                        textStyle = TextStyle(
                            color = color,
                            fontWeight = FontWeight.Bold,
                            fontSize = AMOUNT_FONT_SIZE
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number // only digits expected
                        ),
                        leadingIcon = {
                            Text(
                                text = symbol,
                                color = color,
                                fontWeight = FontWeight.Bold,
                                fontSize = AMOUNT_FONT_SIZE
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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.amount),
                    contentDescription = "Calendar",
                    modifier = Modifier.size(ICON_SIZE)
                )

                Spacer(modifier = Modifier.width(5.dp))
                Text("Amount", fontSize = fontSize, fontWeight = FONT_WEIGHT, color = color)
            }
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
            Card(
                modifier = DIALOG_CARD_MODIFIER
            ) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("Adjust Amount", fontSize = fontSize, fontWeight = FontWeight.Bold)
                    Text("Adjust the amount")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

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

                        VerticalDivider(modifier = Modifier.height(15.dp))

                        /* ---------- Amount field ---------- */
                        OutlinedTextField(
                            state = amountState,
                            lineLimits = TextFieldLineLimits.SingleLine,
                            placeholder = {
                                Text(
                                    text = "0.0",
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AMOUNT_FONT_SIZE
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
                                fontWeight = FontWeight.Bold,
                                fontSize = AMOUNT_FONT_SIZE
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number // only digits expected
                            ),
                            leadingIcon = {
                                Text(
                                    text = symbol,
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AMOUNT_FONT_SIZE
                                )
                            },

                            inputTransformation = InputTransformation.maxLength(16).then(
                                CustomInputTransformation()
                            ),
                            outputTransformation = CustomOutputTransformation(),
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp)
                    ) {
                        TextButton(
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = color
                            ),
                            onClick = { onDialogShow.value = false }
                        ) {
                            Text("Cancel", fontSize = fontSize, color = color)
                        }

                        TextButton(
                            onClick = {
                                onDialogShow.value = false
                                amountToDisplay = if (amountState.text.isNotEmpty())
                                    amountState.text.toString().toDouble().formatToAmount() else
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
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(
                        id = if (datatype == DataType.GOAL) R.drawable.achievement
                        else R.drawable.repay
                    ),
                    contentDescription = "Calendar",
                    modifier = Modifier.size(ICON_SIZE)
                )

                Spacer(modifier = Modifier.width(5.dp))
                Text(datatype.text, fontSize = fontSize, fontWeight = FONT_WEIGHT, color = color)
            }
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


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerComponent(
    onDateSelected: (DatePickerState) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = LocalDate.now(),
        initialDisplayMode = DisplayMode.Input,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerComponent(
    title: String,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit
) {

    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    /** Determines whether the time picker is dial or input */
    var showDial by remember { mutableStateOf(false) }

    /** The icon used for the icon button that switches from dial to input */
    val toggleIcon = if (showDial) {
        Icons.Filled.EditCalendar
    } else {
        Icons.Filled.AccessTime
    }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
                Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                if (showDial) {
                    TimePicker(
                        state = timePickerState,
                    )
                } else {
                    TimeInput(
                        state = timePickerState,
                    )
                }
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = { showDial = !showDial }) {
                        Icon(
                            imageVector = toggleIcon,
                            contentDescription = "Time picker type toggle",
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = { onConfirm(timePickerState) }) { Text("OK") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoubleTimePickerComponent(
    onConfirm: (TimePickerState, TimePickerState) -> Unit,
    onDismiss: () -> Unit
) {

    val currentTime = Calendar.getInstance()

    val firstTimePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    val secondTimePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier =
                Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Your starting time",
                    style = MaterialTheme.typography.labelMedium
                )

                TimeInput(
                    state = firstTimePickerState,
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Your ending time",
                    style = MaterialTheme.typography.labelMedium
                )

                TimeInput(
                    state = secondTimePickerState,
                )

                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        onConfirm(
                            firstTimePickerState,
                            secondTimePickerState
                        )
                    }) { Text("OK") }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeInput(
    showTime: MutableState<Boolean>,
    showDate: MutableState<Boolean>,
    localDateTimeState: MutableState<LocalDateTime>,
    colorResId: Int
) {
    val date = localDateTimeState.value.date
    val time = localDateTimeState.value.time
    val hour = if (time.hour < 10) "0${time.hour}" else time.hour
    val minute = if (time.minute < 10) "0${time.minute}" else time.minute
    val dayOfWeek = localDateTimeState.value.dayOfWeek.name
    val day = date.day.addZeroIfLessThenTen
    val month = date.month.number.addZeroIfLessThenTen
    val year = date.year

    val color = colorResource(colorResId)
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    integerResource(R.integer.modelDrawerPadding).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        /*
    Display the date
     */
        Row(
            modifier = MODIFIER_DRAWER
                .height(height)
                .background(color.copy(alpha = 0.1f))
                .clickable {
                    showDate.value = true
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = INNER_MODIFIER_DRAWER,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Calendar",
                        modifier = Modifier.size(ICON_SIZE)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text("Select date", color = color, fontSize = fontSize)
                }

                Text(
                    "${dayOfWeek.take(3).title} $day.$month.$year",
                    color = color,
                    fontSize = fontSize
                )
            }
        }

        /*
    Display the time
     */
        Row(
            modifier = MODIFIER_DRAWER
                .height(height)
                .background(color.copy(alpha = 0.1f))
                .clickable {
                    showTime.value = true
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = INNER_MODIFIER_DRAWER,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = "Clock",
                        modifier = Modifier.size(ICON_SIZE)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text("Select time", color = color, fontSize = fontSize)
                }
                Text("$hour:$minute", color = color, fontSize = fontSize)
            }
        }
    }


    // Show the date picker.
    if (showDate.value) {
        DatePickerComponent({ localDate ->
            localDate.getSelectedDate()?.let {
                // Change the date to kotlin date
                val localDate = it.toKotlinLocalDate()

                // Add the time to the date
                val localDateTime = localDate.atTime(localDateTimeState.value.time)

                // Update the state
                localDateTimeState.value = localDateTime

                // Close the date picker
                showDate.value = false
            }
        }) {
            showDate.value = false
        }
    }

    // Time picker
    if (showTime.value) {
        TimePickerComponent(
            "Select time",
            onConfirm = { timePickerState ->
                // Hour and minute from time picker
                val hour = timePickerState.hour
                val minute = timePickerState.minute

                // Add the time to the date
                val localDateTime = localDateTimeState.value.date.atTime(hour, minute)

                // Update the state
                localDateTimeState.value = localDateTime

                // Close the time picker
                showTime.value = false
            }) {
            showTime.value = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTimeRange(
    startLocalDateTimeState: MutableState<LocalDateTime>,
    endLocalDateTimeState: MutableState<LocalDateTime>,
    colorResId: Int
) {
    val isPresentStartDateDialogOpen = remember { mutableStateOf(false) }
    val isPresentEndDateDialogOpen = remember { mutableStateOf(false) }
    val isTimeDialogOpen = remember { mutableStateOf(false) }

    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    integerResource(R.integer.modelDrawerPadding).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp
    val color = colorResource(colorResId)

    val startDate = startLocalDateTimeState.value.date
    val day = startDate.day.addZeroIfLessThenTen
    val month = startDate.month.number.addZeroIfLessThenTen
    val year = startDate.year
    val hour = startLocalDateTimeState.value.time.hour
    val minute = startLocalDateTimeState.value.time.minute

    val endDate = endLocalDateTimeState.value.date
    val endingDay = endDate.day.addZeroIfLessThenTen
    val endingMonth = endDate.month.number.addZeroIfLessThenTen
    val endingYear = endDate.year
    val endingHour = endLocalDateTimeState.value.time.hour
    val endingMinute = endLocalDateTimeState.value.time.minute


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        /* Display the time */
        Row(
            modifier = MODIFIER_DRAWER
                .height(height)
                .background(color.copy(alpha = 0.1f))
                .clickable {
                    isTimeDialogOpen.value = true
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = INNER_MODIFIER_DRAWER,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = "Clock",
                        modifier = Modifier.size(ICON_SIZE)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text("Select time", color = color, fontSize = fontSize)
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Start $hour:$minute", color = color, fontSize = fontSize)
                    Text("End $endingHour:$endingMinute", color = color, fontSize = fontSize)
                }
            }
        }

        /*
         Display the starting date
         */
        Row(
            modifier = MODIFIER_DRAWER
                .height(height)
                .background(color.copy(alpha = 0.1f))
                .clickable {
                    isPresentStartDateDialogOpen.value = true
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = INNER_MODIFIER_DRAWER,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Clock",
                        modifier = Modifier.size(ICON_SIZE)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text("Select starting date")
                }
                Text("$year.$month.$day")
            }
        }

        /*
         Display the ending date
         */
        Row(
            modifier = MODIFIER_DRAWER
                .height(height)
                .background(color.copy(alpha = 0.1f))
                .clickable {
                    isPresentEndDateDialogOpen.value = true
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = INNER_MODIFIER_DRAWER,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Clock",
                        modifier = Modifier.size(ICON_SIZE)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text("Select ending date")
                }
                Text("$endingYear.$endingMonth.$endingDay")
            }
        }

    }



    if (isPresentStartDateDialogOpen.value) {
        DatePickerComponent({ localDate ->
            localDate.getSelectedDate()?.let {
                // Change the date to kotlin date
                val localDate = it.toKotlinLocalDate()

                // Add the time to the date
                val localDateTime = localDate.atTime(startLocalDateTimeState.value.time)

                // Update the state
                startLocalDateTimeState.value = localDateTime

                // Close the date picker
                isPresentStartDateDialogOpen.value = false
            }
        }) {
            isPresentStartDateDialogOpen.value = false
        }
    }

    if (isPresentEndDateDialogOpen.value) {
        DatePickerComponent({ localDate ->
            localDate.getSelectedDate()?.let {
                // Change the date to kotlin date
                val localDate = it.toKotlinLocalDate()

                // Add the time to the date
                val localDateTime = localDate.atTime(endLocalDateTimeState.value.time)

                // Update the state
                endLocalDateTimeState.value = localDateTime

                // Close the date picker
                isPresentEndDateDialogOpen.value = false

            }
        }) {
            isPresentEndDateDialogOpen.value = false
        }
    }

    if (isTimeDialogOpen.value) {
        DoubleTimePickerComponent(
            onConfirm = { firstTimePickerState, secondTimePickerState ->
                // Hour and minute from time picker
                val firstHour = firstTimePickerState.hour
                val firstMinute = firstTimePickerState.minute
                val secondHour = secondTimePickerState.hour
                val secondMinute = secondTimePickerState.minute

                // Add the time to the date
                val startLocalDateTime = startLocalDateTimeState.value.date.atTime(
                    firstHour, firstMinute
                )
                val endLocalDateTime = endLocalDateTimeState.value.date.atTime(
                    secondHour, secondMinute
                )


                // Update the state
                startLocalDateTimeState.value = startLocalDateTime
                endLocalDateTimeState.value = endLocalDateTime

                // Close the time picker
                isTimeDialogOpen.value = false
            }
        ) {
            isTimeDialogOpen.value = false
        }
    }

}

