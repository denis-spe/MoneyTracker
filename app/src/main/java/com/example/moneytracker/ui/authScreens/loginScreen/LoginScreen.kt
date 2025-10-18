// Bless be the Name of the Lord

package com.example.moneytracker.ui.authScreens.loginScreen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.AuthInputLayout
import com.example.moneytracker.ui.AuthOutlineTextField
import com.example.moneytracker.ui.AuthPasswordField
import com.example.moneytracker.ui.screenManager.HomeScreenRouter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: NavController? = null
) {
    val uiState = viewModel.uiState.collectAsState()
    val user = uiState.value.user?.collectAsState(initial = null)

    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    AuthInputLayout(
        screenId = R.string.loginScreenId,
        screenImgId = R.drawable.login_logo,
        descriptionId = R.string.loginDescriptionId,
        descriptionText = R.string.login_desc_text,
        pageFlowImgId = R.drawable.login_page_flow,
        nextPageButtonId = R.string.loginBtnId,
        nextPageButtonText = R.string.login_btn_text,
        isLoading = isLoading,
        isError = uiState.value.isEmailError || uiState.value.isPasswordError ||
                uiState.value.credentialErrorMessage.isNotEmpty(),
        nextPageButtonOnClick = {
            if (viewModel.validateBeforeNavigatingToHome()) {
                isLoading = true
                coroutineScope.launch {
                    delay(5000)
                    isLoading = false
                    onNavigate?.navigate(
                        HomeScreenRouter(userId = user?.value?.id ?: "")
                    )
                }

            }
        },
        backBtnOnClick = {
            onNavigate?.popBackStack()
        }
    ) {
        AuthOutlineTextField(
            id = R.string.loginEmailFieldId,
            placeholder = R.string.loginEmailFieldPlaceholder,
            outlineIcon = R.drawable.outline_email,
            filledIcon = R.drawable.filled_email,
            isError = false,
            text = uiState.value.email,
            onNewValue = viewModel::onEmailChange,
            isEmail = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthPasswordField(
            id = R.string.loginPasswordFieldId,
            placeholder = R.string.loginPasswordPlaceholder,
            outlineIcon = R.drawable.outline_password,
            filledIcon = R.drawable.filled_password,
            isError = false,
            text = uiState.value.password,
            onNewValue = viewModel::onPasswordChange
        )

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}