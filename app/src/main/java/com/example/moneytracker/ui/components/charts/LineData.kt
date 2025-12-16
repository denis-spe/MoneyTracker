package com.example.moneytracker.ui.components.charts

import androidx.compose.ui.graphics.Color

data class LineData(
    val x: List<Float> = listOf(),
    val y: List<Int> = listOf(),
    val color: Color = Color.Gray
)
