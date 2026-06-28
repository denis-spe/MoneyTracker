// Glory be to the name of LORD our GOD
package com.example.moneytracker.helper

import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class RoutineExtensionTest {

    @Test
    fun `getTriggerMillisFrom for EveryHour returns exactly 1 hour from base`() {
        val routineData = RoutineData(routine = Routine.EveryHour, routineCount = 1)
        // Fixed base time
        val base = ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.systemDefault())
            .toInstant().toEpochMilli()
        val trigger = routineData.getTriggerMillisFrom(base)

        val diff = trigger - base
        val oneHour = TimeUnit.HOURS.toMillis(1)

        assertEquals(oneHour, diff)
    }

    @Test
    fun `getTriggerMillisFrom for EveryDay returns midnight of next day`() {
        val routineData = RoutineData(routine = Routine.EveryDay, routineCount = 1)
        // Set base to 10:00 AM today
        val base = ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.systemDefault())
            .toInstant().toEpochMilli()
        
        val trigger = routineData.getTriggerMillisFrom(base)
        val triggerDate =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(trigger), ZoneId.systemDefault())

        assertEquals(0, triggerDate.hour)
        assertEquals(0, triggerDate.minute)
        assertEquals(0, triggerDate.second)

        // Difference should be 14 hours (24 - 10)
        val diff = trigger - base
        assertEquals(TimeUnit.HOURS.toMillis(14), diff)
    }

    @Test
    fun `getTriggerMillisFrom for Weekly returns midnight of next week`() {
        val routineData = RoutineData(routine = Routine.Weekly, routineCount = 1)
        // Set base to Monday (1) 10:00 AM
        val base = ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.systemDefault())
            .toInstant().toEpochMilli()
            
        val trigger = routineData.getTriggerMillisFrom(base)
        val triggerDate =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(trigger), ZoneId.systemDefault())

        assertEquals(0, triggerDate.hour)
        assertEquals(0, triggerDate.minute)

        // Should be next Saturday since Routine.Weekly uses TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)
        // 2024-01-01 is Monday. Saturday is 2024-01-06.
        assertEquals(2024, triggerDate.year)
        assertEquals(1, triggerDate.monthValue)
        assertEquals(6, triggerDate.dayOfMonth)
    }

    @Test
    fun `getTriggerMillisFrom handles routineCount of zero by defaulting to one and returning midnight`() {
        val routineData = RoutineData(routine = Routine.EveryDay, routineCount = 0)
        // Set base to 10:00 AM today
        val base = ZonedDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneId.systemDefault())
            .toInstant().toEpochMilli()
            
        val trigger = routineData.getTriggerMillisFrom(base)
        val triggerDate =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(trigger), ZoneId.systemDefault())

        assertEquals(0, triggerDate.hour)
        val diff = trigger - base
        assertEquals(TimeUnit.HOURS.toMillis(14), diff)
    }

    @Test
    fun `getTriggerMillisFrom for SpecificDays returns next selected day`() {
        // Monday (1), Wednesday (3), Friday (5)
        val specificDays = listOf(1, 3, 5)
        val routineData =
            RoutineData(routine = Routine.SpecificDayOfTheWeek, specificDays = specificDays)

        // Base is Tuesday (2) 10:00 AM
        val base = ZonedDateTime.of(2024, 1, 16, 10, 0, 0, 0, ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        val trigger = routineData.getTriggerMillisFrom(base)
        val triggerDate =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(trigger), ZoneId.systemDefault())

        // Should be Wednesday (17th) at 00:00:00
        assertEquals(3, triggerDate.dayOfWeek.value)
        assertEquals(17, triggerDate.dayOfMonth)
        assertEquals(0, triggerDate.hour)
    }

    @Test
    fun `getTriggerMillisFrom for SpecificDays returns first day of next week if all selected days passed`() {
        // Monday (1)
        val specificDays = listOf(1)
        val routineData =
            RoutineData(routine = Routine.SpecificDayOfTheWeek, specificDays = specificDays)

        // Base is Tuesday (2) 10:00 AM
        val base = ZonedDateTime.of(2024, 1, 16, 10, 0, 0, 0, ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        val trigger = routineData.getTriggerMillisFrom(base)
        val triggerDate =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(trigger), ZoneId.systemDefault())

        // Should be next Monday (22nd) at 00:00:00
        assertEquals(1, triggerDate.dayOfWeek.value)
        assertEquals(22, triggerDate.dayOfMonth)
    }
}
