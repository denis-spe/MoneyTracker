package com.example.moneytracker.ui.homeScreen

import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType

data class SortingState(
    val time: SortType,
    val category: String,
    val payment: PaymentMethod?,
    val alphabetical: SortType,
    val amount: SortType
)