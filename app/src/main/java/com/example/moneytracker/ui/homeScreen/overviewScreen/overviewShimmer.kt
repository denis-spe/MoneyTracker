package com.example.moneytracker.ui.homeScreen.overviewScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.ui.dataAddition.ICON_SIZE


private val SPACE = 12.dp
private val CORNER_RADIUS = 16.dp

@Composable
fun HeaderShimmer(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(24.dp)
                .shimmerEffect(shape = RoundedCornerShape(4.dp))
        )
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(24.dp)
                .shimmerEffect(shape = RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun TransactionCardShimmer(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(170.dp)
            .clip(RoundedCornerShape(CORNER_RADIUS))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .shimmerEffect(shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .shimmerEffect(shape = CircleShape)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(12.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(18.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun GoalCardShimmer(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CORNER_RADIUS))
    ) {
        ListItem(
            headlineContent = {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(20.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
            },
            supportingContent = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(24.dp)
                            .padding(top = 4.dp)
                            .shimmerEffect(shape = RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(12.dp)
                            .shimmerEffect(shape = RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .shimmerEffect(shape = RoundedCornerShape(4.dp))
                    )
                }
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .shimmerEffect(shape = CircleShape)
                )
            },
            trailingContent = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(14.dp)
                            .shimmerEffect(shape = RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(12.dp)
                            .shimmerEffect(shape = RoundedCornerShape(4.dp))
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun LiabilityCardShimmer(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .clip(RoundedCornerShape(CORNER_RADIUS))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shimmerEffect(shape = CircleShape)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(16.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(20.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
fun OverviewShimmer() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recent Activity Shimmer
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderShimmer()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(3) {
                    TransactionCardShimmer()
                }
            }
        }

        // Active Goals Shimmer
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderShimmer()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    GoalCardShimmer()
                }
            }
        }

        // Liabilities Shimmer
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderShimmer()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(3) {
                    LiabilityCardShimmer()
                }
            }
        }
    }
}
