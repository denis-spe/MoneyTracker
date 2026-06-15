// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.homeScreen.todayScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.ui.UserViewModel
import com.example.moneytracker.ui.components.charts.DonutChartData
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.ItemListAreaSort
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.itemListContent
import com.example.moneytracker.ui.homeScreen.todayScreen.statArea.statArea

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodayScreen(
    paddingValues: PaddingValues,
    donutChartDataCollection: DataState<List<DonutChartData>>,
    uiState: HomeUiState,
    fulfillmentFinanceEntityList: DataState<List<FinanceEntity>>,
    homeViewModel: HomeViewModel,
    userViewModel: UserViewModel,
    datasetWithAdjust: DataState<List<DataSettlement>>,
    currentAmountBalance: DataState<Map<String, Double>>,
    liabilityBalance: DataState<Map<String, Double>>,
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        statArea(
            donutChartDataCollection = donutChartDataCollection,
            fulfillmentFinanceEntityList = fulfillmentFinanceEntityList,
            currentAmountBalance = currentAmountBalance,
            liabilityBalance = liabilityBalance
        )

        stickyHeader {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ItemListAreaSort(
                    uiState = uiState,
                    onFilterClick = homeViewModel::updateOnFilterClick,
                    categorySorting = homeViewModel::updateCategorySorting,
                    timeSorting = homeViewModel::updateTimeSorting,
                    amountSorting = homeViewModel::updateAmountSorting,
                    paymentSorting = homeViewModel::updatePaymentSorting,
                    alphabeticalOrder = homeViewModel::updateAlphabeticalOrder
                )
            }
        }

        itemListContent(
            datasetWithAdjust = datasetWithAdjust,
            viewModel = homeViewModel,
            userViewModel = userViewModel
        )
    }
}

