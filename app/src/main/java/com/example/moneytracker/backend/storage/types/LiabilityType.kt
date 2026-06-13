package com.example.moneytracker.backend.storage.types

import com.example.moneytracker.R

enum class LiabilityType(
    override val text: String,
    override val color: Int,
    override val outlinedIcon: Int,
    override val filledIcon: Int,
    override val tagIconRes: Int,
    override val typeDescription: String,
) : FinanceCategory {
    DEBT(
        text = "Debt",
        color = R.color.Debt,
        outlinedIcon = R.drawable.outline_debt,
        filledIcon = R.drawable.filled_debt,
        tagIconRes = R.drawable.debt,
        typeDescription = "Manage your debts"
    ),
    LOAN(
        text = "Lent",
        color = R.color.Lent,
        outlinedIcon = R.drawable.outline_lent,
        filledIcon = R.drawable.filled_lent,
        tagIconRes = R.drawable.lent,
        typeDescription = "Recover your funds"
    )
}