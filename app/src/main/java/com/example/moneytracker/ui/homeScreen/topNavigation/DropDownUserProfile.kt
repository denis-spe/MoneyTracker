// Love the LORD your GOD with all your soul and with all your mind
// And with all your strength and love your neighbor as your self.
package com.example.moneytracker.ui.homeScreen.topNavigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.moneytracker.ui.components.ProfileImage
import com.example.moneytracker.ui.homeScreen.Colors
import com.google.firebase.auth.FirebaseUser

@Composable
fun DropDownUserProfile(
    paddingValues: PaddingValues,
    visible: Boolean = false,
    userState: State<FirebaseUser?>,
    onClick: () -> Unit,
) {

    val colors = Colors()

    // Define the colors for the button
    val contentColor = (if (isSystemInDarkTheme()) colors.darkModeColor else colors.lightModeColor)

    // Panel and button color
    val backgroundColor = if (isSystemInDarkTheme()) colors.darkModeBackgroundColor else
        colors.lightModeBackgroundColor

    val density = LocalDensity.current

    val userNames = userState.value?.displayName.let {
        it?.split(" ") ?: listOf("Guest")
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut(
            // Fade in with the initial alpha of 0.3f.
            targetAlpha = 0.3f
        )
    ) {

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .width(220.dp)
                    .height(200.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp, topEnd = 0.dp,
                            bottomStart = 30.dp,
                            bottomEnd = 30.dp
                        )
                    )
                    .background(backgroundColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    userState.value.let {
                        Log.d("Photo", it?.photoUrl.toString())
                        if (it != null && it.photoUrl != null) {
                            ProfileImage(
                                accountSpecificUrl = it.photoUrl,
                                currentAccountId = it.uid,
                                size = 50
                            )
                            Text(
                                userNames[0],
                                color = contentColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                            Text(
                                text = if (userNames.size > 1) userNames[1] else "",
                                color = contentColor,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                                    .padding(5.dp)
                                    .border(4.dp, contentColor.copy(0.5f), RoundedCornerShape(100))
                                    .clip(RoundedCornerShape(100))
                                    .background(Color(0xFFDC0B1E)),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (it != null && it.uid.isNotEmpty())
                                    Text(it.uid[0].toString(), color = contentColor)
                            }

                            Text(
                                "Guest",
                                color = contentColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }

                    TextButton(
                        onClick = onClick
                    ) {
                        Text(
                            text = "Sign out",
                            color = contentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
