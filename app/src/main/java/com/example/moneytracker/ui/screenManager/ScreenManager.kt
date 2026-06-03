package com.example.moneytracker.ui.screenManager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.moneytracker.helper.safePopBackStack
import com.example.moneytracker.ui.LoadingScreen
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.authScreens.loginScreen.LoginScreen
import com.example.moneytracker.ui.authScreens.mailScreen.MailScreen
import com.example.moneytracker.ui.authScreens.registerScreen.EmailRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.NamesRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.PasswordRegistrationScreen
import com.example.moneytracker.ui.authScreens.registerScreen.RegisterViewModel
import com.example.moneytracker.ui.detailScreen.GoalDetailScreen
import com.example.moneytracker.ui.detailScreen.SettlementDetailScreen
import com.example.moneytracker.ui.detailScreen.TransactionDetailScreen
import com.example.moneytracker.ui.homeScreen.HomeScreen
import com.example.moneytracker.ui.homeScreen.HomeViewModel
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
        LoadingScreenRouter(userId = account.currentUserId)
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

            // Collect the user state
            val user by userViewModel.userState.collectAsStateWithLifecycle()

            // Scoped correctly via Hilt, collecting ONLY the unified state
            val homeViewModel: HomeViewModel = hiltViewModel()
            val isDataLoaded by homeViewModel.isDataLoaded.collectAsStateWithLifecycle()

            // Automatically navigate forward once data validation passes
            LaunchedEffect(isDataLoaded) {
                if (isDataLoaded) {
                    navController.navigate(HomeScreenRouter(userId = currentUserId)) {
                        popUpTo<LoadingScreenRouter> { inclusive = true }
                    }
                }
            }

            LoadingScreen(
                user = user != null,
                navController = navController,
                currentUserId = currentUserId,
                isSplashScreen = true,
                isDataLoaded = isDataLoaded,
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
            // 1. Extract the type-safe object
            val arguments = backStackEntry.toRoute<HomeScreenRouter>()

            HomeScreen(
                // 2. Access the property directly from the object
                userId = arguments.userId,
                onNavigate = navController
            )
        }
        composable<SettingsScreenRouter> {
            SettingsScreen(
                onBackClick = { navController.safePopBackStack() },
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
        composable<TransactionDetailScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<TransactionDetailScreenRouter>()
            val transactionId = arguments.transactionId
            TransactionDetailScreen(
                transactionId = transactionId,
                navController = navController,
            )
        }
        composable<LiabilityDetailScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<LiabilityDetailScreenRouter>()
            val liabilityId = arguments.liabilityId
            SettlementDetailScreen(
                liabilityId = liabilityId,
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