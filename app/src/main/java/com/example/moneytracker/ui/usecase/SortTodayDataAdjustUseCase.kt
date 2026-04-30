package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataAdjust
import com.example.moneytracker.backend.storage.Finance
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import javax.inject.Inject

class SortTodayDataAdjustUseCase @Inject constructor() {

    operator fun invoke(
        financeList: List<Finance>,
        timeSorting: SortType,
        categorySorting: String?,
        paymentSorting: PaymentMethod?,
        alphabeticalOrder: SortType,
        amountSorting: SortType,
        take: Int? = null
    ): List<DataAdjust> {

        val comparator = buildComparator(
            timeSorting,
            alphabeticalOrder,
            amountSorting
        )

        return coupleDatasetsWithAdjustments(financeList)
            .asSequence() // 🚀 lazy evaluation
            .filter { it.isForToday() }
            .filter { it.matchesCategory(categorySorting) }
            .filter { it.matchesPayment(paymentSorting) }
            .let { seq ->
                if (comparator != null) seq.sortedWith(comparator) else seq
            }
            .let { seq ->
                if (take != null) seq.take(take) else seq
            }
            .toList()
    }

    // 🔥 Single comparator instead of multiple sorts
    private fun buildComparator(
        timeSorting: SortType,
        alphabeticalOrder: SortType,
        amountSorting: SortType
    ): Comparator<DataAdjust>? {

        val comparators = mutableListOf<Comparator<DataAdjust>>()

        if (timeSorting != SortType.Initial) {
            comparators += compareBy<DataAdjust> { it.time() }
                .applySort(timeSorting)
        }

        if (alphabeticalOrder != SortType.Initial) {
            comparators += compareBy<DataAdjust> { it.label() }
                .applySort(alphabeticalOrder)
        }

        if (amountSorting != SortType.Initial) {
            comparators += compareBy<DataAdjust> { it.amount() }
                .applySort(amountSorting)
        }

        return comparators.reduceOrNull { acc, comp -> acc.then(comp) }
    }

    // 🔥 Extensions = no repeated `when`
    private fun DataAdjust.isForToday(): Boolean = when (this) {
        is DataAdjust.Data -> finance.isForToday
        is DataAdjust.Adjust -> adjustment.isForToday
    }

    private fun DataAdjust.matchesCategory(category: String?): Boolean {
        if (category.isNullOrBlank() || category == "Initial") return true
        return when (this) {
            is DataAdjust.Data -> finance.categoryText == category
            is DataAdjust.Adjust -> adjustment.adjustmentType.text == category
        }
    }

    private fun DataAdjust.matchesPayment(payment: PaymentMethod?): Boolean {
        if (payment == null) return true
        return when (this) {
            is DataAdjust.Data -> finance.paymentMethod == payment
            is DataAdjust.Adjust -> adjustment.paymentMethod == payment
        }
    }

    private fun DataAdjust.time(): Long = when (this) {
        is DataAdjust.Data -> finance.createdAt.toDate().time
        is DataAdjust.Adjust -> adjustment.dateTime.toDate().time
    }

    private fun DataAdjust.label(): String = when (this) {
        is DataAdjust.Data -> finance.label
        is DataAdjust.Adjust -> adjustment.label
    }

    private fun DataAdjust.amount(): Double = when (this) {
        is DataAdjust.Data -> finance.amount
        is DataAdjust.Adjust -> adjustment.amount
    }

    // 🔥 Reusable sort direction
    private fun <T> Comparator<T>.applySort(sortType: SortType): Comparator<T> =
        if (sortType == SortType.Descending) this.reversed() else this
}