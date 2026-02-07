package com.example.moneytracker.backend.storage

import androidx.annotation.Keep

@Keep
data class TagIcon(
    val name: String = "",
    val icon: Int = 0
)
