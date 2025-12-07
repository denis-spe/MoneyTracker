// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.R
import com.example.moneytracker.ui.homeScreen.topNavigation.DropDownUserProfile
import com.example.moneytracker.ui.homeScreen.topNavigation.TopNavPanel
import com.example.moneytracker.ui.homeScreen.topTitle.TopTitlePanel
import com.example.moneytracker.ui.screenManager.StartUpScreenRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModel
    val viewModel: HomeScreenViewModel = hiltViewModel()
    // Collect user information from ViewModel
    val uiStates = viewModel.uiState.collectAsState()
    val userState = viewModel.userState.collectAsState()

    var visible by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag(stringResource(R.string.homeScreenId)),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                    titleContentColor = Color.White,
                ),
                title = {
                    TopTitlePanel(uiStates, viewModel::updateTopTitle)
                },
                navigationIcon = {
                    TopNavPanel(userState) {
                        visible = !visible
                    }
                }
            )
        }
    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(uiStates.value.datasets.toString())
//        }
        DropDownUserProfile(
            paddingValues,
            visible = visible,
            userState = userState,
        ) {
            visible = false
            onNavigate?.navigate(StartUpScreenRouter)
            viewModel.signOut()
        }
    }
}