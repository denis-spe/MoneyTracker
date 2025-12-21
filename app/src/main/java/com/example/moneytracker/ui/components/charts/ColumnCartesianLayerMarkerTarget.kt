// Bless be the name of the LORD GOD
package com.example.moneytracker.ui.components.charts
/*
 * Copyright 2024 by Patryk Goworowski and Patrick Michalik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.common.component.LineComponent

///** Houses information on a set of [ColumnCartesianLayer] columns to be marked. */
interface ColumnCartesianLayerMarkerTarget : CartesianMarker.Target {
    /**
     * Holds [Column] instances, each of which houses information on a [ColumnCartesianLayer] column
     * to be marked.
     */
    val columns: List<Column>

    /**
     * Houses information on a [ColumnCartesianLayer] column to be marked.
     *
     * @param entry the [ColumnCartesianLayerModel.Entry].
     * @param canvasY the pixel _y_ coordinate of the column’s top or bottom edge (depending on the
     *   sign of [ColumnCartesianLayerModel.Entry.y]).
     * @param color the column [LineComponent]’s color.
     */
    data class Column(
        val entry: ColumnCartesianLayerModel.Entry,
        val canvasY: Float,
        val color: Int,
    )
}

internal data class MutableColumnCartesianLayerMarkerTarget(
    override val x: Double,
    override val canvasX: Float,
    override val columns: MutableList<ColumnCartesianLayerMarkerTarget.Column> = mutableListOf(),
) : ColumnCartesianLayerMarkerTarget {
    override fun equals(other: Any?): Boolean {
        if (other is CartesianMarker.Target) return true
        if (this === other) return true

        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + canvasX.hashCode()
        result = 31 * result + columns.hashCode()
        return result
    }
}

