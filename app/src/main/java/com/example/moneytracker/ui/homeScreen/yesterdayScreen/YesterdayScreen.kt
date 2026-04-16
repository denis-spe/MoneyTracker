// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// LORD GOD with all your heart and with all your soul and with all your mind
// and with all your strength, and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.yesterdayScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.DatasetState
import com.example.moneytracker.ui.homeScreen.HomeUiState
import com.example.moneytracker.ui.homeScreen.ItemListAreaShimmer
import com.example.moneytracker.ui.homeScreen.StatAreaShimmer
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayItems
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStatArea

@Composable
fun YesterdayScreen(
    paddingValues: PaddingValues,
    uiState: HomeUiState,
    sortAbleDataAdjust: List<DataAdjust>,
    yesterdayDatasets: List<Dataset>,
) {

    val isLoading = uiState.datasetState is DatasetState.Loading

    val configuration = LocalConfiguration.current

    if (configuration.orientation == ORIENTATION_PORTRAIT) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (isLoading) {
                StatAreaShimmer(modifier = Modifier.weight(0.4f))
                Spacer(modifier = Modifier.weight(0.05f))
                ItemListAreaShimmer(modifier = Modifier.weight(0.6f))
            } else {
                YesterdayStatArea(
                    modifier = Modifier.weight(0.4f),
                    datasets = yesterdayDatasets
                )

                Spacer(modifier = Modifier.weight(0.05f))

                YesterdayItems(
                    modifier = Modifier.weight(0.6f),
                    dataAdjust = sortAbleDataAdjust
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                StatAreaShimmer(modifier = Modifier.weight(0.4f))
                ItemListAreaShimmer(modifier = Modifier.weight(0.6f))
            } else {
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
}

