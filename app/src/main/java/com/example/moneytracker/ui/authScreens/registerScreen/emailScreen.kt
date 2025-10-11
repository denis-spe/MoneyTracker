package com.example.moneytracker.ui.authScreens.registerScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.AuthInputLayout
import com.example.moneytracker.ui.AuthOutlineTextField
import com.example.moneytracker.ui.screenManager.PasswordRegistrationScreenRouter

@Composable
fun EmailRegistrationScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigate: NavController? = null,
) {
    val uiState = viewModel.uiState.collectAsState()

    AuthInputLayout(
        screenId = R.string.emailRegisterScreenId,
        screenImgId = R.drawable.email_logo,
        descriptionId = R.string.emailRegisterDescriptionId,
        descriptionText = R.string.email_register_desc_text,
        pageFlowImgId = R.drawable.email_page_flow,
        nextPageButtonId = R.string.emailRegisterBtnId,
        nextPageButtonText = R.string.email_register_btn_text,
        nextPageButtonOnClick = {
            if (viewModel.validateEmailBeforeNavigate()) {
                onNavigate?.navigate(PasswordRegistrationScreenRouter)
            }
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
            isError = false,
            onNewValue = viewModel::onEmailChange,
            text = uiState.value.email,
            isEmail = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmailScreenPreview() {
    EmailRegistrationScreen()
}