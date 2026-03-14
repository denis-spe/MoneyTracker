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
    Nothing("Nothing");

    fun toMilliseconds(): Long {
        return when (this) {
            EveryHour -> 3600000L
            EveryDay -> 86400000L
            Weekly -> 604800000L
            Monthly -> 2592000000L // Approximation: 30 days
            Yearly -> 31536000000L // Approximation: 365 days
            SpecifyDayOfTheWeek -> 604800000L // Typically treated as weekly
            Nothing -> 0L
        }
    }
}