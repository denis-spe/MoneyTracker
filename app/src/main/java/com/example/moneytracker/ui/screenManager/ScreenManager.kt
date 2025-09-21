package com.example.moneytracker.ui.screenManager

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.ui.authScreens.googleScreen.GoogleScreen
import com.example.moneytracker.ui.authScreens.loginScreen.LoginScreen
import com.example.moneytracker.ui.authScreens.mailScreen.MailScreen
import com.example.moneytracker.ui.authScreens.registerScreen.EmailRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.NamesRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.PasswordRegistrationScreen
import com.example.moneytracker.ui.homeScreen.HomeScreen
import com.example.moneytracker.ui.startUpScreen.StartUpScreen

@Composable
fun ScreenManager(
    navController: NavHostController = rememberNavController()
) {

    NavHost(navController = navController, startDestination = StartUpScreenRouter) {
        composable<StartUpScreenRouter> { StartUpScreen(navController) }
        composable<GoogleScreenRouter> { GoogleScreen(navController) }
        composable<MailScreenRouter> { MailScreen(navController) }
        composable<NamesRegistrationScreenRouter> {
            NamesRegistrationScreen(navController)
        }
        composable<EmailRegistrationScreenRouter> {
            EmailRegistrationScreen(navController)
        }
        composable<PasswordRegistrationScreenRouter> {
            PasswordRegistrationScreen(navController)
        }
        composable<LoginScreenRouter> { LoginScreen(navController) }
        composable<HomeScreenRouter> { HomeScreen(navController) }
    }
}

@Preview
@Composable
fun ScreenManagerPreview() {
    ScreenManager()
}