// Great is the LORD of hosts
package com.example.moneytracker.helper

import android.icu.text.DecimalFormat
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.TagIcon
import com.google.firebase.Timestamp
import kotlinx.datetime.minus
import network.chaintech.kmp_date_time_picker.utils.now
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

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
fun <T> List<T>.mean(selector: (T) -> Double): Double {
    if (isEmpty()) return 0.0
    return sumOf { selector(it) } / size
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
        val avg = mean { it.amount }
        // sum of (value - mean)^2 / count
        return sumOf { (it.amount - avg).let { diff -> diff * diff } } / size
    }

/**
 * Calculates the Standard Deviation: The square root of the Variance.
 */
val List<Dataset>.std: Double
    get() = kotlin.math.sqrt(variance)

val Dataset.isStartDateTimeNotEqualToDeadlineDateTime: Boolean
    get() = dateTime != deadlineDateTime


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

val Adjustment.isForToday: Boolean
    @RequiresApi(Build.VERSION_CODES.O)
    get() {
        val today = kotlinx.datetime.LocalDateTime.now().date
        val dataDate = dateTime.toLocalDateTimeUtc().date
        return today == dataDate
    }

/**
 * Check if the dataset is for yesterday.
 */
val Dataset.isForYesterday: Boolean
    @RequiresApi(Build.VERSION_CODES.O)
    get() {
        val yesterday = kotlinx.datetime.LocalDateTime.now()
            .date.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        val dataDate = dateTime.toLocalDateTimeUtc().date

        return yesterday == dataDate
    }

val Adjustment.isForYesterday: Boolean
    @RequiresApi(Build.VERSION_CODES.O)
    get() {
        val yesterday = kotlinx.datetime.LocalDateTime.now()
            .date.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        val dataDate = dateTime.toLocalDateTimeUtc().date

        return yesterday == dataDate
    }


fun Dataset.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "dataType" to dataType.name, // store enum as String
        "amount" to amount,
        "label" to label,
        "description" to description,
        "dateTime" to dateTime, // Firestore Timestamp as-is
        "deadlineDateTime" to deadlineDateTime,
        "tagIcon" to tagIcon.tagIconToMap, // change model to use a string key
        "paymentMethod" to paymentMethod.name,
        "status" to status.name,
        "adjustment" to adjustment.map { it.adjustmentToMap }, // already map form
        "multipleStatus" to multipleStatus.map { it.name }
    )
}


val Adjustment.adjustmentToMap: Map<String, Any>
    get() = mapOf(
        "adjustmentId" to adjustmentId,
        "amount" to amount,
        "label" to label,
        "description" to description,
        "dateTime" to dateTime,
        "tagIcon" to tagIcon.tagIconToMap,
        "paymentMethod" to paymentMethod,
        "adjustmentType" to adjustmentType
    )

val TagIcon.tagIconToMap: Map<String, Any>
    get() = mapOf(
        "name" to name,
        "icon" to icon
    )

fun Map<*, *>.asTagIcon(): TagIcon {
    val iconValue = (this["icon"] as? Number)?.toInt()
    return TagIcon(
        name = this["name"] as? String ?: "",
        icon = if (iconValue == null || iconValue == 0) R.drawable.circle_error else iconValue
    )
}

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
            AdjustmentType.INITIAL
        }

        is Number -> AdjustmentType.entries.getOrNull(dt.toInt()) ?: AdjustmentType.INITIAL
        is Map<*, *> -> {
            val name = (dt["name"] ?: dt["value"] ?: dt["text"]) as? String
            if (name != null) try {
                AdjustmentType.valueOf(name)
            } catch (_: Exception) {
                AdjustmentType.INITIAL
            } else AdjustmentType.INITIAL
        }

        else -> AdjustmentType.INITIAL
    }

    val tagIcon = (this["tagIcon"] as? Map<*, *>)?.asTagIcon() ?: TagIcon(
        name = "",
        icon = R.drawable.circle_error
    )
    val adjustmentId = this["adjustmentId"] as String

    return Adjustment(
        amount = amount,
        label = label,
        description = description,
        dateTime = dateTime,
        tagIcon = tagIcon,
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
        tagIcon = (this["tagIcon"] as? Map<*, *>)?.asTagIcon() ?: TagIcon(
            name = "",
            icon = R.drawable.circle_error
        ),
        paymentMethod = parseEnum(
            this["paymentMethod"],
            PaymentMethod.entries.toTypedArray(),
            PaymentMethod.CASH
        ),
        status = parseEnum(
            this["status"],
            Status.entries.toTypedArray(),
            Status.PENDING
        ),
        adjustment = (this["adjustment"] as? List<*>)
            ?.mapNotNull { (it as? Map<*, *>)?.asAdjustment() }
            ?: emptyList(),
        multipleStatus = this.toMultipleStatus()
    )
}

