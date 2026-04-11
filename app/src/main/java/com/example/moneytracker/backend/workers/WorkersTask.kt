package com.example.moneytracker.backend.workers

import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.helper.toLocalDateTimeUtc
import com.google.firebase.Timestamp
import kotlinx.datetime.toJavaLocalDateTime
import java.time.Duration
import java.time.Month
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

data class WorkersTask(
    val userId: String,
    val datasetId: String,
    val deadlineDateTime: Timestamp,
    val routineData: RoutineData
) {

    val calculateDelay: Long
        get() {
            val now = ZonedDateTime.now()
            val zone = now.zone
            val scheduledDateTime = deadlineDateTime.toLocalDateTimeUtc()
                .toJavaLocalDateTime()
                .atZone(zone)

            val target: ZonedDateTime = when (routineData.routine) {

                Routine.EveryMinute -> {
                    now.plusMinutes(routineData.routineCount.toLong())
                        .withSecond(0)
                        .withNano(0)
                }

                Routine.EveryHour -> {
                    now.plusHours(routineData.routineCount.toLong())
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0)
                }

                Routine.EveryDay -> {
                    var next = now
                        .withHour(scheduledDateTime.hour)
                        .withMinute(scheduledDateTime.minute)
                        .withSecond(0)
                        .withNano(0)

                    if (next.isBefore(now)) {
                        next = next.plusDays(1)
                    }
                    next
                }

                Routine.Weekly -> {
                    val targetDay = scheduledDateTime.dayOfWeek

                    var next = now
                        .with(TemporalAdjusters.nextOrSame(targetDay))
                        .withHour(scheduledDateTime.hour)
                        .withMinute(scheduledDateTime.minute)
                        .withSecond(0)
                        .withNano(0)

                    if (next.isBefore(now)) {
                        next = next.plusWeeks(1)
                    }
                    next
                }

                Routine.Monthly -> {
                    var next = now
                        .withDayOfMonth(1)
                        .withHour(scheduledDateTime.hour)
                        .withMinute(scheduledDateTime.minute)
                        .withSecond(0)
                        .withNano(0)

                    if (next.isBefore(now)) {
                        next = next.plusMonths(1)
                    }
                    next
                }

                Routine.Yearly -> {
                    var next = now
                        .withMonth(Month.JANUARY.value)
                        .withDayOfMonth(1)
                        .withHour(scheduledDateTime.hour)
                        .withMinute(scheduledDateTime.minute)
                        .withSecond(0)
                        .withNano(0)

                    if (next.isBefore(now)) {
                        next = next.plusYears(1)
                    }
                    next
                }

                else -> {
                    now.plusMinutes(1)
                        .withSecond(0)
                        .withNano(0)
                }
            }

            return Duration.between(now, target).toMillis()
        }
}
