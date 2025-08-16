// Bless be the Name of the Lord
package com.example.moneytracker.ui.authScreens.loginScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.moneytracker.ui.AuthLayout

@Composable
fun LoginScreen(onNavigate: NavController? = null) {
    val roboto = FontFamily(
        Font(
            R.font.roboto,
            FontWeight.Medium,
        )
    )

    AuthLayout(screenId = R.string.loginScreenId) {
        // Login description
        Column(
            modifier = Modifier
                .padding(top = 120.dp, bottom = 80.dp)
                .testTag(stringResource(R.string.loginDescriptionId)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.login_desc_text))
                },
                textAlign = TextAlign.Center,
                fontFamily = roboto,
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.login_img),
                contentDescription = "login icon",
                modifier = Modifier
                    .size(60.dp)
                    .testTag(stringResource(R.string.login_img))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}