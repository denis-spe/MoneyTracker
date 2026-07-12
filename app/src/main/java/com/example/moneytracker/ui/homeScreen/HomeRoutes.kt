// Bless be the name of LORD of hosts, For his mercy endures forever,
// And his faithfulness to all generations.
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.homeScreen.allScreen.AllScreen
import com.example.moneytracker.ui.homeScreen.allScreen.AllViewModel
import com.example.moneytracker.ui.homeScreen.overviewScreen.OverviewScreen
import com.example.moneytracker.ui.homeScreen.overviewScreen.OverviewViewModel
import com.example.moneytracker.ui.homeScreen.todayScreen.TodayScreen
import com.example.moneytracker.ui.homeScreen.todayScreen.TodayViewModel
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.YesterdayScreen
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.YesterdayViewModel

// ─────────────────────────────────────────────────────────────────────────────
// All route composables receive ViewModels as explicit parameters.
// hiltViewModel() is NEVER called here — that would create new instances
// scoped to each pager page, causing duplicate Firestore subscriptions.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TodayScreenRoute(
    paddingValues: PaddingValues,
    viewModel: TodayViewModel,
    homeMainViewModel: HomeMainViewModel,
    userViewModel: UserViewModel,
) {
    val screenData by viewModel.screenData.collectAsStateWithLifecycle()

    TodayScreen(
        paddingValues = paddingValues,
        donutChartDataCollection = screenData.donutChartData,
        uiState = screenData.uiState,
        fulfillmentFinanceEntityList = screenData.fulfillmentFinanceEntity,
        currentAmountBalance = screenData.currentAccountBalance,
        liabilityBalance = screenData.liabilityBalance,
        todayViewModel = viewModel,
        homeMainViewModel = homeMainViewModel,
        datasetWithAdjust = screenData.sortedToday,
        userViewModel = userViewModel
    )
}

@Composable
fun FulfillmentScreenRoute(
    paddingValues: PaddingValues,
    onNavigate: NavController?,
    viewModel: OverviewViewModel,
    onTabClick: (Int) -> Unit = {},
) {
    val allDataset by viewModel.allDataset.collectAsStateWithLifecycle()
    val recentActivity by viewModel.recentActivity.collectAsStateWithLifecycle()
    val sortedGoals by viewModel.sortedGoals.collectAsStateWithLifecycle()
    val sortedLiabilities by viewModel.sortedLiabilities.collectAsStateWithLifecycle()
    val isAscending by viewModel.isAscending.collectAsStateWithLifecycle()

    val transactionSort by viewModel.transactionSort.collectAsStateWithLifecycle()
    val goalSort by viewModel.goalSort.collectAsStateWithLifecycle()
    val liabilitySort by viewModel.liabilitySort.collectAsStateWithLifecycle()

    OverviewScreen(
        onNavigate = onNavigate,
        paddingValues = paddingValues,
        allDataset = allDataset,
        recentActivity = recentActivity,
        sortedGoals = sortedGoals,
        sortedLiabilities = sortedLiabilities,
        isAscending = isAscending,
        transactionSort = transactionSort,
        goalSort = goalSort,
        liabilitySort = liabilitySort,
        onToggleSort = { viewModel.toggleSortOrder() },
        onTransactionSortChange = { viewModel.updateTransactionSort(it) },
        onGoalSortChange = { viewModel.updateGoalSort(it) },
        onLiabilitySortChange = { viewModel.updateLiabilitySort(it) }
    )
}

@Composable
fun YesterdayScreenRoute(
    paddingValues: PaddingValues,
    viewModel: YesterdayViewModel,
    homeMainViewModel: HomeMainViewModel,
    userViewModel: UserViewModel,
) {
    val screenData by viewModel.screenData.collectAsStateWithLifecycle()

    YesterdayScreen(
        paddingValues = paddingValues,
        sortAbleDataSettlementDataState = screenData.sortedYesterday,
        yesterdayChartDataState = screenData.yesterdayChartData,
        yesterdayStatsDataState = screenData.yesterdayStats,
        viewModel = viewModel,
        homeMainViewModel = homeMainViewModel,
        userViewModel = userViewModel
    )
}

@Composable
fun AllScreenRoute(
    paddingValues: PaddingValues,
    viewModel: AllViewModel,
    homeMainViewModel: HomeMainViewModel,
    userViewModel: UserViewModel,
) {
    val screenData by viewModel.screenData.collectAsStateWithLifecycle()

    AllScreen(
        paddingValues = paddingValues,
        viewModel = viewModel,
        dataState = screenData.groupedWeeklyData,
        summaryState = screenData.professionalSummary,
        userViewModel = userViewModel,
        homeMainViewModel = homeMainViewModel
    )
}
