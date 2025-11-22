package com.example.moneytracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.ui.screenManager.ScreenManager

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun App(){
    ScreenManager(navController = rememberNavController())
}