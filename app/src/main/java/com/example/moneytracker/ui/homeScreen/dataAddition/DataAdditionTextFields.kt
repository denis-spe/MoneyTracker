// Praise be the LORD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.dataAddition

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import java.text.NumberFormat
import java.util.Locale

private val AMOUNT_FONT_SIZE = 18.sp

@Composable
fun ModelDrawerTextField(
    state: TextFieldState,
    placeholder: String,
    colorResId: Int,
    iconState: MutableState<Int>? = null,
    textLength: Int? = null,
    viewModel: HomeScreenViewModel,
) {
    val color = colorResource(colorResId)
    val modifier = Modifier.fillMaxWidth(0.7f)

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