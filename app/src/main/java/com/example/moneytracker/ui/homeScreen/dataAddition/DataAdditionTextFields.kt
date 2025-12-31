// Praise be the LORD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.helper.State
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import java.text.NumberFormat
import java.util.Locale


@Composable
fun ModelDrawerTextField(
    state: TextFieldState,
    placeholder: String,
    colorResId: Int,
    iconState: MutableState<Int>? = null,
    textLength: Int? = null,
    wasSuccess: MutableState<State>? = null,
    viewModel: HomeScreenViewModel,
) {
    val isError = wasSuccess != null && state.text.isEmpty() && wasSuccess.value == State.ERROR
    val color = if (isError)
        colorResource(R.color.error_color) else
        colorResource(id = colorResId)
    val modifier = Modifier.fillMaxWidth(0.7f)
    val height = integerResource(R.integer.textFieldAndButtonHeight).dp
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp
    val modifiedPlaceholder = if (isError)
        "Fill the Label" else placeholder

    OutlinedTextField(
        modifier = modifier
            .padding(bottom = 10.dp)
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
                        viewModel.updateIsDescriptionIconVisible(true)
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
    shape: Shape = CircleShape,
    wasSuccess: MutableState<State>? = null,
    modifier: Modifier = Modifier
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
            .fillMaxWidth(0.7f)
            .padding(bottom = 10.dp)
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
fun RepayField(
    datatype: DataType,
    state: TextFieldState,
    datasets: List<Dataset>,
    selectedDataset: MutableState<Dataset?>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val fontSize = integerResource(R.integer.modelDrawerFontSize).sp
    val corner = 30.dp

    // If datasets change (e.g. migration assigned ids), refresh selectedDataset reference
    LaunchedEffect(datasets) {
        val current = selectedDataset.value
        if (current != null) {
            val matched =
                datasets.find { it.label == current.label && it.dateTime == current.dateTime }
            if (matched != null) {
                selectedDataset.value = matched
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            }
        ) {
            datasets.forEach { dataset ->
                DropdownMenuItem(
                    text = {
                        Text(dataset.label, fontSize = fontSize)
                    },
                    onClick = {
                        isExpanded = false
                        selectedDataset.value = dataset
                        state.edit {
                            replace(
                                0,
                                length,
                                dataset.amount.formatToAmount()
                            )
                        }
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = dataset.labelIcon),
                            contentDescription = dataset.label,
                            modifier = Modifier.size(ICON_SIZE)
                        )
                    }
                )
            }
        }
        ModelDrawerButton(
            text = selectedDataset.value?.label ?: "Select ${datatype.text}",
            shape = RoundedCornerShape(
                topStart = corner,
                bottomStart = corner
            ),
            colorResId = R.color.Repay
        ) {
            isExpanded = true
        }
        ModelDrawerAmountField(
            state,
            placeholder = "0",
            colorResId = R.color.Repay,
            shape = RoundedCornerShape(
                topEnd = corner,
                bottomEnd = corner
            ),
        )
    }
}