package com.example.moneytracker.ui.components.charts

import androidx.compose.ui.graphics.Color

data class ChartData(
    val x: List<Int> = listOf(),
    val y: List<Int> = listOf(),
    val color: Color = Color.Gray
)
