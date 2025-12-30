package com.example.moneytracker.backend.storage

import com.example.moneytracker.R

enum class DataType(val text: String, val color: Int) {
    EARNINGS(text = "Earnings", color = R.color.Earnings),
    EXPENSE(text = "Expense", color = R.color.Expense),
    LENT(text = "Lent", color = R.color.Lent),
    DEBT(text = "Debt", color = R.color.Debt),
    SAVINGS(text = "Savings", color = R.color.Savings),
    REPAY(text = "Repay", color = R.color.Repay)
}