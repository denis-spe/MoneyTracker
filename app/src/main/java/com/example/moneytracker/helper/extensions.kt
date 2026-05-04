// Great is the LORD of hosts
package com.example.moneytracker.helper

import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.example.moneytracker.R
import com.example.moneytracker.backend.storage.Achievement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.storage.Settlement
import com.example.moneytracker.backend.storage.SettlementType
import com.example.moneytracker.backend.storage.Status
import com.example.moneytracker.backend.storage.TagIcon
import com.example.moneytracker.backend.storage.types.LiabilityType
import com.example.moneytracker.backend.storage.types.TransactionType
import com.google.firebase.Timestamp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
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

fun java.time.LocalDateTime.toMidnight(): java.time.LocalDateTime =
    this.withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)

fun LocalDateTime.toMidnight(): LocalDateTime =
    this.toJavaLocalDateTime()
        .toMidnight()
        .toKotlinLocalDateTime()

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

fun Modifier.shimmerEffect(
    shape: Shape = RectangleShape,
    width: Dp? = null,
    height: Dp? = null,
    size: Dp? = null
): Modifier = composed {
    val density = LocalDensity.current
    var componentSize by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")

    val startOffsetX by transition.animateFloat(
        initialValue = -2 * componentSize.width.toFloat(),
        targetValue = 2 * componentSize.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000)
        ),
        label = "shimmer_offset"
    )

    val animatedWidth by transition.animateFloat(
        initialValue = if (width != null) with(density) { width.toPx() } * 0.8f else 0f,
        targetValue = if (width != null) with(density) { width.toPx() } else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "width"
    )

    val animatedSize by transition.animateFloat(
        initialValue = if (size != null) with(density) { size.toPx() } * 0.8f else 0f,
        targetValue = if (size != null) with(density) { size.toPx() } else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "size"
    )

    val modifier = if (shape == CircleShape && size != null) {
        this.size(with(density) { animatedSize.toDp() })
    } else if (width != null && height != null) {
        this
            .width(with(density) { animatedWidth.toDp() })
            .height(height)
    } else {
        this
    }

    modifier
        .clip(shape)
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.LightGray.copy(alpha = 0.6f),
                    Color.LightGray.copy(alpha = 0.2f),
                    Color.LightGray.copy(alpha = 0.6f),
                ),
                start = Offset(startOffsetX, 0f),
                end = Offset(
                    startOffsetX + componentSize.width.toFloat(),
                    componentSize.height.toFloat()
                )
            )
        )
        .onGloballyPositioned {
            componentSize = it.size
        }
}

