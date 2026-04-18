package com.example.moneytracker.ui.homeScreen.goalScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.ui.homeScreen.todayScreen.ItemCardShimmer

@Composable
fun GoalScreenShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        repeat(5) {
            ItemCardShimmer()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
