// Bless be the Name of the Lord

package com.example.moneytracker.ui.authScreens.loginScreen

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
import com.example.moneytracker.ui.AuthOutlineTextField
import com.example.moneytracker.ui.AuthPasswordField
import com.example.moneytracker.ui.DisplayErrorMessage
import com.example.moneytracker.ui.screenManager.HomeScreenRouter

/**
 * Login Screen Composable
 * @param viewModel The ViewModel for the Login Screen
 * @param onNavigate The NavController for navigation
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: NavController? = null
) {
    val uiState = viewModel.uiState.collectAsState()

    AuthInputLayout(
        screenId = R.string.loginScreenId,
        screenImgId = R.drawable.login_logo,
        descriptionId = R.string.loginDescriptionId,
        descriptionText = R.string.login_desc_text,
        pageFlowImgId = R.drawable.login_page_flow,
        nextPageButtonId = R.string.loginBtnId,
        nextPageButtonText = R.string.login_btn_text,
        isLoading = uiState.value.isLoading,
        isError = uiState.value.isEmailError ||
                uiState.value.isPasswordError ||
                uiState.value.credentialErrorMessage.isNotEmpty(),
        nextPageButtonOnClick = {
            viewModel.validateBeforeNavigatingToHome { userId ->
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

        Spacer(modifier = Modifier.height(10.dp))

        // Email Field
        AuthOutlineTextField(
            id = R.string.loginEmailFieldId,
            placeholder = R.string.loginEmailFieldPlaceholder,
            outlineIcon = R.drawable.outline_email,
            filledIcon = R.drawable.filled_email,
            isError = uiState.value.isEmailError,
            text = uiState.value.email,
            onNewValue = viewModel::onEmailChange,
            isEmail = uiState.value.isEmailError
        )

        // Email Error Message
        DisplayErrorMessage(
            id = R.string.errorEmailMessage,
            errorMessage = uiState.value.emailErrorMessage
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password Field
        AuthPasswordField(
            id = R.string.loginPasswordFieldId,
            placeholder = R.string.loginPasswordPlaceholder,
            outlineIcon = R.drawable.outline_password,
            filledIcon = R.drawable.filled_password,
            isError = uiState.value.isPasswordError,
            text = uiState.value.password,
            onNewValue = viewModel::onPasswordChange
        )
        // Credential Error Message
        DisplayErrorMessage(
            id = R.string.errorPasswordMessage,
            errorMessage = uiState.value.passwordErrorMessage
        )

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}