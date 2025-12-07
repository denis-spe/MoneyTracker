package com.example.moneytracker.ui.homeScreen

import androidx.compose.ui.graphics.Color

data class Colors(
    val darkModeColor: Color = Color.White.copy(alpha = 0.8f),
    val lightModeColor: Color = Color.Black.copy(alpha = 0.8f),
    val darkModeBackgroundColor: Color = Color.Gray.copy(alpha = 0.7f),
    val lightModeBackgroundColor: Color = Color(0xFFD9D9D9).copy(alpha = 0.3f)
)