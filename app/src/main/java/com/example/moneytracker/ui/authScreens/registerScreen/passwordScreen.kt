package com.example.moneytracker.ui.authScreens.registerScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.AuthInputLayout
import com.example.moneytracker.ui.AuthPasswordField

@Composable
fun PasswordScreen(onNavigate: NavController? = null) {

    val passwordState = remember { mutableStateOf("") }
    val isErrorPassword = remember { mutableStateOf(false) }

    val confirmPasswordState = remember { mutableStateOf("") }
    val isConfirmPasswordError = remember { mutableStateOf(false) }


    AuthInputLayout(
        screenId = R.string.passwordRegisterScreenId,
        screenImgId = R.drawable.password_logo,
        descriptionId = R.string.passwordRegisterDescriptionId,
        descriptionText = R.string.password_register_desc_text,
        pageFlowImgId = R.drawable.password_page_flow,
        nextPageButtonId = R.string.passwordRegisterBtnId,
        nextPageButtonText = R.string.password_register_btn_text,
        nextPageButtonOnClick = {

        },
        backBtnOnClick = {
            onNavigate?.popBackStack()
        }
    ) {
        AuthPasswordField(
            id = R.string.passwordRegisterPasswordFieldId,
            placeholder = R.string.passwordRegisterPasswordFieldPlaceholder,
            outlineIcon = R.drawable.outline_password,
            filledIcon = R.drawable.filled_password,
            isError = isErrorPassword,
            textState = passwordState
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthPasswordField(
            id = R.string.passwordConfirmRegisterPasswordFieldId,
            placeholder = R.string.passwordConfirmRegisterPasswordFieldPlaceholder,
            outlineIcon = R.drawable.outline_password,
            filledIcon = R.drawable.filled_password,
            isError = isConfirmPasswordError,
            textState = confirmPasswordState
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordScreenPreview() {
    PasswordScreen()
}