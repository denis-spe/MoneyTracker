package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HeaderNavUi() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(10.dp, shape = RoundedCornerShape(60.dp))
            .fillMaxWidth(0.6f)
            .height(40.dp)
            .background(Color.White.copy(0.7f))

    ) {
        TextButton(onClick = {}, modifier = Modifier.height(60.dp)) {
            Text("Today")
        }

        TextButton(onClick = {}) {
            Text("Yesterday")
        }

        TextButton(onClick = {}) {
            Text("All")
        }
    }
}