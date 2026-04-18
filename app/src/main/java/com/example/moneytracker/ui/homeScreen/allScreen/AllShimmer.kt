// Hear oh Israel, The LORD our GOD, The LORD is one,
// Thou shalt love the LORD your God with all your heart and
// soul and with all your mind,
// and you shall love your neighbor as yourself
package com.example.moneytracker.ui.homeScreen.allScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.helper.shimmerEffect
import com.example.moneytracker.ui.homeScreen.todayScreen.ItemCardShimmer

@Composable
fun ListForAllShimmer(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        repeat(6) {
            ItemCardShimmer()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun CalendarViewSectionShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .shimmerEffect()
                    .fillMaxWidth(0.25f)
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .shimmerEffect()
                    .fillMaxWidth(0.25f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .shimmerEffect()
                    .fillMaxWidth()
            )
        }
    }
}