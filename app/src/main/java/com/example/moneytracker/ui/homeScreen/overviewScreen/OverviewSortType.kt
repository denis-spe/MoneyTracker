package com.example.moneytracker.ui.homeScreen.overviewScreen

enum class TransactionSort(val label: String) {
    DATE("Date"),
    AMOUNT("Amount"),
    NAME("Name");

    fun getSortLabel(isAscending: Boolean): String = when (this) {
        DATE -> if (isAscending) "Oldest First" else "Newest First"
        AMOUNT -> if (isAscending) "Lowest First" else "Highest First"
        NAME -> if (isAscending) "A-Z" else "Z-A"
    }
}

enum class GoalSort(val label: String) {
    DEADLINE("Deadline"),
    AMOUNT("Amount"),
    ACTIVE("Active");

    fun getSortLabel(isAscending: Boolean): String = when (this) {
        DEADLINE -> if (isAscending) "Earliest First" else "Latest First"
        AMOUNT -> if (isAscending) "Lowest First" else "Highest First"
        ACTIVE -> if (isAscending) "Active First" else "Inactive First"
    }
}

enum class LiabilitySort(val label: String) {
    DATE("Date"),
    AMOUNT("Amount"),
    PAID("Paid"),
    UNPAID("Unpaid");

    fun getSortLabel(isAscending: Boolean): String = when (this) {
        DATE -> if (isAscending) "Oldest First" else "Newest First"
        AMOUNT -> if (isAscending) "Lowest First" else "Highest First"
        PAID -> if (isAscending) "Paid First" else "Unpaid First"
        UNPAID -> if (isAscending) "Unpaid First" else "Paid First"
    }
}
