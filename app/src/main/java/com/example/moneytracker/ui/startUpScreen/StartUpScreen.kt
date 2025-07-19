package com.example.moneytracker.ui.startUpScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.authScreens.AuthButton
import com.example.moneytracker.ui.authScreens.AuthFillButton
import com.example.moneytracker.ui.authScreens.AuthHeader
import com.example.moneytracker.ui.screenManager.GoogleScreenRouter
import com.example.moneytracker.ui.screenManager.MailScreenRouter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartUpScreen(onNavigate: NavController? = null){
    Column(
        modifier = Modifier.fillMaxSize()
            .testTag(stringResource(R.string.startUpScreenId)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        //
        AuthHeader()

        Column(
            modifier = Modifier.padding(top = 120.dp)
                .fillMaxWidth(0.8f),
        ) {
            val redressedFont = FontFamily(
                Font(R.font.redressed_regular, FontWeight.Normal)
            )

            Text(
                text = stringResource(R.string.introduction_text),
                textAlign = TextAlign.Center,
                fontSize = 23.sp,
                fontFamily = redressedFont
            )
        }

        Column(
            modifier = Modifier.padding(top = 50.dp)
        ) {
            Text(text = stringResource(
                R.string.introduction_subText),
                fontSize = 16.sp
            )
        }

        // Start up buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 25.dp)
        ) {
            AuthFillButton(
                id = R.string.googleBtnId,
                text = R.string.google_text,
                icon = R.drawable.google_icon,
                modifier = Modifier.width(160.dp)
            ) {
                onNavigate?.navigate(route = GoogleScreenRouter)
            }

            Text("OR",
                fontWeight = FontWeight.SemiBold,
                )

            AuthButton(
                id = R.string.mailBtnId,
                text = R.string.mail_text,
                icon = R.drawable.email_icon,
                modifier = Modifier.width(160.dp)
            ) {
                onNavigate?.navigate(route = MailScreenRouter)
            }
        }

        Row {
            Image(
                painter = painterResource(id = R.drawable.page_flow),
                contentDescription = "page flow",
                modifier = Modifier.size(42.dp)
                )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StartUpScreenPreview(){
    StartUpScreen()
}