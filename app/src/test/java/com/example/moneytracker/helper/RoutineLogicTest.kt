package com.example.moneytracker.helper

import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RoutineLogicTest {

    private val zone = ZoneId.systemDefault()

    private fun toMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        return LocalDateTime.of(year, month, day, hour, minute)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
    }

    private fun format(millis: Long): String {
        return ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(millis), zone)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    }

    @Test
    fun testEveryDay() {
        // Goal set at 10:00 AM on Jan 15th
        val base = toMillis(2024, 1, 15, 10, 0)
        val routine = RoutineData(routine = Routine.EveryDay, routineCount = 1)

        val nextTrigger = routine.getTriggerMillisFrom(base)

        // Expected: Jan 16th at 00:00:00
        val expected = toMillis(2024, 1, 16, 0, 0)

        println("EveryDay Test:")
        println("Base:     ${format(base)}")
        println("Expected: ${format(expected)}")
        println("Actual:   ${format(nextTrigger)}")

        assertEquals(expected, nextTrigger)
    }

    @Test
    fun testEveryMinute() {
        val base = toMillis(2024, 1, 15, 10, 0)
        val routine = RoutineData(routine = Routine.EveryMinute, routineCount = 1)

        val nextTrigger = routine.getTriggerMillisFrom(base)
        val expected = toMillis(2024, 1, 15, 10, 1)

        assertEquals(expected, nextTrigger)
    }

    @Test
    fun testEveryHour() {
        val base = toMillis(2024, 1, 15, 10, 30)
        val routine = RoutineData(routine = Routine.EveryHour, routineCount = 1)

        val nextTrigger = routine.getTriggerMillisFrom(base)
        val expected = toMillis(2024, 1, 15, 11, 30)
        assertEquals(expected, nextTrigger)
    }

    @Test
    fun testEveryHour_NextPeriod() {
        // Current: 10:30, Expected: 11:00 (Start of next hour)
        val base = toMillis(2024, 1, 15, 10, 30)
        val routine = RoutineData(routine = Routine.EveryHour, routineCount = 1)

        val zdt = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(base), zone)
        val nextTrigger =
            zdt.plusHours(1).withMinute(0).withSecond(0).withNano(0).toInstant().toEpochMilli()

        println("EveryHour (Targeting 11:00): ${format(nextTrigger)}")
    }

    @Test
    fun testWeekly() {
        // Saturday, Jan 13th 2024
        val base = toMillis(2024, 1, 13, 10, 0)
        val routine = RoutineData(routine = Routine.Weekly, routineCount = 1)

        val nextTrigger = routine.getTriggerMillisFrom(base)
        // Code: base.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).plusWeeks(count - 1).toMidnight()

        // Expected: Jan 20th (next week) if Jan 13th is already Saturday?
        // Actually, nextOrSame(SATURDAY) on a SATURDAY returns SAME day.
        // So expected is Jan 13th 00:00:00 (which is in the PAST relative to 10:00 AM).
        val expected = toMillis(2024, 1, 13, 0, 0)

        println("Weekly Test:")
        println("Base:     ${format(base)}")
        println("Expected: ${format(expected)}")
        println("Actual:   ${format(nextTrigger)}")

        assertEquals(expected, nextTrigger)
    }

    @Test
    fun testMonthly() {
        // base: Jan 15th
        val base = toMillis(2024, 1, 15, 10, 0)
        val routine = RoutineData(routine = Routine.Monthly, routineCount = 1)

        val nextTrigger = routine.getTriggerMillisFrom(base)
        // Code: base.with(TemporalAdjusters.lastDayOfMonth()).plusMonths(count - 1).toMidnight()

        val expected = toMillis(2024, 1, 31, 0, 0)

        println("Monthly Test:")
        println("Base:     ${format(base)}")
        println("Expected: ${format(expected)}")
        println("Actual:   ${format(nextTrigger)}")

        assertEquals(expected, nextTrigger)
    }
}
