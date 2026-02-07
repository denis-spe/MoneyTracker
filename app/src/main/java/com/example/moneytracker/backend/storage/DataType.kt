package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class DataType(
    val text: String,
    val color: Int,
    val outlinedIcon: Int,
    val filledIcon: Int,
    val typeDescription: String,
) {
    EARNINGS(
        text = "Earnings",
        color = R.color.Earnings,
        outlinedIcon = R.drawable.outline_earnings,
        filledIcon = R.drawable.filled_earnings,
        typeDescription = "Top up your earnings"
    ),
    EXPENSE(
        text = "Expense",
        color = R.color.Expense,
        outlinedIcon = R.drawable.outline_expenditure,
        filledIcon = R.drawable.filled_expenditure,
        typeDescription = "Add your expenses"
    ),
    LENT(
        text = "Lent",
        color = R.color.Lent,
        outlinedIcon = R.drawable.outline_lent,
        filledIcon = R.drawable.filled_lent,
        typeDescription = "Post your amount which you lent"
    ),
    DEBT(
        text = "Debt",
        color = R.color.Debt,
        outlinedIcon = R.drawable.outline_debt,
        filledIcon = R.drawable.filled_debt,
        typeDescription = "Set your debts which you are owed"
    ),
    SAVINGS(
        text = "Savings",
        color = R.color.Savings,
        outlinedIcon = R.drawable.outline_savings,
        filledIcon = R.drawable.filled_savings,
        typeDescription = "Put a time for your savings"
    ),

    GOAL(
        text = "Goal",
        color = R.color.Goal,
        outlinedIcon = R.drawable.outlined_goal,
        filledIcon = R.drawable.filled_goal,
        typeDescription = "Set your goal to achieve"
    ),
}