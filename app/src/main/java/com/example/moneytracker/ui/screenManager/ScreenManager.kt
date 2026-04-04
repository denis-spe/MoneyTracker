package com.example.moneytracker.ui.screenManager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.moneytracker.backend.auth.AccountServicesImpl
import com.example.moneytracker.ui.authScreens.loginScreen.LoginScreen
import com.example.moneytracker.ui.authScreens.mailScreen.MailScreen
import com.example.moneytracker.ui.authScreens.registerScreen.EmailRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.NamesRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.PasswordRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.RegisterViewModel
import com.example.moneytracker.ui.homeScreen.HomeScreen
import com.example.moneytracker.ui.loading.LoadingScreen
import com.example.moneytracker.ui.loading.LoadingViewModel
import com.example.moneytracker.ui.settings.SettingsScreen
import com.example.moneytracker.ui.startUpScreen.StartUpScreen
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun ScreenManager(
    navController: NavHostController = rememberNavController()
) {

    val account = AccountServicesImpl(FirebaseAuth.getInstance())
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val user = account.userState.collectAsState()
    val loadingViewModel: LoadingViewModel = hiltViewModel()

    val router = if (account.hasUser) {
        HomeScreenRouter(userId = account.currentUserId)
    } else {
        StartUpScreenRouter
    }

    NavHost(navController = navController, startDestination = router) {
        composable<StartUpScreenRouter> {
            StartUpScreen(
                navController,
                loadingViewModel = loadingViewModel
            )
        }
        composable<LoadingScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<LoadingScreenRouter>()
            val currentUserId = arguments.userId

            // 2. Pass the fresh userId to the LoadingScreen
            LoadingScreen(
                user = user,
                navController = navController,
                currentUserId = currentUserId, // Use the argument, not the old state
                content = loadingViewModel.content ?: {}
            )
        }
        composable<MailScreenRouter> { MailScreen(navController) }
        composable<NamesRegistrationScreenRouter> {
            NamesRegistrationScreen(
                viewModel = registerViewModel,
                onNavigate = navController
            )
        }
        composable<EmailRegistrationScreenRouter> {
            EmailRegistrationScreen(
                viewModel = registerViewModel,
                onNavigate = navController
            )
        }
        composable<PasswordRegistrationScreenRouter> {
            PasswordRegistrationScreen(
                viewModel = registerViewModel,
                onNavigate = navController
            )
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
        composable<SettingsScreenRouter> {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview
@Composable
fun ScreenManagerPreview() {
    ScreenManager()
}