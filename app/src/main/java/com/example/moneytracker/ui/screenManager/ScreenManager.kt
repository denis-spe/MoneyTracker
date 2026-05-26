package com.example.moneytracker.ui.screenManager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.moneytracker.backend.auth.AccountServices
import com.example.moneytracker.ui.LoadingScreen
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.authScreens.loginScreen.LoginScreen
import com.example.moneytracker.ui.authScreens.mailScreen.MailScreen
import com.example.moneytracker.ui.authScreens.registerScreen.EmailRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.NamesRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.PasswordRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.RegisterViewModel
import com.example.moneytracker.ui.detailScreen.GoalDetailScreen
import com.example.moneytracker.ui.homeScreen.HomeScreen
import com.example.moneytracker.ui.loading.LoadingViewModel
import com.example.moneytracker.ui.settings.SettingsScreen
import com.example.moneytracker.ui.startUpScreen.StartUpScreen

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun ScreenManager(
    navController: NavHostController = rememberNavController(),
    account: AccountServices = hiltViewModel<ScreenManagerViewModel>().account
) {
    val registerViewModel: RegisterViewModel = hiltViewModel()
    val loadingViewModel: LoadingViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

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
            val user by userViewModel.userState.collectAsStateWithLifecycle()

            // 2. Pass the fresh userId to the LoadingScreen
            LoadingScreen(
                user = user != null,
                navController = navController,
                currentUserId = currentUserId, // Use the argument, not the old state
                isSplashScreen = true,
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
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate(MailScreenRouter) },
                navController = navController,
                userId = account.currentUserId
            )
        }
        composable<FulfillmentDetailScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<FulfillmentDetailScreenRouter>()
            val goalId = arguments.goalId
            GoalDetailScreen(
                goalId = goalId,
                navController = navController,
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