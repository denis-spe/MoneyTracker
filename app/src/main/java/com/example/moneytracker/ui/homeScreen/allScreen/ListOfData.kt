// Bless be the name of the LORD of hosts
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.moneytracker.backend.storage.DataAdjust

@Composable
fun ListOfData(data: List<DataAdjust>) {
    LazyColumn {
        items(data.size) {
            val dataItem = data[it]
            DataCard(dataItem)
        }
    }
}

@Composable
fun DataCard(data: DataAdjust) {
    val label = when (data) {
        is DataAdjust.Data -> data.dataset.label
        is DataAdjust.Adjust -> data.adjustment.label
    }

    Row {
        Text(text = label)
    }
}