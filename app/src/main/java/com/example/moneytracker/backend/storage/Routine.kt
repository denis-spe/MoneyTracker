/**
 * Glory be the name of LORD our GOD
 */
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep

@Keep
enum class Routine(
    val text: String,
) {

    EveryMinute("every minutes"),
    EveryHour("every hours"),
    EveryDay("daily"),
    Weekly("every weeks"),
    Monthly("every months"),
    Yearly("every years"),
    SpecifyDayOfTheYear("day of the year"),
    SpecificDayOfTheWeek("Specific days"),
    Nothing("Nothing");
}