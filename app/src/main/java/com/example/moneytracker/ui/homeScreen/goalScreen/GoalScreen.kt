// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.goalScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.ui.homeScreen.DataViewModel
import com.example.moneytracker.ui.homeScreen.allScreen.DataCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalScreen(
    paddingValues: PaddingValues,
    viewModel: DataViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val goalDatasets = uiState.datasets.filter { it.dataType == DataType.GOAL }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LazyColumn {
            item { Spacer(modifier = Modifier.size(10.dp)) }
            items(goalDatasets) { dataset ->
                DataCard(
                    modifier = Modifier.animateItem(),
                    dataAdjust = DataAdjust.Data(dataset)
                )
            }
            item { Spacer(modifier = Modifier.size(10.dp)) }
        }
    }
}
