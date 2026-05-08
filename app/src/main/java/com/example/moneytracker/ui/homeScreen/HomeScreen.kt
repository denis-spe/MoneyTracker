// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.LoadingScreen
import com.example.moneytracker.ui.homeScreen.dataAddition.DataAdditionFloatingButton
import com.example.moneytracker.ui.homeScreen.dataAddition.DataAdditionModelDrawer
import com.example.moneytracker.ui.homeScreen.topAppAction.TopAppAction
import com.example.moneytracker.ui.homeScreen.topAppNavigation.DropDownUserProfile
import com.example.moneytracker.ui.homeScreen.topAppNavigation.TopAppNav
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopAppTitle
import com.example.moneytracker.ui.homeScreen.topAppTitle.TopBarNav
import com.example.moneytracker.ui.screenManager.SettingsScreenRouter
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter
import com.example.moneytracker.ui.theme.MoneyTrackerTheme
import kotlinx.coroutines.launch

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: NavController? = null,
    userId: String
) {

    val homeViewModel: HomeViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    // ONLY collect lightweight global states here
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    val userUiState by userViewModel.uiState.collectAsStateWithLifecycle()
    val userState by userViewModel.userState.collectAsStateWithLifecycle()

    val snackBarHostState by userViewModel
        .snackBarHostState
        .collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()

    val topBarEntries = remember {
        TopBarNav.entries
    }

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { topBarEntries.size }
    )

    // IMPORTANT:
    // Use settledPage instead of currentPage for smoother movement
    val selectedPage by remember {
        derivedStateOf {
            pagerState.settledPage
        }
    }

    val customColors = MoneyTrackerTheme.colors

    val isLoading = remember(uiState.datasetState, userUiState.isLoading) {
        uiState.datasetState is DatasetState.Loading ||
                userUiState.isLoading
    }

    LaunchedEffect(Unit) {
        userViewModel.navigationEvents.collect {
            onNavigate?.navigate(StartUpScreenRouter) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    if (isLoading) {

        LoadingScreen(
            user = userState != null,
            navController = onNavigate,
            currentUserId = userId
        )

        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

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
                            contentColor = customColors.contentColor,
                            currentPageColor = customColors.currentPage,
                            backgroundColor = customColors.customBackground,

                            ) { tab ->

                            scope.launch {

                                val index = topBarEntries.indexOf(tab)

                                pagerState.animateScrollToPage(
                                    index,
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        easing = androidx.compose.animation.core.FastOutSlowInEasing
                                    )
                                )
                            }
                        }

                    },

                    navigationIcon = {

                        TopAppNav(
                            userState = userState,
                            contentColor = customColors.contentColor,
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
                    isLoading = false
                )
            },

            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            }

        ) { paddingValues ->

            HorizontalPager(
                state = pagerState,

                // VERY IMPORTANT FOR PERFORMANCE
                beyondViewportPageCount = 0,

                pageSpacing = 0.dp,

                snapPosition = SnapPosition.Start,

                // Prevent unnecessary page recreation
                key = { topBarEntries[it] }

            ) { page ->

                when (page) {

                    0 -> {

                        FulfillmentScreenRoute(
                            paddingValues = paddingValues,
                            onNavigate = onNavigate
                        )
                    }

                    1 -> {

                        TodayScreenRoute(
                            paddingValues = paddingValues
                        )
                    }

                    2 -> {

                        YesterdayScreenRoute(
                            paddingValues = paddingValues
                        )
                    }

                    3 -> {

                        AllScreenRoute(
                            paddingValues = paddingValues
                        )
                    }
                }
            }

            DataAdditionModelDrawer(
                viewModel = homeViewModel,
                userViewModel = userViewModel,
                uiState = uiState
            )
        }

        DropDownUserProfile(
            modifier = Modifier.align(Alignment.TopCenter),

            contentColor = customColors.contentColor,

            backgroundColor = customColors.customBackground.copy(
                alpha = 0.9f
            ),

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