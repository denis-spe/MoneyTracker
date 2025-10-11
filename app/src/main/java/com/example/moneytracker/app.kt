package com.example.moneytracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.ui.screenManager.ScreenManager

@Composable
fun App(){
    ScreenManager(navController = rememberNavController())
}