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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val donutChartData by viewModel.donutChartData.collectAsStateWithLifecycle()
    val datasetWithAdjust by viewModel.sortedToday.collectAsStateWithLifecycle()
    val fulfillmentFinanceEntity by viewModel.fulfillmentFinanceEntity.collectAsStateWithLifecycle()
    val currentAmountBalance by viewModel.currentAccountBalance.collectAsStateWithLifecycle()
    val liabilityBalance by viewModel.liabilityBalance.collectAsStateWithLifecycle()

    TodayScreen(
        paddingValues = paddingValues,
        donutChartDataCollection = donutChartData,
        uiState = uiState,
        fulfillmentFinanceEntityList = fulfillmentFinanceEntity,
        currentAmountBalance = currentAmountBalance,
        liabilityBalance = liabilityBalance,
        todayViewModel = viewModel,
        homeMainViewModel = homeMainViewModel,
        datasetWithAdjust = datasetWithAdjust,
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

    OverviewScreen(
        onNavigate = onNavigate,
        paddingValues = paddingValues,
        allDataset = allDataset,
    )
}

@Composable
fun YesterdayScreenRoute(
    paddingValues: PaddingValues,
    viewModel: YesterdayViewModel,
    homeMainViewModel: HomeMainViewModel,
    userViewModel: UserViewModel,
) {
    val yesterdayChartDataState by viewModel.yesterdayChartData.collectAsStateWithLifecycle()
    val yesterdayStatsDataState by viewModel.yesterdayStats.collectAsStateWithLifecycle()
    val sortedYesterday by viewModel.sortedYesterday.collectAsStateWithLifecycle()

    YesterdayScreen(
        paddingValues = paddingValues,
        sortAbleDataSettlementDataState = sortedYesterday,
        yesterdayChartDataState = yesterdayChartDataState,
        yesterdayStatsDataState = yesterdayStatsDataState,
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
    val dataState by viewModel.groupedWeeklyData.collectAsStateWithLifecycle()
    val summaryState by viewModel.professionalSummary.collectAsStateWithLifecycle()

    AllScreen(
        paddingValues = paddingValues,
        viewModel = viewModel,
        dataState = dataState,
        summaryState = summaryState,
        userViewModel = userViewModel,
        homeMainViewModel = homeMainViewModel
    )
}