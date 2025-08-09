// Bless be the Name of the Lord
package com.example.moneytracker.ui.authScreens.googleScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.AuthBackButton
import com.example.moneytracker.ui.AuthButton
import com.example.moneytracker.ui.AuthFillButton
import com.example.moneytracker.ui.AuthLayout
import com.example.moneytracker.ui.screenManager.HomeScreenRouter
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter

@Composable
fun GoogleScreen(onNavigate: NavController? = null) {
    AuthLayout(screenId = R.string.googleScreenId) {

        // Google Description
        Column(
            modifier = Modifier
                .padding(top = 120.dp, bottom = 80.dp)
                .testTag(stringResource(R.string.googleDescriptionId)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val roboto = FontFamily(
                Font(
                    R.font.roboto,
                    FontWeight.Medium,
                )
            )
            Text(
                text = stringResource(R.string.google_instruction_text),
                fontFamily = roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.google_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(35.dp)
                        .testTag(stringResource(R.string.google_img))
                )
                Text(
                    text = stringResource(R.string.google_account_text),
                    fontFamily = roboto,
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp
                )
            }
        }

        // Login and register with google
        AuthFillButton(
            R.string.googleLoginBtnId,
            text = R.string.google_login_text,
            icon = R.drawable.login_icon,
            modifier = Modifier.width(160.dp)
        ) {
            onNavigate?.navigate(HomeScreenRouter("PlaceHolder"))
        }

        Text(
            stringResource(R.string.or_text),
            fontWeight = FontWeight.SemiBold,
        )

        AuthButton(
            R.string.googleRegisterBtnId,
            text = R.string.google_register_text,
            icon = R.drawable.register_icon,
            modifier = Modifier.width(160.dp)
        ) {
            onNavigate?.navigate(HomeScreenRouter("PlaceHolder"))
        }

        Row(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_page_flow),
                contentDescription = "page flow",
                modifier = Modifier
                    .size(42.dp)
                    .testTag(stringResource(R.string.google_page_flow_img))
            )
        }

        AuthBackButton(
            R.string.authBackBtnId,
            icon = R.drawable.back_icon,
            size = 60
        ) {
            onNavigate?.navigate(StartUpScreenRouter)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoogleScreenPreview() {
    GoogleScreen()
}