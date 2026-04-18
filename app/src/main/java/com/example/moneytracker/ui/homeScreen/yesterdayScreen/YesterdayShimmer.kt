package com.example.moneytracker.ui.homeScreen.yesterdayScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.ui.homeScreen.todayScreen.ItemCardShimmer

@Composable
fun YesterdayStatAreaShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Tab row shimmer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .shimmerEffect()
            )
        }

        // Content Area Shimmer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )
    }
}

@Composable
fun YesterdayItemsShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        repeat(4) {
            ItemCardShimmer()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
