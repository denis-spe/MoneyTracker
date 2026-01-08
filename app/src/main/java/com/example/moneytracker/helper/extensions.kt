// Great is the LORD of hosts
package com.example.moneytracker.helper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.Repay
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId

/**
 * Formats a double to a string with two decimal places.
 */
val Int.addZeroIfLessThenTen: String
    get() = if (this < 10) "0$this" else this.toString()

/**
 * Formats a double to a string with two decimal places.
 */
val String.title: String
    get() = lowercase().mapIndexedNotNull { index, c ->
        if (index == 0) c.uppercase() else c.lowercase()
    }.joinToString("")

/**
 * Calculates the Mean: The average of all values in a list.
 */
val List<Dataset>.mean: Double
    get() {
        if (isEmpty()) return 0.0
        return sumOf { it.amount } / size
    }

/**
 * Calculates the Median: The middle value in a sorted list of numbers.
 */
val List<Dataset>.median: Double
    get() {
        if (isEmpty()) return 0.0

        // 1. Sort the list by amount
        val sortedList = sortedBy { it.amount }
        val middle = size / 2

        return if (size % 2 == 0) {
            // 2. If even, median is the average of the two middle numbers
            (sortedList[middle - 1].amount + sortedList[middle].amount) / 2.0
        } else {
            // 3. If odd, median is the exact middle number
            sortedList[middle].amount
        }
    }

/**
 * Calculates the Variance: The average of the squared differences from the Mean.
 */
val List<Dataset>.variance: Double
    get() {
        if (isEmpty()) return 0.0
        val avg = mean
        // sum of (value - mean)^2 / count
        return sumOf { (it.amount - avg).let { diff -> diff * diff } } / size
    }

/**
 * Calculates the Standard Deviation: The square root of the Variance.
 */
val List<Dataset>.std: Double
    get() = kotlin.math.sqrt(variance)

/**
 * Check if the dataset is for today.
 */
val Dataset.isForToday: Boolean
    @RequiresApi(Build.VERSION_CODES.O)
    get() {
        val today = LocalDate.now(ZoneId.systemDefault())
        val dataDate = dateTime.toDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return dataDate == today
    }

val Dataset.subtractedRepay: Double
    get() = amount - repay.sumOf { it.amount }


val Repay.repayToMap: Map<String, Any>
    get() = mapOf(
        "repay" to repayId,
        "amount" to amount,
        "label" to label,
        "description" to description,
        "dateTime" to dateTime,
        "repayIcon" to repayIcon
    )


fun Map<*, *>.toRepay(): Repay {
    val amount = (this["amount"] as? Number)?.toDouble()
        ?: (this["amount"] as? String)?.toDoubleOrNull()
        ?: 0.0
    val label = this["label"] as? String ?: ""
    val description = this["description"] as? String ?: ""
    val dateTime = when (val dateTimeRaw = this["dateTime"]) {
        is Timestamp -> dateTimeRaw
        is Map<*, *> -> {
            val seconds = (dateTimeRaw["seconds"] as? Number)?.toLong()
                ?: (dateTimeRaw["seconds"] as? String)?.toLongOrNull()
                ?: 0L
            val nanoseconds = (dateTimeRaw["nanoseconds"] as? Number)?.toInt()
                ?: (dateTimeRaw["nanoseconds"] as? String)?.toIntOrNull()
                ?: 0
            Timestamp(seconds, nanoseconds)
        }

        is Number -> Timestamp(dateTimeRaw.toLong(), 0)
        is String -> (dateTimeRaw.toLongOrNull()?.let { Timestamp(it, 0) }) ?: Timestamp(0, 0)
        else -> Timestamp(0, 0)
    }
    val repayIcon = (this["repayIcon"] as Number).toInt()

    return Repay(
        amount = amount,
        label = label,
        description = description,
        dateTime = dateTime,
        repayIcon = repayIcon
    )
}

fun Map<*, *>.toDataset(): Dataset {
    // Parse dateTime which may be stored as a Timestamp, a map (with seconds/nanoseconds), or a numeric seconds value
    val dateTime = when (val dateTimeRaw = this["dateTime"]) {
        is Timestamp -> dateTimeRaw
        is Map<*, *> -> {
            val seconds = (dateTimeRaw["seconds"] as? Number)?.toLong()
                ?: (dateTimeRaw["seconds"] as? String)?.toLongOrNull()
                ?: 0L
            val nanoseconds = (dateTimeRaw["nanoseconds"] as? Number)?.toInt()
                ?: (dateTimeRaw["nanoseconds"] as? String)?.toIntOrNull()
                ?: 0
            Timestamp(seconds, nanoseconds)
        }

        is Number -> Timestamp(dateTimeRaw.toLong(), 0)
        is String -> (dateTimeRaw.toLongOrNull()?.let { Timestamp(it, 0) }) ?: Timestamp(0, 0)
        else -> Timestamp(0, 0)
    }

    // amount may be stored as Double or Long (Number) or String
    val amount = (this["amount"] as? Number)?.toDouble()
        ?: (this["amount"] as? String)?.toDoubleOrNull()
        ?: 0.0

    // dataType may be stored as a String (enum name), a Number (ordinal), or a map/pojo
    val dataType = when (val dt = this["dataType"]) {
        is String -> try {
            DataType.valueOf(dt)
        } catch (_: Exception) {
            DataType.EARNINGS
        }

        is Number -> DataType.entries.getOrNull(dt.toInt()) ?: DataType.EARNINGS
        is Map<*, *> -> {
            val name = (dt["name"] ?: dt["value"] ?: dt["text"]) as? String
            if (name != null) try {
                DataType.valueOf(name)
            } catch (_: Exception) {
                DataType.EARNINGS
            } else DataType.EARNINGS
        }

        else -> DataType.EARNINGS
    }

    // repay may be stored as a list of maps; ensure each item is a Map before calling toRepay()
    val repay: List<Repay> = (this["repay"] as? List<*>)?.mapNotNull { item ->
        (item as? Map<*, *>)?.toRepay()
    } ?: emptyList()

    val label = this["label"] as? String ?: ""
    val description = this["description"] as? String ?: ""

    val labelIcon = (this["labelIcon"] as? Number)?.toInt()
        ?: (this["labelIcon"] as? String)?.toIntOrNull()
        ?: 0

    // Map stored 'id' (if any) into the Dataset.id field
    val id = this["id"] as String

    return Dataset(
        id = id,
        dataType = dataType,
        amount = amount,
        label = label,
        description = description,
        dateTime = dateTime,
        labelIcon = labelIcon,
        repay = repay
    )
}