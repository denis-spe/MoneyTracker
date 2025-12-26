// Bless be the Name of the LORD
package com.example.moneytracker.ui.components.charts.collections

data class DonutChartDataCollection(
    var items: List<DonutChartData> = emptyList()
) {
    internal var totalAmount: Float = items.sumOf { it.amount.toDouble() }.toFloat()
        private set
}
