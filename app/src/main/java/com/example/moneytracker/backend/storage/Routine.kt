/**
 * Glory be the name of LORD our GOD
 */
package com.example.moneytracker.backend.storage

import androidx.annotation.Keep

@Keep
enum class Routine(
    val text: String,
) {
    EveryHour("Every hour"),
    EveryDay("Every day"),
    Weekly("Weekly"),
    Monthly("Monthly"),
    Yearly("Yearly"),
    SpecifyDayOfTheWeek("Day of the week"),
    Nothing("Nothing")
}