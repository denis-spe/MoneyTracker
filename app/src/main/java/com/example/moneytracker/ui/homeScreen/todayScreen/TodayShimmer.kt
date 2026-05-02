// Praise be the LORD GOD, For the LORD is good and his mercy endures forever
package com.example.moneytracker.ui.homeScreen.todayScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.helper.shimmerEffect

@Composable
fun StatAreaShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Chart Area Shimmer
        Box(
            modifier = Modifier
                .weight(1.1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = CircleShape, size = 150.dp)
            )
        }

        // Pager Area Shimmer
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shimmerEffect(
                        shape = RoundedCornerShape(12.dp),
                        width = 300.dp,
                        height = 80.dp
                    )
            )
        }
    }
}

@Composable
fun ItemListAreaShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        // Sort Area Shimmer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = RoundedCornerShape(4.dp), width = 160.dp, height = 24.dp)
            )
            Row {
                Box(
                    modifier = Modifier
                        .shimmerEffect(shape = CircleShape, size = 32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .shimmerEffect(shape = CircleShape, size = 32.dp)
                )
            }
        }

        // List Items Shimmer
        Column {
            repeat(2) {
                ItemCardShimmer()
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ItemCardShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading Icon
        Box(
            modifier = Modifier
                .shimmerEffect(shape = CircleShape, size = 40.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Content Area
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .shimmerEffect(
                        shape = RoundedCornerShape(10.dp),
                        width = 150.dp,
                        height = 18.dp
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .shimmerEffect(shape = RoundedCornerShape(4.dp), width = 100.dp, height = 14.dp)
            )
        }

        // Trailing Content
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = RoundedCornerShape(4.dp), width = 70.dp, height = 18.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .shimmerEffect(shape = CircleShape, size = 20.dp)
            )
        }
    }
}
