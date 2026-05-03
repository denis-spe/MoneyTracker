package com.example.moneytracker.helper

import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import com.example.moneytracker.backend.workers.WorkersTask
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class RoutineFrequencyTest {

    class MockTimeProvider(private val mockNow: LocalDateTime) : TimeProvider {
        override fun nowMillis(): Long =
            mockNow.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        override fun nowZonedDateTime(zone: ZoneId): ZonedDateTime = mockNow.atZone(zone)
        override fun nowLocalDateTime(): LocalDateTime = mockNow
    }

    @Test
    fun `calculateDelay returns 0 if deadline is in the past`() {
        val now = LocalDateTime.of(2024, 5, 1, 12, 0)
        val deadlineTime = LocalDateTime.of(2024, 5, 1, 11, 0)
        val deadline = Timestamp(deadlineTime.atZone(ZoneId.systemDefault()).toEpochSecond(), 0)

        val timeProvider = MockTimeProvider(now)
        val task = WorkersTask("user", "data", "GOAL", deadline, RoutineData(), timeProvider)

        assertEquals(0L, task.calculateDelay)
    }

    @Test
    fun `calculateDelay returns correct positive delay if deadline is in the future`() {
        val now = LocalDateTime.of(2024, 5, 1, 12, 0)
        val deadlineTime = LocalDateTime.of(2024, 5, 1, 13, 0)
        val deadline = Timestamp(deadlineTime.atZone(ZoneId.systemDefault()).toEpochSecond(), 0)

        val timeProvider = MockTimeProvider(now)
        val task = WorkersTask("user", "data", "GOAL", deadline, RoutineData(), timeProvider)

        assertEquals(TimeUnit.HOURS.toMillis(1), task.calculateDelay)
    }

    @Test
    fun `rescheduleDeadline for EveryDay returns midnight of tomorrow`() {
        val now = LocalDateTime.of(2024, 5, 1, 10, 0)
        val timeProvider = MockTimeProvider(now)
        val routineData = RoutineData(routine = Routine.EveryDay, routineCount = 1)

        val next = routineData.rescheduleDeadline(timeProvider = timeProvider)
        val expected = LocalDateTime.of(2024, 5, 2, 0, 0)
        assertEquals(expected, next)
    }

    @Test
    fun `rescheduleDeadline for EveryHour with baseTime avoids drift`() {
        // Supposed deadline was 12:10
        val baseTime = LocalDateTime.of(2024, 5, 1, 12, 10)
        // Worker actually triggers at 12:12
        val now = LocalDateTime.of(2024, 5, 1, 12, 12)
        val timeProvider = MockTimeProvider(now)
        val routineData = RoutineData(routine = Routine.EveryHour, routineCount = 1)

        val next = routineData.rescheduleDeadline(baseTime = baseTime, timeProvider = timeProvider)
        val expected = LocalDateTime.of(2024, 5, 1, 13, 10)
        assertEquals(expected, next)
    }

    @Test
    fun `SpecifyDayOfTheWeek mapping for Sunday (1) works correctly and returns midnight`() {
        // May 1st 2024 is Wednesday
        val now = LocalDateTime.of(2024, 5, 1, 10, 0)
        val baseMillis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val routineData =
            RoutineData(routine = Routine.SpecifyDayOfTheWeek, routineCount = 1) // 1 = Sunday
        val triggerMillis = routineData.getTriggerMillisFrom(baseMillis)

        val triggerDate = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(triggerMillis),
            ZoneId.systemDefault()
        )
        // Next Sunday should be May 5th at 00:00
        assertEquals(5, triggerDate.dayOfMonth)
        assertEquals(java.time.DayOfWeek.SUNDAY, triggerDate.dayOfWeek)
        assertEquals(0, triggerDate.hour)
        assertEquals(0, triggerDate.minute)
    }

    @Test
    fun `rescheduleDeadline for EveryMinute repeats correctly and zeros seconds`() {
        val now = LocalDateTime.of(2024, 5, 1, 10, 30, 45)
        val timeProvider = MockTimeProvider(now)
        val routineData = RoutineData(routine = Routine.EveryMinute, routineCount = 3)

        val next = routineData.rescheduleDeadline(timeProvider = timeProvider)
        val expected = LocalDateTime.of(2024, 5, 1, 10, 33, 0)
        assertEquals(expected, next)
    }

    @Test
    fun `rescheduleDeadline for EveryHour repeats correctly and zeros seconds`() {
        val now = LocalDateTime.of(2024, 5, 1, 10, 30, 45)
        val timeProvider = MockTimeProvider(now)
        val routineData = RoutineData(routine = Routine.EveryHour, routineCount = 1)

        val next = routineData.rescheduleDeadline(timeProvider = timeProvider)
        val expected = LocalDateTime.of(2024, 5, 1, 11, 30, 0)
        assertEquals(expected, next)
    }

    @Test
    fun `rescheduleDeadline for Weekly repeats correctly from current week`() {
        // Wednesday, May 1st
        val now = LocalDateTime.of(2024, 5, 1, 10, 0)
        val timeProvider = MockTimeProvider(now)
        val routineData = RoutineData(routine = Routine.Weekly, routineCount = 1)

        val next = routineData.rescheduleDeadline(timeProvider = timeProvider)
        // Next Saturday, May 4th at 00:00 (since May 1st is Wednesday)
        val expected = LocalDateTime.of(2024, 5, 4, 0, 0)
        assertEquals(expected, next)
    }

    @Test
    fun `rescheduleDeadline for Monthly repeats correctly`() {
        val now = LocalDateTime.of(2024, 5, 1, 10, 0)
        val timeProvider = MockTimeProvider(now)
        val routineData = RoutineData(routine = Routine.Monthly, routineCount = 1)

        val next = routineData.rescheduleDeadline(timeProvider = timeProvider)
        // Last day of May: May 31st at 00:00
        val expected = LocalDateTime.of(2024, 5, 31, 0, 0)
        assertEquals(expected, next)
    }

    @Test
    fun `rescheduleDeadline for Yearly repeats correctly`() {
        val now = LocalDateTime.of(2024, 5, 1, 10, 0)
        val timeProvider = MockTimeProvider(now)
        val routineData = RoutineData(routine = Routine.Yearly, routineCount = 1)

        val next = routineData.rescheduleDeadline(timeProvider = timeProvider)
        val expected = LocalDateTime.of(2025, 5, 1, 0, 0)
        assertEquals(expected, next)
    }
}
