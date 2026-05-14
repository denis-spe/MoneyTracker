package com.example.moneytracker.ui.homeScreen.overviewScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.helper.shimmerEffect


@Composable
fun GoalCardShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(100.dp)
                .shimmerEffect(
                    shape = RoundedCornerShape(10.dp),
                    width = 150.dp, height = 18.dp
                )
        )
    }
}

@Composable
fun GoalScreenShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        repeat(3) {
            GoalCardShimmer()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
