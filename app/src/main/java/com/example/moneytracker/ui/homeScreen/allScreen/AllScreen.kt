// Hear oh Israel, The LORD our GOD, The LORD is one,
// Thou shalt love the LORD your God with all your heart and soul and with all your mind,
// and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel

@Composable
fun AllScreen(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel,
    weeklyData: State<List<DataAdjust>>,
    uiState: HomeUiState,
    hasLoadedData: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        if (hasLoadedData) {
            CalendarViewSection(
                updateWeek = viewModel::updateWeekDays,
                viewModel = viewModel
            )

            ListForAll(dataAdjusts = weeklyData.value)
        } else {
            CalendarViewSectionShimmer()
            ListForAllShimmer()
        }
    }
}

