// Bless be the name of LORD of hosts, For his mercy endures forever,
// And his faithfulness to all generations.
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.homeScreen.allScreen.AllScreen
import com.example.moneytracker.ui.homeScreen.overviewScreen.OverviewScreen
import com.example.moneytracker.ui.homeScreen.todayScreen.TodayScreen
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.YesterdayScreen

@Composable
fun TodayScreenRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val donutChartData by viewModel
        .donutChartData
        .collectAsStateWithLifecycle()

    val datasetWithAdjust by viewModel
        .sortedToday
        .collectAsStateWithLifecycle()

    val fulfillmentFinanceEntity by viewModel
        .fulfillmentFinanceEntity
        .collectAsStateWithLifecycle()

    val currentAmountBalance by viewModel
        .currentAccountBalance
        .collectAsStateWithLifecycle()

    val liabilityBalance by viewModel
        .liabilityBalance
        .collectAsStateWithLifecycle()

    TodayScreen(
        paddingValues = paddingValues,

        donutChartDataCollection = donutChartData,

        uiState = uiState,

        fulfillmentFinanceEntityList = fulfillmentFinanceEntity,
        currentAmountBalance = currentAmountBalance,
        liabilityBalance = liabilityBalance,

        homeViewModel = viewModel,
        datasetWithAdjust = datasetWithAdjust,
        userViewModel = userViewModel
    )
}

@Composable
fun FulfillmentScreenRoute(
    paddingValues: PaddingValues,
    onNavigate: NavController?,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val allDataset by viewModel
        .allDataset
        .collectAsStateWithLifecycle()

    OverviewScreen(
        onNavigate = onNavigate,

        paddingValues = paddingValues,

        allDataset = allDataset,

        uiState = uiState,
    )
}

@Composable
fun YesterdayScreenRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val yesterdayChartDataState by viewModel
        .yesterdayChartData
        .collectAsStateWithLifecycle()

    val yesterdayStatsDataState by viewModel
        .yesterdayStats
        .collectAsStateWithLifecycle()

    val sortedYesterday by viewModel
        .sortedYesterday
        .collectAsStateWithLifecycle()

    YesterdayScreen(
        paddingValues = paddingValues,

        sortAbleDataSettlementDataState = sortedYesterday,

        yesterdayChartDataState = yesterdayChartDataState,

        yesterdayStatsDataState = yesterdayStatsDataState,
        viewModel = viewModel,
        userViewModel = userViewModel
    )
}

@Composable
fun AllScreenRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val dataState by viewModel
        .weeklyData
        .collectAsStateWithLifecycle()

    AllScreen(
        paddingValues = paddingValues,

        viewModel = viewModel,

        dataState = dataState,
        userViewModel = userViewModel
    )
}