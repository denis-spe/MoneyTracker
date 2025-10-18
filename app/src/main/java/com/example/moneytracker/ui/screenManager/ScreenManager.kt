package com.example.moneytracker.ui.screenManager

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneytracker.backend.auth.AccountServicesImpl
import com.example.moneytracker.ui.authScreens.googleScreen.GoogleScreen
import com.example.moneytracker.ui.authScreens.loginScreen.LoginScreen
import com.example.moneytracker.ui.authScreens.mailScreen.MailScreen
import com.example.moneytracker.ui.authScreens.registerScreen.EmailRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.NamesRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.PasswordRegistrationScreen
import com.example.moneytracker.ui.homeScreen.HomeScreen
import com.example.moneytracker.ui.startUpScreen.StartUpScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ScreenManager(
    navController: NavHostController = rememberNavController()
) {

    val account = AccountServicesImpl(FirebaseAuth.getInstance())
    val startDestination = if (account.hasUser) {
        HomeScreenRouter(userId = account.currentUserId)
    } else {
        StartUpScreenRouter
    }

    NavHost(navController = navController, startDestination = StartUpScreenRouter) {
        composable<StartUpScreenRouter> { StartUpScreen(navController) }
        composable<GoogleScreenRouter> { GoogleScreen(navController) }
        composable<MailScreenRouter> { MailScreen(navController) }
        composable<NamesRegistrationScreenRouter> {
            NamesRegistrationScreen(onNavigate = navController)
        }
        composable<EmailRegistrationScreenRouter> {
            EmailRegistrationScreen(onNavigate = navController)
        }
        composable<PasswordRegistrationScreenRouter> {
            PasswordRegistrationScreen(onNavigate = navController)
        }
        composable<LoginScreenRouter> {
            LoginScreen(onNavigate = navController)
        }
        composable<HomeScreenRouter> { backStackEntry ->
            HomeScreen(
                userId = backStackEntry.arguments?.getString("userId").orEmpty(),
                onNavigate = navController
            )
        }
    }
}

@Preview
@Composable
fun ScreenManagerPreview() {
    ScreenManager()
}