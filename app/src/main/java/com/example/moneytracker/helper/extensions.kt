// Great is the LORD of hosts
package com.example.moneytracker.helper

import android.icu.text.DecimalFormat
import android.util.Log
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Adjustment
import com.example.moneytracker.backend.storage.AdjustmentType
import com.example.moneytracker.backend.storage.DataType
import com.example.moneytracker.backend.storage.Dataset
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.StatusHistory
import com.example.moneytracker.backend.storage.TagIcon
import com.google.firebase.Timestamp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import net.objecthunter.exp4j.ExpressionBuilder
import network.chaintech.kmp_date_time_picker.utils.now
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

private val zone = ZoneId.systemDefault()

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

fun java.time.LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()): Long =
    atZone(zone).toInstant().toEpochMilli()

fun Long.toLocalDateTime(zone: ZoneId = ZoneId.systemDefault()): java.time.LocalDateTime =
    Instant.ofEpochMilli(this).atZone(zone).toLocalDateTime()

val RoutineData.triggerMillis: Long
    get() = getTriggerMillisFrom(System.currentTimeMillis())

val RoutineData.rescheduleDeadline: java.time.LocalDateTime
    get() {
        val now = java.time.LocalDateTime.now()

        return when (routine) {
            Routine.EveryMinute -> {
                now.plusMinutes(routineCount.toLong())
            }

            Routine.EveryHour -> {
                now.plusHours(routineCount.toLong())
            }

            Routine.EveryDay -> {
                now.plusDays(routineCount.toLong())
            }

            Routine.Weekly -> {
                now.plusWeeks(routineCount.toLong())
            }

            Routine.Monthly -> {
                now.plusMonths(routineCount.toLong())
            }

            Routine.Yearly -> {
                now.plusYears(routineCount.toLong())
            }

            else -> {
                now.plusMinutes(1)
            }
        }
    }

/**
 * Calculates the next trigger time based on a [baseMillis].
 */
fun RoutineData.getTriggerMillisFrom(baseMillis: Long): Long {
    val base = ZonedDateTime.ofInstant(Instant.ofEpochMilli(baseMillis), ZoneId.systemDefault())
    val count = if (this.routineCount <= 0) 1L else this.routineCount.toLong()

    return when (this.routine) {
        Routine.EveryMinute -> base.plusMinutes(count).toInstant()
            .toEpochMilli()

        Routine.EveryHour -> base.plusHours(count).toInstant()
            .toEpochMilli()

        Routine.EveryDay -> base.plusDays(count).toInstant()
            .toEpochMilli()

        Routine.Weekly -> base.plusWeeks(count).toInstant()
            .toEpochMilli()

        Routine.Monthly -> base.plusMonths(count).toInstant()
            .toEpochMilli()

        Routine.Yearly -> base.plusYears(count).toInstant()
            .toEpochMilli()

        Routine.SpecifyDayOfTheWeek -> {
            val targetDay = DayOfWeek.of(((count - 1) % 7 + 1).toInt())
            val next = if (base.dayOfWeek == targetDay) {
                base.plusWeeks(1)
            } else {
                base.with(TemporalAdjusters.next(targetDay))
            }
            next.toInstant().toEpochMilli()
        }

        else -> baseMillis + 86_400_000
    }
}

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
    get() = routine.startDateTime != routine.deadlineDateTime


/**
 * Check if the dataset is for today.
 */
val Dataset.isForToday: Boolean
    get() {

        val today = LocalDateTime.now().date
        val dataDate = createdAt.toLocalDateTimeUtc().date

        return today == dataDate
    }

val Adjustment.isForToday: Boolean
    get() {
        val today = LocalDateTime.now().date
        val dataDate = dateTime.toLocalDateTimeUtc().date
        return today == dataDate
    }

/**
 * Check if the dataset is for yesterday.
 */
val Dataset.isForYesterday: Boolean
    get() {
        val yesterday = LocalDateTime.now()
            .date.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        val dataDate = createdAt.toLocalDateTimeUtc().date

        return yesterday == dataDate
    }

val Adjustment.isForYesterday: Boolean
    get() {
        val yesterday = LocalDateTime.now()
            .date.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        val dataDate = dateTime.toLocalDateTimeUtc().date

        return yesterday == dataDate
    }


fun Dataset.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "dataType" to dataType.name,
        "amount" to amount,
        "label" to label,
        "description" to description,
        "createdAt" to createdAt,
        "tagIcon" to tagIcon.tagIconToMap,
        "paymentMethod" to paymentMethod.name,
        "adjustment" to adjustment.map { it.adjustmentToMap },
        // statusHistory is now stored in subcollection, not in document
        "routineData" to routine.routineToMap
    )
}

