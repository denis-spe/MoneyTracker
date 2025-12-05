// Bless be the Name of the Lord
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.moneytracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: NavController? = null, userId: String) {
    // Initialize ViewModel
    val viewModel: HomeScreenViewModel = hiltViewModel()
    // Collect user information from ViewModel
    val uiStates = viewModel.uiState.collectAsState()
    viewModel.userState.collectAsState()


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
                    HeaderNavUi()
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
            Text(uiStates.value.datasets.toString())
        }
    }
}