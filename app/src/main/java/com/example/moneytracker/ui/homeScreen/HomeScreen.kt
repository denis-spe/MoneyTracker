// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.DatasetUiState
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
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter

@RequiresApi(Build.VERSION_CODES.O)
@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModel
    val viewModel: HomeScreenViewModel = hiltViewModel()
    // Collect user information from ViewModel
    val uiStates = viewModel.uiState.collectAsState()
    val userState = viewModel.userState.collectAsState()
    val datasets = viewModel.fetchLiveChangeDataset.collectAsState(emptyList())

    val colors = Colors()

    // Define the colors for the button
    val contentColor = (if (isSystemInDarkTheme()) colors.darkModeColor else colors.lightModeColor)

    // Panel and button color
    val backgroundColor = if (isSystemInDarkTheme()) colors.darkModeBackgroundColor else
        colors.lightModeBackgroundColor

    LaunchedEffect(datasets) {

    }


    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect {
            onNavigate?.navigate(StartUpScreenRouter)
        }
    }

    AnimatedContent(
        viewModel.datasetUiState,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(1000)
            ) togetherWith fadeOut(animationSpec = tween(1000))
        },
        label = "Animated Content"
    ) { targetState ->
        when (targetState) {
            is DatasetUiState.Success -> {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(stringResource(R.string.homeScreenId))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    viewModel.updateIsUserDropdownVisible(false)
                                }
                            )
                        },
                    topBar = {
                        CenterAlignedTopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors().copy(
                                titleContentColor = Color.White,
                            ),
                            title = {
                                TopAppTitle(
                                    uiStates,
                                    contentColor = contentColor,
                                    currentPageColor = colors.currentPageColor,
                                    backgroundColor = backgroundColor,
                                    viewModel::updateTopTitle
                                )
                            },
                            navigationIcon = {
                                TopAppNav(
                                    userState,
                                    contentColor = contentColor,
                                    userColor = uiStates.value.info.color
                                ) {
                                    viewModel.updateIsUserDropdownVisible(
                                        !uiStates.value.isUserDropdownVisible
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
                        )
                    },
                ) { paddingValues ->

                    when (uiStates.value.topTitle) {
                        TopBarNav.TODAY -> TodayScreen(paddingValues)
                        TopBarNav.YESTERDAY -> YesterdayScreen(paddingValues)
                        TopBarNav.ALL -> AllScreen(paddingValues)
                        TopBarNav.GOAL -> GoalScreen(paddingValues)
                    }


                    // Drop down user profile
                    DropDownUserProfile(
                        paddingValues,
                        contentColor = contentColor,
                        backgroundColor = backgroundColor.copy(alpha = 0.9f),
                        visible = uiStates.value.isUserDropdownVisible,
                        userState = userState,
                        isLoading = uiStates.value.isLogOutLoading,
                    ) {
                        viewModel.handleLogout()
                    }

                    // Modal bottom sheet
                    DataAdditionModelDrawer(
                        viewModel = viewModel,
                        uiState = uiStates.value
                    )
                }
            }

            is DatasetUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.background
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DatasetUiState.Error -> {
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

}