val RoutineData.routineToMap: Map<String, Any>
    get() = mapOf(
        "routine" to routine.name,
        "routineCount" to routineCount,
        "stopRoutine" to stopRoutine,
        "startDateTime" to startDateTime,
        "deadlineDateTime" to deadlineDateTime
    )

val StatusHistory.statusHistoryToMap: Map<String, Any>
    get() = mapOf(
        "status" to status,
        "totalAdjustmentAmount" to totalAdjustmentAmount,
        "startDateTime" to startDateTime,
        "deadlineDateTime" to deadlineDateTime
    )

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

val Status.statusToMap: Map<String, Any>
    get() = mapOf(
        "text" to text,
        "color" to color,
        "icon" to icon
    )


val TagIcon.tagIconToMap: Map<String, Any>
    get() = mapOf(
        "name" to name,
        "icon" to icon
    )

fun Map<*, *>.asStatusHistory(): StatusHistory {
    val status = try {
        Status.valueOf(this["status"] as String)
    } catch (_: Exception) {
        Status.INITIAL
    }
    val totalAdjustmentAmount = (this["totalAdjustmentAmount"] as? Number)?.toDouble() ?: 0.0

    val startDateTime = when (val dt = this["startDateTime"]) {
        is Timestamp -> dt
        is Map<*, *> -> {
            val seconds = (dt["seconds"] as? Number)?.toLong()
                ?: (dt["seconds"] as? String)?.toLongOrNull()
                ?: 0L
            val nanoseconds = (dt["nanoseconds"] as? Number)?.toInt()
                ?: (dt["nanoseconds"] as? String)?.toIntOrNull()
                ?: 0
            Timestamp(seconds, nanoseconds)
        }
        is Number -> Timestamp(dt.toLong(), 0)
        is String -> (dt.toLongOrNull()?.let { Timestamp(it, 0) }) ?: Timestamp(0, 0)
        else -> Timestamp(0, 0)
    }

    val deadlineDateTime = when (val dt = this["deadlineDateTime"]) {
        is Timestamp -> dt
        is Map<*, *> -> {
            val seconds = (dt["seconds"] as? Number)?.toLong()
                ?: (dt["seconds"] as? String)?.toLongOrNull()
                ?: 0L
            val nanoseconds = (dt["nanoseconds"] as? Number)?.toInt()
                ?: (dt["nanoseconds"] as? String)?.toIntOrNull()
                ?: 0
            Timestamp(seconds, nanoseconds)
        }
        is Number -> Timestamp(dt.toLong(), 0)
        is String -> (dt.toLongOrNull()?.let { Timestamp(it, 0) }) ?: Timestamp(0, 0)
        else -> Timestamp(0, 0)
    }

    return StatusHistory(
        status = status.name,
        totalAdjustmentAmount = totalAdjustmentAmount,
        startDateTime = startDateTime,
        deadlineDateTime = deadlineDateTime
    )
}

fun Map<*, *>.asTagIcon(): TagIcon {
    val iconValue = (this["icon"] as? Number)?.toInt()
    return TagIcon(
        name = this["name"] as? String ?: "",
        icon = if (iconValue == null || iconValue == 0) R.drawable.circle_error else iconValue
    )
}

