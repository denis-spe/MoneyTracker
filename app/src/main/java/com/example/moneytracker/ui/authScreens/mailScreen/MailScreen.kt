// Bless be the Name of the Lord
package com.example.moneytracker.ui.authScreens.mailScreen

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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.AuthBackButton
import com.example.moneytracker.ui.AuthButton
import com.example.moneytracker.ui.AuthFillButton
import com.example.moneytracker.ui.AuthLayout
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter

@Composable
fun MailScreen(onNavigate: NavController? = null) {
    val roboto = FontFamily(
        Font(
            R.font.roboto,
            FontWeight.Medium,
        )
    )

    AuthLayout(screenId = R.string.mailScreenId) {
        // Mail Description
        Column(
            modifier = Modifier
                .padding(top = 120.dp, bottom = 80.dp)
                .testTag(stringResource(R.string.mailDescriptionId)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.mail_description_text))
                },
                textAlign = TextAlign.Center,
                fontFamily = roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp
            )

            Image(
                painter = painterResource(id = R.drawable.email_icon),
                contentDescription = "mail icon",
                modifier = Modifier
                    .size(60.dp)
                    .testTag(stringResource(R.string.mail_img))
            )
        }

        // Login and register with mail
        AuthFillButton(
            R.string.mailLoginBtnId,
            text = R.string.mail_login_text,
            icon = R.drawable.login_icon,
            modifier = Modifier.width(160.dp)
        ) {
        }

        Text(
            stringResource(R.string.or_text),
            fontWeight = FontWeight.SemiBold,
        )

        AuthButton(
            R.string.mailRegisterBtnId,
            text = R.string.mail_register_text,
            icon = R.drawable.register_icon,
            modifier = Modifier.width(160.dp)
        ) {
        }

        Row(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.mail_page_flow),
                contentDescription = "page flow",
                modifier = Modifier
                    .size(42.dp)
                    .testTag(stringResource(R.string.mail_page_flow_img))
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
fun MailScreenPreview() {
    MailScreen()
}