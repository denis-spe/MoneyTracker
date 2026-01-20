// Great is the LORD of hosts
package com.example.moneytracker.helper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentStatus
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.google.firebase.Timestamp
import network.chaintech.kmp_date_time_picker.utils.now

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

        val today = kotlinx.datetime.LocalDateTime.now().date
        val dataDate = dateTime.toLocalDateTimeUtc().date

        return today == dataDate
    }



val Adjustment.adjustmentToMap: Map<String, Any>
    get() = mapOf(
        "adjustmentId" to adjustmentId,
        "amount" to amount,
        "label" to label,
        "description" to description,
        "dateTime" to dateTime,
        "adjustmentIcon" to adjustmentIcon,
        "paymentMethod" to paymentMethod,
        "adjustmentType" to adjustmentType
    )


fun Map<*, *>.asAdjustment(): Adjustment {
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
    val paymentMethod = when (val dt = this["paymentMethod"]) {
        is String -> try {
            PaymentMethod.valueOf(dt)
        } catch (_: Exception) {
            PaymentMethod.CASH
        }

        is Number -> PaymentMethod.entries.getOrNull(dt.toInt()) ?: PaymentMethod.CASH
        is Map<*, *> -> {
            val name = (dt["name"] ?: dt["value"] ?: dt["text"]) as? String
            if (name != null) try {
                PaymentMethod.valueOf(name)
            } catch (_: Exception) {
                PaymentMethod.CASH
            } else PaymentMethod.CASH
        }

        else -> PaymentMethod.CASH
    }

    val adjustmentType = when (val dt = this["adjustmentType"]) {
        is String -> try {
            AdjustmentType.valueOf(dt)
        } catch (_: Exception) {
            AdjustmentType.REPAYMENT
        }

        is Number -> AdjustmentType.entries.getOrNull(dt.toInt()) ?: AdjustmentType.REPAYMENT
        is Map<*, *> -> {
            val name = (dt["name"] ?: dt["value"] ?: dt["text"]) as? String
            if (name != null) try {
                AdjustmentType.valueOf(name)
            } catch (_: Exception) {
                AdjustmentType.REPAYMENT
            } else AdjustmentType.REPAYMENT
        }

        else -> AdjustmentType.REPAYMENT
    }

    val adjustmentIcon = (this["adjustmentIcon"] as Number).toInt()
    val adjustmentId = this["adjustmentId"] as String

    return Adjustment(
        amount = amount,
        label = label,
        description = description,
        dateTime = dateTime,
        adjustmentIcon = adjustmentIcon,
        paymentMethod = paymentMethod,
        adjustmentType = adjustmentType,
        adjustmentId = adjustmentId
    )
}

fun Map<*, *>.toDataset(): Dataset {

    fun parseTimestamp(value: Any?): Timestamp = when (value) {
        is Timestamp -> value
        is Map<*, *> -> {
            val sec = (value["seconds"] as? Number)?.toLong() ?: 0L
            val nano = (value["nanoseconds"] as? Number)?.toInt() ?: 0
            Timestamp(sec, nano)
        }

        is Number -> Timestamp(value.toLong(), 0)
        else -> Timestamp.now()
    }

    fun <T : Enum<T>> parseEnum(
        value: Any?,
        values: Array<T>,
        fallback: T
    ): T = when (value) {
        is String -> values.firstOrNull { it.name == value } ?: fallback
        is Number -> values.getOrNull(value.toInt()) ?: fallback
        else -> fallback
    }

    return Dataset(
        id = this["id"] as? String ?: "",
        dataType = parseEnum(this["dataType"], DataType.entries.toTypedArray(), DataType.EARNINGS),
        amount = (this["amount"] as? Number)?.toDouble()
            ?: (this["amount"] as? String)?.toDoubleOrNull()
            ?: 0.0,
        label = this["label"] as? String ?: "",
        description = this["description"] as? String ?: "",
        dateTime = parseTimestamp(this["dateTime"]),
        deadlineDateTime = parseTimestamp(this["deadlineDateTime"]),
        labelIcon = (this["labelIcon"] as? Number)?.toInt() ?: 0,
        paymentMethod = parseEnum(
            this["paymentMethod"],
            PaymentMethod.entries.toTypedArray(),
            PaymentMethod.CASH
        ),
        adjustmentStatus = parseEnum(
            this["adjustmentStatus"],
            AdjustmentStatus.entries.toTypedArray(),
            AdjustmentStatus.PENDING
        ),
        adjustment = (this["adjustment"] as? List<*>)
            ?.mapNotNull { (it as? Map<*, *>)?.asAdjustment() }
            ?: emptyList()
    )
}


fun casting(any: Any?): List<Map<String, Any>>? {
    if (any != null) {
        val map: List<Map<String, Any>> = (any as List<*>).filterIsInstance<Map<String, Any>>()
        return map
    }
    return null
}

// extension for Firebase Timestamp
fun Timestamp.toEpochMillis(): Long =
    this.seconds * 1000L + (this.nanoseconds / 1_000_000L)


fun Dataset.isAmountEqualToAdjustAmount(): Boolean {
    return adjustment.sumOf { it.amount } == amount
}

val Dataset.remainingAmount: Double
    get() = amount - adjustment.sumOf { it.amount }

val Dataset.isOverdue: Boolean
    get() {
        val currentTime = kotlinx.datetime.LocalDateTime.now()
        val deadlineDateTime = deadlineDateTime.toLocalDateTimeUtc()
        return currentTime >= deadlineDateTime
    }

val Dataset.status: AdjustmentStatus
    get() = when {
        isOverdue -> AdjustmentStatus.FAILED
        isAmountEqualToAdjustAmount() -> AdjustmentStatus.COMPLETED
        else -> AdjustmentStatus.PENDING
    }