fun RoutineData.rescheduleDeadline(
    baseTime: java.time.LocalDateTime? = null,
    timeProvider: TimeProvider = SystemTimeProvider
): java.time.LocalDateTime {
    val startBase = baseTime ?: timeProvider.nowLocalDateTime()
    val count = if (routineCount <= 0) 1L else routineCount.toLong()

    return when (routine) {
        Routine.EveryMinute -> {
            startBase.plusMinutes(count)
                .withSecond(0)
                .withNano(0)
        }

        Routine.EveryHour -> {
            startBase.plusHours(count)
                .withSecond(0)
                .withNano(0)
        }

        Routine.EveryDay -> {
            startBase.plusDays(count).toMidnight()
        }

        Routine.Weekly -> {
            startBase.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
                .plusWeeks(count - 1)
                .toMidnight()
        }

        Routine.Monthly -> {
            startBase.with(TemporalAdjusters.lastDayOfMonth())
                .plusMonths(count - 1)
                .toMidnight()
        }

        Routine.Yearly -> {
            startBase.plusYears(count).toMidnight()
        }

        else -> {
            timeProvider.nowLocalDateTime().plusMinutes(1)
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
        Routine.EveryMinute -> base.plusMinutes(count)
            .withSecond(0).withNano(0)
            .toInstant()
            .toEpochMilli()

        Routine.EveryHour -> base.plusHours(count)
            .withSecond(0).withNano(0)
            .toInstant()
            .toEpochMilli()

        Routine.EveryDay -> base.plusDays(count)
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
            .toInstant()
            .toEpochMilli()

        Routine.Weekly -> base.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
            .plusWeeks(count - 1)
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
            .toInstant()
            .toEpochMilli()

        Routine.Monthly -> base.with(TemporalAdjusters.lastDayOfMonth())
            .plusMonths(count - 1)
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
            .toInstant()
            .toEpochMilli()

        Routine.Yearly -> base.plusYears(count)
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
            .toInstant()
            .toEpochMilli()

        Routine.SpecifyDayOfTheWeek -> {
            val safeCount = if (count <= 0) 1L else count
            val targetDay = DayOfWeek.of(((safeCount + 5) % 7 + 1).toInt())
            val next = if (base.dayOfWeek == targetDay) {
                base.plusWeeks(1)
            } else {
                base.with(TemporalAdjusters.next(targetDay))
            }
            next.withHour(0).withMinute(0).withSecond(0).withNano(0)
                .toInstant().toEpochMilli()
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
val List<FinanceEntity>.median: Double
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
val List<FinanceEntity>.variance: Double
    get() {
        if (isEmpty()) return 0.0
        val avg = mean { it.amount }
        // sum of (value - mean)^2 / count
        return sumOf { (it.amount - avg).let { diff -> diff * diff } } / size
    }

/**
 * Calculates the Standard Deviation: The square root of the Variance.
 */
val List<FinanceEntity>.std: Double
    get() = kotlin.math.sqrt(variance)

val FinanceEntity.isStartDateTimeNotEqualToDeadlineDateTime: Boolean
    get() = if (this is FinanceEntity.Goal) routine.startDateTime != routine.deadlineDateTime else false


/**
 * Check if the financeEntity record is for today.
 */
val FinanceEntity.isForToday: Boolean
    get() {
        val today = LocalDateTime.now().date
        val dataDate = createdAt.toLocalDateTimeUtc().date
        return today == dataDate
    }

val Settlement.isForToday: Boolean
    get() {
        val today = LocalDateTime.now().date
        val dataDate = dateTime.toLocalDateTimeUtc().date
        return today == dataDate
    }

/**
 * Check if the dataset is for yesterday.
 */
fun FinanceEntity.isCreatedAtEqualTo(localDate: LocalDate): Boolean {
    val dataDate = createdAt.toLocalDateTimeUtc().date
    return dataDate == localDate
}

fun Settlement.isCreatedAtEqualTo(localDate: LocalDate): Boolean {
    val dataDate = dateTime.toLocalDateTimeUtc().date
    return dataDate == localDate
}


fun FinanceEntity.toMap(): Map<String, Any> {
    val baseMap = mutableMapOf(
        "id" to id,
        "amount" to amount,
        "label" to label,
        "description" to description,
        "createdAt" to createdAt,
        "tagIcon" to tagIcon.tagIconToMap,
        "paymentMethod" to paymentMethod.name,
    )

    when (this) {
        is FinanceEntity.Transaction -> {
            baseMap["financeType"] = "TRANSACTION"
            baseMap["transactionType"] = transactionType.name
        }

        is FinanceEntity.Goal -> {
            baseMap["financeType"] = "GOAL"
            baseMap["routineData"] = routine.routineToMap
        }

        is FinanceEntity.Liability -> {
            baseMap["financeType"] = "LIABILITY"
            baseMap["liabilityType"] = liabilityType.name
        }
    }
    return baseMap
}

val RoutineData.routineToMap: Map<String, Any>
    get() = mapOf(
        "routine" to routine.name,
        "routineCount" to routineCount,
        "stopRoutine" to stopRoutine,
        "startDateTime" to startDateTime,
        "deadlineDateTime" to deadlineDateTime,
        "triggerMillis" to triggerMillis
    )

val Achievement.achievementToMap: Map<String, Any>
    get() = mapOf(
        "status" to status,
        "totalSettlementAmount" to totalSettlementAmount,
        "startDateTime" to startDateTime,
        "deadlineDateTime" to deadlineDateTime
    )

val Settlement.settlementToMap: Map<String, Any>
    get() = mapOf(
        "settlementId" to settlementId,
        "amount" to amount,
        "label" to label,
        "description" to description,
        "dateTime" to dateTime,
        "tagIcon" to tagIcon.tagIconToMap,
        "paymentMethod" to paymentMethod.name,
        "settlementType" to settlementType.name,
        "userId" to userId,
        "datasetId" to datasetId
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

fun Map<*, *>.asAchievement(): Achievement {
    val status = try {
        Status.valueOf(this["status"] as String)
    } catch (_: Exception) {
        Status.INITIAL
    }
    val totalSettlementAmount = (this["totalSettlementAmount"] as? Number)?.toDouble() ?: 0.0

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

    return Achievement(
        status = status.name,
        totalSettlementAmount = totalSettlementAmount,
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

fun Map<*, *>.asSettlement(): Settlement {
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

    val settlementType = when (val dt = this["settlementType"]) {
        is String -> try {
            SettlementType.valueOf(dt)
        } catch (_: Exception) {
            SettlementType.INITIAL
        }

        is Number -> SettlementType.entries.getOrNull(dt.toInt()) ?: SettlementType.INITIAL
        is Map<*, *> -> {
            val name = (dt["name"] ?: dt["value"] ?: dt["text"]) as? String
            if (name != null) try {
                SettlementType.valueOf(name)
            } catch (_: Exception) {
                SettlementType.INITIAL
            } else SettlementType.INITIAL
        }

        else -> SettlementType.INITIAL
    }

    val tagIcon = (this["tagIcon"] as? Map<*, *>)?.asTagIcon() ?: TagIcon(
        name = "",
        icon = R.drawable.circle_error
    )
    val settlementId = this["settlementId"] as? String ?: ""
    val userId = this["userId"] as? String ?: ""
    val datasetId = this["datasetId"] as? String ?: ""

    return Settlement(
        amount = amount,
        label = label,
        description = description,
        dateTime = dateTime,
        tagIcon = tagIcon,
        paymentMethod = paymentMethod,
        settlementType = settlementType,
        settlementId = settlementId,
        userId = userId,
        datasetId = datasetId
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

val FinanceEntity.text: String
    get() = financeType.text

val FinanceEntity.outlinedIcon: Int
    get() = financeType.outlinedIcon

val FinanceEntity.filledIcon: Int
    get() = financeType.filledIcon

val FinanceEntity.typeDescription: String
    get() = financeType.typeDescription

val FinanceEntity.progressPercentage: Double
    get() {
        val totalSettlement = when (this) {
            is FinanceEntity.Goal -> settlement.sumOf { it.amount }
            is FinanceEntity.Liability -> settlement.sumOf { it.amount }
            is FinanceEntity.Transaction -> 0.0
        }
        return if (amount > 0) (totalSettlement / amount) * 100.0 else 0.0
    }

val FinanceEntity.status: Status
    get() {
        val currentTime = LocalDateTime.now()
        val totalSettlement = when (this) {
            is FinanceEntity.Goal -> settlement.sumOf { it.amount }
            is FinanceEntity.Liability -> settlement.sumOf { it.amount }
            is FinanceEntity.Transaction -> 0.0
        }
        val isAchieved = totalSettlement >= amount

        return when (this) {
            is FinanceEntity.Goal -> {
                val deadlineDateTime = routine.deadlineDateTime.toLocalDateTimeUtc()
                when {
                    isAchieved -> Status.COMPLETED
                    currentTime >= deadlineDateTime -> Status.OVERDUE
                    else -> Status.ACTIVE
                }
            }

            is FinanceEntity.Liability -> {
                if (remainingAmount <= 0.0) {
                    if (liabilityType == LiabilityType.DEBT) Status.PAYBACK else Status.REFUNDED
                } else Status.INITIAL
            }

            is FinanceEntity.Transaction -> Status.INITIAL
        }
    }

fun Map<*, *>.toAmount(): Double {
    return (this["amount"] as? Number)?.toDouble()
        ?: (this["amount"] as? String)?.toDoubleOrNull()
        ?: 0.0
}

fun Map<*, *>.toFinance(): FinanceEntity {

    fun <T : Enum<T>> parseEnum(
        value: Any?,
        values: Array<T>,
        fallback: T
    ): T = when (value) {
        is String -> values.firstOrNull { it.name == value } ?: fallback
        is Number -> values.getOrNull(value.toInt()) ?: fallback
        else -> fallback
    }

    val id = this["id"] as? String ?: ""
    val amount = this.toAmount()
    val label = this["label"] as? String ?: ""
    val description = this["description"] as? String ?: ""
    val createdAt = parseTimestamp(this["createdAt"])
    val tagIcon = (this["tagIcon"] as? Map<*, *>)?.asTagIcon() ?: TagIcon(
        name = "",
        icon = R.drawable.initial
    )
    val paymentMethod = parseEnum(
        this["paymentMethod"],
        PaymentMethod.entries.toTypedArray(),
        PaymentMethod.CASH
    )

    // Check for new financeType first, fallback to old dataType
    val financeTypeStr = this["financeType"] as? String
    val dataTypeStr = this["dataType"] as? String

    return when {
        financeTypeStr == "TRANSACTION" || (dataTypeStr in listOf(
            "EARNINGS",
            "EXPENSE",
            "SAVINGS"
        )) -> {
            val transactionType = if (financeTypeStr == "TRANSACTION") {
                parseEnum(
                    this["transactionType"],
                    TransactionType.entries.toTypedArray(),
                    TransactionType.EARNINGS
                )
            } else {
                when (dataTypeStr) {
                    "EXPENSE" -> TransactionType.EXPENSES
                    "SAVINGS" -> TransactionType.SAVINGS
                    else -> TransactionType.EARNINGS
                }
            }
            FinanceEntity.Transaction(
                id = id,
                transactionType = transactionType,
                amount = amount,
                label = label,
                description = description,
                createdAt = createdAt,
                tagIcon = tagIcon,
                paymentMethod = paymentMethod
            )
        }

        financeTypeStr == "GOAL" || dataTypeStr == "GOAL" -> {
            FinanceEntity.Goal(
                id = id,
                amount = amount,
                label = label,
                description = description,
                createdAt = createdAt,
                tagIcon = tagIcon,
                paymentMethod = paymentMethod,
                settlement = this.toSettlement(),
                routine = (this["routineData"] as? Map<*, *>)?.asRoutineData() ?: RoutineData()
            )
        }

        financeTypeStr == "LIABILITY" || (dataTypeStr in listOf("DEBT", "LENT")) -> {
            val liabilityType = if (financeTypeStr == "LIABILITY") {
                parseEnum(
                    this["liabilityType"],
                    LiabilityType.entries.toTypedArray(),
                    LiabilityType.LOAN
                )
            } else {
                if (dataTypeStr == "DEBT") LiabilityType.DEBT else LiabilityType.LOAN
            }
            FinanceEntity.Liability(
                id = id,
                liabilityType = liabilityType,
                amount = amount,
                label = label,
                description = description,
                createdAt = createdAt,
                tagIcon = tagIcon,
                paymentMethod = paymentMethod,
                settlement = this.toSettlement()
            )
        }

        else -> FinanceEntity.Transaction(
            id = id,
            amount = amount,
            label = label,
            description = description,
            createdAt = createdAt,
            tagIcon = tagIcon,
            paymentMethod = paymentMethod
        )
    }
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

fun Map<*, *>.toSettlement(): List<Settlement> {
    return (this["settlement"] as? List<*>)
        ?.mapNotNull { (it as? Map<*, *>)?.asSettlement() }
        ?: emptyList()
}

fun Map<*, *>.toAchievement(): List<Achievement> {
    val statusList = this["achievement"] as? List<*>
    return statusList?.mapNotNull {
        (it as? Map<*, *>)?.asAchievement()
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
fun Timestamp.toEpochMilli(): Long =
    this.seconds * 1000L + (this.nanoseconds / 1_000_000L)


fun FinanceEntity.isAmountEqualToSettleAmount(): Boolean {
    val totalSettlement = when (this) {
        is FinanceEntity.Goal -> settlement.sumOf { it.amount }
        is FinanceEntity.Liability -> settlement.sumOf { it.amount }
        is FinanceEntity.Transaction -> 0.0
    }
    return totalSettlement >= amount
}

val FinanceEntity.remainingAmount: Double
    get() {
        val totalSettlement = when (this) {
            is FinanceEntity.Goal -> settlement.sumOf { it.amount }
            is FinanceEntity.Liability -> settlement.sumOf { it.amount }
            is FinanceEntity.Transaction -> 0.0
        }
        return amount - totalSettlement
    }

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
    return this.toDouble().formatToAmount()
}


fun Float.formatToAmount(): String {
    return this.toDouble().formatToAmount()
}

fun Double.formatToAmount(): String {
    val locale = Locale.getDefault()
    val numberFormat = NumberFormat.getCurrencyInstance(locale)
    val symbol = numberFormat.currency?.symbol ?: "$"

    val absValue = kotlin.math.abs(this)
    val sign = if (this < 0) "-" else ""

    if (absValue < 1_000_000) {
        val rounding = BigDecimal(this).setScale(2, RoundingMode.HALF_UP)

        val formattedAmount = rounding.abs().toString()
            .replace(Regex("\\B(?=(\\d{3})+(?!\\d))"), ",")
            .replace(Regex("\\.00$"), "")
        return "$sign$symbol$formattedAmount"
    }

    val suffixes = charArrayOf('M', 'B', 'T', 'Q')
    val formatter = DecimalFormat("#.##") // Using 2 decimals for precision
    val base = (log10(absValue) / 3).toInt()
    val scaledNumber = absValue / 1000.0.pow(base.toDouble())

    val suffixIndex = base - 2
    return if (suffixIndex >= 0 && suffixIndex < suffixes.size) {
        "$sign$symbol${formatter.format(scaledNumber)}${suffixes[suffixIndex]}"
    } else {
        // Fallback for extremely large numbers or unexpected base
        "$sign$symbol${String.format(Locale.US, "%.2f", absValue)}"
    }
}

infix fun Int.formatToTime(minutes: Int): String = String.format(
    Locale.getDefault(),
    "%02d:%02d",
    this, minutes
)

val LocalDateTime.formatedDateTime: String
    get() {
        val day = day.addZeroIfLessThenTen
        val month = this.month.name.take(3).title
        val year = this.year
        val hour = this.hour.addZeroIfLessThenTen
        val minute = this.minute.addZeroIfLessThenTen
        return "$day $month $year, $hour:$minute"
    }

val LocalDateTime.formattedDate: String
    get() {
        val day = day.addZeroIfLessThenTen
        val month = this.month.name.take(3).title
        val year = this.year
        return "$day $month $year"
    }

val LocalDateTime.formattedTime: String
    get() {
        val hour = this.hour.addZeroIfLessThenTen
        val minute = this.minute.addZeroIfLessThenTen
        return "$hour:$minute"
    }