fun Map<*, *>.toMultipleStatus(): List<Status> {
    val statusList = this["multipleStatus"] as? List<*>
    return statusList?.mapNotNull {
        try {
            Status.valueOf(it as String)
        } catch (_: Exception) {
            null
        }
    } ?: emptyList()
}


fun casting(any: Any?): List<Map<String, Any>>? {
    if (any != null) {
        val map: List<Map<String, Any>> = (any as List<*>).filterIsInstance<Map<String, Any>>()
        return map
    }
    return null
}

fun castToStatusList(any: Any?): List<Status>? {
    if (any != null) {
        val lst: List<Status> = (any as List<*>).filterIsInstance<Status>()
        return lst
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

val Dataset.isOverdue: Status
    get() {
        val currentTime = kotlinx.datetime.LocalDateTime.now()
        val deadlineDateTime = deadlineDateTime.toLocalDateTimeUtc()
        return when (dataType) {
            DataType.GOAL if currentTime >= deadlineDateTime
                    && remainingAmount != 0.0
                -> {
                Status.OVERDUE
            }

            DataType.GOAL if currentTime >= deadlineDateTime
                    && remainingAmount == 0.0
                -> {
                Status.SUCCESS
            }

            DataType.GOAL if currentTime < deadlineDateTime
                -> {
                Status.PENDING
            }

            else -> {
                Status.INITIAL
            }
        }
    }


fun Long.formatToAmount(): String {
    val locale = Locale.getDefault()
    val numberFormat = NumberFormat.getCurrencyInstance(locale)
    val symbol = numberFormat.currency?.symbol ?: "$"

    if (this < 1_000_000) {
        val amount = toDouble()
        val formattedAmount = amount.toString()
            .replace(Regex("\\B(?=(\\d{3})+(?!\\d))"), ",")
            .replace(Regex("\\.0$"), "")
        return "$symbol$formattedAmount"
    }
    val suffixes = charArrayOf('M', 'B', 'T', 'Q') // M for Million, etc.
    val formatter = DecimalFormat("#.#")
    val base = (log10(this.toDouble()) / 3).toInt()
    val scaledNumber = this / 1000.0.pow(base.toDouble())
    return "$symbol${formatter.format(scaledNumber) + suffixes[base - 2]}"
}


fun Float.formatToAmount(): String {
    return this.toLong().formatToAmount()
}

fun Double.formatToAmount(): String {
    val locale = Locale.getDefault()
    val numberFormat = NumberFormat.getCurrencyInstance(locale)
    val symbol = numberFormat.currency?.symbol ?: "$"

    if (this < 1_000_000) {
        val rounding = BigDecimal(this).setScale(2, RoundingMode.HALF_UP)

        val formattedAmount = rounding.toString()
            .replace(Regex("\\B(?=(\\d{3})+(?!\\d))"), ",")
            .replace(Regex("\\.00$"), "")
        return "$symbol$formattedAmount"
    }

    return this.toLong().formatToAmount()
}