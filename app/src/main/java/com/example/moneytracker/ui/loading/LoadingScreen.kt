package com.example.moneytracker.ui.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moneytracker.ui.screenManager.HomeScreenRouter
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay


@Composable
fun LoadingScreen(
    user: State<FirebaseUser?>,
    navController: NavHostController,
    currentUserId: String,
    content: @Composable () -> Unit
) {

    // guard so we only navigate once
    var navigated by remember { mutableStateOf(false) }

    // If you want a short visible delay after image load (optional)
    val postLoadDelayMs = 500L

    // When imageLoaded becomes true, navigate once (side-effect)
    LaunchedEffect(navigated) {
        if (!navigated) {
            // small delay so user sees the UI briefly
            delay(postLoadDelayMs)
            // navigate to home and clear loading from backstack
            navController.navigate(HomeScreenRouter(userId = currentUserId)) {
                popUpTo(0) { inclusive = true } // or use a specific route id
            }
            navigated = true
        }
    }

    // UI: show loader while not navigated
    if (!navigated) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MoneyTrackerTheme.colors.autoBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            content()
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator(strokeCap = StrokeCap.Round)
        }
    }
}
