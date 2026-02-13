package com.example.moneytracker.backend.storage

import androidx.annotation.Keep

@Keep
enum class Status(val text: String) {
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed"),

    SUCCESS("Success")
}
