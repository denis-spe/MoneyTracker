package com.example.moneytracker.ui.components.charts.collections

import androidx.compose.ui.graphics.Color

data class ChartData(
    val x: List<Int> = listOf(),
    val y: List<Int> = listOf(),
    val color: Color = Color.Gray,
    // Optional label shown in legends. Keep default null to avoid breaking existing callers.
    val label: String? = null
) {
    /**
     * Returns true if both x and y are not empty
     */
    fun isXYNotEmpty(): Boolean {
        return x.isNotEmpty() && y.isNotEmpty()
    }
}