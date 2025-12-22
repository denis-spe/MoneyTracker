// Hear oh Israel, The LORD our GOD, the LORD is one,
// Love the LORD your GOD with all your heart and with all your soul
// and with all your might and love your neighbor as your self.
package com.example.moneytracker.ui.components.charts

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.continuous
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.shader.toShaderProvider
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill

@Composable
fun VicoLineChart(
    modifier: Modifier = Modifier,
    chartDataSeries: List<ChartData>,
    placeholderChartDataSeries: List<ChartData> = listOf(
        ChartData(
            x = listOf(0, 1, 2, 3, 4, 5, 6),
            y = listOf(0, 1, 2, 3, 4, 5, 6),
            color = Color.Unspecified
        )
    ),
    fillArea: Boolean = false,
    lineType: LineCartesianLayer.PointConnector = LineCartesianLayer.PointConnector.cubic(),
    xValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    yValueFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() },
    markerFormatter: (value: Double) -> CharSequence = { value -> value.toInt().toString() }
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    var lineSeries = chartDataSeries

    if (lineSeries.isEmpty()) {
        lineSeries = placeholderChartDataSeries
    }

    val lineLayer = rememberLineCartesianLayer(
        lineProvider = { index, _ ->
            val lineColor = lineSeries[index]

            val gradientFill = LineCartesianLayer.AreaFill.single(
                Fill(
                    Brush.verticalGradient(
                        colors = listOf(
                            lineColor.color.copy(alpha = 0.35f),
                            lineColor.color.copy(alpha = 0f)
                        )
                    ).toShaderProvider()
                )
            )

            LineCartesianLayer.Line(
                fill = LineCartesianLayer.LineFill.single(Fill(lineColor.color.toArgb())),
                stroke = LineCartesianLayer.LineStroke.continuous(
                    cap = StrokeCap.Round,
                    thickness = 2.dp
                ),
                areaFill = if (fillArea) gradientFill else null,
                pointConnector = lineType
            )
        }
    )

    val chart = rememberCartesianChart(
        lineLayer,
        marker = rememberMarker(valueFormatter = { _, value ->
            markerFormatter(value[0].x)
        }),
        bottomAxis = HorizontalAxis.rememberBottom(
            guideline = null,
            valueFormatter = { _, value, _ -> xValueFormatter(value) }
        ),
        startAxis = VerticalAxis.rememberStart(
            line = rememberLineComponent(Fill.Transparent),
            title = "X",
            valueFormatter = { _, value, _ -> yValueFormatter(value) }
        )
    )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries {
                lineSeries.forEach { lineData ->
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
