package com.example.moneytracker.ui.screenManager

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.ui.authScreens.googleScreen.GoogleScreen
import com.example.moneytracker.ui.authScreens.loginScreen.LoginScreen
import com.example.moneytracker.ui.authScreens.mailScreen.MailScreen
import com.example.moneytracker.ui.homeScreen.HomeScreen
import com.example.moneytracker.ui.startUpScreen.StartUpScreen

@Composable
fun ScreenManager(){
    // Create a NavController
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination =  StartUpScreenRouter) {
        composable<StartUpScreenRouter> { StartUpScreen(navController) }
        composable<GoogleScreenRouter> { GoogleScreen( navController ) }
        composable<MailScreenRouter> { MailScreen( navController ) }
        composable<LoginScreenRouter> { LoginScreen( navController ) }
        composable<HomeScreenRouter> { HomeScreen( navController ) }
    }
}