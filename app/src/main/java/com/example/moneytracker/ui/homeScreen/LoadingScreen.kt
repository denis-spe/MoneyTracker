// Bless be the name of LORD of hosts
package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneytracker.R
import com.example.moneytracker.ui.theme.MoneyTrackerTheme

@Composable
fun LoadingScreen() {
    val customColors = MoneyTrackerTheme.colors
    val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MoneyTrackerTheme.colors.autoBackground),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = customColors.contentColor,
                style = typography.headlineMedium,
                fontSize = 30.sp
            )

        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 65.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    gapSize = 0.dp,
                    color = MoneyTrackerTheme.colors.themeColor,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                Text(
                    text = buildString {
                        append("Glory be to the Lord of hosts\n")
                        append("Copyright@$currentYear Den.\n All rights reserved.")
                    },
                    color = customColors.contentColor,
                    style = typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}