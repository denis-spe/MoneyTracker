package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class PaymentMethod(val text: String, val icon: Int) {
    CASH(text = "Cash", icon = R.drawable.cash),
    BANK_TRANSFER(text = "Bank Transfer", icon = R.drawable.money_transfer),
    CREDIT_CARD(text = "Credit Card", icon = R.drawable.credit_card),
}