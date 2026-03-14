package com.example.moneytracker.backend.alarmManager

import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

data class AlarmItem(
    val datasetId: String,
    val userId: String,
    val routineData: RoutineData,
) {
    fun triggerMillis(): Long {
        val nowMillis = System.currentTimeMillis()
        val now = ZonedDateTime.ofInstant(Instant.ofEpochMilli(nowMillis), ZoneId.systemDefault())


        return when (routineData.routine) {
            Routine.EveryHour -> now.plusMinutes(routineData.routineCount.toLong()).toInstant()
                .toEpochMilli()

            Routine.EveryDay -> now.plusDays(routineData.routineCount.toLong()).toInstant()
                .toEpochMilli()

            Routine.Weekly -> now.plusWeeks(routineData.routineCount.toLong()).toInstant()
                .toEpochMilli()

            Routine.Monthly -> now.plusMonths(routineData.routineCount.toLong()).toInstant()
                .toEpochMilli()

            Routine.Yearly -> now.plusYears(routineData.routineCount.toLong()).toInstant()
                .toEpochMilli()

            Routine.SpecifyDayOfTheWeek -> {
                val targetDay = DayOfWeek.of((routineData.routineCount % 7) + 1)
                val next = if (now.dayOfWeek == targetDay) {
                    now.plusWeeks(1)
                } else {
                    now.with(TemporalAdjusters.next(targetDay))
                }
                next.toInstant().toEpochMilli()
            }

            else -> nowMillis + 86_400_000
        }
    }
}
