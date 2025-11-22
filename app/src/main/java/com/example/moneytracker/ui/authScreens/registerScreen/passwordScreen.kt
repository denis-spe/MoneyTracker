package com.example.moneytracker.ui.authScreens.registerScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.AuthInputLayout
import com.example.moneytracker.ui.AuthPasswordField
import com.example.moneytracker.ui.DisplayErrorMessage
import com.example.moneytracker.ui.screenManager.HomeScreenRouter


@Composable
fun PasswordRegistrationScreen(
    viewModel: RegisterViewModel,
    onNavigate: NavController? = null,
) {
    val uiState = viewModel.uiState.collectAsState()

    AuthInputLayout(
        screenId = R.string.passwordRegisterScreenId,
        screenImgId = R.drawable.password_logo,
        descriptionId = R.string.passwordRegisterDescriptionId,
        descriptionText = R.string.password_register_desc_text,
        pageFlowImgId = R.drawable.password_page_flow,
        nextPageButtonId = R.string.passwordRegisterBtnId,
        nextPageButtonText = R.string.password_register_btn_text,
        isLoading = uiState.value.isLoading,
        isError = uiState.value.isErrorInPassword ||
                uiState.value.isErrorInConfirmPassword ||
                uiState.value.credentialErrorMessage.isNotEmpty(),
        nextPageButtonOnClick = {
            viewModel.validatePasswordBeforeNavigateToHome { userId ->
                onNavigate?.navigate(
                    HomeScreenRouter(
                        userId = userId
                    )
                )
            }
        },
        backBtnOnClick = {
            onNavigate?.popBackStack()
        }
    ) {
        // Credential Error Message
        DisplayErrorMessage(
            id = R.string.credentialErrorMessage,
            errorMessage = uiState.value.credentialErrorMessage
        )

        AuthPasswordField(
            id = R.string.passwordRegisterPasswordFieldId,
            placeholder = R.string.passwordRegisterPasswordFieldPlaceholder,
            outlineIcon = R.drawable.outline_password,
            filledIcon = R.drawable.filled_password,
            isError = uiState.value.isErrorInPassword,
            text = uiState.value.password,
            onNewValue = viewModel::onPasswordChange
        )

        // Password Error Message
        DisplayErrorMessage(
            id = R.string.errorPasswordMessage,
            errorMessage = uiState.value.passwordErrorMessage
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthPasswordField(
            id = R.string.passwordConfirmRegisterPasswordFieldId,
            placeholder = R.string.passwordConfirmRegisterPasswordFieldPlaceholder,
            outlineIcon = R.drawable.outline_password,
            filledIcon = R.drawable.filled_password,
            isError = uiState.value.isErrorInConfirmPassword,
            text = uiState.value.confirmPassword,
            onNewValue = viewModel::onConfirmPasswordChange
        )

        // Confirm Password Error Message
        DisplayErrorMessage(
            id = R.string.errorConfirmPasswordMessage,
            errorMessage = uiState.value.confirmPasswordErrorMessage
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordScreenPreview() {

    PasswordRegistrationScreen(viewModel = hiltViewModel())
}