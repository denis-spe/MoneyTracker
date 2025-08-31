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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
fun AuthOutlineTextField(
    id: Int,
    placeholder: Int,
    outlineIcon: Int,
    filledIcon: Int,
    modifier: Modifier = Modifier,
    isError: MutableState<Boolean>,
    textState: MutableState<String>,
    isEmail: Boolean = false
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

    val fontSize = 14.sp
    val iconColor = if (textState.value.isEmpty())
        unfocusedColor else focusedColor
    val poppins = FontFamily(
        Font(R.font.poppins_medium, FontWeight.Medium)
    )

    OutlinedTextField(
        value = textState.value,
        onValueChange = {
            isError.value = false
            textState.value = it
        },
        modifier = modifier
            .testTag(stringResource(id = id))
            .fillMaxWidth(0.7f),
        textStyle = TextStyle(
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            fontFamily = poppins
        ),
        isError = isError.value,
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
            if (isError.value)
                Image(
                    painter = painterResource(id = R.drawable.circle_error),
                    contentDescription = stringResource(id = id),
                    modifier = Modifier
                        .size(24.dp)
                        .testTag(stringResource(id = R.string.error_icon)),
                )
            else
                Image(
                    painter = painterResource(
                        id = if (textState.value.isEmpty())
                            outlineIcon else filledIcon
                    ),
                    contentDescription = stringResource(id = id),
                    modifier = Modifier
                        .size(24.dp)
                        .testTag(stringResource(id = R.string.authOutlineFieldLeadingIconId)),
                    colorFilter = ColorFilter.tint(iconColor)
                )
        },
        trailingIcon = @Composable {
            if (textState.value.isNotEmpty())
                IconButton(onClick = { textState.value = "" }) {
                    Image(
                        painter = painterResource(id = R.drawable.clear_icon),
                        contentDescription = stringResource(id = id),
                        modifier = Modifier
                            .size(16.dp)
                            .testTag(
                                stringResource(
                                    id = R.string.authOutlineFieldTrailingIconId
                                )
                            ),
                        colorFilter = ColorFilter.tint(focusedColor)
                    )
                }
        },
        shape = RoundedCornerShape(30),
        colors = customColors,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isEmail) KeyboardType.Email
            else KeyboardType.Text
        )
    )
}

@Composable
fun AuthPasswordField(
    id: Int,
    placeholder: Int,
    outlineIcon: Int,
    filledIcon: Int,
    modifier: Modifier = Modifier,
    isError: MutableState<Boolean>,
    textState: MutableState<String>
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

    val fontSize = 14.sp
    val iconColor = if (textState.value.isEmpty())
        unfocusedColor else focusedColor
    val poppins = FontFamily(
        Font(R.font.poppins_medium, FontWeight.Medium)
    )
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = textState.value,
        onValueChange = {
            isError.value = false
            textState.value = it
        },
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
        isError = isError.value,
        leadingIcon = {
            if (isError.value)
                Image(
                    painter = painterResource(id = R.drawable.circle_error),
                    contentDescription = stringResource(id = id),
                    modifier = Modifier
                        .size(24.dp)
                        .testTag(stringResource(id = R.string.error_icon)),
                )
            else
                Image(
                    painter = painterResource(
                        id = if (textState.value.isEmpty())
                            outlineIcon else filledIcon
                    ),
                    contentDescription = stringResource(id = id),
                    modifier = Modifier
                        .size(24.dp)
                        .testTag(stringResource(id = R.string.authPasswordOutlineFieldLeadingIconId)),
                    colorFilter = ColorFilter.tint(iconColor)
                )
        },
        trailingIcon = @Composable {
            IconButton(onClick = {
                isVisible = isVisible.not()
            }) {
                Image(
                    painter = painterResource(
                        id = if (isVisible) R.drawable.open_password
                        else R.drawable.hidden_password
                    ),
                    contentDescription = stringResource(id = id),
                    modifier = Modifier
                        .size(20.dp)
                        .testTag(
                            stringResource(
                                id = R.string.authPasswordOutlineFieldTrailingIconId
                            )
                        ),
                    colorFilter = ColorFilter.tint(focusedColor)
                )
            }
        },
        shape = RoundedCornerShape(30),
        colors = customColors,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isVisible) VisualTransformation.None
        else PasswordVisualTransformation()
    )
}

