// Hear oh Israel, The LORD our GOD, The LORD is one, You shall love the
// LORD GOD with all your heart and with all your soul and with all your mind
// and with all your strength, and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.yesterdayScreen

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.ui.components.charts.collections.ChartData
import com.example.moneytracker.ui.homeScreen.DataState
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStatArea
import com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea.YesterdayStats

@Composable
fun YesterdayScreen(
    paddingValues: PaddingValues,
    sortAbleDataSettlementDataState: DataState<List<DataSettlement>>,
    yesterdayChartDataState: DataState<List<ChartData>>,
    yesterdayStatsDataState: DataState<YesterdayStats>,
) {
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
            YesterdayStatArea(
                modifier = Modifier.weight(0.4f),
                chartData = yesterdayChartDataState,
                stats = yesterdayStatsDataState
            )

            Spacer(modifier = Modifier.weight(0.05f))

            YesterdayItems(
                modifier = Modifier.weight(0.6f),
                dataSettlementDataState = sortAbleDataSettlementDataState,
            )
        }
    }
}


