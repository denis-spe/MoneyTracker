// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.LoadingScreen
import com.example.moneytracker.ui.homeScreen.allScreen.AllScreen
import com.example.moneytracker.ui.homeScreen.dataAddition.DataAdditionFloatingButton
import com.example.moneytracker.ui.homeScreen.dataAddition.DataAdditionModelDrawer
import com.example.moneytracker.ui.homeScreen.goalScreen.GoalScreen
import com.example.moneytracker.ui.homeScreen.todayScreen.TodayScreen
import com.example.moneytracker.ui.homeScreen.topAppAction.TopAppAction
import com.example.moneytracker.ui.homeScreen.topAppNavigation.DropDownUserProfile
import com.example.moneytracker.ui.homeScreen.topAppNavigation.TopAppNav
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopAppTitle
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopBarNav
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.YesterdayScreen
import com.example.moneytracker.ui.screenManager.SettingsScreenRouter
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.utils.now


@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("G")
        Row {
            Text("Goal")
            Text("Today")
            Text("Yesterday")
            Text("All")
        }
        IconButton(
            onClick = { /*TODO*/ }
        ) {
            Icons.Default.Search
        }
    }
}


@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModels
    val homeViewModel: HomeViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    val todayFinance by homeViewModel.todayFinance.collectAsStateWithLifecycle()
    val datasetWithAdjust by homeViewModel.sortedToday.collectAsStateWithLifecycle()
    val donutChartData by homeViewModel.donutChartData.collectAsStateWithLifecycle()
    val weeklyData by homeViewModel.weeklyData.collectAsStateWithLifecycle()

    val yesterdayFinance by homeViewModel.yesterdayFinance.collectAsStateWithLifecycle()
    val yesterdayChartData by homeViewModel.yesterdayChartData.collectAsStateWithLifecycle()
    val sortAbleDataSettlement by homeViewModel.sortedYesterday.collectAsStateWithLifecycle()
    val goalFinance by homeViewModel.goalFinanceEntity.collectAsStateWithLifecycle()
    val settlementFinance by homeViewModel.adjustFinance.collectAsStateWithLifecycle()
    val yesterdayStats by homeViewModel.yesterdayStats.collectAsStateWithLifecycle()

    // Collect user information from ViewModels
    val uiStates = homeViewModel.uiState.collectAsStateWithLifecycle()
    val userUiStates = userViewModel.uiState.collectAsStateWithLifecycle()
    val userState = userViewModel.userState.collectAsStateWithLifecycle()
    val snackBarHostState = userViewModel.snackBarHostState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val topBarNavEntries = TopBarNav.entries
    val pagerState = rememberPagerState(initialPage = 1) {
        topBarNavEntries.size
    }

    val customColors = MoneyTrackerTheme.colors
    val isLoading = uiStates.value.datasetState is DatasetState.Loading
    LocalDateTime.now().year

    LaunchedEffect(Unit) {
        userViewModel.navigationEvents.collect {
            onNavigate?.navigate(StartUpScreenRouter) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (isLoading || userUiStates.value.isLoading) {
        LoadingScreen(
            user = userState.value != null,
            navController = onNavigate,
            currentUserId = userId
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(stringResource(R.string.homeScreenId)),
                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors().copy(
                            titleContentColor = Color.White,
                        ),
                        title = {
                            TopAppTitle(
                                state = pagerState,
                                contentColor = customColors.contentColor,
                                currentPageColor = customColors.currentPage,
                                backgroundColor = customColors.customBackground,
                                function = {

                                    scope.launch {
                                        pagerState.animateScrollToPage(topBarNavEntries.indexOf(it))
                                    }
                                }
                            )
                        },
                        navigationIcon = {
                            TopAppNav(
                                userState,
                                contentColor = customColors.contentColor,
                                userColor = uiStates.value.info.color,
                            ) {
                                userViewModel.updateIsUserDropdownVisible(
                                    !userUiStates.value.isUserDropdownVisible
                                )
                            }
                        },
                        actions = {
                            TopAppAction()
                        }
                    )
                },
                floatingActionButton = {
                    DataAdditionFloatingButton(
                        uiState = uiStates.value,
                        isLoading = isLoading
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState.value)
                }
            ) { paddingValues ->
                val uiState = uiStates.value


                HorizontalPager(
                    state = pagerState,
                    beyondViewportPageCount = 1,
                    key = { it }
                ) { page ->
                    when (page) {
                        0 -> GoalScreen(
                            onNavigate = onNavigate,
                            paddingValues = paddingValues,
                            goalFinanceEntityList = goalFinance,
                            uiState = uiState,
                            isGoalDataLoading = uiState.isGoalDataLoading
                        )

                        1 -> TodayScreen(
                            paddingValues,
                            donutChartDataCollection = donutChartData,
                            uiState = uiState,
                            todayFinanceEntityList = todayFinance,
                            homeViewModel = homeViewModel,
                            isTodayDataLoading = uiState.isTodayDataLoading,
                            isTodayChartDataLoading = uiState.isTodayChartDataLoading,
                            isSortedTodayLoading = uiState.isSortedTodayLoading,
                            datasetWithAdjust = datasetWithAdjust
                        )

                        2 -> YesterdayScreen(
                            paddingValues,
                            uiState = uiState,
                            sortAbleDataSettlement = sortAbleDataSettlement,
                            yesterdayFinanceEntityList = yesterdayFinance,
                            yesterdayChartData = yesterdayChartData,
                            yesterdayStats = yesterdayStats,
                            isYesterdayDataLoading = uiState.isYesterdayDataLoading,
                            isYesterdayChartDataLoading = uiState.isYesterdayChartDataLoading,
                            isYesterdayStatsLoading = uiState.isYesterdayStatsLoading,
                            isSortedYesterdayLoading = uiState.isSortedYesterdayLoading
                        )

                        3 -> AllScreen(
                            paddingValues,
                            viewModel = homeViewModel,
                            weeklyData = weeklyData,
                            uiState = uiState,
                            isWeeklyDataLoading = uiState.isWeeklyDataLoading
                        )
                    }
                }
                // Modal bottom sheet
                DataAdditionModelDrawer(
                    viewModel = homeViewModel,
                    userViewModel = userViewModel,
                    financeEntityList = settlementFinance,
                    uiState = uiStates.value
                )
            }

            // Drop down user profile
            DropDownUserProfile(
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = customColors.contentColor,
                backgroundColor = customColors.customBackground.copy(alpha = 0.9f),
                visible = userUiStates.value.isUserDropdownVisible,
                userState = userState,
                isLoading = userUiStates.value.isLoading,
                settingsClick = {
                    onNavigate?.navigate(SettingsScreenRouter)
                    userViewModel.updateIsUserDropdownVisible(false)
                },
                onDismiss = {
                    userViewModel.updateIsUserDropdownVisible(false)
                }
            ) {
                userViewModel.handleLogout()
            }

        }
    }
}
