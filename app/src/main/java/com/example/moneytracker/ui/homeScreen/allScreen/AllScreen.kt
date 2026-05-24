// Hear oh Israel, The LORD our GOD, The LORD is one,
// Thou shalt love the LORD your God with all your heart and soul and with all your mind,
// and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeViewModel

@Composable
fun AllScreen(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel,
    userViewModel: UserViewModel,
    dataState: DataState<List<DataSettlement>>,
) {
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

        ListForAll(
            dataSettlements = dataState,
            viewModel = viewModel,
            userViewModel = userViewModel,
        )
    }
}

