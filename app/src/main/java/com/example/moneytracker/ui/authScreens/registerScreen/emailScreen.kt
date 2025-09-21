package com.example.moneytracker.ui.authScreens.registerScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.AuthInputLayout
import com.example.moneytracker.ui.AuthOutlineTextField
import com.example.moneytracker.ui.screenManager.NamesRegistrationScreenRouter

@Composable
fun EmailRegistrationScreen(onNavigate: NavController? = null) {

    val emailState = remember { mutableStateOf("") }
    val isErrorEmail = remember { mutableStateOf(false) }


    AuthInputLayout(
        screenId = R.string.emailRegisterScreenId,
        screenImgId = R.drawable.email_logo,
        descriptionId = R.string.emailRegisterDescriptionId,
        descriptionText = R.string.email_register_desc_text,
        pageFlowImgId = R.drawable.email_page_flow,
        nextPageButtonId = R.string.emailRegisterBtnId,
        nextPageButtonText = R.string.email_register_btn_text,
        nextPageButtonOnClick = {
            onNavigate?.navigate(NamesRegistrationScreenRouter)
        },
        backBtnOnClick = {
            onNavigate?.popBackStack()
        }
    ) {
        AuthOutlineTextField(
            id = R.string.emailRegisterEmailFieldId,
            placeholder = R.string.emailRegisterEmailFieldPlaceholder,
            outlineIcon = R.drawable.outline_email,
            filledIcon = R.drawable.filled_email,
            isError = isErrorEmail,
            textState = emailState,
            isEmail = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmailScreenPreview() {
    EmailRegistrationScreen()
}