@Composable
fun AuthNextPageButton(
    id: Int,
    text: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val poppins = FontFamily(
        Font(R.font.poppins_medium, FontWeight.Medium)
    )

    OutlinedButton(
        modifier = modifier
            .testTag(stringResource(id = id))
            .fillMaxWidth(0.5f),
        shape = RoundedCornerShape(integerResource(R.integer.authButtonRoundedCornerShape)),
        onClick = onClick,
        border = BorderStroke(1.dp, colorResource(id = R.color.authBtnContainerColor)),
    ) {
        Text(
            text = stringResource(id = text),
            fontWeight = FontWeight.SemiBold,
            fontFamily = poppins,
            fontSize = 17.sp,
            color = colorResource(id = R.color.authBtnContainerColor)
        )
    }
}

@Composable
fun AuthInputLayout(
    screenId: Int,
    screenImgId: Int,
    descriptionId: Int,
    descriptionText: Int,
    pageFlowImgId: Int,
    nextPageButtonId: Int,
    nextPageButtonText: Int,
    nextPageButtonOnClick: () -> Unit,
    backBtnOnClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val roboto = FontFamily(
        Font(
            R.font.roboto,
            FontWeight.Medium,
        )
    )

    AuthLayout(screenId = screenId) {

        // Description
        Column(
            modifier = Modifier
                .padding(top = 50.dp, bottom = 40.dp)
                .testTag(stringResource(id = descriptionId))
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(id = descriptionText))
                },
                textAlign = TextAlign.Center,
                fontFamily = roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 15.dp)
            )

            Image(
                painter = painterResource(id = screenImgId),
                contentDescription = stringResource(id = screenId) + " image",
                modifier = Modifier
                    .size(60.dp)
                    .testTag(stringResource(id = R.string.screen_logo))
            )
        }

        // Text Fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }

        // Next Page Button
        AuthNextPageButton(
            id = nextPageButtonId,
            text = nextPageButtonText,
            onClick = nextPageButtonOnClick
        )

        // Page flow
        Row(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
        ) {
            Image(
                painter = painterResource(id = pageFlowImgId),
                contentDescription = stringResource(id = screenId) + " page flow",
                modifier = Modifier
                    .size(42.dp)
                    .testTag(
                        stringResource(R.string.pageFlowId)
                    )
            )
        }

        // Back to recent page button
        AuthBackButton(
            R.string.authBackBtnId,
            icon = R.drawable.back_icon,
            size = 60,
            onClick = backBtnOnClick
        )

    }
}

@Preview(showBackground = true)
@Composable
fun AuthInputLayoutPreview() {
    AuthInputLayout(
        screenId = R.string.loginScreenId,
        screenImgId = R.drawable.login_logo,
        descriptionId = R.string.loginDescriptionId,
        descriptionText = R.string.login_desc_text,
        pageFlowImgId = R.drawable.login_page_flow,
        nextPageButtonId = R.string.loginBtnId,
        nextPageButtonText = R.string.login_btn_text,
        nextPageButtonOnClick = {},
        backBtnOnClick = {}
    ) {
        AuthOutlineTextField(
            id = R.string.loginEmailFieldId,
            placeholder = R.string.loginEmailFieldPlaceholder,
            outlineIcon = R.drawable.outline_email,
            filledIcon = R.drawable.filled_email,
            isError = remember { mutableStateOf(false) },
            textState = remember { mutableStateOf("") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthPasswordField(
            id = R.string.loginPasswordFieldId,
            placeholder = R.string.loginPasswordPlaceholder,
            outlineIcon = R.drawable.outline_password,
            filledIcon = R.drawable.filled_password,
            isError = remember { mutableStateOf(false) },
            textState = remember { mutableStateOf("") }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AuthNextPageButtonPreview(){
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        AuthNextPageButton(
//            id = R.string.loginBtnId,
//            text = R.string.login_btn_text,
//            onClick = {}
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun AuthEmailFieldPreview() {
//    val textState = remember { mutableStateOf("") }
//    val isError = remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        AuthEmailField(
//            id = R.string.loginEmailFieldId,
//            placeholder = R.string.loginEmailFieldPlaceholder,
//            outlineIcon = R.drawable.outline_email,
//            filledIcon = R.drawable.filled_email,
//            isError = isError,
//            textState = textState
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun AuthPasswordFieldPreview() {
//    val textState = remember { mutableStateOf("") }
//    val isError = remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        AuthPasswordField(
//            id = R.string.loginEmailFieldId,
//            placeholder = R.string.loginPasswordPlaceholder,
//            outlineIcon = R.drawable.outline_password,
//            filledIcon = R.drawable.filled_password,
//            isError = isError,
//            textState = textState
//        )
//    }
//}

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


