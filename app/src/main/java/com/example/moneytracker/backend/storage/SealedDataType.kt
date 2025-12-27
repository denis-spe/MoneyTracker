package com.example.moneytracker.backend.storage

sealed class SealedDataType {
    data class Earnings(val amount: Double) : SealedDataType()
    data class Expense(val amount: Double) : SealedDataType()
    data class Debt(val amount: Double) : SealedDataType()
    data class Lent(val amount: Double) : SealedDataType()
    data class Savings(val amount: Double) : SealedDataType()
}