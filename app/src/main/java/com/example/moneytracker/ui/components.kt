// Bless be the name of LORD our GOD

package com.example.moneytracker.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R

@Composable
fun AuthTextButton(
    id: Int,
    text: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.testTag(stringResource(id = id))
    ) {
        val poppins = FontFamily(
            Font(R.font.poppins_medium, FontWeight.Medium)
        )

        Text(
            text = stringResource(id = text),
            color = Color.Black,
            fontSize = 15.sp,
            fontFamily = poppins
        )
    }
}


@Composable
fun AuthButton(
    id: Int,
    text: Int,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
){
    val cornerShape = RoundedCornerShape(integerResource(R.integer.authButtonRoundedCornerShape))

    OutlinedButton (
        modifier = modifier.testTag(stringResource(id = id)),
        shape = cornerShape,
        border = BorderStroke(1.dp, colorResource(id = R.color.authBtnContainerColor)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colorResource(id = R.color.authBtnContainerColor)
        ),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.width(160.dp)
        ){
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = text),
                modifier = Modifier
                    .size(integerResource(id = R.integer.authButtonIconSize).dp)
            )
            Spacer(modifier = Modifier.width(
                integerResource(id = R.integer.authButtonSpacerWidth).dp))
            Text(
                text = stringResource(id = text),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AuthFillButton(
    id: Int,
    text: Int,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
){
    val cornerShape = RoundedCornerShape(integerResource(R.integer.authButtonRoundedCornerShape))

    Button(
        modifier = modifier.testTag(stringResource(id = id)),
        shape = cornerShape,
        border = BorderStroke(1.dp, colorResource(id = R.color.authBtnContainerColor)),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.fillAuthBtnContainerColor).copy(alpha = 0.5f),
            contentColor = colorResource(id = R.color.authBtnContainerColor),
        ),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(160.dp)
        ){
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(id = text),
                modifier = Modifier
                    .size(integerResource(id = R.integer.authButtonIconSize).dp)
            )
            Spacer(modifier = Modifier.width(
                integerResource(id = R.integer.authButtonSpacerWidth).dp))
            Text(
                text = stringResource(id = text),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AuthLayout(screenId: Int, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(stringResource(screenId)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        AuthHeader()
        content()
    }
}

@Composable
fun AuthBackButton(
    id: Int,
    icon: Int,
    size: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .testTag(stringResource(id = id))
            .size(size.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = id),
            modifier = modifier.size(size.dp)
        )
    }
}

@Composable
fun AuthHeader(){
    Column {
        val robotoFont = FontFamily(
            Font(R.font.roboto, FontWeight.Normal)
        )

        Text(
            text = stringResource(R.string.title_money),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = robotoFont
        )
        Text(
            text = stringResource(R.string.title_tracker),
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = robotoFont
        )
    }
}

@Composable
fun AuthEmailField(
    id: Int,
    placeholder: Int,
    outlineIcon: Int,
    filledIcon: Int,
    modifier: Modifier = Modifier,
    textState: MutableState<TextFieldValue>
) {
    val focusedColor = colorResource(R.color.authBtnContainerColor)
    val unfocusedColor = colorResource(R.color.authBtnContainerColor).copy(alpha = 0.6f)

    val customColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.DarkGray,
        unfocusedTextColor = Color.Gray,
        focusedBorderColor = focusedColor,
        unfocusedBorderColor = unfocusedColor,
        cursorColor = focusedColor,
    )

    val fontSize = 17.sp
    val iconColor = if (textState.value.text.isEmpty())
        unfocusedColor else focusedColor
    val poppins = FontFamily(
        Font(R.font.poppins_medium, FontWeight.Medium)
    )

    OutlinedTextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        modifier = modifier
            .testTag(stringResource(id = id))
            .fillMaxWidth(0.7f),
        textStyle = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            fontFamily = poppins
        ),
        placeholder = {
            Text(
                text = stringResource(id = placeholder),
                color = Color.Gray,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                fontFamily = poppins
            )
        },
        leadingIcon = {
            Image(
                painter = painterResource(
                    id = if (textState.value.text.isEmpty())
                        outlineIcon else filledIcon
                ),
                contentDescription = stringResource(id = id),
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(iconColor)
            )
        },
        trailingIcon = @Composable {
            if (textState.value.text.isNotEmpty())
                IconButton(onClick = { textState.value = TextFieldValue("") }) {
                    Image(
                        painter = painterResource(id = R.drawable.clear_icon),
                        contentDescription = stringResource(id = id),
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(focusedColor)
                    )
                }
        },
        shape = RoundedCornerShape(30),
        colors = customColors,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
    )
}

@Preview(showBackground = true)
@Composable
fun AuthEmailFieldPreview() {
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthEmailField(
            id = R.string.loginEmailFieldId,
            placeholder = R.string.loginEmailFieldPlaceholder,
            outlineIcon = R.drawable.outline_email,
            filledIcon = R.drawable.filled_email,
            textState = textState
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AuthButtonPreview(){
//    AuthButton(
//        id = R.string.startup_google_text,
//        text = R.string.startup_google_text,
//        icon = R.drawable.google_icon,
//        onClick = {}
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun AuthFillButtonPreview(){
//    AuthFillButton(
//        id = R.string.startup_google_text,
//        text = R.string.startup_google_text,
//        icon = R.drawable.google_icon,
//        onClick = {}
//    )
//}


