// Bless be the name of LORD of hosts
package com.example.moneytracker.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.screenManager.HomeScreenRouter
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter
import com.example.moneytracker.ui.theme.StewardTheme
import kotlinx.coroutines.delay
import java.util.Calendar

/**
 * A unified Loading Screen that supports both splash-style navigation and in-app loading states.
 *
 * @param user The current FirebaseUser state. If provided with [navController], handles navigation.
 * @param navController The NavHostController used for navigation after loading.
 * @param currentUserId The ID of the current user for home screen navigation.
 * @param content Optional custom content to display in the middle of the screen.
 */
@Composable
fun LoadingScreen(
    user: Boolean = false,
    navController: NavController? = null,
    currentUserId: String? = null,
    isSplashScreen: Boolean = false,
    content: @Composable (() -> Unit)? = null
) {
    val customColors = StewardTheme.colors
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    // Navigation logic (only if user and navController are provided)
    var navigated by remember { mutableStateOf(false) }
    val postLoadDelayMs = 500L

    Log.d("LoadingScreen", "LoadingScreen recomposed. User: ${user}, Navigated: $navigated")

    if (navController != null) {
        LaunchedEffect(user) {
            if (!user) {
                // User signed out or session lost, go back to startup
                navController.navigate(StartUpScreenRouter) {
                    popUpTo(0) { inclusive = true }
                }
                return@LaunchedEffect
            }

            if (isSplashScreen && !navigated) {
                delay(postLoadDelayMs)
                navController.navigate(HomeScreenRouter(userId = currentUserId ?: "")) {
                    popUpTo(0) { inclusive = true }
                }
                navigated = true
            }
        }
    }

    if (!navigated) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StewardTheme.colors.primaryAccent),
            contentAlignment = Alignment.Center
        ) {
            // Top/Center Area: App Name
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(bottom = 100.dp), // Offset slightly upwards
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = customColors.accentContent,
                    style = typography.headlineMedium,
                    fontSize = 30.sp
                )

                if (content != null) {
                    Spacer(modifier = Modifier.height(32.dp))
                    content()
                }
            }

            // Bottom Area: Progress indicator + Copyright
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 65.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 3.dp,
                        strokeCap = StrokeCap.Round,
                        color = StewardTheme.colors.accentContent,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = buildString {
                            append("Glory be to the Lord of hosts\n")
                            append("Copyright@$currentYear Den.\n All rights reserved.")
                        },
                        color = customColors.accentContent,
                        style = typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
