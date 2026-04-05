// Glory be to the name of LORD our GOD
package com.example.moneytracker.helper

import com.example.moneytracker.backend.storage.Routine
import com.example.moneytracker.backend.storage.RoutineData
import org.junit.Assert.assertEquals
import org.junit.Test
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
    fun `getTriggerMillisFrom for EveryDay returns exactly 24 hours from base`() {
        val routineData = RoutineData(routine = Routine.EveryDay, routineCount = 1)
        val base = System.currentTimeMillis()
        val trigger = routineData.getTriggerMillisFrom(base)

        val diff = trigger - base
        val twentyFourHours = TimeUnit.DAYS.toMillis(1)

        assertEquals(twentyFourHours, diff)
    }

    @Test
    fun `getTriggerMillisFrom for Weekly returns exactly 7 days from base`() {
        val routineData = RoutineData(routine = Routine.Weekly, routineCount = 1)
        val base = System.currentTimeMillis()
        val trigger = routineData.getTriggerMillisFrom(base)

        val diff = trigger - base
        val sevenDays = TimeUnit.DAYS.toMillis(7)

        assertEquals(sevenDays, diff)
    }

    @Test
    fun `getTriggerMillisFrom handles routineCount of zero by defaulting to one`() {
        val routineData = RoutineData(routine = Routine.EveryDay, routineCount = 0)
        val base = System.currentTimeMillis()
        val trigger = routineData.getTriggerMillisFrom(base)

        val diff = trigger - base
        val twentyFourHours = TimeUnit.DAYS.toMillis(1)

        assertEquals(twentyFourHours, diff)
    }
}
