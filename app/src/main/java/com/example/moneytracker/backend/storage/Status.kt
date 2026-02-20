package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class Status(val text: String, val color: Int) {
    PENDING("Pending", R.color.teal_200),
    COMPLETED("Completed", R.color.teal_700),
    FAILED("Failed", R.color.error_color),

    SUCCESS("Success", R.color.Earnings),
    INITIAL("Initial", R.color.white),
    OVERDUE("Overdue", R.color.error_color)
}
