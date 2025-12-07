// ======
// Bless be the name of LORD of hosts
// ======
package com.example.moneytracker.ui.homeScreen.topNavigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moneytracker.ui.homeScreen.Colors
import com.google.firebase.auth.FirebaseUser

@Composable
fun TopNavPanel(userState: State<FirebaseUser?>, onClick: () -> Unit = {}) {
    val userName = userState.value?.displayName
    val uid = userState.value?.uid

    val colors = Colors()

    // Define the colors for the button
    val contentColor = (if (isSystemInDarkTheme()) colors.darkModeColor else colors.lightModeColor)

    Column(
        modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .padding(5.dp)
            .border(4.dp, contentColor.copy(0.5f), RoundedCornerShape(100))
            .clip(RoundedCornerShape(100))
            .background(Color(0xFF009688))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (userName != null && userName.isNotEmpty())
            Text(userName[0].toString(), color = contentColor)
        else {
            Text(uid?.get(0).toString(), color = contentColor)
        }
    }
}