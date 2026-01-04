// Great is the LORD of hosts
package com.example.moneytracker.helper

import com.example.moneytracker.backend.storage.Dataset
import java.time.LocalDate
import java.time.ZoneId

/**
 * Formats a double to a string with two decimal places.
 */
val Int.addZeroIfLessThenTen: String
    get() = if (this < 10) "0$this" else this.toString()

/**
 * Formats a double to a string with two decimal places.
 */
val String.title: String
    get() = lowercase().mapIndexedNotNull { index, c ->
        if (index == 0) c.uppercase() else c.lowercase()
    }.joinToString("")

/**
 * Calculates the Mean: The average of all values in a list.
 */
val List<Dataset>.mean: Double
    get() {
        if (isEmpty()) return 0.0
        return sumOf { it.amount } / size
    }

/**
 * Calculates the Median: The middle value in a sorted list of numbers.
 */
val List<Dataset>.median: Double
    get() {
        if (isEmpty()) return 0.0

        // 1. Sort the list by amount
        val sortedList = sortedBy { it.amount }
        val middle = size / 2

        return if (size % 2 == 0) {
            // 2. If even, median is the average of the two middle numbers
            (sortedList[middle - 1].amount + sortedList[middle].amount) / 2.0
        } else {
            // 3. If odd, median is the exact middle number
            sortedList[middle].amount
        }
    }

/**
 * Calculates the Variance: The average of the squared differences from the Mean.
 */
val List<Dataset>.variance: Double
    get() {
        if (isEmpty()) return 0.0
        val avg = mean
        // sum of (value - mean)^2 / count
        return sumOf { (it.amount - avg).let { diff -> diff * diff } } / size
    }

/**
 * Calculates the Standard Deviation: The square root of the Variance.
 */
val List<Dataset>.std: Double
    get() = kotlin.math.sqrt(variance)

/**
 * Check if the dataset is for today.
 */
val Dataset.isForToday: Boolean
    get() {
        val today = LocalDate.now(ZoneId.systemDefault())
        val dataDate = dateTime.toDate().toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return dataDate == today
    }

val Dataset.subtractedRepay: Double
    get() = amount - repay.sumOf { it.amount }



