// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.collections.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.dataAddition.DataAdditionFloatingButton
import com.example.moneytracker.ui.homeScreen.dataAddition.DataAdditionModelDrawer
import com.example.moneytracker.ui.homeScreen.topNavigation.DropDownUserProfile
import com.example.moneytracker.ui.homeScreen.topNavigation.TopNavPanel
import com.example.moneytracker.ui.homeScreen.topTitle.TopTitlePanel
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModel
    val viewModel: HomeScreenViewModel = hiltViewModel()
    // Collect user information from ViewModel
    val uiStates = viewModel.uiState.collectAsState()
    val userState = viewModel.userState.collectAsState()
    val datasets = uiStates.value.datasets

    val colors = Colors()

    // Define the colors for the button
    val contentColor = (if (isSystemInDarkTheme()) colors.darkModeColor else colors.lightModeColor)

    // Panel and button color
    val backgroundColor = if (isSystemInDarkTheme()) colors.darkModeBackgroundColor else
        colors.lightModeBackgroundColor

    val donutChartDataCollection = DonutChartDataCollection(
        datasets
            .groupBy { it.dataType }
            .values.toList()
            .map { lst ->
                val firstItemInList = lst[0]
                val amount = lst.sumOf { it.amount }.toFloat()
                val color = colorResource(firstItemInList.dataType.color)
                val title = firstItemInList.dataType.text

                DonutChartData(
                    amount,
                    color = color,
                    title = title
                )
            }
    )


    LaunchedEffect(key1 = uiStates.value.isLogOutLoading) {
        delay(1000)
        if (uiStates.value.isLogOutLoading) {
            onNavigate?.navigate(StartUpScreenRouter)
            viewModel.signOut()
        }
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
                        viewModel.updateIsUserDropdownVisible(
                            !uiStates.value.isUserDropdownVisible
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            DataAdditionFloatingButton(viewModel::updateOnModelBottomSheetShow)
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DonutChart(
                data = donutChartDataCollection,
                chartSize = 150.dp,
                gapPercentage = 0.06f,
                strokeCap = StrokeCap.Round
            ) {
                Column {
                    Text(text = it?.title ?: "")
                    Text(text = (it?.amount ?: "").toString())
                }
            }

            LazyColumn {

                items(datasets.size) {
                    val dataset = datasets[it]
                    Text(dataset.dataType.text)
                }
            }
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
            viewModel.updateIsLogOutLoading(!uiStates.value.isLogOutLoading)
        }

        // Modal bottom sheet
        DataAdditionModelDrawer(
            uiStates.value.onModelBottomSheetShow,
            viewModel::updateOnModelBottomSheetShow,
            viewModel
        )
    }

}