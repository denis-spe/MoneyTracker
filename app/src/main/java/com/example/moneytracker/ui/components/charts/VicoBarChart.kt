// Hear oh Israel, The LORD our GOD, the LORD is one,
// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self.
package com.example.moneytracker.ui.components.charts


import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.Fill

@Composable
fun VicoBarChart(
    modifier: Modifier = Modifier,
    chartDataSeries: List<ChartData>,
    placeholderChartDataSeries: List<ChartData> = listOf(
        ChartData(
            x = listOf(0, 1, 2, 3, 4, 5, 6),
            y = listOf(0, 1, 2, 3, 4, 5, 6),
            color = Color.Unspecified
        )
    ),
    count: Int = 6,
    xValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    yValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    markerFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() }
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    var columnSeries = chartDataSeries

    if (columnSeries.isEmpty()) {
        columnSeries = placeholderChartDataSeries
    }


    val columnLayer = rememberColumnCartesianLayer(
        ColumnCartesianLayer.ColumnProvider.series(
            columnSeries.map { lineData ->
                val color = lineData.color

                rememberLineComponent(fill(color), Defaults.COLUMN_WIDTH.dp)
            }
        )
    )


    val chart = rememberCartesianChart(
        columnLayer,
        marker = rememberMarker(valueFormatter = { _, value ->
            markerFormatter(value[0].x)
        }),
        bottomAxis = HorizontalAxis.rememberBottom(
            guideline = null,
            valueFormatter = { _, value, _ -> xValueFormatter(value) },
            itemPlacer = HorizontalAxis.ItemPlacer.aligned({ count })
        ),
        startAxis = VerticalAxis.rememberStart(
            line = rememberLineComponent(Fill.Transparent),
            title = "X",
//            itemPlacer = VerticalAxis.ItemPlacer.count({ count }),
            valueFormatter = { _, value, _ -> yValueFormatter(value) }
        )
    )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries {
                columnSeries.forEach { lineData ->
                    series(
                        x = lineData.x,
                        y = lineData.y
                    )
                }
            }
        }
    }


    CartesianChartHost(
        chart = chart,
        modelProducer = modelProducer,
        modifier = modifier
            .height(280.dp)
    )
}