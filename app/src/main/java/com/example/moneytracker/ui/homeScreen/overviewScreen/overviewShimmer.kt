package com.example.moneytracker.ui.homeScreen.overviewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.ui.homeScreen.dataAddition.ICON_SIZE


private val SPACE = 10.dp

@Composable
fun HeaderShimmer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(150.dp)
            .height(24.dp)
            .shimmerEffect(shape = RoundedCornerShape(4.dp))
    )
}

@Composable
fun TransactionCardShimmer(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(50))
    ) {
        Row(
            modifier = Modifier
                .width(165.dp)
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .shimmerEffect(shape = CircleShape)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(ICON_SIZE)
                        .shimmerEffect(shape = CircleShape)
                )
            }

            Column {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(14.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(12.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .height(14.dp)
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.05f)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(24.dp)
                        .padding(top = 4.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .shimmerEffect(shape = CircleShape)
                )
            },
            trailingContent = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(14.dp)
                            .shimmerEffect(shape = RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(12.dp)
                    .shimmerEffect(shape = RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(12.dp)
                    .shimmerEffect(shape = RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun SettlementCardShimmer(
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(130.dp)
            .clip(RoundedCornerShape(10)),
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .shimmerEffect(shape = CircleShape)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(16.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp)
                        .shimmerEffect(shape = RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
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
    Column {
        repeat(3) {
            Spacer(modifier = Modifier.height(SPACE))
            HeaderShimmer()
            Spacer(modifier = Modifier.height(SPACE))

            when (it) {
                0 -> { // Transactions
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(3) {
                            TransactionCardShimmer(
                                modifier = Modifier.padding(end = SPACE)
                            )
                        }
                    }
                }

                1 -> { // Goals
                    repeat(2) {
                        GoalCardShimmer(
                            modifier = Modifier
                                .padding(bottom = SPACE)
                                .clip(RoundedCornerShape(10))
                        )
                    }
                }

                2 -> { // Settlements
                    LazyRow(modifier = Modifier.fillMaxWidth()) {
                        items(3) {
                            SettlementCardShimmer(
                                modifier = Modifier.padding(end = SPACE)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(SPACE))
        }
    }
}
