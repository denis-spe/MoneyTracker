// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.goalScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.ui.homeScreen.HomeViewModel
import com.example.moneytracker.ui.homeScreen.allScreen.DataCard

@Composable
fun GoalScreen(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val goalDatasets by viewModel.goalDatasets.collectAsStateWithLifecycle()

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
