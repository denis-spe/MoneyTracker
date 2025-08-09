package com.example.moneytracker.ui.startUpScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.example.moneytracker.ui.AuthButton
import com.example.moneytracker.ui.AuthFillButton
import com.example.moneytracker.ui.AuthLayout
import com.example.moneytracker.ui.AuthTextButton
import com.example.moneytracker.ui.screenManager.GoogleScreenRouter
import com.example.moneytracker.ui.screenManager.MailScreenRouter

@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope",
    "MissingColorAlphaChannel"
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartUpScreen(onNavigate: NavController? = null){
    AuthLayout(screenId = R.string.startUpScreenId) {

        Column(
            modifier = Modifier
                .padding(top = 120.dp)
                .fillMaxWidth(0.8f),
        ) {
            val redressedFont = FontFamily(
                Font(R.font.redressed_regular, FontWeight.Normal)
            )

            Text(
                text = stringResource(R.string.introduction_text),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 23.sp,
                    fontFamily = redressedFont,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.25f),
                        offset = Offset(8f, 5f),
                        blurRadius = 3.4f
                    )
                )
            )
        }

        Column(
            modifier = Modifier.padding(top = 50.dp)
        ) {
            Text(
                buildAnnotatedString {
                    append(stringResource(R.string.introduction_subText))
                },
                style = TextStyle(
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    brush = Brush.linearGradient(
                        0.0f to Color(0xFFD6E2D2),
                        1.0f to Color(0xFF0C1B08),
                        start = Offset(x = 0f, y = 0.5f)
                    )
                ),
            )
        }

        // Start up buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 25.dp)
        ) {
            AuthFillButton(
                id = R.string.startupGoogleBtnId,
                text = R.string.startup_google_text,
                icon = R.drawable.google_icon,
                modifier = Modifier.width(170.dp)
            ) {
                onNavigate?.navigate(route = GoogleScreenRouter)
            }

            Text(
                stringResource(R.string.or_text),
                fontWeight = FontWeight.SemiBold,
                )

            AuthButton(
                id = R.string.startupMailBtnId,
                text = R.string.startup_mail_text,
                icon = R.drawable.email_icon,
                modifier = Modifier.width(170.dp)
            ) {
                onNavigate?.navigate(route = MailScreenRouter)
            }
        }

        Row {
            Image(
                painter = painterResource(id = R.drawable.page_flow),
                contentDescription = "page flow",
                modifier = Modifier
                    .size(42.dp)
                    .testTag(stringResource(R.string.startup_page_flow_img))
                )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 55.dp)
        ) {
            AuthTextButton(
                id = R.string.startupContinueGuestId,
                text = R.string.startup_continue_guest_text
            ) {

            }
        }
    }
}


@Preview(showBackground = true, device = "id:medium_phone")
@Composable
fun StartUpScreenPreview(){
    StartUpScreen()
}