fun Map<*, *>.asRoutineData(): RoutineData {

    fun parseRoutine(value: Any?): Routine {
        return when (value) {
            is String -> Routine.entries.firstOrNull {
                it.name.equals(value, ignoreCase = true)
            } ?: Routine.Nothing

            is Number -> Routine.entries.getOrNull(value.toInt())
                ?: Routine.Nothing

            is Map<*, *> -> {
                val name = value["name"] ?: value["value"] ?: value["text"]
                parseRoutine(name)
            }

            else -> Routine.Nothing
        }
    }

    val routine = parseRoutine(this["routine"])
    val routineCount = (this["routineCount"] as? Number)?.toInt() ?: 0
    val stopRoutine = this["stopRoutine"] as? Boolean ?: true
    val startDateTime = parseTimestamp(this["startDateTime"])
    val deadlineDateTime = parseTimestamp(this["deadlineDateTime"])
    val triggerMillis = (this["triggerMillis"] as? Number)?.toLong() ?: 0L

    return RoutineData(
        routine = routine,
        routineCount = routineCount,
        stopRoutine = stopRoutine,
        startDateTime = startDateTime,
        deadlineDateTime = deadlineDateTime,
        triggerMillis = triggerMillis
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

fun LocalDateTime.plusHour(hour: Int): LocalDateTime {

    return (
            this.toJavaLocalDateTime()
                .plusHours(hour.toLong())
                .toKotlinLocalDateTime()
            )
}

fun LocalDateTime.plusMinutes(minutes: Int): LocalDateTime {
    return (
            this.toJavaLocalDateTime()
                .plusMinutes(minutes.toLong())
                .toKotlinLocalDateTime()
            )
}

fun LocalDateTime.plusDays(days: Int): LocalDateTime {
    return (
            this.toJavaLocalDateTime()
                .plusDays(days.toLong())
                .toKotlinLocalDateTime()
            )
}

val Dataset.status: Status
    get() {
        val currentTime = LocalDateTime.now()
        val deadlineDateTime = routine.deadlineDateTime.toLocalDateTimeUtc()
        return when (dataType) {
            DataType.GOAL if currentTime >= deadlineDateTime
                    && remainingAmount != 0.0
                -> {
                Status.OVERDUE
            }

            DataType.GOAL if remainingAmount == 0.0
                -> {
                Status.COMPLETED
            }

            DataType.GOAL if currentTime < deadlineDateTime && remainingAmount != 0.0
                -> {
                Status.ACTIVE
            }

            DataType.DEBT if remainingAmount == 0.0 -> {
                Status.PAYBACK
            }

            DataType.LENT if remainingAmount == 0.0 -> {
                Status.REFUNDED
            }

            else -> {
                Status.INITIAL
            }
        }
    }

fun Map<*, *>.toAmount(): Double {
    return (this["amount"] as? Number)?.toDouble()
        ?: (this["amount"] as? String)?.toDoubleOrNull()
        ?: 0.0
}

fun Map<*, *>.toDataset(): Dataset {

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
        amount = this.toAmount(),
        label = this["label"] as? String ?: "",
        description = this["description"] as? String ?: "",
        createdAt = parseTimestamp(this["createdAt"]),
        tagIcon = (this["tagIcon"] as? Map<*, *>)?.asTagIcon() ?: TagIcon(
            name = "",
            icon = R.drawable.initial
        ),
        paymentMethod = parseEnum(
            this["paymentMethod"],
            PaymentMethod.entries.toTypedArray(),
            PaymentMethod.CASH
        ),
        routine = (this["routineData"] as? Map<*, *>)?.asRoutineData() ?: RoutineData(),
        adjustment = this.toAdjustment(),
        statusHistory = emptyList()  // StatusHistory is now in subcollection, load separately if needed
    )
}

val Double.formatResult: String
    get() {
        return if (this % 1.0 == 0.0) {
            toInt().toString()
        } else {
            String.format(Locale.US, "%.4f", this)
                .trimEnd('0')
                .trimEnd('.')
        }
    }

val CharSequence.eval: Double
    get() {
        val expression = replace(Regex("(\\s)"), "")
            .replace('÷', '/')
            .replace('×', '*')
            .replace(",", "")
        Log.d("StringEval", expression)
        return try {
            ExpressionBuilder(expression).build().evaluate()
        } catch (_: Exception) {
            0.0
        }
    }

fun Map<*, *>.toAdjustment(): List<Adjustment> {
    return (this["adjustment"] as? List<*>)
        ?.mapNotNull { (it as? Map<*, *>)?.asAdjustment() }
        ?: emptyList()
}

fun Map<*, *>.toStatusHistory(): List<StatusHistory> {
    val statusList = this["statusHistory"] as? List<*>
    return statusList?.mapNotNull {
        (it as? Map<*, *>)?.asStatusHistory()
    } ?: emptyList()
}

fun castToMutableMap(any: Any?): MutableMap<String, Any> {
    if (any == null) return mutableMapOf()
    return (any as Map<*, *>)
        .mapKeys { it.key.toString() }
        .mapValues { it as Any }
        .toMutableMap()
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

val Timestamp.formatToDateTime: String
    get() {
        val dateTime = toLocalDateTimeUtc()
        val day = dateTime.day.addZeroIfLessThenTen
        val hour = dateTime.hour.addZeroIfLessThenTen
        val minute = dateTime.minute.addZeroIfLessThenTen
        val month = dateTime.month.name.take(3).title
        val year = dateTime.year
        return "$day $month $year, $hour:$minute"
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
    val suffixes = charArrayOf('M', 'B', 'T', 'Q') // M for A Million, etc.
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