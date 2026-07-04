// Hear oh Israel, The LORD our GOD, The LORD is one,
// Thou shalt love the LORD your God with all your heart and soul and with all your mind,
// and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeMainViewModel
import com.example.moneytracker.ui.usecase.ProfessionalSummary
import kotlinx.datetime.LocalDate

@Composable
fun AllScreen(
    paddingValues: PaddingValues,
    viewModel: AllViewModel,
    homeMainViewModel: HomeMainViewModel,
    userViewModel: UserViewModel,
    dataState: DataState<List<Pair<LocalDate, List<DataSettlement>>>>,
    summaryState: DataState<ProfessionalSummary>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarViewSection(
            updateWeek = viewModel::updateWeekDays,
            viewModel = viewModel
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                ProfessionalSummarySection(summaryState = summaryState)
            }

            listForAllContent(
                viewModel = homeMainViewModel,
                userViewModel = userViewModel,
                dataSettlements = dataState
            )
        }
    }
}
