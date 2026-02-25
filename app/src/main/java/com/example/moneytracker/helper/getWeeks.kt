// Bless be the name LORD of hosts.
package com.example.moneytracker.helper

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
fun getWeeks(
    anchorDate: LocalDate,
    weeksBefore: Int,
    weeksAfter: Int
): List<List<LocalDate>> {
    val weeks = mutableListOf<List<LocalDate>>()

    // 1. Find the start of the week for the anchor date
    val anchorWeekStart = anchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

    // 2. Move the pointer back by the number of 'weeksBefore'
    var currentStart = anchorWeekStart.minusWeeks(weeksBefore.toLong())

    // 3. Loop through total count (Before + Current + After)
    val totalWeeks = weeksBefore + 1 + weeksAfter

    repeat(totalWeeks) {
        val week = (0..6).map { currentStart.plusDays(it.toLong()) }
        weeks.add(week)
        currentStart = currentStart.plusWeeks(1)
    }

    return weeks
}