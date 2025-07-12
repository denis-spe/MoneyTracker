package com.example.moneytracker.ui.startUpScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.authScreens.AuthButton
import com.example.moneytracker.ui.authScreens.AuthFillButton
import com.example.moneytracker.ui.screenManager.GoogleScreenRouter
import com.example.moneytracker.ui.screenManager.MailScreenRouter

@Composable
fun StartUpScreen(onNavigate: NavController? = null){
    Column(
        modifier = Modifier.fillMaxSize()
            .testTag(stringResource(R.string.startUpScreenId)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column {
            Text(text = stringResource(R.string.title_money))
            Text(text = stringResource(R.string.title_tracker))
            Text(text = stringResource(R.string.introduction_text))
            Text(text = stringResource(R.string.introduction_subText))
        }

        // Start up buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AuthFillButton(
                id = R.string.googleBtnId,
                text = R.string.google_text,
                icon = R.drawable.google_icon
            ) {
                onNavigate?.navigate(route = GoogleScreenRouter)
            }

            AuthButton(
                id = R.string.mailBtnId,
                text = R.string.mail_text,
                icon = R.drawable.email_icon
            ) {
                onNavigate?.navigate(route = MailScreenRouter)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StartUpScreenPreview(){
    StartUpScreen()
}