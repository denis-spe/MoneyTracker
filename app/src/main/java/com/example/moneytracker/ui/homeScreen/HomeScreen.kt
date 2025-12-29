// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.helper.formatToAmount
import com.example.moneytracker.ui.components.charts.DonutChart
import com.example.moneytracker.ui.components.charts.collections.DonutChartData
import com.example.moneytracker.ui.components.charts.collections.DonutChartDataCollection
import com.example.moneytracker.ui.homeScreen.dataAddition.DataAdditionFloatingButton
import com.example.moneytracker.ui.homeScreen.dataAddition.DataAdditionModelDrawer
import com.example.moneytracker.ui.homeScreen.listItems.ItemList
import com.example.moneytracker.ui.homeScreen.topNavigation.DropDownUserProfile
import com.example.moneytracker.ui.homeScreen.topNavigation.TopNavPanel
import com.example.moneytracker.ui.homeScreen.topTitle.TopTitlePanel
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter
import kotlinx.coroutines.delay

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModel
    val viewModel: HomeScreenViewModel = hiltViewModel()
    // Collect user information from ViewModel
    val uiStates = viewModel.uiState.collectAsState()
    val userState = viewModel.userState.collectAsState()
    val datasets = uiStates.value.datasets

    // Keep the passed userId referenced to avoid unused-parameter warnings
    // userId intentionally unused in this screen; parameter kept for API stability

    val colors = Colors()

    // Define the colors for the button
    val contentColor = (if (isSystemInDarkTheme()) colors.darkModeColor else colors.lightModeColor)

    // Panel and button color
    val backgroundColor = if (isSystemInDarkTheme()) colors.darkModeBackgroundColor else
        colors.lightModeBackgroundColor

    val context = LocalContext.current

    val donutChartDataCollection = remember(datasets, context) {
        DonutChartDataCollection(
            datasets
                .groupBy { it.dataType }
                .values.toList()
                .map { lst ->
                    val firstItemInList = lst[0]
                    val amount = lst.sumOf { it.amount }.toFloat()
                    val colorInt = ContextCompat.getColor(context, firstItemInList.dataType.color)
                    val color = Color(colorInt)
                    val title = firstItemInList.dataType.text

                    DonutChartData(
                        amount,
                        color = color,
                        title = title
                    )
                }
        )
    }


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
                strokeCap = StrokeCap.Round,
                strokeWidthSelected = 30.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    it?.let { donutChartData ->
                        Text(text = donutChartData.title)
                        Text(text = donutChartData.amount.formatToAmount())
                    } ?: run {
                        var enabled by remember { mutableStateOf(true) }
                        val totalAmount: Float by animateFloatAsState(
                            if (enabled)
                                donutChartDataCollection.totalAmount
                            else 0f,
                            label = "Overall Amount",
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = LinearEasing,
                            )
                        )

                        Text(text = "Total")
                        Text(text = totalAmount.formatToAmount())
                    }
                }
            }

            ItemList(datasets)

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