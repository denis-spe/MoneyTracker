package com.example.moneytracker.ui.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HeaderNavUi() {
    val textColor = ButtonDefaults.textButtonColors()
        .copy(contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black)
    val backgroundColor = if (isSystemInDarkTheme()) Color.Gray else Color(0xFFFFFFFF)
    val fontWeight = FontWeight.Medium

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .shadow(2.dp, shape = RoundedCornerShape(60.dp))
            .fillMaxWidth(0.6f)
            .height(40.dp)
            .background(backgroundColor.copy(alpha = 0.3f))

    ) {
        TextButton(
            onClick = {},
            colors = textColor,
        ) {
            Text("Today", fontWeight = fontWeight)
        }

        TextButton(
            onClick = {},
            colors = textColor,

            ) {
            Text("Yesterday", fontWeight = fontWeight)
        }

        TextButton(
            onClick = {},
            colors = textColor
        ) {
            Text("All", fontWeight = fontWeight)
        }
    }
}