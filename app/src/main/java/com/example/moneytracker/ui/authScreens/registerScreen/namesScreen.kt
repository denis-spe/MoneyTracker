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
import com.example.moneytracker.ui.AuthOutlineTextField
import com.example.moneytracker.ui.screenManager.NamesRegistrationScreenRouter

@Composable
fun NamesScreen(onNavigate: NavController? = null) {

    val firstNameState = remember { mutableStateOf("") }
    val isFirstNameError = remember { mutableStateOf(false) }

    val lastNameState = remember { mutableStateOf("") }
    val isLastNameError = remember { mutableStateOf(false) }


    AuthInputLayout(
        screenId = R.string.nameRegisterScreenId,
        screenImgId = R.drawable.name_logo,
        descriptionId = R.string.nameRegisterDescriptionId,
        descriptionText = R.string.name_register_desc_text,
        pageFlowImgId = R.drawable.name_page_flow,
        nextPageButtonId = R.string.nameRegisterBtnId,
        nextPageButtonText = R.string.name_register_btn_text,
        nextPageButtonOnClick = {
            onNavigate?.navigate(
                NamesRegistrationScreenRouter(firstNameState.value)
            )
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
            isError = isFirstNameError,
            textState = firstNameState,
        )

        Spacer(modifier = Modifier.height(20.dp))

        AuthOutlineTextField(
            id = R.string.nameRegisterLastNameFieldId,
            placeholder = R.string.nameRegisterLastNameFieldPlaceholder,
            outlineIcon = R.drawable.outline_name,
            filledIcon = R.drawable.filled_name,
            isError = isLastNameError,
            textState = lastNameState,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NamesScreenPreview() {
    NamesScreen()
}