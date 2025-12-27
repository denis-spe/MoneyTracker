package com.example.moneytracker.backend.storage

enum class DataType(val text: String) {
    EARNINGS(text = "Earnings"),
    EXPENSE(text = "Expense"),
    LENT(text = "Lent"),
    DEBT(text = "Debt"),
    SAVINGS(text = "Savings");

}