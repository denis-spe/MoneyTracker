// Bless be the Name of the Lord
package com.example.moneytracker.ui.screenManager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.moneytracker.ui.homeScreen.HomeMainViewModel
import com.example.moneytracker.ui.homeScreen.HomeScreen
import com.example.moneytracker.ui.homeScreen.allScreen.AllViewModel
import com.example.moneytracker.ui.homeScreen.overviewScreen.OverviewViewModel
import com.example.moneytracker.ui.homeScreen.todayScreen.TodayViewModel
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.YesterdayViewModel
import com.example.moneytracker.ui.loading.LoadingViewModel
import com.example.moneytracker.ui.settings.SettingsScreen
import com.example.moneytracker.ui.showAll.ShowAllGoalScreen
import com.example.moneytracker.ui.showAll.ShowAllLiabilityScreen
import com.example.moneytracker.ui.showAll.ShowAllTransactionScreen
import com.example.moneytracker.ui.showAll.ShowAllViewModel
import com.example.moneytracker.ui.startUpScreen.StartUpScreen

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun ScreenManager(
    homeMainViewModel: HomeMainViewModel,
    overviewViewModel: OverviewViewModel,
    todayViewModel: TodayViewModel,
    yesterdayViewModel: YesterdayViewModel,
    allViewModel: AllViewModel,
    showAllViewModel: ShowAllViewModel,
    navController: NavHostController = rememberNavController(),
    account: AccountServices = hiltViewModel<ScreenManagerViewModel>().account,
    onFullyDrawn: () -> Unit
) {
    val userViewModel: UserViewModel = hiltViewModel()

    // ─── Single source of truth ───────────────────────────────────────────────
    // HomeViewModel is created ONCE here, scoped to ScreenManager's back-stack
    // entry. It is passed down to every composable that needs it — never via
    // hiltViewModel() again inside child composables.
    val isDataLoaded by homeMainViewModel.isDataLoaded.collectAsStateWithLifecycle()
    // ─────────────────────────────────────────────────────────────────────────

    val router = if (account.hasUser) {
        LoadingScreenRouter(userId = account.currentUserId)
    } else {
        StartUpScreenRouter
    }

    NavHost(navController = navController, startDestination = router) {

        // ── Startup / Auth ────────────────────────────────────────────────────

        composable<StartUpScreenRouter> {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            val loadingViewModel: LoadingViewModel = hiltViewModel()

            StartUpScreen(
                navController,
                loadingViewModel = loadingViewModel
            )
        }

        // ── Loading splash ────────────────────────────────────────────────────
        // Reads isDataLoaded from the already-created homeViewModel above.
        // No new HomeViewModel instance is created here.

        composable<LoadingScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<LoadingScreenRouter>()
            val user by userViewModel.userState.collectAsStateWithLifecycle()

            LaunchedEffect(isDataLoaded) {
                if (isDataLoaded) {
                    navController.navigate(HomeScreenRouter(userId = arguments.userId)) {
                        popUpTo<LoadingScreenRouter> { inclusive = true }
                    }
                }
            }

            LoadingScreen(
                user = user != null,
                navController = navController,
                currentUserId = arguments.userId,
                isSplashScreen = true,
                isDataLoaded = isDataLoaded,
                content = null
            )
        }

        // ── Home ──────────────────────────────────────────────────────────────
        // Passes the already-created homeViewModel and userViewModel.
        // HomeScreen no longer calls hiltViewModel() internally.

        composable<HomeScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<HomeScreenRouter>()

            HomeScreen(
                homeMainViewModel = homeMainViewModel,
                userViewModel = userViewModel,
                userId = arguments.userId,
                onNavigate = navController,
                onFullyDrawn = onFullyDrawn,
                overviewViewModel = overviewViewModel,
                todayViewModel = todayViewModel,
                yesterdayViewModel = yesterdayViewModel,
                allViewModel = allViewModel
            )
        }

        // ── Settings ──────────────────────────────────────────────────────────

        composable<SettingsScreenRouter> {
            SettingsScreen(
                onBackClick = { navController.safePopBackStack() },
                onLoginClick = { navController.navigate(MailScreenRouter) },
                navController = navController,
                userId = account.currentUserId
            )
        }

        // ── ShowAll ──────────────────────────────────────────────────────────
        composable<ShowAllTransactionsScreenRouter> {
            ShowAllTransactionScreen(
                viewModel = showAllViewModel,
                navController = navController
            )
        }

        composable<ShowAllLiabilitiesScreenRouter> {
            ShowAllLiabilityScreen(
                viewModel = showAllViewModel,
                navController = navController
            )
        }

        composable<ShowAllGoalsScreenRouter> {
            ShowAllGoalScreen(
                viewModel = showAllViewModel,
                navController = navController
            )
        }

        // ── Auth screens ──────────────────────────────────────────────────────

        composable<MailScreenRouter> {
            MailScreen(navController)
        }

        composable<NamesRegistrationScreenRouter> {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            NamesRegistrationScreen(
                viewModel = registerViewModel,
                onNavigate = navController
            )
        }

        composable<EmailRegistrationScreenRouter> {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            EmailRegistrationScreen(
                viewModel = registerViewModel,
                onNavigate = navController
            )
        }

        composable<PasswordRegistrationScreenRouter> {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            PasswordRegistrationScreen(
                viewModel = registerViewModel,
                onNavigate = navController
            )
        }

        composable<LoginScreenRouter> {
            LoginScreen(onNavigate = navController)
        }

        // ── Detail screens ────────────────────────────────────────────────────

        composable<FulfillmentDetailScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<FulfillmentDetailScreenRouter>()
            GoalDetailScreen(
                goalId = arguments.goalId,
                navController = navController,
            )
        }

        composable<TransactionDetailScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<TransactionDetailScreenRouter>()
            TransactionDetailScreen(
                transactionId = arguments.transactionId,
                navController = navController,
            )
        }

        composable<LiabilityDetailScreenRouter> { backStackEntry ->
            val arguments = backStackEntry.toRoute<LiabilityDetailScreenRouter>()
            SettlementDetailScreen(
                liabilityId = arguments.liabilityId,
                navController = navController,
            )
        }
    }
}