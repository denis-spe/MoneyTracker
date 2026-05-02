package com.example.moneytracker.backend.storage.types

import com.example.moneytracker.R

enum class TransactionType(
    override val text: String,
    override val color: Int,
    override val outlinedIcon: Int,
    override val filledIcon: Int,
    override val tagIconRes: Int,
    override val typeDescription: String,
) : FinanceCategory {
    EARNINGS(
        text = "Earnings",
        color = R.color.Earnings,
        outlinedIcon = R.drawable.outline_earnings,
        filledIcon = R.drawable.filled_earnings,
        tagIconRes = R.drawable.earnings,
        typeDescription = "Top up your earnings"
    ),
    SAVINGS(
        text = "Savings",
        color = R.color.Savings,
        outlinedIcon = R.drawable.outline_savings,
        filledIcon = R.drawable.filled_savings,
        tagIconRes = R.drawable.savings,
        typeDescription = "Put a time for your savings"
    ),
    EXPENSES(
        text = "Expense",
        color = R.color.Expense,
        outlinedIcon = R.drawable.outline_expenditure,
        filledIcon = R.drawable.filled_expenditure,
        tagIconRes = R.drawable.expense,
        typeDescription = "Add your expenses"
    )
}