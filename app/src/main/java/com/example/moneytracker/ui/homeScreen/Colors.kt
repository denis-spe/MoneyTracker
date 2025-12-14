package com.example.moneytracker.ui.homeScreen

import androidx.compose.ui.graphics.Color

data class Colors(
    val darkModeColor: Color = Color.White.copy(alpha = 0.8f),
    val lightModeColor: Color = Color.Black.copy(alpha = 0.8f),
    val darkModeBackgroundColor: Color = Color(0x00252424).copy(alpha = 0.5f),
    val lightModeBackgroundColor: Color = Color(0xFFE0DDDD).copy(alpha = 0.5f),
    val currentPageColor: Color = Color(0xFF8F8686).copy(alpha = 0.2f)
)