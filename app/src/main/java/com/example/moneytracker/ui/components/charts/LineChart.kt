// Hear oh Israel, The LORD our GOD, the LORD is one,
// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self.
package com.example.moneytracker.ui.components.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moneytracker.backend.storage.Dataset
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun SimpleLineChart(
    data: List<Dataset>,
    modifier: Modifier = Modifier
) {

    // Map incoming data to chart entries (recomputed every composition)
    val entries = data.mapIndexed { i, v -> FloatEntry(i.toFloat(), v.amount.toFloat()) }

    // If there's only one point, duplicate it at x+1 so a line segment can be drawn
    val safeEntries = if (entries.size <= 1 && entries.isNotEmpty()) {
        val e = entries.first()
        listOf(e, FloatEntry(e.x + 1f, e.y))
    } else entries

    // Create the model from current entries (cheap operation).
    val model = entryModelOf(safeEntries)

    rememberStartAxis(
        label = textComponent()
    )

    val bottomAxis = rememberBottomAxis(
        itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 1)
    )

    Chart(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        chart = LineChart(),
        model = model,
//        startAxis = startAxis,
        bottomAxis = bottomAxis,
    )
}

