package com.example.moneytracker.ui.homeScreen.yesterdayScreen.statArea

import androidx.compose.runtime.Stable

@Stable
data class YesterdayStats(
    val earnings: Double = 0.0,
    val expenses: Double = 0.0,
    val debts: Double = 0.0,
    val lent: Double = 0.0,
    val payback: Double = 0.0,
    val refund: Double = 0.0,
    val savings: Double = 0.0,
    val reminder: Double = 0.0
)