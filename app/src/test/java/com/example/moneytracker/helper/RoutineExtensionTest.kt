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
        val base = System.currentTimeMillis()
        val trigger = routineData.getTriggerMillisFrom(base)

        val diff = trigger - base
        val oneHour = TimeUnit.HOURS.toMillis(1)

        assertEquals(oneHour, diff)
    }

    @Test
    fun `getTriggerMillisFrom for EveryDay returns midnight of next day`() {
        val routineData = RoutineData(routine = Routine.EveryDay, routineCount = 1)
        // Set base to 10:00 AM today
        val base = ZonedDateTime.now(ZoneId.systemDefault())
            .withHour(10).withMinute(0).withSecond(0).withNano(0)
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
        // Set base to 10:00 AM today
        val base = ZonedDateTime.now(ZoneId.systemDefault())
            .withHour(10).withMinute(0).withSecond(0).withNano(0)
            .toInstant().toEpochMilli()
            
        val trigger = routineData.getTriggerMillisFrom(base)
        val triggerDate =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(trigger), ZoneId.systemDefault())

        assertEquals(0, triggerDate.hour)
        assertEquals(0, triggerDate.minute)

        // Should be roughly 7 days away, minus the 10 hours offset
        val diff = trigger - base
        val expectedDiff = TimeUnit.DAYS.toMillis(7) - TimeUnit.HOURS.toMillis(10)
        assertEquals(expectedDiff, diff)
    }

    @Test
    fun `getTriggerMillisFrom handles routineCount of zero by defaulting to one and returning midnight`() {
        val routineData = RoutineData(routine = Routine.EveryDay, routineCount = 0)
        // Set base to 10:00 AM today
        val base = ZonedDateTime.now(ZoneId.systemDefault())
            .withHour(10).withMinute(0).withSecond(0).withNano(0)
            .toInstant().toEpochMilli()
            
        val trigger = routineData.getTriggerMillisFrom(base)
        val triggerDate =
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(trigger), ZoneId.systemDefault())

        assertEquals(0, triggerDate.hour)
        val diff = trigger - base
        assertEquals(TimeUnit.HOURS.toMillis(14), diff)
    }
}
