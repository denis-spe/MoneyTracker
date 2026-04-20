// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.ui.UserViewModel
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

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModels
    val homeViewModel: HomeViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val todayDatasets by homeViewModel.todayDatasetsState.collectAsStateWithLifecycle()

    val donutChartData by homeViewModel.donutChartDataState.collectAsStateWithLifecycle()
    val weeklyData by homeViewModel.weeklyDataState.collectAsStateWithLifecycle()

    // Collect user information from ViewModels
    val uiStates = homeViewModel.uiState.collectAsStateWithLifecycle()
    val userUiStates = userViewModel.uiState.collectAsStateWithLifecycle()
    val userState = userViewModel.userState.collectAsStateWithLifecycle()
    val snackBarHostState = userViewModel.snackBarHostState.collectAsStateWithLifecycle()

    val yesterdayDatasets by homeViewModel.yesterdayDatasetsState.collectAsStateWithLifecycle()
    val yesterdayChartData by homeViewModel.yesterdayChartDataState.collectAsStateWithLifecycle()
    val sortAbleDataAdjust by homeViewModel.sortedYesterdayState.collectAsStateWithLifecycle()
    val goalDatasets by homeViewModel.goalDatasetsState.collectAsStateWithLifecycle()
    val adjustmentDatasets by homeViewModel.adjustDatasetsState.collectAsStateWithLifecycle()
    val yesterdayStats by homeViewModel.yesterdayStatsState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val topBarNavEntries = TopBarNav.entries
    val pagerState = rememberPagerState(initialPage = 1) {
        topBarNavEntries.size
    }



    val customColors = MoneyTrackerTheme.colors
    val isLoading = uiStates.value.datasetState is DatasetState.Loading

    LaunchedEffect(Unit) {
        userViewModel.navigationEvents.collect {
            onNavigate?.navigate(StartUpScreenRouter)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag(stringResource(R.string.homeScreenId))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        userViewModel.updateIsUserDropdownVisible(false)
                    }
                )
            }
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
                        isLoading = isLoading,
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
                        isLoading = isLoading
                    ) {
                        userViewModel.updateIsUserDropdownVisible(
                            !userUiStates.value.isUserDropdownVisible
                        )
                    }
                },
                actions = {
                    TopAppAction(isLoading = isLoading)
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
        val hasLoadedData = uiStates.value.datasetState is DatasetState.Success

        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            key = { it }
        ) { page ->
            when (page) {
                0 -> GoalScreen(
                    paddingValues,
                    goalDatasets = goalDatasets,
                    uiState = uiStates.value,
                    hasLoadedData = hasLoadedData
                )

                1 -> TodayScreen(
                    paddingValues,
                    todayDatasets = todayDatasets,
                    donutChartDataCollection = donutChartData,
                    uiState = uiStates.value,
                    homeViewModel = homeViewModel,
                    hasLoadedData = hasLoadedData
                )

                2 -> YesterdayScreen(
                    paddingValues,
                    uiState = uiStates.value,
                    sortAbleDataAdjust = sortAbleDataAdjust,
                    yesterdayDatasets = yesterdayDatasets,
                    yesterdayChartData = yesterdayChartData,
                    hasLoadedData = hasLoadedData,
                    yesterdayStats = yesterdayStats
                )

                3 -> AllScreen(
                    paddingValues,
                    viewModel = homeViewModel,
                    weeklyData = weeklyData,
                    uiState = uiStates.value,
                    hasLoadedData = hasLoadedData
                )
            }
        }

        // Drop down user profile
        DropDownUserProfile(
            paddingValues,
            contentColor = customColors.contentColor,
            backgroundColor = customColors.customBackground.copy(alpha = 0.9f),
            visible = userUiStates.value.isUserDropdownVisible,
            userState = userState,
            isLoading = userUiStates.value.isLoading,
            settingsClick = {
                onNavigate?.navigate(SettingsScreenRouter)
                userViewModel.updateIsUserDropdownVisible(false)
            },
        ) {
            userViewModel.handleLogout()
        }

        // Modal bottom sheet
        DataAdditionModelDrawer(
            viewModel = homeViewModel,
            userViewModel = userViewModel,
            uiState = uiStates.value,
            datasets = adjustmentDatasets
        )
    }
}
