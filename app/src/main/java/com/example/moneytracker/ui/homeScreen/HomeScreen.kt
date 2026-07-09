// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.dataAddition.DataAdditionFloatingButton
import com.example.moneytracker.ui.dataAddition.DataAdditionModelDrawer
import com.example.moneytracker.ui.homeScreen.allScreen.AllViewModel
import com.example.moneytracker.ui.homeScreen.overviewScreen.OverviewViewModel
import com.example.moneytracker.ui.homeScreen.todayScreen.TodayViewModel
import com.example.moneytracker.ui.homeScreen.topAppAction.TopAppAction
import com.example.moneytracker.ui.homeScreen.topAppNavigation.DropDownUserProfile
import com.example.moneytracker.ui.homeScreen.topAppNavigation.TopAppNav
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopAppTitle
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopBarNav
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.YesterdayViewModel
import com.example.moneytracker.ui.screenManager.SettingsScreenRouter
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter
import com.example.moneytracker.ui.theme.StewardTheme
import kotlinx.coroutines.launch

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // ViewModels received from ScreenManager — never created here
    homeMainViewModel: HomeMainViewModel,
    overviewViewModel: OverviewViewModel,
    todayViewModel: TodayViewModel,
    yesterdayViewModel: YesterdayViewModel,
    allViewModel: AllViewModel,
    userViewModel: UserViewModel,
    onNavigate: NavController? = null,
    userId: String,
    onFullyDrawn: () -> Unit
) {
    val uiState by homeMainViewModel.uiState.collectAsStateWithLifecycle()
    val userUiState by userViewModel.uiState.collectAsStateWithLifecycle()
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val isLoaded by homeMainViewModel.isDataLoaded.collectAsStateWithLifecycle()

    LaunchedEffect(isLoaded) {
        if (isLoaded) onFullyDrawn()
    }

    val snackBarHostState by userViewModel.snackBarHostState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val topBarEntries = remember { TopBarNav.entries }

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { topBarEntries.size }
    )

    val customColors = StewardTheme.colors

    val secondarySurface = customColors.secondarySurface

    val surfaceColor = MaterialTheme.colorScheme.surface

    val blendedColor = remember(secondarySurface, surfaceColor) {
        secondarySurface.copy(alpha = 0.4f).compositeOver(surfaceColor)
    }

    LaunchedEffect(Unit) {
        userViewModel.navigationEvents.collect {
            onNavigate?.navigate(StartUpScreenRouter) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag(stringResource(R.string.homeScreenId)),

            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        titleContentColor = Color.White
                    ),
                    title = {
                        TopAppTitle(
                            state = pagerState,
                            contentColor = customColors.accentContent,
                            currentPageColor = customColors.primary,
                            backgroundColor = blendedColor,
                        ) { tab ->
                            scope.launch {
                                pagerState.animateScrollToPage(topBarEntries.indexOf(tab))
                            }
                        }
                    },
                    navigationIcon = {
                        TopAppNav(
                            userState = userState,
                            contentColor = customColors.accentContent,
                            userColor = uiState.info.color
                        ) {
                            userViewModel.updateIsUserDropdownVisible(
                                !userUiState.isUserDropdownVisible
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
                    uiState = uiState,
                    isLoading = false,
                    viewModel = homeMainViewModel
                )
            },

            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            }

        ) { paddingValues ->

            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                pageSpacing = 0.dp,
                snapPosition = SnapPosition.Start,
                userScrollEnabled = true,
                key = { topBarEntries[it] },
            ) { page ->
                when (page) {
                    0 -> {
                        val pageScope = rememberCoroutineScope()
                        FulfillmentScreenRoute(
                            paddingValues = paddingValues,
                            onNavigate = onNavigate,
                            viewModel = overviewViewModel,
                            onTabClick = { index ->
                                pageScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        )
                    }
                    1 -> {
                        TodayScreenRoute(
                            paddingValues = paddingValues,
                            viewModel = todayViewModel,
                            homeMainViewModel = homeMainViewModel,
                            userViewModel = userViewModel,
                        )
                    }
                    2 -> {
                        YesterdayScreenRoute(
                            paddingValues = paddingValues,
                            viewModel = yesterdayViewModel,
                            homeMainViewModel = homeMainViewModel,
                            userViewModel = userViewModel,
                        )
                    }
                    3 -> {
                        AllScreenRoute(
                            paddingValues = paddingValues,
                            viewModel = allViewModel,
                            userViewModel = userViewModel,
                            homeMainViewModel = homeMainViewModel,
                        )
                    }
                }
            }

            DataAdditionModelDrawer(
                viewModel = homeMainViewModel,
                userViewModel = userViewModel,
                uiState = uiState
            )
        }

        DropDownUserProfile(
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = customColors.accentContent,
            backgroundColor = customColors.secondarySurface.copy(alpha = 0.9f),
            visible = userUiState.isUserDropdownVisible,
            userState = userState,
            isLoading = userUiState.isLoading,
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