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
import com.example.moneytracker.ui.components.AuthInputLayout
import com.example.moneytracker.ui.components.AuthOutlineTextField
import com.example.moneytracker.ui.components.DisplayErrorMessage
import com.example.moneytracker.ui.screenManager.EmailRegistrationScreenRouter

@Composable
fun NamesRegistrationScreen(
    viewModel: RegisterViewModel,
    onNavigate: NavController? = null,
) {
    val uiState = viewModel.uiState.collectAsState()

    AuthInputLayout(
        screenId = R.string.nameRegisterScreenId,
        screenImgId = R.drawable.name_logo,
        descriptionId = R.string.nameRegisterDescriptionId,
        descriptionText = R.string.name_register_desc_text,
        pageFlowImgId = R.drawable.name_page_flow,
        nextPageButtonId = R.string.nameRegisterBtnId,
        nextPageButtonText = R.string.name_register_btn_text,
        isError = uiState.value.isErrorInFirstName ||
                uiState.value.isErrorInLastName,
        nextPageButtonOnClick = {
            viewModel.validateNameBeforeNavigateToEmail {
                onNavigate?.navigate(EmailRegistrationScreenRouter)
            }
        },
        backBtnOnClick = {
            onNavigate?.popBackStack()
        }
    ) {
        AuthOutlineTextField(
            id = R.string.nameRegisterFirstNameFieldId,
            placeholder = R.string.nameRegisterFirstNameFieldPlaceholder,
            outlineIcon = R.drawable.outline_name,
            filledIcon = R.drawable.filled_name,
            isError = uiState.value.isErrorInFirstName,
            onNewValue = viewModel::onFirstNameChange,
            text = uiState.value.firstName
        )
        // Email Error Message
        DisplayErrorMessage(
            id = R.string.errorFirstNameMessage,
            errorMessage = uiState.value.firstNameErrorMessage
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthOutlineTextField(
            id = R.string.nameRegisterLastNameFieldId,
            placeholder = R.string.nameRegisterLastNameFieldPlaceholder,
            outlineIcon = R.drawable.outline_name,
            filledIcon = R.drawable.filled_name,
            onNewValue = viewModel::onLastNameChange,
            isError = uiState.value.isErrorInLastName,
            text = uiState.value.lastName
        )
        // Email Error Message
        DisplayErrorMessage(
            id = R.string.errorLastNameMessage,
            errorMessage = uiState.value.lastNameErrorMessage
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NamesScreenPreview() {
    NamesRegistrationScreen(viewModel = hiltViewModel())
}