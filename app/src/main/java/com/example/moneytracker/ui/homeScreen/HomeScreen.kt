// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModels
    val homeViewModel: HomeViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val chartViewModel: ChartViewModel = hiltViewModel()
    val todayDatasets by homeViewModel.todayDatasets.collectAsStateWithLifecycle()

    val donutChartDataCollection =
        chartViewModel.todayChartData(todayDatasets).collectAsStateWithLifecycle(emptyList())
    val weeklyData = homeViewModel.weeklyData.collectAsStateWithLifecycle(emptyList())

    // Collect user information from ViewModels
    val uiStates = homeViewModel.uiState.collectAsStateWithLifecycle()
    val userUiStates = userViewModel.uiState.collectAsStateWithLifecycle()
    val userState = userViewModel.userState.collectAsStateWithLifecycle()
    val snackBarHostState = userViewModel.snackBarHostState.collectAsStateWithLifecycle()

    val yesterdayDatasets by homeViewModel.yesterdayDatasets.collectAsStateWithLifecycle()
    val sortAbleDataAdjust by homeViewModel.sortYesterdayDataAdjust().collectAsStateWithLifecycle()
    val goalDatasets by homeViewModel.goalDatasets.collectAsStateWithLifecycle()
    val adjustmentDatasets by homeViewModel.adjustDatasets.collectAsStateWithLifecycle()


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
                        uiStates,
                        contentColor = customColors.contentColor,
                        currentPageColor = customColors.currentPage,
                        backgroundColor = customColors.customBackground,
                        isLoading = isLoading,
                        function = homeViewModel::updateTopTitle
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
        AnimatedContent(
            uiStates.value.datasetState,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(1000)
                ) togetherWith fadeOut(animationSpec = tween(1000))
            },
            label = "Animated Content"
        ) { targetState ->
            when (targetState) {
                is DatasetState.Success -> {
                    when (uiStates.value.topTitle) {
                        TopBarNav.TODAY -> TodayScreen(
                            paddingValues,
                            todayDatasets = todayDatasets,
                            donutChartDataCollection = donutChartDataCollection.value,
                            uiState = uiStates.value,
                            homeViewModel = homeViewModel
                        )

                        TopBarNav.YESTERDAY -> YesterdayScreen(
                            paddingValues,
                            uiState = uiStates.value,
                            sortAbleDataAdjust = sortAbleDataAdjust,
                            yesterdayDatasets = yesterdayDatasets,
                        )

                        TopBarNav.ALL -> AllScreen(
                            paddingValues,
                            viewModel = homeViewModel,
                            weeklyData = weeklyData,
                            uiState = uiStates.value
                        )

                        TopBarNav.GOAL -> GoalScreen(
                            paddingValues,
                            goalDatasets = goalDatasets
                        )
                    }
                }

                is DatasetState.Loading -> {
                    HomeShimmer(paddingValues)
                }

                is DatasetState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = targetState.message ?: "Unknown error")
                    }
                }
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
