/**
 * Glory be the name of LORD our GOD
 */
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep

@Keep
enum class Routine(
    val text: String,
) {

    EveryMinute("minutes"),
    EveryHour("hours"),
    EveryDay("days"),
    Weekly("weeks"),
    Monthly("months"),
    Yearly("years"),
    SpecifyDayOfTheYear("day of the year"),
    SpecificDayOfTheWeek("Specific days"),
    Nothing("Nothing");
}