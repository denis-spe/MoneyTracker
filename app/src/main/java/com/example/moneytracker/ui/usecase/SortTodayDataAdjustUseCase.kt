package com.example.moneytracker.ui.usecase

import com.example.moneytracker.backend.storage.DataSettlement
import com.example.moneytracker.backend.storage.FinanceEntity
import com.example.moneytracker.backend.storage.PaymentMethod
import com.example.moneytracker.helper.isForToday
import com.example.moneytracker.ui.homeScreen.todayScreen.itemListArea.SortType
import javax.inject.Inject

class SortTodayDataSettlementUseCase @Inject constructor() {

    operator fun invoke(
        financeEntityList: List<FinanceEntity>,
        timeSorting: SortType,
        categorySorting: String?,
        paymentSorting: PaymentMethod?,
        alphabeticalOrder: SortType,
        amountSorting: SortType,
        take: Int? = null
    ): List<DataSettlement> {

        val comparator = buildComparator(
            timeSorting,
            alphabeticalOrder,
            amountSorting
        )

        return coupleDatasetsWithSettlements(financeEntityList)
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
    ): Comparator<DataSettlement>? {

        val comparators = mutableListOf<Comparator<DataSettlement>>()

        if (timeSorting != SortType.Initial) {
            comparators += compareBy<DataSettlement> { it.time() }
                .applySort(timeSorting)
        }

        if (alphabeticalOrder != SortType.Initial) {
            comparators += compareBy<DataSettlement> { it.label() }
                .applySort(alphabeticalOrder)
        }

        if (amountSorting != SortType.Initial) {
            comparators += compareBy<DataSettlement> { it.amount() }
                .applySort(amountSorting)
        }

        return comparators.reduceOrNull { acc, comp -> acc.then(comp) }
    }

    // 🔥 Extensions = no repeated `when`
    private fun DataSettlement.isForToday(): Boolean = when (this) {
        is DataSettlement.SettlementData -> financeEntity.isForToday
        is DataSettlement.SettlementAdjust -> settlement.isForToday
    }

    private fun DataSettlement.matchesCategory(category: String?): Boolean {
        if (category.isNullOrBlank() || category == "Initial") return true
        return when (this) {
            is DataSettlement.SettlementData -> financeEntity.categoryText == category
            is DataSettlement.SettlementAdjust -> settlement.settlementType.text == category
        }
    }

    private fun DataSettlement.matchesPayment(payment: PaymentMethod?): Boolean {
        if (payment == null) return true
        return when (this) {
            is DataSettlement.SettlementData -> financeEntity.paymentMethod == payment
            is DataSettlement.SettlementAdjust -> settlement.paymentMethod == payment
        }
    }

    private fun DataSettlement.time(): Long = when (this) {
        is DataSettlement.SettlementData -> financeEntity.createdAt.toDate().time
        is DataSettlement.SettlementAdjust -> settlement.dateTime.toDate().time
    }

    private fun DataSettlement.label(): String = when (this) {
        is DataSettlement.SettlementData -> financeEntity.label
        is DataSettlement.SettlementAdjust -> settlement.label
    }

    private fun DataSettlement.amount(): Double = when (this) {
        is DataSettlement.SettlementData -> financeEntity.amount
        is DataSettlement.SettlementAdjust -> settlement.amount
    }

    // 🔥 Reusable sort direction
    private fun <T> Comparator<T>.applySort(sortType: SortType): Comparator<T> =
        if (sortType == SortType.Descending) this.reversed() else this
}