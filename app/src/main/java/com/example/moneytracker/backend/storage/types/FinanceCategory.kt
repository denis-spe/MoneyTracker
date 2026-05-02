package com.example.moneytracker.backend.storage.types

sealed interface FinanceCategory {
    val text: String
    val color: Int
    val outlinedIcon: Int
    val filledIcon: Int
    val tagIconRes: Int
    val typeDescription: String
}