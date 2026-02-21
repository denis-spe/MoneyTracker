package com.example.moneytracker.backend.storage

import androidx.annotation.Keep
import com.example.moneytracker.R

@Keep
enum class Status(val text: String, val color: Int, val icon: Int) {
    PENDING("Pending", R.color.teal_200, R.drawable.pending),
    OVERDUE("Overdue", R.color.error_color, R.drawable.failed),

    SUCCESS("Success", R.color.success_complete, R.drawable.success),
    COMPLETED("Completed", R.color.success_complete, R.drawable.done),
    INITIAL("Initial", R.color.white, R.drawable.initial),
    PAYBACK("Payback", R.color.RepayDebt, R.drawable.repayment),
    REFUNDED("Refunded", R.color.RepayLoan, R.drawable.repayment),

}
