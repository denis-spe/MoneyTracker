// Bless be the name of LORD of hosts, For his mercy endures forever,
// And his faithfulness to all generations.
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.ui.homeScreen.allScreen.AllScreen
import com.example.moneytracker.ui.homeScreen.fulfillmentScreen.FulfillmentScreen
import com.example.moneytracker.ui.homeScreen.todayScreen.TodayScreen
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.YesterdayScreen

@Composable
fun TodayScreenRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val todayFinance by viewModel
        .todayFinance
        .collectAsStateWithLifecycle()

    val donutChartData by viewModel
        .donutChartData
        .collectAsStateWithLifecycle()

    val datasetWithAdjust by viewModel
        .sortedToday
        .collectAsStateWithLifecycle()

    val fulfillmentFinanceEntity by viewModel
        .fulfillmentFinanceEntity
        .collectAsStateWithLifecycle()

    TodayScreen(
        paddingValues = paddingValues,

        donutChartDataCollection = donutChartData,

        uiState = uiState,

        todayFinanceEntityList = todayFinance,

        fulfillmentFinanceEntityList = fulfillmentFinanceEntity,

        homeViewModel = viewModel,

        isTodayDataLoading = uiState.isTodayDataLoading,

        isTodayChartDataLoading = uiState.isTodayChartDataLoading,

        isSortedTodayLoading = uiState.isSortedTodayLoading,

        datasetWithAdjust = datasetWithAdjust
    )
}

@Composable
fun FulfillmentScreenRoute(
    paddingValues: PaddingValues,
    onNavigate: NavController?,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val fulfillmentFinanceEntity by viewModel
        .fulfillmentFinanceEntity
        .collectAsStateWithLifecycle()

    FulfillmentScreen(
        onNavigate = onNavigate,

        paddingValues = paddingValues,

        fulfillmentFinanceEntityList = fulfillmentFinanceEntity,

        uiState = uiState,

        isGoalDataLoading = uiState.isGoalDataLoading
    )
}

@Composable
fun YesterdayScreenRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val yesterdayFinance by viewModel
        .yesterdayFinance
        .collectAsStateWithLifecycle()

    val yesterdayChartData by viewModel
        .yesterdayChartData
        .collectAsStateWithLifecycle()

    val yesterdayStats by viewModel
        .yesterdayStats
        .collectAsStateWithLifecycle()

    val sortedYesterday by viewModel
        .sortedYesterday
        .collectAsStateWithLifecycle()

    YesterdayScreen(
        paddingValues = paddingValues,

        uiState = uiState,

        sortAbleDataSettlement = sortedYesterday,

        yesterdayFinanceEntityList = yesterdayFinance,

        yesterdayChartData = yesterdayChartData,

        yesterdayStats = yesterdayStats,

        isYesterdayDataLoading = uiState.isYesterdayDataLoading,

        isYesterdayChartDataLoading = uiState.isYesterdayChartDataLoading,

        isYesterdayStatsLoading = uiState.isYesterdayStatsLoading,

        isSortedYesterdayLoading = uiState.isSortedYesterdayLoading
    )
}

@Composable
fun AllScreenRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val weeklyData by viewModel
        .weeklyData
        .collectAsStateWithLifecycle()

    AllScreen(
        paddingValues = paddingValues,

        viewModel = viewModel,

        weeklyData = weeklyData,

        uiState = uiState,

        isWeeklyDataLoading = uiState.isWeeklyDataLoading
    )
}