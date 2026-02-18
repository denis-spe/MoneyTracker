// Hear oh Israel, The LORD our GOD, The LORD is one,
// Thou shalt love the LORD your God with all your heart and soul and with all your mind,
// and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.allScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.ui.homeScreen.HomeScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllScreen(
    paddingValues: PaddingValues,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {


    val weeklyData = viewModel.weeklyData.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CalendarViewSection(
            updateWeek = viewModel::updateWeekDays,
            viewModel = viewModel
        )

        ListOfData(data = weeklyData.value)
    }
}
