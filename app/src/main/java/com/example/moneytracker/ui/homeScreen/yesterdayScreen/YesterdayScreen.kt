// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// LORD GOD with all your heart and with all your soul and with all your mind
// and with all your strength, and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.yesterdayScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayItems
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStatArea

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YesterdayScreen(
    paddingValues: PaddingValues,
) {
    val viewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
    val yesterdayDatasets by viewModel.yesterdayDatasets.collectAsState()
    val sortAbleDataAdjust by viewModel.sortYesterdayDataAdjust().collectAsState()
    val configuration = LocalConfiguration.current


    if (configuration.orientation == ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            YesterdayStatArea(
                modifier = Modifier.weight(0.4f),
                datasets = yesterdayDatasets
            )
            YesterdayItems(
                modifier = Modifier.weight(0.6f),
                dataAdjust = sortAbleDataAdjust
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            YesterdayStatArea(
                modifier = Modifier.weight(0.4f),
                datasets = yesterdayDatasets
            )
            YesterdayItems(
                modifier = Modifier.weight(0.6f),
                dataAdjust = sortAbleDataAdjust
            )
        }
    }
}

