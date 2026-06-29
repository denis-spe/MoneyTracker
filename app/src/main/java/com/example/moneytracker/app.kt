package com.example.moneytracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.screenManager.ScreenManager
import com.example.moneytracker.ui.showAll.ShowAllViewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun App(
    homeViewModel: HomeViewModel,
    showAllViewModel: ShowAllViewModel,
    onFullyDrawn: () -> Unit
) {
    ScreenManager(
        homeViewModel = homeViewModel,
        showAllViewModel = showAllViewModel,
        navController = rememberNavController(),
        onFullyDrawn = onFullyDrawn
    )
}