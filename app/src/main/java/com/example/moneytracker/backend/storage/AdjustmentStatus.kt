package com.example.moneytracker.backend.storage

enum class AdjustmentStatus(val text: String) {
    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed")
}
