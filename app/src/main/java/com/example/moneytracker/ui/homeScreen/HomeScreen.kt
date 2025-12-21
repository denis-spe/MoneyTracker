// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.ui.components.charts.ChartData
import com.example.moneytracker.ui.components.charts.VicoBarChart
import com.example.moneytracker.ui.homeScreen.topNavigation.DropDownUserProfile
import com.example.moneytracker.ui.homeScreen.topNavigation.TopNavPanel
import com.example.moneytracker.ui.homeScreen.topTitle.TopTitlePanel
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModel
    val viewModel: HomeScreenViewModel = hiltViewModel()
    // Collect user information from ViewModel
    val uiStates = viewModel.uiState.collectAsState()
    val userState = viewModel.userState.collectAsState()

    val colors = Colors()

    // Define the colors for the button
    val contentColor = (if (isSystemInDarkTheme()) colors.darkModeColor else colors.lightModeColor)

    // Panel and button color
    val backgroundColor = if (isSystemInDarkTheme()) colors.darkModeBackgroundColor else
        colors.lightModeBackgroundColor


    LaunchedEffect(key1 = uiStates.value.isLogOutLoading) {
        delay(1000)
        if (uiStates.value.isLogOutLoading) {
            onNavigate?.navigate(StartUpScreenRouter)
            viewModel.signOut()
        }
    }

    LaunchedEffect(key1 = uiStates.value.info.color) {
        Log.d("HomeScreenColor", uiStates.value.info.color.toString())
    }

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
                    TopTitlePanel(
                        uiStates,
                        contentColor = contentColor,
                        currentPageColor = colors.currentPageColor,
                        backgroundColor = backgroundColor,
                        viewModel::updateTopTitle
                    )
                },
                navigationIcon = {
                    TopNavPanel(
                        userState,
                        contentColor = contentColor,
                        userColor = uiStates.value.info.color
                    ) {
                        viewModel.addData(
                            Dataset(
                                dataType = com.example.moneytracker.backend.storage.DataType.EARNINGS,
                                amount = 40.0,
                                label = "Test",
                                category = "Test",
                                description = "Test"
                            )
                        )

                        Log.d("HomeScreenClick", uiStates.value.info.color.toString())
                        viewModel.updateIsUserDropdownVisible(
                            !uiStates.value.isUserDropdownVisible
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            VicoBarChart(
                chartDataSeries = listOf(
                    ChartData(
                        x = List(9) { Random.nextInt(20, 100) },
                        y = (0..9).toList(),
                        color = Color.Green
                    ),

                    ChartData(
                        x = List(9) { Random.nextInt(20, 100) },
                        y = (0..9).toList(),
                        color = Color.Yellow
                    ),

                    ChartData(
                        x = List(9) { Random.nextInt(20, 100) },
                        y = (0..9).toList(),
                        color = Color.Red
                    )
                ),
//                xValueFormatter = {
//                    "R ${it.toInt()}"
//                },
//                yValueFormatter = {
//                    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
//                    days[it.toInt()]
//                }
            )
        }

        DropDownUserProfile(
            paddingValues,
            contentColor = contentColor,
            backgroundColor = backgroundColor.copy(alpha = 0.9f),
            visible = uiStates.value.isUserDropdownVisible,
            userState = userState,
            isLoading = uiStates.value.isLogOutLoading,
        ) {
            viewModel.updateIsLogOutLoading(!uiStates.value.isLogOutLoading)
        }
    }
}